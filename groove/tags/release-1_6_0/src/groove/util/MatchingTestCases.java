package groove.util;

import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeSetEdgeSetGraph;
import groove.view.FormatException;


/**
 * @author iGniSz
 */
public class MatchingTestCases {
    /**
     * This returns the following graph
     *
     * 0->1
     * 0->2
     *
     * @return
     */
    public static NodeSetEdgeSetGraph SimpleTestCase1A() {
        NodeSetEdgeSetGraph nesg = new NodeSetEdgeSetGraph();

        Label label = null;
        try {
            label = DefaultLabel.parseLabel("a");

            Node zero = nesg.addNode();
            Node one = nesg.addNode();
            Node two = nesg.addNode();

            nesg.addEdge(zero, label, one);
            nesg.addEdge(zero, label, two);
            
        } catch (FormatException e) {
            e.printStackTrace();
        }

        return nesg;
    }

    /**
     * This returns the following graph
     *
     * 0->1
     * 0->2
     * 2->3
     * 3->1
     *
     * @return
     */
    public static NodeSetEdgeSetGraph SimpleTestCase1B() {
        NodeSetEdgeSetGraph nesg = new NodeSetEdgeSetGraph();

        try {
            Label label = DefaultLabel.parseLabel("a");

            Node zero = nesg.addNode();
            Node one = nesg.addNode();
            Node two = nesg.addNode();
            Node three = nesg.addNode();            

            nesg.addEdge(zero, label, one);
            nesg.addEdge(zero, label, two);
            nesg.addEdge(two, label, three);
            nesg.addEdge(three, label, one);

        } catch (FormatException e) {
            e.printStackTrace();
        }

        return nesg;
    }

    /** This returns the following graph
     *
     * 0->1
     */
    public static NodeSetEdgeSetGraph SimpleTestCase2A() {
        NodeSetEdgeSetGraph nesg = new NodeSetEdgeSetGraph();

        Label label = null;
        try {
            label = DefaultLabel.parseLabel("a");

            Node zero = nesg.addNode();
            Node one = nesg.addNode();

            nesg.addEdge(zero, label, one);

        } catch (FormatException e) {
            e.printStackTrace();
        }

        return nesg;
    }

    /** This returns the following graph
     *
     * 0->1
     * 2
     */
    public static NodeSetEdgeSetGraph SimpleTestCase3A() {
        NodeSetEdgeSetGraph nesg = new NodeSetEdgeSetGraph();

        Label label = null;
        try {
            label = DefaultLabel.parseLabel("a");

            Node zero = nesg.addNode();
            Node one = nesg.addNode();
            Node two = nesg.addNode();

            nesg.addEdge(zero, label, one);
            nesg.addNode(two);

        } catch (FormatException e) {
            e.printStackTrace();
        }

        return nesg;
    }

    /** This returns the following graph
     *
     * 0->1
     * 1->2
     */
    public static NodeSetEdgeSetGraph SimpleTestCase4A() {
        NodeSetEdgeSetGraph nesg = new NodeSetEdgeSetGraph();

        Label label = null;
        try {
            label = DefaultLabel.parseLabel("a");

            Node zero = nesg.addNode();
            Node one = nesg.addNode();
            Node two = nesg.addNode();

            nesg.addEdge(zero, label, one);
            nesg.addEdge(one, label, two);            

        } catch (FormatException e) {
            e.printStackTrace();
        }

        return nesg;
    }

    /** This returns the following graph
     *
     * 0->1
     * 2
     * 3
     */
    public static NodeSetEdgeSetGraph SimpleTestCase5A() {
        NodeSetEdgeSetGraph nesg = new NodeSetEdgeSetGraph();

        Label label = null;
        try {
            label = DefaultLabel.parseLabel("a");

            Node zero = nesg.addNode();
            Node one = nesg.addNode();
            Node two = nesg.addNode();
            Node three = nesg.addNode();

            nesg.addEdge(zero, label, one);

        } catch (FormatException e) {
            e.printStackTrace();
        }

        return nesg;
    }
}
