/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package groove.graph;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import groove.grammar.Action.Role;
import groove.grammar.rule.MethodName.Language;
import groove.grammar.rule.MethodNameParser;
import groove.util.Groove;
import groove.util.Properties;
import groove.util.PropertyKey;
import groove.util.parse.Parser;
import groove.util.parse.StringHandler;
import groove.util.parse.StringParser;

/**
 * Specialised properties class for graphs. This can be stored as part of the
 * graph info.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GraphProperties extends Properties {
    /** Constructs an empty properties object. */
    public GraphProperties() {
        super(Key.class);
    }

    /** Constructs a properties object initialised on a given map. */
    public GraphProperties(Map<?,?> properties) {
        this();
        for (Map.Entry<?,?> e : properties.entrySet()) {
            setProperty((String) e.getKey(), (String) e.getValue());
        }
    }

    @Override
    public synchronized GraphProperties clone() {
        return new GraphProperties(this);
    }

    /** Predefined graph property keys. */
    public static enum Key implements PropertyKey<Object> {
        /** User-defined comment. */
        REMARK("remark", "One-line explanation of the rule, shown e.g. as tool tip"),

        /** Rule priority. */
        PRIORITY("priority", "Higher-priority rules are evaluated first.", Parser.natural),

        /** Rule enabledness. */
        ENABLED("enabled", "Disabled rules are never evaluated.", Parser.boolTrue),

        /** Rule injectivity. */
        INJECTIVE("injective", "<body>Flag determining if the rule is to be matched injectively. " + "<br>Disregarded if injective matching is set on the grammar level.", Parser.boolFalse),

        /** Action role. */
        ROLE("actionRole", "<body>Role of the action. Values are:" + "<li>* <i>transformer</i>: action that causes the graph to change; scheduled by the (im- or explicit) control" + "<li>- <i>forbidden</i>: forbidden graph pattern, dealt with as dictated by the violation policy" + "<li>- <i>invariant</i>: invariant graph property, dealt with as dictated by the violation policy" + "<li>- <i>condition</i>: unmodifying, parameterless action, checked at every state", new Parser.EnumParser<>(Role.class, null)),

        /** Match filter. */
        FILTER("matchFilter", "<body>Boolean method or predicate that filters the matches of the rule. A match is only considered if the method returns <code>true</code>.<br>" + "Format: <tt>lang:name</tt> where the optional <tt>lang</tt> is the name of a language (by default Java) and <tt>name</tt> the fully qualified method name.<br>" + "The method may optionally take parameters of type <tt>groove.grammar.host.HostGraph</tt> and <tt>groove.transform.RuleEvent</tt><br/>" + "Supported languages are: <tt>" + Groove.toString(Language.values(), "", "", ", ") + "</tt>", MethodNameParser.instance()),

        /** Output line format. */
        FORMAT("printFormat", "<body>If nonempty, is printed on <tt>System.out</tt> upon every rule application. " + "<br>Optional format parameters as in <tt>String.format</tt> are instantiated with rule parameters.", StringParser.identity()),

        /** Alternative transition label. */
        TRANSITION_LABEL("transitionLabel", "<body>String to be used as the transition label in the LTS. " + "<p>If empty, defaults to the rule name." + "<br>Optional format parameters as in <tt>String.format</tt> are instantiated with rule parameters."),

        /** Graph version. */
        VERSION("$version", "Graph version");

        /**
         * Constructor for a key with a plain string value
         * @param name name of the key; should be an identifier possibly prefixed by #SYSTEM_KEY_PREFIX
         * @param explanation short explanation of the meaning of the key
         */
        private Key(String name, String explanation) {
            this(name, null, explanation, null);
        }

        /**
         * Constructor for a key with values parsed by a given parser
         * @param name name of the key; should be an identifier possibly prefixed by #SYSTEM_KEY_PREFIX
         * @param explanation short explanation of the meaning of the key
         * @param parser parser for values for this key; if {@code null},
         * {@link StringParser#identity()} is used
         */
        private Key(String name, String explanation, Parser<?> parser) {
            this(name, null, explanation, parser);
        }

        /**
         * Constructor for a key with a plain string value
         * @param name name of the key; should be an identifier possibly prefixed by #SYSTEM_KEY_PREFIX
         * @param keyPhrase user-readable version of the name; if {@code null},
         * the key phrase is constructed from {@code name}
         * @param explanation short explanation of the meaning of the key
         */
        private Key(String name, String keyPhrase, String explanation, Parser<?> parser) {
            this.name = name;
            this.system = name.startsWith(SYSTEM_KEY_PREFIX);
            if (keyPhrase == null) {
                String properName = name.substring(this.system ? SYSTEM_KEY_PREFIX.length() : 0);
                this.keyPhrase = StringHandler.unCamel(properName, false);
            } else {
                this.keyPhrase = keyPhrase;
            }
            this.explanation = explanation;
            this.parser = parser == null ? StringParser.identity() : parser;
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

        @Override
        public boolean isSystem() {
            return this.system;
        }

        private final boolean system;

        @Override
        public String getKeyPhrase() {
            return this.keyPhrase;
        }

        private final String keyPhrase;

        @Override
        public Parser<?> parser() {
            return this.parser;
        }

        private final Parser<?> parser;

        /** Indicates if a given string corresponds to a property key. */
        static public boolean isKey(String key) {
            return keyMap.containsKey(key);
        }

        /** Mapping from graph property key names to keys. */
        private final static Map<String,Key> keyMap;

        static {
            Map<String,Key> keys = new LinkedHashMap<>();
            for (Key key : Key.values()) {
                keys.put(key.getName(), key);
            }
            keyMap = Collections.unmodifiableMap(keys);
        }
    }
}
