package com.filemark.jcr.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jcr.RepositoryException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.filemark.jcr.model.Asset;
import com.filemark.jcr.model.Device;
import com.filemark.jcr.model.Folder;
import com.filemark.jcr.service.JcrIndexService;
import com.filemark.jcr.service.JcrServices;
import com.filemark.utils.WebPage;
import com.lowagie.text.pdf.PdfReader;

public class JcrIndexServiceImpl implements JcrIndexService {
	
	@Autowired
	private JcrServices jcrService;

	private int keepLog = 180;
	private String clusterId;
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	private static final Logger log = LoggerFactory.getLogger(JcrIndexServiceImpl.class);

	public void runScheduledQueue() {
		log.debug("Jcr runScheduledQueue at "+new Date());
		try {
			//backup2Device("/templates");
			//backup2Device("/home");
			Device2Backup();
		} catch (RepositoryException e1) {
			log.error("Device2Backup"+e1.getMessage());;
		}
		Date start = new Date();
		String assetsQuery = "select * from [nt:base] AS s WHERE ISDESCENDANTNODE([/]) and s.delete not like 'true' and s.ocm_classname='com.filemark.jcr.model.Asset' and s.contentType like 'image%' and s.updated not like 'true'  order by s.path";
		WebPage<Asset> assets = jcrService.searchAssets(assetsQuery, 500, 0);
		for(Asset asset:assets.getItems()) {
			Thread.yield();
			File icon = new File(jcrService.getDevice()+asset.getPath()+"/x400.jpg");
			if(!icon.exists()) {
				try {
					jcrService.autoRoateImage(asset.getPath());
					jcrService.createIcon(asset.getPath(), 400, 300);
					//jcrService.createFile(asset.getPath(),400);
				}catch(Exception r) {
					log.error(r.getMessage());
				}
			}
			try {
				jcrService.updatePropertyByPath(asset.getPath(), "updated", "true");
			} catch (RepositoryException e) {
				log.error(e.getMessage());
			}
		}
		Date end = new Date();
		if(assets.getItems().size()>0)
			log.debug("Jcr Asset "+assets.getItems().size()+" updated at "+(end.getTime() - start.getTime())/100000);

		String foldersQuery = "select * from [nt:base] AS s WHERE ISDESCENDANTNODE([/]) and s.delete not like 'true' and s.ocm_classname='com.filemark.jcr.model.Folder' and s.[changed] like 'true' order by s.path";
		WebPage<Folder> folders = jcrService.queryFolders(foldersQuery, 100, 0);
		for(Folder folder:folders.getItems()) {
			Thread.yield();
			try {
				jcrService.updateFolderIcon(folder.getPath());
			} catch (RepositoryException e) {
				log.error(e.getMessage());
			}
		}
		if(folders.getItems().size()>0)
			log.debug("Jcr Folder "+folders.getItems().size()+" updated at "+new Date());
		
		String pdfAssetsQuery = "select * from [nt:base] AS s WHERE ISDESCENDANTNODE([/]) and s.delete not like 'true' and s.ocm_classname='com.filemark.jcr.model.Asset' and s.contentType like 'application/pdf' and s.total is null  order by s.path";
		WebPage<Asset> pdfAssets = jcrService.searchAssets(pdfAssetsQuery, 500, 0);
		for(Asset asset:pdfAssets.getItems()) {
			Thread.yield();
			try {
				File file = new File(jcrService.getDevice()+asset.getPath()+"/origin.pdf");
				if(file.exists()) {
					InputStream in = new FileInputStream(file);
					PdfReader reader = new PdfReader(in);
					int total = reader.getNumberOfPages();
					jcrService.setProperty(asset.getPath(), "total", new Long(total));
				}
				jcrService.updatePropertyByPath(asset.getPath(), "updated", "true");
			} catch (RepositoryException e) {
				log.error(e.getMessage());
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		
		
	}
	
	private void Device2Backup() throws RepositoryException {
		File device = new File(jcrService.getDevice());
/*		if(!jcrService.nodeExsits("/system/devices/backup")) {
			Device backup = new Device(jcrService.getBackup());
			backup.setName("backup");
			backup.setTitle("backup");
			backup.setPath("/system/devices/backup");
			backup.setLocation(jcrService.getBackup());
			jcrService.addOrUpdate(backup);
		}
		if(!jcrService.nodeExsits("/system/devices/default")) {
			Device home = new Device(jcrService.getDevice());
			home.setName("backup");
			home.setTitle("backup");
			home.setPath("/system/devices/backup");
			home.setLocation(jcrService.getDevice());
			jcrService.addOrUpdate(home);			
		}*/
		Device home = (Device)jcrService.getObject("/system/devices/default");
		if(!jcrService.getDevice().equals(home.getLocation())) {
		    jcrService.updatePropertyByPath("/system/devices/default", "location", jcrService.getDevice());
		}
		int count =0;
		if(device.getUsableSpace()*100/device.getTotalSpace()<30) {
			String assetsQuery = "select * from [nt:base] AS s WHERE s.ocm_classname='com.filemark.jcr.model.Asset' and s.[path] not like '/templates/%' and s.[path] not like '%/icon' and s.[device] = '/system/devices/default'  order by s.lastModified, s.size desc";
			WebPage<Asset> assets = jcrService.searchAssets(assetsQuery, 100, 0);
			for(Asset asset:assets.getItems()) {
				String source = jcrService.getDevice()+asset.getPath();
				File srcDir = new File(source);
				if(!srcDir.isDirectory()) continue;
				String destination = jcrService.getBackup()+asset.getPath();;
				File destDir = new File(destination);
				try {
					if(destDir.exists()) {
						FileUtils.cleanDirectory(destDir);
						destDir.delete();
					}else if(!destDir.getParentFile().exists()) destDir.getParentFile().mkdirs();
					
				    FileUtils.moveDirectory(srcDir, destDir);
				    count++;
				    jcrService.updatePropertyByPath(asset.getPath(), "device", "/system/devices/backup");
				} catch (IOException e) {
				    log.error(e.getMessage());
				} catch (RepositoryException e) {
				    log.error(e.getMessage());
				}				
			}
			log.debug("Device2Backup move:"+count+"/"+assets.getPageCount());
		}
	}

	private void backup2Device(String path) throws RepositoryException {
			String assetsQuery = "select * from [nt:base] AS s WHERE ISDESCENDANTNODE(["+path+"]) and s.ocm_classname='com.filemark.jcr.model.Asset' and s.[device] = '/system/devices/backup'  order by s.lastModified";
			WebPage<Asset> assets = jcrService.searchAssets(assetsQuery, 100, 0);
			for(Asset asset:assets.getItems()) {
				String source = jcrService.getBackup()+asset.getPath();
				File srcDir = new File(source);
				if(!srcDir.isDirectory()) continue;
				String destination = jcrService.getDevice()+asset.getPath();;
				File destDir = new File(destination);
				try {
					if(destDir.exists()) {
						FileUtils.cleanDirectory(destDir);
					}else if(!destDir.getParentFile().exists()) destDir.getParentFile().mkdirs();

					FileUtils.moveDirectory(srcDir, destDir);
				    
				    jcrService.updatePropertyByPath(asset.getPath(), "device", "/system/devices/default");
				} catch (IOException e) {
				    log.error(e.getMessage());
				} catch (RepositoryException e) {
				    log.error(e.getMessage());
				}				
			}
			log.debug("backup2device move:"+assets.getItems().size()+"/"+assets.getPageCount());
		}


	public JcrServices getJcrService() {
		return jcrService;
	}




	public void setJcrService(JcrServices jcrService) {
		this.jcrService = jcrService;
	}




	public String getClusterId() {
		return clusterId;
	}


	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}
	

}
