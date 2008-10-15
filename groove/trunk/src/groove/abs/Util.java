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
package groove.abs;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import groove.graph.DefaultMorphism;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.graph.NodeFactory;
import groove.match.GraphSearchPlanFactory;
import groove.match.SearchPlanStrategy;
import groove.rel.VarNodeEdgeMap;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.SPORule;

/**
 * Utility methods for abstraction.
 * @author Iovka Boneva
 * @version $Revision $
 */
public class Util {

    /**
     * Computes the union of two graphs synchronised on a map.
     * @param baseGraph the base graph. Out parameter : after return, it
     *        contains the computed disjoint union.
     * @param addGraph the graph to be added to baseGraph
     * @param sync a used for synchronisation. Out parameter : after return,
     *        contains the map updated for the values of the newly added nodes
     *        to baseGraph.
     * @require sync.nodeMap().keySet() \subseteq baseGraph.nodeSet()
     * @require sync.nodeMap().values() \subseteq addGraph.nodeSet()
     * @require sync in an injective map
     */
    static public void dunion(Graph baseGraph, Graph addGraph,
            NodeEdgeMap sync, NodeFactory nodeFactory) {
        assert baseGraph.nodeSet().containsAll(sync.nodeMap().keySet());
        assert addGraph.nodeSet().containsAll(sync.nodeMap().values());

        Graph addCopy = addGraph.clone();
        // renaming in the add graph, and its inverse map
        Map<Node,Node> renaming = new HashMap<Node,Node>(addCopy.nodeCount());
        // the inverse map is used for computing the baseMorph mapping
        Map<Node,Node> inverseRenaming =
            new HashMap<Node,Node>(addCopy.nodeCount());
        for (Node n : addGraph.nodeSet()) {
            if (baseGraph.containsElement(n)) {
                Node newN =
                    nodeFactory != null ? nodeFactory.newNode()
                            : DefaultNode.createNode();
                addCopy.addNode(newN);
                addCopy.mergeNodes(n, newN);
                renaming.put(n, newN);
                inverseRenaming.put(newN, n);
            } else {
                renaming.put(n, n);
                inverseRenaming.put(n, n);
            }
        }

        assert intersection(addCopy.nodeSet(), baseGraph.nodeSet()).size() == 0 : "Node sets not disjoint";
        assert addCopy.nodeSet().equals(new HashSet<Node>(renaming.values())) : "Incorrect renaming mapping";

        baseGraph.addNodeSet(addCopy.nodeSet());
        baseGraph.addEdgeSet(addCopy.edgeSet());
        for (Map.Entry<Node,Node> e : sync.nodeMap().entrySet()) {
            Node image = renaming.get(e.getValue());
            baseGraph.mergeNodes(image, e.getKey());
            inverseRenaming.remove(image);
        }
        // now inverseRenaming contains only images for nodes that were indeed
        // newly added to baseGraph (and not only for merging)
        sync.nodeMap().putAll(inverseRenaming);
    }

    // ////////////////////////////////////////////////////////////////////
    // For debugging

    static public void checkSubgraph(Graph subgraph, Graph graph) {
        assert graph.containsElementSet(subgraph.nodeSet()) : "Not a subgraph";
        assert graph.containsElementSet(subgraph.edgeSet()) : "Not a subgraph";
    }

    static public <T> Set<? extends T> intersection(Set<? extends T> s1,
            Set<? extends T> s2) {
        Set<T> result = new HashSet<T>(Math.min(s1.size(), s2.size()));
        for (T e : s1) {
            if (s2.contains(e)) {
                result.add(e);
            }
        }
        return result;
    }

    /** Checks whether assertions are enabled. */
    public static boolean ea() {
        if (!Abstraction.DEBUG) {
            return false;
        }
        try {
            assert false;
            return false;
        } catch (AssertionError err) {
            return true;
        }
    }

    public static Morphism getTotalExtension(Morphism morph) {
        SearchPlanStrategy mstr =
            GraphSearchPlanFactory.getInstance().createMatcher(morph.dom(),
                morph.elementMap().nodeMap().keySet(),
                morph.elementMap().edgeMap().keySet());
        return new DefaultMorphism(morph.dom(), morph.cod(), mstr.getMatchIter(
            morph.cod(), morph.elementMap()).next());
    }

    public static Set<Label> labelSet(Graph g) {
        Set<Label> result = new HashSet<Label>();
        for (Edge e : g.edgeSet()) {
            result.add(e.label());
        }
        return result;
    }

