package groove.io.conceptual.graph;

import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectLabel;
import groove.grammar.aspect.AspectParser;
import groove.graph.GraphRole;
import groove.util.Groove;

import java.util.ArrayList;
import java.util.List;

/**
 * Edge class for wrapper around AspectGraph.
 * Unidirectional and attaches itself to both source and target nodes.
 * @author Harold Bruijntjes
 */
public class AbsEdge {
    /** Constructs an edge with given source, target and label. */
    public AbsEdge(AbsNode source, AbsNode target, String name) {
        this.m_source = source;
        this.m_target = target;
        this.m_name = name;

        source.addEdge(this);
        target.addTargetEdge(this);
    }

    /** Returns the source node of this edge. */
    public AbsNode getSource() {
        return this.m_source;
    }

    private final AbsNode m_source;

    /** Returns the target node of this edge. */
    public AbsNode getTarget() {
        return this.m_target;
    }

    private final AbsNode m_target;

    /** Returns the label of this edge. */
    public String getName() {
        return this.m_name;
    }

    /** Sets a new label for this edge. */
    public void setName(String name) {
        this.m_name = name;
    }

    private String m_name;

    @Override
    public String toString() {
        return this.m_name;
    }

    /**
     * Constructs a set of {@link AspectEdge} from this edge.
     * Each sublabel in the newline-separated label of this edge
     * is transformed to an {@link AspectLabel}, from which an edge is constructed.
     * The aspect edge can be retrieved after this call returns by {@link #getAspect()}.
     * @param role graph role for which the edge is to be created
     */
    public void buildAspect(GraphRole role) {
        if (this.m_aspectEdges.size() != 0) {
            return;
        }

        this.m_source.buildAspect(role);
        this.m_target.buildAspect(role);

        for (String sublabel : Groove.splitLines(this.m_name)) {
            AspectLabel alabel = AspectParser.getInstance().parse(sublabel, role);
            if (alabel.isEdgeOnly()) {
                AspectEdge newEdge =
                    new AspectEdge(this.m_source.getAspect(), alabel, this.m_target.getAspect());
                this.m_aspectEdges.add(newEdge);
            } else {
                // error
            }
        }
    }

    /** After a call to {@link #buildAspect(GraphRole)}, returns the
     * aspect edges constructed from this edge.
     */
    public List<AspectEdge> getAspect() {
        return this.m_aspectEdges;
    }

    private final List<AspectEdge> m_aspectEdges = new ArrayList<AspectEdge>();
}
