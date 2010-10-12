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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Customized GUI component that implements a list of type graph names with
 * check boxes.
 * @author Eduardo Zambon
 */
public class JTypeNameList extends JList implements TypePanel.Refreshable {

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

    private final TypePanel panel;
    private final CheckBoxListModel model;
    private final ListSelectionListener selectionListener;

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

        this.selectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    // This event is referencing the previously selected value,
                    // ignore it and wait for the event with the new value.
                    return;
                }
                int index = JTypeNameList.this.getSelectedIndex();
                ListItem item = JTypeNameList.this.model.getElementAt(index);
                JTypeNameList.this.panel.setSelectedType(item.dataItem);
                JTypeNameList.this.panel.displayType();
            }
        };
        this.addListSelectionListener(this.selectionListener);

        this.addMouseListener(new MouseListener());
        this.setCellRenderer(new CellRenderer());
        this.panel.addRefreshable(this);
    }

    // ------------------------------------------------------------------------
    // Overridden methods
    // ------------------------------------------------------------------------

    @Override
    public void refresh() {
        this.removeListSelectionListener(this.selectionListener);
        if (this.panel.getGrammarView() == null) {
            // No grammar. Disable the component. 
            this.setEnabled(false);
            this.setBackground(getColor(false));
        } else {
            Set<String> names =
                new TreeSet<String>(this.panel.getGrammarView().getTypeNames());
            if (!names.isEmpty()) {
                this.setEnabled(true);
                this.setBackground(getColor(true));
            }
            if (this.model.synchronizeModel(names)) {
                List<String> types =
                    this.panel.getGrammarView().getProperties().getTypeNames();
                this.model.setCheckedTypes(types);
                this.model.selectMostAppropriateType();
            }
        }
        this.addListSelectionListener(this.selectionListener);
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
         * @return the corresponding index of the item, -1 if not found.
         */
        public int getIndexByName(String name) {
            for (int i = 0; i < this.getSize(); i++) {
                if (this.items.get(i).dataItem.equals(name)) {
                    return i;
                }
            }
            return -1;
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
            this.uncheckAll();
            for (String type : types) {
                ListItem item = this.getElementByName(type);
                if (item != null) {
                    item.checked = true;
                }
            }
        }

        /**
         * Checks all elements of the list.
         */
        public void checkAll() {
            for (ListItem item : this.items) {
                item.checked = true;
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
         * Unchecks all elements of the list.
         */
        public void uncheckAll() {
            for (ListItem item : this.items) {
                item.checked = false;
            }
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
         * Removes the given type name from the list.
         * @param typeName the name of the graph to remove.
         * @param saveProp flag indicating if the properties should be saved.
         */
        public void removeType(String typeName, boolean saveProp) {
            int index = this.getIndexByName(typeName);
            if (index >= 0) {
                this.items.remove(index);
                if (saveProp) {
                    JTypeNameList.this.panel.doSaveProperties();
                }
            }
        }

        /**
         * Adds a new type name to the list.
         * @param typeName the name of the type graph.
         * @param checked flag indicating if the item should start checked.
         * @param saveProp flag indicating if the properties should be saved.
         */
        public void addType(String typeName, boolean checked, boolean saveProp) {
            ListItem item = this.getElementByName(typeName);
            if (item == null) {
                item = new ListItem(typeName, checked);
                this.items.add(item);
            } else {
                item.checked = checked;
            }
            if (saveProp) {
                JTypeNameList.this.panel.doSaveProperties();
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
         * @return the name of the type graph selected in the list.
         */
        public String getSelectedType() {
            ListItem item = getSelectedItem();
            if (item != null) {
                return item.dataItem;
            } else {
                return null;
            }
        }

        /**
         * Sets the selection of the list to the given type graph.
         * @param typeName the name of the type graph.
         */
        public void setSelectedType(String typeName) {
            int index = this.getIndexByName(typeName);
            if (index >= 0) {
                JTypeNameList.this.setSelectedIndex(index);
            }
        }

        /**
         * Sets the check box of the item.
         * @param typeName the type graph name.
         * @param checked flag to indicate if the check box is ticked.
         */
        public void checkType(String typeName, boolean checked) {
            ListItem item = this.getElementByName(typeName);
            if (item != null) {
                item.checked = checked;
                JTypeNameList.this.panel.doSaveProperties();
            }
        }

        /**
         * Sets the selection of the list to the most appropriate element.
         * If the list already has a selection, it is unchanged.
         * If not, it first tries to set the selection to the first checked
         * element of the list. If there are no checked elements, then it tries
         * to set the selection to the first element of the list.
         */
        public void selectMostAppropriateType() {
            int index = JTypeNameList.this.getSelectedIndex();
            if (index == -1) {
                // There is no selected item. We need to choose one.
                // First we try to get the first checked item.
                List<String> checkedTypes = this.getCheckedTypes();
                if (!checkedTypes.isEmpty()) {
                    // We have at least one checked type. Just use the first.
                    this.setSelectedType(checkedTypes.get(0));
                } else {
                    // There are no checked types. Look for any type in
                    // the grammar.
                    if (this.getSize() > 0) {
                        // We have at least one type in the grammar. Just use
                        // the first.
                        this.setSelectedType(this.items.get(0).dataItem);
                    }
                }
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
                JTypeNameList.this.clearSelection();
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
            if (propertyName == "text"
                || ((propertyName == "font" || propertyName == "foreground")
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
                    ListItem item =
                        JTypeNameList.this.model.getElementAt(index);
                    item.checked = !item.checked;
                    JTypeNameList.this.panel.doSaveProperties();
                }
            }
        }
    } // End MouseListener

} // End JTypeNameList