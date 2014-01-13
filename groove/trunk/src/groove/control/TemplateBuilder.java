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
package groove.control;

import groove.control.symbolic.OutEdge;
import groove.control.symbolic.Term;
import groove.graph.GraphInfo;
import groove.util.Pair;

import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class for constructing control automata.
 * @author Arend Rensink
 * @version $Revision $
 */
public class TemplateBuilder {
    /** Private constructor for the singleton instance. */
    private TemplateBuilder() {
        // empty
    }

    /** Constructs a template from a symbolic location. */
    public Template build(String name, Term init) {
        // initialise the auxiliary data structures
        Template result = this.template = createTemplate(name);
        Map<Term,Location> locMap = this.locMap = new HashMap<Term,Location>();
        Deque<Term> fresh = this.fresh = new LinkedList<Term>();
        // set the initial location
        locMap.put(init, result.getStart());
        fresh.add(init);
        // do the following as long as there are fresh locations
        while (!fresh.isEmpty()) {
            Term next = fresh.poll();
            Location source = locMap.get(next);
            for (OutEdge edge : next.getOutEdges()) {
                Location target = addLocation(edge.getTarget());
                addSwitch(new Switch(source, target, edge.getCall()));
            }
            Term succTerm = next.getSuccess();
            if (succTerm != null) {
                Location target = addLocation(succTerm);
                addSwitch(new Switch(source, target, true));
            }
            Term failTerm = next.getFailure();
            if (failTerm != null) {
                Location target = addLocation(failTerm);
                addSwitch(new Switch(source, target, false));
            }
            if (next.isFinal()) {
                source.setFinal();
            }
        }
        result.setFixed();
        return result;
    }

    /**
     * Adds an edge to the template under construction.
     */
    private void addSwitch(Switch edge) {
        this.template.addEdge(edge);
    }

    /** 
     * Adds a control location corresponding to a given symbolic state to the
     * template and auxiliary data structures, if it does not yet exist.
     * @param symb the symbolic location to be added
     * @return the fresh or pre-existing control location
     */
    private Location addLocation(Term symb) {
        Location result = this.locMap.get(symb);
        if (result == null) {
            this.fresh.add(symb);
            result = this.template.addLocation(symb.getTransitDepth());
            this.locMap.put(symb, result);
            if (symb.isFinal()) {
                result.setFinal();
            }
        }
        return result;
    }

    /** Mapping from symbolic locations to locations. */
    private Map<Term,Location> locMap;
    /** Unexplored set of symbolic locations. */
    private Deque<Term> fresh;

    /** 
     * Computes and returns a normalised version of a given template.
     * Normalisation implies minimisation w.r.t. bisimilarity.
     */
    public Template normalise(Template orig) {
        assert orig.isFixed();
        Template result;
        if (!GraphInfo.hasErrors(orig)) {
            this.template = orig;
            Partition partition = computePartition();
            result = computeQuotient(partition);
            result.setFixed();
        } else {
            result = orig;
        }
        result.setFixed();
        return result;
    }

    /** Computes a location partition for {@link #template}. */
    private Partition computePartition() {
        this.recordMap = new HashMap<Location,Record<Location>>();
        for (Location loc : this.template.nodeSet()) {
            this.recordMap.put(loc, computeRecord(loc));
        }
        Partition result = initPartition();
        int cellCount = result.size();
        int oldCellCount;
        do {
            result = refinePartition(result);
            oldCellCount = cellCount;
            cellCount = result.size();
        } while (cellCount > oldCellCount);
        return result;
    }

    /** 
     * Creates an initial partition for {@link #template},
     * with distinguished cells for the initial location and all locations
     * of a given transient depth.
     */
    private Partition initPartition() {
        Partition result = new Partition();
        Map<Integer,Cell> depthMap = new HashMap<Integer,Cell>();
        Cell finals = new Cell();
        for (Location loc : this.template.nodeSet()) {
            if (loc.isFinal()) {
                finals.add(loc);
            } else {
                Cell cell = depthMap.get(loc.getDepth());
                if (cell == null) {
                    depthMap.put(loc.getDepth(), cell = new Cell());
                }
                cell.add(loc);
            }
        }
        result.add(finals);
        result.addAll(depthMap.values());
        return result;
    }

