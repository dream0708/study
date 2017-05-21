package com.sohu.bp.elite.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

public class WXResponseWrapper extends HttpServletResponseWrapper{
	ByteArrayOutputStream output;
	FilterServletOutputStream filterOutput;

	public WXResponseWrapper(HttpServletResponse response) {
	    super(response);
	    output = new ByteArrayOutputStream();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
	    if (filterOutput == null) {
	        filterOutput = new FilterServletOutputStream(output);
	        				   
	    }
	    return filterOutput;
	}

	public byte[] getContent() {
	    return output.toByteArray();
	}
}
