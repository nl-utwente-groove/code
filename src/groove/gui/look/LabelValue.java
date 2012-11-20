/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.gui.look;

import static groove.io.HTMLConverter.ITALIC_TAG;
import static groove.io.HTMLConverter.STRONG_TAG;
import static groove.io.HTMLConverter.toHtml;
import static groove.view.aspect.AspectKind.REMARK;
import groove.control.CtrlState;
import groove.control.CtrlVar;
import groove.graph.Edge;
import groove.graph.EdgeRole;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.LabelPattern;
import groove.graph.TypeLabel;
import groove.graph.algebra.VariableNode;
import groove.gui.Options;
import groove.gui.jgraph.AspectJEdge;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.AspectJVertex;
import groove.gui.jgraph.CtrlJVertex;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJEdge;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.GraphJVertex;
import groove.gui.jgraph.LTSJEdge;
import groove.gui.jgraph.LTSJVertex;
import groove.gui.tree.LabelTree;
import groove.io.HTMLConverter;
import groove.io.HTMLConverter.HTMLTag;
import groove.io.Util;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.util.Colors;
import groove.view.FormatException;
import groove.view.GraphBasedModel;
import groove.view.GraphBasedModel.TypeModelMap;
import groove.view.aspect.Aspect;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Visual value refresher for the {@link VisualKey#LABEL}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LabelValue implements VisualValue<List<String>> {
    /** Constructs a value strategy for a given JGraph. */
    public LabelValue(GraphJGraph jGraph) {
        this.jGraph = jGraph;
        this.options = jGraph.getOptions();
        this.labelTree = jGraph.getLabelTree();
        this.role = jGraph.getGraphRole();
    }

    /** Sets a label-filtering tree to be taken into account for computing the visibility. */
    public void setLabelTree(LabelTree labelTree) {
        this.labelTree = labelTree;
    }

    @Override
    public List<String> get(GraphJCell cell) {
        List<String> result = null;
        if (cell instanceof GraphJVertex) {
            result = getLines((GraphJVertex) cell);
        } else if (cell instanceof AspectJEdge) {
            result = getAspectEdgeValue((AspectJEdge) cell);
        } else if (cell instanceof GraphJEdge) {
            result = getLines((GraphJEdge) cell);
        }
        return result;
    }

    /** Returns a list of lines together making up the label text of a vertex. */
    protected List<String> getLines(GraphJVertex jVertex) {
        List<String> result;
        switch (this.role) {
        case HOST:
            result = getHostLines((AspectJVertex) jVertex);
            break;
        case RULE:
            result = getRuleLines((AspectJVertex) jVertex);
            break;
        case TYPE:
            result = getTypeLines((AspectJVertex) jVertex);
            break;
        case LTS:
            result = getLTSLines((LTSJVertex) jVertex);
            break;
        case CTRL:
            result = getCtrlLines((CtrlJVertex) jVertex);
            break;
        default:
            result = getBasicLines(jVertex);
        }
        return result;
    }

    private List<String> getAspectEdgeValue(AspectJEdge jEdge) {
        List<String> result = null;
        // if both source and target nodes are nodified, 
        // test for source node first
        if (jEdge.isNodeEdgeOut()) {
            result = Collections.emptyList();
        } else if (jEdge.isNodeEdgeIn()) {
            result = new ArrayList<String>();
            LabelPattern pattern =
                ((AspectJVertex) jEdge.getTargetVertex()).getEdgeLabelPattern();
            @SuppressWarnings({"unchecked", "rawtypes"})
            GraphBasedModel<HostGraph> resourceModel =
                (GraphBasedModel) ((AspectJGraph) getJGraph()).getModel().getResourceModel();
            try {
                result.add(pattern.getLabel(
                    resourceModel.toResource(),
                    (HostNode) resourceModel.getMap().getNode(
                        jEdge.getTargetNode())));
            } catch (FormatException e) {
                // assert false;
            }
        } else {
            result = getLines(jEdge);
        }
        return result;
    }

    /** Returns a list of lines together making up the label text of a vertex. */
    protected List<String> getLines(GraphJEdge jEdge) {
        List<String> result;
        switch (this.role) {
        case HOST:
        case RULE:
        case TYPE:
            result = getAspectLines((AspectJEdge) jEdge);
            break;
        case LTS:
            result = getLTSLines((LTSJEdge) jEdge);
            break;
        default:
            result = getBasicLines(jEdge);
        }
        return result;
    }

    /** This implementation adds the data edges to the super result. */
    private List<String> getBasicLines(GraphJVertex jVertex) {
        List<String> result = new LinkedList<String>();
        // show the node identity if required
        if (getOptionValue(Options.SHOW_NODE_IDS_OPTION)) {
            result.add(ITALIC_TAG.on(jVertex.getNode().toString()));
        }
        // only add edges that have an unfiltered label
        for (Edge edge : jVertex.getEdges()) {
            if (!isFiltered(edge)) {
                StringBuilder line = new StringBuilder(edge.label().text());
                HTMLConverter.toHtml(line);
                result.add(line.toString());
            }
        }
        return result;
    }

    /** Recomputes the set of node lines for this aspect node. */
    private List<String> getHostLines(AspectJVertex jVertex) {
        AspectNode node = jVertex.getNode();
        node.testFixed(true);
        List<String> result = new ArrayList<String>();
        // show the node identity
        if (!node.hasId()) {
            result.addAll(getNodeIdLine(node));
        }
        // the following used to include hasError() as a disjunct
        if (getOptionValue(Options.SHOW_ASPECTS_OPTION)) {
            result.addAll(jVertex.getUserObject().toLines());
        } else {
            // show data constants and variables correctly
            result.addAll(getDataLines(node));
            // show the visible self-edges
            String id =
                node.hasId() ? ITALIC_TAG.on(node.getId().getContent()) : null;
            for (AspectEdge edge : jVertex.getEdges()) {
                if (!isFiltered(edge)) {
                    StringBuilder line = getHostLine(edge);
                    if (id != null && edge.getDisplayLabel().isNodeType()) {
                        line.insert(0, " : ");
                        line.insert(0, id);
                        id = null;
                    }
                    result.add(line.toString());
                }
            }
            if (id != null) {
                // we're not going to have any node types:
                // add the node id on a separate line
                result.add(id);
            }
        }
        for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
            if (!isFiltered(edge)) {
                result.add(getHostLine(edge).toString());
            }
        }
        return result;
    }

    /**
     * On demand prefixes the label with the edge's aspect values.
     */
    private StringBuilder getHostLine(AspectEdge edge) {
        edge.testFixed(true);
        StringBuilder result = new StringBuilder();
        result.append(getLabel(edge));
        if (edge.target() != edge.source()) {
            // this is an attribute edge displayed as a node label
            String suffix =
                ASSIGN_TEXT + edge.target().getAttrAspect().getContentString();
            result.append(HTMLConverter.toHtml(suffix));
        } else {
            addRoleIndicator(result, edge);
        }
        return result;
    }

    /** Recomputes the set of node lines for this aspect node. */
    private List<String> getTypeLines(AspectJVertex jVertex) {
        AspectNode node = jVertex.getNode();
        node.testFixed(true);
        List<String> result = new ArrayList<String>();
        if (getOptionValue(Options.SHOW_ASPECTS_OPTION)) {
            result.addAll(jVertex.getUserObject().toLines());
            for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
                if (!isFiltered(edge)) {
                    result.add(getTypeLine(edge));
                }
            }
        } else {
            if (node.hasImport()) {
                result.add(ITALIC_TAG.on(IMPORT_TEXT));
            }
            // show data constants and variables correctly
            result.addAll(getDataLines(node));
            // show the visible self-edges
            for (AspectEdge edge : jVertex.getEdges()) {
                if (!isFiltered(edge)) {
                    result.add(getTypeLine(edge));
                }
            }
            for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
                if (!isFiltered(edge)) {
                    result.add(getTypeLine(edge));
                }
            }
            if (node.isEdge()) {
                StringBuilder line = new StringBuilder();
                LabelPattern pattern = node.getEdgePattern();
                line.append(">> ");
                line.append(pattern.getLabel(pattern.getArgNames().toArray()));
                result.add(line.toString());
            }
        }
        return result;
    }

    /**
     * On demand prefixes the label with the edge's aspect values.
     */
    private String getTypeLine(AspectEdge edge) {
        edge.testFixed(true);
        StringBuilder result = new StringBuilder();
        result.append(getLabel(edge));
        if (edge.getKind() == AspectKind.ABSTRACT && edge.label().isNodeType()) {
            result = ITALIC_TAG.on(result);
        }
        if (edge.target() != edge.source()) {
            // this is an attribute edge displayed as a node label
            String suffix = TYPE_TEXT + edge.target().getAttrKind().getName();
            result.append(HTMLConverter.toHtml(suffix));
        } else if (edge.getAttrKind().hasSignature()) {
            // this is a field declaration
            result.append(TYPE_TEXT);
            result.append(STRONG_TAG.on(edge.getAttrKind().getName()));
        }
        addRoleIndicator(result, edge);
        return result.toString();
    }

    /** Recomputes the set of node lines for this aspect node. */
    private List<String> getRuleLines(AspectJVertex jVertex) {
        AspectNode node = jVertex.getNode();
        node.testFixed(true);
        List<String> result = new ArrayList<String>();
        // show the node identity
        if (!node.hasId()) {
            result.addAll(getNodeIdLine(node));
        }
        // the following used to include hasError() as a disjunct
        if (getOptionValue(Options.SHOW_ASPECTS_OPTION)) {
            result.addAll(jVertex.getUserObject().toLines());
            for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
                if (!isFiltered(edge)) {
                    result.add(getRuleLine(edge).toString());
                }
            }
            if (node.hasColor()) {
                result.add(node.getColor().toString());
            }
        } else {
            // show the quantifier aspect correctly
            if (node.getKind().isQuantifier()) {
                result.addAll(getQuantifierLines(node));
            }
            // show data constants and variables correctly
            result.addAll(getDataLines(node));
            // show the visible self-edges
            String id =
                node.hasId() ? ITALIC_TAG.on(node.getId().getContent()) : null;
            boolean unshownId =
                id != null && !jVertex.getAspect().isQuantifier();
            for (AspectEdge edge : jVertex.getEdges()) {
                if (!isFiltered(edge)) {
                    StringBuilder line = getRuleLine(edge);
                    if (unshownId && edge.getDisplayLabel().isNodeType()) {
                        line.insert(0, " : ");
                        line.insert(0, id);
                        unshownId = false;
                    }
                    result.add(line.toString());
                }
            }
            if (unshownId) {
                // we're not going to have any node types:
                // add the node id on a separate line
                result.add(id);
            }
            for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
                if (!isFiltered(edge)) {
                    result.add(getRuleLine(edge).toString());
                }
            }
            if (node.hasColor()) {
                StringBuilder line = new StringBuilder("& ");
                line.append(AspectKind.COLOR.getName());
                HTMLTag colorTag =
                    HTMLConverter.createColorTag(Colors.findColor(node.getColor().getContentString()));
                result.add(colorTag.on(line).toString());
            }
        }
        return result;
    }

    /**
     * On demand prefixes the label with the edge's aspect values.
     */
    private StringBuilder getRuleLine(AspectEdge edge) {
        edge.testFixed(true);
        StringBuilder result = new StringBuilder();
        result.append(getLabel(edge));
        addLevelName(result, edge);
        if (edge.target() != edge.source()) {
            // this is an attribute edge displayed as a node label
            String suffix =
                ASSIGN_TEXT + edge.target().getAttrAspect().getContentString();
            result.append(HTMLConverter.toHtml(suffix));
        }
        addRoleIndicator(result, edge);
        return result;
    }

    /** This implementation adds the data edges to the super result. */
    private List<String> getLTSLines(LTSJVertex jVertex) {
        List<String> result = new LinkedList<String>();
        // show the node identity if required
        if (getOptionValue(Options.SHOW_STATE_IDS_OPTION)) {
            GraphState state = jVertex.getNode();
            StringBuilder id = new StringBuilder(state.toString());
            CtrlState ctrlState = state.getCtrlState();
            if (!ctrlState.getAut().isDefault() || !ctrlState.isStart()) {
                id.append("|" + ctrlState.toString());
            }
            result.add(ITALIC_TAG.on(id).toString());
        }
        // only add edges that have an unfiltered label
        boolean isShowAnchors = getOptionValue(Options.SHOW_ANCHORS_OPTION);
        for (Edge edge : jVertex.getEdges()) {
            if (!isFiltered(edge)) {
                String text = ((GraphTransition) edge).text(isShowAnchors);
                StringBuilder line = new StringBuilder(text);
                HTMLConverter.toHtml(line);
                result.add(line.toString());
            }
        }
        return result;
    }

    /**
     * Appends the bound variables to the lines, if this list is not empty
     */
    private List<String> getCtrlLines(CtrlJVertex jVertex) {
        List<String> result = getBasicLines(jVertex);
        CtrlState state = jVertex.getNode();
        List<CtrlVar> boundVars = state.getBoundVars();
        if (boundVars.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(boundVars.toString());
            result.add(sb.toString());
        }
        if (jVertex.isTransient()) {
            StringBuilder action = new StringBuilder();
            action.append(HTMLConverter.toHtml('<'));
            action.append(state.getRecipe());
            action.append(HTMLConverter.toHtml('>'));
            result.add(action.toString());
        }
        return result;
    }

    private List<String> getBasicLines(GraphJEdge jEdge) {
        List<String> result = new ArrayList<String>();
        for (Edge edge : jEdge.getEdges()) {
            // only add edges that have an unfiltered label
            if (!isFiltered(edge)) {
                result.add(edge.label().text());
            }
        }
        return result;
    }

    /** Recomputes the set of node lines for this aspect node. */
    private List<String> getAspectLines(AspectJEdge jEdge) {
        List<String> result;
        if (jEdge.isSourceLabel()) {
            result = Collections.emptyList();
        } else if (getOptionValue(Options.SHOW_ASPECTS_OPTION)) {
            // used to include hasError() as a disjunct
            result = jEdge.getUserObject().toLines();
        } else {
            result = new ArrayList<String>();
            for (AspectEdge edge : jEdge.getEdges()) {
                // only add edges that have an unfiltered label
                if (!isFiltered(edge)) {
                    StringBuilder line =
                        new StringBuilder(edge.getDisplayLabel().text());
                    addLevelName(line, edge);
                    if (line.length() > 0) {
                        result.add(line.toString());
                    }
                }
            }
        }
        return result;
    }

    private List<String> getLTSLines(LTSJEdge jEdge) {
        List<String> result = new ArrayList<String>();
        boolean isShowAnchors = getOptionValue(Options.SHOW_ANCHORS_OPTION);
        for (Edge edge : jEdge.getEdges()) {
            // only add edges that have an unfiltered label
            if (!isFiltered(edge)) {
                GraphTransition trans = (GraphTransition) edge;
                result.add(trans.text(isShowAnchors));
            }
        }
        return result;
    }

    /** Returns a HTML-formatted string representing the label of a given edge. */
    private String getLabel(AspectEdge edge) {
        Label result =
            getOptionValue(Options.SHOW_ASPECTS_OPTION) ? edge.label()
                    : edge.getDisplayLabel();
        return TypeLabel.toHtmlString(result);
    }

    /**
     * Appends a level name to a given text,
     * depending on an edge role.
     */
    private void addLevelName(StringBuilder text, AspectEdge edge) {
        if (edge.getKind().isRole()) {
            String levelName = edge.getLevelName();
            if (levelName != null && levelName.length() != 0) {
                text.append(LEVEL_NAME_SEPARATOR + levelName);
            }
        }
    }

    /**
     * Adds a textual prefix and a HTML colour to a given node line,
     * depending on an edge role.
     */
    private void addRoleIndicator(StringBuilder text, AspectEdge edge) {
        Aspect aspect = edge.getAspect();
        if (!aspect.equals(edge.source().getAspect())) {
            AspectKind kind = aspect.getKind();
            text.insert(0, kind.getDisplayPrefix());
            switch (kind) {
            case ERASER:
                HTMLConverter.ERASER_TAG.on(text);
                break;
            case ADDER:
                HTMLConverter.CREATOR_TAG.on(text);
                break;
            case LET:
                if (edge.getGraphRole() == GraphRole.RULE) {
                    HTMLConverter.CREATOR_TAG.on(text);
                }
                break;
            case CREATOR:
                HTMLConverter.CREATOR_TAG.on(text);
                break;
            case EMBARGO:
                HTMLConverter.EMBARGO_TAG.on(text);
                break;
            case REMARK:
                // replace all newlines by // as well
                String NEWLINE = HTMLConverter.HTML_LINEBREAK;
                int lineStart = text.indexOf(NEWLINE);
                while (lineStart >= 0) {
                    text.insert(lineStart + NEWLINE.length(),
                        REMARK.getDisplayPrefix());
                    lineStart =
                        text.indexOf(NEWLINE, lineStart + NEWLINE.length());
                }
                HTMLConverter.REMARK_TAG.on(text);
                break;
            }
        }
    }

    /** 
     * Returns the (possibly empty) list of lines 
     * describing the node identity, if this is to be shown
     * according to the current setting.
     * @see GraphJGraph#isShowNodeIdentities()
     */
    private List<String> getNodeIdLine(AspectNode node) {
        List<String> result = new ArrayList<String>();
        if (getOptionValue(Options.SHOW_NODE_IDS_OPTION)) {
            String id;
            if (node.getKind().isMeta()) {
                id = null;
            } else if (node.hasAttrAspect()) {
                AspectKind attrKind = node.getAttrKind();
                if (attrKind.hasSignature()) {
                    Object content = node.getAttrAspect().getContent();
                    if (content == null) {
                        id = VariableNode.TO_STRING_PREFIX + node.getNumber();
                    } else {
                        id = content.toString();
                    }
                } else {
                    assert attrKind == AspectKind.PRODUCT;
                    id = "p" + node.getNumber();
                }
            } else {
                id = node.toString();
            }
            if (id != null) {
                result.add(ITALIC_TAG.on(new StringBuilder(id)).toString());
            }
        }
        return result;
    }

    /**
     * Returns the lines describing this node's main aspect.
     * Currently this just concerns a possible quantifier.
     */
    private List<String> getQuantifierLines(AspectNode node) {
        List<String> result = new ArrayList<String>();
        StringBuilder line = new StringBuilder();
        Aspect id = node.getId();
        if (id != null) {
            line.append(HTMLConverter.ITALIC_TAG.on(id.getContent()));
            line.append(" : ");
        }
        switch (node.getKind()) {
        case FORALL:
            line.append(HTML_FORALL);
            break;
        case FORALL_POS:
            line.append(HTML_FORALL);
            line.append(HTMLConverter.SUPER_TAG.on(HTML_GT + "0"));
            break;
        case EXISTS:
            line.append(HTML_EXISTS);
            break;
        case EXISTS_OPT:
            line.append(HTML_EXISTS);
            line.append(HTMLConverter.SUPER_TAG.on("?"));
        }
        if (line.length() > 0) {
            result.add(line.toString());
        }
        return result;
    }

    /** Returns lines describing any data content of the JVertex. */
    private List<String> getDataLines(AspectNode node) {
        List<String> result = new ArrayList<String>();
        Aspect attrAspect = node.getAttrAspect();
        if (attrAspect.getKind().hasSignature()) {
            String dataLine = null;
            if (!attrAspect.hasContent()) {
                dataLine =
                    TypeLabel.toHtmlString(TypeLabel.createLabel(
                        EdgeRole.NODE_TYPE, attrAspect.getKind().getName()));
            } else if (!getOptionValue(Options.SHOW_NODE_IDS_OPTION)) {
                // show constants only if they are not already shown as node identities
                dataLine = attrAspect.getContentString();
            }
            if (dataLine != null) {
                result.add(dataLine);
            }
        }
        return result;
    }

    /** 
     * Tests if a given edge is currently being filtered.
     */
    private boolean isFiltered(Edge key) {
        TypeModelMap typeMap = getTypeMap();
        if (typeMap != null) {
            key = typeMap.getEdge((AspectEdge) key);
        }
        return key != null && this.labelTree != null
            && this.labelTree.isFiltered(key);
    }

    /** Retrieves the type map from the underlying JGraph, if there is any. */
    private TypeModelMap getTypeMap() {
        TypeModelMap result = null;
        if (this.jGraph instanceof AspectJGraph) {
            AspectJModel jModel = ((AspectJGraph) this.jGraph).getModel();
            if (jModel != null) {
                result = jModel.getResourceModel().getTypeMap();
            }
        }
        return result;
    }

    /** Returns the JGraph on which this label refresher is based. */
    public GraphJGraph getJGraph() {
        return this.jGraph;
    }

    /**
     * Retrieves the value for a given option from the options object, or
     * <code>null</code> if the options are not set (i.e., <code>null</code>).
     * @param option the name of the option
     */
    public boolean getOptionValue(String option) {
        return this.options.getItem(option).isEnabled()
            && this.options.isSelected(option);
    }

    private final GraphJGraph jGraph;
    private final GraphRole role;
    private final Options options;
    private LabelTree labelTree;

    static private final String IMPORT_TEXT = String.format("%simport%s",
        HTMLConverter.toHtml(Util.FRENCH_QUOTES_OPEN),
        HTMLConverter.toHtml(Util.FRENCH_QUOTES_CLOSED));
    static private final String HTML_EXISTS = toHtml(Util.EXISTS);
    static private final String HTML_FORALL = toHtml(Util.FORALL);
    static private final String HTML_GT = toHtml('>');
    static private final String ASSIGN_TEXT = " = ";
    static private final String TYPE_TEXT = ": ";
    /** Separator between level name and edge label. */
    static final char LEVEL_NAME_SEPARATOR = '@';
}
