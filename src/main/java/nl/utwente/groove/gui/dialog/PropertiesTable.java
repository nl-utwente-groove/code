/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.gui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import nl.utwente.groove.gui.display.DismissDelayer;
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.io.HTMLConverter.HTMLTag;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.Properties;
import nl.utwente.groove.util.Properties.CheckerMap;
import nl.utwente.groove.util.Properties.Key;
import nl.utwente.groove.util.collect.IndexComparator;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.Parser;

/**
 * Dialog for editing a properties map.
 * @author Arend Rensink
 * @version $Revision$
 */
public class PropertiesTable extends JTable {
    /**
     * Constructs an instance of the dialog for a given parent frame, a given
     * (non-<code>null</code>) set of properties, and a flag indicating if
     * the properties should be editable. A further parameter gives a list of
     * default keys that treated specially: they are added by default during
     * editing, and are ordered first in the list.
     */
    public PropertiesTable(Class<? extends Key> defaultKeys, boolean editable) {
        this.editable = editable;
        this.defaultKeys = new LinkedHashMap<>();
        for (var key : defaultKeys.getEnumConstants()) {
            this.defaultKeys.put(key.getName(), key);
        }
        this.properties = new TreeMap<>(new IndexComparator<>(this.defaultKeys.keySet()));
        this.keyIndexMap = new HashMap<>();
        this.errorMap = new HashMap<>();
        final TableModel model = getTableModel();
        setModel(model);
        setIntercellSpacing(new Dimension(2, -2));
        setDefaultRenderer(getColumnClass(PROPERTY_COLUMN), new CellRenderer());
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        addMouseListener(new DismissDelayer(this));
    }

    /** Changes the underlying properties. */
    public void setProperties(Properties properties) {
        this.properties.clear();
        this.keyIndexMap.clear();
        this.checkerMap = new CheckerMap();
        this.errorMap.clear();
        for (var key : this.defaultKeys.values()) {
            if (!key.isSystem() || key.isDerived()) {
                this.keyIndexMap.put(key, this.keyIndexMap.size());
                this.properties.put(key.getName(), "");
            }
        }
        properties.entryStream().forEach(e -> {
            var key = getKey(e.getKey());
            if (key == null || !key.isSystem() || key.isDerived()) {
                this.properties.put(e.getKey(), e.getValue());
            }
        });
        setPreferredScrollableViewportSize(new Dimension(300,
            Math.max((getModel().getRowCount() + 2) * ROW_HEIGHT, 80)));
        setEnabled(true);
        getTableModel().refreshPropertyKeys();
        setChanged(false);
    }

    /**
     * Sets a checker map for the properties that were set before.
     * If no checker map is set, the empty map is used.
     * Should be (re)invoked after {@link #setProperties(Properties)}
     */
    public void setCheckerMap(CheckerMap checkerMap) {
        this.checkerMap = checkerMap;
        this.errorMap.clear();
        for (var key : this.defaultKeys.values()) {
            check(key);
        }
    }

    /** Resets the underlying properties. */
    public void resetProperties() {
        this.properties.clear();
        setChanged(false);
        setEnabled(false);
        repaint();
    }

    /**
     * Returns an alias to the properties object in the dialog.
     */
    final Map<String,String> aliasProperties() {
        return this.properties;
    }

    /**
     * Returns a copy of the properties object in the dialog,
     * with all default values removed.
     */
    final public Map<String,String> getProperties() {
        Map<String,String> result = new HashMap<>();
        // only copy non-default properties
        for (Map.Entry<String,String> entry : this.properties.entrySet()) {
            String stringKey = entry.getKey();
            String value = entry.getValue();
            var key = this.defaultKeys.get(stringKey);
            if (key == null || !key.parser().parsesToDefault(value)) {
                result.put(stringKey, value);
            }
        }
        return result;
    }

    /** The actual user properties map. */
    private final SortedMap<String,String> properties;

