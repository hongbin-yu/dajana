package com.filemark.utils;
public class Buddy {

	String username, name;
	String  status = "离线";
	boolean online;

	public  Buddy(String name) {
		this.username = name;
	}

	public final  void setName(String name) {
		this.name = name;
	}

	public  String getName() {
		return name;
	}

	public  String getUsername() {
		return username;
	}

	public  void setOnline(boolean online) {
		if(online) { 
			status = "在线";
		} else { 
			status = "离线";
		}

		this.online = online;
	}

	public  boolean isOnline() {
		return online;
	}


	public void setUsername(String username) {
		this.username = username;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


}
