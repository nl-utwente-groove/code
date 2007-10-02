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
 * $Id: RuleMatch.java,v 1.1 2007-10-02 16:14:57 rensink Exp $
 */
package groove.trans;

import groove.graph.NodeFactory;

/**
 * Match interface for rules.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface RuleMatch extends Match {
    /** Returns the rule that has been matched. */
    Rule getRule();
     
    /** 
     * Turns this match into a rule event.
     * @param nodeFatory factory for created nodes; may be <code>null</code>
     * @param reuse if <code>true</code>, the event will optimise by storing data structures
     * internally. This is only useful if events are shared among rule applications.
     * @return a new rule event based on this rule match
     */
    RuleEvent newEvent(NodeFactory nodeFatory, boolean reuse);
}
