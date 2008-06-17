package groove.abs;


/** Class containing global constants for the abstractions. 
 * @author Iovka Boneva
 * */
public class Abstraction {
	
	/** Defines different relations over {@link AbstrGraph}s. 
	 * The exact semantics of these relations is determined by the value of #
	 * */
	public static enum AbstrGraphsRelation {
		/** {@value #QUASI}(G1,G2) if there exists an isomorphism between G1 and G2 that preserves typing.
		 * This is a symmetric relation. */
		QUASI,
		/** {@value #SUPER}(G1,G2) if {@value #QUASI}(G1,G2) with isomorhism h, 
		 * and for all node n in G1 it holds MultInfoRelation.M_CONTAINS(G1.multiplicityOf(n), G2.multiplicityOf(h(n))) 
		 */
		SUPER,
		/** {@value #SUB}(G1,G2) if {@link #SUPER}(G2,G1) */
		SUB,
		/** {@value #EQUAL}(G1,G2) if {@value #SUB}(G1,G2) and {@value #SUPER}(G2,G1).
		 * In other words, EQUL(G1,G2) if there exists an isomorphism between G1 and G2
		 * that preserves typing and multiplicities.
		 * This is a symmetric relation.
		 */
		EQUAL, 
		/** {@value #NOTEQUAL}(G1,G2) if none of the other relations holds. */
		NOTEQUAL
	}
	
	/** Defines different relations over {@link MultiplicityInformation}s. 
	 * All the relations except for {@value #M_NOTEQUAL} are only defined for
	 * {@link MultiplicityInformation}s of same multiplicity sets.
	 */
	public static enum MultInfoRelation {
		/**  {@value #M_BELONGS}(m1,m2) if m1 is precise, m2 represents a set, and m1 belongs to m2 */
		M_BELONGS,
		/** {@value #M_CONTAINS}(m1,m2) if {@value #M_BELONGS}(m2,m1). */
		M_CONTAINS,
		/** {@value #M_SUBSET}(m1,m2) if m1 and m1 are sets, and m1 is a subset of m2 */
		M_SUBSET,
		/** {@value #M_SUPERSET}(m1,m2) if {@value #M_SUBSET}(m2,m1) */
		M_SUPERSET,
		/** {@value #M_EQUAL}(m1,m2) if m1 == m2 */
		M_EQUAL, 
		/** {@value #M_NOTEQUAL}(m1,m2) if none of the other relations holds. */
		M_NOTEQUAL
	}	
	
	/** The multiplicity factory to be used */
	public static final Multiplicity MULTIPLICITY = new MultiplicityImpl();
	
	/** A global constant. The maximal allowed precision. */
	public static final int MAX_ALLOWED_PRECISION = 5;

	/** To be unset after debugging phase */
	public static boolean DEBUG = true;

	/** */
	public enum LinkPrecision {
		/** */
		HIGH, 
		/** */
		LOW
	}
	
	/** The options for abstracting and abstract simulation. */
	public static class Parameters {
		
		/** Option for symmetry reduction.
		 * TODO explain what symmetry reduction is
		 * TODO should be a parameter of the abstract simulation
		 * Symmetry reduction is costly, but may significantly accelerate if there is lots of symmetry.
		 */
		public final boolean SYMMETRY_REDUCTION;		

		/** */
		public final LinkPrecision LINK_PRECISION; 
		
		/** Determines the semantics of the AbstrGraphsRelation.SUB and AbstrGraphsRelation.SUPER relations.
		 * If <code>true</code>, then {@link MultInfoRelation}.M_BELONGS for multiplicities of nodes 
		 * does not break the AbstrGraphsRelation.SUB relation between the corresponding graphs.
		 */
		public final boolean BELONGS_IS_SUB; 
		
		/** */
		public Parameters (boolean symmetry_reduction,
					 	LinkPrecision link_precision,
					 	boolean belongs_is_sub, 
					 	int radius,
					 	int precision,
					 	int maxIncidence) 
		{
			this.SYMMETRY_REDUCTION = symmetry_reduction;
			this.LINK_PRECISION = link_precision;
			this.BELONGS_IS_SUB = belongs_is_sub;
			this.radius = radius;
			this.precision = precision;
			this.maxIncidence = maxIncidence;
		}
		
		/** Default constructor, giving default values for the options. */
		public Parameters (int radius, int precision, int maxIncidence) {
			this.SYMMETRY_REDUCTION = false;
			this.LINK_PRECISION = LinkPrecision.HIGH;
			this.BELONGS_IS_SUB = false;
			this.radius = radius;
			this.precision = precision;
			this.maxIncidence = maxIncidence;
		}
		
		/** Default values for the options, except for SYMMETRY_REDUCTION */
		public Parameters (boolean symmetryReduction, int radius, int precision, int maxIncidence) {
			this.SYMMETRY_REDUCTION = symmetryReduction;
			this.LINK_PRECISION = LinkPrecision.HIGH;
			this.BELONGS_IS_SUB = false;
			this.radius = radius;
			this.precision = precision;
			this.maxIncidence = maxIncidence;
		}
		
		/** */
		public Parameters (boolean symmetry_reduction, LinkPrecision link_precision, int radius, 	int precision, int maxIncidence) {
			this.SYMMETRY_REDUCTION = symmetry_reduction;
			this.LINK_PRECISION = link_precision;
			this.BELONGS_IS_SUB = false;
			this.radius = radius;
			this.precision = precision;
			this.maxIncidence = maxIncidence;
		}
		
		public final int radius;
		public final int precision; 
		public final int maxIncidence;
		
	}
	
}
