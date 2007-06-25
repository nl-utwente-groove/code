package groove.control;

import groove.graph.DefaultMorphism;
import groove.graph.Morphism;
import groove.rel.RegExprGraph;
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
import java.util.ArrayList;
import java.util.List;

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
	
	static {
		try
		{
			Morphism m_l = new DefaultMorphism(new RegExprGraph(), new RegExprGraph());
			Morphism m_e = new DefaultMorphism(new RegExprGraph(), new RegExprGraph());
			LAMBDA_RULE = new SPORule(m_l, new RuleNameLabel(Control.LAMBDA_LABEL),ANY_RULE_PRORITY, new SystemProperties());
			LAMBDA_RULE.setFixed();
			ELSE_RULE = new SPORule(m_e, new RuleNameLabel(Control.ELSE_LABEL),ELSE_RULE_PRIORITY, new SystemProperties());
			ELSE_RULE.setFixed();
		}
		catch(FormatException e){ /* this exception is most certainly never thrown*/ }
	}
	
	
	private List<String> scope = new ArrayList();
	
	private String controlProgram;
	
	private ControlAutomaton automaton;
	
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
			automaton.clear();
			GCLLexer lexer = new GCLLexer(new StringReader(this.controlProgram));
            GCLParser parser = new GCLParser(lexer);
            parser.program();
            GCLBuilder builder = new GCLBuilder(this.automaton);
            builder.program(parser.getAST());
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
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(controlFile));
			
		
			String line;
			while( ((line = br.readLine()) != null) )
			{
				contents.append(line);
			}
			
			//ca.setProgram(contents.toString());

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		this.controlProgram = contents.toString();

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
		
		for( RuleNameLabel label : grammar.getRuleMap().keySet() )
		{
			this.scope.add(label.text());
		}
		automaton = new ControlAutomaton(this.scope);
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
	
	public ControlAutomaton toAutomaton(GraphGrammar grammar) throws FormatException
	{
		List<String> errors = new ArrayList<String>();
		for( ControlTransition transition : automaton.edgeSet() )
		{
			Rule rule = grammar.getRule(transition.ruleName());
			if( rule != null ) {
				transition.setRule(rule);
				transition.source().add(transition);
			}
			else
				errors.add("Format error in control program: unable to find rule for label \"" +  transition.ruleName() + "\"");
			
		}
		
		if( errors.size() > 0 )
			throw new FormatException(errors);
		
		return automaton;
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
