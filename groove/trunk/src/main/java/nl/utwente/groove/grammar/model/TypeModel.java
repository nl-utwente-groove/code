/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.grammar.model;

import static nl.utwente.groove.grammar.aspect.AspectKind.ABSTRACT;
import static nl.utwente.groove.grammar.aspect.AspectKind.COMPOSITE;
import static nl.utwente.groove.grammar.aspect.AspectKind.EDGE;
import static nl.utwente.groove.grammar.aspect.AspectKind.IMPORT;
import static nl.utwente.groove.grammar.aspect.AspectKind.REMARK;
import static nl.utwente.groove.grammar.aspect.AspectKind.SUBTYPE;
import static nl.utwente.groove.graph.EdgeRole.NODE_TYPE;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.type.TypeEdge;
import nl.utwente.groove.grammar.type.TypeFactory;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.grammar.type.TypeNode;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

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
        return typeGraph == null
            ? Collections.<TypeLabel>emptySet()
            : typeGraph.getLabels();
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
            var sortKind = modelNode.getKind(Category.SORT);
            if (sortKind != null) {
                TypeLabel typeLabel
                    = TypeLabel.createLabel(NODE_TYPE, sortKind.getSort().getName());
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
            if (typeLabel != null && typeLabel.hasRole(NODE_TYPE)) {
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
        untypedNodes.removeAll(this.modelMap.nodeMap().keySet());
        Iterator<AspectNode> untypedNodeIter = untypedNodes.iterator();
        while (untypedNodeIter.hasNext()) {
            AspectNode modelNode = untypedNodeIter.next();
            if (modelNode.has(Category.NESTING) || modelNode.has(REMARK)) {
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
            if (!modelEdge.has(Category.NESTING) && !modelEdge.has(REMARK)
                && (typeLabel == null || !typeLabel.hasRole(NODE_TYPE))) {
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
    private void addNodeType(AspectNode modelNode, TypeLabel typeLabel,
                             TypeFactory factory) throws FormatException {
        TypeNode oldTypeNode = this.modelMap.getNode(modelNode);
        if (oldTypeNode != null) {
            throw new FormatException("Duplicate types '%s' and '%s'", typeLabel.text(),
                oldTypeNode.label().text(), modelNode);
        }
        TypeNode typeNode;
        var sortKind = modelNode.getKind(Category.SORT);
        if (sortKind == null) {
            typeNode = factory.createNode(typeLabel);
        } else {
            typeNode = factory.getDataType(sortKind.getSort());
        }
        if (modelNode.has(ABSTRACT)) {
            if (sortKind != null) {
                throw new FormatException("Data type '%s' cannot be abstract", typeLabel.text(),
                    modelNode);
            }
            typeNode.setAbstract(true);
        }
        if (modelNode.has(IMPORT)) {
            if (sortKind != null) {
                throw new FormatException("Data type '%s' cannot be imported", typeLabel.text(),
                    modelNode);
            }
            typeNode.setImported(true);
        }
        if (modelNode.hasColor()) {
            typeNode.setColor(modelNode.getColor());
        }
        if (modelNode.has(EDGE)) {
            if (sortKind != null) {
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
    private void processModelEdge(TypeGraph model, TypeModelMap elementMap,
                                  AspectEdge modelEdge) throws FormatException {
        TypeNode typeSource = elementMap.getNode(modelEdge.source());
        assert typeSource != null : String
            .format("Source of model edge '%s' not in element map %s", modelEdge.source(),
                    elementMap);
        if (typeSource.isImported()) {
            throw new FormatException("Can't change imported type '%s'", typeSource.label(),
                modelEdge);
        }
        TypeNode typeTarget = elementMap.getNode(modelEdge.target());
        assert typeTarget != null : String
            .format("Target of model edge '%s' not in element map %s", modelEdge.source(),
                    elementMap);
        TypeEdge typeEdge = null;
        var sortAspect = modelEdge.get(Category.SORT);
        if (sortAspect != null) {
            TypeNode typeNode = model.getFactory().getDataType(sortAspect.getKind().getSort());
            typeEdge = model.addEdge(typeSource, sortAspect.getContentString(), typeNode);
        } else if (modelEdge.has(SUBTYPE)) {
            model.addInheritance(typeSource, typeTarget);
        } else {
            TypeLabel typeLabel = modelEdge.getTypeLabel();
            typeEdge = model.addEdge(typeSource, typeLabel, typeTarget);
            typeEdge.setComposite(modelEdge.has(COMPOSITE));
            typeEdge.setInMult(modelEdge.getInMult());
            typeEdge.setOutMult(modelEdge.getOutMult());
            typeEdge.setAbstract(modelEdge.has(ABSTRACT));
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
