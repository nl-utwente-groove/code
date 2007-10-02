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
 * $Id: AbstractMorphism.java,v 1.11 2007-10-02 23:06:29 rensink Exp $
 */
package groove.graph;

import groove.match.MatchStrategy;
import groove.rel.RegExprLabel;
import groove.rel.VarNodeEdgeMap;
import groove.util.Reporter;
import groove.util.TransformIterator;
import groove.view.FormatException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of a morphism on the basis of a single (hash) map 
 * for both nodes and edges.
 * @author Arend Rensink
 * @version $Revision: 1.11 $
 */
public abstract class AbstractMorphism extends AbstractNodeEdgeMap<Node,Node,Edge,Edge> implements Morphism {
    /**
     * Constructs a new (empty) Morphism with a given domain and codomain.
     * @ensure dom() == dom, cod() == cod, keySet().isEmpty()
     */
    public AbstractMorphism(Graph dom, Graph cod) {
        this.dom = dom;
        this.cod = cod;
        dom.addGraphListener(this);
        cod.addGraphListener(this);
    }

    /**
     * Constructs a new Morphism as a clone of another.
     * Source and target are aliased, whereas internal maps are cloned.
     */
    protected AbstractMorphism(Morphism morph) {
        this(morph.dom(), morph.cod());
        putAll(morph);
    }

    /**
     * Dummy morphism, just there fore prototype purposes.
     */
    protected AbstractMorphism() {
        this.dom = null;
        this.cod = null;
    }

    public boolean containsKey(Element key) {
        return nodeMap().containsKey(key) || edgeMap().containsKey(key);
    }

    public boolean containsValue(Element value) {
        return nodeMap().containsValue(value) || edgeMap().containsValue(value);
    }

    /**
     * This implementation defers to the element map.
     * @see #elementMap()
	 */
	public Label getLabel(Label label) {
		return elementMap().getLabel(label);
	}

	/**
	 * This implementation defers to the element map.
	 * @see #elementMap()
	 */
	public Edge mapEdge(Edge key) {
		return elementMap().mapEdge(key);
	}

	@Deprecated
    public Element getElement(Element key) {
    	if (key instanceof Node) {
    		return getNode((Node) key);
    	} else {
    		return getEdge((Edge) key);
    	}
    }

    @Deprecated
	public Collection<Element> getElementSet(Collection<? extends Element> keySet) {
        Collection<Element> result = new HashSet<Element>();
        for (Element key: keySet) {
            Element image = getElement(key);
            if (image != null) {
                result.add(image);
            }
        }
        return result;
    }

