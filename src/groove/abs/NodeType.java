package groove.abs;

/**
 * Represents the type of a node of an abstract graph, which is a couple of a GraphPattern and 
 * a MultiplicityInformation.
 * @author Iovka Boneva
 *
 */
public class NodeType {

	/** */
	private GraphPattern pattern;
	/** */
	private MultiplicityInformation mult;
	
	/**
	 * @param pattern
	 * @param mult
	 */
	public NodeType(GraphPattern pattern, MultiplicityInformation mult) {
		super();
		this.pattern = pattern;
		this.mult = mult;
	}
	/**
	 * @return the pattern component
	 */
	public GraphPattern getPattern() {
		return pattern;
	}
	/**  
	 * @param pattern
	 */
	public void setPattern(GraphPattern pattern) {
		this.pattern = pattern;
	}
	
	/**
	 * @return The multiplicity component
	 */
	public MultiplicityInformation getMult() {
		return mult;
	}
	
	/**
	 * @param mult
	 */
	public void setMult(MultiplicityInformation mult) {
		this.mult = mult;
	}
	
	@Override
	public String toString() {
		return "(" + this.mult + ", " + this.pattern.edgeSet() + ")";
	}
	
}
