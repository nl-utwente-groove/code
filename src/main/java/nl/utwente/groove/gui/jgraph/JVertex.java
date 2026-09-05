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
import nl.utwente.groove.gui.view.cell.AViewVertex;
import nl.utwente.groove.util.AIGenerated;

/**
 * JGraph cell showing a neutral vertex cell. Carries the port through which
 * JGraph connects edges to it.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
public class JVertex<G extends @NonNull Graph> extends JCell<G> {
    /** Creates the JGraph item of a given vertex cell. */
    public JVertex(AViewVertex<G> cell) {
        super(cell);
        add(new DefaultPort());
    }

    @Override
    public AViewVertex<G> getViewCell() {
        return (AViewVertex<G>) super.getViewCell();
    }

    /** Returns the port of this vertex. */
    public DefaultPort getPort() {
        DefaultPort result = null;
        for (Object child : getChildren()) {
            if (child instanceof DefaultPort port) {
                result = port;
                break;
            }
        }
        assert result != null : "Vertex " + this + " has no port";
        return result;
    }
}
