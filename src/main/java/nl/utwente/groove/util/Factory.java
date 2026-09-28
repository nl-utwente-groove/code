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
package nl.utwente.groove.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Class implementing a lazy create pattern.
 * <p>
 * Factories track their dependencies: every factory read while another one is being
 * built records the latter as a user, so that {@link #reset()} of the read factory
 * propagates to everything built from its value. Users are held weakly: a factory
 * that is otherwise unreachable is not kept alive by the factories it read, and
 * a reset simply no longer reaches it. The reverse references, from a factory to
 * the ones it read, are strong.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public abstract class Factory<T> implements Supplier<T> {
    /**
     * Empty constructor.
     */
    protected Factory() {
        // nothing happens here
    }

    @Override
    public T get() {
        // lock-free fast path: the value exists and no factory is being built,
        // so there is no dependency to record and nothing the lock protects
        var holder = this.holder;
        if (holder != null && builders == null) {
            return holder.value();
        }
        synchronized (lock) {
            holder = this.holder;
            if (holder == null) {
                holder = new Holder<>(create());
                this.holder = holder;
            }
            addBuilders();
            return holder.value();
        }
    }

    /**
     * Sets the value by hand, rather than to have it computed.
     * This is only allowed if the value has not been computed or set before.
     * @throws IllegalStateException if the value was already set
     */
    public void set(T value) throws IllegalStateException {
        synchronized (lock) {
            var holder = this.holder;
            if (holder != null) {
                throw Exceptions
                    .illegalState("Value already set at %s, can't set it to %s", holder.value(),
                                  value);
            }
            this.holder = new Holder<>(value);
            addBuilders();
        }
    }

    /** Checks if the value has already been computed. */
    public boolean isSet() {
        return this.holder != null;
    }

    /** Resets the computed value, if any. */
    public void reset() {
        synchronized (lock) {
            this.holder = null;
            for (var used : getUsed()) {
                used.removeUser(this);
            }
            getUsed().clear();
            for (var user : new ArrayList<>(getUsers())) {
                user.reset();
                user.removeUsed(this);
            }
            assert getUsers().isEmpty();
            this.used = EMPTY_SET;
            this.users = EMPTY_SET;
        }
    }

    /** Lazily creates the wrapped value, upon the first invocation of {@link #get()}. */
    abstract protected T create();

    /**
     * Holder of the value once it has been computed, {@code null} before that.
     * Volatile so that {@link #get()} can read it without taking the lock:
     * the holder is only published after {@link #create()} has returned,
     * so a reader that sees it sees the completed value.
     */
    @Nullable
    private volatile Holder<T> holder;

    /**
     * Adds the currently registered builders as users of this factory.
     */
    private void addBuilders() {
        var builders = Factory.builders;
        if (builders != null) {
            for (var builder : new ArrayList<>(builders)) {
                addUser(builder);
                builder.addUsed(this);
            }
        }
    }

    /** Returns the set of factories that depend on this one. */
    private Set<Factory<?>> getUsers() {
        return this.users;
    }

    /**
     * Returns the number of factories recorded as depending on this one,
     * not counting those that have been garbage collected.
     * Intended for tests and diagnostics.
     */
    public int getUserCount() {
        synchronized (lock) {
            return this.users.size();
        }
    }

    /** Adds a user that depends on the value of this factory. */
    private void addUser(Factory<?> user) {
        if (this.users == EMPTY_SET) {
            // weakly held, so that a user is not kept alive by what it read
            this.users = Collections.newSetFromMap(new WeakHashMap<>());
        }
        this.users.add(user);
    }

    /** Removes a user that depends on the value this factory. */
    private void removeUser(Factory<?> user) {
        this.users.remove(user);
    }

    /** Set of factories that depend on this one. */
    private Set<Factory<?>> users = EMPTY_SET;

    /** Returns the set of factories that this one depends on. */
    private Set<Factory<?>> getUsed() {
        return this.used;
    }

    /** Adds a dependency to this factory. */
    private void addUsed(Factory<?> used) {
        if (this.used == EMPTY_SET) {
            this.used = new HashSet<>();
        }
        this.used.add(used);
    }

    /** Removes a dependency of this factory. */
    private void removeUsed(Factory<?> used) {
        this.used.remove(used);
    }

    /** Set of factories that this one depends on. */
    private Set<Factory<?>> used = EMPTY_SET;

    @Override
    public String toString() {
        var holder = this.holder;
        var value = holder == null
            ? null
            : holder.value();
        return value instanceof Object[] a
            ? Arrays.toString(a)
            : Objects.toString(value);
    }

    /** Creates an instance of this factory from a given supplier. */
    static public <T> Factory<T> lazy(Supplier<T> create) {
        return new Factory<>() {
            @Override
            protected T create() {
                synchronized (lock) {
                    addBuilder(this);
                    T result = null;
                    try {
                        result = create.get();
                    } finally {
                        removeBuilder(this);
                    }
                    return result;
                }
            }
        };
    }

    /**
     * Creates an instance of this factory from a given supplier, without
     * dependency tracking: factories read while the value is being built do not
     * record this one as a user, so a {@link #reset()} of theirs does not reach it.
     * Intended for factories whose owner's lifetime does not exceed that of its
     * of its dependencies.
     */
    @AIGenerated("Claude Fable 5.1, 2026-09")
    static public <T> Factory<T> untracked(Supplier<T> create) {
        return new Factory<>() {
            @Override
            protected T create() {
                return create.get();
            }
        };
    }

    /** Creates a supplier with a given value. */
    static public <T> Supplier<T> value(T init) {
        return new Supplier<>() {
            @Override
            public T get() {
                return init;
            }
        };
    }

    static private synchronized void addBuilder(Factory<?> builder) {
        var builders = Factory.builders;
        if (builders == null) {
            builders = Factory.builders = new HashSet<>();
        }
        var success = builders.add(builder);
        if (!success) {
            throw Exceptions.illegalState("Circular build of factory");
        }
    }

    static private void removeBuilder(Factory<?> builder) {
        var builders = Factory.builders;
        assert builders != null;
        var success = builders.remove(builder);
        assert success;
        if (builders.isEmpty()) {
            Factory.builders = null;
        }
    }

    /** Lock to avoid concurrent building (which would mess up the builders registration). */
    static private final ReentrantLock lock = new ReentrantLock();
    /**
     * Set of factories currently running their {@link #create()} method.
     * Volatile so that the lock-free path of {@link #get()} sees a build in progress
     * and falls back to the lock, where the dependency on this factory is recorded.
     */
    static private volatile @Nullable Set<Factory<?>> builders;
    /** Shared empty set, to save memory. */
    static private final Set<Factory<?>> EMPTY_SET = Collections.emptySet();

    /** Wrapper distinguishing a computed value (which may be {@code null}) from none. */
    @AIGenerated("Claude Fable 5.1, 2026-09")
    private record Holder<V>(V value) {
        // no members
    }
}
