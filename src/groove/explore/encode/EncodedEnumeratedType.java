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
package groove.explore.encode;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JComboBox;

import groove.grammar.model.GrammarModel;
import groove.gui.dialog.ExplorationDialog;

/**
 * <!=========================================================================>
 * An EncodedEnumeratedType<A> represents a (partial) encoding of a value of
 * type A by means of an enumeration of Strings. The generation of the
 * enumeration must be overridden by the subclass, as well as the parse()
 * method. The creation of the editor is defined locally, using an inspection
 * of the enumeration.
 * <!=========================================================================>
 * @author Maarten de Mol
 */
public abstract class EncodedEnumeratedType<A> implements EncodedType<A,String> {

    /**
     * Creates the type-specific editor (see class EnumeratedEditor below).
     */
    @Override
    public EncodedTypeEditor<A,String> createEditor(GrammarModel grammar) {
        return new EnumeratedEditor<>(grammar, generateOptions(grammar));
    }

    /**
     * Defines the EnumeratedType by generating a Map of options that are
     * available for selection.
     * This method must be overridden by the subclass.
     */
    public abstract Map<String,String> generateOptions(GrammarModel grammar);

    /**
     * <!--------------------------------------------------------------------->
     * An EnumeratedEditor<A> is the type-specific editor that is associated
     * with the EncodedEnumeratedType. It basically consists of a JComboBox
     * that presents all available options.
     * <!--------------------------------------------------------------------->
     */
    private class EnumeratedEditor<X> extends EncodedTypeEditor<X,String> {

        private final JComboBox<String> selector;
        private String[] keys;
        private int nrKeys;

        public EnumeratedEditor(GrammarModel grammar, Map<String,String> options) {
            super(grammar, new FlowLayout(FlowLayout.LEFT, 0, 0));
            setBackground(ExplorationDialog.INFO_BG_COLOR);
            this.selector = new JComboBox<>();
            // MdM - line below causes selector not to appear at all
            // this.selector.setMinimumSize(new Dimension(50, 20));
            this.selector.setBackground(ExplorationDialog.INFO_BOX_BG_COLOR);
            this.selector.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    notifyTemplateListeners();
                }
            });
            this.keys = new String[options.size()];
            this.nrKeys = 0;
            if (this.nrKeys == 0) {
                this.selector.addItem("<HTML><FONT color=red>"
                    + "Error! No valid options available." + "</FONT></HTML>");
            }
            refresh();
            add(this.selector);
        }

        @Override
        public void refresh() {
            this.nrKeys = 0;
            int selected = this.selector.getSelectedIndex();
            this.selector.removeAllItems();
            for (Map.Entry<String,String> optionEntry : generateOptions(getGrammar()).entrySet()) {
                this.selector.addItem("<HTML><FONT color=" + ExplorationDialog.INFO_COLOR + ">"
                    + optionEntry.getValue() + "</FONT></HTML>");
                this.keys[this.nrKeys] = optionEntry.getKey();
                this.nrKeys++;
            }
            this.selector.setSelectedIndex(selected < 0 ? 0 : selected);
            //            revalidate();
            //            repaint();
        }

        @Override
        public String getCurrentValue() {
            if (this.nrKeys == 0) {
                return null;
            } else {
                return this.keys[this.selector.getSelectedIndex()];
            }
        }

        @Override
        public void setCurrentValue(String value) {
            for (int i = 0; i < this.nrKeys; i++) {
                if (this.keys[i].equals(value)) {
                    this.selector.setSelectedIndex(i);
                }
            }
        }
    }
}