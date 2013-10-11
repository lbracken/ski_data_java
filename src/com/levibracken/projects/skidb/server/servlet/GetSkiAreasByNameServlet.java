package com.levibracken.projects.skidb.server.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.levibracken.projects.skidb.server.data.SkiAreaDataSource;
import com.levibracken.projects.skidb.server.data.SkiAreaInMemoryDataSource;
import com.levibracken.projects.skidb.server.entity.SkiArea;
import com.levibracken.projects.skidb.server.entity.SkiAreaFormat;
import com.levibracken.projects.skidb.server.entity.SkiAreaOrder;

public class GetSkiAreasByNameServlet extends GetSkiAreasServletBase {

	private static final long serialVersionUID = -7283603633783716264L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
			
		final Date now = new Date(); 
		System.out.println(">> " + now.toString() + "  get_ski_area_by_name -- " + request.getRemoteAddr());
		
		// Get request parameters
		SkiAreaOrder order = getOrder(request);
		SkiAreaFormat format = getFormat(request);
		String skiAreaName = getSkiAreaName(request);
		
		// Get results
		final SkiAreaDataSource ds = SkiAreaInMemoryDataSource.getInstance();
		final JSONObject output = new JSONObject();
		final JSONArray results = new JSONArray();
		
		try {
			output.put("order", order.toString().toLowerCase());
			output.put("format", format.toString().toLowerCase());
			output.put("results", results);
			
			// Check for an empty, or invalid request
			if(null == skiAreaName || skiAreaName.trim().isEmpty()) {
				output.put("msg", "Invalid request. Valid value for 'name' required.");
				
			// Process the request
			} else {				
				List<SkiArea> skiAreas = ds.getSkiAreasByName(skiAreaName, order);

				if(skiAreas.size() == 1) {
					results.put(skiAreas.get(0).format(format));
					
				} else if(skiAreas.isEmpty()) {
					output.put("msgs", "No results found for '" + skiAreaName + "'");

				} else {
					output.put("msgs", "Multiple results found for '" + skiAreaName + "'.  Be more specific.");
				}
			}
			
		} catch (Throwable caught) {
			caught.printStackTrace();
		}
		
		writeResponse(response, output);
		return;
	}
	
	
	private String getSkiAreaName(HttpServletRequest request) {
		String valueStr = request.getParameter("name");	
		return (null == valueStr) ? null : valueStr.trim();
	}
}
