package com.filemark.jcr.service;

import java.util.Collection;

import javax.jcr.RepositoryException;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.stringprep.XmppStringprepException;

import com.filemark.jcr.model.Asset;
import com.filemark.utils.Buddy;

public interface XMPPService {
	public void initialize();
	public void sendVerifyCode(String to) throws RepositoryException, NotConnectedException, XmppStringprepException, XMPPException, InterruptedException;
	public void sendMessage(String message, String to) throws XMPPException, NotConnectedException, XmppStringprepException, InterruptedException;
	public void sendAsset(Asset asset, String to);
	public Roster getRoster();
	public Collection<Presence> getPresences();
	public Collection<Buddy> getBuddies();	
	public long getOnlineCount();	

}
