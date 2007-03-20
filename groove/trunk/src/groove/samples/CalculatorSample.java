// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente
 
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0 
 
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
// either express or implied. See the License for the specific 
// language governing permissions and limitations under the License.
/*
 * Created on 23 Sep 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package groove.samples;

import java.io.IOException;
import java.util.Collection;

import groove.calc.DefaultGraphCalculator;
import groove.calc.DefaultGraphResult;
import groove.calc.GraphCalculator;
import groove.calc.GraphResult;
import groove.trans.GraphGrammar;
import groove.util.Groove;

/**
 * This class is meant to show an example of how the classes
 * {@link GraphCalculator} and {@link GraphResult} can be used.
 * @author Arend Rensink
 * @version $REVISION $
 */
public class CalculatorSample {
	/**
	 * Name of the sample grammar to be used - here "append" from the GROOVE samples.
	 * Note that the context of the invocation has to make sure that 
	 * a grammar with this name can actually be found.
	 */
	public static final String GRAMMAR_NAME = "append";
	/**
	 * Name of the sample rule to be used.
	 */
	public static final String RULE_NAME = "next";

	/**
	 * Main method.
	 * This sample will use the graph given in {@link #GRAMMAR_NAME},
	 * read in the graph named in the first argument, attempt to apply the rule "next",
	 * and store the resulting graphs under the name
	 * given as the second argument, extended with a number distinguishing the results.
	 * @param args file names for the start and target graph
	 */
	public static void main(String[] args) {
		try {
			String startGraphName = args[0];
			String targetGraphName = args[1];
			GraphGrammar sample = Groove.loadGrammar(GRAMMAR_NAME, startGraphName);
			DefaultGraphCalculator calculator = new DefaultGraphCalculator(sample);
			Collection<GraphResult> result = new DefaultGraphResult(calculator).getAllAfter(RULE_NAME);
			int index = 0;
			for (GraphResult element: result) {
				Groove.saveGraph(element.getGraph(), targetGraphName+"-"+index);
				index++;
			}
		} catch (IOException exc) {
			System.err.println("Error: "+ exc.getMessage());
		}
	}
}