/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: TestingTransforming.java,v 1.6 2008-02-05 13:28:21 rensink Exp $
 */
package groove.test.abs;

import groove.abs.AbstrGraph;
import groove.abs.Abstraction;
import groove.abs.ConcretePart;
import groove.abs.DefaultAbstrGraph;
import groove.abs.ExceptionIncompatibleWithMaxIncidence;
import groove.abs.GraphPattern;
import groove.abs.MultiplicityInformation;
import groove.abs.PatternFamily;
import groove.abs.SetMaterialisations;
import groove.abs.Util;
import groove.graph.DefaultEdge;
import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.DefaultMorphism;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Morphism;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.io.DefaultGxl;
import groove.rel.VarNodeEdgeHashMap;
import groove.rel.VarNodeEdgeMap;
import groove.trans.DefaultApplication;
import groove.trans.GraphGrammar;
import groove.trans.RuleApplication;
import groove.trans.RuleEvent;
import groove.trans.SPOEvent;
import groove.trans.SPORule;
import groove.trans.SystemRecord;
import groove.util.Groove;
import groove.view.FormatException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Tests for the transformation.
 * 
 * @author Iovka Boneva
 * @version $Revision $
 */
@Deprecated
@SuppressWarnings("all")
public class TestingTransforming extends TestCase {

    // private final Abstraction.Parameters defaultOptions = new
    // Abstraction.Parameters(false, LinkPrecision.HIGH, false);
    // -----------------------------------------------------------------
    // GRAPHS AND MATCHINGS
    // -----------------------------------------------------------------
    /** A prefix for the examples. */
    private static final String PATH_PREFIX = "junit/samples/";

    /** Empty graph; its type is used for instanciating all graphs for testing */
    Graph type = new DefaultGraph();

    /** Set of nodes to be used for the graphs */
    Node[] nodes = new Node[20];

    /** List of 5 elements */
    Graph list5;
    /** List of 12 elements */
    Graph list12;
    /** List cell */
    Graph cell;

    /** List transformations */
    GraphGrammar circularListGrammar4;
    GraphGrammar listGrammar4;
    GraphGrammar listGrammar5;
    GraphGrammar listGrammar10;
    GraphGrammar binaryTreeGrammar;

    Map<String,GraphPattern> binaryTreePatternsMap; // keys are pR, pA0, pA1,
    // pA2, pB, pC, pD, pL
    PatternFamily binaryTreePF = new PatternFamily(1, 10);

    @SuppressWarnings("unqualified-field-access")
    @Override
    protected void setUp() {
        // Initialise the list graphs and the cell graph
        for (int i = 0; i < this.nodes.length; i++) {
            this.nodes[i] = DefaultNode.createNode(i);
        }

        this.list5 = this.type.clone();
        for (int i = 0; i < 4; i++) {
            this.list5.addEdge(DefaultEdge.createEdge(this.nodes[i], "n",
                this.nodes[i + 1]));
        }
        this.list12 = this.type.clone();
        for (int i = 0; i < 11; i++) {
            this.list12.addEdge(DefaultEdge.createEdge(this.nodes[i], "n",
                this.nodes[i + 1]));
        }

        this.cell = this.type.clone();
        this.cell.addNode(this.nodes[12]);

        try {
            this.listGrammar4 =
                Groove.loadGrammar(PATH_PREFIX + "list4.gps", "start").toGrammar();
        } catch (FormatException e1) {
            e1.printStackTrace();
            System.exit(1);
        } catch (IOException e1) {
            e1.printStackTrace();
            System.exit(1);
        }

        try {
            this.listGrammar10 =
                Groove.loadGrammar(PATH_PREFIX + "list10.gps", "start").toGrammar();
        } catch (FormatException e1) {
            e1.printStackTrace();
            System.exit(1);
        } catch (IOException e1) {
            e1.printStackTrace();
            System.exit(1);
        }

        try {
            this.listGrammar5 =
                Groove.loadGrammar(PATH_PREFIX + "list5.gps", "start").toGrammar();
        } catch (FormatException e1) {
            e1.printStackTrace();
            System.exit(1);
        } catch (IOException e1) {
            e1.printStackTrace();
            System.exit(1);
        }

        try {
            this.circularListGrammar4 =
                Groove.loadGrammar(PATH_PREFIX + "circularlist4.gps", "start").toGrammar();
        } catch (FormatException e1) {
            e1.printStackTrace();
            System.exit(1);
        } catch (IOException e1) {
            e1.printStackTrace();
            System.exit(1);
        }
        try {
            this.binaryTreeGrammar =
                Groove.loadGrammar(PATH_PREFIX + "generate-binary-tree.gps",
                    "start").toGrammar();
        } catch (FormatException e1) {
            e1.printStackTrace();
            System.exit(1);
        } catch (IOException e1) {
            e1.printStackTrace();
            System.exit(1);
        }

        try {
            initBinaryTreePatterns();
        } catch (ExceptionIncompatibleWithMaxIncidence e) {
            System.err.println("Should not happen");
            e.printStackTrace();
        }
    }

