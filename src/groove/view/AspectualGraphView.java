/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: AspectualGraphView.java,v 1.18 2008-01-30 09:33:25 iovka Exp $
 */
package groove.view;

import static groove.view.aspect.AttributeAspect.getAttributeValue;
import groove.algebra.AlgebraRegister;
import groove.graph.DefaultLabel;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphInfo;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.TypeGraph;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.ValueNode;
import groove.rel.NodeRelation;
import groove.rel.SetNodeRelation;
import groove.trans.SystemProperties;
import groove.util.Pair;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectValue;
import groove.view.aspect.AttributeAspect;
import groove.view.aspect.AttributeElementFactory;
import groove.view.aspect.RuleAspect;
import groove.view.aspect.TypeAspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Aspectual view upon an attributed graph. The attribute values are represented
 * by {@link ValueNode}s with self-{@link OperatorEdge}s.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectualGraphView implements GraphView {
    /**
     * Constructs an instance from a given aspect graph view.
     * @see GraphInfo#getName(groove.graph.GraphShape)
     */
    public AspectualGraphView(AspectGraph view, SystemProperties properties) {
        this.view = view;
        this.attributeFactory = createAttributeFactory(properties);
        // we fix the view; is it conceptually right to do that here?
        view.setFixed();
        String name = GraphInfo.getName(view);
        this.name = name == null ? "" : name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public AspectGraph getView() {
        return this.view;
    }

    public Graph toModel() throws FormatException {
        initialise();
        if (this.model == null) {
            throw new FormatException(getErrors());
        } else {
            return this.model;
        }
    }

    public List<String> getErrors() {
        initialise();
        return this.errors;
    }

    @Override
    public NodeEdgeMap getMap() {
        initialise();
        return this.viewToModelMap;
    }

    /**
     * Changes the system properties under which the model is to be created.
     */
    public void setProperties(SystemProperties properties) {
        AttributeElementFactory newFactory = createAttributeFactory(properties);
        if (!newFactory.equals(this.attributeFactory)) {
            this.attributeFactory = newFactory;
            invalidate();
        }
    }

    /** Changes the type graph under against which the model should be tested. */
    @Override
    public void setType(TypeGraph type) {
        if (this.type != type) {
            this.type = type;
            invalidate();
        }
    }

    /** Returns the set of labels used in this graph. */
    public Set<Label> getLabels() {
        initialise();
        return this.labelSet;
    }

    /** Constructs the model and associated data structures from the view. */
    private void initialise() {
        // first test if there is something to be done
        if (this.errors == null) {
            this.labelSet = new HashSet<Label>();
            Pair<Graph,NodeEdgeMap> modelPlusMap = computeModel(this.view);
            this.model = modelPlusMap.first();
            this.viewToModelMap = modelPlusMap.second();
            this.errors = GraphInfo.getErrors(this.model);
        }
    }

    /**
     * Resets the constructed fields of this view to {@code null}, so that they
     * will be reconstructed again.
     */
    private void invalidate() {
        this.errors = null;
        this.model = null;
        this.viewToModelMap = null;
        this.labelSet = null;
    }

    /** Factory method. */
    private AttributeElementFactory createAttributeFactory(
            SystemProperties properties) {
        return new AttributeElementFactory(this.view, properties);
    }

    /**
     * Computes a fresh model from a given aspect graph, together with a mapping
     * from the aspect graph's node to the (fresh) graph nodes.
     */
    private Pair<Graph,NodeEdgeMap> computeModel(AspectGraph view) {
        Set<String> errors = new TreeSet<String>(view.getErrors());
        Graph model = getGraphFactory().newGraph();
        // we need to record the view-to-model element map for layout transfer
        NodeEdgeMap elementMap = new NodeEdgeHashMap();
        // copy the nodes from view to model
        for (AspectNode viewNode : view.nodeSet()) {
            try {
                processViewNode(model, elementMap, viewNode);
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        // mapping from nodes to node type labels found
        Map<Node,Label> nodeTypes = new HashMap<Node,Label>();
        // collection of declared subtypes
        NodeRelation subtypes = new SetNodeRelation(view);
        // copy the edges from view to model
        for (AspectEdge viewEdge : view.edgeSet()) {
            try {
                processViewEdge(model, elementMap, nodeTypes, subtypes,
                    viewEdge);
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        // remove isolated variable nodes from the result graph
        Iterator<Map.Entry<Node,Node>> viewToModelIter =
            elementMap.nodeMap().entrySet().iterator();
        while (viewToModelIter.hasNext()) {
            Map.Entry<Node,Node> viewToModelEntry = viewToModelIter.next();
            Node modelNode = viewToModelEntry.getValue();
            if (modelNode instanceof ValueNode
                && model.edgeSet(modelNode).isEmpty()) {
                // the node is an isolated value node; remove it
                model.removeNode(modelNode);
                viewToModelIter.remove();
            }
        }
        // test against the type graph, if any
        if (this.type != null) {
            // test if all nodes have valid types
            for (Node node : model.nodeSet()) {
                Label nodeType = nodeTypes.get(node);
                if (nodeType == null) {
                    if (node instanceof ValueNode) {
                        String signature =
                            AlgebraRegister.getSignatureName(((ValueNode) node).getAlgebra());
                        nodeTypes.put(node, DefaultLabel.createLabel(signature,
                            Label.NODE_TYPE));
                    } else {
                        errors.add(String.format("Node '%s' is untyped", node));
                    }
                } else if (!this.type.isNodeType(nodeType)) {
                    errors.add(String.format(
                        "Node '%s' has nonexistent type '%s'", node, nodeType));
                }
            }
            for (Edge edge : model.edgeSet()) {
                Node source = edge.source();
                Label sourceType = nodeTypes.get(source);
                Label targetType = nodeTypes.get(edge.opposite());
                if (sourceType == null || targetType == null) {
                    // this must be due to an untyped node
                    // which was already reported as an error
                    continue;
                }
                Label edgeType = edge.label();
                if (edgeType.isFlag()) {
                    if (!this.type.hasFlag(sourceType, edgeType)) {
                        errors.add(String.format(
                            "Node '%s' with type '%s' has nonexistent flag '%s'",
                            source, sourceType, edgeType));
                    }
                } else if (edgeType.isBinary()) {
                    Label declaredTargetType =
                        this.type.getTarget(sourceType, edgeType);
                    if (declaredTargetType == null) {
                        errors.add(String.format(
                            "Node '%s' with type '%s' has outgoing edge with nonexistent type '%s'",
                            source, sourceType, edgeType));
                    } else if (DefaultLabel.isDataType(declaredTargetType)
                        || DefaultLabel.isDataType(targetType)) {
                        if (!targetType.equals(declaredTargetType)) {
                            errors.add(String.format(
                                "Target node of '%s' has type '%s' but should have data type '%s'",
                                edge, targetType, declaredTargetType));
                        }
                    } else if (!this.type.isSubtype(targetType,
                        declaredTargetType)) {
                        errors.add(String.format(
                            "Target node of '%s' has type '%s' which is not a subtype of '%s'",
                            edge, targetType, declaredTargetType));
                    }
                }
            }
        }
        // transfer graph info such as layout from view to model
        GraphInfo.transfer(view, model, elementMap);
        GraphInfo.setErrors(model, new ArrayList<String>(errors));
        model.setFixed();
        return new Pair<Graph,NodeEdgeMap>(model, elementMap);
    }

    /**
     * Processes the information in a view node by updating the model and
     * element map.
     * @throws FormatException if the presence of the node signifies an error
     */
    private void processViewNode(Graph model, NodeEdgeMap elementMap,
            AspectNode viewNode) throws FormatException {
        String error = null;
        boolean nodeInModel = true;
        for (AspectValue value : viewNode.getAspectMap()) {
            if (isVirtualValue(value)) {
                nodeInModel = false;
            } else if (!isAllowedValue(value)) {
                error =
                    String.format(
                        "Node aspect value '%s' not allowed in graphs", value);
            }
        }
        // include the node in the model if it is not virtual
        if (nodeInModel) {
            Node nodeImage =
                this.attributeFactory.createAttributeNode(viewNode);
            if (nodeImage == null) {
                nodeImage = DefaultNode.createNode(viewNode.getNumber());
                model.addNode(nodeImage);
            } else if (isAllowedNode(nodeImage)) {
                model.addNode(nodeImage);
            } else {
                error =
                    String.format(
                        "Node aspect value '%s' not allowed in graphs",
                        getAttributeValue(viewNode));
            }
            elementMap.putNode(viewNode, nodeImage);
        }
        if (error != null) {
            throw new FormatException(error);
        }
    }

    /**
     * Processes the information in a view edge by updating the model, element
     * map and subtypes.
     * @throws FormatException if the presence of the edge signifies an error
     */
    private void processViewEdge(Graph model, NodeEdgeMap elementMap,
            Map<Node,Label> nodeTypeMap, NodeRelation subtypes,
            AspectEdge viewEdge) throws FormatException {
        if (AttributeAspect.isConstant(viewEdge)
            || viewEdge.getModelLabel() == null) {
            return;
        }
        for (AspectValue value : viewEdge.getAspectMap()) {
            if (isVirtualValue(value)) {
                return;
            }
            if (!isAllowedValue(value)) {
                throw new FormatException(
                    "Edge aspect value '%s' not allowed in graphs", value);
            }
        }
        Node modelSource = elementMap.getNode(viewEdge.source());
        assert modelSource != null : String.format(
            "Source of '%s' is not in element map %s", viewEdge.source(),
            elementMap);
        Node modelTarget = elementMap.getNode(viewEdge.target());
        assert modelTarget != null : String.format(
            "Target of '%s' is not in element map %s", viewEdge.target(),
            elementMap);
        // create an image for the view edge
        if (this.attributeFactory.createAttributeEdge(viewEdge, new Node[] {
            modelSource, modelTarget}) != null) {
            throw new FormatException(
                "Edge aspect value '%s' not allowed in graphs",
                getAttributeValue(viewEdge));
        }
        Label modelLabel = viewEdge.getModelLabel();
        if (DefaultLabel.isDataType(modelLabel)) {
            throw new FormatException(
                "Data type label '%s' not allowed in graphs", modelLabel);
        } else if (modelLabel.isNodeType()) {
            nodeTypeMap.put(modelSource, modelLabel);
        }
        Edge modelEdge = model.addEdge(modelSource, modelLabel, modelTarget);
        this.labelSet.add(modelLabel);
        elementMap.putEdge(viewEdge, modelEdge);
    }

    /**
     * Tests if a certain attribute node is of the type allowed in graphs.
     */
    private boolean isAllowedNode(Node node) {
        return node instanceof ValueNode;
    }

    /**
     * Tests if a certain non-virtual aspect value is allowed in a graph view.
     */
    private boolean isAllowedValue(AspectValue value) {
        return value.getAspect() instanceof AttributeAspect
            && !value.equals(AttributeAspect.PRODUCT)
            && !value.equals(AttributeAspect.ARGUMENT)
            || value.getAspect() instanceof TypeAspect
            && !value.equals(TypeAspect.SUB);
    }

    /**
     * Tests if a certain aspect value causes a graph element to be virtual.
     */
    private boolean isVirtualValue(AspectValue value) {
        return RuleAspect.REMARK.equals(value);
    }

    /**
     * Returns the graph factory used to construct the model.
     */
    private GraphFactory getGraphFactory() {
        return graphFactory;
    }

    /** The name of the view. */
    private final String name;
    /** The view represented by this object. */
    private final AspectGraph view;
    /** The graph model that is being viewed. */
    private Graph model;
    /**
     * List of errors in the view that prevent the model from being constructed.
     */
    private List<String> errors;
    /** Map from view to model nodes. */
    private NodeEdgeMap viewToModelMap;
    /** Set of labels occurring in this graph. */
    private Set<Label> labelSet;
    /** The attribute element factory for this view. */
    private AttributeElementFactory attributeFactory;
    /** Optional type graph for this aspect graph. */
    private TypeGraph type;
    /** The graph factory used by this view, to construct the model. */
    private static final GraphFactory graphFactory = GraphFactory.getInstance();
}
