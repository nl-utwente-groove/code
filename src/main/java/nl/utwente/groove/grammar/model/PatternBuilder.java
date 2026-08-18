/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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

package nl.utwente.groove.grammar.model;

import static nl.utwente.groove.grammar.aspect.AspectKind.CONNECT;
import static nl.utwente.groove.grammar.aspect.AspectKind.PARAM_ASK;
import static nl.utwente.groove.grammar.aspect.AspectKind.PRODUCT;
import static nl.utwente.groove.grammar.aspect.AspectKind.Category.ROLE;
import static nl.utwente.groove.grammar.aspect.AspectKind.Category.SORT;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.algebra.syntax.Variable;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.model.RuleModel.Index;
import nl.utwente.groove.grammar.model.RuleModel.RuleModelMap;
import nl.utwente.groove.grammar.rule.DefaultRuleNode;
import nl.utwente.groove.grammar.rule.LabelVar;
import nl.utwente.groove.grammar.rule.OperatorNode;
import nl.utwente.groove.grammar.rule.RuleEdge;
import nl.utwente.groove.grammar.rule.RuleElement;
import nl.utwente.groove.grammar.rule.RuleFactory;
import nl.utwente.groove.grammar.rule.RuleGraph;
import nl.utwente.groove.grammar.rule.RuleNode;
import nl.utwente.groove.grammar.rule.VariableNode;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.graph.EdgeComparator;
import nl.utwente.groove.graph.Element;
import nl.utwente.groove.graph.NodeComparator;
import nl.utwente.groove.util.Fixable;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * The pass that constructs the untyped per-level patterns of a rule:
 * converts the distributed aspect elements of every quantification level
 * into rule graphs split into LHS, RHS and NACs, together constituting a
 * {@link LevelPattern} tree. Fills the untyped model map as a side effect.
 * @author Arend Rensink
 * @version $Revision$
 */
class PatternBuilder {
    /** Constructs a builder for a given rule compiler.
     * @param compiler the compiler providing the compilation context
     * @param modelMap the (empty) untyped model map, filled by the builder
     */
    PatternBuilder(RuleCompiler compiler, RuleModelMap modelMap) {
        this.compiler = compiler;
        this.modelMap = modelMap;
    }

    /** Builds and returns the untyped level patterns for a given element
     * distribution, in the tree order of the indices. */
    SortedMap<Index,LevelPattern> build(LevelDistribution distribution) throws FormatException {
        SortedMap<Index,Level> levelMap = new TreeMap<>();
        SortedMap<Index,LevelPattern> result = new TreeMap<>();
        FormatErrorSet errors = new FormatErrorSet();
        for (LevelDistribution.Level level1 : distribution.getLevelMap().values()) {
            try {
                Index index = level1.getIndex();
                Level parent = index.isTopLevel()
                    ? null
                    : levelMap.get(index.parent);
                Level level = new Level(level1, parent);
                levelMap.put(index, level);
                result.put(index, level.pattern);
            } catch (FormatException e) {
                errors.addAll(e.getErrors());
            }
        }
        errors.throwException();
        return result;
    }

    /** Convenience method to retrieve the qualified rule name from the compiler. */
    private QualName getQualName() {
        return this.compiler.getQualName();
    }

    /** Convenience method to retrieve the grammar properties from the compiler. */
    private GrammarProperties getGrammarProperties() {
        return this.compiler.getGrammarProperties();
    }

    /** Convenience method to retrieve the type graph from the compiler. */
    private TypeGraph getTypeGraph() {
        return this.compiler.getTypeGraph();
    }

    /** Convenience method to test for injective matching. */
    private boolean isInjective() {
        return this.compiler.isInjective();
    }

    /** Convenience method to test for the rhs-as-nac grammar property. */
    private boolean isRhsAsNac() {
        return this.compiler.isRhsAsNac();
    }

    /** Convenience method to test for the check-creator-edges grammar property. */
    private boolean isCheckCreatorEdges() {
        return this.compiler.isCheckCreatorEdges();
    }

    /** The compiler providing the compilation context. */
    private final RuleCompiler compiler;
    /** Mapping from aspect graph elements to untyped rule elements. */
    private final RuleModelMap modelMap;
    /** Parallel-index allocator, shared between the levels of this rule. */
    private final ParallelIndexAllocator allocator = new ParallelIndexAllocator();

