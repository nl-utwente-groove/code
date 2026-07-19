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
class HostModelMorphism {
    /** Converts a given (normal) aspect graph to an implicitly typed mapped model.
     */
    private HostModelMorphism(@Nullable GrammarModel grammar, AspectGraph source) {
        this.source = source;
        var target = new DefaultHostGraph(source.getName());
        var map = new HostModelMap(target.getFactory());
        var normalSource = source.normalise();
        var errors = new FormatErrorSet();
        if (debug) {
            GraphPreviewDialog.showGraph(normalSource);
        }
        // copy the nodes from source to target
        // first the non-value nodes because their numbers are fixed
        var family = grammar == null
            ? AlgebraFamily.DEFAULT
            : AlgebraFamily.TERM;

        for (AspectNode modelNode : normalSource.nodeSet()) {
            if (!modelNode.has(Category.SORT)) {
                processModelNode(family, modelNode, map, target);
            }
        }
        // then the value nodes because their numbers are generated
        for (AspectNode modelNode : normalSource.nodeSet()) {
            if (modelNode.has(Category.SORT)) {
                processModelNode(family, modelNode, map, target);
            }
        }
        // copy the edges from source to target
        for (AspectEdge modelEdge : normalSource.edgeSet()) {
            processModelEdge(modelEdge, map, target);
        }
        // remove isolated value nodes from the target graph
        for (HostNode modelNode : map.nodeMap().values()) {
            if (modelNode instanceof ValueNode && target.edgeSet(modelNode).isEmpty()) {
                // the node is an isolated value node; remove it
                target.removeNode(modelNode);
            }
        }
        if (normalSource instanceof NormalAspectGraph ng) {
            errors.addAll(ng.getOriginErrors());
            // modify the map so it goes from source rather than normalSource
            var originMap = new HostModelMap(target.getFactory());
            for (var ne : ng.originToNormalMap().nodeMap().entrySet()) {
                var hostNode = map.getNode(ne.getValue());
                if (hostNode != null) {
                    originMap.putNode(ne.getKey(), hostNode);
                }
            }
            for (var ee : ng.originToNormalMap().edgeMap().entrySet()) {
                var hostEdge = map.getEdge(ee.getValue());
                if (hostEdge != null) {
                    originMap.putEdge(ee.getKey(), hostEdge);
                }
            }
            map = originMap;
        } else {
            errors.addAll(source.getErrors());
        }
        // possibly add typing
        if (errors.isEmpty() && grammar != null) {
            try {
                var type = grammar.getTypeGraph();
                var typing = type.analyzeHost(target);
                // typing was successful; adapt the target and map
                target = typing.createImage(target.getName());
                var typeMap = new HostModelMap(target.getFactory());
                for (var nodeEntry : map.nodeMap().entrySet()) {
                    HostNode typedNode = typing.getNode(nodeEntry.getValue());
                    if (typedNode != null) {
                        typeMap.putNode(nodeEntry.getKey(), typedNode);
                    }
                }
                for (var edgeEntry : map.edgeMap().entrySet()) {
                    var typedEdge = typing.getEdge(edgeEntry.getValue());
                    if (typedEdge != null) {
                        typeMap.putEdge(edgeEntry.getKey(), typedEdge);
                    }
                }
                if (grammar.getProperties().getTypePolicy() != CheckPolicy.OFF) {
                    errors.addAll(target.checkTypeConstraints());
                }
                map = typeMap;
            } catch (FormatException exc) {
                errors.applyInverse(map);
                errors.addAll(exc.getErrors());
            }
        }
        // transfer graph info such as layout from model to resource
        GraphInfo.transferProperties(source, target, map);
        target.setFixed();
        errors.setFixed();
        this.map = map;
        this.target = target;
        this.errors = errors;
    }

    /** Returns the source aspect graph of this morphism. */
    AspectGraph source() {
        return this.source;
    }

    private final AspectGraph source;

    /**
     * Returns the source-to-target map of this morphism.
     */
    HostModelMap map() {
        return this.map;
    }

    private final HostModelMap map;

    /**
     * @return Returns the target host graph of this morphism.
     */
    HostGraph target() {
        return this.target;
    }

    private final HostGraph target;

    /** Checks whether the set of errors is non-empty. */
    boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    /**
     * Returns the errors (which are in the context of the source graph).
     */
    FormatErrorSet getErrors() {
        return this.errors;
    }

    private final FormatErrorSet errors;

    /**
     * Processes the information in a model node by updating the model and
     * element map.
     */
    private void processModelNode(AlgebraFamily family, AspectNode modelNode, HostModelMap map,
                                  HostGraph target) {
        // include the node in the model if it is not virtual
        if (!modelNode.has(Category.NESTING) && !modelNode.has(REMARK)) {
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
            map.putNode(modelNode, nodeImage);
        }
    }

    /**
     * Processes the information in a model edge by updating the resource, element
     * map and subtypes.
     */
    private void processModelEdge(AspectEdge modelEdge, HostModelMap map, HostGraph target) {
        if (!modelEdge.has(Category.LABEL)) {
            return;
        }
        HostNode hostSource = map.getNode(modelEdge.source());
        assert hostSource != null : String
            .format("Source of '%s' is not in element map %s", modelEdge.source(), map);
        HostNode hostNode = map.getNode(modelEdge.target());
        assert hostNode != null : String
            .format("Target of '%s' is not in element map %s", modelEdge.target(), map);
        TypeLabel hostLabel = modelEdge.getTypeLabel();
        assert hostLabel != null && !hostLabel.isSort() : String
            .format("Inappropriate label %s", hostLabel);
        HostEdge hostEdge = target.addEdge(hostSource, hostLabel, hostNode);
        assert hostEdge != null;
        map.putEdge(modelEdge, hostEdge);
    }

    /** Convenience method to retrieve the associated type graph. */
    TypeGraph getTypeGraph() {
        return target().getTypeGraph();
    }

    /** Sets the debug mode, causing the normalised graphs to be shown in a dialog. */
    public static void setDebug(boolean debug) {
        HostModelMorphism.debug = debug;
    }

    static private boolean debug;

    /**
     * Constructor method to compute a mapped model from a given aspect graph.
     */
    static HostModelMorphism instance(@Nullable GrammarModel grammar, AspectGraph source) {
        return new HostModelMorphism(grammar, source);
    }
}