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

import groove.grammar.model.FormatException;
import groove.graph.plain.PlainEdge;
import groove.graph.plain.PlainFactory;
import groove.graph.plain.PlainGraph;
import groove.graph.plain.PlainNode;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class to read and write graphs in GXL format, using JXB data binding.
 * @author Arend Rensink
 * @version $Revision: 1568 $
 */
public class DefaultJaxbGxlIO extends AbstractJaxbGxlIO<PlainNode,PlainEdge> {
    /**
     * Private constructor for the singleton instance.
     */
    private DefaultJaxbGxlIO() {
        // Empty by design.
    }

    /**
     * Creates a GROOVE node from a GXL node ID, attempting to retain any node
     * number that appears as a suffix in the GXL node ID.
     * @return A GROOVE node with the number in <code>nodeId</code>, or
     *         <code>null</code> if <code>nodeId</code> does not end on a
     *         number.
     */
    @Override
    protected PlainNode createNode(String nodeId) {
        // attempt to construct node number from gxl node
        // by looking at trailing number shape of node id
        boolean digitFound = false;
        int nodeNr = 0;
        int unit = 1;
        int charIx;
        for (charIx = nodeId.length() - 1; charIx >= 0
            && Character.isDigit(nodeId.charAt(charIx)); charIx--) {
            nodeNr += unit * (nodeId.charAt(charIx) - '0');
            unit *= 10;
            digitFound = true;
        }
        if (charIx >= 0 && nodeId.charAt(charIx) == '-') {
            nodeNr = -nodeNr;
        }
        return digitFound ? elementFactory.createNode(nodeNr) : null;
    }

    /**
     * Callback factory method to create an attribute edge with given source
     * node, and a label based on a given attribute map. The edge will be unary
     * of <code>targetNode == null</code>, binary otherwise.
     */
    @Override
    protected PlainEdge createEdge(PlainNode sourceNode, String label,
            PlainNode targetNode) {
        return elementFactory.createEdge(sourceNode, label, targetNode);
    }

    @Override
    protected PlainGraph createGraph(String name) {
        return new PlainGraph(name);
    }

    /**
     * Loads a graph from an input stream. Convenience method for
     * <code>loadGraphWithMap(in).first()</code>.
     * Specializes the return type.
     */
    @Override
    public PlainGraph loadGraph(InputStream in) throws IOException,
        FormatException {
        return (PlainGraph) loadGraphWithMap(in).one();
    }

    /** Returns the singleton instance of this class. */
    static public DefaultJaxbGxlIO getInstance() {
        return instance;
    }

    static private final PlainFactory elementFactory =
        PlainFactory.instance();
    /** Singleton instance of the class. */
    static private final DefaultJaxbGxlIO instance = new DefaultJaxbGxlIO();

}