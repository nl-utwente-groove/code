package groove.gui.jgraph;

import static groove.gui.jgraph.JAttr.LTS_EDGE_ACTIVE_CHANGE;
import static groove.gui.jgraph.JAttr.LTS_EDGE_ATTR;
import groove.graph.Edge;
import groove.gui.jgraph.JAttr.AttributeMap;
import groove.lts.DerivationLabel;
import groove.lts.GraphTransition;
import groove.util.Converter;
import groove.util.Groove;

/**
 * JEdge class that describes the underlying edge as a graph transition.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LTSJEdge extends GraphJEdge implements LTSJCell {
    /**
     * Constructor for a prototype object of this class.
     */
    LTSJEdge(LTSJGraph jGraph) {
        super(jGraph);
    }

    /**
     * Creates a new instance from a given edge (required to be a
     * {@link GraphTransition}).
     */
    LTSJEdge(LTSJGraph jGraph, GraphTransition edge) {
        super(jGraph, edge);
    }

    @Override
    public LTSJGraph getJGraph() {
        return (LTSJGraph) super.getJGraph();
    }

    @Override
    public GraphTransition getEdge() {
        return (GraphTransition) super.getEdge();
    }

    @Override
    public GraphJEdge newJEdge(Edge<?> edge) {
        return new LTSJEdge(getJGraph(), (GraphTransition) edge);
    }

    @Override
    StringBuilder getEdgeKindDescription() {
        return new StringBuilder("transition");
    }

    @Override
    String getLabelDescription() {
        StringBuffer result = new StringBuffer(", generated by ");
        String[] displayedLabels = new String[getEdges().size()];
        int labelIndex = 0;
        for (Object part : getEdges()) {
            GraphTransition trans = (GraphTransition) part;
            String description;
            if (getJGraph().isShowAnchors()) {
                description = trans.getEvent().toString();
            } else {
                description = trans.getEvent().getRule().getName().toString();
            }
            displayedLabels[labelIndex] =
                Converter.STRONG_TAG.on(description, true);
            labelIndex++;
        }
        if (displayedLabels.length == 1) {
            result.append(displayedLabels[0]);
        } else {
            result.append(Groove.toString(displayedLabels, "<br>- ", "",
                "<br>- "));
        }
        return result.toString();
    }

    @Override
    protected StringBuilder getLine(Edge<?> edge) {
        String text =
            getJGraph().isShowAnchors() ? new DerivationLabel(
                ((GraphTransition) edge).getEvent()).text()
                    : edge.label().text();
        return new StringBuilder(text);
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

    @Override
    protected AttributeMap createAttributes() {
        AttributeMap result = LTS_EDGE_ATTR.clone();
        if (isActive()) {
            result.applyMap(LTS_EDGE_ACTIVE_CHANGE);
        }
        return result;
    }

    private boolean active;

    /** Returns a prototype {@link CtrlJEdge} for a given {@link CtrlJGraph}. */
    public static LTSJEdge getPrototype(LTSJGraph jGraph) {
        return new LTSJEdge(jGraph);
    }
}