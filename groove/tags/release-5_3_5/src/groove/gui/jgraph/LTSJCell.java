/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.gui.jgraph;

import groove.lts.GTS;

import java.io.Serializable;

/**
 * Supertype of {@link LTSJVertex} and {@link LTSJEdge}.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface LTSJCell extends JCell<GTS>, Serializable {
    /**
     * Sets this cell to active. This will result in
     * special display attributes.
     */
    boolean setActive(boolean active);

    /**
     * Sets the explicit visibility of the cell to the given value.
     * The return value indicates if the visibility flag has changed thereby
     */
    public boolean setVisibleFlag(boolean visible);

    /** Retrieves the explicit visibility of the cell. */
    public boolean hasVisibleFlag();
}
