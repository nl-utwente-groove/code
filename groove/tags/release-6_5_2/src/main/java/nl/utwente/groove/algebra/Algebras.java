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
package nl.utwente.groove.algebra;

import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.utwente.groove.annotation.Help;
import nl.utwente.groove.annotation.HelpMap;
import nl.utwente.groove.annotation.OpSymbol;
import nl.utwente.groove.annotation.ToolTipBody;
import nl.utwente.groove.annotation.ToolTipHeader;
import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.LazyFactory;
import nl.utwente.groove.util.Strings;
import nl.utwente.groove.util.parse.OpKind.Placement;

/**
 * Helper class for algebra manipulation.
 * @author Arend Rensink
 * @version $Revision $
 */
public class Algebras {
    /** Private constructor to prevent this class from being instantiated. */
    private Algebras() {
        // empty
    }

    /**
     * Checks if all generic types used in the signature declaration
     * are actually themselves signatures.
     */
    private static void checkSignatureConsistency() {
        for (Sort sigKind : Sort.values()) {
            for (TypeVariable<?> type : sigKind.getSignatureClass().getTypeParameters()) {
                String typeName = type.getName().toLowerCase();
                if (Sort.getSort(typeName) == null) {
                    throw Exceptions
                        .illegalArg("Type '%s' not declared by any signature", typeName);
                }
            }
        }
    }

    static {
        checkSignatureConsistency();
    }

    /**
     * Returns a syntax helper mapping for all operators.
     */
    public static HelpMap getOpDocMap() {
        return opDocMap.get();
    }

    private static HelpMap computeOpDocMap() {
        var result = new HelpMap();
        Map<String,String> sigMap = new HashMap<>(tokenMap);
        for (Method method : GSignature.class.getMethods()) {
            sigMap.put("Q" + method.getName(), method.getName());
            Help help = Help.createHelp(method, sigMap);
            if (help != null) {
                // add a message about the signatures that implement this method
                var forSorts = getSorts(method);
                var plural = forSorts.size() > 1
                    ? "s "
                    : " ";
                var ending = forSorts.size() > 1
                    ? "; may be optionally prefixed by its intended sort to resolve type ambiguity."
                    : ".";
                help
                    .addBody(Groove
                        .toString(forSorts.toArray(),
                                  "<p style=\"margin-top:5;\"/>Available for %1$s value" + plural,
                                  ending, ",", " and "));
                // add a message about prefix or infix notation
                @SuppressWarnings("cast")
                var op = method.getAnnotation((Class<OpSymbol>) OpSymbol.class);
                if (op != null) {
                    var body = switch (op.kind().getPlace()) {
                    case INFIX -> "Alternative (infix) notation: %%2$s %s %%3$s.";
                    case PREFIX -> "Alternative (prefix) notation: %s %%2$s.";
                    default -> throw Exceptions.UNREACHABLE;
                    };
                    help
                        .addBody("<p style=\"margin-top:5;\"/>"
                            + String.format(body, convert(op.symbol(), true)));
                }
                // parameter documentation
                help.addPar("the sort to which the operator belongs");
                for (var param : method.getGenericParameterTypes()) {
                    var sortName = param.getTypeName();
                    if (sortName.equals("MAIN") || sortName.endsWith("List")) {
                        sortName = "%1$s";
                    }
                    help.addPar(sortName + "-typed expression");
                }
                result.add(help);
            }
        }
        return result;
    }

    /** Syntax helper map for operators, from syntax items to associated tool tips. */
    private static final LazyFactory<HelpMap> opDocMap
        = LazyFactory.instance(Algebras::computeOpDocMap);

    /**
     * Returns a syntax helper mapping for all operators.
     */
    public static HelpMap getExprDocMap() {
        return exprDocMap.get();
    }

    private static HelpMap computeExprDocMap() {
        var result = new HelpMap();
        Map<String,String> sigMap = new HashMap<>(tokenMap);
        Map<OpSymbol,List<Method>> opMap = new HashMap<>();
        // collect operators
        for (Method method : GSignature.class.getMethods()) {
            sigMap.put("Q" + method.getName(), method.getName());
            @SuppressWarnings("cast")
            var op = method.getAnnotation((Class<OpSymbol>) OpSymbol.class);
            if (op != null) {
                var opMethods = opMap.get(op);
                if (opMethods == null) {
                    opMap.put(op, opMethods = new ArrayList<>());
                }
                opMethods.add(method);
            }
        }
        // create help for operators
        for (var opEntry : opMap.entrySet()) {
            // create a help item for this infix symbol
            var op = opEntry.getKey();
            var methods = opEntry.getValue();
            Help help = new Help(sigMap);
            help.setSyntax(createSyntax(op));
            help.setHeader(createHeader(methods));
            help.setBody(createBody(methods));
            help
                .addPar("expression of sort "
                    + Groove.toString(getSorts(methods).toArray(), "", "", ",", " or "));
            if (op.kind().getPlace() == Placement.INFIX) {
                help.addPar("expression of the same sort as %1$s");
            }
            result.add(help);
        }
        // other expressions
        for (var sort : Sort.values()) {
            result.add(getLiteralHelp(sort));
        }
        result.add(getParenthesisHelp());
        result.add(getOperatorHelp());
        return result;
    }

