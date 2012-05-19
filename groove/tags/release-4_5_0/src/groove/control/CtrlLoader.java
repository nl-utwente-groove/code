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
import groove.control.parse.CtrlParser;
import groove.control.parse.MyTree;
import groove.control.parse.Namespace;
import groove.graph.GraphInfo;
import groove.trans.Action;
import groove.trans.GraphGrammar;
import groove.trans.Recipe;
import groove.trans.Rule;
import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.util.Pair;
import groove.view.FormatError;
import groove.view.FormatException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.RecognitionException;

/**
 * Wrapper for the new ANTLR control parser and builder.
 */
public class CtrlLoader {
    /**
     * Returns the control automaton for a given control program (given as string) and grammar. 
     * @param properties the system properties under which the automaton is compiled
     * @param rules the set of rules that can be invoked.
     */
    public Result runString(String program, SystemProperties properties,
            Collection<Rule> rules) throws FormatException {
        Result result =
            runStream(new ANTLRStringStream(program), properties, rules);
        return result;
    }

    /**
     * Returns the control automaton for a given control program (given as string) and 
     * set of rules. 
     * @param properties the system properties under which the automaton is compiled
     * @param rules the set of rules that can be invoked.
     */
    public Result runString(String program, SystemProperties properties,
            Set<Rule> rules) throws FormatException {
        Result result =
            runStream(new ANTLRStringStream(program), properties, rules);
        return result;
    }

    /**
     * Returns the control automaton for a given control program (given as filename) and 
     * grammar.  
     * @param properties the system properties under which the automaton is compiled
     * @param rules the set of rules that can be invoked.
     */
    public Result runFile(String inputFileName, SystemProperties properties,
            Collection<Rule> rules) throws FormatException, IOException {
        return runStream(new ANTLRFileStream(inputFileName), properties, rules);
    }

    /**
     * Returns the control automaton for a given control program (given as input stream) and 
     * set of rules. 
     * @param properties the system properties under which the automaton is compiled
     * @param rules the set of rules that can be invoked.
     */
    public Result runStream(CharStream inputStream,
            SystemProperties properties, Collection<Rule> rules)
        throws FormatException {
        try {
            Namespace namespace = new Namespace();
            for (Rule rule : rules) {
                namespace.addRule(rule);
            }
            AlgebraFamily family;
            if (properties == null) {
                family = AlgebraFamily.getInstance();
            } else {
                family =
                    AlgebraFamily.getInstance(properties.getAlgebraFamily());
            }
            MyTree tree = this.parser.run(inputStream, namespace, family);
            if (DEBUG) {
                System.out.printf("Parse tree: %s%n", tree.toStringTree());
            }
            List<FormatError> errors = this.parser.getErrors();
            if (!errors.isEmpty()) {
                throw new FormatException(errors);
            }
            tree = this.checker.run(tree, namespace, family);
            errors = this.checker.getErrors();
            if (!errors.isEmpty()) {
                throw new FormatException(errors);
            }
            CtrlAut aut = this.builder.run(tree, namespace);
            errors = this.builder.getErrors();
            if (!errors.isEmpty()) {
                throw new FormatException(errors);
            }
            List<Recipe> recipes = new ArrayList<Recipe>();
            List<Action> actions = new ArrayList<Action>();
            for (String name : namespace.getTopNames()) {
                switch (namespace.getKind(name)) {
                case RECIPE:
                    Recipe recipe = namespace.getRecipe(name);
                    recipes.add(recipe);
                    actions.add(recipe);
                    break;
                case RULE:
                    actions.add(namespace.getRule(name));
                }
            }
            if (aut == null) {
                aut = CtrlFactory.instance().buildDefault(actions);
            } else {
                aut = aut.normalise();
            }
            if (GraphInfo.hasErrors(aut)) {
                throw new FormatException(GraphInfo.getErrors(aut));
            }
            return new Result(aut, recipes);
        } catch (RecognitionException re) {
            throw new FormatException(re.getMessage(), re.line,
                re.charPositionInLine);
        }
    }

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
                String filename = args[1];
                System.out.printf(
                    "Control automaton for %s:%n%s",
                    filename,
                    instance.runFile(filename, grammar.getProperties(),
                        grammar.getAllRules()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    /** Returns the singleton instance of the control parser. */
    public static CtrlLoader getInstance() {
        return instance;
    }

    private static final CtrlLoader instance = new CtrlLoader();
    private static final boolean DEBUG = false;

    /** Result object of the parser,
     * consisting of a main automaton and a set of recipe automata. 
     */
    public static class Result extends Pair<CtrlAut,List<Recipe>> {
        /** Constructs a result object. */
        Result(CtrlAut one, List<Recipe> two) {
            super(one, two);
        }
    }
}
