package com.levibracken.projects.skidb.server.data;

import java.util.List;

import com.levibracken.projects.skidb.server.entity.SkiArea;
import com.levibracken.projects.skidb.server.entity.SkiAreaOrder;

public interface SkiAreaDataSource {
	
	/**
	 * @return the total count of ski areas in the DB
	 */
	int getSkiAreaCount();

	/**
	 * @param order how results should be sorted
	 * @return
	 */
	List<SkiArea> getAllSkiAreas(SkiAreaOrder order);	
	
	/**
	 * @param pageSize size of page to return
	 * @param pageNumber page number to return from total set
	 * @param order how results should be sorted
	 * @return a data page of ski areas
	 */
	List<SkiArea> getSkiAreaDataPage(int pageSize, int pageNumber, SkiAreaOrder order);

	/**
	 * @param skiAreaIds skiAreaIds ids of ski areas to return
	 * @param order how results should be sorted
	 * @return ski areas matching given ids
	 */
	List<SkiArea> getSkiAreasById(long[] skiAreaIds, SkiAreaOrder order);
	
	/** 
	 * !! Temp only !! -- gets list of SkiAreas that match the given name
	 * This will not execute very fast -- it requires a scan of all skiAreas
	 * 
	 * @param name name string to try an match
	 * @param order how results should be sorted
	 * @return ski areas matching given name
	 */
	@Deprecated
	List<SkiArea> getSkiAreasByName(String name, SkiAreaOrder order);
	
}
