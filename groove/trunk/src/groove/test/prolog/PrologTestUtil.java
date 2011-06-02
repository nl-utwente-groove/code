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
import gnu.prolog.vm.PrologException;
import groove.prolog.GrooveEnvironment;
import groove.prolog.GrooveState;
import groove.prolog.PrologEngine;
import groove.prolog.QueryResult;
import groove.prolog.QueryReturnValue;
import groove.view.FormatException;
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

    private static PrologEngine prologQuery;

    private PrologTestUtil() {
        /**
         * Blank by design
         */
    }

    /**
     * Executes a query
     * @param grooveState       The groove state to use for the query
     * @param query             The query to execute
     * @return                  The query result
     */
    public static QueryResult executeQuery(GrooveState grooveState, String query) {
        if (prologQuery == null) {
            prologQuery = PrologEngine.instance();
        }

        prologQuery.setGrooveState(grooveState);

        try {
            QueryResult queryResult = prologQuery.newQuery(query);

            while (prologQuery.next() != null) {
                // Load all results
            }

            return queryResult;
        } catch (FormatException e) {
            e.printStackTrace();
            fail("Got exception: " + e.toString());
        } catch (PrologException e) {
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
        throws FormatException, PrologException {
        if (prologQuery == null) {
            prologQuery = PrologEngine.instance();
            prologQuery.setEnvironment(new GrooveEnvironment(null, null));
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

    /**
     * Loads the prolog-test grammar
     * @return  A grammar view of the prolog-test grammar
     */
    public static GrammarView testGrammar(String startGraph) {
        return PrologTestUtil.loadGrammar("prolog-test.gps", startGraph);
    }
}
