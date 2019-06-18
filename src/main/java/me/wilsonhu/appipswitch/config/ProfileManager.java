package me.wilsonhu.appipswitch.config;

import java.util.ArrayList;
import java.util.List;

public class ProfileManager {
	private List<Profile> profiles = new ArrayList<Profile>();
	
	public List<Profile> getProfiles(){
		return profiles;
	}
	
	public void setProfile(ArrayList<Profile> list) {
		this.profiles = list;
	}
}
