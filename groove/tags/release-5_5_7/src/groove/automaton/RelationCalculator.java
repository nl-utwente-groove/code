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
package groove.automaton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import groove.automaton.RegExpr.Sharp;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.lts.GTS;
import groove.lts.GTSListener;
import groove.lts.GraphTransition;

/**
 * Calculator yielding a {@link NodeRelation}.
 * @author Arend Rensink
 * @version $Revision$
 */
public class RelationCalculator implements RegExprCalculator<NodeRelation>, GTSListener {
    /**
     * Creates a relation calculator based on a given graph and
     * relation factory.
     */
    public RelationCalculator(Graph graph, NodeRelation factory) {
        this.factory = factory;
        this.graph = graph;
    }

    /**
     * Copies the relation associated with the atom in <code>expr</code> and
     * stores it in the underlying mapping.
     */
    @Override
    public NodeRelation computeAtom(RegExpr.Atom expr) {
        return createRelation(expr.text());
    }

    /**
     * Computes the union of the relations associated with the operands of
     * <code>expr</code>, and stores it in the underlying mapping.
     */
    @Override
    public NodeRelation computeChoice(RegExpr.Choice expr, List<NodeRelation> args) {
        Iterator<NodeRelation> argsIter = args.iterator();
        NodeRelation result = argsIter.next();
        while (argsIter.hasNext()) {
            NodeRelation operand = argsIter.next();
            result.doOr(operand);
        }
        return result;
    }

    /**
     * Computes the identity relation and stores it in the underlying mapping.
     */
    @Override
    public NodeRelation computeEmpty(RegExpr.Empty expr) {
        NodeRelation result = getFactory().newInstance();
        for (Node node : this.graph.nodeSet()) {
            result.addSelfRelated(node);
        }
        return result;
    }

    /**
     * Computes the inverse of the relation associated with the operand of
     * <code>expr</code> and stores it in the underlying mapping.
     */
    @Override
    public NodeRelation computeInv(RegExpr.Inv expr, NodeRelation arg) {
        arg.doInverse();
        return arg;
    }

    /**
     * Negation of operations is not supported.
     * @throws UnsupportedOperationException always
     */
    @Override
    public NodeRelation computeNeg(RegExpr.Neg expr, NodeRelation arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Computes the transitive closure over the relation associated with the
     * operand of <code>expr</code>, and stores it in the underlying mapping.
     */
    @Override
    public NodeRelation computePlus(RegExpr.Plus expr, NodeRelation arg) {
        arg.doTransitiveClosure();
        return arg;
    }

    /**
     * Computes the sequential composition of the relations associated with the
     * operands of <code>expr</code>, and stores it in the underlying mapping.
     */
    @Override
    public NodeRelation computeSeq(RegExpr.Seq expr, List<NodeRelation> argList) {
        Iterator<NodeRelation> argsIter = argList.iterator();
        NodeRelation result = argsIter.next();
        while (argsIter.hasNext()) {
            NodeRelation operand = argsIter.next();
            result.doThen(operand);
        }
        return result;
    }

    /**
     * Computes the transitive and reflexive closure over the relation
     * associated with the operand of <code>expr</code>, and stores it in the
     * underlying mapping.
     */
    @Override
    public NodeRelation computeStar(RegExpr.Star expr, NodeRelation arg) {
        arg.doTransitiveClosure();
        for (Node node : this.graph.nodeSet()) {
            arg.addSelfRelated(node);
        }
        return arg;
    }

    @Override
    public NodeRelation computeSharp(Sharp expr) {
        return createRelation(expr.getAtomText());
    }

    /**
     * Creates a fresh relation on the basis of the set of all pairs.
     */
    @Override
    public NodeRelation computeWildcard(RegExpr.Wildcard expr) {
        NodeRelation result = getFactory().newInstance();
        for (Edge edge : this.graph.edgeSet()) {
            result.addRelated(edge);
        }
        return result;
    }

    /** Returns the graph on which this calculator is based. */
    public Graph getGraph() {
        return this.graph;
    }

    /**
     * Returns the current relation factory.
     */
    public NodeRelation getFactory() {
        return this.factory;
    }

    @Override
    public void addUpdate(GTS gts, GraphTransition transition) {
        if (this.labelEdgeMap != null) {
            addToLabelEdgeMap(transition, this.labelEdgeMap);
        }
    }

    /** Start listening to the wrapped graph, if it supports listeners. */
    public void startListening() {
        if (this.graph instanceof GTS) {
            ((GTS) this.graph).addLTSListener(this);
        }
    }

    /** Stop listening to the wrapped graph. */
    public void stopListening() {
        if (this.graph instanceof GTS) {
            ((GTS) this.graph).removeLTSListener(this);
        }
    }

    private NodeRelation createRelation(String text) {
        NodeRelation result = getFactory().newInstance();
        Set<Edge> edges = getEdgeSet(text);
        if (edges != null) {
            for (Edge edge : edges) {
                result.addRelated(edge);
            }
        }
        return result;
    }

    private Set<Edge> getEdgeSet(String text) {
        if (this.labelEdgeMap == null) {
            this.labelEdgeMap = computeLabelEdgeMap();
        }
        return this.labelEdgeMap.get(text);
    }

    private Map<String,Set<Edge>> computeLabelEdgeMap() {
        Map<String,Set<Edge>> result = new HashMap<>();
        for (Edge edge : this.graph.edgeSet()) {
            addToLabelEdgeMap(edge, result);
        }
        return result;
    }

    /** Adds an edge to a given label-edge-set-map. */
    private void addToLabelEdgeMap(Edge edge, Map<String,Set<Edge>> result) {
        String text = edge.label()
            .text();
        Set<Edge> edges = result.get(text);
        if (edges == null) {
            result.put(text, edges = new HashSet<>());
        }
        edges.add(edge);
    }

    /** Mapping from label test to sets of edges. */
    private Map<String,Set<Edge>> labelEdgeMap;
    /** The graph from which relations are to be computed. */
    private final Graph graph;
    /** Factory for creating relations. */
    private final NodeRelation factory;
}