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
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Class implementing a lazy create pattern.
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
        synchronized (lock) {
            if (!isSet()) {
                this.value = create();
                this.set = true;
            }
            addBuilders();
            return this.value;
        }
    }

    /**
     * Sets the value by hand, rather than to have it computed.
     * This is only allowed if the value has not been computed or set before.
     * @throws IllegalStateException if the value was already set
     */
    public void set(T value) throws IllegalStateException {
        synchronized (lock) {
            if (isSet()) {
                throw Exceptions
                    .illegalState("Value already set at %s, can't set it to %s", this.value, value);
            }
            this.value = value;
            this.set = true;
            addBuilders();
        }
    }

    /** Checks if the value has already been computed. */
    public boolean isSet() {
        return this.set;
    }

    /** Resets the computer value, if any. */
    public void reset() {
        synchronized (lock) {
            this.set = false;
            this.value = null;
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

    /** Field holding the value once it has been computed. */
    @Nullable
    private T value;

    /** Flag indicating whether the value has been computed. */
    private boolean set;

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

    /** Adds a user that depends on the value of this factory. */
    private void addUser(Factory<?> user) {
        if (this.users == EMPTY_SET) {
            this.users = new HashSet<>();
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
        var value = this.value;
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
                    T result = create.get();
                    removeBuilder(this);
                    return result;
                }
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
    /** Set of factories currently running their {@link #create()} method. */
    static private @Nullable Set<Factory<?>> builders;
    /** Shared empty set, to save memory. */
    static private final Set<Factory<?>> EMPTY_SET = Collections.emptySet();
}
