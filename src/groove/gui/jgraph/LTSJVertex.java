package groove.gui.jgraph;

import groove.control.CtrlState;
import groove.graph.Edge;
import groove.graph.Node;
import groove.io.HTMLConverter;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;

import java.util.List;

/**
 * JVertex class that describes the underlying node as a graph state.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LTSJVertex extends GraphJVertex implements LTSJCell {
    /**
     * Creates a new instance for a given node (required to be a
     * {@link GraphState}) in an LTS model.
     */
    LTSJVertex(LTSJModel jModel, GraphState node) {
        super(jModel, node);
        this.visible = true;
    }

    @Override
    public LTSJGraph getJGraph() {
        return (LTSJGraph) super.getJGraph();
    }

    @Override
    public LTSJVertex newJVertex(GraphJModel<?,?> jModel, Node node) {
        return new LTSJVertex((LTSJModel) jModel, (GraphState) node);
    }

    @Override
    public GraphState getNode() {
        return (GraphState) super.getNode();
    }

    @Override
    protected Node getNodeKey() {
        return null;
    }

    /** A state is also visible if it is open, final, or the start state. */
    @Override
    public boolean isVisible() {
        return (getJGraph().isShowPartialTransitions() || !isTransient())
            && (isSpecialNode() || hasVisibleIncidentEdge()) && this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Tests if the state is the start state, a final state, or not yet
     * closed.
     */
    private boolean isSpecialNode() {
        GraphState state = getNode();
        GTS lts = getNode().getGTS();
        return lts.startState().equals(state) // || !state.isClosed()
            || lts.isFinal(state);
    }

    @Override
    StringBuilder getNodeDescription() {
        StringBuilder result = new StringBuilder("State ");
        result.append(HTMLConverter.UNDERLINE_TAG.on(getNode()));
        // if a control location is available, add this to the tooltip
        // if( this.getNode().getLocation() != null ) {
        // result.append("ctrl: " + this.getNode().getLocation());
        // }
        return result;
    }

    /**
     * @return true if the state is a result state.
     */
    public boolean isResult() {
        return getNode().getGTS().isResult(getNode());
    }

    /**
     * @return true if the state is an error state.
     */
    public boolean isError() {
        return getNode().isError();
    }

    /**
     * @return true if the state is a start state.
     */
    public boolean isStart() {
        GTS gts = getNode().getGTS();
        return gts.startState().equals(getNode());
    }

    /**
     * @return true if the state is closed.
     */
    public boolean isClosed() {
        return getNode().isClosed();
    }

    /**
     * @return true if the state is transient.
     */
    public boolean isTransient() {
        return getNode().isTransient();
    }

    /**
     * @return true if the state is final.
     */
    public boolean isFinal() {
        GTS gts = getNode().getGTS();
        return gts.isFinal(getNode());
    }

    @Override
    public List<StringBuilder> getLines() {
        List<StringBuilder> result = super.getLines();
        CtrlState ctrlState = getNode().getCtrlState();
        if (!ctrlState.getAut().isDefault()) {
            result.add(new StringBuilder("ctrl: "
                + HTMLConverter.toHtml(ctrlState.toString())));
        }
        return result;
    }

    /**
     * This implementation returns either the transition label, or the event
     * label, depending on #isShowAnchors().
     */
    @Override
    public StringBuilder getLine(Edge edge) {
        String text =
            ((GraphTransition) edge).text(getJGraph().isShowAnchors());
        StringBuilder result = new StringBuilder(text);
        HTMLConverter.toHtml(result);
        return result;

    }

    /** Indicates that this edge is active. */
    final boolean isActive() {
        return this.active;
    }

    /** Changes the active status of this edge.
     * @return {@code true} if the active status changed as a result of this call.
     */
    public final boolean setActive(boolean active) {
        boolean result = active != this.active;
        if (result) {
            this.active = active;
            refreshAttributes();
        }
        return result;
    }

    /**
     * This implementation adds special attributes for the start state, open
     * states, final states, and the active state.
     * @see LTSJGraph#LTS_NODE_ATTR
     * @see LTSJGraph#LTS_START_NODE_ATTR
     * @see LTSJGraph#LTS_OPEN_NODE_ATTR
     * @see LTSJGraph#LTS_FINAL_NODE_ATTR
     * @see LTSJGraph#LTS_RESULT_NODE_ATTR
     * @see LTSJGraph#LTS_NODE_ACTIVE_CHANGE
     */
    @Override
    protected JAttr.AttributeMap createAttributes() {
        JAttr.AttributeMap result;
        if (isError()) {
            result = LTSJGraph.LTS_ERROR_NODE_ATTR;
        } else if (isResult()) {
            result = LTSJGraph.LTS_RESULT_NODE_ATTR;
        } else if (isStart()) {
            result = LTSJGraph.LTS_START_NODE_ATTR;
        } else if (!isClosed()) {
            result = LTSJGraph.LTS_OPEN_NODE_ATTR;
        } else if (isFinal()) {
            result = LTSJGraph.LTS_FINAL_NODE_ATTR;
        } else {
            result = LTSJGraph.LTS_NODE_ATTR;
        }
        result = result.clone();
        if (isActive()) {
            result.applyMap(LTSJGraph.LTS_NODE_ACTIVE_CHANGE);
        }
        if (getNode().isAbsent()) {
            result.applyMap(LTSJGraph.LTS_NODE_ABSENT_CHANGE);
        }
        if (isTransient()) {
            result.applyMap(LTSJGraph.LTS_NODE_TRANSIENT_CHANGE);
        }
        return result;
    }

    private boolean active;

    private boolean visible;

    /** Returns a prototype {@link LTSJVertex} for a given {@link LTSJGraph}. */
    public static LTSJVertex getPrototype(LTSJGraph jGraph) {
        return new LTSJVertex(null, null);
    }
}