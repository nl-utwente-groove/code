// $ANTLR 3.5.3 .\\Ctrl.g 2024-07-19 14:27:00

package nl.utwente.groove.control.parse;
import nl.utwente.groove.control.*;
import nl.utwente.groove.util.antlr.*;
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
	public static final int ARGS=7;
	public static final int ARG_CALL=8;
	public static final int ARG_ID=9;
	public static final int ARG_LIT=10;
	public static final int ARG_OP=11;
	public static final int ARG_OUT=12;
	public static final int ARG_WILD=13;
	public static final int ASTERISK=14;
	public static final int ATOM=15;
	public static final int BAR=16;
	public static final int BECOMES=17;
	public static final int BLOCK=18;
	public static final int BOOL=19;
	public static final int BQUOTE=20;
	public static final int BSLASH=21;
	public static final int CALL=22;
	public static final int CHOICE=23;
	public static final int COLON=24;
	public static final int COMMA=25;
	public static final int DO=26;
	public static final int DOT=27;
	public static final int DO_UNTIL=28;
	public static final int DO_WHILE=29;
	public static final int Digit=30;
	public static final int ELSE=31;
	public static final int EQ=32;
	public static final int EscapeSequence=33;
	public static final int FALSE=34;
	public static final int FUNCTION=35;
	public static final int FUNCTIONS=36;
	public static final int GEQ=37;
	public static final int HALT=38;
	public static final int ID=39;
	public static final int IF=40;
	public static final int IMPORT=41;
	public static final int IMPORTS=42;
	public static final int INT=43;
	public static final int INT_LIT=44;
	public static final int IntegerNumber=45;
	public static final int LANGLE=46;
	public static final int LCURLY=47;
	public static final int LEQ=48;
	public static final int LPAR=49;
	public static final int Letter=50;
	public static final int MINUS=51;
	public static final int ML_COMMENT=52;
	public static final int NEQ=53;
	public static final int NODE=54;
	public static final int NOT=55;
	public static final int NonIntegerNumber=56;
	public static final int OR=57;
	public static final int OTHER=58;
	public static final int OUT=59;
	public static final int PACKAGE=60;
	public static final int PAR=61;
	public static final int PARS=62;
	public static final int PERCENT=63;
	public static final int PLUS=64;
	public static final int PRIORITY=65;
	public static final int PROGRAM=66;
	public static final int PosDigit=67;
	public static final int QUOTE=68;
	public static final int RANGLE=69;
	public static final int RCURLY=70;
	public static final int REAL=71;
	public static final int REAL_LIT=72;
	public static final int RECIPE=73;
	public static final int RECIPES=74;
	public static final int RPAR=75;
	public static final int SEMI=76;
	public static final int SHARP=77;
	public static final int SLASH=78;
	public static final int SL_COMMENT=79;
	public static final int STAR=80;
	public static final int STRING=81;
	public static final int STRING_LIT=82;
	public static final int TRUE=83;
	public static final int TRY=84;
	public static final int UNDER=85;
	public static final int UNTIL=86;
	public static final int VAR=87;
	public static final int WHILE=88;
	public static final int WS=89;

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
	@Override public String getGrammarFileName() { return ".\\Ctrl.g"; }

	// $ANTLR start "ALAP"
	public final void mALAP() throws RecognitionException {
		try {
			int _type = ALAP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// .\\Ctrl.g:503:10: ( 'alap' )
			// .\\Ctrl.g:503:12: 'alap'
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
			// .\\Ctrl.g:504:6: ( 'any' )
			// .\\Ctrl.g:504:8: 'any'
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
			// .\\Ctrl.g:505:10: ( 'atomic' )
			// .\\Ctrl.g:505:12: 'atomic'
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
			// .\\Ctrl.g:506:10: ( 'bool' )
			// .\\Ctrl.g:506:12: 'bool'
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
			// .\\Ctrl.g:507:10: ( 'choice' )
			// .\\Ctrl.g:507:12: 'choice'
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
			// .\\Ctrl.g:508:10: ( 'do' )
			// .\\Ctrl.g:508:12: 'do'
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
			// .\\Ctrl.g:509:10: ( 'else' )
			// .\\Ctrl.g:509:12: 'else'
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
			// .\\Ctrl.g:510:10: ( 'false' )
			// .\\Ctrl.g:510:12: 'false'
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
			// .\\Ctrl.g:511:10: ( 'function' )
			// .\\Ctrl.g:511:12: 'function'
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

	// $ANTLR start "HALT"
	public final void mHALT() throws RecognitionException {
		try {
			int _type = HALT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// .\\Ctrl.g:512:10: ( 'halt' )
			// .\\Ctrl.g:512:12: 'halt'
			{
			match("halt"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "HALT"

	// $ANTLR start "IF"
	public final void mIF() throws RecognitionException {
		try {
			int _type = IF;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// .\\Ctrl.g:513:10: ( 'if' )
			// .\\Ctrl.g:513:12: 'if'
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
			// .\\Ctrl.g:514:10: ( 'import' )
			// .\\Ctrl.g:514:12: 'import'
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
			// .\\Ctrl.g:515:10: ( 'int' )
			// .\\Ctrl.g:515:12: 'int'
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
			// .\\Ctrl.g:516:10: ( 'node' )
			// .\\Ctrl.g:516:12: 'node'
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
			// .\\Ctrl.g:517:10: ( 'or' )
			// .\\Ctrl.g:517:12: 'or'
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
			// .\\Ctrl.g:518:10: ( 'other' )
			// .\\Ctrl.g:518:12: 'other'
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
			// .\\Ctrl.g:519:6: ( 'out' )
			// .\\Ctrl.g:519:8: 'out'
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
			// .\\Ctrl.g:520:10: ( 'real' )
			// .\\Ctrl.g:520:12: 'real'
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
			// .\\Ctrl.g:521:10: ( 'package' )
			// .\\Ctrl.g:521:12: 'package'
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
			// .\\Ctrl.g:522:10: ( 'priority' )
			// .\\Ctrl.g:522:12: 'priority'
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
			// .\\Ctrl.g:523:10: ( 'recipe' )
			// .\\Ctrl.g:523:12: 'recipe'
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
			// .\\Ctrl.g:524:10: ( 'star' )
			// .\\Ctrl.g:524:12: 'star'
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
			// .\\Ctrl.g:525:10: ( 'string' )
			// .\\Ctrl.g:525:12: 'string'
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
			// .\\Ctrl.g:526:10: ( 'try' )
			// .\\Ctrl.g:526:12: 'try'
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
			// .\\Ctrl.g:527:10: ( 'true' )
			// .\\Ctrl.g:527:12: 'true'
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
			// .\\Ctrl.g:528:10: ( 'until' )
			// .\\Ctrl.g:528:12: 'until'
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
			// .\\Ctrl.g:529:10: ( 'while' )
			// .\\Ctrl.g:529:12: 'while'
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

	// $ANTLR start "Digit"
	public final void mDigit() throws RecognitionException {
		try {
			// .\\Ctrl.g:531:16: ( '0' .. '9' )
			// .\\Ctrl.g:
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

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Digit"

	// $ANTLR start "PosDigit"
	public final void mPosDigit() throws RecognitionException {
		try {
			// .\\Ctrl.g:532:19: ( '1' .. '9' )
			// .\\Ctrl.g:
			{
			if ( (input.LA(1) >= '1' && input.LA(1) <= '9') ) {
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
	// $ANTLR end "PosDigit"

	// $ANTLR start "Letter"
	public final void mLetter() throws RecognitionException {
		try {
			// .\\Ctrl.g:533:17: ( 'a' .. 'z' | 'A' .. 'Z' )
			// .\\Ctrl.g:
			{
			if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
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
	// $ANTLR end "Letter"

	// $ANTLR start "IntegerNumber"
	public final void mIntegerNumber() throws RecognitionException {
		try {
			// .\\Ctrl.g:535:3: ( '0' | PosDigit ( Digit )* )
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
					// .\\Ctrl.g:535:5: '0'
					{
					match('0'); 
					}
					break;
				case 2 :
					// .\\Ctrl.g:536:5: PosDigit ( Digit )*
					{
					mPosDigit(); 

					// .\\Ctrl.g:536:14: ( Digit )*
					loop1:
					while (true) {
						int alt1=2;
						int LA1_0 = input.LA(1);
						if ( ((LA1_0 >= '0' && LA1_0 <= '9')) ) {
							alt1=1;
						}

						switch (alt1) {
						case 1 :
							// .\\Ctrl.g:
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

	// $ANTLR start "NonIntegerNumber"
	public final void mNonIntegerNumber() throws RecognitionException {
		try {
			// .\\Ctrl.g:539:5: ( IntegerNumber '.' ( Digit )* | '.' ( Digit )+ )
			int alt5=2;
			int LA5_0 = input.LA(1);
			if ( ((LA5_0 >= '0' && LA5_0 <= '9')) ) {
				alt5=1;
			}
			else if ( (LA5_0=='.') ) {
				alt5=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 5, 0, input);
				throw nvae;
			}

			switch (alt5) {
				case 1 :
					// .\\Ctrl.g:539:9: IntegerNumber '.' ( Digit )*
					{
					mIntegerNumber(); 

					match('.'); 
					// .\\Ctrl.g:539:27: ( Digit )*
					loop3:
					while (true) {
						int alt3=2;
						int LA3_0 = input.LA(1);
						if ( ((LA3_0 >= '0' && LA3_0 <= '9')) ) {
							alt3=1;
						}

						switch (alt3) {
						case 1 :
							// .\\Ctrl.g:
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
							break loop3;
						}
					}

					}
					break;
				case 2 :
					// .\\Ctrl.g:540:9: '.' ( Digit )+
					{
					match('.'); 
					// .\\Ctrl.g:540:13: ( Digit )+
					int cnt4=0;
					loop4:
					while (true) {
						int alt4=2;
						int LA4_0 = input.LA(1);
						if ( ((LA4_0 >= '0' && LA4_0 <= '9')) ) {
							alt4=1;
						}

						switch (alt4) {
						case 1 :
							// .\\Ctrl.g:
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
							if ( cnt4 >= 1 ) break loop4;
							EarlyExitException eee = new EarlyExitException(4, input);
							throw eee;
						}
						cnt4++;
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

	// $ANTLR start "EscapeSequence"
	public final void mEscapeSequence() throws RecognitionException {
		try {
			// .\\Ctrl.g:543:3: ( BSLASH ( QUOTE | BSLASH ) )
			// .\\Ctrl.g:543:5: BSLASH ( QUOTE | BSLASH )
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

	// $ANTLR start "INT_LIT"
	public final void mINT_LIT() throws RecognitionException {
		try {
			int _type = INT_LIT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// .\\Ctrl.g:550:3: ( IntegerNumber )
			// .\\Ctrl.g:550:5: IntegerNumber
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

	// $ANTLR start "REAL_LIT"
	public final void mREAL_LIT() throws RecognitionException {
		try {
			int _type = REAL_LIT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// .\\Ctrl.g:554:3: ( NonIntegerNumber )
			// .\\Ctrl.g:554:5: NonIntegerNumber
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

	// $ANTLR start "STRING_LIT"
	public final void mSTRING_LIT() throws RecognitionException {
		try {
			int _type = STRING_LIT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// .\\Ctrl.g:559:3: ( QUOTE ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE )
			// .\\Ctrl.g:559:5: QUOTE ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE
			{
			mQUOTE(); 

			// .\\Ctrl.g:560:5: ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )*
			loop6:
			while (true) {
				int alt6=3;
				int LA6_0 = input.LA(1);
				if ( (LA6_0=='\\') ) {
					alt6=1;
				}
				else if ( ((LA6_0 >= '\u0000' && LA6_0 <= '\t')||(LA6_0 >= '\u000B' && LA6_0 <= '\f')||(LA6_0 >= '\u000E' && LA6_0 <= '!')||(LA6_0 >= '#' && LA6_0 <= '[')||(LA6_0 >= ']' && LA6_0 <= '\uFFFF')) ) {
					alt6=2;
				}

				switch (alt6) {
				case 1 :
					// .\\Ctrl.g:560:7: EscapeSequence
					{
					mEscapeSequence(); 

					}
					break;
				case 2 :
					// .\\Ctrl.g:561:7: ~ ( BSLASH | QUOTE | '\\r' | '\\n' )
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
					break loop6;
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

	// $ANTLR start "ID"
	public final void mID() throws RecognitionException {
		try {
			int _type = ID;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// .\\Ctrl.g:567:3: ( Letter ( Letter | Digit | UNDER | MINUS )* | BQUOTE Letter ( Letter | Digit | UNDER | MINUS )* BQUOTE )
			int alt9=2;
			int LA9_0 = input.LA(1);
			if ( ((LA9_0 >= 'A' && LA9_0 <= 'Z')||(LA9_0 >= 'a' && LA9_0 <= 'z')) ) {
				alt9=1;
			}
			else if ( (LA9_0=='`') ) {
				alt9=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 9, 0, input);
				throw nvae;
			}

			switch (alt9) {
				case 1 :
					// .\\Ctrl.g:567:5: Letter ( Letter | Digit | UNDER | MINUS )*
					{
					mLetter(); 

					// .\\Ctrl.g:567:12: ( Letter | Digit | UNDER | MINUS )*
					loop7:
					while (true) {
						int alt7=2;
						int LA7_0 = input.LA(1);
						if ( (LA7_0=='-'||(LA7_0 >= '0' && LA7_0 <= '9')||(LA7_0 >= 'A' && LA7_0 <= 'Z')||LA7_0=='_'||(LA7_0 >= 'a' && LA7_0 <= 'z')) ) {
							alt7=1;
						}

						switch (alt7) {
						case 1 :
							// .\\Ctrl.g:
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
							break loop7;
						}
					}

					}
					break;
				case 2 :
					// .\\Ctrl.g:568:5: BQUOTE Letter ( Letter | Digit | UNDER | MINUS )* BQUOTE
					{
					mBQUOTE(); 

					mLetter(); 

					// .\\Ctrl.g:568:19: ( Letter | Digit | UNDER | MINUS )*
					loop8:
					while (true) {
						int alt8=2;
						int LA8_0 = input.LA(1);
						if ( (LA8_0=='-'||(LA8_0 >= '0' && LA8_0 <= '9')||(LA8_0 >= 'A' && LA8_0 <= 'Z')||LA8_0=='_'||(LA8_0 >= 'a' && LA8_0 <= 'z')) ) {
							alt8=1;
						}

						switch (alt8) {
						case 1 :
							// .\\Ctrl.g:
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
			// .\\Ctrl.g:571:11: ( '&' )
			// .\\Ctrl.g:571:13: '&'
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
			// .\\Ctrl.g:572:11: ( ':=' )
			// .\\Ctrl.g:572:13: ':='
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
			// .\\Ctrl.g:573:11: ( '.' )
			// .\\Ctrl.g:573:13: '.'
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
			// .\\Ctrl.g:574:11: ( '!' )
			// .\\Ctrl.g:574:13: '!'
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
			// .\\Ctrl.g:575:11: ( '|' )
			// .\\Ctrl.g:575:13: '|'
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
			// .\\Ctrl.g:576:11: ( '#' )
			// .\\Ctrl.g:576:13: '#'
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
			// .\\Ctrl.g:577:11: ( '+' )
			// .\\Ctrl.g:577:13: '+'
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
			// .\\Ctrl.g:578:11: ( '*' )
			// .\\Ctrl.g:578:13: '*'
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

	// $ANTLR start "PERCENT"
	public final void mPERCENT() throws RecognitionException {
		try {
			int _type = PERCENT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// .\\Ctrl.g:579:11: ( '%' )
			// .\\Ctrl.g:579:13: '%'
			{
			match('%'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "PERCENT"

	// $ANTLR start "UNDER"
	public final void mUNDER() throws RecognitionException {
		try {
			int _type = UNDER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// .\\Ctrl.g:580:11: ( '_' )
			// .\\Ctrl.g:580:13: '_'
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
	// $ANTLR end "UNDER"

	// $ANTLR start "MINUS"
	public final void mMINUS() throws RecognitionException {
		try {
			int _type = MINUS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// .\\Ctrl.g:581:11: ( '-' )
			// .\\Ctrl.g:581:13: '-'
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
			// .\\Ctrl.g:582:11: ( '\"' )
			// .\\Ctrl.g:582:13: '\"'
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
			// .\\Ctrl.g:583:11: ( '`' )
			// .\\Ctrl.g:583:13: '`'
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

	// $ANTLR start "SLASH"
	public final void mSLASH() throws RecognitionException {
		try {
			int _type = SLASH;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// .\\Ctrl.g:584:11: ( '/' )
			// .\\Ctrl.g:584:13: '/'
			{
			match('/'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "SLASH"

	// $ANTLR start "BSLASH"
	public final void mBSLASH() throws RecognitionException {
		try {
			int _type = BSLASH;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// .\\Ctrl.g:585:11: ( '\\\\' )
			// .\\Ctrl.g:585:13: '\\\\'
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
			// .\\Ctrl.g:586:11: ( ',' )
			// .\\Ctrl.g:586:13: ','
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

	// $ANTLR start "COLON"
	public final void mCOLON() throws RecognitionException {
		try {
			int _type = COLON;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// .\\Ctrl.g:587:11: ( ':' )
			// .\\Ctrl.g:587:13: ':'
			{
			match(':'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "COLON"

	// $ANTLR start "SEMI"
	public final void mSEMI() throws RecognitionException {
		try {
			int _type = SEMI;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// .\\Ctrl.g:588:11: ( ';' )
			// .\\Ctrl.g:588:13: ';'
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
			// .\\Ctrl.g:589:11: ( '(' )
			// .\\Ctrl.g:589:13: '('
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
			// .\\Ctrl.g:590:11: ( ')' )
			// .\\Ctrl.g:590:13: ')'
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
			// .\\Ctrl.g:591:11: ( '<' )
			// .\\Ctrl.g:591:13: '<'
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
			// .\\Ctrl.g:592:11: ( '>' )
			// .\\Ctrl.g:592:13: '>'
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

	// $ANTLR start "LEQ"
	public final void mLEQ() throws RecognitionException {
		try {
			int _type = LEQ;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// .\\Ctrl.g:593:11: ( '<=' )
			// .\\Ctrl.g:593:13: '<='
			{
			match("<="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LEQ"

	// $ANTLR start "GEQ"
	public final void mGEQ() throws RecognitionException {
		try {
			int _type = GEQ;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// .\\Ctrl.g:594:11: ( '>=' )
			// .\\Ctrl.g:594:13: '>='
			{
			match(">="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "GEQ"

	// $ANTLR start "EQ"
	public final void mEQ() throws RecognitionException {
		try {
			int _type = EQ;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// .\\Ctrl.g:595:11: ( '==' )
			// .\\Ctrl.g:595:13: '=='
			{
			match("=="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "EQ"

	// $ANTLR start "NEQ"
	public final void mNEQ() throws RecognitionException {
		try {
			int _type = NEQ;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// .\\Ctrl.g:596:11: ( '!=' )
			// .\\Ctrl.g:596:13: '!='
			{
			match("!="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NEQ"

	// $ANTLR start "LCURLY"
	public final void mLCURLY() throws RecognitionException {
		try {
			int _type = LCURLY;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// .\\Ctrl.g:597:11: ( '{' )
			// .\\Ctrl.g:597:13: '{'
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
			// .\\Ctrl.g:598:11: ( '}' )
			// .\\Ctrl.g:598:13: '}'
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
			// .\\Ctrl.g:600:12: ( '/*' ( options {greedy=false; } : . )* '*/' )
			// .\\Ctrl.g:600:14: '/*' ( options {greedy=false; } : . )* '*/'
			{
			match("/*"); 

			// .\\Ctrl.g:600:19: ( options {greedy=false; } : . )*
			loop10:
			while (true) {
				int alt10=2;
				int LA10_0 = input.LA(1);
				if ( (LA10_0=='*') ) {
					int LA10_1 = input.LA(2);
					if ( (LA10_1=='/') ) {
						alt10=2;
					}
					else if ( ((LA10_1 >= '\u0000' && LA10_1 <= '.')||(LA10_1 >= '0' && LA10_1 <= '\uFFFF')) ) {
						alt10=1;
					}

				}
				else if ( ((LA10_0 >= '\u0000' && LA10_0 <= ')')||(LA10_0 >= '+' && LA10_0 <= '\uFFFF')) ) {
					alt10=1;
				}

				switch (alt10) {
				case 1 :
					// .\\Ctrl.g:600:47: .
					{
					matchAny(); 
					}
					break;

				default :
					break loop10;
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
			// .\\Ctrl.g:601:12: ( '//' (~ ( '\\n' ) )* )
			// .\\Ctrl.g:601:14: '//' (~ ( '\\n' ) )*
			{
			match("//"); 

			// .\\Ctrl.g:601:19: (~ ( '\\n' ) )*
			loop11:
			while (true) {
				int alt11=2;
				int LA11_0 = input.LA(1);
				if ( ((LA11_0 >= '\u0000' && LA11_0 <= '\t')||(LA11_0 >= '\u000B' && LA11_0 <= '\uFFFF')) ) {
					alt11=1;
				}

				switch (alt11) {
				case 1 :
					// .\\Ctrl.g:
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
					break loop11;
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
			// .\\Ctrl.g:603:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
			// .\\Ctrl.g:603:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
			{
			// .\\Ctrl.g:603:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
			int cnt12=0;
			loop12:
			while (true) {
				int alt12=2;
				int LA12_0 = input.LA(1);
				if ( ((LA12_0 >= '\t' && LA12_0 <= '\n')||LA12_0=='\r'||LA12_0==' ') ) {
					alt12=1;
				}

				switch (alt12) {
				case 1 :
					// .\\Ctrl.g:
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
					if ( cnt12 >= 1 ) break loop12;
					EarlyExitException eee = new EarlyExitException(12, input);
					throw eee;
				}
				cnt12++;
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
		// .\\Ctrl.g:1:8: ( ALAP | ANY | ATOM | BOOL | CHOICE | DO | ELSE | FALSE | FUNCTION | HALT | IF | IMPORT | INT | NODE | OR | OTHER | OUT | REAL | PACKAGE | PRIORITY | RECIPE | STAR | STRING | TRY | TRUE | UNTIL | WHILE | INT_LIT | REAL_LIT | STRING_LIT | ID | AMP | BECOMES | DOT | NOT | BAR | SHARP | PLUS | ASTERISK | PERCENT | UNDER | MINUS | QUOTE | BQUOTE | SLASH | BSLASH | COMMA | COLON | SEMI | LPAR | RPAR | LANGLE | RANGLE | LEQ | GEQ | EQ | NEQ | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS )
		int alt13=62;
		alt13 = dfa13.predict(input);
		switch (alt13) {
			case 1 :
				// .\\Ctrl.g:1:10: ALAP
				{
				mALAP(); 

				}
				break;
			case 2 :
				// .\\Ctrl.g:1:15: ANY
				{
				mANY(); 

				}
				break;
			case 3 :
				// .\\Ctrl.g:1:19: ATOM
				{
				mATOM(); 

				}
				break;
			case 4 :
				// .\\Ctrl.g:1:24: BOOL
				{
				mBOOL(); 

				}
				break;
			case 5 :
				// .\\Ctrl.g:1:29: CHOICE
				{
				mCHOICE(); 

				}
				break;
			case 6 :
				// .\\Ctrl.g:1:36: DO
				{
				mDO(); 

				}
				break;
			case 7 :
				// .\\Ctrl.g:1:39: ELSE
				{
				mELSE(); 

				}
				break;
			case 8 :
				// .\\Ctrl.g:1:44: FALSE
				{
				mFALSE(); 

				}
				break;
			case 9 :
				// .\\Ctrl.g:1:50: FUNCTION
				{
				mFUNCTION(); 

				}
				break;
			case 10 :
				// .\\Ctrl.g:1:59: HALT
				{
				mHALT(); 

				}
				break;
			case 11 :
				// .\\Ctrl.g:1:64: IF
				{
				mIF(); 

				}
				break;
			case 12 :
				// .\\Ctrl.g:1:67: IMPORT
				{
				mIMPORT(); 

				}
				break;
			case 13 :
				// .\\Ctrl.g:1:74: INT
				{
				mINT(); 

				}
				break;
			case 14 :
				// .\\Ctrl.g:1:78: NODE
				{
				mNODE(); 

				}
				break;
			case 15 :
				// .\\Ctrl.g:1:83: OR
				{
				mOR(); 

				}
				break;
			case 16 :
				// .\\Ctrl.g:1:86: OTHER
				{
				mOTHER(); 

				}
				break;
			case 17 :
				// .\\Ctrl.g:1:92: OUT
				{
				mOUT(); 

				}
				break;
			case 18 :
				// .\\Ctrl.g:1:96: REAL
				{
				mREAL(); 

				}
				break;
			case 19 :
				// .\\Ctrl.g:1:101: PACKAGE
				{
				mPACKAGE(); 

				}
				break;
			case 20 :
				// .\\Ctrl.g:1:109: PRIORITY
				{
				mPRIORITY(); 

				}
				break;
			case 21 :
				// .\\Ctrl.g:1:118: RECIPE
				{
				mRECIPE(); 

				}
				break;
			case 22 :
				// .\\Ctrl.g:1:125: STAR
				{
				mSTAR(); 

				}
				break;
			case 23 :
				// .\\Ctrl.g:1:130: STRING
				{
				mSTRING(); 

				}
				break;
			case 24 :
				// .\\Ctrl.g:1:137: TRY
				{
				mTRY(); 

				}
				break;
			case 25 :
				// .\\Ctrl.g:1:141: TRUE
				{
				mTRUE(); 

				}
				break;
			case 26 :
				// .\\Ctrl.g:1:146: UNTIL
				{
				mUNTIL(); 

				}
				break;
			case 27 :
				// .\\Ctrl.g:1:152: WHILE
				{
				mWHILE(); 

				}
				break;
			case 28 :
				// .\\Ctrl.g:1:158: INT_LIT
				{
				mINT_LIT(); 

				}
				break;
			case 29 :
				// .\\Ctrl.g:1:166: REAL_LIT
				{
				mREAL_LIT(); 

				}
				break;
			case 30 :
				// .\\Ctrl.g:1:175: STRING_LIT
				{
				mSTRING_LIT(); 

				}
				break;
			case 31 :
				// .\\Ctrl.g:1:186: ID
				{
				mID(); 

				}
				break;
			case 32 :
				// .\\Ctrl.g:1:189: AMP
				{
				mAMP(); 

				}
				break;
			case 33 :
				// .\\Ctrl.g:1:193: BECOMES
				{
				mBECOMES(); 

				}
				break;
			case 34 :
				// .\\Ctrl.g:1:201: DOT
				{
				mDOT(); 

				}
				break;
			case 35 :
				// .\\Ctrl.g:1:205: NOT
				{
				mNOT(); 

				}
				break;
			case 36 :
				// .\\Ctrl.g:1:209: BAR
				{
				mBAR(); 

				}
				break;
			case 37 :
				// .\\Ctrl.g:1:213: SHARP
				{
				mSHARP(); 

				}
				break;
			case 38 :
				// .\\Ctrl.g:1:219: PLUS
				{
				mPLUS(); 

				}
				break;
			case 39 :
				// .\\Ctrl.g:1:224: ASTERISK
				{
				mASTERISK(); 

				}
				break;
			case 40 :
				// .\\Ctrl.g:1:233: PERCENT
				{
				mPERCENT(); 

				}
				break;
			case 41 :
				// .\\Ctrl.g:1:241: UNDER
				{
				mUNDER(); 

				}
				break;
			case 42 :
				// .\\Ctrl.g:1:247: MINUS
				{
				mMINUS(); 

				}
				break;
			case 43 :
				// .\\Ctrl.g:1:253: QUOTE
				{
				mQUOTE(); 

				}
				break;
			case 44 :
				// .\\Ctrl.g:1:259: BQUOTE
				{
				mBQUOTE(); 

				}
				break;
			case 45 :
				// .\\Ctrl.g:1:266: SLASH
				{
				mSLASH(); 

				}
				break;
			case 46 :
				// .\\Ctrl.g:1:272: BSLASH
				{
				mBSLASH(); 

				}
				break;
			case 47 :
				// .\\Ctrl.g:1:279: COMMA
				{
				mCOMMA(); 

				}
				break;
			case 48 :
				// .\\Ctrl.g:1:285: COLON
				{
				mCOLON(); 

				}
				break;
			case 49 :
				// .\\Ctrl.g:1:291: SEMI
				{
				mSEMI(); 

				}
				break;
			case 50 :
				// .\\Ctrl.g:1:296: LPAR
				{
				mLPAR(); 

				}
				break;
			case 51 :
				// .\\Ctrl.g:1:301: RPAR
				{
				mRPAR(); 

				}
				break;
			case 52 :
				// .\\Ctrl.g:1:306: LANGLE
				{
				mLANGLE(); 

				}
				break;
			case 53 :
				// .\\Ctrl.g:1:313: RANGLE
				{
				mRANGLE(); 

				}
				break;
			case 54 :
				// .\\Ctrl.g:1:320: LEQ
				{
				mLEQ(); 

				}
				break;
			case 55 :
				// .\\Ctrl.g:1:324: GEQ
				{
				mGEQ(); 

				}
				break;
			case 56 :
				// .\\Ctrl.g:1:328: EQ
				{
				mEQ(); 

				}
				break;
			case 57 :
				// .\\Ctrl.g:1:331: NEQ
				{
				mNEQ(); 

				}
				break;
			case 58 :
				// .\\Ctrl.g:1:335: LCURLY
				{
				mLCURLY(); 

				}
				break;
			case 59 :
				// .\\Ctrl.g:1:342: RCURLY
				{
				mRCURLY(); 

				}
				break;
			case 60 :
				// .\\Ctrl.g:1:349: ML_COMMENT
				{
				mML_COMMENT(); 

				}
				break;
			case 61 :
				// .\\Ctrl.g:1:360: SL_COMMENT
				{
				mSL_COMMENT(); 

				}
				break;
			case 62 :
				// .\\Ctrl.g:1:371: WS
				{
				mWS(); 

				}
				break;

		}
	}


	protected DFA13 dfa13 = new DFA13(this);
	static final String DFA13_eotS =
		"\1\uffff\20\25\2\105\1\110\1\111\1\uffff\1\113\1\uffff\1\115\1\117\7\uffff"+
		"\1\122\5\uffff\1\124\1\126\4\uffff\5\25\1\134\4\25\1\141\3\25\1\145\11"+
		"\25\2\uffff\1\105\17\uffff\1\25\1\163\3\25\1\uffff\4\25\1\uffff\1\25\1"+
		"\174\1\25\1\uffff\1\25\1\177\6\25\1\u0086\3\25\1\u008a\1\uffff\1\25\1"+
		"\u008c\1\25\1\u008e\2\25\1\u0091\1\25\1\uffff\1\u0093\1\25\1\uffff\1\u0095"+
		"\3\25\1\u0099\1\25\1\uffff\1\u009b\2\25\1\uffff\1\25\1\uffff\1\25\1\uffff"+
		"\1\u00a0\1\25\1\uffff\1\25\1\uffff\1\u00a3\1\uffff\3\25\1\uffff\1\25\1"+
		"\uffff\1\u00a8\1\u00a9\1\u00aa\1\u00ab\1\uffff\1\25\1\u00ad\1\uffff\1"+
		"\u00ae\2\25\1\u00b1\4\uffff\1\25\2\uffff\1\u00b3\1\25\1\uffff\1\u00b5"+
		"\1\uffff\1\u00b6\2\uffff";
	static final String DFA13_eofS =
		"\u00b7\uffff";
	static final String DFA13_minS =
		"\1\11\1\154\1\157\1\150\1\157\1\154\2\141\1\146\1\157\1\162\1\145\1\141"+
		"\1\164\1\162\1\156\1\150\2\56\1\60\1\0\1\uffff\1\101\1\uffff\2\75\7\uffff"+
		"\1\52\5\uffff\2\75\4\uffff\1\141\1\171\3\157\1\55\1\163\1\154\1\156\1"+
		"\154\1\55\1\160\1\164\1\144\1\55\1\150\1\164\1\141\1\143\1\151\1\141\1"+
		"\165\1\164\1\151\2\uffff\1\56\17\uffff\1\160\1\55\1\155\1\154\1\151\1"+
		"\uffff\1\145\1\163\1\143\1\164\1\uffff\1\157\1\55\1\145\1\uffff\1\145"+
		"\1\55\1\154\1\151\1\153\1\157\1\162\1\151\1\55\1\145\1\151\1\154\1\55"+
		"\1\uffff\1\151\1\55\1\143\1\55\1\145\1\164\1\55\1\162\1\uffff\1\55\1\162"+
		"\1\uffff\1\55\1\160\1\141\1\162\1\55\1\156\1\uffff\1\55\1\154\1\145\1"+
		"\uffff\1\143\1\uffff\1\145\1\uffff\1\55\1\151\1\uffff\1\164\1\uffff\1"+
		"\55\1\uffff\1\145\1\147\1\151\1\uffff\1\147\1\uffff\4\55\1\uffff\1\157"+
		"\1\55\1\uffff\1\55\1\145\1\164\1\55\4\uffff\1\156\2\uffff\1\55\1\171\1"+
		"\uffff\1\55\1\uffff\1\55\2\uffff";
	static final String DFA13_maxS =
		"\1\175\1\164\1\157\1\150\1\157\1\154\1\165\1\141\1\156\1\157\1\165\1\145"+
		"\1\162\1\164\1\162\1\156\1\150\1\56\2\71\1\uffff\1\uffff\1\172\1\uffff"+
		"\2\75\7\uffff\1\57\5\uffff\2\75\4\uffff\1\141\1\171\3\157\1\172\1\163"+
		"\1\154\1\156\1\154\1\172\1\160\1\164\1\144\1\172\1\150\1\164\2\143\1\151"+
		"\1\162\1\171\1\164\1\151\2\uffff\1\71\17\uffff\1\160\1\172\1\155\1\154"+
		"\1\151\1\uffff\1\145\1\163\1\143\1\164\1\uffff\1\157\1\172\1\145\1\uffff"+
		"\1\145\1\172\1\154\1\151\1\153\1\157\1\162\1\151\1\172\1\145\1\151\1\154"+
		"\1\172\1\uffff\1\151\1\172\1\143\1\172\1\145\1\164\1\172\1\162\1\uffff"+
		"\1\172\1\162\1\uffff\1\172\1\160\1\141\1\162\1\172\1\156\1\uffff\1\172"+
		"\1\154\1\145\1\uffff\1\143\1\uffff\1\145\1\uffff\1\172\1\151\1\uffff\1"+
		"\164\1\uffff\1\172\1\uffff\1\145\1\147\1\151\1\uffff\1\147\1\uffff\4\172"+
		"\1\uffff\1\157\1\172\1\uffff\1\172\1\145\1\164\1\172\4\uffff\1\156\2\uffff"+
		"\1\172\1\171\1\uffff\1\172\1\uffff\1\172\2\uffff";
	static final String DFA13_acceptS =
		"\25\uffff\1\37\1\uffff\1\40\2\uffff\1\44\1\45\1\46\1\47\1\50\1\51\1\52"+
		"\1\uffff\1\56\1\57\1\61\1\62\1\63\2\uffff\1\70\1\72\1\73\1\76\30\uffff"+
		"\1\34\1\35\1\uffff\1\42\1\53\1\36\1\54\1\41\1\60\1\71\1\43\1\74\1\75\1"+
		"\55\1\66\1\64\1\67\1\65\5\uffff\1\6\4\uffff\1\13\3\uffff\1\17\15\uffff"+
		"\1\2\10\uffff\1\15\2\uffff\1\21\6\uffff\1\30\3\uffff\1\1\1\uffff\1\4\1"+
		"\uffff\1\7\2\uffff\1\12\1\uffff\1\16\1\uffff\1\22\3\uffff\1\26\1\uffff"+
		"\1\31\4\uffff\1\10\2\uffff\1\20\4\uffff\1\32\1\33\1\3\1\5\1\uffff\1\14"+
		"\1\25\2\uffff\1\27\1\uffff\1\23\1\uffff\1\11\1\24";
	static final String DFA13_specialS =
		"\24\uffff\1\0\u00a2\uffff}>";
	static final String[] DFA13_transitionS = {
			"\2\54\2\uffff\1\54\22\uffff\1\54\1\31\1\24\1\33\1\uffff\1\36\1\27\1\uffff"+
			"\1\45\1\46\1\35\1\34\1\43\1\40\1\23\1\41\1\21\11\22\1\30\1\44\1\47\1"+
			"\51\1\50\2\uffff\32\25\1\uffff\1\42\2\uffff\1\37\1\26\1\1\1\2\1\3\1\4"+
			"\1\5\1\6\1\25\1\7\1\10\4\25\1\11\1\12\1\14\1\25\1\13\1\15\1\16\1\17\1"+
			"\25\1\20\3\25\1\52\1\32\1\53",
			"\1\55\1\uffff\1\56\5\uffff\1\57",
			"\1\60",
			"\1\61",
			"\1\62",
			"\1\63",
			"\1\64\23\uffff\1\65",
			"\1\66",
			"\1\67\6\uffff\1\70\1\71",
			"\1\72",
			"\1\73\1\uffff\1\74\1\75",
			"\1\76",
			"\1\77\20\uffff\1\100",
			"\1\101",
			"\1\102",
			"\1\103",
			"\1\104",
			"\1\106",
			"\1\106\1\uffff\12\107",
			"\12\106",
			"\12\112\1\uffff\2\112\1\uffff\ufff2\112",
			"",
			"\32\25\6\uffff\32\25",
			"",
			"\1\114",
			"\1\116",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\120\4\uffff\1\121",
			"",
			"",
			"",
			"",
			"",
			"\1\123",
			"\1\125",
			"",
			"",
			"",
			"",
			"\1\127",
			"\1\130",
			"\1\131",
			"\1\132",
			"\1\133",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\135",
			"\1\136",
			"\1\137",
			"\1\140",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\142",
			"\1\143",
			"\1\144",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\146",
			"\1\147",
			"\1\150\1\uffff\1\151",
			"\1\152",
			"\1\153",
			"\1\154\20\uffff\1\155",
			"\1\157\3\uffff\1\156",
			"\1\160",
			"\1\161",
			"",
			"",
			"\1\106\1\uffff\12\107",
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
			"\1\162",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\164",
			"\1\165",
			"\1\166",
			"",
			"\1\167",
			"\1\170",
			"\1\171",
			"\1\172",
			"",
			"\1\173",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\175",
			"",
			"\1\176",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\u0080",
			"\1\u0081",
			"\1\u0082",
			"\1\u0083",
			"\1\u0084",
			"\1\u0085",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\u0087",
			"\1\u0088",
			"\1\u0089",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"",
			"\1\u008b",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\u008d",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\u008f",
			"\1\u0090",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\u0092",
			"",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\u0094",
			"",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\u0096",
			"\1\u0097",
			"\1\u0098",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\u009a",
			"",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\u009c",
			"\1\u009d",
			"",
			"\1\u009e",
			"",
			"\1\u009f",
			"",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\u00a1",
			"",
			"\1\u00a2",
			"",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"",
			"\1\u00a4",
			"\1\u00a5",
			"\1\u00a6",
			"",
			"\1\u00a7",
			"",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"",
			"\1\u00ac",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\u00af",
			"\1\u00b0",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"",
			"",
			"",
			"",
			"\1\u00b2",
			"",
			"",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"\1\u00b4",
			"",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"",
			"\1\25\2\uffff\12\25\7\uffff\32\25\4\uffff\1\25\1\uffff\32\25",
			"",
			""
	};

	static final short[] DFA13_eot = DFA.unpackEncodedString(DFA13_eotS);
	static final short[] DFA13_eof = DFA.unpackEncodedString(DFA13_eofS);
	static final char[] DFA13_min = DFA.unpackEncodedStringToUnsignedChars(DFA13_minS);
	static final char[] DFA13_max = DFA.unpackEncodedStringToUnsignedChars(DFA13_maxS);
	static final short[] DFA13_accept = DFA.unpackEncodedString(DFA13_acceptS);
	static final short[] DFA13_special = DFA.unpackEncodedString(DFA13_specialS);
	static final short[][] DFA13_transition;

	static {
		int numStates = DFA13_transitionS.length;
		DFA13_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA13_transition[i] = DFA.unpackEncodedString(DFA13_transitionS[i]);
		}
	}

	protected class DFA13 extends DFA {

		public DFA13(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 13;
			this.eot = DFA13_eot;
			this.eof = DFA13_eof;
			this.min = DFA13_min;
			this.max = DFA13_max;
			this.accept = DFA13_accept;
			this.special = DFA13_special;
			this.transition = DFA13_transition;
		}
		@Override
		public String getDescription() {
			return "1:1: Tokens : ( ALAP | ANY | ATOM | BOOL | CHOICE | DO | ELSE | FALSE | FUNCTION | HALT | IF | IMPORT | INT | NODE | OR | OTHER | OUT | REAL | PACKAGE | PRIORITY | RECIPE | STAR | STRING | TRY | TRUE | UNTIL | WHILE | INT_LIT | REAL_LIT | STRING_LIT | ID | AMP | BECOMES | DOT | NOT | BAR | SHARP | PLUS | ASTERISK | PERCENT | UNDER | MINUS | QUOTE | BQUOTE | SLASH | BSLASH | COMMA | COLON | SEMI | LPAR | RPAR | LANGLE | RANGLE | LEQ | GEQ | EQ | NEQ | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS );";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			IntStream input = _input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA13_20 = input.LA(1);
						s = -1;
						if ( ((LA13_20 >= '\u0000' && LA13_20 <= '\t')||(LA13_20 >= '\u000B' && LA13_20 <= '\f')||(LA13_20 >= '\u000E' && LA13_20 <= '\uFFFF')) ) {s = 74;}
						else s = 73;
						if ( s>=0 ) return s;
						break;
			}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 13, _s, input);
			error(nvae);
			throw nvae;
		}
	}

}
