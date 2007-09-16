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
 * $Id: MergeMap.java,v 1.5 2007-09-16 21:44:23 rensink Exp $
 */
package groove.graph;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Variation on a map that only stores non-identity mappings for nodes; hence
 * anything not explicitly set to a particular value defaults to identity.
 * This is actually not a proper node/edge map, in that the entries do not reflect the actual mapping.
 * @author Arend Rensink
 * @version $Revision: 1.5 $
 */
public class MergeMap extends NodeEdgeHashMap {
    /** Internal representation of undefined. */
    static public final Node UNDEFINED = DefaultNode.createNode(); 
    
    /**
     * Creates a global identity function.
     */
    public MergeMap() {
    	mergeTargets = new HashSet<Node>();
        // empty constructor
    }

    /**
     * Returns <tt>null</tt> if the underlying map contains the special undefined value
     * for the key, and <tt>key</tt> itself if the underlying map contains <tt>null</tt>.
     */
    @Override
    public Node getNode(Node key) {
    	return internalToExternal(super.getNode(key), key);
    }

    /**
     * In this implementation, adding a key-value pair means
     * <i>merging</i> the key and value.
     * If the key and/or value are currently already in the map,
     * their current images undergo the same operation.
     */
    @Override
    public Node putNode(Node key, Node value) {
        // the key-image pair should be put in the merge map,
        // but maybe one of them has been merged with a different node already
    	// or deleted
        Node keyImage = getNode(key);
		Node valueImage = getNode(value);
		if (keyImage != valueImage) {
			if (keyImage == null) {
				// delete the key
				removeNode(valueImage);
			} else if (valueImage == null) {
				// delete the value
				removeNode(keyImage);
			} else {
				// merge key and value
				merge(keyImage, valueImage);
			}
		}
		return keyImage;
	}
    
    /** 
     * This implementation returns the identical edge if
     * the end nodes are also mapped to themselves.
     */
    @Override
	public Edge mapEdge(Edge key) {
    	Map<Node,Node> nodeMap = nodeMap();
    	if (! nodeMap.containsKey(key.source()) && ! nodeMap.containsKey(key.opposite())) {
    		return key;
    	} else {
    		return super.mapEdge(key);
    	}
	}

	/**
     * Merges a given key and image.
     * This means that the key and its current pre-images will be
     * mapped to the image. 
     * @param key the key to be merged; should not be <code>null</code>
     * @param image the merge image; should not be <code>null</code>
     */
    private void merge(Node key, Node image) {
    	assert key != null && image != null: "Merging "+key+" and "+image+" not correct: neither should be null";
		super.putNode(key, image);
		mergeTargets.add(image);
		// now redirect all pre-images of key, if necessary
		if (mergeTargets.contains(key)) {
			// map all pre-images of key to image
			for (Map.Entry<Node, Node> entry : nodeMap().entrySet()) {
				if (entry.getValue() == key) {
					setValue(entry, image);
				}
			}
			mergeTargets.remove(key);
		}
    }
    
    /**
     * Removes the key and its pre-images from the map.
     */
    @Override
    public Node removeNode(Node key) {
    	Node keyImage = getNode(key);
		super.putNode(keyImage, UNDEFINED);
		// now redirect all pre-images of keyImage, if necessary
		if (mergeTargets.contains(keyImage)) {
			// map all pre-images of keyImage to UNDEFINED
			for (Map.Entry<Node, Node> entry : nodeMap().entrySet()) {
				if (entry.getValue() == keyImage) {
					entry.setValue(UNDEFINED);
				}
			}
			mergeTargets.remove(keyImage);
		}
		return keyImage;
    }
    
    /**
     * Inserts a value into an entry, according to the rules of the
     * {@link MergeMap}. That is, the proposed value is converted using
     * {@link #externalToInternal(Node, Node)} with the entry key as first parameter.
     */
    private void setValue(Map.Entry<Node,Node> entry, Node value) {
        entry.setValue(externalToInternal(value, entry.getKey()));
    }
    
    /**
     * Converts a value from the external representation to the internal.
     * If the value equals <tt>null</tt>, the internal value is {@link #UNDEFINED}.
     * If the value equals the key, the internal value is <tt>null</tt>. 
     * Otherwise, the value is unchanged.
     * @param value the value to be converted
     * @param key the corresponding key
     */
    private Node externalToInternal(Node value, Node key) {
        if (value == key) {
            return null;
        } else if (value == null) {
            return UNDEFINED;
        } else {
            return value;
        }        
    }
    
    /**
     * Converts a value from the internal representation to the external.
     * If the value equals {@link #UNDEFINED}, the external value is <tt>null</tt>.
     * If the value equals <tt>null</tt>, the external value is the
     * corresponding key. Otherwise, the value is unchanged.
     * @param value the value to be converted
     * @param key the corresponding key
     */
    private Node internalToExternal(Node value, Node key) {
        if (value == null) {
            return key;
        } else if (value == UNDEFINED) {
            return null;
        } else {
            return value;
        }        
    }
    
    /**
     * Set of nodes to which other nodes are mapped.
     * The merge targets are themselves fixpoints of the merge map.
     */
    private Set<Node> mergeTargets;
}