    @Deprecated
    public Collection<Element> getInverseElementSet(Collection<? extends Element> valueSet) {
        Collection<Element> result = new HashSet<Element>();
        // iterate over the element map
        for (Map.Entry<Node,Node> entry: nodeMap().entrySet()) {
            // add the key of this entry to the result if the value is in the value set
            if (valueSet.contains(entry.getValue())) {
                result.add(entry.getKey());
            }
        }
        // iterate over the element map
        for (Map.Entry<Edge,Edge> entry: edgeMap().entrySet()) {
            // add the key of this entry to the result if the value is in the value set
            if (valueSet.contains(entry.getValue())) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    @Deprecated
    public Collection<Element> getInverseElementSet(Element elem) {
        Collection<Element> result = new LinkedList<Element>();
        // iterate over the element map
        for (Map.Entry<Node,Node> entry: nodeMap().entrySet()) {
            // add the key of this entry to the result if the value is the one we're looking for
            if (elem.equals(entry.getValue())) {
                result.add(entry.getKey());
            }
        }
        for (Map.Entry<Edge,Edge> entry: edgeMap().entrySet()) {
            // add the key of this entry to the result if the value is the one we're looking for
            if (elem.equals(entry.getValue())) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    /**
     * @require <tt>morph instanceof InternalMorphism</tt>
     */
    public Morphism after(Morphism morph) {
        reporter.start(AFTER);
        Morphism result = createMorphism(morph.dom(), cod);
        constructConcat(this, morph, result);
        reporter.stop();
        return result;
    }

    /** @ensure <tt>result instanceof InternalMorphism</tt> */
    public Morphism then(Morphism morph) {
        Morphism result = createMorphism(dom, morph.cod());
        constructConcat(morph, this, result);
        return result;
    }

    /** 
     * Returns the concatenation of this node map after the inverse of another,
     * if that yields a function.
     * The concatenation exists iff <tt>other.get(x)</tt> equals <tt>other.get(y)</tt> 
     * (where both are non-null) implies <tt>this.get(x)</tt> equals
     * <tt>this.get(y)</tt>.
     * If the concatenation does not exist, the method returns <tt>null</tt>.
     * @param morph the other morphism, to be inverted in the concatenation
     * @ensure <tt>result == null || result: InternalMorphism == this \circ other^{-1}</tt> 
     */
    public Morphism afterInverse(Morphism morph) {
        reporter.start(AFTER_INVERSE);
        try {
            Morphism result = createMorphism(morph.cod(), cod);
            constructInvertConcat(morph, this, result);
            return result;
        } catch (FormatException exc) {
            return null;
        } finally {
            reporter.stop();
        }
    }

    /** 
     * The type of resulting morphism is determined by {@link #createMorphism(Graph, Graph)}.
     */
    public Morphism inverseThen(Morphism morph) {
        reporter.start(AFTER_INVERSE);
        try {
            Morphism result = createMorphism(cod, morph.cod());
            constructInvertConcat(this, morph, result);
            return result;
        } catch (FormatException exc) {
            return null;
        } finally {
            reporter.stop();
        }
    }

    @Deprecated
    public boolean hasTotalExtensions() {
        reporter.start(HAS_TOTAL_EXTENSIONS);
        VarNodeEdgeMap match = createMatchStrategy().getMatch(cod(), elementMap());
        boolean result = match != null;
        reporter.stop();
        return result;
    }

    @Deprecated
    public Morphism getTotalExtension() {
        VarNodeEdgeMap match = createMatchStrategy().getMatch(cod(), elementMap());
        if (match == null) {
            return null;
        } else {
            return createMorphism(match);
        }
    }

    @Deprecated
    public Collection<? extends Morphism> getTotalExtensions() {
        reporter.start(GET_TOTAL_EXTENSIONS);
        try {
            MatchStrategy<VarNodeEdgeMap> matcher = createMatchStrategy();
            // we choose a LinkedList because there will be removal
            // a HashSet would be an option but then we need to take care of
            // the equals method of the morphisms returned by createMorphism,
            // also in subclasses, which is hard to maintain
            Collection<Morphism> result = new LinkedList<Morphism>();
            Iterator<? extends NodeEdgeMap> refinementIter = matcher.getMatchIter(cod(), elementMap());
            while (refinementIter.hasNext()) {
				NodeEdgeMap elementMap = refinementIter.next();
				result.add(createMorphism(elementMap));
			}
            return result;
        } finally {
            reporter.stop();
        }
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
    @Deprecated
    abstract protected Morphism createMorphism(NodeEdgeMap sim);

    @Deprecated
    public Iterator<? extends Morphism> getTotalExtensionsIter() {
        reporter.start(GET_TOTAL_EXTENSIONS);
        try {
            MatchStrategy<VarNodeEdgeMap> matcher = createMatchStrategy();
            return new TransformIterator<NodeEdgeMap,Morphism>(matcher.getMatchIter(cod(), elementMap())) {
            	@Override
            	public Morphism toOuter(NodeEdgeMap obj) {
                    return createMorphism(obj);
                }
            };
        } finally {
            reporter.stop();
        }
    }

    public boolean isSurjective() {
    	Set<Node> nodeValues = new HashSet<Node>(nodeMap().values());
        if (nodeValues.size() != cod().nodeCount()) {
        	return false;
        }
    	Set<Edge> edgeValues = new HashSet<Edge>(edgeMap().values());
        return edgeValues.size() == cod().edgeCount();
    }

    public boolean isInjective() {
    	Set<Node> nodeValues = new HashSet<Node>(nodeMap().values());
        return nodeMap().size() == nodeValues.size();
    }

    public boolean isFixed() {
        return fixed;
    }

    // ------------------------- OBJECT OVERRIDES -----------------------
    
    public boolean isTotal() {
        return nodeMap().size() + edgeMap().size() == dom().size();
    }
    public Graph dom() {
        return dom;
    }

    public Graph cod() {
        return cod;
    }

    // ------------------------- OBJECT OVERRIDES -----------------------

    /**
     * This implementation calls <code>{@link #createMorphism(Graph,Graph)}</code>.
     */
    @Deprecated
    public Morphism newInstance(Graph dom, Graph cod) {
        return createMorphism(dom, cod);
    }

    /**
     * This implementation invokes <tt>#equals(Morphism)</tt> for the actual comparison.
     */
	@Override
    public boolean equals(Object other) {
        return other instanceof Morphism && equals((Morphism) other);
    }

    /**
     * Two morphisms are equal if they have equal domains, codomains and element maps.
     */
    @Deprecated
    public boolean equals(Morphism other) {
        return dom().equals(other.dom()) && cod().equals(other.cod()) && super.equals(other);
    }

    /**
     * The hash code of a morphism is composed out of those for domain, codomain and element map.
     */
	@Override
    public int hashCode() {
        int res = (dom().hashCode() << 6) + (cod().hashCode() << 3) + elementMap().hashCode();
        return res;
    }

	@Override
    public String toString() {
        return "Source graph:  "
            + AbstractGraph.toString(dom)
            + "\nTarget graph:  "
            + AbstractGraph.toString(cod)
            + "\nNode and edge mapping: "
            + elementMap().toString();
    }

    // ------------------ GRAPH LISTENER METHODS ----------------

    /** Has no effect. (Adding nodes or edges does not affect the morphism.) */
    public void addUpdate(GraphShape graph, Node node) {
    	// has no effect
    }

    /** Has no effect. (Adding edges does not affect the morphism.) */
    public void addUpdate(GraphShape graph, Edge edge) {
    	// has no effect
    }

    /**
     * Removes the given node or edge from the keys or images of this morphism.
     */
    public void removeUpdate(GraphShape graph, Node node) {
        if (graph == dom) {
            removeNode(node);
        } else {
            assert graph == cod : "Morphism listens only to domain and codomain";
            removeImage(node);
            assert !containsValue(node) : "Image "
                + node
                + " not removed properly from morphism "
                + this;
        }
    }

    /**
     * Removes the given node or edge from the keys or images of this morphism.
     */
    public void removeUpdate(GraphShape graph, Edge edge) {
        if (graph == dom) {
            removeEdge(edge);
        } else {
            assert graph == cod : "Morphism listens only to domain and codomain";
            removeImage(edge);
            assert !containsValue(edge) : "Image "
                + edge
                + " not removed properly from morphism "
                + this;
        }
    }

    public void replaceUpdate(GraphShape graph, Node from, Node to) {
        // we perform no check because, due to the contract of GraphListener,
        // the corresponding node and edge replacements are notified separately
        if (graph == dom) {
            if (nodeMap().containsKey(from)) {
                throw new UnsupportedOperationException(
                    "Replacement of " + from + " by " + to + " violates morphism functionality");
            } else {
                putNode(to, removeNode(from));
            }
        } else {
            // update all mappings from elem1 to elem2
        	for (Map.Entry<Node,Node> nodeEntry: nodeMap().entrySet()) {
        		if (nodeEntry.getValue().equals(from)) {
        			nodeEntry.setValue(to);
        		}
        	}
        }
    }

    public void replaceUpdate(GraphShape graph, Edge from, Edge to) {
        // we perform no check because, due to the contract of GraphListener,
        // the corresponding node and edge replacements are notified separately
        if (graph == dom) {
            if (edgeMap().containsKey(from)) {
                throw new UnsupportedOperationException(
                    "Replacement of " + from + " by " + to + " violates morphism functionality");
            } else {
                putEdge(to, removeEdge(from));
            }
        } else {
            // update all mappings from elem1 to elem2
        	for (Map.Entry<Edge,Edge> nodeEntry: edgeMap().entrySet()) {
        		if (nodeEntry.getValue().equals(from)) {
        			nodeEntry.setValue(to);
        		}
        	}
        }
    }

    @Deprecated
    public Element put(Element key, Element value) {
    	if (key instanceof Node) {
    		return putNode((Node) key, (Node) value);
    	} else {
    		return putEdge((Edge) key, (Edge) value);
    	}
    }

    @Deprecated
    public Element remove(Element key) {
    	if (key instanceof Node) {
    		return removeNode((Node) key);
    	} else {
    		return removeEdge((Edge) key);
    	}
    }

    /**
     * This implementation does not call <tt>{@link #remove(Element)}</tt> but works directly 
     * on the element map. Specializations beware!
     * @param image the mage to be removed from the map
     * @return <tt>true</tt> if any entry was actually removed
     */
    public boolean removeImage(Element image) {
    	if (image instanceof Node) {
    		return removeNodeImage((Node) image);
    	} else {
    		return removeEdgeImage((Edge) image);
    	}
    }

    /**
     * This implementation does not call <tt>{@link #remove(Element)}</tt> but works directly 
     * on the element map. Specializations beware!
     * @param image the mage to be removed from the map
     * @return <tt>true</tt> if any entry was actually removed
     */
    @Deprecated
    public boolean removeNodeImage(Node image) {
        boolean result = false;
        // iterate over the element map entries
        Iterator<Map.Entry<Node,Node>> entryIter = nodeMap().entrySet().iterator();
        while (entryIter.hasNext()) {
            Map.Entry<Node,Node> entry = entryIter.next();
            if (entry.getValue().equals(image)) {
                entryIter.remove();
                result = true;
            }
        }
        return result;
    }


    /**
     * This implementation does not call <tt>{@link #remove(Element)}</tt> but works directly 
     * on the element map. Specializations beware!
     * @param image the mage to be removed from the map
     * @return <tt>true</tt> if any entry was actually removed
     */
    @Deprecated
    public boolean removeEdgeImage(Edge image) {
        boolean result = false;
        // iterate over the element map entries
        Iterator<Map.Entry<Edge,Edge>> entryIter = edgeMap().entrySet().iterator();
        while (entryIter.hasNext()) {
            Map.Entry<Edge,Edge> entry = entryIter.next();
            if (entry.getValue().equals(image)) {
                entryIter.remove();
                result = true;
            }
        }
        return result;
    }


    public void setFixed() {
        dom.setFixed();
        cod.setFixed();
        fixed = true;
        assert dom().nodeSet().containsAll(nodeMap().keySet()) : "Morphism domain does not contain all node keys: \n"+this;
        assert dom().edgeSet().containsAll(edgeMap().keySet()) : "Morphism domain does not contain all edge keys: \n"+this;
    }

    /**
     * Abstract clone method with the correct visibility and return type.
     */
	@Override
    abstract public Morphism clone();

    /**
     * Constructs a morphism that is the concatenation of the inverse of the one morphism,
     * followed by another morphism, if this concatenation exists. 
     * It may fail to exist if the inverted morphism is non-injective on elements
     * on which the concatenated morphism is injective; in this case 
     * an {@link FormatException} is thrown.
     * The result is to be stored in a predefined morphism, whose
     * domain and codomain are assumed to have been constructed correctly.
     * @param invert morphism whose inverse is serving as the first argument of the concatenation
     * @param concat second argument of the concatenation
     * @param result morphism where the result is to be stored; may be affected even if a {@link FormatException} is thrown
     * @throws FormatException if the injectivity of <tt>invert</tt> and <tt>concat</tt> is inconsistent 
     */
    static protected void constructInvertConcat(NodeEdgeMap invert, NodeEdgeMap concat, NodeEdgeMap result) throws FormatException {
    	for (Map.Entry<Node,Node> entry: invert.nodeMap().entrySet()) {
            Node image = concat.getNode(entry.getKey());
            if (image != null) {
                Node key = entry.getValue();
                // result already contains an image for nodeKey
                // if it is not the same as the one we want to insert now,
                // stop the whole thing; otherwise we're fine
                Node oldImage = result.getNode(key);
                if (oldImage != null && !oldImage.equals(image)) {
                    throw new FormatException();
                } else {
                	result.putNode(key, image);
                }
            }
        }        
    	for (Map.Entry<Edge,Edge> entry: invert.edgeMap().entrySet()) {
            Edge image = concat.getEdge(entry.getKey());
            if (image != null) {
                Edge key = entry.getValue();
                // result already contains an image for nodeKey
                // if it is not the same as the one we want to insert now,
                // stop the whole thing; otherwise we're fine
                Edge oldImage = result.putEdge(key, image);
                if (oldImage != null && !oldImage.equals(image)) {
                    throw new FormatException();
                }
                String var = RegExprLabel.getWildcardId(key.label());
                if (var != null) {
                    if (!(concat instanceof VarNodeEdgeMap && result instanceof VarNodeEdgeMap)) {
                        throw new FormatException();
                    } else {
                        ((VarNodeEdgeMap) result).putVar(var, ((VarNodeEdgeMap) concat).getVar(var));
                    }
                }
            }
        }
    }

    /**
     * Constructs a morphism that is the concatenation of two morphisms.
     * The result is to be stored in a predefined morphism, whose
     * domain and codomain are assumed to have been constructed correctly.
     * @param subject the first argument of concatenation
     * @param concat the second argument of concatenation
     * @param result morphism where the result is to be stored
     */
    static protected void constructConcat(Morphism subject, Morphism concat, Morphism result) {
    	for (Map.Entry<Node,Node> entry: concat.nodeMap().entrySet()) {
            Node image = subject.getNode(entry.getValue());
            if (image != null) {
                result.putNode(entry.getKey(), image);
            }
        }
    	for (Map.Entry<Edge,Edge> entry: concat.edgeMap().entrySet()) {
            Edge image = subject.getEdge(entry.getValue());
            if (image != null) {
                result.putEdge(entry.getKey(), image);
            }
        }
    }

    /**
     * Factory method for match strategies.
     */
    abstract protected MatchStrategy<VarNodeEdgeMap> createMatchStrategy();

    /**
     * The codomain of this Morphism.
     * @invariant cod != null
     */
    protected Graph cod;
    /**
     * The domain of this Morphism.
     * @invariant dom != null
     */
    protected Graph dom;
    /**
     * A switch to signal whether the morphism is still modifiable.
     * @see #isFixed()
     */
    protected boolean fixed;

    /**
     * Switch to control whether injectivity is checked early (during
     * potential stabilisation) or late (during morphism construction).
     * Late seems to be faster for small morphisms;
     * we conjecture that early is faster for larger cases.
     */
    protected static final boolean EARLY_INJECTIVITY_CHECK = false;

    // ---------------------------- reporting --------------------------------

    /** Reporter instance for profiling {@link Morphism} methods. */
    static public final Reporter reporter = Reporter.register(DefaultMorphism.class);
    /** Handle for profiling {@link #getTotalExtensions()} */
    static public final int GET_TOTAL_EXTENSIONS = reporter.newMethod("getTotalExtensions()");
    /** Handle for profiling {@link #hasTotalExtensions()} */
    static public final int HAS_TOTAL_EXTENSIONS = reporter.newMethod("hasTotalExtensions()");
    /** Handle for profiling {@link #after(Morphism)} */
    static private final int AFTER = reporter.newMethod("after(Morphism)");
    /** Handle for profiling {@link #afterInverse(Morphism)} */
    static private final int AFTER_INVERSE = reporter.newMethod("afterInverse(Morphism)");
}
