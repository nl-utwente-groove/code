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
package groove.verify;

import java.util.HashMap;
import java.util.Map;

import groove.annotation.Syntax;
import groove.annotation.ToolTipBody;
import groove.annotation.ToolTipHeader;
import groove.util.parse.Op;
import groove.util.parse.OpKind;

/** The kind (i.e., top level operator) of a formula. */
public enum LogicOp implements Op {
    /** Proposition, wrapped in an object of type {@link Proposition}. */
    @Syntax("rule [LPAR arg_list RPAR] | string_constant")
    @ToolTipHeader("Atomic or rule call proposition")
    @ToolTipBody({"Holds if %s is enabled in the current state.",
        "Note that this does <i>not</i> mean that %1$s has just been executed.",
        "Without arguments, only the rule name is checked.",
        "Arguments may include the wildcard '_', which matches everything."})
    PROP("", OpKind.ATOM),

    /** True. */
    @Syntax("TRUE")
    @ToolTipHeader("True")
    @ToolTipBody("Trivially holds in every state.")
    TRUE("true", OpKind.ATOM),

    /** False. */
    @Syntax("FALSE")
    @ToolTipHeader("False")
    @ToolTipBody("Always fails to hold.")
    FALSE("false", OpKind.ATOM),

    /** Negation. */
    @Syntax("NOT form")
    @ToolTipHeader("Negation")
    @ToolTipBody("Holds if and only if %s does not hold.")
    NOT("!", OpKind.NOT),

    /** Disjunction. */
    @Syntax("form1 OR form2")
    @ToolTipHeader("Disjunction")
    @ToolTipBody("Either %s or %s (or both) holds.")
    OR("|", OpKind.OR),

    /** Conjunction. */
    @Syntax("form1 AND form2")
    @ToolTipHeader("Conjunction")
    @ToolTipBody("Both %s and %s hold.")
    AND("&", OpKind.AND),

    /** Implication. */
    @Syntax("pre IMPLIES post")
    @ToolTipHeader("Implication")
    @ToolTipBody({"Either%s fails to hold, or %s holds;", "in other words, %1$s implies %2$s."})
    IMPLIES("->", OpKind.IMPLIES),

    /** Inverse implication. */
    @Syntax("post FOLLOWS pre")
    @ToolTipHeader("Inverse implication")
    @ToolTipBody({"Either %2$s fails to hold, or %1$s holds;",
        "in other words, %1$s is implied by %2$s."})
    FOLLOWS("<-", OpKind.IMPLIES),

    /** Equivalence. */
    @Syntax("form1 EQUIV form2")
    @ToolTipHeader("Equivalence")
    @ToolTipBody("Either %s and %s both hold, or both fail to hold.")
    EQUIV("<->", OpKind.EQUIV),

    /** Next-state. */
    @Syntax("NEXT form")
    @ToolTipHeader("Next")
    @ToolTipBody({"In the next state of the current path, %s holds."})
    NEXT("X", OpKind.TEMP_PREFIX),

    /** Temporal until. */
    @Syntax("first UNTIL second")
    @ToolTipHeader("Until")
    @ToolTipBody({"%1$s holds up until one state before %2$s holds,",
        "and %2$s will indeed eventually hold."})
    UNTIL("U", OpKind.TEMP_INFIX),

    /** Weak temporal until (second operand may never hold). */
    @Syntax("first W_UNTIL second")
    @ToolTipHeader("Weak until")
    @ToolTipBody({"Either %1$s holds up until one state before %2$s holds,",
        "or %1$s holds forever."})
    W_UNTIL("W", OpKind.TEMP_INFIX),

    /** Temporal release. */
    RELEASE("R", OpKind.TEMP_INFIX),

    /** Strong temporal release (second operand must eventually hold). */
    S_RELEASE("M", OpKind.TEMP_INFIX),

    /** Everywhere along a path. */
    @Syntax("ALWAYS form")
    @ToolTipHeader("Globally")
    @ToolTipBody("In all states of the current path %s holds.")
    ALWAYS("G", OpKind.TEMP_PREFIX),

    /** Eventually along a path. */
    @Syntax("EVENTUALLY form")
    @ToolTipHeader("Eventually")
    @ToolTipBody("There is a state of the current path in which %s holds.")
    EVENTUALLY("F", OpKind.TEMP_PREFIX),

    /** For all paths. */
    @Syntax("FORALL form")
    @ToolTipHeader("For all paths")
    @ToolTipBody("Along all paths starting in the current state %s holds.")
    FORALL("A", OpKind.TEMP_PREFIX),

    /** There exists a path. */
    @Syntax("EXISTS form")
    @ToolTipHeader("For some path")
    @ToolTipBody("There is a path, starting in the current state, along which %s holds.")
    EXISTS("E", OpKind.TEMP_PREFIX),

    /** Left parenthesis. */
    @Syntax("LPAR form RPAR")
    @ToolTipHeader("Bracketed formula")
    LPAR("(", OpKind.NONE),

    /** Right parenthesis. */
    RPAR(")", OpKind.NONE);

    /** Private constructor for an operator token. */
    private LogicOp(String symbol, OpKind kind) {
        assert symbol != null;
        this.symbol = symbol;
        this.arity = kind.getArity();
        this.kind = kind;
        this.priority = kind.ordinal();
    }

    @Override
    public String toString() {
        return getSymbol();
    }

    @Override
    public String getSymbol() {
        return this.symbol;
    }

    /** The symbol for the top-level operator. */
    private final String symbol;

    @Override
    public int getArity() {
        return this.arity;
    }

    /** The number of operands of a formula of this kind. */
    private final int arity;

    @Override
    public OpKind getKind() {
        return this.kind;
    }

    /** The kind of this operator. */
    private final OpKind kind;

    /** Returns the priority of the operator. */
    public int getPriority() {
        return this.priority;
    }

    /** The priority of the top-level operator. */
    private final int priority;

    /** Returns the logic operator corresponding to a given one-character symbol. */
    public static LogicOp getCompareOp(char c) {
        if (compareOpMap == null) {
            compareOpMap = new HashMap<>();
            registerOp(compareOpMap, EVENTUALLY);
            registerOp(compareOpMap, ALWAYS);
            registerOp(compareOpMap, EXISTS);
            registerOp(compareOpMap, FORALL);
            registerOp(compareOpMap, NEXT);
        }
        return compareOpMap.get(c);
    }

    private static void registerOp(Map<Character,LogicOp> map, LogicOp op) {
        String symbol = op.getSymbol();
        assert symbol.length() == 1;
        map.put(symbol.charAt(0), op);
    }

    private static Map<Character,LogicOp> compareOpMap;
}