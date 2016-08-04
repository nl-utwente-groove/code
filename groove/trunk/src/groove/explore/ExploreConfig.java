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
import groove.explore.config.Setting;
import groove.explore.config.TraverseKind;
import groove.util.parse.FormatException;
import groove.util.parse.StringHandler;

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
        this.pars = new EnumMap<>(ExploreKey.class);
        for (ExploreKey key : ExploreKey.values()) {
            this.pars.put(key, key.getDefaultValue());
        }
    }

    /** Returns the currently set search strategy. */
    public TraverseKind getStrategy() {
        return (TraverseKind) this.pars.get(ExploreKey.TRAVERSE);
    }

    /** Sets the search strategy to a non-{@code null} value. */
    public void setStrategy(TraverseKind order) {
        this.pars.put(ExploreKey.TRAVERSE, order);
    }

    /** Returns the current setting for a given key. */
    public Setting<?,?> get(ExploreKey key) {
        return this.pars.get(key);
    }

    /** Changes the setting for a given key. */
    public Setting<?,?> put(ExploreKey key, Setting<?,?> value) {
        return this.pars.put(key, value);
    }

    /** Parameter map of this configuration. */
    private final Map<ExploreKey,Setting<?,?>> pars;

    /** Converts this properties object to a command-line string. */
    public String toCommandLine() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<ExploreKey,Setting<?,?>> e : this.pars.entrySet()) {
            ExploreKey key = e.getKey();
            Setting<?,?> setting = e.getValue();
            if (key.parser().isDefault(setting)) {
                continue;
            }
            result.append(OPTION);
            StringBuilder arg = new StringBuilder();
            arg.append(key.getName());
            arg.append(SEPARATOR);
            arg.append(key.parser().toParsableString(setting));
            if (arg.indexOf(" ") > 0) {
                result.append(StringHandler.toQuoted(arg.toString(), '"'));
            } else {
                result.append(arg);
            }
            result.append(" ");
        }
        return result.toString().trim();
    }

    /** Converts this configuration into a properties map. */
    public Properties getProperties() {
        Properties result = new Properties();
        for (Map.Entry<ExploreKey,Setting<?,?>> e : this.pars.entrySet()) {
            ExploreKey key = e.getKey();
            Setting<?,?> setting = e.getValue();
            if (!key.parser().isDefault(setting)) {
                result.setProperty(key.getName(), key.parser().toParsableString(setting));
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
            try {
                put(key, key.parser().parse(value));
            } catch (FormatException exc) {
                // skip this key
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

    private final static String OPTION = "-S ";
    private final static String SEPARATOR = "=";
}
