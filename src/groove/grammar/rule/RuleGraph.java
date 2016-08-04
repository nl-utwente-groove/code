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
 * $Id$
 */
package groove.grammar.rule;

import static groove.graph.GraphRole.RULE;
import groove.grammar.type.TypeGuard;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.graph.NodeSetEdgeSetGraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Special class of graphs that can appear (only) in rules.
 * Rule graphs may only have {@link RuleEdge}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RuleGraph extends NodeSetEdgeSetGraph<RuleNode,RuleEdge> {
    /**
     * Constructs a new, empty rule graph.
     * @param name the name of the new rule graph
     */
    public RuleGraph(String name, boolean injective, RuleFactory factory) {
        super(name);
        this.factory = factory;
        if (injective) {
            GraphInfo.setInjective(this, true);
        }
    }

    /**
     * Clones a given rule graph.
     */
    private RuleGraph(RuleGraph graph) {
        super(graph);
        this.factory = graph.getFactory();
    }

    @Override
    public GraphRole getRole() {
        return RULE;
    }

    @Override
    public RuleGraph clone() {
        return new RuleGraph(this);
    }

    @Override
    public RuleGraph newGraph(String name) {
        return new RuleGraph(name, isInjective(), getFactory());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<RuleEdge> edgeSet() {
        return (Set<RuleEdge>) super.edgeSet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<RuleNode> nodeSet() {
        return (Set<RuleNode>) super.nodeSet();
    }

    @Override
    public RuleFactory getFactory() {
        return this.factory;
    }

    private final RuleFactory factory;

    @Override
    public boolean addNode(RuleNode node) {
        boolean result = super.addNode(node);
        if (result) {
            addBinders(node);
        }
        return result;
    }

    @Override
    public boolean addEdge(RuleEdge edge) {
        boolean result = super.addEdge(edge);
        if (result) {
            addBinders(edge);
            for (LabelVar var : edge.label().allVarSet()) {
                addKey(this.varMap, var).add(edge);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + "; Variables: " + varSet();
    }

    /** Adds a given rule element to the variable binding map. */
    private void addBinders(RuleElement element) {
        for (TypeGuard guard : element.getTypeGuards()) {
            LabelVar var = guard.getVar();
            if (var.hasName()) {
                addKey(this.varMap, var).add(element);
                addKey(this.binderMap, var).add(element);
            }
        }
    }

    /** Returns the set of (named) variables bound by elements of this graph. */
    public Set<LabelVar> getBoundVars() {
        return this.binderMap.keySet();
    }

    /**
     * Returns the set of elements that bind a given label variable.
     * @return the (non-empty) set of binders for {@code var}, or {@code null} if {@code var}
     * is not a known variable in this graph
     */
    public Set<RuleElement> getBinders(LabelVar var) {
        return this.binderMap.get(var);
    }

    /** Mapping from label variables to rule elements that bind them. */
    private final Map<LabelVar,Set<RuleElement>> binderMap =
        new HashMap<>();

    /** Adds a variable to those known in this graph. */
    public boolean addVar(LabelVar var) {
        boolean result = !this.varMap.containsKey(var);
        addKey(this.varMap, var);
        return result;
    }

    /** Adds a set of variables to those known in this graph. */
    public boolean addVarSet(Collection<LabelVar> varSet) {
        boolean result = false;
        for (LabelVar var : varSet) {
            result |= addVar(var);
        }
        return result;
    }

    /** Tests if a label variable is known in this graph. */
    public boolean containsVar(LabelVar var) {
        return this.varMap.containsKey(var);
    }

    /** Returns the set of all (named) variables known in this graph. */
    public Set<LabelVar> varSet() {
        return this.varMap.keySet();
    }

    /** Returns the mapping of all (named) variables known in this graph to elements on which they occur. */
    public Map<LabelVar,Set<RuleElement>> varMap() {
        return this.varMap;
    }

    /** Set of all known variables. */
    private final Map<LabelVar,Set<RuleElement>> varMap = new HashMap<>();

    /** Lazily creates and returns the set of binders for a given label variable. */
    private Set<RuleElement> addKey(Map<LabelVar,Set<RuleElement>> map, LabelVar var) {
        Set<RuleElement> result = map.get(var);
        if (result == null) {
            map.put(var, result = new HashSet<>());
        }
        return result;
    }

    /** Indicates if this rule graph is to be injectively mapped. */
    public boolean isInjective() {
        return GraphInfo.isInjective(this);
    }
}
