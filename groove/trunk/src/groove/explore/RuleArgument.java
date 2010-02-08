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
package groove.explore;

import groove.explore.Serialized.SerializedArgument;
import groove.gui.Simulator;
import groove.gui.StatusPanel;
import groove.gui.dialog.ExplorationDialog;
import groove.trans.RuleName;
import groove.view.FormatException;
import groove.view.RuleView;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;

/**
 * The rule argument of a Serialized<?>.
 */
public class RuleArgument implements SerializedArgument {

    // Serialized representation of the rule.
    private String ruleName;

    /**
     * Constructor. Sets an explicit initial value.
     */
    public RuleArgument(String initialValue) {
        this.ruleName = initialValue;
    }

    @Override
    public String getSerializedValue() {
        return this.ruleName.toString();
    }

    @Override
    public void setSerializedValue(String ruleName) {
        this.ruleName = ruleName;
    }

    @Override
    public Object getValue(Simulator simulator) {
        RuleName rn = new RuleName(this.ruleName);
        RuleView rv = simulator.getGrammarView().getRuleView(rn);
        if (rv == null) {
            return null;
        } else {
            try {
                return rv.toRule();
            } catch (FormatException e) {
                return null;
            }
        }
    }

    @Override
    public StatusPanel createSelectorPanel(Simulator simulator) {
        return new SelectorPanel(simulator, this);
    }

    private class SelectorPanel extends StatusPanel implements ActionListener {

        // Reference to the surrounding RuleArgument.
        private RuleArgument parent;

        // The JComboBox that holds the rule selector. Used by action listener.
        private final JComboBox ruleSelector;

        // Sorted array of enabled rules.
        private final EnabledRule[] enabledRules;

        /**
         * Create the panel and all its elements.
         */
        public SelectorPanel(Simulator simulator, RuleArgument parent) {

            // Initialize panel and save local arguments.
            super(new FlowLayout(FlowLayout.LEFT, 0, 0));
            setBackground(ExplorationDialog.INFO_BG_COLOR);
            this.parent = parent;

            // Create a sorted array of enabled rules, and store it.
            this.enabledRules =
                EnabledRule.findEnabledRules(simulator.getGrammarView());

            // Create the rule selector (a JComboBox) and add it to the panel.
            // Treat an empty list of enabled rules as a special case.
            if (this.enabledRules.length == 0) {
                String[] errorArray = new String[1];
                errorArray[0] =
                    "<HTML><FONT color=red>"
                        + "Error! No enabled rule available."
                        + "</FONT></HTML>";
                this.ruleSelector = new JComboBox(errorArray);
                add(this.ruleSelector);
                setStatus(false);
            } else {
                this.ruleSelector = new JComboBox(this.enabledRules);
                this.ruleSelector.addActionListener(this);
                add(this.ruleSelector);
                setStatus(true);
            }
            this.ruleSelector.setBackground(ExplorationDialog.INFO_BOX_BG_COLOR);

            // Find the initially selected rule, which should correspond to
            // the parent ruleName if possible (otherwise choose the first).
            int selectedIndex = 0;
            String initialRule = this.parent.getSerializedValue();
            boolean initialValueValid = false;
            for (int i = 0; i < this.enabledRules.length; i++) {
                if (initialRule.equals(this.enabledRules[i].getRuleName().toString())) {
                    selectedIndex = i;
                    initialValueValid = true;
                }
            }

            // Reset rule name if the initial value was not valid.
            if (!initialValueValid && getStatus()) {
                RuleName name = this.enabledRules[selectedIndex].getRuleName();
                this.parent.setSerializedValue(name.toString());
            }

            // Set the selected index in the JComboBox.
            if (getStatus()) {
                this.ruleSelector.setSelectedIndex(selectedIndex);
            }

            // Add the post-fix identification.
            add(new JLabel("<HTML><B><FONT color="
                + ExplorationDialog.INFO_COLOR + ">&nbsp(rule)</B></HTML>"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Ignore event and query the JComboBox directly.
            int selectedIndex = this.ruleSelector.getSelectedIndex();
            RuleName name = this.enabledRules[selectedIndex].getRuleName();
            this.parent.setSerializedValue(name.toString());
        }
    }
}