package groove.util;

import groove.graph.*;

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
    public static NodeEdgeSetGraph SimpleTestCase1A() {
        NodeEdgeSetGraph nesg = new NodeEdgeSetGraph();

        Label label = null;
        try {
            label = DefaultLabel.parseLabel("a");

            Node zero = nesg.addNode();
            Node one = nesg.addNode();
            Node two = nesg.addNode();

            nesg.addEdge(zero, label, one);
            nesg.addEdge(zero, label, two);
            
        } catch (GraphFormatException e) {
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
    public static NodeEdgeSetGraph SimpleTestCase1B() {
        NodeEdgeSetGraph nesg = new NodeEdgeSetGraph();

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

        } catch (GraphFormatException e) {
            e.printStackTrace();
        }

        return nesg;
    }

    /** This returns the following graph
     *
     * 0->1
     */
    public static NodeEdgeSetGraph SimpleTestCase2A() {
        NodeEdgeSetGraph nesg = new NodeEdgeSetGraph();

        Label label = null;
        try {
            label = DefaultLabel.parseLabel("a");

            Node zero = nesg.addNode();
            Node one = nesg.addNode();

            nesg.addEdge(zero, label, one);

        } catch (GraphFormatException e) {
            e.printStackTrace();
        }

        return nesg;
    }

    /** This returns the following graph
     *
     * 0->1
     * 2
     */
    public static NodeEdgeSetGraph SimpleTestCase3A() {
        NodeEdgeSetGraph nesg = new NodeEdgeSetGraph();

        Label label = null;
        try {
            label = DefaultLabel.parseLabel("a");

            Node zero = nesg.addNode();
            Node one = nesg.addNode();
            Node two = nesg.addNode();

            nesg.addEdge(zero, label, one);
            nesg.addNode(two);

        } catch (GraphFormatException e) {
            e.printStackTrace();
        }

        return nesg;
    }

    /** This returns the following graph
     *
     * 0->1
     * 1->2
     */
    public static NodeEdgeSetGraph SimpleTestCase4A() {
        NodeEdgeSetGraph nesg = new NodeEdgeSetGraph();

        Label label = null;
        try {
            label = DefaultLabel.parseLabel("a");

            Node zero = nesg.addNode();
            Node one = nesg.addNode();
            Node two = nesg.addNode();

            nesg.addEdge(zero, label, one);
            nesg.addEdge(one, label, two);            

        } catch (GraphFormatException e) {
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
    public static NodeEdgeSetGraph SimpleTestCase5A() {
        NodeEdgeSetGraph nesg = new NodeEdgeSetGraph();

        Label label = null;
        try {
            label = DefaultLabel.parseLabel("a");

            Node zero = nesg.addNode();
            Node one = nesg.addNode();
            Node two = nesg.addNode();
            Node three = nesg.addNode();

            nesg.addEdge(zero, label, one);

        } catch (GraphFormatException e) {
            e.printStackTrace();
        }

        return nesg;
    }
}
