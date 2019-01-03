/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.filemark.jcr.controller;



import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.jcr.RepositoryException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
import org.springframework.web.multipart.MultipartFile;

import com.filemark.jcr.model.Page;
import com.filemark.jcr.model.Role;
import com.filemark.jcr.model.User;
import com.filemark.sso.CookieUtil;
import com.filemark.sso.JwtUtil;
import com.filemark.utils.ScanUploadForm;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

@Controller
public class SigninController extends BaseController{
	private static final Logger logger = LoggerFactory.getLogger(SigninController.class);

	@RequestMapping(value="/signin", method={RequestMethod.GET})
	public String signin(String redirect, String username, HttpServletRequest request, HttpServletResponse response,Model model) {

        //String domain = request.getServerName().replaceAll(".*\\.(?=.*\\.)", "");
        //CookieUtil.clear(response, jwtTokenCookieName,domain);
        //request.getSession().invalidate();
		if(username==null) username="";
		model.addAttribute("j_username",username);
		model.addAttribute("loginCount", "0");
    	return "signin";		
	}

	@RequestMapping(value="/signin", method=RequestMethod.POST)
	public String signinPost(HttpServletRequest request,HttpServletResponse httpServletResponse, String j_username, String j_password, String redirect,Integer loginCount, Model model) throws RepositoryException {

    	User user = new User();
    	Page page = new Page();
    	String title = messageSource.getMessage("djn.login", null,"&#30331;&#20837;", localeResolver.resolveLocale(request));
    	int maxAge = -1;
    	if(request.getParameter("reme")!=null) {
    		maxAge = 86400*30;
    		//logger.info("Signin Cookie age="+maxAge);
    	}
    	page.setTitle(title);
    	String lastIp = getClientIpAddress(request);
    	if(loginCount==null) return signin(redirect,j_username, request, httpServletResponse,model);
    	loginCount++;
    	model.addAttribute("page", page);   
    	model.addAttribute("redirect", redirect);
    	model.addAttribute("loginCount", loginCount);  	    	
        String domain =request.getServerName();//.replaceAll(".*\\.(?=.*\\.)", "");
    	if (j_username == null || !jcrService.nodeExsits("/system/users/"+j_username.toLowerCase()) ){
        	String login_error = messageSource.getMessage("login_error", null,"&#29992;&#25143;&#21517;&#25110;&#23494;&#30721;&#26080;&#25928;", localeResolver.resolveLocale(request));
        	
            model.addAttribute("error", login_error);
            return "signin";
        }else {
        	
        	user = (User)jcrService.getObject("/system/users/"+j_username.toLowerCase());
        	//if(user.getSigningKey() ==null || user.getSigningKey().equals("")) user.setSigningKey("dajana.cn");
        	String serverName = request.getServerName();
        	if(!serverName.startsWith("local.home") && !serverName.startsWith("home")) {
        		
        	}
        	if(!user.getSigningKey().equals(j_password)) {
            	String login_error = messageSource.getMessage("login_error", null,"&#29992;&#25143;&#21517;&#25110;&#23494;&#30721;&#26080;&#25928;", localeResolver.resolveLocale(request));
                model.addAttribute("error", login_error);
                return "signin";        		
        	}
			try {

	        	Connection con = Jsoup.connect("http://ns2."+jcrService.getDomain()+":8888/myip/home").timeout(5000);
	        	Connection.Response res = con.execute();
	    		if( res.statusCode() == 200) {
						Document doc = con.get();
						String myip = doc.body().text();
			        	logger.debug("hostIp="+myip+",remoteIp="+lastIp);
						if(lastIp.equals(myip)) {
				        	jcrService.updatePropertyByPath(user.getPath(), "hostIp", myip);
				        	//jcrService.updatePropertyByPath(user.getPath(), "lastIp", lastIp);
				        	domain = "local.home."+jcrService.getDomain();
				        	redirect = "http://home."+jcrService.getDomain()+"/site/view/html";
						}
	    		}else {
	    			logger.debug("statusCode="+ res.statusCode());
	    		}
			} catch (Exception e) {
				logger.error("sigin error:"+e.getMessage());
			}

    		jcrService.updatePropertyByPath(user.getPath(), "lastIp", lastIp);
        }
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+user.getRole().toLowerCase()));//default role

        logger.debug("domain="+domain);
        String remoreHost = request.getRemoteHost();
		InetAddress ipAddr;
		try {
			ipAddr = InetAddress.getLocalHost();
			remoreHost = ipAddr.getHostAddress();
		} catch (UnknownHostException e) {
			logger.error(e.getMessage());;
		}

		for(Role role:user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+role.getRoleName().toUpperCase()));
		}
		authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+"USER"));//default role		
		if(user.getPort()==null || "".equals(user.getPort())) {
			user.setPort("");
		}else {
			user.setPort(":"+user.getPort());
		}
        String token_author = domain+"/"+remoreHost+"/"+j_username+"/"+user.getTitle()+"/"+request.getRemoteAddr();
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

        logger.debug(token_author);
        //(user.getHost()==null || "".equals(user.getHost()))? request.getServerName():user.getHost();
