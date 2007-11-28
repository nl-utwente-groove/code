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
 * $Id$
 */
package groove.trans;


import java.util.Iterator;
import java.util.Set;

/**
 * Class that wraps the algorithm to explore rule applications
 * for a given graph.
 * @author Arend Rensink
 * @version $Revision$ $Date$
 */
public interface RuleApplier {
    /**
     * Returns a set of {@link RuleApplication}s for a given graph,
     * according to the derivation strategy of this deriver.
     * @return a set of rule applications.
     */
	public Set<RuleApplication> getApplications();
	
    /**
     * Returns an iterator over the {@link RuleApplication}s for a given graph,
     * according to the derivation strategy of this deriver.
     * The iterator may operate lazily; it is guaranteed to return every application
     * at most once, so that it behaves functionally the same as <code>getApplications().iterator()</code>
     * @return an iterator over the rule applications for <code>graph</code>
     * @see #getApplications()
     */
	public Iterator<RuleApplication> getApplicationIter();
	
	/**
	 * Calls the action's <code>perform</code> method for all rule applications.
	 * These are the same rule applications returned by {@link #getApplications()}.
	 * @param action the action to be performed
	 * @return <code>true</code> if at least one application was performed
	 */
	public boolean doApplications(Action action);
	
	/** Interface for a method that is called during {@link RuleApplier#doApplications(Action)}. */
	interface Action {
		/** Callback method that is invoked for all rule applications found 
		 * in an invocation of {@link RuleApplier#doApplications(Action)}. 
		 */
		void perform(RuleApplication application);
	}
}
