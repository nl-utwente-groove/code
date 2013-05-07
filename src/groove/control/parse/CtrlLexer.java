// $ANTLR 3.3 Nov 30, 2010 12:45:30 D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g 2013-05-07 17:43:37

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
    public static final int EOF = -1;
    public static final int RECIPES = 4;
    public static final int ARG = 5;
    public static final int ARGS = 6;
    public static final int BLOCK = 7;
    public static final int CALL = 8;
    public static final int DO_WHILE = 9;
    public static final int DO_UNTIL = 10;
    public static final int FUNCTIONS = 11;
    public static final int PROGRAM = 12;
    public static final int VAR = 13;
    public static final int PACKAGE = 14;
    public static final int SEMI = 15;
    public static final int IMPORT = 16;
    public static final int ID = 17;
    public static final int DOT = 18;
    public static final int RECIPE = 19;
    public static final int LPAR = 20;
    public static final int RPAR = 21;
    public static final int FUNCTION = 22;
    public static final int LCURLY = 23;
    public static final int RCURLY = 24;
    public static final int ALAP = 25;
    public static final int WHILE = 26;
    public static final int UNTIL = 27;
    public static final int DO = 28;
    public static final int IF = 29;
    public static final int ELSE = 30;
    public static final int TRY = 31;
    public static final int CHOICE = 32;
    public static final int OR = 33;
    public static final int BAR = 34;
    public static final int TRUE = 35;
    public static final int PLUS = 36;
    public static final int ASTERISK = 37;
    public static final int SHARP = 38;
    public static final int ANY = 39;
    public static final int OTHER = 40;
    public static final int COMMA = 41;
    public static final int OUT = 42;
    public static final int DONT_CARE = 43;
    public static final int FALSE = 44;
    public static final int STRING_LIT = 45;
    public static final int INT_LIT = 46;
    public static final int REAL_LIT = 47;
    public static final int NODE = 48;
    public static final int BOOL = 49;
    public static final int STRING = 50;
    public static final int INT = 51;
    public static final int REAL = 52;
    public static final int PRIORITY = 53;
    public static final int STAR = 54;
    public static final int IntegerNumber = 55;
    public static final int NonIntegerNumber = 56;
    public static final int QUOTE = 57;
    public static final int EscapeSequence = 58;
    public static final int BSLASH = 59;
    public static final int AMP = 60;
    public static final int NOT = 61;
    public static final int MINUS = 62;
    public static final int ML_COMMENT = 63;
    public static final int SL_COMMENT = 64;
    public static final int WS = 65;

    /** Line number of the first line of a control fragment. */
    private int startLine;
    /** Last token read when start position is recorded. */
    private String lastToken;
    /** Start position of a recorded substring of the input. */
    private int recordPos;
    /** Helper class to convert AST trees to namespace. */
    private CtrlHelper helper;

    /** Starts recording the input string. */
    public void startRecord() {
        this.lastToken = this.state.token.getText();
        this.recordPos = getCharIndex();
        this.startLine = this.state.token.getLine();
    }

    public CtrlFragment getRecord() {
        return new CtrlFragment(this.helper.getControlName(), this.startLine);
    }

    public void setHelper(CtrlHelper helper) {
        this.helper = helper;
    }

    public void displayRecognitionError(String[] tokenNames,
            RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        this.helper.addError(hdr + " " + msg, e.line, e.charPositionInLine);
    }

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
        return "D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g";
    }

    // $ANTLR start "ALAP"
    public final void mALAP() throws RecognitionException {
        try {
            int _type = ALAP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:394:10: ( 'alap' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:394:12: 'alap'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:395:9: ( 'any' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:395:11: 'any'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:396:10: ( 'bool' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:396:12: 'bool'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:397:10: ( 'choice' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:397:12: 'choice'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:398:10: ( 'do' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:398:12: 'do'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:399:10: ( 'else' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:399:12: 'else'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:400:10: ( 'false' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:400:12: 'false'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:401:10: ( 'function' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:401:12: 'function'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:402:10: ( 'if' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:402:12: 'if'
            {
                match("if");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "IF"

    // $ANTLR start "IMPORT"
    public final void mIMPORT() throws RecognitionException {
        try {
            int _type = IMPORT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:403:10: ( 'import' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:403:12: 'import'
            {
                match("import");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "IMPORT"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:404:10: ( 'int' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:404:12: 'int'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:405:10: ( 'node' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:405:12: 'node'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:406:10: ( 'or' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:406:12: 'or'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:407:10: ( 'other' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:407:12: 'other'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:408:10: ( 'out' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:408:12: 'out'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:409:10: ( 'real' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:409:12: 'real'
            {
                match("real");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "REAL"

    // $ANTLR start "PACKAGE"
    public final void mPACKAGE() throws RecognitionException {
        try {
            int _type = PACKAGE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:410:10: ( 'package' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:410:12: 'package'
            {
                match("package");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "PACKAGE"

    // $ANTLR start "PRIORITY"
    public final void mPRIORITY() throws RecognitionException {
        try {
            int _type = PRIORITY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:411:10: ( 'priority' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:411:12: 'priority'
            {
                match("priority");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "PRIORITY"

    // $ANTLR start "RECIPE"
    public final void mRECIPE() throws RecognitionException {
        try {
            int _type = RECIPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:412:10: ( 'recipe' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:412:12: 'recipe'
            {
                match("recipe");

            }

            this.state.type = _type;
            this.state.channel = _channel;
        } finally {
        }
    }

    // $ANTLR end "RECIPE"

    // $ANTLR start "STAR"
    public final void mSTAR() throws RecognitionException {
        try {
            int _type = STAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:413:10: ( 'star' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:413:12: 'star'
            {
                match("star");

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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:414:10: ( 'string' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:414:12: 'string'
            {
                match("string");

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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:415:10: ( 'try' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:415:12: 'try'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:416:10: ( 'true' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:416:12: 'true'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:417:10: ( 'until' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:417:12: 'until'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:418:10: ( 'while' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:418:12: 'while'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:421:3: ( IntegerNumber )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:421:5: IntegerNumber
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:426:3: ( '0' | '1' .. '9' ( '0' .. '9' )* )
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:426:5: '0'
            {
                match('0');

            }
                break;
            case 2:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:427:5: '1' .. '9' ( '0' .. '9' )*
            {
                matchRange('1', '9');
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:427:14: ( '0' .. '9' )*
                loop1: do {
                    int alt1 = 2;
                    int LA1_0 = this.input.LA(1);

                    if (((LA1_0 >= '0' && LA1_0 <= '9'))) {
                        alt1 = 1;
                    }

                    switch (alt1) {
                    case 1:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:427:15: '0' .. '9'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:431:3: ( NonIntegerNumber )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:431:5: NonIntegerNumber
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:436:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* | '.' ( '0' .. '9' )+ )
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:436:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )*
            {
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:436:9: ( '0' .. '9' )+
                int cnt3 = 0;
                loop3: do {
                    int alt3 = 2;
                    int LA3_0 = this.input.LA(1);

                    if (((LA3_0 >= '0' && LA3_0 <= '9'))) {
                        alt3 = 1;
                    }

                    switch (alt3) {
                    case 1:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:436:10: '0' .. '9'
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
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:436:27: ( '0' .. '9' )*
                loop4: do {
                    int alt4 = 2;
                    int LA4_0 = this.input.LA(1);

                    if (((LA4_0 >= '0' && LA4_0 <= '9'))) {
                        alt4 = 1;
                    }

                    switch (alt4) {
                    case 1:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:436:28: '0' .. '9'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:437:9: '.' ( '0' .. '9' )+
            {
                match('.');
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:437:13: ( '0' .. '9' )+
                int cnt5 = 0;
                loop5: do {
                    int alt5 = 2;
                    int LA5_0 = this.input.LA(1);

                    if (((LA5_0 >= '0' && LA5_0 <= '9'))) {
                        alt5 = 1;
                    }

                    switch (alt5) {
                    case 1:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:437:15: '0' .. '9'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:442:3: ( QUOTE ( EscapeSequence | ~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:442:5: QUOTE ( EscapeSequence | ~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )* QUOTE
            {
                mQUOTE();
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:443:5: ( EscapeSequence | ~ ( BSLASH | QUOTE | '\\r' | '\\n' ) )*
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
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:443:7: EscapeSequence
                    {
                        mEscapeSequence();

                    }
                        break;
                    case 2:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:444:7: ~ ( BSLASH | QUOTE | '\\r' | '\\n' )
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:451:3: ( BSLASH ( QUOTE BSLASH ) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:451:5: BSLASH ( QUOTE BSLASH )
            {
                mBSLASH();
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:452:5: ( QUOTE BSLASH )
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:452:7: QUOTE BSLASH
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:457:5: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )* )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:457:7: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
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

                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:457:27: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
                loop8: do {
                    int alt8 = 2;
                    int LA8_0 = this.input.LA(1);

                    if ((LA8_0 == '-' || (LA8_0 >= '0' && LA8_0 <= '9')
                        || (LA8_0 >= 'A' && LA8_0 <= 'Z') || LA8_0 == '_' || (LA8_0 >= 'a' && LA8_0 <= 'z'))) {
                        alt8 = 1;
                    }

                    switch (alt8) {
                    case 1:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:459:11: ( '&' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:459:13: '&'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:460:11: ( '.' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:460:13: '.'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:461:11: ( '!' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:461:13: '!'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:462:11: ( '|' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:462:13: '|'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:463:11: ( '#' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:463:13: '#'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:464:11: ( '+' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:464:13: '+'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:465:11: ( '*' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:465:13: '*'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:466:11: ( '_' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:466:13: '_'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:467:11: ( '-' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:467:13: '-'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:468:11: ( '\"' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:468:13: '\"'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:469:11: ( '\\\\' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:469:13: '\\\\'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:470:11: ( ',' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:470:13: ','
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:471:11: ( ';' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:471:13: ';'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:472:11: ( '(' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:472:13: '('
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:473:11: ( ')' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:473:13: ')'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:474:11: ( '{' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:474:13: '{'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:475:11: ( '}' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:475:13: '}'
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:477:12: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:477:14: '/*' ( options {greedy=false; } : . )* '*/'
            {
                match("/*");

                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:477:19: ( options {greedy=false; } : . )*
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
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:477:47: .
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:478:12: ( '//' ( options {greedy=false; } : . )* '\\n' )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:478:14: '//' ( options {greedy=false; } : . )* '\\n'
            {
                match("//");

                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:478:19: ( options {greedy=false; } : . )*
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
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:478:47: .
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:480:5: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:480:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:480:9: ( ' ' | '\\t' | '\\r' | '\\n' )+
                int cnt11 = 0;
                loop11: do {
                    int alt11 = 2;
                    int LA11_0 = this.input.LA(1);

                    if (((LA11_0 >= '\t' && LA11_0 <= '\n') || LA11_0 == '\r' || LA11_0 == ' ')) {
                        alt11 = 1;
                    }

                    switch (alt11) {
                    case 1:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
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
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:8: ( ALAP | ANY | BOOL | CHOICE | DO | ELSE | FALSE | FUNCTION | IF | IMPORT | INT | NODE | OR | OTHER | OUT | REAL | PACKAGE | PRIORITY | RECIPE | STAR | STRING | TRY | TRUE | UNTIL | WHILE | INT_LIT | REAL_LIT | STRING_LIT | ID | AMP | DOT | NOT | BAR | SHARP | PLUS | ASTERISK | DONT_CARE | MINUS | QUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS )
        int alt12 = 49;
        alt12 = this.dfa12.predict(this.input);
        switch (alt12) {
        case 1:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:10: ALAP
        {
            mALAP();

        }
            break;
        case 2:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:15: ANY
        {
            mANY();

        }
            break;
        case 3:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:19: BOOL
        {
            mBOOL();

        }
            break;
        case 4:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:24: CHOICE
        {
            mCHOICE();

        }
            break;
        case 5:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:31: DO
        {
            mDO();

        }
            break;
        case 6:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:34: ELSE
        {
            mELSE();

        }
            break;
        case 7:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:39: FALSE
        {
            mFALSE();

        }
            break;
        case 8:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:45: FUNCTION
        {
            mFUNCTION();

        }
            break;
        case 9:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:54: IF
        {
            mIF();

        }
            break;
        case 10:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:57: IMPORT
        {
            mIMPORT();

        }
            break;
        case 11:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:64: INT
        {
            mINT();

        }
            break;
        case 12:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:68: NODE
        {
            mNODE();

        }
            break;
        case 13:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:73: OR
        {
            mOR();

        }
            break;
        case 14:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:76: OTHER
        {
            mOTHER();

        }
            break;
        case 15:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:82: OUT
        {
            mOUT();

        }
            break;
        case 16:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:86: REAL
        {
            mREAL();

        }
            break;
        case 17:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:91: PACKAGE
        {
            mPACKAGE();

        }
            break;
        case 18:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:99: PRIORITY
        {
            mPRIORITY();

        }
            break;
        case 19:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:108: RECIPE
        {
            mRECIPE();

        }
            break;
        case 20:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:115: STAR
        {
            mSTAR();

        }
            break;
        case 21:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:120: STRING
        {
            mSTRING();

        }
            break;
        case 22:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:127: TRY
        {
            mTRY();

        }
            break;
        case 23:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:131: TRUE
        {
            mTRUE();

        }
            break;
        case 24:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:136: UNTIL
        {
            mUNTIL();

        }
            break;
        case 25:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:142: WHILE
        {
            mWHILE();

        }
            break;
        case 26:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:148: INT_LIT
        {
            mINT_LIT();

        }
            break;
        case 27:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:156: REAL_LIT
        {
            mREAL_LIT();

        }
            break;
        case 28:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:165: STRING_LIT
        {
            mSTRING_LIT();

        }
            break;
        case 29:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:176: ID
        {
            mID();

        }
            break;
        case 30:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:179: AMP
        {
            mAMP();

        }
            break;
        case 31:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:183: DOT
        {
            mDOT();

        }
            break;
        case 32:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:187: NOT
        {
            mNOT();

        }
            break;
        case 33:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:191: BAR
        {
            mBAR();

        }
            break;
        case 34:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:195: SHARP
        {
            mSHARP();

        }
            break;
        case 35:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:201: PLUS
        {
            mPLUS();

        }
            break;
        case 36:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:206: ASTERISK
        {
            mASTERISK();

        }
            break;
        case 37:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:215: DONT_CARE
        {
            mDONT_CARE();

        }
            break;
        case 38:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:225: MINUS
        {
            mMINUS();

        }
            break;
        case 39:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:231: QUOTE
        {
            mQUOTE();

        }
            break;
        case 40:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:237: BSLASH
        {
            mBSLASH();

        }
            break;
        case 41:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:244: COMMA
        {
            mCOMMA();

        }
            break;
        case 42:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:250: SEMI
        {
            mSEMI();

        }
            break;
        case 43:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:255: LPAR
        {
            mLPAR();

        }
            break;
        case 44:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:260: RPAR
        {
            mRPAR();

        }
            break;
        case 45:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:265: LCURLY
        {
            mLCURLY();

        }
            break;
        case 46:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:272: RCURLY
        {
            mRCURLY();

        }
            break;
        case 47:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:279: ML_COMMENT
        {
            mML_COMMENT();

        }
            break;
        case 48:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:290: SL_COMMENT
        {
            mSL_COMMENT();

        }
            break;
        case 49:
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:1:301: WS
        {
            mWS();

        }
            break;

        }

    }

    protected DFA12 dfa12 = new DFA12(this);
    static final String DFA12_eotS =
        "\1\uffff\17\24\2\74\1\77\1\100\22\uffff\4\24\1\110\3\24\1\114\3"
            + "\24\1\120\11\24\2\uffff\1\74\5\uffff\1\24\1\136\2\24\1\uffff\3\24"
            + "\1\uffff\1\24\1\145\1\24\1\uffff\1\24\1\150\6\24\1\157\3\24\1\163"
            + "\1\uffff\1\164\1\24\1\166\3\24\1\uffff\1\172\1\24\1\uffff\1\174"
            + "\3\24\1\u0080\1\24\1\uffff\1\u0082\2\24\2\uffff\1\24\1\uffff\1\u0086"
            + "\2\24\1\uffff\1\u0089\1\uffff\3\24\1\uffff\1\24\1\uffff\1\u008e"
            + "\1\u008f\1\u0090\1\uffff\1\24\1\u0092\1\uffff\1\u0093\2\24\1\u0096"
            + "\3\uffff\1\24\2\uffff\1\u0098\1\24\1\uffff\1\u009a\1\uffff\1\u009b"
            + "\2\uffff";
    static final String DFA12_eofS = "\u009c\uffff";
    static final String DFA12_minS =
        "\1\11\1\154\1\157\1\150\1\157\1\154\1\141\1\146\1\157\1\162\1\145"
            + "\1\141\1\164\1\162\1\156\1\150\2\56\1\60\1\0\20\uffff\1\52\1\uffff"
            + "\1\141\1\171\2\157\1\55\1\163\1\154\1\156\1\55\1\160\1\164\1\144"
            + "\1\55\1\150\1\164\1\141\1\143\1\151\1\141\1\165\1\164\1\151\2\uffff"
            + "\1\56\5\uffff\1\160\1\55\1\154\1\151\1\uffff\1\145\1\163\1\143\1"
            + "\uffff\1\157\1\55\1\145\1\uffff\1\145\1\55\1\154\1\151\1\153\1\157"
            + "\1\162\1\151\1\55\1\145\1\151\1\154\1\55\1\uffff\1\55\1\143\1\55"
            + "\1\145\1\164\1\162\1\uffff\1\55\1\162\1\uffff\1\55\1\160\1\141\1"
            + "\162\1\55\1\156\1\uffff\1\55\1\154\1\145\2\uffff\1\145\1\uffff\1"
            + "\55\1\151\1\164\1\uffff\1\55\1\uffff\1\145\1\147\1\151\1\uffff\1"
            + "\147\1\uffff\3\55\1\uffff\1\157\1\55\1\uffff\1\55\1\145\1\164\1"
            + "\55\3\uffff\1\156\2\uffff\1\55\1\171\1\uffff\1\55\1\uffff\1\55\2"
            + "\uffff";
    static final String DFA12_maxS =
        "\1\175\1\156\1\157\1\150\1\157\1\154\1\165\1\156\1\157\1\165\1"
            + "\145\1\162\1\164\1\162\1\156\1\150\3\71\1\uffff\20\uffff\1\57\1"
            + "\uffff\1\141\1\171\2\157\1\172\1\163\1\154\1\156\1\172\1\160\1\164"
            + "\1\144\1\172\1\150\1\164\2\143\1\151\1\162\1\171\1\164\1\151\2\uffff"
            + "\1\71\5\uffff\1\160\1\172\1\154\1\151\1\uffff\1\145\1\163\1\143"
            + "\1\uffff\1\157\1\172\1\145\1\uffff\1\145\1\172\1\154\1\151\1\153"
            + "\1\157\1\162\1\151\1\172\1\145\1\151\1\154\1\172\1\uffff\1\172\1"
            + "\143\1\172\1\145\1\164\1\162\1\uffff\1\172\1\162\1\uffff\1\172\1"
            + "\160\1\141\1\162\1\172\1\156\1\uffff\1\172\1\154\1\145\2\uffff\1"
            + "\145\1\uffff\1\172\1\151\1\164\1\uffff\1\172\1\uffff\1\145\1\147"
            + "\1\151\1\uffff\1\147\1\uffff\3\172\1\uffff\1\157\1\172\1\uffff\1"
            + "\172\1\145\1\164\1\172\3\uffff\1\156\2\uffff\1\172\1\171\1\uffff"
            + "\1\172\1\uffff\1\172\2\uffff";
    static final String DFA12_acceptS =
        "\24\uffff\1\35\1\36\1\40\1\41\1\42\1\43\1\44\1\45\1\46\1\50\1\51"
            + "\1\52\1\53\1\54\1\55\1\56\1\uffff\1\61\26\uffff\1\32\1\33\1\uffff"
            + "\1\37\1\47\1\34\1\57\1\60\4\uffff\1\5\3\uffff\1\11\3\uffff\1\15"
            + "\15\uffff\1\2\6\uffff\1\13\2\uffff\1\17\6\uffff\1\26\3\uffff\1\1"
            + "\1\3\1\uffff\1\6\3\uffff\1\14\1\uffff\1\20\3\uffff\1\24\1\uffff"
            + "\1\27\3\uffff\1\7\2\uffff\1\16\4\uffff\1\30\1\31\1\4\1\uffff\1\12"
            + "\1\23\2\uffff\1\25\1\uffff\1\21\1\uffff\1\10\1\22";
    static final String DFA12_specialS = "\23\uffff\1\0\u0088\uffff}>";
    static final String[] DFA12_transitionS = {
        "\2\45\2\uffff\1\45\22\uffff\1\45\1\26\1\23\1\30\2\uffff\1\25"
            + "\1\uffff\1\40\1\41\1\32\1\31\1\36\1\34\1\22\1\44\1\20\11\21"
            + "\1\uffff\1\37\5\uffff\32\24\1\uffff\1\35\2\uffff\1\33\1\uffff"
            + "\1\1\1\2\1\3\1\4\1\5\1\6\2\24\1\7\4\24\1\10\1\11\1\13\1\24\1"
            + "\12\1\14\1\15\1\16\1\24\1\17\3\24\1\42\1\27\1\43",
        "\1\46\1\uffff\1\47", "\1\50", "\1\51", "\1\52", "\1\53",
        "\1\54\23\uffff\1\55", "\1\56\6\uffff\1\57\1\60", "\1\61",
        "\1\62\1\uffff\1\63\1\64", "\1\65", "\1\66\20\uffff\1\67", "\1\70",
        "\1\71", "\1\72", "\1\73", "\1\75\1\uffff\12\75",
        "\1\75\1\uffff\12\76", "\12\75",
        "\12\101\1\uffff\2\101\1\uffff\ufff2\101", "", "", "", "", "", "", "",
        "", "", "", "", "", "", "", "", "", "\1\102\4\uffff\1\103", "",
        "\1\104", "\1\105", "\1\106", "\1\107",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "\1\111", "\1\112", "\1\113",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "\1\115", "\1\116", "\1\117",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "\1\121", "\1\122", "\1\123\1\uffff\1\124", "\1\125", "\1\126",
        "\1\127\20\uffff\1\130", "\1\132\3\uffff\1\131", "\1\133", "\1\134",
        "", "", "\1\75\1\uffff\12\76", "", "", "", "", "", "\1\135",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "\1\137", "\1\140", "", "\1\141", "\1\142", "\1\143", "", "\1\144",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "\1\146", "", "\1\147",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "\1\151", "\1\152", "\1\153", "\1\154", "\1\155", "\1\156",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "\1\160", "\1\161", "\1\162",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "\1\165",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "\1\167", "\1\170", "\1\171", "",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "\1\173", "",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "\1\175", "\1\176", "\1\177",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "\1\u0081", "",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "\1\u0083", "\1\u0084", "", "", "\1\u0085", "",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "\1\u0087", "\1\u0088", "",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "", "\1\u008a", "\1\u008b", "\1\u008c", "", "\1\u008d", "",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "", "\1\u0091",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "\1\u0094", "\1\u0095",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "", "", "", "\1\u0097", "", "",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "\1\u0099", "",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "",
        "\1\24\2\uffff\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32" + "\24",
        "", ""};

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
            return "1:1: Tokens : ( ALAP | ANY | BOOL | CHOICE | DO | ELSE | FALSE | FUNCTION | IF | IMPORT | INT | NODE | OR | OTHER | OUT | REAL | PACKAGE | PRIORITY | RECIPE | STAR | STRING | TRY | TRUE | UNTIL | WHILE | INT_LIT | REAL_LIT | STRING_LIT | ID | AMP | DOT | NOT | BAR | SHARP | PLUS | ASTERISK | DONT_CARE | MINUS | QUOTE | BSLASH | COMMA | SEMI | LPAR | RPAR | LCURLY | RCURLY | ML_COMMENT | SL_COMMENT | WS );";
        }

        public int specialStateTransition(int s, IntStream _input)
            throws NoViableAltException {
            IntStream input = _input;
            int _s = s;
            switch (s) {
            case 0:
                int LA12_19 = input.LA(1);

                s = -1;
                if (((LA12_19 >= '\u0000' && LA12_19 <= '\t')
                    || (LA12_19 >= '\u000B' && LA12_19 <= '\f') || (LA12_19 >= '\u000E' && LA12_19 <= '\uFFFF'))) {
                    s = 65;
                } else {
                    s = 64;
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