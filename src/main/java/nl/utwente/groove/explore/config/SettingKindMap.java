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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Mapping from setting kind names to the corresponding kinds.
 * The mapping is normalised to lowercase.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class SettingKindMap {
    /** Creates a map initialised to the constants of a given kind type. */
    public SettingKindMap(Class<? extends Setting.Kind> kindType) {
        for (var kind : kindType.getEnumConstants()) {
            put(kind.getName(), kind);
        }
    }

    /** Inserts a name-kind pair into the map. */
    public void put(String name, Setting.Kind kind) {
        this.map.put(name.toLowerCase(), kind);
    }

    /** Returns the setting kind corresponding to a given name, if any. */
    public Setting.@Nullable Kind get(String name) {
        return this.map.get(name.toLowerCase());
    }

    private final Map<String,Setting.Kind> map = new HashMap<>();
}
