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
package nl.utwente.groove.util.parse;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.util.parse.Parser.AParser;

/**
 * Parser that recognises loadable Java classes.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class ClassParser extends AParser<Class<?>> {
    /**
     * Private constructor for the singleton instance.
     */
    private ClassParser() {
        super("Fully qualified class name of a class", Object.class, false);
    }

    @Override
    public Class<?> parse(String input) throws FormatException {
        if (input.isEmpty()) {
            return getDefaultValue();
        }
        try {
            return ClassLoader.getSystemClassLoader().loadClass(input);
        } catch (ClassNotFoundException exc) {
            throw new FormatException("Cannot load class '%s'", input);
        }
    }

    @Override
    public <V extends Class<?>> String unparse(@NonNull V value) throws IllegalArgumentException {
        return value.getCanonicalName();
    }

    /** Returns the singleton instance of this class. */
    static public ClassParser instance() {
        return INSTANCE;
    }

    static private final ClassParser INSTANCE = new ClassParser();
}
