/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2023
 * University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */
package nl.utwente.groove.gui.jgraph;

import java.util.Collection;

import org.eclipse.jdt.annotation.NonNull;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;

import nl.utwente.groove.graph.Graph;
import nl.utwente.groove.gui.look.VisualMap;
import nl.utwente.groove.gui.view.AspectViewCell;
import nl.utwente.groove.gui.view.EditableLabels;
import nl.utwente.groove.gui.view.ViewCell;
import nl.utwente.groove.gui.view.cell.AViewCell;
import nl.utwente.groove.util.AIGenerated;

/**
 * JGraph cell showing a neutral view cell: the backend item of the cell in this
 * backend. Carries the JGraph attribute map derived from the cell's visuals, and
 * redirects JGraph's user object, through which edited values arrive, to the
 * editable labels of an aspect cell.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5.1, 2026-09")
abstract public class JCell<G extends @NonNull Graph> extends DefaultGraphCell {
    /** Creates the JGraph item of a given cell, and registers it with the cell. */
    protected JCell(AViewCell<G> cell) {
        this.cell = cell;
        cell.setItem(this);
    }

    /** Returns the view cell shown by this JGraph cell. */
    public AViewCell<G> getViewCell() {
        return this.cell;
    }

    /** The view cell shown by this JGraph cell; replaced by a clone when this cell is cloned. */
    private AViewCell<G> cell;

    /* Returns the JGraph attributes derived from (and kept in step with) the visuals. */
    @Override
    public AttributeMap getAttributes() {
        var visuals = this.cell.getVisuals();
        var result = this.attributes;
        if (result == null || this.boundVisuals != visuals) {
            this.attributes = result = new VisualAttributeMap(visuals);
            this.boundVisuals = visuals;
        }
        return result;
    }

    /** The attribute map, bound to {@link #boundVisuals}. */
    private VisualAttributeMap attributes;
    /** The visual map the attributes are derived from; the cell's map is replaced on re-initialisation. */
    private VisualMap boundVisuals;

    /*
     * JGraph applies edited cell values through the user object
     * (DefaultGraphModel.valueForCellChanged); redirected to the editable labels
     * of an aspect cell. A string value is the text of the in-place editor.
     */
    @Override
    public void setUserObject(Object value) {
        if (this.cell instanceof AspectViewCell aspectCell) {
            if (value instanceof EditableLabels labels) {
                aspectCell.setEditableLabels(labels);
            } else {
                var labels = new EditableLabels();
                if (value != null) {
                    labels.load(value.toString());
                }
                aspectCell.setEditableLabels(labels);
            }
        } else {
            super.setUserObject(value);
        }
    }

    /* JGraph reads the cell value through the user object; see setUserObject. */
    @Override
    public Object getUserObject() {
        return this.cell instanceof AspectViewCell aspectCell
            ? aspectCell.getEditableLabels()
            : super.getUserObject();
    }

    /* The clone shows a clone of the view cell. */
    @Override
    public Object clone() {
        @SuppressWarnings("unchecked")
        JCell<G> result = (JCell<G>) super.clone();
        result.cell = this.cell.clone();
        result.cell.setItem(result);
        result.attributes = null;
        result.boundVisuals = null;
        return result;
    }

    @Override
    public String toString() {
        return this.cell.toString();
    }

    /** Returns the JGraph item of a given view cell, which must have one. */
    @SuppressWarnings("unchecked")
    public static <G extends @NonNull Graph> JCell<G> of(ViewCell<G> cell) {
        var result = (JCell<G>) ((AViewCell<G>) cell).getItem();
        assert result != null : "Cell " + cell + " is not shown by a JGraph";
        return result;
    }

    /** Returns the JGraph items of a collection of view cells, as an array. */
    public static Object[] items(Collection<? extends ViewCell<?>> cells) {
        Object[] result = new Object[cells.size()];
        int i = 0;
        for (var cell : cells) {
            result[i] = ((AViewCell<?>) cell).getItem();
            assert result[i] != null : "Cell " + cell + " is not shown by a JGraph";
            i++;
        }
        return result;
    }
}
