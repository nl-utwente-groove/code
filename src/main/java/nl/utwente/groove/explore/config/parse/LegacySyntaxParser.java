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
package nl.utwente.groove.explore.config.parse;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.explore.AcceptorSpec;
import nl.utwente.groove.explore.ExploreType;
import nl.utwente.groove.explore.LTLExploreType;
import nl.utwente.groove.explore.StateExploreType;
import nl.utwente.groove.explore.config.Bound;
import nl.utwente.groove.explore.config.ConfiguredExploreType;
import nl.utwente.groove.explore.config.Cost;
import nl.utwente.groove.explore.config.Count;
import nl.utwente.groove.explore.config.ExploreConfig;
import nl.utwente.groove.explore.config.ExploreKey;
import nl.utwente.groove.explore.config.ExploreTypeConverter;
import nl.utwente.groove.explore.config.Frontier;
import nl.utwente.groove.explore.config.Goal;
import nl.utwente.groove.explore.config.NextState;
import nl.utwente.groove.explore.config.Successor;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.Parser;

/**
 * Parser for the legacy exploration syntax: the strategy and acceptor
 * keywords of the Generator's {@code -s} and {@code -a} options, also stored
 * in the legacy {@code explorationStrategy} grammar property. The keywords
 * are translated directly into the exploration feature model: a
 * config-expressible strategy or acceptor becomes settings of an
 * {@link ExploreConfig}, while the model-checking, minimax and
 * single-state strategies (which the feature model deliberately does not
 * cover) become dedicated {@link ExploreType} subclasses. This makes the
 * legacy syntax a permanent thin front-end of the configuration, with no
 * dependence on the encode/enumerator machinery.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class LegacySyntaxParser {
    private LegacySyntaxParser() {
        // static utility class
    }

    /**
     * Parses a legacy exploration description, consisting of two or three
     * space-separated parts: a strategy, an acceptor and an optional result
     * count (with {@code 0}, the default, meaning unbounded).
     * @throws FormatException if the description could not be parsed
     */
    public static ExploreType parse(String description) throws FormatException {
        String[] parts = description.split("\\s+");
        if (parts.length < 2 || parts.length > 3) {
            throw new FormatException("Can't parse exploration descriptor '%s'. " + SYNTAX_MESSAGE,
                description);
        }
        int count = 0;
        if (parts.length == 3) {
            try {
                count = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                count = -1;
            }
            if (count < 0) {
                throw new FormatException("Result count '%s' must be a non-negative number",
                    parts[2]);
            }
        }
        return build(null, parts[0], parts[1], count);
    }

    /**
     * Overlays legacy strategy, acceptor and result count components onto a
     * base exploration type; an absent component ({@code null} strategy or
     * acceptor, zero count) retains the base value. A config-expressible
     * strategy resets the strategy-owned features (traversal order,
     * successor generation, frontier, heuristic, cost and bound) before
     * setting its own; all other features of the base configuration (such as
     * persistence or collapse) are preserved. Likewise, an acceptor resets
     * only the goal and outcome features. A model-checking, minimax or
     * single-state strategy is not config-based and replaces the base
     * entirely.
     * @throws FormatException if a component cannot be parsed, or the
     * resulting combination is inconsistent, or the base type has no
     * feature-model equivalent to overlay onto
     */
    public static ExploreType overlay(ExploreType base, @Nullable String strategy,
                                      @Nullable String acceptor,
                                      int count) throws FormatException {
        return build(base, strategy, acceptor, count);
    }

    /**
     * Returns a parser for legacy exploration descriptions (see
     * {@link #parse}), with the default exploration type as default value.
     * The unparse direction yields the type's identifier, for display only —
     * the legacy key is no longer written, merely read.
     */
    public static Parser<ExploreType> parser() {
        return PARSER;
    }

    private static final Parser<ExploreType> PARSER
        = new Parser.AParser<>(LegacySyntaxParser.SYNTAX_MESSAGE, ExploreType.getDefault()) {
            @Override
            public ExploreType parse(String input) throws FormatException {
                if (input.isEmpty()) {
                    return getDefaultValue();
                }
                return LegacySyntaxParser.parse(input);
            }

            @Override
            public <V extends ExploreType> String unparse(V value) {
                return value.getIdentifier();
            }
        };

    /** Common implementation of {@link #parse} and {@link #overlay};
     * a {@code null} base stands for the default configuration. */
    private static ExploreType build(@Nullable ExploreType base, @Nullable String strategy,
                                     @Nullable String acceptor,
                                     int count) throws FormatException {
        AcceptorSpec acceptorSpec = acceptor == null
            ? null
            : parseAcceptor(acceptor);
        if (strategy != null) {
            String keyword = keywordOf(strategy);
            if (DIRECT_KEYWORDS.contains(keyword)) {
                return createDirectType(keyword, argsOf(strategy), acceptorSpec, count);
            }
        }
        if (acceptorSpec != null && acceptorSpec.kind() == AcceptorSpec.Kind.CYCLE) {
            throw new FormatException(
                "Acceptor 'cycle' requires an LTL model checking strategy");
        }
        ExploreConfig config = base == null
            ? new ExploreConfig()
            : getBaseConfig(base);
        if (strategy != null) {
            applyStrategy(config, strategy);
        }
        if (acceptorSpec != null) {
            applyAcceptor(config, acceptorSpec);
        }
        if (count > 0) {
            config.put(ExploreKey.COUNT, Count.toSetting(count));
        }
        return ExploreTypeConverter.toExploreType(config);
    }

    /** Extracts the configuration underlying a base exploration type.
     * @throws FormatException if the base type is not configuration-based
     */
    private static ExploreConfig getBaseConfig(ExploreType base) throws FormatException {
        if (base instanceof ConfiguredExploreType configured) {
            return new ExploreConfig(configured.getConfig());
        }
        throw new FormatException(
            "Legacy components cannot be overlaid on exploration type '%s'",
            base.getIdentifier());
    }

    /** Returns the keyword part (before the first colon) of a legacy descriptor. */
    private static String keywordOf(String text) {
        int pos = text.indexOf(':');
        return pos < 0
            ? text
            : text.substring(0, pos);
    }

    /** Returns the argument part (after the first colon) of a legacy
     * descriptor, or {@code null} if there is no colon. */
    private static @Nullable String argsOf(String text) {
        int pos = text.indexOf(':');
        return pos < 0
            ? null
            : text.substring(pos + 1);
    }

    /** Retrieves the (non-empty) argument part of a legacy descriptor.
     * @throws FormatException if there is no argument
     */
    private static String requireArgs(String keyword,
                                      @Nullable String args) throws FormatException {
        if (args == null || args.isEmpty()) {
            throw new FormatException("'%s' requires an argument, as %s:<arg>", keyword, keyword);
        }
        return args;
    }

    /**
     * Translates a config-expressible legacy strategy into the strategy-owned
     * features of a configuration, resetting those features first.
     */
    private static void applyStrategy(ExploreConfig config, String text) throws FormatException {
        for (var key : STRATEGY_KEYS) {
            config.put(key, key.getDefaultSetting());
        }
        String keyword = keywordOf(text);
        String args = argsOf(text);
        switch (keyword) {
        case "bfs" -> applyDepthBound(config, keyword, args);
        case "dfs" -> {
            config.put(ExploreKey.NEXT, NextState.NEWEST.createSetting());
            applyDepthBound(config, keyword, args);
        }
        case "linear" -> {
            config.put(ExploreKey.FRONTIER, Frontier.SINGLE.createSetting());
            config.put(ExploreKey.SUCCESSOR, Successor.SINGLE.createSetting());
        }
        case "random" -> {
            config.put(ExploreKey.FRONTIER, Frontier.SINGLE.createSetting());
            config.put(ExploreKey.SUCCESSOR, Successor.SINGLE_RANDOM.createSetting());
        }
        case "crule" -> config
            .put(ExploreKey.BOUND,
                 Bound.UPTO.createSetting(parseCondition(keyword, requireArgs(keyword, args))));
        case "cnbound" -> config
            .put(ExploreKey.BOUND,
                 Bound.NODES
                     .createSetting(new Bound.Limit(parseNatural(keyword,
                                                                requireArgs(keyword, args)),
                                                    0)));
        case "cebound" -> {
            String bounds = requireArgs(keyword, args);
            // syntax check only; the labels resolve against the grammar
            // when the strategy is instantiated
            EdgeMapParser.parseRaw(bounds);
            config.put(ExploreKey.BOUND, Bound.EDGES.createSetting(bounds));
        }
        case "uptorule" -> applyUptoRule(config, requireArgs(keyword, args));
        default -> throw new FormatException("No such strategy '%s'", text);
        }
    }

    /** Translates the optional depth bound of {@code bfs} or {@code dfs}
     * into a uniform-cost bound. */
    private static void applyDepthBound(ExploreConfig config, String keyword,
                                        @Nullable String args) throws FormatException {
        if (args == null) {
            return;
        }
        int depth = parseNatural(keyword, args);
        if (depth > 0) {
            config.put(ExploreKey.COST, Cost.UNIFORM.createSetting());
            config
                .put(ExploreKey.BOUND, Bound.COST.createSetting(new Bound.Limit(depth, 0)));
        }
    }

    /** Translates the argument of the {@code uptorule} strategy into
     * next-state and condition bound features. */
    private static void applyUptoRule(ExploreConfig config, String args) throws FormatException {
        var matcher = UPTO_RULE_PATTERN.matcher(args);
        if (!matcher.matches()) {
            throw new FormatException(
                "Cannot parse strategy 'uptorule:%s'; required format is"
                    + " uptorule:(bfs|dfs)[<num>](->|=>)[!]<rule>",
                args);
        }
        if ("dfs".equals(matcher.group(1))) {
            config.put(ExploreKey.NEXT, NextState.NEWEST.createSetting());
        }
        String bound = matcher.group(2);
        if (!bound.isEmpty() && Integer.parseInt(bound) > 0) {
            throw new FormatException(
                "A condition bound cannot be combined with a depth bound in the feature model");
        }
        // '->' means up to (hit states are not explored), '=>' means include
        var kind = "->".equals(matcher.group(3))
            ? Bound.UPTO
            : Bound.INCLUDE;
        config.put(ExploreKey.BOUND, kind.createSetting(matcher.group(4) + matcher.group(5)));
    }

    /** Checks that a condition argument is an (optionally {@code !}-prefixed)
     * non-empty rule name, and returns it. */
    private static String parseCondition(String keyword, String args) throws FormatException {
        String name = args.startsWith("!")
            ? args.substring(1)
            : args;
        if (name.isEmpty()) {
            throw new FormatException("'%s' requires a rule name, as %s:[!]<rule>", keyword,
                keyword);
        }
        return args;
    }

    /** Parses a non-negative number argument of a legacy descriptor. */
    private static int parseNatural(String keyword, String text) throws FormatException {
        try {
            int result = Integer.parseInt(text);
            if (result >= 0) {
                return result;
            }
        } catch (NumberFormatException e) {
            // fall through to the error
        }
        throw new FormatException("Argument '%s' of '%s' must be a non-negative number", text,
            keyword);
    }

    /** Parses a legacy acceptor descriptor into an acceptor specification. */
    private static AcceptorSpec parseAcceptor(String text) throws FormatException {
        String keyword = keywordOf(text);
        String args = argsOf(text);
        for (var kind : EnumSet
            .of(AcceptorSpec.Kind.FINAL, AcceptorSpec.Kind.ANY, AcceptorSpec.Kind.NONE,
                AcceptorSpec.Kind.CYCLE)) {
            if (kind.getKeyword().equals(keyword)) {
                if (args != null) {
                    throw new FormatException("Acceptor '%s' does not take an argument", keyword);
                }
                return new AcceptorSpec(kind, "");
            }
        }
        return switch (keyword) {
        case "ruleapp" -> new AcceptorSpec(AcceptorSpec.Kind.RULEAPP,
            requireArgs(keyword, args));
        case "inv" -> new AcceptorSpec(AcceptorSpec.Kind.INVARIANT,
            parseCondition(keyword, requireArgs(keyword, args)));
        case "formula" -> new AcceptorSpec(AcceptorSpec.Kind.FORMULA,
            requireArgs(keyword, args));
        default -> throw new FormatException("No such acceptor '%s'", text);
        };
    }

    /**
     * Translates an acceptor specification into the goal feature of a
     * configuration, resetting the goal and outcome features first.
     */
    private static void applyAcceptor(ExploreConfig config, AcceptorSpec acceptor) {
        config.put(ExploreKey.GOAL, ExploreKey.GOAL.getDefaultSetting());
        config.put(ExploreKey.OUTCOME, ExploreKey.OUTCOME.getDefaultSetting());
        switch (acceptor.kind()) {
        case FINAL -> {
            // the default goal
        }
        case ANY -> config.put(ExploreKey.GOAL, Goal.ANY.createSetting());
        case NONE -> config.put(ExploreKey.GOAL, Goal.NONE.createSetting());
        case RULEAPP -> config
            .put(ExploreKey.GOAL, Goal.FIRES.createSetting(acceptor.content()));
        case INVARIANT, FORMULA -> config
            .put(ExploreKey.GOAL, Goal.CONDITION.createSetting(acceptor.content()));
        default -> throw Exceptions
            .illegalState("Cycle acceptor should have been rejected before");
        }
    }

    /** Creates the dedicated exploration type for a non-config strategy keyword. */
    private static ExploreType createDirectType(String keyword, @Nullable String args,
                                                @Nullable AcceptorSpec acceptor,
                                                int count) throws FormatException {
        if (LTL_KEYWORDS.contains(keyword)) {
            if (acceptor != null && acceptor.kind() != AcceptorSpec.Kind.CYCLE) {
                throw new FormatException(
                    "Strategy '%s' can only be combined with the 'cycle' acceptor", keyword);
            }
            return createLTLType(keyword, requireArgs(keyword, args), count);
        }
        AcceptorSpec effectiveAcceptor = acceptor == null
            ? AcceptorSpec.FINAL
            : acceptor;
        return switch (keyword) {
        case "state" -> {
            if (args != null) {
                throw new FormatException("Strategy 'state' does not take an argument");
            }
            yield new StateExploreType(effectiveAcceptor, count);
        }
        case "minimax" -> createMinimaxType(requireArgs(keyword, args), effectiveAcceptor, count);
        default -> throw Exceptions.illegalState("Unknown direct strategy keyword '%s'", keyword);
        };
    }

    /** Creates the LTL exploration type for a model-checking keyword. */
    private static ExploreType createLTLType(String keyword, String args,
                                             int count) throws FormatException {
        LTLExploreType.Kind kind = Arrays
            .stream(LTLExploreType.Kind.values())
            .filter(k -> k.getKeyword().equals(keyword))
            .findFirst()
            .orElseThrow(() -> Exceptions.illegalState("Unknown LTL keyword '%s'", keyword));
        if (kind == LTLExploreType.Kind.PLAIN) {
            return new LTLExploreType(kind, args, null, count);
        }
        int semi = args.indexOf(';');
        if (semi < 0) {
            throw new FormatException(
                "Cannot parse '%s:%s'; required format is %s:<bound>;<property>", keyword, args,
                keyword);
        }
        String bound = args.substring(0, semi);
        String property = args.substring(semi + 1);
        if (bound.isEmpty() || property.isEmpty()) {
            throw new FormatException(
                "Cannot parse '%s:%s'; required format is %s:<bound>;<property>", keyword, args,
                keyword);
        }
        return new LTLExploreType(kind, property, bound, count);
    }

    /** Creates the minimax exploration type from its comma-separated arguments. */
    // the legacy syntax deliberately keeps supporting the deprecated
    // minimax exploration until its removal in release 8.0 (gh #890)
    @SuppressWarnings("removal")
    private static ExploreType createMinimaxType(String args, AcceptorSpec acceptor,
                                                 int count) throws FormatException {
        String[] parts = args.split(",", -1);
        if (parts.length != 6) {
            throw new FormatException(
                "Cannot parse 'minimax:%s'; required format is minimax:<heuristic-param>,"
                    + "<max-depth>,<rule>[;<rule>]*,<start-max>,<minmax-rule>,<minmax-param>",
                args);
        }
        int heuristicParam = parseNatural("minimax", parts[0]);
        int maxDepth = parseNatural("minimax", parts[1]);
        List<String> ruleNames = Arrays.asList(parts[2].split(";"));
        if (ruleNames.isEmpty() || ruleNames.stream().anyMatch(String::isEmpty)) {
            throw new FormatException("Empty rule list in 'minimax:%s'", args);
        }
        String minmaxRule = parts[4];
        if (minmaxRule.isEmpty()) {
            throw new FormatException("Empty minimax rule name in 'minimax:%s'", args);
        }
        int minmaxParam = parseNatural("minimax", parts[5]);
        // qualified reference, so that no import of the deprecated type is needed
        return new nl.utwente.groove.explore.MinimaxExploreType(heuristicParam, maxDepth,
            ruleNames, parts[3], minmaxRule, minmaxParam, acceptor, count);
    }

    /** The features owned by the strategy part of the legacy syntax. */
    private static final List<ExploreKey> STRATEGY_KEYS
        = List.of(ExploreKey.NEXT, ExploreKey.SUCCESSOR, ExploreKey.FRONTIER,
                  ExploreKey.HEURISTIC, ExploreKey.COST, ExploreKey.BOUND);
    /** The model-checking strategy keywords, derived from the LTL
     * exploration kinds so the two cannot drift apart. */
    private static final Set<String> LTL_KEYWORDS = Arrays
        .stream(LTLExploreType.Kind.values())
        .map(LTLExploreType.Kind::getKeyword)
        .collect(Collectors.toUnmodifiableSet());
    /** The strategy keywords realised by dedicated exploration types
     * rather than the configuration. */
    private static final Set<String> DIRECT_KEYWORDS = Stream
        .concat(LTL_KEYWORDS.stream(), Stream.of("minimax", "state"))
        .collect(Collectors.toUnmodifiableSet());
    /** Pattern for the argument of the {@code uptorule} strategy. */
    private static final Pattern UPTO_RULE_PATTERN
        = Pattern.compile("(bfs|dfs)(\\d*)(->|=>)(!?)(.+)");
    /** Message describing the syntax of a parsable legacy exploration. */
    public static final String SYNTAX_MESSAGE
        = "Required format: \"<strategy> <acceptor> [<resultcount>]\"";
}
