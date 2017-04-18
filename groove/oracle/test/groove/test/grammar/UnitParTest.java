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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import groove.algebra.Sort;
import groove.algebra.syntax.Expression;
import groove.algebra.syntax.Variable;
import groove.control.CtrlType;
import groove.control.CtrlVar;
import groove.grammar.QualName;
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
    /** NODE, IN. */
    private ProcedurePar p2;
    /** BOOL, OUT. */
    private ProcedurePar p3;
    /** INT, IN. */
    private RulePar r1;
    /** INT, BI. */
    private RulePar r2;
    /** BOOL, OUT. */
    private RulePar r3;

    @Before
    public void setup() {
        this.u1 = new UnitPar(CtrlType.INT, "u1", Direction.IN) { // empty
        };
        this.u2 = new UnitPar(CtrlType.NODE, "u2", Direction.IN) { // empty
        };
        this.u3 = new UnitPar(CtrlType.INT, "u3", Direction.OUT) { // empty
        };
        this.p1 = new ProcedurePar(new CtrlVar(null, "p1", CtrlType.INT), false);
        this.p2 = new ProcedurePar(new CtrlVar(null, "p2", CtrlType.NODE), false);
        this.p3 = new ProcedurePar(new CtrlVar(null, "p3", CtrlType.BOOL), true);
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
        Assert.assertEquals(this.u1, this.u1);
        UnitPar myU1 = new UnitPar(CtrlType.INT, "u1", Direction.IN) { // empty
        };
        Assert.assertEquals(myU1, this.u1);
        Assert.assertFalse(this.u1.equals(this.u2));
        Assert.assertFalse(this.u1.equals(this.r1));
        Assert.assertFalse(this.u1.equals(this.p1));
        // ProcedurePar.equals
        Assert.assertEquals(this.p1, this.p1);
        ProcedurePar myP1 = new ProcedurePar(new CtrlVar(null, "p1", CtrlType.INT), false);
        Assert.assertEquals(myP1, this.p1);
        Assert.assertFalse(this.p1.equals(this.p2));
        Assert.assertFalse(this.p1.equals(this.r1));
        // RulePar.equals
        try {
            Assert.assertEquals(this.r1, this.r1);
            Assert.assertFalse(this.r1.equals(this.r2));
            RulePar myR1 = new RulePar(AspectKind.PARAM_IN,
                new VariableNode(0, Expression.parse("1"), this.intNode), false);
            Assert.assertEquals(myR1, this.r1);
        } catch (FormatException exc) {
            Assert.fail(exc.getMessage());
        }
    }

    @Test
    public void testParse() {
        try {
            UnitPar myU1 = parse("int u1");
            UnitPar myU2 = parse("node u2");
            UnitPar myU3 = parse("out int u3");
            myU1.equals(this.u1);
            myU2.equals(this.u2);
            myU3.equals(this.u3);

            parse("int p1").equals(this.p1);
            parse("node p2").equals(this.p2);
            parse("out bool p3").equals(this.p3);

            parse("int r1").equals(this.r1);
            parse("inout int r2").equals(this.r2);
            parse("out bool p1").equals(this.r3);
        } catch (FormatException exc) {
            Assert.fail(exc.getMessage());
        }
    }
}
