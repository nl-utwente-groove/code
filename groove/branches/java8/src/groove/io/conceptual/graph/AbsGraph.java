package groove.io.conceptual.graph;

import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.AspectNode;
import groove.graph.GraphRole;

import java.util.HashSet;
import java.util.Set;

/**
 * Thin layer between {@link AspectGraph} and the conceptual model.
 * Keeps track of nodes by means of references.
 * @author Harold Bruijntjes
 *
 */
public class AbsGraph {
    /** Returns the set of nodes in this graph. */
    public Set<AbsNode> getNodes() {
        return this.m_nodes;
    }

    private final Set<AbsNode> m_nodes = new HashSet<AbsNode>();

    /** Adds a given node to this graph. */
    public void addNode(AbsNode node) {
        if (this.m_nodes.add(node)) {
            node.setId(this.m_nodes.size() + 1);

            for (AbsEdge e : node.getEdges()) {
                addNode(e.getTarget());
            }

            for (AbsEdge e : node.getTargetEdges()) {
                addNode(e.getSource());
            }
        }
    }

    /** Returns an aspect graph constructed from this graph. */
    public AspectGraph toAspectGraph(String name, GraphRole role) {
        AspectGraph result = new AspectGraph(name, role);
        for (AbsNode n : getNodes()) {
            n.buildAspect(role);
            AspectNode an = n.getAspect();
            result.addNode(an);
            for (AspectEdge ae : n.getAspectEdges()) {
                result.addEdge(ae);
            }
        }
        // now build the edges
        getNodes().stream()
            .flatMap(n -> n.getEdges().stream())
            .flatMap(e -> e.getAspect(role).stream())
            .forEach(ae -> result.addEdge(ae));
        result.setFixed();
        return result;
    }
}
