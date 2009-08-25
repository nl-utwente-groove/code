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

import groove.explore.Scenario;
import groove.explore.ScenarioFactory;
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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

/**
 * @author Maarten de Mol
 * @version $Revision $ Creates a dialog in which an exploration method can be
 *          composed out of an arbitrary combination of a Strategy and an
 *          Acceptor.
 */
public class ExplorationDialog extends JDialog implements ActionListener {

    // Constants to define the choices that can be made in this dialog.
    private static final String FULL_EXPLORATION = "Full exploration"; // strategy
    // type
    private static final String PATH_EXPLORATION = "Path exploration"; // strategy
    // type
    private static final String BRANCHING = "Branching"; // full exploration
    // strategy
    private static final String BREADTH_FIRST = "Breadth First"; // full
    // exploration
    // strategy
    private static final String DEPTH_FIRST = "Depth First"; // full exploration
    // strategy
    private static final String LINEAR_CONFLUENCE = "Linear Confluence"; // full
    // exploration
    // strategy
    private static final String LINEAR = "Linear"; // path exploration strategy
    private static final String RANDOM_LINEAR = "Random Linear"; // path
    // exploration
    // strategy
    private static final String NONE_ACCEPTOR = "None"; // acceptor
    private static final String FINAL_STATE = "Final State"; // acceptor
    private static final String RULE_MATCH = "Rule Match"; // acceptor (with
    // rule)
    private static final String RULE_MISMATCH = "Rule Mismatch"; // acceptor
    // (with rule)
    private static final String EXPLORE_COMMAND = "Explore State Space"; // button
    // command
    private static final String CANCEL_COMMAND = "Cancel"; // button command

    // The current selection state of the dialog, which consists of:
    // * isFullExploration - boolean to indicate either full or path exploration
    // * isConditionalAcceptor - boolean to indicate if the acceptor is
    // conditional (needs rule) or not
    // * selectedFullStrategy - selected strategy for full exploration
    // * selectedPathStrategy - selected strategy for path exploration
    // * selectedAcceptor - selected acceptor
    private boolean isFullExploration = true;
    private boolean isConditionalAcceptor = false;
    private String selectedFullStrategy = ExplorationDialog.BRANCHING;
    private final String selectedPathStrategy = ExplorationDialog.LINEAR;
    private String selectedAcceptor = ExplorationDialog.NONE_ACCEPTOR;

    // The panel that displays either the choices for full exploration or the
    // choices for
    // path exploration. This panel is referenced to change the visibility of
    // its CardLayout.
    private final JPanel eitherStrategySelection;

    // The panel that displays either a rule selector or the empty space.
    // This panel is referenced to change the visibility of its CardLayout.
    private final JPanel optionalRulePanel;

    // Local copy of the global Simulator. Used to run scenarios.
    private final Simulator simulator;

