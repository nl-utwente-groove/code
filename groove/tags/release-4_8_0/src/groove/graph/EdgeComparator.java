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
package groove.graph;

import java.util.Comparator;

/**
 * Compares nodes by (successively) their source node numbers, labels, and target node numbers.
 * @author Arend Rensink
 * @version $Revision $
 */
public class EdgeComparator implements Comparator<Edge> {
    private EdgeComparator() {
        // empty
    }

    @Override
    public int compare(Edge o1, Edge o2) {
        int result = o1.source().getNumber() - o2.source().getNumber();
        if (result != 0) {
            return result;
        }
        result = o1.label().compareTo(o2.label());
        if (result != 0) {
            return result;
        }
        result = o1.target().getNumber() - o2.target().getNumber();
        return result;
    }

    /** Returns the singleton instance of this class. */
    public static EdgeComparator instance() {
        return instance;
    }

    private final static EdgeComparator instance = new EdgeComparator();
}
