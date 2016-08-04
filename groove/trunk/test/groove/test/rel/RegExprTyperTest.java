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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import groove.algebra.Sort;
import groove.automaton.RegExpr;
import groove.automaton.RegExprTyper;
import groove.automaton.RegExprTyper.Result;
import groove.grammar.QualName;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.model.GrammarModel;
import groove.grammar.rule.LabelVar;
import groove.grammar.type.ImplicitTypeGraph;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeElement;
import groove.grammar.type.TypeFactory;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeNode;
import groove.graph.EdgeRole;
import groove.util.Exceptions;
import groove.util.Groove;
import groove.util.Pair;
import groove.util.parse.FormatException;

/** Tests the class {@link RegExpr}. */
public class RegExprTyperTest {
    /** Directory with test files (relative to the project) */
    static public final String GRAPH_TEST_DIR = "junit/graphs";
    /** Directory with test grammar (relative to the project) */
    static public final String GRAMMAR = "junit/types/regexpr";

    static RegExprTyper implicitTyper;
    static RegExprTyper explicitTyper;
    static TypeNode A, A1, A2, B, B1, C, D, XInt, XBool, XReal, XString;
    static TypeNode Top, IInt, IBool, IReal, IString;
    static String xBin, xFlag, xType;
    /** Flag switching between implicit and explicit typing. */
    boolean implicit;
    TypeNode[][] m;

    /** Initialisation of statics. */
    @BeforeClass
    public static void initStatics() {
        HostGraph startGraph;
        ImplicitTypeGraph implicitType = null;
        TypeGraph explicitType;
        try {
            GrammarModel view = Groove.loadGrammar(GRAMMAR);
            explicitType = view.getTypeModel(QualName.name("type"))
                .toResource();
            assert explicitType != null; // test grammar has this type graph
            startGraph = view.getHostModel(QualName.name("start"))
                .toResource();
            assert startGraph != null; // test grammar has this start graph
        } catch (FormatException e) {
            fail(e.getMessage());
            throw Exceptions.UNREACHABLE;
        } catch (IOException e) {
            fail(e.getMessage());
            throw Exceptions.UNREACHABLE;
        }
        implicitType = new ImplicitTypeGraph();
        for (HostEdge testEdge : startGraph.edgeSet()) {
            implicitType.addLabel(testEdge.label());
        }
        for (HostNode testNode : startGraph.nodeSet()) {
            implicitType.addLabel(testNode.getType()
                .label());
        }
        xBin = "xBin";
        xFlag = "xFlag";
        xType = "xType";
        Map<LabelVar,Set<? extends TypeElement>> implicitVars =
            new HashMap<>();
        for (TypeEdge edge : implicitType.edgeSet()) {
            String label = edge.label()
                .toString();
            if (label.equals("flag:a2")) {
                implicitVars.put(new LabelVar(xFlag, EdgeRole.FLAG), Collections.singleton(edge));
            } else if (label.equals("type:A2")) {
                implicitVars.put(new LabelVar(xType, EdgeRole.NODE_TYPE),
                    Collections.singleton(edge));
            } else if (label.equals("aToB")) {
                implicitVars.put(new LabelVar(xBin, EdgeRole.BINARY), Collections.singleton(edge));
            }
        }
        implicitTyper = new RegExprTyper(implicitType, implicitVars);
        Map<LabelVar,Set<? extends TypeElement>> explicitVars =
            new HashMap<>();
        for (TypeEdge edge : explicitType.edgeSet()) {
            String label = edge.label()
                .toString();
            if (label.equals("flag:a2")) {
                explicitVars.put(new LabelVar(xFlag, EdgeRole.FLAG), Collections.singleton(edge));
            } else if (label.equals("aToB")) {
                explicitVars.put(new LabelVar(xBin, EdgeRole.BINARY), Collections.singleton(edge));
            }
        }
        explicitVars.put(new LabelVar(xType, EdgeRole.NODE_TYPE),
            Collections.singleton(explicitType.getNode("type:A2")));
        explicitTyper = new RegExprTyper(explicitType, explicitVars);
        A = explicitType.getNode("type:A");
        A1 = explicitType.getNode("type:A1");
        A2 = explicitType.getNode("type:A2");
        B = explicitType.getNode("type:B");
        B1 = explicitType.getNode("type:B1");
        C = explicitType.getNode("type:C");
        D = explicitType.getNode("type:D");
        TypeFactory explicitFactory = explicitType.getFactory();
        XBool = explicitFactory.getDataType(Sort.BOOL);
        XInt = explicitFactory.getDataType(Sort.INT);
        XReal = explicitFactory.getDataType(Sort.REAL);
        XString = explicitFactory.getDataType(Sort.STRING);
        Top = implicitType.getTopNode();
        TypeFactory implicitFactory = implicitType.getFactory();
        IBool = implicitFactory.getDataType(Sort.BOOL);
        IInt = implicitFactory.getDataType(Sort.INT);
        IReal = implicitFactory.getDataType(Sort.REAL);
        IString = implicitFactory.getDataType(Sort.STRING);
    }

    /** Tests the construction of atoms. */
    @Test
    public void testAtom() {
        this.implicit = false;
        TypeNode[][] n1 = {{A, A}, {A1, A1}, {A2, A2}};
        equals("type:A", n1);
        TypeNode[][] e1 = {{A1, C}, {A2, D}};
        equals("aTo", e1);
        TypeNode[][] f1 = {{A2, A2}};
        equals("flag:a2", f1);
        this.implicit = true;
        TypeNode[][] n2 = {{Top, Top}};
        equals("type:A", n2);
        TypeNode[][] e2 = {{Top, Top}, {Top, IInt}, {Top, IReal}, {Top, IString}, {Top, IBool}};
        equals("aTo", e2);
        TypeNode[][] f2 = {{Top, Top}};
        equals("flag:a2", f2);
    }

