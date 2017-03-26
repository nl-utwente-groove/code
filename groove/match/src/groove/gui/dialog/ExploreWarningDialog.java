/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.itextpdf.text.Font;

/**
 * Dialog to warn of a large state space during exploration,
 * and set the next bound to explore to.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ExploreWarningDialog {
    private ExploreWarningDialog() {
        this.listener = new MyPropertyListener();
    }

    /** 
     * Shows the dialog, and returns {@code true} if the user chose to continue.
     * Afterwards, {@link #getBound()} shows the next bound to which to explore.
     */
    public boolean ask(Frame owner) {
        getMessageLabel().setText(String.format("Exploration has generated %s states", getBound()));
        getBoundSpinnerModel().setMinimum(getBound() + 1);
        getBoundSpinnerModel().setValue(getBound() * 2);
        JDialog dialog = createDialog(owner);
        dialog.setVisible(true);
        dialog.dispose();
        return this.answer;
    }

    private JDialog createDialog(Frame owner) {
        JDialog result = getOptionPane().createDialog(owner, "Exploration Progress");
        result.setAlwaysOnTop(true);
        getListener().setDialog(result);
        return result;
    }

    private JOptionPane getOptionPane() {
        if (this.optionPane == null) {
            this.optionPane =
                new JOptionPane(getMessagePanel(), JOptionPane.QUESTION_MESSAGE,
                    JOptionPane.OK_CANCEL_OPTION);
            this.optionPane.addPropertyChangeListener(this.listener);
        }
        return this.optionPane;
    }

    private JOptionPane optionPane;

    private JPanel getMessagePanel() {
        if (this.messagePanel == null) {
            this.messagePanel = new JPanel();
            this.messagePanel.setLayout(new BoxLayout(this.messagePanel, BoxLayout.Y_AXIS));
            this.messagePanel.add(getMessageLabel());
            this.messagePanel.add(Box.createRigidArea(new Dimension(0, 5)));
            this.messagePanel.add(getChoiceComponent());
        }
        return this.messagePanel;
    }

    private JPanel messagePanel;

    /** Lazily constructs and returns the label showing the main message. */
    private JLabel getMessageLabel() {
        if (this.messageLabel == null) {
            this.messageLabel = new JLabel();
            this.messageLabel.setFont(this.messageLabel.getFont().deriveFont(Font.BOLD));
            this.messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        return this.messageLabel;
    }

    private JLabel messageLabel;

    /** Lazily constructs and returns the component for choosing the next bound. */
    private JPanel getChoiceComponent() {
        JPanel result = this.choiceComponent;
        if (result == null) {
            ButtonGroup buttons = new ButtonGroup();
            buttons.add(getBoundedButton());
            buttons.add(getUnboundedButton());
            // build panel containing bounded button and spinner
            JPanel boundedPanel = new JPanel();
            boundedPanel.setLayout(new BoxLayout(boundedPanel, BoxLayout.X_AXIS));
            boundedPanel.add(getBoundedButton());
            boundedPanel.add(Box.createRigidArea(new Dimension(3, 0)));
            boundedPanel.add(getBoundSpinner());
            // add glue to allow the spinner to resize
            boundedPanel.add(Box.createGlue());
            boundedPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            // create choice component
            result = new JPanel();
            result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
            result.add(boundedPanel);
            result.add(getUnboundedButton());
            result.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.choiceComponent = result;
        }
        return result;
    }

    private JPanel choiceComponent;

    private JRadioButton getBoundedButton() {
        if (this.boundedButton == null) {
            this.boundedButton = new JRadioButton("Continue up to", true);
        }
        return this.boundedButton;
    }

    private JRadioButton boundedButton;

    private JRadioButton getUnboundedButton() {
        if (this.unboundedButton == null) {
            this.unboundedButton = new JRadioButton("Do unbounded exploration");
            this.unboundedButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        return this.unboundedButton;
    }

    private JRadioButton unboundedButton;

    private JSpinner getBoundSpinner() {
        if (this.boundSpinner == null) {
            this.boundSpinner = new JSpinner(getBoundSpinnerModel());
            Dimension d = this.boundSpinner.getPreferredSize();
            d.width = 30;
            this.boundSpinner.setPreferredSize(d);
            this.boundSpinner.setPreferredSize(d);
        }
        return this.boundSpinner;
    }

    private JSpinner boundSpinner;

    private SpinnerNumberModel getBoundSpinnerModel() {
        if (this.boundSpinnerModel == null) {
            this.boundSpinnerModel = new SpinnerNumberModel();
            this.boundSpinnerModel.setValue(100000);
            this.boundSpinnerModel.setStepSize(100);
        }
        return this.boundSpinnerModel;
    }

    private SpinnerNumberModel boundSpinnerModel;

    boolean isAnswer() {
        return this.answer;
    }

    void setAnswer(boolean answer) {
        this.answer = answer;
    }

    private boolean answer;

    private MyPropertyListener getListener() {
        return this.listener;
    }

    private final MyPropertyListener listener;

    /** Sets the current bound up to which exploration has continued. */
    public void setBound(int bound) {
        this.bound = bound;
    }

    /**
     * After the dialog has closed, this contains the next bound up to
     * which exploration should continue.
     * A value of 0 stands for unbounded.
     */
    public int getBound() {
        return this.bound;
    }

    private int bound;

    /** Returns the singleton instance of this class. */
    public static ExploreWarningDialog instance() {
        return instance;
    }

    private static ExploreWarningDialog instance = new ExploreWarningDialog();

    /** Sets {@link ExploreWarningDialog#answer} in response to a value change. */
    private class MyPropertyListener implements PropertyChangeListener {
        MyPropertyListener() {
            //
        }

        void setDialog(JDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (this.dialog != null && this.dialog.isVisible()
                && evt.getNewValue() instanceof Integer
                && evt.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) {
                Integer value = (Integer) evt.getNewValue();
                setAnswer(value == JOptionPane.OK_OPTION);
                if (getBoundedButton().isSelected()) {
                    setBound((Integer) getBoundSpinnerModel().getValue());
                } else {
                    setBound(Integer.MAX_VALUE);
                }
                this.dialog.setVisible(false);
                this.dialog = null;
            }
        }

        private JDialog dialog;
    }
}
