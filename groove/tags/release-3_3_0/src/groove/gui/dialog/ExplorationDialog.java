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
import groove.explore.Exploration;
import groove.explore.StrategyEnumerator;
import groove.explore.result.Acceptor;
import groove.explore.result.Result;
import groove.explore.strategy.Strategy;
import groove.gui.Simulator;
import groove.gui.layout.SpringUtilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Dialog that allows the user to compose an exploration out of a strategy,
 * an acceptor and a result.
 * The strategy is selected from one of the options in a StrategyEnumeration.
 * The acceptor is selected from one of the options in a StrategyEnumeration.
 * 
 * This class does not implement any public or private methods.
 * 
 * @author Maarten de Mol
 * @version $Revision $
 */
public class ExplorationDialog extends JDialog implements ActionListener {
    private final String EXPLORE_COMMAND = "Explore State Space";  // button text
    private final String CANCEL_COMMAND = "Cancel";                // button text
    
    private final String STRATEGY_TOOLTIP = "<HTML>"               // tool tip to explain exploration strategy
                                          + "The exploration strategy determines at each state:<BR>"
                                          + "<B>1.</B> Which of the applicable transitions will be taken; and<BR>"
                                          + "<B>2.</B> In which order the reached states will be explored."
                                          + "</HTML>";
    private final String ACCEPTOR_TOOLTIP = "<HTML>"               // tool tip to explain acceptor
                                          + "An acceptor is a predicate that is applied each time the LTS is updated<I>*</I>.<BR>"
                                          + "Information about each acceptor success is added to the result set of the exploration.<BR>"
                                          + "This result set can be used to interrupt exploration.<BR>"
                                          + "<I>(*)<I>The LTS is updated when a transition is applied, or when a new state is reached."
                                          + "</HTML>";
    private final String RESULT_TOOLTIP   = "<HTML>"               // tool tip to explain result
                                          + "Exploration can be interrupted between atomic steps of the strategy.<BR> "
                                          + "The size of the atomic steps depends on the chosen strategy.<BR> "
                                          + "The interruption condition is determined by the indicated number of times that the acceptor succeeds."
                                          + "</HTML>";

    private DocumentedSelection<Strategy> strategySelector;        // panel that holds the strategy selection
    private DocumentedSelection<Acceptor> acceptorSelector;        // panel that holds the acceptor selection
    private ResultSelection resultSelector;                        // panel that holds the result selection
    private JPanel buttonPanel;                                    // panel that holds the buttons
    
    private Simulator simulator;                                   // reference to the simulator
    
    private int oldDismissDelay;                                   // stores the old DismissDelay of the ToolTipManager
    
