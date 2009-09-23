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

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Maarten de Mol
 * @version $Revision $
 * 
 * This class creates a panel with two elements:
 * (1) An inner LTSPanel. (optional)
 * (2) A status line. (can be clicked)
 * These elements are aligned vertically in a BorderLayout.
 */
public class ConditionalLTSPanel extends JPanel implements MouseListener {
    
    /*
     * JLabel for the status line. Contents are changed dynamically.
    */
    private JLabel          statusText;
    
    /*
     * Inner reference to the actual LTSPanel. Is added/removed to the panel dynamically.
    */
    private LTSPanel        LTSPanel;
       
    /*
     * Internal bookkeeping of visibility of the LTSPanel.
    */
    private boolean         LTSPanelVisible;
    
    /*
     * Displayed text when the LTSPanel is hidden.
    */
    private final String    hiddenText  = "<HTML><BODY>"
                                        + "The LTSPanel is currently <FONT color=red>hidden</FONT>. "
                                        + "Click anywhere on this line to display it."
                                        + "</BODY></HTML>";
    
    /* 
     * Displayed text when the LTSPanel is visible.
    */
    private final String    visibleText = "<HTML><BODY>"
                                        + "The LTSPanel is currently <FONT color=green>visible</FONT>. "
                                        + "Click anywhere on this line to hide it."
                                        + "</BODY></HTML>";
    
    /**
     * Constructor for the ConditionalLTSPanel.
     * Draws the LTSPanel (initially always visible), and a status line.
     * @param theLTSPanel  the inner LTSPanel (assumed to have been created elsewhere)
     */
    public ConditionalLTSPanel(LTSPanel theLTSPanel) {
        /*
         * Create the JPanel.
        */
        super(new BorderLayout());
        
        /*
         * Initialize the local variables.
        */
        this.LTSPanel = theLTSPanel;
        this.LTSPanelVisible = true;
        this.statusText = new JLabel(this.visibleText);
        
        /*
         * The status line can be clicked. See below for event handler.
        */
        this.statusText.addMouseListener(this);

        /*
         * Draw the JPanel by adding the LTSPanel (initially always visible) and the statusText.
        */
        this.add(this.LTSPanel, BorderLayout.CENTER);
        this.add(this.statusText, BorderLayout.PAGE_END);
    }
    
    /*
     * Hides the internal LTSPanel.
     * Precondition: LTSPanel is currently visible.
    */
    private void hideLTSPanel() {
        this.remove(this.LTSPanel);
        this.statusText.setText(this.hiddenText);
    }

    /*
     * Shows the internal LTSPanel.
     * Precondition: LTSPanel is currently hidden.
    */
    private void showLTSPanel() {
        this.add(this.LTSPanel, BorderLayout.CENTER);
        this.statusText.setText(this.visibleText);
}
    
    
    /*
     * Event handler for the status line. Responds to arbitrary mouse clicks.
     * If clicked when visible, it hides the LTSPanel. If clicked when hidden, it displays the LTSPanel.
    */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (this.LTSPanelVisible) {
            hideLTSPanel();
        } else {
            showLTSPanel();
        }
        this.LTSPanelVisible = !this.LTSPanelVisible;
        this.LTSPanel.setGUIVisibility(this.LTSPanelVisible);
        this.updateUI();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        /* No specific action on mouse enter. */
    }

    @Override
    public void mouseExited(MouseEvent e) {
        /* No specific action on mouse exit. */
    }

    @Override
    public void mousePressed(MouseEvent e) {
        /* No specific action on mouse press. */
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        /* No specific action on mouse released. */
    }
}
