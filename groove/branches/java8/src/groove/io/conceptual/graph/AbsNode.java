package groove.io.conceptual.graph;

import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectLabel;
import groove.grammar.aspect.AspectNode;
import groove.grammar.aspect.AspectParser;
import groove.graph.GraphRole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Node representation for wrapper around AspectGraph.
 * Keeps track of incoming and outgoing edges (references set by AbsEdge).
 * @author Harold Bruijntjes
 */
public class AbsNode implements AbsNodeIter {
    /**
     * Create node with given names
     * @param names Names of the node
     */
    public AbsNode(String... names) {
        this.m_names = names;
    }

    private String[] m_names;

    /**
     * Add outgoing edge
     * @param e Edge to add
     */
    public void addEdge(AbsEdge e) {
        this.m_edges.add(e);
    }

    /**
     * Return List of outgoing edges
     * @return List of outgoing edges
     */
    public List<AbsEdge> getEdges() {
        return this.m_edges;
    }

    private final List<AbsEdge> m_edges = new ArrayList<AbsEdge>();

    /**
     * Add incoming edge
     * @param e Edge to add
     */
    public void addTargetEdge(AbsEdge e) {
        this.m_targetEdges.add(e);
    }

    /**
     * Return List of incoming edges
     * @return List of incoming edges
     */
    public List<AbsEdge> getTargetEdges() {
        return this.m_targetEdges;
    }

    private final List<AbsEdge> m_targetEdges = new ArrayList<AbsEdge>();

    @Override
    public String toString() {
        return Arrays.toString(this.m_names);
    }

    /**
     * Get names of the node
     * @return Names of the node
     */
    public String[] getNames() {
        return this.m_names;
    }

    /**
     * Add a new name to the node after all other names
     * @param name Name to add
     */
    public void addName(String name) {
        String newNames[] = new String[this.m_names.length + 1];
        System.arraycopy(this.m_names, 0, newNames, 0, this.m_names.length);
        newNames[this.m_names.length] = name;
        this.m_names = newNames;
    }

    /**
     * Add Node to graph with given Id. Node must not belong to any other graph
     * @param id Id of the node within the graph
     */
    public void setId(int id) {
        if (this.m_inGraph) {
            throw new IllegalArgumentException("AbsNode already element of a graph");
        }

        this.m_inGraph = true;
        this.m_id = id;
    }

    /** Flag indicating if the node has been inserted into a graph. */
    private boolean m_inGraph;
    private int m_id = 0;

    /** Creates an aspect node and self-edges from the information in this node.
     * These can be retrieved after this call returns using {@link #getAspect()}
     * and {@link #getAspectEdges()}.
     */
    public void buildAspect(GraphRole role) {
        if (!this.m_inGraph) {
            throw new IllegalArgumentException("Node not part of graph");
        }

        if (this.m_aspectNode != null) {
            return;
        }

        this.m_aspectNode = new AspectNode(this.m_id, role);
        for (String sublabel : this.m_names) {
            AspectLabel alabel = AspectParser.getInstance().parse(sublabel, role);
            // add self edge
            if (alabel.isEdgeOnly()) {
                AspectEdge newEdge = new AspectEdge(this.m_aspectNode, alabel, this.m_aspectNode);
                this.m_aspectEdges.add(newEdge);
            } else {
                this.m_aspectNode.setAspects(alabel);
            }
        }
    }

    /** After a call to {@link #buildAspect(GraphRole)}, retrieves
     * the aspect node constructed from this node.
     */
    public AspectNode getAspect() {
        return this.m_aspectNode;
    }

    private AspectNode m_aspectNode;

    /** After a call to {@link #buildAspect(GraphRole)}, retrieves
     * the aspect self-edges constructed from this node.
     */
    public List<AspectEdge> getAspectEdges() {
        return this.m_aspectEdges;
    }

    private final List<AspectEdge> m_aspectEdges = new ArrayList<AspectEdge>();

    @Override
    public Iterator<AbsNode> iterator() {
        return Collections.singleton(this).iterator();
    }
}
