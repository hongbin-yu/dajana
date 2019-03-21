package com.filemark.utils;
public class Buddy {

	String username, name;
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
		this.online = online;
	}

	public  boolean isOnline() {
		return online;
	}


}
