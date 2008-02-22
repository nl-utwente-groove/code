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
 * $Id: Testing.java,v 1.1 2008-02-22 13:02:47 rensink Exp $
 */
package groove;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import groove.graph.Graph;
import groove.io.AspectualViewGps;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.RuleMatch;
import groove.view.FormatException;

public class Testing {

	public static void main (String args[]) {
		// load the grammar
		String gl = "../samples/dynamic-network.gps";
		AspectualViewGps loader = new AspectualViewGps();
		GraphGrammar grammar = null;
		try {
			grammar = loader.unmarshal(new File(gl), "s1580").toGrammar();
		} catch (FormatException e) {
			System.err.println("Error loading grammar 1");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Error loading grammar 2");
			System.exit(1);
		}
		
		Rule rule = grammar.getRule("three-neighbours");
		Graph startGraph = grammar.getStartGraph();
		Iterator<RuleMatch> matchIter = rule.getMatchIter(startGraph, null);
		HashSet<RuleMatch> matches = new HashSet<RuleMatch>();
		int nbMatch = 0;
		while (matchIter.hasNext()) {
			nbMatch++;
			if (matches.add(matchIter.next())) {
				System.out.println("Match already there !");
			}
		}
		System.out.println("Total : " + nbMatch + " matches");
		
	}
	
}
