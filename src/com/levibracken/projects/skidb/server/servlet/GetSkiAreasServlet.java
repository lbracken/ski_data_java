package com.levibracken.projects.skidb.server.servlet;

import java.io.IOException;
import java.util.Date;

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

public class GetSkiAreasServlet extends GetSkiAreasServletBase {

	private static final long serialVersionUID = 8710474021629946150L;
	
	private JSONObject autoCompleteResponse = null;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
			
		final Date now = new Date(); 
		System.out.println(">> " + now.toString() + "  get_ski_areas -- " + request.getRemoteAddr());
		
		// Get request parameters
		int count = getCount(request);
		int page = getPage(request);
		SkiAreaOrder order = getOrder(request);
		SkiAreaFormat format = getFormat(request);
		
		// Get results
		SkiAreaDataSource ds = SkiAreaInMemoryDataSource.getInstance();
		JSONArray results = new JSONArray();
		JSONObject output = null;
		
		try {
			// Check if this request is for AutoComplete.  It's popular enough
			// that it should be cached.
			boolean isAutoCompleteRequest = isAutoCompleteRequest(count, page, order, format);			
			if(isAutoCompleteRequest && null != autoCompleteResponse) {
				output = autoCompleteResponse;
				
			} else {		
				output = new JSONObject();
				output.put("count", count);
				output.put("page", page);
				output.put("order", order.toString().toLowerCase());
				output.put("format", format.toString().toLowerCase());
				output.put("total_count", ds.getSkiAreaCount());
				output.put("results", results);
				
				for(SkiArea skiArea : ds.getSkiAreaDataPage(count, page, order)) {				
					results.put(skiArea.format(format));
				}
				
				// Cache AutoComplete Response
				if(isAutoCompleteRequest) {
					autoCompleteResponse = output;
				}
			}
			
		} catch (Throwable caught) {
			System.out.println(">> Error processing get_ski_areas:" +
					" Count:" + count + " Page:" + page + 
					" Order" + order + " Format:" + format);
			caught.printStackTrace();
		}
		
		writeResponse(response, output);
		return;
	}

	public boolean isAutoCompleteRequest(int count, int page,
			SkiAreaOrder order, SkiAreaFormat format) {		
		return (0 == count) && (0 == page) && 
				SkiAreaOrder.NAME == order &&
				SkiAreaFormat.LONG == format;
	}
}
