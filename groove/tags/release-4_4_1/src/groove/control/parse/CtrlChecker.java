// $ANTLR 3.2 Sep 23, 2009 12:02:23 CtrlChecker.g 2011-01-12 17:03:05

package groove.control.parse;

import groove.algebra.AlgebraFamily;
import groove.control.CtrlAut;

import java.util.List;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.TreeAdaptor;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeParser;
import org.antlr.runtime.tree.TreeRuleReturnScope;

@SuppressWarnings("all")
public class CtrlChecker extends TreeParser {
    public static final String[] tokenNames = new String[] {"<invalid>",
        "<EOR>", "<DOWN>", "<UP>", "ARG", "ARGS", "BLOCK", "CALL", "DO_WHILE",
        "DO_UNTIL", "FUNCTION", "FUNCTIONS", "PROGRAM", "VAR", "LCURLY",
        "RCURLY", "ID", "LPAR", "RPAR", "ALAP", "WHILE", "UNTIL", "DO", "IF",
        "ELSE", "TRY", "CHOICE", "OR", "SEMI", "BAR", "TRUE", "PLUS",
        "ASTERISK", "SHARP", "ANY", "OTHER", "DOT", "COMMA", "NODE", "BOOL",
        "STRING", "INT", "REAL", "OUT", "DONT_CARE", "FALSE", "STRING_LIT",
        "INT_LIT", "REAL_LIT", "STAR", "IntegerNumber", "NonIntegerNumber",
        "QUOTE", "EscapeSequence", "BSLASH", "AMP", "NOT", "MINUS",
        "ML_COMMENT", "SL_COMMENT", "WS"};
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

    public CtrlChecker(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }

