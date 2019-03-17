package com.filemark.jcr.serviceImpl;

import java.awt.Graphics2D;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import javax.jcr.version.VersionException;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JPanel;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.core.query.QueryImpl;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.query.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.extensions.jcr.JcrCallback;
import org.springframework.extensions.jcr.JcrTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.filemark.jcr.model.Asset;
import com.filemark.jcr.model.Chat;
import com.filemark.jcr.model.Folder;
import com.filemark.jcr.model.JcrNode;
import com.filemark.jcr.model.Log;
import com.filemark.jcr.model.Page;
import com.filemark.jcr.model.SmartiNode;
import com.filemark.jcr.service.JcrServices;
import com.filemark.utils.LinuxUtil;
import com.filemark.utils.ScanUploadForm;
import com.filemark.utils.WebPage;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.codec.TiffImage;

public class JcrServicesImpl implements JcrServices {

	
	private JcrTemplate jcrTemplate;
	private Mapper mapper;
	private final Logger log = LoggerFactory.getLogger(JcrServicesImpl.class);
	private int keepLog = 180;
	private String domain="dajana.cn";
	private String device=null;	
	private String backup=null;		
	private String cache=null;
    private String asianFont;
    private String home;
    private String workingDir;
   
    
	@Override
	public void init() {

			try {
				if(!nodeExsits("/system/users"))
					addNodes("/system/users", "nt:unstructured", "sysuser");
				if(!nodeExsits("/system/devices")) {
					addNodes("/system/devices", "nt:unstructured", "sysuser");
				}
			} catch (RepositoryException e) {
				
				log.error(e.getMessage());
			}
		
		
	}


	@SuppressWarnings("unchecked")
	public WebPage<Asset> searchAssets(final String queryString, final long limit,final long offset) {
		return (WebPage<Asset>) jcrTemplate.execute(new JcrCallback() { 
        	public WebPage<Asset> doInJcr(Session session) throws RepositoryException { 
        		ObjectContentManager ocm = new  ObjectContentManagerImpl(session, mapper);
        		QueryManager queryManager = session.getWorkspace().getQueryManager();
        		
        		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
        		
        		QueryResult result = q.execute();

        		long totalCount = 0;
        		totalCount = result.getRows().getSize();;//result.getNodes().getSize();
        		q.setLimit(limit);
        		q.setOffset(offset*limit);
        		result = q.execute();
        		

        		List<Asset> assets = new ArrayList<Asset>();
        		RowIterator iterator = result.getRows();
        		while (iterator.hasNext()) { 
        			//totalCount++;
        			final Row row = iterator.nextRow(); 
        			Node node = session.getNode(row.getPath("s"));
        			if(node != null) {
            			Asset asset = (Asset)ocm.getObject(Asset.class, node.getPath());
            			asset.setUid(node.getIdentifier());
            			assets.add(asset);          				
        			}
      			
        		}
/*        		NodeIterator it = result.getNodes();
        		while(it.hasNext()) {
        			Node node = it.nextNode();
        			Asset asset = (Asset)ocm.getObject(Asset.class, node.getPath());
        			asset.setUid(node.getIdentifier());
        			assets.add(asset);
        		}	*/
        		
        		WebPage<Asset> webPage = new WebPage<Asset>((int)offset,(int)limit,totalCount,assets);
                return webPage; 
        	} 
        });
	}


	@SuppressWarnings("unchecked")
	public WebPage<Page> searchSite(final String queryString, final long limit, final long offset) {
		return (WebPage<Page>) jcrTemplate.execute(new JcrCallback() { 
        	public WebPage<Page> doInJcr(Session session) throws RepositoryException { 
        		ObjectContentManager ocm = new  ObjectContentManagerImpl(session, mapper);
        		QueryManager queryManager = session.getWorkspace().getQueryManager();
        		
        		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
        		
        		QueryResult result = q.execute();
        		long totalCount = result.getNodes().getSize();
        		q.setLimit(limit);
        		q.setOffset(offset*limit);
        		result = q.execute();

        		List<Page> pages = new ArrayList<Page>();
        		NodeIterator it = result.getNodes();
        		while(it.hasNext()) {
        			Node node = it.nextNode();
        			Page page = (Page)ocm.getObject(Page.class, node.getPath());
        			page.setUid(node.getIdentifier());
        			pages.add(page);
        		}	
        		
        		WebPage<Page> webPage = new WebPage<Page>((int)offset,(int)limit,totalCount,pages);
                return webPage; 
        	} 
        });
	}



	
	@SuppressWarnings("unchecked")
	public WebPage<Log> searchLog(final String queryString, final long limit, final long offset) {
		
		return (WebPage<Log>) jcrTemplate.execute(new JcrCallback() { 
        	public WebPage<Log> doInJcr(Session session) throws RepositoryException { 
        		ObjectContentManager ocm = new  ObjectContentManagerImpl(session, mapper);
        		QueryManager queryManager = session.getWorkspace().getQueryManager();
        		
        		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
        		
        		QueryResult result = q.execute();
        		long totalCount = result.getNodes().getSize();
        		q.setLimit(limit);
        		q.setOffset(offset*limit);
        		result = q.execute();

        		List<Log> pages = new ArrayList<Log>();
        		NodeIterator it = result.getNodes();
        		while(it.hasNext()) {
        			Node node = it.nextNode();
        			Log log = (Log)ocm.getObject(Log.class, node.getPath());
        			pages.add(log);
        		}	
        		
        		WebPage<Log> webPage = new WebPage<Log>((int)offset,(int)limit,totalCount,pages);
                return webPage; 
        	} 
        });
		
	}

