package com.filemark.utils;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;






public class LicenseManager implements HttpSessionListener {
	 private static int  licenseNumber = 100;
	  private static int totalActiveSessions = 0;
	  private static final Logger log = LoggerFactory.getLogger(LicenseManager.class);
	  
	  
	  
	  public LicenseManager() {
		super();
		log.info("License Manager Ativated.");
	}
	public static int getLicenseNumber() {
		  return licenseNumber;
	  }
	  public static int getTotalActiveSession(){
			return totalActiveSessions;
		  }
		 

		  public void sessionCreated(HttpSessionEvent arg0) {
			totalActiveSessions++;
			log.debug("sessionCreated - add one session into counter, total="+totalActiveSessions+"/"+licenseNumber);
		  }
		 

		  public void sessionDestroyed(HttpSessionEvent arg0) {
			totalActiveSessions--;
			log.debug("sessionDestroyed - deduct one session from counter, total="+totalActiveSessions+"/"+licenseNumber);
		  }
}
