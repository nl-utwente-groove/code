package groove.test.sts;

import groove.algebra.PointIntAlgebra;
import groove.grammar.host.DefaultHostGraph;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.model.GrammarModel;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.MatchResult;
import groove.sts.Gate;
import groove.sts.Location;
import groove.sts.LocationVariable;
import groove.sts.STS;
import groove.sts.STSException;
import groove.sts.SwitchRelation;
import groove.util.Groove;
import groove.util.parse.FormatException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Superclass for STS tests. Do not run, run CompleteSTSTest or OnTheFlySTSTest.
 * @author Vincent de Bruijn
 * @version $Revision $
 */
@SuppressWarnings("all")
public class STSTest extends TestCase {

    /** Location of the samples. */
    static protected final String INPUT_DIR = "junit/rules/sts";

    protected HostGraph g1;
    protected HostGraph g2;
    protected HostGraph g3;
    protected HostNode[] n1 = new HostNode[3];
    protected HostEdge[] e1 = new HostEdge[2];
    protected HostNode[] n2 = new HostNode[3];
    protected HostEdge[] e2 = new HostEdge[2];
    protected HostNode[] n3 = new HostNode[3];
    protected HostEdge[] e3 = new HostEdge[2];

    protected STS sts;

    /**
     * Constructor.
     * @param name The name of this test
     */
    public STSTest(String name) {
        super(name);
    }

    /**
     * Sets up all Object needed for the tests
     */
    @Override
    protected void setUp() {
        this.sts = new STS();

        this.g1 = new DefaultHostGraph("g1");
        this.g2 = new DefaultHostGraph("g2");
        this.g3 = new DefaultHostGraph("g2");

        this.n1[0] = this.g1.getFactory().createNode();
        this.n1[1] = this.g1.getFactory().createNode();
        this.n1[2] =
            this.g1.getFactory().createNode(PointIntAlgebra.instance, 0);

        this.e1[0] =
            this.g1.getFactory().createEdge(this.n1[0], "a", this.n1[1]);
        this.e1[1] =
            this.g1.getFactory().createEdge(this.n1[0], "x", this.n1[2]);

        this.n2[0] = this.g2.getFactory().createNode();
        this.n2[1] = this.g2.getFactory().createNode();
        this.n2[2] =
            this.g2.getFactory().createNode(PointIntAlgebra.instance, 0);

        this.e2[0] =
            this.g2.getFactory().createEdge(this.n2[0], "a", this.n2[1]);
        this.e2[1] =
            this.g2.getFactory().createEdge(this.n2[0], "x", this.n2[2]);

        this.n3[0] = this.g3.getFactory().createNode();
        this.n3[1] = this.g3.getFactory().createNode();
        this.n3[2] =
            this.g3.getFactory().createNode(PointIntAlgebra.instance, 0);

        this.e3[0] =
            this.g3.getFactory().createEdge(this.n3[0], "a", this.n3[1]);
        this.e3[1] =
            this.g3.getFactory().createEdge(this.n3[0], "y", this.n3[2]);

        for (int i = 0; i < 3; i++) {
            this.g1.addNode(this.n1[i]);
            this.g2.addNode(this.n2[i]);
            this.g3.addNode(this.n3[i]);
        }

        for (int i = 0; i < 2; i++) {
            this.g1.addEdgeContext(this.e1[i]);
            this.g2.addEdgeContext(this.e2[i]);
            this.g3.addEdgeContext(this.e3[i]);
        }
    }

    /**
     * Tests hostGraphToStartLocation.
     */
    public void testHostGraphToStartLocation() {
        Location l = this.sts.hostGraphToStartLocation(this.g2);
        Assert.assertSame(this.sts.getCurrentLocation(), l);
        Assert.assertSame(this.sts.getStartLocation(), l);

        LocationVariable v = this.sts.getLocationVariable(this.e2[1]);
        Assert.assertNotNull(v);
    }

    /**
     * Tests STSException.
     */
    public void testSTSException() {
        try {
            GrammarModel view =
                Groove.loadGrammar(INPUT_DIR + "/" + "exception");
            GTS gts =
                new GTS(view.getStartGraphModel().getGrammar().toGrammar());
            GraphState state = gts.startState();
            this.sts.hostGraphToStartLocation(state.getGraph());
            for (MatchResult next : state.getMatches()) {
                try {
                    this.sts.ruleMatchToSwitchRelation(state.getGraph(), next,
                        new HashSet<SwitchRelation>());
                    Assert.fail("No STSException thrown.");
                } catch (STSException e) {
                    Assert.assertFalse(e.getMessage().isEmpty());
                }
            }
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (FormatException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Tests the 'final' node in a model.
     */
    public void testFinalNode() {
        test("testCase");
    }

    /** 
     * Tests if the host graphs are correctly generalized to a location.
     */
    public void testHostGraphToLocation() {
        Location l1 = this.sts.hostGraphToLocation(this.g1);
        Location l2 = this.sts.hostGraphToLocation(this.g2);
        Location l3 = this.sts.hostGraphToLocation(this.g3);
        Assert.assertTrue(l1.equals(l2));
        Assert.assertFalse(l1.equals(l3));
    }

    /**
     * Tests toLocation.
     */
    public void testToLocation() {
        Location l = this.sts.hostGraphToLocation(this.g1);
        this.sts.toLocation(l);
        Assert.assertEquals(this.sts.getCurrentLocation(), l);
    }

    /**
     * Tests ruleMatchToSwitchRelation for guards.
     */
    public void testGuards() {
        test("guards");
    }

    /**
     * Tests ruleMatchToSwitchRelation for updates.
     */
    public void testUpdates() {
        test("updates");
    }

    /**
     * Tests ruleMatchToSwitchRelation for the rule matches in the given grammar.
     * @param grammarName The name of the grammar to test on.
     */
    private void test(String grammarName) {
        try {
            GrammarModel view =
                Groove.loadGrammar(INPUT_DIR + "/" + grammarName);
            GTS gts =
                new GTS(view.getStartGraphModel().getGrammar().toGrammar());
            GraphState state = gts.startState();
            for (MatchResult next : state.getMatches()) {
                testRuleMatchToSwitchRelation(state.getGraph(), next);
            }
            toJsonTest();
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (FormatException e) {
            Assert.fail(e.getMessage());
        }
    }

    private void toJsonTest() {
        String json = this.sts.toJSON();
        // TODO: Test if json is well-formed
    }

    private void testRuleMatchToSwitchRelation(HostGraph sourceGraph,
            MatchResult match) {
        this.sts.hostGraphToStartLocation(sourceGraph);
        try {
            SwitchRelation sr =
                this.sts.ruleMatchToSwitchRelation(sourceGraph, match,
                    new HashSet<SwitchRelation>());

            Assert.assertNotNull(sr);
            Assert.assertEquals(
                this.sts.getSwitchRelation(SwitchRelation.getSwitchIdentifier(
                    sr.getGate(), sr.getGuard(), sr.getUpdate())), sr);

            // Test with higher priority match
            this.sts.removeSwitchRelation(sr);
            SwitchRelation higherPriorityRelation =
                new SwitchRelation(new Gate("gate", new HashSet()), "x > 3", "");
            Set<SwitchRelation> higherPriorityRelations =
                new HashSet<SwitchRelation>();
            higherPriorityRelations.add(higherPriorityRelation);
            sr =
                this.sts.ruleMatchToSwitchRelation(sourceGraph, match,
                    higherPriorityRelations);
            Assert.assertTrue(sr.getGuard().contains("!(x > 3)"));
        } catch (STSException e) {
            Assert.fail(e.getMessage());
        }
    }
}
