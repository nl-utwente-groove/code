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
package nl.utwente.groove.control.template;

import org.eclipse.jdt.annotation.Nullable;

import nl.utwente.groove.control.Binding;
import nl.utwente.groove.control.Binding.Source;
import nl.utwente.groove.grammar.UnitPar;
import nl.utwente.groove.grammar.UnitPar.ProcedurePar;
import nl.utwente.groove.grammar.UnitPar.RulePar;

/**
 * Binding of a formal rule parameter to source location
 * variable or constant value.
 * @param par the unit parameter: depending on the associated call, either a
 * {@link RulePar} or a {@link ProcedurePar}
 * @param bind either a {@link Source#CONST} or {@link Source#VAR} binding
 * for the parameter, where the {@link Source#VAR} refers to the index of the
 * source variable from which the argument is to be obtained
 */
public record ParBinding(UnitPar par, @Nullable Binding bind) {
    // no additional functionality
}