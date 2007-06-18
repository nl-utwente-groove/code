// $ANTLR 2.7.6 (2005-12-22): "gcl.g" -> "GCLParser.java"$

package groove.control;

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
		
		seq();
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
	
	public final void seq() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST seq_AST = null;
		
		or();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case SEMICOLON:
		{
			AST tmp7_AST = null;
			tmp7_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp7_AST);
			match(SEMICOLON);
			seq();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case EOF:
		case RCURLY:
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		seq_AST = (AST)currentAST.root;
		returnAST = seq_AST;
	}
	
	public final void body() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST body_AST = null;
		
		match(LCURLY);
		seq();
		astFactory.addASTChild(currentAST, returnAST);
		match(RCURLY);
		body_AST = (AST)currentAST.root;
		returnAST = body_AST;
	}
	
	public final void expression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expression_AST = null;
		
		seq();
		astFactory.addASTChild(currentAST, returnAST);
		expression_AST = (AST)currentAST.root;
		returnAST = expression_AST;
	}
	
	public final void basic() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST basic_AST = null;
		
		seq();
		astFactory.addASTChild(currentAST, returnAST);
		basic_AST = (AST)currentAST.root;
		returnAST = basic_AST;
	}
	
	public final void or() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST or_AST = null;
		
		complex();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case OR:
		{
			AST tmp10_AST = null;
			tmp10_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp10_AST);
			match(OR);
			or();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case EOF:
		case RCURLY:
		case SEMICOLON:
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		or_AST = (AST)currentAST.root;
		returnAST = or_AST;
	}
	
	public final void complex() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST complex_AST = null;
		
		switch ( LA(1)) {
		case ALAP:
		{
			AST tmp11_AST = null;
			tmp11_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp11_AST);
			match(ALAP);
			atom();
			astFactory.addASTChild(currentAST, returnAST);
			complex_AST = (AST)currentAST.root;
			break;
		}
		case DO:
		{
			AST tmp12_AST = null;
			tmp12_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp12_AST);
			match(DO);
			atom();
			astFactory.addASTChild(currentAST, returnAST);
			match(UNTIL);
			atom();
			astFactory.addASTChild(currentAST, returnAST);
			complex_AST = (AST)currentAST.root;
			break;
		}
		case TRY:
		{
			AST tmp14_AST = null;
			tmp14_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp14_AST);
			match(TRY);
			atom();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case ELSE:
			{
				AST tmp15_AST = null;
				tmp15_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp15_AST);
				match(ELSE);
				atom();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case RCURLY:
			case SEMICOLON:
			case OR:
			case RPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			complex_AST = (AST)currentAST.root;
			break;
		}
		case IDENTIFIER:
		case LPAREN:
		{
			atom();
			astFactory.addASTChild(currentAST, returnAST);
			complex_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = complex_AST;
	}
	
	public final void atom() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST atom_AST = null;
		
		switch ( LA(1)) {
		case IDENTIFIER:
		{
			AST tmp16_AST = null;
			tmp16_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp16_AST);
			match(IDENTIFIER);
			atom_AST = (AST)currentAST.root;
			break;
		}
		case LPAREN:
		{
			match(LPAREN);
			seq();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			atom_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = atom_AST;
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
		"RPAREN",
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
	
	
	}
