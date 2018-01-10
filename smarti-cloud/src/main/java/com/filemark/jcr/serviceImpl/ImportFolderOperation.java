package com.filemark.jcr.serviceImpl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import javax.activation.MimetypesFileTypeMap;
import javax.jcr.RepositoryException;

import org.apache.commons.io.FileUtils;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.filemark.jcr.model.Asset;
import com.filemark.jcr.model.Device;
import com.filemark.jcr.model.Folder;
import com.filemark.jcr.service.AssetOperation;
import com.filemark.jcr.service.JcrServices;
import com.filemark.utils.WebPage;

public class ImportFolderOperation extends AssetOperation {
	private String folderPath;
	private String path;
	private String userName;
	private String override;
	private final Logger log = LoggerFactory.getLogger(JcrServicesImpl.class);
	public ImportFolderOperation(JcrServices jcrService, String folderPath,String path,String userName, String override) {
		super(jcrService);
		this.folderPath = folderPath;
		this.path = path;
		this.userName = userName;
		this.override = override;
	}

	@Override
	protected void doRun() {
		if(folderPath.startsWith("http")) {
			importUrl(folderPath,path);
		}else {
			File folder = new File(folderPath);
			importFolder(folder,path);			
		}


	}
	private void importUrl(String url, String path) {
		Asset asset = new Asset();
		InputStream is = null;
		try {
			URL url_img = new URL(url);
	        URLConnection uc = url_img.openConnection();
	        
	    	String contentType = uc.getContentType();
		    MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
		    
		    String ext="";
	
				MimeType mimeType = allTypes.forName(asset.getContentType());
			    ext = mimeType.getExtension(); 
	
			
			
			String urlPath = url_img.getPath();
			if(ext==null || "".equals(ext))
				ext = urlPath.replaceFirst("^.*/[^/]*(\\.[^\\./]*|)$", "$1");
			String nodeName = urlPath.substring(urlPath.lastIndexOf("/")+1, urlPath.length());
	
			String fileName = nodeName;
			fileName = nodeName.replaceAll(" ", "-");
			if(!fileName.matches("(\\w|\\.|\\-|\\s|_)+")) {
				fileName = ""+new Date().getTime()+"."+ext;
			}
			if(!fileName.endsWith(ext)) fileName +=ext;
			String assetPath =  path+"/"+fileName;
			if("true".equals(override) && jcrService.nodeExsits(path+"/"+fileName)) {
				asset = (Asset)jcrService.getObject(path+"/"+fileName);
			}else {
				assetPath = jcrService.getUniquePath(path, fileName);
			}
			if(contentType==null || "".equals(contentType))
				contentType = new MimetypesFileTypeMap().getContentType(fileName);
	    	is = uc.getInputStream();
	    	asset.setTitle(fileName);	
	    	asset.setName(nodeName);
			asset.setCreatedBy(userName);
			asset.setPath(assetPath);
			asset.setLastModified(new Date());
			asset.setContentType(contentType);
			asset.setDevice(this.getDevice().getPath());
			
			jcrService.addOrUpdate(asset);
			jcrService.updateCalendar(path,"lastModified");
			jcrService.setProperty(path, "changed", "true");	
			if(asset.getDevice()!=null) {
				Device device = (Device)jcrService.getObject(asset.getDevice());
				log.debug("Writing device "+device.getPath() +":"+device.getLocation());
				
				File file = new File(device.getLocation()+asset.getPath());
				if(!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}

				FileUtils.copyInputStreamToFile(is, file);
				is.close();
				Date end = new Date();
				asset.setSize(file.length());
				asset.setOriginalDate(new Date(file.lastModified()));
				jcrService.addOrUpdate(asset);

			}else {
				log.debug("Writing jcr");
				jcrService.addFile(assetPath,"original",is,contentType);
			}
		} catch (MimeTypeException e1) {
			log.error(e1.getMessage());
		} catch (MalformedURLException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage());
		}finally {
			if(is != null)
				try {
					is.close();
				} catch (IOException e) {
					log.error(e.getMessage());;
				}
		}
	}
	private void importFolder(File folder, String path) {

		for (File file : folder.listFiles()) {
			if(file.isFile()) {
				String fileName  = file.getName();
				Asset asset = new Asset();
				asset.setName(fileName);
				asset.setCreatedBy(userName);
				String assetPath = path+"/"+fileName;
				long lastModified = file.lastModified();

				asset.setPath(assetPath);
				InputStream is = null;
				try {
					if(jcrService.nodeExsits(assetPath)) {
						Asset old = (Asset)jcrService.getObject(assetPath);
						if(old.getOriginalDate() !=null && old.getOriginalDate().getTime() <= lastModified)
						continue;
					}
					is = new BufferedInputStream(new FileInputStream(file));
					String mimeType = URLConnection.guessContentTypeFromStream(is);
					asset.setContentType(mimeType);	
					asset.setOriginalDate(new Date(lastModified));
					asset.setLastModified(new Date());
					asset.setDevice(this.getDevice().getPath());
					jcrService.addOrUpdate(asset);
        			if(asset.getDevice()!=null) {
        				Device device = (Device)jcrService.getObject(asset.getDevice());
        				
        				File dfile = new File(device.getLocation()+asset.getPath());
        				if(!dfile.getParentFile().exists()) {
        					dfile.getParentFile().mkdirs();
        				}
        				OutputStream output = new FileOutputStream(dfile);

        				byte[] buffer = new byte[8 * 1024];
        				int byteToRead = 0;
        				while((byteToRead = is.read(buffer)) != -1) {
        					output.write(buffer,0,byteToRead);
        				}
        				is.close();
        				output.close();
        			}else {
        				jcrService.addFile(assetPath,"original",is,mimeType);
        			}

				} catch (FileNotFoundException e) {
					log.error(e.getMessage());
					continue;
				} catch (IOException e) {
					log.error(e.getLocalizedMessage());
					continue;
				} catch (RepositoryException e) {
					log.error(e.getLocalizedMessage());
					continue;
				}finally {
					if(is != null)
						try {
							is.close();
						} catch (IOException e) {
							log.error(e.getMessage());;
						}
				}

			}else if(file.isDirectory()) {
				Folder fd = new Folder();
				fd.setParent(path);
				try {
					String newPath = jcrService.getUniquePath(path, file.getName());
					fd.setPath(newPath);
					jcrService.addOrUpdate(fd);
					importFolder(file,newPath);

				} catch (RepositoryException e) {
					log.error(e.getLocalizedMessage());
				}				
			}
		}
	}
	
	private Device getDevice() {
		String deviceRoot = "/system/devices";
   		String deviceQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+deviceRoot+"]) and s.status not like 'disabled' and s.ocm_classname='com.filemark.jcr.model.Device' order by s.order";
		WebPage<Object> devices = jcrService.queryObject(deviceQuery, 20, 0);
		for(Object d:devices.getItems()) {
			Device dv = (Device)d;
			File f = new File(dv.getLocation());
			float used = f.getUsableSpace();
			if(used/f.getTotalSpace() > 0.9) {
				dv.setStatus("disabled");
				try {
					jcrService.addOrUpdate(dv);
				} catch (RepositoryException e) {
					log.error(e.getMessage());
				}
				continue;
			}else {
				return dv;
			}
		}
	return new Device();
}
}