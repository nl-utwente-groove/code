/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.test.prolog;

import java.io.File;
import java.io.IOException;

import gnu.prolog.vm.PrologException;
import nl.utwente.groove.grammar.QualName;
import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.prolog.GrooveEnvironment;
import nl.utwente.groove.prolog.GrooveState;
import nl.utwente.groove.prolog.PrologEngine;
import nl.utwente.groove.prolog.QueryResult;
import nl.utwente.groove.prolog.QueryReturnValue;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Contains some helper methods for building queries
 * @author Lesley Wevers
 */
public class PrologTestUtil {
    /** Location of the samples. */
    static public final String GRAMMAR_DIR = "junit/samples";

    private static PrologEngine prologQuery;

    private PrologTestUtil() {
        /**
         * Blank by design
         */
    }

    /**
     * Checks if a query succeeds
     * @param grooveState       A groove state
     * @param query             A query
     * @return                  Did the query succeed?
     */
    public static boolean test(GrooveState grooveState, String query) throws FormatException,
                                                                      PrologException {
        if (prologQuery == null) {
            prologQuery = new PrologEngine(new GrooveEnvironment(null, null));
        }

        prologQuery.setGrooveState(grooveState);

        QueryResult queryResult = prologQuery.newQuery(query);

        return queryResult.getReturnValue() == QueryReturnValue.SUCCESS
            || queryResult.getReturnValue() == QueryReturnValue.SUCCESS_LAST;
    }

    /**
     * Loads a graph grammar and returns a grammar view
     * @param grammarName       The name of the grammar
     * @param startGraphName    The name of the start graph
     * @return                  A grammar view
     */
    public static GrammarModel loadGrammar(String grammarName, String startGraphName) {
        GrammarModel result = null;
        try {
            result = SystemStore.newGrammar(new File(GRAMMAR_DIR, grammarName));
            result.setLocalActiveNames(ResourceKind.HOST, QualName.parse(startGraphName));
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
        return result;
    }

    /**
     * Loads the prolog-test grammar
     * @return  A grammar view of the prolog-test grammar
     */
    public static GrammarModel testGrammar(String startGraph) {
        return PrologTestUtil.loadGrammar("prolog-test.gps", startGraph);
    }
}
