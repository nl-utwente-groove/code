/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.explore.prettyparse;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.explore.encode.Serialized;

/**
 * A <code>PNumber</code> is a <code>SerializedParser</code> that reads a
 * positive number from a <code>StringConsumer</code>. If a number is found, it
 * is appended to an argument of a <code>Serialized</code>.
 *
 * @see SerializedParser
 * @see Serialized
 * @author Maarten de Mol
 */
@NonNullByDefault
public class PNumber implements SerializedParser {

    // The argument name (of a Serialized) in which the parse result is stored.
    private final String argumentName;

    /**
     * Constructs a <code>PNumber</code> out of an argument name of a
     * <code>Serialized</code>.
     */
    public PNumber(String argumentName) {
        this(argumentName, null);
    }

    /**
     * Constructs a <code>PNumber</code> out of an argument name of a
     * <code>Serialized</code>, with an optional default value.
     */
    public PNumber(String argumentName, @Nullable Integer defaultValue) {
        this.argumentName = argumentName;
        this.defaultValue = defaultValue == null
            ? null
            : defaultValue.toString();
    }

    @Override
    public boolean parse(StringConsumer stream, Serialized serialized) {
        boolean foundNumber = stream.consumeNumber();
        if (foundNumber) {
            serialized.appendArgument(this.argumentName, stream.getLastConsumed());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public @Nullable String toParsableString(Serialized serialized) {
        String value = serialized.getArgument(this.argumentName);
        String result = StringConsumer.parseNumber(value);
        if (result != null) {
            serialized.setArgument(this.argumentName, value.substring(result.length()));
        }
        return result;
    }

    @Override
    public String describeGrammar() {
        return "n";
    }

    @Override
    public boolean hasDefault() {
        return this.defaultValue != null;
    }

    /** The optional default value of this parser. */
    private final @Nullable String defaultValue;

    @Override
    public void setDefault(Serialized serialized) {
        if (this.defaultValue != null) {
            serialized.appendArgument(this.argumentName, this.defaultValue);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean containsDefault(Serialized serialized) {
        boolean result = false;
        var defaultValue = this.defaultValue;
        if (defaultValue != null) {
            String value = serialized.getArgument(this.argumentName);
            result = defaultValue.equals(value);
        }
        return result;
    }

}
