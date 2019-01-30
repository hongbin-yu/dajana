package com.filemark.jcr.serviceImpl;



import java.io.IOException;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.filemark.jcr.service.JcrServices;

public class XMPPServiceImpl implements IncomingChatMessageListener {

	private final Logger log = LoggerFactory.getLogger(XMPPServiceImpl.class);
	private static String host = "host ip";
	private static String domain = "dajana.ca";
	private static int port = 5222;
	@Autowired
	private JcrServices jcrService;

	private AbstractXMPPConnection connection;

	public void initialize() {
		log.info("login "+domain+":"+port+" by yuhong");
		login("admin","admin");
	}
	
	public void login(String username,String password) {
		try {
			XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration
			    .builder()
			    .setUsernameAndPassword(username, password)
			    .setHost(domain)
			    .setXmppDomain(domain)
			    .setPort(port)
			    .setCompressionEnabled(false)
			    .setSecurityMode(SecurityMode.disabled)
			    .build();

				connection = new XMPPTCPConnection(conf);

				connection.connect().login();
				connection.isAuthenticated();
				log.info("XMPPCOnnection:"+connection);
			} catch (SmackException e) {
				log.error(e.getMessage());
			} catch (IOException e) {
				log.error(e.getMessage());
			} catch (XMPPException e) {
				log.error(e.getMessage());
			} catch (InterruptedException e) {
				log.error(e.getMessage());
			}

	}

	
	
	public void disconnect() {
		connection.disconnect();
	}


	@Override
	public void newIncomingMessage(EntityBareJid enId, Message message, Chat chat) {
		log.info("Partner:"+chat.getXmppAddressOfChatPartner());
		log.info("Entity:"+enId.asEntityBareJidString());		
		log.info("message:"+message.getBody());	
	}

/*	@Override
	public void processMessage(Message message) {
		log.info("message:"+message.getBody());
		
	}*/



	
}
