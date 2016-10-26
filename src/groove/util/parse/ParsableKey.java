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
 * Interface for keys with values that can be parsed from strings.
 * @author Arend Rensink
 */
public interface ParsableKey<V> {
    /** Key name, in camel case (starting with lowercase). */
    public String getName();

    /** Returns an explanation of this key. */
    public String getExplanation();

    /** Returns a parser for values of this key. */
    public Parser<? extends V> parser();

    /** Convenience method for {@code parser().getDefaultValue()}. */
    default public V getDefaultValue() {
        return parser().getDefaultValue();
    }

    /** Convenience method for {@code parser().isValue(value)}. */
    default public boolean isValue(Object value) {
        return parser().isValue(value);
    }
}
