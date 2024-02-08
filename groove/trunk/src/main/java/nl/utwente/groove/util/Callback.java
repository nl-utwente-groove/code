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

/**
 * Interface for a parameterless, void method.
 * {@link Runnable} would fir the bill except that it is associated with thread.
 * @see <a href="https://stackoverflow.com/questions/22723599/bad-practice-to-use-runnable-as-callback-subroutine">Stack overflow</a>
 * @see <a href="https://stackoverflow.com/questions/23958814/functional-interface-that-takes-nothing-and-returns-nothing">Stack overflow</a>
 * @author Arend Rensink
 * @version $Revision$
 */
@FunctionalInterface
public interface Callback {
    /** Callback method to be implemented. */
    public void call();
}
