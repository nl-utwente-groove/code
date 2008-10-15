// $ANTLR 3.1b1 GCL.g 2008-10-15 14:55:26

package groove.control.parse;
import groove.control.*;
import java.util.LinkedList;


import org.antlr.runtime.*;
//
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")      
public class GCLLexer extends Lexer {
    public static final int FUNCTION=7;
    public static final int STAR=22;
    public static final int SHARP=23;
    public static final int OTHER=25;
    public static final int FUNCTIONS=6;
    public static final int WHILE=12;
    public static final int ELSE=16;
    public static final int DO=13;
    public static final int NOT=29;
    public static final int ALAP=11;
    public static final int AND=26;
    public static final int EOF=-1;
    public static final int TRUE=20;
    public static final int TRY=15;
    public static final int IF=17;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int ANY=24;
    public static final int WS=30;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int COMMA=27;
    public static final int UNTIL=14;
    public static final int IDENTIFIER=9;
    public static final int BLOCK=5;
    public static final int OR=10;
    public static final int CH_OR=19;
    public static final int PROGRAM=4;
    public static final int PLUS=21;
    public static final int CALL=8;
    public static final int DOT=28;
    public static final int CHOICE=18;

    // delegates
    // delegators

    public GCLLexer() {;} 
    public GCLLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public GCLLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "GCL.g"; }

    // $ANTLR start T__31
    public final void mT__31() throws RecognitionException {
        try {
            int _type = T__31;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:9:7: ( '{' )
            // GCL.g:9:9: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__31

    // $ANTLR start T__32
    public final void mT__32() throws RecognitionException {
        try {
            int _type = T__32;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:10:7: ( '}' )
            // GCL.g:10:9: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__32

    // $ANTLR start T__33
    public final void mT__33() throws RecognitionException {
        try {
            int _type = T__33;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:11:7: ( '(' )
            // GCL.g:11:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__33

    // $ANTLR start T__34
    public final void mT__34() throws RecognitionException {
        try {
            int _type = T__34;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:12:7: ( ')' )
            // GCL.g:12:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__34

    // $ANTLR start T__35
    public final void mT__35() throws RecognitionException {
        try {
            int _type = T__35;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:13:7: ( ';' )
            // GCL.g:13:9: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__35

    // $ANTLR start ALAP
    public final void mALAP() throws RecognitionException {
        try {
            int _type = ALAP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:94:7: ( 'alap' )
            // GCL.g:94:9: 'alap'
            {
            match("alap"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end ALAP

    // $ANTLR start WHILE
    public final void mWHILE() throws RecognitionException {
        try {
            int _type = WHILE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:95:7: ( 'while' )
            // GCL.g:95:9: 'while'
            {
            match("while"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end WHILE

    // $ANTLR start DO
    public final void mDO() throws RecognitionException {
        try {
            int _type = DO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:96:5: ( 'do' )
            // GCL.g:96:7: 'do'
            {
            match("do"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end DO

    // $ANTLR start UNTIL
    public final void mUNTIL() throws RecognitionException {
        try {
            int _type = UNTIL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:97:9: ( 'until' )
            // GCL.g:97:11: 'until'
            {
            match("until"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end UNTIL

    // $ANTLR start IF
    public final void mIF() throws RecognitionException {
        try {
            int _type = IF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:98:5: ( 'if' )
            // GCL.g:98:7: 'if'
            {
            match("if"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end IF

    // $ANTLR start ELSE
    public final void mELSE() throws RecognitionException {
        try {
            int _type = ELSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:99:6: ( 'else' )
            // GCL.g:99:8: 'else'
            {
            match("else"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end ELSE

    // $ANTLR start CHOICE
    public final void mCHOICE() throws RecognitionException {
        try {
            int _type = CHOICE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:100:8: ( 'choice' )
            // GCL.g:100:10: 'choice'
            {
            match("choice"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end CHOICE

    // $ANTLR start CH_OR
    public final void mCH_OR() throws RecognitionException {
        try {
            int _type = CH_OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:101:8: ( 'or' )
            // GCL.g:101:10: 'or'
            {
            match("or"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end CH_OR

    // $ANTLR start TRY
    public final void mTRY() throws RecognitionException {
        try {
            int _type = TRY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:102:6: ( 'try' )
            // GCL.g:102:8: 'try'
            {
            match("try"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end TRY

    // $ANTLR start FUNCTION
    public final void mFUNCTION() throws RecognitionException {
        try {
            int _type = FUNCTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:103:9: ( 'function' )
            // GCL.g:103:11: 'function'
            {
            match("function"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end FUNCTION

    // $ANTLR start TRUE
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:104:6: ( 'true' )
            // GCL.g:104:8: 'true'
            {
            match("true"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end TRUE

    // $ANTLR start OTHER
    public final void mOTHER() throws RecognitionException {
        try {
            int _type = OTHER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:105:7: ( 'other' )
            // GCL.g:105:9: 'other'
            {
            match("other"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end OTHER

    // $ANTLR start ANY
    public final void mANY() throws RecognitionException {
        try {
            int _type = ANY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:106:6: ( 'any' )
            // GCL.g:106:8: 'any'
            {
            match("any"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end ANY

    // $ANTLR start IDENTIFIER
    public final void mIDENTIFIER() throws RecognitionException {
        try {
            int _type = IDENTIFIER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:109:13: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '-' | '_' )* )
            // GCL.g:109:15: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '-' | '_' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // GCL.g:109:35: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '-' | '_' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='-'||(LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // GCL.g:
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
            	    break loop1;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end IDENTIFIER

    // $ANTLR start AND
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:111:6: ( '&' )
            // GCL.g:111:9: '&'
            {
            match('&'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end AND

    // $ANTLR start COMMA
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:112:8: ( ',' )
            // GCL.g:112:11: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end COMMA

    // $ANTLR start DOT
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:113:6: ( '.' )
            // GCL.g:113:9: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end DOT

    // $ANTLR start NOT
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:114:6: ( '!' )
            // GCL.g:114:9: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end NOT

    // $ANTLR start OR
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:115:5: ( '|' )
            // GCL.g:115:8: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end OR

    // $ANTLR start SHARP
    public final void mSHARP() throws RecognitionException {
        try {
            int _type = SHARP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:116:8: ( '#' )
            // GCL.g:116:11: '#'
            {
            match('#'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end SHARP

    // $ANTLR start PLUS
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:117:7: ( '+' )
            // GCL.g:117:10: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end PLUS

    // $ANTLR start STAR
    public final void mSTAR() throws RecognitionException {
        try {
            int _type = STAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:118:7: ( '*' )
            // GCL.g:118:10: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end STAR

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:120:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // GCL.g:120:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // GCL.g:120:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='\t' && LA2_0<='\n')||LA2_0=='\r'||LA2_0==' ') ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // GCL.g:
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
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);

             _channel=HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end WS

    public void mTokens() throws RecognitionException {
        // GCL.g:1:8: ( T__31 | T__32 | T__33 | T__34 | T__35 | ALAP | WHILE | DO | UNTIL | IF | ELSE | CHOICE | CH_OR | TRY | FUNCTION | TRUE | OTHER | ANY | IDENTIFIER | AND | COMMA | DOT | NOT | OR | SHARP | PLUS | STAR | WS )
        int alt3=28;
        alt3 = dfa3.predict(input);
        switch (alt3) {
            case 1 :
                // GCL.g:1:10: T__31
                {
                mT__31(); 

                }
                break;
            case 2 :
                // GCL.g:1:16: T__32
                {
                mT__32(); 

                }
                break;
            case 3 :
                // GCL.g:1:22: T__33
                {
                mT__33(); 

                }
                break;
            case 4 :
                // GCL.g:1:28: T__34
                {
                mT__34(); 

                }
                break;
            case 5 :
                // GCL.g:1:34: T__35
                {
                mT__35(); 

                }
                break;
            case 6 :
                // GCL.g:1:40: ALAP
                {
                mALAP(); 

                }
                break;
            case 7 :
                // GCL.g:1:45: WHILE
                {
                mWHILE(); 

                }
                break;
            case 8 :
                // GCL.g:1:51: DO
                {
                mDO(); 

                }
                break;
            case 9 :
                // GCL.g:1:54: UNTIL
                {
                mUNTIL(); 

                }
                break;
            case 10 :
                // GCL.g:1:60: IF
                {
                mIF(); 

                }
                break;
            case 11 :
                // GCL.g:1:63: ELSE
                {
                mELSE(); 

                }
                break;
            case 12 :
                // GCL.g:1:68: CHOICE
                {
                mCHOICE(); 

                }
                break;
            case 13 :
                // GCL.g:1:75: CH_OR
                {
                mCH_OR(); 

                }
                break;
            case 14 :
                // GCL.g:1:81: TRY
                {
                mTRY(); 

                }
                break;
            case 15 :
                // GCL.g:1:85: FUNCTION
                {
                mFUNCTION(); 

                }
                break;
            case 16 :
                // GCL.g:1:94: TRUE
                {
                mTRUE(); 

                }
                break;
            case 17 :
                // GCL.g:1:99: OTHER
                {
                mOTHER(); 

                }
                break;
            case 18 :
                // GCL.g:1:105: ANY
                {
                mANY(); 

                }
                break;
            case 19 :
                // GCL.g:1:109: IDENTIFIER
                {
                mIDENTIFIER(); 

                }
                break;
            case 20 :
                // GCL.g:1:120: AND
                {
                mAND(); 

                }
                break;
            case 21 :
                // GCL.g:1:124: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 22 :
                // GCL.g:1:130: DOT
                {
                mDOT(); 

                }
                break;
            case 23 :
                // GCL.g:1:134: NOT
                {
                mNOT(); 

                }
                break;
            case 24 :
                // GCL.g:1:138: OR
                {
                mOR(); 

                }
                break;
            case 25 :
                // GCL.g:1:141: SHARP
                {
                mSHARP(); 

                }
                break;
            case 26 :
                // GCL.g:1:147: PLUS
                {
                mPLUS(); 

                }
                break;
            case 27 :
                // GCL.g:1:152: STAR
                {
                mSTAR(); 

                }
                break;
            case 28 :
                // GCL.g:1:157: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA3 dfa3 = new DFA3(this);
    static final String DFA3_eotS =
        "\6\uffff\12\20\12\uffff\3\20\1\51\1\20\1\53\2\20\1\56\4\20\1\64"+
        "\1\20\1\uffff\1\20\1\uffff\2\20\1\uffff\1\20\1\72\2\20\1\75\1\uffff"+
        "\2\20\1\100\2\20\1\uffff\1\103\1\20\1\uffff\1\105\1\106\1\uffff"+
        "\1\20\1\110\1\uffff\1\20\2\uffff\1\112\1\uffff\1\20\1\uffff\1\20"+
        "\1\115\1\uffff";
    static final String DFA3_eofS =
        "\116\uffff";
    static final String DFA3_minS =
        "\1\11\5\uffff\1\154\1\150\1\157\1\156\1\146\1\154\1\150\2\162\1"+
        "\165\12\uffff\1\141\1\171\1\151\1\55\1\164\1\55\1\163\1\157\1\55"+
        "\1\150\1\165\1\156\1\160\1\55\1\154\1\uffff\1\151\1\uffff\1\145"+
        "\1\151\1\uffff\1\145\1\55\1\145\1\143\1\55\1\uffff\1\145\1\154\1"+
        "\55\1\143\1\162\1\uffff\1\55\1\164\1\uffff\2\55\1\uffff\1\145\1"+
        "\55\1\uffff\1\151\2\uffff\1\55\1\uffff\1\157\1\uffff\1\156\1\55"+
        "\1\uffff";
    static final String DFA3_maxS =
        "\1\175\5\uffff\1\156\1\150\1\157\1\156\1\146\1\154\1\150\1\164\1"+
        "\162\1\165\12\uffff\1\141\1\171\1\151\1\172\1\164\1\172\1\163\1"+
        "\157\1\172\1\150\1\171\1\156\1\160\1\172\1\154\1\uffff\1\151\1\uffff"+
        "\1\145\1\151\1\uffff\1\145\1\172\1\145\1\143\1\172\1\uffff\1\145"+
        "\1\154\1\172\1\143\1\162\1\uffff\1\172\1\164\1\uffff\2\172\1\uffff"+
        "\1\145\1\172\1\uffff\1\151\2\uffff\1\172\1\uffff\1\157\1\uffff\1"+
        "\156\1\172\1\uffff";
    static final String DFA3_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\12\uffff\1\23\1\24\1\25\1\26\1\27\1"+
        "\30\1\31\1\32\1\33\1\34\17\uffff\1\10\1\uffff\1\12\2\uffff\1\15"+
        "\5\uffff\1\22\5\uffff\1\16\2\uffff\1\6\2\uffff\1\13\2\uffff\1\20"+
        "\1\uffff\1\7\1\11\1\uffff\1\21\1\uffff\1\14\2\uffff\1\17";
    static final String DFA3_specialS =
        "\116\uffff}>";
    static final String[] DFA3_transitionS = {
            "\2\31\2\uffff\1\31\22\uffff\1\31\1\24\1\uffff\1\26\2\uffff\1"+
            "\21\1\uffff\1\3\1\4\1\30\1\27\1\22\1\uffff\1\23\14\uffff\1\5"+
            "\5\uffff\32\20\6\uffff\1\6\1\20\1\14\1\10\1\13\1\17\2\20\1\12"+
            "\5\20\1\15\4\20\1\16\1\11\1\20\1\7\3\20\1\1\1\25\1\2",
            "",
            "",
            "",
            "",
            "",
            "\1\32\1\uffff\1\33",
            "\1\34",
            "\1\35",
            "\1\36",
            "\1\37",
            "\1\40",
            "\1\41",
            "\1\42\1\uffff\1\43",
            "\1\44",
            "\1\45",
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
            "\1\46",
            "\1\47",
            "\1\50",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\52",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\54",
            "\1\55",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\57",
            "\1\61\3\uffff\1\60",
            "\1\62",
            "\1\63",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\65",
            "",
            "\1\66",
            "",
            "\1\67",
            "\1\70",
            "",
            "\1\71",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\73",
            "\1\74",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "",
            "\1\76",
            "\1\77",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\101",
            "\1\102",
            "",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\104",
            "",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "",
            "\1\107",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "",
            "\1\111",
            "",
            "",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            "",
            "\1\113",
            "",
            "\1\114",
            "\1\20\2\uffff\12\20\7\uffff\32\20\4\uffff\1\20\1\uffff\32\20",
            ""
    };

    static final short[] DFA3_eot = DFA.unpackEncodedString(DFA3_eotS);
    static final short[] DFA3_eof = DFA.unpackEncodedString(DFA3_eofS);
    static final char[] DFA3_min = DFA.unpackEncodedStringToUnsignedChars(DFA3_minS);
    static final char[] DFA3_max = DFA.unpackEncodedStringToUnsignedChars(DFA3_maxS);
    static final short[] DFA3_accept = DFA.unpackEncodedString(DFA3_acceptS);
    static final short[] DFA3_special = DFA.unpackEncodedString(DFA3_specialS);
    static final short[][] DFA3_transition;

    static {
        int numStates = DFA3_transitionS.length;
        DFA3_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA3_transition[i] = DFA.unpackEncodedString(DFA3_transitionS[i]);
        }
    }

    class DFA3 extends DFA {

        public DFA3(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 3;
            this.eot = DFA3_eot;
            this.eof = DFA3_eof;
            this.min = DFA3_min;
            this.max = DFA3_max;
            this.accept = DFA3_accept;
            this.special = DFA3_special;
            this.transition = DFA3_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__31 | T__32 | T__33 | T__34 | T__35 | ALAP | WHILE | DO | UNTIL | IF | ELSE | CHOICE | CH_OR | TRY | FUNCTION | TRUE | OTHER | ANY | IDENTIFIER | AND | COMMA | DOT | NOT | OR | SHARP | PLUS | STAR | WS );";
        }
    }
 

}