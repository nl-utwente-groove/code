/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.gui;

import javax.swing.Icon;

/** Superclass for tree nodes in a display-related list. */
public abstract class DisplayTreeNode extends SortingTreeNode {
    /** Constructs a node. */
    protected DisplayTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    /** Returns the icon to be used when rendering this tree node. */
    public Icon getIcon() {
        return null;
    }

    /** Indicates if this tree node contains an error. */
    public boolean isError() {
        return false;
    }

    /** Indicates if this tree node represent a transient state or partial transition. */
    public boolean isTransient() {
        return false;
    }

    /** Indicates if this tree node is enabled. */
    public boolean isEnabled() {
        return true;
    }

    /** Returns the text to be displayed on the tree node. */
    public String getText() {
        return toString();
    }

    /** Returns the tooltip to be used when rendering this tree node. */
    public String getTip() {
        return null;
    }
}
