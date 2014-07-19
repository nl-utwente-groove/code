/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
package groove.control;

import static groove.io.FileType.CONTROL;
import groove.control.parse.CtrlLexer;
import groove.control.parse.CtrlTree;
import groove.control.parse.Namespace;
import groove.control.template.Program;
import groove.grammar.Callable;
import groove.grammar.Callable.Kind;
import groove.grammar.Grammar;
import groove.grammar.GrammarProperties;
import groove.grammar.QualName;
import groove.grammar.Recipe;
import groove.grammar.Rule;
import groove.util.Groove;
import groove.util.parse.FormatError;
import groove.util.parse.FormatErrorSet;
import groove.util.parse.FormatException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.TokenRewriteStream;

/**
 * Wrapper for the ANTLR control parser and builder.
 */
public class CtrlLoader {
    /**
     * Constructs a control loader for a given set of rules and grammar properties.
     * @param grammarProperties name of the algebra family to compute constant data values
     * @param rules set of rules that can be invoked by the grammar
     * @param checkDependencies flag to determine whether the name space
     * should check for circular dependencies and forward references.
     */
    public CtrlLoader(GrammarProperties grammarProperties, Collection<Rule> rules,
        boolean checkDependencies) {
        this.namespace = new Namespace(grammarProperties);
        for (Rule rule : rules) {
            this.namespace.addRule(rule);
        }
        this.treeMap.clear();
    }

    /**
     * Parses a given, named control program.
     * The parse result is stored internally; a later call to {@link #buildProgram(Collection)}
     * will collect all parse trees and build a control automaton.
     * The tree is not yet checked.
     * @param controlName the qualified name of the control program to be parsed
     * @param program the control program
     */
    public CtrlTree parse(String controlName, String program) throws FormatException {
        if (this.treeMap.containsKey(controlName)) {
            throw new FormatException("Duplicate program name %s", controlName);
        }
        this.namespace.setControlName(controlName);
        CtrlTree tree = CtrlTree.parse(this.namespace, program);
        Object oldRecord = this.treeMap.put(controlName, tree);
        assert oldRecord == null;
        return tree;
    }

    /** Checks all control trees parsed by this loader. */
    public Map<String,CtrlTree> check() throws FormatException {
        Map<String,CtrlTree> result = new HashMap<String,CtrlTree>();
        for (Map.Entry<String,CtrlTree> entry : this.treeMap.entrySet()) {
            entry.setValue(entry.getValue().check());
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /** Returns a control program constructed from a set of previously parsed program names. */
    public Program buildProgram(Collection<String> progNames) throws FormatException {
        FormatErrorSet errors = new FormatErrorSet();
        Program result = new Program();
        for (String name : progNames) {
            try {
                CtrlTree tree = this.treeMap.get(name).check();
                result.add(tree.toProgram());
            } catch (FormatException e) {
                for (FormatError error : e.getErrors()) {
                    errors.add(error, FormatError.control(name));
                }
            }
        }
        errors.throwException();
        if (!result.hasMain()) {
            // try to parse "any" for static semantic checks
            Program main = parse(" main", "#any;").check().toProgram();
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
        Collection<Recipe> result = new ArrayList<Recipe>();
        for (Callable unit : this.namespace.getCallables()) {
            if (unit instanceof Recipe && ((Recipe) unit).isFixed()) {
                result.add((Recipe) unit);
            }
        }
        return result;
    }

    /**
     * Returns renamed versions of the stored control programs.
     * @return a mapping from program names to changed programs
     */
    public Map<String,String> rename(String oldCallName, String newCallName) {
        Map<String,String> result = new HashMap<String,String>();
        for (Map.Entry<String,CtrlTree> entry : this.treeMap.entrySet()) {
            String name = entry.getKey();
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

    /** Returns a version of the control program defining a recipe with a given name,
     * where the declared priority of that recipe has been changed to a given value.
     * TODO finish this (SF Feature Request #172)
     * @param prioMap mapping from the names of the recipes to be changed to
     * the new priority values
     * @return mapping of control program names and new control programs
     */
    public Map<String,String> changePriority(Map<String,Integer> prioMap) {
        Map<String,String> result = new HashMap<String,String>();
        for (Map.Entry<String,Integer> entry : prioMap.entrySet()) {
            String recipeName = entry.getKey();
            int newPriority = entry.getValue();
            String controlName = getNamespace().getControlName(recipeName);
            if (controlName == null) {
                continue;
            }
            CtrlTree tree = this.treeMap.get(controlName);
            assert tree != null : String.format("Parse tree of %s not found", controlName);
            CtrlTree recipeTree = tree.getProcs(Kind.RECIPE).get(recipeName);
            assert recipeTree != null : String.format("Recipe declaration of %s not found",
                recipeName);
            TokenRewriteStream rewriter = getRewriter(tree);
            boolean changed = false;
            if (recipeTree.getChildCount() == 3) {
                // no explicit priority
                if (newPriority != 0) {
                    rewriter.insertAfter(recipeTree.getChild(1).getToken(), "priority "
                        + newPriority);
                    changed = true;
                }
            } else {
                CtrlTree prioTree = recipeTree.getChild(2);
                int oldPriority = Integer.parseInt(prioTree.getText());
                if (oldPriority != newPriority) {
                    rewriter.replace(prioTree.getToken(), "" + newPriority);
                    changed = true;
                }
            }
            if (changed) {
                result.put(controlName, rewriter.toString());
            }
        }
        return result;
    }

    private TokenRewriteStream getRewriter(CtrlTree tree) {
        CtrlLexer lexer = new CtrlLexer(null);
        lexer.setCharStream(new ANTLRStringStream(tree.toInputString()));
        TokenRewriteStream rewriter = new TokenRewriteStream(lexer);
        rewriter.fill();
        return rewriter;
    }

    /** Returns the name space of this loader. */
    public Namespace getNamespace() {
        return this.namespace;
    }

    /** Namespace of this loader. */
    private Namespace namespace;
    /** Mapping from program names to corresponding syntax trees. */
    private final Map<String,CtrlTree> treeMap = new TreeMap<String,CtrlTree>();

    /** Call with [grammarfile] [controlfile]* */
    public static void main(String[] args) {
        try {
            String grammarName = args[0];
            Grammar grammar = Groove.loadGrammar(grammarName).toGrammar();
            for (int i = 1; i < args.length; i++) {
                String programName = CONTROL.stripExtension(args[1]);
                System.out.printf("Control automaton for %s:%n%s", programName,
                    run(grammar, programName, new File(grammarName)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Parses a single control program on the basis of a given grammar. */
    public static Program run(Grammar grammar, String programName, String program)
        throws FormatException {
        CtrlLoader instance = new CtrlLoader(grammar.getProperties(), grammar.getAllRules(), false);
        instance.parse(programName, program);
        Program result = instance.buildProgram(Collections.singleton(programName));
        result.setFixed();
        return result;
    }

    /** Parses a single control program on the basis of a given grammar. */
    public static Program run(Grammar grammar, String programName, File base)
        throws FormatException, IOException {
        CtrlLoader instance = new CtrlLoader(grammar.getProperties(), grammar.getAllRules(), false);
        QualName qualName = new QualName(programName);
        File control = base;
        for (String part : qualName.tokens()) {
            control = new File(control, part);
        }
        File inputFile = CONTROL.addExtension(control);
        Scanner scanner = new Scanner(inputFile).useDelimiter("\\A");
        instance.parse(programName, scanner.next());
        scanner.close();
        Program result = instance.buildProgram(Collections.singleton(programName));
        result.setFixed();
        return result;
    }
}
