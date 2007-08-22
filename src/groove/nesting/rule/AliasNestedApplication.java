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
 * $Id: AliasNestedApplication.java,v 1.1 2007-08-22 09:19:47 kastenberg Exp $
 */
package groove.nesting.rule;

import groove.graph.Graph;
import groove.graph.Node;
import groove.lts.GraphNextState;
import groove.lts.GraphState;
import groove.lts.GraphTransitionStub;
import groove.trans.RuleEvent;
import groove.trans.SPOEvent;

/**
 *
 * @author kramor
 * @version 0.1 $Revision: 1.1 $ $Date: 2007-08-22 09:19:47 $
 */
public class AliasNestedApplication extends NestedApplication {
	/** Counter for the number of true application aliases created. */
    private static int aliasCount;
    
    /** Returns the total number of true application aliases created. */
    public static int getAliasCount() {
        return aliasCount;
    }

    /** 
     * Constructs an SPO application with a given prior target state. 
     * @param event the rule event of this application
     * @param source the source graph of this application
     * source state's predecessor, with the same event
     */
    public AliasNestedApplication(RuleEvent event, GraphNextState source, GraphTransitionStub prior) {
        super((NestedEvent)event, source.getGraph());
        assert event == prior.getEvent(source.source());
        this.source = source;
        this.prior = prior;
        aliasCount++;
    }

	public GraphTransitionStub getPrior() {
    	return prior;
    }
    
    @Deprecated
    public boolean hasPrior() {
        return prior != null;
    }
    
    /** Convenience method to retrieve the parent state of this application's source. */
    private GraphState getParent() {
    	return source.source();
    }
    
    @Override
    protected Node[] computeCoanchorImage() {
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
                result = super.computeCoanchorImage();
    		}
    	}
	    return result;
    }
    
    private final GraphNextState source;
    /** The prior transition for this aliased application, if any. */
    private final GraphTransitionStub prior;
}
