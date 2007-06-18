package groove.control;

import groove.graph.Edge;
import groove.graph.Element;
import groove.graph.Label;
import groove.graph.Node;
import groove.graph.NodeEdgeMap;
import groove.lts.GraphState;
import groove.lts.Transition;
import groove.trans.NameLabel;

public class LocationTransition implements Transition {

	private NameLabel label;
	
	
	private GraphState source;
	private GraphState target;
	
	public LocationTransition(GraphState source, GraphState target)
	{
		this.source = source;
		this.target = target; 
		this.label = new NameLabel("_");
	}
	
	public GraphState source() {
		// TODO Auto-generated method stub
		return source;
	}

	public GraphState target() {
		// TODO Auto-generated method stub
		return target;
	}

	public int compareTo(Element obj) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Node end(int i) {
		// TODO Auto-generated method stub
		return target;
	}

	public int endCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	public int endIndex(Node node) {
		// TODO Auto-generated method stub
		return 1;
	}

	public Node[] ends() {
		// TODO Auto-generated method stub
		return new Node[]{target};
	}

	public boolean hasEnd(Node node) {
		// TODO Auto-generated method stub
		return (node == target);
	}

	public Edge imageFor(NodeEdgeMap elementMap) {
		// TODO Auto-generated method stub
		return null;
	}

	public Label label() {
		// TODO Auto-generated method stub
		return label;
	}

	public Node opposite() {
		// TODO Auto-generated method stub
		return target;
	}

}
