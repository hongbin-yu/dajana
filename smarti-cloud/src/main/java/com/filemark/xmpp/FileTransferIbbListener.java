package com.filemark.xmpp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.jcr.RepositoryException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jxmpp.jid.Jid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.filemark.jcr.serviceImpl.XMPPServiceImpl;

public class FileTransferIbbListener implements FileTransferListener{
	private final Logger log = LoggerFactory.getLogger(FileTransferIbbListener.class);
    private XMPPServiceImpl xmppService;
    
    
	public FileTransferIbbListener(XMPPServiceImpl xmppService) {
		super();
		FileTransferNegotiator.IBB_ONLY = true;
		this.xmppService = xmppService;
	}


	@Override
	public void fileTransferRequest(FileTransferRequest request) {
        try{
        	String fileName = request.getFileName();
        	String description = request.getDescription();
            String contentType = request.getMimeType();
            if(contentType==null) {
            	MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
            	contentType = mimeTypesMap.getContentType(fileName);
            }
            	
            long size = request.getFileSize();
            Jid jid = request.getRequestor();	   
    		String requester = jid.toString().split("@")[0];
            IncomingFileTransfer ift = request.accept();
            
			InputStream is = ift.receiveFile();
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int nRead;
			byte[] buf = new byte[1024];
			while ((nRead = is.read(buf,  0, buf.length)) != -1) {
				os.write(buf, 0, nRead);
			}
			os.flush();
			log.info("Filetransfer receiving status: " + ift.getStatus()+" .size:"+os.size()+"/"+size  + ". Progress: " + ift.getProgress());

	        log.info("fileTransferRequest:"+fileName+"/"+contentType+"/"+requester+"/"+description);
	        InputStream inputStream = new ByteArrayInputStream(os.toByteArray()); 
	        xmppService.saveAsset(requester,inputStream,contentType,fileName,description,size);
        }
        catch(SmackException e){
            log.error(e.getMessage());
        }
        catch(IOException e){
            log.error(e.getMessage());	                    
        } catch (RepositoryException e) {
        	log.error(e.getMessage());
		} catch (InterruptedException e) {
			log.error(e.getMessage());	
		} catch (XMPPErrorException e) {
			log.error(e.getMessage());	
		}
    }


}
