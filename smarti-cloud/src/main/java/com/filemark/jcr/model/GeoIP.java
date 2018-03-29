package com.filemark.jcr.model;

public class GeoIP {
    private String ipAddress;
    private String city;
    private String latitude;
    private String longitude;
    
	public GeoIP(String ip, String cityName, String latitude2, String longitude2) {
		ipAddress = ip;
		city = cityName;
		latitude = latitude2;
		longitude = longitude2;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
    
    
}
