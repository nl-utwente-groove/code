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

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.control.graph.ControlGraph;
import nl.utwente.groove.control.graph.ControlNode;
import nl.utwente.groove.gui.look.Look;
import nl.utwente.groove.gui.view.CtrlViewVertex;
import nl.utwente.groove.gui.view.GraphViewModel;

/**
 * Vertex cell of a control graph, wrapping a control node.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class CtrlVertexCell extends AViewVertex<ControlGraph> implements CtrlViewVertex {
    /** Constructs a vertex cell for a given view model. */
    public CtrlVertexCell(GraphViewModel<ControlGraph> viewModel) {
        super(viewModel);
    }

    @Override
    public void initialise() {
        super.initialise();
        if (isFinal()) {
            setLook(Look.FINAL, true);
        }
    }

    @Override
    public ControlNode getNode() {
        return (ControlNode) super.getNode();
    }

    /** Indicates if this is the start node of the control graph. */
    public boolean isStart() {
        return getNode().getNumber() == 0;
    }

    /** Indicates if this is a final node of the control graph. */
    public boolean isFinal() {
        return getNode().getPosition().isFinal();
    }

    /** Indicates if this is a transient node of the control graph. */
    public boolean isTransient() {
        return getNode().getPosition().getTransience() > 0;
    }

    @Override
    protected Set<Look> getStructuralLooks() {
        if (isStart()) {
            return EnumSet.of(Look.START);
        } else if (isTransient()) {
            return EnumSet.of(Look.CTRL_TRANSIENT_STATE);
        } else {
            return EnumSet.of(Look.STATE);
        }
    }
}
