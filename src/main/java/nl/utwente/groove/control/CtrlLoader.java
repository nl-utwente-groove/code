/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
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
 * $Id$
 */
package nl.utwente.groove.control;

import static nl.utwente.groove.util.io.FileType.CONTROL;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenRewriteStream;

import org.eclipse.jdt.annotation.NonNullByDefault;

import nl.utwente.groove.control.parse.CtrlLexer;
import nl.utwente.groove.control.parse.CtrlTree;
import nl.utwente.groove.control.parse.Namespace;
import nl.utwente.groove.control.template.Fragment;
import nl.utwente.groove.control.template.Program;
import nl.utwente.groove.grammar.Callable;
import nl.utwente.groove.grammar.Callable.Kind;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.grammar.GrammarProperties;
import nl.utwente.groove.grammar.Recipe;
import nl.utwente.groove.grammar.Rule;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;
import nl.utwente.groove.util.parse.FormatError;
import nl.utwente.groove.util.parse.FormatErrorSet;
import nl.utwente.groove.util.parse.FormatException;

/**
 * Wrapper for the ANTLR control parser and builder.
 */
@NonNullByDefault
public class CtrlLoader {
    /**
     * Constructs a control loader for a given set of rules and grammar properties.
     * @param grammarProperties name of the algebra family to compute constant data values
     * @param rules set of rules that can be invoked by the grammar
     */
    public CtrlLoader(GrammarProperties grammarProperties, Collection<Rule> rules) {
        this.namespace = new Namespace(grammarProperties);
        for (Rule rule : rules) {
            this.namespace.addRule(rule);
        }
        this.controlTreeMap = new TreeMap<>();
    }

    /**
     * Parses a given, named control program and returns the corresponding control tree.
     * The parse result is stored internally; a later call to {@link #buildProgram(Collection)}
     * will collect all parse trees and build a control program object.
     * The tree is not yet checked.
     * @param controlName the qualified name of the control program to be parsed
     * @param program the control program
     */
    public CtrlTree addControl(QualName controlName, String program) throws FormatException {
        return addControl(controlName, program, false);
    }

    /**
     * Parses a given, named control program and returns the corresponding control tree.
     * With respect to {@link #addControl(QualName, String)}, has a flag to indicate
     * that the control program is artificially synthesised.
     */
    private CtrlTree addControl(QualName controlName, String program,
                                boolean artificial) throws FormatException {
        if (this.controlTreeMap.containsKey(controlName)) {
            throw new FormatException("Duplicate program name %s", controlName);
        }
        this.namespace.setControlInfo(controlName, artificial);
        CtrlTree tree = CtrlTree.parse(this.namespace, program);
        tree.setArtificial(artificial);
        this.controlTreeMap.put(controlName, tree);
        return tree;
    }

    /** Adds an artificially synthesised main program. */
    private CtrlTree addDefaultMain() throws FormatException {
        return addControl(new QualName(DEFAULT_MAIN_NAME), getDefaultMain(), true);
    }

    /**
     * Parses a control program that is not part of the program being built,
     * and registers its declared procedures as invisible in the name space,
     * to allow more informative error messages for calls and imports of those
     * procedures (see gh #560). Errors in the program itself are ignored here;
     * they are reported when the program is checked in its own right.
     * @param controlName the qualified name of the invisible control program
     * @param program the program text
     * @param reason the reason for the invisibility
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    public void addInvisibleControl(QualName controlName, String program,
                                    Namespace.InvisibleDecl.Reason reason) {
        Namespace scratch = new Namespace(this.namespace.getGrammarProperties());
        scratch.setControlInfo(controlName, false);
        try {
            CtrlTree.parse(scratch, program);
        } catch (FormatException exc) {
            // the declarations collected up to the error are still registered
        }
        for (Callable unit : scratch.getCallables()) {
            this.namespace.addInvisible(unit.getQualName(), unit.getKind(), controlName, reason);
        }
    }

    /**
     * Registers all rule names absent from the name space as invisible,
     * to allow more informative error messages for calls and imports of those
     * rules (see gh #560). An absent rule name is classified as erroneous if it
     * is active, and as disabled otherwise.
     * @param allRuleNames the names of all rules in the grammar
     * @param activeRuleNames the names of the active rules in the grammar
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    public void addInvisibleRules(Collection<QualName> allRuleNames,
                                  Collection<QualName> activeRuleNames) {
        for (QualName ruleName : allRuleNames) {
            if (!this.namespace.hasCallable(ruleName)) {
                var reason = activeRuleNames.contains(ruleName)
                    ? Namespace.InvisibleDecl.Reason.ERRONEOUS
                    : Namespace.InvisibleDecl.Reason.DISABLED;
                this.namespace.addInvisible(ruleName, Kind.RULE, null, reason);
            }
        }
    }

    /** Returns a control program constructed from the collection of previously parsed program names. */
    public Program buildProgram() throws FormatException {
        return buildProgram(this.controlTreeMap.keySet());
    }

