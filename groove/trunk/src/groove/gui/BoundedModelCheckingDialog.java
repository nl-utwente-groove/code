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
 * $Id: OptimizedBoundedNestedDFSStrategy.java,v 1.2 2008/02/22 13:02:45 rensink Exp $
 */
package groove.gui;

import groove.explore.strategy.Boundary;
import groove.explore.strategy.GraphNodeSizeBoundary;
import groove.explore.strategy.RuleSetBorderBoundary;
import groove.explore.strategy.RuleSetStartBoundary;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.verify.ModelChecking;

import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jhlabs.awt.ParagraphLayout;

/**
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class BoundedModelCheckingDialog {

	JOptionPane createContentPane() {
		Object[] buttons = new Object[] {getOkButton(), getCancelButton()};
		pane = new JOptionPane(createPanel(),JOptionPane.PLAIN_MESSAGE,JOptionPane.OK_CANCEL_OPTION, null, buttons);
		return pane;
	}

	private Panel createPanel() {
		Panel panel = new Panel();
		panel.setLayout(new ParagraphLayout());

		emptyLabel = new JLabel("");
		ButtonGroup group = new ButtonGroup();
		graphBoundButton = new JRadioButton("graph size");
		graphBoundButton.addActionListener(selectionListener);
		graphBoundButton.setSelected(true);
		ruleSetBoundButton = new JRadioButton("rule set");
		ruleSetBoundButton.addActionListener(selectionListener);

		deleteButton = new JButton("<<");
		deleteButton.addActionListener(selectionListener);
		deleteButton.setEnabled(false);
		addButton = new JButton(">>");
		addButton.addActionListener(selectionListener);
		addButton.setEnabled(false);

		ruleList = new JList();
		ruleList.setListData(ruleNames.toArray());
		ruleList.setEnabled(false);
		ruleList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		ruleList.addListSelectionListener(selectionListener);
//		ruleList.setBorder(new Border());
		String[] singleton = {"empty"};
		selectedRuleList = new JList();
		selectedRuleList.setListData(singleton);
		selectedRuleList.setEnabled(false);
		selectedRuleList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		selectedRuleList.addListSelectionListener(selectionListener);
		boundLabel = new JLabel("Initial bound:");
		boundField = new JTextField(20);
		deltaLabel = new JLabel("Delta:");
		deltaField = new JTextField(20);
		group.add(graphBoundButton);
		group.add(ruleSetBoundButton);

		panel.add(graphBoundButton, ParagraphLayout.NEW_PARAGRAPH);

		panel.add(boundLabel, ParagraphLayout.NEW_PARAGRAPH);
		panel.add(boundField);

		panel.add(deltaLabel, ParagraphLayout.NEW_PARAGRAPH);
		panel.add(deltaField);

		panel.add(ruleSetBoundButton, ParagraphLayout.NEW_PARAGRAPH);
		panel.add(emptyLabel, ParagraphLayout.NEW_PARAGRAPH);
		panel.add(ruleList);
		panel.add(deleteButton);
		panel.add(addButton);
		panel.add(selectedRuleList);
		return panel;
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

	public void showDialog(JFrame frame) {
//		createContentPane();
		dialog = createContentPane().createDialog(frame, createTitle());
		dialog.setResizable(true);
//		dialog.setSize(200,200);
		dialog.pack();
		dialog.setVisible(true);
	}

	public String createTitle() {
		return DIALOG_TITLE;
	}

	public Boundary getBoundary() {
		return boundary;
	}

	public void setGrammar(GraphGrammar grammar) {
		this.grammar = grammar;
		ruleNames = new ArrayList<String>();
		for (Rule rule: grammar.getRules()) {
			ruleNames.add(rule.getName().name());
		}
	}

	/**
	 * The bound to be set through the dialog.
	 */
	private int bound;
	/**
	 * The delta to be set through the dialog.
	 */
	private int delta;
	/**
	 * The graph-grammar from which to obtain the rules.
	 */
	private GraphGrammar grammar;
	/**
	 * The set of rules from which to select the boundary.
	 */
	private List<String> ruleNames;
	/**
	 * The set of rules selected for the boundary.
	 */
	private Set<String> selectedRuleNames = new HashSet<String>();
	private Boundary boundary;

	private String DIALOG_TITLE = "Set the boundary";
	JDialog dialog = new JDialog();
	JOptionPane pane;

	private JLabel emptyLabel;
	/** The OK button on the option pane. */
	private JButton okButton;
	/** The CANCEL button on the option pane. */
	private JButton cancelButton;
	private JButton addButton;
	private JButton deleteButton;

	private JLabel boundLabel;
	private JTextField boundField;
	private JLabel deltaLabel;
	private JTextField deltaField;

	private JRadioButton graphBoundButton;
	private JRadioButton ruleSetBoundButton;

	private JList ruleList;
	private JList selectedRuleList;

	private SelectionListener selectionListener = new SelectionListener();

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
			if (e.getSource() == getOkButton()) {
				setBoundary();
			}
			dialog.getContentPane().setVisible(false);
			dialog.dispose();
		}

		public void setBoundary() {
			if (graphBoundButton.isSelected()) {
				int graphBound = Integer.parseInt(boundField.getText());
				int delta = Integer.parseInt(deltaField.getText());
				boundary = new GraphNodeSizeBoundary(graphBound, delta);
			} else if (ruleSetBoundButton.isSelected()) {
				Set<Rule> selectedRules = new HashSet<Rule>();
				Iterator<String> selectedRuleNamesIter = selectedRuleNames.iterator();
				while (selectedRuleNamesIter.hasNext()) {
					String ruleName = selectedRuleNamesIter.next();
					selectedRules.add(grammar.getRule(ruleName));
				}
				if (ModelChecking.START_FROM_BORDER_STATES) {
					boundary = new RuleSetBorderBoundary(selectedRules);
				} else {
					boundary = new RuleSetStartBoundary(selectedRules);
				}
			}
		}
	}

	/** 
	 * Action listener that closes the dialog and makes sure that the 
	 * property is set (possibly to null).
	 */
	private class SelectionListener implements ActionListener,ListSelectionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == graphBoundButton) {
				boundField.setEditable(true);
				deltaField.setEditable(true);
				ruleList.setEnabled(false);
				selectedRuleList.setEnabled(false);
			} else if (e.getSource() == ruleSetBoundButton) {
				ruleList.setEnabled(true);
				selectedRuleList.setEnabled(true);
				boundField.setEditable(false);
				deltaField.setEditable(false);
			} else if (e.getSource() == addButton) {
				for (Object object: ruleList.getSelectedValues()) {
					selectedRuleNames.add(object.toString());
				}
				selectedRuleList.setListData(selectedRuleNames.toArray());
			} else if (e.getSource() == deleteButton) {
				for (Object object: selectedRuleList.getSelectedValues()) {
					selectedRuleNames.remove(object.toString());
				}
				selectedRuleList.setListData(selectedRuleNames.toArray());
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			if (e.getSource() == ruleList) {
				if (ruleList.getSelectedValues().length > 0) {
					addButton.setEnabled(true);
				} else {
					addButton.setEnabled(false);
				}
			} else if (e.getSource() == selectedRuleList) {
				if (selectedRuleList.getSelectedValues().length > 0) {
					deleteButton.setEnabled(true);
				} else {
					deleteButton.setEnabled(false);
				}
			}
		}
	}
}
