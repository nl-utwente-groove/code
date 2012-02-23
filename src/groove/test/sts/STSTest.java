package groove.test.sts;

import groove.algebra.JavaIntAlgebra;
import groove.explore.util.MatchSetCollector;
import groove.lts.GTS;
import groove.lts.MatchResult;
import groove.lts.StartGraphState;
import groove.sts.Location;
import groove.sts.STS;
import groove.trans.DefaultHostGraph;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.SystemRecord;
import groove.util.Groove;
import groove.view.FormatException;
import groove.view.GrammarModel;

import java.io.IOException;
import java.util.Collection;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Superclass for STS tests. Do not run, run CompleteSTSTest or OnTheFlySTSTest.
 * @author Vincent de Bruijn
 * @version $Revision $
 */
@SuppressWarnings("all")
public abstract class STSTest extends TestCase {

    /** Location of the samples. */
    static protected final String INPUT_DIR = "junit/rules";

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
        this.g1 = new DefaultHostGraph("g1");
        this.g2 = new DefaultHostGraph("g2");
        this.g3 = new DefaultHostGraph("g2");

        this.n1[0] = this.g1.getFactory().createNode();
        this.n1[1] = this.g1.getFactory().createNode();
        this.n1[2] =
            this.g1.getFactory().createValueNode(JavaIntAlgebra.instance, 0);

        this.e1[0] =
            this.g1.getFactory().createEdge(this.n1[0], "a", this.n1[1]);
        this.e1[1] =
            this.g1.getFactory().createEdge(this.n1[0], "x", this.n1[2]);

        this.n2[0] = this.g2.getFactory().createNode();
        this.n2[1] = this.g2.getFactory().createNode();
        this.n2[2] =
            this.g2.getFactory().createValueNode(JavaIntAlgebra.instance, 1);

        this.e2[0] =
            this.g2.getFactory().createEdge(this.n2[0], "a", this.n2[1]);
        this.e2[1] =
            this.g2.getFactory().createEdge(this.n2[0], "x", this.n2[2]);

        this.n3[0] = this.g3.getFactory().createNode();
        this.n3[1] = this.g3.getFactory().createNode();
        this.n3[2] =
            this.g3.getFactory().createValueNode(JavaIntAlgebra.instance, 0);

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
            this.g1.addEdge(this.e1[i]);
            this.g2.addEdge(this.e2[i]);
            this.g3.addEdge(this.e3[i]);
        }
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
     * Tests ruleMatchToSwitchRelation for simple guards.
     */
    public void testSimpleGuards() {
        test("simpleGuards");
    }

    /**
     * Tests ruleMatchToSwitchRelation for the rule matches in the given grammar.
     * @param grammarName The name of the grammar to test on.
     */
    protected void test(String grammarName) {
        try {
            GrammarModel view =
                Groove.loadGrammar(INPUT_DIR + "/" + grammarName);
            HostGraph graph = view.getStartGraphModel().toHost();
            for (MatchResult next : createMatchSet(view)) {
                testRuleMatchToSwitchRelation(graph, next);
            }
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (FormatException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Tests ruleMatchToSwitchRelation.
     */
    protected abstract void testRuleMatchToSwitchRelation(
            HostGraph sourceGraph, MatchResult match);

    /** 
     * Gets the first matchset for the given grammar for rule to switchrelation tests 
     */
    protected Collection<MatchResult> createMatchSet(GrammarModel view) {
        try {
            HostGraph graph = view.getStartGraphModel().toHost();
            GTS gts =
                new GTS(view.getStartGraphModel().getGrammar().toGrammar());
            SystemRecord record = new SystemRecord(gts);
            StartGraphState state = new StartGraphState(record, graph);
            return new MatchSetCollector(state, record, gts.checkDiamonds()).getMatchSet();
        } catch (FormatException e) {
            Assert.fail(e.getMessage());
        }
        return null;
    }
}
