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
package groove.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import groove.explore.strategy.Boundary;
import groove.explore.strategy.GraphNodeSizeBoundary;
import groove.explore.strategy.RuleSetBoundary;
import groove.grammar.Action;
import groove.grammar.Grammar;
import groove.grammar.QualName;
import groove.grammar.Rule;
import groove.gui.layout.SpringUtilities;

/**
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class BoundedModelCheckingDialog {

    JOptionPane createContentPane() {
        Object[] buttons = new Object[] {getOkButton(), getCancelButton()};
        this.pane = new JOptionPane(createPanel(), JOptionPane.PLAIN_MESSAGE,
            JOptionPane.OK_CANCEL_OPTION, null, buttons);
        return this.pane;
    }

    private JPanel createPanel() {
        SpringLayout layout = new SpringLayout();
        JPanel panel = new JPanel(layout);

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

        this.ruleList = new JList<>();
        this.ruleList.setListData(this.ruleNames.toArray(new String[this.ruleNames.size()]));
        this.ruleList.setEnabled(false);
        this.ruleList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.ruleList.addListSelectionListener(this.selectionListener);
        String[] singleton = {"empty"};
        this.selectedRuleList = new JList<>();
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

        panel.add(this.graphBoundButton);
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        panel.add(this.boundLabel);
        panel.add(this.boundField);
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        panel.add(this.deltaLabel);
        panel.add(this.deltaField);
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        panel.add(this.ruleSetBoundButton);
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        panel.add(this.ruleList);
        panel.add(this.deleteButton);
        panel.add(this.addButton);
        panel.add(this.selectedRuleList);
        SpringUtilities.makeCompactGrid(panel, 5, 5, 5, 5, 10, 10);

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
     */
    public void showDialog(JFrame frame) {
        this.dialog = createContentPane().createDialog(frame, createTitle());
        this.dialog.setResizable(true);
        this.dialog.pack();
        this.dialog.setVisible(true);
    }

    private String createTitle() {
        return DIALOG_TITLE;
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
     */
    public void setGrammar(Grammar grammar) {
        this.grammar = grammar;
        this.ruleNames = new ArrayList<>();
        for (Action rule : grammar.getActions()) {
            this.ruleNames.add(rule.getQualName());
        }
    }

    /**
     * The graph-grammar from which to obtain the rules.
     */
    protected Grammar grammar;
    /**
     * The set of rules from which to select the boundary.
     */
    private List<QualName> ruleNames;
    /**
     * The set of rules selected for the boundary.
     */
    protected final Set<QualName> selectedRuleNames = new HashSet<>();
    private Boundary boundary;

    private static final String DIALOG_TITLE = "Set the boundary";
    JDialog dialog = new JDialog();
    JOptionPane pane;

    /** The OK button on the option pane. */
    private JButton okButton;
    /** The CANCEL button on the option pane. */
    private JButton cancelButton;
    private JButton addButton;
    /** The Delete button on the option pane. */
    protected JButton deleteButton;

    private JLabel boundLabel;
    private JTextField boundField;
    private JLabel deltaLabel;
    private JTextField deltaField;

    private JRadioButton graphBoundButton;
    private JRadioButton ruleSetBoundButton;

    private JList<String> ruleList;
    private JList<String> selectedRuleList;

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

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (e.getSource() == getOkButton()) {
                    setBoundary();
                }
                BoundedModelCheckingDialog.this.dialog.getContentPane()
                    .setVisible(false);
                BoundedModelCheckingDialog.this.dialog.dispose();
            } catch (NumberFormatException e1) {
                // invalid entries in the dialog, do not do anything
            }
        }

        private void setBoundary() {
            BoundedModelCheckingDialog aDialog = BoundedModelCheckingDialog.this;
            if (aDialog.graphBoundButton.isSelected()) {
                int graphBound = Integer.parseInt(aDialog.boundField.getText());
                int delta = Integer.parseInt(aDialog.deltaField.getText());
                aDialog.boundary = new GraphNodeSizeBoundary(graphBound, delta);
            } else if (aDialog.ruleSetBoundButton.isSelected()) {
                Set<Rule> selectedRules = new HashSet<>();
                Iterator<QualName> selectedRuleNamesIter = aDialog.selectedRuleNames.iterator();
                while (selectedRuleNamesIter.hasNext()) {
                    QualName ruleName = selectedRuleNamesIter.next();
                    selectedRules.add(aDialog.grammar.getRule(ruleName));
                }
                aDialog.boundary = new RuleSetBoundary(selectedRules);
            }
        }
    }

    /**
     * Action listener that closes the dialog and makes sure that the property
     * is set (possibly to null).
     */
    private class SelectionListener implements ActionListener, ListSelectionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            BoundedModelCheckingDialog aDialog = BoundedModelCheckingDialog.this;
            if (e.getSource() == aDialog.graphBoundButton) {
                aDialog.boundField.setEditable(true);
                aDialog.deltaField.setEditable(true);
                aDialog.ruleList.setEnabled(false);
                aDialog.selectedRuleList.setEnabled(false);
            } else if (e.getSource() == aDialog.ruleSetBoundButton) {
                aDialog.ruleList.setEnabled(true);
                aDialog.selectedRuleList.setEnabled(true);
                aDialog.boundField.setEditable(false);
                aDialog.deltaField.setEditable(false);
            } else if (e.getSource() == aDialog.addButton) {
                for (String name : aDialog.ruleList.getSelectedValuesList()) {
                    aDialog.selectedRuleNames.add(QualName.parse(name));
                }
                aDialog.selectedRuleList.setListData(aDialog.selectedRuleNames
                    .toArray(new String[aDialog.selectedRuleNames.size()]));
            } else if (e.getSource() == aDialog.deleteButton) {
                for (String name : aDialog.selectedRuleList.getSelectedValuesList()) {
                    aDialog.selectedRuleNames.remove(QualName.parse(name));
                }
                aDialog.selectedRuleList.setListData(aDialog.selectedRuleNames.stream()
                    .map(n -> n.toString())
                    .collect(Collectors.toList())
                    .toArray(new String[0]));
            }
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            BoundedModelCheckingDialog aDialog = BoundedModelCheckingDialog.this;
            if (e.getSource() == aDialog.ruleList) {
                if (aDialog.ruleList.getSelectedValuesList()
                    .size() > 0) {
                    aDialog.addButton.setEnabled(true);
                } else {
                    aDialog.addButton.setEnabled(false);
                }
            } else if (e.getSource() == aDialog.selectedRuleList) {
                if (aDialog.selectedRuleList.getSelectedValuesList()
                    .size() > 0) {
                    aDialog.deleteButton.setEnabled(true);
                } else {
                    aDialog.deleteButton.setEnabled(false);
                }
            }
        }
    }
}
