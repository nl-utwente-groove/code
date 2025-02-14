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

import static nl.utwente.groove.grammar.aspect.AspectKind.REMARK;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import nl.utwente.groove.algebra.Algebra;
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
import nl.utwente.groove.grammar.host.HostFactory;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.grammar.host.HostNode;
import nl.utwente.groove.grammar.host.ValueNode;
import nl.utwente.groove.grammar.type.ImplicitTypeGraph;
import nl.utwente.groove.grammar.type.TypeGraph;
import nl.utwente.groove.grammar.type.TypeLabel;
import nl.utwente.groove.graph.GraphInfo;
import nl.utwente.groove.gui.dialog.GraphPreviewDialog;
import nl.utwente.groove.util.Factory;
import nl.utwente.groove.util.Pair;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Graph-based model of a host graph graph. Attribute values are represented
 * by {@link ValueNode}s.
 * @author Arend Rensink
 * @version $Rev$
 */
public class HostModel extends GraphBasedModel<HostGraph> {
    /**
     * Constructs an instance from a given aspect graph.
     * @param grammar the grammar to which the host graph belongs; may be {@code null} if
     * there is no enclosing grammar
     */
    public HostModel(GrammarModel grammar, AspectGraph source) {
        super(grammar, source);
        source.testFixed(true);
        if (grammar == null) {
            this.typeGraph = ImplicitTypeGraph.newInstance(getLabels());
        } else {
            this.typeGraph = grammar.getTypeGraph();
        }
        setDependencies(ResourceKind.TYPE, ResourceKind.PROPERTIES);
    }

    /**
     * Constructs the host graph from this resource.
     * @throws FormatException if the resource contains errors.
     */
    public HostGraph toHost() throws FormatException {
        return toResource();
    }

    @Override
    public HostModelMap getMap() {
        synchronise();
        if (hasErrors()) {
            throw new IllegalStateException();
        }
        return this.hostModelMap;
    }

    @Override
    public @NonNull TypeGraph getTypeGraph() {
        return this.typeGraph;
    }

    private final TypeGraph typeGraph;

    @Override
    public TypeModelMap getTypeMap() {
        synchronise();
        return this.typeMap;
    }

    /** Returns the set of labels used in this graph. */
    @Override
    public Set<TypeLabel> getLabels() {
        return this.labelSet.get();
    }

    /** Set of labels occurring in this graph. */
    private final Factory<Set<TypeLabel>> labelSet = Factory.lazy(this::createLabels);

    private Set<TypeLabel> createLabels() {
        Set<TypeLabel> result = new HashSet<>();
        for (AspectEdge edge : getNormalSource().edgeSet()) {
            TypeLabel label = edge.getTypeLabel();
            if (label != null) {
                result.add(label);
            }
        }
        return result;
    }

    /**
     * The algebra is the term algebra at this point.
     */
    private AlgebraFamily getFamily() {
        // if there is a grammar involved, the real algebra family
        // will be set only later
        return getGrammar() == null
            ? AlgebraFamily.DEFAULT
            : AlgebraFamily.TERM;
    }

    private AspectGraph getNormalSource() {
        return this.normalSource.get();
    }

    /** The normalised source model. */
    private final Factory<AspectGraph> normalSource = Factory.lazy(() -> getSource().normalise());

    @Override
    void notifyWillRebuild() {
        super.notifyWillRebuild();
        this.labelSet.reset();
        this.typeMap = null;
    }

    @Override
    HostGraph compute() throws FormatException {
        this.algebraFamily = getFamily();
        getSource().getErrors().throwException();
        var modelPlusMap = computeModel();
        var result = modelPlusMap.one();
        var hostModelMap = modelPlusMap.two();
        // create the type map
        var typeMap = new TypeModelMap(result.getTypeGraph().getFactory());
        for (var ne : hostModelMap.nodeMap().entrySet()) {
            typeMap.putNode(ne.getKey(), ne.getValue().getType());
        }
        for (var ee : hostModelMap.edgeMap().entrySet()) {
            typeMap.putEdge(ee.getKey(), ee.getValue().getType());
        }
        this.typeMap = typeMap;
        this.hostModelMap = hostModelMap;
        result.getErrors().throwException();
        return result;
    }

