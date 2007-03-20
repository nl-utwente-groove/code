/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
 * $Id: RuleJModel.java,v 1.1.1.2 2007-03-20 10:42:47 kastenberg Exp $
 */
package groove.gui.jgraph;

import groove.graph.Edge;
import groove.graph.Label;
import groove.graph.Node;
import groove.trans.Rule;
import groove.trans.view.RuleEdge;
import groove.trans.view.RuleGraph;
import groove.trans.view.RuleNode;
import groove.util.Groove;

import static groove.gui.jgraph.JAttr.*;
import static groove.trans.view.RuleGraph.*;

import java.awt.Color;
import java.util.Collection;
import java.util.LinkedList;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * Implements jgraph's GraphModel interface on top of a SPORule.
 * All NACs must be embargoes.
 * Self-edges are represented as dangling incoming edges with a circle decoration
 * on the other end.
 * Dangling edges get invisible end nodes for their undefined source/target,
 * to improve (automatic) layout.
 * <p>NOTE: the JModel-GraphJModel-RuleJModel structure is up for revision.
 * @author Arend Rensink
 * @version $Revision: 1.1.1.2 $
 */
public class RuleJModel extends GraphJModel {
    // attribute arrays
    static public final AttributeMap[] RULE_NODE_ATTR = new AttributeMap[RuleGraph.NR_OF_ROLES];
    static public final AttributeMap[] RULE_EDGE_ATTR = new AttributeMap[RuleGraph.NR_OF_ROLES];
    
    static public final RuleJModel EMPTY_JMODEL = new RuleJModel();

    static {
        for (int role = 0; role < NR_OF_ROLES; role++) {
            // edge attributes
            AttributeMap edgeAttr = (AttributeMap) JAttr.DEFAULT_EDGE_ATTR.clone();
            GraphConstants.setEditable(edgeAttr, false);
            GraphConstants.setForeground(edgeAttr, RULE_COLOR[role]);
            GraphConstants.setLineColor(edgeAttr, RULE_COLOR[role]);
            GraphConstants.setLineWidth(edgeAttr, RULE_WIDTH[role]);
            GraphConstants.setDashPattern(edgeAttr, RULE_DASH[role]);
            GraphConstants.setLineEnd(edgeAttr, GraphConstants.ARROW_CLASSIC);
            GraphConstants.setEndFill(edgeAttr, role != EMBARGO);
            GraphConstants.setBeginFill(edgeAttr, true);
            GraphConstants.setBendable(edgeAttr, true);
            GraphConstants.setBackground(edgeAttr, Color.WHITE);
            GraphConstants.setOpaque(edgeAttr, true);
            GraphConstants.setConnectable(edgeAttr, false);
            GraphConstants.setDisconnectable(edgeAttr, false);
            RULE_EDGE_ATTR[role] = edgeAttr;

            // set default node attributes
            AttributeMap nodeAttr = (AttributeMap) JAttr.DEFAULT_NODE_ATTR.clone();
            nodeAttr.applyMap(edgeAttr);
            GraphConstants.setBorderColor(nodeAttr, RULE_COLOR[role]);
            GraphConstants.setBorderColor(nodeAttr, RULE_COLOR[role]);
            GraphConstants.setAutoSize(nodeAttr, true);
            GraphConstants.setSizeable(nodeAttr, false);
            GraphConstants.setBorder(nodeAttr, RULE_BORDER[role]);
            RULE_NODE_ATTR[role] = nodeAttr;
        }
    }

    static private final String[] ROLE_NAMES = new String[NR_OF_ROLES]; 
    static private final String[] ROLE_DESCRIPTIONS = new String[NR_OF_ROLES]; 
    
    static {
        ROLE_NAMES[EMBARGO] = "Embargo";
        ROLE_NAMES[READER] = "Reader";
        ROLE_NAMES[CREATOR] = "Creator";
        ROLE_NAMES[ERASER] = "Eraser";
        
        ROLE_DESCRIPTIONS[EMBARGO] = "Must be absent from a graph for this rule to apply";
        ROLE_DESCRIPTIONS[READER] = "Must be present in a graph for this rule to apply";
        ROLE_DESCRIPTIONS[CREATOR] = "Will be created by applying this rule";
        ROLE_DESCRIPTIONS[ERASER] = "Will be deleted by applying this rule";
    }

