// $ANTLR 2.7.6 (2005-12-22): "gcl.g" -> "GCLChecker.java"$

package groove.control.parse;
import groove.control.ControlAutomaton;
import antlr.NoViableAltException;
import antlr.RecognitionException;
import antlr.collections.AST;
import antlr.collections.impl.BitSet;


public class GCLChecker extends antlr.TreeParser       implements GCLCheckerTokenTypes
 {

	private ControlAutomaton aut;
	
	public GCLChecker(ControlAutomaton ca) {
		this.aut = ca;
	}
	
    private Namespace namespace;
	public void setNamespace(Namespace namespace) {
		this.namespace = namespace;
	}
public GCLChecker() {
	tokenNames = _tokenNames;
}

	public final void program(AST _t) throws RecognitionException {
		
		AST program_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t23 = _t;
			AST tmp1_AST_in = (AST)_t;
			match(_t,PROGRAM);
			_t = _t.getFirstChild();
			{
			_loop25:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==PROC)) {
					proc(_t);
					_t = _retTree;
				}
				else {
					break _loop25;
				}
				
			} while (true);
			}
			_t = __t23;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void proc(AST _t) throws RecognitionException {
		
		AST proc_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST p = null;
		AST o = null;
		
		try {      // for error handling
			AST __t27 = _t;
			p = _t==ASTNULL ? null :(AST)_t;
			match(_t,PROC);
			_t = _t.getFirstChild();
			o = (AST)_t;
			match(_t,IDENTIFIER);
			_t = _t.getNextSibling();
			block(_t);
			_t = _retTree;
			_t = __t27;
			_t = _t.getNextSibling();
			namespace.store(o.getText(), p);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void block(AST _t) throws RecognitionException {
		
		AST block_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t29 = _t;
			AST tmp2_AST_in = (AST)_t;
			match(_t,BLOCK);
			_t = _t.getFirstChild();
			{
			_loop31:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_0.member(_t.getType()))) {
					statement(_t);
					_t = _retTree;
				}
				else {
					break _loop31;
				}
				
			} while (true);
			}
			_t = __t29;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void statement(AST _t) throws RecognitionException {
		
		AST statement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ALAP:
			{
				AST __t33 = _t;
				AST tmp3_AST_in = (AST)_t;
				match(_t,ALAP);
				_t = _t.getFirstChild();
				block(_t);
				_t = _retTree;
				_t = __t33;
				_t = _t.getNextSibling();
				break;
			}
			case WHILE:
			{
				AST __t34 = _t;
				AST tmp4_AST_in = (AST)_t;
				match(_t,WHILE);
				_t = _t.getFirstChild();
				condition(_t);
				_t = _retTree;
				block(_t);
				_t = _retTree;
				_t = __t34;
				_t = _t.getNextSibling();
				break;
			}
			case DO:
			{
				AST __t35 = _t;
				AST tmp5_AST_in = (AST)_t;
				match(_t,DO);
				_t = _t.getFirstChild();
				block(_t);
				_t = _retTree;
				condition(_t);
				_t = _retTree;
				_t = __t35;
				_t = _t.getNextSibling();
				break;
			}
			case TRY:
			{
				AST __t36 = _t;
				AST tmp6_AST_in = (AST)_t;
				match(_t,TRY);
				_t = _t.getFirstChild();
				block(_t);
				_t = _retTree;
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case BLOCK:
				{
					block(_t);
					_t = _retTree;
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
				_t = __t36;
				_t = _t.getNextSibling();
				break;
			}
			case IF:
			{
				AST __t38 = _t;
				AST tmp7_AST_in = (AST)_t;
				match(_t,IF);
				_t = _t.getFirstChild();
				condition(_t);
				_t = _retTree;
				block(_t);
				_t = _retTree;
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case BLOCK:
				{
					block(_t);
					_t = _retTree;
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
				_t = __t38;
				_t = _t.getNextSibling();
				break;
			}
			case CHOICE:
			{
				AST __t40 = _t;
				AST tmp8_AST_in = (AST)_t;
				match(_t,CHOICE);
				_t = _t.getFirstChild();
				{
				int _cnt42=0;
				_loop42:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==BLOCK)) {
						block(_t);
						_t = _retTree;
					}
					else {
						if ( _cnt42>=1 ) { break _loop42; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt42++;
				} while (true);
				}
				_t = __t40;
				_t = _t.getNextSibling();
				break;
			}
			case OR:
			case PROCUSE:
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
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case OR:
			{
				AST __t50 = _t;
				AST tmp9_AST_in = (AST)_t;
				match(_t,OR);
				_t = _t.getFirstChild();
				condition(_t);
				_t = _retTree;
				condition(_t);
				_t = _retTree;
				_t = __t50;
				_t = _t.getNextSibling();
				break;
			}
			case IDENTIFIER:
			{
				rule(_t);
				_t = _retTree;
				break;
			}
			case TRUE:
			{
				AST tmp10_AST_in = (AST)_t;
				match(_t,TRUE);
				_t = _t.getNextSibling();
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
	
	public final void expression(AST _t) throws RecognitionException {
		
		AST expression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST i = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case OR:
			{
				AST __t44 = _t;
				AST tmp11_AST_in = (AST)_t;
				match(_t,OR);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t44;
				_t = _t.getNextSibling();
				break;
			}
			case PLUS:
			{
				AST __t45 = _t;
				AST tmp12_AST_in = (AST)_t;
				match(_t,PLUS);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				_t = __t45;
				_t = _t.getNextSibling();
				break;
			}
			case STAR:
			{
				AST __t46 = _t;
				AST tmp13_AST_in = (AST)_t;
				match(_t,STAR);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				_t = __t46;
				_t = _t.getNextSibling();
				break;
			}
			case SHARP:
			{
				AST __t47 = _t;
				AST tmp14_AST_in = (AST)_t;
				match(_t,SHARP);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				_t = __t47;
				_t = _t.getNextSibling();
				break;
			}
			case PROCUSE:
			{
				AST __t48 = _t;
				AST tmp15_AST_in = (AST)_t;
				match(_t,PROCUSE);
				_t = _t.getFirstChild();
				i = (AST)_t;
				match(_t,IDENTIFIER);
				_t = _t.getNextSibling();
				_t = __t48;
				_t = _t.getNextSibling();
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
		
		try {      // for error handling
			AST tmp16_AST_in = (AST)_t;
			match(_t,IDENTIFIER);
			_t = _t.getNextSibling();
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
		"SHARP"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 59256688L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	}
	
