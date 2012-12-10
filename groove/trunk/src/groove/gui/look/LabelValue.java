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

import static groove.graph.EdgeRole.NODE_TYPE;
import groove.algebra.SignatureKind;
import groove.control.CtrlState;
import groove.control.CtrlVar;
import groove.graph.Edge;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.graph.LabelPattern;
import groove.graph.algebra.VariableNode;
import groove.gui.jgraph.AspectJEdge;
import groove.gui.jgraph.AspectJGraph;
import groove.gui.jgraph.AspectJVertex;
import groove.gui.jgraph.CtrlJVertex;
import groove.gui.jgraph.GraphJCell;
import groove.gui.jgraph.GraphJEdge;
import groove.gui.jgraph.GraphJGraph;
import groove.gui.jgraph.GraphJVertex;
import groove.gui.jgraph.LTSJEdge;
import groove.gui.jgraph.LTSJVertex;
import groove.gui.look.Line.ColorType;
import groove.gui.look.Line.Style;
import groove.gui.look.MultiLabel.Direct;
import groove.gui.tree.LabelTree;
import groove.io.Util;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.trans.HostGraph;
import groove.trans.HostNode;
import groove.util.Colors;
import groove.view.FormatException;
import groove.view.GraphBasedModel;
import groove.view.aspect.Aspect;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectKind;
import groove.view.aspect.AspectNode;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Visual value refresher for the {@link VisualKey#LABEL}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LabelValue implements VisualValue<MultiLabel> {
    /** Constructs a value strategy for a given JGraph. */
    public LabelValue(GraphJGraph jGraph) {
        this.jGraph = jGraph;
        this.role = jGraph.getGraphRole();
    }

    @Override
    public MultiLabel get(GraphJCell cell) {
        MultiLabel result = null;
        if (cell instanceof GraphJVertex) {
            result = getJVertexLabel((GraphJVertex) cell);
        } else if (cell instanceof GraphJEdge) {
            result = getJEdgeLabel((GraphJEdge) cell);
        }
        return result;
    }

    /** Returns a list of lines together making up the label text of a vertex. */
    protected MultiLabel getJVertexLabel(GraphJVertex jVertex) {
        MultiLabel result;
        switch (this.role) {
        case HOST:
            result = getHostNodeLabel((AspectJVertex) jVertex);
            break;
        case RULE:
            result = getRuleNodeLabel((AspectJVertex) jVertex);
            break;
        case TYPE:
            result = getTypeNodeLabel((AspectJVertex) jVertex);
            break;
        case LTS:
            result = getLTSJVertexLabel((LTSJVertex) jVertex);
            break;
        case CTRL:
            result = getCtrlJVertexLabel((CtrlJVertex) jVertex);
            break;
        default:
            result = getBasicVertexLabel(jVertex);
        }
        return result;
    }

    /** This implementation adds the data edges to the super result. */
    private MultiLabel getBasicVertexLabel(GraphJVertex jVertex) {
        MultiLabel result = new MultiLabel();
        // show the node identity if required
        if (jVertex.getJGraph().isShowNodeIdentities()) {
            result.add(
                Line.atom(jVertex.getNode().toString()).style(Style.ITALIC),
                Direct.NONE);
        }
        // only add edges that have an unfiltered label
        for (Edge edge : jVertex.getEdges()) {
            if (!isFiltered(jVertex, edge)) {
                result.add(Line.atom(edge.label().text()), Direct.NONE);
            }
        }
        return result;
    }

    /** Recomputes the set of node lines for this aspect node. */
    private MultiLabel getHostNodeLabel(AspectJVertex jVertex) {
        AspectNode node = jVertex.getNode();
        node.testFixed(true);
        MultiLabel result = new MultiLabel();
        if (!jVertex.getLooks().contains(Look.NODIFIED)) {
            // show the node identity
            if (jVertex.getJGraph().isShowNodeIdentities() && !node.hasId()) {
                result.add(getNodeIdLine(node));
            }
            // the following used to include hasError() as a disjunct
            if (jVertex.getJGraph().isShowAspects()) {
                result.add(jVertex.getUserObject().toLines(Direct.NONE));
            } else {
                // show data constants and variables correctly
                result.add(getDataLines(node));
                // show the visible self-edges
                Line id =
                    node.hasId()
                            ? Line.atom(node.getId().getContentString()).style(
                                Style.ITALIC) : null;
                for (AspectEdge edge : jVertex.getEdges()) {
                    if (!isFiltered(jVertex, edge)) {
                        Line line = getHostLine(edge);
                        if (id != null
                            && edge.getDisplayLabel().getRole() == NODE_TYPE) {
                            line = id.append(" : ").append(line);
                            id = null;
                        }
                        result.add(line, Direct.NONE);
                    }
                }
                if (id != null) {
                    // we're not going to have any node types:
                    // add the node id on a separate line
                    result.add(id, Direct.NONE);
                }
            }
            for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
                if (!isFiltered(jVertex, edge)) {
                    result.add(getHostLine(edge), Direct.NONE);
                }
            }
        }
        return result;
    }

    /**
     * On demand prefixes the label with the edge's aspect values.
     */
    private Line getHostLine(AspectEdge edge) {
        edge.testFixed(true);
        Line result = getAspectEdgeLine(edge);
        if (edge.target() != edge.source()) {
            // this is an attribute edge displayed as a node label
            String suffix =
                ASSIGN_TEXT + edge.target().getAttrAspect().getContentString();
            result = result.append(suffix);
        } else {
            result = addRoleIndicator(result, edge);
        }
        return result;
    }

    /** Recomputes the set of node lines for this aspect node. */
    private MultiLabel getTypeNodeLabel(AspectJVertex jVertex) {
        AspectNode node = jVertex.getNode();
        node.testFixed(true);
        MultiLabel result = new MultiLabel();
        if (jVertex.getJGraph().isShowAspects()) {
            result.add(jVertex.getUserObject().toLines(Direct.NONE));
            for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
                if (!isFiltered(jVertex, edge)) {
                    result.add(getTypeLine(edge), Direct.NONE);
                }
            }
        } else {
            if (node.hasImport()) {
                result.add(IMPORT_LINE, Direct.NONE);
            }
            // show data constants and variables correctly
            result.add(getDataLines(node));
            // show the visible self-edges
            for (AspectEdge edge : jVertex.getEdges()) {
                if (!isFiltered(jVertex, edge)) {
                    result.add(getTypeLine(edge), Direct.NONE);
                }
            }
            for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
                if (!isFiltered(jVertex, edge)) {
                    result.add(getTypeLine(edge), Direct.NONE);
                }
            }
            if (node.isEdge()) {
                StringBuilder line = new StringBuilder();
                LabelPattern pattern = node.getEdgePattern();
                line.append(">> ");
                line.append(pattern.getLabel(pattern.getArgNames().toArray()));
                result.add(Line.atom(line.toString()), Direct.NONE);
            }
        }
        return result;
    }

    /**
     * On demand prefixes the label with the edge's aspect values.
     */
    private Line getTypeLine(AspectEdge edge) {
        edge.testFixed(true);
        Line result = getAspectEdgeLine(edge);
        if (edge.getKind() == AspectKind.ABSTRACT
            && edge.label().getRole() == NODE_TYPE) {
            result = result.style(Style.ITALIC);
        }
        if (edge.target() != edge.source()) {
            result = result.append(TYPE_TEXT);
            result =
                result.append(getSignatureLine(edge.target().getAttrKind().getSignature()));
        } else if (edge.getAttrKind().hasSignature()) {
            // this is a field declaration
            result = result.append(TYPE_TEXT);
            result =
                result.append(getSignatureLine(edge.getAttrKind().getSignature()));
        }
        result = addRoleIndicator(result, edge);
        return result;
    }

    /** Recomputes the set of node lines for this aspect node. */
    private MultiLabel getRuleNodeLabel(AspectJVertex jVertex) {
        AspectNode node = jVertex.getNode();
        node.testFixed(true);
        MultiLabel result = new MultiLabel();
        // show the node identity
        if (jVertex.getJGraph().isShowNodeIdentities() && !node.hasId()) {
            result.add(getNodeIdLine(node));
        }
        // the following used to include hasError() as a disjunct
        if (jVertex.getJGraph().isShowAspects()) {
            result.add(jVertex.getUserObject().toLines(Direct.NONE));
            for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
                if (!isFiltered(jVertex, edge)) {
                    result.add(getRuleLine(edge), Direct.NONE);
                }
            }
            if (node.hasColor()) {
                result.add(Line.atom(node.getColor().toString()), Direct.NONE);
            }
        } else {
            Line idLine =
                node.hasId()
                        ? Line.atom(node.getId().getContentString()).style(
                            Style.ITALIC) : null;
            // show the quantifier aspect correctly
            if (node.getKind().isQuantifier()) {
                result.add(getQuantifierLines(node, idLine));
                idLine = null;
            }
            // show data constants and variables correctly
            result.add(getDataLines(node));
            // show the visible self-edges
            for (AspectEdge edge : jVertex.getEdges()) {
                if (!isFiltered(jVertex, edge)) {
                    Line line = getRuleLine(edge);
                    if (idLine != null
                        && edge.getDisplayLabel().getRole() == NODE_TYPE) {
                        line = idLine.append(" : ").append(line);
                        idLine = null;
                    }
                    result.add(line, Direct.NONE);
                }
            }
            if (idLine != null) {
                // we're not going to have any node types:
                // add the node id on a separate line
                result.add(idLine, Direct.NONE);
            }
            for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
                if (!isFiltered(jVertex, edge)) {
                    result.add(getRuleLine(edge), Direct.NONE);
                }
            }
            if (node.hasColor()) {
                StringBuilder text = new StringBuilder("& ");
                text.append(AspectKind.COLOR.getName());
                Line colorLine =
                    Line.atom(text.toString()).color(
                        Colors.findColor(node.getColor().getContentString()));
                result.add(colorLine, Direct.NONE);
            }
        }
        return result;
    }

    /**
     * On demand prefixes the label with the edge's aspect values.
     */
    private Line getRuleLine(AspectEdge edge) {
        edge.testFixed(true);
        Line result = getAspectEdgeLine(edge);
        if (edge.target() != edge.source()) {
            // this is an attribute edge displayed as a node label
            String suffix =
                ASSIGN_TEXT + edge.target().getAttrAspect().getContentString();
            result = result.append(suffix);
        }
        result = addRoleIndicator(result, edge);
        return result;
    }

    /** This implementation adds the data edges to the super result. */
    private MultiLabel getLTSJVertexLabel(LTSJVertex jVertex) {
        MultiLabel result = new MultiLabel();
        // show the node identity if required
        if (jVertex.getJGraph().isShowStateIdentities()) {
            GraphState state = jVertex.getNode();
            StringBuilder id = new StringBuilder(state.toString());
            CtrlState ctrlState = state.getCtrlState();
            if (!ctrlState.getAut().isDefault() || !ctrlState.isStart()) {
                id.append("|" + ctrlState.toString());
            }
            result.add(Line.atom(id.toString()).style(Style.ITALIC),
                Direct.NONE);
        }
        // only add edges that have an unfiltered label
        boolean isShowAnchors = jVertex.getJGraph().isShowAnchors();
        for (Edge edge : jVertex.getEdges()) {
            if (!isFiltered(jVertex, edge)) {
                String text = ((GraphTransition) edge).text(isShowAnchors);
                result.add(Line.atom(text), Direct.NONE);
            }
        }
        return result;
    }

    /**
     * Appends the bound variables to the lines, if this list is not empty
     */
    private MultiLabel getCtrlJVertexLabel(CtrlJVertex jVertex) {
        MultiLabel result = getBasicVertexLabel(jVertex);
        CtrlState state = jVertex.getNode();
        List<CtrlVar> boundVars = state.getBoundVars();
        if (boundVars.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(boundVars.toString());
            result.add(Line.atom(sb.toString()), Direct.NONE);
        }
        if (jVertex.isTransient()) {
            StringBuilder action = new StringBuilder();
            action.append('<');
            action.append(state.getRecipe());
            action.append('>');
            result.add(Line.atom(action.toString()), Direct.NONE);
        }
        return result;
    }

    /** Returns a list of lines together making up the label text of a vertex. */
    protected MultiLabel getJEdgeLabel(GraphJEdge jEdge) {
        MultiLabel result;
        switch (this.role) {
        case HOST:
        case RULE:
        case TYPE:
            result = getAspectJEdgeLabel((AspectJEdge) jEdge);
            break;
        case LTS:
            result = getLTSJEdgeLabel((LTSJEdge) jEdge);
            break;
        default:
            result = getBasicJEdgeLabel(jEdge);
        }
        return result;
    }

    private MultiLabel getBasicJEdgeLabel(GraphJEdge jEdge) {
        MultiLabel result = new MultiLabel();
        for (Edge edge : jEdge.getEdges()) {
            // only add edges that have an unfiltered label
            if (!isFiltered(jEdge, edge)) {
                result.add(Line.atom(edge.label().text()),
                    jEdge.getDirect(edge));
            }
        }
        return result;
    }

    private MultiLabel getAspectJEdgeLabel(AspectJEdge jEdge) {
        MultiLabel result = null;
        // if both source and target nodes are nodified, 
        // test for source node first
        if (jEdge.isNodeEdgeOut()) {
            result = new MultiLabel();
        } else if (jEdge.isNodeEdgeIn()) {
            result = new MultiLabel();
            LabelPattern pattern =
                ((AspectJVertex) jEdge.getTargetVertex()).getEdgeLabelPattern();
            @SuppressWarnings({"unchecked", "rawtypes"})
            GraphBasedModel<HostGraph> resourceModel =
                (GraphBasedModel) jEdge.getJModel().getResourceModel();
            try {
                HostNode target =
                    (HostNode) resourceModel.getMap().getNode(
                        jEdge.getTargetNode());
                String label =
                    pattern.getLabel(resourceModel.toResource(), target);
                result.add(Line.atom(label), jEdge.getDirect(null));
            } catch (FormatException e) {
                // assert false;
            }
        } else if (jEdge.isSourceLabel()) {
            result = new MultiLabel();
        } else {
            result = new MultiLabel();
            for (AspectEdge edge : jEdge.getEdges()) {
                // only add edges that have an unfiltered label
                if (!isFiltered(jEdge, edge)) {
                    result.add(getAspectEdgeLine(edge), jEdge.getDirect(edge));
                }
            }
        }
        return result;
    }

    /** Computes the multi-line label for a given LSTJEdge. */
    private MultiLabel getLTSJEdgeLabel(LTSJEdge jEdge) {
        MultiLabel result = new MultiLabel();
        boolean isShowAnchors = jEdge.getJGraph().isShowAnchors();
        for (Edge edge : jEdge.getEdges()) {
            // only add edges that have an unfiltered label
            if (!isFiltered(jEdge, edge)) {
                GraphTransition trans = (GraphTransition) edge;
                result.add(Line.atom(trans.text(isShowAnchors)),
                    jEdge.getDirect(edge));
            }
        }
        return result;
    }

    /** Returns a line representing the label of a given edge. */
    private Line getAspectEdgeLine(AspectEdge edge) {
        Label label =
            ((AspectJGraph) getJGraph()).isShowAspects() ? edge.label()
                    : edge.getDisplayLabel();
        Line result = Line.atom(label.text());
        switch (label.getRole()) {
        case FLAG:
            result = result.style(Style.ITALIC);
            break;
        case NODE_TYPE:
            result = result.style(Style.BOLD);
        }
        result = addLevelName(result, edge);
        return result;
    }

    /**
     * Appends a level name to a given text,
     * depending on an edge role.
     */
    private Line addLevelName(Line line, AspectEdge edge) {
        Line result = line;
        if (edge.getKind().isRole()) {
            String name = edge.getLevelName();
            if (name != null && name.length() != 0) {
                if (!name.equals(edge.source().getLevelName())
                    && !name.equals(edge.source().getLevelName())) {
                    result = result.append(LEVEL_NAME_SEPARATOR + name);
                }
            }
        }
        return result;
    }

    /**
     * Decorates a given line with an optional prefix and colour.
     */
    private Line addRoleIndicator(Line text, AspectEdge edge) {
        Line result = text;
        Aspect aspect = edge.getAspect();
        if (aspect != null && !aspect.equals(edge.source().getAspect())) {
            AspectKind kind = aspect.getKind();
            result = getRolePrefix(kind).append(result);
            switch (kind) {
            case ERASER:
                result = result.color(ColorType.ERASER);
                break;
            case ADDER:
                result = result.color(ColorType.CREATOR);
                break;
            case LET:
                if (edge.getGraphRole() == GraphRole.RULE) {
                    result = result.color(ColorType.CREATOR);
                }
                break;
            case CREATOR:
                result = result.color(ColorType.CREATOR);
                break;
            case EMBARGO:
                result = result.color(ColorType.EMBARGO);
                break;
            case REMARK:
                result = result.color(ColorType.REMARK);
                /* TODO test if there are errors caused by commenting out
                // replace all newlines by // as well
                String NEWLINE = HTMLConverter.HTML_LINEBREAK;
                int lineStart = text.indexOf(NEWLINE);
                while (lineStart >= 0) {
                    text.insert(lineStart + NEWLINE.length(),
                        REMARK.getDisplayPrefix());
                    lineStart =
                        text.indexOf(NEWLINE, lineStart + NEWLINE.length());
                }
                */
                break;
            }
        }
        return result;
    }

    /** 
     * Returns the (possibly empty) list of lines 
     * describing the node identity, if this is to be shown
     * according to the current setting.
     */
    private MultiLabel getNodeIdLine(AspectNode node) {
        MultiLabel result = new MultiLabel();
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
            result.add(Line.atom(id).style(Style.ITALIC), Direct.NONE);
        }
        return result;
    }

    /**
     * Returns the lines describing this node's main aspect.
     * Currently this just concerns a possible quantifier.
     */
    private MultiLabel getQuantifierLines(AspectNode node, Line id) {
        Line line = Line.empty();
        if (id != null) {
            line = line.append(id).append(" : ");
        }
        switch (node.getKind()) {
        case FORALL:
            line = line.append(FORALL);
            break;
        case FORALL_POS:
            line = line.append(FORALL_POS);
            break;
        case EXISTS:
            line = line.append(EXISTS);
            break;
        case EXISTS_OPT:
            line = line.append(EXISTS_OPT);
        }
        return MultiLabel.singleton(line, Direct.NONE);
    }

    /** Returns lines describing any data content of the JVertex. */
    private MultiLabel getDataLines(AspectNode node) {
        MultiLabel result = new MultiLabel();
        Aspect attrAspect = node.getAttrAspect();
        if (attrAspect.getKind().hasSignature()) {
            Line dataLine = null;
            if (!attrAspect.hasContent()) {
                dataLine =
                    getSignatureLine(attrAspect.getKind().getSignature());
            } else if (!this.jGraph.isShowNodeIdentities()) {
                // show constants only if they are not already shown as node identities
                dataLine = Line.atom(attrAspect.getContentString());
            }
            if (dataLine != null) {
                result.add(dataLine, Direct.NONE);
            }
        }
        return result;
    }

    /** 
     * Tests if a given edge is currently being filtered.
     */
    private boolean isFiltered(GraphJCell jCell, Edge edge) {
        boolean result = false;
        LabelTree labelTree = jCell.getJGraph().getLabelTree();
        if (edge != null && labelTree != null) {
            Label key = jCell.getKey(edge);
            result = key != null && labelTree.isFiltered(key);
        }
        return result;
    }

    /** Returns the JGraph on which this label refresher is based. */
    public GraphJGraph getJGraph() {
        return this.jGraph;
    }

    private final GraphJGraph jGraph;
    private final GraphRole role;

    /** Returns the label prefix associated with a given role. */
    private static Line getSignatureLine(SignatureKind kind) {
        return sigLineMap.get(kind);
    }

    /** Returns the label prefix associated with a given role. */
    private static Line getRolePrefix(AspectKind kind) {
        return rolePrefixMap.get(kind);
    }

    static private final Map<SignatureKind,Line> sigLineMap;
    static {
        Map<SignatureKind,Line> map =
            new EnumMap<SignatureKind,Line>(SignatureKind.class);
        for (SignatureKind kind : SignatureKind.values()) {
            map.put(kind, Line.atom(kind.getName()).style(Style.BOLD));
        }
        sigLineMap = map;
    }

    static private final Map<AspectKind,Line> rolePrefixMap;
    static {
        Map<AspectKind,Line> map =
            new EnumMap<AspectKind,Line>(AspectKind.class);
        for (AspectKind kind : AspectKind.values()) {
            Line prefix;
            switch (kind) {
            case ADDER:
                prefix = Line.atom("!+ ");
                break;
            case CREATOR:
                prefix = Line.atom("+ ");
                break;
            case EMBARGO:
                prefix = Line.atom("! ");
                break;
            case ERASER:
                prefix = Line.atom("- ");
                break;
            case REMARK:
                prefix = Line.atom("// ");
                break;
            default:
                prefix = Line.empty();
            }
            map.put(kind, prefix);
        }
        rolePrefixMap = map;
    }

    static private final String IMPORT_TEXT = String.format("%simport%s",
        Util.FRENCH_QUOTES_OPEN, Util.FRENCH_QUOTES_CLOSED);
    static private final Line IMPORT_LINE = Line.atom(IMPORT_TEXT).style(
        Style.ITALIC);
    static private final Line EXISTS = Line.atom("" + Util.EXISTS);
    static private final Line EXISTS_OPT = EXISTS.append(Line.atom("?").style(
        Style.SUPER));
    static private final Line FORALL = Line.atom("" + Util.FORALL);
    static private final Line FORALL_POS = FORALL.append(Line.atom(">0").style(
        Style.SUPER));
    static private final String ASSIGN_TEXT = " = ";
    static private final String TYPE_TEXT = ": ";
    /** Separator between level name and edge label. */
    static final char LEVEL_NAME_SEPARATOR = '@';
}
