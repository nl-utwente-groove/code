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

import static groove.graph.EdgeRole.NODE_TYPE;
import static groove.view.aspect.AspectKind.ABSTRACT;
import static groove.view.aspect.AspectKind.NONE;
import static groove.view.aspect.AspectKind.SUBTYPE;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.LabelStore;
import groove.graph.TypeEdge;
import groove.graph.TypeFactory;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.TypeNode;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectNode;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * View translating an aspect graph (with type role) to a type graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultTypeView implements TypeView {
    /**
     * Constructs an instance from a given aspect graph.
     */
    public DefaultTypeView(AspectGraph view) {
        view.testFixed(true);
        this.view = view;
    }

    public String getName() {
        return this.view.getName();
    }

    @Override
    public AspectGraph getAspectGraph() {
        return this.view;
    }

    @Override
    public boolean isEnabled() {
        return GraphProperties.isEnabled(getAspectGraph());
    }

    @Override
    public void setType(TypeGraph type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLabelStore(LabelStore labelStore) {
        throw new UnsupportedOperationException();
    }

    public TypeGraph toModel() throws FormatException {
        initialise();
        if (this.model == null) {
            throw new FormatException(getErrors());
        } else {
            return this.model;
        }
    }

    public List<FormatError> getErrors() {
        initialise();
        return this.errors;
    }

    @Override
    public ViewToTypeMap getMap() {
        initialise();
        return this.elementMap;
    }

    /** 
     * Returns the set of labels used in this graph.
     * @return the set of labels, or {@code null} if the model could not be computed 
     */
    public Set<TypeLabel> getLabels() {
        initialise();
        return this.model == null ? null
                : this.model.getLabelStore().getLabels();
    }

    /** Constructs the model and associated data structures from the view,
     * if this has not already been done and the view itself does not contain
     * errors. */
    private void initialise() {
        // first test if there is something to be done
        if (this.errors == null) {
            this.errors = new ArrayList<FormatError>(this.view.getErrors());
            if (this.errors.isEmpty()) {
                initialiseModel();
            }
        }
    }

    /** Constructs the model and associated data structures from the view. */
    private void initialiseModel() {
        this.model = new TypeGraph(getName());
        this.elementMap = new ViewToTypeMap();
        // collect primitive type nodes
        for (AspectNode viewNode : this.view.nodeSet()) {
            AspectKind attrKind = viewNode.getAttrKind();
            if (attrKind != NONE) {
                TypeLabel modelLabel =
                    TypeLabel.createLabel(NODE_TYPE, attrKind.getName());
                addNodeType(viewNode, modelLabel);
            }
        }
        // collect node type edges and build the view type map
        for (AspectEdge viewEdge : this.view.edgeSet()) {
            TypeLabel typeLabel = viewEdge.getTypeLabel();
            if (typeLabel != null && typeLabel.isNodeType()) {
                addNodeType(viewEdge.source(), typeLabel);
            }
        }
        // check if there are untyped, non-virtual nodes
        Set<AspectNode> untypedNodes =
            new HashSet<AspectNode>(this.view.nodeSet());
        untypedNodes.removeAll(this.viewTypeMap.keySet());
        Iterator<AspectNode> untypedNodeIter = untypedNodes.iterator();
        while (untypedNodeIter.hasNext()) {
            AspectNode viewNode = untypedNodeIter.next();
            if (viewNode.getKind().isMeta()) {
                untypedNodeIter.remove();
            } else {
                // add a node anyhow, to ensure all edge ends have images
                TypeNode modelNode = new TypeNode(viewNode.getNumber());
                this.model.addNode(modelNode);
                this.elementMap.putNode(viewNode, modelNode);
            }
        }
        for (AspectNode untypedNode : untypedNodes) {
            this.errors.add(new FormatError("Node '%s' has no type label",
                untypedNode));
        }
        // copy the edges from view to model
        for (AspectEdge viewEdge : this.view.edgeSet()) {
            // do not process the node type edges again
            TypeLabel typeLabel = viewEdge.getTypeLabel();
            if (typeLabel == null || !typeLabel.isNodeType()) {
                try {
                    processViewEdge(this.model, this.elementMap, viewEdge);
                } catch (FormatException exc) {
                    this.errors.addAll(exc.getErrors());
                }
            }
        }
        if (this.errors.isEmpty()) {
            try {
                this.model.test();
            } catch (FormatException exc) {
                this.errors.addAll(exc.getErrors());
            }
        }
        // transfer graph info such as layout from view to model
        GraphInfo.transfer(this.view, this.model, this.elementMap);

    }

    /**
     * Adds a node type to the model.
     * @param viewNode the node in the aspect graph that stands for a node type
     * @param modelLabel the node type label
     */
    private void addNodeType(AspectNode viewNode, TypeLabel modelLabel) {
        TypeNode oldTypeNode = this.elementMap.getNode(viewNode);
        if (oldTypeNode != null) {
            this.errors.add(new FormatError(
                "Node '%s' has types '%s' and '%s'", viewNode, modelLabel,
                oldTypeNode.getType()));
        } else {
            this.viewTypeMap.put(viewNode, modelLabel);
            TypeNode typeNode = this.typeNodeMap.get(modelLabel);
            if (typeNode == null) {
                typeNode = new TypeNode(viewNode.getNumber(), modelLabel);
                typeNode.setAbstract(viewNode.getKind() == ABSTRACT);
                typeNode.setImported(viewNode.hasImport());
                if (viewNode.hasColor()) {
                    typeNode.setColor((Color) viewNode.getColor().getContent());
                }
                this.model.addNode(typeNode);
                this.typeNodeMap.put(modelLabel, typeNode);
                this.modelTypeMap.put(typeNode, modelLabel);
            }
            this.elementMap.putNode(viewNode, typeNode);
        }
    }

    /**
     * Processes the information in a view edge by updating the model, element
     * map and subtypes.
     */
    private void processViewEdge(TypeGraph model, ViewToTypeMap elementMap,
            AspectEdge viewEdge) throws FormatException {
        TypeNode modelSource = elementMap.getNode(viewEdge.source());
        assert modelSource != null : String.format(
            "Source of view edge '%s' not in element map %s",
            viewEdge.source(), elementMap);
        if (modelSource.isImported()) {
            throw new FormatException("Can't change imported type '%s'",
                modelSource.getType(), viewEdge);
        }
        TypeNode modelTarget = elementMap.getNode(viewEdge.target());
        assert modelTarget != null : String.format(
            "Target of view edge '%s' not in element map %s",
            viewEdge.source(), elementMap);
        if (viewEdge.getKind() == SUBTYPE) {
            model.addSubtype(modelSource, modelTarget);
        } else {
            TypeLabel modelLabel = viewEdge.getTypeLabel();
            TypeEdge modelEdge =
                model.addEdge(modelSource, modelLabel, modelTarget);
            modelEdge.setAbstract(viewEdge.getKind() == ABSTRACT);
            elementMap.putEdge(viewEdge, modelEdge);
        }
    }

    /** The view represented by this object. */
    private final AspectGraph view;
    /** The graph model that is being viewed. */
    private TypeGraph model;
    /**
     * List of errors in the view that prevent the model from being constructed.
     */
    private List<FormatError> errors;

    /** Auxiliary mapping from view nodes to types. */
    private Map<AspectNode,TypeLabel> viewTypeMap =
        new HashMap<AspectNode,TypeLabel>();
    /** Auxiliary from model nodes to types */
    private Map<TypeNode,TypeLabel> modelTypeMap =
        new HashMap<TypeNode,TypeLabel>();
    /** Auxiliary from types to model nodes */
    private Map<TypeLabel,TypeNode> typeNodeMap =
        new HashMap<TypeLabel,TypeNode>();
    /** Map from view to model nodes. */
    private ViewToTypeMap elementMap;

    /** Mapping from type graph elements to rule graph elements. */
    public static class ViewToTypeMap extends ViewToModelMap<TypeNode,TypeEdge> {
        /**
         * Creates a new, empty map.
         */
        public ViewToTypeMap() {
            super(TypeFactory.instance());
        }

        @Override
        public ViewToTypeMap newMap() {
            return new ViewToTypeMap();
        }
    }
}
