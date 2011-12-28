package groove.gui.jgraph;

import groove.control.CtrlState;
import groove.control.CtrlVar;
import groove.graph.Node;
import groove.io.HTMLConverter;

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
    CtrlJVertex(CtrlJGraph jGraph, GraphJModel<?,?> jModel, CtrlState node) {
        super(jGraph, jModel, node);
    }

    @Override
    public CtrlJGraph getJGraph() {
        return (CtrlJGraph) super.getJGraph();
    }

    @Override
    public GraphJVertex newJVertex(GraphJModel<?,?> jModel, Node node) {
        return new CtrlJVertex(getJGraph(), jModel, (CtrlState) node);
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
        if (isTransient()) {
            StringBuilder action = new StringBuilder();
            action.append(HTMLConverter.toHtml('<'));
            action.append(getNode().getAction());
            action.append(HTMLConverter.toHtml('>'));
            result.add(action);
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

    /** Indicates if this jVertex corresponds to a transient control state. */
    public boolean isTransient() {
        return getNode().isTransient();
    }

    /**
     * This implementation adds special attributes for the start state, open
     * states, final states, and the active state.
     * @see LTSJGraph#LTS_NODE_ATTR
     * @see LTSJGraph#LTS_START_NODE_ATTR
     * @see LTSJGraph#LTS_OPEN_NODE_ATTR
     * @see LTSJGraph#LTS_FINAL_NODE_ATTR
     * @see LTSJGraph#LTS_NODE_ACTIVE_CHANGE
     */
    @Override
    protected AttributeMap createAttributes() {
        AttributeMap result;
        if (isStart()) {
            result = CtrlJGraph.CONTROL_START_NODE_ATTR.clone();
        } else if (isFinal()) {
            result = CtrlJGraph.CONTROL_SUCCESS_NODE_ATTR.clone();
        } else if (isTransient()) {
            result = CtrlJGraph.CONTROL_TRANSIENT_NODE_ATTR.clone();
        } else {
            result = CtrlJGraph.CONTROL_NODE_ATTR.clone();
        }

        return result;
    }

    /** Returns a prototype {@link CtrlJVertex} for a given {@link CtrlJGraph}. */
    public static CtrlJVertex getPrototype(CtrlJGraph jGraph) {
        return new CtrlJVertex(jGraph, null, null);
    }
}