package com.levibracken.projects.skidb.server.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import com.levibracken.projects.skidb.server.entity.SkiArea;
import com.levibracken.projects.skidb.server.entity.SkiAreaComparator;
import com.levibracken.projects.skidb.server.entity.SkiAreaOrder;

public class SkiAreaInMemoryDataSource implements SkiAreaDataSource {
	
	//private static final String SKI_AREA_INPUT_FILE = "/WEB-INF/skiAreaInput.csv";
	private static final String SKI_AREA_INPUT_FILE = "/WEB-INF/skiAreaData.tsv";
	
	private final Map<Long, SkiArea> skiAreaDB = new HashMap<Long, SkiArea>();

	private SkiAreaInMemoryDataSource(){
	}
	
	private static class SingletonHolder { 
        public static final SkiAreaInMemoryDataSource instance = new SkiAreaInMemoryDataSource();
	}
	
	public static SkiAreaInMemoryDataSource getInstance() {
		return SingletonHolder.instance;
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	//     SkiAreaDataSource implementation
	///////////////////////////////////////////////////////////////////////////
	
	@Override
	public int getSkiAreaCount() {
		return skiAreaDB.size();
	}
	
	@Override
	public List<SkiArea> getAllSkiAreas(SkiAreaOrder order) {		
		final List<SkiArea> allSkiAreas = new ArrayList<SkiArea>(skiAreaDB.values());		
		Collections.sort(allSkiAreas, new SkiAreaComparator(order));		
		return allSkiAreas;
	}

	@Override
	public List<SkiArea> getSkiAreaDataPage(int pageSize, int pageNumber, SkiAreaOrder order) {
		
		final List<SkiArea> allSkiAreas = getAllSkiAreas(order);
		
		// There are currently no bounds on the page size.  Zero indicates that
		// all results should be returned.  
		if(pageSize <= 0) {
			return allSkiAreas;
		}
		
		// Check bounds of pageNumber
		if(pageNumber < 0) {
			pageNumber = 0;
		}
		
		// Calculate start/end index
		int startIdx = pageNumber * pageSize;
		int endIdx = startIdx + pageSize;
		
		// Sanity check bounds of start/end index
		if(startIdx < 0) { startIdx = 0; }
		if(endIdx < 0) { endIdx = 0; }
		
		// Check for the conditions that should result in an empty list being returned
		if(startIdx >= allSkiAreas.size() || (startIdx >= endIdx)) {
			return new ArrayList<SkiArea>(0);
		}
		
		// Snap endIdx to the max value
		if(endIdx >= allSkiAreas.size()) {
			endIdx = allSkiAreas.size();
		}
		
		// Return the data page (subset) of results
		return new ArrayList<SkiArea>(allSkiAreas.subList(startIdx, endIdx));		
	}


	@Override
	public List<SkiArea> getSkiAreasById(long[] skiAreaIds, SkiAreaOrder order) {

		final List<SkiArea> skiAreas = new ArrayList<SkiArea>();		
		for(Long skiAreaId : skiAreaIds) {
			if(skiAreaDB.containsKey(skiAreaId)) {
				skiAreas.add(skiAreaDB.get(skiAreaId));
			}
		}
				
		Collections.sort(skiAreas, new SkiAreaComparator(order));
		return skiAreas;		
	}
	


	@Override
	public List<SkiArea> getSkiAreasByName(String name, SkiAreaOrder order) {
		
		name = name.toLowerCase();
		final List<SkiArea> skiAreas = new ArrayList<SkiArea>();
		for(SkiArea skiArea : skiAreaDB.values()) {
			if(skiArea.getName().toLowerCase().contains(name)) {
				skiAreas.add(skiArea);
			}
		}
		
		Collections.sort(skiAreas, new SkiAreaComparator(order));
		return skiAreas;	
	}
	
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	
	// Create and setup the in-memory Ski DB
	public void initSkiAreaDB(ServletContext servletContext) {
		
		System.out.println(">> Starting processing of SkiArea input file...");
		
		final Date start = new Date();
		DataInputStream in = null;
		
		// Clear the SkiAreaDB
		skiAreaDB.clear();
		
		try {			
			String absoluteFilePath = servletContext.getRealPath(SKI_AREA_INPUT_FILE);
			in = new DataInputStream(new FileInputStream(absoluteFilePath));
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));			
			String line = null;
			
			while ((line = reader.readLine()) != null)   {
				
				// Skip blank lines
				if(null == line || line.trim().isEmpty()) {
					continue;
				}
				
				line = line.trim();
				
				// Skip commented lines
				if(line.startsWith("#")) {
					continue;
				}
				
				// Parse line, ignore lines not in proper format
				String[] parsedLine = line.split("\t");
				if(parsedLine.length != 19) {
					continue;
				}
				
				// Isolate bad lines
				try {
					
					// Order of columns in spreadsheet
					// 0	1		2		3		4			5			6		7		8			9		10			11			12			13		14		15		16		17			18     
					// id	name	trails	lifts	vertical	elevation	base	area	snowfall	city	postal code	latitude	longitude	website	wiki	region	country	continent	metric					
					addSkiAreaToDB(new SkiArea(
						getLong(parsedLine[0].trim()),			// Id
						parsedLine[1].trim(),					// Name
						getInteger(parsedLine[2].trim()),		// Trails
						getInteger(parsedLine[3].trim()),		// Lifts
						getDouble(parsedLine[4].trim()),		// Vertical
						getDouble(parsedLine[5].trim()),		// Elevation
						getDouble(parsedLine[6].trim()),		// Base Elevation
						getDouble(parsedLine[7].trim()),		// Area
						getDouble(parsedLine[8].trim()),		// Snowfall
						parsedLine[17].trim(),					// Continent
						parsedLine[16].trim(),					// Country
						parsedLine[15].trim(),					// Region
						parsedLine[9].trim(),					// City
						parsedLine[10].trim(),					// Postal Code
						getDouble(parsedLine[11].trim()),		// Latitude
						getDouble(parsedLine[12].trim()),		// Longitude
						parsedLine[13].trim(),					// Website
						parsedLine[14].trim(),					// Wiki
						getBoolean(parsedLine[18].trim())));	// Metric Values
					
				} catch (Throwable caught) {
					System.out.println("ERROR: Could not parse the following line:");
					System.out.println("    >> " + line);
					caught.printStackTrace();
				}
				
			}			
			
		}  catch (Throwable caught) {
			System.out.println("ERROR: Problem reading input file.");
			caught.toString();
			
		} finally {
			if(null != in) {
				try {
					in.close();
				} catch (IOException ioe) {
					System.out.println("ERROR: Could not close input stream.");
					ioe.printStackTrace();
				}
			}
		}		
		
