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

import org.eclipse.jdt.annotation.Nullable;

import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.gui.look.Look;
import groove.gui.look.MultiLabel.Direct;

/**
 * JGraph edge wrapping a set of graph edges.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface JEdge<G extends Graph> extends JCell<G> {
    /**
     * The cloned object is equal to this one after a reset.
     */
    public abstract JEdge<G> clone();

    /**
     * Returns the j-vertex that is the parent of the source port of this
     * j-edge.
     * @return the source vertex; may be {@code null} if the model has not
     * yet been fully initialised
     */
    abstract public @Nullable JVertex<G> getSourceVertex();

    /**
     * Returns the j-vertex that is the parent of the target port of this
     * j-edge.
     * @return the target vertex; may be {@code null} if the model has not
     * yet been fully initialised
     */
    abstract public @Nullable JVertex<G> getTargetVertex();

    /**
     * Returns the common source of the underlying graph edges.
     */
    public abstract Node getSourceNode();

    /**
     * Returns the common target of the underlying graph edges.
     */
    public abstract Node getTargetNode();

    /** Returns true if source and target node coincide. */
    public abstract boolean isLoop();

    /**
     * Returns the first edge from the set of underlying edges.
     */
    public abstract Edge getEdge();

    /**
     * Determines the direction corresponding to a given edge
     * wrapped into this JEdge, to be displayed on the JEdge label.
     * This is {@link Direct#NONE} if {@link JGraph#isShowArrowsOnLabels()}
     * is {@code false}, otherwise {@link Direct#BIDIRECTIONAL} if the edge
     * look is {@link Look#BIDIRECTIONAL}; otherwise it is determined
     * by the relative direction of the edge with respect to this JEdge.
     * @param edge the edge of which the direction should be returned; if {@code null},
     * it is assumed to be a forward edge
     */
    public abstract Direct getDirect(Edge edge);
}