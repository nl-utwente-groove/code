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
 * $Id: RegExprLabelParser.java,v 1.3 2007-08-26 07:24:10 rensink Exp $
 */
package groove.view;

import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.rel.RegExpr;
import groove.rel.RegExprLabel;

/** Parser that attempts to turn the string into a regular expression label. */
public class RegExprLabelParser implements LabelParser {
	/**
	 * This implementation attempts to turn <code>text</code> into a 
	 * regular expression, and if successful, turns the expression into
	 * a {@link RegExprLabel}.
	 */
	public Label parse(String text) throws FormatException {
		RegExpr expr = RegExpr.parse(text);
		if (expr.isAtom()) {
			return DefaultLabel.createLabel(expr.getAtomText());
		} else {
			return expr.toLabel();
		}
	}

	/** 
     * This implementation puts quotes around the label text, if it can otherwise be
     * interpreted as a non-atom. 
     */
    public String unparse(Label label) {
        String result = label.text();
        if (!(label instanceof RegExprLabel) && !RegExpr.isAtom(result)) {
            result = RegExpr.atom(result).toString();
        }
        return result;
    }
}
