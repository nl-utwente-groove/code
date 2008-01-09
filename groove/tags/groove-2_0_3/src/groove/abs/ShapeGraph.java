package groove.abs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import groove.graph.AdjacencyMapGraph;
import groove.graph.DefaultGraph;
import groove.graph.DefaultNode;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.util.TreeHashSet;

/** A pattern shape graph is a graph together with types (graph patterns) and multiplicities 
 * associated to its nodes.
 * @author io
 *
 */
public class ShapeGraph extends DefaultGraph {
	
	/** The multiplicity of n in the shape
	 * @param n
	 * @return the multiplicity of n
	 * @require n is a node of this shape
	 */
	public MultiplicityInformation multiplicityOf (Node n) {
		assert this.containsElement(n) : "Node not is this shape";
		return this.type.get(n).getMult();
	}
	
	/** The type (graph pattern) of a node.
	 * @param n
	 * @return the type of n
	 * @require n is a node of this shape
	 */
	public GraphPattern typeOf (Node n) {
		assert this.containsElement(n) : "Node not is this shape";
		return this.type.get(n).getPattern();
	}
	
	/** Creates a factory object with specified family and precision.
	 * @param family
	 * @param precision
	 * @return a factory object
	 */
	public static ShapeGraph.Factory factory(PatternFamily family, int precision) {
		return (new ShapeGraph()).new Factory(family, precision);
	}
	
	/** Checks whether this PatternShapeGraph represents a unique shape (i.e. all multiplicities are precise)
	 * @return true if all multiplicities are precise
	 */
	public boolean isPrecise () {
		for (NodeType c : this.type.values()) {
			if (! Abstraction.MULTIPLICITY.isPrecise(c.getMult())) {
				return false;
			}
		}
		return true;
	}
	
	/** Checks whether this PatternShapeGraph represents a normalised shape (i.e. a unique node per type)
	 * @return true if each PatternGraph present as type of a node of this shape is the node of a unique shape
	 */
	public boolean isNormalised () {
		Set<GraphPattern> setOfTypes = new TreeHashSet<GraphPattern>();
		for (Node n : this.nodeSet()) {
			GraphPattern type = this.typeOf(n);
			if (setOfTypes.contains(type)) {
				return false;
			}
			setOfTypes.add(type);
		}
		return true;
	}
	
	// ----------------------------------------------------------------------------------
	// SUBTYPES
	// ----------------------------------------------------------------------------------
	
	
	/** A factory for PatternShapeGraph objects. 
	 * Such a factory is the unique way for obtaining PatternShapeGraph objects.
	 * @author io
	 *
	 */
	public class Factory {
		/** The Pattern family that defines types for nodes in this shape graph. */
		private final PatternFamily family;
		/** The precision of multiplicity information. */
		private final int precision;
		
		/** Creates a factory for PatternShapeGraph objects
		 * with common PatternFamily and precision.
		 * @param family
		 * @param precision
		 */
		public Factory(PatternFamily family, int precision) {
			super();
			this.family = family;
			this.precision = precision;
		}
				
		/**
		 * @return the family of this factory
		 */
		public PatternFamily getFamily() {
			return family;
		}

		/**
		 * @return the precision of the factory
		 */
		public int getPrecision() {
			return precision;
		}

