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

import groove.gui.Simulator;
import groove.trans.Rule;
import groove.trans.RuleName;
import groove.view.FormatException;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * @author Eduardo Zambon
 * @version $Revision$
 */
public class RuleSelectionDialog extends JDialog implements ActionListener{

    /**
     * Creates a dialog with a list of rule names.
     * @param simulator the reference to the simulator object.
     * @param owner parent element of the interface.
     * @param mayBeNegated flag that indicates if the condition over the rule
     *                     may be negated.
     */
    public RuleSelectionDialog(
            Simulator simulator,
            Component owner,
            boolean mayBeNegated) {
        // Open a modal dialog, which can be closed by the user.
        super(getParentFrame(owner), "Rule Selection", true);
        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        //setResizable(false);
        
        // Remember simulator.
        this.simulator = simulator;
        
        // Create the content panel, which is laid out as a single column.
        // Add an empty space of 10 pixels between the dialog and the content
        // panel.
        JPanel dialogContent = new JPanel();
        dialogContent.setLayout(
                            new BoxLayout(dialogContent, BoxLayout.Y_AXIS));
        dialogContent.setBorder(
                            BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // List of rule names.
        this.list = new JList(
                    this.simulator.getGrammarView().getRuleNames().toArray());
        this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.list.setLayoutOrientation(JList.VERTICAL);
        this.list.setSelectedIndex(0);
        
        // Scroll pane for the list. 
        JScrollPane listScroller = new JScrollPane(this.list);
        listScroller.setPreferredSize(new Dimension(250, 250));
        dialogContent.add(listScroller);
        
        // Radio buttons.
        this.positiveButton = new JRadioButton("Positive");
        this.negativeButton = new JRadioButton("Negative");
        this.positiveButton.setSelected(true);
        this.negativeButton.setEnabled(mayBeNegated);
        // Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(this.positiveButton);
        group.add(this.negativeButton);
        // Add the radio buttons to a panel.
        JPanel radioPanel = new JPanel();
        radioPanel.add(new JLabel("Rule condition: "));
        radioPanel.add(this.positiveButton);
        radioPanel.add(this.negativeButton);
        // Add one panel to the other.
        dialogContent.add(radioPanel);

        // OK & Cancel buttons.
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton(OK_COMMAND);
        okButton.addActionListener(this);
        buttonPanel.add(okButton);
        JButton cancelButton = new JButton(CANCEL_COMMAND);
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);
        // Add them to the panel.
        dialogContent.add(buttonPanel);
        
        // Add the dialogContent to the dialog and finish the dialog.
        this.add(dialogContent);
        this.pack();
        this.setLocationRelativeTo(owner);
        this.setVisible(true);
    }
    
    /**
     * @return the last rule selected when the dialog was closed.
     */
    public Rule getSelectedRule() {
        return this.getRuleFromName((RuleName) this.list.getSelectedValue());
    }
    
    /**
     * @return whether the rule is to be used as a negated condition or not.
     */
    public boolean isNegated() {
        return this.negativeButton.isSelected();
    }
    
    /**
     * @return whether the dialog was canceled or not.
     */
    public boolean wasCanceled() {
        return this.canceled;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        this.setVisible(false);
        if (event.getActionCommand().equals(OK_COMMAND)) {
            this.canceled = false;
        } else if (event.getActionCommand().equals(CANCEL_COMMAND)) {
            this.canceled = true;
        }
        return;
    }

    private Rule getRuleFromName(RuleName name) {
        Rule rule = null;
        try {
            rule = this.simulator.getGrammarView().getRuleView(name).toRule();
        } catch (FormatException e) {
            // This should never happen...
        }
        return rule;
    }
    
    /**
     * Searches upwards in the hierarchy of parent components until it finds a
     * <tt>JFrame</tt> or <tt>null</tt>.
     */
    static protected JFrame getParentFrame(Component component) {
        if (component == null || component instanceof JFrame) {
            return (JFrame) component;
        } else {
            return getParentFrame(component.getParent());
        }
    }

    private JList list;
    private Simulator simulator;
    JRadioButton positiveButton;
    JRadioButton negativeButton;
    boolean canceled;
    
    private static final String OK_COMMAND = "OK";
    private static final String CANCEL_COMMAND = "Cancel";
}
