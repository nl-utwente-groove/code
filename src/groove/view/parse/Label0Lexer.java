// $ANTLR 3.1b1 Label0.g 2010-04-26 09:45:54

package groove.view.parse;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")      
public class Label0Lexer extends Lexer {
    public static final int DOLLAR=53;
    public static final int STAR=44;
    public static final int LSQUARE=48;
    public static final int FORALLX=11;
    public static final int LETTER=55;
    public static final int DEL=5;
    public static final int LBRACE=35;
    public static final int NEW=4;
    public static final int DQUOTE=52;
    public static final int IDENTCHAR=56;
    public static final int EQUALS=30;
    public static final int NOT=6;
    public static final int ATOM=26;
    public static final int EOF=-1;
    public static final int TYPE=22;
    public static final int HAT=49;
    public static final int UNDER=54;
    public static final int T__58=58;
    public static final int PLING=37;
    public static final int LPAR=46;
    public static final int ARG=20;
    public static final int COMMA=50;
    public static final int PATH=24;
    public static final int PROD=19;
    public static final int PAR=21;
    public static final int IDENT=31;
    public static final int PLUS=45;
    public static final int DIGIT=57;
    public static final int EXISTS=12;
    public static final int DOT=41;
    public static final int ATTR=18;
    public static final int RBRACE=36;
    public static final int BOOL=17;
    public static final int NUMBER=34;
    public static final int FORALL=10;
    public static final int INT=14;
    public static final int RSQUARE=51;
    public static final int MINUS=43;
    public static final int REM=9;
    public static final int SQUOTE=39;
    public static final int TRUE=27;
    public static final int CNEW=8;
    public static final int FLAG=23;
    public static final int EMPTY=25;
    public static final int COLON=32;
    public static final int NESTED=13;
    public static final int REAL=15;
    public static final int LABEL=33;
    public static final int QUERY=38;
    public static final int RPAR=47;
    public static final int USE=7;
    public static final int FALSE=28;
    public static final int CONSTRAINT=29;
    public static final int BSLASH=40;
    public static final int BAR=42;
    public static final int STRING=16;

    // delegates
    // delegators

