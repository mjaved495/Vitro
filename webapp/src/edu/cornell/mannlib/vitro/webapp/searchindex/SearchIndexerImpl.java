/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.searchindex;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelNames.DISPLAY;
import static edu.cornell.mannlib.vitro.webapp.modules.searchIndexer.SearchIndexer.Event.Type.SHUTDOWN_COMPLETE;
import static edu.cornell.mannlib.vitro.webapp.modules.searchIndexer.SearchIndexer.Event.Type.SHUTDOWN_REQUESTED;
import static edu.cornell.mannlib.vitro.webapp.modules.searchIndexer.SearchIndexer.Event.Type.STARTUP;
import static java.util.concurrent.TimeUnit.MINUTES;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.WebappDaoFactoryFiltering;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.VitroFilterUtils;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.VitroFilters;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.modules.Application;
import edu.cornell.mannlib.vitro.webapp.modules.ComponentStartupStatus;
import edu.cornell.mannlib.vitro.webapp.modules.searchIndexer.SearchIndexer;
import edu.cornell.mannlib.vitro.webapp.modules.searchIndexer.SearchIndexerStatus;
import edu.cornell.mannlib.vitro.webapp.modules.searchIndexer.SearchIndexerStatus.State;
import edu.cornell.mannlib.vitro.webapp.searchindex.documentBuilding.DocumentModifier;
import edu.cornell.mannlib.vitro.webapp.searchindex.exclusions.SearchIndexExcluder;
import edu.cornell.mannlib.vitro.webapp.searchindex.indexing.IndexingUriFinder;
import edu.cornell.mannlib.vitro.webapp.searchindex.tasks.RebuildIndexTask;
import edu.cornell.mannlib.vitro.webapp.searchindex.tasks.UpdateUrisTask;
import edu.cornell.mannlib.vitro.webapp.utils.configuration.ConfigurationBeanLoader;
import edu.cornell.mannlib.vitro.webapp.utils.configuration.ConfigurationBeanLoaderException;
import edu.cornell.mannlib.vitro.webapp.utils.threads.VitroBackgroundThread;

/**
 * An implementation of the SearchIndexer interface.
 * 
 * This implementation uses a single-threaded task queue to permit indexing to
 * run one at a time in a "background" thread. The task queue is controlled by a
 * scheduler that allows us to suspend incoming tasks (pause).
 * 
 * A thread pool is available so the tasks can create small units of work to be
 * run in parallel. Each task should block until all of its work units are
 * complete, to preserve the pattern of running one task at a time.
 */
public class SearchIndexerImpl implements SearchIndexer {
	private static final Log log = LogFactory.getLog(SearchIndexerImpl.class);

	private final ListenerList listeners = new ListenerList();
	private final TaskQueue taskQueue = new TaskQueue();
	private final Scheduler scheduler = new Scheduler(taskQueue);
	private final WorkerThreadPool pool = new WorkerThreadPool();

	private ServletContext ctx;
	private Set<SearchIndexExcluder> excluders;
	private Set<DocumentModifier> modifiers;
	private Set<IndexingUriFinder> uriFinders;
	private WebappDaoFactory wadf;

	@Override
	public void startup(Application application, ComponentStartupStatus ss) {
		try {
			this.ctx = application.getServletContext();

			loadConfiguration();

			this.wadf = getFilteredWebappDaoFactory();

			listeners.fireEvent(new Event(STARTUP, getStatus()));
			ss.info("Configured SearchIndexer: excluders=" + excluders
					+ ", modifiers=" + modifiers + ", uriFinders=" + uriFinders);
		} catch (Exception e) {
			ss.fatal("Failed to configure the SearchIndexer", e);
		}
	}

