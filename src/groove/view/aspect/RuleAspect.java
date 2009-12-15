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
 * $Id: RuleAspect.java,v 1.17 2008-01-31 13:12:08 rensink Exp $
 */
package groove.view.aspect;

import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.graph.Node;
import groove.rel.RegExpr;
import groove.rel.RegExprLabel;
import groove.trans.RuleName;
import groove.util.Groove;
import groove.util.Pair;
import groove.view.FormatException;
import groove.view.FreeLabelParser;
import groove.view.LabelParser;
import groove.view.RegExprLabelParser;

/**
 * Graph aspect dealing with transformation rules. Values are: <i>eraser</i>,
 * <i>reader</i> or <i>creator</i>.
 * @author Arend Rensink
 * @version $Revision$
 */
public class RuleAspect extends AbstractAspect {

    /** Private constructor to create the singleton instance. */
    private RuleAspect() {
        super(RULE_ASPECT_NAME);
    }

    /**
     * This implementation considers {@link #EMBARGO} to be more demanding than
     * {@link #ERASER}.
     */
    @Override
    protected AspectValue getMaxValue(AspectValue value1, AspectValue value2)
        throws FormatException {
        if (EMBARGO.equals(value1) && ERASER.equals(value2)) {
            return EMBARGO;
        } else if (EMBARGO.equals(value2) && ERASER.equals(value1)) {
            return EMBARGO;
        } else if (REMARK.equals(value1) || REMARK.equals(value2)) {
            return REMARK;
        } else {
            return super.getMaxValue(value1, value2);
        }
    }

    @Override
    public void checkEdge(AspectEdge edge, AspectGraph graph)
        throws FormatException {
        // test for merge edges between creator nodes
        if (isMerger(edge)) {
            AspectNode creatorEnd = null;
            for (Node end : edge.ends()) {
                if (isCreator((AspectNode) end)) {
                    creatorEnd = (AspectNode) end;
                    break;
                }
            }
            if (creatorEnd != null) {
                throw new FormatException(
                    "Merge edge '%s' with creator end node '%s' not allowed",
                    edge, creatorEnd);
            }
        }
    }

    /**
     * This implementation tests for certain regular expressions. No declared
     * eraser may carry a regular expression label other than a wildcard or
     * variable, and no inferred creator may have a regular expression other
     * than a wildcard, merger or variable.
     */
    @Override
    public void testLabel(Label label, AspectValue declaredValue,
            AspectValue inferredValue) throws FormatException {
        // if the label is not a regular expression, it is in any case fine
        if (label instanceof RegExprLabel) {
            testLabel(((RegExprLabel) label).getRegExpr(), declaredValue,
                inferredValue);
        }
    }

    /**
     * Callback method to test the label with the knowledge that it is a regular
     * expression.
     * @see #testLabel(Label, AspectValue, AspectValue)
     */
    private void testLabel(RegExpr expr, AspectValue declaredValue,
            AspectValue inferredValue) throws FormatException {
        // check if negation occurs anywhere except on top level
        if (expr.containsOperator(RegExpr.NEG_OPERATOR)) {
            throw new FormatException(
                "Negation may only occur on top level in %s", expr);
        }
        // check the expression is a regular eraser pattern
        if (ERASER.equals(declaredValue)) {
            if (!expr.isWildcard()) {
                throw new FormatException(
                    "Regular expression %s not allowed on an eraser edge", expr);
            }
        }
        // check the expression is a regular creator pattern
        if (CREATOR.equals(inferredValue)) {
            if (!(expr.getWildcardId() != null || expr.isEmpty())) {
                throw new FormatException(
                    "Regular expression %s not allowed on a creator edge", expr);
            }
        }
    }

    /**
     * Returns a {@link NamedAspectValue} with the given name.
     * @param name the name content
     * @throws FormatException if <code>name</code> is not correctly formatted
     *         as an identifier
     */
    @Override
    protected AspectValue createValue(String name) throws FormatException {
        if (name.equals(READER_NAME) || name.equals(RULE_NAME)
            || name.equals(REMARK_NAME)) {
            return super.createValue(name);
        } else {
            return new NamedAspectValue(getInstance(), name);
        }
    }

