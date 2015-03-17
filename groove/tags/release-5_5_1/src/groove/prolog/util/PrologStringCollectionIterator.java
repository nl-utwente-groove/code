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
package groove.prolog.util;

/* GNU Prolog for Java
 * Copyright (C) 1997-1999  Constantine Plotnikov
 * Copyright (C) 2009       Michiel Hendriks
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA. The text of license can be also found 
 * at http://www.gnu.org/copyleft/lgpl.html
 */

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.BacktrackInfo;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologCode;
import gnu.prolog.vm.PrologException;

import java.util.Iterator;

/**
 * String collection iterator which can be used by PrologCode implementations.
 * 
 * @author Michiel Hendriks, modified by Lesley Wevers
 */
public class PrologStringCollectionIterator extends BacktrackInfo {
    /**
     * The iterator it will go through
     */
    protected Iterator<String> iterator;

    /**
     * The term to unify the value with
     */
    protected Term destTerm;

    /**
     * The start undo position
     */
    protected int startUndoPosition;

    /**
     * @param iterable
     *          The collection to iterate over
     * @param destination
     *          The destination term
     * @param undoPosition
     *          the value of interpreter.getUndoPosition();
     */
    public PrologStringCollectionIterator(Iterable<String> iterable,
            Term destination, int undoPosition) {
        this(iterable.iterator(), destination, undoPosition);
    }

    /**
     * @param iterable
     *          The collection to iterate over
     * @param destination
     *          The destination term
     * @param undoPosition
     *          the value of interpreter.getUndoPosition();
     */
    public PrologStringCollectionIterator(Iterator<String> iterable,
            Term destination, int undoPosition) {
        super(-1, -1);
        this.iterator = iterable;
        this.destTerm = destination;
        this.startUndoPosition = undoPosition;
    }

    /**
     * @return the startUndoPosition
     */
    public int getUndoPosition() {
        return this.startUndoPosition;
    }

    /**
     * Get the next value
     * 
     * @param interpreter               The prolog interpreter
     * @return PrologCode               Return code
     */
    public int nextSolution(Interpreter interpreter) throws PrologException {
        while (this.iterator.hasNext()) {
            Term term = AtomTerm.get(this.iterator.next());
            int rc = interpreter.unify(this.destTerm, term);
            if (rc == PrologCode.FAIL) {
                interpreter.undo(this.startUndoPosition);
                continue;
            }
            interpreter.pushBacktrackInfo(this);
            return PrologCode.SUCCESS;
        }
        return PrologCode.FAIL;
    }
}