    /**
     * Specialized j-vertex for rule graphs, with its own tool tip text.
     */
    public static class RuleJVertex extends GraphJVertex {
        public RuleJVertex(RuleJModel jModel, RuleNode node) {
            super(jModel, node);
            this.role = node.role();
        }

        /** Specialises the return type. */
        @Override
		public RuleNode getNode() {
			return (RuleNode) super.getNode();
		}

		/**
         * Overwrites the method in <code>GraphJNode</code> to provide production rule specific
         * tool tips for nodes.
         */
        public String getToolTipText() {
            Collection<String> labels = getLabelSet();
            Node node = getNode();
            StringBuffer res = new StringBuffer(ROLE_NAMES[role]);
            res.append(" node ");
            res.append(strongTag.on(node));
            if (!labels.isEmpty()) {
                res.append(labels.size() == 1 ? " with self-edge " : " with self-edges ");
                res.append(Groove.toString(strongTag.on(labels.toArray(), true), "", "", ", ", " and "));
            }
            res.append("<br>"+ROLE_DESCRIPTIONS[role]);
            return htmlTag.on(res);
        }
        
        /**
         * Returns <tt>true</tt> only if the role of the edge to be added
         * equals the role of this j-vertex, and the superclass is also willing.
         * @require <tt>edge instanceof RuleGraph.RuleEdge</tt>
         */
        public boolean addSelfEdge(Edge edge) {
            if (((RuleEdge) edge).role() == role) {
                return super.addSelfEdge(edge);
            } else {
                return false;
            }
        }
        
        /**
         * Adds the appropriate colour around the text. 
         */
        @Override
		public String getHtmlText() {
        	Color roleColour = RULE_COLOR[role];
        	int red = roleColour.getRed();
        	int blue = roleColour.getBlue();
        	int green = roleColour.getGreen();
        	String innerText = super.getHtmlText();
        	return String.format("<span style=\"color: rgb(%s,%s,%s);\">%s</span>", red, green, blue, innerText);
		}

		/** The role of the underlying rule node. */
        private final int role;
    }

    /**
     * Specialized j-edge for rule graphs, with its own tool tip text.
     */
    public static class RuleJEdge extends GraphJEdge {
        public RuleJEdge(RuleEdge edge) {
            super(edge);
            this.role = edge.role();
        }

        /**
         * Overwrites the method to provide production rule specific
         * tool tips for edges.
         */
        public String getToolTipText() {
            Collection<String> labels = getLabelSet();
            StringBuffer res = new StringBuffer();
            int role = ((RuleEdge) getEdgeSet().iterator().next()).role();
            // special case for merge embargo
            if (role == EMBARGO && labels.contains(RuleGraph.MERGE_LABEL_TEXT)) {
                // merge embargo
                res.append("Merge embargo<br>Connected nodes must be distinct for this rule to apply");
            } else {
                res.append(ROLE_NAMES[role]);
                res.append(labels.size() == 1 ? " edge with label " : " edges with labels ");
                res.append(Groove.toString(strongTag.on(labels.toArray(), true), "", "", ", ", " and "));
                res.append("<br>"+ROLE_DESCRIPTIONS[role]);
            }
            return htmlTag.on(res);
        }
        
        /**
         * Returns <tt>true</tt> only if the role of the edge to be added
         * equals the role of this j-edge, and the superclass is also willing.
         * @require <tt>edge instanceof RuleGraph.RuleEdge</tt>
         */
        public boolean addEdge(Edge edge) {
            if (((RuleEdge) edge).role() == role) {
                return super.addEdge(edge);
            } else {
                return false;
            }
        }
        
        /** Specialises the return type. */
        @Override
		public RuleEdge getEdge() {
			return (RuleEdge) super.getEdge();
		}

		private final int role;
    }

    // --------------------- INSTANCE DEFINITIONS ------------------------

    /** 
     * Creates a new GraphJModel instance on top of a given SPORule.
     * @require rule != null;
     *          rule.nacSet() \subseteq Embargo
     * @ensure rule().equals(rule)
     */
    public RuleJModel(RuleGraph graph) {
        super(graph);
        this.rule = graph.toRule();
    }
    
    /** Constructor for a dummy model. */
    protected RuleJModel() {
    	rule = null;
    }

    /**
     * Returns the underlying production rule of this graph model.
     * @ensure result != null
     */
    public Rule getRule() {
        return rule;
    }

