package com.filemark.jcr.controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.filemark.sso.JwtUtil;

import javax.imageio.ImageIO;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jfree.util.Log;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.filemark.jcr.model.Asset;
import com.filemark.jcr.model.Chat;
import com.filemark.jcr.model.News;
import com.filemark.jcr.model.Page;
import com.filemark.jcr.model.User;
import com.filemark.utils.ImageUtil;
import com.filemark.utils.WebPage;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;



@Controller
public class ContentController extends BaseController {


	private static final Logger logger = LoggerFactory.getLogger(ContentController.class);

    @ExceptionHandler(Exception.class)
    public ModelAndView  handleException(Exception ex,HttpServletRequest request) {
	  String path = request.getRequestURI();
	  String paths[] = path.split("\\.");
      ModelAndView modelAndView = new ModelAndView("error404");
      String message = request.getRequestURI() +":"+ ex.toString();
      modelAndView.addObject("error",message);
      try {
  	    String simpleName = ex.getCause().getClass().getSimpleName();
  	    logger.info(simpleName);
  	    if (simpleName.equals("ClientAbortException") || simpleName.equals("SocketException")) {
			ImageUtil.gpio("write","18","0");
  	    	return null;
  	    }
		  modelAndView.addObject("navigation", getNavigation());
    	  //modelAndView.addObject("navigation",getNavigator(apps,contentPath));
    	  modelAndView.addObject("breadcrumb", jcrService.getBreadcrumb(paths[0]) );
      } catch(Exception e ) {
		ImageUtil.gpio("write","18","0");
    	  logger.error(e.getMessage()); 
      }
      invalidCache();
      logger.error(ex.toString());
	  ImageUtil.gpio("write","18","0");
      return modelAndView;
    }
    @RequestMapping(value = {"/*","/mysite"}, method = RequestMethod.GET)
   	public String homesite(Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String host="";
    	if(request.getRemoteHost().endsWith(jcrService.getDomain())) host = request.getRemoteHost();
		String assetsQuery = "select s.* from [nt:base] AS s WHERE ISCHILDNODE([/templates/assets/splash]) and s.[delete] not like 'true' and s.ocm_classname='com.filemark.jcr.model.Asset' order by s.[lastModified] desc";
		//logger.info(isIntranet+",ip="+getClientIpAddress(request)+",q="+assetsQuery);;
		WebPage<Asset> assets = jcrService.searchAssets(assetsQuery, 12, 0);
		String splashImagePaths = "";
		int i = 0;
		String contextPath = request.getContextPath();
		if(contextPath.equals("/")) contextPath = "";
		for(Asset asset:assets.getItems()) {
			splashImagePaths += contextPath+asset.getPath()+",";
		}
    	model.addAttribute("splashImagePaths", splashImagePaths);
    	model.addAttribute("numImages", new Integer(assets.getItems().size()));
    	model.addAttribute("host", host);
    	
		return "content/mysite";

    }    

  
    @RequestMapping(value = {"/myip/{uid}"}, method = RequestMethod.GET)
   	public @ResponseBody String myip(@PathVariable String uid,Model model,HttpServletRequest request, HttpServletResponse response) {
    	String userPath="/system/users/"+uid,myip="";
    	try {
			InetAddress ipAddr = InetAddress.getLocalHost();
	    	myip = getClientIpAddress(request);/*ipAddr.getHostAddress()+"="+request.getRemoteAddr()+"="+getPublicIpAddress()+"ip"+*/
	    	User user = (User)jcrService.getObject("/system/users/"+uid);
	    	if(user !=null && !myip.equals(user.getHostIp())) {
	    		jcrService.updatePropertyByPath(userPath, "hostIp", myip);
	    	}
    	} catch (UnknownHostException e) {
    		myip ="error:UnknownHostException";
			logger.error(e.getMessage());
		} catch (RepositoryException e) {
			myip ="error:user not found";
			logger.error(e.getMessage());
		}


    	
		return myip;

    } 

