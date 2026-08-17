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
package nl.utwente.groove.test.control;

import static nl.utwente.groove.util.io.FileType.CONTROL;

import java.io.File;

import nl.utwente.groove.control.CtrlLoader;
import nl.utwente.groove.grammar.Grammar;
import nl.utwente.groove.io.Groove;

/**
 * Hand-run debug harness for {@link CtrlLoader}.
 * @author Arend Rensink
 * @version $Revision$
 */
public class CtrlLoaderTool {
    private CtrlLoaderTool() {
        // empty by design
    }

    /** Call with [grammarfile] [controlfile]* */
    public static void main(String[] args) {
        try {
            String grammarName = args[0];
            Grammar grammar = Groove.loadGrammar(grammarName).toGrammar();
            for (int i = 1; i < args.length; i++) {
                String programName = CONTROL.stripExtension(args[1]);
                System.out
                    .printf("Control automaton for %s:%n%s", programName,
                            CtrlLoader.run(grammar, programName, new File(grammarName)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
