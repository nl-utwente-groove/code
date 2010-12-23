/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.gui.jgraphx;

import groove.gui.layout.LayoutMap;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectValue;
import groove.view.aspect.RuleAspect;

import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class RuleJNode extends mxCell {

    private final RuleJGraph ruleJGraph;
    private final AspectNode ruleNode;
    private final Set<RuleJEdge> outEdges;

    public RuleJNode(RuleJGraph ruleJGraph, AspectNode ruleNode) {
        super();
        this.ruleJGraph = ruleJGraph;
        this.ruleNode = ruleNode;
        this.outEdges = new HashSet<RuleJEdge>();
        this.setVertex(true);

        this.setGeometry();
        this.setStyle();
        this.setVisibility();
    }

    private void setGeometry() {
        LayoutMap<AspectNode,AspectEdge> layoutMap =
            this.ruleJGraph.getRuleGraph().getInfo().getLayoutMap();
        Rectangle2D rect = layoutMap.getLayout(this.ruleNode).getBounds();
        mxGeometry geo =
            new mxGeometry(rect.getX(), rect.getY(), rect.getWidth(),
                rect.getHeight());
        this.setGeometry(geo);
    }

    private void setStyle() {
        AspectValue nodeType = this.ruleNode.getType();
        if (RuleAspect.CREATOR.equals(nodeType)) {
            this.setStyle(JGraphXStyles.CREATOR_NODE_STYLE);
        } else if (RuleAspect.ERASER.equals(nodeType)) {
            this.setStyle(JGraphXStyles.ERASER_NODE_STYLE);
        } else {
            this.setStyle(JGraphXStyles.READER_NODE_STYLE);
        }
    }

    private void setVisibility() {
        this.setVisible(true);
    }

    public void setInscription() {
        this.setValue(this.getInscription());
        this.ruleJGraph.cellSizeUpdated(this, false);
    }

    private String getInscription() {
        StringBuilder builder = new StringBuilder();
        for (RuleJEdge ruleJEdge : this.outEdges) {
            if (!ruleJEdge.isVisible()) {
                builder.append(ruleJEdge.getInscription() + "\n");
            }
        }
        return builder.toString();
    }

    public void addOutEdge(RuleJEdge ruleJEdge) {
        this.outEdges.add(ruleJEdge);
    }

}
