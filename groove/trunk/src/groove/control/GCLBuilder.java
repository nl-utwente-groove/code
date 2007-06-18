// $ANTLR 2.7.6 (2005-12-22): "gcl.g" -> "GCLBuilder.java"$

package groove.control;

import antlr.TreeParser;
import antlr.Token;
import antlr.collections.AST;
import antlr.RecognitionException;
import antlr.ANTLRException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.collections.impl.BitSet;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;


public class GCLBuilder extends antlr.TreeParser       implements GCLBuilderTokenTypes
 {

	private ControlAutomaton aut;
	
	public GCLBuilder(ControlAutomaton ca) {
		this.aut = ca;
	}
public GCLBuilder() {
	tokenNames = _tokenNames;
}

	public final void program(AST _t) throws RecognitionException {
		
		AST program_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		ControlState[] states;
		
		try {      // for error handling
			AST __t13 = _t;
			AST tmp1_AST_in = (AST)_t;
			match(_t,PROGRAM);
			_t = _t.getFirstChild();
			states=expression(_t);
			_t = _retTree;
			_t = __t13;
			_t = _t.getNextSibling();
			
					this.aut.setStartState(states[0]);
					this.aut.addFinalState(states[1]);
				
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final ControlState[]  expression(AST _t) throws RecognitionException {
		ControlState[] states;
		
		AST expression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST rule = null;
		states = new ControlState[2]; ControlState[] first; ControlState[] second;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case SEMICOLON:
			{
				AST __t15 = _t;
				AST tmp2_AST_in = (AST)_t;
				match(_t,SEMICOLON);
				_t = _t.getFirstChild();
				first=expression(_t);
				_t = _retTree;
				second=expression(_t);
				_t = _retTree;
				_t = __t15;
				_t = _t.getNextSibling();
				
						states[0] = first[0];
						states[1] = second[1];
						aut.addLambdaTransition(first[1],second[0]);
					
				break;
			}
			case OR:
			{
				AST __t16 = _t;
				AST tmp3_AST_in = (AST)_t;
				match(_t,OR);
				_t = _t.getFirstChild();
				first=expression(_t);
				_t = _retTree;
				second=expression(_t);
				_t = _retTree;
				_t = __t16;
				_t = _t.getNextSibling();
				
						states = first;
						aut.addLambdaTransition(states[0],second[0]);
						aut.addLambdaTransition(second[1],states[1]);		
					
				break;
			}
			case ALAP:
			{
				AST __t17 = _t;
				AST tmp4_AST_in = (AST)_t;
				match(_t,ALAP);
				_t = _t.getFirstChild();
				first=expression(_t);
				_t = _retTree;
				_t = __t17;
				_t = _t.getNextSibling();
				
						states[0] = first[0];
						states[1] = aut.newState();
						aut.addElseTransition(states[0],states[1]);
						aut.addLambdaTransition(first[1], first[0]);
					
				break;
			}
			case DO:
			{
				AST __t18 = _t;
				AST tmp5_AST_in = (AST)_t;
				match(_t,DO);
				_t = _t.getFirstChild();
				first=expression(_t);
				_t = _retTree;
				second=expression(_t);
				_t = _retTree;
				_t = __t18;
				_t = _t.getNextSibling();
				
						states = second;
						aut.addElseTransition(states[0], first[0]);
						aut.addLambdaTransition(first[1], states[0]);
					
				break;
			}
			case IDENTIFIER:
			{
				rule = (AST)_t;
				match(_t,IDENTIFIER);
				_t = _t.getNextSibling();
				
						states[0] = aut.newState();
						states[1] = aut.newState();
						aut.addRuleTransition(states[0],states[1], rule.toString() );
					
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return states;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"alap\"",
		"\"until\"",
		"\"try\"",
		"\"else\"",
		"\"do\"",
		"PROGRAM",
		"LCURLY",
		"RCURLY",
		"SEMICOLON",
		"OR",
		"IDENTIFIER",
		"LPAREN",
		"RPAREN"
	};
	
	}
	
