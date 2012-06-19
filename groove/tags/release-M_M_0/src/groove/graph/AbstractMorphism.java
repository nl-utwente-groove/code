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
 * $Id: AbstractMorphism.java,v 1.13 2008-01-30 09:32:57 iovka Exp $
 */
package groove.graph;

import groove.match.MatchStrategy;
import groove.rel.VarNodeEdgeMap;
import groove.view.FormatException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of a morphism on the basis of a single (hash) map for both
 * nodes and edges.
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class AbstractMorphism extends
        AbstractNodeEdgeMap<Node,Node,Edge,Edge> implements Morphism {
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
     * Constructs a new Morphism as a clone of another. Source and target are
     * aliased, whereas internal maps are cloned.
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

    /**
     * @require <tt>morph instanceof InternalMorphism</tt>
     */
    public Morphism after(Morphism morph) {
        Morphism result = createMorphism(morph.dom(), this.cod);
        constructConcat(this, morph, result);
        return result;
    }

    /** @ensure <tt>result instanceof InternalMorphism</tt> */
    public Morphism then(Morphism morph) {
        Morphism result = createMorphism(this.dom, morph.cod());
        constructConcat(morph, this, result);
        return result;
    }

    /**
     * Returns the concatenation of this node map after the inverse of another,
     * if that yields a function. The concatenation exists iff
     * <tt>other.get(x)</tt> equals <tt>other.get(y)</tt> (where both are
     * non-null) implies <tt>this.get(x)</tt> equals <tt>this.get(y)</tt>.
     * If the concatenation does not exist, the method returns <tt>null</tt>.
     * @param morph the other morphism, to be inverted in the concatenation
     * @ensure <tt>result == null || result: InternalMorphism == this \circ other^{-1}</tt>
     */
    public Morphism afterInverse(Morphism morph) {
        try {
            Morphism result = createMorphism(morph.cod(), this.cod);
            constructInvertConcat(morph, this, result);
            return result;
        } catch (FormatException exc) {
            return null;
        }
    }

    /**
     * The type of resulting morphism is determined by
     * {@link #createMorphism(Graph, Graph)}.
     */
    public Morphism inverseThen(Morphism morph) {
        try {
            Morphism result = createMorphism(this.cod, morph.cod());
            constructInvertConcat(this, morph, result);
            return result;
        } catch (FormatException exc) {
            return null;
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
        return this.fixed;
    }

    // ------------------------- OBJECT OVERRIDES -----------------------

    public boolean isTotal() {
        return nodeMap().size() + edgeMap().size() == dom().size();
    }

    public Graph dom() {
        return this.dom;
    }

    public Graph cod() {
        return this.cod;
    }

    // ------------------------- OBJECT OVERRIDES -----------------------

    /**
     * This implementation invokes <tt>#equals(Morphism)</tt> for the actual
     * comparison.
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Morphism) {
            return dom().equals(((Morphism) other).dom())
                && cod().equals(((Morphism) other).cod())
                && super.equals(other);
        } else {
            return false;
        }
    }

    /**
     * The hash code of a morphism is composed out of those for domain, codomain
     * and element map.
     */
    @Override
    public int hashCode() {
        int res =
            (dom().hashCode() << 6) + (cod().hashCode() << 3)
                + elementMap().hashCode();
        return res;
    }

    @Override
    public String toString() {
        return "Source graph:  " + AbstractGraphShape.toString(this.dom)
            + "\nTarget graph:  " + AbstractGraphShape.toString(this.cod)
            + "\nNode and edge mapping: " + elementMap().toString();
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
        if (graph == this.dom) {
            removeNode(node);
        } else {
            assert graph == this.cod : "Morphism listens only to domain and codomain";
            removeNodeImage(node);
            assert !containsValue(node) : "Image " + node
                + " not removed properly from morphism " + this;
        }
    }

    /**
     * Removes the given node or edge from the keys or images of this morphism.
     */
    public void removeUpdate(GraphShape graph, Edge edge) {
        if (graph == this.dom) {
            removeEdge(edge);
        } else {
            assert graph == this.cod : "Morphism listens only to domain and codomain";
            removeEdgeImage(edge);
            assert !containsValue(edge) : "Image " + edge
                + " not removed properly from morphism " + this;
        }
    }

    public void replaceUpdate(GraphShape graph, Node from, Node to) {
        // we perform no check because, due to the contract of GraphListener,
        // the corresponding node and edge replacements are notified separately
        if (graph == this.dom) {
            if (nodeMap().containsKey(from)) {
                throw new UnsupportedOperationException("Replacement of "
                    + from + " by " + to + " violates morphism functionality");
            } else {
                putNode(to, removeNode(from));
            }
        } else {
            // update all mappings from elem1 to elem2
            for (Map.Entry<Node,Node> nodeEntry : nodeMap().entrySet()) {
                if (nodeEntry.getValue().equals(from)) {
                    nodeEntry.setValue(to);
                }
            }
        }
    }

    public void replaceUpdate(GraphShape graph, Edge from, Edge to) {
        // we perform no check because, due to the contract of GraphListener,
        // the corresponding node and edge replacements are notified separately
        if (graph == this.dom) {
            if (edgeMap().containsKey(from)) {
                throw new UnsupportedOperationException("Replacement of "
                    + from + " by " + to + " violates morphism functionality");
            } else {
                putEdge(to, removeEdge(from));
            }
        } else {
            // update all mappings from elem1 to elem2
            for (Map.Entry<Edge,Edge> nodeEntry : edgeMap().entrySet()) {
                if (nodeEntry.getValue().equals(from)) {
                    nodeEntry.setValue(to);
                }
            }
        }
    }

    /**
     * This implementation 
     * works directly on the element map. Specialisations beware!
     * @param image the image to be removed from the map
     * @return <tt>true</tt> if any entry was actually removed
     */
    private boolean removeNodeImage(Node image) {
        boolean result = false;
        // iterate over the element map entries
        Iterator<Map.Entry<Node,Node>> entryIter =
            nodeMap().entrySet().iterator();
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
     * This implementation 
     * works directly on the element map. Specialisations beware!
     * @param image the image to be removed from the map
     * @return <tt>true</tt> if any entry was actually removed
     */
    private boolean removeEdgeImage(Edge image) {
        boolean result = false;
        // iterate over the element map entries
        Iterator<Map.Entry<Edge,Edge>> entryIter =
            edgeMap().entrySet().iterator();
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
        this.dom.setFixed();
        this.cod.setFixed();
        this.fixed = true;
        assert dom().nodeSet().containsAll(nodeMap().keySet()) : "Morphism domain does not contain all node keys: \n"
            + this;
        assert dom().edgeSet().containsAll(edgeMap().keySet()) : "Morphism domain does not contain all edge keys: \n"
            + this;
    }

    /**
     * Abstract clone method with the correct visibility and return type.
     */
    @Override
    abstract public Morphism clone();

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
     * Switch to control whether injectivity is checked early (during potential
     * stabilisation) or late (during morphism construction). Late seems to be
     * faster for small morphisms; we conjecture that early is faster for larger
     * cases.
     */
    protected static final boolean EARLY_INJECTIVITY_CHECK = false;

    /**
     * Constructs a morphism that is the concatenation of the inverse of the one
     * morphism, followed by another morphism, if this concatenation exists. It
     * may fail to exist if the inverted morphism is non-injective on elements
     * on which the concatenated morphism is injective; in this case an
     * {@link FormatException} is thrown. The result is to be stored in a
     * predefined morphism, whose domain and codomain are assumed to have been
     * constructed correctly.
     * @param invert morphism whose inverse is serving as the first argument of
     *        the concatenation
     * @param concat second argument of the concatenation
     * @param result morphism where the result is to be stored; may be affected
     *        even if a {@link FormatException} is thrown
     * @throws FormatException if the injectivity of <tt>invert</tt> and
     *         <tt>concat</tt> is inconsistent
     */
    static public void constructInvertConcat(NodeEdgeMap invert,
            NodeEdgeMap concat, NodeEdgeMap result) throws FormatException {
        for (Map.Entry<Node,Node> entry : invert.nodeMap().entrySet()) {
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
        for (Map.Entry<Edge,Edge> entry : invert.edgeMap().entrySet()) {
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
            }
        }
    }

    /**
     * Constructs a morphism that is the concatenation of two morphisms. The
     * result is to be stored in a predefined morphism, whose domain and
     * codomain are assumed to have been constructed correctly.
     * @param subject the first argument of concatenation
     * @param concat the second argument of concatenation
     * @param result morphism where the result is to be stored
     */
    static public void constructConcat(Morphism subject, Morphism concat,
            Morphism result) {
        for (Map.Entry<Node,Node> entry : concat.nodeMap().entrySet()) {
            Node image = subject.getNode(entry.getValue());
            if (image != null) {
                result.putNode(entry.getKey(), image);
            }
        }
        for (Map.Entry<Edge,Edge> entry : concat.edgeMap().entrySet()) {
            Edge image = subject.getEdge(entry.getValue());
            if (image != null) {
                result.putEdge(entry.getKey(), image);
            }
        }
    }
}