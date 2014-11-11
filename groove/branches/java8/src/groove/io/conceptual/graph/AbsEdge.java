package groove.io.conceptual.graph;

import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectLabel;
import groove.grammar.aspect.AspectNode;
import groove.grammar.aspect.AspectParser;
import groove.graph.GraphRole;
import groove.util.Groove;

import java.util.List;
import java.util.stream.Collectors;

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

    /** Computes and returns the
     * aspect edges constructed from this edge.
     */
    public List<AspectEdge> getAspect(GraphRole role) {
        if (this.m_aspectEdges == null) {
            this.m_aspectEdges = computeAspect(role);
        }
        return this.m_aspectEdges;
    }

    /**
     * Constructs a set of {@link AspectEdge} from this edge.
     * Each sublabel in the newline-separated label of this edge
     * is transformed to an {@link AspectLabel}, from which an edge is constructed.
     * @param role graph role for which the edge is to be created
     */
    public List<AspectEdge> computeAspect(GraphRole role) {
        this.m_source.buildAspect(role);
        AspectNode source = this.m_source.getAspect();
        this.m_target.buildAspect(role);
        AspectNode target = this.m_target.getAspect();
        return Groove.splitLines(this.m_name)
            .stream()
            .map(lab -> AspectParser.getInstance().parse(lab, role))
            .filter(al -> !al.isNodeOnly())
            .map(al -> new AspectEdge(source, al, target))
            .collect(Collectors.toList());
    }

    private List<AspectEdge> m_aspectEdges;
}
