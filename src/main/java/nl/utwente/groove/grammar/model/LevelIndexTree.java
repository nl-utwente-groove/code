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

import static nl.utwente.groove.grammar.aspect.AspectKind.EXISTS;
import static nl.utwente.groove.grammar.aspect.AspectKind.FORALL_POS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;

import nl.utwente.groove.grammar.Condition;
import nl.utwente.groove.grammar.Condition.Op;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.model.RuleModel.Index;
import nl.utwente.groove.util.QualName;

/**
 * Tree of quantification level indices of a rule, derived from the nesting
 * aspects of the (normalised) rule graph: the set of level indices in tree
 * order, with lookup maps from nesting nodes and quantifier names to indices,
 * and from (universal) indices to their match count nodes.
 * A pure data structure, constructed by {@link #from}.
 * @author Arend Rensink
 * @version $Revision$
 */
class LevelIndexTree {
    /** Constructs the index tree for a given rule source graph. */
    private LevelIndexTree(AspectGraph source, QualName qualName) {
        this.qualName = qualName;
        // First build an explicit tree of level nodes
        Map<Index,List<Index>> indexTree = new HashMap<>();
        this.topLevelIndex = createIndex(Op.EXISTS, false, null, indexTree);
        // build the index tree
        indexTree.put(this.topLevelIndex, new ArrayList<>());
        for (AspectNode node : source.nodeSet()) {
            if (node.has(Category.NESTING)) {
                // look for the parent level
                Index parentIndex;
                // by the correctness of the aspect graph we know that
                // there is at most one outgoing edge, which is a parent
                // edge and points to the parent level node
                AspectNode parentNode = node.getParentNode();
                if (parentNode == null) {
                    parentIndex = this.topLevelIndex;
                } else {
                    AspectKind parentKind = parentNode.getKind(Category.NESTING);
                    parentIndex = getIndex(parentKind, parentNode, indexTree);
                }
                Index myIndex = getIndex(node.getKind(Category.NESTING), node, indexTree);
                var siblings = indexTree.get(parentIndex);
                assert siblings != null; // getIndex registers all indices in the tree
                siblings.add(myIndex);
                if (node.getMatchCount() != null) {
                    this.matchCountMap.put(myIndex, node.getMatchCount());
                }
            }
        }
        // insert the children into the indices themselves and build the index set
        SortedSet<Index> indexSet = new TreeSet<>();
        Queue<Index> indexQueue = new LinkedList<>();
        indexQueue.add(this.topLevelIndex);
        while (!indexQueue.isEmpty()) {
            Index next = indexQueue.poll();
            assert next != null; // queue is non-empty
            next.setFixed();
            List<Index> children = indexTree.get(next);
            assert children != null; // all indices are registered in the tree
            // add an implicit existential sub-level to childless universal
            // levels
            if (next.getOperator() == Op.FORALL && children.isEmpty()) {
                Index implicitChild = createIndex(Op.EXISTS, true, null, indexTree);
                children.add(implicitChild);
            }
            // set the parent of all children
            for (int i = 0; i < children.size(); i++) {
                children.get(i).setParent(next, i);
            }
            indexQueue.addAll(children);
            indexSet.add(next);
        }
        this.indices = indexSet;
    }

    /**
     * Lazily creates and returns a level index for a given level nesting node.
     * @param nestingNode the level node for which a level is to be created;
     *        should satisfy
     *        {@link AspectKind#isQuantifier()}
     */
    private Index getIndex(AspectKind quantifier, AspectNode nestingNode,
                           Map<Index,List<Index>> indexTree) {
        Index result = this.nestingIndexMap.get(nestingNode);
        if (result == null) {
            AspectKind nestingKind = nestingNode.getKind(Category.NESTING);
            assert nestingKind != null;
            Condition.Op operator = nestingKind.isExists()
                ? Op.EXISTS
                : Op.FORALL;
            boolean positive = nestingKind == EXISTS || nestingKind == FORALL_POS;
            this.nestingIndexMap
                .put(nestingNode,
                     result = createIndex(operator, positive, nestingNode, indexTree));
            if (nestingNode.hasId()) {
                String id = nestingNode.getId();
                Index oldIndex = this.nameIndexMap.put(id, result);
                assert oldIndex == null : String.format("Duplicate quantifier name %s", id);
            }
        }
        return result;
    }

    /** Creates a level index for a given nesting node and creates
     * an entry in the level tree.
     * @param levelNode the quantifier nesting node
     * @param levelTree the tree of level indices
     * @return the fresh level index
     */
    private Index createIndex(Condition.Op operator, boolean positive, AspectNode levelNode,
                              Map<Index,List<Index>> levelTree) {
        Index result = new Index(operator, positive, levelNode, this.qualName);
        levelTree.put(result, new ArrayList<>());
        return result;
    }

    /** Returns the level indices, in tree order. */
    SortedSet<Index> getIndices() {
        return this.indices;
    }

    /** Returns the top level index. */
    Index getTopLevelIndex() {
        return this.topLevelIndex;
    }

    /** Returns the level index of a given nesting node. */
    Index getIndex(AspectNode nestingNode) {
        return this.nestingIndexMap.get(nestingNode);
    }

    /** Returns the level index with a given quantifier name,
     * or {@code null} if there is no such index. */
    Index getIndex(String name) {
        return this.nameIndexMap.get(name);
    }

    /** Returns the mapping from (universal) level indices to match count nodes. */
    Map<Index,AspectNode> getMatchCountMap() {
        return this.matchCountMap;
    }

    /** The qualified rule name, used as prefix for the level names. */
    private final QualName qualName;
    /** The top level of the tree. */
    private final Index topLevelIndex;
    /** The set of all level indices, in tree order. */
    private final SortedSet<Index> indices;
    /** Mapping from nesting nodes to level indices. */
    private final Map<AspectNode,Index> nestingIndexMap = new HashMap<>();
    /** Mapping from nesting level names to level indices. */
    private final Map<String,Index> nameIndexMap = new HashMap<>();
    /** Mapping from (universal) level indices to match count nodes. */
    private final Map<Index,AspectNode> matchCountMap = new HashMap<>();

    /** Builds and returns the level index tree of a given rule source graph.
     * @param source the (normalised) source graph of the rule
     * @param qualName the qualified rule name, used as prefix for the level names
     */
    static LevelIndexTree from(AspectGraph source, QualName qualName) {
        return new LevelIndexTree(source, qualName);
    }
}
