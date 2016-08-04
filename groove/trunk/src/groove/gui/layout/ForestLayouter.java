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
package groove.gui.layout;

import java.util.ArrayList;
import java.util.Arrays;
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

import groove.control.graph.ControlGraph;
import groove.control.graph.ControlNode;
import groove.graph.Edge;
import groove.graph.EdgeComparator;
import groove.graph.NodeComparator;
import groove.gui.jgraph.CtrlJGraph;
import groove.gui.jgraph.JEdge;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JModel;
import groove.gui.jgraph.JVertex;
import groove.gui.jgraph.LTSJGraph;
import groove.gui.jgraph.LTSJModel;
import groove.util.Pair;

/**
 * Layout action for JGraphs that creates a top-to-bottom forest layout.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ForestLayouter extends AbstractLayouter {
    /**
     * Constructs a factory instance of this layouter.
     */
    private ForestLayouter() {
        super(ACTION_NAME);
    }

    /**
     * Constructs a layouter for a given j-graph.
     */
    protected ForestLayouter(String name, JGraph<?> jgraph) {
        super(name, jgraph);
        // setEnabled(true);
    }

    @Override
    public Layouter newInstance(JGraph<?> jgraph) {
        return new ForestLayouter(getName(), jgraph);
    }

    /**
     * This implementation successively calls <tt>reset()</tt>,
     * <tt>prepare()</tt>, <tt>layout()</tt> and <tt>finish()</tt>.
     */
    @Override
    public void start() {
        synchronized (getJGraph()) {
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
        Set<JVertex<?>> fixed = new HashSet<>();
        fixed.retainAll(oldBranchMap.keySet());
        // clear the indegree- and branch maps
        Map<Integer,Set<LayoutNode>> inDegreeMap = new TreeMap<>();
        BranchMap branchMap = new BranchMap();
        // compose the branch map
        for (JVertex<?> key : this.layoutMap.keySet()) {
            assert key.getVisuals()
                .isVisible();
            // add the layoutable to the leaves and the branch map
            Set<LayoutNode> branchSet = new LinkedHashSet<>();
            branchMap.put(key, branchSet);
            // copy the immovable children from the old branch set to the new
            Set<LayoutNode> oldBranchSet = oldBranchMap.get(key);
            if (oldBranchSet != null) {
                for (LayoutNode oldChild : oldBranchSet) {
                    JVertex<?> jVertex = oldChild.getVertex();
                    if (this.immovableMap.containsKey(jVertex)) {
                        branchSet.add(this.layoutMap.get(jVertex));
                        fixed.add(jVertex);
                    }
                }
            }
        }
        // count the incoming edges and add outgoing edges to the branch map
        for (Map.Entry<JVertex<?>,LayoutNode> layoutEntry : this.layoutMap.entrySet()) {
            JVertex<?> key = layoutEntry.getKey();
            // Initialise the incoming edge count
            int inEdgeCount = 0;
            // calculate the incoming edge count and (deterministic) outgoing edge map
            Set<JEdge<?>> outEdges = new TreeSet<>(edgeComparator);
            // iterate over the incident edges
            Iterator<?> edgeIter = key.getPort()
                .edges();
            while (edgeIter.hasNext()) {
                JEdge<?> edge = (JEdge<?>) edgeIter.next();
                if (!edge.getVisuals()
                    .isVisible()) {
                    continue;
                }
                if (edge.isGrayedOut()) {
                    continue;
                }
                // the edge source is a node for sure
                JVertex<?> sourceVertex = edge.getSourceVertex();
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
            for (JEdge<?> edge : outEdges) {
                JVertex<?> targetVertex = edge.getTargetVertex();
                branchSet.add(this.layoutMap.get(targetVertex));
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
            JVertex<?> oldVertex = oldRoot.getVertex();
            if (this.immovableMap.containsKey(oldVertex)) {
                remaining.add(this.layoutMap.get(oldVertex));
            }
        }
        // Transfer the suggested roots (if any) from j-cells to layoutables
        Collection<?> suggestedRoots = getSuggestedRoots();
        if (suggestedRoots != null) {
            for (Object root : getSuggestedRoots()) {
                if (!(root instanceof JVertex)) {
                    continue;
                }
                LayoutNode layoutable = ForestLayouter.this.layoutMap.get(root);
                if (layoutable == null) {
                    throw new IllegalArgumentException(
                        "Suggested root " + root + " is not a known graph cell");
                }
                remaining.add(layoutable);
            }
        }
        for (Set<LayoutNode> next : inDegreeMap.values()) {
            remaining.addAll(next);
        }
        return new Forest(remaining, branchMap);
    }

    /**
     * Callback method to determine a set of j-cells that are to be used as
     * roots in the forest layout. A return value of <tt>null</tt> means no
     * suggestions. The current implementation returns the list of selected
     * cells of the underlying {@link JGraph}.
     */
    protected Collection<?> getSuggestedRoots() {
        Collection<?> result;
        JGraph<?> jGraph = getJGraph();
        if (jGraph instanceof LTSJGraph) {
            LTSJModel jModel = ((LTSJGraph) jGraph).getModel();
            result = Collections.singleton(jModel.getJCellForNode(jModel.getGraph()
                .startState()));
        } else if (jGraph instanceof CtrlJGraph) {
            JModel<ControlGraph> jModel = ((CtrlJGraph) jGraph).getModel();
            ControlNode start = jModel.getGraph()
                .getStart();
            result = Collections.singleton(jModel.getJCellForNode(start));
        } else {
            result = Arrays.asList(jGraph.getSelectionCells());
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
            int fit = (left.count == 0) ? 0
                : left.rightIndents[0] + right.leftIndents[0] - MIN_CHILD_DISTANCE;
            for (int level = 0; level < Math.min(left.count, right.count); level++) {
                fit = Math.min(fit,
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

    private final static Comparator<JEdge<?>> edgeComparator = new Comparator<JEdge<?>>() {
        @Override
        public int compare(JEdge<?> o1, JEdge<?> o2) {
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

    private static class BranchMap extends LinkedHashMap<JVertex<?>,Set<LayoutNode>> {
        //
    }

    private static class Forest extends Pair<Collection<LayoutNode>,BranchMap> {
        /** Constructs an empty forest. */
        public Forest() {
            super(new LinkedHashSet<LayoutNode>(), new BranchMap());
        }

        public Forest(Collection<LayoutNode> one, BranchMap two) {
            super(one, two);
        }

        /** Returns the branches of a given layout node. */
        public Set<LayoutNode> getBranches(LayoutNode parent) {
            return two().get(parent.getVertex());
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
