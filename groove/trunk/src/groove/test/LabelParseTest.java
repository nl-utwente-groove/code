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
package groove.test;

import static org.junit.Assert.assertTrue;
import groove.control.parse.ASTFrame;
import groove.view.FormatException;
import groove.view.parse.Label0Checker;
import groove.view.parse.Label0Lexer;
import groove.view.parse.Label0Parser;

import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.junit.Test;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class LabelParseTest {
    /**
     * Tests some graph labels
     */
    @Test
    public void testGraph() {
        this.isGraph = true;
        testCorrect("int:0");
        testCorrect("real:1.");
        testCorrect("real:.2");
        testCorrect("string:\"te\\\"xt\"");
        testCorrect("bool:true");
        testCorrect("pp{'");
        testCorrect(":\\:pp{'");
    }

    /**
     * Tests some rule labels
     */
    @Test
    public void testRule() {
        this.isGraph = false;
        testWrong("forall=x:new=y:label");
        testCorrect("prod:");
        testCorrect("arg:5");
        testCorrect("par=$2:");
        testCorrect("par:");
        testWrong("!{!label}");
        testCorrect("del:?[a,b,c]");
        testCorrect("not:!((a.b)|-?x[^b])+.=");
        testCorrect("not:{!((a.b)|-?x[^b])+.=}");
    }

    private void testCorrect(String label) {
        try {
            test(label);
        } catch (Exception e) {
            if (PARSE_DEBUG) {
                e.printStackTrace();
            }
            assertTrue(false);
        }
    }

    private void testWrong(String label) {
        try {
            test(label);
            assertTrue(false);
        } catch (Exception e) {
            // this is the expected outcome
        }
    }

    private void test(String label) throws Exception {
        Lexer lexer = new Label0Lexer(new ANTLRStringStream(label));
        CommonTree parsedLabel = parse(lexer);
        check(parsedLabel);
    }

    private void testAndReport(String label) {
        try {
            Lexer lexer = new Label0Lexer(new ANTLRStringStream(label));
            System.out.println(new CommonTokenStream(lexer));
            lexer = new Label0Lexer(new ANTLRStringStream(label));
            CommonTree parsedLabel = parse(lexer);
            System.out.println("Parsed label: " + parsedLabel.toStringTree());
            if (PARSE_DEBUG) {
                ASTFrame graphFrame =
                    new ASTFrame("parser label result", parsedLabel);
                graphFrame.setSize(500, 1000);
                graphFrame.setVisible(true);
            }
            CommonTree checkedLabel = check(parsedLabel);
            if (CHECK_DEBUG) {
                ASTFrame frame = new ASTFrame("checker result", checkedLabel);
                frame.setSize(500, 1000);
                frame.setVisible(true);
            }
            System.out.println("Checked label: " + checkedLabel.toStringTree());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CommonTree parse(Lexer lexer) throws RecognitionException,
        FormatException {
        Label0Parser parser = new Label0Parser(new CommonTokenStream(lexer));
        parser.setIsGraph(this.isGraph);
        CommonTree labelReturn = (CommonTree) parser.label().getTree();
        List<String> errors = parser.getErrors();
        if (errors.size() != 0) {
            errors.add(0, "Encountered parse errors in label");
            throw new FormatException(errors);
        }
        return labelReturn;
    }

    private CommonTree check(CommonTree labelReturn)
        throws RecognitionException, FormatException {
        // fetch the resulting tree
        CommonTreeNodeStream nodes = new CommonTreeNodeStream(labelReturn);
        // checker will store and remove functions
        Label0Checker checker = new Label0Checker(nodes);
        Label0Checker.label_return c_r = checker.label();
        List<String> errors = checker.getErrors();
        if (errors.size() != 0) {
            errors.add(0, "Encountered checker errors in label");
            throw new FormatException(errors);
        }
        return (CommonTree) c_r.getTree();
    }

    private boolean isGraph;

    /**
     * Runs the test, displaying a label parse tree
     * @param args args[0] should be the label to be parsed
     */
    public static void main(String[] args) {
        boolean isGraph = Boolean.parseBoolean(args[0]);
        LabelParseTest tester = new LabelParseTest();
        tester.isGraph = isGraph;
        for (int i = 1; i < args.length; i++) {
            tester.testAndReport(args[i]);
        }
    }

    private static final boolean PARSE_DEBUG = false;
    private static final boolean CHECK_DEBUG = false;
}
