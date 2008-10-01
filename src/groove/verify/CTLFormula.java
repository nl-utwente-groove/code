/*
 * GROOVE: GRaphs for Object Oriented VErification
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
 * $Id: CTLFormula.java,v 1.9 2008-01-28 12:21:05 kastenberg Exp $
 */

package groove.verify;

import groove.view.FormatException;

import java.util.ArrayList;
import java.util.List;

/**
 * Specific class for parsing CTL formulae.
 * @author Harmen Kastenberg
 * @version $Revision$ $Date: 2008-01-28 12:21:05 $
 */
public class CTLFormula extends CTLStarFormula {

    @Override
	protected TemporalFormula createAll(TemporalFormula operand) throws FormatException {
		// the path quantifier all must be followed by a temporal operator
		if (operand instanceof Next) {
	        // AX(phi) <==> !EX(!phi)
			return createAllNext(operand.getOperands().get(0));
		} else if (operand instanceof Until) {
			return createAllUntil(operand.getOperands());
		} else if (operand instanceof Finally) {
	    	// AF(phi) <==> A(true U phi)
			return createAllFinally(operand.getOperands().get(0));
		} else if (operand instanceof Globally) {
	    	// AG(phi) <==> !(EF(!phi))
			return createAllGlobally(operand.getOperands().get(0));
		} else {
			throw new FormatException("The path quantifier 'All' should be followed by a temporal operation instead of " + operand);
		}
	}

    @Override
	protected TemporalFormula createExists(TemporalFormula operand) throws FormatException {
		// the path quantifier all must be followed by a temporal operator
		if (operand instanceof Next) {
			return createExistsNext(operand.getOperands().get(0));
		} else if (operand instanceof Until) {
			return createExistsUntil(operand.getOperands());
		} else if (operand instanceof Finally) {
	    	// EF(phi) <==> E(true U phi)
			return createExistsFinally(operand.getOperands().get(0));
		} else if (operand instanceof Globally) {
	    	// EG(phi) <==> !(AF(!phi))
			return createExistsGlobally(operand.getOperands().get(0));
		} else {
			throw new FormatException("The path quantifier 'Exists' should be followed by a temporal operation instead of " + operand);
		}
	}

    /**
	 * Creates an exists-formula with a check for correct nesting of operators.
	 * @param operand the operand of the formula
	 * @return the freshly created formula
	 * @throws FormatException if the formula is not formatted correctly
	 */
    protected TemporalFormula createExistsNext(TemporalFormula operand) throws FormatException {
		if (operand instanceof TemporalOperator) {
			throw new FormatException("Temporal operators should be proceeded by a path quantifier: " + operand.getOperator());
		}
		CTLStarFormula factory = TemporalFormula.getFactory();
		TemporalFormula next = factory.createNext(operand);
		return Exists.createInstance(next);
    }

    /**
	 * Creates an exists-formula with a check for correct nesting of operators.
	 * @param operands the operands of the formula
	 * @return the freshly created formula
	 * @throws FormatException if the formula is not formatted correctly
	 */
    protected TemporalFormula createExistsUntil(List<TemporalFormula> operands) throws FormatException {
    	for(TemporalFormula operand: operands) {
    		if (operand instanceof TemporalOperator) {
    			throw new FormatException("Temporal operators should be proceeded by a path quantifier: " + operand.getOperator());
    		}
    	}
		CTLStarFormula factory = TemporalFormula.getFactory();
		TemporalFormula until = factory.createUntil(operands);
		return Exists.createInstance(until);
    }

    /**
	 * Creates an equivalent exists-formula from the current all-formula.
	 * @param operand the operand of the equivalent formula
	 * @return the freshly created formula
	 * @throws FormatException if the formula is not formatted correctly
	 */
	protected TemporalFormula createAllNext(TemporalFormula operand) throws FormatException {
		if (operand instanceof TemporalOperator) {
			throw new FormatException("Temporal operators should be proceeded by a path quantifier: " + operand.getOperator());
		}
        // AX(phi) <==> !EX(!phi)
		CTLStarFormula factory = TemporalFormula.getFactory();
		TemporalFormula neg = factory.createNeg(operand);
		TemporalFormula next = factory.createNext(neg);
		TemporalFormula exists = factory.createExists(next);
		return factory.createNeg(exists);
	}

