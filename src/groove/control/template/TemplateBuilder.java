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
import groove.control.CallStack;
import groove.control.CtrlVarSet;
import groove.control.Position;
import groove.control.Position.Type;
import groove.control.Procedure;
import groove.control.term.Derivation;
import groove.control.term.DerivationAttempt;
import groove.control.term.Term;
import groove.util.Duo;
import groove.util.Pair;
import groove.util.Quad;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
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
     * Construct an automata template for a given program.
     * As a side effect, all procedure templates are also constructed.
     */
    public Template build(Program prog) {
        for (Procedure proc : prog.getProcs().values()) {
            build(proc.getTerm(), getTemplate(proc));
        }
        return build(prog.getName(), prog.getMain());
    }

    /**
     * Constructs an automata template for a given term.
     * As a side effect, all templates of procedures
     * recursively called from the term are also constructed.
     */
    public Template build(String name, Term init) throws IllegalStateException {
        Template result = new Template(name);
        build(init, result);
        result = normalise(result);
        for (Map.Entry<Procedure,Template> e : this.templateMap.entrySet()) {
            e.getKey().setTemplate(e.getValue());
        }
        clearBuildData();
        return result;
    }

    /**
     * Constructs a template from a term.
     * @param result the template to be built
     * @param init the term for which the template should be built
     * @throws IllegalStateException if {@code init} contains procedure
     * calls with uninitialised templates
     */
    private void build(Term init, Template result) throws IllegalStateException {
        assert init.getTransience() == 0 : "Can't build template from transient term";
        // initialise the auxiliary data structures
        Map<Pair<Term,CtrlVarSet>,Location> locMap = getLocMap(result);
        Deque<Pair<Term,CtrlVarSet>> fresh = getFresh(result);
        // set the initial location
        Pair<Term,CtrlVarSet> initKey = Pair.newPair(init, new CtrlVarSet());
        locMap.put(initKey, result.getStart());
        result.getStart().setType(init.getType());
        fresh.add(initKey);
        // do the following as long as there are fresh locations
        while (!fresh.isEmpty()) {
            Pair<Term,CtrlVarSet> next = fresh.poll();
            if (!next.one().isTrial()) {
                continue;
            }
            Location source = locMap.get(next);
            DerivationAttempt nextAttempt = next.one().getAttempt();
            Location succTarget = addLocation(result, nextAttempt.onSuccess(), null);
            Location failTarget = addLocation(result, nextAttempt.onFailure(), null);
            SwitchAttempt locAttempt = new SwitchAttempt(source, succTarget, failTarget);
            for (Derivation deriv : nextAttempt) {
                // build the (possibly nested) switch
                locAttempt.add(addSwitch(result, deriv));
            }
            source.setAttempt(locAttempt);
        }
    }

    /**
     * Adds a control location corresponding to a given symbolic term to the
     * template and auxiliary data structures, if it does not yet exist.
     * @param template the template to which the location should be added
     * @param term the term to be added
     * @param incoming incoming control call leading to the location to be created;
     * may be {@code null}
     * @return the fresh or pre-existing control location
     */
    private Location addLocation(Template template, Term term, Call incoming) {
        Map<Pair<Term,CtrlVarSet>,Location> locMap = getLocMap(template);
        CtrlVarSet vars = new CtrlVarSet();
        if (incoming != null) {
            vars.addAll(incoming.getOutVars().keySet());
        }
        Pair<Term,CtrlVarSet> key = Pair.newPair(term, vars);
        Location result = locMap.get(key);
        if (result == null) {
            getFresh(template).add(key);
            result = template.addLocation(term.getTransience());
            result.setVars(vars);
            result.setType(term.getType());
            locMap.put(key, result);
        }
        return result;
    }

    /**
     * Returns the mapping from terms to locations for a given template.
     */
    private Map<Pair<Term,CtrlVarSet>,Location> getLocMap(Template template) {
        Map<Pair<Term,CtrlVarSet>,Location> result = this.locMapMap.get(template);
        if (result == null) {
            this.locMapMap.put(template, result = new HashMap<Pair<Term,CtrlVarSet>,Location>());
        }
        return result;
    }

    /** For each template, a mapping from terms to locations. */
    private final Map<Template,Map<Pair<Term,CtrlVarSet>,Location>> locMapMap =
        new HashMap<Template,Map<Pair<Term,CtrlVarSet>,Location>>();

    /**
     * Adds a switch corresponding to a given derivation to the
     * template and auxiliary data structures, if it does not yet exist.
     * @param template the template to which the location should be added
     * @param deriv the derivation to be added
     * @return the fresh or pre-existing control switch
     * @throws IllegalStateException if {@code deriv} has a nested derivation
     * but the procedure does not have an initialised template
     */
    private SwitchStack addSwitch(Template template, Derivation deriv) throws IllegalStateException {
        Map<Derivation,SwitchStack> switchMap = getSwitchMap(template);
        SwitchStack result = switchMap.get(deriv);
        if (result == null) {
            result = new SwitchStack();
            Location target = addLocation(template, deriv.onFinish(), deriv.getCall());
            result.add(new Switch(deriv.getCall(), deriv.getTransience(), target));
            if (deriv.hasNested()) {
                Procedure caller = (Procedure) deriv.getCall().getUnit();
                SwitchStack nested = addSwitch(getTemplate(caller), deriv.getNested());
                result.addAll(nested);
            }
            switchMap.put(deriv, result);
        }
        return result;
    }

    /**
     * Returns the mapping from derivations to switches for a given template.
     */
    private Map<Derivation,SwitchStack> getSwitchMap(Template template) {
        Map<Derivation,SwitchStack> result = this.switchMapMap.get(template);
        if (result == null) {
            this.switchMapMap.put(template, result = new HashMap<Derivation,SwitchStack>());
        }
        return result;
    }

    /** For each template, a mapping from derivations to switches. */
    private final Map<Template,Map<Derivation,SwitchStack>> switchMapMap =
        new HashMap<Template,Map<Derivation,SwitchStack>>();

    /**
     * Returns the mapping from terms to locations for a given template.
     */
    private Deque<Pair<Term,CtrlVarSet>> getFresh(Template template) {
        Deque<Pair<Term,CtrlVarSet>> result = this.freshMap.get(template);
        if (result == null) {
            this.freshMap.put(template, result = new LinkedList<Pair<Term,CtrlVarSet>>());
        }
        return result;
    }

    /** Unexplored set of symbolic locations per template. */
    private final Map<Template,Deque<Pair<Term,CtrlVarSet>>> freshMap =
        new HashMap<Template,Deque<Pair<Term,CtrlVarSet>>>();

    /** Returns the template being built for a given procedure. */
    private Template getTemplate(Procedure proc) {
        Template result = this.templateMap.get(proc);
        if (result == null) {
            result = proc.getTemplate();
            if (result == null) {
                result = new Template(proc);
            }
            this.templateMap.put(proc, result);
        }
        return result;
    }

    /** Map from procedures to corresponding templates. */
    private final Map<Procedure,Template> templateMap = new HashMap<Procedure,Template>();

    /** Clears the auxiliary data structures. */
    private void clearBuildData() {
        this.locMapMap.clear();
        this.switchMapMap.clear();
        this.freshMap.clear();
        this.templateMap.clear();
        this.recordMap.clear();
    }

    /**
     * Computes and returns a normalised version of a given template.
     * Normalisation implies minimisation w.r.t. bisimilarity.
     */
    private Template normalise(Template orig) {
        assert orig.getStart().getTransience() == 0;
        Partition partition = computePartition(orig);
        Map<Template,Template> result = computeQuotient(partition);
        for (Map.Entry<Procedure,Template> e : this.templateMap.entrySet()) {
            e.setValue(result.get(e.getValue()));
        }
        return result.get(orig);
    }

    /** Computes a location partition for a given template,
     * as well as all templates in the template map. */
    private Partition computePartition(Template template) {
        for (Location loc : template.getLocations()) {
            this.recordMap.put(loc, computeRecord(loc));
        }
        for (Template procTemplate : this.templateMap.values()) {
            for (Location loc : procTemplate.getLocations()) {
                this.recordMap.put(loc, computeRecord(loc));
            }
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
     * Creates an initial partition for the locations in {@link #recordMap}
     * with distinguished cells for the initial location and all locations
     * of a given transient depth.
     */
    private Partition initPartition() {
        Partition result = new Partition();
        Map<LocationKey,Cell> cellMap = new HashMap<LocationKey,Cell>();
        for (Location loc : this.recordMap.keySet()) {
            LocationKey key = new LocationKey(loc);
            Cell cell = cellMap.get(key);
            if (cell == null) {
                cellMap.put(key, cell = new Cell(loc.getTemplate()));
            }
            cell.add(loc);
        }
        result.addAll(cellMap.values());
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
                    split.put(rec, locCell = new Cell(loc.getTemplate()));
                }
                locCell.add(loc);
            }
            result.addAll(split.values());
        }
        return result;
    }

    /** Computes the record of the choice and call switches for a given location. */
    private Record<Location> computeRecord(Location loc) {
        CallMap<Location> callMap = new CallMap<Location>();
        Location onSuccess = null;
        Location onFailure = null;
        if (loc.isTrial()) {
            SwitchAttempt attempt = loc.getAttempt();
            for (SwitchStack swit : attempt) {
                CallStack call = swit.getCallStack();
                Set<Stack<Location>> targets = callMap.get(call);
                if (targets == null) {
                    callMap.put(call, targets = new HashSet<Stack<Location>>());
                }
                Stack<Location> targetStack = new Stack<Location>();
                for (Switch sub : swit) {
                    targetStack.add(sub.onFinish());
                }
                targets.add(targetStack);
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
        CallMap<Cell> map = new CallMap<Cell>();
        for (Map.Entry<CallStack,Set<Stack<Location>>> e : record.getMap().entrySet()) {
            Set<Stack<Cell>> target = new HashSet<Stack<Cell>>();
            for (Stack<Location> locStack : e.getValue()) {
                Stack<Cell> cellStack = new Stack<Cell>();
                for (Location loc : locStack) {
                    cellStack.add(part.getCell(loc));
                }
                target.add(cellStack);
            }
            map.put(e.getKey(), target);
        }
        return new Record<Cell>(success, failure, map, record.getType());
    }

    /** Computes the quotient of a given template from a given partition. */
    private Map<Template,Template> computeQuotient(Partition partition) {
        Map<Template,Template> result = new HashMap<Template,Template>();
        // set of representative source locations
        Set<Location> reprSet = new HashSet<Location>();
        // map from all source locations to the result locations
        Map<Location,Location> locMap = new HashMap<Location,Location>();
        for (Cell cell : partition) {
            Template source = cell.getTemplate();
            Template target = getTemplate(result, source);
            // representative location of the cell
            Location repr;
            Location image;
            if (cell.contains(source.getStart())) {
                repr = source.getStart();
                image = target.getStart();
            } else {
                repr = cell.iterator().next();
                image = target.addLocation(repr.getTransience());
            }
            image.setType(repr.getType());
            reprSet.add(repr);
            for (Location loc : cell) {
                locMap.put(loc, image);
            }
        }
        // create canonical switch images
        Map<Switch,Switch> switchMap = new HashMap<Switch,Switch>();
        for (Location repr : reprSet) {
            if (!repr.isTrial()) {
                continue;
            }
            for (SwitchStack stack : repr.getAttempt()) {
                Switch swit = stack.getBottom();
                Location target = locMap.get(swit.onFinish());
                target.setVars(swit.getCall().getOutVars().keySet());
                Switch imageSwitch = new Switch(swit.getCall(), swit.getTransience(), target);
                switchMap.put(swit, imageSwitch);
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
            for (SwitchStack reprStack : reprAttempt) {
                SwitchStack imageStack = new SwitchStack();
                for (Switch swit : reprStack) {
                    imageStack.add(switchMap.get(swit));
                }
                imageAttempt.add(imageStack);
            }
            image.setAttempt(imageAttempt);
        }
        return result;
    }

    /** Mapping from locations to their records, in terms of target locations. */
    private final Map<Location,Record<Location>> recordMap =
        new HashMap<Location,Record<Location>>();

    private Template getTemplate(Map<Template,Template> map, Template key) {
        Template result = map.get(key);
        if (result == null) {
            result = createTemplate(key.getOwner(), key.getName());
            map.put(key, result);
        }
        return result;
    }

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

    private static final TemplateBuilder INSTANCE = new TemplateBuilder();

    /**
     * Type serving to distinguish locations in the initial partition.
     * The distinction is made on the basis of template, final status,
     * transient depth and sets of control variables.
     */
    private static class LocationKey extends Quad<Template,Boolean,Integer,CtrlVarSet> {
        LocationKey(Location loc) {
            super(loc.getTemplate(), loc.isFinal(), loc.getTransience(), new CtrlVarSet(
                loc.getVars()));
        }
    }

    /** Local type for a cell of a partition of locations. */
    private static class Cell extends TreeSet<Location> {
        Cell(Template template) {
            this.template = template;
        }

        Template getTemplate() {
            return this.template;
        }

        private final Template template;
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
    private static class Record<L> extends Pair<Duo<L>,CallMap<L>> {
        Record(L success, L failure, CallMap<L> transMap, Position.Type type) {
            super(Duo.newDuo(success, failure), transMap);
            this.type = type;
        }

        L getSuccess() {
            return one().one();
        }

        L getFailure() {
            return one().two();
        }

        CallMap<L> getMap() {
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

    /** Convenience type for the mapping of calls to sets of possible targets. */
    private static class CallMap<L> extends LinkedHashMap<CallStack,Set<Stack<L>>> {
        // empty
    }
}
