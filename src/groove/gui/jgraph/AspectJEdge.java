package groove.gui.jgraph;

import static groove.view.aspect.AspectKind.ARGUMENT;
import static groove.view.aspect.AspectKind.DEFAULT;
import groove.graph.Edge;
import groove.graph.EdgeRole;
import groove.graph.GraphRole;
import groove.graph.LabelPattern;
import groove.gui.jgraph.JAttr.AttributeMap;
import groove.io.HTMLConverter;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.RuleLabel;
import groove.util.ChangeCount;
import groove.util.ChangeCount.Tracker;
import groove.view.FormatError;
import groove.view.FormatException;
import groove.view.GraphBasedModel;
import groove.view.GraphBasedModel.TypeModelMap;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectLabel;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectParser;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jgraph.graph.GraphConstants;

/**
 * Specialized j-edge for rule graphs, with its own tool tip text.
 */
public class AspectJEdge extends GraphJEdge implements AspectJCell {
    /** 
     * Creates an uninitialised instance.
     * @param jGraph the {@link GraphJGraph} in which this JEdge will be used.
     */
    public AspectJEdge(AspectJGraph jGraph, AspectJModel jModel) {
        super(jGraph, jModel);
        setUserObject(null);
        this.aspect = DEFAULT;
        resetTracker();
        refreshAttributes();
    }

    /** Creates a j-edge on the basis of a given (aspectual) edge. */
    public AspectJEdge(AspectJGraph jGraph, AspectJModel jModel, AspectEdge edge) {
        super(jGraph, jModel, edge);
        this.jModelTracker =
            jModel == null ? ChangeCount.DUMMY_TRACKER
                    : jModel.getModCount().createTracker();
        setUserObject(null);
        this.aspect = edge.getKind();
        this.errors.addAll(edge.getErrors());
        refreshAttributes();
    }

    @Override
    public AspectJGraph getJGraph() {
        return (AspectJGraph) super.getJGraph();
    }

    @Override
    public AspectJModel getJModel() {
        return (AspectJModel) super.getJModel();
    }

    @Override
    public AspectKind getAspect() {
        return this.aspect;
    }

    @Override
    public AspectNode getSourceNode() {
        return (AspectNode) super.getSourceNode();
    }

    @Override
    public AspectNode getTargetNode() {
        return (AspectNode) super.getTargetNode();
    }

    /** Indicates if this is the incoming part of a nodified edge. */
    public boolean isNodeEdgeIn() {
        return getTargetVertex() != null
            && ((AspectJVertex) getTargetVertex()).isEdge();
    }

