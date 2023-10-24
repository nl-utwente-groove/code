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
import java.util.TreeMap;

import nl.utwente.groove.annotation.Help;
import nl.utwente.groove.annotation.InfixSymbol;
import nl.utwente.groove.annotation.PrefixSymbol;
import nl.utwente.groove.io.HTMLConverter;
import nl.utwente.groove.util.Exceptions;
import nl.utwente.groove.util.Groove;
import nl.utwente.groove.util.LazyFactory;
import nl.utwente.groove.util.Strings;

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
                if (Sort.getKind(typeName) == null) {
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
    public static Map<String,String> getOpDocMap() {
        return opDocMap.get();
    }

    private static Map<String,String> computeOpDocMap() {
        Map<String,String> result = new TreeMap<>();
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
                        .toString(forSorts.toArray(), "<p/><p/>Available for %1$s value" + plural,
                                  ending, ",", " and "));
                // add a message about prefix or infix notation
                @SuppressWarnings("cast")
                var infix = method.getAnnotation((Class<InfixSymbol>) InfixSymbol.class);
                if (infix != null) {
                    help
                        .addBody("<p/><p/>Alternative (infix) notation: %2$s "
                            + HTMLConverter.toHtml(infix.symbol()) + " %3$s.");
                }
                @SuppressWarnings("cast")
                var prefix = method.getAnnotation((Class<PrefixSymbol>) PrefixSymbol.class);
                if (prefix != null) {
                    help
                        .addBody("<p/><p/>Alternative (prefix) notation: " + prefix.symbol()
                            + "%2$s.");
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
                result.put(help.getItem(), help.getTip());
            }
        }
        return result;
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

    /** Syntax helper map for operators, from syntax items to associated tool tips. */
    private static final LazyFactory<Map<String,String>> opDocMap
        = LazyFactory.instance(Algebras::computeOpDocMap);

    /**
     * Returns a syntax helper mapping for all operators.
     */
    public static Map<String,String> getExprDocMap() {
        return exprDocMap.get();
    }

    private static Map<String,String> computeExprDocMap() {
        Map<String,String> result = new TreeMap<>();
        Map<String,String> sigMap = new HashMap<>(tokenMap);
        Map<String,List<Method>> infixMap = new HashMap<>();
        Map<String,List<Method>> prefixMap = new HashMap<>();
        // collect all prefix and infix operators
        for (Method method : GSignature.class.getMethods()) {
            sigMap.put("Q" + method.getName(), method.getName());
            @SuppressWarnings("cast")
            var infix = method.getAnnotation((Class<InfixSymbol>) InfixSymbol.class);
            if (infix != null) {
                var infixes = infixMap.get(infix.symbol());
                if (infixes == null) {
                    infixMap.put(infix.symbol(), infixes = new ArrayList<>());
                }
                infixes.add(method);
            }
            @SuppressWarnings("cast")
            var prefix = method.getAnnotation((Class<PrefixSymbol>) PrefixSymbol.class);
            if (prefix != null) {
                var prefixes = prefixMap.get(prefix.symbol());
                if (prefixes == null) {
                    prefixMap.put(prefix.symbol(), prefixes = new ArrayList<>());
                }
                prefixes.add(method);
            }
        }
        for (var infixEntry : infixMap.entrySet()) {
            // create a help item for this infix symbol
            var methods = infixEntry.getValue();
            var helps = new ArrayList<Help>();
            methods.stream().map(m -> Help.createHelp(m, sigMap)).forEach(helps::add);
            var tokenMap = new HashMap<String,String>();
            helps.stream().map(Help::getTokenMap).forEach(tokenMap::putAll);
            Help infixHelp = new Help(tokenMap);
            // add the syntax line
            var syntax = "expr1" + HTMLConverter.toHtml(infixEntry.getKey()) + "expr2";
            infixHelp.setSyntax(syntax);
            // add the header
            var headers = new ArrayList<String>();
            helps.stream().map(Help::getHeader).map(Strings::toLower).forEach(headers::add);
            var header = Groove.toString(headers.toArray(), "", "", ", ", " or ");
            header = Strings.toUpper(header);
            infixHelp.setHeader(header);
            // add the body
            var bodyBuilder = new StringBuilder();
            var allSorts = getSorts(methods.get(0));
            if (methods.size() == 1) {
                bodyBuilder.append(helps.get(0).getBody());
            } else {
                for (int i = 0; i < helps.size(); i++) {
                    var sorts = getSorts(methods.get(i));
                    bodyBuilder
                        .append(Groove.toString(sorts.toArray(), "<li> For ", ": ", ", ", " or "));
                    bodyBuilder.append(helps.get(i).getBody());
                    allSorts.addAll(sorts);
                }
            }
            String body = bodyBuilder
                .toString()
                .replaceAll("\\%2\\$s", "\\%1\\$s")
                .replaceAll("\\%3\\$s", "\\%2\\$s");
            infixHelp.setBody(body);
            infixHelp
                .addPar("expression of sort "
                    + Groove.toString(allSorts.toArray(), "", "", ",", " or "));
            infixHelp.addPar("expression of the same sort as %1$s");
            result.put(infixHelp.getItem(), infixHelp.getTip());
        }
        return result;
    }

    /** Syntax helper map for expressions, from syntax items to associated tool tips. */
    private static final LazyFactory<Map<String,String>> exprDocMap
        = LazyFactory.instance(Algebras::computeExprDocMap);

    /**
     * Mapping from keywords in syntax descriptions to corresponding text.
     */
    private static final Map<String,String> tokenMap;

    static {
        tokenMap = new HashMap<>();
        tokenMap.put("LPAR", "(");
        tokenMap.put("RPAR", ")");
        tokenMap.put("COMMA", ",");
        tokenMap.put("COLON", ":");
        tokenMap.put("TRUE", "true");
        tokenMap.put("FALSE", "false");
        for (var sort : Sort.values()) {
            tokenMap.put(sort.name(), sort.getName());
        }
    }
}
