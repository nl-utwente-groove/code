// $ANTLR 3.4 E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g 2013-12-28 13:40:09

package groove.algebra.syntax;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class ExprLexer extends Lexer {
    public static final int EOF=-1;
    public static final int AMP=4;
    public static final int ASSIGN=5;
    public static final int ASTERISK=6;
    public static final int BAR=7;
    public static final int BECOMES=8;
    public static final int BOOL=9;
    public static final int BSLASH=10;
    public static final int CALL=11;
    public static final int COLON=12;
    public static final int COMMA=13;
    public static final int CONST=14;
    public static final int DOLLAR=15;
    public static final int DONT_CARE=16;
    public static final int DOT=17;
    public static final int EQ=18;
    public static final int EscapeSequence=19;
    public static final int FALSE=20;
    public static final int FIELD=21;
    public static final int GE=22;
    public static final int GT=23;
    public static final int ID=24;
    public static final int INT=25;
    public static final int LCURLY=26;
    public static final int LE=27;
    public static final int LPAR=28;
    public static final int LT=29;
    public static final int MINUS=30;
    public static final int NAT_LIT=31;
    public static final int NEQ=32;
    public static final int NOT=33;
    public static final int Naturalumber=34;
    public static final int NonIntegerNumber=35;
    public static final int OPER=36;
    public static final int PAR=37;
    public static final int PERCENT=38;
    public static final int PLUS=39;
    public static final int QUOTE=40;
    public static final int RCURLY=41;
    public static final int REAL=42;
    public static final int REAL_LIT=43;
    public static final int RPAR=44;
    public static final int SEMI=45;
    public static final int SHARP=46;
    public static final int SLASH=47;
    public static final int STRING=48;
    public static final int STRING_LIT=49;
    public static final int TRUE=50;
    public static final int WS=51;

    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public ExprLexer() {} 
    public ExprLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public ExprLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g"; }

    // $ANTLR start "TRUE"
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:151:6: ( 'true' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:151:8: 'true'
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

    // $ANTLR start "FALSE"
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:152:7: ( 'false' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:152:9: 'false'
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

    // $ANTLR start "NAT_LIT"
    public final void mNAT_LIT() throws RecognitionException {
        try {
            int _type = NAT_LIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:155:3: ( Naturalumber )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:155:5: Naturalumber
            {
            mNaturalumber(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NAT_LIT"

    // $ANTLR start "Naturalumber"
    public final void mNaturalumber() throws RecognitionException {
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:163:3: ( '0' | '1' .. '9' ( '0' .. '9' )* )
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
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:163:5: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:164:5: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 

                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:164:14: ( '0' .. '9' )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0 >= '0' && LA1_0 <= '9')) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:
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
    // $ANTLR end "Naturalumber"

    // $ANTLR start "REAL_LIT"
    public final void mREAL_LIT() throws RecognitionException {
        try {
            int _type = REAL_LIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:166:3: ( NonIntegerNumber )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:166:5: NonIntegerNumber
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
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:174:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* | '.' ( '0' .. '9' )+ )
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
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:174:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )*
                    {
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:174:9: ( '0' .. '9' )+
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
                    	    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:
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

                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:174:27: ( '0' .. '9' )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0 >= '0' && LA4_0 <= '9')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:
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
                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:175:9: '.' ( '0' .. '9' )+
                    {
                    match('.'); 

                    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:175:13: ( '0' .. '9' )+
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
                    	    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:
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
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:177:3: ( QUOTE ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:177:5: QUOTE ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE
            {
            mQUOTE(); 


            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:178:5: ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )*
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
            	    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:178:7: EscapeSequence
            	    {
            	    mEscapeSequence(); 


            	    }
            	    break;
            	case 2 :
            	    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:179:7: ~ ( BSLASH | QUOTE | '\\r' | '\\n' )
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
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:187:3: ( BSLASH ( QUOTE BSLASH ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:187:5: BSLASH ( QUOTE BSLASH )
            {
            mBSLASH(); 


            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:188:5: ( QUOTE BSLASH )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:188:7: QUOTE BSLASH
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
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:192:5: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:192:7: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:192:27: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( ((LA8_0 >= '0' && LA8_0 <= '9')||(LA8_0 >= 'A' && LA8_0 <= 'Z')||LA8_0=='_'||(LA8_0 >= 'a' && LA8_0 <= 'z')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
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
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:194:11: ( '&' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:194:13: '&'
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

    // $ANTLR start "ASTERISK"
    public final void mASTERISK() throws RecognitionException {
        try {
            int _type = ASTERISK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:195:11: ( '*' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:195:13: '*'
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

    // $ANTLR start "BAR"
    public final void mBAR() throws RecognitionException {
        try {
            int _type = BAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:196:11: ( '|' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:196:13: '|'
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

    // $ANTLR start "BSLASH"
    public final void mBSLASH() throws RecognitionException {
        try {
            int _type = BSLASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:197:11: ( '\\\\' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:197:13: '\\\\'
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

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:198:11: ( ':' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:198:13: ':'
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

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:199:11: ( ',' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:199:13: ','
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

    // $ANTLR start "DOLLAR"
    public final void mDOLLAR() throws RecognitionException {
        try {
            int _type = DOLLAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:200:11: ( '$' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:200:13: '$'
            {
            match('$'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DOLLAR"

    // $ANTLR start "DONT_CARE"
    public final void mDONT_CARE() throws RecognitionException {
        try {
            int _type = DONT_CARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:201:11: ( '_' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:201:13: '_'
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

    // $ANTLR start "DOT"
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:202:11: ( '.' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:202:13: '.'
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

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:203:11: ( '-' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:203:13: '-'
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

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:204:11: ( '!' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:204:13: '!'
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

    // $ANTLR start "PERCENT"
    public final void mPERCENT() throws RecognitionException {
        try {
            int _type = PERCENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:205:11: ( '%' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:205:13: '%'
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

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:206:11: ( '+' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:206:13: '+'
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

    // $ANTLR start "QUOTE"
    public final void mQUOTE() throws RecognitionException {
        try {
            int _type = QUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:207:11: ( '\"' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:207:13: '\"'
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

    // $ANTLR start "SEMI"
    public final void mSEMI() throws RecognitionException {
        try {
            int _type = SEMI;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:208:11: ( ';' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:208:13: ';'
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

    // $ANTLR start "SHARP"
    public final void mSHARP() throws RecognitionException {
        try {
            int _type = SHARP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:209:11: ( '#' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:209:13: '#'
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

    // $ANTLR start "SLASH"
    public final void mSLASH() throws RecognitionException {
        try {
            int _type = SLASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:210:11: ( '/' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:210:13: '/'
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

    // $ANTLR start "LPAR"
    public final void mLPAR() throws RecognitionException {
        try {
            int _type = LPAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:211:11: ( '(' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:211:13: '('
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
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:212:11: ( ')' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:212:13: ')'
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
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:213:11: ( '{' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:213:13: '{'
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
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:214:11: ( '}' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:214:13: '}'
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

    // $ANTLR start "ASSIGN"
    public final void mASSIGN() throws RecognitionException {
        try {
            int _type = ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:215:11: ( '=' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:215:13: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ASSIGN"

    // $ANTLR start "BECOMES"
    public final void mBECOMES() throws RecognitionException {
        try {
            int _type = BECOMES;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:216:11: ( ':=' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:216:13: ':='
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

    // $ANTLR start "EQ"
    public final void mEQ() throws RecognitionException {
        try {
            int _type = EQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:217:11: ( '==' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:217:13: '=='
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
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:218:11: ( '!=' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:218:13: '!='
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

    // $ANTLR start "GT"
    public final void mGT() throws RecognitionException {
        try {
            int _type = GT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:219:11: ( '>' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:219:13: '>'
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
    // $ANTLR end "GT"

    // $ANTLR start "GE"
    public final void mGE() throws RecognitionException {
        try {
            int _type = GE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:220:11: ( '>=' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:220:13: '>='
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
    // $ANTLR end "GE"

    // $ANTLR start "LT"
    public final void mLT() throws RecognitionException {
        try {
            int _type = LT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:221:11: ( '<' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:221:13: '<'
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
    // $ANTLR end "LT"

    // $ANTLR start "LE"
    public final void mLE() throws RecognitionException {
        try {
            int _type = LE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:222:11: ( '<=' )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:222:13: '<='
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
    // $ANTLR end "LE"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:224:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:224:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:224:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0 >= '\t' && LA9_0 <= '\n')||LA9_0=='\r'||LA9_0==' ') ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:
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
            	    if ( cnt9 >= 1 ) break loop9;
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
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
        // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:8: ( TRUE | FALSE | NAT_LIT | REAL_LIT | STRING_LIT | ID | AMP | ASTERISK | BAR | BSLASH | COLON | COMMA | DOLLAR | DONT_CARE | DOT | MINUS | NOT | PERCENT | PLUS | QUOTE | SEMI | SHARP | SLASH | LPAR | RPAR | LCURLY | RCURLY | ASSIGN | BECOMES | EQ | NEQ | GT | GE | LT | LE | WS )
        int alt10=36;
        alt10 = dfa10.predict(input);
        switch (alt10) {
            case 1 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:10: TRUE
                {
                mTRUE(); 


                }
                break;
            case 2 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:15: FALSE
                {
                mFALSE(); 


                }
                break;
            case 3 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:21: NAT_LIT
                {
                mNAT_LIT(); 


                }
                break;
            case 4 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:29: REAL_LIT
                {
                mREAL_LIT(); 


                }
                break;
            case 5 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:38: STRING_LIT
                {
                mSTRING_LIT(); 


                }
                break;
            case 6 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:49: ID
                {
                mID(); 


                }
                break;
            case 7 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:52: AMP
                {
                mAMP(); 


                }
                break;
            case 8 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:56: ASTERISK
                {
                mASTERISK(); 


                }
                break;
            case 9 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:65: BAR
                {
                mBAR(); 


                }
                break;
            case 10 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:69: BSLASH
                {
                mBSLASH(); 


                }
                break;
            case 11 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:76: COLON
                {
                mCOLON(); 


                }
                break;
            case 12 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:82: COMMA
                {
                mCOMMA(); 


                }
                break;
            case 13 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:88: DOLLAR
                {
                mDOLLAR(); 


                }
                break;
            case 14 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:95: DONT_CARE
                {
                mDONT_CARE(); 


                }
                break;
            case 15 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:105: DOT
                {
                mDOT(); 


                }
                break;
            case 16 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:109: MINUS
                {
                mMINUS(); 


                }
                break;
            case 17 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:115: NOT
                {
                mNOT(); 


                }
                break;
            case 18 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:119: PERCENT
                {
                mPERCENT(); 


                }
                break;
            case 19 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:127: PLUS
                {
                mPLUS(); 


                }
                break;
            case 20 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:132: QUOTE
                {
                mQUOTE(); 


                }
                break;
            case 21 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:138: SEMI
                {
                mSEMI(); 


                }
                break;
            case 22 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:143: SHARP
                {
                mSHARP(); 


                }
                break;
            case 23 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:149: SLASH
                {
                mSLASH(); 


                }
                break;
            case 24 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:155: LPAR
                {
                mLPAR(); 


                }
                break;
            case 25 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:160: RPAR
                {
                mRPAR(); 


                }
                break;
            case 26 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:165: LCURLY
                {
                mLCURLY(); 


                }
                break;
            case 27 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:172: RCURLY
                {
                mRCURLY(); 


                }
                break;
            case 28 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:179: ASSIGN
                {
                mASSIGN(); 


                }
                break;
            case 29 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:186: BECOMES
                {
                mBECOMES(); 


                }
                break;
            case 30 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:194: EQ
                {
                mEQ(); 


                }
                break;
            case 31 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:197: NEQ
                {
                mNEQ(); 


                }
                break;
            case 32 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:201: GT
                {
                mGT(); 


                }
                break;
            case 33 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:204: GE
                {
                mGE(); 


                }
                break;
            case 34 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:207: LT
                {
                mLT(); 


                }
                break;
            case 35 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:210: LE
                {
                mLE(); 


                }
                break;
            case 36 :
                // E:\\Eclipse\\groove-head\\src\\groove\\algebra\\syntax\\Expr.g:1:213: WS
                {
                mWS(); 


                }
                break;

        }

    }


    protected DFA10 dfa10 = new DFA10(this);
    static final String DFA10_eotS =
        "\1\uffff\2\7\2\41\1\44\1\45\5\uffff\1\50\4\uffff\1\52\11\uffff\1"+
        "\54\1\56\1\60\1\uffff\2\7\2\uffff\1\41\15\uffff\2\7\1\65\1\7\1\uffff"+
        "\1\67\1\uffff";
    static final String DFA10_eofS =
        "\70\uffff";
    static final String DFA10_minS =
        "\1\11\1\162\1\141\2\56\1\60\1\0\5\uffff\1\75\4\uffff\1\75\11\uffff"+
        "\3\75\1\uffff\1\165\1\154\2\uffff\1\56\15\uffff\1\145\1\163\1\60"+
        "\1\145\1\uffff\1\60\1\uffff";
    static final String DFA10_maxS =
        "\1\175\1\162\1\141\3\71\1\uffff\5\uffff\1\75\4\uffff\1\75\11\uffff"+
        "\3\75\1\uffff\1\165\1\154\2\uffff\1\71\15\uffff\1\145\1\163\1\172"+
        "\1\145\1\uffff\1\172\1\uffff";
    static final String DFA10_acceptS =
        "\7\uffff\1\6\1\7\1\10\1\11\1\12\1\uffff\1\14\1\15\1\16\1\20\1\uffff"+
        "\1\22\1\23\1\25\1\26\1\27\1\30\1\31\1\32\1\33\3\uffff\1\44\2\uffff"+
        "\1\3\1\4\1\uffff\1\17\1\24\1\5\1\35\1\13\1\37\1\21\1\36\1\34\1\41"+
        "\1\40\1\43\1\42\4\uffff\1\1\1\uffff\1\2";
    static final String DFA10_specialS =
        "\6\uffff\1\0\61\uffff}>";
    static final String[] DFA10_transitionS = {
            "\2\36\2\uffff\1\36\22\uffff\1\36\1\21\1\6\1\25\1\16\1\22\1\10"+
            "\1\uffff\1\27\1\30\1\11\1\23\1\15\1\20\1\5\1\26\1\3\11\4\1\14"+
            "\1\24\1\35\1\33\1\34\2\uffff\32\7\1\uffff\1\13\2\uffff\1\17"+
            "\1\uffff\5\7\1\2\15\7\1\1\6\7\1\31\1\12\1\32",
            "\1\37",
            "\1\40",
            "\1\42\1\uffff\12\42",
            "\1\42\1\uffff\12\43",
            "\12\42",
            "\12\46\1\uffff\2\46\1\uffff\ufff2\46",
            "",
            "",
            "",
            "",
            "",
            "\1\47",
            "",
            "",
            "",
            "",
            "\1\51",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\53",
            "\1\55",
            "\1\57",
            "",
            "\1\61",
            "\1\62",
            "",
            "",
            "\1\42\1\uffff\12\43",
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
            "\1\63",
            "\1\64",
            "\12\7\7\uffff\32\7\4\uffff\1\7\1\uffff\32\7",
            "\1\66",
            "",
            "\12\7\7\uffff\32\7\4\uffff\1\7\1\uffff\32\7",
            ""
    };

    static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
    static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
    static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
    static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
    static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
    static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
    static final short[][] DFA10_transition;

    static {
        int numStates = DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
        }
    }

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = DFA10_eot;
            this.eof = DFA10_eof;
            this.min = DFA10_min;
            this.max = DFA10_max;
            this.accept = DFA10_accept;
            this.special = DFA10_special;
            this.transition = DFA10_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( TRUE | FALSE | NAT_LIT | REAL_LIT | STRING_LIT | ID | AMP | ASTERISK | BAR | BSLASH | COLON | COMMA | DOLLAR | DONT_CARE | DOT | MINUS | NOT | PERCENT | PLUS | QUOTE | SEMI | SHARP | SLASH | LPAR | RPAR | LCURLY | RCURLY | ASSIGN | BECOMES | EQ | NEQ | GT | GE | LT | LE | WS );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA10_6 = input.LA(1);

                        s = -1;
                        if ( ((LA10_6 >= '\u0000' && LA10_6 <= '\t')||(LA10_6 >= '\u000B' && LA10_6 <= '\f')||(LA10_6 >= '\u000E' && LA10_6 <= '\uFFFF')) ) {s = 38;}

                        else s = 37;

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 10, _s, input);
            error(nvae);
            throw nvae;
        }

    }
 

}