    /**
     * Computes a host graph from the source aspect graph, together with a mapping
     * from the aspect graph's node to the (fresh) graph nodes.
     */
    private Pair<DefaultHostGraph,HostModelMap> computeModel() {
        // actually, we start with the normalised source graph
        AspectGraph normalSource = getNormalSource();
        if (debug) {
            GraphPreviewDialog.showGraph(normalSource);
        }
        FormatErrorSet errors = new FormatErrorSet(normalSource.getErrors());
        // start with an untyped host graph; this will be typed later on
        DefaultHostGraph result = new DefaultHostGraph(normalSource.getName());
        // we need to record the normal-source-to-host element map for layout transfer
        HostModelMap elementMap = new HostModelMap(result.getFactory());
        // copy the nodes from model to resource
        // first the non-value nodes because their numbers are fixed
        for (AspectNode modelNode : normalSource.nodeSet()) {
            if (!modelNode.has(Category.SORT)) {
                processModelNode(result, elementMap, modelNode);
            }
        }
        // then the value nodes because their numbers are generated
        for (AspectNode modelNode : normalSource.nodeSet()) {
            if (modelNode.has(Category.SORT)) {
                processModelNode(result, elementMap, modelNode);
            }
        }
        // copy the edges from model to resource
        for (AspectEdge modelEdge : normalSource.edgeSet()) {
            processModelEdge(result, elementMap, modelEdge);
        }
        // remove isolated value nodes from the result graph
        for (HostNode modelNode : elementMap.nodeMap().values()) {
            if (modelNode instanceof ValueNode && result.edgeSet(modelNode).isEmpty()) {
                // the node is an isolated value node; remove it
                result.removeNode(modelNode);
            }
        }
        var grammar = getGrammar();
        if (grammar != null) {
            try {
                // test against the type graph
                TypeGraph type = grammar.getTypeGraph();
                var typing = type.analyzeHost(result);
                result = typing.createImage(result.getName());
                HostModelMap newElementMap = new HostModelMap(result.getFactory());
                for (var nodeEntry : elementMap.nodeMap().entrySet()) {
                    HostNode typedNode = typing.getNode(nodeEntry.getValue());
                    if (typedNode != null) {
                        newElementMap.putNode(nodeEntry.getKey(), typedNode);
                    }
                }
                for (var edgeEntry : elementMap.edgeMap().entrySet()) {
                    var typedEdge = typing.getEdge(edgeEntry.getValue());
                    if (typedEdge != null) {
                        newElementMap.putEdge(edgeEntry.getKey(), typedEdge);
                    }
                }
                elementMap = newElementMap;
            } catch (FormatException e) {
                errors.addAll(e.getErrors());
            }
            if (grammar.getProperties().getTypePolicy() != CheckPolicy.OFF) {
                errors.addAll(result.checkTypeConstraints());
            }
        }
        // transfer graph info such as layout from model to resource
        GraphInfo.transferProperties(normalSource, result, elementMap);
        // for the result value we need a map from the source to the host graph,
        // whereas elementMap goes from the normalised source
        if (normalSource instanceof NormalAspectGraph ng) {
            var sourceMap = new HostModelMap(result.getFactory());
            for (var ne : ng.sourceToNormalMap().nodeMap().entrySet()) {
                var hostNode = elementMap.getNode(ne.getValue());
                if (hostNode != null) {
                    sourceMap.putNode(ne.getKey(), hostNode);
                }
            }
            for (var ee : ng.sourceToNormalMap().edgeMap().entrySet()) {
                var hostEdge = elementMap.getEdge(ee.getValue());
                if (hostEdge != null) {
                    sourceMap.putEdge(ee.getKey(), hostEdge);
                }
            }
            elementMap = sourceMap;
        }
        result.setErrors(errors.wrap(elementMap));
        result.setFixed();
        return new Pair<>(result, elementMap);
    }

    /**
     * Processes the information in a model node by updating the model and
     * element map.
     */
    private void processModelNode(DefaultHostGraph result, HostModelMap elementMap,
                                  AspectNode modelNode) {
        // include the node in the model if it is not virtual
        if (!modelNode.has(Category.NESTING) && !modelNode.has(REMARK)) {
            HostNode nodeImage = null;
            Aspect sortAspect = modelNode.get(Category.SORT);
            if (sortAspect != null) {
                Algebra<?> nodeAlgebra
                    = this.algebraFamily.getAlgebra(sortAspect.getContentKind().getSort());
                Constant term = ((ConstContent) sortAspect.getContent()).get();
                nodeImage = result.getFactory().createNode(nodeAlgebra, nodeAlgebra.toValue(term));
                result.addNode(nodeImage);
            } else {
                nodeImage = result.addNode(modelNode.getNumber());
            }
            elementMap.putNode(modelNode, nodeImage);
        }
    }

    /**
     * Processes the information in a model edge by updating the resource, element
     * map and subtypes.
     */
    private void processModelEdge(HostGraph result, HostModelMap elementMap, AspectEdge modelEdge) {
        if (!modelEdge.has(Category.LABEL)) {
            return;
        }
        HostNode hostSource = elementMap.getNode(modelEdge.source());
        assert hostSource != null : String
            .format("Source of '%s' is not in element map %s", modelEdge.source(), elementMap);
        HostNode hostNode = elementMap.getNode(modelEdge.target());
        assert hostNode != null : String
            .format("Target of '%s' is not in element map %s", modelEdge.target(), elementMap);
        TypeLabel hostLabel = modelEdge.getTypeLabel();
        assert hostLabel != null && !hostLabel.isSort() : String
            .format("Inappropriate label %s", hostLabel);
        HostEdge hostEdge = result.addEdge(hostSource, hostLabel, hostNode);
        assert hostEdge != null;
        elementMap.putEdge(modelEdge, hostEdge);
    }

    /** Map from source model to resource nodes. */
    private HostModelMap hostModelMap;
    /** Map from source model to types. */
    private TypeModelMap typeMap;
    /** The attribute element factory for this model. */
    private AlgebraFamily algebraFamily;

    /** Sets the debug mode, causing the normalised graphs to be shown in a dialog. */
    public static void setDebug(boolean debug) {
        HostModel.debug = debug;
    }

    private static boolean debug;

    /** Mapping from aspect graph to type graph. */
    public static class HostModelMap extends ModelMap<@NonNull HostNode,@NonNull HostEdge> {
        /**
         * Creates a new, empty map.
         */
        public HostModelMap(HostFactory factory) {
            super(factory);
        }
    }
}
