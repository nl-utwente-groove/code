/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.explore.encode;

import static groove.io.HTMLConverter.HTML_TAG;
import static groove.io.HTMLConverter.STRONG_TAG;
import groove.explore.ParsableValue;
import groove.explore.prettyparse.SerializedParser;
import groove.explore.prettyparse.StringConsumer;
import groove.grammar.Grammar;
import groove.grammar.model.GrammarModel;
import groove.gui.dialog.ExplorationDialog;
import groove.gui.layout.SpringUtilities;
import groove.io.HTMLConverter.HTMLTag;
import groove.util.parse.FormatException;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

/**
 * <!=========================================================================>
 * A Template<A> describes the encoding of values of type A by means of a
 * Serialized that starts with a given keyword.
 * <!=========================================================================>
 * @author Maarten de Mol
 */
public abstract class Template<A> implements EncodedType<A,Serialized> {
    /** Enumerator value for the template. */
    private final ParsableValue value;
    /** Template visibility. Defaults to always visible. */
    private Visibility visibility = Visibility.ALL;

    /** The possible visibilities of templates. */
    public static enum Visibility {
        /** always visible */
        ALL,
        /** only visible in development version of GROOVE */
        DEVELOPMENT_ONLY
    }

    private final SerializedParser commandlineParser; // for the arguments only
    /** Array of argument names. */
    private final String[] argumentNames;
    /** Map of the argument types. */
    private final Map<String,EncodedType<?,String>> argumentTypes;

    /**
     * Builds the template, which consists of a keyword for the command line,
     * a name, an explanation, a parser for the arguments on the command line,
     * and an array of argument names. The types of the arguments have to be
     * set later by calls to setArgumentType().
     */
    public Template(ParsableValue value, SerializedParser commandlineParser,
        String... argumentNames) {
        this.value = value;
        this.commandlineParser = commandlineParser;
        this.argumentNames = argumentNames;
        this.argumentTypes = new TreeMap<>();
    }

    /**
     * Sets the type of an argument, which can be an arbitrary type that has a
     * String encoding.
     */
    public void setArgumentType(String name, EncodedType<?,String> type) {
        this.argumentTypes.put(name, type);
    }

    /**
     * Returns the enumeration value for which this is the template.
     */
    public ParsableValue getValue() {
        return this.value;
    }

    /**
     * Getter for the keyword.
     */
    public String getKeyword() {
        return getValue().getKeyword();
    }

    /**
     * Getter for the name.
     */
    public String getName() {
        return getValue().getName();
    }

    /**
     * Getter for the visibility flag.
     */
    public Visibility getVisibility() {
        return this.visibility;
    }

    /**
     * Setter for the visibility flag.
     */
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public String toString() {
        return "Template for " + this.value.toString();
    }

    /** Creates a serialized object based on this template,
     * in which the template arguments are instantiated by the
     * string representation of values passed in as parameters.
     * @param args the values for the template arguments; must
     * be parsable as valid arguments.
     */
    abstract public Serialized toSerialized(Object... args);

    /**
     * Creates the type-specific editor (see class TemplateEditor below).
     */
    @Override
    public EncodedTypeEditor<A,Serialized> createEditor(GrammarModel grammar) {
        return new TemplateEditor<>(grammar);
    }

    /**
     * Create a parse error message for a specific argument.
     */
    String argumentError(String argName) {
        return "Unable to parse the " + argName + " argument of " + getKeyword() + ".";
    }

    /**
     * Parses a command line argument into a <code>Serialized</code> that
     * represents this template. Returns <code>null</code> if parsing fails.
     */
    Serialized parseCommandline(String text) {
        StringConsumer stream = new StringConsumer(text);
        if (!stream.consumeLiteral(getKeyword())) {
            return null;
        }
        Serialized result = new Serialized(getKeyword());
        if (this.argumentNames.length == 0) {
            if (stream.isEmpty()) {
                return result;
            } else {
                return null;
            }
        }
        if (!stream.consumeLiteral(":")) {
            return null;
        }
        if (!this.commandlineParser.parse(stream, result)) {
            return null;
        }
        return result;
    }

