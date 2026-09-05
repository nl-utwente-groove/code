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
package nl.utwente.groove.gui.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.graph.ControlGraph;
import nl.utwente.groove.control.graph.ControlNode;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.EdgeComparator;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.NodeComparator;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.ViewEdge;
import nl.utwente.groove.gui.view.ViewVertex;
import nl.utwente.groove.lts.GTS;
import nl.utwente.groove.util.Pair;

/**
 * Layout action for graph canvases that creates a top-to-bottom forest layout.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class ForestLayouter extends AbstractLayouter {
    /**
     * Constructs a factory instance of this layouter.
     */
    private ForestLayouter() {
        super(ACTION_NAME);
    }

    /**
     * Constructs a layouter for a given canvas.
     */
    protected ForestLayouter(String name, GraphCanvas<?> canvas) {
        super(name, canvas);
    }

    @Override
    public Layouter newInstance(GraphCanvas<?> canvas) {
        return new ForestLayouter(getName(), canvas);
    }

    /**
     * This implementation successively calls <tt>reset()</tt>,
     * <tt>prepare()</tt>, <tt>layout()</tt> and <tt>finish()</tt>.
     */
    @Override
    public void start() {
        synchronized (getCanvas()) {
            prepare(true);
            this.forest = computeForest(this.forest);
            this.forest.prune();
            layout(this.forest.one(), 0);
            // shift the graph to the right to make it less cramped and to
            // make some room for long labels
            shift(this.forest.one(), MIN_NODE_DISTANCE);
            finish();
        }
    }

    /**
     * Computes and returns the full branching structure from the layout map.
     */
    private Forest computeForest(Forest oldForest) {
        BranchMap oldBranchMap = oldForest.two();
        // Collect the layout nodes whose position in the forest should remain fixed
        Set<ViewVertex<?>> fixed = new HashSet<>();
        fixed.retainAll(oldBranchMap.keySet());
        // clear the indegree- and branch maps
        Map<Integer,Set<LayoutNode>> inDegreeMap = new TreeMap<>();
        BranchMap branchMap = new BranchMap();
        // compose the branch map
        for (ViewVertex<?> key : this.layoutMap.keySet()) {
            assert key.getVisuals().isVisible();
            // add the layoutable to the leaves and the branch map
            Set<LayoutNode> branchSet = new LinkedHashSet<>();
            branchMap.put(key, branchSet);
            // copy the immovable children from the old branch set to the new
            Set<LayoutNode> oldBranchSet = oldBranchMap.get(key);
            if (oldBranchSet != null) {
                for (LayoutNode oldChild : oldBranchSet) {
                    ViewVertex<?> jVertex = oldChild.getVertex();
                    if (this.immovableMap.containsKey(jVertex)) {
                        var layoutNode = this.layoutMap.get(jVertex);
                        assert layoutNode != null; // the immovables are a subset of the layout map
                        branchSet.add(layoutNode);
                        fixed.add(jVertex);
                    }
                }
            }
        }
        // count the incoming edges and add outgoing edges to the branch map
        for (Map.Entry<ViewVertex<?>,LayoutNode> layoutEntry : this.layoutMap.entrySet()) {
            ViewVertex<?> key = layoutEntry.getKey();
            // Initialise the incoming edge count
            int inEdgeCount = 0;
            // calculate the incoming edge count and (deterministic) outgoing edge map
            Set<ViewEdge<?>> outEdges = new TreeSet<>(edgeComparator);
            // iterate over the incident edges
            Iterator<? extends ViewEdge<?>> edgeIter = key.getContext();
            while (edgeIter.hasNext()) {
                ViewEdge<?> edge = edgeIter.next();
                if (!edge.getVisuals().isVisible()) {
                    continue;
                }
                if (edge.isGrayedOut()) {
                    continue;
                }
                // the edge source is a node for sure
                ViewVertex<?> sourceVertex = edge.getSourceVertex();
                // the edge target may be a point only
                if (sourceVertex != null && sourceVertex.equals(key)) {
                    if (!fixed.contains(edge.getTargetVertex())) {
                        outEdges.add(edge);
                    }
                } else {
                    // the key vertex is the target and not the source,
                    // so this must be an incoming (non-self) edge of
                    // the key
                    inEdgeCount++;
                }
            }
            Set<LayoutNode> branchSet = branchMap.get(key);
            assert branchSet != null; // the branch map was filled for every layout map key
            for (ViewEdge<?> edge : outEdges) {
                LayoutNode target = this.layoutMap.get(edge.getTargetVertex());
                if (target != null) {
                    branchSet.add(target);
                }
            }
            // add the cell to the count map
            Set<LayoutNode> inDegreeSet = inDegreeMap.get(inEdgeCount);
            if (inDegreeSet == null) {
                inDegreeMap.put(inEdgeCount, inDegreeSet = new LinkedHashSet<>());
            }
            inDegreeSet.add(layoutEntry.getValue());
        }
        Set<LayoutNode> remaining = new LinkedHashSet<>();
        // Transfer immovable old roots
        for (LayoutNode oldRoot : oldForest.one()) {
            ViewVertex<?> oldVertex = oldRoot.getVertex();
            if (this.immovableMap.containsKey(oldVertex)) {
                var layoutNode = this.layoutMap.get(oldVertex);
                assert layoutNode != null; // the immovables are a subset of the layout map
                remaining.add(layoutNode);
            }
        }
        // Transfer the suggested roots (if any) from cells to layoutables
        for (Object root : getSuggestedRoots()) {
            if (!(root instanceof ViewVertex)) {
                continue;
            }
            LayoutNode layoutable = ForestLayouter.this.layoutMap.get(root);
            if (layoutable != null) {
                remaining.add(layoutable);
            }
        }
        for (Set<LayoutNode> next : inDegreeMap.values()) {
            remaining.addAll(next);
        }
        return new Forest(remaining, branchMap);
    }

    /**
     * Callback method to determine a set of cells that are to be used as
     * roots in the forest layout. The current implementation returns the start
     * state or node for LTSs and control graphs, and the list of selected
     * cells of the underlying canvas otherwise.
     */
    protected Collection<?> getSuggestedRoots() {
        Collection<?> result;
        var canvas = getCanvas();
        var viewModel = canvas.getNonNullViewModel();
        @Nullable Graph graph = viewModel.getGraph();
        assert graph != null; // a layout is requested only for a loaded graph
        if (graph instanceof GTS lts) {
            var start = viewModel.getJCellForNode(lts.startState());
            result = start == null
                ? Collections.emptyList()
                : Collections.singleton(start);
        } else if (graph instanceof ControlGraph ctrl) {
            ControlNode startNode = ctrl.getStart();
            var start = viewModel.getJCellForNode(startNode);
            result = start == null
                ? Collections.emptyList()
                : Collections.singleton(start);
        } else {
            result = canvas.getSelection();
        }
        return result;
    }

    private Forest forest = new Forest();

    /**
     * Returns an array consisting of one Integer and two int[]'s. The first
     * value is the total width of the layed-out tree at the given set of root
     * cells; the second is the indentation from the left of each tree level,
     * and the third the indentation from the right.
     */
    private Layout layout(Collection<LayoutNode> branches, int height) {
        Layout result = new Layout(0);
        LinkedList<LayoutNode> previousBranches = new LinkedList<>();
        for (LayoutNode branch : branches) {
            Layout left = result;
            Layout right = layout(branch, height);
            result = new Layout(Math.max(left.count, right.count));
            int fit = (left.count == 0)
                ? 0
                : left.rightIndents[0] + right.leftIndents[0] - MIN_CHILD_DISTANCE;
            for (int level = 0; level < Math.min(left.count, right.count); level++) {
                fit = Math
                    .min(fit,
                         left.rightIndents[level] + right.leftIndents[level] - MIN_NODE_DISTANCE);
            }
            for (int level = 0; level < result.count; level++) {
                if (level < left.count) {
                    result.leftIndents[level] = left.leftIndents[level];
                } else {
                    result.leftIndents[level] = right.leftIndents[level] + left.width - fit;
                }
                if (level < right.count) {
                    result.rightIndents[level] = right.rightIndents[level];
                } else {
                    result.rightIndents[level] = left.rightIndents[level] + right.width - fit;
                }
            }
            // shift the right and left branches as required to accommodate the
            // fit
            result.width = left.width + right.width - fit;
            if (fit < left.width) {
                shift(branch, left.width - fit);
            } else if (fit > left.width) {
                shift(previousBranches, fit - left.width);
                shift(result.leftIndents, fit - left.width);
                result.width = right.width;
            }
            if (fit > right.width) {
                shift(result.rightIndents, fit - right.width);
                result.width = left.width;
            }
            previousBranches.add(branch);
        }
        return result;
    }

    /**
     * Returns an array consisting of one Integer and two int[]'s. The first
     * value is the total width of the layed-out tree at the given root cell;
     * the second is the indentation from the left of each tree level, and the
     * third the indentation from the right.
     */
    private Layout layout(LayoutNode layoutable, int height) {
        // recursively call layouting for the next level of the tree
        Set<LayoutNode> branches = this.forest.getBranches(layoutable);
        Layout branch = layout(branches, height + VERTICAL_SPACE + (int) layoutable.getHeight());
        // compute the width and adjust
        int cellWidth = (int) layoutable.getWidth();
        // the top cell should be centred w.r.t. the top level of the branches
        int rootIndent = (branch.width - cellWidth) / 2;
        // create the result for this tree
        Layout result = new Layout(branch.count + 1);
        result.leftIndents[0] = rootIndent;
        result.rightIndents[0] = rootIndent;
        System.arraycopy(branch.leftIndents, 0, result.leftIndents, 1, branch.count);
        System.arraycopy(branch.rightIndents, 0, result.rightIndents, 1, branch.count);
        // shift the result and the left and right indent if the root indent is
        // negative
        if (rootIndent < 0) {
            shift(branches, -rootIndent);
            shift(result.leftIndents, -rootIndent);
            shift(result.rightIndents, -rootIndent);
        }
        layoutable.setLocation(result.leftIndents[0], height);
        result.width = result.leftIndents[0] + cellWidth + result.rightIndents[0];
        return result;
    }

    /**
     * Shifts the position of a forest starting at a given set of cells to the
     * right by a certain distance
     * @param branches the roots of the forest to be shifted
     * @param shift the distance to shift the forest
     */
    private void shift(Collection<LayoutNode> branches, int shift) {
        for (LayoutNode branch : branches) {
            shift(branch, shift);
        }
    }

    /**
     * Shifts the position of a tree starting at a given cell to the right by a
     * certain distance
     * @param layoutable the root of the tree to be shifted
     * @param shift the distance to shift the tree
     */
    private void shift(LayoutNode layoutable, int shift) {
        layoutable.setLocation(layoutable.getX() + shift, layoutable.getY());
        shift(this.forest.getBranches(layoutable), shift);
    }

    /**
     * Shifts an array of indentations by a specified amount, by adding the
     * shift amount to each indentation.
     * @param indents the indentations to be shifted
     * @param shift the shift amount
     */
    private void shift(int[] indents, int shift) {
        for (int i = 0; i < indents.length; i++) {
            indents[i] += shift;
        }
    }

    private final static Comparator<ViewEdge<?>> edgeComparator = new Comparator<>() {
        @Override
        public int compare(ViewEdge<?> o1, ViewEdge<?> o2) {
            int result = nodeComp.compare(o1.getTargetNode(), o2.getTargetNode());
            if (result != 0) {
                return result;
            }
            result = edgeComp.compare(o1.getEdge(), o2.getEdge());
            return result;
        }
    };

    private final static NodeComparator nodeComp = NodeComparator.instance();
    private final static Comparator<Edge> edgeComp = EdgeComparator.instance();

    /** Prototype instance of the forest layouter. */
    public static final ForestLayouter PROTOTYPE = new ForestLayouter();
    /** Name of the layouter. */
    static public final String ACTION_NAME = "Forest layout";
    /**
     * The minimum horizontal space to between child nodes, not including node
     * width
     */
    static public final int MIN_CHILD_DISTANCE = 60;
    /**
     * The minimum horizontal space to between arbitrary nodes, not including
     * node width
     */
    static public final int MIN_NODE_DISTANCE = 40;
    /** The vertical space between levels, excluding the node height. */
    static public final int VERTICAL_SPACE = 40;

    private static class BranchMap extends LinkedHashMap<ViewVertex<?>,Set<LayoutNode>> {
        //
    }

    private static class Forest extends Pair<Collection<LayoutNode>,BranchMap> {
        /** Constructs an empty forest. */
        public Forest() {
            super(new LinkedHashSet<>(), new BranchMap());
        }

        public Forest(Collection<LayoutNode> one, BranchMap two) {
            super(one, two);
        }

        /** Returns the branches of a given layout node. */
        public Set<LayoutNode> getBranches(LayoutNode parent) {
            var result = two().get(parent.getVertex());
            assert result != null; // every layout node has a branch set
            return result;
        }

        /**
         * Prunes the forest by making sure that every node is either
         * a root, or a child of exactly one parent.
         */
        public void prune() {
            Collection<LayoutNode> remaining = one();
            // Add real roots one by one
            List<LayoutNode> roots = new ArrayList<>();
            while (!remaining.isEmpty()) {
                Iterator<LayoutNode> remainingIter = remaining.iterator();
                LayoutNode root = remainingIter.next();
                roots.add(root);
                remainingIter.remove();
                // compute reachable children and take them from remaining candidate roots
                // also adjust the branch sets of the reachable leaves
                Set<LayoutNode> children = new LinkedHashSet<>();
                children.add(root);
                while (!children.isEmpty()) {
                    Iterator<LayoutNode> childIter = children.iterator();
                    LayoutNode child = childIter.next();
                    childIter.remove();
                    // look up the next generation
                    Set<LayoutNode> branches = getBranches(child);
                    // restrict to remaining layoutables
                    branches.retainAll(remaining);
                    children.addAll(branches);
                    // remove the new branches from the remaining candidate roots
                    remaining.removeAll(branches);
                }
            }
            setOne(roots);
        }

    }

    /** Layout object describing the characteristics of a subtree. */
    private static class Layout {
        Layout(int count) {
            this(0, new int[count], new int[count]);
        }

        Layout(int width, int[] leftIndents, int[] rightIndents) {
            this.width = width;
            this.count = leftIndents.length;
            this.leftIndents = leftIndents;
            this.rightIndents = rightIndents;
        }

        int width;
        final int count;
        final int[] leftIndents;
        final int[] rightIndents;
    }
}
