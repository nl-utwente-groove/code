/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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

import groove.view.StoredGrammarView;
import groove.view.TypeView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

/**
 * Customized GUI component that implements a list of type graph names with
 * check boxes.
 * @author Eduardo Zambon
 */
public class JTypeNameList extends JList {

    // ------------------------------------------------------------------------
    // Static Fields
    // ------------------------------------------------------------------------

    private static final int LIST_HEIGHT = 35;
    /** The min dimensions of the list. */
    public static final Dimension MIN_DIMENSIONS = new Dimension(200,
        LIST_HEIGHT);
    /** The max dimensions of the list. */
    public static final Dimension MAX_DIMENSIONS = new Dimension(400,
        LIST_HEIGHT);

    private static final EmptyBorder INSET_BORDER = new EmptyBorder(0, 2, 0, 7);
    private static final String CHECKBOX_ORIENTATION = BorderLayout.WEST;
    private static final int CHECKBOX_WIDTH =
        new JCheckBox().getPreferredSize().width;

    private static JTextField enabledField = new JTextField();
    private static JTextField disabledField = new JTextField();
    static {
        enabledField.setEditable(true);
        disabledField.setEditable(false);
    }
    private static Color ENABLED_COLOUR = enabledField.getBackground();
    private static Color DISABLED_COLOUR = disabledField.getBackground();

    // ------------------------------------------------------------------------
    // Static Methods
    // ------------------------------------------------------------------------

    static private Color getColor(boolean enabled) {
        return enabled ? ENABLED_COLOUR : DISABLED_COLOUR;
    }

    // ------------------------------------------------------------------------
    // Object Fields
    // ------------------------------------------------------------------------

