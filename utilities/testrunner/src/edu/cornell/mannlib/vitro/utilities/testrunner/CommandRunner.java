/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.utilities.testrunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * A harness that runs a system-level command.
 * </p>
 * <p>
 * No provision is made for standard input.
 * </p>
 * <p>
 * The standard output and standard error streams are asynchronously read, so
 * the sub-process will not block on full buffers. Warning: if either of these
 * streams contain more data than can fit into a String, then we will have a
 * problem.
 * </p>
 * 
 * @author jblake
 */
public class CommandRunner {

	private int returnCode;
	private String stdOut = "";
	private String stdErr = "";
	private File workingDirectory;

	/* Gets informed of output as it arrives. Never null. */
	private final Listener listener;

	private final Map<String, String> environmentAdditions = new HashMap<String, String>();

	public CommandRunner(SeleniumRunnerParameters parms) {
		this.listener = parms.getListener();
	}

	/** Set the directory that the command will run in. */
	public void setWorkingDirectory(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	/** Add (or replace) any environment variable. */
	public void setEnvironmentVariable(String key, String value) {
		this.environmentAdditions.put(key, value);
	}

	/**
	 * Run the command.
	 * 
	 * @param command
	 *            a list containing the operating system program and its
	 *            arguments. See
	 *            {@link java.lang.ProcessBuilder#ProcessBuilder(List)}.
	 */
	public void run(List<String> command) throws CommandRunnerException {
		listener.subProcessStart(command);
		try {
			ProcessBuilder builder = new ProcessBuilder(command);

			if (workingDirectory != null) {
				builder.directory(workingDirectory);
			}

			if (!environmentAdditions.isEmpty()) {
				builder.environment().putAll(this.environmentAdditions);
			}

			Process process = builder.start();
			StreamEater outputEater = new StreamEater(process.getInputStream(),
					false);
			StreamEater errorEater = new StreamEater(process.getErrorStream(),
					true);

			this.returnCode = process.waitFor();

			outputEater.join();
			this.stdOut = outputEater.getContents();

			errorEater.join();
			this.stdErr = errorEater.getContents();
		} catch (IOException e) {
			throw new CommandRunnerException(
					"Exception when handling sub-process:", e);
		} catch (InterruptedException e) {
			throw new CommandRunnerException(
					"Exception when handling sub-process:", e);
		}
		listener.subProcessStop(command);
	}

	/**
	 * Run the command and don't wait for it to complete. {@link #stdErr} and
	 * {@link #stdOut} will not be set, but output from the process may be sent
	 * to the listener at any time.
	 * 
	 * @param command
	 *            a list containing the operating system program and its
	 *            arguments. See
	 *            {@link java.lang.ProcessBuilder#ProcessBuilder(List)}.
	 */
	public void runAsBackground(List<String> command)
			throws CommandRunnerException {
		listener.subProcessStartInBackground(command);
		try {
			ProcessBuilder builder = new ProcessBuilder(command);

			if (workingDirectory != null) {
				builder.directory(workingDirectory);
			}

			if (!environmentAdditions.isEmpty()) {
				builder.environment().putAll(this.environmentAdditions);
			}

			Process process = builder.start();
			StreamEater outputEater = new StreamEater(process.getInputStream(),
					false);
			StreamEater errorEater = new StreamEater(process.getErrorStream(),
					true);

		} catch (IOException e) {
			throw new CommandRunnerException(
					"Exception when handling sub-process:", e);
		}
	}

	public int getReturnCode() {
		return returnCode;
	}

	public String getStdErr() {
		return stdErr;
	}

	public String getStdOut() {
		return stdOut;
	}

	/**
	 * A thread that reads an InputStream until it reaches end of file, then
	 * closes the stream. Designated as error stream or not, so it can tell the
	 * logger.
	 */
	private class StreamEater extends Thread {
		private final InputStream stream;
		private final boolean isError;

		private final StringWriter contents = new StringWriter();

		private final byte[] buffer = new byte[4096];

		public StreamEater(InputStream stream, boolean isError) {
			this.stream = stream;
			this.isError = isError;
			this.start();
		}

		@Override
		public void run() {
			try {
				int howMany = 0;
				while (true) {
					howMany = stream.read(buffer);
					if (howMany > 0) {
						String string = new String(buffer, 0, howMany);
						contents.write(string);

						if (isError) {
							listener.subProcessErrout(string);
						} else {
							listener.subProcessStdout(string);
						}
					} else if (howMany == 0) {
						Thread.yield();
					} else {
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public String getContents() {
			return contents.toString();
		}
	}

}
