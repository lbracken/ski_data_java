package com.levibracken.projects.skidb.server.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.levibracken.projects.skidb.server.data.SkiAreaDataSource;
import com.levibracken.projects.skidb.server.data.SkiAreaInMemoryDataSource;
import com.levibracken.projects.skidb.server.entity.SkiArea;
import com.levibracken.projects.skidb.server.entity.SkiAreaFormat;
import com.levibracken.projects.skidb.server.entity.SkiAreaOrder;

public class GetSkiAreasByIdServlet extends GetSkiAreasServletBase {

	private static final long serialVersionUID = -2576292191081816760L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
			
		final Date now = new Date(); 
		System.out.println(">> " + now.toString() + "  get_ski_areas_by_id -- " + request.getRemoteAddr());
		
		// Get request parameters
		SkiAreaOrder order = getOrder(request);
		SkiAreaFormat format = getFormat(request);
		long[] skiAreaIds = getSkiAreaIds(request);
		
		// Temp >>>>
		StringBuffer skiAreaIdsToPrint = new StringBuffer();
		for(long skiAreaId : skiAreaIds) {
			skiAreaIdsToPrint.append(skiAreaId + " ");
		}
		System.out.println("     Ids: " + skiAreaIdsToPrint.toString());
		// <<<<
		
		// Get results
		final SkiAreaDataSource ds = SkiAreaInMemoryDataSource.getInstance();
		final JSONObject output = new JSONObject();
		final JSONArray results = new JSONArray();
		
		try {
			output.put("order", order.toString().toLowerCase());
			output.put("format", format.toString().toLowerCase());
			output.put("results", results);
			
			// Check for an empty, or invalid request
			if(skiAreaIds.length == 0) {
				output.put("msg", "Invalid request. Valid value for 'id' required.");
				
			// Process the request
			} else {				
				List<SkiArea> skiAreas = ds.getSkiAreasById(skiAreaIds, order);
				for(SkiArea skiArea : skiAreas) {				
					results.put(skiArea.format(format));
				}
				
				// Confirm that number of ids requested matches count of
				// results being returned.
				if(skiAreas.size() != skiAreaIds.length) {
					output.put("msgs", "One or more ids were invalid.");
				}
			}
			
		} catch (Throwable caught) {
			caught.printStackTrace();
		}
		
		writeResponse(response, output);
		return;
	}
	
	
	private long[] getSkiAreaIds(HttpServletRequest request) {

		String valueStr = request.getParameter("id");		
		if(null != valueStr) {
			
			valueStr = valueStr.trim();
			try {			
				// Parse as JSON Array
				if(valueStr.startsWith("[")) {
					
					JSONArray jsonIdArray = new JSONArray(valueStr);
					long[] idArray = new long[jsonIdArray.length()];	
					Set<Long> idArrayDupCheck = new HashSet<Long>(jsonIdArray.length());
					long id;
					
					for(int ctr=0; ctr < jsonIdArray.length(); ctr++) {
						
						id = jsonIdArray.getLong(ctr);
						if(idArrayDupCheck.contains(id)) {
							continue;
						}
						
						idArray[ctr] = id;
						idArrayDupCheck.add(id);
					}
					return idArray;
				
				// Parse as single id value
				} else {
					return new long[]{Long.parseLong(valueStr)};
				}
				
			} catch (NumberFormatException nfe) {
				// Ignore, empty array will be returned
			} catch (JSONException je) {
				// Ignore, empty array will be returned
			}
		}		
		
		return new long[0];
		
		
	}
}
