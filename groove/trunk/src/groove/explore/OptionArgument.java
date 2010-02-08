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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;

/**
 * A Panel in which the user can select an option from a fixed list.
 * The selected option is stored in an argument of a Serialized<?> object.
 */
public class OptionArgument implements SerializedArgument {

    // The identifier for the list of options as a whole.
    private String identifier;

    // The index of the option that is currently selected.
    private int selectedIndex;

    // The available options, consisting of a keyword and an identifier. 
    // Maximum number of allowed options is 25.
    private String[] keywords = new String[25];
    private String[] identifiers = new String[25];
    private int nrOptions;

    /**
     * Constructor. WARNING: does not set an initial value. Instead, first
     * use addOption to add the options, and then use setSerializedValue.
     */
    public OptionArgument(String identifier) {
        this.identifier = identifier;
        this.nrOptions = 0;
    }

    /**
     * Add an available option (keyword + identifier).
     */
    public void addOption(String keyword, String identifier) {
        this.keywords[this.nrOptions] = keyword;
        this.identifiers[this.nrOptions] = identifier;
        this.nrOptions++;
    }

    /**
     * Query the number of available options.
     */
    public int getNrOptions() {
        return this.nrOptions;
    }

    @Override
    public String getSerializedValue() {
        return this.keywords[this.selectedIndex];
    }

    @Override
    public void setSerializedValue(String keyword) {
        for (int i = 0; i < this.nrOptions; i++) {
            if (this.keywords[i].equals(keyword)) {
                this.selectedIndex = i;
            }
        }
    }

    @Override
    public Object getValue(Simulator simulator) {
        return this.getSerializedValue();
    }

    @Override
    public StatusPanel createSelectorPanel(Simulator simulator) {
        return new SelectorPanel(simulator, this, this.identifier);
    }

    /**
     * Special setter, for use in the SelectorPanel only.
     */
    protected void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    /**
     * Special getter, for use in the SelectorPanel only.
     */
    protected int getSelectedIndex() {
        return this.selectedIndex;
    }

    /**
     * Special query operation. For use in the SelectorPanel only.
     */
    protected String getIdentifierAt(int index) {
        return this.identifiers[index];
    }

    private class SelectorPanel extends StatusPanel implements ActionListener {

        // Reference to the surrounding OptionArgument.
        private OptionArgument parent;

        // The JComboBox that holds the option selector. Used by action listener.
        private final JComboBox optionSelector;

        /**
         * Create the panel and all its elements.
         */
        public SelectorPanel(Simulator simulator, OptionArgument parent,
                String identifier) {

            // Initialize panel and save local arguments.
            super(new FlowLayout(FlowLayout.LEFT, 0, 0));
            setBackground(ExplorationDialog.INFO_BG_COLOR);
            this.parent = parent;

            // Create the option selector (a JComboBox) and add it to the panel.
            this.optionSelector = new JComboBox();
            for (int i = 0; i < this.parent.getNrOptions(); i++) {
                this.optionSelector.addItem("<HTML><FONT color="
                    + ExplorationDialog.INFO_COLOR + ">"
                    + this.parent.getIdentifierAt(i) + "</FONT></HTML>");
            }
            this.optionSelector.addActionListener(this);
            add(this.optionSelector);
            setStatus(true);

            // Set selected index.
            this.optionSelector.setSelectedIndex(this.parent.getSelectedIndex());

            // Add the post-fix identification.
            add(new JLabel("<HTML><B><FONT color="
                + ExplorationDialog.INFO_COLOR + ">&nbsp(" + identifier
                + ")</B></HTML>"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Ignore event and query the JComboBox directly.
            int selectedIndex = this.optionSelector.getSelectedIndex();
            this.parent.setSelectedIndex(selectedIndex);
        }
    }
}