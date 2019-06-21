package me.wilsonhu.appipswitch.core;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import me.wilsonhu.appipswitch.AppIPSwitch;
import me.wilsonhu.appipswitch.config.Profile;

public class Functions {
    
    private String FORCE_BIND_IP_NAME = "ForceBindIP.exe";
    private String FORCE_BIND_IP_NAME_64 = "ForceBindIP64.exe";
    
    private AppIPSwitch gui;
    
    public Functions(AppIPSwitch main) {
    	this.gui = main;
    }
    
    public boolean isProgramFound(){
        boolean check = new File(gui.getSettings().getForceBindDir() + File.separator + FORCE_BIND_IP_NAME).exists();
        return check;
    }
    
    public void changeDir(String newDir){
        gui.getSettings().setForceBindDir(newDir);
        gui.getJsonManager().writeSettingsJson();
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
            cmd = "cd "+ gui.getSettings().getForceBindDir() + " && " + FORCE_BIND_IP_NAME_64 + " -i " + useNetIntAddress + " \"" + programFile + "\" " + cmdOptions;
        }else if(i){
            cmd =  "cd "+ gui.getSettings().getForceBindDir() + " && " + FORCE_BIND_IP_NAME + " -i " + useNetIntAddress + " \"" + programFile + "\" " + cmdOptions;
        }else if(x64){
            cmd =  "cd "+ gui.getSettings().getForceBindDir() + " && " + FORCE_BIND_IP_NAME_64 + " " + useNetIntAddress + " \"" + programFile + "\" " + cmdOptions;
        }else{
            cmd =  "cd "+ gui.getSettings().getForceBindDir() + " && " + FORCE_BIND_IP_NAME + " " + useNetIntAddress + " \"" + programFile + "\" " + cmdOptions;
        }
        pb = new ProcessBuilder("cmd.exe", "/c", cmd);
        pb.redirectErrorStream(true).inheritIO();
        Process p =  pb.start();
        return p;
    }
    
    public void updateOptionsFromProfile() {
    	gui.txtExeDirectory.setText(gui.profile.getExecutable());
		gui.txtName.setText(gui.profile.getName());
		gui.x64.setSelected(gui.profile.isX64());
		gui.useI.setSelected(gui.profile.isUseI());
		gui.useCMDOptions.setSelected(gui.profile.isUseCMDOptions());
		gui.txtCMDOptions.setText(gui.profile.getCmdOptions());
		loadSavedMACAddress(gui.profile.getNID());
	}
	
	public void updateProfileFromOptions() {
		if(gui.profileDropList.getSelectedValue() == null)return;
		if(!gui.txtName.getText().trim().isEmpty()) {
			gui.profileDropList.getSelectedValue().setName(gui.txtName.getText());
		}else {
			gui.txtName.setText(gui.profileDropList.getSelectedValue().getName());
		}
		if(!gui.txtExeDirectory.getText().trim().toLowerCase().endsWith(".exe") && !gui.txtExeDirectory.getText().trim().isEmpty()) {
			gui.profileDropList.getSelectedValue().setExecutable(gui.txtExeDirectory.getText());
		}else {
			gui.txtExeDirectory.setText(gui.profileDropList.getSelectedValue().getExecutable());
		}
		gui.profileDropList.getSelectedValue().setExecutable(gui.txtExeDirectory.getText());
		gui.profileDropList.getSelectedValue().setCmdOptions(gui.txtCMDOptions.getText());
		gui.profileDropList.getSelectedValue().setX64(gui.x64.isSelected());
		gui.profileDropList.getSelectedValue().setUseI(gui.useI.isSelected());
		gui.profileDropList.getSelectedValue().setUseCMDOptions(gui.useCMDOptions.isSelected());
		gui.profileDropList.refresh();
		gui.getProfileManager().getProfiles().clear();
		for(int i = 0; i < gui.profileDropList.getList().getModel().getSize(); i++){
		     Profile p = gui.profileDropList.getList().getModel().getElementAt(i);
		     gui.getProfileManager().getProfiles().add(p);
		}
		gui.getJsonManager().writeProfilesJson();
	}
	
	public void updateNIDLabels() {
		NetworkInterface currentInterface = (NetworkInterface)gui.networkDevices.getSelectedItem();
		if(currentInterface == null)return;
		gui.nidAddress.setText("IPv4 Address:     " + currentInterface.getInetAddresses().nextElement().getHostAddress());
		byte[] mac1 = null;
		String macAddress = "";
		try {
			mac1 = currentInterface.getHardwareAddress();
		} catch (SocketException e) {
			e.printStackTrace();
		}
        if (mac1 != null) {
            for (int k = 0; k < mac1.length; k++) {
                macAddress = macAddress + String.format("%02X%s", mac1[k], (k < mac1.length - 1) ? "-" : "");
            }
        }
        if(macAddress.trim().equals("")) {
        	gui.nidMACAddress.setText("MAC Address:     Not Available");
        }else {
        	gui.nidMACAddress.setText("MAC Address:     " + macAddress);
        }
        gui.nidType.setText("Network Type:   " + currentInterface.getName());
    	if(gui.profileDropList.getSelectedValue() != null)gui.profileDropList.getSelectedValue().setNID(macAddress);
	}
	
	public void refreshNetworkInterfaces() {
		gui.networkDevices.removeAllItems();
		try {
			for(NetworkInterface nid: Collections.list(NetworkInterface.getNetworkInterfaces())) {
				if(nid.getInetAddresses().hasMoreElements() && nid.getInetAddresses().nextElement().getHostAddress().contains(".")) {
					gui.networkDevices.addItem(nid);
				}
			}
		} catch (SocketException e2) {
			e2.printStackTrace();
		}
		updateNIDLabels();
	}

	public void loadSavedMACAddress(String mac) {
		int size = gui.networkDevices.getItemCount();
		for (int i = 0; i < size; i++) {
		  NetworkInterface item = gui.networkDevices.getItemAt(i);
		  byte[] mac1 = null;
		  String macAddress = "";
		  try {
			  mac1 = item.getHardwareAddress();
		  } catch (SocketException e) {
			  e.printStackTrace();
		  }
		  if (mac1 != null) {
			  for (int k = 0; k < mac1.length; k++) {
				  macAddress = macAddress + String.format("%02X%s", mac1[k], (k < mac1.length - 1) ? "-" : "");
			  }
		  }
		  if(mac.equalsIgnoreCase(macAddress) && !mac.trim().isEmpty()) {
			  gui.networkDevices.setSelectedItem(item);
		  }
		}
	}
	
	public void refreshFoundLabel() throws Exception{
		if(gui.getFunctions().isProgramFound()){
			gui.txtisFound.setText("Found");
			gui.txtisFound.setForeground(Color.GREEN);
			gui.txtForceBindIPVersion.setText("Version: " + gui.getFunctions().readFileAsString(gui.getSettings().getForceBindDir() + File.separator + gui.getSettings().getForceBindVersionFile()));
		}else{
			gui.txtisFound.setText("Not Found");
			gui.txtisFound.setForeground(Color.RED);
			gui.txtForceBindIPVersion.setText("");
		}
		
		gui.txtcurrentDir.setText("Directory: " + gui.getSettings().getForceBindDir());
		gui.txtcurrentDir.setToolTipText(gui.getSettings().getForceBindDir());
	}
	
	public void checkItemSelected() {
		if(gui.isItemSelected() && gui.getFunctions().isProgramFound()) {
			gui.run.setEnabled(true);
			gui.txtName.setEnabled(true);
			gui.networkDevices.setEnabled(true);
			gui.x64.setEnabled(true);
			gui.useI.setEnabled(true);
			gui.useCMDOptions.setEnabled(true);
		}else {
			gui.run.setEnabled(false);
			gui.txtName.setEnabled(false);
			gui.networkDevices.setEnabled(false);
			gui.x64.setEnabled(false);
			gui.useI.setEnabled(false);
			gui.useCMDOptions.setEnabled(false);
			gui.txtCMDOptions.setEnabled(false);
		}
	}
}
