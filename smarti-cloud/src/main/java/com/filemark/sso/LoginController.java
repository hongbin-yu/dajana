package com.filemark.sso;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Random;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.filemark.jcr.controller.BaseController;
import com.filemark.jcr.controller.SiteController;
import com.filemark.jcr.model.Page;
import com.filemark.jcr.model.Role;
import com.filemark.jcr.model.User;
import com.filemark.utils.DjnUtils;

@Controller
public class LoginController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(SiteController.class);

    public LoginController() {
    }



    @RequestMapping(value="/login",method = RequestMethod.GET)
    public String login(Model model,HttpServletRequest request,HttpServletResponse httpServletResponse, String redirect,String info, Integer loginCount) throws UnsupportedEncodingException{
    	if(redirect!=null && redirect.equals("")) {
            String domain = request.getServerName().replaceAll(".*\\.(?=.*\\.)", "");
            CookieUtil.clear(httpServletResponse, JwtUtil.jwtTokenCookieName,domain);
            
            request.getSession().invalidate();
    		return "login";
    	}
    	if(redirect!=null && redirect.equals("signin")) return "redirect:signin";    	
    	String lastIp = request.getRemoteAddr();
    	String remoteHost = request.getRemoteHost();
        String username = JwtUtil.getSubject(request, JwtUtil.jwtTokenCookieName, JwtUtil.signingKey);
        if(loginCount==null) {
        	loginCount=0;
        }
        if(username != null){
        	String authors[] = username.split("/");
        	if(authors[0]==null || "null".equals(authors[0])) {
        		authors[0] = "";
        	}
			Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			User user = new User();
			user.setHost(authors[0]);
			user.setPort(authors[1]);
			user.setUserName(authors[2]);
			user.setTitle(authors[3]);
			user.setSigningKey(authors[4]);			
        	try {
        		if(jcrService.nodeExsits("/system/users/"+authors[1].toLowerCase())) {
        			user = (User)jcrService.getObject("/system/users/"+authors[2].toLowerCase());
        			if(!lastIp.equals(user.getLastIp())) return "login";
        			for(Role role:user.getRoles()) {
        				authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+role.getRoleName().toUpperCase()));
        			}
        			
        			
        		}
    			for(int i=4; i<authors.length; i++) {
    				authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+authors[i].toUpperCase()));
    				
    			}
    			
				authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+user.getRole().toLowerCase()));//default role
				org.springframework.security.core.userdetails.User userdetails = new org.springframework.security.core.userdetails.User(user.getUserName(),"protected",true,true,true,true,authorities);
  			
    			Authentication auth = 
    			new UsernamePasswordAuthenticationToken(userdetails, null, authorities);
    			SecurityContext securityContext = SecurityContextHolder.getContext();
    			securityContext.setAuthentication(auth);    			
    			HttpSession session = request.getSession(true);
    	        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
        		if(redirect !=null && !"".equals(redirect) && !"login".equals(redirect))
        			return "redirect:"+redirect;
        		else if(!authors[0].equals(remoteHost)) {
        			return "redirect:http://"+authors[0]+"/site/editor.html?path=/content/"+authors[2];
        		}else {
        			return "redirect:/site/editor.html?path=/content/"+authors[2];
        			
        		}

        	} catch (RepositoryException e) {
				Log.error(e.getMessage());
				model.addAttribute("error", e.getMessage());
			}
        }    	
 
    	Page page = new Page();
    	String title = messageSource.getMessage("login", null,"&#30331;&#20837;", localeResolver.resolveLocale(request));

    	page.setTitle(title);
    	model.addAttribute("j_username", "");
    	model.addAttribute("info", info);
    	model.addAttribute("page", page);
    	model.addAttribute("redirect", redirect);
    	model.addAttribute("loginCount", loginCount);  	
    	
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(HttpServletRequest request,HttpServletResponse httpServletResponse, String j_username, String j_password, String redirect,Integer loginCount, Model model) throws RepositoryException{
    	User user = new User();
    	Page page = new Page();
    	String title = messageSource.getMessage("djn.login", null,"&#30331;&#20837;", localeResolver.resolveLocale(request));

    	page.setTitle(title);
    	String lastIp = getClientIpAddress(request);
    	if(loginCount==null) loginCount=0;
    	loginCount++;
    	model.addAttribute("page", page);   
    	model.addAttribute("redirect", redirect);
    	model.addAttribute("loginCount", loginCount);  	    	
        if (j_username == null || !jcrService.nodeExsits("/system/users/"+j_username.toLowerCase()) ){
        	String login_error = messageSource.getMessage("login_error", null,"&#29992;&#25143;&#21517;&#25110;&#23494;&#30721;&#26080;&#25928;", localeResolver.resolveLocale(request));
        	
            model.addAttribute("error", login_error);
            return "login";
        }else {
        	
        	user = (User)jcrService.getObject("/system/users/"+j_username.toLowerCase());
        	if(user.getSigningKey() ==null || user.getSigningKey().equals("")) user.setSigningKey("dajana.cn");
        	
        	if(!user.getPassword().equals(j_password)) {
            	String login_error = messageSource.getMessage("login_error", null,"&#29992;&#25143;&#21517;&#25110;&#23494;&#30721;&#26080;&#25928;", localeResolver.resolveLocale(request));
        		
                model.addAttribute("error", login_error);
                return "login";        		
        	}
        	jcrService.updatePropertyByPath(user.getPath(), "lastIp", lastIp);
        }
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+"USER"));
		for(Role role:user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+role.getRoleName().toUpperCase()));
		} 
		if(user.getPort()==null || "".equals(user.getPort())) {
			user.setPort("");
		}else {
			user.setPort(":"+user.getPort());
		}
        String token_author = user.getHost()+"/"+user.getPort()+"/"+j_username+"/"+user.getTitle()+"/"+request.getRemoteAddr();
		for(Role role:user.getRoles()) {
			token_author += "/"+role.getRoleName();
		}
		org.springframework.security.core.userdetails.User userdetails = new org.springframework.security.core.userdetails.User(user.getUserName(),"proptected",true,true,true,true,authorities);
		
		Authentication auth = 
		new UsernamePasswordAuthenticationToken(userdetails, null, authorities);
		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(auth);
		HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
		request.setAttribute("usersite", user.getHost());
		request.setAttribute("port", user.getPort());
		request.setAttribute("username", j_username);
		request.setAttribute("usertitle", user.getTitle()); 
		request.setAttribute("signingKey", user.getSigningKey());  
        String token = JwtUtil.generateToken(JwtUtil.signingKey, token_author);
        String domain =(user.getHost()==null || "".equals(user.getHost()))? request.getServerName():user.getHost();
        logger.debug("domain:"+domain);
        CookieUtil.create(httpServletResponse, JwtUtil.jwtTokenCookieName, token, false, -1, domain);

        if(redirect==null || "".equals(redirect) || "login".equals(redirect)) {
    		String content = "/content/"+user.getUserName();

    		if(user.getHost()!=null && !"".equals(user.getHost())) {
    			String url = "http://"+(user.getHost()+(user.getPort()==null || "".equals(user.getPort())?"":user.getPort()) + content+".html");
    			logger.debug("redirect:"+url);
    			return "redirect:"+url;
    		}
    		logger.debug("redirect:/site/editor.html?path=" + content);
    		return "redirect:/site/editor.html?path=" + content;
        }else {
        	logger.debug("redirect:"+ redirect);
        	return "redirect:"+ redirect;
        }
    }
    @RequestMapping(value="/signup",method = RequestMethod.GET)
    public String signup(String host,Model model,HttpServletResponse httpServletResponse){
    	String imgs[] = {"shu","niu","fu","tu","long","she","ma","yang","hou","ji","gou","zhu"};
    	String ids[] = {"A0","A1","A2","B0","B1","B2","C0","C1","C2","D0","D1","D2"};
    	Page page = new Page();
    	page.setTitle("\u6CE8\u518C ");
    	
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
    	User user = new User();
    	user.setHost(host);
    	model.addAttribute("user", user);
    	model.addAttribute("imgs", imgs);    
    	model.addAttribute("ids", ids);
    	model.addAttribute("page", page);    	
        return "signup";
    } 
    
    @RequestMapping(value="/signup",method = RequestMethod.POST)
    public String register(User user,Model model,HttpServletRequest request,HttpServletResponse httpServletResponse) throws RepositoryException, UnsupportedEncodingException{
    	if(jcrService.nodeExsits("/system/users/"+user.getUserName().toLowerCase()))
    	{
    		model.addAttribute("user", user);
        	String user_exists = messageSource.getMessage("djn.user_exists", null,"\u7F51\u540D\u5B58\u5728\u7CFB\u7EDF\u4E2D,\u8BF7\u6362\u4E2A\u7F51\u540D", localeResolver.resolveLocale(request));

    		model.addAttribute("error",user.getUserName()+user_exists);
    	}else if((user.getEmail()==null || user.getEmail().equals("")) && (user.getPhoneNumber()==null || user.getPhoneNumber().equals(""))) {
    		model.addAttribute("user", user);
        	String email_phone_empty = messageSource.getMessage("djn.email_phone_empty", null,"\u5730\u5740\u90AE\u4EF6\u6216\u7535\u8BDD\u53F7\u7801\u5FC5\u987B\u586B\u4E00\u4E2A\u6211\u627E\u4E3A\u5BC6\u7801", localeResolver.resolveLocale(request));
    		
    		model.addAttribute("error", email_phone_empty);
    	}else {
    		if(!jcrService.nodeExsits("/system/users")) {
    			jcrService.addNodes("/system/users", "nt:unstructured","systemUser");		
    		}
        	String lastIp = request.getRemoteAddr();
    		user.setPath("/system/users/"+user.getUserName());
    		String title =user.getTitle();// DjnUtils.Iso2Uft8(user.getTitle());
    		user.setTitle(title);
    		user.setLastIp(lastIp);
    		jcrService.addOrUpdate(user);

        	String signup_success = messageSource.getMessage("djn.signup_success", null,"\u6CE8\u518C\u6210\u529F\u8BF7\u767B\u5165", localeResolver.resolveLocale(request));
    		
    		model.addAttribute("info", signup_success);
    		model.addAttribute("j_username", user.getUserName());
    		return "login";
    	}

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
    	model.addAttribute("page", page);    	

        return "signup";
    }     
	private String getRolePrefix() {
		
		return "ROLE_";
	}   
	
	private static final String[] IP_HEADER_CANDIDATES = { 
	    "X-Forwarded-For",
	    "Proxy-Client-IP",
	    "WL-Proxy-Client-IP",
	    "HTTP_X_FORWARDED_FOR",
	    "HTTP_X_FORWARDED",
	    "HTTP_X_CLUSTER_CLIENT_IP",
	    "HTTP_CLIENT_IP",
	    "HTTP_FORWARDED_FOR",
	    "HTTP_FORWARDED",
	    "HTTP_VIA",
	    "REMOTE_ADDR" };
	
	public static String getClientIpAddress(HttpServletRequest request) {
	    for (String header : IP_HEADER_CANDIDATES) {
	        String ip = request.getHeader(header);
	        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
	            return ip;
	        }
	    }
	    return request.getRemoteAddr();
	}	
}
