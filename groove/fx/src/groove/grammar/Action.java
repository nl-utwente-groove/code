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

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import groove.gui.look.Values;
import groove.io.HTMLConverter;
import groove.io.HTMLConverter.HTMLTag;
import groove.util.collect.Comparator;

/**
 * Supertype of the actions in a rule system.
 * @author Arend Rensink
 * @version $Revision $
 */
public interface Action extends Callable, Comparable<Action> {
    /** Indicates if this action serves to test a property of a graph.
     * Convenience method for {@code getRole().isProperty()}.
     * @see Role#isProperty()
     */
    public boolean isProperty();

    /** Indicates if this is a partial action.
     * A partial action is a rule that serves as a sub-rule of some recipe.
     */
    public boolean isPartial();

    /**
     * Returns the priority of the action.
     */
    public int getPriority();

    /**
     * Returns the label to be used in the LTS when this rule is applied.
     * Defaults to the rule name, if the property is undefined.
     */
    public String getTransitionLabel();

    /**
     * Returns a format string for the standard output.
     * Whenever a transition with this action is added to a GTS, a
     * corresponding string is sent to the standard output.
     */
    public String getFormatString();

    /** Returns the grammar properties for this action. */
    public GrammarProperties getGrammarProperties();

    /**
     * Returns the action kind of this action.
     * @return the action kind; cannot be {@link Callable.Kind#FUNCTION}.
     */
    @Override
    public Kind getKind();

    /**
     * Returns the role of this action.
     * @return the action role.
     */
    public Role getRole();

    /**
     * If this action is an invariant or forbidden property,
     * returns the consequence of its violation.
     */
    public CheckPolicy getPolicy();

    /**
     * A comparator for priorities, encoded as {@link Integer} objects. This
     * implementation orders priorities from high to low.
     */
    public static final Comparator<Integer> PRIORITY_COMPARATOR = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2.intValue() - o1.intValue();
        }

    };

    /**
     * A comparator for actions. This
     * implementation orders priorities from high to low, and within priority,
     * according to the action (full) name.
     */
    public static final Comparator<Action> ACTION_COMPARATOR = new Comparator<Action>() {
        @Override
        public int compare(Action o1, Action o2) {
            if (o1 == o2) {
                return 0;
            }
            int result = compare(o2.isProperty(), o1.isProperty());
            if (result != 0) {
                return result;
            }
            result = o2.getPriority() - o1.getPriority();
            if (result != 0) {
                return result;
            }
            result = o1.getQualName()
                .compareTo(o2.getQualName());
            return result;
        }

    };

    /** A comparator for actions that orders all non-partial rules before
     * partial rules, and otherwise behaves like @{link #ACTION_COMPARATOR}.
     */
    public static final Comparator<Action> PARTIAL_COMPARATOR = new Comparator<Action>() {
        @Override
        public int compare(Action o1, Action o2) {
            int result = compare(!o1.isPartial(), !o2.isPartial());
            if (result != 0) {
                return result;
            }
            return ACTION_COMPARATOR.compare(o1, o2);
        }
    };

    /**
     * The lowest rule priority, which is also the default value if no explicit
     * priority is given.
     */
    public static final int DEFAULT_PRIORITY = 0;

    /** Role of the action within the grammar. */
    public static enum Role {
        /** Action that modifies a graph. */
        TRANSFORMER("transformer", null),
        /** Action that captures a forbidden graph property. */
        FORBIDDEN("forbidden", Values.FORBIDDEN_COLOR),
        /** Action that captures an invariant graph property. */
        INVARIANT("invariant", Values.INVARIANT_COLOR),
        /** Action that captures a general graph condition. */
        CONDITION("condition", null),;

        private Role(String text, Color color) {
            this.text = text;
            this.color = color;
        }

        /** Indicates if this role is anything but {@link #TRANSFORMER}. */
        public boolean isProperty() {
            return this != TRANSFORMER;
        }

        /** Indicates if this role is {@link #FORBIDDEN} or {@link #INVARIANT}. */
        public boolean isConstraint() {
            return this == FORBIDDEN || this == INVARIANT;
        }

        @Override
        public String toString() {
            return this.text;
        }

        /** Returns the name of this role, with the first letter optionally capitalised. */
        public String text(boolean capitalised) {
            if (capitalised) {
                StringBuffer result = new StringBuffer(toString());
                result.setCharAt(0, Character.toUpperCase(result.charAt(0)));
                return result.toString();
            } else {
                return toString();
            }
        }

        private final String text;

        /** Indicates if this role has a special colour. */
        public boolean hasColor() {
            return getColor() != null;
        }

        /** Returns the special colour for actions with this role;
         * if {@code null}, there is no special colour.
         */
        public Color getColor() {
            return this.color;
        }

        private final Color color;

        /**
         * Returns an HTML tag for the colour of this role, if the role has a special colour;
         * {@code null} otherwise.
         * @see #getColor()
         */
        public HTMLTag getColorTag() {
            if (this.colorTag == null && hasColor()) {
                this.colorTag = HTMLConverter.createColorTag(getColor());
            }
            return this.colorTag;
        }

        private HTMLTag colorTag;

        /** Returns the role corresponding to a given string,
         * or {@code null} if the string does not denote a role.
         */
        static public Role toRole(String text) {
            if (roleMap == null) {
                roleMap = new HashMap<>();
                for (Role role : Role.values()) {
                    roleMap.put(role.toString(), role);
                }
            }
            return roleMap.get(text);
        }

        static private Map<String,Role> roleMap;
    }
}
