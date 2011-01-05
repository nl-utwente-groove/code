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
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.SPORule;
import groove.util.Groove;
import groove.view.FormatException;

import java.io.IOException;
import java.util.List;

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
     */
    public CtrlAut runString(String program, GraphGrammar grammar)
        throws FormatException {
        CtrlAut result = runStream(new ANTLRStringStream(program), grammar);
        result.setProgram(program);
        return result;
    }

    /**
     * Returns the control automaton for a given control program (given as filename) and grammar. 
     */
    public CtrlAut runFile(String inputFileName, GraphGrammar grammar)
        throws FormatException, IOException {
        return runStream(new ANTLRFileStream(inputFileName), grammar);
    }

    /**
     * Returns the control automaton for a given control program (given as input stream) and grammar. 
     */
    public CtrlAut runStream(CharStream inputStream, GraphGrammar grammar)
        throws FormatException {
        try {
            Namespace namespace = new Namespace();
            for (Rule rule : grammar.getRules()) {
                namespace.addRule((SPORule) rule);
            }
            AlgebraFamily family;
            if (grammar.getProperties() == null) {
                family = AlgebraFamily.getInstance();
            } else {
                family =
                    AlgebraFamily.getInstance(grammar.getProperties().getAlgebraFamily());
            }
            MyTree tree = this.parser.run(inputStream, namespace, family);
            if (DEBUG) {
                System.out.printf("Parse tree: %s%n", tree.toStringTree());
            }
            List<String> errors = this.parser.getErrors();
            if (!errors.isEmpty()) {
                throw new FormatException(errors);
            }
            tree = this.checker.run(tree, namespace, family);
            errors = this.checker.getErrors();
            if (!errors.isEmpty()) {
                throw new FormatException(errors);
            }
            return this.builder.run(tree, namespace).normalise();
        } catch (RecognitionException re) {
            throw new FormatException(re.getMessage());
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
                System.out.printf("Control automaton for %s:%n%s", filename,
                    instance.runFile(filename, grammar));
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

}