    public Label0Lexer() {;} 
    public Label0Lexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public Label0Lexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "Label0.g"; }

    // $ANTLR start NEW
    public final void mNEW() throws RecognitionException {
        try {
            int _type = NEW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:11:5: ( 'new' )
            // Label0.g:11:7: 'new'
            {
            match("new"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end NEW

    // $ANTLR start DEL
    public final void mDEL() throws RecognitionException {
        try {
            int _type = DEL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:12:5: ( 'del' )
            // Label0.g:12:7: 'del'
            {
            match("del"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end DEL

    // $ANTLR start NOT
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:13:5: ( 'not' )
            // Label0.g:13:7: 'not'
            {
            match("not"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end NOT

    // $ANTLR start USE
    public final void mUSE() throws RecognitionException {
        try {
            int _type = USE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:14:5: ( 'use' )
            // Label0.g:14:7: 'use'
            {
            match("use"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end USE

    // $ANTLR start CNEW
    public final void mCNEW() throws RecognitionException {
        try {
            int _type = CNEW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:15:6: ( 'cnew' )
            // Label0.g:15:8: 'cnew'
            {
            match("cnew"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end CNEW

    // $ANTLR start REM
    public final void mREM() throws RecognitionException {
        try {
            int _type = REM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:16:5: ( 'rem' )
            // Label0.g:16:7: 'rem'
            {
            match("rem"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end REM

    // $ANTLR start FORALL
    public final void mFORALL() throws RecognitionException {
        try {
            int _type = FORALL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:17:8: ( 'forall' )
            // Label0.g:17:10: 'forall'
            {
            match("forall"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end FORALL

    // $ANTLR start FORALLX
    public final void mFORALLX() throws RecognitionException {
        try {
            int _type = FORALLX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:18:9: ( 'forallx' )
            // Label0.g:18:11: 'forallx'
            {
            match("forallx"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end FORALLX

    // $ANTLR start EXISTS
    public final void mEXISTS() throws RecognitionException {
        try {
            int _type = EXISTS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:19:8: ( 'exists' )
            // Label0.g:19:10: 'exists'
            {
            match("exists"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end EXISTS

    // $ANTLR start NESTED
    public final void mNESTED() throws RecognitionException {
        try {
            int _type = NESTED;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:20:8: ( 'nested' )
            // Label0.g:20:10: 'nested'
            {
            match("nested"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end NESTED

    // $ANTLR start INT
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:21:5: ( 'int' )
            // Label0.g:21:7: 'int'
            {
            match("int"); 


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
            // Label0.g:22:6: ( 'real' )
            // Label0.g:22:8: 'real'
            {
            match("real"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end REAL

    // $ANTLR start STRING
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:23:8: ( 'string' )
            // Label0.g:23:10: 'string'
            {
            match("string"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end STRING

    // $ANTLR start BOOL
    public final void mBOOL() throws RecognitionException {
        try {
            int _type = BOOL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:24:6: ( 'bool' )
            // Label0.g:24:8: 'bool'
            {
            match("bool"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end BOOL

    // $ANTLR start ATTR
    public final void mATTR() throws RecognitionException {
        try {
            int _type = ATTR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:25:6: ( 'attr' )
            // Label0.g:25:8: 'attr'
            {
            match("attr"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end ATTR

    // $ANTLR start PROD
    public final void mPROD() throws RecognitionException {
        try {
            int _type = PROD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:26:6: ( 'prod' )
            // Label0.g:26:8: 'prod'
            {
            match("prod"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end PROD

    // $ANTLR start ARG
    public final void mARG() throws RecognitionException {
        try {
            int _type = ARG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:27:5: ( 'arg' )
            // Label0.g:27:7: 'arg'
            {
            match("arg"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end ARG

    // $ANTLR start PAR
    public final void mPAR() throws RecognitionException {
        try {
            int _type = PAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:28:5: ( 'par' )
            // Label0.g:28:7: 'par'
            {
            match("par"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end PAR

    // $ANTLR start TYPE
    public final void mTYPE() throws RecognitionException {
        try {
            int _type = TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:29:6: ( 'type' )
            // Label0.g:29:8: 'type'
            {
            match("type"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end TYPE

    // $ANTLR start FLAG
    public final void mFLAG() throws RecognitionException {
        try {
            int _type = FLAG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:30:6: ( 'flag' )
            // Label0.g:30:8: 'flag'
            {
            match("flag"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end FLAG

    // $ANTLR start PATH
    public final void mPATH() throws RecognitionException {
        try {
            int _type = PATH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:31:6: ( 'path' )
            // Label0.g:31:8: 'path'
            {
            match("path"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end PATH

    // $ANTLR start TRUE
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:32:6: ( 'true' )
            // Label0.g:32:8: 'true'
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
            // Label0.g:33:7: ( 'false' )
            // Label0.g:33:9: 'false'
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

    // $ANTLR start T__58
    public final void mT__58() throws RecognitionException {
        try {
            int _type = T__58;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:34:7: ( '\\n' )
            // Label0.g:34:9: '\\n'
            {
            match('\n'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__58

    // $ANTLR start MINUS
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:182:8: ( '-' )
            // Label0.g:182:10: '-'
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

    // $ANTLR start STAR
    public final void mSTAR() throws RecognitionException {
        try {
            int _type = STAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:183:8: ( '*' )
            // Label0.g:183:10: '*'
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

    // $ANTLR start PLUS
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:184:8: ( '+' )
            // Label0.g:184:10: '+'
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

    // $ANTLR start DOT
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:185:8: ( '.' )
            // Label0.g:185:10: '.'
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

    // $ANTLR start BAR
    public final void mBAR() throws RecognitionException {
        try {
            int _type = BAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:186:8: ( '|' )
            // Label0.g:186:10: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end BAR

    // $ANTLR start HAT
    public final void mHAT() throws RecognitionException {
        try {
            int _type = HAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:187:8: ( '^' )
            // Label0.g:187:10: '^'
            {
            match('^'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end HAT

    // $ANTLR start EQUALS
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:188:8: ( '=' )
            // Label0.g:188:10: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end EQUALS

    // $ANTLR start LBRACE
    public final void mLBRACE() throws RecognitionException {
        try {
            int _type = LBRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:189:8: ( '{' )
            // Label0.g:189:10: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end LBRACE

    // $ANTLR start RBRACE
    public final void mRBRACE() throws RecognitionException {
        try {
            int _type = RBRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:190:8: ( '}' )
            // Label0.g:190:10: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end RBRACE

    // $ANTLR start LPAR
    public final void mLPAR() throws RecognitionException {
        try {
            int _type = LPAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:191:8: ( '(' )
            // Label0.g:191:10: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end LPAR

    // $ANTLR start RPAR
    public final void mRPAR() throws RecognitionException {
        try {
            int _type = RPAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:192:8: ( ')' )
            // Label0.g:192:10: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end RPAR

    // $ANTLR start LSQUARE
    public final void mLSQUARE() throws RecognitionException {
        try {
            int _type = LSQUARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:193:9: ( '[' )
            // Label0.g:193:11: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end LSQUARE

    // $ANTLR start RSQUARE
    public final void mRSQUARE() throws RecognitionException {
        try {
            int _type = RSQUARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:194:9: ( ']' )
            // Label0.g:194:11: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end RSQUARE

    // $ANTLR start PLING
    public final void mPLING() throws RecognitionException {
        try {
            int _type = PLING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:195:8: ( '!' )
            // Label0.g:195:10: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end PLING

    // $ANTLR start QUERY
    public final void mQUERY() throws RecognitionException {
        try {
            int _type = QUERY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:196:8: ( '?' )
            // Label0.g:196:10: '?'
            {
            match('?'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end QUERY

    // $ANTLR start COLON
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:197:8: ( ':' )
            // Label0.g:197:10: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end COLON

    // $ANTLR start COMMA
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:198:8: ( ',' )
            // Label0.g:198:10: ','
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

    // $ANTLR start SQUOTE
    public final void mSQUOTE() throws RecognitionException {
        try {
            int _type = SQUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:199:8: ( '\\'' )
            // Label0.g:199:10: '\\''
            {
            match('\''); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end SQUOTE

    // $ANTLR start DQUOTE
    public final void mDQUOTE() throws RecognitionException {
        try {
            int _type = DQUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:200:8: ( '\"' )
            // Label0.g:200:10: '\"'
            {
            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end DQUOTE

    // $ANTLR start DOLLAR
    public final void mDOLLAR() throws RecognitionException {
        try {
            int _type = DOLLAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:201:8: ( '$' )
            // Label0.g:201:10: '$'
            {
            match('$'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end DOLLAR

    // $ANTLR start UNDER
    public final void mUNDER() throws RecognitionException {
        try {
            int _type = UNDER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:202:8: ( '_' )
            // Label0.g:202:10: '_'
            {
            match('_'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end UNDER

    // $ANTLR start BSLASH
    public final void mBSLASH() throws RecognitionException {
        try {
            int _type = BSLASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:203:8: ( '\\\\' )
            // Label0.g:203:10: '\\\\'
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

    // $ANTLR start IDENT
    public final void mIDENT() throws RecognitionException {
        try {
            int _type = IDENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:206:4: ( LETTER ( IDENTCHAR )* )
            // Label0.g:206:6: LETTER ( IDENTCHAR )*
            {
            mLETTER(); 
            // Label0.g:206:13: ( IDENTCHAR )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='$'||(LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // Label0.g:206:13: IDENTCHAR
            	    {
            	    mIDENTCHAR(); 

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
    // $ANTLR end IDENT

    // $ANTLR start NUMBER
    public final void mNUMBER() throws RecognitionException {
        try {
            int _type = NUMBER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:210:4: ( ( DIGIT )+ )
            // Label0.g:210:6: ( DIGIT )+
            {
            // Label0.g:210:6: ( DIGIT )+
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
            	    // Label0.g:210:6: DIGIT
            	    {
            	    mDIGIT(); 

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

    // $ANTLR start LABEL
    public final void mLABEL() throws RecognitionException {
        try {
            int _type = LABEL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label0.g:214:4: ( ( IDENTCHAR )+ )
            // Label0.g:214:6: ( IDENTCHAR )+
            {
            // Label0.g:214:6: ( IDENTCHAR )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0=='$'||(LA3_0>='0' && LA3_0<='9')||(LA3_0>='A' && LA3_0<='Z')||LA3_0=='_'||(LA3_0>='a' && LA3_0<='z')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // Label0.g:214:6: IDENTCHAR
            	    {
            	    mIDENTCHAR(); 

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
    // $ANTLR end LABEL

    // $ANTLR start IDENTCHAR
    public final void mIDENTCHAR() throws RecognitionException {
        try {
            // Label0.g:218:4: ( LETTER | DIGIT | DOLLAR | UNDER )
            // Label0.g:
            {
            if ( input.LA(1)=='$'||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end IDENTCHAR

    // $ANTLR start LETTER
    public final void mLETTER() throws RecognitionException {
        try {
            // Label0.g:220:17: ( 'a' .. 'z' | 'A' .. 'Z' )
            // Label0.g:
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end LETTER

    // $ANTLR start DIGIT
    public final void mDIGIT() throws RecognitionException {
        try {
            // Label0.g:221:17: ( '0' .. '9' )
            // Label0.g:221:19: '0' .. '9'
            {
            matchRange('0','9'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end DIGIT

    public void mTokens() throws RecognitionException {
        // Label0.g:1:8: ( NEW | DEL | NOT | USE | CNEW | REM | FORALL | FORALLX | EXISTS | NESTED | INT | REAL | STRING | BOOL | ATTR | PROD | ARG | PAR | TYPE | FLAG | PATH | TRUE | FALSE | T__58 | MINUS | STAR | PLUS | DOT | BAR | HAT | EQUALS | LBRACE | RBRACE | LPAR | RPAR | LSQUARE | RSQUARE | PLING | QUERY | COLON | COMMA | SQUOTE | DQUOTE | DOLLAR | UNDER | BSLASH | IDENT | NUMBER | LABEL )
        int alt4=49;
        alt4 = dfa4.predict(input);
        switch (alt4) {
            case 1 :
                // Label0.g:1:10: NEW
                {
                mNEW(); 

                }
                break;
            case 2 :
                // Label0.g:1:14: DEL
                {
                mDEL(); 

                }
                break;
            case 3 :
                // Label0.g:1:18: NOT
                {
                mNOT(); 

                }
                break;
            case 4 :
                // Label0.g:1:22: USE
                {
                mUSE(); 

                }
                break;
            case 5 :
                // Label0.g:1:26: CNEW
                {
                mCNEW(); 

                }
                break;
            case 6 :
                // Label0.g:1:31: REM
                {
                mREM(); 

                }
                break;
            case 7 :
                // Label0.g:1:35: FORALL
                {
                mFORALL(); 

                }
                break;
            case 8 :
                // Label0.g:1:42: FORALLX
                {
                mFORALLX(); 

                }
                break;
            case 9 :
                // Label0.g:1:50: EXISTS
                {
                mEXISTS(); 

                }
                break;
            case 10 :
                // Label0.g:1:57: NESTED
                {
                mNESTED(); 

                }
                break;
            case 11 :
                // Label0.g:1:64: INT
                {
                mINT(); 

                }
                break;
            case 12 :
                // Label0.g:1:68: REAL
                {
                mREAL(); 

                }
                break;
            case 13 :
                // Label0.g:1:73: STRING
                {
                mSTRING(); 

                }
                break;
            case 14 :
                // Label0.g:1:80: BOOL
                {
                mBOOL(); 

                }
                break;
            case 15 :
                // Label0.g:1:85: ATTR
                {
                mATTR(); 

                }
                break;
            case 16 :
                // Label0.g:1:90: PROD
                {
                mPROD(); 

                }
                break;
            case 17 :
                // Label0.g:1:95: ARG
                {
                mARG(); 

                }
                break;
            case 18 :
                // Label0.g:1:99: PAR
                {
                mPAR(); 

                }
                break;
            case 19 :
                // Label0.g:1:103: TYPE
                {
                mTYPE(); 

                }
                break;
            case 20 :
                // Label0.g:1:108: FLAG
                {
                mFLAG(); 

                }
                break;
            case 21 :
                // Label0.g:1:113: PATH
                {
                mPATH(); 

                }
                break;
            case 22 :
                // Label0.g:1:118: TRUE
                {
                mTRUE(); 

                }
                break;
            case 23 :
                // Label0.g:1:123: FALSE
                {
                mFALSE(); 

                }
                break;
            case 24 :
                // Label0.g:1:129: T__58
                {
                mT__58(); 

                }
                break;
            case 25 :
                // Label0.g:1:135: MINUS
                {
                mMINUS(); 

                }
                break;
            case 26 :
                // Label0.g:1:141: STAR
                {
                mSTAR(); 

                }
                break;
            case 27 :
                // Label0.g:1:146: PLUS
                {
                mPLUS(); 

                }
                break;
            case 28 :
                // Label0.g:1:151: DOT
                {
                mDOT(); 

                }
                break;
            case 29 :
                // Label0.g:1:155: BAR
                {
                mBAR(); 

                }
                break;
            case 30 :
                // Label0.g:1:159: HAT
                {
                mHAT(); 

                }
                break;
            case 31 :
                // Label0.g:1:163: EQUALS
                {
                mEQUALS(); 

                }
                break;
            case 32 :
                // Label0.g:1:170: LBRACE
                {
                mLBRACE(); 

                }
                break;
            case 33 :
                // Label0.g:1:177: RBRACE
                {
                mRBRACE(); 

                }
                break;
            case 34 :
                // Label0.g:1:184: LPAR
                {
                mLPAR(); 

                }
                break;
            case 35 :
                // Label0.g:1:189: RPAR
                {
                mRPAR(); 

                }
                break;
            case 36 :
                // Label0.g:1:194: LSQUARE
                {
                mLSQUARE(); 

                }
                break;
            case 37 :
                // Label0.g:1:202: RSQUARE
                {
                mRSQUARE(); 

                }
                break;
            case 38 :
                // Label0.g:1:210: PLING
                {
                mPLING(); 

                }
                break;
            case 39 :
                // Label0.g:1:216: QUERY
                {
                mQUERY(); 

                }
                break;
            case 40 :
                // Label0.g:1:222: COLON
                {
                mCOLON(); 

                }
                break;
            case 41 :
                // Label0.g:1:228: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 42 :
                // Label0.g:1:234: SQUOTE
                {
                mSQUOTE(); 

                }
                break;
            case 43 :
                // Label0.g:1:241: DQUOTE
                {
                mDQUOTE(); 

                }
                break;
            case 44 :
                // Label0.g:1:248: DOLLAR
                {
                mDOLLAR(); 

                }
                break;
            case 45 :
                // Label0.g:1:255: UNDER
                {
                mUNDER(); 

                }
                break;
            case 46 :
                // Label0.g:1:261: BSLASH
                {
                mBSLASH(); 

                }
                break;
            case 47 :
                // Label0.g:1:268: IDENT
                {
                mIDENT(); 

                }
                break;
            case 48 :
                // Label0.g:1:274: NUMBER
                {
                mNUMBER(); 

                }
                break;
            case 49 :
                // Label0.g:1:281: LABEL
                {
                mLABEL(); 

                }
                break;

        }

    }


    protected DFA4 dfa4 = new DFA4(this);
    static final String DFA4_eotS =
        "\1\uffff\15\51\24\uffff\1\74\1\76\1\uffff\1\51\1\77\2\51\1\uffff"+
        "\22\51\4\uffff\1\126\1\51\1\130\1\131\1\132\1\51\1\134\5\51\1\142"+
        "\3\51\1\146\1\51\1\150\3\51\1\uffff\1\51\3\uffff\1\155\1\uffff\1"+
        "\156\1\51\1\160\2\51\1\uffff\1\51\1\164\1\165\1\uffff\1\166\1\uffff"+
        "\1\167\1\170\1\171\1\51\2\uffff\1\51\1\uffff\1\174\2\51\6\uffff"+
        "\1\177\1\u0081\1\uffff\1\u0082\1\u0083\1\uffff\1\u0084\4\uffff";
    static final String DFA4_eofS =
        "\u0085\uffff";
    static final String DFA4_minS =
        "\1\12\15\44\24\uffff\2\44\1\uffff\4\44\1\uffff\22\44\4\uffff\26"+
        "\44\1\uffff\1\44\3\uffff\1\44\1\uffff\5\44\1\uffff\3\44\1\uffff"+
        "\1\44\1\uffff\4\44\2\uffff\1\44\1\uffff\3\44\6\uffff\2\44\1\uffff"+
        "\2\44\1\uffff\1\44\4\uffff";
    static final String DFA4_maxS =
        "\1\175\15\172\24\uffff\2\172\1\uffff\4\172\1\uffff\22\172\4\uffff"+
        "\26\172\1\uffff\1\172\3\uffff\1\172\1\uffff\5\172\1\uffff\3\172"+
        "\1\uffff\1\172\1\uffff\4\172\2\uffff\1\172\1\uffff\3\172\6\uffff"+
        "\2\172\1\uffff\2\172\1\uffff\1\172\4\uffff";
    static final String DFA4_acceptS =
        "\16\uffff\1\30\1\31\1\32\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\42"+
        "\1\43\1\44\1\45\1\46\1\47\1\50\1\51\1\52\1\53\2\uffff\1\56\4\uffff"+
        "\1\57\22\uffff\1\54\1\61\1\55\1\60\26\uffff\1\1\1\uffff\1\3\1\2"+
        "\1\4\1\uffff\1\6\5\uffff\1\13\3\uffff\1\21\1\uffff\1\22\4\uffff"+
        "\1\5\1\14\1\uffff\1\24\3\uffff\1\16\1\17\1\20\1\25\1\23\1\26\2\uffff"+
        "\1\27\2\uffff\1\12\1\uffff\1\7\1\11\1\15\1\10";
    static final String DFA4_specialS =
        "\u0085\uffff}>";
    static final String[] DFA4_transitionS = {
            "\1\16\26\uffff\1\34\1\41\1\uffff\1\42\2\uffff\1\40\1\30\1\31"+
            "\1\20\1\21\1\37\1\17\1\22\1\uffff\12\46\1\36\2\uffff\1\25\1"+
            "\uffff\1\35\1\uffff\32\45\1\32\1\44\1\33\1\24\1\43\1\uffff\1"+
            "\13\1\12\1\4\1\2\1\7\1\6\2\45\1\10\4\45\1\1\1\45\1\14\1\45\1"+
            "\5\1\11\1\15\1\3\5\45\1\26\1\23\1\27",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\4\52"+
            "\1\47\11\52\1\50\13\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\4\52"+
            "\1\53\25\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\22\52"+
            "\1\54\7\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\15\52"+
            "\1\55\14\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\4\52"+
            "\1\56\25\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\1\61"+
            "\12\52\1\60\2\52\1\57\13\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\27\52"+
            "\1\62\2\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\15\52"+
            "\1\63\14\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\23\52"+
            "\1\64\6\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\16\52"+
            "\1\65\13\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\21\52"+
            "\1\67\1\52\1\66\6\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\1\71"+
            "\20\52\1\70\10\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\21\52"+
            "\1\73\6\52\1\72\1\52",
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
            "",
            "\1\75\13\uffff\12\75\7\uffff\32\75\4\uffff\1\75\1\uffff\32\75",
            "\1\75\13\uffff\12\75\7\uffff\32\75\4\uffff\1\75\1\uffff\32\75",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\75\13\uffff\12\46\7\uffff\32\75\4\uffff\1\75\1\uffff\32\75",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\22\52"+
            "\1\101\3\52\1\100\3\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\23\52"+
            "\1\102\6\52",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\13\52"+
            "\1\103\16\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\4\52"+
            "\1\104\25\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\4\52"+
            "\1\105\25\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\1\107"+
            "\13\52\1\106\15\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\21\52"+
            "\1\110\10\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\1\111"+
            "\31\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\13\52"+
            "\1\112\16\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\10\52"+
            "\1\113\21\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\23\52"+
            "\1\114\6\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\21\52"+
            "\1\115\10\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\16\52"+
            "\1\116\13\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\23\52"+
            "\1\117\6\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\6\52"+
            "\1\120\23\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\16\52"+
            "\1\121\13\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\21\52"+
            "\1\122\1\52\1\123\6\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\17\52"+
            "\1\124\12\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\24\52"+
            "\1\125\5\52",
            "",
            "",
            "",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\23\52"+
            "\1\127\6\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\26\52"+
            "\1\133\3\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\13\52"+
            "\1\135\16\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\1\136"+
            "\31\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\6\52"+
            "\1\137\23\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\22\52"+
            "\1\140\7\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\22\52"+
            "\1\141\7\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\10\52"+
            "\1\143\21\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\13\52"+
            "\1\144\16\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\21\52"+
            "\1\145\10\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\3\52"+
            "\1\147\26\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\7\52"+
            "\1\151\22\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\4\52"+
            "\1\152\25\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\4\52"+
            "\1\153\25\52",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\4\52"+
            "\1\154\25\52",
            "",
            "",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\13\52"+
            "\1\157\16\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\4\52"+
            "\1\161\25\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\23\52"+
            "\1\162\6\52",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\15\52"+
            "\1\163\14\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\3\52"+
            "\1\172\26\52",
            "",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\13\52"+
            "\1\173\16\52",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\22\52"+
            "\1\175\7\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\6\52"+
            "\1\176\23\52",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\27\52"+
            "\1\u0080\2\52",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA4_eot = DFA.unpackEncodedString(DFA4_eotS);
    static final short[] DFA4_eof = DFA.unpackEncodedString(DFA4_eofS);
    static final char[] DFA4_min = DFA.unpackEncodedStringToUnsignedChars(DFA4_minS);
    static final char[] DFA4_max = DFA.unpackEncodedStringToUnsignedChars(DFA4_maxS);
    static final short[] DFA4_accept = DFA.unpackEncodedString(DFA4_acceptS);
    static final short[] DFA4_special = DFA.unpackEncodedString(DFA4_specialS);
    static final short[][] DFA4_transition;

    static {
        int numStates = DFA4_transitionS.length;
        DFA4_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA4_transition[i] = DFA.unpackEncodedString(DFA4_transitionS[i]);
        }
    }

    class DFA4 extends DFA {

        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = DFA4_eot;
            this.eof = DFA4_eof;
            this.min = DFA4_min;
            this.max = DFA4_max;
            this.accept = DFA4_accept;
            this.special = DFA4_special;
            this.transition = DFA4_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( NEW | DEL | NOT | USE | CNEW | REM | FORALL | FORALLX | EXISTS | NESTED | INT | REAL | STRING | BOOL | ATTR | PROD | ARG | PAR | TYPE | FLAG | PATH | TRUE | FALSE | T__58 | MINUS | STAR | PLUS | DOT | BAR | HAT | EQUALS | LBRACE | RBRACE | LPAR | RPAR | LSQUARE | RSQUARE | PLING | QUERY | COLON | COMMA | SQUOTE | DQUOTE | DOLLAR | UNDER | BSLASH | IDENT | NUMBER | LABEL );";
        }
    }
 

}