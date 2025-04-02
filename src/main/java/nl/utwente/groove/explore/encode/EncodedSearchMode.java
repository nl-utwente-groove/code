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
package nl.utwente.groove.explore.encode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An {@link EncodedSearchMode} gives a choice between Depth-First and Breadth-First search.
 * @author Arend Rensink
 */
public class EncodedSearchMode extends EncodedFixedEnumeratedType<EncodedSearchMode.SearchMode> {

    /**
     * Keyword for Depth-First search
     */
    public static final String DFS_KEY = "dfs";

    /**
    * Keyword for Breadth-First search
    */
    public static final String BFS_KEY = "bfs";

    private static final String DFS_TEXT = "Depth-First Search: newest state first.";
    private static final String BFS_TEXT = "Breadth-First Search: oldest state first.";

    @Override
    public Map<String,String> fixedOptions() {
        Map<String,String> result = new LinkedHashMap<>();
        result.put(DFS_KEY, DFS_TEXT);
        result.put(BFS_KEY, BFS_TEXT);
        return result;
    }

    @Override
    public Map<String,SearchMode> fixedValues() {
        Map<String,SearchMode> result = new LinkedHashMap<>();
        result.put(DFS_KEY, SearchMode.DFS);
        result.put(BFS_KEY, SearchMode.BFS);
        return result;
    }

    /** The search mode value. */
    public enum SearchMode {
        /** Depth-First search */
        DFS,
        /** Breadth-First search */
        BFS
    }
}
