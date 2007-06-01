// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: RegExprMorphism.java,v 1.5 2007-06-01 18:04:13 rensink Exp $
 */
package groove.rel;

import groove.graph.DefaultMorphism;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.NodeEdgeMap;
import groove.graph.match.Matcher;
import groove.rel.match.RegExprMatcher;

import java.util.Map;

/**
 * Implementation of the {@link groove.rel.VarMorphism} interface that
 * implements the required variable by putting it into the element map.
 * @author Arend Rensink
 * @version $Revision: 1.5 $
 */
public class RegExprMorphism extends DefaultMorphism implements VarMorphism {
    /**
     * Creates an initially empty morphism between two given graphs.
     */
    public RegExprMorphism(VarGraph dom, Graph cod) {
        super(dom, cod);
    }

    /**
     * Creates a morphism by copying an existing one.
     * Also copies the valuation of the other morphism, if that is a {@link VarMorphism}.
     */
    protected RegExprMorphism(RegExprMorphism morph) {
        super(morph);
        putAllVar(((VarMorphism) morph).getValuation());
    }

    @Override
	public VarNodeEdgeMap elementMap() {
		return (VarNodeEdgeMap) super.elementMap();
	}

    /**
     * In addition to invoking the <code>super</code> method,
     * registers the valuation if <code>key</code> binds variables.
     */
    @Override
    public Edge putEdge(Edge key, Edge value) {
    	String var = RegExprLabel.getWildcardId(key.label());
    	if (var != null) {
            putVar(var, value.label());
        } else if (value instanceof ValuationEdge) {
        	putAllVar(((ValuationEdge) value).getValue());
        }
        return super.putEdge(key, value);
    }

    @Override
	public Morphism clone() {
        return new RegExprMorphism(this);
    }

    /**
     * This implementation returns a {@link RegExprMorphism}.
     * The first parameter is required to be a {@link VarGraph}.
     */
    @Override
    public RegExprMorphism createMorphism(Graph dom, Graph cod) {
        return new RegExprMorphism((VarGraph) dom, cod);
    }

    /**
     * This implementation returns a {@link RegExprMorphism}.
     * The simulation is required to be a {@link RegExprMatcher};
     * the variable map is taken from the simulation.
     */
    @Override
    protected Morphism createMorphism(final NodeEdgeMap sim) {
        RegExprMorphism result = new RegExprMorphism((VarGraph) dom(), cod()) {
            @Override
            protected VarNodeEdgeMap createElementMap() {
                return (VarNodeEdgeMap) sim;
            }
        };
        return result;
    }

    /**
     * This implementation returns a {@link RegExprMatcher}.
     */
    @Override
    protected Matcher createMatcher() {
        return new RegExprMatcher(this, false);
    }
    
    /**
     * Delegates the method to the underlying element map
     * (which is known to be a {@link VarNodeEdgeMap}). 
     */
    public Map<String, Label> getValuation() {
        return elementMap().getValuation();
    }

    /**
     * Delegates the method to the underlying element map
     * (which is known to be a {@link VarNodeEdgeMap}). 
     */
    public Label getVar(String var) {
        return elementMap().getVar(var);
    }

    /**
     * Delegates the method to the underlying element map
     * (which is known to be a {@link VarNodeEdgeMap}). 
     */
    public Label putVar(String var, Label value) {
        return elementMap().putVar(var, value);
    }

    /**
     * Delegates the method to the underlying element map
     * (which is known to be a {@link VarNodeEdgeMap}). 
     */
    public void putAllVar(Map<String, Label> valuation) {
        elementMap().putAllVar(valuation);
    }
    
    /**
     * This implementation returns a {@link VarNodeEdgeHashMap}.
     */
    @Override
    protected VarNodeEdgeMap createElementMap() {
        return new VarNodeEdgeHashMap();
    }
}