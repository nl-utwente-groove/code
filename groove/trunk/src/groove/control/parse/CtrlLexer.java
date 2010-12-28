// $ANTLR 3.2 Sep 23, 2009 12:02:23 Ctrl.g 2010-12-28 09:29:27

package groove.control.parse;

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
public class CtrlLexer extends Lexer {
    public static final int REAL_LIT = 48;
    public static final int FUNCTION = 10;
    public static final int DO_UNTIL = 9;
    public static final int STAR = 49;
    public static final int INT_LIT = 47;
    public static final int WHILE = 20;
    public static final int FUNCTIONS = 11;
    public static final int IntegerNumber = 50;
    public static final int STRING_LIT = 46;
    public static final int AMP = 55;
    public static final int DO = 22;
    public static final int NOT = 56;
    public static final int ALAP = 19;
    public static final int ID = 16;
    public static final int EOF = -1;
    public static final int ASTERISK = 32;
    public static final int IF = 23;
    public static final int ML_COMMENT = 58;
    public static final int QUOTE = 52;
    public static final int LPAR = 17;
    public static final int ARG = 4;
    public static final int COMMA = 37;
    public static final int NonIntegerNumber = 51;
    public static final int DO_WHILE = 8;
    public static final int ARGS = 5;
    public static final int PLUS = 31;
    public static final int VAR = 13;
    public static final int DOT = 36;
    public static final int CHOICE = 26;
    public static final int SHARP = 33;
    public static final int OTHER = 35;
    public static final int NODE = 38;
    public static final int ELSE = 24;
    public static final int BOOL = 39;
    public static final int LCURLY = 14;
    public static final int INT = 41;
    public static final int MINUS = 57;
    public static final int SEMI = 28;
    public static final int TRUE = 30;
    public static final int TRY = 25;
    public static final int REAL = 42;
    public static final int DONT_CARE = 44;
    public static final int ANY = 34;
    public static final int WS = 60;
    public static final int OUT = 43;
    public static final int UNTIL = 21;
    public static final int BLOCK = 6;
    public static final int OR = 27;
    public static final int RCURLY = 15;
    public static final int SL_COMMENT = 59;
    public static final int PROGRAM = 12;
    public static final int RPAR = 18;
    public static final int CALL = 7;
    public static final int FALSE = 45;
    public static final int EscapeSequence = 53;
    public static final int BSLASH = 54;
    public static final int BAR = 29;
    public static final int STRING = 40;

    // delegates
    // delegators

    public CtrlLexer() {
        ;
    }

