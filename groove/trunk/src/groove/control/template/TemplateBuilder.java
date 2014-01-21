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
package groove.control.template;

import groove.control.Call;
import groove.control.Position;
import groove.control.SingleAttempt;
import groove.control.Position.Type;
import groove.control.term.Derivation;
import groove.control.term.Term;
import groove.graph.GraphInfo;
import groove.util.Duo;
import groove.util.Pair;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
        Template result = this.template = createTemplate(name, init.getDepth());
        Map<Term,Location> locMap = this.locMap = new HashMap<Term,Location>();
        Deque<Term> fresh = this.fresh = new LinkedList<Term>();
        // set the initial location
        locMap.put(init, result.getStart());
        result.getStart().setType(init.getType());
        fresh.add(init);
        // do the following as long as there are fresh locations
        while (!fresh.isEmpty()) {
            Term next = fresh.poll();
            Location source = locMap.get(next);
            for (Derivation edge : next.getAttempt()) {
                Location target = addLocation(edge.target());
                addSwitch(new Switch(source, target, edge.getCall()));
            }
            Term succTerm = next.onSuccess();
            if (succTerm != null) {
                if (succTerm.isDead()) {
                    setDeadlockVerdict(source, true);
                } else {
                    Location target = addLocation(succTerm);
                    addSwitch(new Switch(source, target, true));
                }
            }
            Term failTerm = next.onFailure();
            if (failTerm != null) {
                if (failTerm.isDead()) {
                    setDeadlockVerdict(source, false);
                } else {
                    Location target = addLocation(failTerm);
                    addSwitch(new Switch(source, target, false));
                }
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
     * Adds a control location corresponding to a given symbolic term to the
     * template and auxiliary data structures, if it does not yet exist.
     * @param term the symbolic location to be added
     * @return the fresh or pre-existing control location
     */
    private Location addLocation(Term term) {
        Location result = this.locMap.get(term);
        if (result == null) {
            this.fresh.add(term);
            result = this.template.addLocation(term.getDepth());
            this.locMap.put(term, result);
            result.setType(term.getType());
        }
        return result;
    }

    /** Sets a {@link Deadlock} verdict for a location. */
    private void setDeadlockVerdict(Location loc, boolean success) {
        if (success) {
            loc.setDeadSuccess();
        } else {
            loc.setDeadFailure();
        }
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
        this.recordMap = new HashMap<Location,Record<TemplatePosition>>();
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
        for (Cell cell : orig) {
            Map<Record<Cell>,Cell> split = new HashMap<Record<Cell>,Cell>();
            for (TemplatePosition loc : cell) {
                Record<Cell> rec = append(this.recordMap.get(loc), orig);
                Cell locCell = split.get(rec);
                if (locCell == null) {
                    split.put(rec, locCell = new Cell());
                }
                locCell.add(loc);
            }
            result.addAll(split.values());
        }
        return result;
    }

    /** Computes the record of the choice and call switches for a given location. */
    private Record<TemplatePosition> computeRecord(TemplatePosition loc) {
        Map<Call,Set<TemplatePosition>> callMap =
            new HashMap<Call,Set<TemplatePosition>>();
        for (SingleAttempt<Location> edge : loc.getAttempt()) {
            Call call = edge.getCall();
            Set<TemplatePosition> targets = callMap.get(call);
            if (targets == null) {
                callMap.put(call, targets = new HashSet<TemplatePosition>());
            }
            targets.add(edge.target());
        }
        return new Record<TemplatePosition>(loc.onSuccess(), loc.onFailure(),
            callMap, loc.getType(), loc instanceof Deadlock);
    }

    /** Converts a record pointing to locations, to a record pointing to cells. */
    private Record<Cell> append(Record<TemplatePosition> record, Partition part) {
        Cell success = part.getCell(record.getSuccess());
        Cell failure = part.getCell(record.getFailure());
        Map<Call,Set<Cell>> map = new HashMap<Call,Set<Cell>>();
        for (Map.Entry<Call,Set<TemplatePosition>> e : record.getMap().entrySet()) {
            Set<Cell> target = new HashSet<Cell>();
            for (TemplatePosition loc : e.getValue()) {
                target.add(part.getCell(loc));
            }
            map.put(e.getKey(), target);
        }
        return new Record<Cell>(success, failure, map, record.getType(),
            record.isDeadlock());
    }

    /** Computes the quotient of {@link #template} from a given partition. */
    private Template computeQuotient(Partition partition) {
        Template result =
            createTemplate("Normalised " + this.template.getName(),
                this.template.getStart().getDepth());
        // set of representative source locations
        Set<TemplatePosition> reprSet = new HashSet<TemplatePosition>();
        // map from all source locations to the result locations
        Map<TemplatePosition,TemplatePosition> locMap =
            new HashMap<TemplatePosition,TemplatePosition>();
        for (Cell cell : partition) {
            // representative location of the cell
            TemplatePosition repr;
            TemplatePosition image;
            if (cell.contains(this.template.getStart())) {
                repr = this.template.getStart();
                Location locImage = result.getStart();
                locImage.setType(repr.getType());
                image = locImage;
            } else {
                repr = cell.iterator().next();
                if (repr instanceof Deadlock) {
                    image = repr;
                } else {
                    Location locImage = result.addLocation(repr.getDepth());
                    locImage.setType(repr.getType());
                    image = locImage;
                }
            }
            reprSet.add(repr);
            for (TemplatePosition loc : cell) {
                locMap.put(loc, image);
            }
        }
        // Add transitions to the result
        for (TemplatePosition repr : reprSet) {
            if (repr instanceof Deadlock) {
                continue;
            }
            Location locRepr = (Location) repr;
            Location image = (Location) locMap.get(repr);
            if (locRepr.onSuccess() instanceof Deadlock) {
                image.setDeadSuccess();
            }
            if (locRepr.onFailure() instanceof Deadlock) {
                image.setDeadFailure();
            }
            for (Switch edge : locRepr.getSwitches()) {
                Location target = (Location) locMap.get(edge.target());
                if (edge.isVerdict()) {
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
    private Map<Location,Record<TemplatePosition>> recordMap;

    /** Callback factory method for a template. */
    private Template createTemplate(String name, int depth) {
        return new Template(name, depth);
    }

    /** Returns the singleton instance of this class. */
    public static TemplateBuilder instance() {
        return INSTANCE;
    }

    /** Template under construction. */
    private Template template;
    private static final TemplateBuilder INSTANCE = new TemplateBuilder();

    /** Local type for a cell of a partition of locations. */
    private static class Cell extends TreeSet<TemplatePosition> {
        // empty
    }

    /** Local type for a partition of locations. */
    private static class Partition extends LinkedHashSet<Cell> {
        @Override
        public boolean add(Cell cell) {
            boolean result = super.add(cell);
            if (result) {
                for (TemplatePosition loc : cell) {
                    this.cellMap.put(loc, cell);
                }
            }
            return result;
        }

        Cell getCell(TemplatePosition loc) {
            return loc == null ? null : this.cellMap.get(loc);
        }

        private final Map<TemplatePosition,Cell> cellMap =
            new TreeMap<TemplatePosition,TemplateBuilder.Cell>();
    }

    /**
     * Convenience type to collect the targets of the verdicts and call switches
     * of a given location.
     * @param <L> type of the targets
     */
    private static class Record<L> extends Pair<Duo<L>,Map<Call,Set<L>>> {
        Record(L success, L failure, Map<Call,Set<L>> transMap,
                Position.Type type, boolean deadlock) {
            super(Duo.newDuo(success, failure), transMap);
            this.type = type;
            this.deadlock = deadlock;
        }

        L getSuccess() {
            return one().one();
        }

        L getFailure() {
            return one().two();
        }

        Map<Call,Set<L>> getMap() {
            return two();
        }

        Type getType() {
            return this.type;
        }

        private final Type type;

        boolean isDeadlock() {
            return this.deadlock;
        }

        private final boolean deadlock;

        @SuppressWarnings("rawtypes")
        @Override
        public boolean equals(Object obj) {
            if (!super.equals(obj)) {
                return false;
            }
            return this.deadlock == ((Record) obj).deadlock;
        }

        @Override
        public int hashCode() {
            int prime = 31;
            return prime * super.hashCode() + (this.deadlock ? 1231 : 1237);
        }
    }
}
