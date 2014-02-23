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
import groove.control.Position.Type;
import groove.control.Procedure;
import groove.control.term.Derivation;
import groove.control.term.DerivationAttempt;
import groove.control.term.Term;
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

    /** 
     * Constructs automata templates for the main term as well as
     * all procedures of a given program.
     */
    public Template build(Program prog) {
        this.freshMap.clear();
        this.locMapMap.clear();
        Template result = null;
        for (Procedure proc : prog.getProcs().values()) {
            Template template = new Template(proc);
            proc.setTemplate(template);
        }
        for (Procedure proc : prog.getProcs().values()) {
            build(proc.getTerm(), proc.getTemplate());
        }
        if (prog.hasBody()) {
            result = new Template(prog.getName());
            build(prog.getTerm(), result);
        }
        return result;
    }

    /** 
     * Constructs an automata templates for a given term.
     */
    public Template build(Term init) {
        Template result = new Template(init.toString());
        this.freshMap.clear();
        this.locMapMap.clear();
        build(init, result);
        return result;
    }

    /** 
     * Constructs a template from a term. 
     * @param result the template to be built
     * @param init the term for which the template should be built
     */
    private void build(Term init, Template result) {
        assert init.getDepth() == 0 : "Can't build template from transient term";
        // initialise the auxiliary data structures
        Map<Term,Location> locMap = getLocMap(result);
        Deque<Term> fresh = getFresh(result);
        // set the initial location
        locMap.put(init, result.getStart());
        result.getStart().setType(init.getType());
        fresh.add(init);
        // do the following as long as there are fresh locations
        while (!fresh.isEmpty()) {
            Term next = fresh.poll();
            if (!next.isTrial()) {
                continue;
            }
            Location source = locMap.get(next);
            DerivationAttempt attempt = next.getAttempt();
            Location succTarget = addLocation(result, attempt.onSuccess());
            Location failTarget = addLocation(result, attempt.onFailure());
            SwitchAttempt locAttempt = new SwitchAttempt(source, succTarget, failTarget);
            for (Derivation deriv : attempt) {
                // build the (possibly nested) switch
                Location target = addLocation(result, deriv.onFinish());
                Switch swit = new Switch(source, target, deriv.getCall(), deriv.getDepth());
                Switch last = swit;
                while (deriv.hasNested()) {
                    Procedure caller = (Procedure) deriv.getCall().getUnit();
                    Template called = caller.getTemplate();
                    Location start = called.getStart();
                    deriv = deriv.getNested();
                    target = addLocation(called, deriv.onFinish());
                    Switch nested = new Switch(start, target, deriv.getCall(), deriv.getDepth());
                    last.setNested(nested);
                    last = nested;
                }
                locAttempt.add(swit);
            }
            source.setAttempt(locAttempt);
        }
    }

    /** 
     * Adds a control location corresponding to a given symbolic term to the
     * template and auxiliary data structures, if it does not yet exist.
     * @param term the symbolic location to be added
     * @return the fresh or pre-existing control location
     */
    private Location addLocation(Template template, Term term) {
        Map<Term,Location> locMap = getLocMap(template);
        Location result = locMap.get(term);
        if (result == null) {
            getFresh(template).add(term);
            result = template.addLocation(term.getDepth());
            locMap.put(term, result);
            result.setType(term.getType());
        }
        return result;
    }

    /**
     * Returns the mapping from terms to locations for a given template.
     */
    private Map<Term,Location> getLocMap(Template template) {
        Map<Term,Location> locMap = this.locMapMap.get(template);
        if (locMap == null) {
            this.locMapMap.put(template, locMap = new HashMap<Term,Location>());
        }
        return locMap;
    }

    /**
     * Returns the mapping from terms to locations for a given template.
     */
    private Deque<Term> getFresh(Template template) {
        Deque<Term> result = this.freshMap.get(template);
        if (result == null) {
            this.freshMap.put(template, result = new LinkedList<Term>());
        }
        return result;
    }

    /** For each template, a mapping from terms to locations. */
    private final Map<Template,Map<Term,Location>> locMapMap =
        new HashMap<Template,Map<Term,Location>>();
    /** Unexplored set of symbolic locations per template. */
    private final Map<Template,Deque<Term>> freshMap = new HashMap<Template,Deque<Term>>();

    /** 
     * Computes and returns a normalised version of a given template.
     * Normalisation implies minimisation w.r.t. bisimilarity.
     */
    public Template normalise(Template orig) {
        assert orig.getStart().getDepth() == 0;
        this.template = orig;
        Partition partition = computePartition();
        return computeQuotient(partition);
    }

    /** Computes a location partition for {@link #template}. */
    private Partition computePartition() {
        this.recordMap = new HashMap<Location,Record<Location>>();
        for (Location loc : this.template.getLocations()) {
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
        for (Location loc : this.template.getLocations()) {
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
            for (Location loc : cell) {
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
    private Record<Location> computeRecord(Location loc) {
        Map<Call,Set<Location>> callMap = new HashMap<Call,Set<Location>>();
        Location onSuccess = null;
        Location onFailure = null;
        if (loc.isTrial()) {
            SwitchAttempt attempt = loc.getAttempt();
            for (Switch swit : attempt) {
                Call call = swit.getCall();
                Set<Location> targets = callMap.get(call);
                if (targets == null) {
                    callMap.put(call, targets = new HashSet<Location>());
                }
                targets.add(swit.onFinish());
            }
            onSuccess = attempt.onSuccess();
            onFailure = attempt.onFailure();
        }
        return new Record<Location>(onSuccess, onFailure, callMap, loc.getType());
    }

    /** Converts a record pointing to locations, to a record pointing to cells. */
    private Record<Cell> append(Record<Location> record, Partition part) {
        Cell success = part.getCell(record.getSuccess());
        Cell failure = part.getCell(record.getFailure());
        Map<Call,Set<Cell>> map = new HashMap<Call,Set<Cell>>();
        for (Map.Entry<Call,Set<Location>> e : record.getMap().entrySet()) {
            Set<Cell> target = new HashSet<Cell>();
            for (Location loc : e.getValue()) {
                target.add(part.getCell(loc));
            }
            map.put(e.getKey(), target);
        }
        return new Record<Cell>(success, failure, map, record.getType());
    }

    /** Computes the quotient of {@link #template} from a given partition. */
    private Template computeQuotient(Partition partition) {
        Template result =
            createTemplate(this.template.getOwner(), "Normalised " + this.template.getName());
        // set of representative source locations
        Set<Location> reprSet = new HashSet<Location>();
        // map from all source locations to the result locations
        Map<Location,Location> locMap = new HashMap<Location,Location>();
        for (Cell cell : partition) {
            // representative location of the cell
            Location repr;
            Location image;
            if (cell.contains(this.template.getStart())) {
                repr = this.template.getStart();
                image = result.getStart();
                image.setType(repr.getType());
            } else {
                repr = cell.iterator().next();
                image = result.addLocation(repr.getDepth());
                image.setType(repr.getType());
            }
            reprSet.add(repr);
            for (Location loc : cell) {
                locMap.put(loc, image);
            }
        }
        // Add attempts to the result
        for (Location repr : reprSet) {
            if (!repr.isTrial()) {
                continue;
            }
            SwitchAttempt reprAttempt = repr.getAttempt();
            Location image = locMap.get(repr);
            Location imageSucc = locMap.get(reprAttempt.onSuccess());
            Location imageFail = locMap.get(reprAttempt.onFailure());
            SwitchAttempt imageAttempt = new SwitchAttempt(image, imageSucc, imageFail);
            for (Switch swit : reprAttempt) {
                Location target = locMap.get(swit.onFinish());
                Switch imageSwitch = new Switch(image, target, swit.getCall(), swit.getDepth());
                Switch last = imageSwitch;
                while (swit.hasNested()) {
                    swit = swit.getNested();
                    // we are now in another template, do not use locmap
                    target = swit.onFinish();
                    Switch imageNested = new Switch(image, target, swit.getCall(), swit.getDepth());
                    last.setNested(imageNested);
                    last = imageNested;
                }
                imageAttempt.add(imageSwitch);
            }
            image.setAttempt(imageAttempt);
        }
        return result;
    }

    /** Mapping from locations to their records, in terms of target locations. */
    private Map<Location,Record<Location>> recordMap;

    /** Callback factory method for a template. */
    private Template createTemplate(Procedure owner, String name) {
        if (owner != null) {
            return new Template(owner);
        } else {
            return new Template(name);
        }
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
    private static class Partition extends LinkedHashSet<Cell> {
        @Override
        public boolean add(Cell cell) {
            boolean result = super.add(cell);
            if (result) {
                for (Location loc : cell) {
                    this.cellMap.put(loc, cell);
                }
            }
            return result;
        }

        Cell getCell(Location loc) {
            return loc == null ? null : this.cellMap.get(loc);
        }

        private final Map<Location,Cell> cellMap = new TreeMap<Location,TemplateBuilder.Cell>();
    }

    /**
     * Convenience type to collect the targets of the verdicts and call switches
     * of a given location.
     * @param <L> type of the targets
     */
    private static class Record<L> extends Pair<Duo<L>,Map<Call,Set<L>>> {
        Record(L success, L failure, Map<Call,Set<L>> transMap, Position.Type type) {
            super(Duo.newDuo(success, failure), transMap);
            this.type = type;
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

        @SuppressWarnings("rawtypes")
        @Override
        public boolean equals(Object obj) {
            if (!super.equals(obj)) {
                return false;
            }
            return this.type == ((Record) obj).type;
        }

        @Override
        public int hashCode() {
            int prime = 31;
            return prime * super.hashCode() + this.type.hashCode();
        }
    }
}
