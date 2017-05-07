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

/** The matching strategy. */
public enum MatchKind implements SettingKey {
    /** Search plan-based matching. */
    PLAN("plan", "Match hint", "Search plan-based matching", MatchHint.PARSER),
    /** RETE-based incremental matching. */
    RETE("rete", null, "Incremental (rete-based) matching", null),;

    private MatchKind(String name, String contentName, String explanation,
        Parser<MatchHint> parser) {
        this.name = name;
        this.contentName = contentName;
        this.explanation = explanation;
        this.parser = parser == null ? NullParser.instance(MatchHint.class) : parser;
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
    public String getExplanation() {
        return this.explanation;
    }

    private final String explanation;

    @Override
    public Parser<MatchHint> parser() {
        return this.parser;
    }

    private final Parser<MatchHint> parser;

    @Override
    public Setting<MatchKind,MatchHint> getDefaultSetting() {
        return createSetting(getDefaultValue());
    }

    @Override
    public Setting<MatchKind,MatchHint> createSettting() throws IllegalArgumentException {
        return createSetting(null);
    }

    @Override
    public Setting<MatchKind,MatchHint> createSetting(Object content)
        throws IllegalArgumentException {
        if (!isValue(content)) {
            throw new IllegalArgumentException(
                String.format("'%s' is not a valid value for '%s'", content, this));
        }
        return new DefaultSetting<>(this, (MatchHint) content);
    }
}
