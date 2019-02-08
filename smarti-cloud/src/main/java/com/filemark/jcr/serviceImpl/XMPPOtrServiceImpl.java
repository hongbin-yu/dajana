package com.filemark.jcr.serviceImpl;

import java.security.KeyPair;

import org.jivesoftware.smack.XMPPConnection;

import net.java.otr4j.OtrEngineHost;
import net.java.otr4j.OtrException;
import net.java.otr4j.OtrKeyManagerImpl;
import net.java.otr4j.OtrPolicy;
import net.java.otr4j.OtrPolicyImpl;
import net.java.otr4j.crypto.OtrCryptoEngineImpl;
import net.java.otr4j.session.FragmenterInstructions;
import net.java.otr4j.session.InstanceTag;
import net.java.otr4j.session.OtrSm;
import net.java.otr4j.session.SessionID;

public class XMPPOtrServiceImpl implements OtrEngineHost {
	private String peer;
	private XMPPConnection con;
	private OtrKeyManagerImpl otrKeyManager;
	private OtrPolicy otrPolicy;
	private SessionID sessionID;
	private OtrSm otrSm;
    private OtrCryptoEngineImpl otrEngine;
    
    
    XMPPOtrServiceImpl() {
    	
    }
    
	@Override
	public void askForSecret(SessionID arg0, InstanceTag arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finishedSessionMessage(SessionID arg0, String arg1)
			throws OtrException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getFallbackMessage(SessionID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FragmenterInstructions getFragmenterInstructions(SessionID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getLocalFingerprintRaw(SessionID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KeyPair getLocalKeyPair(SessionID arg0) throws OtrException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReplyForUnreadableMessage(SessionID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OtrPolicy getSessionPolicy(SessionID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void injectMessage(SessionID arg0, String arg1) throws OtrException {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageFromAnotherInstanceReceived(SessionID arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void multipleInstancesDetected(SessionID arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requireEncryptedMessage(SessionID arg0, String arg1)
			throws OtrException {
		// TODO Auto-generated method stub

	}

	@Override
	public void showError(SessionID arg0, String arg1) throws OtrException {
		// TODO Auto-generated method stub

	}

	@Override
	public void smpAborted(SessionID arg0) throws OtrException {
		// TODO Auto-generated method stub

	}

	@Override
	public void smpError(SessionID arg0, int arg1, boolean arg2)
			throws OtrException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unencryptedMessageReceived(SessionID arg0, String arg1)
			throws OtrException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unreadableMessageReceived(SessionID arg0) throws OtrException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unverify(SessionID arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void verify(SessionID arg0, String arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

}
