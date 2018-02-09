package com.filemark.jcr.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.filemark.jcr.model.Asset;
import com.filemark.jcr.model.Folder;
import com.filemark.jcr.service.JcrIndexService;
import com.filemark.jcr.service.JcrServices;
import com.filemark.utils.WebPage;

public class JcrIndexServiceImpl implements JcrIndexService {
	
	@Autowired
	private JcrServices jcrService;

	private int keepLog = 180;
	private String clusterId;
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	private static final Logger log = LoggerFactory.getLogger(JcrIndexServiceImpl.class);

	public void runScheduledQueue() {
		log.debug("Jcr runScheduledQueue at "+new Date());
		Date start = new Date();
		String assetsQuery = "select * from [nt:base] AS s WHERE ISDESCENDANTNODE([/]) and s.delete not like 'true' and s.ocm_classname='com.filemark.jcr.model.Asset' and s.contentType like 'image%' and s.updated not like 'true'  order by s.path";
		WebPage<Asset> assets = jcrService.searchAssets(assetsQuery, 500, 0);
		for(Asset asset:assets.getItems()) {
			Thread.yield();
			if(!jcrService.nodeExsits(asset.getPath()+"/file-400")) {
				try {
					jcrService.autoRoateImage(asset.getPath());
					jcrService.createIcon(asset.getPath(), 400, 300);
					//jcrService.createFile(asset.getPath(),400);
				}catch(Exception r) {
/*					try {
						jcrService.deleteNode(asset.getPath());
						continue;
					} catch (RepositoryException e) {
						log.error(e.getMessage());
					}*/
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
		log.debug("Jcr Folder "+folders.getItems().size()+" updated at "+new Date());
		

		/*		@SuppressWarnings("unchecked")
		List<Object> objects = (List<Object>)jcrService.getObjects(Folder.root+"/eventgroup//", Eventgroupnames.class);
		for(Object object:objects) {
			Eventgroupnames group = (Eventgroupnames)object;
			String clusterId = group.getClusterId();
			if(this.clusterId!=null &&  this.clusterId.equals(clusterId)) {
				jcrService.addUsagelog(clusterId, group.getName() +" Jcr runScheduledQueue at "+new Date(), "Scheduled jobs");
				try {
					runSchedQueue(group);
				} catch (RepositoryException e) {
					log.error(e.getMessage());
					jcrService.addUsagelog("FMDBA", e.getMessage(), "error");
				}				
			}
		}*/
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
