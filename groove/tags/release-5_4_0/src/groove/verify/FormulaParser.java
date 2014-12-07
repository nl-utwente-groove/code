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

import static groove.util.parse.TermTreeParser.TokenClaz.CONST;
import static groove.util.parse.TermTreeParser.TokenClaz.LPAR;
import static groove.util.parse.TermTreeParser.TokenClaz.NAME;
import static groove.util.parse.TermTreeParser.TokenClaz.RPAR;
import groove.algebra.Constant;
import groove.algebra.Sort;
import groove.annotation.Help;
import groove.util.parse.FormatException;
import groove.util.parse.Id;
import groove.util.parse.OpKind;
import groove.util.parse.TermTreeParser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser for temporal formulas, following the {@code groove.util.parse} architecture.
 * @author Arend Rensink
 * @version $Revision $
 */
public class FormulaParser extends TermTreeParser<LogicOp,Formula> {
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
            Id id = parseId();
            result = createTree(has(LPAR) ? LogicOp.CALL : LogicOp.PROP);
            result.setId(id);
            if (consume(LPAR) != null) {
                if (consume(RPAR) == null) {
                    result.addArg(parseArg());
                    while (consume(TokenClaz.COMMA) != null) {
                        result.addArg(parseArg());
                    }
                    if (consume(RPAR) == null) {
                        throw expectedToken(RPAR, next());
                    }
                }
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

    /** Attempts to parse the input as an atomic formula:
     * a constant or simple name.
     */
    private Formula parseArg() throws FormatException {
        Formula result = createTree(LogicOp.ARG);
        Token atomToken = next();
        if (atomToken.has(TokenClaz.CONST)) {
            consume(CONST);
            result.setConstant(Constant.instance(atomToken.substring()));
        } else if (atomToken.has(NAME)) {
            Id id = parseId();
            if (id.size() > 1) {
                throw invalidArg(id.getName(), atomToken);
            }
            result.setId(id);
        } else {
            throw unexpectedToken(atomToken);
        }
        setParseString(result, atomToken);
        return result;
    }

    /** Converts all constants into strings. */
    @Override
    protected Formula parseConst() throws FormatException {
        Formula result = createTree(getAtomOp());
        Token constToken = consume(CONST);
        Constant constant;
        if (constToken.type(CONST).sort() == Sort.STRING) {
            constant = constToken.createConstant();
        } else {
            constant = Constant.instance(constToken.substring());
        }
        result.setConstant(constant);
        setParseString(result, constToken);
        return result;
    }

    private FormatException invalidArg(String arg, Token token) {
        return new FormatException("Invalide call argument '%s' at index '%s'", arg, token.start());
    }

    /** Returns an inversely ordered list of single-character prefix operators
     * corresponding to a given string.
     */
    private List<LogicOp> findPrefixOps(String input) {
        List<LogicOp> result = new ArrayList<LogicOp>();
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
        Map<String,String> result = new LinkedHashMap<String,String>();
        for (Field field : LogicOp.class.getFields()) {
            if (field.isEnumConstant()) {
                LogicOp token = nameToTokenMap.get(field.getName());
                if (logic.getOps().contains(token)) {
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
    private static Map<String,LogicOp> nameToTokenMap = new HashMap<String,LogicOp>();
    /** Mapping from token symbols to token values. */
    private static Map<String,String> nameToSymbolMap = new HashMap<String,String>();
    private static Map<Logic,Map<String,String>> docMapMap = new EnumMap<Logic,Map<String,String>>(
        Logic.class);

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
