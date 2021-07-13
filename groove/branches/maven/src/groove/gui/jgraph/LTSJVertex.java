package groove.gui.jgraph;

import java.util.Iterator;

import groove.control.instance.Frame;
import groove.graph.Edge;
import groove.graph.Node;
import groove.gui.look.Look;
import groove.gui.look.VisualKey;
import groove.io.HTMLConverter;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.StartGraphState;

/**
 * JVertex class that describes the underlying node as a graph state.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LTSJVertex extends AJVertex<GTS,LTSJGraph,LTSJModel,LTSJEdge>implements LTSJCell {
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
    public void initialise() {
        super.initialise();
        this.visibleFlag = true;
        this.outCount = -1;
        GraphState state = getNode();
        setLook(Look.OPEN, !state.isClosed());
        setLook(Look.ABSENT, state.isAbsent());
        setLook(Look.RECIPE, state.isInternalState());
        setLook(Look.TRANSIENT, state.isTransient());
        setLook(Look.FINAL, state.isFinal());
        setLook(Look.RESULT, isResult());
        setLook(Look.ERROR, state.isError());
    }

    @Override
    public void addEdge(Edge edge) {
        super.addEdge(edge);
        setStale(VisualKey.LABEL);
        setStale(VisualKey.TEXT_SIZE);
        setStale(VisualKey.NODE_SIZE);
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
        return getNode().isDone() && getOutCount() == getOutVisibleCount();
    }

    /** Returns the number of outgoing transitions that are in principle shown on the LTS panel. */
    private int getOutCount() {
        LTSJGraph jGraph = getJGraph();
        assert jGraph != null; // guaranteed by now
        if (this.outCount < 0) {
            this.outCount = getNode().getTransitions(jGraph.getTransitionClass())
                .size();
        }
        return this.outCount;
    }

    private int outCount;

    /** Returns the number of outgoing transitions that is currently visible on the LTS panel. */
    private int getOutVisibleCount() {
        return this.outVisibles + getEdges().size();
    }

    /** Adjusts the number of visibly outgoing transitions by a given number.
     * @param visible if {@code true}, the number is increased, otherwise decreased
     * @param count the number of additional/fewer visible outgoing transitions
     */
    void changeOutVisible(boolean visible, int count) {
        boolean oldAllOutVisible = isAllOutVisible();
        if (visible) {
            this.outVisibles += count;
        } else {
            this.outVisibles -= count;
        }
        if (isAllOutVisible() != oldAllOutVisible) {
            setStale(VisualKey.LABEL);
            setStale(VisualKey.TEXT_SIZE);
            setStale(VisualKey.NODE_SIZE);
        }
    }

    private int outVisibles;

    /** Sets the parent edge. */
    void setParentEdge(LTSJEdge parent) {
        this.parentEdge = parent;
    }

    /** Returns the parent edge in a spanning tree. */
    public LTSJEdge getParentEdge() {
        LTSJEdge result = this.parentEdge;
        if (result == null && !(getNode() instanceof StartGraphState)) {
            Iterator<? extends LTSJEdge> iter = getContext();
            while (iter.hasNext()) {
                LTSJEdge edge = iter.next();
                if (edge.getTargetVertex() == this && edge.getSourceVertex() != this) {
                    result = edge;
                    break;
                }
            }
        }
        return result;
    }

    private LTSJEdge parentEdge;

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
        LTSJGraph jGraph = getJGraph();
        assert jGraph != null; // guaranteed by now
        return jGraph.isResult(getNode());
    }

    @Override
    public boolean hasErrors() {
        return getNode().isError();
    }

    /**
     * @return true if the state is a start state.
     */
    public boolean isStart() {
        GTS gts = getNode().getGTS();
        return gts.startState()
            .equals(getNode());
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