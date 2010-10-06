/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: DefaultMorphism.java,v 1.17 2008-01-30 09:32:58 iovka Exp $
 */
package groove.graph;

import groove.match.GraphSearchPlanFactory;
import groove.match.MatchStrategy;
import groove.rel.VarNodeEdgeMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a morphism on the basis of a single (hash) map for both
 * nodes and edges.
 * @author Arend Rensink
 * @version $Revision$
 */
public class DefaultMorphism extends AbstractMorphism implements Cloneable {
    /**
     * Constructs a new, fixed morphism with a given domain, codomain and
     * element map. The element map is aliased!
     */
    public DefaultMorphism(Graph dom, Graph cod, NodeEdgeMap elementMap) {
        super(dom, cod);
        this.dom = dom;
        this.cod = cod;
        this.elementMap = elementMap;
        setFixed();
    }

    /**
     * Constructs a new (empty) Morphism with a given domain and codomain.
     * @ensure dom() == dom, cod() == cod, keySet().isEmpty()
     */
    public DefaultMorphism(Graph dom, Graph cod) {
        super(dom, cod);
        this.dom = dom;
        this.cod = cod;
        this.elementMap = createElementMap();
        dom.addGraphListener(this);
        cod.addGraphListener(this);
    }

    /**
     * Constructs a new Morphism as a clone of another. Source and target are
     * aliased, whereas internal maps are cloned.
     */
    public DefaultMorphism(Morphism morph) {
        super(morph);
    }

    /**
     * Constructs a prototype object of this class, to be used as a factory for
     * new (default) morphisms. The prototype is only intended to be used for
     * its <tt>newMorphism()</tt> method.
     * @see #prototype
     */
    protected DefaultMorphism() {
        // empty constructor
    }

    public NodeEdgeMap elementMap() {
        if (this.elementMap == null) {
            return this.elementMap = createElementMap();
        }
        return this.elementMap;
    }

    public Map<Edge,Edge> edgeMap() {
        return elementMap().edgeMap();
    }

    public Map<Node,Node> nodeMap() {
        return elementMap().nodeMap();
    }

    // ------------------- commands --------------------------

    @Override
    public Node putNode(Node key, Node value) {
        Node result;
        testNotFixed();
        result = super.putNode(key, value);
        assert result == null || result.equals(value) : "Cannot replace images in a morphism: "
            + key + " had image " + result + ", now changing into " + value;
        return result;
    }

    @Override
    public Edge putEdge(Edge key, Edge value) {
        Edge result;
        testNotFixed();
        result = super.putEdge(key, value);
        assert result == null || result.equals(value) : "Cannot replace images in a morphism: "
            + key + " had image " + result + ", now changing into " + value;
        return result;
    }

    /**
     * This implementation returns a new <tt>DefaultMorphism</tt>, sharing
     * the domain and codomain but with a copy of the element map. The resulting
     * morphism is not fixed, even if this one is.
     */
    @Override
    public Morphism clone() {
        return new DefaultMorphism(this);
    }

    /**
     * @ensure <tt>result instanceof DefaultMorphism</tt>
     */
    public Morphism createMorphism(Graph dom, Graph cod) {
        return new DefaultMorphism(dom, cod);
    }

    /**
     * Factory method for match strategies.
     */
    @Override
    protected MatchStrategy<VarNodeEdgeMap> createMatchStrategy() {
        return GraphSearchPlanFactory.getInstance().createMatcher(dom(),
            elementMap().nodeMap().keySet(), elementMap().edgeMap().keySet(),
            null);
    }

    /**
     * Factory method for the element map. This implementation returns a
     * {@link HashMap}.
     */
    protected NodeEdgeMap createElementMap() {
        return new NodeEdgeHashMap();
    }

    /**
     * Tests if the morphism is currently not fixed. Throws an
     * {@link IllegalStateException} if it is fixed.
     * 
     */
    protected void testNotFixed() {
        if (isFixed()) {
            throw new IllegalStateException(
                "Operation not allowed whn morphism is fixed");
        }
    }

    /**
     * The underlying node and edge map of this Morphism.
     * @invariant <tt>source: dom.nodeSet() -> cod.nodeSet() \cup 
     *                       dom.edgeSet() -> cod.edgeSet()</tt>
     */
    private NodeEdgeMap elementMap;

    /**
     * A prototype object of this class, to be used as a factory for new
     * (default) morphisms. That is, the object is only intended to be used for
     * invoking <tt>newMorphism(Graph,Graph)</tt>.
     */
    static public final Morphism prototype = new DefaultMorphism();
}