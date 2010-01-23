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

import groove.graph.Edge;
import groove.graph.GraphInfo;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.graph.TypeGraph;
import groove.graph.TypeNode;
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
public class DefaultTypeView implements TypeView {
    /**
     * Constructs an instance from a given aspect graph.
     * @see GraphInfo#getName(groove.graph.GraphShape)
     */
    public DefaultTypeView(AspectGraph view) {
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

    public TypeGraph toModel() throws FormatException {
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
        return this.model.getLabelStore().getLabels();
    }

    /** Constructs the model and associated data structures from the view. */
    private void initialise() {
        // first test if there is something to be done
        if (this.errors == null) {
            try {
                Pair<TypeGraph,NodeEdgeMap> modelPlusMap =
                    computeModel(this.view);
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
    private Pair<TypeGraph,NodeEdgeMap> computeModel(AspectGraph view)
        throws FormatException {
        Set<String> errors = new TreeSet<String>(view.getErrors());
        TypeGraph model = new TypeGraph();
        // first check the nodes for allowed aspect values
        for (AspectNode viewNode : view.nodeSet()) {
            checkViewNode(viewNode);
        }
        // mapping from view nodes to types
        Map<AspectNode,Label> viewTypeMap = new HashMap<AspectNode,Label>();
        // mapping from model nodes to types
        Map<Node,Label> modelTypeMap = new HashMap<Node,Label>();
        // mapping from types to model nodes
        Map<Label,TypeNode> typeNodeMap = new HashMap<Label,TypeNode>();
        // View-to-model element map
        NodeEdgeMap elementMap = new NodeEdgeHashMap();
        // collect node type edges and build the view type map
        for (AspectEdge viewEdge : view.edgeSet()) {
            Label modelLabel = viewEdge.getModelLabel();
            if (modelLabel != null && modelLabel.isNodeType()) {
                AspectNode viewSource = viewEdge.source();
                TypeNode oldTypeNode =
                    (TypeNode) elementMap.getNode(viewSource);
                if (oldTypeNode != null) {
                    errors.add(String.format(
                        "Node '%s' has types '%s' and '%s'", viewSource,
                        modelLabel, oldTypeNode.getType()));
                    continue;
                }
                viewTypeMap.put(viewSource, modelLabel);
                TypeNode typeNode = typeNodeMap.get(modelLabel);
                if (typeNode == null) {
                    typeNode = new TypeNode(viewSource.getNumber(), modelLabel);
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
                TypeNode modelNode = new TypeNode(viewNode.getNumber());
                model.addNode(modelNode);
                elementMap.putNode(viewNode, modelNode);
            }
        }
        if (!untypedNodes.isEmpty()) {
            errors.add(String.format("Untyped nodes %s in type graph",
                untypedNodes));
        }
        // copy the edges from view to model
        for (AspectEdge viewEdge : view.edgeSet()) {
            try {
                processViewEdge(model, elementMap, viewEdge);
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        // add subtype relations to the model
        for (AspectEdge viewEdge : view.edgeSet()) {
            try {
                if (TypeAspect.isSubtype(viewEdge)) {
                    model.addSubtype(elementMap.getNode(viewEdge.target()),
                        elementMap.getNode(viewEdge.source()));
                }
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        if (errors.isEmpty()) {
            try {
                model.test();
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        // transfer graph info such as layout from view to model
        GraphInfo.transfer(view, model, elementMap);
        if (errors.isEmpty()) {
            model.setFixed();
            return new Pair<TypeGraph,NodeEdgeMap>(model, elementMap);
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
    private void processViewEdge(TypeGraph model, NodeEdgeMap elementMap,
            AspectEdge viewEdge) throws FormatException {
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
        if (!TypeAspect.isSubtype(viewEdge)) {
            Label modelLabel = viewEdge.getModelLabel();
            Edge modelEdge =
                model.addEdge(modelSource, modelLabel, modelTarget);
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

    /** The name of the view. */
    private final String name;
    /** The view represented by this object. */
    private final AspectGraph view;
    /** The graph model that is being viewed. */
    private TypeGraph model;
    /**
     * List of errors in the view that prevent the model from being constructed.
     */
    private List<String> errors;
    /** Map from view to model nodes. */
    private NodeEdgeMap viewToModelMap;
}
