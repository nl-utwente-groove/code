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

import groove.gui.Simulator;
import groove.gui.dialog.ExplorationDialog;
import groove.gui.layout.SpringUtilities;
import groove.lts.GTS;
import groove.view.FormatException;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * <!=========================================================================>
 * A TemplateList<A> describes the encoding of values of type A by means of a
 * Serialized. The encoding is basically the union of the encodings of a list
 * of Template<A>'s.
 * <!=========================================================================>
 * @author Maarten de Mol
 */
public class TemplateList<A> implements EncodedType<A,Serialized> {

    private final LinkedList<Template<A>> templates;
    private final String typeIdentifier;
    private final String typeToolTip;
    private LinkedList<TemplateListListener> listeners;

    /**
     * Constructor. Initializes an identifier and tool-tip for the type A.
     * Creates an empty list of held templates.
     */
    public TemplateList(String typeIdentifier, String typeToolTip) {
        this.templates = new LinkedList<Template<A>>();
        this.typeIdentifier = typeIdentifier;
        this.typeToolTip = typeToolTip;
        this.listeners = new LinkedList<TemplateListListener>();
    }

    /**
     * Getter for the typeIdentifier.
     */
    public String getTypeIdentifier() {
        return this.typeIdentifier;
    }

    /**
     * Add a template. The keyword of the template is assumed to be unique
     * with respect to the already stored templates.
     */
    public void addTemplate(Template<A> template) {
        this.templates.add(template);
    }

    /**
     * Adds a listener, which will be invoked each time the selected
     * Template changes in the created editor.
     */
    public void addListener(TemplateListListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a listener.
     */
    public void removeListener(TemplateListListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Create the type-specific editor (see class TemplateListEditor below).
     */
    @Override
    public EncodedTypeEditor<A,Serialized> createEditor(Simulator simulator) {
        return new TemplateListEditor<A>(simulator);
    }

    /**
     * Create an A out of a Serialized by finding the template that starts
     * with the given keyword and then using its parse method.
     */
    @Override
    public A parse(GTS gts, Serialized source) throws FormatException {
        for (Template<A> template : this.templates) {
            if (template.getKeyword().equals(source.getKeyword())) {
                return template.parse(gts, source);
            }
        }

        StringBuffer error = new StringBuffer();
        error.append("Unknown keyword '" + source.getKeyword() + "' for the "
            + this.typeIdentifier + ".\n");
        error.append("Expected one of the following keywords:");
        for (Template<A> template : this.templates) {
            error.append(" '");
            error.append(template.getKeyword());
            error.append("'");
        }
        error.append(".");
        throw new FormatException(error.toString());
    }

    /**
     * Parses a command line argument into a <code>Serialized</code> that
     * represents one of the templates. Returns <code>null</code> if parsing
     * fails.
     */
    public Serialized parseCommandline(String text) {
        for (Template<A> template : this.templates) {
            Serialized result = template.parseCommandline(text);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns a description of the grammar that is used to parse this template
     * list on the command line. The grammar is displayed as a (pretty-printed)
     * array of regular expressions, one for each available template. 
     */
    public String[] describeCommandlineGrammar() {
        String[] desc = new String[this.templates.size()];
        int index = 0;

        for (Template<A> template : this.templates) {
            desc[index] = template.describeCommandlineGrammar();
            index++;
        }
        return desc;
    }

    /**
     * <!--------------------------------------------------------------------->
     * A TemplateListEditor<A> is the type-specific editor that is associated
     * with the TemplateList. It consists of two components: a listPanel,
     * which displays a list of the names of the available templates, and an
     * infoPanel, which is a CardLayout of the editors belonging to the
     * templates. 
     * <!--------------------------------------------------------------------->
     */
    private class TemplateListEditor<X> extends EncodedTypeEditor<X,Serialized>
            implements ListSelectionListener {

        private final Map<String,EncodedTypeEditor<A,Serialized>> editors =
            new TreeMap<String,EncodedTypeEditor<A,Serialized>>();
        private ArrayList<String> templateKeywords;
        private ArrayList<String> templateNames;
        private JList nameSelector;
        private JPanel infoPanel;

        public TemplateListEditor(Simulator simulator) {
            super(new SpringLayout());
            extractFromTemplates(simulator);
            addHeaderText();
            addListPanel();
            addInfoPanel();
            SpringUtilities.makeCompactGrid(this, 3, 1, 0, 0, 0, 3);
        }

        private void extractFromTemplates(Simulator simulator) {
            int nrTemplates = TemplateList.this.templates.size();
            this.templateKeywords = new ArrayList<String>(nrTemplates);
            this.templateNames = new ArrayList<String>(nrTemplates);
            for (Template<A> template : TemplateList.this.templates) {
                this.templateKeywords.add(template.getKeyword());
                this.templateNames.add(template.getName());
                this.editors.put(template.getKeyword(),
                    template.createEditor(simulator));
            }
        }

        private void addHeaderText() {
            JLabel headerText =
                new JLabel("<HTML><B><FONT color="
                    + ExplorationDialog.HEADER_COLOR + ">Select "
                    + TemplateList.this.typeIdentifier + ":");
            headerText.setToolTipText(TemplateList.this.typeToolTip);
            add(headerText);
        }

        private void addListPanel() {
            this.nameSelector = new JList(this.templateNames.toArray());
            this.nameSelector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.nameSelector.setSelectedIndex(0);
            this.nameSelector.addListSelectionListener(this);
            JScrollPane listScroller = new JScrollPane(this.nameSelector);
            listScroller.setPreferredSize(new Dimension(350, 200));
            add(listScroller);
        }

        private void addInfoPanel() {
            this.infoPanel = new JPanel(new CardLayout());
            this.infoPanel.setPreferredSize(new Dimension(350, 200));
            this.infoPanel.setBorder(BorderFactory.createLineBorder(new Color(
                150, 150, 255)));
            for (String keyword : this.templateKeywords) {
                this.infoPanel.add(this.editors.get(keyword), keyword);
            }
            add(this.infoPanel);
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            int selectedIndex = this.nameSelector.getSelectedIndex();
            String selectedKeyword = this.templateKeywords.get(selectedIndex);
            CardLayout cards = (CardLayout) (this.infoPanel.getLayout());
            cards.show(this.infoPanel, selectedKeyword);
            for (TemplateListListener listener : TemplateList.this.listeners) {
                listener.selectionChanged();
            }
        }

        @Override
        public Serialized getCurrentValue() {
            int selectedIndex = this.nameSelector.getSelectedIndex();
            String selectedKeyword = this.templateKeywords.get(selectedIndex);
            return this.editors.get(selectedKeyword).getCurrentValue();
        }

        @Override
        public void setCurrentValue(Serialized value) {
            if (this.templateKeywords.contains(value.getKeyword())) {
                this.editors.get(value.getKeyword()).setCurrentValue(value);
                this.nameSelector.setSelectedIndex(this.templateKeywords.indexOf(value.getKeyword()));
                CardLayout cards = (CardLayout) (this.infoPanel.getLayout());
                cards.show(this.infoPanel, value.getKeyword());
            }
        }
    }

    /**
     * <!--------------------------------------------------------------------->
     * A TemplateListListener describes an interface for objects that need to
     * react to changes on the editor that is created by the TemplateList. 
     * <!--------------------------------------------------------------------->
     */
    public interface TemplateListListener {
        /**
         * Invoked whenever a new Template is selected, either by the user
         * or by setCurrentValue (which calls the ActionListener implicitly).
         */
        public void selectionChanged();
    }
}
