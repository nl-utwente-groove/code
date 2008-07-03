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
 * $Id: Namespace.java,v 1.4 2008-02-05 13:27:53 rensink Exp $
 */
package groove.control.parse;

import groove.trans.GraphGrammar;
import groove.trans.RuleNameLabel;
import groove.view.DefaultGrammarView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;

import antlr.SemanticException;

/**
 * Namespace class for the AutomatonBuilder (for checking).
 * Can be used to store names with an optional referenced Object.
 * 
 * @author Tom Staijen
 * @version $Revision $
 */
public class Namespace {

	private Set<String> ruleNames = new HashSet();
	
	private GraphGrammar grammar;
	private DefaultGrammarView view;

	private HashMap<String, CommonTree> procs = new HashMap<String, CommonTree>();

	/**
	 * Stores the AST of a procedure.
	 * @param name
	 * @param ast
	 */
	public void store(String name, CommonTree ast) {
		this.procs.put(name, ast);
	}
	
	/**
	 * Returns the AST for a procedure
	 * @param name
	 * @return AST
	 * @throws SemanticException
	 */
	public CommonTree getProc(String name) throws RecognitionException {
		// TODO: throw exception if no such function
		return procs.get(name);
	}
	
	public boolean ruleExists(String name) throws SemanticException {
		 return ruleNames.contains(name);
	}
	
	public void setRuleNames(DefaultGrammarView grammarView) {
		for( RuleNameLabel rule : grammarView.getRuleMap().keySet() ) {
			this.ruleNames.add(rule.text());
		}
	}
	
}