    /**
     * Returns the name content of the rule aspect value of a given, aspect
     * element, if it is a {@link NamedAspectValue}.
     * @param elem the aspect element to retrieve the name from
     * @return the name in <code>value</code>, or <code>null</code> if
     *         <code>value</code> is not a {@link NamedAspectValue}.
     */
    static public String getName(AspectElement elem) {
        String result = null;
        AspectValue value = elem.getValue(getInstance());
        if (value instanceof NamedAspectValue) {
            result = ((NamedAspectValue) value).getContent();
            if (result != null && result.length() == 0) {
                result = null;
            }
        }
        return result;
    }

    /**
     * Returns the singleton instance of this aspect.
     */
    public static RuleAspect getInstance() {
        return instance;
    }

    /**
     * Returns the rule aspect value associated with a given aspect element.
     * Convenience method for {@link AspectElement#getValue(Aspect)} with
     * {@link #getInstance()} as parameter.
     */
    public static AspectValue getRuleValue(AspectElement elem) {
        return elem.getValue(getInstance());
    }

    /**
     * Tests if a given aspect element contains a {@link RuleAspect} value that
     * indicates presence in the left hand side. This is the case if there is an
     * aspect value in the element which equals either {@link #READER} or
     * {@link #ERASER}.
     * @param element the element to be tested
     * @return <code>true</code> if <code>element</code> contains a
     *         {@link RuleAspect} value that equals either {@link #READER} or
     *         {@link #ERASER}.
     */
    public static boolean inLHS(AspectElement element) {
        AspectValue role = getRuleValue(element);
        return (READER.equals(role) || ERASER.equals(role)) && hasRole(element);
    }

    /**
     * Tests if a given aspect element contains a {@link RuleAspect} value that
     * indicates presence in the right hand side. This is the case if there is
     * an aspect value in the element which equals either {@link #READER} or
     * {@link #CREATOR}.
     * @param element the element to be tested
     * @return <code>true</code> if <code>element</code> contains a
     *         {@link RuleAspect} value that equals either {@link #READER} or
     *         {@link #CREATOR}.
     */
    public static boolean inRHS(AspectElement element) {
        AspectValue role = getRuleValue(element);
        return (READER.equals(role) || CREATOR.equals(role) || CNEW.equals(role))
            && hasRole(element);
    }

    /**
     * Tests if a given element has no rule aspect value, and no other aspect
     * values that prevent it from being interpreted as reader.
     */
    private static boolean hasRole(AspectElement element) {
        return !NestingAspect.isMetaElement(element);
    }

    /**
     * Tests if a given aspect element contains a {@link RuleAspect} value that
     * indicates presence a negative application condition. This is the case if
     * there is an aspect value in the element which equals {@link #EMBARGO}.
     * @param element the element to be tested
     * @return <code>true</code> if <code>element</code> contains a
     *         {@link RuleAspect} value that equals {@link #EMBARGO}.
     */
    public static boolean inNAC(AspectElement element) {
        return hasRole(element) && (EMBARGO.equals(getRuleValue(element)))
            || isCNEW(element);
    }

    /**
     * Tests if a given aspect element is a creator. This is the case if there
     * is an aspect value in the element which equals {@link #CREATOR}.
     * @param element the element to be tested
     * @return <code>true</code> if <code>element</code> contains a
     *         {@link RuleAspect} value that equals {@link #CREATOR}.
     */
    public static boolean isCreator(AspectElement element) {
        return hasRole(element) && CREATOR.equals(getRuleValue(element))
            || isCNEW(element);
    }

    /**
     * Tests if a given aspect element is an embargo creator. This is the case
     * if there is an aspect value in the element which equals EMBARGOCREATOR.
     */
    public static boolean isCNEW(AspectElement element) {
        return hasRole(element) && CNEW.equals(getRuleValue(element));
    }