    /** Indicates if this is the incoming pars of a nodified edge. */
    public boolean isNodeEdgeOut() {
        return ((AspectJVertex) getSourceVertex()).isEdge();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<AspectEdge> getEdges() {
        return (Set<AspectEdge>) super.getEdges();
    }

    @Override
    public AspectEdge getEdge() {
        return (AspectEdge) super.getEdge();
    }

    /** Clears the errors and the aspect, in addition to calling the super method. */
    @Override
    void reset() {
        super.reset();
        this.errors.clear();
        clearExtraErrors();
        this.aspect = DEFAULT;
    }

    @Override
    public AspectJEdge clone() {
        AspectJEdge result = (AspectJEdge) super.clone();
        result.errors = new ArrayList<FormatError>();
        result.extraErrors = new ArrayList<FormatError>();
        result.resetTracker();
        return result;
    }

    @Override
    public AspectJEdge newJEdge(GraphJModel<?,?> jModel, Edge edge) {
        if (edge == null) {
            return new AspectJEdge(getJGraph(), (AspectJModel) jModel);
        } else {
            return new AspectJEdge(getJGraph(), (AspectJModel) jModel,
                (AspectEdge) edge);
        }
    }

    /**
     * Returns <tt>true</tt> only if {@code mustAdd} holds or if the aspect 
     * values of the edge to be added equal those of this JEdge.
     * @param mustAdd if {@code true}, the edge is added even if its
     * aspects conflict with previously added edges (but an error is added
     * to the edge).
     */
    private boolean addEdge(Edge edge, boolean mustAdd) {
        AspectEdge aspectEdge = (AspectEdge) edge;
        boolean first = getEdges().isEmpty();
        AspectEdge oldEdge = getEdge();
        boolean compatible = first || aspectEdge.equalsAspects(oldEdge);
        boolean result = (compatible || mustAdd) && super.addEdge(aspectEdge);
        if (result) {
            if (first) {
                this.aspect = aspectEdge.getKind();
            }
            FormatError error = null;
            if (edge.getRole() != EdgeRole.BINARY) {
                error =
                    new FormatError("Node label '%s' not allowed on edges",
                        edge.label(), this);
            } else if (!compatible) {
                error =
                    new FormatError(
                        "Conflicting aspects in edge labels %s and %s",
                        oldEdge.label(), edge.label(), this);
            }
            if (error != null) {
                aspectEdge =
                    new AspectEdge(aspectEdge.source(), aspectEdge.label(),
                        aspectEdge.target());
                aspectEdge.addError(error);
                aspectEdge.setFixed();
                replaceEdge(aspectEdge);
            }
            this.errors.addAll(aspectEdge.getErrors());
        }
        return result;
    }

    /**
     * Returns <tt>true</tt> only if the aspect values of the edge to be
     * added equal those of this j-edge, and the superclass is also willing.
     */
    @Override
    public boolean addEdge(Edge edge) {
        return addEdge(edge, false);
    }

    @Override
    public String getText() {
        String result = null;
        if (isNodeEdgeIn()) {
            LabelPattern pattern =
                ((AspectJVertex) getTargetVertex()).getEdgeLabelPattern();
            @SuppressWarnings({"unchecked", "rawtypes"})
            GraphBasedModel<HostGraph> resourceModel =
                (GraphBasedModel) getJModel().getResourceModel();
            try {
                result =
                    pattern.getLabel(
                        resourceModel.toResource(),
                        (HostNode) resourceModel.getMap().getNode(
                            getTargetNode()));
            } catch (FormatException e) {
                // assert false;
            }
        } else if (isNodeEdgeOut()) {
            result = "";
        } else {
            result = super.getText();
        }
        return result;
    }

    @Override
    StringBuilder getEdgeDescription() {
        getEdge().testFixed(true);
        StringBuilder result = new StringBuilder();
        if (hasError()) {
            for (FormatError error : this.extraErrors) {
                if (result.length() > 0) {
                    result.append("<br>");
                }
                result.append(error.toString());
            }
            HTMLConverter.red.on(result);
        } else {
            AspectKind attrKind = getEdge().getAttrKind();
            if (attrKind == ARGUMENT) {
                result.append(new StringBuilder("Argument edge"));
            } else if (attrKind.hasSignature()) {
                result.append(new StringBuilder("Operation edge"));
            } else {
                result.append(super.getEdgeDescription());
            }
            if (AspectJModel.ROLE_DESCRIPTIONS.containsKey(this.aspect)) {
                result.append("<br>"
                    + AspectJModel.ROLE_DESCRIPTIONS.get(this.aspect));
            }
        }
        return result;
    }

    @Override
    StringBuilder getEdgeKindDescription() {
        StringBuilder result = super.getEdgeKindDescription();
        if (AspectJModel.ROLE_NAMES.containsKey(this.aspect)) {
            HTMLConverter.toUppercase(result, false);
            result.insert(0, " ");
            result.insert(0, AspectJModel.ROLE_NAMES.get(this.aspect));
        }
        return result;
    }

    @Override
    public List<StringBuilder> getLines() {
        updateCachedValues();
        return this.lines;
    }

    @Override
    public Collection<Edge> getKeys() {
        updateCachedValues();
        return this.keys;
    }

    /** 
     * Updates the cached values of {@link #lines} and {@link #keys},
     * if the model has been modified in the meantime.
     */
    private void updateCachedValues() {
        if (this.jModelTracker.isStale()) {
            this.keys = computeKeys();
            this.lines = computeLines();
        }
    }

    /** Recomputes the set of node lines for this aspect node. */
    private List<StringBuilder> computeLines() {
        if (isSourceLabel()) {
            return Collections.emptyList();
        } else if (getJGraph().isShowAspects()) {
            // used to include hasError() as a disjunct
            return getUserObject().toLines();
        } else {
            return super.getLines();
        }
    }

    /**
     * On demand prefixes the label with the edge's aspect values.
     */
    @Override
    public StringBuilder getLine(Edge edge) {
        AspectEdge aspectEdge = (AspectEdge) edge;
        StringBuilder result = new StringBuilder();
        result.append(aspectEdge.getDisplayLabel().text());
        // add the level name, if not already shown as an aspect
        if (this.aspect.isRole()) {
            String levelName = aspectEdge.getLevelName();
            if (levelName != null && levelName.length() != 0) {
                result.append(LEVEL_NAME_SEPARATOR + levelName);
            }
        }
        return result;
    }

    /** Recomputes the set of keys for this aspect node. */
    private Collection<Edge> computeKeys() {
        if (this.aspect.isMeta()) {
            return Collections.emptySet();
        } else {
            return super.getKeys();
        }
    }

    @Override
    public Edge getKey(Edge edge) {
        Edge result;
        TypeModelMap typeMap = getJModel().getResourceModel().getTypeMap();
        if (typeMap != null) {
            result = typeMap.getEdge((AspectEdge) edge);
        } else {
            result = edge;
        }
        return result;
    }

    /** 
     * Indicates if this JEdge should be shown
     * instead as part of the source node label.
     * This is true if this is an attribute edge to a "pure" value node, 
     * and value nodes are not shown.
     */
    public boolean isSourceLabel() {
        if (getJGraph().isShowValueNodes()) {
            return false;
        }
        if (getSourceNode().getAttrKind().hasSignature()) {
            return false;
        }
        if (this.aspect.isRole() || this.aspect == AspectKind.NESTED) {
            // we're in a rule graph; watch for parameters and variable nodes
            return getTargetNode().getAttrAspect().hasContent()
                && !getTargetNode().hasParam();
        }
        return getTargetNode().getAttrKind().hasSignature();
    }

    @Override
    public boolean isVisible() {
        boolean result = true;
        if (getJGraph().getLevelTree() != null) {
            result = getJGraph().getLevelTree().isVisible(this);
        }
        if (result) {
            result = super.isVisible();
        }
        return result;
    }

    @Override
    public final boolean hasError() {
        return !this.extraErrors.isEmpty() || !this.errors.isEmpty();
    }

    @Override
    public void clearExtraErrors() {
        this.extraErrors.clear();
    }

    @Override
    public void addExtraError(FormatError error) {
        this.extraErrors.add(error);
        refreshAttributes();
    }

    /** Returns the (possibly empty) set of errors in this JEdge. */
    public Collection<FormatError> getErrors() {
        return this.errors;
    }

    @Override
    protected AttributeMap createAttributes() {
        AttributeMap result =
            AspectJGraph.ASPECT_EDGE_ATTR.get(this.aspect).clone();
        AspectEdge edge = getEdge();
        RuleLabel ruleModelLabel = edge == null ? null : edge.getRuleLabel();
        if (ruleModelLabel != null) {
            if (ruleModelLabel.isEmpty() && this.aspect != AspectKind.CREATOR
                || ruleModelLabel.isNeg()
                && ruleModelLabel.getNegOperand().isEmpty()) {
                // remove edge arrow
                GraphConstants.setLineEnd(result, GraphConstants.ARROW_NONE);
            } else if (!ruleModelLabel.isAtom()) {
                setFontAttr(result, Font.ITALIC);
            }
        }
        if (edge != null && edge.isComposite()) {
            GraphConstants.setBeginSize(result, 15);
            GraphConstants.setLineBegin(result, GraphConstants.ARROW_DIAMOND);
        }
        if (edge != null && isNodeEdgeIn()) {
            GraphConstants.setLineEnd(result, GraphConstants.ARROW_NONE);
        }
        if (edge != null && isBidirectional()) {
            GraphConstants.setLineBegin(result, GraphConstants.ARROW_CLASSIC);
        }
        if (edge != null) {
            if (edge.getInMult() != null || edge.getOutMult() != null) {
                String inMult =
                    edge.getInMult() == null ? "" : edge.getInMult().toString();
                String outMult =
                    edge.getOutMult() == null ? ""
                            : edge.getOutMult().toString();
                GraphConstants.setExtraLabels(result, new Object[] {outMult,
                    inMult});
                Point2D[] labelPositions =
                    {new Point2D.Double(IN_MULT_DIST, MULT_X),
                        new Point2D.Double(OUT_MULT_DIST, MULT_X)};
                GraphConstants.setExtraLabelPositions(result, labelPositions);
            } else {
                GraphConstants.setRemoveAttributes(result, MULT_REMOVAL);
            }
        }
        if (getJGraph().hasActiveEditor()) {
            GraphConstants.setEditable(result, true);
            GraphConstants.setConnectable(result, true);
            GraphConstants.setDisconnectable(result, true);
        }
        if (getSourceVertex() != null && getEdge() != null
            && getEdge().getGraphRole() != GraphRole.RULE) {
            Color color = ((AspectJVertex) getSourceVertex()).getColor();
            if (color != null) {
                GraphConstants.setForeground(result, color);
                GraphConstants.setLineColor(result, color);
            }
        }
        return result;
    }

    /** Modifies the font attribute in the given attribute map. */
    final protected void setFontAttr(AttributeMap result, int fontAttr) {
        Font currentFont = GraphConstants.getFont(result);
        GraphConstants.setFont(result, currentFont.deriveFont(fontAttr));
    }

    public void saveToUserObject() {
        // collect the edge information
        AspectJObject userObject = getUserObject();
        userObject.clear();
        userObject.addEdges(getEdges());
    }

    @Override
    public void loadFromUserObject(GraphRole role) {
        reset();
        AspectParser parser = AspectParser.getInstance();
        for (String text : getUserObject()) {
            AspectLabel label = parser.parse(text, role);
            AspectEdge edge =
                new AspectEdge(getSourceNode(), label, getTargetNode());
            edge.setFixed();
            boolean added = addEdge(edge, true);
            assert added : String.format("Could not add edge %s to jEdge %s",
                edge, this);
        }
        refreshAttributes();
    }

    /**
     * Creates a new used object, and initialises it from a given value.
     * If the value is a collection or a string, loads the user object from it.
     */
    @Override
    public void setUserObject(Object value) {
        // we do need to create a new object, otherwise undos do not work
        AspectJObject myObject = new AspectJObject();
        if (value instanceof AspectJObject) {
            myObject.addAll((AspectJObject) value);
        } else if (value != null) {
            myObject.load(value.toString());
        }
        super.setUserObject(myObject);
    }

    /** Specialises the return type. */
    @Override
    public AspectJObject getUserObject() {
        return (AspectJObject) super.getUserObject();
    }

    /** 
     * Sets the {@link #jModelTracker} to a fresh value.
     * This is delegated to a separate method because it needs
     * to be invoked upon cloning as well as in the constructor.
     * @see #clone()
     */
    private void resetTracker() {
        this.jModelTracker =
            getJModel() == null ? ChangeCount.DUMMY_TRACKER
                    : getJModel().getModCount().createTracker();
    }

    /** Cached lines. */
    private List<StringBuilder> lines;
    /** Cached tree entries. */
    private Collection<Edge> keys;
    /** JModel modification tracker. */
    private Tracker jModelTracker;
    private AspectKind aspect;

    private Collection<FormatError> errors = new LinkedHashSet<FormatError>();

    private List<FormatError> extraErrors = new ArrayList<FormatError>();

    /** Returns a prototype {@link AspectJEdge} for a given {@link AspectJGraph}. */
    public static AspectJEdge getPrototype(AspectJGraph jGraph) {
        return new AspectJEdge(jGraph, null);
    }

    /** Permille fractional distance of in multiplicity label from source node. */
    private static final double IN_MULT_DIST =
        GraphConstants.PERMILLE * 90 / 100;
    /** Permille fractional distance of out multiplicity label from target node. */
    private static final double OUT_MULT_DIST =
        GraphConstants.PERMILLE * 10 / 100;
    /** x-position of multiplicity labels. */
    private static final double MULT_X = -11;
    /** Multiplicity removal object. */
    private static final Object[] MULT_REMOVAL = new Object[] {
        GraphConstants.EXTRALABELPOSITIONS, GraphConstants.EXTRALABELS};
}