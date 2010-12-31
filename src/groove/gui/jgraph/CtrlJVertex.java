package groove.gui.jgraph;

import groove.control.CtrlState;
import groove.control.CtrlTransition;
import groove.control.CtrlVar;

import java.util.List;

import org.jgraph.graph.AttributeMap;

/**
 * JVertex class that describes the underlying node as a graph state.
 * @author Tom Staijen
 * @version $Revision $
 */
public class CtrlJVertex extends GraphJVertex<CtrlState,CtrlTransition> {
    /**
     * Creates a new instance for a given node (required to be a
     * {@link CtrlState}) in an LTS model.
     */
    CtrlJVertex(CtrlJModel jModel, CtrlState node) {
        super(jModel, node, false);
    }

    @Override
    public CtrlJModel getJModel() {
        return (CtrlJModel) super.getJModel();
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
        return getJModel().getGraph().getStart().equals(getNode());
    }

    /** Indicates if this jVertex represents the start state of the control automaton. */
    public boolean isFinal() {
        return getJModel().getGraph().getFinal().equals(getNode());
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
}