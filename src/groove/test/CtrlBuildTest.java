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
package groove.test;

import groove.control.CtrlAut;
import groove.control.CtrlParser;
import groove.io.ExtensionFilter;
import groove.trans.GraphGrammar;
import groove.util.Groove;
import groove.view.FormatException;

import java.io.IOException;

import junit.framework.TestCase;

/**
 * Tests the revised control automaton building.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlBuildTest extends TestCase {
    private static final String GRAMMAR_DIR = "junit/samples/";
    private static final String CONTROL_DIR = "junit/control/";
    private static final ExtensionFilter CONTROL_FILTER =
        Groove.createControlFilter();

    private final CtrlParser parser = CtrlParser.getInstance();
    private GraphGrammar testGrammar;
    {
        try {
            this.testGrammar =
                Groove.loadGrammar(GRAMMAR_DIR + "emptyrules").toGrammar();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /** Test for initialisation errors. */
    public void testInitErrors() {
        buildWrong("node x; if (a) bNode(out x); bNode(x);");
        buildWrong("node x; bNode(x);");
    }

    /** Test for typing errors. */
    public void testTypeErrors() {
        buildWrong("bInt(\"string\");");
        buildWrong("node x; bInt(out x);");
        buildWrong("a(_);");
    }

    /** Test for in/output parameter errors. */
    public void testDirErrors() {
        buildWrong("node x; bNode(out x); oNode(x);");
        buildWrong("int x; iInt(out x);");
        buildWrong("oNode(_)");
    }

    /** Tests building various loop structures. */
    public void testLoops() {
        buildCorrect("while (a|b) { c; d; }", 4, 5);
        buildCorrect("until (a|b) { c; d; }", 4, 5);
        buildCorrect("alap { choice { a; b; } or c; } d;", 4, 5);
        buildCorrect("(a|b)*;", 2, 3);
    }

    /** Sequences of rule calls. */
    public void testSeq() {
        buildCorrect("a;", 3, 2);
        buildCorrect("a; b; a;", 5, 4);
        buildCorrect("bNode(_); bNode-oNode(_,_);", 4, 3);
        buildCorrect("node x; bNode(out x); bNode-oNode(x, out x);", 4, 3);
    }

    /** Tests building if statements. */
    public void testIf() {
        buildCorrect("if (a|b) c;", 4, 5);
        buildCorrect("if (a|b) c; d;", 5, 6);
        buildCorrect("if (a|b) c; else d;", 4, 5);
    }

    /** Tests building try statements. */
    public void testTry() {
        buildCorrect("try { a;b; } d;", 5, 5);
        buildCorrect("try { a; b; } else { c; } d;", 5, 5);
    }

    /** Tests the {@code any} and {@code other} statements. */
    public void testAnyOther() {
        buildWrong("any;");
        buildCorrect(
            "node x; bNode(out x); iNode(x); iInt(3); iString-oNode(\"a\",_); other;",
            7, 13);
    }

    /** Tests function calls. */
    public void testFunctions() {
        buildCorrect("f(); function f() { a; }", 3, 2);
        buildCorrect("f(); f(); function f() { choice a; or {b;c;} }", 6, 7);
        buildCorrect("f(); function f() { node x; bNode(out x); }", 3, 2);
    }

    /** Builds a control automaton that should contain an error. */
    private void buildWrong(String program) {
        buildWrong(program, false);
    }

    /** Builds a control automaton that should contain an error. */
    private void buildWrong(String name, boolean file) {
        try {
            CtrlAut aut;
            if (file) {
                aut = buildFile(name);
            } else {
                aut = buildString(name);
            }
            System.err.printf("%s builds without errors: %n%s%n", name,
                aut.toString());
            assertTrue(false);
        } catch (FormatException e) {
            if (DEBUG) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void buildCorrect(String name, int nodeCount, int edgeCount) {
        buildCorrect(name, false, nodeCount, edgeCount);
    }

    private void buildCorrect(String name, boolean file, int nodeCount,
            int edgeCount) {
        try {
            CtrlAut result = file ? buildFile(name) : buildString(name);
            assertEquals(nodeCount, result.nodeCount());
            assertEquals(edgeCount, result.edgeCount());
        } catch (FormatException e) {
            System.err.printf("Errors in %s:%n%s%n", name, e.getMessage());
            assertTrue(false);
        }
    }

    /** Builds a control automaton from a file with a given name. */
    private CtrlAut buildFile(String filename) throws FormatException {
        CtrlAut result = null;
        try {
            result =
                this.parser.runFile(
                    CONTROL_FILTER.addExtension(CONTROL_DIR + filename),
                    this.testGrammar);
            if (DEBUG) {
                System.out.printf("Control automaton for %s:%n%s%n", filename,
                    result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /** Builds a control automaton from a given program. */
    private CtrlAut buildString(String program) throws FormatException {
        CtrlAut result = null;
        result = this.parser.runString(program, this.testGrammar);
        if (DEBUG) {
            System.out.printf("Control automaton for \'%s\':%n%s%n", program,
                result);
        }
        return result;
    }

    static private final boolean DEBUG = true;
}
