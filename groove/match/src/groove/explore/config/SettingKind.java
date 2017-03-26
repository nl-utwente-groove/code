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

import groove.util.parse.Parser;

/**
 * Setting key with no further parameters, so the keys in themselves serve as settings.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface SettingKind<K extends Enum<K> & SettingKind<K>>
    extends SettingKey, Setting<K,Null> {
    @Override
    default public K createSetting(Object content) throws IllegalArgumentException {
        if (content != null) {
            throw new IllegalArgumentException();
        }
        return getKind();
    }

    @Override
    default public K getKind() {
        @SuppressWarnings("unchecked") K result = (K) this;
        return result;
    }

    @Override
    default public String getContentName() {
        return null;
    }

    @Override
    default public Null getContent() {
        return null;
    }

    @Override
    default public Parser<Null> parser() {
        return Null.PARSER;
    }
}
