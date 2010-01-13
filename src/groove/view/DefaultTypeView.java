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
import groove.rel.NodeRelation;
import groove.rel.SetNodeRelation;
import groove.trans.SystemProperties;
import groove.util.Pair;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectValue;
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
 * View translating an aspect graph (with type role) to a type graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultTypeView implements GraphView {
    /**
     * Constructs an instance from a given aspect graph.
     * @see GraphInfo#getName(groove.graph.GraphShape)
     */
    public DefaultTypeView(AspectGraph view, SystemProperties properties) {
        this.view = view;
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

    /** Returns the set of labels used in this graph. */
    public Set<Label> getLabels() {
        initialise();
        return this.labelSet;
    }

    //
    // /**
    // * Invalidates the model and associated data structures.
    // * This results in them being recomputed upon the next call of {@link
    // #initialise()}.
    // * Called in response to a change in the system properties.
    // */
    // private void invalidate() {
    // model = null;
    // errors = null;
    // viewToModelMap = null;
    // labelSet = null;
    // }
    //    
    /** Constructs the model and associated data structures from the view. */
    private void initialise() {
        // first test if there is something to be done
        if (this.errors == null) {
            this.labelSet = new HashSet<Label>();
            try {
                Pair<Graph,NodeEdgeMap> modelPlusMap = computeModel(this.view);
                this.model = modelPlusMap.first();
                this.viewToModelMap = modelPlusMap.second();
                this.errors = Collections.emptyList();
            } catch (FormatException e) {
                this.model = null;
                this.viewToModelMap = new NodeEdgeHashMap();
                this.errors = e.getErrors();
            }
        }
    }

    /**
     * Computes a fresh model from a given aspect graph, together with a mapping
     * from the aspect graph's node to the (fresh) graph nodes.
     */
    private Pair<Graph,NodeEdgeMap> computeModel(AspectGraph view)
        throws FormatException {
        Set<String> errors = new TreeSet<String>(view.getErrors());
        Graph model = getGraphFactory().newGraph();
        // first check the nodes for allowed aspect values
        for (AspectNode viewNode : view.nodeSet()) {
            checkViewNode(viewNode);
        }
        // mapping from view nodes to types
        Map<AspectNode,Label> viewTypeMap = new HashMap<AspectNode,Label>();
        // mapping from model nodes to types
        Map<Node,Label> modelTypeMap = new HashMap<Node,Label>();
        // mapping from types to model nodes
        Map<Label,Node> typeNodeMap = new HashMap<Label,Node>();
        // View-to-model element map
        NodeEdgeMap elementMap = new NodeEdgeHashMap();
        // collect node type edges and build the view type map
        for (AspectEdge viewEdge : view.edgeSet()) {
            Label modelLabel = viewEdge.getModelLabel();
            if (modelLabel != null && modelLabel.isNodeType()) {
                AspectNode viewSource = viewEdge.source();
                viewTypeMap.put(viewSource, modelLabel);
                Node typeNode = typeNodeMap.get(modelLabel);
                if (typeNode == null) {
                    typeNode = createModelNode(viewSource);
                    model.addNode(typeNode);
                    typeNodeMap.put(modelLabel, typeNode);
                    modelTypeMap.put(typeNode, modelLabel);
                }
                elementMap.putNode(viewSource, typeNode);
            }
        }
        // check if there are untyped, non-virtual nodes
        Set<AspectNode> untypedNodes = new HashSet<AspectNode>(view.nodeSet());
        untypedNodes.removeAll(viewTypeMap.keySet());
        Iterator<AspectNode> untypedNodeIter = untypedNodes.iterator();
        while (untypedNodeIter.hasNext()) {
            AspectNode viewNode = untypedNodeIter.next();
            if (RuleAspect.isRemark(viewNode)) {
                untypedNodeIter.remove();
            } else {
                // add a node anyhow, to ensure all edge ends have images
                Node modelNode = createModelNode(viewNode);
                model.addNode(modelNode);
                elementMap.putNode(viewNode, modelNode);
            }
        }
        if (!untypedNodes.isEmpty()) {
            errors.add(String.format("Untyped nodes %s in type graph",
                untypedNodes));
        }
        // collection of declared subtypes
        NodeRelation subtypes = new SetNodeRelation(model);
        // copy the edges from view to model
        for (AspectEdge viewEdge : view.edgeSet()) {
            try {
                processViewEdge(model, elementMap, subtypes, viewEdge);
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        checkModel(model, modelTypeMap, subtypes);
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
     * Checks if the aspect values in a view node are legal
     * @throws FormatException if the presence of the edge signifies an error
     */
    private void checkViewNode(AspectNode viewNode) throws FormatException {
        for (AspectValue value : viewNode.getAspectMap()) {
            if (isVirtualValue(value)) {
                return;
            }
            if (!isAllowedValue(value)) {
                throw new FormatException(
                    "Node aspect value '%s' not allowed in type graphs", value);
            }
        }
    }

    /**
     * Processes the information in a view edge by updating the model, element
     * map and subtypes.
     * @throws FormatException if the presence of the edge signifies an error
     */
    private void processViewEdge(Graph model, NodeEdgeMap elementMap,
            NodeRelation subtypes, AspectEdge viewEdge) throws FormatException {
        for (AspectValue value : viewEdge.getAspectMap()) {
            if (isVirtualValue(value)) {
                return;
            }
            if (!isAllowedValue(value)) {
                throw new FormatException(
                    "Edge aspect value '%s' not allowed in type graphs", value);
            }
        }
        Node modelSource = elementMap.getNode(viewEdge.source());
        assert modelSource != null : String.format(
            "Source of view edge '%s' not in element map %s",
            viewEdge.source(), elementMap);
        Node modelTarget = elementMap.getNode(viewEdge.target());
        assert modelTarget != null : String.format(
            "Target of view edge '%s' not in element map %s",
            viewEdge.source(), elementMap);
        // register subtype edges
        if (TypeAspect.isSubtype(viewEdge)) {
            subtypes.addRelated(modelSource, modelTarget);
        } else {
            Label modelLabel = viewEdge.getModelLabel();
            Edge modelEdge =
                model.addEdge(modelSource, modelLabel, modelTarget);
            this.labelSet.add(modelLabel);
            elementMap.putEdge(viewEdge, modelEdge);
        }
    }

    /**
     * Tests if a certain non-virtual aspect value is allowed in a type view.
     */
    private boolean isAllowedValue(AspectValue value) {
        return value.getAspect() instanceof TypeAspect;
    }

    /**
     * Tests if a certain aspect value causes a graph element to be virtual.
     */
    private boolean isVirtualValue(AspectValue value) {
        return RuleAspect.REMARK.equals(value);
    }

    private DefaultNode createModelNode(AspectNode viewSource) {
        return DefaultNode.createNode(viewSource.getNumber());
    }

    /** Checks if a given graph satisfies the properties of a type graph. */
    private void checkModel(Graph model, Map<Node,Label> typeMap,
            NodeRelation subtypes) throws FormatException {
        Set<String> errors = new TreeSet<String>();
        // check for self-subtypes and subtype declarations between data types
        for (Edge subtypePair : subtypes.getAllRelated()) {
            if (subtypePair.source().equals(subtypePair.opposite())) {
                errors.add(String.format(
                    "Type '%s' cannot be a subtype of itself",
                    typeMap.get(subtypePair.source())));
            } else {
                Label sourceType = typeMap.get(subtypePair.source());
                Label targetType = typeMap.get(subtypePair.opposite());
                if (DefaultLabel.isDataType(sourceType)) {
                    errors.add(String.format(
                        "Data type '%s' cannot be declared as subtype",
                        sourceType));
                }
                if (DefaultLabel.isDataType(targetType)) {
                    errors.add(String.format(
                        "Data type '%s' cannot be declared as supertype",
                        sourceType));
                }
            }
        }
        // check for outgoing edge types from data types
        for (Edge typeEdge : model.edgeSet()) {
            Label sourceType = typeMap.get(typeEdge.source());
            if (!typeEdge.label().isNodeType()
                && DefaultLabel.isDataType(sourceType)) {
                errors.add(String.format("Data type '%s' cannot have %s",
                    sourceType, typeEdge.label().isFlag() ? "flags"
                            : "outgoing edges"));
            }
        }
        // check for cycles in the subtyping relation
        NodeRelation downClosure = subtypes.getTransitiveClosure().getInverse();
        if (errors.isEmpty()) {
            Set<Label> cyclicSubtypes = new HashSet<Label>();
            for (Edge typeEdge : downClosure.getAllRelated()) {
                if (typeEdge.source().equals(typeEdge.opposite())) {
                    cyclicSubtypes.add(typeMap.get(typeEdge.source()));
                }
            }
            if (!cyclicSubtypes.isEmpty()) {
                errors.add(String.format("Subtyping relation is cyclic in %s",
                    cyclicSubtypes));
            }
        }
        // check for name shadowing of edge types
        downClosure.doReflexiveClosure();
        Map<Node,Map<Label,Node>> outTypeMap =
            new HashMap<Node,Map<Label,Node>>();
        for (Edge typeEdge : model.edgeSet()) {
            if (!typeEdge.label().isNodeType()) {
                Node source = typeEdge.source();
                for (Node subtype : downClosure.getRelated(source)) {
                    Map<Label,Node> outTypes = outTypeMap.get(subtype);
                    if (outTypes == null) {
                        outTypeMap.put(subtype, outTypes =
                            new HashMap<Label,Node>());
                    }
                    Node oldTypeNode = outTypes.put(typeEdge.label(), subtype);
                    if (oldTypeNode != null) {
                        errors.add(String.format(
                            "Conflicting edge type '%s' in '%s' and '%s'",
                            typeEdge.label(), typeMap.get(source),
                            typeMap.get(oldTypeNode)));
                    }
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
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
    /** The graph factory used by this view, to construct the model. */
    private static final GraphFactory graphFactory = GraphFactory.getInstance();
}
