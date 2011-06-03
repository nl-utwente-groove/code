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

import groove.gui.action.CancelEditGraphAction;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Superclass for grammar component editors.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class EditorPanel<D extends Display> extends JPanel {
    /** Creates a panel for a given display. */
    public EditorPanel(D parent) {
        final Simulator simulator = parent.getSimulator();
        this.display = parent;
        this.simulator = simulator;
    }

    /**
     * Returns the title of this panel. The title is the name plus an optional
     * indication of the (dirty) status of the editor.
     */
    public String getTitle() {
        return (isDirty() ? "*" : "") + getName();
    }

    /** Indicates if the editor has unsaved changes. */
    abstract public boolean isDirty();

    /** Sets the status of the editor to clean. */
    abstract public void setClean();

    /** Calls {@link CancelEditGraphAction#execute()}. */
    public boolean cancelEditing(boolean confirm) {
        boolean result = false;
        if (!confirm || confirmAbandon()) {
            dispose();
            result = true;
        }
        return result;
    }

    /** 
     * Returns the component to be used to fill the tab in a 
     * {@link JTabbedPane}, when this panel is displayed.
     */
    public final TabLabel getTabLabel() {
        if (this.tabComponent == null) {
            this.tabComponent = createTabComponent();
        }
        return this.tabComponent;
    }

    /**
     * Callback method to create a tab component for this panel,
     * in case it is used in a {@link JTabbedPane}.
     */
    protected TabLabel createTabComponent() {
        return new TabLabel(this, getDisplay().getKind().getEditIcon(),
            getTitle());
    }

    /**
     * Creates and shows a confirmation dialog for abandoning the currently
     * edited component.
     */
    public abstract boolean confirmAbandon();

    /** Disposes the editor, by removing it as a listener and simulator panel component. */
    public abstract void dispose();

    /** Returns the display on which this editor is placed. */
    protected final D getDisplay() {
        return this.display;
    }

    /** Convenience method to retrieve the simulator. */
    public final Simulator getSimulator() {
        return this.simulator;
    }

    /** Convenience method to retrieve the simulator model. */
    protected final SimulatorModel getSimulatorModel() {
        return getSimulator().getModel();
    }

    /** Container of this editor. */
    private final D display;
    /** The simulator to which the panel reports. */
    private final Simulator simulator;
    /** The component that constitutes the tab when this panel is used in a {@link JTabbedPane}. */
    private TabLabel tabComponent;
}
