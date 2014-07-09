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
import groove.control.CtrlVar;
import groove.control.Position;
import groove.control.Valuator;
import groove.control.instance.Frame;
import groove.control.template.Location;
import groove.control.template.Switch;
import groove.grammar.aspect.Aspect;
import groove.grammar.aspect.AspectEdge;
import groove.grammar.aspect.AspectKind;
import groove.grammar.aspect.AspectNode;
import groove.grammar.host.HostGraph;
import groove.grammar.host.HostNode;
import groove.grammar.host.ValueNode;
import groove.grammar.model.FormatException;
import groove.grammar.model.GraphBasedModel;
import groove.grammar.rule.VariableNode;
import groove.grammar.type.LabelPattern;
import groove.graph.Edge;
import groove.graph.EdgeRole;
import groove.graph.GraphRole;
import groove.graph.Label;
import groove.gui.jgraph.AspectJEdge;
import groove.gui.jgraph.AspectJVertex;
import groove.gui.jgraph.CtrlJVertex;
import groove.gui.jgraph.JCell;
import groove.gui.jgraph.JEdge;
import groove.gui.jgraph.JGraph;
import groove.gui.jgraph.JVertex;
import groove.gui.jgraph.LTSJEdge;
import groove.gui.jgraph.LTSJVertex;
import groove.gui.look.Line.Style;
import groove.gui.look.MultiLabel.Direct;
import groove.gui.tree.LabelTree;
import groove.io.Util;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.StartGraphState;
import groove.lts.Status.Flag;
import groove.util.Colors;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Visual value refresher for the {@link VisualKey#LABEL}.
 * @author Arend Rensink
 * @version $Revision $
 */
public class LabelValue implements VisualValue<MultiLabel> {
    /** Constructs a value strategy for a given JGraph. */
    public LabelValue(JGraph<?> jGraph) {
        this.jGraph = jGraph;
        this.role = jGraph.getGraphRole();
    }

    @Override
    public MultiLabel get(JCell<?> cell) {
        MultiLabel result = null;
        if (cell instanceof JVertex) {
            result = getJVertexLabel((JVertex<?>) cell);
        } else if (cell instanceof JEdge) {
            result = getJEdgeLabel((JEdge<?>) cell);
        }
        return result;
    }

    /** Returns a list of lines together making up the label text of a vertex. */
    protected MultiLabel getJVertexLabel(JVertex<?> jVertex) {
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
    private MultiLabel getBasicVertexLabel(JVertex<?> jVertex) {
        MultiLabel result = new MultiLabel();
        // show the node identity if required
        if (jVertex.getJGraph().isShowNodeIdentities()) {
            result.add(Line.atom(jVertex.getNode().toString()).style(Style.ITALIC));
        }
        // only add edges that have an unfiltered label
        addEdgeLabels(jVertex, result);
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
                result.add(jVertex.getUserObject().toLines());
            } else {
                // show data constants and variables correctly
                result.add(getDataLines(node));
                // show the visible self-edges
                Line id =
                    node.hasId() ? Line.atom(node.getId().getContentString()).style(Style.ITALIC)
                        : null;
                boolean implicitType = jVertex.getJModel().getTypeGraph().isImplicit();
                for (AspectEdge edge : jVertex.getEdges()) {
                    if (!isFiltered(jVertex, edge)) {
                        Line line = edge.toLine(true, jVertex.getAspect());
                        if (id != null) {
                            if (edge.getRole() == NODE_TYPE) {
                                line = id.append(" : ").append(line);
                            } else {
                                // we're not going to have any node types:
                                // add the node id on a separate line
                                result.add(id);
                            }
                            id = null;
                        }
                        if (!implicitType && edge.getRole() == EdgeRole.BINARY && !edge.hasErrors()) {
                            line = line.append(LOOP_SUFFIX);
                        }
                        result.add(line);
                    }
                }
            }
            for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
                if (!isFiltered(jVertex, edge)) {
                    result.add(edge.toLine(true, jVertex.getAspect()));
                }
            }
        }
        return result;
    }

    /** Recomputes the set of node lines for this aspect node. */
    private MultiLabel getTypeNodeLabel(AspectJVertex jVertex) {
        AspectNode node = jVertex.getNode();
        node.testFixed(true);
        MultiLabel result = new MultiLabel();
        if (jVertex.getJGraph().isShowAspects()) {
            result.add(jVertex.getUserObject().toLines());
            for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
                if (!isFiltered(jVertex, edge)) {
                    Line line = edge.label().toLine();
                    // check for primitive type edges
                    if (!edge.isLoop()) {
                        SignatureKind type = edge.target().getAttrKind().getSignature();
                        line = line.append(Line.atom(type.getName()));
                    }
                    result.add(line);
                }
            }
        } else {
            if (node.hasImport()) {
                result.add(IMPORT_LINE);
            }
            // show data constants and variables correctly
            result.add(getDataLines(node));
            // show the visible self-edges
            boolean implicitType = jVertex.getJModel().getTypeGraph().isImplicit();
            for (AspectEdge edge : jVertex.getEdges()) {
                if (!isFiltered(jVertex, edge)) {
                    Line line = edge.toLine(true, jVertex.getAspect());
                    if (!implicitType && edge.getRole() == EdgeRole.BINARY && !edge.hasErrors()) {
                        line = line.append(LOOP_SUFFIX);
                    }
                    result.add(line);
                }
            }
            for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
                if (!isFiltered(jVertex, edge)) {
                    result.add(edge.toLine(true, jVertex.getAspect()));
                }
            }
            if (node.isEdge()) {
                StringBuilder line = new StringBuilder();
                LabelPattern pattern = node.getEdgePattern();
                line.append(">> ");
                line.append(pattern.getLabel(pattern.getArgNames().toArray()));
                result.add(Line.atom(line.toString()));
            }
        }
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
            result.add(jVertex.getUserObject().toLines());
            for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
                if (!isFiltered(jVertex, edge)) {
                    Line line = edge.label().toLine();
                    // check for assignment edges
                    if (!edge.isLoop()) {
                        line =
                            line.append(" = " + edge.target().getAttrAspect().getContentString());
                    }
                    result.add(line);
                }
            }
            if (node.hasColor()) {
                result.add(Line.atom(node.getColor().toString()));
            }
        } else {
            Line idLine =
                node.hasId() ? Line.atom(node.getId().getContentString()).style(Style.ITALIC)
                    : null;
            // show the quantifier aspect correctly
            if (node.getKind().isQuantifier()) {
                result.add(getQuantifierLines(node, idLine));
                idLine = null;
            }
            // show data constants and variables correctly
            result.add(getDataLines(node));
            // show the visible self-edges
            boolean implicitType = jVertex.getJModel().getTypeGraph().isImplicit();
            for (AspectEdge edge : jVertex.getEdges()) {
                if (!isFiltered(jVertex, edge)) {
                    Line line = edge.toLine(true, jVertex.getAspect());
                    if (idLine != null) {
                        if (edge.getRole() == NODE_TYPE) {
                            line = idLine.append(" : ").append(line);
                        } else {
                            result.add(idLine);
                        }
                        idLine = null;
                    }
                    if (!implicitType && edge.getRole() == EdgeRole.BINARY && !edge.hasErrors()) {
                        line = line.append(LOOP_SUFFIX);
                    }
                    result.add(line);
                }
            }
            if (idLine != null) {
                // we're not going to have any node types:
                // add the node id on a separate line
                result.add(idLine);
            }
            for (AspectEdge edge : jVertex.getExtraSelfEdges()) {
                if (!isFiltered(jVertex, edge)) {
                    result.add(edge.toLine(true, jVertex.getAspect()));
                }
            }
            if (node.hasColor()) {
                StringBuilder text = new StringBuilder("& ");
                text.append(AspectKind.COLOR.getName());
                Line colorLine =
                    Line.atom(text.toString()).color(
                        Colors.findColor(node.getColor().getContentString()));
                result.add(colorLine);
            }
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
            line = line.append(FORALL_LINE);
            break;
        case FORALL_POS:
            line = line.append(FORALL_POS_LINE);
            break;
        case EXISTS:
            line = line.append(EXISTS_LINE);
            break;
        case EXISTS_OPT:
            line = line.append(EXISTS_OPT_LINE);
        }
        return MultiLabel.singleton(line, Direct.NONE);
    }

    /** This implementation adds the data edges to the super result. */
    private MultiLabel getLTSJVertexLabel(LTSJVertex jVertex) {
        MultiLabel result = new MultiLabel();
        // show the node identity if required
        Line idLine = null;
        if (jVertex.getJGraph().isShowStateIdentities()) {
            GraphState state = jVertex.getNode();
            StringBuilder id = new StringBuilder(state.toString());
            idLine = Line.atom(id.toString()).style(Style.ITALIC);
        }
        if (jVertex.getJGraph().isShowStateStatus()) {
            Line statusLine = getStatus(jVertex.getNode());
            if (idLine == null) {
                idLine = statusLine;
            } else {
                idLine = idLine.append(" : ").append(statusLine);
            }
        }
        if (idLine != null) {
            result.add(idLine);
        }
        if (jVertex.getJGraph().isShowControlStates()) {
            GraphState state = jVertex.getNode();
            Frame frame = state.getPrimeFrame();
            Object[] values = state.getPrimeValues();
            if (!frame.isStart() || values.length > 0) {
                result.add(getStackLine(frame.getLocation(), values));
            }
            Stack<Switch> stack = frame.getSwitchStack();
            for (int i = stack.size() - 1; i >= 0; i--) {
                values = Valuator.pop(values);
                Switch sw = stack.get(i);
                result.add(getStackLine(sw.getSource(), values));
            }
        }
        // only add edges that have an unfiltered label
        boolean isShowAnchors = jVertex.getJGraph().isShowAnchors();
        for (Edge edge : jVertex.getEdges()) {
            if (!isFiltered(jVertex, edge)) {
                Line line;
                if (isShowAnchors) {
                    line = Line.atom(((GraphTransition) edge).text(isShowAnchors));
                } else {
                    line = edge.label().toLine();
                }
                if (edge.getRole() == EdgeRole.BINARY) {
                    line = line.append(LOOP_SUFFIX);
                }
                result.add(line);
            }
        }
        if (!jVertex.isAllOutVisible()) {
            result.add(RESIDUAL_LINE);
        }
        return result;
    }

    /** Returns the status line for a given state. */
    private Line getStatus(GraphState state) {
        Line result;
        if (state instanceof StartGraphState && !state.isError() && !state.isFinal()
            && !state.isResult()) {
            result = this.startLine;
        } else {
            // determine main flag
            Flag main = null;
            if (state.isAbsent()) {
                main = Flag.ABSENT;
            } else if (state.isInternalState()) {
                main = Flag.INTERNAL;
            } else if (state.isError()) {
                main = Flag.ERROR;
            } else if (state.isTransient()) {
                main = Flag.TRANSIENT;
            } else if (state.isResult()) {
                main = Flag.RESULT;
            } else if (state.isFinal()) {
                main = Flag.FINAL;
            } else if (state.isDone()) {
                main = Flag.DONE;
            } else if (state.isClosed()) {
                main = Flag.CLOSED;
            }
            result = main == null ? this.openLine : getStatus(main);
        }
        return result;
    }

    /** Returns the status line for a given status flag. */
    private Line getStatus(Flag flag) {
        if (this.statusMap == null) {
            this.statusMap = new EnumMap<Flag,Line>(Flag.class);
            for (Flag f : Flag.values()) {
                String text = null;
                switch (f) {
                case ABSENT:
                    text = "absent";
                    break;
                case CLOSED:
                case DONE:
                    text = "closed";
                    break;
                case ERROR:
                    text = "error";
                    break;
                case FINAL:
                    text = "final";
                    break;
                case INTERNAL:
                    text = "internal";
                    break;
                case RESULT:
                    text = "result";
                    break;
                case TRANSIENT:
                    text = "transient";
                    break;
                }
                if (text != null) {
                    this.statusMap.put(f, Line.atom(text).style(Style.BOLD));
                }
            }
        }
        return this.statusMap.get(flag);
    }

    /** Map from flags to corresponding lines on state. */
    private Map<Flag,Line> statusMap;
    /** State line for the start state. */
    private final Line startLine = Line.atom("start").style(Style.BOLD);
    /** State line for an open state. */
    private final Line openLine = Line.atom("open").style(Style.BOLD);

    private Line getStackLine(Location loc, Object[] values) {
        Line result = Line.empty();
        if (loc != null) {
            result = Line.atom(loc.toString()).style(Style.ITALIC);
            if (loc.hasVars()) {
                List<CtrlVar> vars = loc.getVars();
                StringBuilder content = new StringBuilder();
                content.append(" [");
                for (int i = 0; i < vars.size(); i++) {
                    if (i > 0) {
                        content.append(',');
                    }
                    content.append(vars.get(i).getName());
                    content.append('=');
                    HostNode val = (HostNode) values[i];
                    if (val instanceof ValueNode) {
                        content.append(((ValueNode) val).getSymbol());
                    } else {
                        content.append(val);
                    }
                }
                content.append(']');
                result = result.append(content.toString());
            }
        }
        return result;
    }

    /**
     * Appends the bound variables to the lines, if this list is not empty
     */
    private MultiLabel getCtrlJVertexLabel(CtrlJVertex jVertex) {
        MultiLabel result = new MultiLabel();
        result.add(Line.atom(jVertex.getNode().toString()).style(Style.ITALIC));
        Position<?,?> state = jVertex.getNode().getPosition();
        // add start/final/depth qualifiers
        Line qualifiers = Line.empty();
        if (state.isStart()) {
            qualifiers = qualifiers.append(START_LINE);
        }
        if (!state.isTrial()) {
            if (!qualifiers.isEmpty()) {
                qualifiers = qualifiers.append("/");
            }
            qualifiers = qualifiers.append(state.isDead() ? DEAD_LINE : FINAL_LINE);
        }
        if (!qualifiers.isEmpty()) {
            result.add(qualifiers.style(Style.BOLD));
        }
        if (state.getTransience() > 0) {
            result.add(Line.atom("transience = " + state.getTransience()));
        }
        // add location variables
        for (CtrlVar var : state.getVars()) {
            Line line =
                Line.atom(var.getName()).append(" : ").append(
                    Line.atom(var.getType().toString()).style(Style.BOLD));
            result.add(line);
        }
        // add self-edges
        addEdgeLabels(jVertex, result);
        return result;
    }

    /** Returns a list of lines together making up the label text of a jEdge. */
    protected MultiLabel getJEdgeLabel(JEdge<?> jEdge) {
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

    private MultiLabel getBasicJEdgeLabel(JEdge<?> jEdge) {
        MultiLabel result = new MultiLabel();
        addEdgeLabels(jEdge, result);
        return result;
    }

    /**
     * Adds the labels of all edges of a given cell to a multi-label.
     * @param jCell the cell from which the edges are added
     * @param result the resulting multi-label; modified by this call
     */
    private void addEdgeLabels(JCell<?> jCell, MultiLabel result) {
        boolean onVertex = jCell instanceof JVertex;
        for (Edge edge : jCell.getEdges()) {
            // only add edges that have an unfiltered label
            if (!isFiltered(jCell, edge)) {
                Direct dir = onVertex ? Direct.NONE : ((JEdge<?>) jCell).getDirect(edge);
                Line line = edge.label().toLine();
                if (onVertex && edge.getRole() == EdgeRole.BINARY) {
                    line = line.append(LOOP_SUFFIX);
                }
                result.add(line, dir);
            }
        }
    }

    private MultiLabel getAspectJEdgeLabel(AspectJEdge jEdge) {
        MultiLabel result = null;
        // if both source and target nodes are nodified,
        // test for source node first
        if (jEdge.isNodeEdgeOut()) {
            result = new MultiLabel();
        } else if (jEdge.isNodeEdgeIn()) {
            result = new MultiLabel();
            LabelPattern pattern = jEdge.getTargetVertex().getEdgeLabelPattern();
            @SuppressWarnings({"unchecked", "rawtypes"})
            GraphBasedModel<HostGraph> resourceModel =
                (GraphBasedModel) jEdge.getJModel().getResourceModel();
            try {
                HostNode target = (HostNode) resourceModel.getMap().getNode(jEdge.getTargetNode());
                String label = pattern.getLabel(resourceModel.toResource(), target);
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
                    Line line;
                    if (jEdge.getJGraph().isShowAspects()) {
                        line = edge.label().toLine();
                    } else {
                        line = edge.toLine(false, jEdge.getAspect());
                    }
                    result.add(line, jEdge.getDirect(edge));
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
                result.add(Line.atom(trans.text(isShowAnchors)), jEdge.getDirect(edge));
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
            result.add(Line.atom(id).style(Style.ITALIC));
        }
        return result;
    }

    /** Returns lines describing any data content of the JVertex. */
    private MultiLabel getDataLines(AspectNode node) {
        MultiLabel result = new MultiLabel();
        Aspect attrAspect = node.getAttrAspect();
        if (attrAspect.getKind().hasSignature()) {
            if (!attrAspect.hasContent()) {
                result.add(getSignatureLine(attrAspect.getKind().getSignature()));
            } else if (!this.jGraph.isShowNodeIdentities()) {
                // show constants only if they are not already shown as node identities
                result.add(Line.atom(attrAspect.getContentString()));
            }
        }
        return result;
    }

    /**
     * Tests if a given edge is currently being filtered.
     */
    private boolean isFiltered(JCell<?> jCell, Edge edge) {
        boolean result = false;
        LabelTree<?> labelTree = jCell.getJGraph().getLabelTree();
        if (edge != null && labelTree != null) {
            Label key = jCell.getKey(edge);
            result = key != null && labelTree.isFiltered(key);
        }
        return result;
    }

    /** Returns the JGraph on which this label refresher is based. */
    public JGraph<?> getJGraph() {
        return this.jGraph;
    }

    private final JGraph<?> jGraph;
    private final GraphRole role;

    /** Returns the label prefix associated with a given role. */
    private static Line getSignatureLine(SignatureKind kind) {
        return sigLineMap.get(kind);
    }

    static private final Map<SignatureKind,Line> sigLineMap;
    static {
        Map<SignatureKind,Line> map = new EnumMap<SignatureKind,Line>(SignatureKind.class);
        for (SignatureKind kind : SignatureKind.values()) {
            map.put(kind, Line.atom(kind.getName()).style(Style.BOLD));
        }
        sigLineMap = map;
    }

    static private final String IMPORT_TEXT = String.format("%simport%s", Util.FRENCH_QUOTES_OPEN,
        Util.FRENCH_QUOTES_CLOSED);
    static private final Line IMPORT_LINE = Line.atom(IMPORT_TEXT).style(Style.ITALIC);
    static private final Line EXISTS_LINE = Line.atom("" + Util.EXISTS);
    static private final Line EXISTS_OPT_LINE =
        EXISTS_LINE.append(Line.atom("?").style(Style.SUPER));
    static private final Line FORALL_LINE = Line.atom("" + Util.FORALL);
    static private final Line FORALL_POS_LINE = FORALL_LINE.append(Line.atom(">0").style(
        Style.SUPER));
    /** Final line in a state vertex indicating residual invisible outgoing transitions. */
    static private final Line RESIDUAL_LINE = Line.atom("" + Util.DLA + Util.DA + Util.DRA);
    /** Line in a control vertex indicating a start state. */
    static private final Line START_LINE = Line.atom("start");
    /** Line in a control vertex indicating a deadlocked state. */
    static private final Line DEAD_LINE = Line.atom("dead");
    /** Line in a control vertex indicating a final state. */
    static private final Line FINAL_LINE = Line.atom("final");
    /** Suffix indicating a self-loop on a node label. */
    static private final String LOOP_SUFFIX = " " + Util.CA;
}
