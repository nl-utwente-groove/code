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
 * $Id: ErrorDialog.java,v 1.5 2008-01-30 09:33:36 iovka Exp $
 */
package groove.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * Implements a dialog with the ability to show details about the error.
 * @author Arend Rensink
 * @version $Revision: 1.5 $
 */
public class ErrorDialog extends JDialog {
	/** Dialog title. */
    public static final String ERROR_MESSAGE_TEXT = "Error message";
	/** Button text of the Details button. */
    public static final String NO_DETAILS_BUTTON_TEXT = "Details >>";
	/** Button text of the More Details button. */
    public static final String SOME_DETAILS_BUTTON_TEXT = "More >>";
	/** Button text of the No Details button. */
    public static final String ALL_DETAILS_BUTTON_TEXT = "Details <<";
	/** Button text of the Cancel button. */
    public static final String CANCEL_BUTTON_TEXT = "OK";

    /** Details status: no details. */
    public static final int NO_DETAILS = 0;
    /** Details status: some details. */
    public static final int SOME_DETAILS = 1;
    /** Details status: full details. */
    public static final int FULL_DETAILS = 2;

    /** Details text array. */
    private static final String[] DETAILS_LEVEL_TEXT =
        { NO_DETAILS_BUTTON_TEXT, SOME_DETAILS_BUTTON_TEXT, ALL_DETAILS_BUTTON_TEXT };

    /**
     * Searches upwards in the hierarchy of parent components
     * until it finds a <tt>JFrame</tt> or <tt>null</tt>.
     */
    static protected JFrame getParentFrame(Component component) {
        if (component == null || component instanceof JFrame) {
            return (JFrame) component;
        } else {
            return getParentFrame(component.getParent());
        }
    }

    /**
     * Consructs a new error dialog, with the same top-level frame as the
     * given component, a simple error message, and an exception giving more
     * detail about the error. The dialog is not yet shown.
     */
    public ErrorDialog(Component component, String message, Throwable exc) {
        super(getParentFrame(component), ERROR_MESSAGE_TEXT, true);
        setLocationRelativeTo(component);
        this.exc = exc;

        // setup cancel button
        JComponent cancelPane = Box.createHorizontalBox();
        cancelButton = new JButton(CANCEL_BUTTON_TEXT);
        cancelPane.add(Box.createHorizontalGlue());
        cancelPane.add(cancelButton);
        cancelPane.add(Box.createHorizontalGlue());
        cancelButton.setSelected(true);

        // setup details pane
        detailsButton = new JButton(NO_DETAILS_BUTTON_TEXT);
        detailsButton.setEnabled(exc != null);
        JComponent detailsButtonPane = Box.createHorizontalBox();
        detailsButtonPane.add(cancelButton);
        detailsButtonPane.add(Box.createHorizontalGlue());
        detailsButtonPane.add(detailsButton);

        detailsPane = new JPanel(new BorderLayout());
        detailsPane.add(detailsButtonPane, BorderLayout.SOUTH);

        // setup message pane
        JPanel messagePane = new JPanel(new BorderLayout());
        messagePane.add(detailsPane, BorderLayout.CENTER);

        // setup option pane
        JOptionPane optionPane =
            new JOptionPane(
                message,
                JOptionPane.ERROR_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[] { messagePane });
        optionPane.add(messagePane, BorderLayout.SOUTH);

        // setup content pane
        Container contentPane = getContentPane();
        contentPane.add(optionPane);

        // setup text area
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsTextPane = new JScrollPane(detailsArea);
        detailsTextPane.setPreferredSize(new Dimension(300, 100));
        detailsTextPane.setBorder(new EmptyBorder(5, 0, 5, 0));
        setDetailsLevel(NO_DETAILS);

        // setup button listener
        ActionListener buttonListener = new ButtonListener();
        cancelButton.addActionListener(buttonListener);
        detailsButton.addActionListener(buttonListener);
    }

    /**
     * Sets the level of details in the text area of the error dialog.
     * Legal values are: <ul>
     * <li> <code>NO_DETAILS</code>: the text area is hidden
     * <li> <code>SOME_DETAILS</code>: the text area is shown with just the message in the exception
     * <li> <code>FULL_DETAILS</code>: the text area is shown with the error stack trace
     * </ul>
     * @param detailsLevel the desired level of details
     */
    public void setDetailsLevel(int detailsLevel) {
        switch (detailsLevel) {
            case NO_DETAILS :
                detailsPane.remove(detailsTextPane);
                break;
            case SOME_DETAILS :
                detailsArea.setText(exc.getMessage() + "\n");
                detailsPane.add(detailsTextPane, BorderLayout.CENTER);
                detailsTextPane.scrollRectToVisible(new Rectangle(0, 0, 10, 10));
                break;
            case FULL_DETAILS :
                detailsArea.setText("");
                detailsArea.append(exc.toString() + "\n");
                StackTraceElement[] traceElems = exc.getStackTrace();
                for (int i = 0; i < traceElems.length; i++) {
                    detailsArea.append("    " + traceElems[i].toString() + "\n");
                }
                detailsPane.add(detailsTextPane, BorderLayout.CENTER);
                detailsArea.scrollRectToVisible(new Rectangle(0, 0, 10, 10));
        }
        detailsButton.setText(DETAILS_LEVEL_TEXT[detailsLevel]);
        pack();
    }

    /** Action listener that takes care of the dialog buttons. */
    protected class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            Object source = evt.getSource();
            if (source.equals(detailsButton)) {
                detailsLevel = (detailsLevel + 1) % 3;
                if (detailsLevel == SOME_DETAILS && exc.getMessage() == null) {
                    detailsLevel = (detailsLevel + 1) % 3;
                }
                setDetailsLevel(detailsLevel);
            } else if (source.equals(cancelButton)) {
                setVisible(false);
            }
        }
    }

    /** Button to control the amount of detail shown about the error. */
    protected final JButton detailsButton;
    /** The button cancelling the display of the dialog. */
    protected final JButton cancelButton;
    /** The panel which may show or hide the details text panel */
    protected final JPanel detailsPane;
    /** The text area with error details. */
    protected final JTextArea detailsArea;
    /** The panel upon which <tt>detailsArea</tt> is shown */
    protected final JScrollPane detailsTextPane;
    /** The exception reported by this dialog. */
    protected final Throwable exc;

    /** The level of details in the error dialog. */
    protected int detailsLevel = NO_DETAILS;
}
