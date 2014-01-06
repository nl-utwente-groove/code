// $ANTLR 3.4 D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g 2014-01-05 23:15:52

package groove.control.parse;
import groove.control.*;
import groove.util.antlr.*;
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
    public static final int IMPORT=29;
    public static final int INT=30;
    public static final int INT_LIT=31;
    public static final int IntegerNumber=32;
    public static final int LCURLY=33;
    public static final int LPAR=34;
    public static final int MINUS=35;
    public static final int ML_COMMENT=36;
    public static final int NODE=37;
    public static final int NOT=38;
    public static final int NonIntegerNumber=39;
    public static final int OR=40;
    public static final int OTHER=41;
    public static final int OUT=42;
    public static final int PACKAGE=43;
    public static final int PLUS=44;
    public static final int PRIORITY=45;
    public static final int PROGRAM=46;
    public static final int QUOTE=47;
    public static final int RCURLY=48;
    public static final int REAL=49;
    public static final int REAL_LIT=50;
    public static final int RECIPE=51;
    public static final int RECIPES=52;
    public static final int RPAR=53;
    public static final int SEMI=54;
    public static final int SHARP=55;
    public static final int SL_COMMENT=56;
    public static final int STAR=57;
    public static final int STRING=58;
    public static final int STRING_LIT=59;
    public static final int TRUE=60;
    public static final int TRY=61;
    public static final int UNTIL=62;
    public static final int VAR=63;
    public static final int WHILE=64;
    public static final int WS=65;

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
    public String getGrammarFileName() { return "D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g"; }

    // $ANTLR start "ALAP"
    public final void mALAP() throws RecognitionException {
        try {
            int _type = ALAP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:362:10: ( 'alap' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:362:12: 'alap'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:363:9: ( 'any' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:363:11: 'any'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:364:10: ( 'bool' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:364:12: 'bool'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:365:10: ( 'choice' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:365:12: 'choice'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:366:10: ( 'do' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:366:12: 'do'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:367:10: ( 'else' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:367:12: 'else'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:368:10: ( 'false' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:368:12: 'false'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:369:10: ( 'function' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:369:12: 'function'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:370:10: ( 'if' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:370:12: 'if'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:371:10: ( 'import' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:371:12: 'import'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:372:10: ( 'int' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:372:12: 'int'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:373:10: ( 'node' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:373:12: 'node'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:374:10: ( 'or' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:374:12: 'or'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:375:10: ( 'other' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:375:12: 'other'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:376:10: ( 'out' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:376:12: 'out'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:377:10: ( 'real' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:377:12: 'real'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:378:10: ( 'package' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:378:12: 'package'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:379:10: ( 'priority' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:379:12: 'priority'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:380:10: ( 'recipe' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:380:12: 'recipe'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:381:10: ( 'star' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:381:12: 'star'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:382:10: ( 'string' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:382:12: 'string'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:383:10: ( 'try' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:383:12: 'try'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:384:10: ( 'true' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:384:12: 'true'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:385:10: ( 'until' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:385:12: 'until'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:386:10: ( 'while' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:386:12: 'while'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:389:3: ( IntegerNumber )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:389:5: IntegerNumber
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:395:3: ( '0' | '1' .. '9' ( '0' .. '9' )* )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:395:5: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:396:5: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:396:14: ( '0' .. '9' )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0 >= '0' && LA1_0 <= '9')) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:399:3: ( NonIntegerNumber )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:399:5: NonIntegerNumber
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:405:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* | '.' ( '0' .. '9' )+ )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:405:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )*
                    {
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:405:9: ( '0' .. '9' )+
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
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
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

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:405:27: ( '0' .. '9' )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0 >= '0' && LA4_0 <= '9')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:406:9: '.' ( '0' .. '9' )+
                    {
                    match('.'); 

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:406:13: ( '0' .. '9' )+
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
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:410:3: ( QUOTE ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:410:5: QUOTE ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE
            {
            mQUOTE(); 


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:411:5: ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )*
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
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:411:7: EscapeSequence
            	    {
            	    mEscapeSequence(); 


            	    }
            	    break;
            	case 2 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:412:7: ~ ( BSLASH | QUOTE | '\\r' | '\\n' )
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:420:3: ( BSLASH ( QUOTE BSLASH ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:420:5: BSLASH ( QUOTE BSLASH )
            {
            mBSLASH(); 


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:421:5: ( QUOTE BSLASH )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:421:7: QUOTE BSLASH
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:425:5: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )* )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:425:7: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:425:27: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0=='-'||(LA8_0 >= '0' && LA8_0 <= '9')||(LA8_0 >= 'A' && LA8_0 <= 'Z')||LA8_0=='_'||(LA8_0 >= 'a' && LA8_0 <= 'z')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:427:11: ( '&' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:427:13: '&'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:428:11: ( '.' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:428:13: '.'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:429:11: ( '!' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:429:13: '!'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:430:11: ( '|' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:430:13: '|'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:431:11: ( '#' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:431:13: '#'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:432:11: ( '+' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:432:13: '+'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:433:11: ( '*' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:433:13: '*'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:434:11: ( '_' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:434:13: '_'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:435:11: ( '-' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:435:13: '-'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:436:11: ( '\"' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:436:13: '\"'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:437:11: ( '\\\\' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:437:13: '\\\\'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:438:11: ( ',' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:438:13: ','
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:439:11: ( ';' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:439:13: ';'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:440:11: ( '(' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:440:13: '('
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:441:11: ( ')' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:441:13: ')'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:442:11: ( '{' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:442:13: '{'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:443:11: ( '}' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:443:13: '}'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:445:12: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:445:14: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 



            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:445:19: ( options {greedy=false; } : . )*
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
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:445:47: .
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:446:12: ( '//' ( options {greedy=false; } : . )* '\\n' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:446:14: '//' ( options {greedy=false; } : . )* '\\n'
            {
            match("//"); 



            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:446:19: ( options {greedy=false; } : . )*
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
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:446:47: .
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:448:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:448:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:448:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
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
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
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
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:8: ( ALAP | ANY | BOOL | CHOICE | DO | ELSE | FALSE | FUNCTION | IF | IMPORT | INT | NODE | OR | OTHER | OUT | REAL | PACKAGE | PRIORITY | RECIPE | STAR | STRING | TRY | TRUE | UNTIL | WHILE | INT_LIT | REAL_LIT | STRING_LIT | ID | AMP | DOT | NOT | BAR | SHARP | PLUS | ASTERISK | DONT_CARE | MINUS | QUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS )
        int alt12=49;
        alt12 = dfa12.predict(input);
        switch (alt12) {
            case 1 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:10: ALAP
                {
                mALAP(); 


                }
                break;
            case 2 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:15: ANY
                {
                mANY(); 


                }
                break;
            case 3 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:19: BOOL
                {
                mBOOL(); 


                }
                break;
            case 4 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:24: CHOICE
                {
                mCHOICE(); 


                }
                break;
            case 5 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:31: DO
                {
                mDO(); 


                }
                break;
            case 6 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:34: ELSE
                {
                mELSE(); 


                }
                break;
            case 7 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:39: FALSE
                {
                mFALSE(); 


                }
                break;
            case 8 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:45: FUNCTION
                {
                mFUNCTION(); 


                }
                break;
            case 9 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:54: IF
                {
                mIF(); 


                }
                break;
            case 10 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:57: IMPORT
                {
                mIMPORT(); 


                }
                break;
            case 11 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:64: INT
                {
                mINT(); 


                }
                break;
            case 12 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:68: NODE
                {
                mNODE(); 


                }
                break;
            case 13 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:73: OR
                {
                mOR(); 


                }
                break;
            case 14 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:76: OTHER
                {
                mOTHER(); 


                }
                break;
            case 15 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:82: OUT
                {
                mOUT(); 


                }
                break;
            case 16 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:86: REAL
                {
                mREAL(); 


                }
                break;
            case 17 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:91: PACKAGE
                {
                mPACKAGE(); 


                }
                break;
            case 18 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:99: PRIORITY
                {
                mPRIORITY(); 


                }
                break;
            case 19 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:108: RECIPE
                {
                mRECIPE(); 


                }
                break;
            case 20 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:115: STAR
                {
                mSTAR(); 


                }
                break;
            case 21 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:120: STRING
                {
                mSTRING(); 


                }
                break;
            case 22 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:127: TRY
                {
                mTRY(); 


                }
                break;
            case 23 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:131: TRUE
                {
                mTRUE(); 


                }
                break;
            case 24 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:136: UNTIL
                {
                mUNTIL(); 


                }
                break;
            case 25 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:142: WHILE
                {
                mWHILE(); 


                }
                break;
            case 26 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:148: INT_LIT
                {
                mINT_LIT(); 


                }
                break;
            case 27 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:156: REAL_LIT
                {
                mREAL_LIT(); 


                }
                break;
            case 28 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:165: STRING_LIT
                {
                mSTRING_LIT(); 


                }
                break;
            case 29 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:176: ID
                {
                mID(); 


                }
                break;
            case 30 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:179: AMP
                {
                mAMP(); 


                }
                break;
            case 31 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:183: DOT
                {
                mDOT(); 


                }
                break;
            case 32 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:187: NOT
                {
                mNOT(); 


                }
                break;
            case 33 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:191: BAR
                {
                mBAR(); 


                }
                break;
            case 34 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:195: SHARP
                {
                mSHARP(); 


                }
                break;
            case 35 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:201: PLUS
                {
                mPLUS(); 


                }
                break;
            case 36 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:206: ASTERISK
                {
                mASTERISK(); 


                }
                break;
            case 37 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:215: DONT_CARE
                {
                mDONT_CARE(); 


                }
                break;
            case 38 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:225: MINUS
                {
                mMINUS(); 


                }
                break;
            case 39 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:231: QUOTE
                {
                mQUOTE(); 


                }
                break;
            case 40 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:237: BSLASH
                {
                mBSLASH(); 


                }
                break;
            case 41 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:244: COMMA
                {
                mCOMMA(); 


                }
                break;
            case 42 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:250: SEMI
                {
                mSEMI(); 


                }
                break;
            case 43 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:255: LPAR
                {
                mLPAR(); 


                }
                break;
            case 44 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:260: RPAR
                {
                mRPAR(); 


                }
                break;
            case 45 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:265: LCURLY
                {
                mLCURLY(); 


                }
                break;
            case 46 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:272: RCURLY
                {
                mRCURLY(); 


                }
                break;
            case 47 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:279: ML_COMMENT
                {
                mML_COMMENT(); 


                }
                break;
            case 48 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:290: SL_COMMENT
                {
                mSL_COMMENT(); 


                }
                break;
            case 49 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:301: WS
                {
                mWS(); 


                }
                break;

        }

    }


    protected DFA12 dfa12 = new DFA12(this);
    static final String DFA12_eotS =
        "\1\uffff\17\24\2\74\1\77\1\100\22\uffff\4\24\1\110\3\24\1\114\3"+
        "\24\1\120\11\24\2\uffff\1\74\5\uffff\1\24\1\136\2\24\1\uffff\3\24"+
        "\1\uffff\1\24\1\145\1\24\1\uffff\1\24\1\150\6\24\1\157\3\24\1\163"+
        "\1\uffff\1\164\1\24\1\166\3\24\1\uffff\1\172\1\24\1\uffff\1\174"+
        "\3\24\1\u0080\1\24\1\uffff\1\u0082\2\24\2\uffff\1\24\1\uffff\1\u0086"+
        "\2\24\1\uffff\1\u0089\1\uffff\3\24\1\uffff\1\24\1\uffff\1\u008e"+
        "\1\u008f\1\u0090\1\uffff\1\24\1\u0092\1\uffff\1\u0093\2\24\1\u0096"+
        "\3\uffff\1\24\2\uffff\1\u0098\1\24\1\uffff\1\u009a\1\uffff\1\u009b"+
        "\2\uffff";
    static final String DFA12_eofS =
        "\u009c\uffff";
    static final String DFA12_minS =
        "\1\11\1\154\1\157\1\150\1\157\1\154\1\141\1\146\1\157\1\162\1\145"+
        "\1\141\1\164\1\162\1\156\1\150\2\56\1\60\1\0\20\uffff\1\52\1\uffff"+
        "\1\141\1\171\2\157\1\55\1\163\1\154\1\156\1\55\1\160\1\164\1\144"+
        "\1\55\1\150\1\164\1\141\1\143\1\151\1\141\1\165\1\164\1\151\2\uffff"+
        "\1\56\5\uffff\1\160\1\55\1\154\1\151\1\uffff\1\145\1\163\1\143\1"+
        "\uffff\1\157\1\55\1\145\1\uffff\1\145\1\55\1\154\1\151\1\153\1\157"+
        "\1\162\1\151\1\55\1\145\1\151\1\154\1\55\1\uffff\1\55\1\143\1\55"+
        "\1\145\1\164\1\162\1\uffff\1\55\1\162\1\uffff\1\55\1\160\1\141\1"+
        "\162\1\55\1\156\1\uffff\1\55\1\154\1\145\2\uffff\1\145\1\uffff\1"+
        "\55\1\151\1\164\1\uffff\1\55\1\uffff\1\145\1\147\1\151\1\uffff\1"+
        "\147\1\uffff\3\55\1\uffff\1\157\1\55\1\uffff\1\55\1\145\1\164\1"+
        "\55\3\uffff\1\156\2\uffff\1\55\1\171\1\uffff\1\55\1\uffff\1\55\2"+
        "\uffff";
    static final String DFA12_maxS =
        "\1\175\1\156\1\157\1\150\1\157\1\154\1\165\1\156\1\157\1\165\1\145"+
        "\1\162\1\164\1\162\1\156\1\150\3\71\1\uffff\20\uffff\1\57\1\uffff"+
        "\1\141\1\171\2\157\1\172\1\163\1\154\1\156\1\172\1\160\1\164\1\144"+
        "\1\172\1\150\1\164\2\143\1\151\1\162\1\171\1\164\1\151\2\uffff\1"+
        "\71\5\uffff\1\160\1\172\1\154\1\151\1\uffff\1\145\1\163\1\143\1"+
        "\uffff\1\157\1\172\1\145\1\uffff\1\145\1\172\1\154\1\151\1\153\1"+
        "\157\1\162\1\151\1\172\1\145\1\151\1\154\1\172\1\uffff\1\172\1\143"+
        "\1\172\1\145\1\164\1\162\1\uffff\1\172\1\162\1\uffff\1\172\1\160"+
        "\1\141\1\162\1\172\1\156\1\uffff\1\172\1\154\1\145\2\uffff\1\145"+
        "\1\uffff\1\172\1\151\1\164\1\uffff\1\172\1\uffff\1\145\1\147\1\151"+
        "\1\uffff\1\147\1\uffff\3\172\1\uffff\1\157\1\172\1\uffff\1\172\1"+
        "\145\1\164\1\172\3\uffff\1\156\2\uffff\1\172\1\171\1\uffff\1\172"+
        "\1\uffff\1\172\2\uffff";
    static final String DFA12_acceptS =
        "\24\uffff\1\35\1\36\1\40\1\41\1\42\1\43\1\44\1\45\1\46\1\50\1\51"+
        "\1\52\1\53\1\54\1\55\1\56\1\uffff\1\61\26\uffff\1\32\1\33\1\uffff"+
        "\1\37\1\47\1\34\1\57\1\60\4\uffff\1\5\3\uffff\1\11\3\uffff\1\15"+
        "\15\uffff\1\2\6\uffff\1\13\2\uffff\1\17\6\uffff\1\26\3\uffff\1\1"+
        "\1\3\1\uffff\1\6\3\uffff\1\14\1\uffff\1\20\3\uffff\1\24\1\uffff"+
        "\1\27\3\uffff\1\7\2\uffff\1\16\4\uffff\1\30\1\31\1\4\1\uffff\1\12"+
        "\1\23\2\uffff\1\25\1\uffff\1\21\1\uffff\1\10\1\22";
    static final String DFA12_specialS =
        "\23\uffff\1\0\u0088\uffff}>";
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
            "\1\56\6\uffff\1\57\1\60",
            "\1\61",
            "\1\62\1\uffff\1\63\1\64",
            "\1\65",
            "\1\66\20\uffff\1\67",
            "\1\70",
            "\1\71",
            "\1\72",
            "\1\73",
            "\1\75\1\uffff\12\75",
            "\1\75\1\uffff\12\76",
            "\12\75",
            "\12\101\1\uffff\2\101\1\uffff\ufff2\101",
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
            "\1\102\4\uffff\1\103",
            "",
            "\1\104",
            "\1\105",
            "\1\106",
            "\1\107",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\111",
            "\1\112",
            "\1\113",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\115",
            "\1\116",
            "\1\117",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\121",
            "\1\122",
            "\1\123\1\uffff\1\124",
            "\1\125",
            "\1\126",
            "\1\127\20\uffff\1\130",
            "\1\132\3\uffff\1\131",
            "\1\133",
            "\1\134",
            "",
            "",
            "\1\75\1\uffff\12\76",
            "",
            "",
            "",
            "",
            "",
            "\1\135",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\137",
            "\1\140",
            "",
            "\1\141",
            "\1\142",
            "\1\143",
            "",
            "\1\144",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\146",
            "",
            "\1\147",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\151",
            "\1\152",
            "\1\153",
            "\1\154",
            "\1\155",
            "\1\156",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\160",
            "\1\161",
            "\1\162",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\165",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\167",
            "\1\170",
            "\1\171",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\173",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\175",
            "\1\176",
            "\1\177",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\u0081",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\u0083",
            "\1\u0084",
            "",
            "",
            "\1\u0085",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\u0087",
            "\1\u0088",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\u008a",
            "\1\u008b",
            "\1\u008c",
            "",
            "\1\u008d",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\u0091",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\u0094",
            "\1\u0095",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "",
            "",
            "\1\u0097",
            "",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\u0099",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
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
            return "1:1: Tokens : ( ALAP | ANY | BOOL | CHOICE | DO | ELSE | FALSE | FUNCTION | IF | IMPORT | INT | NODE | OR | OTHER | OUT | REAL | PACKAGE | PRIORITY | RECIPE | STAR | STRING | TRY | TRUE | UNTIL | WHILE | INT_LIT | REAL_LIT | STRING_LIT | ID | AMP | DOT | NOT | BAR | SHARP | PLUS | ASTERISK | DONT_CARE | MINUS | QUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA12_19 = input.LA(1);

                        s = -1;
                        if ( ((LA12_19 >= '\u0000' && LA12_19 <= '\t')||(LA12_19 >= '\u000B' && LA12_19 <= '\f')||(LA12_19 >= '\u000E' && LA12_19 <= '\uFFFF')) ) {s = 65;}

                        else s = 64;

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