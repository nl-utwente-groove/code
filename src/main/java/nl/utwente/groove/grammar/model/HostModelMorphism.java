/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package nl.utwente.groove.grammar.model;

import static nl.utwente.groove.grammar.aspect.AspectKind.REMARK;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.AlgebraFamily;
import nl.utwente.groove.algebra.Constant;
import nl.utwente.groove.grammar.CheckPolicy;
import nl.utwente.groove.grammar.aspect.Aspect;
import nl.utwente.groove.grammar.aspect.AspectContent.ConstContent;
import nl.utwente.groove.grammar.aspect.AspectEdge;
import nl.utwente.groove.grammar.aspect.AspectGraph;
import nl.utwente.groove.grammar.aspect.AspectKind.Category;
import nl.utwente.groove.grammar.aspect.AspectNode;
import nl.utwente.groove.grammar.aspect.NormalAspectGraph;
import nl.utwente.groove.grammar.host.DefaultHostGraph;
import nl.utwente.groove.grammar.host.HostEdge;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.host.ValueNode;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.gui.dialog.GraphPreviewDialog;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/** Record consisting of mapping from aspect graph to host graph, together with the host graph itself. */
@NonNullByDefault
record HostModelMorphism(HostModelMap map, HostGraph target) {
    /** Creates a new instance for a given target graph, with an initially empty map. */
    private HostModelMorphism(HostGraph target) {
        this(new HostModelMap(target.getFactory()), target);
    }

    /** Creates a new, empty instance with a given name. */
    private HostModelMorphism(String name) {
        this(new DefaultHostGraph(name));
    }

    /** Converts a given (normal) aspect graph to an implicitly typed mapped model.
     */
    private HostModelMorphism(@Nullable GrammarModel grammar, AspectGraph source) {
        this(source.getName());
        assert source.isNormal();
        if (HostModel.debug) {
            GraphPreviewDialog.showGraph(source);
        }
        // copy the nodes from source to target
        // first the non-value nodes because their numbers are fixed
        var family = grammar == null
            ? AlgebraFamily.DEFAULT
            : AlgebraFamily.TERM;

        for (AspectNode modelNode : source.nodeSet()) {
            if (!modelNode.has(Category.SORT)) {
                processModelNode(family, modelNode);
            }
        }
        // then the value nodes because their numbers are generated
        for (AspectNode modelNode : source.nodeSet()) {
            if (modelNode.has(Category.SORT)) {
                processModelNode(family, modelNode);
            }
        }
        // copy the edges from source to target
        for (AspectEdge modelEdge : source.edgeSet()) {
            processModelEdge(modelEdge);
        }
        // remove isolated value nodes from the target graph
        for (HostNode modelNode : map().nodeMap().values()) {
            if (modelNode instanceof ValueNode && target().edgeSet(modelNode).isEmpty()) {
                // the node is an isolated value node; remove it
                target().removeNode(modelNode);
            }
        }
    }

    /**
     * Processes the information in a model node by updating the model and
     * element map.
     */
    private void processModelNode(AlgebraFamily family, AspectNode modelNode) {
        // include the node in the model if it is not virtual
        if (!modelNode.has(Category.NESTING) && !modelNode.has(REMARK)) {
            var target = target();
            HostNode nodeImage = null;
            Aspect sortAspect = modelNode.get(Category.SORT);
            if (sortAspect != null) {
                var nodeAlgebra = family.getAlgebra(sortAspect.getContentKind().getSort());
                Constant term = ((ConstContent) sortAspect.getContent()).get();
                nodeImage = target.getFactory().createNode(nodeAlgebra, nodeAlgebra.toValue(term));
                target.addNode(nodeImage);
            } else {
                nodeImage = target.addNode(modelNode.getNumber());
            }
            map().putNode(modelNode, nodeImage);
        }
    }