/*        if(redirect !=null) {
        	try {
				URL url = new URL(redirect);
				domain = url.getHost();
			} catch (MalformedURLException e) {
				redirect +="&error="+e.getMessage();
			}	
        }*/
        
        CookieUtil.create(httpServletResponse, JwtUtil.jwtTokenCookieName, token, false, maxAge, domain);
        //if(user.getHost()!=null && !domain.equals(user.getHost()))
        	//CookieUtil.create(httpServletResponse, JwtUtil.jwtTokenCookieName, token, false, -1, user.getHost());
        if(redirect==null || "".equals(redirect) || "signin".equals(redirect)) {
    		return "redirect:/site/view.html?type=child";
        }else {
        	logger.debug("Redirect to:"+ redirect+"&domain="+domain);
       		return "redirect:"+ redirect+"&domain="+domain;
        }
		
	
	}	
	
    @RequestMapping(value = {"/forget"}, method ={ RequestMethod.GET})
   	public String forgetpassword(String j,Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {

    	if(j!=null) {
	        String domain = request.getServerName();
    		try {
    			String json = JwtUtil.decode(j);
    			JsonParser parser = new JsonParser();
    			final JsonObject jsonObject = parser.parse(json).getAsJsonObject();
    			String username = jsonObject.get("username").isJsonNull()?"":jsonObject.get("username").getAsString();
    			String password = jsonObject.get("password").isJsonNull()?"":jsonObject.get("password").getAsString();
    			String isIntranet = (jsonObject.get("isIntranet")==null || jsonObject.get("isIntranet").isJsonNull())?"false":jsonObject.get("isIntranet").getAsString();    			
    			long expired = (jsonObject.get("expired")==null || jsonObject.get("expired").isJsonNull())?0:jsonObject.get("expired").getAsLong();
    			String redirect = (jsonObject.get("redirect")==null || jsonObject.get("redirect").isJsonNull())?"":jsonObject.get("redirect").getAsString();
    			if("yes".equals(isIntranet) && !isIntranet(request)) {
    	            model.addAttribute("error", "login_intranet"); 
    	            return "redirect:http://"+domain+"/forget?error=login_intranet"; 		    				
    			}
    			if(expired >0 && expired > new Date().getTime()) {
    	            model.addAttribute("error", "login_expired"); 
    	            return "redirect:http://"+domain+"/forget?error=login_expired"; 				
    			}
    			User user = (User)jcrService.getObject("/system/users/"+username);
    			if(user.getPassword()==null || !user.getPassword().equals(password)) {
    	            model.addAttribute("error", "login_error");    
    	            return "redirect:http://"+domain+"/forget?error=bad_credentials"; 		
    			}
    			Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    			authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+user.getRole().toLowerCase()));//default role

    			for(Role role:user.getRoles()) {
    				authorities.add(new SimpleGrantedAuthority(this.getRolePrefix()+role.getRoleName().toUpperCase()));
    			} 
    			if(user.getPort()==null || "".equals(user.getPort())) {
    				user.setPort("");
    			}else {
    				user.setPort(":"+user.getPort());
    			}
    	        String token_author = user.getHost()+"/"+user.getPort()+"/"+username+"/"+user.getTitle()+"/"+request.getRemoteAddr();
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
    			request.setAttribute("username", username);
    			request.setAttribute("usertitle", user.getTitle()); 
    			request.setAttribute("signingKey", user.getSigningKey());  
    	        String token = JwtUtil.generateToken(JwtUtil.signingKey, token_author);


   	        
    	        CookieUtil.create(response, JwtUtil.jwtTokenCookieName, token, false, -1, domain);
    	        if(user.getHost()!=null && !domain.equals(user.getHost()))
    	        	CookieUtil.create(response, JwtUtil.jwtTokenCookieName, token, false, -1, user.getHost());
    	        if(redirect==null || "".equals(redirect) || "signin".equals(redirect)) {
    	    		return "redirect:http://"+domain+"/protected/youlook.html";
    	        }else {
    	       		return "redirect:"+ redirect+"&domain="+domain;
    	        }
    			
    		}catch( Exception e) {
    			logger.error("forget:"+e.getMessage());
    			return "redirect:http://"+domain+"/forget?error="+e.getMessage();
    		}
    	}
    	return "forget";
    }
    @RequestMapping(value = {"/forget","/uploadAsset.html"}, method ={ RequestMethod.POST}) 
    public String forgetPost(String j_username, String j_code, String redirect,/*ScanUploadForm uploadForm,*/Model model,HttpServletRequest request, HttpServletResponse response) {
    	//String error=uploadForm.getFilename();
    	
    	try {
    		//if(j_username !=null && j_password != null && !"".equals(j_password) && !"".equals(j_username)) {
            	User user = (User)jcrService.getObject("/system/users/"+j_username.toLowerCase());
        		Date now = new Date();
         	
            	if(!user.getCode().equals(j_code) || user.getLastVerified()==null || now.getTime() - user.getLastVerified().getTime() > 120000) {
                	String login_error = messageSource.getMessage("djn.verified_error", null,"用户名或验证码无效", localeResolver.resolveLocale(request));
                    model.addAttribute("error", login_error);
                    return "forget";        		
            	}    			
    		//}
            	return signinPost(request,response,user.getUserName(),user.getSigningKey(),redirect,1,model);
/*    		for (MultipartFile multipartFile : uploadForm.getFile()) {
		    	final ByteArrayOutputStream os = new ByteArrayOutputStream();	
	    		InputStream in = multipartFile.getInputStream();
	    		BufferedImage image = ImageIO.read(in);
	    		in.close();
				ImageIO.write(image, "jpg", os);
				os.close();
	    		String qrimage =  "data:image/png;base64,"+Base64.getEncoder().encodeToString(os.toByteArray());
				model.addAttribute("qrimage",qrimage);
	            LuminanceSource tmpSource = new BufferedImageLuminanceSource(image);
	            BinaryBitmap tmpBitmap = new BinaryBitmap(new HybridBinarizer(tmpSource));
	    		MultiFormatReader tmpBarcodeReader = new MultiFormatReader();
	    		Result result = tmpBarcodeReader.decode(tmpBitmap);
	    		String url = result.getText();
	    		if(url.startsWith("http")) {
	    			response.sendRedirect(url);
	    			return null;
	    		}else {
	    			RequestDispatcher dispatcher = request.getRequestDispatcher(url);
	    		    dispatcher.forward(request, response);
	    		    return null;
	    		}



			}*/
    	}catch(Exception e) {
    		logger.error("error:"+e.getMessage());
    		String error=e.getMessage();
    		model.addAttribute("error", "出错:"+error);
    	}

    	return "forget";
    }
    
	@RequestMapping(value="/register", method=RequestMethod.GET)
	public void register(String redirect, HttpServletRequest request, HttpServletResponse response) {
	}
	
	private String getRolePrefix() {
		
		return "ROLE_";
	}

}
