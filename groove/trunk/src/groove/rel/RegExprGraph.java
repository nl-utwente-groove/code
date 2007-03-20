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
 * $Id: RegExprGraph.java,v 1.1.1.2 2007-03-20 10:42:53 kastenberg Exp $
 */
package groove.rel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import groove.graph.BinaryEdge;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeSetEdgeSetGraph;
import groove.graph.UnaryEdge;

/**
 * Default implementation of the {@link groove.rel.VarGraph} interface,
 * where the variables correspond to wildcard identifiers in the edge labels.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class RegExprGraph extends NodeSetEdgeSetGraph implements VarGraph {
    /**
     * Constructs an empty graph.
     */
    public RegExprGraph() {
        // empty constructor
    }

    /**
     * Constructs a clone of a given graph.
     */
    public RegExprGraph(Graph graph) {
        super(graph);
    }

    /**
     * Returns an unmodifiable view on the set of variables in this graph.
     * The set is created on demand, and stored if the graph is fixed.
     */
    public Set<String> allVarSet() {
        Set<String> result = vars;
        if (result == null) {
            result = computeVars();
            if (isFixed()) {
                vars = result;
            }
        }
        return Collections.unmodifiableSet(result);
    }

    /**
     * Returns an unmodifiable view on the set of binders in this graph.
     * The set is created on demand, and stored if the graph is fixed.
     */
    public Set<String> boundVarSet() {
        Set<String> result = boundVars;
        if (result == null) {
            result = computeBoundVars();
            if (isFixed()) {
                boundVars = result;
            }
        }
        return Collections.unmodifiableSet(result);
    }
    
    public boolean bindsVar(String var) {
        return boundVarSet().contains(var);
    }

    public boolean hasVar(String var) {
        return allVarSet().contains(var);
    }

    public boolean hasVars() {
        return !allVarSet().isEmpty();
    }

    /**
     * Returns an unmodifiable view on the set of {@link VarEdge}s in this graph.
     * The set is created on demand, and stored if the graph is fixed.
     */
    public Set<VarEdge> varEdgeSet() {
        Set<VarEdge> result = varEdgeSet;
        if (result == null) {
            result = computeVarEdges();
            if (isFixed()) {
                varEdgeSet = result;
            }
        }
        return Collections.unmodifiableSet(result);
    }

    /**
     * This implementation returns a new, empty {@link VarGraph}.
     */
    public RegExprGraph newGraph() {
        return new RegExprGraph();
    }

    /**
     * This implementation returns a new {@link VarGraph} that is a copy of this one.
     */
    public RegExprGraph clone() {
        return new RegExprGraph(this);
    }

    /**
     * If the label wraps a variable, creates a {@link VarBinaryEdge};
     * otherwise delegates the method to <code>super</code>.
     */
    public BinaryEdge createEdge(Node source, Label label, Node target) {
        String labelVar = RegExprLabel.getWildcardId(label);
        if (labelVar == null) {
            return super.createEdge(source, label, target);
        } else {
            return new VarBinaryEdge(source, labelVar, target);
        }
    }

    /**
     * If the label wraps a variable, creates a {@link VarFlag};
     * otherwise delegates the method to <code>super</code>.
     */
    public UnaryEdge createEdge(Node source, Label label) {
        String labelVar = RegExprLabel.getWildcardId(label);
        if (labelVar == null) {
            return super.createEdge(source, label);
        } else {
            return new VarFlag(source, labelVar);
        }
    }

    /**
     * Constructs the set of all variables occurring in the context of
     * regular expression labels in this graph, by iterating over the edges.
     */
    protected Set<String> computeVars() {
        Set<String> result = new HashSet<String>();
        for (Edge edge: edgeSet()) {
            if (edge.label() instanceof RegExprLabel) {
                result.addAll(((RegExprLabel) edge.label()).getRegExpr().allVarSet());
            }
        }
        return result;
    }
    
    /**
     * Constructs the set of all variable binders occurring in the context of
     * regular expression labels in this graph, by iterating over the edges.
     */
    protected Set<String> computeBoundVars() {
        Set<String> result = new HashSet<String>();
        for (Edge edge: edgeSet()) {
            if (edge.label() instanceof RegExprLabel) {
                result.addAll(((RegExprLabel) edge.label()).getRegExpr().boundVarSet());
            }
        }
        return result;
    }
    
    /**
     * Constructs the set of all variable binders occurring in the context of
     * regular expression labels in this graph, by iterating over the edges.
     */
    protected Set<VarEdge> computeVarEdges() {
        Set<VarEdge> result = new HashSet<VarEdge>();
        for (Edge edge: edgeSet()) {
            if (edge instanceof VarEdge) {
                result.add((VarEdge) edge);
            }
        }
        return result;
    }
    
    /**
     * The internally stored set of variables.
     * <code>null</code> as long as the graph is not fixed.
     */
    private Set<String> vars;
    /**
     * The internally stored set of variable binders.
     * <code>null</code> as long as the graph is not fixed.
     */
    private Set<String> boundVars;
    /**
     * The internally stored set of {@link VarEdge}s.
     * <code>null</code> as long as the graph is not fixed.
     */
    private Set<VarEdge> varEdgeSet;
}
