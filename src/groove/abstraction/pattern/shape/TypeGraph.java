/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.abstraction.pattern.shape;

import groove.abstraction.MyHashMap;
import groove.abstraction.pattern.Util;
import groove.abstraction.pattern.match.Match;
import groove.abstraction.pattern.match.Matcher;
import groove.abstraction.pattern.match.MatcherFactory;
import groove.abstraction.pattern.trans.PatternGraphRuleApplication;
import groove.abstraction.pattern.trans.PatternRule;
import groove.abstraction.pattern.trans.RuleFactory;
import groove.abstraction.pattern.trans.RuleNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.GraphRole;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.trans.DefaultHostGraph;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.Rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Pattern type graph.
 * 
 * @author Eduardo Zambon
 */
public final class TypeGraph extends AbstractPatternGraph<TypeNode,TypeEdge> {

    // ------------------------------------------------------------------------
    // Static fields
    // ------------------------------------------------------------------------

    /** Prototype simple graph used to create new patterns. */
    private static final HostGraph protSimpleGraph = new DefaultHostGraph(
        "protSGraph");

    // ------------------------------------------------------------------------
    // Object fields
    // ------------------------------------------------------------------------

    private final PatternFactory patternFactory;
    private final RuleFactory ruleFactory;
    private final Map<TypeNode,PatternRule> closureRules;
    private boolean fixed;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    public TypeGraph(String name) {
        super(name);
        this.patternFactory = new PatternFactory(this);
        this.ruleFactory = new RuleFactory();
        this.closureRules = new MyHashMap<TypeNode,PatternRule>();
        this.fixed = false;
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pattern type graph: " + getName() + "\n");
        sb.append("Nodes: " + nodeSet() + "\n");
        sb.append("Edges: " + edgeSet() + "\n");
        sb.append("\nPatterns (depth = " + depth() + "):\n");
        for (TypeNode node : nodeSet()) {
            sb.append("  " + node.getPattern().getName() + ": "
                + node.getPattern().toString() + "\n");
        }
        sb.append("\nMorphisms:\n");
        for (TypeEdge edge : edgeSet()) {
            sb.append("  " + edge.getMorphism().toString() + "\n");
        }
        return sb.toString();
    }

    @Override
    public boolean isFixed() {
        return this.fixed;
    }

    @Override
    public void setFixed() {
        assert !isFixed();
        for (TypeNode node : this.nodeSet()) {
            node.setFixed();
        }
        for (TypeEdge edge : this.edgeSet()) {
            edge.setFixed();
        }
        computeLayers();
        createClosureRules();
        if (getInfo() == null) {
            setInfo(new GraphInfo<TypeNode,TypeEdge>());
        }
        this.fixed = true;
    }

    @Override
    public void testFixed(boolean fixed) {
        if (isFixed() != fixed) {
            throw new IllegalStateException();
        }
    }

    @Override
    public TypeNode addNode(int nr) {
        HostGraph pattern = newSimpleGraph(TypeNode.PREFIX + nr);
        TypeNode node = new TypeNode(nr, pattern);
        addNode(node);
        return node;
    }

    @Override
    public GraphRole getRole() {
        return GraphRole.TYPE;
    }

    @Override
    protected boolean isTypeCorrect(Node node) {
        return node instanceof TypeNode;
    }

    @Override
    protected boolean isTypeCorrect(Edge edge) {
        return edge instanceof TypeEdge;
    }

