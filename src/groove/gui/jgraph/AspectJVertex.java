package groove.gui.jgraph;

import static groove.view.aspect.AspectKind.REMARK;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.GraphRole;
import groove.graph.LabelPattern;
import groove.graph.Node;
import groove.graph.TypeGraph;
import groove.graph.TypeNode;
import groove.graph.algebra.VariableNode;
import groove.gui.look.Look;
import groove.gui.look.VisualKey;
import groove.io.HTMLConverter;
import groove.util.ChangeCount;
import groove.util.ChangeCount.Tracker;
import groove.view.FormatError;
import groove.view.GraphBasedModel.TypeModelMap;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectLabel;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Specialized j-vertex for rule graphs, with its own tool tip text.
 */
public class AspectJVertex extends GraphJVertex implements AspectJCell {
    /** 
     * Creates a fresh, uninitialised JVertex.
     * Call {@link #setJModel(GraphJModel)} and {@link #setNode(Node)}
     * to initialise.
     */
    private AspectJVertex() {
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
    public AspectNode getNode() {
        return (AspectNode) super.getNode();
    }

    @Override
    public AspectKind getAspect() {
        return this.aspect;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<AspectEdge> getEdges() {
        return (Set<AspectEdge>) super.getEdges();
    }

    @Override
    protected void initialise() {
        super.initialise();
        if (getNode() != null) {
            this.aspect = getNode().getKind();
            getErrors().addErrors(getNode().getErrors(), true);
        }
        resetJModelTracker();
    }

    @Override
    public boolean addEdge(Edge edge) {
        boolean result = super.addEdge(edge);
        if (result) {
            getErrors().addErrors(((AspectEdge) edge).getErrors(), true);
        }
        return result;
    }

    @Override
    protected boolean isCompatible(Edge edge) {
        return super.isCompatible(edge)
            || ((AspectEdge) edge).getKind() == REMARK;
    }

    /** 
     * Collects a set of edges that under the current
     * display settings are also to be shown on this label.
     * These are obtained from the outgoing JEdges that
     * have this JVertex as their source label and for which
     * {@link AspectJEdge#isSourceLabel()} holds.
     */
    public Set<AspectEdge> getExtraSelfEdges() {
        Set<AspectEdge> result = new TreeSet<AspectEdge>();
        // add all outgoing JEdges that are source labels
        for (GraphJEdge edgeObject : getJEdges()) {
            AspectJEdge jEdge = (AspectJEdge) edgeObject;
            if (jEdge.getSourceVertex() == this && jEdge.isSourceLabel()) {
                result.addAll(jEdge.getEdges());
            }
        }
        return result;
    }

    /** 
     * Retrieves the node type corresponding to the node type label,
     * if the type graph is not implicit. 
     */
    private TypeNode getNodeType() {
        TypeNode result = null;
        TypeGraph typeGraph = getJModel().getTypeGraph();
        for (AspectEdge edge : getEdges()) {
            if (typeGraph.isNodeType(edge)) {
                result = typeGraph.getNode(edge.getTypeLabel());
                break;
            }
        }
        return result == null ? typeGraph.getFactory().getTopNode() : result;
    }

    void setNodeFixed() {
        getNode().setFixed();
        if (getNode().hasErrors()) {
            getErrors().addErrors(getNode().getErrors(), true);
            setStale(VisualKey.ERROR);
        }
    }

    @Override
    protected String getNodeIdString() {
        if (this.aspect.isMeta()) {
            return null;
        } else if (getNode().hasAttrAspect()) {
            AspectKind attrKind = getNode().getAttrKind();
            if (attrKind.hasSignature()) {
                Object content = getNode().getAttrAspect().getContent();
                if (content == null) {
                    return VariableNode.TO_STRING_PREFIX
                        + getNode().getNumber();
                } else {
                    return content.toString();
                }
            } else {
                assert attrKind == AspectKind.PRODUCT;
                // delegate the identity string to a corresponding product node
                return "p" + getNode().getNumber();
            }
        } else {
            return super.getNodeIdString();
        }
    }

    /**
     * This implementation prefixes the node description with an indication
     * of the role, if the model is a rule.
     */
    @Override
    StringBuilder getNodeDescription() {
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
            if (getNode().getAttrKind().hasSignature()) {
                if (getNode().getAttrAspect().hasContent()) {
                    result.append("Constant node");
                } else {
                    result.append("Variable node");
                }
            } else if (getNode().hasAttrAspect()) {
                result.append("Product node");
            } else {
                result.append(super.getNodeDescription());
            }
            if (AspectJModel.ROLE_NAMES.containsKey(this.aspect)) {
                HTMLConverter.toUppercase(result, false);
                result.insert(0, " ");
                result.insert(0, AspectJModel.ROLE_NAMES.get(this.aspect));
                result.append("<br>"
                    + AspectJModel.ROLE_DESCRIPTIONS.get(this.aspect));
            }
        }
        return result;
    }

    @Override
    public Collection<Element> getKeys() {
        updateCachedValues();
        return this.keys;
    }

    /** 
     * Updates the cached values of {@link #keys},
     * if the model has been modified in the meantime.
     */
    private void updateCachedValues() {
        if (this.keys == null || getJModelTracker().isStale()) {
            this.keys = computeKeys();
        }
    }

    /** Recomputes the set of list labels for this aspect node. */
    private Collection<Element> computeKeys() {
        getNode().testFixed(true);
        Collection<Element> result = new ArrayList<Element>();
        if (!this.aspect.isMeta()) {
            for (Edge edge : getEdges()) {
                Edge key = getKey(edge);
                if (key != null) {
                    result.add(key);
                }
            }
            for (AspectEdge edge : getExtraSelfEdges()) {
                Edge key = getKey(edge);
                if (key != null) {
                    result.add(key);
                }
            }
            Node nodeKey = getNodeKey();
            if (result.isEmpty() || nodeKey instanceof TypeNode
                && !((TypeNode) nodeKey).isTopType()) {
                result.add(nodeKey);
            }
        }
        return result;
    }

    @Override
    protected Node getNodeKey() {
        TypeModelMap typeMap = getJModel().getResourceModel().getTypeMap();
        return typeMap == null ? getNode() : typeMap.getNode(getNode());
    }

    @Override
    public Edge getKey(Edge edge) {
        TypeModelMap typeMap = getJModel().getResourceModel().getTypeMap();
        return typeMap == null ? edge : typeMap.getEdge((AspectEdge) edge);
    }

    @Override
    protected Look getStructuralLook() {
        if (isNodeEdge()) {
            return Look.NODIFIED;
        } else if (getNode().hasAttrAspect()) {
            return Look.getLookFor(getNode().getAttrKind());
        } else if (getNode().getGraphRole() == GraphRole.TYPE
            && getAspect() == AspectKind.DEFAULT) {
            return Look.TYPE;
        } else {
            return Look.getLookFor(getAspect());
        }
    }

    /** Indicates if this vertex is in fact a nodified edge. */
    public boolean isNodeEdge() {
        return getJGraph().getMode() != JGraphMode.EDIT_MODE
            && getEdgeLabelPattern() != null;
    }

    /**
     * Returns the (possibly {@code null}) edge label pattern, if
     * this node is a nodified edge. 
     */
    public LabelPattern getEdgeLabelPattern() {
        LabelPattern result = null;
        if (getNode().getGraphRole() == GraphRole.HOST) {
            TypeNode typeNode = getNodeType();
            result = typeNode.getLabelPattern();
        }
        return result;
    }

    public void saveToUserObject() {
        // collect the node and edge information
        AspectJObject userObject = getUserObject();
        userObject.clear();
        userObject.addLabels(getNode().getNodeLabels());
        userObject.addEdges(getEdges());
    }

    @Override
    public void loadFromUserObject(GraphRole role) {
        AspectNode node = new AspectNode(getNode().getNumber(), role);
        setNode(node);
        AspectParser parser = AspectParser.getInstance();
        List<AspectLabel> edgeLabels = new ArrayList<AspectLabel>();
        for (String text : getUserObject()) {
            AspectLabel label = parser.parse(text, role);
            if (label.isNodeOnly()) {
                node.setAspects(label);
            } else {
                // don't process the edge labels yet, as the node is not
                // yet completely determined
                edgeLabels.add(label);
            }
        }
        this.aspect = node.getKind();
        // collect remark edges
        StringBuilder remarkText = new StringBuilder();
        // now process the edge labels
        for (AspectLabel label : edgeLabels) {
            AspectEdge edge = new AspectEdge(node, label, node);
            edge.setFixed();
            if (edge.getAspect() != null
                && edge.getAspect().getKind() == REMARK) {
                if (remarkText.length() > 0) {
                    remarkText.append('\n');
                }
                remarkText.append(label.getInnerText());
            } else {
                boolean added = addEdge(edge);
                assert added;
            }
        }
        // turn the collected remark text into a single edge
        if (remarkText.length() > 0) {
            remarkText.insert(0, REMARK.getPrefix());
            AspectEdge edge =
                new AspectEdge(node, parser.parse(remarkText.toString(), role),
                    node);
            edge.setFixed();
            boolean added = addEdge(edge);
            assert added;
        }
        setStale(VisualKey.refreshables());
        // attributes will be refreshed upon the call to setNodeFixed()
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

    /** Resets the model tracker. */
    private void resetJModelTracker() {
        this.jModelTracker = null;
        getJModelTracker();
    }

    /** Lazily creates and returns a change tracker for the underlying JModel. */
    private Tracker getJModelTracker() {
        Tracker result = this.jModelTracker;
        if (result == null || result == ChangeCount.DUMMY_TRACKER) {
            AspectJModel jModel = getJModel();
            if (jModel == null) {
                result = ChangeCount.DUMMY_TRACKER;
            } else {
                result = jModel.getModCount().createTracker();
            }
            this.jModelTracker = result;
        }
        return result;
    }

    /** Cached tree entries. */
    private Collection<Element> keys;
    /** JModel modification tracker. */
    private Tracker jModelTracker;
    /** The role of the underlying rule node. */
    private AspectKind aspect;

    /** 
     * Returns a fresh, uninitialised instance.
     * Call {@link #setJModel(GraphJModel)} and {@link #setNode(Node)} to initialise. 
     */
    public static AspectJVertex newInstance() {
        return new AspectJVertex();
    }
}