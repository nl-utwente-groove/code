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
package groove.util.collect;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Tree map from strings to values
 * where the keys are compared modulo case distinctions.
 * @author Arend Rensink
 * @version $Revision $
 */
public class UncasedStringMap<V> extends TreeMap<String,V> {
    /**
     * Creates an empty map.
     */
    public UncasedStringMap() {
        super(COMPARATOR);
    }

    /** String comparator that abstracts away from case. */
    public static final UncasedComparator COMPARATOR = new UncasedComparator();

    private static class UncasedComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            int result = 0;
            int min = Math.min(o1.length(), o2.length());
            for (int i = 0; result == 0 && i < min; i++) {
                result = Character.toLowerCase(o1.charAt(i)) - Character.toLowerCase(o2.charAt(i));
            }
            if (result == 0) {
                result = o1.length() - o2.length();
            }
            return result;
        }
    }
}
