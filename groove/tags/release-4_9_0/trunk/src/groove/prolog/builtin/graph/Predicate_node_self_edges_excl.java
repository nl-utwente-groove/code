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
package groove.prolog.builtin.graph;

import java.util.List;

/**
 * Predicate node_self_edges(+Graph,+Node,?Edge)
 * @author Michiel Hendriks
 */
public class Predicate_node_self_edges_excl extends Predicate_node_self_edges {
    @Override
    protected boolean isValidList(List<String> testSet, List<String> curSet) {
        return super.isValidList(testSet, curSet)
            && testSet.size() == curSet.size();
    }
}
