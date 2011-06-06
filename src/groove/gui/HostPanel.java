/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
 * $Id: StatePanel.java,v 1.31 2008-02-05 13:28:05 rensink Exp $
 */
package groove.gui;

import static groove.gui.Options.SHOW_ASPECTS_OPTION;
import static groove.gui.Options.SHOW_NODE_IDS_OPTION;
import static groove.gui.Options.SHOW_REMARKS_OPTION;
import static groove.gui.Options.SHOW_UNFILTERED_EDGES_OPTION;
import static groove.gui.Options.SHOW_VALUE_NODES_OPTION;
import static groove.gui.SimulatorModel.Change.GRAMMAR;
import static groove.gui.SimulatorModel.Change.HOST;
import groove.graph.GraphRole;
import groove.graph.LabelStore;
import groove.graph.TypeLabel;
import groove.gui.GraphDisplay.GraphTab;
import groove.gui.SimulatorModel.Change;
import groove.gui.dialog.ErrorDialog;
import groove.io.HTMLConverter;
import groove.trans.SystemProperties;
import groove.view.FormatException;
import groove.view.GrammarModel;
import groove.view.TypeModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.SwingUtilities;

/**
 * Window that displays and controls the current state graph. Auxiliary class
 * for Simulator.
 * @author Arend Rensink
 * @version $Revision: 3419 $
 */
public class HostPanel extends GraphTab implements SimulatorListener {
    /** Display name of this panel. */
    public static final String FRAME_NAME = "Current state";

    // --------------------- INSTANCE DEFINITIONS ----------------------

    /**
     * Constructs a new state panel.
     */
    public HostPanel(final Simulator simulator) {
        super(simulator, GraphRole.HOST);
        initialise();
        setBorder(null);
        getJGraph().setToolTipEnabled(true);
    }

    @Override
    public void dispose() {
        super.dispose();
        suspendListeners();
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        this.simulatorListener = this;
        addRefreshListener(SHOW_NODE_IDS_OPTION);
        addRefreshListener(SHOW_ASPECTS_OPTION);
        addRefreshListener(SHOW_REMARKS_OPTION);
        addRefreshListener(SHOW_VALUE_NODES_OPTION);
        addRefreshListener(SHOW_UNFILTERED_EDGES_OPTION);
        getLabelTree().addLabelStoreObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                assert arg instanceof LabelStore;
                final SystemProperties newProperties =
                    getSimulatorModel().getGrammar().getProperties().clone();
                newProperties.setSubtypes(((LabelStore) arg).toDirectSubtypeString());
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getSimulator().getModel().doSetProperties(
                                newProperties);
                        } catch (IOException exc) {
                            new ErrorDialog(HostPanel.this,
                                "Error while modifying type hierarchy", exc).setVisible(true);
                        }
                    }
                });
            }
        });
        activateListeners();
    }

    /**
     * Activates all listeners.
     */
    private void activateListeners() {
        if (this.listening) {
            throw new IllegalStateException();
        }
        getSimulatorModel().addListener(this.simulatorListener, GRAMMAR, HOST);
        this.listening = true;
    }

    /**
     * Suspend all listening activity to avoid dependent updates.
     */
    private void suspendListeners() {
        if (!this.listening) {
            throw new IllegalStateException();
        }
        getSimulatorModel().removeListener(this.simulatorListener);
        this.listening = false;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        suspendListeners();
        //        if (changes.contains(GRAMMAR)) {
        //            setGrammar(source.getGrammar());
        //        }
        //        refreshStatus();
        activateListeners();
    }

    /**
     * Sets the underlying model of this state frame to the initial graph of the
     * new grammar.
     */
    private void setGrammar(GrammarModel grammar) {
        getJGraph().getFilteredLabels().clear();
        if (grammar == null) {
            setJModel(null);
            getJGraph().setType(null, null);
        } else {
            // set the type or the label store for the JGraph
            Map<String,Set<TypeLabel>> labelsMap =
                new HashMap<String,Set<TypeLabel>>();
            try {
                for (String typeName : grammar.getTypeNames()) {
                    TypeModel view = grammar.getTypeModel(typeName);
                    // the view may be null if type names
                    // overlap modulo upper/lowercase
                    if (view != null && view.isEnabled()) {
                        labelsMap.put(typeName, view.getLabels());
                    }
                }
                getJGraph().setType(grammar.getTypeModel().toResource(),
                    labelsMap);
            } catch (FormatException e) {
                labelsMap.clear();
            }
            if (labelsMap.isEmpty()) {
                getJGraph().setLabelStore(grammar.getLabelStore());
            }
        }
        refreshStatus();
    }

    /**
     * Text to indicate which state is chosen and which match is emphasised.
     */
    @Override
    protected String getStatusText() {
        StringBuilder result = new StringBuilder();
        if (getGraph() != null) {
            result.append("Graph: ");
            result.append(HTMLConverter.STRONG_TAG.on(getGraph().getName()));
        }
        return HTMLConverter.HTML_TAG.on(result).toString();
    }

    /** Flag indicating that the listeners are activated. */
    private boolean listening;
    private SimulatorListener simulatorListener;

}