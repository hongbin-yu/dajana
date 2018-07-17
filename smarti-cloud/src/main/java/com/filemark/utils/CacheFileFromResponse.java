package com.filemark.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CacheFileFromResponse extends HttpServletResponseWrapper {
	private File file;
    private PrintWriter printWriter = null;
    private ServletOutputStream outputStream = null;
    
	public CacheFileFromResponse(HttpServletResponse response, File file) {
		super(response);
		this.file = file;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
        if (this.printWriter != null) {
            throw new IllegalStateException(
                "Cannot call getOutputStream( ) after getWriter( )");
        }

        if (this.outputStream == null) {
        	this.outputStream = super.getOutputStream();
        }
        return super.getOutputStream();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
        if (this.outputStream != null) {
            throw new IllegalStateException(
                    "Cannot call getWriter( ) after getOutputStream( )");
        }

        if (this.printWriter == null) {
        	this.printWriter = new PrintWriter(file);
        }
        return this.printWriter;
	}
	
}
