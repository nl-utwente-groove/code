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
 *  translating an aspect graph (with type role) to a type graph.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TypeModel extends GraphBasedModel<TypeGraph> {
    /**
     * Constructs an instance from a given aspect graph.
     */
    public TypeModel(AspectGraph source) {
        super(source);
        source.testFixed(true);
    }

    /** Indicates that the type graph is currently enabled. */
    public boolean isEnabled() {
        return GraphProperties.isEnabled(getSource());
    }

    @Override
    public void setType(TypeGraph type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLabelStore(LabelStore labelStore) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeGraph toResource() throws FormatException {
        initialise();
        if (this.model == null) {
            throw new FormatException(getErrors());
        } else {
            return this.model;
        }
    }

    @Override
    public List<FormatError> getErrors() {
        initialise();
        return this.errors;
    }

    @Override
    public TypeModelMap getMap() {
        initialise();
        return this.modelMap;
    }

    /** 
     * Returns the set of labels used in this graph.
     * @return the set of labels, or {@code null} if the model could not be computed 
     */
    @Override
    public Set<TypeLabel> getLabels() {
        initialise();
        return this.model == null ? null
                : this.model.getLabelStore().getLabels();
    }

    /** Constructs the model and associated data structures from the view,
     * if this has not already been done and the model itself does not contain
     * errors. */
    private void initialise() {
        // first test if there is something to be done
        if (this.errors == null) {
            this.errors = new ArrayList<FormatError>(getSource().getErrors());
            if (this.errors.isEmpty()) {
                initialiseModel();
            }
        }
    }

    /** Constructs the model and associated data structures for model. */
    private void initialiseModel() {
        this.model = new TypeGraph(getName());
        this.modelMap = new TypeModelMap();
        // collect primitive type nodes
        for (AspectNode modelNode : getSource().nodeSet()) {
            AspectKind attrKind = modelNode.getAttrKind();
            if (attrKind != NONE) {
                TypeLabel modelLabel =
                    TypeLabel.createLabel(NODE_TYPE, attrKind.getName());
                addNodeType(modelNode, modelLabel);
            }
        }
        // collect node type edges and build the model type map
        for (AspectEdge modelEdge : getSource().edgeSet()) {
            TypeLabel typeLabel = modelEdge.getTypeLabel();
            if (typeLabel != null && typeLabel.isNodeType()) {
                addNodeType(modelEdge.source(), typeLabel);
            }
        }
        // check if there are untyped, non-virtual nodes
        Set<AspectNode> untypedNodes =
            new HashSet<AspectNode>(getSource().nodeSet());
        untypedNodes.removeAll(this.modelTyping.keySet());
        Iterator<AspectNode> untypedNodeIter = untypedNodes.iterator();
        while (untypedNodeIter.hasNext()) {
            AspectNode modelNode = untypedNodeIter.next();
            if (modelNode.getKind().isMeta()) {
                untypedNodeIter.remove();
            } else {
                // add a node anyhow, to ensure all edge ends have images
                TypeNode typeNode = new TypeNode(modelNode.getNumber());
                this.model.addNode(typeNode);
                this.modelMap.putNode(modelNode, typeNode);
            }
        }
        for (AspectNode untypedNode : untypedNodes) {
            this.errors.add(new FormatError("Node '%s' has no type label",
                untypedNode));
        }
        // copy the edges from model to model
        for (AspectEdge modelEdge : getSource().edgeSet()) {
            // do not process the node type edges again
            TypeLabel typeLabel = modelEdge.getTypeLabel();
            if (typeLabel == null || !typeLabel.isNodeType()) {
                try {
                    processModelEdge(this.model, this.modelMap, modelEdge);
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
        // transfer graph info such as layout from model to model
        GraphInfo.transfer(getSource(), this.model, this.modelMap);

    }

    /**
     * Adds a node type to the model.
     * @param modelNode the node in the aspect graph that stands for a node type
     * @param typeLabel the node type label
     */
    private void addNodeType(AspectNode modelNode, TypeLabel typeLabel) {
        TypeNode oldTypeNode = this.modelMap.getNode(modelNode);
        if (oldTypeNode != null) {
            this.errors.add(new FormatError(
                "Node '%s' has types '%s' and '%s'", modelNode, typeLabel,
                oldTypeNode.getType()));
        } else {
            this.modelTyping.put(modelNode, typeLabel);
            TypeNode typeNode = this.typeNodeMap.get(typeLabel);
            if (typeNode == null) {
                typeNode = new TypeNode(modelNode.getNumber(), typeLabel);
                typeNode.setAbstract(modelNode.getKind() == ABSTRACT);
                typeNode.setImported(modelNode.hasImport());
                if (modelNode.hasColor()) {
                    typeNode.setColor((Color) modelNode.getColor().getContent());
                }
                this.model.addNode(typeNode);
                this.typeNodeMap.put(typeLabel, typeNode);
                this.resourceTyping.put(typeNode, typeLabel);
            }
            this.modelMap.putNode(modelNode, typeNode);
        }
    }

    /**
     * Processes the information in a model edge by updating the model, element
     * map and subtypes.
     */
    private void processModelEdge(TypeGraph model, TypeModelMap elementMap,
            AspectEdge modelEdge) throws FormatException {
        TypeNode typeSource = elementMap.getNode(modelEdge.source());
        assert typeSource != null : String.format(
            "Source of model edge '%s' not in element map %s",
            modelEdge.source(), elementMap);
        if (typeSource.isImported()) {
            throw new FormatException("Can't change imported type '%s'",
                typeSource.getType(), modelEdge);
        }
        TypeNode typeTarget = elementMap.getNode(modelEdge.target());
        assert typeTarget != null : String.format(
            "Target of model edge '%s' not in element map %s",
            modelEdge.source(), elementMap);
        if (modelEdge.getKind() == SUBTYPE) {
            model.addSubtype(typeTarget, typeSource);
        } else {
            TypeLabel typeLabel = modelEdge.getTypeLabel();
            TypeEdge typeEdge =
                model.addEdge(typeSource, typeLabel, typeTarget);
            typeEdge.setAbstract(modelEdge.getKind() == ABSTRACT);
            elementMap.putEdge(modelEdge, typeEdge);
        }
    }

    /** The resource being constructed. */
    private TypeGraph model;
    /**
     * List of errors in the model that prevent the resource from being constructed.
     */
    private List<FormatError> errors;

    /** Auxiliary mapping from model nodes to types. */
    private Map<AspectNode,TypeLabel> modelTyping =
        new HashMap<AspectNode,TypeLabel>();
    /** Auxiliary from resource nodes to type labels */
    private Map<TypeNode,TypeLabel> resourceTyping =
        new HashMap<TypeNode,TypeLabel>();
    /** Auxiliary from types to resource nodes */
    private Map<TypeLabel,TypeNode> typeNodeMap =
        new HashMap<TypeLabel,TypeNode>();
    /** Map from model to resource nodes. */
    private TypeModelMap modelMap;

    /** Mapping from type graph elements to rule graph elements. */
    public static class TypeModelMap extends ModelMap<TypeNode,TypeEdge> {
        /**
         * Creates a new, empty map.
         */
        public TypeModelMap() {
            super(TypeFactory.instance());
        }

        @Override
        public TypeModelMap newMap() {
            return new TypeModelMap();
        }
    }
}
