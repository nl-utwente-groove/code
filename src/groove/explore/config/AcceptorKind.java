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

import groove.util.Parser;

/**
 * Key determining which states are accepted as results.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum AcceptorKind implements SettingKey {
    /** Final states. */
    FINAL("final", NullContent.PARSER),
    /** States satisfying a graph condition. */
    CONDITION("condition", StringContent.PARSER) {
        @Override
        public Setting<?,?> createSettting() throws IllegalArgumentException {
            throw new IllegalArgumentException();
        }

        @Override
        public Setting<?,?> createSetting(SettingContent content) throws IllegalArgumentException {
            return new DefaultSetting<AcceptorKind,SettingContent>(this, content);
        }
    },
    /** States satisfying a propositional formula. */
    FORMULA("formula", StringContent.PARSER) {
        @Override
        public Setting<?,?> createSettting() throws IllegalArgumentException {
            throw new IllegalArgumentException();
        }

        @Override
        public Setting<?,?> createSetting(SettingContent content) throws IllegalArgumentException {
            return new DefaultSetting<AcceptorKind,SettingContent>(this, content);
        }
    },
    /** All states. */
    ANY("any", NullContent.PARSER),
    /** No states. */
    NONE("none", NullContent.PARSER), ;

    private AcceptorKind(String name, Parser<? extends SettingContent> parser) {
        this.name = name;
        this.parser = parser;
    }

    /** Returns the name of this search order. */
    @Override
    public String getName() {
        return this.name;
    }

    private final String name;

    @Override
    public Setting<?,?> createSettting() throws IllegalArgumentException {
        return new DefaultSetting<AcceptorKind,NullContent>(this);
    }

    @Override
    public Setting<?,?> createSetting(SettingContent content) throws IllegalArgumentException {
        return new DefaultSetting<AcceptorKind,NullContent>(this);
    }

    @Override
    public String getExplanation() {
        return "Condition for result states";
    }

    @Override
    public Parser<? extends SettingContent> parser() {
        return this.parser;
    }

    private final Parser<? extends SettingContent> parser;

    @Override
    public SettingContent getDefaultValue() {
        return this.parser.getDefaultValue();
    }

    @Override
    public boolean isValue(Object value) {
        return this.parser.isValue(value);
    }
}
