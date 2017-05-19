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
package groove.gui;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

/**
 * Option type that can take either two or three values: <i>Ask</i> and <i>Auto</i>,
 * or <i>Ask</i>, <i>Always</i> or <i>Never</i>. This refers to the setting
 * for some behavioural feature of the tool.
 */
public class BehaviourOption extends JMenu implements ItemListener {
    /**
     * Constructs a menu with a given name, and either 2 or 3 option values.
     * @param name the name of the behaviour option
     * @param choiceCount the number of values should be either 2 or 3
     */
    public BehaviourOption(String name, int choiceCount) {
        super(name);
        if (choiceCount != 2 && choiceCount != 3) {
            throw new IllegalArgumentException(String.format("Number of options cannot be %d",
                choiceCount));
        }
        this.answers = choiceCount == 2 ? standardAnswers2 : standardAnswers3;
        this.answerGroup = new ButtonGroup();
        for (String answer : this.answers) {
            addAnswer(answer);
        }
        getItem(ASK).setSelected(true);
    }

    private void addAnswer(String answer) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(answer);
        this.answerGroup.add(item);
        this.add(item);
        item.addItemListener(this);
    }

    /**
     * Returns the currently selected menu value.
     * @return one of {@link #ASK}, {@link #ALWAYS} or {@link #NEVER}.
     */
    public final int getValue() {
        return this.value;
    }

    /**
     * Sets the menu value to one of {@link #ASK}, {@link #ALWAYS} or
     * {@link #NEVER}.
     */
    public final void setValue(int value) {
        if (value < 0 || value >= getItemCount()) {
            throw new IllegalArgumentException(String.format("Value should be in the range %d-%d",
                0, getItemCount()));
        }
        if (value != this.value) {
            int oldValue = this.value;
            this.value = value;
            getItem(value).setSelected(true);
            firePropertyChange(SELECTION, oldValue, value);
        }
    }

    /** Possible answers of this menu. */
    public List<String> getAnswers() {
        return this.answers;
    }

    /**
     * Indicates if this option is confirmed. This can be either because its
     * value is {@link #ALWAYS}, or because the user has confirmed an
     * appropriate dialog.
     * @param owner the component on which the dialog is to be shown
     * @param question if not <code>null</code>, replaces the name of the
     *        option in the dialog
     * @return <code>true</code> if the option is confirmed.
     */
    public boolean confirm(Component owner, String question) {
        if (this.value == ASK) {
            if (question == null) {
                question = getText();
            }
            List<String> options = getDialogOptions();
            JOptionPane pane =
                new JOptionPane(question, JOptionPane.QUESTION_MESSAGE,
                    JOptionPane.YES_NO_CANCEL_OPTION, null, options.toArray());
            pane.createDialog(owner, DIALOG_TITLE).setVisible(true);
            int dialogValue = options.indexOf(pane.getValue());
            if (dialogValue > NO) {
                setValue(dialogValue - 1);
            }
            return dialogValue == YES || dialogValue - 1 == ALWAYS;
        } else {
            return this.value == ALWAYS;
        }
    }

    private List<String> getDialogOptions() {
        if (this.dialogOptions == null) {
            this.dialogOptions = new ArrayList<>();
            this.dialogOptions.add(YES_TEXT);
            this.dialogOptions.add(NO_TEXT);
            Iterator<String> answerIter = getAnswers().iterator();
            answerIter.next();
            while (answerIter.hasNext()) {
                this.dialogOptions.add(answerIter.next());
            }
        }
        return this.dialogOptions;
    }

    /** Sets this menu's value according to the selected menu item. */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            // look up the item
            int value = 0;
            while (getItem(value) != e.getSource()) {
                value++;
            }
            setValue(value);
        }
    }

    /** Current value of the menu, as an index in {@link #answers}. */
    private int value;
    /** List of answers of this menu. */
    private final List<String> answers;
    /** List of answers of this menu. */
    private List<String> dialogOptions;
    /** Group of answer buttons. */
    private final ButtonGroup answerGroup;

    /** Value belonging to the <i>Ask</i> choice of the menu. */
    static public final int ASK = 0;
    /** Text of the <i>Ask</i> choice of the menu. */
    static public final String ASK_TEXT = "Ask";
    /**
     * Value belonging to the <i>Always</i> (or <i>Auto</i>) choice of the
     * menu.
     */
    static public final int ALWAYS = 1;
    /** Text of the <i>Always</i> choice of the menu. */
    static public final String ALWAYS_TEXT = "Always";
    /** Text of the <i>Auto</i> choice of the menu. */
    static public final String AUTO_TEXT = "Auto";
    /** Value belonging to the <i>Never</i> choice of the menu. */
    static public final int NEVER = 2;
    /** Text of the <i>Never</i> choice of the menu. */
    static public final String NEVER_TEXT = "Never";
    /** Index of the <i>Yes</i> button of the dialog. */
    static private final int YES = 0;
    /** Text of the <i>Yes</i> button of the dialog. */
    static private final String YES_TEXT = "Yes";
    /** Index of the <i>No</i> button of the dialog. */
    static private final int NO = 1;
    /** Text of the <i>No</i> button of the dialog. */
    static private final String NO_TEXT = "No";
    /** Array of values for behaviour menus with 2 options. */
    static private final List<String> standardAnswers2 =
        Collections.unmodifiableList(Arrays.asList(ASK_TEXT, ALWAYS_TEXT));
    /** Array of values for behaviour menus with 3 options. */
    static private final List<String> standardAnswers3 =
        Collections.unmodifiableList(Arrays.asList(ASK_TEXT, ALWAYS_TEXT, NEVER_TEXT));
    /** Title if the dialog displayed by {@link #confirm(Component, String)}. */
    static private final String DIALOG_TITLE = "Confirm";
    /**
     * Property name of the selection property, with values from 0 up to the
     * number of values - 1.
     */
    static public final String SELECTION = "selection";
}