    /**
     * Tests if a given aspect element is an eraser. This is the case if there
     * is an aspect value in the element which equals {@link #ERASER}.
     * @param element the element to be tested
     * @return <code>true</code> if <code>element</code> contains a
     *         {@link RuleAspect} value that equals {@link #ERASER}.
     */
    public static boolean isEraser(AspectElement element) {
        return hasRole(element) && ERASER.equals(getRuleValue(element));
    }

    /**
     * Tests if a given aspect edge is a merger. This is the case if it is a
     * creator with a merge label.
     * @param edge the edge to be tested
     * @return <code>true</code> if <code>edge</code> is a merger
     */
    public static boolean isMerger(AspectEdge edge) throws FormatException {
        if (isCreator(edge)) {
            return RegExprLabel.isEmpty(CREATOR.getLabelParser().parse(
                edge.label()));
        } else {
            return false;
        }
    }

    /**
     * Convenience method to test if a given aspectual element stands for an
     * actual rule element. If not, this means that it provides information
     * <i>about</i> the rule.
     */
    public static boolean inRule(AspectElement elem) {
        // JHK: Nesting Meta-nodes and edges are not in the rule
        return hasRole(elem) && (inLHS(elem) || inRHS(elem) || inNAC(elem));
    }

    /**
     * Tests if a given aspect element is a remark. This is the case if there is
     * an aspect value in the element which equals {@link #REMARK}.
     * @param element the element to be tested
     * @return <code>true</code> if <code>element</code> contains a
     *         {@link RuleAspect} value that equals {@link #REMARK}.
     */
    public static boolean isRemark(AspectElement element) {
        AspectValue role = getRuleValue(element);
        return (REMARK.equals(role));
    }

    /**
     * Convenience method to retrieve the content of a rule aspect value of a
     * given node.
     * @return the content of the rule aspect value of <code>node</code>, or
     *         <code>null</code> if <code>node</code> does not have this aspect
     *         value.
     */
    public static Pair<RuleName,Integer> getRuleContent(AspectNode node) {
        AspectValue ruleValue = getRuleValue(node);
        if (ruleValue instanceof RuleAspectValue) {
            return ((RuleAspectValue) ruleValue).getContent();
        } else {
            return null;
        }
    }

    /** Static fixed parser for rule content. */
    static final ContentParser<Pair<RuleName,Integer>> parser =
        new RuleContentParser();

    /**
     * The name of the rule aspect.
     */
    public static final String RULE_ASPECT_NAME = "role";
    /** Name of the eraser aspect value. */
    public static final String ERASER_NAME =
        Groove.getXMLProperty("label.eraser.prefix");
    /** The eraser aspect value. */
    public static final AspectValue ERASER;
    /** Name of the reader aspect value. */
    public static final String READER_NAME =
        Groove.getXMLProperty("label.reader.prefix");
    /** The reader aspect value. */
    public static final AspectValue READER;
    /** Name of the creator aspect value. */
    public static final String CREATOR_NAME =
        Groove.getXMLProperty("label.creator.prefix");
    /** The creator aspect value. */
    public static final AspectValue CREATOR;
    /** Name of the embargo aspect value. */
    public static final String EMBARGO_NAME =
        Groove.getXMLProperty("label.embargo.prefix");
    /** The embargo aspect value. */
    public static final AspectValue EMBARGO;
    /** Name of the embargo creator aspect value. */
    public static final String CNEW_NAME =
        Groove.getXMLProperty("label.embargocreator.prefix");
    /** The embargo creator aspect value. */
    public static final AspectValue CNEW;
    /** The total number of roles. */
    public static final int VALUE_COUNT;
    /** Name of the remark aspect value. */
    public static final String REMARK_NAME =
        Groove.getXMLProperty("label.remark.prefix");
    /** The remark aspect value. */
    public static final AspectValue REMARK;
    /** Name of the rule aspect value. */
    public static final String RULE_NAME =
        Groove.getXMLProperty("label.rule.prefix");
    // /** The remark aspect value. */
    // public static final RuleAspectValue RULE;
    /**
     * The singleton instance of this class.
     */
    private static final RuleAspect instance = new RuleAspect();

