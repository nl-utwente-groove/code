/* * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007 * University of Twente *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not * use this file except in compliance with the License. You may obtain a copy of * the License at http://www.apache.org/licenses/LICENSE-2.0 *  * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the * License for the specific language governing permissions and limitations under * the License. *  * $Id: BuchiTransition.java,v 1.3 2008-02-20 07:41:09 kastenberg Exp $ */package groove.verify;import java.util.Set;import rwth.i2.ltl2ba4j.model.IGraphProposition;/** * Class representing transitions of automata modelling temporal formulae. *  * @author Harmen Kastenberg * @version $Revision$ $Date: 2008-02-20 07:41:09 $ */public class BuchiTransition {    private final BuchiLocation targetLocation;    private final Set<IGraphProposition> labels;    public BuchiTransition(BuchiLocation target, Set<IGraphProposition> labels) {        this.targetLocation = target;        this.labels = labels;    }    public BuchiLocation getTargetLocation() {        return this.targetLocation;    }    public Set<IGraphProposition> getLabels() {        return this.labels;    }}