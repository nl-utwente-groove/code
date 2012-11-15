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
 * $Id: ForestLayouter.java,v 1.6 2008-01-30 09:33:01 iovka Exp $
 */
package groove.gui.layout;

import groove.gui.jgraph.GraphJEdge;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.GraphJVertex;
import groove.util.CollectionOfCollections;
import groove.util.NestedIterator;
import groove.util.Pair;
import groove.util.TransformIterator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jgraph.graph.EdgeView;

/**
 * Layout action for JGraphs that creates a top-to-bottom forest layout.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ForestLayouter extends AbstractLayouter {
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

    /**
     * Constructs a factory instance of this layouter.
     */
    public ForestLayouter() {
        super(ACTION_NAME);
    }

    /**
     * Constructs a layouter for a given j-graph.
     */
    protected ForestLayouter(String name, GraphJGraph jgraph) {
        super(name, jgraph);
        // setEnabled(true);
    }

    public Layouter newInstance(GraphJGraph jgraph) {
        return new ForestLayouter(this.name, jgraph);
    }

    /**
     * This implementation successively calls <tt>reset()</tt>,
     * <tt>prepare()</tt>, <tt>layout()</tt> and <tt>finish()</tt>.
     */
    public void start(boolean complete) {
        if (complete) {
            reset();
        }
        synchronized (this.jgraph) {
            prepare();
            computeBranchMap();
            computeRoots();
            layout(this.roots, 0);
            // shift the graph to the right to make it less cramped and to
            // make some room for long labels
            shift(this.roots, MIN_NODE_DISTANCE);
            finish();
        }
    }

    /** This implementation does nothing, */
    public void stop() {
        // does nothing
    }

    /**
     * Callback method to determine a set of j-cells that are to be used as
     * roots in the forest layout. A return value of <tt>null</tt> means no
     * suggestions. The current implementation returns the list of selected
     * cells of the underlying {@link GraphJGraph}.
     */
    protected Collection<?> getSuggestedRoots() {
        return Arrays.asList(this.jgraph.getSelectionCells());
    }

    /**
     * Computes the full branching structure from the layout map, and stores it
     * in {@link #branchMap}.
     */
    private void computeBranchMap() {
        // clear the indegree- and branch maps
        this.inDegreeMap.clear();
        this.branchMap.clear();
        // count the incoming edges and compose the branch map
        for (Map.Entry<Object,Layoutable> cellLayoutableEntry : this.toLayoutableMap.entrySet()) {
            Object key = cellLayoutableEntry.getKey();
            Layoutable cellLayoutable = cellLayoutableEntry.getValue();
            // add the layoutable to the leaves and the branch map
            Set<Layoutable> branchSet = new LinkedHashSet<Layoutable>();
            this.branchMap.put(cellLayoutable, branchSet);
            if (key instanceof GraphJVertex && ((GraphJVertex) key).isVisible()) {
                // Initialise the incoming edge count
                int inEdgeCount = 0;
                // calculate the incoming edge count and outgoing edge map
                // iterate over the incident edges
                Set<GraphJEdge> outEdges =
                    new TreeSet<GraphJEdge>(edgeComparator);
                Iterator<?> edgeIter = ((GraphJVertex) key).getPort().edges();
                while (edgeIter.hasNext()) {
                    GraphJEdge edge = (GraphJEdge) edgeIter.next();
                    // it's possible that the edge is displayed as node label
                    // even though it has an explicit layout
                    EdgeView edgeView =
                        (EdgeView) this.jgraph.getGraphLayoutCache().getMapping(
                            edge, false);
                    if (edgeView != null && edge.isVisible()
                        && !edge.isGrayedOut()) {
                        // the edge source is a node for sure
                        GraphJVertex sourceVertex = edge.getSourceVertex();
                        // the edge target may be a point only
                        if (sourceVertex.equals(key)) {
                            outEdges.add(edge);
                        } else {
                            // the key vertex is the target and not the source,
                            // so this must be an incoming (non-self) edge of
                            // the key
                            inEdgeCount++;
                        }
                    }
                }
                for (GraphJEdge edge : outEdges) {
                    EdgeView edgeView =
                        (EdgeView) this.jgraph.getGraphLayoutCache().getMapping(
                            edge, false);
                    // add all the points on the edge to the branches of
                    // the
                    // source node
                    // as well as its end node (if any)
                    List<?> points = edgeView.getPoints();
                    GraphJVertex targetVertex = edge.getTargetVertex();
                    // the first point is the (port of the) source node
                    // itself; skip it
                    for (int i = 1; i < points.size() - 1; i++) {
                        branchSet.add(this.toLayoutableMap.get(Pair.newPair(
                            edge, i)));
                    }
                    branchSet.add(this.toLayoutableMap.get(targetVertex));
                }
                // add the cell to the count map
                Integer inEdgeCountKey = Integer.valueOf(inEdgeCount);
                Set<Layoutable> cellsWithInEdgeCount =
                    this.inDegreeMap.get(inEdgeCountKey);
                if (cellsWithInEdgeCount == null) {
                    this.inDegreeMap.put(inEdgeCountKey, cellsWithInEdgeCount =
                        new LinkedHashSet<Layoutable>());
                }
                cellsWithInEdgeCount.add(cellLayoutable);
            }
        }
    }

    /**
     * Computes a set of roots for the forest. The method takes a hint as to the
     * most suitable roots, and then continues with the layoutables with the
     * least in-degree (according to {@link #inDegreeMap}). Call
     * {@link #getSuggestedRoots} for a hint as to the most appropriate roots;
     * disregarded if <tt>null</tt> The result is stored in <code>roots</code>.
     */
    private void computeRoots() {
        Iterator<Layoutable> rootIter =
            new CollectionOfCollections<Layoutable>(this.inDegreeMap.values()).iterator();
        // Transfer the suggested roots (if any) from j-cells to layoutables
        Collection<?> suggestedRoots = getSuggestedRoots();
        if (suggestedRoots != null && !suggestedRoots.isEmpty()) {
            Iterator<Layoutable> suggestedRootIter =
                new TransformIterator<Object,Layoutable>(
                    suggestedRoots.iterator()) {
                    @Override
                    public Layoutable toOuter(Object in) {
                        Layoutable result =
                            ForestLayouter.this.toLayoutableMap.get(in);
                        if (result == null) {
                            throw new IllegalArgumentException(
                                "Suggested root " + in
                                    + " is not a known graph cell");
                        }
                        return result;
                    }
                };
            rootIter =
                new NestedIterator<Layoutable>(suggestedRootIter, rootIter);
        }
        // now add real roots to the result list, one by one
        this.roots.clear();
        Set<Layoutable> leaves =
            new LinkedHashSet<Layoutable>(this.branchMap.keySet());
        while (rootIter.hasNext()) {
            Layoutable root = rootIter.next();
            if (leaves.contains(root)) {
                this.roots.add(root);
                leaves.remove(root);
                // compute reachable cells and take them from leaves
                // also adjust the branch sets of the reachable leaves
                Set<Layoutable> reachableCells =
                    new LinkedHashSet<Layoutable>();
                reachableCells.add(root);
                while (!reachableCells.isEmpty()) {
                    Iterator<Layoutable> reachableCellIter =
                        reachableCells.iterator();
                    Object nextReachableCell = reachableCellIter.next();
                    reachableCellIter.remove();
                    // we might have duplication in the list of reachable cells
                    // so we have to check whether this one was not done before
                    Set<Layoutable> branches =
                        this.branchMap.get(nextReachableCell);
                    // restrict to branches which were unreached before
                    branches.retainAll(leaves);
                    reachableCells.addAll(branches);
                    // remove the new branches from the unreached leaves
                    leaves.removeAll(branches);
                }
            }
        }
    }

    /**
     * Returns an array consisting of one Integer and two int[]'s. The first
     * value is the total width of the layed-out tree at the given set of root
     * cells; the second is the indentation from the left of each tree level,
     * and the third the indentation from the right.
     */
    private Object[] layout(Collection<Layoutable> branches, int height) {
        int width = 0;
        int levelCount = 0;
        int[] leftIndent = new int[levelCount];
        int[] rightIndent = new int[levelCount];
        LinkedList<Layoutable> previousBranches = new LinkedList<Layoutable>();
        for (Layoutable branch : branches) {
            Object[] rightLayout = layout(branch, height);
            int leftWidth = width;
            int leftLevelCount = levelCount;
            int[] leftLeftIndent = leftIndent;
            int[] leftRightIndent = rightIndent;
            int rightWidth = ((Integer) rightLayout[0]).intValue();
            int[] rightLeftIndent = (int[]) rightLayout[1];
            int[] rightRightIndent = (int[]) rightLayout[2];
            int rightLevelCount = rightLeftIndent.length;
            levelCount = Math.max(leftLevelCount, rightLevelCount);
            leftIndent = new int[levelCount];
            rightIndent = new int[levelCount];
            int fit =
                (leftLevelCount == 0) ? 0 : leftRightIndent[0]
                    + rightLeftIndent[0] - MIN_CHILD_DISTANCE;
            // fit = Math.min(fit, leftWidth);
            for (int level = 0; level < levelCount; level++) {
                if (level < leftLevelCount && level < rightLevelCount) {
                    fit =
                        Math.min(fit, leftRightIndent[level]
                            + rightLeftIndent[level] - MIN_NODE_DISTANCE);
                }
            }
            for (int level = 0; level < levelCount; level++) {
                if (level < leftLevelCount) {
                    leftIndent[level] = leftLeftIndent[level];
                } else {
                    leftIndent[level] =
                        rightLeftIndent[level] + leftWidth - fit;
                }
                if (level < rightLevelCount) {
                    rightIndent[level] = rightRightIndent[level];
                } else {
                    rightIndent[level] =
                        leftRightIndent[level] + rightWidth - fit;
                }
            }
            // shift the right and left branches as required to accommodate the
            // fit
            width = leftWidth + rightWidth - fit;
            if (fit < leftWidth) {
                shift(branch, leftWidth - fit);
            } else if (fit > leftWidth) {
                shift(previousBranches, fit - leftWidth);
                shift(leftIndent, fit - leftWidth);
                width = rightWidth;
            }
            if (fit > rightWidth) {
                shift(rightIndent, fit - rightWidth);
                width = leftWidth;
            }
            previousBranches.add(branch);
        }
        return new Object[] {Integer.valueOf(width), leftIndent, rightIndent};
    }

    /**
     * Returns an array consisting of one Integer and two int[]'s. The first
     * value is the total width of the layed-out tree at the given root cell;
     * the second is the indentation from the left of each tree level, and the
     * third the indentation from the right.
     */
    private Object[] layout(Layoutable layoutable, int height) {
        // recursively call layouting for the next level of the tree
        Set<Layoutable> branches = this.branchMap.get(layoutable);
        Object[] branchLayout =
            layout(branches,
                height + VERTICAL_SPACE + (int) layoutable.getHeight());
        int branchWidth = ((Integer) branchLayout[0]).intValue();
        int[] branchLeftIndent = (int[]) branchLayout[1];
        int[] branchRightIndent = (int[]) branchLayout[2];
        int branchLevelCount = branchLeftIndent.length;
        // compute the width and adjust
        int cellWidth = (int) layoutable.getWidth();
        // the top cell should be centered w.r.t. the top level of the branches
        int branchLevel0LeftIndent =
            branchLevelCount == 0 ? 0 : branchLeftIndent[0];
        int branchLevel0RightIndent =
            branchLevelCount == 0 ? 0 : branchRightIndent[0];
        int branchLevel0Width =
            branchWidth - branchLevel0LeftIndent - branchLevel0RightIndent;
        int rootLeftIndent =
            branchLevel0LeftIndent + (branchLevel0Width - cellWidth) / 2;
        int rootRightIndent = branchWidth - rootLeftIndent - cellWidth;
        // create the result for this tree
        int levelCount = branchLevelCount + 1;
        int[] leftIndent = new int[levelCount];
        int[] rightIndent = new int[levelCount];
        leftIndent[0] = rootLeftIndent;
        rightIndent[0] = rootRightIndent;
        System.arraycopy(branchLeftIndent, 0, leftIndent, 1, branchLevelCount);
        System.arraycopy(branchRightIndent, 0, rightIndent, 1, branchLevelCount);
        // shift the result and the left indent if the root left indent is
        // negative
        if (rootLeftIndent < 0) {
            shift(branches, -rootLeftIndent);
            shift(leftIndent, -rootLeftIndent);
        }
        // shift the right indent if the root right indent is negative
        if (rootRightIndent < 0) {
            shift(rightIndent, -rootRightIndent);
        }
        layoutable.setLocation(leftIndent[0], height);
        int width = leftIndent[0] + cellWidth + rightIndent[0];
        return new Object[] {Integer.valueOf(width), leftIndent, rightIndent};
    }

    /**
     * Shifts the position of a forest starting at a given set of cells to the
     * right by a certain distance
     * @param branches the roots of the forest to be shifted
     * @param shift the distance to shift the forest
     */
    private void shift(Collection<Layoutable> branches, int shift) {
        for (Layoutable branch : branches) {
            shift(branch, shift);
        }
    }

    /**
     * Shifts the position of a tree starting at a given cell to the right by a
     * certain distance
     * @param layoutable the root of the tree to be shifted
     * @param shift the distance to shift the tree
     */
    private void shift(Layoutable layoutable, int shift) {
        layoutable.setLocation(layoutable.getX() + shift, layoutable.getY());
        shift(this.branchMap.get(layoutable), shift);
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

    /**
     * The in-degree of all layoutables, as a mapping from
     * {@link groove.gui.layout.AbstractLayouter.Layoutable} to {@link Integer}.
     */
    private final Map<Integer,Set<Layoutable>> inDegreeMap =
        new TreeMap<Integer,Set<Layoutable>>();
    /**
     * The branch map of the forest. It maps each layoutable item (jgraph node
     * bounds or edge point) to the set of its children. For a node, all points
     * on the outgoing edges are children. Edge points have no children of their
     * own. If the branch set is empty, the cell is a leaf.
     */
    private final Map<Layoutable,Set<Layoutable>> branchMap =
        new LinkedHashMap<Layoutable,Set<Layoutable>>();
    /** The roots of the forest. */
    private final Collection<Layoutable> roots = new LinkedList<Layoutable>();

    private final static Comparator<GraphJEdge> edgeComparator =
        new Comparator<GraphJEdge>() {
            @Override
            public int compare(GraphJEdge o1, GraphJEdge o2) {
                int result = o1.getTargetNode().compareTo(o2.getTargetNode());
                if (result == 0) {
                    result = o1.getEdge().compareTo(o2.getEdge());
                }
                return result;
            }
        };
}
