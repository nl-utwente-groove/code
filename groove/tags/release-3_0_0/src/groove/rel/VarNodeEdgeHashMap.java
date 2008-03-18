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
 * $Id: VarNodeEdgeHashMap.java,v 1.6 2008-03-18 10:02:04 iovka Exp $
 */
package groove.rel;

import groove.graph.Edge;
import groove.graph.GenericNodeEdgeMap;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the {@link VarNodeEdgeMap} interface where
 * the variable mapping part is given by a separater instance variable.
 * @author Arend Rensink
 * @version $Revision: 1.6 $
 */
public class VarNodeEdgeHashMap extends NodeEdgeHashMap implements VarNodeEdgeMap {
    /**
     * Creates an empty map with an empty valuation.
     */
    public VarNodeEdgeHashMap() {
        this.valuation = createValuation();
    }

    /**
     * Creates a map filled from a given map.
     */
    public VarNodeEdgeHashMap(NodeEdgeMap map) {
        super(map);
        this.valuation = createValuation();
        if (map instanceof VarNodeEdgeMap) {
            valuation.putAll(((VarNodeEdgeMap) map).getValuation());
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
	@Override
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
	public void putAll(GenericNodeEdgeMap<Node, Node, Edge, Edge> other) {
		super.putAll(other);
		if (other instanceof VarNodeEdgeMap) {
			putAllVar(((VarNodeEdgeMap) other).getValuation());
		}
	}

	/**
	 * This implementation returns a {@link VarNodeEdgeHashMap}.}
	 */
	@Override
    public VarNodeEdgeMap clone() {
    	return new VarNodeEdgeHashMap(this);
    }

    @Override
	public void clear() {
		super.clear();
		valuation.clear();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof VarNodeEdgeMap && super.equals(obj) && valuation.equals(((VarNodeEdgeMap) obj).getValuation());
	}

	@Override
	public int hashCode() {
		return super.hashCode() + valuation.hashCode();
	}

	@Override
	public String toString() {
		return super.toString() + " Valuation: "+valuation;
	}

	/**
     * Callback factory method for the valuation mapping.
     * This implementation returns a {@link HashMap}.
     */
    protected Map<String, Label> createValuation() {
        return new HashMap<String, Label>();
    }
    
    /** The internal map from variables to labels. */
    private final Map<String,Label> valuation; 
}
