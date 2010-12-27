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
 * $Id: AspectJModel.java,v 1.35 2008-03-13 14:40:32 rensink Exp $
 */
package groove.gui.jgraph;

import static groove.gui.jgraph.JAttr.RULE_EDGE_ATTR;
import static groove.gui.jgraph.JAttr.RULE_EDGE_EMPH_CHANGE;
import static groove.gui.jgraph.JAttr.RULE_NODE_ATTR;
import static groove.gui.jgraph.JAttr.RULE_NODE_EMPH_CHANGE;
import static groove.view.aspect.AspectKind.REMARK;
import groove.graph.DefaultGraph;
import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Label;
import groove.graph.Node;
import groove.gui.Options;
import groove.trans.RuleLabel;
import groove.util.Converter;
import groove.view.FormatError;
import groove.view.View;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectLabel;
import groove.view.aspect.AspectNode;
import groove.view.aspect.Aspect;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * Implements jgraph's GraphModel interface on top of a {@link View}. This is
 * used to visualise rules and attributed graphs.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectJModel extends GraphJModel<AspectNode,AspectEdge> {

    // --------------------- INSTANCE DEFINITIONS ------------------------

    /**
     * Creates a new aspect model instance on top of a given aspectual view.
     */
    AspectJModel(View<?> view, Options options) {
        super(view.getView(), options);
        this.view = view;
    }

    /** Constructor for a dummy model. */
    AspectJModel() {
        this.view = null;
    }

    /** Specialises the return type. */
    @Override
    public AspectGraph getGraph() {
        return (AspectGraph) super.getGraph();
    }

    /**
     * This implementation directly computes the plain graph from the underlying
     * aspect graph
     * @see #getGraph()
     */
    @Override
    public DefaultGraph toPlainGraph() {
        return getGraph().toPlainGraph();
    }

    @Override
    public boolean hasError(JCell cell) {
        return this.errorCells.contains(cell);
    }

    @Override
    public void reload() {
        super.reload();
        List<FormatError> graphErrors = this.view.getErrors();
        if (graphErrors != null) {
            for (FormatError error : graphErrors) {
                for (Element errorObject : error.getElements()) {
                    JCell errorCell = null;
                    if (errorObject instanceof AspectNode) {
                        errorCell = getJCellForNode((AspectNode) errorObject);
                    } else if (errorObject instanceof AspectEdge) {
                        errorCell = getJCell(errorObject);
                        if (errorCell instanceof GraphJEdge
                            && ((GraphJEdge<?,?>) errorCell).isDataEdgeSourceLabel()) {
                            errorCell =
                                ((GraphJEdge<?,?>) errorCell).getSourceVertex();
                        }
                    }
                    if (errorCell != null) {
                        this.errorCells.add(errorCell);
                    }
                }
            }
        }
    }

    /**
     * Indicates whether aspect prefixes should be shown for nodes and edges.
     */
    public final boolean isShowRemarks() {
        return getOptionValue(Options.SHOW_REMARKS_OPTION);
    }

    @Override
    protected AttributeMap createJVertexAttr(AspectNode node) {
        AttributeMap result;
        AspectKind kind = node.getKind();
        result = RULE_NODE_ATTR.get(kind).clone();
        if (result != null && kind.isData()) {
            result.applyMap(getJVertexDataAttr());
        }
        return result;
    }

    @Override
    protected void modifyJEdgeAttr(AttributeMap result, AspectEdge edge) {
        super.modifyJEdgeAttr(result, edge);
        result.applyMap(RULE_EDGE_ATTR.get(edge.getKind()));
        RuleLabel ruleModelLabel = edge.getRuleLabel();
        if (ruleModelLabel != null) {
            if (ruleModelLabel.isEmpty() || ruleModelLabel.isNeg()
                && ruleModelLabel.getNegOperand().isEmpty()) {
                // remove edge arrow
                GraphConstants.setLineEnd(result, GraphConstants.ARROW_NONE);
            } else if (!ruleModelLabel.isAtom()) {
                setFontAttr(result, Font.ITALIC);
            }
        }
    }

    /** Adds the correct line width emphasis. */
    @Override
    protected AttributeMap getJEdgeEmphAttr(JEdge jCell) {
        AspectEdge ruleEdge = ((AspectJEdge) jCell).getEdge();
        return RULE_EDGE_EMPH_CHANGE.get(ruleEdge.getKind());
    }

    /** Adds the correct border emphasis. */
    @Override
    protected AttributeMap getJVertexEmphAttr(JVertex jCell) {
        AspectNode ruleNode = ((AspectJVertex) jCell).getNode();
        return RULE_NODE_EMPH_CHANGE.get(ruleNode.getKind());
    }

    /**
     * Overwrites the method so as to return a rule vertex.
     * @require <tt>edge instanceof RuleGraph.RuleNode</tt>
     */
    @Override
    protected GraphJVertex<AspectNode,AspectEdge> createJVertex(AspectNode node) {
        return new AspectJVertex(this, node);
    }

    /**
     * Overwrites the method so as to return a rule edge.
     * @require <tt>edge instanceof RuleGraph.RuleEdge</tt>
     */
    @Override
    protected GraphJEdge<AspectNode,AspectEdge> createJEdge(AspectEdge edge) {
        return new AspectJEdge(edge);
    }

    /** Retrieves a node's image according to the view. */
    Node getModelNode(AspectNode node) {
        View.ViewToModelMap<?,?,?> viewMap = this.view.getMap();
        return viewMap == null ? null : viewMap.getNode(node);
    }

    /** Retrieves an edge's image according to the view. */
    Edge getModelEdge(AspectEdge edge) {
        View.ViewToModelMap<?,?,?> viewMap = this.view.getMap();
        return viewMap == null ? null : viewMap.getEdge(edge);
    }

    /**
     * The underlying view of this graph model.
     */
    private final View<?> view;
    /** Mapping from the elements of the model to those of the view. */
    /** Set of cells with a format error. */
    private final Set<JCell> errorCells = new HashSet<JCell>();

    /**
     * Creates a new aspect model instance on top of a given aspectual view.
     * Returns {@link #EMPTY_ASPECT_JMODEL} if the view is <code>null</code>.
     */
    static public AspectJModel newInstance(View<?> view, Options options) {
        if (view == null) {
            return EMPTY_ASPECT_JMODEL;
        } else {
            AspectJModel result = new AspectJModel(view, options);
            result.reload();
            return result;
        }
    }

    /** Empty instance of the {@link AspectJModel}. */
    static public final AspectJModel EMPTY_ASPECT_JMODEL = new AspectJModel();

    /** Role names (for the tool tips). */
    static final Map<AspectKind,String> ROLE_NAMES =
        new EnumMap<AspectKind,String>(AspectKind.class);
    /** Role descriptions (for the tool tips). */
    static final Map<AspectKind,String> ROLE_DESCRIPTIONS =
        new EnumMap<AspectKind,String>(AspectKind.class);

    static {
        ROLE_NAMES.put(AspectKind.EMBARGO, "Embargo");
        ROLE_NAMES.put(AspectKind.READER, "Reader");
        ROLE_NAMES.put(AspectKind.CREATOR, "Creator");
        ROLE_NAMES.put(AspectKind.ADDER, "Adder");
        ROLE_NAMES.put(AspectKind.ERASER, "Eraser");
        ROLE_NAMES.put(AspectKind.REMARK, "Remark");

        ROLE_DESCRIPTIONS.put(AspectKind.EMBARGO,
            "Must be absent from a graph for this rule to apply");
        ROLE_DESCRIPTIONS.put(AspectKind.READER,
            "Must be matched for this rule to apply");
        ROLE_DESCRIPTIONS.put(AspectKind.CREATOR,
            "Will be created by applying this rule");
        ROLE_DESCRIPTIONS.put(
            AspectKind.ADDER,
            "Must be absent from a graph for this rule to apply, and will be created when applying this rule");
        ROLE_DESCRIPTIONS.put(AspectKind.ERASER,
            "Will be deleted by applying this rule");
        ROLE_DESCRIPTIONS.put(AspectKind.REMARK,
            "Has no effect on the execution of the rule");
    }

    /**
     * Specialized j-vertex for rule graphs, with its own tool tip text.
     */
    private class AspectJVertex extends GraphJVertex<AspectNode,AspectEdge> {
        /** Creates a j-vertex on the basis of a given (aspectual) node. */
        public AspectJVertex(AspectJModel jModel, AspectNode node) {
            super(jModel, node);
            this.aspect = node.getKind();
        }

        /**
         * This implementation return's the model node of {@link #getNode()}.
         * @see #getModelNode(AspectNode)
         */
        @Override
        public Node getActualNode() {
            return getModelNode(getNode());
        }

        /**
         * This implementation prefixes the node description with an indication
         * of the role, if the model is a rule.
         */
        @Override
        StringBuilder getNodeDescription() {
            StringBuilder res = super.getNodeDescription();
            if (ROLE_NAMES.containsKey(this.aspect)) {
                Converter.toUppercase(res, false);
                res.insert(0, " ");
                res.insert(0, ROLE_NAMES.get(this.aspect));
                res.append("<br>" + ROLE_DESCRIPTIONS.get(this.aspect));
            }
            return res;
        }

        /**
         * Tests {@link #isAllowedNodeLabel(AspectEdge)} and calls the super
         * method if the test is successful.
         */
        @Override
        public boolean addSelfEdge(AspectEdge edge) {
            return isAllowedNodeLabel(edge) && super.addSelfEdge(edge);
        }

        /** Adds a quantifier, if the nesting aspect justifies this. */
        @Override
        public List<StringBuilder> getLines() {
            List<StringBuilder> result = super.getLines();
            if (isShowAspects()) {
                result.add(0, getRoleLine());
            }
            // adds a quantor if the node is a nesting node
            if (this.aspect.isQuantifier()) {
                result.add(0, getQuantifierLine(getNode().getAspect()));
            }
            // adds a parameter string if the node is a rule parameter
            if (getNode().hasParam()) {
                String parString = (String) getNode().getParam().getContent();
                if (parString != null && parString.length() > 0) {
                    result.add(new StringBuilder(parString));
                }
            }
            return result;
        }

        /**
         * Tests if a given edge can be used as node label. This is the case if
         * rule role and nesting label of the edge coincide with those of the
         * node.
         */
        boolean isAllowedNodeLabel(AspectEdge dataEdge) {
            boolean result =
                !dataEdge.isBinary() || dataEdge.getKind().isMeta();
            if (!result) {
                // test for equal rule roles
                result = dataEdge.equalsAspects(getNode());
            }
            return result;
        }

        /**
         * Returns a string with the aspect prefix for this node's role, of the
         * empty string if the node has no special role.
         */
        private StringBuilder getRoleLine() {
            boolean hasRoleValue =
                this.aspect.isRole() && this.aspect != AspectKind.READER;
            return new StringBuilder(hasRoleValue
                    ? getNode().getAspect().toString() : "");
        }

        /** Returns an HTML-formatted line describing a given quantifier value. */
        private StringBuilder getQuantifierLine(Aspect nesting) {
            StringBuilder result = new StringBuilder();
            AspectKind kind = nesting.getKind();
            if (kind == AspectKind.FORALL) {
                result.append(Converter.HTML_FORALL);
            } else if (kind == AspectKind.FORALL_POS) {
                result.append(Converter.HTML_FORALL);
                result.append(Converter.SUPER_TAG.on(Converter.HTML_GT + "0"));
            } else if (kind == AspectKind.EXISTS) {
                result.append(Converter.HTML_EXISTS);
            }
            String level = (String) nesting.getContent();
            if (level != null && level.length() != 0) {
                result.append(Converter.SUB_TAG.on(level));
            }
            return result;
        }

        /**
         * On demand prefixes the label with the edge's aspect values.
         */
        @Override
        public StringBuilder getLine(AspectEdge edge) {
            StringBuilder result = new StringBuilder();
            if (isShowAspects()) {
                for (Aspect aspect : edge.label().getAspects()) {
                    result.append(aspect);
                }
            }
            result.append(super.getLine(edge));
            // add nesting level, if any
            String levelName = edge.getLevelName();
            if (!isShowAspects() && levelName != null
                && levelName.length() != 0) {
                result.append(Converter.SUB_TAG.on(levelName));
            }
            return result;
        }

        /**
         * This implementation adds node and edge aspects.
         */
        @Override
        public Collection<String> getPlainLabels() {
            Collection<String> result = new ArrayList<String>();
            for (AspectLabel nodeLabel : getNode().getNodeLabels()) {
                result.add(nodeLabel.text());
            }
            // we do not do a super call, for that adds the value of the actual
            // node which we have here anyway
            for (AspectEdge edge : getSelfEdges()) {
                result.add(edge.label().text());
            }
            // result.addAll(super.getPlainLabels());
            return result;
        }

        /**
         * This implementation adds an edge aspect prefix.
         */
        @Override
        public String getPlainLabel(AspectEdge edge) {
            return edge.label().text();
        }

        @Override
        public Label getLabel(AspectEdge edge) {
            return edge.getDisplayLabel();
        }

        /**
         * This implementation makes remark edges invisible as demanded by the
         * {@link Options#SHOW_REMARKS_OPTION}.
         */
        @Override
        public boolean isVisible() {
            if (this.aspect == REMARK) {
                return isShowRemarks();
            } else if (getNode().hasParam()) {
                return true;
            } else {
                return super.isVisible();
            }
        }

        /**
         * This implementation returns <code>true</code> only if
         * {@link AspectKind#isMeta()} holds for the underlying
         * node.
         */
        @Override
        public boolean isListable() {
            return super.isListable() && !this.aspect.isMeta();
        }

        /** The role of the underlying rule node. */
        private final AspectKind aspect;
    }

    /**
     * Specialized j-edge for rule graphs, with its own tool tip text.
     */
    private class AspectJEdge extends GraphJEdge<AspectNode,AspectEdge> {
        /** Creates a j-edge on the basis of a given (aspectual) edge. */
        public AspectJEdge(AspectEdge edge) {
            super(AspectJModel.this, edge);
            this.aspect = edge.getKind();
        }

        @Override
        StringBuilder getEdgeDescription() {
            StringBuilder result = super.getEdgeDescription();
            if (ROLE_DESCRIPTIONS.containsKey(this.aspect)) {
                result.append("<br>" + ROLE_DESCRIPTIONS.get(this.aspect));
            }
            return result;
        }

        @Override
        StringBuilder getEdgeKindDescription() {
            StringBuilder result = super.getEdgeKindDescription();
            if (ROLE_NAMES.containsKey(this.aspect)) {
                Converter.toUppercase(result, false);
                result.insert(0, " ");
                result.insert(0, ROLE_NAMES.get(this.aspect));
            }
            return result;
        }

        @Override
        Edge getActualEdge() {
            return getModelEdge(getEdge());
        }

        /** This implementation returns the (unparsed) label of the model edge. */
        @Override
        public Label getLabel(AspectEdge edge) {
            return edge.getDisplayLabel();
        }

        /**
         * Returns <tt>true</tt> only if the aspect values of the edge to be
         * added equal those of this j-edge, and the superclass is also willing.
         */
        @Override
        public boolean addEdge(AspectEdge edge) {
            if (edge.equalsAspects(getEdge())) {
                return super.addEdge(edge);
            } else {
                return false;
            }
        }

        /**
         * On demand prefixes the label with the edge's aspect values.
         */
        @Override
        public StringBuilder getLine(AspectEdge edge) {
            StringBuilder result = new StringBuilder();
            if (isShowAspects()) {
                for (Aspect aspect : edge.label().getAspects()) {
                    result.append(aspect);
                }
            } else if (this.aspect.isRole()) {
                // add nesting level, if any
                String levelName = edge.getLevelName();
                if (levelName != null && levelName.length() != 0) {
                    result.append(levelName + LEVEL_NAME_SEPARATOR);
                }
            }
            result.append(super.getLine(edge));
            return result;
        }

        /**
         * This implementation adds node and edge aspects.
         */
        @Override
        public String getPlainLabel(AspectEdge edge) {
            return edge.toString();
        }

        /**
         * This implementation makes remark edges invisible as demanded by the
         * {@link Options#SHOW_REMARKS_OPTION}.
         */
        @Override
        public boolean isVisible() {
            return super.isVisible()
                && (isShowRemarks() || this.aspect != REMARK);
        }

        /**
         * This implementation returns <code>true</code> only if
         * {@link AspectKind#isMeta()} holds for the underlying
         * edge.
         */
        @Override
        public boolean isListable() {
            return super.isListable() && !this.aspect.isMeta();
        }

        /**
         * Only returns <code>true</code> if this edge has the same aspect
         * values as the source node. This is to prevent ambiguities.
         */
        @Override
        public boolean isDataEdgeSourceLabel() {
            return super.isDataEdgeSourceLabel()
                && getEdge().equalsAspects(getSourceNode())
                && getSourceNode().getKind() != REMARK;
        }

        private final AspectKind aspect;

        /** Separator between level name and edge label. */
        private static final char LEVEL_NAME_SEPARATOR = ':';
    }
}