    /** */
    public void _test() {
        testConstructionConcrPart();
        testSetMaterialisations1();
        testSetMaterialisations2();
        testSetMaterialisations3();
        testSetMaterialisations4();
        testTransformCircularList();
    }

    /** */
    @SuppressWarnings("unqualified-field-access")
    public void testConstructionConcrPart() throws AssertionError {
        PatternFamily pf = new PatternFamily(1, 10);

        DefaultAbstrGraph s = null;
        try {
            s = DefaultAbstrGraph.factory(pf, 1).getShapeGraphFor(this.list5);
        } catch (ExceptionIncompatibleWithMaxIncidence e) {
            e.printStackTrace();
        }

        // Compute a morphism from cell into the middle node of the shape
        Node middle = null;
        for (Node n : s.nodeSet()) {
            if (s.edgeSet(n).size() > 1) {
                middle = n;
                break;
            }
        }
        // should not happen
        assertTrue(middle != null);

        Morphism morph = new DefaultMorphism(this.cell, s);
        Node cellNode = this.cell.nodeSet().iterator().next();
        morph.putNode(cellNode, middle);
        morph = Util.getTotalExtension(morph);

        // Constuct concrete parts
        Collection<ConcretePart> ext =
            ConcretePart.extensions(this.cell, new TypingImpl(s, morph), pf,
                false, null);
        assertEquals(1, ext.size());
    }

    /**
     * Test with - list with 4 elements - rule adding an object - matching to
     * the middle shape node - precision 2 - radius 1
     */
    @SuppressWarnings("unqualified-field-access")
    public void testSetMaterialisations1() throws AssertionError {
        PatternFamily pf = new PatternFamily(1, 10);
        DefaultAbstrGraph s = null;
        try {
            s =
                DefaultAbstrGraph.factory(pf, 2).getShapeGraphFor(
                    this.listGrammar4.getStartGraph());
        } catch (ExceptionIncompatibleWithMaxIncidence e) {
            e.printStackTrace();
        }

        // Compute a morphism from cell into the middle node of the shape
        Node middle = null;
        for (Node n : s.nodeSet()) {
            if (s.edgeSet(n).size() > 2) {
                middle = n;
                break;
            }
        }
        // should not happen
        assertTrue(middle != null);

        SPORule rule = (SPORule) this.listGrammar4.getRule("add");
        Morphism morph = new DefaultMorphism(rule.lhs(), s);
        Node cellNode = rule.lhs().nodeSet().iterator().next();
        morph.putNode(cellNode, middle);
        morph = Util.getTotalExtension(morph);

        // Constuct concrete parts
        SystemRecord syst = new SystemRecord(this.listGrammar4, true);
        ConcretePart.Typing typing = new TypingImpl(s, morph);
        Collection<ConcretePart> ext =
            ConcretePart.extensions(rule.lhs(), typing, pf, false, syst);
        // there is only one extension
        ConcretePart cp = ext.iterator().next();
        Abstraction.Parameters options = new Abstraction.Parameters(1, 2, 10);
        SetMaterialisations smat =
            new SetMaterialisations(cp, s, morph.elementMap(), options);

        // remap the initial mapping into the concrete part
        NodeEdgeMap match = new NodeEdgeHashMap();
        for (Node n : morph.nodeMap().keySet()) {
            match.putNode(n, n);
        }
        for (Edge e : morph.edgeMap().keySet()) {
            match.putEdge(e, e);
        }

        RuleEvent event =
            new SPOEvent(rule, new VarNodeEdgeHashMap(match), syst, false);
        RuleApplication appl = new DefaultApplication(event, cp.graph());
        Collection<AbstrGraph> result = smat.transform(appl, syst);
        assertEquals(2, result.size());
    }