    @RequestMapping(value = {"/yhyun"}, method = RequestMethod.GET)
   	public String yhyun(HttpServletRequest request, HttpServletResponse response) {
    	String myip = "dajana.cn";
    	//try {
			//InetAddress ipAddr = InetAddress.getLocalHost();
	    myip = getClientIpAddress(request);/*ipAddr.getHostAddress()+"="+request.getRemoteAddr()+"="+getPublicIpAddress()+"ip"+*/

        try {
            Response res = Jsoup.connect("http://"+myip+":8888/signin")
               .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
               .timeout(5000)
               .execute();
            if(res.statusCode()==200) {
            	return "redirect:http://"+myip+":8888/signin";
            }else {
            	return "redirect:/?ip="+myip+"&error="+res.statusCode();            	
            }
         } catch (IOException e) {
            return "redirect:/?ip="+myip+"&error="+e.getMessage();
         }
    } 
    @RequestMapping(value = {"/dydns"}, method = RequestMethod.POST)
   	public String dydns(String content,HttpServletRequest request, HttpServletResponse response) {
    	String myip = "dajana.cn";
	    myip = getClientIpAddress(request);/*ipAddr.getHostAddress()+"="+request.getRemoteAddr()+"="+getPublicIpAddress()+"ip"+*/
		String json_user = JwtUtil.decode(content);
        try {
			Gson gson = new Gson();
			User user = gson.fromJson(json_user, User.class);
			user.setHostIp(myip);
			user.setLastModified(new Date());
			if(jcrService.nodeExsits(user.getPath())) {
				String hostIp = jcrService.getProperty(user.getPath(), "hostIp");
				if(!myip.equals(hostIp)) {
					jcrService.setProperty(user.getPath(), "hostIp", myip);
					jcrService.updateCalendar(user.getPath(), "lastModified");
				}
				
			}else {
				jcrService.addOrUpdate(user);
			}

            response.setStatus(HttpStatus.SC_OK);			
         } catch (Exception e) {
        	logger.error("dydns:"+e.getMessage());
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
         }
        
        return null;
    } 
    
    @RequestMapping(value = {"/uinfo/{uid}"}, method = RequestMethod.GET)
   	public @ResponseBody String uinfo(@PathVariable String uid,Model model,HttpServletRequest request, HttpServletResponse response) {
    	String userPath="/system/users/"+uid,uinfo="";
    	try {

	    	InetAddress ipAddr = InetAddress.getLocalHost();
	    	User user = (User)jcrService.getObject("/system/users/"+uid);
	    	if(user.getHost() == null || "".equals(user.getHost())) {
	    	   user.setHost(request.getServerName());	
	    	   
	    	}
	    	if(user.getPort() == null || "".equals(user.getPort())) {
	    		user.setPort(""+request.getServerPort());
	    	}
	    	uinfo = user.getName()+"/"+user.getHost()+"/"+user.getPort()+"/"+user.getHostIp()+"/"+ipAddr.getHostAddress();
    	} catch (UnknownHostException e) {
    		uinfo ="error:UnknownHostException";
			logger.error(e.getMessage());
		} catch (RepositoryException e) {
			uinfo ="error:user not found";
			logger.error(e.getMessage());
		}    	
		return uinfo;

    } 
    @RequestMapping(value = {"/myhost/{uid}"}, method = RequestMethod.GET)
   	public @ResponseBody String myhost(@PathVariable String uid,Model model,HttpServletRequest request, HttpServletResponse response) {
    	String myip="";
    	try {
    		InetAddress ipAddr = InetAddress.getLocalHost();
    		myip = ipAddr.getHostAddress();//+"="+request.getRemoteAddr()+"="+getPublicIpAddress()+"ip"+*/getClientIpAddress(request);
		} catch (UnknownHostException e) {
			myip ="error:UnknownHostException";
			logger.error(e.getMessage());
		}   	
		return myip;

    }

