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
package nl.utwente.groove.gui.dialog;

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

import nl.utwente.groove.util.Exceptions;

/**
 * Implements a dialog with the ability to show details about the error.
 * @author Arend Rensink
 * @version $Revision$
 */
public class ErrorDialog extends JDialog {
    /**
     * Constructs a new error dialog, with the same top-level frame as the given
     * component, a simple error message, and an exception giving more detail
     * about the error. The dialog is not yet shown.
     */
    public ErrorDialog(Component component, String message, Throwable exc) {
        super(getParentFrame(component), ERROR_MESSAGE_TEXT, true);
        setLocationRelativeTo(component);
        this.exc = exc;

        // setup cancel button
        JComponent cancelPane = Box.createHorizontalBox();
        this.cancelButton = new JButton(CANCEL_BUTTON_TEXT);
        cancelPane.add(Box.createHorizontalGlue());
        cancelPane.add(this.cancelButton);
        cancelPane.add(Box.createHorizontalGlue());
        this.cancelButton.setSelected(true);

        // setup details pane
        this.detailsButton = new JButton(NO_DETAILS_BUTTON_TEXT);
        this.detailsButton.setEnabled(exc != null);
        JComponent detailsButtonPane = Box.createHorizontalBox();
        detailsButtonPane.add(this.cancelButton);
        detailsButtonPane.add(Box.createHorizontalGlue());
        detailsButtonPane.add(this.detailsButton);

        this.detailsPane = new JPanel(new BorderLayout());
        this.detailsPane.add(detailsButtonPane, BorderLayout.SOUTH);

        // setup message pane
        JPanel messagePane = new JPanel(new BorderLayout());
        messagePane.setPreferredSize(new Dimension(300, 100));
        messagePane.add(this.detailsPane, BorderLayout.CENTER);

        // setup option pane
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE,
            JOptionPane.DEFAULT_OPTION, null, new Object[] {messagePane});
        optionPane.add(messagePane, BorderLayout.SOUTH);
        // setup content pane
        Container contentPane = getContentPane();
        contentPane.add(optionPane);
        // constrain size to maximum
        setPreferredSize();

        // setup text area
        this.detailsArea = new JTextArea();
        this.detailsArea.setEditable(false);
        this.detailsTextPane = new JScrollPane(this.detailsArea);
        this.detailsTextPane.setPreferredSize(new Dimension(300, 100));
        this.detailsTextPane.setBorder(new EmptyBorder(5, 0, 5, 0));
        setDetailsLevel(Details.NO);

        // setup button listener
        ActionListener buttonListener = new ButtonListener();
        this.cancelButton.addActionListener(buttonListener);
        this.detailsButton.addActionListener(buttonListener);
        pack();
    }

    /**
     * Sets the level of details in the text area of the error dialog. Legal
     * values are:
     * <ul>
     * <li> <code>NO_DETAILS</code>: the text area is hidden
     * <li> <code>SOME_DETAILS</code>: the text area is shown with just the
     * message in the exception
     * <li> <code>FULL_DETAILS</code>: the text area is shown with the error
     * stack trace
     * </ul>
     * @param detailsLevel the desired level of details
     */
    public void setDetailsLevel(Details detailsLevel) {
        switch (detailsLevel) {
        case NO:
            this.detailsPane.remove(this.detailsTextPane);
            break;
        case SOME:
            this.detailsArea.setText(this.exc.getMessage() + "\n");
            this.detailsPane.add(this.detailsTextPane, BorderLayout.CENTER);
            this.detailsTextPane.scrollRectToVisible(new Rectangle(0, 0, 10, 10));
            break;
        case FULL:
            this.detailsArea.setText("");
            this.detailsArea.append(this.exc.toString() + "\n");
            StackTraceElement[] traceElems = this.exc.getStackTrace();
            for (StackTraceElement element : traceElems) {
                this.detailsArea.append("    " + element.toString() + "\n");
            }
            this.detailsPane.add(this.detailsTextPane, BorderLayout.CENTER);
            this.detailsArea.scrollRectToVisible(new Rectangle(0, 0, 10, 10));
            break;
        default:
            throw Exceptions.UNREACHABLE;
        }
        this.detailsButton.setText(detailsLevel.text());
        repaint();
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
    protected Details detailsLevel = Details.NO;

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

    /** Resets the content pane's preferred size with a maximum width of 500. */
    private void setPreferredSize() {
        Container contentPane = getContentPane();
        contentPane.setPreferredSize(null);
        Dimension size = contentPane.getPreferredSize();
        size.width = Math.min(size.width, 500);
        contentPane.setPreferredSize(size);
    }

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

    /** Level of detail shown in dialog. */
    public static enum Details {
        /** Details status: no details. */
        NO("Details >>"),
        /** Details status: some details. */
        SOME("More >>"),
        /** Details status: full details. */
        FULL("Details <<");

        private Details(String text) {
            this.text = text;
        }

        /** Returns the text corresponding to this detail level. */
        public String text() {
            return this.text;
        }

        /** Returns the next level in a round-robin scheme. */
        public Details next() {
            return switch (this) {
            case NO -> SOME;
            case SOME -> FULL;
            case FULL -> NO;
            };
        }

        private final String text;
    }

    /** Action listener that takes care of the dialog buttons. */
    protected class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            Object source = evt.getSource();
            if (source.equals(ErrorDialog.this.detailsButton)) {
                ErrorDialog.this.detailsLevel = ErrorDialog.this.detailsLevel.next();
                if (ErrorDialog.this.detailsLevel == Details.SOME
                    && ErrorDialog.this.exc.getMessage() == null) {
                    ErrorDialog.this.detailsLevel = ErrorDialog.this.detailsLevel.next();
                }
                setDetailsLevel(ErrorDialog.this.detailsLevel);
            } else if (source.equals(ErrorDialog.this.cancelButton)) {
                setVisible(false);
            }
        }
    }
}
