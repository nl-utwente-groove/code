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
package groove.gui.look;

import static groove.view.aspect.AspectKind.REMARK;
import groove.graph.GraphRole;
import groove.gui.jgraph.AspectJCell;
import groove.gui.jgraph.AspectJEdge;
import groove.gui.jgraph.AspectJVertex;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJEdge;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.GraphJVertex;
import groove.gui.jgraph.LTSJCell;
import groove.gui.jgraph.LTSJEdge;
import groove.gui.jgraph.LTSJVertex;
import groove.gui.tree.LabelTree;
import groove.gui.tree.RuleLevelTree;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.view.aspect.Aspect;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectNode;

/**
 * Strategy to determine whether a given cell is currently visible.
 * @author Arend Rensink
 * @version $Revision $
 */
public class VisibleValue implements VisualValue<Boolean> {
    @Override
    public Boolean get(GraphJCell cell) {
        boolean result = true;
        boolean isVertex = cell instanceof GraphJVertex;
        if (cell instanceof AspectJCell) {
            result =
                isVertex ? getAspectVertexValue((AspectJVertex) cell)
                        : getAspectEdgeValue((AspectJEdge) cell);
        } else if (cell instanceof LTSJCell) {
            result =
                isVertex ? getLTSVertexValue((LTSJVertex) cell)
                        : getLTSEdgeValue((LTSJEdge) cell);
        } else if (cell instanceof GraphJVertex) {
            result =
                isVertex ? getBasicVertexValue((GraphJVertex) cell)
                        : getBasicEdgeValue((GraphJEdge) cell);
        }
        return result;
    }

    private boolean getBasicVertexValue(GraphJVertex jVertex) {
        GraphJGraph jGraph = jVertex.getJGraph();
        LabelTree labelTree = jGraph.getLabelTree();
        if (labelTree == null || !labelTree.isFiltered(jVertex)) {
            return true;
        }
        if (!jGraph.isShowUnfilteredEdges()) {
            return false;
        }
        if (hasVisibleIncidentEdge(jVertex)) {
            return true;
        }
        return false;
    }

    private boolean getBasicEdgeValue(GraphJEdge jEdge) {
        boolean result = true;
        GraphJVertex source = jEdge.getSourceVertex();
        GraphJVertex target = jEdge.getTargetVertex();
        if (source == null || !source.getVisuals().isVisible()) {
            return false;
        }
        if (target == null || !target.getVisuals().isVisible()) {
            return false;
        }
        LabelTree labelTree = jEdge.getJGraph().getLabelTree();
        if (labelTree != null) {
            result = !labelTree.isFiltered(jEdge);
        }
        return result;
    }

    private boolean getAspectVertexValue(AspectJVertex jVertex) {
        AspectNode node = jVertex.getNode();
        AspectKind aspect = jVertex.getAspect();
        // remark nodes are always visible
        if (aspect == REMARK) {
            return true;
        }
        // anything explicitly filtered by the level tree is not visible
        RuleLevelTree levelTree = jVertex.getJGraph().getLevelTree();
        if (levelTree != null && !levelTree.isVisible(jVertex)) {
            return false;
        }
        // parameter nodes, quantifiers and error nodes are always visible
        if (node.hasParam() || aspect.isQuantifier() || jVertex.hasErrors()) {
            return true;
        }
        // anything declared invisible by the super method is not visible
        if (!getBasicVertexValue(jVertex)) {
            return false;
        }
        Aspect attr = node.getAttrAspect();
        // explicit product nodes should be visible
        if (attr == null || !attr.getKind().hasSignature()) {
            return true;
        }
        // in addition, value nodes or data type nodes may be filtered
        if (jVertex.getJGraph().isShowValueNodes()) {
            return true;
        }
        // data type nodes in type graphs should never be shown
        if (node.getGraphRole() == GraphRole.TYPE) {
            return false;
        }
        // we are now sure that the underlying node has a data type;
        // variable nodes should be shown
        if (!attr.hasContent()) {
            return true;
        }
        // any regular expression edge on the node makes it visible
        for (Object jEdge : jVertex.getPort().getEdges()) {
            AspectEdge edge = ((AspectJEdge) jEdge).getEdge();
            if (edge.getRuleLabel() != null && !edge.getRuleLabel().isAtom()) {
                return true;
            }
        }
        return false;
    }

    private boolean getAspectEdgeValue(AspectJEdge jEdge) {
        // anything explicitly filtered by the level tree is not visible
        RuleLevelTree levelTree = jEdge.getJGraph().getLevelTree();
        if (levelTree != null && !levelTree.isVisible(jEdge)) {
            return false;
        }
        return getBasicEdgeValue(jEdge);
    }

    private boolean getLTSVertexValue(LTSJVertex jVertex) {
        GraphState state = jVertex.getNode();
        if (!jVertex.hasVisibleFlag()) {
            return false;
        }
        if (!jVertex.getJGraph().isShowPartialTransitions()
            && jVertex.isTransient() && state.isDone()) {
            return false;
        }
        if (jVertex.isStart() || jVertex.isFinal() || !jVertex.isClosed()) {
            return true;
        }
        if (hasVisibleIncidentEdge(jVertex)) {
            return true;
        }
        return false;
    }

    private boolean getLTSEdgeValue(LTSJEdge jEdge) {
        GraphTransition trans = jEdge.getEdge();
        if (!jEdge.hasVisibleFlag()) {
            return false;
        }
        if (!jEdge.getJGraph().isShowPartialTransitions() && trans.isPartial()
            && trans.source().isDone()) {
            return false;
        }
        if (!getBasicEdgeValue(jEdge)) {
            return false;
        }
        return true;
    }

    /**
     * Callback method to test if this node has an incident edge
     * with nonempty (unfiltered) label text.
     * This is to determine the visibility of the node.
     */
    private boolean hasVisibleIncidentEdge(GraphJVertex jVertex) {
        boolean result = false;
        LabelTree labelTree = jVertex.getJGraph().getLabelTree();
        for (GraphJEdge jEdge : jVertex.getJEdges()) {
            if (labelTree == null || !labelTree.isFiltered(jEdge)) {
                result = true;
                break;
            }
        }
        return result;
    }
}
