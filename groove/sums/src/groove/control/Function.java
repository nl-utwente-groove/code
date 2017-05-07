/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.control;

import groove.grammar.GrammarProperties;
import groove.grammar.QualName;
import groove.grammar.Signature;
import groove.grammar.UnitPar;
import groove.util.Fixable;

/**
 * Control program function.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Function extends Procedure implements Fixable {
    /**
     * Constructs a function with the given parameters.
     */
    public Function(QualName fullName, Signature<UnitPar.ProcedurePar> signature,
        QualName controlName, int startLine, GrammarProperties grammarProperties) {
        super(fullName, Kind.FUNCTION, signature, controlName, startLine, grammarProperties);
    }
}
