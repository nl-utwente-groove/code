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
 * $Id: JCell.java,v 1.6 2008-01-30 09:33:12 iovka Exp $
 */
package groove.gui.jgraph;

import groove.graph.Label;
import groove.graph.LabelKind;
import groove.graph.TypeLabel;
import groove.trans.RuleLabel;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphCell;

/**
 * Interface for <tt>GraphCell</tt>s whose user objects are based on a set of
 * strings, displayed in multiline format but edited in single-line format.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface JCell extends GraphCell, Serializable {
    /** Returns the model with which this cell is associated. */
    public JModel getJModel();

    /**
     * Returns the complete text that should be displayed upon the cell. This is
     * obtained from {@link #getLines()} by inserting appropriate line
     * separators.
     */
    public String getText();

    /**
     * Returns the collection of lines to be displayed upon the cell. These are
     * the lines that make up the text returned by {@link #getText()}. The test
     * is html-formatted, but without the surrounding html-tag.
     */
    public abstract List<StringBuilder> getLines();

    /** Indicates if the cell is currently visible in the j-model. */
    public boolean isVisible();

    /**
     * Returns the set of labels to be associated with this cell in a label
     * list.
     */
    public abstract Collection<? extends Label> getListLabels();

    /** Indicates if this cell is currently emphasised. */
    boolean isEmphasised();

    /**  
     * Sets this cell to emphasised.
     * @return {@code true} if the emphasised status changed as a result of this call
     */
    boolean setEmphasised(boolean emph);

    /** Indicates if this cell is currently grayed-out. */
    boolean isGrayedOut();

    /** 
     * Sets this cell to grayed-out. 
     * @return {@code true} if the grayed-out status changed as a result of this call
     */
    boolean setGrayedOut(boolean gray);

    /** 
     * Indicates if this cell contains an error.
     * This affects the rendering.
     */
    public boolean hasError();

    /**
     * Returns tool tip text for this j-cell.
     */
    public abstract String getToolTipText();

    /** 
     * Creates a fresh attribute map for this cell, with respect to a given model.
     * Also stores the attribute map in the cell.
     */
    public AttributeMap createAttributes(JModel jModel);

    /** Pseudo-label for cells with an empty list label set. */
    static public final TypeLabel NO_LABEL = TypeLabel.createLabel(
        LabelKind.NODE_TYPE, "\u0000");
    /** Pseudo-label for subtype edges. */
    static public final RuleLabel SUBTYPE_LABEL = new RuleLabel(
        TypeLabel.createLabel(LabelKind.NODE_TYPE, "\u0001"));

}