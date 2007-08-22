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
 * $Id: VarNodeEdgeMultiHashMap.java,v 1.1 2007-08-22 09:19:49 kastenberg Exp $
 */
package groove.nesting;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.GenericNodeEdgeHashMap;
import groove.graph.GenericNodeEdgeMap;
import groove.graph.Label;
import groove.graph.Node;
import groove.rel.RegExprLabel;
import groove.rel.VarNodeEdgeHashMap;
import groove.rel.VarNodeEdgeMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author kramor
 * @version 0.1 $Revision: 1.1 $ $Date: 2007-08-22 09:19:49 $
 */
public class VarNodeEdgeMultiHashMap extends GenericNodeEdgeHashMap<Node, Set<Node>, Edge, Set<Edge>> implements VarNodeEdgeMultiMap {
	
	
    /* (non-Javadoc)
	 * @see groove.nesting.VarNodeEdgeMultiMap#flatten()
	 */
	public VarNodeEdgeMap flatten() {
		VarNodeEdgeMap result = new VarNodeEdgeHashMap();
		for( Map.Entry<Node, Set<Node>> entry : nodeMap().entrySet() ) {
			result.putNode(entry.getKey(), entry.getValue().toArray(new Node[0])[0]);
		}
		for( Map.Entry<Edge, Set<Edge>> entry : edgeMap().entrySet() ) {
			result.putEdge(entry.getKey(), entry.getValue().toArray(new Edge[0])[0]);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see groove.nesting.VarNodeEdgeMultiMap#putAll(groove.rel.VarNodeEdgeMap)
	 */
	public void putAll(VarNodeEdgeMap other) {
		for( Map.Entry<Node, Node> entry : other.nodeMap().entrySet() ) {
			putNode(entry.getKey(), entry.getValue());
		}
		for( Map.Entry<Edge, Edge> entry : other.edgeMap().entrySet() ) {
			putEdge(entry.getKey(), entry.getValue());
		}
		for( Map.Entry<String, Label> entry : other.getValuation().entrySet() ) {
			putVar(entry.getKey(), entry.getValue());
		}
	}

	/* (non-Javadoc)
	 * @see groove.graph.AbstractNodeEdgeMap#putEdge(java.lang.Object, java.lang.Object)
	 */
	public Set<Edge> putEdge(Edge key, Edge layout) {
		if( ! edgeMap().containsKey(key) ) {
			edgeMap().put(key, new HashSet<Edge> ());
		}
		edgeMap().get(key).add(layout);
		return edgeMap().get(key);
	}

	/* (non-Javadoc)
	 * @see groove.graph.AbstractNodeEdgeMap#putNode(java.lang.Object, java.lang.Object)
	 */
	public Set<Node> putNode(Node key, Node layout) {
		if( ! nodeMap().containsKey(key) ) {
			nodeMap().put(key, new HashSet<Node> ());
		}
		nodeMap().get(key).add(layout);
		return nodeMap().get(key);
	}

	/**
     * Creates an empty map with an empty valuation.
     */
    public VarNodeEdgeMultiHashMap() {
        this.valuation = createValuation();
    }

    /**
     * Creates a map filled from a given map.
     */
    public VarNodeEdgeMultiHashMap(VarNodeEdgeMultiMap other) {
    	for( Map.Entry<Node, Set<Node>> entry : other.nodeMap().entrySet() ) {
    		nodeMap().put(entry.getKey(), new HashSet<Node> (entry.getValue()));
    	}
    	for( Map.Entry<Edge, Set<Edge>> entry : other.edgeMap().entrySet() ) {
    		edgeMap().put(entry.getKey(), new HashSet<Edge> (entry.getValue()));
    	}
		this.valuation = createValuation();
        valuation.putAll(other.getValuation());
    }

	/**
	 * Tests if a given element occurs as a key in the node or edge map.
	 * @param elem the element tested for
	 * @return <code>true</code> if <code>elem</code> occurs as a key
	 */
	public boolean containsKey(Element elem) {
		if (elem instanceof Node) {
			return nodeMap().containsKey(elem);
		} else {
			return edgeMap().containsKey(elem);
		}
	}
	
	/**
	 * Tests if a given element occurs as a value in the node or edge map.
	 * @param elem the element tested for
	 * @return <code>true</code> if <code>elem</code> occurs as a value
	 */
	public boolean containsValue(Element elem) {
		if (elem instanceof Node) {
			Collection<Set<Node>> values = nodeMap().values();
			for( Set<Node> lst : values ) {
				if( lst.contains(elem) ) {
					return true;
				}
			}
			return false;
		} else {
			Collection<Set<Edge>> values = edgeMap().values();
			for( Set<Edge> lst : values ) {
				if( lst.contains(elem) ) {
					return true;
				}
			}
			return false;
		}
	}
	
    /**
     * This implementation watches for {@link RegExprLabel}s;
     * such a label is mapped only if it is a named wildcard,
     * otherwise it throws an exception.
     * @return the label itself if it is not a {@link RegExprLabel};'
     * otherwise, the image of label according to the valuation (which may be <code>null</code>).
     * @throws IllegalArgumentException if the label is a regular expression
     * but not a variable
     * @see #getVar(String)
	 */
	public Label getLabel(Label label) {
		if (label instanceof RegExprLabel) {
			String var = RegExprLabel.getWildcardId(label);
			if (var == null) {
				throw new IllegalArgumentException(String.format("Label %s cannot be mapped", label));
			} else {
				return getVar(var);
			}
		} else {
			return label;
		}
	}

	public Map<String, Label> getValuation() {
        return valuation;
    }

    public Label getVar(String var) {
        return valuation.get(var);
    }

    public Label putVar(String var, Label value) {
        return valuation.put(var, value);
    }

    public void putAllVar(Map<String, Label> valuation) {
        this.valuation.putAll(valuation);
    }
    
	/**
	 * Also copies the other's valuation, if any.
	 */
	@Override
	public void putAll(GenericNodeEdgeMap<Node, Set<Node>, Edge, Set<Edge>> other) {
		super.putAll(other);
		if (other instanceof VarNodeEdgeMultiMap) {
			putAllVar(((VarNodeEdgeMultiMap) other).getValuation());
		}
	}
	
	@Override
	public VarNodeEdgeMultiMap clone() {
		return new VarNodeEdgeMultiHashMap(this);
	}

    @Override
	public void clear() {
		super.clear();
		valuation.clear();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof VarNodeEdgeMultiMap && super.equals(obj) && valuation.equals(((VarNodeEdgeMultiMap) obj).getValuation());
	}

	@Override
	public int hashCode() {
		return super.hashCode() + valuation.hashCode();
	}

	@Override
	public String toString() {
		return super.toString() + "\nValuation: "+valuation;
	}

	/**
     * Callback factory method for the valuation mapping.
     * This implementation returns a {@link HashMap}.
     */
    protected Map<String, Label> createValuation() {
        return new HashMap<String, Label>();
    }
    
    /** The internal map from variables to labels. */
    private final Map<String, Label> valuation; 
}
