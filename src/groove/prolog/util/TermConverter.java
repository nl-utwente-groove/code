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

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.CompoundTerm;
import gnu.prolog.term.CompoundTermTag;
import gnu.prolog.term.FloatTerm;
import gnu.prolog.term.IntegerTerm;
import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.term.VariableTerm;
import gnu.prolog.vm.TermConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Convert Prolog Terms to Java objects
 * 
 * @author Michiel Hendriks
 */
public final class TermConverter {
    /**
     * Converts a map of VariableTerms into a map of java objects
     */
    public static Map<String,Object> convert(Map<String,VariableTerm> rawVars) {
        HashMap<String,Object> result = new HashMap<>();
        for (Entry<String,VariableTerm> entry : rawVars.entrySet()) {
            if (entry.getKey().charAt(0) != '_') {
                result.put(entry.getKey(), convert(entry.getValue()));
            }
        }
        return result;
    }

    /**
     * Converts a term into a java object
     */
    public static Object convert(Term value) {
        value = value.dereference();
        if (value instanceof JavaObjectTerm) {
            return ((JavaObjectTerm) value).value;
        } else if (value instanceof IntegerTerm) {
            return ((IntegerTerm) value).value;
        } else if (value instanceof FloatTerm) {
            return ((FloatTerm) value).value;
        } else if (value instanceof AtomTerm) {
            return ((AtomTerm) value).value;
        } else if (value instanceof CompoundTerm) {
            CompoundTerm ct = (CompoundTerm) value;
            if (ct.tag == TermConstants.listTag) {
                List<Object> compound = new ArrayList<>();
                while (true) {
                    value = ct.args[0].dereference();
                    if (value == TermConstants.emptyListAtom) {
                        // nop
                    } else {
                        compound.add(convert(value));
                    }
                    value = ct.args[1];
                    if (value != null) {
                        value = value.dereference();
                    }
                    if (value == TermConstants.emptyListAtom) {
                        break;
                    } else if (value instanceof CompoundTerm
                        && ((CompoundTerm) value).tag == TermConstants.listTag) {
                        ct = (CompoundTerm) value;
                        continue;
                    } else {
                        // anonymous list tail
                        compound.add(SpecialValue.ANONYMOUS_LIST_TAIL);
                        break;
                    }
                }
                return compound;
            } else if (ct.tag == CompoundTermTag.divide2) {
                return CompoundTermTag.get(ct).toString();
            }
        }
        return null;
    }

    /**
     * A special result value
     * 
     * @author Michiel Hendriks
     */
    public static enum SpecialValue {
        /**
         * ANONYMOUS_LIST_TAIL
         */
        ANONYMOUS_LIST_TAIL
    }
}
