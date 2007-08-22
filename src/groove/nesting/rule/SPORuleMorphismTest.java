package groove.nesting.rule;

import groove.graph.DefaultMorphism;
import groove.graph.Graph;
import groove.rel.RegExprGraph;
import groove.trans.RuleNameLabel;
import groove.trans.SPORule;
import groove.view.FormatException;
import junit.framework.TestCase;

/**
 * JUnit Test Case for the SPORuleMorphism class
 *
 * @author kramor
 * @version 0.1 $Revision: 1.1 $ $Date: 2007-08-22 09:19:48 $
 */
public class SPORuleMorphismTest extends TestCase {

	private Graph graphG, graphH, graphI, graphJ, graphK, graphL;
	private SPORule ruleA, ruleB, ruleC;
	
	private SPORuleMorphism testInstanceA, testInstanceB; 
	
	/**
	 * Constructs a new SPORuleMorphismTest
	 */
	public SPORuleMorphismTest() {
		graphG = new RegExprGraph();
		graphH = new RegExprGraph();
		graphI = new RegExprGraph();
		graphJ = new RegExprGraph();
		graphK = new RegExprGraph();
		graphL = new RegExprGraph();
		try {
			ruleA = new SPORule(DefaultMorphism.prototype.createMorphism(graphG, graphH), new RuleNameLabel("rule A"), 1, null);
			ruleB = new SPORule(DefaultMorphism.prototype.createMorphism(graphI, graphJ), new RuleNameLabel("rule B"), 1, null);
			ruleC = new SPORule(DefaultMorphism.prototype.createMorphism(graphK, graphL), new RuleNameLabel("rule C"), 1, null);
		} catch (FormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		testInstanceA = SPORuleMorphism.prototype.createMorphism(ruleA, ruleB);
		testInstanceB = SPORuleMorphism.prototype.createMorphism(ruleB, ruleC);
	}

	/**
	 * Tests if the newly constructed SPORuleMorphisms are correct
	 */
	public void testSPORuleMorphismSPORuleSPORule() {
		if( testInstanceA.dom() != ruleA 
				|| testInstanceA.cod() != ruleB 
				|| testInstanceB.dom() != ruleB
				|| testInstanceB.cod() != ruleC )
			fail("Domain or codomain is incorrect");
	}

	/**
	 * Tests the SPORuleMorphism.after method
	 */
	public void testAfter() {
		SPORuleMorphism after = testInstanceB.after(testInstanceA);
		if( after.dom() != testInstanceA.dom() || after.cod() != testInstanceB.cod() )
			fail("Domain or codomain of rule morphism is incorrect");
		if( after.getLeftMorphism().dom() != testInstanceA.dom().lhs() 
				|| after.getLeftMorphism().cod() != testInstanceB.cod().lhs() )
			fail("Domain or codomain of left morphism is incorrect");
		if( after.getRightMorphism().dom() != testInstanceA.dom().rhs() 
				|| after.getRightMorphism().cod() != testInstanceB.cod().rhs() )
			fail("Domain or codomain of right morphism is incorrect");
	}

	/**
	 * Tests the internal left morphism of the SPORuleMorphism
	 */
	public void testGetLeftMorphism() {
		if( testInstanceA.getLeftMorphism().dom() != testInstanceA.dom().lhs() 
				|| testInstanceA.getLeftMorphism().cod() != testInstanceA.cod().lhs()
				|| testInstanceB.getLeftMorphism().dom() != testInstanceB.dom().lhs()
				|| testInstanceB.getLeftMorphism().cod() != testInstanceB.cod().lhs() )
			fail("The internal left morphism's domain or codomain is incorrect");
	}

	/**
	 * Tests the internal right morphism of the SPORuleMorphism
	 */
	public void testGetRightMorphism() {
		if( testInstanceA.getRightMorphism().dom() != testInstanceA.dom().rhs() 
				|| testInstanceA.getRightMorphism().cod() != testInstanceA.cod().rhs()
				|| testInstanceB.getRightMorphism().dom() != testInstanceB.dom().rhs()
				|| testInstanceB.getRightMorphism().cod() != testInstanceB.cod().rhs() )
			fail("The internal right morphism's domain or codomain is incorrect");
	}

	
	/**
	 * Tests the SPORuleMorphism.then method
	 */
	public void testThen() {
		SPORuleMorphism then = testInstanceA.then(testInstanceB);
		if( then.dom() != testInstanceA.dom() || then.cod() != testInstanceB.cod() )
			fail("Domain or codomain of rule morphism is incorrect");
		if( then.getLeftMorphism().dom() != testInstanceA.dom().lhs() 
				|| then.getLeftMorphism().cod() != testInstanceB.cod().lhs() )
			fail("Domain or codomain of left morphism is incorrect");
		if( then.getRightMorphism().dom() != testInstanceA.dom().rhs() 
				|| then.getRightMorphism().cod() != testInstanceB.cod().rhs() )
			fail("Domain or codomain of right morphism is incorrect");
	}

}
