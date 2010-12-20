/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: ModelCheckingStrategy.java,v 1.2 2008-03-04 14:42:42 kastenberg Exp $
 */
package groove.explore.strategy;

import groove.explore.result.Result;
import groove.lts.ProductGTS;
import groove.verify.BuchiGraphState;
import groove.verify.BuchiLocation;

import java.util.List;

/**
 * Interface for explore strategies that on-the-fly perform model checking.
 * 
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public interface ModelCheckingStrategy extends Strategy {
    /** 
     * Value indicating that the ltl2ba library is used for generating 
     * Büchi automata.
     */
    public static final int LTL2BA = 1;
    /** 
     * Value indicating that the ltlbuchi library is used for generating 
     * Büchi automata.
     */
    public static final int NASABUCHI = 2;
    /** Method using for generating Büchi automata.
     * Possible values are {@link #LTL2BA} and {@link #NASABUCHI}.
     */
    public static final int LTL2BUCHI_METHOD = NASABUCHI;

    /**
     * Returns the product gts.
     */
    public ProductGTS getProductGTS();

    /**
     * Sets the result container for the strategy
     * @param result the result container
     */
    public void setResult(Result result);

    /**
     * Returns the result container.
     * @return the result container
     */
    public Result getResult();

    /**
     * Returns the Büchi graph-state the strategy is currently at.
     */
    public BuchiGraphState getAtBuchiState();

    /**
     * Returns the current Büchi location.
     */
    public BuchiLocation getAtBuchiLocation();

    /**
     * Sets the property to be verified.
     * @param property the property to be verified. It is required
     * that this property can be parsed correctly
     */
    public void setProperty(String property);

    /**
     * Returns the current search-stack.
     */
    public List<BuchiGraphState> searchStack();
}
