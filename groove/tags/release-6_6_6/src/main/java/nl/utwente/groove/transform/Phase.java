/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2023 University of Twente
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
package nl.utwente.groove.transform;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.instance.Frame;
import nl.utwente.groove.grammar.host.HostGraph;
import nl.utwente.groove.lts.MatchResult;

/**
 * Class encoding a phase during the transformation of a graph according
 * to a control program. The phase essentially combines a graph and a
 * control frame, and has functionality for finding rule matches.
 * @author Arend Rensink
 * @version $Revision$
 */
@NonNullByDefault
public interface Phase {
    /** Returns the transformation record for this phase. */
    public Record getRecord();

    /** Returns the graph at this phase. */
    public HostGraph getGraph();

    /**
     * Sets a new actual control frame for this phase.
     * This also initialises the prime frame, if that has not been done yet.
     * If the prime frame has been initialised, it should equal the prime of
     * the new actual frame.
     */
    public void setFrame(Frame frame);

    /**
     * Returns the prime control frame associated with this phase.
     * The prime frame is the frame with which the phase is initialised;
     * it is fixed for the lifetime of the phase.
     * This is in contrast with the actual frame, which may change as
     * the phase is explored.
     */
    public Frame getPrimeFrame();

    /**
     * Returns the actual control frame associated with this phase.
     * The actual control frame evolves as the phase is explored, whereas the
     * prime frame is fixed at creation time.
     * The prime frame is always the prime of the actual frame.
     * @see Frame#getPrime()
     */
    public Frame getActualFrame();

    /**
     * Returns a stack of values for the bound variables of
     * the prime control frame.
     * @see #getPrimeFrame()
     * @see Frame#getVars()
     */
    public Object[] getPrimeValues();

    /**
     * Returns a stack of values for the bound variables of
     * the actual control frame.
     * @see #getActualFrame()
     * @see Frame#getVars()
     */
    public Object[] getActualValues();

    /**
     * Returns the first unexplored match found for this phase, insofar one can
     * currently be computed.
     */
    public @Nullable MatchResult getMatch();

    /**
     * Returns the set of all unexplored matches for this phase, insofar they can
     * currently be computed.
     */
    public List<MatchResult> getMatches();
}
