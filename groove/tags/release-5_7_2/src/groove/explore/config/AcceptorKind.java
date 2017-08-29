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
package groove.explore.config;

import groove.util.parse.NullParser;
import groove.util.parse.Parser;
import groove.util.parse.StringParser;

/**
 * Key determining which states are accepted as results.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum AcceptorKind implements SettingKey {
    /** Final states. */
    FINAL("final", null, "Final states are results", null),
    /** States satisfying a graph condition. */
    CONDITION("condition", "Property name", "Any state satisfying a given property", StringParser.identity()) {
        @Override
        public Setting<?,?> createSettting() throws IllegalArgumentException {
            throw new IllegalArgumentException();
        }

        @Override
        public Setting<?,?> createSetting(Object content) throws IllegalArgumentException {
            return new DefaultSetting<>(this, content);
        }
    },
    /** States satisfying a propositional formula. */
    FORMULA("formula", "Property formula", "Any state satisfying a propositional formula", StringParser.identity()) {
        @Override
        public Setting<?,?> createSettting() throws IllegalArgumentException {
            throw new IllegalArgumentException();
        }

        @Override
        public Setting<?,?> createSetting(Object content) throws IllegalArgumentException {
            return new DefaultSetting<>(this, content);
        }
    },
    /** All states. */
    ANY("any", null, "All states are results", null),
    /** No states. */
    NONE("none", null, "No state is considered a result", null),;

    private AcceptorKind(String name, String contentName, String explanation, Parser<?> parser) {
        this.name = name;
        this.contentName = contentName;
        this.explanation = explanation;
        this.parser = parser == null ? NullParser.instance(Object.class) : parser;
    }

    @Override
    public String getName() {
        return this.name;
    }

    private final String name;

    @Override
    public String getContentName() {
        return this.contentName;
    }

    private final String contentName;

    @Override
    public Setting<?,?> getDefaultSetting() {
        return createSetting(getDefaultValue());
    }

    @Override
    public Setting<?,?> createSettting() throws IllegalArgumentException {
        return new DefaultSetting<AcceptorKind,Null>(this);
    }

    @Override
    public Setting<?,?> createSetting(Object content) throws IllegalArgumentException {
        return new DefaultSetting<AcceptorKind,Null>(this);
    }

    @Override
    public String getExplanation() {
        return this.explanation;
    }

    private final String explanation;

    @Override
    public Parser<?> parser() {
        return this.parser;
    }

    private final Parser<?> parser;
}
