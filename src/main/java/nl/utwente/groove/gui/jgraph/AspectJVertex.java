package nl.utwente.groove.gui.jgraph;

import static nl.utwente.groove.grammar.aspect.AspectKind.PRODUCT;
import static nl.utwente.groove.grammar.aspect.AspectKind.REMARK;
import static nl.utwente.groove.gui.look.VisualKey.COLOR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.gui.view.EditableLabels;
import nl.utwente.groove.gui.view.GraphViewMode;
import nl.utwente.groove.grammar.aspect.Aspect;
import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectElement;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.aspect.AspectLabel;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.aspect.AspectParser;
import nl.utwente.groove.grammar.model.GraphBasedModel;
import nl.utwente.groove.grammar.model.GraphBasedModel.TypeModelMap;
import nl.utwente.groove.grammar.type.TypeGraph;
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
import nl.utwente.groove.gui.look.Values;
import nl.utwente.groove.util.HTMLConverter;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.gui.view.AspectViewCellErrors;
import nl.utwente.groove.gui.view.AspectViewVertex;
import nl.utwente.groove.gui.view.ViewVertex;

/**
 * Specialized j-vertex for rule graphs, with its own tool tip text.
 */
public class AspectJVertex extends
    AJVertex<@NonNull AspectGraph,AspectJGraph,AspectJModel,AspectJEdge> implements AspectViewVertex {
    /**
     * Creates a fresh, uninitialised ViewVertex.
     * Call {@link #setJModel} and {@link #setNode(Node)}
     * to initialise.
     * @param graphRole graph role for which this ViewEdge is intended
     */
    private AspectJVertex(GraphRole graphRole) {
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

    @Override
    public void initialise() {
        super.initialise();
        this.errors.clear();
        AspectNode node = getNode();
        getAspects().putAll(node.getAspects());
        var data = node.getKind(Category.SORT);
        if (data != null) {
            setLook(Look.getLookFor(data), true);
        } else if (node.has(PRODUCT)) {
            setLook(Look.getLookFor(PRODUCT), true);
        }
        getErrors().addErrors(node.getErrors(), true);
        setStale(COLOR);
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
     * have this ViewVertex as their source label and for which
     * {@link AspectJEdge#isSourceLabel()} holds.
     */
    @Override
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

    @Override
    public void setNodeFixed() {
        getNode().setFixed();
        if (getNode().hasErrors()) {
            getErrors().addErrors(getNode().getErrors(), true);
            setStale(VisualKey.ERROR);
        }
    }

    @Override
    public String getNodeIdString() {
        if (getAspects().containsKey(Category.NESTING)) {
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
    @Override
    StringBuilder getNodeDescription() {
        StringBuilder result = new StringBuilder();
        if (hasErrors()) {
            for (FormatError error : getErrors()) {
                if (result.length() > 0) {
                    result.append("<br>");
                }
                result.append(Values.getSeverityTag(error.getSeverity()).on(error.toString()));
            }
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
    public Collection<? extends Label> getLabels() {
        Collection<TypeElement> result = new ArrayList<>();
        if (!getAspects().containsKey(Category.NESTING)) {
            for (Edge edge : getEdges()) {
                TypeEdge key = getKey(edge);
                if (key != null) {
                    result.add(key);
                }
            }
            // add additional self-edges
            for (var edge : getExtraSelfEdges()) {
                TypeEdge key = getKey(edge);
                if (key != null) {
                    result.add(key);
                }
            }
            result.addAll(getNodeKeys());
        }
        return result;
    }

    @Override
    public Collection<? extends Label> getKeys() {
        Collection<TypeElement> result = new ArrayList<>();
        if (!getAspects().containsKey(Category.NESTING)) {
            for (Edge edge : getEdges()) {
                TypeEdge key = getKey(edge);
                if (key != null) {
                    result.add(key);
                }
            }
            // add incident edges
            var jEdges = getContext();
            while (jEdges.hasNext()) {
                for (var edge : jEdges.next().getEdges()) {
                    TypeEdge key = getKey(edge);
                    if (key != null) {
                        result.add(key);
                    }
                }
            }
            result.addAll(getNodeKeys());
        }
        return result;
    }

    @Override
    protected Collection<TypeNode> getNodeKeys() {
        List<TypeNode> result = new ArrayList<>();
        TypeModelMap typeMap = getTypeMap();
        if (typeMap != null) {
            TypeNode type = typeMap.getNode(getNode());
            if (type != null) {
                result.add(type);
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
        return getResourceModel().getTypeMap();
    }

    @Override
    public GraphBasedModel<?> getResourceModel() {
        return getJModel().getResourceModel();
    }

    @Override
    public TypeGraph getTypeGraph() {
        return getJModel().getTypeGraph();
    }

    @Override
    protected Set<Look> getStructuralLooks() {
        if (isNodeEdge()) {
            return EnumSet.of(Look.NODIFIED);
        } else if (getNode().hasGraphRole(GraphRole.TYPE)
            && !getAspects().containsKey(Category.SORT)) {
            return getNode().has(REMARK)
                ? EnumSet.of(Look.TYPE_REMARK)
                : EnumSet.of(Look.TYPE);
        } else {
            return Look.getLooksFor(getAspects());
        }
    }

    /** Indicates if this vertex is in fact a nodified edge. */
    @Override
    public boolean isNodeEdge() {
        JGraph<?> jGraph = getJGraph();
        return jGraph != null && jGraph.getMode() != GraphViewMode.EDIT_MODE
            && getEdgeLabelPattern() != null;
    }

    /**
     * Returns the (possibly {@code null}) edge label pattern, if
     * this node is a nodified edge.
     */
    @Override
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
    @Override
    public TypeNode getNodeType() {
        TypeModelMap typeMap = getTypeMap();
        return typeMap == null
            ? null
            : typeMap.getNode(getNode());
    }

    @Override
    public void refreshEditableLabels() {
        // collect the node and edge information
        var labels = getEditableLabels();
        labels.clear();
        labels.addLabels(getNode().getNodeLabels());
        labels.addEdges(getEdges());
    }

    @Override
    public void applyEditableLabels(AspectGraph graph) {
        AspectNode node = new AspectNode(getNode().getNumber(), graph);
        AspectParser parser = AspectParser.getInstance();
        List<AspectLabel> edgeLabels = new ArrayList<>();
        for (String text : getEditableLabels()) {
            AspectLabel label = parser.parse(text, graph.getRole());
            if (label.isNodeOnly()) {
                node.addLabel(label);
            } else {
                // don't process the edge labels yet, as the node is not
                // yet completely determined
                edgeLabels.add(label);
            }
        }
        node.setParsed();
        // collect remark edges
        boolean hasRemark = false;
        StringBuilder remarkText = new StringBuilder();
        // collect edges to be added explicitly
        List<AspectEdge> newEdges = new ArrayList<>();
        // now process the edge labels
        for (AspectLabel label : edgeLabels) {
            if (label.has(REMARK)) {
                if (hasRemark) {
                    remarkText.append(EditableLabels.NEWLINE);
                }
                remarkText.append(label.getInnerText());
                hasRemark = true;
            } else {
                AspectEdge edge = new AspectEdge(node, label, node);
                newEdges.add(edge);
            }
        }
        // turn the collected remark text into a single edge
        if (hasRemark) {
            remarkText.insert(0, REMARK.getPrefix());
            AspectEdge edge
                = new AspectEdge(node, parser.parse(remarkText.toString(), graph.getRole()), node);
            newEdges.add(edge);
        }
        setNode(node);
        initialise();
        for (AspectEdge edge : newEdges) {
            edge.setParsed();
            addEdge(edge);
        }
        setStale(VisualKey.refreshables());
        // attributes will be refreshed upon the call to setNodeFixed()
    }

    @Override
    public EditableLabels getEditableLabels() {
        return this.labels;
    }

    @Override
    public void setEditableLabels(EditableLabels labels) {
        // a fresh object is needed, otherwise undo does not work
        this.labels = new EditableLabels(labels);
    }

    /** The editable labels of this cell. */
    private EditableLabels labels = new EditableLabels();

    /*
     * JGraph applies edited cell values through the user object
     * (DefaultGraphModel.valueForCellChanged); redirected to the editable labels.
     * A string value is the text of the in-place editor.
     */
    @Override
    public void setUserObject(Object value) {
        if (value instanceof EditableLabels o) {
            setEditableLabels(o);
        } else {
            var labels = new EditableLabels();
            if (value != null) {
                labels.load(value.toString());
            }
            this.labels = labels;
        }
    }

    /* JGraph reads the cell value through the user object; see setUserObject. */
    @Override
    public EditableLabels getUserObject() {
        return this.labels;
    }

    @Override
    public ViewVertex<@NonNull AspectGraph> clone() {
        AspectJVertex result = (AspectJVertex) super.clone();
        result.labels = new EditableLabels(this.labels);
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <E extends Edge> Comparator<E> edgeComparator() {
        return (Comparator<E>) EDGE_COMPARATOR;
    }

    /**
     * Returns a fresh, uninitialised instance.
     * Call {@link #setJModel} and {@link #setNode(Node)} to initialise.
     * @param graphRole the graph role for which the new edge will serve
     */
    public static AspectJVertex newInstance(GraphRole graphRole) {
        return new AspectJVertex(graphRole);
    }

    @Override
    public AspectViewCellErrors getErrors() {
        return this.errors;
    }

    /** Object containing this cell's errors, if any. */
    private final AspectViewCellErrors errors = new AspectViewCellErrors(this);
}