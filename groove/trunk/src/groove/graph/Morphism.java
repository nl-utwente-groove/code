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
 * $Id: Morphism.java,v 1.6 2008-01-30 09:32:52 iovka Exp $
 */
package groove.graph;


/**
 * Interface of a partial graph morphism.
 * Extends <tt>NodeEdgeMap</tt> with a fixed source and target graph.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:52 $
 */
public interface Morphism extends NodeEdgeMap, GraphListener {
    /**
     * Returns the concatenation of another morphism followed by this one.
     * Does not alias or modify either this morphism or the other.
     * The type of the result is determined by {@link #createMorphism(Graph, Graph)}.
     * @param morph the morphism to be applied before this one
     * @return the concatenation of <tt>morph</tt>, followed by this morphism
     * @require <tt>morph != null</tt>, <tt>morph.cod().equals(morph.dom())</tt>
     * @ensure <tt>result == this \circ morph</tt>
     * @see #then(Morphism)
     */
    public Morphism after(Morphism morph);

    /**
     * Returns the concatenation of this morphism followed by another.
     * Does not alias or modify either this morphism or the other.
     * The type of the result is determined by {@link #createMorphism(Graph, Graph)}.
     * @param morph the morphism to be applied after this one
     * @return the concatenation of this morphism, followed by morph
     * @require <tt>morph != null</tt>, <tt>this.cod().equals(morph.dom())</tt>
     * @ensure <tt>result == morph \circ this</tt>
     * @see #after(Morphism)
     */
    public Morphism then(Morphism morph);

    /**
     * Returns the concatenation of the inverse of another morphism
     * followed by this one, if defined.
     * Does not alias or modify either this morphism or the other.
     * The type of the result is determined by {@link #createMorphism(Graph, Graph)}.
     * @param morph the morphism to be inversedly applied before this one
     * @return the concatenation of this morphism, followed by the inverse of morph
     * @require morph != null, this.dom().equals(morph.dom())
     * @ensure <tt>result == this \circ morph^{-1}</tt>, if yields is a morphism
     * @see #inverseThen(Morphism)
     */
    public Morphism afterInverse(Morphism morph);

    /**
     * Returns the concatenation of the inverse of this morphism
     * followed by another, if defined.
     * Does not alias or modify either this morphism or the other.
     * The type of the result is determined by {@link #createMorphism(Graph, Graph)}.
     * @param morph the morphism to be applied after the inverse of this one
     * @return the concatenation of the inverse of this Moprhism, followed by morph
     * @require morph != null, this.dom().equals(morph.dom())
     * @ensure <tt>result == morph \circ this^{-1}</tt>, if this yields a morphism
     * @see #afterInverse(Morphism)
     */
    public Morphism inverseThen(Morphism morph);
    
    /**
     * Tests whether all nodes and edges in cod()
     * are in the codomain of this morphism.
     */
    public boolean isSurjective();

    /**
     * Tests whether all nodes in dom()
     * are mapped to different elements.
     */
    public boolean isInjective();

    /**
     * Tests whether all nodes and edges in dom()
     * are in the domain of this morphism.
     */
    public boolean isTotal();

    /**
     * Indicates whether this mapping is modifiable.
     * If not, no <tt>put</tt>- and <tt>remove</tt>-commands
     * are allowed any more.
     * @return <tt>true</tt> if the mapping is modifiable
     * @see #setFixed()
     */
    boolean isFixed();

    /**
     * After calling this method, no modifications to the morphism
     * should be made any more. The effect of the <tt>put</tt>- and
     * <tt>remove</tt>-commands becomes unpredictable.
     * @ensure <tt>! isModifiable()</tt>
     * @see #isFixed()
     */
    void setFixed();

    /**
     * Returns the source graph of this mapping.
     * @ensure result != null
     */
    public Graph dom();

    /**
     * Returns the target graph of this mapping.
     * @ensure result != null
     */
    public Graph cod();

    // ------------------------------------ MAP QUERIES -------------------------------

    /** 
     * Tests whether a given graph element is in the domain of the mapping.
     * @param key the element for which is tested
     * @return <tt>true</tt> if <tt>key</tt> in the domain of this mapping
     * @require <tt>key != null</tt>
     * @ensure <tt>result == (get(key) != null)</tt> 
     */
    boolean containsKey(Element key);

