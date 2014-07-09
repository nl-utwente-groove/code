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

/** The matching strategy. */
public enum MatchKind implements SettingKey {
    /** Search plan-based matching. */
    PLAN("plan", "Search plan-based matching", MatchHint.PARSER),
    /** RETE-based incremental matching. */
    RETE("rete", "Incremental (rete-based) matching", NullContent.PARSER), ;

    private MatchKind(String name, String explanation, Parser<? extends SettingContent> parser) {
        this.name = name;
        this.explanation = explanation;
        this.parser = parser;
    }

    @Override
    public String getName() {
        return this.name;
    }

    private final String name;

    @Override
    public String getExplanation() {
        return this.explanation;
    }

    private final String explanation;

    @Override
    public Parser<? extends SettingContent> parser() {
        return this.parser;
    }

    private final Parser<? extends SettingContent> parser;

    @Override
    public SettingContent getDefaultValue() {
        return parser().getDefaultValue();
    }

    @Override
    public boolean isValue(Object value) {
        return parser().isValue(value);
    }

    @Override
    public Setting<?,?> createSettting() throws IllegalArgumentException {
        return createSetting(null);
    }

    @Override
    public Setting<?,?> createSetting(SettingContent content) throws IllegalArgumentException {
        if (!isValue(content)) {
            throw new IllegalArgumentException();
        }
        return new DefaultSetting<MatchKind,SettingContent>(this, content);
    }
}
