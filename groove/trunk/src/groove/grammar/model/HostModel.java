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

import groove.algebra.Algebra;
import groove.algebra.AlgebraFamily;
import groove.algebra.Sort;
import groove.algebra.syntax.Expression;
import groove.grammar.CheckPolicy;
import groove.grammar.aspect.Aspect;
import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.AspectGraph.AspectGraphMorphism;
import groove.grammar.aspect.AspectKind;
import groove.grammar.aspect.AspectNode;
import groove.grammar.host.DefaultHostGraph;
import groove.grammar.host.HostEdge;
import groove.grammar.host.HostFactory;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostGraphMorphism;
import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeLabel;
import groove.graph.Element;
import groove.graph.GraphInfo;
import groove.gui.dialog.GraphPreviewDialog;
import groove.util.Pair;
import groove.util.parse.FormatError;
import groove.util.parse.FormatErrorSet;
import groove.util.parse.FormatException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Graph-based model of a host graph graph. Attribute values are represented
 * by {@link ValueNode}s.
 * @author Arend Rensink
 * @version $Revision $
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
    public TypeModelMap getTypeMap() {
        synchronise();
        return this.typeMap;
    }

    /** Returns the set of labels used in this graph. */
    @Override
    public Set<TypeLabel> getLabels() {
        if (this.labelSet == null) {
            this.labelSet = new HashSet<>();
            for (AspectEdge edge : getNormalSource().edgeSet()) {
                TypeLabel label = edge.getTypeLabel();
                if (label != null) {
                    this.labelSet.add(label);
                }
            }
        }
        return this.labelSet;
    }

    /**
     * The algebra is the term algebra at this point.
     */
    private AlgebraFamily getFamily() {
        // if there is a grammar involved, the real algebra family
        // will be set only later
        return getGrammar() == null ? AlgebraFamily.DEFAULT : AlgebraFamily.TERM;
    }

    private AspectGraph getNormalSource() {
        if (this.normalSource == null) {
            this.normalMap = new AspectGraphMorphism(getSource().getRole());
            this.normalSource = getSource().normalise(this.normalMap);
        }
        return this.normalSource;
    }

    @Override
    void notifyWillRebuild() {
        super.notifyWillRebuild();
        this.labelSet = null;
        this.typeMap = null;
    }

    @Override
    HostGraph compute() throws FormatException {
        this.algebraFamily = getFamily();
        GraphInfo.throwException(getSource());
        Pair<DefaultHostGraph,HostModelMap> modelPlusMap = computeModel(getSource());
        HostGraph result = modelPlusMap.one();
        GraphInfo.throwException(result);
        HostModelMap hostModelMap = modelPlusMap.two();
        TypeModelMap typeMap = new TypeModelMap(result.getTypeGraph().getFactory());
        for (Map.Entry<AspectNode,HostNode> nodeEntry : hostModelMap.nodeMap().entrySet()) {
            typeMap.putNode(nodeEntry.getKey(), nodeEntry.getValue().getType());
        }
        for (AspectEdge sourceEdge : getSource().edgeSet()) {
            AspectEdge normalEdge = this.normalMap.getEdge(sourceEdge);
            if (normalEdge == null) {
                normalEdge = sourceEdge;
            }
            HostEdge hostEdge = hostModelMap.getEdge(normalEdge);
            if (hostEdge != null) {
                typeMap.putEdge(sourceEdge, hostEdge.getType());
            }
        }
        this.typeMap = typeMap;
        this.hostModelMap = hostModelMap;
        return result;
    }

    /**
     * Computes a fresh model from a given aspect graph, together with a mapping
     * from the aspect graph's node to the (fresh) graph nodes.
     */
    private Pair<DefaultHostGraph,HostModelMap> computeModel(AspectGraph source) {
        AspectGraph normalSource = getNormalSource();
        if (debug) {
            GraphPreviewDialog.showGraph(normalSource);
        }
        FormatErrorSet errors = new FormatErrorSet(GraphInfo.getErrors(normalSource));
        DefaultHostGraph result = new DefaultHostGraph(normalSource.getName());
        // we need to record the model-to-resource element map for layout transfer
        HostModelMap elementMap = new HostModelMap(result.getFactory());
        // copy the nodes from model to resource
        // first the non-value nodes because their numbers are fixed
        for (AspectNode modelNode : normalSource.nodeSet()) {
            if (!modelNode.getAttrKind().hasSort()) {
                processModelNode(result, elementMap, modelNode);
            }
        }
        // then the value nodes because their numbers are generated
        for (AspectNode modelNode : normalSource.nodeSet()) {
            if (modelNode.getAttrKind().hasSort()) {
                processModelNode(result, elementMap, modelNode);
            }
        }
        // copy the edges from model to resource
        for (AspectEdge modelEdge : normalSource.edgeSet()) {
            try {
                processModelEdge(result, elementMap, modelEdge);
            } catch (FormatException exc) {
                errors.addAll(exc.getErrors());
            }
        }
        // remove isolated value nodes from the result graph
        for (HostNode modelNode : elementMap.nodeMap().values()) {
            if (modelNode instanceof ValueNode && result.edgeSet(modelNode).isEmpty()) {
                // the node is an isolated value node; remove it
                result.removeNode(modelNode);
            }
        }
        if (getGrammar() != null) {
            try {
                // test against the type graph, if any
                TypeGraph type = getGrammar().getTypeGraph();
                HostGraphMorphism typing = type.analyzeHost(result);
                result = typing.createImage(result.getName());
                HostModelMap newElementMap = new HostModelMap(result.getFactory());
                for (Map.Entry<AspectNode,HostNode> nodeEntry : elementMap.nodeMap().entrySet()) {
                    HostNode typedNode = typing.getNode(nodeEntry.getValue());
                    if (typedNode != null) {
                        newElementMap.putNode(nodeEntry.getKey(), typedNode);
                    }
                }
                // factor the edges through the normalisation mapping
                for (AspectEdge sourceEdge : getSource().edgeSet()) {
                    AspectEdge normalEdge = this.normalMap.getEdge(sourceEdge);
                    if (normalEdge == null) {
                        normalEdge = sourceEdge;
                    }
                    HostEdge hostEdge = elementMap.getEdge(normalEdge);
                    if (hostEdge != null) {
                        newElementMap.putEdge(normalEdge, typing.getEdge(hostEdge));
                    }
                }
                elementMap = newElementMap;
                if (getGrammar().getProperties().getTypePolicy() != CheckPolicy.OFF) {
                    result.checkTypeConstraints().throwException();
                }
            } catch (FormatException e) {
                // compute inverse element map
                Map<Element,Element> inverseMap = new HashMap<>();
                for (Map.Entry<AspectNode,HostNode> nodeEntry : elementMap.nodeMap().entrySet()) {
                    inverseMap.put(nodeEntry.getValue(), nodeEntry.getKey());
                }
                for (Map.Entry<AspectEdge,HostEdge> edgeEntry : elementMap.edgeMap().entrySet()) {
                    inverseMap.put(edgeEntry.getValue(), edgeEntry.getKey());
                }
                for (FormatError error : e.getErrors()) {
                    errors.add(error.transfer(inverseMap));
                }
            }
        }
        // transfer graph info such as layout from model to resource
        GraphInfo.transfer(normalSource, result, elementMap);
        GraphInfo.setErrors(result, errors);
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
        if (!modelNode.getKind().isMeta()) {
            HostNode nodeImage = null;
            AspectKind attrType = modelNode.getAttrKind();
            if (attrType.hasSort()) {
                Algebra<?> nodeAlgebra =
                    this.algebraFamily.getAlgebra(Sort.getKind(attrType.getName()));
                Aspect dataType = modelNode.getAttrAspect();
                Expression term = (Expression) dataType.getContent();
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
     * @throws FormatException if the presence of the edge signifies an error
     */
    private void processModelEdge(HostGraph result, HostModelMap elementMap, AspectEdge modelEdge)
        throws FormatException {
        if (modelEdge.getKind().isMeta()) {
            return;
        }
        HostNode hostSource = elementMap.getNode(modelEdge.source());
        assert hostSource != null : String.format("Source of '%s' is not in element map %s",
            modelEdge.source(), elementMap);
        HostNode hostNode = elementMap.getNode(modelEdge.target());
        assert hostNode != null : String.format("Target of '%s' is not in element map %s",
            modelEdge.target(), elementMap);
        TypeLabel hostLabel = modelEdge.getTypeLabel();
        assert hostLabel != null && !hostLabel.isDataType() : String.format(
            "Inappropriate label %s", hostLabel);
        HostEdge hostEdge = result.addEdge(hostSource, hostLabel, hostNode);
        elementMap.putEdge(modelEdge, hostEdge);
    }

    /** Map from source model to resource nodes. */
    private HostModelMap hostModelMap;
    /** Map from source model to types. */
    private TypeModelMap typeMap;
    /** The normalised source model. */
    private AspectGraph normalSource;
    /** Mapping from the source to the normal source. */
    private AspectGraphMorphism normalMap;
    /** Set of labels occurring in this graph. */
    private Set<TypeLabel> labelSet;
    /** The attribute element factory for this model. */
    private AlgebraFamily algebraFamily;

    /** Sets the debug mode, causing the normalised graphs to be shown in a dialog. */
    public static void setDebug(boolean debug) {
        HostModel.debug = debug;
    }

    private static boolean debug;

    /** Mapping from aspect graph to type graph. */
    public static class HostModelMap extends ModelMap<HostNode,HostEdge> {
        /**
         * Creates a new, empty map.
         */
        public HostModelMap(HostFactory factory) {
            super(factory);
        }
    }
}
