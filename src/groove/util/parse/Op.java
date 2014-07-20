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
 * Interface for expression operators.
 * @author Arend Rensink
 * @version $Id$
 */
public interface Op {
    /** Indicates if this operator has a dedicated symbol. */
    public boolean hasSymbol();

    /** Returns the optional symbol for this operator. */
    public String getSymbol();

    /** Returns the operator precedence. */
    public OpKind getKind();

    /** Returns the expected number of arguments of the operator. */
    public int getArity();
}
