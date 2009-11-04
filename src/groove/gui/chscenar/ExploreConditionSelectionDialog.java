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

import groove.explore.result.ExploreCondition;
import groove.trans.GraphGrammar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


/** A Dialog allowing to construct an explore condition
 * 
 * @author Iovka Boneva
 * @version $Revision $
 */
@Deprecated
@SuppressWarnings("all")
public class ExploreConditionSelectionDialog {

	// FIELDS
	/** The grammar used for creating the possible options in the dialogs. */
    private GraphGrammar grammar;
	/** The panel allowing to select an explore condition. */
    private ExploreConditionSelectionPanel selectionPanel;
    /** The constructed explore condition. */
    private ExploreCondition<?> exploreCondition;

    /** The ok button. */
    private JButton okButton;
    /** The cancel button. */
    private JButton cancelButton;
    /** The owner frame of the dialog to be shown. */
    private JFrame owner;
    
    /** Creates the dialog object. */
    public ExploreConditionSelectionDialog(JFrame owner, GraphGrammar grammar) {    	
    	this.owner = owner;
    	this.grammar = grammar;
    }

    // METHODS
    
    /** Lazily creates an explore condition panel.
     * @require The grammar should be already set.
     * 
     * @return
     */
    private ExploreConditionSelectionPanel getExploreConditionSelectionPanel() {
        if (selectionPanel == null) {
            selectionPanel = new ExploreConditionSelectionPanel(grammar);
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
    
    /** Shows the dialog and awaits for the explore condition to be chosen,
     * or the dialog to be explicitly closed.
     * @return The explore condition options that have been constructed.
     */
    public ExploreCondition<?> showDialog() {
        Object[] buttons = new Object[]{getOkButton(), getCancelButton()};
        JOptionPane pane = new JOptionPane(getExploreConditionSelectionPanel(),
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION,
                null,
                buttons);
        JDialog dialog = pane.createDialog(owner, "Set an explore condition");
        getOkButton().addActionListener(new ValidateDisposeActionListener(dialog));
        getCancelButton().addActionListener(new DisposeActionListener(dialog));
        dialog.pack();
        dialog.setVisible(true);
        return exploreCondition;
    }

    /** Action for the cancel button. */
    class DisposeActionListener implements ActionListener {
    	private JDialog dialog;
    	DisposeActionListener (JDialog dialog) {
    		this.dialog = dialog;
    	}
		public void actionPerformed(ActionEvent e) {
			dialog.dispose();
			
		}
    }

    /** Action for the ok button. */
    class ValidateDisposeActionListener implements ActionListener {

    	private JDialog dialog;
    	
    	ValidateDisposeActionListener (JDialog dialog) {
    		this.dialog = dialog;
    	}
    	
		public void actionPerformed(ActionEvent arg0) {
			exploreCondition = selectionPanel.getExploreCondition();
			if (exploreCondition != null) {
				dialog.dispose();
			}
		}
    	
    }
    
}
