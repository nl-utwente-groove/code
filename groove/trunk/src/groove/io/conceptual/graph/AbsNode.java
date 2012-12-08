package groove.io.conceptual.graph;

import groove.graph.GraphRole;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectLabel;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Node representation for wrapper around AspectGraph. Keeps track of incoming and outgoing edges (references set by AbsEdge).
 * @author s0141844
 * 
 */
public class AbsNode {
    private String[] m_names;

    private List<AbsEdge> m_edges = new ArrayList<AbsEdge>();
    private List<AbsEdge> m_targetEdges = new ArrayList<AbsEdge>();

    private AspectNode m_aspectNode;
    private List<AspectEdge> m_aspectEdges = new ArrayList<AspectEdge>();

    private AbsGraph m_parent = null;
    private int m_id = 0;

    /**
     * Create node with given names
     * @param names Names of the node
     */
    public AbsNode(String... names) {
        m_names = names;
    }

    /**
     * Add outgoing edge
     * @param e Edge to add
     */
    public void addEdge(AbsEdge e) {
        m_edges.add(e);
    }

    /**
     * Add incoming edge
     * @param e Edge to add
     */
    public void addTargetEdge(AbsEdge e) {
        m_targetEdges.add(e);
    }

    /**
     * Return List of outgoing edges
     * @return List of outgoing edges
     */
    public List<AbsEdge> getEdges() {
        return m_edges;
    }

    /**
     * Return List of incoming edges
     * @return List of incoming edges
     */
    public List<AbsEdge> getTargetEdges() {
        return m_targetEdges;
    }

    @Override
    public String toString() {
        return Arrays.toString(m_names);
    }

    /**
     * Get names of the node
     * @return Names of the node
     */
    public String[] getNames() {
        return m_names;
    }

    /**
     * Add a new name to the node after all other names
     * @param name Name to add
     */
    public void addName(String name) {
        String newNames[] = new String[m_names.length + 1];
        System.arraycopy(m_names, 0, newNames, 0, m_names.length);
        newNames[m_names.length] = name;
        m_names = newNames;
    }

    /**
     * Add Node to graph with given Id. Node must not belong to any other graph
     * @param g Graph to add node to
     * @param id Id of the node within the graph
     */
    public void addToGraph(AbsGraph g, int id) {
        if (m_parent != null && m_parent != g) {
            throw new IllegalArgumentException("AbsNode already element of a graph");
        }

        m_parent = g;
        m_id = id;
    }

    /**
     * Get graph node belongs to
     * @return Graph node belongs to
     */
    public AbsGraph getParent() {
        return m_parent;
    }

    /**
     * Id of the node in the parent graph if any
     * @return Id of the node
     */
    public int getId() {
        return m_id;
    }

    public void buildAspect(GraphRole role) {
        if (m_parent == null) {
            throw new IllegalArgumentException("Node not part of graph");
        }

        if (m_aspectNode != null) {
            return;
        }

        String[] labels = m_names;
        m_aspectNode = new AspectNode(m_id, role);

        for (String sublabel : labels) {
            AspectLabel alabel = AspectParser.getInstance().parse(sublabel, role);
            // add self edge
            if (alabel.isEdgeOnly()) {
                AspectEdge newEdge = new AspectEdge(m_aspectNode, alabel, m_aspectNode);
                m_aspectEdges.add(newEdge);
            } else {
                m_aspectNode.setAspects(alabel);
            }
        }
    }

    public AspectNode getAspect() {
        return m_aspectNode;
    }

    public List<AspectEdge> getAspectEdges() {
        return m_aspectEdges;
    }
}
