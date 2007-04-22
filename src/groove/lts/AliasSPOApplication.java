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
package groove.lts;

import groove.graph.Element;
import groove.graph.Graph;
import groove.graph.Node;
import groove.trans.RuleEvent;
import groove.trans.SPOApplication;
import groove.trans.SPOEvent;

/**
 * This implementation ensures that the target states
 * are {@link groove.lts.DerivedGraphState}s.
 */
public class AliasSPOApplication extends SPOApplication implements AliasRuleApplication {
	/** Counter for the number of true application aliases created. */
    private static int aliasCount;
    
    /** Returns the total number of true application aliases created. */
    public static int getAliasCount() {
        return aliasCount;
    }

    /** 
     * Constructs an SPO application with a given prior target state. 
     * @param event the rule event of this application
     * @param host the source graph of this application
     * source state's predecessor, with the same event
     */
    public AliasSPOApplication(RuleEvent event, Graph host, GraphTransition prior) {
        super((SPOEvent) event, host);
        this.prior = prior;
        aliasCount++;
    }

	public GraphTransition getPrior() {
    	return prior;
    }
    
    @Deprecated
    public boolean hasPrior() {
        return prior != null;
    }
    
    @Override
    protected Element[] computeCoanchorImage() {
    	Element[] result;
        if (prior.isIdMorphism() && ((DerivedGraphState) prior.target()).getEvent() == getEvent()) {
        	result = ((DerivedGraphState) prior.target()).getCoanchorImage();
        } else {
            result = super.computeCoanchorImage();
        }
	    assert getRule().coanchor().length == 0 || result[0] instanceof Node;
	    return result;
    }
    
    /** The prior transition for this aliased application, if any. */
    private final GraphTransition prior;
}
