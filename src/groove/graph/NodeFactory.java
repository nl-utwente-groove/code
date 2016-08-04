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

import java.util.NoSuchElementException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import groove.util.Dispenser;
import groove.util.SingleDispenser;

/**
 * Abstract implementation preparing some of the functionality
 * of an {@link ElementFactory}.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class NodeFactory<N extends Node> {
    /**
     * Creates a fresh node with a number that is as yet
     * unused according to this factory.
     * @see #createNode(Dispenser)
     */
    public @NonNull N createNode() {
        return createNode(getNodeNrDispenser());
    }

    /**
     * Returns a suitable node with a given (non-negative) node number.
     * The node is created if no such node is known to this factory, but
     * the call may fail if a node with the given number is known but unsuitable.
     * This calls {@link #createNode(Dispenser)} with a {@link SingleDispenser}
     * initialised at the given number.
     * @throws NoSuchElementException if the number is unsuitable
     * @see #createNode(Dispenser)
     */
    public @NonNull N createNode(int nr) {
        return createNode(Dispenser.single(nr));
    }

    /**
     * Returns a suitable node with a number obtained from a dispenser.
     * Typically the node will get the first available number,
     * but if node numbers may be unsuitable for some reason then
     * the dispenser may be invoked multiple times.
     * @throws NoSuchElementException if the dispenser runs out of numbers
     */
    public @NonNull N createNode(Dispenser dispenser) {
        @Nullable N result = null;
        do {
            int nr = dispenser.getNext();
            result = getNode(nr);
            if (result == null) {
                // create a new node of the correct type
                result = newNode(nr);
                registerNode(result);
            } else if (!isAllowed(result)) {
                // do not use the existing node with this number
                result = null;
            }
        } while (result == null);
        return result;
    }

    /**
     * Callback method from {@link #createNode()} to retrieve a
     * previously created node with a given number.
     * @return a previously created node with number {@code nr},
     * or {@code null} if there is no such node
     */
    abstract protected @Nullable N getNode(int nr);

    /**
     * Callback method from {@link #createNode()} to register a given node
     * as having been created by this factory.
     * It is an error to register the same node number more than once.
     */
    protected abstract void registerNode(N node);

    /**
     * Callback method from {@link #createNode()} to test whether
     * a given (previously existing) node is suitable to be produced
     * by this factory.
     */
    abstract protected boolean isAllowed(N node);

    /** Callback factory method to create a node of the right type. */
    protected abstract @NonNull N newNode(int nr);

    /** Returns the fresh node number dispenser of this factory. */
    protected abstract Dispenser getNodeNrDispenser();
}
