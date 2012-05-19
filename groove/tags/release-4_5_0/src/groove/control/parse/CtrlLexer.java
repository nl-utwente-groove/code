// $ANTLR 3.4 E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g 2012-02-13 21:37:31

package groove.control.parse;
import groove.control.*;
import java.util.LinkedList;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class CtrlLexer extends Lexer {
    public static final int EOF=-1;
    public static final int ALAP=4;
    public static final int AMP=5;
    public static final int ANY=6;
    public static final int ARG=7;
    public static final int ARGS=8;
    public static final int ASTERISK=9;
    public static final int BAR=10;
    public static final int BLOCK=11;
    public static final int BOOL=12;
    public static final int BSLASH=13;
    public static final int CALL=14;
    public static final int CHOICE=15;
    public static final int COMMA=16;
    public static final int DO=17;
    public static final int DONT_CARE=18;
    public static final int DOT=19;
    public static final int DO_UNTIL=20;
    public static final int DO_WHILE=21;
    public static final int ELSE=22;
    public static final int EscapeSequence=23;
    public static final int FALSE=24;
    public static final int FUNCTION=25;
    public static final int FUNCTIONS=26;
    public static final int ID=27;
    public static final int IF=28;
    public static final int INT=29;
    public static final int INT_LIT=30;
    public static final int IntegerNumber=31;
    public static final int LCURLY=32;
    public static final int LPAR=33;
    public static final int MINUS=34;
    public static final int ML_COMMENT=35;
    public static final int NODE=36;
    public static final int NOT=37;
    public static final int NonIntegerNumber=38;
    public static final int OR=39;
    public static final int OTHER=40;
    public static final int OUT=41;
    public static final int PLUS=42;
    public static final int PRIORITY=43;
    public static final int PROGRAM=44;
    public static final int QUOTE=45;
    public static final int RCURLY=46;
    public static final int REAL=47;
    public static final int REAL_LIT=48;
    public static final int RECIPE=49;
    public static final int RECIPES=50;
    public static final int RPAR=51;
    public static final int SEMI=52;
    public static final int SHARP=53;
    public static final int SL_COMMENT=54;
    public static final int STAR=55;
    public static final int STRING=56;
    public static final int STRING_LIT=57;
    public static final int TRUE=58;
    public static final int TRY=59;
    public static final int UNTIL=60;
    public static final int VAR=61;
    public static final int WHILE=62;
    public static final int WS=63;

        /** Last token read when start position is recorded. */
        private String lastToken;
        /** Start position of a recorded substring of the input. */
        private int recordPos;
        /** Helper class to convert AST trees to namespace. */
        private CtrlHelper helper;
        
        /** Starts recording the input string. */
        public void startRecord() {
            lastToken = this.state.token.getText();
            recordPos = getCharIndex();
        }
        
        public String getRecord() {
            org.antlr.runtime.Token currentToken = this.state.token;
            int currentTokenLength =
                currentToken == null ? 0 : currentToken.getText().length();
            return (this.lastToken + getCharStream().substring(this.recordPos,
                getCharIndex() - 1 - currentTokenLength)).trim();
        }
        
        public void setHelper(CtrlHelper helper) {
            this.helper = helper;
        }
        
        public void displayRecognitionError(String[] tokenNames,
                RecognitionException e) {
            String hdr = getErrorHeader(e);
            String msg = getErrorMessage(e, tokenNames);
            this.helper.addError(hdr + " " + msg, e.line, e.charPositionInLine);
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
    public String getGrammarFileName() { return "E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g"; }

    // $ANTLR start "ALAP"
    public final void mALAP() throws RecognitionException {
        try {
            int _type = ALAP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:353:10: ( 'alap' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:353:12: 'alap'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:354:9: ( 'any' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:354:11: 'any'
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

    // $ANTLR start "BOOL"
    public final void mBOOL() throws RecognitionException {
        try {
            int _type = BOOL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:355:10: ( 'bool' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:355:12: 'bool'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:356:10: ( 'choice' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:356:12: 'choice'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:357:10: ( 'do' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:357:12: 'do'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:358:10: ( 'else' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:358:12: 'else'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:359:10: ( 'false' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:359:12: 'false'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:360:10: ( 'function' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:360:12: 'function'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:361:10: ( 'if' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:361:12: 'if'
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

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:362:10: ( 'int' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:362:12: 'int'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:363:10: ( 'node' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:363:12: 'node'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:364:10: ( 'or' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:364:12: 'or'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:365:10: ( 'other' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:365:12: 'other'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:366:10: ( 'out' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:366:12: 'out'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:367:10: ( 'real' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:367:12: 'real'
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

    // $ANTLR start "PRIORITY"
    public final void mPRIORITY() throws RecognitionException {
        try {
            int _type = PRIORITY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:368:10: ( 'priority' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:368:12: 'priority'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:369:10: ( 'recipe' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:369:12: 'recipe'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:370:10: ( 'star' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:370:12: 'star'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:371:10: ( 'string' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:371:12: 'string'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:372:10: ( 'try' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:372:12: 'try'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:373:10: ( 'true' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:373:12: 'true'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:374:10: ( 'until' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:374:12: 'until'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:375:10: ( 'while' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:375:12: 'while'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:378:3: ( IntegerNumber )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:378:5: IntegerNumber
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:384:3: ( '0' | '1' .. '9' ( '0' .. '9' )* )
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
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:384:5: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:385:5: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 

                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:385:14: ( '0' .. '9' )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0 >= '0' && LA1_0 <= '9')) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:
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
                    } while (true);


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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:388:3: ( NonIntegerNumber )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:388:5: NonIntegerNumber
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:394:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* | '.' ( '0' .. '9' )+ )
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
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:394:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )*
                    {
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:394:9: ( '0' .. '9' )+
                    int cnt3=0;
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0 >= '0' && LA3_0 <= '9')) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:
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
                                EarlyExitException eee =
                                    new EarlyExitException(3, input);
                                throw eee;
                        }
                        cnt3++;
                    } while (true);


                    match('.'); 

                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:394:27: ( '0' .. '9' )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0 >= '0' && LA4_0 <= '9')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:
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
                    } while (true);


                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:395:9: '.' ( '0' .. '9' )+
                    {
                    match('.'); 

                    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:395:13: ( '0' .. '9' )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( ((LA5_0 >= '0' && LA5_0 <= '9')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:
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
                                EarlyExitException eee =
                                    new EarlyExitException(5, input);
                                throw eee;
                        }
                        cnt5++;
                    } while (true);


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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:399:3: ( QUOTE ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:399:5: QUOTE ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE
            {
            mQUOTE(); 


            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:400:5: ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )*
            loop7:
            do {
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
            	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:400:7: EscapeSequence
            	    {
            	    mEscapeSequence(); 


            	    }
            	    break;
            	case 2 :
            	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:401:7: ~ ( BSLASH | QUOTE | '\\r' | '\\n' )
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
            } while (true);


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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:409:3: ( BSLASH ( QUOTE BSLASH ) )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:409:5: BSLASH ( QUOTE BSLASH )
            {
            mBSLASH(); 


            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:410:5: ( QUOTE BSLASH )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:410:7: QUOTE BSLASH
            {
            mQUOTE(); 


            mBSLASH(); 


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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:414:5: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )* )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:414:7: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:414:27: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0=='-'||(LA8_0 >= '0' && LA8_0 <= '9')||(LA8_0 >= 'A' && LA8_0 <= 'Z')||LA8_0=='_'||(LA8_0 >= 'a' && LA8_0 <= 'z')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:
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
            } while (true);


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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:416:11: ( '&' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:416:13: '&'
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

    // $ANTLR start "DOT"
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:417:11: ( '.' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:417:13: '.'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:418:11: ( '!' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:418:13: '!'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:419:11: ( '|' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:419:13: '|'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:420:11: ( '#' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:420:13: '#'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:421:11: ( '+' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:421:13: '+'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:422:11: ( '*' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:422:13: '*'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:423:11: ( '_' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:423:13: '_'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:424:11: ( '-' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:424:13: '-'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:425:11: ( '\"' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:425:13: '\"'
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

    // $ANTLR start "BSLASH"
    public final void mBSLASH() throws RecognitionException {
        try {
            int _type = BSLASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:426:11: ( '\\\\' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:426:13: '\\\\'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:427:11: ( ',' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:427:13: ','
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:428:11: ( ';' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:428:13: ';'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:429:11: ( '(' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:429:13: '('
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:430:11: ( ')' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:430:13: ')'
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

    // $ANTLR start "LCURLY"
    public final void mLCURLY() throws RecognitionException {
        try {
            int _type = LCURLY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:431:11: ( '{' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:431:13: '{'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:432:11: ( '}' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:432:13: '}'
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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:434:12: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:434:14: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 



            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:434:19: ( options {greedy=false; } : . )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0=='*') ) {
                    int LA9_1 = input.LA(2);

                    if ( (LA9_1=='/') ) {
                        alt9=2;
                    }
                    else if ( ((LA9_1 >= '\u0000' && LA9_1 <= '.')||(LA9_1 >= '0' && LA9_1 <= '\uFFFF')) ) {
                        alt9=1;
                    }


                }
                else if ( ((LA9_0 >= '\u0000' && LA9_0 <= ')')||(LA9_0 >= '+' && LA9_0 <= '\uFFFF')) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:434:47: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:435:12: ( '//' ( options {greedy=false; } : . )* '\\n' )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:435:14: '//' ( options {greedy=false; } : . )* '\\n'
            {
            match("//"); 



            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:435:19: ( options {greedy=false; } : . )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0=='\n') ) {
                    alt10=2;
                }
                else if ( ((LA10_0 >= '\u0000' && LA10_0 <= '\t')||(LA10_0 >= '\u000B' && LA10_0 <= '\uFFFF')) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:435:47: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            match('\n'); 

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
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:437:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:437:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:437:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            int cnt11=0;
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( ((LA11_0 >= '\t' && LA11_0 <= '\n')||LA11_0=='\r'||LA11_0==' ') ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:
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
            	    if ( cnt11 >= 1 ) break loop11;
                        EarlyExitException eee =
                            new EarlyExitException(11, input);
                        throw eee;
                }
                cnt11++;
            } while (true);


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

    public void mTokens() throws RecognitionException {
        // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:8: ( ALAP | ANY | BOOL | CHOICE | DO | ELSE | FALSE | FUNCTION | IF | INT | NODE | OR | OTHER | OUT | REAL | PRIORITY | RECIPE | STAR | STRING | TRY | TRUE | UNTIL | WHILE | INT_LIT | REAL_LIT | STRING_LIT | ID | AMP | DOT | NOT | BAR | SHARP | PLUS | ASTERISK | DONT_CARE | MINUS | QUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS )
        int alt12=47;
        alt12 = dfa12.predict(input);
        switch (alt12) {
            case 1 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:10: ALAP
                {
                mALAP(); 


                }
                break;
            case 2 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:15: ANY
                {
                mANY(); 


                }
                break;
            case 3 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:19: BOOL
                {
                mBOOL(); 


                }
                break;
            case 4 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:24: CHOICE
                {
                mCHOICE(); 


                }
                break;
            case 5 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:31: DO
                {
                mDO(); 


                }
                break;
            case 6 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:34: ELSE
                {
                mELSE(); 


                }
                break;
            case 7 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:39: FALSE
                {
                mFALSE(); 


                }
                break;
            case 8 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:45: FUNCTION
                {
                mFUNCTION(); 


                }
                break;
            case 9 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:54: IF
                {
                mIF(); 


                }
                break;
            case 10 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:57: INT
                {
                mINT(); 


                }
                break;
            case 11 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:61: NODE
                {
                mNODE(); 


                }
                break;
            case 12 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:66: OR
                {
                mOR(); 


                }
                break;
            case 13 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:69: OTHER
                {
                mOTHER(); 


                }
                break;
            case 14 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:75: OUT
                {
                mOUT(); 


                }
                break;
            case 15 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:79: REAL
                {
                mREAL(); 


                }
                break;
            case 16 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:84: PRIORITY
                {
                mPRIORITY(); 


                }
                break;
            case 17 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:93: RECIPE
                {
                mRECIPE(); 


                }
                break;
            case 18 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:100: STAR
                {
                mSTAR(); 


                }
                break;
            case 19 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:105: STRING
                {
                mSTRING(); 


                }
                break;
            case 20 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:112: TRY
                {
                mTRY(); 


                }
                break;
            case 21 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:116: TRUE
                {
                mTRUE(); 


                }
                break;
            case 22 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:121: UNTIL
                {
                mUNTIL(); 


                }
                break;
            case 23 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:127: WHILE
                {
                mWHILE(); 


                }
                break;
            case 24 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:133: INT_LIT
                {
                mINT_LIT(); 


                }
                break;
            case 25 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:141: REAL_LIT
                {
                mREAL_LIT(); 


                }
                break;
            case 26 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:150: STRING_LIT
                {
                mSTRING_LIT(); 


                }
                break;
            case 27 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:161: ID
                {
                mID(); 


                }
                break;
            case 28 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:164: AMP
                {
                mAMP(); 


                }
                break;
            case 29 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:168: DOT
                {
                mDOT(); 


                }
                break;
            case 30 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:172: NOT
                {
                mNOT(); 


                }
                break;
            case 31 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:176: BAR
                {
                mBAR(); 


                }
                break;
            case 32 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:180: SHARP
                {
                mSHARP(); 


                }
                break;
            case 33 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:186: PLUS
                {
                mPLUS(); 


                }
                break;
            case 34 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:191: ASTERISK
                {
                mASTERISK(); 


                }
                break;
            case 35 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:200: DONT_CARE
                {
                mDONT_CARE(); 


                }
                break;
            case 36 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:210: MINUS
                {
                mMINUS(); 


                }
                break;
            case 37 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:216: QUOTE
                {
                mQUOTE(); 


                }
                break;
            case 38 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:222: BSLASH
                {
                mBSLASH(); 


                }
                break;
            case 39 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:229: COMMA
                {
                mCOMMA(); 


                }
                break;
            case 40 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:235: SEMI
                {
                mSEMI(); 


                }
                break;
            case 41 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:240: LPAR
                {
                mLPAR(); 


                }
                break;
            case 42 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:245: RPAR
                {
                mRPAR(); 


                }
                break;
            case 43 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:250: LCURLY
                {
                mLCURLY(); 


                }
                break;
            case 44 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:257: RCURLY
                {
                mRCURLY(); 


                }
                break;
            case 45 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:264: ML_COMMENT
                {
                mML_COMMENT(); 


                }
                break;
            case 46 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:275: SL_COMMENT
                {
                mSL_COMMENT(); 


                }
                break;
            case 47 :
                // E:\\Eclipse\\groove-regexp\\src\\groove\\control\\parse\\Ctrl.g:1:286: WS
                {
                mWS(); 


                }
                break;

        }

    }


    protected DFA12 dfa12 = new DFA12(this);
    static final String DFA12_eotS =
        "\1\uffff\17\24\2\72\1\75\1\76\22\uffff\4\24\1\106\3\24\1\112\2\24"+
        "\1\115\10\24\2\uffff\1\72\5\uffff\1\24\1\132\2\24\1\uffff\3\24\1"+
        "\uffff\1\140\1\24\1\uffff\1\24\1\143\5\24\1\151\3\24\1\155\1\uffff"+
        "\1\156\1\24\1\160\2\24\1\uffff\1\163\1\24\1\uffff\1\165\2\24\1\170"+
        "\1\24\1\uffff\1\172\2\24\2\uffff\1\24\1\uffff\1\176\1\24\1\uffff"+
        "\1\u0080\1\uffff\2\24\1\uffff\1\24\1\uffff\1\u0084\1\u0085\1\u0086"+
        "\1\uffff\1\24\1\uffff\1\u0088\1\24\1\u008a\3\uffff\1\24\1\uffff"+
        "\1\24\1\uffff\1\u008d\1\u008e\2\uffff";
    static final String DFA12_eofS =
        "\u008f\uffff";
    static final String DFA12_minS =
        "\1\11\1\154\1\157\1\150\1\157\1\154\1\141\1\146\1\157\1\162\1\145"+
        "\1\162\1\164\1\162\1\156\1\150\2\56\1\60\1\0\20\uffff\1\52\1\uffff"+
        "\1\141\1\171\2\157\1\55\1\163\1\154\1\156\1\55\1\164\1\144\1\55"+
        "\1\150\1\164\1\141\1\151\1\141\1\165\1\164\1\151\2\uffff\1\56\5"+
        "\uffff\1\160\1\55\1\154\1\151\1\uffff\1\145\1\163\1\143\1\uffff"+
        "\1\55\1\145\1\uffff\1\145\1\55\1\154\1\151\1\157\1\162\1\151\1\55"+
        "\1\145\1\151\1\154\1\55\1\uffff\1\55\1\143\1\55\1\145\1\164\1\uffff"+
        "\1\55\1\162\1\uffff\1\55\1\160\1\162\1\55\1\156\1\uffff\1\55\1\154"+
        "\1\145\2\uffff\1\145\1\uffff\1\55\1\151\1\uffff\1\55\1\uffff\1\145"+
        "\1\151\1\uffff\1\147\1\uffff\3\55\1\uffff\1\157\1\uffff\1\55\1\164"+
        "\1\55\3\uffff\1\156\1\uffff\1\171\1\uffff\2\55\2\uffff";
    static final String DFA12_maxS =
        "\1\175\1\156\1\157\1\150\1\157\1\154\1\165\1\156\1\157\1\165\1\145"+
        "\1\162\1\164\1\162\1\156\1\150\3\71\1\uffff\20\uffff\1\57\1\uffff"+
        "\1\141\1\171\2\157\1\172\1\163\1\154\1\156\1\172\1\164\1\144\1\172"+
        "\1\150\1\164\1\143\1\151\1\162\1\171\1\164\1\151\2\uffff\1\71\5"+
        "\uffff\1\160\1\172\1\154\1\151\1\uffff\1\145\1\163\1\143\1\uffff"+
        "\1\172\1\145\1\uffff\1\145\1\172\1\154\1\151\1\157\1\162\1\151\1"+
        "\172\1\145\1\151\1\154\1\172\1\uffff\1\172\1\143\1\172\1\145\1\164"+
        "\1\uffff\1\172\1\162\1\uffff\1\172\1\160\1\162\1\172\1\156\1\uffff"+
        "\1\172\1\154\1\145\2\uffff\1\145\1\uffff\1\172\1\151\1\uffff\1\172"+
        "\1\uffff\1\145\1\151\1\uffff\1\147\1\uffff\3\172\1\uffff\1\157\1"+
        "\uffff\1\172\1\164\1\172\3\uffff\1\156\1\uffff\1\171\1\uffff\2\172"+
        "\2\uffff";
    static final String DFA12_acceptS =
        "\24\uffff\1\33\1\34\1\36\1\37\1\40\1\41\1\42\1\43\1\44\1\46\1\47"+
        "\1\50\1\51\1\52\1\53\1\54\1\uffff\1\57\24\uffff\1\30\1\31\1\uffff"+
        "\1\35\1\45\1\32\1\55\1\56\4\uffff\1\5\3\uffff\1\11\2\uffff\1\14"+
        "\14\uffff\1\2\5\uffff\1\12\2\uffff\1\16\5\uffff\1\24\3\uffff\1\1"+
        "\1\3\1\uffff\1\6\2\uffff\1\13\1\uffff\1\17\2\uffff\1\22\1\uffff"+
        "\1\25\3\uffff\1\7\1\uffff\1\15\3\uffff\1\26\1\27\1\4\1\uffff\1\21"+
        "\1\uffff\1\23\2\uffff\1\10\1\20";
    static final String DFA12_specialS =
        "\23\uffff\1\0\173\uffff}>";
    static final String[] DFA12_transitionS = {
            "\2\45\2\uffff\1\45\22\uffff\1\45\1\26\1\23\1\30\2\uffff\1\25"+
            "\1\uffff\1\40\1\41\1\32\1\31\1\36\1\34\1\22\1\44\1\20\11\21"+
            "\1\uffff\1\37\5\uffff\32\24\1\uffff\1\35\2\uffff\1\33\1\uffff"+
            "\1\1\1\2\1\3\1\4\1\5\1\6\2\24\1\7\4\24\1\10\1\11\1\13\1\24\1"+
            "\12\1\14\1\15\1\16\1\24\1\17\3\24\1\42\1\27\1\43",
            "\1\46\1\uffff\1\47",
            "\1\50",
            "\1\51",
            "\1\52",
            "\1\53",
            "\1\54\23\uffff\1\55",
            "\1\56\7\uffff\1\57",
            "\1\60",
            "\1\61\1\uffff\1\62\1\63",
            "\1\64",
            "\1\65",
            "\1\66",
            "\1\67",
            "\1\70",
            "\1\71",
            "\1\73\1\uffff\12\73",
            "\1\73\1\uffff\12\74",
            "\12\73",
            "\12\77\1\uffff\2\77\1\uffff\ufff2\77",
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
            "\1\100\4\uffff\1\101",
            "",
            "\1\102",
            "\1\103",
            "\1\104",
            "\1\105",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\107",
            "\1\110",
            "\1\111",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\113",
            "\1\114",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\116",
            "\1\117",
            "\1\120\1\uffff\1\121",
            "\1\122",
            "\1\123\20\uffff\1\124",
            "\1\126\3\uffff\1\125",
            "\1\127",
            "\1\130",
            "",
            "",
            "\1\73\1\uffff\12\74",
            "",
            "",
            "",
            "",
            "",
            "\1\131",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\133",
            "\1\134",
            "",
            "\1\135",
            "\1\136",
            "\1\137",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\141",
            "",
            "\1\142",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\144",
            "\1\145",
            "\1\146",
            "\1\147",
            "\1\150",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\152",
            "\1\153",
            "\1\154",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\157",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\161",
            "\1\162",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\164",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\166",
            "\1\167",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\171",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\173",
            "\1\174",
            "",
            "",
            "\1\175",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\177",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\u0081",
            "\1\u0082",
            "",
            "\1\u0083",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\u0087",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\u0089",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "",
            "",
            "\1\u008b",
            "",
            "\1\u008c",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            ""
    };

    static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
    static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
    static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
    static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
    static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
    static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
    static final short[][] DFA12_transition;

    static {
        int numStates = DFA12_transitionS.length;
        DFA12_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
        }
    }

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = DFA12_eot;
            this.eof = DFA12_eof;
            this.min = DFA12_min;
            this.max = DFA12_max;
            this.accept = DFA12_accept;
            this.special = DFA12_special;
            this.transition = DFA12_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( ALAP | ANY | BOOL | CHOICE | DO | ELSE | FALSE | FUNCTION | IF | INT | NODE | OR | OTHER | OUT | REAL | PRIORITY | RECIPE | STAR | STRING | TRY | TRUE | UNTIL | WHILE | INT_LIT | REAL_LIT | STRING_LIT | ID | AMP | DOT | NOT | BAR | SHARP | PLUS | ASTERISK | DONT_CARE | MINUS | QUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA12_19 = input.LA(1);

                        s = -1;
                        if ( ((LA12_19 >= '\u0000' && LA12_19 <= '\t')||(LA12_19 >= '\u000B' && LA12_19 <= '\f')||(LA12_19 >= '\u000E' && LA12_19 <= '\uFFFF')) ) {s = 63;}

                        else s = 62;

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 12, _s, input);
            error(nvae);
            throw nvae;
        }

    }
 

}