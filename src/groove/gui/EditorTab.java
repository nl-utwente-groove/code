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

import groove.gui.ResourceDisplay.Tab;
import groove.gui.action.CancelEditAction;
import groove.gui.action.SaveAction;
import groove.trans.ResourceKind;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

/**
 * Superclass for grammar component editors.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class EditorTab extends JPanel implements Tab {
    /** Creates a panel for a given display. */
    public EditorTab(ResourceDisplay display) {
        final Simulator simulator = display.getSimulator();
        this.display = display;
        this.resourceKind = display.getResourceKind();
        this.simulator = simulator;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public Icon getIcon() {
        return Icons.getEditorTabIcon(getDisplay().getKind().getResource());
    }

    /**
     * Returns the title of this panel. The title is the name plus an optional
     * indication of the (dirty) status of the editor.
     */
    @Override
    public String getTitle() {
        return (isDirty() ? "*" : "") + getName();
    }

    /** Returns the resource kind of this editor tab. */
    final public ResourceKind getResourceKind() {
        return this.resourceKind;
    }

    @Override
    public boolean isEditor() {
        return true;
    }

    /**
     * Creates a tool bar for this editor tab.
     * This implementation only adds a save and cancel button; this
     * can be extended by subclasses.
     */
    protected JToolBar createToolBar() {
        JToolBar result = Options.createToolBar();
        result.add(createSaveButton());
        result.add(createCancelButton());
        return result;
    }

    /** Indicates if the editor has unsaved changes. */
    abstract public boolean isDirty();

    /** Sets the status of the editor to clean. */
    abstract public void setClean();

    /** Calls {@link CancelEditAction#execute()}. */
    public boolean cancelEditing(boolean confirm) {
        boolean result = false;
        if (!confirm || confirmCancel()) {
            dispose();
            result = true;
        }
        return result;
    }

    /**
     * Creates and shows a confirmation dialog for abandoning the currently
     * edited graph.
     */
    public boolean confirmCancel() {
        boolean result = true;
        if (isDirty()) {
            int answer =
                JOptionPane.showConfirmDialog(this, String.format(
                    "%s '%s' has been modified. Save changes?",
                    getResourceKind().getName(), getName()), null,
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                saveResource();
            }
            result = answer != JOptionPane.CANCEL_OPTION;
        }
        return result;
    }

    /** 
     * Returns the component to be used to fill the tab in a 
     * {@link JTabbedPane}, when this panel is displayed.
     */
    public final TabLabel getTabLabel() {
        if (this.tabLabel == null) {
            this.tabLabel = createTabLabel();
        }
        return this.tabLabel;
    }

    /**
     * Callback method to create a tab component for this panel,
     * in case it is used in a {@link JTabbedPane}.
     */
    protected TabLabel createTabLabel() {
        return new TabLabel(this, getIcon(), getName());
    }

    /**
     * Callback method to notify the editor of a change in dirty status.
     */
    protected void updateDirty() {
        getTabLabel().setTitle(this.display.getLabelText(getName()));
        getSaveAction().refresh();
        this.display.getListPanel().repaint();
    }

    /** Saves the resource that is currently being edited. */
    abstract protected void saveResource();

    /** Disposes the editor, by removing it as a listener and simulator panel component. */
    public abstract void dispose();

    /** Returns the display on which this editor is placed. */
    protected final ResourceDisplay getDisplay() {
        return this.display;
    }

    /** Indicates if the edited resource is currently in an error state. */
    abstract protected boolean hasErrors();

    /** Creates and returns a Cancel button, for use on the tool bar. */
    private JButton createCancelButton() {
        return Options.createButton(getCancelAction());
    }

    /** Creates and returns an OK button, for use on the tool bar. */
    private JButton createSaveButton() {
        return Options.createButton(getSaveAction());
    }

    /** Creates and returns the cancel action. */
    protected final CancelEditAction getCancelAction() {
        return getSimulator().getActions().getCancelEditAction(
            getResourceKind());
    }

    /** Returns the save action of this editor. */
    protected final SaveAction getSaveAction() {
        return getSimulator().getActions().getSaveAction(getResourceKind());
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
    private final ResourceDisplay display;
    /** The resource kind of this editor tab. */
    private final ResourceKind resourceKind;
    /** The simulator to which the panel reports. */
    private final Simulator simulator;
    /** The component that constitutes the tab when this panel is used in a {@link JTabbedPane}. */
    private TabLabel tabLabel;
}
