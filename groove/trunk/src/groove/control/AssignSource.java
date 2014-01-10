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
package groove.control;

import groove.control.CtrlPar.Const;

/** Source for a variable assignment in a control step. */
public class AssignSource {
    /**
     * Internal constructor setting all fields.
     */
    private AssignSource(AssignSource.SourceKind type, int index, Const value) {
        this.type = type;
        this.index = index;
        this.value = value;
    }

    /** Returns the type of assignment source. */
    public AssignSource.SourceKind getType() {
        return this.type;
    }

    /** Returns the index, if this is not a constant assignment. */
    public int getIndex() {
        assert getType() != SourceKind.CONST;
        return this.index;
    }

    /** Returns the assigned value, if this is a constant assignment. */
    public Const getValue() {
        assert getType() == SourceKind.CONST;
        return this.value;
    }

    private final AssignSource.SourceKind type;
    private final int index;
    private final Const value;

    /** Constructs an {@link SourceKind#ARG} assignment source. */
    public static AssignSource arg(int index) {
        return new AssignSource(SourceKind.ARG, index, null);
    }

    /** Constructs a {@link SourceKind#CONST} assignment source. */
    public static AssignSource value(Const value) {
        return new AssignSource(SourceKind.CONST, 0, value);
    }

    /** Constructs an {@link SourceKind#CALLER} assignment source. */
    public static AssignSource caller(int index) {
        return new AssignSource(SourceKind.CALLER, index, null);
    }

    /** Constructs an {@link SourceKind#VAR} assignment source. */
    public static AssignSource var(int index) {
        return new AssignSource(SourceKind.VAR, index, null);
    }

    /** Constructs an {@link SourceKind#OUT} assignment source. */
    public static AssignSource out(int index) {
        return new AssignSource(SourceKind.OUT, index, null);
    }

    /** Kind of source for a variable assignment. */
    public static enum SourceKind {
        /** Transition argument. */
        ARG,
        /** Source location variable. */
        VAR,
        /** Caller location variable. */
        CALLER,
        /** Rule anchor/fresh node. */
        OUT,
        /** Constant value. */
        CONST, ;
    }
}