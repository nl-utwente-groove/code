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
 * A <code>SerializedParser</code> describes a parser that reads from a
 * <code>StringConsumer</code> and immediately stores the result in a given
 * <code>Serialized</code>. The parser is implemented by means of two methods:
 * one for the combination of parsing and storing the result itself; and one
 * for showing a pretty printed grammar of the accepted language. 
 * 
 * @see Serialized
 * @see StringConsumer
 * @author Maarten de Mol
 */
public interface SerializedParser {

    /**
     * The <code>parse</code> method recognizes an initial part of a given
     * <code>StringConsumer</code> stream. On success, the parsed initial part
     * is removed from the stream; on failure, the stream is left unchanged.
     * The result of parsing is stored immediately in the argument
     * <code>Serialized</code>, which is only guaranteed to be valid when
     * parsing succeeds (and should be thrown away otherwise).  
     */
    public boolean parse(StringConsumer stream, Serialized serialized);

    /**
     * The <code>describeGrammar</code> returns a (humanly readable)
     * representation of the grammar that is accepted by the parser.
     */
    public String describeGrammar();

    /** Converts a serialized source object back to a string that,
     * when parsed by {@link #parse(StringConsumer, Serialized)},
     * will result in an object equal to the argument.
     * Returns {@code null} if the source was not produced by this parser.
     * @param source the source to be converted to string; may be
     * modified as a result of this call
     */
    public String toParsableString(Serialized source);
}
