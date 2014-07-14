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

import groove.algebra.AlgebraFamily;
import groove.util.Parser;

/**
 * Kind of exploration strategies.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum AlgebraKind implements SettingKey, Setting<AlgebraKind,NullContent> {
    /** Depth-first search. */
    DEFAULT(AlgebraFamily.DEFAULT),
    /** Breadth-first search. */
    BIG(AlgebraFamily.BIG),
    /** Linear search. */
    POINT(AlgebraFamily.POINT),
    /** Best-first search, driven by some heuristic. */
    TERM(AlgebraFamily.TERM), ;

    private AlgebraKind(AlgebraFamily family) {
        this.family = family;
    }

    /** Returns the algebra family. */
    public AlgebraFamily getFamily() {
        return this.family;
    }

    private final AlgebraFamily family;

    /** Returns the name of this algebra. */
    @Override
    public String getName() {
        return getFamily().getName();
    }

    @Override
    public SettingList getDefaultSetting() {
        return SettingList.single(createSetting(getDefaultValue()));
    }

    @Override
    public AlgebraKind createSettting() {
        return this;
    }

    @Override
    public AlgebraKind createSetting(SettingContent content) throws IllegalArgumentException {
        if (content != null) {
            throw new IllegalArgumentException();
        }
        return this;
    }

    @Override
    public AlgebraKind getKind() {
        return this;
    }

    @Override
    public NullContent getContent() {
        return null;
    }

    @Override
    public SettingList wrap() {
        return SettingList.single(this);
    }

    @Override
    public String getExplanation() {
        return getFamily().getExplanation();
    }

    @Override
    public Parser<? extends SettingContent> parser() {
        return NullContent.PARSER;
    }

    @Override
    public SettingContent getDefaultValue() {
        return null;
    }

    @Override
    public boolean isValue(Object value) {
        return value == null;
    }

    @Override
    public Class<NullContent> getContentType() {
        return NullContent.class;
    }
}