    @RequestMapping(value = {"/mydomain/{uid}"}, method = RequestMethod.GET)
   	public @ResponseBody String mydomain(@PathVariable String uid,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
 
		return request.getServerName();

    }
	@RequestMapping(value = {"/*/resources/images/*.*","/*/resources/**/*.*","/content/resources/dist/img/*.*","/content/**/resources/dist/img/*.*","/content/**/resources/images/*.*"}, method = RequestMethod.GET)
   	public String resources(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String paths[] = request.getRequestURL().toString().split("/resources");;
		
		return "redirect:/resources"+paths[1];

    }
	
	

   	@RequestMapping(value = {"/content/sitemap.html","/content/sitemap"}, method = RequestMethod.GET)
   	public String sitemap(Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String pagesQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE([/content])" +" and s.status like 'true' and lastPublished is not null and s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
		WebPage<Page> pages = jcrService.queryPages(pagesQuery, 100, 0);
		String site="/content/sitemap";
		
		return "redirect:"+site;

   	}
   	
   	@RequestMapping(value = {"/content","/content.html"}, method = RequestMethod.GET)
   	public String home(Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
   		String content = "/content";
   		String username = getUsername();
   		if(username!=null ) content +="/"+username;
   		else content +="/home";
		Page home = new Page();
		if(!jcrService.nodeExsits(content)) {//create root
			if(!jcrService.nodeExsits("/content")) {
				Page djn = new Page();
				djn.setPath("/content");
				djn.setParent("/");
				djn.setTitle("\u5927\u5BB6\u62FF");
				djn.setRedirectTo("http://www.dajana.ca");
				djn.setCreatedBy(getUsername());
				djn.setLastModified(Calendar.getInstance().getTime());
				jcrService.addOrUpdate(djn);				
			}

			home.setPath(content);
			home.setParent("/content");
			home.setTitle("\u9996\u9875");
			home.setCreatedBy(getUsername());
			home.setLastModified(Calendar.getInstance().getTime());
			jcrService.addOrUpdate(home);			

		}else 
			home = jcrService.getPage(content);

   		return page("home",home.getUid(),0,model,request,response);
   	}
   	
	@RequestMapping(value = {"/content/{site}.passcode","/content/{site}.passcode","/content/{site}/**/*.passcode","/content/{site}/*.passcode"}, method = RequestMethod.POST)
	public String passcode(@PathVariable String site, String passcode, Model model,HttpServletRequest request, HttpServletResponse response) throws Exception  {
		try {
			String paths = URLDecoder.decode(request.getRequestURI(),"UTF-8");
			if(!request.getContextPath().equals("/"))
				paths = paths.replaceFirst(request.getContextPath(), "");
			String path = paths.split(".passcode")[0];
			HttpSession session = request.getSession();
			Page currentpage = new Page();
			Gson gson = new Gson();
			File json = new File(jcrService.getHome()+path+".json");
			if(json.exists()) {
				InputStream is = new FileInputStream(json);
				currentpage = gson.fromJson(new InputStreamReader(is,"UTF-8"), Page.class);
				is.close();
			}
			model.addAttribute("username", session.getAttribute("username"));
			model.addAttribute("site", site);
			model.addAttribute("path", path);
			model.addAttribute("title", currentpage.getTitle());
			if(currentpage.getPasscode() !=null && !"".equals(currentpage.getPasscode())) {
				String pagePasscode = currentpage.getPasscode();
				if("true".equals(currentpage.getSecured())) {
					//logger.debug("passcode before="+pagePasscode);
					pagePasscode = JwtUtil.decode(pagePasscode);
					//logger.debug("passcode after="+pagePasscode);
				}
				if(passcode==null || !passcode.equals(pagePasscode)) {
					model.addAttribute("message", "error");
					return "content/passcode";
				}else {
					session.setAttribute(path, pagePasscode);
					session.setAttribute(pagePasscode, path);
					return "redirect:"+path+".html";
				}
			}else {
				return "redirect:"+path+".html";
			}
	
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			throw new Exception("\u8DEF\u5F84\u51FA\u9519!");
		} 

	}
   	
