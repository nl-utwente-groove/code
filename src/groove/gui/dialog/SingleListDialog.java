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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 * This dialog shows a list that allows the user to make a single selection.
 * @author Eduardo Zambon
 * @version $Revision$
 */
public class SingleListDialog extends JDialog implements ActionListener {

    /**
     * Creates a dialog with a list of given names.
     */
    public SingleListDialog(Component parent, String title, String message,
            List<String> list, boolean showCancelButton) {
        // Open a modal dialog, which can be closed by the user.
        super(getParentFrame(parent), title, true);
        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setResizable(false);

        // Create the content panel, which is laid out as a single column.
        // Add an empty space of 10 pixels between the dialog and the content
        // panel.
        JPanel dialogContent = new JPanel();
        dialogContent.setLayout(new BoxLayout(dialogContent, BoxLayout.Y_AXIS));
        dialogContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Make sure that the dialog listens to Escape and Enter.
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        dialogContent.registerKeyboardAction(this.escapeListener, escape,
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialogContent.registerKeyboardAction(this.enterListener, enter,
            JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Top label
        JPanel labelPanel = new JPanel();
        labelPanel.add(new JLabel(message));
        dialogContent.add(labelPanel);

        Collections.sort(list);
        this.list = new JList(list.toArray());
        this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.list.setLayoutOrientation(JList.VERTICAL);
        this.list.setSelectedIndex(0);

        // Scroll pane for the list. 
        JScrollPane listScroller = new JScrollPane(this.list);
        listScroller.setPreferredSize(new Dimension(100, 150));
        dialogContent.add(listScroller);

        // OK & Cancel buttons.
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton(OK_COMMAND);
        okButton.addActionListener(this);
        buttonPanel.add(okButton);
        if (showCancelButton) {
            JButton cancelButton = new JButton(CANCEL_COMMAND);
            cancelButton.addActionListener(this);
            buttonPanel.add(cancelButton);
        }
        // Add them to the panel.
        dialogContent.add(buttonPanel);

        // Add the dialogContent to the dialog and finish the dialog.
        this.add(dialogContent);
        this.pack();
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
    }

    /**
     * @return whether the dialog was canceled or not.
     */
    public boolean wasCanceled() {
        return this.canceled;
    }

    /**
     * Close action. Also sets canceled flag.
     */
    private void closeDialog(boolean canceled) {
        this.canceled = canceled;
        this.setVisible(false);
    }

    /**
     * @return the item that is selected in the dialog.
     */
    public String getSelectedItem() {
        return (String) this.list.getSelectedValue();
    }

    /**
     * Action that responds to the Escape key.
     */
    ActionListener escapeListener = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            closeDialog(true);
        }
    };

    /**
     * Action that responds to the Enter key.
     */
    ActionListener enterListener = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            closeDialog(false);
        }
    };

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals(OK_COMMAND)) {
            closeDialog(false);
        } else if (event.getActionCommand().equals(CANCEL_COMMAND)) {
            closeDialog(true);
        }
        return;
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
    boolean canceled;

    private static final String OK_COMMAND = "OK";
    private static final String CANCEL_COMMAND = "Cancel";
}