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
 * $Id: AntlrParser.java,v 1.3 2008-01-30 09:33:33 iovka Exp $
 */
package groove.view.parse;

/**
 * Static function to parse the grammar files.
 * @author Tom Staijen
 * @version $Revision $
 */
public class AntlrParser {
    /**
     * Parse the grammar files.
     */
    public static void main(String[] args) {
        try {
            String grammarName = "Label" + VERSION;
            switch (Integer.parseInt(args[0])) {
            case 0:
                System.out.println("Lexer/Parser");
                org.antlr.Tool.main(new String[] {grammarName + ".g"});
                break;
            case 1:
                System.out.println("Checker");
                org.antlr.Tool.main(new String[] {grammarName + "Checker.g"});
                break;
            case 2:
                System.out.println("Builder");
                org.antlr.Tool.main(new String[] {grammarName + "Builder.g"});
                break;
            default:
                printUsageMessage();
            }
        } catch (NumberFormatException exc) {
            printUsageMessage();
        } catch (ArrayIndexOutOfBoundsException exc) {
            printUsageMessage();
        }
    }

    static private void printUsageMessage() {
        System.err.printf("Usage: AntlrParser <option>%n");
        System.err.printf("Option values: 0 for lexer/parser generation%n");
        System.err.printf("               1 for checker generation%n");
        System.err.printf("               2 for builder generation%n");
        System.err.printf("               3 for determinism checker generation%n");
    }

    static private final int VERSION = 0;
}
