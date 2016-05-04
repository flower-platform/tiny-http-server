package org.flowerplatform.launcher;

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
	
	public void launch(String param) throws ClassNotFoundException {
		// TODO check if newBin exist. if true, delete prevBin, curBin = prevBin, newBin = curBin
		
		// TODO load all jar/zips from curBin. 
		
		// TODO init launcherClass and start
	}
	
}
