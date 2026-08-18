/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package nl.utwente.groove.test.prolog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;

import org.junit.Test;

import nl.utwente.groove.grammar.model.GrammarModel;
import nl.utwente.groove.grammar.model.ResourceKind;
import nl.utwente.groove.io.store.SystemStore;
import nl.utwente.groove.prolog.GrooveEnvironment;
import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.QualName;

/**
 * Tests the service-loaded prolog validator and the prolog-program carrier on
 * the compiled grammar: a broken program flags both the resource and the
 * grammar, while a valid program is carried into the compiled grammar and
 * loads into a prolog environment.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
public class PrologValidatorTest {
    /** Directory of the sample grammars. */
    static private final String INPUT_DIR = "junit/samples";

    /** Tests that a valid program leaves the grammar error-free, is carried
     * by the compiled grammar, and loads into an environment. */
    @Test
    public void testValidProgram() throws Exception {
        String program = "father(abraham, isaac).\n";
        var grammar = newGrammar(program);
        assertFalse(grammar.hasErrors());
        assertFalse(grammar.getPrologModel(QualName.name("test")).hasErrors());
        var compiled = grammar.toGrammar();
        assertEquals(Map.of(QualName.name("test"), program), compiled.getPrologPrograms());
        // the carried program loads into a fresh environment
        GrooveEnvironment.ofGrammar(compiled);
    }

    /** Tests that a syntax error in a program is recorded on the resource
     * model (by the service-loaded validator) and copied to the grammar
     * errors. */
    @Test
    public void testBrokenProgram() throws Exception {
        var grammar = newGrammar("father(abraham, isaac\n");
        assertTrue(grammar.hasErrors());
        assertTrue(grammar.getPrologModel(QualName.name("test")).hasErrors());
    }

    /** Creates a modifiable ferryman grammar model with a single active
     * prolog program named {@code test} holding the given text. */
    static private GrammarModel newGrammar(String program) throws Exception {
        SystemStore original
            = SystemStore.newStore(new File(INPUT_DIR + "/ferryman.gps"), false, true);
        File dir = Files.createTempDirectory("prolog-validator-test").toFile();
        dir.deleteOnExit();
        SystemStore store = original.save(new File(dir, "ferryman.gps"), true);
        store.putTexts(ResourceKind.PROLOG, Map.of(QualName.name("test"), program));
        GrammarModel result = store.toGrammarModel();
        result.setLocalActiveNames(ResourceKind.HOST, QualName.name("start"));
        result.setLocalActiveNames(ResourceKind.PROLOG, QualName.name("test"));
        return result;
    }
}
