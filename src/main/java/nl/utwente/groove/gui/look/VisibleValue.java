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

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.view.AspectViewEdge;
import nl.utwente.groove.gui.view.AspectGraphViewController;
import nl.utwente.groove.gui.view.AspectViewVertex;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.ViewEdge;
import nl.utwente.groove.gui.view.GraphViewController;
import nl.utwente.groove.gui.view.ViewVertex;
import nl.utwente.groove.gui.view.LTSViewCell;
import nl.utwente.groove.gui.view.LTSViewEdge;
import nl.utwente.groove.gui.view.LTSGraphViewController;
import nl.utwente.groove.gui.view.LTSViewVertex;
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
    public <G extends @NonNull Graph> Boolean get(GraphViewController<G> controller, ViewCell<G> cell) {
        boolean result = true;
        boolean isVertex = cell instanceof ViewVertex;
        assert controller != null; // should be the case by the time this method gets called
        if (cell instanceof AspectViewCell) {
            result = isVertex
                ? getAspectVertexValue((AspectGraphViewController) controller, (AspectViewVertex) cell)
                : getAspectEdgeValue((AspectGraphViewController) controller, (AspectViewEdge) cell);
        } else if (cell instanceof LTSViewCell) {
            result = isVertex
                ? getLTSVertexValue((LTSGraphViewController) controller, (LTSViewVertex) cell)
                : getLTSEdgeValue((LTSGraphViewController) controller, (LTSViewEdge) cell);
        } else if (cell instanceof ViewVertex) {
            result = isVertex
                ? getBasicVertexValue(controller, (ViewVertex<G>) cell)
                : getBasicEdgeValue(controller, (ViewEdge<G>) cell);
        }
        return result;
    }

    private <G extends @NonNull Graph> boolean getBasicVertexValue(GraphViewController<G> controller,
                                                                   ViewVertex<G> jVertex) {
        LabelTree<G> labelTree = controller.getLabelTree();
        return labelTree == null || labelTree.isIncluded(jVertex);
    }

    private <G extends @NonNull Graph> boolean getBasicEdgeValue(GraphViewController<G> controller, ViewEdge<G> jEdge) {
        boolean result = true;
        ViewVertex<?> source = jEdge.getSourceVertex();
        ViewVertex<?> target = jEdge.getTargetVertex();
        if (source == null || !source.getVisuals().isVisible()) {
            return false;
        }
        if (target == null || !target.getVisuals().isVisible()) {
            return false;
        }
        LabelTree<G> labelTree = controller.getLabelTree();
        if (labelTree != null) {
            result = labelTree.isIncluded(jEdge);
        }
        return result;
    }

    private boolean getAspectVertexValue(AspectGraphViewController controller, AspectViewVertex jVertex) {
        AspectNode node = jVertex.getNode();
        // remark nodes are always visible
        if (node.has(REMARK)) {
            return true;
        }
        // anything explicitly filtered by the level tree is not visible
        RuleLevelTree levelTree = controller.getLevelTree();
        if (levelTree != null && !levelTree.isVisible(jVertex)) {
            return false;
        }
        // anything declared invisible by the super method is not visible
        if (!getBasicVertexValue(controller, jVertex)) {
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
        if (controller.isShowValueNodes()) {
            return true;
        }
        // nodes with expressions should be shown
        if (node.hasExpression() && !node.hasConstant()) {
            return true;
        }
        // data nodes with test edges should always be shown
        if (jVertex.getEdges().stream().anyMatch(e -> e.has(AspectKind.TEST))) {
            return true;
        }
        // data type nodes in type graphs should never be shown
        if (node.getGraphRole() == GraphRole.TYPE) {
            return false;
        }
        // we are now sure that the underlying node has a data type;
        // any non-source-label of an incoming edge makes the node visible
        var edgeIter = jVertex.getContext();
        while (edgeIter.hasNext()) {
            var e = edgeIter.next();
            if (e.getTargetVertex() == jVertex && (e.isLoop() || !e.isSourceLabel())) {
                return true;
            }
        }
        return false;
    }

    private boolean getAspectEdgeValue(AspectGraphViewController controller, AspectViewEdge jEdge) {
        // anything explicitly filtered by the level tree is not visible
        RuleLevelTree levelTree = controller.getLevelTree();
        if (levelTree != null && !levelTree.isVisible(jEdge)) {
            return false;
        }
        return getBasicEdgeValue(controller, jEdge);
    }

    private boolean getLTSVertexValue(LTSGraphViewController controller, LTSViewVertex jVertex) {
        GraphState state = jVertex.getNode();
        if (!jVertex.hasVisibleFlag()) {
            return false;
        }
        if (!controller.isShowAbsentStates() && state.isAbsent()) {
            return false;
        }
        if (!controller.isShowRecipeSteps() && state.isInner() && state.isFull()) {
            return false;
        }
        if (jVertex.isStart() || jVertex.isFinal() || !jVertex.isClosed()) {
            return true;
        }
        if (hasVisibleIncidentEdge(controller, jVertex)) {
            return true;
        }
        return false;
    }

    private boolean getLTSEdgeValue(LTSGraphViewController controller, LTSViewEdge jEdge) {
        GraphTransition trans = jEdge.getEdge();
        if (!jEdge.hasVisibleFlag()) {
            return false;
        }
        if (!controller.isShowRecipeSteps() && trans.isInnerStep() && trans.source().isFull()) {
            return false;
        }
        if (!getBasicEdgeValue(controller, jEdge)) {
            return false;
        }
        return true;
    }

    /**
     * Callback method to test if this node has an incident edge
     * with nonempty (unfiltered) label text.
     * This is to determine the visibility of the node.
     */
    private <G extends @NonNull Graph> boolean hasVisibleIncidentEdge(@NonNull GraphViewController<G> controller,
                                                                      ViewVertex<G> jVertex) {
        boolean result = false;
        LabelTree<G> labelTree = controller.getLabelTree();
        if (labelTree == null) {
            result = true;
        } else {
            Iterator<? extends ViewEdge<G>> iter = jVertex.getContext();
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
