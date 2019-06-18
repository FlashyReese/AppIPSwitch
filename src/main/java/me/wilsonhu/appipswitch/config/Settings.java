package me.wilsonhu.appipswitch.config;

public class Settings {
	private String forceBindDir;
	private String forceBindVersionFile;
	
	public Settings() {
		this.setForceBindDir(System.getProperty("user.dir"));
		this.setForceBindVersionFile("versionfb");
	}
	
	public String getForceBindVersionFile() {
		return forceBindVersionFile;
	}
	public void setForceBindVersionFile(String forceBindVersionFile) {
		this.forceBindVersionFile = forceBindVersionFile;
	}
	public String getForceBindDir() {
		return forceBindDir;
	}
	public void setForceBindDir(String forceBindDir) {
		this.forceBindDir = forceBindDir;
	}
}
