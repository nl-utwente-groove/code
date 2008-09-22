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
 * $Id: ModelCheckingStrategy.java,v 1.2 2008-03-04 14:42:42 kastenberg Exp $
 */
package groove.explore.strategy;

import groove.explore.result.Result;
import groove.gui.Simulator;
import groove.lts.GraphState;
import groove.lts.ProductGTS;
import groove.verify.BuchiGraphState;
import groove.verify.BuchiLocation;

import java.util.List;

/**
 * Interface for explore strategies that on-the-fly
 * perform model checking.
 * 
 * @author Harmen Kastenberg
 * @version $Revision: 1.2 $
 */
public interface ModelCheckingStrategy extends Strategy {

	/**
	 * Sets the product gts for the strategy.
	 * @param gts the product gts
	 */
	public void setProductGTS(ProductGTS gts);

	/**
	 * Returns the product gts.
	 * @return the product gts
	 */
	public ProductGTS getProductGTS();

	/**
	 * Sets the result container for the strategy
	 * @param result the result container
	 */
	public void setResult(Result<GraphState> result);

	/**
	 * Returns the result container.
	 * @return the result container
	 */
	public Result<GraphState> getResult();

	/**
	 * Initializes the strategy.
	 * @throws IllegalArgumentException
	 */
	public void setup() throws IllegalArgumentException;

    /**
     * Returns the Buechi graph-state the strategy is currently at.
     * @return the Buechi graph-state the strategy is currently at
     */
    public BuchiGraphState getAtBuchiState();

    /**
     * Sets the current Buechi graph-state of the strategy.
     * @param atState the new current Buchi graph-state
     */
    public void setAtBuchiState(BuchiGraphState atState);

    /**
     * Returns the current Buechi location. 
     * @return the current Buechi location
     */
    public BuchiLocation getAtBuchiLocation();

    /**
     * Sets the property to be verified.
     * @param property the property to be verified
     */
    public void setProperty(String property);

    /**
     * Returns the property to be verified.
     * @return the property to be verified
     */
    public String getProperty();

    /**
     * Sets the triggering simulator-instance.
     * @param simulator the triggering simulator-instance
     */
    public void setSimulator(Simulator simulator);

    /**
     * Returns the current search-stack.
     * @return the current search-stack
     */
    public List<BuchiGraphState> searchStack();
}
