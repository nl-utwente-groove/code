/*
 * $Id: EdgeAnchorFactory.java,v 1.3 2008-01-30 09:32:36 iovka Exp $
 */
package groove.trans;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Node;

/**
 * This implementation does not try to save space by minimizing the size of the
 * anchor, but tries to save time by selecting the entire non-reader part of the
 * LHS.
 * @author Arend Rensink
 * @version $Revision$
 */
public class EdgeAnchorFactory implements AnchorFactory {
    /**
     * Returns the singleton instance of this class.
     */
    static public AnchorFactory getInstance() {
        return instance;
    }

    /** The singleton instance of this class. */
    static private AnchorFactory instance = new EdgeAnchorFactory();

    // Commented this classvariable, seems unused (AREND)
    // private int attribute_0 = 0;

    /** Private empty constructor to make this a singleton class. */
    private EdgeAnchorFactory() {
        // empty constructor
    }

    /**
     * This implementation assumes that the rule is an <tt>SPORule</tt>.
     */
    public Element[] newAnchors(Rule generalRule) {
        SPORule rule = (SPORule) generalRule;
        Set<Element> anchors = new LinkedHashSet<Element>();
        // remember which eraser nodes should be part of the anchor
        Set<Node> eraserNodes =
            new LinkedHashSet<Node>(Arrays.asList(rule.getEraserNodes()));
        // the variable and eraser edges are most distinguishing; add them first
        for (Edge varEdge : rule.getSimpleVarEdges()) {
            anchors.add(varEdge);
            eraserNodes.removeAll(Arrays.asList(varEdge.ends()));
        }
        for (Edge eraserEdge : rule.getEraserEdges()) {
            anchors.add(eraserEdge);
            eraserNodes.removeAll(Arrays.asList(eraserEdge.ends()));
        }
        // add any eraser nodes that are not endpoints
        anchors.addAll(eraserNodes);
        Set<? extends Node> creatorNodes = rule.getCreatorGraph().nodeSet();
        for (Map.Entry<Node,Node> ruleMorphNodeEntry : rule.getMorphism().nodeMap().entrySet()) {
            if (creatorNodes.contains(ruleMorphNodeEntry.getValue())) {
                anchors.add(ruleMorphNodeEntry.getKey());
            }
        }
        anchors.addAll(rule.getMergeMap().keySet());
        return anchors.toArray(new Element[0]);
    }
}
