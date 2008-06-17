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


///**
// * Class encoding one of the benchmarks in the Varro et al paper. 
// */
//public class VarroBenchmark {
//	/** Call with an int parameter to determine the size of the problem. */
//	public static void main(String[] args) {
//		try {
//			int n = Integer.parseInt(args[0]);
//			GraphGrammar benchmarkRules = new AspectualViewGps().unmarshal(new File("sts.gps")).toGrammar();
//			DefaultGraphCalculator calculator = new DefaultGraphCalculator(benchmarkRules);
//			GenerateProgressMonitor monitor = new GenerateProgressMonitor();
//			GTS gts = calculator.getGTS();
//			GraphState result = new DefaultGraphResult(calculator);
//			for (int i = 0; i < n-2; i++) {
//				result = result.getFirstAfter("newRule");
//				System.out.println("newRule "+i);
//			}
//			result = result.getFirstAfter("mountRule");
//			monitor.addUpdate(gts, (GraphState) result.getGraph());
//			for (int i = 0; i < n; i++) {
//				result = result.getFirstAfter("requestRule");
//				System.out.println("requestRule "+i);
//			}
//			for (int i = 0; i < n; i++) {
//				result = result.getFirstAfter("takeRule");
//				System.out.println("takeRule "+i);
//				result = result.getFirstAfter("releaseRule");
//				System.out.println("releaseRule "+i);
//				result = result.getFirstAfter("giveRule");
//				System.out.println("giveRule "+i);
//			}
//		} catch (Exception exc) {
//			System.err.println("Error: "+ exc.getMessage());
//		}
//	}
//
//}