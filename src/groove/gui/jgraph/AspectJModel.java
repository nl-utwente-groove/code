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
 * $Id: AspectJModel.java,v 1.10 2007-05-14 18:51:57 rensink Exp $
 */
package groove.gui.jgraph;

import static groove.gui.jgraph.JAttr.RULE_BACKGROUND;
import static groove.gui.jgraph.JAttr.RULE_BORDER;
import static groove.gui.jgraph.JAttr.RULE_COLOR;
import static groove.gui.jgraph.JAttr.RULE_DASH;
import static groove.gui.jgraph.JAttr.RULE_WIDTH;
import static groove.view.aspect.RuleAspect.CREATOR;
import static groove.view.aspect.RuleAspect.EMBARGO;
import static groove.view.aspect.RuleAspect.ERASER;
import static groove.view.aspect.RuleAspect.READER;
import static groove.view.aspect.RuleAspect.REMARK;
import groove.graph.BinaryEdge;
import groove.graph.Edge;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.gui.Options;
import groove.rel.RegExprLabel;
import groove.trans.NameLabel;
import groove.util.Groove;
import groove.util.Pair;
import groove.view.AspectualView;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectElement;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectParser;
import groove.view.aspect.AspectValue;
import groove.view.aspect.AttributeAspect;
import groove.view.aspect.RuleAspect;

import java.awt.Color;
import java.awt.Rectangle;
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
 * @version $Revision: 1.10 $
 */
public class AspectJModel extends GraphJModel {

    // --------------------- INSTANCE DEFINITIONS ------------------------

    /** 
     * Creates a new aspect model instance on top of a given aspectual view.
     */
    public AspectJModel(AspectualView view, Options options) {
        super(view.getAspectGraph(), options);
        this.view = view;
    }

    /** 
     * Creates a new aspect model instance on top of a given aspectual view.
     */
    public AspectJModel(AspectualView view) {
    	this(view, new Options());
    }
    
    /** Constructor for a dummy model. */
    protected AspectJModel() {
    	view = null;
    }

    /** Specialises the return type. */
    @Override
	public AspectGraph graph() {
		return (AspectGraph) super.graph();
	}

    /** Returns the view on which this model is based. */
    public AspectualView<?> getView() {
    	return view;
    }
    
	/** 
	 * If <code>edge</code> is an AspectEdge, returns the super value;
	 * otherwise, assumes that it is a model edge, and returns the image
	 * of the corresponding view edge. 
	 */
	@Override
	public JCell getJCell(Edge edge) {
		if (edge instanceof AspectEdge) {
			return super.getJCell(edge);
		} else {
			return super.getJCell(getModelToViewMap().getEdge(edge));
		}
	}

	/** 
	 * If <code>node</code> is an AspectNode, returns the super value;
	 * otherwise, assumes that it is a model node, and returns the image
	 * of the corresponding view node. 
	 */
	@Override
	public GraphJVertex getJVertex(Node node) {
		if (node instanceof AspectNode) {
			return super.getJVertex(node);
		} else {
			return super.getJVertex(getModelToViewMap().getNode(node));
		}
	}

	/** Lazily computes and returns a mapping from model elements to view elements. */
	private NodeEdgeMap getModelToViewMap() {
		if (modelToViewMap == null) {
			modelToViewMap = new NodeEdgeHashMap();
			for (Map.Entry<Node,Node> nodeEntry: view.getMap().nodeMap().entrySet()) {
				modelToViewMap.putNode(nodeEntry.getValue(), nodeEntry.getKey());
			}
			for (Map.Entry<Edge,Edge> edgeEntry: view.getMap().edgeMap().entrySet()) {
				modelToViewMap.putEdge(edgeEntry.getValue(), edgeEntry.getKey());
			}
		}
		return modelToViewMap;
	}
	
	/** 
     * This implementation returns <code>false</code> if the node 
     * represented by the cell has a {@link RuleAspect#RULE_ASPECT_NAME} role.
     */
	@Override
	public boolean isMoveable(JCell jCell) {
		return jCell instanceof JEdge || isMoveable(((AspectJVertex) jCell).getNode());
	}

