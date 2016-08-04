package groove.io.conceptual.graph;

import java.util.ArrayList;
import java.util.List;

import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectLabel;
import groove.grammar.aspect.AspectParser;
import groove.graph.GraphRole;

/**
 * Edge class for wrapper around AspectGraph. Unidirectional and attaches itself to both source and target nodes.
 * @author Harold Bruijntjes
 *
 */
@SuppressWarnings("javadoc")
public class AbsEdge {
    String m_name;
    AbsNode m_source, m_target;

    List<AspectEdge> m_aspectEdges = new ArrayList<>();

    public AbsEdge(AbsNode source, AbsNode target, String name) {
        if (target == null) {
            throw new NullPointerException();
        }
        this.m_source = source;
        this.m_target = target;
        this.m_name = name;

        source.addEdge(this);
        target.addTargetEdge(this);
    }

    public AbsNode getSource() {
        return this.m_source;
    }

    public AbsNode getTarget() {
        return this.m_target;
    }

    public String getName() {
        return this.m_name;
    }

    public void setName(String name) {
        this.m_name = name;
    }

    @Override
    public String toString() {
        return this.m_name;
    }

    public void buildAspect(GraphRole role) {
        if (this.m_aspectEdges.size() != 0) {
            return;
        }

        this.m_source.buildAspect(role);
        this.m_target.buildAspect(role);

        String[] labels = this.m_name.split("\n");
        for (String sublabel : labels) {
            AspectLabel alabel = AspectParser.getInstance()
                .parse(sublabel, role);
            if (alabel.isEdgeOnly()) {
                AspectEdge newEdge =
                    new AspectEdge(this.m_source.getAspect(), alabel, this.m_target.getAspect());
                this.m_aspectEdges.add(newEdge);
            } else {
                // error
            }
        }
    }

    public List<AspectEdge> getAspect() {
        return this.m_aspectEdges;
    }

}
