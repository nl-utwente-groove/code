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
 * $Id: EditableJEdge.java,v 1.7 2008-01-30 09:33:11 iovka Exp $
 */
package groove.gui.jgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * J-Graph edge for the editor.
 * This has a {@link EditableContent} as user object,
 * which can be loaded from a string or set of strings.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EditableJEdge extends JEdge implements EditableJCell {
	/** Constructs a new, empty empty j-edge. */
	public EditableJEdge() {
		// empty constructor.
	}
	
    /** Constructs a j-edge by cloning another one. */
    public EditableJEdge(JEdge other) {
        getAttributes().applyMap(other.getAttributes());
        setUserObject(other.getUserObject().getLabelSet());
    }
    
    /** This implementation just returns the user object. */
    public List<StringBuilder> getLines() {
    	List<StringBuilder> result = new ArrayList<StringBuilder>();
    	for (String label: getUserObject()) {
    		result.add(new StringBuilder(label));
    	}
		return result;
	}

    /** This implementation just returns the user object. */
	public Collection<String> getListLabels() {
		return getUserObject();
	}

    /** This implementation just returns the user object. */
	public Collection<String> getPlainLabels() {
		return getUserObject();
	}

	/** 
     * If the value is a collection or a string, loads the
     * user object from it.
     */
    @Override
    public void setUserObject(Object value) {
    	EditableContent newObject = createUserObject();
    	super.setUserObject(newObject);
        if (value instanceof Collection) {
        	newObject.load((Collection) value);
        } else if (value != null) {
        	newObject.load(value.toString());
        }
    }
    
    /** Specialises the return type. */
    @Override
	public EditableContent getUserObject() {
		return (EditableContent) super.getUserObject();
	}

	/**
     * Callback factory method to create a user object.
     * Called lazily in {@link #getUserObject()}.
     */
    @Override
    protected EditableContent createUserObject() {
    	return new EditableContent(false);
    }
}