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
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.GraphFactory;
import groove.graph.GraphInfo;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.algebra.OperatorEdge;
import groove.graph.algebra.ValueNode;
import groove.graph.algebra.VariableNode;
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
import java.util.Collections;
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
        this.labelSet = new HashSet<Label>();
        // we fix the view; is it conceptually right to do that here?
        view.setFixed();
        String name = GraphInfo.getName(view);
        this.name = name == null ? "" : name;
        Graph model;
        NodeEdgeMap viewToModelMap;
        List<String> errors;
        try {
            Pair<Graph,NodeEdgeMap> modelPlusMap = computeModel(view);
            model = modelPlusMap.first();
            viewToModelMap = modelPlusMap.second();
            errors = Collections.emptyList();
        } catch (FormatException e) {
            model = null;
            viewToModelMap = new NodeEdgeHashMap();
            errors = e.getErrors();
        }
        this.model = model;
        this.viewToModelMap = viewToModelMap;
        this.errors = errors;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public AspectGraph getView() {
        return this.view;
    }

    public Graph toModel() throws FormatException {
        if (this.model == null) {
            throw new FormatException(getErrors());
        } else {
            return this.model;
        }
    }

    public List<String> getErrors() {
        return this.errors;
    }

    @Override
    public NodeEdgeMap getMap() {
        return this.viewToModelMap;
    }

    /**
     * Tests if the properties object with which this view was created is
     * <i>essentially equal</i> to a given properties object. Only tests for
     * equality of the algebra family property.
     * @param properties the properties to be compared with the internal ones
     * @return <code>true</code> if the properties objects are essentially equal
     * @see SystemProperties#getAlgebraFamily()
     */
    public boolean hasProperties(SystemProperties properties) {
        return this.attributeFactory.equals(createAttributeFactory(properties));
    }

    /** Returns the set of labels used in this graph. */
    public Set<Label> getLabels() {
        return this.labelSet;
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
    private Pair<Graph,NodeEdgeMap> computeModel(AspectGraph view)
        throws FormatException {
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
        // transfer graph info such as layout from view to model
        GraphInfo.transfer(view, model, elementMap);
        if (errors.isEmpty()) {
            model.setFixed();
            return new Pair<Graph,NodeEdgeMap>(model, elementMap);
        } else {
            throw new FormatException(new ArrayList<String>(errors));
        }
    }

    /**
     * Processes the information in a view node by updating the model and
     * element map.
     * @throws FormatException if the presence of the node signifies an error
     */
    private void processViewNode(Graph model, NodeEdgeMap elementMap,
            AspectNode viewNode) throws FormatException {
        boolean nodeInModel = true;
        for (AspectValue value : viewNode.getAspectMap()) {
            if (isVirtualValue(value)) {
                nodeInModel = false;
            } else if (!isAllowedValue(value)) {
                throw new FormatException(
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
                throw new FormatException(
                    "Node aspect value '%s' not allowed in graphs",
                    getAttributeValue(viewNode));
            }
            elementMap.putNode(viewNode, nodeImage);
            // modelToViewMap.put(nodeImage, viewNode);
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
            || viewEdge.getModelLabel(false) == null) {
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
        // type edges must either be self-edges or "sub"-labelled edges in a
        // type graph
        if (TypeAspect.isNodeType(viewEdge)
            && !viewEdge.source().equals(viewEdge.opposite())) {
            if (GraphInfo.hasTypeRole(this.view)
                && viewEdge.label().equals(TypeAspect.SUB_LABEL)) {
                subtypes.addRelated(viewEdge);
                return;
            } else {
                throw new FormatException(
                    "Node type label '%s' only allowed on self-edges",
                    viewEdge.label());
            }
        }
        // include the edge in the model if all end nodes are there
        Node[] endImages = new Node[viewEdge.endCount()];
        for (int i = 0; i < endImages.length; i++) {
            endImages[i] = elementMap.getNode(viewEdge.end(i));
            if (endImages[i] == null) {
                return;
            }
        }
        // create an image for the view edge
        if (this.attributeFactory.createAttributeEdge(viewEdge, endImages) != null) {
            throw new FormatException(
                "Edge aspect value '%s' not allowed in graphs",
                getAttributeValue(viewEdge));
        }
        Label modelLabel = viewEdge.getModelLabel(false);
        // collect node types in a type graph
        if (GraphInfo.hasTypeRole(this.view) && modelLabel.isNodeType()) {
            Label oldType = nodeTypeMap.put(endImages[0], modelLabel);
            if (oldType != null) {
                throw new FormatException(
                    "Double node type '%s' and '%s' not allowed in type graphs",
                    oldType, modelLabel);
            }
        }
        Edge modelEdge = model.addEdge(endImages, modelLabel);
        this.labelSet.add(modelLabel);
        elementMap.putEdge(viewEdge, modelEdge);
    }

    /**
     * Tests if a certain attribute node is of the type allowed in graphs.
     */
    private boolean isAllowedNode(Node node) {
        boolean result;
        if (GraphInfo.hasGraphRole(this.view)) {
            result = node instanceof ValueNode;
        } else {
            result =
                node.getClass().equals(VariableNode.class)
                    && ((VariableNode) node).getAlgebra() != null;
        }
        return result;
    }

    /**
     * Tests if a certain non-virtual aspect value is allowed for nodes in a
     * graph view.
     */
    private boolean isAllowedValue(AspectValue value) {
        return value.getAspect() instanceof AttributeAspect
            || value.getAspect() instanceof TypeAspect;
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
    private final Graph model;
    /**
     * List of errors in the view that prevent the model from being constructed.
     */
    private final List<String> errors;
    /** Map from view to model nodes. */
    private final NodeEdgeMap viewToModelMap;
    /** Set of labels occurring in this graph. */
    private final Set<Label> labelSet;
    /** The attribute element factory for this view. */
    private final AttributeElementFactory attributeFactory;
    /** The graph factory used by this view, to construct the model. */
    private static final GraphFactory graphFactory = GraphFactory.getInstance();
}
