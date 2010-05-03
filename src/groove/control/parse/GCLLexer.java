// $ANTLR 3.1b1 GCL.g 2010-04-23 13:02:38

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
    public static final int STAR=24;
    public static final int FUNCTIONS=6;
    public static final int WHILE=15;
    public static final int BOOL_TYPE=29;
    public static final int NODE_TYPE=28;
    public static final int DO=9;
    public static final int PARAM=11;
    public static final int NOT=43;
    public static final int ALAP=14;
    public static final int AND=41;
    public static final int EOF=-1;
    public static final int IF=19;
    public static final int ML_COMMENT=44;
    public static final int QUOTE=40;
    public static final int T__51=51;
    public static final int COMMA=33;
    public static final int IDENTIFIER=12;
    public static final int CH_OR=18;
    public static final int PLUS=23;
    public static final int VAR=10;
    public static final int DOT=42;
    public static final int T__50=50;
    public static final int CHOICE=17;
    public static final int T__47=47;
    public static final int SHARP=25;
    public static final int OTHER=27;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int ELSE=20;
    public static final int INT=38;
    public static final int INT_TYPE=31;
    public static final int TRUE=22;
    public static final int TRY=21;
    public static final int REAL=39;
    public static final int REAL_TYPE=32;
    public static final int DONT_CARE=35;
    public static final int WS=46;
    public static final int ANY=26;
    public static final int OUT=34;
    public static final int UNTIL=16;
    public static final int STRING_TYPE=30;
    public static final int BLOCK=5;
    public static final int OR=13;
    public static final int SL_COMMENT=45;
    public static final int PROGRAM=4;
    public static final int CALL=8;
    public static final int FALSE=36;
    public static final int STRING=37;

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

    // $ANTLR start T__47
    public final void mT__47() throws RecognitionException {
        try {
            int _type = T__47;
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
    // $ANTLR end T__47

    // $ANTLR start T__48
    public final void mT__48() throws RecognitionException {
        try {
            int _type = T__48;
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
    // $ANTLR end T__48

    // $ANTLR start T__49
    public final void mT__49() throws RecognitionException {
        try {
            int _type = T__49;
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
    // $ANTLR end T__49

    // $ANTLR start T__50
    public final void mT__50() throws RecognitionException {
        try {
            int _type = T__50;
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
    // $ANTLR end T__50

    // $ANTLR start T__51
    public final void mT__51() throws RecognitionException {
        try {
            int _type = T__51;
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
    // $ANTLR end T__51

    // $ANTLR start ALAP
    public final void mALAP() throws RecognitionException {
        try {
            int _type = ALAP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:138:7: ( 'alap' )
            // GCL.g:138:9: 'alap'
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
            // GCL.g:139:7: ( 'while' )
            // GCL.g:139:9: 'while'
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
            // GCL.g:140:5: ( 'do' )
            // GCL.g:140:7: 'do'
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
            // GCL.g:141:9: ( 'until' )
            // GCL.g:141:11: 'until'
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
            // GCL.g:142:5: ( 'if' )
            // GCL.g:142:7: 'if'
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
            // GCL.g:143:6: ( 'else' )
            // GCL.g:143:8: 'else'
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
            // GCL.g:144:8: ( 'choice' )
            // GCL.g:144:10: 'choice'
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
            // GCL.g:145:8: ( 'or' )
            // GCL.g:145:10: 'or'
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
            // GCL.g:146:6: ( 'try' )
            // GCL.g:146:8: 'try'
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
            // GCL.g:147:9: ( 'function' )
            // GCL.g:147:11: 'function'
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
            // GCL.g:148:6: ( 'true' )
            // GCL.g:148:8: 'true'
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

    // $ANTLR start FALSE
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:149:7: ( 'false' )
            // GCL.g:149:9: 'false'
            {
            match("false"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end FALSE

    // $ANTLR start OTHER
    public final void mOTHER() throws RecognitionException {
        try {
            int _type = OTHER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:150:7: ( 'other' )
            // GCL.g:150:9: 'other'
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
            // GCL.g:151:6: ( 'any' )
            // GCL.g:151:8: 'any'
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
            // GCL.g:152:11: ( 'node' )
            // GCL.g:152:13: 'node'
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

    // $ANTLR start BOOL_TYPE
    public final void mBOOL_TYPE() throws RecognitionException {
        try {
            int _type = BOOL_TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:153:11: ( 'bool' )
            // GCL.g:153:13: 'bool'
            {
            match("bool"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end BOOL_TYPE

    // $ANTLR start STRING_TYPE
    public final void mSTRING_TYPE() throws RecognitionException {
        try {
            int _type = STRING_TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:154:13: ( 'string' )
            // GCL.g:154:15: 'string'
            {
            match("string"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end STRING_TYPE

    // $ANTLR start INT_TYPE
    public final void mINT_TYPE() throws RecognitionException {
        try {
            int _type = INT_TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:155:10: ( 'int' )
            // GCL.g:155:12: 'int'
            {
            match("int"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end INT_TYPE

    // $ANTLR start REAL_TYPE
    public final void mREAL_TYPE() throws RecognitionException {
        try {
            int _type = REAL_TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:156:11: ( 'real' )
            // GCL.g:156:13: 'real'
            {
            match("real"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end REAL_TYPE

    // $ANTLR start OUT
    public final void mOUT() throws RecognitionException {
        try {
            int _type = OUT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:157:6: ( 'out' )
            // GCL.g:157:8: 'out'
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
            // GCL.g:160:13: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '-' | '_' | '.' )* )
            // GCL.g:160:15: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '-' | '_' | '.' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // GCL.g:160:35: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '-' | '_' | '.' )*
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

    // $ANTLR start STRING
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:161:8: ( QUOTE ( options {greedy=false; } : . )* QUOTE )
            // GCL.g:161:10: QUOTE ( options {greedy=false; } : . )* QUOTE
            {
            mQUOTE(); 
            // GCL.g:161:16: ( options {greedy=false; } : . )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0=='\"') ) {
                    alt2=2;
                }
                else if ( ((LA2_0>='\u0000' && LA2_0<='!')||(LA2_0>='#' && LA2_0<='\uFFFE')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // GCL.g:161:43: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop2;
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
    // $ANTLR end STRING

    // $ANTLR start INT
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:163:5: ( ( '0' .. '9' )+ )
            // GCL.g:163:7: ( '0' .. '9' )+
            {
            // GCL.g:163:7: ( '0' .. '9' )+
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
            	    // GCL.g:163:8: '0' .. '9'
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


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end INT

    // $ANTLR start REAL
    public final void mREAL() throws RecognitionException {
        try {
            int _type = REAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:164:6: ( ( '0' .. '9' )+ ( '.' ( '0' .. '9' )+ ) )
            // GCL.g:164:8: ( '0' .. '9' )+ ( '.' ( '0' .. '9' )+ )
            {
            // GCL.g:164:8: ( '0' .. '9' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>='0' && LA4_0<='9')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // GCL.g:164:9: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

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

            // GCL.g:164:20: ( '.' ( '0' .. '9' )+ )
            // GCL.g:164:21: '.' ( '0' .. '9' )+
            {
            match('.'); 
            // GCL.g:164:25: ( '0' .. '9' )+
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
            	    // GCL.g:164:26: '0' .. '9'
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


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end REAL

    // $ANTLR start AND
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:166:6: ( '&' )
            // GCL.g:166:9: '&'
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
            // GCL.g:167:8: ( ',' )
            // GCL.g:167:11: ','
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
            // GCL.g:168:6: ( '.' )
            // GCL.g:168:9: '.'
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
            // GCL.g:169:6: ( '!' )
            // GCL.g:169:9: '!'
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
            // GCL.g:170:5: ( '|' )
            // GCL.g:170:8: '|'
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
            // GCL.g:171:8: ( '#' )
            // GCL.g:171:11: '#'
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
            // GCL.g:172:7: ( '+' )
            // GCL.g:172:10: '+'
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
            // GCL.g:173:7: ( '*' )
            // GCL.g:173:10: '*'
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
            // GCL.g:174:11: ( '_' )
            // GCL.g:174:13: '_'
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

    // $ANTLR start QUOTE
    public final void mQUOTE() throws RecognitionException {
        try {
            int _type = QUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:175:9: ( '\"' )
            // GCL.g:175:11: '\"'
            {
            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end QUOTE

    // $ANTLR start ML_COMMENT
    public final void mML_COMMENT() throws RecognitionException {
        try {
            int _type = ML_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:177:12: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // GCL.g:177:14: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // GCL.g:177:19: ( options {greedy=false; } : . )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0=='*') ) {
                    int LA6_1 = input.LA(2);

                    if ( (LA6_1=='/') ) {
                        alt6=2;
                    }
                    else if ( ((LA6_1>='\u0000' && LA6_1<='.')||(LA6_1>='0' && LA6_1<='\uFFFE')) ) {
                        alt6=1;
                    }


                }
                else if ( ((LA6_0>='\u0000' && LA6_0<=')')||(LA6_0>='+' && LA6_0<='\uFFFE')) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // GCL.g:177:47: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop6;
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
            // GCL.g:178:12: ( '//' ( options {greedy=false; } : . )* '\\n' )
            // GCL.g:178:14: '//' ( options {greedy=false; } : . )* '\\n'
            {
            match("//"); 

            // GCL.g:178:19: ( options {greedy=false; } : . )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0=='\n') ) {
                    alt7=2;
                }
                else if ( ((LA7_0>='\u0000' && LA7_0<='\t')||(LA7_0>='\u000B' && LA7_0<='\uFFFE')) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // GCL.g:178:47: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop7;
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
            // GCL.g:180:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // GCL.g:180:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // GCL.g:180:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( ((LA8_0>='\t' && LA8_0<='\n')||LA8_0=='\r'||LA8_0==' ') ) {
                    alt8=1;
                }


                switch (alt8) {
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
            	    if ( cnt8 >= 1 ) break loop8;
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
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
        // GCL.g:1:8: ( T__47 | T__48 | T__49 | T__50 | T__51 | ALAP | WHILE | DO | UNTIL | IF | ELSE | CHOICE | CH_OR | TRY | FUNCTION | TRUE | FALSE | OTHER | ANY | NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE | OUT | IDENTIFIER | STRING | INT | REAL | AND | COMMA | DOT | NOT | OR | SHARP | PLUS | STAR | DONT_CARE | QUOTE | ML_COMMENT | SL_COMMENT | WS )
        int alt9=42;
        alt9 = dfa9.predict(input);
        switch (alt9) {
            case 1 :
                // GCL.g:1:10: T__47
                {
                mT__47(); 

                }
                break;
            case 2 :
                // GCL.g:1:16: T__48
                {
                mT__48(); 

                }
                break;
            case 3 :
                // GCL.g:1:22: T__49
                {
                mT__49(); 

                }
                break;
            case 4 :
                // GCL.g:1:28: T__50
                {
                mT__50(); 

                }
                break;
            case 5 :
                // GCL.g:1:34: T__51
                {
                mT__51(); 

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
                // GCL.g:1:99: FALSE
                {
                mFALSE(); 

                }
                break;
            case 18 :
                // GCL.g:1:105: OTHER
                {
                mOTHER(); 

                }
                break;
            case 19 :
                // GCL.g:1:111: ANY
                {
                mANY(); 

                }
                break;
            case 20 :
                // GCL.g:1:115: NODE_TYPE
                {
                mNODE_TYPE(); 

                }
                break;
            case 21 :
                // GCL.g:1:125: BOOL_TYPE
                {
                mBOOL_TYPE(); 

                }
                break;
            case 22 :
                // GCL.g:1:135: STRING_TYPE
                {
                mSTRING_TYPE(); 

                }
                break;
            case 23 :
                // GCL.g:1:147: INT_TYPE
                {
                mINT_TYPE(); 

                }
                break;
            case 24 :
                // GCL.g:1:156: REAL_TYPE
                {
                mREAL_TYPE(); 

                }
                break;
            case 25 :
                // GCL.g:1:166: OUT
                {
                mOUT(); 

                }
                break;
            case 26 :
                // GCL.g:1:170: IDENTIFIER
                {
                mIDENTIFIER(); 

                }
                break;
            case 27 :
                // GCL.g:1:181: STRING
                {
                mSTRING(); 

                }
                break;
            case 28 :
                // GCL.g:1:188: INT
                {
                mINT(); 

                }
                break;
            case 29 :
                // GCL.g:1:192: REAL
                {
                mREAL(); 

                }
                break;
            case 30 :
                // GCL.g:1:197: AND
                {
                mAND(); 

                }
                break;
            case 31 :
                // GCL.g:1:201: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 32 :
                // GCL.g:1:207: DOT
                {
                mDOT(); 

                }
                break;
            case 33 :
                // GCL.g:1:211: NOT
                {
                mNOT(); 

                }
                break;
            case 34 :
                // GCL.g:1:215: OR
                {
                mOR(); 

                }
                break;
            case 35 :
                // GCL.g:1:218: SHARP
                {
                mSHARP(); 

                }
                break;
            case 36 :
                // GCL.g:1:224: PLUS
                {
                mPLUS(); 

                }
                break;
            case 37 :
                // GCL.g:1:229: STAR
                {
                mSTAR(); 

                }
                break;
            case 38 :
                // GCL.g:1:234: DONT_CARE
                {
                mDONT_CARE(); 

                }
                break;
            case 39 :
                // GCL.g:1:244: QUOTE
                {
                mQUOTE(); 

                }
                break;
            case 40 :
                // GCL.g:1:250: ML_COMMENT
                {
                mML_COMMENT(); 

                }
                break;
            case 41 :
                // GCL.g:1:261: SL_COMMENT
                {
                mSL_COMMENT(); 

                }
                break;
            case 42 :
                // GCL.g:1:272: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA9 dfa9 = new DFA9(this);
    static final String DFA9_eotS =
        "\6\uffff\16\24\1\uffff\1\65\1\70\13\uffff\3\24\1\76\1\24\1\100\3"+
        "\24\1\104\11\24\6\uffff\1\24\1\120\1\24\1\uffff\1\24\1\uffff\1\123"+
        "\2\24\1\uffff\1\24\1\127\1\130\7\24\1\140\1\uffff\2\24\1\uffff\1"+
        "\143\2\24\2\uffff\1\146\2\24\1\151\1\152\1\24\1\154\1\uffff\1\155"+
        "\1\156\1\uffff\1\24\1\160\1\uffff\1\24\1\162\2\uffff\1\24\3\uffff"+
        "\1\164\1\uffff\1\24\1\uffff\1\166\1\uffff\1\24\1\uffff\1\170\1\uffff";
    static final String DFA9_eofS =
        "\171\uffff";
    static final String DFA9_minS =
        "\1\11\5\uffff\1\154\1\150\1\157\1\156\1\146\1\154\1\150\2\162\1"+
        "\141\2\157\1\164\1\145\1\uffff\1\0\1\56\11\uffff\1\52\1\uffff\1"+
        "\141\1\171\1\151\1\55\1\164\1\55\1\164\1\163\1\157\1\55\1\150\1"+
        "\164\1\165\1\156\1\154\1\144\1\157\1\162\1\141\6\uffff\1\160\1\55"+
        "\1\154\1\uffff\1\151\1\uffff\1\55\1\145\1\151\1\uffff\1\145\2\55"+
        "\1\145\1\143\1\163\1\145\1\154\1\151\1\154\1\55\1\uffff\1\145\1"+
        "\154\1\uffff\1\55\1\143\1\162\2\uffff\1\55\1\164\1\145\2\55\1\156"+
        "\1\55\1\uffff\2\55\1\uffff\1\145\1\55\1\uffff\1\151\1\55\2\uffff"+
        "\1\147\3\uffff\1\55\1\uffff\1\157\1\uffff\1\55\1\uffff\1\156\1\uffff"+
        "\1\55\1\uffff";
    static final String DFA9_maxS =
        "\1\175\5\uffff\1\156\1\150\1\157\2\156\1\154\1\150\1\165\1\162\1"+
        "\165\2\157\1\164\1\145\1\uffff\1\ufffe\1\71\11\uffff\1\57\1\uffff"+
        "\1\141\1\171\1\151\1\172\1\164\1\172\1\164\1\163\1\157\1\172\1\150"+
        "\1\164\1\171\1\156\1\154\1\144\1\157\1\162\1\141\6\uffff\1\160\1"+
        "\172\1\154\1\uffff\1\151\1\uffff\1\172\1\145\1\151\1\uffff\1\145"+
        "\2\172\1\145\1\143\1\163\1\145\1\154\1\151\1\154\1\172\1\uffff\1"+
        "\145\1\154\1\uffff\1\172\1\143\1\162\2\uffff\1\172\1\164\1\145\2"+
        "\172\1\156\1\172\1\uffff\2\172\1\uffff\1\145\1\172\1\uffff\1\151"+
        "\1\172\2\uffff\1\147\3\uffff\1\172\1\uffff\1\157\1\uffff\1\172\1"+
        "\uffff\1\156\1\uffff\1\172\1\uffff";
    static final String DFA9_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\16\uffff\1\32\2\uffff\1\36\1\37\1\40"+
        "\1\41\1\42\1\43\1\44\1\45\1\46\1\uffff\1\52\23\uffff\1\47\1\33\1"+
        "\35\1\34\1\50\1\51\3\uffff\1\10\1\uffff\1\12\3\uffff\1\15\13\uffff"+
        "\1\23\2\uffff\1\27\3\uffff\1\31\1\16\7\uffff\1\6\2\uffff\1\13\2"+
        "\uffff\1\20\2\uffff\1\24\1\25\1\uffff\1\30\1\7\1\11\1\uffff\1\22"+
        "\1\uffff\1\21\1\uffff\1\14\1\uffff\1\26\1\uffff\1\17";
    static final String DFA9_specialS =
        "\171\uffff}>";
    static final String[] DFA9_transitionS = {
            "\2\41\2\uffff\1\41\22\uffff\1\41\1\32\1\25\1\34\2\uffff\1\27"+
            "\1\uffff\1\3\1\4\1\36\1\35\1\30\1\uffff\1\31\1\40\12\26\1\uffff"+
            "\1\5\5\uffff\32\24\4\uffff\1\37\1\uffff\1\6\1\21\1\14\1\10\1"+
            "\13\1\17\2\24\1\12\4\24\1\20\1\15\2\24\1\23\1\22\1\16\1\11\1"+
            "\24\1\7\3\24\1\1\1\33\1\2",
            "",
            "",
            "",
            "",
            "",
            "\1\42\1\uffff\1\43",
            "\1\44",
            "\1\45",
            "\1\46",
            "\1\47\7\uffff\1\50",
            "\1\51",
            "\1\52",
            "\1\53\1\uffff\1\54\1\55",
            "\1\56",
            "\1\60\23\uffff\1\57",
            "\1\61",
            "\1\62",
            "\1\63",
            "\1\64",
            "",
            "\uffff\66",
            "\1\67\1\uffff\12\26",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\71\4\uffff\1\72",
            "",
            "\1\73",
            "\1\74",
            "\1\75",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\77",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\101",
            "\1\102",
            "\1\103",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\105",
            "\1\106",
            "\1\110\3\uffff\1\107",
            "\1\111",
            "\1\112",
            "\1\113",
            "\1\114",
            "\1\115",
            "\1\116",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\117",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\121",
            "",
            "\1\122",
            "",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\124",
            "\1\125",
            "",
            "\1\126",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\131",
            "\1\132",
            "\1\133",
            "\1\134",
            "\1\135",
            "\1\136",
            "\1\137",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\141",
            "\1\142",
            "",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\144",
            "\1\145",
            "",
            "",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\147",
            "\1\150",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\153",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\157",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\161",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "",
            "\1\163",
            "",
            "",
            "",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\165",
            "",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "",
            "\1\167",
            "",
            "\2\24\1\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            ""
    };

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__47 | T__48 | T__49 | T__50 | T__51 | ALAP | WHILE | DO | UNTIL | IF | ELSE | CHOICE | CH_OR | TRY | FUNCTION | TRUE | FALSE | OTHER | ANY | NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE | OUT | IDENTIFIER | STRING | INT | REAL | AND | COMMA | DOT | NOT | OR | SHARP | PLUS | STAR | DONT_CARE | QUOTE | ML_COMMENT | SL_COMMENT | WS );";
        }
    }
 

}