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

import groove.explore.prettyparse.SerializedParser;
import groove.explore.prettyparse.StringConsumer;
import groove.gui.Simulator;
import groove.gui.dialog.ExplorationDialog;
import groove.gui.layout.SpringUtilities;
import groove.lts.GTS;
import groove.view.FormatException;

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

    private final String keyword;
    private final String name;
    private final String explanation;
    private final SerializedParser commandlineParser; // for the arguments only
    private final String[] argumentNames;
    private final Map<String,EncodedType<?,String>> argumentTypes;

    /**
     * Builds the template, which consists of a keyword for the command line, 
     * a name, an explanation, a parser for the arguments on the command line,
     * and an array of argument names. The types of the arguments have to be
     * set later by calls to setArgumentType().
     */
    public Template(String keyword, String name, String explanation,
            SerializedParser commandlineParser, String... argumentNames) {
        this.keyword = keyword;
        this.name = name;
        this.explanation = explanation;
        this.commandlineParser = commandlineParser;
        this.argumentNames = argumentNames;
        this.argumentTypes = new TreeMap<String,EncodedType<?,String>>();
    }

    /**
     * Sets the type of an argument, which can be an arbitrary type that has a
     * String encoding.
     */
    public void setArgumentType(String name, EncodedType<?,String> type) {
        this.argumentTypes.put(name, type);
    }

    /**
     * Getter for the keyword.
     */
    public String getKeyword() {
        return this.keyword;
    }

    /**
     * Getter for the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Creates the type-specific editor (see class TemplateEditor below).
     */
    @Override
    public EncodedTypeEditor<A,Serialized> createEditor(Simulator simulator) {
        return new TemplateEditor<A>(simulator);
    }

    /**
     * Create a parse error message for a specific argument.
     */
    public String argumentError(String argName) {
        return "Unable to parse the " + argName + " argument of "
            + getKeyword() + ".";
    }

    /**
     * Parses a command line argument into a <code>Serialized</code> that
     * represents this template. Returns <code>null</code> if parsing fails.
     */
    public Serialized parseCommandline(String text) {
        StringConsumer stream = new StringConsumer(text);
        if (!stream.consumeLiteral(this.keyword)) {
            return null;
        }
        Serialized result = new Serialized(this.keyword);
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
     * Returns a description of the grammar that is used to parse this template
     * on the command line. The grammar is displayed as a (pretty-printed)
     * regular expression. 
     */
    public String describeCommandlineGrammar() {
        StringBuffer desc = new StringBuffer();
        desc.append(this.keyword);
        if (this.argumentNames.length != 0) {
            desc.append(":");
            desc.append(this.commandlineParser.describeGrammar());
        }
        desc.append(" - ");
        desc.append(this.name);
        return desc.toString();
    }

    /**
     * <!--------------------------------------------------------------------->
     * A TemplateEditor<A> is the type-specific editor that is associated
     * with the Template. It basically is an info panel that contains both
     * documentation for the keyword and editors for the arguments.
     * <!--------------------------------------------------------------------->
     */
    private class TemplateEditor<X> extends EncodedTypeEditor<X,Serialized> {

        private final Map<String,EncodedTypeEditor<?,String>> editors =
            new TreeMap<String,EncodedTypeEditor<?,String>>();

        public TemplateEditor(Simulator simulator) {
            super(new SpringLayout());
            setBackground(ExplorationDialog.INFO_BG_COLOR);
            addName();
            addExplanation();
            add(Box.createRigidArea(new Dimension(0, 6)));
            addKeyword();
            addNrArguments();
            add(Box.createRigidArea(new Dimension(0, 6)));
            for (String argName : Template.this.argumentNames) {
                addArgument(argName, simulator);
            }
            add(Box.createRigidArea(new Dimension(0, 400)));
            SpringUtilities.makeCompactGrid(this,
                7 + Template.this.argumentNames.length, 1, 2, 2, 0, 0);
        }

        private void addName() {
            add(new JLabel("<HTML><B><U><FONT color="
                + ExplorationDialog.INFO_COLOR + ">" + Template.this.name
                + ":</FONT></U></B></HTML>"));
        }

        private void addExplanation() {
            add(new JLabel("<HTML><FONT color=" + ExplorationDialog.INFO_COLOR
                + ">" + Template.this.explanation + "</FONT></HTML>"));
        }

        private void addKeyword() {
            add(new JLabel("<HTML><FONT color=" + ExplorationDialog.INFO_COLOR
                + ">" + "Keyword for commandline: <B>" + Template.this.keyword
                + "</B>.</FONT></HTML>"));
        }

        private void addNrArguments() {
            add(new JLabel("<HTML><FONT color="
                + ExplorationDialog.INFO_COLOR
                + ">Additional arguments: <B>"
                + Integer.toString(Template.this.argumentNames.length)
                + "</B>"
                + ((Template.this.argumentNames.length == 0) ? "."
                        : " (select values below).") + "</FONT></HTML>"));
        }

        private void addArgument(String argName, Simulator simulator) {
            this.editors.put(
                argName,
                Template.this.argumentTypes.get(argName).createEditor(simulator));

            JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            line.setBackground(ExplorationDialog.INFO_BG_COLOR);
            line.add(this.editors.get(argName));
            line.add(Box.createRigidArea(new Dimension(5, 0)));
            line.add(new JLabel("<HTML><B><FONT color="
                + ExplorationDialog.INFO_COLOR + ">(" + argName
                + ")</B></HTML>"));
            add(line);
        }

        @Override
        public Serialized getCurrentValue() {
            Serialized value = new Serialized(Template.this.keyword);
            for (String argName : Template.this.argumentNames) {
                String argValue = this.editors.get(argName).getCurrentValue();
                if (argValue == null) {
                    return null;
                }
                value.setArgument(argName, argValue);
            }
            return value;
        }

        @Override
        public void setCurrentValue(Serialized value) {
            if (!value.getKeyword().equals(Template.this.keyword)) {
                return;
            }
            for (String argName : Template.this.argumentNames) {
                this.editors.get(argName).setCurrentValue(
                    value.getArgument(argName));
            }
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
        public Template0(String keyword, String name, String explanation) {
            super(keyword, name, explanation, null);
        }

        @Override
        public X parse(GTS gts, Serialized source) throws FormatException {
            if (!source.getKeyword().equals(getKeyword())) {
                throw new FormatException("Type mismatch between '"
                    + source.getKeyword() + "' and '" + getKeyword() + "'.");
            }
            return create(gts);
        }

        /**
         * Typed version of the parse method. To be implemented by subclass.
         */
        public abstract X create(GTS gts);
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
        public Template1(String keyword, String name, String explanation,
                SerializedParser commandlineParser, String arg1Name,
                EncodedType<P1,String> arg1Type) {
            super(keyword, name, explanation, commandlineParser, arg1Name);
            this.type1 = arg1Type;
            this.name1 = arg1Name;
            setArgumentType(arg1Name, arg1Type);
        }

        @Override
        public X parse(GTS gts, Serialized source) throws FormatException {
            P1 v1;

            if (!source.getKeyword().equals(getKeyword())) {
                throw new FormatException("Type mismatch between '"
                    + source.getKeyword() + "' and '" + getKeyword() + "'.");
            }

            try {
                v1 = this.type1.parse(gts, source.getArgument(this.name1));
            } catch (FormatException exc) {
                exc.insert(new FormatException(argumentError(this.name1)));
                throw exc;
            }

            return create(gts, v1);
        }

        /**
         * Typed version of the parse method. To be implemented by subclass.
         */
        public abstract X create(GTS gts, P1 arg1);
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
        public Template2(String keyword, String name, String explanation,
                SerializedParser commandlineParser, String arg1Name,
                EncodedType<P1,String> arg1Type, String arg2Name,
                EncodedType<P2,String> arg2Type) {
            super(keyword, name, explanation, commandlineParser, arg1Name,
                arg2Name);
            this.type1 = arg1Type;
            this.name1 = arg1Name;
            setArgumentType(arg1Name, arg1Type);
            this.type2 = arg2Type;
            this.name2 = arg2Name;
            setArgumentType(arg2Name, arg2Type);
        }

        @Override
        public X parse(GTS gts, Serialized source) throws FormatException {
            P1 v1;
            P2 v2;

            if (!source.getKeyword().equals(getKeyword())) {
                throw new FormatException("Type mismatch between '"
                    + source.getKeyword() + "' and '" + getKeyword() + "'.");
            }

            try {
                v1 = this.type1.parse(gts, source.getArgument(this.name1));
            } catch (FormatException exc) {
                exc.insert(new FormatException(argumentError(this.name1)));
                throw exc;
            }

            try {
                v2 = this.type2.parse(gts, source.getArgument(this.name2));
            } catch (FormatException exc) {
                exc.insert(new FormatException(argumentError(this.name2)));
                throw exc;
            }

            return create(gts, v1, v2);
        }

        /**
         * Typed version of the parse method. To be implemented by subclass.
         */
        public abstract X create(GTS gts, P1 arg1, P2 arg2);
    }
}