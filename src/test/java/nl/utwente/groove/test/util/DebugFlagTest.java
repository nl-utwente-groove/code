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
package nl.utwente.groove.test.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import nl.utwente.groove.util.AIGenerated;
import nl.utwente.groove.util.DebugFlag;

/**
 * Checks that every field annotated with {@link DebugFlag} in the main source
 * tree is a {@code boolean} initialised with the literal {@code false}.
 * <p>
 * The check works on the source text rather than by reflection: reading a
 * static field reflectively initialises its class, which the GUI classes do not
 * survive headless, and compile-time constants are inlined at their use sites
 * anyway. Comments are blanked out before matching, so that mentions of the
 * annotation in documentation do not count as declarations.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
@NonNullByDefault
public class DebugFlagTest {
    /** Root of the main source tree, relative to the project directory. */
    private static final Path SOURCE_ROOT = Path.of("src", "main", "java");
    /** Simple name of the annotation, as it appears in the source. */
    private static final String ANNOTATION = "@" + DebugFlag.class.getSimpleName();
    /**
     * Pattern for a field declaration carrying the annotation.
     * Whitespace between the parts may include line breaks; other annotations
     * may follow the marker. Group 1 is the field type, group 2 the field name
     * and group 3 the initialiser (absent if the field has none).
     */
    private static final Pattern DECLARATION = Pattern
        .compile(ANNOTATION + "\\b(?:\\(\\s*\\))?\\s*" // the marker
            + "(?:@\\w+(?:\\([^)]*\\))?\\s+)*" // other annotations
            + "(?:(?:public|protected|private|static|final|transient|volatile)\\s+)*" // modifiers
            + "([\\w.<>\\[\\]]+)\\s+" // type
            + "(\\w+)\\s*" // name
            + "(?:=\\s*([^;]*?)\\s*)?;"); // optional initialiser
    /** Pattern for block and line comments. */
    private static final Pattern COMMENT
        = Pattern.compile("/\\*.*?\\*/|//[^\\n]*", Pattern.DOTALL);

    /** Every annotated field in the main source tree passes. */
    @Test
    public void testMainSources() throws IOException {
        List<String> violations = new ArrayList<>();
        int count = 0;
        try (Stream<Path> files = Files.walk(SOURCE_ROOT)) {
            for (Path file : (Iterable<Path>) files::iterator) {
                if (!file.toString().endsWith(".java")) {
                    continue;
                }
                String source = Files.readString(file, StandardCharsets.UTF_8);
                if (!source.contains(ANNOTATION)) {
                    continue;
                }
                count += check(file.toString(), source, violations);
            }
        }
        assertTrue(count > 0,
                   "No " + ANNOTATION + " fields found under " + SOURCE_ROOT
                       + "; the scan is broken");
        assertTrue(violations.isEmpty(), () -> String.join(System.lineSeparator(), violations));
    }

    /** The scanner recognises the shapes a declaration can take. */
    @Test
    public void testScannerAccepts() {
        assertEquals(1, check(" private static final boolean DEBUG = false;"));
        assertEquals(1, check(" static final private boolean DEBUG=false ;"));
        assertEquals(1, check(" static boolean DEBUG = false;"));
        assertEquals(1, check(" boolean DEBUG = false;"));
        assertEquals(1, check("() private static final boolean DEBUG = false;"));
        assertEquals(1,
                     check("\n    @SuppressWarnings(\"unused\")\n    private\n    static\n"
                         + "    final boolean\n    DEBUG\n    =\n    false\n    ;"));
    }

    /** The scanner rejects the violations it is meant to find. */
    @Test
    public void testScannerRejects() {
        assertEquals(-1, check(" private static final boolean DEBUG = true;"));
        assertEquals(-1, check(" private static final boolean DEBUG = !false;"));
        assertEquals(-1, check(" private static final boolean DEBUG = Boolean.FALSE;"));
        assertEquals(-1, check(" private static final boolean DEBUG;"));
        assertEquals(-1, check(" private static final Boolean DEBUG = false;"));
        assertEquals(-1, check(" private static final int DEBUG = 0;"));
        assertEquals(-1, check(" private static final boolean DEBUG = false, TRACE = true;"));
        assertEquals(-1, check(" private static final boolean DEBUG = false, TRACE = false;"));
        assertEquals(-1, check(" private static final boolean debug() { return false; }"));
    }

    /** Annotations mentioned in comments are not declarations. */
    @Test
    public void testCommentsIgnored() {
        List<String> violations = new ArrayList<>();
        String source = "/** Carries " + ANNOTATION + " int x = 1; */\n" //
            + "// " + ANNOTATION + " boolean y = true;\n" //
            + "class C { " + ANNOTATION + " boolean z = false; }";
        assertEquals(1, check("C.java", source, violations));
        assertTrue(violations.isEmpty(), () -> violations.toString());
    }

    /** Violations name the file and the line of the annotation. */
    @Test
    public void testViolationMessage() {
        List<String> violations = new ArrayList<>();
        String source = "class C {\n\n    " + ANNOTATION + "\n    boolean z = true;\n}";
        check("C.java", source, violations);
        assertEquals(1, violations.size());
        String violation = violations.get(0);
        assertTrue(violation.startsWith("C.java:3: "), violation);
        assertTrue(violation.contains("'z'"), violation);
        assertTrue(violation.contains("true"), violation);
    }

    /**
     * Scans a source text for annotated field declarations and adds a message
     * for every violation to a list.
     * @return the number of annotated declarations found
     */
    private int check(String fileName, String source, List<String> violations) {
        String code = blankComments(source);
        int count = 0;
        Matcher matcher = DECLARATION.matcher(code);
        int pos = 0;
        while ((pos = code.indexOf(ANNOTATION, pos)) >= 0) {
            count++;
            String location = fileName + ":" + lineOf(code, pos) + ": ";
            if (matcher.find(pos) && matcher.start() == pos) {
                String type = matcher.group(1);
                String name = matcher.group(2);
                String init = matcher.group(3);
                if (!"boolean".equals(type)) {
                    violations
                        .add(location + ANNOTATION + " field '" + name + "' is not a boolean but a "
                            + type);
                } else if (init == null) {
                    violations
                        .add(location + ANNOTATION + " field '" + name
                            + "' has no initialiser; it must be false");
                } else if (!"false".equals(init)) {
                    violations
                        .add(location + ANNOTATION + " field '" + name + "' is set to " + init
                            + "; it must be false");
                }
            } else {
                violations
                    .add(location + ANNOTATION
                        + " is not followed by a recognisable field declaration");
            }
            pos += ANNOTATION.length();
        }
        return count;
    }

    /**
     * Scans a single (uncommented) declaration following the annotation.
     * @return {@code 1} if the declaration passes, {@code -1} if it is a violation
     */
    private int check(String declaration) {
        List<String> violations = new ArrayList<>();
        int count = check("Test.java", ANNOTATION + declaration, violations);
        assertEquals(1, count);
        return violations.isEmpty() ? 1 : -1;
    }

    /** Replaces the content of all comments by spaces, keeping line breaks. */
    private static String blankComments(String source) {
        Matcher matcher = COMMENT.matcher(source);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(result, "");
            String comment = matcher.group();
            assert comment != null; // find() succeeded
            result.append(comment.replaceAll("[^\\n]", " "));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /** Returns the 1-based line number of a character position. */
    private static int lineOf(String text, int pos) {
        int line = 1;
        for (int i = 0; i < pos; i++) {
            if (text.charAt(i) == '\n') {
                line++;
            }
        }
        return line;
    }
}