	private void loadConfiguration() throws ConfigurationBeanLoaderException {
		ConfigurationBeanLoader beanLoader = new ConfigurationBeanLoader(
				ModelAccess.on(ctx).getOntModel(DISPLAY), ctx);
		excluders = beanLoader.loadAll(SearchIndexExcluder.class);
		modifiers = beanLoader.loadAll(DocumentModifier.class);
		uriFinders = beanLoader.loadAll(IndexingUriFinder.class);
	}

	/**
	 * Use a filtered DAO factory, so only public data goes into the search
	 * index.
	 */
	private WebappDaoFactory getFilteredWebappDaoFactory() {
		WebappDaoFactory rawWadf = ModelAccess.on(ctx).getWebappDaoFactory();
		VitroFilters vf = VitroFilterUtils.getPublicFilter(ctx);
		return new WebappDaoFactoryFiltering(rawWadf, vf);
	}

	@Override
	public void scheduleUpdatesForUris(Collection<String> uris) {
		log.debug("Schedule updates for " + uris.size() + " uris.");
		scheduler.scheduleTask(new UpdateUrisTask(uris, excluders, modifiers,
				wadf.getIndividualDao(), listeners, pool));
	}

	@Override
	public void rebuildIndex() {
		scheduler.scheduleTask(new RebuildIndexTask());
	}

	@Override
	public void pause() {
		scheduler.pause();
	}

	@Override
	public void unpause() {
		scheduler.unpause();
	}

	@Override
	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	@Override
	public SearchIndexerStatus getStatus() {
		return taskQueue.getStatus();
	}

	@Override
	public synchronized void shutdown(Application application) {
		SearchIndexerStatus status = taskQueue.getStatus();
		if (status.getState() != State.SHUTDOWN) {
			listeners.fireEvent(new Event(SHUTDOWN_REQUESTED, status));

			taskQueue.shutdown();

			for (DocumentModifier dm : modifiers) {
				try {
					dm.shutdown();
				} catch (Exception e) {
					log.warn("Failed to shut down document modifier " + dm, e);
				}
			}

			listeners.fireEvent(new Event(SHUTDOWN_COMPLETE, taskQueue
					.getStatus()));
		}
	}

	// ----------------------------------------------------------------------
	// Helper classes
	// ----------------------------------------------------------------------

	/**
	 * A simple thread-safe list of event listeners. All methods are
	 * synchronized.
	 */
	public static class ListenerList {
		private final List<Listener> list;

		public ListenerList() {
			list = new ArrayList<Listener>();
		}

		public synchronized void add(Listener l) {
			list.add(l);
		}

		public synchronized void remove(Listener l) {
			list.remove(l);
		}

		public synchronized void fireEvent(Event event) {
			for (Listener l : list) {
				try {
					l.receiveSearchIndexerEvent(event);
				} catch (Exception e) {
					log.warn("Failed to deliver event '" + event
							+ "' to listener '" + l + "'", e);
				}
			}
		}
	}

	/**
	 * A scheduler either collects tasks (if paused), or passes them to the
	 * queue (if not paused). All methods are synchronized.
	 */
	private static class Scheduler {
		private final TaskQueue taskQueue;
		private final List<Task> deferredQueue;
		private volatile boolean paused;

		public Scheduler(TaskQueue taskQueue) {
			this.taskQueue = taskQueue;
			this.deferredQueue = new ArrayList<Task>();
		}

		public synchronized void scheduleTask(Task task) {
			if (paused) {
				deferredQueue.add(task);
				log.debug("added task to deferred queue: " + task);
			} else {
				taskQueue.scheduleTask(task);
				log.debug("added task to task queue: " + task);
			}
		}

		public synchronized void pause() {
			paused = true;
		}

		public synchronized void unpause() {
			paused = false;
			for (Task task : deferredQueue) {
				taskQueue.scheduleTask(task);
				log.debug("moved task from deferred queue to task queue: " + task);
			}
		}
	}

	/**
	 * A single-threaded task queue that can tell us the status of the current
	 * task.
	 * 
	 * If no current task, it can return a status of IDLE or SHUTDOWN.
	 */
	private static class TaskQueue {
		private final ExecutorService queue = Executors
				.newSingleThreadExecutor(new VitroBackgroundThread.Factory(
						"SearchIndexer_TaskQueue"));

