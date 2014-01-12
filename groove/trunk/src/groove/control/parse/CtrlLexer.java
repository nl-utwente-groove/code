// $ANTLR 3.4 D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g 2014-01-12 19:53:19

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
    public static final int ATOM=10;
    public static final int BAR=11;
    public static final int BLOCK=12;
    public static final int BOOL=13;
    public static final int BSLASH=14;
    public static final int CALL=15;
    public static final int CHOICE=16;
    public static final int COMMA=17;
    public static final int DO=18;
    public static final int DONT_CARE=19;
    public static final int DOT=20;
    public static final int DO_UNTIL=21;
    public static final int DO_WHILE=22;
    public static final int ELSE=23;
    public static final int EscapeSequence=24;
    public static final int FALSE=25;
    public static final int FUNCTION=26;
    public static final int FUNCTIONS=27;
    public static final int ID=28;
    public static final int IF=29;
    public static final int IMPORT=30;
    public static final int INT=31;
    public static final int INT_LIT=32;
    public static final int IntegerNumber=33;
    public static final int LCURLY=34;
    public static final int LPAR=35;
    public static final int MINUS=36;
    public static final int ML_COMMENT=37;
    public static final int NODE=38;
    public static final int NOT=39;
    public static final int NonIntegerNumber=40;
    public static final int OR=41;
    public static final int OTHER=42;
    public static final int OUT=43;
    public static final int PACKAGE=44;
    public static final int PAR=45;
    public static final int PARS=46;
    public static final int PLUS=47;
    public static final int PRIORITY=48;
    public static final int PROGRAM=49;
    public static final int QUOTE=50;
    public static final int RCURLY=51;
    public static final int REAL=52;
    public static final int REAL_LIT=53;
    public static final int RECIPE=54;
    public static final int RECIPES=55;
    public static final int RPAR=56;
    public static final int SEMI=57;
    public static final int SHARP=58;
    public static final int SL_COMMENT=59;
    public static final int STAR=60;
    public static final int STRING=61;
    public static final int STRING_LIT=62;
    public static final int TRUE=63;
    public static final int TRY=64;
    public static final int UNTIL=65;
    public static final int VAR=66;
    public static final int WHILE=67;
    public static final int WS=68;

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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:391:10: ( 'alap' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:391:12: 'alap'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:392:9: ( 'any' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:392:11: 'any'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:393:10: ( 'atomic' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:393:12: 'atomic'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:394:10: ( 'bool' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:394:12: 'bool'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:395:10: ( 'choice' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:395:12: 'choice'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:396:10: ( 'do' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:396:12: 'do'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:397:10: ( 'else' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:397:12: 'else'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:398:10: ( 'false' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:398:12: 'false'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:399:10: ( 'function' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:399:12: 'function'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:400:10: ( 'if' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:400:12: 'if'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:401:10: ( 'import' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:401:12: 'import'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:402:10: ( 'int' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:402:12: 'int'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:403:10: ( 'node' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:403:12: 'node'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:404:10: ( 'or' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:404:12: 'or'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:405:10: ( 'other' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:405:12: 'other'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:406:10: ( 'out' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:406:12: 'out'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:407:10: ( 'real' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:407:12: 'real'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:408:10: ( 'package' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:408:12: 'package'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:409:10: ( 'priority' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:409:12: 'priority'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:410:10: ( 'recipe' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:410:12: 'recipe'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:411:10: ( 'star' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:411:12: 'star'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:412:10: ( 'string' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:412:12: 'string'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:413:10: ( 'try' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:413:12: 'try'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:414:10: ( 'true' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:414:12: 'true'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:415:10: ( 'until' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:415:12: 'until'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:416:10: ( 'while' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:416:12: 'while'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:419:3: ( IntegerNumber )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:419:5: IntegerNumber
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:425:3: ( '0' | '1' .. '9' ( '0' .. '9' )* )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:425:5: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:426:5: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:426:14: ( '0' .. '9' )*
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:429:3: ( NonIntegerNumber )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:429:5: NonIntegerNumber
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:435:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* | '.' ( '0' .. '9' )+ )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:435:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )*
                    {
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:435:9: ( '0' .. '9' )+
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

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:435:27: ( '0' .. '9' )*
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:436:9: '.' ( '0' .. '9' )+
                    {
                    match('.'); 

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:436:13: ( '0' .. '9' )+
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:440:3: ( QUOTE ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:440:5: QUOTE ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE
            {
            mQUOTE(); 


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:441:5: ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )*
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
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:441:7: EscapeSequence
            	    {
            	    mEscapeSequence(); 


            	    }
            	    break;
            	case 2 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:442:7: ~ ( BSLASH | QUOTE | '\\r' | '\\n' )
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:450:3: ( BSLASH ( QUOTE BSLASH ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:450:5: BSLASH ( QUOTE BSLASH )
            {
            mBSLASH(); 


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:451:5: ( QUOTE BSLASH )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:451:7: QUOTE BSLASH
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:455:5: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )* )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:455:7: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:455:27: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:457:11: ( '&' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:457:13: '&'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:458:11: ( '.' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:458:13: '.'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:459:11: ( '!' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:459:13: '!'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:460:11: ( '|' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:460:13: '|'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:461:11: ( '#' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:461:13: '#'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:462:11: ( '+' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:462:13: '+'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:463:11: ( '*' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:463:13: '*'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:464:11: ( '_' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:464:13: '_'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:465:11: ( '-' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:465:13: '-'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:466:11: ( '\"' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:466:13: '\"'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:467:11: ( '\\\\' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:467:13: '\\\\'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:468:11: ( ',' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:468:13: ','
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:469:11: ( ';' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:469:13: ';'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:470:11: ( '(' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:470:13: '('
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:471:11: ( ')' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:471:13: ')'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:472:11: ( '{' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:472:13: '{'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:473:11: ( '}' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:473:13: '}'
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:475:12: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:475:14: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 



            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:475:19: ( options {greedy=false; } : . )*
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
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:475:47: .
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:476:12: ( '//' ( options {greedy=false; } : . )* '\\n' )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:476:14: '//' ( options {greedy=false; } : . )* '\\n'
            {
            match("//"); 



            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:476:19: ( options {greedy=false; } : . )*
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
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:476:47: .
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:478:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:478:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:478:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
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
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:8: ( ALAP | ANY | ATOM | BOOL | CHOICE | DO | ELSE | FALSE | FUNCTION | IF | IMPORT | INT | NODE | OR | OTHER | OUT | REAL | PACKAGE | PRIORITY | RECIPE | STAR | STRING | TRY | TRUE | UNTIL | WHILE | INT_LIT | REAL_LIT | STRING_LIT | ID | AMP | DOT | NOT | BAR | SHARP | PLUS | ASTERISK | DONT_CARE | MINUS | QUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS )
        int alt12=50;
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
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:19: ATOM
                {
                mATOM(); 


                }
                break;
            case 4 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:24: BOOL
                {
                mBOOL(); 


                }
                break;
            case 5 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:29: CHOICE
                {
                mCHOICE(); 


                }
                break;
            case 6 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:36: DO
                {
                mDO(); 


                }
                break;
            case 7 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:39: ELSE
                {
                mELSE(); 


                }
                break;
            case 8 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:44: FALSE
                {
                mFALSE(); 


                }
                break;
            case 9 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:50: FUNCTION
                {
                mFUNCTION(); 


                }
                break;
            case 10 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:59: IF
                {
                mIF(); 


                }
                break;
            case 11 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:62: IMPORT
                {
                mIMPORT(); 


                }
                break;
            case 12 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:69: INT
                {
                mINT(); 


                }
                break;
            case 13 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:73: NODE
                {
                mNODE(); 


                }
                break;
            case 14 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:78: OR
                {
                mOR(); 


                }
                break;
            case 15 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:81: OTHER
                {
                mOTHER(); 


                }
                break;
            case 16 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:87: OUT
                {
                mOUT(); 


                }
                break;
            case 17 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:91: REAL
                {
                mREAL(); 


                }
                break;
            case 18 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:96: PACKAGE
                {
                mPACKAGE(); 


                }
                break;
            case 19 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:104: PRIORITY
                {
                mPRIORITY(); 


                }
                break;
            case 20 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:113: RECIPE
                {
                mRECIPE(); 


                }
                break;
            case 21 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:120: STAR
                {
                mSTAR(); 


                }
                break;
            case 22 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:125: STRING
                {
                mSTRING(); 


                }
                break;
            case 23 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:132: TRY
                {
                mTRY(); 


                }
                break;
            case 24 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:136: TRUE
                {
                mTRUE(); 


                }
                break;
            case 25 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:141: UNTIL
                {
                mUNTIL(); 


                }
                break;
            case 26 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:147: WHILE
                {
                mWHILE(); 


                }
                break;
            case 27 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:153: INT_LIT
                {
                mINT_LIT(); 


                }
                break;
            case 28 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:161: REAL_LIT
                {
                mREAL_LIT(); 


                }
                break;
            case 29 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:170: STRING_LIT
                {
                mSTRING_LIT(); 


                }
                break;
            case 30 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:181: ID
                {
                mID(); 


                }
                break;
            case 31 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:184: AMP
                {
                mAMP(); 


                }
                break;
            case 32 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:188: DOT
                {
                mDOT(); 


                }
                break;
            case 33 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:192: NOT
                {
                mNOT(); 


                }
                break;
            case 34 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:196: BAR
                {
                mBAR(); 


                }
                break;
            case 35 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:200: SHARP
                {
                mSHARP(); 


                }
                break;
            case 36 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:206: PLUS
                {
                mPLUS(); 


                }
                break;
            case 37 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:211: ASTERISK
                {
                mASTERISK(); 


                }
                break;
            case 38 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:220: DONT_CARE
                {
                mDONT_CARE(); 


                }
                break;
            case 39 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:230: MINUS
                {
                mMINUS(); 


                }
                break;
            case 40 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:236: QUOTE
                {
                mQUOTE(); 


                }
                break;
            case 41 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:242: BSLASH
                {
                mBSLASH(); 


                }
                break;
            case 42 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:249: COMMA
                {
                mCOMMA(); 


                }
                break;
            case 43 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:255: SEMI
                {
                mSEMI(); 


                }
                break;
            case 44 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:260: LPAR
                {
                mLPAR(); 


                }
                break;
            case 45 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:265: RPAR
                {
                mRPAR(); 


                }
                break;
            case 46 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:270: LCURLY
                {
                mLCURLY(); 


                }
                break;
            case 47 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:277: RCURLY
                {
                mRCURLY(); 


                }
                break;
            case 48 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:284: ML_COMMENT
                {
                mML_COMMENT(); 


                }
                break;
            case 49 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:295: SL_COMMENT
                {
                mSL_COMMENT(); 


                }
                break;
            case 50 :
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:306: WS
                {
                mWS(); 


                }
                break;

        }

    }


    protected DFA12 dfa12 = new DFA12(this);
    static final String DFA12_eotS =
        "\1\uffff\17\24\2\75\1\100\1\101\22\uffff\5\24\1\112\3\24\1\116\3"+
        "\24\1\122\11\24\2\uffff\1\75\5\uffff\1\24\1\140\3\24\1\uffff\3\24"+
        "\1\uffff\1\24\1\150\1\24\1\uffff\1\24\1\153\6\24\1\162\3\24\1\166"+
        "\1\uffff\1\24\1\170\1\24\1\172\3\24\1\uffff\1\176\1\24\1\uffff\1"+
        "\u0080\3\24\1\u0084\1\24\1\uffff\1\u0086\2\24\1\uffff\1\24\1\uffff"+
        "\1\24\1\uffff\1\u008b\2\24\1\uffff\1\u008e\1\uffff\3\24\1\uffff"+
        "\1\24\1\uffff\1\u0093\1\u0094\1\u0095\1\u0096\1\uffff\1\24\1\u0098"+
        "\1\uffff\1\u0099\2\24\1\u009c\4\uffff\1\24\2\uffff\1\u009e\1\24"+
        "\1\uffff\1\u00a0\1\uffff\1\u00a1\2\uffff";
    static final String DFA12_eofS =
        "\u00a2\uffff";
    static final String DFA12_minS =
        "\1\11\1\154\1\157\1\150\1\157\1\154\1\141\1\146\1\157\1\162\1\145"+
        "\1\141\1\164\1\162\1\156\1\150\2\56\1\60\1\0\20\uffff\1\52\1\uffff"+
        "\1\141\1\171\3\157\1\55\1\163\1\154\1\156\1\55\1\160\1\164\1\144"+
        "\1\55\1\150\1\164\1\141\1\143\1\151\1\141\1\165\1\164\1\151\2\uffff"+
        "\1\56\5\uffff\1\160\1\55\1\155\1\154\1\151\1\uffff\1\145\1\163\1"+
        "\143\1\uffff\1\157\1\55\1\145\1\uffff\1\145\1\55\1\154\1\151\1\153"+
        "\1\157\1\162\1\151\1\55\1\145\1\151\1\154\1\55\1\uffff\1\151\1\55"+
        "\1\143\1\55\1\145\1\164\1\162\1\uffff\1\55\1\162\1\uffff\1\55\1"+
        "\160\1\141\1\162\1\55\1\156\1\uffff\1\55\1\154\1\145\1\uffff\1\143"+
        "\1\uffff\1\145\1\uffff\1\55\1\151\1\164\1\uffff\1\55\1\uffff\1\145"+
        "\1\147\1\151\1\uffff\1\147\1\uffff\4\55\1\uffff\1\157\1\55\1\uffff"+
        "\1\55\1\145\1\164\1\55\4\uffff\1\156\2\uffff\1\55\1\171\1\uffff"+
        "\1\55\1\uffff\1\55\2\uffff";
    static final String DFA12_maxS =
        "\1\175\1\164\1\157\1\150\1\157\1\154\1\165\1\156\1\157\1\165\1\145"+
        "\1\162\1\164\1\162\1\156\1\150\3\71\1\uffff\20\uffff\1\57\1\uffff"+
        "\1\141\1\171\3\157\1\172\1\163\1\154\1\156\1\172\1\160\1\164\1\144"+
        "\1\172\1\150\1\164\2\143\1\151\1\162\1\171\1\164\1\151\2\uffff\1"+
        "\71\5\uffff\1\160\1\172\1\155\1\154\1\151\1\uffff\1\145\1\163\1"+
        "\143\1\uffff\1\157\1\172\1\145\1\uffff\1\145\1\172\1\154\1\151\1"+
        "\153\1\157\1\162\1\151\1\172\1\145\1\151\1\154\1\172\1\uffff\1\151"+
        "\1\172\1\143\1\172\1\145\1\164\1\162\1\uffff\1\172\1\162\1\uffff"+
        "\1\172\1\160\1\141\1\162\1\172\1\156\1\uffff\1\172\1\154\1\145\1"+
        "\uffff\1\143\1\uffff\1\145\1\uffff\1\172\1\151\1\164\1\uffff\1\172"+
        "\1\uffff\1\145\1\147\1\151\1\uffff\1\147\1\uffff\4\172\1\uffff\1"+
        "\157\1\172\1\uffff\1\172\1\145\1\164\1\172\4\uffff\1\156\2\uffff"+
        "\1\172\1\171\1\uffff\1\172\1\uffff\1\172\2\uffff";
    static final String DFA12_acceptS =
        "\24\uffff\1\36\1\37\1\41\1\42\1\43\1\44\1\45\1\46\1\47\1\51\1\52"+
        "\1\53\1\54\1\55\1\56\1\57\1\uffff\1\62\27\uffff\1\33\1\34\1\uffff"+
        "\1\40\1\50\1\35\1\60\1\61\5\uffff\1\6\3\uffff\1\12\3\uffff\1\16"+
        "\15\uffff\1\2\7\uffff\1\14\2\uffff\1\20\6\uffff\1\27\3\uffff\1\1"+
        "\1\uffff\1\4\1\uffff\1\7\3\uffff\1\15\1\uffff\1\21\3\uffff\1\25"+
        "\1\uffff\1\30\4\uffff\1\10\2\uffff\1\17\4\uffff\1\31\1\32\1\3\1"+
        "\5\1\uffff\1\13\1\24\2\uffff\1\26\1\uffff\1\22\1\uffff\1\11\1\23";
    static final String DFA12_specialS =
        "\23\uffff\1\0\u008e\uffff}>";
    static final String[] DFA12_transitionS = {
            "\2\45\2\uffff\1\45\22\uffff\1\45\1\26\1\23\1\30\2\uffff\1\25"+
            "\1\uffff\1\40\1\41\1\32\1\31\1\36\1\34\1\22\1\44\1\20\11\21"+
            "\1\uffff\1\37\5\uffff\32\24\1\uffff\1\35\2\uffff\1\33\1\uffff"+
            "\1\1\1\2\1\3\1\4\1\5\1\6\2\24\1\7\4\24\1\10\1\11\1\13\1\24\1"+
            "\12\1\14\1\15\1\16\1\24\1\17\3\24\1\42\1\27\1\43",
            "\1\46\1\uffff\1\47\5\uffff\1\50",
            "\1\51",
            "\1\52",
            "\1\53",
            "\1\54",
            "\1\55\23\uffff\1\56",
            "\1\57\6\uffff\1\60\1\61",
            "\1\62",
            "\1\63\1\uffff\1\64\1\65",
            "\1\66",
            "\1\67\20\uffff\1\70",
            "\1\71",
            "\1\72",
            "\1\73",
            "\1\74",
            "\1\76\1\uffff\12\76",
            "\1\76\1\uffff\12\77",
            "\12\76",
            "\12\102\1\uffff\2\102\1\uffff\ufff2\102",
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
            "\1\103\4\uffff\1\104",
            "",
            "\1\105",
            "\1\106",
            "\1\107",
            "\1\110",
            "\1\111",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\113",
            "\1\114",
            "\1\115",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\117",
            "\1\120",
            "\1\121",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\123",
            "\1\124",
            "\1\125\1\uffff\1\126",
            "\1\127",
            "\1\130",
            "\1\131\20\uffff\1\132",
            "\1\134\3\uffff\1\133",
            "\1\135",
            "\1\136",
            "",
            "",
            "\1\76\1\uffff\12\77",
            "",
            "",
            "",
            "",
            "",
            "\1\137",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\141",
            "\1\142",
            "\1\143",
            "",
            "\1\144",
            "\1\145",
            "\1\146",
            "",
            "\1\147",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\151",
            "",
            "\1\152",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\154",
            "\1\155",
            "\1\156",
            "\1\157",
            "\1\160",
            "\1\161",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\163",
            "\1\164",
            "\1\165",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\167",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\171",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\173",
            "\1\174",
            "\1\175",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\177",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\u0081",
            "\1\u0082",
            "\1\u0083",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\u0085",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\u0087",
            "\1\u0088",
            "",
            "\1\u0089",
            "",
            "\1\u008a",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\u008c",
            "\1\u008d",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\u008f",
            "\1\u0090",
            "\1\u0091",
            "",
            "\1\u0092",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\u0097",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\u009a",
            "\1\u009b",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "",
            "",
            "",
            "\1\u009d",
            "",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\u009f",
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
            return "1:1: Tokens : ( ALAP | ANY | ATOM | BOOL | CHOICE | DO | ELSE | FALSE | FUNCTION | IF | IMPORT | INT | NODE | OR | OTHER | OUT | REAL | PACKAGE | PRIORITY | RECIPE | STAR | STRING | TRY | TRUE | UNTIL | WHILE | INT_LIT | REAL_LIT | STRING_LIT | ID | AMP | DOT | NOT | BAR | SHARP | PLUS | ASTERISK | DONT_CARE | MINUS | QUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA12_19 = input.LA(1);

                        s = -1;
                        if ( ((LA12_19 >= '\u0000' && LA12_19 <= '\t')||(LA12_19 >= '\u000B' && LA12_19 <= '\f')||(LA12_19 >= '\u000E' && LA12_19 <= '\uFFFF')) ) {s = 66;}

                        else s = 65;

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