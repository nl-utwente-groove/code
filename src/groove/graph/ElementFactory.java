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
 * $Id$
 */
package groove.graph;

import groove.util.Dispenser;
import groove.util.SingleDispenser;

/** Factory class for graph elements. */
public interface ElementFactory<N extends Node,E extends Edge> {
    /** 
     * Creates a fresh node with a number that is as yet
     * unused according to this factory.
     * @see ElementFactory#createNode(Dispenser)
     */
    N createNode();

    /** 
     * Returns a suitable node with a given (non-negative) node number.
     * The node is created if no such node is known to this factory, but
     * the call may fail if a node with the given number is known but unsuitable.
     * This calls {@link #createNode(Dispenser)} with a {@link SingleDispenser}
     * initialised at the given number.
     * @throws IllegalStateException if the number is unsuitable
     * @see #createNode(Dispenser)
     */
    N createNode(int nr);

    /** 
     * Returns a suitable node with a number obtained from a dispenser.
     * Typically the node will get the first available number,
     * but if node numbers may be unsuitable for some reason then
     * the dispenser may be invoked multiple times. 
     * @throws IllegalStateException if the dispenser runs out of numbers
     */
    N createNode(Dispenser dispenser);

    /** Creates a label with the given text. */
    Label createLabel(String text);

    /** Creates an edge with the given source, label text and target. */
    E createEdge(N source, String text, N target);

    /** Creates an edge with the given source, label and target. */
    E createEdge(N source, Label label, N target);

    /** Creates a fresh morphism between the elements of this factory. */
    Morphism<N,E> createMorphism();

    /** Returns the maximum node number known to this factory. */
    int getMaxNodeNr();
}
