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
package groove.abstraction.pattern.gui.jgraph;

import groove.abstraction.pattern.shape.AbstractPatternNode;
import groove.abstraction.pattern.shape.PatternNode;
import groove.abstraction.pattern.shape.PatternShape;
import groove.graph.Graph;
import groove.graph.Node;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.GraphJVertex;
import groove.gui.jgraph.JAttr;

import java.util.Collections;
import java.util.List;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * Class that connects to the JGraph library for displaying pattern nodes.
 * 
 * @author Eduardo Zambon
 */
public class PatternJVertex extends GraphJVertex {

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    // Private constructor. Use the prototype.
    private PatternJVertex(PatternJGraph jGraph, PatternJModel jModel,
            AbstractPatternNode pNode) {
        super(jGraph, jModel, pNode);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public PatternJGraph getJGraph() {
        return (PatternJGraph) super.getJGraph();
    }

    @Override
    public PatternJVertex newJVertex(GraphJModel<?,?> jModel, Node node) {
        assert node instanceof AbstractPatternNode;
        return new PatternJVertex(getJGraph(), (PatternJModel) jModel,
            (AbstractPatternNode) node);
    }

    @Override
    public String toString() {
        return String.format("PatternJVertex %d with labels %s", getNumber(),
            getKeys());
    }

    @Override
    public List<StringBuilder> getLines() {
        return Collections.emptyList();
    }

    @Override
    public String getAdornment() {
        String result = getNode().toString();
        Graph<?,?> graph = getJModel().getGraph();
        if (graph instanceof PatternShape) {
            PatternShape pShape = (PatternShape) graph;
            result += "(" + pShape.getMult((PatternNode) getNode()) + ")";
        }
        return result;
    }

    /**
     * Callback method for creating the core attributes.
     * These might be modified by other parameters; don't call this
     * method directly.
     */
    @Override
    protected AttributeMap createAttributes() {
        return DEFAULT_PNODE_ATTR.clone();
    }

    // ------------------------------------------------------------------------
    // Static methods and fields
    // ------------------------------------------------------------------------

    /** Returns a prototype {@link PatternJVertex} for a given {@link PatternJGraph}. */
    public static PatternJVertex getPrototype(PatternJGraph jGraph) {
        return new PatternJVertex(jGraph, null, null);
    }

    /**
     * The standard jgraph attributes used for representing equivalence classes.
     */
    private static final JAttr.AttributeMap DEFAULT_PNODE_ATTR;

    static {
        DEFAULT_PNODE_ATTR = GraphJGraph.DEFAULT_NODE_ATTR.clone();
        DEFAULT_PNODE_ATTR.remove(GraphConstants.BACKGROUND);
        GraphConstants.setAutoSize(DEFAULT_PNODE_ATTR, true);
        GraphConstants.setGroupOpaque(DEFAULT_PNODE_ATTR, true);
        GraphConstants.setInset(DEFAULT_PNODE_ATTR, 8);
        GraphConstants.setDashPattern(DEFAULT_PNODE_ATTR, JAttr.NESTED_DASH);
    }

}