    @Override
    public boolean addNode(TypeNode node) {
        boolean result;
        assert !isFixed() : "Trying to add " + node + " to unmodifiable graph";
        result = this.graphNodeSet.add(node);
        return result;
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Creates a new empty simple graph with the given name. */
    public HostGraph newSimpleGraph(String name) {
        HostGraph result = new DefaultHostGraph(protSimpleGraph);
        result.setName(name);
        return result;
    }

    /** Creates and returns an empty simple graph morphism. */
    public SimpleMorphism newSimpleMorphism(String name, TypeNode source,
            TypeNode target) {
        return new SimpleMorphism(name, source, target);
    }

    /** Creates and returns a new edge with an empty morphism. */
    public TypeEdge addEdge(int nr, TypeNode source, TypeNode target) {
        SimpleMorphism morph =
            newSimpleMorphism(TypeEdge.PREFIX + nr, source, target);
        TypeEdge edge = new TypeEdge(nr, source, target, morph);
        addEdge(edge);
        return edge;
    }

    private void computeLayers() {
        for (TypeNode node : nodeSet()) {
            addToLayer(node);
        }
    }

    private PatternNode createPatternNode(TypeNode type) {
        return getPatternFactory().createNode(type);
    }

    private PatternNode createPatternNode(TypeNode type,
            Collection<PatternNode> used) {
        return getPatternFactory().createNode(type, used);
    }

    private PatternEdge createPatternEdge(PatternNode source, TypeEdge type,
            PatternNode target) {
        return getPatternFactory().createEdge(source, type, target);
    }

    /** Lifts the given simple graph to a pattern graph. */
    public PatternGraph lift(Graph<?,?> graph) {
        PatternGraph result = getPatternFactory().newPatternGraph();
        Map<Node,PatternNode> nodeMap = new MyHashMap<Node,PatternNode>();
        lift(graph, result, nodeMap, null);
        // Compute the closure for the pattern graph we have so far.
        close(result);
        assert result.isWellFormed();
        assert result.isCommuting();
        return result;
    }

    /**
     * Private implementation of lifting that only works on layers 0 and 1.
     * @param graph the simple graph to be lifted.
     * @param result the resulting partially lifted pattern graph. This graph
     *               is NOT closed.
     * @param nodeMap
     *    a non-null map of simple nodes to pattern nodes of layer 0. If the
     *    map is non-empty the corresponding pattern nodes in the
     *    image of the map are reused when necessary.
     * @param edgeMap
     *    a map of simple edges to pattern nodes of layer 1. The map may be null. 
     */
    private void lift(Graph<?,?> graph, PatternGraph result,
            Map<Node,PatternNode> nodeMap, Map<Edge,PatternNode> edgeMap) {
        // First lift the nodes of layer 0.
        for (TypeNode tNode : getLayerNodes(0)) {
            // For each node pattern.
            Set<TypeLabel> nodeLabels = tNode.getNodeLabels();
            // For each matched node in the simple graph.
            for (Node sNode : match(graph, nodeLabels)) {
                // Create a new pattern node.
                PatternNode pNode = nodeMap.get(sNode);
                if (pNode == null) {
                    pNode = createPatternNode(tNode, nodeMap.values());
                    nodeMap.put(sNode, pNode);
                }
                result.addNode(pNode);
            }
        }

        // Now lift the nodes of layer 1.
        for (TypeNode tTgt : getLayerNodes(1)) {
            // For each edge pattern.
            HostGraph pattern = tTgt.getPattern();
            HostEdge edge = tTgt.getSimpleEdge();
            Set<TypeLabel> srcLabels =
                Util.getNodeLabels(pattern, edge.source());
            Set<TypeLabel> tgtLabels =
                Util.getNodeLabels(pattern, edge.target());
            TypeLabel edgeLabel = edge.label();
            // For each matched edge in the simple graph.
            for (Edge sEdge : match(graph, srcLabels, edgeLabel, tgtLabels)) {
                // Create a new pattern node.
                PatternNode pTgt;
                if (edgeMap != null) {
                    pTgt = edgeMap.get(sEdge);
                    if (pTgt == null) {
                        pTgt = createPatternNode(tTgt, edgeMap.values());
                        edgeMap.put(sEdge, pTgt);
                    }
                } else {
                    pTgt = createPatternNode(tTgt);
                }
                result.addNode(pTgt);
                // Find source pattern node and pattern edge type.
                PatternNode pSrc = nodeMap.get(sEdge.source());
                TypeEdge tEdge = getCoveringEdge(tTgt, pTgt.getSource());
                PatternEdge pEdge = createPatternEdge(pSrc, tEdge, pTgt);
                result.addEdge(pEdge);
                // Now repeat for the target of the simple edge.
                if (!sEdge.source().equals(sEdge.target())) {
                    pSrc = nodeMap.get(sEdge.target());
                    tEdge = getCoveringEdge(tTgt, pTgt.getTarget());
                    pEdge = createPatternEdge(pSrc, tEdge, pTgt);
                    result.addEdge(pEdge);
                }
            }
        }
    }

    /** Lifts the given simple rule to a pattern rule. */
    public PatternRule lift(Rule sRule) {
        // First we lift the simple rule to pattern graphs...
        PatternGraph liftedLhs = getPatternFactory().newPatternGraph();
        PatternGraph liftedRhs = getPatternFactory().newPatternGraph();
        Map<Node,PatternNode> nodeMap = new MyHashMap<Node,PatternNode>();
        Map<Edge,PatternNode> edgeMap = new MyHashMap<Edge,PatternNode>();
        lift(sRule.lhs(), liftedLhs, nodeMap, edgeMap);
        lift(sRule.rhs(), liftedRhs, nodeMap, edgeMap);
        close(liftedLhs);
        close(liftedRhs);

        // ...then we properly fill the pattern rule object.
        PatternRule pRule = new PatternRule(sRule, this);
        Map<PatternNode,RuleNode> ruleMap =
            new MyHashMap<PatternNode,RuleNode>();
        // Add nodes to the rule.
        for (PatternNode pNode : liftedLhs.nodeSet()) {
            RuleNode rNode;
            if (liftedRhs.nodeSet().contains(pNode)) {
                rNode = pRule.addReaderNode(pNode.getType());
            } else {
                rNode = pRule.addEraserNode(pNode.getType());
            }
            ruleMap.put(pNode, rNode);
        }
        for (PatternNode pNode : liftedRhs.nodeSet()) {
            if (!liftedLhs.nodeSet().contains(pNode)) {
                RuleNode rNode = pRule.addCreatorNode(pNode.getType());
                ruleMap.put(pNode, rNode);
            }
        }
        // Add edges to the rule.
        for (PatternEdge pEdge : liftedLhs.edgeSet()) {
            RuleNode rSrc = ruleMap.get(pEdge.source());
            RuleNode rTgt = ruleMap.get(pEdge.target());
            if (liftedRhs.edgeSet().contains(pEdge)) {
                pRule.addReaderEdge(rSrc, pEdge.getType(), rTgt);
            } else {
                pRule.addEraserEdge(rSrc, pEdge.getType(), rTgt);
            }
        }
        for (PatternEdge pEdge : liftedRhs.edgeSet()) {
            if (!liftedLhs.edgeSet().contains(pEdge)) {
                pRule.addCreatorEdge(ruleMap.get(pEdge.source()),
                    pEdge.getType(), ruleMap.get(pEdge.target()));
            }
        }

        return pRule;
    }

    /** Computes the closure for the given pattern shape w.r.t. this type graph. */
    public void close(PatternGraph pGraph) {
        // Iterate from layer 2 on.
        for (int layer = 2; layer <= depth(); layer++) {
            // For each pattern of the layer.
            for (TypeNode tNode : getLayerNodes(layer)) {
                // Check if we can compose this new pattern type.
                PatternRule pRule = getClosureRule(tNode);
                Matcher matcher =
                    MatcherFactory.instance().getMatcher(pRule, true);
                for (Match match : matcher.findMatches(pGraph)) {
                    // For each match we found we add a new pattern. We don't
                    // have to recompute any matches after the transformation
                    // because the dependency on patterns grows towards the
                    // depth of the type graph. So no patterns of the same
                    // layer can depend on each other.
                    PatternGraphRuleApplication app =
                        new PatternGraphRuleApplication(pGraph, match);
                    app.transform(true);
                }
            }
        }
    }

    /** Returns the pattern factory associated with this type graph. */
    public PatternFactory getPatternFactory() {
        return this.patternFactory;
    }

    /** Returns the rule factory associated with this type graph. */
    public RuleFactory getRuleFactory() {
        return this.ruleFactory;
    }

    private List<Node> match(Graph<?,?> graph, Set<TypeLabel> nodeLabels) {
        List<Node> result = new ArrayList<Node>(graph.nodeSet().size());
        for (Node node : graph.nodeSet()) {
            if (match(graph, node, nodeLabels)) {
                result.add(node);
            }
        }
        Collections.sort(result);
        return result;
    }

    private List<Edge> match(Graph<?,?> graph, Set<TypeLabel> srcLabels,
            TypeLabel edgeLabel, Set<TypeLabel> tgtLabels) {
        List<Edge> result = new ArrayList<Edge>();
        for (Edge edge : graph.labelEdgeSet(edgeLabel)) {
            if ((srcLabels.isEmpty() || match(graph, edge.source(), srcLabels))
                && (tgtLabels.isEmpty() || match(graph, edge.target(),
                    tgtLabels))) {
                result.add(edge);
            }
        }
        Collections.sort(result);
        return result;
    }

    private boolean match(Graph<?,?> graph, Node node, Set<TypeLabel> nodeLabels) {
        Set<TypeLabel> other = Util.getNodeLabels(graph, node);
        return nodeLabels.containsAll(other) && other.containsAll(nodeLabels);
    }

    private void createClosureRules() {
        for (int layer = 0; layer <= depth(); layer++) {
            for (TypeNode tNode : getLayerNodes(layer)) {
                HostGraph pattern = tNode.getPattern();
                PatternRule pRule = new PatternRule(pattern.getName(), this);
                RuleNode rTgt = pRule.addCreatorNode(tNode);
                for (TypeEdge tEdge : inEdgeSet(tNode)) {
                    RuleNode rSrc =
                        pRule.addRhsAsReader(getClosureRule(tEdge.source()));
                    pRule.addCreatorEdge(rSrc, tEdge, rTgt);
                }
                pRule.fixCommutativity();
                pRule.removeSpuriousNodes();
                this.closureRules.put(tNode, pRule);
            }
        }
    }

    private PatternRule getClosureRule(TypeNode tNode) {
        PatternRule result = this.closureRules.get(tNode);
        assert result != null;
        return result;
    }

    // ------------------------------------------------------------------------
    // Unsupported methods
    // ------------------------------------------------------------------------

    @Override
    public boolean removeNode(TypeNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeEdge(TypeEdge edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeNodeSet(Collection<? extends TypeNode> nodeSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addEdgeSetWithoutCheck(Collection<? extends TypeEdge> edgeSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeNodeWithoutCheck(TypeNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeNodeSetWithoutCheck(
            Collection<? extends TypeNode> nodeSet) {
        throw new UnsupportedOperationException();
    }

}
