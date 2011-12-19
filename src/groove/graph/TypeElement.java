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
package groove.graph;

import java.util.Set;

/** Superinterface for {@link TypeNode} and {@link TypeEdge}.
 * 
 * @author Arend Rensink
 * @version $Revision $
 */
public interface TypeElement extends Element {
    /** 
     * Returns the type label of this type element.
     * Note that for type edges, the label does not completely determine
     * the edge, whereas for type nodes it does. 
     */
    public TypeLabel label();

    /**
     * Returns the (possibly {@code null}) type graph to which
     * this type element belongs.
     * @return the associated type graph, or {@code null} if there is none.
     */
    public TypeGraph getGraph();

    /** Indicates if this type element has an associated type graph.
     * @see #getGraph() 
     */
    public boolean hasGraph();

    /** Returns the (reflexively and transitively closed) set of subtypes of this type element. */
    public Set<? extends TypeElement> getSubtypes();

    /** Returns the (reflexively and transitively closed) set of supertypes of this type element. */
    public Set<? extends TypeElement> getSupertypes();
}
