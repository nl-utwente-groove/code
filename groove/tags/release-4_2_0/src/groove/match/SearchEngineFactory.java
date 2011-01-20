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
package groove.match;

import groove.match.rete.ReteSearchEngine;
import groove.trans.RuleToHostMap;
import groove.trans.SystemProperties;

/**
 * This is a factory class generating search engines. This is where
 * decision as to which search engine is to be used in groove should be made.
 *
 * @author Arash Jalali
 * @version $Revision $
 */

public class SearchEngineFactory {

    /**
     * Enumerates the types of engine available 
     */
    public enum EngineType {
        /** 
         * the engine type that uses the search plan algorithm
         */
        SEARCH_PLAN,
        /**
         * the engine type that uses the RETE algorithm
         */
        RETE
    }

    private static SearchEngineFactory instance = null;
    private EngineType currentEngineType;

    private SearchEngineFactory() {
        this.currentEngineType = getDefaultEngineType();
    }

    /**
     * @return The default engine used by GROOVE
     */
    public EngineType getDefaultEngineType() {
        return EngineType.SEARCH_PLAN;
    }

    /**
     * Factory method that gives the singleton instance of this 
     * class
     * @return a reference to the singleton instance of this class
     */
    public synchronized static SearchEngineFactory getInstance() {
        if (instance == null) {
            instance = new SearchEngineFactory();
        }
        return instance;
    }

    /**
     * The factory method returning the currently used search engine
     * in GROOVE. Currently supporting the Search Plan engine and
     * the RETE engine.
     * @param properties system properties determining some of the choices in
     * the matching, such as the algebra register and injectivity
     * @return the currently active engine that matches based on 
     *         the requirements specified in the parameters.
     */
    public SearchEngine<? extends AbstractMatchStrategy<RuleToHostMap>> getEngine(
            SystemProperties properties) {
        SearchEngine<? extends AbstractMatchStrategy<RuleToHostMap>> result =
            null;
        switch (this.getCurrentEngineType()) {
        case SEARCH_PLAN:
            result = SearchPlanEngine.getInstance(properties);
            break;
        case RETE:
            result =
                ReteSearchEngine.getInstance(properties.isInjective(), false);
        }
        return result;

    }

    /**
     * @return the currently used engine type
     */
    public EngineType getCurrentEngineType() {
        return this.currentEngineType;
    }

    /**
     * Changes the currently used search engine instance to the type specified in the
     * parameter.
     * @param engineType the type of engine to be returned by the factory 
     */
    public void setCurrentEngineType(EngineType engineType) {
        this.currentEngineType = engineType;
    }

}