    /** 
     * Tests whether a given graph element is in the range of the mapping.
     * @param value the element for which is tested
     * @return <tt>true</tt> if <tt>value</tt> in the range of this mapping
     * @require <tt>value != null</tt>
     * @ensure <tt>result</tt> iff <tt>get(key).equals(value))</tt> for some <tt>key</tt> 
     */
    boolean containsValue(Element value);

    /**
     * Returns the value associated by this mapping to a given graph node.
     * @param key the element whose image is requested
     * @return the value associated with <tt>key</tt>, 
     * or <tt>null</tt> if there is none such
     */
    Node getNode(Node key);

    /**
     * Returns the value associated by this mapping to a given graph edge.
     * @param key the element whose image is requested
     * @return the value associated with <tt>key</tt>, 
     * or <tt>null</tt> if there is none such
     */
    Edge getEdge(Edge key);

    /**
     * Returns a modifiable view on the mapping's element map.
     * This sends source graph nodes to target graph nodes and source graph
     * edges to target graph edges.
     * @return the element map underlying this mapping.
     */
    NodeEdgeMap elementMap();

    /**
     * Convenience method for <tt>put(Element,Element)</tt> in case the
     * type of the element is <tt>Node</tt>.
     */
    Node putNode(Node key, Node value);

    /**
     * Convenience method for <tt>put(Element,Element)</tt> in case the
     * type of the element is <tt>Edge</tt>.
     */
    Edge putEdge(Edge key, Edge value);
    
    /**
     * Convenience method for <tt>remove(Element)</tt> in case the
     * type of the element is <tt>Edge</tt>.
     */
    @Deprecated
    Edge removeEdge(Edge key);

    /**
     * Convenience method for <tt>remove(Element)</tt> in case the
     * type of the element is <tt>Node</tt>.
     */
    @Deprecated
    Node removeNode(Node key);
    
    /**
     * Removes a number of (key,value)-pairs of graph elements from the morphism, 
     * based on a given (node) image.
     * Any edge in the morphism of which the key is a part is also 
     * removed from the range.
     * The method returns <tt>true</tt> if the image did occur, or
     * <tt>false</tt> otherwise.
     * @param image the value to be removed
     * @return <tt>true</tt> if the image occurred in
     * the morphism
     * @ensure <tt>! containsValue(image)</tt>.
     */
    @Deprecated
    boolean removeNodeImage(Node image);
    
    /**
     * Removes a  (key,value)-pair of edges from the morphism, 
     * based on a given (edge) image.
     * The method returns <tt>true</tt> if the image did occur, or
     * <tt>false</tt> otherwise.
     * @param image the value to be removed
     * @return <tt>true</tt> if the image occurred in
     * the morphism
     * @ensure <tt>! containsValue(value)</tt>.
     */
    @Deprecated
    boolean removeEdgeImage(Edge image);

    /**
     * For efficientcy, morphisms are equal only if they are identical
     * (i.e., the same object). This does not cause difficulties because
     * reasoning is anyhow on the level of isomorphism.
     * @param obj the object to be compared with this one
     * @return <tt>true</tt> if <tt>this</tt> en <tt>obj</tt> are the same object
     */
    public boolean equals(Object obj);
    
    /**
     * Overrides the method in <tt>NodeEdgeMap</tt> to reset equality to identity.
     * @param other the morphism to be compared with this one
     * @return <tt>true</tt> if <tt>equals(other)</tt>
     * @see #equals(Object)
     */
    @Deprecated
    public boolean equals(Morphism other);

    /**
     * Factory method; returns a new, empty morphism between two given graphs.
     * @param dom source graph of the new morphism
     * @param cod target graph of the new morphism
     * @require <tt>dom != null</tt>, <tt>cod != null</tt>
     * @ensure <tt>result.dom()==dom</tt>,
     *         <tt>result.cod()==cod</tt>, <tt>result.size()==0</tt>
     */
    public Morphism createMorphism(Graph dom, Graph cod);

    /**
     * Factory method: returns a new mapping based on the same domain and codomain
     * graphs as the current mapping.
     */
    Morphism clone();
}