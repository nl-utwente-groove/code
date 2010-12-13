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

/** Factory class for graph elements. */
public interface ElementFactory<N extends Node,L extends Label,E extends Edge> {
    /** Creates a fresh node. */
    N createNode();

    /** Creates a node with a given number. */
    N createNode(int nr);

    /** Creates a label with the given text. */
    L createLabel(String text);

    /** Creates an edge with the given source, label text and target. */
    E createEdge(Node source, String label, Node target);

    /** Creates an edge with the given source, label and target. */
    E createEdge(Node source, Label label, Node target);
}
