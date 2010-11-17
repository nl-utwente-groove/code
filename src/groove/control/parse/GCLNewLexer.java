// $ANTLR 3.2 Sep 23, 2009 12:02:23 GCLNew.g 2010-11-17 18:08:38

package groove.control.parse;
import groove.control.*;
import java.util.LinkedList;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class GCLNewLexer extends Lexer {
    public static final int REAL_LIT=46;
    public static final int FUNCTION=7;
    public static final int STAR=30;
    public static final int INT_LIT=45;
    public static final int FUNCTIONS=6;
    public static final int WHILE=18;
    public static final int IntegerNumber=47;
    public static final int BOOL_TYPE=37;
    public static final int NODE_TYPE=36;
    public static final int STRING_LIT=44;
    public static final int DO=20;
    public static final int NOT=55;
    public static final int ALAP=17;
    public static final int ID=14;
    public static final int AND=54;
    public static final int EOF=-1;
    public static final int IF=21;
    public static final int ML_COMMENT=57;
    public static final int QUOTE=49;
    public static final int LPAR=15;
    public static final int ARG=11;
    public static final int COMMA=35;
    public static final int NonIntegerNumber=48;
    public static final int DO_WHILE=9;
    public static final int CH_OR=25;
    public static final int PLUS=29;
    public static final int VAR=10;
    public static final int NL=53;
    public static final int DOT=34;
    public static final int CHOICE=24;
    public static final int SHARP=31;
    public static final int OTHER=33;
    public static final int ELSE=22;
    public static final int LCURLY=12;
    public static final int MINUS=56;
    public static final int INT_TYPE=39;
    public static final int SEMI=26;
    public static final int TRUE=28;
    public static final int TRY=23;
    public static final int REAL_TYPE=40;
    public static final int DONT_CARE=42;
    public static final int ANY=32;
    public static final int WS=59;
    public static final int OUT=41;
    public static final int UNTIL=19;
    public static final int STRING_TYPE=38;
    public static final int BLOCK=5;
    public static final int OR=27;
    public static final int RCURLY=13;
    public static final int SL_COMMENT=58;
    public static final int PROGRAM=4;
    public static final int RPAR=16;
    public static final int CALL=8;
    public static final int FALSE=43;
    public static final int CR=52;
    public static final int EscapeSequence=50;
    public static final int BSLASH=51;

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
            // GCLNew.g:166:13: ( 'alap' )
            // GCLNew.g:166:15: 'alap'
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
            // GCLNew.g:167:13: ( 'while' )
            // GCLNew.g:167:15: 'while'
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
            // GCLNew.g:168:13: ( 'do' )
            // GCLNew.g:168:15: 'do'
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
            // GCLNew.g:169:13: ( 'until' )
            // GCLNew.g:169:15: 'until'
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
            // GCLNew.g:170:13: ( 'if' )
            // GCLNew.g:170:15: 'if'
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
            // GCLNew.g:171:13: ( 'else' )
            // GCLNew.g:171:15: 'else'
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
            // GCLNew.g:172:13: ( 'choice' )
            // GCLNew.g:172:15: 'choice'
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

    // $ANTLR start "CH_OR"
    public final void mCH_OR() throws RecognitionException {
        try {
            int _type = CH_OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:173:13: ( 'or' )
            // GCLNew.g:173:15: 'or'
            {
            match("or"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CH_OR"

    // $ANTLR start "TRY"
    public final void mTRY() throws RecognitionException {
        try {
            int _type = TRY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:174:13: ( 'try' )
            // GCLNew.g:174:15: 'try'
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
            // GCLNew.g:175:13: ( 'function' )
            // GCLNew.g:175:15: 'function'
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
            // GCLNew.g:176:13: ( 'true' )
            // GCLNew.g:176:15: 'true'
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
            // GCLNew.g:177:13: ( 'false' )
            // GCLNew.g:177:15: 'false'
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
            // GCLNew.g:178:13: ( 'other' )
            // GCLNew.g:178:15: 'other'
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
            // GCLNew.g:179:12: ( 'any' )
            // GCLNew.g:179:14: 'any'
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

    // $ANTLR start "NODE_TYPE"
    public final void mNODE_TYPE() throws RecognitionException {
        try {
            int _type = NODE_TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:180:13: ( 'node' )
            // GCLNew.g:180:15: 'node'
            {
            match("node"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NODE_TYPE"

    // $ANTLR start "BOOL_TYPE"
    public final void mBOOL_TYPE() throws RecognitionException {
        try {
            int _type = BOOL_TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:181:13: ( 'bool' )
            // GCLNew.g:181:15: 'bool'
            {
            match("bool"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BOOL_TYPE"

    // $ANTLR start "STRING_TYPE"
    public final void mSTRING_TYPE() throws RecognitionException {
        try {
            int _type = STRING_TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:182:13: ( 'string' )
            // GCLNew.g:182:15: 'string'
            {
            match("string"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING_TYPE"

    // $ANTLR start "INT_TYPE"
    public final void mINT_TYPE() throws RecognitionException {
        try {
            int _type = INT_TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:183:13: ( 'int' )
            // GCLNew.g:183:15: 'int'
            {
            match("int"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INT_TYPE"

    // $ANTLR start "REAL_TYPE"
    public final void mREAL_TYPE() throws RecognitionException {
        try {
            int _type = REAL_TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:184:13: ( 'real' )
            // GCLNew.g:184:15: 'real'
            {
            match("real"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "REAL_TYPE"

    // $ANTLR start "OUT"
    public final void mOUT() throws RecognitionException {
        try {
            int _type = OUT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:185:13: ( 'out' )
            // GCLNew.g:185:15: 'out'
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
            // GCLNew.g:188:3: ( IntegerNumber )
            // GCLNew.g:188:5: IntegerNumber
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
            // GCLNew.g:193:3: ( '0' | '1' .. '9' ( '0' .. '9' )* )
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
                    // GCLNew.g:193:5: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // GCLNew.g:194:5: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 
                    // GCLNew.g:194:14: ( '0' .. '9' )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0>='0' && LA1_0<='9')) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // GCLNew.g:194:15: '0' .. '9'
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
            // GCLNew.g:198:3: ( NonIntegerNumber )
            // GCLNew.g:198:5: NonIntegerNumber
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
            // GCLNew.g:203:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* | '.' ( '0' .. '9' )+ )
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
                    // GCLNew.g:203:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )*
                    {
                    // GCLNew.g:203:9: ( '0' .. '9' )+
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
                    	    // GCLNew.g:203:10: '0' .. '9'
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
                    // GCLNew.g:203:27: ( '0' .. '9' )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0>='0' && LA4_0<='9')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // GCLNew.g:203:28: '0' .. '9'
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
                    // GCLNew.g:204:9: '.' ( '0' .. '9' )+
                    {
                    match('.'); 
                    // GCLNew.g:204:13: ( '0' .. '9' )+
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
                    	    // GCLNew.g:204:15: '0' .. '9'
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
            // GCLNew.g:208:3: ( QUOTE ( EscapeSequence | ~ ( BSLASH | QUOTE | CR | NL ) )* QUOTE )
            // GCLNew.g:208:5: QUOTE ( EscapeSequence | ~ ( BSLASH | QUOTE | CR | NL ) )* QUOTE
            {
            mQUOTE(); 
            // GCLNew.g:209:5: ( EscapeSequence | ~ ( BSLASH | QUOTE | CR | NL ) )*
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
            	    // GCLNew.g:209:7: EscapeSequence
            	    {
            	    mEscapeSequence(); 

            	    }
            	    break;
            	case 2 :
            	    // GCLNew.g:210:7: ~ ( BSLASH | QUOTE | CR | NL )
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
        }
        finally {
        }
    }
    // $ANTLR end "STRING_LIT"

    // $ANTLR start "EscapeSequence"
    public final void mEscapeSequence() throws RecognitionException {
        try {
            // GCLNew.g:217:3: ( BSLASH ( QUOTE BSLASH ) )
            // GCLNew.g:217:5: BSLASH ( QUOTE BSLASH )
            {
            mBSLASH(); 
            // GCLNew.g:218:5: ( QUOTE BSLASH )
            // GCLNew.g:218:7: QUOTE BSLASH
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
            // GCLNew.g:223:5: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )* )
            // GCLNew.g:223:7: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // GCLNew.g:223:27: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
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
            // GCLNew.g:225:11: ( '\\r' )
            // GCLNew.g:225:13: '\\r'
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
            // GCLNew.g:226:11: ( '\\n' )
            // GCLNew.g:226:13: '\\n'
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

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:227:11: ( '&' )
            // GCLNew.g:227:13: '&'
            {
            match('&'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "DOT"
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:228:11: ( '.' )
            // GCLNew.g:228:13: '.'
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
            // GCLNew.g:229:11: ( '!' )
            // GCLNew.g:229:13: '!'
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

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:230:11: ( '|' )
            // GCLNew.g:230:13: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "SHARP"
    public final void mSHARP() throws RecognitionException {
        try {
            int _type = SHARP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:231:11: ( '#' )
            // GCLNew.g:231:13: '#'
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
            // GCLNew.g:232:11: ( '+' )
            // GCLNew.g:232:13: '+'
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
            // GCLNew.g:233:11: ( '*' )
            // GCLNew.g:233:13: '*'
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
            // GCLNew.g:234:11: ( '_' )
            // GCLNew.g:234:13: '_'
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
            // GCLNew.g:235:11: ( '-' )
            // GCLNew.g:235:13: '-'
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
            // GCLNew.g:236:11: ( '\"' )
            // GCLNew.g:236:13: '\"'
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
            // GCLNew.g:237:11: ( '\\\\' )
            // GCLNew.g:237:13: '\\\\'
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
            // GCLNew.g:238:11: ( ',' )
            // GCLNew.g:238:13: ','
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
            // GCLNew.g:239:11: ( ';' )
            // GCLNew.g:239:13: ';'
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
            // GCLNew.g:240:11: ( '(' )
            // GCLNew.g:240:13: '('
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
            // GCLNew.g:241:11: ( ')' )
            // GCLNew.g:241:13: ')'
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
            // GCLNew.g:242:11: ( '{' )
            // GCLNew.g:242:13: '{'
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
            // GCLNew.g:243:11: ( '}' )
            // GCLNew.g:243:13: '}'
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
            // GCLNew.g:245:12: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // GCLNew.g:245:14: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // GCLNew.g:245:19: ( options {greedy=false; } : . )*
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
            	    // GCLNew.g:245:47: .
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
            // GCLNew.g:246:12: ( '//' ( options {greedy=false; } : . )* '\\n' )
            // GCLNew.g:246:14: '//' ( options {greedy=false; } : . )* '\\n'
            {
            match("//"); 

            // GCLNew.g:246:19: ( options {greedy=false; } : . )*
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
            	    // GCLNew.g:246:47: .
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
            // GCLNew.g:248:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // GCLNew.g:248:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // GCLNew.g:248:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
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
        // GCLNew.g:1:8: ( ALAP | WHILE | DO | UNTIL | IF | ELSE | CHOICE | CH_OR | TRY | FUNCTION | TRUE | FALSE | OTHER | ANY | NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE | OUT | INT_LIT | REAL_LIT | STRING_LIT | ID | CR | NL | AND | DOT | NOT | OR | SHARP | PLUS | STAR | DONT_CARE | MINUS | QUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS )
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
                // GCLNew.g:1:45: CH_OR
                {
                mCH_OR(); 

                }
                break;
            case 9 :
                // GCLNew.g:1:51: TRY
                {
                mTRY(); 

                }
                break;
            case 10 :
                // GCLNew.g:1:55: FUNCTION
                {
                mFUNCTION(); 

                }
                break;
            case 11 :
                // GCLNew.g:1:64: TRUE
                {
                mTRUE(); 

                }
                break;
            case 12 :
                // GCLNew.g:1:69: FALSE
                {
                mFALSE(); 

                }
                break;
            case 13 :
                // GCLNew.g:1:75: OTHER
                {
                mOTHER(); 

                }
                break;
            case 14 :
                // GCLNew.g:1:81: ANY
                {
                mANY(); 

                }
                break;
            case 15 :
                // GCLNew.g:1:85: NODE_TYPE
                {
                mNODE_TYPE(); 

                }
                break;
            case 16 :
                // GCLNew.g:1:95: BOOL_TYPE
                {
                mBOOL_TYPE(); 

                }
                break;
            case 17 :
                // GCLNew.g:1:105: STRING_TYPE
                {
                mSTRING_TYPE(); 

                }
                break;
            case 18 :
                // GCLNew.g:1:117: INT_TYPE
                {
                mINT_TYPE(); 

                }
                break;
            case 19 :
                // GCLNew.g:1:126: REAL_TYPE
                {
                mREAL_TYPE(); 

                }
                break;
            case 20 :
                // GCLNew.g:1:136: OUT
                {
                mOUT(); 

                }
                break;
            case 21 :
                // GCLNew.g:1:140: INT_LIT
                {
                mINT_LIT(); 

                }
                break;
            case 22 :
                // GCLNew.g:1:148: REAL_LIT
                {
                mREAL_LIT(); 

                }
                break;
            case 23 :
                // GCLNew.g:1:157: STRING_LIT
                {
                mSTRING_LIT(); 

                }
                break;
            case 24 :
                // GCLNew.g:1:168: ID
                {
                mID(); 

                }
                break;
            case 25 :
                // GCLNew.g:1:171: CR
                {
                mCR(); 

                }
                break;
            case 26 :
                // GCLNew.g:1:174: NL
                {
                mNL(); 

                }
                break;
            case 27 :
                // GCLNew.g:1:177: AND
                {
                mAND(); 

                }
                break;
            case 28 :
                // GCLNew.g:1:181: DOT
                {
                mDOT(); 

                }
                break;
            case 29 :
                // GCLNew.g:1:185: NOT
                {
                mNOT(); 

                }
                break;
            case 30 :
                // GCLNew.g:1:189: OR
                {
                mOR(); 

                }
                break;
            case 31 :
                // GCLNew.g:1:192: SHARP
                {
                mSHARP(); 

                }
                break;
            case 32 :
                // GCLNew.g:1:198: PLUS
                {
                mPLUS(); 

                }
                break;
            case 33 :
                // GCLNew.g:1:203: STAR
                {
                mSTAR(); 

                }
                break;
            case 34 :
                // GCLNew.g:1:208: DONT_CARE
                {
                mDONT_CARE(); 

                }
                break;
            case 35 :
                // GCLNew.g:1:218: MINUS
                {
                mMINUS(); 

                }
                break;
            case 36 :
                // GCLNew.g:1:224: QUOTE
                {
                mQUOTE(); 

                }
                break;
            case 37 :
                // GCLNew.g:1:230: BSLASH
                {
                mBSLASH(); 

                }
                break;
            case 38 :
                // GCLNew.g:1:237: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 39 :
                // GCLNew.g:1:243: SEMI
                {
                mSEMI(); 

                }
                break;
            case 40 :
                // GCLNew.g:1:248: LPAR
                {
                mLPAR(); 

                }
                break;
            case 41 :
                // GCLNew.g:1:253: RPAR
                {
                mRPAR(); 

                }
                break;
            case 42 :
                // GCLNew.g:1:258: LCURLY
                {
                mLCURLY(); 

                }
                break;
            case 43 :
                // GCLNew.g:1:265: RCURLY
                {
                mRCURLY(); 

                }
                break;
            case 44 :
                // GCLNew.g:1:272: ML_COMMENT
                {
                mML_COMMENT(); 

                }
                break;
            case 45 :
                // GCLNew.g:1:283: SL_COMMENT
                {
                mSL_COMMENT(); 

                }
                break;
            case 46 :
                // GCLNew.g:1:294: WS
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
            return "1:1: Tokens : ( ALAP | WHILE | DO | UNTIL | IF | ELSE | CHOICE | CH_OR | TRY | FUNCTION | TRUE | FALSE | OTHER | ANY | NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE | OUT | INT_LIT | REAL_LIT | STRING_LIT | ID | CR | NL | AND | DOT | NOT | OR | SHARP | PLUS | STAR | DONT_CARE | MINUS | QUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS );";
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