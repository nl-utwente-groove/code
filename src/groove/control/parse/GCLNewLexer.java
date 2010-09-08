// $ANTLR 3.2 Sep 23, 2009 12:02:23 GCLNew.g 2010-09-08 15:26:58

package groove.control.parse;
import groove.control.*;
import java.util.LinkedList;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class GCLNewLexer extends Lexer {
    public static final int FUNCTION=7;
    public static final int STAR=30;
    public static final int WHILE=18;
    public static final int FUNCTIONS=6;
    public static final int BOOL_TYPE=37;
    public static final int NODE_TYPE=36;
    public static final int DO=20;
    public static final int NOT=49;
    public static final int ALAP=17;
    public static final int ID=14;
    public static final int AND=48;
    public static final int EOF=-1;
    public static final int IF=21;
    public static final int ML_COMMENT=50;
    public static final int QUOTE=44;
    public static final int ARG=11;
    public static final int LPAR=15;
    public static final int COMMA=35;
    public static final int DO_WHILE=9;
    public static final int CH_OR=25;
    public static final int PLUS=29;
    public static final int VAR=10;
    public static final int DOT=34;
    public static final int CHOICE=24;
    public static final int SHARP=31;
    public static final int OTHER=33;
    public static final int ELSE=22;
    public static final int NUMBER=47;
    public static final int LCURLY=12;
    public static final int MINUS=46;
    public static final int INT_TYPE=39;
    public static final int SEMI=26;
    public static final int TRUE=28;
    public static final int TRY=23;
    public static final int REAL_TYPE=40;
    public static final int DONT_CARE=42;
    public static final int WS=52;
    public static final int ANY=32;
    public static final int OUT=41;
    public static final int UNTIL=19;
    public static final int STRING_TYPE=38;
    public static final int BLOCK=5;
    public static final int OR=27;
    public static final int RCURLY=13;
    public static final int SL_COMMENT=51;
    public static final int PROGRAM=4;
    public static final int RPAR=16;
    public static final int CALL=8;
    public static final int FALSE=43;
    public static final int BSLASH=45;

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
            // GCLNew.g:183:13: ( 'alap' )
            // GCLNew.g:183:15: 'alap'
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
            // GCLNew.g:184:13: ( 'while' )
            // GCLNew.g:184:15: 'while'
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
            // GCLNew.g:185:13: ( 'do' )
            // GCLNew.g:185:15: 'do'
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
            // GCLNew.g:186:13: ( 'until' )
            // GCLNew.g:186:15: 'until'
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
            // GCLNew.g:187:13: ( 'if' )
            // GCLNew.g:187:15: 'if'
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
            // GCLNew.g:188:13: ( 'else' )
            // GCLNew.g:188:15: 'else'
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
            // GCLNew.g:189:13: ( 'choice' )
            // GCLNew.g:189:15: 'choice'
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
            // GCLNew.g:190:13: ( 'or' )
            // GCLNew.g:190:15: 'or'
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
            // GCLNew.g:191:13: ( 'try' )
            // GCLNew.g:191:15: 'try'
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
            // GCLNew.g:192:13: ( 'function' )
            // GCLNew.g:192:15: 'function'
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
            // GCLNew.g:193:13: ( 'true' )
            // GCLNew.g:193:15: 'true'
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
            // GCLNew.g:194:13: ( 'false' )
            // GCLNew.g:194:15: 'false'
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
            // GCLNew.g:195:13: ( 'other' )
            // GCLNew.g:195:15: 'other'
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
            // GCLNew.g:196:12: ( 'any' )
            // GCLNew.g:196:14: 'any'
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
            // GCLNew.g:197:13: ( 'node' )
            // GCLNew.g:197:15: 'node'
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
            // GCLNew.g:198:13: ( 'bool' )
            // GCLNew.g:198:15: 'bool'
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
            // GCLNew.g:199:13: ( 'string' )
            // GCLNew.g:199:15: 'string'
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
            // GCLNew.g:200:13: ( 'int' )
            // GCLNew.g:200:15: 'int'
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
            // GCLNew.g:201:13: ( 'real' )
            // GCLNew.g:201:15: 'real'
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
            // GCLNew.g:202:13: ( 'out' )
            // GCLNew.g:202:15: 'out'
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

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:204:11: ( '&' )
            // GCLNew.g:204:13: '&'
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
            // GCLNew.g:205:11: ( '.' )
            // GCLNew.g:205:13: '.'
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
            // GCLNew.g:206:11: ( '!' )
            // GCLNew.g:206:13: '!'
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
            // GCLNew.g:207:11: ( '|' )
            // GCLNew.g:207:13: '|'
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
            // GCLNew.g:208:11: ( '#' )
            // GCLNew.g:208:13: '#'
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
            // GCLNew.g:209:11: ( '+' )
            // GCLNew.g:209:13: '+'
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
            // GCLNew.g:210:11: ( '*' )
            // GCLNew.g:210:13: '*'
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
            // GCLNew.g:211:11: ( '_' )
            // GCLNew.g:211:13: '_'
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
            // GCLNew.g:212:11: ( '-' )
            // GCLNew.g:212:13: '-'
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
            // GCLNew.g:213:11: ( '\"' )
            // GCLNew.g:213:13: '\"'
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
            // GCLNew.g:214:11: ( '\\\\' )
            // GCLNew.g:214:13: '\\\\'
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
            // GCLNew.g:215:11: ( ',' )
            // GCLNew.g:215:13: ','
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
            // GCLNew.g:216:11: ( ';' )
            // GCLNew.g:216:13: ';'
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
            // GCLNew.g:217:11: ( '(' )
            // GCLNew.g:217:13: '('
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
            // GCLNew.g:218:11: ( ')' )
            // GCLNew.g:218:13: ')'
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
            // GCLNew.g:219:11: ( '{' )
            // GCLNew.g:219:13: '{'
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
            // GCLNew.g:220:11: ( '}' )
            // GCLNew.g:220:13: '}'
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

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:222:5: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )* )
            // GCLNew.g:222:7: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // GCLNew.g:222:27: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='-'||(LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
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
    // $ANTLR end "ID"

    // $ANTLR start "NUMBER"
    public final void mNUMBER() throws RecognitionException {
        try {
            int _type = NUMBER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:224:8: ( ( '0' .. '9' )+ )
            // GCLNew.g:224:10: ( '0' .. '9' )+
            {
            // GCLNew.g:224:10: ( '0' .. '9' )+
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
            	    // GCLNew.g:224:11: '0' .. '9'
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
    // $ANTLR end "NUMBER"

    // $ANTLR start "ML_COMMENT"
    public final void mML_COMMENT() throws RecognitionException {
        try {
            int _type = ML_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:226:12: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // GCLNew.g:226:14: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // GCLNew.g:226:19: ( options {greedy=false; } : . )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0=='*') ) {
                    int LA3_1 = input.LA(2);

                    if ( (LA3_1=='/') ) {
                        alt3=2;
                    }
                    else if ( ((LA3_1>='\u0000' && LA3_1<='.')||(LA3_1>='0' && LA3_1<='\uFFFF')) ) {
                        alt3=1;
                    }


                }
                else if ( ((LA3_0>='\u0000' && LA3_0<=')')||(LA3_0>='+' && LA3_0<='\uFFFF')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // GCLNew.g:226:47: .
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
    // $ANTLR end "ML_COMMENT"

    // $ANTLR start "SL_COMMENT"
    public final void mSL_COMMENT() throws RecognitionException {
        try {
            int _type = SL_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:227:12: ( '//' ( options {greedy=false; } : . )* '\\n' )
            // GCLNew.g:227:14: '//' ( options {greedy=false; } : . )* '\\n'
            {
            match("//"); 

            // GCLNew.g:227:19: ( options {greedy=false; } : . )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0=='\n') ) {
                    alt4=2;
                }
                else if ( ((LA4_0>='\u0000' && LA4_0<='\t')||(LA4_0>='\u000B' && LA4_0<='\uFFFF')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // GCLNew.g:227:47: .
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
    // $ANTLR end "SL_COMMENT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCLNew.g:229:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // GCLNew.g:229:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // GCLNew.g:229:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
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
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // GCLNew.g:1:8: ( ALAP | WHILE | DO | UNTIL | IF | ELSE | CHOICE | CH_OR | TRY | FUNCTION | TRUE | FALSE | OTHER | ANY | NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE | OUT | AND | DOT | NOT | OR | SHARP | PLUS | STAR | DONT_CARE | MINUS | QUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LCURLY | RCURLY | ID | NUMBER | ML_COMMENT | SL_COMMENT | WS )
        int alt6=42;
        alt6 = dfa6.predict(input);
        switch (alt6) {
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
                // GCLNew.g:1:140: AND
                {
                mAND(); 

                }
                break;
            case 22 :
                // GCLNew.g:1:144: DOT
                {
                mDOT(); 

                }
                break;
            case 23 :
                // GCLNew.g:1:148: NOT
                {
                mNOT(); 

                }
                break;
            case 24 :
                // GCLNew.g:1:152: OR
                {
                mOR(); 

                }
                break;
            case 25 :
                // GCLNew.g:1:155: SHARP
                {
                mSHARP(); 

                }
                break;
            case 26 :
                // GCLNew.g:1:161: PLUS
                {
                mPLUS(); 

                }
                break;
            case 27 :
                // GCLNew.g:1:166: STAR
                {
                mSTAR(); 

                }
                break;
            case 28 :
                // GCLNew.g:1:171: DONT_CARE
                {
                mDONT_CARE(); 

                }
                break;
            case 29 :
                // GCLNew.g:1:181: MINUS
                {
                mMINUS(); 

                }
                break;
            case 30 :
                // GCLNew.g:1:187: QUOTE
                {
                mQUOTE(); 

                }
                break;
            case 31 :
                // GCLNew.g:1:193: BSLASH
                {
                mBSLASH(); 

                }
                break;
            case 32 :
                // GCLNew.g:1:200: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 33 :
                // GCLNew.g:1:206: SEMI
                {
                mSEMI(); 

                }
                break;
            case 34 :
                // GCLNew.g:1:211: LPAR
                {
                mLPAR(); 

                }
                break;
            case 35 :
                // GCLNew.g:1:216: RPAR
                {
                mRPAR(); 

                }
                break;
            case 36 :
                // GCLNew.g:1:221: LCURLY
                {
                mLCURLY(); 

                }
                break;
            case 37 :
                // GCLNew.g:1:228: RCURLY
                {
                mRCURLY(); 

                }
                break;
            case 38 :
                // GCLNew.g:1:235: ID
                {
                mID(); 

                }
                break;
            case 39 :
                // GCLNew.g:1:238: NUMBER
                {
                mNUMBER(); 

                }
                break;
            case 40 :
                // GCLNew.g:1:245: ML_COMMENT
                {
                mML_COMMENT(); 

                }
                break;
            case 41 :
                // GCLNew.g:1:256: SL_COMMENT
                {
                mSL_COMMENT(); 

                }
                break;
            case 42 :
                // GCLNew.g:1:267: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA6 dfa6 = new DFA6(this);
    static final String DFA6_eotS =
        "\1\uffff\16\40\25\uffff\3\40\1\74\1\40\1\76\3\40\1\102\11\40\2"+
        "\uffff\1\40\1\116\1\40\1\uffff\1\40\1\uffff\1\121\2\40\1\uffff\1"+
        "\40\1\125\1\126\7\40\1\136\1\uffff\2\40\1\uffff\1\141\2\40\2\uffff"+
        "\1\144\2\40\1\147\1\150\1\40\1\152\1\uffff\1\153\1\154\1\uffff\1"+
        "\40\1\156\1\uffff\1\40\1\160\2\uffff\1\40\3\uffff\1\162\1\uffff"+
        "\1\40\1\uffff\1\164\1\uffff\1\40\1\uffff\1\166\1\uffff";
    static final String DFA6_eofS =
        "\167\uffff";
    static final String DFA6_minS =
        "\1\11\1\154\1\150\1\157\1\156\1\146\1\154\1\150\2\162\1\141\2\157"+
        "\1\164\1\145\23\uffff\1\52\1\uffff\1\141\1\171\1\151\1\55\1\164"+
        "\1\55\1\164\1\163\1\157\1\55\1\150\1\164\1\165\1\156\1\154\1\144"+
        "\1\157\1\162\1\141\2\uffff\1\160\1\55\1\154\1\uffff\1\151\1\uffff"+
        "\1\55\1\145\1\151\1\uffff\1\145\2\55\1\145\1\143\1\163\1\145\1\154"+
        "\1\151\1\154\1\55\1\uffff\1\145\1\154\1\uffff\1\55\1\143\1\162\2"+
        "\uffff\1\55\1\164\1\145\2\55\1\156\1\55\1\uffff\2\55\1\uffff\1\145"+
        "\1\55\1\uffff\1\151\1\55\2\uffff\1\147\3\uffff\1\55\1\uffff\1\157"+
        "\1\uffff\1\55\1\uffff\1\156\1\uffff\1\55\1\uffff";
    static final String DFA6_maxS =
        "\1\175\1\156\1\150\1\157\2\156\1\154\1\150\1\165\1\162\1\165\2"+
        "\157\1\164\1\145\23\uffff\1\57\1\uffff\1\141\1\171\1\151\1\172\1"+
        "\164\1\172\1\164\1\163\1\157\1\172\1\150\1\164\1\171\1\156\1\154"+
        "\1\144\1\157\1\162\1\141\2\uffff\1\160\1\172\1\154\1\uffff\1\151"+
        "\1\uffff\1\172\1\145\1\151\1\uffff\1\145\2\172\1\145\1\143\1\163"+
        "\1\145\1\154\1\151\1\154\1\172\1\uffff\1\145\1\154\1\uffff\1\172"+
        "\1\143\1\162\2\uffff\1\172\1\164\1\145\2\172\1\156\1\172\1\uffff"+
        "\2\172\1\uffff\1\145\1\172\1\uffff\1\151\1\172\2\uffff\1\147\3\uffff"+
        "\1\172\1\uffff\1\157\1\uffff\1\172\1\uffff\1\156\1\uffff\1\172\1"+
        "\uffff";
    static final String DFA6_acceptS =
        "\17\uffff\1\25\1\26\1\27\1\30\1\31\1\32\1\33\1\34\1\35\1\36\1\37"+
        "\1\40\1\41\1\42\1\43\1\44\1\45\1\46\1\47\1\uffff\1\52\23\uffff\1"+
        "\50\1\51\3\uffff\1\3\1\uffff\1\5\3\uffff\1\10\13\uffff\1\16\2\uffff"+
        "\1\22\3\uffff\1\24\1\11\7\uffff\1\1\2\uffff\1\6\2\uffff\1\13\2\uffff"+
        "\1\17\1\20\1\uffff\1\23\1\2\1\4\1\uffff\1\15\1\uffff\1\14\1\uffff"+
        "\1\7\1\uffff\1\21\1\uffff\1\12";
    static final String DFA6_specialS =
        "\167\uffff}>";
    static final String[] DFA6_transitionS = {
            "\2\43\2\uffff\1\43\22\uffff\1\43\1\21\1\30\1\23\2\uffff\1\17"+
            "\1\uffff\1\34\1\35\1\25\1\24\1\32\1\27\1\20\1\42\12\41\1\uffff"+
            "\1\33\5\uffff\32\40\1\uffff\1\31\2\uffff\1\26\1\uffff\1\1\1"+
            "\14\1\7\1\3\1\6\1\12\2\40\1\5\4\40\1\13\1\10\2\40\1\16\1\15"+
            "\1\11\1\4\1\40\1\2\3\40\1\36\1\22\1\37",
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
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "\1\75",
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "\1\77",
            "\1\100",
            "\1\101",
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
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
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "\1\117",
            "",
            "\1\120",
            "",
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "\1\122",
            "\1\123",
            "",
            "\1\124",
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "\1\127",
            "\1\130",
            "\1\131",
            "\1\132",
            "\1\133",
            "\1\134",
            "\1\135",
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "",
            "\1\137",
            "\1\140",
            "",
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "\1\142",
            "\1\143",
            "",
            "",
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "\1\145",
            "\1\146",
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "\1\151",
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "",
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "",
            "\1\155",
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "",
            "\1\157",
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "",
            "",
            "\1\161",
            "",
            "",
            "",
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "",
            "\1\163",
            "",
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "",
            "\1\165",
            "",
            "\1\40\2\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
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
            return "1:1: Tokens : ( ALAP | WHILE | DO | UNTIL | IF | ELSE | CHOICE | CH_OR | TRY | FUNCTION | TRUE | FALSE | OTHER | ANY | NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE | OUT | AND | DOT | NOT | OR | SHARP | PLUS | STAR | DONT_CARE | MINUS | QUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LCURLY | RCURLY | ID | NUMBER | ML_COMMENT | SL_COMMENT | WS );";
        }
    }
 

}