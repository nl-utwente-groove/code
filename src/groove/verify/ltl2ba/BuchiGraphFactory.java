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
package groove.verify.ltl2ba;

import groove.view.FormatException;

/**
 * @author Harmen Kastenberg
 * @version $Revision $
 */
public abstract class BuchiGraphFactory {
    /**
     * Default prototype class for creating Buchi graphs.
     */
    static private final BuchiGraph PROTOTYPE = NASABuchiGraph.getPrototype();

    /**
     * Creates a Buchi graph factory for creating Buchi graph of the default prototype class.
     * 
     * @return the corresponding Buchi graph factory
     */
    public static BuchiGraphFactory getInstance() {
        return getInstance(PROTOTYPE);
    }

    /**
     * Creates a Buchi graph factory for creating Buchi graphs of the provided prototype class.
     * 
     * @param prototype
     *          the prototype class for creating Buchi graphs
     * @return the correponding Buchi graph factory
     */
    public static BuchiGraphFactory getInstance(final BuchiGraph prototype) {
        return new BuchiGraphFactory() {
            @Override
            public BuchiGraph newBuchiGraph(String formula)
                throws FormatException {
                return prototype.newBuchiGraph(formula);
            }
        };
    }

    /**
     * Create a new Buchi graph using a Buchi graph factory.
     * @throws FormatException if the formula contains (parsing) errors
     */
    public abstract BuchiGraph newBuchiGraph(String formula)
        throws FormatException;
}
