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
 * $Id$
 */
package groove.gui.dialog;

import groove.explore.AcceptorEnumerator;
import groove.explore.Documented;
import groove.explore.Enumerator;
import groove.explore.Scenario;
import groove.explore.ScenarioFactory;
import groove.explore.StrategyEnumerator;
import groove.explore.result.Acceptor;
import groove.explore.result.FinalStateAcceptor;
import groove.explore.result.InvariantViolatedAcceptor;
import groove.explore.result.Result;
import groove.explore.strategy.BFSStrategy;
import groove.explore.strategy.BranchingStrategy;
import groove.explore.strategy.ExploreRuleDFStrategy;
import groove.explore.strategy.LinearConfluentRules;
import groove.explore.strategy.LinearStrategy;
import groove.explore.strategy.RandomLinearStrategy;
import groove.explore.strategy.Strategy;
import groove.gui.Simulator;
import groove.gui.layout.SpringUtilities;
import groove.trans.Rule;
import groove.trans.RuleName;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author Maarten de Mol
 * @version $Revision $ Creates a dialog in which an exploration method can be
 *          composed out of an arbitrary combination of a Strategy and an
 *          Acceptor.
 */
public class ExplorationDialog extends JDialog implements ActionListener {
    private static final String EXPLORE_COMMAND = "Explore State Space";        // button text/command
    private static final String CANCEL_COMMAND = "Cancel";                      // button text/command

    private FormattedSelection strategyPanel;
    private JPanel acceptorPanel;
    private JPanel resultPanel;
    private JPanel buttonPanel;
    
    private Simulator simulator;
    
    /**
     * @param owner The JFrame of the parent of this dialog.
     */
    public ExplorationDialog(Simulator simulator, JFrame owner) {

        // Open a modal dialog, which cannot be resized and can be closed by
        // the user.
        super(owner, "ExplorationDialog", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Remember simulator.
        this.simulator = simulator;
        
        // Create the content panel, which is laid out as a single column.
        // Add an empty space of 10 pixels between the dialog and the content
        // panel.
        JPanel dialogContent = new JPanel(new SpringLayout());
        dialogContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));

        // Create the panels.
        createButtonPanel();
        
        // Add the panels, and layout, to the dialog.
        // dialogContent.add(new JLabel(" "));
        dialogContent.add(new DocumentedSelection<Strategy>("exploration strategy", new StrategyEnumerator()));
        dialogContent.add(new JLabel(" "));
        dialogContent.add(new DocumentedSelection<Acceptor>("acceptor", new AcceptorEnumerator()));
        dialogContent.add(new JLabel(" "));
        dialogContent.add(this.buttonPanel);
        SpringUtilities.makeCompactGrid(dialogContent, 5, 1, 0, 0, 0, 0);

        // Add the dialogContent to the dialog and finish the dialog.
        add(dialogContent);
        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
        
