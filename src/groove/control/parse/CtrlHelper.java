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
import groove.algebra.syntax.Expression;
import groove.control.Call;
import groove.control.CtrlPar;
import groove.control.CtrlType;
import groove.control.CtrlVar;
import groove.control.Procedure;
import groove.grammar.Action;
import groove.grammar.Callable;
import groove.grammar.QualName;
import groove.grammar.Callable.Kind;
import groove.grammar.model.FormatException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;

/**
 * Helper class for GCL parsing.
 * Acts as an interface between the grammar and the namespace.
 * @author Arend Rensink
 * @version $Revision $
 */
public class CtrlHelper {
    /** Constructs a helper object for a given parser and namespace. */
    public CtrlHelper(Namespace namespace) {
        assert namespace != null;
        this.namespace = namespace;
    }

    /** Sets the package for the declared names. */
    void setPackage(CtrlTree packageTree) {
        this.packageName = packageTree.getText();
        String parentName = this.namespace.getParentName();
        if (!parentName.equals(this.packageName)) {
            emitErrorMessage(packageTree,
                "Package declaration '%s' does not equal program location '%s'", this.packageName,
                parentName);
        }
    }

    /**
     * Creates a new tree with a CtrlParser#ID} token at the root, and
     * an empty text.
     */
    CtrlTree emptyPackage() {
        CtrlTree result = new CtrlTree(CtrlParser.PACKAGE);
        result.addChild(toQualName(Collections.<Token>emptyList()));
        result.addChild(new CtrlTree(CtrlParser.SEMI));
        return result;
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
        this.initVarScopes.push(new Set[] {new HashSet<CtrlVar>(this.initVars), null});
    }

    /**
     * Switches to the next option in the top level branch of the program.
     */
    void nextBranch() {
        Set<CtrlVar>[] topInitVarScope = this.initVarScopes.peek();
        if (topInitVarScope[1] == null) {
            topInitVarScope[1] = new HashSet<CtrlVar>(this.initVars);
        } else {
            topInitVarScope[1].retainAll(this.initVars);
        }
        this.initVars = new HashSet<CtrlVar>(topInitVarScope[0]);
    }

    /**
     * Ends the top level branch of the program. Sets the initialised variables
     * to those initialised in every option.
     */
    void endBranch() {
        Set<CtrlVar>[] topInitVarScope = this.initVarScopes.pop();
        if (topInitVarScope[1] == null) {
            // this was the only branch
        } else {
            // at least one branch was ended before; take the intersection
            // with the current (final) branch
            this.initVars.retainAll(topInitVarScope[1]);
        }
    }

    /** Creates a new tree with a {@link CtrlParser#ID} token at the root,
     * of which the text is the concatenation of a given list of tokens.
     * The last token in the list is added as child to the result, to retain traceability.
     */
    CommonTree toQualName(List<? extends Token> children) {
        CommonToken token = new CommonToken(CtrlParser.ID, flatten(children));
        Token lastChild;
        // set the line/column info to get useful error output
        if (children.isEmpty()) {
            lastChild = token;
        } else {
            CommonToken child = (CommonToken) children.get(0);
            token.setLine(child.getLine());
            token.setTokenIndex(child.getTokenIndex());
            lastChild = children.get(children.size() - 1);
        }
        CtrlTree result = new CtrlTree(token);
        result.addChild(new CtrlTree(lastChild));
        return result;
    }

