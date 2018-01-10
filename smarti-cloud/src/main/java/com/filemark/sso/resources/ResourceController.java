package com.filemark.sso.resources;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.filemark.jcr.controller.BaseController;
import com.filemark.sso.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;

@Controller
public class ResourceController  extends BaseController{
    private static final String jwtTokenCookieName = "JWT-TOKEN";

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
        String domain = request.getServerName().replaceAll(".*\\.(?=.*\\.)", "");
        CookieUtil.clear(httpServletResponse, jwtTokenCookieName,domain);
        
        request.getSession().invalidate();
        
        if(redirect !=null && !"".equals(redirect))
        	return "redirect:"+redirect;
        else
        	return "redirect:/signout";
    }
}