    /** The row number of non-system keys. */
    private final Map<Key,Integer> keyIndexMap;

    private CheckerMap checkerMap;

    /**
     * Checks the value currently entered for a given key,
     * and puts the resulting errors into the error map.
     */
    void check(Key key) {
        String value = Groove.orElse(this.properties.get(key.getName()), "");
        this.errorMap.put(key, this.checkerMap.get(key).check(value));
    }

    private final Map<Key,FormatErrorSet> errorMap;

    /**
     * Indicates if the properties are editable.
     * @return <code>true</code> if the properties are editable
     */
    public boolean isEditable() {
        return this.editable;
    }

    /** Flag indicating that the properties are editable. */
    private final boolean editable;

    /** Creates an instance of {@link TableModel}. */
    private TableModel getTableModel() {
        if (this.tableModel == null) {
            this.tableModel = new TableModel();
            this.tableModel.addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    setChanged(true);
                }
            });
        }
        return this.tableModel;
    }

    /** Underlying data model for any table created in this dialog. */
    private TableModel tableModel;

    /**
     * Indicates if the editing session has made a change to the properties.
     */
    public boolean isChanged() {
        return this.changed;
    }

    /** Sets the value of the changed field. */
    void setChanged(boolean changed) {
        this.changed = changed;
    }

    /** Flag indicating that the properties have changed. */
    private boolean changed;

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        if (column == PROPERTY_COLUMN) {
            return getKeyEditor();
        } else {
            return getValueEditor(((TableModel) getModel()).getPropertyKey(row));
        }
    }

    /**
     * Returns a (fixed) editor that tests if the value entered is a well-formed
     * property key.
     */
    CellEditor getKeyEditor() {
        if (this.cellEditor == null) {
            this.cellEditor = createCellEditor();
        }
        this.cellEditor.setEditingValueForKey(null);
        return this.cellEditor;
    }

    /**
     * Creates an editor that tests if the value entered is a well-formed
     * property key.
     */
    CellEditor createCellEditor() {
        return new CellEditor();
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

    /** Returns the cell editor used for cell values. */
    private CellEditor cellEditor;

    /** Returns the key with a given name, if any. */
    Key getKey(String name) {
        return this.defaultKeys.get(name);
    }

    /** Checks if the table has a notable value for a given property key. */
    boolean hasNotableValue(Key key) {
        String value = this.properties.get(key.getName());
        return key.isNotable() && value != null && !key.parsesToDefault(value);
    }

    /** A list of default property keys; possibly <code>null</code>. */
    private final Map<String,Key> defaultKeys;

    /** Sets the selection to a given property key. */
    public void setSelected(Key key) {
        if (this.keyIndexMap.containsKey(key)) {
            int index = this.keyIndexMap.get(key);
            getSelectionModel().setSelectionInterval(index, index);
        }
    }

    /** Returns the errors in the value at the currently selected row. */
    public FormatErrorSet getSelectedErrors() {
        FormatErrorSet result = null;
        int row = getSelectedRow();
        if (row >= 0) {
            var key = getKey((String) getValueAt(row, PROPERTY_COLUMN));
            if (key != null) {
                result = this.errorMap.get(key);
            }
        }
        return result == null
            ? new FormatErrorSet()
            : result;
    }

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
    public class CellEditor extends DefaultCellEditor {
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
                var key = getKey(this.editingValueForKey);
                if (key != null) {
                    Parser<?> parser = key.parser();
                    String tip = parser.getDescription();
                    result.setToolTipText(HTMLConverter.HTML_TAG.on(tip));
                }
            }
            return result;
        }

        /** Sets the editor to edit the value for a given key. */
        void setEditingValueForKey(String keyword) {
            this.editingValueForKey = keyword;
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
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            // determine tool tip
            String tip = null;
            String text = (String) value;
            boolean error = false;
            boolean isNotable = false;
            if (row < table.getRowCount()) {
                String keyword = (String) table.getValueAt(row, PROPERTY_COLUMN);
                var key = getKey(keyword);
                if (key != null) {
                    if (column == PROPERTY_COLUMN) {
                        text = key.getKeyPhrase();
                        tip = key.getExplanation();
                    } else {
                        tip = key.parser().getDescription();
                    }
                    FormatErrorSet errors = PropertiesTable.this.errorMap.get(key);
                    error = errors != null && !errors.isEmpty();
                    if (error) {
                        assert errors != null; // guaranteed by error
                        for (FormatError err : errors) {
                            tip += HTMLConverter.HTML_LINEBREAK;
                            tip += this.errorTag.on(HTMLConverter.toHtml(err));
                        }
                    } else {
                        isNotable = hasNotableValue(key);
                    }
                }
            }
            setToolTipText(tip == null
                ? null
                : HTMLConverter.HTML_TAG.on(tip));
            super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);

            Values.ColorSet colors = error
                ? Values.ERROR_COLORS
                : isNotable
                    ? Values.INFO_COLORS
                    : Values.NORMAL_COLORS;
            Color foreground = colors.getForeground(isSelected, hasFocus);
            setForeground(foreground);
            Color background = colors.getBackground(isSelected, hasFocus);
            if (background == Color.WHITE) {
                background = null;
            }
            setBackground(background);
            return this;
        }

        private final HTMLTag errorTag
            = HTMLConverter.createColorTag(Values.ERROR_NORMAL_FOREGROUND);
    }

    /** Table model with key and value columns. */
    private class TableModel extends AbstractTableModel {
        /**
         * Empty constructor with the correct visibility.
         */
        public TableModel() {
            // empty
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public int getRowCount() {
            int size = aliasProperties().size();
            return isEditable()
                ? size + 1
                : size;
        }

        @Override
        public String getColumnName(int column) {
            if (column == PROPERTY_COLUMN) {
                return PROPERTY_HEADER;
            } else {
                return VALUE_HEADER;
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex == aliasProperties().size()) {
                return "";
            } else if (columnIndex == PROPERTY_COLUMN) {
                return getPropertyKey(rowIndex);
            } else {
                return aliasProperties().get(getPropertyKey(rowIndex));
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (!isEditable()) {
                return false;
            } else if (columnIndex == PROPERTY_COLUMN) {
                return rowIndex >= PropertiesTable.this.keyIndexMap.size();
            } else { // VALUE_COLUMN
                if (rowIndex >= aliasProperties().size()) {
                    return false;
                } else {
                    var key = getKey(getPropertyKey(rowIndex));
                    return key == null || !key.isSystem();
                }
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            String value = (String) aValue;
            if (rowIndex == aliasProperties().size()) {
                // key added
                if (value.length() > 0) {
                    aliasProperties().put(value, "");
                    refreshPropertyKeys();
                }
            } else if (columnIndex == VALUE_COLUMN) {
                // value changed
                String keyword = getPropertyKey(rowIndex);
                if (!value.equals(aliasProperties().get(keyword))) {
                    aliasProperties().put(keyword, value);
                    var key = getKey(keyword);
                    if (key != null) {
                        check(key);
                    }
                    // also update the property column because the error status may have changed
                    fireTableCellUpdated(rowIndex, PROPERTY_COLUMN);
                    fireTableCellUpdated(rowIndex, VALUE_COLUMN);
                }
            } else {
                // key changed
                String oldValue = aliasProperties().remove(getPropertyKey(rowIndex));
                if (!value.isEmpty()) {
                    aliasProperties().put(value, oldValue);
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
            this.propertyKeyList = new ArrayList<>(aliasProperties().keySet());
        }

        /** Retrieves a property key by index. */
        String getPropertyKey(int rowIndex) {
            return getPropertyKeyList().get(rowIndex);
        }

        /** Helper list to translate indices to property keys. */
        private List<String> propertyKeyList;
    }
}
