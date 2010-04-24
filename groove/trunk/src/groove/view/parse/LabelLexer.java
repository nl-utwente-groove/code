// $ANTLR 3.1b1 Label.g 2010-04-23 15:26:08

package groove.view.parse;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")      
public class LabelLexer extends Lexer {
    public static final int DOLLAR=31;
    public static final int STAR=42;
    public static final int LSQUARE=47;
    public static final int FORALLX=11;
    public static final int LETTER=53;
    public static final int DQTEXT=38;
    public static final int DEL=5;
    public static final int LBRACE=33;
    public static final int NEW=4;
    public static final int IDENTCHAR=46;
    public static final int DQUOTE=51;
    public static final int EQUALS=29;
    public static final int NOT=6;
    public static final int ATOM=26;
    public static final int EOF=-1;
    public static final int TYPE=22;
    public static final int HAT=48;
    public static final int UNDER=52;
    public static final int PLING=32;
    public static final int LPAR=44;
    public static final int ARG=20;
    public static final int COMMA=49;
    public static final int PATH=24;
    public static final int PROD=19;
    public static final int PAR=21;
    public static final int PLUS=43;
    public static final int DIGIT=54;
    public static final int EXISTS=12;
    public static final int DOT=40;
    public static final int ATTR=18;
    public static final int RBRACE=34;
    public static final int BOOL=17;
    public static final int FORALL=10;
    public static final int INT=14;
    public static final int RSQUARE=50;
    public static final int REM=9;
    public static final int SQUOTE=36;
    public static final int MINUS=41;
    public static final int TRUE=27;
    public static final int CNEW=8;
    public static final int FLAG=23;
    public static final int EMPTY=25;
    public static final int COLON=30;
    public static final int NESTED=13;
    public static final int REAL=15;
    public static final int QUERY=35;
    public static final int RPAR=45;
    public static final int USE=7;
    public static final int FALSE=28;
    public static final int BSLASH=37;
    public static final int BAR=39;
    public static final int STRING=16;

    // delegates
    // delegators

