package com.levibracken.projects.skidb.server.filter;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * See: http://code.google.com/speed/articles/caching.html
 * 
 * Controlled by configuration setup in web.xml
 */
public class CacheFilter implements Filter {
	
	private static final String HEADER_CACHE_CTRL = "Cache-Control";

	private FilterConfig filterConfig;

	@Override
 	public void doFilter( ServletRequest request, ServletResponse response, 
 			FilterChain filterChain) throws IOException, ServletException {
 		
 		HttpServletResponse httpResponse = (HttpServletResponse) response;      
 		String cacheCtrlValue = filterConfig.getInitParameter(HEADER_CACHE_CTRL); 		
 		httpResponse.setHeader(HEADER_CACHE_CTRL, cacheCtrlValue);
 		
 		filterChain.doFilter(request, httpResponse);
 	}

 	@Override
 	public void init(FilterConfig filterConfig) throws ServletException {
 		this.filterConfig = filterConfig;
 	}

 	@Override
 	public void destroy() {
 		this.filterConfig = null;
 	}
}