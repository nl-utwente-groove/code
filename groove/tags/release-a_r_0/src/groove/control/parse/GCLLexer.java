// $ANTLR 3.2 Sep 23, 2009 12:02:23 GCL.g 2010-09-09 10:23:43

package groove.control.parse;

import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;

@SuppressWarnings("all")
public class GCLLexer extends Lexer {
    public static final int FUNCTION = 7;
    public static final int STAR = 24;
    public static final int FUNCTIONS = 6;
    public static final int WHILE = 15;
    public static final int BOOL_TYPE = 30;
    public static final int NODE_TYPE = 29;
    public static final int DO = 9;
    public static final int PARAM = 11;
    public static final int NOT = 44;
    public static final int ALAP = 14;
    public static final int AND = 43;
    public static final int EOF = -1;
    public static final int IF = 19;
    public static final int ML_COMMENT = 45;
    public static final int ANY_CHAR = 48;
    public static final int QUOTE = 39;
    public static final int T__51 = 51;
    public static final int T__52 = 52;
    public static final int T__53 = 53;
    public static final int COMMA = 34;
    public static final int IDENTIFIER = 12;
    public static final int CH_OR = 18;
    public static final int PLUS = 23;
    public static final int VAR = 10;
    public static final int DOT = 28;
    public static final int T__50 = 50;
    public static final int CHOICE = 17;
    public static final int SHARP = 25;
    public static final int OTHER = 27;
    public static final int T__49 = 49;
    public static final int ELSE = 20;
    public static final int NUMBER = 42;
    public static final int MINUS = 41;
    public static final int INT_TYPE = 32;
    public static final int TRUE = 22;
    public static final int TRY = 21;
    public static final int REAL_TYPE = 33;
    public static final int DONT_CARE = 36;
    public static final int WS = 47;
    public static final int ANY = 26;
    public static final int OUT = 35;
    public static final int UNTIL = 16;
    public static final int STRING_TYPE = 31;
    public static final int BLOCK = 5;
    public static final int OR = 13;
    public static final int SL_COMMENT = 46;
    public static final int PROGRAM = 4;
    public static final int CALL = 8;
    public static final int FALSE = 37;
    public static final int BSLASH = 40;
    public static final int STRING = 38;

    private List<String> errors = new LinkedList<String>();

    public void displayRecognitionError(String[] tokenNames,
            RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        this.errors.add(hdr + " " + msg);
    }

    public List<String> getErrors() {
        return this.errors;
    }

    // delegates
    // delegators

    public GCLLexer() {
        ;
    }

