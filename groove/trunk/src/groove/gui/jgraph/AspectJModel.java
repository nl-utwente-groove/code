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
 * $Id: AspectJModel.java,v 1.26 2007-10-10 08:59:51 rensink Exp $
 */
package groove.gui.jgraph;

import static groove.gui.jgraph.JAttr.NESTING_EDGE_ATTR;
import static groove.gui.jgraph.JAttr.NESTING_NODE_ATTR;
import static groove.gui.jgraph.JAttr.RULE_EDGE_ATTR;
import static groove.gui.jgraph.JAttr.RULE_EDGE_EMPH_CHANGE;
import static groove.gui.jgraph.JAttr.RULE_NODE_ATTR;
import static groove.gui.jgraph.JAttr.RULE_NODE_EMPH_CHANGE;
import static groove.view.aspect.NestingAspect.getNestingValue;
import static groove.view.aspect.RuleAspect.CREATOR;
import static groove.view.aspect.RuleAspect.EMBARGO;
import static groove.view.aspect.RuleAspect.ERASER;
import static groove.view.aspect.RuleAspect.READER;
import static groove.view.aspect.RuleAspect.REMARK;
import static groove.view.aspect.RuleAspect.getRuleValue;
import static groove.view.aspect.AttributeAspect.getAttributeValue;
import groove.graph.BinaryEdge;
import groove.graph.Edge;
import groove.graph.GraphInfo;
import groove.graph.Node;
import groove.graph.NodeEdgeHashMap;
import groove.graph.NodeEdgeMap;
import groove.gui.Options;
import groove.rel.RegExprLabel;
import groove.util.Converter;
import groove.util.Groove;
import groove.view.AspectualView;
import groove.view.LabelParser;
import groove.view.aspect.AspectEdge;
import groove.view.aspect.AspectElement;
import groove.view.aspect.AspectGraph;
import groove.view.aspect.AspectNode;
import groove.view.aspect.AspectParser;
import groove.view.aspect.AspectValue;
import groove.view.aspect.NestingAspect;
import groove.view.aspect.NestingAspectValue;
import groove.view.aspect.RuleAspect;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

/**
 * Implements jgraph's GraphModel interface on top of an {@link AspectualView}.
 * This is used to visualise rules and attributed graphs.
 * @author Arend Rensink
 * @version $Revision: 1.26 $
 */
public class AspectJModel extends GraphJModel {

    // --------------------- INSTANCE DEFINITIONS ------------------------

    /** 
     * Creates a new aspect model instance on top of a given aspectual view.
     */
    AspectJModel(AspectualView<?> view, Options options) {
        super(view.getAspectGraph(), options);
        this.view = view;
    }
    
    /** Constructor for a dummy model. */
    AspectJModel() {
    	view = null;
    }

	/** Specialises the return type. */
    @Override
	public AspectGraph getGraph() {
		return (AspectGraph) super.getGraph();
	}

    /** 
     * This implementation retrieves the role from the graph itself.
     */
    @Override
    public String getRole() {
    	return GraphInfo.getRole(getGraph());
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
        AttributeMap result;
		AspectNode aspectNode = (AspectNode) node;
		if (getNestingValue(aspectNode) != null) {
			result = NESTING_NODE_ATTR.clone();
		} else {
			AspectValue role = role(aspectNode);
			result = RULE_NODE_ATTR.get(role).clone();
			if (getAttributeValue(aspectNode) != null) {
				result.applyMap(getJVertexDataAttr());
			}
		}
        if (! isMoveable(aspectNode)) {
        	GraphConstants.setMoveable(result, false);
        	GraphConstants.setBounds(result, new Rectangle(0,0));
        }
        return result;
    }

    @Override
    protected AttributeMap createJEdgeAttr(Set<? extends Edge> edgeSet) {
        AttributeMap result;
        assert !edgeSet.isEmpty() : String.format("Underlying edge set should not be empty");
        AspectEdge aspectEdge = (AspectEdge) edgeSet.iterator().next();
        AspectValue nestingValue = getNestingValue(aspectEdge);
        if (nestingValue != null && !nestingValue.isNodeValue()) {
        	result = NESTING_EDGE_ATTR.clone();
        } else {
			AspectValue role = role(aspectEdge);
			result = RULE_EDGE_ATTR.get(role).clone();
			if (RegExprLabel.isEmpty(aspectEdge.label())) {
				// remove edge arrow
				GraphConstants.setLineEnd(result, GraphConstants.ARROW_NONE);
			}
        }
        return result;
    }
    
    /** Adds the correct line width emphasis. */
	@Override
	protected AttributeMap getJEdgeEmphAttr(JEdge jCell) {
        AspectEdge ruleEdge = ((AspectJEdge) jCell).getEdge();
		return RULE_EDGE_EMPH_CHANGE.get(role(ruleEdge));
	}

