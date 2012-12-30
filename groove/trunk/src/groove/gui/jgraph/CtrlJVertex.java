package groove.gui.jgraph;

import groove.control.CtrlAut;
import groove.control.CtrlState;
import groove.gui.look.Look;

/**
 * JVertex class that describes the underlying node as a graph state.
 * @author Tom Staijen
 * @version $Revision $
 */
public class CtrlJVertex extends
        AJVertex<CtrlAut,CtrlJGraph,JModel<CtrlAut>,CtrlJEdge> {
    /**
     * Creates a new instance.
     * Call {@link #setJModel} and {@link #setNode(groove.graph.Node)}
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
    public CtrlState getNode() {
        return (CtrlState) super.getNode();
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
     * Call {@link #setJModel} and {@link #setNode(groove.graph.Node)} to initialise. 
     */
    public static CtrlJVertex newInstance() {
        return new CtrlJVertex();
    }
}