        /*
        // Query for the exploration method (full or path).
        FormattedSelection methodSelection =
            new FormattedSelection(
                "Choose a method to explore the state space:", this);
        methodSelection.addOption(ExplorationDialog.FULL_EXPLORATION,
            "explore all rule matches in each reached state");
        methodSelection.addOption(ExplorationDialog.PATH_EXPLORATION,
            "explore exactly one rule match in each reached state");
        dialogContent.add(methodSelection);

        // Query for the full exploration strategy (Strategy).
        FormattedSelection fullStrategySelection =
            new FormattedSelection(
                "Choose a strategy for the full exploration:", this);
        fullStrategySelection.addOption(ExplorationDialog.BRANCHING, "?");
        fullStrategySelection.addOption(ExplorationDialog.BREADTH_FIRST, "?");
        fullStrategySelection.addOption(ExplorationDialog.DEPTH_FIRST, "?");
        fullStrategySelection.addOption(ExplorationDialog.LINEAR_CONFLUENCE,
            "?");

        // Query for the path exploration strategy (Strategy).
        FormattedSelection pathStrategySelection =
            new FormattedSelection(
                "Choose a strategy for the path exploration:", this);
        pathStrategySelection.addOption(ExplorationDialog.LINEAR, "?");
        pathStrategySelection.addOption(ExplorationDialog.RANDOM_LINEAR, "?");
        pathStrategySelection.add(new JLabel(" "));
        pathStrategySelection.add(new JLabel(" "));

        // Put fullStrategySelection and pathStrategySelection in a CardLayout.
        this.eitherStrategySelection = new JPanel(new CardLayout());
        this.eitherStrategySelection.add(fullStrategySelection,
            ExplorationDialog.FULL_EXPLORATION);
        this.eitherStrategySelection.add(pathStrategySelection,
            ExplorationDialog.PATH_EXPLORATION);
        dialogContent.add(new JLabel(" "));
        dialogContent.add(this.eitherStrategySelection);

        // Query for the termination condition (Acceptor).
        FormattedSelection acceptorSelection =
            new FormattedSelection(
                "Choose an additional termination condition:", this);
        acceptorSelection.addOption(ExplorationDialog.NONE_ACCEPTOR,
            "continue until no further matches are available");
        acceptorSelection.addOption(ExplorationDialog.FINAL_STATE,
            "stop as soon as a final state is encountered");
        acceptorSelection.addOption(ExplorationDialog.RULE_MATCH,
            "stop as soon as the selected rule matches");
        acceptorSelection.addOption(ExplorationDialog.RULE_MISMATCH,
            "stop as soon as the selected rule does not match");
        dialogContent.add(new JLabel(" "));
        dialogContent.add(acceptorSelection);

        // Query for the transformation rule (in case of RULE_MATCH or
        // RULE_MISMATCH)
        JPanel emptyPanel = new JPanel();
        JPanel rulePanel = new JPanel(new BorderLayout());
        JPanel innerRulePanel = new JPanel();
        innerRulePanel.add(new JLabel(" "));
        innerRulePanel.add(new JLabel(
            "<HTML><FONT color=996666>Select the transformation for Rule (Mis)Match:</FONT></HTML> "));
        JComboBox ruleSelector = new JComboBox();
        boolean first_element = true;
        for (RuleName ruleName : this.simulator.getGrammarView().getRuleNames()) {
            ruleSelector.addItem(ruleName);
            if (first_element) {
                first_element = false;
            }
        }
        ruleSelector.addActionListener(this);
        innerRulePanel.add(ruleSelector);
        rulePanel.add(innerRulePanel, BorderLayout.LINE_START);
        this.optionalRulePanel = new JPanel(new CardLayout());
        this.optionalRulePanel.add(emptyPanel, "EMPTY");
        this.optionalRulePanel.add(rulePanel, "RULE");
        dialogContent.add(this.optionalRulePanel);
        */
    }
     
    private void createButtonPanel() {
        this.buttonPanel = new JPanel();
        JButton exploreButton = new JButton(EXPLORE_COMMAND);
        exploreButton.addActionListener(this);
        this.buttonPanel.add(exploreButton);
        JButton cancelButton = new JButton(CANCEL_COMMAND);
        cancelButton.addActionListener(this);
        this.buttonPanel.add(cancelButton);
    }
       
    // The action listener of the dialog. Uses the String content of the message
    // to update the internal state of the dialog.
    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals(EXPLORE_COMMAND)) {
            // this.dispose();
            // this.simulator.doGenerate(getScenario());
            // return;
        }

        if (event.getActionCommand().equals(CANCEL_COMMAND)) {
            this.dispose();
            return;
        }
    }

    /*
    // Return the acceptor associated with the current selection state of the
    // dialog.
    // Should only be called when 'isConditionalAcceptor' is false.
    private Acceptor getAcceptor() {
        if (this.selectedAcceptor == ExplorationDialog.NONE_ACCEPTOR) {
            return (new Acceptor());
        }
        if (this.selectedAcceptor == ExplorationDialog.FINAL_STATE) {
            return (new FinalStateAcceptor(new Result(1)));
        }

        // Default case. Should never be reached.
        return (new Acceptor());
    }

    // Return the scenario associated with the current selection state of the
    // dialog.
    private Scenario getScenario() {
        if (!this.isConditionalAcceptor) {
            return ScenarioFactory.getScenario(getStrategy(), getAcceptor(),
                "", "");
        }
        return ScenarioFactory.getConditionalScenario(getStrategy(),
            new InvariantViolatedAcceptor<Rule>(new Result(1)), "", "",
            this.selectedAcceptor == ExplorationDialog.RULE_MISMATCH);
    }

    // Return the strategy associated with the current selection state of the
    // dialog.
    private Strategy getStrategy() {
        if (this.isFullExploration) {
            if (this.selectedFullStrategy == ExplorationDialog.BRANCHING) {
                return (new BranchingStrategy());
            }
            if (this.selectedFullStrategy == ExplorationDialog.DEPTH_FIRST) {
                return (new ExploreRuleDFStrategy());
            }
            if (this.selectedFullStrategy == ExplorationDialog.BREADTH_FIRST) {
                return (new BFSStrategy());
            }
            if (this.selectedFullStrategy == ExplorationDialog.LINEAR_CONFLUENCE) {
                return (new LinearConfluentRules());
            }
        } else {
            if (this.selectedPathStrategy == ExplorationDialog.LINEAR) {
                return (new LinearStrategy());
            }
            if (this.selectedPathStrategy == ExplorationDialog.RANDOM_LINEAR) {
                return (new RandomLinearStrategy());
            }
        }

        // Default case. Should never be reached.
        return (new BranchingStrategy());
    }
    */

    // Auxiliary extension of JPanel which creates the formatted selection lists
    // in a uniform layout.
    private class FormattedSelection extends JPanel {
        // Separate button group for all the check boxes in this JPanel.
        private ButtonGroup buttonGroup;

        // Local copy of the ActionListener (the surrounding Dialog).
        private final ActionListener listener;
        
        // Number of added elements. Needed for final layouting.
        private int nrComponents = 0;
        
        // Creates an initially empty JPanel with a leading label line only.
        // Add options by means of addOption.
        FormattedSelection(ActionListener listener) {
            super(new SpringLayout());
            this.buttonGroup = new ButtonGroup();
            this.listener = listener;
        }

        // Creates and formats a single option line.
        public void addOption(String shortName, String toolTipText, Boolean needsArguments, Boolean selected) {
            JPanel optionLine = new JPanel(new SpringLayout());

            // Create the check box.
            JCheckBox checkBox = new JCheckBox(shortName);
            checkBox.addActionListener(this.listener);
            if (toolTipText != null) checkBox.setToolTipText(toolTipText);
            optionLine.add(checkBox);
            this.buttonGroup.add(checkBox);
            checkBox.setSelected(selected);

            // Add an empty space between the shortName and the additionalInfo.
            optionLine.add(new JLabel(" "));

            // Add the additional info.
            if (needsArguments)
                optionLine.add(new JLabel("<HTML><FONT color=669966>(needs additional arguments)</FONT></HTML>"));
            else
                optionLine.add(new JLabel(""));

            // Layout the panel as a whole.
            SpringUtilities.makeCompactGrid(optionLine, 1, 3, 0, 0, 0, 0);
            this.add(optionLine);
            this.nrComponents++;
            // this.add(new JSeparator(JSeparator.HORIZONTAL));
        }
        
        public void addSeparator() {
            this.add(new JSeparator(JSeparator.HORIZONTAL));
            this.nrComponents++;
        }
        
        public void finalizeLayout() {
            SpringUtilities.makeCompactGrid(this, this.nrComponents, 1, 0, 0, 0, 0);
        }
    }
    
    private class DocumentedSelection<A> extends JPanel implements ListSelectionListener {
        private Enumerator<A> enumerator;
        private String objectType;
        private Documented<A> currentlySelected;
        private JLabel currentInfo;
        
        DocumentedSelection(String objectType, Enumerator<A> enumerator) {
            super(new SpringLayout());
            
            this.enumerator = enumerator;
            this.currentlySelected = enumerator.getElement(0);
            this.objectType = objectType;
            
            this.add(leftColumn());
            this.add(rightColumn());
            SpringUtilities.makeCompactGrid(this, 1, 2, 0, 0, 10, 0);
            updateInfo();
        }
        
        private JPanel leftColumn() {
            JList list = new JList(this.enumerator.getAllNames());
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setSelectedIndex(0);
            list.addListSelectionListener(this);
            JScrollPane listScroller = new JScrollPane(list);
            listScroller.setPreferredSize(new Dimension(250, 150));

            JPanel column = new JPanel(new SpringLayout());
            String leadingText = new String("<HTML><FONT color=green><B>Select ");
            leadingText = leadingText.concat(this.objectType);
            leadingText = leadingText.concat(":</B></FONT></HTML>");
            column.add(new JLabel(leadingText));
            column.add(listScroller);          
            SpringUtilities.makeCompactGrid(column, 2, 1, 0, 0, 0, 3);

            return column;
        }
        
        private JPanel rightColumn() {
            this.currentInfo = new JLabel();
            this.currentInfo.setPreferredSize(new Dimension(250, 150));
            this.currentInfo.setVerticalAlignment(SwingConstants.TOP);
            this.currentInfo.setBorder(BorderFactory.createLineBorder(new Color(175, 175, 175)));
            
            JPanel column = new JPanel(new SpringLayout());
            column.add(new JLabel("<HTML><FONT color=green><B>Additional information:</B></FONT></HTML>"));
            column.add(this.currentInfo);
            SpringUtilities.makeCompactGrid(column, 2, 1, 0, 0, 0, 3);
            return column;
        }
        
        private void updateInfo() {
            String infoText = new String();
            
            infoText = infoText.concat("<HTML><U>" 
                                       + this.currentlySelected.getName()
                                       + ":</U><BR>"
                                       + "<FONT color=blue>"
                                       + this.currentlySelected.getExplanation() 
                                       + "</FONT>");
            if (this.currentlySelected.needsArguments()) {
                infoText = infoText.concat("<BR>"
                                           + "<FONT color=red>"
                                           + "This strategy needs (an) additional argument(s)."
                                           + "</FONT>");
            }
            infoText = infoText.concat("</HTML>");
            
            this.currentInfo.setText(infoText);
        }
 
        public void valueChanged(ListSelectionEvent e) {
            Documented<A> newSelected = this.enumerator.findByName((String) ((JList) e.getSource()).getSelectedValue());
            if (newSelected != null) {
                this.currentlySelected = newSelected;
                updateInfo();
            }
        }
    }
}