    public CtrlChecker(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);

    }

    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }

    public TreeAdaptor getTreeAdaptor() {
        return this.adaptor;
    }

    public String[] getTokenNames() {
        return CtrlChecker.tokenNames;
    }

    public String getGrammarFileName() {
        return "CtrlChecker.g";
    }

    /** Helper class to convert AST trees to namespace. */
    private CtrlHelper helper;

    public void displayRecognitionError(String[] tokenNames,
            RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        this.helper.addError(hdr + " " + msg);
    }

    public List<String> getErrors() {
        return this.helper.getErrors();
    }

    /**
     * Runs the lexer and parser on a given input character stream,
     * with a (presumably empty) namespace.
     * @return the resulting syntax tree
     */
    public MyTree run(MyTree tree, Namespace namespace, AlgebraFamily family)
        throws RecognitionException {
        this.helper = new CtrlHelper(this, namespace, family);
        MyTreeAdaptor treeAdaptor = new MyTreeAdaptor();
        setTreeAdaptor(treeAdaptor);
        setTreeNodeStream(treeAdaptor.createTreeNodeStream(tree));
        return (MyTree) program().getTree();
    }

    public static class program_return extends TreeRuleReturnScope {
        MyTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "program"
    // CtrlChecker.g:51:1: program : ^( PROGRAM functions block ) ;
    public final CtrlChecker.program_return program()
        throws RecognitionException {
        CtrlChecker.program_return retval = new CtrlChecker.program_return();
        retval.start = this.input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree PROGRAM1 = null;
        CtrlChecker.functions_return functions2 = null;

        CtrlChecker.block_return block3 = null;

        MyTree PROGRAM1_tree = null;

        try {
            // CtrlChecker.g:52:3: ( ^( PROGRAM functions block ) )
            // CtrlChecker.g:52:6: ^( PROGRAM functions block )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    PROGRAM1 =
                        (MyTree) match(this.input, PROGRAM,
                            FOLLOW_PROGRAM_in_program57);

                    if (_first_0 == null) {
                        _first_0 = PROGRAM1;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_functions_in_program59);
                    functions2 = functions();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = functions2.tree;
                    }
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_block_in_program61);
                    block3 = block();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = block3.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        } finally {
        }
        return retval;
    }

    // $ANTLR end "program"

    public static class functions_return extends TreeRuleReturnScope {
        MyTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "functions"
    // CtrlChecker.g:55:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final CtrlChecker.functions_return functions()
        throws RecognitionException {
        CtrlChecker.functions_return retval =
            new CtrlChecker.functions_return();
        retval.start = this.input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree FUNCTIONS4 = null;
        CtrlChecker.function_return function5 = null;

        MyTree FUNCTIONS4_tree = null;

        try {
            // CtrlChecker.g:56:3: ( ^( FUNCTIONS ( function )* ) )
            // CtrlChecker.g:56:5: ^( FUNCTIONS ( function )* )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    FUNCTIONS4 =
                        (MyTree) match(this.input, FUNCTIONS,
                            FOLLOW_FUNCTIONS_in_functions77);

                    if (_first_0 == null) {
                        _first_0 = FUNCTIONS4;
                    }
                    if (this.input.LA(1) == Token.DOWN) {
                        match(this.input, Token.DOWN, null);
                        // CtrlChecker.g:56:17: ( function )*
                        loop1: do {
                            int alt1 = 2;
                            int LA1_0 = this.input.LA(1);

                            if ((LA1_0 == FUNCTION)) {
                                alt1 = 1;
                            }

                            switch (alt1) {
                            case 1:
                                // CtrlChecker.g:56:17: function
                            {
                                _last = (MyTree) this.input.LT(1);
                                pushFollow(FOLLOW_function_in_functions79);
                                function5 = function();

                                this.state._fsp--;

                                if (_first_1 == null) {
                                    _first_1 = function5.tree;
                                }

                                retval.tree = (MyTree) _first_0;
                                if (this.adaptor.getParent(retval.tree) != null
                                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                    retval.tree =
                                        (MyTree) this.adaptor.getParent(retval.tree);
                                }
                            }
                                break;

                            default:
                                break loop1;
                            }
                        } while (true);

                        match(this.input, Token.UP, null);
                    }
                    _last = _save_last_1;
                }

                this.helper.reorderFunctions(FUNCTIONS4);

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        } finally {
        }
        return retval;
    }

    // $ANTLR end "functions"

    public static class function_return extends TreeRuleReturnScope {
        MyTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "function"
    // CtrlChecker.g:60:1: function : ^( FUNCTION ID block ) ;
    public final CtrlChecker.function_return function()
        throws RecognitionException {
        CtrlChecker.function_return retval = new CtrlChecker.function_return();
        retval.start = this.input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree FUNCTION6 = null;
        MyTree ID7 = null;
        CtrlChecker.block_return block8 = null;

        MyTree FUNCTION6_tree = null;
        MyTree ID7_tree = null;

        try {
            // CtrlChecker.g:61:3: ( ^( FUNCTION ID block ) )
            // CtrlChecker.g:61:5: ^( FUNCTION ID block )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    FUNCTION6 =
                        (MyTree) match(this.input, FUNCTION,
                            FOLLOW_FUNCTION_in_function102);

                    if (_first_0 == null) {
                        _first_0 = FUNCTION6;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    ID7 =
                        (MyTree) match(this.input, ID, FOLLOW_ID_in_function104);

                    if (_first_1 == null) {
                        _first_1 = ID7;
                    }
                    this.helper.startFunction(ID7);
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_block_in_function123);
                    block8 = block();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = block8.tree;
                    }
                    this.helper.endFunction();

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        } finally {
        }
        return retval;
    }

    // $ANTLR end "function"

    public static class block_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        MyTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "block"
    // CtrlChecker.g:68:1: block returns [ CtrlAut aut ] : ^( BLOCK ( stat )* ) ;
    public final CtrlChecker.block_return block() throws RecognitionException {
        CtrlChecker.block_return retval = new CtrlChecker.block_return();
        retval.start = this.input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree BLOCK9 = null;
        CtrlChecker.stat_return stat10 = null;

        MyTree BLOCK9_tree = null;

        try {
            // CtrlChecker.g:69:3: ( ^( BLOCK ( stat )* ) )
            // CtrlChecker.g:69:5: ^( BLOCK ( stat )* )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    BLOCK9 =
                        (MyTree) match(this.input, BLOCK,
                            FOLLOW_BLOCK_in_block161);

                    if (_first_0 == null) {
                        _first_0 = BLOCK9;
                    }
                    this.helper.openScope();

                    if (this.input.LA(1) == Token.DOWN) {
                        match(this.input, Token.DOWN, null);
                        // CtrlChecker.g:71:8: ( stat )*
                        loop2: do {
                            int alt2 = 2;
                            int LA2_0 = this.input.LA(1);

                            if (((LA2_0 >= BLOCK && LA2_0 <= CALL)
                                || LA2_0 == VAR
                                || (LA2_0 >= ALAP && LA2_0 <= UNTIL)
                                || LA2_0 == IF
                                || (LA2_0 >= TRY && LA2_0 <= CHOICE)
                                || LA2_0 == TRUE
                                || (LA2_0 >= ANY && LA2_0 <= OTHER) || LA2_0 == STAR)) {
                                alt2 = 1;
                            }

                            switch (alt2) {
                            case 1:
                                // CtrlChecker.g:71:8: stat
                            {
                                _last = (MyTree) this.input.LT(1);
                                pushFollow(FOLLOW_stat_in_block179);
                                stat10 = stat();

                                this.state._fsp--;

                                if (_first_1 == null) {
                                    _first_1 = stat10.tree;
                                }

                                retval.tree = (MyTree) _first_0;
                                if (this.adaptor.getParent(retval.tree) != null
                                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                    retval.tree =
                                        (MyTree) this.adaptor.getParent(retval.tree);
                                }
                            }
                                break;

                            default:
                                break loop2;
                            }
                        } while (true);

                        this.helper.closeScope();

                        match(this.input, Token.UP, null);
                    }
                    _last = _save_last_1;
                }

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        } finally {
        }
        return retval;
    }

    // $ANTLR end "block"

    public static class stat_return extends TreeRuleReturnScope {
        MyTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "stat"
    // CtrlChecker.g:76:1: stat : ( block | var_decl | ^( ALAP stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE );
    public final CtrlChecker.stat_return stat() throws RecognitionException {
        CtrlChecker.stat_return retval = new CtrlChecker.stat_return();
        retval.start = this.input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree ALAP13 = null;
        MyTree WHILE15 = null;
        MyTree UNTIL18 = null;
        MyTree TRY21 = null;
        MyTree IF24 = null;
        MyTree CHOICE28 = null;
        MyTree STAR31 = null;
        MyTree ANY34 = null;
        MyTree OTHER35 = null;
        MyTree TRUE36 = null;
        CtrlChecker.block_return block11 = null;

        CtrlChecker.var_decl_return var_decl12 = null;

        CtrlChecker.stat_return stat14 = null;

        CtrlChecker.stat_return stat16 = null;

        CtrlChecker.stat_return stat17 = null;

        CtrlChecker.stat_return stat19 = null;

        CtrlChecker.stat_return stat20 = null;

        CtrlChecker.stat_return stat22 = null;

        CtrlChecker.stat_return stat23 = null;

        CtrlChecker.stat_return stat25 = null;

        CtrlChecker.stat_return stat26 = null;

        CtrlChecker.stat_return stat27 = null;

        CtrlChecker.stat_return stat29 = null;

        CtrlChecker.stat_return stat30 = null;

        CtrlChecker.stat_return stat32 = null;

        CtrlChecker.rule_return rule33 = null;

        MyTree ALAP13_tree = null;
        MyTree WHILE15_tree = null;
        MyTree UNTIL18_tree = null;
        MyTree TRY21_tree = null;
        MyTree IF24_tree = null;
        MyTree CHOICE28_tree = null;
        MyTree STAR31_tree = null;
        MyTree ANY34_tree = null;
        MyTree OTHER35_tree = null;
        MyTree TRUE36_tree = null;

        try {
            // CtrlChecker.g:77:3: ( block | var_decl | ^( ALAP stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE )
            int alt6 = 13;
            switch (this.input.LA(1)) {
            case BLOCK: {
                alt6 = 1;
            }
                break;
            case VAR: {
                alt6 = 2;
            }
                break;
            case ALAP: {
                alt6 = 3;
            }
                break;
            case WHILE: {
                alt6 = 4;
            }
                break;
            case UNTIL: {
                alt6 = 5;
            }
                break;
            case TRY: {
                alt6 = 6;
            }
                break;
            case IF: {
                alt6 = 7;
            }
                break;
            case CHOICE: {
                alt6 = 8;
            }
                break;
            case STAR: {
                alt6 = 9;
            }
                break;
            case CALL: {
                alt6 = 10;
            }
                break;
            case ANY: {
                alt6 = 11;
            }
                break;
            case OTHER: {
                alt6 = 12;
            }
                break;
            case TRUE: {
                alt6 = 13;
            }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, this.input);

                throw nvae;
            }

            switch (alt6) {
            case 1:
                // CtrlChecker.g:77:5: block
            {
                _last = (MyTree) this.input.LT(1);
                pushFollow(FOLLOW_block_in_stat209);
                block11 = block();

                this.state._fsp--;

                if (_first_0 == null) {
                    _first_0 = block11.tree;
                }

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 2:
                // CtrlChecker.g:78:5: var_decl
            {
                _last = (MyTree) this.input.LT(1);
                pushFollow(FOLLOW_var_decl_in_stat215);
                var_decl12 = var_decl();

                this.state._fsp--;

                if (_first_0 == null) {
                    _first_0 = var_decl12.tree;
                }

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 3:
                // CtrlChecker.g:79:5: ^( ALAP stat )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    ALAP13 =
                        (MyTree) match(this.input, ALAP, FOLLOW_ALAP_in_stat222);

                    if (_first_0 == null) {
                        _first_0 = ALAP13;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat224);
                    stat14 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat14.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 4:
                // CtrlChecker.g:80:5: ^( WHILE stat stat )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    WHILE15 =
                        (MyTree) match(this.input, WHILE,
                            FOLLOW_WHILE_in_stat233);

                    if (_first_0 == null) {
                        _first_0 = WHILE15;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat242);
                    stat16 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat16.tree;
                    }
                    this.helper.startBranch();
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat260);
                    stat17 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat17.tree;
                    }
                    this.helper.endBranch();

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 5:
                // CtrlChecker.g:86:5: ^( UNTIL stat stat )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    UNTIL18 =
                        (MyTree) match(this.input, UNTIL,
                            FOLLOW_UNTIL_in_stat284);

                    if (_first_0 == null) {
                        _first_0 = UNTIL18;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat293);
                    stat19 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat19.tree;
                    }
                    this.helper.startBranch();
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat311);
                    stat20 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat20.tree;
                    }
                    this.helper.endBranch();

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 6:
                // CtrlChecker.g:92:5: ^( TRY stat ( stat )? )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    TRY21 =
                        (MyTree) match(this.input, TRY, FOLLOW_TRY_in_stat335);

                    if (_first_0 == null) {
                        _first_0 = TRY21;
                    }
                    this.helper.startBranch();

                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat353);
                    stat22 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat22.tree;
                    }
                    // CtrlChecker.g:95:8: ( stat )?
                    int alt3 = 2;
                    int LA3_0 = this.input.LA(1);

                    if (((LA3_0 >= BLOCK && LA3_0 <= CALL) || LA3_0 == VAR
                        || (LA3_0 >= ALAP && LA3_0 <= UNTIL) || LA3_0 == IF
                        || (LA3_0 >= TRY && LA3_0 <= CHOICE) || LA3_0 == TRUE
                        || (LA3_0 >= ANY && LA3_0 <= OTHER) || LA3_0 == STAR)) {
                        alt3 = 1;
                    }
                    switch (alt3) {
                    case 1:
                        // CtrlChecker.g:95:10: stat
                    {
                        this.helper.nextBranch();
                        _last = (MyTree) this.input.LT(1);
                        pushFollow(FOLLOW_stat_in_stat375);
                        stat23 = stat();

                        this.state._fsp--;

                        if (_first_1 == null) {
                            _first_1 = stat23.tree;
                        }

                        retval.tree = (MyTree) _first_0;
                        if (this.adaptor.getParent(retval.tree) != null
                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                            retval.tree =
                                (MyTree) this.adaptor.getParent(retval.tree);
                        }
                    }
                        break;

                    }

                    this.helper.endBranch();

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 7:
                // CtrlChecker.g:100:5: ^( IF stat stat ( stat )? )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    IF24 = (MyTree) match(this.input, IF, FOLLOW_IF_in_stat409);

                    if (_first_0 == null) {
                        _first_0 = IF24;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat419);
                    stat25 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat25.tree;
                    }
                    this.helper.startBranch();
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat438);
                    stat26 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat26.tree;
                    }
                    // CtrlChecker.g:104:8: ( stat )?
                    int alt4 = 2;
                    int LA4_0 = this.input.LA(1);

                    if (((LA4_0 >= BLOCK && LA4_0 <= CALL) || LA4_0 == VAR
                        || (LA4_0 >= ALAP && LA4_0 <= UNTIL) || LA4_0 == IF
                        || (LA4_0 >= TRY && LA4_0 <= CHOICE) || LA4_0 == TRUE
                        || (LA4_0 >= ANY && LA4_0 <= OTHER) || LA4_0 == STAR)) {
                        alt4 = 1;
                    }
                    switch (alt4) {
                    case 1:
                        // CtrlChecker.g:104:10: stat
                    {
                        this.helper.nextBranch();
                        _last = (MyTree) this.input.LT(1);
                        pushFollow(FOLLOW_stat_in_stat461);
                        stat27 = stat();

                        this.state._fsp--;

                        if (_first_1 == null) {
                            _first_1 = stat27.tree;
                        }

                        retval.tree = (MyTree) _first_0;
                        if (this.adaptor.getParent(retval.tree) != null
                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                            retval.tree =
                                (MyTree) this.adaptor.getParent(retval.tree);
                        }
                    }
                        break;

                    }

                    this.helper.endBranch();

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 8:
                // CtrlChecker.g:109:5: ^( CHOICE stat ( stat )* )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    CHOICE28 =
                        (MyTree) match(this.input, CHOICE,
                            FOLLOW_CHOICE_in_stat495);

                    if (_first_0 == null) {
                        _first_0 = CHOICE28;
                    }
                    this.helper.startBranch();

                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat513);
                    stat29 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat29.tree;
                    }
                    // CtrlChecker.g:112:8: ( stat )*
                    loop5: do {
                        int alt5 = 2;
                        int LA5_0 = this.input.LA(1);

                        if (((LA5_0 >= BLOCK && LA5_0 <= CALL) || LA5_0 == VAR
                            || (LA5_0 >= ALAP && LA5_0 <= UNTIL) || LA5_0 == IF
                            || (LA5_0 >= TRY && LA5_0 <= CHOICE)
                            || LA5_0 == TRUE
                            || (LA5_0 >= ANY && LA5_0 <= OTHER) || LA5_0 == STAR)) {
                            alt5 = 1;
                        }

                        switch (alt5) {
                        case 1:
                            // CtrlChecker.g:112:10: stat
                        {
                            this.helper.nextBranch();
                            _last = (MyTree) this.input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat536);
                            stat30 = stat();

                            this.state._fsp--;

                            if (_first_1 == null) {
                                _first_1 = stat30.tree;
                            }

                            retval.tree = (MyTree) _first_0;
                            if (this.adaptor.getParent(retval.tree) != null
                                && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                retval.tree =
                                    (MyTree) this.adaptor.getParent(retval.tree);
                            }
                        }
                            break;

                        default:
                            break loop5;
                        }
                    } while (true);

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                this.helper.endBranch();

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 9:
                // CtrlChecker.g:117:5: ^( STAR stat )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    STAR31 =
                        (MyTree) match(this.input, STAR, FOLLOW_STAR_in_stat572);

                    if (_first_0 == null) {
                        _first_0 = STAR31;
                    }
                    this.helper.startBranch();

                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat590);
                    stat32 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat32.tree;
                    }
                    this.helper.endBranch();

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 10:
                // CtrlChecker.g:122:5: rule
            {
                _last = (MyTree) this.input.LT(1);
                pushFollow(FOLLOW_rule_in_stat612);
                rule33 = rule();

                this.state._fsp--;

                if (_first_0 == null) {
                    _first_0 = rule33.tree;
                }

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 11:
                // CtrlChecker.g:123:5: ANY
            {
                _last = (MyTree) this.input.LT(1);
                ANY34 = (MyTree) match(this.input, ANY, FOLLOW_ANY_in_stat618);

                if (_first_0 == null) {
                    _first_0 = ANY34;
                }
                this.helper.checkAny(ANY34);

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 12:
                // CtrlChecker.g:125:5: OTHER
            {
                _last = (MyTree) this.input.LT(1);
                OTHER35 =
                    (MyTree) match(this.input, OTHER, FOLLOW_OTHER_in_stat630);

                if (_first_0 == null) {
                    _first_0 = OTHER35;
                }
                this.helper.checkOther(OTHER35);

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 13:
                // CtrlChecker.g:127:5: TRUE
            {
                _last = (MyTree) this.input.LT(1);
                TRUE36 =
                    (MyTree) match(this.input, TRUE, FOLLOW_TRUE_in_stat642);

                if (_first_0 == null) {
                    _first_0 = TRUE36;
                }

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;

            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        } finally {
        }
        return retval;
    }

    // $ANTLR end "stat"

    public static class rule_return extends TreeRuleReturnScope {
        MyTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "rule"
    // CtrlChecker.g:130:1: rule : ^( CALL id= ID ( ^( ARGS ( arg )* ) )? ) ;
    public final CtrlChecker.rule_return rule() throws RecognitionException {
        CtrlChecker.rule_return retval = new CtrlChecker.rule_return();
        retval.start = this.input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree id = null;
        MyTree CALL37 = null;
        MyTree ARGS38 = null;
        CtrlChecker.arg_return arg39 = null;

        MyTree id_tree = null;
        MyTree CALL37_tree = null;
        MyTree ARGS38_tree = null;

        try {
            // CtrlChecker.g:132:3: ( ^( CALL id= ID ( ^( ARGS ( arg )* ) )? ) )
            // CtrlChecker.g:132:5: ^( CALL id= ID ( ^( ARGS ( arg )* ) )? )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    CALL37 =
                        (MyTree) match(this.input, CALL, FOLLOW_CALL_in_rule660);

                    if (_first_0 == null) {
                        _first_0 = CALL37;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    id = (MyTree) match(this.input, ID, FOLLOW_ID_in_rule664);

                    if (_first_1 == null) {
                        _first_1 = id;
                    }
                    // CtrlChecker.g:132:18: ( ^( ARGS ( arg )* ) )?
                    int alt8 = 2;
                    int LA8_0 = this.input.LA(1);

                    if ((LA8_0 == ARGS)) {
                        alt8 = 1;
                    }
                    switch (alt8) {
                    case 1:
                        // CtrlChecker.g:132:19: ^( ARGS ( arg )* )
                    {
                        _last = (MyTree) this.input.LT(1);
                        {
                            MyTree _save_last_2 = _last;
                            MyTree _first_2 = null;
                            _last = (MyTree) this.input.LT(1);
                            ARGS38 =
                                (MyTree) match(this.input, ARGS,
                                    FOLLOW_ARGS_in_rule668);

                            if (_first_1 == null) {
                                _first_1 = ARGS38;
                            }
                            if (this.input.LA(1) == Token.DOWN) {
                                match(this.input, Token.DOWN, null);
                                // CtrlChecker.g:132:26: ( arg )*
                                loop7: do {
                                    int alt7 = 2;
                                    int LA7_0 = this.input.LA(1);

                                    if ((LA7_0 == ARG)) {
                                        alt7 = 1;
                                    }

                                    switch (alt7) {
                                    case 1:
                                        // CtrlChecker.g:132:26: arg
                                    {
                                        _last = (MyTree) this.input.LT(1);
                                        pushFollow(FOLLOW_arg_in_rule670);
                                        arg39 = arg();

                                        this.state._fsp--;

                                        if (_first_2 == null) {
                                            _first_2 = arg39.tree;
                                        }

                                        retval.tree = (MyTree) _first_0;
                                        if (this.adaptor.getParent(retval.tree) != null
                                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                            retval.tree =
                                                (MyTree) this.adaptor.getParent(retval.tree);
                                        }
                                    }
                                        break;

                                    default:
                                        break loop7;
                                    }
                                } while (true);

                                match(this.input, Token.UP, null);
                            }
                            _last = _save_last_2;
                        }

                        retval.tree = (MyTree) _first_0;
                        if (this.adaptor.getParent(retval.tree) != null
                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                            retval.tree =
                                (MyTree) this.adaptor.getParent(retval.tree);
                        }
                    }
                        break;

                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }

            this.helper.checkCall(((MyTree) retval.tree));
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        } finally {
        }
        return retval;
    }

    // $ANTLR end "rule"

    public static class var_decl_return extends TreeRuleReturnScope {
        MyTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "var_decl"
    // CtrlChecker.g:135:1: var_decl : ^( VAR type ( ID )+ ) ;
    public final CtrlChecker.var_decl_return var_decl()
        throws RecognitionException {
        CtrlChecker.var_decl_return retval = new CtrlChecker.var_decl_return();
        retval.start = this.input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree VAR40 = null;
        MyTree ID42 = null;
        CtrlChecker.type_return type41 = null;

        MyTree VAR40_tree = null;
        MyTree ID42_tree = null;

        try {
            // CtrlChecker.g:136:2: ( ^( VAR type ( ID )+ ) )
            // CtrlChecker.g:136:4: ^( VAR type ( ID )+ )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    VAR40 =
                        (MyTree) match(this.input, VAR,
                            FOLLOW_VAR_in_var_decl689);

                    if (_first_0 == null) {
                        _first_0 = VAR40;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_type_in_var_decl691);
                    type41 = type();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = type41.tree;
                    }
                    // CtrlChecker.g:138:7: ( ID )+
                    int cnt9 = 0;
                    loop9: do {
                        int alt9 = 2;
                        int LA9_0 = this.input.LA(1);

                        if ((LA9_0 == ID)) {
                            alt9 = 1;
                        }

                        switch (alt9) {
                        case 1:
                            // CtrlChecker.g:138:9: ID
                        {
                            _last = (MyTree) this.input.LT(1);
                            ID42 =
                                (MyTree) match(this.input, ID,
                                    FOLLOW_ID_in_var_decl708);

                            if (_first_1 == null) {
                                _first_1 = ID42;
                            }
                            this.helper.declareVar(ID42, (type41 != null
                                    ? ((MyTree) type41.tree) : null));

                            retval.tree = (MyTree) _first_0;
                            if (this.adaptor.getParent(retval.tree) != null
                                && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                retval.tree =
                                    (MyTree) this.adaptor.getParent(retval.tree);
                            }
                        }
                            break;

                        default:
                            if (cnt9 >= 1) {
                                break loop9;
                            }
                            EarlyExitException eee =
                                new EarlyExitException(9, this.input);
                            throw eee;
                        }
                        cnt9++;
                    } while (true);

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        } finally {
        }
        return retval;
    }

    // $ANTLR end "var_decl"

    public static class type_return extends TreeRuleReturnScope {
        MyTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "type"
    // CtrlChecker.g:144:1: type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlChecker.type_return type() throws RecognitionException {
        CtrlChecker.type_return retval = new CtrlChecker.type_return();
        retval.start = this.input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree NODE43 = null;
        MyTree BOOL44 = null;
        MyTree STRING45 = null;
        MyTree INT46 = null;
        MyTree REAL47 = null;

        MyTree NODE43_tree = null;
        MyTree BOOL44_tree = null;
        MyTree STRING45_tree = null;
        MyTree INT46_tree = null;
        MyTree REAL47_tree = null;

        try {
            // CtrlChecker.g:145:3: ( NODE | BOOL | STRING | INT | REAL )
            int alt10 = 5;
            switch (this.input.LA(1)) {
            case NODE: {
                alt10 = 1;
            }
                break;
            case BOOL: {
                alt10 = 2;
            }
                break;
            case STRING: {
                alt10 = 3;
            }
                break;
            case INT: {
                alt10 = 4;
            }
                break;
            case REAL: {
                alt10 = 5;
            }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, this.input);

                throw nvae;
            }

            switch (alt10) {
            case 1:
                // CtrlChecker.g:145:5: NODE
            {
                _last = (MyTree) this.input.LT(1);
                NODE43 =
                    (MyTree) match(this.input, NODE, FOLLOW_NODE_in_type747);

                if (_first_0 == null) {
                    _first_0 = NODE43;
                }
                this.helper.checkType(NODE43);

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 2:
                // CtrlChecker.g:146:5: BOOL
            {
                _last = (MyTree) this.input.LT(1);
                BOOL44 =
                    (MyTree) match(this.input, BOOL, FOLLOW_BOOL_in_type757);

                if (_first_0 == null) {
                    _first_0 = BOOL44;
                }
                this.helper.checkType(BOOL44);

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 3:
                // CtrlChecker.g:147:5: STRING
            {
                _last = (MyTree) this.input.LT(1);
                STRING45 =
                    (MyTree) match(this.input, STRING, FOLLOW_STRING_in_type767);

                if (_first_0 == null) {
                    _first_0 = STRING45;
                }
                this.helper.checkType(STRING45);

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 4:
                // CtrlChecker.g:148:5: INT
            {
                _last = (MyTree) this.input.LT(1);
                INT46 = (MyTree) match(this.input, INT, FOLLOW_INT_in_type775);

                if (_first_0 == null) {
                    _first_0 = INT46;
                }
                this.helper.checkType(INT46);

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 5:
                // CtrlChecker.g:149:5: REAL
            {
                _last = (MyTree) this.input.LT(1);
                REAL47 =
                    (MyTree) match(this.input, REAL, FOLLOW_REAL_in_type786);

                if (_first_0 == null) {
                    _first_0 = REAL47;
                }
                this.helper.checkType(REAL47);

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;

            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        } finally {
        }
        return retval;
    }

    // $ANTLR end "type"

    public static class arg_return extends TreeRuleReturnScope {
        MyTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "arg"
    // CtrlChecker.g:152:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final CtrlChecker.arg_return arg() throws RecognitionException {
        CtrlChecker.arg_return retval = new CtrlChecker.arg_return();
        retval.start = this.input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree ARG48 = null;
        MyTree OUT49 = null;
        MyTree ID50 = null;
        MyTree DONT_CARE51 = null;
        CtrlChecker.literal_return literal52 = null;

        MyTree ARG48_tree = null;
        MyTree OUT49_tree = null;
        MyTree ID50_tree = null;
        MyTree DONT_CARE51_tree = null;

        try {
            // CtrlChecker.g:153:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // CtrlChecker.g:153:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    ARG48 =
                        (MyTree) match(this.input, ARG, FOLLOW_ARG_in_arg806);

                    if (_first_0 == null) {
                        _first_0 = ARG48;
                    }
                    match(this.input, Token.DOWN, null);
                    // CtrlChecker.g:154:7: ( ( OUT )? ID | DONT_CARE | literal )
                    int alt12 = 3;
                    switch (this.input.LA(1)) {
                    case ID:
                    case OUT: {
                        alt12 = 1;
                    }
                        break;
                    case DONT_CARE: {
                        alt12 = 2;
                    }
                        break;
                    case TRUE:
                    case FALSE:
                    case STRING_LIT:
                    case INT_LIT:
                    case REAL_LIT: {
                        alt12 = 3;
                    }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 12, 0, this.input);

                        throw nvae;
                    }

                    switch (alt12) {
                    case 1:
                        // CtrlChecker.g:154:9: ( OUT )? ID
                    {
                        // CtrlChecker.g:154:9: ( OUT )?
                        int alt11 = 2;
                        int LA11_0 = this.input.LA(1);

                        if ((LA11_0 == OUT)) {
                            alt11 = 1;
                        }
                        switch (alt11) {
                        case 1:
                            // CtrlChecker.g:154:9: OUT
                        {
                            _last = (MyTree) this.input.LT(1);
                            OUT49 =
                                (MyTree) match(this.input, OUT,
                                    FOLLOW_OUT_in_arg817);

                            if (_first_1 == null) {
                                _first_1 = OUT49;
                            }

                            retval.tree = (MyTree) _first_0;
                            if (this.adaptor.getParent(retval.tree) != null
                                && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                retval.tree =
                                    (MyTree) this.adaptor.getParent(retval.tree);
                            }
                        }
                            break;

                        }

                        _last = (MyTree) this.input.LT(1);
                        ID50 =
                            (MyTree) match(this.input, ID, FOLLOW_ID_in_arg820);

                        if (_first_1 == null) {
                            _first_1 = ID50;
                        }
                        this.helper.checkVarArg(ARG48);

                        retval.tree = (MyTree) _first_0;
                        if (this.adaptor.getParent(retval.tree) != null
                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                            retval.tree =
                                (MyTree) this.adaptor.getParent(retval.tree);
                        }
                    }
                        break;
                    case 2:
                        // CtrlChecker.g:155:9: DONT_CARE
                    {
                        _last = (MyTree) this.input.LT(1);
                        DONT_CARE51 =
                            (MyTree) match(this.input, DONT_CARE,
                                FOLLOW_DONT_CARE_in_arg832);

                        if (_first_1 == null) {
                            _first_1 = DONT_CARE51;
                        }
                        this.helper.checkDontCareArg(ARG48);

                        retval.tree = (MyTree) _first_0;
                        if (this.adaptor.getParent(retval.tree) != null
                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                            retval.tree =
                                (MyTree) this.adaptor.getParent(retval.tree);
                        }
                    }
                        break;
                    case 3:
                        // CtrlChecker.g:156:9: literal
                    {
                        _last = (MyTree) this.input.LT(1);
                        pushFollow(FOLLOW_literal_in_arg844);
                        literal52 = literal();

                        this.state._fsp--;

                        if (_first_1 == null) {
                            _first_1 = literal52.tree;
                        }
                        this.helper.checkConstArg(ARG48);

                        retval.tree = (MyTree) _first_0;
                        if (this.adaptor.getParent(retval.tree) != null
                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                            retval.tree =
                                (MyTree) this.adaptor.getParent(retval.tree);
                        }
                    }
                        break;

                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        } finally {
        }
        return retval;
    }

    // $ANTLR end "arg"

    public static class literal_return extends TreeRuleReturnScope {
        MyTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "literal"
    // CtrlChecker.g:161:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlChecker.literal_return literal()
        throws RecognitionException {
        CtrlChecker.literal_return retval = new CtrlChecker.literal_return();
        retval.start = this.input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree set53 = null;

        MyTree set53_tree = null;

        try {
            // CtrlChecker.g:162:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // CtrlChecker.g:
            {
                _last = (MyTree) this.input.LT(1);
                set53 = (MyTree) this.input.LT(1);
                if (this.input.LA(1) == TRUE
                    || (this.input.LA(1) >= FALSE && this.input.LA(1) <= REAL_LIT)) {
                    this.input.consume();

                    this.state.errorRecovery = false;
                } else {
                    MismatchedSetException mse =
                        new MismatchedSetException(null, this.input);
                    throw mse;
                }

                retval.tree = (MyTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }

            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        } finally {
        }
        return retval;
    }

    // $ANTLR end "literal"

    // Delegated rules

    public static final BitSet FOLLOW_PROGRAM_in_program57 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_functions_in_program59 = new BitSet(
        new long[] {0x0000000000000040L});
    public static final BitSet FOLLOW_block_in_program61 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions77 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions79 = new BitSet(
        new long[] {0x0000000000000408L});
    public static final BitSet FOLLOW_FUNCTION_in_function102 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function104 = new BitSet(
        new long[] {0x0000000000000040L});
    public static final BitSet FOLLOW_block_in_function123 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block161 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block179 = new BitSet(
        new long[] {0x0002000C46B820C8L});
    public static final BitSet FOLLOW_block_in_stat209 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat215 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat222 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat224 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat233 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat242 = new BitSet(
        new long[] {0x0002000C46B820C8L});
    public static final BitSet FOLLOW_stat_in_stat260 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat284 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat293 = new BitSet(
        new long[] {0x0002000C46B820C8L});
    public static final BitSet FOLLOW_stat_in_stat311 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat335 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat353 = new BitSet(
        new long[] {0x0002000C46B820C8L});
    public static final BitSet FOLLOW_stat_in_stat375 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat409 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat419 = new BitSet(
        new long[] {0x0002000C46B820C8L});
    public static final BitSet FOLLOW_stat_in_stat438 = new BitSet(
        new long[] {0x0002000C46B820C8L});
    public static final BitSet FOLLOW_stat_in_stat461 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat495 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat513 = new BitSet(
        new long[] {0x0002000C46B820C8L});
    public static final BitSet FOLLOW_stat_in_stat536 = new BitSet(
        new long[] {0x0002000C46B820C8L});
    public static final BitSet FOLLOW_STAR_in_stat572 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat590 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat612 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat618 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat630 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat642 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule660 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule664 = new BitSet(
        new long[] {0x0000000000000028L});
    public static final BitSet FOLLOW_ARGS_in_rule668 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_arg_in_rule670 = new BitSet(
        new long[] {0x0000000000000018L});
    public static final BitSet FOLLOW_VAR_in_var_decl689 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl691 = new BitSet(
        new long[] {0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_var_decl708 = new BitSet(
        new long[] {0x0000000000010008L});
    public static final BitSet FOLLOW_NODE_in_type747 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_type757 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_type767 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_type775 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_REAL_in_type786 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_arg806 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg817 = new BitSet(
        new long[] {0x0000000000010000L});
    public static final BitSet FOLLOW_ID_in_arg820 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg832 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg844 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_literal0 = new BitSet(
        new long[] {0x0000000000000002L});

}