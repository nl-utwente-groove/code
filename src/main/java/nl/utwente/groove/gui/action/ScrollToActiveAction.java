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
package nl.utwente.groove.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import nl.utwente.groove.gui.Options;
import nl.utwente.groove.gui.display.LTSGraphViewController;
import nl.utwente.groove.lts.GraphState;
import nl.utwente.groove.lts.GraphTransition;

/**
 * Action to scroll the LTS display to a (previously set) node or edge.
 * @see LTSGraphViewController#scrollToActive()
 * @author Arend Rensink
 * @version $Revision$
 */
public class ScrollToActiveAction extends AbstractAction {
    /** Constructs an instance of the action. */
    public ScrollToActiveAction(LTSGraphViewController controller) {
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        this.controller.scrollToActive();
    }

    /**
     * Adapts the name of the action so that it reflects that the element to
     * scroll to is a given transition.
     */
    public void setTransition(GraphTransition edge) {
        putValue(Action.NAME, Options.SCROLL_TO_ACTION_NAME + " transition");
    }

    /**
     * Adapts the name of the action so that it reflects that the element to
     * scroll to is a given state.
     */
    public void setState(GraphState node) {
        putValue(Action.NAME, Options.SCROLL_TO_ACTION_NAME + " state");
    }

    /** The graph-view controller on which this action works. */
    private final LTSGraphViewController controller;
}
