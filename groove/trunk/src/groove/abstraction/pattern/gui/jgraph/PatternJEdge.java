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

import groove.abstraction.pattern.shape.AbstractPatternEdge;
import groove.graph.Edge;
import groove.gui.jgraph.GraphJEdge;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JAttr;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * Class that connects to the JGraph library for displaying pattern edges.
 * 
 * @author Eduardo Zambon
 */
public class PatternJEdge extends GraphJEdge {

    private PatternJEdge(PatternJGraph jGraph, PatternJModel jModel) {
        super(jGraph, jModel);
    }

    private PatternJEdge(PatternJGraph jGraph, PatternJModel jModel,
            AbstractPatternEdge<?> pEdge) {
        super(jGraph, jModel, pEdge);
    }

    @Override
    public PatternJGraph getJGraph() {
        return (PatternJGraph) super.getJGraph();
    }

    @Override
    public String toString() {
        return String.format("PatternJEdge with labels %s", getKeys());
    }

    /** 
     * Factory method, in case this object is used as a prototype.
     * Returns a fresh {@link GraphJEdge} of the same type as this one. 
     */
    @Override
    public PatternJEdge newJEdge(GraphJModel<?,?> jModel, Edge edge) {
        assert edge instanceof AbstractPatternEdge<?>;
        return new PatternJEdge(getJGraph(), (PatternJModel) jModel,
            (AbstractPatternEdge<?>) edge);
    }

    /** Returns a prototype {@link GraphJEdge} for a given {@link GraphJGraph}. */
    public static PatternJEdge getPrototype(PatternJGraph jGraph) {
        return new PatternJEdge(jGraph, null);
    }

    /**
     * Callback method for creating the core attributes.
     * These might be modified by other parameters; don't call this
     * method directly.
     */
    @Override
    protected AttributeMap createAttributes() {
        return DEFAULT_PEDGE_ATTR.clone();
    }

    /**
     * The standard jgraph attributes used for representing equivalence classes.
     */
    public static final JAttr.AttributeMap DEFAULT_PEDGE_ATTR;

    static {
        DEFAULT_PEDGE_ATTR = GraphJGraph.DEFAULT_EDGE_ATTR.clone();
        GraphConstants.setDashPattern(DEFAULT_PEDGE_ATTR, JAttr.NESTED_DASH);
    }

}
