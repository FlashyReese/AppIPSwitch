package me.wilsonhu.appipswitch.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import me.wilsonhu.appipswitch.AppIPSwitch;

public class JsonManager {
	
	private AppIPSwitch gui;
	
	public JsonManager(AppIPSwitch main) {
		this.gui = main;
	}
	
	public void loadAllJsonSettings() {
		readProfilesJson();
		gui.profileDropList.loadSettings();
    	readSettingsJson();
	}
	
	public void writeProfilesJson() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(gui.getProfileManager().getProfiles());
            FileWriter fw = new FileWriter("profiles.json");
            fw.write(json);
            fw.flush();
            fw.close();
        } catch (Exception ex) {}
    }
	
	public void readProfilesJson() {
		try {
            Gson gson = new Gson();
            FileReader fileReader = new FileReader("profiles.json");
            BufferedReader buffered = new BufferedReader(fileReader);
            Type type = new TypeToken<ArrayList<Profile>>(){}.getType();
            gui.getProfileManager().setProfile(gson.fromJson(fileReader, type));
            buffered.close();
            fileReader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
	
	public void writeSettingsJson() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(gui.getSettings());
            FileWriter fw = new FileWriter("settings.json");
            fw.write(json);
            fw.flush();
            fw.close();
        } catch (Exception ex) {}
    }
	
	public void readSettingsJson() {
		try {
            Gson gson = new Gson();
            FileReader fileReader = new FileReader("settings.json");
            BufferedReader buffered = new BufferedReader(fileReader);
            Type type = new TypeToken<Settings>(){}.getType();
            gui.setSettings(gson.fromJson(fileReader, type));
            buffered.close();
            fileReader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
	
}
