/*
 * Groove Prolog Interface
 * Copyright (C) 2009 Michiel Hendriks, University of Twente
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package groove.prolog;


import java.util.Map;

/**
 * The result of a prolog query
 * 
 * @author Michiel Hendriks
 */
public interface QueryResult
{
	/**
	 * @return The query string
	 */
	String getQuery();

	/**
	 * @return The return value of the query
	 */
	QueryReturnValue getReturnValue();

	/**
	 * @return nano seconds the query took to execute
	 */
	long getExecutionTime();

	/**
	 * @return The previous result, can be null if this is the first result
	 */
	QueryResult getPreviousResult();

	/**
	 * @return The next result, will be null if this is the last result, or if
	 *         the next result has not be calculated
	 */
	QueryResult getNextResult();

	/**
	 * @return True if this is the last result in the list
	 */
	boolean isLastResult();

	/**
	 * @return the map of variables that were resolved in the query. When the
	 *         query failed this will be an empty list.
	 */
	Map<String, Object> getVariables();
}
