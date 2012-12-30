package groove.gui.jgraph;

import static groove.gui.look.VisualKey.COLOR;
import static groove.view.aspect.AspectKind.ARGUMENT;
import static groove.view.aspect.AspectKind.DEFAULT;
import groove.graph.Edge;
import groove.graph.EdgeRole;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.TypeEdge;
import groove.gui.look.Look;
import groove.gui.look.VisualKey;
import groove.io.HTMLConverter;
import groove.trans.RuleLabel;
import groove.view.FormatError;
import groove.view.GraphBasedModel.TypeModelMap;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectLabel;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectParser;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Specialized j-edge for rule graphs, with its own tool tip text.
 */
public class AspectJEdge extends JEdge<AspectGraph> implements AspectJCell {
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

    @SuppressWarnings("unchecked")
    @Override
    public Collection<AspectJVertex> getContext() {
        return (Collection<AspectJVertex>) super.getContext();
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
            && ((AspectJVertex) getTargetVertex()).isNodeEdge();
    }

    /** Indicates if this is the incoming pars of a nodified edge. */
    public boolean isNodeEdgeOut() {
        return getSourceVertex() != null
            && ((AspectJVertex) getSourceVertex()).isNodeEdge();
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
    }

    @Override
    public boolean isCompatible(Edge edge) {
        if (!super.isCompatible(edge)) {
            return false;
        }
        if (!((AspectEdge) edge).equalsAspects(getEdge())) {
            return false;
        }
        return true;
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
        refreshVisual(COLOR);
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
        if (edge.isComposite()) {
            setLook(Look.COMPOSITE, true);
        }
        getErrors().addErrors(edge.getErrors(), true);
        setStale(VisualKey.ERROR);
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
    public Collection<? extends Label> getKeys() {
        if (this.aspect.isMeta()) {
            return Collections.emptySet();
        } else {
            return super.getKeys();
        }
    }

    @Override
    public TypeEdge getKey(Edge edge) {
        TypeEdge result = null;
        TypeModelMap typeMap = getTypeMap();
        if (typeMap != null) {
            result = typeMap.getEdge((AspectEdge) edge);
        }
        return result;
    }

    private TypeModelMap getTypeMap() {
        return getJModel().getResourceModel().getTypeMap();
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
        if (!getTargetNode().getAttrKind().hasSignature()) {
            return false;
        }
        if (getTargetNode().hasParam()) {
            return false;
        }
        if (getJGraph().getGraphRole() != GraphRole.TYPE
            && !getTargetNode().getAttrAspect().hasContent()) {
            return false;
        }
        return true;
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

    private AspectKind aspect;

    /** 
     * Returns a fresh, uninitialised instance.
     * Call {@link #setJModel} to initialise. 
     */
    @SuppressWarnings("unchecked")
    public static AspectJEdge newInstance() {
        return new AspectJEdge();
    }
}