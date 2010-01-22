// $ANTLR 3.1b1 GCL.g 2010-01-22 12:45:07

package groove.control.parse;
import groove.control.*;
import java.util.LinkedList;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")      
public class GCLLexer extends Lexer {
    public static final int FUNCTION=7;
    public static final int T__42=42;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int STAR=24;
    public static final int OTHER=27;
    public static final int SHARP=25;
    public static final int WHILE=15;
    public static final int FUNCTIONS=6;
    public static final int NODE_TYPE=28;
    public static final int ELSE=18;
    public static final int DO=9;
    public static final int PARAM=11;
    public static final int NOT=34;
    public static final int ALAP=14;
    public static final int AND=32;
    public static final int EOF=-1;
    public static final int TRUE=22;
    public static final int TRY=17;
    public static final int IF=19;
    public static final int DONT_CARE=31;
    public static final int ML_COMMENT=35;
    public static final int ANY=26;
    public static final int WS=37;
    public static final int OUT=30;
    public static final int T__38=38;
    public static final int COMMA=29;
    public static final int T__39=39;
    public static final int UNTIL=16;
    public static final int IDENTIFIER=12;
    public static final int BLOCK=5;
    public static final int OR=13;
    public static final int SL_COMMENT=36;
    public static final int CH_OR=21;
    public static final int PROGRAM=4;
    public static final int PLUS=23;
    public static final int VAR=10;
    public static final int CALL=8;
    public static final int DOT=33;
    public static final int CHOICE=20;

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

