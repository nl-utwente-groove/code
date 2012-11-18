package groove.gui.jgraph;

import static groove.view.aspect.AspectKind.ARGUMENT;
import static groove.view.aspect.AspectKind.DEFAULT;
import groove.graph.Edge;
import groove.graph.EdgeRole;
import groove.graph.GraphRole;
import groove.graph.LabelPattern;
import groove.gui.look.Look;
import groove.gui.look.VisualKey;
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Specialized j-edge for rule graphs, with its own tool tip text.
 */
public class AspectJEdge extends GraphJEdge implements AspectJCell {
    /** 
     * Creates an uninitialised instance.
     */
    private AspectJEdge() {
        setUserObject(null);
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
        return getSourceVertex() != null
            && ((AspectJVertex) getSourceVertex()).isEdge();
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

    @Override
    protected void initialise() {
        super.initialise();
        this.aspect = DEFAULT;
        if (getJModel() != null) {
            resetJModelTracker();
        }
    }

    @Override
    public boolean isCompatible(Edge edge) {
        boolean result =
            (edge instanceof AspectEdge) && super.isCompatible(edge);
        if (result) {
            AspectEdge oldEdge = getEdge();
            if (oldEdge != null) {
                result = ((AspectEdge) edge).equalsAspects(oldEdge);
            }
        }
        return result;
    }

    /**
     * Returns <tt>true</tt> only if the aspect values of the edge to be
     * added equal those of this j-edge, and the superclass is also willing.
     */
    @Override
    public void addEdge(Edge e) {
        AspectEdge edge = (AspectEdge) e;
        AspectEdge oldEdge = getEdge();
        if (oldEdge == null) {
            this.aspect = edge.getKind();
        }
        FormatError error = null;
        if (edge.getRole() != EdgeRole.BINARY) {
            error =
                new FormatError("Node label '%s' not allowed on edges",
                    edge.label(), this);
        } else if (oldEdge != null && !edge.equalsAspects(oldEdge)) {
            error =
                new FormatError("Conflicting aspects in edge labels %s and %s",
                    oldEdge.label(), edge.label(), this);
        }
        if (error != null) {
            edge = new AspectEdge(edge.source(), edge.label(), edge.target());
            edge.addError(error);
            edge.setFixed();
        }
        super.addEdge(edge);
        updateLook(edge);
    }

    /** Update this cell's look due to the addition of an edge. */
    private void updateLook(AspectEdge edge) {
        // maybe update the look 
        RuleLabel ruleLabel = edge.getRuleLabel();
        if (ruleLabel != null) {
            if (ruleLabel.isEmpty() && this.aspect != AspectKind.CREATOR
                || ruleLabel.isNeg() && ruleLabel.getNegOperand().isEmpty()) {
                // remove edge arrow
                setLook(Look.NO_ARROW, true);
            } else if (!ruleLabel.isAtom()) {
                setLook(Look.REGULAR, true);
            }
        }
        getErrors().addErrors(edge.getErrors(), true);
        setStale(VisualKey.ERROR);
    }

    @Override
    public String getText() {
        String result = null;
        // if both source and target nodes are nodified, 
        // test for source node first
        if (isNodeEdgeOut()) {
            result = "";
        } else if (isNodeEdgeIn()) {
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
        } else {
            result = super.getText();
        }
        return result;
    }

    @Override
    StringBuilder getEdgeDescription() {
        getEdge().testFixed(true);
        StringBuilder result = new StringBuilder();
        if (hasErrors()) {
            for (FormatError error : getErrors()) {
                if (result.length() > 0) {
                    result.append("<br>");
                }
                result.append(error.toString());
            }
            HTMLConverter.EMBARGO_TAG.on(result);
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
        if (this.keys == null || this.jModelTracker.isStale()) {
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
            return !getTargetVertex().getVisuals().isVisible();
        }
        return getTargetNode().getAttrKind().hasSignature();
    }

    @Override
    protected Look getStructuralLook() {
        if (isNodeEdgeIn()) {
            return Look.NODIFIED;
        } else {
            return Look.getLookFor(getAspect());
        }
    }

    public void saveToUserObject() {
        // collect the edge information
        AspectJObject userObject = getUserObject();
        userObject.clear();
        userObject.addEdges(getEdges());
    }

    @Override
    public void loadFromUserObject(GraphRole role) {
        initialise();
        AspectParser parser = AspectParser.getInstance();
        for (String text : getUserObject()) {
            AspectLabel label = parser.parse(text, role);
            AspectEdge edge =
                new AspectEdge(getSourceNode(), label, getTargetNode());
            edge.setFixed();
            addEdge(edge);
        }
        setStale(VisualKey.refreshables());
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
     */
    private void resetJModelTracker() {
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

    /** 
     * Returns a fresh, uninitialised instance.
     * Call {@link #setJModel(GraphJModel)} to initialise. 
     */
    public static AspectJEdge newInstance() {
        return new AspectJEdge();
    }
}