    /**
     * Inverse to {@link #parseCommandline(String)}.
     * @param source the object to be converted to string; may
     * be modified as a result of this call
     */
    String toParsableString(Serialized source) {
        String result = getKeyword();
        if (this.commandlineParser != null) {
            result += ":" + this.commandlineParser.toParsableString(source);
        }
        return result;
    }

    /**
     * Returns a description of the grammar that is used to parse this template
     * on the command line. The grammar is displayed as a (pretty-printed)
     * regular expression.
     */
    String describeCommandlineGrammar() {
        StringBuffer desc = new StringBuffer();
        desc.append(getKeyword());
        if (this.argumentNames.length != 0) {
            desc.append(":");
            desc.append(this.commandlineParser.describeGrammar());
        }
        desc.append(" - ");
        desc.append(getName());
        return desc.toString();
    }

    private static final HTMLTag INFO_FONT = new HTMLTag("FONT", "color",
        ExplorationDialog.INFO_COLOR);

    /**
     * <!--------------------------------------------------------------------->
     * A TemplateEditor<A> is the type-specific editor that is associated
     * with the Template. It basically is an info panel that contains both
     * documentation for the keyword and editors for the arguments.
     * <!--------------------------------------------------------------------->
     */
    private class TemplateEditor<X> extends EncodedTypeEditor<X,Serialized> {
        private final Map<String,EncodedTypeEditor<?,String>> editors =
            new TreeMap<>();

        public TemplateEditor(GrammarModel grammar) {
            super(grammar, new SpringLayout());
            setBackground(ExplorationDialog.INFO_BG_COLOR);
            addName();
            addExplanation();
            add(Box.createRigidArea(new Dimension(0, 6)));
            addKeyword();
            addNrArguments();
            add(Box.createRigidArea(new Dimension(0, 6)));
            for (String argName : Template.this.argumentNames) {
                addArgument(argName);
            }
            SpringUtilities.makeCompactGrid(this,
                6 + Template.this.argumentNames.length,
                1,
                2,
                2,
                0,
                0);
            refresh();
        }

        @Override
        public void refresh() {
            for (EncodedTypeEditor<?,?> editor : this.editors.values()) {
                editor.refresh();
            }
        }

        private void addName() {
            add(new JLabel("<HTML><B><U><FONT color=" + ExplorationDialog.INFO_COLOR + ">"
                + Template.this.getName() + ":</FONT></U></B></HTML>"));
        }

        private void addExplanation() {
            StringBuilder text = new StringBuilder();
            text.append(getValue().getDescription());
            INFO_FONT.on(text);
            HTML_TAG.on(text);
            add(new JLabel(text.toString()));
        }

        private void addKeyword() {
            StringBuilder text = new StringBuilder();
            text.append("Keyword for commandline: ");
            text.append(STRONG_TAG.on(getKeyword()));
            text.append('.');
            INFO_FONT.on(text);
            HTML_TAG.on(text);
            add(new JLabel(text.toString()));
        }

        private void addNrArguments() {
            add(new JLabel("<HTML><FONT color=" + ExplorationDialog.INFO_COLOR
                + ">Additional arguments: <B>"
                + Integer.toString(Template.this.argumentNames.length) + "</B>"
                + ((Template.this.argumentNames.length == 0) ? "." : " (select values below).")
                + "</FONT></HTML>"));
        }

        private void addArgument(String argName) {
            EncodedTypeEditor<?,String> editor =
                Template.this.argumentTypes.get(argName).createEditor(getGrammar());
            JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            line.setBackground(ExplorationDialog.INFO_BG_COLOR);
            if (editor != null) {
                this.editors.put(argName, editor);
                line.add(editor);
            }
            line.add(Box.createRigidArea(new Dimension(5, 0)));
            line.add(new JLabel("<HTML><B><FONT color=" + ExplorationDialog.INFO_COLOR + ">("
                + argName + ")</B></HTML>"));
            add(line);
        }

        @Override
        public Serialized getCurrentValue() {
            Serialized value = new Serialized(getKeyword());
            for (Map.Entry<String,EncodedTypeEditor<?,String>> entry : this.editors.entrySet()) {
                String argValue = entry.getValue().getCurrentValue();
                if (argValue == null) {
                    return null;
                }
                value.setArgument(entry.getKey(), argValue);
            }
            return value;
        }

