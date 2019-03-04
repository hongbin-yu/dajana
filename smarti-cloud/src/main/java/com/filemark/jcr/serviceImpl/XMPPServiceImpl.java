package com.filemark.jcr.serviceImpl;



import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.activation.MimetypesFileTypeMap;
import javax.jcr.RepositoryException;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.JTextField;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.ReconnectionManager.ReconnectionPolicy;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.ExtensionElement;
//import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.xhtmlim.packet.XHTMLExtension;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.xml.splitter.XmlSplitter;
import org.pegdown.PegDownProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.tools.codec.Base64Decoder;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.filemark.jcr.model.Asset;
import com.filemark.jcr.model.Device;
import com.filemark.jcr.model.Folder;
import com.filemark.jcr.model.User;
import com.filemark.jcr.service.JcrServices;
import com.filemark.jcr.service.XMPPService;
import com.filemark.utils.ImageUtil;
import com.filemark.utils.WebPage;
import com.lowagie.text.pdf.PdfReader;


@SuppressWarnings("deprecation")
public class XMPPServiceImpl implements XMPPService{

	private final Logger log = LoggerFactory.getLogger(XMPPServiceImpl.class);
	//private static String host = "host ip";
	private static String filedomain = "tu.dajana.ca";	
	private static String fileport = "";		
	private static String domain = "dajana.ca";
	private static Long port = new Long(5222);
	private String username="tester";
	private String password="tester";

	@Autowired
	private JcrServices jcrService;
	private PingManager pingManager;
	private ReconnectionManager reconnectionManager;
	private FileTransferManager fileTransferManager ;
    private JTextField jid;	
    
	private AbstractXMPPConnection connection;
	private ConnectionListener connectionListener;
	private Roster roster;
	private Presence presence;
	private static boolean isConnected = false; 
	//private PingFailedListener pingFailedListener ;
	private static final PegDownProcessor pegDownProcessor = new PegDownProcessor();
			//Extensions.ALL | Extensions.SUPPRESS_ALL_HTML, 5000);
			
	//private IncomingChatMessageListener incomingChatMessageListener;
	public void initialize() {

		JSONParser parser = new JSONParser();
		try {
			Asset asset = (Asset)jcrService.getObject("/assets/home/json/yuhongyun.json");
			
			Device device = (Device)jcrService.getObject(asset.getDevice());
		
			String filePath = device.getLocation()+asset.getPath()+"/origin"+asset.getExt();
			Object obj = parser.parse(new FileReader(filePath));
			
			JSONObject jsonObject = (JSONObject) obj;
			
			domain = (String)jsonObject.get("domain");
			port = (Long)jsonObject.get("port");
			username = (String)jsonObject.get("username");
			password = (String)jsonObject.get("password");			
			filedomain = (String)jsonObject.get("filedomain");
			fileport = (String)jsonObject.get("fileport");
			
			log.info("login "+domain+":"+port+" by "+filedomain);
			login(username,password);				
			checkConnection();
		} catch (IOException | ParseException e) {
			log.error("init error:"+e.getMessage());
		} catch (RepositoryException e) {
			log.error("init error:"+e.getMessage());
		} catch(NullPointerException e) {
			log.error("init error:"+e.getMessage());		
		}


		

	}
	
