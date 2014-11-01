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

import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import net.sf.epsgraphics.ColorMode;
import net.sf.epsgraphics.EpsGraphics;

/** Class offering the functionality to save a JGraph to EPS format. */
public class GraphToEPS extends GraphToVector {
    @Override
    public void renderGraph(JGraph<?> graph, Path file) throws PortException {
        // Get graph bounds. If not available, do nothing (probably empty graph)
        Rectangle2D bounds = graph.getGraphBounds();
        if (bounds == null) {
            return;
        }

        try (OutputStream fos = Files.newOutputStream(file)) {
            EpsGraphics g2d =
                new EpsGraphics(graph.getModel().getName(), fos, 0, 0, (int) bounds.getWidth(),
                    (int) bounds.getHeight(), ColorMode.COLOR_RGB);
            // Render
            toGraphics(graph, g2d);
        } catch (FileNotFoundException e) {
            throw new PortException(e);
        } catch (IOException e) {
            throw new PortException(e);
        }
    }
}
