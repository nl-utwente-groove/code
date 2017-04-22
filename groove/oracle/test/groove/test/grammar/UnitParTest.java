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
package groove.test.grammar;

import static groove.grammar.UnitPar.parse;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.fail;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import groove.algebra.Sort;
import groove.algebra.syntax.Expression;
import groove.algebra.syntax.Variable;
import groove.control.CtrlType;
import groove.control.CtrlVar;
import groove.grammar.QualName;
import groove.grammar.Signature;
import groove.grammar.UnitPar;
import groove.grammar.UnitPar.Direction;
import groove.grammar.UnitPar.ProcedurePar;
import groove.grammar.UnitPar.RulePar;
import groove.grammar.aspect.AspectKind;
import groove.grammar.rule.VariableNode;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeNode;
import groove.util.parse.FormatException;

/**
 * Test for {@link UnitPar}
 * @author Arend Rensink
 * @version $Revision $
 */
@SuppressWarnings("javadoc")
public class UnitParTest {
    /** Test type graph. */
    private TypeGraph typeGraph;
    /** Bool type node. */
    private TypeNode boolNode;
    /** Integer type node. */
    private TypeNode intNode;
    /** INT, IN. */
    private UnitPar u1;
    /** NODE, IN. */
    private UnitPar u2;
    /** INT, OUT. */
    private UnitPar u3;
    /** INT, IN. */
    private ProcedurePar p1;
    /** BOOL, ASK. */
    private ProcedurePar p2;
    /** NODE, OUT. */
    private ProcedurePar p3;
    /** INT, IN. */
    private RulePar r1;
    /** INT, BI. */
    private RulePar r2;
    /** BOOL, OUT. */
    private RulePar r3;

    @Before
    public void setup() {
        this.u1 = UnitPar.par("u1", CtrlType.INT, Direction.IN);
        this.u2 = UnitPar.par("u2", CtrlType.NODE, Direction.IN);
        this.u3 = UnitPar.par("u3", CtrlType.INT, Direction.OUT);
        this.p1 = new ProcedurePar(new CtrlVar(null, "p1", CtrlType.INT), Direction.IN);
        this.p2 = new ProcedurePar(new CtrlVar(null, "p2", CtrlType.BOOL), Direction.ASK);
        this.p3 = new ProcedurePar(new CtrlVar(null, "p3", CtrlType.NODE), Direction.OUT);
        this.typeGraph = new TypeGraph(QualName.name("test"));
        this.boolNode = this.typeGraph.getFactory()
            .getDataType(Sort.BOOL);
        this.intNode = this.typeGraph.getFactory()
            .getDataType(Sort.INT);
        try {
            this.r1 = new RulePar(AspectKind.PARAM_IN,
                new VariableNode(0, Expression.parse("1"), this.intNode), false);
            this.r2 = new RulePar(AspectKind.PARAM_BI,
                new VariableNode(1, new Variable("r2", Sort.INT), this.intNode), false);
            this.r3 = new RulePar(AspectKind.PARAM_OUT,
                new VariableNode(2, new Variable("r3", Sort.BOOL), this.boolNode), false);
        } catch (FormatException exc) {
            Assert.fail(exc.getMessage());
        }
    }

    @Test
    public void testEquals() {
        assertEquals(this.u1, this.u1);
        UnitPar myU1 = UnitPar.par("u1", CtrlType.INT, Direction.IN);
        assertEquals(myU1, this.u1);
        assertFalse(this.u1.equals(this.u2));
        assertFalse(this.u1.equals(this.r1));
        assertFalse(this.u1.equals(this.p1));
        // ProcedurePar.equals
        assertEquals(this.p1, this.p1);
        ProcedurePar myP1 = new ProcedurePar(new CtrlVar(null, "p1", CtrlType.INT), Direction.IN);
        assertEquals(myP1, this.p1);
        assertFalse(this.p1.equals(this.p2));
        assertFalse(this.p1.equals(this.r1));
        // RulePar.equals
        try {
            assertEquals(this.r1, this.r1);
            assertFalse(this.r1.equals(this.r2));
            RulePar myR1 = new RulePar(AspectKind.PARAM_IN,
                new VariableNode(0, Expression.parse("1"), this.intNode), false);
            assertEquals(myR1, this.r1);
        } catch (FormatException exc) {
            fail(exc.getMessage());
        }
    }

    @Test
    public void testParParse() {
        try {
            UnitPar myU1 = parse("int u1");
            UnitPar myU2 = parse("node u2");
            UnitPar myU3 = parse("out int u3");
            assertEquals(this.u1, myU1);
            assertEquals(this.u2, myU2);
            assertEquals(this.u3, myU3);

            assertEquals(this.p1, parse("int p1"));
            assertEquals(this.p2, parse("ask bool p2"));
            assertEquals(this.p3, parse("out node p3"));

            assertFalse(parse("int r1").equals(this.r1));
            assertFalse(parse("bi int r2").equals(this.r2));
            assertFalse(parse("out bool r3").equals(this.r3));
        } catch (FormatException exc) {
            fail(exc.getMessage());
        }
    }

    @Test
    public void testSigParse() {
        try {
            Signature<?> expected = new Signature<>(Arrays.asList(this.p1, this.p2, this.p3));
            Signature<?> parsed = Signature.parse("int p1, ask bool p2, out node p3");
            Assert.assertEquals(expected, parsed);
        } catch (FormatException exc) {
            Assert.fail(exc.getMessage());
        }
    }
}
