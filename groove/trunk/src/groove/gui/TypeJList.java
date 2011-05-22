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

import groove.gui.SimulatorModel.Change;
import groove.gui.SimulatorPanel.TabKind;
import groove.gui.action.ActionStore;
import groove.gui.dialog.ErrorDialog;
import groove.lts.GTS;
import groove.view.StoredGrammarView;
import groove.view.TypeView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * List of available type graphs, showing their enabledness status.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TypeJList extends JList implements SimulatorListener {
    /**
     * Creates a new state list viewer.
     */
    protected TypeJList(final Simulator simulator) {
        this.simulator = simulator;
        this.setEnabled(false);
        this.setCellRenderer(new MyCellRenderer());
        installListeners();
    }

    private void installListeners() {
        getSimulatorModel().addListener(this, Change.GRAMMAR, Change.TYPE);
        addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                TypeJList.this.repaint();
            }

            @Override
            public void focusGained(FocusEvent e) {
                TypeJList.this.repaint();
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

    /** Creates a tool bar for the rule tree. */
    void fillToolBar(JToolBar result) {
        result.setFloatable(false);
        result.add(getActions().getNewTypeAction());
        result.addSeparator();
        result.add(getActions().getCopyTypeAction());
        result.add(getActions().getDeleteTypeAction());
        result.add(getActions().getRenameTypeAction());
        result.addSeparator();
        result.add(getEnableButton());
        // make sure tool tips get displayed
        ToolTipManager.sharedInstance().registerComponent(result);
    }

    private JToggleButton getEnableButton() {
        if (this.enableButton == null) {
            this.enableButton =
                new JToggleButton(getActions().getEnableTypeAction());
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
        result.add(getActions().getNewTypeAction());
        result.setFocusable(false);
        // add rest only if mouse is actually over a type name
        int index = locationToIndex(atPoint);
        if (getCellBounds(index, index).contains(atPoint)) {
            result.add(getActions().getEditTypeAction());
            result.addSeparator();
            result.add(getActions().getCopyTypeAction());
            result.add(getActions().getDeleteTypeAction());
            result.add(getActions().getRenameTypeAction());
            result.addSeparator();
            result.add(getActions().getEnableTypeAction());
        }
        return result;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        suspendListeners();
        if (changes.contains(Change.GRAMMAR)) {
            if (source.getGrammar() == null) {
                removeAll();
                setEnabled(false);
            } else {
                setEnabled(true);
                refresh();
            }
        } else if (changes.contains(Change.TYPE)) {
            refresh();
        }
        activateListeners();
    }

    /**
     * Refreshes the list from the grammar.
     */
    private void refresh() {
        Object[] typeNames = getGrammar().getTypeNames().toArray();
        Arrays.sort(typeNames);
        setListData(typeNames);
        TypeView selection = getSimulatorModel().getType();
        // turn the selection into a set of names
        if (selection == null) {
            clearSelection();
            getEnableButton().setSelected(false);
        } else {
            setSelectedValue(selection.getName(), true);
            getEnableButton().setSelected(
                getGrammar().getActiveTypeNames().contains(selection.getName()));
        }
    }

    /**
     * Returns the current grammar view from the simulator.
     */
    private StoredGrammarView getGrammar() {
        return getSimulatorModel().getGrammar();
    }

    /** Convenience method to retrieve the current GTS from the simulator. */
    private GTS getCurrentGTS() {
        return getSimulatorModel().getGts();
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
                    TypeJList.this.requestFocus();
                    createPopupMenu(evt.getPoint()).show(evt.getComponent(),
                        evt.getX(), evt.getY());
                }
            } else if (evt.getClickCount() == 2) { // Left double click
                if (TypeJList.this.isEnabled() && cellSelected) {
                    try {
                        getSimulatorModel().doEnableType(
                            getSimulatorModel().getType().getAspectGraph());
                    } catch (IOException e) {
                        new ErrorDialog(getSimulator().getFrame(),
                            "Error while enabling type graph", e).setVisible(true);
                    }
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            getSimulatorModel().setTabKind(TabKind.TYPE);
        }
    }

    private class MySelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            getSimulatorModel().setType((String) getSelectedValue());
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
            if (isSelected && !TypeJList.this.isFocusOwner()) {
                Color foreground = Color.BLACK;
                Color background = SELECTION_NON_FOCUS_COLOR;
                if (getCurrentGTS() == null) {
                    foreground = Color.WHITE;
                    background = background.darker();
                }
                result.setForeground(foreground);
                result.setBackground(background);
            }
            // set tool tips and special formats
            if (getGrammar().getActiveTypeNames().contains(value.toString())) {
                setFont(getFont().deriveFont(Font.BOLD));
                setToolTipText("Enabled type graph");
            } else {
                setToolTipText("Disabled type graph; doubleclick to enable");
            }
            return result;
        }

        private final Border emptyBorder = new EmptyBorder(0, 3, 0, 0);
    }
}
