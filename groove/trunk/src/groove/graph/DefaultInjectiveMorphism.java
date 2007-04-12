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
 * $Id: DefaultInjectiveMorphism.java,v 1.5 2007-04-12 13:45:34 rensink Exp $
 */
package groove.graph;

import groove.graph.iso.IsoMatcher;
import groove.graph.match.Matcher;
import groove.util.CollectionView;
import groove.util.FilterIterator;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Implementation of an injective morphism between <tt>Graph</tt>s,
 * as an extension of <tt>DefaultMorphism</tt>.
 * @author Arend Rensink
 * @version $Revision: 1.5 $
 * @deprecated the {@link InjectiveMorphism} hierarchy is being abandoned
 */
@Deprecated
public class DefaultInjectiveMorphism extends DefaultMorphism implements InjectiveMorphism {
//    /**
//     * A protytpe object of this class, to be used as a factory
//     * for new (default) injective morphisms. That is, the object is only intended to
//     * be used for invoking <tt>newInjectiveMorphism(SimpleGraph,SimpleGraph)</tt>.
//     */
//    static public final InjectiveMorphism prototype = new DefaultInjectiveMorphism();
//
    /**
     * Constant null reference.
     */
    static private final Reference<NodeEdgeMap> NULL_REFERENCE = new SoftReference<NodeEdgeMap>(null);
    
    /**
     * Constructs a new identity morphism between a given domain and codomain.
     * @require dom != null, cod != null
     * @ensure dom() == from, cod() == to, keySet().isEmpty()
     */
    public DefaultInjectiveMorphism(Graph dom, Graph cod) {
        super(dom, cod);
    }
//
//    /**
//     * Creates an injective morphism from a given morphism if that is
//     * injective. Throws an {@link IllegalArgumentException} if the 
//     * passed-in morphism is not injective.
//     * @param morph the morphism to be copied
//     * @throws IllegalArgumentException if <code>morph</code> is not injective
//     */
//    public DefaultInjectiveMorphism(Morphism morph) throws IllegalArgumentException {
//        this(morph.dom(), morph.cod(), morph.elementMap());
//        if (!morph.isInjective()) {
//            throw new IllegalArgumentException("Attempting to construct injective morphism from non-injective element map");
//        }
//    }
//
//    /**
//     * Constructs a new injective morphism with a given domain and codomain,
//     * and including the given element map.
//     * @require dom != null, cod != null
//     * @ensure dom() == from, cod() == to, keySet().isEmpty()
//     */
//    protected DefaultInjectiveMorphism(Graph dom, Graph cod, NodeEdgeMap nodeEdgeMap) {
//        this(dom, cod);
//        elementMap().putAll(nodeEdgeMap);
//    }
//
//    /**
//     * Clones a given injective morphism.
//     */
//    protected DefaultInjectiveMorphism(InjectiveMorphism other) {
//        this(other.dom(), other.cod());
//        elementMap().putAll(other.elementMap());
//    }

    /**
     * Creates a clone of a given injective morphism.
     * @ensure <tt>morph.equals(result)</tt>
     */
    protected DefaultInjectiveMorphism(DefaultInjectiveMorphism morph) {
        super(morph);
    }
//
//    /**
//     * Constructs a protytpe object of this class, to be used as a factory
//     * for new (default) injective morphisms. The prototype is only intended to
//     * be used for its <tt>newInjectiveMorphism()</tt> method.
//     * @see #prototype
//     */
//    protected DefaultInjectiveMorphism() {
//    	// empty constructor needed for subclasses
//    }

    @Deprecated
    public Element getInverseElement(Element elem) {
    	if (elem instanceof Node) {
    		return inverseElementMap().getNode((Node) elem);
    	} else {
    		return inverseElementMap().getEdge((Edge) elem);
    	}
    }

    @Deprecated
    @Override
    public Collection<Element> getInverseElementSet(Collection<? extends Element> valueSet) {
        Collection<Element> result = new HashSet<Element>();
        for (Element value: valueSet) {
            Element key = getInverseElement(value);
            if (key != null) {
                result.add(key);
            }
        }
        return result;
    }

    @Deprecated
    @Override
    public Collection<Element> getInverseElementSet(Element value) {
        Collection<Element> result = new LinkedList<Element>();
        Element key = getInverseElement(value);
        if (key != null) {
            result.add(key);
        }
        return result;
    }

