/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
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
package groove.util;

import java.util.Collection;

/** Visitor for a certain type. */
abstract public class Visitor<T,R> {
    /** 
     * Visits a (non-{@code null)} object.
     * @return {@code false} if no more objects need to be visited
     */
    abstract public boolean visit(T object);

    /** Returns the result of the visits. */
    abstract public R getResult();

    /** Constructs a finder for a given property. */
    static public <T> Finder<T> createFinder(Property<T> property) {
        return new Finder<T>(property);
    }

    /** Constructs a collector for a given property. */
    static public <T> Collector<T> createCollector(Collection<T> collection,
            Property<T> property) {
        return new Collector<T>(collection, property);
    }

    /** Constructs a collector. */
    static public <T> Collector<T> createCollector(Collection<T> collection) {
        return new Collector<T>(collection);
    }

    /** 
     * Uses a fixed collector, wrapped around a given collection.
     * WARNING: only use this locally!
     */
    @SuppressWarnings("unchecked")
    static public <T> Collector<T> useCollector(Collection<T> collection) {
        reusableCollector.collection = collection;
        return reusableCollector;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static private Collector reusableCollector = new Collector(null);

    /** Simple visitor that does not have a meaningful result value. */
    static public abstract class Simple<T> extends Visitor<T,Object> {
        @Override
        public Object getResult() {
            return null;
        }
    }

    /** A visitor that stores the first visited object satisfying a given property. */
    static public class Finder<T> extends Visitor<T,T> {
        /**
         * Constructs a finder for a certain property.
         */
        public Finder(Property<T> property) {
            this.property = property;
        }

        @Override
        public boolean visit(T object) {
            boolean result = true;
            if (this.object == null
                && (this.property == null || this.property.isSatisfied(object))) {
                this.object = object;
                result = false;
            }
            return result;
        }

        /** Returns the result found, if any. */
        @Override
        public final T getResult() {
            return this.object;
        }

        /** Reports if an object has been found. */
        public boolean found() {
            return this.object != null;
        }

        /** Resets the result to {@code null}. */
        public void reset() {
            this.object = null;
        }

        /** The property of the object to be found. */
        final private Property<T> property;

        private T object;
    }

    /**
     * A visitor that collects all visited objects, possibly filtered by 
     * a property of the object. 
     */
    static public class Collector<T> extends Visitor<T,Collection<T>> {
        /**
         * Constructs a collector for a given collection and property.
         */
        public Collector(Collection<T> collection, Property<T> property) {
            this.collection = collection;
            this.property = property;
        }

        /**
         * Constructs a collector for a given collection and without filter.
         */
        public Collector(Collection<T> collection) {
            this(collection, null);
        }

        @Override
        public boolean visit(T object) {
            if (this.property == null || this.property.isSatisfied(object)) {
                this.collection.add(object);
            }
            return true;
        }

        /** Returns the wrapped collection of objects. */
        @Override
        final public Collection<T> getResult() {
            return this.collection;
        }

        /** Resets the collector to a different collection. */
        public Collector<T> reset(Collection<T> collection) {
            this.collection = collection;
            return this;
        }

        /** The wrapped collection to which elements are added. */
        private Collection<T> collection;
        /** Filtering property. */
        private final Property<T> property;
    }
}
