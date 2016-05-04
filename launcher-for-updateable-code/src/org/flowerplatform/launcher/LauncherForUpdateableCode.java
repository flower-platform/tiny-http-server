package org.flowerplatform.launcher;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.io.FileUtils;
/**
 * @author Silviu Negoita
 */
public class LauncherForUpdateableCode {
	String previousBinDir;
	String currentBinDir;
	String newBinDir;
	String className;
	
	public LauncherForUpdateableCode(String previousBinDir, String currentBinDir, String newBinDir, String className) {
		this.previousBinDir = previousBinDir;
		this.currentBinDir = currentBinDir;
		this.newBinDir = newBinDir;
		this.className = className;
	}

	public void launch(LauncherDto param) throws ClassNotFoundException, IOException {
		File newBin = new File(currentBinDir);
		if (newBin.exists()) {
			FileUtils.deleteDirectory(new File(previousBinDir));
			previousBinDir = currentBinDir;
			currentBinDir = newBinDir;
		}
		
		File currentDirectory = new File(currentBinDir);
		try {
			File[] archives = currentDirectory.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return (name.toLowerCase().endsWith(".jar") ||
							name.toLowerCase().endsWith(".zip"));
				}
			});
			
			URL[] urlList = new URL[archives.length];
	        for (int j = 0; j < urlList.length; j++) {
	        	urlList[j] = archives[j].toURI().toURL();
	        }
	        URLClassLoader loader = new URLClassLoader(urlList);
	        
	        Class<?> toLaunchClass = Class.forName(className, true, loader);
	        RunnableWithParam<Void, LauncherDto> toLaunchRunnable = (RunnableWithParam<Void, LauncherDto>) toLaunchClass.newInstance();
	        toLaunchRunnable.run(param);
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}
}
