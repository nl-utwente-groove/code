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
package nl.utwente.groove.graph;

import java.util.Comparator;

/** Compares nodes by their numbers.
 * @author Arend Rensink
 * @version $Revision$
 */
public class NodeComparator implements Comparator<Node> {
    private NodeComparator() {
        // empty
    }

    @Override
    public int compare(Node o1, Node o2) {
        return o1.getNumber() - o2.getNumber();
    }

    /** Returns the singleton instance of this class. */
    public static NodeComparator instance() {
        return instance;
    }

    private final static NodeComparator instance = new NodeComparator();
}
