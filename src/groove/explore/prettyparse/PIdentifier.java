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
 * A <code>PIdentifier</code> is a <code>SerializedParser</code> that reads an
 * identifier from a <code>StringConsumer</code>. If an identifier is found, it
 * is appended to an argument of a <code>Serialized</code>.
 * 
 * @see SerializedParser
 * @see Serialized
 * @author Maarten de Mol
 */
public class PIdentifier implements SerializedParser {

    // The argument name (of a Serialized) in which the parse result is stored.
    private final String argumentName;

    /**
     * Constructs a <code>PIdentifier</code> out of an argument name of a
     * <code>Serialized</code>.
     */
    public PIdentifier(String argumentName) {
        this.argumentName = argumentName;
    }

    @Override
    public boolean parse(StringConsumer stream, Serialized serialized) {
        boolean foundIdentifier = stream.consumeIdentifier();
        if (foundIdentifier) {
            serialized.appendArgument(this.argumentName, stream.getLastConsumed());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toParsableString(Serialized serialized) {
        String value = serialized.getArgument(this.argumentName);
        String result = StringConsumer.parseIdentifier(value);
        if (result != null) {
            serialized.setArgument(this.argumentName, value.substring(result.length()));
        }
        return result;
    }

    @Override
    public String describeGrammar() {
        return "id";
    }

}
