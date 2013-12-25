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

import groove.util.DefaultDispenser;
import groove.util.Dispenser;
import groove.util.SingleDispenser;

/**
 * Abstract implementation preparing some of the functionality
 * of an {@link ElementFactory}.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class AbstractFactory<N extends Node,E extends Edge> implements
        ElementFactory<N,E> {
    @Override
    public N createNode() {
        return createNode(this.nodeNrs);
    }

    @Override
    public N createNode(int nr) {
        return createNode(new SingleDispenser(nr));
    }

    @Override
    public N createNode(Dispenser dispenser) {
        int nr = dispenser.getNext();
        notifyNodeNr(nr);
        return newNode(nr);
    }

    /** Callback method to indicate that a given node number has been used. */
    protected final void notifyNodeNr(int nr) {
        this.nodeNrs.maxCount(nr + 1);
    }

    /** Callback factory method to create a node of the right type. */
    protected abstract N newNode(int nr);

    @Override
    public E createEdge(N source, String text, N target) {
        return createEdge(source, createLabel(text), target);
    }

    @Override
    public int getMaxNodeNr() {
        return getNodeNrs().getCount() - 1;
    }

    /** Returns the node number dispenser of this factory. */
    protected final DefaultDispenser getNodeNrs() {
        return this.nodeNrs;
    }

    private DefaultDispenser nodeNrs = new DefaultDispenser();
}
