package me.wilsonhu.appipswitch.updater;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class CheckForUpdate implements Runnable{

	private Thread thread;
	private AutoUpdater gui;
	public CheckForUpdate(AutoUpdater main) {
		this.gui = main;
		System.out.println("Created Thread");
	}
	
	@Override
	public void run() {
		 String line;
	    	try {
	            URL url = new URL("https://raw.githubusercontent.com/FlashyReese/AppIPSwitch/master/version");
	            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	            while ((line = in.readLine()) != null) {
	            	double newestVersion = Double.parseDouble(line);
	            	FileReader fr = new FileReader("version");
	            	String cVer = "";
	            	int i; 
	                while ((i=fr.read()) != -1) cVer = cVer + (char) i;
	                double currentVersion = Double.parseDouble(cVer);
	                fr.close();
	                if(currentVersion<newestVersion) {
	                	gui.log("Downloading Update","Retrieving latest update...");
	                	gui.getUpdateURL();
	                	gui.url = gui.getUpdateURL();
	                	gui.startDownload(gui.url);
	                }else if(currentVersion==newestVersion) {
	                	gui.log("Up to date!", "You are currently running the latest version v" + newestVersion);
	                	//Close this and open the other app
	                }else if(currentVersion>newestVersion) {
	                	gui.log("I see you working hard!", "You are a developer? Haha good for you. v" + currentVersion);
	                	//Close this and open the other app
	                }
	                
	            }
	            in.close();
	             
	        }
	        catch (MalformedURLException e) {
	            System.out.println("Malformed URL: " + e.getMessage());
	            gui.log("Error", "Couldn't retrieve latest update version");
	        }
	        catch (IOException e) {
	            System.out.println("I/O Error: " + e.getMessage());
	            gui.log("Error", "Couldn't retrieve latest update version");
	        }
	}
	
	public void start () {
		System.out.println("Checking for Updates?");
		if (thread == null) {
			thread = new Thread (this, "Update Checker");
			thread.start ();
		}
	}
	
	public Thread getThread() {
		return thread;
	}
}
