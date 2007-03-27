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
 * $Id: AspectJModel.java,v 1.1 2007-03-27 14:18:29 rensink Exp $
 */
package groove.gui.jgraph;

import groove.graph.Edge;
import groove.graph.Node;
import groove.graph.aspect.AspectEdge;
import groove.graph.aspect.AspectElement;
import groove.graph.aspect.AspectGraph;
import groove.graph.aspect.AspectNode;
import groove.graph.aspect.AspectParser;
import groove.graph.aspect.AspectValue;
import groove.graph.aspect.AttributeAspect;
import groove.graph.aspect.RuleAspect;
import groove.rel.RegExprLabel;
import groove.trans.view.AspectualView;
import groove.util.Groove;

import static groove.gui.jgraph.JAttr.*;
import static groove.graph.aspect.RuleAspect.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * Implements jgraph's GraphModel interface on top of an {@link AspectualView}.
 * This is used to visualise rules and attributed graphs.
 * @author Arend Rensink
 * @version $Revision: 1.1 $
 */
public class AspectJModel extends GraphJModel {
    // attribute arrays
    static public final Map<AspectValue,AttributeMap> RULE_NODE_ATTR = new HashMap<AspectValue,AttributeMap>();
    static public final Map<AspectValue,AttributeMap> RULE_EDGE_ATTR = new HashMap<AspectValue,AttributeMap>();
    
    static public final AspectJModel EMPTY_JMODEL = new AspectJModel();

    static {
        for (AspectValue role: RuleAspect.getInstance().getValues()) {
            // edge attributes
            AttributeMap edgeAttr = (AttributeMap) JAttr.DEFAULT_EDGE_ATTR.clone();
            GraphConstants.setEditable(edgeAttr, false);
            GraphConstants.setForeground(edgeAttr, RULE_COLOR.get(role));
            GraphConstants.setLineColor(edgeAttr, RULE_COLOR.get(role));
            GraphConstants.setLineWidth(edgeAttr, RULE_WIDTH.get(role));
            GraphConstants.setDashPattern(edgeAttr, RULE_DASH.get(role));
            GraphConstants.setLineEnd(edgeAttr, GraphConstants.ARROW_CLASSIC);
            GraphConstants.setEndFill(edgeAttr, role != EMBARGO);
            GraphConstants.setBeginFill(edgeAttr, true);
            GraphConstants.setBendable(edgeAttr, true);
            GraphConstants.setBackground(edgeAttr, Color.WHITE);
            GraphConstants.setOpaque(edgeAttr, true);
            GraphConstants.setConnectable(edgeAttr, false);
            GraphConstants.setDisconnectable(edgeAttr, false);
            RULE_EDGE_ATTR.put(role,edgeAttr);

            // set default node attributes
            AttributeMap nodeAttr = (AttributeMap) JAttr.DEFAULT_NODE_ATTR.clone();
            nodeAttr.applyMap(edgeAttr);
            GraphConstants.setBorderColor(nodeAttr, RULE_COLOR.get(role));
            GraphConstants.setBorderColor(nodeAttr, RULE_COLOR.get(role));
            GraphConstants.setAutoSize(nodeAttr, true);
            GraphConstants.setSizeable(nodeAttr, false);
            GraphConstants.setBorder(nodeAttr, RULE_BORDER.get(role));
            RULE_NODE_ATTR.put(role,nodeAttr);
        }
    }

    static private final Map<AspectValue,String> ROLE_NAMES = new HashMap<AspectValue,String>(); 
	static private final Map<AspectValue,String> ROLE_DESCRIPTIONS = new HashMap<AspectValue,String>(); 
    
    static {
        ROLE_NAMES.put(EMBARGO,"Embargo");
        ROLE_NAMES.put(READER,"Reader");
        ROLE_NAMES.put(CREATOR,"Creator");
        ROLE_NAMES.put(ERASER,"Eraser");
        
        ROLE_DESCRIPTIONS.put(EMBARGO,"Must be absent from a graph for this rule to apply");
        ROLE_DESCRIPTIONS.put(READER, "Must be present in a graph for this rule to apply");
        ROLE_DESCRIPTIONS.put(CREATOR,"Will be created by applying this rule");
        ROLE_DESCRIPTIONS.put(ERASER,"Will be deleted by applying this rule");
    }

    /** Helper method to return the rule aspect value of an aspect node. */
	static private AspectValue role(AspectElement node) {
		return node.getValue(RuleAspect.getInstance());
	}

    /**
     * Specialized j-vertex for rule graphs, with its own tool tip text.
     */
    public class RuleJVertex extends GraphJVertex {
        public RuleJVertex(AspectJModel jModel, AspectNode node) {
            super(jModel, node);
            this.role = role(node);
        }