	@RequestMapping(value = {"/content/{site}","/content/{site}.html","/content/{site}/**/*","/content/{site}/**/*.html","/content/{site}/*.html"}, method = RequestMethod.GET)
	public String page(@PathVariable String site, String uid,Integer p, Model model,HttpServletRequest request, HttpServletResponse response) throws Exception  {
		try {
			ImageUtil.HDDOn();
			String paths = URLDecoder.decode(request.getRequestURI(),"UTF-8");
			if(!request.getContextPath().equals("/"))
				paths = paths.replaceFirst(request.getContextPath(), "");
			String path = paths.split(".html")[0];
			Page currentpage = new Page();
			Gson gson = new Gson();
			File json = new File(jcrService.getHome()+path+".json");
			//logger.info(paths);
			if(uid==null) {
				//if(jcrService.nodeExsits(path))
				if(json.exists()) {
					//FileReader reader = new FileReader(json);
					InputStream is = new FileInputStream(json);
					currentpage = gson.fromJson(new InputStreamReader(is,"UTF-8"), Page.class);
					is.close();
					//currentpage = jcrService.getPage(path);
				} else {
					File file =new File(jcrService.getHome()+paths);
					if(file.exists()) {
/*						FileInputStream in = new FileInputStream(file);
						response.setContentLength((int)file.length());
						IOUtils.copy(in, response.getOutputStream());
						in.close();*/
						file.setReadOnly();
						super.serveResource(request, response, file, null);

						return null;
					}else {
						logger.error("Error:"+HttpServletResponse.SC_NOT_FOUND);
						response.sendError(HttpServletResponse.SC_NOT_FOUND);
						ImageUtil.HDDOff();
						return null;
					}
				}
			}else if(uid!=null) {
				currentpage = jcrService.getPageByUid(uid);
				path = currentpage.getPath();
			}
			String navigation = currentpage.getNavigation(),breadcrumb=currentpage.getBreadcrumb(),content=currentpage.getContent(),menuPath=currentpage.getMenuPath();
			String page_passcode = currentpage.getPasscode();
			if("true".equals(currentpage.getSecured())) {
				page_passcode = JwtUtil.decode(page_passcode);
				content = JwtUtil.decode(content);
			}
			if(page_passcode !=null && !"".equals(page_passcode)) {
				HttpSession session = request.getSession();
				String parent_path = (String)session.getAttribute(page_passcode);
				String passcode = (String)session.getAttribute(parent_path);
				if(passcode==null || parent_path==null || !passcode.equals(page_passcode)) {
			    	String host="";
			    	if(request.getRemoteHost().endsWith(jcrService.getDomain())) host = request.getRemoteHost();
					String assetsQuery = "select s.* from [nt:base] AS s WHERE ISCHILDNODE([/templates/assets/splash]) and s.[delete] not like 'true' and s.ocm_classname='com.filemark.jcr.model.Asset' order by s.[lastModified] desc";
					WebPage<Asset> assets = jcrService.searchAssets(assetsQuery, 12, 0);
					String splashImagePaths = "";
					int i = 0;
					String contextPath = request.getContextPath();
					if(contextPath.equals("/")) contextPath = "";
					for(Asset asset:assets.getItems()) {
						splashImagePaths += contextPath+asset.getPath()+",";
					}
			    	model.addAttribute("splashImagePaths", splashImagePaths);
			    	model.addAttribute("numImages", new Integer(assets.getItems().size()));
			    	model.addAttribute("host", host);					
					model.addAttribute("path", path);
					model.addAttribute("title", currentpage.getTitle());
					model.addAttribute("passcode", currentpage.getPasscode());
					ImageUtil.HDDOff();
					return "content/passcode";
				}
			}

			if(currentpage.getRedirectTo() != null && !"".equals(currentpage.getRedirectTo())) {
				ImageUtil.HDDOff();
				return "redirect:"+currentpage.getRedirectTo();
			}
			navigation = FileUtils.readFileToString(new File(jcrService.getHome()+menuPath+"/navimenu.html"),"UTF-8");

			model.addAttribute("breadcrumb",breadcrumb);
			model.addAttribute("site", site);
			model.addAttribute("username", getUsername());
			model.addAttribute("navigation",navigation);
			model.addAttribute("page", currentpage); 
			model.addAttribute("content", content); 
			model.addAttribute("p", p); 			
			model.addAttribute("origin", request.getRequestURL()+"?"+request.getQueryString());	
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			ImageUtil.HDDOff();
			throw new Exception("\u8DEF\u5F84\u51FA\u9519!");
		} catch (RepositoryException e) {
			logger.error(e.getMessage());
			ImageUtil.HDDOff();
			throw new Exception("\u9875\u9762\u6CA1\u627E\u5230!");
		} catch (IOException e) {
			logger.error(e.getMessage());
			ImageUtil.HDDOff();
			throw new Exception(e.getMessage());			
		}
		ImageUtil.HDDOff();
		return "content/page";
	}
	@RequestMapping(value = {"/content/{site}.menu","/content/{site}/**/*.menu","/content/{site}/*.menu"}, method = RequestMethod.GET)
	public String menu(@PathVariable String site,String path, String uid,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String menu="";

		if(path==null) {
			String paths = URLDecoder.decode(request.getRequestURI(),"UTF-8");
			if(!request.getContextPath().equals("/"))
				paths = paths.replaceFirst(request.getContextPath(), "");
			path = paths.replace(".menu", "");
		}
		Page currentpage = new Page();
		if(uid==null) {
			currentpage = jcrService.getPage(path);
		}else if(uid!=null) {
			currentpage = jcrService.getPageByUid(uid);
			path = currentpage.getPath();
		}
		if(!"true".equals(currentpage.getStatus())) {
			response.sendError(404);
			//throw new Exception("此页已下线");
			return null;
		}
		int level = 2;
		if(currentpage.getShowThememenu()!=null && "true".equals(currentpage.getShowThememenu()))
			level++;
		String navigation = "";
		if(level == 3) {
			String menuPath=jcrService.getAncestorPath(currentpage.getPath(), level);
			navigation = FileUtils.readFileToString(new File(jcrService.getHome()+menuPath+"/menu.html"),"UTF-8");
		}else {
			File menuFile = new File(jcrService.getHome()+"/content/"+site+"/menu.html");
			if(menuFile.exists())	
				navigation = FileUtils.readFileToString(menuFile,"UTF-8");
		}
		model.addAttribute("navigation", navigation);
		return "content/navmenu";
	}
	
