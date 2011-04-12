/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.test.prolog;

import static org.junit.Assert.fail;
import groove.prolog.GrooveState;
import groove.prolog.PrologQuery;
import groove.prolog.QueryResult;
import groove.prolog.QueryReturnValue;
import groove.prolog.exception.GroovePrologException;
import groove.prolog.exception.GroovePrologLoadingException;
import groove.view.GrammarView;
import groove.view.StoredGrammarView;

import java.io.File;
import java.io.IOException;

/**
 * Contains some helper methods for building queries
 * @author Lesley Wevers
 */
public class PrologTestUtil {
    /** Location of the samples. */
    static public final String GRAMMAR_DIR = "junit/samples";

    private PrologTestUtil() {
        /**
         * Left blank by design
         */
    }

    /**
     * Executes a query
     * @param grooveState       The groove state to use for the query
     * @param query             The query to execute
     * @return                  The query result
     */
    public static QueryResult executeQuery(GrooveState grooveState, String query) {
        PrologQuery prologQuery = new PrologQuery(grooveState);

        try {
            QueryResult queryResult = prologQuery.newQuery(query);

            /**
             * TODO: I think this is a bit ugly
             * It would be better if there would be a Query class, and an Engine class, 
             * where the Engine contains the environment and creates Queries, and the 
             * Query class contains the interpreter.
             */
            while (prologQuery.next() != null) {
                // Load all results
            }

            return queryResult;
        } catch (GroovePrologLoadingException e) {
            e.printStackTrace();
            fail("Got exception: " + e.toString());
        } catch (GroovePrologException e) {
            e.printStackTrace();
            fail("Got exception: " + e.toString());
        }

        return null;
    }

    /**
     * Checks if a query succeeds
     * @param grooveState       A groove state
     * @param query             A query
     * @return                  Did the query succeed?
     */
    public static boolean test(GrooveState grooveState, String query)
        throws GroovePrologException {
        PrologQuery prologQuery = new PrologQuery(grooveState);

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
    public static GrammarView loadGrammar(String grammarName,
            String startGraphName) {
        GrammarView result = null;
        try {
            result =
                StoredGrammarView.newInstance(
                    new File(GRAMMAR_DIR, grammarName), startGraphName, false);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
        return result;
    }

    private static GrammarView testGrammar;

    /**
     * Loads the prolog-test grammar
     * @return  A grammar view of the prolog-test grammar
     */
    public static GrammarView testGrammar(String startGraph) {
        if (testGrammar == null) {
            testGrammar =
                PrologTestUtil.loadGrammar("prolog-test.gps", startGraph);
        }
        return testGrammar;
    }
}
