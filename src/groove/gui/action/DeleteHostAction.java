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
package groove.gui.action;

import groove.gui.Icons;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.view.HostModel;
import groove.view.aspect.AspectGraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Action that takes care of deleting a host graph in a grammar view.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DeleteHostAction extends SimulatorAction {
    /** Constructs a new action for a given simulator. */
    public DeleteHostAction(Simulator simulator) {
        super(simulator, Options.DELETE_GRAPH_ACTION_NAME, Icons.DELETE_ICON);
        putValue(ACCELERATOR_KEY, Options.DELETE_KEY);
        getSimulator().addAccelerator(this);
    }

    @Override
    public void refresh() {
        setEnabled(getSimulatorModel().getStore() != null
            && getSimulatorModel().getStore().isModifiable()
            && !getSimulatorModel().getHostSet().isEmpty());
    }

    @Override
    public boolean execute() {
        boolean result = false;
        // Multiple selection
        // copy selected graph names
        Collection<HostModel> hostViews = getSimulatorModel().getHostSet();
        // first collect the affected graphs and compose the question
        List<String> graphNames = new ArrayList<String>();
        boolean deleteStartGraph = false;
        for (HostModel hostView : hostViews) {
            AspectGraph hostGraph = hostView.getSource();
            graphNames.add(hostGraph.getName());
            // compose the question
            deleteStartGraph |=
                hostView.equals(getSimulatorModel().getGrammar().getStartGraphModel());
        }
        String question;
        if (graphNames.size() == 1) {
            question =
                String.format("Delete %sgraph '%s'?", deleteStartGraph
                        ? "start " : "", graphNames.get(0));
        } else {
            question =
                String.format("Delete these %d graphs%s?", graphNames.size(),
                    deleteStartGraph ? " (including start graph)" : "");
        }
        if (confirmBehaviour(Options.DELETE_GRAPH_OPTION, question)
            && getStateDisplay().disposeEditors(
                graphNames.toArray(new String[0]))) {
            try {
                result |= getSimulatorModel().doDeleteHosts(graphNames);
            } catch (IOException exc) {
                showErrorDialog(exc, "Error during graph deletion");
            }
        }
        return result;
    }
}