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
 * $Id: ControlView.java,v 1.6 2007-11-22 15:39:11 fladder Exp $
 */
package groove.control;

import groove.control.parse.AutomatonBuilder;
import groove.control.parse.GCLBuilder;
import groove.control.parse.GCLChecker;
import groove.control.parse.GCLLexer;
import groove.control.parse.GCLParser;
import groove.control.parse.Namespace;
import groove.graph.GraphFactory;
import groove.graph.Morphism;
import groove.trans.GraphGrammar;
import groove.trans.Rule;
import groove.trans.RuleNameLabel;
import groove.trans.SPORule;
import groove.trans.SystemProperties;
import groove.view.AspectualRuleView;
import groove.view.DefaultGrammarView;
import groove.view.FormatException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Staijen
 * Loads a control program into a given ControlAutomaton
 */
public class ControlView {
	
	/** currently used file **/
	private File controlFile;
	
	/** priority for a lamba Rule , that can be applied anytime **/
	public static final int ANY_RULE_PRORITY = Integer.MAX_VALUE-1;
	
	/** priority for an Else Rule **/
	public static final int ELSE_RULE_PRIORITY = -1;
	
	/** the Rule object used for Lambda Transitions **/
	public static Rule LAMBDA_RULE;
	
	/** the RUle object used for Else Transitions **/
	public static Rule ELSE_RULE;
	
    private static GraphFactory factory = GraphFactory.getInstance();
    
	static {
		try
		{
			Morphism m_l = factory.newMorphism(factory.newGraph(), factory.newGraph());
			Morphism m_e = factory.newMorphism(factory.newGraph(), factory.newGraph());
			LAMBDA_RULE = new SPORule(m_l, new RuleNameLabel(ControlView.LAMBDA_LABEL),ANY_RULE_PRORITY, new SystemProperties());
			LAMBDA_RULE.setFixed();
			ELSE_RULE = new SPORule(m_e, new RuleNameLabel(ControlView.ELSE_LABEL),ELSE_RULE_PRIORITY, new SystemProperties());
			ELSE_RULE.setFixed();
		}
		catch(FormatException e){ /* this exception is most certainly never thrown*/ }
	}
	
	
	private Map<String, RuleNameLabel> scope = new HashMap<String, RuleNameLabel>();
	
	private String controlProgram;
	
	private ControlShape programShape;
	
	private ControlAutomaton automaton;

	public static final String LAMBDA_LABEL = "tau";

	public static final String ELSE_LABEL = "else";
	
	/**
	 * @param args
	 */
	public void loadProgram() {

		if( controlProgram == null ) {
			System.err.println("Error in control: no program available");
			return;
		}
		try
        {
			GCLLexer lexer = new GCLLexer(new StringReader(this.controlProgram));
            Namespace namespace = new Namespace();
			GCLParser parser = new GCLParser(lexer);
            parser.program();
            
            AutomatonBuilder b = new AutomatonBuilder();
            
            GCLChecker checker = new GCLChecker();
            checker.setNamespace(b);
            
            checker.program(parser.getAST());
            
            GCLBuilder builder = new GCLBuilder();
            builder.setBuilder(b);
            
            this.programShape = builder.program(parser.getAST());
            this.automaton = new ControlAutomaton(this.programShape);
        }
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("Parse error loading control program: " + e.getMessage());
		}
	}

	/** sets the program **/
	public void setProgram(String program) {
		this.controlProgram = program;
	}
	
	/** loads the program from a File **/
	public void loadFile(File controlFile) throws IOException, FileNotFoundException {
		this.controlFile = controlFile;
		
		StringBuilder contents = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(controlFile));
			String line;
			while( ((line = br.readLine()) != null) )
			{
				contents.append(line);
			}
			//ca.setProgram(contents.toString());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		setProgram(contents.toString());
	}
	
	/**
	 * Initialises the Control given the rulenames in the grammar
	 * Can only be called once and must be called before any other method is used.
	 * @param grammar
	 */
	public void initScope(DefaultGrammarView grammar)
	{
		if( this.scope.size() > 0 )
			throw new RuntimeException("Error: ControlView::initScope can only be called once");
		
		try
		{
			AspectualRuleView lambdaRV = new AspectualRuleView(ControlView.LAMBDA_RULE);
			AspectualRuleView elseRV = new AspectualRuleView(ControlView.ELSE_RULE);
			grammar.addRule(lambdaRV);
			grammar.addRule(elseRV);
		} catch(FormatException e) {
			// will not happen
		}
		
		for( RuleNameLabel rule : grammar.getRuleMap().keySet() ) {
			this.scope.put(rule.text(), rule);
		}
		//automaton = new ControlAutomaton(this.scope.keySet());
	}
	
	public ControlAutomaton getAutomaton() 
	{
		return this.automaton;
	}
	
	public String program() {
		return this.controlProgram;
	}
	
	public File getFile() {
		return this.controlFile;
	}
	
	/**
	 * This method should only be called from DefaultGrammarView.computeGrammar
	 * 
	 * @param grammar
	 * @return
	 * @throws FormatException
	 */
	public ControlAutomaton toAutomaton(GraphGrammar grammar) throws FormatException
	{
//		List<String> errors = new ArrayList<String>();
//		
//		for( ControlTransition transition : automaton.edgeSet() )
//		{
//			Rule rule = grammar.getRule(transition.ruleName());
//			if( rule != null ) {
//				transition.setRule(rule);
//				transition.source().add(transition);
//			}
//			else
//			{
//				// if the rulename is a group, this will return all child rules.
//				Set<Rule> rules = grammar.getChildRules(transition.ruleName());
//				if( !rules.isEmpty() ) {
//					ControlTransition childTrans;
//					for( Rule childRule : rules) {
//						//automaton.removeTransition(transition);
//						childTrans = new ControlTransition(transition.source(), transition.target(), childRule.getName().name());
//						childTrans.setRule(childRule);
//						transition.source().add(childTrans);
//						// this is for viewing purposes only
//						childTrans.setVisibleParent(transition);
//					}
//					// remove the original transition;
//				}
//				else
//					errors.add("Format error in control program: unable to find rule for label \"" +  transition.ruleName() + "\"");
//			}
//		}
//		
//		if( errors.size() > 0 )
//			throw new FormatException(errors);
//		
		return this.getAutomaton();
	}
	
	/**
	 * Saves the program to the given file.
	 * 
	 * @param controlProgram
	 * @param file
	 * @throws IOException
	 */
	public static void saveFile(String controlProgram, File file) throws IOException {
		PrintWriter pw = new PrintWriter(file);
		pw.write(controlProgram);
		pw.close();
	}
	
}