        @Override
        public void setCurrentValue(Serialized value) {
            if (!value.getKeyword().equals(getKeyword())) {
                return;
            }
            for (String argName : Template.this.argumentNames) {
                this.editors.get(argName).setCurrentValue(value.getArgument(argName));
            }
        }

        @Override
        public void addTemplateListener(TemplateListener listener) {
            super.addTemplateListener(listener);
            for (EncodedTypeEditor<?,?> editor : this.editors.values()) {
                editor.addTemplateListener(listener);
            }
        }

        @Override
        public void removeTemplateListener(TemplateListener listener) {
            for (EncodedTypeEditor<?,?> editor : this.editors.values()) {
                editor.removeTemplateListener(listener);
            }
            super.removeTemplateListener(listener);
        }
    }

    /**
     * <!---------------------------------------------------------------------->
     * A Template0<X> describes the encoding of values of type X by means of a
     * Serialized that starts with a given keyword and has no arguments.
     * Implements the method parse, but in turns requires the method create
     * to be defined by any concrete subclass.
     * <!---------------------------------------------------------------------->
     */
    public static abstract class Template0<X> extends Template<X> {

        /**
         * Localized creation of the Template class (with no arguments).
         */
        public Template0(ParsableValue value) {
            super(value, null);
        }

        @Override
        public X parse(Grammar rules, Serialized source) throws FormatException {
            if (!source.getKeyword().equals(getKeyword())) {
                throw new FormatException("Type mismatch between '" + source.getKeyword()
                    + "' and '" + getKeyword() + "'.");
            }
            return create();
        }

        @Override
        public Serialized toSerialized(Object... args) {
            assert args.length == 0;
            return getValue().toSerialized();
        }

        /**
         * Typed version of the parse method. To be implemented by subclass.
         */
        public abstract X create();
    }

    /**
     * <!---------------------------------------------------------------------->
     * A Template1<X,P1> describes the encoding of values of type X by means of
     * a Serialized that starts with a given keyword and has one argument of
     * type EncodedType<P1,String>.
     * Implements the method parse, but in turns requires the method create
     * to be defined by any concrete subclass.
     * <!---------------------------------------------------------------------->
     */
    public static abstract class Template1<X,P1> extends Template<X> {

        private final EncodedType<P1,String> type1;
        private final String name1;

        /**
         * Localized creation of the Template class (with 1 argument).
         */
        public Template1(ParsableValue value, SerializedParser commandlineParser, String arg1Name,
            EncodedType<P1,String> arg1Type) {
            super(value, commandlineParser, arg1Name);
            this.type1 = arg1Type;
            this.name1 = arg1Name;
            setArgumentType(arg1Name, arg1Type);
        }

        @Override
        public X parse(Grammar rules, Serialized source) throws FormatException {
            P1 v1;

            if (!source.getKeyword().equals(getKeyword())) {
                throw new FormatException("Type mismatch between '" + source.getKeyword()
                    + "' and '" + getKeyword() + "'.");
            }

            try {
                v1 = this.type1.parse(rules, source.getArgument(this.name1));
            } catch (FormatException exc) {
                exc.insert(new FormatException(argumentError(this.name1)));
                throw exc;
            }

            return create(v1);
        }

        @Override
        public Serialized toSerialized(Object... args) {
            assert args.length == 1;
            Serialized result = getValue().toSerialized();
            result.setArgument(this.name1, args[0].toString());
            return result;
        }

        /**
         * Typed version of the parse method. To be implemented by subclass.
         */
        public abstract X create(P1 arg1);
    }

    /**
     * <!---------------------------------------------------------------------->
     * A Template2<X,P1,P2> describes the encoding of values of type X by means
     * of a Serialized that starts with a given keyword and has two argument, of
     * types EncodedType<P1,String> and EncodedType<P2,String> respectively.
     * Implements the method parse, but in turns requires the method create
     * to be defined by any concrete subclass.
     * <!---------------------------------------------------------------------->
     */
    public static abstract class Template2<X,P1,P2> extends Template<X> {

