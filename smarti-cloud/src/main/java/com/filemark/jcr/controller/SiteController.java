package com.filemark.jcr.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.FileUtils;
import org.apache.poi.util.IOUtils;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.filemark.jcr.model.Asset;
import com.filemark.jcr.model.Device;
import com.filemark.jcr.model.Djcontainer;
import com.filemark.jcr.model.Folder;
import com.filemark.jcr.model.Page;
import com.filemark.jcr.model.User;
import com.filemark.jcr.service.AssetManager;
import com.filemark.sso.JwtUtil;
import com.filemark.utils.ImageUtil;
import com.filemark.utils.ScanUploadForm;
import com.filemark.utils.WebPage;
import com.google.gson.Gson;


@Controller
public class SiteController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(SiteController.class);
	
	@Inject
	protected AssetManager assetManager;
	
    @ExceptionHandler(Exception.class)
    public ModelAndView  handleException(Exception ex,HttpServletRequest request) throws UnsupportedEncodingException {

		String path = URLDecoder.decode(request.getRequestURI(),"UTF-8");
		String paths[] = path.split("\\.");
	    ModelAndView modelAndView = new ModelAndView("error404");
	    String errorcode = request.getParameter("error");
	    String message =  ex.getMessage();
	    
	    String simpleName ="";
	    if(ex.getCause()!=null)
	    	simpleName = ex.getCause().getClass().getSimpleName();
	    //logger.info(simpleName);
	    if (simpleName.equals("ClientAbortException") || simpleName.equals("SocketException")) {
			ImageUtil.HDDOff();
	    	return null;
	    }
	    request.getSession().invalidate();
	    modelAndView.addObject("error",message);
	    //modelAndView.addObject("breadcrumb", jcrService.getBreadcrumb(paths[0]));
	    logger.error(ex.toString());
		ImageUtil.HDDOff();
	    return modelAndView;
    }
    
	
	@RequestMapping(value = {"/site/browse.html","/site/image.html"}, method = {RequestMethod.GET,RequestMethod.POST},produces = "text/plain;charset=UTF-8")
	public String browse(String path,String type, String input,String kw,Integer p,Integer m,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		ImageUtil.HDDOn();
		String assetFolder = "/"+getUsername()+"/assets";
		if(!jcrService.nodeExsits(assetFolder)) {
			jcrService.addNodes(assetFolder, "nt:unstructured",getUsername());		
		}
		int max = 20;
		if(path == null) {
			path=assetFolder;
			max = 20;
		}
		
		Folder currentNode = jcrService.getFolder(path);
		String orderby = "[lastModified] desc";
		if(currentNode.getOrderby()!=null && !"".equals(currentNode.getOrderby()) && !"rank,name".equals(currentNode.getOrderby())) {
			orderby = currentNode.getOrderby();
		}
		if(p==null) p= 0 ;
		String keywords = "";
		if(kw==null) {
			kw="";
		}else if(!"".equals(kw)){
			//kw = DjnUtils.Iso2Uft8(kw);//kw = new String(kw.getBytes("ISO-8859-1"), "GB18030");
			keywords = " and contains(s.*,'"+kw+"')";
		}
		String contentType = "";
		if(type!=null && !"".equals(type)) {
			if(type.equals("media")) type="video";
			contentType = " and s.contentType like '"+type+"%'";
		} 
		boolean isIntranet = isIntranet(request);
		String ISDESCENDANTNODE = "ISDESCENDANTNODE";
		String intranet = (isIntranet?"":" and (s.intranet is null or s.intranet not like 'true')");
		String foldersQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+path+"])" +keywords+intranet+" and s.delete not like 'true' and s.ocm_classname='com.filemark.jcr.model.Folder' order by s.path";
		WebPage<Folder> folders = jcrService.queryFolders(foldersQuery, 100, 0);
		model.addAttribute("folders", folders);

		String assetsQuery = "select s.* from [nt:base] AS s INNER JOIN [nt:unstructured] AS f ON ISCHILDNODE(s, f) WHERE "+ISDESCENDANTNODE+"(s,["+path+"])" +keywords+contentType+intranet+" and s.[delete] not like 'true' and s.ocm_classname='com.filemark.jcr.model.Asset' order by s."+orderby+", s.[name]";
		WebPage<Asset> assets = jcrService.searchAssets(assetsQuery, 12, p);		

	
		String carouselQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+path+"])" +" and s.deleted not like 'true' and s.contentType like 'image/%' and s.ocm_classname='com.filemark.jcr.model.Asset' order by s."+orderby+", s.name";
		if (m==null) m = 6;
		WebPage<Asset> carousel = jcrService.searchAssets(carouselQuery, m, 0);
		model.addAttribute("carousel", carousel);	
		
		Page page = new Page();
		page.setTitle("\u8D44\u6E90");
		model.addAttribute("page", page);		
		model.addAttribute("folder", currentNode);
		model.addAttribute("assets", assets);
		model.addAttribute("path", path);
		model.addAttribute("type", type);
		model.addAttribute("input", input);		
		model.addAttribute("kw", kw);	
		ImageUtil.HDDOff();
		return "site/browse";
	}

	@RequestMapping(value = {"/site/browsemore.html"}, method = {RequestMethod.GET,RequestMethod.POST},produces = "text/plain;charset=UTF-8")
	public String browsemore(String path,String type, String input,String kw,Integer p,Integer m,String topage,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		ImageUtil.HDDOn();
		String assetFolder = "/"+getUsername()+"/assets";
		if(!jcrService.nodeExsits(assetFolder)) {
			jcrService.addNodes(assetFolder, "nt:unstructured",getUsername());		
		}
		int max = 20;
		if(path == null) {
			path=assetFolder;
			max = 20;
		}
		if(topage==null) topage="browsemorew";
		Folder currentNode = jcrService.getFolder(path);
		String orderby = "[lastModified] desc";
		if(currentNode.getOrderby()!=null && !"".equals(currentNode.getOrderby()) && !"rank,name".equals(currentNode.getOrderby())) {
			orderby = currentNode.getOrderby();
		}
		if(p==null) p= 0 ;
		String keywords = "";
		if(kw==null) {
			kw="";
		}else if(!"".equals(kw)){
			//kw = DjnUtils.Iso2Uft8(kw);//kw = new String(kw.getBytes("ISO-8859-1"), "GB18030");
			keywords = " and contains(s.*,'"+kw+"')";
		}
		String contentType = "";
		if(type!=null && !"".equals(type)) {
			if(type.equals("media")) type="video";
			contentType = " and s.contentType like '"+type+"%'";
		} 
		boolean isIntranet = isIntranet(request);
		String ISDESCENDANTNODE = "ISDESCENDANTNODE";
		String intranet = (isIntranet?"":" and (s.intranet is null or s.intranet not like 'true')");

		String assetsQuery = "select s.* from [nt:base] AS s INNER JOIN [nt:unstructured] AS f ON ISCHILDNODE(s, f) WHERE "+ISDESCENDANTNODE+"(s,["+path+"])" +keywords+contentType+intranet+" and s.[delete] not like 'true' and s.ocm_classname='com.filemark.jcr.model.Asset' order by s."+orderby+", s.[name]";
		WebPage<Asset> assets = jcrService.searchAssets(assetsQuery, 12, p);		
		
		model.addAttribute("assets", assets);
		model.addAttribute("path", path);
		model.addAttribute("type", type);
		model.addAttribute("input", input);		
		model.addAttribute("kw", kw);	
		ImageUtil.HDDOff();
		return "site/"+topage;
	}
	@RequestMapping(value = {"/site/media.html"}, method = {RequestMethod.GET,RequestMethod.POST})
	public String media(String path,String type, String input,String kw,Integer p,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {

		String assetFolder = "/"+getUsername()+"/assets";
		if(!jcrService.nodeExsits(assetFolder)) {
			jcrService.addNodes(assetFolder, "nt:unstructured",getUsername());		
		}
		int max = 10;
		if(path == null) {
			path=assetFolder;
			max = 10;
		}
		
		Folder currentNode = jcrService.getFolder(path);
		
		if(p==null) p= 0 ;
		String keywords = "";
		if(kw==null) {
			kw="";
		}else if(!"".equals(kw)){
			//kw = DjnUtils.Iso2Uft8(kw);//kw = new String(kw.getBytes("ISO-8859-1"), "GB18030");
			keywords = " and contains(s.*,'"+kw+"')";
		}
		String contentType = " and s.contentType like 'video%'";;
/*		if(type!=null && !"".equals(type)) {
			contentType = " and s.contentType like '"+type+"%'";
		} */
		String foldersQuery = "select s.* from [nt:base] AS s WHERE ISCHILDNODE(["+path+"])" +keywords+contentType+" and s.delete not like 'true' and (contentType like 'video%' or contentType like 'audio%') and s.ocm_classname='com.filemark.jcr.model.Folder' order by s.path";
		WebPage<Folder> folders = jcrService.queryFolders(foldersQuery, 100, 0);
		model.addAttribute("folders", folders);

		String assetsQuery = "select s.* from [nt:base] AS s WHERE ISDESCENDANTNODE(["+path+"])" +keywords+contentType+" and s.delete not like 'true' and s.ocm_classname='com.filemark.jcr.model.Asset' order by s.lastModified desc, s.name";
		WebPage<Asset> assets = jcrService.searchAssets(assetsQuery, max, p);
		Page page = new Page();
		page.setTitle("\u8D44\u6E90");
		model.addAttribute("page", page);		
		model.addAttribute("folder", currentNode);
		model.addAttribute("assets", assets);
		model.addAttribute("path", path);
		model.addAttribute("type", type);
		model.addAttribute("input", input);		
		model.addAttribute("kw", kw);	
		return "site/media";
	}
	@RequestMapping(value = {"/site/passcode.html"}, method = RequestMethod.POST)
	public String passcode(String path, String passcode, Model model,HttpServletRequest request, HttpServletResponse response) throws Exception  {
		try {
			//String paths = URLDecoder.decode(request.getRequestURI(),"UTF-8");
			Folder currentNode = jcrService.getFolder(path);
			model.addAttribute("path", path);
			model.addAttribute("title", currentNode.getTitle());
			if(currentNode.getPasscode() !=null && !"".equals(currentNode.getPasscode())) {
				if(passcode==null || !passcode.equals(currentNode.getPasscode())) {
					return "site/passcode";
				}else {
					request.getSession().setAttribute(path, passcode);
					request.getSession().setAttribute(passcode, path);
					return "redirect:"+"/site/assets.html";
				}
			}else {
				return "redirect:"+"/site/passcode.html";
			}

		} catch (RepositoryException e) {
			logger.error(e.getMessage());
			throw new Exception("\u9875\u9762\u6CA1\u627E\u5230!");
		}

	}
 	
	@RequestMapping(value = {"/site/assets.html","/site/assets"}, method = {RequestMethod.GET,RequestMethod.POST},produces = "text/plain;charset=UTF-8")
	public String assets(String path,String type, String input,String kw,Integer p,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		ImageUtil.HDDOn();
		String assetFolder = "/"+getUsername()+"/assets";
		if(!jcrService.nodeExsits(assetFolder)) {
			jcrService.addNodes(assetFolder, "nt:unstructured",getUsername());		
		}		
		if(path == null || !path.startsWith(assetFolder)) path=assetFolder;
	
		Folder currentNode = jcrService.getFolder(path);
		String page_passcode = currentNode.getPasscode();

		if(page_passcode !=null && !"".equals(page_passcode) && !this.isRole("ROLE_ADMINISTRATOR") ) {
			HttpSession session = request.getSession();
			String parent_path = (String)session.getAttribute(page_passcode);
			String passcode = (String)session.getAttribute(parent_path);
			if(passcode==null || parent_path==null || !passcode.equals(page_passcode) ) {
				model.addAttribute("path", path);
				model.addAttribute("title", currentNode.getTitle());
				model.addAttribute("passcode", currentNode.getPasscode());
				return "site/passcode";
			}
		}		
		
		String orderby = "[lastModified] desc";
		if(currentNode.getOrderby()!=null && !"".equals(currentNode.getOrderby()) && !"rank,name".equals(currentNode.getOrderby())) {
			orderby = currentNode.getOrderby();
		}		
		if(p==null) p= 0 ;
		String keywords = "";
		if(kw==null) {
			kw="";
		}else if(!"".equals(kw)){

			keywords = " and contains(s.*,'"+kw+"')";
		}

		String contentType = "";
		
		if(type!=null && !type.equals("") && !"child".equals(type)) {
			if(type.equals("media")) type="video";
			contentType = " and s.contentType like '"+type+"%'";
		}
		boolean isIntranet = isIntranet(request);
		String intranet = (isIntranet?"":" and (s.intranet is null or s.intranet not like 'true')");
		String intranetFolder = (isIntranet?"":" and (f.intranet is null or f.intranet not like 'true')");
		String ISDESCENDANTNODE = "ISDESCENDANTNODE";
		if(type!=null && "child".equals(type)) ISDESCENDANTNODE = "ISCHILDNODE";
		String sharingQuery = "select * from [nt:base] AS s WHERE s.sharing like '%"+getUsername()+"@%'" +keywords+intranet+" and s.delete not like 'true' and s.ocm_classname='com.filemark.jcr.model.Folder' order by s.path";
		WebPage<Folder> shares = jcrService.queryFolders(sharingQuery, 100, 0);
		model.addAttribute("shares", shares);

		String foldersQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+path+"])" +keywords+intranet+" and s.delete not like 'true' and s.ocm_classname='com.filemark.jcr.model.Folder' order by s.path";
		WebPage<Folder> folders = jcrService.queryFolders(foldersQuery, 100, 0);
		model.addAttribute("folders", folders);

		String assetsQuery = "select s.* from [nt:base] AS s INNER JOIN [nt:unstructured] AS f ON ISCHILDNODE(s, f) WHERE "+ISDESCENDANTNODE+"(s,["+path+"])" +keywords+contentType+intranetFolder+" and s.[delete] not like 'true' and s.ocm_classname='com.filemark.jcr.model.Asset' order by s."+orderby+", s.[name]";
		//logger.info(isIntranet+",ip="+getClientIpAddress(request)+",q="+assetsQuery);;
		WebPage<Asset> assets = jcrService.searchAssets(assetsQuery, 12, p);
		Page page = new Page();
		page.setTitle("\u4E91\u7AD9");
		model.addAttribute("page", page);
		model.addAttribute("folder", currentNode);
		model.addAttribute("assets", assets);
		model.addAttribute("path", path);
		model.addAttribute("type", type);
		model.addAttribute("input", input);		
		model.addAttribute("kw", kw);	
		ImageUtil.HDDOff();
		return "site/asset";
	}
	
	@RequestMapping(value = {"/site/createPage.html"}, method = RequestMethod.GET)
	public String createPageGet(String path,String uid,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		ImageUtil.HDDOn();
		if(uid!=null) {
			Page page = jcrService.getPageByUid(uid);
			path = page.getPath();
		}
		Page currentPage = jcrService.getPage(path);
		if(currentPage.getDepth()==2 && !path.startsWith("/content/templates")) {
			String themesQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE([/content/templates])  and s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
			WebPage<Page> themes = jcrService.queryPages(themesQuery, 100, 0);
			String mythemesQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+currentPage.getPath()+"])  and s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
			WebPage<Page> mythemes = jcrService.queryPages(mythemesQuery, 100, 0);
			List<Page> newThemes = new ArrayList<Page>(); 
			for(Page p:themes.getItems()) {
				for(Page mp:mythemes.getItems()) {
					if(p.getName().equals(mp.getName())) {
						continue;
					}
				}
				newThemes.add(p);
			}
			model.addAttribute("themes", newThemes);
		}
		String paths[] = path.split("/");
		if(paths.length>3) {
			String templatePath = "/content/templates/"+paths[3];
			String templatesQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+templatePath+"])  and s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
			WebPage<Page> templates = jcrService.queryPages(templatesQuery, 100, 0);
			model.addAttribute("templates", templates);
		}	
		model.addAttribute("path", path);
		ImageUtil.HDDOff();
		return "site/createPage";
	}
	
	@RequestMapping(value = {"/site/editImage.html"}, method = RequestMethod.GET)
	public String editImage(String path,String uid,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {

		if(uid!=null) {
			Asset asset = jcrService.getAssetById(uid);
			if(!jcrService.nodeExsits(asset.getPath()+"/file-1000"))
				jcrService.createFile(asset.getPath(), 10);
			model.addAttribute("asset", asset);
			if(asset.getHtml()==null || "".equals(asset.getHtml())) {
				String html = "<img class=\"img-responsive\" alt=\"\" src=\"file?uid="+asset.getUid()+"&w=10\">";
				model.addAttribute("html", html);				
			}else if("imageHtml".endsWith(asset.getHtml())){
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				FileUtils.copyFile(new File(asset.getHtml()), output);
				model.addAttribute("html", output.toString());				
			}else {
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				jcrService.readAsset(asset.getPath()+"/imageHtml", output);
				model.addAttribute("html", output.toString());					
			}

		}
		return "site/editImage";
	}	
	
	@RequestMapping(value = {"/site/createPage.html"}, method = RequestMethod.POST)
	public @ResponseBody String createPage(String path,String templatePath,Integer p,Page page,Model model,HttpServletRequest request, HttpServletResponse response) {
		ImageUtil.HDDOn();
		Page currentPage = null;
		if(path==null) return "error:path is null";
		page.setParent(path);
		try {
			page.setPath(path+"/"+page.getName());
			if(templatePath!=null && !"".equals(templatePath)) {
				if("/content/templates".equals(templatePath))
					templatePath +="/"+page.getName();
				if(jcrService.nodeExsits(templatePath)) {
					Page template = jcrService.getPage(templatePath);
					if(page.getTitle()==null)
						page.setTitle(template.getTitle());
					page.setContent(template.getContent());
				}
			}
			
			page = jcrService.add(page);
			if(page.getBreadcrumb()==null) {
				page.setBreadcrumb(jcrService.getBreadcrumb(page.getPath()));
				jcrService.addOrUpdate(page);
			}
			currentPage = jcrService.getPage(page.getPath());
		} catch (Exception e) {
			ImageUtil.HDDOff();
			return "error:"+e.getMessage();
		}
		ImageUtil.HDDOff();
		return currentPage.getPath();
	}
	@RequestMapping(value = {"/site/createFolder.html"}, method = RequestMethod.POST)
	public @ResponseBody String createFolder(String path,Integer p,Folder folder,Model model,HttpServletRequest request, HttpServletResponse response) {
		if(path==null) return "error:path is null";
		folder.setParent(path);
		try {
			String folderName = folder.getName().toLowerCase().replaceAll(" ", "-");
    		if(!folderName.matches("(\\w|\\.|\\-|\\s|_)+")) {
    			folderName = path+"/"+getDateTime();
    		}
			folder.setPath(jcrService.getUniquePath(path, folderName));
			folder.setLastModified(new Date());
			jcrService.addOrUpdate(folder);

		} catch (RepositoryException e) {
			return "error:"+e.getMessage();
		}

		return folder.getPath();
	}
	
	@RequestMapping(value = {"/site/editpp.html","/editpp.html/**","/editpp.html/**/*.*"}, method = {RequestMethod.GET})
	public String getPageProperties(String uid,String path,Page page, Model model,HttpServletRequest request, HttpServletResponse response) {
		ImageUtil.HDDOn();
		Page currentpage = new Page();
		try {
			if(uid!=null) path = jcrService.getNodeById(uid);
			currentpage = jcrService.getPage(path);

		} catch (RepositoryException e) {
			ImageUtil.HDDOff();
			model.addAttribute("message", "<div class=\"alert alert-danger\"><h2>&#22833;&#36133;</h2><p>"+e.getMessage()+"</p></div>");
		}
		
		model.addAttribute("page", currentpage); 
		ImageUtil.HDDOff();
		return "site/pproperties";
	}
	@RequestMapping(value = {"/site/editpp.html"}, method = {RequestMethod.POST})
	public String editPageProperties(String uid,String path,Page page, Model model,HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		Page currentpage = new Page();
		ImageUtil.HDDOn();
		try {
			if(uid!=null) path = jcrService.getNodeById(uid);
			currentpage = jcrService.getPage(path);
			if(page !=null) {
				currentpage.setTitle(page.getTitle());
				currentpage.setDescription(page.getDescription());
				currentpage.setShowChat(page.getShowChat()); 
				currentpage.setShowComment(page.getShowComment());
				currentpage.setShowLeftmenu(page.getShowLeftmenu());
				currentpage.setRedirectTo(page.getRedirectTo());
				jcrService.addOrUpdate(currentpage);
				model.addAttribute("info", "<h2>&#25104;&#21151;</h2>");
			}

		} catch (RepositoryException e) {
			ImageUtil.HDDOff();
			model.addAttribute("error", "<h3>&#22833;&#36133;</h3><p>"+e.getMessage()+"</p>");
		}
		ImageUtil.HDDOff();
		model.addAttribute("page", currentpage); 

		return "redirect:/site/editor.html?uid="+currentpage.getUid();
	}

	@RequestMapping(value = {"/site/editor.html"}, method = RequestMethod.GET)
	public String editPage(String uid,String path,String input,String kw,Integer p,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String content = "/content/"+getUsername();
		Page currentpage = new Page();
		ImageUtil.HDDOn();
		if(!jcrService.nodeExsits(content)) {//create root
			if(!jcrService.nodeExsits("/content")) {
				Page djn = new Page();
				djn.setPath("/content");
				djn.setParent("/");
				djn.setTitle("\u5927\u5BB6\u62FF");
				djn.setRedirectTo("http://www.dajana.cn");
				djn.setCreatedBy(getUsername());
				djn.setLastModified(Calendar.getInstance().getTime());
				jcrService.addOrUpdate(djn);				
			}
			currentpage.setPath(content);
			currentpage.setParent("/content");
			currentpage.setTitle("\u9996\u9875");
			currentpage.setCreatedBy(getUsername());
			currentpage.setLastModified(Calendar.getInstance().getTime());
			jcrService.addOrUpdate(currentpage);
		}

		if(uid==null && path!=null) {
			currentpage = jcrService.getPage(path.replace(".html",""));
		}else if(uid!=null) {
			currentpage = jcrService.getPageByUid(uid); 
		}else {
			currentpage = jcrService.getPage(content);
		}
		if(!currentpage.getPath().startsWith(content)) {
			if(getUsername()==null) {
				ImageUtil.HDDOff();
				return "redirect:/mysite";
			} else	{		
				ImageUtil.HDDOff();
				return "redirect:/site/editor.html?path="+content;
			}
		}		
		if(currentpage.getBreadcrumb()==null) {
			currentpage.setBreadcrumb(jcrService.getBreadcrumb(currentpage.getPath()));
			jcrService.addOrUpdate(currentpage);
		}		

		String contentPath = request.getContextPath();
		if(currentpage.getBreadcrumb()==null) {
			String breadcrumb = jcrService.getBreadcrumb(currentpage.getPath());
			if(!contentPath.equals("/") && breadcrumb.indexOf(contentPath)<0) {
				breadcrumb= breadcrumb.replaceAll("/content/", request.getContextPath()+"/content/");
			}			
			currentpage.setBreadcrumb(breadcrumb);
			jcrService.addOrUpdate(currentpage);
		}

		String menu = jcrService.getNavigation(currentpage.getPath(),2,20);
		if(!contentPath.equals("/") && menu.indexOf(contentPath)<0) {
			menu= menu.replaceAll("/content/", request.getContextPath()+"/content/");
		}
		leftmenu(uid,path,model,request,response);
		//currentpage.setShowFilter("false");
		if(currentpage.getContent()!=null)
			currentpage.setContent(currentpage.getContent().replaceAll("wb-mltmd", "wb-mltmd-edit"));
		
		model.addAttribute("navigation", menu);
		model.addAttribute("page", currentpage); 
		model.addAttribute("origin", request.getRequestURL()+"?"+request.getQueryString());
		if(currentpage.getPath().startsWith("/content/templates")) {
			ImageUtil.HDDOff();
			return "site/template";
		}
		ImageUtil.HDDOff();
		return "site/editor";
	}

	@RequestMapping(value = {"/content/{site}.edit","/content/{site}/*.edit","/content/{site}/**/*.edit"}, method = RequestMethod.GET)
	public String editor(String input,String kw,Integer p,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String content = "/content/"+getUsername();
		Page currentpage = new Page();
		if(!jcrService.nodeExsits(content)) {//create root
			if(!jcrService.nodeExsits("/content")) {
				Page djn = new Page();
				djn.setPath("/content");
				djn.setParent("/");
				djn.setTitle("\u5927\u5BB6\u62FF");
				djn.setRedirectTo("http://www."+jcrService.getDomain());
				djn.setCreatedBy(getUsername());
				djn.setLastModified(Calendar.getInstance().getTime());
				jcrService.addOrUpdate(djn);				
			}
			String usersite = (String)request.getSession().getAttribute("usertitle");
			if(usersite==null)
				usersite="\u9996\u9875";
			currentpage.setPath(content);
			currentpage.setParent("/content");
			currentpage.setTitle(usersite);
			currentpage.setCreatedBy(getUsername());
			currentpage.setLastModified(Calendar.getInstance().getTime());
			jcrService.addOrUpdate(currentpage);
		}

		String paths = URLDecoder.decode(request.getRequestURI(),"UTF-8");
		if(!request.getContextPath().equals("/"))
			paths = paths.replaceFirst(request.getContextPath(), "");
		String path = paths.split(".edit")[0];
		currentpage = jcrService.getPage(path);
		if(!currentpage.getPath().startsWith(content)) {
			if(getUsername()==null)
				return "redirect:/mysite";
			else			
				return "redirect:/site/editor.html?path="+content;
		}		


		String contentPath = request.getContextPath();
		if(currentpage.getBreadcrumb()==null) {
			String breadcrumb = jcrService.getBreadcrumb(currentpage.getPath());
			if(!contentPath.equals("/") && breadcrumb.indexOf(contentPath)<0) {
				breadcrumb= breadcrumb.replaceAll("/content/", request.getContextPath()+"/content/");
			}			
			currentpage.setBreadcrumb(breadcrumb);
			jcrService.addOrUpdate(currentpage);
		}


		leftmenu(null,path,model,request,response);
		//currentpage.setShowFilter("false");
		if(currentpage.getContent()!=null)
			currentpage.setContent(currentpage.getContent().replaceAll("wb-mltmd", "wb-mltmd-edit"));

			
		
		//model.addAttribute("navigation", menu);
		model.addAttribute("page", currentpage); 
		model.addAttribute("origin", request.getRequestURL()+"?"+request.getQueryString());
		if(currentpage.getPath().startsWith("/content/templates"))
			return "site/template";
		return "site/editor";
	}
	@RequestMapping(value = {"/site/menu.html"}, method = RequestMethod.GET)
	public String menu(String uid,String path,String input,String kw,Integer p,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		ImageUtil.HDDOff();
		String menu="";
		String content = "/content/"+getUsername();
		Page currentpage = new Page();

		if(uid==null && path!=null) {
			currentpage = jcrService.getPage(path.replace(".html",""));
		}else if(uid!=null) {
			currentpage = jcrService.getPageByUid(uid); 
		}else {
			currentpage = jcrService.getPage(content);
		}
		int level = 2;
		if(currentpage.getShowThememenu()!=null && "true".equals(currentpage.getShowThememenu()))
			level++;			
		menu = jcrService.getNavigation(currentpage.getPath(),level,20);
		if(!request.getContextPath().equals("/")) {
			menu = menu.replaceAll("/content/", request.getContextPath()+"/content/");
		}
		ImageUtil.HDDOff();
		model.addAttribute("navigation", menu);
		return "site/navmenu";
	}
	@RequestMapping(value = {"/site/preview.html"}, method = RequestMethod.GET)
	public String previewPage(String uid,String path,String input,String kw,Integer p,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		ImageUtil.HDDOn();
		String content = "/content/"+getUsername();
		Page currentpage = new Page();

		if(uid==null && path!=null) {
			currentpage = jcrService.getPage(path);
		}else if(uid!=null) {
			currentpage = jcrService.getPageByUid(uid); 
		}else {
			currentpage = jcrService.getPage(content);
		}
		if(!currentpage.getPath().startsWith(content)) {
			throw new Exception("&#27809;&#26377;&#26435;&#38480;&#65306;"+currentpage.getPath()+"!="+content);
		}
		String contentPath = request.getContextPath();
		if(currentpage.getBreadcrumb()==null) {
			String breadcrumb = jcrService.getBreadcrumb(currentpage.getPath());
			if(!contentPath.equals("/") && breadcrumb.indexOf(contentPath)<0) {
				breadcrumb= breadcrumb.replaceAll("/content/", request.getContextPath()+"/content/");
			}			
			currentpage.setBreadcrumb(breadcrumb);
			jcrService.addOrUpdate(currentpage);
		}
		int level = 2;
		if(currentpage.getShowThememenu()!=null && "true".equals(currentpage.getShowThememenu()))
			level++;		
		String navigation = jcrService.getPageNavigation(path,level);
		String menuPath=jcrService.getAncestorPath(currentpage.getPath(), level);
		currentpage.setMenuPath(menuPath);		
		if(currentpage.getContent()!=null)
			currentpage.setContent(currentpage.getContent().replaceAll("-edit", ""));
		leftmenu(uid,path,model,request,response);
		model.addAttribute("page", currentpage); 
		model.addAttribute("navigation",navigation);
		model.addAttribute("content",currentpage.getContent());
		model.addAttribute("origin", request.getRequestURL()+"?"+request.getQueryString());

		ImageUtil.HDDOff();	
		return "content/page";
	}

	@RequestMapping(value = {"/site/leftmenu.html","/leftmenu.html"}, method = RequestMethod.GET)
	public String leftmenu(String uid,String path,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		ImageUtil.HDDOn();
		String content = "/content/"+getUsername();		
		Page currentpage = new Page();
		if(uid==null && path!=null) {
			currentpage = jcrService.getPage(path.replace(".html", ""));
		}else if(uid!=null) {
			currentpage = jcrService.getPageByUid(uid); 
		}else {
			currentpage = jcrService.getPage(content);
		}

		String pagesQuery = "select * from [nt:base] AS s WHERE ISDESCENDANTNODE(["+content+"]) and ISCHILDNODE(["+currentpage.getParent()+"])  and s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
		if(currentpage.getPath().equals("/content/"+getUsername()) || currentpage.getPath().equals("/content")) {
			pagesQuery = "select * from [nt:base] AS s WHERE ISSAMENODE([/content/"+getUsername()+"])  and s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
		}
		WebPage<Page> pages = jcrService.queryPages(pagesQuery, 100, 0);
		String childPagesQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+currentpage.getPath()+"]) and s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
		WebPage<Page> children = jcrService.queryPages(childPagesQuery, 100, 0);
		for(Page sibling:pages.getItems()) {
			if(sibling.getPath().equals(currentpage.getPath())) {
				sibling.setChildPages(children.getItems());
				sibling.setShowChildren(true);
				sibling.setCssClass(" wb-navcurr");
			}
		}
		model.addAttribute("parent",jcrService.getPage(currentpage.getParent()));
		model.addAttribute("menu", pages); 
		model.addAttribute("page", currentpage);
		ImageUtil.HDDOff();
		return "site/leftmenu";
	}

	@RequestMapping(value = {"/templates.html"}, method = RequestMethod.GET)
	public String templates(String path,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		ImageUtil.HDDOn();
		Page currentpage = new Page();
		if(path==null || path.equals("/content"))
			path = "/content/templates";
		currentpage = jcrService.getPage(path);

		String pagesQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+currentpage.getParent()+"]) and s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
		if(currentpage.getPath().equals("/content/templates")) {
			pagesQuery = "select * from [nt:base] AS s WHERE ISSAMENODE([/content/templates]) and s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
		}
		WebPage<Page> pages = jcrService.queryPages(pagesQuery, 100, 0);
		String childPagesQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+currentpage.getPath()+"]) and s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
		WebPage<Page> children = jcrService.queryPages(childPagesQuery, 100, 0);
		for(Page sibling:pages.getItems()) {
			if(sibling.getPath().equals(currentpage.getPath())) {
				sibling.setChildPages(children.getItems());
				sibling.setShowChildren(true);
				sibling.setCssClass(" wb-navcurr");
			}
		}
		model.addAttribute("parent",jcrService.getPage(currentpage.getParent()));
		model.addAttribute("menu", pages); 
		model.addAttribute("page", currentpage);
		ImageUtil.HDDOff();
		return "site/templates";
	}
	@RequestMapping(value = {"/site/pages.html","/site/file.html"}, method = RequestMethod.GET)
	public String files(String path,String type, String input,String kw,Integer p,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		ImageUtil.HDDOn();
		if(path==null) {
			path="/content/"+getUsername();
		}
		Page currentpage = new Page();
		currentpage = jcrService.getPage(path);
		
		String pagesQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+currentpage.getParent()+"])  and s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
		if(currentpage.getPath().equals("/content/"+getUsername()) || currentpage.getPath().equals("/content")) {
			pagesQuery = "select * from [nt:base] AS s WHERE ISSAMENODE([/content/"+getUsername()+"])  and s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
		}
		WebPage<Page> pages = jcrService.queryPages(pagesQuery, 100, 0);
		String childPagesQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+currentpage.getPath()+"]) and s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
		WebPage<Page> children = jcrService.queryPages(childPagesQuery, 100, 0);
		for(Page sibling:pages.getItems()) {
			if(sibling.getPath().equals(currentpage.getPath())) {
				sibling.setChildPages(children.getItems());
				sibling.setShowChildren(true);
				sibling.setCssClass(" wb-navcurr");
			}
		}
		model.addAttribute("parent",jcrService.getPage(currentpage.getParent()));
		model.addAttribute("menu", pages); 
		model.addAttribute("page", currentpage);
		model.addAttribute("path", path);
		model.addAttribute("type", type);
		model.addAttribute("input", input);		
		ImageUtil.HDDOff();
		return "site/pages";
	}	
	
	@RequestMapping(value = {"/site/page.html"}, method = RequestMethod.GET)
	public String pages(String path,String type, String input,String kw,Integer p,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		ImageUtil.HDDOn();
		String content = "/content/"+getUsername();
		Page currentpage = new Page();
		if(!jcrService.nodeExsits(content)) {//create root
			if(!jcrService.nodeExsits("/content")) {
				Page djn = new Page();
				djn.setPath("/content");
				djn.setParent("/");
				djn.setTitle("\u5927\u5BB6\u62FF");
				djn.setRedirectTo("http://www."+jcrService.getDomain());
				djn.setCreatedBy(getUsername());
				djn.setLastModified(Calendar.getInstance().getTime());
				jcrService.addOrUpdate(djn);				
			}
			currentpage.setPath(content);
			currentpage.setParent("/content");
			currentpage.setTitle("\u9996\u9875");
			currentpage.setCreatedBy(getUsername());
			currentpage.setLastModified(Calendar.getInstance().getTime());
			jcrService.addOrUpdate(currentpage);
		}

		if(!jcrService.nodeExsits(path)) {
			jcrService.addNodes(path, "nt:unstructured",getUsername());		
		}		
		currentpage = jcrService.getPage(path);
		Page parent = currentpage = jcrService.getPage(currentpage.getParent());
		if(p==null) p= 0 ;
		String keywords = "";
		if(kw==null) {
			kw="";
		}else if(!"".equals(kw)){
			//kw = DjnUtils.Iso2Uft8(kw);//new String(kw.getBytes("ISO-8859-1"), "GB18030");
			keywords = " and contains(s.*,'"+kw+"')";
		}
		String contentType = "";
		if(type!=null && !type.equals("")) {
			contentType = " and s.contentType like '"+type+"%'";
		}
		if(currentpage.getBreadcrumb()==null) {
			currentpage.setBreadcrumb(jcrService.getBreadcrumb(currentpage.getPath()));
			jcrService.addOrUpdate(currentpage);
		}		
		String pagesQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+path+"])" +keywords+contentType+" and s.delete not like 'true' and s.ocm_classname='com.filemark.jcr.model.Page' order by s.path";
		WebPage<Page> pages = jcrService.queryPages(pagesQuery, 100, 0);

		model.addAttribute("parent", parent);
		model.addAttribute("page", currentpage);
		model.addAttribute("pages", pages);
		model.addAttribute("path", path);
		model.addAttribute("type", type);
		model.addAttribute("input", input);		
		model.addAttribute("kw", kw);	
		ImageUtil.HDDOff();
		return "site/page";
	}
	
	@RequestMapping(value = {"/site/folder.html"}, method = RequestMethod.GET)
	public String folders(String uid,String path, String type, String input,String kw,Integer p,Integer m,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		ImageUtil.HDDOn();
		String assetFolder = "/"+getUsername()+"/assets";

		if(!jcrService.nodeExsits(assetFolder)) {//create root
			jcrService.addNodes(assetFolder, "nt:unstructured",getUsername());
			
		}		
		if( path == null) path = assetFolder;
		if(uid != null) {
			path = jcrService.getNodeById(uid);
		}
		
	
		Folder currentNode = jcrService.getFolder(path);
		if(p==null) p= 0 ;
		String keywords = "";
		if(kw==null) {
			kw="";
		}else if(!"".equals(kw)){
			//kw = DjnUtils.Iso2Uft8(kw);//new String(kw.getBytes("ISO-8859-1"), "GB18030");
			keywords = " and contains(s.*,'"+kw+"')";
		}
		String contentType = "";
		if(type!=null && !type.equals("")) {
			contentType = " and s.contentType like '"+type+"%'";
		}
		String foldersQuery = "select * from [nt:base] AS s WHERE ISDESCENDANTNODE(["+path+"])" +keywords+contentType+" and s.delete not like 'true' and s.ocm_classname='com.filemark.jcr.model.Folder' order by s.path";
		WebPage<Folder> folders = jcrService.queryFolders(foldersQuery, 100, 0);
		String assetsQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+path+"])" +" and s.deleted not like 'true' and s.ocm_classname='com.filemark.jcr.model.Asset' order by s.lastModified desc, s.name";
		if (m==null) m = 10;
		WebPage<Asset> assets = jcrService.searchAssets(assetsQuery, m, 0);
		model.addAttribute("assets", assets); 
	
		model.addAttribute("folders", folders);
		model.addAttribute("folder", currentNode);
		model.addAttribute("path", path);
		model.addAttribute("type", type);
		model.addAttribute("input", input);		
		model.addAttribute("kw", kw);
		ImageUtil.HDDOff();
		return "site/folder";
	}	
	
	@RequestMapping(value = {"/site/upload.html"}, method = {RequestMethod.GET,RequestMethod.POST})
	public String upload(String path,String redirect,String type, String input,String kw,Integer p,ScanUploadForm uploadForm,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {


		if(!jcrService.nodeExsits(path)) {
			jcrService.addNodes(path, "nt:unstructured",getUsername());		
		}
		model.addAttribute("fileUrl",jcrService.uploadAssets(path,uploadForm,getUsername()));		
		if(redirect!=null)
			return "redirect:"+redirect;
		return assets(path,type,input,kw,0,model,request,response);
	}
	
	@RequestMapping(value = {"/site/texteditor.html"}, method = {RequestMethod.GET,RequestMethod.POST})
	public String textEditor(String path,String uid,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		Asset asset = jcrService.getAssetById(uid);
		String content = getContent(asset);
		model.addAttribute("content", content);
		model.addAttribute("asset", asset);
		return "site/textEditor";
	}

	@RequestMapping(value = {"/site/uploadAsset.html"}, method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody Asset  assetsUpload(String path,String lastModified,String proccess,Integer total,String override,ScanUploadForm uploadForm,Model model,HttpServletRequest request, HttpServletResponse response) {
		Asset asset= new Asset();
		String username = getUsername();
		if(username == null) {
			asset.setTitle("error:Login again");
			return asset;
		}
		if(total == null) total = 1;
		logger.debug("Uploading..");
		Date start = new Date();
		ImageUtil.HDDOn();
		try {
			if(!jcrService.nodeExsits(path)) {
				jcrService.addNodes(path, "nt:unstructured",getUsername());		
			}
    		for (MultipartFile multipartFile : uploadForm.getFile()) {
        		String fileName = multipartFile.getOriginalFilename();
        		if(fileName==null || "".equals(fileName)) {
        			fileName = uploadForm.getFilename();
        		}
        		fileName = fileName.toLowerCase().replaceAll(" ", "-");
        		String ext = "";
        		if(fileName.lastIndexOf(".")>0) {
        			ext = fileName.substring(fileName.lastIndexOf("."), fileName.length());
        		}
        		String assetPath = fileName;
        		if(!fileName.matches("(\\w|\\.|\\-|\\s|_)+")) {
        			assetPath = path+"/"+getDateTime()+ext;
        			//fileName = DjnUtils.Iso2Uft8(fileName);
        		}else {
        			
        			if(jcrService.nodeExsits(path+"/"+fileName)) {
        				if("true".equals(override) ) {
	        				asset = (Asset)jcrService.getObject(path+"/"+fileName);
	        				assetPath = path+"/"+fileName;
        				}else {
        					assetPath = path+"/"+getDateTime()+ext;
        				}
        			}else {
        				assetPath = jcrService.getUniquePath(path, fileName);
        			}
    		
        		};
/*        		if(!assetPath.endsWith(ext)) {
        			assetPath += ext;
        		}*/
    			asset.setExt(ext);
         		asset.setName(fileName);
        		asset.setCreatedBy(username);
        		asset.setPath(assetPath);
       			asset.setLastModified(new Date());
     			asset.setDevice(this.getDevice().getPath());
        		if(lastModified!=null && lastModified.matches("-?\\d+(\\.\\d+)?")) {
        			
        			asset.setOriginalDate(new Date(Long.parseLong(lastModified)));
        		}
        		String contentType = multipartFile.getContentType();
        		asset.setContentType(contentType);
        		asset.setSize(multipartFile.getSize());
        		jcrService.addOrUpdate(asset);
        		jcrService.updateCalendar(path,"lastModified");
        		jcrService.setProperty(path, "changed", "true");
        		try {
        			String infile=null;
        			if(asset.getDevice()!=null) {
        				Device device = (Device)jcrService.getObject(asset.getDevice());
        				//logger.debug("Writing device "+device.getPath() +":"+device.getLocation());
        				infile = device.getLocation()+asset.getPath();
        				File folder = new File(device.getLocation()+asset.getPath());
        				if(!folder.exists()) folder.mkdirs();
        				folder.setLastModified(new Date().getTime());
        				File file = new File(device.getLocation()+asset.getPath()+"/origin"+ext);
        				file.setLastModified(new Date().getTime());
/*        				if(!file.getParentFile().exists()) {
        					file.getParentFile().mkdirs();
        				}*/
        				InputStream in = multipartFile.getInputStream();
        				FileUtils.copyInputStreamToFile(in, file);
        				in.close();
	           			if("application/vnd.ms-powerpoint".equals(contentType) || contentType.startsWith("application/vnd.ms-excel") || contentType.startsWith("application/vnd.openformats-officedocument")  || contentType.startsWith("application/vnd.openxmlformats-officedocument")) {
	           				 logger.debug("xls2pdf:"+file.getAbsolutePath());
	        				 ImageUtil.xls2pdf(file.getAbsolutePath(), file.getParentFile().getAbsolutePath());

	           			}else if("application/vnd.ms-powerpoint".equals(contentType) || "application/vnd.ms-word".equals(contentType) || "application/vnd.ms-excel".equals(contentType) || "application/msword".equals(contentType) || assetPath.endsWith(".doc") || assetPath.endsWith(".docx")) {	
	           				 logger.debug("doc2pdf:"+file.getAbsolutePath());
	        				 ImageUtil.doc2pdf(file.getAbsolutePath(), file.getParentFile().getAbsolutePath());
	        			}        				
	           			if(contentType!=null && contentType.startsWith("video/")) {	
	           				 logger.debug("video2mp4:"+file.getAbsolutePath());
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

        			}else {
        				//logger.debug("Writing jcr");
            			jcrService.addFile(assetPath,"original",multipartFile.getInputStream(),contentType);
        			}
        			//logger.debug("Done");
        			asset.setTitle(asset.getTitle() +" - "+(new Date().getTime() - start.getTime()));


        		}catch(Exception ej) {
        			logger.error(ej.getMessage());
        			jcrService.updatePropertyByPath(assetPath, "description", ej.getMessage());
        		}
    		}
    		

		}catch (Exception e){
			logger.error("error:"+e.getMessage());
			asset.setTitle("error:"+e.getMessage());
			
		}
		ImageUtil.HDDOff();
		return asset;
	}	

	@RequestMapping(value = {"/site/uploadIcon.html"}, method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String  iconUpload(String path,ScanUploadForm uploadForm,Model model,HttpServletRequest request, HttpServletResponse response) {

		String username = getUsername();
		Asset asset= new Asset();
		ImageUtil.HDDOn();
		try {
			for (MultipartFile multipartFile : uploadForm.getFile()) {
				String fileName = multipartFile.getOriginalFilename();
	    		String ext = "";
	    		if(fileName.lastIndexOf(".")>0) {
	    			ext = fileName.substring(fileName.lastIndexOf("."), fileName.length());
	    		}
	    		String assetPath = "/"+username+"/assets/icon";
				asset.setExt(ext);
	     		asset.setName(fileName);
	    		asset.setCreatedBy(username);
	    		asset.setPath(assetPath);
	   			asset.setLastModified(new Date());
	 			asset.setDevice(this.getDevice().getPath());
        		String contentType = multipartFile.getContentType();
        		asset.setContentType(contentType);
				InputStream in = multipartFile.getInputStream();
				File folder = new File(jcrService.getDevice()+asset.getPath());
				if(!folder.exists()) folder.mkdirs();
				File file = new File(folder,"origin"+ext);
				FileUtils.copyInputStreamToFile(in, file);
				in.close();
				jcrService.addOrUpdate(asset);
				jcrService.autoRoateImage(asset.getPath());
				jcrService.createIcon(asset.getPath(), 48,48);
				jcrService.createIcon(asset.getPath(), 120,120);
				jcrService.createIcon(asset.getPath(), 400,400);
			}

		}catch (Exception e){
			logger.error("error:"+e.getMessage());
			return "error:"+e.getMessage();
			
		}
		ImageUtil.HDDOff();
		return "/site/file/icon.jpg?path="+asset.getPath()+"/x120.jpg&time="+new Date().getTime();
	}	
	
	@RequestMapping(value = {"/site/importAsset.html"}, method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody Asset  assetsImport(String path,String url, String override,Model model,HttpServletRequest request, HttpServletResponse response) {
		Asset asset= new Asset();
		String username = getUsername();
		ImageUtil.HDDOn();
		try {
		if(!jcrService.nodeExsits(path)) {
			jcrService.addNodes(path, "nt:unstructured",getUsername());		
		}
		//int count=0;
		Date start = new Date();
        URL url_img = new URL(url);
    	HttpURLConnection conn = (HttpURLConnection) url_img.openConnection();
    	conn.setReadTimeout(30000);
    	conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
    	conn.addRequestProperty("User-Agent", "Mozilla");
    	conn.addRequestProperty("Referer", "baidu.com");
    	boolean redirect = false;

    	// normally, 3xx is redirect
    	int status = conn.getResponseCode();
    	if (status != HttpURLConnection.HTTP_OK) {
    		if (status == HttpURLConnection.HTTP_MOVED_TEMP
    			|| status == HttpURLConnection.HTTP_MOVED_PERM
    				|| status == HttpURLConnection.HTTP_SEE_OTHER)
    		redirect = true;
    	}
    	if (redirect) {

    		// get redirect url from "location" header field
    		String newUrl = conn.getHeaderField("Location");

    		// get the cookie if need, for login
    		String cookies = conn.getHeaderField("Set-Cookie");

    		// open the new connnection again
    		url_img = new URL(newUrl);
    		conn = (HttpURLConnection) url_img.openConnection();
    		conn.setRequestProperty("Cookie", cookies);
    		conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
    		conn.addRequestProperty("User-Agent", "Mozilla");
    		conn.addRequestProperty("Referer", "baidu.com");

    		logger.debug("Redirect to:"+newUrl);

    	}        
        
    	String contentType = conn.getContentType();
	    MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
	    logger.debug("contentType="+contentType);
	    String ext="";
	    if(contentType !=null)
		try {
			MimeType mimeType = allTypes.forName(contentType);
		    ext = mimeType.getExtension(); 
		} catch (MimeTypeException e1) {
			ImageUtil.HDDOff();
			logger.error(e1.getMessage());
		}
		
		
		String urlPath = url_img.getPath();
		if(ext==null || "".equals(ext))
			ext = urlPath.replaceFirst("^.*/[^/]*(\\.[^\\./]*|)$", "$1");
		String nodeName = urlPath.substring(urlPath.lastIndexOf("/")+1, urlPath.length());

		String fileName = nodeName;
		fileName = nodeName.replaceAll(" ", "-");
		if(!fileName.matches("(\\w|\\.|\\-|\\s|_)+")) {
			fileName = ""+getDateTime()+"."+ext;
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
		logger.debug("nodeName="+nodeName);
    	asset.setTitle(fileName);	
    	asset.setName(fileName);
		asset.setCreatedBy(username);
		asset.setPath(assetPath);
		asset.setLastModified(new Date());
		asset.setContentType(contentType);
		asset.setDevice(this.getDevice().getPath());
		
		//jcrService.addOrUpdate(asset);
		//jcrService.updateCalendar(path,"lastModified");
		//jcrService.setProperty(path, "changed", "true");	
		if(asset.getDevice()!=null) {
			Device device = (Device)jcrService.getObject(asset.getDevice());
			logger.debug("Writing device "+device.getPath() +":"+device.getLocation());
			
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
			long speed = file.length()*8/(end.getTime() - start.getTime()); 
			asset.setSize(file.length());
			asset.setOriginalDate(new Date(file.lastModified()));
			jcrService.addOrUpdate(asset);
			jcrService.updateCalendar(path,"lastModified");
			jcrService.setProperty(path, "changed", "true");
			asset.setTitle(asset.getTitle() +" - "+speed+"kb/s");
			//output.close();
		}else {
			logger.debug("Writing jcr");
	    	InputStream is = conn.getInputStream();
			jcrService.addFile(assetPath,"original",is,contentType);
			is.close();
		}
		logger.debug("Done");

		
		if(contentType != null && contentType.startsWith("image/")) {
			jcrService.autoRoateImage(assetPath);
			logger.debug("create icon");
			jcrService.createIcon(assetPath, 400,400);
		}		

		}catch (Exception e){
			ImageUtil.HDDOff();
			logger.error("error:"+e.getMessage());
			asset.setTitle("error:"+e.getMessage());
			
		}
		ImageUtil.HDDOff();
		return asset;
	}

	@RequestMapping(value = {"/site/importAssetMove.html"}, method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody Asset  assetsImportMove(String path,String uid, Model model,HttpServletRequest request, HttpServletResponse response) {
		Asset asset= new Asset();
		String username = getUsername();
		ImageUtil.HDDOn();
		try {
			asset = (Asset)jcrService.getObject(uid);//jcrService.getAssetById(uid);
			//String names[]=asset.getPath().split("/");
			//String nodeName = asset.getPath().split("/")[names.length-1];
			//String frompath = asset.getPath();
			String topath = path+"/"+getDateTime();
			String contentType = asset.getContentType();
			Device device = getDevice();

    		Asset new_asset = new Asset();
    		new_asset.setPath(topath);
    		new_asset.setTitle(asset.getTitle());
    		new_asset.setDevice(device.getPath());
    		new_asset.setCreatedBy(username);
    		new_asset.setLastModified(new Date());
    		new_asset.setContentType(asset.getContentType());
    		new_asset.setSize(asset.getSize());    		
    		jcrService.addOrUpdate(new_asset);
    		File fromFile = new File(device.getLocation()+asset.getPath());
    		if(asset.getDevice()!=null) {
    			Device d = (Device)jcrService.getObject(asset.getDevice());
    			fromFile = new File(d.getLocation()+asset.getPath());
    		}
    		if(device.getLocation()!=null) {
	    		File toFile = new File(device.getLocation()+topath);
	    		toFile.getParentFile().mkdirs();
	    		if(fromFile.exists()) {
	    			FileUtils.copyFile(fromFile, toFile);
	    		}else if(jcrService.nodeExsits(asset.getPath()+"/original")){
	    			OutputStream output = new FileOutputStream(toFile,true);
	    			jcrService.readAsset(asset.getPath()+"/original", output);
	    			output.close();
	    		} 
    		}else {
    			InputStream is = jcrService.getInputStream(asset.getPath()+"/original");
    			jcrService.addFile(topath, "original", is, contentType);
    		}
			if(contentType != null && contentType.startsWith("image/")) {
				jcrService.createIcon(topath, 400,400);
			}   
    		//jcrService.deleteNode(asset.getPath());
		}catch (Exception e){
			ImageUtil.HDDOff();
			logger.error("error:"+e.getMessage());
			asset.setTitle("error:"+e.getMessage());
			
		}
		ImageUtil.HDDOff();
		return asset;
	}	
	
	@RequestMapping(value = {"/site/importFolder.html"}, method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String folderImport(String path,String url,String override, Model model,HttpServletRequest request, HttpServletResponse response) {
		//Asset asset= new Asset();		
		String userName = getUsername();
		File folder = new File(url);
		if(folder.exists()) {
			assetManager.executeImportFolderOperationBackend(url, path, userName,override);
		}else {
			return "error:"+url+"&#36335;&#24452;&#27809;&#25214;&#21040;";
		}
		return url+"&#30446;&#24405;&#19979;&#25152;&#26377;&#25991;&#20214;&#23558;&#34987;&#19978;&#36733;&#12290;";
	}
	
	@RequestMapping(value = {"/site/updateProperty.html","/admin/updateProperty.html","/proptected/updateProperty.html"}, method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String  updateProperty(String uid,String path,String name, String value,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String result="";
		try {
			Page page = null;
			if(uid !=null && !"".equals(uid)) {
				result =  jcrService.updateProperty(uid,name, value);
				page = jcrService.getPageByUid(uid);
			}else if(path !=null && !"".equals(path)){
				result =  jcrService.updatePropertyByPath(path, name, value);
				page = jcrService.getPageByUid(uid);
			}else {
				return "error:";
			}
			

    		if((name.equals("jcr:title") || name.equals("order") || name.equals("status")) && page.getDepth()<=4 ) {
    			int level = 2;
    			
    			jcrService.setPageNavigation(result,level,20);
    			jcrService.updatePropertyByPath(path,"breadcrumb",jcrService.getBreadcrumb(path));
    			
    			//jcrService.setPageNavigation(result,level,20);    			
    		}			
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
			result = "error:"+e.getMessage();
			
		}
		return result;
	}	
	@RequestMapping(value = {"/site/updateContent.html"}, method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String  updateContent(String uid,String path,String content,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String result="";
		try {
			Asset asset = jcrService.getAssetById(uid);
			ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
			if(asset.getDevice()!=null) {
					File file = null;
					Device device = (Device)jcrService.getObject(asset.getDevice());
					file = new File(device.getLocation()+asset.getPath());
    				if(!file.getParentFile().exists()) {
    					file.getParentFile().mkdirs();
    				}
    				if(file.isDirectory()) {
    					file.setLastModified(new Date().getTime());
    					String ext = asset.getExt();
    					if(asset.getPath().lastIndexOf(".")>0)
    						ext = asset.getPath().substring(asset.getPath().lastIndexOf("."));
    					file = new File(file,"origin"+ext);
    				}
    				file.setLastModified(new Date().getTime());
    				OutputStream output = new FileOutputStream(file,false);
    				byte[] buffer = new byte[8 * 1024];
    				int byteToRead = 0;
    				while((byteToRead = in.read(buffer)) != -1) {
    					output.write(buffer,0,byteToRead);
    				}
    				in.close();
    				output.close();
    			}else {
    				//logger.info("Writing jcr");
    				jcrService.deleteNode(asset.getPath()+"/original");
        			jcrService.addFile(asset.getPath(),"original",in,asset.getContentType());
    			}
		
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
			result = "error:"+e.getMessage();
			
		}
		return result;
	}
	
	@RequestMapping(value = {"/site/publish.html"}, method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String  publish(String uid,String path,String name, String value,Model model,HttpServletRequest request, HttpServletResponse response) throws RepositoryException {
		String result="";
		if(name==null) name="status";
		if(value==null) value="true";
		BufferedWriter bufferWriter = null;
		//try {
			ImageUtil.HDDOn();
			Page page = null;
			//String template = getAssetContent("/templates/assets/structure/page.html");
			if(uid !=null && !"".equals(uid)) {
				result =  jcrService.updateProperty(uid,name, value);
				page = jcrService.getPageByUid(uid);
			}else if(path !=null && !"".equals(path)){
				result =  jcrService.updatePropertyByPath(path, name, value);
				page = jcrService.getPage(path);
			}else {
				logger.error("error: path is null");
				return "error:";
			}
			page.setStatus("true");
			//logger.info("home="+jcrService.getHome());
    		File file = new File(jcrService.getHome()+page.getPath()+".html");
    		if(!file.getParentFile().exists()) {
    			file.getParentFile().mkdirs();
    		}
			int level = 2;
			if(page.getShowThememenu()!=null && "true".equals(page.getShowThememenu()))
				level++;	
			jcrService.setPageNavigation(result,level,20);
			String navigation = jcrService.getPageNavigation(page.getPath(),level);
			if(navigation==null) {
				navigation = "";
			}
			if(!request.getContextPath().equals("/")) {
				navigation= navigation.replaceAll("/content/", request.getContextPath()+"/content/");
			}
			String menuPath=jcrService.getAncestorPath(page.getPath(), level);
			page.setMenuPath(menuPath);
    		
    		try {
				if(page.getDepth() < 6) {
					File menuFile = new File(jcrService.getHome()+menuPath+"/navimenu.html");
		    		if(!menuFile.getParentFile().exists()) {
		    			menuFile.getParentFile().mkdirs();
		    		}
						bufferWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(menuFile),"UTF-8"));
						bufferWriter.write(navigation);
						bufferWriter.close();	
				
				}
/*				Gson gson = new Gson();
				File json = new File(jcrService.getHome()+page.getPath()+".json");
				String jsonPage = gson.toJson(page);
				//bufferWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(json)));
				bufferWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(json),"UTF-8"));
				bufferWriter.write(jsonPage);
				bufferWriter.close();  */		
    		} catch (UnsupportedEncodingException e) {
    			logger.error(e.toString());
    			result = "UnsupportedEncodingException error:"+e.getMessage();
			} catch (FileNotFoundException e) {
				logger.error(e.toString());
				result = "FileNotFoundException error:"+e.getMessage();
			} catch (IOException e) {
				logger.error(e.toString());
				result = "IOException error:"+e.getMessage();
			}   		
			String breadcrumb = page.getBreadcrumb();
			if(breadcrumb==null) {
				breadcrumb = "";
			}
			if(!request.getContextPath().equals("/")) {
				breadcrumb= breadcrumb.replaceAll("/content/", request.getContextPath()+"/content/");
			
			}    		

    		
			String content = page.getContent();
			if(content==null) {
				logger.error("content is null");
			}
			content = content.replaceAll("-edit", "");
			try {
				content = publishAssets(page.getPath(),content);
			}catch(Exception e) {
				logger.error(e.toString());
				result = "publishAssets error:"+e.getMessage();	
			}

			try {
				if(page.getPasscode()!=null && !"".equals(page.getPasscode()) && "true".equals(page.getSecured())) {
					content = JwtUtil.encode(content);
				}
				if("true".equals(page.getSecured()))
					page.setPasscode(JwtUtil.encode(page.getPasscode()));
				page.setContent(content);
				page.setBreadcrumb(breadcrumb);
				page.setNavigation(navigation);
				Gson gson = new Gson();
				File json = new File(jcrService.getHome()+page.getPath()+".json");
				String jsonPage = gson.toJson(page);
				//bufferWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(json)));
				bufferWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(json),"UTF-8"));
				bufferWriter.write(jsonPage);
				bufferWriter.close();  				
			}catch(Exception e) {
				logger.error(e.toString());
				result = "setBreadcrumb error:"+e.getMessage();	
			}
 			

    		
/*		}catch (Exception e){
			logger.error(e.toString());
			result = "error:"+e.getMessage();
			
		}*/
		ImageUtil.HDDOff();
		return result;
	}
	
	@RequestMapping(value = {"/site/publishfolder.html"}, method = {RequestMethod.POST})
	public @ResponseBody String  publishFolder(String uid,String path,String name, String value,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String result="";
		int count = 0;
		try {
			Page page = null;
			if(uid !=null && !"".equals(uid)) {
				page = jcrService.getPageByUid(uid);
				jcrService.updateProperty(uid,name, value);
			}else if(path !=null && !"".equals(path)){
				page = jcrService.getPageByUid(uid);
			}else {
				return "error:";
			}			
			publish(uid,path,"status","true",model,request,response);
			count++;
			String pagesQuery = "select * from [nt:base] AS s WHERE ISDESCENDANTNODE(["+page.getPath()+"]) and s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
			WebPage<Page> pages = jcrService.queryPages(pagesQuery, 500, 0);

			for(Page p:pages.getItems()) {
				publish(p.getUid(),path,"status","true",model,request,response);
				count++;
			}
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
			result = "error:"+e.getMessage();
			
		}
		return result+count;
	}
	@RequestMapping(value = {"/site/unpublishfolder.html"}, method = {RequestMethod.POST})
	public @ResponseBody String  unpublishFolder(String uid,String path,String name, String value,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String result="";
		int count = 0;
		try {
			Page page = null;
			if(uid !=null && !"".equals(uid)) {
				page = jcrService.getPageByUid(uid);
			}else if(path !=null && !"".equals(path)){
				page = jcrService.getPageByUid(uid);
			}else {
				return "error:";
			}			
			unpublish(page);
			count++;
			String pagesQuery = "select * from [nt:base] AS s WHERE ISDESCENDANTNODE(["+page.getPath()+"]) and s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
			WebPage<Page> pages = jcrService.queryPages(pagesQuery, 1000, 0);

			for(Page p:pages.getItems()) {
				jcrService.updatePropertyByPath(p.getPath(), "status", "false");
				count++;
			}
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
			result = "error:"+e.getMessage();
			
		}
		return result+count;
	}

	
	@RequestMapping(value = {"/admin/updateNodeProperty.html","/site/updateNodeProperty.html","/protected/updateNodeProperty.html"}, method = {RequestMethod.POST})
	public @ResponseBody String  updateNodeProperty(String uid,String path,String name, String value,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String result="";
		try {
			if(uid !=null && !"".equals(uid)) {
				logger.info("value="+value);
				result =  jcrService.updateProperty(uid,name, value);

			}else if(path !=null && !"".equals(path)){
				result =  jcrService.updatePropertyByPath(path, name, value);

			}else {
				return "error:path is null = "+uid;
			}
			
		
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
			result = "error:"+e.getMessage();
			
		}
		return result;
	}

	@RequestMapping(value = {"/site/updateImage.html"}, method = {RequestMethod.POST})
	public @ResponseBody String  updateImage(String uid,String name, String value,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String result="";
		try {
			if(uid !=null && !"".equals(uid)) {
				Asset asset = jcrService.getAssetById(uid);
				Device device = getDevice();
				if(device!=null && device.getPath()!=null) {

					File file = new File(device.getLocation()+asset.getPath()+".html");
					if(!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					logger.info("write to:"+file.getAbsolutePath());
					OutputStream output = new FileOutputStream(file);
					output.write(value.getBytes());
					output.close();
					result =  jcrService.updateProperty(uid,"html", device.getLocation()+asset.getPath()+".html");
				}else {
					logger.info("write to:"+asset.getPath());
					InputStream in = new ByteArrayInputStream(value.getBytes("UTF-8")); 
        			jcrService.addFile(asset.getPath(),"imageHtml",in,"text/html");
					result =  jcrService.updateProperty(uid,"html", "imageHtml");

				}
				
			}else {
				return "error:path is null = "+uid;
			}
			
		
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
			result = "error:"+e.getMessage();
			
		}
		return result;
	}
	@RequestMapping(value = {"/site/rotateImage.html","/protected/rotateImage.html"}, method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String  rotateImage(String uid,Integer angle, Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String result="";
		Asset asset = null;
		ImageUtil.HDDOn();
		try {
			asset = jcrService.getAssetById(uid);

			Device device = (Device)jcrService.getObject(asset.getDevice());
			String infile = device.getLocation()+asset.getPath();
			if(ImageUtil.rotate(infile, infile, angle)!=0) {
				jcrService.roateImage(asset.getPath(), angle);
				jcrService.createFile(asset.getPath(), 400);				
			}else {
				infile = jcrService.getHome()+"/icon400/"+asset.getPath();
				if(ImageUtil.rotate(infile, infile, angle)!=0)
					jcrService.createIcon(asset.getPath(), 400, 400);				
			}

		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
			ImageUtil.HDDOff();
			result = "error:"+e.getMessage();
			
		}
		ImageUtil.HDDOff();
		return result;
	}	
	
	@RequestMapping(value = {"/site/delete.html","/protected/delete.html"}, method = RequestMethod.GET)
	public String deleteNodeConfirm(String uid,String path,String redirect,Model model,HttpServletRequest request, HttpServletResponse response) {
		ImageUtil.HDDOn();
			try {
				if(uid!=null && !uid.equals("")) {
					path = jcrService.getNodeById(uid);
				}else if( path==null || "".equals(path)) {
					model.addAttribute("error", "路径必须输入");
				}
				model.addAttribute("redirect", redirect);
				model.addAttribute("smartiNode", jcrService.getObject(path));
			} catch (RepositoryException e) {
				ImageUtil.HDDOff();
				model.addAttribute("error", "路径没找到"+path);
			}
		ImageUtil.HDDOff();
		return "site/deleteNode";
	}


	@RequestMapping(value = {"/site/deleteasset.html","/protected/deleteasset.html"}, method = RequestMethod.GET)
	public String deleteAssetConfirm(String uid,String path,String redirect,Model model,HttpServletRequest request, HttpServletResponse response) {
		ImageUtil.HDDOn();
			try {
				if(uid!=null && !uid.equals("")) {
					path = jcrService.getNodeById(uid);
				}else if( path==null || "".equals(path)) {
					model.addAttribute("error", "路径必须输入");
				}
				model.addAttribute("redirect", redirect);
				model.addAttribute("smartiNode", jcrService.getObject(path));
			} catch (RepositoryException e) {
				ImageUtil.HDDOff();
				model.addAttribute("error", "路径没找到"+path);
			}
		ImageUtil.HDDOff();
		return "site/deleteAsset";
	}
	
	@RequestMapping(value = {"/site/deletePage.html"}, method = RequestMethod.GET)
	public String deletePageConfirm(String uid,String path,String redirect,Model model,HttpServletRequest request, HttpServletResponse response) {
		ImageUtil.HDDOn();
			try {
				if(uid!=null && !uid.equals("")) {
					path = jcrService.getNodeById(uid);
				}else if( path==null || "".equals(path)) {
					model.addAttribute("error", "路径必须输入");
				}
				model.addAttribute("redirect", redirect);
				model.addAttribute("page", jcrService.getPage(path));
			} catch (RepositoryException e) {
				ImageUtil.HDDOff();
				model.addAttribute("error", "路径没找到"+path);
			}
		ImageUtil.HDDOff();
		return "site/deletePage";
	} 
	@RequestMapping(value = {"/site/deletePage.html"}, method = RequestMethod.POST)
	public String deletePage(String uid,String path,String redirect,Model model,HttpServletRequest request, HttpServletResponse response) {
		String result="";
		ImageUtil.HDDOn();
		if(uid!=null && !uid.equals("")) {
			try {
				path = jcrService.getNodeById(uid);
				Page page = jcrService.getPage(path);
				unpublish(page);
				jcrService.deleteNode(path);
			} catch (RepositoryException e) {
				logger.error(e.getMessage());
				ImageUtil.HDDOff();
				result = "error:"+e.getMessage();
			} catch (IOException e) {
				ImageUtil.HDDOff();
				logger.error(e.getMessage());
				result = "error:"+e.getMessage();
			}
		}
		ImageUtil.HDDOff();
		return result;
	} 

	@RequestMapping(value = {"/admin/delete.html","/site/delete.html","/protected/delete.html"}, method = RequestMethod.POST)
	public @ResponseBody String deleteNode(String uid,String path,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(uid!=null && !uid.equals("")) {
			path = jcrService.getNodeById(uid);
		}else if( path==null || "".equals(path)) {
			throw new Exception("路径没找到");
		}
		return jcrService.deleteNode(path);
	} 

	@RequestMapping(value = {"/site/deleteasset.html","/protected/deleteasset.html"}, method = RequestMethod.POST)
	public @ResponseBody String deleteAsset(String uid,String path,Model model,HttpServletRequest request, HttpServletResponse response) {

		try {
			if(uid!=null && !uid.equals("")) {
				path = jcrService.getNodeById(uid);
			}else if( path==null || "".equals(path)) {
				return "error:路径没找到";
			}
			File file = jcrService.getFile(path);
			if(file.isDirectory()) {
				FileUtils.cleanDirectory(file);
			}
			file.delete();

			return jcrService.deleteNode(path);
		} catch (RepositoryException e) {
			logger.error(e.getMessage());
			return "error:"+e.getMessage();
		} catch (IOException e) {
			logger.error(e.getMessage());
			return "error:"+e.getMessage();
		}		


	}

	@RequestMapping(value = {"/site/deleteassets.html","/protected/deleteasset.html"}, method = RequestMethod.POST)
	public @ResponseBody String deleteAssets(String[] uid,Model model,HttpServletRequest request, HttpServletResponse response) {
		if(uid!=null) {
			for(String id:uid) {

			
				try {
					Asset asset = jcrService.getAssetById(id);
					File file = jcrService.getFile(asset.getPath());
					if(file.isDirectory()) {
						FileUtils.cleanDirectory(file);
					}
					file.delete();
	
					jcrService.deleteNode(asset.getPath());
				} catch (RepositoryException e) {
					logger.error(e.getMessage());
					return "error:"+e.getMessage();
				} catch (IOException e) {
					logger.error(e.getMessage());
					return "error:"+e.getMessage();
				}
			}
		}else {
			return "error:路径没找到";
		}
		return "";
	}	
	@RequestMapping(value = {"/site/removeNode.html"}, method = RequestMethod.GET)
	public @ResponseBody String removeNode(String uid,String path,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(uid!=null && !uid.equals("")) {
			path = jcrService.getNodeById(uid);
		}else if( path==null || "".equals(path)) {
			throw new Exception("路径没找到");
		}
		return jcrService.deleteNode(path);
	}
	
	@RequestMapping(value = {"/site/add.html"}, method = RequestMethod.GET)
	public @ResponseBody String addComponent(String path,Model model,Djcontainer djcontainer,HttpServletRequest request, HttpServletResponse response) throws Exception {

		return super.deleteNode(model, request, response);
	}
	
	@RequestMapping(value = {"/site/profile.html"}, method = RequestMethod.GET)
	public String profile(String path,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String username = getUsername();
		User user  = (User)jcrService.getObject("/system/users/"+username);
/*		if(path !=null) {
			user = (User)jcrService.getObject(path);
		}
		if(!user.getUserName().equals(username)) {
			response.setStatus(HttpStatus.SC_UNAUTHORIZED);
		}*/
		File f = new File(jcrService.getDevice());
		model.addAttribute("usage",""+f.getUsableSpace()/1000000+"MB/"+f.getTotalSpace()/1000000+"MB");
    	String imgs[] = {"shu","niu","fu","tu","long","she","ma","yang","hou","ji","gou","zhu"};
    	String ids[] = {"A0","A1","A2","B0","B1","B2","C0","C1","C2","D0","D1","D2"};
    	Page page = new Page();
    	//page.setTitle("&#27880;&#20876;");
    	page.setTitle(messageSource.getMessage("djn.signup", null,"\u6CE8\u518C", localeResolver.resolveLocale(request)));
    	Random rnd = new Random();
    	for(int i = imgs.length - 1; i >0 ; i--) {
    		int index = rnd.nextInt(i);
    		String a = imgs[index];
    		String b = ids[index];
    		imgs[index] = imgs[i];
    		ids[index] = ids[i];
    		imgs[i] = a;
    		ids[i] = b;
    	}
    	model.addAttribute("imgs", imgs);    
    	model.addAttribute("ids", ids);


		model.addAttribute("user", user);
		return "site/profile";
	}
	
	@RequestMapping(value = {"/site/profile.html"}, method = RequestMethod.POST)
	public String profileUpdate(User user,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String username = getUsername();
		User dbuser  = (User)jcrService.getObject("/system/users/"+username);
		dbuser.setSigningKey(user.getSigningKey());
		jcrService.addOrUpdate(dbuser);
		if(!jcrService.nodeExsits("/templates/assets/bash")) {
			jcrService.addNodes("/templates/assets/bash", "nt:unstructured", username);
		}

		dbuser.setSigningKey("dajanaSigningKey");
		Gson gson = new Gson();
		String bash = "curl -F content="+JwtUtil.encode(gson.toJson(dbuser)) +" http://dajana.cn:8888/dynds";
		File file = new File(getDevice().getLocation()+"/templates/assets/bash/dydns.sh");
		if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
		BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
		bufferWriter.write("#!bash\r\n");
		bufferWriter.write(bash);
		bufferWriter.close();
		long length = file.length();
		if(!jcrService.nodeExsits("/templates/assets/bash/dydns.sh")) {
			Asset asset = new Asset();
			asset.setPath("/templates/assets/bash/dydns.sh");
			asset.setLastModified(new Date());
			asset.setName("dydns.sh");
			asset.setContentType("text/plain");
			asset.setTitle("Dynamic DNS");
			asset.setSize(length);
			asset.setDevice(getDevice().getPath());
			jcrService.addOrUpdate(asset);
		}else {
			Asset asset = (Asset)jcrService.getObject("/templates/assets/bash/dydns.sh");
			asset.setSize(length);
			asset.setLastModified(new Date());
			jcrService.addOrUpdate(asset);
		}
		return "redirect:/signin?info=pwchanged&username="+username;
	}
	
	@RequestMapping(value = {"/site/doc2pdf.pdf"}, method = {RequestMethod.GET,RequestMethod.POST,RequestMethod.HEAD})
	public @ResponseBody String doc2pdf(String path,HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException  {

		try {
			//String pdfpath = path.substring(0, path.lastlastIndexOf("."))+".pdf";


			File pdffile = new File(jcrService.getDevice()+path+"/origin.pdf");
			if(pdffile.exists()) {
/*				FileInputStream in = new FileInputStream(pdffile);
				response.setContentType("application/pdf");
				IOUtils.copy(in, response.getOutputStream());
				in.close();	*/
				super.serveResource(request, response, pdffile, "application/pdf");

				return null;
			}
    		String ext = "";
    		if(path.lastIndexOf(".")>0) {
    			ext = path.substring(path.lastIndexOf("."));
    		}

			if(path !=null  && jcrService.nodeExsits(path)) {
				Asset asset = (Asset)jcrService.getObject(path);
				if(asset.getDevice()!=null) {
					File file = null;
					Device device = (Device)jcrService.getObject(asset.getDevice());
					String docname = device.getLocation()+asset.getPath()+"/origin"+ext;
					String pdfname = device.getLocation()+asset.getPath()+"/origin.pdf";
					//String pdfname = device.getLocation()+asset.getPath().replaceFirst(".docx", ".pdf").replaceFirst(".doc", ".pdf").replaceFirst(".rtf", ".pdf");
					file = new File(pdfname);
					if(!file.exists()) {
						try {
							int exit = ImageUtil.doc2pdf(docname, file.getParentFile().getAbsolutePath());
							if(exit != 0) {

								return "文件转换将在15分钟内完成！";									
							}
						} catch (InterruptedException e) {
							logger.error("doc2pdf:"+e.getMessage());

							return e.getMessage();
						}
					}
/*					FileInputStream in = new FileInputStream(file);
					response.setContentType("application/pdf");
					IOUtils.copy(in, response.getOutputStream());
					in.close();				*/	
					file.setReadOnly();
					super.serveResource(request, response, file, "application/jpdf");

				}
			}
		} catch (RepositoryException e) {
			logger.error("doc2pdf:"+e.getMessage());

			return e.getMessage();
		} catch (FileNotFoundException e) {
			logger.error("doc2pdf.pdf:"+e.getMessage());

			return e.getMessage();
		} catch (IOException e) {
			logger.error("doc2pdf.pdf:"+e.getMessage());

			return e.getMessage();
		} catch (Exception e) {
			logger.error("pdf2pdf.pdf:"+e.getMessage());

			return e.getMessage();
		}

		return null;
	}
	
	@RequestMapping(value = {"/site/doc2pdf.jpg"}, method = {RequestMethod.GET,RequestMethod.POST,RequestMethod.HEAD})
	public @ResponseBody String doc2pdf2jpg(String path,Integer p,HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException  {

		try {
			if (p==null) p=0;
			String pdfpath = path+"/origin.pdf";
			String jpgpath = path+"/origin"+"-"+p+".jpg";
			File pdffile = new File(jcrService.getDevice()+pdfpath);
			File jpgfile = new File(jcrService.getDevice()+jpgpath);
			if(pdffile.exists() && !jpgfile.exists()) {
				if(ImageUtil.pdf2jpg(pdffile.getAbsolutePath(), p, "1600x1600", jpgfile.getAbsolutePath())==0) {
/*					FileInputStream in = new FileInputStream(jpgfile);
					response.setContentType("image/jpeg");
					IOUtils.copy(in, response.getOutputStream());
					in.close();	*/
					super.serveResource(request, response, jpgfile, "image/jpeg");

					return null;					
				}
					
			}
			if(jpgfile.exists()) {
/*				FileInputStream in = new FileInputStream(jpgfile);
				response.setContentType("image/jpeg");
				IOUtils.copy(in, response.getOutputStream());
				in.close();	*/
				jpgfile.setReadOnly();
				super.serveResource(request, response, jpgfile, "image/jpeg");

				return null;
			}

    		String ext = "";
    		if(path.lastIndexOf(".")>0) {
    			ext = path.substring(path.lastIndexOf("."));
    		}
			if(path !=null  && jcrService.nodeExsits(path)) {
				Asset asset = (Asset)jcrService.getObject(path);
				if(asset.getDevice()!=null) {
					File file = null;
					Device device = (Device)jcrService.getObject(asset.getDevice());
					String docname = device.getLocation()+asset.getPath()+"/orogin"+ext;
					String pdfname = device.getLocation()+asset.getPath()+"/orogin.pdf";//device.getLocation()+asset.getPath().replaceFirst(".docx", ".pdf").replaceFirst(".doc", ".pdf").replaceFirst(".rtf", ".pdf");
					file = new File(pdfname);
					if(!file.exists()) {
						try {
							int exit = ImageUtil.doc2pdf(docname, file.getParentFile().getAbsolutePath());
							if(exit != 0) {
								ImageUtil.HDDOff();
								return "文件转换将在15分钟内完成！";									
							}
						} catch (InterruptedException e) {
							logger.error("doc2pdf:"+e.getMessage());
							ImageUtil.HDDOff();
							return e.getMessage();
						}
					}
					if(pdffile.exists() && !jpgfile.exists()) {
						if(ImageUtil.pdf2jpg(pdffile.getAbsolutePath(), p, "1600x1600", jpgfile.getAbsolutePath())==0) {
/*							FileInputStream in = new FileInputStream(jpgfile);
							response.setContentType("image/jpeg");
							IOUtils.copy(in, response.getOutputStream());
							in.close();	*/
							jpgfile.setReadOnly();
							super.serveResource(request, response, jpgfile, "image/jpeg");
							return null;					
						}
							
					}					
				}
				
			}
		} catch (RepositoryException e) {
			logger.error("doc2pdf.jpg:"+e.getMessage());

			return e.getMessage();
		} catch (FileNotFoundException e) {
			logger.error("doc2pdf.jpg:"+e.getMessage());

			return e.getMessage();
		} catch (IOException e) {
			logger.error("doc2pdf.jpg:"+e.getMessage());

			return e.getMessage();
		} catch (Exception e) {
			logger.error("pdf2pdf.jpg:"+e.getMessage());

			return e.getMessage();
		}


		return null;
	}

	
	@RequestMapping(value = {"/site/pdf2jpg.jpg"}, method = {RequestMethod.GET,RequestMethod.POST,RequestMethod.HEAD})
	public @ResponseBody String pdf2jpg(String path,HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException  {

		try {
			if(path==null) {
				response.sendRedirect("/resources/images/pdf-icon.png");									

			}
			String pdfpath = path+"/origin.pdf";

			File file = null;
			String jpgname = jcrService.getDevice()+path+"/x400"+".jpg";
			String pdfname = jcrService.getDevice()+pdfpath;
			file = new File(jpgname);
			if(!file.exists()) {

				int exit = ImageUtil.pdf2jpg(pdfname,0,"400x400", jpgname);
				if(exit != 0) {
					response.sendRedirect("/resources/images/pdf-icon.png");									
				}

			}
/*			FileInputStream in = new FileInputStream(file);
			response.setContentType("image/jpeg");
			IOUtils.copy(in, response.getOutputStream());
			in.close();		*/	
			file.setReadOnly();
			super.serveResource(request, response, file, "image/jpeg");
		} catch (FileNotFoundException e) {
			logger.error("pdf2jpg:"+e.getMessage());

			return e.getMessage();
		} catch (IOException e) {
			logger.error("pdf2jpg:"+e.getMessage());

			return e.getMessage();
		} catch (Exception e) {
			logger.error("pdf2jpg:"+e.getMessage());

			return e.getMessage();
		}


		return null;
	}
	
	@RequestMapping(value = {"/site/pdf2img.jpg"}, method = {RequestMethod.GET,RequestMethod.POST,RequestMethod.HEAD})
	public @ResponseBody String pdf2img(String path,Integer p,HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException  {
	
		try {
			if(path==null) {
				response.sendRedirect("/resources/images/pdf-icon.png");									

			}
			if(p==null) p=0;
			//String pdfbase = path.substring(0, path.lastlastIndexOf("."));
			String pdfpath = path+"/origin.pdf";

			File file = null;
			String jpgname = jcrService.getDevice()+path+"/origin-"+p+".jpg";
			String pdfname = jcrService.getDevice()+pdfpath;
			file = new File(jpgname);
			if(!file.exists()) {

				int exit = ImageUtil.pdf2jpg(pdfname,p,"1600x1600", jpgname);
				if(exit != 0) {
					response.sendRedirect("/resources/images/pdf-icon.png");									
				}

			}
/*			FileInputStream in = new FileInputStream(file);
			response.setContentType("image/jpeg");
			IOUtils.copy(in, response.getOutputStream());
			in.close();			*/
			file.setReadOnly();
			super.serveResource(request, response, file, "image/jpeg");
		} catch (FileNotFoundException e) {
			logger.error("pdf2img:"+e.getMessage());

			return e.getMessage();
		} catch (IOException e) {
			logger.error("pdf2img:"+e.getMessage());

			return e.getMessage();
		} catch (Exception e) {
			logger.error("pdf2img:"+e.getMessage());

			return e.getMessage();
		}


		return null;
	}
	

	@RequestMapping(value = {"/site/doc2jpg.jpg"}, method = {RequestMethod.GET,RequestMethod.POST,RequestMethod.HEAD})
	public @ResponseBody String doc2jpg(String path,Integer p,HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException  {


		try {
			if(path==null) {
				response.sendRedirect("/resources/images/word-icon.png");									

			}
			if(p==null) p=0;
			File file = null;
			//String filebase = path.substring(0, path.lastlastIndexOf("."));
			String jpgname = jcrService.getDevice()+path+"/origin-"+p+".jpg";
			String pdfpath = path+"/origin.pdf";
			String pdfname = jcrService.getDevice()+ pdfpath;
			file = new File(jpgname);
			if(!file.exists()) {

				int exit = ImageUtil.pdf2jpg(pdfname,p,"1600x1600", jpgname);
				if(exit != 0) {
				    if(path.endsWith(".doc") || path.endsWith(".docx")) {	
						response.sendRedirect("/resources/images/word-icon.png");	
				    }else if(path.endsWith(".xls") || path.endsWith(".xlsx") || path.endsWith(".csv") || path.endsWith(".rtf")) {
				    	response.sendRedirect("resources/images/excel-icon.png");
				    }else if(path.endsWith(".ppt")) {
				    	response.sendRedirect("resources/images/ppt-icon.png");
				    }else {
				    	response.sendRedirect("resources/images/document-icon.png");
				    }
				}

			}
/*			FileInputStream in = new FileInputStream(file);
			response.setContentType("image/jpeg");
			IOUtils.copy(in, response.getOutputStream());
			in.close();	*/	
			file.setReadOnly();
			super.serveResource(request, response, file, "image/jpeg");
		} catch (FileNotFoundException e) {
			logger.error("doc2jpg:"+e.getMessage());

			return e.getMessage();
		} catch (IOException e) {
			logger.error("doc2jpg:"+e.getMessage());

			return e.getMessage();
		} catch (Exception e) {
			logger.error("doc2jpg:"+e.getMessage());

			return e.getMessage();
		}

		return null;
	}	
	@RequestMapping(value = {"/site/video2jpg.jpg"}, method = {RequestMethod.GET,RequestMethod.POST,RequestMethod.HEAD})
	public @ResponseBody String video2jpg(String path,HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException  {
		ImageUtil.HDDOn();	
		try {
			if(path==null) {
				response.sendRedirect("/resources/images/video-icon.png");									
				
			}
    		String ext = "";
    		if(path.lastIndexOf(".")>0) {
    			ext = path.substring(path.lastIndexOf("."));
    		}			
			File file = null;
			String infile = jcrService.getDevice()+path+"/origin"+ext;
			String jpgname = jcrService.getDevice()+path+"/x400"+".jpg";
			file = new File(jpgname);
			if(!file.exists()) {
				if(ImageUtil.video2jpg(infile, "400x300", jpgname) !=0)
					response.sendRedirect("/resources/images/video-icon.png");									
			}
			file.setReadOnly();
			super.serveResource(request, response, file, "image/jpeg");
/*			FileInputStream in = new FileInputStream(file);
			response.setContentType("image/jpeg");
			IOUtils.copy(in, response.getOutputStream());
			in.close();		*/				
		} catch (FileNotFoundException e) {
			logger.error("doc2jpg:"+e.getMessage());
			ImageUtil.HDDOff();
			return e.getMessage();
		} catch (IOException e) {
			logger.error("doc2jpg:"+e.getMessage());
			ImageUtil.HDDOff();
			return e.getMessage();
		} catch (Exception e) {
			logger.error("doc2jpg:"+e.getMessage());
			ImageUtil.HDDOff();
			return e.getMessage();
		}

		ImageUtil.HDDOff();
		return null;
	}	
	@RequestMapping(value = {"/site/video.mp4"}, method = {RequestMethod.GET,RequestMethod.POST,RequestMethod.HEAD})
	public ResponseEntity<InputStreamResource> video2mp4(String path,HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException  {
		ImageUtil.HDDOn();	
		try {
    		String ext = "";
    		if(path.lastIndexOf(".")>0) {
    			ext = path.substring(path.lastIndexOf("."));
    		}
			File file = new File(jcrService.getDevice()+path+"/origin"+ext+".mp4");
			String contentType="video/mp4";
			if(!file.exists()) {
				file = new File(jcrService.getDevice()+path+"/origin"+ext);
				logger.debug("video original:"+file.getAbsolutePath());
				try {
					Asset asset = (Asset)jcrService.getObject(path);
					contentType=asset.getContentType();
				} catch (RepositoryException e1) {
					logger.error(e1.getMessage());
				}				
			}
			file.setReadOnly();

			serveResource(request,response,file,contentType);


				
		} catch (FileNotFoundException e1) {
			logger.error("video2mp4:"+e1.getMessage());
			//ImageUtil.HDDOff();

		} catch (IOException e1) {
			logger.error("video2mp4:"+e1.getMessage());
			//ImageUtil.HDDOff();

		} catch (Exception e) {
			logger.error("video2mp4:"+e.getMessage());
		}
		
		ImageUtil.HDDOff();

		return null;
	}

	@RequestMapping(value = {"/site/video.webm"}, method = {RequestMethod.GET,RequestMethod.POST,RequestMethod.HEAD})
	public @ResponseBody String video2webm(String path,HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException  {
		ImageUtil.HDDOn();	
		try {
			File file = new File(jcrService.getDevice()+path+".webm");
			String contentType="video/webm";
			if(!file.exists() || (path.endsWith(".webm") && isIntranet(request))) {
				file = new File(jcrService.getDevice()+path);
				logger.debug("video original:"+file.getAbsolutePath());
				try {
					Asset asset = (Asset)jcrService.getObject(path);
					contentType=asset.getContentType();
				} catch (RepositoryException e1) {
					logger.error(e1.getMessage());
				}				
			}			
			serveResource(request,response,file,contentType);

					
		} catch (FileNotFoundException e) {
			logger.error("videowebm:"+e.getMessage());
			ImageUtil.HDDOff();
			return e.getMessage();
		} catch (IOException e) {
			logger.error("video2webm:"+e.getMessage());
			ImageUtil.HDDOff();
			return e.getMessage();
		} catch (Exception e) {
			logger.error("video2webm:"+e.getMessage());
		}

		ImageUtil.HDDOff();
		return null;
	}
	@RequestMapping(value = {"/site/file/*.*","/site/viewimage","/content/viewimage","/content/**/viewimage","/protected/viewimage","/protected/**/viewimage","/site/file","/site/file*.*","/content/file","/content/file*.*","/content/**/file","/content/**/file*.*"}, method = {RequestMethod.GET,RequestMethod.POST,RequestMethod.HEAD})
	public @ResponseBody String viewFile(String uid,String path,Integer w,HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException  {

		Integer width = null;
		if(w !=null && w <= 12) {
			width = w*100;
			if(width==0)
				width = 48;
		}
		try {
			if(uid!=null && (path==null || "".equals(path)))
				path = jcrService.getNodeById(uid);
			File outFile = new File(jcrService.getDevice()+path);
			if(outFile.exists() && outFile.isFile()) {
				logger.debug("path is file output:"+outFile.getAbsolutePath());
				super.serveResource(request, response, outFile, null);
				return null;					
			}
			String ext = ".jpg";
			if(path.lastIndexOf(".")>0) ext = path.substring(path.lastIndexOf("."));
			outFile = new File(jcrService.getDevice()+path+(width==null?"/origin"+ext:"/x"+width+".jpg"));
			if(outFile.exists() && outFile.isFile()) {
				logger.debug("output:"+outFile.getAbsolutePath());
				super.serveResource(request, response, outFile, null);
				return null;					
			}

			if(path !=null  && jcrService.nodeExsits(path)) {
				Asset asset = (Asset)jcrService.getObject(path);

/*				if(width!=null && jcrService.nodeExsits(path+"/file-"+width)) {
					response.setContentType(asset.getContentType());
					jcrService.readAsset(path+"/file-"+width, response.getOutputStream());
				}else */
				if(asset.getDevice()!=null) {
					Device device = (Device)jcrService.getObject(asset.getDevice());
					File file = new File(device.getLocation()+asset.getPath()+"/origin"+ext);
					if(file.exists() && asset.getWidth() == null && asset.getContentType().startsWith("image/")) {
						jcrService.autoRoateImage(path);
					}
					if(width!=null && file.exists()) {
						String iconfile = device.getLocation()+asset.getPath()+"/x"+width+".jpg";
						File icon = new File(iconfile);
						
						if(!icon.exists()) {
							jcrService.createIcon(path, width,width);
						}
						if(icon.exists())
							file = icon;

					}
					if(file.exists()) {
						file.setReadOnly();
						super.serveResource(request, response, file, null);
					}else  if(jcrService.nodeExsits(path+"/original")) {
						response.setContentType(asset.getContentType());
						if(asset.getSize()!=null)
							response.setContentLength(asset.getSize().intValue());
						if(asset.getOriginalDate()!=null)
							response.setDateHeader("Last-Modified", asset.getOriginalDate().getTime());
						
						file = new File(getDevice().getLocation()+asset.getPath());
						file.getParentFile().mkdirs();
						OutputStream out = new FileOutputStream(file);
						jcrService.readAsset(path+"/original",  out);
						asset.setDevice(getDevice().getPath());
						jcrService.addOrUpdate(asset);
						
						out.close();
						super.serveResource(request, response, file, null);
						//jcrService.readAsset(path+"/original",  response.getOutputStream());

						return null;
						
					}
/*					FileInputStream in = new FileInputStream(file);
					response.setContentType(asset.getContentType());
					IOUtils.copy(in, response.getOutputStream());
					in.close();*/

					return null;
				}else  if(jcrService.nodeExsits(path+"/original")) {
					response.setContentType(asset.getContentType());
					if(asset.getSize()!=null)
						response.setContentLength(asset.getSize().intValue());
					if(asset.getOriginalDate()!=null)
						response.setDateHeader("Last-Modified", asset.getOriginalDate().getTime());
					
					File file = new File(getDevice().getLocation()+asset.getPath());
					file.getParentFile().mkdirs();
					OutputStream out = new FileOutputStream(file);
					jcrService.readAsset(path+"/original",  out);
					asset.setDevice(getDevice().getPath());
					jcrService.addOrUpdate(asset);
					
					out.close();
					file.setReadOnly();
					super.serveResource(request, response, file, null);
					//jcrService.readAsset(path+"/original",  response.getOutputStream());

					return null;
				}else {

					return path +" original file not found";
				}
			}else {

				return path +" file not found";		
			}

		}catch(Exception e) {
			logger.error("viewFile:"+e.getMessage()+",path="+path);

			return e.getMessage();
		}
		
		
	} 
	
	@RequestMapping(value = {"/templates/**/*.*"}, method = {RequestMethod.GET,RequestMethod.POST,RequestMethod.HEAD})
	public @ResponseBody String viewTempates(Integer w,HttpServletRequest request, HttpServletResponse response) throws Exception  {

		String path = URLDecoder.decode(request.getRequestURI(),"UTF-8");
		if(!request.getContextPath().equals("/"))
			path = path.replaceFirst(request.getContextPath(), "");
		Integer width = null;
		if(w !=null && w <= 12) {
			width = w*100;
			if(width==0)
				width = 48;
		}
		try {
			File outfile = new File(jcrService.getDevice()+path);
			String ext = path.substring(path.lastIndexOf("."));
			if(outfile.isDirectory()) {
				outfile = new File(jcrService.getDevice()+path+"/origin"+ext);
			}
			if(outfile.exists()) {
				super.serveResource(request, response, outfile, null);				
			}else if(path !=null && jcrService.nodeExsits(path)) {
				Asset asset = (Asset)jcrService.getObject(path);
				if(width!=null && jcrService.nodeExsits(path+"/file-"+width)) {
					response.setContentType(asset.getContentType());
					jcrService.readAsset(path+"/file-"+width, response.getOutputStream());
				}else if(asset.getDevice()!=null) {
					File file = null;
					Device device = (Device)jcrService.getObject(asset.getDevice());
					file = new File(device.getLocation()+asset.getPath());
					ext = asset.getPath().substring(asset.getPath().lastIndexOf("."));
					if(file.isDirectory()) {
						file = new File(jcrService.getDevice()+asset.getPath()+"/origin"+ext);
					}
/*
					FileInputStream in = new FileInputStream(file);
					response.setContentType(asset.getContentType());
					response.setContentLength((int)file.length());

					IOUtils.copy(in, response.getOutputStream());	
					in.close();*/
					if(file.exists()) {
						super.serveResource(request, response, file, null);
						return null;						
					}else {
						if(jcrService.nodeExsits(path+"/original")) {
							response.setContentType(asset.getContentType());
							file = new File(device.getLocation()+asset.getPath());
							file.getParentFile().mkdirs();
							OutputStream out = new FileOutputStream(file);
							jcrService.readAsset(path+"/original",  out);
							asset.setDevice(device.getPath());
							jcrService.addOrUpdate(asset);
							
							out.close();
							if(file.exists()) {
								super.serveResource(request, response, file, null);
								return null;	
							}else {
								jcrService.readAsset(path+"/original",  response.getOutputStream());
								//logger.error("jcrService.readAsset"+file.getAbsolutePath());
								//response.setStatus(404);
							}
						}else 
							response.setStatus(404);
					}

				}else  if(jcrService.nodeExsits(path+"/original")) {
					response.setContentType(asset.getContentType());
					//OutputStream output = response.getOutputStream();
					//jcrService.readAsset(path+"/original",  output);
					Device device = getDevice();
					File file = new File(device.getLocation()+asset.getPath());
					file.getParentFile().mkdirs();
					OutputStream out = new FileOutputStream(file);
					jcrService.readAsset(path+"/original",  out);
					asset.setDevice(device.getPath());
					jcrService.addOrUpdate(asset);
					out.close();
					if(file.exists()) {
						super.serveResource(request, response, file, null);
						return null;	
					}else {
						jcrService.readAsset(path+"/original",  response.getOutputStream());

						//logger.error("jcrService.readAsset original:"+file.getAbsolutePath());
						//response.setStatus(404);
					}
				}else 
					response.setStatus(404);
					//return path +" original file not found";
			}else
				response.setStatus(404);
				//return path +" file not found";		

		}catch(Exception e) {
			logger.error("templates:"+e.getMessage());
			response.setStatus(404);
			//return e.getMessage();
		}

		
		return null;
		
	} 	
	@RequestMapping(value = {"/site/viewpdf","/site/viewpdf.pdf","/content/viewpdf","/content/**/viewpdf"}, method = {RequestMethod.GET})
	public @ResponseBody String viewPdf(String uid[],HttpServletRequest request, HttpServletResponse response) throws IOException, RepositoryException {
		List<Asset> assets = new ArrayList<Asset>();
		ImageUtil.HDDOn();
		try {
			for(String id:uid) {
				assets.add(jcrService.getAssetById(id));
			}
			jcrService.assets2pdf(assets, response.getOutputStream());			
		}catch(Exception e) {
			logger.error("viewPdf"+e.getMessage());
			ImageUtil.HDDOff();
			return "Error:"+e.getMessage();
		}
		ImageUtil.HDDOff();
		return null;
	}

/*	@RequestMapping(value = {"/importWord.html"}, method = {RequestMethod.GET})
	public @ResponseBody String importWord(String path,HttpServletRequest request, HttpServletResponse response) throws IOException, RepositoryException {
		try {
		    String result = jcrService.extractWord(path);
		    return result;
		}catch(Exception e) {
			logger.error(e.getLocalizedMessage());
			return "Error:"+e.getMessage();
		}

	}	*/
	@RequestMapping(value = {"/site/viewf2p","/site/viewf2p.pdf","/content/viewf2p","/content/**/viewf2p"}, method = {RequestMethod.GET})
	public @ResponseBody String viewf2p(String path,HttpServletRequest request, HttpServletResponse response) throws IOException, RepositoryException {
		ImageUtil.HDDOn();
		try {
			String assetsQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+path+"]) and s.delete not like 'true' and s.ocm_classname='com.filemark.jcr.model.Asset' order by s.lastModified desc, s.name";
			WebPage<Asset> result = jcrService.searchAssets(assetsQuery, 100, 0);
			
			jcrService.assets2pdf(result.getItems(), response.getOutputStream());			
		}catch(Exception e) {
			logger.error("viewf2p:"+e.getMessage());
			ImageUtil.HDDOff();
			return "Error:"+e.getMessage();
		}
		ImageUtil.HDDOff();
		return null;
	}
	
	@RequestMapping(value = {"/site/viewfolder"}, method = {RequestMethod.GET})
	public @ResponseBody String viewFolder(String uid,String path,Integer w,HttpServletRequest request, HttpServletResponse response) throws IOException, RepositoryException {
		ImageUtil.HDDOn();	
		Integer width = null;
		if(w !=null && w <= 12) {
			width = w*100;
			if(width == 0)
				width = 48;
		}
		try {
			if(path==null)
				path = jcrService.getNodeById(uid);
			if(path !=null) {
				//Folder folder = jcrService.getFolder(path);
				if(!jcrService.nodeExsits(path+"/original")) {
					
					jcrService.readAsset("/templates/assets/folder360x360.png/original", response);
					return null;
					//jcrService.updateFolderIcon(path);
				//}else if(folder.getLastUpdated()==null || folder.getLastModified() == null || folder.getLastModified().after(folder.getLastUpdated())) {
					//new CreateFolderIconOperation(jcrService,path).run();
	    			//logger.info("Update folder Icon="+path);   
	    			//jcrService.updateFolderIcon(path);
					//assetManager.executeCreateFolderIconOperationFrontend(path);
				}
				if(width!=null) {
					if(jcrService.nodeExsits(path+"/file-"+width)) {
						jcrService.readAsset(path+"/file-"+width, response);
					}else {
						jcrService.createFile(path,width);
						jcrService.readAsset(path+"/file-"+width, response);
					}
				}else
					jcrService.readAsset(path+"/original", response);
				ImageUtil.HDDOff();
				return null;
	
			}else {
				ImageUtil.HDDOff();
				return path +" file not found";
			}
		}catch(Exception e) {
			logger.error("viewFolder:"+e.getMessage());
			ImageUtil.HDDOff();
			return e.getMessage();
		}
		
		
	}	
	@RequestMapping(value = {"/site/carousel.html","/content/carousel.html","/content/**/carousel.html"}, method = {RequestMethod.GET})
	public String carousel(String uid,String path,Integer m,Model model,HttpServletRequest request, HttpServletResponse response) throws IOException, RepositoryException {
        if(path==null)
        	 path = "/"+getUsername()+"/assets";
		if(uid != null) {
			path = jcrService.getNodeById(uid);
		}
		if(!jcrService.nodeExsits(path)) {
			jcrService.addNodes(path, "nt:unstructured",getUsername());	
		}
		
		Folder currentNode = jcrService.getFolder(path);
		String orderby = "lastModified desc";
		if(currentNode.getOrderby()!=null && !"".equals(currentNode.getOrderby())) {
			orderby = currentNode.getOrderby();
		}			
		String assetsQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+path+"])" +" and s.deleted not like 'true' and contentType like 'image%' and s.ocm_classname='com.filemark.jcr.model.Asset' order by s."+orderby+", s.name";
		if (m==null) m = 6;
		WebPage<Asset> assets = jcrService.searchAssets(assetsQuery, m, 0);
		if(assets.getPageCount()==0) {
			assetsQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE([/templates/assets/theme/carousel])" +" and s.deleted not like 'true' and s.ocm_classname='com.filemark.jcr.model.Asset' order by s.lastModified desc, s.name";
			assets = jcrService.searchAssets(assetsQuery, m, 0);
		}
		model.addAttribute("uid", uid);
		model.addAttribute("assets", assets); 
		return "site/carousel";
	}
	
	@RequestMapping(value = {"/site/suggestions.html"}, method = {RequestMethod.GET})
	public String suggestion(String path,Model model,HttpServletRequest request, HttpServletResponse response) throws IOException, RepositoryException {
		if(!request.getContextPath().equals("/")) {
			path = path.replaceFirst(request.getContextPath(), "");
		}
		String paths[] = path.split("/");
		if(paths.length>3) {
			String templatePath = "/content/templates/"+paths[3];
			String templatesQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+templatePath+"])  and s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
			WebPage<Page> templates = jcrService.queryPages(templatesQuery, 100, 0);
			model.addAttribute("templates", templates);
		}
		return "site/suggestions";
	}
/*	
	private String getAssetContent(String path) throws RepositoryException, IOException {
		String content = null;
		if(jcrService.nodeExsits(path)) {
			Asset asset = (Asset)jcrService.getObject(path);
			content = getContent(asset);
		}
		return content;
	}
	*/
	private String getContent(Asset asset) throws RepositoryException, IOException {
		String content = "";
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		if(asset.getDevice()!=null) {
			File file = null;
			Device device = (Device)jcrService.getObject(asset.getDevice());
			file = new File(device.getLocation()+asset.getPath());
			if(file.isDirectory()) {
				String ext = asset.getExt();
				if(asset.getPath().lastIndexOf(".")>0)
					ext = asset.getPath().substring(asset.getPath().lastIndexOf("."));
				file = new File(file,"origin"+ext);
			}
			FileInputStream in = new FileInputStream(file);
			byte[] buffer = new byte[8 * 1024];
			int byteToRead = 0;
			while((byteToRead = in.read(buffer)) != -1) {
				output.write(buffer,0,byteToRead);
			}
			in.close();
			content = new String(output.toByteArray(),"UTF-8");			
			output.close();	
		}else  if(jcrService.nodeExsits(asset.getPath()+"/original")) {
			
			jcrService.readAsset(asset.getPath()+"/original",  output);
			content = new String(output.toByteArray(),"UTF-8");
			output.close();
		}
		return content;
	}
	
	private void unpublish(Page page) throws RepositoryException, IOException {
		    jcrService.updatePropertyByPath(page.getPath(), "status", "false");
			String filePath = jcrService.getHome()+page.getPath()+".html";
			File file = new File(filePath);
			if(file.exists()) {
				file.delete();
			}
			String jsonPath = jcrService.getHome()+page.getPath()+".json";
			File jsonFile = new File(jsonPath);
			if(jsonFile.exists()) {
				jsonFile.delete();
			}				
			File folder = new File(jcrService.getHome()+page.getPath());
			if(folder.exists() && folder.isDirectory()) {
				FileUtils.deleteDirectory(folder);
			}
	
	}
	
	private String publishAssets(String path,String content) {
		Map<String,String> urls = new HashMap<String,String>();
		Document doc = Jsoup.parse(content);
	    MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();

		File folder = new File(jcrService.getHome()+path+"/assets");
		String location = folder.getParentFile().getName()+"/assets/";
		String prefix = folder.getParentFile().getName()+"_"; int i = 0;		
		if(!folder.exists()) folder.mkdirs();

		try {
			FileUtils.cleanDirectory(folder);
		} catch (IOException e2) {
			logger.error(e2.getMessage());
		}
		for (Element e:doc.getElementsByTag("img")) {
			String src = e.attr("src");
			if(urls.containsKey(src)) {
				e.attr("src", urls.get(src));
				continue;
			}
			if(src!=null && !"".equals(src) && !src.startsWith("http") && !src.startsWith("resources/") && !src.startsWith("/resources/")) {
				try {
					String filename = toFile(src,prefix+i,folder.getAbsolutePath(),path,allTypes);
					i++;
					e.attr("src", location+filename);
					e.removeAttr("height");
					e.removeAttr("width");					
					urls.put(src, location+filename);				    			
				} catch (MalformedURLException e2) {
					logger.error(e2.getMessage());
				} catch (UnsupportedEncodingException e1) {
					logger.error(e1.getMessage());;
				} catch (RepositoryException e1) {
					logger.error(e1.getMessage());
				} catch (FileNotFoundException e1) {
					logger.error(e1.getMessage());
				} catch (IOException e1) {
					logger.error(e1.getMessage());;
				} catch (Exception el) {
					logger.error("img error:"+el.getMessage());;
				}
			}
		}
		for (Element e:doc.getElementsByTag("a")) {
			String href = e.attr("href");
			if(href==null) continue;
			if(urls.containsKey(href)) {
				e.attr("href", urls.get(href));
				continue;
			}
			if(!"".equals(href) && !href.startsWith("#") && !href.startsWith("http") &&  !href.startsWith("/templates/") && !href.startsWith("resources/") && !href.startsWith("/content/") && !href.startsWith("../content/") && !href.startsWith("/resources/")) {
				try {
					String filename = toFile(href,prefix+i,folder.getAbsolutePath(),path,allTypes);
					i++;
					e.attr("href", location+filename);
					urls.put(href, location+filename);
				} catch (MalformedURLException e2) {
					logger.error("href :"+href+" error:"+e2.getMessage());
				} catch (UnsupportedEncodingException e1) {
					logger.error("href :"+href+" error:"+e1.getMessage());;
				} catch (RepositoryException e1) {
					logger.error("href :"+href+" error:"+e1.getMessage());
				} catch (FileNotFoundException e1) {
					logger.error("href :"+href+" error:"+e1.getMessage());
				} catch (IOException e1) {
					logger.error("href :"+href+" error:"+e1.getMessage());;
				} catch (Exception el) {
					logger.error("href :"+href+" error:"+el.getMessage());;
				}
			}
		}

		for (Element e:doc.getElementsByTag("video")) {
			String href = e.attr("poster");
			if(href!=null && urls.containsKey(href)) {
				e.attr("poster", urls.get(href));
				continue;
			}
			if(href!=null && !"".equals(href) && href!=null &&  !href.startsWith("#") && !"".equals(href) &&  !href.startsWith("http") && !href.startsWith("resources/") && !href.startsWith("/resources/")) {
				try {
					String filename = toFile(href,prefix+i,folder.getAbsolutePath(),path,allTypes);
					logger.info(filename);
					i++;
					e.attr("poster", location+filename);
					urls.put(href, location+filename);
				} catch (MalformedURLException e2) {
					logger.error(e2.getMessage());
				} catch (UnsupportedEncodingException e1) {
					logger.error(e1.getMessage());;
				} catch (RepositoryException e1) {
					logger.error(e1.getMessage());
				} catch (FileNotFoundException e1) {
					logger.error(e1.getMessage());
				} catch (IOException e1) {
					logger.error(e1.getMessage());;
				} catch (Exception el) {
					logger.error("video error:"+el.getMessage());;
				}
			}
		}
		
		for (Element e:doc.getElementsByTag("source")) {
			String href = e.attr("src");
			if(href==null) continue;
			if(urls.containsKey(href)) {
				e.attr("src", urls.get(href));
				continue;
			}
			if(href!=null && !"".equals(href) &&  !href.startsWith("#")  && !href.startsWith("http") && !href.startsWith("resources/") && !href.startsWith("/resources/")) {
				try {
					String filename = toFile(href,prefix+i,folder.getAbsolutePath(),path,allTypes);
					i++;
					e.attr("src", location+filename);
					urls.put(href, location+filename);
				} catch (MalformedURLException e2) {
					logger.error(e2.getMessage());
				} catch (UnsupportedEncodingException e1) {
					logger.error(e1.getMessage());;
				} catch (RepositoryException e1) {
					logger.error(e1.getMessage());
				} catch (FileNotFoundException e1) {
					logger.error(e1.getMessage());
				} catch (IOException e1) {
					logger.error(e1.getMessage());;
				} catch (Exception el) {
					logger.error("source error:"+el.getMessage());;
				}
			}
		}	
		for (Element e:doc.getElementsByTag("video")) {
			String href = e.attr("poster");
			if(href==null) continue;
			if(urls.containsKey(href)) {
				e.attr("poster", urls.get(href));
				continue;
			}
			if(href!=null && !"".equals(href) &&  !href.startsWith("#")  && !href.startsWith("http") && !href.startsWith("resources/") && !href.startsWith("/resources/")) {
				try {
					String filename = toFile(href,prefix+i,folder.getAbsolutePath(),path,allTypes);
					i++;
					e.attr("poster", location+filename);
					urls.put(href, location+filename);
				} catch (MalformedURLException e2) {
					logger.error(e2.getMessage());
				} catch (UnsupportedEncodingException e1) {
					logger.error(e1.getMessage());;
				} catch (RepositoryException e1) {
					logger.error(e1.getMessage());
				} catch (FileNotFoundException e1) {
					logger.error(e1.getMessage());
				} catch (IOException e1) {
					logger.error(e1.getMessage());;
				} catch (Exception el) {
					logger.error("poster error:"+el.getMessage());;
				}
			}
		}			
		//update description
		String description = jcrService.getProperty(path, "description");
		if(doc.getElementsByClass("pagetag") !=null && (description == null || "".equals(description))) {
			
			description = doc.getElementsByClass("pagetag").html();
			if(description != null) {
				try {
					jcrService.updatePropertyByPath(path, "description", description);
				} catch (RepositoryException e1) {
					logger.error(e1.getMessage());
				}
			}else {
				logger.debug("description is not found by class 'pagetag'");
			}
		}
		//limit size max 1200

		ImageUtil.limit(folder.getAbsolutePath(), "jpg", 1200);

		
		return doc.body().html();
	}
	
	private String toFile(String link,String filename,String assetFolder, String path,MimeTypes allTypes) throws RepositoryException, IOException {
			URL url = new URL("http://localhost/"+link);
		    Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		    String query = url.getQuery();
		    
		    String[] pairs = query.split("&");
		    
		    for (String pair : pairs) {
		        int idx = pair.indexOf("=");
		        query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		    }
		    String uid = query_pairs.get("uid");
		    String spath = query_pairs.get("path");
		    String w = query_pairs.get("w");
		    String p = query_pairs.get("p");
			Asset asset = null;
			if(uid!=null)
				asset = jcrService.getAssetById(uid);
			else if(spath !=null) {
				asset = (Asset)jcrService.getObject(spath);
			}
			if(asset==null) {
				logger.error("Asset is null:"+spath+",uid="+uid);
				return "error";
			}
		    String ext =asset.getExt();		
		    if(ext==null) {
				try {
					MimeType mimeType = allTypes.forName(asset.getContentType());
				    ext = mimeType.getExtension(); 
				} catch (MimeTypeException e1) {
					logger.error(e1.getMessage());
				}				    	
		    }
			ext =asset.getPath().substring(asset.getPath().lastIndexOf("."));
			String filePath = jcrService.getDevice()+asset.getPath();
			if(link.startsWith("doc2jpg")) {
				filePath = jcrService.getDevice()+asset.getPath()+"/x400"+".jpg";
				ext=".jpg";
			}else if(link.startsWith("pdf2jpg")) {					
				filePath = jcrService.getDevice()+asset.getPath()+"/x400"+".jpg";
				ext=".jpg";
			}else if(link.startsWith("video2jpg")) {
				filePath = jcrService.getDevice()+asset.getPath()+"/x400"+".jpg";
				ext=".jpg";
			}else if(link.startsWith("video.mp4")) {
				if(new File(jcrService.getDevice()+asset.getPath()+"/origin"+ext+".mp4").exists()) {
					filePath = jcrService.getDevice()+asset.getPath()+"/origin"+ext+".mp4";
				} else 
					filePath = jcrService.getDevice()+asset.getPath();
					ext=".mp4";
			}else if(link.startsWith("doc2pdf")) {
				filePath = jcrService.getDevice()+asset.getPath()+"/origin.pdf";
				ext=".pdf";
			}else if(link.startsWith("pdf2img")) {
				filePath = jcrService.getDevice()+asset.getPath()+"/origin-"+p+".jpg";
				ext=".pdf";
				File file = new File(filePath);
				if(!file.exists()) {
					String pdfPath = jcrService.getDevice()+asset.getPath()+"/origin.pdf";
					int exit = ImageUtil.pdf2jpg(pdfPath,Integer.parseInt(p),"1600x1600", filePath);

				}			
			}		    
		    
			FileOutputStream output = new FileOutputStream(assetFolder+"/"+filename+ext);
			if(w != null) {
				File icon = new File(jcrService.getDevice()+asset.getPath()+"/x"+w+"00.jpg");
				if(icon.exists()) {
					FileInputStream in = new FileInputStream(icon);
					IOUtils.copy(in, output);	
					in.close();
				}else {

					File file =new File(jcrService.getFile(asset.getPath()),"/origin"+ext);
					FileInputStream in = new FileInputStream(file);
					IOUtils.copy(in, output);	
					in.close();	

				}

			}else if(asset.getDevice()!=null) {
				File file = null;
	
				file = new File(filePath);
				if(file.isDirectory()) file = new File(file,"origin"+ext);
				if(file.exists()) {
					logger.debug(file.getAbsolutePath()+" to "+assetFolder+"\\"+filename+ext);
					FileInputStream in = new FileInputStream(file);
					IOUtils.copy(in, output);
					in.close();
				}
			}else  if(jcrService.nodeExsits(path+"/original")) {
				jcrService.readAsset(path+"/original", output);
			}

			output.close();
	
			return filename+ext;
	}
	
	private Device getDevice() {
			String deviceRoot = "/system/devices";
	   		String deviceQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+deviceRoot+"]) and s.ocm_classname='com.filemark.jcr.model.Device' order by s.order";
			WebPage<Object> devices = jcrService.queryObject(deviceQuery, 20, 0);
			for(Object d:devices.getItems()) {
				Device dv = (Device)d;
				File f = new File(dv.getLocation());
				float usable = f.getUsableSpace();
				if(usable/f.getTotalSpace() > 0.1) {
					dv.setStatus("enabled");
					try {
						jcrService.addOrUpdate(dv);
					} catch (RepositoryException e) {
						logger.error(e.getMessage());
					}
					return dv;
				}else {
					continue;
				}
			}
			if(jcrService.getDevice()!=null && !"".equals(jcrService.getDevice())) {
				Device device = new Device(jcrService.getDevice());
				if(!jcrService.nodeExsits("/system/devices")) {
					try {
						jcrService.addNodes("/system/devices", "nt:unstructured", getUsername());
					} catch (RepositoryException e) {
						logger.error(e.getMessage());
					}
				}
				device.setPath("/system/devices/default");
				device.setTitle("default");
				try {
					File dir = new File(device.getLocation());
					if(!dir.exists())
						dir.mkdirs();
					device.setStatus("enabled");
					jcrService.addOrUpdate(device);
					
					return device;
				} catch (RepositoryException e) {
					logger.error("device:"+e.getMessage());
				}				


			}
		return new Device();
	}

	private String getDateTime() {
		Date now = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyMMddHHmmss");
		return sf.format(now);
		
		
	}
	  
}