    static {
        try {
            ERASER = instance.addValue(ERASER_NAME);
            CREATOR = instance.addValue(CREATOR_NAME);
            EMBARGO = instance.addValue(EMBARGO_NAME);
            CNEW = instance.addValue(CNEW_NAME);
            READER = instance.addValue(READER_NAME);
            REMARK = instance.addValue(REMARK_NAME);
            ERASER.setLabelParser(EraserParser.getInstance());
            CREATOR.setLabelParser(CreatorParser.getInstance());
            EMBARGO.setLabelParser(RegExprLabelParser.getInstance());
            CNEW.setLabelParser(CreatorParser.getInstance());
            READER.setLabelParser(RegExprLabelParser.getInstance());
            REMARK.setLabelParser(FreeLabelParser.getInstance());
            // RULE = null; //new RuleAspectValue(); // currently not added to
            // values!
            instance.setDefaultValue(READER);
            CREATOR.setSourceToEdge(CREATOR);
            CREATOR.setTargetToEdge(CREATOR);
            ERASER.setSourceToEdge(ERASER);
            ERASER.setTargetToEdge(ERASER);
            CNEW.setSourceToEdge(CNEW);
            CNEW.setTargetToEdge(CNEW);
            EMBARGO.setSourceToEdge(EMBARGO);
            EMBARGO.setTargetToEdge(EMBARGO);
            REMARK.setSourceToEdge(REMARK);
            REMARK.setTargetToEdge(REMARK);
            // incompatibilities
            // instance.setIncompatible(NestingAspect.getInstance());
            VALUE_COUNT = instance.getValues().size();
        } catch (FormatException exc) {
            throw new Error("Aspect '" + RULE_ASPECT_NAME
                + "' cannot be initialised due to name conflict", exc);
        }
    }

    /** Type for the content of a rule aspect value. */
    public static class RuleAspectValue extends
            ContentAspectValue<Pair<RuleName,Integer>> {
        /** Constructs a factory instance. */
        public RuleAspectValue() throws FormatException {
            super(getInstance(), RULE_NAME);
        }

        /** Creates an instance with actual content. */
        public RuleAspectValue(RuleAspectValue original,
                Pair<RuleName,Integer> content) {
            super(original, content);
        }

        @Override
        ContentParser<Pair<RuleName,Integer>> createParser() {
            return parser;
        }

        @Override
        public RuleAspectValue newValue(String value) throws FormatException {
            return new RuleAspectValue(this, getParser().toContent(value));
        }
    }

    /**
     * Creates a parser that converts to an from a pair consisting of a
     * structured rule name and an optional priority indicator. The string
     * should be formatted according to <code>name</code> or
     * <code>name + SEPARATOR + priority</code>.
     * @author Arend Rensink
     * @version $Revision $
     */
    private static class RuleContentParser implements
            ContentParser<Pair<RuleName,Integer>> {
        /** Empty constructor with the correct visibility. */
        RuleContentParser() {
            // empty
        }

        /**
         * Value used to signal that the priority is not explicitly given
         * (meaning that the rule has default priority).
         */
        public final static int IMPLICIT_PRIORITY = -1;

        /**
         * Creates a pair of a rule name and priority indicator from a given
         * string. The string is assumed to be formatted
         * <code>name + SEPARATOR + priority</code> or just <code>name</code>.
         * In the latter case the value returned for the priority is
         * {@link #IMPLICIT_PRIORITY}.
         */
        public Pair<RuleName,Integer> toContent(String value)
            throws FormatException {
            String name;
            int priority;
            int separatorIndex = value.indexOf(CONTENT_SEPARATOR);
            if (separatorIndex < 0) {
                name = value;
                priority = IMPLICIT_PRIORITY;
            } else {
                name = value.substring(0, separatorIndex);
                try {
                    priority =
                        Integer.parseInt(value.substring(separatorIndex
                            + CONTENT_SEPARATOR.length()));
                } catch (NumberFormatException exc) {
                    throw new FormatException(
                        "Priority value in %s cannot be parsed as a number",
                        value);
                }
                if (priority < 0) {
                    throw new FormatException(
                        "Priority value %s should be non-negative", value);
                }
            }
            if (name.length() == 0) {
                throw new FormatException("Rule name should be non-empty");
            }
            return new Pair<RuleName,Integer>(createName(name), priority);
        }

        /**
         * Returns a string of the form
         * <code>content.first() + SEPARATOR + content.second()</code> if the
         * second component is not {@link #IMPLICIT_PRIORITY}, or just
         * <code>content.first()</code> otherwise.
         */
        public String toString(Pair<RuleName,Integer> content) {
            String name = content.first().text();
            int priority = content.second();
            return name
                + (priority == IMPLICIT_PRIORITY ? "" : CONTENT_SEPARATOR
                    + priority);
        }

        /** Callback factory method to create a rule name from a given string. */
        protected RuleName createName(String text) {
            return new RuleName(text);
        }
    }

