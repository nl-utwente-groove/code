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
package groove.abstraction.pattern.trans;

import groove.abstraction.MyHashMap;
import groove.abstraction.MyHashSet;
import groove.abstraction.pattern.shape.TypeEdge;
import groove.abstraction.pattern.shape.TypeGraph;
import groove.abstraction.pattern.shape.TypeNode;
import groove.trans.HostNode;
import groove.trans.Rule;
import groove.util.Pair;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Rule to transform pattern graphs.
 * 
 * @author Eduardo Zambon
 */
public final class PatternRule {

    private final String name;

    private final Rule sRule;

    private final TypeGraph type;

    private final boolean closure;

    private final boolean modifying;

    private final PatternRuleGraph lhs;

    private final PatternRuleGraph rhs;

    private int maxNodeNr;
    private int maxEdgeNr;

    private RuleNode[] eraserNodes;
    private RuleEdge[] eraserEdges;
    private RuleNode[] creatorNodes;
    private RuleEdge[] creatorEdges;

    /** Constructor for closure rules. */
    public PatternRule(String name, TypeGraph type) {
        this.name = name;
        this.sRule = null;
        this.type = type;
        this.closure = true;
        this.modifying = true;
        this.lhs = new PatternRuleGraph(name + "-lhs");
        this.rhs = new PatternRuleGraph(name + "-rhs");
    }

    /** Constructor that lifts a given simple rule to a pattern rule. */
    public PatternRule(Rule sRule, TypeGraph type) {
        this.name = sRule.getLastName();
        this.sRule = sRule;
        this.type = type;
        this.closure = false;
        this.modifying = sRule.isModifying();
        this.lhs = new PatternRuleGraph(this.name + "-lhs");
        this.rhs = new PatternRuleGraph(this.name + "-rhs");
    }

    @Override
    public String toString() {
        return "Pattern rule: " + this.name + "\n" + this.lhs + this.rhs;
    }

    /** Returns true if this is a special rule used for closure computation. */
    public boolean isClosure() {
        return this.closure;
    }

    /** Returns true if this rule can modify a host graph. */
    public boolean isModifying() {
        return this.modifying;
    }

    /** Returns the LHS of this rule. */
    public PatternRuleGraph lhs() {
        return this.lhs;
    }

    /** Returns the RHS of this rule. */
    public PatternRuleGraph rhs() {
        return this.rhs;
    }

    /** Creates and returns a new rule node of the given type and adds it to the RHS. */
    public RuleNode addCreatorNode(TypeNode tNode) {
        RuleNode result = createNode(tNode);
        addToRhs(result);
        return result;
    }

    /** Creates and returns a new rule edge of the given type and adds it to the RHS. */
    public RuleEdge addCreatorEdge(RuleNode rSrc, TypeEdge tEdge, RuleNode rTgt) {
        RuleEdge result = createEdge(rSrc, tEdge, rTgt);
        addToRhs(result);
        return result;
    }

    /** Creates and returns a new rule node of the given type and adds it to the LHS. */
    public RuleNode addEraserNode(TypeNode tNode) {
        RuleNode result = createNode(tNode);
        addToLhs(result);
        return result;
    }

    /** Creates and returns a new rule edge of the given type and adds it to the LHS. */
    public RuleEdge addEraserEdge(RuleNode rSrc, TypeEdge tEdge, RuleNode rTgt) {
        RuleEdge result = createEdge(rSrc, tEdge, rTgt);
        addToLhs(result);
        return result;
    }

    /** Creates and returns a new rule node of the given type and adds it to the LHS and RHS. */
    public RuleNode addReaderNode(TypeNode tNode) {
        RuleNode result = createNode(tNode);
        addToLhs(result);
        addToRhs(result);
        return result;
    }

    /** Creates and returns a new rule edge of the given type and adds it to the LHS and RHS. */
    public RuleEdge addReaderEdge(RuleNode rSrc, TypeEdge tEdge, RuleNode rTgt) {
        RuleEdge result = createEdge(rSrc, tEdge, rTgt);
        addToLhs(result);
        addToRhs(result);
        return result;
    }

    /**
     * Adds the entire RHS of the given rule as reader elements of this rule.
     * Returns the newly created rule node associated with the first creator
     * element of the given rule.
     */
    public RuleNode addRhsAsReader(PatternRule pRule) {
        assert isClosure();
        PatternRuleGraph rGraph = pRule.rhs();
        Map<RuleNode,RuleNode> newNodeMap = new MyHashMap<RuleNode,RuleNode>();
        for (RuleNode rNode : rGraph.nodeSet()) {
            RuleNode addedNode = addReaderNode(rNode.getType());
            newNodeMap.put(rNode, addedNode);
        }
        for (RuleEdge rEdge : rGraph.edgeSet()) {
            addReaderEdge(newNodeMap.get(rEdge.source()), rEdge.getType(),
                newNodeMap.get(rEdge.target()));
        }
        return newNodeMap.get(pRule.getCreatorNodes()[0]);
    }

