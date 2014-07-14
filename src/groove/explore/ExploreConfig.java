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
import groove.explore.config.StrategyKind;

import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

/**
 * Collection of all properties influencing the exploration of a GTS.
 * @author Arend Rensink
 * @version $Revision $
 */
public class ExploreConfig {
    /** Creates a configuration with values taken from a given properties map. */
    public ExploreConfig(Properties props) {
        this();
        putProperties(props);
    }

    /** Creates a configuration with all default values. */
    public ExploreConfig() {
        this.pars = new EnumMap<ExploreKey,SettingList>(ExploreKey.class);
        for (ExploreKey key : ExploreKey.values()) {
            this.pars.put(key, key.getDefaultValue());
        }
    }

    /** Returns the currently set search strategy. */
    public StrategyKind getStrategy() {
        return (StrategyKind) this.pars.get(ExploreKey.STRATEGY).single();
    }

    /** Sets the search strategy to a non-{@code null} value. */
    public void setStrategy(StrategyKind order) {
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

    /** Converts this configuration into a properties map. */
    public Properties getProperties() {
        Properties result = new Properties();
        for (Map.Entry<ExploreKey,SettingList> e : this.pars.entrySet()) {
            ExploreKey key = e.getKey();
            SettingList setting = e.getValue();
            if (!key.parser().isDefault(setting)) {
                result.setProperty(key.getName(), key.parser().toParsableString(setting.single()));
            }
        }
        return result;
    }

    /**
     * Fills this configuration from a properties map.
     * Unknown keys in the properties map are ignored.
     */
    public void putProperties(Properties props) {
        for (ExploreKey key : ExploreKey.values()) {
            String value = props.getProperty(key.getName());
            if (value != null && key.parser().accepts(value)) {
                put(key, key.parser().parse(value));
            }
        }
    }

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