    /** The panel showing the type graphs. */
    protected final TypePanel panel;
    private final CheckBoxListModel model;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs a check box list with type graph names.
     * @param panel the panel where this list is placed.
     */
    public JTypeNameList(TypePanel panel) {
        this.panel = panel;
        this.model = new CheckBoxListModel();
        this.setModel(this.model);

        this.setEnabled(false);
        this.setBackground(getColor(false));
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setLayoutOrientation(JList.VERTICAL_WRAP);
        this.setVisibleRowCount(1);

        this.addMouseListener(new MouseListener());
        this.setCellRenderer(new CellRenderer());
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    /** Reads in the type names and enabled types, and creates the name list. */
    public void refreshTypes() {
        StoredGrammarView grammar = this.panel.getSimulatorModel().getGrammar();
        if (grammar == null) {
            // No grammar. Disable the component. 
            this.setEnabled(false);
            this.setBackground(getColor(false));
        } else {
            Set<String> names = new TreeSet<String>(grammar.getTypeNames());
            if (!names.isEmpty()) {
                this.setEnabled(true);
                this.setBackground(getColor(true));
            }
            this.model.synchronizeModel(names);
            this.model.setCheckedTypes(grammar.getActiveTypeNames());
            TypeView type = this.panel.getSimulatorModel().getType();
            if (type != null) {
                setSelectedIndex(this.model.getIndexByName(type.getName()));
            } else {
                clearSelection();
            }
        }
    }

    @Override
    public CheckBoxListModel getModel() {
        return this.model;
    }

    // ------------------------------------------------------------------------
    // Normal methods
    // ------------------------------------------------------------------------

    /** Tests if a given event is over the check box part of a graph name. */
    private boolean isOverCheckBox(MouseEvent e) {
        boolean result = false;
        int index = this.locationToIndex(e.getPoint());
        if (index != -1) {
            Rectangle cellBounds = this.getCellBounds(index, index);
            int checkboxBorder = cellBounds.x + CHECKBOX_WIDTH;
            result = e.getX() < checkboxBorder;
        }
        return result;
    }

    private int getStringWidth(String text) {
        // Get the information on the font the is being used on the list.
        FontMetrics fontMetrics = this.getFontMetrics(this.getFont());
        return fontMetrics.stringWidth(text);
    }

    // ------------------------------------------------------------------------
    // Inner Classes
    // ------------------------------------------------------------------------

    /**
     * Holds a String and a value indicating whether or not it is selected.
     */
    protected static class ListItem {
        /** The item in the list. */
        public String dataItem;
        /** The check box. */
        public boolean checked;

        /**
         * Initializes a new ListItem
         * @param dataItem the item to display
         * @param checked boolean indicating if the item is ticked
         */
        public ListItem(String dataItem, boolean checked) {
            this.dataItem = dataItem;
            this.checked = checked;
        }

        @Override
        public String toString() {
            return this.dataItem;
        }
    }

    // ------------------------------------------------------------------------
    // CheckBoxListModel
    // ------------------------------------------------------------------------

    /**
     * The underlying model that backs the JList content.
     * @author Eduardo Zambon
     * @version $Revision $
     */
    protected class CheckBoxListModel extends AbstractListModel {
        private ArrayList<ListItem> items = new ArrayList<ListItem>();

        /**
         * @return the size of the list.
         */
        public int getSize() {
            return this.items.size();
        }

        /**
         * @param index the index of the item to find.
         * @return the item at the specified index.
         */
        public ListItem getElementAt(int index) {
            return this.items.get(index);
        }

        /**
         * @param name the name of the element to look for.
         * @return the corresponding list item, null if not found.
         */
        public ListItem getElementByName(String name) {
            for (ListItem item : this.items) {
                if (item.dataItem.equals(name)) {
                    return item;
                }
            }
            return null;
        }

        /**
         * @param name the name of the element to look for.
         * @return the index of the corresponding list item, -1 if not found.
         */
        public int getIndexByName(String name) {
            int result = -1;
            for (int i = 0; i < this.items.size(); i++) {
                if (this.items.get(i).dataItem.equals(name)) {
                    result = i;
                    break;
                }
            }
            return result;
        }

        /**
         * @return a list with all checked type names in the component.
         */
        public List<String> getCheckedTypes() {
            ArrayList<String> types = new ArrayList<String>();
            for (ListItem item : this.items) {
                if (item.checked) {
                    types.add(item.dataItem);
                }
            }
            return types;
        }

        /**
         * Checks the given types in the list.
         * @param types the types to check.
         */
        public void setCheckedTypes(List<String> types) {
            for (ListItem item : this.items) {
                item.checked = false;
            }
            for (String type : types) {
                ListItem item = this.getElementByName(type);
                if (item != null) {
                    item.checked = true;
                }
            }
        }

        /**
         * @return true if all elements of the list are checked, 
         *         false otherwise.
         */
        public boolean isAllChecked() {
            for (ListItem item : this.items) {
                if (!item.checked) {
                    return false;
                }
            }
            return true;
        }

        /**
         * @return true if all elements of the list are unchecked, 
         *         false otherwise.
         */
        public boolean isAllUnchecked() {
            for (ListItem item : this.items) {
                if (item.checked) {
                    return false;
                }
            }
            return true;
        }

        /**
         * @return the item selected in the list.
         */
        public ListItem getSelectedItem() {
            int index = JTypeNameList.this.getSelectedIndex();
            if (index >= 0) {
                return this.items.get(index);
            } else {
                return null;
            }
        }

        /**
         * @return true is the item selected in the list is checked,
         *         false otherwise.
         */
        public boolean isSelectedChecked() {
            ListItem item = getSelectedItem();
            if (item != null) {
                return item.checked;
            } else {
                return false;
            }
        }

        /**
         * Synchronizes the underlying list model with the items given.
         * @param items the elements to go into the list.
         * @return true if the model was changed, false otherwise.
         */
        public boolean synchronizeModel(Set<String> items) {
            boolean modelChanged = false;

            // Remove entries from the model.
            Collection<ListItem> toRemove = new ArrayList<ListItem>();
            for (ListItem listItem : this.items) {
                if (!items.contains(listItem.dataItem)) {
                    toRemove.add(listItem);
                    modelChanged = true;
                }
            }
            this.items.removeAll(toRemove);

            // Add new entries.
            Collection<ListItem> toAdd = new ArrayList<ListItem>();
            for (String stringItem : items) {
                ListItem listItem = this.getElementByName(stringItem);
                if (listItem == null) {
                    listItem = new ListItem(stringItem, false);
                    toAdd.add(listItem);
                    modelChanged = true;
                }
            }
            this.items.addAll(toAdd);

            if (modelChanged) {
                this.fireContentsChanged(this, 0, this.items.size() - 1);
            }

            return modelChanged;
        }
    }

    // ------------------------------------------------------------------------
    // CellRenderer
    // ------------------------------------------------------------------------

    /**
     * Renders ListItems as JCheckBoxes.
     */
    private class CellRenderer extends JPanel implements ListCellRenderer {

        private final DefaultListCellRenderer jLabel;
        private final JCheckBox checkBox;
        private Box containerBox;

        CellRenderer() {
            this.jLabel = new DefaultListCellRenderer();
            this.jLabel.setBorder(JTypeNameList.INSET_BORDER);
            this.checkBox = new JCheckBox();
            this.checkBox.setOpaque(false);
            this.setLayout(new BorderLayout());
            this.add(this.jLabel, BorderLayout.CENTER);
            this.add(this.checkBox, JTypeNameList.CHECKBOX_ORIENTATION);
            setBorder(new EmptyBorder(0, 2, 0, 0));
            setComponentOrientation(JTypeNameList.this.getComponentOrientation());
            setOpaque(false);
        }

        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JComponent result;
            this.jLabel.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);
            Color background = getColor(JTypeNameList.this.isEnabled());

            ListItem item = null;
            if (value instanceof ListItem) {
                item = (ListItem) value;
                this.containerBox = Box.createHorizontalBox();
                this.containerBox.add(new JLabel(item.dataItem));
                int width = getStringWidth(item.dataItem);
                int height = this.containerBox.getHeight();
                this.containerBox.setSize(width, height);
            } else {
                this.containerBox = null;
            }
            if (this.containerBox != null) {
                // Store the max width of a cell
                this.checkBox.setSelected(item.checked);
                setBackground(background);
                // Re-add the label (it gets detached if used as a stand-alone
                // renderer)
                add(this.jLabel, BorderLayout.CENTER);
                result = this;
            } else {
                result = this.jLabel;
            }

            return result;
        }

