/* $Id: PropertiesDialog.java,v 1.2 2007-05-02 08:44:32 rensink Exp $ */
package groove.gui;

import groove.graph.GraphProperties;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.DefaultCellEditor;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

/**
 * Dialog for editing a properties map.
 * @author Arend Rensink
 * @version $Revision $
 */
public class PropertiesDialog {
	/** Constructs an instance of the dialog for a given
	 * parent frame, a given (non-<code>null</code>) set of properties,
	 * and a flag indicating if the properties should be editable.
	 */
	public PropertiesDialog(Frame owner, Properties properties, boolean editable) {
		this.frame = owner;
		if (properties instanceof GraphProperties) {
			this.properties = new TreeMap<String,String>(GraphProperties.getKeyComparator());
		} else {
			this.properties = new TreeMap<String,String>();
		}
		for (Map.Entry property: properties.entrySet()) {
			this.properties.put((String) property.getKey(), (String) property.getValue());
		}
		this.editable = editable;
		pane = createContentPane();
	}
	
	/** 
	 * Makes the dialog visible, awaits the user's response,
	 * and returns a value indicating if the properties have changed.
	 */
	public boolean showDialog() {
		int response;
		JDialog dialog = pane.createDialog(frame, createTitle());
//		dialog.setLocationRelativeTo(frame);
		do {
			response = JOptionPane.CLOSED_OPTION;
			dialog.setVisible(true);
			Object selectedValue = pane.getValue();
			if (selectedValue instanceof Integer) {
				response = (Integer) selectedValue;
			}
			if (changed && response != JOptionPane.OK_OPTION) {
				response = showAbandonDialog();
			}
		} while (changed && response == JOptionPane.CANCEL_OPTION);
		dialog.dispose();
		return response == JOptionPane.OK_OPTION || response == JOptionPane.YES_OPTION;
	}

    /** Creates and shows a confirmation dialog for abandoning the currently edited graph. */
    private int showAbandonDialog() {
		int response = JOptionPane.showConfirmDialog(frame,
				"Use changed properties?",
				null,
				JOptionPane.YES_NO_CANCEL_OPTION);
		return response;
	}

	/**
	 * Returns the (possibly edited) properties in the dialog.
	 */
	public final SortedMap<String, String> getProperties() {
		return this.properties;
	}

	private String createTitle() {
		return DIALOG_TITLE;
	}

	private JOptionPane createContentPane() {
		int buttons = editable ? JOptionPane.OK_CANCEL_OPTION : JOptionPane.DEFAULT_OPTION;
		return new JOptionPane(createTablePane(), JOptionPane.PLAIN_MESSAGE,
				buttons, null);
	}
	
	/** Creates the pane for the table model. */
	public Container createTablePane() {
		JTable table = createTable();
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
		return scrollPane;
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
	public JTable createTable() {
		final JTable table = new JTable(createTableModel());
		table.setRowSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setPreferredScrollableViewportSize(new Dimension(300, 70));
		DefaultCellEditor cellEditor = new DefaultCellEditor(new JTextField());
		cellEditor.setClickCountToStart(1);
		table.setCellEditor(cellEditor);
		table.getColumnModel().getColumn(PROPERTY_COLUMN).setCellEditor(createKeyEditor());
		return table;
	}
	
	/** 
	 * Creates an editor that tests if the value entered is a well-formed 
	 * property key. This is the case if and only if it matches {@link #IDENTIFIER_REGEXPR}.
	 */
	private TableCellEditor createKeyEditor() {
		DefaultCellEditor result = new DefaultCellEditor(new JTextField()) {
			@Override
			public boolean stopCellEditing() {
				if (!((String) getCellEditorValue()).matches(IDENTIFIER_REGEXPR)) {
					return showAbandonDialog() == JOptionPane.YES_OPTION;
				} else {
					return super.stopCellEditing();
				}
			}
			
		    /** Creates and shows a confirmation dialog for abandoning the currently edited graph. */
		    private int showAbandonDialog() {
				int response = JOptionPane.showConfirmDialog(frame,
						"Not a valid property key. Abandon?",
						null,
						JOptionPane.YES_NO_OPTION);
				return response;
			}
		};
		result.setClickCountToStart(1);
		return result;
	}

	/** Creates an instanceof {@link TableModel}. */
	private javax.swing.table.TableModel createTableModel() {
		return new TableModel();
	}
	
	/** The parent fraom of the dialog. */
	private final Frame frame;
	/** The option pane creating the dialog. */
	private final JOptionPane pane;
	/** Flag indicating that the properties are editable. */
	private final boolean editable;
	/** The ectual properties map. */
	private final SortedMap<String,String> properties;
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
	private static final String IDENTIFIER_REGEXPR = "(\\p{Alpha}\\p{Alnum}*)?";
	
	private class TableModel extends AbstractTableModel {
		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			int size = properties.size();
			return editable ? size + 1 : size;
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
			return editable && (rowIndex < properties.size() || columnIndex == PROPERTY_COLUMN);
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (rowIndex == properties.size()) {
				if (aValue instanceof String && ((String) aValue).length() > 0) {
					properties.put((String) aValue, "");
					refreshPropertyKeys();
				}
			} else if (columnIndex == VALUE_COLUMN) {
				properties.put(getPropertyKey(rowIndex), (String) aValue);
				fireTableCellUpdated(rowIndex, columnIndex);
			} else {
				String value = properties.remove(getPropertyKey(rowIndex));
				if (aValue instanceof String && ((String) aValue).length() > 0) {
					properties.put((String) aValue, value);
				}
				refreshPropertyKeys();
			}
		}

		@Override
		public String getColumnName(int column) {
			if (column == PROPERTY_COLUMN) {
				return PROPERTY_HEADER;
			} else {
				return VALUE_HEADER;
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
			changed = true;
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
		private String getPropertyKey(int rowIndex) {
			return getPropertyKeyList().get(rowIndex);
		}

		/** Helper list to translate indices to property keys. */
		private List<String> propertyKeyList;
	}
}