    /** 
     * Callback method to determine whether a certain node is moveable in
     * the GUI. Rule-identifying nodes are not moveable.
     */
	protected boolean isMoveable(AspectNode node) {
		return !(role(node) instanceof RuleAspect.RuleAspectValue);
	}

    /**
	 * Indicates whether aspect prefixes should be shown for nodes and edges.
	 */
	public final boolean isShowRemarks() {
		return getOptionValue(Options.SHOW_REMARKS_OPTION);
	}

	@Override
    protected AttributeMap createJVertexAttr(Node node) {
		AspectNode aspectNode = (AspectNode) node;
        AspectValue role = role(aspectNode);
        AttributeMap result = (AttributeMap) RULE_NODE_ATTR.get(role).clone();
        if (aspectNode.getValue(AttributeAspect.getInstance()) != null) {
        	result.applyMap(getJVertexValueAttr());
        }
        if (! isMoveable(aspectNode)) {
        	GraphConstants.setMoveable(result, false);
        	GraphConstants.setBounds(result, new Rectangle(0,0));
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
    
    /** Adds the correct line width emphasis. */
	@Override
	protected AttributeMap getJEdgeEmphAttr(JEdge jCell) {
		AttributeMap result = super.getJEdgeEmphAttr(jCell);
        AspectEdge ruleEdge = ((AspectJEdge) jCell).getEdge();
		GraphConstants.setLineWidth(result, JAttr.RULE_EMPH_WIDTH.get(role(ruleEdge)));
		return result;
	}

    /** Adds the correct border emphasis. */
	@Override
	protected AttributeMap getJVertexEmphAttr(JVertex jCell) {
		AttributeMap result = super.getJVertexEmphAttr(jCell);
        AspectNode ruleNode = ((AspectJVertex) jCell).getNode();
		GraphConstants.setBorder(result, JAttr.RULE_EMPH_BORDER.get(role(ruleNode)));
		return result;
	}

	/**
     * Overwrites the method so as to return a rule vertex.
     * @require <tt>edge instanceof RuleGraph.RuleNode</tt>
     */
    @Override
    protected GraphJVertex createJVertex(Node node) {
        return new AspectJVertex(this, (AspectNode) node);
    }
    
    /**
	 * Overwrites the method so as to return a rule edge.
	 * @require <tt>edge instanceof RuleGraph.RuleEdge</tt>
	 */
	@Override
	protected GraphJEdge createJEdge(BinaryEdge edge) {
	    return new AspectJEdge((AspectEdge) edge);
	}
	
    /**
     * The underlying view of this graph model.
     */
    private final AspectualView<?> view;
    /** Mapping from the elements of the model to those of the view. */
    private NodeEdgeMap modelToViewMap;
    
    /** Helper method to return the rule aspect value of an aspect node. */
	static private AspectValue role(AspectElement node) {
		return node.getValue(RuleAspect.getInstance());
	}

    /** Empty instance of the {@link AspectJModel}. */
    static public final AspectJModel EMPTY_JMODEL = new AspectJModel();

    /** Collection of attributes for rule nodes. */
    static private final Map<AspectValue,AttributeMap> RULE_NODE_ATTR = new HashMap<AspectValue,AttributeMap>();
    /** Collection of attributes for rule edges. */
    static private final Map<AspectValue,AttributeMap> RULE_EDGE_ATTR = new HashMap<AspectValue,AttributeMap>();

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
            GraphConstants.setLineWidth(nodeAttr, RULE_WIDTH.get(role));
            Color background = RULE_BACKGROUND.get(role);
            if (background != null) {
            	GraphConstants.setBackground(nodeAttr, background);
            }
            RULE_NODE_ATTR.put(role,nodeAttr);
        }
//        GraphConstants.setSelectable(RULE_EDGE_ATTR.get(RULE), false);
//        GraphConstants.setSelectable(RULE_NODE_ATTR.get(RULE), false);
    }

    /** Role names (for the tool tips). */
    static private final Map<AspectValue,String> ROLE_NAMES = new HashMap<AspectValue,String>(); 
    /** Role descriptions (for the tool tips). */
	static private final Map<AspectValue,String> ROLE_DESCRIPTIONS = new HashMap<AspectValue,String>(); 
    
