package groove.gui.jgraph;

import groove.control.CtrlState;
import groove.control.CtrlVar;
import groove.gui.look.Look;
import groove.io.HTMLConverter;

import java.util.List;

/**
 * JVertex class that describes the underlying node as a graph state.
 * @author Tom Staijen
 * @version $Revision $
 */
public class CtrlJVertex extends GraphJVertex {
    /**
     * Creates a new instance.
     * Call {@link #setJModel(GraphJModel)} and {@link #setNode(groove.graph.Node)}
     * to initialise.
     */
    private CtrlJVertex() {
        // empty
    }

    @Override
    protected void initialise() {
        super.initialise();
        if (isFinal()) {
            setLook(Look.FINAL, true);
        }
    }

    @Override
    public CtrlJGraph getJGraph() {
        return (CtrlJGraph) super.getJGraph();
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
            action.append(getNode().getRecipe());
            action.append(HTMLConverter.toHtml('>'));
            result.add(action);
        }
        return result;
    }

    /** Indicates if this jVertex represents the start state of the control automaton. */
    public boolean isStart() {
        return getNode() != null
            && getNode().getAut().getStart().equals(getNode());
    }

    /** Indicates if this jVertex represents the start state of the control automaton. */
    public boolean isFinal() {
        return getNode() != null
            && getNode().getAut().getFinal().equals(getNode());
    }

    /** Indicates if this jVertex corresponds to a transient control state. */
    public boolean isTransient() {
        return getNode() != null && getNode().isTransient();
    }

    @Override
    protected Look getStructuralLook() {
        if (isStart()) {
            return Look.START;
        } else if (isTransient()) {
            return Look.CTRL_TRANSIENT_STATE;
        } else {
            return super.getStructuralLook();
        }
    }

    /** Returns a fresh, uninitialised instance.
     * Call {@link #setJModel(GraphJModel)} and {@link #setNode(groove.graph.Node)} to initialise. 
     */
    public static CtrlJVertex newInstance() {
        return new CtrlJVertex();
    }
}