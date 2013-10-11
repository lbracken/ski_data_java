package com.levibracken.projects.skidb.server.entity;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents and contains information about a SkiArea
 * @author mbracken
 */
public class SkiArea implements Serializable {

	private static final long serialVersionUID = -8406815361828476654L;
	
	// Basic info
	private long id;	
	private String name;
	
	// Mountain Statistics
	private int trails;
	private int lifts;
	private double vertical;		// In meters
	private double elevation;		// In meters
	private double baseElevation;	// In meters
	private double area;			// In acres
	private double snowfall;		// In meters
	
	// Location Data
	private String continent;
	private String country;
	private String region;
	private String city;
	private String postalCode;
	private double latitude;
	private double longitude;
	
	// Misc information
	private String website;
	private String wiki;
	
	
	public SkiArea(long id, String name) {		
		this.id = id;
		this.name = name;
	}	

	public SkiArea(long id, String name, int trails, int lifts, double vertical,
			double elevation, double baseElevation, double area, double snowfall,
			String continent, String country, String region, String city,
			String postalCode, double latitude, double longitude,
			String website, String wiki, boolean metricValues) {
		
		this.id = id;
		this.name = name;
		
		this.trails = trails;
		this.lifts = lifts;
		this.vertical = vertical;
		this.elevation = elevation;
		this.baseElevation = baseElevation;		
		this.area = area;
		this.snowfall = snowfall;		
		
		this.continent = continent;
		this.country = country;
		this.region = region;
		this.city = city;
		this.postalCode = postalCode;
		this.latitude = latitude;
		this.longitude = longitude;
		this.website = website;
		this.wiki = wiki;
		
		
		// TEMP -- Added on 1/2/12 to handle convert metric data to US until both are supported...
		if(metricValues) {			
			this.vertical = (int)(vertical * 3.2808399);	// Meter to Foot		
			this.elevation = (int)(elevation * 3.2808399);	// Meter to Foot		
			this.baseElevation = (int)(baseElevation * 3.2808399);	// Meter to Foot			
			this.snowfall = (int)(snowfall * 0.393700787);		// cm to inch
		}		
	}

	public double getVertical() {
		return vertical;
	}
	
	public void setVertical(double vertical) {
		this.vertical = vertical;
	}
	
	public double getElevation() {
		return elevation;
	}
	
	public void setElevation(double elevation) {
		this.elevation = elevation;
	}
	
	public double getBaseElevation() {
		return baseElevation;
	}
	
	public void setBaseElevation(double baseElevation) {
		this.baseElevation = baseElevation;
	}
	
	public double getArea() {
		return area;
	}
	
	public void setArea(double area) {
		this.area = area;
	}
	
	public int getTrails() {
		return trails;
	}

	public void setTrails(int trails) {
		this.trails = trails;
	}

	public int getLifts() {
		return lifts;
	}

	public void setLifts(int lifts) {
		this.lifts = lifts;
	}

	public double getSnowfall() {
		return snowfall;
	}

	public void setSnowfall(double snowfall) {
		this.snowfall = snowfall;
	}

	public String getContinent() {
		return continent;
	}
	
	public void setContinent(String continent) {
		this.continent = continent;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getRegion() {
		return region;
	}
	
	public void setRegion(String region) {
		this.region = region;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getPostalCode() {
		return postalCode;
	}
	
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public String getWebsite() {
		return website;
	}
	
	public void setWebsite(String website) {
		this.website = website;
	}
	
	public String getWiki() {
		return wiki;
	}
	
	public void setWiki(String wiki) {
		this.wiki = wiki;
	}
	
	public long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}	
	
	public JSONObject format(SkiAreaFormat format) throws JSONException {
		
		if(null == format) {
			throw new IllegalArgumentException("Format must be non-null.");
		}
		
		final JSONObject skiAreaJSON = new JSONObject();
		skiAreaJSON.put("id", id);
		skiAreaJSON.put("name", name);
		
		switch(format) {
			
			case SHORT:
				return skiAreaJSON;
				
			case LONG:
				skiAreaJSON.put("trails", trails);
				skiAreaJSON.put("lifts", lifts);
				skiAreaJSON.put("vertical", vertical);
				skiAreaJSON.put("elevation", elevation);
				skiAreaJSON.put("baseElevation", baseElevation);
				skiAreaJSON.put("area", area);
				skiAreaJSON.put("snowfall", snowfall);
				skiAreaJSON.put("continent", continent);
				skiAreaJSON.put("country", country);
				skiAreaJSON.put("region", region);
				skiAreaJSON.put("city", city);
				skiAreaJSON.put("postalCode", postalCode);
				skiAreaJSON.put("latitude", latitude);
				skiAreaJSON.put("longitude", longitude);
				skiAreaJSON.put("website", website);
				skiAreaJSON.put("wiki", wiki);
				return skiAreaJSON;
				
				
			case US_LOCATION:
				skiAreaJSON.put("area", area);
				skiAreaJSON.put("vertical", vertical);
				skiAreaJSON.put("elevation", elevation);
				skiAreaJSON.put("snowfall", snowfall);
				skiAreaJSON.put("trails", trails);
				
				final JSONArray coordinates = new JSONArray();
				coordinates.put(longitude);
				coordinates.put(latitude);				
				skiAreaJSON.put("coordinates", coordinates);				
				return skiAreaJSON;								
				
			case AUTOCOMPLETE:				
				// The label field is what gets indexed by autocomplete, so 
				// include the name, region, country and continent so they
				// can all be searched for.
				skiAreaJSON.put("desc", getLocationDescription());
				skiAreaJSON.put("label", name + ", " + region + ", " + country + ", " + continent);
				return skiAreaJSON;
				
			default:
				throw new UnsupportedOperationException("Unsupported format: " + format.toString());
		}
	}
	
	public String getLocationDescription() {

		boolean hasRegion = hasValue(getRegion());
		boolean hasCountry = hasValue(getCountry());
		
		if(hasRegion && hasCountry) {	
			return getRegion() + ", " + getCountry();
		}
		
		if(hasCountry) {
			return getCountry();
		}
		
		if(hasRegion) {
			return getRegion();
		}
		
		return "";
	}

	private boolean hasValue(String value) {
		return (null == value || value.trim().isEmpty()) ? false : true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SkiArea other = (SkiArea) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
