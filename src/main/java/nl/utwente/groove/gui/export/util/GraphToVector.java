/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.gui.export.util;

import java.awt.Graphics2D;
import java.io.File;

import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.io.external.PortException;

/**
 * Simple interface between VectorFormat and GraphToPDF/EPS. Allows GROOVE to be loaded w/o PDF/EPS support
 * @author Harold
 * @version $Revision$
 */
public abstract class GraphToVector {
    /** Saves the graph shown on a given canvas to a file according to this vector format. */
    public abstract void renderGraph(GraphCanvas<?> canvas, File file) throws PortException;

    /** Paints the graph shown on a given canvas in a {@link Graphics2D} object. */
    protected void toGraphics(GraphCanvas<?> canvas, Graphics2D graphics) {
        canvas.paintGraph(graphics);
    }
}
