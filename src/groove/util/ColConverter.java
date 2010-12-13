/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.util;

import groove.algebra.Algebra;
import groove.algebra.AlgebraRegister;
import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.TypeLabel;
import groove.graph.algebra.ValueNode;
import groove.io.ExtensionFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/** Converts files in .col format to Groove graphs. 
 * The format is described in <a href="http://mat.gsia.cmu.edu/COLOR/general/ccformat.ps">http://mat.gsia.cmu.edu/COLOR/general/ccformat.ps</a>.
 * See <a href="http://mat.gsia.cmu.edu/COLOR/instances.html">http://mat.gsia.cmu.edu/COLOR/instances.html</a>
 * for example graphs in this format. 
 */
public class ColConverter {

    /**
     * Should be called with a list of filenames and/or directories to be converted.
     */
    public static void main(String[] args) {
        for (String filename : args) {
            try {
                process(filename);
            } catch (IOException exc) {
                System.err.printf("Error convertion %s: %s", filename,
                    exc.getMessage());
            }
        }
    }

    /**
     * Processes a filename by converting it from .col format to .gst format.
     * If the filename refers to a directory, recursively descends into it.
     */
    private static void process(String filename) throws IOException {
        String pureFilename = ExtensionFilter.getPureName(filename);
        if (new File(filename).isDirectory()) {
            for (File subfile : new File(filename).listFiles(colFilter)) {
                process(subfile.getCanonicalPath());
            }
        } else {
            File inFile = new File(colFilter.addExtension(filename));
            File outFile =
                new File(Groove.createStateFilter().addExtension(pureFilename));
            Groove.saveGraph(convert(inFile), outFile);
        }
    }

    /** Reads a .col file and returns the corresponding graph. */
    private static Graph convert(File inFile) throws IOException {
        Graph result = new DefaultGraph();
        System.out.printf("Converting %s%n", inFile.getCanonicalPath());
        BufferedReader reader = new BufferedReader(new FileReader(inFile));
        Algebra<?> intAlgebra = AlgebraRegister.getInstance().getAlgebra("0");
        Label valueLabel = DefaultLabel.createLabel("value");
        for (String nextLine = reader.readLine(); nextLine != null; nextLine =
            reader.readLine()) {
            if (DEBUG) {
                System.out.println(nextLine);
            }
            String[] fragments = nextLine.split(" ");
            if (fragments[0].equals("n")) {
                Node node = addNode(result, fragments[1]);
                Node valueNode =
                    ValueNode.createValueNode(intAlgebra,
                        intAlgebra.getValue(fragments[2]));
                result.addNode(valueNode);
                result.addEdge(node, valueLabel, valueNode);
            } else if (fragments[0].equals("e")) {
                Node source = addNode(result, fragments[1]);
                Node target = addNode(result, fragments[2]);
                result.addEdge(source, LABEL, target);
            }
        }
        if (DEBUG) {
            System.out.println(result);
        }
        return result;
    }

    private static Node addNode(Graph result, String id) {
        Node node = result.addNode(Integer.parseInt(id));
        result.addEdge(node, TypeLabel.createLabel("i" + id, Label.FLAG), node);
        return node;
    }

    private static final ExtensionFilter colFilter = new ExtensionFilter(
        "DIMACS graph format", ".col");
    private static final Label LABEL = TypeLabel.createLabel("n");
    private static final boolean DEBUG = false;
}
