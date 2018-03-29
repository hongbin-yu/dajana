package com.filemark.jcr.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.filemark.jcr.model.Asset;
import com.filemark.jcr.model.GeoIP;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
//http://geolite.maxmind.com/download/geoip/database/GeoLite2-City.tar.gz
public class IP2GeoLocationService {
    private DatabaseReader dbReader;
    private String dbLcation=Asset.getDevicePath()+"/database/GeoLite2-City_20180327/GeoLite2-City.mmdb";
	private final Logger log = LoggerFactory.getLogger(IP2GeoLocationService.class);
    
    public IP2GeoLocationService(){
    	try {
	        File database = new File(dbLcation);
	        dbReader = new DatabaseReader.Builder(database).build();
    	}catch(IOException e) {
    		log.error(e.getMessage());
    	}
    }
     
    public GeoIP getLocation(String ip) 
      throws IOException, GeoIp2Exception {
        InetAddress ipAddress = InetAddress.getByName(ip);
        CityResponse response = dbReader.city(ipAddress);
         
        String cityName = response.getCity().getName();
        String latitude = 
          response.getLocation().getLatitude().toString();
        String longitude = 
          response.getLocation().getLongitude().toString();
        return new GeoIP(ip, cityName, latitude, longitude);
    }

	public String getDbLcation() {
		return dbLcation;
	}

	public void setDbLcation(String dbLcation) {
		this.dbLcation = dbLcation;
	}
    
    
}
