package org.flowerplatform.updatable_code.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Utilities for checking and downloading updates
 * 
 * @author Silviu Negoita
 * @author Claudiu Matei
 */
public class UpdatableCodeUtils {
	
	final static String ARCHIVE_DOWNLOAD_NAME = "/download.zip";

	
	public static Properties checkAndDownloadUpdate(String currentVersion, String updateInfoUrl, File updateLocation) {
		Properties updateInfo = getUpdateInfo(updateInfoUrl);
		
		// check update version
		if (!isUpdateNewer(currentVersion, updateInfo.getProperty("version"))) {
			return null;
		}

		// download new version
		UpdatableCodeUtils.downloadAndUnzip(updateInfo.getProperty("url"), updateLocation);
		return updateInfo;
	}

	public static Properties getUpdateInfo(String updateInfoUrl) {
		Properties updateInfo = new Properties();
		try (InputStream in = new URL(updateInfoUrl).openStream()) {
			updateInfo.load(in);
		} catch (IOException e) {
			throw new RuntimeException("Error getting update information from: " + updateInfoUrl, e);
		} 
		return updateInfo;
	}

	public static boolean isUpdateNewer(String currentVersion, String updateVersion) {
		String[] splitCurrentVersion = currentVersion.split("\\.");
		String[] splitUpdateVersion = updateVersion.split("\\.");
		for (int i = 0 ; i < Math.min(splitCurrentVersion.length, splitUpdateVersion.length); i++) {
			int current = Integer.parseInt(splitCurrentVersion[i]);
			int update = Integer.parseInt(splitUpdateVersion[i]);
			if (current == update) {
				continue;
			}
			return update > current;
		}
		return  splitUpdateVersion.length > splitCurrentVersion.length;
	}
	
	public static void downloadAndUnzip(String url, File location) {
		InputStream in = null;
		FileOutputStream out = null;
		File archive = null;
		try {
			new File(location.getCanonicalPath()).mkdirs();
			String archivePath = location.getCanonicalPath() + ARCHIVE_DOWNLOAD_NAME;
			archive = new File(archivePath);
			URL website = new URL((String) url);
			in = website.openStream();
			out = new FileOutputStream(archivePath);
			final byte[] data = new byte[1024]; // 1 KB data
			int count;
			while ((count = in.read(data)) != -1) {
				out.write(data, 0, count);
			}
			unzipArchive(archive, location);
		} catch (Exception e) {
			location.delete();
			throw new RuntimeException(e);
		} finally {
			try { in.close(); } catch (Exception e) { e.printStackTrace(); }
			try { out.close(); } catch (Exception e) { e.printStackTrace(); }
			if (archive != null) {
				archive.delete();
			}
		}
	}

	
	@SuppressWarnings("rawtypes")
	private static void unzipArchive(File archive, File outputDir) throws IOException {
		ZipFile zipfile = new ZipFile(archive);
		for (Enumeration e = zipfile.entries(); e.hasMoreElements();) {
			ZipEntry entry = ((ZipEntry) e.nextElement());
			unzipEntry(zipfile, entry, outputDir);
		}
		zipfile.close();
	}
	
	private static void unzipEntry(ZipFile zipfile, ZipEntry entry, File outputDir) throws IOException {
		File outputFile = new File(outputDir, entry.getName());

		if (entry.isDirectory()) {
			outputFile.mkdirs();
			return;
		}

		if (!outputFile.getParentFile().exists()) {
			outputFile.getParentFile().mkdirs();
		}

		BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

		try {
			byte[] buf = new byte[1024 * 1024];
			int k;
			while ((k = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, k);
				outputStream.flush();
			}
		} finally {
			try { outputStream.close(); } catch (Exception e) { e.printStackTrace(); }
			try { inputStream.close(); } catch (Exception e) { e.printStackTrace(); }
		}
	}

}
