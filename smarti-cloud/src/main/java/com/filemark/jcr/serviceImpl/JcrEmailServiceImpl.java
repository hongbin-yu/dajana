package com.filemark.jcr.serviceImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.inject.Inject;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.filemark.jcr.model.Asset;
import com.filemark.jcr.service.JcrIndexService;
import com.filemark.jcr.service.JcrServices;
import com.filemark.jcr.service.jcrEmailService;
import com.sun.mail.pop3.POP3SSLStore;

public class JcrEmailServiceImpl implements jcrEmailService {
	
		private String mailServer;
		private int pop3Port = 101;
		private static final Logger log = LoggerFactory.getLogger(JcrEmailServiceImpl.class);
		private String emailFolder = "INBOX";
	    Store store = null;
	    Folder folder = null;
	    Session session;
	    String lineSeperator = System.getProperty("line.separator");
		@Autowired
		private JcrServices jcrService;
		@Autowired
		private JcrIndexService jcrIndexService;	
	    @Inject
		private JavaMailSenderImpl mailSender;
	    @Inject
		private MessageSource messageSource;




	    

		@SuppressWarnings("unchecked")
		public synchronized void indexEmail() {
			log.info("Auto index email start.");		
	
			
		}
		

		public synchronized int indexJcrEmail() throws Exception {
			int count = 0;

			return count;
		}
		
		private synchronized int receive(String path,String user, String pass) throws IOException {

		    session = Session.getInstance(new Properties(), null);
		    int count = 0;
			try {
				
				store = session.getStore("pop3");
				store.connect(mailServer, user, pass);
			    // Get folder
			    folder = store.getFolder(emailFolder);
			    folder.open(Folder.READ_WRITE);

			    count = dumpJcr(path);
			    

			} catch (NoSuchProviderException e) {
				log.error("receive:NoSuchProviderException:"+e);
			} catch (MessagingException e) {
				log.error("receive:MessageException:"+e);
			} catch (Exception e) {
				log.error("receive email"+e);
			}finally {
				try {
				    if (folder != null)
				    	folder.close(true);
				    if (store != null)
				    	store.close();						
				} catch (MessagingException e1) {
					log.error("close:MessageException:"+e1);
				}				
			}
			return count;
		}	
		


		private synchronized int receiveSSL(String path,String username, String password) throws Exception {

			connect(username,password);
			openFolder(emailFolder);
			int count = dumpJcr(path);
			closeFolder();
			disconnect();
			return count;
		}

		
		private synchronized int receiveIMAP(String path,String username, String password) throws Exception {

			connectIMAP(username,password);
			openFolder(emailFolder);
			int count = dumpJcr(path);
			closeFolder();
			disconnect();
			return count;
		}
	    private void connect(String username, String password) throws Exception {
	        
	        String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	        
	        Properties pop3Props = new Properties();
	        
	        pop3Props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
	        pop3Props.setProperty("mail.pop3.socketFactory.fallback", "false");
	        pop3Props.setProperty("mail.pop3.port",  "995");
	        pop3Props.setProperty("mail.pop3.socketFactory.port", "995");
	        
	        URLName url = new URLName("pop3", mailServer, 995, "",
	                username, password);
	        
	        session = Session.getInstance(pop3Props, null);
	        store = new POP3SSLStore(session, url);
	        store.connect();
	        
	    }
	    
	    private void connectIMAP(String username, String password) throws Exception {
	        
	        String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	        
	        Properties imapProps = new Properties();
	        
	        imapProps.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
	        imapProps.setProperty("mail.imap.socketFactory.fallback", "false");
	        imapProps.setProperty("mail.imap.port",  "993");
	        imapProps.setProperty("mail.imap.socketFactory.port", "993");
	        

	        session = Session.getInstance(imapProps, null);
	        store = session.getStore("imaps");
	        store.connect(mailServer,username,password);
	        
	    }

	    
	    private void openFolder(String folderName) throws Exception {
	        
	        // Open the Folder
	        folder = store.getDefaultFolder();
	        
	        folder = folder.getFolder(folderName);
	        
	        if (folder == null) {
	            throw new Exception("Invalid folder");
	        }
	        
	        // try to open read/write and if that fails try read-only
	        try {
	            
	            folder.open(Folder.READ_WRITE);
	            
	        } catch (MessagingException ex) {
	            log.error("Read only:"+ex);
	            folder.open(Folder.READ_ONLY);
	            
	        }
	    }
	    
