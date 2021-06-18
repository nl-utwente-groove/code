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
 * $Id$
 */
package groove.grammar.rule;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import groove.grammar.AnchorKind;
import groove.grammar.host.AnchorValue;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostFactory;
import groove.grammar.host.HostNode;
import groove.grammar.type.TypeElement;
import groove.grammar.type.TypeGuard;
import groove.grammar.type.TypeLabel;
import groove.graph.AElementMap;
import groove.graph.Label;

/**
 * Mapping from rule graph elements (including label variables) to host graph elements.
 * @author Arend Rensink
 * @version $Revision$
 */
public class RuleToHostMap extends AElementMap<RuleNode,RuleEdge,HostNode,HostEdge>
    implements VarMap {
    /**
     * Creates an empty map with an empty valuation.
     */
    public RuleToHostMap(HostFactory factory) {
        super(factory);
        this.valuation = createValuation();
    }

    @Override
    public HostNode putNode(RuleNode key, HostNode image) {
        HostNode result = super.putNode(key, image);
        for (TypeGuard guard : key.getTypeGuards()) {
            putVar(guard.getVar(), image.getType());
        }
        return result;
    }

    /**
     * Maps named wildcards, sharp types and atoms to a corresponding
     * type label.
     * @see #getVar(LabelVar)
     */
    @Override
    public TypeLabel mapLabel(Label label) {
        RuleLabel ruleLabel = (RuleLabel) label;
        TypeLabel result;
        if (ruleLabel.isWildcard()) {
            TypeGuard guard = ruleLabel.getWildcardGuard();
            if (!guard.isNamed()) {
                throw new IllegalArgumentException(
                    String.format("Label %s cannot be mapped", ruleLabel));
            } else {
                result = getVar(guard.getVar()).label();
            }
        } else {
            assert ruleLabel.isSharp() || ruleLabel.isAtom() : String
                .format("Label %s should be sharp or atom", label);
            result = ruleLabel.getTypeLabel();
        }
        return result;
    }

    @Override
    public Valuation getValuation() {
        return this.valuation;
    }

    @Override
    public TypeElement getVar(LabelVar var) {
        return this.valuation.get(var);
    }

    @Override
    public TypeElement putVar(LabelVar var, TypeElement value) {
        return this.valuation.put(var, value);
    }

    @Override
    public void putAllVar(Valuation valuation) {
        this.valuation.putAll(valuation);
    }

    /**
     * Also copies the other's valuation, if any.
     */
    @Override
    public void putAll(AElementMap<RuleNode,RuleEdge,HostNode,HostEdge> other) {
        super.putAll(other);
        if (other instanceof RuleToHostMap) {
            putAllVar(((RuleToHostMap) other).getValuation());
        }
    }

    /**
     * Inserts an anchor key-value pair into the map.
     * @return the old value for {@code key}
     */
    public AnchorValue put(AnchorKey key, AnchorValue value) {
        AnchorValue result = null;
        switch (key.getAnchorKind()) {
        case NODE:
            result = putNode(AnchorKind.node(key), AnchorKind.node(value));
            break;
        case EDGE:
            result = putEdge(AnchorKind.edge(key), AnchorKind.edge(value));
            break;
        case LABEL:
            result = putVar(AnchorKind.label(key), AnchorKind.label(value));
            break;
        default:
            assert false;
        }
        return result;
    }

    /** Returns the anchor value to which a given anchor key is mapped. */
    public AnchorValue get(AnchorKey key) {
        AnchorValue result = null;
        switch (key.getAnchorKind()) {
        case NODE:
            result = getNode(AnchorKind.node(key));
            break;
        case EDGE:
            result = getEdge(AnchorKind.edge(key));
            break;
        case LABEL:
            result = getVar(AnchorKind.label(key));
            break;
        default:
            assert false;
        }
        return result;
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

    /** Specialises the return type. */
    @Override
    public HostFactory getFactory() {
        return (HostFactory) super.getFactory();
    }

    /**
     * Callback factory method for the valuation mapping. This implementation
     * returns a {@link HashMap}.
     */
    protected Valuation createValuation() {
        return new Valuation();
    }

    @Override
    protected Map<RuleEdge,HostEdge> createEdgeMap() {
        return new LinkedHashMap<>();
    }

    @Override
    protected Map<RuleNode,HostNode> createNodeMap() {
        return new LinkedHashMap<>();
    }

    /** The internal map from variables to labels. */
    private final Valuation valuation;
}
