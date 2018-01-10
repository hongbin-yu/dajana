package com.filemark.utils;

public class UsageReport {
	private long id;
	private int year;
	private int month;
	private int day;
	private String info;
	private long   count;
	private int event;

	
	
/*
	public UsageReport(int year, int month, int event,long count) {
		this.year = year;
		this.month = month;
		this.count = count;
		this.event = event;
	}

	public UsageReport(int year, int month,int day, int event,long count) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.count = count;
		this.event = event;
	}
	*/
	public UsageReport(int year, int month, String info,long count) {
		this.year = year;
		this.month = month;
		this.count = count;
		this.info = info;
	}

	public UsageReport(int year, int month, int day, int event, String info,long count) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.event = event;
		this.count = count;
		this.info = info;
	}
	
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public int getEvent() {
		return event;
	}
	public void setEvent(int event) {
		this.event = event;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	
}
