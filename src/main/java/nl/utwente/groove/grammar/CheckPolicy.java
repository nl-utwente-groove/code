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
package nl.utwente.groove.grammar;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import nl.utwente.groove.grammar.Action.Role;
import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.Parser;

/**
 * Policy for dealing with run-time checks,
 * i.e. for typing errors and invariant and forbidden properties.
 */
public enum CheckPolicy {
    /** No checking occurs. */
    OFF("off", "The constraint is disabled"),
    /** Violation is checked but not propagated. */
    SILENT("silent", "The constraint is tested silently"),
    /** Violation is a state error. */
    ERROR("error", "Constraint violation is a state error"),
    /** Violation removes the state. */
    REMOVE("remove", "Constraint violation causes a state to be removed"),;

    private CheckPolicy(String name, String explanation) {
        this.name = name;
        this.explanation = explanation;
    }

    /**
     * Returns the overruling policy of this and another.
     * @param other another policy; may be {@code null}.
     */
    public CheckPolicy max(CheckPolicy other) {
        if (other == null || compareTo(other) > 0) {
            return this;
        } else {
            return other;
        }
    }

    /** Returns the name of this policy. */
    public String getName() {
        return this.name;
    }

    /** Tests if this policy is suitable for a given action role.
     * @return an {@link Optional} error, if the policy is not suitable. */
    public Optional<FormatError> isFor(Role role) {
        if (this == OFF || role.isConstraint()) {
            return Optional.empty();
        } else {
            return Optional.of(new FormatError("%s is not a constraint", role));
        }
    }

    private final String name;

    /** Returns a short (capitalised) explanation of the policy. */
    public String getExplanation() {
        return this.explanation;
    }

    private final String explanation;

    /** Parser that returns a policy. */
    public static final Parser<CheckPolicy> singleParser
        = new Parser.EnumParser<>(CheckPolicy.class, ERROR);
    /** Parser that returns a policy map. */
    public static final Parser<PolicyMap> multiParser = new PolicyMapParser();

    private final static char ASSIGN_CHAR = ':';

    /** Mapping from action names to policies. */
    public static class PolicyMap extends TreeMap<QualName,CheckPolicy> {
        /**
         * Returns the policy for a given action.
         * If there is no explicit policy for the action,
         * the default is returned ({@link #ERROR} for constraints,
         * {@code null} for other actions.
         */
        public CheckPolicy get(Action key) {
            CheckPolicy result = super.get(key.getQualName());
            if (result == null && key.getRole().isConstraint()) {
                result = ERROR;
            }
            return result;
        }
    }

    private static class PolicyMapParser extends Parser.AParser<PolicyMap> {
        private PolicyMapParser() {
            super(null, new PolicyMap());
        }

        /** Creates a description of PolicyMap values. */
        @Override
        protected String createDescription() {
            StringBuilder result = new StringBuilder();
            result
                .append("A space-separated list of <i>name:value</i> pairs,<br>"
                    + "with <i>value</i> ");
            result.append(Strings.toLower(singleParser.getDescription()));
            return result.toString();
        }

        @Override
        public PolicyMap parse(String input) throws FormatException {
            PolicyMap result = new PolicyMap();
            String[] split = input.trim().split("\\s");
            for (String pair : split) {
                if (pair.length() == 0) {
                    continue;
                }
                int pos = pair.indexOf(ASSIGN_CHAR);
                if (pos < 0) {
                    throw new FormatException(
                        "Assignment character '%s' missing in substring '%s' of %s", ASSIGN_CHAR,
                        pair, input);
                }
                QualName name = QualName.parse(pair.substring(0, pos)).testValid();
                String value = pair.substring(pos + 1, pair.length());
                CheckPolicy policy = singleParser.parse(value);
                result.put(name, policy);
            }
            return result;
        }

        @Override
        public String unparse(PolicyMap value) {
            StringBuffer result = new StringBuffer();
            for (Map.Entry<QualName,CheckPolicy> e : value.entrySet()) {
                if (e.getValue() != ERROR) {
                    result.append(e.getKey());
                    result.append(ASSIGN_CHAR);
                    result.append(e.getValue().getName());
                    result.append(' ');
                }
            }
            return result.toString();
        }
    }
}
