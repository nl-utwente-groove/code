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

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class DefaultOp implements Op {
    /**
     * Constructs an atomic operator with empty symbol.
     */
    protected DefaultOp() {
        this(OpKind.ATOM, "", 0);
    }

    /**
     * Constructs an operator of a given kind, symbol and arity.
     */
    protected DefaultOp(OpKind kind, String symbol, int arity) {
        assert symbol != null;
        this.kind = kind;
        this.symbol = symbol;
        this.arity = arity;
        assert kind.getArity() < 0 || kind.getArity() == arity;
    }

    @Override
    public String getSymbol() {
        return this.symbol;
    }

    private final String symbol;

    @Override
    public OpKind getKind() {
        return this.kind;
    }

    private final OpKind kind;

    @Override
    public int getArity() {
        return this.arity;
    }

    private final int arity;
}
