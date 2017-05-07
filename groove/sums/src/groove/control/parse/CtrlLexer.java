// $ANTLR null E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g 2017-04-10 19:15:58

package groove.control.parse;
import groove.control.*;
import groove.util.antlr.*;
import java.util.LinkedList;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class CtrlLexer extends Lexer {
	public static final int EOF=-1;
	public static final int ALAP=4;
	public static final int AMP=5;
	public static final int ANY=6;
	public static final int ARG=7;
	public static final int ARGS=8;
	public static final int ASTERISK=9;
	public static final int ATOM=10;
	public static final int BAR=11;
	public static final int BECOMES=12;
	public static final int BLOCK=13;
	public static final int BOOL=14;
	public static final int BQUOTE=15;
	public static final int BSLASH=16;
	public static final int CALL=17;
	public static final int CHOICE=18;
	public static final int COMMA=19;
	public static final int DO=20;
	public static final int DONT_CARE=21;
	public static final int DOT=22;
	public static final int DO_UNTIL=23;
	public static final int DO_WHILE=24;
	public static final int ELSE=25;
	public static final int EscapeSequence=26;
	public static final int FALSE=27;
	public static final int FUNCTION=28;
	public static final int FUNCTIONS=29;
	public static final int ID=30;
	public static final int IF=31;
	public static final int IMPORT=32;
	public static final int IMPORTS=33;
	public static final int INT=34;
	public static final int INT_LIT=35;
	public static final int IntegerNumber=36;
	public static final int LANGLE=37;
	public static final int LCURLY=38;
	public static final int LPAR=39;
	public static final int MINUS=40;
	public static final int ML_COMMENT=41;
	public static final int NODE=42;
	public static final int NOT=43;
	public static final int NonIntegerNumber=44;
	public static final int OR=45;
	public static final int OTHER=46;
	public static final int OUT=47;
	public static final int PACKAGE=48;
	public static final int PAR=49;
	public static final int PARS=50;
	public static final int PLUS=51;
	public static final int PRIORITY=52;
	public static final int PROGRAM=53;
	public static final int QUOTE=54;
	public static final int RANGLE=55;
	public static final int RCURLY=56;
	public static final int REAL=57;
	public static final int REAL_LIT=58;
	public static final int RECIPE=59;
	public static final int RECIPES=60;
	public static final int RPAR=61;
	public static final int SEMI=62;
	public static final int SHARP=63;
	public static final int SL_COMMENT=64;
	public static final int STAR=65;
	public static final int STRING=66;
	public static final int STRING_LIT=67;
	public static final int TRUE=68;
	public static final int TRY=69;
	public static final int UNTIL=70;
	public static final int VAR=71;
	public static final int WHILE=72;
	public static final int WS=73;

	    /** Name space to record lexer errors. */
	    private Namespace namespace;
	    
	    public void initialise(ParseInfo namespace) {
	        this.namespace = (Namespace) namespace;
	    }
	    
	    public void displayRecognitionError(String[] tokenNames,
	            RecognitionException e) {
	        String hdr = getErrorHeader(e);
	        String msg = getErrorMessage(e, tokenNames);
	        this.namespace.addError(hdr + " " + msg, e.line, e.charPositionInLine);
	    }


	// delegates
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public CtrlLexer() {} 
	public CtrlLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}
	public CtrlLexer(CharStream input, RecognizerSharedState state) {
		super(input,state);
	}
	@Override public String getGrammarFileName() { return "E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g"; }

	// $ANTLR start "ALAP"
	public final void mALAP() throws RecognitionException {
		try {
			int _type = ALAP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:445:10: ( 'alap' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:445:12: 'alap'
			{
			match("alap"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ALAP"

	// $ANTLR start "ANY"
	public final void mANY() throws RecognitionException {
		try {
			int _type = ANY;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:446:9: ( 'any' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:446:11: 'any'
			{
			match("any"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ANY"

	// $ANTLR start "ATOM"
	public final void mATOM() throws RecognitionException {
		try {
			int _type = ATOM;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:447:10: ( 'atomic' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:447:12: 'atomic'
			{
			match("atomic"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ATOM"

	// $ANTLR start "BOOL"
	public final void mBOOL() throws RecognitionException {
		try {
			int _type = BOOL;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:448:10: ( 'bool' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:448:12: 'bool'
			{
			match("bool"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "BOOL"

	// $ANTLR start "CHOICE"
	public final void mCHOICE() throws RecognitionException {
		try {
			int _type = CHOICE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:449:10: ( 'choice' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:449:12: 'choice'
			{
			match("choice"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "CHOICE"

	// $ANTLR start "DO"
	public final void mDO() throws RecognitionException {
		try {
			int _type = DO;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:450:10: ( 'do' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:450:12: 'do'
			{
			match("do"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DO"

	// $ANTLR start "ELSE"
	public final void mELSE() throws RecognitionException {
		try {
			int _type = ELSE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:451:10: ( 'else' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:451:12: 'else'
			{
			match("else"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ELSE"

	// $ANTLR start "FALSE"
	public final void mFALSE() throws RecognitionException {
		try {
			int _type = FALSE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:452:10: ( 'false' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:452:12: 'false'
			{
			match("false"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "FALSE"

	// $ANTLR start "FUNCTION"
	public final void mFUNCTION() throws RecognitionException {
		try {
			int _type = FUNCTION;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:453:10: ( 'function' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:453:12: 'function'
			{
			match("function"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "FUNCTION"

	// $ANTLR start "IF"
	public final void mIF() throws RecognitionException {
		try {
			int _type = IF;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:454:10: ( 'if' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:454:12: 'if'
			{
			match("if"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "IF"

	// $ANTLR start "IMPORT"
	public final void mIMPORT() throws RecognitionException {
		try {
			int _type = IMPORT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:455:10: ( 'import' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:455:12: 'import'
			{
			match("import"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "IMPORT"

	// $ANTLR start "INT"
	public final void mINT() throws RecognitionException {
		try {
			int _type = INT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:456:10: ( 'int' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:456:12: 'int'
			{
			match("int"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "INT"

	// $ANTLR start "NODE"
	public final void mNODE() throws RecognitionException {
		try {
			int _type = NODE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:457:10: ( 'node' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:457:12: 'node'
			{
			match("node"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NODE"

	// $ANTLR start "OR"
	public final void mOR() throws RecognitionException {
		try {
			int _type = OR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:458:10: ( 'or' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:458:12: 'or'
			{
			match("or"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OR"

	// $ANTLR start "OTHER"
	public final void mOTHER() throws RecognitionException {
		try {
			int _type = OTHER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:459:10: ( 'other' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:459:12: 'other'
			{
			match("other"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OTHER"

	// $ANTLR start "OUT"
	public final void mOUT() throws RecognitionException {
		try {
			int _type = OUT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:460:10: ( 'out' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:460:12: 'out'
			{
			match("out"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OUT"

	// $ANTLR start "REAL"
	public final void mREAL() throws RecognitionException {
		try {
			int _type = REAL;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:461:10: ( 'real' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:461:12: 'real'
			{
			match("real"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "REAL"

	// $ANTLR start "PACKAGE"
	public final void mPACKAGE() throws RecognitionException {
		try {
			int _type = PACKAGE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:462:10: ( 'package' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:462:12: 'package'
			{
			match("package"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "PACKAGE"

	// $ANTLR start "PRIORITY"
	public final void mPRIORITY() throws RecognitionException {
		try {
			int _type = PRIORITY;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:463:10: ( 'priority' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:463:12: 'priority'
			{
			match("priority"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "PRIORITY"

	// $ANTLR start "RECIPE"
	public final void mRECIPE() throws RecognitionException {
		try {
			int _type = RECIPE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:464:10: ( 'recipe' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:464:12: 'recipe'
			{
			match("recipe"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RECIPE"

	// $ANTLR start "STAR"
	public final void mSTAR() throws RecognitionException {
		try {
			int _type = STAR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:465:10: ( 'star' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:465:12: 'star'
			{
			match("star"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "STAR"

	// $ANTLR start "STRING"
	public final void mSTRING() throws RecognitionException {
		try {
			int _type = STRING;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:466:10: ( 'string' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:466:12: 'string'
			{
			match("string"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "STRING"

	// $ANTLR start "TRY"
	public final void mTRY() throws RecognitionException {
		try {
			int _type = TRY;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:467:10: ( 'try' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:467:12: 'try'
			{
			match("try"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "TRY"

	// $ANTLR start "TRUE"
	public final void mTRUE() throws RecognitionException {
		try {
			int _type = TRUE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:468:10: ( 'true' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:468:12: 'true'
			{
			match("true"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "TRUE"

	// $ANTLR start "UNTIL"
	public final void mUNTIL() throws RecognitionException {
		try {
			int _type = UNTIL;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:469:10: ( 'until' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:469:12: 'until'
			{
			match("until"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "UNTIL"

	// $ANTLR start "WHILE"
	public final void mWHILE() throws RecognitionException {
		try {
			int _type = WHILE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:470:10: ( 'while' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:470:12: 'while'
			{
			match("while"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WHILE"

	// $ANTLR start "INT_LIT"
	public final void mINT_LIT() throws RecognitionException {
		try {
			int _type = INT_LIT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:473:3: ( IntegerNumber )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:473:5: IntegerNumber
			{
			mIntegerNumber(); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "INT_LIT"

	// $ANTLR start "IntegerNumber"
	public final void mIntegerNumber() throws RecognitionException {
		try {
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:479:3: ( '0' | '1' .. '9' ( '0' .. '9' )* )
			int alt2=2;
			int LA2_0 = input.LA(1);
			if ( (LA2_0=='0') ) {
				alt2=1;
			}
			else if ( ((LA2_0 >= '1' && LA2_0 <= '9')) ) {
				alt2=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 2, 0, input);
				throw nvae;
			}

			switch (alt2) {
				case 1 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:479:5: '0'
					{
					match('0'); 
					}
					break;
				case 2 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:480:5: '1' .. '9' ( '0' .. '9' )*
					{
					matchRange('1','9'); 
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:480:14: ( '0' .. '9' )*
					loop1:
					while (true) {
						int alt1=2;
						int LA1_0 = input.LA(1);
						if ( ((LA1_0 >= '0' && LA1_0 <= '9')) ) {
							alt1=1;
						}

						switch (alt1) {
						case 1 :
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							break loop1;
						}
					}

					}
					break;

			}
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "IntegerNumber"

	// $ANTLR start "REAL_LIT"
	public final void mREAL_LIT() throws RecognitionException {
		try {
			int _type = REAL_LIT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:483:3: ( NonIntegerNumber )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:483:5: NonIntegerNumber
			{
			mNonIntegerNumber(); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "REAL_LIT"

	// $ANTLR start "NonIntegerNumber"
	public final void mNonIntegerNumber() throws RecognitionException {
		try {
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:489:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* | '.' ( '0' .. '9' )+ )
			int alt6=2;
			int LA6_0 = input.LA(1);
			if ( ((LA6_0 >= '0' && LA6_0 <= '9')) ) {
				alt6=1;
			}
			else if ( (LA6_0=='.') ) {
				alt6=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 6, 0, input);
				throw nvae;
			}

			switch (alt6) {
				case 1 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:489:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )*
					{
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:489:9: ( '0' .. '9' )+
					int cnt3=0;
					loop3:
					while (true) {
						int alt3=2;
						int LA3_0 = input.LA(1);
						if ( ((LA3_0 >= '0' && LA3_0 <= '9')) ) {
							alt3=1;
						}

						switch (alt3) {
						case 1 :
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt3 >= 1 ) break loop3;
							EarlyExitException eee = new EarlyExitException(3, input);
							throw eee;
						}
						cnt3++;
					}

					match('.'); 
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:489:27: ( '0' .. '9' )*
					loop4:
					while (true) {
						int alt4=2;
						int LA4_0 = input.LA(1);
						if ( ((LA4_0 >= '0' && LA4_0 <= '9')) ) {
							alt4=1;
						}

						switch (alt4) {
						case 1 :
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							break loop4;
						}
					}

					}
					break;
				case 2 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:490:9: '.' ( '0' .. '9' )+
					{
					match('.'); 
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:490:13: ( '0' .. '9' )+
					int cnt5=0;
					loop5:
					while (true) {
						int alt5=2;
						int LA5_0 = input.LA(1);
						if ( ((LA5_0 >= '0' && LA5_0 <= '9')) ) {
							alt5=1;
						}

						switch (alt5) {
						case 1 :
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt5 >= 1 ) break loop5;
							EarlyExitException eee = new EarlyExitException(5, input);
							throw eee;
						}
						cnt5++;
					}

					}
					break;

			}
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NonIntegerNumber"

	// $ANTLR start "STRING_LIT"
	public final void mSTRING_LIT() throws RecognitionException {
		try {
			int _type = STRING_LIT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:494:3: ( QUOTE ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:494:5: QUOTE ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE
			{
			mQUOTE(); 

			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:495:5: ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )*
			loop7:
			while (true) {
				int alt7=3;
				int LA7_0 = input.LA(1);
				if ( (LA7_0=='\\') ) {
					alt7=1;
				}
				else if ( ((LA7_0 >= '\u0000' && LA7_0 <= '\t')||(LA7_0 >= '\u000B' && LA7_0 <= '\f')||(LA7_0 >= '\u000E' && LA7_0 <= '!')||(LA7_0 >= '#' && LA7_0 <= '[')||(LA7_0 >= ']' && LA7_0 <= '\uFFFF')) ) {
					alt7=2;
				}

				switch (alt7) {
				case 1 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:495:7: EscapeSequence
					{
					mEscapeSequence(); 

					}
					break;
				case 2 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:496:7: ~ ( BSLASH | QUOTE | '\\r' | '\\n' )
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop7;
				}
			}

			mQUOTE(); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "STRING_LIT"

	// $ANTLR start "EscapeSequence"
	public final void mEscapeSequence() throws RecognitionException {
		try {
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:504:3: ( BSLASH ( QUOTE | BSLASH ) )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:504:5: BSLASH ( QUOTE | BSLASH )
			{
			mBSLASH(); 

			if ( input.LA(1)=='\"'||input.LA(1)=='\\' ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "EscapeSequence"

	// $ANTLR start "ID"
	public final void mID() throws RecognitionException {
		try {
			int _type = ID;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:510:3: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )* | BQUOTE ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )* BQUOTE )
			int alt10=2;
			int LA10_0 = input.LA(1);
			if ( ((LA10_0 >= 'A' && LA10_0 <= 'Z')||(LA10_0 >= 'a' && LA10_0 <= 'z')) ) {
				alt10=1;
			}
			else if ( (LA10_0=='`') ) {
				alt10=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 10, 0, input);
				throw nvae;
			}

			switch (alt10) {
				case 1 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:510:5: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
					{
					if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:510:25: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
					loop8:
					while (true) {
						int alt8=2;
						int LA8_0 = input.LA(1);
						if ( (LA8_0=='-'||(LA8_0 >= '0' && LA8_0 <= '9')||(LA8_0 >= 'A' && LA8_0 <= 'Z')||LA8_0=='_'||(LA8_0 >= 'a' && LA8_0 <= 'z')) ) {
							alt8=1;
						}

						switch (alt8) {
						case 1 :
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:
							{
							if ( input.LA(1)=='-'||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							break loop8;
						}
					}

					}
					break;
				case 2 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:511:5: BQUOTE ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )* BQUOTE
					{
					mBQUOTE(); 

					if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:511:32: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
					loop9:
					while (true) {
						int alt9=2;
						int LA9_0 = input.LA(1);
						if ( (LA9_0=='-'||(LA9_0 >= '0' && LA9_0 <= '9')||(LA9_0 >= 'A' && LA9_0 <= 'Z')||LA9_0=='_'||(LA9_0 >= 'a' && LA9_0 <= 'z')) ) {
							alt9=1;
						}

						switch (alt9) {
						case 1 :
							// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:
							{
							if ( input.LA(1)=='-'||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							break loop9;
						}
					}

					mBQUOTE(); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ID"

	// $ANTLR start "AMP"
	public final void mAMP() throws RecognitionException {
		try {
			int _type = AMP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:514:11: ( '&' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:514:13: '&'
			{
			match('&'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "AMP"

	// $ANTLR start "BECOMES"
	public final void mBECOMES() throws RecognitionException {
		try {
			int _type = BECOMES;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:515:11: ( ':=' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:515:13: ':='
			{
			match(":="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "BECOMES"

	// $ANTLR start "DOT"
	public final void mDOT() throws RecognitionException {
		try {
			int _type = DOT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:516:11: ( '.' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:516:13: '.'
			{
			match('.'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DOT"

	// $ANTLR start "NOT"
	public final void mNOT() throws RecognitionException {
		try {
			int _type = NOT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:517:11: ( '!' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:517:13: '!'
			{
			match('!'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NOT"

	// $ANTLR start "BAR"
	public final void mBAR() throws RecognitionException {
		try {
			int _type = BAR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:518:11: ( '|' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:518:13: '|'
			{
			match('|'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "BAR"

	// $ANTLR start "SHARP"
	public final void mSHARP() throws RecognitionException {
		try {
			int _type = SHARP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:519:11: ( '#' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:519:13: '#'
			{
			match('#'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "SHARP"

	// $ANTLR start "PLUS"
	public final void mPLUS() throws RecognitionException {
		try {
			int _type = PLUS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:520:11: ( '+' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:520:13: '+'
			{
			match('+'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "PLUS"

	// $ANTLR start "ASTERISK"
	public final void mASTERISK() throws RecognitionException {
		try {
			int _type = ASTERISK;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:521:11: ( '*' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:521:13: '*'
			{
			match('*'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ASTERISK"

	// $ANTLR start "DONT_CARE"
	public final void mDONT_CARE() throws RecognitionException {
		try {
			int _type = DONT_CARE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:522:11: ( '_' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:522:13: '_'
			{
			match('_'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DONT_CARE"

	// $ANTLR start "MINUS"
	public final void mMINUS() throws RecognitionException {
		try {
			int _type = MINUS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:523:11: ( '-' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:523:13: '-'
			{
			match('-'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "MINUS"

	// $ANTLR start "QUOTE"
	public final void mQUOTE() throws RecognitionException {
		try {
			int _type = QUOTE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:524:11: ( '\"' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:524:13: '\"'
			{
			match('\"'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "QUOTE"

	// $ANTLR start "BQUOTE"
	public final void mBQUOTE() throws RecognitionException {
		try {
			int _type = BQUOTE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:525:11: ( '`' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:525:13: '`'
			{
			match('`'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "BQUOTE"

	// $ANTLR start "BSLASH"
	public final void mBSLASH() throws RecognitionException {
		try {
			int _type = BSLASH;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:526:11: ( '\\\\' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:526:13: '\\\\'
			{
			match('\\'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "BSLASH"

	// $ANTLR start "COMMA"
	public final void mCOMMA() throws RecognitionException {
		try {
			int _type = COMMA;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:527:11: ( ',' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:527:13: ','
			{
			match(','); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "COMMA"

	// $ANTLR start "SEMI"
	public final void mSEMI() throws RecognitionException {
		try {
			int _type = SEMI;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:528:11: ( ';' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:528:13: ';'
			{
			match(';'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "SEMI"

	// $ANTLR start "LPAR"
	public final void mLPAR() throws RecognitionException {
		try {
			int _type = LPAR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:529:11: ( '(' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:529:13: '('
			{
			match('('); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LPAR"

	// $ANTLR start "RPAR"
	public final void mRPAR() throws RecognitionException {
		try {
			int _type = RPAR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:530:11: ( ')' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:530:13: ')'
			{
			match(')'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RPAR"

	// $ANTLR start "LANGLE"
	public final void mLANGLE() throws RecognitionException {
		try {
			int _type = LANGLE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:531:11: ( '<' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:531:13: '<'
			{
			match('<'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LANGLE"

	// $ANTLR start "RANGLE"
	public final void mRANGLE() throws RecognitionException {
		try {
			int _type = RANGLE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:532:11: ( '>' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:532:13: '>'
			{
			match('>'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RANGLE"

	// $ANTLR start "LCURLY"
	public final void mLCURLY() throws RecognitionException {
		try {
			int _type = LCURLY;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:533:11: ( '{' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:533:13: '{'
			{
			match('{'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LCURLY"

	// $ANTLR start "RCURLY"
	public final void mRCURLY() throws RecognitionException {
		try {
			int _type = RCURLY;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:534:11: ( '}' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:534:13: '}'
			{
			match('}'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RCURLY"

	// $ANTLR start "ML_COMMENT"
	public final void mML_COMMENT() throws RecognitionException {
		try {
			int _type = ML_COMMENT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:536:12: ( '/*' ( options {greedy=false; } : . )* '*/' )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:536:14: '/*' ( options {greedy=false; } : . )* '*/'
			{
			match("/*"); 

			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:536:19: ( options {greedy=false; } : . )*
			loop11:
			while (true) {
				int alt11=2;
				int LA11_0 = input.LA(1);
				if ( (LA11_0=='*') ) {
					int LA11_1 = input.LA(2);
					if ( (LA11_1=='/') ) {
						alt11=2;
					}
					else if ( ((LA11_1 >= '\u0000' && LA11_1 <= '.')||(LA11_1 >= '0' && LA11_1 <= '\uFFFF')) ) {
						alt11=1;
					}

				}
				else if ( ((LA11_0 >= '\u0000' && LA11_0 <= ')')||(LA11_0 >= '+' && LA11_0 <= '\uFFFF')) ) {
					alt11=1;
				}

				switch (alt11) {
				case 1 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:536:47: .
					{
					matchAny(); 
					}
					break;

				default :
					break loop11;
				}
			}

			match("*/"); 

			 _channel=HIDDEN; 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ML_COMMENT"

	// $ANTLR start "SL_COMMENT"
	public final void mSL_COMMENT() throws RecognitionException {
		try {
			int _type = SL_COMMENT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:537:12: ( '//' (~ ( '\\n' ) )* )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:537:14: '//' (~ ( '\\n' ) )*
			{
			match("//"); 

			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:537:19: (~ ( '\\n' ) )*
			loop12:
			while (true) {
				int alt12=2;
				int LA12_0 = input.LA(1);
				if ( ((LA12_0 >= '\u0000' && LA12_0 <= '\t')||(LA12_0 >= '\u000B' && LA12_0 <= '\uFFFF')) ) {
					alt12=1;
				}

				switch (alt12) {
				case 1 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop12;
				}
			}

			 _channel=HIDDEN; 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "SL_COMMENT"

	// $ANTLR start "WS"
	public final void mWS() throws RecognitionException {
		try {
			int _type = WS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:539:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:539:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
			{
			// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:539:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
			int cnt13=0;
			loop13:
			while (true) {
				int alt13=2;
				int LA13_0 = input.LA(1);
				if ( ((LA13_0 >= '\t' && LA13_0 <= '\n')||LA13_0=='\r'||LA13_0==' ') ) {
					alt13=1;
				}

				switch (alt13) {
				case 1 :
					// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt13 >= 1 ) break loop13;
					EarlyExitException eee = new EarlyExitException(13, input);
					throw eee;
				}
				cnt13++;
			}

			 _channel=HIDDEN; 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WS"

	@Override
	public void mTokens() throws RecognitionException {
		// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:8: ( ALAP | ANY | ATOM | BOOL | CHOICE | DO | ELSE | FALSE | FUNCTION | IF | IMPORT | INT | NODE | OR | OTHER | OUT | REAL | PACKAGE | PRIORITY | RECIPE | STAR | STRING | TRY | TRUE | UNTIL | WHILE | INT_LIT | REAL_LIT | STRING_LIT | ID | AMP | BECOMES | DOT | NOT | BAR | SHARP | PLUS | ASTERISK | DONT_CARE | MINUS | QUOTE | BQUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LANGLE | RANGLE | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS )
		int alt14=54;
		alt14 = dfa14.predict(input);
		switch (alt14) {
			case 1 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:10: ALAP
				{
				mALAP(); 

				}
				break;
			case 2 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:15: ANY
				{
				mANY(); 

				}
				break;
			case 3 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:19: ATOM
				{
				mATOM(); 

				}
				break;
			case 4 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:24: BOOL
				{
				mBOOL(); 

				}
				break;
			case 5 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:29: CHOICE
				{
				mCHOICE(); 

				}
				break;
			case 6 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:36: DO
				{
				mDO(); 

				}
				break;
			case 7 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:39: ELSE
				{
				mELSE(); 

				}
				break;
			case 8 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:44: FALSE
				{
				mFALSE(); 

				}
				break;
			case 9 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:50: FUNCTION
				{
				mFUNCTION(); 

				}
				break;
			case 10 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:59: IF
				{
				mIF(); 

				}
				break;
			case 11 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:62: IMPORT
				{
				mIMPORT(); 

				}
				break;
			case 12 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:69: INT
				{
				mINT(); 

				}
				break;
			case 13 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:73: NODE
				{
				mNODE(); 

				}
				break;
			case 14 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:78: OR
				{
				mOR(); 

				}
				break;
			case 15 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:81: OTHER
				{
				mOTHER(); 

				}
				break;
			case 16 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:87: OUT
				{
				mOUT(); 

				}
				break;
			case 17 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:91: REAL
				{
				mREAL(); 

				}
				break;
			case 18 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:96: PACKAGE
				{
				mPACKAGE(); 

				}
				break;
			case 19 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:104: PRIORITY
				{
				mPRIORITY(); 

				}
				break;
			case 20 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:113: RECIPE
				{
				mRECIPE(); 

				}
				break;
			case 21 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:120: STAR
				{
				mSTAR(); 

				}
				break;
			case 22 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:125: STRING
				{
				mSTRING(); 

				}
				break;
			case 23 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:132: TRY
				{
				mTRY(); 

				}
				break;
			case 24 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:136: TRUE
				{
				mTRUE(); 

				}
				break;
			case 25 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:141: UNTIL
				{
				mUNTIL(); 

				}
				break;
			case 26 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:147: WHILE
				{
				mWHILE(); 

				}
				break;
			case 27 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:153: INT_LIT
				{
				mINT_LIT(); 

				}
				break;
			case 28 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:161: REAL_LIT
				{
				mREAL_LIT(); 

				}
				break;
			case 29 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:170: STRING_LIT
				{
				mSTRING_LIT(); 

				}
				break;
			case 30 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:181: ID
				{
				mID(); 

				}
				break;
			case 31 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:184: AMP
				{
				mAMP(); 

				}
				break;
			case 32 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:188: BECOMES
				{
				mBECOMES(); 

				}
				break;
			case 33 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:196: DOT
				{
				mDOT(); 

				}
				break;
			case 34 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:200: NOT
				{
				mNOT(); 

				}
				break;
			case 35 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:204: BAR
				{
				mBAR(); 

				}
				break;
			case 36 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:208: SHARP
				{
				mSHARP(); 

				}
				break;
			case 37 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:214: PLUS
				{
				mPLUS(); 

				}
				break;
			case 38 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:219: ASTERISK
				{
				mASTERISK(); 

				}
				break;
			case 39 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:228: DONT_CARE
				{
				mDONT_CARE(); 

				}
				break;
			case 40 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:238: MINUS
				{
				mMINUS(); 

				}
				break;
			case 41 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:244: QUOTE
				{
				mQUOTE(); 

				}
				break;
			case 42 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:250: BQUOTE
				{
				mBQUOTE(); 

				}
				break;
			case 43 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:257: BSLASH
				{
				mBSLASH(); 

				}
				break;
			case 44 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:264: COMMA
				{
				mCOMMA(); 

				}
				break;
			case 45 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:270: SEMI
				{
				mSEMI(); 

				}
				break;
			case 46 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:275: LPAR
				{
				mLPAR(); 

				}
				break;
			case 47 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:280: RPAR
				{
				mRPAR(); 

				}
				break;
			case 48 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:285: LANGLE
				{
				mLANGLE(); 

				}
				break;
			case 49 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:292: RANGLE
				{
				mRANGLE(); 

				}
				break;
			case 50 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:299: LCURLY
				{
				mLCURLY(); 

				}
				break;
			case 51 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:306: RCURLY
				{
				mRCURLY(); 

				}
				break;
			case 52 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:313: ML_COMMENT
				{
				mML_COMMENT(); 

				}
				break;
			case 53 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:324: SL_COMMENT
				{
				mSL_COMMENT(); 

				}
				break;
			case 54 :
				// E:\\Eclipse\\neon\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:335: WS
				{
				mWS(); 

				}
				break;

		}
	}


	protected DFA14 dfa14 = new DFA14(this);
	static final String DFA14_eotS =
		"\1\uffff\17\24\2\101\1\104\1\105\1\uffff\1\107\24\uffff\5\24\1\117\3\24"+
		"\1\123\3\24\1\127\11\24\2\uffff\1\101\6\uffff\1\24\1\145\3\24\1\uffff"+
		"\3\24\1\uffff\1\24\1\155\1\24\1\uffff\1\24\1\160\6\24\1\167\3\24\1\173"+
		"\1\uffff\1\24\1\175\1\24\1\177\3\24\1\uffff\1\u0083\1\24\1\uffff\1\u0085"+
		"\3\24\1\u0089\1\24\1\uffff\1\u008b\2\24\1\uffff\1\24\1\uffff\1\24\1\uffff"+
		"\1\u0090\2\24\1\uffff\1\u0093\1\uffff\3\24\1\uffff\1\24\1\uffff\1\u0098"+
		"\1\u0099\1\u009a\1\u009b\1\uffff\1\24\1\u009d\1\uffff\1\u009e\2\24\1\u00a1"+
		"\4\uffff\1\24\2\uffff\1\u00a3\1\24\1\uffff\1\u00a5\1\uffff\1\u00a6\2\uffff";
	static final String DFA14_eofS =
		"\u00a7\uffff";
	static final String DFA14_minS =
		"\1\11\1\154\1\157\1\150\1\157\1\154\1\141\1\146\1\157\1\162\1\145\1\141"+
		"\1\164\1\162\1\156\1\150\2\56\1\60\1\0\1\uffff\1\101\22\uffff\1\52\1\uffff"+
		"\1\141\1\171\3\157\1\55\1\163\1\154\1\156\1\55\1\160\1\164\1\144\1\55"+
		"\1\150\1\164\1\141\1\143\1\151\1\141\1\165\1\164\1\151\2\uffff\1\56\6"+
		"\uffff\1\160\1\55\1\155\1\154\1\151\1\uffff\1\145\1\163\1\143\1\uffff"+
		"\1\157\1\55\1\145\1\uffff\1\145\1\55\1\154\1\151\1\153\1\157\1\162\1\151"+
		"\1\55\1\145\1\151\1\154\1\55\1\uffff\1\151\1\55\1\143\1\55\1\145\1\164"+
		"\1\162\1\uffff\1\55\1\162\1\uffff\1\55\1\160\1\141\1\162\1\55\1\156\1"+
		"\uffff\1\55\1\154\1\145\1\uffff\1\143\1\uffff\1\145\1\uffff\1\55\1\151"+
		"\1\164\1\uffff\1\55\1\uffff\1\145\1\147\1\151\1\uffff\1\147\1\uffff\4"+
		"\55\1\uffff\1\157\1\55\1\uffff\1\55\1\145\1\164\1\55\4\uffff\1\156\2\uffff"+
		"\1\55\1\171\1\uffff\1\55\1\uffff\1\55\2\uffff";
	static final String DFA14_maxS =
		"\1\175\1\164\1\157\1\150\1\157\1\154\1\165\1\156\1\157\1\165\1\145\1\162"+
		"\1\164\1\162\1\156\1\150\3\71\1\uffff\1\uffff\1\172\22\uffff\1\57\1\uffff"+
		"\1\141\1\171\3\157\1\172\1\163\1\154\1\156\1\172\1\160\1\164\1\144\1\172"+
		"\1\150\1\164\2\143\1\151\1\162\1\171\1\164\1\151\2\uffff\1\71\6\uffff"+
		"\1\160\1\172\1\155\1\154\1\151\1\uffff\1\145\1\163\1\143\1\uffff\1\157"+
		"\1\172\1\145\1\uffff\1\145\1\172\1\154\1\151\1\153\1\157\1\162\1\151\1"+
		"\172\1\145\1\151\1\154\1\172\1\uffff\1\151\1\172\1\143\1\172\1\145\1\164"+
		"\1\162\1\uffff\1\172\1\162\1\uffff\1\172\1\160\1\141\1\162\1\172\1\156"+
		"\1\uffff\1\172\1\154\1\145\1\uffff\1\143\1\uffff\1\145\1\uffff\1\172\1"+
		"\151\1\164\1\uffff\1\172\1\uffff\1\145\1\147\1\151\1\uffff\1\147\1\uffff"+
		"\4\172\1\uffff\1\157\1\172\1\uffff\1\172\1\145\1\164\1\172\4\uffff\1\156"+
		"\2\uffff\1\172\1\171\1\uffff\1\172\1\uffff\1\172\2\uffff";
	static final String DFA14_acceptS =
		"\24\uffff\1\36\1\uffff\1\37\1\40\1\42\1\43\1\44\1\45\1\46\1\47\1\50\1"+
		"\53\1\54\1\55\1\56\1\57\1\60\1\61\1\62\1\63\1\uffff\1\66\27\uffff\1\33"+
		"\1\34\1\uffff\1\41\1\51\1\35\1\52\1\64\1\65\5\uffff\1\6\3\uffff\1\12\3"+
		"\uffff\1\16\15\uffff\1\2\7\uffff\1\14\2\uffff\1\20\6\uffff\1\27\3\uffff"+
		"\1\1\1\uffff\1\4\1\uffff\1\7\3\uffff\1\15\1\uffff\1\21\3\uffff\1\25\1"+
		"\uffff\1\30\4\uffff\1\10\2\uffff\1\17\4\uffff\1\31\1\32\1\3\1\5\1\uffff"+
		"\1\13\1\24\2\uffff\1\26\1\uffff\1\22\1\uffff\1\11\1\23";
	static final String DFA14_specialS =
		"\23\uffff\1\0\u0093\uffff}>";
	static final String[] DFA14_transitionS = {
			"\2\51\2\uffff\1\51\22\uffff\1\51\1\30\1\23\1\32\2\uffff\1\26\1\uffff"+
			"\1\42\1\43\1\34\1\33\1\40\1\36\1\22\1\50\1\20\11\21\1\27\1\41\1\44\1"+
			"\uffff\1\45\2\uffff\32\24\1\uffff\1\37\2\uffff\1\35\1\25\1\1\1\2\1\3"+
			"\1\4\1\5\1\6\2\24\1\7\4\24\1\10\1\11\1\13\1\24\1\12\1\14\1\15\1\16\1"+
			"\24\1\17\3\24\1\46\1\31\1\47",
			"\1\52\1\uffff\1\53\5\uffff\1\54",
			"\1\55",
			"\1\56",
			"\1\57",
			"\1\60",
			"\1\61\23\uffff\1\62",
			"\1\63\6\uffff\1\64\1\65",
			"\1\66",
			"\1\67\1\uffff\1\70\1\71",
			"\1\72",
			"\1\73\20\uffff\1\74",
			"\1\75",
			"\1\76",
			"\1\77",
			"\1\100",
			"\1\102\1\uffff\12\102",
			"\1\102\1\uffff\12\103",
			"\12\102",
			"\12\106\1\uffff\2\106\1\uffff\ufff2\106",
			"",
			"\32\24\6\uffff\32\24",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\110\4\uffff\1\111",
			"",
			"\1\112",
			"\1\113",
			"\1\114",
			"\1\115",
			"\1\116",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"\1\120",
			"\1\121",
			"\1\122",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"\1\124",
			"\1\125",
			"\1\126",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"\1\130",
			"\1\131",
			"\1\132\1\uffff\1\133",
			"\1\134",
			"\1\135",
			"\1\136\20\uffff\1\137",
			"\1\141\3\uffff\1\140",
			"\1\142",
			"\1\143",
			"",
			"",
			"\1\102\1\uffff\12\103",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\144",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"\1\146",
			"\1\147",
			"\1\150",
			"",
			"\1\151",
			"\1\152",
			"\1\153",
			"",
			"\1\154",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"\1\156",
			"",
			"\1\157",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"\1\161",
			"\1\162",
			"\1\163",
			"\1\164",
			"\1\165",
			"\1\166",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"\1\170",
			"\1\171",
			"\1\172",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"",
			"\1\174",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"\1\176",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"\1\u0080",
			"\1\u0081",
			"\1\u0082",
			"",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"\1\u0084",
			"",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"\1\u0086",
			"\1\u0087",
			"\1\u0088",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"\1\u008a",
			"",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"\1\u008c",
			"\1\u008d",
			"",
			"\1\u008e",
			"",
			"\1\u008f",
			"",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"\1\u0091",
			"\1\u0092",
			"",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"",
			"\1\u0094",
			"\1\u0095",
			"\1\u0096",
			"",
			"\1\u0097",
			"",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"",
			"\1\u009c",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"\1\u009f",
			"\1\u00a0",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"",
			"",
			"",
			"",
			"\1\u00a2",
			"",
			"",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"\1\u00a4",
			"",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"",
			"\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
			"",
			""
	};

	static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
	static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
	static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
	static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
	static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
	static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
	static final short[][] DFA14_transition;

	static {
		int numStates = DFA14_transitionS.length;
		DFA14_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
		}
	}

	protected class DFA14 extends DFA {

		public DFA14(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 14;
			this.eot = DFA14_eot;
			this.eof = DFA14_eof;
			this.min = DFA14_min;
			this.max = DFA14_max;
			this.accept = DFA14_accept;
			this.special = DFA14_special;
			this.transition = DFA14_transition;
		}
		@Override
		public String getDescription() {
			return "1:1: Tokens : ( ALAP | ANY | ATOM | BOOL | CHOICE | DO | ELSE | FALSE | FUNCTION | IF | IMPORT | INT | NODE | OR | OTHER | OUT | REAL | PACKAGE | PRIORITY | RECIPE | STAR | STRING | TRY | TRUE | UNTIL | WHILE | INT_LIT | REAL_LIT | STRING_LIT | ID | AMP | BECOMES | DOT | NOT | BAR | SHARP | PLUS | ASTERISK | DONT_CARE | MINUS | QUOTE | BQUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LANGLE | RANGLE | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS );";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			IntStream input = _input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA14_19 = input.LA(1);
						s = -1;
						if ( ((LA14_19 >= '\u0000' && LA14_19 <= '\t')||(LA14_19 >= '\u000B' && LA14_19 <= '\f')||(LA14_19 >= '\u000E' && LA14_19 <= '\uFFFF')) ) {s = 70;}
						else s = 69;
						if ( s>=0 ) return s;
						break;
			}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 14, _s, input);
			error(nvae);
			throw nvae;
		}
	}

}