    /** */
    public static final GraphSearchPlanFactory injspf =
        GraphSearchPlanFactory.getInstance(true, false);
    /** */
    public static final GraphSearchPlanFactory spf =
        GraphSearchPlanFactory.getInstance(false, false);

    /**
     * Computes all injective matchings between two graphs and that extend an
     * existing match.
     * @param dom The domain of the matchings
     * @param cod Tho codomain of the matchings.
     * @param toExtend The matching to be extended. Should be a matching from
     *        <code>dom</code> into <code>cod</code>.
     * @return All injective matchings from <code>dom</code> into
     *         <code>cod</code> that extend the matching <code>toExtend</code>.
     * @see #getMatchSet(Graph, Graph, NodeEdgeMap)
     */
    public static Collection<VarNodeEdgeMap> getInjMatchSet(Graph dom,
            Graph cod, NodeEdgeMap toExtend) {
        SearchPlanStrategy mstr =
            Util.injspf.createMatcher(dom, toExtend.nodeMap().keySet(),
                toExtend.edgeMap().keySet());
        return mstr.getMatchSet(cod, toExtend);
    }

    /**
     * Computes all injective matchings between two graphs that extend an
     * existing match.
     * @param dom The domain of the matchings
     * @param cod Tho codomain of the matchings.
     * @param toExtend The matching to be extended. Should be a matching from
     *        <code>dom</code> into <code>cod</code>.
     * @return In iterator ovec all injective matchings from <code>dom</code>
     *         into <code>cod</code> that extend the matching
     *         <code>toExtend</code>.
     * @see #getMatchSet(Graph, Graph, NodeEdgeMap)
     */
    public static Iterable<VarNodeEdgeMap> getInjMatchesIter(Graph dom,
            Graph cod, NodeEdgeMap toExtend) {
        SearchPlanStrategy mstr =
            Util.injspf.createMatcher(dom, toExtend.nodeMap().keySet(),
                toExtend.edgeMap().keySet());
        return mstr.getMatches(cod, toExtend);
    }

    /**
     * Computes all matchings between two graphs and that extend an existing
     * match.
     * @param dom The domain of the matchings
     * @param cod Tho codomain of the matchings.
     * @param toExtend The matching to be extended. Should be a matching from
     *        <code>dom</code> into <code>cod</code>.
     * @return All injective matchings from <code>dom</code> into
     *         <code>cod</code> that extend the matching <code>toExtend</code>.
     * @see #getInjMatchSet(Graph, Graph, NodeEdgeMap)
     */
    public static Collection<VarNodeEdgeMap> getMatchSet(Graph dom, Graph cod,
            NodeEdgeMap toExtend) {
        SearchPlanStrategy mstr =
            Util.spf.createMatcher(dom, toExtend.nodeMap().keySet(),
                toExtend.edgeMap().keySet());
        return mstr.getMatchSet(cod, toExtend);
    }

    /**
     * Computes all matchings between two graphs and that extend an existing
     * match.
     * @param dom The domain of the matchings
     * @param cod Tho codomain of the matchings.
     * @param toExtend The matching to be extended. Should be a matching from
     *        <code>dom</code> into <code>cod</code>.
     * @return An iterator ovec all injective matchings from <code>dom</code>
     *         into <code>cod</code> that extend the matching
     *         <code>toExtend</code>.
     * @see #getInjMatchSet(Graph, Graph, NodeEdgeMap)
     */
    public static Iterable<VarNodeEdgeMap> getMatchesIter(Graph dom, Graph cod,
            NodeEdgeMap toExtend) {
        SearchPlanStrategy mstr =
            Util.spf.createMatcher(dom, toExtend.nodeMap().keySet(),
                toExtend.edgeMap().keySet());
        return mstr.getMatches(cod, toExtend);
    }

    /**
     * Defines whether abstract transformations are possible for a given graph
     * grammar. Abstract transformations are only possible for grammars without
     * nested rules (so, without negative application conditions).
     * @param grammar
     * @return <code>true</code> if abstract transformation is possible for
     *         this grammar
     */
    public static boolean isAbstractionPossible(GraphGrammar grammar) {
        for (Rule rule : grammar.getRules()) {
            SPORule r = (SPORule) rule;
            if (!r.getSubConditions().isEmpty()) {
                return false;
            }
        }
        return true;
    }

}
