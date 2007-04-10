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
 * $Id: JCell.java,v 1.2 2007-03-27 14:18:29 rensink Exp $
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
 * @version $Revision: 1.2 $
 */
public interface JCell extends GraphCell, Serializable {
    /**
     * Returns un unmodifiable view upon the set of labels (as strings) in the user object.
     * @return the underlying set of strings
     */
    public abstract Collection<String> getLabelSet();
    
    /** 
     * Callback method to convert an element of the user object
     * into a String.
     */
    public abstract String getLabel(Object object);
    
    /**
     * Returns a {@link JUserObject}, which is a set of objects associated with this
     * cell.
     * @ensure <tt>result instanceof JUserObject</tt>
     */
    public abstract JUserObject<?> getUserObject();
    
    /**
     * Returns tool tip text for this j-cell.
     */
    public abstract String getToolTipText();
}