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
 * $Id$
 */
package groove.gui.jgraph;

import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.AspectKind;
import groove.graph.GraphRole;

import java.util.Iterator;

/**
 * Instantiation of a {@link JCell} with an {@link AspectJObject}
 * that stores the (editable) string representation of the node/edge label.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface AspectJCell extends JCell<AspectGraph> {
    @Override
    public Iterator<? extends AspectJCell> getContext();

    /** Returns the aspect kind of the element wrapped in this cell. */
    AspectKind getAspect();

    /** Returns the user object of this cell, with the given type. */
    AspectJObject getUserObject();

    /** Sets the user object to a given value. */
    void setUserObject(Object value);

    /**
     * Sets the user object with information from the cell's wrapped
     * nodes and edges.
     */
    void saveToUserObject();

    /**
     * Resets the cell's nodes and edges from the user object.
     */
    void loadFromUserObject(GraphRole role);

    /** Separator between level name and edge label. */
    static final char LEVEL_NAME_SEPARATOR = '@';
}
