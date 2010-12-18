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
 * $Id: NodeEdgeMap.java,v 1.4 2008-01-30 09:32:58 iovka Exp $
 */
package groove.graph;

import java.util.Map;

/**
 * Specialisation of a {@link Map} for {@link Element}s with some added
 * functionality.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface NodeEdgeMap extends GraphMap<Node,Label,Edge,Node,Label,Edge> {
    /**
     * Returns the concatenation of another morphism followed by this one. Does
     * not alias or modify either this morphism or the other. 
     * @param morph the morphism to be applied before this one
     * @return the concatenation of <tt>morph</tt>, followed by this morphism
     * @see #then(NodeEdgeMap)
     */
    public NodeEdgeMap after(NodeEdgeMap morph);

    /**
     * Returns the concatenation of this morphism followed by another. Does not
     * alias or modify either this morphism or the other.
     * @param morph the morphism to be applied after this one
     * @return the concatenation of this morphism, followed by morph
     * @see #after(NodeEdgeMap)
     */
    public NodeEdgeMap then(NodeEdgeMap morph);

    /**
     * Returns the concatenation of the inverse of another morphism followed by
     * this one, if defined. Does not alias or modify either this morphism or
     * the other.
     * @param morph the morphism to be inversely applied before this one
     * @return the concatenation of this morphism, followed by the inverse of
     *         morph
     * @see #inverseThen(NodeEdgeMap)
     */
    public NodeEdgeMap afterInverse(NodeEdgeMap morph);

    /**
     * Returns the concatenation of the inverse of this morphism followed by
     * another, if defined. Does not alias or modify either this morphism or the
     * other.
     * @param morph the morphism to be applied after the inverse of this one
     * @return the concatenation of the inverse of this morphism, followed by
     *         morph
     * @see #afterInverse(NodeEdgeMap)
     */
    public NodeEdgeMap inverseThen(NodeEdgeMap morph);

    public NodeEdgeMap clone();
}
