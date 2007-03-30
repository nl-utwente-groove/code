// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * $Id: ExploreStrategyMenu.java,v 1.2 2007-03-30 15:50:35 rensink Exp $
 */
package groove.gui;

import java.util.HashMap;
import java.util.Map;

import groove.graph.GraphShape;
import groove.graph.Node;
import groove.lts.ConditionalExploreStrategy;
import groove.lts.ExploreStrategy;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.LTS;
import groove.lts.LTSAdapter;
import groove.lts.LTSListener;
import groove.lts.State;
import groove.lts.explore.BoundedStrategy;
import groove.lts.explore.BranchingStrategy;
import groove.lts.explore.LinearStrategy;
import groove.lts.explore.FullStrategy;
import groove.lts.explore.BarbedStrategy;
import groove.trans.NameLabel;

import javax.swing.Action;
import javax.swing.JMenu;

/**
 * 
 * @author Arend Rensink
 * @version $Revision: 1.2 $
 */
public class ExploreStrategyMenu extends JMenu implements SimulationListener {
    /**
     * Constructs an exploration menu on top of a given simulator.
     * The menu will disable as soon as all states are closed.
     * @param simulator the associated simulator
     */
    public ExploreStrategyMenu(Simulator simulator) {
        this(simulator, true);
    }
    
    /**
     * Constructs an exploration menu on top of a given simulator.
     * The menu will optionally disable as soon as all states are closed.
     * @param simulator the associated simulator
     * @param disableOnFinish <tt>true</tt> if the menu is to be disabled when
     * the last state is closed
     */
    public ExploreStrategyMenu(Simulator simulator, boolean disableOnFinish) {
        super(Options.EXPLORE_MENU_NAME);
        this.simulator = simulator;
        this.disableOnFinish = disableOnFinish;
        simulator.addSimulationListener(this);
        addExploreStrategy(new FullStrategy());
        addExploreStrategy(new BranchingStrategy());
        addExploreStrategy(new BarbedStrategy());
        addExploreStrategy(new LinearStrategy());
        addExploreStrategy(new BoundedStrategy());
        addExploreStrategy(new BoundedStrategy(true));
    }

    /**
     * Adds an explication strategy action to the end of this menu.
     * @param strategy the new exploration strategy
     */
    public void addExploreStrategy(ExploreStrategy strategy) {
        Action generateAction = simulator.new GenerateLTSAction(strategy);
        generateAction.setEnabled(false);
        strategyActionMap.put(strategy, generateAction);
        add(generateAction);
    }

    // ----------------------------- simulation listener methods -----------------------
    public void setGrammarUpdate(GTS gts) {
        if (isLTSLoaded()) {
        	gts.removeGraphListener(ltsListener);
        }
        currentGTS = gts;
        // the initial state is open
        openStateCount = 1;
        if (currentGTS != null) {
        	currentGTS.addGraphListener(ltsListener);
            // the lts's of the strategies in this menu are changed
            // moreover, the conditions in condition strategies are reset
            // furthermore, the enabling is (re)set
            for (Map.Entry<ExploreStrategy,Action> entry: strategyActionMap.entrySet()) {
                ExploreStrategy strategy = entry.getKey();
                strategy.setLTS(currentGTS);
                Action generateAction = entry.getValue();
                if (strategy instanceof ConditionalExploreStrategy) {
                    ((ConditionalExploreStrategy) strategy).setCondition(null);
                    generateAction.putValue(Action.NAME, strategy.toString());
                    generateAction.setEnabled(false);
                } else {
                    generateAction.setEnabled(!isLTSExplored());
                }
            }
        }
        setStateUpdate(!isLTSLoaded() ? null : (GraphState) currentGTS.startState());
    }

    public void setStateUpdate(GraphState state) {
        for (Map.Entry<ExploreStrategy,Action> entry: strategyActionMap.entrySet()) {
            ExploreStrategy strategy = entry.getKey();
            Action generateAction = entry.getValue();
            strategy.setAtState(state);
            generateAction.putValue(Action.NAME, strategy.toString());
        }
        this.setEnabled(!isLTSExplored());
    }

    public void setRuleUpdate(NameLabel name) {
        for (Map.Entry<ExploreStrategy,Action> entry: strategyActionMap.entrySet()) {
            ExploreStrategy strategy = entry.getKey();
            if (strategy instanceof ConditionalExploreStrategy) {
                Action generateAction = entry.getValue();
                ((ConditionalExploreStrategy) strategy).setCondition(currentGTS.ruleSystem().getRule(name));
                generateAction.putValue(Action.NAME, strategy.toString());
                generateAction.setEnabled(true);
            }
        }
        this.setEnabled(!isLTSExplored());
    }

    public void setTransitionUpdate(GraphTransition transition) {
        setStateUpdate(transition.source());
    }

    public void applyTransitionUpdate(GraphTransition transition) {
        setStateUpdate(transition.target());
    }

    /**
     * Indicates if an LTS is currently loaded.
     * This may fail to be the case if no grammar was loaded, or the loaded grammar
     * has no start state.
     */
    protected boolean isLTSLoaded() {
        return currentGTS != null;
    }

    /**
     * Indicates if the currently loaded LTS is fully explored.
     * Also returns <code>true</code> if there is no LTS loaded.
     */
    protected boolean isLTSExplored() {
        return isLTSLoaded() && openStateCount == 0;
    }
    
    /**
     * The simulator with which this menu is associated.
     */
    private final Simulator simulator;
    /**
     * Indicates if the menu should be disable after tha last LTS state has closed.
     */
    private final boolean disableOnFinish;
    /** Mapping from exploratin strategies to {@link Action}s resulting in that strategy. */
    private final Map<ExploreStrategy,Action> strategyActionMap = new HashMap<ExploreStrategy,Action>();
    /** The currently loaded graph grammar. */
    private GTS currentGTS;
//    /** The transition system of the currently loaded grammar; may be <code>null</code>. */
//    private LTS currentLTS;
    /** The number of open states of the currently loaded LTS (if any). */
    private int openStateCount;
    /** The (permanent) LTS listener associated with this menu. */
    private final LTSListener ltsListener = new LTSAdapter() {
        @Override
        public void closeUpdate(LTS graph, State explored) {
            assert graph == currentGTS;
            openStateCount--;
            setEnabled(!disableOnFinish || !isLTSExplored());
        }

        /** If the added element is a state, increases the open state count. */
        @Override
        public void addUpdate(GraphShape graph, Node node) {
        	openStateCount++;
        }
    };
}