    /**
     * Label parser for eraser edges. Recognises atoms and wildcards.
     * @author Arend Rensink
     * @version $Revision $
     */
    static private class EraserParser implements LabelParser {
        /** Empty constructor to ensure a singleton class. */
        private EraserParser() {
            // empty
        }

        /**
         * This implementation tries to parse the label text as a regular
         * expression, but throws an exception if the result is anything other
         * than a wildcard or an atom.
         */
        public Label parse(Label label) throws FormatException {
            Label result = this.preParser.parse(label);
            if (!(result instanceof DefaultLabel || RegExprLabel.isWildcard(result))) {
                throw new FormatException(
                    "Eraser label %s should be wildcard or atom", label);
            }
            return result;
        }

        /** This implementation returns a default label based on the label text. */
        public DefaultLabel unparse(Label label) {
            if (label instanceof DefaultLabel) {
                return (DefaultLabel) label;
            } else {
                return DefaultLabel.createLabel(label.text());
            }
        }

        /**
         * The parser used to decompose the label, after which a selection
         * follows.
         */
        private final LabelParser preParser = RegExprLabelParser.getInstance();

        /** Returns the singleton instance of this class. */
        static public EraserParser getInstance() {
            return instance;
        }

        /** Singleton instance of this class. */
        static private EraserParser instance = new EraserParser();
    }

    /**
     * Label parser for creator edges. Recognises atoms, named unguarded
     * wildcards, and mergers.
     * @author Arend Rensink
     * @version $Revision $
     */
    static private class CreatorParser implements LabelParser {
        /** Empty constructor to ensure a singleton class. */
        private CreatorParser() {
            // empty
        }

        /**
         * This implementation tries to parse the label text as a regular
         * expression, but throws an exception if the result is anything other
         * than an unguarded named wildcard, a merger, or an atom.
         */
        public Label parse(Label label) throws FormatException {
            Label result = this.preParser.parse(label);
            boolean allowed;
            if (result instanceof DefaultLabel) {
                allowed = true;
            } else {
                RegExpr expr = ((RegExprLabel) result).getRegExpr();
                allowed =
                    expr.getWildcardId() != null
                        && expr.getWildcardGuard() == null || expr.isEmpty();
            }
            if (!allowed) {
                throw new FormatException(
                    "Creator label %s should be named unguarded wildcard, merger or atom",
                    label);
            }
            return result;
        }

        /** This implementation returns a default label based on the label text. */
        public DefaultLabel unparse(Label label) {
            if (label instanceof DefaultLabel) {
                return (DefaultLabel) label;
            } else {
                return DefaultLabel.createLabel(label.text());
            }
        }

        /**
         * The parser used to decompose the label, after which a selection
         * follows.
         */
        private final LabelParser preParser = RegExprLabelParser.getInstance();

        /** Returns the singleton instance of this class. */
        static public CreatorParser getInstance() {
            return instance;
        }

        /** Singleton instance of this class. */
        static private CreatorParser instance = new CreatorParser();
    }
}
