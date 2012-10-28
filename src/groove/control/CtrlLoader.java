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
 * $Id: ControlView.java,v 1.10 2008-03-18 12:17:29 fladder Exp $
 */
package groove.control;

import groove.algebra.AlgebraFamily;
import groove.control.parse.CtrlBuilder;
import groove.control.parse.CtrlChecker;
import groove.control.parse.CtrlLexer;
import groove.control.parse.CtrlParser;
import groove.control.parse.CtrlTree;
import groove.control.parse.Namespace;
import groove.io.ExtensionFilter;
import groove.io.FileType;
import groove.trans.Action;
import groove.trans.GraphGrammar;
import groove.trans.QualName;
import groove.trans.Recipe;
import groove.trans.Rule;
import groove.util.Groove;
import groove.view.FormatErrorSet;
import groove.view.FormatException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenRewriteStream;

/**
 * Wrapper for the ANTLR control parser and builder.
 */
public class CtrlLoader {
    /** 
     * Initialises the loader to a given graph grammar.
     * @param algebraFamily name of the algebra family to compute constant data values
     * @param rules set of rules that can be invoked by the grammar
     */
    public void init(AlgebraFamily algebraFamily, Collection<Rule> rules) {
        this.family =
            algebraFamily == null ? AlgebraFamily.DEFAULT : algebraFamily;
        this.namespace = new Namespace();
        for (Rule rule : rules) {
            this.namespace.addRule(rule);
        }
        this.treeMap.clear();
        this.errors = null;
    }

    /**
     * Parses a given, named control program on the basis of a set of rules.
     * The parse result is stored internally; a later call to {@link #buildAutomaton(String)}
     * will collect all parse trees and build a control automaton.
     * @param name the qualified name of the control program to be parsed
     * @param program the control program
     */
    public void parse(String name, String program) throws FormatException {
        assert this.errors == null;
        ANTLRStringStream charStream = new ANTLRStringStream(program);
        CtrlTree tree = parse(name, charStream);
        Object oldRecord = this.treeMap.put(name, tree);
        assert oldRecord == null;
    }

    /**
     * Returns the syntax tree for a given program. 
     * @param name the qualified name of the control program to be parsed
     */
    private CtrlTree parse(String name, ANTLRStringStream inputStream)
        throws FormatException {
        try {
            this.namespace.setFullName(name);
            CtrlTree tree =
                this.parser.run(inputStream, this.namespace, this.family);
            if (DEBUG) {
                System.out.printf("Parse tree: %s%n", tree.toStringTree());
            }
            this.parser.getErrors().throwException();
            tree = this.checker.run(tree, this.namespace, this.family);
            this.checker.getErrors().throwException();
            tree.setInputStream(inputStream);
            return tree;
        } catch (RecognitionException re) {
            throw new FormatException(re.getMessage(), re.line,
                re.charPositionInLine);
        }
    }

    /**
     * Builds the control automaton for a given named control program.
     * Should only be called after the {@link #parse} methods.
     */
    public CtrlAut buildAutomaton(String name) throws FormatException {
        this.namespace.setFullName(name);
        CtrlTree tree = this.treeMap.get(name);
        try {
            CtrlAut result = this.builder.run(tree, this.namespace);
            this.builder.getErrors().throwException();
            return result == null ? null : result.clone(name);
        } catch (RecognitionException re) {
            throw new FormatException(re.getMessage(), re.line,
                re.charPositionInLine);
        }
    }

    /** 
     * Builds a default control automaton out of the actions 
     * parsed in previous calls of the {@link #parse} methods.
     */
    public CtrlAut buildDefaultAutomaton() throws FormatException {
        return CtrlFactory.instance().buildDefault(getActions(),
            this.family.supportsSymbolic());
    }

    /** 
     * Returns the set of all top-level actions collected in the course of
     * processing all control files since the last {@link #init}.
     */
    public Collection<Action> getActions() {
        return this.namespace.getActions();
    }

    /** 
     * Returns the set of all recipes collected in the course of
     * processing all control files since the last {@link #init}.
     */
    public Collection<Recipe> getRecipes() {
        return this.namespace.getRecipes();
    }

    /** 
     * Returns a renamed version of an existing control program.
     * TODO extend this to deal correctly with qualified names (SF Feature Request #3581300) 
     */
    public String rename(String name, String oldCallName, String newCallName) {
        CtrlTree tree = this.treeMap.get(name);
        CtrlLexer lexer = new CtrlLexer(null);
        ANTLRStringStream charStream = tree.getInputStream();
        charStream.reset();
        lexer.setCharStream(charStream);
        TokenRewriteStream rewriter = new TokenRewriteStream(lexer);
        rewriter.fill();
        for (Token t : tree.getCallTokens(oldCallName)) {
            rewriter.replace(t, newCallName);
        }
        return rewriter.toString();
    }

    /** Namespace of this loader. */
    private Namespace namespace;
    /** Algebra family for this control loader. */
    private AlgebraFamily family;
    /** Mapping from program names to corresponding syntax trees. */
    private final Map<String,CtrlTree> treeMap = new TreeMap<String,CtrlTree>();
    /** The possibly empty set of errors in the control automaton,
     * if an attempt to build the automaton has been made. 
     */
    private FormatErrorSet errors;
    /** The parser object. */
    private final CtrlParser parser = new CtrlParser(null);
    /** The parser object. */
    private final CtrlChecker checker = new CtrlChecker(null);
    /** The parser object. */
    private final CtrlBuilder builder = new CtrlBuilder(null);

    /** Call with [grammarfile] [controlfile]* */
    public static void main(String[] args) {
        try {
            String grammarName = args[0];
            GraphGrammar grammar = Groove.loadGrammar(grammarName).toGrammar();
            for (int i = 1; i < args.length; i++) {
                String programName = controlFilter.stripExtension(args[1]);
                System.out.printf("Control automaton for %s:%n%s", programName,
                    run(grammar, programName, new File(grammarName)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    private static final ExtensionFilter controlFilter =
        FileType.CONTROL_FILTER;
    private static final boolean DEBUG = false;

    /** Parses a single control program on the basis of a given grammar. */
    public static CtrlAut run(GraphGrammar grammar, String programName,
            String program) throws FormatException {
        instance.init(grammar.getProperties().getAlgebraFamily(),
            grammar.getAllRules());
        instance.parse(programName, program);
        return instance.buildAutomaton(programName).normalise();
    }

    /** Parses a single control program on the basis of a given grammar. */
    public static CtrlAut run(GraphGrammar grammar, String programName,
            File base) throws FormatException, IOException {
        instance.init(grammar.getProperties().getAlgebraFamily(),
            grammar.getAllRules());
        QualName qualName = new QualName(programName);
        File control = base;
        for (String part : qualName.tokens()) {
            control = new File(control, part);
        }
        String inputFileName = controlFilter.addExtension(control.getPath());
        instance.parse(programName, new ANTLRFileStream(inputFileName));
        return instance.buildAutomaton(programName);
    }

    private static final CtrlLoader instance = new CtrlLoader();
}
