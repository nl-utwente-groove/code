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
package groove.gui.chscenar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import groove.explore.Scenario;
import groove.trans.GraphGrammar;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/** Allows to display a dialogue window for defining a scenario.
 * @author Iovka Boneva
 */
@Deprecated
@SuppressWarnings("all")
public class ScenarioSelectionDialog {
	
	// FIELDS
	/** The grammar used for creating the possible options in the dialogs. */
    private GraphGrammar grammar;
	/** The panel allowing to select an explore condition. */
    private ScenarioSelectionPanel selectionPanel;
    /** The scenario that has been defined. */
    private Scenario scenario;

    /** The ok button. */
    private JButton okButton;
    /** The cancel button. */
    private JButton cancelButton;
    /** The owner frame of the dialog to be shown. */
    private JFrame owner;
    /** The dialog window that is displayed. */
    private JDialog dialogWindow;
    
    /** Creates the dialog object. */
    public ScenarioSelectionDialog (JFrame owner, GraphGrammar grammar) {    	
    	this.owner = owner;
    	this.grammar = grammar;
    }
    
    // METHODS
    /** Lazily creates an scenario selection panel.
     * @require The grammar should be already set.
     */
    private ScenarioSelectionPanel getExploreConditionSelectionPanel() {
        if (selectionPanel == null) {
            selectionPanel = new ScenarioSelectionPanel(owner, grammar);
        }
        return selectionPanel;
    }
    
    /** Lazily creates the ok button. */
    private JButton getOkButton() {
    	if (okButton == null) {
    		okButton = new JButton("Ok");
    	}
        return okButton;
    }
	
    /** Lazily creates the cancel button. */
    private JButton getCancelButton() {
    	if (cancelButton == null) {
    		cancelButton = new JButton("Cancel");
    	}
        return cancelButton;
    }
    
    /** Shows the dialog and awaits for the scenario to be chosen,
     * or the dialog to be explicitly closed.
     * @return The scenario that have been constructed, or null if no valid scenario was constructed.
     */
    public Scenario showDialog() {
        Object[] buttons = new Object[]{getOkButton(), getCancelButton()};
        JOptionPane pane = new JOptionPane(getExploreConditionSelectionPanel(),
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION,
                null,
                buttons);
        dialogWindow = pane.createDialog(owner, "Set an explore condition");
        getOkButton().addActionListener(new OkButtonActionListener());
        getCancelButton().addActionListener(new CancelButtonActionListener());
        dialogWindow.pack();
        dialogWindow.setVisible(true);
        return scenario;
    }
    
    /** Action for the cancel button. */
    class CancelButtonActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			dialogWindow.dispose();
		}
    }

    /** Action for the ok button. */
    class OkButtonActionListener implements ActionListener {
    	
		public void actionPerformed(ActionEvent arg0) {
			ScenarioSelectionModel model = selectionPanel.getModel();
			scenario = model.getScenario();
                        System.out.println(scenario);
			if (scenario == null) {
				assert ! model.getStatus().isOk() : "The status should not be ok";
				JOptionPane.showMessageDialog(dialogWindow, model.getStatus().getStatusText());
			}
                        else {
                            dialogWindow.dispose();
                        }
		}
    }
	
}
