package com.filemark.sso.resources;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.filemark.jcr.controller.BaseController;
import com.filemark.sso.CookieUtil;
import com.filemark.sso.JwtUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;

@Controller
public class ResourceController  extends BaseController{

    @RequestMapping("/protected")
    public String home() {
        return "redirect:/protected";
    }

    @RequestMapping("/protected/resources")
    public String protectedResource() {
        return "protected/resources";
    }

    @RequestMapping("/logout")
    public String logout(String redirect,HttpServletRequest request,HttpServletResponse httpServletResponse) {
    	//String username = getUsername();
        String domain = request.getServerName();//.replaceAll(".*\\.(?=.*\\.)", "");
        String root = domain.replaceAll(".*\\.(?=.*\\.)", "");
        
        CookieUtil.clear(httpServletResponse, JwtUtil.jwtTokenCookieName,domain);
        CookieUtil.clear(httpServletResponse, JwtUtil.jwtTokenCookieName,root);
        Cookie[] cookies = request.getCookies();
        if (cookies != null)
            for (Cookie cookie : cookies) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                httpServletResponse.addCookie(cookie);
            }       
        request.getSession().invalidate();
        
        if(redirect !=null && !"".equals(redirect))
        	return "redirect:"+redirect;
        else
        	return "redirect:/signout";
    }
}