	public void login(String username,String password) {
		
		try {
			XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration
			    .builder()
			    .setUsernameAndPassword(username, password)
			    .setResource(filedomain)
			    .setHost(domain)
			    .setXmppDomain(domain)
			    .setPort(port.intValue())
			    .setSendPresence(true)
			    .setCompressionEnabled(false)
			    .setSecurityMode(SecurityMode.required)
			    .build();
			    connection = new XMPPTCPConnection(conf);
				connection.connect().login();
				roster = Roster.getInstanceFor(connection);
				roster.setRosterLoadedAtLogin(true);
				roster = Roster.getInstanceFor(connection);
				if (!roster.isLoaded())
					  try {
					            roster.reloadAndWait();
					        } catch (SmackException.NotLoggedInException |        SmackException.NotConnectedException | InterruptedException e) {
					            e.printStackTrace();
					        }
				Collection<RosterEntry> entries = roster.getEntries();
/*				for (RosterEntry entry : entries) {
					//example: get presence, type, mode, status
					        Presence entryPresence = roster.getPresence(entry.getJid());

					        Presence.Type userType = entryPresence.getType();
					        Presence.Mode mode = entryPresence.getMode();
					        String status = entryPresence.getStatus();

					        log.info("####User status"+"...."+entry.getJid()+"....."+entryPresence.getStatus()+"....."+entryPresence +" \ntype: "+"\n"+userType + "\nmode: " +mode + "\nstatus: " + status);// + "\nType: " + status.getType());
					    }*/
				//roster.setRosterLoadedAtLogin(true);

				//presence = new Presence(connection.getUser(),Type.available);
				presence = roster.getPresence(connection.getUser().asBareJid());
			    //connection.setReplyTimeout(10000);
				presence.setType(Type.available);
				log.info("Status:"+presence.getStatus()+"/"+presence.getType()+"/"+presence);

				presence.setPriority(presence.getPriority()+1);
				connection.sendStanza(presence);
				connection.setReplyTimeout(60000);
				fileTransferManager = FileTransferManager.getInstanceFor(connection);
				//jingleManager = JingleManager.getInstanceFor(connection);
				reconnectionManager = ReconnectionManager.getInstanceFor(connection);
				ReconnectionManager.setEnabledPerDefault(true);
				reconnectionManager.enableAutomaticReconnection();
				reconnectionManager.setReconnectionPolicy(ReconnectionPolicy.RANDOM_INCREASING_DELAY);
				installConnectionListeners(connection);
	            installIncomingChatMessageListener(connection);
	            //checkConnection();
				//connection.isAuthenticated();
			    pingManager = PingManager.getInstanceFor(connection);
			    pingManager.setPingInterval(100);
			    pingManager.pingMyServer();	
			    pingManager.registerPingFailedListener(new PingFailedListener() {

					@Override
					public void pingFailed() {
						log.error("Ping failed:");
						disconnect();
						//checkConnection();
					}
		        	
		        });

			    //FileTransferNegotiator.getInstanceFor(connection)    
			    
			    fileTransferManager.addFileTransferListener(new FileTransferListener() {
			        @Override
			        public void fileTransferRequest(FileTransferRequest request) {
/*			        	ProviderManager.addIQProvider("query",

		                        "http://jabber.org/protocol/bytestreams",

		                        new BytestreamsProvider());

		 

		                ProviderManager.addIQProvider("query",

		                        "http://jabber.org/protocol/disco#items",

		                        new DiscoverItemsProvider());

		 

		                ProviderManager.addIQProvider("query",

		                        "http://jabber.org/protocol/disco#info",

		                        new DiscoverInfoProvider());			        	
		                FileTransferNegotiator fileTransferNegotiator = FileTransferNegotiator.getInstanceFor(connection);
		                try {
							fileTransferNegotiator.selectStreamNegotiator(request);
						} catch (NoStreamMethodsOfferedException
								| NoAcceptableTransferMechanisms
								| NotConnectedException | InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}*/
			            IncomingFileTransfer ift = request.accept();
			            long size = request.getFileSize();
			            String fileName = request.getFileName();
			            String contentType = request.getMimeType();

			            Jid jid = request.getRequestor();
			    		String username = jid.toString().split("@")[0];
			    		String filepath = "/assets/"+username+"/httpfileupload";
			            Asset ex_asset = findAsset(filepath,fileName,size);
			            if(ex_asset!=null) {
			            	ift.cancel();
			            	sendAsset(ex_asset,jid.asEntityBareJidIfPossible().toString());
			            	return;
			            }
			    		log.info("fileTransferRequestor:"+jid+",fileName="+fileName);
			            //byte[] dataReceived = new byte[1];
			            InputStream is = null;
						try {
//							if(contentType == null) {
								final File tempFile = File.createTempFile("temp-",fileName);
						        tempFile.deleteOnExit();
								ift.receiveFile(tempFile);
								log.info("Incoming file transfer: " + fileName);
								log.info("Transfer status is: " + ift.getStatus());
								if(ift.getStatus().toString().equals("Error")) {
									sendMessage("Transfer file error",jid.toString());
									ift.cancel();
									return;
								}
							    double progress = ift.getProgress();
							    double progressPercent = progress * 100.0;
							    Date start=new Date();
								while (!ift.isDone())
								{
								    Thread.sleep(1000);
									progress = ift.getProgress();
								    progressPercent = progress * 100.0;
								    String percComplete = String.format("%1$,.2f", progressPercent);
								    log.info("Transfer status is: " + ift.getStatus());
								    log.info("File transfer is " + percComplete + "% complete");

					                //Thread.sleep(2000L);

					                if (ift.getStatus().equals(FileTransfer.Status.cancelled)) {

					                	log.info("File transfer CANCELLED");

					                }

					 

					                if (ift.getStatus().equals(FileTransfer.Status.complete)) {

					                    log.info("File transfer COMPLETE："+(new Date().getTime() - start.getTime()));

					                }

					 

					                if (ift.getStatus().equals(FileTransfer.Status.error)) {

					                	log.info("File transfer ERROR");

					                    ift.cancel();
					                    sendMessage("ERROR",jid.toString());
					                    return;

					                }

					 

					                if (ift.getStatus().equals(FileTransfer.Status.in_progress)) {

					                	log.info("File transfer IN PROGRESS");

					                }

					 

					                if (ift.getStatus().equals(FileTransfer.Status.negotiating_transfer)) {

					                	log.info("File transfer IN NEGOTIATING");

					                }

					 

					                if (ift.getStatus().equals(FileTransfer.Status.initial)) {

					                	log.info("File now transfer INITIAL");

					                }

					 

					                if (ift.getStatus().equals(FileTransfer.Status.negotiating_stream)) {

					                	log.info("File transfer NEGOTIATING STREAM");

					                }

					 

					                if (ift.getStatus().equals(FileTransfer.Status.refused)) {

					                	log.info("File transfer REFUSED");

					                }
					                
					                if(new Date().getTime() - start.getTime() > 60000) {
					                    ift.cancel();	
										sendMessage("文件传输超时,请用以下网站上载文件： http://"+filedomain+fileport+"/site/assets.html",jid.asEntityBareJidIfPossible());
					                    return;
					                }
				                

								}
								is = new FileInputStream(tempFile);
						/*	}else {
				                is = ift.receiveFile();								
							}
							 is = ift.receiveFile();	
						*/
			           		if(!jcrService.nodeExsits("/assets/"+username)) {
			           			Folder folder = new Folder();
			           			folder.setTitle(username);
			           			folder.setDescription(jid.toString());
			           			folder.setName(username);
			           			folder.setPath("/assets/"+username);
			           			folder.setLastUpdated(new Date());
			           			folder.setLastModified(new Date());   			
			           			jcrService.addOrUpdate(folder);
			           		}
			                Asset asset = saveAsset(null,username,fileName,contentType,filepath,size,is);
							//sendMessage("http://"+filedomain+fileport+"/site/httpfileupload/"+asset.getUid()+"/"+asset.getName().replaceAll(" ", "+"),jid.toString());
			                is.close();
			                ShareFileForm shareFileForm = new ShareFileForm(DataForm.Type.form);
			                shareFileForm.addAsset(asset);
			        		Message msg = new Message();
			        		msg.setSubject(asset.getTitle());
			        		String url = "http://"+filedomain+fileport+"/protected/httpfileupload/"+asset.getUid()+"/"+asset.getName().toLowerCase();
			        		msg.setBody(url);	
			        		msg.addExtension(shareFileForm);
			    			msg.addExtension(new StandardExtensionElement("active","http://jabber.org/protocol/chatstates"));
			    			XHTMLExtension xhtmlExtension = new XHTMLExtension();
			    			xhtmlExtension.addBody("<body xmlns='http://www.w3.org/1999/xhtml'>"
			    				    +"<p style='font-weight:bold'>welcome:"+url+"</p>"
			    				    +"</body>");
			    			msg.addExtension(xhtmlExtension);
			    			if(asset.getContentType() != null && asset.getContentType().startsWith("image/")) {
			    				jcrService.autoRoateImage(asset.getPath());
			    				asset = (Asset)jcrService.getObject(asset.getPath());
			    				//jcrService.createIcon(assetPath, 400,400);
			    				//jcrService.createIcon(assetPath, 100,100);				
			    			}				                
			        		sendMessage(msg,jid.asEntityBareJidIfPossible());

			    			/*			                ByteArrayOutputStream os = new ByteArrayOutputStream();
			                int nRead;
			                byte[] buf = new byte[1024];
			                while ((nRead = is.read(buf,  0, buf.length)) != -1) {
			                    os.write(buf, 0, nRead);
			                }
			                os.flush();
			                dataReceived = os.toByteArray();*/
			            } catch (SmackException | IOException | XMPPErrorException e) {
							log.error("fileTransferManager"+e.getMessage());
			            } catch (InterruptedException e) {
							log.error("fileTransferManager"+e.getMessage());
						} catch (RepositoryException e) {
							log.error("fileTransferManager"+e.getMessage());
						} catch (XMPPException e) {
							log.error("fileTransferManager"+e.getMessage());
						}finally {
							if(is !=null)
								try {
									is.close();
								} catch (IOException e) {
									log.error("fileTransferManager"+e.getMessage());

								}
						}
			        }


			    });

			    isConnected = connection.isConnected();
				//InetAddress ipAddr = InetAddress.getLocalHost();
				//filedomain = ipAddr.getHostAddress();			    
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
		isConnected = false;
		log.info("remove connection:"+connection);
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
	
	public void sendMessage(Message message, String to) {
		ChatManager chatManager = ChatManager.getInstanceFor(connection);
		Chat chat;
		try {
			chat = chatManager.chatWith(JidCreate.entityBareFrom(to));
			chat.send(message);
		} catch (XmppStringprepException | InterruptedException e) {
			log.error(e.getMessage());
		} catch (NotConnectedException e) {
			log.error(e.getMessage());
			reconnect(message,to);
		} 

	}
	
	public void sendAsset(Asset asset, String to) {
        ShareFileForm shareFileForm = new ShareFileForm(DataForm.Type.form);
        shareFileForm.addAsset(asset);
		Message msg = new Message();
		msg.setSubject(asset.getTitle());
		String url = "http://"+filedomain+fileport+"/publish/httpfileupload/"+asset.getUid()+"/"+asset.getName().toLowerCase();
		asset.setUrl(url);
		msg.setBody(url);	
		msg.addExtension(shareFileForm);
		msg.addExtension(new StandardExtensionElement("active","http://jabber.org/protocol/chatstates"));

		try {
			log.info(url);
			jcrService.updatePropertyByPath(asset.getPath(), "status", "bullhorn");

			sendMessage(msg,to);
		} catch (RepositoryException e) {
			log.error(e.getMessage());;
		}			
	}

	private void sendDoc(Asset asset, EntityBareJid to) {
        ShareFileForm shareFileForm = new ShareFileForm(DataForm.Type.form);
        long n = getNumberOfPage(asset);
		Message msg = new Message();
		msg.setSubject(asset.getTitle());
		asset.setUrl( "http://"+filedomain+fileport+"/content/httpfileupload/"+asset.getUid()+"/"+asset.getName().toLowerCase());
		shareFileForm.addAsset(asset);
		if(n>0) {
			for (int i=0;i<n;i++) {
	        	Asset a = new Asset();
	        	a.setContentType("image/jpg");
	    		String url = "http://"+filedomain+fileport+"/publish/doc2pdf.jpj?path="+asset.getPath()+"&p="+i;
	    		msg.setBody(url);
	    		a.setUrl(url);        	
	            shareFileForm.addAsset(a);
	       	
	        }			
		}
		msg.addExtension(shareFileForm);

		try {
			sendMessage(msg,to);
			//log.info("pages:"+n+","+msg.toXML("x").toString());
		} catch (NotConnectedException | XmppStringprepException
				| XMPPException | InterruptedException e) {
			log.error(e.getMessage());;
		}			
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
        //log.info(message.toString());
        try {
            //sendMessage(message.getBody(),from);
        	String xmppid = from.toString();
        	String username = xmppid.split("@")[0];
        	if(jcrService.nodeExsits("/system/users/"+username)) {
        		User user = (User)jcrService.getObject("/system/users/"+username);
        		if(!xmppid.equals(user.getXmppid())) {
        			user.setXmppid(xmppid);
        			jcrService.addOrUpdate(user);
        		}
        	}
            String body = message.getBody();
    		//log.info(message.getType()+"/"+filedomain+"/ "+from+" 说: " + body);
            log.info(message.toXML("x").toString());
        	switch(parseType(body)) {
	        case 0:

	        	processSearch(from,message,chat);

	        	break;
	        case 1:
	        	processCommand(from,message,chat);
	        	sendMessage("收到",from);
	        	break;
	        case 2:
	        	processAssets(from,message,chat);

	        	break;
	        case 3:
	        	processSearch(from,message,chat);
	        	//sendMessage("结果:",from);
	        	break;
	        case 4:
	        	proccessAstraChat(from,message,chat);
	        	break;	        	
	        case 5:
	        	jcrService.updatePropertyByPath("/system/users/"+username, "status", "locked");
	        	sendMessage(username+" 账号被锁定。",from);
	        	sendVerifyCode(from.toString());
	        	break;	      	        	
	        case 100:
	        	sendVerifyCode(from.toString());

	        	break;	
	        case 101:
	        	sendVerifyCode(from.toString());

	        	break;		        	
	
	        }
	        
		} catch (NotConnectedException e1) {
			log.error(e1.getMessage());
			reconnect(message,from.toString());
		} catch (XmppStringprepException e) {
			log.error(e.getMessage());
		} catch (XMPPException e) {
			log.error(e.getMessage());;
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		
	}
	
	private void proccessAstraChat(EntityBareJid from, Message message, Chat chat) {
		String username = from.toString().split("@")[0];
		String path = "/assets/"+username+"/httpfileupload";
		try {
	
			XmlPullParser parser = PacketParserUtils.getParserFor(message.toXML("x").toString());
			boolean done = false;
			String name = parser.getName();
			//ExtensionElement	image_element = null;
			while(!done) {
				int eventType = parser.next();
				if(eventType == XmlPullParser.START_TAG) {
					
					if(parser.getName().equals("image")) {
						String contentType = parser.getAttributeValue(null, "type");
					    String ext=".jpeg";
						//log.info("contentType="+contentType);
					    if(contentType !=null) {
							try {
								MimeType mimeType = MimeTypes.getDefaultMimeTypes().forName(contentType);
							    ext = mimeType.getExtension(); 
							} catch (MimeTypeException e1) {
								log.error(e1.getMessage());
							}
					    }
						//log.info("ext="+ext);
					    String filename = getDateTime()+ext;
						String imageBinary = parser.nextText();
						//log.info("image="+imageBinary);
						byte data[] = DatatypeConverter.parseBase64Binary(imageBinary); 
						InputStream is = new ByteArrayInputStream(data);
						//log.info("path="+path);
						if(!jcrService.nodeExsits("/assets/"+username)) {
				   			Folder folder = new Folder();
				   			folder.setTitle(username);
				   			//folder.setDescription(jid.toString());
				   			folder.setName(username);
				   			folder.setPath("/assets/"+username);
				   			folder.setLastUpdated(new Date());
				   			folder.setLastModified(new Date());   			
				   			jcrService.addOrUpdate(folder);
				   		}	
						//log.info("filename="+filename);
						Asset asset = saveAsset(null,username,filename,contentType,path,0,is);
						is.close();
						Message msg = new Message();
						msg.setSubject(asset.getTitle());
						String url = "http://"+filedomain+fileport+"/publish/httpfileupload/"+asset.getUid()+"/"+asset.getName().toLowerCase();
						asset.setUrl(url);
						msg.setBody(url);
						ImageExtension extension = new ImageExtension(asset.getContentType(),imageBinary); 
						msg.addExtension(extension);
						sendMessage(msg,from);
						//sendAsset(asset,from);
					}
				}else if(eventType == XmlPullParser.END_TAG) {
					if(parser.getName().equals(name)) {
						done = true;
					}

				}
			}
        	//sendMessage("优云现在不支持Astrachat,请用以下网站上载文件： http://"+filedomain+fileport+"/site/assets.html",from);

			//log.info(PacketParserUtils.parseContent(parser).toString());
			//log.info(PacketParserUtils.parseElementText(parser).toString());
			
		} catch (XmlPullParserException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}/* catch (Exception e) {
			log.error(e.getMessage());
		}*/ catch (RepositoryException e) {
			log.error("RepositoryException:"+e.getMessage());
		} catch (NotConnectedException e) {
			log.error(e.getMessage());
		} catch (XMPPException e) {
			log.error(e.getMessage());
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		} 
	}
	
	private int parseType(String body) {
		//CommandLineParser parser = new DefautParser();
		if(body.startsWith("?OTRv23?")) return 101;
		if(body.equals("-c")) return 100;
		if(body.startsWith("-t")) return 1;
		if(body.indexOf("/httpfileupload/")>0) return 2;	
		if(body.endsWith("?") || body.endsWith("？")) return 3;
		if(body.indexOf("AstraChat")>=0) return 4;
		if(body.startsWith("-l") || body.startsWith("-L")) return 5;		
		return 0;
		
	}
	
	private void sendHtmlLink(EntityBareJid from, String url, Asset asset) throws NotConnectedException, XMPPException, InterruptedException, XmppStringprepException, RepositoryException {
		// User1 creates a message to send to user2
		String title = asset.getTitle();
		Message msg = new Message();
		msg.setSubject(title);
		msg.setBody(url);
		// Create a XHTMLExtension Package and add it to the message

		XHTMLExtension xhtmlExtension = new XHTMLExtension();

			xhtmlExtension.addBody("<html xmlns='http://jabber.org/protocol/xhtml-im'>"
					+"<body xmlns='http://www.w3.org/1999/xhtml'>"
				    +"<p style='font-weight:bold'>"+url+"</p>"
				    +"</body>"
				    +"</html>");

			ShareFileForm shareFileForm = new ShareFileForm(DataForm.Type.form);
			msg.addExtension(shareFileForm);
			msg.addExtension(new StandardExtensionElement("active","http://jabber.org/protocol/chatstates"));

	    sendMessage(msg,from);

	    //Thread.sleep(200);

	}

	private void sendHLink(EntityBareJid from, String url, Asset asset) throws NotConnectedException, XMPPException, InterruptedException, XmppStringprepException, RepositoryException {
		// User1 creates a message to send to user2
		String title = asset.getTitle();
		Message msg = new Message();
		msg.setSubject(title);
		msg.setBody(url);
		// Create a XHTMLExtension Package and add it to the message

		XHTMLExtension xhtmlExtension = new XHTMLExtension();

			xhtmlExtension.addBody("<html xmlns='http://jabber.org/protocol/xhtml-im'>"
					+"<body xmlns='http://www.w3.org/1999/xhtml'>"
				    +"<p style='font-weight:bold'>"+url+"</p>"
				    +"</body>"
				    +"</html>");

			ShareFileForm shareFileForm = new ShareFileForm(DataForm.Type.form);
			msg.addExtension(shareFileForm);
			msg.addExtension(new StandardExtensionElement("active","http://jabber.org/protocol/chatstates"));

	    sendMessage(msg,from);

	    //Thread.sleep(200);

	}	
	
	private void sendBinaryImage(EntityBareJid from, String url, Asset asset) throws NotConnectedException, XMPPException, InterruptedException, RepositoryException, IOException {
		// User1 creates a message to send to user2
		String title = asset.getTitle();
		Message msg = new Message();
		msg.setSubject(title);
		msg.setBody(url);
		File origin = new File(asset.getFilePath()+"/origin"+asset.getExt());
        FileInputStream fileInputStreamReader = new FileInputStream(origin);
        byte[] bytes = new byte[(int)origin.length()];
        fileInputStreamReader.read(bytes);
        fileInputStreamReader.close();
        String encodedfile = new String(Base64.encodeBase64(bytes), "UTF-8");
		ImageExtension imageExtendsion = new ImageExtension(asset.getContentType(),encodedfile);
		msg.addExtension(imageExtendsion);
	    sendMessage(msg,from);

	}
	
	@Override
	public void sendVerifyCode(String from) throws RepositoryException, NotConnectedException, XmppStringprepException, XMPPException, InterruptedException {
		String username = from.split("@")[0];
		if(jcrService.nodeExsits("/system/users/"+username)) {
			User dbuser  = (User)jcrService.getObject("/system/users/"+username);
			Date now = new Date();
			//if(dbuser.getLastVerified()==null || now.getTime() - dbuser.getLastVerified().getTime() > 120000) {
				Random random = new Random();
				int code = random.nextInt(899999)+100000;
				dbuser.setCode(""+code);
				dbuser.setLastVerified(now);
				jcrService.addOrUpdate(dbuser);	
			//}
			Asset asset = new Asset();
			asset.setTitle("\""+username+"\"优云验证码："+dbuser.getCode() +"两分钟内有效");
			asset.setContentType("text/html");
			asset.setUrl("https://dajana.ca");
			asset.setSize(new Long(1000));
			ShareFileForm shareFileForm = new ShareFileForm(DataForm.Type.form);
			shareFileForm.addAsset(asset);
			Message msg = new Message();
			msg.setBody(asset.getTitle());
			
			msg.addExtension(shareFileForm);
			//sendMessage(msg,from);
			sendMessage("\""+username+"\"优云验证码："+dbuser.getCode() +"两分钟内有效 : http://"+filedomain+fileport+"/forget?j_username="+username,from);			
		}else {
			sendMessage("\""+username+"\"不在系统中！",from);	
		}

	}
	
	private void processAssets(EntityBareJid from, Message message, Chat chat) throws NotConnectedException, XmppStringprepException, XMPPException, InterruptedException, RepositoryException, UnsupportedEncodingException {
		String fileupload[] = message.getBody().split("\n");
		String username = from.toString().split("@")[0];
		String filepath = "/assets/"+username+"/httpfileupload";
		String html ="<section class=\"wb-lbx lbx-hide-gal\"><ul class=\"list-inline\">";
   		if(!jcrService.nodeExsits("/assets/"+username)) {
   			Folder folder = new Folder();
   			folder.setTitle(username);
   			folder.setDescription(jid.toString());
   			folder.setName(username);
   			folder.setPath("/assets/"+username);
   			folder.setLastUpdated(new Date());
   			folder.setLastModified(new Date());   			
   			jcrService.addOrUpdate(folder);
   		}
        ShareFileForm shareFileForm = new ShareFileForm(DataForm.Type.form);
		Message msg = new Message();

		for(String url:fileupload) {
			try {
		
				Asset asset = importAsset(url,username.toString(),filepath);
				jcrService.updatePropertyByPath(asset.getPath(), "status", "bullhorn");

		        if(asset.getExt().endsWith(".doc") || asset.getExt().endsWith(".docx"))  {
		        	sendDoc(asset,from);
		        	continue;
		        }
				shareFileForm.addAsset(asset);
				String link = "http://"+filedomain+fileport+"/publish/httpfileupload/"+asset.getUid()+"/"+asset.getName();
				asset.setUrl(link);
				msg.setBody(link);	
				//msg.setSubject(asset.getTitle());
		        String httpfileupload = "/publish/httpfileupload/"+asset.getUid()+"/"+asset.getName().toLowerCase();
				//log.info(httpfileupload);
				html +="<li><a href=\""+httpfileupload+"\" title=\"\"><img src=\""+httpfileupload+"?w=4\" class=\"img-resposive\" alt=\"\"></li>";
			//sendMessage("https://"+filedomain+fileport+httpfileupload,from);
				//sendHtmlLink(from,"http://"+filedomain+fileport+httpfileupload,asset);
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		if(shareFileForm.getSize()==0) return;
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
		msg.addExtension(shareFileForm);
		//msg.addExtension(new StandardExtensionElement("active","http://jabber.org/protocol/chatstates"));
		log.info(msg.toXML("x").toString());
		sendMessage(msg,from);
	}
	
	private Asset importAsset(String url, String username, String path) throws RepositoryException, IOException {
		
		Asset asset= new Asset();
		String nodeName = url.substring(url.lastIndexOf("/")+1, url.length());
		//nodeName = URLDecoder.decode(nodeName, "UTF-8");
			if(!jcrService.nodeExsits(path)) {
				jcrService.addNodes(path, "nt:unstructured",username);		
			}			
	        URL url_img = new URL(getUTF8(url).replace(" ", "+"));
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
	    	log.info("Status:"+status +",contentType:"+conn.getContentType());
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
	    	Long size = new Long(conn.getContentLength());
	    	Asset ex_asset = findAsset(path,nodeName,size);
	    	if(ex_asset!=null) {
	    		log.info("File exists:"+nodeName);
	    		return ex_asset;
	    	}
	    	InputStream is = conn.getInputStream();

	    	asset = saveAsset(url,username,nodeName,contentType,path,size,is);
	    	is.close();
			if(contentType != null && contentType.startsWith("image/")) {
				jcrService.autoRoateImage(asset.getPath());
				asset = (Asset)jcrService.getObject(asset.getPath());
				//jcrService.createIcon(assetPath, 400,400);
				//jcrService.createIcon(assetPath, 100,100);				
			}		    	
/*		}catch (Exception e){
			log.error("error:"+e.getMessage());

		}*/

		return asset;//"/protected/httpfileupload/"+asset.getUid()+"/"+nodeName;
	}
	
	private Asset saveAsset(String url ,String username,String fileName,String contentType,String path,long size,InputStream is) throws RepositoryException, IOException {
		Asset asset= new Asset();
		String nodeName = fileName;
	    MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
   		if(!jcrService.nodeExsits(path)) {
   			Folder folder = new Folder();
   			folder.setTitle("云存储");
   			folder.setName("httpfileupload");
   			folder.setPath(path);
   			folder.setLastUpdated(new Date());
   			folder.setLastModified(new Date());   			
   			jcrService.addOrUpdate(folder);
   		}
   		
	    String ext="";
	    if(contentType !=null)
		try {
			MimeType mimeType = allTypes.forName(contentType);
		    ext = mimeType.getExtension(); 
		} catch (MimeTypeException e1) {
			log.error(e1.getMessage());
		}
    	String devicePath = "/system/devices/default";//jcrService.getDevice();
		if(contentType != null && contentType.startsWith("video/")) {
			devicePath = "/system/devices/backup";//jcrService.getBackup();
		}
		if(ext==null || "".equals(ext))
			ext = fileName.substring(fileName.lastIndexOf("."), fileName.length());
		fileName = nodeName.toLowerCase();
		fileName = nodeName.replaceAll(" ", "-");
		if(!fileName.matches("(\\w|\\.|\\-|\\s|_)+")) {
			fileName = ""+getDateTime()+ext;
		}
		if(!fileName.endsWith(ext)) fileName +=ext;
		String assetPath =  path+"/"+fileName;
		Asset ex_asset = findAsset(path,nodeName,size);
		if(ex_asset!=null) {
			log.info("File exists:"+nodeName);
			return ex_asset;//"/protected/httpfileupload/"+asset.getUid()+"/"+fileName;
		}else {
			assetPath = jcrService.getUniquePath(path, fileName);
		}
		if(contentType==null || "".equals(contentType) || "application/x-ole-storage".equals(contentType))
			contentType = new MimetypesFileTypeMap().getContentType(nodeName);
		if(ext.equals(".xls") || ext.equals(".xlsx")) contentType= "application/vnd.ms-excel";
		else if(ext.equals(".doc") || ext.equals(".docx")) contentType= "application/msword";
    	asset.setTitle(nodeName);	
    	asset.setName(nodeName);
		asset.setCreatedBy(username);
		asset.setPath(assetPath);
		asset.setLastModified(new Date());
		asset.setContentType(contentType);
		asset.setDevice(devicePath);	
		asset.setExt(ext);
		asset.setUrl(url);

		if(asset.getDevice()!=null) {
			Device device = (Device)jcrService.getObject(asset.getDevice());
			log.debug("Writing device "+device.getPath() +":"+device.getLocation());
			
			File folder = new File(device.getLocation()+asset.getPath());
			File file = new File(device.getLocation()+asset.getPath()+"/origin"+ext);
			if(!folder.exists()) folder.mkdirs();
	
			//FileUtils.copyURLToFile(url_img, file);
			FileUtils.copyInputStreamToFile(is, file);
			is.close();
			//Date end = new Date();
			//long speed = file.length()*8/(end.getTime() - start.getTime()); 
			asset.setFilePath(device.getLocation()+asset.getPath());
			asset.setSize(file.length());
			asset.setOriginalDate(new Date(file.lastModified()));
			jcrService.addOrUpdate(asset);
			jcrService.updateCalendar(path,"lastModified");
			jcrService.setProperty(path, "changed", "true");
			//asset.setTitle(asset.getTitle() +" - "+speed+"kb/s");
			//output.close();
			try {
	   			if("application/vnd.ms-powerpoint".equals(contentType) || contentType.startsWith("application/vnd.ms-excel") || contentType.startsWith("application/vnd.openformats-officedocument")  || contentType.startsWith("application/vnd.openxmlformats-officedocument")) {
	  				 log.debug("xls2pdf:"+file.getAbsolutePath());
					 ImageUtil.xls2pdf(file.getAbsolutePath(), file.getParentFile().getAbsolutePath());
	
	  			}else if("application/vnd.ms-powerpoint".equals(contentType) || "application/vnd.ms-word".equals(contentType) || "application/vnd.ms-excel".equals(contentType) || "application/msword".equals(contentType) || assetPath.endsWith(".doc") || assetPath.endsWith(".docx")) {	
	  				 log.debug("doc2pdf:"+file.getAbsolutePath());
					 ImageUtil.doc2pdf(file.getAbsolutePath(), file.getParentFile().getAbsolutePath());
				}
       			if(contentType!=null && contentType.startsWith("video/")) {	
      				 log.debug("video2mp4:"+file.getAbsolutePath());
      				 Folder currentFolder = jcrService.getFolder(path);
      				 String resolution = "540x360";
      				 if(currentFolder.getResolution()!=null) {
      					 resolution = currentFolder.getResolution();
      				 }
      				 if("1080x720".equals(resolution) && contentType.equals("video/mp4")) {
      					 asset.setWidth(1080l);
      					 asset.setHeight(720l);		           					 
      				 }else if("720x540".equals(resolution)) {
      					 asset.setWidth(720l);
      					 asset.setHeight(540l);
      				 }else if("540x360".equals(resolution)) {
      					 asset.setWidth(540l);
      					 asset.setHeight(360l);	           					 
      				 }else {
      					 asset.setWidth(360l);
      					 asset.setHeight(280l);		           					 
      				 }
      				jcrService.addOrUpdate(asset);
      				if(!"1080x720".equals(resolution) && !contentType.equals("video/mp4"))
      					ImageUtil.video2mp4(file.getAbsolutePath(),resolution);
   			} 	   			
			} catch(IOException | InterruptedException e) {
   				log.error(e.toString());
   			}
		}else {
			log.debug("Writing jcr");
	    	jcrService.addFile(assetPath,"original",is,contentType);
			//is.close();
		}

		return asset;
	}
	
	private void processCommand(EntityBareJid from, Message message, Chat chat) {
		
	}	
	
	private void processSearch(EntityBareJid from, Message message, Chat chat) throws NotConnectedException, XMPPException, InterruptedException, RepositoryException, IOException {
		String resource = message.getFrom().toString();
		String query = message.getBody().replace("?", "").replace("？", "");
		String keywords =" and contains(s.*,'"+ query+"')";
		String username = from.toString().split("@")[0];
		String ISDESCENDANTNODE = "ISDESCENDANTNODE";
		String orderby = "[lastModified] desc";
		String path = "/assets/"+username+"/httpfileupload";
		String assetsQuery = "select s.* from [nt:base] AS s INNER JOIN [nt:base] AS f ON ISCHILDNODE(s, f) WHERE "+ISDESCENDANTNODE+"(s,["+path+"])" +keywords+" and s.[delete] not like 'true' and s.ocm_classname='com.filemark.jcr.model.Asset' order by s."+orderby+", s.[name]";
		WebPage<Asset> assets = jcrService.searchAssets(assetsQuery, 12, 0);
		ShareFileForm shareFileForm = new ShareFileForm(DataForm.Type.form);
		log.info("resource="+resource);
		for(Asset asset:assets.getItems()) {
			jcrService.updatePropertyByPath(asset.getPath(), "status", "bullhorn");

			if(asset.getContentType().startsWith("image/")) {
				if(resource.indexOf("AstraChat")>0) {
					String url = "http://"+filedomain+fileport+"/publish/httpfileupload/"+asset.getUid()+"/"+asset.getName().toLowerCase();
					sendBinaryImage(from, url,asset);
				}else if (resource.startsWith("Spark")) {
					sendAsset(asset,from.toString());					
				}else {
					shareFileForm.addAsset(asset);

				}
			}else {
				ShareFileForm fileForm = new ShareFileForm(DataForm.Type.form);
				fileForm.addAsset(asset);
				Message msg = new Message();
				msg.setBody(asset.getTitle());
				msg.addExtension(fileForm);
				sendMessage(msg,from);
			}
		}
		//shareFileForm.addAssets(assets.getItems());
		Message msg = new Message();
		msg.setBody(query+ " : "+assets.getItems().size()+"/"+assets.getPageCount());
		msg.addExtension(shareFileForm);
/*		XHTMLExtension xhtmlExtension = new XHTMLExtension();
		xhtmlExtension.addBody("<body xmlns='http://www.w3.org/1999/xhtml'>"
			    +"<p style='font-weight:bold'>"+query+"</p>"
			    +"</body>");
		msg.addExtension(xhtmlExtension);*/
		//String html = pegDownProcessor.markdownToHtml(message.getBody());
		//log.info(msg.toXML("x").toString());
		sendMessage(msg,from);
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
	
	private void sendFile(EntityBareJid from,Asset asset, Chat chat ) throws InterruptedException, FileNotFoundException {
		    OutgoingFileTransfer oft = fileTransferManager.createOutgoingFileTransfer(from.asEntityFullJidIfPossible());
		    oft.sendStream(new FileInputStream(asset.getFilePath()), asset.getTitle(), asset.getSize(), asset.getTitle());
		    outerloop: while (!oft.isDone()) {
		        switch (oft.getStatus()) {
		        case error:
		            System.out.println("Filetransfer error: " + oft.getError());
		            break outerloop;
		        default:
		            System.out.println("Filetransfer status: " + oft.getStatus() + ". Progress: " + oft.getProgress());
		            break;
		        }
		        Thread.sleep(1000);
		    }

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
   	   		chat.setPath(path+"/"+getDateTime());
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
					isConnected = true;
					//roster = Roster.getInstanceFor(connection);
					//installConnectionListeners(connection);
				}

				@Override
				public void connectionClosed() {
					log.info("XMPPConnection closed");
					isConnected = false;
					//disconnect();
				}

				@Override
				public void connectionClosedOnError(Exception e) {
					log.error("connection close with error:"+e.getMessage());
					isConnected = false;
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
		public long getNumberOfPage(Asset asset) {
			long numberOfPage = 0;
			if(asset.getTotal()!=null) return asset.getTotal();
			try {
				File file = jcrService.getFile(asset.getPath());
				File pdf = new File(file,"origin.pdf");
				if(pdf.exists()) {
					PdfReader reader = new PdfReader(pdf.getAbsolutePath());
					reader.close();
					jcrService.setProperty(asset.getPath(), "total", (long)reader.getNumberOfPages());
					numberOfPage = reader.getNumberOfPages();				
				}
			} catch (RepositoryException e) {
				log.error(e.getMessage());
			} catch (IOException e) {
				log.error(e.getMessage());
			}
			return numberOfPage;
		}	  
		
	   private void reconnect(Message message,String to) {
		   disconnect();
		   login(username,password);
		   sendMessage(message,to);
	   }
	   
	   private void checkConnection() {
		   TimerTask repeatedTask = new TimerTask() {
		        public void run() {
					try {
						
		                //PingManager pingManager = PingManager.getInstanceFor(getConnection());
		                if(isConnected && !pingManager.pingMyServer()) {
		                	disconnect();
		                }else if(!isConnected) {
		                	login(username,password);
		                }else {
		                	//presence.setType(Type.available);
		                	//connection.sendStanza(presence);
		                	//Calendar calendar = Calendar.getInstance();
		                	//calendar.setTimeInMillis(connection.getLastStanzaReceived());
		                	//log.info("Last Stenza:"+calendar.getTime());
		                }
						//log.info("Task performed on " + new Date()+", isConnection:"+pingManager.pingMyServer());
					} catch (NotConnectedException e) {
						log.error("checkConnection:"+e.getMessage());
						disconnect();
					} catch (InterruptedException e) {
						log.error("checkConnection:"+e.getMessage());
					}
		        }
		    };
		    Timer timer = new Timer("Timer");
		    long delay = 1000L;
		    long period = 60*1000L;		    
		    timer.schedule(repeatedTask, delay, period);		    
	   }
		protected String getDateTime() {
			Date now = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd.HHmmss");
			return sf.format(now);
			
			
		}
		private String getUTF8(String message) throws UnsupportedEncodingException {
			return new String(message.getBytes("UTF-8"),"ISO-8859-1"); 
		}
		/*
	   protected void finalize() throws Throwable {
	        try {
	            disconnect();
	        } finally {
	            super.finalize();
	        }
	    }
*/

		public static String getFiledomain() {
			return filedomain;
		}

		public static void setFiledomain(String filedomain) {
			XMPPServiceImpl.filedomain = filedomain;
		}

		public static String getFileport() {
			return fileport;
		}

		public static void setFileport(String fileport) {
			XMPPServiceImpl.fileport = fileport;
		}

		public static String getDomain() {
			return domain;
		}

		public static void setDomain(String domain) {
			XMPPServiceImpl.domain = domain;
		}

		public static Long getPort() {
			return port;
		}

		public static void setPort(int port) {
			XMPPServiceImpl.port = new Long(port);
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
		
		
		public Roster getRoster() {
		  try {
	            roster.reloadAndWait();
	        } catch (SmackException.NotLoggedInException |        SmackException.NotConnectedException | InterruptedException e) {
	            e.printStackTrace();
	        }			
			return roster;
		}

		public Collection<RosterEntry> getEntries() {
			  try {
		            roster.reloadAndWait();
		        } catch (SmackException.NotLoggedInException |        SmackException.NotConnectedException | InterruptedException e) {
		            e.printStackTrace();
		        }

				return roster.getEntries();
			}	
		
		public Collection<Presence> getPresences() {
			Collection<Presence> presences = new ArrayList<Presence>();
			  if(!roster.isLoaded()) 	
			  try {
		            roster.reloadAndWait();
		        } catch (SmackException.NotLoggedInException | SmackException.NotConnectedException | InterruptedException e) {
		            e.printStackTrace();
		        }
			   for (RosterEntry entry : roster.getEntries()) {
				   //if(entry.isApproved()) {
				        Presence entryPresence = roster.getPresence(entry.getJid());
				        presences.add(entryPresence);					   
				   //}

			   }
				return presences;
			}	
		


		private Asset findAsset(String path,String title,long size) {
			Asset asset = null;
			String ISDESCENDANTNODE = "ISCHILDNODE";
			String orderby = "[lastModified] desc";
			String assetsQuery = "select s.* from [nt:base] AS s INNER JOIN [nt:base] AS f ON ISCHILDNODE(s, f) WHERE "+ISDESCENDANTNODE+"(s,["+path+"]) and s.[name] like '"+title +"' and s.size = "+size+" and s.[delete] not like 'true' and s.ocm_classname='com.filemark.jcr.model.Asset' order by s."+orderby+", s.[name]";
			WebPage<Asset> assets = jcrService.searchAssets(assetsQuery, 1, 0);
			if(assets.getPageCount()>0) {
				asset = assets.getItems().get(0);
			}
			return asset;
		}
		public class ShareFileForm extends DataForm {
			private ArrayList<Asset> assets = new ArrayList<Asset>();
			
		    public ShareFileForm(Type type) {
				super(type);
			}

			@Override
			public XmlStringBuilder toXML(String arg0) {
		        XmlStringBuilder xml = new XmlStringBuilder(this);
		        xml.append(" type=\"form\">");
		        for(int i=0; i<assets.size();i++) {
		        	Asset asset = assets.get(i);
					String url = "http://"+filedomain+fileport+"/publish/"+(asset.getContentType().startsWith("image/")?"httpfileupload":"download")+"/"+asset.getUid()+"/"+asset.getName().toLowerCase();
			        xml.append("<field label=\""+asset.getTitle()+"\" var=\"media"+i+"\">");
			        xml.append("<media xmlns=\"urn:xmpp:media-element\" height=\""+(asset.getHeight()==null?0:asset.getHeight())+"\" width=\""+(asset.getWidth()==null?0:asset.getWidth())+"\">");
			        xml.append("<uri type=\""+asset.getContentType()+"\" size=\""+asset.getSize()+"\" duration=\"0\">");
			        xml.append(url+"</uri></media></field>");
		        }
		        xml.closeElement("x");
		        return xml;
			}



			public void addAsset(Asset asset) {
				assets.add(asset);
			}
			public void addAssets(List<Asset> assets) {
				for(Asset asset:assets) {
					assets.add(asset);
				}
				//this.assets.addAll(assets);
			}
			public int getSize() {
				return assets.size();
			}
		}
		
		public class ImageExtension implements ExtensionElement {
			private String contentType;
			private String imageString;
			public ImageExtension(String contentType,String imageString) {
				this.contentType = contentType;
				this.imageString = imageString;
			}
			@Override
			public String getElementName() {
				
				return "image";
			}

			@Override
			public CharSequence toXML(String arg0) {
				XmlStringBuilder xml = new XmlStringBuilder(this);
		        xml.append("xmlns:stream='http://etherx.jabber.org/streams' type='"+contentType+"'>");				
		        xml.append(imageString);
		        xml.closeElement("image");
		        return xml;
			}

			@Override
			public String getNamespace() {
				return "http://mangga.me/protocol/image";
			}
			
		}

}