    public GCLLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }

    public GCLLexer(CharStream input, RecognizerSharedState state) {
        super(input, state);

    }

    public String getGrammarFileName() {
        return "GCL.g";
    }

    // $ANTLR start "T__49"
    public final void mT__49() throws RecognitionException {
        try {
            int _type = T__49;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:23:7: ( '{' )
            // GCL.g:23:9: '{'
            {
                match('{');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "T__49"

    // $ANTLR start "T__50"
    public final void mT__50() throws RecognitionException {
        try {
            int _type = T__50;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:24:7: ( '}' )
            // GCL.g:24:9: '}'
            {
                match('}');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "T__50"

    // $ANTLR start "T__51"
    public final void mT__51() throws RecognitionException {
        try {
            int _type = T__51;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:25:7: ( '(' )
            // GCL.g:25:9: '('
            {
                match('(');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "T__51"

    // $ANTLR start "T__52"
    public final void mT__52() throws RecognitionException {
        try {
            int _type = T__52;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:26:7: ( ')' )
            // GCL.g:26:9: ')'
            {
                match(')');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "T__52"

    // $ANTLR start "T__53"
    public final void mT__53() throws RecognitionException {
        try {
            int _type = T__53;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:27:7: ( ';' )
            // GCL.g:27:9: ';'
            {
                match(';');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "T__53"

    // $ANTLR start "ALAP"
    public final void mALAP() throws RecognitionException {
        try {
            int _type = ALAP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:207:7: ( 'alap' )
            // GCL.g:207:9: 'alap'
            {
                match("alap");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "ALAP"

    // $ANTLR start "WHILE"
    public final void mWHILE() throws RecognitionException {
        try {
            int _type = WHILE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:208:7: ( 'while' )
            // GCL.g:208:9: 'while'
            {
                match("while");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "WHILE"

    // $ANTLR start "DO"
    public final void mDO() throws RecognitionException {
        try {
            int _type = DO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:209:5: ( 'do' )
            // GCL.g:209:7: 'do'
            {
                match("do");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "DO"

    // $ANTLR start "UNTIL"
    public final void mUNTIL() throws RecognitionException {
        try {
            int _type = UNTIL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:210:9: ( 'until' )
            // GCL.g:210:11: 'until'
            {
                match("until");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "UNTIL"

    // $ANTLR start "IF"
    public final void mIF() throws RecognitionException {
        try {
            int _type = IF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:211:5: ( 'if' )
            // GCL.g:211:7: 'if'
            {
                match("if");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "IF"

    // $ANTLR start "ELSE"
    public final void mELSE() throws RecognitionException {
        try {
            int _type = ELSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:212:6: ( 'else' )
            // GCL.g:212:8: 'else'
            {
                match("else");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "ELSE"

    // $ANTLR start "CHOICE"
    public final void mCHOICE() throws RecognitionException {
        try {
            int _type = CHOICE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:213:8: ( 'choice' )
            // GCL.g:213:10: 'choice'
            {
                match("choice");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "CHOICE"

    // $ANTLR start "CH_OR"
    public final void mCH_OR() throws RecognitionException {
        try {
            int _type = CH_OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:214:8: ( 'or' )
            // GCL.g:214:10: 'or'
            {
                match("or");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "CH_OR"

    // $ANTLR start "TRY"
    public final void mTRY() throws RecognitionException {
        try {
            int _type = TRY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:215:6: ( 'try' )
            // GCL.g:215:8: 'try'
            {
                match("try");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "TRY"

    // $ANTLR start "FUNCTION"
    public final void mFUNCTION() throws RecognitionException {
        try {
            int _type = FUNCTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:216:9: ( 'function' )
            // GCL.g:216:11: 'function'
            {
                match("function");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "FUNCTION"

    // $ANTLR start "TRUE"
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:217:6: ( 'true' )
            // GCL.g:217:8: 'true'
            {
                match("true");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "TRUE"

    // $ANTLR start "FALSE"
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:218:7: ( 'false' )
            // GCL.g:218:9: 'false'
            {
                match("false");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "FALSE"

    // $ANTLR start "OTHER"
    public final void mOTHER() throws RecognitionException {
        try {
            int _type = OTHER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:219:7: ( 'other' )
            // GCL.g:219:9: 'other'
            {
                match("other");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "OTHER"

    // $ANTLR start "ANY"
    public final void mANY() throws RecognitionException {
        try {
            int _type = ANY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:220:6: ( 'any' )
            // GCL.g:220:8: 'any'
            {
                match("any");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "ANY"

    // $ANTLR start "NODE_TYPE"
    public final void mNODE_TYPE() throws RecognitionException {
        try {
            int _type = NODE_TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:221:11: ( 'node' )
            // GCL.g:221:13: 'node'
            {
                match("node");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "NODE_TYPE"

    // $ANTLR start "BOOL_TYPE"
    public final void mBOOL_TYPE() throws RecognitionException {
        try {
            int _type = BOOL_TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:222:11: ( 'bool' )
            // GCL.g:222:13: 'bool'
            {
                match("bool");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "BOOL_TYPE"

    // $ANTLR start "STRING_TYPE"
    public final void mSTRING_TYPE() throws RecognitionException {
        try {
            int _type = STRING_TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:223:13: ( 'string' )
            // GCL.g:223:15: 'string'
            {
                match("string");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "STRING_TYPE"

    // $ANTLR start "INT_TYPE"
    public final void mINT_TYPE() throws RecognitionException {
        try {
            int _type = INT_TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:224:10: ( 'int' )
            // GCL.g:224:12: 'int'
            {
                match("int");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "INT_TYPE"

    // $ANTLR start "REAL_TYPE"
    public final void mREAL_TYPE() throws RecognitionException {
        try {
            int _type = REAL_TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:225:11: ( 'real' )
            // GCL.g:225:13: 'real'
            {
                match("real");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "REAL_TYPE"

    // $ANTLR start "OUT"
    public final void mOUT() throws RecognitionException {
        try {
            int _type = OUT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:226:6: ( 'out' )
            // GCL.g:226:8: 'out'
            {
                match("out");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "OUT"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:229:6: ( '&' )
            // GCL.g:229:9: '&'
            {
                match('&');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "AND"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:230:8: ( ',' )
            // GCL.g:230:11: ','
            {
                match(',');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "COMMA"

    // $ANTLR start "DOT"
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:231:6: ( '.' )
            // GCL.g:231:9: '.'
            {
                match('.');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "DOT"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:232:6: ( '!' )
            // GCL.g:232:9: '!'
            {
                match('!');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "NOT"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:233:5: ( '|' )
            // GCL.g:233:8: '|'
            {
                match('|');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "OR"

    // $ANTLR start "SHARP"
    public final void mSHARP() throws RecognitionException {
        try {
            int _type = SHARP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:234:8: ( '#' )
            // GCL.g:234:11: '#'
            {
                match('#');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "SHARP"

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:235:7: ( '+' )
            // GCL.g:235:10: '+'
            {
                match('+');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "PLUS"

    // $ANTLR start "STAR"
    public final void mSTAR() throws RecognitionException {
        try {
            int _type = STAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:236:7: ( '*' )
            // GCL.g:236:10: '*'
            {
                match('*');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "STAR"

    // $ANTLR start "DONT_CARE"
    public final void mDONT_CARE() throws RecognitionException {
        try {
            int _type = DONT_CARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:237:11: ( '_' )
            // GCL.g:237:13: '_'
            {
                match('_');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "DONT_CARE"

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:238:7: ( '-' )
            // GCL.g:238:9: '-'
            {
                match('-');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "MINUS"

    // $ANTLR start "QUOTE"
    public final void mQUOTE() throws RecognitionException {
        try {
            int _type = QUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:239:9: ( '\"' )
            // GCL.g:239:11: '\"'
            {
                match('\"');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "QUOTE"

    // $ANTLR start "BSLASH"
    public final void mBSLASH() throws RecognitionException {
        try {
            int _type = BSLASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:240:8: ( '\\\\' )
            // GCL.g:240:10: '\\\\'
            {
                match('\\');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "BSLASH"

    // $ANTLR start "IDENTIFIER"
    public final void mIDENTIFIER() throws RecognitionException {
        try {
            int _type = IDENTIFIER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:242:13: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )* )
            // GCL.g:242:15: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
            {
                if ((this.input.LA(1) >= 'A' && this.input.LA(1) <= 'Z')
                    || (this.input.LA(1) >= 'a' && this.input.LA(1) <= 'z')) {
                    this.input.consume();

                } else {
                    MismatchedSetException mse =
                        new MismatchedSetException(null, this.input);
                    recover(mse);
                    throw mse;
                }

                // GCL.g:242:35: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
                loop1: do {
                    int alt1 = 2;
                    int LA1_0 = this.input.LA(1);

                    if ((LA1_0 == '-' || (LA1_0 >= '0' && LA1_0 <= '9')
                        || (LA1_0 >= 'A' && LA1_0 <= 'Z') || LA1_0 == '_' || (LA1_0 >= 'a' && LA1_0 <= 'z'))) {
                        alt1 = 1;
                    }

                    switch (alt1) {
                    case 1:
                        // GCL.g:
                    {
                        if (this.input.LA(1) == '-'
                            || (this.input.LA(1) >= '0' && this.input.LA(1) <= '9')
                            || (this.input.LA(1) >= 'A' && this.input.LA(1) <= 'Z')
                            || this.input.LA(1) == '_'
                            || (this.input.LA(1) >= 'a' && this.input.LA(1) <= 'z')) {
                            this.input.consume();

                        } else {
                            MismatchedSetException mse =
                                new MismatchedSetException(null, this.input);
                            recover(mse);
                            throw mse;
                        }

                    }
                        break;

                    default:
                        break loop1;
                    }
                } while (true);

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "IDENTIFIER"

    // $ANTLR start "NUMBER"
    public final void mNUMBER() throws RecognitionException {
        try {
            int _type = NUMBER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:245:8: ( ( '0' .. '9' )+ )
            // GCL.g:245:10: ( '0' .. '9' )+
            {
                // GCL.g:245:10: ( '0' .. '9' )+
                int cnt2 = 0;
                loop2: do {
                    int alt2 = 2;
                    int LA2_0 = this.input.LA(1);

                    if (((LA2_0 >= '0' && LA2_0 <= '9'))) {
                        alt2 = 1;
                    }

                    switch (alt2) {
                    case 1:
                        // GCL.g:245:11: '0' .. '9'
                    {
                        matchRange('0', '9');

                    }
                        break;

                    default:
                        if (cnt2 >= 1) {
                            break loop2;
                        }
                        EarlyExitException eee =
                            new EarlyExitException(2, this.input);
                        throw eee;
                    }
                    cnt2++;
                } while (true);

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "NUMBER"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:246:8: ( QUOTE (~ ( QUOTE | BSLASH ) | BSLASH ( QUOTE | BSLASH ) )* QUOTE )
            // GCL.g:246:10: QUOTE (~ ( QUOTE | BSLASH ) | BSLASH ( QUOTE | BSLASH ) )* QUOTE
            {
                mQUOTE();
                // GCL.g:246:16: (~ ( QUOTE | BSLASH ) | BSLASH ( QUOTE | BSLASH ) )*
                loop3: do {
                    int alt3 = 3;
                    int LA3_0 = this.input.LA(1);

                    if (((LA3_0 >= '\u0000' && LA3_0 <= '!')
                        || (LA3_0 >= '#' && LA3_0 <= '[') || (LA3_0 >= ']' && LA3_0 <= '\uFFFF'))) {
                        alt3 = 1;
                    } else if ((LA3_0 == '\\')) {
                        alt3 = 2;
                    }

                    switch (alt3) {
                    case 1:
                        // GCL.g:246:17: ~ ( QUOTE | BSLASH )
                    {
                        if ((this.input.LA(1) >= '\u0000' && this.input.LA(1) <= '!')
                            || (this.input.LA(1) >= '#' && this.input.LA(1) <= '[')
                            || (this.input.LA(1) >= ']' && this.input.LA(1) <= '\uFFFF')) {
                            this.input.consume();

                        } else {
                            MismatchedSetException mse =
                                new MismatchedSetException(null, this.input);
                            recover(mse);
                            throw mse;
                        }

                    }
                        break;
                    case 2:
                        // GCL.g:246:35: BSLASH ( QUOTE | BSLASH )
                    {
                        mBSLASH();
                        if (this.input.LA(1) == '\"'
                            || this.input.LA(1) == '\\') {
                            this.input.consume();

                        } else {
                            MismatchedSetException mse =
                                new MismatchedSetException(null, this.input);
                            recover(mse);
                            throw mse;
                        }

                    }
                        break;

                    default:
                        break loop3;
                    }
                } while (true);

                mQUOTE();

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "STRING"

    // $ANTLR start "ML_COMMENT"
    public final void mML_COMMENT() throws RecognitionException {
        try {
            int _type = ML_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:248:12: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // GCL.g:248:14: '/*' ( options {greedy=false; } : . )* '*/'
            {
                match("/*");

                // GCL.g:248:19: ( options {greedy=false; } : . )*
                loop4: do {
                    int alt4 = 2;
                    int LA4_0 = this.input.LA(1);

                    if ((LA4_0 == '*')) {
                        int LA4_1 = this.input.LA(2);

                        if ((LA4_1 == '/')) {
                            alt4 = 2;
                        } else if (((LA4_1 >= '\u0000' && LA4_1 <= '.') || (LA4_1 >= '0' && LA4_1 <= '\uFFFF'))) {
                            alt4 = 1;
                        }

                    } else if (((LA4_0 >= '\u0000' && LA4_0 <= ')') || (LA4_0 >= '+' && LA4_0 <= '\uFFFF'))) {
                        alt4 = 1;
                    }

                    switch (alt4) {
                    case 1:
                        // GCL.g:248:47: .
                    {
                        matchAny();

                    }
                        break;

                    default:
                        break loop4;
                    }
                } while (true);

                match("*/");

                _channel = HIDDEN;

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "ML_COMMENT"

    // $ANTLR start "SL_COMMENT"
    public final void mSL_COMMENT() throws RecognitionException {
        try {
            int _type = SL_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:249:12: ( '//' ( options {greedy=false; } : . )* '\\n' )
            // GCL.g:249:14: '//' ( options {greedy=false; } : . )* '\\n'
            {
                match("//");

                // GCL.g:249:19: ( options {greedy=false; } : . )*
                loop5: do {
                    int alt5 = 2;
                    int LA5_0 = this.input.LA(1);

                    if ((LA5_0 == '\n')) {
                        alt5 = 2;
                    } else if (((LA5_0 >= '\u0000' && LA5_0 <= '\t') || (LA5_0 >= '\u000B' && LA5_0 <= '\uFFFF'))) {
                        alt5 = 1;
                    }

                    switch (alt5) {
                    case 1:
                        // GCL.g:249:47: .
                    {
                        matchAny();

                    }
                        break;

                    default:
                        break loop5;
                    }
                } while (true);

                match('\n');
                _channel = HIDDEN;

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "SL_COMMENT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:251:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // GCL.g:251:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
                // GCL.g:251:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
                int cnt6 = 0;
                loop6: do {
                    int alt6 = 2;
                    int LA6_0 = this.input.LA(1);

                    if (((LA6_0 >= '\t' && LA6_0 <= '\n') || LA6_0 == '\r' || LA6_0 == ' ')) {
                        alt6 = 1;
                    }

                    switch (alt6) {
                    case 1:
                        // GCL.g:
                    {
                        if ((this.input.LA(1) >= '\t' && this.input.LA(1) <= '\n')
                            || this.input.LA(1) == '\r'
                            || this.input.LA(1) == ' ') {
                            this.input.consume();

                        } else {
                            MismatchedSetException mse =
                                new MismatchedSetException(null, this.input);
                            recover(mse);
                            throw mse;
                        }

                    }
                        break;

                    default:
                        if (cnt6 >= 1) {
                            break loop6;
                        }
                        EarlyExitException eee =
                            new EarlyExitException(6, this.input);
                        throw eee;
                    }
                    cnt6++;
                } while (true);

                _channel = HIDDEN;

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "WS"

    // $ANTLR start "ANY_CHAR"
    public final void mANY_CHAR() throws RecognitionException {
        try {
            int _type = ANY_CHAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // GCL.g:259:10: ( . )
            // GCL.g:259:12: .
            {
                matchAny();

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "ANY_CHAR"

    public void mTokens() throws RecognitionException {
        // GCL.g:1:8: ( T__49 | T__50 | T__51 | T__52 | T__53 | ALAP | WHILE | DO | UNTIL | IF | ELSE | CHOICE | CH_OR | TRY | FUNCTION | TRUE | FALSE | OTHER | ANY | NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE | OUT | AND | COMMA | DOT | NOT | OR | SHARP | PLUS | STAR | DONT_CARE | MINUS | QUOTE | BSLASH | IDENTIFIER | NUMBER | STRING | ML_COMMENT | SL_COMMENT | WS | ANY_CHAR )
        int alt7 = 44;
        alt7 = this.dfa7.predict(this.input);
        switch (alt7) {
        case 1:
            // GCL.g:1:10: T__49
        {
            mT__49();

        }
            break;
        case 2:
            // GCL.g:1:16: T__50
        {
            mT__50();

        }
            break;
        case 3:
            // GCL.g:1:22: T__51
        {
            mT__51();

        }
            break;
        case 4:
            // GCL.g:1:28: T__52
        {
            mT__52();

        }
            break;
        case 5:
            // GCL.g:1:34: T__53
        {
            mT__53();

        }
            break;
        case 6:
            // GCL.g:1:40: ALAP
        {
            mALAP();

        }
            break;
        case 7:
            // GCL.g:1:45: WHILE
        {
            mWHILE();

        }
            break;
        case 8:
            // GCL.g:1:51: DO
        {
            mDO();

        }
            break;
        case 9:
            // GCL.g:1:54: UNTIL
        {
            mUNTIL();

        }
            break;
        case 10:
            // GCL.g:1:60: IF
        {
            mIF();

        }
            break;
        case 11:
            // GCL.g:1:63: ELSE
        {
            mELSE();

        }
            break;
        case 12:
            // GCL.g:1:68: CHOICE
        {
            mCHOICE();

        }
            break;
        case 13:
            // GCL.g:1:75: CH_OR
        {
            mCH_OR();

        }
            break;
        case 14:
            // GCL.g:1:81: TRY
        {
            mTRY();

        }
            break;
        case 15:
            // GCL.g:1:85: FUNCTION
        {
            mFUNCTION();

        }
            break;
        case 16:
            // GCL.g:1:94: TRUE
        {
            mTRUE();

        }
            break;
        case 17:
            // GCL.g:1:99: FALSE
        {
            mFALSE();

        }
            break;
        case 18:
            // GCL.g:1:105: OTHER
        {
            mOTHER();

        }
            break;
        case 19:
            // GCL.g:1:111: ANY
        {
            mANY();

        }
            break;
        case 20:
            // GCL.g:1:115: NODE_TYPE
        {
            mNODE_TYPE();

        }
            break;
        case 21:
            // GCL.g:1:125: BOOL_TYPE
        {
            mBOOL_TYPE();

        }
            break;
        case 22:
            // GCL.g:1:135: STRING_TYPE
        {
            mSTRING_TYPE();

        }
            break;
        case 23:
            // GCL.g:1:147: INT_TYPE
        {
            mINT_TYPE();

        }
            break;
        case 24:
            // GCL.g:1:156: REAL_TYPE
        {
            mREAL_TYPE();

        }
            break;
        case 25:
            // GCL.g:1:166: OUT
        {
            mOUT();

        }
            break;
        case 26:
            // GCL.g:1:170: AND
        {
            mAND();

        }
            break;
        case 27:
            // GCL.g:1:174: COMMA
        {
            mCOMMA();

        }
            break;
        case 28:
            // GCL.g:1:180: DOT
        {
            mDOT();

        }
            break;
        case 29:
            // GCL.g:1:184: NOT
        {
            mNOT();

        }
            break;
        case 30:
            // GCL.g:1:188: OR
        {
            mOR();

        }
            break;
        case 31:
            // GCL.g:1:191: SHARP
        {
            mSHARP();

        }
            break;
        case 32:
            // GCL.g:1:197: PLUS
        {
            mPLUS();

        }
            break;
        case 33:
            // GCL.g:1:202: STAR
        {
            mSTAR();

        }
            break;
        case 34:
            // GCL.g:1:207: DONT_CARE
        {
            mDONT_CARE();

        }
            break;
        case 35:
            // GCL.g:1:217: MINUS
        {
            mMINUS();

        }
            break;
        case 36:
            // GCL.g:1:223: QUOTE
        {
            mQUOTE();

        }
            break;
        case 37:
            // GCL.g:1:229: BSLASH
        {
            mBSLASH();

        }
            break;
        case 38:
            // GCL.g:1:236: IDENTIFIER
        {
            mIDENTIFIER();

        }
            break;
        case 39:
            // GCL.g:1:247: NUMBER
        {
            mNUMBER();

        }
            break;
        case 40:
            // GCL.g:1:254: STRING
        {
            mSTRING();

        }
            break;
        case 41:
            // GCL.g:1:261: ML_COMMENT
        {
            mML_COMMENT();

        }
            break;
        case 42:
            // GCL.g:1:272: SL_COMMENT
        {
            mSL_COMMENT();

        }
            break;
        case 43:
            // GCL.g:1:283: WS
        {
            mWS();

        }
            break;
        case 44:
            // GCL.g:1:286: ANY_CHAR
        {
            mANY_CHAR();

        }
            break;

        }

    }

    protected DFA7 dfa7 = new DFA7(this);
    static final String DFA7_eotS =
        "\6\uffff\16\54\12\uffff\1\110\3\uffff\1\44\7\uffff\2\54\1\uffff"
            + "\1\54\1\122\1\54\1\124\3\54\1\130\11\54\21\uffff\1\54\1\144\1\54"
            + "\1\uffff\1\54\1\uffff\1\147\2\54\1\uffff\1\54\1\153\1\154\7\54\1"
            + "\164\1\uffff\2\54\1\uffff\1\167\2\54\2\uffff\1\172\2\54\1\175\1"
            + "\176\1\54\1\u0080\1\uffff\1\u0081\1\u0082\1\uffff\1\54\1\u0084\1"
            + "\uffff\1\54\1\u0086\2\uffff\1\54\3\uffff\1\u0088\1\uffff\1\54\1"
            + "\uffff\1\u008a\1\uffff\1\54\1\uffff\1\u008c\1\uffff";
    static final String DFA7_eofS = "\u008d\uffff";
    static final String DFA7_minS =
        "\1\0\5\uffff\1\154\1\150\1\157\1\156\1\146\1\154\1\150\2\162\1"
            + "\141\2\157\1\164\1\145\12\uffff\1\0\3\uffff\1\52\7\uffff\1\141\1"
            + "\171\1\uffff\1\151\1\55\1\164\1\55\1\164\1\163\1\157\1\55\1\150"
            + "\1\164\1\165\1\156\1\154\1\144\1\157\1\162\1\141\21\uffff\1\160"
            + "\1\55\1\154\1\uffff\1\151\1\uffff\1\55\1\145\1\151\1\uffff\1\145"
            + "\2\55\1\145\1\143\1\163\1\145\1\154\1\151\1\154\1\55\1\uffff\1\145"
            + "\1\154\1\uffff\1\55\1\143\1\162\2\uffff\1\55\1\164\1\145\2\55\1"
            + "\156\1\55\1\uffff\2\55\1\uffff\1\145\1\55\1\uffff\1\151\1\55\2\uffff"
            + "\1\147\3\uffff\1\55\1\uffff\1\157\1\uffff\1\55\1\uffff\1\156\1\uffff"
            + "\1\55\1\uffff";
    static final String DFA7_maxS =
        "\1\uffff\5\uffff\1\156\1\150\1\157\2\156\1\154\1\150\1\165\1\162"
            + "\1\165\2\157\1\164\1\145\12\uffff\1\uffff\3\uffff\1\57\7\uffff\1"
            + "\141\1\171\1\uffff\1\151\1\172\1\164\1\172\1\164\1\163\1\157\1\172"
            + "\1\150\1\164\1\171\1\156\1\154\1\144\1\157\1\162\1\141\21\uffff"
            + "\1\160\1\172\1\154\1\uffff\1\151\1\uffff\1\172\1\145\1\151\1\uffff"
            + "\1\145\2\172\1\145\1\143\1\163\1\145\1\154\1\151\1\154\1\172\1\uffff"
            + "\1\145\1\154\1\uffff\1\172\1\143\1\162\2\uffff\1\172\1\164\1\145"
            + "\2\172\1\156\1\172\1\uffff\2\172\1\uffff\1\145\1\172\1\uffff\1\151"
            + "\1\172\2\uffff\1\147\3\uffff\1\172\1\uffff\1\157\1\uffff\1\172\1"
            + "\uffff\1\156\1\uffff\1\172\1\uffff";
    static final String DFA7_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\16\uffff\1\32\1\33\1\34\1\35\1\36"
            + "\1\37\1\40\1\41\1\42\1\43\1\uffff\1\45\1\46\1\47\1\uffff\1\53\1"
            + "\54\1\1\1\2\1\3\1\4\1\5\2\uffff\1\46\21\uffff\1\32\1\33\1\34\1\35"
            + "\1\36\1\37\1\40\1\41\1\42\1\43\1\44\1\50\1\45\1\47\1\51\1\52\1\53"
            + "\3\uffff\1\10\1\uffff\1\12\3\uffff\1\15\13\uffff\1\23\2\uffff\1"
            + "\27\3\uffff\1\31\1\16\7\uffff\1\6\2\uffff\1\13\2\uffff\1\20\2\uffff"
            + "\1\24\1\25\1\uffff\1\30\1\7\1\11\1\uffff\1\22\1\uffff\1\21\1\uffff"
            + "\1\14\1\uffff\1\26\1\uffff\1\17";
    static final String DFA7_specialS = "\1\0\35\uffff\1\1\156\uffff}>";
    static final String[] DFA7_transitionS =
        {
            "\11\44\2\43\2\44\1\43\22\44\1\43\1\27\1\36\1\31\2\44\1\24\1"
                + "\44\1\3\1\4\1\33\1\32\1\25\1\35\1\26\1\42\12\41\1\44\1\5\5\44"
                + "\32\40\1\44\1\37\2\44\1\34\1\44\1\6\1\21\1\14\1\10\1\13\1\17"
                + "\2\40\1\12\4\40\1\20\1\15\2\40\1\23\1\22\1\16\1\11\1\40\1\7"
                + "\3\40\1\1\1\30\1\2\uff82\44",
            "",
            "",
            "",
            "",
            "",
            "\1\52\1\uffff\1\53",
            "\1\55",
            "\1\56",
            "\1\57",
            "\1\60\7\uffff\1\61",
            "\1\62",
            "\1\63",
            "\1\64\1\uffff\1\65\1\66",
            "\1\67",
            "\1\71\23\uffff\1\70",
            "\1\72",
            "\1\73",
            "\1\74",
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
            "",
            "\0\111",
            "",
            "",
            "",
            "\1\114\4\uffff\1\115",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\117",
            "\1\120",
            "",
            "\1\121",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            "\1\123",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            "\1\125",
            "\1\126",
            "\1\127",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            "\1\131",
            "\1\132",
            "\1\134\3\uffff\1\133",
            "\1\135",
            "\1\136",
            "\1\137",
            "\1\140",
            "\1\141",
            "\1\142",
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
            "\1\143",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            "\1\145",
            "",
            "\1\146",
            "",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            "\1\150",
            "\1\151",
            "",
            "\1\152",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            "\1\155",
            "\1\156",
            "\1\157",
            "\1\160",
            "\1\161",
            "\1\162",
            "\1\163",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            "",
            "\1\165",
            "\1\166",
            "",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            "\1\170",
            "\1\171",
            "",
            "",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            "\1\173",
            "\1\174",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            "\1\177",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            "",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            "",
            "\1\u0083",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            "",
            "\1\u0085",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            "",
            "",
            "\1\u0087",
            "",
            "",
            "",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            "",
            "\1\u0089",
            "",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            "",
            "\1\u008b",
            "",
            "\1\54\2\uffff\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32" + "\54",
            ""};

    static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
    static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
    static final char[] DFA7_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
    static final char[] DFA7_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
    static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
    static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
    static final short[][] DFA7_transition;

    static {
        int numStates = DFA7_transitionS.length;
        DFA7_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
        }
    }

    class DFA7 extends DFA {

        public DFA7(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 7;
            this.eot = DFA7_eot;
            this.eof = DFA7_eof;
            this.min = DFA7_min;
            this.max = DFA7_max;
            this.accept = DFA7_accept;
            this.special = DFA7_special;
            this.transition = DFA7_transition;
        }

        public String getDescription() {
            return "1:1: Tokens : ( T__49 | T__50 | T__51 | T__52 | T__53 | ALAP | WHILE | DO | UNTIL | IF | ELSE | CHOICE | CH_OR | TRY | FUNCTION | TRUE | FALSE | OTHER | ANY | NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE | OUT | AND | COMMA | DOT | NOT | OR | SHARP | PLUS | STAR | DONT_CARE | MINUS | QUOTE | BSLASH | IDENTIFIER | NUMBER | STRING | ML_COMMENT | SL_COMMENT | WS | ANY_CHAR );";
        }

        public int specialStateTransition(int s, IntStream _input)
            throws NoViableAltException {
            IntStream input = _input;
            int _s = s;
            switch (s) {
            case 0:
                int LA7_0 = input.LA(1);

                s = -1;
                if ((LA7_0 == '{')) {
                    s = 1;
                }

                else if ((LA7_0 == '}')) {
                    s = 2;
                }

                else if ((LA7_0 == '(')) {
                    s = 3;
                }

                else if ((LA7_0 == ')')) {
                    s = 4;
                }

                else if ((LA7_0 == ';')) {
                    s = 5;
                }

                else if ((LA7_0 == 'a')) {
                    s = 6;
                }

                else if ((LA7_0 == 'w')) {
                    s = 7;
                }

                else if ((LA7_0 == 'd')) {
                    s = 8;
                }

                else if ((LA7_0 == 'u')) {
                    s = 9;
                }

                else if ((LA7_0 == 'i')) {
                    s = 10;
                }

                else if ((LA7_0 == 'e')) {
                    s = 11;
                }

                else if ((LA7_0 == 'c')) {
                    s = 12;
                }

                else if ((LA7_0 == 'o')) {
                    s = 13;
                }

                else if ((LA7_0 == 't')) {
                    s = 14;
                }

                else if ((LA7_0 == 'f')) {
                    s = 15;
                }

                else if ((LA7_0 == 'n')) {
                    s = 16;
                }

                else if ((LA7_0 == 'b')) {
                    s = 17;
                }

                else if ((LA7_0 == 's')) {
                    s = 18;
                }

                else if ((LA7_0 == 'r')) {
                    s = 19;
                }

                else if ((LA7_0 == '&')) {
                    s = 20;
                }

                else if ((LA7_0 == ',')) {
                    s = 21;
                }

                else if ((LA7_0 == '.')) {
                    s = 22;
                }

                else if ((LA7_0 == '!')) {
                    s = 23;
                }

                else if ((LA7_0 == '|')) {
                    s = 24;
                }

                else if ((LA7_0 == '#')) {
                    s = 25;
                }

                else if ((LA7_0 == '+')) {
                    s = 26;
                }

                else if ((LA7_0 == '*')) {
                    s = 27;
                }

                else if ((LA7_0 == '_')) {
                    s = 28;
                }

                else if ((LA7_0 == '-')) {
                    s = 29;
                }

                else if ((LA7_0 == '\"')) {
                    s = 30;
                }

                else if ((LA7_0 == '\\')) {
                    s = 31;
                }

                else if (((LA7_0 >= 'A' && LA7_0 <= 'Z')
                    || (LA7_0 >= 'g' && LA7_0 <= 'h')
                    || (LA7_0 >= 'j' && LA7_0 <= 'm')
                    || (LA7_0 >= 'p' && LA7_0 <= 'q') || LA7_0 == 'v' || (LA7_0 >= 'x' && LA7_0 <= 'z'))) {
                    s = 32;
                }

                else if (((LA7_0 >= '0' && LA7_0 <= '9'))) {
                    s = 33;
                }

                else if ((LA7_0 == '/')) {
                    s = 34;
                }

                else if (((LA7_0 >= '\t' && LA7_0 <= '\n') || LA7_0 == '\r' || LA7_0 == ' ')) {
                    s = 35;
                }

                else if (((LA7_0 >= '\u0000' && LA7_0 <= '\b')
                    || (LA7_0 >= '\u000B' && LA7_0 <= '\f')
                    || (LA7_0 >= '\u000E' && LA7_0 <= '\u001F')
                    || (LA7_0 >= '$' && LA7_0 <= '%') || LA7_0 == '\''
                    || LA7_0 == ':' || (LA7_0 >= '<' && LA7_0 <= '@')
                    || LA7_0 == '[' || (LA7_0 >= ']' && LA7_0 <= '^')
                    || LA7_0 == '`' || (LA7_0 >= '~' && LA7_0 <= '\uFFFF'))) {
                    s = 36;
                }

                if (s >= 0) {
                    return s;
                }
                break;
            case 1:
                int LA7_30 = input.LA(1);

                s = -1;
                if (((LA7_30 >= '\u0000' && LA7_30 <= '\uFFFF'))) {
                    s = 73;
                } else {
                    s = 72;
                }

                if (s >= 0) {
                    return s;
                }
                break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 7, _s, input);
            error(nvae);
            throw nvae;
        }
    }

}