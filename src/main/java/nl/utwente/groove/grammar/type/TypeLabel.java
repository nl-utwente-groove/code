/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
package nl.utwente.groove.grammar.type;

import static nl.utwente.groove.graph.EdgeRole.BINARY;
import static nl.utwente.groove.graph.EdgeRole.NODE_TYPE;

import java.util.EnumMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.algebra.Sort;
import nl.utwente.groove.grammar.rule.RuleLabel;
import nl.utwente.groove.graph.ALabel;
import nl.utwente.groove.graph.EdgeRole;
import nl.utwente.groove.graph.Label;
import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.io.Util;
import nl.utwente.groove.util.line.Line;
import nl.utwente.groove.util.line.Line.Style;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.IdValidator;

/**
 * Labels encapsulating node or edge types.
 * These are the labels that occur in host graphs.
 * @author Arend Rensink
 * @version $Revision$
 */
public final class TypeLabel extends ALabel {
    /**
     * Constructs a standard implementation of Label on the basis of a given
     * text index. For internal purposes only.
     * @param text the label text
     * @param kind indicator of the type of label (normal, node type or flag).
     */
    TypeLabel(String text, EdgeRole kind) {
        this.role = kind;
        this.text = text;
        this.sort = kind == NODE_TYPE
            ? Sort.getSort(text)
            : null;
    }

    @Override
    protected Line computeLine() {
        Line result = Line.atom(this.text);
        switch (getRole()) {
        case BINARY:
            break;
        case FLAG:
            result = result.style(Style.ITALIC);
            break;
        case NODE_TYPE:
            result = result.style(Style.BOLD);
            break;
        default:
            assert false;
        }
        return result;
    }

    /**
     * Returns the text of the label string, prefixed by the node type
     * or flag aspect if the label is a node type or flag.
     */
    @Override
    public String toParsableString() {
        return getRole().getPrefix() + text();
    }

    /** Returns the prefixed text. */
    @Override
    public String toString() {
        return toParsableString();
    }

    @Override
    public EdgeRole getRole() {
        return this.role;
    }

    /** Indicates if this label stands for a data sort. */
    public boolean isSort() {
        return this.sort != null;
    }

    /** Returns the data sort wrapped in this label, if any. */
    public @Nullable Sort getSort() {
        return this.sort;
    }

    private final @Nullable Sort sort;

    /** The label text. */
    private final String text;
    /** The type of label (normal, node type or flag). */
    private final EdgeRole role;

    /** Returns the node type label for a given data signature. */
    static public final TypeLabel getLabel(Sort sigKind) {
        return sigLabelMap.get(sigKind);
    }

    /**
     * Returns a default or node type label, depending on the prefix in the
     * input string.
     * @param prefixedText text of the label, possibly prefixed with a label
     * kind
     * @return a label with label type determined by the prefix
     */
    public static TypeLabel createLabel(String prefixedText) {
        return typeFactory.createLabel(prefixedText);
    }

    /**
     * Returns a default or node type label, depending on the prefix in the
     * input string.
     * @param prefixedText text of the label, possibly prefixed with a label
     * kind
     * @return a label with label type determined by the prefix
     * @throws FormatException if {@code text} does not satisfy the constraints
     * for labels
     */
    public static TypeLabel createLabelWithCheck(String prefixedText) throws FormatException {
        TypeLabel result = createLabel(prefixedText);
        if (result.getRole() != BINARY && !IdValidator.GROOVE_ID.isValid(result.text())) {
            throw new FormatException("%s label '%s' is not a valid edge label",
                result.getRole().getDescription(true), result.text());
        }
        return result;
    }

    /**
     * Returns the unique binary {@link TypeLabel} for a given
     * string. The string is used as-is, and is guaranteed to equal the text of
     * the resulting label.
     * @param text the text of the label; non-null
     * @return an existing or new label with the given text; non-null
     */
    public static TypeLabel createBinaryLabel(String text) {
        return typeFactory.createLabel(BINARY, text);
    }

    /**
     * Returns the unique representative of a {@link TypeLabel} for a given
     * string and label kind, while optionally testing if this label is legal.
     * The string is used as-is, and is guaranteed to
     * equal the text of the resulting label.
     * @param kind kind of label to be created
     * @param text the text of the label; non-null
     * @param test if {@code true}, a {@link nl.utwente.groove.util.parse.FormatException} may be thrown
     * if {@code text} does not satisfy the requirements of {@code kind}-labels.
     * @return an existing or new label with the given text and kind; non-null
     * @see #createLabel(EdgeRole, String)
     * @throws FormatException if {@code text} does not satisfy the constraints
     * for labels of kind {@code kind}
     */
    public static TypeLabel createLabel(EdgeRole kind, String text,
                                        boolean test) throws FormatException {
        if (test && kind != BINARY && !IdValidator.GROOVE_ID.isValid(text)) {
            throw new FormatException("%s label '%s' is not a valid edge label",
                kind.getDescription(true), text);
        }
        return createLabel(kind, text);
    }

    /**
     * Returns the unique representative of a {@link TypeLabel} for a given
     * string and label kind. The string is used as-is, and is guaranteed to
     * equal the text of the resulting label.
     * @param kind kind of label to be created
     * @param text the text of the label; non-null
     * @return an existing or new label with the given text and kind; non-null
     * @see #getRole()
     */
    public static TypeLabel createLabel(EdgeRole kind, String text) {
        return typeFactory.createLabel(kind, text);
    }

    /**
     * Returns a HTML-formatted string for a given label, without the
     * surrounding html-tag. The string is set to bold if the label is a node
     * type and is set to italic if the label is a flag.
     */
    static public String toHtmlString(Label label) {
        String result = HTMLConverter.toHtml(label.text());
        switch (label.getRole()) {
        case NODE_TYPE:
            result = HTMLConverter.STRONG_TAG.on(result);
            break;
        case FLAG:
            result = HTMLConverter.ITALIC_TAG.on(result);
            break;
        default:
            if (label instanceof RuleLabel ruleLabel) {
                if (!ruleLabel.isAtom() && !ruleLabel.isSharp()) {
                    result = HTMLConverter.ITALIC_TAG.on(result);
                }
            }
        }
        return result;
    }

    static private final Map<Sort,TypeLabel> sigLabelMap = new EnumMap<>(Sort.class);
    static {
        for (Sort sort : Sort.values()) {
            sigLabelMap.put(sort, new TypeLabel(sort.getName(), EdgeRole.NODE_TYPE));
        }
    }

    /** Text of the node type label in an untyped setting. */
    static public final String NODE_LABEL_TEXT = "" + Util.UC_OMEGA;

    /** Type label for nodes in an untyped setting. */
    static public final TypeLabel NODE = new TypeLabel(NODE_LABEL_TEXT, EdgeRole.NODE_TYPE);

    /**
     * Unique type factory used for creating labels statically.
     * For exception-free class initialisation, this needs to come after {@link #NODE}.
     */
    private static TypeFactory typeFactory = TypeFactory.newInstance();
}