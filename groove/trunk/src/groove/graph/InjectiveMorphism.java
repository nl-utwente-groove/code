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
 * $Id: InjectiveMorphism.java,v 1.1.1.2 2007-03-20 10:42:42 kastenberg Exp $
 */
package groove.graph;

/**
 * Interface of a partial injective graph morphism.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:42:42 $
 */
public interface InjectiveMorphism extends Morphism {
    /**
     * Returns the inverse InjectiveMorphism of this one.
     * Does not modify this InjectiveMorphism.
     * @ensure <tt>result == this^{-1}</tt>
     */
    public InjectiveMorphism inverse();
    
    /**
     * Tests if this injective morphism can be extended to an isomorphism.
     * Does not change the morphism.
     * @return <tt>true</tt> if there exists an isomorphism extending this morphism.
     * @see #extendToIsomorphism()
     */
    public boolean hasIsomorphismExtension();

    /**
     * Extends this injective morphism to an isomorphism, if possible.
     * If there is no isomorphism extension, the morphism is unchanged.
     * @return if <tt>true</tt> then <tt>isTotal</tt> and <tt>isSurjective</tt>.
     * @see #hasIsomorphismExtension()
     */
    public boolean extendToIsomorphism();
    
    /**
     * Returns the inverse image element of a given graph element, if defined.
     * @param elem the element of which the inverse image is demanded
     * @return the inverse image of <tt>elem</tt>;
     * <tt>null</tt> if <tt>elem</tt> is not an image of this morphism
     * @require <tt>elem != null</tt>
     * @ensure if <tt>result != null</tt> then <tt>get(result) == elem</tt>
     */
    @Deprecated
    public Element getInverseElement(Element elem);
    
    /**
     * Factory method; returns a new, empty injective morphism between two given graphs.
     * @param dom source graph of the new morphism
     * @param cod target graph of the new morphism
     * @require <tt>dom != null</tt>, <tt>cod != null</tt>
     * @ensure <tt>result.dom()==dom</tt>,
     *         <tt>result.cod()==cod</tt>, <tt>result.size()==0</tt>
     * @deprecated use {@link #createMorphism(Graph,Graph)} instead
     */
    public InjectiveMorphism createInjectiveMorphism(Graph dom, Graph cod);
}

