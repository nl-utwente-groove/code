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
 * $Id: ModelCheckingStrategy.java,v 1.1 2008-02-20 10:07:56 kastenberg Exp $
 */

package groove.explore.strategy;

import groove.explore.result.Result;
import groove.gui.Simulator;
import groove.lts.ProductGTS;
import groove.verify.BuchiGraphState;

import java.util.List;

/**
 * Interface for explore strategies that on-the-fly
 * perform model checking.
 * 
 * @author Harmen Kastenberg
 *
 */
public interface ModelCheckingStrategy<T> extends Strategy {

	public void setProductGTS(ProductGTS gts);

	public ProductGTS getProductGTS();

	public void setResult(Result<T> result);

	public Result<T> getResult();

    public void setup() throws IllegalArgumentException;

    public BuchiGraphState getAtBuchiState();

    public void setAtBuchiState(BuchiGraphState atState);

    public void setProperty(String property);

    public void setSimulator(Simulator simulator);

    public List<BuchiGraphState> searchStack();
}
