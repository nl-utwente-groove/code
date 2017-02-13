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

import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

import groove.explore.config.BoundSetting;
import groove.explore.config.CostSetting;
import groove.explore.config.ExploreKey;
import groove.explore.config.FrontierSizeKind;
import groove.explore.config.GoalSetting;
import groove.explore.config.HeuristicSetting;
import groove.explore.config.ResultTypeKind;
import groove.explore.config.Setting;
import groove.explore.config.SuccessorKind;
import groove.explore.config.TraverseKind;
import groove.util.Exceptions;
import groove.util.parse.FormatException;
import groove.util.parse.StringHandler;

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

    /** Returns the currently set traversal strategy. */
    public TraverseKind getTraversal() {
        return (TraverseKind) this.pars.get(ExploreKey.TRAVERSE);
    }

    /** Sets the traversal strategy to a non-{@code null} value. */
    public void setTraversal(TraverseKind order) {
        this.pars.put(ExploreKey.TRAVERSE, order);
    }

    /** Returns the currently set successor strategy. */
    public SuccessorKind getSuccessor() {
        return (SuccessorKind) this.pars.get(ExploreKey.SUCCESSOR);
    }

    /** Returns the goal setting of this configuration. */
    public GoalSetting getGoal() {
        return (GoalSetting) this.pars.get(ExploreKey.GOAL);
    }

    /** Returns the maximum size of the frontier, or {@code 0} if there is no maximum. */
    public int getFrontierSize() {
        Setting<?,?> setting = this.pars.get(ExploreKey.FRONTIER_SIZE);
        switch ((FrontierSizeKind) setting.getKind()) {
        case BEAM:
            return (Integer) setting.getContent();
        case COMPLETE:
            return 0;
        case SINGLE:
            return 1;
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    /** Returns the cost setting of this configuration. */
    public CostSetting getCost() {
        return (CostSetting) get(ExploreKey.COST);
    }

    /** Returns the heuristic setting of this configuration. */
    public HeuristicSetting getHeuristic() {
        return (HeuristicSetting) get(ExploreKey.HEURISTIC);
    }

    /** Returns the bound setting of this configuration. */
    public BoundSetting getBound() {
        return (BoundSetting) get(ExploreKey.BOUND);
    }

    /** Returns the result type according to this configuration. */
    public ResultTypeKind getResultType() {
        return (ResultTypeKind) get(ExploreKey.RESULT_TYPE);
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
            if (key.parser()
                .isDefault(setting)) {
                continue;
            }
            result.append(OPTION);
            StringBuilder arg = new StringBuilder();
            arg.append(key.getName());
            arg.append(SEPARATOR);
            arg.append(key.parser()
                .toParsableString(setting));
            if (arg.indexOf(" ") > 0) {
                result.append(StringHandler.toQuoted(arg.toString(), '"'));
            } else {
                result.append(arg);
            }
            result.append(" ");
        }
        return result.toString()
            .trim();
    }

    /** Converts this configuration into a properties map. */
    public Properties getProperties() {
        Properties result = new Properties();
        for (Map.Entry<ExploreKey,Setting<?,?>> e : this.pars.entrySet()) {
            ExploreKey key = e.getKey();
            Setting<?,?> setting = e.getValue();
            if (!key.parser()
                .isDefault(setting)) {
                result.setProperty(key.getName(), key.parser()
                    .toParsableString(setting));
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
                put(key, key.parser()
                    .parse(value));
            } catch (FormatException exc) {
                // skip this key
            }
        }
    }

    @Override
    public String toString() {
        return "ExploreConfig:" + this.pars.values();
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
