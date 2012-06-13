/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.abstraction.pattern.shape;

import groove.graph.Edge;
import groove.graph.GraphRole;
import groove.graph.Node;

import java.util.Set;

/**
 * Pattern graph.
 * 
 * @author Eduardo Zambon
 */
public class PatternGraph extends AbstractPatternGraph<PatternNode,PatternEdge> {

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** Associated type graph. */
    private final TypeGraph type;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /** Default constructor. */
    public PatternGraph(String name, TypeGraph type) {
        super(name);
        this.type = type;
    }

    /** Copying constructor. */
    private PatternGraph(PatternGraph pGraph) {
        this(pGraph.getName(), pGraph.getTypeGraph());
        this.depth = pGraph.depth;
        this.layers.addAll(pGraph.layers);
        this.graphNodeSet.addAll(pGraph.graphNodeSet);
        this.graphEdgeSet.addAll(pGraph.graphEdgeSet);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public GraphRole getRole() {
        return GraphRole.HOST;
    }

    @Override
    protected boolean isTypeCorrect(Node node) {
        return node instanceof PatternNode;
    }

    @Override
    protected boolean isTypeCorrect(Edge edge) {
        return edge instanceof PatternEdge;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<PatternNode> nodeSet() {
        return (Set<PatternNode>) super.nodeSet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<PatternEdge> edgeSet() {
        return (Set<PatternEdge>) super.edgeSet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<PatternEdge> inEdgeSet(Node node) {
        return (Set<PatternEdge>) super.inEdgeSet(node);
    }

    @Override
    public PatternFactory getFactory() {
        return this.type.getPatternFactory();
    }

    @Override
    public PatternGraph clone() {
        return new PatternGraph(this);
    }

    // ------------------------------------------------------------------------
    // Other methods
    // ------------------------------------------------------------------------

    /** Basic getter method. */
    public TypeGraph getTypeGraph() {
        return this.type;
    }

}