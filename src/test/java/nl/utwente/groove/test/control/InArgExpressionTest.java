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
 * $Id: TermTest.java 6285 2023-11-13 14:26:58Z rensink $
 */
package nl.utwente.groove.test.control;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.algebra.syntax.ExprTreeParser;
import nl.utwente.groove.algebra.syntax.Expression;
import nl.utwente.groove.algebra.syntax.SortMap;
import nl.utwente.groove.control.Call;
import nl.utwente.groove.control.CtrlArg;
import nl.utwente.groove.control.CtrlArg.Expr;
import nl.utwente.groove.control.CtrlArg.Var;
import nl.utwente.groove.control.CtrlType;
import nl.utwente.groove.control.CtrlVar;
import nl.utwente.groove.control.parse.CtrlTree;
import nl.utwente.groove.control.term.Term;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Test class for the construction of symbolic control terms.
 * @author Arend Rensink
 * @version $Revision: 6285 $
 */
@SuppressWarnings("javadoc")
public class InArgExpressionTest extends CtrlTester {
    {
        initGrammar("inArgs");
    }

    @BeforeClass
    public static void initPrototype() {
        p = Term.prototype();
    }

    @Before
    public void init() {
        this.iOut = getRule("outInt");
        this.iRule = getRule("inIntOutInt");
        this.rRule = getRule("inRealOutReal");
        this.sRule = getRule("inStringOutString");
        this.sortMap = new SortMap();
        this.sortMap.add("ix", Sort.INT);
        this.sortMap.add("iy", Sort.INT);
        this.sortMap.add("rx", Sort.REAL);
        this.sortMap.add("ry", Sort.REAL);
        this.sortMap.add("sx", Sort.STRING);
        this.sortMap.add("sy", Sort.STRING);
    }

    private Rule iOut;
    private Rule iRule;
    private Rule sRule;
    private Rule rRule;
    private SortMap sortMap;

    @Test
    public void testEqual() {
        Rule iOut = this.iOut;
        Rule iRule = this.iRule;
        Rule rRule = this.rRule;
        Rule sRule = this.sRule;
        //equal("int ix := inIntOutInt(1);", call(iRule, "1", "ix"));
        equal("int iy := outInt(); int ix := inIntOutInt(ite(true,3,iy+3));",
              call(iOut, "iy").seq(call(iRule, "ite(true,3,iy+3)", "ix")));
        equal("string sx; inStringOutString(\"a\"+\"b\", out sx);",
              call(sRule, "\"a\"+\"b\"", "sx"));
        equal("real rx := inRealOutReal(1.0); alap { rx := inRealOutReal(max(rx,(real)1));}",
              call(rRule, "1.0", "rx").seq(call(rRule, "max(rx,(real) 1)", "rx").alap()));
    }

    @Test
    public void testFail() {
        buildWrong("int ix = inIntOutInt(1.1)");
        buildWrong("int ix = inIntOutInt(add(1))");
        buildWrong("int ix = inIntOutInt(1+ix)");
        buildWrong("real rx = inRealOutReal(1.0); int ix = inIntOutInt(1+rx)");
    }

    private Term call(Rule rule, String... args) {
        List<CtrlArg> ruleArgs = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            var arg = args[i];
            var par = rule.getSignature().getPar(i);
            CtrlArg ruleArg;
            if (par.getType() == CtrlType.NODE || par.isOutOnly()) {
                ruleArg = new Var(new CtrlVar(arg, par.getType()), !par.isOutOnly());
            } else {
                ruleArg = new Expr(parseAsExpr(arg));
            }
            ruleArgs.add(ruleArg);
        }
        return p.call(new Call(rule, ruleArgs));
    }

    private Expression parseAsExpr(String arg) {
        try {
            return ExprTreeParser.EXPR_PARSER.parse(arg).toExpression(this.sortMap);
        } catch (FormatException exc) {
            Assert
                .fail(String
                    .format("%s cannot be parsed as expression: %s", arg, exc.getMessage()));
            throw Exceptions.UNREACHABLE;
        }
    }

    void equal(String program, Term term) {
        assertEquals(term, buildTerm(program));
    }

    /**
     * Builds a symbolic term from a control program.
     * @param program control expression; non-{@code null}
     */
    protected Term buildTerm(String program) {
        return buildFragment(program).getMain();
    }

    /**
     * Builds a symbolic term from a function or recipe in a control program.
     * @param program control expression; non-{@code null}
     * @param procName name of the recipe or function
     * @param function if {@code true}, a function is retrieved, otherwise a recipe
     */
    protected Term buildProcTerm(String program, String procName, boolean function) {
        try {
            CtrlTree tree = buildTree(program)
                .getChild(function
                    ? 2
                    : 3);
            CtrlTree body = null;
            for (int i = 0; i < tree.getChildCount(); i++) {
                CtrlTree procTree = tree.getChild(i);
                if (procTree.getChild(0).getText().equals(procName)) {
                    body = procTree.getChild(2);
                }
            }
            assert body != null : String
                .format("Invoked procedure '%s' not declared in '%s'", procName, program);
            return body.toTerm();
        } catch (FormatException e) {
            Assert.fail(e.getMessage());
            return null;
        }
    }

    static private Term p;
}