    /**
     * Create the dialog.
     * @param simulator - reference to the simulator
     * @param owner - reference to the parent GUI component
     */
    public ExplorationDialog(Simulator simulator, JFrame owner) {       
        // Open a modal dialog, which cannot be resized and can be closed by the user.
        super(owner, "ExplorationDialog", true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        
        // Override DismissDelay of the ToolTipManager.
        // Old value will be reset when the dialog is closed.
        this.oldDismissDelay = ToolTipManager.sharedInstance().getDismissDelay();
        ToolTipManager.sharedInstance().setDismissDelay(1000000000);

        // Make sure that closeDialog is called whenever the dialog is closed. 
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
              closeDialog();
            }
          });

        // Remember simulator.
        this.simulator = simulator;
        
        // Create the content panel, which is laid out as a single column.
        // Add an empty space of 10 pixels between the dialog and the content
        // panel.
        JPanel dialogContent = new JPanel(new SpringLayout());
        dialogContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
        
        // Make sure that the dialog listens to Escape and Enter.
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        dialogContent.registerKeyboardAction(this.escapeListener, escape, JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialogContent.registerKeyboardAction(this.enterListener, enter, JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        // Add the panels to the dialogContent.
        this.strategySelector = new DocumentedSelection<Strategy>("exploration strategy", 
                                                                  this.STRATEGY_TOOLTIP,
                                                                  new StrategyEnumerator());
        dialogContent.add(this.strategySelector);
        dialogContent.add(new JLabel(" "));
        this.acceptorSelector = new DocumentedSelection<Acceptor>("acceptor", 
                                                                  this.ACCEPTOR_TOOLTIP,
                                                                  new AcceptorEnumerator());
        dialogContent.add(this.acceptorSelector);
        dialogContent.add(new JLabel(" "));
        this.resultSelector = new ResultSelection(this.RESULT_TOOLTIP); 
        dialogContent.add(this.resultSelector);
        dialogContent.add(new JLabel(" "));
        createButtonPanel();
        dialogContent.add(this.buttonPanel);
        
        // Put the panels in a CompactGrid layout.
        SpringUtilities.makeCompactGrid(dialogContent, 7, 1, 0, 0, 0, 0);

        // Add the dialogContent to the dialog.
        add(dialogContent);
        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    /**
     * Create the button panel.
     */
    private void createButtonPanel() {
        this.buttonPanel = new JPanel();
        
        JButton exploreButton = new JButton(this.EXPLORE_COMMAND);
        exploreButton.addActionListener(this);
        this.buttonPanel.add(exploreButton);
        if (this.simulator.getGrammarView() == null ||
            this.simulator.getGrammarView().getStartGraphView() == null)
            exploreButton.setEnabled(false);
        
        JButton cancelButton = new JButton(this.CANCEL_COMMAND);
        cancelButton.addActionListener(this);
        this.buttonPanel.add(cancelButton);
    }
    
    /**
     * Customized close action.
     * Disposes dialog and resets DismissDelay of the ToolTipManager.
     */
    private void closeDialog() {
        this.dispose();
        ToolTipManager.sharedInstance().setDismissDelay(this.oldDismissDelay);
    }

    /**
     * The exploration action.
     * Gets the current selection (strategy, acceptor and result), constructs
     * an exploration out of it and then runs it.
     * 
     */
    private void doExploration() {
        Strategy strategy = this.strategySelector.getSelectedValue().getObjectForUI(this.simulator, this);
        if (strategy == null)
            return;
        Acceptor acceptor = this.acceptorSelector.getSelectedValue().getObjectForUI(this.simulator, this);
        if (acceptor == null)
            return;
        Integer nrResults = this.resultSelector.getSelectedValue();
        if (nrResults == null)
            return;
        
        String shortName;
        String resultName;
        if (nrResults == 0)
            resultName = "Infinite";
        else
            resultName = Integer.toString(nrResults);        
        shortName = this.strategySelector.getSelectedValue().getKeyword()
                  + "/"
                  + this.acceptorSelector.getSelectedValue().getKeyword()
                  + "/"
                  + resultName;
        
        Exploration exploration = new Exploration(strategy, acceptor, new Result(nrResults), shortName);           
        closeDialog();
        ToolTipManager.sharedInstance().setDismissDelay(this.oldDismissDelay);
        this.simulator.doRunExploration(exploration);
    }
    
    /**
     *  The action listener of the dialog. Responds to button presses only.
     */
    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals(this.EXPLORE_COMMAND)) {
            doExploration();
            return;
        }

        if (event.getActionCommand().equals(this.CANCEL_COMMAND)) {
            closeDialog();
            return;
        }
    }

    /**
     * Action that responds to the Escape key.
     * Ensures that the dialog is closed.
     */
    ActionListener escapeListener = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            closeDialog();
        }
    };   

    /**
     * Action that responds to the Enter key.
     * Ensures that the currently selected exploration is executed.
     */
    ActionListener enterListener = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            doExploration();
        }
    };

    /**
     * A generic wrapper class that allows elements from an arbitrary DocumentedSelection
     * to be selected by the user.
     */ 
    private class DocumentedSelection<A> extends JPanel implements ListSelectionListener {
        private Enumerator<A> enumerator;
        private String objectType;
        private String tooltip;
        private Documented<A> currentlySelected;
        private JLabel currentInfo;
        
        DocumentedSelection(String objectType, String tooltip, Enumerator<A> enumerator) {
            super(new SpringLayout());
            
            this.enumerator = enumerator;
            this.currentlySelected = enumerator.getElement(0);
            this.objectType = objectType;
            this.tooltip = tooltip;
            
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
            listScroller.setPreferredSize(new Dimension(300, 150));

            JPanel column = new JPanel(new SpringLayout());
            String leadingText = new String("<HTML><FONT color=green><B>Select ");
            leadingText = leadingText.concat(this.objectType);
            leadingText = leadingText.concat(":</B></FONT></HTML>");
            JLabel leadingLabel = new JLabel(leadingText);
            leadingLabel.setToolTipText(this.tooltip);
            column.add(leadingLabel);
            column.add(listScroller);          
            SpringUtilities.makeCompactGrid(column, 2, 1, 0, 0, 0, 3);

            return column;
        }
        
        private JPanel rightColumn() {
            this.currentInfo = new JLabel();
            this.currentInfo.setPreferredSize(new Dimension(300, 150));
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
                                       + "</FONT><BR>"
                                       + "<FONT color=#555555>Commandline keyword: "
                                       + this.currentlySelected.getKeyword()
                                       + ".</FONT></U>");
            if (this.currentlySelected.needsArguments()) {
                infoText = infoText.concat("<BR>"
                                           + "<FONT color=red>"
                                           + "This "
                                           + this.objectType
                                           + " needs (an) additional argument(s)."
                                           + "</FONT>");
            }
            infoText = infoText.concat("</HTML>");
            
            this.currentInfo.setText(infoText);
        }
        
        public Documented<A> getSelectedValue() {
            return this.currentlySelected;
        }
 
        public void valueChanged(ListSelectionEvent e) {
            Documented<A> newSelected = this.enumerator.findByName((String) ((JList) e.getSource()).getSelectedValue());
            if (newSelected != null) {
                this.currentlySelected = newSelected;
                updateInfo();
            }
        }
    }
        
    /**
     * The panel where the results can be selected.
     */
    private class ResultSelection extends JPanel implements ActionListener {
        JRadioButton[] checkboxes;
        JTextField customNumber;
        
        ResultSelection(String tooltip) {
            super(new SpringLayout());

            this.checkboxes = new JRadioButton[3];
            this.checkboxes[0] = new JRadioButton("Infinite (don't interrupt)");
            this.checkboxes[1] = new JRadioButton("1 (interrupt as soon as acceptor succeeds)");
            this.checkboxes[2] = new JRadioButton("Custom: ");
            this.checkboxes[0].setSelected(true);
            for (int i = 0; i < 3; i++)
                this.checkboxes[i].addActionListener(this);
            
            this.customNumber = new JTextField("2", 3);
            this.customNumber.addKeyListener(new OnlyListenToNumbers());
            this.customNumber.setEnabled(false);
            
            JLabel leadingLabel = new JLabel("<HTML><FONT color=green><B>Interrupt exploration when the following number of accepted results have been found: </HTML>");
            leadingLabel.setToolTipText(tooltip);
            this.add(leadingLabel);
            ButtonGroup options = new ButtonGroup();
            JPanel optionsLine = new JPanel(new SpringLayout());
            for (int i = 0; i < 3; i++) {
                optionsLine.add(this.checkboxes[i]);
                if (i < 2)
                    optionsLine.add(Box.createRigidArea(new Dimension(25,0)));
                options.add(this.checkboxes[i]);
            }
            optionsLine.add(this.customNumber);
            optionsLine.add(Box.createRigidArea(new Dimension(50,0)));
            SpringUtilities.makeCompactGrid(optionsLine, 1, 7, 0, 0, 0, 0);
            this.add(optionsLine);

            SpringUtilities.makeCompactGrid(this, 2, 1, 0, 0, 0, 0);
        }
        
        public Integer getSelectedValue() {
            if (this.checkboxes[0].isSelected())
                return 0;
            if (this.checkboxes[1].isSelected())
                return 1;
            if (this.checkboxes[2].isSelected())
                return Integer.parseInt(this.customNumber.getText());
            return null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == this.checkboxes[0]) {
                this.customNumber.setEnabled(false);
            }

            if (e.getSource() == this.checkboxes[1]) {
                this.customNumber.setEnabled(false);
            }

            if (e.getSource() == this.checkboxes[2]) {
                this.customNumber.setEnabled(true);
            }
        }

        private class OnlyListenToNumbers extends KeyAdapter {
            @Override
            public void keyTyped(KeyEvent evt) {
                char ch = evt.getKeyChar();
                
                if (!Character.isDigit(ch))
                    evt.consume();
            }
        }
    }
}