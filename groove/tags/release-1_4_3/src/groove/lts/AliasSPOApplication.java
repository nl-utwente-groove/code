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
import groove.trans.RuleFactory;
import groove.trans.SPOApplication;
import groove.trans.SPOEvent;

/**
 * This implementation ensures that the target states
 * are {@link groove.lts.DerivedGraphState}s.
 */
public class AliasSPOApplication extends SPOApplication implements AliasRuleApplication {
	/** Counter for the number of true application aliases created. */
    private static int priorTransitionCount;
    
    /** Returns the total number of true application aliases created. */
    public static int getPriorTransitionCount() {
        return priorTransitionCount;
    }
    
    /** Constructs an alias application, without yet setting a prior transition. */
    public AliasSPOApplication(SPOEvent event, Graph source) {
        super(event, source);
    }

	/**
	 * Returns the rule factory of this applier.
	 */
	protected RuleFactory getRuleFactory() {
		return getEvent().getRuleFactory();
	}

    public GraphOutTransition getPrior() {
    	return prior;
    }

    public void setPrior(GraphOutTransition prior) {
    	assert prior.getEvent() == getEvent() : "Rule of prime is "+prior.getRule().getName()+" whereas mine is "+getRule().getName();
		this.prior = prior;
        priorTransitionCount++;
	}
    
    public boolean hasPrior() {
        return prior != null;
    }
    
    @Override
    protected Element[] computeCoanchorImage() {
        if (prior instanceof DerivedGraphState) {
        	return ((DerivedGraphState) prior).getCoanchorImage();
        } else {
            return super.computeCoanchorImage();
        }
    }

    @Override
    protected Graph computeTarget() {
		Graph target = createTarget();
		target.setFixed();
		return target;
	}
    
    /**
     * This implementation defers to the rule factory.
     * @see RuleFactory#createTarget(groove.trans.RuleApplication)
     */
    @Override
    protected Graph createTarget() {
    	return getRuleFactory().createTarget(this);
    }
    
    /** The prior transition for this aliased application, if any. */
    private GraphOutTransition prior;
}