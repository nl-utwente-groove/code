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
import static groove.view.aspect.AspectKind.DEFAULT;
import static groove.view.aspect.AspectKind.SUBTYPE;
import groove.algebra.SignatureKind;
import groove.graph.GraphInfo;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
    public TypeModel(GrammarModel grammar, AspectGraph source) {
        super(grammar, source);
        source.testFixed(true);
    }

    @Override
    public boolean isEnabled() {
        return getGrammar().getProperties().getTypeNames().contains(getName());
    }

    @Override
    public TypeModelMap getMap() {
        synchronise();
        return this.modelMap;
    }

    /** 
     * Returns the set of labels used in this graph.
     * @return the set of labels, or {@code null} if the model could not be computed 
     */
    @Override
    public Set<TypeLabel> getLabels() {
        TypeGraph typeGraph = getResource();
        return typeGraph == null ? Collections.<TypeLabel>emptySet()
                : typeGraph.getLabelStore().getLabels();
    }

    @Override
    TypeGraph compute() throws FormatException {
        if (getSource().hasErrors()) {
            throw new FormatException(getSource().getErrors());
        }
        Collection<FormatError> errors = createErrors();
        TypeGraph result = new TypeGraph(getName());
        this.modelMap = new TypeModelMap();
        // collect primitive type nodes
        for (AspectNode modelNode : getSource().nodeSet()) {
            AspectKind attrKind = modelNode.getAttrKind();
            if (attrKind != DEFAULT) {
                TypeLabel typeLabel =
                    TypeLabel.createLabel(NODE_TYPE, attrKind.getName());
                try {
                    TypeNode typeNode = getNodeType(modelNode, typeLabel);
                    result.addNode(typeNode);
                } catch (FormatException e) {
                    errors.addAll(e.getErrors());
                }
            }
        }
        // collect node type edges and build the model type map
        for (AspectEdge modelEdge : getSource().edgeSet()) {
            TypeLabel typeLabel = modelEdge.getTypeLabel();
            if (typeLabel != null && typeLabel.isNodeType()) {
                AspectNode modelNode = modelEdge.source();
                try {
                    TypeNode typeNode = getNodeType(modelNode, typeLabel);
                    result.addNode(typeNode);
                } catch (FormatException e) {
                    errors.addAll(e.getErrors());
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new FormatException(errors);
        }
        // check if there are untyped, non-virtual nodes
        Set<AspectNode> untypedNodes =
            new HashSet<AspectNode>(getSource().nodeSet());
        untypedNodes.removeAll(this.modelMap.nodeMap().keySet());
        Iterator<AspectNode> untypedNodeIter = untypedNodes.iterator();
        while (untypedNodeIter.hasNext()) {
            AspectNode modelNode = untypedNodeIter.next();
            if (modelNode.getKind().isMeta()) {
                untypedNodeIter.remove();
            } else {
                // add a node anyhow, to ensure all edge ends have images
                TypeNode typeNode = new TypeNode(modelNode.getNumber());
                result.addNode(typeNode);
                this.modelMap.putNode(modelNode, typeNode);
            }
        }
        for (AspectNode untypedNode : untypedNodes) {
            errors.add(new FormatError("Node '%s' has no type label",
                untypedNode));
        }
        // copy the edges from model to model
        for (AspectEdge modelEdge : getSource().edgeSet()) {
            // do not process the node type edges again
            TypeLabel typeLabel = modelEdge.getTypeLabel();
            if (!modelEdge.getKind().isMeta()
                && (typeLabel == null || !typeLabel.isNodeType())) {
                try {
                    processModelEdge(result, this.modelMap, modelEdge);
                } catch (FormatException exc) {
                    errors.addAll(exc.getErrors());
                }
            }
        }
        if (errors.isEmpty()) {
            try {
                result.test();
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        if (errors.isEmpty()) {
            // transfer graph info such as layout from model to resource
            GraphInfo.transfer(getSource(), result, this.modelMap);
            result.setFixed();
            return result;
        } else {
            throw new FormatException(transferErrors(errors, this.modelMap));
        }
    }

    /**
     * Returns a node type for a given model node and type label.
     * Also adds the type to the {@link #modelMap}.
     * @param modelNode the node in the aspect graph that stands for a node type
     * @param typeLabel the node type label
     */
    private TypeNode getNodeType(AspectNode modelNode, TypeLabel typeLabel)
        throws FormatException {
        TypeNode oldTypeNode = this.modelMap.getNode(modelNode);
        if (oldTypeNode != null) {
            throw new FormatException("Duplicate types '%s' and '%s'",
                typeLabel.text(), oldTypeNode.getLabel().text(), modelNode);
        }
        TypeNode typeNode;
        SignatureKind signature = modelNode.getAttrKind().getSignature();
        if (signature == null) {
            typeNode = getTypeNode(modelNode.getNumber(), typeLabel);
        } else {
            typeNode = TypeNode.getDataType(signature);
        }
        if (modelNode.getKind() == ABSTRACT) {
            typeNode.setAbstract(true);
        }
        if (modelNode.hasImport()) {
            typeNode.setImported(true);
        }
        if (modelNode.hasColor()) {
            typeNode.setColor((Color) modelNode.getColor().getContent());
        }
        if (modelNode.isEdge()) {
            typeNode.setLabelPattern(modelNode.getEdgePattern());
        }
        this.modelMap.putNode(modelNode, typeNode);
        return typeNode;
    }

    /**
     * Returns a type node for a given type label,
     * with other aspects taken from a given model node.
     * Reuses previously stored type nodes whenever possible.
     */
    private TypeNode getTypeNode(int nr, TypeLabel typeLabel) {
        TypeNode typeNode = this.typeNodeMap.get(typeLabel);
        if (typeNode == null) {
            typeNode = new TypeNode(nr, typeLabel);
            this.typeNodeMap.put(typeLabel, typeNode);
        }
        return typeNode;
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
                typeSource.getLabel(), modelEdge);
        }
        TypeNode typeTarget = elementMap.getNode(modelEdge.target());
        assert typeTarget != null : String.format(
            "Target of model edge '%s' not in element map %s",
            modelEdge.source(), elementMap);
        TypeEdge typeEdge = null;
        if (modelEdge.getAttrKind().hasSignature()) {
            TypeNode typeNode = TypeNode.getDataType(modelEdge.getSignature());
            typeEdge =
                model.addEdge(typeSource,
                    modelEdge.getAttrAspect().getContentString(), typeNode);
        } else if (modelEdge.getKind() == SUBTYPE) {
            model.addInheritance(typeTarget, typeSource);
        } else {
            TypeLabel typeLabel = modelEdge.getTypeLabel();
            typeEdge = model.addEdge(typeSource, typeLabel, typeTarget);
            typeEdge.setAbstract(modelEdge.getKind() == ABSTRACT);
        }
        if (typeEdge != null) {
            elementMap.putEdge(modelEdge, typeEdge);
        }
    }

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
