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
package groove.grammar;

import groove.util.ExprParser;
import groove.util.Parser;

import java.util.Map;
import java.util.TreeMap;

/**
 * Policy for dealing with run-time checks,
 * i.e. for typing errors and invariant and forbidden properties.
 */
public enum CheckPolicy {
    /** No checking occurs. */
    OFF("off"),
    /** Violation is checked but not propagated. */
    SILENT("silent"),
    /** Violation is a state error. */
    ERROR("error"),
    /** Violation removes the state. */
    REMOVE("remove"), ;

    private CheckPolicy(String name) {
        this.name = name;
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

    private final String name;

    /** Parser that returns a policy. */
    public static final Parser<CheckPolicy> singleParser = new Parser.EnumParser<CheckPolicy>(
        CheckPolicy.class, ERROR);
    /** Parser that returns a policy map. */
    public static final Parser<PolicyMap> multiParser = new PolicyMapParser();

    private final static char ASSIGN_CHAR = '=';

    /** Mapping from action names to policies. */
    public static class PolicyMap extends TreeMap<String,CheckPolicy> {
        /**
         * Returns the policy for a given action.
         * If there is no explicit policy for the action,
         * the default is returned ({@link #ERROR} for constraints,
         * {@code null} for other actions.
         */
        public CheckPolicy get(Action key) {
            CheckPolicy result = super.get(key);
            if (result == null && key.getRole().isConstraint()) {
                result = ERROR;
            }
            return result;
        }
    }

    private static class PolicyMapParser implements Parser<CheckPolicy.PolicyMap> {
        @Override
        public String getDescription(boolean uppercase) {
            StringBuilder result = new StringBuilder(uppercase ? "A " : "a ");
            result.append("space-separated list of <i>name=value</i> pairs<br>"
                + "with <i>value</i> ");
            result.append(singleParser.getDescription(false));
            return result.toString();
        }

        @Override
        public boolean accepts(String text) {
            return parse(text) != null;
        }

        @Override
        public PolicyMap parse(String text) {
            PolicyMap result = new PolicyMap();
            if (text != null) {
                String[] split = text.trim().split("\\s");
                for (String pair : split) {
                    int pos = pair.indexOf(ASSIGN_CHAR);
                    if (pos < 0) {
                        result = null;
                        break;
                    }
                    String name = pair.substring(0, pos);
                    String value = pair.substring(pos + 1, pair.length());
                    CheckPolicy policy = singleParser.parse(value);
                    if (!ExprParser.isIdentifier(name) || policy == null) {
                        result = null;
                        break;
                    }
                    result.put(name, policy);
                }
            }
            return result;
        }

        @Override
        public String toParsableString(Object value) {
            StringBuffer result = new StringBuffer();
            if (value instanceof PolicyMap) {
                for (Map.Entry<String,CheckPolicy> e : ((PolicyMap) value).entrySet()) {
                    if (e.getValue() != ERROR) {
                        result.append(e.getKey());
                        result.append(ASSIGN_CHAR);
                        result.append(e.getValue());
                        result.append(' ');
                    }
                }
            }
            return result.toString();
        }

        @Override
        public boolean isValue(Object value) {
            return value instanceof PolicyMap;
        }

        @Override
        public PolicyMap getDefaultValue() {
            return EMPTY;
        }

        @Override
        public String getDefaultString() {
            return "";
        }

        @Override
        public boolean isDefault(Object value) {
            return value instanceof PolicyMap && ((PolicyMap) value).isEmpty();
        }

        private final static PolicyMap EMPTY = new PolicyMap();
    }
}
