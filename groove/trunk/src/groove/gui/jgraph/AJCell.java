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
 * $Id: EditableJCell.java,v 1.3 2008-01-30 09:33:13 iovka Exp $
 */
package groove.gui.jgraph;

import groove.graph.GraphRole;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectNode;

/**
 * Instantiation of a {@link JCell} with an {@link StringObject}.
 * @author Arend Rensink
 * @version $Revision $
 */
interface AJCell extends GraphJCell<AspectNode,AspectEdge> {
    /** Returns the user object of this cell, with the given type. */
    AJObject getUserObject();

    /** Sets the user object to a given value. */
    void setUserObject(Object value);

    /** 
     * Sets the user object with information from the cell's wrapped 
     * nodes and edges.
     */
    void saveToUserObject();

    /**
     * Resets the cell's nodes and edges from the user object.
     * @param role TODO
     */
    void loadFromUserObject(GraphRole role);
}
