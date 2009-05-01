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
 * $Id$
 */
package groove.gui.dialog;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import groove.explore.result.Acceptor;
import groove.explore.strategy.BranchingStrategy;
import groove.explore.strategy.Strategy;
import groove.gui.layout.SpringUtilities;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

/**
 * @author Maarten de Mol
 * @version $Revision $
 * Creates a dialog in which an exploration method can be composed out of an arbitrary
 * combination of a Strategy and an Acceptor. 
 */
public class ExplorationDialog extends JDialog implements ActionListener {
    // The current selection of the dialog.
    // Initially, the BranchingStrategy is selected, along with the None acceptor.
    // A scenario is conditional if the acceptor is either Rule Match or Rule Mismatch.
    private Strategy selectedStrategy = new BranchingStrategy();
    private Acceptor selectedAcceptor = new Acceptor();
    private boolean conditionalScenario = false;
    
    private JLabel test;
    
    public ExplorationDialog(JFrame owner) {
        // Open a modal dialog (cannot be resized) which can be closed by the user.
        super(owner, "ExplorationDialog", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Create the content panel, which is laid out as a single column.
        // Add an empty space of 10 pixels between the dialog and the content panel.
        JPanel dialogContent = new JPanel(new SpringLayout());
        dialogContent.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        // Query for the exploration method (full or path).
        FormattedSelection methodSelection = new FormattedSelection("Choose a method to explore the state space:", this);
        methodSelection.addOption("Full exploration",            "explore all rule matches in each reached state");
        methodSelection.addOption("Path exploration",            "explore exactly one rule match in each reached state");
        dialogContent.add(methodSelection);
        
        // Query for the exploration strategy (Strategy).
        FormattedSelection fullStrategySelection = new FormattedSelection("Choose a strategy for the full exploration:", this);
        fullStrategySelection.addOption("Branching",             "?");
        fullStrategySelection.addOption("Breadth-First",         "?");
        fullStrategySelection.addOption("Depth-First",           "?");
        fullStrategySelection.addOption("Lineair Confluence",    "?");
        dialogContent.add(new JLabel(" "));
        dialogContent.add(fullStrategySelection);

        // Query for the termination condition (Acceptor).
        FormattedSelection acceptorSelection = new FormattedSelection("Choose an additional termination condition:", this);
        acceptorSelection.addOption("None",                      "continue until no further matches are available");
        acceptorSelection.addOption("Final State",               "stop as soon as a final state is encountered");
        acceptorSelection.addOption("Rule Match",                "stop as soon as the given rule matches");
        acceptorSelection.addOption("Rule Mismatch",             "stop as soon as the given rule stops matching");
        dialogContent.add(new JLabel(" "));
        dialogContent.add(acceptorSelection);

        // Create 'OK' and a 'Cancel' button.
        // These have to be laid out horizontally, so they need to be placed in a new JPanel.
        dialogContent.add(new JLabel(" "));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(new JButton("Explore State Space"));
        buttonPanel.add(new JButton("Cancel"));
        dialogContent.add(buttonPanel);
        
        test = new JLabel("---");
        dialogContent.add(test);
        
        // Lay out the dialog as a single column.
        SpringUtilities.makeCompactGrid(dialogContent, 8, 1, 0, 0, 0, 0);
      
        // Add the dialogContent to the dialog and finish the dialog.
        add(dialogContent);
        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent event){
        this.test.setText(event.getActionCommand());
    }
    
    private class FormattedSelection extends JPanel {
        private final ButtonGroup buttonGroup;
        private final ActionListener listener;
        private boolean noCheckBoxesCreatedYet = true;
        
        // Creates an initially empty JPanel.
        // Add checkboxes by means of addCheckBox.
        FormattedSelection(String title, ActionListener listener){
            super(new GridLayout(0,1));
            this.buttonGroup = new ButtonGroup();
            this.listener = listener;
            this.add(new JLabel("<HTML><FONT color=green><B>" + title + "</B></FONT></HTML>"));
        }
        
        public void addOption(String shortName, String explanation){
            JPanel optionPanel = new JPanel(new SpringLayout());
            JCheckBox checkBox = new JCheckBox(shortName);
//            JCheckBox checkBox = new JCheckBox("<HTML>" + shortName + " <FONT color=669966>(" + explanation + ")</FONT></HTML>");
            checkBox.addActionListener(this.listener);
            optionPanel.add(checkBox);
            optionPanel.add(new JLabel(" "));
            optionPanel.add(new JLabel("<HTML><FONT color=669966>(" + explanation + ")</FONT></HTML>"));
            this.buttonGroup.add(checkBox);
            SpringUtilities.makeCompactGrid(optionPanel, 1, 3, 0, 0, 0, 0);
            this.add(optionPanel);
            if (this.noCheckBoxesCreatedYet){
                checkBox.setSelected(true);
                this.noCheckBoxesCreatedYet = false;
            }
        }      
    }   
}