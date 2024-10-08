/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.look;

import static nl.utwente.groove.grammar.aspect.AspectKind.REMARK;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.gui.jgraph.AspectJCell;
import nl.utwente.groove.gui.jgraph.AspectJEdge;
import nl.utwente.groove.gui.jgraph.AspectJGraph;
import nl.utwente.groove.gui.jgraph.AspectJVertex;
import nl.utwente.groove.gui.jgraph.JCell;
import nl.utwente.groove.gui.jgraph.JEdge;
import nl.utwente.groove.gui.jgraph.JGraph;
import nl.utwente.groove.gui.jgraph.JVertex;
import nl.utwente.groove.gui.jgraph.LTSJCell;
import nl.utwente.groove.gui.jgraph.LTSJEdge;
import nl.utwente.groove.gui.jgraph.LTSJGraph;
import nl.utwente.groove.gui.jgraph.LTSJVertex;
import nl.utwente.groove.gui.tree.LabelTree;
import nl.utwente.groove.gui.tree.RuleLevelTree;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;

/**
 * Strategy to determine whether a given cell is currently visible.
 * @author Arend Rensink
 * @version $Revision$
 */
public class VisibleValue implements VisualValue<Boolean> {
    @Override
    public <G extends @NonNull Graph> Boolean get(JGraph<G> jGraph, JCell<G> cell) {
        boolean result = true;
        boolean isVertex = cell instanceof JVertex;
        assert jGraph != null; // should be the case by the time this method gets called
        if (cell instanceof AspectJCell) {
            result = isVertex
                ? getAspectVertexValue((AspectJGraph) jGraph, (AspectJVertex) cell)
                : getAspectEdgeValue((AspectJGraph) jGraph, (AspectJEdge) cell);
        } else if (cell instanceof LTSJCell) {
            result = isVertex
                ? getLTSVertexValue((LTSJGraph) jGraph, (LTSJVertex) cell)
                : getLTSEdgeValue((LTSJGraph) jGraph, (LTSJEdge) cell);
        } else if (cell instanceof JVertex) {
            result = isVertex
                ? getBasicVertexValue(jGraph, (JVertex<G>) cell)
                : getBasicEdgeValue(jGraph, (JEdge<G>) cell);
        }
        return result;
    }

    private <G extends @NonNull Graph> boolean getBasicVertexValue(JGraph<G> jGraph,
                                                                   JVertex<G> jVertex) {
        LabelTree<G> labelTree = jGraph.getLabelTree();
        return labelTree == null || labelTree.isIncluded(jVertex);
    }

    private <G extends @NonNull Graph> boolean getBasicEdgeValue(JGraph<G> jGraph, JEdge<G> jEdge) {
        boolean result = true;
        JVertex<?> source = jEdge.getSourceVertex();
        JVertex<?> target = jEdge.getTargetVertex();
        if (source == null || !source.getVisuals().isVisible()) {
            return false;
        }
        if (target == null || !target.getVisuals().isVisible()) {
            return false;
        }
        LabelTree<G> labelTree = jGraph.getLabelTree();
        if (labelTree != null) {
            result = labelTree.isIncluded(jEdge);
        }
        return result;
    }

    private boolean getAspectVertexValue(AspectJGraph jGraph, AspectJVertex jVertex) {
        AspectNode node = jVertex.getNode();
        // remark nodes are always visible
        if (node.has(REMARK)) {
            return true;
        }
        // anything explicitly filtered by the level tree is not visible
        RuleLevelTree levelTree = jGraph.getLevelTree();
        if (levelTree != null && !levelTree.isVisible(jVertex)) {
            return false;
        }
        // anything declared invisible by the super method is not visible
        if (!getBasicVertexValue(jGraph, jVertex)) {
            return false;
        }
        // identified nodes, parameter nodes, quantifiers and error nodes are always visible
        if (node.hasId() || node.has(Category.PARAM) || node.has(Category.NESTING)
            || jVertex.hasErrors()) {
            return true;
        }
        // All non-sorted nodes should be visible
        var sortAspect = node.get(Category.SORT);
        if (sortAspect == null) {
            return true;
        }
        // in addition, value nodes or data type nodes may be filtered
        if (jGraph.isShowValueNodes()) {
            return true;
        }
        // nodes with expressions should be shown
        if (node.hasExpression() && !node.hasConstant()) {
            return true;
        }
        // data type nodes in type graphs should never be shown
        if (node.getGraphRole() == GraphRole.TYPE) {
            return false;
        }
        // we are now sure that the underlying node has a data type;
        @SuppressWarnings({"cast", "unchecked"})
        var edges = (Set<AspectJEdge>) jVertex.getPort().getEdges();
        // any non-source-label of an incoming edge makes the node visible
        return edges.stream().anyMatch(e -> e.getTargetVertex() == jVertex && !e.isSourceLabel());
    }

    private boolean getAspectEdgeValue(AspectJGraph jGraph, AspectJEdge jEdge) {
        // anything explicitly filtered by the level tree is not visible
        RuleLevelTree levelTree = jGraph.getLevelTree();
        if (levelTree != null && !levelTree.isVisible(jEdge)) {
            return false;
        }
        return getBasicEdgeValue(jGraph, jEdge);
    }

    private boolean getLTSVertexValue(LTSJGraph jGraph, LTSJVertex jVertex) {
        GraphState state = jVertex.getNode();
        if (!jVertex.hasVisibleFlag()) {
            return false;
        }
        if (!jGraph.isShowAbsentStates() && state.isAbsent()) {
            return false;
        }
        if (!jGraph.isShowRecipeSteps() && state.isInner() && state.isFull()) {
            return false;
        }
        if (jVertex.isStart() || jVertex.isFinal() || !jVertex.isClosed()) {
            return true;
        }
        if (hasVisibleIncidentEdge(jGraph, jVertex)) {
            return true;
        }
        return false;
    }

    private boolean getLTSEdgeValue(LTSJGraph jGraph, LTSJEdge jEdge) {
        GraphTransition trans = jEdge.getEdge();
        if (!jEdge.hasVisibleFlag()) {
            return false;
        }
        if (!jGraph.isShowRecipeSteps() && trans.isInnerStep() && trans.source().isFull()) {
            return false;
        }
        if (!getBasicEdgeValue(jGraph, jEdge)) {
            return false;
        }
        return true;
    }

    /**
     * Callback method to test if this node has an incident edge
     * with nonempty (unfiltered) label text.
     * This is to determine the visibility of the node.
     */
    private <G extends @NonNull Graph> boolean hasVisibleIncidentEdge(@NonNull JGraph<G> jGraph,
                                                                      JVertex<G> jVertex) {
        boolean result = false;
        LabelTree<G> labelTree = jGraph.getLabelTree();
        if (labelTree == null) {
            result = true;
        } else {
            Iterator<? extends JEdge<G>> iter = jVertex.getContext();
            while (iter.hasNext()) {
                if (labelTree.isIncluded(iter.next())) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
}
