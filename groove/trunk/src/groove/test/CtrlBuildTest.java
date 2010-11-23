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

    public void testSimpleRuleCall() {
        try {
            CtrlAut result = build("simpleRuleCall");
            assertEquals(result.nodeCount(), 3);
            assertEquals(result.edgeCount(), 2);
        } catch (FormatException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    public void testWhile() {
        try {
            CtrlAut result = build("while");
            assertEquals(result.nodeCount(), 6);
            assertEquals(result.edgeCount(), 9);
        } catch (FormatException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    public void testUntil() {
        try {
            CtrlAut result = build("until");
            assertEquals(result.nodeCount(), 6);
            assertEquals(result.edgeCount(), 9);
        } catch (FormatException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    public void testSeq() {
        try {
            CtrlAut result = build("seq");
            assertEquals(result.nodeCount(), 5);
            assertEquals(result.edgeCount(), 4);
        } catch (FormatException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    public void testIfThen() {
        try {
            CtrlAut result = build("ifThen");
            assertEquals(result.nodeCount(), 5);
            assertEquals(result.edgeCount(), 6);
        } catch (FormatException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    public void testIfThenSeq() {
        try {
            CtrlAut result = build("ifThenSeq");
            assertEquals(result.nodeCount(), 6);
            assertEquals(result.edgeCount(), 7);
        } catch (FormatException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    public void testIfThenElse() {
        try {
            CtrlAut result = build("ifThenElse");
            assertEquals(result.nodeCount(), 6);
            assertEquals(result.edgeCount(), 7);
        } catch (FormatException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    public void testTry() {
        try {
            CtrlAut result = build("try");
            assertEquals(result.nodeCount(), 5);
            assertEquals(result.edgeCount(), 5);
        } catch (FormatException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    public void testTryElse() {
        try {
            CtrlAut result = build("tryElse");
            assertEquals(result.nodeCount(), 6);
            assertEquals(result.edgeCount(), 6);
        } catch (FormatException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    public void testAlap() {
        try {
            CtrlAut result = build("alap");
            assertEquals(result.nodeCount(), 6);
            assertEquals(result.edgeCount(), 11);
        } catch (FormatException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    public void testStar() {
        try {
            CtrlAut result = build("star");
            assertEquals(result.nodeCount(), 4);
            assertEquals(result.edgeCount(), 9);
        } catch (FormatException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    private CtrlAut build(String filename) throws FormatException {
        CtrlAut result = null;
        try {
            result =
                this.parser.run(
                    CONTROL_FILTER.addExtension(CONTROL_DIR + filename),
                    this.testGrammar);
            System.out.printf("Control automaton for %s:%n%s%n", filename,
                result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
