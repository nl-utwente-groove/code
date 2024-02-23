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
package nl.utwente.groove.grammar.host;

import java.util.Comparator;

/**
 * Comparator of host nodes.
 * Value nodes are compared based on their class name, then string representation;
 * default host nodes are compared based on their number.
 * All default host nodes are smaller than all value nodes.
 * @author Arend Rensink
 * @version $Revision$
 */
public class HostNodeComparator implements Comparator<HostNode> {
    private HostNodeComparator() {
        // empty;
    }

    @Override
    public int compare(HostNode o1, HostNode o2) {
        if (o1 instanceof DefaultHostNode d1) {
            if (o2 instanceof DefaultHostNode d2) {
                return d1.getNumber() - d2.getNumber();
            } else {
                return -1;
            }
        } else {
            if (o2 instanceof DefaultHostNode) {
                return 1;
            } else {
                return o1.toString().compareTo(o2.toString());
            }
        }
    }

    /** Returns the singleton instance of this class. */
    static public HostNodeComparator instance() {
        return INSTANCE;
    }

    /** The singleton instance of this class. */
    static private HostNodeComparator INSTANCE = new HostNodeComparator();
}