    /**
     * Processes the information in a model edge by updating the resource, element
     * map and subtypes.
     */
    private void processModelEdge(AspectEdge modelEdge) {
        if (!modelEdge.has(Category.LABEL)) {
            return;
        }
        var map = map();
        HostNode hostSource = map.getNode(modelEdge.source());
        assert hostSource != null : String
            .format("Source of '%s' is not in element map %s", modelEdge.source(), map);
        HostNode hostNode = map.getNode(modelEdge.target());
        assert hostNode != null : String
            .format("Target of '%s' is not in element map %s", modelEdge.target(), map);
        TypeLabel hostLabel = modelEdge.getTypeLabel();
        assert hostLabel != null && !hostLabel.isSort() : String
            .format("Inappropriate label %s", hostLabel);
        HostEdge hostEdge = target().addEdge(hostSource, hostLabel, hostNode);
        assert hostEdge != null;
        map.putEdge(modelEdge, hostEdge);
    }

    /** Convenience method to retrieve the associated type graph. */
    TypeGraph getTypeGraph() {
        return this.target.getTypeGraph();
    }

    /** Converts this (implicitly typed) mapped model to a typed one.
     * @throws FormatException if there are typing errors in the host graph
     */
    private HostModelMorphism toTyped(TypeGraph type) throws FormatException {
        var typing = type.analyzeHost(target());
        // typing was successful; adapt the result and element map
        var target = typing.createImage(target().getName());
        var typeMap = new HostModelMap(this.target.getFactory());
        for (var nodeEntry : map().nodeMap().entrySet()) {
            HostNode typedNode = typing.getNode(nodeEntry.getValue());
            if (typedNode != null) {
                typeMap.putNode(nodeEntry.getKey(), typedNode);
            }
        }
        for (var edgeEntry : map().edgeMap().entrySet()) {
            var typedEdge = typing.getEdge(edgeEntry.getValue());
            if (typedEdge != null) {
                typeMap.putEdge(edgeEntry.getKey(), typedEdge);
            }
        }
        return new HostModelMorphism(typeMap, target);
    }

    private HostModelMorphism toDenormalised(NormalAspectGraph normalSource) {
        var elementMap = map();
        var sourceMap = new HostModelMap(target().getFactory());
        for (var ne : normalSource.sourceToNormalMap().nodeMap().entrySet()) {
            var hostNode = elementMap.getNode(ne.getValue());
            if (hostNode != null) {
                sourceMap.putNode(ne.getKey(), hostNode);
            }
        }
        for (var ee : normalSource.sourceToNormalMap().edgeMap().entrySet()) {
            var hostEdge = elementMap.getEdge(ee.getValue());
            if (hostEdge != null) {
                sourceMap.putEdge(ee.getKey(), hostEdge);
            }
        }
        return new HostModelMorphism(sourceMap, target());
    }

    /** Transfers properties from given source to target and adds errors computed over the target. */
    private void transferProperties(AspectGraph source, FormatErrorSet errors) {
        // transfer graph info such as layout from model to resource
        GraphInfo.transferProperties(source, target(), map());
        target().setErrors(errors.applyInverse(map()));
        target().setFixed();
    }

    /**
     * Constructor method to compute a mapped model from a given aspect graph.
     */
    static HostModelMorphism instance(@Nullable GrammarModel grammar, AspectGraph source) {
        var normalSource = source.normalise();
        var result = new HostModelMorphism(grammar, normalSource);
        var errors = new FormatErrorSet(normalSource.getErrors());
        if (grammar != null) {
            try {
                result = result.toTyped(grammar.getTypeGraph());
                if (grammar.getProperties().getTypePolicy() != CheckPolicy.OFF) {
                    result.target().checkTypeConstraints().throwException();
                }
            } catch (FormatException e) {
                errors.addAll(e.getErrors());
            }
        }
        // for the result value we need a map from the source to the host graph,
        // whereas elementMap goes from the normalised source
        if (normalSource instanceof NormalAspectGraph ng) {
            result = result.toDenormalised(ng);
        }
        result.transferProperties(source, errors);
        return result;
    }
}