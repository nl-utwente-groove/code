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
 * $Id: Element.java,v 1.7 2008-01-30 09:32:53 iovka Exp $
 */
package groove.graph;

/**
 * Common interface for graph elements.
 * The direct subinterfaces are: {@link Node} and {@link Edge}.
 * {@link Edge}s are essentially labelled hyper-edges
 * consisting of a number of <i>end points</i> (at least one), which are {@link Node}s.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface Element extends Comparable<Element>, java.io.Serializable {
//    /**
//     * Computes the image of this element under a given element map.
//     * The result is either the current image of this element under
//     * the map, or, if the map does not contain this element as a
//     * key and the element is an {@link Edge}, a new element 
//     * constructed from the images of the end points.
//     * If the end points also do not occur as map keys, the
//     * method returns <tt>null</tt>
//     * @param elementMap the element map used to determine the image
//     * @return the image of this element according to <tt>elementMap</tt>,
//     * constructed fresh if required and possible; or <tt>null</tt> if
//     * the parts of this element are not in <tt>elementMap.keySet()</tt>
//     * @require <tt>elementMap != null</tt>
//     * @ensure <tt>result == null</tt> if 
//     * <tt>! elementMap.keySet().containsAll(ends())</tt>; otherwise
//     * <tt>result.end(i).equals(elementMap.get(end(i)))</tt> for all valid <tt>i</tt>. 
//     * @deprecated use NodeEdgeMap#getNode(Node) or {@link NodeEdgeMap#mapEdge(Edge)}
//     */
//	@Deprecated
//    public Element imageFor(GenericNodeEdgeMap elementMap);
//
    /**
     * The rules for ordering {@link Element}s are as follows:
     * <ul>
     * <li> {@link Element}s are comparable to all other {@link Element}s,
     * but to no other objects
     * <li> {@link Node}s must define their own natural ordering
     * <li> A {@link Node} is smaller than an {@link Edge} if and only if it smaller than
     * or equal to the source node of the {@link Edge}
     * <li> To compare two {@link Edge}s, first compare their source nodes,
     * then their end count, then their labels and then the rest of their end points
     * </ul>
     */
    public int compareTo(Element obj);
}