    /**
     * Tests if the rule name is qualified;
     * if not, first tries to look it up in the import map, and if that fails,
     * prefixes it with the package name.
     */
    CommonTree qualify(CommonTree ruleNameToken) {
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
            result.addChild(ruleNameToken.getChild(0));
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

    /** Sets the control name (from the name space) of this given program tree. */
    void declareProgram(CtrlTree programTree) {
        programTree.setControlName(this.namespace.getControlName());
    }

    /**
     * Attempts to add a function or recipe declaration with a given name.
     * Checks for overlap with the previously declared names.
     * @return {@code true} if no rule, function or recipe with the name of this one was
     * already declared; {@code false} otherwise
     */
    boolean declareCtrlUnit(CtrlTree unitTree) {
        boolean result = false;
        assert (unitTree.getType() == CtrlParser.FUNCTION || unitTree.getType() == CtrlParser.RECIPE)
        && unitTree.getChildCount() <= 4;
        String fullName = qualify(unitTree.getChild(0).getText());
        Callable unit = this.namespace.getCallable(fullName);
        if (unit != null) {
            emitErrorMessage(unitTree, "Duplicate name: %s %s already defined",
                unit.getKind().getName(true), fullName);
        } else {
            int priority =
                    unitTree.getChildCount() == 3 ? 0
                            : Integer.parseInt(unitTree.getChild(2).getText());
            List<CtrlPar.Var> parList = getPars(fullName, unitTree.getChild(1));
            String controlName = this.namespace.getControlName();
            Kind kind = toProcKind(unitTree);
            this.namespace.addProcedure(Procedure.newInstance(fullName, kind, priority, parList,
                controlName, unitTree.getLine(), this.namespace.getGrammarProperties()));
            result = true;
        }
        return result;
    }

    /**
     * Converts a tree token type to a procedure kind.
     */
    private Kind toProcKind(CtrlTree tree) {
        return tree.getType() == CtrlParser.FUNCTION ? Kind.FUNCTION : Kind.RECIPE;
    }

    /**
     * Extracts the parameter declarations.
     */
    private List<CtrlPar.Var> getPars(String procName, CtrlTree parListTree) {
        assert parListTree.getType() == CtrlChecker.PARS;
        List<CtrlPar.Var> result = new ArrayList<CtrlPar.Var>();
        for (int i = 0; i < parListTree.getChildCount(); i++) {
            CtrlTree parTree = parListTree.getChild(i);
            boolean out = parTree.getChildCount() == 3;
            CtrlTree typeTree = parTree.getChild(out ? 1 : 0);
            CtrlType type = typeTree.getCtrlType();
            String name = parTree.getChild(out ? 2 : 1).getText();
            result.add(CtrlPar.var(procName, name, type, !out));
        }
        return result;
    }

    /** Starts a procedure declaration. */
    void startBody(CtrlTree unitTree) {
        setContext(unitTree);
        this.initVars.clear();
        openScope();
    }

    /** Ends a procedure declaration. */
    void endBody(CtrlTree bodyTree) {
        for (String outPar : this.symbolTable.getOutPars()) {
            if (!this.initVars.contains(new CtrlVar(this.procName, outPar,
                this.symbolTable.getType(outPar)))) {
                emitErrorMessage(bodyTree, "Output parameter %s may fail to be initialised", outPar);
            }
        }
        closeScope();
        resetContext();
    }

    /** Sets the context being processed to a given procedure. */
    void setContext(CtrlTree unitTree) {
        assert this.procName == null;
        String procName = qualify(unitTree.getChild(0).getText());
        this.procName = procName;
        this.procKind = toProcKind(unitTree);
    }

    /** Resets the context being processed. */
    void resetContext() {
        assert this.procName != null;
        this.procName = null;
        this.procKind = null;
    }

    /** The procedure name currently processed. */
    private String procName;
    /** The kind of {@link #procName}. */
    private Kind procKind;

    /**
     * Registers a call dependency.
     */
    void registerCall(CtrlTree callTree) {
        String from = this.procName;
        String to = callTree.getText();
        this.namespace.addCall(from, to);
    }

    /** Adds a formal parameter to the symbol table. */
    boolean declarePar(CtrlTree nameTree, CtrlTree typeTree, CtrlTree out) {
        boolean result = true;
        String name = nameTree.getText();
        CtrlType type = typeTree.getCtrlType();
        if (!this.symbolTable.declareSymbol(name, type, out != null)) {
            emitErrorMessage(nameTree, "Duplicate local variable name %s", name);
            result = false;
        } else if (out == null) {
            this.initVars.add(new CtrlVar(this.procName, name, type));
        }
        return result;
    }

    /** Adds a variable to the symbol table. */
    boolean declareVar(CtrlTree nameTree, CtrlTree typeTree) {
        boolean result = true;
        String name = nameTree.getText();
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
            emitErrorMessage(importTree, "Package %s should be %s", name, actualName);
        }
    }

