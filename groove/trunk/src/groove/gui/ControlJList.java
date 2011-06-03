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

import groove.gui.DisplaysPanel.DisplayKind;
import groove.gui.SimulatorModel.Change;
import groove.gui.action.ActionStore;
import groove.gui.action.SimulatorAction;
import groove.gui.jgraph.JAttr;
import groove.view.ControlModel;
import groove.view.GrammarModel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * List of available control programs, showing their enabledness status.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ControlJList extends JList implements SimulatorListener {
    /**
     * Creates a new state list viewer.
     */
    protected ControlJList(ControlDisplay display) {
        this.display = display;
        this.setEnabled(false);
        this.setCellRenderer(new MyCellRenderer());
        installListeners();
    }

    private void installListeners() {
        getSimulatorModel().addListener(this, Change.GRAMMAR, Change.CONTROL);
        addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                ControlJList.this.repaint();
            }

            @Override
            public void focusGained(FocusEvent e) {
                ControlJList.this.repaint();
            }
        });
        addMouseListener(new MyMouseListener());
        this.listListeners =
            new ListSelectionListener[] {new MySelectionListener()};
        activateListeners();
    }

    private void activateListeners() {
        if (this.listening) {
            throw new IllegalStateException();
        }
        for (ListSelectionListener listener : this.listListeners) {
            addListSelectionListener(listener);
        }
        this.listListeners = null;
        this.listening = true;
    }

    /**
     * Removes all list selection listeners. The listeners should be added back
     * later using {@link #activateListeners()}
     */
    private void suspendListeners() {
        if (!this.listening) {
            throw new IllegalStateException();
        }
        this.listListeners = getListSelectionListeners();
        for (ListSelectionListener listener : this.listListeners) {
            removeListSelectionListener(listener);
        }
        this.listening = false;
    }

    /**
     * In addition to delegating the method to <tt>super</tt>, sets the
     * background colour to <tt>null</tt> when disabled and back to the default
     * when enabled.
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != isEnabled()) {
            if (!enabled) {
                this.enabledBackground = getBackground();
                setBackground(null);
            } else if (this.enabledBackground != null) {
                setBackground(this.enabledBackground);
            }
        }
        super.setEnabled(enabled);
    }

    /**
     * Creates a popup menu.
     */
    private JPopupMenu createPopupMenu(Point atPoint) {
        JPopupMenu result = new JPopupMenu();
        result.add(getActions().getNewControlAction());
        result.setFocusable(false);
        // add rest only if mouse is actually over a control name
        int index = locationToIndex(atPoint);
        if (index != -1 && getCellBounds(index, index).contains(atPoint)) {
            result.add(getActions().getEditControlAction());
            result.addSeparator();
            result.add(getActions().getCopyControlAction());
            result.add(getActions().getDeleteControlAction());
            result.add(getActions().getRenameControlAction());
            result.addSeparator();
            result.add(getActions().getEnableControlAction());
        }
        return result;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        suspendListeners();
        if (changes.contains(Change.GRAMMAR)) {
            if (source.getGrammar() == null
                || source.getGrammar().getControlNames().isEmpty()) {
                setListData(new String[0]);
                setEnabled(false);
            } else {
                setEnabled(true);
                refresh();
            }
        } else if (changes.contains(Change.CONTROL) || source.hasControl()) {
            setSelectedValue(source.getControl().getName(), true);
        }
        activateListeners();
    }

    /**
     * Refreshes the list from the grammar.
     */
    private void refresh() {
        Object[] controlNames = getGrammar().getControlNames().toArray();
        Arrays.sort(controlNames);
        setListData(controlNames);
        ControlModel selection = getSimulatorModel().getControl();
        // turn the selection into a set of names
        if (selection == null) {
            clearSelection();
        } else {
            setSelectedValue(selection.getName(), true);
        }
    }

    /**
     * Returns the current grammar view from the simulator.
     */
    private GrammarModel getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    /** Returns the simulator to which the state list belongs. */
    private Simulator getSimulator() {
        return this.display.getSimulator();
    }

    /** Returns the simulator to which the state list belongs. */
    private SimulatorModel getSimulatorModel() {
        return getSimulator().getModel();
    }

    /** Returns the simulator to which the state list belongs. */
    private ActionStore getActions() {
        return getSimulator().getActions();
    }

    private final ControlDisplay display;
    private boolean listening;
    /**
     * Temporary store of suspended list selection listeners.
     * @see #suspendListeners()
     * @see #activateListeners()
     */
    private ListSelectionListener[] listListeners;
    /**
     * The background colour of this component when it is enabled.
     */
    private Color enabledBackground;

    /** Class to deal with mouse events over the label list. */
    private class MyMouseListener extends MouseAdapter {

        /** Empty constructor with the correct visibility. */
        MyMouseListener() {
            // empty
        }

        @Override
        public void mouseClicked(MouseEvent evt) {
            int index = locationToIndex(evt.getPoint());
            Rectangle cellBounds = getCellBounds(index, index);
            boolean cellSelected =
                cellBounds != null && cellBounds.contains(evt.getPoint());
            if (evt.getClickCount() == 1) {
                if (evt.getButton() == MouseEvent.BUTTON3) { // Right click
                    // Determine if index was really selected
                    if (index >= 0 && cellSelected) {
                        if (getSelectedIndices().length < 2) {
                            // Adjust list selection accordingly.
                            setSelectedIndex(index);
                        }
                    }
                    ControlJList.this.requestFocus();
                    createPopupMenu(evt.getPoint()).show(evt.getComponent(),
                        evt.getX(), evt.getY());
                }
            } else if (evt.getClickCount() == 2) { // Left double click
                SimulatorAction editAction =
                    getActions().getEditControlAction();
                if (ControlJList.this.isEnabled() && editAction.isEnabled()
                    && cellSelected) {
                    editAction.execute();
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            getSimulatorModel().setDisplay(DisplayKind.CONTROL);
        }
    }

    private class MySelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            getSimulatorModel().setControl((String) getSelectedValue());
        }
    }

    /**
     * Cell renderer that distinguishes the name corresponding to the current
     * start graph and to the simulation state indicator.
     */
    private class MyCellRenderer extends DefaultListCellRenderer {
        // This is the only method defined by ListCellRenderer.
        // We just reconfigure the JLabel each time we're called.
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, // cell index
                boolean isSelected, // is the cell selected
                boolean cellHasFocus) // the list and the cell have the focus
        {
            Component result =
                super.getListCellRendererComponent(list, value, index,
                    isSelected, false);
            // ensure some space to the left of the label
            setBorder(this.emptyBorder);
            String ctrlName = value.toString();
            boolean isActiveControl =
                ctrlName.equals(getGrammar().getControlName());
            boolean error = getGrammar().getControlModel(ctrlName).hasErrors();
            setForeground(JAttr.getForeground(isSelected, cellHasFocus, error));
            setBackground(JAttr.getBackground(isSelected, cellHasFocus, error));
            setText(ControlJList.this.display.getLabelText(ctrlName));
            setIcon(ControlJList.this.display.getListIcon(value.toString()));
            // set tool tips and special formats
            String tip;
            if (isActiveControl) {
                tip = "Active control program";
            } else {
                tip = "Inactive control program";
            }
            if (getActions().getEditControlAction().isEnabled()) {
                tip += "; doubleclick to edit";
            }
            return result;
        }

        private final Border emptyBorder = new EmptyBorder(1, 3, 1, 0);
    }
}
