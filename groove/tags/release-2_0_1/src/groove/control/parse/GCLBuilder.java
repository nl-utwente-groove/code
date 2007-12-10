// $ANTLR 2.7.6 (2005-12-22): "builder.g" -> "GCLBuilder.java"$

package groove.control.parse;
import groove.control.*;

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

	AutomatonBuilder builder;
    
    public void setBuilder(AutomatonBuilder ab) {
    	this.builder = ab;
    }
    
public GCLBuilder() {
	tokenNames = _tokenNames;
}

	public final ControlShape  program(AST _t) throws RecognitionException {
		ControlShape shape=null;
		
		AST program_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		ControlState start; ControlState end;
		
		try {      // for error handling
			AST __t82 = _t;
			AST tmp1_AST_in = (AST)_t;
			match(_t,PROGRAM);
			_t = _t.getFirstChild();
			builder.startProgram(); shape = builder.currentShape(); start = builder.getStart(); end = builder.getEnd();
			{
			proc(_t);
			_t = _retTree;
			}
			builder.endProgram();
			_t = __t82;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return shape;
	}
	
	public final void proc(AST _t) throws RecognitionException {
		
		AST proc_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST p = null;
		AST o = null;
		
		try {      // for error handling
			AST __t85 = _t;
			p = _t==ASTNULL ? null :(AST)_t;
			match(_t,PROC);
			_t = _t.getFirstChild();
			o = (AST)_t;
			match(_t,IDENTIFIER);
			_t = _t.getNextSibling();
			builder.openScope(o.getText());
			block(_t);
			_t = _retTree;
			_t = __t85;
			_t = _t.getNextSibling();
			builder.closeScope();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void block(AST _t) throws RecognitionException {
		
		AST block_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		ControlState start = builder.getStart(); ControlState end = builder.getEnd();
		
		try {      // for error handling
			AST __t87 = _t;
			AST tmp2_AST_in = (AST)_t;
			match(_t,BLOCK);
			_t = _t.getFirstChild();
			ControlState newState = builder.newState(); builder.restore(start, newState);
			{
			_loop89:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_0.member(_t.getType()))) {
					statement(_t);
					_t = _retTree;
					builder.restore(newState, newState = builder.newState());
				}
				else {
					break _loop89;
				}
				
			} while (true);
			}
			_t = __t87;
			_t = _t.getNextSibling();
			builder.rmState(newState); builder.restore(builder.getStart(), end); builder.merge();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void statement(AST _t) throws RecognitionException {
		
		AST statement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST b = null;
		AST e = null;
		ControlState start = builder.getStart(); ControlState end = builder.getEnd(); ControlState newState;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ALAP:
			{
				AST __t91 = _t;
				AST tmp3_AST_in = (AST)_t;
				match(_t,ALAP);
				_t = _t.getFirstChild();
				builder.addElse(); newState = builder.newState(); builder.restore(newState, start); builder.addLambda(); builder.restore(start, newState);
				block(_t);
				_t = _retTree;
				_t = __t91;
				_t = _t.getNextSibling();
				break;
			}
			case WHILE:
			{
				AST __t92 = _t;
				AST tmp4_AST_in = (AST)_t;
				match(_t,WHILE);
				_t = _t.getFirstChild();
				builder.addElse(); newState = builder.newState(); builder.restore(start, newState);
				condition(_t);
				_t = _retTree;
				builder.restore(newState, start);
				block(_t);
				_t = _retTree;
				_t = __t92;
				_t = _t.getNextSibling();
				break;
			}
			case DO:
			{
				AST __t93 = _t;
				AST tmp5_AST_in = (AST)_t;
				match(_t,DO);
				_t = _t.getFirstChild();
				newState = builder.newState(); builder.restore(newState, end); builder.addElse(); builder.restore(start, newState);
				block(_t);
				_t = _retTree;
				builder.restore(newState, start);
				condition(_t);
				_t = _retTree;
				_t = __t93;
				_t = _t.getNextSibling();
				break;
			}
			case TRY:
			{
				AST __t94 = _t;
				AST tmp6_AST_in = (AST)_t;
				match(_t,TRY);
				_t = _t.getFirstChild();
				newState = builder.newState(); builder.restore(start, newState); builder.addElse(); builder.restore(start, end);
				block(_t);
				_t = _retTree;
				builder.restore(newState, end);
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case BLOCK:
				{
					b = _t==ASTNULL ? null : (AST)_t;
					block(_t);
					_t = _retTree;
					if( b == null) builder.merge();
					break;
				}
				case 3:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				_t = __t94;
				_t = _t.getNextSibling();
				break;
			}
			case IF:
			{
				AST __t96 = _t;
				AST tmp7_AST_in = (AST)_t;
				match(_t,IF);
				_t = _t.getFirstChild();
				newState = builder.newState(); builder.restore(start, newState);
				condition(_t);
				_t = _retTree;
				builder.restore(newState, end);
				block(_t);
				_t = _retTree;
				newState = builder.newState(); builder.restore(start, newState); builder.addElse(); builder.restore(newState,end);
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case BLOCK:
				{
					e = _t==ASTNULL ? null : (AST)_t;
					block(_t);
					_t = _retTree;
					if( e == null) builder.merge();
					break;
				}
				case 3:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				_t = __t96;
				_t = _t.getNextSibling();
				break;
			}
			case CHOICE:
			{
				AST __t98 = _t;
				AST tmp8_AST_in = (AST)_t;
				match(_t,CHOICE);
				_t = _t.getFirstChild();
				{
				int _cnt100=0;
				_loop100:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==BLOCK)) {
						newState = builder.newState(); builder.restore(start, newState); builder.addLambda(); builder.restore(newState, end);
						block(_t);
						_t = _retTree;
					}
					else {
						if ( _cnt100>=1 ) { break _loop100; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt100++;
				} while (true);
				}
				_t = __t98;
				_t = _t.getNextSibling();
				break;
			}
			case OR:
			case PROCUSE:
			case TRUE:
			case IDENTIFIER:
			case PLUS:
			case STAR:
			case SHARP:
			{
				expression(_t);
				_t = _retTree;
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
	}
	
	public final void condition(AST _t) throws RecognitionException {
		
		AST condition_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			expression(_t);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void expression(AST _t) throws RecognitionException {
		
		AST expression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST e = null;
		AST i = null;
		ControlState start = builder.getStart(); ControlState end = builder.getEnd(); ControlState newState;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case OR:
			{
				AST __t102 = _t;
				AST tmp9_AST_in = (AST)_t;
				match(_t,OR);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				builder.restore(start, end);
				expression(_t);
				_t = _retTree;
				_t = __t102;
				_t = _t.getNextSibling();
				break;
			}
			case PLUS:
			{
				AST __t103 = _t;
				AST tmp10_AST_in = (AST)_t;
				match(_t,PLUS);
				_t = _t.getFirstChild();
				e = _t==ASTNULL ? null : (AST)_t;
				expression(_t);
				_t = _retTree;
				builder.restore(end,end); expression(e);
				_t = __t103;
				_t = _t.getNextSibling();
				break;
			}
			case STAR:
			{
				AST __t104 = _t;
				AST tmp11_AST_in = (AST)_t;
				match(_t,STAR);
				_t = _t.getFirstChild();
				builder.merge() ;
				expression(_t);
				_t = _retTree;
				_t = __t104;
				_t = _t.getNextSibling();
				break;
			}
			case SHARP:
			{
				AST __t105 = _t;
				AST tmp12_AST_in = (AST)_t;
				match(_t,SHARP);
				_t = _t.getFirstChild();
				builder.addElse(); builder.restore(start, start);
				expression(_t);
				_t = _retTree;
				_t = __t105;
				_t = _t.getNextSibling();
				break;
			}
			case PROCUSE:
			{
				AST __t106 = _t;
				AST tmp13_AST_in = (AST)_t;
				match(_t,PROCUSE);
				_t = _t.getFirstChild();
				i = (AST)_t;
				match(_t,IDENTIFIER);
				_t = _t.getNextSibling();
				_t = __t106;
				_t = _t.getNextSibling();
				proc(builder.getProc(i.getText()));
				break;
			}
			case TRUE:
			{
				AST tmp14_AST_in = (AST)_t;
				match(_t,TRUE);
				_t = _t.getNextSibling();
				builder.addLambda();
				break;
			}
			case IDENTIFIER:
			{
				rule(_t);
				_t = _retTree;
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
	}
	
	public final void rule(AST _t) throws RecognitionException {
		
		AST rule_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST i = null;
		
		try {      // for error handling
			i = (AST)_t;
			match(_t,IDENTIFIER);
			_t = _t.getNextSibling();
			builder.addTransition(i.getText());
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"alap\"",
		"\"while\"",
		"\"try\"",
		"\"else\"",
		"\"do\"",
		"\"if\"",
		"\"choice\"",
		"\"or\"",
		"\"proc\"",
		"PROCUSE",
		"PROGRAM",
		"BLOCK",
		"\"true\"",
		"LCURLY",
		"RCURLY",
		"IDENTIFIER",
		"LPAREN",
		"RPAREN",
		"SEMICOLON",
		"PLUS",
		"STAR",
		"SHARP",
		"AND",
		"COMMA",
		"DOT",
		"NOT",
		"RSQUARE",
		"DIGIT",
		"LETTER",
		"NEWLINE",
		"WS",
		"SPECIAL"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 59322224L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	}
	
