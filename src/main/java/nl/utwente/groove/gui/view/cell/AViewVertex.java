/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
 * $Id$
 */
package nl.utwente.groove.gui.view.cell;

import static nl.utwente.groove.graph.EdgeRole.BINARY;
import static nl.utwente.groove.util.HTMLConverter.ITALIC_TAG;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.gui.view.GraphViewModel;
import nl.utwente.groove.gui.view.ViewVertex;
import nl.utwente.groove.util.HTMLConverter;

/**
 * Backend-independent implementation of a vertex cell. The incident edge cells
 * (the context) are registered by the edges when they are connected, see
 * {@link AViewEdge#setSource} and {@link AViewEdge#setTarget}.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public abstract class AViewVertex<G extends Graph> extends AViewCell<G>
    implements ViewVertex<G> {
    /** Constructs a vertex cell for a given view model. */
    protected AViewVertex(GraphViewModel<G> viewModel) {
        super(viewModel);
    }

    @Override
    public Iterator<? extends AViewEdge<G>> getContext() {
        return this.context.iterator();
    }

    /** Returns the number of incident edge cells. */
    public int getContextSize() {
        return this.context.size();
    }

    /** Registers an incident edge cell; called when the edge is connected to this vertex. */
    void addContextEdge(AViewEdge<G> edge) {
        this.context.add(edge);
    }

    /** Unregisters an incident edge cell; called when the edge is disconnected. */
    void removeContextEdge(AViewEdge<G> edge) {
        this.context.remove(edge);
    }

    /** The incident edge cells, in order of connection. */
    private Set<AViewEdge<G>> context = new LinkedHashSet<>();

    @Override
    public void setNode(Node node) {
        this.node = node;
    }

    @Override
    public Node getNode() {
        var result = this.node;
        assert result != null; // set before the cell is used
        return result;
    }

    /** The graph node wrapped by this cell; set right after construction. */
    private @Nullable Node node;

    @Override
    final public boolean isLayoutable() {
        return this.layoutable;
    }

    @Override
    final public boolean setLayoutable(boolean layedOut) {
        boolean result = layedOut != this.layoutable;
        if (result) {
            this.layoutable = layedOut;
        }
        return result;
    }

    /** Flag indicating that this vertex is to be laid out. */
    private boolean layoutable;

    /* The clone starts without incident edges; the backend reconnects them. */
    @Override
    public AViewVertex<G> clone() {
        AViewVertex<G> result = (AViewVertex<G>) super.clone();
        result.context = new LinkedHashSet<>();
        return result;
    }

    @Override
    public boolean isCompatible(Edge edge) {
        if (edge.getRole() != BINARY) {
            return true;
        }
        if (getLayout(edge) != null) {
            return false;
        }
        return getController().isShowLoopsAsNodeLabels() && edge.source() == edge.target()
            && edge.source() == getNode();
    }

    @Override
    public Collection<? extends Label> getKeys() {
        Collection<Label> result = new ArrayList<>();
        for (Edge edge : getEdges()) {
            Label key = getKey(edge);
            if (key != null) {
                result.add(key);
            }
        }
        result.addAll(getNodeKeys());
        return result;
    }

    /** Returns the label keys stemming from the node itself rather than from its edges. */
    protected Collection<? extends Label> getNodeKeys() {
        return Collections.emptySet();
    }

    @Override
    public @Nullable Label getKey(Edge edge) {
        return edge.label();
    }

    @Override
    public @Nullable String getNodeIdString() {
        return getNode().toString();
    }

    /** Returns an HTML description of the node, for the tool tip. */
    protected StringBuilder getNodeDescription() {
        StringBuilder result = new StringBuilder();
        result.append("Node");
        String id = getNodeIdString();
        if (id != null) {
            result.append(" ");
            result.append(ITALIC_TAG.on(id));
        }
        return result;
    }

    @Override
    public int getNumber() {
        return getNode().getNumber();
    }

    @Override
    public String toString() {
        return String
            .format("%s %d with labels %s", getClass().getSimpleName(), getNumber(), getKeys());
    }

    @Override
    public String getToolTipText() {
        return HTMLConverter.HTML_TAG.on(getNodeDescription()).toString();
    }
}
