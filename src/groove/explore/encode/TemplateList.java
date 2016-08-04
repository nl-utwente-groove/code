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

import static groove.io.HTMLConverter.HTML_TAG;
import static groove.io.HTMLConverter.STRONG_TAG;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import groove.explore.ParsableValue;
import groove.grammar.Grammar;
import groove.grammar.model.GrammarModel;
import groove.gui.dialog.ExplorationDialog;
import groove.gui.layout.SpringUtilities;
import groove.util.Version;
import groove.util.parse.FormatException;

/**
 * <!=========================================================================>
 * A TemplateList<A> describes the encoding of values of type A by means of a
 * Serialized. The encoding is basically the union of the encodings of a list
 * of Template<A>'s.
 * <!=========================================================================>
 * @author Maarten de Mol
 */
public abstract class TemplateList<A> implements EncodedType<A,Serialized> {

    /** The list of templates. */
    private final ArrayList<Template<A>> templates;
    /** String identifying the type. */
    private final String typeIdentifier;
    /** The tool tip string. */
    private final String typeToolTip;
    /** Mask for the subset of templates that are available in the editor. */
    private Set<? extends ParsableValue> mask;

    /**
     * Constructor. Initializes an identifier and tool-tip for the type A.
     * Creates an empty list of held templates.
     */
    public TemplateList(String typeIdentifier, String typeToolTip) {
        this.templates = new ArrayList<>(15);
        this.typeIdentifier = typeIdentifier;
        this.typeToolTip = typeToolTip;
    }

    /**
     * Getter for the typeIdentifier.
     */
    public String getTypeIdentifier() {
        return this.typeIdentifier;
    }

    /**
     * Adds a template. The keyword of the template is assumed to be unique
     * with respect to the already stored templates.
     */
    protected void addTemplate(Template<A> template) {
        if (this.mask == null || this.mask.contains(template.getValue())) {
            boolean fresh = this.templates.add(template);
            assert fresh;
        }
    }

    /**
     * Create the type-specific editor (see class TemplateListEditor below).
     */
    @Override
    public EncodedTypeEditor<A,Serialized> createEditor(GrammarModel grammar) {
        return new TemplateListEditor<>(grammar);
    }

