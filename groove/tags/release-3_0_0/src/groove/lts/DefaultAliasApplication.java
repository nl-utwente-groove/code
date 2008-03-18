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

import groove.graph.Node;
import groove.trans.DefaultApplication;
import groove.trans.RuleEvent;

/**
 * Extension of the default graph application that includes information about a 
 * prior transition.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultAliasApplication extends DefaultApplication implements AliasRuleApplication {
    /** 
     * Constructs an SPO application with a given prior target state. 
     * @param event the rule event of this application
     * @param source the source graph of this application
     * source state's predecessor, with the same event
     */
    public DefaultAliasApplication(RuleEvent event, GraphNextState source, GraphTransitionStub prior) {
        super(event, source.getGraph());
        assert event == prior.getEvent(source.source());
        this.source = source;
        this.prior = prior;
        aliasCount++;
    }

	public GraphTransitionStub getPrior() {
    	return prior;
    }
    
    @Override
    protected Node[] computeCreatedNodes() {
    	Node[] result = prior.getAddedNodes(getParent());
    	// if this application's event is the same as that of prior,
    	// the added nodes may coincide
    	if (result.length > 0 && source.getEvent() == prior.getEvent(getParent())) {
    		Node[] sourceAddedNodes = source.getAddedNodes();
    		boolean conflict = false;
    		for (int i = 0; !conflict && i < result.length; i++) {
    			conflict = result[i] == sourceAddedNodes[i];
    			assert conflict || !getSource().containsElement(result[i]);
    		}
    		if (conflict) {
                result = super.computeCreatedNodes();
    		}
    	}
	    return result;
    }

    /** Convenience method to retrieve the parent state of this application's source. */
    private GraphState getParent() {
        return source.source();
    }
    
    private final GraphNextState source;
    /** The prior transition for this aliased application, if any. */
    private final GraphTransitionStub prior;
    
    /** Returns the total number of true application aliases created. */
    public static int getAliasCount() {
        return aliasCount;
    }

    /** Counter for the number of true application aliases created. */
    private static int aliasCount;
}