    /**
     * Test with - list with 4 elements - rule adding an object - matching to
     * the middle shape node - precision 1 - radius 1
     */
    @SuppressWarnings("unqualified-field-access")
    public void testSetMaterialisations2() throws AssertionError {
        PatternFamily pf = new PatternFamily(1, 10);
        DefaultAbstrGraph s = null;
        try {
            s =
                DefaultAbstrGraph.factory(pf, 1).getShapeGraphFor(
                    this.listGrammar4.getStartGraph());
        } catch (ExceptionIncompatibleWithMaxIncidence e) {
            e.printStackTrace();
        }

        // Compute a morphism from cell into the middle node of the shape
        Node middle = null;
        for (Node n : s.nodeSet()) {
            if (s.edgeSet(n).size() > 2) {
                middle = n;
                break;
            }
        }
        // should not happen
        assertTrue(middle != null);

        SPORule rule = (SPORule) this.listGrammar4.getRule("add");
        Morphism morph = new DefaultMorphism(rule.lhs(), s);
        Node cellNode = rule.lhs().nodeSet().iterator().next();
        morph.putNode(cellNode, middle);
        morph = Util.getTotalExtension(morph);

        // Constuct concrete parts
        SystemRecord syst = new SystemRecord(this.listGrammar4, true);
        ConcretePart.Typing typing = new TypingImpl(s, morph);
        Collection<ConcretePart> ext =
            ConcretePart.extensions(rule.lhs(), typing, pf, false, syst);
        // there is only one extension
        ConcretePart cp = ext.iterator().next();
        Abstraction.Parameters options = new Abstraction.Parameters(1, 1, 10);
        SetMaterialisations smat =
            new SetMaterialisations(cp, s, morph.elementMap(), options);

        // remap the initial mapping into the concrete part
        NodeEdgeMap match = new NodeEdgeHashMap();
        for (Node n : morph.nodeMap().keySet()) {
            match.putNode(n, n);
        }
        for (Edge e : morph.edgeMap().keySet()) {
            match.putEdge(e, e);
        }

        RuleEvent event =
            new SPOEvent(rule, new VarNodeEdgeHashMap(match), syst, false);
        RuleApplication appl = new DefaultApplication(event, cp.graph());
        Collection<AbstrGraph> result = smat.transform(appl, syst);

        String fileNameBase = "../tests/out3/graph";
        int i = 1;
        for (AbstrGraph ag : result) {
            // System.out.println(ag + "\n");
            String fileName = fileNameBase + i++;
            try {
                (new DefaultGxl()).marshalGraph(ag, new File(fileName));
            } catch (IOException e1) {
                System.err.println("Unable to write file " + fileName);
                e1.printStackTrace();
            }
        }
        assertEquals(10, result.size());
    }

