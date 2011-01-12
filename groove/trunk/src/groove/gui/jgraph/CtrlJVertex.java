package groove.gui.jgraph;

import groove.control.CtrlState;
import groove.control.CtrlVar;
import groove.graph.Node;

import java.util.List;

import org.jgraph.graph.AttributeMap;

/**
 * JVertex class that describes the underlying node as a graph state.
 * @author Tom Staijen
 * @version $Revision $
 */
public class CtrlJVertex extends GraphJVertex {
    /**
     * Creates a new instance for a given node (required to be a
     * {@link CtrlState}) in an LTS model.
     */
    CtrlJVertex(CtrlJGraph jGraph, CtrlState node) {
        super(jGraph, node, false);
    }

    @Override
    public CtrlJGraph getJGraph() {
        return (CtrlJGraph) super.getJGraph();
    }

    @Override
    public GraphJVertex newJVertex(Node node) {
        return new CtrlJVertex(getJGraph(), (CtrlState) node);
    }

    @Override
    public CtrlState getNode() {
        return (CtrlState) super.getNode();
    }

    /**
     * Appends the bound variables to the lines, if this list is not empty
     */
    @Override
    public java.util.List<StringBuilder> getLines() {
        List<StringBuilder> result = super.getLines();
        List<CtrlVar> boundVars = getNode().getBoundVars();
        if (boundVars.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(boundVars.toString());
            result.add(sb);
        }
        return result;
    }

    /** Indicates if this jVertex represents the start state of the control automaton. */
    public boolean isStart() {
        return getNode().getAut().getStart().equals(getNode());
    }

    /** Indicates if this jVertex represents the start state of the control automaton. */
    public boolean isFinal() {
        return getNode().getAut().getFinal().equals(getNode());
    }

    /**
     * This implementation adds special attributes for the start state, open
     * states, final states, and the active state.
     * @see JAttr#LTS_NODE_ATTR
     * @see JAttr#LTS_START_NODE_ATTR
     * @see JAttr#LTS_OPEN_NODE_ATTR
     * @see JAttr#LTS_FINAL_NODE_ATTR
     * @see JAttr#LTS_NODE_ACTIVE_CHANGE
     */
    @Override
    protected AttributeMap createAttributes() {
        AttributeMap result;
        if (isStart()) {
            result = JAttr.CONTROL_START_NODE_ATTR.clone();
        } else if (isFinal()) {
            result = JAttr.CONTROL_SUCCESS_NODE_ATTR.clone();
        } else {
            result = JAttr.CONTROL_NODE_ATTR.clone();
        }

        return result;
    }

    /** Returns a prototype {@link CtrlJVertex} for a given {@link CtrlJGraph}. */
    public static CtrlJVertex getPrototype(CtrlJGraph jGraph) {
        return new CtrlJVertex(jGraph, null);
    }
}