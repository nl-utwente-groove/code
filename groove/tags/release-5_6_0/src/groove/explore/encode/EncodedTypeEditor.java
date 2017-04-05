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

import groove.grammar.model.GrammarModel;

import java.awt.LayoutManager;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * <!=========================================================================>
 * An EncodedTypeEditor<B> is an editor for values of type B that represent
 * values of a type A. It is basically an arbitrary JPanel that is extended
 * with a getter and a setter for a B.
 * Note that the type A is never used locally; it is only provided as
 * additional documentation.
 * <!=========================================================================>
 * @author Maarten de Mol
 */
public abstract class EncodedTypeEditor<A,B> extends JPanel {
    /**
     * Constructor for the case with layout manager.
     */
    public EncodedTypeEditor(GrammarModel grammar, LayoutManager layout) {
        super(layout);
        this.grammar = grammar;
        this.listeners = new ArrayList<>();
    }

    /**
     * Getter for the current value. Returns null if no valid value is
     * currently selected.
     */
    public abstract B getCurrentValue();

    /**
     * Setter for the current value. Will be ignored if B is not a valid
     * encoding of an A.
     */
    public abstract void setCurrentValue(B value);

    /** Reloads the content of the editor. */
    public abstract void refresh();

    /** Returns the grammar on which this editor is based. */
    protected GrammarModel getGrammar() {
        return this.grammar;
    }

    /**
     * Adds a listener, which will be invoked each time the selected
     * Template changes in the created editor.
     */
    public void addTemplateListener(TemplateListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a listener.
     */
    public void removeTemplateListener(TemplateListener listener) {
        this.listeners.remove(listener);
    }

    /** 
     * Notifies all registered {@link TemplateListener}s
     * by invoking {@link TemplateListener#templateEdited()}.
     */
    protected void notifyTemplateListeners() {
        for (TemplateListener listener : this.listeners) {
            listener.templateEdited();
        }
    }

    private final GrammarModel grammar;
    /** List of listeners connected to this list */
    private final ArrayList<TemplateListener> listeners;
}
