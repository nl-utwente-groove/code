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
 * $Id: EditableJVertex.java,v 1.1.1.2 2007-03-20 10:42:45 kastenberg Exp $
 */
package groove.gui.jgraph;

import java.util.Collection;

/**
 * J-Graph vertex for the editor.
 * This has a {@link EditableJUserObject} as user object,
 * which can be loaded from a string or set of strings.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EditableJVertex extends JVertex implements EditableJCell {
	/** Constructs a new, empty empty j-vertex. */
	public EditableJVertex() {
		// empty constructor.
	}
	
    /** Constructs a jvertex by cloning another one. */
    public EditableJVertex(JVertex other) {
        getAttributes().applyMap(other.getAttributes());
        setUserObject(other.getLabelSet());
    }

    /** 
     * If the value is a collection or a string, loads the
     * user object from it.
     */
    public void setUserObject(Object value) {
        if (value == null) {
            getUserObject().clear();
        } else if (value instanceof Collection) {
            if (value != getUserObject()) {
                getUserObject().load((Collection) value);
            }
        } else {
            getUserObject().load(value.toString());
        }
    }

    /** Specialises the return type. */
    @Override
	public EditableJUserObject getUserObject() {
		return (EditableJUserObject) super.getUserObject();
	}

	/**
     * Callback factory method to create a user object.
     * Called lazily in {@link #getUserObject()}.
     */
    protected EditableJUserObject createUserObject() {
    	return new EditableJUserObject(JUserObject.NEWLINE, JUserObject.NEWLINE);
    }
}
