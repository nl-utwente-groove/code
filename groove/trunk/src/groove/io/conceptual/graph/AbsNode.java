package groove.io.conceptual.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectLabel;
import groove.grammar.aspect.AspectNode;
import groove.grammar.aspect.AspectParser;
import groove.graph.GraphRole;

/**
 * Node representation for wrapper around AspectGraph. Keeps track of incoming and outgoing edges (references set by AbsEdge).
 * @author Harold Bruijntjes
 *
 */
@SuppressWarnings("javadoc")
public class AbsNode {
    private String[] m_names;

    private List<AbsEdge> m_edges = new ArrayList<>();
    private List<AbsEdge> m_targetEdges = new ArrayList<>();

    private AspectNode m_aspectNode;
    private List<AspectEdge> m_aspectEdges = new ArrayList<>();

    private AbsGraph m_parent = null;
    private int m_id = 0;

    /**
     * Create node with given names
     * @param names Names of the node
     */
    public AbsNode(String... names) {
        this.m_names = names;
    }

    /**
     * Add outgoing edge
     * @param e Edge to add
     */
    public void addEdge(AbsEdge e) {
        this.m_edges.add(e);
    }

    /**
     * Add incoming edge
     * @param e Edge to add
     */
    public void addTargetEdge(AbsEdge e) {
        this.m_targetEdges.add(e);
    }

    /**
     * Return List of outgoing edges
     * @return List of outgoing edges
     */
    public List<AbsEdge> getEdges() {
        return this.m_edges;
    }

    /**
     * Return List of incoming edges
     * @return List of incoming edges
     */
    public List<AbsEdge> getTargetEdges() {
        return this.m_targetEdges;
    }

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
     * @param g Graph to add node to
     * @param id Id of the node within the graph
     */
    public void addToGraph(AbsGraph g, int id) {
        if (this.m_parent != null && this.m_parent != g) {
            throw new IllegalArgumentException("AbsNode already element of a graph");
        }

        this.m_parent = g;
        this.m_id = id;
    }

    /**
     * Get graph node belongs to
     * @return Graph node belongs to
     */
    public AbsGraph getParent() {
        return this.m_parent;
    }

    /**
     * Id of the node in the parent graph if any
     * @return Id of the node
     */
    public int getId() {
        return this.m_id;
    }

    public void buildAspect(GraphRole role) {
        if (this.m_parent == null) {
            throw new IllegalArgumentException("Node not part of graph");
        }

        if (this.m_aspectNode != null) {
            return;
        }

        String[] labels = this.m_names;
        this.m_aspectNode = new AspectNode(this.m_id, role);

        for (String sublabel : labels) {
            AspectLabel alabel = AspectParser.getInstance()
                .parse(sublabel, role);
            // add self edge
            if (alabel.isEdgeOnly()) {
                AspectEdge newEdge = new AspectEdge(this.m_aspectNode, alabel, this.m_aspectNode);
                this.m_aspectEdges.add(newEdge);
            } else {
                this.m_aspectNode.setAspects(alabel);
            }
        }
    }

    public AspectNode getAspect() {
        return this.m_aspectNode;
    }

    public List<AspectEdge> getAspectEdges() {
        return this.m_aspectEdges;
    }
}
