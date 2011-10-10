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
 * $Id: Node.java,v 1.4 2008-01-30 09:32:58 iovka Exp $
 */
package groove.graph;

/**
 * Interface of a graph node. A node is a graph element that is not a composite.
 * @author Arend Rensink
 * @version $Revision$ $Date: 2008-01-30 09:32:58 $
 */
public interface Node extends Element {
    /**
     * Returns the number of this node.
     * Within a graph, a node is uniquely identified by the combination of 
     * its (precise) class and its number.
     * This means that different implementations can reuse the same range of numbers.
     */
    int getNumber();

    /** 
     * Interface for factories of {@link Node} specialisations.
     * In practice, node classes will be their own factories.
     */
    interface Factory<N extends Node> {
        /** 
         * Creates a new node of the specified type, with a given number.
         */
        N newNode(int nr, TypeNode type);
    }
}
