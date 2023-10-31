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
package nl.utwente.groove.control;

import nl.utwente.groove.control.CtrlPar.Const;

/** Source for a variable assignment in a control step. */
public record Binding(Binding.Source type, int index, Const value) {

    /** Returns the index, if this is not a constant assignment. */
    public int index() {
        assert type() != Source.CONST;
        return this.index;
    }

    /** Returns the assigned value, if this is a value binding. */
    public Const value() {
        assert type() == Source.CONST;
        return this.value;
    }

    @Override
    public String toString() {
        return this.type.name() + ":" + (type() == Source.CONST ? value() : index());
    }

    /** Constructs a binding to a constant value.
     * @see Source#CONST
     */
    public static Binding value(Const value) {
        return new Binding(Source.CONST, 0, value);
    }

    /** Constructs a binding to a variable in the caller location.
     * This is used for procedure call arguments.
     * @see Source#CALLER
     */
    public static Binding caller(int index) {
        return new Binding(Source.CALLER, index, null);
    }

    /** Constructs a binding to a variable in the source location.
     * @see Source#VAR
     */
    public static Binding var(int index) {
        return new Binding(Source.VAR, index, null);
    }

    /** Constructs a binding to an anchor node of a rule match.
     * @see Source#ANCHOR
     */
    public static Binding anchor(int index) {
        return new Binding(Source.ANCHOR, index, null);
    }

    /** Constructs a binding to a creator node in a rule application.
     * @see Source#CREATOR
     */
    public static Binding creator(int index) {
        return new Binding(Source.CREATOR, index, null);
    }

    /** Kind of source for a variable assignment. */
    public enum Source {
        /** Source location variable. */
        VAR,
        /** Parent location variable. */
        CALLER,
        /** Rule anchor. */
        ANCHOR,
        /** Creator node image. */
        CREATOR,
        /** Constant value. */
        CONST,;
    }
}