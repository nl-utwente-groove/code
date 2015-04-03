/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
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

package groove.explore.result;

/**
 * Acceptor that accepts no state that is added to the LTS.
 * This is the default behaviour of super class {@link Acceptor} so there is
 * nothing to be done here.
 *
 * @author Eduardo Zambon
 */
public class NoStateAcceptor extends Acceptor {
    /**
     * Constructor. Only calls super method.
     */
    private NoStateAcceptor(boolean prototype) {
        super(prototype);
    }

    @Override
    public NoStateAcceptor newAcceptor(int bound) {
        return new NoStateAcceptor(false);
    }

    /** Returns the singleton instance of this class. */
    public static final NoStateAcceptor INSTANCE = new NoStateAcceptor(true);
}