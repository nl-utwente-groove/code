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
package groove.gui.jgraph;

import java.util.Iterator;

import org.eclipse.jdt.annotation.NonNull;
import org.jgraph.graph.DefaultPort;

import groove.graph.Graph;
import groove.graph.Node;

/**
 * JGraph vertex wrapping a single graph node and a set of graph edges.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface JVertex<G extends Graph> extends JCell<G> {
    /** Returns the set of incident JEdges. */
    @Override
    public Iterator<? extends JEdge<G>> getContext();

    /**
     * Sets a new node in this JVertex, and resets all other structures
     * to their initial values.
     */
    public void setNode(Node node);

    /**
     * Returns the graph node wrapped by this {@link JVertex}.
     */
    public @NonNull Node getNode();

    /**
     * Returns this graph node's one and only port.
     */
    public DefaultPort getPort();

    /**
     * The cloned object is equal to this one after a reset.
     */
    public JVertex<G> clone();

    /** Returns the number with which this vertex was initialised. */
    public int getNumber();

    /**
     * Callback method yielding a string description of the underlying node,
     * used for the node inscription in case node identities are to be shown.
     * Subclasses may return {@code null} if there is no useful node identity.
     */
    public String getNodeIdString();

    /** Indicates if this jVertex is currently layed-out. */
    boolean isLayoutable();

    /**
     * Sets this jVertex to layed-out.
     * This means that the next attempt to layout the graph will not
     * change the position of this cell.
     * @return {@code true} if the layed-out status changed as a result of this call
     */
    boolean setLayoutable(boolean layoutable);
}