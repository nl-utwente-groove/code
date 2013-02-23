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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/** 
 * Class that implements saving graphs as JPEG images.
 * Loading in this format is unsupported.
 * 
 * @author Arend Rensink 
 */

public class JpgFormat extends AbstractExternalFileFormat<Graph> {

    private static final JpgFormat INSTANCE = new JpgFormat();

    /** Returns the singleton instance of this class. */
    public static final JpgFormat getInstance() {
        return INSTANCE;
    }

    private JpgFormat() {
        super(FileType.JPG);
    }

    // Methods from FileFormat.

    @Override
    public void load(Graph graph, File file) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(JGraph<Graph> jGraph, File file) throws IOException {
        BufferedImage image = jGraph.toImage();
        if (image == null) {
            throw new IOException("Cannot export blank image");
        }
        String format = this.getFilter().getExtension().substring(1);
        ImageIO.write(image, format, file);
    }

    @Override
    public void save(Graph graph, File file) throws IOException {
        throw new UnsupportedOperationException();
    }

    // Methods from Xml

    @Override
    public Graph createGraph(String graphName) {
        throw new UnsupportedOperationException();
    }

}
