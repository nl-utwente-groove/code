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
package nl.utwente.groove.gui.prolog;

import nl.utwente.groove.annotation.Signature;
import nl.utwente.groove.annotation.ToolTipBody;
import nl.utwente.groove.prolog.GrooveEnvironment;
import nl.utwente.groove.prolog.builtin.GroovePredicates;
import nl.utwente.groove.util.AIGenerated;

/** GROOVE predicates that depend on the GUI.
 * These are only available after {@link #register()} has been called,
 * which the GUI tools do at startup.
 * @author Arend Rensink
 * @version $Revision$
 */
@AIGenerated("Claude Fable 5, 2026-08")
@SuppressWarnings("all")
public class GuiPredicates extends GroovePredicates {
    @Signature({"Graph", "+"})
    @ToolTipBody("Displays the given graph in a new preview dialog.")
    public void show_graph_1() {
        s(Predicate_show_graph.class, 1);
    }

    /** Contributes the GUI-bound predicates to the prolog environment. */
    public static void register() {
        GrooveEnvironment.addPredicates(GuiPredicates.class);
    }
}
