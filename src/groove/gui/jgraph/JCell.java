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
 * $Id: JCell.java,v 1.1.1.1 2007-03-20 10:05:32 kastenberg Exp $
 */
package groove.gui.jgraph;

import java.io.Serializable;
import java.util.Collection;

import org.jgraph.graph.GraphCell;

/**
 * Interface for <tt>GraphCell</tt>s whose user objects are based
 * on a set of strings, displayed in multiline format but edited in
 * single-line format.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.1 $
 */
public interface JCell extends GraphCell, Serializable {
    /**
     * Returns un unmodifiable view upon the set of labels (as strings) in the user object.
     * @return the underlying set of strings
     */
    public abstract Collection<String> getLabelSet();
    
    /**
     * Returns a {@link JUserObject}, which is a set of objects associated with this
     * cell.
     * @ensure <tt>result instanceof JUserObject</tt>
     */
    public abstract JUserObject<?> getUserObject();
    
    /**
     * If <tt>value</tt> is <tt>null</tt>, this implementation empties the
     * user object. Otherwise, if <tt>value</tt> is a {@link Collection}, this 
     * implementation loads the user object of this cell from the label set of 
     * <tt>value</tt>; otherwise it tries to load the user cell of this vertex 
     * from the string description of <tt>value</tt> using {@link JUserObject#load(String)}.
     * @see JUserObject#load(Collection)
     * @see JUserObject#load(String)
     */
    public abstract void setUserObject(Object value);
    
    /**
     * Returns tool tip text for this j-cell.
     */
    public abstract String getToolTipText();
}