// $ANTLR 3.1b1 Label.g 2010-04-23 10:01:16

package groove.view.parse;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")      
public class LabelLexer extends Lexer {
    public static final int DOLLAR=53;
    public static final int T__68=68;
    public static final int T__69=69;
    public static final int T__66=66;
    public static final int T__67=67;
    public static final int T__64=64;
    public static final int STAR=41;
    public static final int FORALLX=11;
    public static final int T__65=65;
    public static final int LSQUARE=46;
    public static final int T__62=62;
    public static final int T__63=63;
    public static final int LETTER=55;
    public static final int DEL=5;
    public static final int DQTEXT=31;
    public static final int LBRACE=34;
    public static final int NEW=4;
    public static final int DQUOTE=52;
    public static final int IDENTCHAR=43;
    public static final int RNUMBER=30;
    public static final int EQUALS=26;
    public static final int NOT=6;
    public static final int T__61=61;
    public static final int EOF=-1;
    public static final int T__60=60;
    public static final int TYPE=22;
    public static final int HAT=47;
    public static final int T__57=57;
    public static final int UNDER=54;
    public static final int T__58=58;
    public static final int PLING=32;
    public static final int LPAR=44;
    public static final int ARG=20;
    public static final int COMMA=48;
    public static final int PATH=24;
    public static final int T__59=59;
    public static final int PROD=19;
    public static final int IDENT=27;
    public static final int PAR=21;
    public static final int PLUS=42;
    public static final int EXISTS=12;
    public static final int DIGIT=33;
    public static final int DOT=39;
    public static final int ATTR=18;
    public static final int RBRACE=35;
    public static final int BOOL=17;
    public static final int NUMBER=29;
    public static final int FORALL=10;
    public static final int INT=14;
    public static final int REM=9;
    public static final int MINUS=40;
    public static final int RSQUARE=49;
    public static final int SQUOTE=50;
    public static final int CNEW=8;
    public static final int FLAG=23;
    public static final int EMPTY=25;
    public static final int NESTED=13;
    public static final int COLON=28;
    public static final int REAL=15;
    public static final int T__71=71;
    public static final int T__72=72;
    public static final int SQTEXT=36;
    public static final int T__70=70;
    public static final int LABEL=56;
    public static final int QUERY=37;
    public static final int RPAR=45;
    public static final int USE=7;
    public static final int T__76=76;
    public static final int T__75=75;
    public static final int T__74=74;
    public static final int T__73=73;
    public static final int BSLASH=51;
    public static final int BAR=38;
    public static final int T__79=79;
    public static final int STRING=16;
    public static final int T__78=78;
    public static final int T__77=77;

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

