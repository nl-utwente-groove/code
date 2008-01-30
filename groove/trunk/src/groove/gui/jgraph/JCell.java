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
 * $Id: JCell.java,v 1.6 2008-01-30 09:33:12 iovka Exp $
 */
package groove.gui.jgraph;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.jgraph.graph.GraphCell;

/**
 * Interface for <tt>GraphCell</tt>s whose user objects are based
 * on a set of strings, displayed in multiline format but edited in
 * single-line format.
 * @author Arend Rensink
 * @version $Revision: 1.6 $
 */
public interface JCell extends GraphCell, Serializable {
	/** 
	 * Returns the complete text that should be displayed upon the cell.
	 * This is obtained from {@link #getLines()} by inserting appropriate
	 * line separators. 
	 */
	public String getText();
	/**
	 * Returns the collection of lines to be displayed upon the cell.
	 * These are the lines that make up the text returned by {@link #getText()}.
	 * The test is html-formatted, but without the surrounding html-tag.
	 */
	public abstract List<StringBuilder> getLines();
	/** Indicates if the cell is currently visible in the j-model. */
	public boolean isVisible();
	/** Indicates if the cell should be registered in the label list. */
	public boolean isListable();
    /**
     * Returns the set of labels to be associated with this cell in a label list.
     */
    public abstract Collection<String> getListLabels();
    /**
     * Returns the set of labels to be shown on this cell in a plain graph view.
     * A plain graph view is the representation used in the editor. 
     */
    public abstract Collection<String> getPlainLabels();
    
    /**
     * Returns a {@link JCellContent}, which is a set of objects associated with this
     * cell.
     * @ensure <tt>result instanceof JUserObject</tt>
     */
    public abstract JCellContent<?> getUserObject();
    
    /**
     * Returns tool tip text for this j-cell.
     */
    public abstract String getToolTipText();
}