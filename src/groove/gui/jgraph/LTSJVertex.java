package groove.gui.jgraph;

import static groove.gui.jgraph.JAttr.LTS_FINAL_NODE_ATTR;
import static groove.gui.jgraph.JAttr.LTS_NODE_ACTIVE_CHANGE;
import static groove.gui.jgraph.JAttr.LTS_NODE_ATTR;
import static groove.gui.jgraph.JAttr.LTS_OPEN_NODE_ATTR;
import static groove.gui.jgraph.JAttr.LTS_RESULT_NODE_ATTR;
import static groove.gui.jgraph.JAttr.LTS_START_NODE_ATTR;
import groove.control.CtrlState;
import groove.graph.Label;
import groove.lts.DerivationLabel;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.util.Converter;

import java.util.List;

import org.jgraph.graph.AttributeMap;

/**
 * JVertex class that describes the underlying node as a graph state.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LTSJVertex extends GraphJVertex<GraphState,GraphTransition> {
    /**
     * Creates a new instance for a given node (required to be a
     * {@link GraphState}) in an LTS model.
     */
    LTSJVertex(LTSJModel jModel, GraphState node) {
        super(jModel, node, true);
    }

    @Override
    public LTSJModel getJModel() {
        return (LTSJModel) super.getJModel();
    }

    /** 
     * Returns the GTS wrapped in the model.
     * Convenience method for {@code getJModel().getGraph()}. 
     */
    GTS getGraph() {
        return getJModel().getGraph();
    }

    /** A state is also visible if it is open, final, or the start state. */
    @Override
    public boolean isVisible() {
        return isSpecialNode() || hasVisibleIncidentEdge();
    }

    /**
     * Tests if the state is the start state, a final state, or not yet
     * closed.
     */
    private boolean isSpecialNode() {
        GTS lts = getGraph();
        GraphState state = getNode();
        return lts.startState().equals(state) // || !state.isClosed()
            || lts.isFinal(state);
    }

    @Override
    StringBuilder getNodeDescription() {
        StringBuilder result = new StringBuilder("State ");
        result.append(Converter.UNDERLINE_TAG.on(getNode()));
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
        return getGraph().isResult(getNode());
    }

    /**
     * @return true if the state is a start state.
     */
    public boolean isStart() {
        return getGraph().startState().equals(getNode());
    }

    /**
     * @return true if the state is closed.
     */
    public boolean isClosed() {
        return getNode().isClosed();
    }

    /**
     * @return true if the state is final.
     */
    public boolean isFinal() {
        return getGraph().isFinal(getNode());
    }

    @Override
    public List<StringBuilder> getLines() {
        List<StringBuilder> result = super.getLines();
        CtrlState ctrlState = getNode().getCtrlState();
        if (ctrlState.getAut().getProgram() != null) {
            result.add(new StringBuilder("ctrl: "
                + Converter.toHtml(ctrlState.toString())));
        }
        return result;
    }

    /**
     * This implementation returns either the transition label, or the event
     * label, depending on #isShowAnchors().
     */
    @Override
    public Label getLabel(GraphTransition edge) {
        return getJModel().isShowAnchors() ? new DerivationLabel(
            edge.getEvent()) : super.getLabel(edge);
    }

    @Override
    public StringBuilder getLine(GraphTransition edge) {
        return Converter.toHtml(new StringBuilder(edge.label().text()));
    }

    /** Indicates that this edge is active. */
    final boolean isActive() {
        return this.active;
    }

    /** Changes the active status of this edge.
     * @return {@code true} if the active status changed as a result of this call.
     */
    final boolean setActive(boolean active) {
        boolean result = active != this.active;
        if (result) {
            this.active = active;
            createAttributes(getJModel());
        }
        return result;
    }

    /**
     * This implementation adds special attributes for the start state, open
     * states, final states, and the active state.
     * @see JAttr#LTS_NODE_ATTR
     * @see JAttr#LTS_START_NODE_ATTR
     * @see JAttr#LTS_OPEN_NODE_ATTR
     * @see JAttr#LTS_FINAL_NODE_ATTR
     * @see JAttr#LTS_RESULT_NODE_ATTR
     * @see JAttr#LTS_NODE_ACTIVE_CHANGE
     */
    @Override
    protected AttributeMap createAttributes() {
        AttributeMap result;
        if (isResult()) {
            result = LTS_RESULT_NODE_ATTR.clone();
        } else if (isStart()) {
            result = LTS_START_NODE_ATTR.clone();
        } else if (!isClosed()) {
            result = LTS_OPEN_NODE_ATTR.clone();
        } else if (isFinal()) {
            result = LTS_FINAL_NODE_ATTR.clone();
        } else {
            result = LTS_NODE_ATTR.clone();
        }
        if (isActive()) {
            result.applyMap(LTS_NODE_ACTIVE_CHANGE);
        }
        return result;
    }

    private boolean active;
}