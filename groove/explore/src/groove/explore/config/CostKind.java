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
 * $Id: TraverseKind.java 5811 2016-10-26 15:31:09Z rensink $
 */
package groove.explore.config;

/**
 * Cost function for paths.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum CostKind implements SettingKind<CostKind> {
    /** Zero rule cost. */
    NONE("None", "Paths are ignored"),
    /** Breadth-first search. */
    UNIFORM("Uniform", "Path length"),
    /** Random selection. */
    RULE("Rule-based", "Rule-specific cost"),;

    private CostKind(String name, String explanation) {
        this.name = name;
        this.explanation = explanation;
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
}
