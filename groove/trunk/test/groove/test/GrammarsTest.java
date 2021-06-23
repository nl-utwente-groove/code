/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.test;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import groove.io.FileType;
import groove.transform.Transformer;
import junit.framework.Assert;

/**
 * Loads and partially explores all grammars provided with the GROOVE release.
 * To run, set {@link #SAMPLE_DIR} and {@link #GRAMMAR_DIR} to the right locations in your project
 * @author Arend Rensink
 * @version $Revision $
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
        if (!location.isDirectory()) {
            try {
                Assert.fail(
                    String.format("Directory %s cannot be found", location.getCanonicalPath()));
            } catch (IOException e) {
                Assert.fail(
                    String.format("Directory %s cannot be parsed", location.getAbsolutePath()));
            }
        }
        for (File file : location.listFiles()) {
            if (FileType.GRAMMAR.hasExtension(file)) {
                testGrammar(file);
            }
        }
    }

    private void testGrammar(File grammarLocation) {
        try {
            Transformer transformer = new Transformer(grammarLocation);
            transformer.setAcceptor("any");
            transformer.setResultCount(5);
            transformer.explore();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(String
                .format("Error while testing %s:%n%s", grammarLocation.getName(), e.getMessage()));
        }
    }

    /** Location of the (downloaded) samples module of SourceForge. */
    private static final String SAMPLE_DIR = "../groove-samples";
    /** Location of the (downloaded) grammars module of SourceForge. */
    private static final String GRAMMAR_DIR = "../groove-grammars";
}