        /** Specialises the return type. */
        @Override
		public AspectNode getNode() {
			return (AspectNode) super.getNode();
		}

		/**
         * Overwrites the method in <code>GraphJNode</code> to provide production rule specific
         * tool tips for nodes.
         */
        @Override
        public String getToolTipText() {
            Collection<String> labels = getLabelSet();
            StringBuffer res = new StringBuffer(ROLE_NAMES.get(role));
            res.append(" node ");
            res.append(strongTag.on(getNodeIdentity()));
            if (!labels.isEmpty()) {
                res.append(labels.size() == 1 ? " with self-edge " : " with self-edges ");
                res.append(Groove.toString(strongTag.on(labels.toArray(), true), "", "", ", ", " and "));
            }
            res.append("<br>"+ROLE_DESCRIPTIONS.get(role));
            return htmlTag.on(res);
        }
        
        /**
         * Returns <tt>true</tt> only if the role of the edge to be added
         * equals the role of this j-vertex, and the superclass is also willing.
         * @require <tt>edge instanceof RuleGraph.RuleEdge</tt>
         */
        @Override
        public boolean addSelfEdge(Edge edge) {
            if (role((AspectEdge) edge) == role) {
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
        	String innerText = super.getHtmlText();
			if (innerText.length() > 0) {
				Color roleColour = RULE_COLOR.get(role);
				int red = roleColour.getRed();
				int blue = roleColour.getBlue();
				int green = roleColour.getGreen();
				return String.format("<span style=\"color: rgb(%s,%s,%s);\">%s</span>",
						red,
						green,
						blue,
						innerText);
        	} else {
        		return innerText;
        	}
		}
        
		/**
		 * On demand prefixes the label with the edge's aspect values.
		 */
		@Override
		public String getLabel(Object object) {
			assert object instanceof AspectEdge;
			String result = super.getLabel(object);
			if (isShowAspects()) {
				result = AspectParser.toString(((AspectEdge) object).getDeclaredValues(), result);
			}
			return result;
		}

		/**
		 * On demand adds the node aspects to the label set.
		 */
		@Override
		public Collection<String> getLabelSet() {
			Collection<String> result = new ArrayList<String>();
			if (isShowAspects()) {
				for (AspectValue value: getNode().getDeclaredValues()) {
					result.add(AspectParser.toString(value));
				}
			}
			result.addAll(super.getLabelSet());
			return result;
		}

		/**
		 * This implementation retrieves the node's image in the
		 * model and uses that one's identity.
		 */
		@Override
		protected String getNodeIdentity() {
			return view.getViewMap().get(getNode()).toString();
		}

		/** The role of the underlying rule node. */
        private final AspectValue role;
    }

    /**
     * Specialized j-edge for rule graphs, with its own tool tip text.
     */
    public class RuleJEdge extends GraphJEdge {
        public RuleJEdge(AspectEdge edge) {
            super(edge);
            this.role = role(edge);
        }

        /**
         * Overwrites the method to provide production rule specific
         * tool tips for edges.
         */
        @Override
        public String getToolTipText() {
            Collection<String> labels = getLabelSet();
            StringBuffer res = new StringBuffer();
            AspectValue role = role((AspectEdge) getEdgeSet().iterator().next());
            res.append(ROLE_NAMES.get(role));
            res.append(labels.size() == 1 ? " edge with label " : " edges with labels ");
            res.append(Groove.toString(strongTag.on(labels.toArray(), true), "", "", ", ", " and "));
            res.append("<br>"+ROLE_DESCRIPTIONS.get(role));
            return htmlTag.on(res);
        }
        
        /**
         * Returns <tt>true</tt> only if the role of the edge to be added
         * equals the role of this j-edge, and the superclass is also willing.
         * @require <tt>edge instanceof RuleGraph.RuleEdge</tt>
         */
        @Override
        public boolean addEdge(Edge edge) {
            if (role((AspectEdge) edge) == role) {
                return super.addEdge(edge);
            } else {
                return false;
            }
        }
//        
//		/**
//		 * Specialises the return type.
//		 */
//		@Override
//		public Set<AspectEdge> getEdgeSet() {
//			return (Set<AspectEdge>) super.getEdgeSet();
//		}
//		
		/**
		 * On demand prefixes the label with the edge's aspect values.
		 */
		@Override
		public String getLabel(Object object) {
			assert object instanceof AspectEdge;
			String result = super.getLabel(object);
			if (isShowAspects()) {
				result = AspectParser.toString(((AspectEdge) object).getDeclaredValues(), result);
			}
			return result;
		}