    /**
     * Test with - list with 10 elements - rule adding an object - matching to
     * the middle shape node - precision 1 - radius 2
     */
    @SuppressWarnings("unqualified-field-access")
    public void testSetMaterialisations3() throws AssertionError {
        PatternFamily pf = new PatternFamily(2, 10);
        DefaultAbstrGraph s = null;
        try {
            s =
                DefaultAbstrGraph.factory(pf, 1).getShapeGraphFor(
                    this.listGrammar10.getStartGraph());
        } catch (ExceptionIncompatibleWithMaxIncidence e) {
            e.printStackTrace();
        }

        // Compute a morphism from cell into the middle node of the shape
        Node middle = null;
        for (Node n : s.nodeSet()) {
            if (s.edgeSet(n).size() > 3) {
                middle = n;
                break;
            }
        }
        // should not happen
        assertTrue(middle != null);

        SPORule rule = (SPORule) this.listGrammar10.getRule("add");
        Morphism morph = new DefaultMorphism(rule.lhs(), s);
        Node cellNode = rule.lhs().nodeSet().iterator().next();
        morph.putNode(cellNode, middle);
        morph = Util.getTotalExtension(morph);

        // Constuct concrete parts
        SystemRecord syst = new SystemRecord(this.listGrammar10, true);
        ConcretePart.Typing typing = new TypingImpl(s, morph);
        Collection<ConcretePart> ext =
            ConcretePart.extensions(rule.lhs(), typing, pf, false, syst);
        // there is only one extension
        ConcretePart cp = ext.iterator().next();
        Abstraction.Parameters options = new Abstraction.Parameters(2, 1, 10);
        SetMaterialisations smat =
            new SetMaterialisations(cp, s, morph.elementMap(), options);

        // remap the initial mapping into the concrete part
        NodeEdgeMap match = new NodeEdgeHashMap();
        for (Node n : morph.nodeMap().keySet()) {
            match.putNode(n, n);
        }
        for (Edge e : morph.edgeMap().keySet()) {
            match.putEdge(e, e);
        }

        RuleEvent event =
            new SPOEvent(rule, new VarNodeEdgeHashMap(match), syst, false);
        RuleApplication appl = new DefaultApplication(event, cp.graph());
        Collection<AbstrGraph> result = smat.transform(appl, syst);

        String fileNameBase = "../tests/out4/graph";
        int i = 1;
        for (AbstrGraph ag : result) {
            // System.out.println(ag + "\n");
            String fileName = fileNameBase + i++;
            try {
                (new DefaultGxl()).marshalGraph(ag, new File(fileName));
            } catch (IOException e1) {
                System.err.println("Unable to write file " + fileName);
                e1.printStackTrace();
            }
        }
        assertEquals(17, result.size());
    }

    /**
     * Tests all possible materialisations, for all possible matchings - list
     * with 4 elements - precision 2 - radius 1
     */
    @SuppressWarnings("unqualified-field-access")
    public void testSetMaterialisations4() throws AssertionError {
        PatternFamily pf = new PatternFamily(1, 10);
        DefaultAbstrGraph s = null;
        try {
            s =
                DefaultAbstrGraph.factory(pf, 2).getShapeGraphFor(
                    this.listGrammar4.getStartGraph());
        } catch (ExceptionIncompatibleWithMaxIncidence e) {
            e.printStackTrace();
        }

        SPORule rule = (SPORule) this.listGrammar4.getRule("add");

        Collection<AbstrGraph> all = new ArrayList<AbstrGraph>();

        for (VarNodeEdgeMap match : Util.getMatchesIter(rule.lhs(), s,
            new NodeEdgeHashMap())) {
            if (!s.isInjectiveMap(match)) {
                continue;
            }

            SystemRecord syst = new SystemRecord(this.listGrammar4, true);
            ConcretePart.Typing typing = new TypingImpl(s, match);
            Collection<ConcretePart> ext =
                ConcretePart.extensions(rule.lhs(), typing, pf, false, syst);
            for (ConcretePart cp : ext) {
                Abstraction.Parameters options =
                    new Abstraction.Parameters(1, 2, 10);
                SetMaterialisations smat =
                    new SetMaterialisations(cp, s, match, options);
                RuleEvent event =
                    new SPOEvent(rule, smat.updateMatch(match), syst, false);
                RuleApplication appl =
                    new DefaultApplication(event, cp.graph());
                Collection<AbstrGraph> result = smat.transform(appl, syst);
                all.addAll(result);
            }
        }
        assertEquals(4, all.size());
    }

