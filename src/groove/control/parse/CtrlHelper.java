/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.control.parse;

import groove.algebra.AlgebraFamily;
import groove.control.CtrlAut;
import groove.control.CtrlCall;
import groove.control.CtrlCall.Kind;
import groove.control.CtrlPar;
import groove.control.CtrlState;
import groove.control.CtrlTransition;
import groove.control.CtrlType;
import groove.control.CtrlVar;
import groove.grammar.QualName;
import groove.grammar.model.FormatErrorSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

/**
 * Helper class for GCL parsing.
 * Acts as an interface between the grammar and the namespace.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlHelper {
    /** Constructs a helper object for a given parser and namespace. */
    public CtrlHelper(BaseRecognizer recogniser, Namespace namespace,
            AlgebraFamily family) {
        this.namespace = namespace;
        this.algebraFamily = family;
    }

    /** Returns the full name of the control program currently being parsed. */
    String getControlName() {
        return this.namespace.getFullName();
    }

    /** Sets the package for the declared names. */
    void setPackage(CommonTree packageTree) {
        this.packageName = packageTree.getText();
        String parentName = this.namespace.getParentName();
        if (!parentName.equals(this.packageName)) {
            emitErrorMessage(
                packageTree,
                "Package declaration '%s' does not equal program location '%s'",
                this.packageName, parentName);
        }
    }

    /** Adds an import to this compilation unit. */
    void addImport(CommonTree importTree) {
        String fullName = importTree.getText();
        String name = QualName.getLastName(fullName);
        this.importMap.put(name, fullName);
    }

    /** Closes the current variable scope. */
    void openScope() {
        this.symbolTable.openScope();
    }

    /** Opens a new variable scope. */
    void closeScope() {
        this.symbolTable.closeScope();
    }

    /**
     * Declares a new branch in the program. This checkpoints the set of
     * initialised variables.
     */
    @SuppressWarnings("unchecked")
    void startBranch() {
        this.initVarScopes.push(new Set[] {new HashSet<String>(this.initVars),
            null});
    }

    /**
     * Switches to the next option in the top level branch of the program.
     */
    void nextBranch() {
        Set<String>[] topInitVarScope = this.initVarScopes.peek();
        if (topInitVarScope[1] == null) {
            topInitVarScope[1] = new HashSet<String>(this.initVars);
        } else {
            topInitVarScope[1].retainAll(this.initVars);
        }
        this.initVars = new HashSet<String>(topInitVarScope[0]);
    }

    /**
     * Ends the top level branch of the program. Sets the initialised variables
     * to those initialised in every option.
     */
    void endBranch() {
        Set<String>[] topInitVarScope = this.initVarScopes.pop();
        this.initVars = topInitVarScope[0];
        if (topInitVarScope[1] != null) {
            this.initVars.retainAll(topInitVarScope[1]);
        }
    }

    /**
     * Creates a new tree with a CtrlParser#ID} token at the root, and 
     * an empty text.
     */
    CommonTree emptyPackage() {
        CtrlTree result = new CtrlTree(new CommonToken(CtrlParser.PACKAGE));
        result.addChild(toQualName(Collections.<Token>emptyList()));
        return result;
    }

    /** Creates a new tree with a {@link CtrlParser#ID} token at the root,
     * of which the text is the concatenation of the children of the given tree.
     */
    CommonTree toQualName(List<? extends Token> children) {
        CommonToken token = new CommonToken(CtrlParser.ID, flatten(children));
        // set the line/column info to get useful error output
        if (!children.isEmpty()) {
            CommonToken child = (CommonToken) children.get(0);
            token.setLine(child.getLine());
            token.setTokenIndex(child.getTokenIndex());
        }
        return new CtrlTree(token);
    }

    /** 
     * Tests if the rule name is qualified;
     * if not, first tries to look it up in the import map, and if that fails,
     * prefixes it with the package name.
     */
    CommonTree lookup(CommonTree ruleNameToken) {
        CommonTree result = ruleNameToken;
        String name = ruleNameToken.getText();
        if (QualName.getParent(name).isEmpty()) {
            if (this.importMap.containsKey(name)) {
                name = this.importMap.get(name);
            } else {
                name = QualName.extend(this.packageName, name);
            }
            CommonToken token = new CommonToken(CtrlParser.ID, name);
            token.setLine(ruleNameToken.getLine());
            token.setTokenIndex(ruleNameToken.getToken().getTokenIndex());
            result = new CtrlTree(token);
        }
        if (!this.namespace.hasName(name)) {
            emitErrorMessage(result, "Unknown name or identifier %s", name);
        }
        return result;
    }

    /** Qualifies a given name by prefixing it with the package name. */
    String qualify(String name) {
        return QualName.extend(this.namespace.getParentName(), name);
    }

    /** 
     * Looks up a name in the import map,
     * returning either the imported qualified name if there is any,
     * or the package-prefixed qualification of the looked-up name otherwise.
     */
    String looukp(String name) {
        String result = this.importMap.get(name);
        if (result == null) {
            result = qualify(name);
        }
        return name;
    }

    /** Returns a dot-separated string consisting of a number of token texts. */
    String flatten(List<?> children) {
        String result = "";
        for (Object token : children) {
            result = QualName.extend(result, ((CommonToken) token).getText());
        }
        return result.toString();
    }

    /** 
     * Attempts to add a function or recipe declaration with a given name.
     * Checks for overlap with the previously declared names.
     * @return {@code true} if no rule, function or recipe with the name of this one was
     * already declared; {@code false} otherwise
     */
    boolean declareName(Tree functionTree, CtrlFragment fragment) {
        boolean result = false;
        assert (functionTree.getType() == CtrlParser.FUNCTION || functionTree.getType() == CtrlParser.RECIPE)
            && functionTree.getChildCount() <= 3;
        String name = qualify(functionTree.getChild(0).getText());
        if (this.namespace.hasName(name)) {
            emitErrorMessage(functionTree,
                "Duplicate name: %s %s already defined",
                this.namespace.getKind(name).getName(true), name);
        } else if (functionTree.getType() == CtrlParser.FUNCTION) {
            // it's a function
            this.namespace.addFunction(name, new ArrayList<CtrlPar.Var>());
            result = true;
        } else {
            // it's a recipe
            String priority =
                functionTree.getChildCount() == 2 ? "0"
                        : functionTree.getChild(1).getText();
            this.namespace.addRecipe(name, Integer.parseInt(priority),
                new ArrayList<CtrlPar.Var>(), fragment.getName(),
                fragment.getStartLine());
            result = true;
        }
        return result;
    }

    /** Sets the current function name to a given value. */
    void startBody(CtrlTree nameTree, CtrlCall.Kind kind) {
        assert this.currentName == null;
        this.currentName = nameTree.getText();
        this.currentKind = kind;
    }

    /** Sets the current function name to {@code null}. */
    void endBody() {
        assert this.currentName != null;
        this.currentName = null;
        this.currentKind = null;
    }

    /** Reorders the functions according to their dependencies. */
    void reorderFunctions(CtrlTree functionsTree) {
        assert functionsTree.getType() == CtrlChecker.FUNCTIONS;
        int functionsCount = functionsTree.getChildCount();
        Map<String,CtrlTree> functionMap = new HashMap<String,CtrlTree>();
        for (int i = 0; i < functionsCount; i++) {
            CtrlTree function = functionsTree.getChild(i);
            functionMap.put(function.getChild(0).getText(), function);
        }
        Set<String> resolved = new LinkedHashSet<String>();
        for (int i = 0; i < functionsCount; i++) {
            String next = null;
            // look for the first function name of which all dependencies have been resolved
            for (String from : functionMap.keySet()) {
                Set<String> to = this.dependencyMap.get(from);
                if (!resolved.contains(from)
                    && (to == null || resolved.containsAll(to))) {
                    next = from;
                    break;
                }
            }
            if (next == null) {
                emitErrorMessage(functionsTree,
                    "Circular dependencies in function calls");
                break;
            }
            resolved.add(next);
        }
        if (resolved.size() == functionsCount) {
            int i = 0;
            for (String name : resolved) {
                functionsTree.setChild(i, functionMap.get(name));
                i++;
            }
        }
    }

    /** Prefixes a given name with the current function name, if any. */
    private String toLocalName(String name) {
        return this.currentName == null ? name : this.currentName + "." + name;
    }

    /** The function or transaction name currently processed. */
    private String currentName;
    /** The kind ofr {@link #currentName}. */
    private CtrlCall.Kind currentKind;

    boolean declareVar(Tree nameTree, CtrlTree typeTree) {
        boolean result = true;
        String name = toLocalName(nameTree.getText());
        if (!this.symbolTable.declareSymbol(name, typeTree.getCtrlType())) {
            emitErrorMessage(nameTree, "Duplicate local variable name %s", name);
            result = false;
        }
        return result;
    }

    /** Tests whether a package declaration is appropriate. */
    void checkPackage(CtrlTree importTree) {
        String name = importTree.getText();
        String actualName = this.namespace.getParentName();
        if (!name.equals(actualName)) {
            emitErrorMessage(importTree, "Package %s should be %s", name,
                actualName);
        }
    }

    /** Tests whether an imported name actually exists. */
    void checkImport(CtrlTree importTree) {
        String name = importTree.getText();
        if (!this.namespace.hasName(name)) {
            emitErrorMessage(importTree, "Imported name '%s' does not exist",
                name);
        }
    }

    /**
     * Checks the control type generated by a type tree node.
     * Returns the type and stores in the tree node.
     */
    CtrlType checkType(CtrlTree typeTree) {
        CtrlType result;
        if (typeTree.getType() == CtrlChecker.NODE) {
            result = CtrlType.NODE;
        } else {
            result = CtrlType.valueOf(typeTree.getText().toUpperCase());
        }
        typeTree.setCtrlType(result);
        return result;
    }

    /**
     * Checks whether a given variable has been declared and
     * (optionally) initialised.
     * 
     * @param nameTree the variable to be checked
     * @param checkInit if {@code true}, the variable should also be checked for initialisation
     * @return the type of the variable, if it was declared
     */
    CtrlVar checkVar(CtrlTree nameTree, boolean checkInit) {
        CtrlVar result = null;
        String name = toLocalName(nameTree.getText());
        CtrlType type = this.symbolTable.getType(name);
        if (type == null) {
            emitErrorMessage(nameTree, "Local variable %s not declared", name);
        } else {
            result = new CtrlVar(name, type);
            nameTree.setCtrlVar(result);
            if (checkInit && !this.initVars.contains(name)) {
                emitErrorMessage(nameTree,
                    "Variable %s may not have been initialised", name);
            } else {
                this.initVars.add(name);
            }
        }
        return result;
    }

    CtrlPar checkVarArg(CtrlTree argTree) {
        CtrlPar result = null;
        int childCount = argTree.getChildCount();
        assert argTree.getType() == CtrlChecker.ARG && childCount > 0
            && childCount <= 2;
        boolean isOutArg = childCount == 2;
        CtrlVar var = checkVar(argTree.getChild(childCount - 1), !isOutArg);
        if (var != null) {
            result = new CtrlPar.Var(var, !isOutArg);
            argTree.setCtrlPar(result);
        }
        return result;
    }

    CtrlPar checkDontCareArg(CtrlTree argTree) {
        assert argTree.getType() == CtrlChecker.ARG
            && argTree.getChildCount() == 1;
        CtrlPar result = new CtrlPar.Wild();
        argTree.setCtrlPar(result);
        return result;
    }

    CtrlPar checkConstArg(CtrlTree argTree) {
        assert argTree.getType() == CtrlChecker.ARG
            && argTree.getChildCount() == 1;
        String constant = argTree.getChild(0).getText();
        CtrlPar result =
            new CtrlPar.Const(this.algebraFamily.getAlgebraFor(constant),
                constant);
        argTree.setCtrlPar(result);
        return result;
    }

    CtrlCall checkCall(CtrlTree callTree) {
        int childCount = callTree.getChildCount();
        assert callTree.getType() == CtrlChecker.CALL && childCount >= 1;
        CtrlCall result = null;
        testArgs: {
            String name = callTree.getChild(0).getText();
            // get the arguments
            List<CtrlPar> args = null;
            if (childCount == 2) {
                args = new ArrayList<CtrlPar>();
                CtrlTree argsTree = callTree.getChild(1);
                for (int i = 0; i < argsTree.getChildCount(); i++) {
                    CtrlPar arg = argsTree.getChild(i).getCtrlPar();
                    // if any of the arguments is null, an error was detected
                    // and reported earlier; we silently fail
                    if (arg == null) {
                        break testArgs;
                    }
                    args.add(arg);
                }
            }
            if (checkCall(callTree, name, args)) {
                // create the (rule or function) call
                CtrlCall.Kind kind = this.namespace.getKind(name);
                this.namespace.useName(name);
                if (kind == CtrlCall.Kind.RULE) {
                    result = new CtrlCall(this.namespace.getRule(name), args);
                } else {
                    // it's a function call
                    result = new CtrlCall(kind, name, args);
                    if (this.currentName != null) {
                        addDependency(this.currentName, name);
                    }
                }
                callTree.setCtrlCall(result);
            }
        }
        return result;
    }

    void checkAny(CtrlTree anyTree) {
        if (this.currentKind == CtrlCall.Kind.RECIPE) {
            emitErrorMessage(anyTree, "'any' may not be used within a recipe");
        }
        Set<String> anyNames =
            new HashSet<String>(this.namespace.getTopNames());
        anyNames.removeAll(this.namespace.getUsedNames());
        checkGroupCall(anyTree, anyNames);
    }

    void checkOther(CtrlTree otherTree) {
        if (this.currentKind == CtrlCall.Kind.RECIPE) {
            emitErrorMessage(otherTree,
                "'other' may not be used within a recipe");
        }
        Set<String> unusedRules =
            new HashSet<String>(this.namespace.getTopNames());
        unusedRules.removeAll(this.namespace.getUsedNames());
        checkGroupCall(otherTree, unusedRules);
    }

    private void checkGroupCall(CtrlTree callTree, Set<String> rules) {
        for (String ruleName : rules) {
            checkCall(callTree, ruleName, null);
        }
    }

    void checkEOF(CommonTree EOFToken) {
        if (this.packageName.isEmpty()
            && !this.namespace.getParentName().isEmpty()) {
            emitErrorMessage(EOFToken, "Missing package declaration",
                this.namespace.getFullName());
        }
    }

    /** 
     * Tests if a call with a given argument list is compatible with
     * the declared signature.
     */
    private boolean checkCall(CtrlTree callTree, String name, List<CtrlPar> args) {
        CtrlCall.Kind kind = this.namespace.getKind(name);
        List<CtrlPar.Var> sig =
            kind == null ? null : this.namespace.getSig(name);
        boolean result = sig != null;
        if (!result) {
            emitErrorMessage(callTree, "Unknown action '%s'", name);
        } else if (args == null) {
            result = kind == CtrlCall.Kind.RULE || kind == Kind.RECIPE;
            for (int i = 0; result && i < sig.size(); i++) {
                result = sig.get(i).compatibleWith(new CtrlPar.Wild());
            }
            if (!result) {
                String message = "%s %s%s not applicable without arguments";
                String ruleSig = toTypeString(sig);
                emitErrorMessage(callTree, message, kind.getName(true), name,
                    ruleSig);
            }
        } else {
            result = args.size() == sig.size();
            for (int i = 0; result && i < args.size(); i++) {
                result = sig.get(i).compatibleWith(args.get(i));
            }
            if (!result) {
                String message = "%s %s%s not applicable for arguments %s";
                String callSig = toTypeString(args);
                String ruleSig = toTypeString(sig);
                emitErrorMessage(callTree, message, kind.getName(true), name,
                    ruleSig, callSig);
            }
        }
        return result;
    }

    String toTypeString(List<? extends CtrlPar> sig) {
        StringBuilder result = new StringBuilder();
        result.append('(');
        for (CtrlPar par : sig) {
            if (result.length() > 1) {
                result.append(',');
            }
            if (par.isOutOnly()) {
                result.append(CtrlPar.OUT_PREFIX);
                result.append(' ');
            }
            result.append(par.getType());
        }
        result.append(')');
        return result.toString();
    }

    private void emitErrorMessage(Tree marker, String message, Object... args) {
        if (marker == null) {
            this.errors.add(message, args);
        } else {
            int line = marker.getLine();
            int column = marker.getCharPositionInLine();
            message = String.format(message, args);
            message = String.format("line %d:%d %s", line, column, message);
            addError(message, line, column);
        }
    }

    void addError(String message, int line, int column) {
        this.errors.add(message, line, column);
    }

    /**
     * Returns the (possibly empty) list of errors found during the last call of
     * the program.
     */
    FormatErrorSet getErrors() {
        return this.errors;
    }

    private void addDependency(String from, String to) {
        Set<String> dependencies = this.dependencyMap.get(from);
        if (dependencies == null) {
            this.dependencyMap.put(from, dependencies = new HashSet<String>());
        }
        dependencies.add(to);
    }

    /** 
     * Tests if a given control automaton is suitable as body of a 
     * recipe.
     */
    boolean checkRecipeBody(CtrlTree actionTree, String name, CtrlAut aut) {
        boolean result = true;
        for (CtrlState state : aut.nodeSet()) {
            if (state.isTransient()) {
                emitErrorMessage(actionTree,
                    "Recipe '%s' contains a nested recipe call to '%s'", name,
                    state.getRecipe());
            }
        }
        for (CtrlTransition omegaTrans : aut.getOmegas()) {
            if (omegaTrans.source() == aut.getStart()) {
                emitErrorMessage(actionTree, "Recipe '%s' has empty behaviour",
                    name);
                result = false;
                break;
            }
        }
        if (aut.getOmegas().isEmpty()) {
            emitErrorMessage(actionTree, "Recipe '%s' does not terminate", name);
            result = false;
        } else if (!aut.isEndDeterministic()) {
            emitErrorMessage(actionTree,
                "Recipe '%s' does not terminate deterministically", name);
            result = false;
        }
        return result;
    }

    /** Namespace to enter the declared functions. */
    private final Namespace namespace;
    /** The algebra family to be used for constant arguments. */
    private final AlgebraFamily algebraFamily;
    /** Flag indicating that errors were found during the current run. */
    private final FormatErrorSet errors = new FormatErrorSet();
    /** The symbol table holding the local variable declarations. */
    private final SymbolTable symbolTable = new SymbolTable();
    /** Mapping from function names to other functions being invoked from it. */
    private final Map<String,Set<String>> dependencyMap =
        new HashMap<String,Set<String>>();
    /** Set of currently initialised variables. */
    private Set<String> initVars = new HashSet<String>();
    /**
     * Stack of checkpointed initialised variables. Each stack record consists
     * of two sets of variables. The first element is the set of variables
     * initialised at the start of the branch, the second is the set of
     * variables initialised in each case of the branch.
     */
    private final Stack<Set<String>[]> initVarScopes =
        new Stack<Set<String>[]>();

    /** Name of the module in which all declared names should be placed. */
    private String packageName = "";
    /** Map from names to imported qualified names. */
    private Map<String,String> importMap = new HashMap<String,String>();
}
