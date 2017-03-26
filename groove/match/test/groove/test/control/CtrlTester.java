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
import groove.control.CtrlLoader;
import groove.control.instance.Automaton;
import groove.control.parse.CtrlTree;
import groove.control.template.Fragment;
import groove.control.template.Program;
import groove.grammar.Grammar;
import groove.grammar.QualName;
import groove.grammar.Rule;
import groove.graph.Graph;
import groove.util.Groove;
import groove.util.parse.FormatException;
import junit.framework.Assert;

/**
 * Tests the revised control automaton building.
 * @author Arend Rensink
 * @version $Revision $
 */
abstract public class CtrlTester {
    /** The directory from which grammars are loaded. */
    public static final String CONTROL_DIR = "junit/control/";

    /** Loads the grammar to be used for testing. */
    protected void initGrammar(String name) {
        if (!name.equals(this.grammarName)) {
            this.testGrammar = loadGrammar(name);
            this.grammarName = name;
        }
    }

    private String grammarName;

    /** Returns the currently loaded grammar. */
    protected Grammar getGrammar() {
        return this.testGrammar;
    }

    private Grammar testGrammar;

    /** Loads a named grammar from {@link #CONTROL_DIR}.*/
    protected Grammar loadGrammar(String name) {
        Grammar result = null;
        try {
            result = Groove.loadGrammar(CONTROL_DIR + name)
                .toGrammar();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return result;
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
            buildString(name, program);
            fail(String.format("%s builds without errors%n", name));
        } catch (FormatException e) {
            if (DEBUG) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Builds a control automaton of a certain expected size.
     * @param program control expression; non-{@code null}
     * @param nodeCount expected node count
     * @param edgeCount expected edge count
     */
    protected Graph buildCorrect(String program, int nodeCount, int edgeCount) {
        return buildCorrect("dummy", program, nodeCount, edgeCount);
    }

    /**
     * Builds a named control automaton of a certain expected size.
     * @param name automaton name, or file name if there is no program
     * @param program control expression; may be {@code null}
     * @param nodeCount expected node count
     * @param edgeCount expected edge count
     */
    protected Graph buildCorrect(String name, String program, int nodeCount, int edgeCount) {
        Graph result = null;
        try {
            Program prog = buildString(name, program);
            result = new Automaton(prog).toGraph(false);
            assertEquals(nodeCount, result.nodeCount());
            assertEquals(edgeCount, result.edgeCount());
        } catch (FormatException e) {
            fail(e.getMessage());
        }
        return result;
    }

    /** Builds a control automaton from a given program. */
    protected Program buildString(String programName, String program) throws FormatException {
        Program result = null;
        result = CtrlLoader.run(this.testGrammar, programName, program);
        if (DEBUG) {
            System.out.printf("Control program for \'%s\':%n%s%n", program, result);
        }
        return result;
    }

    /** Returns the rule with a given name. */
    protected Rule getRule(String name) {
        return this.testGrammar.getRule(QualName.parse(name));
    }

    /**
     * Builds a symbolic term from a control program.
     * @param program control expression; non-{@code null}
     */
    protected Fragment buildFragment(String program) {
        Fragment result = null;
        try {
            result = buildTree(program).toFragment();
        } catch (FormatException e) {
            Assert.fail(e.getMessage());
        }
        return result;
    }

    /**
     * Builds a symbolic term from a control program.
     * @param program control expression; non-{@code null}
     */
    protected CtrlTree buildTree(String program) {
        try {
            return createLoader().addControl(DUMMY, program)
                .check();
        } catch (FormatException e) {
            Assert.fail(e.getMessage());
            return null;
        }
    }

    /** Callback factory method for a loader of the test grammar. */
    protected CtrlLoader createLoader() {
        return new CtrlLoader(this.testGrammar.getProperties(), this.testGrammar.getAllRules());
    }

    static private final boolean DEBUG = false;
    /** Dummy name for test programs. */
    static protected final QualName DUMMY = QualName.name("dummy");
}
