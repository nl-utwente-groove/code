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
package groove.gui;

import groove.explore.ModelCheckingScenario;
import groove.explore.Scenario;
import groove.explore.strategy.BoundedNestedDFSPocketStrategy;
import groove.explore.strategy.BoundedNestedDFSStrategy;
import groove.explore.strategy.NestedDFSStrategy;
import groove.explore.strategy.OptimizedBoundedNestedDFSPocketStrategy;
import groove.explore.strategy.OptimizedBoundedNestedDFSStrategy;
import groove.gui.SimulatorModel.Change;
import groove.lts.GTS;
import groove.lts.GTSAdapter;
import groove.lts.GraphState;

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
        this(simulator, true);
    }

    /**
     * Constructs a model-checking menu on top of a given simulator. The menu
     * will optionally disable as soon as all states are closed.
     * @param simulator the associated simulator
     * @param disableOnFinish <tt>true</tt> if the menu is to be disabled when
     *        the last state is closed
     */
    public ModelCheckingMenu(Simulator simulator, boolean disableOnFinish) {
        super(Options.VERIFY_MENU_NAME);
        this.simulator = simulator;
        this.disableOnFinish = disableOnFinish;
        simulator.addSimulatorListener(this);

        createAddMenuItems();
    }

    /**
     * Creates and adds the different menu items, corresponding to the different
     * exploration scenarios.
     */
    protected void createAddMenuItems() {
        Scenario scenario =
            new ModelCheckingScenario(new NestedDFSStrategy(),
                Options.CHECK_LTL_ACTION_NAME);
        addScenarioHandler(scenario);

        scenario =
            new ModelCheckingScenario(new BoundedNestedDFSStrategy(),
                Options.CHECK_LTL_BOUNDED_ACTION_NAME);
        addScenarioHandler(scenario);

        scenario =
            new ModelCheckingScenario(new BoundedNestedDFSPocketStrategy(),
                Options.CHECK_LTL_POCKET_ACTION_NAME);
        addScenarioHandler(scenario);

        scenario =
            new ModelCheckingScenario(new OptimizedBoundedNestedDFSStrategy(),
                Options.CHECK_LTL_OPTIMIZED_ACTION_NAME);
        addScenarioHandler(scenario);

        scenario =
            new ModelCheckingScenario(
                new OptimizedBoundedNestedDFSPocketStrategy(),
                Options.CHECK_LTL_OPTMIZED_POCKET_ACTION_NAME);
        addScenarioHandler(scenario);
    }

    /**
     * Adds an explication strategy action to the end of this menu.
     * @param scenario the new exploration strategy
     */
    public void addScenarioHandler(Scenario scenario) {
        Action generateAction =
            this.simulator.getLaunchScenarioAction(scenario);
        generateAction.setEnabled(false);
        this.scenarioActionMap.put(scenario, generateAction);
        JMenuItem menuItem = add(generateAction);
        menuItem.setToolTipText(scenario.getName());
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        if (changes.contains(Change.GTS)) {
            GTS newGts = source.getGts();
            this.gtsListener.set(newGts);
            // the lts's of the strategies in this menu are changed
            // moreover, the conditions in condition strategies are reset
            // furthermore, the enabling is (re)set
            for (Map.Entry<Scenario,Action> entry : this.scenarioActionMap.entrySet()) {
                Action generateAction = entry.getValue();
                generateAction.setEnabled(newGts != null);
            }
        }
        if (changes.contains(Change.STATE) && source.getState() != null) {
            for (Map.Entry<Scenario,Action> entry : this.scenarioActionMap.entrySet()) {
                Scenario scenario = entry.getKey();
                Action generateAction = entry.getValue();
                generateAction.putValue(Action.NAME, scenario.getName());
            }
        }
    }

    /**
     * @return Returns the disableOnFinish.
     */
    final boolean isDisableOnFinish() {
        return this.disableOnFinish;
    }

    /**
     * The simulator with which this menu is associated.
     */
    protected final Simulator simulator;
    /**
     * Indicates if the menu should be disable after the last LTS state has
     * closed.
     */
    protected final boolean disableOnFinish;
    /**
     * Mapping from exploration strategies to {@link Action}s resulting in that
     * strategy.
     */
    private final Map<Scenario,Action> scenarioActionMap =
        new HashMap<Scenario,Action>();
    /** The (permanent) GTS listener associated with this menu. */
    private final GTSListener gtsListener = new GTSListener();

    /** Listener that can be refreshed with the current GTS. */
    private class GTSListener extends GTSAdapter {
        /** Empty constructor with the correct visibility. */
        GTSListener() {
            // empty
        }

        /** Sets the GTS to listen to. */
        public void set(GTS newGTS) {
            if (this.gts != null) {
                this.gts.removeLTSListener(this);
            }
            this.gts = newGTS;
            if (this.gts != null) {
                this.gts.addLTSListener(this);
                this.openStateCount = this.gts.openStateCount();
                setEnabled(true);
            }
        }

        @Override
        public void closeUpdate(GTS lts, GraphState explored) {
            assert lts == this.gts;
            this.openStateCount--;
            assert this.openStateCount == this.gts.openStateCount();
            if (this.openStateCount == 0 && !isDisableOnFinish()) {
                setEnabled(false);
            }
        }

        /** If the added element is a state, increases the open state count. */
        @Override
        public void addUpdate(GTS gts, GraphState state) {
            this.openStateCount++;
        }

        /** The GTS this listener currently listens to. */
        private GTS gts;
        /** The number of open states of the currently loaded LTS (if any). */
        private int openStateCount;
    }
}
