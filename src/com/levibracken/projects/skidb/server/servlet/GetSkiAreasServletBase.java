package com.levibracken.projects.skidb.server.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.levibracken.projects.skidb.server.entity.SkiAreaFormat;
import com.levibracken.projects.skidb.server.entity.SkiAreaOrder;

public abstract class GetSkiAreasServletBase extends HttpServlet {

	private static final long serialVersionUID = 8710474021629946150L;

	protected static final int DEFAULT_COUNT = 0;	// Zero will return all results
	protected static final int DEFAULT_PAGE = 0;	
	protected static final SkiAreaOrder DEFAULT_ORDER = SkiAreaOrder.NAME;
	protected static final SkiAreaFormat DEFAULT_FORMAT = SkiAreaFormat.SHORT;
		
	
	protected int getCount(HttpServletRequest request) {
		return getIntValueFromRequestParameter(request, "count", DEFAULT_COUNT);
	}
	
	protected int getPage(HttpServletRequest request) {
		return getIntValueFromRequestParameter(request, "page", DEFAULT_PAGE);
	}
	
	protected SkiAreaOrder getOrder(HttpServletRequest request) {
		return getEnumValueFromRequestParameter(request, "order",
				SkiAreaOrder.class, DEFAULT_ORDER);
	}
	
	protected SkiAreaFormat getFormat(HttpServletRequest request) {
		return getEnumValueFromRequestParameter(request, "format",
				SkiAreaFormat.class, DEFAULT_FORMAT);
	}
	
	protected int getIntValueFromRequestParameter(HttpServletRequest request,
			final String key, final int defaultValue) {
		
		final String valueStr = request.getParameter(key);		
		if(null != valueStr) {
			try { 
				return Integer.parseInt(valueStr.trim());
			} catch (NumberFormatException nfe) {
				// Ignore, default value will be returned
			}
		}		
		return defaultValue;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends Enum> T  getEnumValueFromRequestParameter(
			HttpServletRequest request,	final String key, 
			Class<T> type, final T defaultValue) {
		
		final String valueStr = request.getParameter(key);		
		if(null != valueStr) {
			try {
				return (T)Enum.valueOf(type, valueStr.trim().toUpperCase());
			} catch (IllegalArgumentException ie) {
				// Ignore, default value will be returned
			}
		}		
		return defaultValue;
	}
	
	
	protected void writeResponse(HttpServletResponse response, JSONObject output) throws IOException {
		
		String toPrint = (null == output) ? "error" : output.toString();
		
		// Write the output
		//response.setContentType("text/html");	// Used when viewing/debugging in browser
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");	
		response.getWriter().print(toPrint);
	}
}