	public Object add(final Object obj) throws RepositoryException {
		
		return jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException { 
        		ObjectContentManager ocm = new  ObjectContentManagerImpl(session, mapper);
        		ocm.insert(obj); 
	            ocm.save();
                return obj; 
        	} 
        });
	}

	public Object addOrUpdate(final SmartiNode obj) throws RepositoryException {
		
		return jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException { 
        		ObjectContentManager ocm = new  ObjectContentManagerImpl(session, mapper);
        		if(session.nodeExists(obj.getPath())) {
            		ocm.update(obj);
            		Node node = session.getNode(obj.getPath());
            		node.setProperty("jcr:lastModified", Calendar.getInstance());
                    log.debug("Node exists. update node={}", node);
    	            try {
    	            	String result = LinuxUtil.update(obj);
    					log.debug(result);
    				} catch (IOException | InterruptedException e) {
    					log.error(e.getMessage());
    				}       			
        		}else {
        			ocm.insert(obj); 
            		Node node = session.getNode(obj.getPath());
            		node.setProperty("jcr:lastModified", Calendar.getInstance());
                    log.debug("Saved node.  node={}", node);
    	            try {
    					String result = LinuxUtil.add(obj);
    					log.debug(result);
    				} catch (IOException | InterruptedException e) {
    					log.error(e.getMessage());
    				}     			
        		}
	            ocm.save();

	            return obj; 
        	} 
        });
	}

	public Object addOrUpdate(final Page page) throws RepositoryException {
		
		return jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException { 
        		ObjectContentManager ocm = new  ObjectContentManagerImpl(session, mapper);
        		if(session.nodeExists(page.getPath())) {
            		ocm.update(page);
            		Node node = session.getNode(page.getPath());
            		node.setProperty("jcr:lastModified", Calendar.getInstance());
                    log.debug("Node exists. update node={}", node);
        			
        		}else {
        			ocm.insert(page); 
            		Node node = session.getNode(page.getPath());
            		node.setProperty("jcr:lastModified", Calendar.getInstance());
                    log.debug("Saved node.  node={}", node);
     			
        		}
	            ocm.save();

	            return page; 
        	} 
        });
	}

	public Page add(final Page page) throws RepositoryException {
		
		return (Page)jcrTemplate.execute(new JcrCallback() { 
        	public Page doInJcr(Session session) throws RepositoryException { 
        		ObjectContentManager ocm = new  ObjectContentManagerImpl(session, mapper);
        		Node node = JcrUtils.getOrCreateUniqueByPath(page.getPath(), "nt:unstructured", session);
        		page.setPath(node.getPath());
        		page.setLastModified(new Date());
        		node.remove();
            	ocm.insert(page);
                log.debug("Node exists. update node={}", node);
	            ocm.save();

	            return page; 
        	} 
        });
	}
	
	public void update(final Folder folder) throws RepositoryException {
		
		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException { 
        		Node node = session.getNode(folder.getPath());
        		node.setProperty("name", folder.getName());
        		node.setProperty("rank", folder.getRank());
        		node.setProperty("orderby", folder.getOrderby());
        		if(folder.getOcm_classname() != null)
        		node.setProperty("ocm_classname", folder.getOcm_classname());
        		node.setProperty("groups", folder.getGroups());
        		node.setProperty("creadtedBy", folder.getCreatedBy());
        		node.setProperty("jcr:title", folder.getTitle());  
        		node.setProperty("jcr:lastModified", Calendar.getInstance());
        		session.save();
	            return null; 
        	} 
        });
	}
	

	public String getUniquePath(final String path, final String name) throws RepositoryException {
		return (String) jcrTemplate.execute(new JcrCallback() { 
	    	public String doInJcr(final Session session) throws RepositoryException { 
	    		Node child = JcrUtils.getOrCreateUniqueByPath(path+"/"+name, "nt:unstructured", session);
	    		String path = child.getPath();
	    		child.remove();
	    		return path;
	    	}
		});
		
	}

	public String getOrCreateByPath(final String path, final String nodeType,
			final String username) {
		return (String) jcrTemplate.execute(new JcrCallback() { 
	    	public String doInJcr(final Session session) throws RepositoryException { 
	    		
	    		Node child = JcrUtils.getOrCreateUniqueByPath(path, nodeType, session);
	    		child.setProperty("createdBy", username);
	    		session.save();
	    	
	    		return child.getPath();
	    	}
		});
		
	}


	public Node addNode(final String path, final String name, final String nodeType) throws RepositoryException {
		return (Node) jcrTemplate.execute(new JcrCallback() { 
	    	public Node doInJcr(final Session session) throws RepositoryException { 
	    		
	    		Node child = JcrUtils.getOrCreateUniqueByPath(path+"/"+name, nodeType, session);
	    		session.save();
	    	
	    		return child;
	    	}
		});
		
	}

	public String addNodes(final String path, final String nodeType,final String username) throws RepositoryException {
		return (String) jcrTemplate.execute(new JcrCallback() { 
    	public String doInJcr(final Session session) throws RepositoryException { 
    		Node node = session.getRootNode();
    		String titles[] = path.split("/");
            String name[] = path.toLowerCase().replaceAll("/0", "/f0").replaceAll("[\\:\\]\\[\\|\\*\\.]", "").replaceAll("[ -]", "_").split("/");
            ObjectContentManager ocm = new  ObjectContentManagerImpl(session, mapper);
            try {
             for(int i=0; i<name.length;i++) {
            	 if(!name[i].equals("")) {
            		 if(node.hasNode(name[i])) {
                         log.debug("Node exists.  node={}", node);
                         //node = node.addNode(name[i],nodeType) ; 
                         node = node.getNode(name[i]);
            		 }else {
            			 Folder folder = new Folder();
            			 folder.setName(titles[i]);
            			 folder.setTitle(titles[i]);
            			 long rank = 0;//node.getNodes().getSize();
            			 if(node.getPath().equals("/"))
            				 folder.setPath(node.getPath()+name[i]);
            			 else
            				 folder.setPath(node.getPath()+"/"+name[i]);
            			 folder.setRank(rank);
            			 folder.setOrderby("rank,name");
            			 folder.setCreatedBy(username);
            			 folder.setLastModified(new Date());
            			 //node = node.addNode(name[i],nodeType);
                         ocm.insert(folder);
                         node = session.getNode(folder.getPath());
            			 node.setProperty(Property.JCR_CREATED, Calendar.getInstance());
                         log.debug("added node.  node={}", node);

           		 }
            	 }
             }
             session.save();
             ocm.save();
            }catch(Exception e) {
            	log.error(e.getMessage());
            }finally {
                //session.logout();
            	
            }


             return node.getPath();
    	} 
    });
		
	}

	public void addFolders(final String path) throws RepositoryException {
		jcrTemplate.execute(new JcrCallback() { 
    	public Object doInJcr(final Session session) throws RepositoryException { 
    		
    		Node node = session.getRootNode();
    		String titles[] = path.split("/");
            String name[] = path.replaceAll("[\\:\\]\\[\\|\\*\\.]", "").replaceAll("[ -]", "_").split("/");
            try {
             for(int i=0; i<name.length;i++) {
            	 if(!name[i].equals("")) {
            		 if(node.hasNode(name[i])) {
                         log.debug("Node exists.  node={}", node);
                         node = node.addNode(name[i],"nt:folder") ;            			 
            		 }else {
            			 long rank = node.getNodes().getSize();
            			 node = node.addNode(name[i],"nt:folder");
            			 node.setProperty("rank", rank);
            			 log.debug(titles[i]);
                         log.debug("added node.  node={}", node);
           		 }
            	 }
             }
             session.save();
            }catch(Exception e) {
            	log.error(e.getMessage());
            }finally {
                //session.logout();
            	
            }


             return node;
    	} 
    });
		
	}

	
	@Override
	public Asset getAssetById(String uid) throws RepositoryException {
		String path= getNodeById(uid);
		Asset asset = (Asset)getObject(path);
		return asset;
	}


	public Object getObject(final String path) throws RepositoryException {
		
		return jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException { 
        		ObjectContentManager ocm = new  ObjectContentManagerImpl(session, mapper);
        		
	            return  ocm.getObject(path);
                
        	} 
        });
	}

	
	@Override
	public Object getFirstObjectByTitle(String path, String title) {
		
		return null;
	}


	public String getValidPath(final String path) throws RepositoryException {
		
		return (String) jcrTemplate.execute(new JcrCallback() { 
        	public String doInJcr(Session session) throws RepositoryException { 
/*        		String validPath = path;
        		if(session.nodeExists(path)) {
            		int i = 0;
            		while(session.nodeExists(path+i)) {
            			i++;
            		}
            		validPath +=i;
        		}*/
	    		Node node = JcrUtils.getOrCreateUniqueByPath(path, "nt:unstructured", session);

	            return  node.getPath();
                
        	} 
        });
	}
	

	@SuppressWarnings("unchecked")
	public List<Object> getObjects(final String path, final Class<?> aclass) {
		return (List<Object>) jcrTemplate.execute(new JcrCallback() { 
        	public List<Object> doInJcr(Session session) throws RepositoryException { 
        		ObjectContentManager ocm = new  ObjectContentManagerImpl(session, mapper);
        		org.apache.jackrabbit.ocm.query.QueryManager  queryManager = ocm.getQueryManager();
        		Filter filter = queryManager.createFilter(aclass);
        		filter.setScope(path);
        		
        		org.apache.jackrabbit.ocm.query.Query query = queryManager.createQuery(filter);
        		query.addOrderByAscending("rank");
        		List<Object> objects = (List<Object>)ocm.getObjects(query);
                return objects;
        	} 
        });		
		
	}
	


	public boolean nodeExsits(final String path) {
		return (Boolean) jcrTemplate.execute(new JcrCallback() { 
	    	public Boolean doInJcr(final Session session) throws RepositoryException { 
	             
	             return session.nodeExists(path);
	    	}
		});
	}
	
	public Node getNode(final String path) throws RepositoryException {
		return (Node) jcrTemplate.execute(new JcrCallback() { 
    	public Object doInJcr(final Session session) throws RepositoryException { 
             Node node = session.getNode(path);
             return node;
    	} 
    });
		
	}

	public String getNodeById(final String uuid) throws RepositoryException {
		return (String) jcrTemplate.execute(new JcrCallback() { 
    	public String doInJcr(final Session session) throws RepositoryException { 
             Node node = session.getNodeByIdentifier(uuid);
             
             return node.getPath();
    	} 
    });
		
	}
	public Folder getFolder(final String path) throws RepositoryException {
		return (Folder) jcrTemplate.execute(new JcrCallback() { 
    	public Folder doInJcr(final Session session) throws RepositoryException { 
             Node node = session.getNode(path);
             Folder folder =new Folder();
             if(node.hasProperty("name"))
            	 folder.setName(node.getProperty("name").getString());
             else
             folder.setName(getTitle(node));
             folder.setTitle(node.getProperty("jcr:title").getString());
             folder.setPath(node.getPath());
             folder.setLevel(node.getDepth());
             folder.setNodeName(node.getName());
             folder.setUid(node.getIdentifier());
             if(node.hasProperty("lastModified"))
            	 folder.setLastModified(node.getProperty("lastModified").getDate().getTime());
             if(node.hasProperty("lastUpdated"))
            	 folder.setLastUpdated(node.getProperty("lastUpdated").getDate().getTime());
             if(node.hasProperty("orderby"))
            	 folder.setOrderby(node.getProperty("orderby").getValue().getString());
             if(node.hasProperty("rank"))
            	 folder.setRank(node.getProperty("rank").getValue().getLong());
             if(node.hasProperty("groups"))
            	 folder.setGroups(node.getProperty("groups").getValue().getString());
             if(node.hasProperty("passcode"))
            	 folder.setPasscode(node.getProperty("passcode").getString());   
             if(node.hasProperty("intranet"))
            	 folder.setIntranet(node.getProperty("intranet").getString());   
             if(node.hasProperty("sharing"))
            	 folder.setSharing(node.getProperty("sharing").getString());   
             if(node.hasProperty("readonly"))
            	 folder.setReadonly(node.getProperty("readonly").getString());   
             if(node.hasProperty("description"))
            	 folder.setDescription(node.getProperty("description").getString());   
             if(node.hasProperty("resolution"))
            	 folder.setResolution(node.getProperty("resolution").getString());   

             if(node.hasProperty("ocm_classname")) {
            	 String[] classname = node.getProperty("ocm_classname").getString().split("\\.");
            	 
            	 folder.setOcm_classname(classname[classname.length-1]);
             }
             folder.setHasNodes(node.hasNodes());
             Node parent = node.getParent();
             folder.setParent(parent.getPath());
             if(parent.hasProperty("jcr:title")) {
                 folder.setParentTitle(parent.getProperty("jcr:title").getString());
            	 
             }

             return folder;
    	} 
    });
		
	}

	public Page getPage(final String path) throws RepositoryException {
		return (Page) jcrTemplate.execute(new JcrCallback() { 
	    	public Page doInJcr(final Session session) throws RepositoryException { 
	    		 ObjectContentManager ocm = new  ObjectContentManagerImpl(session, mapper);
	             Node node = session.getNode(path);
	             Page page = (Page)ocm.getObject(path);
	             if(page.getContent() == null) {
	            	 page.setContent("<h1 property=\"name\" id=\"wb-cont\">"+page.getTitle()+"</h1><div><p class=\"pagetag\">"+page.getDescription()+"</p></div>");
	             }
	             page.setDepth(node.getDepth());
	
	             page.setHasNodes(node.hasNodes());
	             page.setParent(node.getParent().getPath());
	
	             return page;
	    	} 
	    });
			
	}
	
	public Chat getChat(final String path) throws RepositoryException {
		return (Chat) jcrTemplate.execute(new JcrCallback() { 
	    	public Chat doInJcr(final Session session) throws RepositoryException { 
	    		 ObjectContentManager ocm = new  ObjectContentManagerImpl(session, mapper);
	             //Node node = session.getNode(path);
	             Chat chat = (Chat)ocm.getObject(path);
            
	             return chat;
	    	} 
	    });
			
	}	
	public Page getPageByUid(final String uid) throws RepositoryException {
		return (Page) jcrTemplate.execute(new JcrCallback() { 
    	public Page doInJcr(final Session session) throws RepositoryException { 
             Node node = session.getNodeByIdentifier(uid);
    		 ObjectContentManager ocm = new  ObjectContentManagerImpl(session, mapper);
             Page page = (Page)ocm.getObject(node.getPath());
             if(page.getContent() == null) {
            	 page.setContent("<h1>"+page.getTitle()+"</h1><p>"+page.getDescription()+"</p>");
             }
             page.setDepth(node.getDepth());

             page.setHasNodes(node.hasNodes());
             page.setParent(node.getParent().getPath());

             return page;
    	} 
    });
		
	}	
	public Folder getFolder(final String path, final String permission) throws RepositoryException {
		return (Folder) jcrTemplate.execute(new JcrCallback() { 
    	public Folder doInJcr(final Session session) throws RepositoryException { 
    		
    		String queryString = "SELECT * FROM [nt:base] AS s WHERE ISSAMENODE(["+path+"])"+permission;
    		QueryManager queryManager = session.getWorkspace().getQueryManager();    		
    		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
    		QueryResult result = q.execute();
    		if(result.getNodes().getSize() == 0) {
    			throw new RepositoryException("Permission denied!");
    		}
             Node node = session.getNode(path);
             Folder folder =new Folder();
             if(node.hasProperty("name"))
            	 folder.setName(node.getProperty("name").getString());
             else
            	 folder.setName(getTitle(node));
             if(node.hasProperty("rank"))
            	 folder.setRank(node.getProperty("rank").getValue().getLong());
             if(node.hasProperty("orderby"))
            	 folder.setOrderby(node.getProperty("orderby").getValue().getString());       
             if(node.hasProperty("passcode"))
            	 folder.setPasscode(node.getProperty("passcode").getString());        
             if(node.hasProperty("intranet"))
            	 folder.setIntranet(node.getProperty("intranet").getString());   
             if(node.hasProperty("resolution"))
            	 folder.setResolution(node.getProperty("resolution").getString());   
             
             folder.setTitle(getTitle(node));
             folder.setPath(node.getPath());
             folder.setLevel(node.getDepth());
             folder.setNodeName(node.getName());
             if(node.hasProperty("ocm_classname")) {
            	 String[] classname = node.getProperty("ocm_classname").getString().split("\\.");
            	 
            	 folder.setOcm_classname(classname[classname.length-1]);
             }
             folder.setHasNodes(node.hasNodes());
             folder.setParent(node.getParent().getPath());
             
             return folder;
    	} 
    });
		
	}
	public String deleteNodeAndOrphan(final String path) throws RepositoryException {
		return (String) jcrTemplate.execute(new JcrCallback() { 
    	public String doInJcr(final Session session) throws RepositoryException { 
    		String parentPath =path;
    		if(session.nodeExists(path)) {
                Node node = session.getNode(path);
                parentPath = node.getParent().getPath(); 
                node.remove();
                session.save();
                node = session.getNode(parentPath);
                if(!node.hasNodes()) {
                	parentPath = node.getParent().getPath();
                	node.remove();
                	session.save();
                    try {
    					String result = LinuxUtil.delete(node.getIdentifier());
    					log.debug(result);
    				} catch (IOException e) {
    					log.error(e.getMessage());
    				} catch (InterruptedException e) {
    					log.error(e.getMessage());
    				}                   	
                }
                log.debug("Node deleted node="+node);
    		}else {
                log.debug("Node does not exists path="+path);
    			
    		}
             return parentPath;
    	} 
    });
		
	}
	
	public void  deleteNodes(final String queryString) {
		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 
        		QueryManager queryManager = session.getWorkspace().getQueryManager();
     		
        		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
        		
        		QueryResult result = q.execute();

        		NodeIterator it = result.getNodes();
        		
        		while(it.hasNext()) {
        			Node node = it.nextNode();
        			node.remove();
                    try {
    					LinuxUtil.delete(node.getIdentifier());
    				} catch (IOException e) {
    					log.error(e.getMessage());
    				} catch (InterruptedException e) {
    					log.error(e.getMessage());
    				}           			

        		}
        		session.save();
        		return null;
        	} 		
		});
		
	}
	public String deleteNode(final String path) throws RepositoryException {
		return (String) jcrTemplate.execute(new JcrCallback() { 
    	public String doInJcr(final Session session) throws RepositoryException { 
    		String parentPath =path;
    		if(session.nodeExists(path)) {
                Node node = session.getNode(path);
                parentPath = node.getParent().getPath(); 
                updateCalendar(parentPath,"lastModified");
                node.remove();
                session.save();
                node = session.getNode(parentPath);
                try {
					LinuxUtil.delete(node.getIdentifier());
				} catch (IOException e) {
					log.error(e.getMessage());
				} catch (InterruptedException e) {
					log.error(e.getMessage());
				}                
                log.debug("Node deleted node="+node);
    		}else {
                log.debug("Node does not exists path="+path);
    			
    		}
             return parentPath;
    	} 
    });
		
	}

	public void rename(final Node node,final String newName) throws AccessDeniedException, ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException, ItemNotFoundException, RepositoryException {
			jcrTemplate.execute(new JcrCallback() { 
		    	public Object doInJcr(final Session session) throws RepositoryException { 
		    		session.move(node.getPath(), node.getParent().getPath()+"/"+newName);
		         return node;
		    	} 
		    });

	}


	public String getBreadcrumb(Page page) {

		return null;
	}

	@SuppressWarnings("unchecked")
	public List<JcrNode> getBreadcrumbNodes(final String path) {

		return (List<JcrNode>)jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 
        	   List<JcrNode> nodes = new ArrayList<JcrNode>();
               Node node = session.getNode(path);
               while(node.getDepth() > 2) {
            	    node = node.getParent();
            	    JcrNode f = new JcrNode();
            	    f.setPath(node.getPath());
            	    if(node.hasProperty("jcr:title")) {
                       f.setTitle(node.getProperty("jcr:title").getString());
                    }else {
                    	f.setTitle(node.getName());    
                    }
            	    nodes.add(0,f);
               }
               return  nodes;
        	} 		
		});

	}
	

	public String getBreadcrumb(final String path) {

		return (String)jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 
               String breadcrumb  = "";
               Node node = session.getNode(path);
               String title="";
               if(node.hasProperty("navTitle"))
                  title = node.getProperty("navTitle").getString();
               else if(node.hasProperty("jcr:title")) {
            	   title = node.getProperty("jcr:title").getString();            	   
               }else {
            	   title = node.getName();    
               }
               breadcrumb = "";//"<li><a href=\""+node.getPath()+".html\">"+title+"</a></li>";
               while(node.getDepth() > 2) {
            	   node = node.getParent();
            	   if(node.hasProperty("hideNav") && "true".equals(node.getProperty("hideNav").getString())) {
            		   continue;
               	    }else if(node.hasProperty("navTitle")) {
            		   title = node.getProperty("navTitle").getString();
            	    } else if(node.hasProperty("jcr:title")) {
                       title = node.getProperty("jcr:title").getString();
                    }else {
                 	   title = node.getName();    
                    }

            		   breadcrumb = "<li><a href=\""+node.getPath()+".html\">"+title+"</a></li>"+breadcrumb;

               }
               return  "<ol class=\"breadcrumb\"><li><a href=\"http://"+domain+"\">\u5927\u5BB6\u62FF</a></li>"+breadcrumb+"</ol>";
        	} 		
		});

	}

	public String getBreadcrumb(final String path, final String permission) {

		return (String)jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 
        		QueryManager queryManager = session.getWorkspace().getQueryManager();    		

        		String breadcrumb  = "";
               String existingpath = path;
               while(!session.nodeExists(existingpath) && existingpath.length()>0) {
            	   existingpath = existingpath.substring(0, existingpath.lastIndexOf("/"));
               };
               session.getRootNode();
               Node node = session.getNode(existingpath);
               String groups = "";
               if(node.hasProperty("groups")) {
            	   groups = node.getProperty("groups").getString();
               }
               if(!"".equals(groups) && groups!=null) {
              		String queryString = "SELECT * FROM [nt:base] AS s WHERE ISSAMENODE(["+node.getPath()+"])"+permission;
              		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
	           		QueryResult result = q.execute();
	           		if(result.getNodes().getSize() == 0) {
	           			throw new RepositoryException("Permission denied for "+permission+" at "+node.getPath());
	           		}
           	   
               }
               if(node.hasProperty("Title"))
				node.getProperty("Title").getString();
			else if(node.hasProperty("name")) {
            	   node.getProperty("name").getString();            	   
               }else {
            	   node.getName();    
               }
               breadcrumb = "<li><a href=\""+node.getPath()+(node.getDepth()>1?".html":"")+"\">"+getTitle(node)+"</a>"+(node.getDepth()>3?" <a href=\""+node.getPath()+".pdf?mp=100\" target=\"_blank\"><img src=\""+Folder.root+"/resources/images/com.filemark.model.Pages.gif\" title=\"open all pdf\" alt=\"open all pdf\"></a>":"")+"</li>";
               while(node.getDepth() > 1) {
            	   node = node.getParent();
                   if(node.hasProperty("groups")) {
                	   groups = node.getProperty("groups").getString();
                   }
                   if(!"".equals(groups) && groups!=null) {
                  		String queryString = "SELECT * FROM [nt:base] AS s WHERE ISSAMENODE(["+node.getPath()+"])"+permission;
                  		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
    	           		QueryResult result = q.execute();
    	           		if(result.getNodes().getSize() == 0) {
    	           			throw new RepositoryException("Permission denied "+permission+" at "+node.getPath());
    	           		}
               	   
                   }

                   if(node.hasProperty("Title"))
					node.getProperty("Title").getString();
				else if(node.hasProperty("name")) {
                 	   node.getProperty("name").getString();            	   
                    }else {
                 	   node.getName();    
                    }

            		   breadcrumb = "<li><a href=\""+node.getPath()+(node.getDepth()>1?".html":"")+"\">"+getTitle(node)+"</a></li>"+breadcrumb;
                   }

               return  "<ol class=\"breadcrumb\">"+breadcrumb+"</ol>";
        	} 		
		});

	}

	public String getBreadcrumb(final String path, final String permission,final int depth) {

		return (String)jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 
        		QueryManager queryManager = session.getWorkspace().getQueryManager();    		

        		String breadcrumb  = "";
               String existingpath = path;
               while(!session.nodeExists(existingpath) && existingpath.length()>0) {
            	   existingpath = existingpath.substring(0, existingpath.lastIndexOf("/"));
               };
               session.getRootNode();
               Node node = session.getNode(existingpath);
               String groups = "";
               if(node.hasProperty("groups")) {
            	   groups = node.getProperty("groups").getString();
               }
               if(!"".equals(groups) && groups!=null) {
              		String queryString = "SELECT * FROM [nt:base] AS s WHERE ISSAMENODE(["+node.getPath()+"])"+permission;
              		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
	           		QueryResult result = q.execute();
	           		if(result.getNodes().getSize() == 0) {
	           			throw new RepositoryException("Permission denied for "+permission+" at "+node.getPath());
	           		}
           	   
               }
               if(node.hasProperty("Title"))
				node.getProperty("Title").getString();
			else if(node.hasProperty("name")) {
            	   node.getProperty("name").getString();            	   
               }else {
            	   node.getName();    
               }
               breadcrumb = "<li>"+(node.getDepth()<depth?"<a href=\""+node.getPath()+(node.getDepth()>1?".html":"")+"\">"+getTitle(node)+"</a>":"")+(node.getDepth()>3?" <a href=\""+node.getPath()+".pdf?mp=100\" target=\"_blank\"><img src=\""+Folder.root+"/resources/images/com.filemark.model.Pages.gif\" title=\"open all pdf\" alt=\"open all pdf\"></a>":"")+"</li>";
               while(node.getDepth() > 1) {
            	   node = node.getParent();
                   if(node.hasProperty("groups")) {
                	   groups = node.getProperty("groups").getString();
                   }
                   if(!"".equals(groups) && groups!=null) {
                  		String queryString = "SELECT * FROM [nt:base] AS s WHERE ISSAMENODE(["+node.getPath()+"])"+permission;
                  		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
    	           		QueryResult result = q.execute();
    	           		if(result.getNodes().getSize() == 0) {
    	           			throw new RepositoryException("Permission denied "+permission+" at "+node.getPath());
    	           		}
               	   
                   }
                   if(node.getDepth()<=depth) {
                   if(node.hasProperty("Title"))
					node.getProperty("Title").getString();
				else if(node.hasProperty("name")) {
                 	   node.getProperty("name").getString();            	   
                    }else {
                 	   node.getName();    
                    }

            		   breadcrumb = "<li><a href=\"/"+node.getPath()+(node.getDepth()>1?".html":"")+"\">"+getTitle(node)+"</a></li>"+breadcrumb;
                   }
               }
               return  "<ol class=\"breadcrumb\">"+breadcrumb+"</ol>";
        	} 		
		});

	}

    public String getSavedQueries(final String queryString)	 {
		return (String)jcrTemplate.execute(new JcrCallback() { 
        	public String doInJcr(Session session) throws RepositoryException, IOException { 
            	String savedQueries = "";
        		QueryManager queryManager = session.getWorkspace().getQueryManager();    		
        		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
        		q.setLimit(20);
        		QueryResult result = q.execute();
           		NodeIterator it = result.getNodes();
           		while(it.hasNext()) {
           			Node node = it.nextNode();
           			if(node.hasProperty("url"))
           			savedQueries +="<li><a href=\""+node.getProperty("url").getString()+"\">"+getTitle(node)+"</a></li>";      					
           		}
        		return savedQueries;
        	}
		});
    }
	public String getPageNavigation(final String path,final int level) {

		return (String)jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 
            String navigator  = "";
            Node node = getNode(path);
            String start = node.getAncestor(level).getPath();
            Node start_node = getNode(start);            
            if(start_node.hasProperty("navigation")) {
            	navigator = start_node.getProperty("navigation").getString();
            }
            return  navigator;
        	}
		});

	}

	public String setPageNavigation(final String path,final int level,final int max) {

		return (String)jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 
            String navigator  = "";
            Node node = getNode(path);
            String start = node.getAncestor(level).getPath();
            Node start_node = getNode(start);
       		QueryManager queryManager = session.getWorkspace().getQueryManager();
    		String queryString = "SELECT s.* FROM [nt:base] AS s WHERE ISCHILDNODE(s,["+start+"]) "
    		+" and s.ocm_classname='com.filemark.jcr.model.Page' and s.hideNav not like 'true' and s.status like 'true' ORDER BY s.order";
       		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
       		
       		QueryResult result = q.execute();

       		q.setLimit(max);
       		result = q.execute();


       		NodeIterator it = result.getNodes();
       		
       		while(it.hasNext()) {
       			Node child_node = it.nextNode();
       			String child_title = child_node.getProperty("jcr:title").getString();
        		String queryChildren = "SELECT s.* FROM [nt:base] AS s WHERE ISCHILDNODE(s,["+child_node.getPath()+"]) "
        	    		+" and s.ocm_classname='com.filemark.jcr.model.Page' and s.hideNav not like 'true' and s.status like 'true' ORDER BY s.order";
           		QueryImpl child_q = (QueryImpl)queryManager.createQuery(queryChildren, Query.JCR_SQL2);
           		child_q.setLimit(20);
           		
           		QueryResult child_result = child_q.execute(); 
           		NodeIterator child_it = child_result.getNodes();
       			navigator +="<li><a class=\"item\" href=\""+(child_it.hasNext()?"#"+child_node.getIdentifier():child_node.getPath()+".html")+"\">"+child_title+"</a>";

           		if(child_it.hasNext()) {
       				navigator +="<ul class=\"sm list-unstyled\" id=\""+node.getIdentifier()+"\" role=\"menu\">";
       				while(child_it.hasNext()) {
       					Node child = child_it.nextNode();
       	       			navigator +="<li><a href=\""+child.getPath()+".html\" role=\"menuitem\">"+child.getProperty("jcr:title").getString()+"</a></li>";      					
       				}
       				navigator +="<li class=\"slflnk\"><a href=\""+child_node.getPath()+".html\" role=\"menuitem\">"+child_title+" - \u5168\u90E8</a><li>";
       				navigator +="</ul>";
       			}
       			navigator +="</li>";
        	} 
       		start_node.setProperty("navigation", navigator);
       		session.save();
            return  navigator;
        	}
		});

	}
		
	public String getNavigation(final String path ,final int level,final int max) {

		return (String)jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 
            String navigator  = "";
            Node start_node = getNode(path);
            String start = start_node.getAncestor(level).getPath();   
       		QueryManager queryManager = session.getWorkspace().getQueryManager();
    		String queryString = "SELECT s.* FROM [nt:base] AS s WHERE ISCHILDNODE(s,["+start+"]) "//+permission
    		+" and s.ocm_classname='com.filemark.jcr.model.Page' and s.hideNav not like 'true' ORDER BY s.order";
       		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
       		
       		QueryResult result = q.execute();

       		q.setLimit(20);
       		result = q.execute();


       		NodeIterator it = result.getNodes();
       		
       		while(it.hasNext()) {
       			Node node = it.nextNode();
        		String queryChildren = "SELECT s.* FROM [nt:base] AS s WHERE ISCHILDNODE(s,["+node.getPath()+"]) "//+permission
        	    		+" and s.ocm_classname='com.filemark.jcr.model.Page' and s.hideNav not like 'true' ORDER BY s.order";
           		QueryImpl child_q = (QueryImpl)queryManager.createQuery(queryChildren, Query.JCR_SQL2);
           		child_q.setLimit(20);
           		
           		QueryResult child_result = child_q.execute(); 
           		NodeIterator child_it = child_result.getNodes();
       			navigator +="<li><a class=\"item\" href=\""+(child_it.hasNext()?"#"+node.getIdentifier():node.getPath()+".html")+"\">"+node.getProperty("jcr:title").getString()+"</a>";

           		if(child_it.hasNext()) {
       				navigator +="<ul class=\"sm list-unstyled\" id=\""+node.getIdentifier()+"\" role=\"menu\">";
       				while(child_it.hasNext()) {
       					Node child = child_it.nextNode();
       	       			navigator +="<li><a href=\".."+child.getPath()+".html\" role=\"menuitem\">"+child.getProperty("jcr:title").getString()+"</a></li>";      					
       				}
       				navigator +="<li class=\"slflnk\"><a href=\""+node.getPath()+".html\" role=\"menuitem\">"+node.getProperty("jcr:title").getString()+" - \u5168\u90E8</a><li>";
      				
       				navigator +="</ul>";
       			}
       			navigator +="</li>";
        	} 
            return  navigator;
        	}
		});

	}	
	public String getNavigation(final String path , final String permission) {

		return (String)jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 
               String navigator  = "";
       		QueryManager queryManager = session.getWorkspace().getQueryManager();
    		String queryString = "SELECT s.* FROM [nt:base] AS s WHERE ISCHILDNODE(s,["+path+"]) "//+permission
    		+" and s.ocm_classname='com.filemark.jcr.model.Page' ORDER BY s.order";
       		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
       		
       		QueryResult result = q.execute();

       		q.setLimit(20);
       		result = q.execute();


       		NodeIterator it = result.getNodes();
       		
       		while(it.hasNext()) {
       			Node node = it.nextNode();
       			navigator +="<li><a class=\"item\" href=\""+(node.hasNodes()?"#"+node.getIdentifier():node.getPath()+".html")+"\">"+node.getProperty("jcr:title").getString()+"</a>";
        		String queryChildren = "SELECT s.* FROM [nt:base] AS s WHERE ISCHILDNODE(s,["+node.getPath()+"]) "//+permission
        	    		+" and s.ocm_classname='com.filemark.jcr.model.Page' ORDER BY s.order";
           		QueryImpl child_q = (QueryImpl)queryManager.createQuery(queryChildren, Query.JCR_SQL2);
           		child_q.setLimit(20);
           		
           		QueryResult child_result = child_q.execute(); 
           		NodeIterator child_it = child_result.getNodes();
       			if(child_it.hasNext()) {
       				navigator +="<ul class=\"sm list-unstyled\" id=\""+node.getIdentifier()+"\" role=\"menu\">";
       				while(child_it.hasNext()) {
       					Node child = child_it.nextNode();
       	       			navigator +="<li><a href=\".."+child.getPath()+".html\">"+child.getProperty("jcr:title").getString()+"</a></li>";      					
       				}
       				navigator +="</ul>";
       			}
       			navigator +="</li>";
        	} 
            return  navigator;
        	}
		});

	}
	
	public void exportDocument(final String path, final OutputStream out,final Boolean binary) {

		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 

        		session.exportDocumentView(path, out, binary,true);
	            return null;
                
        	} 		
		});

	}

	public void exportSystem(final String path, final OutputStream out,final Boolean binary) {

		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 

        		session.exportSystemView(path, out, binary,false);
	            return null;
                
        	} 		
		});

	}

	public void importSystem(final String path, final InputStream in,final int behavior ) {

		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 

        		session.importXML(path, in, behavior);
        		session.save();
	            return null;
                
        	} 		
		});

	}
	public void exportXml(final String path, final OutputStream out) {

		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 

        		session.exportSystemView(path, out, false, false);
	            return null;
                
        	} 		
		});

	}

	public InputStream getInputStream(final String path) {

		return (InputStream)jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 
        		InputStream is= null;
                Node jcrNode = session.getNode(path+"/jcr:content");
                is = jcrNode.getProperty("jcr:data").getBinary().getStream();
	            return is;
                
        	} 		
		});		
	}
	
	public void exportPdfFile(final String path, final HttpServletResponse response, final long limit, final long offset, final int pageNumber) {

		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 
        		PdfCopy copy = null;
        		Document doc = null;
        		//PdfOutline root = null;
        		//PdfContentByte cb =null;
        		//PdfOutline outline = null;
        		OutputStream outputStream = null;
        		try {

        	       		QueryManager queryManager = session.getWorkspace().getQueryManager();
    	        		String queryString = "select * from [nt:file] AS s WHERE ISDESCENDANTNODE(["+path+"])";
    	        		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
    	        		
    	        		q.setLimit(limit);
    	        		q.setOffset(offset);
    	        		QueryResult result = q.execute();
    	        		NodeIterator it = result.getNodes();
    	        		//RowIterator rows = result.getRows();
    	        		
    	        		while(it.hasNext()) {
    	        			//Row row = (Row) rows.next();
    	        			Node node = it.nextNode();

    	        			//Node pdf = node.getNode("pdf");
    	                    //Node jcrNode = node.getNode("jcr:content");
    	                    InputStream inputStream = JcrUtils.readFile(node);//jcrNode.getProperty("jcr:data").getBinary().getStream();
    						PdfReader reader = new PdfReader(inputStream);
    						
    						if (doc ==null) {
    							doc = new Document(reader.getPageSizeWithRotation(1));
    							outputStream = response.getOutputStream();
    							response.setContentType("application/pdf");							
    							copy = new PdfCopy(doc, outputStream);
    							doc.open();
    							//cb = copy.getDirectContent();
    							//root = cb.getRootOutline();
    						}
    				        int numPages = reader.getNumberOfPages();
    				        PdfImportedPage pdfPage = null;
    				        if(pageNumber <=0 || pageNumber>numPages) {
	    				        for(int j=1; j<=numPages;j++) {
	    					        pdfPage = copy.getImportedPage(reader, j);
	    					        copy.addPage(pdfPage);
	    					    }
    				        }else {
    					        pdfPage = copy.getImportedPage(reader, pageNumber);
    					        copy.addPage(pdfPage);   				        
    				        }
    				        inputStream.close();
    	        		}   				


        		} catch (DocumentException e) {
        			throw new IOException(e.getMessage());
				}finally {
        			if(doc != null)
        				doc.close();
        			if(outputStream!=null) {
        				outputStream.flush();
        				outputStream.close();
        			}
        		}

	            return null;
                
        	} 		
		});

	}

	public void assets2pdf(final List<Asset> assets,final OutputStream outputStream) {
		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 
        		PdfCopy copy = null;
        		Document doc = null;
        		PdfOutline root = null;
        		PdfContentByte cb =null;
        		int pageCount = 1;
        		 ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();        		
        		try {

 	        		
	        		for(Asset asset:assets) {
	        			Node node = null;

	        			if(asset.getContentType() ==null || !(asset.getContentType().startsWith("image") || asset.getContentType().equals("application/pdf"))) continue;
	        			
	                    InputStream inputStream = null;
	                    File file =getFile(asset.getPath());
	                    String ext = asset.getExt();
	                    if(ext == null) ext = asset.getName().substring(asset.getName().lastIndexOf("."));
	                    if(file.isDirectory()) file = new File(file,"/origin"+ext);
	                    if(file != null && file.exists()) {
	                    	inputStream = new FileInputStream(file);
	                    }else if(nodeExsits(asset.getPath()+"/original")){
	                    	node = getNode(asset.getPath()+"/original");
		                    inputStream = JcrUtils.readFile(node);//jcrNode.getProperty("jcr:data").getBinary().getStream();
	                    	
	                    }else {
	                    	continue;
	                    }
	                    

	        			if(asset.getContentType().equals("image/tiff") || asset.getContentType().equals("image/x-tiff")) {
	        				try {
								convertTifToPDF(pdfStream,inputStream,asset.getDescription());
								inputStream = new ByteArrayInputStream(pdfStream.toByteArray());
							} catch (Exception e) {
								log.error(e.getMessage());
								continue;
							}
	        			}else if(asset.getContentType().startsWith("image")) {
							try {
								convertIMGToPDF(pdfStream,inputStream,asset.getDescription());
								inputStream = new ByteArrayInputStream(pdfStream.toByteArray());
							} catch (Exception e) {
								log.error(e.getMessage());
								continue;
							}
        				
	        			}else if(!asset.getContentType().equals("application/pdf")) {
	        				continue;
	        			}
	                    
						PdfReader reader = new PdfReader(inputStream);
						
						if (doc ==null) {
							doc = new Document(reader.getPageSizeWithRotation(1));

							copy = new PdfCopy(doc, outputStream);
							doc.open();
							cb = copy.getDirectContent();
							pageCount = copy.getCurrentPageNumber();
							root = cb.getRootOutline();
						

						}
				        int numPages = reader.getNumberOfPages();
				        PdfImportedPage pdfPage = null;
				        String title = asset.getTitle();
			    		new PdfOutline(root, 
								PdfAction.gotoLocalPage(pageCount, new PdfDestination(PdfDestination.FIT), copy),StringEscapeUtils.escapeXml(title));


				        for(int j=1; j<=numPages;j++) {
					        pdfPage = copy.getImportedPage(reader, j);
					        copy.addPage(pdfPage);
					    }
				        
				        pageCount +=numPages;
				        inputStream.close();
	        		}   				


    		} catch (DocumentException e) {
    			throw new IOException(e.getMessage());
			}finally {
    			if(doc != null)
    				doc.close();
    			if(outputStream!=null) {
    				outputStream.flush();
    				outputStream.close();
    			}
    		}

            return null;
            
    	} 		
	});

	}
	
	public void convertPDFtoPDF(OutputStream outputStream, InputStream inputStream) throws IOException, DocumentException {
		PdfCopy copy = null;
		Document doc = null;
		PdfContentByte cb =null;
		PdfReader reader = new PdfReader(inputStream);
		
		if (doc ==null) {
			doc = new Document(reader.getPageSizeWithRotation(1));

			copy = new PdfCopy(doc, outputStream);
			doc.open();
			cb = copy.getDirectContent();
			copy.getCurrentPageNumber();
			cb.getRootOutline();
		

		}
        int numPages = reader.getNumberOfPages();
        PdfImportedPage pdfPage = null;


        for(int j=1; j<=numPages;j++) {
	        pdfPage = copy.getImportedPage(reader, j);
	        copy.addPage(pdfPage);
	    }
        
        inputStream.close();
	   				
	}
	
	public void convertIMGToPDF(OutputStream output, InputStream inputStream, String description) throws Exception {
		//com.lowagie.text.Document pdfDoc = null;
		com.itextpdf.text.Document pdfDoc = null;
		com.itextpdf.text.Image img = null;
		byte[] data = new byte[inputStream.available()];
		float space = 10.0f;

		Resource resource = new ClassPathResource(asianFont);
		
		Font font = FontFactory.getFont(resource.getFile().getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		inputStream.read(data);
			  img = com.itextpdf.text.Image.getInstance(data);
			  if(description !=null && !"".equals(description)) {
				  com.itextpdf.text.Rectangle pagesize = new com.itextpdf.text.Rectangle(PageSize.LETTER.getWidth()+20, PageSize.LETTER.getHeight()+20);
				  pdfDoc = new com.itextpdf.text.Document();
				  pdfDoc.setPageSize(pagesize);
				  pdfDoc.setMargins(space, space, space, space);
			  }else {
				  com.itextpdf.text.Rectangle pagesize = new com.itextpdf.text.Rectangle(img.getWidth()+20, img.getHeight()+20);
			      pdfDoc = new com.itextpdf.text.Document();
			      pdfDoc.setPageSize(pagesize);
				  pdfDoc.setMargins(space, space, space, space);			      
			  }
			  com.itextpdf.text.pdf.PdfWriter pdfWriter = com.itextpdf.text.pdf.PdfWriter.getInstance( pdfDoc, output);
			  
			  pdfDoc.open( );
			  img.setAlignment( com.itextpdf.text.Image.LEFT );
			  //img.scalePercent( 72f / 200f * 100 );
			  if(description !=null)
			  img.scaleToFit(PageSize.LETTER.getWidth(), PageSize.LETTER.getHeight());
			  img.setSpacingBefore( 0 );
			pdfDoc.add( img );
			if(description !=null) {
				font.setSize(16);
				log.debug(description);
				description = description.replaceAll("<p>", "").replaceAll("</p>", "");
				Paragraph p = new Paragraph(description, font);
				pdfDoc.add(p);
			}

	          log.debug("Image 2 pdf.");
	          pdfDoc.close( );
	          pdfWriter.close();

	         
	 }
	
	 @SuppressWarnings("null")
	public void convertTifToPDF(OutputStream output, InputStream inputStream, String description) throws Exception{
		 com.lowagie.text.Document pdfDoc = null;	
		 com.lowagie.text.Image img = null;    

	    
	      log.debug("creating PDF output document.");
	      RandomAccessFileOrArray inFile = new RandomAccessFileOrArray(inputStream);
	      int total = TiffImage.getNumberOfPages(inFile);//dec.getNumPages( );

	      pdfDoc = new com.lowagie.text.Document(new Rectangle(img.getWidth(),img.getHeight()), 0, 0, 0, 0 );
	      PdfWriter.getInstance( pdfDoc, output);
	
	      pdfDoc.open( );


	      for(int k=1; k<=total; ++k) {
	    	  img = TiffImage.getTiffImage(inFile,k);
              if (img.getPlainWidth() > pdfDoc.getPageSize().getWidth() || img.getPlainHeight() > pdfDoc.getPageSize().getHeight()) {		    	  
            	  img.scaleToFit(pdfDoc.getPageSize().getWidth(),pdfDoc.getPageSize().getHeight());
              }	
	          pdfDoc.add(img);
			if(description !=null && k==1) {
				com.lowagie.text.Paragraph p = new com.lowagie.text.Paragraph(description);
				p.setAlignment(Element.ALIGN_CENTER);
				pdfDoc.add(p);
			}	          
	          pdfDoc.newPage( );
	    	  
	      }
		  inFile.close();    
	      pdfDoc.close( );
	 }		
	
	public void exportPdfFile(final String path, final OutputStream outputStream,final int pageNumber,final int pageCount ) {

		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 
        		PdfCopy copy = null;
        		Document doc = null;
        		PdfOutline root = null;
        		PdfContentByte cb =null;
        		int pageCount = 1;
        		try {

        	       		QueryManager queryManager = session.getWorkspace().getQueryManager();
    	        		String queryString = "select * from [nt:file] AS s WHERE ISDESCENDANTNODE(["+path+"])";
    	        		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
    	        		

    	        		QueryResult result = q.execute();
    	        		NodeIterator it = result.getNodes();
    	        		//RowIterator rows = result.getRows();
    	        		
    	        		while(it.hasNext()) {
    	        			//Row row = (Row) rows.next();
    	        			Node node = it.nextNode();
    	        			if(node.getName().equals("defaultpdf")) {//do not export default pdf
    	        				continue;
    	        			}
    	        			//Node pdf = node.getNode("pdf");
    	                    //Node jcrNode = node.getNode("jcr:content");
    	                    InputStream inputStream = JcrUtils.readFile(node);//jcrNode.getProperty("jcr:data").getBinary().getStream();
    						PdfReader reader = new PdfReader(inputStream);
    						
    						if (doc ==null) {
    							doc = new Document(reader.getPageSizeWithRotation(1));

    							copy = new PdfCopy(doc, outputStream);
    							doc.open();
    							cb = copy.getDirectContent();
    							pageCount = copy.getCurrentPageNumber();
    							root = cb.getRootOutline();
    						

    						}
    				        int numPages = reader.getNumberOfPages();
    				        PdfImportedPage pdfPage = null;
    				        String pathPath = getAncestorPath(node.getPath(), 8);
    				        if(pathPath.indexOf("/pageses/page")>0) {
        				        Page page = (Page)getObject(pathPath);
        			    		String title = page.getTitle();
        			    		new PdfOutline(root, 
        								PdfAction.gotoLocalPage(pageCount, new PdfDestination(PdfDestination.FIT), copy),StringEscapeUtils.escapeXml(title));
    				        	
    				        }

    				        if(pageNumber <=0 || pageNumber>numPages) {
	    				        for(int j=1; j<=numPages;j++) {
	    					        pdfPage = copy.getImportedPage(reader, j);
	    					        copy.addPage(pdfPage);
	    					    }
    				        }else {
	    				        for(int j=pageNumber; j<=numPages && j<pageCount+pageNumber;j++) {   				        	
	    					        pdfPage = copy.getImportedPage(reader, j);
	    					        copy.addPage(pdfPage); 
	    				        }
    				        }
    				        pageCount +=numPages;
    				        inputStream.close();
    	        		}   				


        		} catch (DocumentException e) {
        			throw new IOException(e.getMessage());
				}finally {
        			if(doc != null)
        				doc.close();
        			if(outputStream!=null) {
        				outputStream.flush();
        				outputStream.close();
        			}
        		}

	            return null;
                
        	} 		
		});

	}
	
	
	public void readAsset(final String path, final HttpServletResponse response) {
		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException {
        		Node node = getNode(path);
        		JcrUtils.readFile(node, response.getOutputStream());
        		return null;
        	}
		});
	}
	
	public void readAsset(final String path, final OutputStream output) {
		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException {
        		Node node = getNode(path);
        		JcrUtils.readFile(node, output);
        		return null;
        	}
		});
	}
	
	

	public void createFile(final String path, final Integer x) {
		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException {
        		BufferedImage image = null;
                String ext = getProperty(path,"ext");
                if(path.lastIndexOf(".")>0) 
                	ext = path.substring(path.lastIndexOf("."));

        		File file = new File(getFile(path),"origin"+ext);
        		if(!file.exists()) {
        			file = new File(getBackup()+path+"/origin"+ext);
        		}
        		
        		if(file !=null && file.exists()) {
        			image = ImageIO.read(file);
        		}else {
            		image = ImageIO.read(JcrUtils.readFile(getNode(path+"/original")));        			
        		}
        		//updatePropertyByPath(path, "updated", "true");

        		if(image.getWidth() < x) {
        			return null;
        		}
        		BufferedImage resizedImg = scaleBufferedImage(image,x);
    			//ByteArrayOutputStream os = new ByteArrayOutputStream();
        		//File iconfile = new File(device+path+"/x"+x+".jpg");
        		//if(!iconfile.exists()) iconfile.createNewFile();
        		OutputStream os = new FileOutputStream(getFile(path)+"/x"+x+".jpg");
    			ImageIO.write(resizedImg, "jpeg", os);
    			//updatePropertyByPath(path, "icon", device+path+"/x"+x+".jpg");
    			os.close();
    			
       			//InputStream is = new ByteArrayInputStream(os.toByteArray());
    			//String mineType = node.getParent().getProperty("contentType").getString();
    			//addFile(path,"file-"+x,is,"image/jpeg");
        		return null;
        	}
		});

		
	}


	public void createIcon(final String path, final Integer w,final Integer h) {
		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException {
        		File file = getFile(path);
        		if(file !=null && file.exists()) {
        			String infile = file.getAbsolutePath();
        			String ext = getProperty(path,"ext");
        			if(infile.indexOf(".") >0)
        				ext = infile.substring(infile.lastIndexOf("."));
        			if(file.isDirectory()) {
        				infile +="/origin"+ ext;
        			}
        			String outfile =device+ path+"/x"+w+".jpg";
        			File out = new File(outfile);
        			if(!out.getParentFile().exists()) {
        				out.getParentFile().mkdirs();
        			}

    				int exit = LinuxUtil.opencvResize(infile, outfile, w, h);
    				if(exit != 0)
    					exit =  LinuxUtil.convert(infile, outfile, w, h);
    				if(exit==0) {
    					//updatePropertyByPath(path, "icon", outfile);
    					//updatePropertyByPath(path, "updated", "true");
    				} else {
						createFile(path,w);							
					}

        		}
        		return null;
        	}
		});

		
	}
	
	public void updateFolderIcon(final String path) {
		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException {
        		
        		String assetsQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+path+"])" +" and s.delete not like 'true' and s.contentType like 'image%' and s.ocm_classname='com.filemark.jcr.model.Asset' order by s.lastModified desc, s.name";
        		WebPage<Asset> assets = searchAssets(assetsQuery, 4, 0);


        	    BufferedImage resizedImg = new BufferedImage(360, 360, BufferedImage.TYPE_INT_RGB);
        	    Graphics2D g2 = resizedImg.createGraphics();
        	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        	    Node folder = getNode(path);
        	    File file360 = new File(device+"/assets/templates/folder360x360.png");
        	    if(file360.isDirectory()) file360 = new File(device+"/assets/templates/folder360x360.png/origin.png");
        	    if(file360.exists()) {
        	    	BufferedImage folderImage = ImageIO.read(file360);
        	    	g2.drawImage(folderImage, 0, 0, null);
        	    }else {
        	    	log.info("/assets/templates/folder360x360.png not found!");
        	    	return null;
        	    }
    	    	log.debug("image found:"+path+"="+assets.getPageCount());

        	    int i = 0, x=30, y=90;
        	    for(Asset asset : assets.getItems()) {
        	    	BufferedImage image = null;
        	    	String ext = asset.getExt();
        	    	File file = getFile(asset.getPath());
        	    	if(file.isDirectory()) file = new File(file,"origin"+ext);
        	    	File icon = new File(device+asset.getPath()+"/x400.jpg");
        	    	if(!icon.exists()) {
        	    		icon = new File(backup+asset.getPath()+"/x400.jpg");
        	    	}
        	    	if(icon.exists()) {
         	    		image = ImageIO.read(icon);
        	    	}else if(file !=null && file.exists()) {
        	    		image = ImageIO.read(file);
        	    	}else if(session.nodeExists(asset.getPath()+"/original")){
            	    	Node node = getNode(asset.getPath()+"/original");
            	    	image = ImageIO.read(JcrUtils.readFile(node));        	    		
        	    	}
        	    	if(image!=null) {
	        	    	BufferedImage imageResize = scaleBufferedImage(image,150);
	        	    	if(imageResize.getHeight() + y>310)
	        	    		y = 310 - imageResize.getHeight();
	        	    	g2.drawImage(imageResize, x, y, null);
	        	    	i++;
	        	    	if(i==1) {
	        	    		x = 180; y = 90;
	        	    	}else if(i==2) {
	        	    		x = 30; y = 200;
	        	    	}else {
	        	    		x = 180; y = 200;
	        	    	}
        	    	}
        	    }
        	    
        	    g2.dispose();  
        	    //resizedImg.getGraphics().drawImage(image, 0, 0, null);
    			ByteArrayOutputStream os = new ByteArrayOutputStream();
    			ImageIO.write(resizedImg, "jpeg", os);
       			InputStream is = new ByteArrayInputStream(os.toByteArray());
    			//String mineType = node.getParent().getProperty("contentType").getString();
    			addFile(path,"original",is,"image/jpeg");
        	    folder.setProperty("lastUpdated", Calendar.getInstance());
				folder.setProperty("changed", "false");
    			//log.info(path +" updated!");
        	    is.close();
    			session.save();
        		return null;
        	}
		});

		
	}
	
	public void roateImage(final String path,final double angle) {
		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException {
        	BufferedImage image = null;
        	String ext = path.substring(path.lastIndexOf("."));
        	File file = new File(getFile(path),"origin"+ext);
        	if(file.exists()) {
        		image = ImageIO.read(file);
        	}else {
        		Node node = getNode(path+"/original");	
				image = ImageIO.read(JcrUtils.readFile(node));        		
        	}
			double rads = Math.toRadians(angle);
			int w = image.getWidth();
			int h = image.getHeight();
			double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
			int newwidth = (int)Math.floor(w * cos +h * sin);
			int newheight = (int)Math.floor(h * cos +w * sin);
			BufferedImage imageRotated = new BufferedImage(newwidth,newheight,BufferedImage.TYPE_INT_RGB);
			AffineTransform trans = new AffineTransform();
			trans.translate((newwidth - w)/2, (newheight-h)/2);
			
			int x = w/2;
			int y = h/2;
			trans.rotate(rads,x,y);
			Graphics2D g2 =  imageRotated.createGraphics();
    	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.setTransform(trans);
			g2.drawImage(image,0,0, null);
			g2.dispose();  

			//String mineType = node.getParent().getProperty("contentType").getString();
			if(file.exists()) {
				
				ImageIO.write(imageRotated, "jpeg", new FileOutputStream(file));
			}else {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(imageRotated, "jpeg", os);
	   			InputStream is = new ByteArrayInputStream(os.toByteArray());				
	   			addFile(path,"original",is,"image/jpeg");	
	   			is.close();
	   			os.close();
			}

			Node asset = getNode(path);
			asset.setProperty("contentType", "image/jpeg");
			//log.info(path +" updated!");
    	    
			session.save();			
			return null;
        	}
		});
		
	}

	public void autoRoateImage(final String path) {
		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException {
        	BufferedImage image = null;
        	File file = getFile(path);
        	
        	int width,height; 
            Node node = getNode(path);
            String ext = getProperty(path,"ext");
            if(path.lastIndexOf(".")>0)
            		ext= path.substring(path.lastIndexOf("."));
            
      	    String infile = file.getAbsolutePath()+"/origin"+ext;
      	    file = new File(file,"origin"+ext);
	        Metadata metadata = null;
			try {
				String sorientation=LinuxUtil.oreintation(infile);
				String position = LinuxUtil.getPosition(infile);
				if(position.indexOf("Warning")<0) {
					updateProperty(node.getIdentifier(),"position", position);					
				}

				log.debug("linux orientation="+sorientation+",position="+position);
				if("2,3,4,5,6,7".indexOf(sorientation)>=0) {
					if(!"".equals(sorientation) && (LinuxUtil.opencvRotate(infile, infile, Integer.parseInt(sorientation) - 1) == 0 || LinuxUtil.autoRotate(infile, infile)==0)) {
						String wxh=LinuxUtil.getWidthxHeight(infile);
						log.debug("linux wxh="+wxh);
						if(wxh.indexOf("x")>0) {
							String w_h[] = wxh.split("x");
							updateProperty(node.getIdentifier(),"width", Long.parseLong(w_h[0]));
			                updateProperty(node.getIdentifier(),"height", Long.parseLong(w_h[1]));  		
			                session.save();		                	
		                	return null;  
						}
					}
				}else {
					String wxh=LinuxUtil.getWidthxHeight(infile);
					log.debug("linux wxh="+wxh);
					if(wxh.indexOf("x")>0) {
						String w_h[] = wxh.split("x");
						updateProperty(node.getIdentifier(),"width", Long.parseLong(w_h[0]));
		                updateProperty(node.getIdentifier(),"height", Long.parseLong(w_h[1]));  		
		                session.save();		                	
	                	return null;  
					}
				}
				
	        	if(file != null && file.exists()) {
	        		image = ImageIO.read(file);
	        	} else {
	        		Node filenode = getNode(path+"/original");
					image = ImageIO.read(JcrUtils.readFile(filenode));        		
	        	}
                width = image.getWidth();
                height = image.getHeight();
				if(file != null && file.exists())
					metadata = ImageMetadataReader.readMetadata(file);
				else {
					Node imagenode = getNode(path+"/original");
					metadata = ImageMetadataReader.readMetadata(JcrUtils.readFile(imagenode));
				}
				if (metadata==null) return null;
    	        ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
    	        if(exifIFD0Directory == null) return null;
    	        //JpegDirectory jpegDirectory = (JpegDirectory) metadata.getDirectoriesOfType(JpegDirectory.class);
    	        int orientation = 1;
    	        if(!exifIFD0Directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
    	        	return null;
    	        }
    	        orientation = exifIFD0Directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            	AffineTransform affineTransform = new AffineTransform();
                //int height = jpegDirectory.getImageHeight();
                //log.debug("Orientation="+orientation);
                if(orientation == 1 || orientation >7) {
					updateProperty(node.getIdentifier(),"width", image.getWidth());
	                updateProperty(node.getIdentifier(),"height", image.getHeight());   
	                //session.save();		                	
                	return null;
                }
                
/*				if(ImageUtil.autoRotate(infile, infile)==0) {
	        		image = ImageIO.read(file);

					node.setProperty("width", image.getWidth());
	                node.setProperty("height", image.getHeight());   
	                session.save();		
	                return null;
				}*/

                switch (orientation) {
                case 1:
                	
                    break;
                case 2: // Flip X
                    affineTransform.scale(-1.0, 1.0);
                    affineTransform.translate(-width, 0);
                    break;
                case 3: // PI rotation
                    affineTransform.translate(width, height);
                    affineTransform.rotate(Math.PI);
                    height = width;
                    width = image.getHeight();
                    break;
                case 4: // Flip Y
                    affineTransform.scale(1.0, -1.0);
                    affineTransform.translate(0, -height);
                    break;
                case 5: // - PI/2 and Flip X
                    affineTransform.rotate(-Math.PI / 2);
                    affineTransform.scale(-1.0, 1.0);
                    break;
                case 6: // -PI/2 and -width
                    affineTransform.translate(height, 0);
                    affineTransform.rotate(Math.PI / 2);
                    break;
                case 7: // PI/2 and Flip
                    affineTransform.scale(-1.0, 1.0);
                    affineTransform.translate(-height, 0);
                    affineTransform.translate(0, width);
                    affineTransform.rotate(3 * Math.PI / 2);
                    break;
                case 8: // PI / 2
                    affineTransform.translate(0, width);
                    affineTransform.rotate(3 * Math.PI / 2);
                    break;
                default:
                	
                    break;
                }  
                if(orientation >1) {
    		        AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BILINEAR);  
    		        BufferedImage destinationImage = new BufferedImage(height,width, image.getType());
    		        destinationImage = affineTransformOp.filter(image, destinationImage);
    		        if(file != null && file.exists()) {
    		        	ImageIO.write(destinationImage, "jpg", file);
    		        }else {
    					ByteArrayOutputStream os = new ByteArrayOutputStream();
    					ImageIO.write(destinationImage, "jpeg", os);
    		   			InputStream is = new ByteArrayInputStream(os.toByteArray());				
    		   			addFile(path,"original",is,"image/jpeg");
    		   			is.close();
    		        }                	
                }

                updateProperty(node.getIdentifier(),"width", width);
                updateProperty(node.getIdentifier(),"height", height);   
                session.save();		        
			} catch (ImageProcessingException e) {
				log.error(e.getMessage());
			} catch (MetadataException e) {
				log.error(e.getMessage());
			} catch (InterruptedException e) {
				log.error(e.getMessage());
			}

			return null;
        	}
		});
		
	}
	private BufferedImage scaleBufferedImage(BufferedImage image,int x) {
   	    int finalw = x;
	    int finalh = x;
	    double factor = 1.0d;
	    if(image.getWidth() > x ){
	        factor = ((double)x/(double)image.getWidth());
	        finalw = (int)(image.getWidth() * factor);
	        finalh = (int)(image.getHeight() * factor);	    		
	    }
	    
	    if(finalh>x) {
	        factor = ((double)x/(double)finalh);
	        finalw = (int)(finalw * factor);
	        finalh = (int)(finalh * factor);
	    }           		
	    java.awt.Image scaledImage = image.getScaledInstance(finalw, finalh, java.awt.Image.SCALE_SMOOTH);
		
	    BufferedImage resizedImg = new BufferedImage(finalw, finalh, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g2 = resizedImg.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(scaledImage, 0, 0, null);
	    g2.dispose();  
		
		return resizedImg;
	}
    /**
     * Since some methods like toolkit.getImage() are asynchronous, this
     * method should be called to load them completely.
     */
    @SuppressWarnings("unused")
	private void loadCompletely (java.awt.Image img)
    {
        MediaTracker tracker = new MediaTracker(new JPanel());
        tracker.addImage(img, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
	
	@SuppressWarnings("unchecked")
	public WebPage<Page> queryPages(final String queryString, final long limit, final long offset) {
		return (WebPage<Page>) jcrTemplate.execute(new JcrCallback() { 
        	public WebPage <Page>doInJcr(Session session) throws RepositoryException, IOException { 
        		QueryManager queryManager = session.getWorkspace().getQueryManager();
        		//ObjectContentManager ocm = new  ObjectContentManagerImpl(session, mapper);        		
        		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
        		
        		QueryResult result = q.execute();
        		long totalCount = result.getNodes().getSize();
        		q.setLimit(limit);
        		q.setOffset(offset*limit);
        		result = q.execute();

        		List<Page> pages = new ArrayList<Page>();
        		NodeIterator it = result.getNodes();
        		
        		while(it.hasNext()) {
        			Node node = it.nextNode();
            		Page page = new Page();
            		page.setUid(node.getIdentifier());
            		page.setPath(node.getPath());
            		page.setName(node.getName());
            		if(node.hasProperty("jcr:title"))
            			page.setTitle(node.getProperty("jcr:title").getString());
            		else
            			page.setTitle(node.getName());
            		page.setHasNodes(node.hasNodes());
            		if(node.hasProperty("description"))
            			page.setDescription(node.getProperty("description").getString());
            		if(node.hasProperty("status"))
            			page.setStatus(node.getProperty("status").getString());
            		if(node.hasProperty("lastPublished"))
            			page.setLastPublished(node.getProperty("lastPublished").getDate().getTime());
          			page.setParent(node.getParent().getProperty("jcr:title").getString());
          		           		

       			
            		pages.add(page);
        		}
        		WebPage<Page> webPage = new WebPage<Page>((int)offset,(int)limit,totalCount,pages);
        		
                return webPage;
        	} 		
		});
		
	}
	@SuppressWarnings("unchecked")
	public WebPage<Page> queryPageContent(final String queryString, final long limit, final long offset) {
		return (WebPage<Page>) jcrTemplate.execute(new JcrCallback() { 
        	public WebPage <Page>doInJcr(Session session) throws RepositoryException, IOException { 
        		QueryManager queryManager = session.getWorkspace().getQueryManager();
        		new  ObjectContentManagerImpl(session, mapper);        		
        		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
        		
        		QueryResult result = q.execute();
        		long totalCount = result.getNodes().getSize();
        		q.setLimit(limit);
        		q.setOffset(offset*limit);
        		result = q.execute();

        		List<Page> pages = new ArrayList<Page>();
        		NodeIterator it = result.getNodes();
        		
        		while(it.hasNext()) {
        			Node node = it.nextNode();
        			Page page = getPage(node.getPath());
            		pages.add(page);
        		}
        		WebPage<Page> webPage = new WebPage<Page>((int)offset,(int)limit,totalCount,pages);
        		
                return webPage;
        	} 		
		});
		
	}
	@SuppressWarnings("unchecked")
	public WebPage<Folder> queryFolders(final String queryString, final long limit, final long offset) {
		return (WebPage<Folder>) jcrTemplate.execute(new JcrCallback() { 
        	public WebPage <Folder>doInJcr(Session session) throws RepositoryException, IOException { 
        		QueryManager queryManager = session.getWorkspace().getQueryManager();
        		//ObjectContentManager ocm = new  ObjectContentManagerImpl(session, mapper);        		
        		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
        		
        		QueryResult result = q.execute();
        		long totalCount =result.getRows().getSize();// result.getNodes().getSize();
        		q.setLimit(limit);
        		q.setOffset(offset*limit);
        		result = q.execute();

        		List<Folder> folders = new ArrayList<Folder>();
        		//NodeIterator it = result.getNodes();
        		RowIterator it = result.getRows();
        		while(it.hasNext()) {
        			//Node node = it.nextNode();
        			final Row row = it.nextRow(); 
        			Node node = session.getNode(row.getPath("s"));
        			//String ocm_classname= node.hasProperty("ocm_classname")?node.getProperty("ocm_classname").getString():"Folder";

            			Folder folder = new Folder();
            			folder.setUid(node.getIdentifier());
            			folder.setPath(node.getPath());
        				folder.setNodeName(node.getName());                        
            			folder.setTitle(getTitle(node));
            			folders.add(folder);
            			folder.setHasNodes(node.hasNodes());
            			if(node.hasProperty("ocm_classname"))
            				folder.setOcm_classname(node.getProperty("ocm_classname").getString());
            			else
            				folder.setOcm_classname("Folder");
            			if(node.hasProperty("url")) {
            				folder.setPath(node.getProperty("url").getString());
            				folder.setOcm_classname("Query");
            			}
            			if(node.hasProperty("orderby")) {
            				folder.setOrderby(node.getProperty("orderby").getString());
            			}
            			if(node.hasProperty("createdBy")) {
            				folder.setCreatedBy(node.getProperty("createdBy").getString());
            			}            			
                        if(node.hasProperty("intranet"))
                       	 folder.setIntranet(node.getProperty("intranet").getString());   
                        if(node.hasProperty("resolution"))
                       	 folder.setResolution(node.getProperty("resolution").getString());   
        			

        		}
        		WebPage<Folder> webPage = new WebPage<Folder>((int)offset,(int)limit,totalCount,folders);
        		
                return webPage;
        	} 		
		});
		
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public WebPage<Chat> queryChats(final String queryString, final long limit, final long offset) {
		return (WebPage<Chat>) jcrTemplate.execute(new JcrCallback() { 
        	public WebPage <Chat>doInJcr(Session session) throws RepositoryException, IOException { 
        		QueryManager queryManager = session.getWorkspace().getQueryManager();
        		ObjectContentManager ocm = new  ObjectContentManagerImpl(session, mapper);        		
        		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
        		
        		QueryResult result = q.execute();
        		long totalCount = result.getNodes().getSize();
        		q.setLimit(limit);
        		q.setOffset(offset*limit);
        		result = q.execute();

        		List<Chat> chats = new ArrayList<Chat>();
        		NodeIterator it = result.getNodes();
        		
        		while(it.hasNext()) {
        			Node node = it.nextNode();
        			Chat chat = (Chat)ocm.getObject(node.getPath());
        			chats.add(0,chat);
        		}
        		WebPage<Chat> webPage = new WebPage<Chat>((int)offset,(int)limit,totalCount,chats);
        		
                return webPage;
        	} 		
		});
	}


	@SuppressWarnings("unchecked")
	public WebPage<Object> queryObject(final String queryString, final long limit, final long offset) {
		return (WebPage<Object>) jcrTemplate.execute(new JcrCallback() { 
        	public WebPage <Object>doInJcr(Session session) throws RepositoryException, IOException { 
        		QueryManager queryManager = session.getWorkspace().getQueryManager();
        		ObjectContentManager ocm = new  ObjectContentManagerImpl(session, mapper);        		
        		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
        		
        		QueryResult result = q.execute();
        		long totalCount = result.getNodes().getSize();
        		q.setLimit(limit);
        		q.setOffset(offset*limit);
        		result = q.execute();

        		List<Object> objects = new ArrayList<Object>();
        		NodeIterator it = result.getNodes();
        		
        		while(it.hasNext()) {
        			Node node = it.nextNode();
        			objects.add(ocm.getObject(node.getPath()));
        		}
        		WebPage<Object> webPage = new WebPage<Object>((int)offset,(int)limit,totalCount,objects);
        		
                return webPage;
        	} 		
		});
		
	}

	

	public Node addFile(final String path, final String name, final InputStream data, final String mimeType) {
		return (Node) jcrTemplate.execute(new JcrCallback() { 
        	public Node doInJcr(Session session) throws RepositoryException, IOException { 
        		Node node = session.getNode(path);
        		JcrUtils.putFile(node, name, mimeType, data, Calendar.getInstance());
        		node.setProperty("jcr:lastModified",Calendar.getInstance());
                session.save();
	            return node;
        	} 		
		});
		
	
	}

	

	public Node addProperty(final String path,final String name, final String content) {
		return (Node) jcrTemplate.execute(new JcrCallback() { 
        	public Node doInJcr(Session session) throws RepositoryException, IOException { 
        		Node node = session.getNode(path);
        		node.setProperty(name, content);
				updateProperty(node.getIdentifier(),name, content);
                session.save();
	            return node;
        	} 		
		});
		
	
	}

	public String getAncestorPath(final String path,final int level) {
		return (String) jcrTemplate.execute(new JcrCallback() { 
        	public String doInJcr(Session session) throws RepositoryException, IOException { 
        		Node node = session.getNode(path);
	            return node.getAncestor(level).getPath();
        	} 		
		});
		
	
	}

	
	public String getProperty(final String path,final String name) {
		return (String) jcrTemplate.execute(new JcrCallback() { 
        	public String doInJcr(Session session) throws RepositoryException, IOException { 
        		String value=null;
        		if(session.nodeExists(path)) {
        			Node node = session.getNode(path);
        			if(node.hasProperty(name)) {
        				value = node.getProperty(name).getValue().getString();
        			}
        		}

	            return value;
        	} 		
		});		
	}

	public void setProperty(final String path,final String name,final String value) {
		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 

        		if(session.nodeExists(path)) {
        			Node node = session.getNode(path);
        			node.setProperty(name, value);
    				updateProperty(node.getIdentifier(),name, value);        			
        			session.save();
        		}

        		return null;
        	} 		
		});		
	}
	

	public void setProperty(final String path,final String name,final Long value) {
		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 

        		if(session.nodeExists(path)) {
        			Node node = session.getNode(path);
        			node.setProperty(name, value);
    				updateProperty(node.getIdentifier(),name, value);        			
        			session.save();
        		}

        		return null;
        	} 		
		});		
	}

	public String uploadAssets(final String path, final ScanUploadForm uploadForm, final String username)
			throws RepositoryException {
			
		return (String)jcrTemplate.execute(new JcrCallback() { 
	    	public String doInJcr(final Session session) throws RepositoryException, IOException { 
	    		String result="";	
	    		for (MultipartFile multipartFile : uploadForm.getFile()) {
	        		String fileName = multipartFile.getOriginalFilename();
	        		if(fileName==null || "".equals(fileName)) {
	        			fileName = uploadForm.getFilename();
	        		}
	        		fileName = fileName.toLowerCase();
	        		String ext=null,nodeName=null;
	        		if(fileName.lastIndexOf(".")>0) {
	        			ext = fileName.substring(fileName.lastIndexOf(".")+1);
	        			nodeName = fileName.substring(0,fileName.lastIndexOf("."));
	        			//nodeName = nodeName.replaceAll("/0", "/f0").replaceAll("[\\:\\]\\[\\|\\*\\.]", "").replaceAll("[ ?]", "-").toLowerCase();
	        			
	        		}else {
	        			nodeName = fileName;
	        		}
	        		String decode_fileName = new String(fileName.getBytes("ISO-8859-1"), "UTF-8");
	        		Asset asset = new Asset();
	        		asset.setName(decode_fileName);

	        		asset.setCreatedBy(username);
	        		String assetPath = getValidPath(path+"/"+nodeName)+"."+ext;
	        		Node newAssetNode = JcrUtils.getOrCreateUniqueByPath(assetPath, "nt:unstructured", session);
	        		assetPath = newAssetNode.getPath();
	        		newAssetNode.remove();
	        		asset.setPath(assetPath);
	        		asset.setLastModified(new Date());
	        		asset.setContentType(multipartFile.getContentType());
	        		add(asset);
        			try {
	    					addFile(assetPath,"original",multipartFile.getInputStream(),multipartFile.getContentType());
							result = asset.getPath();
							Node folder = getNode(path);
							folder.setProperty("lastModified", Calendar.getInstance());
							folder.setProperty("changed", "true");
							session.save();
						} catch (Exception e) {
							result +=fileName+" error:"+e.getMessage()+",";
						}
	    		}
	         return result;
	    	} 
	    });		
	}

	public Asset importAsset(final String path, final String url, final String username)
			throws RepositoryException {
			
		return (Asset)jcrTemplate.execute(new JcrCallback() { 
	    	public Asset doInJcr(final Session session) throws RepositoryException, IOException { 
        		Asset asset = new Asset();	
	    		String nodeName = url.substring(url.lastIndexOf("/")+1, url.length());
                URL url_img = new URL(url);
                URLConnection uc = url_img.openConnection();
                
            	String mineType = uc.getContentType();
            	InputStream is = uc.getInputStream();
            	asset.setTitle(nodeName);	
            	asset.setName(nodeName);
        		asset.setCreatedBy(username);
        		String assetPath = getValidPath(path+"/"+nodeName);
        		Node newAssetNode = JcrUtils.getOrCreateUniqueByPath(assetPath, "nt:unstructured", session);
        		assetPath = newAssetNode.getPath();
        		newAssetNode.remove();
        		asset.setPath(assetPath);
        		asset.setLastModified(new Date());
        		asset.setContentType(mineType);
        		add(asset);
        		try {
					String result = LinuxUtil.add(asset);
					log.debug(result);
				} catch (InterruptedException e1) {
					log.error("importAsset:"+e1.getMessage());
				}
    			try {
    					addFile(assetPath,"original",is,mineType);
						Node folder = getNode(path);
						folder.setProperty("lastModified", Calendar.getInstance());
						folder.setProperty("changed", "true");
						session.save();
					} catch (Exception e) {
						log.error(e.getMessage());
						asset.setTitle("error:"+e.getMessage());
					}

	         return asset;
	    	} 
	    });		
	}
	
	public String updateProperty(final String uid,final String name,final long value)
			throws RepositoryException {
		return (String) jcrTemplate.execute(new JcrCallback() { 
        	public String doInJcr(Session session) throws RepositoryException, IOException {
        		Node node = session.getNodeByIdentifier(uid);
        		node.setProperty(name, value);
        		session.save();
        		LinuxUtil.updateProperty(node.getIdentifier(), name,value); 		
        		return node.getPath();
        	};
		});
	}

	
	public String updateProperty(final String uid,final String name,final String value)
			throws RepositoryException {
		return (String) jcrTemplate.execute(new JcrCallback() { 
        	public String doInJcr(Session session) throws RepositoryException, IOException {
        		Node node = session.getNodeByIdentifier(uid);
        		node.setProperty(name, value);
        		if(name.equals("passcode")) {
            		NodeIterator it = node.getNodes();
            		
            		while(it.hasNext()) {
            			Node child = it.nextNode();
            			updateProperty(child.getIdentifier(),name, value);
            		}
        		}
        		node.setProperty("lastModified", Calendar.getInstance());
        		node.getParent().setProperty("lastModified", Calendar.getInstance());        		
        		if(name.equals("status") && value.equals("true")) {
        			node.setProperty("lastPublished", Calendar.getInstance());
        			node.setProperty("breadcrumb", getBreadcrumb(node.getPath()));
        			//setPageNavigation(node.getPath(),2,20);
        		}

        		session.save();
        		LinuxUtil.updateProperty(node.getIdentifier(), name,value); 
        		return node.getPath();
        	};
		});

	}

	public String updatePropertyByPath(final String path,final String name,final String value)
			throws RepositoryException {
		return (String) jcrTemplate.execute(new JcrCallback() { 
        	public String doInJcr(Session session) throws RepositoryException, IOException {
        		Node node = session.getNode(path);
        		node.setProperty(name, value);
        		node.setProperty("lastModified", Calendar.getInstance());
        		node.getParent().setProperty("lastModified", Calendar.getInstance()); 
        		if(name.equals("status") && value.equals("true")) {
        			node.setProperty("lastPublished", Calendar.getInstance());
        			node.setProperty("breadcrumb", getBreadcrumb(node.getPath()));
        			//setPageNavigation(node.getPath(),2,20);
        		}
        		session.save();
        		LinuxUtil.updateProperty(node.getIdentifier(), name,value);
        		return node.getPath();
        	};
		});

	}
	
	
	@Override
	public void updateCalendar(final String path, final String name) {
		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException {
        		Node node = session.getNode(path);
        		Calendar calendar = Calendar.getInstance();
       			node.setProperty(name, calendar);
        		session.save();
        		LinuxUtil.updateProperty(node.getIdentifier(), name,calendar.getTime().getTime()); 
	
				return node;
        	};
        	
		});
	}

	@Override
	public void updateCalendar(final String path, final String name, final Calendar calendar) {
		jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException {
        		Node node = session.getNode(path);
       			node.setProperty(name, calendar);
        		session.save();

        		LinuxUtil.updateProperty(node.getIdentifier(), name,calendar.getTime().getTime()); 
        		
				return node;
        	};
        	
		});
	}	
