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
import groove.gui.action.CopyAction;
import groove.gui.action.SimulatorAction;
import groove.trans.ResourceKind;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

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
    protected ResourceDisplay(Simulator simulator, DisplayKind kind) {
        this.simulator = simulator;
        this.kind = kind;
        this.resource = kind.getResource();
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
    public String getName() {
        if (getSimulatorModel().isSelected(getResource())) {
            return getSimulatorModel().getSelected(getResource());
        } else {
            return null;
        }
    }

    /** Creates an editor for the resource with given name. */
    public abstract void createEditor(String name);

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
            res.addSeparator();
            res.add(getEnableAction());
        }
        return res;
    }

    /** 
     * Creates a tool bar for the list panel.
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
    protected final SimulatorAction getSaveAction(boolean saveAs) {
        return getActions().getSaveGraphAction(getResource().getGraphRole());
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
    final protected ResourceKind getResource() {
        return this.resource;
    }

    private final Simulator simulator;
    private final ResourceKind resource;
    private final DisplayKind kind;
    private JToggleButton enableButton;
}
