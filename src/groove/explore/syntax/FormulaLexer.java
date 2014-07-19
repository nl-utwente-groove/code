// $ANTLR 3.4 E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g 2014-07-19 10:54:06

package groove.explore.syntax;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class FormulaLexer extends Lexer {
    public static final int EOF=-1;
    public static final int AMP=4;
    public static final int AND=5;
    public static final int BAR=6;
    public static final int BOOL=7;
    public static final int BSLASH=8;
    public static final int CALL=9;
    public static final int COMMA=10;
    public static final int CONST=11;
    public static final int DONT_CARE=12;
    public static final int DOT=13;
    public static final int EQUIV=14;
    public static final int EscapeSequence=15;
    public static final int FALSE=16;
    public static final int FIELD=17;
    public static final int ID=18;
    public static final int IMPL=19;
    public static final int IMPL_BY=20;
    public static final int INT=21;
    public static final int LPAR=22;
    public static final int MINUS=23;
    public static final int NAT_LIT=24;
    public static final int NOT=25;
    public static final int NaturalNumber=26;
    public static final int NonIntegerNumber=27;
    public static final int OPER=28;
    public static final int OR=29;
    public static final int PAR=30;
    public static final int QUOTE=31;
    public static final int REAL=32;
    public static final int REAL_LIT=33;
    public static final int RPAR=34;
    public static final int STRING=35;
    public static final int STRING_LIT=36;
    public static final int TRUE=37;
    public static final int WS=38;

    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public FormulaLexer() {} 
    public FormulaLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public FormulaLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g"; }

    // $ANTLR start "TRUE"
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:104:6: ( 'true' )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:104:8: 'true'
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
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:105:7: ( 'false' )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:105:9: 'false'
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

    // $ANTLR start "STRING_LIT"
    public final void mSTRING_LIT() throws RecognitionException {
        try {
            int _type = STRING_LIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:108:3: ( QUOTE ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:108:5: QUOTE ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE
            {
            mQUOTE(); 


            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:109:5: ( EscapeSequence |~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )*
            loop1:
            do {
                int alt1=3;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='\\') ) {
                    alt1=1;
                }
                else if ( ((LA1_0 >= '\u0000' && LA1_0 <= '\t')||(LA1_0 >= '\u000B' && LA1_0 <= '\f')||(LA1_0 >= '\u000E' && LA1_0 <= '!')||(LA1_0 >= '#' && LA1_0 <= '[')||(LA1_0 >= ']' && LA1_0 <= '\uFFFF')) ) {
                    alt1=2;
                }


                switch (alt1) {
            	case 1 :
            	    // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:109:7: EscapeSequence
            	    {
            	    mEscapeSequence(); 


            	    }
            	    break;
            	case 2 :
            	    // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:110:7: ~ ( BSLASH | QUOTE | '\\r' | '\\n' )
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
            	    break loop1;
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
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:118:3: ( BSLASH ( QUOTE BSLASH ) )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:118:5: BSLASH ( QUOTE BSLASH )
            {
            mBSLASH(); 


            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:119:5: ( QUOTE BSLASH )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:119:7: QUOTE BSLASH
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

    // $ANTLR start "NAT_LIT"
    public final void mNAT_LIT() throws RecognitionException {
        try {
            int _type = NAT_LIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:124:3: ( NaturalNumber )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:124:5: NaturalNumber
            {
            mNaturalNumber(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NAT_LIT"

    // $ANTLR start "NaturalNumber"
    public final void mNaturalNumber() throws RecognitionException {
        try {
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:132:3: ( '0' | '1' .. '9' ( '0' .. '9' )* )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='0') ) {
                alt3=1;
            }
            else if ( ((LA3_0 >= '1' && LA3_0 <= '9')) ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;

            }
            switch (alt3) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:132:5: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:133:5: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 

                    // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:133:14: ( '0' .. '9' )*
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( ((LA2_0 >= '0' && LA2_0 <= '9')) ) {
                            alt2=1;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:
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
                    	    break loop2;
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
    // $ANTLR end "NaturalNumber"

    // $ANTLR start "REAL_LIT"
    public final void mREAL_LIT() throws RecognitionException {
        try {
            int _type = REAL_LIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:135:3: ( NonIntegerNumber )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:135:5: NonIntegerNumber
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
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:143:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* | '.' ( '0' .. '9' )+ )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( ((LA7_0 >= '0' && LA7_0 <= '9')) ) {
                alt7=1;
            }
            else if ( (LA7_0=='.') ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;

            }
            switch (alt7) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:143:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )*
                    {
                    // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:143:9: ( '0' .. '9' )+
                    int cnt4=0;
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0 >= '0' && LA4_0 <= '9')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:
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
                                EarlyExitException eee =
                                    new EarlyExitException(4, input);
                                throw eee;
                        }
                        cnt4++;
                    } while (true);


                    match('.'); 

                    // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:143:27: ( '0' .. '9' )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( ((LA5_0 >= '0' && LA5_0 <= '9')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:
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
                    	    break loop5;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:144:9: '.' ( '0' .. '9' )+
                    {
                    match('.'); 

                    // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:144:13: ( '0' .. '9' )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0 >= '0' && LA6_0 <= '9')) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:
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
                    	    if ( cnt6 >= 1 ) break loop6;
                                EarlyExitException eee =
                                    new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
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

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:145:5: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:145:7: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:145:27: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( ((LA8_0 >= '0' && LA8_0 <= '9')||(LA8_0 >= 'A' && LA8_0 <= 'Z')||LA8_0=='_'||(LA8_0 >= 'a' && LA8_0 <= 'z')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:
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
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:147:11: ( '&' )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:147:13: '&'
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

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:148:11: ( '&&' )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:148:13: '&&'
            {
            match("&&"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "BAR"
    public final void mBAR() throws RecognitionException {
        try {
            int _type = BAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:149:11: ( '|' )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:149:13: '|'
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
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:150:11: ( '\\\\' )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:150:13: '\\\\'
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
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:151:11: ( ',' )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:151:13: ','
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

    // $ANTLR start "DONT_CARE"
    public final void mDONT_CARE() throws RecognitionException {
        try {
            int _type = DONT_CARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:152:11: ( '_' )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:152:13: '_'
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
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:153:11: ( '.' )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:153:13: '.'
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

    // $ANTLR start "EQUIV"
    public final void mEQUIV() throws RecognitionException {
        try {
            int _type = EQUIV;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:154:11: ( '<->' )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:154:13: '<->'
            {
            match("<->"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EQUIV"

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:155:11: ( '-' )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:155:13: '-'
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

    // $ANTLR start "LPAR"
    public final void mLPAR() throws RecognitionException {
        try {
            int _type = LPAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:156:11: ( '(' )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:156:13: '('
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
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:157:11: ( ')' )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:157:13: ')'
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

    // $ANTLR start "IMPL"
    public final void mIMPL() throws RecognitionException {
        try {
            int _type = IMPL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:158:11: ( '->' )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:158:13: '->'
            {
            match("->"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "IMPL"

    // $ANTLR start "IMPL_BY"
    public final void mIMPL_BY() throws RecognitionException {
        try {
            int _type = IMPL_BY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:159:11: ( '<-' )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:159:13: '<-'
            {
            match("<-"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "IMPL_BY"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:160:11: ( '||' )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:160:13: '||'
            {
            match("||"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:161:11: ( '!' )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:161:13: '!'
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

    // $ANTLR start "QUOTE"
    public final void mQUOTE() throws RecognitionException {
        try {
            int _type = QUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:162:11: ( '\"' )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:162:13: '\"'
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

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:164:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:164:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:164:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
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
            	    // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:
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
        // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:8: ( TRUE | FALSE | STRING_LIT | NAT_LIT | REAL_LIT | ID | AMP | AND | BAR | BSLASH | COMMA | DONT_CARE | DOT | EQUIV | MINUS | LPAR | RPAR | IMPL | IMPL_BY | OR | NOT | QUOTE | WS )
        int alt10=23;
        alt10 = dfa10.predict(input);
        switch (alt10) {
            case 1 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:10: TRUE
                {
                mTRUE(); 


                }
                break;
            case 2 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:15: FALSE
                {
                mFALSE(); 


                }
                break;
            case 3 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:21: STRING_LIT
                {
                mSTRING_LIT(); 


                }
                break;
            case 4 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:32: NAT_LIT
                {
                mNAT_LIT(); 


                }
                break;
            case 5 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:40: REAL_LIT
                {
                mREAL_LIT(); 


                }
                break;
            case 6 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:49: ID
                {
                mID(); 


                }
                break;
            case 7 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:52: AMP
                {
                mAMP(); 


                }
                break;
            case 8 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:56: AND
                {
                mAND(); 


                }
                break;
            case 9 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:60: BAR
                {
                mBAR(); 


                }
                break;
            case 10 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:64: BSLASH
                {
                mBSLASH(); 


                }
                break;
            case 11 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:71: COMMA
                {
                mCOMMA(); 


                }
                break;
            case 12 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:77: DONT_CARE
                {
                mDONT_CARE(); 


                }
                break;
            case 13 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:87: DOT
                {
                mDOT(); 


                }
                break;
            case 14 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:91: EQUIV
                {
                mEQUIV(); 


                }
                break;
            case 15 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:97: MINUS
                {
                mMINUS(); 


                }
                break;
            case 16 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:103: LPAR
                {
                mLPAR(); 


                }
                break;
            case 17 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:108: RPAR
                {
                mRPAR(); 


                }
                break;
            case 18 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:113: IMPL
                {
                mIMPL(); 


                }
                break;
            case 19 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:118: IMPL_BY
                {
                mIMPL_BY(); 


                }
                break;
            case 20 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:126: OR
                {
                mOR(); 


                }
                break;
            case 21 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:129: NOT
                {
                mNOT(); 


                }
                break;
            case 22 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:133: QUOTE
                {
                mQUOTE(); 


                }
                break;
            case 23 :
                // E:\\Eclipse\\groove\\src\\groove\\explore\\syntax\\Formula.g:1:139: WS
                {
                mWS(); 


                }
                break;

        }

    }


    protected DFA10 dfa10 = new DFA10(this);
    static final String DFA10_eotS =
        "\1\uffff\2\7\1\25\2\27\1\32\1\uffff\1\34\1\36\4\uffff\1\41\4\uffff"+
        "\2\7\4\uffff\1\27\5\uffff\1\45\2\uffff\2\7\2\uffff\1\50\1\7\1\uffff"+
        "\1\52\1\uffff";
    static final String DFA10_eofS =
        "\53\uffff";
    static final String DFA10_minS =
        "\1\11\1\162\1\141\1\0\2\56\1\60\1\uffff\1\46\1\174\3\uffff\1\55"+
        "\1\76\4\uffff\1\165\1\154\4\uffff\1\56\5\uffff\1\76\2\uffff\1\145"+
        "\1\163\2\uffff\1\60\1\145\1\uffff\1\60\1\uffff";
    static final String DFA10_maxS =
        "\1\174\1\162\1\141\1\uffff\3\71\1\uffff\1\46\1\174\3\uffff\1\55"+
        "\1\76\4\uffff\1\165\1\154\4\uffff\1\71\5\uffff\1\76\2\uffff\1\145"+
        "\1\163\2\uffff\1\172\1\145\1\uffff\1\172\1\uffff";
    static final String DFA10_acceptS =
        "\7\uffff\1\6\2\uffff\1\12\1\13\1\14\2\uffff\1\20\1\21\1\25\1\27"+
        "\2\uffff\1\26\1\3\1\4\1\5\1\uffff\1\15\1\10\1\7\1\24\1\11\1\uffff"+
        "\1\22\1\17\2\uffff\1\16\1\23\2\uffff\1\1\1\uffff\1\2";
    static final String DFA10_specialS =
        "\3\uffff\1\0\47\uffff}>";
    static final String[] DFA10_transitionS = {
            "\2\22\2\uffff\1\22\22\uffff\1\22\1\21\1\3\3\uffff\1\10\1\uffff"+
            "\1\17\1\20\2\uffff\1\13\1\16\1\6\1\uffff\1\4\11\5\2\uffff\1"+
            "\15\4\uffff\32\7\1\uffff\1\12\2\uffff\1\14\1\uffff\5\7\1\2\15"+
            "\7\1\1\6\7\1\uffff\1\11",
            "\1\23",
            "\1\24",
            "\12\26\1\uffff\2\26\1\uffff\ufff2\26",
            "\1\30\1\uffff\12\30",
            "\1\30\1\uffff\12\31",
            "\12\30",
            "",
            "\1\33",
            "\1\35",
            "",
            "",
            "",
            "\1\37",
            "\1\40",
            "",
            "",
            "",
            "",
            "\1\42",
            "\1\43",
            "",
            "",
            "",
            "",
            "\1\30\1\uffff\12\31",
            "",
            "",
            "",
            "",
            "",
            "\1\44",
            "",
            "",
            "\1\46",
            "\1\47",
            "",
            "",
            "\12\7\7\uffff\32\7\4\uffff\1\7\1\uffff\32\7",
            "\1\51",
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
            return "1:1: Tokens : ( TRUE | FALSE | STRING_LIT | NAT_LIT | REAL_LIT | ID | AMP | AND | BAR | BSLASH | COMMA | DONT_CARE | DOT | EQUIV | MINUS | LPAR | RPAR | IMPL | IMPL_BY | OR | NOT | QUOTE | WS );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA10_3 = input.LA(1);

                        s = -1;
                        if ( ((LA10_3 >= '\u0000' && LA10_3 <= '\t')||(LA10_3 >= '\u000B' && LA10_3 <= '\f')||(LA10_3 >= '\u000E' && LA10_3 <= '\uFFFF')) ) {s = 22;}

                        else s = 21;

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