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
package nl.utwente.groove.io.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.plain.PlainGraph;
import nl.utwente.groove.graph.plain.PlainNode;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.io.FileType;

/**
 * Plain graph reader/writer for the CADP {@code .aut} format.
 * @see FileType#AUT
 * @author Arend Rensink
 * @version $Revision$
 */
public class AutIO extends GraphIO<PlainGraph> {
    /** Saves a graph to a file, streaming it through an {@link AutListener}. */
    @Override
    protected void doSaveGraph(Graph graph, File file) throws IOException {
        new AutListener().write(graph, file);
    }

    @Override
    public boolean canLoad() {
        return true;
    }

    @Override
    public PlainGraph loadGraph(InputStream in) throws IOException {
        PlainGraph result = createGraph();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("aut file is empty");
            }
            int rootStart = line.indexOf('(') + 1;
            int edgeCountStart = line.indexOf(',') + 1;
            Map<Integer,PlainNode> nodeMap = new HashMap<>();
            int root = Integer.parseInt(line.substring(rootStart, edgeCountStart - 1).trim());
            PlainNode rootNode = result.addNode(root);
            nodeMap.put(root, rootNode);
            if (this.rootLabel != null) {
                result.addEdge(rootNode, this.rootLabel, rootNode);
            }
            for (line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.trim().length() > 0) {
                    int sourceStart = line.indexOf('(') + 1;
                    int labelStart = line.indexOf(',') + 1;
                    int targetStart = line.lastIndexOf(',') + 1;
                    int source
                        = Integer.parseInt(line.substring(sourceStart, labelStart - 1).trim());
                    String label = parseLabelField(line.substring(labelStart, targetStart - 1));
                    int target = Integer
                        .parseInt(line.substring(targetStart, line.lastIndexOf(')')).trim());
                    PlainNode sourceNode = nodeMap.get(source);
                    if (sourceNode == null) {
                        sourceNode = result.addNode(source);
                        nodeMap.put(source, sourceNode);
                    }
                    PlainNode targetNode = nodeMap.get(target);
                    if (targetNode == null) {
                        targetNode = result.addNode(target);
                        nodeMap.put(target, targetNode);
                    }
                    result.addEdge(sourceNode, label, targetNode);
                }
            }
        }
        return result;
    }

    @Override
    public PlainGraph loadPlainGraph(InputStream in) throws IOException {
        return loadGraph(in);
    }

    /**
     * Sets the label used to distinguish the root node.
     * By default, there is no root label.
     * @param rootLabel label for the root node; if {@code null}, no label will be added.
     */
    public void setRootLabel(String rootLabel) {
        this.rootLabel = rootLabel;
    }

    private String rootLabel;

    /**
     * Callback factory method for a plain graph with the right name and role.
     * @see #setGraphName(String)
     * @see #setGraphRole(GraphRole)
     */
    private PlainGraph createGraph() {
        // LTS graphs may contain parallel transitions, so they are non-simple;
        // the .aut format has no way to declare this, hence the role decides
        GraphRole role = getGraphRole();
        PlainGraph result = new PlainGraph(getGraphName(), role, role != GraphRole.LTS);
        return result;
    }

    /**
     * Converts label text to its on-disk {@code .aut} field form.
     * A label containing a comma or a quote character is surrounded by
     * quotes, with embedded backslashes and quotes backslash-escaped;
     * any other label is written verbatim.
     * @see #parseLabelField(String)
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    static String toLabelField(String text) {
        String result = text;
        if (text.indexOf(',') >= 0 || text.indexOf('"') >= 0) {
            result = '"' + text.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
        }
        return result;
    }

    /**
     * Converts an on-disk {@code .aut} label field back to label text.
     * A field that (after trimming) is surrounded by quotes is unquoted
     * and backslash-unescaped; any other field is taken verbatim.
     * @see #toLabelField(String)
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    static private String parseLabelField(String field) {
        String result = field;
        String trimmed = field.trim();
        if (trimmed.length() >= 2 && trimmed.charAt(0) == '"'
            && trimmed.charAt(trimmed.length() - 1) == '"') {
            // unescape \" before \\; every quote in the escaped text is part
            // of a \" pair, so the replacements cannot match across pairs
            result = trimmed
                .substring(1, trimmed.length() - 1)
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
        }
        return result;
    }

}
