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
package groove.abstraction.neigh.trans;

import groove.abstraction.neigh.MyHashSet;
import groove.abstraction.neigh.shape.ShapeEdge;

import java.util.Iterator;
import java.util.Set;

/**
 * Iterator that produces the power set of a set of edges.
 * @author Eduardo Zambon
 */
public class PowerSetIterator implements Iterator<Set<ShapeEdge>> {

    final Set<ShapeEdge> fixedEdges;
    final ShapeEdge flexEdges[];
    final int masks[];
    int curr;
    Set<ShapeEdge> next;

    /** EDUARDO: Comment this... */
    public PowerSetIterator(Set<ShapeEdge> fixedEdges, Set<ShapeEdge> flexEdges) {
        this.fixedEdges = fixedEdges;
        this.flexEdges = new ShapeEdge[flexEdges.size()];
        this.masks = new int[flexEdges.size()];
        int i = 0;
        for (ShapeEdge elem : flexEdges) {
            this.flexEdges[i] = elem;
            this.masks[i] = 1 << i;
            i++;
        }
        this.curr = 0;
        this.computeFirst();
    }

    @Override
    public boolean hasNext() {
        return this.next != null;
    }

    @Override
    public Set<ShapeEdge> next() {
        assert this.hasNext();
        Set<ShapeEdge> result = this.next;
        this.computeNext();
        this.curr++;
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private void computeFirst() {
        assert this.curr == 0;
        Set<ShapeEdge> result = new MyHashSet<ShapeEdge>();
        result.addAll(this.fixedEdges);
        this.curr++;
        this.next = result;
    }

    private void computeNext() {
        Set<ShapeEdge> result = new MyHashSet<ShapeEdge>();
        for (int i = 0; i < this.flexEdges.length; i++) {
            if ((this.curr & this.masks[i]) == this.masks[i]) {
                result.add(this.flexEdges[i]);
            }
        }
        if (!result.isEmpty()) {
            result.addAll(this.fixedEdges);
        } else {
            result = null;
        }
        this.next = result;
    }

}