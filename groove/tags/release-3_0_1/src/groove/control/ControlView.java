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
 * $Id: ControlView.java,v 1.10 2008-03-18 12:17:29 fladder Exp $
 */
package groove.control;

import groove.control.parse.AutomatonBuilder;
import groove.control.parse.Counter;
import groove.control.parse.GCLBuilder;
import groove.control.parse.GCLChecker;
import groove.control.parse.GCLLexer;
import groove.control.parse.GCLParser;
import groove.graph.GraphFactory;
import groove.trans.GraphGrammar;
import groove.view.DefaultGrammarView;
import groove.view.FormatException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

/**
 * 
 * The Control part of the GrammarView. For loading, saving, getting an actual representation, etc.
 * 
 * @author Staijen
 * Loads a control program into a given ControlAutomaton
 */
public class ControlView {
	
	/** currently used file **/
	private File controlFile;

    private static GraphFactory factory = GraphFactory.getInstance();
    
	
	private AutomatonBuilder builder = new AutomatonBuilder();
	
	private String controlProgram;
	
	private ControlShape programShape;
	
	private ControlAutomaton automaton;

	/**
	 * Constructor, needs a gramamrview and a filename to a controlprogram.
	 * 
	 * Afterwards, programShape and automaton should have a value.
	 * 
	 * @param result
	 * @param controlProgramFile
	 * @throws IOException
	 */
	public ControlView(DefaultGrammarView result, File controlProgramFile) throws IOException  {
		this.initNamespace(result);
		this.loadFile(controlProgramFile);

	}
	
	/**
	 * load the program currently in controlProgram
	 */
	private void loadProgram() throws FormatException {
		if( controlProgram == null ) {
			throw new FormatException("Error in control:no program available ");
		}
		if( builder == null ) {
			throw new FormatException("Error in control: trying to parse before the scope is initialized");
		}
		try
        {
			GCLLexer lexer = new GCLLexer(new StringReader(this.controlProgram));
			GCLParser parser = new GCLParser(lexer);
            parser.program();
            
            GCLChecker checker = new GCLChecker();
            checker.setNamespace(this.builder);
            checker.program(parser.getAST());
            
            GCLBuilder gclb = new GCLBuilder();
            gclb.setBuilder(this.builder);

            // reset the counter for unique controlstate numbers to 0
			Counter.reset();
            this.programShape = gclb.program(parser.getAST());

            builder.optimize();


            this.automaton = new ControlAutomaton(this.programShape);
        }
		catch(Exception e)
		{
			throw new FormatException("Error in control: load error =>" + e.getMessage());
		}
	}

	/** sets the program **/
	public void setProgram(String program) {
		this.controlProgram = program;
		this.programShape = null;
		this.automaton = null;
	}
	
	/** loads the program from a File **/
	public void loadFile(File controlFile) throws IOException, FileNotFoundException {
		this.controlFile = controlFile;
		
		//System.out.println("Loading control from file: " + controlFile.getName());
		
		StringBuilder contents = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(controlFile));
			String line;
			while( ((line = br.readLine()) != null) )
			{
				contents.append(line+ "\r\n"); 
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
	public void initNamespace(DefaultGrammarView grammar)
	{
		
		//System.out.println("Initializing Control NameSpace");
		
//		try
//		{
//			AspectualRuleView lambdaRV = new AspectualRuleView(ControlView.LAMBDA_RULE);
//			AspectualRuleView elseRV = new AspectualRuleView(ControlView.ELSE_RULE);
//			grammar.addRule(lambdaRV);
//			grammar.addRule(elseRV);
//			
//		} catch(FormatException e) {
//			// will not happen
//		}
		
		this.builder = new AutomatonBuilder();
		this.builder.setRuleNames(grammar);
	}
	
	/** returns the control automaton */
	public ControlAutomaton getAutomaton() 
	{
		return this.automaton;
	}
	
	/** returns the textual control program */
	public String program() {
		return this.controlProgram;
	}
	
	/** returns the File containing the current control program */
	public File getFile() {
		return this.controlFile;
	}
	
	/**
	 * This method should only be called from DefaultGrammarView.computeGrammar
	 * Create the automaton once, then, use getAutomaton() to get the automaton.
	 * @param grammar
	 * @return ControlAutomaton
	 * @throws FormatException
	 */
	public ControlAutomaton toAutomaton(GraphGrammar grammar) throws FormatException
	{
		this.loadProgram();
		builder.finalize(grammar);
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

	/** label for lambda transitions */
	public static final String LAMBDA_LABEL = "tau";

	/** label for else transitions */
	public static final String ELSE_LABEL = "else";
	
	/** priority for a lamba Rule , that can be applied anytime **/
	public static final int ANY_RULE_PRORITY = Integer.MAX_VALUE-1;
	
	/** priority for an Else Rule **/
	public static final int ELSE_RULE_PRIORITY = -1;
	
//	/** the Rule object used for Lambda Transitions **/
//	public static Rule LAMBDA_RULE;
	
//	/** the RUle object used for Else Transitions **/
//	public static Rule ELSE_RULE;

//	static {
//		try
//		{
//			Morphism m_l = factory.newMorphism(factory.newGraph(), factory.newGraph());
//			Morphism m_e = factory.newMorphism(factory.newGraph(), factory.newGraph());
//			LAMBDA_RULE = new SPORule(m_l, new RuleNameLabel(ControlView.LAMBDA_LABEL),ANY_RULE_PRORITY, new SystemProperties());
//			LAMBDA_RULE.setFixed();
//			ELSE_RULE = new SPORule(m_e, new RuleNameLabel(ControlView.ELSE_LABEL),ELSE_RULE_PRIORITY, new SystemProperties());
//			ELSE_RULE.setFixed();
//		}
//		catch(FormatException e){ /* this exception is most certainly never thrown :) */ }
//	}
	
}
