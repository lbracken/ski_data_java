package com.levibracken.projects.skidb.server.servlet;

import java.io.IOException;
import java.util.ArrayList;
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

/* TEMP CLASS */
public class GetUSSkiAreaLocationsServlet extends GetSkiAreasServletBase {

	private static final long serialVersionUID = -6497924593915626223L;
	
	private JSONObject cachedResponse = null;
	
	private double verticalMax = 1;
	private double verticalMin = 10000;
	private double areaMax = 1;
	private double areaMin = 10000;
	private double elevationMax = 1;
	private double elevationMin = 10000;
	private double snowfallMax = 0;
	private double snowfallMin = 10000;
	private double trailsMax = 0;
	private double trailsMin = 10000;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
			
		final Date now = new Date(); 
		System.out.println(">> " + now.toString() + "  get_us_ski_areas_location_data -- " + request.getRemoteAddr());
		
		// Ignore any request parameters
		
		// Get results		
		JSONArray results = new JSONArray();
		JSONObject output = null;
		
		try {		
			if(null != cachedResponse) {
				output = cachedResponse;
				
			} else {		
				output = new JSONObject();


				for(SkiArea skiArea : getUSSkiAreas()) {				
					results.put(skiArea.format(SkiAreaFormat.US_LOCATION));
				}
				
				output.put("results", results);
				output.put("vertical_min", verticalMin);
				output.put("vertical_max", verticalMax);
				output.put("area_min", areaMin);
				output.put("area_max", areaMax);
				output.put("elevation_min", elevationMin);
				output.put("elevation_max", elevationMax);
				output.put("snowfall_min", snowfallMin);
				output.put("snowfall_max", snowfallMax);
				output.put("trails_min", trailsMin);
				output.put("trails_max", trailsMax);
				
				// Cache Response for next time
				cachedResponse = output;
			}
			
		} catch (Throwable caught) {
			System.out.println(">> Error processing get_us_ski_areas_location_data.");
			caught.printStackTrace();
		}
		
		writeResponse(response, output);
		return;
	}
	
	private List<SkiArea> getUSSkiAreas() {
		
		List<SkiArea> usSkiAreas = new ArrayList<SkiArea>();
		SkiAreaDataSource ds = SkiAreaInMemoryDataSource.getInstance();
		
		for(SkiArea skiArea : ds.getAllSkiAreas(SkiAreaOrder.ID)) {
			
			// Only add us ski areas
			if("USA".equals(skiArea.getCountry())) {
				usSkiAreas.add(skiArea);
					
				if(skiArea.getVertical() > verticalMax)
					verticalMax = skiArea.getVertical();
				if(skiArea.getVertical() < verticalMin)
					verticalMin = skiArea.getVertical();
				
				if(skiArea.getArea() > areaMax)
					areaMax = skiArea.getArea();
				if(skiArea.getArea() < areaMin)
					areaMin = skiArea.getArea();				

				if(skiArea.getElevation() > elevationMax)
					elevationMax = skiArea.getElevation();
				if(skiArea.getElevation() < elevationMin)
					elevationMin = skiArea.getElevation();
				
				if(skiArea.getSnowfall() > snowfallMax)
					snowfallMax = skiArea.getSnowfall();
				// Snowfall min is 0 since some areas don't have snowfall data
				snowfallMin = 0;
				//if(skiArea.getSnowfall() < snowfallMin)
				//	snowfallMin = skiArea.getSnowfall();
				
				if(skiArea.getTrails() > trailsMax)
					trailsMax = skiArea.getTrails();
				if(skiArea.getTrails() < trailsMin)
					trailsMin = skiArea.getTrails();
			}			
		}
		
		return usSkiAreas;
	}
}
