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
 * $Id: OptimizedBoundedNestedDFSStrategy.java,v 1.2 2008/02/22 13:02:45 rensink
 * Exp $
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
        this.pane =
            new JOptionPane(createPanel(), JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION, null, buttons);
        return this.pane;
    }

    private Panel createPanel() {
        Panel panel = new Panel();
        panel.setLayout(new ParagraphLayout());

        this.emptyLabel = new JLabel("");
        ButtonGroup group = new ButtonGroup();
        this.graphBoundButton = new JRadioButton("graph size");
        this.graphBoundButton.addActionListener(this.selectionListener);
        this.graphBoundButton.setSelected(true);
        this.ruleSetBoundButton = new JRadioButton("rule set");
        this.ruleSetBoundButton.addActionListener(this.selectionListener);

        this.deleteButton = new JButton("<<");
        this.deleteButton.addActionListener(this.selectionListener);
        this.deleteButton.setEnabled(false);
        this.addButton = new JButton(">>");
        this.addButton.addActionListener(this.selectionListener);
        this.addButton.setEnabled(false);

        this.ruleList = new JList();
        this.ruleList.setListData(this.ruleNames.toArray());
        this.ruleList.setEnabled(false);
        this.ruleList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.ruleList.addListSelectionListener(this.selectionListener);
        // ruleList.setBorder(new Border());
        String[] singleton = {"empty"};
        this.selectedRuleList = new JList();
        this.selectedRuleList.setListData(singleton);
        this.selectedRuleList.setEnabled(false);
        this.selectedRuleList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.selectedRuleList.addListSelectionListener(this.selectionListener);
        this.boundLabel = new JLabel("Initial bound:");
        this.boundField = new JTextField(20);
        this.deltaLabel = new JLabel("Delta:");
        this.deltaField = new JTextField(20);
        group.add(this.graphBoundButton);
        group.add(this.ruleSetBoundButton);

        panel.add(this.graphBoundButton, ParagraphLayout.NEW_PARAGRAPH);

        panel.add(this.boundLabel, ParagraphLayout.NEW_PARAGRAPH);
        panel.add(this.boundField);

        panel.add(this.deltaLabel, ParagraphLayout.NEW_PARAGRAPH);
        panel.add(this.deltaField);

        panel.add(this.ruleSetBoundButton, ParagraphLayout.NEW_PARAGRAPH);
        panel.add(this.emptyLabel, ParagraphLayout.NEW_PARAGRAPH);
        panel.add(this.ruleList);
        panel.add(this.deleteButton);
        panel.add(this.addButton);
        panel.add(this.selectedRuleList);
        return panel;
    }

    /**
     * Lazily creates and returns a button labelled OK.
     * @return the ok button
     */
    JButton getOkButton() {
        if (this.okButton == null) {
            this.okButton = new JButton("OK");
            this.okButton.addActionListener(new CloseListener());
        }
        return this.okButton;
    }

    /**
     * Lazily creates and returns a button labelled CANCEL.
     * @return the cancel button
     */
    JButton getCancelButton() {
        if (this.cancelButton == null) {
            this.cancelButton = new JButton("Cancel");
            this.cancelButton.addActionListener(new CloseListener());
        }
        return this.cancelButton;
    }

    /**
     * Shows the dialog that requests the boundary.
     * @param frame
     */
    public void showDialog(JFrame frame) {
        // createContentPane();
        this.dialog = createContentPane().createDialog(frame, createTitle());
        this.dialog.setResizable(true);
        // dialog.setSize(200,200);
        this.dialog.pack();
        this.dialog.setVisible(true);
    }

    private String createTitle() {
        return this.DIALOG_TITLE;
    }

    /**
     * Gives the boundary inserted in the dialog
     * @return the inserted boundary
     */
    public Boundary getBoundary() {
        return this.boundary;
    }

    /**
     * Set the grammar for which a boundary is to be given.
     * @param grammar
     */
    public void setGrammar(GraphGrammar grammar) {
        this.grammar = grammar;
        this.ruleNames = new ArrayList<String>();
        for (Rule rule : grammar.getRules()) {
            this.ruleNames.add(rule.getName().name());
        }
    }

    //
    // /**
    // * The bound to be set through the dialog.
    // */
    // private int bound;
    // /**
    // * The delta to be set through the dialog.
    // */
    // private int delta;
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
    private final Set<String> selectedRuleNames = new HashSet<String>();
    private Boundary boundary;

    private final String DIALOG_TITLE = "Set the boundary";
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

    private final SelectionListener selectionListener = new SelectionListener();

    /**
     * Action listener that closes the dialog and makes sure that the property
     * is set (possibly to null).
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
            BoundedModelCheckingDialog.this.dialog.getContentPane().setVisible(
                false);
            BoundedModelCheckingDialog.this.dialog.dispose();
        }

        private void setBoundary() {
            if (BoundedModelCheckingDialog.this.graphBoundButton.isSelected()) {
                int graphBound =
                    Integer.parseInt(BoundedModelCheckingDialog.this.boundField.getText());
                int delta =
                    Integer.parseInt(BoundedModelCheckingDialog.this.deltaField.getText());
                BoundedModelCheckingDialog.this.boundary =
                    new GraphNodeSizeBoundary(graphBound, delta);
            } else if (BoundedModelCheckingDialog.this.ruleSetBoundButton.isSelected()) {
                Set<Rule> selectedRules = new HashSet<Rule>();
                Iterator<String> selectedRuleNamesIter =
                    BoundedModelCheckingDialog.this.selectedRuleNames.iterator();
                while (selectedRuleNamesIter.hasNext()) {
                    String ruleName = selectedRuleNamesIter.next();
                    selectedRules.add(BoundedModelCheckingDialog.this.grammar.getRule(ruleName));
                }
                if (ModelChecking.START_FROM_BORDER_STATES) {
                    BoundedModelCheckingDialog.this.boundary =
                        new RuleSetBorderBoundary(selectedRules);
                } else {
                    BoundedModelCheckingDialog.this.boundary =
                        new RuleSetStartBoundary(selectedRules);
                }
            }
        }
    }

    /**
     * Action listener that closes the dialog and makes sure that the property
     * is set (possibly to null).
     */
    private class SelectionListener implements ActionListener,
            ListSelectionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == BoundedModelCheckingDialog.this.graphBoundButton) {
                BoundedModelCheckingDialog.this.boundField.setEditable(true);
                BoundedModelCheckingDialog.this.deltaField.setEditable(true);
                BoundedModelCheckingDialog.this.ruleList.setEnabled(false);
                BoundedModelCheckingDialog.this.selectedRuleList.setEnabled(false);
            } else if (e.getSource() == BoundedModelCheckingDialog.this.ruleSetBoundButton) {
                BoundedModelCheckingDialog.this.ruleList.setEnabled(true);
                BoundedModelCheckingDialog.this.selectedRuleList.setEnabled(true);
                BoundedModelCheckingDialog.this.boundField.setEditable(false);
                BoundedModelCheckingDialog.this.deltaField.setEditable(false);
            } else if (e.getSource() == BoundedModelCheckingDialog.this.addButton) {
                for (Object object : BoundedModelCheckingDialog.this.ruleList.getSelectedValues()) {
                    BoundedModelCheckingDialog.this.selectedRuleNames.add(object.toString());
                }
                BoundedModelCheckingDialog.this.selectedRuleList.setListData(BoundedModelCheckingDialog.this.selectedRuleNames.toArray());
            } else if (e.getSource() == BoundedModelCheckingDialog.this.deleteButton) {
                for (Object object : BoundedModelCheckingDialog.this.selectedRuleList.getSelectedValues()) {
                    BoundedModelCheckingDialog.this.selectedRuleNames.remove(object.toString());
                }
                BoundedModelCheckingDialog.this.selectedRuleList.setListData(BoundedModelCheckingDialog.this.selectedRuleNames.toArray());
            }
        }

        public void valueChanged(ListSelectionEvent e) {
            if (e.getSource() == BoundedModelCheckingDialog.this.ruleList) {
                if (BoundedModelCheckingDialog.this.ruleList.getSelectedValues().length > 0) {
                    BoundedModelCheckingDialog.this.addButton.setEnabled(true);
                } else {
                    BoundedModelCheckingDialog.this.addButton.setEnabled(false);
                }
            } else if (e.getSource() == BoundedModelCheckingDialog.this.selectedRuleList) {
                if (BoundedModelCheckingDialog.this.selectedRuleList.getSelectedValues().length > 0) {
                    BoundedModelCheckingDialog.this.deleteButton.setEnabled(true);
                } else {
                    BoundedModelCheckingDialog.this.deleteButton.setEnabled(false);
                }
            }
        }
    }
}
