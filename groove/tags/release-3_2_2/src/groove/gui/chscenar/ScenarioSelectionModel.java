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
package groove.gui.chscenar;

import groove.explore.DefaultScenario;
import groove.explore.Scenario;
import groove.explore.chscenar.ScenarioChecker;
import groove.explore.result.Acceptor;
import groove.explore.result.ExploreCondition;
import groove.explore.result.Result;
import groove.explore.strategy.Strategy;

/** Model for a scenario that is currently being constructed. */
public class ScenarioSelectionModel {

	/** The scenario checker, used for checking correctness of 
	 * a configuration.
	 */
	private final ScenarioChecker checker = new ScenarioChecker();
	
	private ScenarioSelectionStatus status = new ScenarioSelectionStatus();
	
	/** The currently chosen strategy. */
	private StrategyChoice str;
	/** The currently chosen acceptor. */
	private AcceptorChoice acc;
	/** The currently chosen result. */
	private ResultChoice res;
	
    /** An explore condition which could be set for an acceptor.
     * In case acceptors can get other option types than explore conditions,
     * this should be made more general.
     */
    private ExploreCondition<?> acceptorExplCond = null;
    /** An explore condition which could be set for a strategy.
     * In case acceptors can get other option types than explore conditions,
     * this should be made more general.
     */
    private ExploreCondition<?> strategyExplCond = null;
	
	/** Sets a choice for a strategy. 
	 * Updates the status.
	 * @param s
	 */
	public void setStrategy (StrategyChoice s) {
		str = s;
		strategyExplCond = null;
	}
	
	/** Sets a choice for a result.
	 * Updates the status.
	 * @param r
	 */
	public void setResult (ResultChoice r) {
		res = r;
	}
	
	/** Sets a choice for the acceptor.
	 * Updates the status.
	 * @param a
	 */
	public void setAcceptor (AcceptorChoice a) {
		acc = a;
		acceptorExplCond = null;
	}
	
	/** Sets the explore condition for the acceptor.
	 * @param cond
	 */
	public void setAcceptorExplCond (ExploreCondition<?> cond) {
		acceptorExplCond = cond;
	}
	
	/** Sets the explore condition for the strategy.
	 * @param cond
	 */
	public void setStrategyExplCond (ExploreCondition<?> cond) {
		strategyExplCond = cond;
	}
	
	/** Indicates whether the current strategy requires
	 * to set some properties.
	 * @return
	 */
	public boolean requireStrategyProperties () {
		return str.optionsClass() != null;
	}
	
	/** Indicates whether the current acceptor requires
	 * to set some properties.
	 * @return 
	 */
	public boolean requireAcceptorProperties () {
		return acc.optionsClass() != null;
	}
	
	/** Indicates whether the current result requires
	 * to set some properties.
	 * @return
	 */
	public boolean requireResultProperties () {
		return false;
	}
	
	/** Computes and returns the current status.
	 * 
	 * @return The current status.
	 */
	public ScenarioSelectionStatus getStatus () {
		updateStatus();
		return status;
	}
	
	/** Updates the current status.
	 * 
	 */
	private void updateStatus () {
        status.setText("");
		// First check whether this is an error
        if (!checker.isAllowed(
                str.implementingClass(),
                res.implementingClass(),
                acc.implementingClass())) {

            status.setText("The current combination is not allowed");
            status.setError(ErrorStatus.ERROR);
            return;
        } 
        
        // If the acceptor or the strategy changed and require properties,
        // then update the status message
        if (requireAcceptorProperties() && acceptorExplCond == null) {
        	status.setText("Please set parameters of the Look for criterion.");
        	status.setError(ErrorStatus.WARNING);
        	return;
        } 
        if (requireStrategyProperties() && strategyExplCond == null) {
        	status.setText("Please set parameters of the conditional strategy.");
        	status.setError(ErrorStatus.WARNING);
        	return;
        }
        status.setError(ErrorStatus.OK);
	}
	
    /** Returns the strategy that has been selected, or null if
     * no correct strategy was selected.
     * @return The selected strategy, or null if no correct strategy was selected.
     */
    private Strategy getStrategy () {
    	if (requireStrategyProperties() && strategyExplCond == null) {
    		return null;
        }
        try {
            return (Strategy) str.getInstance(strategyExplCond);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }
        
    /** Returns the result that has been selected, or null if
     * no correct result was selected.
     * @return The selected result, or null if no correct result was selected.
     */
    private Result getResult () {
    	return (Result) res.getInstance(null);
    }
    
    /** Returns the acceptor that has been selected, or null if
     * no correct acceptor was selected.
     * @return The selected acceptor, or null if no correct result was selected.
     */
    private Acceptor getAcceptor () {
    	if (requireAcceptorProperties() && acceptorExplCond == null) {
    		return null;
    	}
    	try {
             return (Acceptor) acc.getInstance(acceptorExplCond);
         } catch (IllegalArgumentException e) {
             throw new UnsupportedOperationException(e.getMessage());
         }
    }
    
    /** The selected scenario, or null if no valid scenario was selected.
     * Will return a non-null value only if getStatus().isOk()
     * @return The selected scenario, or null if no valid scenario was selected. The GTS
     * and start state of the scenario have not been set.
     */
    public Scenario getScenario () {
    	Strategy strategy = getStrategy();
    	Acceptor acceptor = getAcceptor();
    	Result result = getResult();
    	if (strategy == null || acceptor == null || result == null) {
    		return null;
    	}
        if (! checker.isAllowed(strategy, result, acceptor)) {
            return null;
        }
    	DefaultScenario scenario = new DefaultScenario(strategy, acceptor);
    	
        checker.isAllowed(strategy, result, acceptor);
        
    	return scenario;
    }
	
	
	private enum ErrorStatus {
		ERROR,
		WARNING,
		OK;
	}
    
    
	
	/** Gives information on the current status. 
	 * The status is represented by a one-sentence status information,
	 * which may be an error message.
	 */
	public class ScenarioSelectionStatus {
		
		private String text;
		private ErrorStatus error;
		
		/** Creates a status with empty non error message. */
		ScenarioSelectionStatus () {
			text = "";
			error = ErrorStatus.OK;
		}
		
		/** Text indicating the status. */
		public String getStatusText () {
			return text;
		}
		
		/** Returns <true> only if the current
		 * selection of a strategy / result / acceptor is
		 * acceptable.
		 * This does not mean that the status is ok.
		 * @see #isOK()
		 */
		public boolean isAcceptableConfiguration () {
			return error == ErrorStatus.ERROR;
		}
		
		/** Returns <true> only if the current
		 * selection allows to construct a scenario. 
		 * A scenario can be constructed when the
		 * the configuration is acceptable and 
		 * the additional properties has been set.
		 * @see #isAcceptableConfiguration()
		 */
		public boolean isOk() {
			return error == ErrorStatus.OK;
		}

		void setText(String text) {
			this.text = text;
		}

		void setError(ErrorStatus error) {
			this.error = error;
		}
	}
}
