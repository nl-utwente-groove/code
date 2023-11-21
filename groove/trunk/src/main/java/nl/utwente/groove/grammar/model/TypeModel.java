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
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

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
 * @version $Revision$
 */
@NonNullByDefault
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
        this.converter = null;
    }

    @Override
    public TypeModelMap getMap() {
        var result = getTypeMap();
        if (result == null) {
            throw new IllegalStateException();
        }
        return result;
    }

    @Override
    public @Nullable TypeModelMap getTypeMap() {
        synchronise();
        return this.typeModelMap;
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

    /** Lazily creates and returns the aspect-to-type-graph converter for this model. */
    private Converter getConverter() {
        var result = this.converter;
        if (result == null) {
            result = this.converter = createConverter();
        }
        return result;
    }

    /** Factory method for the aspect-to-type-graph converter. */
    Converter createConverter() {
        return new Converter(getSource());
    }

    /** Aspect-to-type-graph converter for this model. */
    private @Nullable Converter converter;

    @Override
    TypeGraph compute() throws FormatException {
        var converter = getConverter();
        var result = converter.convert();
        result.setFixed();
        var typeModelMap = converter.getModelMap();
        try {
            result.test();
        } catch (FormatException exc) {
            exc.getErrors().wrap(typeModelMap).throwException();
        }
        this.typeModelMap = typeModelMap;
        return result;
    }

    /** Map from model to resource nodes.
     * Non-{@code null} if the model has been constructed without errors.
     */
    private @Nullable TypeModelMap typeModelMap;

    /** Converter class to construct a TypeGraph from an AspectGraph. */
    static class Converter {
        Converter(AspectGraph source) {
            this.source = source;
        }

        private final AspectGraph source;

        TypeGraph convert() throws FormatException {
            // return the result if it had already been computed
            if (this.result != null) {
                return this.result;
            }
            // throw the errors in the previous computation, if any
            var errors = this.errors;
            errors.throwException();
            // this is the first attempt to construct the graph
            var source = this.source;
            errors.addAll(source.getErrors());
            // we don't try anything if the source already has errors
            errors.throwException();
            var result = new TypeGraph(source.getQualName());
            TypeFactory factory = result.getFactory();
            var modelMap = new TypeModelMap(factory);
            // collect primitive type nodes
            for (AspectNode node : source.nodeSet()) {
                var sortKind = node.getKind(Category.SORT);
                if (sortKind != null) {
                    TypeLabel typeLabel
                        = TypeLabel.createLabel(NODE_TYPE, sortKind.getSort().getName());
                    try {
                        addNodeType(node, typeLabel, modelMap);
                    } catch (FormatException e) {
                        errors.addAll(e.getErrors());
                    }
                }
            }
            // collect node type edges and build the model type map
            for (AspectEdge edge : source.edgeSet()) {
                TypeLabel typeLabel = edge.getTypeLabel();
                if (typeLabel != null && typeLabel.hasRole(NODE_TYPE)) {
                    AspectNode node = edge.source();
                    try {
                        addNodeType(node, typeLabel, modelMap);
                    } catch (FormatException e) {
                        errors.addAll(e.getErrors());
                    }
                }
            }
            errors.wrap(modelMap).throwException();
            // check if there are untyped, non-virtual nodes
            for (var node : source.nodeSet()) {
                if (!modelMap.nodeMap().containsKey(node) && !node.has(REMARK)) {
                    // add a node anyhow, to ensure all edge ends have images
                    TypeNode typeNode = factory.getTopNode();
                    result.addNode(typeNode);
                    modelMap.putNode(node, typeNode);
                    errors.add("Node '%s' has no type label", node);
                }
            }
            // copy the edges from source graph to model
            for (AspectEdge edge : source.edgeSet()) {
                // do not process the node type edges again
                TypeLabel typeLabel = edge.getTypeLabel();
                if (!edge.has(REMARK) && (typeLabel == null || !typeLabel.hasRole(NODE_TYPE))) {
                    try {
                        processModelEdge(result, edge, modelMap);
                    } catch (FormatException exc) {
                        errors.addAll(exc.getErrors());
                    }
                }
            }
            errors.wrap(modelMap).throwException();
            // transfer graph info such as layout from model to resource
            GraphInfo.transferProperties(source, result, modelMap);
            setModelMap(modelMap);
            this.result = result;
            return result;
        }

        private @Nullable TypeGraph result;

        /**
         * Adds a node type for a given model node and type label
         * to the type graph and {@link #modelMap}.
         * @param modelNode the node in the aspect graph that stands for a node type
         * @param typeLabel the node type label
         * @param modelMap aspect-to-type-graph mapping
         */
        private void addNodeType(AspectNode modelNode, TypeLabel typeLabel,
                                 TypeModelMap modelMap) throws FormatException {
            TypeNode oldTypeNode = modelMap.getNode(modelNode);
            if (oldTypeNode != null) {
                throw new FormatException("Duplicate types '%s' and '%s'", typeLabel.text(),
                    oldTypeNode.label().text(), modelNode, oldTypeNode);
            }
            var factory = modelMap.getFactory();
            TypeNode typeNode;
            var sortKind = modelNode.getKind(Category.SORT);
            if (sortKind == null) {
                typeNode = factory.createNode(typeLabel);
            } else {
                typeNode = factory.getDataType(sortKind.getSort());
            }
            if (modelNode.has(ABSTRACT)) {
                typeNode.setAbstract();
            }
            if (modelNode.has(IMPORT)) {
                typeNode.setImported();
            }
            if (modelNode.hasColor()) {
                typeNode.setColor(modelNode.getColor());
            }
            if (modelNode.has(EDGE)) {
                typeNode.setLabelPattern(modelNode.getEdgePattern());
            }
            modelMap.putNode(modelNode, typeNode);
        }

        /**
         * Processes the information in a model edge by updating the model, element
         * map and subtypes.
         */
        private void processModelEdge(TypeGraph model, AspectEdge modelEdge,
                                      TypeModelMap modelMap) throws FormatException {
            TypeNode typeSource = modelMap.getNode(modelEdge.source());
            assert typeSource != null : String
                .format("Source of model edge '%s' not in element map %s", modelEdge.source(),
                        modelMap);
            if (typeSource.isImported()) {
                throw new FormatException("Can't change imported type '%s'", typeSource.label(),
                    modelEdge);
            }
            TypeNode typeTarget = modelMap.getNode(modelEdge.target());
            assert typeTarget != null : String
                .format("Target of model edge '%s' not in element map %s", modelEdge.source(),
                        modelMap);
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
                modelMap.putEdge(modelEdge, typeEdge);
            }
        }

        private void setModelMap(TypeModelMap modelMap) {
            this.modelMap = modelMap;
        }

        /** Returns the mapping from source element to type graph elements.
         * This is only non-{@code null} if {@link #convert()} has been invoked
         * and has not thrown an exception.
         */
        @Nullable
        TypeModelMap getModelMap() {
            return this.modelMap;
        }

        private @Nullable TypeModelMap modelMap;

        FormatErrorSet getErrors() {
            return this.errors;
        }

        private final FormatErrorSet errors = new FormatErrorSet();
    }
}