    // $ANTLR start T__57
    public final void mT__57() throws RecognitionException {
        try {
            int _type = T__57;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:11:7: ( 'new' )
            // Label.g:11:9: 'new'
            {
            match("new"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__57

    // $ANTLR start T__58
    public final void mT__58() throws RecognitionException {
        try {
            int _type = T__58;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:12:7: ( 'del' )
            // Label.g:12:9: 'del'
            {
            match("del"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__58

    // $ANTLR start T__59
    public final void mT__59() throws RecognitionException {
        try {
            int _type = T__59;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:13:7: ( 'cnew' )
            // Label.g:13:9: 'cnew'
            {
            match("cnew"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__59

    // $ANTLR start T__60
    public final void mT__60() throws RecognitionException {
        try {
            int _type = T__60;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:14:7: ( 'not' )
            // Label.g:14:9: 'not'
            {
            match("not"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__60

    // $ANTLR start T__61
    public final void mT__61() throws RecognitionException {
        try {
            int _type = T__61;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:15:7: ( 'use' )
            // Label.g:15:9: 'use'
            {
            match("use"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__61

    // $ANTLR start T__62
    public final void mT__62() throws RecognitionException {
        try {
            int _type = T__62;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:16:7: ( 'rem' )
            // Label.g:16:9: 'rem'
            {
            match("rem"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__62

    // $ANTLR start T__63
    public final void mT__63() throws RecognitionException {
        try {
            int _type = T__63;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:17:7: ( 'forall' )
            // Label.g:17:9: 'forall'
            {
            match("forall"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__63

    // $ANTLR start T__64
    public final void mT__64() throws RecognitionException {
        try {
            int _type = T__64;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:18:7: ( 'forallx' )
            // Label.g:18:9: 'forallx'
            {
            match("forallx"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__64

    // $ANTLR start T__65
    public final void mT__65() throws RecognitionException {
        try {
            int _type = T__65;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:19:7: ( 'exists' )
            // Label.g:19:9: 'exists'
            {
            match("exists"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__65

    // $ANTLR start T__66
    public final void mT__66() throws RecognitionException {
        try {
            int _type = T__66;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:20:7: ( 'nested' )
            // Label.g:20:9: 'nested'
            {
            match("nested"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__66

    // $ANTLR start T__67
    public final void mT__67() throws RecognitionException {
        try {
            int _type = T__67;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:21:7: ( 'par' )
            // Label.g:21:9: 'par'
            {
            match("par"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__67

    // $ANTLR start T__68
    public final void mT__68() throws RecognitionException {
        try {
            int _type = T__68;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:22:7: ( 'attr' )
            // Label.g:22:9: 'attr'
            {
            match("attr"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__68

    // $ANTLR start T__69
    public final void mT__69() throws RecognitionException {
        try {
            int _type = T__69;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:23:7: ( 'prod' )
            // Label.g:23:9: 'prod'
            {
            match("prod"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__69

    // $ANTLR start T__70
    public final void mT__70() throws RecognitionException {
        try {
            int _type = T__70;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:24:7: ( 'arg' )
            // Label.g:24:9: 'arg'
            {
            match("arg"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__70

    // $ANTLR start T__71
    public final void mT__71() throws RecognitionException {
        try {
            int _type = T__71;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:25:7: ( 'int' )
            // Label.g:25:9: 'int'
            {
            match("int"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__71

    // $ANTLR start T__72
    public final void mT__72() throws RecognitionException {
        try {
            int _type = T__72;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:26:7: ( 'real' )
            // Label.g:26:9: 'real'
            {
            match("real"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__72

    // $ANTLR start T__73
    public final void mT__73() throws RecognitionException {
        try {
            int _type = T__73;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:27:7: ( 'string' )
            // Label.g:27:9: 'string'
            {
            match("string"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__73

    // $ANTLR start T__74
    public final void mT__74() throws RecognitionException {
        try {
            int _type = T__74;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:28:7: ( 'bool' )
            // Label.g:28:9: 'bool'
            {
            match("bool"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__74

    // $ANTLR start T__75
    public final void mT__75() throws RecognitionException {
        try {
            int _type = T__75;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:29:7: ( 'type' )
            // Label.g:29:9: 'type'
            {
            match("type"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__75

    // $ANTLR start T__76
    public final void mT__76() throws RecognitionException {
        try {
            int _type = T__76;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:30:7: ( 'flag' )
            // Label.g:30:9: 'flag'
            {
            match("flag"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__76

    // $ANTLR start T__77
    public final void mT__77() throws RecognitionException {
        try {
            int _type = T__77;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:31:7: ( 'path' )
            // Label.g:31:9: 'path'
            {
            match("path"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__77

    // $ANTLR start T__78
    public final void mT__78() throws RecognitionException {
        try {
            int _type = T__78;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:32:7: ( 'true' )
            // Label.g:32:9: 'true'
            {
            match("true"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end T__78

    // $ANTLR start T__79
    public final void mT__79() throws RecognitionException {
        try {
            int _type = T__79;
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
    // $ANTLR end T__79

    // $ANTLR start SQTEXT
    public final void mSQTEXT() throws RecognitionException {
        try {
            int _type = SQTEXT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:140:4: ( SQUOTE (~ ( SQUOTE | BSLASH ) | BSLASH ( BSLASH | SQUOTE ) )* SQUOTE )
            // Label.g:140:6: SQUOTE (~ ( SQUOTE | BSLASH ) | BSLASH ( BSLASH | SQUOTE ) )* SQUOTE
            {
            mSQUOTE(); 
            // Label.g:140:13: (~ ( SQUOTE | BSLASH ) | BSLASH ( BSLASH | SQUOTE ) )*
            loop1:
            do {
                int alt1=3;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='\u0000' && LA1_0<='&')||(LA1_0>='(' && LA1_0<='[')||(LA1_0>=']' && LA1_0<='\uFFFE')) ) {
                    alt1=1;
                }
                else if ( (LA1_0=='\\') ) {
                    alt1=2;
                }


                switch (alt1) {
            	case 1 :
            	    // Label.g:140:14: ~ ( SQUOTE | BSLASH )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;
            	case 2 :
            	    // Label.g:140:33: BSLASH ( BSLASH | SQUOTE )
            	    {
            	    mBSLASH(); 
            	    if ( input.LA(1)=='\''||input.LA(1)=='\\' ) {
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

            mSQUOTE(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end SQTEXT

    // $ANTLR start DQTEXT
    public final void mDQTEXT() throws RecognitionException {
        try {
            int _type = DQTEXT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:143:4: ( DQUOTE (~ ( DQUOTE | BSLASH ) | BSLASH ( BSLASH | DQUOTE ) )* DQUOTE )
            // Label.g:143:6: DQUOTE (~ ( DQUOTE | BSLASH ) | BSLASH ( BSLASH | DQUOTE ) )* DQUOTE
            {
            mDQUOTE(); 
            // Label.g:143:13: (~ ( DQUOTE | BSLASH ) | BSLASH ( BSLASH | DQUOTE ) )*
            loop2:
            do {
                int alt2=3;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='\u0000' && LA2_0<='!')||(LA2_0>='#' && LA2_0<='[')||(LA2_0>=']' && LA2_0<='\uFFFE')) ) {
                    alt2=1;
                }
                else if ( (LA2_0=='\\') ) {
                    alt2=2;
                }


                switch (alt2) {
            	case 1 :
            	    // Label.g:143:14: ~ ( DQUOTE | BSLASH )
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
            	    // Label.g:143:33: BSLASH ( BSLASH | DQUOTE )
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
            	    break loop2;
                }
            } while (true);

            mDQUOTE(); 

            }

            state.type = _type;
            state.channel = _channel;
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
            // Label.g:174:8: ( '-' )
            // Label.g:174:10: '-'
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
            // Label.g:175:8: ( '*' )
            // Label.g:175:10: '*'
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
            // Label.g:176:8: ( '+' )
            // Label.g:176:10: '+'
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
            // Label.g:177:8: ( '.' )
            // Label.g:177:10: '.'
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
            // Label.g:178:8: ( '|' )
            // Label.g:178:10: '|'
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
            // Label.g:179:8: ( '^' )
            // Label.g:179:10: '^'
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
            // Label.g:180:8: ( '=' )
            // Label.g:180:10: '='
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
            // Label.g:181:8: ( '{' )
            // Label.g:181:10: '{'
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
            // Label.g:182:8: ( '}' )
            // Label.g:182:10: '}'
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
            // Label.g:183:8: ( '(' )
            // Label.g:183:10: '('
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
            // Label.g:184:8: ( ')' )
            // Label.g:184:10: ')'
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
            // Label.g:185:9: ( '[' )
            // Label.g:185:11: '['
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
            // Label.g:186:9: ( ']' )
            // Label.g:186:11: ']'
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
            // Label.g:187:8: ( '!' )
            // Label.g:187:10: '!'
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
            // Label.g:188:8: ( '?' )
            // Label.g:188:10: '?'
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
            // Label.g:189:8: ( ':' )
            // Label.g:189:10: ':'
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
            // Label.g:190:8: ( ',' )
            // Label.g:190:10: ','
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
            // Label.g:191:8: ( '\\'' )
            // Label.g:191:10: '\\''
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
            // Label.g:192:8: ( '\"' )
            // Label.g:192:10: '\"'
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
            // Label.g:193:8: ( '$' )
            // Label.g:193:10: '$'
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
            // Label.g:194:8: ( '_' )
            // Label.g:194:10: '_'
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
            // Label.g:195:8: ( '\\\\' )
            // Label.g:195:10: '\\\\'
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
            // Label.g:198:4: ( LETTER ( IDENTCHAR )* )
            // Label.g:198:6: LETTER ( IDENTCHAR )*
            {
            mLETTER(); 
            // Label.g:198:13: ( IDENTCHAR )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0=='$'||(LA3_0>='0' && LA3_0<='9')||(LA3_0>='A' && LA3_0<='Z')||LA3_0=='_'||(LA3_0>='a' && LA3_0<='z')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // Label.g:198:13: IDENTCHAR
            	    {
            	    mIDENTCHAR(); 

            	    }
            	    break;

            	default :
            	    break loop3;
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
            // Label.g:202:4: ( ( DIGIT )+ )
            // Label.g:202:6: ( DIGIT )+
            {
            // Label.g:202:6: ( DIGIT )+
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
            	    // Label.g:202:6: DIGIT
            	    {
            	    mDIGIT(); 

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


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end NUMBER

    // $ANTLR start RNUMBER
    public final void mRNUMBER() throws RecognitionException {
        try {
            int _type = RNUMBER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:206:4: ( ( ( DIGIT )+ ( DOT ( DIGIT )* )? | DOT ( DIGIT )+ ) )
            // Label.g:206:6: ( ( DIGIT )+ ( DOT ( DIGIT )* )? | DOT ( DIGIT )+ )
            {
            // Label.g:206:6: ( ( DIGIT )+ ( DOT ( DIGIT )* )? | DOT ( DIGIT )+ )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( ((LA9_0>='0' && LA9_0<='9')) ) {
                alt9=1;
            }
            else if ( (LA9_0=='.') ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // Label.g:206:7: ( DIGIT )+ ( DOT ( DIGIT )* )?
                    {
                    // Label.g:206:7: ( DIGIT )+
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
                    	    // Label.g:206:7: DIGIT
                    	    {
                    	    mDIGIT(); 

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

                    // Label.g:206:14: ( DOT ( DIGIT )* )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0=='.') ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // Label.g:206:15: DOT ( DIGIT )*
                            {
                            mDOT(); 
                            // Label.g:206:19: ( DIGIT )*
                            loop6:
                            do {
                                int alt6=2;
                                int LA6_0 = input.LA(1);

                                if ( ((LA6_0>='0' && LA6_0<='9')) ) {
                                    alt6=1;
                                }


                                switch (alt6) {
                            	case 1 :
                            	    // Label.g:206:19: DIGIT
                            	    {
                            	    mDIGIT(); 

                            	    }
                            	    break;

                            	default :
                            	    break loop6;
                                }
                            } while (true);


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // Label.g:206:30: DOT ( DIGIT )+
                    {
                    mDOT(); 
                    // Label.g:206:34: ( DIGIT )+
                    int cnt8=0;
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( ((LA8_0>='0' && LA8_0<='9')) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // Label.g:206:34: DIGIT
                    	    {
                    	    mDIGIT(); 

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


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end RNUMBER

    // $ANTLR start LABEL
    public final void mLABEL() throws RecognitionException {
        try {
            int _type = LABEL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Label.g:210:4: ( ( IDENTCHAR )* )
            // Label.g:210:6: ( IDENTCHAR )*
            {
            // Label.g:210:6: ( IDENTCHAR )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0=='$'||(LA10_0>='0' && LA10_0<='9')||(LA10_0>='A' && LA10_0<='Z')||LA10_0=='_'||(LA10_0>='a' && LA10_0<='z')) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // Label.g:210:6: IDENTCHAR
            	    {
            	    mIDENTCHAR(); 

            	    }
            	    break;

            	default :
            	    break loop10;
                }
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
            // Label.g:214:4: ( LETTER | DIGIT | DOLLAR | UNDER )
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
            // Label.g:216:17: ( 'a' .. 'z' | 'A' .. 'Z' )
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

        }
        finally {
        }
    }
    // $ANTLR end LETTER

    // $ANTLR start DIGIT
    public final void mDIGIT() throws RecognitionException {
        try {
            // Label.g:217:17: ( '0' .. '9' )
            // Label.g:217:19: '0' .. '9'
            {
            matchRange('0','9'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end DIGIT

    public void mTokens() throws RecognitionException {
        // Label.g:1:8: ( T__57 | T__58 | T__59 | T__60 | T__61 | T__62 | T__63 | T__64 | T__65 | T__66 | T__67 | T__68 | T__69 | T__70 | T__71 | T__72 | T__73 | T__74 | T__75 | T__76 | T__77 | T__78 | T__79 | SQTEXT | DQTEXT | MINUS | STAR | PLUS | DOT | BAR | HAT | EQUALS | LBRACE | RBRACE | LPAR | RPAR | LSQUARE | RSQUARE | PLING | QUERY | COLON | COMMA | SQUOTE | DQUOTE | DOLLAR | UNDER | BSLASH | IDENT | NUMBER | RNUMBER | LABEL )
        int alt11=51;
        alt11 = dfa11.predict(input);
        switch (alt11) {
            case 1 :
                // Label.g:1:10: T__57
                {
                mT__57(); 

                }
                break;
            case 2 :
                // Label.g:1:16: T__58
                {
                mT__58(); 

                }
                break;
            case 3 :
                // Label.g:1:22: T__59
                {
                mT__59(); 

                }
                break;
            case 4 :
                // Label.g:1:28: T__60
                {
                mT__60(); 

                }
                break;
            case 5 :
                // Label.g:1:34: T__61
                {
                mT__61(); 

                }
                break;
            case 6 :
                // Label.g:1:40: T__62
                {
                mT__62(); 

                }
                break;
            case 7 :
                // Label.g:1:46: T__63
                {
                mT__63(); 

                }
                break;
            case 8 :
                // Label.g:1:52: T__64
                {
                mT__64(); 

                }
                break;
            case 9 :
                // Label.g:1:58: T__65
                {
                mT__65(); 

                }
                break;
            case 10 :
                // Label.g:1:64: T__66
                {
                mT__66(); 

                }
                break;
            case 11 :
                // Label.g:1:70: T__67
                {
                mT__67(); 

                }
                break;
            case 12 :
                // Label.g:1:76: T__68
                {
                mT__68(); 

                }
                break;
            case 13 :
                // Label.g:1:82: T__69
                {
                mT__69(); 

                }
                break;
            case 14 :
                // Label.g:1:88: T__70
                {
                mT__70(); 

                }
                break;
            case 15 :
                // Label.g:1:94: T__71
                {
                mT__71(); 

                }
                break;
            case 16 :
                // Label.g:1:100: T__72
                {
                mT__72(); 

                }
                break;
            case 17 :
                // Label.g:1:106: T__73
                {
                mT__73(); 

                }
                break;
            case 18 :
                // Label.g:1:112: T__74
                {
                mT__74(); 

                }
                break;
            case 19 :
                // Label.g:1:118: T__75
                {
                mT__75(); 

                }
                break;
            case 20 :
                // Label.g:1:124: T__76
                {
                mT__76(); 

                }
                break;
            case 21 :
                // Label.g:1:130: T__77
                {
                mT__77(); 

                }
                break;
            case 22 :
                // Label.g:1:136: T__78
                {
                mT__78(); 

                }
                break;
            case 23 :
                // Label.g:1:142: T__79
                {
                mT__79(); 

                }
                break;
            case 24 :
                // Label.g:1:148: SQTEXT
                {
                mSQTEXT(); 

                }
                break;
            case 25 :
                // Label.g:1:155: DQTEXT
                {
                mDQTEXT(); 

                }
                break;
            case 26 :
                // Label.g:1:162: MINUS
                {
                mMINUS(); 

                }
                break;
            case 27 :
                // Label.g:1:168: STAR
                {
                mSTAR(); 

                }
                break;
            case 28 :
                // Label.g:1:173: PLUS
                {
                mPLUS(); 

                }
                break;
            case 29 :
                // Label.g:1:178: DOT
                {
                mDOT(); 

                }
                break;
            case 30 :
                // Label.g:1:182: BAR
                {
                mBAR(); 

                }
                break;
            case 31 :
                // Label.g:1:186: HAT
                {
                mHAT(); 

                }
                break;
            case 32 :
                // Label.g:1:190: EQUALS
                {
                mEQUALS(); 

                }
                break;
            case 33 :
                // Label.g:1:197: LBRACE
                {
                mLBRACE(); 

                }
                break;
            case 34 :
                // Label.g:1:204: RBRACE
                {
                mRBRACE(); 

                }
                break;
            case 35 :
                // Label.g:1:211: LPAR
                {
                mLPAR(); 

                }
                break;
            case 36 :
                // Label.g:1:216: RPAR
                {
                mRPAR(); 

                }
                break;
            case 37 :
                // Label.g:1:221: LSQUARE
                {
                mLSQUARE(); 

                }
                break;
            case 38 :
                // Label.g:1:229: RSQUARE
                {
                mRSQUARE(); 

                }
                break;
            case 39 :
                // Label.g:1:237: PLING
                {
                mPLING(); 

                }
                break;
            case 40 :
                // Label.g:1:243: QUERY
                {
                mQUERY(); 

                }
                break;
            case 41 :
                // Label.g:1:249: COLON
                {
                mCOLON(); 

                }
                break;
            case 42 :
                // Label.g:1:255: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 43 :
                // Label.g:1:261: SQUOTE
                {
                mSQUOTE(); 

                }
                break;
            case 44 :
                // Label.g:1:268: DQUOTE
                {
                mDQUOTE(); 

                }
                break;
            case 45 :
                // Label.g:1:275: DOLLAR
                {
                mDOLLAR(); 

                }
                break;
            case 46 :
                // Label.g:1:282: UNDER
                {
                mUNDER(); 

                }
                break;
            case 47 :
                // Label.g:1:288: BSLASH
                {
                mBSLASH(); 

                }
                break;
            case 48 :
                // Label.g:1:295: IDENT
                {
                mIDENT(); 

                }
                break;
            case 49 :
                // Label.g:1:301: NUMBER
                {
                mNUMBER(); 

                }
                break;
            case 50 :
                // Label.g:1:308: RNUMBER
                {
                mRNUMBER(); 

                }
                break;
            case 51 :
                // Label.g:1:316: LABEL
                {
                mLABEL(); 

                }
                break;

        }

    }


    protected DFA11 dfa11 = new DFA11(this);
    static final String DFA11_eotS =
        "\1\46\15\51\1\74\1\76\3\uffff\1\100\15\uffff\1\102\1\103\1\uffff"+
        "\1\51\1\104\1\uffff\2\51\1\uffff\22\51\11\uffff\1\133\1\51\1\135"+
        "\1\136\1\51\1\140\1\141\5\51\1\147\3\51\1\153\1\154\4\51\1\uffff"+
        "\1\51\2\uffff\1\162\2\uffff\1\163\1\51\1\165\2\51\1\uffff\1\170"+
        "\1\171\1\172\2\uffff\1\51\1\174\1\175\1\176\1\51\2\uffff\1\51\1"+
        "\uffff\1\u0081\1\51\3\uffff\1\51\3\uffff\1\u0084\1\u0086\1\uffff"+
        "\1\u0087\1\u0088\1\uffff\1\u0089\4\uffff";
    static final String DFA11_eofS =
        "\u008a\uffff";
    static final String DFA11_minS =
        "\1\41\15\44\2\0\3\uffff\1\60\15\uffff\2\44\1\uffff\2\44\1\uffff"+
        "\2\44\1\uffff\22\44\11\uffff\26\44\1\uffff\1\44\2\uffff\1\44\2\uffff"+
        "\5\44\1\uffff\3\44\2\uffff\5\44\2\uffff\1\44\1\uffff\2\44\3\uffff"+
        "\1\44\3\uffff\2\44\1\uffff\2\44\1\uffff\1\44\4\uffff";
    static final String DFA11_maxS =
        "\1\175\15\172\2\ufffe\3\uffff\1\71\15\uffff\2\172\1\uffff\2\172"+
        "\1\uffff\2\172\1\uffff\22\172\11\uffff\26\172\1\uffff\1\172\2\uffff"+
        "\1\172\2\uffff\5\172\1\uffff\3\172\2\uffff\5\172\2\uffff\1\172\1"+
        "\uffff\2\172\3\uffff\1\172\3\uffff\2\172\1\uffff\2\172\1\uffff\1"+
        "\172\4\uffff";
    static final String DFA11_acceptS =
        "\20\uffff\1\32\1\33\1\34\1\uffff\1\36\1\37\1\40\1\41\1\42\1\43\1"+
        "\44\1\45\1\46\1\47\1\50\1\51\1\52\2\uffff\1\57\2\uffff\1\63\2\uffff"+
        "\1\60\22\uffff\1\53\1\30\1\54\1\31\1\35\1\62\1\55\1\56\1\61\26\uffff"+
        "\1\1\1\uffff\1\4\1\2\1\uffff\1\5\1\6\5\uffff\1\13\3\uffff\1\16\1"+
        "\17\5\uffff\1\3\1\20\1\uffff\1\24\2\uffff\1\25\1\15\1\14\1\uffff"+
        "\1\22\1\23\1\26\2\uffff\1\27\2\uffff\1\12\1\uffff\1\7\1\11\1\21"+
        "\1\10";
    static final String DFA11_specialS =
        "\u008a\uffff}>";
    static final String[] DFA11_transitionS = {
            "\1\35\1\17\1\uffff\1\41\2\uffff\1\16\1\31\1\32\1\21\1\22\1\40"+
            "\1\20\1\23\1\uffff\12\45\1\37\2\uffff\1\26\1\uffff\1\36\1\uffff"+
            "\32\44\1\33\1\43\1\34\1\25\1\42\1\uffff\1\11\1\14\1\3\1\2\1"+
            "\7\1\6\2\44\1\12\4\44\1\1\1\44\1\10\1\44\1\5\1\13\1\15\1\4\5"+
            "\44\1\27\1\24\1\30",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\4\52"+
            "\1\47\11\52\1\50\13\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\4\52"+
            "\1\53\25\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\15\52"+
            "\1\54\14\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\22\52"+
            "\1\55\7\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\4\52"+
            "\1\56\25\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\1\61"+
            "\12\52\1\60\2\52\1\57\13\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\27\52"+
            "\1\62\2\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\1\63"+
            "\20\52\1\64\10\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\21\52"+
            "\1\66\1\52\1\65\6\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\15\52"+
            "\1\67\14\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\23\52"+
            "\1\70\6\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\16\52"+
            "\1\71\13\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\21\52"+
            "\1\73\6\52\1\72\1\52",
            "\uffff\75",
            "\uffff\77",
            "",
            "",
            "",
            "\12\101",
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
            "\1\46\13\uffff\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "\1\46\13\uffff\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\46\11\uffff\1\101\1\uffff\12\45\7\uffff\32\46\4\uffff\1\46"+
            "\1\uffff\32\46",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\22\52"+
            "\1\106\3\52\1\105\3\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\23\52"+
            "\1\107\6\52",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\13\52"+
            "\1\110\16\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\4\52"+
            "\1\111\25\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\4\52"+
            "\1\112\25\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\1\114"+
            "\13\52\1\113\15\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\21\52"+
            "\1\115\10\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\1\116"+
            "\31\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\13\52"+
            "\1\117\16\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\10\52"+
            "\1\120\21\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\21\52"+
            "\1\121\1\52\1\122\6\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\16\52"+
            "\1\123\13\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\23\52"+
            "\1\124\6\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\6\52"+
            "\1\125\23\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\23\52"+
            "\1\126\6\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\21\52"+
            "\1\127\10\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\16\52"+
            "\1\130\13\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\17\52"+
            "\1\131\12\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\24\52"+
            "\1\132\5\52",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\23\52"+
            "\1\134\6\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\26\52"+
            "\1\137\3\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\13\52"+
            "\1\142\16\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\1\143"+
            "\31\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\6\52"+
            "\1\144\23\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\22\52"+
            "\1\145\7\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\22\52"+
            "\1\146\7\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\7\52"+
            "\1\150\22\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\3\52"+
            "\1\151\26\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\21\52"+
            "\1\152\10\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\10\52"+
            "\1\155\21\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\13\52"+
            "\1\156\16\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\4\52"+
            "\1\157\25\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\4\52"+
            "\1\160\25\52",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\4\52"+
            "\1\161\25\52",
            "",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\13\52"+
            "\1\164\16\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\4\52"+
            "\1\166\25\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\23\52"+
            "\1\167\6\52",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\15\52"+
            "\1\173\14\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\3\52"+
            "\1\177\26\52",
            "",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\13\52"+
            "\1\u0080\16\52",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\22\52"+
            "\1\u0082\7\52",
            "",
            "",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\6\52"+
            "\1\u0083\23\52",
            "",
            "",
            "",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52",
            "\1\52\13\uffff\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\27\52"+
            "\1\u0085\2\52",
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

    static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
    static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
    static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
    static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
    static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
    static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
    static final short[][] DFA11_transition;

    static {
        int numStates = DFA11_transitionS.length;
        DFA11_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
        }
    }

    class DFA11 extends DFA {

        public DFA11(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 11;
            this.eot = DFA11_eot;
            this.eof = DFA11_eof;
            this.min = DFA11_min;
            this.max = DFA11_max;
            this.accept = DFA11_accept;
            this.special = DFA11_special;
            this.transition = DFA11_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__57 | T__58 | T__59 | T__60 | T__61 | T__62 | T__63 | T__64 | T__65 | T__66 | T__67 | T__68 | T__69 | T__70 | T__71 | T__72 | T__73 | T__74 | T__75 | T__76 | T__77 | T__78 | T__79 | SQTEXT | DQTEXT | MINUS | STAR | PLUS | DOT | BAR | HAT | EQUALS | LBRACE | RBRACE | LPAR | RPAR | LSQUARE | RSQUARE | PLING | QUERY | COLON | COMMA | SQUOTE | DQUOTE | DOLLAR | UNDER | BSLASH | IDENT | NUMBER | RNUMBER | LABEL );";
        }
    }
 

}