    static {
        ROLE_NAMES.put(EMBARGO,"Embargo");
        ROLE_NAMES.put(READER,"Reader");
        ROLE_NAMES.put(CREATOR,"Creator");
        ROLE_NAMES.put(ERASER,"Eraser");
        ROLE_NAMES.put(REMARK,"Remark");
//        ROLE_NAMES.put(RULE,"Rule");
        
        ROLE_DESCRIPTIONS.put(EMBARGO,"Must be absent from a graph for this rule to apply");
        ROLE_DESCRIPTIONS.put(READER, "Must be present in a graph for this rule to apply");
        ROLE_DESCRIPTIONS.put(CREATOR,"Will be created by applying this rule");
        ROLE_DESCRIPTIONS.put(ERASER,"Will be deleted by applying this rule");
        ROLE_DESCRIPTIONS.put(REMARK,"Has no effect on the execution of the rule");
//        ROLE_DESCRIPTIONS.put(RULE,"Has no effect on the execution of the rule");
    }

    /**
     * Specialized j-vertex for rule graphs, with its own tool tip text.
     */
    public class AspectJVertex extends GraphJVertex {
    	/** Creates a j-vertex on the basis of a given (aspectual) node. */
        public AspectJVertex(AspectJModel jModel, AspectNode node) {
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
         * Takes care of rule node display.
         */
        @Override
		public String getHtmlText() {
    		Pair<NameLabel,Integer> ruleContent = RuleAspect.getRuleContent(getNode());
        	if (ruleContent != null) {
        		String name = ruleContent.first().name();
        		int priority = ruleContent.second();
        		return "Rule "+name + (priority >= 0 ? ", priority "+priority : "");
        	} else {
        		return super.getHtmlText();
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
		 * view and uses that one's identity.
		 */
		@Override
		protected String getNodeIdentity() {
			Node ruleNode = view.getMap().getNode(getNode());
			return ruleNode == null ? null : ruleNode.toString();
		}

		/** 
		 * This implementation makes remark edges invisible as demanded 
		 * by the {@link Options#SHOW_REMARKS_OPTION}.
		 */
		@Override
		public boolean isVisible() {
			return isShowRemarks() || ! RuleAspect.isRemark(getNode());
		}

		/** 
		 * This implementation only returns <code>true</code> only if
		 * {@link RuleAspect#inRule(AspectElement)} holds for the underlying node.
		 */
		@Override
		public boolean isListable() {
			return RuleAspect.inRule(getNode());
		}

		/** The role of the underlying rule node. */
        private final AspectValue role;
    }

    /**
     * Specialized j-edge for rule graphs, with its own tool tip text.
     */
    public class AspectJEdge extends GraphJEdge {
    	/** Creates a j-edge on the basis of a given (aspectual) edge. */
        public AspectJEdge(AspectEdge edge) {
            super(edge);
            this.role = role(edge);
        }

        /** Specialises the return type. */
        @Override
		public AspectEdge getEdge() {
			return (AspectEdge) super.getEdge();
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
        public boolean addEdge(BinaryEdge edge) {
            if (role((AspectEdge) edge) == role) {
                return super.addEdge(edge);
            } else {
                return false;
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
		 * This implementation retrieves the source node's image in the
		 * model and uses that one's identity.
		 */
		@Override
		protected String getSourceIdentity() {
			return view.getMap().getNode(getSourceNode()).toString();
		}

		/**
		 * This implementation retrieves the target node's image in the
		 * model and uses that one's identity.
		 */
		@Override
		protected String getTargetIdentity() {
			return view.getMap().getNode(getTargetNode()).toString();
		}

		/** 
		 * This implementation makes remark edges invisible as demanded 
		 * by the {@link Options#SHOW_REMARKS_OPTION}.
		 */
		@Override
		public boolean isVisible() {
			return isShowRemarks() || role(getEdge()) != RuleAspect.REMARK;
		}

		/** 
		 * This implementation only returns <code>true</code> only if
		 * {@link RuleAspect#inRule(AspectElement)} holds for the underlying edge.
		 */
		@Override
		public boolean isListable() {
			return RuleAspect.inRule(getEdge());
		}
		
		private final AspectValue role;
    }
}