/**
 * Copyright (C) 2006 United States Government as represented by the
 * Administrator of the National Aeronautics and Space Administration
 * (NASA).  All Rights Reserved.
 *
 * This software is distributed under the NASA Open Source Agreement
 * (NOSA), version 1.3.  The NOSA has been approved by the Open Source
 * Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
 * directory tree for the complete NOSA document.
 *
 * THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
 * KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
 * LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
 * SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
 * THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
 * DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
 */
package groove.verify;

import static groove.util.parse.ATermTreeParser.TokenClaz.CONST;
import static groove.util.parse.ATermTreeParser.TokenClaz.LPAR;
import static groove.util.parse.ATermTreeParser.TokenClaz.NAME;
import static groove.util.parse.ATermTreeParser.TokenClaz.RPAR;
import static groove.util.parse.ATermTreeParser.TokenClaz.UNDER;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import groove.algebra.Sort;
import groove.annotation.Help;
import groove.grammar.QualName;
import groove.util.parse.ATermTreeParser;
import groove.util.parse.FormatException;
import groove.util.parse.OpKind;
import groove.verify.Proposition.Arg;

/**
 * Parser for temporal formulas, following the {@code groove.util.parse} architecture.
 * @author Arend Rensink
 * @version $Revision $
 */
public class FormulaParser extends ATermTreeParser<LogicOp,Formula> {
    /**
     * Constructs a new parser.
     */
    private FormulaParser() {
        super(new Formula(LogicOp.PROP));
        setQualIds(true);
    }

    /* Allows user-defined calls, with atomic operands. */
    @Override
    protected Formula parseName() throws FormatException {
        assert has(NAME);
        Formula result = null;
        Token firstToken = next();
        // see if the name is a sequence of one-character prefix operators
        List<LogicOp> prefixOps = findPrefixOps(firstToken.substring());
        if (prefixOps == null) {
            QualName id = parseId();
            if (consume(LPAR) == null) {
                // it's an (unquoted) string constant: create an atomic proposition
                result = Formula.atom(id);
            } else {
                result = createTree(LogicOp.PROP);
                List<Proposition.Arg> args = new ArrayList<>();
                if (consume(RPAR) == null) {
                    args.add(parseArg());
                    while (consume(TokenClaz.COMMA) != null) {
                        args.add(parseArg());
                    }
                    if (consume(RPAR) == null) {
                        throw expectedToken(RPAR, next());
                    }
                }
                result.setProp(new Proposition(id, args));
            }
        } else {
            consume(NAME);
            Formula arg = result = parse(OpKind.COMPARE);
            for (LogicOp op : prefixOps) {
                result = createTree(op);
                result.addArg(arg);
                arg = result;
            }
        }
        setParseString(result, firstToken);
        return result;
    }

    /** Attempts to parse the input as a call argument. */
    private Proposition.Arg parseArg() throws FormatException {
        Proposition.Arg result;
        Token atomToken = next();
        if (atomToken.has(TokenClaz.CONST)) {
            consume(CONST);
            result = Arg.arg(atomToken.createConstant());
        } else if (atomToken.has(NAME)) {
            consume(NAME);
            String name = atomToken.substring();
            result = Arg.arg(name);
        } else if (atomToken.has(UNDER)) {
            consume(UNDER);
            result = Proposition.Arg.WILD_ARG;
        } else {
            throw unexpectedToken(atomToken);
        }
        return result;
    }

    /* Converts string constants into label propositions. */
    @Override
    protected Formula parseConst() throws FormatException {
        Formula result = createTree(LogicOp.PROP);
        Token constToken = consume(CONST);
        Sort sort = constToken.type(CONST)
            .sort();
        if (sort != Sort.STRING) {
            throw new FormatException("Can't parse '%s' constant as formula at index '%s'", sort,
                constToken.start());
        }
        String label = constToken.createConstant()
            .getStringRepr();
        result = Formula.atom(label);
        setParseString(result, constToken);
        return result;
    }

    /** Returns an inversely ordered list of single-character prefix operators
     * corresponding to a given string.
     */
    private List<LogicOp> findPrefixOps(String input) {
        List<LogicOp> result = new ArrayList<>();
        for (int i = input.length() - 1; i >= 0; i--) {
            LogicOp op = LogicOp.getCompareOp(input.charAt(i));
            if (op == null) {
                result = null;
                break;
            }
            result.add(op);
        }
        return result;
    }

    /**
     * Returns a mapping from syntax documentation lines to associated (possibly {@code null}) tooltips.
     * @param logic the logic variant concerned
     */
    public static Map<String,String> getDocMap(Logic logic) {
        Map<String,String> result = docMapMap.get(logic);
        if (result == null) {
            docMapMap.put(logic, result = computeDocMap(logic));
        }
        return result;
    }

    /**
     * Computes a mapping from syntax documentation lines to associated (possibly {@code null}) tooltips.
     * @param logic the logic variant concerned
     */
    private static Map<String,String> computeDocMap(Logic logic) {
        Map<String,String> result = new LinkedHashMap<>();
        for (Field field : LogicOp.class.getFields()) {
            if (field.isEnumConstant()) {
                LogicOp token = nameToTokenMap.get(field.getName());
                if (logic.getOps()
                    .contains(token)) {
                    Help help = Help.createHelp(field, nameToSymbolMap);
                    if (help != null) {
                        result.put(help.getItem(), help.getTip());
                    }
                }
            }
        }
        return result;
    }

    /** Mapping from token names to token values. */
    private static Map<String,LogicOp> nameToTokenMap = new HashMap<>();
    /** Mapping from token symbols to token values. */
    private static Map<String,String> nameToSymbolMap = new HashMap<>();
    private static Map<Logic,Map<String,String>> docMapMap =
        new EnumMap<>(Logic.class);

    static {
        for (LogicOp token : LogicOp.values()) {
            nameToTokenMap.put(token.name(), token);
            nameToSymbolMap.put(token.name(), token.getSymbol());
        }
    }

    /** Returns the singleton instance of this parser not specialised to any logic. */
    public final static FormulaParser instance() {
        return INSTANCE;
    }

    /** Returns the singleton instance of this parser for CTL or LTL. */
    public final static FormulaParser instance(Logic logic) {
        return logic == Logic.LTL ? LTL_INSTANCE : CTL_INSTANCE;
    }

    private final static FormulaParser INSTANCE = new FormulaParser();
    private final static FormulaParser LTL_INSTANCE = new FormulaParser() {
        @Override
        public Formula parse(String input) {
            Formula result = super.parse(input);
            try {
                result.toLtlFormula();
            } catch (FormatException exc) {
                // do nothing; errors are now stored in the formula object
            }
            return result;
        }
    };
    private final static FormulaParser CTL_INSTANCE = new FormulaParser() {
        @Override
        public Formula parse(String input) {
            Formula result = super.parse(input);
            try {
                result = result.toCtlFormula();
            } catch (FormatException exc) {
                // do nothing; errors are now stored in the formula object
            }
            return result;
        }
    };
}
