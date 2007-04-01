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
 * $Id: DefaultMorphism.java,v 1.3 2007-04-01 12:49:56 rensink Exp $
 */
package groove.graph;

import groove.graph.match.DefaultMatcher;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a morphism on the basis of a single (hash) map 
 * for both nodes and edges.
 * @author Arend Rensink
 * @version $Revision: 1.3 $
 */
public class DefaultMorphism extends AbstractMorphism {
    /**
     * A prototype object of this class, to be used as a factory
     * for new (default) morphisms. That is, the object is only intended to
     * be used for invoking <tt>newMorphism(Graph,Graph)</tt>.
     */
    static public final Morphism prototype = new DefaultMorphism();

    /**
     * Constructs a new (empty) Morphism with a given domain and codomain.
     * @ensure dom() == dom, cod() == cod, keySet().isEmpty()
     */
    public DefaultMorphism(Graph dom, Graph cod) {
    	super(dom,cod);
        this.dom = dom;
        this.cod = cod;
//        setRuleFactory(ruleFactory);
        this.elementMap = createElementMap();
        dom.addGraphListener(this);
        cod.addGraphListener(this);
    }

    /**
     * Constructs a new Morphism as a clone of another.
     * Source and target are aliased, whereas internal maps are cloned.
     */
    public DefaultMorphism(Morphism morph) {
    	super(morph);
    }

    /**
     * Constructs a new morphism as a clone of another with a given rule factory.
     * @param morph the morphism to be cloned
     * @param ruleFactory the rule factory to be used for creating a simulation
     */
//    public DefaultMorphism(Morphism morph, RuleFactory ruleFactory) {
//    	this(morph);
//    	setRuleFactory(ruleFactory);
//    	this.ruleFactory = ruleFactory;
//    }

    /**
     * Constructs a protytpe object of this class, to be used as a factory
     * for new (default) morphisms. The prototype is only intended to
     * be used for its <tt>newMorphism()</tt> method.
     * @see #prototype
     */
    protected DefaultMorphism() {
    	// empty constructor
    }

    public NodeEdgeMap elementMap() {
    	if (elementMap == null) {
    		return elementMap = createElementMap();
    	}
        return elementMap;
    }

    public Map<Edge, Edge> edgeMap() {
		return elementMap().edgeMap();
	}

	public Map<Node, Node> nodeMap() {
		return elementMap().nodeMap();
	}

    // ------------------- commands --------------------------

	@Override
    public Node putNode(Node key, Node value) {
    	Node result;
        testNotFixed();
        result = super.putNode(key, value);
        assert result == null
        || result.equals(value) : "Cannot replace images in a morphism: "
            + key
            + " had image "
            + result
            + ", now changing into "
            + value;
        return result;
    }

	@Override
    public Edge putEdge(Edge key, Edge value) {
    	Edge result;
        testNotFixed();
        result = super.putEdge(key, value);
        assert result == null
        || result.equals(value) : "Cannot replace images in a morphism: "
            + key
            + " had image "
            + result
            + ", now changing into "
            + value;
        return result;
    }

    @Deprecated
	@Override
    public Node removeNode(Node key) {
        testNotFixed();
        return super.removeNode(key);
    }

    @Deprecated
	@Override
    public Edge removeEdge(Edge key) {
        testNotFixed();
        return super.removeEdge(key);
    }
    
    /**
     * Callback method to create a fixed morphism from a simulation.
     * The underlying element map of the morphism will be derived from the
     * key-to-singular-image mapping of the simulation.  
     * @param sim the simulation to underly the morphism
     * @see #getTotalExtension()
     * @see #getTotalExtensions()
     * @see #getTotalExtensionsIter()
     */
    protected Morphism createMorphism(final Simulation sim) {
        Morphism result = new DefaultMorphism(sim.dom(), sim.cod()) {
        	@Override
            protected NodeEdgeMap createElementMap() {
                return sim.getSingularMap();
            }
        };
        result.setFixed();
        return result;
    }

	/**
     * This implementation returns a new <tt>DefaultMorphism</tt>,
     * sharing the domain and codomain but with a copy of the element map.
     * The resulting morphism is not fixed, even if this one is.
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
     * This implementation returns a {@link DefaultMorphism}.
     */
	@Override
    protected Morphism createMorphism(final NodeEdgeMap sim) {
        Morphism result = new DefaultMorphism(dom(), cod()) {
        	@Override
            protected NodeEdgeMap createElementMap() {
                return sim;
            }
        };
        result.setFixed();
        return result;
    }

    /**
     * Factory method for simulations.
     * This implementation returns a <tt>{@link DefaultSimulation}</tt>.
     */
	@Override
    protected Simulation createSimulation() {
        return new DefaultMatcher(this);
    }
    
    /**
     * Factory method for the element map.
     * This implementation returns a {@link HashMap}.
     */
    protected NodeEdgeMap createElementMap() {
        return new NodeEdgeHashMap();
    }
    
    /**
     * Tests if the morphism is currently not fixed.
     * Throws an {@link IllegalStateException} if it is fixed.
     *
     */
    protected void testNotFixed() {
        if (isFixed()) {
            throw new IllegalStateException("Operation not allowed whn morphism is fixed");
        }
    }

    /**
     * The underlying node and edge map of this Morphism.
     * @invariant <tt>source: dom.nodeSet() -> cod.nodeSet() \cup 
     *                       dom.edgeSet() -> cod.edgeSet()</tt> 
     */
    protected NodeEdgeMap elementMap;
}