    /** Tests the construction of the empty expression. */
    @Test
    public void testEmpty() {
        this.implicit = false;
        TypeNode[][] n1 = {{A, A}, {A, A1}, {A, A2}, {A1, A}, {A1, A1}, {A1, A2}, {A2, A}, {A2, A1},
            {A2, A2}, {B, B}, {B, B1}, {B1, B}, {B1, B1}, {C, C}, {D, D}, {XInt, XInt},
            {XReal, XReal}, {XString, XString}, {XBool, XBool}};
        equals("=", n1);
        this.implicit = true;
        TypeNode[][] n2 =
            {{Top, Top}, {IInt, IInt}, {IReal, IReal}, {IString, IString}, {IBool, IBool}};
        equals("=", n2);
    }

    /** Tests the construction of sharp labels. */
    @Test
    public void testSharp() {
        this.implicit = false;
        TypeNode[][] n1 = {{A, A}, {A1, A1}, {A2, A2}};
        equals("type:#A", n1);
        this.implicit = true;
        TypeNode[][] n2 = {{Top, Top}};
        equals("type:#A", n2);
    }

    /** Tests the wildcard operator. */
    @Test
    public void testWildcard() {
        this.implicit = false;
        TypeNode[][] n1 = {{A2, A2}};
        equals("type:?xType", n1);
        TypeNode[][] e1 = {{A, B}, {A, B1}, {A1, B}, {A1, B1}, {A2, B}, {A2, B1}};
        equals("?xBin", e1);
        TypeNode[][] f1 = {{A2, A2}};
        equals("flag:?xFlag", f1);
        TypeNode[][] nx1 = {{A, A}, {A1, A1}, {A2, A2}, {B1, B1}};
        equals("type:?[A,B1]", nx1);
        TypeNode[][] ex1 = {{A1, C}, {A2, D}, {B1, A2}};
        equals("?[aTo,b1ToA2]", ex1);
        TypeNode[][] fx1 = {{A2, A2}};
        equals("flag:?[a2]", fx1);
        TypeNode[][] fxx1 = {{A1, A1}};
        equals("flag:?[^a,a2]", fxx1);
        this.implicit = true;
        TypeNode[][] n2 = {{Top, Top}};
        equals("type:?", n2);
        TypeNode[][] f2 = {};
        equals("flag:?[^a,a1,a2]", f2);
        TypeNode[][] e2 = {{Top, Top}, {Top, IInt}, {Top, IReal}, {Top, IString}, {Top, IBool}};
        equals("?", e2);
    }

    /** Tests the inversion operator. */
    @Test
    public void testInv() {
        this.implicit = false;
        TypeNode[][] x = {{C, A1}, {D, A2}};
        equals("-aTo", x);
    }

    /** Tests the choice operator. */
    @Test
    public void testChoice() {
        this.implicit = false;
        TypeNode[][] x = {{C, A}, {C, A1}, {C, A2}, {B1, A2}, {D, D}, {A1, A1}};
        equals("cToA | b1ToA2 | type:D | flag:a1", x);
    }

    /** Tests the sequential operator. */
    @Test
    public void testSeq() {
        this.implicit = false;
        TypeNode[][] x1 = {};
        equals("cToA.b1ToA2", x1);
        TypeNode[][] x2 = {{C, B}, {C, B1}};
        equals("cToA.a1ToB", x2);
    }

    /** Tests the star suffix operator. */
    @Test
    public void testStar() {
        this.implicit = false;
        TypeNode[][] x1 = {};
        equals("(cToA.b1ToA2)*", x1);
        TypeNode[][] x2 = {{A, A2}, {A1, A2}, {A2, A2}};
        equals("(aToB.b1ToA2)*", x2);
    }

    /** Tests the plus suffix operator. */
    @Test
    public void testPlus() {
        this.implicit = false;
        TypeNode[][] x1 = {};
        equals("(cToA.b1ToA2)+", x1);
        TypeNode[][] x2 = {{A, A2}, {A1, A2}, {A2, A2}};
        equals("(aToB.b1ToA2)+", x2);
    }

    private Pair<RegExpr,TypeNode[][]> equals(String expr, TypeNode[][] matrix) {
        RegExpr e = parse(expr);
        Result r = e.apply(this.implicit ? implicitTyper : explicitTyper);
        assertEquals(r(matrix), r.getMap());
        return Pair.newPair(e, matrix);
    }

    private RegExpr parse(String s) {
        RegExpr result = null;
        try {
            result = RegExpr.parse(s);
            RegExpr other = RegExpr.parse(s);
            assertEquals(result, other);
            assertEquals(result.hashCode(), other.hashCode());
        } catch (FormatException e) {
            fail("Can't parse expression " + s);
        }
        return result;
    }

    private Map<TypeNode,Set<TypeNode>> r(TypeNode[][] m) {
        Map<TypeNode,Set<TypeNode>> result = new HashMap<>();
        for (TypeNode[] p : m) {
            assert p.length == 2;
            Set<TypeNode> image = result.get(p[0]);
            if (image == null) {
                result.put(p[0], image = new HashSet<>());
            }
            image.add(p[1]);
        }
        return result;
    }
}