    // $ANTLR start T__38
    public final void mT__38() throws RecognitionException {
        try {
            int _type = T__38;
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
    // $ANTLR end T__38

    // $ANTLR start T__39
    public final void mT__39() throws RecognitionException {
        try {
            int _type = T__39;
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
    // $ANTLR end T__39

    // $ANTLR start T__40
    public final void mT__40() throws RecognitionException {
        try {
            int _type = T__40;
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
    // $ANTLR end T__40

    // $ANTLR start T__41
    public final void mT__41() throws RecognitionException {
        try {
            int _type = T__41;
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
    // $ANTLR end T__41

    // $ANTLR start T__42
    public final void mT__42() throws RecognitionException {
        try {
            int _type = T__42;
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
    // $ANTLR end T__42

    // $ANTLR start ALAP
    public final void mALAP() throws RecognitionException {
        try {
            int _type = ALAP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:115:7: ( 'alap' )
            // GCL.g:115:9: 'alap'
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
            // GCL.g:116:7: ( 'while' )
            // GCL.g:116:9: 'while'
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
            // GCL.g:117:5: ( 'do' )
            // GCL.g:117:7: 'do'
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
            // GCL.g:118:9: ( 'until' )
            // GCL.g:118:11: 'until'
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
            // GCL.g:119:5: ( 'if' )
            // GCL.g:119:7: 'if'
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
            // GCL.g:120:6: ( 'else' )
            // GCL.g:120:8: 'else'
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
            // GCL.g:121:8: ( 'choice' )
            // GCL.g:121:10: 'choice'
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
            // GCL.g:122:8: ( 'or' )
            // GCL.g:122:10: 'or'
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
            // GCL.g:123:6: ( 'try' )
            // GCL.g:123:8: 'try'
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
            // GCL.g:124:9: ( 'function' )
            // GCL.g:124:11: 'function'
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
            // GCL.g:125:6: ( 'true' )
            // GCL.g:125:8: 'true'
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
            // GCL.g:126:7: ( 'other' )
            // GCL.g:126:9: 'other'
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
            // GCL.g:127:6: ( 'any' )
            // GCL.g:127:8: 'any'
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

    // $ANTLR start NODE_TYPE
    public final void mNODE_TYPE() throws RecognitionException {
        try {
            int _type = NODE_TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:128:11: ( 'node' )
            // GCL.g:128:13: 'node'
            {
            match("node"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end NODE_TYPE

    // $ANTLR start OUT
    public final void mOUT() throws RecognitionException {
        try {
            int _type = OUT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:129:6: ( 'out' )
            // GCL.g:129:8: 'out'
            {
            match("out"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end OUT

    // $ANTLR start IDENTIFIER
    public final void mIDENTIFIER() throws RecognitionException {
        try {
            int _type = IDENTIFIER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:132:13: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '-' | '_' | '.' )* )
            // GCL.g:132:15: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '-' | '_' | '.' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // GCL.g:132:35: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '-' | '_' | '.' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='-' && LA1_0<='.')||(LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // GCL.g:
            	    {
            	    if ( (input.LA(1)>='-' && input.LA(1)<='.')||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
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
            // GCL.g:134:6: ( '&' )
            // GCL.g:134:9: '&'
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
            // GCL.g:135:8: ( ',' )
            // GCL.g:135:11: ','
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
            // GCL.g:136:6: ( '.' )
            // GCL.g:136:9: '.'
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
            // GCL.g:137:6: ( '!' )
            // GCL.g:137:9: '!'
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
            // GCL.g:138:5: ( '|' )
            // GCL.g:138:8: '|'
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
            // GCL.g:139:8: ( '#' )
            // GCL.g:139:11: '#'
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
            // GCL.g:140:7: ( '+' )
            // GCL.g:140:10: '+'
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
            // GCL.g:141:7: ( '*' )
            // GCL.g:141:10: '*'
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

    // $ANTLR start DONT_CARE
    public final void mDONT_CARE() throws RecognitionException {
        try {
            int _type = DONT_CARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:142:11: ( '_' )
            // GCL.g:142:13: '_'
            {
            match('_'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end DONT_CARE

    // $ANTLR start ML_COMMENT
    public final void mML_COMMENT() throws RecognitionException {
        try {
            int _type = ML_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:144:12: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // GCL.g:144:14: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // GCL.g:144:19: ( options {greedy=false; } : . )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0=='*') ) {
                    int LA2_1 = input.LA(2);

                    if ( (LA2_1=='/') ) {
                        alt2=2;
                    }
                    else if ( ((LA2_1>='\u0000' && LA2_1<='.')||(LA2_1>='0' && LA2_1<='\uFFFE')) ) {
                        alt2=1;
                    }


                }
                else if ( ((LA2_0>='\u0000' && LA2_0<=')')||(LA2_0>='+' && LA2_0<='\uFFFE')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // GCL.g:144:47: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop2;
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
    // $ANTLR end ML_COMMENT

    // $ANTLR start SL_COMMENT
    public final void mSL_COMMENT() throws RecognitionException {
        try {
            int _type = SL_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:145:12: ( '//' ( options {greedy=false; } : . )* '\\n' )
            // GCL.g:145:14: '//' ( options {greedy=false; } : . )* '\\n'
            {
            match("//"); 

            // GCL.g:145:19: ( options {greedy=false; } : . )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0=='\n') ) {
                    alt3=2;
                }
                else if ( ((LA3_0>='\u0000' && LA3_0<='\t')||(LA3_0>='\u000B' && LA3_0<='\uFFFE')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // GCL.g:145:47: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop3;
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
    // $ANTLR end SL_COMMENT

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:147:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // GCL.g:147:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // GCL.g:147:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>='\t' && LA4_0<='\n')||LA4_0=='\r'||LA4_0==' ') ) {
                    alt4=1;
                }


                switch (alt4) {
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
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
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
        // GCL.g:1:8: ( T__38 | T__39 | T__40 | T__41 | T__42 | ALAP | WHILE | DO | UNTIL | IF | ELSE | CHOICE | CH_OR | TRY | FUNCTION | TRUE | OTHER | ANY | NODE_TYPE | OUT | IDENTIFIER | AND | COMMA | DOT | NOT | OR | SHARP | PLUS | STAR | DONT_CARE | ML_COMMENT | SL_COMMENT | WS )
        int alt5=33;
        alt5 = dfa5.predict(input);
        switch (alt5) {
            case 1 :
                // GCL.g:1:10: T__38
                {
                mT__38(); 

                }
                break;
            case 2 :
                // GCL.g:1:16: T__39
                {
                mT__39(); 

                }
                break;
            case 3 :
                // GCL.g:1:22: T__40
                {
                mT__40(); 

                }
                break;
            case 4 :
                // GCL.g:1:28: T__41
                {
                mT__41(); 

                }
                break;
            case 5 :
                // GCL.g:1:34: T__42
                {
                mT__42(); 

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
                // GCL.g:1:109: NODE_TYPE
                {
                mNODE_TYPE(); 

                }
                break;
            case 20 :
                // GCL.g:1:119: OUT
                {
                mOUT(); 

                }
                break;
            case 21 :
                // GCL.g:1:123: IDENTIFIER
                {
                mIDENTIFIER(); 

                }
                break;
            case 22 :
                // GCL.g:1:134: AND
                {
                mAND(); 

                }
                break;
            case 23 :
                // GCL.g:1:138: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 24 :
                // GCL.g:1:144: DOT
                {
                mDOT(); 

                }
                break;
            case 25 :
                // GCL.g:1:148: NOT
                {
                mNOT(); 

                }
                break;
            case 26 :
                // GCL.g:1:152: OR
                {
                mOR(); 

                }
                break;
            case 27 :
                // GCL.g:1:155: SHARP
                {
                mSHARP(); 

                }
                break;
            case 28 :
                // GCL.g:1:161: PLUS
                {
                mPLUS(); 

                }
                break;
            case 29 :
                // GCL.g:1:166: STAR
                {
                mSTAR(); 

                }
                break;
            case 30 :
                // GCL.g:1:171: DONT_CARE
                {
                mDONT_CARE(); 

                }
                break;
            case 31 :
                // GCL.g:1:181: ML_COMMENT
                {
                mML_COMMENT(); 

                }
                break;
            case 32 :
                // GCL.g:1:192: SL_COMMENT
                {
                mSL_COMMENT(); 

                }
                break;
            case 33 :
                // GCL.g:1:203: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA5 dfa5 = new DFA5(this);
    static final String DFA5_eotS =
        "\6\uffff\13\21\14\uffff\3\21\1\60\1\21\1\62\2\21\1\65\5\21\2\uffff"+
        "\1\21\1\75\1\21\1\uffff\1\21\1\uffff\2\21\1\uffff\1\21\1\103\1\104"+
        "\3\21\1\110\1\uffff\2\21\1\113\2\21\2\uffff\1\116\1\21\1\120\1\uffff"+
        "\1\121\1\122\1\uffff\1\21\1\124\1\uffff\1\21\3\uffff\1\126\1\uffff"+
        "\1\21\1\uffff\1\21\1\131\1\uffff";
    static final String DFA5_eofS =
        "\132\uffff";
    static final String DFA5_minS =
        "\1\11\5\uffff\1\154\1\150\1\157\1\156\1\146\1\154\1\150\2\162\1"+
        "\165\1\157\12\uffff\1\52\1\uffff\1\141\1\171\1\151\1\55\1\164\1"+
        "\55\1\163\1\157\1\55\1\150\1\164\1\165\1\156\1\144\2\uffff\1\160"+
        "\1\55\1\154\1\uffff\1\151\1\uffff\1\145\1\151\1\uffff\1\145\2\55"+
        "\1\145\1\143\1\145\1\55\1\uffff\1\145\1\154\1\55\1\143\1\162\2\uffff"+
        "\1\55\1\164\1\55\1\uffff\2\55\1\uffff\1\145\1\55\1\uffff\1\151\3"+
        "\uffff\1\55\1\uffff\1\157\1\uffff\1\156\1\55\1\uffff";
    static final String DFA5_maxS =
        "\1\175\5\uffff\1\156\1\150\1\157\1\156\1\146\1\154\1\150\1\165\1"+
        "\162\1\165\1\157\12\uffff\1\57\1\uffff\1\141\1\171\1\151\1\172\1"+
        "\164\1\172\1\163\1\157\1\172\1\150\1\164\1\171\1\156\1\144\2\uffff"+
        "\1\160\1\172\1\154\1\uffff\1\151\1\uffff\1\145\1\151\1\uffff\1\145"+
        "\2\172\1\145\1\143\1\145\1\172\1\uffff\1\145\1\154\1\172\1\143\1"+
        "\162\2\uffff\1\172\1\164\1\172\1\uffff\2\172\1\uffff\1\145\1\172"+
        "\1\uffff\1\151\3\uffff\1\172\1\uffff\1\157\1\uffff\1\156\1\172\1"+
        "\uffff";
    static final String DFA5_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\13\uffff\1\25\1\26\1\27\1\30\1\31\1"+
        "\32\1\33\1\34\1\35\1\36\1\uffff\1\41\16\uffff\1\37\1\40\3\uffff"+
        "\1\10\1\uffff\1\12\2\uffff\1\15\7\uffff\1\22\5\uffff\1\24\1\16\3"+
        "\uffff\1\6\2\uffff\1\13\2\uffff\1\20\1\uffff\1\23\1\7\1\11\1\uffff"+
        "\1\21\1\uffff\1\14\2\uffff\1\17";
    static final String DFA5_specialS =
        "\132\uffff}>";
    static final String[] DFA5_transitionS = {
            "\2\34\2\uffff\1\34\22\uffff\1\34\1\25\1\uffff\1\27\2\uffff\1"+
            "\22\1\uffff\1\3\1\4\1\31\1\30\1\23\1\uffff\1\24\1\33\13\uffff"+
            "\1\5\5\uffff\32\21\4\uffff\1\32\1\uffff\1\6\1\21\1\14\1\10\1"+
            "\13\1\17\2\21\1\12\4\21\1\20\1\15\4\21\1\16\1\11\1\21\1\7\3"+
            "\21\1\1\1\26\1\2",
            "",
            "",
            "",
            "",
            "",
            "\1\35\1\uffff\1\36",
            "\1\37",
            "\1\40",
            "\1\41",
            "\1\42",
            "\1\43",
            "\1\44",
            "\1\45\1\uffff\1\46\1\47",
            "\1\50",
            "\1\51",
            "\1\52",
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
            "\1\53\4\uffff\1\54",
            "",
            "\1\55",
            "\1\56",
            "\1\57",
            "\2\21\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32\21",
            "\1\61",
            "\2\21\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32\21",
            "\1\63",
            "\1\64",
            "\2\21\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32\21",
            "\1\66",
            "\1\67",
            "\1\71\3\uffff\1\70",
            "\1\72",
            "\1\73",
            "",
            "",
            "\1\74",
            "\2\21\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32\21",
            "\1\76",
            "",
            "\1\77",
            "",
            "\1\100",
            "\1\101",
            "",
            "\1\102",
            "\2\21\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32\21",
            "\2\21\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32\21",
            "\1\105",
            "\1\106",
            "\1\107",
            "\2\21\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32\21",
            "",
            "\1\111",
            "\1\112",
            "\2\21\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32\21",
            "\1\114",
            "\1\115",
            "",
            "",
            "\2\21\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32\21",
            "\1\117",
            "\2\21\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32\21",
            "",
            "\2\21\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32\21",
            "\2\21\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32\21",
            "",
            "\1\123",
            "\2\21\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32\21",
            "",
            "\1\125",
            "",
            "",
            "",
            "\2\21\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32\21",
            "",
            "\1\127",
            "",
            "\1\130",
            "\2\21\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32\21",
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__38 | T__39 | T__40 | T__41 | T__42 | ALAP | WHILE | DO | UNTIL | IF | ELSE | CHOICE | CH_OR | TRY | FUNCTION | TRUE | OTHER | ANY | NODE_TYPE | OUT | IDENTIFIER | AND | COMMA | DOT | NOT | OR | SHARP | PLUS | STAR | DONT_CARE | ML_COMMENT | SL_COMMENT | WS );";
        }
    }
 

}