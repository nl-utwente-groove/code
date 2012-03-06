package groove.gui.jgraph;

import groove.graph.Edge;
import groove.gui.jgraph.JAttr.AttributeMap;
import groove.io.HTMLConverter;
import groove.lts.GraphTransition;
import groove.lts.RuleTransitionLabel;
import groove.lts.RuleTransition;
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
    LTSJEdge(LTSJGraph jGraph, LTSJModel jModel) {
        super(jGraph, jModel);
        this.visible = true;
    }

    /**
     * Creates a new instance from a given edge (required to be a
     * {@link RuleTransition}).
     */
    LTSJEdge(LTSJGraph jGraph, LTSJModel jModel, GraphTransition edge) {
        super(jGraph, jModel, edge);
        this.visible = true;
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
    public GraphJEdge newJEdge(GraphJModel<?,?> jModel, Edge edge) {
        return new LTSJEdge(getJGraph(), (LTSJModel) jModel,
            (GraphTransition) edge);
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
            RuleTransition trans = (RuleTransition) part;
            String description;
            if (getJGraph().isShowAnchors()) {
                description = trans.getEvent().toString();
            } else {
                description = trans.getEvent().getRule().getFullName();
            }
            displayedLabels[labelIndex] =
                HTMLConverter.STRONG_TAG.on(description, true);
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
    protected StringBuilder getLine(Edge edge) {
        String text =
            getJGraph().isShowAnchors() && edge instanceof RuleTransition
                    ? RuleTransitionLabel.getAnchorText(((RuleTransition) edge).getEvent())
                    : edge.label().text();
        return new StringBuilder(text);
    }

    /** Indicates that this edge is active. */
    final boolean isActive() {
        return this.active;
    }

    /** Indicates that the node or target of this edge is absent. */
    final boolean isAbsent() {
        return getEdge().source().isAbsent() || getEdge().target().isAbsent();
    }

    /** Indicates that this edge is a partial rule application. */
    final boolean isPartial() {
        return getEdge().isPartial();
    }

    @Override
    public boolean isVisible() {
        boolean result =
            (getJGraph().isShowPartialTransitions() || !isPartial())
                && super.isVisible();
        return result && this.visible;
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
        AttributeMap result = LTSJGraph.LTS_EDGE_ATTR.clone();
        if (isActive()) {
            result.applyMap(LTSJGraph.LTS_EDGE_ACTIVE_CHANGE);
        }
        if (isAbsent()) {
            result.applyMap(LTSJGraph.LTS_EDGE_ABSENT_CHANGE);
        }
        if (isPartial()) {
            result.applyMap(LTSJGraph.LTS_EDGE_TRANSIENT_CHANGE);
        }
        return result;
    }

    private boolean active;

    private boolean visible;

    /** Returns a prototype {@link CtrlJEdge} for a given {@link CtrlJGraph}. */
    public static LTSJEdge getPrototype(LTSJGraph jGraph) {
        return new LTSJEdge(jGraph, null);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

}