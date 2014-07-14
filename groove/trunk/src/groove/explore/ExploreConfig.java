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
package groove.explore;

import groove.explore.config.ExploreKey;
import groove.explore.config.SettingList;
import groove.explore.config.StrategyKey;

import java.util.EnumMap;
import java.util.Map;

/**
 * Collection of all properties influencing the exploration of a GTS.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ExploreConfig {
    /** Creates a configuration with all default values. */
    public ExploreConfig() {
        this.pars = new EnumMap<ExploreKey,SettingList>(ExploreKey.class);
        for (ExploreKey key : ExploreKey.values()) {
            this.pars.put(key, key.getDefaultValue());
        }
    }

    /** Returns the currently set search strategy. */
    public StrategyKey getStrategy() {
        return (StrategyKey) this.pars.get(ExploreKey.STRATEGY).single();
    }

    /** Sets the search strategy to a non-{@code null} value. */
    public void setStrategy(StrategyKey order) {
        this.pars.put(ExploreKey.STRATEGY, SettingList.single(order));
    }

    /** Returns the current setting for a given key. */
    public SettingList get(ExploreKey key) {
        return this.pars.get(key);
    }

    /** Changes the setting for a given key. */
    public SettingList put(ExploreKey key, SettingList value) {
        return this.pars.put(key, value);
    }

    /** Parameter map of this configuration. */
    private final Map<ExploreKey,SettingList> pars;

    @Override
    public String toString() {
        return "ExploreConfig[" + this.pars + "]";
    }

    @Override
    public int hashCode() {
        return this.pars.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ExploreConfig other = (ExploreConfig) obj;
        return this.pars.equals(other.pars);
    }
}
