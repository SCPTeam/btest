package it.unige.cseclab.instr;

import java.io.File;
import java.io.IOException;

public class ApkSetup {
	
	static final String WORKDIR = "./sootOutput"; 
	static final String SIGNSCRIPT = "./AutoSignApk.sh"; 	
	static final String SEP = "/"; 		

	public static void signAndInstall(String app) {
	
		ProcessBuilder pb = new ProcessBuilder(SIGNSCRIPT, SEP, app);
		pb.directory(new File(WORKDIR));
		pb.redirectErrorStream(true);
		Process P = null;
		try {
			P = pb.start();
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		
		try {
			P.waitFor();
		} catch (InterruptedException e2) {	}
		
		pb = new ProcessBuilder("adb", "install", app + "-signed.apk");
		pb.directory(new File(WORKDIR));
		pb.redirectErrorStream(true);
		try {
			P = pb.start();
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		
		try {
			P.waitFor();
		} catch (InterruptedException e2) {	}
		
		// Lists all files in folder
		File folder = new File(WORKDIR);
		File[] fList = folder.listFiles();
		// Searchs .lck
		for (int i = 0; i < fList.length; i++) {
		    String pes = fList[i].getName();
		    if (pes.endsWith(".apk")) {
		        // and deletes
		        fList[i].delete();
		    }
		}
	
	}

}