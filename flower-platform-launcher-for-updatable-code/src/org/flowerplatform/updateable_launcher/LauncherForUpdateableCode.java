package org.flowerplatform.updateable_launcher;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Silviu Negoita
 */
public class LauncherForUpdateableCode {
	
	private File previousBinDir;

	private File currentBinDir;
	
	private File newBinDir;

	public LauncherForUpdateableCode(String previousBinDir, String currentBinDir, String newBinDir) {
		this.previousBinDir = new File(previousBinDir);
		this.currentBinDir = new File(currentBinDir);
		this.newBinDir = new File(newBinDir);
	}

	public URLClassLoader load() {
		if (newBinDir.exists()) {
			try {
				delete(previousBinDir.toPath());
			} catch (Exception e) {
				throw new RuntimeException("Can't delete directory: " + previousBinDir.getAbsolutePath());
			}

			currentBinDir.renameTo(previousBinDir);
			newBinDir.renameTo(currentBinDir);
		}

		try {
			File[] archives = currentBinDir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return (name.toLowerCase().endsWith(".jar") || name.toLowerCase().endsWith(".zip"));
				}
			});

			URL[] urlList = new URL[archives.length];
			for (int j = 0; j < urlList.length; j++) {
				urlList[j] = archives[j].toURI().toURL();
			}
			URLClassLoader loader = new URLClassLoader(urlList, this.getClass().getClassLoader());
			return loader;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void delete(final Path path) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			
			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(final Path file, final IOException e) {
				return handleException(e);
			}

			private FileVisitResult handleException(final IOException e) {
				e.printStackTrace(); // replace with more robust error handling
				return FileVisitResult.TERMINATE;
			}

			@Override
			public FileVisitResult postVisitDirectory(final Path dir, final IOException e) throws IOException {
				if (e != null) {
					return handleException(e);
				}
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		
		});
	};

}