        private final EncodedType<P1,String> type1;
        private final String name1;
        private final EncodedType<P2,String> type2;
        private final String name2;

        /**
         * Localized creation of the Template class (with 1 argument).
         */
        public Template2(ParsableValue value, SerializedParser commandlineParser, String arg1Name,
            EncodedType<P1,String> arg1Type, String arg2Name, EncodedType<P2,String> arg2Type) {
            super(value, commandlineParser, arg1Name, arg2Name);
            this.type1 = arg1Type;
            this.name1 = arg1Name;
            setArgumentType(arg1Name, arg1Type);
            this.type2 = arg2Type;
            this.name2 = arg2Name;
            setArgumentType(arg2Name, arg2Type);
        }

        @Override
        public X parse(Grammar rules, Serialized source) throws FormatException {
            P1 v1;
            P2 v2;

            if (!source.getKeyword().equals(getKeyword())) {
                throw new FormatException("Type mismatch between '" + source.getKeyword()
                    + "' and '" + getKeyword() + "'.");
            }

            try {
                v1 = this.type1.parse(rules, source.getArgument(this.name1));
            } catch (FormatException exc) {
                exc.insert(new FormatException(argumentError(this.name1)));
                throw exc;
            }

            try {
                v2 = this.type2.parse(rules, source.getArgument(this.name2));
            } catch (FormatException exc) {
                exc.insert(new FormatException(argumentError(this.name2)));
                throw exc;
            }

            return create(v1, v2);
        }

        @Override
        public Serialized toSerialized(Object... args) {
            assert args.length == 2;
            Serialized result = getValue().toSerialized();
            result.setArgument(this.name1, args[0].toString());
            result.setArgument(this.name2, args[1].toString());
            return result;
        }

        /**
         * Typed version of the parse method. To be implemented by subclass.
         */
        public abstract X create(P1 arg1, P2 arg2);
    }

    /**
     * <!---------------------------------------------------------------------->
     * A TemplateN<X> describes the encoding of values of type X by means
     * of a Serialized that starts with a given keyword and has n arguments, of
     * n types EncodedType<Object,String>.
     * Implements the method parse, but in turns requires the method create
     * to be defined by any concrete subclass.
     * <!---------------------------------------------------------------------->
     * This class does not perform any typing checks
     */
    public static abstract class TemplateN<X> extends Template<X> {
        private final String[] typenames;
        private final EncodedType<? extends Object,String>[] encodedtypes;

        /**
         * Localized creation of the template class (with argumentNames.length arguments)
         * It is required that argumentTypes.length==argumentNames.length
         */
        @SafeVarargs
        public TemplateN(ParsableValue value, SerializedParser commandlineParser,
            String[] argumentNames, EncodedType<? extends Object,String>... argumentTypes) {
            super(value, commandlineParser, argumentNames);
            this.typenames = argumentNames;
            this.encodedtypes = argumentTypes;
            assert this.typenames.length == this.encodedtypes.length;
            for (int i = 0; i < this.typenames.length; i++) {
                setArgumentType(this.typenames[i], this.encodedtypes[i]);
            }
        }

        @Override
        public X parse(Grammar rules, Serialized source) throws FormatException {
            Object[] v = new Object[this.typenames.length];

            if (!source.getKeyword().equals(getKeyword())) {
                throw new FormatException("Type mismatch between '" + source.getKeyword()
                    + "' and '" + getKeyword() + "'.");
            }
            for (int i = 0; i < v.length; i++) {
                try {
                    v[i] = this.encodedtypes[i].parse(rules, source.getArgument(this.typenames[i]));
                } catch (FormatException exc) {
                    exc.insert(new FormatException(argumentError(this.typenames[i])));
                    throw exc;
                }
            }
            return create(v);
        }

        @Override
        public Serialized toSerialized(Object... args) {
            assert args.length == this.typenames.length;
            Serialized result = getValue().toSerialized();
            for (int i = 0; i < this.typenames.length; i++) {
                result.setArgument(this.typenames[i], args[i].toString());
            }
            return result;
        }

        /**
         * Typed version of the parse method. To be implemented by subclass.
         */
        public abstract X create(Object[] arguments);
    }
}