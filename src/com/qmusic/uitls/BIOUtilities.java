package com.qmusic.uitls;

import java.io.BufferedReader;
import java.io.File;
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

public final class BIOUtilities {
	static final String TAG = BIOUtilities.class.getSimpleName();

	public final static void copyAssertToSDCard(final Context ctx, final String assertPath, final File disPath) {
		if (disPath == null || !disPath.isDirectory()) {
			return;
		}
		try {
			String[] files = ctx.getAssets().list(assertPath);
			if (files.length == 0) {// is file
				copyAssetFile(ctx, assertPath, disPath);
			} else {// is folder
				BLog.i(TAG, "copy folder from " + assertPath + " to " + disPath);
				String[] fileSeg = assertPath.split(File.separator);
				String newFilePath = fileSeg[fileSeg.length - 1];
				File subFolder = new File(disPath, newFilePath);
				if (!subFolder.exists()) {
					subFolder.mkdir();
				}
				for (String file : files) {
					copyAssertToSDCard(ctx, String.format("%s%s%s", assertPath, File.separator, file), subFolder);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param ctx
	 * @param srcFileName
	 * @param dst
	 */
	public static final void copyAssetFile(Context ctx, String srcFileName, File dst) {
		InputStream is = null;
		OutputStream os = null;
		try {
			if (!dst.exists()) {
				boolean result = dst.mkdirs();
				BLog.i(TAG, "make dir " + dst.getAbsolutePath() + "; result:" + result);
			}
			is = ctx.getAssets().open(srcFileName);
			String[] filePath = srcFileName.split(File.separator);
			String fileName = filePath[filePath.length - 1];
			os = new FileOutputStream(new File(dst, fileName));
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
					while ((len = inZip.read(buffer)) > 0) {
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

}