    /** Adds the correct border emphasis. */
	@Override
	protected AttributeMap getJVertexEmphAttr(JVertex jCell) {
        AspectNode ruleNode = ((AspectJVertex) jCell).getNode();
		return RULE_NODE_EMPH_CHANGE.get(role(ruleNode));
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

	/** Retrieves a node's image according to the view. */
	Node getModelNode(AspectNode node) {
		NodeEdgeMap viewMap = view.getMap();
		return viewMap == null ? null : viewMap.getNode(node);
	}

	/** Retrieves an edge's image according to the view. */
	Edge getModelEdge(AspectEdge edge) {
		NodeEdgeMap viewMap = view.getMap();
		return viewMap == null ? null : viewMap.getEdge(edge);
	}

    /**
     * The underlying view of this graph model.
     */
    private final AspectualView<?> view;
    /** Mapping from the elements of the model to those of the view. */
    private NodeEdgeMap modelToViewMap;
    
    /** 
     * Creates a new aspect model instance on top of a given aspectual view.
     * Returns {@link #EMPTY_ASPECT_JMODEL} if the view is <code>null</code>.
     */
    static public AspectJModel newInstance(AspectualView<?> view, Options options) {
    	if (view == null) {
    		return EMPTY_ASPECT_JMODEL;
    	} else {
			AspectJModel result = new AspectJModel(view, options);
			result.reload();
			return result;
		}
    }
    
    /** Helper method to return the rule aspect value of an aspect node. */
	static AspectValue role(AspectElement node) {
		return getRuleValue(node);
	}

    /** Empty instance of the {@link AspectJModel}. */
    static public final AspectJModel EMPTY_ASPECT_JMODEL = new AspectJModel();

    /** Role names (for the tool tips). */
    static final Map<AspectValue,String> ROLE_NAMES = new HashMap<AspectValue,String>(); 
    /** Role descriptions (for the tool tips). */
	static final Map<AspectValue,String> ROLE_DESCRIPTIONS = new HashMap<AspectValue,String>(); 
    
    static {
        ROLE_NAMES.put(EMBARGO,"Embargo");
        ROLE_NAMES.put(READER,"Reader");
        ROLE_NAMES.put(CREATOR,"Creator");
        ROLE_NAMES.put(ERASER,"Eraser");
        ROLE_NAMES.put(REMARK,"Remark");
//        ROLE_NAMES.put(RULE,"Rule");
        
        ROLE_DESCRIPTIONS.put(EMBARGO,"Must be absent from a graph for this rule to apply");
        ROLE_DESCRIPTIONS.put(READER, "Must be matched for this rule to apply");
        ROLE_DESCRIPTIONS.put(CREATOR,"Will be created by applying this rule");
        ROLE_DESCRIPTIONS.put(ERASER,"Will be deleted by applying this rule");
        ROLE_DESCRIPTIONS.put(REMARK,"Has no effect on the execution of the rule");
//        ROLE_DESCRIPTIONS.put(RULE,"Has no effect on the execution of the rule");
    }

    /**
     * Specialized j-vertex for rule graphs, with its own tool tip text.
     */
    private class AspectJVertex extends GraphJVertex {
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
         * This implementation return's the model node of {@link #getNode()}.
         * @see #getModelNode(AspectNode)
         */
		@Override
		Node getActualNode() {
			return getModelNode(getNode());
		}
        
        /** 
         * This implementation prefixes the node description with
         * an indication of the role, if the model is a rule.
         */
        @Override
		StringBuilder getNodeDescription() {
            StringBuilder res = super.getNodeDescription();
            if (getNode().getDeclaredValues().contains(role)) {
            	Converter.toUppercase(res, false);
            	res.insert(0, " ");
            	res.insert(0, ROLE_NAMES.get(role));
                if (getNode().getDeclaredValues().contains(role)) {
                	res.append("<br>"+ROLE_DESCRIPTIONS.get(role));
                }
            }
            return res;
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
        
        /** Adds a quantifier, if the nesting aspect justifies this. */
		@Override
		public List<StringBuilder> getLines() {
			List<StringBuilder> result = super.getLines();
			AspectValue nesting = getNestingValue(getNode());
			if (nesting != null) {
				result.add(0, getQuantifierLine((NestingAspectValue) nesting));
			}
			return result;
		}

		/** Returns an HTML-formatted line describing a given quantifier value. */
		private StringBuilder getQuantifierLine(NestingAspectValue nesting) {
			StringBuilder result = new StringBuilder();
			if (NestingAspect.FORALL.equals(nesting)) {
				result.append(Converter.HTML_FORALL);
			} else {
				assert NestingAspect.EXISTS.equals(nesting);
				result.append(Converter.HTML_EXISTS);
			}
			String level = nesting.getContent();
			if (level.length() != 0) {
				result.append(Converter.SUPER_TAG.on(level));
			}
			return result;
		}
		
		/**
		 * On demand prefixes the label with the edge's aspect values.
		 */
		@Override
		public StringBuilder getLine(Edge object) {
			assert object instanceof AspectEdge;
			StringBuilder result = super.getLine(object);
			if (isShowAspects()) {
				result = AspectParser.toString(((AspectEdge) object).getDeclaredValues(), result);
			}
			return result;
		}
//
//		/**
//		 * On demand adds the node aspects to the label set.
//		 */
//		@Override
//		public Collection<String> getListLabels() {
//			if (isShowAspects()) {
//				Collection<String> result = new ArrayList<String>();
//				for (AspectValue value : getNode().getDeclaredValues()) {
//					result.add(AspectParser.toString(value));
//				}
//				result.addAll(super.getListLabels());
//				return result;
//			} else {
//				return super.getListLabels();
//			}
//		}

	    /**
		 * This implementation adds node and edge aspects.
		 */
		@Override
		public Collection<String> getPlainLabels() {
			Collection<String> result = new ArrayList<String>();
			for (AspectValue value : getNode().getDeclaredValues()) {
				result.add(AspectParser.toString(value));
			}
            // we do not do a super call, for that adds the value of the actual node
            // which we have here anyway
            for (Edge edge : getSelfEdges()) {
                result.add(getPlainLabel(edge));
            }
//			result.addAll(super.getPlainLabels());
			return result;
		}
        
        /**
         * This implementation adds an edge aspect prefix.
         */
        @Override
        public String getPlainLabel(Edge edge) {
            StringBuilder text = new StringBuilder(super.getPlainLabel(edge));
            return AspectParser.toString(((AspectEdge) edge).getDeclaredValues(), text).toString();
        }

        @Override
        LabelParser createLabelParser() {
            return AspectParser.getLabelParser(getNode().getAspectMap().values());
        }

		/** 
		 * This implementation makes remark edges invisible as demanded 
		 * by the {@link Options#SHOW_REMARKS_OPTION}.
		 */
		@Override
		public boolean isVisible() {
			if (RuleAspect.isRemark(getNode())) {
				return isShowRemarks();
			} else {
				return super.isVisible();
			}
		}

		/** 
		 * This implementation returns <code>true</code> only if
		 * {@link RuleAspect#inRule(AspectElement)} holds for the underlying node.
		 */
		@Override
		public boolean isListable() {
			return super.isListable() && RuleAspect.inRule(getNode());
		}

		/** The role of the underlying rule node. */
        private final AspectValue role;
    }

    /**
     * Specialized j-edge for rule graphs, with its own tool tip text.
     */
    private class AspectJEdge extends GraphJEdge {
    	/** Creates a j-edge on the basis of a given (aspectual) edge. */
        public AspectJEdge(AspectEdge edge) {
            super(AspectJModel.this, edge);
            this.role = role(edge);
        }

        @Override
		StringBuilder getEdgeDescription() {
			StringBuilder result = super.getEdgeDescription();
			if (getEdge().getDeclaredValues().contains(role)) {
				result.append("<br>"+ROLE_DESCRIPTIONS.get(role));
			}
			return result;
		}

        @Override
		StringBuilder getEdgeKindDescription() {
			StringBuilder result = super.getEdgeKindDescription();
			if (Groove.RULE_ROLE.equals(getRole())) {
				Converter.toUppercase(result, false);
				result.insert(0, " ");
            	result.insert(0, ROLE_NAMES.get(role));
			}
			return result;
		}

		/** Specialises the return type. */
        @Override
		public AspectEdge getEdge() {
			return (AspectEdge) super.getEdge();
		}
        
        @Override
		Edge getActualEdge() {
			return getModelEdge(getEdge());
		}

		/**
         * Returns <tt>true</tt> only if the aspect values of the edge to be added
         * equal those of this j-edge, and the superclass is also willing.
         * @require <tt>edge instanceof RuleGraph.RuleEdge</tt>
         */
        @Override
        public boolean addEdge(BinaryEdge edge) {
            if (((AspectEdge) edge).getAspectMap().equals(getEdge().getAspectMap())) {
                return super.addEdge(edge);
            } else {
                return false;
            }
        }
        
		/**
		 * On demand prefixes the label with the edge's aspect values.
		 */
		@Override
		public StringBuilder getLine(Edge object) {
			assert object instanceof AspectEdge;
			StringBuilder result = super.getLine(object);
			if (isShowAspects()) {
				result = AspectParser.toString(((AspectEdge) object).getDeclaredValues(), result);
			}
			return result;
		}
	    /**
		 * This implementation adds node and edge aspects.
		 */
		@Override
		public String getPlainLabel(Edge edge) {
		    StringBuilder text = new StringBuilder(super.getPlainLabel(edge));
			return AspectParser.toString(((AspectEdge) edge).getDeclaredValues(), text).toString();
		}
        
		@Override
        LabelParser createLabelParser() {
            return AspectParser.getLabelParser(getEdge().getAspectMap().values());
        }

		/** 
		 * This implementation makes remark edges invisible as demanded 
		 * by the {@link Options#SHOW_REMARKS_OPTION}.
		 */
		@Override
		public boolean isVisible() {
			return super.isVisible() && (isShowRemarks() || role(getEdge()) != RuleAspect.REMARK);
		}

		/** 
		 * This implementation returns <code>true</code> only if
		 * {@link RuleAspect#inRule(AspectElement)} holds for the underlying edge.
		 */
		@Override
		public boolean isListable() {
			return super.isListable() && RuleAspect.inRule(getEdge());
		}
		
		private final AspectValue role;
    }
}