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
import static nl.utwente.groove.grammar.aspect.AspectKind.PRODUCT;
import static nl.utwente.groove.grammar.aspect.AspectKind.REMARK;
import static nl.utwente.groove.grammar.aspect.AspectKind.Category.ROLE;
import static nl.utwente.groove.grammar.aspect.AspectKind.Category.SORT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Operator;
import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectElement;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.model.RuleModel.Index;
import nl.utwente.groove.grammar.rule.LabelVar;
import nl.utwente.groove.grammar.rule.RuleLabel;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Distribution of the elements of a rule graph over its quantification
 * levels: for every level index, a {@link Level} holding the aspect nodes and
 * edges on that level, the label variables they bind, and the match count
 * node of universal levels. The elements are not yet differentiated by role.
 * A data structure constructed by {@link #from}.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
class LevelDistribution {
    /** Constructs the distribution for a given rule source graph. */
    private LevelDistribution(RuleCompiler compiler, AspectGraph source,
                              LevelIndexTree indexTree) throws FormatException {
        this.compiler = compiler;
        this.indexTree = indexTree;
        this.levelMap = buildLevels(source);
    }

    /** Distributes the elements of the source graph over the levels. */
    private SortedMap<Index,Level> buildLevels(AspectGraph source) throws FormatException {
        FormatErrorSet errors = new FormatErrorSet();
        // Set the parentage in tree preorder
        // Build the level data map,
        // in the tree-order of the indices
        SortedMap<Index,Level> result = new TreeMap<>();
        for (Index index : this.indexTree.getIndices()) {
            Level parentLevel = index.isTopLevel()
                ? null
                : result.get(index.getParent());
            Level level = new Level(index, parentLevel);
            result.put(index, level);
        }
        // initialise the match count nodes and check that they are defined at super-levels
        for (Map.Entry<Index,AspectNode> matchCountEntry : this.indexTree
            .getMatchCountMap()
            .entrySet()) {
            AspectNode matchCount = matchCountEntry.getValue();
            Index definedAt = getLevel(result, matchCount).getIndex();
            Index usedAt = matchCountEntry.getKey();
            if (!definedAt.higherThan(usedAt) || definedAt.equals(usedAt)) {
                errors.add("Match count not defined at appropriate level", matchCount);
            }
            Level level = result.get(usedAt);
            assert level != null; // all indices have a level
            // add the match count node to all intermediate levels
            // (between definition and usage)
            Index addTo = usedAt.getParent();
            while (addTo != null && !addTo.equals(definedAt)) {
                var addToLevel = result.get(addTo);
                assert addToLevel != null; // all indices have a level
                addToLevel.addNode(matchCount);
                addTo = addTo.getParent();
            }
            level.setMatchCount(matchCount);
        }
        // add nodes to nesting data structures
        for (AspectNode node : source.nodeSet()) {
            if (!node.has(REMARK) && !node.has(Category.NESTING)) {
                getLevel(result, node).addNode(node);
            }
        }
        // add edges to nesting data structures
        for (AspectEdge edge : source.edgeSet()) {
            try {
                if (!edge.has(REMARK) && (edge.has(CONNECT) || !edge.has(Category.NESTING))) {
                    getLevel(result, edge).addEdge(edge);
                }
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        Map<LabelVar,Set<AspectEdge>> modelVarMap = new HashMap<>();
        for (Level level : result.values()) {
            modelVarMap.putAll(level.modelVars);
        }
        Map<String,LabelVar> nameVarMap = new HashMap<>();
        for (Map.Entry<LabelVar,Set<AspectEdge>> varEntry : modelVarMap.entrySet()) {
            LabelVar var = varEntry.getKey();
            LabelVar oldVar = nameVarMap.put(var.getName(), var);
            if (oldVar != null && !oldVar.equals(var)) {
                var oldVarEdges = modelVarMap.get(oldVar);
                assert oldVarEdges != null; // oldVar was taken from this map
                errors
                    .add("Duplicate variable '%s' for %s and %s labels", var,
                         var.getKind().getDescription(false),
                         oldVar.getKind().getDescription(false), varEntry.getValue().toArray(),
                         oldVarEdges.toArray());
            }
        }
        for (Level level : result.values()) {
            level.setFixed();
        }
        errors.throwException();
        return result;
    }

    /**
     * Returns the maximum (i.e., lowest-level) level of this and another,
     * given level; or {@code null} if neither is smaller than the other.
     */
    private @Nullable Level max(Level first, Level other) {
        if (first.index.higherThan(other.index)) {
            return other;
        } else if (other.index.higherThan(first.index)) {
            return first;
        } else {
            return null;
        }
    }

    /**
     * Returns the quantification level of a given aspect rule node.
     * @param node the node for which the quantification level is to be
     *        determined
     * @return the level for {@code node}; non-null
     */
    private Level getLevel(Map<Index,Level> levelMap, AspectNode node) {
        Level result = getNodeLevelMap().get(node);
        if (result == null) {
            // find the corresponding quantifier node
            AspectNode nestingNode = node.getLevelNode();
            Index index = nestingNode == null
                ? this.indexTree.getTopLevelIndex()
                : this.indexTree.getIndex(nestingNode);
            assert index != null : String.format("No valid nesting level found for %s", node);
            result = levelMap.get(index);
            assert result != null : String
                .format("Level map %s does not contain entry for index %s", levelMap, index);
            getNodeLevelMap().put(node, result);
        }
        return result;
    }

    /**
     * Returns the quantification level of a given aspect rule edge.
     * @param edge the edge for which the quantification level is to be
     *        determined
     */
    private Level getLevel(Map<Index,Level> levelMap, AspectEdge edge) throws FormatException {
        Level sourceLevel = getLevel(levelMap, edge.source());
        Level targetLevel = getLevel(levelMap, edge.target());
        @Nullable
        Level result = max(sourceLevel, targetLevel);
        // if one of the end nodes is a NAC, it must be the max of the two
        if (edge.source().has(Category.ROLE, AspectKind::inNAC) && !sourceLevel.equals(result)
            || edge.target().has(Category.ROLE, AspectKind::inNAC)
                && !targetLevel.equals(result)) {
            result = null;
        }
        if (result == null) {
            throw new FormatException("Source and target of edge %s have incompatible nesting",
                edge);
        }
        String levelName = edge.getLevelName();
        if (levelName != null) {
            Index edgeLevelIndex = this.indexTree.getIndex(levelName);
            if (edgeLevelIndex == null) {
                throw new FormatException("Undefined nesting level '%s' in edge %s", levelName,
                    edge);
            }
            var edgeLevel = levelMap.get(edgeLevelIndex);
            assert edgeLevel != null; // all indices have a level
            result = max(result, edgeLevel);
            if (result == null) {
                throw new FormatException(
                    "Nesting level %s in edge %s is incompatible with end nodes", levelName, edge);
            }
        }
        return result;
    }

    /**
     * Lazily creates and returns the mapping from rule model nodes to the
     * corresponding quantification levels.
     */
    private Map<AspectNode,Level> getNodeLevelMap() {
        var result = this.nodeLevelMap;
        if (result == null) {
            this.nodeLevelMap = result = new HashMap<>();
        }
        return result;
    }

    /** Returns the mapping from level indices to per-level element data,
     * in the tree order of the indices. */
    SortedMap<Index,Level> getLevelMap() {
        return this.levelMap;
    }

    /** The compiler providing the compilation context. */
    private final RuleCompiler compiler;
    /** The quantification level index tree of the rule. */
    private final LevelIndexTree indexTree;
    /** Mapping from level indices to per-level element data, in tree order. */
    private final SortedMap<Index,Level> levelMap;
    /** Mapping from model nodes to the corresponding nesting level. */
    private @Nullable Map<AspectNode,Level> nodeLevelMap;

    /** Builds and returns the level distribution of a given rule source graph.
     * @param compiler the compiler providing the compilation context
     * @param source the (normalised) source graph of the rule
     * @param indexTree the level index tree of the rule
     */
    static LevelDistribution from(RuleCompiler compiler, AspectGraph source,
                                  LevelIndexTree indexTree) throws FormatException {
        return new LevelDistribution(compiler, source, indexTree);
    }

    /**
     * The rule model elements on a given quantification level.
     */
    class Level {
        /**
         * Creates a new level, with a given index and parent level.
         * @param index the index of the new level
         * @param parent the parent level; may be {@code null} if this is the
         *        top level.
         */
        public Level(Index index, @Nullable Level parent) {
            this.index = index;
            this.parent = parent;
            if (parent != null) {
                assert index.getParent().equals(parent.getIndex()) : String
                    .format("Parent index %s should be parent of %s", parent.index, index);
                parent.addChild(this);
            } else {
                assert index.isTopLevel() : String
                    .format("Level with index %s should have non-null parent", index);
            }
        }

        /** Adds a child level to this level. */
        private void addChild(Level child) {
            assert this.index.equals(child.index.parent);
            this.children.add(child);
        }

        /**
         * Considers adding a node to the set of nodes on this level. The node
         * is also added to the
         * child levels if it satisfies {@link #isForNextLevel(AspectElement)}.
         */
        public void addNode(AspectNode modelNode) {
            if (isForThisLevel(modelNode)) {
                // put the node on this level
                this.modelNodes.add(modelNode);
            }
            // put the node on the sublevels, if it is supposed to be there
            if (isForNextLevel(modelNode)) {
                for (Level sublevel : this.children) {
                    sublevel.addNode(modelNode);
                }
            }
        }

        /**
         * Consider adding an edge to the set of edges on this level. The edge
         * is also added to the
         * child levels if it satisfies {@link #isForNextLevel(AspectElement)}.
         */
        public void addEdge(AspectEdge modelEdge) {
            if (isForThisLevel(modelEdge)) {
                // put the edge on this level
                this.modelEdges.add(modelEdge);
                // add end nodes to this and all parent levels, if
                // they are not yet there
                addNodeToParents(modelEdge.source());
                if (!isSetOperator(modelEdge)) {
                    addNodeToParents(modelEdge.target());
                }
                // add variables
                addToVars(modelEdge);
            }
            // put the edge on the sublevels, if it is supposed to be there
            if (isForNextLevel(modelEdge)) {
                for (Level sublevel : this.children) {
                    sublevel.addEdge(modelEdge);
                }
            }
        }

        /** Adds the variables of a given aspect edge to the variable map. */
        private void addToVars(AspectEdge modelEdge) {
            RuleLabel ruleLabel = modelEdge.getRuleLabel();
            if (ruleLabel != null) {
                for (LabelVar var : ruleLabel.allVarSet()) {
                    Set<AspectEdge> binders = this.modelVars.get(var);
                    if (binders == null) {
                        this.modelVars.put(var, binders = new HashSet<>());
                    }
                    binders.add(modelEdge);
                }
            }
        }

        /** Initialises the match count for this (universal) level. */
        public void setMatchCount(AspectNode matchCount) {
            this.countNode = matchCount;
        }

        /**
         * Adds a node to this and all parent levels, if it is not yet there
         */
        private void addNodeToParents(AspectNode modelNode) {
            Level ascendingLevel = this;
            while (ascendingLevel.modelNodes.add(modelNode)) {
                assert !ascendingLevel.index.isTopLevel() : String
                    .format("Node not found at any level");
                var parent = ascendingLevel.parent;
                assert parent != null; // guaranteed by the preceding assert
                ascendingLevel = parent;
            }
        }

        private boolean isSetOperator(AspectEdge edge) {
            Operator op = edge.getOperator();
            return op != null && op.isVarArgs();
        }

        /**
         * Indicates if a given element should be included on the level on which
         * it it is defined in the model. Node creators should not appear on
         * universal levels since those get translated to conditions, not rules;
         * instead they are pushed to the next (existential) sublevels.
         * @param elem the element about which the question is asked
         */
        private boolean isForThisLevel(AspectElement elem) {
            return this.index.getOperator().hasPattern();
        }

        /**
         * Indicates if a given element should occur on the sublevels of the
         * level on which it is defined in the model. This is the case for nodes
         * in injective rules (otherwise we cannot check injectivity) as well as
         * for edges that bind variables.
         * @param elem the element about which the question is asked
         */
        private boolean isForNextLevel(AspectElement elem) {
            assert elem.has(CONNECT) || !elem.has(Category.NESTING);
            boolean result = false;
            if (!this.index.getOperator().hasPattern()) {
                result = true;
            } else if (elem instanceof AspectNode n) {
                // we need to push non-attribute nodes down in injective mode
                // to be able to compare images of nodes at different levels
                result = LevelDistribution.this.compiler.isInjective()
                    && n.has(ROLE, AspectKind::inLHS) && !n.has(SORT) && !n.has(PRODUCT);
            } else {
                // we need to push down edges that bind wildcards
                // to ensure the bound value is known at sublevels
                // (there is currently no way to do this only when required)
                // as well as  all node type labels
                // to enable correct typing at sublevels
                //                RuleLabel varLabel = ((AspectEdge) elem).getRuleLabel();
                //                if (varLabel != null) {
                //                    result = getType().isNodeType(varLabel);
                //                }
            }
            return result;
        }

        /** Returns the index of this level. */
        public final Index getIndex() {
            return this.index;
        }

        @Override
        public String toString() {
            return String
                .format("Rule %s, level %s distribution",
                        LevelDistribution.this.compiler.getQualName(), getIndex());
        }

        /**
         * Does some post-processing after all elements have been added
         * to this and the parent levels.
         */
        public void setFixed() {
            var parent = this.parent;
            if (parent != null) {
                for (LabelVar var : this.modelVars.keySet()) {
                    parent.testParentBinding(var);
                }
            }
        }

        /** Tests if a given variable is already bound at this or a parent
         * level and, if so, adds it to the {@link #modelVars} at the intermediate
         * levels.
         */
        private boolean testParentBinding(LabelVar var) {
            boolean result = this.modelVars.containsKey(var);
            var parent = this.parent;
            if (!result && parent != null) {
                result = parent.testParentBinding(var);
                if (result) {
                    this.modelVars.put(var, new HashSet<>());
                }
            }
            return result;
        }

        /** Index of this level. */
        final Index index;
        /** Parent level; {@code null} if this is the top level. */
        final @Nullable Level parent;
        /** Children level data. */
        private final List<Level> children = new ArrayList<>();
        /** Set of model nodes on this level. */
        final Set<AspectNode> modelNodes = new HashSet<>();
        /** Set of model edges on this level. */
        final Set<AspectEdge> modelEdges = new HashSet<>();
        /** Set of label variables used on this level. */
        final Map<LabelVar,Set<AspectEdge>> modelVars = new HashMap<>();
        /** The model node registering the match count; may be {@code null}. */
        @Nullable
        AspectNode countNode;
    }
}