    protected AttributeMap createJVertexAttr(JVertex cell) {
        assert cell instanceof RuleJVertex : "Rule model cannot include non-JNode " + cell;
        RuleNode ruleNode = ((RuleJVertex) cell).getNode();
        return (AttributeMap) RULE_NODE_ATTR[ruleNode.role()].clone();
    }

    protected AttributeMap createJEdgeAttr(JEdge edge) {
        assert edge instanceof GraphJEdge : "Rule model cannot include non-JEdge " + edge;
        assert !((GraphJEdge) edge).getEdgeSet().isEmpty() : "Underlying edge set of "+edge+" ("+edge.getClass()+") should not be empty";
        Edge ruleEdge = ((GraphJEdge) edge).getEdgeSet().iterator().next();
        assert ruleEdge instanceof RuleEdge : "Rule model cannot include non-RuleEdge " + ruleEdge;
        int role = ((RuleEdge) ruleEdge).role();
        AttributeMap result = (AttributeMap) RULE_EDGE_ATTR[role].clone();
        if (role == EMBARGO && ruleEdge.label().equals(RuleGraph.MERGE_LABEL)) {
            // remove edge arrow 
            GraphConstants.setLineEnd(result, GraphConstants.ARROW_NONE);
        }
        return result;
    }
    
    /**
     * Overwrites the method so as to return a rule edge.
     * @require <tt>edge instanceof RuleGraph.RuleEdge</tt>
     */
    protected GraphJEdge createJEdge(Edge edge) {
        return new RuleJEdge((RuleEdge) edge);
    }

    /** Adds the correct border emphasis. */
	@Override
	protected AttributeMap getJVertexEmphAttr(JVertex jCell) {
		AttributeMap result = super.getJVertexEmphAttr(jCell);
        RuleNode ruleNode = ((RuleJVertex) jCell).getNode();
		GraphConstants.setBorder(result, JAttr.RULE_EMPH_BORDER[ruleNode.role()]);
		return result;
	}

	/**
     * Overwrites the method so as to return a rule vertex.
     * @require <tt>edge instanceof RuleGraph.RuleNode</tt>
     */
    protected GraphJVertex createJVertex(Node node) {
        return new RuleJVertex(this, (RuleNode) node);
    }
    
    /**
     * This implementation adds a "minimal" role prefix to the labels;
     * that is, reader roles are not added and other roles are only added
     * if the source and target nodes are both reader nodes.
     */
    protected Collection<String> getLabels(JEdge jEdge) {
        RuleEdge firstEdge = ((RuleJEdge) jEdge).getEdge();
        Collection<String> result = new LinkedList<String>();
        for (Edge edge: ((RuleJEdge) jEdge).getEdgeSet()) {
            // test if the edge shares a role with one of its end nodes
            boolean sharesRole = false;
            int edgeRole = ((RuleEdge) edge).role();
            for (int i = 0; !sharesRole && i < firstEdge.endCount(); i++) {
                sharesRole = ((RuleNode) firstEdge.end(i)).role() == edgeRole;
            }
            if (!sharesRole || causesRoleConfusion(edge.label())) {
                result.add(RuleGraph.ROLE_PREFIX[edgeRole] + edge.label().text());
            } else {
                result.add(edge.label().text());
            }
        }
        return result;
    }
    
    /**
     * This implementation adds a role label to the set if the underlying graph node is not a
     * reader.
     */
    protected Collection<String> getLabels(JVertex jCell) {
        Collection<String> result = new LinkedList<String>();
        int nodeRole = ((RuleJVertex) jCell).getNode().role();
        for (Edge selfEdge: ((RuleJVertex) jCell).getSelfEdgeSet()) {
            if (causesRoleConfusion(selfEdge.label())) {
                result.add(RuleGraph.ROLE_PREFIX[nodeRole] + selfEdge.label().text());
            } else {
                result.add(selfEdge.label().text());
            }
        }
        if (nodeRole != READER) {
            result.add(RuleGraph.ROLE_PREFIX[nodeRole]);
        }
        return result;
    }

    /**
     * Tests if a label may cause role confusion, because it is itself formatted as a label-with-role.
     * @param label the label to be tested
     * @return <code>true</code> if <code>label</code> may cause confusion
     */
    protected boolean causesRoleConfusion(Label label) {
        return RuleGraph.labelRole(label) != RuleGraph.NO_ROLE;
    }
    
    /**
     * The underlying production rule of this graph model.
     * @invariant rule != null
     */
    private final Rule rule;
}