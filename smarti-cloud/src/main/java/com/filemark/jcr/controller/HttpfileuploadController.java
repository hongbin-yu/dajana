package com.filemark.jcr.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.filemark.jcr.model.Asset;

@Controller
public class HttpfileuploadController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(SiteController.class);

	private int maxFileSize = 50 * 1024;
	private int maxMemSize = 4 * 1024;
	private String filePath;
    @ExceptionHandler(Exception.class)
    public ModelAndView  handleException(Exception ex,HttpServletRequest request) throws UnsupportedEncodingException {

		String path = URLDecoder.decode(request.getRequestURI(),"UTF-8");
		String paths[] = path.split("\\.");
	    ModelAndView modelAndView = new ModelAndView("error404");
	    String errorcode = request.getParameter("error");
	    String message =  ex.getMessage();
	    modelAndView.addObject("error",message);
	    //modelAndView.addObject("breadcrumb", jcrService.getBreadcrumb(paths[0]));
	    logger.error(ex.toString());
	    return modelAndView;
    }
    
	@RequestMapping(value = {"/httpfileupload/**"}, method = {RequestMethod.PUT},produces = "text/plain;charset=UTF-8")
	public @ResponseBody String htttfileupload(Model model,HttpServletRequest request, HttpServletResponse response) {
		  boolean isMultipart = ServletFileUpload.isMultipartContent(request);  
		  if(!isMultipart) {
			  response.setStatus(403);
		  }
		  filePath = jcrService.getBackup()+"/assets";
		  File file ;
		  String uri = request.getRequestURI();
		  DiskFileItemFactory factory = new DiskFileItemFactory();
	      
	      // maximum size that will be stored in memory
	      factory.setSizeThreshold(maxMemSize);
	   
	      // Location to save data that is larger than maxMemSize.
	      factory.setRepository(new File("/home/device/temp"));

	      // Create a new file upload handler
	      ServletFileUpload upload = new ServletFileUpload(factory);
	   
	      // maximum file size to be uploaded.
	      upload.setSizeMax( maxFileSize );
	      Asset asset= new Asset();
	      String assetPath = filePath+uri;
  		  String ext = "";
  		  if(uri.lastIndexOf(".")>0) {
  			  ext = uri.substring(uri.lastIndexOf("."), uri.length());
  		  }	      
	      try { 
	         // Parse the request to get file items.
	         List<FileItem> fileItems = upload.parseRequest(request);
		
	         // Process the uploaded file items
	         Iterator<FileItem> i = fileItems.iterator();


	   
	         while ( i.hasNext () ) {
	            FileItem fi = (FileItem)i.next();
	            if ( !fi.isFormField () ) {
	               // Get the uploaded file parameters
	               String fieldName = fi.getFieldName();
	               String fileName = fi.getName();
	               String contentType = fi.getContentType();
	               boolean isInMemory = fi.isInMemory();
	               long sizeInBytes = fi.getSize();
	               asset.setExt(ext);
	               asset.setName(fileName);
	               asset.setCreatedBy("openfire");
	               asset.setPath(assetPath);
	       		   asset.setLastModified(new Date());
	       		   asset.setDevice(filePath);
	       		   asset.setSize(sizeInBytes);
	               // Write the file
                   file = new File( filePath +uri +"/origin"+ext) ;

	               fi.write( file ) ;
	        	   jcrService.addOrUpdate(asset);
	        	   jcrService.updateCalendar(filePath +uri,"lastModified");
	        	   jcrService.setProperty(filePath +uri, "changed", "true");
	               logger.debug("Uploaded Filename: " + fileName + "<br>");
	            }
	         }

	         } catch(Exception ex) {
	            logger.error(ex.getMessage());
	            response.setStatus(500);
	         }
	    response.setStatus(200);
		return null;
	}
	
	@RequestMapping(value = {"/httpfileupload/**"}, method = {RequestMethod.GET},produces = "text/plain;charset=UTF-8")
	public @ResponseBody String getHttpfileupload(Model model,HttpServletRequest request, HttpServletResponse response) {
		String uri = request.getRequestURI();
		String ext = "";
		if(uri.lastIndexOf(".")>0) {
		  ext = uri.substring(uri.lastIndexOf("."), uri.length());
		}	   		
		try {
			File outFile = new File(filePath+uri+"/origin"+ext);
			if(outFile.exists() && outFile.isFile()) {
		        long lastModified = outFile.lastModified();
		        long ifModifiedSince = request.getDateHeader("If-Modified-Since");
		        logger.debug("ifModifiedSince="+ifModifiedSince+"/"+lastModified);
		        if (ifModifiedSince != -1 && ifModifiedSince + 1000 <= lastModified) {
					FileInputStream in = new FileInputStream(outFile);
					IOUtils.copy(in, response.getOutputStream());	
					in.close();	
					return null;
		        }				
				logger.debug("path is file output:"+outFile.getAbsolutePath());
				super.serveResource(request, response, outFile, uri,null);
				return null;					
			}else {
				response.setStatus(404);
			}
		}catch(Exception e) {
			logger.error("viewFile:"+e.getMessage()+",path="+uri);
			response.setStatus(500);
			try {
				response.sendError(500, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}		
		return null;
	}	
}
