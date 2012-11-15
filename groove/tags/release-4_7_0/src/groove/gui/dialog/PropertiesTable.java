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
 * $Id: PropertiesDialog.java,v 1.11 2008-01-30 09:33:37 iovka Exp $
 */
package groove.gui.dialog;

import groove.graph.GraphProperties;
import groove.util.ListComparator;
import groove.util.Property;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

/**
 * Dialog for editing a properties map.
 * @author Arend Rensink
 * @version $Revision $
 */
public class PropertiesTable extends JTable {
    /**
     * Constructs an instance of the dialog for a given parent frame, a given
     * (non-<code>null</code>) set of properties, and a flag indicating if
     * the properties should be editable. A further parameter gives a list of
     * default keys that treated specially: they are added by default during
     * editing, and are ordered first in the list.
     */
    public PropertiesTable(Map<String,? extends PropertyKey> defaultKeys,
            boolean editable) {
        this.editable = editable;
        this.defaultKeys = defaultKeys;
        this.properties =
            new TreeMap<String,String>(new ListComparator<String>(
                this.defaultKeys.keySet()));
        final TableModel model = getTableModel();
        setModel(model);
        setIntercellSpacing(new Dimension(2, -2));
        setDefaultRenderer(getColumnClass(PROPERTY_COLUMN), new CellRenderer());
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    }

    /** Changes the underlying properties. */
    public void setProperties(Properties properties) {
        this.properties.clear();
        for (PropertyKey key : this.defaultKeys.values()) {
            if (!key.isSystem()) {
                this.properties.put(key.getName(), key.getDefaultValue());
            }
        }
        this.nonSystemKeyCount = this.properties.size();
        for (Map.Entry<?,?> property : properties.entrySet()) {
            String keyword = (String) property.getKey();
            PropertyKey key = this.defaultKeys.get(keyword);
            if (key == null || !key.isSystem()) {
                this.properties.put(keyword, (String) property.getValue());
            }
        }
        setPreferredScrollableViewportSize(new Dimension(300, Math.max(
            (getModel().getRowCount() + 2) * ROW_HEIGHT, 80)));
        setEnabled(true);
        getTableModel().fireTableDataChanged();
        setChanged(false);
    }

    /** Resets the underlying properties. */
    public void resetProperties() {
        this.properties.clear();
        setChanged(false);
        setEnabled(false);
        repaint();
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        if (column == PROPERTY_COLUMN) {
            return getKeyEditor();
        } else {
            return getValueEditor(((TableModel) getModel()).getPropertyKey(row));
        }
    }

    /**
     * Returns an alias to the properties object in the dialog.
     */
    final public Map<String,String> getProperties() {
        return this.properties;
    }

    /**
     * Indicates if the editing session has made a change to the properties.
     */
    public boolean isChanged() {
        return this.changed;
    }

    /**
     * Indicates if the properties are editable.
     * @return <code>true</code> if the properties are editable
     */
    public boolean isEditable() {
        return this.editable;
    }

