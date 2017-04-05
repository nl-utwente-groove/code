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
package groove.lts;

import groove.control.template.Switch;
import groove.grammar.Action;
import groove.grammar.host.HostNode;
import groove.graph.Label;

/**
 * Class of labels occurring on graph transitions.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface ActionLabel extends Label {
    /** Returns the action for which this is a label. */
    public Action getAction();

    /** Returns the control switch underlying this action. */
    public Switch getSwitch();

    /**
     * Returns the arguments for the graph transition.
     * This is an array compatible with the signature of the action,
     * with {@code null} elements for output nodes that are currently unknown.
     */
    public HostNode[] getArguments();
}
