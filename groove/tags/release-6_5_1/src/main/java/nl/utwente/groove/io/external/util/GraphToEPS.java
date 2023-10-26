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
package nl.utwente.groove.io.external.util;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.xmlgraphics.java2d.ps.EPSDocumentGraphics2D;

import nl.utwente.groove.gui.jgraph.JGraph;
import nl.utwente.groove.io.external.PortException;

/** Class offering the functionality to save a JGraph to EPS format. */
public class GraphToEPS extends GraphToVector {
    @Override
    public void renderGraph(JGraph<?> graph, File file) throws PortException {
        // Get graph bounds. If not available, do nothing (probably empty graph)
        Rectangle2D bounds = graph.getGraphBounds();
        if (bounds == null) {
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(file);) {
            var epsg2d = new EPSDocumentGraphics2D(false);
            epsg2d.setGraphicContext(new org.apache.xmlgraphics.java2d.GraphicContext());
            epsg2d.setupDocument(fos, (int) bounds.getWidth(), (int) bounds.getHeight());
            toGraphics(graph, epsg2d);
            epsg2d.finish();
        } catch (IOException e) {
            throw new PortException(e);
        }
    }
}
