/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.annotation;

import static groove.io.HTMLConverter.HTML_LINEBREAK;
import static groove.io.HTMLConverter.HTML_TAG;
import static groove.io.HTMLConverter.ITALIC_TAG;
import static groove.io.HTMLConverter.STRONG_TAG;
import static groove.io.HTMLConverter.toHtml;
import groove.io.HTMLConverter;
import groove.io.HTMLConverter.HTMLTag;
import groove.util.Pair;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Class offering support for syntax help.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Help {
    /** Constructs a help item using a given token map for syntax formatting. */
    public Help(Map<String,String> tokenMap) {
        this.tokenMap = tokenMap;
    }

    /** Constructs a help item that does not perform syntax formatting. */
    public Help() {
        this.tokenMap = null;
    }

    /** Sets the syntax line of the actual documentation. */
    public void setSyntax(String syntax) {
        setSyntax(syntax, isFormatSyntax());
    }

    /**
     * Sets the syntax line of the actual documentation.
     * A flag controls if the syntax line should be parsed for
     * tokens and parameters.
     * @see #setSyntax(String)
     */
    public void setSyntax(String syntax, boolean parse) {
        if (parse) {
            Pair<String,List<String>> parsed = processTokensAndArgs(syntax, this.tokenMap);
            this.syntax = html(parsed.one());
            for (String parName : parsed.two()) {
                this.parNames.add(it(parName));
            }
        } else {
            this.syntax = html(syntax);
        }
    }

    /** Sets a header for the tool tip.
     * @param header the tool tip header: should be HTML-formatted
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /** Adds a line to the tool tip main body. */
    public void addBody(String text) {
        if (this.body.length() > 0) {
            this.body.append(' ');
        }
        this.body.append(text);
    }

    /** Sets the tool tip main body. */
    public void setBody(String... text) {
        setBody(Arrays.asList(text));
    }

    /** Sets to the tool tip main body. */
    public void setBody(List<String> text) {
        if (this.body.length() > 0) {
            throw new IllegalStateException(String.format("Tool tip body already set to %s",
                this.body));
        }
        for (String line : text) {
            addBody(line);
        }
    }

    /**
     * Sets the parameter names.
     * This is only allowed if the syntax description is not parsed for
     * parameters.
     */
    public void setParNames(List<String> names) {
        for (String name : names) {
            this.parNames.add(it(name));
        }
    }

    /** Sets the documentation line for the parameters. */
    public void setPars(String[] parDocs) {
        setPars(Arrays.asList(parDocs));
    }

    /** Sets the documentation line for the parameters. */
    public void setPars(List<String> parDocs) {
        this.parDocs.addAll(parDocs);
    }

    /** Adds a documentation line for the next parameter. */
    public void addPar(String parDoc) {
        if (!isFormatSyntax()) {
            throw new IllegalStateException(String.format("Parameter name for %s must be provided",
                parDoc));
        }
        this.parDocs.add(parDoc);
    }

    /** Returns the text of the help item. */
    public String getItem() {
        if (this.syntax == null) {
            throw new IllegalStateException("Syntax of item is not set.");
        }
        return this.syntax;
    }

    /** Returns a HTML-formatted tool tip for the help item. */
    public String getTip() {
        StringBuilder result = new StringBuilder();
        if (this.header != null) {
            result.append(bf(this.header));
            result.append(HTML_LINEBREAK);
        }
        if (this.body.length() > 0) {
            result.append(DIV_TAG.on(format(this.body.toString())));
        }
        if (!this.parDocs.isEmpty()) {
            if (this.parNames.size() != this.parDocs.size()) {
                throw new IllegalStateException(String.format(
                    "Parameter count error: %s documentation lines for %s parameters",
                    this.parDocs.size(), this.parNames.size()));
            }
            StringBuilder paramText = new StringBuilder();
            for (int p = 0; p < this.parNames.size(); p++) {
                paramText.append("<tr><th align=\"right\">");
                paramText.append(bf(this.parNames.get(p)));
                paramText.append("<td width=\"5\"><td> - ");
                paramText.append(format(this.parDocs.get(p)));
            }
            result.append(HTML_LINEBREAK);
            result.append(TABLE_TAG.on(paramText));
        }
        return result.length() == 0 ? null : HTML_TAG.on(result).toString();
    }

    private boolean isFormatSyntax() {
        return this.tokenMap != null;
    }

    private String format(String text) {
        if (isFormatSyntax()) {
            text = processTokens(text, this.tokenMap);
        }
        try {
            return String.format(text, this.parNames.toArray());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Help [syntax=" + this.syntax + ", header=" + this.header + ", body=" + this.body
            + ", parDocs=" + this.parDocs + "]";
    }

    /**
     * Creates a syntax help object from the annotations of a given class.
     */
    public static Help createHelp(Class<?> source, Map<String,String> tokenMap) {
        Syntax syntax = source.getAnnotation(Syntax.class);
        ToolTipHeader header = source.getAnnotation(ToolTipHeader.class);
        ToolTipBody body = source.getAnnotation(ToolTipBody.class);
        ToolTipPars pars = source.getAnnotation(ToolTipPars.class);
        return createHelp(source, syntax, header, body, pars, tokenMap);
    }

    /**
     * Creates a syntax help object from the annotations of a reflection object.
     */
    public static Help createHelp(AccessibleObject source, Map<String,String> tokenMap) {
        Syntax syntax = source.getAnnotation(Syntax.class);
        ToolTipHeader header = source.getAnnotation(ToolTipHeader.class);
        ToolTipBody body = source.getAnnotation(ToolTipBody.class);
        ToolTipPars pars = source.getAnnotation(ToolTipPars.class);
        return createHelp(source, syntax, header, body, pars, tokenMap);
    }

    private static Help createHelp(Object source, Syntax syntax, ToolTipHeader header,
        ToolTipBody body, ToolTipPars pars, Map<String,String> tokenMap) {
        Help result = null;
        if (syntax != null) {
            result = new Help(tokenMap);
            String syntaxText =
                String.format(syntax.value(), getReflectionName(source), getContextName(source));
            result.setSyntax(syntaxText);
            if (header != null) {
                result.setHeader(header.value());
            }
            if (body != null) {
                result.setBody(body.value());
            }
            if (pars != null) {
                result.setPars(pars.value());
            }
        }
        return result;
    }

    /** Returns the name of a given reflection object, if the object is recognised. */
    private static String getReflectionName(Object object) {
        String result = null;
        if (object instanceof Class) {
            result = ((Class<?>) object).getSimpleName();
        } else if (object instanceof Field) {
            result = ((Field) object).getName();
        } else if (object instanceof Method) {
            result = ((Method) object).getName();
        }
        return result;
    }

    /** Returns the name of the defining context of a given reflection object, if the object is recognised. */
    private static String getContextName(Object object) {
        String result = null;
        if (object instanceof Field) {
            result = ((Field) object).getDeclaringClass().getSimpleName();
        } else if (object instanceof Method) {
            result = ((Method) object).getDeclaringClass().getSimpleName();
        }
        return result;
    }

    /** Possibly {@code null} mapping from token names to token text. */
    private final Map<String,String> tokenMap;
    private final StringBuilder body = new StringBuilder();
    /** Syntax line, to be used as item. */
    private String syntax;
    /** Header of the tool tip. */
    private String header;
    /** List of parameter names. */
    private final List<String> parNames = new ArrayList<>();
    /** List of parameter documentation lines. */
    private final List<String> parDocs = new ArrayList<>();

    /**
     * Turns text into boldface by putting
     * an HTML {@code strong} tag around a string builder.
     * @see HTMLConverter#STRONG_TAG
     */
    public static StringBuilder bf(StringBuilder text) {
        return STRONG_TAG.on(text);
    }

    /**
     * Turns text into boldface by putting
     * an HTML {@code strong} tag around a string.
     * @see HTMLConverter#STRONG_TAG
     */
    public static String bf(String text) {
        return STRONG_TAG.on(text);
    }

    /**
     * Turns text into boldface by putting
     * an HTML {@code strong} tag around an object's string description.
     * @see HTMLConverter#STRONG_TAG
     */
    public static String bf(Object text) {
        return STRONG_TAG.on(text);
    }

    /**
     * Turns text into italic by putting
     * an HTML {@code i} tag around a string builder.
     * @see HTMLConverter#ITALIC_TAG
     */
    public static StringBuilder it(StringBuilder text) {
        return ITALIC_TAG.on(text);
    }

    /**
     * Turns text into italic by putting
     * an HTML {@code i} tag around a string.
     * @see HTMLConverter#ITALIC_TAG
     */
    public static String it(String text) {
        return ITALIC_TAG.on(text);
    }

    /**
     * Turns text into italic by putting
     * an HTML {@code i} tag around an object's string description.
     * @see HTMLConverter#STRONG_TAG
     */
    public static String it(Object text) {
        return ITALIC_TAG.on(text);
    }

    /** Gives text special characteristics designating it as source code. */
    public static String source(String text) {
        return SOURCE_TAG.on(text);
    }

    /**
     * Finalises html text by putting
     * an {@code html} tag around a string.
     * @see HTMLConverter#HTML_TAG
     */
    public static String html(String text) {
        return HTML_TAG.on(text);
    }

    /**
     * Transforms a string by replacing all occurrences of tokens.
     * Tokens are certain identifiers (formed according to the Java rules)
     * characterised by the a mapping to replacement text, passed in as a
     * parameter.
     * @param text the string to be transformed
     * @param tokenMap mapping from tokens to replacement text
     * @return the transformed version of {@code text}
     * @see #processTokensAndArgs(String, Map)
     */
    static private String processTokens(String text, Map<String,String> tokenMap) {
        return processTokensAndArgs(text, tokenMap, false).one();
    }

    /**
     * Transforms a string by replacing all occurrences of tokens, and
     * recognises all other identifiers as arguments.
     * Tokens are certain identifiers (formed according to the Java rules)
     * characterised by the a mapping to replacement text, passed in as a
     * parameter.
     * @param text the string to be transformed
     * @param tokenMap mapping from tokens to replacement text
     * @return the transformed version of {@code text}, paired with the
     * list of recognised arguments in the order of their occurrence in {@code text}
     */
    static private Pair<String,List<String>> processTokensAndArgs(String text,
        Map<String,String> tokenMap) {
        return processTokensAndArgs(text, tokenMap, true);
    }

    /**
     * Internal method unifying the functionality of
     * {@link #processTokens(String, Map)} and {@link #processTokensAndArgs(String, Map)}.
     */
    static private Pair<String,List<String>> processTokensAndArgs(String text,
        Map<String,String> tokenMap, boolean getArgs) {
        StringBuilder result = new StringBuilder(text);
        List<String> args = new ArrayList<>();
        for (int i = 0; i < result.length(); i++) {
            char first = result.charAt(i);
            if (getArgs && first == '.') {
                result.replace(i, i + 1, "");
                i--;
            } else if (Character.isJavaIdentifierStart(first)) {
                int start = i;
                int end = i + 1;
                while (end < result.length() && Character.isJavaIdentifierPart(result.charAt(end))) {
                    end++;
                }
                String id = result.substring(start, end);
                String token = tokenMap.get(id);
                if (token != null) {
                    id = source(bf(toHtml(token)));
                } else if (getArgs) {
                    id = source(it(id));
                    args.add(id);
                }
                result.replace(start, end, id);
                i += id.length() - 1;
            }
        }
        return Pair.newPair(result.toString(), args);
    }

    private static HTMLTag DIV_TAG = HTMLConverter.createDivTag("width: 250px;");
    static private final HTMLTag TABLE_TAG = HTMLConverter.createHtmlTag(
        HTMLConverter.TABLE_TAG_NAME, "cellpadding", "0");
    static private final HTMLTag SOURCE_TAG = HTMLConverter.createHtmlTag("font", "color", "green");
}
