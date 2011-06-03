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
import groove.gui.jgraph.JAttr;
import groove.view.PrologView;
import groove.view.StoredGrammarView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
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
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * List of available prolog programs.
 * @author Arend Rensink
 * @version $Revision $
 */
public class PrologJList extends JList implements SimulatorListener {
    /**
     * Creates a new state list viewer.
     */
    protected PrologJList(PrologDisplay display) {
        this.display = display;
        this.simulator = display.getSimulator();
        this.setEnabled(false);
        this.setCellRenderer(new MyCellRenderer());
        installListeners();
    }

    private void installListeners() {
        getSimulatorModel().addListener(this, Change.GRAMMAR, Change.PROLOG);
        addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                PrologJList.this.repaint();
            }

            @Override
            public void focusGained(FocusEvent e) {
                PrologJList.this.repaint();
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

    private JToggleButton getEnableButton() {
        if (this.enableButton == null) {
            this.enableButton =
                new JToggleButton(getActions().getEnableControlAction());
            this.enableButton.setText(null);
            this.enableButton.setMargin(new Insets(3, 1, 3, 1));
            this.enableButton.setFocusable(false);
        }
        return this.enableButton;
    }

    /**
     * Creates a popup menu.
     */
    private JPopupMenu createPopupMenu(Point atPoint) {
        JPopupMenu result = new JPopupMenu();
        result.add(getActions().getNewPrologAction());
        result.setFocusable(false);
        // add rest only if mouse is actually over a prolog name
        int index = locationToIndex(atPoint);
        if (index != -1 && getCellBounds(index, index).contains(atPoint)) {
            //            result.add(getActions().getEditControlAction());
            //            result.addSeparator();
            //            result.add(getActions().getCopyControlAction());
            //            result.add(getActions().getDeleteControlAction());
            //            result.add(getActions().getRenameControlAction());
            //            result.addSeparator();
            //            result.add(getActions().getEnableControlAction());
        }
        return result;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        suspendListeners();
        if (changes.contains(Change.GRAMMAR)) {
            if (source.getGrammar() == null
                || source.getGrammar().getPrologNames().isEmpty()) {
                setListData(new String[0]);
                setEnabled(false);
            } else {
                setEnabled(true);
                refresh();
            }
        }
        if (changes.contains(Change.PROLOG) && source.hasProlog()) {
            setSelectedValue(source.getProlog().getName(), true);
        }
        activateListeners();
    }

    /**
     * Refreshes the list from the grammar.
     */
    private void refresh() {
        Object[] prologNames = getGrammar().getPrologNames().toArray();
        Arrays.sort(prologNames);
        setListData(prologNames);
        PrologView selection = getSimulatorModel().getProlog();
        // turn the selection into a set of names
        if (selection == null) {
            clearSelection();
            getEnableButton().setSelected(false);
        } else {
            setSelectedValue(selection.getName(), true);
        }
    }

    /**
     * Returns the current grammar view from the simulator.
     */
    private StoredGrammarView getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    /** Returns the simulator to which the state list belongs. */
    private Simulator getSimulator() {
        return this.simulator;
    }

    /** Returns the simulator to which the state list belongs. */
    private SimulatorModel getSimulatorModel() {
        return getSimulator().getModel();
    }

    /** Returns the simulator to which the state list belongs. */
    private ActionStore getActions() {
        return getSimulator().getActions();
    }

    private final PrologDisplay display;
    /**
     * The simulator to which this directory belongs.
     * @invariant simulator != null
     */
    private final Simulator simulator;
    /** The type enable button. */
    private JToggleButton enableButton;
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

    /** The background colour of a selected cell if the list does not have focus. */
    static private final Color SELECTION_NON_FOCUS_COLOR = Color.LIGHT_GRAY;

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
                    PrologJList.this.requestFocus();
                    createPopupMenu(evt.getPoint()).show(evt.getComponent(),
                        evt.getX(), evt.getY());
                }
            } else if (evt.getClickCount() == 2) { // Left double click
                if (PrologJList.this.isEnabled() && cellSelected) {
                    getActions().getEditPrologAction().execute();
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            getSimulatorModel().setDisplay(DisplayKind.PROLOG);
        }
    }

    private class MySelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            getSimulatorModel().setProlog((String) getSelectedValue());
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
            if (isSelected && !PrologJList.this.isFocusOwner()) {
                Color foreground = Color.BLACK;
                Color background = SELECTION_NON_FOCUS_COLOR;
                if (getSimulatorModel().getGts() == null) {
                    foreground = Color.WHITE;
                    background = background.darker();
                }
                result.setForeground(foreground);
                result.setBackground(background);
            }
            String ctrlName = value.toString();
            boolean error = getGrammar().getPrologView(ctrlName).hasErrors();
            setForeground(JAttr.getForeground(isSelected, cellHasFocus, error));
            setBackground(JAttr.getBackground(isSelected, cellHasFocus, error));
            setText(PrologJList.this.display.getLabelText(ctrlName));
            setIcon(PrologJList.this.display.getListIcon(value.toString()));
            return result;
        }

        private final Border emptyBorder = new EmptyBorder(1, 3, 1, 0);
    }
}
