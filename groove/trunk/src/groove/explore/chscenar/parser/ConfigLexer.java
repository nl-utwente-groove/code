// $ANTLR 3.1b1 ../../src/groove/explore/chscenar/parser/Config.g 2008-07-08 15:23:46

package groove.explore.chscenar.parser;
import groove.explore.chscenar.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class ConfigLexer extends Lexer {
    public static final int WS=5;
    public static final int LINE_COMMENT=6;
    public static final int T__12=12;
    public static final int T__11=11;
    public static final int T__14=14;
    public static final int T__13=13;
    public static final int T__10=10;
    public static final int ID=4;
    public static final int EOF=-1;
    public static final int T__9=9;
    public static final int T__8=8;
    public static final int T__7=7;

    // delegates
    // delegators

    public ConfigLexer() {;} 
    public ConfigLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public ConfigLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "../../src/groove/explore/chscenar/parser/Config.g"; }

    // $ANTLR start T__7
    public final void mT__7() throws RecognitionException {
        try {
            int _type = T__7;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../../src/groove/explore/chscenar/parser/Config.g:8:6: ( 'RULE' )
            // ../../src/groove/explore/chscenar/parser/Config.g:8:8: 'RULE'
            {
            match("RULE"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__7

    // $ANTLR start T__8
    public final void mT__8() throws RecognitionException {
        try {
            int _type = T__8;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../../src/groove/explore/chscenar/parser/Config.g:9:6: ( '::ALLOW' )
            // ../../src/groove/explore/chscenar/parser/Config.g:9:8: '::ALLOW'
            {
            match("::ALLOW"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__8

    // $ANTLR start T__9
    public final void mT__9() throws RecognitionException {
        try {
            int _type = T__9;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../../src/groove/explore/chscenar/parser/Config.g:10:6: ( '::DENY' )
            // ../../src/groove/explore/chscenar/parser/Config.g:10:8: '::DENY'
            {
            match("::DENY"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__9

    // $ANTLR start T__10
    public final void mT__10() throws RecognitionException {
        try {
            int _type = T__10;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../../src/groove/explore/chscenar/parser/Config.g:11:7: ( ':STRATEGY' )
            // ../../src/groove/explore/chscenar/parser/Config.g:11:9: ':STRATEGY'
            {
            match(":STRATEGY"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__10

    // $ANTLR start T__11
    public final void mT__11() throws RecognitionException {
        try {
            int _type = T__11;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../../src/groove/explore/chscenar/parser/Config.g:12:7: ( ':RESULT' )
            // ../../src/groove/explore/chscenar/parser/Config.g:12:9: ':RESULT'
            {
            match(":RESULT"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__11

    // $ANTLR start T__12
    public final void mT__12() throws RecognitionException {
        try {
            int _type = T__12;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../../src/groove/explore/chscenar/parser/Config.g:13:7: ( ':ACCEPTOR' )
            // ../../src/groove/explore/chscenar/parser/Config.g:13:9: ':ACCEPTOR'
            {
            match(":ACCEPTOR"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__12

    // $ANTLR start T__13
    public final void mT__13() throws RecognitionException {
        try {
            int _type = T__13;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../../src/groove/explore/chscenar/parser/Config.g:14:7: ( ',' )
            // ../../src/groove/explore/chscenar/parser/Config.g:14:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__13

    // $ANTLR start T__14
    public final void mT__14() throws RecognitionException {
        try {
            int _type = T__14;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../../src/groove/explore/chscenar/parser/Config.g:15:7: ( '.' )
            // ../../src/groove/explore/chscenar/parser/Config.g:15:9: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__14

    // $ANTLR start ID
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../../src/groove/explore/chscenar/parser/Config.g:136:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // ../../src/groove/explore/chscenar/parser/Config.g:136:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // ../../src/groove/explore/chscenar/parser/Config.g:136:31: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../../src/groove/explore/chscenar/parser/Config.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
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
    // $ANTLR end ID

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../../src/groove/explore/chscenar/parser/Config.g:137:5: ( ( ' ' | '\\t' | ( '\\r' )? '\\n' )+ )
            // ../../src/groove/explore/chscenar/parser/Config.g:137:9: ( ' ' | '\\t' | ( '\\r' )? '\\n' )+
            {
            // ../../src/groove/explore/chscenar/parser/Config.g:137:9: ( ' ' | '\\t' | ( '\\r' )? '\\n' )+
            int cnt3=0;
            loop3:
            do {
                int alt3=4;
                switch ( input.LA(1) ) {
                case ' ':
                    {
                    alt3=1;
                    }
                    break;
                case '\t':
                    {
                    alt3=2;
                    }
                    break;
                case '\n':
                case '\r':
                    {
                    alt3=3;
                    }
                    break;

                }

                switch (alt3) {
            	case 1 :
            	    // ../../src/groove/explore/chscenar/parser/Config.g:137:10: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;
            	case 2 :
            	    // ../../src/groove/explore/chscenar/parser/Config.g:137:14: '\\t'
            	    {
            	    match('\t'); 

            	    }
            	    break;
            	case 3 :
            	    // ../../src/groove/explore/chscenar/parser/Config.g:137:20: ( '\\r' )? '\\n'
            	    {
            	    // ../../src/groove/explore/chscenar/parser/Config.g:137:20: ( '\\r' )?
            	    int alt2=2;
            	    int LA2_0 = input.LA(1);

            	    if ( (LA2_0=='\r') ) {
            	        alt2=1;
            	    }
            	    switch (alt2) {
            	        case 1 :
            	            // ../../src/groove/explore/chscenar/parser/Config.g:137:20: '\\r'
            	            {
            	            match('\r'); 

            	            }
            	            break;

            	    }

            	    match('\n'); 

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

            skip();

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end WS

    // $ANTLR start LINE_COMMENT
    public final void mLINE_COMMENT() throws RecognitionException {
        try {
            int _type = LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ../../src/groove/explore/chscenar/parser/Config.g:139:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )
            // ../../src/groove/explore/chscenar/parser/Config.g:139:7: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
            {
            match("//"); 

            // ../../src/groove/explore/chscenar/parser/Config.g:139:12: (~ ( '\\n' | '\\r' ) )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>='\u0000' && LA4_0<='\t')||(LA4_0>='\u000B' && LA4_0<='\f')||(LA4_0>='\u000E' && LA4_0<='\uFFFE')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ../../src/groove/explore/chscenar/parser/Config.g:139:12: ~ ( '\\n' | '\\r' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            // ../../src/groove/explore/chscenar/parser/Config.g:139:26: ( '\\r' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='\r') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // ../../src/groove/explore/chscenar/parser/Config.g:139:26: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }

            match('\n'); 
            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end LINE_COMMENT

    public void mTokens() throws RecognitionException {
        // ../../src/groove/explore/chscenar/parser/Config.g:1:8: ( T__7 | T__8 | T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | ID | WS | LINE_COMMENT )
        int alt6=11;
        alt6 = dfa6.predict(input);
        switch (alt6) {
            case 1 :
                // ../../src/groove/explore/chscenar/parser/Config.g:1:10: T__7
                {
                mT__7(); 

                }
                break;
            case 2 :
                // ../../src/groove/explore/chscenar/parser/Config.g:1:15: T__8
                {
                mT__8(); 

                }
                break;
            case 3 :
                // ../../src/groove/explore/chscenar/parser/Config.g:1:20: T__9
                {
                mT__9(); 

                }
                break;
            case 4 :
                // ../../src/groove/explore/chscenar/parser/Config.g:1:25: T__10
                {
                mT__10(); 

                }
                break;
            case 5 :
                // ../../src/groove/explore/chscenar/parser/Config.g:1:31: T__11
                {
                mT__11(); 

                }
                break;
            case 6 :
                // ../../src/groove/explore/chscenar/parser/Config.g:1:37: T__12
                {
                mT__12(); 

                }
                break;
            case 7 :
                // ../../src/groove/explore/chscenar/parser/Config.g:1:43: T__13
                {
                mT__13(); 

                }
                break;
            case 8 :
                // ../../src/groove/explore/chscenar/parser/Config.g:1:49: T__14
                {
                mT__14(); 

                }
                break;
            case 9 :
                // ../../src/groove/explore/chscenar/parser/Config.g:1:55: ID
                {
                mID(); 

                }
                break;
            case 10 :
                // ../../src/groove/explore/chscenar/parser/Config.g:1:58: WS
                {
                mWS(); 

                }
                break;
            case 11 :
                // ../../src/groove/explore/chscenar/parser/Config.g:1:61: LINE_COMMENT
                {
                mLINE_COMMENT(); 

                }
                break;

        }

    }


    protected DFA6 dfa6 = new DFA6(this);
    static final String DFA6_eotS =
        "\1\uffff\1\5\6\uffff\1\5\4\uffff\1\5\2\uffff\1\21\1\uffff";
    static final String DFA6_eofS =
        "\22\uffff";
    static final String DFA6_minS =
        "\1\11\1\125\1\72\5\uffff\1\114\1\101\3\uffff\1\105\2\uffff\1\60"+
        "\1\uffff";
    static final String DFA6_maxS =
        "\1\172\1\125\1\123\5\uffff\1\114\1\104\3\uffff\1\105\2\uffff\1\172"+
        "\1\uffff";
    static final String DFA6_acceptS =
        "\3\uffff\1\7\1\10\1\11\1\12\1\13\2\uffff\1\4\1\5\1\6\1\uffff\1\2"+
        "\1\3\1\uffff\1\1";
    static final String DFA6_specialS =
        "\22\uffff}>";
    static final String[] DFA6_transitionS = {
            "\2\6\2\uffff\1\6\22\uffff\1\6\13\uffff\1\3\1\uffff\1\4\1\7\12"+
            "\uffff\1\2\6\uffff\21\5\1\1\10\5\4\uffff\1\5\1\uffff\32\5",
            "\1\10",
            "\1\11\6\uffff\1\14\20\uffff\1\13\1\12",
            "",
            "",
            "",
            "",
            "",
            "\1\15",
            "\1\16\2\uffff\1\17",
            "",
            "",
            "",
            "\1\20",
            "",
            "",
            "\12\5\7\uffff\32\5\4\uffff\1\5\1\uffff\32\5",
            ""
    };

    static final short[] DFA6_eot = DFA.unpackEncodedString(DFA6_eotS);
    static final short[] DFA6_eof = DFA.unpackEncodedString(DFA6_eofS);
    static final char[] DFA6_min = DFA.unpackEncodedStringToUnsignedChars(DFA6_minS);
    static final char[] DFA6_max = DFA.unpackEncodedStringToUnsignedChars(DFA6_maxS);
    static final short[] DFA6_accept = DFA.unpackEncodedString(DFA6_acceptS);
    static final short[] DFA6_special = DFA.unpackEncodedString(DFA6_specialS);
    static final short[][] DFA6_transition;

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA6_transition[i] = DFA.unpackEncodedString(DFA6_transitionS[i]);
        }
    }

    class DFA6 extends DFA {

        public DFA6(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 6;
            this.eot = DFA6_eot;
            this.eof = DFA6_eof;
            this.min = DFA6_min;
            this.max = DFA6_max;
            this.accept = DFA6_accept;
            this.special = DFA6_special;
            this.transition = DFA6_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__7 | T__8 | T__9 | T__10 | T__11 | T__12 | T__13 | T__14 | ID | WS | LINE_COMMENT );";
        }
    }
 

}