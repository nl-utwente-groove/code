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
package groove.trans;

import static groove.graph.GraphRole.RULE;
import groove.graph.GraphRole;
import groove.graph.NodeSetEdgeSetGraph;
import groove.graph.TypeGuard;
import groove.rel.LabelVar;

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
     * Constructs a new, empty rule graph with a fresh rule factory.
     * @param name the name of the new rule graph
     */
    public RuleGraph(String name) {
        this(name, RuleFactory.newInstance());
    }

    /**
     * Constructs a new, empty rule graph.
     * @param name the name of the new rule graph
     */
    public RuleGraph(String name, RuleFactory factory) {
        super(name);
        this.factory = factory;
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
        return new RuleGraph(name, getFactory());
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

    @Override
    public boolean addNode(RuleNode node) {
        boolean result = super.addNode(node);
        if (result) {
            addBinders(node);
        }
        return result;
    }

    @Override
    public boolean addEdgeWithoutCheck(RuleEdge edge) {
        boolean result = super.addEdgeWithoutCheck(edge);
        if (result) {
            addBinders(edge);
            if (edge.label().isMatchable()) {
                this.allVars.addAll(edge.label().getMatchExpr().allVarSet());
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + "; Variables: " + getAllVars();
    }

    /** Adds a given rule element to the variable binding map. */
    private void addBinders(RuleElement element) {
        for (TypeGuard guard : element.getTypeGuards()) {
            LabelVar var = guard.getVar();
            createBinders(var).add(element);
            this.allVars.add(var);
        }
    }

    /** 
     * Adds a variable to those bound in this graph.
     * This is to declare that the variable is bound by the condition root. 
     */
    public boolean addBoundVar(LabelVar var) {
        boolean result = !this.binderMap.containsKey(var);
        if (result) {
            createBinders(var);
            return this.allVars.add(var);
        }
        return result;
    }

    /** Returns the set of (named) variables bound by elements of this graph or by the root. */
    public Set<LabelVar> getBoundVars() {
        return this.binderMap.keySet();
    }

    /** Adds a variable to those known in this graph. */
    public boolean addVar(LabelVar var) {
        return this.allVars.add(var);
    }

    /** Adds a set of variables to those known in this graph. */
    public boolean addVarSet(Collection<LabelVar> varSet) {
        return this.allVars.addAll(varSet);
    }

    /** Tests if a label variable is known in this graph. */
    public boolean containsVar(LabelVar var) {
        return this.allVars.contains(var);
    }

    /** Returns the set of all (named) variables known in this graph. */
    public Set<LabelVar> getAllVars() {
        return this.allVars;
    }

    /**
     * Returns the set of elements that bind a given label variable.
     * If the set is empty, the variable is used but not bound in this graph.
     * @return the set of binders for {@code var}, or {@code null} if {@code var}
     * is not a known variable in this graph
     */
    public Set<RuleElement> getBinders(LabelVar var) {
        return this.binderMap.get(var);
    }

    /** Lazily creates and returns the set of binders for a given label variable. */
    private Set<RuleElement> createBinders(LabelVar var) {
        Set<RuleElement> result = this.binderMap.get(var);
        if (result == null) {
            this.binderMap.put(var, result = new HashSet<RuleElement>());
        }
        return result;
    }

    /** Mapping from label variables to rule elements that bind them. */
    private final Map<LabelVar,Set<RuleElement>> binderMap =
        new HashMap<LabelVar,Set<RuleElement>>();
    /** Set of all known variables. */
    private final Set<LabelVar> allVars = new HashSet<LabelVar>();
    private final RuleFactory factory;
}
