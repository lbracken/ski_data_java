package com.levibracken.projects.skidb.server.entity;

import java.util.Comparator;

public class SkiAreaComparator implements Comparator<SkiArea> {
	
	private final SkiAreaOrder order;
	
	public SkiAreaComparator(SkiAreaOrder order) {		
		if(null == order)
			throw new IllegalArgumentException("Order must be non-null");		
		this.order = order;
	}

	@Override
	public int compare(SkiArea o1, SkiArea o2)  {
		
		// Check object identity, and for nulls.
		if(o1 == o2)
			return 0;
		else if (null == o1)
			return -1;
		else if (null == o2)
			return 1;

		switch(order) {			
			case ID:
				return compareNumericValue(o1.getId(), o2.getId());
			case NAME:
				return compareStringSafe(o1.getName(), o2.getName());
			case TRAILS:
				return compareNumericValue(o1.getTrails(), o2.getTrails());
			case LIFTS:
				return compareNumericValue(o1.getLifts(), o2.getLifts());
			case VERTICAL:
				return compareNumericValue(o1.getVertical(), o2.getVertical());
			case ELEVATION:
				return compareNumericValue(o1.getElevation(), o2.getElevation());
			case BASE_ELEVATION:
				return compareNumericValue(o1.getBaseElevation(), o2.getBaseElevation());
			case AREA:
				return compareNumericValue(o1.getArea(), o2.getArea());
			case SNOWFALL:
				return compareNumericValue(o1.getSnowfall(), o2.getSnowfall());
			case CONTINENT:
				return compareStringSafe(o1.getContinent(), o2.getContinent());
			case COUNTRY:
				return compareStringSafe(o1.getCountry(), o2.getCountry());
			case REGION:
				return compareStringSafe(o1.getRegion(), o2.getRegion());
			case CITY:
				return compareStringSafe(o1.getCity(), o2.getCity());
			case POSTAL_CODE:
				return compareStringSafe(o1.getPostalCode(), o2.getPostalCode());
			default: 
				throw new UnsupportedOperationException("Sorting by order " +
						order.toString() + " is not supported.");
		}		
	}
	
	
	private int compareStringSafe(String s1, String s2) {
		
		if(null == s1 && null == s2)
			return 0;
		
		if(null == s1)
			return -1;
		
		if(null == s2)
			return 1;
		
		return s1.compareTo(s2);		
	}
	
	
	private int compareNumericValue(double v1, double v2) {
		
		if(v1 == v2)
			return 0;
		
		return (v1 > v2) ? 1 : 0;
	}
}
