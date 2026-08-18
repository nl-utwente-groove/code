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
package nl.utwente.groove.test;

import java.io.File;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import nl.utwente.groove.explore.Transformer;
import nl.utwente.groove.explore.config.LegacySyntaxParser;
import nl.utwente.groove.util.io.FileType;

/**
 * Loads and partially explores all grammars provided with the GROOVE release.
 * The sample and grammar checkouts are located as sibling directories of the
 * working directory by default; when the tests run elsewhere (e.g. from a git
 * worktree), pass the locations via the system properties
 * {@code groove.samples.dir} and {@code groove.grammars.dir}.
 * @author Arend Rensink
 * @version $Revision$
 */
public class GrammarsTest {
    /** Tests the samples. */
    @Test
    public void testSamples() {
        testDir(SAMPLE_DIR);
    }

    /** Tests the grammars. */
    @Test
    public void testGrammars() {
        testDir(GRAMMAR_DIR);
    }

    private void testDir(String dirName) {
        File location = new File(dirName);
        // skip (rather than fail) if the external checkout is not present
        Assume
            .assumeTrue(String.format("Directory %s cannot be found", location.getAbsolutePath()),
                        location.isDirectory());
        File[] files = location.listFiles();
        assert files != null; // location is a directory by the assumption above
        for (File file : files) {
            if (FileType.GRAMMAR.hasExtension(file)) {
                testGrammar(file);
            }
        }
    }

    private void testGrammar(File grammarLocation) {
        try {
            Transformer transformer = new Transformer(grammarLocation);
            transformer
                .setExploreType(LegacySyntaxParser
                    .overlay(transformer.getExploreType(), null, "any", 5));
            transformer.explore();
        } catch (Exception e) {
            e.printStackTrace();
            Assert
                .fail(String
                    .format("Error while testing %s:%n%s", grammarLocation.getName(),
                            e.getMessage()));
        }
    }

    /** Location of the (downloaded) samples module of SourceForge. */
    private static final String SAMPLE_DIR
        = System.getProperty("groove.samples.dir", "../samples");
    /** Location of the (downloaded) grammars module of SourceForge. */
    private static final String GRAMMAR_DIR
        = System.getProperty("groove.grammars.dir", "../grammars");
}
