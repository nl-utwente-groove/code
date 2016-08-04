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
package groove.explore.config;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Default implementation of {@link Setting}
 * @author Arend Rensink
 */
public class DefaultSetting<K extends Enum<K> & SettingKey,C> implements Setting<K,@Nullable C> {
    /** Constructs a value with a given value kind and {@code null} content. */
    protected DefaultSetting(K kind) {
        this(kind, null);
    }

    /** Constructs a value with given value kind and content. */
    protected DefaultSetting(K kind, @Nullable C content) {
        assert kind.parser()
            .isValue(content);
        this.kind = kind;
        this.content = content;
    }

    /** Returns the kind of value. */
    @Override
    public K getKind() {
        return this.kind;
    }

    private final K kind;

    /**
     * Returns the content of the value.
     * May be {@code null}, if this is allowed by the value kind.
     */
    @Override
    public C getContent() {
        return this.content;
    }

    private final @Nullable C content;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.kind.hashCode();
        @Nullable C content = this.content;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Setting)) {
            return false;
        }
        Setting<?,?> other = (Setting<?,?>) obj;
        @Nullable C content = this.content;
        if (content == null) {
            if (other.getContent() != null) {
                return false;
            }
        } else if (!content.equals(other.getContent())) {
            return false;
        }
        return this.kind.equals(other.getKind());
    }

    @Override
    public String toString() {
        return "Setting[" + this.kind + "," + this.content + "]";
    }
}
