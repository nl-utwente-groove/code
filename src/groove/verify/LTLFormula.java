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
 * $Id: LTLFormula.java,v 1.5 2008-03-04 14:52:12 kastenberg Exp $
 */

package groove.verify;

import groove.view.FormatException;

/**
 * Specific class for parsing LTL formulae.
 * @author Harmen Kastenberg
 * @version $Revision$
 */
public class LTLFormula extends CTLStarFormula {
    @Override
	protected TemporalFormula createAll(TemporalFormula operand) throws FormatException {
		throw new FormatException("The path quantifier 'All' is not defined for LTL formulae");
	}

    @Override
	protected TemporalFormula createExists(TemporalFormula operand) throws FormatException {
		throw new FormatException("The path quantifier 'Exists' is not defined for LTL formulae");
	}

	static public TemporalFormula parseFormula(String expr) throws FormatException {
    	CTLStarFormula parser = LTLFormula.getInstance();
    	TemporalFormula.setFactory(parser);
    	return parser.parse(expr);
    }

    static public CTLStarFormula getInstance() {
		if (instance == null) {
			instance = new LTLFormula();
		}
		return instance;
	}

	static private LTLFormula instance; 
}
