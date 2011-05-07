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
import static groove.io.HTMLConverter.TABLE_TAG_NAME;
import groove.io.HTMLConverter;
import groove.io.HTMLConverter.HTMLTag;
import groove.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Class offering support for syntax help.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Help {
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

    /** 
     * Finalises html text by putting
     * an {@code html} tag around a string builder. 
     * @see HTMLConverter#HTML_TAG
     */
    public static StringBuilder html(StringBuilder text) {
        return HTML_TAG.on(text);
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
     * Composes an HTML tool tip, consisting of a
     * main body optionally preceded by one or more header lines.
     * The header lines will be set in boldface.
     */
    public static String tip(String text, String... headers) {
        return tip(text, Collections.<String,String>emptyMap(), headers);
    }

    /** 
     * Composes an HTML tool tip, consisting of a
     * main body optionally preceded by one or more header lines
     * and optionally succeded by a parameter map. 
     * The header lines will be set in boldface, and
     * the parameter map as a table with boldface first column.
     */
    public static String tip(String text, Map<String,String> pars,
            String... headers) {
        StringBuilder result = new StringBuilder();
        if (headers.length > 0 && headers[0] != null) {
            for (int i = 0; i < headers.length; i++) {
                result.append(bf(headers[i]));
                result.append(HTMLConverter.HTML_LINEBREAK);
            }
        }
        if (text.length() > 0) {
            result.append(DIV_TAG.on(text));
        }
        if (!pars.isEmpty()) {
            StringBuilder paramText = new StringBuilder();
            for (Map.Entry<String,String> parEntry : pars.entrySet()) {
                paramText.append("<tr><th align=\"right\">");
                paramText.append(parEntry.getKey());
                paramText.append("<td width=\"5\"><td> - ");
                paramText.append(parEntry.getValue());
            }
            result.append(HTML_LINEBREAK);
            result.append(TABLE_TAG.on(paramText));
        }
        return HTMLConverter.HTML_TAG.on(result).toString();
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
    static public String processTokens(String text, Map<String,String> tokenMap) {
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
    static public Pair<String,List<String>> processTokensAndArgs(String text,
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
        List<String> args = new ArrayList<String>();
        for (int i = 0; i < result.length(); i++) {
            char first = result.charAt(i);
            if (Character.isJavaIdentifierStart(first)) {
                int start = i;
                int end = i + 1;
                while (end < result.length()
                    && Character.isJavaIdentifierPart(result.charAt(end))) {
                    end++;
                }
                String id = result.substring(start, end);
                String token = tokenMap.get(id);
                if (token != null) {
                    id = token;
                } else if (getArgs) {
                    id = it(id);
                    args.add(id);
                }
                result.replace(start, end, id);
                i += id.length() - 1;
            }
        }
        return Pair.newPair(result.toString(), args);
    }

    private static HTMLTag DIV_TAG =
        HTMLConverter.createDivTag("width: 250px;");
    static private final HTMLTag TABLE_TAG = HTMLConverter.createHtmlTag(
        TABLE_TAG_NAME, "cellpadding", "0");
}