    public CtrlLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }

    public CtrlLexer(CharStream input, RecognizerSharedState state) {
        super(input, state);

    }

    public String getGrammarFileName() {
        return "Ctrl.g";
    }

    // $ANTLR start "ALAP"
    public final void mALAP() throws RecognitionException {
        try {
            int _type = ALAP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:175:10: ( 'alap' )
            // Ctrl.g:175:12: 'alap'
            {
                match("alap");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "ALAP"

    // $ANTLR start "ANY"
    public final void mANY() throws RecognitionException {
        try {
            int _type = ANY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:176:9: ( 'any' )
            // Ctrl.g:176:11: 'any'
            {
                match("any");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "ANY"

    // $ANTLR start "BOOL"
    public final void mBOOL() throws RecognitionException {
        try {
            int _type = BOOL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:177:10: ( 'bool' )
            // Ctrl.g:177:12: 'bool'
            {
                match("bool");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "BOOL"

    // $ANTLR start "CHOICE"
    public final void mCHOICE() throws RecognitionException {
        try {
            int _type = CHOICE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:178:10: ( 'choice' )
            // Ctrl.g:178:12: 'choice'
            {
                match("choice");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "CHOICE"

    // $ANTLR start "DO"
    public final void mDO() throws RecognitionException {
        try {
            int _type = DO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:179:10: ( 'do' )
            // Ctrl.g:179:12: 'do'
            {
                match("do");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "DO"

    // $ANTLR start "ELSE"
    public final void mELSE() throws RecognitionException {
        try {
            int _type = ELSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:180:10: ( 'else' )
            // Ctrl.g:180:12: 'else'
            {
                match("else");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "ELSE"

    // $ANTLR start "FALSE"
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:181:10: ( 'false' )
            // Ctrl.g:181:12: 'false'
            {
                match("false");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "FALSE"

    // $ANTLR start "FUNCTION"
    public final void mFUNCTION() throws RecognitionException {
        try {
            int _type = FUNCTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:182:10: ( 'function' )
            // Ctrl.g:182:12: 'function'
            {
                match("function");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "FUNCTION"

    // $ANTLR start "IF"
    public final void mIF() throws RecognitionException {
        try {
            int _type = IF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:183:10: ( 'if' )
            // Ctrl.g:183:12: 'if'
            {
                match("if");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "IF"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:184:10: ( 'int' )
            // Ctrl.g:184:12: 'int'
            {
                match("int");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "INT"

    // $ANTLR start "NODE"
    public final void mNODE() throws RecognitionException {
        try {
            int _type = NODE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:185:10: ( 'node' )
            // Ctrl.g:185:12: 'node'
            {
                match("node");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "NODE"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:186:10: ( 'or' )
            // Ctrl.g:186:12: 'or'
            {
                match("or");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "OR"

    // $ANTLR start "OTHER"
    public final void mOTHER() throws RecognitionException {
        try {
            int _type = OTHER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:187:10: ( 'other' )
            // Ctrl.g:187:12: 'other'
            {
                match("other");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "OTHER"

    // $ANTLR start "OUT"
    public final void mOUT() throws RecognitionException {
        try {
            int _type = OUT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:188:10: ( 'out' )
            // Ctrl.g:188:12: 'out'
            {
                match("out");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "OUT"

    // $ANTLR start "REAL"
    public final void mREAL() throws RecognitionException {
        try {
            int _type = REAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:189:10: ( 'real' )
            // Ctrl.g:189:12: 'real'
            {
                match("real");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "REAL"

    // $ANTLR start "STAR"
    public final void mSTAR() throws RecognitionException {
        try {
            int _type = STAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:190:10: ( 'string' )
            // Ctrl.g:190:12: 'string'
            {
                match("string");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "STAR"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:191:10: ( 'star' )
            // Ctrl.g:191:12: 'star'
            {
                match("star");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "STRING"

    // $ANTLR start "TRY"
    public final void mTRY() throws RecognitionException {
        try {
            int _type = TRY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:192:10: ( 'try' )
            // Ctrl.g:192:12: 'try'
            {
                match("try");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "TRY"

    // $ANTLR start "TRUE"
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:193:10: ( 'true' )
            // Ctrl.g:193:12: 'true'
            {
                match("true");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "TRUE"

    // $ANTLR start "UNTIL"
    public final void mUNTIL() throws RecognitionException {
        try {
            int _type = UNTIL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:194:10: ( 'until' )
            // Ctrl.g:194:12: 'until'
            {
                match("until");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "UNTIL"

    // $ANTLR start "WHILE"
    public final void mWHILE() throws RecognitionException {
        try {
            int _type = WHILE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:195:10: ( 'while' )
            // Ctrl.g:195:12: 'while'
            {
                match("while");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "WHILE"

    // $ANTLR start "INT_LIT"
    public final void mINT_LIT() throws RecognitionException {
        try {
            int _type = INT_LIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:198:3: ( IntegerNumber )
            // Ctrl.g:198:5: IntegerNumber
            {
                mIntegerNumber();

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "INT_LIT"

    // $ANTLR start "IntegerNumber"
    public final void mIntegerNumber() throws RecognitionException {
        try {
            // Ctrl.g:203:3: ( '0' | '1' .. '9' ( '0' .. '9' )* )
            int alt2 = 2;
            int LA2_0 = this.input.LA(1);

            if ((LA2_0 == '0')) {
                alt2 = 1;
            } else if (((LA2_0 >= '1' && LA2_0 <= '9'))) {
                alt2 = 2;
            } else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, this.input);

                throw nvae;
            }
            switch (alt2) {
            case 1:
                // Ctrl.g:203:5: '0'
            {
                match('0');

            }
                break;
            case 2:
                // Ctrl.g:204:5: '1' .. '9' ( '0' .. '9' )*
            {
                matchRange('1', '9');
                // Ctrl.g:204:14: ( '0' .. '9' )*
                loop1: do {
                    int alt1 = 2;
                    int LA1_0 = this.input.LA(1);

                    if (((LA1_0 >= '0' && LA1_0 <= '9'))) {
                        alt1 = 1;
                    }

                    switch (alt1) {
                    case 1:
                        // Ctrl.g:204:15: '0' .. '9'
                    {
                        matchRange('0', '9');

                    }
                        break;

                    default:
                        break loop1;
                    }
                } while (true);

            }
                break;

            }
        } finally {
        }
    }

    // $ANTLR end "IntegerNumber"

    // $ANTLR start "REAL_LIT"
    public final void mREAL_LIT() throws RecognitionException {
        try {
            int _type = REAL_LIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:208:3: ( NonIntegerNumber )
            // Ctrl.g:208:5: NonIntegerNumber
            {
                mNonIntegerNumber();

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "REAL_LIT"

    // $ANTLR start "NonIntegerNumber"
    public final void mNonIntegerNumber() throws RecognitionException {
        try {
            // Ctrl.g:213:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* | '.' ( '0' .. '9' )+ )
            int alt6 = 2;
            int LA6_0 = this.input.LA(1);

            if (((LA6_0 >= '0' && LA6_0 <= '9'))) {
                alt6 = 1;
            } else if ((LA6_0 == '.')) {
                alt6 = 2;
            } else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, this.input);

                throw nvae;
            }
            switch (alt6) {
            case 1:
                // Ctrl.g:213:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )*
            {
                // Ctrl.g:213:9: ( '0' .. '9' )+
                int cnt3 = 0;
                loop3: do {
                    int alt3 = 2;
                    int LA3_0 = this.input.LA(1);

                    if (((LA3_0 >= '0' && LA3_0 <= '9'))) {
                        alt3 = 1;
                    }

                    switch (alt3) {
                    case 1:
                        // Ctrl.g:213:10: '0' .. '9'
                    {
                        matchRange('0', '9');

                    }
                        break;

                    default:
                        if (cnt3 >= 1) {
                            break loop3;
                        }
                        EarlyExitException eee =
                            new EarlyExitException(3, this.input);
                        throw eee;
                    }
                    cnt3++;
                } while (true);

                match('.');
                // Ctrl.g:213:27: ( '0' .. '9' )*
                loop4: do {
                    int alt4 = 2;
                    int LA4_0 = this.input.LA(1);

                    if (((LA4_0 >= '0' && LA4_0 <= '9'))) {
                        alt4 = 1;
                    }

                    switch (alt4) {
                    case 1:
                        // Ctrl.g:213:28: '0' .. '9'
                    {
                        matchRange('0', '9');

                    }
                        break;

                    default:
                        break loop4;
                    }
                } while (true);

            }
                break;
            case 2:
                // Ctrl.g:214:9: '.' ( '0' .. '9' )+
            {
                match('.');
                // Ctrl.g:214:13: ( '0' .. '9' )+
                int cnt5 = 0;
                loop5: do {
                    int alt5 = 2;
                    int LA5_0 = this.input.LA(1);

                    if (((LA5_0 >= '0' && LA5_0 <= '9'))) {
                        alt5 = 1;
                    }

                    switch (alt5) {
                    case 1:
                        // Ctrl.g:214:15: '0' .. '9'
                    {
                        matchRange('0', '9');

                    }
                        break;

                    default:
                        if (cnt5 >= 1) {
                            break loop5;
                        }
                        EarlyExitException eee =
                            new EarlyExitException(5, this.input);
                        throw eee;
                    }
                    cnt5++;
                } while (true);

            }
                break;

            }
        } finally {
        }
    }

    // $ANTLR end "NonIntegerNumber"

    // $ANTLR start "STRING_LIT"
    public final void mSTRING_LIT() throws RecognitionException {
        try {
            int _type = STRING_LIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:219:3: ( QUOTE ( EscapeSequence | ~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE )
            // Ctrl.g:219:5: QUOTE ( EscapeSequence | ~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE
            {
                mQUOTE();
                // Ctrl.g:220:5: ( EscapeSequence | ~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )*
                loop7: do {
                    int alt7 = 3;
                    int LA7_0 = this.input.LA(1);

                    if ((LA7_0 == '\\')) {
                        alt7 = 1;
                    } else if (((LA7_0 >= '\u0000' && LA7_0 <= '\t')
                        || (LA7_0 >= '\u000B' && LA7_0 <= '\f')
                        || (LA7_0 >= '\u000E' && LA7_0 <= '!')
                        || (LA7_0 >= '#' && LA7_0 <= '[') || (LA7_0 >= ']' && LA7_0 <= '\uFFFF'))) {
                        alt7 = 2;
                    }

                    switch (alt7) {
                    case 1:
                        // Ctrl.g:220:7: EscapeSequence
                    {
                        mEscapeSequence();

                    }
                        break;
                    case 2:
                        // Ctrl.g:221:7: ~ ( BSLASH | QUOTE | '\\r' | '\\n' )
                    {
                        if ((this.input.LA(1) >= '\u0000' && this.input.LA(1) <= '\t')
                            || (this.input.LA(1) >= '\u000B' && this.input.LA(1) <= '\f')
                            || (this.input.LA(1) >= '\u000E' && this.input.LA(1) <= '!')
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

                    default:
                        break loop7;
                    }
                } while (true);

                mQUOTE();

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "STRING_LIT"

    // $ANTLR start "EscapeSequence"
    public final void mEscapeSequence() throws RecognitionException {
        try {
            // Ctrl.g:228:3: ( BSLASH ( QUOTE BSLASH ) )
            // Ctrl.g:228:5: BSLASH ( QUOTE BSLASH )
            {
                mBSLASH();
                // Ctrl.g:229:5: ( QUOTE BSLASH )
                // Ctrl.g:229:7: QUOTE BSLASH
                {
                    mQUOTE();
                    mBSLASH();

                }

            }

        } finally {
        }
    }

    // $ANTLR end "EscapeSequence"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:234:5: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )* )
            // Ctrl.g:234:7: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
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

                // Ctrl.g:234:27: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
                loop8: do {
                    int alt8 = 2;
                    int LA8_0 = this.input.LA(1);

                    if ((LA8_0 == '-' || (LA8_0 >= '0' && LA8_0 <= '9')
                        || (LA8_0 >= 'A' && LA8_0 <= 'Z') || LA8_0 == '_' || (LA8_0 >= 'a' && LA8_0 <= 'z'))) {
                        alt8 = 1;
                    }

                    switch (alt8) {
                    case 1:
                        // Ctrl.g:
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
                        break loop8;
                    }
                } while (true);

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "ID"

    // $ANTLR start "AMP"
    public final void mAMP() throws RecognitionException {
        try {
            int _type = AMP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:236:11: ( '&' )
            // Ctrl.g:236:13: '&'
            {
                match('&');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "AMP"

    // $ANTLR start "DOT"
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:237:11: ( '.' )
            // Ctrl.g:237:13: '.'
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
            // Ctrl.g:238:11: ( '!' )
            // Ctrl.g:238:13: '!'
            {
                match('!');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "NOT"

    // $ANTLR start "BAR"
    public final void mBAR() throws RecognitionException {
        try {
            int _type = BAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:239:11: ( '|' )
            // Ctrl.g:239:13: '|'
            {
                match('|');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "BAR"

    // $ANTLR start "SHARP"
    public final void mSHARP() throws RecognitionException {
        try {
            int _type = SHARP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:240:11: ( '#' )
            // Ctrl.g:240:13: '#'
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
            // Ctrl.g:241:11: ( '+' )
            // Ctrl.g:241:13: '+'
            {
                match('+');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "PLUS"

    // $ANTLR start "ASTERISK"
    public final void mASTERISK() throws RecognitionException {
        try {
            int _type = ASTERISK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:242:11: ( '*' )
            // Ctrl.g:242:13: '*'
            {
                match('*');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "ASTERISK"

    // $ANTLR start "DONT_CARE"
    public final void mDONT_CARE() throws RecognitionException {
        try {
            int _type = DONT_CARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:243:11: ( '_' )
            // Ctrl.g:243:13: '_'
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
            // Ctrl.g:244:11: ( '-' )
            // Ctrl.g:244:13: '-'
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
            // Ctrl.g:245:11: ( '\"' )
            // Ctrl.g:245:13: '\"'
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
            // Ctrl.g:246:11: ( '\\\\' )
            // Ctrl.g:246:13: '\\\\'
            {
                match('\\');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "BSLASH"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:247:11: ( ',' )
            // Ctrl.g:247:13: ','
            {
                match(',');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "COMMA"

    // $ANTLR start "SEMI"
    public final void mSEMI() throws RecognitionException {
        try {
            int _type = SEMI;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:248:11: ( ';' )
            // Ctrl.g:248:13: ';'
            {
                match(';');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "SEMI"

    // $ANTLR start "LPAR"
    public final void mLPAR() throws RecognitionException {
        try {
            int _type = LPAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:249:11: ( '(' )
            // Ctrl.g:249:13: '('
            {
                match('(');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "LPAR"

    // $ANTLR start "RPAR"
    public final void mRPAR() throws RecognitionException {
        try {
            int _type = RPAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:250:11: ( ')' )
            // Ctrl.g:250:13: ')'
            {
                match(')');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "RPAR"

    // $ANTLR start "LCURLY"
    public final void mLCURLY() throws RecognitionException {
        try {
            int _type = LCURLY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:251:11: ( '{' )
            // Ctrl.g:251:13: '{'
            {
                match('{');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "LCURLY"

    // $ANTLR start "RCURLY"
    public final void mRCURLY() throws RecognitionException {
        try {
            int _type = RCURLY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:252:11: ( '}' )
            // Ctrl.g:252:13: '}'
            {
                match('}');

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "RCURLY"

    // $ANTLR start "ML_COMMENT"
    public final void mML_COMMENT() throws RecognitionException {
        try {
            int _type = ML_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // Ctrl.g:254:12: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // Ctrl.g:254:14: '/*' ( options {greedy=false; } : . )* '*/'
            {
                match("/*");

                // Ctrl.g:254:19: ( options {greedy=false; } : . )*
                loop9: do {
                    int alt9 = 2;
                    int LA9_0 = this.input.LA(1);

                    if ((LA9_0 == '*')) {
                        int LA9_1 = this.input.LA(2);

                        if ((LA9_1 == '/')) {
                            alt9 = 2;
                        } else if (((LA9_1 >= '\u0000' && LA9_1 <= '.') || (LA9_1 >= '0' && LA9_1 <= '\uFFFF'))) {
                            alt9 = 1;
                        }

                    } else if (((LA9_0 >= '\u0000' && LA9_0 <= ')') || (LA9_0 >= '+' && LA9_0 <= '\uFFFF'))) {
                        alt9 = 1;
                    }

                    switch (alt9) {
                    case 1:
                        // Ctrl.g:254:47: .
                    {
                        matchAny();

                    }
                        break;

                    default:
                        break loop9;
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
            // Ctrl.g:255:12: ( '//' ( options {greedy=false; } : . )* '\\n' )
            // Ctrl.g:255:14: '//' ( options {greedy=false; } : . )* '\\n'
            {
                match("//");

                // Ctrl.g:255:19: ( options {greedy=false; } : . )*
                loop10: do {
                    int alt10 = 2;
                    int LA10_0 = this.input.LA(1);

                    if ((LA10_0 == '\n')) {
                        alt10 = 2;
                    } else if (((LA10_0 >= '\u0000' && LA10_0 <= '\t') || (LA10_0 >= '\u000B' && LA10_0 <= '\uFFFF'))) {
                        alt10 = 1;
                    }

                    switch (alt10) {
                    case 1:
                        // Ctrl.g:255:47: .
                    {
                        matchAny();

                    }
                        break;

                    default:
                        break loop10;
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
            // Ctrl.g:257:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // Ctrl.g:257:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
                // Ctrl.g:257:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
                int cnt11 = 0;
                loop11: do {
                    int alt11 = 2;
                    int LA11_0 = this.input.LA(1);

                    if (((LA11_0 >= '\t' && LA11_0 <= '\n') || LA11_0 == '\r' || LA11_0 == ' ')) {
                        alt11 = 1;
                    }

                    switch (alt11) {
                    case 1:
                        // Ctrl.g:
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
                        if (cnt11 >= 1) {
                            break loop11;
                        }
                        EarlyExitException eee =
                            new EarlyExitException(11, this.input);
                        throw eee;
                    }
                    cnt11++;
                } while (true);

                _channel = HIDDEN;

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // Ctrl.g:1:8: ( ALAP | ANY | BOOL | CHOICE | DO | ELSE | FALSE | FUNCTION | IF | INT | NODE | OR | OTHER | OUT | REAL | STAR | STRING | TRY | TRUE | UNTIL | WHILE | INT_LIT | REAL_LIT | STRING_LIT | ID | AMP | DOT | NOT | BAR | SHARP | PLUS | ASTERISK | DONT_CARE | MINUS | QUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS )
        int alt12 = 45;
        alt12 = this.dfa12.predict(this.input);
        switch (alt12) {
        case 1:
            // Ctrl.g:1:10: ALAP
        {
            mALAP();

        }
            break;
        case 2:
            // Ctrl.g:1:15: ANY
        {
            mANY();

        }
            break;
        case 3:
            // Ctrl.g:1:19: BOOL
        {
            mBOOL();

        }
            break;
        case 4:
            // Ctrl.g:1:24: CHOICE
        {
            mCHOICE();

        }
            break;
        case 5:
            // Ctrl.g:1:31: DO
        {
            mDO();

        }
            break;
        case 6:
            // Ctrl.g:1:34: ELSE
        {
            mELSE();

        }
            break;
        case 7:
            // Ctrl.g:1:39: FALSE
        {
            mFALSE();

        }
            break;
        case 8:
            // Ctrl.g:1:45: FUNCTION
        {
            mFUNCTION();

        }
            break;
        case 9:
            // Ctrl.g:1:54: IF
        {
            mIF();

        }
            break;
        case 10:
            // Ctrl.g:1:57: INT
        {
            mINT();

        }
            break;
        case 11:
            // Ctrl.g:1:61: NODE
        {
            mNODE();

        }
            break;
        case 12:
            // Ctrl.g:1:66: OR
        {
            mOR();

        }
            break;
        case 13:
            // Ctrl.g:1:69: OTHER
        {
            mOTHER();

        }
            break;
        case 14:
            // Ctrl.g:1:75: OUT
        {
            mOUT();

        }
            break;
        case 15:
            // Ctrl.g:1:79: REAL
        {
            mREAL();

        }
            break;
        case 16:
            // Ctrl.g:1:84: STAR
        {
            mSTAR();

        }
            break;
        case 17:
            // Ctrl.g:1:89: STRING
        {
            mSTRING();

        }
            break;
        case 18:
            // Ctrl.g:1:96: TRY
        {
            mTRY();

        }
            break;
        case 19:
            // Ctrl.g:1:100: TRUE
        {
            mTRUE();

        }
            break;
        case 20:
            // Ctrl.g:1:105: UNTIL
        {
            mUNTIL();

        }
            break;
        case 21:
            // Ctrl.g:1:111: WHILE
        {
            mWHILE();

        }
            break;
        case 22:
            // Ctrl.g:1:117: INT_LIT
        {
            mINT_LIT();

        }
            break;
        case 23:
            // Ctrl.g:1:125: REAL_LIT
        {
            mREAL_LIT();

        }
            break;
        case 24:
            // Ctrl.g:1:134: STRING_LIT
        {
            mSTRING_LIT();

        }
            break;
        case 25:
            // Ctrl.g:1:145: ID
        {
            mID();

        }
            break;
        case 26:
            // Ctrl.g:1:148: AMP
        {
            mAMP();

        }
            break;
        case 27:
            // Ctrl.g:1:152: DOT
        {
            mDOT();

        }
            break;
        case 28:
            // Ctrl.g:1:156: NOT
        {
            mNOT();

        }
            break;
        case 29:
            // Ctrl.g:1:160: BAR
        {
            mBAR();

        }
            break;
        case 30:
            // Ctrl.g:1:164: SHARP
        {
            mSHARP();

        }
            break;
        case 31:
            // Ctrl.g:1:170: PLUS
        {
            mPLUS();

        }
            break;
        case 32:
            // Ctrl.g:1:175: ASTERISK
        {
            mASTERISK();

        }
            break;
        case 33:
            // Ctrl.g:1:184: DONT_CARE
        {
            mDONT_CARE();

        }
            break;
        case 34:
            // Ctrl.g:1:194: MINUS
        {
            mMINUS();

        }
            break;
        case 35:
            // Ctrl.g:1:200: QUOTE
        {
            mQUOTE();

        }
            break;
        case 36:
            // Ctrl.g:1:206: BSLASH
        {
            mBSLASH();

        }
            break;
        case 37:
            // Ctrl.g:1:213: COMMA
        {
            mCOMMA();

        }
            break;
        case 38:
            // Ctrl.g:1:219: SEMI
        {
            mSEMI();

        }
            break;
        case 39:
            // Ctrl.g:1:224: LPAR
        {
            mLPAR();

        }
            break;
        case 40:
            // Ctrl.g:1:229: RPAR
        {
            mRPAR();

        }
            break;
        case 41:
            // Ctrl.g:1:234: LCURLY
        {
            mLCURLY();

        }
            break;
        case 42:
            // Ctrl.g:1:241: RCURLY
        {
            mRCURLY();

        }
            break;
        case 43:
            // Ctrl.g:1:248: ML_COMMENT
        {
            mML_COMMENT();

        }
            break;
        case 44:
            // Ctrl.g:1:259: SL_COMMENT
        {
            mSL_COMMENT();

        }
            break;
        case 45:
            // Ctrl.g:1:270: WS
        {
            mWS();

        }
            break;

        }

    }

    protected DFA12 dfa12 = new DFA12(this);
    static final String DFA12_eotS =
        "\1\uffff\16\23\2\70\1\73\1\74\22\uffff\4\23\1\104\3\23\1\110\2"
            + "\23\1\113\7\23\2\uffff\1\70\5\uffff\1\23\1\126\2\23\1\uffff\3\23"
            + "\1\uffff\1\134\1\23\1\uffff\1\23\1\137\3\23\1\143\3\23\1\147\1\uffff"
            + "\1\150\1\23\1\152\2\23\1\uffff\1\155\1\23\1\uffff\1\157\1\23\1\161"
            + "\1\uffff\1\162\2\23\2\uffff\1\23\1\uffff\1\166\1\23\1\uffff\1\170"
            + "\1\uffff\1\23\2\uffff\1\172\1\173\1\174\1\uffff\1\23\1\uffff\1\176"
            + "\3\uffff\1\23\1\uffff\1\u0080\1\uffff";
    static final String DFA12_eofS = "\u0081\uffff";
    static final String DFA12_minS =
        "\1\11\1\154\1\157\1\150\1\157\1\154\1\141\1\146\1\157\1\162\1\145"
            + "\1\164\1\162\1\156\1\150\2\56\1\60\1\0\20\uffff\1\52\1\uffff\1\141"
            + "\1\171\2\157\1\55\1\163\1\154\1\156\1\55\1\164\1\144\1\55\1\150"
            + "\1\164\2\141\1\165\1\164\1\151\2\uffff\1\56\5\uffff\1\160\1\55\1"
            + "\154\1\151\1\uffff\1\145\1\163\1\143\1\uffff\1\55\1\145\1\uffff"
            + "\1\145\1\55\1\154\1\151\1\162\1\55\1\145\1\151\1\154\1\55\1\uffff"
            + "\1\55\1\143\1\55\1\145\1\164\1\uffff\1\55\1\162\1\uffff\1\55\1\156"
            + "\1\55\1\uffff\1\55\1\154\1\145\2\uffff\1\145\1\uffff\1\55\1\151"
            + "\1\uffff\1\55\1\uffff\1\147\2\uffff\3\55\1\uffff\1\157\1\uffff\1"
            + "\55\3\uffff\1\156\1\uffff\1\55\1\uffff";
    static final String DFA12_maxS =
        "\1\175\1\156\1\157\1\150\1\157\1\154\1\165\1\156\1\157\1\165\1"
            + "\145\1\164\1\162\1\156\1\150\3\71\1\uffff\20\uffff\1\57\1\uffff"
            + "\1\141\1\171\2\157\1\172\1\163\1\154\1\156\1\172\1\164\1\144\1\172"
            + "\1\150\1\164\1\141\1\162\1\171\1\164\1\151\2\uffff\1\71\5\uffff"
            + "\1\160\1\172\1\154\1\151\1\uffff\1\145\1\163\1\143\1\uffff\1\172"
            + "\1\145\1\uffff\1\145\1\172\1\154\1\151\1\162\1\172\1\145\1\151\1"
            + "\154\1\172\1\uffff\1\172\1\143\1\172\1\145\1\164\1\uffff\1\172\1"
            + "\162\1\uffff\1\172\1\156\1\172\1\uffff\1\172\1\154\1\145\2\uffff"
            + "\1\145\1\uffff\1\172\1\151\1\uffff\1\172\1\uffff\1\147\2\uffff\3"
            + "\172\1\uffff\1\157\1\uffff\1\172\3\uffff\1\156\1\uffff\1\172\1\uffff";
    static final String DFA12_acceptS =
        "\23\uffff\1\31\1\32\1\34\1\35\1\36\1\37\1\40\1\41\1\42\1\44\1\45"
            + "\1\46\1\47\1\50\1\51\1\52\1\uffff\1\55\23\uffff\1\26\1\27\1\uffff"
            + "\1\33\1\43\1\30\1\53\1\54\4\uffff\1\5\3\uffff\1\11\2\uffff\1\14"
            + "\12\uffff\1\2\5\uffff\1\12\2\uffff\1\16\3\uffff\1\22\3\uffff\1\1"
            + "\1\3\1\uffff\1\6\2\uffff\1\13\1\uffff\1\17\1\uffff\1\21\1\23\3\uffff"
            + "\1\7\1\uffff\1\15\1\uffff\1\24\1\25\1\4\1\uffff\1\20\1\uffff\1\10";
    static final String DFA12_specialS = "\22\uffff\1\0\156\uffff}>";
    static final String[] DFA12_transitionS =
        {
            "\2\44\2\uffff\1\44\22\uffff\1\44\1\25\1\22\1\27\2\uffff\1\24"
                + "\1\uffff\1\37\1\40\1\31\1\30\1\35\1\33\1\21\1\43\1\17\11\20"
                + "\1\uffff\1\36\5\uffff\32\23\1\uffff\1\34\2\uffff\1\32\1\uffff"
                + "\1\1\1\2\1\3\1\4\1\5\1\6\2\23\1\7\4\23\1\10\1\11\2\23\1\12\1"
                + "\13\1\14\1\15\1\23\1\16\3\23\1\41\1\26\1\42",
            "\1\45\1\uffff\1\46",
            "\1\47",
            "\1\50",
            "\1\51",
            "\1\52",
            "\1\53\23\uffff\1\54",
            "\1\55\7\uffff\1\56",
            "\1\57",
            "\1\60\1\uffff\1\61\1\62",
            "\1\63",
            "\1\64",
            "\1\65",
            "\1\66",
            "\1\67",
            "\1\71\1\uffff\12\71",
            "\1\71\1\uffff\12\72",
            "\12\71",
            "\12\75\1\uffff\2\75\1\uffff\ufff2\75",
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
            "\1\76\4\uffff\1\77",
            "",
            "\1\100",
            "\1\101",
            "\1\102",
            "\1\103",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "\1\105",
            "\1\106",
            "\1\107",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "\1\111",
            "\1\112",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "\1\114",
            "\1\115",
            "\1\116",
            "\1\120\20\uffff\1\117",
            "\1\122\3\uffff\1\121",
            "\1\123",
            "\1\124",
            "",
            "",
            "\1\71\1\uffff\12\72",
            "",
            "",
            "",
            "",
            "",
            "\1\125",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "\1\127",
            "\1\130",
            "",
            "\1\131",
            "\1\132",
            "\1\133",
            "",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "\1\135",
            "",
            "\1\136",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "\1\140",
            "\1\141",
            "\1\142",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "\1\144",
            "\1\145",
            "\1\146",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "\1\151",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "\1\153",
            "\1\154",
            "",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "\1\156",
            "",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "\1\160",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "\1\163",
            "\1\164",
            "",
            "",
            "\1\165",
            "",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "\1\167",
            "",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "",
            "\1\171",
            "",
            "",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "",
            "\1\175",
            "",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            "",
            "",
            "",
            "\1\177",
            "",
            "\1\23\2\uffff\12\23\7\uffff\32\23\4\uffff\1\23\1\uffff\32" + "\23",
            ""};

    static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
    static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
    static final char[] DFA12_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
    static final char[] DFA12_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
    static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
    static final short[] DFA12_special =
        DFA.unpackEncodedString(DFA12_specialS);
    static final short[][] DFA12_transition;

    static {
        int numStates = DFA12_transitionS.length;
        DFA12_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
        }
    }

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = DFA12_eot;
            this.eof = DFA12_eof;
            this.min = DFA12_min;
            this.max = DFA12_max;
            this.accept = DFA12_accept;
            this.special = DFA12_special;
            this.transition = DFA12_transition;
        }

        public String getDescription() {
            return "1:1: Tokens : ( ALAP | ANY | BOOL | CHOICE | DO | ELSE | FALSE | FUNCTION | IF | INT | NODE | OR | OTHER | OUT | REAL | STAR | STRING | TRY | TRUE | UNTIL | WHILE | INT_LIT | REAL_LIT | STRING_LIT | ID | AMP | DOT | NOT | BAR | SHARP | PLUS | ASTERISK | DONT_CARE | MINUS | QUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS );";
        }

        public int specialStateTransition(int s, IntStream _input)
            throws NoViableAltException {
            IntStream input = _input;
            int _s = s;
            switch (s) {
            case 0:
                int LA12_18 = input.LA(1);

                s = -1;
                if (((LA12_18 >= '\u0000' && LA12_18 <= '\t')
                    || (LA12_18 >= '\u000B' && LA12_18 <= '\f') || (LA12_18 >= '\u000E' && LA12_18 <= '\uFFFF'))) {
                    s = 61;
                } else {
                    s = 60;
                }

                if (s >= 0) {
                    return s;
                }
                break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 12, _s, input);
            error(nvae);
            throw nvae;
        }
    }

}