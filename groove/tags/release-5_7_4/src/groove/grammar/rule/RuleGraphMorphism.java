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

import groove.grammar.type.TypeElement;
import groove.graph.AElementMap;
import groove.graph.Morphism;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Mapping between {@link RuleGraph} elements.
 * @author Arend Rensink
 * @version $Revision$
 */
public class RuleGraphMorphism extends Morphism<RuleNode,RuleEdge> {
    /** Constructs a morphism to a rule graph with a given type factory. */
    public RuleGraphMorphism(RuleFactory factory) {
        super(factory);
    }

    /**
     * Creates a new, empty morphism to an untyped rule graph.
     */
    public RuleGraphMorphism() {
        this(RuleFactory.newInstance());
    }

    @Override
    public RuleFactory getFactory() {
        return (RuleFactory) super.getFactory();
    }

    @Override
    public RuleGraphMorphism clone() {
        RuleGraphMorphism result = (RuleGraphMorphism) super.clone();
        // deep copy the variable typing
        result.varTyping = new HashMap<>();
        result.copyVarTyping(this);
        return result;
    }

    @Override
    public void putAll(AElementMap<RuleNode,RuleEdge,RuleNode,RuleEdge> other) {
        assert other instanceof RuleGraphMorphism;
        super.putAll(other);
        copyVarTyping((RuleGraphMorphism) other);
    }

    @Override
    protected RuleGraphMorphism newMap() {
        return new RuleGraphMorphism(getFactory());
    }

    /** 
     * Adds a mapping from a label variable to a set of possible types for that variable.
     * Constrains the previously stored set if there is one.
     * @return the (constrained) set of possible types
     */
    public Set<? extends TypeElement> addVarTypes(LabelVar var,
            Set<? extends TypeElement> types) {
        Set<? extends TypeElement> result = this.varTyping.get(var);
        if (result == null) {
            this.varTyping.put(var, result = new HashSet<TypeElement>(types));
        } else {
            result.retainAll(types);
        }
        return result;
    }

    /** Returns the types allowed for a given label variable. */
    public Set<? extends TypeElement> getVarTypes(LabelVar var) {
        return this.varTyping.get(var);
    }

    /** Returns mapping from label variables to types. */
    public Map<LabelVar,Set<? extends TypeElement>> getVarTyping() {
        return this.varTyping;
    }

    /** Copies the variable typing map from another morphism to this one. */
    public void copyVarTyping(RuleGraphMorphism other) {
        for (Map.Entry<LabelVar,Set<? extends TypeElement>> entry : other.varTyping.entrySet()) {
            addVarTypes(entry.getKey(), entry.getValue());
        }
    }

    private Map<LabelVar,Set<? extends TypeElement>> varTyping =
        new HashMap<>();
}
