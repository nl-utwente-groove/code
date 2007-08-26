/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: PartitionMap.java,v 1.4 2007-08-26 07:23:11 rensink Exp $
 */
package groove.graph.iso;

import groove.graph.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping from certificate values to sets of graph elements having those certificates.
 * For efficiency, singular image sets are stored as single objects. 
 * @author Arend Rensink
 * @version $Revision: 1.4 $
 */
public class PartitionMap {
	/** Adds a pair of certificate and graph element to the partition map. */
	public void add(Object certificate, Element elem) {
	    // retrieve the image of the certificate, if any
	    Object oldPartition = partitionMap.get(certificate);
	    if (oldPartition == null) {
	        // no, the certificate did not yet exist; create an entry for it
	    	partitionMap.put(certificate, elem);
	    } else if (oldPartition instanceof Collection) {
	        ((Collection<Element>) oldPartition).add(elem);
	    } else {
	        Collection<Element> partitionSet = new ArrayList<Element>();
	        partitionSet.add((Element) oldPartition);
	        partitionSet.add(elem);
	        partitionMap.put(certificate, partitionSet);
	        oneToOne = false;
	    }
	}
	
	/** Indicates if the partition map has only singleton partitions as values. */
	public boolean isOneToOne() {
		return oneToOne;
	}
	
	/** Retrieves the partition for a given certificate value.
	 * The partition can be a single {@link Element} or a {@link Collection} of elements.
	 * @param certificate the value for which we want the partition.
	 * @return an object of type {@link Element} or type {@link Collection}, or <code>null</code>
	 */
	public Object get(Object certificate) {
		return partitionMap.get(certificate);
	}
	
	/** Number of certificates in the map. */
	public int size() {
		return partitionMap.size();
	}
	
	/**
	 * Returns the string description of the internal partition map.
	 */
	@Override
	public String toString() {
		return partitionMap.toString();
	}

	/** The actual mapping. */
	private Map<Object,Object> partitionMap = new HashMap<Object,Object>();
	/** Flag indicating if the partition map contains non-singleton images. */
	private boolean oneToOne = true;
}
