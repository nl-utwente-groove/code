package groove.io.conceptual.graph;

import java.util.HashSet;
import java.util.Set;

import groove.grammar.QualName;
import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.AspectNode;
import groove.graph.GraphRole;

/**
 * Thin layer between {@link AspectGraph} and the conceptual model.
 * Keeps track of edges and nodes by means of references.
 * @author Harold Bruijntjes
 */
public class AbsGraph {
    private final Set<AbsNode> m_nodes = new HashSet<>();
    private final Set<AbsEdge> m_edges = new HashSet<>();
    private final QualName name;
    private final GraphRole role;
    private AspectGraph m_aGraph = null;

    /** Constructs an empty graph. */
    public AbsGraph(QualName name, GraphRole role) {
        this.name = name.toValidName();
        this.role = role;
    }

    /** Returns the set of nodes in this graph. */
    public Set<AbsNode> getNodes() {
        return this.m_nodes;
    }

    /** Returns the set of edges in this graph. */
    public Set<AbsEdge> getEdges() {
        return this.m_edges;
    }

    /** Adds a node to this graph. */
    public void addNode(AbsNode node) {
        if (node.getParent() != null && node.getParent() != this) {
            throw new IllegalArgumentException("Node already added to some other graph!");
        }
        if (this.m_nodes.contains(node)) {
            return;
        }

        this.m_nodes.add(node);
        node.addToGraph(this, this.m_nodes.size() + 1);

        for (AbsEdge e : node.getEdges()) {
            addEdge(e);
            addNode(e.getTarget());
        }

        for (AbsEdge e : node.getTargetEdges()) {
            addNode(e.getSource());
        }
        this.m_aGraph = null;
    }

    /** Adds an edge to this graph. */
    public void addEdge(AbsEdge edge) {
        if (!this.m_edges.contains(edge)) {
            this.m_edges.add(edge);
            this.m_aGraph = null;
        }
    }

    /** Empties this graph. */
    public void clear() {
        this.m_nodes.clear();
        this.m_edges.clear();
        this.m_aGraph = null;
    }

    /** Turns this graph into an {@link AspectGraph} with a given name. */
    public AspectGraph toAspectGraph() {
        if (this.m_aGraph != null) {
            return this.m_aGraph;
        }

        AspectGraph ag = new AspectGraph(this.name.toString(), this.role);

        for (AbsNode n : this.m_nodes) {
            n.buildAspect(this.role);
            AspectNode an = n.getAspect();
            ag.addNode(an);
            for (AspectEdge ae : n.getAspectEdges()) {
                ag.addEdge(ae);
            }
        }

        for (AbsEdge e : this.m_edges) {
            e.buildAspect(this.role);
            for (AspectEdge ae : e.getAspect()) {
                ag.addEdge(ae);
            }
        }

        ag.setFixed();

        this.m_aGraph = ag;

        return ag;
    }
}