    /**
     * Create an A out of a Serialized by finding the template that starts
     * with the given keyword and then using its parse method.
     */
    @Override
    public A parse(Grammar rules, Serialized source) throws FormatException {
        for (Template<A> template : this.templates) {
            if (template.getKeyword()
                .equals(source.getKeyword())) {
                return template.parse(rules, source);
            }
        }

        StringBuffer error = new StringBuffer();
        error.append(
            "Unknown keyword '" + source.getKeyword() + "' for the " + this.typeIdentifier + ".\n");
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
        Serialized result = null;
        for (Template<A> template : this.templates) {
            result = template.parseCommandline(text);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    /** Inverse operation to {@link #parseCommandline(String)}. */
    public String toParsableString(Serialized source) {
        source = source.clone();
        for (Template<A> template : this.templates) {
            if (template.getKeyword()
                .equals(source.getKeyword())) {
                return template.toParsableString(source);
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
            new TreeMap<>();
        private ArrayList<String> templateKeywords;
        private JList<String> nameSelector;
        private JPanel infoPanel;

        public TemplateListEditor(GrammarModel grammar) {
            super(grammar, new SpringLayout());
            extractFromTemplates();
            addHeaderText();
            addListPanel();
            addInfoPanel();
            SpringUtilities.makeCompactGrid(this, 3, 1, 0, 0, 0, 3);
            refresh();
        }

        @Override
        public void refresh() {
            int nrTemplates = TemplateList.this.templates.size();
            List<String> templateNames = new ArrayList<>(nrTemplates);
            for (Template<A> template : TemplateList.this.templates) {
                if (Version.isDevelopmentVersion() || !template.getValue()
                    .isDevelopment()) {
                    String templateName = template.getName();
                    if (template.getValue()
                        .isDefault(getGrammar())) {
                        templateName = HTML_TAG.on(STRONG_TAG.on(templateName + " (default)"));
                    }
                    templateNames.add(templateName);
                }
            }
            int selected = this.nameSelector.getSelectedIndex();
            this.nameSelector.removeListSelectionListener(this);
            this.nameSelector.setListData(templateNames.toArray(new String[templateNames.size()]));
            if (selected >= 0) {
                this.nameSelector.setSelectedIndex(selected);
            }
            this.nameSelector.addListSelectionListener(this);
        }

        private void extractFromTemplates() {
            int nrTemplates = TemplateList.this.templates.size();
            this.templateKeywords = new ArrayList<>(nrTemplates);
            for (Template<A> template : TemplateList.this.templates) {
                if (Version.isDevelopmentVersion() || !template.getValue()
                    .isDevelopment()) {
                    this.templateKeywords.add(template.getKeyword());
                    this.editors.put(template.getKeyword(), template.createEditor(getGrammar()));
                }
            }
        }

        private void addHeaderText() {
            JLabel headerText = new JLabel("<HTML><B><FONT color=" + ExplorationDialog.HEADER_COLOR
                + ">Select " + TemplateList.this.typeIdentifier + ":");
            headerText.setToolTipText(TemplateList.this.typeToolTip);
            add(headerText);
        }

        private void addListPanel() {
            this.nameSelector = new JList<>();
            this.nameSelector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.nameSelector.setSelectedIndex(0);
            JScrollPane listScroller = new JScrollPane(this.nameSelector);
            listScroller.setPreferredSize(new Dimension(350, 200));
            add(listScroller);
        }

        private void addInfoPanel() {
            this.infoPanel = new JPanel(new CardLayout());
            this.infoPanel.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 255)));
            JScrollPane infoScroller = new JScrollPane(this.infoPanel);
            infoScroller
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            infoScroller.setPreferredSize(new Dimension(350, 200));
            for (String keyword : this.templateKeywords) {
                this.infoPanel.add(this.editors.get(keyword), keyword);
            }
            add(infoScroller);
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            int selectedIndex = this.nameSelector.getSelectedIndex();
            String selectedKeyword = this.templateKeywords.get(selectedIndex);
            EncodedTypeEditor<?,?> editor = this.editors.get(selectedKeyword);
            editor.refresh();
            int editorHeight = editor.getMinimumSize().height;
            this.infoPanel.setPreferredSize(new Dimension(0, editorHeight));
            CardLayout cards = (CardLayout) (this.infoPanel.getLayout());
            cards.show(this.infoPanel, selectedKeyword);
            notifyTemplateListeners();
        }

        @Override
        public void addTemplateListener(TemplateListener listener) {
            super.addTemplateListener(listener);
            for (EncodedTypeEditor<?,?> editor : this.editors.values()) {
                editor.addTemplateListener(listener);
            }
        }

        @Override
        public void removeTemplateListener(TemplateListener listener) {
            for (EncodedTypeEditor<?,?> editor : this.editors.values()) {
                editor.removeTemplateListener(listener);
            }
            super.removeTemplateListener(listener);
        }

        @Override
        public Serialized getCurrentValue() {
            Serialized result = null;
            int selectedIndex = this.nameSelector.getSelectedIndex();
            if (selectedIndex >= 0) {
                String selectedKeyword = this.templateKeywords.get(selectedIndex);
                result = this.editors.get(selectedKeyword)
                    .getCurrentValue();
            }
            return result;
        }

        @Override
        public void setCurrentValue(Serialized value) {
            String keyword = value.getKeyword();
            if (this.templateKeywords.contains(keyword)) {
                this.nameSelector.setSelectedIndex(this.templateKeywords.indexOf(keyword));
                EncodedTypeEditor<?,Serialized> editor = this.editors.get(keyword);
                editor.setCurrentValue(value);
                int editorHeight = editor.getMinimumSize().height;
                this.infoPanel.setPreferredSize(new Dimension(0, editorHeight));
                CardLayout cards = (CardLayout) (this.infoPanel.getLayout());
                cards.show(this.infoPanel, value.getKeyword());
            }
        }
    }
}