    /** Creates an instance of {@link TableModel}. */
    private TableModel getTableModel() {
        if (this.tableModel == null) {
            this.tableModel = new TableModel();
            this.tableModel.addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    setChanged(true);
                }
            });
        }
        return this.tableModel;
    }

    /** Sets the value of the changed field. */
    void setChanged(boolean changed) {
        this.changed = changed;
    }

    /**
     * Returns a (fixed) editor that tests if the value entered is a well-formed
     * property key.
     */
    CellEditor getKeyEditor() {
        if (this.cellEditor == null) {
            this.cellEditor = createCellEditor();
        }
        this.cellEditor.setEditingKey();
        return this.cellEditor;
    }

    /**
     * Returns a map from default property keys to the properties they should
     * satisfy.
     */
    final Map<String,? extends PropertyKey> getDefaultKeys() {
        return this.defaultKeys;
    }

    /**
     * Returns a (fixed) editor for values of a given key.
     */
    TableCellEditor getValueEditor(String key) {
        if (this.cellEditor == null) {
            this.cellEditor = createCellEditor();
        }
        this.cellEditor.setEditingValueForKey(key);
        return this.cellEditor;
    }

    /**
     * Creates an editor that tests if the value entered is a well-formed
     * property key.
     */
    CellEditor createCellEditor() {
        return new CellEditor();
    }

    /** Flag indicating that the properties are editable. */
    private final boolean editable;
    /** A list of default property keys; possibly <code>null</code>. */
    private final Map<String,? extends PropertyKey> defaultKeys;
    /** The number of non-system keys. */
    private int nonSystemKeyCount;
    /** The actual user properties map. */
    private final SortedMap<String,String> properties;
    /** Underlying data model for any table created in this dialog. */
    private TableModel tableModel;
    /** Returns the cell editor used for cell values. */
    private CellEditor cellEditor;
    /** Flag indicating that the properties have changed. */
    private boolean changed;
    /** Title of the dialog. */
    public static final String DIALOG_TITLE = "Properties editor";
    /** Column header of the property column. */
    public static final String PROPERTY_HEADER = "Property";
    /** Column header of the value column. */
    public static final String VALUE_HEADER = "Value";
    /** Column number of the property column (in the model). */
    private static final int PROPERTY_COLUMN = 0;
    /** Column number of the value column (in the model). */
    private static final int VALUE_COLUMN = 1;
    /** Height of a row in the dialog. */
    private static final int ROW_HEIGHT = 15;

    /** Editor for the cells of the property table. */
    private class CellEditor extends DefaultCellEditor {
        /** Constructs a new property cell editor. */
        public CellEditor() {
            super(new JTextField());
            setClickCountToStart(1);
        }

        private JTextField getTextField() {
            return (JTextField) super.getComponent();
        }

        @Override
        public JTextField getComponent() {
            JTextField result = getTextField();
            if (this.editingValueForKey != null) {
                PropertyKey key = getDefaultKeys().get(this.editingValueForKey);
                if (key != null) {
                    Property<String> test = key.getFormat();
                    result.setToolTipText(test == null ? null : test.toString());
                }
            }
            return result;
        }

        @Override
        public boolean stopCellEditing() {
            boolean result;
            String editedValue = (String) getCellEditorValue();
            if (editedValue.length() == 0 || isEditedValueCorrect(editedValue)) {
                result = super.stopCellEditing();
            } else {
                if (showContinueDialog(editedValue)) {
                    getComponent().setSelectionStart(0);
                    getComponent().setSelectionEnd(
                        getComponent().getText().length());
                    result = false;
                } else {
                    super.cancelCellEditing();
                    result = true;
                }
            }
            return result;
        }

        @Override
        public void cancelCellEditing() {
            super.cancelCellEditing();
        }

        /**
         * Tests if a given non-empty string value is correct. The answer
         * depends on what kind of cell the editor is currently editing.
         */
        private boolean isEditedValueCorrect(String value) {
            if (this.editingValueForKey == null) {
                return GraphProperties.isValidUserKey(value)
                    && !PropertiesTable.this.defaultKeys.containsKey(value);
            } else {
                PropertyKey key = getDefaultKeys().get(this.editingValueForKey);
                Property<String> test = key == null ? null : key.getFormat();
                return test == null || test.isSatisfied(value);
            }
        }

        /**
         * Creates and shows a confirmation dialog for continuing the current
         * edit.
         */
        private boolean showContinueDialog(String value) {
            int response =
                JOptionPane.showConfirmDialog(PropertiesTable.this,
                    getContinueQuestion(value), null, JOptionPane.YES_NO_OPTION);
            return response == JOptionPane.YES_OPTION;
        }

        /** Returns the string to display in the abandon dialog. */
        private String getContinueQuestion(String value) {
            StringBuilder result = new StringBuilder();
            if (this.editingValueForKey == null) {
                // editing a key
                if (PropertiesTable.this.properties.containsKey(value)) {
                    result.append(String.format(
                        "Property key '%s' already exists", value));
                } else {
                    result.append(String.format(
                        "Property key '%s' is not a valid identifier.", value));
                }
            } else {
                // editing a value
                Property<String> test =
                    getDefaultKeys().get(this.editingValueForKey).getFormat();
                String description =
                    test == null ? null : test.getDescription();
                if (description == null) {
                    result.append(String.format(
                        "Incorrect value '%s' for key '%s'.",
                        this.editingValueForKey));
                } else {
                    result.append(String.format("Key '%s' expects %s.",
                        this.editingValueForKey, description));
                }
            }
            result.append(" Continue?");
            return result.toString();
        }

        /** Sets the editor to editing a property key. */
        void setEditingKey() {
            this.editingValueForKey = null;
        }

        /** Sets the editor to edit the value for a given key. */
        void setEditingValueForKey(String key) {
            this.editingValueForKey = key;
        }

        /**
         * Field indicating what the editor is currently editing. If
         * <code>null</code>, it is editing a property key; otherwise, it is
         * editing the value for the key name contained herein.
         */
        private String editingValueForKey;
    }

    /** Renderer class that returns appropriate tool tips. */
    private class CellRenderer extends DefaultTableCellRenderer {
        /**
         * Empty constructor with the correct visibility.
         */
        public CellRenderer() {
            setMinimumSize(new Dimension(0, 2 * ROW_HEIGHT));
            // empty
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            // determine tool tip
            String tip = null;
            String text = (String) value;
            if (getDefaultKeys() != null && row < table.getRowCount()) {
                String keyword =
                    (String) table.getValueAt(row, PROPERTY_COLUMN);
                if (keyword != null && getDefaultKeys().containsKey(keyword)) {
                    PropertyKey key = getDefaultKeys().get(keyword);
                    Property<String> format = key.getFormat();
                    if (column == PROPERTY_COLUMN) {
                        text = key.getDescription();
                        if (format != null) {
                            tip = format.getComment();
                        }
                    } else {
                        if (text == null || text.isEmpty()) {
                            text = key.getDefaultValue();
                        }
                        if (format != null) {
                            tip = format.getDescription();
                        }
                    }
                }
            }
            setToolTipText(tip);
            return super.getTableCellRendererComponent(table, text, isSelected,
                hasFocus, row, column);
        }

    }

    /** Table model with key and value columns. */
    private class TableModel extends AbstractTableModel {
        /**
         * Empty constructor with the correct visibility.
         */
        public TableModel() {
            // empty
        }

        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            int size = getProperties().size();
            return isEditable() ? size + 1 : size;
        }

        @Override
        public String getColumnName(int column) {
            if (column == PROPERTY_COLUMN) {
                return PROPERTY_HEADER;
            } else {
                return VALUE_HEADER;
            }
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex == getProperties().size()) {
                return "";
            } else if (columnIndex == PROPERTY_COLUMN) {
                return getPropertyKey(rowIndex);
            } else {
                return getProperties().get(getPropertyKey(rowIndex));
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (!isEditable()) {
                return false;
            } else if (columnIndex == PROPERTY_COLUMN) {
                return rowIndex >= PropertiesTable.this.nonSystemKeyCount;
            } else { // VALUE_COLUMN
                if (rowIndex >= getProperties().size()) {
                    return false;
                } else {
                    PropertyKey key =
                        getDefaultKeys().get(getPropertyKey(rowIndex));
                    Property<String> property =
                        key == null ? null : key.getFormat();
                    return property == null || property.isEditable();
                }
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (rowIndex == getProperties().size()) {
                // key added
                if (aValue instanceof String && ((String) aValue).length() > 0) {
                    getProperties().put((String) aValue, "");
                    refreshPropertyKeys();
                }
            } else if (columnIndex == VALUE_COLUMN) {
                // value changed
                String keyword = getPropertyKey(rowIndex);
                if (!aValue.equals(getProperties().get(keyword))) {
                    getProperties().put(keyword, (String) aValue);
                    fireTableCellUpdated(rowIndex, columnIndex);
                }
            } else {
                // key changed
                String value = getProperties().remove(getPropertyKey(rowIndex));
                if (aValue instanceof String && ((String) aValue).length() > 0) {
                    getProperties().put((String) aValue, value);
                }
                fireTableCellUpdated(rowIndex, columnIndex);
                refreshPropertyKeys();
            }
        }

        /**
         * Lazily creates and returns a list of property keys, in the order they
         * occur in the properties map.
         */
        private List<String> getPropertyKeyList() {
            if (this.propertyKeyList == null) {
                initPropertyKeys();
            }
            return this.propertyKeyList;
        }

        /**
         * Refreshes the list of property keys according to the current state of
         * the property map, sets the changed flag to <code>true</code> and
         * calls {@link #fireTableDataChanged()}.
         */
        private void refreshPropertyKeys() {
            initPropertyKeys();
            fireTableDataChanged();
        }

        /**
         * Initialises the list of property keys according to the current state
         * of the property map.
         */
        private void initPropertyKeys() {
            this.propertyKeyList =
                new ArrayList<String>(getProperties().keySet());
        }

        /** Retrieves a property key by index. */
        String getPropertyKey(int rowIndex) {
            return getPropertyKeyList().get(rowIndex);
        }

        /** Helper list to translate indices to property keys. */
        private List<String> propertyKeyList;
    }
}