		/** Computes the abstract graph for g. All computed patterns are added to the family of this factory.
		 * @param graph
		 * @return The computed shape graph
		 * @throws ExceptionIncompatibleWithMaxIncidence
		 */
		public ShapeGraph getShapeGraphFor (Graph graph) throws ExceptionIncompatibleWithMaxIncidence {
			
			// TODO the initial capacity may probably be optimised		
			ShapeGraph result = new ShapeGraph (this.family, this.precision);
			result.type = new HashMap<Node,NodeType>(graph.nodeCount());
			
			// Stores the pattern computed for each node of the parameter graph
			Map<Node,GraphPattern> computedPattern = new HashMap<Node,GraphPattern>(graph.nodeCount());
			// Stores the correspondence between the computed patterns and the corresponding node in the result shape
			// (inverse to the typeMult map when multiplicities are abstracted)
			// Should be added an entry at the same time as to result.typeMult
			Map<GraphPattern,Node> inverseMap = new HashMap<GraphPattern,Node>(graph.nodeCount());
			
			// for each node of the graph, comute the corresponding pattern,
			// add a new node to the resulting graph if necessary, and update the typeMult map
			for (Node currNode : graph.nodeSet()) {
				GraphPattern pattern = this.family.computeAddPattern(graph, currNode);
				computedPattern.put(currNode, pattern);
				
				Node nodeInResult = inverseMap.get(pattern);
				if (nodeInResult != null) {
					NodeType type = result.type.get(nodeInResult);
					type.setMult(Abstraction.MULTIPLICITY.add(type.getMult(), 1));
				}
				else {
					nodeInResult = DefaultNode.createNode();
					result.addNode(nodeInResult);
					result.type.put(nodeInResult, new NodeType(pattern, Abstraction.MULTIPLICITY.getElement(1,this.precision)));
					result.type.put(nodeInResult, new NodeType(pattern, Abstraction.MULTIPLICITY.getElement(1,this.precision)));
					inverseMap.put(pattern, nodeInResult);
				}
			}			
			
			// add the edges
			for (Edge e : graph.edgeSet()) {
				Node srcPattern = inverseMap.get(computedPattern.get(e.end(0)));
				Node tgtPattern = inverseMap.get(computedPattern.get(e.end(1)));
				result.addEdge(srcPattern, e.label(), tgtPattern);
			}
			
			result.setFixed();
			result.checkInvariants();
			return result;
		}
		
	}
	
	
	/** Debugging method. TODO
	 * @param pattern
	 * @return the node of this graph corresponding to a graph pattern
	 */
	public Node getNodeFor (GraphPattern pattern) {
		for (Map.Entry<Node, NodeType> entry : this.type.entrySet()) {
			if (entry.getValue().getPattern() == pattern) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	
	// ----------------------------------------------------------------------------------
	// FIELDS, CONSTRUCTORS AND STANDARD METHODS
	// ----------------------------------------------------------------------------------
	
	/** The Pattern family that defines types for nodes in this shape graph. */
	protected final PatternFamily family;
	/** The precision of multiplicity information. */
	protected final int precision;
	/** Associates a pattern and a multiplicity with each node in the graph (the couple is called type). 
	 * @invariant (A) nodeSet() is equal to typeMult.keySet()
	 * @invariant (B) for all node of this graph, multiplicityOf(node) is a multiplicity with precision this.precision
	 * @invariant (C) for all node of this graph, typeOf(node) is issued by this.family
 	 */
	protected Map<Node,NodeType> type; 

	/** Private constructor.
	 * Used by the factory. 
	 * @param family
	 * @param precision
	 */
	private ShapeGraph (PatternFamily family, int precision) {
		super();
		this.family = family;
		this.precision = precision;
	}
	
	/** Constructor to be used by the extending classes.
	 * Clones the type map.
	 * @param sg
	 */
	protected ShapeGraph (ShapeGraph sg) {
		super(sg);
		this.family = sg.family;
		this.precision = sg.precision;
		this.type = new HashMap<Node, NodeType>(sg.type);
	}
	
	/** Private constructor to be used only for creating a factory. */
	private ShapeGraph() {
		this.family = null;
		this.precision = 0;
	}

	/**
	 * @return the family of this shape
	 */
	public PatternFamily family() {
		return family;
	}
	
	/**
	 * @return the precision of this shape
	 */
	public int precision() {
		return precision;
	}
	
	@Override
	public String toString () {
		String result = super.toString();
		result += " Types: " + this.type + ";";
		return result;
	}
	
	
	/** A string representation of the underlying graph. 
	 * @return
	 */
	protected String graphToString () {
		return super.toString();
	}
	
	/** 
	 * @return A shallow copy of the type map of this PatternShapeGraph.
	 * Keys and values in the map are not copied, only the map is.
	 */
	Map<Node,NodeType> typeMapClone () {
		return new HashMap<Node,NodeType>(this.type);
	}
	
	// ////////////////////////////////////////////////////////
	// Checking invariants
	// ////////////////////////////////////////////////////////
	
	// TODO
	/*
	 * @invariant (A) nodeSet() is equal to typeMult.keySet()
	 * @invariant (B) for all node of this graph, multiplicityOf(node) is a multiplicity with precision this.precision
	 * @invariant (C) for all node of this graph, typeOf(node) is issued by this.family
*/
	private void checkInvariants () {
		if (! Util.ea()) { return; }
		checkInvA(); checkInvB(); checkInvC();
	}
	
	/** */
	private void checkInvA() {
		assert this.nodeSet().equals(this.type.keySet()) : "Invariant A failed";
	}
	
	/** */
	private void checkInvB() {
		for (Node n : this.nodeSet()) {
			assert Abstraction.MULTIPLICITY.getPrecision(this.multiplicityOf(n)) == this.precision() : "Invariant B failed";
		}
	}

	/** */
	private void checkInvC() {
		for (Node n : this.nodeSet()) {
			assert this.family().issued(this.typeOf(n)) : "Invariant C failed";
		}
	}
	
	
}
