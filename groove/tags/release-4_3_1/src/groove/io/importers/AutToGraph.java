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
package groove.io.importers;

import groove.graph.Graph;
import groove.graph.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads in a graph from CADP .aut format.
 * @author Arend Rensink.
 */
public class AutToGraph {

    /** Reads in a graph from CADP .aut format. */
    static public <N extends Node> Map<String,N> convert(InputStream reader,
            Graph<N,?> graph) throws IOException {
        Map<String,N> result = new HashMap<String,N>();
        BufferedReader in = new BufferedReader(new InputStreamReader(reader));
        int linenr = 0;
        try {
            String line = in.readLine();
            linenr++;
            int rootStart = line.indexOf('(') + 1;
            int edgeCountStart = line.indexOf(',') + 1;
            int root =
                Integer.parseInt(line.substring(rootStart, edgeCountStart - 1).trim());
            N rootNode = graph.addNode(root);
            result.put("" + root, rootNode);
            graph.addEdge(rootNode, ROOT_LABEL, rootNode);
            for (line = in.readLine(); line != null; line = in.readLine()) {
                linenr++;
                if (line.trim().length() > 0) {
                    int sourceStart = line.indexOf('(') + 1;
                    int labelStart = line.indexOf(',') + 1;
                    int targetStart = line.lastIndexOf(',') + 1;
                    int source =
                        Integer.parseInt(line.substring(sourceStart,
                            labelStart - 1).trim());
                    String label = line.substring(labelStart, targetStart - 1);
                    int target =
                        Integer.parseInt(line.substring(targetStart,
                            line.lastIndexOf(')')).trim());
                    N sourceNode = graph.addNode(source);
                    N targetNode = graph.addNode(target);
                    result.put("" + source, sourceNode);
                    result.put("" + target, targetNode);
                    graph.addEdge(sourceNode, label, targetNode);
                }
            }
        } catch (Exception e) {
            throw new IOException(String.format("Format error in line %d: %s",
                linenr, e.getMessage()));
        }
        return result;
    }

    /** Label used to identify the start state, when reading in from .aut */
    private static final String ROOT_LABEL = "$ROOT$";

}
