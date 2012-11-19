/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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
package groove.test.abstraction.pattern;

import groove.abstraction.pattern.PatternAbsParam;

import org.junit.After;
import org.junit.Test;

/**
 * @author Eduardo Zambon
 */
@SuppressWarnings("all")
public class TestPatternShapeGenerator {

    private static final int VERBOSITY = 0;

    private String[] getArgs(String grammar, String startGraph, String typeGraph) {
        return new String[] {"-v", VERBOSITY + "", grammar, startGraph,
            typeGraph};
    }

    private String[] getArgsWithThreeValue(String grammar, String startGraph,
            String typeGraph) {
        return new String[] {"-v", VERBOSITY + "", "-t", grammar, startGraph,
            typeGraph};
    }

    @After
    public void restoreMultiplicitySettings() {
        PatternAbsParam.getInstance().setUseThreeValues(false);
    }

    @Test
    public void test0() {
        // EDUARDO: Restore tests.
    }

}
