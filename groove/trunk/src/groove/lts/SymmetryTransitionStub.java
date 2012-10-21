// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: SymmetryTransitionStub.java,v 1.3 2008-01-30 09:32:20 iovka Exp $
 */
package groove.lts;

import groove.trans.HostNode;
import groove.trans.RuleEvent;

/**
 * Graph transition stub based on a true event renaming.
 * @author Arend Rensink
 * @version $Revision$
 */
class SymmetryTransitionStub extends AbstractRuleTransitionStub {
    /**
     * Default constructor, providing the event, added nodes and target of the
     * stub.
     */
    SymmetryTransitionStub(RuleEvent event, HostNode[] addedNodes, GraphState target) {
        super(event, addedNodes, target);
    }

    /** This type of transition stub involves a non-trivial symmetry. */
    public boolean isSymmetry() {
        return true;
    }
}
