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
 * @author Arend Rensink
 * @version $Revision $
 */
public enum ResultCountKey implements SettingKey {
    /** The single key, wrapping a natural number that is the actual count. */
    COUNT("count", "Number of results before halting; 0 means unbounded", IntContent.NAT_PARSER), ;

    private ResultCountKey(String name, String explanation, Parser<IntContent> parser) {
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
    public Parser<IntContent> parser() {
        return this.parser;
    }

    private final Parser<IntContent> parser;

    @Override
    public SettingContent getDefaultValue() {
        return parser().getDefaultValue();
    }

    @Override
    public boolean isValue(Object value) {
        return parser().isValue(value);
    }

    @Override
    public SettingList getDefaultSetting() {
        return SettingList.single(createSetting(getDefaultValue()));
    }

    @Override
    public Setting<ResultCountKey,IntContent> createSettting() throws IllegalArgumentException {
        return createSetting(null);
    }

    @Override
    public Setting<ResultCountKey,IntContent> createSetting(SettingContent content)
        throws IllegalArgumentException {
        if (!isValue(content)) {
            throw new IllegalArgumentException();
        }
        return new DefaultSetting<ResultCountKey,IntContent>(this, (IntContent) content);
    }
}
