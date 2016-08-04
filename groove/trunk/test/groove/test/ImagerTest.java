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

import groove.io.FileType;
import groove.io.Imager;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the Imager command-line facility
 * @author Arend Rensink
 * @version $Revision $
 */
@SuppressWarnings("javadoc")
public class ImagerTest {
    private final static String TEST_DIR = "junit/samples/ferryman.gps";
    private final static String OUTPUT_DIR = "junit/try";

    private Set<String> graphFiles;

    @Before
    public void setUp() {
        this.graphFiles = new TreeSet<>();
        for (File file : new File(TEST_DIR).listFiles(FileType.GRAPHS.getFilter())) {
            this.graphFiles.add(file.getName());
        }
        new File(OUTPUT_DIR).mkdir();
    }

    @After
    public void tearDown() {
        for (File file : new File(OUTPUT_DIR).listFiles()) {
            file.delete();
        }
        new File(OUTPUT_DIR).delete();
    }

    @Test
    public void testPng() {
        test(FileType.PNG);
    }

    @Test
    public void testPdf() {
        test(FileType.PDF);
    }

    @Test
    public void testTikz() {
        test(FileType.TIKZ);
    }

    private void test(FileType type) {
        new File(OUTPUT_DIR).mkdir();
        try {
            Imager.execute(new String[] {"-f", type.getExtension().substring(1), "-v", "0",
                TEST_DIR, OUTPUT_DIR});
        } catch (Exception exc) {
            exc.printStackTrace();
            Assert.fail(exc.getMessage());
        }
        Set<String> imageFiles = new TreeSet<>();
        for (File file : new File(OUTPUT_DIR).listFiles()) {
            imageFiles.add(type.stripExtension(file.getName()));
        }
        Assert.assertEquals(this.graphFiles, imageFiles);
        new File(OUTPUT_DIR).delete();
    }
}