/*	@Override
	public String extractWord(final String path) {
		return (String)jcrTemplate.execute(new JcrCallback() { 
        	public String doInJcr(Session session) throws RepositoryException, IOException {
        		Node node = session.getNode(path+"/original");
        		InputStream in = JcrUtils.readFile(node);
    		    AutoDetectParser parser = new AutoDetectParser();
    		    BodyContentHandler handler = new BodyContentHandler();
    		    Metadata metadata = new Metadata();
    		    try {
					parser.parse(in, handler, metadata);
				} catch (SAXException e) {
					log.error(e.getMessage());
				} catch (TikaException e) {
					log.error(e.getMessage());
				}
    		    return handler.toString();
        	};
        	
		});
	}*/
	
	public long getCount(final String queryString) {
		return (Long) jcrTemplate.execute(new JcrCallback() { 
        	public Object doInJcr(Session session) throws RepositoryException, IOException { 

        		QueryManager queryManager = session.getWorkspace().getQueryManager();
        		
        		QueryImpl q = (QueryImpl)queryManager.createQuery(queryString, Query.JCR_SQL2);
        		
        		QueryResult result = q.execute();
        		
                return result.getNodes().getSize();
        	} 		
		});
		
	}
	
	public String move(final String frompath,final String topath,final String username) {
		return (String) jcrTemplate.execute(new JcrCallback() { 
        	public String doInJcr(Session session) throws RepositoryException, IOException { 
        		Node node = session.getNode(frompath);
        		String parent_path = node.getParent().getPath();
        		String toparent = topath.substring(0, topath.lastIndexOf("/"));
        		if(!session.nodeExists(toparent)) {
        			addNodes(toparent,"nt:unstructured",username);
        		}
        		session.move(frompath,topath);
        		Node parent = session.getNode(parent_path);
        		if(parent.getNodes().getSize()==0) {
        			parent_path = parent.getParent().getPath();
        			parent.remove();
        		}
        		session.save();
        		return node.getIdentifier();
        	} 		
		});		
	}

	public String archive(final String frompath) {
		return (String) jcrTemplate.execute(new JcrCallback() { 
        	public String doInJcr(Session session) throws RepositoryException, IOException { 
        		String topath = frompath.indexOf("applications")>0?frompath.replaceFirst("applications", "archives"):frompath.replaceFirst("archives", "applications");
        		if(session.nodeExists(topath)) {
        			throw new RepositoryException(topath +" exists");
        		}
        		String parent_path = topath.substring(0, topath.lastIndexOf("/"));
        		if(!session.nodeExists(parent_path)) {
        			Node source = session.getNode(frompath);
        			String names = getNames(source.getParent());
        			if(frompath.indexOf("applications")>0) 
        				names = names.replaceFirst("applications", "archives");
        			else 
        				names = names.replaceFirst("archives", "applications");
        			addNodes(names,"nt:unstructured","fmdba");
        		}
        		session.move(frompath, topath);
        		Node parent = session.getNode(parent_path);
        		
        		if(parent.getNodes().getSize()==0) {
        			parent_path = parent.getParent().getPath();
        			parent.remove();
        		}
        		session.save();
        		return parent_path;
        	} 		
		});		
	}
	
	private String  getNames(Node node) throws AccessDeniedException, PathNotFoundException, ConstraintViolationException, VersionException, ItemExistsException, LockException, RepositoryException {
		String names = "";
		if(node.hasNode("name"))
			names = getTitle(node);
		else
			names = node.getName();
		while(node.getDepth()>2) {
			node = node.getParent();
			names = getTitle(node)+"/"+names;
		}
		return Folder.root+"/"+names;
	}
	
	public Mapper getMapper() {
		return mapper;
	}

	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	public JcrTemplate getJcrTemplate() {
		return jcrTemplate;
	}

	public void setJcrTemplate(JcrTemplate jcrTemplate) {
		this.jcrTemplate = jcrTemplate;
	}
	
	private String getTitle(Node node) throws ValueFormatException, PathNotFoundException, RepositoryException {
		String title;
		if(node.hasProperty("jcr:title")) {
			title = node.getProperty("jcr:title").getString();
		}else if(node.hasProperty("Title")) {
	        title = node.getProperty("Title").getString();
	     }else {
	  	   title = node.getName();    
	     }
	    return title;
	}



	public String getHome() {
		return home;
	}


	public void setHome(String home) {
		this.home = home;
	}


	public void addUsagelog(final String username, final String message, final String type) {
	    	Log log = new Log(username,message,type);	
	    	String path = log.getPath();
	    	path = path.substring(0, path.lastIndexOf("/"));
		try {
   	    	if(!this.nodeExsits(path))
   	    		this.addNodes(path, "nt:unstructured", username);

   	    	this.add(log);
   	    	Calendar calendar = Calendar.getInstance();
   	    	calendar.add(Calendar.DATE, -keepLog);
   	    	SimpleDateFormat sf =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
   	    	String queryString ="SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE(["+Folder.root+"/logs]) and  s.createdDate < CAST('"+sf.format(calendar.getTime())+"' as DATE) and s.ocm_classname='com.filemark.jcr.model.Log' order by createdDate desc";
   	    	deleteNodes(queryString);
   	    	//WebPage<Log> logs = searchLog(queryString,100,0);
   	    	
   	    	//for(Log delog:logs.getItems()) {
   	    	//	this.deleteNode(delog.getPath());
   	    	//}
		} catch (RepositoryException e) {
			this.log.error(e.getMessage());
		} 
		
	}


	public int getKeepLog() {
		return keepLog;
	}

	public void setKeepLog(int keepLog) {
		this.keepLog = keepLog;
	}


	public String getDomain() {
		return domain;
	}


	public void setDomain(String domain) {
		this.domain = domain;
	}
	

	public String getDevice() {
		return device;
	}


	public void setDevice(String device) {
		this.device = device;
	}


	public String getWorkingDir() {
		return workingDir;
	}

	

	public String getBackup() {
		return backup;
	}


	public void setBackup(String backup) {
		this.backup = backup;
	}


	@Override
	public String getCache() {
		if(cache == null)
			return this.backup+"/cache";
		return cache;
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
		File dir = new File(workingDir);
		if(dir.exists() || dir.mkdirs()) {
			boolean result = (System.setProperty("user.dir", dir.getAbsolutePath()) != null);
			log.debug("Working Dir "+workingDir+" is set:"+result);
		}
/*		String os = System.getProperty("os.name").toLowerCase();
		
		log.debug("loadLibrary opencv core:"+os+","+Core.NATIVE_LIBRARY_NAME);
		try {
			nu.pattern.OpenCV.loadShared();
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		} catch (Exception e) {
			log.error("loadLibrary error:"+e.getMessage());
		}
*/
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}


	public Logger getLog() {
		return log;
	}


	public String getAsianFont() {
		return asianFont;
	}


	public void setAsianFont(String asianFont) {
		this.asianFont = asianFont;
	}

	public File getFile(String path) throws RepositoryException {
		
		Node node = getNode(path);
		if(node.hasProperty("device")) {
			Node deviceNode = getNode(node.getProperty("device").getString());
			if(deviceNode.hasProperty("location")) {
				String location = deviceNode.getProperty("location").getString();
				
				return new File(location+path);
			}
		}
		return null;
		
	}

/*	public class CNElementHandler implements ElementHandler{
		private Paragraph p = new Paragraph();
		public CNElementHandler(Font font) {
			p.setFont(font);
		}
	
		
		public void add(final Writable w) {
			
			if(w instanceof Writable ) {
				List<Element> elements = ((WritableElement) w).elements();
				for(Element element:elements) {
					p.add(element);
				}
			}

		}
		
		public Paragraph getParagraph() {
			return p;
		}
	}*/
}
