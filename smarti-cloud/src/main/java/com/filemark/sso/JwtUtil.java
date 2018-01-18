package com.filemark.sso;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;

import java.util.Base64;
import java.util.Date;

public class JwtUtil {
	static String SYSTEMSECRET = Base64.getEncoder().encodeToString("yuhongyun+".getBytes()); 
	
    public static String generateToken(String signingKey, String subject) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        final String SECRET = Base64.getEncoder().encodeToString(signingKey.getBytes());
        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, SECRET);

        return builder.compact();
    }

    public static String encode(String subject) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, SYSTEMSECRET);

        return builder.compact();
    }
    
    public static String getSubject(HttpServletRequest httpServletRequest, String jwtTokenCookieName, String signingKey){
        String token = CookieUtil.getValue(httpServletRequest, jwtTokenCookieName);
        final String SECRET = Base64.getEncoder().encodeToString(signingKey.getBytes());
        
        if(token == null) return null;
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().getSubject();
    }
    
    public static String decodeToken(String signingKey, String token) {
        if(token == null) return null;
        final String SECRET = Base64.getEncoder().encodeToString(signingKey.getBytes());
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().getSubject();
    	
    }
    
    public static String decode(String token) {
        if(token == null) return null;
        return Jwts.parser().setSigningKey(SYSTEMSECRET).parseClaimsJws(token).getBody().getSubject();
    	
    }    
}
