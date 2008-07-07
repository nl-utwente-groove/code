// $ANTLR 3.1b1 GCL.g 2008-07-07 09:49:30

package groove.control.parse;
import groove.control.*;
import java.util.LinkedList;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class GCLLexer extends Lexer {
    public static final int FUNCTION=7;
    public static final int T__29=29;
    public static final int STAR=20;
    public static final int T__28=28;
    public static final int SHARP=21;
    public static final int T__27=27;
    public static final int FUNCTIONS=6;
    public static final int WHILE=12;
    public static final int ELSE=15;
    public static final int DO=13;
    public static final int NOT=25;
    public static final int ALAP=11;
    public static final int AND=22;
    public static final int EOF=-1;
    public static final int TRY=14;
    public static final int IF=16;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int WS=26;
    public static final int COMMA=23;
    public static final int IDENTIFIER=9;
    public static final int BLOCK=5;
    public static final int OR=10;
    public static final int CH_OR=18;
    public static final int PROGRAM=4;
    public static final int PLUS=19;
    public static final int CALL=8;
    public static final int DOT=24;
    public static final int CHOICE=17;

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

    // $ANTLR start T__27
    public final void mT__27() throws RecognitionException {
        try {
            int _type = T__27;
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
    // $ANTLR end T__27

    // $ANTLR start T__28
    public final void mT__28() throws RecognitionException {
        try {
            int _type = T__28;
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
    // $ANTLR end T__28

    // $ANTLR start T__29
    public final void mT__29() throws RecognitionException {
        try {
            int _type = T__29;
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
    // $ANTLR end T__29

    // $ANTLR start T__30
    public final void mT__30() throws RecognitionException {
        try {
            int _type = T__30;
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
    // $ANTLR end T__30

    // $ANTLR start T__31
    public final void mT__31() throws RecognitionException {
        try {
            int _type = T__31;
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
    // $ANTLR end T__31

    // $ANTLR start T__32
    public final void mT__32() throws RecognitionException {
        try {
            int _type = T__32;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:14:7: ( 'true' )
            // GCL.g:14:9: 'true'
            {
            match("true"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__32

    // $ANTLR start ALAP
    public final void mALAP() throws RecognitionException {
        try {
            int _type = ALAP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:90:7: ( 'alap' )
            // GCL.g:90:9: 'alap'
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
            // GCL.g:91:7: ( 'while' )
            // GCL.g:91:9: 'while'
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
            // GCL.g:92:4: ( 'do' )
            // GCL.g:92:6: 'do'
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

    // $ANTLR start IF
    public final void mIF() throws RecognitionException {
        try {
            int _type = IF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:93:4: ( 'if' )
            // GCL.g:93:6: 'if'
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
            // GCL.g:94:6: ( 'else' )
            // GCL.g:94:8: 'else'
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
            // GCL.g:95:8: ( 'choice' )
            // GCL.g:95:10: 'choice'
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
            // GCL.g:96:8: ( 'or' )
            // GCL.g:96:10: 'or'
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
            // GCL.g:97:5: ( 'try' )
            // GCL.g:97:7: 'try'
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
            // GCL.g:98:9: ( 'function' )
            // GCL.g:98:11: 'function'
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

    // $ANTLR start IDENTIFIER
    public final void mIDENTIFIER() throws RecognitionException {
        try {
            int _type = IDENTIFIER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:101:13: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '-' | '_' )* )
            // GCL.g:101:15: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '-' | '_' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // GCL.g:101:35: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '-' | '_' )*
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
            // GCL.g:103:6: ( '&' )
            // GCL.g:103:9: '&'
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
            // GCL.g:104:8: ( ',' )
            // GCL.g:104:11: ','
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
            // GCL.g:105:6: ( '.' )
            // GCL.g:105:9: '.'
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
            // GCL.g:106:6: ( '!' )
            // GCL.g:106:9: '!'
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
            // GCL.g:107:5: ( '|' )
            // GCL.g:107:8: '|'
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
            // GCL.g:108:8: ( '#' )
            // GCL.g:108:11: '#'
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
            // GCL.g:109:7: ( '+' )
            // GCL.g:109:10: '+'
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
            // GCL.g:110:7: ( '*' )
            // GCL.g:110:10: '*'
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
            // GCL.g:112:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // GCL.g:112:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // GCL.g:112:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
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
        // GCL.g:1:8: ( T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | ALAP | WHILE | DO | IF | ELSE | CHOICE | CH_OR | TRY | FUNCTION | IDENTIFIER | AND | COMMA | DOT | NOT | OR | SHARP | PLUS | STAR | WS )
        int alt3=25;
        alt3 = dfa3.predict(input);
        switch (alt3) {
            case 1 :
                // GCL.g:1:10: T__27
                {
                mT__27(); 

                }
                break;
            case 2 :
                // GCL.g:1:16: T__28
                {
                mT__28(); 

                }
                break;
            case 3 :
                // GCL.g:1:22: T__29
                {
                mT__29(); 

                }
                break;
            case 4 :
                // GCL.g:1:28: T__30
                {
                mT__30(); 

                }
                break;
            case 5 :
                // GCL.g:1:34: T__31
                {
                mT__31(); 

                }
                break;
            case 6 :
                // GCL.g:1:40: T__32
                {
                mT__32(); 

                }
                break;
            case 7 :
                // GCL.g:1:46: ALAP
                {
                mALAP(); 

                }
                break;
            case 8 :
                // GCL.g:1:51: WHILE
                {
                mWHILE(); 

                }
                break;
            case 9 :
                // GCL.g:1:57: DO
                {
                mDO(); 

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
                // GCL.g:1:94: IDENTIFIER
                {
                mIDENTIFIER(); 

                }
                break;
            case 17 :
                // GCL.g:1:105: AND
                {
                mAND(); 

                }
                break;
            case 18 :
                // GCL.g:1:109: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 19 :
                // GCL.g:1:115: DOT
                {
                mDOT(); 

                }
                break;
            case 20 :
                // GCL.g:1:119: NOT
                {
                mNOT(); 

                }
                break;
            case 21 :
                // GCL.g:1:123: OR
                {
                mOR(); 

                }
                break;
            case 22 :
                // GCL.g:1:126: SHARP
                {
                mSHARP(); 

                }
                break;
            case 23 :
                // GCL.g:1:132: PLUS
                {
                mPLUS(); 

                }
                break;
            case 24 :
                // GCL.g:1:137: STAR
                {
                mSTAR(); 

                }
                break;
            case 25 :
                // GCL.g:1:142: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA3 dfa3 = new DFA3(this);
    static final String DFA3_eotS =
        "\6\uffff\11\17\12\uffff\3\17\1\46\1\47\2\17\1\52\2\17\1\55\2\17"+
        "\2\uffff\2\17\1\uffff\1\17\1\63\1\uffff\1\64\1\17\1\66\2\17\2\uffff"+
        "\1\71\1\uffff\2\17\1\uffff\1\74\1\17\1\uffff\1\17\1\77\1\uffff";
    static final String DFA3_eofS =
        "\100\uffff";
    static final String DFA3_minS =
        "\1\11\5\uffff\1\162\1\154\1\150\1\157\1\146\1\154\1\150\1\162\1"+
        "\165\12\uffff\1\165\1\141\1\151\2\55\1\163\1\157\1\55\1\156\1\145"+
        "\1\55\1\160\1\154\2\uffff\1\145\1\151\1\uffff\1\143\1\55\1\uffff"+
        "\1\55\1\145\1\55\1\143\1\164\2\uffff\1\55\1\uffff\1\145\1\151\1"+
        "\uffff\1\55\1\157\1\uffff\1\156\1\55\1\uffff";
    static final String DFA3_maxS =
        "\1\175\5\uffff\1\162\1\154\1\150\1\157\1\146\1\154\1\150\1\162\1"+
        "\165\12\uffff\1\171\1\141\1\151\2\172\1\163\1\157\1\172\1\156\1"+
        "\145\1\172\1\160\1\154\2\uffff\1\145\1\151\1\uffff\1\143\1\172\1"+
        "\uffff\1\172\1\145\1\172\1\143\1\164\2\uffff\1\172\1\uffff\1\145"+
        "\1\151\1\uffff\1\172\1\157\1\uffff\1\156\1\172\1\uffff";
    static final String DFA3_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\11\uffff\1\20\1\21\1\22\1\23\1\24\1"+
        "\25\1\26\1\27\1\30\1\31\15\uffff\1\11\1\12\2\uffff\1\15\2\uffff"+
        "\1\16\5\uffff\1\6\1\7\1\uffff\1\13\2\uffff\1\10\2\uffff\1\14\2\uffff"+
        "\1\17";
    static final String DFA3_specialS =
        "\100\uffff}>";
    static final String[] DFA3_transitionS = {
            "\2\30\2\uffff\1\30\22\uffff\1\30\1\23\1\uffff\1\25\2\uffff\1"+
            "\20\1\uffff\1\3\1\4\1\27\1\26\1\21\1\uffff\1\22\14\uffff\1\5"+
            "\5\uffff\32\17\6\uffff\1\7\1\17\1\14\1\11\1\13\1\16\2\17\1\12"+
            "\5\17\1\15\4\17\1\6\2\17\1\10\3\17\1\1\1\24\1\2",
            "",
            "",
            "",
            "",
            "",
            "\1\31",
            "\1\32",
            "\1\33",
            "\1\34",
            "\1\35",
            "\1\36",
            "\1\37",
            "\1\40",
            "\1\41",
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
            "\1\42\3\uffff\1\43",
            "\1\44",
            "\1\45",
            "\1\17\2\uffff\12\17\7\uffff\32\17\4\uffff\1\17\1\uffff\32\17",
            "\1\17\2\uffff\12\17\7\uffff\32\17\4\uffff\1\17\1\uffff\32\17",
            "\1\50",
            "\1\51",
            "\1\17\2\uffff\12\17\7\uffff\32\17\4\uffff\1\17\1\uffff\32\17",
            "\1\53",
            "\1\54",
            "\1\17\2\uffff\12\17\7\uffff\32\17\4\uffff\1\17\1\uffff\32\17",
            "\1\56",
            "\1\57",
            "",
            "",
            "\1\60",
            "\1\61",
            "",
            "\1\62",
            "\1\17\2\uffff\12\17\7\uffff\32\17\4\uffff\1\17\1\uffff\32\17",
            "",
            "\1\17\2\uffff\12\17\7\uffff\32\17\4\uffff\1\17\1\uffff\32\17",
            "\1\65",
            "\1\17\2\uffff\12\17\7\uffff\32\17\4\uffff\1\17\1\uffff\32\17",
            "\1\67",
            "\1\70",
            "",
            "",
            "\1\17\2\uffff\12\17\7\uffff\32\17\4\uffff\1\17\1\uffff\32\17",
            "",
            "\1\72",
            "\1\73",
            "",
            "\1\17\2\uffff\12\17\7\uffff\32\17\4\uffff\1\17\1\uffff\32\17",
            "\1\75",
            "",
            "\1\76",
            "\1\17\2\uffff\12\17\7\uffff\32\17\4\uffff\1\17\1\uffff\32\17",
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
            return "1:1: Tokens : ( T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | ALAP | WHILE | DO | IF | ELSE | CHOICE | CH_OR | TRY | FUNCTION | IDENTIFIER | AND | COMMA | DOT | NOT | OR | SHARP | PLUS | STAR | WS );";
        }
    }
 

}