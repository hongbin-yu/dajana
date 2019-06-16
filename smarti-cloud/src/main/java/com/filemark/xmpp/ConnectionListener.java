package com.filemark.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.filemark.jcr.serviceImpl.XMPPServiceImpl;

public class ConnectionListener implements org.jivesoftware.smack.ConnectionListener {
    private XMPPServiceImpl xmppService;
	private final Logger log = LoggerFactory.getLogger(ConnectionListener.class);
  
    
	@Override
	public void authenticated(XMPPConnection arg0, boolean arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connected(XMPPConnection arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionClosed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionClosedOnError(Exception arg0) {
		// TODO Auto-generated method stub
		
	}

}
