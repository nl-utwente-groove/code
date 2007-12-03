// $ANTLR 2.7.6 (2005-12-22): "gcl.g" -> "GCLParser.java"$

package groove.control.parse;
import groove.control.*;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import java.util.Hashtable;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

public class GCLParser extends antlr.LLkParser       implements GCLParserTokenTypes
 {

protected GCLParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public GCLParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected GCLParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public GCLParser(TokenStream lexer) {
  this(lexer,2);
}

public GCLParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void program() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST program_AST = null;
		
		proclist();
		astFactory.addASTChild(currentAST, returnAST);
		match(Token.EOF_TYPE);
		program_AST = (AST)currentAST.root;
		program_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(PROGRAM,"program")).add(program_AST));
		currentAST.root = program_AST;
		currentAST.child = program_AST!=null &&program_AST.getFirstChild()!=null ?
			program_AST.getFirstChild() : program_AST;
		currentAST.advanceChildToEnd();
		program_AST = (AST)currentAST.root;
		returnAST = program_AST;
	}
	
	public final void proclist() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST proclist_AST = null;
		
		procdef();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case PROC:
		{
			proclist();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case EOF:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		proclist_AST = (AST)currentAST.root;
		returnAST = proclist_AST;
	}
	
	public final void block() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST block_AST = null;
		
		match(LCURLY);
		statements();
		astFactory.addASTChild(currentAST, returnAST);
		match(RCURLY);
		block_AST = (AST)currentAST.root;
		block_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(BLOCK,"block")).add(block_AST));
		currentAST.root = block_AST;
		currentAST.child = block_AST!=null &&block_AST.getFirstChild()!=null ?
			block_AST.getFirstChild() : block_AST;
		currentAST.advanceChildToEnd();
		block_AST = (AST)currentAST.root;
		returnAST = block_AST;
	}
	
	public final void statements() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statements_AST = null;
		
		statement();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case ALAP:
		case WHILE:
		case TRY:
		case DO:
		case IF:
		case CHOICE:
		case IDENTIFIER:
		case LPAREN:
		case SHARP:
		{
			statements();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case RCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		statements_AST = (AST)currentAST.root;
		returnAST = statements_AST;
	}
	
	public final void procdef() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST procdef_AST = null;
		Token  p = null;
		AST p_AST = null;
		Token  i = null;
		AST i_AST = null;
		
		p = LT(1);
		p_AST = astFactory.create(p);
		astFactory.makeASTRoot(currentAST, p_AST);
		match(PROC);
		i = LT(1);
		i_AST = astFactory.create(i);
		astFactory.addASTChild(currentAST, i_AST);
		match(IDENTIFIER);
		match(LPAREN);
		match(RPAREN);
		block();
		astFactory.addASTChild(currentAST, returnAST);
		procdef_AST = (AST)currentAST.root;
		returnAST = procdef_AST;
	}
	
	public final void statement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statement_AST = null;
		
		switch ( LA(1)) {
		case ALAP:
		{
			AST tmp22_AST = null;
			tmp22_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp22_AST);
			match(ALAP);
			block();
			astFactory.addASTChild(currentAST, returnAST);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case WHILE:
		{
			AST tmp23_AST = null;
			tmp23_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp23_AST);
			match(WHILE);
			match(LPAREN);
			condition();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			match(DO);
			block();
			astFactory.addASTChild(currentAST, returnAST);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case DO:
		{
			AST tmp27_AST = null;
			tmp27_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp27_AST);
			match(DO);
			block();
			astFactory.addASTChild(currentAST, returnAST);
			match(WHILE);
			condition();
			astFactory.addASTChild(currentAST, returnAST);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case TRY:
		{
			AST tmp29_AST = null;
			tmp29_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp29_AST);
			match(TRY);
			block();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case ELSE:
			{
				match(ELSE);
				block();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case ALAP:
			case WHILE:
			case TRY:
			case DO:
			case IF:
			case CHOICE:
			case RCURLY:
			case IDENTIFIER:
			case LPAREN:
			case SHARP:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			statement_AST = (AST)currentAST.root;
			break;
		}
		case IF:
		{
			AST tmp31_AST = null;
			tmp31_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp31_AST);
			match(IF);
			match(LPAREN);
			condition();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case CHOICE:
		{
			AST tmp34_AST = null;
			tmp34_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp34_AST);
			match(CHOICE);
			block();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop11:
			do {
				if ((LA(1)==OR)) {
					match(OR);
					block();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop11;
				}
				
			} while (true);
			}
			statement_AST = (AST)currentAST.root;
			break;
		}
		case IDENTIFIER:
		case LPAREN:
		case SHARP:
		{
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			statement_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = statement_AST;
	}
	
	public final void condition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST condition_AST = null;
		
		conditionliteral();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case OR:
		{
			AST tmp37_AST = null;
			tmp37_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp37_AST);
			match(OR);
			condition();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case ALAP:
		case WHILE:
		case TRY:
		case DO:
		case IF:
		case CHOICE:
		case RCURLY:
		case IDENTIFIER:
		case LPAREN:
		case RPAREN:
		case SHARP:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		condition_AST = (AST)currentAST.root;
		returnAST = condition_AST;
	}
	
	public final void expression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expression_AST = null;
		
		switch ( LA(1)) {
		case IDENTIFIER:
		case LPAREN:
		{
			expression_atom();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case OR:
			{
				{
				AST tmp38_AST = null;
				tmp38_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp38_AST);
				match(OR);
				expression();
				astFactory.addASTChild(currentAST, returnAST);
				}
				break;
			}
			case PLUS:
			{
				AST tmp39_AST = null;
				tmp39_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp39_AST);
				match(PLUS);
				break;
			}
			case STAR:
			{
				AST tmp40_AST = null;
				tmp40_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp40_AST);
				match(STAR);
				break;
			}
			case RPAREN:
			case SEMICOLON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			expression_AST = (AST)currentAST.root;
			break;
		}
		case SHARP:
		{
			AST tmp41_AST = null;
			tmp41_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp41_AST);
			match(SHARP);
			expression_atom();
			astFactory.addASTChild(currentAST, returnAST);
			expression_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = expression_AST;
	}
	
	public final void conditionliteral() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST conditionliteral_AST = null;
		
		switch ( LA(1)) {
		case TRUE:
		{
			AST tmp42_AST = null;
			tmp42_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp42_AST);
			match(TRUE);
			conditionliteral_AST = (AST)currentAST.root;
			break;
		}
		case IDENTIFIER:
		{
			rule();
			astFactory.addASTChild(currentAST, returnAST);
			conditionliteral_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = conditionliteral_AST;
	}
	
	public final void rule() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rule_AST = null;
		
		AST tmp43_AST = null;
		tmp43_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp43_AST);
		match(IDENTIFIER);
		rule_AST = (AST)currentAST.root;
		returnAST = rule_AST;
	}
	
	public final void expression_atom() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expression_atom_AST = null;
		
		if ((LA(1)==IDENTIFIER) && (_tokenSet_0.member(LA(2)))) {
			rule();
			astFactory.addASTChild(currentAST, returnAST);
			expression_atom_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==LPAREN)) {
			match(LPAREN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			expression_atom_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==IDENTIFIER) && (LA(2)==LPAREN)) {
			procuse();
			astFactory.addASTChild(currentAST, returnAST);
			expression_atom_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = expression_atom_AST;
	}
	
	public final void procuse() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST procuse_AST = null;
		
		AST tmp46_AST = null;
		tmp46_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp46_AST);
		match(IDENTIFIER);
		match(LPAREN);
		match(RPAREN);
		procuse_AST = (AST)currentAST.root;
		procuse_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(PROCUSE,"procuse")).add(procuse_AST));
		currentAST.root = procuse_AST;
		currentAST.child = procuse_AST!=null &&procuse_AST.getFirstChild()!=null ?
			procuse_AST.getFirstChild() : procuse_AST;
		currentAST.advanceChildToEnd();
		procuse_AST = (AST)currentAST.root;
		returnAST = procuse_AST;
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
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 31459328L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	
	}
