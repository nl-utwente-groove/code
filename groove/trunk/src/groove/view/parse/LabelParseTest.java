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
     * Parses a given label as graph and rule label and displays the parse trees
     * @param label the label to be parsed
     */
    public LabelParseTest(String label) {
        try {
            LabelLexer lexer = new LabelLexer(new ANTLRStringStream(label));
            LabelParser parser = new LabelParser(new CommonTokenStream(lexer));
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
        new LabelParseTest(args[0]);
    }

    private static int openWindows = 0;
}
