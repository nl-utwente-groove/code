package groove.io.conceptual.graph;

import groove.graph.GraphRole;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectLabel;
import groove.view.aspect.AspectParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Edge class for wrapper around AspectGraph. Unidirectional and attaches itself to both source and target nodes.
 * @author s0141844
 *
 */
public class AbsEdge {
    String m_name;
    AbsNode m_source, m_target;
    
    List<AspectEdge> m_aspectEdges = new ArrayList<AspectEdge>();
    
    public AbsEdge(AbsNode source, AbsNode target, String name) {
        if (target == null)
            throw new NullPointerException();
        m_source = source;
        m_target = target;
        m_name = name;
        
        source.addEdge(this);
        target.addTargetEdge(this);
    }

    public AbsNode getSource() {
        return m_source;
    }

    public AbsNode getTarget() {
        return m_target;
    }
    
    public String getName() {
        return m_name;
    }
    
    public void setName(String name) {
        m_name = name;
    }
    
    @Override
    public String toString() {
        return m_name;
    }
    
    public void buildAspect(GraphRole role) {
        if (m_aspectEdges.size() != 0)
            return;

        m_source.buildAspect(role);
        m_target.buildAspect(role);
        
        String[] labels = m_name.split("\n");
        for (String sublabel : labels) {
            AspectLabel alabel = AspectParser.getInstance().parse(sublabel, role);
            if (alabel.isEdgeOnly()) {
                AspectEdge newEdge = new AspectEdge(m_source.getAspect(), alabel, m_target.getAspect()); 
                m_aspectEdges.add(newEdge);
            } else {
                // error
            }
        }
    }
    
    public List<AspectEdge> getAspect() {
        return m_aspectEdges;
    }

}
