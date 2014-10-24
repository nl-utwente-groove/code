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
 * $Id$
 */
package groove.automaton;

import groove.graph.ANode;

/**
 * Nodes used in regular automata.
 * @author Arend Rensink
 * @version $Revision $
 */
public class RegNode extends ANode {
    /**
     * Creates a node with a given number.
     */
    public RegNode(int nr) {
        super(nr);
    }

    @Override
    protected String getToStringPrefix() {
        return "r";
    }
}