    /** Tests whether an imported name actually exists. */
    void checkImport(CtrlTree importTree) {
        String name = importTree.getText();
        if (!this.namespace.hasCallable(name)) {
            emitErrorMessage(importTree, "Imported name '%s' does not exist", name);
        }
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
        String name = nameTree.getText();
        CtrlType type = this.symbolTable.getType(name);
        if (type == null) {
            emitErrorMessage(nameTree, "Local variable %s not declared", name);
        } else {
            result = new CtrlVar(this.procName, name, type);
            nameTree.setCtrlVar(result);
            if (checkInit && !this.initVars.contains(result)) {
                emitErrorMessage(nameTree, "Variable %s may not have been initialised", name);
            } else {
                this.initVars.add(result);
            }
        }
        return result;
    }

    CtrlPar checkVarArg(CtrlTree argTree) {
        CtrlPar result = null;
        int childCount = argTree.getChildCount();
        assert argTree.getType() == CtrlChecker.ARG && childCount > 0 && childCount <= 2;
        boolean isOutArg = childCount == 2;
        CtrlVar var = checkVar(argTree.getChild(childCount - 1), !isOutArg);
        if (var != null) {
            result = new CtrlPar.Var(var, !isOutArg);
            argTree.setCtrlPar(result);
        }
        return result;
    }

    CtrlPar checkDontCareArg(CtrlTree argTree) {
        assert argTree.getType() == CtrlChecker.ARG && argTree.getChildCount() == 1;
        CtrlPar result = CtrlPar.wild();
        argTree.setCtrlPar(result);
        return result;
    }

    CtrlPar checkConstArg(CtrlTree argTree) {
        assert argTree.getType() == CtrlChecker.ARG && argTree.getChildCount() == 1;
        try {
            Expression constant = Expression.parse(argTree.getChild(0).getText());
            AlgebraFamily family = this.namespace.getGrammarProperties().getAlgebraFamily();
            CtrlPar result =
                    new CtrlPar.Const(family.getAlgebra(constant.getSignature()),
                        family.toValue(constant));
            argTree.setCtrlPar(result);
            return result;
        } catch (FormatException e) {
            // this cannot occur, as the constant string has just been approved
            // by the control parser
            assert false : String.format("%s is not a parsable constant",
                argTree.getChild(0).getText());
        return null;
        }
    }

    void checkCall(CtrlTree callTree) {
        int childCount = callTree.getChildCount();
        assert callTree.getType() == CtrlChecker.CALL && childCount >= 1;
        String unitName = callTree.getChild(0).getText();
        Call result = null;
        testArgs: {
            // get the arguments
            List<CtrlPar> args = null;
            if (childCount == 2) {
                args = new ArrayList<CtrlPar>();
                CtrlTree argsTree = callTree.getChild(1);
                // stop at the closing RPAR
                for (int i = 0; i < argsTree.getChildCount() - 1; i++) {
                    CtrlPar arg = argsTree.getChild(i).getCtrlPar();
                    // if any of the arguments is null, an error was detected
                    // and reported earlier; we silently fail
                    if (arg == null) {
                        break testArgs;
                    }
                    args.add(arg);
                }
            }
            Callable unit = this.namespace.getCallable(unitName);
            if (unit == null) {
                emitErrorMessage(callTree, "Unknown action '%s'", unitName);
            } else if (unit instanceof Action && ((Action) unit).getPriority() > 0) {
                String message = "Explicit call of prioritised %s %s not allowed";
                emitErrorMessage(callTree, message, unit.getKind().getName(false), unitName);
            } else if (checkCall(callTree, unit, args)) {
                // create the call
                result = args == null ? new Call(unit) : new Call(unit, args);
                callTree.setCall(result);
            }
        }
    }

