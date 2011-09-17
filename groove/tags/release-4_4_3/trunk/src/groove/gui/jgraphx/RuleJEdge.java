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

import groove.gui.layout.JEdgeLayout;
import groove.gui.layout.LayoutMap;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectNode;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class RuleJEdge extends mxCell {

    private final RuleJGraph ruleJGraph;
    private final AspectEdge ruleEdge;

    public RuleJEdge(RuleJGraph ruleJGraph, AspectEdge ruleEdge) {
        super();
        this.ruleJGraph = ruleJGraph;
        this.ruleEdge = ruleEdge;
        this.setEdge(true);

        this.setValue(this.getInscription());

        this.setGeometry();
        this.setStyle();
        this.setVisibility();
    }

    public String getInscription() {
        return this.getLabelText();
    }

    private void setGeometry() {
        mxGeometry geo = new mxGeometry();
        geo.setRelative(true);

        LayoutMap<AspectNode,AspectEdge> layoutMap =
            this.ruleJGraph.getRuleGraph().getInfo().getLayoutMap();
        JEdgeLayout layout = layoutMap.getLayout(this.ruleEdge);
        if (layout != null) {
            List<mxPoint> mxPoints = new ArrayList<mxPoint>();
            for (Point2D point : layout.getPoints()) {
                mxPoints.add(new mxPoint(point));
            }
            geo.setPoints(mxPoints);
        }

        this.setGeometry(geo);
    }

    private void setStyle() {
        String prefix = this.getPrefix();
        if (prefix.equals("new")) {
            this.setStyle(JGraphXStyles.CREATOR_EDGE_STYLE);
        } else if (prefix.equals("del")) {
            this.setStyle(JGraphXStyles.ERASER_EDGE_STYLE);
        } else {
            this.setStyle(JGraphXStyles.READER_EDGE_STYLE);
        }
    }

    private void setVisibility() {
        String prefix = this.getPrefix();
        boolean visible = !(prefix.equals("type") || prefix.equals("flag"));
        this.setVisible(visible);
    }

    private String getPrefix() {
        return this.ruleEdge.label().text().split(":")[0];
    }

    private String getLabelText() {
        return this.ruleEdge.label().text().split(":")[1];
    }
}