    /** Returns a control program constructed from a set of previously parsed program names. */
    public Program buildProgram(Collection<QualName> progNames) throws FormatException {
        FormatErrorSet errors = new FormatErrorSet();
        Program result = new Program();
        for (QualName name : progNames) {
            try {
                CtrlTree tree = this.controlTreeMap.get(name).check();
                result.add(tree.toFragment());
            } catch (FormatException e) {
                for (FormatError error : e.getErrors()) {
                    errors.add(error, FormatError.control(name));
                }
            }
        }
        errors.throwException();
        if (!result.hasMain()) {
            // try to parse "any" for static semantic checks
            Fragment main = addDefaultMain().check().toFragment();
            result.add(main);
        }
        result.setProperties(this.namespace.getProperties());
        result.setFixed();
        return result;
    }

    /**
     * Returns the set of all fixed recipes collected in the course of
     * processing all control files since the construction of this loader.
     */
    public Collection<Recipe> getRecipes() {
        Collection<Recipe> result = new ArrayList<>();
        for (Callable unit : this.namespace.getCallables()) {
            if (unit instanceof Recipe r && r.isFixed()) {
                result.add(r);
            }
        }
        return result;
    }

    /**
     * Returns renamed versions of the stored control programs.
     * @return a mapping from program names to changed programs
     */
    public Map<QualName,String> rename(QualName oldCallName, QualName newCallName) {
        Map<QualName,String> result = new HashMap<>();
        for (Map.Entry<QualName,CtrlTree> entry : this.controlTreeMap.entrySet()) {
            QualName name = entry.getKey();
            CtrlTree tree = entry.getValue();
            TokenRewriteStream rewriter = getRewriter(tree);
            boolean changed = false;
            for (CtrlTree t : tree.getRuleIdTokens(oldCallName)) {
                rewriter.replace(t.getToken(), t.getChild(0).getToken(), newCallName);
                changed = true;
            }
            if (changed) {
                result.put(name, rewriter.toString());
            }
        }
        return result;
    }

    /** Returns versions of the control programs declaring a given set of recipes,
     * where the declared priorities of those recipes have been changed to given values.
     * A priority of 0 is realised by omitting the priority clause.
     * @param prioMap mapping from the names of the recipes to be changed to
     * the new priority values
     * @return mapping of control program names and new control programs
     */
    public Map<QualName,String> changePriority(Map<QualName,Integer> prioMap) {
        // share one rewriter per control program, so that multiple
        // recipe changes within the same program accumulate
        Map<QualName,TokenRewriteStream> rewriterMap = new HashMap<>();
        Set<QualName> changed = new HashSet<>();
        for (Map.Entry<QualName,Integer> entry : prioMap.entrySet()) {
            QualName recipeName = entry.getKey();
            int newPriority = entry.getValue();
            QualName controlName = getNamespace().getDeclaringName(recipeName);
            if (controlName == null) {
                continue;
            }
            CtrlTree tree = this.controlTreeMap.get(controlName);
            assert tree != null : String.format("Parse tree of %s not found", controlName);
            CtrlTree recipeTree = tree.getProcs(Kind.RECIPE).get(recipeName);
            assert recipeTree != null : String
                .format("Recipe declaration of %s not found", recipeName);
            TokenRewriteStream rewriter = rewriterMap
                .computeIfAbsent(controlName, n -> getRewriter(tree));
            if (recipeTree.getChildCount() == 3) {
                // no explicit priority clause
                if (newPriority != 0) {
                    // the body carries the opening curly brace token
                    CtrlTree bodyTree = recipeTree.getChild(2);
                    rewriter.insertBefore(bodyTree.getToken(), "priority " + newPriority + " ");
                    changed.add(controlName);
                }
            } else {
                CtrlTree prioTree = recipeTree.getChild(2);
                int oldPriority = Integer.parseInt(prioTree.getText());
                if (oldPriority != newPriority) {
                    if (newPriority == 0) {
                        deletePriorityClause(rewriter, prioTree);
                    } else {
                        rewriter.replace(prioTree.getToken(), Integer.toString(newPriority));
                    }
                    changed.add(controlName);
                }
            }
        }
        Map<QualName,String> result = new HashMap<>();
        for (QualName controlName : changed) {
            result.put(controlName, rewriterMap.get(controlName).toString());
        }
        return result;
    }

