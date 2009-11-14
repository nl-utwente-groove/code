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
import groove.trans.SystemProperties;
import groove.util.Pair;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectValue;
import groove.view.aspect.AttributeAspect;
import groove.view.aspect.AttributeElementFactory;
import groove.view.aspect.NodeTypeAspect;
import groove.view.aspect.RuleAspect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
public class AspectualGraphView extends AspectualView<Graph> {
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
    public AspectGraph getAspectGraph() {
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

    @Override
    protected LabelParser getDefaultLabelParser() {
        return FreeLabelParser.getInstance();
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
        // we need to record the view-to-model node map for the return value
        Map<AspectNode,Node> viewToModelMap = new HashMap<AspectNode,Node>();
        // // we need to record the model-to-view node map for removing isolated
        // // value nodes
        // Map<Node,AspectNode> modelToViewMap = new HashMap<Node,AspectNode>();
        // copy the nodes from view to model
        for (AspectNode viewNode : view.nodeSet()) {
            try {
                boolean nodeInModel = true;
                for (AspectValue value : viewNode.getAspectMap().values()) {
                    if (isVirtualValue(value)) {
                        nodeInModel = false;
                    } else if (!isAllowedValue(value)) {
                        throw new FormatException(
                            "Node aspect value '%s' not allowed in graphs",
                            value);
                    }
                }
                // include the node in the model if it is not virtual
                if (nodeInModel) {
                    Node nodeImage =
                        this.attributeFactory.createAttributeNode(viewNode);
                    if (nodeImage == null) {
                        nodeImage =
                            DefaultNode.createNode(viewNode.getNumber());
                        model.addNode(nodeImage);
                    } else if (isAllowedNode(nodeImage)) {
                        model.addNode(nodeImage);
                    } else {
                        errors.add(String.format(
                            "Node aspect value '%s' not allowed in graphs",
                            getAttributeValue(viewNode)));
                    }
                    viewToModelMap.put(viewNode, nodeImage);
                    // modelToViewMap.put(nodeImage, viewNode);
                }
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        elementMap.nodeMap().putAll(viewToModelMap);
        // set of nodes for which a node type label was found
        Map<Node,Label> nodeTypes = new HashMap<Node,Label>();
        // copy the edges from view to model
        edgeLoop: for (AspectEdge viewEdge : view.edgeSet()) {
            if (AttributeAspect.isConstant(viewEdge)) {
                continue edgeLoop;
            }
            for (AspectValue value : viewEdge.getAspectMap().values()) {
                if (isVirtualValue(value)) {
                    continue edgeLoop;
                }
                if (!isAllowedValue(value)) {
                    throw new FormatException(
                        "Edge aspect value '%s' not allowed in graphs", value);
                }
            }
            // include the edge in the model if it is not virtual
            Node[] endImages = new Node[viewEdge.endCount()];
            for (int i = 0; i < endImages.length; i++) {
                endImages[i] = viewToModelMap.get(viewEdge.end(i));
                if (endImages[i] == null) {
                    continue edgeLoop;
                }
            }
            try {
                // create an image for the view edge
                Edge edgeImage =
                    this.attributeFactory.createAttributeEdge(viewEdge,
                        endImages);
                if (edgeImage == null) {
                    edgeImage = model.addEdge(endImages, parse(viewEdge));
                    Label edgeLabel = edgeImage.label();
                    if (edgeLabel.isNodeType()) {
                        if (!edgeImage.source().equals(edgeImage.opposite())) {
                            throw new FormatException(
                                "Node type label '%s' only allowed on self-edges",
                                edgeLabel);
                        }
                        Label oldType =
                            nodeTypes.put(edgeImage.source(), edgeLabel);
                        if (oldType != null) {
                            throw new FormatException(
                                "Double node type: '%s' and '%s'", oldType,
                                edgeLabel);
                        }
                    }
                } else if (!isAllowedEdge(edgeImage)) {
                    throw new FormatException(
                        "Edge aspect value '%s' not allowed in graphs",
                        getAttributeValue(viewEdge));
                }
                elementMap.putEdge(viewEdge, edgeImage);
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        // remove isolated variable nodes from the result graph
        Iterator<Map.Entry<AspectNode,Node>> viewToModelIter =
            viewToModelMap.entrySet().iterator();
        while (viewToModelIter.hasNext()) {
            Map.Entry<AspectNode,Node> viewToModelEntry =
                viewToModelIter.next();
            Node modelNode = viewToModelEntry.getValue();
            if (modelNode instanceof ValueNode
                && model.edgeSet(modelNode).isEmpty()) {
                // the node is an isolated value node; remove it
                model.removeNode(modelNode);
                elementMap.removeNode(viewToModelEntry.getKey());
                viewToModelIter.remove();
            }
        }
        // // turn variable nodes into value nodes
        // NodeEdgeMap conversionMap = new NodeEdgeHashMap();
        // model = AlgebraGraph.getInstance().convertGraph(model,
        // conversionMap);
        // // adapt the element map
        // for (Map.Entry<Node,Node> nodeEntry: elementMap.nodeMap().entrySet())
        // {
        // nodeEntry.setValue(conversionMap.getNode(nodeEntry.getValue()));
        // }
        // for (Map.Entry<Edge,Edge> edgeEntry: elementMap.edgeMap().entrySet())
        // {
        // edgeEntry.setValue(conversionMap.getEdge(edgeEntry.getValue()));
        // }
        // transfer graph info such as layout from view to model
        GraphInfo.transfer(view, model, elementMap);
        if (errors.isEmpty()) {
            model.setFixed();
            return new Pair<Graph,NodeEdgeMap>(model, elementMap);
        } else {
            throw new FormatException(new ArrayList<String>(errors));
        }
    }

    //
    // /**
    // * Attempts to create an attribute node from a given aspect node.
    // * @return null if the aspect node is not an attribute node.
    // * @throws FormatException if the aspect value is wrongly formatted
    // */
    // private Node createAttributeNode(AspectGraph view, AspectNode viewNode)
    // throws FormatException {
    // Node result = this.attributeFactory.createAttributeNode(viewNode);
    // return result;
    // }

    /**
     * Tests if a certain attribute node is of the type allowed in graphs.
     */
    private boolean isAllowedNode(Node node) {
        return node instanceof ValueNode;
    }

    /**
     * Tests if a certain attribute edge is of the type allowed in graphs.
     */
    private boolean isAllowedEdge(Edge edge) {
        return false;
        // return edge instanceof OperatorEdge
        // && ((OperatorEdge) edge).getOperation() instanceof Constant;
    }

    /**
     * Tests if a certain non-virtual aspect value is allowed for nodes in a
     * graph view.
     */
    private boolean isAllowedValue(AspectValue value) {
        return value.getAspect() instanceof AttributeAspect
            || value.getAspect() instanceof NodeTypeAspect;
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
    /** The attribute element factory for this view. */
    private final AttributeElementFactory attributeFactory;
    /** The graph factory used by this view, to construct the model. */
    private static final GraphFactory graphFactory = GraphFactory.getInstance();
}
