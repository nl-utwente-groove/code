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
package groove.abstraction.neigh.shape;

/**
 * Data structure storing a "flattened" representation of a shape graph.
 * This is a representation that is as memory-efficient as possible.
 * @author Arend Rensink
 * @version $Revision $
 */
interface ShapeStore {
    /** Creates a shape graph store from a given cache. */
    public ShapeStore flatten(ShapeCache cache);

    /** Fills a given shape cache with the data from this store. */
    public void fill(ShapeCache cache);
}
