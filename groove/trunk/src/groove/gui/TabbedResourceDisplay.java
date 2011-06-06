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
package groove.gui;

import groove.trans.ResourceKind;

import java.awt.Component;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Resource display class that includes a tabbed pane,
 * with a single main tab for switching quickly between resources that are merely displayed,
 * and multiple tabs for pinned editors.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class TabbedResourceDisplay extends ResourceDisplay {
    /**
     * Constructs a display, for a given simulator and resource kind.
     */
    public TabbedResourceDisplay(Simulator simulator, ResourceKind resource) {
        super(simulator, resource);
    }

    /**
     * Returns the panel holding all display tabs.
     * This may or may not be the same as #getDisplayPanel().
     */
    abstract protected JTabbedPane getTabPane();

    /** Callback to obtain the main tab of this display. */
    abstract public Tab getMainTab();

    /**
     * Initialises all listening activity on the display, and 
     * calls {@link #activateListening()}.
     */
    protected void installListeners() {
        getTabPane().addChangeListener(getTabListener());
        activateListening();
    }

    /** 
     * Returns a change listener that informs the simulator model
     * of selection changes on this display.
     */
    private ChangeListener getTabListener() {
        if (this.tabListener == null) {
            this.tabListener = new ChangeListener() {
                public void stateChanged(ChangeEvent evt) {
                    if (suspendListening()) {
                        selectionChanged();
                        activateListening();
                    }
                    getListPanel().repaint();
                }
            };
        }
        return this.tabListener;
    }

    /** Tests if listening is currently activated. */
    protected boolean isListening() {
        return this.listening;
    }

    /** 
     * Tests and sets the listening flag.
     * This should be used by all listeners to test if they are
     * supposed to be active, before they take any actions that can cause circular
     * dependencies.
     * 
     * @return {@code true} if the listening flag was on
     * before the call.
     */
    protected boolean suspendListening() {
        boolean result = this.listening;
        if (result) {
            this.listening = false;
        }
        return result;
    }

    /** 
     * Sets the listening flag to true.
     * This means all listeners to events on this display become active.
     */
    protected void activateListening() {
        if (this.listening) {
            throw new IllegalStateException();
        }
        this.listening = true;
    }

    /** Callback method that is invoked when the tab selection has changed. */
    abstract protected void selectionChanged();

    /** Flag indicating that the listeners are currently active. */
    private boolean listening;
    /** Listener that forwards tab changes to the simulator model. */
    private ChangeListener tabListener;

    /** Interface for tabs on this display. */
    protected static interface Tab {
        /** Changes this tab so as to display a given, named resource. */
        public void setResource(String name);

        /** Returns the name of the resource currently displayed on this tab. */
        public String getName();

        /** Returns the tab label component to be used for this tab. */
        public TabLabel getTabLabel();

        /**
         * Returns the actual component of this tab.
         * This is typically {@code this}.
         */
        public Component getComponent();

        /** Method to repaint the tab. */
        public void repaint();
    }

    /** Interface for editor tabs on this display. */
    protected static interface EditorTab extends Tab {

    }
}
