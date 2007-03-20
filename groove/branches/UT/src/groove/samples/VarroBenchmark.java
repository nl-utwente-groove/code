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

import groove.calc.DefaultGraphCalculator;
import groove.calc.DefaultGraphResult;
import groove.calc.GraphResult;
import groove.graph.GraphFactory;
import groove.io.GpsGrammar;
import groove.io.UntypedGxl;
import groove.lts.DerivedGraphRuleFactory;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.trans.GraphGrammar;
import groove.util.GenerateProgressMonitor;

import java.io.File;
import java.io.IOException;

public class VarroBenchmark {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			int n = Integer.parseInt(args[0]);
			GraphFactory graphFactory = GraphFactory.newInstance();
			GraphGrammar benchmarkRules = new GpsGrammar(new UntypedGxl(graphFactory), DerivedGraphRuleFactory.getInstance()).unmarshal(new File("sts.gps"));
			DefaultGraphCalculator calculator = new DefaultGraphCalculator(benchmarkRules);
			GenerateProgressMonitor monitor = new GenerateProgressMonitor();
			GTS gts = calculator.getGTS();
			GraphResult result = new DefaultGraphResult(calculator);
			for (int i = 0; i < n-2; i++) {
				result = result.getFirstAfter("newRule");
				System.out.println("newRule "+i);
			}
			result = result.getFirstAfter("mountRule");
			monitor.addUpdate(gts, (GraphState) result.getGraph());
			for (int i = 0; i < n; i++) {
				result = result.getFirstAfter("requestRule");
				System.out.println("requestRule "+i);
			}
			for (int i = 0; i < n; i++) {
				result = result.getFirstAfter("takeRule");
				System.out.println("takeRule "+i);
				result = result.getFirstAfter("releaseRule");
				System.out.println("releaseRule "+i);
				result = result.getFirstAfter("giveRule");
				System.out.println("giveRule "+i);
			}
		} catch (IOException exc) {
			System.err.println("Error: "+ exc.getMessage());
		}
	}

}