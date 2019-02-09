package com.filemark.jcr.serviceImpl;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.activation.MimetypesFileTypeMap;
import javax.jcr.RepositoryException;
import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.xhtmlim.packet.XHTMLExtension;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.pegdown.PegDownProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.filemark.jcr.model.Asset;
import com.filemark.jcr.model.Device;
import com.filemark.jcr.model.Folder;
import com.filemark.jcr.model.User;
import com.filemark.jcr.service.JcrServices;


public class XMPPServiceImpl {

	private final Logger log = LoggerFactory.getLogger(XMPPServiceImpl.class);
	private static String host = "host ip";
	private static String filedomain = "tu.dajana.ca";	
	private static String fileport = "";		
	private static String domain = "dajana.ca";
	private static int port = 5222;
	private static Options options = new Options();
	@Autowired
	private JcrServices jcrService;
	private PingManager pingManager;
	ReconnectionManager reconnectionManager;
	private AbstractXMPPConnection connection;
	private ConnectionListener connectionListener;
	private Roster roster;

	//private PingFailedListener pingFailedListener ;
	private static final PegDownProcessor pegDownProcessor = new PegDownProcessor();
			//Extensions.ALL | Extensions.SUPPRESS_ALL_HTML, 5000);
			
	//private IncomingChatMessageListener incomingChatMessageListener;
	public void initialize() {
		System.gc();
		//if(connection == null) {
			log.info("login "+domain+":"+port+" by yuhong");
			login("admin","admin");			
		//}
		//options.addOption("-c", "验证码");
	}
	
