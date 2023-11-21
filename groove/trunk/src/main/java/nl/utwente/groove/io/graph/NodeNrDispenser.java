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
package nl.utwente.groove.io.graph;

/**
 * @author Arend Rensink
 * @version $Revision$
 */
public abstract class NodeNrDispenser {
    /** Computes a node number on the basis of a string ID. */
    abstract public int compute(String id);

    /** Returns a node dispenser, depending on the stored user preference. */
    static public NodeNrDispenser instance() {
        if (STRING) {
            return new StringBased();
        } else {
            return new NextBased();
        }
    }

    static private final boolean STRING = true;

    /** Dispenser that extracts node numbers from the input string.
     * If the string contains no number, behaves as NextBased.
     */
    static private class StringBased extends NextBased {
        @Override
        public int compute(String id) {
            // detect a suffix that represents a number
            boolean digitFound = false;
            int nodeNr = 0;
            int unit = 1;
            int charIx;
            for (charIx = id.length() - 1; charIx >= 0 && Character.isDigit(id.charAt(charIx));
                 charIx--) {
                nodeNr += unit * (id.charAt(charIx) - '0');
                unit *= 10;
                digitFound = true;
            }
            return digitFound
                ? nodeNr
                : super.compute(id);
        }
    }

    /** Dispenser that gives out consecutive numbers, starting at 0. */
    static private class NextBased extends NodeNrDispenser {
        @Override
        public int compute(String id) {
            int result = this.current;
            this.current++;
            return result;
        }

        private int current = 0;
    }
}
