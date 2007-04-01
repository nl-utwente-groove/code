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
 * $Id$
 */
package groove.graph.aspect;

import groove.graph.Label;
import groove.rel.RegExpr;
import groove.rel.RegExprLabel;
import groove.util.FormatException;
import groove.util.Groove;

/**
 * Graph aspect dealing with transformation rules.
 * Values are: <i>eraser</i>, <i>reader</i> or <i>creator</i>.
 * @author Arend Rensink
 * @version $Revision$
 */
public class RuleAspect extends AbstractAspect {
    /**
     * The name of the rule aspect.
     */
    public static final String RULE_ASPECT_NAME = "role";
    /** Name of the eraser aspect value. */
    public static final String ERASER_NAME = Groove.getXMLProperty("label.eraser.prefix");
    /** The eraser aspect value. */
    public static final AspectValue ERASER;
    /** Name of the reader aspect value. */
    public static final String READER_NAME = Groove.getXMLProperty("label.reader.prefix");
    /** The reader aspect value. */
    public static final AspectValue READER;
    /** Name of the creator aspect value. */
    public static final String CREATOR_NAME = Groove.getXMLProperty("label.creator.prefix");
    /** The creator aspect value. */
    public static final AspectValue CREATOR;
    /** Name of the embargo aspect value. */
    public static final String EMBARGO_NAME = Groove.getXMLProperty("label.embargo.prefix");
    /** The embargo aspect value. */
    public static final AspectValue EMBARGO;
    /** The total number of roles. */
    public static final int VALUE_COUNT; 
    /**
	 * The singleton instance of this class.
	 */
	private static final RuleAspect instance = new RuleAspect();

	static {
		try {
			ERASER = instance.addValue(ERASER_NAME);
			CREATOR = instance.addValue(CREATOR_NAME);
			EMBARGO = instance.addValue(EMBARGO_NAME);
			READER = instance.addValue(READER_NAME);
			instance.setDefaultValue(READER);
			CREATOR.setSourceToEdge(CREATOR);
			CREATOR.setTargetToEdge(CREATOR);
			ERASER.setSourceToEdge(ERASER);
			ERASER.setTargetToEdge(ERASER);
			EMBARGO.setSourceToEdge(EMBARGO);
			EMBARGO.setTargetToEdge(EMBARGO);
			VALUE_COUNT = instance.getValues().size();
		} catch (FormatException exc) {
			throw new Error("Aspect '" + RULE_ASPECT_NAME
					+ "' cannot be initialised due to name conflict", exc);
		}
    }
    /**
     * Returns the singleton instance of this aspect.
     */
    public static RuleAspect getInstance() {
        return instance;
    }
    
    /**
	 * Tests if a given aspect element contains a {@link RuleAspect} value
	 * that indicates presence in the left hand side.
	 * This is the case if there is an aspect value in the element which
	 * equals either {@link #READER} or {@link #ERASER}.
	 * @param element the element to be tested
	 * @return <code>true</code> if <code>element</code> contains a {@link RuleAspect}
	 * value that equals either  {@link #READER} or {@link #ERASER}.
	 */
	public static boolean inLHS(AspectElement element) {
		AspectValue role = element.getValue(getInstance());
		return (role == READER || role == ERASER);
	}

	/**
	 * Tests if a given aspect element contains a {@link RuleAspect} value
	 * that indicates presence in the right hand side.
	 * This is the case if there is an aspect value in the element which
	 * equals either {@link #READER} or {@link #CREATOR}.
	 * @param element the element to be tested
	 * @return <code>true</code> if <code>element</code> contains a {@link RuleAspect}
	 * value that equals either  {@link #READER} or {@link #CREATOR}.
	 */
	public static boolean inRHS(AspectElement element) {
		AspectValue role = element.getValue(getInstance());
		return (role == READER || role == CREATOR);
	}

	/**
	 * Tests if a given aspect element contains a {@link RuleAspect} value
	 * that indicates presence a negative application condition.
	 * This is the case if there is an aspect value in the element which
	 * equals {@link #EMBARGO}.
	 * @param element the element to be tested
	 * @return <code>true</code> if <code>element</code> contains a {@link RuleAspect}
	 * value that equals {@link #EMBARGO}.
	 */
	public static boolean inNAC(AspectElement element) {
		AspectValue role = element.getValue(getInstance());
		return (role == EMBARGO);
	}

	/**
	 * Tests if a given aspect element is a creator.
	 * This is the case if there is an aspect value in the element which
	 * equals {@link #CREATOR}.
	 * @param element the element to be tested
	 * @return <code>true</code> if <code>element</code> contains a {@link RuleAspect}
	 * value that equals {@link #CREATOR}.
	 */
	public static boolean isCreator(AspectElement element) {
		AspectValue role = element.getValue(getInstance());
		return (role == CREATOR);
	}

	/** Private constructor to create the singleton instance. */
    private RuleAspect() {
		super(RULE_ASPECT_NAME);
	}

	/**
	 * This implementation considers {@link #EMBARGO} to be more
	 * demanding than {@link #ERASER}.
	 */
	@Override
	protected AspectValue getMaxValue(AspectValue value1, AspectValue value2) throws FormatException {
		if (value1 == ERASER && value2 == EMBARGO) {
			return EMBARGO;
		} else if (value1 == EMBARGO && value2 == ERASER) {
			return EMBARGO;
		} else {
			return super.getMaxValue(value1, value2);
		}
	}

	/**
	 * This implementation tests for certain regular expressions.
	 * No declared eraser may carry a regular expression label other
	 * than a wildcard or variable, and no inferred creator may have 
	 * a regular expression other than a wildcard, merger or variable.
	 */
	@Override
	public void testLabel(Label label, AspectValue declaredValue, AspectValue inferredValue) throws FormatException {
		// if the label is not a regular expression, it is in any case fine
		if (label instanceof RegExprLabel) {
			testLabel(((RegExprLabel) label).getRegExpr(), declaredValue, inferredValue);
		}
	}

	/**
	 * Callback method to test the label with the knowledge that it is 
	 * a regular expression.
	 * @see #testLabel(Label, AspectValue, AspectValue)
	 */
	private void testLabel(RegExpr expr, AspectValue declaredValue,
			AspectValue inferredValue) throws FormatException {
		// check if negation occurs anywhere except on top level
		if (expr.containsOperator(RegExpr.NEG_OPERATOR)) {
			throw new FormatException("Negation may only occur on top level in %s", expr);
		}
		// check the expression is a regular eraser pattern
		if (declaredValue == ERASER) {
			if (! expr.isWildcard()) {
				throw new FormatException("Regular expression %s not allowed on an eraser edge", expr);
			}
		}
		// check the expression is a regular creator pattern
		if (inferredValue == CREATOR) {
			if (! (expr.isWildcard() || expr.isEmpty())) {
				throw new FormatException("Regular expression %s not allowed on a creator edge", expr);
			}
		}
	}
}