    public LabelLexer() {;} 
    public LabelLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public LabelLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "Label.g"; }

    // $ANTLR start NEW
    public final void mNEW() throws RecognitionException {
        try {
            int _type = NEW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:11:5: ( 'new' )
            // Label.g:11:7: 'new'
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
            // Label.g:12:5: ( 'del' )
            // Label.g:12:7: 'del'
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
            // Label.g:13:5: ( 'not' )
            // Label.g:13:7: 'not'
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
            // Label.g:14:5: ( 'use' )
            // Label.g:14:7: 'use'
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
            // Label.g:15:6: ( 'cnew' )
            // Label.g:15:8: 'cnew'
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
            // Label.g:16:5: ( 'rem' )
            // Label.g:16:7: 'rem'
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
            // Label.g:17:8: ( 'forall' )
            // Label.g:17:10: 'forall'
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
            // Label.g:18:9: ( 'forallx' )
            // Label.g:18:11: 'forallx'
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
            // Label.g:19:8: ( 'exists' )
            // Label.g:19:10: 'exists'
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
            // Label.g:20:8: ( 'nested' )
            // Label.g:20:10: 'nested'
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
            // Label.g:21:5: ( 'int' )
            // Label.g:21:7: 'int'
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
            // Label.g:22:6: ( 'real' )
            // Label.g:22:8: 'real'
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
            // Label.g:23:8: ( 'string' )
            // Label.g:23:10: 'string'
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
            // Label.g:24:6: ( 'bool' )
            // Label.g:24:8: 'bool'
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
            // Label.g:25:6: ( 'attr' )
            // Label.g:25:8: 'attr'
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
            // Label.g:26:6: ( 'prod' )
            // Label.g:26:8: 'prod'
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
            // Label.g:27:5: ( 'arg' )
            // Label.g:27:7: 'arg'
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
            // Label.g:28:5: ( 'par' )
            // Label.g:28:7: 'par'
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
            // Label.g:29:6: ( 'type' )
            // Label.g:29:8: 'type'
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
            // Label.g:30:6: ( 'flag' )
            // Label.g:30:8: 'flag'
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
            // Label.g:31:6: ( 'path' )
            // Label.g:31:8: 'path'
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
            // Label.g:32:6: ( 'true' )
            // Label.g:32:8: 'true'
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
            // Label.g:33:7: ( 'false' )
            // Label.g:33:9: 'false'
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

    // $ANTLR start DQTEXT
    public final void mDQTEXT() throws RecognitionException {
        try {
            // Label.g:144:4: ( DQUOTE (~ ( DQUOTE | BSLASH ) | BSLASH ( BSLASH | DQUOTE ) )* DQUOTE )
            // Label.g:144:6: DQUOTE (~ ( DQUOTE | BSLASH ) | BSLASH ( BSLASH | DQUOTE ) )* DQUOTE
            {
            mDQUOTE(); 
            // Label.g:144:13: (~ ( DQUOTE | BSLASH ) | BSLASH ( BSLASH | DQUOTE ) )*
            loop1:
            do {
                int alt1=3;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='\u0000' && LA1_0<='!')||(LA1_0>='#' && LA1_0<='[')||(LA1_0>=']' && LA1_0<='\uFFFE')) ) {
                    alt1=1;
                }
                else if ( (LA1_0=='\\') ) {
                    alt1=2;
                }


                switch (alt1) {
            	case 1 :
            	    // Label.g:144:14: ~ ( DQUOTE | BSLASH )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;
            	case 2 :
            	    // Label.g:144:33: BSLASH ( BSLASH | DQUOTE )
            	    {
            	    mBSLASH(); 
            	    if ( input.LA(1)=='\"'||input.LA(1)=='\\' ) {
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

            mDQUOTE(); 

            }

        }
        finally {
        }
    }
    // $ANTLR end DQTEXT

    // $ANTLR start MINUS
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:146:8: ( '-' )
            // Label.g:146:10: '-'
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
            // Label.g:147:8: ( '*' )
            // Label.g:147:10: '*'
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
            // Label.g:148:8: ( '+' )
            // Label.g:148:10: '+'
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
            // Label.g:149:8: ( '.' )
            // Label.g:149:10: '.'
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
            // Label.g:150:8: ( '|' )
            // Label.g:150:10: '|'
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
            // Label.g:151:8: ( '^' )
            // Label.g:151:10: '^'
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
            // Label.g:152:8: ( '=' )
            // Label.g:152:10: '='
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
            // Label.g:153:8: ( '{' )
            // Label.g:153:10: '{'
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
            // Label.g:154:8: ( '}' )
            // Label.g:154:10: '}'
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
            // Label.g:155:8: ( '(' )
            // Label.g:155:10: '('
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
            // Label.g:156:8: ( ')' )
            // Label.g:156:10: ')'
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
            // Label.g:157:9: ( '[' )
            // Label.g:157:11: '['
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
            // Label.g:158:9: ( ']' )
            // Label.g:158:11: ']'
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
            // Label.g:159:8: ( '!' )
            // Label.g:159:10: '!'
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
            // Label.g:160:8: ( '?' )
            // Label.g:160:10: '?'
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
            // Label.g:161:8: ( ':' )
            // Label.g:161:10: ':'
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
            // Label.g:162:8: ( ',' )
            // Label.g:162:10: ','
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
            // Label.g:163:8: ( '\\'' )
            // Label.g:163:10: '\\''
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
            // Label.g:164:8: ( '\"' )
            // Label.g:164:10: '\"'
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
            // Label.g:165:8: ( '$' )
            // Label.g:165:10: '$'
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
            // Label.g:166:8: ( '_' )
            // Label.g:166:10: '_'
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
            // Label.g:167:8: ( '\\\\' )
            // Label.g:167:10: '\\\\'
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

    // $ANTLR start IDENTCHAR
    public final void mIDENTCHAR() throws RecognitionException {
        try {
            // Label.g:182:4: ( LETTER | DIGIT | DOLLAR | UNDER )
            // Label.g:
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
            int _type = LETTER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:184:8: ( 'a' .. 'z' | 'A' .. 'Z' )
            // Label.g:
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end LETTER

    // $ANTLR start DIGIT
    public final void mDIGIT() throws RecognitionException {
        try {
            int _type = DIGIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:185:8: ( '0' .. '9' )
            // Label.g:185:10: '0' .. '9'
            {
            matchRange('0','9'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end DIGIT

    public void mTokens() throws RecognitionException {
        // Label.g:1:8: ( NEW | DEL | NOT | USE | CNEW | REM | FORALL | FORALLX | EXISTS | NESTED | INT | REAL | STRING | BOOL | ATTR | PROD | ARG | PAR | TYPE | FLAG | PATH | TRUE | FALSE | MINUS | STAR | PLUS | DOT | BAR | HAT | EQUALS | LBRACE | RBRACE | LPAR | RPAR | LSQUARE | RSQUARE | PLING | QUERY | COLON | COMMA | SQUOTE | DQUOTE | DOLLAR | UNDER | BSLASH | LETTER | DIGIT )
        int alt2=47;
        alt2 = dfa2.predict(input);
        switch (alt2) {
            case 1 :
                // Label.g:1:10: NEW
                {
                mNEW(); 

                }
                break;
            case 2 :
                // Label.g:1:14: DEL
                {
                mDEL(); 

                }
                break;
            case 3 :
                // Label.g:1:18: NOT
                {
                mNOT(); 

                }
                break;
            case 4 :
                // Label.g:1:22: USE
                {
                mUSE(); 

                }
                break;
            case 5 :
                // Label.g:1:26: CNEW
                {
                mCNEW(); 

                }
                break;
            case 6 :
                // Label.g:1:31: REM
                {
                mREM(); 

                }
                break;
            case 7 :
                // Label.g:1:35: FORALL
                {
                mFORALL(); 

                }
                break;
            case 8 :
                // Label.g:1:42: FORALLX
                {
                mFORALLX(); 

                }
                break;
            case 9 :
                // Label.g:1:50: EXISTS
                {
                mEXISTS(); 

                }
                break;
            case 10 :
                // Label.g:1:57: NESTED
                {
                mNESTED(); 

                }
                break;
            case 11 :
                // Label.g:1:64: INT
                {
                mINT(); 

                }
                break;
            case 12 :
                // Label.g:1:68: REAL
                {
                mREAL(); 

                }
                break;
            case 13 :
                // Label.g:1:73: STRING
                {
                mSTRING(); 

                }
                break;
            case 14 :
                // Label.g:1:80: BOOL
                {
                mBOOL(); 

                }
                break;
            case 15 :
                // Label.g:1:85: ATTR
                {
                mATTR(); 

                }
                break;
            case 16 :
                // Label.g:1:90: PROD
                {
                mPROD(); 

                }
                break;
            case 17 :
                // Label.g:1:95: ARG
                {
                mARG(); 

                }
                break;
            case 18 :
                // Label.g:1:99: PAR
                {
                mPAR(); 

                }
                break;
            case 19 :
                // Label.g:1:103: TYPE
                {
                mTYPE(); 

                }
                break;
            case 20 :
                // Label.g:1:108: FLAG
                {
                mFLAG(); 

                }
                break;
            case 21 :
                // Label.g:1:113: PATH
                {
                mPATH(); 

                }
                break;
            case 22 :
                // Label.g:1:118: TRUE
                {
                mTRUE(); 

                }
                break;
            case 23 :
                // Label.g:1:123: FALSE
                {
                mFALSE(); 

                }
                break;
            case 24 :
                // Label.g:1:129: MINUS
                {
                mMINUS(); 

                }
                break;
            case 25 :
                // Label.g:1:135: STAR
                {
                mSTAR(); 

                }
                break;
            case 26 :
                // Label.g:1:140: PLUS
                {
                mPLUS(); 

                }
                break;
            case 27 :
                // Label.g:1:145: DOT
                {
                mDOT(); 

                }
                break;
            case 28 :
                // Label.g:1:149: BAR
                {
                mBAR(); 

                }
                break;
            case 29 :
                // Label.g:1:153: HAT
                {
                mHAT(); 

                }
                break;
            case 30 :
                // Label.g:1:157: EQUALS
                {
                mEQUALS(); 

                }
                break;
            case 31 :
                // Label.g:1:164: LBRACE
                {
                mLBRACE(); 

                }
                break;
            case 32 :
                // Label.g:1:171: RBRACE
                {
                mRBRACE(); 

                }
                break;
            case 33 :
                // Label.g:1:178: LPAR
                {
                mLPAR(); 

                }
                break;
            case 34 :
                // Label.g:1:183: RPAR
                {
                mRPAR(); 

                }
                break;
            case 35 :
                // Label.g:1:188: LSQUARE
                {
                mLSQUARE(); 

                }
                break;
            case 36 :
                // Label.g:1:196: RSQUARE
                {
                mRSQUARE(); 

                }
                break;
            case 37 :
                // Label.g:1:204: PLING
                {
                mPLING(); 

                }
                break;
            case 38 :
                // Label.g:1:210: QUERY
                {
                mQUERY(); 

                }
                break;
            case 39 :
                // Label.g:1:216: COLON
                {
                mCOLON(); 

                }
                break;
            case 40 :
                // Label.g:1:222: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 41 :
                // Label.g:1:228: SQUOTE
                {
                mSQUOTE(); 

                }
                break;
            case 42 :
                // Label.g:1:235: DQUOTE
                {
                mDQUOTE(); 

                }
                break;
            case 43 :
                // Label.g:1:242: DOLLAR
                {
                mDOLLAR(); 

                }
                break;
            case 44 :
                // Label.g:1:249: UNDER
                {
                mUNDER(); 

                }
                break;
            case 45 :
                // Label.g:1:255: BSLASH
                {
                mBSLASH(); 

                }
                break;
            case 46 :
                // Label.g:1:262: LETTER
                {
                mLETTER(); 

                }
                break;
            case 47 :
                // Label.g:1:269: DIGIT
                {
                mDIGIT(); 

                }
                break;

        }

    }


    protected DFA2 dfa2 = new DFA2(this);
    static final String DFA2_eotS =
        "\1\uffff\15\44\64\uffff\1\104\2\uffff";
    static final String DFA2_eofS =
        "\105\uffff";
    static final String DFA2_minS =
        "\1\41\2\145\1\163\1\156\1\145\1\141\1\170\1\156\1\164\1\157\1\162"+
        "\1\141\1\162\30\uffff\1\163\4\uffff\1\141\1\162\11\uffff\1\162\6"+
        "\uffff\1\141\2\uffff\2\154\1\170\2\uffff";
    static final String DFA2_maxS =
        "\1\175\1\157\1\145\1\163\1\156\1\145\1\157\1\170\1\156\1\164\1\157"+
        "\1\164\1\162\1\171\30\uffff\1\167\4\uffff\1\155\1\162\11\uffff\1"+
        "\164\6\uffff\1\141\2\uffff\2\154\1\170\2\uffff";
    static final String DFA2_acceptS =
        "\16\uffff\1\30\1\31\1\32\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\42"+
        "\1\43\1\44\1\45\1\46\1\47\1\50\1\51\1\52\1\53\1\54\1\55\1\56\1\57"+
        "\1\uffff\1\3\1\2\1\4\1\5\2\uffff\1\24\1\27\1\11\1\13\1\15\1\16\1"+
        "\17\1\21\1\20\1\uffff\1\23\1\26\1\1\1\12\1\6\1\14\1\uffff\1\22\1"+
        "\25\3\uffff\1\10\1\7";
    static final String DFA2_specialS =
        "\105\uffff}>";
    static final String[] DFA2_transitionS = {
            "\1\33\1\40\1\uffff\1\41\2\uffff\1\37\1\27\1\30\1\17\1\20\1\36"+
            "\1\16\1\21\1\uffff\12\45\1\35\2\uffff\1\24\1\uffff\1\34\1\uffff"+
            "\32\44\1\31\1\43\1\32\1\23\1\42\1\uffff\1\13\1\12\1\4\1\2\1"+
            "\7\1\6\2\44\1\10\4\44\1\1\1\44\1\14\1\44\1\5\1\11\1\15\1\3\5"+
            "\44\1\25\1\22\1\26",
            "\1\46\11\uffff\1\47",
            "\1\50",
            "\1\51",
            "\1\52",
            "\1\53",
            "\1\56\12\uffff\1\55\2\uffff\1\54",
            "\1\57",
            "\1\60",
            "\1\61",
            "\1\62",
            "\1\64\1\uffff\1\63",
            "\1\66\20\uffff\1\65",
            "\1\70\6\uffff\1\67",
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
            "",
            "",
            "",
            "",
            "\1\72\3\uffff\1\71",
            "",
            "",
            "",
            "",
            "\1\74\13\uffff\1\73",
            "\1\75",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\76\1\uffff\1\77",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\100",
            "",
            "",
            "\1\101",
            "\1\102",
            "\1\103",
            "",
            ""
    };

    static final short[] DFA2_eot = DFA.unpackEncodedString(DFA2_eotS);
    static final short[] DFA2_eof = DFA.unpackEncodedString(DFA2_eofS);
    static final char[] DFA2_min = DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
    static final char[] DFA2_max = DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
    static final short[] DFA2_accept = DFA.unpackEncodedString(DFA2_acceptS);
    static final short[] DFA2_special = DFA.unpackEncodedString(DFA2_specialS);
    static final short[][] DFA2_transition;

    static {
        int numStates = DFA2_transitionS.length;
        DFA2_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA2_transition[i] = DFA.unpackEncodedString(DFA2_transitionS[i]);
        }
    }

    class DFA2 extends DFA {

        public DFA2(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 2;
            this.eot = DFA2_eot;
            this.eof = DFA2_eof;
            this.min = DFA2_min;
            this.max = DFA2_max;
            this.accept = DFA2_accept;
            this.special = DFA2_special;
            this.transition = DFA2_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( NEW | DEL | NOT | USE | CNEW | REM | FORALL | FORALLX | EXISTS | NESTED | INT | REAL | STRING | BOOL | ATTR | PROD | ARG | PAR | TYPE | FLAG | PATH | TRUE | FALSE | MINUS | STAR | PLUS | DOT | BAR | HAT | EQUALS | LBRACE | RBRACE | LPAR | RPAR | LSQUARE | RSQUARE | PLING | QUERY | COLON | COMMA | SQUOTE | DQUOTE | DOLLAR | UNDER | BSLASH | LETTER | DIGIT );";
        }
    }
 

}