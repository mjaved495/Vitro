/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.startup;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Accumulates a list of messages from the StartupManager, and from the context
 * listeners that the run during startup.
 */
public class StartupStatus {
	private static final Log log = LogFactory.getLog(StartupStatus.class);

	private static final String ATTRIBUTE_NAME = "STARTUP_STATUS";

	// ----------------------------------------------------------------------
	// static methods
	// ----------------------------------------------------------------------

	public static StartupStatus getBean(ServletContext ctx) {
		StartupStatus ss;

		Object o = ctx.getAttribute(ATTRIBUTE_NAME);
		if (o instanceof StartupStatus) {
			ss = (StartupStatus) o;
		} else {
			ss = new StartupStatus();
			ctx.setAttribute(ATTRIBUTE_NAME, ss);
		}

		return ss;
	}

	// ----------------------------------------------------------------------
	// methods to set status
	// ----------------------------------------------------------------------

	private List<StatusItem> itemList = new ArrayList<StatusItem>();

	public void info(ServletContextListener listener, String message) {
		addItem(StatusItem.Level.INFO, listener, message, null);
	}

	public void info(ServletContextListener listener, String message,
			Throwable cause) {
		addItem(StatusItem.Level.INFO, listener, message, cause);
	}

	public void warning(ServletContextListener listener, String message) {
		addItem(StatusItem.Level.WARNING, listener, message, null);
	}

	public void warning(ServletContextListener listener, String message,
			Throwable cause) {
		addItem(StatusItem.Level.WARNING, listener, message, cause);
	}

	public void fatal(ServletContextListener listener, String message) {
		addItem(StatusItem.Level.FATAL, listener, message, null);
	}

	public void fatal(ServletContextListener listener, String message,
			Throwable cause) {
		addItem(StatusItem.Level.FATAL, listener, message, cause);
	}

	/** Say that a previous fatal error prevented this listener from running. */
	public void listenerNotExecuted(ServletContextListener listener) {
		addItem(StatusItem.Level.NOT_EXECUTED,
				listener,
				"Not executed - startup was aborted by a previous fatal error.",
				null);
	}

	/** Create a simple item for this listener if no other exists. */
	public void listenerExecuted(ServletContextListener listener) {
		for (StatusItem item : itemList) {
			if (item.getSourceName().equals(listener.getClass().getName())) {
				return;
			}
		}
		addItem(StatusItem.Level.INFO, listener, "Ran successfully.", null);
	}

	private void addItem(StatusItem.Level level, ServletContextListener source,
			String message, Throwable cause) {
		itemList.add(new StatusItem(level, source, message, cause));
	}

	// ----------------------------------------------------------------------
	// methods to query status
	// ----------------------------------------------------------------------

	public boolean allClear() {
		return getErrorItems().isEmpty() && getWarningItems().isEmpty();
	}

	public boolean isStartupAborted() {
		return !getErrorItems().isEmpty();
	}

	public List<StatusItem> getStatusItems() {
		return Collections.unmodifiableList(itemList);
	}

	public List<StatusItem> getErrorItems() {
		List<StatusItem> list = new ArrayList<StartupStatus.StatusItem>();
		for (StatusItem item : itemList) {
			if (item.level == StatusItem.Level.FATAL) {
				list.add(item);
			}
		}
		return list;
	}

	public List<StatusItem> getWarningItems() {
		List<StatusItem> list = new ArrayList<StartupStatus.StatusItem>();
		for (StatusItem item : itemList) {
			if (item.level == StatusItem.Level.WARNING) {
				list.add(item);
			}
		}
		return list;
	}

	// ----------------------------------------------------------------------
	// helper classes
	// ----------------------------------------------------------------------

	public static class StatusItem {
		public enum Level {
			INFO, WARNING, FATAL, NOT_EXECUTED
		}

		private final Level level;
		private final String sourceName;
		private final String shortSourceName;
		private final String message;
		private final String cause;

		private boolean unexpectedArguments;

		public StatusItem(Level level, ServletContextListener source,
				String message, Throwable cause) {
			this.level = figureLevel(level);
			this.sourceName = figureSourceName(source);
			this.shortSourceName = figureShortSourceName(source);
			this.message = message;
			this.cause = figureCauseString(cause);

			if (unexpectedArguments) {
				log.error("Unexpected arguments to "
						+ StatusItem.class.getName() + ": level=" + level
						+ ", source=" + source + ", message=" + message
						+ ", cause=" + cause);
			}
		}

		/** Level should never be null: we have a problem. */
		private Level figureLevel(Level newLevel) {
			if (newLevel == null) {
				unexpectedArguments = true;
				return Level.FATAL;
			} else {
				return newLevel;
			}
		}

		private String figureSourceName(ServletContextListener source) {
			if (source == null) {
				unexpectedArguments = true;
				return "UNKNOWN SOURCE";
			} else {
				return source.getClass().getName();
			}
		}

		/**
		 * Don't just use getSimpleName(): on an inner class we'd like to see
		 * the parent also.
		 */
		private String figureShortSourceName(ServletContextListener source) {
			if (source == null) {
				unexpectedArguments = true;
				return "UNKNOWN_SOURCE";
			} else {
				String sourceClassName = source.getClass().getName();
				int lastPeriodHere = sourceClassName.lastIndexOf('.');
				return sourceClassName.substring(lastPeriodHere + 1);
			}
		}

		/** Cause may be null - that's not unexpected. */
		private String figureCauseString(Throwable newCause) {
			if (newCause == null) {
				return "";
			} else {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				newCause.printStackTrace(pw);
				return sw.toString();
			}
		}

		public Level getLevel() {
			return level;
		}

		public String getSourceName() {
			return sourceName;
		}

		public String getShortSourceName() {
			return shortSourceName;
		}

		public String getMessage() {
			return message;
		}

		public String getCause() {
			return cause;
		}

	}

}