        /**
         * Overridden for performance reasons.
         */
        @Override
        protected void firePropertyChange(String propertyName, Object oldValue,
                Object newValue) {
            // Strings get interned...
            if ("text".equals(propertyName)
                || (("font".equals(propertyName) || "foreground".equals(propertyName))
                    && oldValue != newValue && getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey) != null)) {
                super.firePropertyChange(propertyName, oldValue, newValue);
            }
        }

        /**
         * Overridden for performance reasons.
         */
        @Override
        public void firePropertyChange(String propertyName, byte oldValue,
                byte newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons.
         */
        @Override
        public void firePropertyChange(String propertyName, char oldValue,
                char newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons.
         */
        @Override
        public void firePropertyChange(String propertyName, short oldValue,
                short newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons.
         */
        @Override
        public void firePropertyChange(String propertyName, int oldValue,
                int newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons.
         */
        @Override
        public void firePropertyChange(String propertyName, long oldValue,
                long newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons.
         */
        @Override
        public void firePropertyChange(String propertyName, float oldValue,
                float newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons.
         */
        @Override
        public void firePropertyChange(String propertyName, double oldValue,
                double newValue) {
            // empty
        }

        /**
         * Overridden for performance reasons.
         */
        @Override
        public void firePropertyChange(String propertyName, boolean oldValue,
                boolean newValue) {
            // empty
        }

    } // End CellRenderer

    // ------------------------------------------------------------------------
    // MouseListener
    // ------------------------------------------------------------------------

    private class MouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (isOverCheckBox(e) || e.getClickCount() == 2) {
                // Toggle check box.
                int index = locationToIndex(e.getPoint());
                if (index != -1) {
                    ListItem item = getModel().getElementAt(index);
                    item.checked = !item.checked;
                    Simulator simulator =
                        JTypeNameList.this.panel.getSimulator();
                    try {
                        simulator.getModel().doSetActiveTypes(
                            getModel().getCheckedTypes());
                    } catch (IOException exc) {
                        simulator.showErrorDialog(
                            "Error while modifying type graph composition", exc);
                    }
                }
            }
        }
    } // End MouseListener

} // End JTypeNameList