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
import groove.gui.Options;
import groove.gui.jgraph.AspectJCell;
import groove.gui.jgraph.AspectJEdge;
import groove.gui.jgraph.AspectJGraph;
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
public class VisibleValue implements VisualValue {
    /** Constructs a value strategy for a given JGraph. */
    public VisibleValue(GraphJGraph jGraph) {
        this.options = jGraph.getOptions();
        this.labelTree = jGraph.getLabelTree();
        if (jGraph instanceof AspectJGraph) {
            this.levelTree = ((AspectJGraph) jGraph).getLevelTree();
        }
    }

    /** Sets a label-filtering tree to be taken into account for computing the visibility. */
    public void setLabelTree(LabelTree labelTree) {
        this.labelTree = labelTree;
    }

    /** Sets a quantifier nesting to be taken into account for computing the visibility. */
    public void setLevelTree(RuleLevelTree levelTree) {
        this.levelTree = levelTree;
    }

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
        boolean result = true;
        if (this.labelTree != null) {
            result = !this.labelTree.isFiltered(jVertex);
        }
        if (!result) {
            result =
                getOptionValue(Options.SHOW_UNFILTERED_EDGES_OPTION)
                    && hasVisibleIncidentEdge(jVertex);
        }
        return result;
    }

    private boolean getBasicEdgeValue(GraphJEdge jEdge) {
        boolean result = true;
        GraphJVertex source = jEdge.getSourceVertex();
        GraphJVertex target = jEdge.getTargetVertex();
        if (source == null || !source.getVisuals().isVisible()) {
            result = false;
        } else if (target == null || !target.getVisuals().isVisible()) {
            result = false;
        } else if (this.labelTree != null) {
            result = !this.labelTree.isFiltered(jEdge);
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
        RuleLevelTree levelTree = this.levelTree;
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
        if (getOptionValue(Options.SHOW_VALUE_NODES_OPTION)) {
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
        RuleLevelTree levelTree = this.levelTree;
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
        if (!getOptionValue(Options.SHOW_PARTIAL_GTS_OPTION)
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
        if (!getOptionValue(Options.SHOW_PARTIAL_GTS_OPTION)
            && trans.isPartial() && trans.source().isDone()) {
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
        for (GraphJEdge jEdge : jVertex.getJEdges()) {
            if (this.labelTree == null || !this.labelTree.isFiltered(jEdge)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Retrieves the value for a given option from the options object, or
     * <code>null</code> if the options are not set (i.e., <code>null</code>).
     * @param option the name of the option
     */
    private boolean getOptionValue(String option) {
        return this.options.getItem(option).isEnabled()
            && this.options.isSelected(option);
    }

    private final Options options;
    private LabelTree labelTree;
    private RuleLevelTree levelTree;
}
