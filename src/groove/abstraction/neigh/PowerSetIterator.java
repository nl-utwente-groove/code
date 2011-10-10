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
package groove.abstraction.neigh;

import gnu.trove.THashSet;
import groove.abstraction.neigh.shape.ShapeEdge;
import groove.abstraction.neigh.trans.EquationSystem.EdgeBundle;
import groove.abstraction.neigh.trans.Materialisation;

import java.util.Iterator;
import java.util.Set;

/**
 * EDUARDO: Comment this... 
 * @author Eduardo Zambon
 */
public class PowerSetIterator implements Iterator<Set<ShapeEdge>> {

    final Materialisation mat;
    final Set<EdgeBundle> bundles;
    final Set<ShapeEdge> fixedEdges;
    final ShapeEdge flexEdges[];
    final int masks[];
    int curr;
    Set<ShapeEdge> next;
    boolean areAllBundlesUnbounded;

    /** EDUARDO: Comment this... */
    public PowerSetIterator(Materialisation mat, Set<ShapeEdge> fixedEdges,
            Set<ShapeEdge> flexEdges, Set<EdgeBundle> bundles) {
        this.mat = mat;
        this.bundles = bundles;
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
        this.checkBundles();
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
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private void checkBundles() {
        boolean allUnbounded = true;
        for (EdgeBundle bundle : this.bundles) {
            if (!bundle.getOrigMult().isUnbounded()) {
                allUnbounded = false;
                break;
            }
        }
        this.areAllBundlesUnbounded = allUnbounded;
    }

    private void computeFirst() {
        assert this.curr == 0;
        Set<ShapeEdge> result = new THashSet<ShapeEdge>();
        result.addAll(this.fixedEdges);
        this.curr++;
        this.next = result;
    }

    private void computeNext() {
        Set<ShapeEdge> intResult = new THashSet<ShapeEdge>();
        Set<ShapeEdge> finalResult = new THashSet<ShapeEdge>();
        boolean finished = false;
        while (!finished) {
            for (int i = 0; i < this.flexEdges.length; i++) {
                if ((this.curr & this.masks[i]) == this.masks[i]) {
                    intResult.add(this.flexEdges[i]);
                }
            }
            if (!intResult.isEmpty()) {
                if (this.isValid(intResult)) {
                    finalResult.addAll(intResult);
                    finalResult.addAll(this.fixedEdges);
                    /*if (!this.isValid(finalResult)) {
                        finalResult = intResult;
                    }*/
                    this.fixOnSecondStage(intResult);
                    finished = true;
                } else {
                    intResult.clear();
                    finalResult.clear();
                }
                this.curr++;
            } else {
                finished = true;
                finalResult = null;
            }
        }
        this.next = finalResult;
    }

    private void fixOnSecondStage(Set<ShapeEdge> edges) {
        for (EdgeBundle bundle : this.bundles) {
            if (bundle.getOrigMult().isUnbounded()) {
                for (ShapeEdge edge : edges) {
                    if (bundle.containsEdge(edge)) {
                        this.mat.setFixedOnSecondStage(edge);
                    }
                }
            }
        }
    }

    private boolean isValid(Set<ShapeEdge> edges) {
        if (this.areAllBundlesUnbounded) {
            return true;
        }
        boolean valid = true;
        Set<ShapeEdge> bundleEdges = new THashSet<ShapeEdge>();
        for (EdgeBundle bundle : this.bundles) {
            for (ShapeEdge edge : edges) {
                if (bundle.containsEdge(edge)) {
                    bundleEdges.add(edge);
                }
            }
            if (!Multiplicity.getEdgeSetMult(bundleEdges).le(
                bundle.getOrigMult())) {
                valid = false;
                break;
            }
            bundleEdges.clear();
        }
        return valid;
    }
}
