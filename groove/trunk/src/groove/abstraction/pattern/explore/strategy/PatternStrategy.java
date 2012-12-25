/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.abstraction.pattern.explore.strategy;

import groove.abstraction.pattern.lts.PGTS;
import groove.explore.strategy.Strategy;

/**
 * Common interface of exploration strategies for pattern abstraction.
 * 
 * See {@link Strategy}.
 */
public interface PatternStrategy {
    /** Sets the PGTS to be explored. */
    public void prepare(PGTS pgts);

    /**
     * Executes one step of the strategy.
     * @return false if the strategy is completed, <code>true</code>
     *         otherwise.
     * @require The previous call of this method, if any, returned
     *          <code>true</code>. Otherwise, the behaviour is not
     *          guaranteed.
     */
    public boolean next();
}
