package nl.utwente.groove.gui.jgraph;

import static nl.utwente.groove.grammar.aspect.AspectKind.PRODUCT;
import static nl.utwente.groove.grammar.aspect.AspectKind.REMARK;
import static nl.utwente.groove.gui.look.VisualKey.COLOR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nl.utwente.groove.grammar.aspect.Aspect;
import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectElement;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.aspect.AspectLabel;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.aspect.AspectParser;
import nl.utwente.groove.grammar.model.GraphBasedModel.TypeModelMap;
import nl.utwente.groove.grammar.rule.OperatorNode;
import nl.utwente.groove.grammar.rule.VariableNode;
import nl.utwente.groove.grammar.type.LabelPattern;
import nl.utwente.groove.grammar.type.TypeEdge;
import nl.utwente.groove.grammar.type.TypeElement;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.graph.Edge;
import nl.utwente.groove.graph.GraphRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.graph.Node;
import nl.utwente.groove.gui.look.Look;
import nl.utwente.groove.gui.look.VisualKey;
import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.util.parse.FormatError;

/**
 * Specialized j-vertex for rule graphs, with its own tool tip text.
 */
public class AspectJVertex extends AJVertex<AspectGraph,AspectJGraph,AspectJModel,AspectJEdge>
    implements AspectJCell {
    /**
     * Creates a fresh, uninitialised JVertex.
     * Call {@link #setJModel} and {@link #setNode(Node)}
     * to initialise.
     * @param graphRole graph role for which this JEdge is intended
     */
    private AspectJVertex(GraphRole graphRole) {
        setUserObject(null);
        this.aspects = new Aspect.Map(true, graphRole);
    }

    @Override
    public AspectNode getNode() {
        return (AspectNode) super.getNode();
    }

    @Override
    public Aspect.Map getAspects() {
        return this.aspects;
    }

    /** The role of the underlying rule node. */
    private final Aspect.Map aspects;

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
        var data = node.getKind(Category.SORT);
        if (data != null) {
            setLook(Look.getLookFor(data), true);
        } else if (node.has(PRODUCT)) {
            setLook(Look.getLookFor(PRODUCT), true);
        }
        getErrors().addErrors(node.getErrors(), true);
        refreshVisual(COLOR);
    }

    @Override
    public void addEdge(Edge edge) {
        super.addEdge(edge);
        getErrors().addErrors(((AspectElement) edge).getErrors(), true);
    }

    @Override
    public boolean isCompatible(Edge edge) {
        if (super.isCompatible(edge)) {
            return true;
        } else if (((AspectEdge) edge).has(REMARK)) {
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
        if (getAspects().containsKey(Category.META)) {
            return null;
        } else if (getNode().has(Category.SORT)) {
            // this is an expression or variable node
            if (getNode().hasValue()) {
                return null;
            } else {
                return VariableNode.TO_STRING_PREFIX + getNode().getNumber();
            }
        } else if (getNode().has(PRODUCT)) {
            // delegate the identity string to a corresponding product node
            return OperatorNode.TO_STRING_PREFIX + getNode().getNumber();
        } else {
            return super.getNodeIdString();
        }
    }

    /**
     * This implementation prefixes the node description with an indication
     * of the role, if the model is a rule.
     */
    @SuppressWarnings("null")
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
            if (getNode().has(Category.SORT)) {
                if (getNode().hasValue()) {
                    result.append("Expression node");
                } else {
                    result.append("Variable node");
                }
            } else if (getNode().has(PRODUCT)) {
                result.append("Product node");
            } else {
                result.append(super.getNodeDescription());
            }
            var roleAspect = getAspects().get(Category.ROLE);
            if (roleAspect != null) {
                HTMLConverter.toUppercase(result, false);
                result.insert(0, " ");
                result.insert(0, AspectJModel.ROLE_NAMES.get(roleAspect.getKind()));
                result.append("<br>" + AspectJModel.ROLE_DESCRIPTIONS.get(roleAspect.getKind()));
            }
        }
        return result;
    }

    @Override
    public Collection<? extends Label> getKeys() {
        getNode().testFixed(true);
        Collection<TypeElement> result = new ArrayList<>();
        if (!getAspects().containsKey(Category.META)) {
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
        return typeMap == null
            ? null
            : typeMap.getEdge(edge);
    }

    private TypeModelMap getTypeMap() {
        return getJModel().getResourceModel().getTypeMap();
    }

    @Override
    protected Set<Look> getStructuralLooks() {
        if (isNodeEdge()) {
            return EnumSet.of(Look.NODIFIED);
        } else if (getNode().hasGraphRole(GraphRole.TYPE)
            && !getAspects().containsKey(Category.SORT)) {
            return EnumSet.of(Look.TYPE);
        } else {
            return Look.getLooksFor(getAspects());
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
        return typeMap == null
            ? null
            : typeMap.getNode(getNode());
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
    public void loadFromUserObject(AspectGraph graph) {
        AspectNode node = new AspectNode(getNode().getNumber(), graph);
        AspectParser parser = AspectParser.getInstance();
        List<AspectLabel> edgeLabels = new ArrayList<>();
        for (String text : getUserObject()) {
            AspectLabel label = parser.parse(text, graph.getRole());
            if (label.isNodeOnly()) {
                node.addLabel(label);
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
            if (label.has(REMARK)) {
                nr = remarkCount;
                remarkCount++;
            }
            AspectEdge edge = new AspectEdge(node, label, node, nr);
            newEdges.add(edge);
        }
        // turn the collected remark text into a single edge
        if (remarkText.length() > 0) {
            remarkText.insert(0, REMARK.getPrefix());
            AspectEdge edge
                = new AspectEdge(node, parser.parse(remarkText.toString(), graph.getRole()), node);
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
        if (value instanceof AspectJObject o) {
            myObject.addAll(o);
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
     * Returns a fresh, uninitialised instance.
     * Call {@link #setJModel} and {@link #setNode(Node)} to initialise.
     * @param graphRole the graph role for which the new edge will serve
     */
    public static AspectJVertex newInstance(GraphRole graphRole) {
        return new AspectJVertex(graphRole);
    }
}