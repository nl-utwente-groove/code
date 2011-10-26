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

import groove.gui.Display.Tab;
import groove.gui.action.CancelEditAction;
import groove.gui.action.SaveAction;
import groove.gui.action.SimulatorAction;
import groove.trans.ResourceKind;
import groove.view.FormatError;
import groove.view.ResourceModel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Observer;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

/**
 * Superclass for grammar component editors.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class ResourceTab extends JPanel implements Tab {
    /** Creates a panel for a given display. */
    public ResourceTab(ResourceDisplay display) {
        final Simulator simulator = display.getSimulator();
        this.display = display;
        this.resourceKind = display.getResourceKind();
        this.simulator = simulator;
        setBorder(null);
        setLayout(new BorderLayout());
        addAccelerator(getSaveAction());
        addAccelerator(getCancelAction());
    }

    /** 
     * Starts the editor.
     * This method should be called directly after the constructor.
     */
    protected void start() {
        if (isEditor()) {
            add(createToolBar(), BorderLayout.NORTH);
        }
        add(getMainPanel());
    }

    /** Adds a key accelerator for a given action. */
    private void addAccelerator(SimulatorAction action) {
        KeyStroke key = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
        String name = (String) action.getValue(Action.NAME);
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, name);
        getInputMap(WHEN_FOCUSED).put(key, name);
        getActionMap().put(name, action);
    }

    /** Creates a panel consisting of the error panel and the status bar. */
    private JSplitPane getMainPanel() {
        if (this.mainPanel == null) {
            this.mainPanel =
                new JSplitPane(JSplitPane.VERTICAL_SPLIT, getEditArea(),
                    getErrorPanel());
            this.mainPanel.setDividerSize(1);
            this.mainPanel.setContinuousLayout(true);
            this.mainPanel.setResizeWeight(0.9);
            this.mainPanel.resetToPreferredSizes();
            this.mainPanel.setBorder(null);
        }
        return this.mainPanel;
    }

    /** Lazily creates and returns the error panel. */
    final protected ListPanel getErrorPanel() {
        if (this.errorPanel == null) {
            this.errorPanel = new ListPanel();
            this.errorPanel.setTitle(String.format("Errors in %s",
                getResourceKind().getDescription()));
            this.errorPanel.setEntryType(FormatError.prototype);
            this.errorPanel.addSelectionListener(createErrorListener());
        }
        return this.errorPanel;
    }

    /** Callback method to construct the area where the actual editing goes on. */
    abstract protected JComponent getEditArea();

    /**
     * Creates an observer for the error panel that will select the
     * erroneous part of the resource upon selection of an error.
     */
    abstract protected Observer createErrorListener();

    /**
     * Displays a list of errors, or hides the error panel if the list is empty.
     */
    final protected void updateErrors() {
        getErrorPanel().setEntries(getErrors());
        if (getErrorPanel().isVisible()) {
            getMainPanel().setBottomComponent(getErrorPanel());
            getMainPanel().setDividerSize(1);
            getMainPanel().resetToPreferredSizes();
        } else {
            getMainPanel().remove(getErrorPanel());
            getMainPanel().setDividerSize(0);
        }
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

    /** 
     * Saves and optionally disposes the editor, after an optional confirmation dialog.
     * @param confirm if {@code true}, the user is asked for confirmation if the editor is dirty 
     * @param dispose if {@code true}, the editor is disposed (unless the action is cancelled)
     * @return {@code true} if the user did not cancel the action
     */
    public boolean saveEditor(boolean confirm, boolean dispose) {
        boolean result = false;
        if (!confirm || confirmCancel()) {
            if (dispose) {
                dispose();
            }
            result = true;
        }
        return result;
    }

    /**
     * Creates and shows a confirmation dialog for abandoning the currently
     * edited graph.
     * @return {@code true} if the user did not cancel the action
     */
    public boolean confirmCancel() {
        boolean result = true;
        if (isDirty()) {
            int answer =
                JOptionPane.showConfirmDialog(getDisplay().getDisplayPanel(),
                    String.format("%s '%s' has been modified. Save changes?",
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
    private TabLabel createTabLabel() {
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

    /** Returns the resource model displayed on this tab. */
    protected ResourceModel<?> getResource() {
        return getSimulatorModel().getGrammar().getResource(getResourceKind(),
            getName());
    }

    /** Saves the resource that is currently being displayed. */
    abstract protected void saveResource();

    /** Disposes the editor, by removing it as a listener and simulator panel component. */
    public void dispose() {
        getDisplay().getTabPane().remove(this);
    }

    /** Returns the display on which this tab is placed. */
    public final ResourceDisplay getDisplay() {
        return this.display;
    }

    /** Returns the errors of the displayed resource. */
    protected Collection<FormatError> getErrors() {
        return getResource().getErrors();
    }

    /** Indicates if the displayed resource is currently in an error state. */
    final protected boolean hasErrors() {
        return !getErrors().isEmpty();
    }

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
    /** Panel containing the edit area and error panel. */
    private JSplitPane mainPanel;
    /** Panel displaying format error messages. */
    private ListPanel errorPanel;
    /** The component that constitutes the tab when this panel is used in a {@link JTabbedPane}. */
    private TabLabel tabLabel;

    /** Mouse listener that ensures doubleclicking starts an editor for this tab. */
    protected final class EditMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                getDisplay().getEditAction().execute();
            }
        }
    }
}
