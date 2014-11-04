package com.qmusic.uitls;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CommandConsole {
	private Boolean can_su;

	public SH sh;
	public SH su;

	public CommandConsole() {
		sh = new SH("sh");
		su = new SH("su");
	}

	/**
	 * Please call this function async
	 * 
	 * @return
	 */
	public boolean canSU() {
		return canSU(false);
	}

	/**
	 * Please call this function async
	 * 
	 * @return
	 */
	public boolean canSU(boolean force_check) {
		if (can_su == null || force_check) {
			CommandResult r = su.runWaitFor("id");
			// StringBuilder out = new StringBuilder();
			// if (r.stdout != null)
			// out.append(r.stdout).append(" ; ");
			// if (r.stderr != null)
			// out.append(r.stderr);
			can_su = r.success();
		}
		return can_su;
	}

	/**
	 * Please call this function async
	 * 
	 * @return
	 */
	public SH suOrSH() {
		return canSU() ? su : sh;
	}

	public SH sh() {
		return sh;
	}

	public static class CommandResult {
		public final String stdout;
		public final String stderr;
		public final Integer exitValue;

		CommandResult(Integer exitValueIn, String stdoutIn, String stderrIn) {
			exitValue = exitValueIn;
			stdout = stdoutIn;
			stderr = stderrIn;
		}

		CommandResult(Integer exit_value_in) {
			this(exit_value_in, null, null);
		}

		public boolean success() {
			return exitValue != null && exitValue == 0;
		}
	}

	public static class SH {
		private String SHELL = "sh";

		public SH(String SHELL_in) {
			SHELL = SHELL_in;
		}

		public Process run(String s) {
			Process process = null;
			try {
				process = Runtime.getRuntime().exec(SHELL);
				DataOutputStream toProcess = new DataOutputStream(process.getOutputStream());
				toProcess.writeBytes("exec " + s + "\n");
				toProcess.flush();
			} catch (Exception e) {
				process = null;
			}
			return process;
		}

		public void run(String s, File logFile) {
			Process process = run(s);
			if (process != null) {
				BufferedOutputStream outputStream = null;
				try {
					outputStream = new BufferedOutputStream(new FileOutputStream(logFile, true));
					byte[] buffer = new byte[512];
					int byteRead = 0;
					InputStream inputStream = process.getInputStream();
					while ((byteRead = inputStream.read(buffer)) > 0) {
						outputStream.write(buffer, 0, byteRead);
						outputStream.flush();
					}
					inputStream = process.getErrorStream();
					while ((byteRead = inputStream.read(buffer)) > 0) {
						outputStream.write(buffer, 0, byteRead);
						outputStream.flush();
					}
					int exitCode = process.waitFor();
					outputStream.write(String.format("Exit Code:%d\n", exitCode).getBytes());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (outputStream != null) {
							outputStream.flush();
							outputStream.close();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}

		public CommandResult runWaitFor(String s) {
			Process process = run(s);
			Integer exit_value = null;
			String stdout = null;
			String stderr = null;
			if (process != null) {
				try {
					exit_value = process.waitFor();
					stdout = getStreamLines(process.getInputStream());
					stderr = getStreamLines(process.getErrorStream());

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
			return new CommandResult(exit_value, stdout, stderr);
		}

		private String getStreamLines(InputStream is) {
			DataInputStream dis = new DataInputStream(is);
			StringBuilder sb = new StringBuilder();
			try {
				byte[] bytes = new byte[1024];
				int result = 0;
				while ((result = dis.read(bytes)) > 0) {
					String line = new String(bytes, 0, result);
					sb.append(line);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				try {
					dis.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			return sb.toString();
		}
	}
}