	    private void closeFolder() throws Exception {
	        folder.close(true);
	    }
	    
	    private int getMessageCount() throws Exception {
	        return folder.getMessageCount();
	    }
	    
	    private int getNewMessageCount() throws Exception {
	        return folder.getNewMessageCount();
	    }
	    
	    private void disconnect() throws Exception {
	        store.close();
	    }
	    private int dumpJcr(String path) throws Exception {

	    	Message message[] = folder.getMessages();
		    int count=folder.getNewMessageCount();
		    for (int i=0, n=message.length; i<n ; i++) {
		    	if (message[i] == null || message[i].getFlags().contains(Flags.Flag.DELETED))
		    		continue;
		    	
				String title = null;
		        Object content = message[i].getContent();
		        String subject = message[i].getSubject();
		        String from = ((InternetAddress)message[i].getFrom()[0]).getAddress();
		        String name	= ((InternetAddress)message[i].getFrom()[0]).getPersonal();

		        String to = ((InternetAddress)message[i].getAllRecipients()[0]).getAddress();
		        String note = getText(message[i]);

		        message[i].setFlag(Flags.Flag.DELETED, true);

		        int attach_count = 0;

		        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		        
			    

		        //Pages page = indexPage(coldReport,note,now);
		        if (content instanceof Multipart) {
		        	Multipart multipart = (Multipart)content;
			        try {
			  		  	for (int j=0, m=multipart.getCount(); j<m; j++) {
			  		  		Part part = multipart.getBodyPart(j);
			  		  		handlePart(part,path);
			  		  	}

			        }catch (Exception e) {
						log.error("receive:Exception:"+e);
			        	continue;
					}finally {
					
					}
			          if (attach_count == 0) {
				        	continue;
			          }else {
			          }
			        } else {
			        	
			        	continue;
			        	//handlePart((Part)content,address,note,writer, recordClass, from, index1);
			        }
			        String confirm[]={""+attach_count,path},warning="";
			        count +=attach_count;

		    }
		    
			
	    	return count;
	    }
	    

		  private Asset handlePart(Part part,String path) throws MessagingException, IOException {
				

				Asset asset = new Asset();  
				String disposition = part.getDisposition();
			    String contentType = part.getContentType();
			    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

			    if (disposition == null) { // When just body
			    	log.debug("just body: "  + contentType);
			      // Check if plain
			      if (part.isMimeType("multipart/alternative")) {
			          //part.writeTo(out);
			    	  log.debug("contentType "+ contentType);  
			      }else if(part.isMimeType("image/*")) {
			    	  log.debug("Image: " + contentType);
					String ext = "";
					if (part.getFileName().lastIndexOf(".")>0) {
						ext = part.getFileName().substring(part.getFileName().lastIndexOf(".")+1);
						ext = ext.toLowerCase();
					}
					File file = new File(path,"origin"+ext);
					InputStream in = part.getInputStream();
					OutputStream out = new FileOutputStream(file);
					IOUtils.copy(in, out);
			      }else if (part.isMimeType("multipart/*")) { 
			    	  log.debug("contentType "+ contentType); 
					    Multipart mp = (Multipart)part.getContent();
					    for (int i = 0; i < mp.getCount(); i++) {
					    	asset =handlePart(mp.getBodyPart(i),path);
					    }	    	  
			      } else { // Don't think this will happen
			    	  log.debug("Other body: " + contentType);
			          //part.writeTo(out);
			      }
			    } else if (disposition.equalsIgnoreCase(Part.ATTACHMENT)) {
			    	log.debug("Attachment: " + part.getFileName() + " : " + contentType);
					String ext = "";
					if (part.getFileName().lastIndexOf(".")>0) {
						ext = part.getFileName().substring(part.getFileName().lastIndexOf(".")+1);
						ext = ext.toLowerCase();
					}
					File file = new File(path,"origin"+ext);
					InputStream in = part.getInputStream();
					OutputStream out = new FileOutputStream(file);
					IOUtils.copy(in, out);
			    } else if (disposition.equalsIgnoreCase(Part.INLINE)) {
			    	log.debug("Inline: " + part.getFileName() + " : " + contentType);
					String ext = "";
					if (part.getFileName().lastIndexOf(".")>0) {
						ext = part.getFileName().substring(part.getFileName().lastIndexOf(".")+1);
						ext = ext.toLowerCase();
					}
					File file = new File(path,"origin"+ext);
					InputStream in = part.getInputStream();
					OutputStream out = new FileOutputStream(file);
					IOUtils.copy(in, out);
			    } else {  // Should never happen
			      log.debug("Other: " + disposition);
			    }
			    
			    return asset;
			  }


