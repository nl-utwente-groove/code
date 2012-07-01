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
package groove.io.importers;

import groove.algebra.Algebra;
import groove.algebra.AlgebraFamily;
import groove.graph.EdgeRole;
import groove.graph.TypeLabel;
import groove.graph.algebra.ValueNode;
import groove.io.ExtensionFilter;
import groove.trans.DefaultHostGraph;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.util.Groove;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/** Converts files in .col format to Groove graphs. 
 * The format is described in <a href="http://mat.gsia.cmu.edu/COLOR/general/ccformat.ps">http://mat.gsia.cmu.edu/COLOR/general/ccformat.ps</a>.
 * See <a href="http://mat.gsia.cmu.edu/COLOR/instances.html">http://mat.gsia.cmu.edu/COLOR/instances.html</a>
 * for example graphs in this format. 
 */
public class ColToGraph {

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
    private static HostGraph convert(File inFile) throws IOException {
        DefaultHostGraph result = new DefaultHostGraph("graph");
        System.out.printf("Converting %s%n", inFile.getCanonicalPath());
        BufferedReader reader = new BufferedReader(new FileReader(inFile));
        Algebra<?> intAlgebra = AlgebraFamily.getInstance().getAlgebraFor("0");
        TypeLabel valueLabel = TypeLabel.createBinaryLabel("value");
        for (String nextLine = reader.readLine(); nextLine != null; nextLine =
            reader.readLine()) {
            if (DEBUG) {
                System.out.println(nextLine);
            }
            String[] fragments = nextLine.split(" ");
            if (fragments[0].equals("n")) {
                HostNode node = addNode(result, fragments[1]);
                ValueNode valueNode =
                    result.addNode(intAlgebra,
                        intAlgebra.getValue(fragments[2]));
                result.addEdge(node, valueLabel, valueNode);
            } else if (fragments[0].equals("e")) {
                HostNode source = addNode(result, fragments[1]);
                HostNode target = addNode(result, fragments[2]);
                result.addEdge(source, LABEL, target);
            }
        }
        if (DEBUG) {
            System.out.println(result);
        }
        return result;
    }

    private static HostNode addNode(HostGraph result, String id) {
        HostNode node = result.addNode(Integer.parseInt(id));
        result.addEdge(node, TypeLabel.createLabel(EdgeRole.FLAG, "i" + id),
            node);
        return node;
    }

    private static final ExtensionFilter colFilter = new ExtensionFilter(
        "DIMACS graph format", ".col");
    private static final TypeLabel LABEL = TypeLabel.createBinaryLabel("n");
    private static final boolean DEBUG = false;
}