	/**
	 * Creates an equivalent all-formula from the current all-formula.
	 * @param operand the operand for the equivalent formula
	 * @return the freshly created formula
	 * @throws FormatException if the formula is not formatted correctly
	 */
	protected TemporalFormula createAllFinally(TemporalFormula operand) throws FormatException {
		if (operand instanceof TemporalOperator) {
			throw new FormatException("Temporal operators should be proceeded by a path quantifier: " + operand.getOperator());
		}
    	// AF(phi) <==> A(true U phi)
    	List<TemporalFormula> operandList = new ArrayList<TemporalFormula>();
    	operandList.add(new True());
    	operandList.add(operand);
		CTLStarFormula factory = TemporalFormula.getFactory();
		TemporalFormula until = factory.createUntil(operandList);
		return factory.createAll(until);
	}

	/**
	 * Creates an equivalent exists-formula from the current all-formula.
	 * @param operand the operand for the equivalent formula
	 * @return the freshly created formula
	 * @throws FormatException if the formula is not formatted correctly
	 */
	protected TemporalFormula createAllGlobally(TemporalFormula operand) throws FormatException {
		if (operand instanceof TemporalOperator) {
			throw new FormatException("Temporal operators should be proceeded by a path quantifier: " + operand.getOperator());
		}

		// AG(phi) <==> !(EF(!phi))
		CTLStarFormula factory = TemporalFormula.getFactory();
		TemporalFormula negFormula = factory.createNeg(operand);
		TemporalFormula finallyFormula = factory.createFinally(negFormula);
		TemporalFormula existsFormula = factory.createExists(finallyFormula);
		return factory.createNeg(existsFormula);
	}

    /**
	 * Creates an all-formula with a check for correct nesting of operators.
	 * @param operands the operands of the formula
	 * @return the freshly created formula
	 * @throws FormatException if the formula is not formatted correctly
	 */
    protected TemporalFormula createAllUntil(List<TemporalFormula> operands) throws FormatException {
    	for(TemporalFormula operand: operands) {
    		if (operand instanceof TemporalOperator) {
    			throw new FormatException("Temporal operators should be proceeded by a path quantifier: " + operand.getOperator());
    		}
    	}
		CTLStarFormula factory = TemporalFormula.getFactory();
		TemporalFormula until = factory.createUntil(operands);
		return All.createInstance(until);
    }

    /**
	 * Creates an equivalent exists-formula from the current exists-formula.
	 * @param operand the operand for the equivalent formula
	 * @return the freshly created formula
	 * @throws FormatException if the formula is not formatted correctly
	 */
	protected TemporalFormula createExistsFinally(TemporalFormula operand) throws FormatException {
		if (operand instanceof TemporalOperator) {
			throw new FormatException("Temporal operators should be proceeded by a path quantifier: " + operand.getOperator());
		}
    	// EF(phi) <==> E(true U phi)
    	List<TemporalFormula> operandList = new ArrayList<TemporalFormula>();
    	operandList.add(new True());
    	operandList.add(operand);
		CTLStarFormula factory = TemporalFormula.getFactory();
		TemporalFormula until = factory.createUntil(operandList);
		return factory.createExists(until);
	}

	/**
	 * Creates an equivalent exists-formula from the current exists-formula.
	 * @param operand the operand for the equivalent formula
	 * @return the freshly created formula
	 * @throws FormatException if the formula is not formatted correctly
	 */
	protected TemporalFormula createExistsGlobally(TemporalFormula operand) throws FormatException {
		if (operand instanceof TemporalOperator) {
			throw new FormatException("Temporal operators should be proceeded by a path quantifier: " + operand.getOperator());
		}
    	// EG(phi) <==> !(AF(!phi))
		CTLStarFormula factory = TemporalFormula.getFactory();
		TemporalFormula negFormula = factory.createNeg(operand);
		TemporalFormula finallyFormula = factory.createFinally(negFormula);
		TemporalFormula allFormula = factory.createAll(finallyFormula);
		return factory.createNeg(allFormula);
	}

	static public TemporalFormula parseFormula(String expr) throws FormatException {
    	CTLStarFormula parser = CTLFormula.getInstance();
    	TemporalFormula.setFactory(parser);
    	TemporalFormula result = parser.parse(expr);
    	if (result instanceof TemporalOperator) {
    		throw new FormatException("Temporal operator should always be preceeded by a path quantifier: " + result.getOperator());
    	} else {
    		return result;
    	}
    }

    static public CTLStarFormula getInstance() {
		if (instance == null) {
			instance = new CTLFormula();
		}
		return instance;
	}

	/**
	 * The singleton instance.
	 */
	static private CTLFormula instance; 
}
