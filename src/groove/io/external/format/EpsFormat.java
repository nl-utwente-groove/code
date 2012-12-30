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
package groove.io.external.format;

import groove.graph.Graph;
import groove.gui.jgraph.JGraph;
import groove.io.FileType;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.sf.epsgraphics.ColorMode;
import net.sf.epsgraphics.EpsGraphics;

/** 
 * Class that implements saving graphs as EPS (Embedded PostScript) images.
 * Loading in this format is unsupported.
 * 
 * @author Arend Rensink 
 */

public class EpsFormat extends AbstractExternalFileFormat<Graph<?,?>> {

    private static final EpsFormat INSTANCE = new EpsFormat();

    /** Returns the singleton instance of this class. */
    public static final EpsFormat getInstance() {
        return INSTANCE;
    }

    private EpsFormat() {
        super(FileType.EPS);
    }

    // Methods from FileFormat.

    @Override
    public void load(Graph<?,?> graph, File file) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(JGraph<Graph<?,?>> jGraph, File file)
        throws IOException {
        // Create a graphics contents on the buffered image
        BufferedImage image = jGraph.toImage();
        if (image == null) {
            throw new IOException("Cannot export blank image");
        }
        // Create an output stream
        OutputStream out = new FileOutputStream(file);
        // minX,minY,maxX,maxY
        EpsGraphics g2d =
            new EpsGraphics("Title", out, 0, 0, image.getWidth(),
                image.getHeight(), ColorMode.COLOR_RGB);
        g2d.drawImage(jGraph.toImage(), new AffineTransform(), null);
        g2d.close(); // This also closes the OutputStream...
    }

    @Override
    public void save(Graph<?,?> graph, File file) throws IOException {
        throw new UnsupportedOperationException();
    }

    // Methods from Xml

    @Override
    public Graph<?,?> createGraph(String graphName) {
        throw new UnsupportedOperationException();
    }

}
