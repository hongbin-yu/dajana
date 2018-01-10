package com.filemark.jcr.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.filemark.jcr.model.Asset;
import com.filemark.jcr.model.Device;
import com.filemark.jcr.model.Folder;
import com.filemark.utils.DjnUtils;
import com.filemark.utils.ScanUploadForm;
import com.filemark.utils.WebPage;

@Controller
public class AdminController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(SiteController.class);

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
    
   	@RequestMapping(value = {"/admin/usermanager.html","/admin/usermanager"}, method = {RequestMethod.GET,RequestMethod.POST})
   	public String userManager(String kw,Integer p,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(p==null) p=0;
		String q = "";
		String keywords = "";
		if(kw==null) {
			kw="";
		}else if(!"".equals(kw)){
			kw = DjnUtils.Iso2Uft8(kw);//new String(kw.getBytes("ISO-8859-1"), "GB18030");
			keywords = " and contains(s.*,'"+kw+"')";
		}
   		String usertQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE([/system/users]) "+keywords+" and s.ocm_classname='com.filemark.jcr.model.User'";
		WebPage<Object> users = jcrService.queryObject(usertQuery, 20, p);
		model.addAttribute("users", users);
   		return "admin/usermanager";
   	}

   	@RequestMapping(value = {"/admin/usereditor.html","/admin/usereditor"}, method = {RequestMethod.GET,RequestMethod.POST})
   	public String userEditor(String path,String kw,Integer p,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(p==null) p=0;
		
   		String usertQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE([/system/users]) and s.ocm_classname='com.filemark.jcr.model.User'";
		WebPage<Object> users = jcrService.queryObject(usertQuery, 20, p);
		model.addAttribute("users", users);
		model.addAttribute("user", jcrService.getObject(path));
   		return "admin/usereditor";
   	} 
   	
   	@RequestMapping(value = {"/admin/devicemanager.html","/admin/usermanager"}, method = {RequestMethod.GET})
   	public String deviceManager(Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
   		String deviceQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE([/system/devices]) and s.ocm_classname='com.filemark.jcr.model.Device'";
		WebPage<Object> devices = jcrService.queryObject(deviceQuery, 20, 0);
		model.addAttribute("devices", devices);
			for(Object d:devices.getItems()) {
   				Device dv = (Device)d;
   				File f = new File(dv.getLocation());
   				dv.setUsage(""+f.getUsableSpace()/1000000+"/"+f.getTotalSpace()/1000000);
   			}		
		model.addAttribute("device", new Device());
   		return "admin/devicemanager";
   	}

   	@RequestMapping(value = {"/admin/deviceeditor.html","/admin/deviceeditor"}, method = {RequestMethod.GET})
   	public String deviceEditor(String path,Integer p,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(p==null) p=0;
   		String usertQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE([/system/devices]) and s.ocm_classname='com.filemark.jcr.model.Device'";
		WebPage<Object> devices = jcrService.queryObject(usertQuery, 20, p);
			for(Object d:devices.getItems()) {
   				Device dv = (Device)d;
   				File f = new File(dv.getLocation());
   				dv.setUsage(""+f.getUsableSpace()/1000000+"/"+f.getTotalSpace()/1000000);
   			}		
		model.addAttribute("devices", devices);
		model.addAttribute("device", jcrService.getObject(path));
   		return "admin/deviceeditor";
   	}  
   	
   	@RequestMapping(value = {"/admin/deviceupdate.html","/admin/deviceupdate"}, method = {RequestMethod.POST})
   	public String deviceUpdate(Device device,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
   		try {
   			File folder = new File(device.getLocation());
   			if(!folder.exists()) {
   				folder.mkdirs();
   			}
   			String deviceRoot = "/system/devices";
   			if(!jcrService.nodeExsits(deviceRoot)) {//create root
   				jcrService.addNodes(deviceRoot, "nt:unstructured",getUsername());
   				
   			}	 
   			device.setUsage(""+folder.getUsableSpace()+"/"+folder.getTotalSpace());
   			if(device.getPath()==null || "".equals(device.getPath())) {
        		if(!device.getTitle().matches("(\\w|\\.|\\-|\\s|_)+")) {
        			String path = deviceRoot+"/"+new Date().getTime();
        			device.setPath(path);	
        		}else {
        			String path = jcrService.getUniquePath(deviceRoot, device.getTitle());
        			device.setPath(path);	
        		};
        		jcrService.addOrUpdate(device);
   	   			model.addAttribute("info", "Device "+device.getTitle() +" added!");    				
   			}
   	   		String deviceQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+deviceRoot+"]) and s.ocm_classname='com.filemark.jcr.model.Device'";
   			WebPage<Object> devices = jcrService.queryObject(deviceQuery, 20, 0);
   			for(Object d:devices.getItems()) {
   				Device dv = (Device)d;
   				File f = new File(dv.getLocation());
   				dv.setUsage(""+f.getUsableSpace()/1000000+"/"+f.getTotalSpace()/1000000);
   			}
   			model.addAttribute("devices", devices);
   			model.addAttribute("device", device); 
   			File df = new File(device.getLocation());
   			if(!df.exists()) {
   				df.mkdirs();
   			}

   		}catch(Exception e) {
   			model.addAttribute("error", e.getMessage());
   		}

   		return "admin/deviceeditor";
   	}  
   	
	@RequestMapping(value = {"/admin/export.html"}, method = {RequestMethod.GET,RequestMethod.POST})
	public String export(Model model,String userName,String filename,String location,Integer p, Boolean binary,String path,HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(p==null) p=0;
		if(userName!=null)
			model.addAttribute("user", jcrService.getObject("/system/users/"+userName));
   		String usertQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE([/system/users]) and s.ocm_classname='com.filemark.jcr.model.User'";
		WebPage<Object> users = jcrService.queryObject(usertQuery, 20, p);
		model.addAttribute("users", users);
		if(location!=null) {
			if(!jcrService.nodeExsits(location)) {
				jcrService.addNodes(location, "nt:unstructured",getUsername());	
			}
			if(!jcrService.nodeExsits(location+"/"+filename)) {
				Asset asset= new Asset();
         		asset.setName(filename);
        		asset.setCreatedBy(getUsername());
        		asset.setPath(location+"/"+filename);
       			asset.setLastModified(new Date());
        		String contentType = "application/xml";
        		asset.setContentType(contentType);
        		jcrService.addOrUpdate(asset);
			}
			
			File dir = new File(path);
			if(!dir.exists()) {
				dir.mkdirs();
			}
			if(binary==null)
				binary = true;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();  
			OutputStream out = new FileOutputStream(new File(path,"systemview.xml"));
			jcrService.exportDocument(path, out,binary);
			jcrService.exportDocument(path, bout,binary);
			InputStream inputStream = new ByteArrayInputStream(bout.toByteArray());
			out.close();
			jcrService.addFile(location+"/"+filename, "original", inputStream, "application/xml");
			model.addAttribute("info", path+" has been exorpted to "+location);			
			
		}	
		return "admin/export";
		
	}
	
	@RequestMapping(value = {"/admin/importuser.html"}, method = {RequestMethod.GET,RequestMethod.POST})
	public String importSystem(Integer p,String userName,Model model,Integer behavior,String path,ScanUploadForm uploadForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(p==null) p=0;
		String result="";
		if(userName!=null)
			model.addAttribute("user", jcrService.getObject("/system/users/"+userName));
   		String usertQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE([/system/users]) and s.ocm_classname='com.filemark.jcr.model.User'";
		WebPage<Object> users = jcrService.queryObject(usertQuery, 20, p);
		model.addAttribute("users", users);	
		if(uploadForm!=null && path!=null) {
		for (MultipartFile multipartFile : uploadForm.getFile()) {
			jcrService.importSystem(path, multipartFile.getInputStream(), behavior);
			result+=multipartFile.getOriginalFilename()+" imported,";
		}
		model.addAttribute("info", result);
		}
		return "admin/import";
		
	}   	
}
