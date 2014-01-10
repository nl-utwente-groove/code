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
package groove.test.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import groove.control.CtrlAut;
import groove.control.CtrlLoader;
import groove.control.symbolic.Term;
import groove.grammar.Grammar;
import groove.grammar.Rule;
import groove.grammar.model.FormatException;
import groove.util.Groove;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

/**
 * Tests the revised control automaton building.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlTester {
    private static final String CONTROL_DIR = "junit/control/";

    private Grammar testGrammar;
    {
        initGrammar("emptyrules");
    }

    void initGrammar(String name) {
        try {
            this.testGrammar =
                Groove.loadGrammar(CONTROL_DIR + name).toGrammar();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /** 
     * Builds a control automaton that should contain an error.
     * @param program control expression; non-{@code null}
     */
    protected void buildWrong(String program) {
        buildWrong("dummy", program);
    }

    /** 
     * Builds a named control automaton that should contain an error.
     * @param name automaton name, or file name if there is no program
     * @param program control expression; may be {@code null}
     */
    protected void buildWrong(String name, String program) {
        try {
            CtrlAut aut;
            if (program == null) {
                aut = buildFile(name);
            } else {
                aut = buildString(name, program);
            }
            fail(String.format("%s builds without errors: %n%s%n", name,
                aut.toString()));
        } catch (FormatException e) {
            if (DEBUG) {
                System.out.println(e.getMessage());
            }
        }
    }

    /** 
     * Builds a symbolic term from a control program.
     * @param program control expression; non-{@code null}
     */
    protected Term buildTerm(String program) {
        try {
            return createLoader().parse("dummy", program).getChild(3).toTerm();
        } catch (FormatException e) {
            Assert.fail(e.getMessage());
            return null;
        }
    }

    /** 
     * Builds a control automaton of a certain expected size.
     * @param program control expression; non-{@code null}
     * @param nodeCount expected node count
     * @param edgeCount expected edge count
     */
    protected CtrlAut buildCorrect(String program, int nodeCount, int edgeCount) {
        return buildCorrect("dummy", program, nodeCount, edgeCount);
    }

    /** 
     * Builds a named control automaton of a certain expected size.
     * @param name automaton name, or file name if there is no program
     * @param program control expression; may be {@code null}
     * @param nodeCount expected node count
     * @param edgeCount expected edge count
     */
    protected CtrlAut buildCorrect(String name, String program, int nodeCount,
            int edgeCount) {
        CtrlAut result = null;
        try {
            result =
                program == null ? buildFile(name) : buildString(name, program);
            assertEquals(nodeCount, result.nodeCount());
            assertEquals(edgeCount, result.edgeCount());
        } catch (FormatException e) {
            fail(e.getMessage());
        }
        return result;
    }

    /** Builds a control automaton from a file with a given name. */
    protected CtrlAut buildFile(String programName) throws FormatException {
        CtrlAut result = null;
        try {
            result =
                CtrlLoader.run(this.testGrammar, programName, new File(
                    CONTROL_DIR));
            if (DEBUG) {
                System.out.printf("Control automaton for %s:%n%s%n",
                    programName, result);
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
        return result;
    }

    /** Builds a control automaton from a given program. */
    protected CtrlAut buildString(String programName, String program)
        throws FormatException {
        CtrlAut result = null;
        result = CtrlLoader.run(this.testGrammar, programName, program);
        if (DEBUG) {
            System.out.printf("Control automaton for \'%s\':%n%s%n", program,
                result);
        }
        return result;
    }

    /** Returns the rule with a given name. */
    protected Rule getRule(String name) {
        return this.testGrammar.getRule(name);
    }

    /** Callback factory method for a loader of the test grammar. */
    protected CtrlLoader createLoader() {
        return new CtrlLoader(
            this.testGrammar.getProperties().getAlgebraFamily(),
            this.testGrammar.getAllRules());
    }

    static private final boolean DEBUG = false;
}
