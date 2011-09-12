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

import groove.algebra.Algebra;
import groove.algebra.AlgebraFamily;
import groove.algebra.Constant;
import groove.algebra.SignatureKind;
import groove.graph.Element;
import groove.graph.GraphInfo;
import groove.graph.TypeGraph;
import groove.graph.TypeLabel;
import groove.graph.algebra.ValueNode;
import groove.gui.dialog.GraphPreviewDialog;
import groove.trans.DefaultHostGraph;
import groove.trans.HostEdge;
import groove.trans.HostFactory;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.trans.SystemProperties;
import groove.util.Pair;
import groove.view.aspect.Aspect;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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

    /** Returns the set of labels used in this graph. */
    @Override
    public Set<TypeLabel> getLabels() {
        if (this.labelSet == null) {
            this.labelSet = new HashSet<TypeLabel>();
            for (AspectEdge edge : getNormalSource().edgeSet()) {
                TypeLabel label = edge.getTypeLabel();
                if (label != null) {
                    this.labelSet.add(label);
                }
            }
        }
        return this.labelSet;
    }

    @Override
    public boolean isEnabled() {
        return getGrammar() == null
            || getName().equals(getGrammar().getStartGraphName());
    }

    /** 
     * Extracts the algebra family from a (possibly {@code null}) properties
     * object.
     */
    private AlgebraFamily getFamily() {
        AlgebraFamily result;
        SystemProperties properties =
            getGrammar() == null ? null : getGrammar().getProperties();
        if (properties == null) {
            result = AlgebraFamily.getInstance();
        } else {
            result = AlgebraFamily.getInstance(properties.getAlgebraFamily());
        }
        return result;
    }

    private AspectGraph getNormalSource() {
        if (this.normalSource == null) {
            this.normalSource = getSource().normalise();
        }
        return this.normalSource;
    }

    @Override
    void notifyGrammarModified() {
        super.notifyGrammarModified();
        this.labelSet = null;
    }

    @Override
    HostGraph compute() throws FormatException {
        this.algebraFamily = getFamily();
        if (getSource().hasErrors()) {
            throw new FormatException(getSource().getErrors());
        }
        Pair<DefaultHostGraph,HostModelMap> modelPlusMap =
            computeModel(getSource());
        HostGraph result = modelPlusMap.one();
        if (GraphInfo.hasErrors(result)) {
            throw new FormatException(GraphInfo.getErrors(result));
        }
        this.hostModelMap = modelPlusMap.two();
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
        Set<FormatError> errors =
            new TreeSet<FormatError>(normalSource.getErrors());
        DefaultHostGraph result = createGraph(normalSource.getName());
        // we need to record the model-to-resource element map for layout transfer
        HostModelMap elementMap = new HostModelMap(result.getFactory());
        // copy the nodes from model to resource
        // first the non-value nodes because their numbers are fixed
        for (AspectNode modelNode : normalSource.nodeSet()) {
            if (!modelNode.getAttrKind().hasSignature()) {
                processModelNode(result, elementMap, modelNode);
            }
        }
        // then the value nodes because their numbers are generated
        for (AspectNode modelNode : normalSource.nodeSet()) {
            if (modelNode.getAttrKind().hasSignature()) {
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
            if (modelNode instanceof ValueNode
                && result.edgeSet(modelNode).isEmpty()) {
                // the node is an isolated value node; remove it
                result.removeNode(modelNode);
            }
        }
        // test against the type graph, if any
        TypeGraph type =
            getGrammar() == null ? null : getGrammar().getTypeGraph();
        if (type != null) {
            try {
                type.analyzeHost(result);
            } catch (FormatException e) {
                // compute inverse element map
                Map<Element,Element> inverseMap =
                    new HashMap<Element,Element>();
                for (Map.Entry<AspectNode,? extends HostNode> nodeEntry : elementMap.nodeMap().entrySet()) {
                    inverseMap.put(nodeEntry.getValue(), nodeEntry.getKey());
                }
                for (Map.Entry<AspectEdge,? extends HostEdge> edgeEntry : elementMap.edgeMap().entrySet()) {
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
        return new Pair<DefaultHostGraph,HostModelMap>(result, elementMap);
    }

    /**
     * Processes the information in a model node by updating the model and
     * element map.
     */
    private void processModelNode(DefaultHostGraph result,
            HostModelMap elementMap, AspectNode modelNode) {
        // include the node in the model if it is not virtual
        if (!modelNode.getKind().isMeta()) {
            HostNode nodeImage = null;
            AspectKind attrType = modelNode.getAttrKind();
            if (attrType.hasSignature()) {
                Algebra<?> nodeAlgebra =
                    this.algebraFamily.getAlgebra(SignatureKind.getKind(attrType.getName()));
                Aspect dataType = modelNode.getAttrAspect();
                String symbol = ((Constant) dataType.getContent()).getSymbol();
                nodeImage =
                    result.getFactory().createNodeFromString(nodeAlgebra,
                        symbol);
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
    private void processModelEdge(HostGraph result, HostModelMap elementMap,
            AspectEdge modelEdge) throws FormatException {
        if (modelEdge.getKind().isMeta()) {
            return;
        }
        HostNode hostSource = elementMap.getNode(modelEdge.source());
        assert hostSource != null : String.format(
            "Source of '%s' is not in element map %s", modelEdge.source(),
            elementMap);
        HostNode hostNode = elementMap.getNode(modelEdge.target());
        assert hostNode != null : String.format(
            "Target of '%s' is not in element map %s", modelEdge.target(),
            elementMap);
        TypeLabel hostLabel = modelEdge.getTypeLabel();
        assert hostLabel != null && !hostLabel.isDataType() : String.format(
            "Inappropriate label %s", hostLabel);
        HostEdge hostEdge = result.addEdge(hostSource, hostLabel, hostNode);
        elementMap.putEdge(modelEdge, hostEdge);
    }

    /**
     * Callback method to create the host graph.
     */
    private DefaultHostGraph createGraph(String name) {
        return new DefaultHostGraph(name);
    }

    /** Map from model to resource nodes. */
    private HostModelMap hostModelMap;
    /** The normalised source model. */
    private AspectGraph normalSource;
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

        @Override
        public HostModelMap newMap() {
            return new HostModelMap(getFactory());
        }

        @Override
        public HostFactory getFactory() {
            return (HostFactory) super.getFactory();
        }
    }
}