		/**
		 * This implementation retrieves the source node's image in the
		 * model and uses that one's identity.
		 */
		@Override
		protected String getSourceIdentity() {
			return view.getViewMap().get(getSourceNode()).toString();
		}

		/**
		 * This implementation retrieves the target node's image in the
		 * model and uses that one's identity.
		 */
		@Override
		protected String getTargetIdentity() {
			return view.getViewMap().get(getTargetNode()).toString();
		}

		private final AspectValue role;
    }

    // --------------------- INSTANCE DEFINITIONS ------------------------

    /** 
     * Creates a new GraphJModel instance on top of a given SPORule.
     * @require rule != null;
     *          rule.nacSet() \subseteq Embargo
     * @ensure rule().equals(rule)
     */
    public AspectJModel(AspectualView view) {
        super(view.getView());
        this.view = view;
    }
    
    /** Constructor for a dummy model. */
    protected AspectJModel() {
    	view = null;
    }

    /** Specialises the return type to return the aspect view on the rule. */
	@Override
	public AspectGraph graph() {
		return (AspectGraph) super.graph();
	}

	@Override
    protected AttributeMap createJVertexAttr(Node node) {
		AspectNode aspectNode = (AspectNode) node;
        AspectValue role = role(aspectNode);
        AttributeMap result = (AttributeMap) RULE_NODE_ATTR.get(role).clone();
        if (aspectNode.getValue(AttributeAspect.getInstance()) != null) {
        	result.applyMap(valueNodeAttr);
        }
        return result;
    }

    @Override
    protected AttributeMap createJEdgeAttr(Set<? extends Edge> edgeSet) {
        assert !edgeSet.isEmpty() : String.format("Underlying edge set should not be empty");
        Edge ruleEdge = edgeSet.iterator().next();
        assert ruleEdge instanceof AspectEdge : "Rule model cannot include non-RuleEdge " + ruleEdge;
        AspectValue role = role((AspectEdge) ruleEdge);
        AttributeMap result = (AttributeMap) RULE_EDGE_ATTR.get(role).clone();
        if (RegExprLabel.isEmpty(ruleEdge.label())) {
            // remove edge arrow 
            GraphConstants.setLineEnd(result, GraphConstants.ARROW_NONE);
        }
        return result;
    }
    
    /**
     * Overwrites the method so as to return a rule edge.
     * @require <tt>edge instanceof RuleGraph.RuleEdge</tt>
     */
    @Override
    protected GraphJEdge createJEdge(Edge edge) {
        return new RuleJEdge((AspectEdge) edge);
    }

    /** Adds the correct line width emphasis. */
	@Override
	protected AttributeMap getJEdgeEmphAttr(JEdge jCell) {
		AttributeMap result = super.getJEdgeEmphAttr(jCell);
        AspectEdge ruleEdge = (AspectEdge) ((RuleJEdge) jCell).getEdge();
		GraphConstants.setLineWidth(result, JAttr.RULE_EMPH_WIDTH.get(role(ruleEdge)));
		return result;
	}

    /** Adds the correct border emphasis. */
	@Override
	protected AttributeMap getJVertexEmphAttr(JVertex jCell) {
		AttributeMap result = super.getJVertexEmphAttr(jCell);
        AspectNode ruleNode = ((RuleJVertex) jCell).getNode();
		GraphConstants.setBorder(result, JAttr.RULE_EMPH_BORDER.get(role(ruleNode)));
		return result;
	}

	/**
     * Overwrites the method so as to return a rule vertex.
     * @require <tt>edge instanceof RuleGraph.RuleNode</tt>
     */
    @Override
    protected GraphJVertex createJVertex(Node node) {
        return new RuleJVertex(this, (AspectNode) node);
    }

    /**
     * Makes sure aspect information is included in the labels.
	 */
	@Override
	protected Collection<String> getLabels(JEdge jEdge) {
		// briefly set the show-aspects property to true
		boolean oldShowAspects = isShowAspects();
		setShowAspects(true);
		Collection<String> result = super.getLabels(jEdge);
		// reset the show-aspects property
		setShowAspects(oldShowAspects);
		return result;
	}

    /**
     * Makes sure aspect information is included in the labels.
	 */
	@Override
	protected Collection<String> getLabels(JVertex jCell) {
		// briefly set the show-aspects property to true
		boolean oldShowAspects = isShowAspects();
		setShowAspects(true);
		Collection<String> result = super.getLabels(jCell);
		// reset the show-aspects property
		setShowAspects(oldShowAspects);
		return result;
	}
    /**
     * The underlying production rule of this graph model.
     * @invariant rule != null
     */
    private final AspectualView view;
}