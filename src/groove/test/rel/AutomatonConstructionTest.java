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
package groove.test.rel;

import static groove.rel.Direction.BACKWARD;
import static groove.rel.Direction.FORWARD;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import groove.graph.EdgeRole;
import groove.graph.TypeFactory;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.rel.LabelVar;
import groove.rel.NormalAutomaton;
import groove.rel.NormalState;
import groove.rel.RegNode;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the construction of NormalAutomata
 * @author Arend Rensink
 * @version $Revision $
 */
@SuppressWarnings("unused")
public class AutomatonConstructionTest {
    /** Directory with test files (relative to the project) */
    static public final String GRAMMAR = "junit/samples/regexpr";
    /** Name of the type graph used in this test. */
    static public final String TYPE_NAME = "construction";

    private TypeGraph type;
    private final TypeLabel a = TypeFactory.instance().createLabel("a");
    private final TypeLabel b = TypeFactory.instance().createLabel("b");
    private final TypeLabel c = TypeFactory.instance().createLabel("c");
    private final TypeLabel cFlag = TypeFactory.instance().createLabel(
        EdgeRole.FLAG, "c");
    private final TypeLabel dFlag = TypeFactory.instance().createLabel(
        EdgeRole.FLAG, "d");
    private final TypeLabel aType = TypeFactory.instance().createLabel(
        EdgeRole.FLAG, "A");
    private final TypeLabel a1Type = TypeFactory.instance().createLabel(
        EdgeRole.FLAG, "A1");
    private final TypeLabel bType = TypeFactory.instance().createLabel(
        EdgeRole.FLAG, "B");
    private final TypeLabel cType = TypeFactory.instance().createLabel(
        EdgeRole.FLAG, "C");
    private final LabelVar x = new LabelVar("x", EdgeRole.BINARY);
    private final LabelVar yFlag = new LabelVar("y", EdgeRole.FLAG);
    private final LabelVar zType = new LabelVar("z", EdgeRole.NODE_TYPE);

    /** Loads type graph. */
    @Before
    public void setUp() {
        try {
            GrammarModel view = Groove.loadGrammar(GRAMMAR);
            this.type = view.getTypeModel(TYPE_NAME).toResource();
        } catch (FormatException e) {
            Assert.fail(e.getMessage());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    /** Directly tests NormalAutomaton construction methods. */
    @Test
    public void simpleConstructTest() {
        NormalAutomaton one = createAutomaton(false);
        assertFalse(one.isEquivalent(createAutomaton(true)));
        NormalState s0 = one.getStartState();
        assertFalse(s0.isFinal());
        assertTrue(s0.isInitial());
        NormalState s1 = addState(one, false);
        assertFalse(s1.isFinal());
        assertFalse(s1.isInitial());
        NormalState s2 = addState(one, true);
        assertTrue(s2.isFinal());
        assertFalse(s2.isInitial());
        assertFalse(one.isEquivalent(createAutomaton(false)));
        assertEquals(new HashSet<NormalState>(Arrays.asList(s0, s1, s2)),
            new HashSet<NormalState>(one.getStates()));
        s0.addSuccessor(FORWARD, this.a, s1);
        s0.addSuccessor(BACKWARD, this.x, s1);
        s1.addSuccessor(FORWARD, this.x, s2);
        s1.addSuccessor(BACKWARD, this.b, s0);
    }

    private NormalAutomaton createAutomaton(boolean isFinal) {
        return new NormalAutomaton(Collections.singleton(new RegNode(0)),
            isFinal);
    }

    private NormalState addState(NormalAutomaton aut, boolean isFinal) {
        return aut.addState(
            Collections.singleton(new RegNode(aut.getStates().size())), isFinal);
    }
}
