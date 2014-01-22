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
package groove.explore.prettyparse;

import groove.explore.encode.Serialized;

/**
 * A <code>PAll</code> is a <code>SerializedParser</code> that passes the
 * entire input string to an argument of a <code>Serialized</code>.
 * 
 * @see SerializedParser
 * @see Serialized
 * @author Maarten de Mol
 */
public class PAll implements SerializedParser {

    // The argument name (of a Serialized) in which the parse result is stored.
    private final String argumentName;

    /**
     * Constructs a <code>PAll</code> out of an argument name of a
     * <code>Serialized</code>.
     */
    public PAll(String argumentName) {
        this.argumentName = argumentName;
    }

    @Override
    public boolean parse(StringConsumer stream, Serialized serialized) {
        stream.consumeAll();
        serialized.setArgument(this.argumentName, stream.getLastConsumed());
        return true;
    }

    @Override
    public String toParsableString(Serialized serialized) {
        return serialized.getArgument(this.argumentName);
    }

    @Override
    public String describeGrammar() {
        return this.argumentName;
    }
}
