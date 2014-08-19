// $ANTLR 3.4 E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g 2014-08-20 00:00:03

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
    public static final int BQUOTE=14;
    public static final int BSLASH=15;
    public static final int CALL=16;
    public static final int CHOICE=17;
    public static final int COMMA=18;
    public static final int DO=19;
    public static final int DONT_CARE=20;
    public static final int DOT=21;
    public static final int DO_UNTIL=22;
    public static final int DO_WHILE=23;
    public static final int ELSE=24;
    public static final int EscapeSequence=25;
    public static final int FALSE=26;
    public static final int FUNCTION=27;
    public static final int FUNCTIONS=28;
    public static final int ID=29;
    public static final int IF=30;
    public static final int IMPORT=31;
    public static final int IMPORTS=32;
    public static final int INT=33;
    public static final int INT_LIT=34;
    public static final int IntegerNumber=35;
    public static final int LANGLE=36;
    public static final int LCURLY=37;
    public static final int LPAR=38;
    public static final int MINUS=39;
    public static final int ML_COMMENT=40;
    public static final int NODE=41;
    public static final int NOT=42;
    public static final int NonIntegerNumber=43;
    public static final int OR=44;
    public static final int OTHER=45;
    public static final int OUT=46;
    public static final int PACKAGE=47;
    public static final int PAR=48;
    public static final int PARS=49;
    public static final int PLUS=50;
    public static final int PRIORITY=51;
    public static final int PROGRAM=52;
    public static final int QUOTE=53;
    public static final int RANGLE=54;
    public static final int RCURLY=55;
    public static final int REAL=56;
    public static final int REAL_LIT=57;
    public static final int RECIPE=58;
    public static final int RECIPES=59;
    public static final int RPAR=60;
    public static final int SEMI=61;
    public static final int SHARP=62;
    public static final int SL_COMMENT=63;
    public static final int STAR=64;
    public static final int STRING=65;
    public static final int STRING_LIT=66;
    public static final int TRUE=67;
    public static final int TRY=68;
    public static final int UNTIL=69;
    public static final int VAR=70;
    public static final int WHILE=71;
    public static final int WS=72;

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
    public String getGrammarFileName() { return "E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g"; }

    // $ANTLR start "ALAP"
    public final void mALAP() throws RecognitionException {
        try {
            int _type = ALAP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:413:10: ( 'alap' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:413:12: 'alap'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:414:9: ( 'any' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:414:11: 'any'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:415:10: ( 'atomic' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:415:12: 'atomic'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:416:10: ( 'bool' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:416:12: 'bool'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:417:10: ( 'choice' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:417:12: 'choice'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:418:10: ( 'do' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:418:12: 'do'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:419:10: ( 'else' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:419:12: 'else'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:420:10: ( 'false' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:420:12: 'false'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:421:10: ( 'function' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:421:12: 'function'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:422:10: ( 'if' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:422:12: 'if'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:423:10: ( 'import' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:423:12: 'import'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:424:10: ( 'int' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:424:12: 'int'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:425:10: ( 'node' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:425:12: 'node'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:426:10: ( 'or' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:426:12: 'or'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:427:10: ( 'other' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:427:12: 'other'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:428:10: ( 'out' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:428:12: 'out'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:429:10: ( 'real' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:429:12: 'real'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:430:10: ( 'package' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:430:12: 'package'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:431:10: ( 'priority' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:431:12: 'priority'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:432:10: ( 'recipe' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:432:12: 'recipe'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:433:10: ( 'star' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:433:12: 'star'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:434:10: ( 'string' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:434:12: 'string'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:435:10: ( 'try' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:435:12: 'try'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:436:10: ( 'true' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:436:12: 'true'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:437:10: ( 'until' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:437:12: 'until'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:438:10: ( 'while' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:438:12: 'while'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:441:3: ( IntegerNumber )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:441:5: IntegerNumber
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:447:3: ( '0' | '1' .. '9' ( '0' .. '9' )* )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:447:5: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:448:5: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:448:14: ( '0' .. '9' )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0 >= '0' && LA1_0 <= '9')) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:451:3: ( NonIntegerNumber )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:451:5: NonIntegerNumber
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:457:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* | '.' ( '0' .. '9' )+ )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:457:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )*
                    {
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:457:9: ( '0' .. '9' )+
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
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
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

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:457:27: ( '0' .. '9' )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0 >= '0' && LA4_0 <= '9')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:458:9: '.' ( '0' .. '9' )+
                    {
                    match('.'); 

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:458:13: ( '0' .. '9' )+
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
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:462:3: ( QUOTE ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:462:5: QUOTE ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE
            {
            mQUOTE(); 


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:463:5: ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )*
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
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:463:7: EscapeSequence
            	    {
            	    mEscapeSequence(); 


            	    }
            	    break;
            	case 2 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:464:7: ~ ( BSLASH | QUOTE | '\\r' | '\\n' )
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:472:3: ( BSLASH ( QUOTE BSLASH ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:472:5: BSLASH ( QUOTE BSLASH )
            {
            mBSLASH(); 


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:473:5: ( QUOTE BSLASH )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:473:7: QUOTE BSLASH
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:478:3: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )* | BQUOTE ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )* BQUOTE )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:478:5: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
                    {
                    if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:478:25: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( (LA8_0=='-'||(LA8_0 >= '0' && LA8_0 <= '9')||(LA8_0 >= 'A' && LA8_0 <= 'Z')||LA8_0=='_'||(LA8_0 >= 'a' && LA8_0 <= 'z')) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
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
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:479:5: BQUOTE ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )* BQUOTE
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


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:479:32: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0=='-'||(LA9_0 >= '0' && LA9_0 <= '9')||(LA9_0 >= 'A' && LA9_0 <= 'Z')||LA9_0=='_'||(LA9_0 >= 'a' && LA9_0 <= 'z')) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
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
                    } while (true);


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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:482:11: ( '&' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:482:13: '&'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:483:11: ( '.' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:483:13: '.'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:484:11: ( '!' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:484:13: '!'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:485:11: ( '|' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:485:13: '|'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:486:11: ( '#' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:486:13: '#'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:487:11: ( '+' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:487:13: '+'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:488:11: ( '*' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:488:13: '*'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:489:11: ( '_' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:489:13: '_'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:490:11: ( '-' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:490:13: '-'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:491:11: ( '\"' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:491:13: '\"'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:492:11: ( '`' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:492:13: '`'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:493:11: ( '\\\\' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:493:13: '\\\\'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:494:11: ( ',' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:494:13: ','
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:495:11: ( ';' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:495:13: ';'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:496:11: ( '(' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:496:13: '('
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:497:11: ( ')' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:497:13: ')'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:498:11: ( '<' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:498:13: '<'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:499:11: ( '>' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:499:13: '>'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:500:11: ( '{' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:500:13: '{'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:501:11: ( '}' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:501:13: '}'
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:503:12: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:503:14: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 



            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:503:19: ( options {greedy=false; } : . )*
            loop11:
            do {
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
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:503:47: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop11;
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:504:12: ( '//' (~ ( '\\n' ) )* )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:504:14: '//' (~ ( '\\n' ) )*
            {
            match("//"); 



            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:504:19: (~ ( '\\n' ) )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0 >= '\u0000' && LA12_0 <= '\t')||(LA12_0 >= '\u000B' && LA12_0 <= '\uFFFF')) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
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
    // $ANTLR end "SL_COMMENT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:506:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:506:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:506:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            int cnt13=0;
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( ((LA13_0 >= '\t' && LA13_0 <= '\n')||LA13_0=='\r'||LA13_0==' ') ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
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
                        EarlyExitException eee =
                            new EarlyExitException(13, input);
                        throw eee;
                }
                cnt13++;
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
        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:8: ( ALAP | ANY | ATOM | BOOL | CHOICE | DO | ELSE | FALSE | FUNCTION | IF | IMPORT | INT | NODE | OR | OTHER | OUT | REAL | PACKAGE | PRIORITY | RECIPE | STAR | STRING | TRY | TRUE | UNTIL | WHILE | INT_LIT | REAL_LIT | STRING_LIT | ID | AMP | DOT | NOT | BAR | SHARP | PLUS | ASTERISK | DONT_CARE | MINUS | QUOTE | BQUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LANGLE | RANGLE | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS )
        int alt14=53;
        alt14 = dfa14.predict(input);
        switch (alt14) {
            case 1 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:10: ALAP
                {
                mALAP(); 


                }
                break;
            case 2 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:15: ANY
                {
                mANY(); 


                }
                break;
            case 3 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:19: ATOM
                {
                mATOM(); 


                }
                break;
            case 4 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:24: BOOL
                {
                mBOOL(); 


                }
                break;
            case 5 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:29: CHOICE
                {
                mCHOICE(); 


                }
                break;
            case 6 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:36: DO
                {
                mDO(); 


                }
                break;
            case 7 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:39: ELSE
                {
                mELSE(); 


                }
                break;
            case 8 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:44: FALSE
                {
                mFALSE(); 


                }
                break;
            case 9 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:50: FUNCTION
                {
                mFUNCTION(); 


                }
                break;
            case 10 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:59: IF
                {
                mIF(); 


                }
                break;
            case 11 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:62: IMPORT
                {
                mIMPORT(); 


                }
                break;
            case 12 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:69: INT
                {
                mINT(); 


                }
                break;
            case 13 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:73: NODE
                {
                mNODE(); 


                }
                break;
            case 14 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:78: OR
                {
                mOR(); 


                }
                break;
            case 15 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:81: OTHER
                {
                mOTHER(); 


                }
                break;
            case 16 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:87: OUT
                {
                mOUT(); 


                }
                break;
            case 17 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:91: REAL
                {
                mREAL(); 


                }
                break;
            case 18 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:96: PACKAGE
                {
                mPACKAGE(); 


                }
                break;
            case 19 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:104: PRIORITY
                {
                mPRIORITY(); 


                }
                break;
            case 20 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:113: RECIPE
                {
                mRECIPE(); 


                }
                break;
            case 21 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:120: STAR
                {
                mSTAR(); 


                }
                break;
            case 22 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:125: STRING
                {
                mSTRING(); 


                }
                break;
            case 23 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:132: TRY
                {
                mTRY(); 


                }
                break;
            case 24 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:136: TRUE
                {
                mTRUE(); 


                }
                break;
            case 25 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:141: UNTIL
                {
                mUNTIL(); 


                }
                break;
            case 26 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:147: WHILE
                {
                mWHILE(); 


                }
                break;
            case 27 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:153: INT_LIT
                {
                mINT_LIT(); 


                }
                break;
            case 28 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:161: REAL_LIT
                {
                mREAL_LIT(); 


                }
                break;
            case 29 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:170: STRING_LIT
                {
                mSTRING_LIT(); 


                }
                break;
            case 30 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:181: ID
                {
                mID(); 


                }
                break;
            case 31 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:184: AMP
                {
                mAMP(); 


                }
                break;
            case 32 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:188: DOT
                {
                mDOT(); 


                }
                break;
            case 33 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:192: NOT
                {
                mNOT(); 


                }
                break;
            case 34 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:196: BAR
                {
                mBAR(); 


                }
                break;
            case 35 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:200: SHARP
                {
                mSHARP(); 


                }
                break;
            case 36 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:206: PLUS
                {
                mPLUS(); 


                }
                break;
            case 37 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:211: ASTERISK
                {
                mASTERISK(); 


                }
                break;
            case 38 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:220: DONT_CARE
                {
                mDONT_CARE(); 


                }
                break;
            case 39 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:230: MINUS
                {
                mMINUS(); 


                }
                break;
            case 40 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:236: QUOTE
                {
                mQUOTE(); 


                }
                break;
            case 41 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:242: BQUOTE
                {
                mBQUOTE(); 


                }
                break;
            case 42 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:249: BSLASH
                {
                mBSLASH(); 


                }
                break;
            case 43 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:256: COMMA
                {
                mCOMMA(); 


                }
                break;
            case 44 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:262: SEMI
                {
                mSEMI(); 


                }
                break;
            case 45 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:267: LPAR
                {
                mLPAR(); 


                }
                break;
            case 46 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:272: RPAR
                {
                mRPAR(); 


                }
                break;
            case 47 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:277: LANGLE
                {
                mLANGLE(); 


                }
                break;
            case 48 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:284: RANGLE
                {
                mRANGLE(); 


                }
                break;
            case 49 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:291: LCURLY
                {
                mLCURLY(); 


                }
                break;
            case 50 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:298: RCURLY
                {
                mRCURLY(); 


                }
                break;
            case 51 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:305: ML_COMMENT
                {
                mML_COMMENT(); 


                }
                break;
            case 52 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:316: SL_COMMENT
                {
                mSL_COMMENT(); 


                }
                break;
            case 53 :
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:327: WS
                {
                mWS(); 


                }
                break;

        }

    }


    protected DFA14 dfa14 = new DFA14(this);
    static final String DFA14_eotS =
        "\1\uffff\17\24\2\100\1\103\1\104\1\uffff\1\106\23\uffff\5\24\1\116"+
        "\3\24\1\122\3\24\1\126\11\24\2\uffff\1\100\6\uffff\1\24\1\144\3"+
        "\24\1\uffff\3\24\1\uffff\1\24\1\154\1\24\1\uffff\1\24\1\157\6\24"+
        "\1\166\3\24\1\172\1\uffff\1\24\1\174\1\24\1\176\3\24\1\uffff\1\u0082"+
        "\1\24\1\uffff\1\u0084\3\24\1\u0088\1\24\1\uffff\1\u008a\2\24\1\uffff"+
        "\1\24\1\uffff\1\24\1\uffff\1\u008f\2\24\1\uffff\1\u0092\1\uffff"+
        "\3\24\1\uffff\1\24\1\uffff\1\u0097\1\u0098\1\u0099\1\u009a\1\uffff"+
        "\1\24\1\u009c\1\uffff\1\u009d\2\24\1\u00a0\4\uffff\1\24\2\uffff"+
        "\1\u00a2\1\24\1\uffff\1\u00a4\1\uffff\1\u00a5\2\uffff";
    static final String DFA14_eofS =
        "\u00a6\uffff";
    static final String DFA14_minS =
        "\1\11\1\154\1\157\1\150\1\157\1\154\1\141\1\146\1\157\1\162\1\145"+
        "\1\141\1\164\1\162\1\156\1\150\2\56\1\60\1\0\1\uffff\1\101\21\uffff"+
        "\1\52\1\uffff\1\141\1\171\3\157\1\55\1\163\1\154\1\156\1\55\1\160"+
        "\1\164\1\144\1\55\1\150\1\164\1\141\1\143\1\151\1\141\1\165\1\164"+
        "\1\151\2\uffff\1\56\6\uffff\1\160\1\55\1\155\1\154\1\151\1\uffff"+
        "\1\145\1\163\1\143\1\uffff\1\157\1\55\1\145\1\uffff\1\145\1\55\1"+
        "\154\1\151\1\153\1\157\1\162\1\151\1\55\1\145\1\151\1\154\1\55\1"+
        "\uffff\1\151\1\55\1\143\1\55\1\145\1\164\1\162\1\uffff\1\55\1\162"+
        "\1\uffff\1\55\1\160\1\141\1\162\1\55\1\156\1\uffff\1\55\1\154\1"+
        "\145\1\uffff\1\143\1\uffff\1\145\1\uffff\1\55\1\151\1\164\1\uffff"+
        "\1\55\1\uffff\1\145\1\147\1\151\1\uffff\1\147\1\uffff\4\55\1\uffff"+
        "\1\157\1\55\1\uffff\1\55\1\145\1\164\1\55\4\uffff\1\156\2\uffff"+
        "\1\55\1\171\1\uffff\1\55\1\uffff\1\55\2\uffff";
    static final String DFA14_maxS =
        "\1\175\1\164\1\157\1\150\1\157\1\154\1\165\1\156\1\157\1\165\1\145"+
        "\1\162\1\164\1\162\1\156\1\150\3\71\1\uffff\1\uffff\1\172\21\uffff"+
        "\1\57\1\uffff\1\141\1\171\3\157\1\172\1\163\1\154\1\156\1\172\1"+
        "\160\1\164\1\144\1\172\1\150\1\164\2\143\1\151\1\162\1\171\1\164"+
        "\1\151\2\uffff\1\71\6\uffff\1\160\1\172\1\155\1\154\1\151\1\uffff"+
        "\1\145\1\163\1\143\1\uffff\1\157\1\172\1\145\1\uffff\1\145\1\172"+
        "\1\154\1\151\1\153\1\157\1\162\1\151\1\172\1\145\1\151\1\154\1\172"+
        "\1\uffff\1\151\1\172\1\143\1\172\1\145\1\164\1\162\1\uffff\1\172"+
        "\1\162\1\uffff\1\172\1\160\1\141\1\162\1\172\1\156\1\uffff\1\172"+
        "\1\154\1\145\1\uffff\1\143\1\uffff\1\145\1\uffff\1\172\1\151\1\164"+
        "\1\uffff\1\172\1\uffff\1\145\1\147\1\151\1\uffff\1\147\1\uffff\4"+
        "\172\1\uffff\1\157\1\172\1\uffff\1\172\1\145\1\164\1\172\4\uffff"+
        "\1\156\2\uffff\1\172\1\171\1\uffff\1\172\1\uffff\1\172\2\uffff";
    static final String DFA14_acceptS =
        "\24\uffff\1\36\1\uffff\1\37\1\41\1\42\1\43\1\44\1\45\1\46\1\47\1"+
        "\52\1\53\1\54\1\55\1\56\1\57\1\60\1\61\1\62\1\uffff\1\65\27\uffff"+
        "\1\33\1\34\1\uffff\1\40\1\50\1\35\1\51\1\63\1\64\5\uffff\1\6\3\uffff"+
        "\1\12\3\uffff\1\16\15\uffff\1\2\7\uffff\1\14\2\uffff\1\20\6\uffff"+
        "\1\27\3\uffff\1\1\1\uffff\1\4\1\uffff\1\7\3\uffff\1\15\1\uffff\1"+
        "\21\3\uffff\1\25\1\uffff\1\30\4\uffff\1\10\2\uffff\1\17\4\uffff"+
        "\1\31\1\32\1\3\1\5\1\uffff\1\13\1\24\2\uffff\1\26\1\uffff\1\22\1"+
        "\uffff\1\11\1\23";
    static final String DFA14_specialS =
        "\23\uffff\1\0\u0092\uffff}>";
    static final String[] DFA14_transitionS = {
            "\2\50\2\uffff\1\50\22\uffff\1\50\1\27\1\23\1\31\2\uffff\1\26"+
            "\1\uffff\1\41\1\42\1\33\1\32\1\37\1\35\1\22\1\47\1\20\11\21"+
            "\1\uffff\1\40\1\43\1\uffff\1\44\2\uffff\32\24\1\uffff\1\36\2"+
            "\uffff\1\34\1\25\1\1\1\2\1\3\1\4\1\5\1\6\2\24\1\7\4\24\1\10"+
            "\1\11\1\13\1\24\1\12\1\14\1\15\1\16\1\24\1\17\3\24\1\45\1\30"+
            "\1\46",
            "\1\51\1\uffff\1\52\5\uffff\1\53",
            "\1\54",
            "\1\55",
            "\1\56",
            "\1\57",
            "\1\60\23\uffff\1\61",
            "\1\62\6\uffff\1\63\1\64",
            "\1\65",
            "\1\66\1\uffff\1\67\1\70",
            "\1\71",
            "\1\72\20\uffff\1\73",
            "\1\74",
            "\1\75",
            "\1\76",
            "\1\77",
            "\1\101\1\uffff\12\101",
            "\1\101\1\uffff\12\102",
            "\12\101",
            "\12\105\1\uffff\2\105\1\uffff\ufff2\105",
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
            "\1\107\4\uffff\1\110",
            "",
            "\1\111",
            "\1\112",
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
            "\1\125",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\127",
            "\1\130",
            "\1\131\1\uffff\1\132",
            "\1\133",
            "\1\134",
            "\1\135\20\uffff\1\136",
            "\1\140\3\uffff\1\137",
            "\1\141",
            "\1\142",
            "",
            "",
            "\1\101\1\uffff\12\102",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\143",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\145",
            "\1\146",
            "\1\147",
            "",
            "\1\150",
            "\1\151",
            "\1\152",
            "",
            "\1\153",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\155",
            "",
            "\1\156",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\160",
            "\1\161",
            "\1\162",
            "\1\163",
            "\1\164",
            "\1\165",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\167",
            "\1\170",
            "\1\171",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\173",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\175",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\177",
            "\1\u0080",
            "\1\u0081",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\u0083",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\u0085",
            "\1\u0086",
            "\1\u0087",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\u0089",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\u008b",
            "\1\u008c",
            "",
            "\1\u008d",
            "",
            "\1\u008e",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\u0090",
            "\1\u0091",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\u0093",
            "\1\u0094",
            "\1\u0095",
            "",
            "\1\u0096",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\u009b",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\u009e",
            "\1\u009f",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "",
            "",
            "",
            "\1\u00a1",
            "",
            "",
            "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\u00a3",
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

    class DFA14 extends DFA {

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
        public String getDescription() {
            return "1:1: Tokens : ( ALAP | ANY | ATOM | BOOL | CHOICE | DO | ELSE | FALSE | FUNCTION | IF | IMPORT | INT | NODE | OR | OTHER | OUT | REAL | PACKAGE | PRIORITY | RECIPE | STAR | STRING | TRY | TRUE | UNTIL | WHILE | INT_LIT | REAL_LIT | STRING_LIT | ID | AMP | DOT | NOT | BAR | SHARP | PLUS | ASTERISK | DONT_CARE | MINUS | QUOTE | BQUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LANGLE | RANGLE | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA14_19 = input.LA(1);

                        s = -1;
                        if ( ((LA14_19 >= '\u0000' && LA14_19 <= '\t')||(LA14_19 >= '\u000B' && LA14_19 <= '\f')||(LA14_19 >= '\u000E' && LA14_19 <= '\uFFFF')) ) {s = 69;}

                        else s = 68;

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