    void checkAny(CtrlTree anyTree) {
        if (this.procKind == Kind.RECIPE) {
            emitErrorMessage(anyTree, "'any' may not be used within a recipe");
        }
        checkGroupCall(anyTree, this.namespace.getActions());
    }

    void checkOther(CtrlTree otherTree) {
        if (this.procKind == Kind.RECIPE) {
            emitErrorMessage(otherTree, "'other' may not be used within a recipe");
        }
        Set<Action> otherActions = new HashSet<Action>();
        Set<String> usedNames = this.namespace.getUsedNames();
        for (Action action : this.namespace.getActions()) {
            if (!usedNames.contains(action.getFullName())) {
                otherActions.add(action);
            }
        }
        otherActions.removeAll(this.namespace.getUsedNames());
        checkGroupCall(otherTree, otherActions);
    }

    private void checkGroupCall(CtrlTree callTree, Set<Action> actions) {
        for (Action action : actions) {
            checkCall(callTree, action, null);
        }
    }

    /**
     * Tests if a call with a given argument list is compatible with
     * the declared signature.
     */
    private boolean checkCall(CtrlTree callTree, Callable unit, List<CtrlPar> args) {
        assert unit != null;
        boolean result;
        String name = unit.getFullName();
        List<CtrlPar.Var> sig = unit.getSignature();
        Kind unitKind = unit.getKind();
        if (args == null) {
            result = true;
            for (int i = 0; result && i < sig.size(); i++) {
                result = sig.get(i).compatibleWith(CtrlPar.wild());
            }
            if (!result) {
                String message = "%s %s%s not applicable without arguments";
                String ruleSig = toTypeString(sig);
                emitErrorMessage(callTree, message, unitKind.getName(true), name, ruleSig);
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
                emitErrorMessage(callTree, message, unitKind.getName(true), name, ruleSig, callSig);
            }
        }
        return result;
    }

    void checkEOF(CtrlTree EOFToken) {
        if (this.packageName.isEmpty() && !this.namespace.getParentName().isEmpty()) {
            emitErrorMessage(EOFToken, "Missing package declaration",
                this.namespace.getControlName());
        }
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

    /** Clears the name space errors. */
    void clearErrors() {
        // we're starting a new control expression forget the old errors
        this.namespace.getErrors().clear();
    }

    /**Adds an error to the name space, if possible prefixed with the line and
     * column of a given control tree. */
    void emitErrorMessage(CtrlTree marker, String message, Object... args) {
        if (marker == null) {
            this.namespace.addError(message, args);
        } else {
            this.namespace.addError(marker.createError(message, args));
        }
    }

    /** Adds an error to the name space. */
    void addError(String message, int line, int column) {
        this.namespace.addError(message, line, column);
    }

    Namespace getNamespace() {
        return this.namespace;
    }

    /** Namespace to enter the declared functions. */
    private final Namespace namespace;
    /** The symbol table holding the local variable declarations. */
    private final SymbolTable symbolTable = new SymbolTable();
    /** Set of currently initialised variables. */
    private Set<CtrlVar> initVars = new HashSet<CtrlVar>();
    /**
     * Stack of checkpointed initialised variables. Each stack record consists
     * of two sets of variables. The first element is the set of variables
     * initialised at the start of the branch, the second is the set of
     * variables initialised in each case of the branch.
     */
    private final Stack<Set<CtrlVar>[]> initVarScopes = new Stack<Set<CtrlVar>[]>();

    /** Name of the module in which all declared names should be placed. */
    private String packageName = "";
    /** Map from names to imported qualified names. */
    private Map<String,String> importMap = new HashMap<String,String>();
}
