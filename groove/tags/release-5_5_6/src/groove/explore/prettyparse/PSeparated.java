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
 * A <code>PSeparated</code> is a <code>SerializedParser</code> that applies a
 * given parser as long as possible, using another parser to explicitly
 * recognize a separation token. 
 * 
 * @see SerializedParser
 * @see Serialized
 * @author Maarten de Mol
 */
public class PSeparated implements SerializedParser {

    // The parser for the items.
    private final SerializedParser itemParser;

    // The parser for the separator.
    private final SerializedParser separatorParser;

    /**
     * Constructs a <code>PSeparated</code> out of a parser for an item and a
     * parser for the separator symbol.
     */
    public PSeparated(SerializedParser itemParser, SerializedParser separatorParser) {
        this.itemParser = itemParser;
        this.separatorParser = separatorParser;
    }

    @Override
    public boolean parse(StringConsumer stream, Serialized serialized) {
        if (this.itemParser.parse(stream, serialized)) {
            while (true) {
                if (this.separatorParser.parse(stream, serialized)) {
                    if (!this.itemParser.parse(stream, serialized)) {
                        return false;
                    }
                } else {
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public String toParsableString(Serialized source) {
        String result = this.itemParser.toParsableString(source);
        if (result != null) {
            String next = this.separatorParser.toParsableString(source);
            while (next != null) {
                result = result + next;
                next = this.itemParser.toParsableString(source);
                if (next == null) {
                    result = null;
                } else {
                    result = result + next;
                    next = this.separatorParser.toParsableString(source);
                }
            }
        }
        return result;
    }

    @Override
    public String describeGrammar() {
        return this.itemParser.describeGrammar() + this.separatorParser.describeGrammar() + "...";
    }
}
