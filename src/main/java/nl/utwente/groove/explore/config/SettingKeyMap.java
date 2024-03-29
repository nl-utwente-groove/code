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
package nl.utwente.groove.explore.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapping from setting key names to the corresponding keys.
 * The mapping is normalised to lowercase.
 * @author Arend Rensink
 * @version $Revision$
 */
public class SettingKeyMap {
    /** Creates a map initialised to a given setting key. */
    public SettingKeyMap(Class<? extends Setting.Key> keyType) {
        for (var kind : keyType.getEnumConstants()) {
            put(kind.getName(), kind);
        }
    }

    /** Inserts a name-value pair into the map. */
    public void put(String name, Setting.Key kind) {
        this.map.put(name.toLowerCase(), kind);
    }

    /** Returns the setting kind corresponding to a given name. */
    public Setting.Key get(String name) {
        return this.map.get(name.toLowerCase());
    }

    private final Map<String,Setting.Key> map = new HashMap<>();
}
