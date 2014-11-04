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
 * A <code>PSequence</code> implements a <code>SerializedParser</code> that
 * is made up of a sequence of individual <code>SerializedParsers</code>.
 * The contained parsers are applied one after the other and must all succeed.
 * 
 * @see SerializedParser
 * @author Maarten de Mol
 */
public class PSequence implements SerializedParser {

    // The contained parsers.
    private final SerializedParser[] parsers;

    /**
     * Constructs a <code>PSequence</code> by storing the component parsers.
     */
    public PSequence(SerializedParser... parsers) {
        this.parsers = parsers;
    }

    @Override
    public boolean parse(StringConsumer stream, Serialized serialized) {
        for (SerializedParser parser : this.parsers) {
            if (!parser.parse(stream, serialized)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toParsableString(Serialized source) {
        String result = "";
        for (SerializedParser parser : this.parsers) {
            String next = parser.toParsableString(source);
            if (next == null) {
                result = null;
                break;
            } else {
                result = result + next;
            }
        }
        return result;
    }

    @Override
    public String describeGrammar() {
        StringBuffer buffer = new StringBuffer();
        for (SerializedParser parser : this.parsers) {
            buffer.append(parser.describeGrammar());
        }
        return buffer.toString();
    }
}