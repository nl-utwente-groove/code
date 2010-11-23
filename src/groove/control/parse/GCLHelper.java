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

import groove.control.CtrlCall;
import groove.control.CtrlPar;
import groove.control.CtrlType;
import groove.control.CtrlVar;
import groove.util.Groove;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

/**
 * Helper class for GCL parsing.
 * Acts as an interface between the grammar and the namespace.
 * @author Arend Rensink
 * @version $Revision $
 */
public class GCLHelper {
    /** Constructs a helper object for a given parser and namespace. */
    public GCLHelper(BaseRecognizer recogniser, NamespaceNew namespace) {
        this.recogniser = recogniser;
        this.namespace = namespace;
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
    void branchStart() {
        this.initVarScopes.push(new Set[] {new HashSet<String>(this.initVars),
            null});
    }

    /**
     * Switches to the next option in the top level branch of the program.
     */
    void branchNext() {
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
    void branchEnd() {
        Set<String>[] topInitVarScope = this.initVarScopes.pop();
        if (topInitVarScope[1] != null) {
            this.initVars.retainAll(topInitVarScope[1]);
        }
    }

    /** Creates a new tree with a {@link GCLNewParser#ID} token at the root,
     * of which the text is the concatenation of the children of the given tree.
     */
    CommonTree toRuleName(List<?> children) {
        String result;
        StringBuilder builder = new StringBuilder();
        for (Object token : children) {
            if (builder.length() > 0) {
                builder.append('.');
            }
            builder.append(((CommonToken) token).getText());
        }
        result = builder.toString();
        return new MyTree(new CommonToken(GCLNewParser.ID, result));
    }

    /** 
     * Attempts to add a function declaration with a given name.
     * Checks for overlap with the previously declared functions and rules.
     * @return {@code true} if no rule or function with the name of this one was
     * already declared; {@code false} otherwise
     */
    boolean declareFunction(Tree functionTree) {
        boolean result = false;
        assert functionTree.getType() == GCLNewParser.FUNCTION
            && functionTree.getChildCount() == 2;
        String name = functionTree.getChild(0).getText();
        if (this.namespace.hasRule(name)) {
            emitErrorMessage(functionTree,
                "Duplicate rule name: Rule %s already defined", name);
        } else if (this.namespace.hasFunction(name)) {
            emitErrorMessage(functionTree,
                "Duplicate name: Function %s already defined", name);
        } else {
            this.namespace.addFunction(name, new ArrayList<CtrlPar.Var>());
            result = true;
        }
        return result;
    }

    boolean declareVar(Tree nameTree, MyTree typeTree) {
        boolean result = true;
        String name = nameTree.getText();
        if (!this.symbolTable.declareSymbol(name, typeTree.getCtrlType())) {
            emitErrorMessage(nameTree, "Duplicate local variable name %s", name);
            result = false;
        }
        return result;
    }

    /**
     * Checks the control type generated by a type tree node.
     * Returns the type and stores in the tree node.
     */
    CtrlType checkType(MyTree typeTree) {
        CtrlType result;
        if (typeTree.getType() == GCLNewChecker.NODE) {
            result = CtrlType.createNodeType();
        } else {
            result = CtrlType.createDataType(typeTree.getText());
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
    CtrlVar checkVar(MyTree nameTree, boolean checkInit) {
        CtrlVar result = null;
        String name = nameTree.getText();
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

    CtrlPar checkVarArg(MyTree argTree) {
        CtrlPar result = null;
        int childCount = argTree.getChildCount();
        assert argTree.getType() == GCLNewChecker.ARG && childCount > 0
            && childCount <= 2;
        boolean isOutArg = childCount == 2;
        CtrlVar var = checkVar(argTree.getChild(childCount - 1), !isOutArg);
        if (var != null) {
            result = new CtrlPar.Var(var);
            argTree.setCtrlPar(result);
        }
        return result;
    }

    CtrlPar checkDontCareArg(MyTree argTree) {
        assert argTree.getType() == GCLNewChecker.ARG
            && argTree.getChildCount() == 0;
        CtrlPar result = new CtrlPar.Wild();
        argTree.setCtrlPar(result);
        return result;
    }

    CtrlPar checkConstArg(MyTree argTree) {
        assert argTree.getType() == GCLNewChecker.ARG
            && argTree.getChildCount() == 1;
        CtrlPar result = new CtrlPar.Const(argTree.getChild(0).getText());
        argTree.setCtrlPar(result);
        return result;
    }

    CtrlCall checkCall(MyTree callTree) {
        assert callTree.getType() == GCLChecker.CALL
            && callTree.getChildCount() >= 1;
        CtrlCall result = null;
        // if any of the arguments is null, an error was detected
        // and reported earlier; we silently fail
        testArgs: {
            List<CtrlPar> args = new ArrayList<CtrlPar>();
            for (int i = 1; i < callTree.getChildCount(); i++) {
                CtrlPar arg = callTree.getChild(i).getCtrlPar();
                if (arg == null) {
                    break testArgs;
                }
                args.add(arg);
            }
            String name = callTree.getChild(0).getText();
            List<CtrlPar.Var> sig = this.namespace.getSig(name);
            if (sig == null) {
                emitErrorMessage(callTree, "No function or rule %s defined",
                    name);
            } else if (checkCall(callTree, name, sig, args)) {
                // create the (rule or function) call
                if (this.namespace.hasRule(name)) {
                    result = new CtrlCall(this.namespace.useRule(name), args);
                } else {
                    result = new CtrlCall(name, args);
                }
                callTree.setCtrlCall(result);
            }
        }
        return result;
    }

    /** 
     * Tests if a call with a given argument list is compatible with
     * the declared signature.
     */
    private boolean checkCall(MyTree callTree, String name,
            List<CtrlPar.Var> sig, List<CtrlPar> args) {
        boolean result = args.size() == sig.size();
        for (int i = 0; result && i < args.size(); i++) {
            result = sig.get(i).compatibleWith(args.get(i));
        }
        if (!result) {
            String message = "%s %s%s not applicable for arguments %s";
            String ruleOrFunction =
                this.namespace.hasRule(name) ? "Rule" : "Function";
            String callSig = Groove.toString(args.toArray(), "(", ")", ",");
            String ruleSig = Groove.toString(args.toArray(), "(", ")", ",");
            emitErrorMessage(callTree, message, ruleOrFunction, name, ruleSig,
                callSig);
        }
        return result;
    }

    private void emitErrorMessage(Tree marker, String message, Object... args) {
        message =
            String.format("line %d:%d %s", marker.getLine(),
                marker.getCharPositionInLine(), String.format(message, args));
        this.recogniser.emitErrorMessage(message);
        addError(message);
    }

    void addError(String message) {
        this.errors.add(message);
    }

    /**
     * Returns the (possibly empty) list of errors found during the last call of
     * the program.
     */
    List<String> getErrors() {
        return this.errors;
    }

    private final BaseRecognizer recogniser;
    /** Namespace to enter the declared functions. */
    private final NamespaceNew namespace;
    /** Flag indicating that errors were found during the current run. */
    private final List<String> errors = new ArrayList<String>();
    /** The symbol table holding the local variable declarations. */
    private final NewSymbolTable symbolTable = new NewSymbolTable();
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
}
