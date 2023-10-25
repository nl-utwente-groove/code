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
 * $Id: GraphToSVG.java 6232 2023-10-17 15:49:19Z rensink $
 */
package nl.utwente.groove.io.external.util;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;

import nl.utwente.groove.gui.jgraph.JGraph;
import nl.utwente.groove.io.external.PortException;

/** Class offering the functionality to save a JGraph to EPS format. */
public class GraphToSVG extends GraphToVector {
    @Override
    public void renderGraph(JGraph<?> graph, File file) throws PortException {
        // Get graph bounds. If not available, do nothing (probably empty graph)
        Rectangle2D bounds = graph.getGraphBounds();
        if (bounds == null) {
            return;
        }

        try (FileWriter fos = new FileWriter(file);) {
            // Get a DOMImplementation.
            DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

            // Create an instance of org.w3c.dom.Document.
            String svgNS = "http://www.w3.org/2000/svg";
            var document = domImpl.createDocument(svgNS, graph.getModel().getName(), null);

            var svgDocumentElement = document.getDocumentElement();
            svgDocumentElement.setAttribute("height", String.valueOf(bounds.getHeight()));
            svgDocumentElement.setAttribute("width", String.valueOf(bounds.getWidth()));

            // Create an instance of the SVG Generator.
            SVGGraphics2D svgg2d = new SVGGraphics2D(document);
            svgg2d.setSVGCanvasSize(bounds.getBounds().getSize());
            toGraphics(graph, svgg2d);
            svgg2d.stream(fos);
        } catch (IOException e) {
            throw new PortException(e);
        }
    }
}
