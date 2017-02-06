/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
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
package groove.gui.layout;

import groove.gui.jgraph.JGraph;

/**
 * Interface for classes that can layout a <tt>JGraph</tt> in some fashion.
 * @author Arend Rensink
 * @version $Revision$
 */
public interface Layouter {
    /**
     * Factory method for layouters of this class.
     * @param jgraph The underlying jgraph for the layouter
     * @return a new layouter
     */
    public abstract Layouter newInstance(JGraph<?> jgraph);

    /**
     * Returns the name of this layouter.
     */
    public String getName();

    /**
     * Lays out the <tt>jgraph</tt>, optionally taking existing layout
     * information into account. Existing layout information is kept for the
     * jgraph cells marked unmoveable, if the specific layouter is able to do
     * so. After layouting, all cells are marked moveable. If an implementor
     * does layouting in parallel, the <tt>stop()</tt> method should make sure
     * that the layout thread stops, and the <tt>start()</tt> itself shouls
     * call <tt>stop()</tt> before it does anything else.
     */
    public abstract void start();

    /**
     * Returns a version of this layouter that is capable of doing
     * incremental layouts.
     */
    public Layouter getIncremental();
}