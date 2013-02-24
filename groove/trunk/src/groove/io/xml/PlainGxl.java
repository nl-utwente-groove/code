// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: DefaultGxl.java,v 1.21 2007-12-03 08:55:18 rensink Exp $
 */
package groove.io.xml;

import static groove.io.FileType.LAYOUT_FILTER;
import groove.grammar.model.FormatException;
import groove.graph.Graph;
import groove.graph.GraphInfo;
import groove.graph.plain.PlainEdge;
import groove.graph.plain.PlainGraph;
import groove.graph.plain.PlainNode;
import groove.gui.layout.LayoutMap;
import groove.io.LayoutIO;
import groove.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Class to convert graphs to GXL format and back. Currently the conversion only
 * supports binary edges. This class is implemented using data binding.
 * @author Arend Rensink
 * @version $Revision: 2973 $
 */
public class PlainGxl extends AbstractGxl<PlainNode,PlainEdge,PlainGraph> {

    /** Returns the singleton instance of this class. */
    public static PlainGxl getInstance() {
        return INSTANCE;
    }

    private PlainGxl() {
        // Private to avoid object creation. Use getInstance() method.
    }

    /** Marshaller/unmarshaller. */
    static private final DefaultJaxbGxlIO io = DefaultJaxbGxlIO.getInstance();

    private static final PlainGxl INSTANCE = new PlainGxl();

    @Override
    public void marshalGraph(Graph graph, File file) throws IOException {
        super.marshalGraph(graph, file);
        // layout is now saved in the gxl file; delete the layout file
        deleteFile(toLayoutFile(file));
    }

    @Override
    public void deleteGraph(File file) {
        super.deleteGraph(file);
        // delete the layout file as well, if any
        deleteFile(toLayoutFile(file));
    }

    @Override
    protected GxlIO<PlainNode,PlainEdge,PlainGraph> getIO() {
        return io;
    }

    /* Overridden for backward compatibility: get layout from external file. */
    @Override
    protected Pair<PlainGraph,Map<String,PlainNode>> unmarshalGraphMap(File file)
        throws IOException {
        // first get the non-layed out result
        Pair<PlainGraph,Map<String,PlainNode>> result =
            super.unmarshalGraphMap(file);
        PlainGraph resultGraph = result.one();
        Map<String,PlainNode> nodeMap = result.two();
        File layoutFile = toLayoutFile(file);
        if (layoutFile.exists()) {
            try {
                InputStream in = new FileInputStream(layoutFile);
                try {
                    LayoutMap layout =
                        LayoutIO.getInstance().readLayout(nodeMap, in);
                    GraphInfo.setLayoutMap(resultGraph, layout);
                } catch (FormatException exc) {
                    GraphInfo.addErrors(resultGraph, exc.getErrors());
                }
            } catch (IOException e) {
                // we do nothing when there is no layout found
            }
        }
        return result;
    }

    /**
     * Converts a file containing a graph to the file containing the graph's
     * layout information, by adding <code>Groove.LAYOUT_EXTENSION</code> to the
     * file name.
     */
    private File toLayoutFile(File graphFile) {
        return new File(LAYOUT_FILTER.addExtension(graphFile.toString()));
    }
}