package com.levibracken.projects.skidb.server.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.levibracken.projects.skidb.server.data.SkiAreaInMemoryDataSource;

public class StartupServlet extends HttpServlet {

	private static final long serialVersionUID = -8630651888180993220L;

	@Override
    public void init(ServletConfig config) throws ServletException {
		
		super.init(config);
		
		System.out.println(">> StartupServet.init()");
		
		final ServletContext sc = getServletContext();
		SkiAreaInMemoryDataSource.getInstance().initSkiAreaDB(sc);
		
		System.out.println(">> StartupServet.init() -- done");
	}
}
