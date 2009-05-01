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
package groove.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import groove.explore.result.Acceptor;
import groove.explore.strategy.BranchingStrategy;
import groove.explore.strategy.Strategy;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Maarten de Mol
 * @version $Revision $
 * Creates a dialog in which an exploration method can be composed out of an arbitrary
 * combination of a Strategy and an Acceptor. 
 */
public class ExplorationDialog extends JDialog {
    // Local copy of the state of the dialog.
    // Initially, the BranchingStrategy is selected, along with the None acceptor.
    private Strategy selectedStrategy = new BranchingStrategy();
    private Acceptor selectedAcceptor = new Acceptor();
    
    ExplorationDialog(JFrame owner) {
        // Open a non-resizable modal dialog which can be closed by the user.
        super(owner, "ExplorationDialog", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Create the content panel, which is laid out as a single column.
        // Add an empty space of 10 pixels between the dialog and the content panel.
        JPanel dialogContent = new JPanel(new GridLayout(0,1));
        dialogContent.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        // Query for the exploration method (full or path).
        FormattedSelection methodSelection = new FormattedSelection("Choose a method to explore the state space:", this);
        methodSelection.addOption("Full exploration",            "explore all rule matches in each reached state");
        methodSelection.addOption("Path exploration",            "explore exactly one rule match in each reached state");
        dialogContent.add(methodSelection);
        
        // Query for the exploration strategy (Strategy).
        FormattedSelection fullStrategySelection = new FormattedSelection("Choose a strategy for the full exploration:", this);
        fullStrategySelection.addStrategyOption("Branching",             "?", new BranchingStrategy());
        fullStrategySelection.addOption("Breadth-First",         "?");
        fullStrategySelection.addOption("Depth-First",           "?");
        fullStrategySelection.addOption("Lineair Confluence",    "?");
        dialogContent.add(new JLabel(""));
        dialogContent.add(fullStrategySelection);

        // Query for the termination condition (Acceptor).
        FormattedSelection acceptorSelection = new FormattedSelection("Choose an additional termination condition:", this);
        acceptorSelection.addOption("None",                      "continue until no further matches are available");
        acceptorSelection.addOption("Final State",               "stop as soon as a final state is encountered");
        acceptorSelection.addOption("Rule Match",                "stop as soon as the given rule matches");
        acceptorSelection.addOption("Rule Mismatch",             "stop as soon as the given rule stops matching");
        dialogContent.add(new JLabel(""));
        dialogContent.add(acceptorSelection);

        // Create 'OK' and a 'Cancel' button.
        // These have to be laid out horizontally, so they need to be placed in a new JPanel.
        dialogContent.add(new JLabel(""));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(new JButton("Explore State Space"));
        buttonPanel.add(new JButton("Cancel"));
        dialogContent.add(buttonPanel);
      
        // Add the dialogContent to the dialog and finish the dialog.
        add(dialogContent);
        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }
    
    public void listenToStrategy(Strategy newStrategy){
    }
    
    private void buildSelection(JPanel panel, String... options) {
        ButtonGroup buttonGroup = new ButtonGroup();
        for (int i = 0; i < options.length; i++) {
            JCheckBox checkBox = new JCheckBox(options[i]);
            if (i == 0) checkBox.setSelected(true);
            buttonGroup.add(checkBox);
            panel.add(checkBox);
        }
    }
    
    private class FormattedSelection extends JPanel implements ActionListener {
        private final ButtonGroup buttonGroup;
        private final ExplorationDialog dialog;
        private boolean empty;
        
        // Creates an initially empty JPanel.
        // Add checkboxes by means of addCheckBox.
        FormattedSelection(String title, ExplorationDialog dialog){
            super(new GridLayout(0,1));
            this.buttonGroup = new ButtonGroup();
            this.dialog = dialog;
            this.empty = true;
            this.add(new JLabel("<HTML><FONT color=green><B>" + title + "</B></FONT></HTML>"));
        }
        
        public void addOption(String shortName, String explanation){
            JCheckBox checkBox = new JCheckBox("<HTML>" + shortName + " <FONT color=669966>(" + explanation + ")</FONT></HTML>");
            this.add(checkBox);
            this.buttonGroup.add(checkBox);
            if (this.empty) checkBox.setSelected(true);
            this.empty = false;
        }

        public void addStrategyOption(String shortName, String explanation, Strategy strategy){
            JCheckBox checkBox = new JCheckBox("<HTML>" + shortName + " <FONT color=669966>(" + explanation + ")</FONT></HTML>");
            this.add(checkBox);
            this.buttonGroup.add(checkBox);
            if (this.empty) checkBox.setSelected(true);
            this.empty = false;
        }
        
        public void actionPerformed(ActionEvent e){
        }
    }   
}