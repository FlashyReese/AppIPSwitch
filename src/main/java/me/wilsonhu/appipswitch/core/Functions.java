package me.wilsonhu.appipswitch.core;

import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;

import me.wilsonhu.appipswitch.AppIPSwitch;

public class Functions {
    
    private String FORCE_BIND_IP_NAME = "ForceBindIP.exe";
    private String FORCE_BIND_IP_NAME_64 = "ForceBindIP64.exe";
    
    public boolean isProgramFound(){
        boolean check = new File(AppIPSwitch.getInstance().getSettings().getForceBindDir() + File.separator + FORCE_BIND_IP_NAME).exists();
        return check;
    }
    
    public void changeDir(String newDir){
        AppIPSwitch.getInstance().getSettings().setForceBindDir(newDir);
        AppIPSwitch.getInstance().getJsonManager().writeSettingsJson();
    }
    
	public String readFileAsString(String fileName)throws Exception{ 
		String data = ""; 
	    data = new String(Files.readAllBytes(Paths.get(fileName))); 
	    return data; 
	} 
    
    public Process runProcess(boolean i, boolean x64, String useNetIntAddress, String programFile, String cmdOptions) throws IOException{
        ProcessBuilder pb;
        String cmd;
        if(i && x64){
            cmd = "cd "+ AppIPSwitch.getInstance().getSettings().getForceBindDir() + " && " + FORCE_BIND_IP_NAME_64 + " -i " + useNetIntAddress + " \"" + programFile + "\" " + cmdOptions;
        }else if(i){
            cmd =  "cd "+ AppIPSwitch.getInstance().getSettings().getForceBindDir() + " && " + FORCE_BIND_IP_NAME + " -i " + useNetIntAddress + " \"" + programFile + "\" " + cmdOptions;
        }else if(x64){
            cmd =  "cd "+ AppIPSwitch.getInstance().getSettings().getForceBindDir() + " && " + FORCE_BIND_IP_NAME_64 + " " + useNetIntAddress + " \"" + programFile + "\" " + cmdOptions;
        }else{
            cmd =  "cd "+ AppIPSwitch.getInstance().getSettings().getForceBindDir() + " && " + FORCE_BIND_IP_NAME + " " + useNetIntAddress + " \"" + programFile + "\" " + cmdOptions;
        }
        pb = new ProcessBuilder("cmd.exe", "/c", cmd);
        pb.redirectErrorStream(true).inheritIO();
        Process p =  pb.start();
        return p;
    }
}
