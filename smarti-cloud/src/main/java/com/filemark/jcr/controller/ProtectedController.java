package com.filemark.jcr.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.filemark.jcr.model.Chat;
import com.filemark.jcr.model.Folder;
import com.filemark.jcr.model.Page;
import com.filemark.jcr.model.User;
import com.filemark.utils.ImageUtil;
import com.filemark.utils.WebPage;

@Controller
public class ProtectedController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(ProtectedController.class);

    
    @ExceptionHandler(Exception.class)
    public ModelAndView  handleException(Exception ex,HttpServletRequest request) {
	  String path = request.getRequestURI();
	  String paths[] = path.split("\\.");
      ModelAndView modelAndView = new ModelAndView("error404");
      String message = request.getRequestURI() +":"+ ex.toString();
      modelAndView.addObject("error",message);
      try {

		  modelAndView.addObject("navigation", getNavigation());
    	  //modelAndView.addObject("navigation",getNavigator(apps,contentPath));
    	  modelAndView.addObject("breadcrumb", jcrService.getBreadcrumb(paths[0]) );
      } catch(Exception e ) {
    	  logger.error(e.getMessage()); 
      }
      invalidCache();
      logger.error(ex.toString());
      return modelAndView;
    }
    @RequestMapping(value = {"/protected/mycloud"}, method = RequestMethod.GET)
   	public String mycloud(Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String host="";
    	if(request.getRemoteHost().endsWith(jcrService.getDomain())) { 
    		host = request.getRemoteHost();
    		model.addAttribute("host", host);
    	}
    	
		return "redirect:/site/assets.html";

    }         
   	@RequestMapping(value = {"/protected/chat.html"}, method = {RequestMethod.GET})
   	public String mychat(Model model,String path,HttpServletRequest request, HttpServletResponse response) throws Exception {
   		String username = getUsername();
   		User user = (User)jcrService.getObject("/system/users/"+username);
   		String chatRoot = "/chat";
   		if(path==null && user != null && "Owner".equals(user.getRole())) path="/chat";

		String folderQuery = "select s.* from [nt:base] AS s INNER JOIN [nt:base] AS child ON ISCHILDNODE(child,s) WHERE ISDESCENDANTNODE(s,["+chatRoot+"])" +" and child.userName like '%"+username+"' and s.ocm_classname='com.filemark.jcr.model.Folder' and child.ocm_classname ='com.filemark.jcr.model.User' order by s.path";

   		if(user.getRole().equals("Administrator")||user.getRole().equals("Owner"))
   			folderQuery = "select s.* from [nt:base] AS s WHERE ISDESCENDANTNODE(["+chatRoot+"])" +" and s.ocm_classname='com.filemark.jcr.model.Folder' order by s.path";
		logger.debug(folderQuery);
		WebPage<Folder> folders = jcrService.queryFolders(folderQuery, 100, 0);
		if(path !=null) {
			String chatQuery = "select * from [nt:base] AS s WHERE ISDESCENDANTNODE(["+path+"])" +" and s.ocm_classname='com.filemark.jcr.model.Chat' order by s.[jcr:lastModified] DESC";
			WebPage<Chat> chats = jcrService.queryChats(chatQuery, 12, 0);
			model.addAttribute("chats", chats);
			model.addAttribute("folder", jcrService.getFolder(path));

		}

		Page page = new Page();
		page.setTitle("优信");
		page.setPath("/content/"+username);
   		model.addAttribute("folders", folders);
   		model.addAttribute("page", page);
   		model.addAttribute("user", user);
   		model.addAttribute("username", request.getAttribute("username"));
   		model.addAttribute("usertitle", request.getAttribute("usertitle"));   		
		//model.addAttribute("navigation",jcrService.getPageNavigation("/content/"+getUsername(),2));		
   		return "chat/mychat";
   	}
   	@RequestMapping(value = {"/protected/wojia"}, method = {RequestMethod.GET})
   	public String mysite(String path,String lastModified,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
   		String username = getUsername();
   		String content = "/content/"+username;
   		if(!jcrService.nodeExsits(content)) {//create root
			if(!jcrService.nodeExsits("/content")) {
				Page djn = new Page();
	        	String dajana_home = messageSource.getMessage("dajana_home", null,"\u5927\u5BB6\u62FF", localeResolver.resolveLocale(request));
	
				djn.setPath("/content");
				djn.setParent("/");
				djn.setTitle(dajana_home);
				djn.setRedirectTo("http://www.dajana.ca");
				djn.setCreatedBy(username);
				djn.setLastModified(Calendar.getInstance().getTime());
				jcrService.addOrUpdate(djn);				
			}
			Page currentpage = new Page();
			String title = (String)request.getAttribute("usertitle");
			if(title==null) title = username;
			currentpage.setPath(content);
			currentpage.setParent("/content");
			currentpage.setTitle(username);
			currentpage.setCreatedBy(username);
			currentpage.setLastModified(Calendar.getInstance().getTime());
			if(jcrService.nodeExsits("/content/templates")) {
				Page template = jcrService.getPage("/content/templates");
				currentpage.setContent(template.getContent());
				
			}
	
		jcrService.addOrUpdate(currentpage);
	}  
   		String usersite = (String)request.getSession().getAttribute("usersite");
   		if(usersite!=null && !"null".equals(usersite) && !"".equals(usersite)) return "redirect:http://"+usersite+"/content/"+username+".html";
   		if(path!=null && path.startsWith(content)) return "redirect:/site/editor.html?path="+path;
   		return "redirect:/site/editor.html?path=/content/"+username;
   	}

   	@RequestMapping(value = {"/protected/mysite.html","/protected/mysite"}, method = RequestMethod.GET)
   	public String mysite(Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
   		String domain = (String)request.getAttribute("usersite");
   		String port = (String)request.getAttribute("port");
   		String username = (String)request.getAttribute("username");
   		String site = (domain==null||"null".equals(domain)?"/content/"+username+".html":"http://"+domain+port+"/content/"+username+".html");
   		logger.debug("redirect to "+site);
   		return "redirect:"+site;

   	}
	@RequestMapping(value = {"/protected/browse.html","/site/image.html"}, method = {RequestMethod.GET,RequestMethod.POST},produces = "text/plain;charset=UTF-8")
	public String browse(String path,String type, String input,String kw,Integer p,Integer m,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		ImageUtil.HDDOn();
		String assetFolder = "/"+getUsername()+"/assets";
		if(!jcrService.nodeExsits(assetFolder)) {
			jcrService.addNodes(assetFolder, "nt:unstructured",getUsername());		
		}
		int max = 12;
		if(path == null) {
			path=assetFolder;
			max = 12;
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
		return "chat/browse";
	}

	@RequestMapping(value = {"/protected/browsemore.html","/protected/**/browsemore.html"}, method = {RequestMethod.GET,RequestMethod.POST},produces = "text/plain;charset=UTF-8")
	public String browsemore(String path,String type, String input,String kw,Integer p,Integer m,String topage,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		ImageUtil.HDDOn();
		String assetFolder = "/"+getUsername()+"/assets";
		if(!jcrService.nodeExsits(assetFolder)) {
			jcrService.addNodes(assetFolder, "nt:unstructured",getUsername());		
		}
		int max = 12;
		if(path == null) {
			path=assetFolder;
			max = 12;
		}
		if(topage==null) topage="browsemore";
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
		return "chat/"+topage;
	}
	
   	@RequestMapping(value = {"/protected/unreadchat.json"}, method = {RequestMethod.GET})
   	public @ResponseBody WebPage<Folder> unreadChatJson(String path,Model model,HttpServletRequest request, HttpServletResponse response) {
   		String username = getUsername();
		String folderQuery = "select s.* from [nt:base] AS s INNER JOIN [nt:base] AS child ON ISCHILDNODE(child,s) WHERE ISDESCENDANTNODE(s,[/chat])" +" and child.userName like '%"+username+"' and s.ocm_classname='com.filemark.jcr.model.Folder' and child.ocm_classname ='com.filemark.jcr.model.User' order by s.path";

		WebPage<Folder> folders = jcrService.queryFolders(folderQuery, 100, 0);
		//logger.debug("Count="+folders.getPageCount());
		for(Folder folder:folders.getItems()) {
			String userpath = folder.getPath()+"/"+username;
			User user;
			try {
				user = (User)jcrService.getObject(userpath);
			} catch (RepositoryException e) {
				logger.error(e.getMessage());
				continue;
			}
	   		String operator=">";
	   		if(path==null) path="/chat";
	   		SimpleDateFormat sdf;
	   		sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	   		//logger.debug(sdf.format(user.getLastModified()));
	   		String dateRange = user.getLastModified()==null?"":"and s.[jcr:lastModified] "+operator+" CAST('"+sdf.format(user.getLastModified())+"' AS DATE)";
			String chatQuery = "select * from [nt:base] AS s WHERE ISDESCENDANTNODE(["+folder.getPath()+"]) "+dateRange+" and s.ocm_classname='com.filemark.jcr.model.Chat' order by s.[jcr:lastModified] DESC";
			long chatCount = jcrService.getCount(chatQuery);
			folder.setChildCount(chatCount);
			folder.setLastModified(user.getLastModified());

		}
   		return folders;
   	}

   	
   	@RequestMapping(value = {"/protected/chat.json"}, method = {RequestMethod.GET})
   	public @ResponseBody WebPage<Chat> mychatJson(String path,String lastModified,String operator,Model model,HttpServletRequest request, HttpServletResponse response) {
   		String username = getUsername();
   		//String chatRoot = "/chat/"+username;
   		if(operator==null) operator=">";
   		if(path==null) path="/chat";
   		String dateRange = lastModified==null?"":"and s.[jcr:lastModified] "+operator+" CAST('"+lastModified+"' AS DATE)";
		String chatQuery = "select * from [nt:base] AS s WHERE ISDESCENDANTNODE(["+path+"]) "+dateRange+" and s.ocm_classname='com.filemark.jcr.model.Chat' order by s.[jcr:lastModified] DESC";
		WebPage<Chat> chats = jcrService.queryChats(chatQuery, 12, 0);
		if(!"/chat".equals(path) && jcrService.nodeExsits(path+"/"+username)) {
			if(chats.getPageCount()>0) {
				Chat chat = chats.getItems().get(chats.getItems().size()-1);
				//logger.debug("lastModified:"+chat.getLastModified());
				jcrService.updateCalendar(path+"/"+username, "lastModified",chat.getLastModified());
			}

		}

   		return chats;
   	}

   	@RequestMapping(value = {"/protected/comments.html"}, method = {RequestMethod.GET})
   	public String  comments(String path,String status,Integer p,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
   		path += "/comments";
   		if(p==null)
   			p=0;
   		if( status == null || "0".equals(status)) {
   			status="";
		}else {
			status = " and s.from like '"+status+"'";
		}
		String chatQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+path+"])"+status+" and s.ocm_classname='com.filemark.jcr.model.Chat' order by s.[jcr:lastModified] DESC";
		WebPage<Chat> chats = jcrService.queryChats(chatQuery, 20, p);
		model.addAttribute("chats", chats);
   		return "chat/comments";
   	}

   	@RequestMapping(value = {"/protected/groupedit.html"}, method = {RequestMethod.GET})
   	public String  groupedit(String path,Integer p,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
   		if(p==null) p=0;
   		String userQuery = "select * from [nt:base] AS s WHERE ISCHILDNODE([/system/users]) and s.ocm_classname='com.filemark.jcr.model.User'";
		WebPage<Object> users = jcrService.queryObject(userQuery, 20, p);
   		String userInGroup = "select * from [nt:base] AS s WHERE ISCHILDNODE(["+path+"]) and s.ocm_classname='com.filemark.jcr.model.User'";
		WebPage<Object> usersInGroup = jcrService.queryObject(userInGroup, 20, p);

		model.addAttribute("users", users);
		model.addAttribute("usersInGroup", usersInGroup);
		model.addAttribute("folder", jcrService.getObject(path));   		
   		return "chat/group";
   	}
   	@RequestMapping(value = {"/protected/adduser.html"}, method = {RequestMethod.POST,RequestMethod.GET})
   	public @ResponseBody User addUser(String path,String group,Model model,HttpServletRequest request, HttpServletResponse response){

		User user = new User();
   		try {
   			User yun_user = (User)jcrService.getObject(path);
   			if(jcrService.nodeExsits(group+"/"+yun_user.getName())) {
   				user.setTitle("warning:用户已在群里！");
   			}else {
   				user.setUserName(yun_user.getUserName());
   				user.setTitle(yun_user.getTitle());
   				user.setPath(group+"/"+yun_user.getName());
   				user.setLastModified(new Date());
   				jcrService.addOrUpdate(user);
   			}
   			
   		}catch(Exception e) {
   			user.setTitle("error:"+e.getMessage());
   		}     		
   		return user;
   	}

   	@RequestMapping(value = {"/protected/removeuser.html"}, method = {RequestMethod.POST,RequestMethod.GET})
   	public @ResponseBody User removeUser(String path,Model model,HttpServletRequest request, HttpServletResponse response){

		User user = new User();
   		try {
   			jcrService.deleteNode(path);
   			user.setTitle("deleted");
   		}catch(Exception e) {
   			user.setTitle("error:"+e.getMessage());
   		}     		
   		return user;
   	}   	
   	@RequestMapping(value = {"/protected/addyouchat.html"}, method = {RequestMethod.POST})
   	public @ResponseBody String addyouchat(String path,String content,Model model,HttpServletRequest request, HttpServletResponse response){
			String username = getUsername();
   		try {

	   		if(content!=null && !content.equals("<p></p>") && !content.equals("") ) {
	   	   		Chat chat = new Chat();
	   	   		chat.setFrom(path);
	   	   		chat.setContent(content.replace("-edit",""));
	   	   		Calendar calendar = Calendar.getInstance();
	   	   		chat.setLastModified(calendar);
	   	   		chat.setPath(path+"/"+getDateTime());
	   	   		chat.setCreatedBy(username);
	   	   		chat.setTitle((String)request.getAttribute("usertitle"));
	   	   		jcrService.addOrUpdate(chat);
	 			
	   		}

   		}catch(Exception e) {
   			return "error:"+e.getMessage();
   		}     		
   		return username;
   	}
   	
   	@RequestMapping(value = {"/protected/addchat.html"}, method = {RequestMethod.POST})
   	public @ResponseBody String addchat(String path,String content,Model model,HttpServletRequest request, HttpServletResponse response){
   		String paths[] = path.split("/content/");
   		
   		paths = paths[1].split("/");
   		String home = "/chat/"+paths[0];
   		String username = getUsername();
   		try {
   		if(!jcrService.nodeExsits(home)) {
   			jcrService.addNodes(home, "nt:unstructured", username);
   		}
   		if(!jcrService.nodeExsits(home+"/"+username)) {
   			Folder folder = new Folder();
   			folder.setTitle((String)request.getAttribute("usertitle"));
   			folder.setName(username);
   			folder.setPath(home+"/"+username);
   			jcrService.addOrUpdate(folder);
   		}
	   		if(content!=null && !content.equals("<p></p>") && !content.equals("") ) {
	   	   		Chat chat = new Chat();
	   	   		chat.setFrom(path);
	   	   		chat.setContent(content.replace("-edit",""));
	   	   		Calendar calendar = Calendar.getInstance();
	   	   		chat.setLastModified(calendar);
	   	   		chat.setPath(home+"/"+username+"/"+calendar.getTime().getTime());
	   	   		chat.setCreatedBy(username);
	   	   		chat.setTitle((String)request.getAttribute("usertitle"));
	   	   		jcrService.addOrUpdate(chat);
	 			
	   		}

   		}catch(Exception e) {
   			return "error:"+e.getMessage();
   		}     		
   		return home+"/"+username;
   	}

   	@RequestMapping(value = {"/protected/addcomment.html"}, method = {RequestMethod.POST})
   	public @ResponseBody String addComment(String path,String content,String status,Model model,HttpServletRequest request, HttpServletResponse response){

   		String username = getUsername();
   		String comment = path+"/comments";
   		try {
	   		if(!jcrService.nodeExsits(comment)) {
	   			jcrService.addNodes(comment, "nt:unstructured", username);
	   		}
	   		if(content!=null && !content.equals("<p></p>") && !content.equals("") ) {
	   	   		Chat chat = new Chat();
	   	   		chat.setFrom(status);
	   	   		chat.setContent(content.replace("-edit",""));
	   	   		Calendar calendar = Calendar.getInstance();
	   	   		chat.setLastModified(calendar);
	   	   		chat.setPath(comment+"/"+calendar.getTime().getTime());
	   	   		chat.setCreatedBy(username);
	   	   		chat.setTitle((String)request.getAttribute("usertitle"));
	   	   		jcrService.addOrUpdate(chat);
	 			
	   		}
   		}catch(Exception e) {
   			return "error:"+e.getMessage();
   		}     		
   		return "";
   	}
    	
   	@RequestMapping(value = {"/protected/share.html"}, method = {RequestMethod.GET})
   	public String share(Model model,String path,HttpServletRequest request, HttpServletResponse response) throws Exception {
   		String username = getUsername();
   		Page page = null;

   		if(path.indexOf("/content/templates")>=0) {
   			page = copy(username,path);
   		}else {
   			page = share(username,path);
   		}
   		page.setBreadcrumb(jcrService.getBreadcrumb(page.getPath()));
   		jcrService.addOrUpdate(page);
   		model.addAttribute("page", page);
   		model.addAttribute("username", request.getAttribute("username"));
   		model.addAttribute("usertitle", request.getAttribute("usertitle"));   		
		//model.addAttribute("navigation",jcrService.getPageNavigation("/content/"+getUsername(),2));		
   		return "content/page";
   	}
   	
   	private Page share(String username,String path) throws RepositoryException {
   		Page shared = jcrService.getPage(path);
   		String paths[] = path.split("/");
   		String parentPath = "/content/"+username+"/shared";
   		if(!jcrService.nodeExsits(parentPath)) {
   			copy(username,"/content/templates/shared");
   		}
   		for(int i = 3; i <paths.length - 3; i++) {
   			parentPath +="/"+paths[i];
   		}
   		if(!jcrService.nodeExsits(parentPath)) {
   			share(username,parentPath);
   		}
   		Page page = new Page();
   		page.setParent(parentPath);
   		page.setPath(parentPath+"/"+paths[paths.length-1]);
   		page.setDescription(shared.getDescription());
   		page.setRedirectTo(path);
   		page.setContent(shared.getContent());   		
   		page.setTitle(shared.getTitle());
   		page.setStatus(shared.getStatus());
   		page.setLastPublished(shared.getLastPublished());
   		page.setCreatedBy(username);
   		jcrService.addOrUpdate(page);
   		return page;
   	}
   	
   	@RequestMapping(value = {"/protected/sharing.html"}, method = {RequestMethod.GET})
   	public String sharing(String path,String type, String input,String kw,Integer p,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
   		String username = getUsername();
		Folder folder = jcrService.getFolder(path);
		Folder parent = jcrService.getFolder(folder.getParent());		
		
		if((folder.getSharing()==null || folder.getSharing().indexOf(username+"@")<0) && (parent.getSharing()==null || parent.getSharing().indexOf(username+"@")<0)) {
			throw new Exception("æ²¡æœ‰æ�ƒé™�ï¼�");
		}
		String orderby = "[lastModified] desc";
		if(folder.getOrderby()!=null && !"".equals(folder.getOrderby()) && !"rank,name".equals(folder.getOrderby())) {
			orderby = folder.getOrderby();
		}		
		if(p==null) p= 0 ;
		String keywords = "";
		if(kw==null) {
			kw="";
		}else if(!"".equals(kw)){
			//logger.info(kw);
			//kw = DjnUtils.Iso2Uft8(kw);//new String(kw.getBytes("ISO-8859-1"), "GB18030");
			//logger.info(kw);
			keywords = " and contains(s.*,'"+kw+"')";
		}

		String contentType = "";
		
		if(type!=null && !type.equals("") && !"child".equals(type)) {
			if(type.equals("media")) type="video";
			contentType = " and s.contentType like '"+type+"%'";
		}
		boolean isIntranet = isIntranet(request);
		String intranet = (isIntranet?"":" and (f.intranet is null or f.intranet not like 'true')");
		String ISDESCENDANTNODE = "ISDESCENDANTNODE";
		if(type!=null && "child".equals(type)) ISDESCENDANTNODE = "ISCHILDNODE";
		String sharingQuery = "select * from [nt:base] AS f WHERE f.sharing like '%"+getUsername()+"@%'" +keywords+intranet+" and f.delete not like 'true' and f.ocm_classname='com.filemark.jcr.model.Folder' order by f.path";
		WebPage<Folder> shares = jcrService.queryFolders(sharingQuery, 100, 0);
		model.addAttribute("shares", shares);

		String foldersQuery = "select * from [nt:base] AS f WHERE ISCHILDNODE(["+path+"])" +" and (f.sharing like '%"+getUsername()+"@%' or f.sharing is null)" +keywords+intranet+" and f.delete not like 'true' and f.ocm_classname='com.filemark.jcr.model.Folder' order by f.path";
		WebPage<Folder> folders = jcrService.queryFolders(foldersQuery, 100, 0);
		model.addAttribute("folders", folders);

		String assetsQuery = "select s.* from [nt:base] AS s INNER JOIN [nt:unstructured] AS f ON ISCHILDNODE(s, f) WHERE "+ISDESCENDANTNODE+"(s,["+path+"])"+" and (f.sharing like '%"+getUsername()+"@%' or f.sharing is null)"+keywords+contentType+intranet+" and s.[delete] not like 'true' and s.ocm_classname='com.filemark.jcr.model.Asset' order by s."+orderby+", s.[name]";
		//logger.info(isIntranet+",ip="+getClientIpAddress(request)+",q="+assetsQuery);;
		WebPage<Asset> assets = jcrService.searchAssets(assetsQuery, 12, p);
		Page page = new Page();
		page.setTitle("å…±äº«äº‘");
		model.addAttribute("page", page);
		model.addAttribute("folder", folder);
		model.addAttribute("assets", assets);
		model.addAttribute("path", path);
		model.addAttribute("type", type);
		model.addAttribute("input", input);		
		model.addAttribute("kw", kw);	
		if(folder.getReadonly() !=null && "true".equals(folder.getReadonly()) || ( parent !=null && "true".equals(parent.getReadonly()))) {
			return "chat/asset_readonly";
		}
		return "chat/asset";
   		//return "protected/sharing";
   	}
   	
   	private Page copy(String username,String path) throws RepositoryException {
   		Page shared = jcrService.getPage(path);
   		String paths[] = path.split("/");
   		String parentPath = "/content/"+username;
   		for(int i = 3; i <paths.length - 3; i++) {
   			parentPath +="/"+paths[i];
   		}
   		if(!jcrService.nodeExsits(parentPath)) {
   			copy(username,parentPath);
   		}
   		Page page = new Page();
   		page.setParent(parentPath);
   		page.setPath(parentPath+"/"+paths[paths.length-1]);
   		page.setDescription(shared.getDescription());
   		page.setContent(shared.getContent().replace("/templates", "/"+username));
   		page.setTitle(shared.getTitle());
   		page.setStatus(shared.getStatus());
   		page.setLastPublished(shared.getLastPublished());
   		page.setCreatedBy(username);
   		jcrService.addOrUpdate(page);
   		return page;
   	}   	
	private String getRolePrefix() {
		
		return "ROLE_";
	} 
}
