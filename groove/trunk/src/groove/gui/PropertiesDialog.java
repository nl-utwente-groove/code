/* $Id: PropertiesDialog.java,v 1.6 2007-05-29 21:36:09 rensink Exp $ */
package groove.gui;

import groove.calc.Property;
import groove.util.ListComparator;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
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
public class PropertiesDialog {
	/** 
	 * Constructs an instance of the dialog for a given
	 * parent frame, a given (non-<code>null</code>) set of properties,
	 * and a flag indicating if the properties should be editable.
	 * A further parameter gives a list of default keys that treated 
	 * specially: they are added by default during editing, and are
	 * ordered first in the list.
	 */
	public PropertiesDialog(Properties properties, Map<String, Property<String>> defaultKeys, boolean editable) {
		this.defaultKeys = defaultKeys;
		this.editable = editable;
		if (defaultKeys == null) {
			this.properties = new TreeMap<String,String>();
		} else {
			this.properties = new TreeMap<String,String>(new ListComparator<String>(defaultKeys.keySet()));
			for (String key: defaultKeys.keySet()) {
				this.properties.put(key, "");
			}
		}
		for (Map.Entry property: properties.entrySet()) {
			this.properties.put((String) property.getKey(), (String) property.getValue());
		}
	}
	
	/** 
	 * Constructs an instance of the dialog for a given
	 * parent frame, a given (non-<code>null</code>) set of properties,
	 * and a flag indicating if the properties should be editable.
	 */
	public PropertiesDialog(Properties properties, boolean editable) {
		this(properties, null, editable);
	}
	
	/** 
	 * Makes the dialog visible and awaits the user's response.
	 * Since the dialog is modal, this method returns only 
	 * when the user closes the dialog. The return
	 * value indicates if the properties have changed.
	 * @param frame the frame on which the fialog is to be displayed
	 * @return <code>true</code> if the properties have changed during
	 * the time the dialog was visible.
	 */
	public boolean showDialog(Component frame) {
		boolean result;
		boolean stopDialog;
		do {
			getContentPane().setValue(null);
			getContentPane().setVisible(true);
			JDialog dialog = getContentPane().createDialog(frame, createTitle());
			dialog.setVisible(true);
			dialog.dispose();
			Object selectedValue = getContentPane().getValue();
			if (isChanged()) {
				if (selectedValue == getOkButton()) {
					result = stopDialog = true;
				} else {
					int abandon = showAbandonDialog();
					result = abandon == JOptionPane.YES_OPTION;
					stopDialog = abandon != JOptionPane.CANCEL_OPTION;
				}
			} else {
				// nothing was changed during editing
				result = false;
				stopDialog = true;
			}
		} while (!stopDialog);
		return result;
	}

    /** Creates and shows a confirmation dialog for abandoning the currently edited graph. */
    private int showAbandonDialog() {
		int response = JOptionPane.showConfirmDialog(getContentPane(),
				"Use changed properties?",
				null,
				JOptionPane.YES_NO_CANCEL_OPTION);
		return response;
	}

	/**
	 * Returns the (possibly edited) properties in the dialog.
	 */
	public final Map<String, String> getProperties() {
		Iterator<Map.Entry<String,String>> propertyIter = properties.entrySet().iterator();
		while (propertyIter.hasNext()) {
			if (propertyIter.next().getValue().length() == 0) {
				propertyIter.remove();
			}
		}
		return properties;
	}

	private String createTitle() {
		return DIALOG_TITLE;
	}

	private JOptionPane getContentPane() {
		if (pane == null) {
			int mode;
			Object[] buttons;
			if (editable) {
				mode = JOptionPane.OK_CANCEL_OPTION;
				buttons = new Object[] { getOkButton(), createCancelButton() };
			} else {
				mode = JOptionPane.DEFAULT_OPTION;
				buttons = new Object[] { createCancelButton() };
			}
			pane = new JOptionPane(createTablePane(),
					JOptionPane.PLAIN_MESSAGE, mode, null, buttons);
		}
		return pane;
	}

	/**
	 * Lazily creates and returns a button labelled OK that signals the
	 * editors to stop editing. This makes sure that any partially edited
	 * result is not lost.
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton("OK");
			okButton.addActionListener(new CloseListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					TableCellEditor editor = getTable().getCellEditor();// Component();
					if (editor == null || editor.stopCellEditing()) {
						super.actionPerformed(e);
					}
				}
			});
		}
		return okButton;
	}

	/** Creates and returns a button labelled Cancel. */
	private JButton createCancelButton() {
		JButton result = new JButton("Cancel");
		result.addActionListener(new CloseListener());
		return result;
	}
	
