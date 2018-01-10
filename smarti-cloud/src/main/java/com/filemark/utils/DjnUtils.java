package com.filemark.utils;

import java.io.UnsupportedEncodingException;

public class DjnUtils {
	public static String Iso2Uft8(String original) throws UnsupportedEncodingException {
		return new String(original.getBytes("ISO-8859-1"), "UTF-8");
	}
	public static String Iso2GB18030(String original) throws UnsupportedEncodingException {
		return new String(original.getBytes("ISO-8859-1"), "GB18030");
	}
	
	public static String utf8(String original) throws UnsupportedEncodingException {
		return new String(original.getBytes("UTF-8"));
	}		
}
