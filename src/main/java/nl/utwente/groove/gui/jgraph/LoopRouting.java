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
package nl.utwente.groove.gui.jgraph;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.jgraph.graph.Edge;
import org.jgraph.graph.Edge.Routing;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.VertexView;

import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.view.LoopRouter;
import nl.utwente.groove.gui.view.ViewEdge;

/**
 * JGraph edge routing that gives loops without bends of their own the default points
 * computed by the neutral {@link LoopRouter}, and stores them in the cell's visuals.
 * @author Arend Rensink
 * @version $Revision$
 */
final class LoopRouting implements Routing {
    @Override
    public int getPreferredLineStyle(EdgeView edge) {
        if (isRoutable(edge)) {
            return GraphConstants.STYLE_SPLINE;
        } else {
            return Edge.Routing.NO_PREFERENCE;
        }
    }

    @Override
    public List<?> route(GraphLayoutCache cache, EdgeView edgeView) {
        List<Point2D> result = null;
        if (isRoutable(edgeView)) {
            ViewEdge<?> jEdge = (ViewEdge<?>) edgeView.getCell();
            // find out the source bounds
            VertexView sourceView = (VertexView) edgeView.getSource().getParentView();
            // first refresh the source view, otherwise the view bounds
            // might be out of date
            sourceView.refresh(cache, cache, true);
            JGraph<?> jGraph = (JGraph<?>) jEdge.getCanvas();
            assert jGraph != null; // guaranteed by now
            jGraph.updateAutoSize(sourceView);
            Rectangle2D sourceBounds = sourceView.getBounds();
            result = LoopRouter.route(jEdge, sourceBounds);
            VisualMap visuals = jEdge.getVisuals();
            visuals.setPoints(result);
            GraphConstants.setPoints(edgeView.getAllAttributes(), result);
        }
        return result;
    }

    /** Determines if this edge should be routed. */
    private boolean isRoutable(EdgeView edgeView) {
        if (edgeView.getSource() == null) {
            return false;
        }
        if (!edgeView.isLoop()) {
            return false;
        }
        JGraph<?> jGraph = (JGraph<?>) ((ViewEdge<?>) edgeView.getCell()).getCanvas();
        assert jGraph != null; // known by now
        if (jGraph.isLayouting()) {
            return false;
        }
        return edgeView.getPointCount() <= 2;
    }
}