    public InjectiveMorphism inverse() {
    	InjectiveMorphism result = new DefaultInjectiveMorphism(cod, dom);
        result.putAll(inverseElementMap());
        return result;
    }

    /**
     * Method overridden for efficiency.
     */
    @Override
    public boolean isInjective() {
        return true;
    }

    /**
     * Method overridden for the sake of efficiency.
     */
    @Override
    public boolean containsValue(Element value) {
        return inverseElementMap().containsKey(value);
    }

    // ---------------------- OBJECT OVERRIDES ---------------------

    @Override
    public Morphism clone() {
        return new DefaultInjectiveMorphism(this);
    }

    /**
     * The new morphism is actually created through <tt>newInjectiveMorphism</tt>.
     * @require <tt>cod, dom instanceof PackageGraph</tt>
     * @ensure <tt>result instanceof DefaultInjectiveMorphism</tt>
     * @see #createInjectiveMorphism(Graph,Graph)
     */
    @Override
    public Morphism createMorphism(Graph dom, Graph cod) {
        return createInjectiveMorphism(dom, cod);
    }

    @Deprecated
    public InjectiveMorphism createInjectiveMorphism(Graph dom, Graph cod) {
        return new DefaultInjectiveMorphism(dom, cod);
    }
    
    @Override
	public Morphism getTotalExtension() {
    	Iterator<? extends Morphism> iter = getTotalExtensionsIter();
    	if (iter.hasNext()) {
    		return iter.next();
    	} else {
    		return null;
    	}
	}

	@Override
	public Collection<? extends Morphism> getTotalExtensions() {
		return new CollectionView<Morphism>(super.getTotalExtensions()) {
			@Override
			public boolean approves(Object morph) {
				return morph instanceof Morphism && ((Morphism) morph).isInjective();
			}
		};
	}

	@Override
	public Iterator<? extends Morphism> getTotalExtensionsIter() {
		return new FilterIterator<Morphism>(super.getTotalExtensionsIter()) {
			@Override
			public boolean approves(Object morph) {
				return morph instanceof Morphism && ((Morphism) morph).isInjective();
			}
		};
	}

	public boolean hasIsomorphismExtension() {
        reporter.start(EXTEND_TO_ISOMORPHISM);
        try {
        	Matcher sim = createIsoMatcher();
        	boolean result = sim.hasRefinement();
        	return result;
        } finally {
            reporter.stop();
        }
    }

    public boolean extendToIsomorphism() {
        reporter.start(EXTEND_TO_ISOMORPHISM);
        try {
            // test for equality of the numbers of nodes and edges
            if (dom.nodeCount() != cod.nodeCount() || dom.edgeCount() != cod.edgeCount()) {
                return false;
            }
            // now attempt to create an identity mapping
            boolean identityFound = true;
            Iterator<? extends Node> nodeIter = dom.nodeSet().iterator();
            while (identityFound && nodeIter.hasNext()) {
                identityFound = cod.containsElement(nodeIter.next());
            }
            Iterator<? extends Edge> edgeIter = dom.edgeSet().iterator();
            while (identityFound && edgeIter.hasNext()) {
                identityFound = cod.containsElement(edgeIter.next());
            }
            if (identityFound) {
            	for (Node domNode: dom.nodeSet()) {
            		putNode(domNode, domNode);
            	}
            	for (Edge domEdge: dom.edgeSet()) {
            		putEdge(domEdge, domEdge);
            	}
            	return true;
            }
            // that didn't work; construct a simulation instead
            NodeEdgeMap refinement = createIsoMatcher().getRefinement();
            if (refinement == null) {
                return false;
            } else {
            	putAll(refinement);
                return true;
            }
        } finally {
            reporter.stop();
        }
    }

    @Override
    public boolean removeImage(Element image) {
    	if (image instanceof Node) {
            Node key = inverseElementMap().getNode((Node) image);
            if (key != null)
                removeNode(key);
            return key != null;
    	} else {
            Edge key = inverseElementMap().getEdge((Edge) image);
            if (key != null)
                removeEdge(key);
            return key != null;
    	}
    }

    /** Merges are forbidden in domain and codomain of an injective morphism */
    @Override
    public void replaceUpdate(GraphShape graph, Node fom, Node to) {
        throw new UnsupportedOperationException("Replacement not implemented in injective morphism");
    }

    /** Merges are forbidden in domain and codomain of an injective morphism */
    @Override
    public void replaceUpdate(GraphShape graph, Edge from, Edge to) {
        throw new UnsupportedOperationException("Replacement not implemented in injective morphism");
    }

