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
package groove.match.rete;

import groove.trans.RuleElement;
import groove.trans.RuleNode;

/**
 * Represents the abstract representation of a node checker
 * @author Arash Jalali
 * @version $Revision $
 */
public abstract class NodeChecker extends ReteNetworkNode implements
        StateSubscriber {

    /**
     * Single node pattern represented by this node checker
     */
    protected RuleElement[] pattern = new RuleElement[1];

    /**
     * This constructor should not be called directly
     */
    protected NodeChecker(ReteNetwork network) {
        super(network);
    }

    /**
     * 
     * @return the node object associated with this checker
     */
    public RuleNode getNode() {
        return (RuleNode) this.pattern[0];
    }

    @Override
    public RuleElement[] getPattern() {
        return this.pattern;
    }

}
