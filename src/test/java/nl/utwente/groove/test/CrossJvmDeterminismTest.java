/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2026
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

package nl.utwente.groove.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import nl.utwente.groove.util.AIGenerated;

/**
 * Cross-JVM determinism test (gh #894): runs {@link IdentityHashProbe} in
 * separate JVMs whose identity hash sequences are shifted by different
 * numbers of pre-draws, and asserts that all runs print the identical
 * signature (rule anchors, exploration event stream, final GTS with
 * transition hashes). This covers the class of leak that
 * {@link DeterminismTest} cannot see: identity hashes that are constant
 * within one JVM but differ between JVM runs, such as {@link Class} and
 * {@link Enum} identity hashes (the mechanism behind the 2026-08 rule-anchor
 * instability, gh #893).
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@NonNullByDefault
@Category(SlowTest.class)
public class CrossJvmDeterminismTest {
    /** Asserts that shifted identity-hash sequences leave the probe signature unchanged. */
    @Test
    public void testIdentityHashInsensitivity() throws IOException, InterruptedException {
        String base = runProbe(0);
        // guard against vacuous success on an empty or truncated dump
        assertTrue("probe produced no anchor dump", base.contains("anchor "));
        assertTrue("probe produced no GTS dump", base.contains("-- final GTS --"));
        for (int draws : new int[] {4096, 65536}) {
            assertEquals("probe signature differs after " + draws + " identity-hash pre-draws",
                         base, runProbe(draws));
        }
    }

    /**
     * Runs {@link IdentityHashProbe} in a fresh JVM with a given number of
     * identity-hash pre-draws, inheriting this JVM's classpath and working
     * directory, and returns its standard output.
     */
    private String runProbe(int draws) throws IOException, InterruptedException {
        String javaHome = System.getProperty("java.home");
        assert javaHome != null; // always set by the JVM
        String java = Path.of(javaHome, "bin", "java").toString();
        ProcessBuilder pb
            = new ProcessBuilder(java, IdentityHashProbe.class.getName(), Integer.toString(draws));
        // pass the classpath through the environment rather than -cp, to stay
        // clear of the Windows command-line length limit; under surefire the
        // main classes are on the module path, not the classpath, so append
        // that as well (the probe needs no module encapsulation)
        String classPath = System.getProperty("java.class.path");
        assert classPath != null; // always set by the JVM
        String modulePath = System.getProperty("jdk.module.path");
        if (modulePath != null) {
            classPath = classPath + File.pathSeparator + modulePath;
        }
        pb.environment().put("CLASSPATH", classPath);
        Process process = pb.start();
        // the output is small (well below the pipe buffer size), so the
        // streams can safely be drained one after the other
        String out;
        String err;
        try (var outStream = process.getInputStream(); var errStream = process.getErrorStream()) {
            out = new String(outStream.readAllBytes(), Charset.defaultCharset());
            err = new String(errStream.readAllBytes(), Charset.defaultCharset());
        }
        int exit = process.waitFor();
        assertEquals("probe JVM with " + draws + " pre-draws failed:\n" + err, 0, exit);
        return out;
    }
}
