package com.qmusic.uitls;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.androidquery.util.AQUtility;

public final class BIOUtilities {
	static final String TAG = BIOUtilities.class.getSimpleName();

	public final static void copyAssertToSDcardAsync(final Context ctx, final String assertPath, final File disPath) {
		AsyncTask<Void, Void, Void> fileCopyer = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				copyAssertToSDCard(ctx, assertPath, disPath);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

			}
		};
		fileCopyer.execute();
	}

	public final static void copyAssertToSDCard(final Context ctx, final String assertPath, final File disPath) {
		try {
			String[] files = ctx.getAssets().list(assertPath);
			if (files.length == 0) {
				copyFile(ctx, assertPath, disPath, true);
			} else {
				BLog.i(TAG, "copy folder from " + assertPath + " to " + disPath);
				for (int i = 0; i < files.length; i++) {
					copyAssertToSDCard(ctx, assertPath + File.separator + files[i], disPath);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final void copyFile(Context ctx, File src, File dst) {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(src);
			os = new FileOutputStream(dst);
			byte[] b = new byte[1024];
			int len;
			while ((len = is.read(b)) != -1) {
				os.write(b, 0, len);
			}
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public static final void copyFile(Context ctx, String src, File dst, boolean createIntermediateFolders) {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = ctx.getAssets().open(src);
			String[] filePath = src.split(File.separator);
			String fileName = filePath[filePath.length - 1];
			File subDir = dst;
			if (createIntermediateFolders) {
				String subFolder = src.substring(0, src.length() - fileName.length() - 1);
				subDir = new File(dst, subFolder);
				if (!subDir.exists()) {
					boolean result = subDir.mkdirs();
					BLog.i(TAG, "make dir " + subDir.getAbsolutePath() + "; result:" + result);
				}
			}
			// EdoLog.i(TAG, "copy from " + src + " to " + subDir +
			// File.separator + fileName);
			os = new FileOutputStream(new File(subDir, fileName));
			byte[] b = new byte[1024];
			int len;
			while ((len = is.read(b)) != -1) {
				os.write(b, 0, len);
			}
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public final static void unZipFolder(InputStream zipFileStream, String outPathString) {
		ZipInputStream inZip = null;
		FileOutputStream out = null;
		try {
			inZip = new ZipInputStream(zipFileStream);
			ZipEntry zipEntry;
			String szName = "";
			while ((zipEntry = inZip.getNextEntry()) != null) {
				szName = zipEntry.getName();

				if (zipEntry.isDirectory()) {

					// get the folder name of the widget
					szName = szName.substring(0, szName.length() - 1);
					java.io.File folder = new java.io.File(outPathString + java.io.File.separator + szName);
					folder.mkdirs();

				} else {

					java.io.File file = new java.io.File(outPathString + java.io.File.separator + szName);
					file.createNewFile();
					// get the output stream of the file
					out = new FileOutputStream(file);
					int len;
					byte[] buffer = new byte[1024];
					// read (len) bytes into buffer
					while ((len = inZip.read(buffer)) != -1) {
						// write (len) byte from buffer at the position 0
						out.write(buffer, 0, len);
						out.flush();
					}
					out.close();
				}
			}// end of while

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (inZip != null) {
				try {
					inZip.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public final static String readStream(InputStream in) {
		if (in == null)
			return null;
		byte[] bytes = new byte[1024];
		StringBuffer sb = new StringBuffer();
		String str = null;
		int count;
		try {
			do {
				count = in.read(bytes, 0, 1024);
				if (count > 0) {
					str = new String(bytes, 0, count, "UTF-8");
					sb.append(str);
				}
			} while (count > 0);
		} catch (EOFException ex) {
			ex.printStackTrace();
		} catch (IOException ioe) {
			str = null;
			ioe.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sb.toString();
	}

	public final static String readTextFile(String filename) {
		String ret = null;
		Log.d(TAG, "read from file:" + filename);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		FileInputStream inputStream = null;
		try {
			File file = AQUtility.getContext().getFileStreamPath(filename);
			if (!file.exists()) {
				Log.w(TAG, "file " + filename + " doesn't exist");
				return null;
			}

			inputStream = AQUtility.getContext().openFileInput(filename);
			byte buf[] = new byte[1024];
			int len;

			while ((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			ret = outputStream.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();

				if (inputStream != null)
					inputStream.close();
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		}

		return ret;
	}

	public static final String getCpuType() {
		String cpu = "";
		BufferedReader localBufferedReader = null;
		try {
			FileReader fr = new FileReader("/proc/cpuinfo");
			localBufferedReader = new BufferedReader(fr, 1024);
			String line;
			Pattern pattern = Pattern.compile("[(arm)|(intel)|(mips)]");
			while ((line = localBufferedReader.readLine()) != null) {
				String[] secs = line.split(":");
				if (secs.length > 1) {
					String value = line.split(":")[1].toLowerCase(Locale.getDefault());
					Matcher matcher = pattern.matcher(value);
					if (matcher.find()) {
						cpu = value;
						break;
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (localBufferedReader != null) {
				try {
					localBufferedReader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return cpu;
	}

	public final static boolean writeToFile(String filename, String data) {
		Log.d(TAG, "write to file:" + filename);
		boolean ret = false;
		FileOutputStream outputStream = null;
		try {
			outputStream = AQUtility.getContext().openFileOutput(filename, Context.MODE_PRIVATE);
			outputStream.write(data.getBytes());
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
}
