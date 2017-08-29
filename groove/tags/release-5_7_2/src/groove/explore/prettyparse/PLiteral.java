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
 * A <code>PLiteral</code> is a <code>SerializedParser</code> that reads a
 * specific literal from a <code>StringConsumer</code>. If the literal is
 * present, it is optionally appended to an argument of a <code>Serialized</code>.
 * 
 * @see SerializedParser
 * @see Serialized
 * @author Maarten de Mol
 */
public class PLiteral implements SerializedParser {

    // The literal to search for.
    private final String literal;

    // The argument name (of a Serialized) in which the parse result is stored.
    private final String argumentName;

    /**
     * Constructs a <code>POptional</code> out of a literal to search for.
     * @param literal the literal to look for
     */
    public PLiteral(String literal) {
        this(literal, null);
    }

    /**
     * Constructs a <code>POptional</code> out of a literal to search for and
     * an optional argument name of a <code>Serialized</code>.
     * @param literal the literal to look for
     * @param argumentName if not {@code null}, the argument name of the {@link Serialized}
     * that the value (if found) is appended to
     */
    public PLiteral(String literal, String argumentName) {
        this.literal = literal;
        this.argumentName = argumentName;
    }

    @Override
    public boolean parse(StringConsumer stream, Serialized serialized) {
        boolean foundLiteral = stream.consumeLiteral(this.literal);
        if (foundLiteral) {
            if (this.argumentName != null) {
                serialized.appendArgument(this.argumentName, this.literal);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toParsableString(Serialized serialized) {
        String result = null;
        if (this.argumentName == null) {
            result = this.literal;
        } else {
            String value = serialized.getArgument(this.argumentName);
            if (value.startsWith(this.literal)) {
                result = this.literal;
                serialized.setArgument(this.argumentName, value.substring(result.length()));
            }
        }
        return result;
    }

    @Override
    public String describeGrammar() {
        return this.literal;
    }

}
