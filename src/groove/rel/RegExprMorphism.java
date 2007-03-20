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
 * $Id: RegExprMorphism.java,v 1.1.1.1 2007-03-20 10:05:24 kastenberg Exp $
 */
package groove.rel;

import groove.graph.DefaultMorphism;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.NodeEdgeMap;
import groove.graph.Simulation;
import groove.rel.match.RegExprMatcher;

import java.util.Map;

/**
 * Implementation of the {@link groove.rel.VarMorphism} interface that
 * implements the required variable mapping through a straightforward 
 * instance variable.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
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

	public Morphism clone() {
        return new RegExprMorphism(this);
    }

    /**
     * This implementation returns a {@link RegExprMorphism}.
     * The first parameter is required to be a {@link VarGraph}.
     */
    public RegExprMorphism createMorphism(Graph dom, Graph cod) {
    	// TODO: should we use the rule-factory to create the morphism?
        return new RegExprMorphism((VarGraph) dom, cod);
    }

    /**
     * This implementation returns a {@link RegExprMorphism}.
     * The simulation is required to be a {@link RegExprSimulation};
     * the variable map is taken from the simulation.
     */
    protected RegExprMorphism createMorphism(final NodeEdgeMap sim) {
        RegExprMorphism result = new RegExprMorphism((VarGraph) dom(), cod()) {
            protected VarNodeEdgeMap createElementMap() {
                return (VarNodeEdgeMap) sim;
            }
        };
//    }
//    protected RegExprMorphism createMorphism(final Simulation sim) {
//    	// TODO: should we use the rule-factory to create the morphism?
//        RegExprMorphism result = new RegExprMorphism((VarGraph) sim.dom(), sim.cod()) {
//            protected VarElementMap createElementMap() {
//                return ((RegExprSimulation) sim).getSingularMap();
//            }
//        };
//        result.setFixed();
        return result;
    }

    /**
     * This implementation returns a {@link RegExprMatcher}.
     */
    protected Simulation createSimulation() {
        return new RegExprMatcher(this);
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
    protected VarNodeEdgeMap createElementMap() {
        return new VarNodeEdgeHashMap();
    }
}