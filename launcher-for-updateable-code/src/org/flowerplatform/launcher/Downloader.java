package org.flowerplatform.launcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

/**
 * Class used to download and unzip a archive
 * 
 * @author Silviu Negoita
 */
public class Downloader {
	final static String ARCHIVE_DOWNLOAD_NAME = "/download.zip";
	public static void downloadAndUnzip(String url, File location) {
		
		InputStream in = null;
		FileOutputStream out = null;
		String archivePath = location.getAbsolutePath() + ARCHIVE_DOWNLOAD_NAME;
		File archive = new File(archivePath);
		try {
			URL website = new URL((String) url);
			in = website.openStream();
			out = new FileOutputStream(archivePath);
			final byte[] data = new byte[1024]; //  1 KB data
	        int count;	        
			while ((count = in.read(data, 0, 1024)) != -1) {
				out.write(data, 0, count);
	        }
	        unzipArchive(archive, location);
		} catch (Exception e) {
	        throw new RuntimeException(e);
		} finally {		
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			archive.delete();
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	public static void unzipArchive(File archive, File outputDir) throws IOException {
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
			IOUtils.copy(inputStream, outputStream);
		} finally {
			outputStream.close();
			inputStream.close();
		}
	}
}
