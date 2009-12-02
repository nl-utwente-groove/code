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
package groove.io;

import groove.view.aspect.AspectGraph;

/**
 * Instance of the generic system store where both the graph and the rule
 * representations are {@link AspectGraph}s.
 * @author Arend
 * @version $Revision $
 */
public interface SystemStore extends
        GenericSystemStore<AspectGraph,AspectGraph> {
    /** Value used in notifying changes to the properties. */
    static public final int PROPERTIES_CHANGE = 0x1;
    /** Value used in notifying changes to the control map. */
    static public final int CONTROL_CHANGE = 0x2;
    /** Value used in notifying changes to the rule map. */
    static public final int RULE_CHANGE = 0x4;
    /** Value used in notifying changes to the graph map. */
    static public final int GRAPH_CHANGE = 0x8;
}
