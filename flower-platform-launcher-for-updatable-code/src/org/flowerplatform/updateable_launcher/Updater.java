package org.flowerplatform.updateable_launcher;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

/**
 * @author Claudiu Matei
 */
public class Updater {
	
	public static void checkAndDownloadUpdate(String currentVersion, String latestVersionUrl, String downloadUrl, File updateLocation) {
		// check for newer version
		String latestVersion = null; 
		try (Scanner scanner = new Scanner(new URL(latestVersionUrl).openStream())) {
			latestVersion = scanner.nextLine();
			if (!isOlder(currentVersion, latestVersion)) {
				return;
			}
		} catch (IOException e) {
			throw new RuntimeException("Error getting latest version from: " + latestVersionUrl, e);
		} 

		// download new version
		updateLocation.mkdirs();
		try {
			Downloader.downloadAndUnzip(downloadUrl, updateLocation);
		} finally {
			updateLocation.delete();
		}
	}

	public static boolean isOlder(String version1, String version2) {
		String[] splitVersion1 = version1.split("\\.");
		String[] splitVersion2 = version2.split("\\.");
		for (int i = 0 ; i < Math.min(splitVersion1.length, splitVersion2.length); i++) {
			if (Integer.parseInt(splitVersion1[i]) < Integer.parseInt(splitVersion2[i])) {
				return true;
			}
		}
		return splitVersion1.length < splitVersion2.length;
	}

	
}