    /**
     * Test with - list with 4 elements - rule adding an object - matching to
     * the middle shape node - precision 1 - radius 1 - low links precision
     */
    @SuppressWarnings("unqualified-field-access")
    public void testSetMaterialisations5() throws AssertionError {
        PatternFamily pf = new PatternFamily(1, 10);
        DefaultAbstrGraph s = null;
        try {
            s =
                DefaultAbstrGraph.factory(pf, 1).getShapeGraphFor(
                    this.listGrammar4.getStartGraph());
        } catch (ExceptionIncompatibleWithMaxIncidence e) {
            e.printStackTrace();
        }

        // Compute a morphism from cell into the middle node of the shape
        Node middle = null;
        for (Node n : s.nodeSet()) {
            if (s.edgeSet(n).size() > 2) {
                middle = n;
                break;
            }
        }
        // should not happen
        assertTrue(middle != null);

        SPORule rule = (SPORule) this.listGrammar4.getRule("add");
        Morphism morph = new DefaultMorphism(rule.lhs(), s);
        Node cellNode = rule.lhs().nodeSet().iterator().next();
        morph.putNode(cellNode, middle);
        morph = Util.getTotalExtension(morph);

        // Constuct concrete parts
        SystemRecord syst = new SystemRecord(this.listGrammar4, true);
        ConcretePart.Typing typing = new TypingImpl(s, morph);
        Collection<ConcretePart> ext =
            ConcretePart.extensions(rule.lhs(), typing, pf, false, syst);
        // there is only one extension
        ConcretePart cp = ext.iterator().next();
        Abstraction.Parameters options =
            new Abstraction.Parameters(true, Abstraction.LinkPrecision.LOW, 1,
                2, 10);
        SetMaterialisations smat =
            new SetMaterialisations(cp, s, morph.elementMap(), options);

        // remap the initial mapping into the concrete part
        NodeEdgeMap match = new NodeEdgeHashMap();
        for (Node n : morph.nodeMap().keySet()) {
            match.putNode(n, n);
        }
        for (Edge e : morph.edgeMap().keySet()) {
            match.putEdge(e, e);
        }

        RuleEvent event =
            new SPOEvent(rule, new VarNodeEdgeHashMap(match), syst, false);
        RuleApplication appl = new DefaultApplication(event, cp.graph());
        Collection<AbstrGraph> result = smat.transform(appl, syst);

        String fileNameBase = "../tests/out3/graph";
        int i = 1;
        for (AbstrGraph ag : result) {
            // System.out.println(ag + "\n");
            String fileName = fileNameBase + i++;
            try {
                (new DefaultGxl()).marshalGraph(ag, new File(fileName));
            } catch (IOException e1) {
                System.err.println("Unable to write file " + fileName);
                e1.printStackTrace();
            }
        }
        assertEquals(4, result.size());
    }

    /** */
    @SuppressWarnings("unqualified-field-access")
    public void testTransformCircularList() throws AssertionError {
        // common variables
        Abstraction.Parameters options = new Abstraction.Parameters(1, 1, 10);
        SystemRecord syst = new SystemRecord(this.circularListGrammar4, true);
        SPORule rule = (SPORule) this.listGrammar4.getRule("add");
        PatternFamily pf = new PatternFamily(options.radius, 10);

        // The first abstract graph
        DefaultAbstrGraph s = null;

        // The second abstract graph
        AbstrGraph s2 = null;

        // Compute the first abstract graph
        {
            try {
                s =
                    DefaultAbstrGraph.factory(pf, options.precision).getShapeGraphFor(
                        this.circularListGrammar4.getStartGraph());
            } catch (ExceptionIncompatibleWithMaxIncidence e) {
                e.printStackTrace();
            }
        }

        // Compute the unique derivation, which initialises s2
        {
            VarNodeEdgeMap match =
                Util.getMatchesIter(rule.lhs(), s, new NodeEdgeHashMap()).iterator().next();
            ConcretePart.Typing typing = new TypingImpl(s, match);
            ConcretePart cp =
                ConcretePart.extensions(rule.lhs(), typing, pf, false, syst).iterator().next();
            SetMaterialisations smat =
                new SetMaterialisations(cp, s, match, options);
            RuleEvent event =
                new SPOEvent(rule, smat.updateMatch(match), syst, false);
            RuleApplication appl = new DefaultApplication(event, cp.graph());
            s2 = smat.transform(appl, syst).iterator().next();
            assertEquals(5, s2.nodeCount());
        }

        // Compute a matching into the abstract node
        VarNodeEdgeMap match2 = null;

        {
            NodeEdgeMap map = new NodeEdgeHashMap();

            Node abstrNode = null;
            for (Node n : s2.nodeSet()) {
                if (Abstraction.MULTIPLICITY.containsOmega(s2.multiplicityOf(n))) {
                    abstrNode = n;
                }
            }
            assertNotNull(abstrNode);
            map.putNode(rule.lhs().nodeSet().iterator().next(), abstrNode);
            match2 = new VarNodeEdgeHashMap(map);
        }

        // Construct the second set of materialisations, and the rule
        // application
        SetMaterialisations smat2 = null;
        RuleApplication appl2 = null;

        {
            TypingImpl typing2 = new TypingImpl((DefaultAbstrGraph) s2, match2);
            ConcretePart cp2 =
                ConcretePart.extensions(rule.lhs(), typing2, pf, false, syst).iterator().next();
            smat2 =
                new SetMaterialisations(cp2, (DefaultAbstrGraph) s2, match2,
                    options);
            RuleEvent event2 =
                new SPOEvent(rule, smat2.updateMatch(match2), syst, false);
            appl2 = new DefaultApplication(event2, cp2.graph());
        }

        // Transform
        Collection<AbstrGraph> result = smat2.transform(appl2, syst);

        // for (AbstrGraph ag : result) {
        // System.out.println(ag + "\n");
        // }
        //		
        // int size = result.size();
        // Set<AbstrGraph> resultSet = new HashSet<AbstrGraph>(result);
        //		
        // System.out.println("Before: " + size + ", After: " +
        // resultSet.size());

    }

