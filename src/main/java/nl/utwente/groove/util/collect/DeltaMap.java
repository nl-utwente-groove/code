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
package nl.utwente.groove.util.collect;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.util.parse.FormatException;
import nl.utwente.groove.util.parse.Parser;

/**
 * Map from keys to #{@link Delta}s.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public class DeltaMap<T> {
    /** Constructor for an empty map. */
    public DeltaMap() {
        this.map = new TreeMap<>();
        this.inverseMap = new EnumMap<>(Delta.class);
        Arrays.stream(Delta.values()).forEach(d -> this.inverseMap.put(d, new HashSet<>()));
    }

    /** Constructor for cloning an existing map. */
    public DeltaMap(DeltaMap<T> orig) {
        this();
        orig.entrySet().forEach(e -> set(e.getKey(), e.getValue()));
    }

    private final SortedMap<T,Delta> map;
    private final Map<Delta,Set<T>> inverseMap;

    /** Sets the delta for a given key, and optionally returns the old value. */
    @SuppressWarnings("null")
    public @Nullable Delta set(T key, Delta delta) {
        var result = this.map.put(key, delta);
        if (result != null) {
            this.inverseMap.get(result).remove(key);
        }
        this.inverseMap.get(delta).add(key);
        return result;
    }

    /** Removes a key from the map, and optionally returns the old value. */
    @SuppressWarnings("null")
    public @Nullable Delta remove(T key) {
        var result = this.map.remove(key);
        if (result != null) {
            this.inverseMap.get(result).remove(key);
        }
        return result;
    }

    /** Retrieves the delta for a given key. */
    public @Nullable Delta get(T key) {
        return this.map.get(key);
    }

    /** Returns the set of keys with a given delta. */
    public Set<T> getKeys(Delta delta) {
        return this.inverseMap.get(delta);
    }

    /** Returns the entry set of the delta map. */
    public Set<Map.Entry<T,Delta>> entrySet() {
        return this.map.entrySet();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.map);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DeltaMap<?> other = (DeltaMap<?>) obj;
        return Objects.equals(this.map, other.map);
    }

    @Override
    public String toString() {
        return "DeltaMap [map=" + this.map + "]";
    }

    /** Returns a parser for space-separated lists of deltas. */
    public static final <T extends Comparable<T>> Parser<DeltaMap<T>> parser(Parser<T> innerParser) {
        return new DeltaMapParser<>(innerParser);
    }

    /** The delta for a given item: addition or removal. */
    static public enum Delta {
        /** Addition. */
        ADD("+"),
        /** Removal. */
        REMOVE("-");

        private Delta(String symbol) {
            this.symbol = symbol;
        }

        /** Returns the symbol of this delta. */
        public String symbol() {
            return this.symbol;
        }

        private final String symbol;

        static Optional<Delta> parse(String input) {
            return Arrays.stream(values()).filter(d -> d.symbol().equals(input)).findFirst();
        }
    }

    /** Parser for a {@link DeltaMap}. */
    static public class DeltaMapParser<T> extends Parser.AParser<DeltaMap<T>> {
        DeltaMapParser(Parser<T> innerParser) {
            super(null, new DeltaMap<>());
            this.innerParser = innerParser;
        }

        private final Parser<T> innerParser;

        @Override
        protected @NonNull String createDescription() {
            return "A space-separated list of <i>%srule</i> and <i>%srule</i> pairs"
                .formatted(Delta.ADD.symbol(), Delta.REMOVE.symbol());
        }

        @Override
        public DeltaMap<T> parse(String input) throws FormatException {
            var result = new DeltaMap<T>();
            String[] split = input.trim().split("\\s");
            for (String pair : split) {
                if (pair.length() == 0) {
                    continue;
                }
                var delta = Delta.parse(pair.substring(0, 1));
                if (delta.isEmpty()) {
                    throw new FormatException("Entry %s does not start with '%s' or '%s'", pair,
                        Delta.ADD.symbol(), Delta.REMOVE.symbol());
                }
                T key = this.innerParser.parse(pair.substring(1));
                var old = result.set(key, delta.get());
                if (old != null) {
                    throw new FormatException("Key %s appears twice in list", key);
                }
            }
            return result;
        }

        @Override
        public <V extends DeltaMap<T>> String unparse(@NonNull V value) throws IllegalArgumentException {
            StringBuffer result = new StringBuffer();
            for (var e : value.entrySet()) {
                result.append(e.getValue().symbol());
                result.append(e.getKey());
                result.append(' ');
            }
            return result.toString();
        }
    }
}
