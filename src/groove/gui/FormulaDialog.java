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
 * $Id: FormulaDialog.java,v 1.9 2008-02-04 08:50:00 kastenberg Exp $
 */
package groove.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Dialog for entering temporal formulae.
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class FormulaDialog {

	/** 
	 * Constructs an instance of the dialog for a given
	 * parent frame, a given (non-<code>null</code>) set of properties,
	 * and a flag indicating if the properties should be editable.
	 * A further parameter gives a list of default keys that treated 
	 * specially: they are added by default during editing, and are
	 * ordered first in the list.
	 */
	public FormulaDialog() {
		this.history = new ArrayList<String>();
		history.add("Select a previous formula.");
	}
	
	/** 
	 * Makes the dialog visible and awaits the user's response.
	 * Since the dialog is modal, this method returns only 
	 * when the user closes the dialog. The return
	 * value indicates if the properties have changed.
	 * @param frame the frame on which the fialog is to be displayed
	 */
	public void showDialog(Component frame) {
		dialog = getContentPane().createDialog(frame, createTitle());
		dialog.pack();
		dialog.setVisible(true);
	}

	/**
	 * @return the title of the dialog
	 */
	private String createTitle() {
		return DIALOG_TITLE;
	}

	/**
	 * Return the property that is entered for verification.
	 * @return the property in String format
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * @return the contentpane
	 */
	JOptionPane getContentPane() {
		Object[] buttons = new Object[] {getOkButton(), getCancelButton()};
		pane = new JOptionPane(createPanel(),JOptionPane.PLAIN_MESSAGE,JOptionPane.OK_CANCEL_OPTION, null, buttons);
		return pane;
	}

	/**
	 * Create and return the main panel.
	 * @return the main panel.
	 */
	private JPanel createPanel() {
		JPanel result = new JPanel();
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		JPanel history = new JPanel();
		JPanel buttons = new JPanel();

		// editing formula
		formulaLabel = new JLabel("Formula:");
		formulaField = new JTextField(30);
		formulaField.addActionListener(new CloseListener());
		panel.add(formulaLabel);
		panel.add(formulaField);

		// select formula from list
		historyBox = new JComboBox(this.history.toArray());
		historyBox.addActionListener(new SelectionListener());
		history.add(historyBox);

		// OK or CANCEL
		okButton = getOkButton();
		cancelButton = getCancelButton();
		buttons.add(okButton);
		buttons.add(cancelButton);

		result.add(panel);
		result.add(history, BorderLayout.AFTER_LAST_LINE);
		result.add(buttons);

		return result;
	}

	/**
	 * Lazily creates and returns a button labelled OK.
	 * @return the ok button
	 */
	JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton("OK");
			okButton.addActionListener(new CloseListener());
		}
		return okButton;
	}

	/**
	 * Lazily creates and returns a button labelled CANCEL.
	 * @return the cancel button
	 */
	JButton getCancelButton(){
		if (cancelButton == null) {
			cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new CloseListener());
		}
		return cancelButton;
	}

	/**
	 * Add an item to the history.
	 * @param item the item to be added
	 */
	void addItem(String item) {
		if (!history.contains(item)) {
			history.add(1, item);
		}
	}

	/** The option pane creating the dialog. */
	private JOptionPane pane;

	/** The formula label */
	private JLabel formulaLabel;
	/** The formula field */
	JTextField formulaField;
	/** The history box */
	JComboBox historyBox;
	/** The OK button on the option pane. */
	private JButton okButton;
	/** The CANCEL button on the option pane. */
	private JButton cancelButton;
	/** Title of the dialog. */
	public static final String DIALOG_TITLE = "Enter Temporal Formula";

	/** The history list */
	private List<String> history;

	/** The field in which to store the provided data */
	String property;

	/** The dialog to be shown */
	JDialog dialog;

	/** 
	 * Action listener that closes the dialog and makes sure that the 
	 * property is set (possibly to null).
	 */
	private class CloseListener implements ActionListener {
		/**
		 * Empty constructor with the correct visibility.
		 */
		public CloseListener() {
			// empty
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getOkButton() || e.getSource() == formulaField || e.getSource() == historyBox) {
				if (!formulaField.getText().equals("")) {
					property = formulaField.getText();
					addItem(property);
				} else {
					if (historyBox.getSelectedIndex() > 0) {
						property = historyBox.getSelectedItem().toString();
					} else {
						JOptionPane.showMessageDialog(dialog, "No formula was selected.", "Formula selection", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
			} else if (e.getSource() == getCancelButton()) {
				property = null;
			}

			getContentPane().setVisible(false);
			dialog.dispose();
		}
	}

	/** 
	 * Action listener that closes the dialog and makes sure that the 
	 * property is set (possibly to null).
	 */
	private class SelectionListener implements ActionListener {
		/**
		 * Empty constructor with the correct visibility.
		 */
		public SelectionListener() {
			// empty
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == historyBox) {
				if (historyBox.getSelectedIndex() > 0) {
					formulaField.setText(historyBox.getSelectedItem().toString());
				}
			}
		}
	}
}
