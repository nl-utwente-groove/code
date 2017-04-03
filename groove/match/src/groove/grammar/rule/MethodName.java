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
package groove.grammar.rule;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import groove.grammar.QualName;
import groove.grammar.host.HostGraph;
import groove.util.Exceptions;

/**
 * encoded method name in a given language,
 * with functionality to invoke it on a given host graph and rule event.
 * @author Arend Rensink
 * @version $Revision $
 */
@NonNullByDefault
abstract public class MethodName {
    /** Constructs a method name for a given language. */
    protected MethodName(Language language, QualName qualName) {
        this.language = language;
        this.qualName = qualName;
    }

    /**
     * Returns the qualified name of this method.
     */
    public QualName getQualName() {
        return this.qualName;
    }

    /** The qualified name of this method. */
    private final QualName qualName;

    /** Returns the language of this method. */
    public Language getLanguage() {
        return this.language;
    }

    /** The language of this method. */
    private final Language language;

    /** Tests if a method with this name actually exists. */
    abstract public boolean exists();

    /** Invokes this method on a given host graph and event.
     * @throws UnsupportedOperationException TODO
     * @throws InvocationTargetException TODO
     * @see #exists()
     */
    abstract public boolean invoke(HostGraph graph, RuleToHostMap anchorMap)
        throws UnsupportedOperationException, InvocationTargetException;

    @Override
    public String toString() {
        return getLanguage().getName() + ":" + getQualName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.language.hashCode();
        result = prime * result + this.qualName.hashCode();
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MethodName)) {
            return false;
        }
        MethodName other = (MethodName) obj;
        if (this.language != other.language) {
            return false;
        }
        if (!this.qualName.equals(other.qualName)) {
            return false;
        }
        return true;
    }

    /** Creates a method name object of the correct type. */
    public static MethodName createName(Language language, QualName qualName) {
        switch (language) {
        case JAVA:
            return new JavaMethodName(qualName);
        default:
            throw Exceptions.UNREACHABLE;
        }
    }

    /** Language in which the method is implemented. */
    public static enum Language {
        /** The Java language. */
        JAVA,;

        private Language() {
            this.name = name().toLowerCase();
        }

        private Language(String name) {
            this.name = name;
        }

        /**
         * Returns the name of this language.
         */
        public String getName() {
            return this.name;
        }

        private final String name;

        @Override
        public String toString() {
            return getName();
        }
    }
}