    /** Deletes the priority clause ending in a given priority value token,
     * together with the whitespace separating it from the parameter list.
     */
    @AIGenerated("Claude Fable 5, 2026-08")
    private void deletePriorityClause(TokenRewriteStream rewriter, CtrlTree prioTree) {
        int end = prioTree.getToken().getTokenIndex();
        // scan back over hidden tokens to the PRIORITY keyword
        int start = end - 1;
        while (rewriter.get(start).getChannel() != Token.DEFAULT_CHANNEL) {
            start--;
        }
        assert rewriter.get(start).getType() == CtrlLexer.PRIORITY;
        // also absorb directly preceding whitespace (but not comments)
        Token before = rewriter.get(start - 1);
        if (before.getChannel() != Token.DEFAULT_CHANNEL && before.getText().isBlank()) {
            start--;
        }
        rewriter.delete(start, end);
    }

    private TokenRewriteStream getRewriter(CtrlTree tree) {
        CtrlLexer lexer = new CtrlLexer(null);
        var inputString = tree.toInputString();
        lexer
            .setCharStream(new ANTLRStringStream(inputString == null
                ? ""
                : inputString));
        TokenRewriteStream rewriter = new TokenRewriteStream(lexer);
        rewriter.fill();
        return rewriter;
    }

    /** Returns the name space of this loader. */
    public Namespace getNamespace() {
        return this.namespace;
    }

    /** Namespace of this loader. */
    private final Namespace namespace;
    /** Mapping from program names to corresponding control trees. */
    private final Map<QualName,CtrlTree> controlTreeMap;

    /** Returns the default main program text. */
    private String getDefaultMain() {
        return this.defaultMain;
    }

    /** Sets the default main program text. */
    public void setDefaultMain(String defaultMain) {
        this.defaultMain = defaultMain;
    }

    private String defaultMain = DEFAULT_MAIN;

    /** The default main program name, used if a (combined) program does not declare a main. */
    public static final String DEFAULT_MAIN_NAME = "main";

    /** The default main program text, used if a (combined) program does not declare a main. */
    public static final String DEFAULT_MAIN = "# *.any;";

    /** Parses a single control program on the basis of a given grammar. */
    public static Program run(Grammar grammar, String programName,
                              String program) throws FormatException {
        CtrlLoader instance = new CtrlLoader(grammar.getProperties(), grammar.getAllRules());
        QualName qualName = QualName.parse(programName).testValid();
        instance.addControl(qualName, program);
        Program result = instance.buildProgram(Collections.singleton(qualName));
        result.setFixed();
        return result;
    }

    /** Parses a single control program on the basis of a given grammar. */
    public static Program run(Grammar grammar, String programName,
                              File base) throws FormatException, IOException {
        CtrlLoader instance = new CtrlLoader(grammar.getProperties(), grammar.getAllRules());
        QualName qualName = QualName.parse(programName).testValid();
        File control = base;
        for (String part : qualName.tokens()) {
            control = new File(control, part);
        }
        File inputFile = CONTROL.addExtension(control);
        try (Scanner scanner = new Scanner(inputFile)) {
            scanner.useDelimiter("\\A");
            instance.addControl(qualName, scanner.next());
        }
        Program result = instance.buildProgram(Collections.singleton(qualName));
        result.setFixed();
        return result;
    }
}
