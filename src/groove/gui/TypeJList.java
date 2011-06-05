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
import groove.gui.jgraph.JAttr;
import groove.view.GrammarModel;
import groove.view.TypeModel;

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
 * List of available type graphs, showing their enabledness status.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TypeJList extends JList implements SimulatorListener {
    /**
     * Creates a new state list viewer.
     */
    protected TypeJList(TypeDisplay display) {
        this.display = display;
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

    /**
     * Creates a popup menu.
     */
    private JPopupMenu createPopupMenu(Point atPoint) {
        int index = locationToIndex(atPoint);
        boolean overItem =
            index != -1 && getCellBounds(index, index).contains(atPoint);
        JPopupMenu result = this.display.createListPopupMenu(overItem);
        return result;
    }

    @Override
    public void update(SimulatorModel source, SimulatorModel oldModel,
            Set<Change> changes) {
        suspendListeners();
        if (changes.contains(Change.GRAMMAR)) {
            if (source.getGrammar() == null
                || source.getGrammar().getTypeNames().isEmpty()) {
                setListData(new String[0]);
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
        TypeModel selection = getSimulatorModel().getType();
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
    private SimulatorModel getSimulatorModel() {
        return this.display.getSimulatorModel();
    }

    /** The display from which this list is derived. */
    private final TypeDisplay display;
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
                    TypeJList.this.requestFocus();
                    createPopupMenu(evt.getPoint()).show(evt.getComponent(),
                        evt.getX(), evt.getY());
                }
            } else if (evt.getClickCount() == 2) { // Left double click
                if (TypeJList.this.isEnabled() && cellSelected) {
                    TypeJList.this.display.getEditAction().execute();
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            getSimulatorModel().setDisplay(DisplayKind.TYPE);
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
            boolean error = TypeJList.this.display.hasError(value.toString());
            setForeground(JAttr.getForeground(isSelected, cellHasFocus, error));
            setBackground(JAttr.getBackground(isSelected, cellHasFocus, error));
            // set tool tips and special formats
            if (getGrammar().getTypeModel(value.toString()).isEnabled()) {
                setToolTipText("Enabled type graph; doubleclick to edit");
            } else {
                setToolTipText("Disabled type graph; doubleclick to edit");
            }
            setIcon(TypeJList.this.display.getListIcon(value.toString()));
            setText(TypeJList.this.display.getLabelText(value.toString()));
            return result;
        }

        private final Border emptyBorder = new EmptyBorder(1, 3, 1, 0);
    }
}
