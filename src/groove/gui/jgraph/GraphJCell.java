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
 * $Id: GraphJCell.java,v 1.5 2008-01-30 09:33:13 iovka Exp $
 */
package groove.gui.jgraph;

import groove.graph.EdgeRole;
import groove.graph.Element;
import groove.graph.TypeLabel;
import groove.gui.look.Look;
import groove.gui.look.VisualKey;
import groove.gui.look.VisualMap;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jgraph.graph.GraphCell;

/**
 * Extension of a graph cell that recognises that cells have underlying edges.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface GraphJCell extends GraphCell, Serializable {
    /** Returns the fixed jGraph on which this jCell is displayed. */
    public GraphJGraph getJGraph();

    /** Returns the fixed jModel to which this jCell belongs. */
    public GraphJModel<?,?> getJModel();

    /** Returns the end nodes (for an edge) or the incident edges (for a vertex). */
    public Collection<? extends GraphJCell> getContext();

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

    /**
     * Returns the set of keys to be associated with this cell in a label
     * tree.
     */
    public abstract Collection<? extends Element> getKeys();

    /** Indicates if this cell is currently layed-out. */
    boolean isLayoutable();

    /** 
     * Sets this cell to layed-out. 
     * This means that the next attempt to layout the graph will not
     * change the position of this cell.
     * @return {@code true} if the layed-out status changed as a result of this call
     */
    boolean setLayoutable(boolean layoutable);

    /** Indicates if this cell is currently grayed-out. */
    boolean isGrayedOut();

    /** 
     * Sets this cell to grayed-out. 
     * @return {@code true} if the grayed-out status changed as a result of this call
     */
    boolean setGrayedOut(boolean gray);

    /** 
     * Indicates if there are errors in this JCell;
     * i.e., if {@link #getErrors()} would return a non-empty object.
     */
    boolean hasErrors();

    /** 
     * Returns the errors in this jCell.
     */
    AspectJCellErrors getErrors();

    /**
     * Returns tool tip text for this j-cell.
     */
    public abstract String getToolTipText();

    /** Returns the looks of this JCell. */
    public Set<Look> getLooks();

    /** Sets or resets a look value. */
    public boolean setLook(Look look, boolean set);

    /** 
     * Returns the visual aspects of this JCell.
     * These are derived from the looks, together with the 
     * layout information and text label(s) of the cell.
     */
    public VisualMap getVisuals();

    /**
     * Sets a number of refreshable visual keys to stale, meaning that 
     * their values in the visual map should be refreshed.
     * @param keys the keys to be refreshed
     */
    public void setStale(VisualKey... keys);

    /** Pseudo-label for cells with an empty list label set. */
    static public final TypeLabel NO_LABEL = TypeLabel.createLabel(
        EdgeRole.NODE_TYPE, "\u0000");
}
