package groove.gui.jgraph;

import static groove.grammar.aspect.AspectKind.ARGUMENT;
import static groove.grammar.aspect.AspectKind.DEFAULT;
import static groove.gui.look.VisualKey.COLOR;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.AspectKind;
import groove.grammar.aspect.AspectLabel;
import groove.grammar.aspect.AspectNode;
import groove.grammar.aspect.AspectParser;
import groove.grammar.model.GraphBasedModel.TypeModelMap;
import groove.grammar.rule.RuleLabel;
import groove.grammar.type.TypeEdge;
import groove.graph.Edge;
import groove.graph.EdgeComparator;
import groove.graph.EdgeRole;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.gui.look.Look;
import groove.gui.look.VisualKey;
import groove.io.HTMLConverter;
import groove.util.parse.FormatError;

/**
 * Specialized j-edge for rule graphs, with its own tool tip text.
 */
public class AspectJEdge extends AJEdge<AspectGraph,AspectJGraph,AspectJModel,AspectJVertex>
    implements AspectJCell {
    /**
     * Creates an uninitialised instance.
     */
    private AspectJEdge() {
        setUserObject(null);
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
        AspectJVertex target = getTargetVertex();
        return target != null && target.isNodeEdge();
    }

    /** Indicates if this is the incoming pars of a nodified edge. */
    public boolean isNodeEdgeOut() {
        AspectJVertex source = getSourceVertex();
        return source != null && source.isNodeEdge();
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
    public void initialise() {
        super.initialise();
        this.aspect = DEFAULT;
    }

    @Override
    public boolean isCompatible(Edge edge) {
        if (!super.isCompatible(edge)) {
            return false;
        }
        if (!((AspectEdge) edge).isCompatible(getEdge())) {
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
        if (oldEdge == null || this.aspect == AspectKind.REMARK) {
            this.aspect = edge.getKind();
        }
        FormatError error = null;
        if (edge.getRole() != EdgeRole.BINARY) {
            error = new FormatError("Node label '%s' not allowed on edges", edge.label(), this);
        } else if (oldEdge != null && !edge.isCompatible(oldEdge)) {
            error = new FormatError("Conflicting aspects in edge labels %s and %s", oldEdge.label(),
                edge.label(), this);
        }
        if (error != null) {
            edge = new AspectEdge(edge.source(), edge.label(), edge.target(), edge.getNumber());
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
                || ruleLabel.isNeg() && ruleLabel.getNegOperand()
                    .isEmpty()) {
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
            } else if (attrKind.hasSort()) {
                result.append(new StringBuilder("Operation edge"));
            } else {
                result.append(super.getEdgeDescription());
            }
            if (AspectJModel.ROLE_DESCRIPTIONS.containsKey(this.aspect)) {
                result.append("<br>" + AspectJModel.ROLE_DESCRIPTIONS.get(this.aspect));
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
            result = typeMap.getEdge(edge);
        }
        return result;
    }

    private TypeModelMap getTypeMap() {
        return getJModel().getResourceModel()
            .getTypeMap();
    }

    /**
     * Indicates if this JEdge should be shown
     * instead as part of the source node label.
     * This is true if this is an attribute edge to a "pure" value node,
     * and value nodes are not shown.
     */
    public boolean isSourceLabel() {
        AspectJGraph graph = getJGraph();
        if (graph != null && graph.isShowValueNodes()) {
            return false;
        }
        if (getSourceNode().getAttrKind()
            .hasSort()) {
            return false;
        }
        if (!getTargetNode().getAttrKind()
            .hasSort()) {
            return false;
        }
        if (getTargetNode().hasParam()) {
            return false;
        }
        if (graph != null && graph.getGraphRole() != GraphRole.TYPE
            && !getTargetNode().getAttrAspect()
                .hasContent()) {
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

    @Override
    public void saveToUserObject() {
        // collect the edge information
        AspectJObject userObject = getUserObject();
        userObject.clear();
        userObject.addEdges(getEdges());
    }

    @Override
    public void loadFromUserObject(GraphRole role) {
        boolean bidirectional = getLooks().contains(Look.BIDIRECTIONAL);
        initialise();
        AspectParser parser = AspectParser.getInstance();
        for (String text : getUserObject()) {
            AspectLabel label = parser.parse(text, role);
            AspectEdge edge = new AspectEdge(getSourceNode(), label, getTargetNode());
            edge.setFixed();
            addEdge(edge);
            if (bidirectional) {
                edge = new AspectEdge(getTargetNode(), label, getSourceNode());
                edge.setFixed();
                addEdge(edge);
            }
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

    @Override
    protected Comparator<Edge> edgeComparator() {
        return COMPARATOR;
    }

    private final static Comparator<Edge> COMPARATOR = new Comparator<Edge>() {
        @Override
        public int compare(Edge o1, Edge o2) {
            int result = getKind(o1).compareTo(getKind(o2));
            if (result != 0) {
                return result;
            }
            return EdgeComparator.instance()
                .compare(o1, o2);
        }

        private AspectKind getKind(Edge e) {
            AspectKind result = ((AspectEdge) e).getKind();
            if (result == null) {
                result = DEFAULT;
            }
            return result;
        }
    };

    /**
     * Returns a fresh, uninitialised instance.
     * Call {@link #setJModel} to initialise.
     */
    public static AspectJEdge newInstance() {
        return new AspectJEdge();
    }
}