    public void testTransformBinaryTree1()
        throws ExceptionIncompatibleWithMaxIncidence {

        int precision = 1;
        int radius = this.binaryTreePF.getRadius();

        // Construct the problematic graph
        DefaultAbstrGraph.AbstrGraphCreator creator =
            DefaultAbstrGraph.getAbstrGraphCreatorInstance();
        {
            creator.init(this.binaryTreePF, precision);
            MultiplicityInformation one =
                Abstraction.MULTIPLICITY.getElement(1, 1);
            MultiplicityInformation omega =
                Abstraction.MULTIPLICITY.getElement(2, 1);
            Label laba = DefaultLabel.createLabel("a");
            Label labm = DefaultLabel.createLabel("med");
            Label labl = DefaultLabel.createLabel("leaf");

            Node nA1 =
                creator.addNode(one, this.binaryTreePatternsMap.get("pA1"));
            Node nC =
                creator.addNode(omega, this.binaryTreePatternsMap.get("pC"));
            Node nB =
                creator.addNode(one, this.binaryTreePatternsMap.get("pB"));
            Node nL =
                creator.addNode(omega, this.binaryTreePatternsMap.get("pL"));
            creator.addEdge(nA1, labm, nA1);
            creator.addEdge(nC, labm, nC);
            creator.addEdge(nC, laba, nC);
            creator.addEdge(nB, labm, nB);
            creator.addEdge(nL, labl, nL);//
            creator.addEdge(nA1, laba, nC);
            creator.addEdge(nC, laba, nB);
            creator.addEdge(nB, laba, nL);
            creator.addEdge(nA1, laba, nL);
            creator.addEdge(nC, laba, nL);

            creator.setFixed();
        }
        DefaultAbstrGraph ag = creator.getConstructedGraph();

        // find the match and perform the transformation
        SPORule rule = (SPORule) this.binaryTreeGrammar.getRule("expand");
        SystemRecord syst = new SystemRecord(this.binaryTreeGrammar, true);

        Collection<AbstrGraph> all = new ArrayList<AbstrGraph>();
        VarNodeEdgeMap match =
            Util.getMatchesIter(rule.lhs(), ag, new NodeEdgeHashMap()).iterator().next(); // only
        // one
        // match
        ConcretePart.Typing typing = new TypingImpl(ag, match);
        Collection<ConcretePart> ext =
            ConcretePart.extensions(rule.lhs(), typing, this.binaryTreePF,
                false, syst);
        for (ConcretePart cp : ext) {
            Abstraction.Parameters options =
                new Abstraction.Parameters(true, precision, radius, 10);
            SetMaterialisations smat =
                new SetMaterialisations(cp, ag, match, options);
            RuleEvent event =
                new SPOEvent(rule, smat.updateMatch(match), syst, false);
            RuleApplication appl = new DefaultApplication(event, cp.graph());
            Collection<AbstrGraph> resultTransform = smat.transform(appl, syst);
            all.addAll(resultTransform);
        }

    }

