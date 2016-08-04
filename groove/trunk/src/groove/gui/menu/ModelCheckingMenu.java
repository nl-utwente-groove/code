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
 * $Id$
 */
package groove.gui.menu;

import groove.explore.StrategyValue;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.gui.SimulatorListener;
import groove.gui.SimulatorModel;
import groove.gui.SimulatorModel.Change;
import groove.gui.action.SimulatorAction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * A menu for the model-checking actions.
 *
 * @author Iovka Boneva
 * @version $Revision $
 */
public class ModelCheckingMenu extends JMenu implements SimulatorListener {

    /**
     * Constructs an model-checking menu on top of a given simulator. The menu
     * will disable as soon as all states are closed.
     * @param simulator the associated simulator
     */
    public ModelCheckingMenu(Simulator simulator) {
        super(Options.VERIFY_MENU_NAME);
        this.simulator = simulator;
        createAddMenuItems();
        refreshActions(true);
        simulator.getModel().addListener(this, Change.GTS);
    }

    /**
     * Creates and adds the different menu items, corresponding to the different
     * exploration scenarios.
     */
    protected void createAddMenuItems() {
        addScenarioHandler(StrategyValue.LTL, Options.CHECK_LTL_ACTION_NAME);
        addScenarioHandler(StrategyValue.LTL_BOUNDED, Options.CHECK_LTL_BOUNDED_ACTION_NAME);
        addScenarioHandler(StrategyValue.LTL_POCKET, Options.CHECK_LTL_POCKET_ACTION_NAME);
    }

    /**
     * Adds an explication strategy action to the end of this menu.
     * @param strategyType the new exploration strategy
     */
    public void addScenarioHandler(StrategyValue strategyType, String name) {
        SimulatorAction generateAction =
            this.simulator.getActions().getCheckLTLAction(strategyType, name);
        generateAction.setEnabled(false);
        this.scenarioActionMap.put(strategyType, generateAction);
        JMenuItem menuItem = add(generateAction);
        menuItem.setToolTipText(strategyType.getDescription());
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel, Set<Change> changes) {
        if (changes.contains(Change.GTS)) {
            refreshActions(source.getGTS() != null);
        }
    }

    private void refreshActions(boolean enabled) {
        for (Action generateAction : this.scenarioActionMap.values()) {
            generateAction.setEnabled(enabled);
        }
    }

    /**
     * The simulator with which this menu is associated.
     */
    protected final Simulator simulator;
    /**
     * Mapping from exploration strategies to {@link Action}s resulting in that
     * strategy.
     */
    private final Map<StrategyValue,SimulatorAction> scenarioActionMap =
            new HashMap<>();
}
