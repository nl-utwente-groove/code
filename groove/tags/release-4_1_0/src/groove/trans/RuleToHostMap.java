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
package groove.trans;

import groove.graph.GenericNodeEdgeMap;
import groove.graph.GraphHashMap;
import groove.graph.Label;
import groove.graph.TypeLabel;
import groove.rel.LabelVar;
import groove.rel.VarMap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of the {@link RuleToHostMap} interface where the variable
 * mapping part is given by a separate instance variable.
 * @author Arend Rensink
 * @version $Revision$
 */
public class RuleToHostMap extends
        GraphHashMap<RuleNode,HostNode,RuleEdge,HostEdge> implements VarMap {
    /**
     * Creates an empty map with an empty valuation.
     */
    public RuleToHostMap() {
        this.valuation = createValuation();
    }

    /**
     * Maps named wildcards, sharp types and atoms to a corresponding
     * type label.
     * @see #getVar(LabelVar)
     */
    @Override
    public Label mapLabel(Label label) {
        Label result;
        RuleLabel ruleLabel = (RuleLabel) label;
        if (ruleLabel.isWildcard()) {
            LabelVar var = ruleLabel.getWildcardId();
            if (var == null) {
                throw new IllegalArgumentException(String.format(
                    "Label %s cannot be mapped", label));
            } else {
                result = getVar(var);
            }
        } else {
            assert ruleLabel.isSharp() || ruleLabel.isAtom();
            result = ruleLabel.getTypeLabel();
        }
        return result;
    }

    public Map<LabelVar,TypeLabel> getValuation() {
        return this.valuation;
    }

    public TypeLabel getVar(LabelVar var) {
        return this.valuation.get(var);
    }

    public TypeLabel putVar(LabelVar var, TypeLabel value) {
        return this.valuation.put(var, value);
    }

    public void putAllVar(Map<LabelVar,TypeLabel> valuation) {
        this.valuation.putAll(valuation);
    }

    /**
     * Also copies the other's valuation, if any.
     */
    @Override
    public void putAll(
            GenericNodeEdgeMap<RuleNode,HostNode,RuleEdge,HostEdge> other) {
        super.putAll(other);
        if (other instanceof RuleToHostMap) {
            putAllVar(((RuleToHostMap) other).getValuation());
        }
    }

    /**
     * This implementation returns a {@link RuleToHostMap}.}
     */
    @Override
    public RuleToHostMap clone() {
        return (RuleToHostMap) super.clone();
    }

    @Override
    public void clear() {
        super.clear();
        this.valuation.clear();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RuleToHostMap && super.equals(obj)
            && this.valuation.equals(((RuleToHostMap) obj).getValuation());
    }

    @Override
    public int hashCode() {
        return super.hashCode() + this.valuation.hashCode();
    }

    @Override
    public String toString() {
        return super.toString() + " Valuation: " + this.valuation;
    }

    @Override
    public RuleToHostMap newMap() {
        return new RuleToHostMap();
    }

    @Override
    public HostFactory getFactory() {
        return HostFactory.INSTANCE;
    }

    /**
     * Callback factory method for the valuation mapping. This implementation
     * returns a {@link HashMap}.
     */
    protected Map<LabelVar,TypeLabel> createValuation() {
        return new LinkedHashMap<LabelVar,TypeLabel>();
    }

    @Override
    protected Map<RuleEdge,HostEdge> createEdgeMap() {
        return new LinkedHashMap<RuleEdge,HostEdge>();
    }

    @Override
    protected Map<RuleNode,HostNode> createNodeMap() {
        return new LinkedHashMap<RuleNode,HostNode>();
    }

    /** The internal map from variables to labels. */
    private final Map<LabelVar,TypeLabel> valuation;
}
