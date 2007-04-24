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
 * $Id: IdentityTransitionStub.java,v 1.1 2007-04-24 10:06:43 rensink Exp $
 */
package groove.lts;

import groove.trans.RuleEvent;

/**
 * Graph transition stub based on an identity morphism.
 * @author Arend Rensink
 * @version $Revision: 1.1 $
 */
class IdentityTransitionStub extends AbstractGraphTransitionStub {
    /**
     * Default constructor, providing the event and target of the stub.
     */
    IdentityTransitionStub(RuleEvent event, GraphState target) {
    	super(event,target);
    }

    /** This type of transition stub involves no non-trivial symmetry. */
	public boolean isSymmetry() {
		return false;
	}	
}
