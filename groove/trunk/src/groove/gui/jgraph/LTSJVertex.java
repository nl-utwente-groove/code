package groove.gui.jgraph;

import groove.control.instance.Frame;
import groove.graph.Node;
import groove.gui.look.Look;
import groove.gui.look.VisualKey;
import groove.io.HTMLConverter;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition.Claz;

/**
 * JVertex class that describes the underlying node as a graph state.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LTSJVertex extends AJVertex<GTS,LTSJGraph,LTSJModel,LTSJEdge> implements LTSJCell {
    /**
     * Creates a new, uninitialised instance.
     * Call {@link #setJModel(JModel)} and {@link #setNode(Node)} to initialise.
     */
    private LTSJVertex() {
        // empty
    }

    @Override
    public GraphState getNode() {
        return (GraphState) super.getNode();
    }

    @Override
    protected void initialise() {
        super.initialise();
        this.visibleFlag = true;
        GraphState state = getNode();
        if (state != null) {
            setLook(Look.OPEN, !state.isClosed());
            setLook(Look.ABSENT, state.isAbsent());
            setLook(Look.RECIPE, state.isInternalState());
            setLook(Look.TRANSIENT, state.isTransient());
            setLook(Look.FINAL, state.isFinal());
            setLook(Look.RESULT, state.isResult());
            setLook(Look.ERROR, state.isError());
        }
    }

    @Override
    public boolean setVisibleFlag(boolean visible) {
        boolean result = this.visibleFlag != visible;
        if (result) {
            this.visibleFlag = visible;
            setStale(VisualKey.VISIBLE);
        }
        return result;
    }

    @Override
    public boolean hasVisibleFlag() {
        return this.visibleFlag;
    }

    private boolean visibleFlag;

    /** Indicates that all outgoing transitions of this node are also visible. */
    public boolean isAllOutVisible() {
        return getNode().isDone()
                && getNode().getTransitions(Claz.ANY).size() == this.outVisibles + getEdges().size();
    }

    void changeOutVisible(boolean visible) {
        if (visible) {
            this.outVisibles++;
        } else {
            this.outVisibles--;
        }
    }

    private int outVisibles;

    @Override
    StringBuilder getNodeDescription() {
        StringBuilder result = new StringBuilder("State ");
        result.append(HTMLConverter.UNDERLINE_TAG.on(getNode()));
        Frame frame = getNode().getPrimeFrame();
        if (!frame.isStart()) {
            result.append(" with control state ");
            result.append(HTMLConverter.UNDERLINE_TAG.on(frame));
        }
        return result;
    }

    /**
     * Returns {@code true} if the state is a result state.
     */
    public boolean isResult() {
        return getNode().isResult();
    }

    /* Always false: error states are reported through other means. */
    @Override
    public boolean hasErrors() {
        return false;
    }

    /**
     * @return true if the state is a start state.
     */
    public boolean isStart() {
        GTS gts = getNode().getGTS();
        return gts.startState().equals(getNode());
    }

    /**
     * Returns {@code true} if the state is closed.
     */
    public boolean isClosed() {
        return getNode().isClosed();
    }

    /**
     * @return true if the state is final.
     */
    public boolean isFinal() {
        return getNode().isFinal();
    }

    @Override
    public String getNodeIdString() {
        String result = super.getNodeIdString();
        Frame frame = getNode().getPrimeFrame();
        if (!frame.isStart()) {
            result += "|" + frame.toString();
        }
        return result;
    }

    /** Indicates that this edge is active. */
    final boolean isActive() {
        return getLooks().contains(Look.ACTIVE);
    }

    /** Changes the active status of this edge.
     * @return {@code true} if the active status changed as a result of this call.
     */
    @Override
    public final boolean setActive(boolean active) {
        return setLook(Look.ACTIVE, active);
    }

    @Override
    protected Look getStructuralLook() {
        if (isStart()) {
            return Look.START;
        } else {
            return Look.STATE;
        }
    }

    /**
     * Returns a fresh instance.
     * Call {@link #setJModel(JModel)} and {@link #setNode(Node)} to initialise.
     */
    public static LTSJVertex newInstance() {
        return new LTSJVertex();
    }
}