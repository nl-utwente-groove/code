// $ANTLR 3.2 Sep 23, 2009 12:02:23 GCLNew.g 2010-11-18 07:10:31

package groove.control.parse;
import groove.control.*;
import java.util.LinkedList;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class GCLNewLexer extends Lexer {
    public static final int REAL_LIT=47;
    public static final int FUNCTION=7;
    public static final int DO_UNTIL=10;
    public static final int STAR=31;
    public static final int INT_LIT=46;
    public static final int FUNCTIONS=6;
    public static final int WHILE=19;
    public static final int IntegerNumber=48;
    public static final int STRING_LIT=45;
    public static final int AMP=55;
    public static final int DO=21;
    public static final int NOT=56;
    public static final int ALAP=18;
    public static final int ID=15;
    public static final int EOF=-1;
    public static final int IF=22;
    public static final int ML_COMMENT=58;
    public static final int QUOTE=50;
    public static final int LPAR=16;
    public static final int ARG=12;
    public static final int COMMA=36;
    public static final int NonIntegerNumber=49;
    public static final int DO_WHILE=9;
    public static final int PLUS=30;
    public static final int VAR=11;
    public static final int NL=54;
    public static final int DOT=35;
    public static final int CHOICE=25;
    public static final int SHARP=32;
    public static final int OTHER=34;
    public static final int NODE=37;
    public static final int ELSE=23;
    public static final int BOOL=38;
    public static final int LCURLY=13;
    public static final int INT=40;
    public static final int MINUS=57;
    public static final int SEMI=27;
    public static final int TRUE=29;
    public static final int TRY=24;
    public static final int REAL=41;
    public static final int DONT_CARE=43;
    public static final int ANY=33;
    public static final int WS=60;
    public static final int OUT=42;
    public static final int UNTIL=20;
    public static final int BLOCK=5;
    public static final int OR=26;
    public static final int RCURLY=14;
    public static final int SL_COMMENT=59;
    public static final int PROGRAM=4;
    public static final int RPAR=17;
    public static final int CALL=8;
    public static final int FALSE=44;
    public static final int CR=53;
    public static final int EscapeSequence=51;
    public static final int BSLASH=52;
    public static final int BAR=28;
    public static final int STRING=39;

        /** Strips the outer (double) quotes and unescapes all characters in a string.
         * Returns a new {@link CommonTree} with {@link GCLNewParser#ID} root token
         * and the stripped string as text.
         */
        private String toUnquoted(String text) {
            StringBuffer result = new StringBuffer();
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c == '\\') {
                    i++;
                    c = text.charAt(i);
                    result.append(c);
                } else if (c != '"') {
                    result.append(c);
                }
            }
            return result.toString();
        }


    // delegates
    // delegators

    public GCLNewLexer() {;} 
    public GCLNewLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public GCLNewLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "GCLNew.g"; }

    // $ANTLR start "ALAP"
    public final void mALAP() throws RecognitionException {
        try {
            int _type = ALAP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:192:10: ( 'alap' )
            // GCLNew.g:192:12: 'alap'
            {
            match("alap"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ALAP"

    // $ANTLR start "WHILE"
    public final void mWHILE() throws RecognitionException {
        try {
            int _type = WHILE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:193:10: ( 'while' )
            // GCLNew.g:193:12: 'while'
            {
            match("while"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHILE"

    // $ANTLR start "DO"
    public final void mDO() throws RecognitionException {
        try {
            int _type = DO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:194:10: ( 'do' )
            // GCLNew.g:194:12: 'do'
            {
            match("do"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DO"

    // $ANTLR start "UNTIL"
    public final void mUNTIL() throws RecognitionException {
        try {
            int _type = UNTIL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:195:10: ( 'until' )
            // GCLNew.g:195:12: 'until'
            {
            match("until"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "UNTIL"

    // $ANTLR start "IF"
    public final void mIF() throws RecognitionException {
        try {
            int _type = IF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:196:10: ( 'if' )
            // GCLNew.g:196:12: 'if'
            {
            match("if"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IF"

    // $ANTLR start "ELSE"
    public final void mELSE() throws RecognitionException {
        try {
            int _type = ELSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:197:10: ( 'else' )
            // GCLNew.g:197:12: 'else'
            {
            match("else"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ELSE"

    // $ANTLR start "CHOICE"
    public final void mCHOICE() throws RecognitionException {
        try {
            int _type = CHOICE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:198:10: ( 'choice' )
            // GCLNew.g:198:12: 'choice'
            {
            match("choice"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CHOICE"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:199:10: ( 'or' )
            // GCLNew.g:199:12: 'or'
            {
            match("or"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "TRY"
    public final void mTRY() throws RecognitionException {
        try {
            int _type = TRY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:200:10: ( 'try' )
            // GCLNew.g:200:12: 'try'
            {
            match("try"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TRY"

    // $ANTLR start "FUNCTION"
    public final void mFUNCTION() throws RecognitionException {
        try {
            int _type = FUNCTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:201:10: ( 'function' )
            // GCLNew.g:201:12: 'function'
            {
            match("function"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FUNCTION"

    // $ANTLR start "TRUE"
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:202:10: ( 'true' )
            // GCLNew.g:202:12: 'true'
            {
            match("true"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TRUE"

    // $ANTLR start "FALSE"
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:203:10: ( 'false' )
            // GCLNew.g:203:12: 'false'
            {
            match("false"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FALSE"

    // $ANTLR start "OTHER"
    public final void mOTHER() throws RecognitionException {
        try {
            int _type = OTHER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:204:10: ( 'other' )
            // GCLNew.g:204:12: 'other'
            {
            match("other"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OTHER"

    // $ANTLR start "ANY"
    public final void mANY() throws RecognitionException {
        try {
            int _type = ANY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:205:9: ( 'any' )
            // GCLNew.g:205:11: 'any'
            {
            match("any"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ANY"

    // $ANTLR start "NODE"
    public final void mNODE() throws RecognitionException {
        try {
            int _type = NODE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:206:10: ( 'node' )
            // GCLNew.g:206:12: 'node'
            {
            match("node"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NODE"

    // $ANTLR start "BOOL"
    public final void mBOOL() throws RecognitionException {
        try {
            int _type = BOOL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:207:10: ( 'bool' )
            // GCLNew.g:207:12: 'bool'
            {
            match("bool"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BOOL"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:208:10: ( 'string' )
            // GCLNew.g:208:12: 'string'
            {
            match("string"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:209:10: ( 'int' )
            // GCLNew.g:209:12: 'int'
            {
            match("int"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "REAL"
    public final void mREAL() throws RecognitionException {
        try {
            int _type = REAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:210:10: ( 'real' )
            // GCLNew.g:210:12: 'real'
            {
            match("real"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "REAL"

    // $ANTLR start "OUT"
    public final void mOUT() throws RecognitionException {
        try {
            int _type = OUT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:211:10: ( 'out' )
            // GCLNew.g:211:12: 'out'
            {
            match("out"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OUT"

    // $ANTLR start "INT_LIT"
    public final void mINT_LIT() throws RecognitionException {
        try {
            int _type = INT_LIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:214:3: ( IntegerNumber )
            // GCLNew.g:214:5: IntegerNumber
            {
            mIntegerNumber(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INT_LIT"

    // $ANTLR start "IntegerNumber"
    public final void mIntegerNumber() throws RecognitionException {
        try {
            // GCLNew.g:219:3: ( '0' | '1' .. '9' ( '0' .. '9' )* )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='0') ) {
                alt2=1;
            }
            else if ( ((LA2_0>='1' && LA2_0<='9')) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // GCLNew.g:219:5: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // GCLNew.g:220:5: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 
                    // GCLNew.g:220:14: ( '0' .. '9' )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0>='0' && LA1_0<='9')) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // GCLNew.g:220:15: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

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
        }
    }
    // $ANTLR end "IntegerNumber"

    // $ANTLR start "REAL_LIT"
    public final void mREAL_LIT() throws RecognitionException {
        try {
            int _type = REAL_LIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:224:3: ( NonIntegerNumber )
            // GCLNew.g:224:5: NonIntegerNumber
            {
            mNonIntegerNumber(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "REAL_LIT"

    // $ANTLR start "NonIntegerNumber"
    public final void mNonIntegerNumber() throws RecognitionException {
        try {
            // GCLNew.g:229:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* | '.' ( '0' .. '9' )+ )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( ((LA6_0>='0' && LA6_0<='9')) ) {
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
                    // GCLNew.g:229:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )*
                    {
                    // GCLNew.g:229:9: ( '0' .. '9' )+
                    int cnt3=0;
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0>='0' && LA3_0<='9')) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // GCLNew.g:229:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

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
                    // GCLNew.g:229:27: ( '0' .. '9' )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0>='0' && LA4_0<='9')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // GCLNew.g:229:28: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop4;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // GCLNew.g:230:9: '.' ( '0' .. '9' )+
                    {
                    match('.'); 
                    // GCLNew.g:230:13: ( '0' .. '9' )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( ((LA5_0>='0' && LA5_0<='9')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // GCLNew.g:230:15: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

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
        }
    }
    // $ANTLR end "NonIntegerNumber"

    // $ANTLR start "STRING_LIT"
    public final void mSTRING_LIT() throws RecognitionException {
        try {
            int _type = STRING_LIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:235:3: ( QUOTE ( EscapeSequence | ~ ( BSLASH | QUOTE | CR | NL ) )* QUOTE )
            // GCLNew.g:235:5: QUOTE ( EscapeSequence | ~ ( BSLASH | QUOTE | CR | NL ) )* QUOTE
            {
            mQUOTE(); 
            // GCLNew.g:236:5: ( EscapeSequence | ~ ( BSLASH | QUOTE | CR | NL ) )*
            loop7:
            do {
                int alt7=3;
                int LA7_0 = input.LA(1);

                if ( (LA7_0=='\\') ) {
                    alt7=1;
                }
                else if ( ((LA7_0>='\u0000' && LA7_0<='\t')||(LA7_0>='\u000B' && LA7_0<='\f')||(LA7_0>='\u000E' && LA7_0<='!')||(LA7_0>='#' && LA7_0<='[')||(LA7_0>=']' && LA7_0<='\uFFFF')) ) {
                    alt7=2;
                }


                switch (alt7) {
            	case 1 :
            	    // GCLNew.g:236:7: EscapeSequence
            	    {
            	    mEscapeSequence(); 

            	    }
            	    break;
            	case 2 :
            	    // GCLNew.g:237:7: ~ ( BSLASH | QUOTE | CR | NL )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


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
             setText(toUnquoted(getText()));     }
        finally {
        }
    }
    // $ANTLR end "STRING_LIT"

    // $ANTLR start "EscapeSequence"
    public final void mEscapeSequence() throws RecognitionException {
        try {
            // GCLNew.g:244:3: ( BSLASH ( QUOTE BSLASH ) )
            // GCLNew.g:244:5: BSLASH ( QUOTE BSLASH )
            {
            mBSLASH(); 
            // GCLNew.g:245:5: ( QUOTE BSLASH )
            // GCLNew.g:245:7: QUOTE BSLASH
            {
            mQUOTE(); 
            mBSLASH(); 

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "EscapeSequence"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:250:5: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )* )
            // GCLNew.g:250:7: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // GCLNew.g:250:27: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0=='-'||(LA8_0>='0' && LA8_0<='9')||(LA8_0>='A' && LA8_0<='Z')||LA8_0=='_'||(LA8_0>='a' && LA8_0<='z')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // GCLNew.g:
            	    {
            	    if ( input.LA(1)=='-'||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


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
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "CR"
    public final void mCR() throws RecognitionException {
        try {
            int _type = CR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:252:11: ( '\\r' )
            // GCLNew.g:252:13: '\\r'
            {
            match('\r'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CR"

    // $ANTLR start "NL"
    public final void mNL() throws RecognitionException {
        try {
            int _type = NL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:253:11: ( '\\n' )
            // GCLNew.g:253:13: '\\n'
            {
            match('\n'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NL"

    // $ANTLR start "AMP"
    public final void mAMP() throws RecognitionException {
        try {
            int _type = AMP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:254:11: ( '&' )
            // GCLNew.g:254:13: '&'
            {
            match('&'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AMP"

    // $ANTLR start "DOT"
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:255:11: ( '.' )
            // GCLNew.g:255:13: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOT"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:256:11: ( '!' )
            // GCLNew.g:256:13: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "BAR"
    public final void mBAR() throws RecognitionException {
        try {
            int _type = BAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:257:11: ( '|' )
            // GCLNew.g:257:13: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BAR"

    // $ANTLR start "SHARP"
    public final void mSHARP() throws RecognitionException {
        try {
            int _type = SHARP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:258:11: ( '#' )
            // GCLNew.g:258:13: '#'
            {
            match('#'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SHARP"

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:259:11: ( '+' )
            // GCLNew.g:259:13: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLUS"

    // $ANTLR start "STAR"
    public final void mSTAR() throws RecognitionException {
        try {
            int _type = STAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:260:11: ( '*' )
            // GCLNew.g:260:13: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STAR"

    // $ANTLR start "DONT_CARE"
    public final void mDONT_CARE() throws RecognitionException {
        try {
            int _type = DONT_CARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:261:11: ( '_' )
            // GCLNew.g:261:13: '_'
            {
            match('_'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DONT_CARE"

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:262:11: ( '-' )
            // GCLNew.g:262:13: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MINUS"

    // $ANTLR start "QUOTE"
    public final void mQUOTE() throws RecognitionException {
        try {
            int _type = QUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:263:11: ( '\"' )
            // GCLNew.g:263:13: '\"'
            {
            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "QUOTE"

    // $ANTLR start "BSLASH"
    public final void mBSLASH() throws RecognitionException {
        try {
            int _type = BSLASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:264:11: ( '\\\\' )
            // GCLNew.g:264:13: '\\\\'
            {
            match('\\'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BSLASH"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:265:11: ( ',' )
            // GCLNew.g:265:13: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "SEMI"
    public final void mSEMI() throws RecognitionException {
        try {
            int _type = SEMI;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:266:11: ( ';' )
            // GCLNew.g:266:13: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SEMI"

    // $ANTLR start "LPAR"
    public final void mLPAR() throws RecognitionException {
        try {
            int _type = LPAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:267:11: ( '(' )
            // GCLNew.g:267:13: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LPAR"

    // $ANTLR start "RPAR"
    public final void mRPAR() throws RecognitionException {
        try {
            int _type = RPAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:268:11: ( ')' )
            // GCLNew.g:268:13: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RPAR"

    // $ANTLR start "LCURLY"
    public final void mLCURLY() throws RecognitionException {
        try {
            int _type = LCURLY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:269:11: ( '{' )
            // GCLNew.g:269:13: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LCURLY"

    // $ANTLR start "RCURLY"
    public final void mRCURLY() throws RecognitionException {
        try {
            int _type = RCURLY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:270:11: ( '}' )
            // GCLNew.g:270:13: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RCURLY"

    // $ANTLR start "ML_COMMENT"
    public final void mML_COMMENT() throws RecognitionException {
        try {
            int _type = ML_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:272:12: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // GCLNew.g:272:14: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // GCLNew.g:272:19: ( options {greedy=false; } : . )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0=='*') ) {
                    int LA9_1 = input.LA(2);

                    if ( (LA9_1=='/') ) {
                        alt9=2;
                    }
                    else if ( ((LA9_1>='\u0000' && LA9_1<='.')||(LA9_1>='0' && LA9_1<='\uFFFF')) ) {
                        alt9=1;
                    }


                }
                else if ( ((LA9_0>='\u0000' && LA9_0<=')')||(LA9_0>='+' && LA9_0<='\uFFFF')) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // GCLNew.g:272:47: .
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
        }
    }
    // $ANTLR end "ML_COMMENT"

    // $ANTLR start "SL_COMMENT"
    public final void mSL_COMMENT() throws RecognitionException {
        try {
            int _type = SL_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:273:12: ( '//' ( options {greedy=false; } : . )* '\\n' )
            // GCLNew.g:273:14: '//' ( options {greedy=false; } : . )* '\\n'
            {
            match("//"); 

            // GCLNew.g:273:19: ( options {greedy=false; } : . )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0=='\n') ) {
                    alt10=2;
                }
                else if ( ((LA10_0>='\u0000' && LA10_0<='\t')||(LA10_0>='\u000B' && LA10_0<='\uFFFF')) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // GCLNew.g:273:47: .
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
        }
    }
    // $ANTLR end "SL_COMMENT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:275:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // GCLNew.g:275:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // GCLNew.g:275:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            int cnt11=0;
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( ((LA11_0>='\t' && LA11_0<='\n')||LA11_0=='\r'||LA11_0==' ') ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // GCLNew.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


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
        }
    }
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // GCLNew.g:1:8: ( ALAP | WHILE | DO | UNTIL | IF | ELSE | CHOICE | OR | TRY | FUNCTION | TRUE | FALSE | OTHER | ANY | NODE | BOOL | STRING | INT | REAL | OUT | INT_LIT | REAL_LIT | STRING_LIT | ID | CR | NL | AMP | DOT | NOT | BAR | SHARP | PLUS | STAR | DONT_CARE | MINUS | QUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS )
        int alt12=46;
        alt12 = dfa12.predict(input);
        switch (alt12) {
            case 1 :
                // GCLNew.g:1:10: ALAP
                {
                mALAP(); 

                }
                break;
            case 2 :
                // GCLNew.g:1:15: WHILE
                {
                mWHILE(); 

                }
                break;
            case 3 :
                // GCLNew.g:1:21: DO
                {
                mDO(); 

                }
                break;
            case 4 :
                // GCLNew.g:1:24: UNTIL
                {
                mUNTIL(); 

                }
                break;
            case 5 :
                // GCLNew.g:1:30: IF
                {
                mIF(); 

                }
                break;
            case 6 :
                // GCLNew.g:1:33: ELSE
                {
                mELSE(); 

                }
                break;
            case 7 :
                // GCLNew.g:1:38: CHOICE
                {
                mCHOICE(); 

                }
                break;
            case 8 :
                // GCLNew.g:1:45: OR
                {
                mOR(); 

                }
                break;
            case 9 :
                // GCLNew.g:1:48: TRY
                {
                mTRY(); 

                }
                break;
            case 10 :
                // GCLNew.g:1:52: FUNCTION
                {
                mFUNCTION(); 

                }
                break;
            case 11 :
                // GCLNew.g:1:61: TRUE
                {
                mTRUE(); 

                }
                break;
            case 12 :
                // GCLNew.g:1:66: FALSE
                {
                mFALSE(); 

                }
                break;
            case 13 :
                // GCLNew.g:1:72: OTHER
                {
                mOTHER(); 

                }
                break;
            case 14 :
                // GCLNew.g:1:78: ANY
                {
                mANY(); 

                }
                break;
            case 15 :
                // GCLNew.g:1:82: NODE
                {
                mNODE(); 

                }
                break;
            case 16 :
                // GCLNew.g:1:87: BOOL
                {
                mBOOL(); 

                }
                break;
            case 17 :
                // GCLNew.g:1:92: STRING
                {
                mSTRING(); 

                }
                break;
            case 18 :
                // GCLNew.g:1:99: INT
                {
                mINT(); 

                }
                break;
            case 19 :
                // GCLNew.g:1:103: REAL
                {
                mREAL(); 

                }
                break;
            case 20 :
                // GCLNew.g:1:108: OUT
                {
                mOUT(); 

                }
                break;
            case 21 :
                // GCLNew.g:1:112: INT_LIT
                {
                mINT_LIT(); 

                }
                break;
            case 22 :
                // GCLNew.g:1:120: REAL_LIT
                {
                mREAL_LIT(); 

                }
                break;
            case 23 :
                // GCLNew.g:1:129: STRING_LIT
                {
                mSTRING_LIT(); 

                }
                break;
            case 24 :
                // GCLNew.g:1:140: ID
                {
                mID(); 

                }
                break;
            case 25 :
                // GCLNew.g:1:143: CR
                {
                mCR(); 

                }
                break;
            case 26 :
                // GCLNew.g:1:146: NL
                {
                mNL(); 

                }
                break;
            case 27 :
                // GCLNew.g:1:149: AMP
                {
                mAMP(); 

                }
                break;
            case 28 :
                // GCLNew.g:1:153: DOT
                {
                mDOT(); 

                }
                break;
            case 29 :
                // GCLNew.g:1:157: NOT
                {
                mNOT(); 

                }
                break;
            case 30 :
                // GCLNew.g:1:161: BAR
                {
                mBAR(); 

                }
                break;
            case 31 :
                // GCLNew.g:1:165: SHARP
                {
                mSHARP(); 

                }
                break;
            case 32 :
                // GCLNew.g:1:171: PLUS
                {
                mPLUS(); 

                }
                break;
            case 33 :
                // GCLNew.g:1:176: STAR
                {
                mSTAR(); 

                }
                break;
            case 34 :
                // GCLNew.g:1:181: DONT_CARE
                {
                mDONT_CARE(); 

                }
                break;
            case 35 :
                // GCLNew.g:1:191: MINUS
                {
                mMINUS(); 

                }
                break;
            case 36 :
                // GCLNew.g:1:197: QUOTE
                {
                mQUOTE(); 

                }
                break;
            case 37 :
                // GCLNew.g:1:203: BSLASH
                {
                mBSLASH(); 

                }
                break;
            case 38 :
                // GCLNew.g:1:210: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 39 :
                // GCLNew.g:1:216: SEMI
                {
                mSEMI(); 

                }
                break;
            case 40 :
                // GCLNew.g:1:221: LPAR
                {
                mLPAR(); 

                }
                break;
            case 41 :
                // GCLNew.g:1:226: RPAR
                {
                mRPAR(); 

                }
                break;
            case 42 :
                // GCLNew.g:1:231: LCURLY
                {
                mLCURLY(); 

                }
                break;
            case 43 :
                // GCLNew.g:1:238: RCURLY
                {
                mRCURLY(); 

                }
                break;
            case 44 :
                // GCLNew.g:1:245: ML_COMMENT
                {
                mML_COMMENT(); 

                }
                break;
            case 45 :
                // GCLNew.g:1:256: SL_COMMENT
                {
                mSL_COMMENT(); 

                }
                break;
            case 46 :
                // GCLNew.g:1:267: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA12 dfa12 = new DFA12(this);
    static final String DFA12_eotS =
        "\1\uffff\16\23\2\73\1\75\1\76\1\uffff\1\100\1\101\21\uffff\3\23"+
        "\1\107\1\23\1\111\3\23\1\115\11\23\2\uffff\1\73\7\uffff\1\23\1\131"+
        "\1\23\1\uffff\1\23\1\uffff\1\134\2\23\1\uffff\1\23\1\140\1\141\7"+
        "\23\1\151\1\uffff\2\23\1\uffff\1\154\2\23\2\uffff\1\157\2\23\1\162"+
        "\1\163\1\23\1\165\1\uffff\1\166\1\167\1\uffff\1\23\1\171\1\uffff"+
        "\1\23\1\173\2\uffff\1\23\3\uffff\1\175\1\uffff\1\23\1\uffff\1\177"+
        "\1\uffff\1\23\1\uffff\1\u0081\1\uffff";
    static final String DFA12_eofS =
        "\u0082\uffff";
    static final String DFA12_minS =
        "\1\11\1\154\1\150\1\157\1\156\1\146\1\154\1\150\2\162\1\141\2\157"+
        "\1\164\1\145\2\56\1\60\1\0\1\uffff\2\11\17\uffff\1\52\1\uffff\1"+
        "\141\1\171\1\151\1\55\1\164\1\55\1\164\1\163\1\157\1\55\1\150\1"+
        "\164\1\165\1\156\1\154\1\144\1\157\1\162\1\141\2\uffff\1\56\7\uffff"+
        "\1\160\1\55\1\154\1\uffff\1\151\1\uffff\1\55\1\145\1\151\1\uffff"+
        "\1\145\2\55\1\145\1\143\1\163\1\145\1\154\1\151\1\154\1\55\1\uffff"+
        "\1\145\1\154\1\uffff\1\55\1\143\1\162\2\uffff\1\55\1\164\1\145\2"+
        "\55\1\156\1\55\1\uffff\2\55\1\uffff\1\145\1\55\1\uffff\1\151\1\55"+
        "\2\uffff\1\147\3\uffff\1\55\1\uffff\1\157\1\uffff\1\55\1\uffff\1"+
        "\156\1\uffff\1\55\1\uffff";
    static final String DFA12_maxS =
        "\1\175\1\156\1\150\1\157\2\156\1\154\1\150\1\165\1\162\1\165\2"+
        "\157\1\164\1\145\3\71\1\uffff\1\uffff\2\40\17\uffff\1\57\1\uffff"+
        "\1\141\1\171\1\151\1\172\1\164\1\172\1\164\1\163\1\157\1\172\1\150"+
        "\1\164\1\171\1\156\1\154\1\144\1\157\1\162\1\141\2\uffff\1\71\7"+
        "\uffff\1\160\1\172\1\154\1\uffff\1\151\1\uffff\1\172\1\145\1\151"+
        "\1\uffff\1\145\2\172\1\145\1\143\1\163\1\145\1\154\1\151\1\154\1"+
        "\172\1\uffff\1\145\1\154\1\uffff\1\172\1\143\1\162\2\uffff\1\172"+
        "\1\164\1\145\2\172\1\156\1\172\1\uffff\2\172\1\uffff\1\145\1\172"+
        "\1\uffff\1\151\1\172\2\uffff\1\147\3\uffff\1\172\1\uffff\1\157\1"+
        "\uffff\1\172\1\uffff\1\156\1\uffff\1\172\1\uffff";
    static final String DFA12_acceptS =
        "\23\uffff\1\30\2\uffff\1\33\1\35\1\36\1\37\1\40\1\41\1\42\1\43"+
        "\1\45\1\46\1\47\1\50\1\51\1\52\1\53\1\uffff\1\56\23\uffff\1\26\1"+
        "\25\1\uffff\1\34\1\44\1\27\1\31\1\32\1\54\1\55\3\uffff\1\3\1\uffff"+
        "\1\5\3\uffff\1\10\13\uffff\1\16\2\uffff\1\22\3\uffff\1\24\1\11\7"+
        "\uffff\1\1\2\uffff\1\6\2\uffff\1\13\2\uffff\1\17\1\20\1\uffff\1"+
        "\23\1\2\1\4\1\uffff\1\15\1\uffff\1\14\1\uffff\1\7\1\uffff\1\21\1"+
        "\uffff\1\12";
    static final String DFA12_specialS =
        "\22\uffff\1\0\157\uffff}>";
    static final String[] DFA12_transitionS = {
            "\1\46\1\25\2\uffff\1\24\22\uffff\1\46\1\27\1\22\1\31\2\uffff"+
            "\1\26\1\uffff\1\41\1\42\1\33\1\32\1\37\1\35\1\21\1\45\1\17\11"+
            "\20\1\uffff\1\40\5\uffff\32\23\1\uffff\1\36\2\uffff\1\34\1\uffff"+
            "\1\1\1\14\1\7\1\3\1\6\1\12\2\23\1\5\4\23\1\13\1\10\2\23\1\16"+
            "\1\15\1\11\1\4\1\23\1\2\3\23\1\43\1\30\1\44",
            "\1\47\1\uffff\1\50",
            "\1\51",
            "\1\52",
            "\1\53",
            "\1\54\7\uffff\1\55",
            "\1\56",
            "\1\57",
            "\1\60\1\uffff\1\61\1\62",
            "\1\63",
            "\1\65\23\uffff\1\64",
            "\1\66",
            "\1\67",
            "\1\70",
            "\1\71",
            "\1\72\1\uffff\12\72",
            "\1\72\1\uffff\12\74",
            "\12\72",
            "\12\77\1\uffff\2\77\1\uffff\ufff2\77",
            "",
            "\2\46\2\uffff\1\46\22\uffff\1\46",
            "\2\46\2\uffff\1\46\22\uffff\1\46",
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
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
            "\1\110",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
            "\1\112",
            "\1\113",
            "\1\114",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
            "\1\116",
            "\1\117",
            "\1\121\3\uffff\1\120",
            "\1\122",
            "\1\123",
            "\1\124",
            "\1\125",
            "\1\126",
            "\1\127",
            "",
            "",
            "\1\72\1\uffff\12\74",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\130",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
            "\1\132",
            "",
            "\1\133",
            "",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
            "\1\135",
            "\1\136",
            "",
            "\1\137",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
            "\1\142",
            "\1\143",
            "\1\144",
            "\1\145",
            "\1\146",
            "\1\147",
            "\1\150",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
            "",
            "\1\152",
            "\1\153",
            "",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
            "\1\155",
            "\1\156",
            "",
            "",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
            "\1\160",
            "\1\161",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
            "\1\164",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
            "",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
            "",
            "\1\170",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
            "",
            "\1\172",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
            "",
            "",
            "\1\174",
            "",
            "",
            "",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
            "",
            "\1\176",
            "",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
            "",
            "\1\u0080",
            "",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32"+
            "\23",
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
            return "1:1: Tokens : ( ALAP | WHILE | DO | UNTIL | IF | ELSE | CHOICE | OR | TRY | FUNCTION | TRUE | FALSE | OTHER | ANY | NODE | BOOL | STRING | INT | REAL | OUT | INT_LIT | REAL_LIT | STRING_LIT | ID | CR | NL | AMP | DOT | NOT | BAR | SHARP | PLUS | STAR | DONT_CARE | MINUS | QUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA12_18 = input.LA(1);

                        s = -1;
                        if ( ((LA12_18>='\u0000' && LA12_18<='\t')||(LA12_18>='\u000B' && LA12_18<='\f')||(LA12_18>='\u000E' && LA12_18<='\uFFFF')) ) {s = 63;}

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