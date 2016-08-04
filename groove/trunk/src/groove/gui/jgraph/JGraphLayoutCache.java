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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewFactory;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

/**
 * A layout cache that, for efficiency, does not pass on all change events,
 * and sets a {@link JCellViewFactory}. It should be possible to use the
 * partiality of the cache to hide elements, but this seems unnecessarily
 * complicated.
 */
public class JGraphLayoutCache extends GraphLayoutCache {
    /** Constructs an instance of the cache. */
    JGraphLayoutCache(CellViewFactory viewFactory) {
        super(null, viewFactory, true);
        setSelectsLocalInsertedCells(false);
        setShowsExistingConnections(false);
        setShowsChangedConnections(false);
        setShowsInsertedConnections(false);
        setHidesExistingConnections(false);
        setHidesDanglingConnections(false);
    }

    /*
     * Make sure all views are correctly inserted
     */
    @Override
    public void setModel(GraphModel model) {
        this.partial = false;
        super.setModel(model);
        this.partial = true;
    }

    @Override
    public boolean isVisible(Object cell) {
        if (cell instanceof JCell) {
            return ((JCell<?>) cell).getVisuals().isVisible();
        } else if (cell instanceof DefaultPort) {
            return isVisible(((DefaultPort) cell).getParent());
        } else {
            return super.isVisible(cell);
        }
    }

    /*
     * Overwritten to reduce refreshing
     */
    @Override
    protected void reloadRoots() {
        // Reorder roots
        Object[] orderedCells = DefaultGraphModel.getAll(this.graphModel);
        List<CellView> newRoots = new ArrayList<>();
        for (Object element : orderedCells) {
            CellView view = getMapping(element, true);
            if (view != null) {
                // the following line is commented out wrt the super implementation
                // to prevent over-enthousiastic refreshing
                // view.refresh(this, this, true);
                if (view.getParentView() == null) {
                    newRoots.add(view);
                }
            }
        }
        this.roots = newRoots;
    }

    /*
     * Overwritten to prevent NPE in case some roots have no view.
     */
    @Override
    public synchronized void reload() {
        List<CellView> newRoots = new ArrayList<>();
        @SuppressWarnings("unchecked")
        Map<?,?> oldMapping = new Hashtable<>(this.mapping);
        this.mapping.clear();
        @SuppressWarnings("unchecked")
        Set<Object> rootsSet = new HashSet<Object>(this.roots);
        for (Map.Entry<?,?> entry : oldMapping.entrySet()) {
            Object cell = entry.getKey();
            CellView oldView = (CellView) entry.getValue();
            CellView newView = getMapping(cell, true);
            // thr following test is the only change wrt the super-implementation
            if (newView != null) {
                newView.changeAttributes(this, oldView.getAttributes());
                // newView.refresh(getModel(), this, false);
                if (rootsSet.contains(oldView)) {
                    newRoots.add(newView);
                }
            }
        }
        // replace hidden
        this.hiddenMapping.clear();
        this.roots = newRoots;
    }

    /* Overwritten to increase visibility. */
    @Override
    public void updatePorts() {
        super.updatePorts();
    }
}