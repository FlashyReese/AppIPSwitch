package me.wilsonhu.appipswitch.config;

public class Profile {
	private String name;
	private String executable;
	private String NID;
	private String cmdOptions;
	private boolean x64;
	private boolean useI;
	private boolean useCMDOptions;
	
	public Profile(String name, String nid, String exec, boolean i, boolean x64){
		this.name = name;
		this.NID = nid;
		this.executable = exec;
		this.useI = i;
		this.x64 = x64;
		this.cmdOptions = "";
		this.useCMDOptions = false;
	}
	    
	public Profile(String name, String nid, String exec, boolean i, boolean x64, String cmd, boolean useCMD){
		this.name = name;
		this.NID = nid;
		this.executable = exec;
		this.useI = i;
		this.x64 = x64;
		this.cmdOptions = cmd;
		this.useCMDOptions = useCMD;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getExecutable() {
		return executable;
	}
	
	public void setExecutable(String executable) {
		this.executable = executable;
	}
	
	public String getNID() {
		return NID;
	}
	
	public void setNID(String nID) {
		NID = nID;
	}
	
	public String getCmdOptions() {
		return cmdOptions;
	}
	
	public void setCmdOptions(String cmdOptions) {
		this.cmdOptions = cmdOptions;
	}
	
	public boolean isX64() {
		return x64;
	}
	
	public void setX64(boolean x64) {
		this.x64 = x64;
	}
	
	public boolean isUseI() {
		return useI;
	}
	
	public void setUseI(boolean useI) {
		this.useI = useI;
	}

	public boolean isUseCMDOptions() {
		return useCMDOptions;
	}

	public void setUseCMDOptions(boolean useCMDOptions) {
		this.useCMDOptions = useCMDOptions;
	}
}