    /** Syntax helper map for expressions, from syntax items to associated tool tips. */
    private static final LazyFactory<HelpMap> exprDocMap
        = LazyFactory.instance(Algebras::computeExprDocMap);

    static private Help getLiteralHelp(Sort sort) {
        try {
            return Help.createHelp(sort.getClass().getField(sort.name()), tokenMap);
        } catch (NoSuchFieldException exc) {
            throw Exceptions.UNREACHABLE;
        }
    }

    static private Help getParenthesisHelp() {
        var result = new Help(tokenMap);
        result.setSyntax("LPAR. expr .RPAR");
        result.setHeader("Parenthesised expression");
        result.addBody("Parenthesis can be used to circumvent operator priority.");
        return result;
    }

    static private Help getOperatorHelp() {
        var result = new Help(tokenMap);
        result.setSyntax("[sort.COLON].name.LPAR.pars.RPAR");
        result.setHeader("operator invocation");
        result.addBody("Call of an algebraic operator; for further information see the Ops tab");
        result.addPar("Optional sort to which the operator belongs");
        result.addPar("Name of the operator");
        result.addPar("List of operand expressions");
        return result;
    }

    /** Constructs a collective help header for a set of methods. */
    static private String createHeader(List<Method> methods) {
        var headers = new ArrayList<String>();
        methods
            .stream()
            .map(m -> m.getAnnotation(ToolTipHeader.class))
            .map(ToolTipHeader::value)
            .map(Strings::toLower)
            .forEach(headers::add);
        var header = Groove.toString(headers.toArray(), "", "", ", ", " or ");
        return Strings.toUpper(header);
    }

    /** Constructs a help syntax line for a given operator. */
    static private String createSyntax(OpSymbol op) {
        return switch (op.kind().getPlace()) {
        case INFIX -> "expr1 " + convert(op.symbol(), false) + " expr2";
        case PREFIX -> convert(op.symbol(), false) + " expr";
        default -> throw Exceptions.UNREACHABLE;
        };
    }

    /** Constructs a collective help body for a set of methods. */
    static private String createBody(List<Method> methods) {
        String result = "";
        for (var m : methods) {
            if (methods.size() > 1) {
                var sorts = getSorts(m);
                result += Groove.toString(sorts.toArray(), "<li> For ", ": ", ", ", " or ");
            }
            for (var line : m.getAnnotation(ToolTipBody.class).value()) {
                result += line;
            }
        }
        return result.replaceAll("\\%2\\$s", "\\%1\\$s").replaceAll("\\%3\\$s", "\\%2\\$s");
    }

    /** Formats a string so that it is usable in a HTML context
     * as well as (optionally) as {@link String#format} parameter. */
    static private String convert(String text, boolean format) {
        if (format) {
            text = text.replace("%", "%%");
        }
        // convert keywords in the token map
        for (var e : tokenMap.entrySet()) {
            if (Character.isLowerCase(e.getValue().charAt(0))) {
                text = text.replaceAll(e.getValue(), e.getKey());
            }
        }
        text = HTMLConverter.toHtml(text);
        return text;
    }

    /** Returns the list of sort names that declare a given operator method. */
    static private Set<String> getSorts(Method method) {
        var result = new LinkedHashSet<String>();
        for (var sort : Sort.values()) {
            try {
                sort
                    .getSignatureClass()
                    .getDeclaredMethod(method.getName(), method.getParameterTypes());
                // when we get here, the method is (re)declared in the signature
                result.add(sort.name());
            } catch (NoSuchMethodException exc) {
                // the method does not exist in this signature
            }
        }
        return result;
    }

    /** Returns the list of sort names that declare one of a list of operator methods. */
    static private Set<String> getSorts(List<Method> methods) {
        var result = new LinkedHashSet<String>();
        methods.stream().map(Algebras::getSorts).forEach(result::addAll);
        return result;
    }

    /**
     * Mapping from keywords in syntax descriptions to corresponding text.
     */
    private static final Map<String,String> tokenMap;

    static {
        tokenMap = new HashMap<>();
        tokenMap.put("LPAR", "(");
        tokenMap.put("BAR", " | ");
        tokenMap.put("RPAR", ")");
        tokenMap.put("COMMA", ",");
        tokenMap.put("COLON", ":");
        tokenMap.put("QUOTE", "\"");
        tokenMap.put("DOT", ".");
        tokenMap.put("TRUE", "true");
        tokenMap.put("FALSE", "false");
        for (var sort : Sort.values()) {
            tokenMap.put(sort.name(), sort.getName());
        }
    }
}
