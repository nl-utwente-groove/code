/**
 * 
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
 * @version $Revision: 1.1 $
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
	
	/** Indicates if the partition map has non-singleton partitions as values. */
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
	
	/** The actual mapping. */
	private Map<Object,Object> partitionMap = new HashMap<Object,Object>();
	/** Flag indicating if the partition map contains non-singleton images. */
	private boolean oneToOne = true;
}
