package com.filemark.jcr.service;

import javax.jcr.RepositoryException;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jxmpp.stringprep.XmppStringprepException;

public interface XMPPService {
	public void initialize();
	public void sendVerifyCode(String to) throws RepositoryException, NotConnectedException, XmppStringprepException, XMPPException, InterruptedException;
}
