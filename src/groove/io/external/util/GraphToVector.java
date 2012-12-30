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
package groove.io.external.util;

import groove.gui.jgraph.JGraph;
import groove.io.external.PortException;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.File;

/**
 * Simple interface between VectorFormat and GraphToPDF/EPS. Allows GROOVE to be loaded w/o PDF/EPS support
 * @author Harold
 * @version $Revision $
 */
public abstract class GraphToVector {
    /** Saves a given jGraph to a file according to this vector format. */
    public abstract void renderGraph(JGraph<?> graph, File file)
        throws PortException;

    /** Paints a given jGraph in a {@link Graphics} object. */
    protected void toGraphics(JGraph<?> graph, Graphics2D graphics) {
        Rectangle2D bounds = graph.getGraphBounds();

        graphics.translate(-bounds.getMinX(), -bounds.getMinY());
        double scale = graph.getScale();
        graphics.scale(1.0 / scale, 1.0 / scale);

        graph.toScreen(bounds);

        Object[] selection = graph.getSelectionCells();
        boolean gridVisible = graph.isGridVisible();
        boolean dBuf = graph.isDoubleBuffered();
        graph.setGridVisible(false);
        graph.clearSelection();
        // Turn off double buffering, otherwise everything gets rasterized
        graph.setDoubleBuffered(false);

        graph.paint(graphics);

        graph.setDoubleBuffered(dBuf);
        graph.setSelectionCells(selection);
        graph.setGridVisible(gridVisible);
    }
}
