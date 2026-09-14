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
package nl.utwente.groove.gui.jgraph;

import org.eclipse.jdt.annotation.NonNull;
import org.jgraph.graph.DefaultPort;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.view.cell.AViewEdge;
import nl.utwente.groove.gui.view.cell.AViewVertex;
import nl.utwente.groove.util.AIGenerated;

/**
 * JGraph cell showing a neutral edge cell. JGraph connects it through the ports
 * of the end vertices; each connection is mirrored on the view cell, so that the
 * neutral structure (end vertices and vertex contexts) follows JGraph's edits,
 * including undo, which reconnects through the same calls.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
public class JEdge<G extends @NonNull Graph> extends JCell<G> implements org.jgraph.graph.Edge {
    /** Creates the JGraph item of a given edge cell. */
    public JEdge(AViewEdge<G> cell) {
        super(cell);
    }

    @Override
    public AViewEdge<G> getViewCell() {
        return (AViewEdge<G>) super.getViewCell();
    }

    @Override
    public DefaultPort getSource() {
        return this.sourcePort;
    }

    @Override
    public DefaultPort getTarget() {
        return this.targetPort;
    }

    @Override
    public void setSource(Object port) {
        this.sourcePort = (DefaultPort) port;
        getViewCell().setSource(vertexOf(port));
    }

    @Override
    public void setTarget(Object port) {
        this.targetPort = (DefaultPort) port;
        getViewCell().setTarget(vertexOf(port));
    }

    /** Returns the vertex cell owning a given port, or {@code null} if the port is. */
    private AViewVertex<G> vertexOf(Object port) {
        if (port == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        JVertex<G> vertex = (JVertex<G>) ((DefaultPort) port).getParent();
        return vertex.getViewCell();
    }

    /** The source port; {@code null} while unconnected. */
    private DefaultPort sourcePort;
    /** The target port; {@code null} while unconnected. */
    private DefaultPort targetPort;

    /* The clone starts unconnected, as does its view cell. */
    @Override
    public Object clone() {
        JEdge<?> result = (JEdge<?>) super.clone();
        result.sourcePort = null;
        result.targetPort = null;
        return result;
    }
}