	/** Creates the pane for the table model. */
	public Container createTablePane() {
		JTable table = getTable();
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
		return scrollPane;
	}

	/**
	 * Indicates if the editing session has made a change to the properties.
	 */
	public boolean isChanged() {
		return changed;
	}
	
	/**
	 * Indicates if the properties are editable.
	 * @return <code>true</code> if the properties are editable
	 */
	public boolean isEditable() {
		return editable;
	}
	
	/**
	 * Creates a table of properties, which is editable according to
	 * the ediability of the dialog, set at constuction time.
	 * @see #isEditable()
	 */
	private JTable getTable() {
		if (table == null) {
			final TableModel model = getTableModel();
			table = new JTable(model) {
				@Override
				public TableCellEditor getCellEditor(int row, int column) {
					if (column == PROPERTY_COLUMN) {
						return getKeyEditor();
					} else {
						return getValueEditor(model.getPropertyKey(row));
					}
				}
			};
			table.setPreferredScrollableViewportSize(new Dimension(300, 70));
			table.setDefaultRenderer(table.getColumnClass(PROPERTY_COLUMN), new CellRenderer());
		}
		return table;
	}
	
	/** Creates an instanceof {@link TableModel}. */
	private TableModel getTableModel() {
		if (tableModel == null) {
			tableModel = new TableModel();
			tableModel.addTableModelListener(new TableModelListener() {
				public void tableChanged(TableModelEvent e) {
					changed = true;
//					changed = e.getColumn() == VALUE_COLUMN || e.getType() == TableModelEvent.DELETE;
				}
			});
		}
		return tableModel;
	}
	
	/** 
	 * Returns a (fixed) editor that tests if the value entered is a well-formed 
	 * property key. This is the case if and only if it matches {@link #IDENTIFIER_REGEXPR}.
	 */
	private CellEditor getKeyEditor() {
		if (cellEditor == null) {
			cellEditor = createCellEditor();
		}
		cellEditor.setEditingKey();
		return cellEditor;
	}

	/** 
	 * Returns a (fixed) editor for values of a given key.
	 */
	private TableCellEditor getValueEditor(String key) {
		if (cellEditor == null) {
			cellEditor = new CellEditor();
		}
		cellEditor.setEditingValueForKey(key);
		return cellEditor;
	}

	
	/** 
	 * Creates an editor that tests if the value entered is a well-formed 
	 * property key. This is the case if and only if it matches {@link #IDENTIFIER_REGEXPR}.
	 */
	private CellEditor createCellEditor() {
		if (cellEditor == null) {
			cellEditor = new CellEditor();
		}
		return cellEditor;
	}

	/** The table component. */
	private JTable table;
	/** The option pane creating the dialog. */
	private JOptionPane pane;
	/** The OK button on the option pane. */
	private JButton okButton;
	/** Flag indicating that the properties are editable. */
	private final boolean editable;
	/** A list of default property keys; possibly <code>null</code>. */
	private final Map<String,Property<String>> defaultKeys;
	/** The actual properties map. */
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
	/** Regular expression for matching property keys. */
	private static final String IDENTIFIER_REGEXPR = "(\\p{Alpha}\\p{Alnum}*)?";

