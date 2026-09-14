/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.gui.view;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.graph.ControlGraph;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.layout.ForestLayouter;
import nl.utwente.groove.gui.layout.Layouter;

/**
 * Display controller for graph views showing control graphs.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class CtrlGraphViewController extends GraphViewController<ControlGraph> {
    /**
     * Constructs a controller.
     * @param simulator simulator to which the display belongs; may be {@code null}
     */
    public CtrlGraphViewController(@Nullable Simulator simulator) {
        super(simulator);
    }

    @Override
    protected CtrlGraphCanvas createCanvas(GraphBackend backend) {
        return backend.newCtrlCanvas(this);
    }

    /* Specialises the return type. */
    @Override
    public CtrlGraphCanvas getCanvas() {
        return (CtrlGraphCanvas) super.getCanvas();
    }

    @Override
    public GraphRole getGraphRole() {
        return GraphRole.CTRL;
    }

    /* Node identities are always shown in control graphs. */
    @Override
    public boolean isShowNodeIdentities() {
        return true;
    }

    /* Self-edges are always shown as node labels in control graphs. */
    @Override
    public boolean isShowLoopsAsNodeLabels() {
        return true;
    }

    /* Control graphs are laid out as a forest by default. */
    @Override
    public Layouter getDefaultLayouter() {
        return ForestLayouter.PROTOTYPE;
    }
}
