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
package nl.utwente.groove.gui.action;

import java.awt.Color;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JColorChooser;
import javax.swing.JDialog;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.grammar.aspect.Aspect;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.view.AspectViewVertex;
import nl.utwente.groove.gui.view.GraphCanvas;
import nl.utwente.groove.gui.view.GraphCanvasListener;

/**
 * Action for selecting a colour for a type node.
 */
@NonNullByDefault
public class SelectColorAction extends SimulatorAction
    implements GraphCanvasListener<AspectGraph> {
    /** Constructs an instance of the action. */
    public SelectColorAction(Simulator simulator) {
        super(simulator, Options.SELECT_COLOR_ACTION_NAME, null);
        putValue(SHORT_DESCRIPTION, Options.SELECT_COLOR_ACTION_NAME);
        refresh();
        this.chooser = new JColorChooser();
    }

    /** Checks if in a given canvas a type label is selected. */
    private void checkCanvas(GraphCanvas<AspectGraph> canvas) {
        this.graph = canvas.getGraph();
        this.nodes.clear();
        // find the relevant nodes
        canvas
            .getSelection()
            .stream()
            .filter(c -> c instanceof AspectViewVertex)
            .map(v -> ((AspectViewVertex) v).getNode())
            .forEach(this.nodes::add);
        refresh();
    }

    @Override
    public void execute() {
        var initColour = this.nodes.stream().findFirst().get().getColor();
        if (initColour != null) {
            this.chooser.setColor(initColour);
        }
        JDialog dialog = JColorChooser
            .createDialog(getFrame(), "Choose colour for type", false, this.chooser,
                          e -> setColour(SelectColorAction.this.chooser.getColor()), null);
        dialog.setVisible(true);
    }

    private void setColour(Color newColour) {
        Aspect colourAspect = null;
        if (!newColour.equals(Color.black)) {
            colourAspect = AspectKind.COLOR.newAspect(newColour);
        }
        var hostGraph = this.graph;
        assert hostGraph != null;
        var newHostGraph = hostGraph.colour(this.nodes, colourAspect);
        if (newHostGraph != hostGraph) {
            try {
                getSimulatorModel()
                    .doAddGraph(ResourceKind.toResource(hostGraph.getRole()), newHostGraph, false);
            } catch (IOException exc) {
                showErrorDialog(exc, String
                    .format("Error while saving host graph '%s'", hostGraph.getName()));
            }
        }
    }

    /** Sets {@link #nodes} based on the canvas selection. */
    @Override
    public void selectionChanged(GraphCanvas<AspectGraph> canvas) {
        checkCanvas(canvas);
    }

    @Override
    public void refresh() {
        super.setEnabled(!this.nodes.isEmpty());
    }

    /** The graph to be changed. */
    private @Nullable AspectGraph graph;
    /** The selected nodes to be changed */
    private final Set<AspectNode> nodes = new HashSet<>();

    private final JColorChooser chooser;
}
