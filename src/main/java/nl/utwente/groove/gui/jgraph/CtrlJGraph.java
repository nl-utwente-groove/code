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

import nl.utwente.groove.control.graph.ControlGraph;
import nl.utwente.groove.control.template.Template;
import nl.utwente.groove.gui.view.CellStore;
import nl.utwente.groove.gui.view.CtrlGraphCanvas;
import nl.utwente.groove.gui.view.CtrlGraphViewController;
import nl.utwente.groove.gui.view.CtrlGraphViewModel;

/**
 * This is the JGraph representation of a ControlAutomaton.
 * @author Tom Staijen
 * @version $Revision$
 */
public class CtrlJGraph extends JGraph<@NonNull ControlGraph> implements CtrlGraphCanvas {
    /**
     * Creates a ControlJGraph given a ControlJModel
     * @param controller the controller of this canvas
     */
    public CtrlJGraph(CtrlGraphViewController controller) {
        super(controller);
        getGraphLayoutCache().setSelectsAllInsertedCells(false);
        setConnectable(false);
        setDisconnectable(false);
        setEnabled(true);
        setToolTipEnabled(true);
    }

    /** Creates a new model based on a given control automaton. */
    public void setModel(Template template) {
        var model = getModel();
        var graph = model == null
            ? null
            : model.getGraph();
        if (graph == null || graph.getTemplate() != template) {
            var newModel = newModel();
            newModel.loadGraph(template.toGraph(true));
            setModel(newModel);
        }
    }

    /* Specialises the return type. */
    @Override
    public CtrlGraphViewController getController() {
        return (CtrlGraphViewController) super.getController();
    }

    @Override
    CtrlGraphViewModel createViewModel(CellStore<@NonNull ControlGraph> store) {
        return new CtrlGraphViewModel(getController(), store);
    }
}
