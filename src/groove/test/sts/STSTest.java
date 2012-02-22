package groove.test.sts;

import groove.algebra.JavaIntAlgebra;
import groove.sts.Location;
import groove.sts.STS;
import groove.trans.DefaultHostGraph;
import groove.trans.HostEdge;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import junit.framework.TestCase;

import org.junit.Test;

@SuppressWarnings("all")
public class STSTest extends TestCase {

    protected HostGraph g1;
    protected HostGraph g2;
    protected HostGraph g3;
    protected HostNode[] n1 = new HostNode[3];
    protected HostEdge[] e1 = new HostEdge[2];
    protected HostNode[] n2 = new HostNode[3];
    protected HostEdge[] e2 = new HostEdge[2];
    protected HostNode[] n3 = new HostNode[3];
    protected HostEdge[] e3 = new HostEdge[2];

    /**
     * Creates a new instance of this test.
     * @param name The name of this test
     */
    public STSTest(String name) {
        super(name);
    }

    /** 
     * Sets up all Object needed for the tests
     */
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
        STS s = new STS();
        Location l1 = s.hostGraphToLocation(this.g1);
        Location l2 = s.hostGraphToLocation(this.g2);
        Location l3 = s.hostGraphToLocation(this.g3);
        assert l1.equals(l2);
        assert !l1.equals(l3);
    }

}
