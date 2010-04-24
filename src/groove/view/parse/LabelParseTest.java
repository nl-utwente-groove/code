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

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
public class LabelParseTest {
    /**
     * Called after closing a window. Reduces the open window count and shuts
     * down if no more windows are open.
     */
    public static void closeWindow() {
        openWindows--;
        if (openWindows == 0) {
            System.exit(0);
        }
    }

    /**
     * Runs the test, displaying a label parse tree
     * @param args args[0] should be the label to be parsed
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            boolean isGraph = Boolean.parseBoolean(args[0]);
            for (int i = 1; i < args.length; i++) {
                test(args[i], isGraph);
            }
        } else {
            test("forall=x:label", false);
            test("pp{'", false);
            test("pp{'", true);
        }
    }

    private static void test(String label, boolean isGraph) {
        try {
            LabelLexer lexer = new LabelLexer(new ANTLRStringStream(label));
            System.out.println(new CommonTokenStream(lexer));
            lexer = new LabelLexer(new ANTLRStringStream(label));
            LabelParser parser = new LabelParser(new CommonTokenStream(lexer));
            parser.setIsGraph(isGraph);
            LabelParser.label_return labelReturn = parser.label();
            ASTFrame graphFrame =
                new ASTFrame("parser label result",
                    (org.antlr.runtime.tree.CommonTree) labelReturn.getTree());
            graphFrame.setSize(500, 1000);
            graphFrame.setVisible(true);
            //
            //            List<String> errors = parser.getErrors();
            //            if (errors.size() != 0) {
            //                errors.add(0, "Encountered parse errors in control program");
            //                throw new FormatException(errors);
            //            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int openWindows = 0;
}