	public void login(String username,String password) {
		
		try {
			XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration
			    .builder()
			    .setUsernameAndPassword(username, password)
			    .setResource(filedomain)
			    .setHost(domain)
			    .setXmppDomain(domain)
			    .setPort(port)
			    .setSendPresence(true)
			    .setCompressionEnabled(false)
			    .setSecurityMode(SecurityMode.disabled)
			    .build();
			    connection = new XMPPTCPConnection(conf);
				connection.connect().login();
				roster = Roster.getInstanceFor(connection);
			    //connection.setReplyTimeout(10000);
			    
				reconnectionManager = ReconnectionManager.getInstanceFor(connection);
				ReconnectionManager.setEnabledPerDefault(true);
				reconnectionManager.enableAutomaticReconnection();
				reconnectionManager.setFixedDelay(60);
				installConnectionListeners(connection);
	            installIncomingChatMessageListener(connection);

				//connection.isAuthenticated();
			    pingManager = PingManager.getInstanceFor(connection);
			    pingManager.setPingInterval(30);
			    pingManager.pingMyServer();	
			    pingManager.registerPingFailedListener(new PingFailedListener() {

					@Override
					public void pingFailed() {
						log.error("Ping failed:");
						checkConnection();
					}
		        	
		        });
			    

			
	            log.info("Connection:"+connection);

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
		log.debug("remove connection:"+connection);
		connection.removeConnectionListener(connectionListener);
		connection.disconnect();
	}

	public void sendMessage(String message, String to) throws XMPPException, NotConnectedException, XmppStringprepException, InterruptedException {
		ChatManager chatManager = ChatManager.getInstanceFor(connection);
		Chat chat = chatManager.chatWith(JidCreate.entityBareFrom(to)); // pass XmppClient instance as listener for received messages.
		chat.send(message);
	}
	
	public void sendMessage(String message, EntityBareJid to) throws XMPPException, NotConnectedException, XmppStringprepException, InterruptedException {
		ChatManager chatManager = ChatManager.getInstanceFor(connection);
		Chat chat = chatManager.chatWith(to); // pass XmppClient instance as listener for received messages.
		chat.send(message);
	}

	public void sendMessage(Message message, EntityBareJid to) throws XMPPException, NotConnectedException, XmppStringprepException, InterruptedException {
		ChatManager chatManager = ChatManager.getInstanceFor(connection);
		Chat chat = chatManager.chatWith(to); // pass XmppClient instance as listener for received messages.
		chat.send(message);
	}	

	private void installIncomingChatMessageListener(final AbstractXMPPConnection connection) {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);  

        chatManager.addIncomingListener(new IncomingChatMessageListener() {
            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) { 
            	processMessage(from, message,chat);
            }
        });			
	}

	private void processMessage(EntityBareJid from, Message message, Chat chat) {
        System.out.println(message.getType()+"/"+message.getSubject()+"/ "+from+" say: " + message.getBody());
        //log.info(message.toString());
        try {
            //sendMessage(message.getBody(),from);

        	switch(parseType(message.getBody())) {
	        case 0:

	        	processChat(from,message,chat);

	        	break;
	        case 1:
	        	processSearch(from,message,chat);
	        	sendMessage("收到",from);
	        	break;
	        case 2:
	        	processAssets(from,message,chat);

	        	break;
	        case 3:
	        	processCommand(from,message,chat);
	        	sendMessage("结果:",from);
	        	break;
	        case 100:
	        	sendVerifyCode(from.toString());

	        	break;	
	        case 101:
	        	sendVerifyCode(from.toString());

	        	break;		        	
	
	        }
	        
        }catch(Exception e) {
        	log.error(e.getMessage());
        	try {
				sendMessage(e.getMessage(),from);
			} catch (NotConnectedException e1) {
				log.error(e1.getMessage());
			} catch (XmppStringprepException e1) {
				log.error(e1.getMessage());
			} catch (XMPPException e1) {
				log.error(e1.getMessage());;
			} catch (InterruptedException e1) {
				log.error(e1.getMessage());
			}
        }
		
	}
	private int parseType(String body) {
		//CommandLineParser parser = new DefaultParser();
		if(body.startsWith("?OTRv23?")) return 101;
		if(body.equals("-c")) return 100;
		if(body.startsWith("-")) return 1;
		if(body.indexOf("/httpfileupload/")>0) return 2;	
		if(body.endsWith("?") || body.endsWith("？")) return 3;
		return 0;
		
	}
	
	private void sendHtmlLink(EntityBareJid from, String url, Asset asset) throws NotConnectedException, XMPPException, InterruptedException, XmppStringprepException {
		// User1 creates a message to send to user2
		String title = asset.getTitle();
		Message msg = new Message();
		msg.setSubject(title);
		msg.setBody(url);
		// Create a XHTMLExtension Package and add it to the message
		String filePath = asset.getFilePath()+"/x100.jpg";
		File icon = new File(filePath);
		if(!icon.exists()) jcrService.createIcon(asset.getPath(), 100, 100);
		String imageString = "https://"+filedomain+fileport+"/resources/images/document-icon100.png";
		try {
	    	InputStream is = new FileInputStream(filePath);    	

			byte buffer[] = new byte[is.available()];
			IOUtils.read(is, buffer);			
			is.close();
			imageString = Base64.encodeBase64String(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XHTMLExtension xhtmlExtension = new XHTMLExtension();

		xhtmlExtension.addBody(
		"<body><p style='font-size:large'><a href='"+url+"' title=''><h5>"+title+"</h5><img src='data:image/jpg;base64, "+imageString+"' alt=''></a></p></body>");
		msg.addExtension(xhtmlExtension);
		// User1 sends the message that contains the XHTML to user2
		//log.info(imageString);
		sendMessage(url,from);
	    sendMessage(msg,from);
	    Thread.sleep(200);

	}
	
	private void sendVerifyCode(String from) throws RepositoryException, NotConnectedException, XmppStringprepException, XMPPException, InterruptedException {
		String username = from.toString().split("@")[0];
		User dbuser  = (User)jcrService.getObject("/system/users/"+username);
		Date now = new Date();
		//if(dbuser.getLastVerified()==null || now.getTime() - dbuser.getLastVerified().getTime() > 120000) {
			Random random = new Random();
			int code = random.nextInt(899999)+100000;
			dbuser.setCode(""+code);
			dbuser.setLastVerified(now);
			jcrService.addOrUpdate(dbuser);	
		//}
		
		sendMessage("优云验证码："+dbuser.getCode() +"两分钟内有效",from);
	}
	
	private void processAssets(EntityBareJid from, Message message, Chat chat) throws NotConnectedException, XmppStringprepException, XMPPException, InterruptedException, RepositoryException {
		String fileupload[] = message.getBody().split("\n");
		String username = from.toString().split("@")[0];
		String filepath = "/assets/"+username+"/httpfileupload";
		String html ="<section class=\"wb-lbx lbx-hide-gal\"><ul class=\"list-inline\">";
   		if(!jcrService.nodeExsits(filepath)) {
   			Folder folder = new Folder();
   			folder.setTitle("云存储");
   			folder.setName("httpfileupload");
   			folder.setPath(filepath);
   			folder.setLastUpdated(new Date());
   			folder.setLastModified(new Date());   			
   			jcrService.addOrUpdate(folder);
   		}
   		
		for(String url:fileupload) {
			Asset asset = importAsset(url,username.toString(),filepath);
			String httpfileupload = "/protected/httpfileupload/"+asset.getUid()+"/"+asset.getName();
			log.info(httpfileupload);
			html +="<li><a href=\""+httpfileupload+"\" title=\"\"><img src=\""+httpfileupload+"?w=4\" alt=\"\"></li>";
			//sendMessage("https://"+filedomain+fileport+httpfileupload,from);
			try {
				sendHtmlLink(from,"https://"+filedomain+fileport+httpfileupload,asset);
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		html +="</ul></section>";
		String subject = message.getSubject();
		if(subject == null) subject = username;
		
		String path = "/youchat/"+subject;
		if(!jcrService.nodeExsits(path)) {
   			Folder folder = new Folder();
   			folder.setName(subject);
   			folder.setPath(path);
   			folder.setLastUpdated(new Date());
   			folder.setLastModified(new Date());
   			jcrService.addOrUpdate(folder);
   		}	   		
		
		addChat(html,username,path);

	}
	
	private Asset importAsset(String url, String username, String path) {
		Asset asset= new Asset();
		String fileName = "error404.html";
		String nodeName = url.substring(url.lastIndexOf("/")+1, url.length());
		try {
			if(!jcrService.nodeExsits(path)) {
				jcrService.addNodes(path, "nt:unstructured",username);		
			}			
	        URL url_img = new URL(url.replace(" ", "+"));
	    	HttpsURLConnection conn = (HttpsURLConnection) url_img.openConnection();
	    	conn.setReadTimeout(30000);
	    	conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
	    	conn.addRequestProperty("User-Agent", "Mozilla");
	    	conn.addRequestProperty("Referer", "dajana.cn");
	    	boolean redirect = false;
	
	    	// normally, 3xx is redirect
	    	int status = conn.getResponseCode();
	    	if (status != HttpURLConnection.HTTP_OK) {
	    		if (status == HttpURLConnection.HTTP_MOVED_TEMP
	    			|| status == HttpURLConnection.HTTP_MOVED_PERM
	    				|| status == HttpURLConnection.HTTP_SEE_OTHER)
	    		redirect = true;
	    	}
	    	log.info("Status:"+status);
	    	if (redirect) {
	
	    		// get redirect url from "location" header field
	    		String newUrl = conn.getHeaderField("Location");
	
	    		// get the cookie if need, for login
	    		String cookies = conn.getHeaderField("Set-Cookie");
	
	    		// open the new connnection again
	    		url_img = new URL(newUrl);
	    		conn = (HttpsURLConnection) url_img.openConnection();
	    		conn.setRequestProperty("Cookie", cookies);
	    		conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
	    		conn.addRequestProperty("User-Agent", "Mozilla");
	    		conn.addRequestProperty("Referer", "dajana.cn");
	
	    		log.debug("Redirect to:"+newUrl);
	
	    	}        
	        
	    	String contentType = conn.getContentType();
	    	String devicePath = "/system/devices/default";//jcrService.getDevice();
			if(contentType != null && contentType.startsWith("video/")) {
				devicePath = "/system/devices/backup";//jcrService.getBackup();
			}
		    MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
		    log.debug("contentType="+contentType);
		    String ext="";
		    if(contentType !=null)
			try {
				MimeType mimeType = allTypes.forName(contentType);
			    ext = mimeType.getExtension(); 
			} catch (MimeTypeException e1) {
				log.error(e1.getMessage());
			}
			String urlPath = url;
			if(ext==null || "".equals(ext))
				ext = urlPath.replaceFirst("^.*/[^/]*(\\.[^\\./]*|)$", "$1");


			fileName = nodeName;
			fileName = nodeName.replaceAll(" ", "-");
			if(!fileName.matches("(\\w|\\.|\\-|\\s|_)+")) {
				fileName = ""+getDateTime()+ext;
			}
			if(!fileName.endsWith(ext)) fileName +=ext;
			String assetPath =  path+"/"+fileName;
			if(jcrService.nodeExsits(path+"/"+fileName)) {
				asset = (Asset)jcrService.getObject(path+"/"+fileName);
				if(asset.getFilePath()==null) {
					Device device = (Device)jcrService.getObject(asset.getDevice());
					asset.setFilePath(device.getLocation()+asset.getPath());
					jcrService.updatePropertyByPath(asset.getPath(), "filePath", asset.getFilePath());
				}
				return asset;//"/protected/httpfileupload/"+asset.getUid()+"/"+fileName;
			}else {
				assetPath = jcrService.getUniquePath(path, fileName);
			}
			if(contentType==null || "".equals(contentType))
				contentType = new MimetypesFileTypeMap().getContentType(nodeName);
			log.debug("nodeName="+nodeName);
	    	asset.setTitle(nodeName);	
	    	asset.setName(nodeName);
			asset.setCreatedBy(username);
			asset.setPath(assetPath);
			asset.setLastModified(new Date());
			asset.setContentType(contentType);
			asset.setDevice(devicePath);	
			asset.setExt(ext);

			if(asset.getDevice()!=null) {
				Device device = (Device)jcrService.getObject(asset.getDevice());
				log.debug("Writing device "+device.getPath() +":"+device.getLocation());
				
				File folder = new File(device.getLocation()+asset.getPath());
				File file = new File(device.getLocation()+asset.getPath()+"/origin"+ext);
				if(!folder.exists()) folder.mkdirs();
	/*			if(!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
	*/
		
				//FileUtils.copyURLToFile(url_img, file);
		    	InputStream is = conn.getInputStream();
				FileUtils.copyInputStreamToFile(is, file);
				is.close();
				Date end = new Date();
				//long speed = file.length()*8/(end.getTime() - start.getTime()); 
				asset.setFilePath(device.getLocation()+asset.getPath());
				asset.setSize(file.length());
				asset.setOriginalDate(new Date(file.lastModified()));
				jcrService.addOrUpdate(asset);
				jcrService.updateCalendar(path,"lastModified");
				jcrService.setProperty(path, "changed", "true");
				//asset.setTitle(asset.getTitle() +" - "+speed+"kb/s");
				//output.close();
			}else {
				log.debug("Writing jcr");
		    	InputStream is = conn.getInputStream();
				jcrService.addFile(assetPath,"original",is,contentType);
				is.close();
			}
			if(contentType != null && contentType.startsWith("image/")) {
				jcrService.autoRoateImage(assetPath);
				log.debug("create icon");
				jcrService.createIcon(assetPath, 400,400);
				jcrService.createIcon(assetPath, 100,100);				
			}		
			
			log.info("Done");
		    
		}catch (Exception e){
			log.error("error:"+e.getMessage());

		}
		
		return asset;//"/protected/httpfileupload/"+asset.getUid()+"/"+nodeName;
	}
	
	private void processCommand(EntityBareJid from, Message message, Chat chat) {
		
	}	
	
	private void processSearch(EntityBareJid from, Message message, Chat chat) throws NotConnectedException, XmppStringprepException, XMPPException, InterruptedException {


		String html = pegDownProcessor.markdownToHtml(message.getBody());
		sendMessage(html,from);
	}

	private void processChat(EntityBareJid from, Message message, Chat chat) throws NotConnectedException, XmppStringprepException, XMPPException, InterruptedException, RepositoryException {
		String username = from.toString().split("@")[0];
		String html = pegDownProcessor.markdownToHtml(message.getBody());
		String subject = message.getSubject();
		if(subject == null) subject = username;
		
		String path = "/youchat/"+subject;
		if(!jcrService.nodeExsits(path)) {
   			Folder folder = new Folder();
   			folder.setName(subject);
   			folder.setPath(path);
   			jcrService.addOrUpdate(folder);
   		}	   		

		addChat(html,username,path);
	}
	
	private void addChat(String message, String username, String path) throws RepositoryException {
		    com.filemark.jcr.model.Chat chat = new com.filemark.jcr.model.Chat();
	   		if(!jcrService.nodeExsits(path)) {
	   			jcrService.addNodes(path, "nt:unstructured", username);
	   		}
	   		if(!jcrService.nodeExsits(path+"/"+username)) {
	   			User user = new User();
	   			user.setTitle(username);
	   			user.setLastModified(new Date());
	   			user.setPath(path+"/"+username);
	   			jcrService.addOrUpdate(user);
	   		}	   		
		    
		    chat.setFrom(username);

	   		Calendar calendar = Calendar.getInstance();
	   		chat.setLastModified(calendar);
	   		chat.setContent(message);
	   		chat.setTitle(username);
   	   		chat.setPath(path+"/"+username+"/"+getDateTime());
	   		//chat.setPath(path+"/"+username+"/"+calendar.getTime().getTime());
	   		chat.setCreatedBy(username);
	   		//chat.setTimer(timer);

	   		jcrService.addOrUpdate(chat);		
		
	}
	/**
	 * Configure a session, setting some action listeners...
	 * 
	 * @param connection
	 *            The connection to set up
	 */
	private void installConnectionListeners(final XMPPConnection connection) {
	    if (connection != null) {
	        connectionListener = new ConnectionListener() {


				@Override
				public void authenticated(XMPPConnection connection, boolean arg1) {
					log.info("Reconnect is Authencated:"+connection.isAuthenticated());
/*					ChatManager chatManager = ChatManager.getInstanceFor(connection);   
			        chatManager.addIncomingListener(new IncomingChatMessageListener() {
			            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {                
			            	processMessage(from, message,chat);
			            }
			        });		*/		
				}

				@Override
				public void connected(XMPPConnection connection) {
					log.info("XMPPConnection:"+connection +" connected");
					roster = Roster.getInstanceFor(connection);
					//installConnectionListeners(connection);
				}

				@Override
				public void connectionClosed() {
					log.info("XMPPConnection closed");
					//disconnect();
				}

				@Override
				public void connectionClosedOnError(Exception e) {
					log.error("connection close with error:"+e.getMessage());
					//disconnect();
					
				}
	        };
	        connection.addConnectionListener(connectionListener);
	    }
	}
	
	    public AbstractXMPPConnection getConnection() throws SmackException.NotConnectedException {
	        if (null == this.connection) {
	            log.warn("Could not get Xmpp Connection, connection was null.");
	            throw new SmackException.NotConnectedException();
	        } else if (!this.connection.isConnected()) {
	            try {
	                // reconnect if ran into timeout
	                this.connection.connect();
	            } catch (Exception e) {
	                this.connection = null;
	                log.error("Could not get Xmpp Connection, failed to reconnect.");
	                throw new SmackException.NotConnectedException();
	            }
	        }
	        return this.connection;
	    }
	    
	   private void checkConnection() {
		   TimerTask repeatedTask = new TimerTask() {
		        public void run() {
					try {
		                //PingManager pingManager = PingManager.getInstanceFor(getConnection());
		                if(!pingManager.pingMyServer()) {
		                	log.info("Ping false");
		                	
		                	if(!connection.isConnected()) {
		                		connection.connect();
		                	}
		                	if(!connection.isAuthenticated()) {
		                		log.info("login again");
		                		connection.login();
		                	}else {
		                		log.info("disconnect and login again");
		                		connection.disconnect();
		                		login("admin","admin");
		                	}

		                }
						//log.info("Task performed on " + new Date()+", isConnection:"+pingManager.pingMyServer());
					} catch (NotConnectedException e) {
						log.error(e.getMessage());
					} catch (InterruptedException e) {
						log.error(e.getMessage());
					} catch (SmackException e) {
						log.error(e.getMessage());
					} catch (IOException e) {
						log.error(e.getMessage());
					} catch (XMPPException e) {
						log.error(e.getMessage());
					}
		        }
		    };
		    Timer timer = new Timer("Timer");
		    long delay = 1000L;
		    long period = 60*1000L;		    
		    timer.schedule(repeatedTask, delay);		    
	   }
		protected String getDateTime() {
			Date now = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("yyMMddHHmmss");
			return sf.format(now);
			
			
		}
	   protected void finalize() throws Throwable {
	        try {
	            disconnect();
	        } finally {
	            super.finalize();
	        }
	    }


}
