package groove.gui.jgraph;

import static groove.view.aspect.AspectKind.ARGUMENT;
import static groove.view.aspect.AspectKind.NONE;
import static groove.view.aspect.AspectKind.REMARK;
import groove.graph.Edge;
import groove.graph.EdgeRole;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.gui.jgraph.JAttr.AttributeMap;
import groove.io.HTMLConverter;
import groove.trans.RuleLabel;
import groove.view.FormatError;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectLabel;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectParser;

import java.awt.Color;
import java.awt.Font;
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
    public AspectJEdge(AspectJGraph jGraph) {
        super(jGraph);
        setUserObject(null);
        this.aspect = NONE;
        refreshAttributes();
    }

    /** Creates a j-edge on the basis of a given (aspectual) edge. */
    public AspectJEdge(AspectJGraph jGraph, AspectEdge edge) {
        super(jGraph, edge);
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
    public AspectNode getSourceNode() {
        return (AspectNode) super.getSourceNode();
    }

    @Override
    public AspectNode getTargetNode() {
        return (AspectNode) super.getTargetNode();
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
        this.extraError = false;
        this.aspect = NONE;
    }

    @Override
    public AspectJEdge clone() {
        AspectJEdge result = (AspectJEdge) super.clone();
        result.errors = new ArrayList<FormatError>();
        return result;
    }

    @Override
    public AspectJEdge newJEdge(Edge<?> edge) {
        if (edge == null) {
            return new AspectJEdge(getJGraph());
        } else {
            return new AspectJEdge(getJGraph(), (AspectEdge) edge);
        }
    }

    /**
     * Returns <tt>true</tt> only if {@code mustAdd} holds or if the aspect 
     * values of the edge to be added equal those of this JEdge.
     * @param mustAdd if {@code true}, the edge is added even if its
     * aspects conflict with previously added edges (but an error is added
     * to the edge).
     */
    private boolean addEdge(Edge<?> edge, boolean mustAdd) {
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
    public boolean addEdge(Edge<?> edge) {
        return addEdge(edge, false);
    }

    @Override
    StringBuilder getEdgeDescription() {
        getEdge().testFixed(true);
        StringBuilder result = new StringBuilder();
        AspectKind attrKind = getEdge().getAttrKind();
        if (attrKind == ARGUMENT) {
            result.append(new StringBuilder("Argument edge"));
        } else if (attrKind.isTypedData()) {
            result.append(new StringBuilder("Operation edge"));
        } else {
            result.append(super.getEdgeDescription());
        }
        if (AspectJModel.ROLE_DESCRIPTIONS.containsKey(this.aspect)) {
            result.append("<br>"
                + AspectJModel.ROLE_DESCRIPTIONS.get(this.aspect));
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
        if (isSourceLabel() || this.aspect == REMARK
            && !getJGraph().isShowRemarks()) {
            return Collections.emptyList();
        } else if (hasError() || getJGraph().isShowAspects()) {
            return getUserObject().toLines();
        } else {
            return super.getLines();
        }
    }

    /**
     * On demand prefixes the label with the edge's aspect values.
     */
    @Override
    public StringBuilder getLine(Edge<?> edge) {
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

    @Override
    public Collection<? extends Label> getListLabels() {
        if (hasError()) {
            return getUserObject().toLabels();
        } else if (this.aspect.isMeta()) {
            return Collections.emptySet();
        } else {
            return super.getListLabels();
        }
    }

    @Override
    public Set<? extends Label> getListLabels(Edge<?> edge) {
        AspectEdge aspectEdge = (AspectEdge) edge;
        Set<? extends Label> result;
        Label label = aspectEdge.getRuleLabel();
        if (label != null && ((RuleLabel) label).isMatchable()) {
            result = ((RuleLabel) label).getMatchExpr().getTypeLabels();
        } else {
            result = Collections.singleton(aspectEdge.getDisplayLabel());
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
        if (hasError()) {
            return false;
        }
        if (getJGraph().isShowValueNodes()) {
            return false;
        }
        if (this.aspect.isRole()) {
            // we're in a rule graph; watch for parameters and variable nodes
            return getTargetNode().getAttrAspect().hasContent()
                && !getTargetNode().hasParam();
        } else {
            return getTargetNode().getAttrKind().isTypedData();
        }
    }

    @Override
    public final boolean hasError() {
        return this.extraError || !this.errors.isEmpty();
    }

    @Override
    public void setExtraError(boolean error) {
        if (this.extraError != error) {
            this.extraError = error;
            refreshAttributes();
        }
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
            if (ruleModelLabel.isEmpty() || ruleModelLabel.isNeg()
                && ruleModelLabel.getNegOperand().isEmpty()) {
                // remove edge arrow
                GraphConstants.setLineEnd(result, GraphConstants.ARROW_NONE);
            } else if (!ruleModelLabel.isAtom()) {
                setFontAttr(result, Font.ITALIC);
            }
        }
        if (getJGraph().hasActiveEditor()) {
            GraphConstants.setEditable(result, true);
            GraphConstants.setConnectable(result, true);
            GraphConstants.setDisconnectable(result, true);
        }
        if (getSourceVertex() != null && getEdge() != null
            && getEdge().getGraphRole() != GraphRole.RULE) {
            Color typeColor =
                ((AspectJVertex) getSourceVertex()).getNodeColor();
            if (typeColor != null) {
                GraphConstants.setForeground(result, typeColor);
                GraphConstants.setLineColor(result, typeColor);
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

    private AspectKind aspect;

    private Collection<FormatError> errors = new LinkedHashSet<FormatError>();

    private boolean extraError;

    /** Returns a prototype {@link AspectJEdge} for a given {@link AspectJGraph}. */
    public static AspectJEdge getPrototype(AspectJGraph jGraph) {
        return new AspectJEdge(jGraph);
    }

    /** Separator between level name and edge label. */
    private static final char LEVEL_NAME_SEPARATOR = ':';
}