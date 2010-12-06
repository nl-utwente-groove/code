/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: VarNodeEdgeHashMap.java,v 1.6 2008-03-18 10:02:04 iovka Exp $
 */
package groove.rel;

import groove.graph.DefaultEdge;
import groove.graph.Edge;
import groove.graph.GenericNodeEdgeMap;
import groove.graph.GraphHashMap;
import groove.graph.Label;
import groove.graph.Node;
import groove.trans.RuleEdge;
import groove.trans.RuleNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the {@link RuleToStateMap} interface where the variable
 * mapping part is given by a separate instance variable.
 * @author Arend Rensink
 * @version $Revision$
 */
public class RuleToStateHashMap extends
        GraphHashMap<RuleNode,Node,RuleEdge,Edge> implements
        RuleToStateMap {
    /**
     * Creates an empty map with an empty valuation.
     */
    public RuleToStateHashMap() {
        this.valuation = createValuation();
    }

    /**
     * Creates a map filled from a given map.
     */
    public RuleToStateHashMap(RuleToStateMap map) {
        nodeMap().putAll(map.nodeMap());
        edgeMap().putAll(map.edgeMap());
        this.valuation = createValuation();
        this.valuation.putAll(map.getValuation());
    }

    /**
     * This implementation watches for {@link RegExprLabel}s; such a label is
     * mapped only if it is a named wildcard, otherwise it throws an exception.
     * @return the label itself if it is not a {@link RegExprLabel};'
     *         otherwise, the image of label according to the valuation (which
     *         may be <code>null</code>).
     * @throws IllegalArgumentException if the label is a regular expression but
     *         not a variable
     * @see #getVar(LabelVar)
     */
    @Override
    public Label getLabel(Label label) {
        if (label instanceof RegExprLabel) {
            LabelVar var = RegExprLabel.getWildcardId(label);
            if (var == null) {
                throw new IllegalArgumentException(String.format(
                    "Label %s cannot be mapped", label));
            } else {
                return getVar(var);
            }
        } else {
            return label;
        }
    }

    public Map<LabelVar,Label> getValuation() {
        return this.valuation;
    }

    public Label getVar(LabelVar var) {
        return this.valuation.get(var);
    }

    public Label putVar(LabelVar var, Label value) {
        return this.valuation.put(var, value);
    }

    public void putAllVar(Map<LabelVar,Label> valuation) {
        this.valuation.putAll(valuation);
    }

    /**
     * Also copies the other's valuation, if any.
     */
    @Override
    public void putAll(GenericNodeEdgeMap<RuleNode,Node,RuleEdge,Edge> other) {
        super.putAll(other);
        if (other instanceof RuleToStateMap) {
            putAllVar(((RuleToStateMap) other).getValuation());
        }
    }

    /**
     * This implementation returns a {@link RuleToStateHashMap}.}
     */
    @Override
    public RuleToStateMap clone() {
        return new RuleToStateHashMap(this);
    }

    @Override
    public void clear() {
        super.clear();
        this.valuation.clear();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RuleToStateMap && super.equals(obj)
            && this.valuation.equals(((RuleToStateMap) obj).getValuation());
    }

    @Override
    public int hashCode() {
        return super.hashCode() + this.valuation.hashCode();
    }

    @Override
    public String toString() {
        return super.toString() + " Valuation: " + this.valuation;
    }

    /**
     * Callback method to create a binary edge image.
     */
    @Override
    protected Edge createEdge(Node source, Label label,
            Node target) {
        return DefaultEdge.createEdge(source, label, target);
    }

    /**
     * Callback factory method for the valuation mapping. This implementation
     * returns a {@link HashMap}.
     */
    protected Map<LabelVar,Label> createValuation() {
        return new HashMap<LabelVar,Label>();
    }

    /** The internal map from variables to labels. */
    private final Map<LabelVar,Label> valuation;
}
