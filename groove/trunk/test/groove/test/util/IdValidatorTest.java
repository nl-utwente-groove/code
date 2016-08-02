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
package groove.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import groove.util.parse.IdValidator;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
@SuppressWarnings("javadoc")
public class IdValidatorTest {
    @Test
    public void testPathetic() {
        repair(null, "_NULL_");
        repair("", "_EMP_");
    }

    @Test
    public void testReplace() {
        repair("&", "_AMP_");
        repair("&#", "_AMP__HASH_");
        repair("&-*1^", "_AMP_-_STAR_1_HAT_");
    }

    @Test
    public void testAlpha() {
        // ids without errors
        correct("a");
        correct("ab");
        correct("a-b");
        correct("_---b");
        // ids with errors
        repair("0", "_0");
        repair("_", "_0");
        repair("_", "_0");
    }

    @Test
    public void testSeparators() {
        // ids without errors
        correct("a/b");
        correct("a-b/_0/_--a-1");
        // ids with errors
        repair("a/-/c", "a/_-0/c");
        repair("a/-1-/c", "a/_-1-_/c");
        repair("a/&/c", "a/_AMP_/c");
        repair("a/_/1", "a/_0/_1");
        repair("/", "_EMP_/_EMP_");
        repair("//", "_EMP_/_EMP_/_EMP_");
    }

    private void correct(String name) {
        repair(name, name);
    }

    private void repair(String original, String expected) {
        String actual = VAL.repair(original);
        if (PRINT && VAL.hasErrors()) {
            System.out.println("Errors in " + original + ":");
            System.out.println("  " + VAL.getErrors());
        }
        assertEquals(expected, actual);
        if (expected.equals(original) == VAL.hasErrors()) {
            fail();
        }
    }

    private static final IdValidator VAL = new IdValidator() {
        @Override
        public boolean isSeparator(char c) {
            return c == '/';
        }

        @Override
        public boolean isIdentifierPart(char c) {
            return super.isIdentifierPart(c) || c == '-';
        }
    };

    private static final boolean PRINT = false;
}
