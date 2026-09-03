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
package nl.utwente.groove.gui.display;

import java.awt.Point;

import javax.swing.Action;
import javax.swing.JMenu;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.gui.Simulator;
import nl.utwente.groove.gui.action.ScrollToActiveAction;
import nl.utwente.groove.gui.jgraph.JGraphMode;
import nl.utwente.groove.gui.jgraph.LTSJGraph;
import nl.utwente.groove.gui.menu.ModelCheckingMenu;
import nl.utwente.groove.gui.menu.MyJMenu;
import nl.utwente.groove.lts.GTS;

/**
 * Display controller for graph views showing the LTS.
 * Adds the exploration and traversal menus.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class LTSGraphViewController extends GraphViewController<GTS> {
    /**
     * Constructs a controller for a given graph-view component.
     * @param graphView the graph-view component that this controller belongs to
     * @param simulator simulator to which the display belongs; may be {@code null}
     */
    public LTSGraphViewController(LTSJGraph graphView, @Nullable Simulator simulator) {
        super(graphView, simulator);
    }

    /* Specialises the return type. */
    @Override
    public LTSJGraph getGraphView() {
        return (LTSJGraph) super.getGraphView();
    }

    /*
     * This implementation adds actions to move to different states within the
     * LTS, to apply the current transition and to explore the LTS, and
     * subsequently invokes the super implementation.
     */
    @Override
    public JMenu createPopupMenu(@Nullable Point atPoint) {
        MyJMenu result = new MyJMenu("Popup");
        if (getGraphView().getMode() == JGraphMode.SELECT_MODE) {
            result.addSubmenu(createExploreMenu());
            result.addSubmenu(createGotoMenu());
            result.addSubmenu(super.createPopupMenu(atPoint));
        } else {
            result.addSubmenu(createGotoMenu());
            result.addSubmenu(createShowHideMenu());
            result.addSubmenu(createZoomMenu());
        }
        return result;
    }

    @Override
    public JMenu createExportMenu() {
        MyJMenu result = new MyJMenu();
        var actions = getActions();
        assert actions != null; // the LTS view only exists with a simulator present
        result.add(actions.getSaveLTSAsAction());
        result.add(actions.getSaveStateAction());
        result.addMenuItems(super.createExportMenu());
        return result;
    }

    /** Creates a state exploration sub-menu. */
    public JMenu createExploreMenu() {
        JMenu result = new JMenu("Explore");
        var actions = getActions();
        assert actions != null; // the LTS view only exists with a simulator present
        result.add(actions.getExplorationDialogAction());
        result.add(actions.getApplyMatchAction());
        result.add(actions.getExploreAction());
        result.addSeparator();
        result.add(getCheckerMenu());
        return result;
    }

    /** Creates a traversal sub-menu. */
    public JMenu createGotoMenu() {
        JMenu result = new JMenu("Go To");
        var actions = getActions();
        assert actions != null; // the LTS view only exists with a simulator present
        result.add(actions.getGotoStartStateAction());
        result.add(actions.getGotoFinalStateAction());
        result.add(getScrollToActiveAction());
        return result;
    }

    /**
     * Lazily creates and returns the model-checking menu.
     */
    private JMenu getCheckerMenu() {
        var result = this.checkerMenu;
        if (result == null) {
            var simulator = getSimulator();
            assert simulator != null; // the LTS view only exists with a simulator present
            this.checkerMenu = result = new ModelCheckingMenu(simulator);
        }
        return result;
    }

    /** The lazily created model-checking menu. */
    private @Nullable JMenu checkerMenu;

    /** Initialises and returns the action to scroll to the active state or transition. */
    private Action getScrollToActiveAction() {
        if (getGraphView().getActiveTransition() == null) {
            this.scrollToActiveAction.setState(getGraphView().getActiveState());
        } else {
            this.scrollToActiveAction.setTransition(getGraphView().getActiveTransition());
        }
        return this.scrollToActiveAction;
    }

    /**
     * Action to scroll the graph view to the current state or derivation.
     */
    private final ScrollToActiveAction scrollToActiveAction
        = new ScrollToActiveAction(getGraphView());
}