	@RequestMapping(value = {"/content/{site}.cnt","/content/{site}/**/*.cnt","/content/{site}/*.cnt"}, method = RequestMethod.GET)
	public @ResponseBody String content(@PathVariable String site,String path, String uid,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String menu="";

		if(path==null) {
			String paths = URLDecoder.decode(request.getRequestURI(),"UTF-8");
			if(!request.getContextPath().equals("/"))
				paths = paths.replaceFirst(request.getContextPath(), "");
			path = paths.replace(".cnt", "");
		}
		Page currentpage = new Page();
		if(uid==null) {
			currentpage = jcrService.getPage(path);
		}else if(uid!=null) {
			currentpage = jcrService.getPageByUid(uid);
			path = currentpage.getPath();
		}
		if(!"true".equals(currentpage.getStatus())) {
			response.sendError(404);
			return null;
			//return "此页已下线";
		}
		File fileContent = new File(jcrService.getHome()+currentpage.getPath()+".html");
		if(fileContent.exists())
			FileUtils.copyFile(fileContent, response.getOutputStream());

		return null;
	}
	
	@RequestMapping(value = {"/content/{site}.comment","/content/{site}/**/*.comment","/content/{site}/**/*.comment","/content/{site}/*.comment"}, method = RequestMethod.GET)
	public String comment(@PathVariable String site, String uid,Integer p, Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			String paths = URLDecoder.decode(request.getRequestURI(),"UTF-8");
			if(!request.getContextPath().equals("/"))
				paths = paths.replaceFirst(request.getContextPath(), "");
			String path = paths.split(".comment")[0];
			Page currentpage = new Page();
			if(uid==null) {
				currentpage = jcrService.getPage(path);
			}else if(uid!=null) {
				currentpage = jcrService.getPageByUid(uid);
				path = currentpage.getPath();
			}

			if("true".equals(currentpage.getShowComment())) {
				if(p==null) p=0;
				String chatQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+currentpage.getPath()+"/comments"+"]) and s.ocm_classname='com.filemark.jcr.model.Chat' order by s.[jcr:lastModified] DESC";
				WebPage<Chat> chats = jcrService.queryChats(chatQuery, 20, p);
				model.addAttribute("chats",chats);
			}
		} catch (UnsupportedEncodingException e) {
			throw new Exception("\u8DEF\u5F84\u51FA\u9519!");
		} catch (RepositoryException e) {
			throw new Exception("\u9875\u9762\u6CA1\u627E\u5230!");
		}

