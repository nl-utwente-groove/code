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

import org.eclipse.jdt.annotation.Nullable;

/**
 * Visitor for objects of a certain type.
 * @param <T> the type of the objects to be visited
 * @param <R> the type of the result of the visitor
 */
abstract public class Visitor<T,R> {
    /** Constructs a visitor with a {@code null} result object. */
    protected Visitor() {
        // empty
    }

    /** Constructs a visitor with a given (initial) result object. */
    protected Visitor(R result) {
        this.result = result;
    }

    /**
     * Callback method from {@link #visit(Object)}
     * which does the actual processing.
     * This is the method that has to be overridden in an implementation.
     * @param object the object to be processed
     * @return if {@code true}, visiting should continue with the next
     * object; if {@code false}, the visitor is finished
     */
    abstract protected boolean process(T object);

    /**
     * Visits a (non-{@code null)} object.
     * The return value is guaranteed to equal that of
     * {@link #isContinue()} and indicates if the traversal should continue.
     * The implementation calls {@link #process(Object)} for the
     * actual processing of the object; the return value is stored
     * and can be tested afterwards using {@link #isContinue()}.
     * @param object the visited object
     * @return {@code false} if no more objects need to be visited
     * @see #isContinue()
     */
    final public boolean visit(T object) {
        assert!isDisposed() && isContinue();
        if (!process(object)) {
            finish();
        }
        return isContinue();
    }

    /** Returns the result of the visits. */
    final public @Nullable R getResult() {
        return this.result;
    }

    /** Tests if the result object has been set. */
    final protected boolean hasResult() {
        return this.result != null;
    }

    /** Sets the visitor result to a given value. */
    final protected void setResult(R result) {
        this.result = result;
    }

    /**
     * Invalidates the visitor.
     * This signals that the object is available for reuse.
     * Also sets the result object to {@code null}.
     */
    public void dispose() {
        this.disposed = true;
        this.result = null;
    }

    /** Indicates whether the visitor has been disposed.
     * @return {@code true} if the visitor has been disposed.
     * @see #dispose()
     */
    protected final boolean isDisposed() {
        return this.disposed;
    }

    /** Resets the disposed flag to {@code false}
     * and the continuation state to {@code true}. */
    protected final void resurrect() {
        this.disposed = false;
        this.cont = true;
    }

    /**
     * Indicates if the visitor is in the continue state.
     * This is initially set to {@code true} and subsequently
     * reflects the last return value of {@link #visit(Object)}.
     */
    public final boolean isContinue() {
        return this.cont;
    }

    /**
     * Sets the continuation state to {@code false}.
     * @see #isContinue()
     */
    private void finish() {
        this.cont = false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + getResult() + ']';
    }

    /** The result object. */
    private @Nullable R result;
    /** Flag indicating that the visitor has been disposed. */
    private boolean disposed;
    /**
     * Flag storing the continuation state of the visitor.
     */
    private boolean cont = true;

    /** Constructs a finder for a given property. */
    @SuppressWarnings("unchecked")
    static public <T> Finder<T> newFinder(Property<T> property) {
        return prototypeFinder.newInstance(property);
    }

    /** Constructs a collector for a given property and collection. */
    @SuppressWarnings("unchecked")
    static public <T,C extends Collection<T>> Collector<T,C> newCollector(C collection,
        Property<T> property) {
        if (property == null) {
            return prototypeCollector.newInstance(collection, property);
        } else {
            return new Collector<>(collection, property);
        }
    }

    /** Constructs a collector. */
    @SuppressWarnings("unchecked")
    static public <T,C extends Collection<T>> Collector<T,C> newCollector(C collection) {
        return prototypeCollector.newInstance(collection);
    }

    /** Constructs a prototype collector. */
    @SuppressWarnings("unchecked")
    static public <T,C extends Collection<T>> Collector<T,C> newCollector() {
        Collector<T,C> result = prototypeCollector.newInstance(null);
        result.dispose();
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"}) private static final Collector prototypeCollector =
        new Collector(null);

    @SuppressWarnings({"rawtypes", "unchecked"}) private static final Finder prototypeFinder =
        new Finder(null);

    /** A visitor that stores the first visited object satisfying a given property. */
    static public class Finder<T> extends Visitor<T,T> {
        /**
         * Constructs a finder for a certain property.
         * @param property the property of the returned object; may be {@code null},
         * in which case the first object is returned.
         */
        public Finder(Property<T> property) {
            this.property = property;
        }

        @Override
        protected boolean process(T object) {
            if (!hasResult() && (this.property == null || this.property.isSatisfied(object))) {
                setResult(object);
            }
            return !hasResult();
        }

        /** Reports if an object has been found. */
        public boolean found() {
            return hasResult();
        }

        /**
         * Returns a new finder for a given property.
         * Reuses this object if it has been disposed.
         */
        public Finder<T> newInstance(Property<T> property) {
            if (isDisposed()) {
                this.property = property;
                resurrect();
                return this;
            } else {
                return new Finder<>(property);
            }
        }

        /** Returns a new finder for the same property. */
        public Finder<T> newInstance() {
            return newInstance(null);
        }

        /** The property of the object to be found. */
        private Property<T> property;
    }

    /**
     * A visitor that collects all visited objects, possibly filtered by
     * a property of the object.
     */
    static public class Collector<T,C extends Collection<T>> extends Visitor<T,C> {
        /**
         * Constructs a collector for a given collection and property.
         */
        public Collector(C collection, Property<T> property) {
            super(collection);
            if (collection == null) {
                dispose();
            }
            this.property = property;
        }

        /**
         * Constructs a collector for a given collection and without filter.
         */
        public Collector(C collection) {
            this(collection, null);
        }

        @Override
        protected boolean process(T object) {
            Collection<T> result = getResult();
            assert result != null; // by assumption this has to be true for collectors
            if (this.property == null || this.property.isSatisfied(object)) {
                result.add(object);
            }
            return true;
        }

        /**
         * Returns a collector for the given collection and
         * the property of the current collector.
         * Reuses this object if it has been disposed.
         */
        public Collector<T,C> newInstance(C collection) {
            if (isDisposed()) {
                setResult(collection);
                resurrect();
                return this;
            } else {
                return createInstance(collection, this.property);
            }
        }

        /**
         * Returns a collector for the given collection and property.
         * Reuses this object if it has been disposed.
         */
        public Collector<T,C> newInstance(C collection, Property<T> property) {
            if (isDisposed()) {
                setResult(collection);
                this.property = property;
                resurrect();
                return this;
            } else {
                return createInstance(collection, property);
            }
        }

        /**
         * Callback factory method for creating a new collector,
         * in case this is not yet disposed.
         */
        protected Collector<T,C> createInstance(C collection, Property<T> property) {
            return new Collector<>(collection, property);
        }

        /** Filtering property. */
        private Property<T> property;
    }
}
