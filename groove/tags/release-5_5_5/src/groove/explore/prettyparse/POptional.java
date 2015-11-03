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
 * A <code>POptional</code> is a <code>SerializedParser</code> that either
 * reads a specific literal from a <code>StringConsumer</code>, or succeeds
 * vacuously. The result is stored in an argument of a <code>Serialized</code>;
 * the written value depends on whether the literal was found or not.
 * 
 * @see SerializedParser
 * @see Serialized
 * @author Maarten de Mol
 */
public class POptional implements SerializedParser {

    // The literal to search for.
    private final String literal;

    // The argument name (of a Serialized) in which the parse result is stored.
    private final String argumentName;

    // The value to be stored if the literal is found.
    private final String presentValue;

    // The value to be stored if the literal is not found.
    private final String absentValue;

    /**
     * Constructs a <code>POptional</code> out of a literal to search for, an
     * argument name of a <code>Serialized</code>, a value to be stored on
     * presence and a value to be stored on absence.
     */
    public POptional(String literal, String argumentName, String presentValue, String absentValue) {
        this.literal = literal;
        this.argumentName = argumentName;
        this.presentValue = presentValue;
        this.absentValue = absentValue;
    }

    @Override
    public boolean parse(StringConsumer stream, Serialized serialized) {
        boolean foundLiteral = stream.consumeLiteral(this.literal);
        if (foundLiteral) {
            serialized.setArgument(this.argumentName, this.presentValue);
        } else {
            serialized.setArgument(this.argumentName, this.absentValue);
        }
        return true;
    }

    @Override
    public String toParsableString(Serialized serialized) {
        String result = null;
        String keyword = serialized.getArgument(this.argumentName);
        if (this.presentValue.equals(keyword)) {
            result = this.literal;
        } else if (this.absentValue.equals(keyword)) {
            result = "";
        }
        return result;
    }

    @Override
    public String describeGrammar() {
        return "[" + this.literal + "]";
    }

}
