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

import java.util.function.Supplier;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Class implementing a lazy create pattern.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
public abstract class LazyFactory<T> implements Supplier<T> {
    /**
     * Empty constructor.
     */
    protected LazyFactory() {
        // nothing happens here
    }

    @Override
    public T get() {
        if (!isSet()) {
            this.value = create();
            this.set = true;
        }
        return this.value;
    }

    /**
     * Sets the value by hand, rather than to have it computed.
     * This is only allowed if the value has not been computed or set before.
     * @throws IllegalStateException if the value was already set
     */
    public void set(T value) throws IllegalStateException {
        if (isSet()) {
            throw Exceptions
                .illegalState("Value already set at %s, can't set it to %s", this.value, value);
        }
        this.value = value;
        this.set = true;
    }

    /** Checks if the value has already been computed. */
    public boolean isSet() {
        return this.set;
    }

    /** Resets the computer value, if any. */
    public void reset() {
        this.set = false;
        this.value = null;
    }

    /** Lazily creates the wrapped value, upon the first invocation of {@link #get()}. */
    abstract protected T create();

    /** Field holding the value once it has been computed. */
    @Nullable
    private T value;

    /** Flag indicating whether the value has been computed. */
    private boolean set;

    /** Creates an instance of this factory from a given supplier. */
    static public <T> LazyFactory<T> instance(Supplier<T> create) {
        return new LazyFactory<>() {
            @Override
            protected T create() {
                return create.get();
            }
        };
    }

    /** Equivalent of {@link #instance(Supplier)} to enable understandable static imports. */
    static public <T> LazyFactory<T> lazyFactory(Supplier<T> create) {
        return instance(create);
    }
}