		return "content/comment";
	}
	
	@RequestMapping(value = {"/publish/*.html","/publish/**/*.html"}, method = RequestMethod.GET)
	public @ResponseBody String published(Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try {
			String path[] = URLDecoder.decode(request.getRequestURI(),"UTF-8").split("published");
			
	        String filename = jcrService.getHome()+path[1];
			File file = new File(filename);
	        response.setHeader("Content-Type", "text/html");
	        response.setHeader("Content-Length", String.valueOf(file.length()));
	        response.setHeader("Content-Disposition", "inline; filename=\"" + URLEncoder.encode(filename, "UTF-8") + "\"");
	        IOUtils.copy(new FileInputStream(file), response.getOutputStream());			
		}catch(Exception e) {
			logger.error(e.getMessage());
			return e.getMessage();
		}

		
		return null;
	}
	
	//@RequestMapping(value = {"/content/{site}.pdf","/content/{site}.pdf","/content/{site}/**/*.pdf","/content/{site}/**/*.pdf","/content/{site}/*.pdf"}, method = RequestMethod.GET)
	public @ResponseBody String page2pdf(@PathVariable String site, String uid,Integer p, Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			String paths = URLDecoder.decode(request.getRequestURI(),"UTF-8");
			if(!request.getContextPath().equals("/"))
				paths = paths.replaceFirst(request.getContextPath(), "");
			String path = paths.split(".pdf")[0];
			Page currentpage = new Page();
			if(uid==null) {
				currentpage = jcrService.getPage(path);
			}else if(uid!=null) {
				currentpage = jcrService.getPageByUid(uid);
				path = currentpage.getPath();
			}
			if(currentpage.getRedirectTo() != null && !"".equals(currentpage.getRedirectTo())) {
				return "redirect:"+currentpage.getRedirectTo();
			}
			response.setContentType("application/pdf");
			String fontPath = "resources/fonts/NotoSansCJKsc-Regular.otf";
			String CSS = "resources/css/wet-boew.css";
			Resource resource = new ClassPathResource(fontPath);
			if(!resource.getFile().exists()) {
				Log.error("resource not exists:"+resource.getFilename());
			}
			String content = currentpage.getContent();
			if(content==null) {
				return "No content";
			}
	        // CSS
	        CSSResolver cssResolver = new StyleAttrCSSResolver();
	        CssFile cssFile = XMLWorkerHelper.getCSS(new FileInputStream(CSS));
	        cssResolver.addCss(cssFile);
	        
			Document doc = new Document();
			PdfWriter writer = PdfWriter.getInstance(doc,response.getOutputStream());
			doc.open();
/*			XMLWorkerFontProvider fontProvider  = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
			fontProvider.register(fontPath);
			fontProvider.register("resources/fonts/Cardo-Regular.ttf");
	        fontProvider.addFontSubstitute("lowagie", "cardo");
	        CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);
	        HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
	        htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());	
	        
			InputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
	        // Pipelines
	        PdfWriterPipeline pdf = new PdfWriterPipeline(doc, writer);
	        HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
	        CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);
	 
	        // XML Worker
	        XMLWorker worker = new XMLWorker(css, true);
	        XMLParser parser = new XMLParser(worker);
	        parser.parse(in);		*/
			InputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
			XMLWorkerHelper.getInstance().parseXHtml(writer, doc, in, Charset.forName("UTF-8"));
			doc.close();
		} catch (UnsupportedEncodingException e) {
			return "\u8DEF\u5F84\u51FA\u9519!";
		} catch (RepositoryException e) {
			return "\u9875\u9762\u6CA1\u627E\u5230!";
		}

		return null;
	}

	
	@RequestMapping(value = {"/content/leftmenu.html"}, method = RequestMethod.GET)
	public String leftmenu(String uid,String path,Model model,HttpServletRequest request, HttpServletResponse response) throws IOException {

		try {
			Page currentpage = new Page();
			if(uid==null && path!=null) {
				currentpage = jcrService.getPage(path.split(".html")[0]);
			}else if(uid!=null) {
				currentpage = jcrService.getPageByUid(uid); 
			}else {
				throw new Exception("Path is missing");
			}
			String site = currentpage.getPath().split("/")[2];
			String content = "/content/"+site;
			String pagesQuery = "select * from [nt:base] AS s WHERE ISDESCENDANTNODE(["+content+"]) and ISCHILDNODE(["+currentpage.getParent()+"]) and s.status like 'true' and s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
			if(currentpage.getPath().equals(content) || currentpage.getPath().equals("/content")) {
				pagesQuery = "select * from [nt:base] AS s WHERE ISSAMENODE([/content/"+site+"]) and s.status like 'true' and  s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
			}
			WebPage<Page> pages = jcrService.queryPages(pagesQuery, 100, 0);
			String childPagesQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+currentpage.getPath()+"]) and s.status like 'true' and s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
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
		}catch (Exception e) {
			logger.error("Error:"+HttpServletResponse.SC_NOT_FOUND);
			
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;			
		}

		return "content/leftmenu";
	}	
	@RequestMapping(value = {"/content/*.json"}, method = RequestMethod.GET)
	public @ResponseBody List<Page> getTemplates(String path,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String paths = URLDecoder.decode(request.getRequestURI(),"UTF-8");
		if(!request.getContextPath().equals("/"))
			paths = paths.replaceFirst(request.getContextPath(), "");
		if(path==null)
			path = paths.replace(".json", "");

		String pagesQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+path+"])" +" and s.status like 'true' and s.ocm_classname='com.filemark.jcr.model.Page' order by s.order";
		WebPage<Page> pages = jcrService.queryPageContent(pagesQuery, 100, 0);

		
		return pages.getItems();
	}

	@RequestMapping(value = {"/content/**/*.json"}, method = RequestMethod.GET)
	public @ResponseBody String getJsons(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String paths = URLDecoder.decode(request.getRequestURI(),"UTF-8");
		if(!request.getContextPath().equals("/"))
			paths = paths.replaceFirst(request.getContextPath(), "");
		File file =new File(jcrService.getHome()+paths);
		if(file.exists()) {
			FileInputStream in = new FileInputStream(file);
			response.setContentLength((int)file.length());
			IOUtils.copy(in, response.getOutputStream());
			in.close();
			return null;
		}else {
			logger.error("Error:"+HttpServletResponse.SC_NOT_FOUND);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}		
		
	}
	
	@RequestMapping(value = {"/content/*.news.json","/content/**/*.news.json","/content/**/*.html.news.json"}, method = RequestMethod.GET)
	public @ResponseBody Map<String, News[]> getNews(String path,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String paths = URLDecoder.decode(request.getRequestURI(),"UTF-8");
		if(!request.getContextPath().equals("/"))
			paths = paths.replaceFirst(request.getContextPath(), "");
		path = paths.replace(".html.news.json", "").replace(".news.json", "");

		String pagesQuery = "select * from [nt:base] AS s WHERE ISDESCENDANTNODE(["+path+"])" +" and s.status like 'true' and lastPublished is not null and s.ocm_classname='com.filemark.jcr.model.Page' order by s.lastPublished desc";
		WebPage<Page> pages = jcrService.queryPages(pagesQuery, 100, 0);
		News news[] = new News[pages.getItems().size()];
		int i = 0;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String contextPath = request.getContextPath();
		if("/".equals(contextPath)) contextPath = "";
		for(Page page:pages.getItems()) {
			News p2news = new News();
			p2news.setTitle("<a href=\""+contextPath+page.getPath()+".html\">"+page.getTitle()+"</a>");
			p2news.setDescription(page.getDescription());
			if(page.getLastPublished()!=null)
			p2news.setLastPublished(sf.format(page.getLastPublished()));
			p2news.setSubjects(page.getParent());
			news[i++] = p2news;
		}
		Map<String, News[]> data = new HashMap<String, News[]>();
		data.put("data", news);
		return data;
	}

   	@RequestMapping(value = {"/content/comments.json"}, method = {RequestMethod.GET})
   	public @ResponseBody Map<String , Chat[]>  commentsJson(String path,String status,Integer p,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
   		path += "/cocments";
   		if(p==null)
   			p=0;
   		if( status == null || "0".equals(status)) {
   			status="";
		}else {
			status = " and s.from like '"+status+"'";
		}
		String chatQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+path+"])"+status+" and s.ocm_classname='com.filemark.jcr.model.Chat' order by s.[jcr:lastModified] DESC";
		WebPage<Chat> chats = jcrService.queryChats(chatQuery, 20, p);
		Chat achats[] = new Chat[chats.getItems().size()];
		int i=0;
		Map<String , Chat[]> data = new HashMap<String, Chat[]>(); 

		for(Chat chat:chats.getItems() ) {
			achats[i] = chat;
		}
		data.put("data", achats);
   		return data;
   	}	
	@RequestMapping(value = {"/template.json"}, method = RequestMethod.GET)
	public @ResponseBody Page getTemplate(String path,HttpServletRequest request, HttpServletResponse response) throws Exception {

		Page page = jcrService.getPage(path);
		
		return page;
	}
	
	@RequestMapping(value = {"/content/*.shr","/content/**/*.shr"}, method = RequestMethod.GET)
	public String share(String path,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {

		String url = request.getRequestURL().toString().replace(".shr", ".qrb");;
		if(path==null)
			path = url;
		model.addAttribute("qrpath", path);
		
		return "content/share";
	}
	
	@RequestMapping(value = {"/content/*.qrb","/content/**/*.qrb"}, method = RequestMethod.GET)
	public @ResponseBody String getQRBarcode(String path,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String url = request.getRequestURL().toString().replace(".qrb", ".html");;
		if(path==null)
			path = url;

        BarcodeQRCode barcodeQRCode = new BarcodeQRCode(path, 360, 360, null);
        java.awt.Image awtImage = barcodeQRCode.createAwtImage(Color.BLACK, Color.WHITE);
        BufferedImage bImage= new BufferedImage(360, 360, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bImage.createGraphics();
        g.drawImage(awtImage, 0, 0, null);
        g.dispose();
        response.setContentType("image/jpg");
        ImageIO.write(bImage, "jpg", response.getOutputStream());
		
		return null;
	}
	
}