		  private String getText(Part p) throws MessagingException, IOException {
			if (p.isMimeType("text/plain")) {
			    String s = (String)p.getContent();
			    //textIsHtml = p.isMimeType("text/html");
			    return s;
			}
			
			if (p.isMimeType("multipart/alternative")) {
			    // prefer html text over plain text
			    Multipart mp = (Multipart)p.getContent();
			    String text = null;
			    for (int i = 0; i < mp.getCount(); i++) {
			        Part bp = mp.getBodyPart(i);
			        if (bp.isMimeType("text/plain")) {
			            if (text == null)
			                text = getText(bp);
			            continue;
			        } else if (bp.isMimeType("text/html")) {//igore html
			          //  String s = getText(bp);
			          //  if (s != null)
			          //      return s;
			        } else {
			            return getText(bp);
			        }
			    }
			    return text;
			} else if (p.isMimeType("multipart/*")) {
			    Multipart mp = (Multipart)p.getContent();
			    for (int i = 0; i < mp.getCount(); i++) {
			        String s = getText(mp.getBodyPart(i));
			        if (s != null)
			            return s;
			    }
			}
			
			return null;
			}
		    


		  private void sendMessage(String to, String subject, String message) {
			  try {

			    MimeMessage mimeMessage = mailSender.createMimeMessage();
			    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage); 
			    helper.setFrom(mailSender.getUsername());
				//mimeMessage.addRecipients(RecipientType.TO, to);
			    helper.setTo(to);
				helper.setSubject(subject);
				helper.setText(message);
				mailSender.send(mimeMessage);
			  } catch (MessagingException e) {
				  log.error("Message error:"+e);
			  } catch (MailSendException e) {
				  log.error("sendMessage error:"+e);
			  }
		  }

		public JavaMailSenderImpl getMailSender() {
			return mailSender;
		}

		public void setMailSender(JavaMailSenderImpl mailSender) {
			this.mailSender = mailSender;
		}

		public MessageSource getMessageSource() {
			return messageSource;
		}

		public void setMessageSource(MessageSource messageSource) {
			this.messageSource = messageSource;
		}

		public String getMailServer() {
			return mailServer;
		}

		public void setMailServer(String mailServer) {
			this.mailServer = mailServer;
		}

		public int getPop3Port() {
			return pop3Port;
		}

		public void setPop3Port(int po3Port) {
			this.pop3Port = po3Port;
		}

		public JcrServices getJcrService() {
			return jcrService;
		}

		public void setJcrService(JcrServices jcrService) {
			this.jcrService = jcrService;
		}

		public JcrIndexService getJcrIndexService() {
			return jcrIndexService;
		}

		public void setJcrIndexService(JcrIndexService jcrIndexService) {
			this.jcrIndexService = jcrIndexService;
		}


		
}
