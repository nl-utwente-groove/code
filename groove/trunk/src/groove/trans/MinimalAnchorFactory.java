// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: MinimalAnchorFactory.java,v 1.8 2008-02-29 11:02:20 fladder Exp $
 */
package groove.trans;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Node;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * In this implementation, the anchors are the minimal set of nodes and edges
 * needed to reconstruct the transformation, but not necessarily the entire
 * matching: only mergers, eraser nodes and edges (the later only if they are
 * not incident to an eraser node) and the incident nodes of creator edges are
 * stored.
 * @author Arend Rensink
 * @version $Revision$
 */
public class MinimalAnchorFactory implements AnchorFactory<SPORule> {
    /** Private empty constructor to make this a singleton class. */
    private MinimalAnchorFactory() {
        // empty constructor
    }

    /**
     * This implementation assumes that the rule is an <tt>SPORule</tt>, and
     * that the rule's internal sets of <tt>lhsOnlyNodes</tt> etc. have been
     * initialised already.
     * @require <tt>rule instanceof SPORule</tt>
     */
    public Element[] newAnchors(SPORule rule) {
        Set<Element> anchors =
            new LinkedHashSet<Element>(Arrays.asList(rule.getEraserNodes()));
        anchors.addAll(rule.getHiddenPars());
        anchors.addAll(rule.getInPars());
        // set of endpoints that we will remove again
        Set<Node> removableEnds = new HashSet<Node>();
        for (Edge lhsVarEdge : rule.getSimpleVarEdges()) {
            anchors.add(lhsVarEdge);
            // if we have the edge in the anchors, its end nodes need not be
            // there
            removableEnds.addAll(Arrays.asList(lhsVarEdge.ends()));
        }
        for (Edge eraserEdge : rule.getEraserEdges()) {
            Collection<Node> eraserEdgeEnds = Arrays.asList(eraserEdge.ends());
            if (!anchors.containsAll(eraserEdgeEnds)) {
                anchors.add(eraserEdge);
                // if we have the edge in the anchors, its end nodes need not be
                // there
                removableEnds.addAll(eraserEdgeEnds);
            }
        }
        // addRootImageEdges(rule, anchors, removableEnds);
        anchors.addAll(rule.getModifierEnds());
        // anchors.addAll(rule.getMergeMap().keySet());
        anchors.removeAll(removableEnds);
        return anchors.toArray(new Element[0]);
    }

    //
    // /** Adds the node images of the root map to the anchors. */
    // private void addRootImageNodes(SPORule rule, Set<Element> anchors) {
    // // why do we need the root images, if this rule doesn't do anything with
    // // them?
    // if (false) {
    // for (Node rootImage : rule.getRootMap().nodeMap().values()) {
    // if (isAnchorable(rootImage)) {
    // anchors.add(rootImage);
    // }
    // }
    // }
    // }
    //
    // /** Adds the edge images of the root map to the anchors. */
    // private void addRootImageEdges(SPORule rule, Set<Element> anchors,
    // Set<Node> removableEnds) {
    // // why do we need the root images, if this rule doesn't do anything with
    // // them?
    // if (false) {
    // for (Edge rootEdge : rule.getRootMap().edgeMap().values()) {
    // if (isAnchorable(rootEdge)) {
    // Collection<Node> rootEdgeEnds =
    // Arrays.asList(rootEdge.ends());
    // if (!anchors.containsAll(rootEdgeEnds)) {
    // anchors.add(rootEdge);
    // // if we have the edge in the anchors, its end nodes
    // // need not be there
    // removableEnds.addAll(rootEdgeEnds);
    // }
    // }
    // }
    // }
    // }
    //
    // /**
    // * Tests if a given node can be an anchor. This fails to hold for
    // * {@link ProductNode}s that are not {@link ValueNode}s.
    // */
    // private boolean isAnchorable(Node node) {
    // return !(node instanceof ProductNode) || node instanceof ValueNode;
    // }
    //
    // /**
    // * Tests if a given edge can be an anchor. This fails to hold for
    // * {@link OperatorEdge}s that are not {@link ArgumentEdge}s.
    // */
    // private boolean isAnchorable(Edge edge) {
    // return !(edge instanceof ArgumentEdge || edge instanceof OperatorEdge);
    // }
    /**
     * Returns the singleton instance of this class.
     */
    static public MinimalAnchorFactory getInstance() {
        return prototype;
    }

    /** The singleton instance of this class. */
    static private MinimalAnchorFactory prototype = new MinimalAnchorFactory();
}