    /**
     * @param owner The JFrame of the parent of this dialog.
     */
    public ExplorationDialog(Simulator simulator, JFrame owner) {

        // Open a modal dialog, which cannot be resized and can be closed by the
        // user.
        super(owner, "ExplorationDialog", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Remember simulator.
        this.simulator = simulator;

        // Create the content panel, which is laid out as a single column.
        // Add an empty space of 10 pixels between the dialog and the content
        // panel.
        JPanel dialogContent = new JPanel(new SpringLayout());
        dialogContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

        // Create an 'OK' and a 'Cancel' button.
        // These have to be laid out horizontally, so they need to be placed in
        // a new JPanel.
        dialogContent.add(new JLabel(" "));
        JPanel buttonPanel = new JPanel();
        JButton exploreButton = new JButton(ExplorationDialog.EXPLORE_COMMAND);
        exploreButton.addActionListener(this);
        buttonPanel.add(exploreButton);
        JButton cancelButton = new JButton(ExplorationDialog.CANCEL_COMMAND);
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);
        dialogContent.add(buttonPanel);

        // Lay out the dialog as a single column.
        SpringUtilities.makeCompactGrid(dialogContent, 8, 1, 0, 0, 0, 0);

        // Add the dialogContent to the dialog and finish the dialog.
        add(dialogContent);
        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    // The action listener of the dialog. Uses the String content of the message
    // to
    // update the internal state of the dialog.
    public void actionPerformed(ActionEvent event) {

        // Respond to changes of the strategy type.
        if (event.getActionCommand() == ExplorationDialog.FULL_EXPLORATION
            || event.getActionCommand() == ExplorationDialog.PATH_EXPLORATION) {
            this.isFullExploration =
                (event.getActionCommand() == ExplorationDialog.FULL_EXPLORATION);
            CardLayout cards =
                (CardLayout) (this.eitherStrategySelection.getLayout());
            cards.show(this.eitherStrategySelection, event.getActionCommand());
            return;
        }

        // Respond to changes of the full exploration strategy.
        if (event.getActionCommand() == ExplorationDialog.BRANCHING
            || event.getActionCommand() == ExplorationDialog.DEPTH_FIRST
            || event.getActionCommand() == ExplorationDialog.BREADTH_FIRST
            || event.getActionCommand() == ExplorationDialog.LINEAR_CONFLUENCE) {
            this.selectedFullStrategy = event.getActionCommand();
            return;
        }

        // Respond to changes of the path exploration strategy.
        if (event.getActionCommand() == ExplorationDialog.LINEAR
            || event.getActionCommand() == ExplorationDialog.RANDOM_LINEAR) {
            this.selectedFullStrategy = event.getActionCommand();
            return;
        }

        // Respond to changes of the acceptor (no rule).
        if (event.getActionCommand() == ExplorationDialog.NONE_ACCEPTOR
            || event.getActionCommand() == ExplorationDialog.FINAL_STATE) {
            this.isConditionalAcceptor = false;
            CardLayout cards =
                (CardLayout) (this.optionalRulePanel.getLayout());
            cards.show(this.optionalRulePanel, "EMPTY");
            this.selectedAcceptor = event.getActionCommand();
            return;
        }

        // Respond to changes of the acceptor (conditional).
        if (event.getActionCommand() == ExplorationDialog.RULE_MATCH
            || event.getActionCommand() == ExplorationDialog.RULE_MISMATCH) {
            this.isConditionalAcceptor = true;
            CardLayout cards =
                (CardLayout) (this.optionalRulePanel.getLayout());
            cards.show(this.optionalRulePanel, "RULE");
            this.selectedAcceptor = event.getActionCommand();
            return;
        }

        // Respond to the 'Explore' button
        if (event.getActionCommand() == ExplorationDialog.EXPLORE_COMMAND) {
            this.dispose();
            this.simulator.doGenerate(getScenario());
            return;
        }

        // Respond to the 'Cancel' button
        if (event.getActionCommand() == ExplorationDialog.CANCEL_COMMAND) {
            this.dispose();
            return;
        }
    }

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

    // Auxiliary extension of JPanel which creates the formatted selection lists
    // in a uniform layout.
    private class FormattedSelection extends JPanel {

        // Separate button group for all the check boxes in this JPanel.
        private final ButtonGroup buttonGroup;

        // Local copy of the ActionListener (which is the surrounding Dialog as
        // a whole)
        private final ActionListener listener;

        // Variable used to identify the first check box (which should be
        // selected initially).
        private boolean noCheckBoxesCreatedYet = true;

        // Creates an initially empty JPanel with a leading label line only.
        // Add options by means of addOption.
        FormattedSelection(String title, ActionListener listener) {
            super(new GridLayout(0, 1));
            this.buttonGroup = new ButtonGroup();
            this.listener = listener;
            this.add(new JLabel("<HTML><FONT color=green><B>" + title
                + "</B></FONT></HTML>"));
        }

        // Creates and formats a single option line.
        public void addOption(String shortName, String explanation) {

            // The option line is formatted as a panel, with three components:
            // check box, empty space, explanation.
            JPanel optionPanel = new JPanel(new SpringLayout());

            // Create the check box.
            JCheckBox checkBox = new JCheckBox(shortName);
            checkBox.addActionListener(this.listener);
            optionPanel.add(checkBox);
            this.buttonGroup.add(checkBox);
            if (this.noCheckBoxesCreatedYet) {
                checkBox.setSelected(true);
                this.noCheckBoxesCreatedYet = false;
            }

            // Add the empty space.
            optionPanel.add(new JLabel(" "));

            // Add the explanation
            optionPanel.add(new JLabel("<HTML><FONT color=669966>("
                + explanation + ")</FONT></HTML>"));

            // Layout the panel as a whole.
            SpringUtilities.makeCompactGrid(optionPanel, 1, 3, 0, 0, 0, 0);
            this.add(optionPanel);
        }
    }
}