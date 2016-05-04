package org.flowerplatform.launcher;

import java.io.File;

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
	
	public void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
	
	public void launch(LauncherDto param) throws ClassNotFoundException {
		//TODO check if newBin exist. if true, delete prevBin, curBin = prevBin, newBin = curBin
		File newBin = new File(currentBinDir);
		if (newBin.exists()) {
			File prevBin = new File(previousBinDir);
			deleteFolder(prevBin);
			previousBinDir = currentBinDir;
			currentBinDir = newBinDir;
		}
		
		
		try {
			//TODO load all jar/zips from curBin. 
			
	        //TODO init launcherClass and start	        
	      
	        
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	
	
}
