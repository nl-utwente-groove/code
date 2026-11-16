/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
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
package nl.utwente.groove.gui.yfiles;

import java.awt.GridLayout;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.AIGenerated;

/**
 * Panel with the settings of one layouter, as shown by the layout dialog.
 * Every control applies its value through the setter it was created with and
 * then restarts the layouter, so that the effect of a setting is seen at once
 * (as the JGraph backend's panels do). The setters are plain lambdas: the
 * yFiles license does not allow reflection over the library's classes.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
class LayoutSettingsPanel extends JPanel {
    /**
     * Creates an empty panel.
     * @param restart the action that restarts the layouter, invoked after every change
     */
    LayoutSettingsPanel(Runnable restart) {
        this.restart = restart;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    }

    /** The action that restarts the layouter, invoked after every change. */
    private final Runnable restart;

    /** Adds a check box for a boolean setting. */
    void addCheckBox(String label, boolean initial, Consumer<Boolean> setter) {
        JCheckBox checkBox = new JCheckBox(label, initial);
        checkBox.addItemListener(e -> {
            setter.accept(checkBox.isSelected());
            this.restart.run();
        });
        add(checkBox);
    }

    /** Adds a spinner for a numeric setting. */
    void addSpinner(String label, double initial, double min, double max, double step,
                    DoubleConsumer setter) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(initial, min, max, step));
        spinner.addChangeListener(e -> {
            setter.accept(((Number) spinner.getValue()).doubleValue());
            this.restart.run();
        });
        JLabel spinnerLabel = new JLabel(label);
        spinnerLabel.setLabelFor(spinner);
        add(spinnerLabel);
        add(spinner);
    }

    /**
     * Adds a group of radio buttons for a setting with a fixed range of values.
     * @param title title of the group
     * @param labels labels of the values, in order
     * @param initial index of the initially selected value
     * @param setter receives the index of the selected value
     */
    void addChoice(String title, List<String> labels, int initial, IntConsumer setter) {
        ButtonGroup group = new ButtonGroup();
        JPanel choicePanel = new JPanel(new GridLayout(0, 2));
        for (int i = 0; i < labels.size(); i++) {
            int index = i;
            JRadioButton button = new JRadioButton(labels.get(i), i == initial);
            button.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 4));
            button.addActionListener(e -> {
                setter.accept(index);
                this.restart.run();
            });
            group.add(button);
            choicePanel.add(button);
        }
        choicePanel
            .setBorder(BorderFactory
                .createTitledBorder(BorderFactory.createEtchedBorder(), title));
        add(choicePanel);
    }
}