    public void testTransformBinaryTree2()
        throws ExceptionIncompatibleWithMaxIncidence {

        int precision = 1;
        int radius = this.binaryTreePF.getRadius();

        DefaultAbstrGraph.AbstrGraphCreator creator =
            DefaultAbstrGraph.getAbstrGraphCreatorInstance();
        {
            creator.init(this.binaryTreePF, 1);
            MultiplicityInformation one =
                Abstraction.MULTIPLICITY.getElement(1, 1);
            MultiplicityInformation omega =
                Abstraction.MULTIPLICITY.getElement(2, 1);
            Label laba = DefaultLabel.createLabel("a");
            Label labm = DefaultLabel.createLabel("med");
            Label labl = DefaultLabel.createLabel("leaf");

            Node nA2 =
                creator.addNode(one, this.binaryTreePatternsMap.get("pA2"));
            Node nB =
                creator.addNode(one, this.binaryTreePatternsMap.get("pB"));
            Node nL =
                creator.addNode(omega, this.binaryTreePatternsMap.get("pL"));
            creator.addEdge(nA2, labm, nA2);
            creator.addEdge(nB, labm, nB);
            creator.addEdge(nL, labl, nL);//
            creator.addEdge(nA2, laba, nB);
            creator.addEdge(nB, laba, nL);

            creator.setFixed();
        }
        DefaultAbstrGraph ag = creator.getConstructedGraph();

        // find the match and perform the transformation
        SPORule rule = (SPORule) this.binaryTreeGrammar.getRule("expand");
        SystemRecord syst = new SystemRecord(this.binaryTreeGrammar, true);

        Collection<AbstrGraph> all = new ArrayList<AbstrGraph>();
        VarNodeEdgeMap match =
            Util.getMatchesIter(rule.lhs(), ag, new NodeEdgeHashMap()).iterator().next(); // only
        // one
        // match
        ConcretePart.Typing typing = new TypingImpl(ag, match);
        Collection<ConcretePart> ext =
            ConcretePart.extensions(rule.lhs(), typing, this.binaryTreePF,
                false, syst);
        for (ConcretePart cp : ext) {
            Abstraction.Parameters options =
                new Abstraction.Parameters(true, precision, radius, 10);
            SetMaterialisations smat =
                new SetMaterialisations(cp, ag, match, options);
            RuleEvent event =
                new SPOEvent(rule, smat.updateMatch(match), syst, false);
            RuleApplication appl = new DefaultApplication(event, cp.graph());
            Collection<AbstrGraph> resultTransform = smat.transform(appl, syst);
            all.addAll(resultTransform);
        }

        for (AbstrGraph g : all) {
            System.out.println(g);
        }

    }

