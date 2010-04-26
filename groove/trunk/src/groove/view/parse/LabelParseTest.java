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
package groove.view.parse;

import groove.control.parse.ASTFrame;
import groove.view.FormatException;

import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class LabelParseTest {
    public LabelParseTest(boolean isGraph) {
        super();
        this.isGraph = isGraph;
    }

    protected void test(String label) {
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

    protected CommonTree parse(Lexer lexer) throws RecognitionException,
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

    protected CommonTree check(CommonTree labelReturn)
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

    private final boolean isGraph;

    /**
     * Runs the test, displaying a label parse tree
     * @param args args[0] should be the label to be parsed
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            boolean isGraph = Boolean.parseBoolean(args[0]);
            LabelParseTest tester = new LabelParseTest(isGraph);
            for (int i = 1; i < args.length; i++) {
                tester.test(args[i]);
            }
        } else {
            LabelParseTest tester = new LabelParseTest(true);
            tester.test("int:0");
            tester.test("real:1.");
            tester.test("real:.2");
            tester.test("string:\"te\\\"xt\"");
            tester.test("bool:true");
            tester.test("pp{'");
            tester.test(":\\:pp{'");
            tester = new LabelParseTest(false);
            tester.test("forall=x:new=y:label");
            tester.test("prod:");
            tester.test("arg:5");
            tester.test("par=$2:");
            tester.test("par:");
            tester.test("!{!label}");
            tester.test("del:?[a,b,c]");
            tester.test("not:!((a.b)|-?x[^b])+.=");
            tester.test("not:{!((a.b)|-?x[^b])+.=}");
        }
    }

    private static final boolean PARSE_DEBUG = false;
    private static final boolean CHECK_DEBUG = false;

    private static final int VERSION = 0;
}