    /**
     * Class containing all rule elements on a given rule level,
     * differentiated by role (LHS, RHS and NACs).
     */
    private class Level {
        /**
         * Creates a new level, with a given index and parent level.
         * @param origin the level distribution data from which this level is created
         * @param parent the parent's level object, if this is not a top level
         */
        public Level(LevelDistribution.Level origin, Level parent) throws FormatException {
            this.factory = PatternBuilder.this.modelMap.getFactory();
            Index index = this.index = origin.index;
            this.parent = parent;
            this.isRule = index.isTopLevel();
            // initialise the rule data structures
            this.lhs = createGraph(getQualName() + "-" + index + "-lhs");
            this.rhs = createGraph(getQualName() + "-" + index + "-rhs");
            FormatErrorSet errors = new FormatErrorSet();
            try {
                if (origin.countNode != null) {
                    this.countNode = (VariableNode) getNodeImage(origin.countNode);
                    this.outputNodes.add(this.countNode);
                }
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
            for (AspectNode modelNode : origin.modelNodes) {
                try {
                    if (modelNode.has(ROLE) && !modelNode.has(PRODUCT)) {
                        processNode(modelNode);
                    }
                } catch (FormatException exc) {
                    errors.addAll(exc.getErrors());
                }
            }
            // if there are errors in the node map, don't try mapping the edges
            errors.throwException();
            for (AspectEdge modelEdge : origin.modelEdges) {
                try {
                    if (modelEdge.has(CONNECT)) {
                        addConnect(modelEdge);
                    } else if (modelEdge.has(SORT)) {
                        assert modelEdge.isOperator();
                        addOperator(modelEdge);
                    } else if (modelEdge.has(ROLE) && !modelEdge.isArgument()) {
                        processEdge(modelEdge);
                    }
                } catch (FormatException exc) {
                    errors.addAll(exc.getErrors());
                }
            }
            for (LabelVar modelVar : origin.modelVars.keySet()) {
                processVar(modelVar);
            }
            try {
                this.nacs.addAll(computeNacs());
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
            if (!index.isTopLevel()) {
                var originParent = origin.parent;
                assert originParent != null; // the level tree is congruent to the index tree
                this.parentVars.addAll(originParent.modelVars.keySet());
            }
            checkAttributes(errors);
            checkVariables(errors);
            errors.throwException();
            this.pattern = new LevelPattern(index, parent == null
                ? null
                : parent.pattern, this.lhs, this.rhs, this.nacs, this.countNode, this.outputNodes,
                this.colorMap, this.isRule);
        }

        private void processVar(LabelVar modelVar) {
            this.lhs.addVar(modelVar);
        }

        /**
         * Adds a node to the LHS, RHS or NAC node set, whichever is appropriate.
         */
        private void processNode(AspectNode modelNode) throws FormatException {
            AspectKind roleKind = modelNode.getKind(ROLE);
            assert roleKind != null;
            this.isRule |= roleKind.inLHS() != roleKind.inRHS();
            RuleNode ruleNode = getNodeImage(modelNode);
            boolean isAskNode = modelNode.has(PARAM_ASK);
            if (roleKind.inLHS() && !isAskNode) {
                this.lhs.addNode(ruleNode);
                if (roleKind.inRHS()) {
                    this.rhs.addNode(ruleNode);
                }
            } else {
                if (roleKind.inNAC()) {
                    // embargo node
                    this.nacNodeSet.add(ruleNode);
                }
                if (roleKind.inRHS()) {
                    // creator node
                    this.rhs.addNode(ruleNode);
                    if (isRhsAsNac() && !isAskNode) {
                        this.nacNodeSet.add(ruleNode);
                    }
                }
            }
            if (modelNode.hasColor()) {
                this.colorMap.put(ruleNode, modelNode.getColor());
            }
        }

        /**
         * Adds an edge to the LHS, RHS or NAC edge set, whichever is appropriate.
         */
        private void processEdge(AspectEdge modelEdge) throws FormatException {
            AspectKind roleKind = modelEdge.getKind(ROLE);
            assert roleKind != null;
            this.isRule |= roleKind.inLHS() != roleKind.inRHS();
            RuleEdge ruleEdge = getEdgeImage(modelEdge);
            if (ruleEdge == null) {
                // this was an argument or operation edge;
                // it has been processed by adding the info to the operator node
                return;
            }
            if (roleKind.inLHS()) {
                // flag indicating that the rule edge is fresh in the LHS
                boolean freshInLhs = this.lhs.addEdgeContext(ruleEdge);
                if (freshInLhs) {
                    if (roleKind.inRHS()) {
                        this.rhs.addEdgeContext(ruleEdge);
                    } else if (getTypeGraph().isNodeType(ruleEdge)
                        && this.rhs.containsNode(ruleEdge.source())) {
                        throw new FormatException("Node type label %s cannot be deleted",
                            ruleEdge.label().text(), modelEdge.source());
                    }
                } else {
                    if (!roleKind.inRHS()) {
                        // remove the edge from the RHS, if it was there
                        // (which is the case if it also exists as reader edge)
                        this.rhs.removeEdge(ruleEdge);
                    }
                }
            } else {
                if (roleKind.inNAC()) {
                    // embargo edge
                    this.nacEdgeSet.add(ruleEdge);
                }
                if (roleKind.inRHS()) {
                    // creator edge
                    if (getTypeGraph().isNodeType(ruleEdge)
                        && this.lhs.containsNode(ruleEdge.source())) {
                        throw new FormatException("Node type %s cannot be created",
                            ruleEdge.label(), modelEdge.source());
                    }
                    this.rhs.addEdgeContext(ruleEdge);
                    if (isRhsAsNac()) {
                        this.nacEdgeSet.add(ruleEdge);
                    } else if (isCheckCreatorEdges()
                        && modelEdge.source().has(ROLE, AspectKind::inLHS)
                        && modelEdge.target().has(ROLE, AspectKind::inLHS)) {
                        this.nacEdgeSet.add(ruleEdge);
                    }
                }
            }
        }

        /** Adds a NAC connection edge. */
        private void addConnect(AspectEdge connectEdge) throws FormatException {
            RuleNode node1 = getNodeImage(connectEdge.source());
            RuleNode node2 = getNodeImage(connectEdge.target());
            Set<RuleNode> nodeSet = new HashSet<>(Arrays.asList(node1, node2));
            this.connectMap.put(connectEdge, nodeSet);
        }

        private void addOperator(AspectEdge operatorEdge) throws FormatException {
            AspectNode productNode = operatorEdge.source();
            Operator operator = operatorEdge.getOperator();
            assert operator != null;
            if (productNode.getLevelNode() != null && operator.isIndeterminate()) {
                //                throw new FormatException(
                //                    "Indeterminate operator '%s' not allowed on quantified level "
                //                        + "(do a feature request if you want this constraint dropped!)",
                //                    operator.getName(), operatorEdge);
            }
            boolean embargo = productNode.has(ROLE, AspectKind::inNAC);
            List<VariableNode> arguments = new ArrayList<>();
            for (AspectNode argModelNode : productNode.getArgNodes()) {
                VariableNode argument = (VariableNode) getNodeImage(argModelNode);
                boolean argOnThisLevel = this.lhs.nodeSet().contains(argument);
                if (!(argOnThisLevel || embargo && this.nacNodeSet.contains(argument))) {
                    String nodeName = argModelNode.hasId()
                        ? "'" + argModelNode.getId() + "' "
                        : "";
                    if (argModelNode.has(PARAM_ASK)) {
                        throw new FormatException(
                            "User input value %s not supported as expression argument", nodeName,
                            argModelNode, operatorEdge);
                    } else {
                        throw new FormatException(
                            "Argument %s does not exist on the level of the operator '%s'",
                            nodeName, operator.getName(), argModelNode, operatorEdge);
                    }
                }
                arguments.add(argument);
            }
            AspectNode targetModelNode = operatorEdge.target();
            VariableNode target = (VariableNode) getNodeImage(targetModelNode);
            boolean setOperator = operator.isVarArgs();
            if (!(setOperator || this.lhs.nodeSet().contains(target)
                || embargo && this.nacNodeSet.contains(target))) {
                String nodeName = targetModelNode.hasId()
                    ? targetModelNode.getId()
                    : targetModelNode.toString();
                throw new FormatException(
                    "Target of operator '%s' does not exist on the level of the operator edge",
                    nodeName, operator.getName(), operatorEdge);
            }
            // make sure that set operator targets appear on the parent level already
            if (setOperator) {
                if (!(this.parent != null && this.parent.lhs.nodeSet().contains(target))) {
                    throw new FormatException(
                        "Target of set operator '%s' must be defined on the parent level",
                        operator.getName(), operatorEdge);
                }
                if (!getIndex().isUniversal()) {
                    throw new FormatException(
                        "Argument of set operator '%s' must be universally quantified",
                        operator.getName(), operatorEdge);
                }
                if (!operator.isZeroArgs() && !getIndex().isPositive()) {
                    throw new FormatException(
                        "Argument of set operator '%s' needs a non-vacuous quantification",
                        operator.getName(), operatorEdge);
                }
                // a set operator argument is an output node of the condition
                this.outputNodes.add(arguments.get(0));
            }
            RuleNode opNode = this.factory
                .createOperatorNode(productNode.getNumber(), operator, arguments, target);
            Level level = setOperator
                ? this.parent
                : this;
            if (operatorEdge.has(ROLE, AspectKind::inNAC)) {
                level.nacNodeSet.add(opNode);
            } else {
                level.lhs.addNode(opNode);
                level.rhs.addNode(opNode);
            }
        }

        /** Constructs the NACs for this rule. */
        private List<RuleGraph> computeNacs() throws FormatException {
            List<RuleGraph> result = new ArrayList<>();
            // add the nacs to the rule
            // find connected sets of NAC nodes, taking the
            // connection edges into account
            for (Cell cell : getConnectedSets()) {
                // construct the NAC graph
                RuleGraph nac = createGraph(this.lhs.getName() + "-nac-" + result.size());
                for (RuleNode node : cell.getNodes()) {
                    nac.addNode(node);
                    if (node instanceof OperatorNode) {
                        nac.addNodeSet(((OperatorNode) node).getArguments());
                        nac.addNode(((OperatorNode) node).getTarget());
                    }
                }
                for (RuleEdge edge : cell.getEdges()) {
                    nac.addEdgeContext(edge);
                }
                result.add(nac);
            }
            return result;
        }

        /**
         * Partitions a set of graph elements into its maximal connected subsets.
         * The set does not necessarily contain all endpoints of edges it contains.
         * A subset is connected if there is a chain of edges and edge endpoints,
         * all of which are in the set, between all pairs of elements in the set.
         * @return The set of maximal connected subsets of {@link #nacNodeSet} and
         * {@link #nacEdgeSet}
         */
        private SortedSet<Cell> getConnectedSets() throws FormatException {
            // mapping from nodes of elementSet to sets of connected elements
            Map<Element,Cell> result = new HashMap<>();
            for (RuleNode node : this.nacNodeSet) {
                Cell nodeCell = new Cell();
                nodeCell.add(node);
                result.put(node, nodeCell);
            }
            // merge cells connected by an operator
            for (RuleNode node : this.nacNodeSet) {
                if (node instanceof OperatorNode opNode) {
                    Cell nodeCell = result.get(opNode);
                    assert nodeCell != null; // filled for all NAC nodes above
                    for (RuleNode argNode : opNode.getArguments()) {
                        Cell argCell = result.get(argNode);
                        if (argCell != null) {
                            nodeCell.addAll(argCell);
                        }
                    }
                    VariableNode target = opNode.getTarget();
                    Cell targetCell = result.get(target);
                    if (targetCell != null) {
                        nodeCell.addAll(targetCell);
                    }
                    for (RuleElement elem : nodeCell) {
                        result.put(elem, nodeCell);
                    }
                }
            }
            // merge cells connected by an edge
            for (RuleEdge edge : this.nacEdgeSet) {
                Cell edgeCell = new Cell();
                edgeCell.add(edge);
                Cell sourceCell = result.get(edge.source());
                if (sourceCell != null) {
                    edgeCell.addAll(sourceCell);
                }
                Cell targetCell = result.get(edge.target());
                if (targetCell != null) {
                    edgeCell.addAll(targetCell);
                }
                for (RuleElement elem : edgeCell) {
                    result.put(elem, edgeCell);
                }
            }
            // merge cells connected by an explicit connection
            for (Map.Entry<AspectEdge,Set<RuleNode>> connection : this.connectMap.entrySet()) {
                // find the (separate) cells for the target nodes of the connect edge
                Cell newCell = new Cell();
                for (RuleNode node : connection.getValue()) {
                    Cell nodeCell = result.get(node);
                    if (nodeCell == null) {
                        throw new FormatException("Connect edge should be between distinct NACs",
                            connection.getKey());
                    }
                    newCell.addAll(nodeCell);
                }
                for (RuleElement elem : newCell) {
                    result.put(elem, newCell);
                }
            }
            return new TreeSet<>(result.values());
        }

        private class Cell extends HashSet<RuleElement> implements Comparable<Cell>, Fixable {
            public Cell() {
                // empty
            }

            @Override
            public boolean setFixed() {
                boolean result = !this.fixed;
                this.fixed = true;
                return result;
            }

            @Override
            public boolean isFixed() {
                return this.fixed;
            }

            @Override
            public boolean add(RuleElement e) {
                testFixed(false);
                return super.add(e);
            }

            @Override
            public boolean remove(Object o) {
                testFixed(false);
                return super.remove(o);
            }

            @Override
            public void clear() {
                testFixed(false);
                super.clear();
            }

            /**
             * Returns the set of nodes in this cell. Only call after
             * the cell has been completely fixed.
             */
            public SortedSet<RuleNode> getNodes() {
                setFixed();
                if (this.nodes == null) {
                    this.nodes = computeNodes();
                }
                return this.nodes;
            }

            private SortedSet<RuleNode> computeNodes() {
                TreeSet<RuleNode> result = new TreeSet<>(NodeComparator.instance());
                for (RuleElement elem : this) {
                    if (elem instanceof RuleNode) {
                        result.add((RuleNode) elem);
                    }
                }
                return result;
            }

            /**
             * Returns the set of edges in this cell. Only call after
             * the cell has been completely fixed.
             */
            public SortedSet<RuleEdge> getEdges() {
                setFixed();
                if (this.edges == null) {
                    this.edges = computeEdges();
                }
                return this.edges;
            }

            private SortedSet<RuleEdge> computeEdges() {
                TreeSet<RuleEdge> result = new TreeSet<>(EdgeComparator.instance());
                for (RuleElement elem : this) {
                    if (elem instanceof RuleEdge) {
                        result.add((RuleEdge) elem);
                    }
                }
                return result;
            }

            @Override
            public int compareTo(Cell o) {
                // comparison of node set size
                int result = getNodes().size() - o.getNodes().size();
                if (result != 0) {
                    return result;
                }
                // comparison of edge set size
                result = getEdges().size() - o.getEdges().size();
                if (result != 0) {
                    return result;
                }
                // lexicographical comparison of the ordered sets of nodes
                Iterator<RuleNode> myNodeIter = getNodes().iterator();
                Iterator<RuleNode> otherNodeIter = o.getNodes().iterator();
                Comparator<? super RuleNode> nodeComp = getNodes().comparator();
                assert nodeComp != null; // the node set is created with an explicit comparator
                while (myNodeIter.hasNext()) {
                    result = nodeComp.compare(myNodeIter.next(), otherNodeIter.next());
                    if (result != 0) {
                        return result;
                    }
                }
                // lexicographical comparison of the ordered sets of edges
                Iterator<RuleEdge> myEdgeIter = getEdges().iterator();
                Iterator<RuleEdge> otherEdgeIter = o.getEdges().iterator();
                Comparator<? super RuleEdge> edgeComp = getEdges().comparator();
                assert edgeComp != null; // the edge set is created with an explicit comparator
                while (myEdgeIter.hasNext()) {
                    result = edgeComp.compare(myEdgeIter.next(), otherEdgeIter.next());
                    if (result != 0) {
                        return result;
                    }
                }
                return result;
            }

            private boolean fixed = false;
            private SortedSet<RuleNode> nodes;
            private SortedSet<RuleEdge> edges;
        }

        /**
         * Checks if all product nodes have all their arguments.
         */
        private void checkAttributes(FormatErrorSet errors) {
            // check if product nodes have all their arguments (on this level)
            for (RuleNode prodNode : this.lhs.nodeSet()) {
                if (!(prodNode instanceof OperatorNode opNode)) {
                    continue;
                }
                for (RuleNode argNode : opNode.getArguments()) {
                    if (!this.lhs.nodeSet().contains(argNode)) {
                        errors
                            .add("Argument must occur on the level of the product node", opNode,
                                 argNode);

                    }
                }
                RuleNode opTarget = opNode.getTarget();
                if (!this.lhs.nodeSet().contains(opTarget)) {
                    errors
                        .add("Operation target must occur on the level of the product node", opNode,
                             opTarget);

                }
            }
        }

        /**
         * Checks if all label variables are bound
         */
        private void checkVariables(FormatErrorSet errors) {
            Map<LabelVar,Set<RuleElement>> allVars = new HashMap<>();
            allVars.putAll(this.lhs.varMap());
            allVars.putAll(this.rhs.varMap());
            for (RuleGraph nac : this.nacs) {
                allVars.putAll(nac.varMap());
            }
            Map<String,LabelVar> varNames = new HashMap<>();
            for (Map.Entry<LabelVar,Set<RuleElement>> varEntry : allVars.entrySet()) {
                LabelVar var = varEntry.getKey();
                LabelVar oldVar = varNames.put(var.getKey(), var);
                if (oldVar != null && !oldVar.equals(var)) {
                    errors
                        .add("Duplicate variable '%s' for %s and %s labels", var,
                             var.getKind().getDescription(false),
                             oldVar.getKind().getDescription(false), varEntry.getValue().toArray());
                }
            }
            allVars.keySet().removeAll(this.lhs.getBoundVars());
            allVars.keySet().removeAll(this.parentVars);
            for (Map.Entry<LabelVar,Set<RuleElement>> varEntry : allVars.entrySet()) {
                LabelVar var = varEntry.getKey();
                errors.add("Unassigned label variable %s", var, varEntry.getValue().toArray());
            }
        }

        /**
         * Lazily creates and returns a rule image for a given model node.
         * @param modelNode the node for which an image is to be created
         * @throws FormatException if <code>node</code> does not occur in a
         *         correct way in <code>context</code>
         */
        private RuleNode getNodeImage(AspectNode modelNode) throws FormatException {
            RuleNode result = PatternBuilder.this.modelMap.getNode(modelNode);
            if (result == null) {
                PatternBuilder.this.modelMap.putNode(modelNode, result = computeNodeImage(modelNode));
            }
            return result;
        }

        /**
         * Lazily creates and returns a rule image for a given model edge.
         * @param modelEdge the node for which an image is to be created
         * @return the rule edge corresponding to <code>viewEdge</code>; may be
         *         <code>null</code>
         * @throws FormatException if <code>node</code> does not occur in a
         *         correct way in <code>context</code>
         */
        private RuleEdge getEdgeImage(AspectEdge modelEdge) throws FormatException {
            RuleEdge result = PatternBuilder.this.modelMap.getEdge(modelEdge);
            if (result == null) {
                result = computeEdgeImage(modelEdge, PatternBuilder.this.modelMap.nodeMap());
                if (result != null) {
                    PatternBuilder.this.modelMap.putEdge(modelEdge, result);
                }
            }
            return result;
        }

        /**
         * Creates an image for a given aspect node. Node numbers are copied.
         * @param node the node for which an image is to be created
         * @return the fresh node
         * @throws FormatException if <code>node</code> does not occur in a correct
         *         way in <code>context</code>
         */
        private RuleNode computeNodeImage(AspectNode node) throws FormatException {
            RuleNode result;
            if (node.has(Category.PARAM) && !this.index.isTopLevel()) {
                throw new FormatException("Parameter '%d' only allowed on top existential level",
                    node.getNumber(), node);
            }
            int nr = node.getNumber();
            AspectKind sortKind = node.getKind(SORT);
            if (sortKind != null) {
                Expression term;
                String id = node.getId();
                if (node.hasExpression()) {
                    term = node.getExpression();
                    assert term instanceof Constant;
                } else {
                    String varName = id == null
                        ? VariableNode.TO_STRING_PREFIX + nr
                        : id;
                    term = new Variable(varName, sortKind.getSort());
                }
                VariableNode image = this.factory.createVariableNode(nr, term);
                if (id != null) {
                    image.setId(id);
                }
                result = image;
            } else {
                DefaultRuleNode image = (DefaultRuleNode) this.factory.createNode(nr);
                result = image;
            }
            return result;
        }

        /**
         * Creates an edge by copying a given model edge under a given node mapping. The
         * mapping is assumed to have images for all end nodes.
         * @param edge the edge for which an image is to be created
         * @param elementMap the mapping of the end nodes
         * @return the new edge
         * @throws FormatException if <code>edge</code> does not occur in a correct
         *         way in <code>context</code>
         */
        private RuleEdge computeEdgeImage(AspectEdge edge,
                                          Map<AspectNode,? extends RuleNode> elementMap) throws FormatException {
            assert edge.getRuleLabel() != null : String
                .format("Edge '%s' does not belong in model", edge);
            RuleNode sourceImage = elementMap.get(edge.source());
            if (sourceImage == null) {
                throw new FormatException(
                    "Cannot compute image of '%s'-edge: source node does not have image",
                    edge.label(), edge.source());
            }
            RuleNode targetImage = elementMap.get(edge.target());
            if (targetImage == null) {
                throw new FormatException(
                    "Cannot compute image of '%s'-edge: target node does not have image",
                    edge.label(), edge.target());
            }
            RuleEdge result = this.factory.createEdge(sourceImage, edge.getRuleLabel(), targetImage);
            // in multigraph mode, every aspect edge gets its own parallel
            // index for its content, so that copies declared by distinct
            // aspect edges never coalesce; in particular, created copies are
            // always fresh with respect to matched copies. Embargo edges are
            // exempt: they declare no copies of their own
            if (getGrammarProperties().getParallelMode().isMulti()
                && edge.has(ROLE, k -> k != AspectKind.EMBARGO)) {
                int index = PatternBuilder.this.allocator.getIndex(edge, result);
                if (index > 0) {
                    result = this.factory
                        .createEdge(sourceImage, edge.getRuleLabel(), targetImage, index);
                }
            }
            return result;
        }

        /**
         * Callback method to create an untyped graph that can serve as LHS or RHS of a rule.
         * The graph is non-simple if the grammar allows parallel edges.
         */
        private RuleGraph createGraph(String name) {
            return new RuleGraph(name, isInjective(),
                !getGrammarProperties().getParallelMode().isMulti(), this.factory);
        }

        @Override
        public String toString() {
            return String.format("Rule %s, level %s, untyped pattern", getQualName(), getIndex());
        }

        /** Returns the index of this level. */
        public final Index getIndex() {
            return this.index;
        }

        private final RuleFactory factory;
        /** The untyped pattern constructed by this level. */
        private final LevelPattern pattern;
        /** Index of this level. */
        private final Index index;
        /** Parent level. */
        private final Level parent;
        /** Map of all connect edges on this level. */
        private final Map<AspectEdge,Set<RuleNode>> connectMap = new HashMap<>();
        /** The rule node registering the match count. */
        private VariableNode countNode;
        /** Condition output nodes. */
        private final Set<VariableNode> outputNodes = new HashSet<>();
        /** Map from rule nodes to declared colours. */
        private final Map<RuleNode,Color> colorMap = new HashMap<>();
        /** Flag indicating that modifiers have been found at this level. */
        private boolean isRule;
        /** The left hand side graph of the rule. */
        private final RuleGraph lhs;
        /** The right hand side graph of the rule. */
        private final RuleGraph rhs;
        /** The set of nodes appearing in NACs. */
        private final Set<RuleNode> nacNodeSet = new HashSet<>();
        /** The set of edges appearing in NACs. */
        private final Set<RuleEdge> nacEdgeSet = new HashSet<>();
        /** Collection of NAC graphs. */
        private final List<RuleGraph> nacs = new ArrayList<>();
        /** Variables bound at the parent level. */
        private final Set<LabelVar> parentVars = new HashSet<>();
    }

    /**
     * Allocator of parallel-edge indices for the aspect edges of a multigraph
     * rule: every aspect edge gets its own parallel index for its content, so
     * that the copies declared by distinct aspect edges never coalesce — in
     * particular, created copies are always fresh with respect to matched
     * copies. Shared between all levels of one rule, so that an aspect edge
     * occurring at several quantification levels keeps the same copy.
     */
    static private class ParallelIndexAllocator {
        /**
         * Returns the parallel index allocated to a given aspect edge,
         * allocating the next free index for its content on the first call.
         * @param modelEdge the aspect edge for which the index is allocated
         * @param edge0 the index-0 rule edge image of the aspect edge,
         * serving as the content representative
         */
        int getIndex(AspectEdge modelEdge, RuleEdge edge0) {
            Integer result = this.indexMap.get(modelEdge);
            if (result == null) {
                result = this.nextIndexMap.getOrDefault(edge0, 0);
                this.indexMap.put(modelEdge, result);
                this.nextIndexMap.put(edge0, result + 1);
            }
            return result;
        }

        /** Map from aspect edges to their allocated index. */
        private final Map<AspectEdge,Integer> indexMap = new HashMap<>();
        /** Map from index-0 content representatives to the next free index. */
        private final Map<RuleEdge,Integer> nextIndexMap = new HashMap<>();
    }
}