    /** 
     * Refines a given partition and returns the result.
     * The refinement is done by splitting every cell into new cells in
     * which all locations have the same success and failure targets
     * as well as the same call targets, in terms of cells of the original partition.
     */
    private Partition refinePartition(Partition orig) {
        Partition result = new Partition();
        for (Map.Entry<Location,Cell> e : orig.entrySet()) {
            Map<Record<Cell>,Cell> split = new HashMap<Record<Cell>,Cell>();
            for (Location loc : e.getValue()) {
                Record<Cell> rec = append(this.recordMap.get(loc), orig);
                Cell cell = split.get(rec);
                if (cell == null) {
                    split.put(rec, cell = new Cell());
                }
                cell.add(loc);
            }
            result.addAll(split.values());
        }
        return result;
    }

    /** Computes the record of the choice and call switches for a given location. */
    private Record<Location> computeRecord(Location loc) {
        Map<Call,Set<Location>> callMap = new HashMap<Call,Set<Location>>();
        for (Switch edge : loc.getOutCalls()) {
            Call call = edge.getCall();
            Set<Location> targets = callMap.get(call);
            if (targets == null) {
                callMap.put(call, targets = new HashSet<Location>());
            }
            targets.add(edge.target());
        }
        return new Record<Location>(loc.getSuccessNext(), loc.getFailureNext(),
            callMap);
    }

    /** Converts a record pointing to locations, to a record pointing to cells. */
    private Record<Cell> append(Record<Location> record, Partition part) {
        Cell success =
            record.hasSuccess() ? part.get(record.getSuccess()) : null;
        Cell failure =
            record.hasFailure() ? part.get(record.getFailure()) : null;
        Map<Call,Set<Cell>> map = new HashMap<Call,Set<Cell>>();
        for (Map.Entry<Call,Set<Location>> e : record.getMap().entrySet()) {
            Set<Cell> target = new HashSet<Cell>();
            for (Location loc : e.getValue()) {
                target.add(part.get(loc));
            }
            map.put(e.getKey(), target);
        }
        return new Record<Cell>(success, failure, map);
    }

    /** Computes the quotient of {@link #template} from a given partition. */
    private Template computeQuotient(Partition partition) {
        Template result = createTemplate(this.template.getName());
        // set of representative source locations
        Set<Location> reprSet = new HashSet<Location>();
        // map from all source locations to the result locations
        Map<Location,Location> locMap = new HashMap<Location,Location>();
        for (Cell cell : partition.values()) {
            // representative location of the cell
            Location repr;
            Location image;
            if (cell.contains(this.template.getStart())) {
                repr = this.template.getStart();
                image = result.getStart();
            } else {
                repr = cell.iterator().next();
                image = result.addLocation(repr.getDepth());
            }
            if (repr.isFinal()) {
                image.setFinal();
            }
            reprSet.add(repr);
            for (Location loc : cell) {
                locMap.put(loc, image);
            }
        }
        // Add transitions to the result
        for (Location repr : reprSet) {
            Location image = locMap.get(repr);
            for (Switch edge : repr.getOutEdges()) {
                Location target = locMap.get(edge.target());
                if (edge.isChoice()) {
                    result.addEdge(new Switch(image, target, edge.isSuccess()));
                } else {
                    result.addEdge(new Switch(image, target, edge.getCall()));
                }
            }
        }
        result.setFixed();
        return result;
    }

    /** Mapping from locations to their records, in terms of target locations. */
    private Map<Location,Record<Location>> recordMap;

    /** Callback factory method for a template. */
    private Template createTemplate(String name) {
        return new Template(name);
    }

    /** Returns the singleton instance of this class. */
    public static TemplateBuilder instance() {
        return INSTANCE;
    }

    /** Template under construction. */
    private Template template;
    private static final TemplateBuilder INSTANCE = new TemplateBuilder();

    /** Local type for a cell of a partition of locations. */
    private static class Cell extends TreeSet<Location> {
        // empty
    }

    /** Local type for a partition of locations. */
    private static class Partition extends TreeMap<Location,Cell> {
        void add(Cell cell) {
            for (Location loc : cell) {
                put(loc, cell);
            }
        }

        void addAll(Collection<Cell> cells) {
            for (Cell cell : cells) {
                add(cell);
            }
        }
    }

    /**
     * Convenience type to collect the targets of the choice and call switches
     * of a given location.
     * @param <L> type of the targets
     */
    private static class Record<L> extends Pair<Pair<L,L>,Map<Call,Set<L>>> {
        Record(L success, L failure, Map<Call,Set<L>> transMap) {
            super(Pair.newPair(success, failure), transMap);
        }

        boolean hasSuccess() {
            return getSuccess() != null;
        }

        L getSuccess() {
            return one().one();
        }

        boolean hasFailure() {
            return getFailure() != null;
        }

        L getFailure() {
            return one().two();
        }

        Map<Call,Set<L>> getMap() {
            return two();
        }
    }
}