		long duration = new Date().getTime() - start.getTime();
		System.out.println("... processing complete.  (" + duration + "ms)");
		System.out.println("    " + skiAreaDB.size() + " ski areas in DB");	
	}
	
	private long getLong(String longStr) {
		
		if(null == longStr || longStr.isEmpty())
			return -1;
		
		return Long.parseLong(longStr);		
	}
	
	private int getInteger(String intStr) {
		
		if(null == intStr || intStr.isEmpty())
			return -1;
		
		return Integer.parseInt(intStr);		
	}
	
	private double getDouble(String doubleStr) {
		
		if(null == doubleStr || doubleStr.isEmpty())
			return -1;
		
		return Double.parseDouble(doubleStr);		
	}
	
	private boolean getBoolean(String booleanStr) {
		
		if(null == booleanStr || booleanStr.isEmpty())
			return false;
		
		return Boolean.parseBoolean(booleanStr);
	}
	
	private void addSkiAreaToDB(SkiArea skiArea) {
		if(null == skiArea)
			return;
		
		if(skiAreaDB.containsKey(skiArea.getId())) {
			throw new IllegalArgumentException("Ski Area with id '" +
					skiArea.getId() + "' already exists in Ski Area DB.");
		}
		
		skiAreaDB.put(skiArea.getId(), skiArea);
	}

}