		private AtomicReference<QueueStatus> current = new AtomicReference<>(
				new QueueStatus(SearchIndexerStatus.idle()));

		public void scheduleTask(Task task) {
			try {
				queue.execute(new TaskWrapper(task));
			} catch (RejectedExecutionException e) {
				log.warn("Search Indexer task was rejected: " + e);
			}
		}

		public SearchIndexerStatus getStatus() {
			return current.get().getStatus();
		}

		public void shutdown() {
			try {
				queue.shutdownNow();
				boolean terminated = queue.awaitTermination(1, MINUTES);
				if (!terminated) {
					log.warn("SearchIndexer task queue did not shut down "
							+ "within 1 minute.");
				}
				current.set(new QueueStatus(SearchIndexerStatus.shutdown()));
			} catch (InterruptedException e) {
				log.warn("call to 'awaitTermination' was interrupted.");
			}
		}

		/** When this wrapper is run, we will know the current task and status. */
		private class TaskWrapper implements Runnable {
			private final Task task;

			public TaskWrapper(Task task) {
				this.task = task;
			}

			@Override
			public void run() {
				current.set(new QueueStatus(task));
				log.debug("starting task: " + task);
				task.run();
				current.set(new QueueStatus(SearchIndexerStatus.idle()));
				log.debug("ended task: " + task);
			}
		}

		/** Either a specific status or a task to interrogate. */
		private class QueueStatus {
			private final Task task;
			private final SearchIndexerStatus status;

			public QueueStatus(Task task) {
				this.task = Objects.requireNonNull(task);
				this.status = null;
			}

			public QueueStatus(SearchIndexerStatus status) {
				this.task = null;
				this.status = Objects.requireNonNull(status);
			}

			public SearchIndexerStatus getStatus() {
				if (task != null) {
					return task.getStatus();
				} else {
					return status;
				}
			}
		}
	}

	public static interface Task extends Runnable {
		public SearchIndexerStatus getStatus();

		public void notifyWorkUnitCompletion(Runnable workUnit);
	}

	/**
	 * A thread pool for handling many small units of work submitted by a task.
	 * 
	 * The task is notified as each unit completes.
	 * 
	 * Only one task is active at a time, so the task can simply wait until this
	 * pool is idle to know that all of its units have completed.
	 * 
	 * When shutting down, no attempt is made to interrupt the currently
	 * executing work units, since they are assumed to be small.
	 */
	public static class WorkerThreadPool {
		private final ThreadPoolExecutor pool;

		public WorkerThreadPool() {
			this.pool = new ThreadPoolExecutor(10, 10, 10, TimeUnit.SECONDS,
					new ArrayBlockingQueue<Runnable>(50),
					new VitroBackgroundThread.Factory(
							"SearchIndexer_ThreadPool"));
		}

		public void submit(Runnable workUnit, Task task) {
			pool.execute(new WorkUnitWrapper(workUnit, task));
		}

		public void waitUntilIdle() {
			for (int i = 0; i < 60; i++) {
				if (pool.getActiveCount() == 0) {
					return;
				} else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}

		public void shutdown() {
			pool.shutdown();
			try {
				boolean terminated = pool.awaitTermination(1, MINUTES);
				if (!terminated) {
					log.warn("SearchIndexer thread pool did not shut down "
							+ "within 1 minute.");
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		private static class WorkUnitWrapper implements Runnable {
			private final Runnable workUnit;
			private final Task task;

			public WorkUnitWrapper(Runnable workUnit, Task task) {
				this.workUnit = workUnit;
				this.task = task;
			}

			@Override
			public void run() {
				try {
					workUnit.run();
				} finally {
					task.notifyWorkUnitCompletion(workUnit);
				}
			}

		}

	}
}