    /**
     * Method overridden to also add the pair to the inverse map.
     */
    @Deprecated
    @Override
    public Element put(Element key, Element image) {
        Element result = super.put(key, image);
        // if something changed, change it also in the inverse map
        if (result == null) {
            inverseElementMap = NULL_REFERENCE;
        }
        return result;
    }

    /**
     * Method overridden to also add the pair to the inverse map.
     */
    @Override
    public Node putNode(Node key, Node image) {
    	Node result = super.putNode(key, image);
        // if something changed, change it also in the inverse map
        if (result == null) {
            inverseElementMap = NULL_REFERENCE;
        }
        return result;
    }

    /**
     * Method overridden to also add the pair to the inverse map.
     */
    @Override
    public Edge putEdge(Edge key, Edge image) {
        Edge result = super.putEdge(key, image);
        // if something changed, change it also in the inverse map
        if (result == null) {
            inverseElementMap = NULL_REFERENCE;
        }
        return result;
    }

    /**
     * Overwrites the method to also remove key's image from the inverse map.
     */
    @Deprecated
    @Override
    public Node removeNode(Node key) {
        Node result = super.removeNode(key);
        if (result != null) {
            // if the inverse map is currently not computed, don't do anything
            NodeEdgeMap inverseMap = inverseElementMap.get();
            if (inverseMap != null) {
                // otherwise, remove the image
                Object doubleInverse = inverseMap.removeNode(result);
                assert doubleInverse.equals(key) : "Inconsistent inverse map: ("
                    + key
                    + ","
                    + result
                    + ") in map but ("
                    + result
                    + ","
                    + doubleInverse
                    + ") in inverse map";
            }
        }
        return result;
    }

    /**
     * Overwrites the method to also remove key's image from the inverse map.
     */
    @Deprecated
    @Override
    public Edge removeEdge(Edge key) {
        Edge result = super.removeEdge(key);
        if (result != null) {
            // if the inverse map is currently not computed, don't do anything
            NodeEdgeMap inverseMap = inverseElementMap.get();
            if (inverseMap != null) {
                // otherwise, remove the image
                Object doubleInverse = inverseMap.removeEdge(result);
                assert doubleInverse.equals(key) : "Inconsistent inverse map: ("
                    + key
                    + ","
                    + result
                    + ") in map but ("
                    + result
                    + ","
                    + doubleInverse
                    + ") in inverse map";
            }
        }
        return result;
    }
//
//    /**
//     * This implementation returns an <tt>{@link InjectiveSimulation}</tt>.
//     */
//    @Override
//    protected Simulation createSimulation() {
//        return new InjectiveSimulation(this);
//    }

    /**
     * This implementation returns an {@link IsoMatcher}.
     */
    protected Matcher createIsoMatcher() {
        return new IsoMatcher(this);
    }

    /**
     * This implementation returns an {@link InjectiveMorphism}.
     */
    @Override
    protected Morphism createMorphism(final NodeEdgeMap sim) {
        Morphism result = new DefaultInjectiveMorphism(dom(), cod()) {
            @Override
            protected NodeEdgeMap createElementMap() {
                return sim;
            }
        };
        result.setFixed();
        return result;
    }

    /** 
     * Returns the inverse of the element map.
     * The inverse map is created lazily and cached.
     */
    protected NodeEdgeMap inverseElementMap() {
    	NodeEdgeMap result = inverseElementMap.get();
        if (result == null) {
            result = new NodeEdgeHashMap();
            for (Map.Entry<Node,Node> entry: elementMap().nodeMap().entrySet()) {
                result.putNode(entry.getValue(), entry.getKey());
            }
            for (Map.Entry<Edge,Edge> entry: elementMap().edgeMap().entrySet()) {
                result.putEdge(entry.getValue(), entry.getKey());
            }
            inverseElementMap = new SoftReference<NodeEdgeMap>(result);
        }
        return result;
    }

    /**
     * Inverse map from codomain elements to domain elements.
     * @invariant <tt>inverseNodeMap = map^{-1}</tt>
     */
    protected Reference<NodeEdgeMap> inverseElementMap = NULL_REFERENCE;
    
    /** Handle to profile the {@link #extendToIsomorphism()} method. */
    static public int EXTEND_TO_ISOMORPHISM = reporter.newMethod("extendToIsomorphism");
}