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
 * $Id$
 */
package groove.grammar.model;

import static groove.grammar.aspect.AspectKind.ABSTRACT;
import static groove.grammar.aspect.AspectKind.DEFAULT;
import static groove.grammar.aspect.AspectKind.SUBTYPE;
import static groove.graph.EdgeRole.NODE_TYPE;

import java.awt.Color;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import groove.algebra.Sort;
import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.AspectKind;
import groove.grammar.aspect.AspectNode;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeFactory;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeLabel;
import groove.grammar.type.TypeNode;
import groove.graph.GraphInfo;
import groove.util.parse.FormatErrorSet;
import groove.util.parse.FormatException;

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
    void notifyWillRebuild() {
        super.notifyWillRebuild();
        this.typeMap = null;
    }

    @Override
    boolean isShouldRebuild() {
        boolean result = super.isShouldRebuild();
        if (result) {
            result = isStale(ResourceKind.TYPE);
        }
        return result;
    }

    @Override
    public TypeModelMap getMap() {
        synchronise();
        if (hasErrors()) {
            throw new IllegalStateException();
        }
        return this.modelMap;
    }

    @Override
    public TypeModelMap getTypeMap() {
        synchronise();
        return this.typeMap;
    }

    /**
     * Returns the set of labels used in this graph.
     * @return the set of labels, or {@code null} if the model could not be computed
     */
    @Override
    public Set<TypeLabel> getLabels() {
        TypeGraph typeGraph = getResource();
        return typeGraph == null ? Collections.<TypeLabel>emptySet() : typeGraph.getLabels();
    }

    @Override
    TypeGraph compute() throws FormatException {
        GraphInfo.throwException(getSource());
        FormatErrorSet errors = createErrors();
        TypeGraph result = new TypeGraph(getQualName());
        TypeFactory factory = result.getFactory();
        this.modelMap = new TypeModelMap(factory);
        // collect primitive type nodes
        for (AspectNode modelNode : getSource().nodeSet()) {
            AspectKind attrKind = modelNode.getAttrKind();
            if (attrKind != DEFAULT) {
                TypeLabel typeLabel = TypeLabel.createLabel(NODE_TYPE, attrKind.getName());
                try {
                    addNodeType(modelNode, typeLabel, factory);
                } catch (FormatException e) {
                    errors.addAll(e.getErrors());
                }
            }
        }
        // collect node type edges and build the model type map
        for (AspectEdge modelEdge : getSource().edgeSet()) {
            TypeLabel typeLabel = modelEdge.getTypeLabel();
            if (typeLabel != null && typeLabel.getRole() == NODE_TYPE) {
                AspectNode modelNode = modelEdge.source();
                try {
                    addNodeType(modelNode, typeLabel, factory);
                } catch (FormatException e) {
                    errors.addAll(e.getErrors());
                }
            }
        }
        errors.throwException();
        // check if there are untyped, non-virtual nodes
        Set<AspectNode> untypedNodes = new HashSet<>(getSource().nodeSet());
        untypedNodes.removeAll(this.modelMap.nodeMap()
            .keySet());
        Iterator<AspectNode> untypedNodeIter = untypedNodes.iterator();
        while (untypedNodeIter.hasNext()) {
            AspectNode modelNode = untypedNodeIter.next();
            if (modelNode.getKind()
                .isMeta()) {
                untypedNodeIter.remove();
            } else {
                // add a node anyhow, to ensure all edge ends have images
                TypeNode typeNode = factory.getTopNode();
                result.addNode(typeNode);
                this.modelMap.putNode(modelNode, typeNode);
            }
        }
        for (AspectNode untypedNode : untypedNodes) {
            errors.add("Node '%s' has no type label", untypedNode);
        }
        // copy the edges from model to model
        for (AspectEdge modelEdge : getSource().edgeSet()) {
            // do not process the node type edges again
            TypeLabel typeLabel = modelEdge.getTypeLabel();
            if (!modelEdge.getKind()
                .isMeta() && (typeLabel == null || typeLabel.getRole() != NODE_TYPE)) {
                try {
                    processModelEdge(result, this.modelMap, modelEdge);
                } catch (FormatException exc) {
                    errors.addAll(exc.getErrors());
                }
            }
        }
        transferErrors(errors, this.modelMap).throwException();
        // transfer graph info such as layout from model to resource
        GraphInfo.transfer(getSource(), result, this.modelMap);
        result.setFixed();
        try {
            result.test();
        } catch (FormatException exc) {
            transferErrors(exc.getErrors(), this.modelMap).throwException();
        }
        this.typeMap = this.modelMap;
        return result;
    }

    /**
     * Adds a node type for a given model node and type label
     * to the type graph and {@link #modelMap}.
     * @param modelNode the node in the aspect graph that stands for a node type
     * @param typeLabel the node type label
     */
    private void addNodeType(AspectNode modelNode, TypeLabel typeLabel, TypeFactory factory)
        throws FormatException {
        TypeNode oldTypeNode = this.modelMap.getNode(modelNode);
        if (oldTypeNode != null) {
            throw new FormatException("Duplicate types '%s' and '%s'", typeLabel.text(),
                oldTypeNode.label()
                    .text(),
                modelNode);
        }
        TypeNode typeNode;
        Sort signature = modelNode.getAttrKind()
            .getSort();
        if (signature == null) {
            typeNode = factory.createNode(typeLabel);
        } else {
            typeNode = factory.getDataType(signature);
        }
        if (modelNode.getKind() == ABSTRACT) {
            if (signature != null) {
                throw new FormatException("Data type '%s' cannot be abstract", typeLabel.text(),
                    modelNode);
            }
            typeNode.setAbstract(true);
        }
        if (modelNode.hasImport()) {
            if (signature != null) {
                throw new FormatException("Data type '%s' cannot be imported", typeLabel.text(),
                    modelNode);
            }
            typeNode.setImported(true);
        }
        if (modelNode.hasColor()) {
            typeNode.setColor((Color) modelNode.getColor()
                .getContent());
        }
        if (modelNode.isEdge()) {
            if (signature != null) {
                throw new FormatException("Data type '%s' cannot be a nodified edge",
                    typeLabel.text(), modelNode);
            }
            typeNode.setLabelPattern(modelNode.getEdgePattern());
        }
        this.modelMap.putNode(modelNode, typeNode);
    }

    /**
     * Processes the information in a model edge by updating the model, element
     * map and subtypes.
     */
    private void processModelEdge(TypeGraph model, TypeModelMap elementMap, AspectEdge modelEdge)
        throws FormatException {
        TypeNode typeSource = elementMap.getNode(modelEdge.source());
        assert typeSource != null : String.format("Source of model edge '%s' not in element map %s",
            modelEdge.source(),
            elementMap);
        if (typeSource.isImported()) {
            throw new FormatException("Can't change imported type '%s'", typeSource.label(),
                modelEdge);
        }
        TypeNode typeTarget = elementMap.getNode(modelEdge.target());
        assert typeTarget != null : String.format("Target of model edge '%s' not in element map %s",
            modelEdge.source(),
            elementMap);
        TypeEdge typeEdge = null;
        if (modelEdge.getAttrKind()
            .hasSort()) {
            TypeNode typeNode = model.getFactory()
                .getDataType(modelEdge.getSignature());
            typeEdge = model.addEdge(typeSource, modelEdge.getAttrAspect()
                .getContentString(), typeNode);
        } else if (modelEdge.getKind() == SUBTYPE) {
            model.addInheritance(typeSource, typeTarget);
        } else {
            TypeLabel typeLabel = modelEdge.getTypeLabel();
            typeEdge = model.addEdge(typeSource, typeLabel, typeTarget);
            typeEdge.setComposite(modelEdge.isComposite());
            typeEdge.setInMult(modelEdge.getInMult());
            typeEdge.setOutMult(modelEdge.getOutMult());
            typeEdge.setAbstract(modelEdge.getKind() == ABSTRACT);
            typeEdge.setComposite(modelEdge.isComposite());
        }
        if (typeEdge != null) {
            elementMap.putEdge(modelEdge, typeEdge);
        }
    }

    /** Map from model to resource nodes. */
    private TypeModelMap modelMap;
    /** Map from source model to types; equals {@link #modelMap} if there are no errors,
     * {@code null} otherwise. */
    private TypeModelMap typeMap;
}
