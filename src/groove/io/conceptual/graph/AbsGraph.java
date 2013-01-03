package groove.io.conceptual.graph;

import groove.graph.GraphRole;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;

import java.util.HashSet;
import java.util.Set;

/**
 * Thin layer between AspectGraph and the conceptual model. Keeps track of edges and nodes by means of references.
 * @author Harold Bruijntjes
 * 
 */
public class AbsGraph {
    private Set<AbsNode> m_nodes = new HashSet<AbsNode>();
    private Set<AbsEdge> m_edges = new HashSet<AbsEdge>();
    private AspectGraph m_aGraph = null;

    public AbsGraph() {

    }

    public Set<AbsNode> getNodes() {
        return this.m_nodes;
    }

    public Set<AbsEdge> getEdges() {
        return this.m_edges;
    }

    public void addNode(AbsNode node) {
        if (node.getParent() != null && node.getParent() != this) {
            throw new IllegalArgumentException("Node already added to graph!");
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
    }

    public void addEdge(AbsEdge edge) {
        if (!this.m_edges.contains(edge)) {
            this.m_edges.add(edge);
        }
    }

    public void clear() {
        this.m_nodes.clear();
        this.m_edges.clear();
        this.m_aGraph = null;
    }

    public void buildFromNode(AbsNode node) {
        addNode(node);
    }

    public AspectGraph toAspectGraph(String name, GraphRole role) {
        if (this.m_aGraph != null) {
            return this.m_aGraph;
        }

        AspectGraph ag = new AspectGraph(name, role);

        for (AbsNode n : this.m_nodes) {
            n.buildAspect(role);
            AspectNode an = n.getAspect();
            ag.addNode(an);
            for (AspectEdge ae : n.getAspectEdges()) {
                ag.addEdge(ae);
            }
        }

        for (AbsEdge e : this.m_edges) {
            e.buildAspect(role);
            for (AspectEdge ae : e.getAspect()) {
                ag.addEdge(ae);
            }
        }

        ag.setFixed();

        this.m_aGraph = ag;

        return ag;
    }
}
