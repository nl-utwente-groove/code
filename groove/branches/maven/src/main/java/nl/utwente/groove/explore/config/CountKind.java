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
package nl.utwente.groove.explore.config;

import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.NullParser;
import nl.utwente.groove.util.parse.Parser;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public enum CountKind implements SettingKey {
    /** Continue regardless of results found. */
    ALL("All", "Continue regardless of the number of results", null),
    /** Halt after the first result. */
    ONE("One", "Halt after the first result", null),
    /** User-defined count; 0 means unbounded. */
    COUNT("Value", "User-defined; 0 means unbounded", Parser.natural),;

    private CountKind(String name, String explanation, Parser<Integer> parser) {
        this.name = name;
        this.explanation = explanation;
        this.parser = parser == null ? NullParser.instance(Integer.class) : parser;
    }

    @Override
    public String getName() {
        return this.name;
    }

    private final String name;

    @Override
    public String getContentName() {
        return "Result count";
    }

    @Override
    public String getExplanation() {
        return this.explanation;
    }

    private final String explanation;

    @Override
    public Parser<Integer> parser() {
        return this.parser;
    }

    private final Parser<Integer> parser;

    @Override
    public Setting<CountKind,Integer> getDefaultSetting() {
        return createSetting(getDefaultValue());
    }

    @Override
    public Setting<CountKind,Integer> createSetting(Object content)
        throws IllegalArgumentException {
        if (!isValue(content)) {
            throw Exceptions.illegalArg("Illegal value '%s' for count kind '%s'", content, this);
        }
        return new DefaultSetting<>(this, (Integer) content);
    }
}
