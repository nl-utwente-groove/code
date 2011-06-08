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

import groove.gui.action.ActionStore;
import groove.gui.action.CancelEditAction;
import groove.gui.action.CopyAction;
import groove.gui.action.SaveAction;
import groove.gui.action.SimulatorAction;
import groove.trans.ResourceKind;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;

/**
 * Display component for a grammar resource.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class ResourceDisplay implements Display {
    /**
     * Constructs an instance for a given simulator and display kind.
     * The display kind should have an associated resource kind.
     */
    protected ResourceDisplay(Simulator simulator, ResourceKind resource) {
        this.simulator = simulator;
        this.kind = DisplayKind.toDisplay(resource);
        this.resource = resource;
        assert this.resource != null;
    }

    @Override
    public DisplayKind getKind() {
        return this.kind;
    }

    @Override
    public Simulator getSimulator() {
        return this.simulator;
    }

    @Override
    public String getTitle() {
        if (getSimulatorModel().isSelected(getResourceKind())) {
            return getSimulatorModel().getSelected(getResourceKind());
        } else {
            return null;
        }
    }

    /** Creates an editor for the resource with given name. */
    public abstract void startEditResource(String name);

    /** Attempts to cancel an edit action for a given named resource.
     * @param name the name of the editor to be cancelled
     * @param confirm if {@code true}, the user should explicitly confirm
     * @return {@code true} if the editing was cancelled
     */
    abstract public boolean cancelEditResource(String name, boolean confirm);

    /**
     * Attempts to close all editors on this display, asking permission 
     * for the dirty ones and possibly saving them.
     * This is done in preparation to changing the grammar.
     * @return {@code true} if all editors were disposed.
     */
    abstract public boolean cancelAllEdits();

    /** Returns the GUI component showing the list of control program names. */
    public JPanel getListPanel() {
        if (this.listPanel == null) {
            JScrollPane controlPane = new JScrollPane(getList()) {
                @Override
                public Dimension getPreferredSize() {
                    Dimension superSize = super.getPreferredSize();
                    return new Dimension((int) superSize.getWidth(),
                        Simulator.START_LIST_MINIMUM_HEIGHT);
                }
            };

            this.listPanel = new JPanel(new BorderLayout(), false);
            this.listPanel.add(getListToolBar(), BorderLayout.NORTH);
            this.listPanel.add(controlPane, BorderLayout.CENTER);
            // make sure tool tips get displayed
            ToolTipManager.sharedInstance().registerComponent(this.listPanel);
        }
        return this.listPanel;
    }

    /** Resets the list panel to {@code null}, so that the next invocation
     * of {@link #getListPanel()} creates a fresh panel.
     */
    protected void resetListPanel() {
        this.listPanel = null;
    }

    /** Returns the label list for this display. */
    abstract protected JComponent getList();

    /** Creates and returns the fixed tool bar for the label list. */
    final protected JToolBar getListToolBar() {
        if (this.listToolBar == null) {
            this.listToolBar = createListToolBar();
        }
        return this.listToolBar;
    }

    /** 
     * Creates a popup menu for the label list.
     * @param overResource flag indicating that the mouse is over a 
     * resource in the label list. This displays more items 
     */
    protected JPopupMenu createListPopupMenu(boolean overResource) {
        JPopupMenu res = new JPopupMenu();
        res.setFocusable(false);
        res.add(getNewAction());
        if (overResource) {
            res.add(getEditAction());
            res.addSeparator();
            res.add(getCopyAction());
            res.add(getDeleteAction());
            res.add(getRenameAction());
            if (getResourceKind() != ResourceKind.PROLOG) {
                res.addSeparator();
                res.add(getEnableAction());
            }
        }
        return res;
    }

    /** 
     * Callback method to creates a tool bar for the list panel.
     */
    protected JToolBar createListToolBar() {
        return createListToolBar(-1);
    }

    /** 
     * Creates a tool bar for the list panel.
     * @param separation width of the separator on the tool bar;
     * if negative, the default separator is used
     */
    protected JToolBar createListToolBar(int separation) {
        JToolBar result = Options.createToolBar();
        result.add(getNewAction());
        result.add(getEditAction());
        if (separation >= 0) {
            result.addSeparator(new Dimension(separation, 0));
        } else {
            result.addSeparator();
        }
        result.add(getCopyAction());
        result.add(getDeleteAction());
        result.add(getRenameAction());
        if (separation >= 0) {
            result.addSeparator(new Dimension(separation, 0));
        } else {
            result.addSeparator();
        }
        if (getKind() != DisplayKind.PROLOG) {
            result.add(getEnableButton());
        }
        return result;
    }

    /** Returns the copy action associated with this kind of resource. */
    protected final CopyAction getCopyAction() {
        return getActions().getCopyAction(getKind());
    }

    /** Returns the delete action associated with this kind of resource. */
    protected final SimulatorAction getDeleteAction() {
        return getActions().getDeleteAction(getKind());
    }

    /** Returns the edit action associated with this kind of resource. */
    protected final SimulatorAction getEditAction() {
        return getActions().getEditAction(getKind());
    }

    /** Returns the edit action associated with this kind of resource. */
    protected final SimulatorAction getEnableAction() {
        return getActions().getEnableAction(getKind());
    }

    /** Returns the new action associated with this kind of resource. */
    protected final SimulatorAction getNewAction() {
        return getActions().getNewAction(getKind());
    }

    /** Returns the rename action associated with this kind of resource. */
    protected final SimulatorAction getRenameAction() {
        return getActions().getRenameAction(getKind());
    }

    /** Returns the save action associated with this kind of resource. */
    protected final SaveAction getSaveAction() {
        return getActions().getSaveAction(getResourceKind());
    }

    /** Returns the save action associated with this kind of resource. */
    protected final CancelEditAction getCancelEditAction() {
        return getActions().getCancelEditAction(getResourceKind());
    }

    /** Creates a toggle button wrapping the enable action of this display. */
    protected final JToggleButton getEnableButton() {
        if (this.enableButton == null) {
            this.enableButton = Options.createToggleButton(getEnableAction());
            this.enableButton.setMargin(new Insets(3, 1, 3, 1));
            this.enableButton.setText(null);
        }
        return this.enableButton;
    }

    /** Convenience method to retrieve the simulator model. */
    final protected SimulatorModel getSimulatorModel() {
        return getSimulator().getModel();
    }

    /** Convenience method to retrieve the action store. */
    final protected ActionStore getActions() {
        return getSimulator().getActions();
    }

    /** Returns the resource kind shown on this display. */
    final protected ResourceKind getResourceKind() {
        return this.resource;
    }

    private final Simulator simulator;
    private final ResourceKind resource;
    private final DisplayKind kind;
    /** Panel with the label list. */
    private JPanel listPanel;
    /** Toolbar for the {@link #listPanel}. */
    private JToolBar listToolBar;
    private JToggleButton enableButton;
}