    private void addToLhs(RuleNode rNode) {
        this.lhs.addNode(rNode);
    }

    private void addToLhs(RuleEdge rEdge) {
        this.lhs.addEdge(rEdge);
    }

    private void addToRhs(RuleNode rNode) {
        this.rhs.addNode(rNode);
    }

    private void addToRhs(RuleEdge rEdge) {
        this.rhs.addEdge(rEdge);
    }

    private RuleNode createNode(TypeNode tNode) {
        return getFactory().createNode(this.maxNodeNr++, tNode);
    }

    private RuleEdge createEdge(RuleNode rSrc, TypeEdge tEdge, RuleNode rTgt) {
        return getFactory().createEdge(this.maxEdgeNr++, rSrc, tEdge, rTgt);
    }

    private RuleFactory getFactory() {
        return this.type.getRuleFactory();
    }

    /** Returns the pattern type graph associated with this rule. */
    public TypeGraph getTypeGraph() {
        return this.type;
    }

    /** Basic getter method. */
    public RuleNode[] getEraserNodes() {
        if (this.eraserNodes == null) {
            this.eraserNodes = computeEraserNodes();
        }
        return this.eraserNodes;
    }

    /** Basic getter method. */
    public RuleEdge[] getEraserEdges() {
        if (this.eraserEdges == null) {
            this.eraserEdges = computeEraserEdges();
        }
        return this.eraserEdges;
    }

    /** Basic getter method. */
    public RuleNode[] getCreatorNodes() {
        if (this.creatorNodes == null) {
            this.creatorNodes = computeCreatorNodes();
        }
        return this.creatorNodes;
    }

    /** Basic getter method. */
    public RuleEdge[] getCreatorEdges() {
        if (this.creatorEdges == null) {
            this.creatorEdges = computeCreatorEdges();
        }
        return this.creatorEdges;
    }

    private RuleNode[] computeEraserNodes() {
        Set<RuleNode> result = new MyHashSet<RuleNode>();
        result.addAll(lhs().nodeSet());
        result.removeAll(rhs().nodeSet());
        return result.toArray(new RuleNode[result.size()]);
    }

    private RuleEdge[] computeEraserEdges() {
        Set<RuleEdge> result = new MyHashSet<RuleEdge>();
        result.addAll(lhs().edgeSet());
        result.removeAll(rhs().edgeSet());
        return result.toArray(new RuleEdge[result.size()]);
    }

    private RuleNode[] computeCreatorNodes() {
        Set<RuleNode> result = new MyHashSet<RuleNode>();
        result.addAll(rhs().nodeSet());
        result.removeAll(lhs().nodeSet());
        return result.toArray(new RuleNode[result.size()]);
    }

    private RuleEdge[] computeCreatorEdges() {
        Set<RuleEdge> result = new MyHashSet<RuleEdge>();
        result.addAll(rhs().edgeSet());
        result.removeAll(lhs().edgeSet());
        return result.toArray(new RuleEdge[result.size()]);
    }

    /** Makes the LHS and RHS commuting by merging nodes on layer 0. */
    public void fixCommutativity() {
        assert isClosure();

        if (rhs().depth() <= 1) {
            return;
        }

        RuleNode creatorNode =
            rhs().getLayerNodes(rhs().depth()).iterator().next();
        Map<RuleNode,RuleNode> replacementMap =
            new MyHashMap<RuleNode,RuleNode>();
        // For each simple node in the pattern.
        for (HostNode sNode : creatorNode.getPattern().nodeSet()) {
            Set<RuleEdge> coverEdges =
                rhs().getCoveringEdges(creatorNode, sNode);
            List<Pair<RuleNode,HostNode>> ancestors =
                rhs().getAncestors(coverEdges, sNode);
            if (ancestors.size() > 1) {
                Iterator<Pair<RuleNode,HostNode>> it = ancestors.iterator();
                RuleNode toKeep = it.next().one();
                while (it.hasNext()) {
                    replacementMap.put(it.next().one(), toKeep);
                }
            }
        }
        // For each entry in the replacement map.
        for (Entry<RuleNode,RuleNode> entry : replacementMap.entrySet()) {
            mergeNodes(entry.getKey(), entry.getValue());
        }

        assert lhs().isWellFormed();
        assert lhs().isCommuting();
        assert rhs().isWellFormed();
        assert rhs().isCommuting();
    }

    private void mergeNodes(RuleNode from, RuleNode to) {
        assert from.isNodePattern();
        assert to.isNodePattern();
        for (RuleEdge rEdge : rhs().outEdgeSet(from)) {
            addReaderEdge(to, rEdge.getType(), rEdge.target());
        }
        lhs().removeNode(from);
        rhs().removeNode(from);
    }

    /** Returns the simple rule out of which this pattern rule was created. */
    public Rule getSimpleRule() {
        return this.sRule;
    }

    /** Basic getter. */
    public String getName() {
        return this.name;
    }

}
