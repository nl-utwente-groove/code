package groove.gui.jgraph;

import static groove.grammar.aspect.AspectKind.REMARK;
import static groove.gui.look.VisualKey.COLOR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import groove.algebra.syntax.Expression;
import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.AspectKind;
import groove.grammar.aspect.AspectLabel;
import groove.grammar.aspect.AspectNode;
import groove.grammar.aspect.AspectParser;
import groove.grammar.model.GraphBasedModel.TypeModelMap;
import groove.grammar.rule.VariableNode;
import groove.grammar.type.LabelPattern;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeElement;
import groove.grammar.type.TypeNode;
import groove.graph.Edge;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.Node;
import groove.gui.look.Look;
import groove.gui.look.VisualKey;
import groove.io.HTMLConverter;
import groove.util.parse.FormatError;

/**
 * Specialized j-vertex for rule graphs, with its own tool tip text.
 */
public class AspectJVertex extends AJVertex<AspectGraph,AspectJGraph,AspectJModel,AspectJEdge>
    implements AspectJCell {
    /**
     * Creates a fresh, uninitialised JVertex.
     * Call {@link #setJModel} and {@link #setNode(Node)}
     * to initialise.
     */
    private AspectJVertex() {
        setUserObject(null);
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
    //
    //    @Override
    //    public void setNode(Node node) {
    //        AspectNode aspectNode = (AspectNode) node;
    //        this.aspect = aspectNode.getKind();
    //        super.setNode(node);
    //        if (aspectNode.hasAttrAspect()) {
    //            setLook(Look.getLookFor(getNode().getAttrKind()), true);
    //        }
    //        getErrors().addErrors(aspectNode.getErrors(), true);
    //        refreshVisual(COLOR);
    //    }

    @Override
    public void initialise() {
        super.initialise();
        AspectNode node = getNode();
        this.aspect = node.getKind();
        if (node.hasAttrAspect()) {
            setLook(Look.getLookFor(getNode().getAttrKind()), true);
        }
        getErrors().addErrors(node.getErrors(), true);
        refreshVisual(COLOR);
    }

    @Override
    public void addEdge(Edge edge) {
        super.addEdge(edge);
        getErrors().addErrors(((AspectEdge) edge).getErrors(), true);
    }

    @Override
    public boolean isCompatible(Edge edge) {
        if (super.isCompatible(edge)) {
            return true;
        } else if (((AspectEdge) edge).getKind() == REMARK) {
            return edge.source() == getNode() && edge.target() == getNode();
        }
        return false;
    }

    /**
     * Collects a set of edges that under the current
     * display settings are also to be shown on this label.
     * These are obtained from the outgoing JEdges that
     * have this JVertex as their source label and for which
     * {@link AspectJEdge#isSourceLabel()} holds.
     */
    public Set<AspectEdge> getExtraSelfEdges() {
        Set<AspectEdge> result = createEdgeSet();
        // add all outgoing JEdges that are source labels
        Iterator<? extends AspectJEdge> iter = getContext();
        while (iter.hasNext()) {
            AspectJEdge jEdge = iter.next();
            if (jEdge.getSourceVertex() == this && jEdge.isSourceLabel()) {
                result.addAll(jEdge.getEdges());
            }
        }
        return result;
    }

    void setNodeFixed() {
        getNode().setFixed();
        if (getNode().hasErrors()) {
            getErrors().addErrors(getNode().getErrors(), true);
            setStale(VisualKey.ERROR);
        }
    }

    @Override
    public String getNodeIdString() {
        if (this.aspect.isMeta()) {
            return null;
        } else if (getNode().hasAttrAspect()) {
            AspectKind attrKind = getNode().getAttrKind();
            if (attrKind.hasSort()) {
                // this is a constant or variable node
                Object content = getNode().getAttrAspect()
                    .getContent();
                if (content == null) {
                    return VariableNode.TO_STRING_PREFIX + getNode().getNumber();
                } else if (content instanceof Expression) {
                    return ((Expression) content).toDisplayString();
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
            if (getNode().getAttrKind()
                .hasSort()) {
                if (getNode().getAttrAspect()
                    .hasContent()) {
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
                result.append("<br>" + AspectJModel.ROLE_DESCRIPTIONS.get(this.aspect));
            }
        }
        return result;
    }

    @Override
    public Collection<? extends Label> getKeys() {
        getNode().testFixed(true);
        Collection<TypeElement> result = new ArrayList<>();
        if (!this.aspect.isMeta()) {
            for (Edge edge : getEdges()) {
                TypeEdge key = getKey(edge);
                if (key != null) {
                    result.add(key);
                }
            }
            for (AspectEdge edge : getExtraSelfEdges()) {
                TypeEdge key = getKey(edge);
                if (key != null) {
                    result.add(key);
                }
            }
            result.addAll(getNodeKeys(!result.isEmpty()));
        }
        return result;
    }

    @Override
    protected Collection<TypeNode> getNodeKeys(boolean hasEdgeKeys) {
        List<TypeNode> result = new ArrayList<>();
        TypeModelMap typeMap = getTypeMap();
        if (typeMap != null) {
            TypeNode type = typeMap.getNode(getNode());
            if (type != null && (!hasEdgeKeys || !type.isTopType())) {
                result.addAll(type.getSupertypes());
            }
        }
        return result;
    }

    @Override
    public TypeEdge getKey(Edge edge) {
        TypeModelMap typeMap = getTypeMap();
        return typeMap == null ? null : typeMap.getEdge(edge);
    }

    private TypeModelMap getTypeMap() {
        return getJModel().getResourceModel()
            .getTypeMap();
    }

    @Override
    protected Look getStructuralLook() {
        if (isNodeEdge()) {
            return Look.NODIFIED;
        } else if (getNode().getGraphRole() == GraphRole.TYPE
            && getAspect() == AspectKind.DEFAULT) {
            return Look.TYPE;
        } else {
            return Look.getLookFor(getAspect());
        }
    }

    /** Indicates if this vertex is in fact a nodified edge. */
    public boolean isNodeEdge() {
        JGraph<?> jGraph = getJGraph();
        return jGraph != null && jGraph.getMode() != JGraphMode.EDIT_MODE
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
            if (typeNode != null) {
                result = typeNode.getLabelPattern();
            }
        }
        return result;
    }

    /**
     * Retrieves the node type corresponding to the node type label.
     * The node type may be {@code null} if the graph has typing errors.
     */
    public TypeNode getNodeType() {
        TypeModelMap typeMap = getTypeMap();
        return typeMap == null ? null : typeMap.getNode(getNode());
    }

    @Override
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
        AspectParser parser = AspectParser.getInstance();
        List<AspectLabel> edgeLabels = new ArrayList<>();
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
        // collect remark edges
        StringBuilder remarkText = new StringBuilder();
        // collect edges to be added explicitly
        List<AspectEdge> newEdges = new ArrayList<>();
        // now process the edge labels
        int remarkCount = 0;
        for (AspectLabel label : edgeLabels) {
            int nr = 0;
            if (label.containsAspect(REMARK)) {
                nr = remarkCount;
                remarkCount++;
            }
            AspectEdge edge = new AspectEdge(node, label, node, nr);
            newEdges.add(edge);
        }
        // turn the collected remark text into a single edge
        if (remarkText.length() > 0) {
            remarkText.insert(0, REMARK.getPrefix());
            AspectEdge edge = new AspectEdge(node, parser.parse(remarkText.toString(), role), node);
            edge.setFixed();
            newEdges.add(edge);
        }
        setNode(node);
        initialise();
        for (AspectEdge edge : newEdges) {
            addEdge(edge);
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

    /** The role of the underlying rule node. */
    private AspectKind aspect;

    /**
     * Returns a fresh, uninitialised instance.
     * Call {@link #setJModel} and {@link #setNode(Node)} to initialise.
     */
    public static AspectJVertex newInstance() {
        return new AspectJVertex();
    }
}