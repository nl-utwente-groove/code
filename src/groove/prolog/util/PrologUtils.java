/*
 * Groove Prolog Interface
 * Copyright (C) 2009 Michiel Hendriks, University of Twente
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package groove.prolog.util;

import gnu.prolog.term.CompoundTerm;
import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.TermConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Utility class
 * 
 * @author Michiel Hendriks
 */
public class PrologUtils {
    /**
     * Create a list of JavaObjectTerms from the given collection
     */
    public static final List<Term> createJOTlist(Collection<?> elements) {
        List<Term> result = new ArrayList<>();
        for (Object o : elements) {
            result.add(new JavaObjectTerm(o));
        }
        return result;
    }

    /**
     * Create a list of JavaObjectTerms from the given collection
     */
    public static final List<Term> createJOTlist(Object[] elements) {
        List<Term> result = new ArrayList<>();
        for (Object o : elements) {
            result.add(new JavaObjectTerm(o));
        }
        return result;
    }

    /** expand compound terms */
    public static void getTermSet(Term term, Set<Term> set) {
        term = term.dereference();
        if (term instanceof CompoundTerm) {
            CompoundTerm ct = (CompoundTerm) term;
            for (int i = ct.tag.arity - 1; i >= 0; i--) {
                getTermSet(ct.args[i], set);
            }
        } else {
            if (!TermConstants.emptyListAtom.equals(term)) {
                set.add(term);
            }
        }
    }

    /**
     * Private constructor
     */
    private PrologUtils() {
        /**
         * Left blank by design
         */
    }
}
