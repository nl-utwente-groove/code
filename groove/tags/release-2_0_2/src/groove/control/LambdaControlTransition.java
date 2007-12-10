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
 * $Id: LambdaControlTransition.java,v 1.5 2007-11-26 08:58:12 fladder Exp $
 */
package groove.control;

/**
 * A controltransition that can always be taken (internal transition, tau-transition).
 * 
 * @author Tom Staijen
 * @version $Revision $
 */
public class LambdaControlTransition extends RuleControlTransition {
	
	/**
	 *  The public constructor calls the super constructor with the default label for lambda transitions.
	 */
	public LambdaControlTransition(ControlState source, ControlState target)
	{
		super(source, target, ControlView.LAMBDA_LABEL);
	}
}
