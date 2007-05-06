/* $Id: RuleNameDialog.java,v 1.2 2007-05-06 10:47:51 rensink Exp $ */
package groove.gui;

import groove.trans.RuleNameLabel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Dialog class that lets the user choose a fresh rule name.
 * @author Arend Rensink
 * @version $Revision $
 */
class RuleNameDialog {
	/**
	 * Constructs a dialog instance, given a set of existing names (that
	 * should not be used) as well as a suggested value for the new rule name.
	 */
	RuleNameDialog(Set<RuleNameLabel> existingNames) {
		this.existingNames = new HashSet<RuleNameLabel>(existingNames);
	}
	
	/** 
	 * Creates a dialog and makes it visible, so that the user can choose a file name.
	 * The return value indicates if a valid new rule name was input.
	 * @param frame the frame on which the dialog is shown.
	 * @param suggestion the suggested name to start with
	 * @return <code>true</code> if the user agreed with the outcome of the dialog.
	 */
	public boolean showDialog(JFrame frame, RuleNameLabel suggestion) {
		// set the suggested name in the name field
		JTextField nameField = getNameField();
		nameField.setText(suggestion.name());
		nameField.setSelectionStart(0);
		nameField.setSelectionEnd(nameField.getText().length());
		getOkButton().setEnabled(isNameFieldValid());
		JDialog dialog = getOptionPane().createDialog(frame, "Select rule name");
		dialog.setVisible(true);
		Object response = getOptionPane().getValue();
		boolean result = response == getOkButton() || response == getNameField();
		setName(result ? getChosenName() : null);
		return result;
	}
	
	/** 
	 * Lazily creates and returns the option pane that is to form the content
	 * of the dialog.
	 */
	private JOptionPane getOptionPane() {
		if (optionPane == null) {
//			JPanel editPane = new JPanel();
//			editPane.setLayout(new BorderLayout());
//			editPane.add(new JLabel("Rule name: "), BorderLayout.NORTH);
			JTextField nameField = getNameField();
//			editPane.add(nameField);
			optionPane = new JOptionPane(nameField, JOptionPane.PLAIN_MESSAGE,
					JOptionPane.OK_CANCEL_OPTION, null, new Object[] {
							getOkButton(), getCancelButton() });
		}
		return optionPane;
	}

	/**
	 * Returns the OK button on the dialog.
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton("OK");
			okButton.addActionListener(new CloseListener());
		}
		return okButton;
	}
	
	/**
	 * Returns the OK button on the dialog.
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new CloseListener());
		}
		return cancelButton;
	}
	
	/** Returns the text field in which the user is to enter his input. */
	private JTextField getNameField() {
		if (nameField == null) {
			nameField = new JTextField();
			nameField.getDocument().addDocumentListener(new OverlapListener());
			nameField.addActionListener(new CloseListener());
		}
		return nameField;
	}
	
	/** Returns the rule name currently filled in in the name field. */
	private RuleNameLabel getChosenName() {
		return new RuleNameLabel(getNameField().getText());
	}
	
	/**
	 * Returns the name chosen by the user in the course of the dialog.
	 * The return value is guaranteed to be distinct from any of the existing 
	 * names entered at construction time.
	 */
	public final RuleNameLabel getName() {
		return this.name;
	}
	
	/**
	 * Sets the value of the chosen name field.
	 */
	private final void setName(RuleNameLabel name) {
		this.name = name;
	}
	
	/** 
	 * Tests if a given string is a correct value for the new rule name.
	 * and if so, assigns it to the chosen name; if not, sets the chosen name to <code>null</code>.
	 * @return <code>true</code> if <code>chosenName</code> was found to be
	 * a correct value, and assigned to the chose name field
	 */
	private boolean isNameFieldValid() {
		return ! existingNames.contains(getChosenName());
	}
	
	/** The option pane that is the core of the dialog. */
	private JOptionPane optionPane;

	/** The OK button in the dialog. */
	private JButton okButton;

	/** The Cancel button in the dialog. */
	private JButton cancelButton;
	
	/** The text field where the rule name is entered. */
	private JTextField nameField;
	
	/** Set of existing rule names. */
	private final Set<RuleNameLabel> existingNames;

	/** The rule name selected by the user. */
	private RuleNameLabel name;
	
	/** 
	 * Action listener that closes the dialog and sets the option pane's
	 * value to the source of the event, provided the source of the event
	 * is the cancel button, or the value of the text field is a valid rule name.
	 */
	private class CloseListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getCancelButton() || isNameFieldValid()) {
				getOptionPane().setValue(e.getSource());
				getOptionPane().setVisible(false);
			}
		}
	}
	
	/** 
	 * Document listener that enables or disables the OK button,
	 * depending on whether {@link #isNameFieldValid()} returns <code>true</code>.
	 */
	private class OverlapListener implements DocumentListener {
		public void changedUpdate(DocumentEvent e) {
			testNameField();
		}

		public void insertUpdate(DocumentEvent e) {
			testNameField();
		}

		public void removeUpdate(DocumentEvent e) {
			testNameField();
		}

		/** 
		 * Tests if the content of the name field is a good choice of rule name.
		 * The OK button is enabled or disabled as a consequence of this. 
		 */
		private void testNameField() {
			getOkButton().setEnabled(isNameFieldValid());
		}
	}
}
