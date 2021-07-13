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
package groove.util.parse;

import static groove.util.parse.ATermTreeParser.TokenClaz.CONST;
import static groove.util.parse.ATermTreeParser.TokenClaz.NAME;

/**
 * Extension of term tree parser that recognises identifiers and constants as atoms.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AExprTreeParser<O extends Op,T extends AExprTree<O,T>> extends ATermTreeParser<O,T> {
    /**
     * Constructs an expression parser, from a given prototype tree.
     */
    protected AExprTreeParser(T prototype) {
        super(prototype);
    }

    @Override
    protected T parseName() throws FormatException {
        assert has(NAME);
        Token firstToken = next();
        T result = createTree(getAtomOp());
        result.setId(parseId());
        setParseString(result, firstToken);
        return result;
    }

    @Override
    protected T parseConst() throws FormatException {
        T result = createTree(getAtomOp());
        Token constToken = consume(CONST);
        result.setConstant(constToken.createConstant());
        setParseString(result, constToken);
        return result;
    }
}
