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
	File previousBinDir;
	File currentBinDir;
	File newBinDir;
	String className;
	
	public LauncherForUpdateableCode(String previousBinDir, String currentBinDir, String newBinDir, String className) {
		this.previousBinDir = new File(previousBinDir);
		this.currentBinDir = new File(currentBinDir);
		this.newBinDir = new File(newBinDir);
		this.className = className;
	}

	public void launch(LauncherDto param) throws ClassNotFoundException, IOException {
		if (newBinDir.exists()) {
			FileUtils.deleteDirectory(previousBinDir);
			currentBinDir.renameTo(previousBinDir);
			newBinDir.renameTo(currentBinDir);
		}
		
		try {
			File[] archives = currentBinDir.listFiles(new FilenameFilter() {
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
	
	// this is for test
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		LauncherForUpdateableCode launcher = new LauncherForUpdateableCode("resources/prevBin/", "resources/currBin/", "resources/newBin/", "org.flowerplatform.launcher.test.RunnableHelloWorld");
		launcher.launch(new LauncherDto(null));
	}
}