	/** 
	 * Action listener that closes the dialog and sets the option pane's
	 * value to the source of the event.
	 */
	private class CloseListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			getContentPane().setValue(e.getSource());
			getContentPane().setVisible(false);
		}
	}
	
	/** Editor for the cells of the property table. */
	private class CellEditor extends DefaultCellEditor {
		/** Constructs a new property cell editor. */
		public CellEditor() {
			super(new JTextField());
			setClickCountToStart(1);
		}
		
		@Override
		public JTextField getComponent() {
			JTextField result = (JTextField) super.getComponent();
			if (editingValueForKey != null) {
				Property<String> test = defaultKeys.get(editingValueForKey);
				result.setToolTipText(test.toString());
			}
			return result;
		}

		@Override
		public boolean stopCellEditing() {
			String editedValue = (String) getCellEditorValue();
			if (editedValue.length() == 0 || isEditedValueCorrect(editedValue)) {
				return super.stopCellEditing();
			} else {
				if (showContinueDialog()) {
					getComponent().setSelectionStart(0);
					getComponent().setSelectionEnd(getComponent().getText().length());
					return false;
				} else {
					super.cancelCellEditing();
					return true;
				}
			}
		}
		
		/** 
		 * Tests if a given non-empty string value is correct.
		 * The answer depends on what kind of cell the editor is currently editing.
		 */
		private boolean isEditedValueCorrect(String value) {
			if (editingValueForKey == null) {
				return value.matches(IDENTIFIER_REGEXPR);
			} else {
				Property<String> test = defaultKeys.get(editingValueForKey);
				return test == null || test.isSatisfied(value);
			}
		}

		/**
		 * Creates and shows a confirmation dialog for continuing the current edit.
		 */
		private boolean showContinueDialog() {
			int response = JOptionPane.showConfirmDialog(getContentPane(),
					getContinueQuestion(),
					null,
					JOptionPane.YES_NO_OPTION);
			return response == JOptionPane.YES_OPTION;
		}
		
		/** Returns the string to display in the abandon dialog. */
		private String getContinueQuestion() {
			if (editingValueForKey == null) {
				// editing a key
				return "Property keys must be identifiers. Continue?";
			} else {
				// editing a value
				Property<String> test = defaultKeys.get(editingValueForKey);
				String description = test == null ? null : test.getDescription();
				if (description == null) {
					return String.format("Incorrect value for key '%s'. Continue?", editingValueForKey);
				} else {
					return String.format("Key '%s' expects %s. Continue?", editingValueForKey, description);
				}
			}
		}
		
		/** Sets the editor to editing a property key. */
		void setEditingKey() {
			editingValueForKey = null;
		}
		
		/** Sets the editor to edit the value for a given key. */
		void setEditingValueForKey(String key) {
			editingValueForKey = key;
		}

		/** 
		 * Field indicating what the editor is currently editing.
		 * If <code>null</code>, it is editing a property key; otherwise,
		 * it is editing the value for the key name contained herein.
		 */
		private String editingValueForKey;
	}
	
	/** Renderer class that returns appropriate tool tips. */
	private class CellRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if (defaultKeys != null && column == PROPERTY_COLUMN && row < table.getRowCount()) {
				String key = (String) table.getValueAt(row, column);
				if (key != null && defaultKeys.get(key) != null) {
					setToolTipText(defaultKeys.get(key).getComment());
				}
			}
			return super.getTableCellRendererComponent(table,
					value,
					isSelected,
					hasFocus,
					row,
					column);
		}
		
	}
	
	/** Table model with key and value columns. */
	private class TableModel extends AbstractTableModel {
		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			int size = properties.size();
			return editable ? size + 1 : size;
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
			if (rowIndex == properties.size()) {
				return "";
			} else if (columnIndex == PROPERTY_COLUMN) {
				return getPropertyKey(rowIndex);
			} else {
				return properties.get(getPropertyKey(rowIndex));
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (! isEditable()) {
				return false; 
			} else if (columnIndex == PROPERTY_COLUMN) {
				return defaultKeys == null || rowIndex >= defaultKeys.size();
			} else {
				return rowIndex < properties.size();
			}
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (rowIndex == properties.size()) {
				// key added
				if (aValue instanceof String && ((String) aValue).length() > 0) {
					properties.put((String) aValue, "");
					refreshPropertyKeys();
				}
			} else if (columnIndex == VALUE_COLUMN) {
				// value changed
				properties.put(getPropertyKey(rowIndex), (String) aValue);
				fireTableCellUpdated(rowIndex, columnIndex);
			} else {
				// key changed
				String value = properties.remove(getPropertyKey(rowIndex));
				if (aValue instanceof String && ((String) aValue).length() > 0) {
					properties.put((String) aValue, value);
				}
				fireTableCellUpdated(rowIndex, columnIndex);
				refreshPropertyKeys();
			}
		}

		/** 
		 * Lazily creates and returns a list of property keys,
		 * in the order they occur in the properties map. 
		 */
		private List<String> getPropertyKeyList() {
			if (propertyKeyList == null) {
				initPropertyKeys();
			}
			return propertyKeyList;
		}
		
		/** 
		 * Refreshes the list of property keys according to the current
		 * state of the property map, sets the changed flag to <code>true</code>
		 * and calls {@link #fireTableDataChanged()}.
		 */
		private void refreshPropertyKeys() {
			initPropertyKeys();
			fireTableDataChanged();
		}
		
		/** 
		 * Initialises the list of property keys according to the current
		 * state of the property map.
		 */
		private void initPropertyKeys() {
			propertyKeyList = new ArrayList<String>(properties.keySet());
		}
		
		/** Retrieves a property key by index. */
		String getPropertyKey(int rowIndex) {
			return getPropertyKeyList().get(rowIndex);
		}

		/** Helper list to translate indices to property keys. */
		private List<String> propertyKeyList;
	}
}
