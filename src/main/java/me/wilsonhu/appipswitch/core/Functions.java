package me.wilsonhu.appipswitch.core;

import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

import me.wilsonhu.appipswitch.AppIPSwitch;

public class Functions {
    
    private String FORCE_BIND_IP_NAME = "ForceBindIP.exe";
    private String FORCE_BIND_IP_NAME_64 = "ForceBindIP64.exe";
    private int ARBRITRARY_ARRAY_SIZE = 100;
    private Enumeration<NetworkInterface> NetInterface;
    
    public boolean isProgramFound(){
        boolean check = new File(AppIPSwitch.getInstance().getSettings().getForceBindDir() + File.separator + FORCE_BIND_IP_NAME).exists();
        return check;
    }
    
    public void changeDir(String newDir){

        AppIPSwitch.getInstance().getSettings().setForceBindDir(newDir);
        AppIPSwitch.getInstance().getJsonManager().writeSettingsJson();
    }
    
    public String[] getNetInterfaceInfo(){
        String[] outArr = new String[ARBRITRARY_ARRAY_SIZE];
        int count = 0;
        for (NetworkInterface netIf : Collections.list(NetInterface)) {
            if(netIf.getInetAddresses().hasMoreElements() && netIf.getInetAddresses().nextElement().getHostAddress().charAt(3) == '.'){
                outArr[count] = netIf.getDisplayName() + " " + netIf.getInetAddresses().nextElement().getHostAddress();
                count++;
            }
        }
        String[] newArr = new String[count];
        for(int x = 0; x < count; x++)
            newArr[x] = outArr[x];
        return newArr;
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
        System.out.println(cmd);
        pb = new ProcessBuilder("cmd.exe", "/c", cmd);
        pb.redirectErrorStream(true).inheritIO();
        Process p =  pb.start();
        return p;
    }
}