    @SuppressWarnings("unqualified-field-access")
    private void initBinaryTreePatterns()
        throws ExceptionIncompatibleWithMaxIncidence {
        if (this.binaryTreePatternsMap != null) {
            return;
        }
        this.binaryTreePatternsMap = new HashMap<String,GraphPattern>();

        Graph g;
        Label l_a = DefaultLabel.createLabel("a");
        Label l_med = DefaultLabel.createLabel("med");
        Label l_leaf = DefaultLabel.createLabel("leaf");

        // one leaf "pR"
        g = new DefaultGraph();
        g.addNode(this.nodes[0]);
        g.addEdge(this.nodes[0], l_leaf, this.nodes[0]);
        this.binaryTreePatternsMap.put("pR",
            this.binaryTreePF.computeAddPattern(g, this.nodes[0]));

        // root with two leafs "pA0"
        g = new DefaultGraph();
        for (int i = 0; i < 3; i++) {
            g.addNode(this.nodes[i]);
        }
        g.addEdge(DefaultEdge.createEdge(this.nodes[0], l_med, this.nodes[0]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[1], l_leaf, this.nodes[1]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[2], l_leaf, this.nodes[2]));//
        g.addEdge(DefaultEdge.createEdge(this.nodes[0], l_a, this.nodes[1]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[0], l_a, this.nodes[2]));
        this.binaryTreePatternsMap.put("pA0",
            this.binaryTreePF.computeAddPattern(g, this.nodes[0]));

        // root with one leaf and one med "pA1"
        g = new DefaultGraph();
        for (int i = 0; i < 3; i++) {
            g.addNode(this.nodes[i]);
        }
        g.addEdge(DefaultEdge.createEdge(this.nodes[0], l_med, this.nodes[0]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[1], l_leaf, this.nodes[1]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[2], l_med, this.nodes[2]));//
        g.addEdge(DefaultEdge.createEdge(this.nodes[0], l_a, this.nodes[1]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[0], l_a, this.nodes[2]));
        this.binaryTreePatternsMap.put("pA1",
            this.binaryTreePF.computeAddPattern(g, this.nodes[0]));

        // root with two med "pA2"
        g = new DefaultGraph();
        for (int i = 0; i < 3; i++) {
            g.addNode(this.nodes[i]);
        }
        g.addEdge(DefaultEdge.createEdge(this.nodes[0], l_med, this.nodes[0]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[1], l_med, this.nodes[1]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[2], l_med, this.nodes[2]));//
        g.addEdge(DefaultEdge.createEdge(this.nodes[0], l_a, this.nodes[1]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[0], l_a, this.nodes[2]));
        this.binaryTreePatternsMap.put("pA2",
            this.binaryTreePF.computeAddPattern(g, this.nodes[0]));

        // med with two leafs "pB"
        g = new DefaultGraph();
        for (int i = 0; i < 4; i++) {
            g.addNode(this.nodes[i]);
        }
        g.addEdge(DefaultEdge.createEdge(this.nodes[0], l_med, this.nodes[0]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[1], l_leaf, this.nodes[1]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[2], l_leaf, this.nodes[2]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[3], l_med, this.nodes[3]));//
        g.addEdge(DefaultEdge.createEdge(this.nodes[0], l_a, this.nodes[1]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[0], l_a, this.nodes[2]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[3], l_a, this.nodes[0]));
        this.binaryTreePatternsMap.put("pB",
            this.binaryTreePF.computeAddPattern(g, this.nodes[0]));

        // med with one leaf and one med "pC"
        g = new DefaultGraph();
        for (int i = 0; i < 4; i++) {
            g.addNode(this.nodes[i]);
        }
        g.addEdge(DefaultEdge.createEdge(this.nodes[0], l_med, this.nodes[0]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[1], l_leaf, this.nodes[1]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[2], l_med, this.nodes[2]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[3], l_med, this.nodes[3]));//
        g.addEdge(DefaultEdge.createEdge(this.nodes[0], l_a, this.nodes[1]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[0], l_a, this.nodes[2]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[3], l_a, this.nodes[0]));
        this.binaryTreePatternsMap.put("pC",
            this.binaryTreePF.computeAddPattern(g, this.nodes[0]));

        // med with two med "pD"
        g = new DefaultGraph();
        for (int i = 0; i < 4; i++) {
            g.addNode(this.nodes[i]);
        }
        g.addEdge(DefaultEdge.createEdge(this.nodes[0], l_med, this.nodes[0]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[1], l_med, this.nodes[1]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[2], l_med, this.nodes[2]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[3], l_med, this.nodes[3]));//
        g.addEdge(DefaultEdge.createEdge(this.nodes[0], l_a, this.nodes[1]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[0], l_a, this.nodes[2]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[3], l_a, this.nodes[0]));
        this.binaryTreePatternsMap.put("pD",
            this.binaryTreePF.computeAddPattern(g, this.nodes[0]));

        // leaf not root "pL"
        g = new DefaultGraph();
        for (int i = 0; i < 2; i++) {
            g.addNode(this.nodes[i]);
        }
        g.addEdge(DefaultEdge.createEdge(this.nodes[0], l_leaf, this.nodes[0]));
        g.addEdge(DefaultEdge.createEdge(this.nodes[1], l_med, this.nodes[1]));//
        g.addEdge(DefaultEdge.createEdge(this.nodes[1], l_a, this.nodes[0]));
        this.binaryTreePatternsMap.put("pL",
            this.binaryTreePF.computeAddPattern(g, this.nodes[0]));
    }

    public static void main(String[] args) {

        TestingTransforming test = new TestingTransforming();
        test.setUp();
        test.testSetMaterialisations5();

        /*
         * try { test.testTransformBinaryTree2(); } catch
         * (ExceptionIncompatibleWithMaxIncidence e) { e.printStackTrace(); }
         */

    }

    private class TypingImpl implements ConcretePart.Typing {
        DefaultAbstrGraph sg;
        NodeEdgeMap m;

        TypingImpl(DefaultAbstrGraph sg, NodeEdgeMap m) {
            this.sg = sg;
            this.m = m;
        }

        public GraphPattern typeOf(Node n) {
            return this.sg.typeOf(this.m.getNode(n));
        }
    }

}
