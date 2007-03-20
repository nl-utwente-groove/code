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
 * $Id: CTLFormula.java,v 1.1.1.2 2007-03-20 10:43:00 kastenberg Exp $
 */

package groove.verify;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;

import groove.util.ExprFormatException;
import groove.verify.CTLStarFormula.All;
import groove.verify.CTLStarFormula.Exists;
import groove.verify.CTLStarFormula.Neg;
import groove.verify.CTLStarFormula.Next;
import groove.verify.CTLStarFormula.True;
import groove.verify.CTLStarFormula.Until;

/**
 * Specific class for parsing CTL formulae.
 * @author Harmen Kastenberg
 * @version $Revision: 1.1.1.2 $ $Date: 2007-03-20 10:43:00 $
 */
public class CTLFormula extends CTLStarFormula {

	/* (non-Javadoc)
	 * @see groove.verify.CTLStarFormula#createAll(groove.verify.TemporalFormula)
	 */
	protected TemporalFormula createAll(TemporalFormula operand) throws ExprFormatException {
		// the path quantifier all must be followed by a temporal operator
		if (operand instanceof Next) {
	        // AX(phi) <==> !EX(!phi)
			return createAllNext(operand.getOperands().get(0));
		} else if (operand instanceof Until) {
			return All.createInstance(operand);
		} else if (operand instanceof Finally) {
	    	// AF(phi) <==> A(true U phi)
			return createAllFinally(operand.getOperands().get(0));
		} else if (operand instanceof Globally) {
	    	// AG(phi) <==> !(EF(!phi))
			return createAllGlobally(operand.getOperands().get(0));
		} else {
			throw new ExprFormatException("The path quantifier 'All' should be followed by a temporal operation instead of " + operand);
		}
	}

	/* (non-Javadoc)
	 * @see groove.verify.CTLStarFormula#createExists(groove.verify.TemporalFormula)
	 */
	protected TemporalFormula createExists(TemporalFormula operand) throws ExprFormatException {
		// the path quantifier all must be followed by a temporal operator
		if (operand instanceof Next) {
			return Exists.createInstance(operand);
		} else if (operand instanceof Until) {
			return Exists.createInstance(operand);
		} else if (operand instanceof Finally) {
	    	// EF(phi) <==> E(true U phi)
			return createExistsFinally(operand.getOperands().get(0));
		} else if (operand instanceof Globally) {
	    	// EG(phi) <==> !(AF(!phi))
			return createExistsGlobally(operand.getOperands().get(0));
		} else {
			throw new ExprFormatException("The path quantifier 'Exists' should be followed by a temporal operation instead of " + operand);
		}
	}

	/**
	 * Creates an equivalent exists-formula from the current all-formula.
	 * @param operand the operand of the equivalent formula
	 * @return the freshly created formula
	 * @throws ExprFormatException if the formula is not formatted correctly
	 */
	protected TemporalFormula createAllNext(TemporalFormula operand) throws ExprFormatException {
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
	 * @throws ExprFormatException if the formula is not formatted correctly
	 */
	protected TemporalFormula createAllFinally(TemporalFormula operand) throws ExprFormatException {
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
	 * @throws ExprFormatException if the formula is not formatted correctly
	 */
	protected TemporalFormula createAllGlobally(TemporalFormula operand) throws ExprFormatException {
    	// AG(phi) <==> !(EF(!phi))
		CTLStarFormula factory = TemporalFormula.getFactory();
		TemporalFormula negFormula = factory.createNeg(operand);
		TemporalFormula finallyFormula = factory.createFinally(negFormula);
		TemporalFormula existsFormula = factory.createExists(finallyFormula);
		return factory.createNeg(existsFormula);
	}

	/**
	 * Creates an equivalent exists-formula from the current exists-formula.
	 * @param operand the operand for the equivalent formula
	 * @return the freshly created formula
	 * @throws ExprFormatException if the formula is not formatted correctly
	 */
	protected TemporalFormula createExistsFinally(TemporalFormula operand) throws ExprFormatException {
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
	 * @throws ExprFormatException if the formula is not formatted correctly
	 */
	protected TemporalFormula createExistsGlobally(TemporalFormula operand) throws ExprFormatException {
    	// EG(phi) <==> !(AF(!phi))
		CTLStarFormula factory = TemporalFormula.getFactory();
		TemporalFormula negFormula = factory.createNeg(operand);
		TemporalFormula finallyFormula = factory.createFinally(negFormula);
		TemporalFormula allFormula = factory.createAll(finallyFormula);
		return factory.createNeg(allFormula);
	}

	static public TemporalFormula parseFormula(String expr) throws ExprFormatException {
    	CTLStarFormula parser = CTLFormula.getInstance();
    	TemporalFormula.setFactory(parser);
    	return parser.parse(expr);
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
