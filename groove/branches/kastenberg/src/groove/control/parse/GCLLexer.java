// $ANTLR 3.1b1 GCL.g 2010-05-04 16:29:38

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
    public static final int BOOL_TYPE=30;
    public static final int NODE_TYPE=29;
    public static final int DO=9;
    public static final int PARAM=11;
    public static final int NOT=43;
    public static final int ALAP=14;
    public static final int AND=42;
    public static final int EOF=-1;
    public static final int IF=19;
    public static final int ML_COMMENT=44;
    public static final int QUOTE=38;
    public static final int T__51=51;
    public static final int COMMA=34;
    public static final int IDENTIFIER=12;
    public static final int CH_OR=18;
    public static final int PLUS=23;
    public static final int VAR=10;
    public static final int DOT=28;
    public static final int T__50=50;
    public static final int CHOICE=17;
    public static final int T__47=47;
    public static final int SHARP=25;
    public static final int OTHER=27;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int ELSE=20;
    public static final int NUMBER=41;
    public static final int MINUS=40;
    public static final int INT_TYPE=32;
    public static final int TRUE=22;
    public static final int TRY=21;
    public static final int REAL_TYPE=33;
    public static final int DONT_CARE=36;
    public static final int WS=46;
    public static final int ANY=26;
    public static final int OUT=35;
    public static final int UNTIL=16;
    public static final int STRING_TYPE=31;
    public static final int BLOCK=5;
    public static final int OR=13;
    public static final int SL_COMMENT=45;
    public static final int PROGRAM=4;
    public static final int CALL=8;
    public static final int FALSE=37;
    public static final int BSLASH=39;

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
            // GCL.g:171:7: ( 'alap' )
            // GCL.g:171:9: 'alap'
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
            // GCL.g:172:7: ( 'while' )
            // GCL.g:172:9: 'while'
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
            // GCL.g:173:5: ( 'do' )
            // GCL.g:173:7: 'do'
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
            // GCL.g:174:9: ( 'until' )
            // GCL.g:174:11: 'until'
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
            // GCL.g:175:5: ( 'if' )
            // GCL.g:175:7: 'if'
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
            // GCL.g:176:6: ( 'else' )
            // GCL.g:176:8: 'else'
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
            // GCL.g:177:8: ( 'choice' )
            // GCL.g:177:10: 'choice'
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
            // GCL.g:178:8: ( 'or' )
            // GCL.g:178:10: 'or'
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
            // GCL.g:179:6: ( 'try' )
            // GCL.g:179:8: 'try'
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
            // GCL.g:180:9: ( 'function' )
            // GCL.g:180:11: 'function'
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
            // GCL.g:181:6: ( 'true' )
            // GCL.g:181:8: 'true'
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
            // GCL.g:182:7: ( 'false' )
            // GCL.g:182:9: 'false'
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
            // GCL.g:183:7: ( 'other' )
            // GCL.g:183:9: 'other'
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
            // GCL.g:184:6: ( 'any' )
            // GCL.g:184:8: 'any'
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
            // GCL.g:185:11: ( 'node' )
            // GCL.g:185:13: 'node'
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
            // GCL.g:186:11: ( 'bool' )
            // GCL.g:186:13: 'bool'
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
            // GCL.g:187:13: ( 'string' )
            // GCL.g:187:15: 'string'
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
            // GCL.g:188:10: ( 'int' )
            // GCL.g:188:12: 'int'
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
            // GCL.g:189:11: ( 'real' )
            // GCL.g:189:13: 'real'
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
            // GCL.g:190:6: ( 'out' )
            // GCL.g:190:8: 'out'
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

    // $ANTLR start AND
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:193:6: ( '&' )
            // GCL.g:193:9: '&'
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
            // GCL.g:194:8: ( ',' )
            // GCL.g:194:11: ','
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
            // GCL.g:195:6: ( '.' )
            // GCL.g:195:9: '.'
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
            // GCL.g:196:6: ( '!' )
            // GCL.g:196:9: '!'
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
            // GCL.g:197:5: ( '|' )
            // GCL.g:197:8: '|'
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
            // GCL.g:198:8: ( '#' )
            // GCL.g:198:11: '#'
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
            // GCL.g:199:7: ( '+' )
            // GCL.g:199:10: '+'
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
            // GCL.g:200:7: ( '*' )
            // GCL.g:200:10: '*'
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
            // GCL.g:201:11: ( '_' )
            // GCL.g:201:13: '_'
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

    // $ANTLR start MINUS
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:202:7: ( '-' )
            // GCL.g:202:9: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end MINUS

    // $ANTLR start QUOTE
    public final void mQUOTE() throws RecognitionException {
        try {
            int _type = QUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:203:9: ( '\"' )
            // GCL.g:203:11: '\"'
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

    // $ANTLR start BSLASH
    public final void mBSLASH() throws RecognitionException {
        try {
            int _type = BSLASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:204:8: ( '\\\\' )
            // GCL.g:204:10: '\\\\'
            {
            match('\\'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end BSLASH

    // $ANTLR start IDENTIFIER
    public final void mIDENTIFIER() throws RecognitionException {
        try {
            int _type = IDENTIFIER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:206:13: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // GCL.g:206:15: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // GCL.g:206:35: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // GCL.g:
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
    // $ANTLR end IDENTIFIER

    // $ANTLR start NUMBER
    public final void mNUMBER() throws RecognitionException {
        try {
            int _type = NUMBER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:209:8: ( ( '0' .. '9' )+ )
            // GCL.g:209:10: ( '0' .. '9' )+
            {
            // GCL.g:209:10: ( '0' .. '9' )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='0' && LA2_0<='9')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // GCL.g:209:11: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

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


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end NUMBER

    // $ANTLR start ML_COMMENT
    public final void mML_COMMENT() throws RecognitionException {
        try {
            int _type = ML_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:212:12: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // GCL.g:212:14: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // GCL.g:212:19: ( options {greedy=false; } : . )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0=='*') ) {
                    int LA3_1 = input.LA(2);

                    if ( (LA3_1=='/') ) {
                        alt3=2;
                    }
                    else if ( ((LA3_1>='\u0000' && LA3_1<='.')||(LA3_1>='0' && LA3_1<='\uFFFE')) ) {
                        alt3=1;
                    }


                }
                else if ( ((LA3_0>='\u0000' && LA3_0<=')')||(LA3_0>='+' && LA3_0<='\uFFFE')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // GCL.g:212:47: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop3;
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
            // GCL.g:213:12: ( '//' ( options {greedy=false; } : . )* '\\n' )
            // GCL.g:213:14: '//' ( options {greedy=false; } : . )* '\\n'
            {
            match("//"); 

            // GCL.g:213:19: ( options {greedy=false; } : . )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0=='\n') ) {
                    alt4=2;
                }
                else if ( ((LA4_0>='\u0000' && LA4_0<='\t')||(LA4_0>='\u000B' && LA4_0<='\uFFFE')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // GCL.g:213:47: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop4;
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
            // GCL.g:215:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // GCL.g:215:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // GCL.g:215:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0>='\t' && LA5_0<='\n')||LA5_0=='\r'||LA5_0==' ') ) {
                    alt5=1;
                }


                switch (alt5) {
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
            	    if ( cnt5 >= 1 ) break loop5;
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
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
        // GCL.g:1:8: ( T__47 | T__48 | T__49 | T__50 | T__51 | ALAP | WHILE | DO | UNTIL | IF | ELSE | CHOICE | CH_OR | TRY | FUNCTION | TRUE | FALSE | OTHER | ANY | NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE | OUT | AND | COMMA | DOT | NOT | OR | SHARP | PLUS | STAR | DONT_CARE | MINUS | QUOTE | BSLASH | IDENTIFIER | NUMBER | ML_COMMENT | SL_COMMENT | WS )
        int alt6=42;
        alt6 = dfa6.predict(input);
        switch (alt6) {
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
                // GCL.g:1:170: AND
                {
                mAND(); 

                }
                break;
            case 27 :
                // GCL.g:1:174: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 28 :
                // GCL.g:1:180: DOT
                {
                mDOT(); 

                }
                break;
            case 29 :
                // GCL.g:1:184: NOT
                {
                mNOT(); 

                }
                break;
            case 30 :
                // GCL.g:1:188: OR
                {
                mOR(); 

                }
                break;
            case 31 :
                // GCL.g:1:191: SHARP
                {
                mSHARP(); 

                }
                break;
            case 32 :
                // GCL.g:1:197: PLUS
                {
                mPLUS(); 

                }
                break;
            case 33 :
                // GCL.g:1:202: STAR
                {
                mSTAR(); 

                }
                break;
            case 34 :
                // GCL.g:1:207: DONT_CARE
                {
                mDONT_CARE(); 

                }
                break;
            case 35 :
                // GCL.g:1:217: MINUS
                {
                mMINUS(); 

                }
                break;
            case 36 :
                // GCL.g:1:223: QUOTE
                {
                mQUOTE(); 

                }
                break;
            case 37 :
                // GCL.g:1:229: BSLASH
                {
                mBSLASH(); 

                }
                break;
            case 38 :
                // GCL.g:1:236: IDENTIFIER
                {
                mIDENTIFIER(); 

                }
                break;
            case 39 :
                // GCL.g:1:247: NUMBER
                {
                mNUMBER(); 

                }
                break;
            case 40 :
                // GCL.g:1:254: ML_COMMENT
                {
                mML_COMMENT(); 

                }
                break;
            case 41 :
                // GCL.g:1:265: SL_COMMENT
                {
                mSL_COMMENT(); 

                }
                break;
            case 42 :
                // GCL.g:1:276: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA6 dfa6 = new DFA6(this);
    static final String DFA6_eotS =
        "\6\uffff\16\40\20\uffff\3\40\1\74\1\40\1\76\3\40\1\102\11\40\2\uffff"+
        "\1\40\1\116\1\40\1\uffff\1\40\1\uffff\1\121\2\40\1\uffff\1\40\1"+
        "\125\1\126\7\40\1\136\1\uffff\2\40\1\uffff\1\141\2\40\2\uffff\1"+
        "\144\2\40\1\147\1\150\1\40\1\152\1\uffff\1\153\1\154\1\uffff\1\40"+
        "\1\156\1\uffff\1\40\1\160\2\uffff\1\40\3\uffff\1\162\1\uffff\1\40"+
        "\1\uffff\1\164\1\uffff\1\40\1\uffff\1\166\1\uffff";
    static final String DFA6_eofS =
        "\167\uffff";
    static final String DFA6_minS =
        "\1\11\5\uffff\1\154\1\150\1\157\1\156\1\146\1\154\1\150\2\162\1"+
        "\141\2\157\1\164\1\145\16\uffff\1\52\1\uffff\1\141\1\171\1\151\1"+
        "\60\1\164\1\60\1\164\1\163\1\157\1\60\1\150\1\164\1\165\1\156\1"+
        "\154\1\144\1\157\1\162\1\141\2\uffff\1\160\1\60\1\154\1\uffff\1"+
        "\151\1\uffff\1\60\1\145\1\151\1\uffff\1\145\2\60\1\145\1\143\1\163"+
        "\1\145\1\154\1\151\1\154\1\60\1\uffff\1\145\1\154\1\uffff\1\60\1"+
        "\143\1\162\2\uffff\1\60\1\164\1\145\2\60\1\156\1\60\1\uffff\2\60"+
        "\1\uffff\1\145\1\60\1\uffff\1\151\1\60\2\uffff\1\147\3\uffff\1\60"+
        "\1\uffff\1\157\1\uffff\1\60\1\uffff\1\156\1\uffff\1\60\1\uffff";
    static final String DFA6_maxS =
        "\1\175\5\uffff\1\156\1\150\1\157\2\156\1\154\1\150\1\165\1\162\1"+
        "\165\2\157\1\164\1\145\16\uffff\1\57\1\uffff\1\141\1\171\1\151\1"+
        "\172\1\164\1\172\1\164\1\163\1\157\1\172\1\150\1\164\1\171\1\156"+
        "\1\154\1\144\1\157\1\162\1\141\2\uffff\1\160\1\172\1\154\1\uffff"+
        "\1\151\1\uffff\1\172\1\145\1\151\1\uffff\1\145\2\172\1\145\1\143"+
        "\1\163\1\145\1\154\1\151\1\154\1\172\1\uffff\1\145\1\154\1\uffff"+
        "\1\172\1\143\1\162\2\uffff\1\172\1\164\1\145\2\172\1\156\1\172\1"+
        "\uffff\2\172\1\uffff\1\145\1\172\1\uffff\1\151\1\172\2\uffff\1\147"+
        "\3\uffff\1\172\1\uffff\1\157\1\uffff\1\172\1\uffff\1\156\1\uffff"+
        "\1\172\1\uffff";
    static final String DFA6_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\16\uffff\1\32\1\33\1\34\1\35\1\36\1"+
        "\37\1\40\1\41\1\42\1\43\1\44\1\45\1\46\1\47\1\uffff\1\52\23\uffff"+
        "\1\50\1\51\3\uffff\1\10\1\uffff\1\12\3\uffff\1\15\13\uffff\1\23"+
        "\2\uffff\1\27\3\uffff\1\31\1\16\7\uffff\1\6\2\uffff\1\13\2\uffff"+
        "\1\20\2\uffff\1\24\1\25\1\uffff\1\30\1\7\1\11\1\uffff\1\22\1\uffff"+
        "\1\21\1\uffff\1\14\1\uffff\1\26\1\uffff\1\17";
    static final String DFA6_specialS =
        "\167\uffff}>";
    static final String[] DFA6_transitionS = {
            "\2\43\2\uffff\1\43\22\uffff\1\43\1\27\1\36\1\31\2\uffff\1\24"+
            "\1\uffff\1\3\1\4\1\33\1\32\1\25\1\35\1\26\1\42\12\41\1\uffff"+
            "\1\5\5\uffff\32\40\1\uffff\1\37\2\uffff\1\34\1\uffff\1\6\1\21"+
            "\1\14\1\10\1\13\1\17\2\40\1\12\4\40\1\20\1\15\2\40\1\23\1\22"+
            "\1\16\1\11\1\40\1\7\3\40\1\1\1\30\1\2",
            "",
            "",
            "",
            "",
            "",
            "\1\44\1\uffff\1\45",
            "\1\46",
            "\1\47",
            "\1\50",
            "\1\51\7\uffff\1\52",
            "\1\53",
            "\1\54",
            "\1\55\1\uffff\1\56\1\57",
            "\1\60",
            "\1\62\23\uffff\1\61",
            "\1\63",
            "\1\64",
            "\1\65",
            "\1\66",
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
            "\1\67\4\uffff\1\70",
            "",
            "\1\71",
            "\1\72",
            "\1\73",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
            "\1\75",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
            "\1\77",
            "\1\100",
            "\1\101",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
            "\1\103",
            "\1\104",
            "\1\106\3\uffff\1\105",
            "\1\107",
            "\1\110",
            "\1\111",
            "\1\112",
            "\1\113",
            "\1\114",
            "",
            "",
            "\1\115",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
            "\1\117",
            "",
            "\1\120",
            "",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
            "\1\122",
            "\1\123",
            "",
            "\1\124",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
            "\1\127",
            "\1\130",
            "\1\131",
            "\1\132",
            "\1\133",
            "\1\134",
            "\1\135",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
            "",
            "\1\137",
            "\1\140",
            "",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
            "\1\142",
            "\1\143",
            "",
            "",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
            "\1\145",
            "\1\146",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
            "\1\151",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
            "",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
            "",
            "\1\155",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
            "",
            "\1\157",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
            "",
            "",
            "\1\161",
            "",
            "",
            "",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
            "",
            "\1\163",
            "",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
            "",
            "\1\165",
            "",
            "\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32\40",
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
            return "1:1: Tokens : ( T__47 | T__48 | T__49 | T__50 | T__51 | ALAP | WHILE | DO | UNTIL | IF | ELSE | CHOICE | CH_OR | TRY | FUNCTION | TRUE | FALSE | OTHER | ANY | NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE | OUT | AND | COMMA | DOT | NOT | OR | SHARP | PLUS | STAR | DONT_CARE | MINUS | QUOTE | BSLASH | IDENTIFIER | NUMBER | ML_COMMENT | SL_COMMENT | WS );";
        }
    }
 

}