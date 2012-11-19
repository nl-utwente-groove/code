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
package groove.gui.jgraph;

import groove.gui.look.Look;
import groove.gui.look.VisualKey;
import groove.gui.look.VisualKey.Nature;
import groove.gui.look.VisualMap;
import groove.gui.look.VisualValue;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;

/**
 * Abstract GraphJCell implementation, providing some of the basic functionality.
 * @author Arend Rensink
 * @version $Revision $
 */
public abstract class AbstractJCell extends DefaultGraphCell implements
        GraphJCell {
    /** 
     * Constructs a new, uninitialised cell.
     * Call {@link #setJModel(GraphJModel)} to initialise to a given model.
     */
    protected AbstractJCell() {
        // empty
    }

    /** Constructs a cell for a given GraphJModel. */
    protected AbstractJCell(GraphJModel<?,?> jModel) {
        setJModel(jModel);
    }

    @Override
    public GraphJGraph getJGraph() {
        return getJModel().getJGraph();
    }

    /** Sets a new JModel for this cell. */
    public void setJModel(GraphJModel<?,?> jModel) {
        this.jModel = jModel;
        initialise();
    }

    @Override
    public GraphJModel<?,?> getJModel() {
        return this.jModel;
    }

    /** The fixed jModel to which this jVertex belongs. */
    private GraphJModel<?,?> jModel;

    /** Sets or resets all auxiliary data structures to their initial values. */
    protected void initialise() {
        if (this.looks != null) {
            this.looks = EnumSet.copyOf(this.looks);
        }
        VisualMap oldVisuals = this.visuals;
        this.visuals = new VisualMap();
        if (oldVisuals != null) {
            this.visuals.putAll(oldVisuals);
        }
        this.looksChanged = true;
        this.staleKeys = EnumSet.noneOf(VisualKey.class);
        this.staleKeys.addAll(Arrays.asList(VisualKey.refreshables()));
        this.errors = null;
    }

    /** Sets or resets a look value. */
    public boolean setLook(Look look, boolean set) {
        assert !look.isStructural();
        boolean change = set ? getLooks().add(look) : getLooks().remove(look);
        if (change) {
            this.looksChanged = true;
        }
        return change;
    }

    @Override
    final public Set<Look> getLooks() {
        if (this.looks == null) {
            this.looks = EnumSet.noneOf(Look.class);
            Look structuralLook = getStructuralLook();
            assert structuralLook.isStructural();
            this.looks.add(structuralLook);
            this.looksChanged = true;
        }
        return this.looks;
    }

    /** The looks of this JCell. */
    private Set<Look> looks;

    /** Flag indicating that {@link #looks} has changed. */
    private boolean looksChanged;

    /** Creator method for the (fixed) structural look of this JCell. */
    protected Look getStructuralLook() {
        return Look.BASIC;
    }

    final public void putVisual(VisualKey key, Object value) {
        assert key.getNature() == Nature.CONTROLLED;
        this.visuals.put(key, value);
    }

    final public void putVisuals(VisualMap map) {
        for (VisualKey key : VisualKey.controlleds()) {
            if (map.containsKey(key)) {
                putVisual(key, map.get(key));
            }
        }
    }

    @Override
    final public VisualMap getVisuals() {
        if (this.looksChanged || this.looks == null) {
            // refresh the derived part of the visual map
            this.visuals.setLooks(getLooks());
            this.looksChanged = false;
        }
        if (!this.staleKeys.isEmpty()) {
            for (VisualKey key : VisualKey.refreshables()) {
                if (this.staleKeys.remove(key)) {
                    VisualValue refresher = getRefresher(key);
                    this.visuals.put(key, refresher.get(this));
                }
            }
        }
        return this.visuals;
    }

    /** The visual aspects of this JVertex. */
    private VisualMap visuals;

    /** Returns the visual refresher for a given (refreshable) key. */
    private final VisualValue getRefresher(VisualKey key) {
        return getJGraph().getVisualValue(key);
    }

    @Override
    public void setStale(VisualKey... keys) {
        for (VisualKey key : keys) {
            assert key.getNature() == Nature.REFRESHABLE;
            this.staleKeys.add(key);
        }
    }

    /** The set of (refreshable) visual keys to be refreshed. */
    private Set<VisualKey> staleKeys;

    @Override
    final public boolean isLayoutable() {
        return this.layoutable;
    }

    @Override
    final public boolean setLayoutable(boolean layedOut) {
        boolean result = layedOut != this.layoutable;
        if (result) {
            this.layoutable = layedOut;
        }
        return result;
    }

    /** Flag indicating that this cell may be touched by a layouter. */
    private boolean layoutable;

    @Override
    final public boolean isGrayedOut() {
        return getLooks().contains(Look.GRAYED_OUT);
    }

    @Override
    final public boolean setGrayedOut(boolean grayedOut) {
        return setLook(Look.GRAYED_OUT, grayedOut);
    }

    @Override
    public boolean hasErrors() {
        boolean result = false;
        if (this.errors != null) {
            result = !this.errors.isEmpty();
        }
        return result;
    }

    @Override
    public AspectJCellErrors getErrors() {
        if (this.errors == null) {
            this.errors = new AspectJCellErrors(this);
        }
        return this.errors;
    }

    /** Object containing this cell's errors, if any. */
    private AspectJCellErrors errors;

    @Override
    public AttributeMap getAttributes() {
        return getVisuals().getAttributes();
    }
}
