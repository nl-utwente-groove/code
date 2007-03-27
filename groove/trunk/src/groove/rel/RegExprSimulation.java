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
 * $Id: RegExprSimulation.java,v 1.2 2007-03-27 14:18:36 rensink Exp $
 */
package groove.rel;

import groove.graph.DefaultSimulation;
import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.util.FilterIterator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Simulation from a {@link groove.rel.VarGraph} in a {@link groove.graph.Graph}. 
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public class RegExprSimulation extends DefaultSimulation {
	/**
	 * Class that delegates the {@link VarMap}-functionality to 
	 * its enclosing {@link RegExprSimulation}.
	 */
    protected class MyVarNodeEdgeMap extends MyNodeEdgeMap implements VarNodeEdgeMap {
        /**
         * Returns the valuation from the enclosing {@link RegExprSimulation}.
         * @see RegExprSimulation#getValuation()
         */
        public Map<String, Label> getValuation() {
            return RegExprSimulation.this.getValuation();
        }

        /**
         * Looks up the label in the valuation of the enclosing {@link RegExprSimulation}.
         * @see RegExprSimulation#getVar(String)
         */
        public Label getVar(String var) {
            return RegExprSimulation.this.getVar(var);
        }

        /**
         * Puts the label into the valuation of the enclosing {@link RegExprSimulation}.
         * @see RegExprSimulation#putVar(String,Label)
         */
        public Label putVar(String var, Label value) {
            return RegExprSimulation.this.putVar(var, value);
        }

        /**
         * Puts the mapping into the valuation of the enclosing {@link RegExprSimulation}.
         * @see RegExprSimulation#putAllVar(Map)
         */
        public void putAllVar(Map<String, Label> valuation) {
            RegExprSimulation.this.putAllVar(valuation);
        }

        public MyVarNodeEdgeMap clone() {
        	return new MyVarNodeEdgeMap();
        }
    }
    
    /**
     * Creates a simulation on the basis of a given regular expression morphism.
     */
    public RegExprSimulation(VarMorphism mapping) {
        super(mapping);
        try {
            // possibly the image initialization already created some inconsistent variable matchings
            putAllVar(mapping.getValuation());
        } catch (IllegalStateException exc) {
            notifyInconsistent();
        }
    }

    /**
     * Returns the (partial) variable map in this simulation.
     * The map goes from the variables in the domain graph to the labels
     * of the codomain graph. 
     */
    public Map<String, Label> getValuation() {
    	if (valuation == null) {
    		valuation = createValuation();
    	}
    	return valuation;
    }
    
    /**
     * Callback factory method to create a valuation map.
     * @return a fresh, empty valuation map.
     */
    protected Map<String,Label> createValuation() {
    	return new HashMap<String,Label>();
    }
    
    /**
     * Returns the value of a given variable, according to
     * the valuation in this simulation.
     */
    public Label getVar(String var) {
        return getValuation().get(var);
    }

    /**
     * Inserts a value for a variable into the valuation map.
     * If the variable already had a valuation, differing from the new one,
     * an {@link IllegalStateException} is thrown.
     * @see #getVar(String)
     */
    public Label putVar(String var, Label value) {
        Label oldImage = getValuation().put(var, value);
        if (backupValuation != null) {
            backupValuation.put(var, oldImage);
        }
        if (oldImage != null && !value.equals(oldImage)) {
            throw new IllegalStateException();
        }
        return oldImage;
    }

    /**
     * Copies a given valuation mapping to the valuation in this simulation.
     * Iterates over <code>valuation.entrySet()</code> and invokes {@link #putVar(String, Label)}
     * for each entry.
     */
    public void putAllVar(Map<String, Label> valuation) {
    	for (Map.Entry<String,Label> idEntry: valuation.entrySet()) {
            putVar(idEntry.getKey(), idEntry.getValue());
        }
    }

    /**
     * This implementation returns a {@link VarNodeEdgeMap} that also includes the 
     * valuation of the simulation.
     */
    public VarNodeEdgeMap getSingularMap() {
        return new MyVarNodeEdgeMap();
    }
    
    /**
     * In addition to invoking the <code>super</code> method, also
     * clones the wildcard identity map.
     */
    public RegExprSimulation clone() {
        RegExprSimulation result = (RegExprSimulation) super.clone();
        result.valuation = createValuation();
        result.valuation.putAll(getValuation());
        return result;
    }

    /**
     * Returns the set of composite elements matching a given domain edge.
     * @param key the edge on whose label the match should be based; may be a <code>DefaultLabel</code>
     * or a <code>RegExprLabel</code>
     * @return the set of edges (out of the values of <code>codLabelEdgeMap</code>)
     * that match <code>label</code>
     */
    protected Iterator<? extends Edge> getEdgeMatches(Edge key) {
        Edge edgeKey = key;
        if (edgeKey instanceof VarEdge) {
            return getVarEdgeMatches((VarEdge) edgeKey);
        } else if (edgeKey.label() instanceof RegExprLabel) {
            return getRegExprMatches(edgeKey);
        } else {
            return super.getEdgeMatches(key);
        }
    }

    /**
     * Returns the elements of the codomain matching a given edge wrapping a regular expression.
     */
    protected Iterator<? extends Edge> getRegExprMatches(Edge edgeKey) {
        RegExprLabel label = (RegExprLabel) edgeKey.label();
        Automaton labelAutomaton = label.getAutomaton();
        NodeRelation matches;
        if (labelAutomaton instanceof VarAutomaton) {
            matches = ((VarAutomaton) labelAutomaton).getMatches(cod(), getNode(edgeKey.source()), getNode(edgeKey.opposite()), getValuation());            
        } else {
            matches = labelAutomaton.getMatches(cod(), getNode(edgeKey.source()), getNode(edgeKey.opposite()));
        }
        return filterEnds(matches.getAllRelated().iterator(), edgeKey);
    }

    /**
     * Returns the elements of the codomain matching a given variable edge
     */
    protected Iterator<? extends Edge> getVarEdgeMatches(VarEdge edgeKey) {
        final int arity = edgeKey.endCount();
        Label varImage = getVar(edgeKey.var());
        Iterator<? extends Edge> labelEdgeIter;
        if (varImage != null) {
            labelEdgeIter = cod().labelEdgeSet(arity, varImage).iterator();
        } else {
            labelEdgeIter = new FilterIterator<Edge>(cod().edgeSet().iterator()) {
                /** Only allows the edges with the correct end count. */
                protected boolean approves(Object obj) {
                    return ((Edge) obj).endCount() == arity;
                }
            };
        }
        return filterEnds(labelEdgeIter, edgeKey);
    }

    /**
     * If the changed image set is singular,
     * registers any variable mappings that can be derived from it.
     * Then invokes the <code>super</code> method.
     */
    protected void notifyEdgeChange(ImageSet<Edge> changed, Node trigger) {
        if (changed.isSingular()) {
        	Edge image = changed.getSingular();
            Label imageLabel = image.label();
            if (changed.getKey() instanceof VarEdge) {
                putVar(((VarEdge) changed.getKey()).var(), imageLabel);
            } else if (image instanceof ValuationEdge) {
                putAllVar(((ValuationEdge) image).getValue());
            }
        }
        super.notifyEdgeChange(changed, trigger);
    }
    
    /**
     * In addition to calling the <code>super</code> method, also backs up the variable map.
     * @see #getValuation()
     */
    protected void backup() {
        backupValuation = new HashMap<String,Label>();
        super.backup();
    }

    /**
     * In addition to calling the <code>super</code> method, also restores
     * the variable map.
     */
    protected void restore() {
        getValuation().putAll(backupValuation);
        super.restore();
    }

    protected NodeRelation getRelationFactory() {
        if (factory == null) {
            factory = new SetNodeRelation(morph.cod());
        }
        return factory;
    }
    
    /**
     * Returns the internal relation calculator.
     */
    protected RelationCalculator getRelationCalculator() {
        if (calculator == null) {
            calculator = new RelationCalculator(getRelationFactory());
        }
        return calculator;
    }
    
    /**
     * The internal relation factory.
     * Initialized lazily in {@link #getRelationFactory()}.
     */
    private NodeRelation factory;
    
    /**
     * The internal relation calculator.
     * Initialized lazily in {@link #getRelationCalculator()}.
     */
    private RelationCalculator calculator;
    /**
     * Mapping from (wildcard) identitiers to labels, discovered during this simulation.
     */
    private Map<String,Label> valuation;
    /**
     * Mapping from (wildcard) identitiers to labels, discovered during this simulation.
     */
    private Map<String,Label> backupValuation = new HashMap<String,Label>();
}