// $ANTLR 3.2 Sep 23, 2009 12:02:23 GCLNewChecker.g 2010-11-20 11:01:58

package groove.control.parse;

import java.util.List;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeParser;
import org.antlr.runtime.tree.TreeRuleReturnScope;

@SuppressWarnings("all")
public class GCLNewChecker extends TreeParser {
    public static final String[] tokenNames = new String[] {"<invalid>",
        "<EOR>", "<DOWN>", "<UP>", "PROGRAM", "BLOCK", "FUNCTIONS", "FUNCTION",
        "CALL", "DO_WHILE", "DO_UNTIL", "VAR", "ARG", "LCURLY", "RCURLY", "ID",
        "LPAR", "RPAR", "ALAP", "WHILE", "UNTIL", "DO", "IF", "ELSE", "TRY",
        "CHOICE", "OR", "SEMI", "BAR", "TRUE", "PLUS", "STAR", "SHARP", "ANY",
        "OTHER", "DOT", "COMMA", "NODE", "BOOL", "STRING", "INT", "REAL",
        "OUT", "DONT_CARE", "FALSE", "STRING_LIT", "INT_LIT", "REAL_LIT",
        "IntegerNumber", "NonIntegerNumber", "QUOTE", "EscapeSequence",
        "BSLASH", "CR", "NL", "AMP", "NOT", "MINUS", "ML_COMMENT",
        "SL_COMMENT", "WS"};
    public static final int REAL_LIT = 47;
    public static final int FUNCTION = 7;
    public static final int DO_UNTIL = 10;
    public static final int STAR = 31;
    public static final int INT_LIT = 46;
    public static final int FUNCTIONS = 6;
    public static final int WHILE = 19;
    public static final int IntegerNumber = 48;
    public static final int STRING_LIT = 45;
    public static final int AMP = 55;
    public static final int DO = 21;
    public static final int NOT = 56;
    public static final int ALAP = 18;
    public static final int ID = 15;
    public static final int EOF = -1;
    public static final int IF = 22;
    public static final int ML_COMMENT = 58;
    public static final int QUOTE = 50;
    public static final int LPAR = 16;
    public static final int ARG = 12;
    public static final int COMMA = 36;
    public static final int NonIntegerNumber = 49;
    public static final int DO_WHILE = 9;
    public static final int PLUS = 30;
    public static final int VAR = 11;
    public static final int NL = 54;
    public static final int DOT = 35;
    public static final int CHOICE = 25;
    public static final int SHARP = 32;
    public static final int OTHER = 34;
    public static final int NODE = 37;
    public static final int ELSE = 23;
    public static final int BOOL = 38;
    public static final int LCURLY = 13;
    public static final int INT = 40;
    public static final int MINUS = 57;
    public static final int SEMI = 27;
    public static final int TRUE = 29;
    public static final int TRY = 24;
    public static final int REAL = 41;
    public static final int DONT_CARE = 43;
    public static final int ANY = 33;
    public static final int WS = 60;
    public static final int OUT = 42;
    public static final int UNTIL = 20;
    public static final int BLOCK = 5;
    public static final int OR = 26;
    public static final int RCURLY = 14;
    public static final int SL_COMMENT = 59;
    public static final int PROGRAM = 4;
    public static final int RPAR = 17;
    public static final int CALL = 8;
    public static final int FALSE = 44;
    public static final int CR = 53;
    public static final int EscapeSequence = 51;
    public static final int BSLASH = 52;
    public static final int BAR = 28;
    public static final int STRING = 39;

    // delegates
    // delegators

    public GCLNewChecker(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }

    public GCLNewChecker(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);

    }

    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }

    public TreeAdaptor getTreeAdaptor() {
        return this.adaptor;
    }

    @Override
    public String[] getTokenNames() {
        return GCLNewChecker.tokenNames;
    }

    @Override
    public String getGrammarFileName() {
        return "GCLNewChecker.g";
    }

    /** Helper class to convert AST trees to namespace. */
    private GCLHelper helper;

    @Override
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
    public Tree run(MyTree tree, NamespaceNew namespace)
        throws RecognitionException {
        this.helper = new GCLHelper(this, namespace);
        MyTreeAdaptor treeAdaptor = new MyTreeAdaptor();
        setTreeAdaptor(treeAdaptor);
        setTreeNodeStream(treeAdaptor.createTreeNodeStream(tree));
        return (Tree) program().getTree();
    }

    //    
    //    private List<String> syntaxInit = new ArrayList<String>();
    //    
    //    int numParameters = 0;
    //    SPORule currentRule;
    //    HashSet<String> currentOutputParameters = new HashSet<String>();
    //    
    //    private void debug(String msg) {
    //    	if (namespace.usesVariables()) {
    //    		//System.err.println("Variables debug (GCLChecker): "+msg);
    //    	}
    //    }

    public static class program_return extends TreeRuleReturnScope {
        MyTree tree;

        @Override
        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "program"
    // GCLNewChecker.g:62:1: program : ^( PROGRAM functions block ) ;
    public final GCLNewChecker.program_return program()
        throws RecognitionException {
        GCLNewChecker.program_return retval =
            new GCLNewChecker.program_return();
        retval.start = this.input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree PROGRAM1 = null;
        GCLNewChecker.functions_return functions2 = null;

        GCLNewChecker.block_return block3 = null;

        MyTree PROGRAM1_tree = null;

        try {
            // GCLNewChecker.g:63:3: ( ^( PROGRAM functions block ) )
            // GCLNewChecker.g:63:6: ^( PROGRAM functions block )
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

                retval.tree = _first_0;
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

        @Override
        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "functions"
    // GCLNewChecker.g:66:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final GCLNewChecker.functions_return functions()
        throws RecognitionException {
        GCLNewChecker.functions_return retval =
            new GCLNewChecker.functions_return();
        retval.start = this.input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree FUNCTIONS4 = null;
        GCLNewChecker.function_return function5 = null;

        MyTree FUNCTIONS4_tree = null;

        try {
            // GCLNewChecker.g:67:3: ( ^( FUNCTIONS ( function )* ) )
            // GCLNewChecker.g:67:5: ^( FUNCTIONS ( function )* )
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
                        // GCLNewChecker.g:67:17: ( function )*
                        loop1: do {
                            int alt1 = 2;
                            int LA1_0 = this.input.LA(1);

                            if ((LA1_0 == FUNCTION)) {
                                alt1 = 1;
                            }

                            switch (alt1) {
                            case 1:
                                // GCLNewChecker.g:67:17: function
                            {
                                _last = (MyTree) this.input.LT(1);
                                pushFollow(FOLLOW_function_in_functions79);
                                function5 = function();

                                this.state._fsp--;

                                if (_first_1 == null) {
                                    _first_1 = function5.tree;
                                }

                                retval.tree = _first_0;
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

                retval.tree = _first_0;
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

        @Override
        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "function"
    // GCLNewChecker.g:69:1: function : ^( FUNCTION ID block ) ;
    public final GCLNewChecker.function_return function()
        throws RecognitionException {
        GCLNewChecker.function_return retval =
            new GCLNewChecker.function_return();
        retval.start = this.input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree FUNCTION6 = null;
        MyTree ID7 = null;
        GCLNewChecker.block_return block8 = null;

        MyTree FUNCTION6_tree = null;
        MyTree ID7_tree = null;

        try {
            // GCLNewChecker.g:70:3: ( ^( FUNCTION ID block ) )
            // GCLNewChecker.g:70:5: ^( FUNCTION ID block )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    FUNCTION6 =
                        (MyTree) match(this.input, FUNCTION,
                            FOLLOW_FUNCTION_in_function92);

                    if (_first_0 == null) {
                        _first_0 = FUNCTION6;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    ID7 =
                        (MyTree) match(this.input, ID, FOLLOW_ID_in_function94);

                    if (_first_1 == null) {
                        _first_1 = ID7;
                    }
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_block_in_function96);
                    block8 = block();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = block8.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = _first_0;
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
        MyTree tree;

        @Override
        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "block"
    // GCLNewChecker.g:73:1: block : ^( BLOCK ( stat )* ) ;
    public final GCLNewChecker.block_return block() throws RecognitionException {
        GCLNewChecker.block_return retval = new GCLNewChecker.block_return();
        retval.start = this.input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree BLOCK9 = null;
        GCLNewChecker.stat_return stat10 = null;

        MyTree BLOCK9_tree = null;

        try {
            // GCLNewChecker.g:74:3: ( ^( BLOCK ( stat )* ) )
            // GCLNewChecker.g:74:5: ^( BLOCK ( stat )* )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    BLOCK9 =
                        (MyTree) match(this.input, BLOCK,
                            FOLLOW_BLOCK_in_block113);

                    if (_first_0 == null) {
                        _first_0 = BLOCK9;
                    }
                    this.helper.openScope();

                    if (this.input.LA(1) == Token.DOWN) {
                        match(this.input, Token.DOWN, null);
                        // GCLNewChecker.g:74:37: ( stat )*
                        loop2: do {
                            int alt2 = 2;
                            int LA2_0 = this.input.LA(1);

                            if ((LA2_0 == BLOCK || LA2_0 == CALL
                                || LA2_0 == VAR
                                || (LA2_0 >= ALAP && LA2_0 <= UNTIL)
                                || LA2_0 == IF
                                || (LA2_0 >= TRY && LA2_0 <= CHOICE)
                                || LA2_0 == TRUE || LA2_0 == STAR || (LA2_0 >= ANY && LA2_0 <= OTHER))) {
                                alt2 = 1;
                            }

                            switch (alt2) {
                            case 1:
                                // GCLNewChecker.g:74:38: stat
                            {
                                _last = (MyTree) this.input.LT(1);
                                pushFollow(FOLLOW_stat_in_block118);
                                stat10 = stat();

                                this.state._fsp--;

                                if (_first_1 == null) {
                                    _first_1 = stat10.tree;
                                }

                                retval.tree = _first_0;
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

                retval.tree = _first_0;
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

        @Override
        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "stat"
    // GCLNewChecker.g:77:1: stat : ( block | var_decl | ^( ALAP stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE ( stat )+ ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE );
    public final GCLNewChecker.stat_return stat() throws RecognitionException {
        GCLNewChecker.stat_return retval = new GCLNewChecker.stat_return();
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
        MyTree STAR30 = null;
        MyTree ANY33 = null;
        MyTree OTHER34 = null;
        MyTree TRUE35 = null;
        GCLNewChecker.block_return block11 = null;

        GCLNewChecker.var_decl_return var_decl12 = null;

        GCLNewChecker.stat_return stat14 = null;

        GCLNewChecker.stat_return stat16 = null;

        GCLNewChecker.stat_return stat17 = null;

        GCLNewChecker.stat_return stat19 = null;

        GCLNewChecker.stat_return stat20 = null;

        GCLNewChecker.stat_return stat22 = null;

        GCLNewChecker.stat_return stat23 = null;

        GCLNewChecker.stat_return stat25 = null;

        GCLNewChecker.stat_return stat26 = null;

        GCLNewChecker.stat_return stat27 = null;

        GCLNewChecker.stat_return stat29 = null;

        GCLNewChecker.stat_return stat31 = null;

        GCLNewChecker.rule_return rule32 = null;

        MyTree ALAP13_tree = null;
        MyTree WHILE15_tree = null;
        MyTree UNTIL18_tree = null;
        MyTree TRY21_tree = null;
        MyTree IF24_tree = null;
        MyTree CHOICE28_tree = null;
        MyTree STAR30_tree = null;
        MyTree ANY33_tree = null;
        MyTree OTHER34_tree = null;
        MyTree TRUE35_tree = null;

        try {
            // GCLNewChecker.g:78:3: ( block | var_decl | ^( ALAP stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE ( stat )+ ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE )
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
                // GCLNewChecker.g:78:5: block
            {
                _last = (MyTree) this.input.LT(1);
                pushFollow(FOLLOW_block_in_stat136);
                block11 = block();

                this.state._fsp--;

                if (_first_0 == null) {
                    _first_0 = block11.tree;
                }

                retval.tree = _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 2:
                // GCLNewChecker.g:79:5: var_decl
            {
                _last = (MyTree) this.input.LT(1);
                pushFollow(FOLLOW_var_decl_in_stat142);
                var_decl12 = var_decl();

                this.state._fsp--;

                if (_first_0 == null) {
                    _first_0 = var_decl12.tree;
                }

                retval.tree = _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 3:
                // GCLNewChecker.g:80:5: ^( ALAP stat )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    ALAP13 =
                        (MyTree) match(this.input, ALAP, FOLLOW_ALAP_in_stat149);

                    if (_first_0 == null) {
                        _first_0 = ALAP13;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat151);
                    stat14 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat14.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 4:
                // GCLNewChecker.g:81:5: ^( WHILE stat stat )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    WHILE15 =
                        (MyTree) match(this.input, WHILE,
                            FOLLOW_WHILE_in_stat159);

                    if (_first_0 == null) {
                        _first_0 = WHILE15;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat161);
                    stat16 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat16.tree;
                    }
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat163);
                    stat17 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat17.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 5:
                // GCLNewChecker.g:82:5: ^( UNTIL stat stat )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    UNTIL18 =
                        (MyTree) match(this.input, UNTIL,
                            FOLLOW_UNTIL_in_stat171);

                    if (_first_0 == null) {
                        _first_0 = UNTIL18;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat173);
                    stat19 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat19.tree;
                    }
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat175);
                    stat20 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat20.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 6:
                // GCLNewChecker.g:83:5: ^( TRY stat ( stat )? )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    TRY21 =
                        (MyTree) match(this.input, TRY, FOLLOW_TRY_in_stat183);

                    if (_first_0 == null) {
                        _first_0 = TRY21;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat185);
                    stat22 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat22.tree;
                    }
                    // GCLNewChecker.g:83:16: ( stat )?
                    int alt3 = 2;
                    int LA3_0 = this.input.LA(1);

                    if ((LA3_0 == BLOCK || LA3_0 == CALL || LA3_0 == VAR
                        || (LA3_0 >= ALAP && LA3_0 <= UNTIL) || LA3_0 == IF
                        || (LA3_0 >= TRY && LA3_0 <= CHOICE) || LA3_0 == TRUE
                        || LA3_0 == STAR || (LA3_0 >= ANY && LA3_0 <= OTHER))) {
                        alt3 = 1;
                    }
                    switch (alt3) {
                    case 1:
                        // GCLNewChecker.g:83:17: stat
                    {
                        _last = (MyTree) this.input.LT(1);
                        pushFollow(FOLLOW_stat_in_stat188);
                        stat23 = stat();

                        this.state._fsp--;

                        if (_first_1 == null) {
                            _first_1 = stat23.tree;
                        }

                        retval.tree = _first_0;
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

                retval.tree = _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 7:
                // GCLNewChecker.g:84:5: ^( IF stat stat ( stat )? )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    IF24 = (MyTree) match(this.input, IF, FOLLOW_IF_in_stat198);

                    if (_first_0 == null) {
                        _first_0 = IF24;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat200);
                    stat25 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat25.tree;
                    }
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat202);
                    stat26 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat26.tree;
                    }
                    // GCLNewChecker.g:84:20: ( stat )?
                    int alt4 = 2;
                    int LA4_0 = this.input.LA(1);

                    if ((LA4_0 == BLOCK || LA4_0 == CALL || LA4_0 == VAR
                        || (LA4_0 >= ALAP && LA4_0 <= UNTIL) || LA4_0 == IF
                        || (LA4_0 >= TRY && LA4_0 <= CHOICE) || LA4_0 == TRUE
                        || LA4_0 == STAR || (LA4_0 >= ANY && LA4_0 <= OTHER))) {
                        alt4 = 1;
                    }
                    switch (alt4) {
                    case 1:
                        // GCLNewChecker.g:84:21: stat
                    {
                        _last = (MyTree) this.input.LT(1);
                        pushFollow(FOLLOW_stat_in_stat205);
                        stat27 = stat();

                        this.state._fsp--;

                        if (_first_1 == null) {
                            _first_1 = stat27.tree;
                        }

                        retval.tree = _first_0;
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

                retval.tree = _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 8:
                // GCLNewChecker.g:85:5: ^( CHOICE ( stat )+ )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    CHOICE28 =
                        (MyTree) match(this.input, CHOICE,
                            FOLLOW_CHOICE_in_stat215);

                    if (_first_0 == null) {
                        _first_0 = CHOICE28;
                    }
                    match(this.input, Token.DOWN, null);
                    // GCLNewChecker.g:85:14: ( stat )+
                    int cnt5 = 0;
                    loop5: do {
                        int alt5 = 2;
                        int LA5_0 = this.input.LA(1);

                        if ((LA5_0 == BLOCK || LA5_0 == CALL || LA5_0 == VAR
                            || (LA5_0 >= ALAP && LA5_0 <= UNTIL) || LA5_0 == IF
                            || (LA5_0 >= TRY && LA5_0 <= CHOICE)
                            || LA5_0 == TRUE || LA5_0 == STAR || (LA5_0 >= ANY && LA5_0 <= OTHER))) {
                            alt5 = 1;
                        }

                        switch (alt5) {
                        case 1:
                            // GCLNewChecker.g:85:14: stat
                        {
                            _last = (MyTree) this.input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat217);
                            stat29 = stat();

                            this.state._fsp--;

                            if (_first_1 == null) {
                                _first_1 = stat29.tree;
                            }

                            retval.tree = _first_0;
                            if (this.adaptor.getParent(retval.tree) != null
                                && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                retval.tree =
                                    (MyTree) this.adaptor.getParent(retval.tree);
                            }
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

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 9:
                // GCLNewChecker.g:86:5: ^( STAR stat )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    STAR30 =
                        (MyTree) match(this.input, STAR, FOLLOW_STAR_in_stat226);

                    if (_first_0 == null) {
                        _first_0 = STAR30;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat228);
                    stat31 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat31.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 10:
                // GCLNewChecker.g:87:5: rule
            {
                _last = (MyTree) this.input.LT(1);
                pushFollow(FOLLOW_rule_in_stat235);
                rule32 = rule();

                this.state._fsp--;

                if (_first_0 == null) {
                    _first_0 = rule32.tree;
                }

                retval.tree = _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 11:
                // GCLNewChecker.g:88:5: ANY
            {
                _last = (MyTree) this.input.LT(1);
                ANY33 = (MyTree) match(this.input, ANY, FOLLOW_ANY_in_stat241);

                if (_first_0 == null) {
                    _first_0 = ANY33;
                }

                retval.tree = _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 12:
                // GCLNewChecker.g:89:5: OTHER
            {
                _last = (MyTree) this.input.LT(1);
                OTHER34 =
                    (MyTree) match(this.input, OTHER, FOLLOW_OTHER_in_stat247);

                if (_first_0 == null) {
                    _first_0 = OTHER34;
                }

                retval.tree = _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 13:
                // GCLNewChecker.g:90:5: TRUE
            {
                _last = (MyTree) this.input.LT(1);
                TRUE35 =
                    (MyTree) match(this.input, TRUE, FOLLOW_TRUE_in_stat253);

                if (_first_0 == null) {
                    _first_0 = TRUE35;
                }

                retval.tree = _first_0;
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

        @Override
        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "rule"
    // GCLNewChecker.g:93:1: rule : ^( CALL ID ( arg )* ) ;
    public final GCLNewChecker.rule_return rule() throws RecognitionException {
        GCLNewChecker.rule_return retval = new GCLNewChecker.rule_return();
        retval.start = this.input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree CALL36 = null;
        MyTree ID37 = null;
        GCLNewChecker.arg_return arg38 = null;

        MyTree CALL36_tree = null;
        MyTree ID37_tree = null;

        try {
            // GCLNewChecker.g:94:3: ( ^( CALL ID ( arg )* ) )
            // GCLNewChecker.g:94:5: ^( CALL ID ( arg )* )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    CALL36 =
                        (MyTree) match(this.input, CALL, FOLLOW_CALL_in_rule267);

                    if (_first_0 == null) {
                        _first_0 = CALL36;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    ID37 = (MyTree) match(this.input, ID, FOLLOW_ID_in_rule269);

                    if (_first_1 == null) {
                        _first_1 = ID37;
                    }
                    // GCLNewChecker.g:94:15: ( arg )*
                    loop7: do {
                        int alt7 = 2;
                        int LA7_0 = this.input.LA(1);

                        if ((LA7_0 == ARG)) {
                            alt7 = 1;
                        }

                        switch (alt7) {
                        case 1:
                            // GCLNewChecker.g:94:15: arg
                        {
                            _last = (MyTree) this.input.LT(1);
                            pushFollow(FOLLOW_arg_in_rule271);
                            arg38 = arg();

                            this.state._fsp--;

                            if (_first_1 == null) {
                                _first_1 = arg38.tree;
                            }

                            retval.tree = _first_0;
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
                    _last = _save_last_1;
                }

                this.helper.checkCall(CALL36_tree);

                retval.tree = _first_0;
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

    // $ANTLR end "rule"

    public static class var_decl_return extends TreeRuleReturnScope {
        MyTree tree;

        @Override
        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "var_decl"
    // GCLNewChecker.g:98:1: var_decl : ^( VAR type ID ) ;
    public final GCLNewChecker.var_decl_return var_decl()
        throws RecognitionException {
        GCLNewChecker.var_decl_return retval =
            new GCLNewChecker.var_decl_return();
        retval.start = this.input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree VAR39 = null;
        MyTree ID41 = null;
        GCLNewChecker.type_return type40 = null;

        MyTree VAR39_tree = null;
        MyTree ID41_tree = null;

        try {
            // GCLNewChecker.g:99:2: ( ^( VAR type ID ) )
            // GCLNewChecker.g:99:4: ^( VAR type ID )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    VAR39 =
                        (MyTree) match(this.input, VAR,
                            FOLLOW_VAR_in_var_decl294);

                    if (_first_0 == null) {
                        _first_0 = VAR39;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (MyTree) this.input.LT(1);
                    pushFollow(FOLLOW_type_in_var_decl296);
                    type40 = type();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = type40.tree;
                    }
                    _last = (MyTree) this.input.LT(1);
                    ID41 =
                        (MyTree) match(this.input, ID, FOLLOW_ID_in_var_decl298);

                    if (_first_1 == null) {
                        _first_1 = ID41;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                this.helper.declareVar(ID41_tree, (type40 != null
                        ? ((MyTree) type40.tree) : null));

                retval.tree = _first_0;
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

        @Override
        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "type"
    // GCLNewChecker.g:103:1: type : ( NODE | BOOL | STRING | INT | REAL );
    public final GCLNewChecker.type_return type() throws RecognitionException {
        GCLNewChecker.type_return retval = new GCLNewChecker.type_return();
        retval.start = this.input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree set42 = null;

        MyTree set42_tree = null;

        try {
            // GCLNewChecker.g:105:3: ( NODE | BOOL | STRING | INT | REAL )
            // GCLNewChecker.g:
            {
                _last = (MyTree) this.input.LT(1);
                set42 = (MyTree) this.input.LT(1);
                if ((this.input.LA(1) >= NODE && this.input.LA(1) <= REAL)) {
                    this.input.consume();

                    this.state.errorRecovery = false;
                } else {
                    MismatchedSetException mse =
                        new MismatchedSetException(null, this.input);
                    throw mse;
                }

                retval.tree = _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree = (MyTree) this.adaptor.getParent(retval.tree);
                }

            }

            this.helper.checkType((retval.tree));
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

        @Override
        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "arg"
    // GCLNewChecker.g:108:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final GCLNewChecker.arg_return arg() throws RecognitionException {
        GCLNewChecker.arg_return retval = new GCLNewChecker.arg_return();
        retval.start = this.input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree ARG43 = null;
        MyTree OUT44 = null;
        MyTree ID45 = null;
        MyTree DONT_CARE46 = null;
        GCLNewChecker.literal_return literal47 = null;

        MyTree ARG43_tree = null;
        MyTree OUT44_tree = null;
        MyTree ID45_tree = null;
        MyTree DONT_CARE46_tree = null;

        try {
            // GCLNewChecker.g:109:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // GCLNewChecker.g:109:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
                _last = (MyTree) this.input.LT(1);
                {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree) this.input.LT(1);
                    ARG43 =
                        (MyTree) match(this.input, ARG, FOLLOW_ARG_in_arg354);

                    if (_first_0 == null) {
                        _first_0 = ARG43;
                    }
                    match(this.input, Token.DOWN, null);
                    // GCLNewChecker.g:110:7: ( ( OUT )? ID | DONT_CARE | literal )
                    int alt9 = 3;
                    switch (this.input.LA(1)) {
                    case ID:
                    case OUT: {
                        alt9 = 1;
                    }
                        break;
                    case DONT_CARE: {
                        alt9 = 2;
                    }
                        break;
                    case TRUE:
                    case FALSE:
                    case STRING_LIT:
                    case INT_LIT:
                    case REAL_LIT: {
                        alt9 = 3;
                    }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 0, this.input);

                        throw nvae;
                    }

                    switch (alt9) {
                    case 1:
                        // GCLNewChecker.g:110:9: ( OUT )? ID
                    {
                        // GCLNewChecker.g:110:9: ( OUT )?
                        int alt8 = 2;
                        int LA8_0 = this.input.LA(1);

                        if ((LA8_0 == OUT)) {
                            alt8 = 1;
                        }
                        switch (alt8) {
                        case 1:
                            // GCLNewChecker.g:110:9: OUT
                        {
                            _last = (MyTree) this.input.LT(1);
                            OUT44 =
                                (MyTree) match(this.input, OUT,
                                    FOLLOW_OUT_in_arg365);

                            if (_first_1 == null) {
                                _first_1 = OUT44;
                            }

                            retval.tree = _first_0;
                            if (this.adaptor.getParent(retval.tree) != null
                                && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                retval.tree =
                                    (MyTree) this.adaptor.getParent(retval.tree);
                            }
                        }
                            break;

                        }

                        _last = (MyTree) this.input.LT(1);
                        ID45 =
                            (MyTree) match(this.input, ID, FOLLOW_ID_in_arg368);

                        if (_first_1 == null) {
                            _first_1 = ID45;
                        }
                        this.helper.checkVarArg(ARG43_tree);

                        retval.tree = _first_0;
                        if (this.adaptor.getParent(retval.tree) != null
                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                            retval.tree =
                                (MyTree) this.adaptor.getParent(retval.tree);
                        }
                    }
                        break;
                    case 2:
                        // GCLNewChecker.g:111:9: DONT_CARE
                    {
                        _last = (MyTree) this.input.LT(1);
                        DONT_CARE46 =
                            (MyTree) match(this.input, DONT_CARE,
                                FOLLOW_DONT_CARE_in_arg380);

                        if (_first_1 == null) {
                            _first_1 = DONT_CARE46;
                        }
                        this.helper.checkDontCareArg(ARG43_tree);

                        retval.tree = _first_0;
                        if (this.adaptor.getParent(retval.tree) != null
                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                            retval.tree =
                                (MyTree) this.adaptor.getParent(retval.tree);
                        }
                    }
                        break;
                    case 3:
                        // GCLNewChecker.g:112:9: literal
                    {
                        _last = (MyTree) this.input.LT(1);
                        pushFollow(FOLLOW_literal_in_arg392);
                        literal47 = literal();

                        this.state._fsp--;

                        if (_first_1 == null) {
                            _first_1 = literal47.tree;
                        }
                        this.helper.checkConstArg(ARG43_tree);

                        retval.tree = _first_0;
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

                retval.tree = _first_0;
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

        @Override
        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "literal"
    // GCLNewChecker.g:117:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final GCLNewChecker.literal_return literal()
        throws RecognitionException {
        GCLNewChecker.literal_return retval =
            new GCLNewChecker.literal_return();
        retval.start = this.input.LT(1);

        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree set48 = null;

        MyTree set48_tree = null;

        try {
            // GCLNewChecker.g:118:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // GCLNewChecker.g:
            {
                _last = (MyTree) this.input.LT(1);
                set48 = (MyTree) this.input.LT(1);
                if (this.input.LA(1) == TRUE
                    || (this.input.LA(1) >= FALSE && this.input.LA(1) <= REAL_LIT)) {
                    this.input.consume();

                    this.state.errorRecovery = false;
                } else {
                    MismatchedSetException mse =
                        new MismatchedSetException(null, this.input);
                    throw mse;
                }

                retval.tree = _first_0;
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
        new long[] {0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_program61 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions77 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions79 = new BitSet(
        new long[] {0x0000000000000088L});
    public static final BitSet FOLLOW_FUNCTION_in_function92 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function94 = new BitSet(
        new long[] {0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_function96 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block113 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block118 = new BitSet(
        new long[] {0x00000006A35C0928L});
    public static final BitSet FOLLOW_block_in_stat136 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat142 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat149 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat151 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat159 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat161 = new BitSet(
        new long[] {0x00000006A35C0928L});
    public static final BitSet FOLLOW_stat_in_stat163 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat171 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat173 = new BitSet(
        new long[] {0x00000006A35C0928L});
    public static final BitSet FOLLOW_stat_in_stat175 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat183 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat185 = new BitSet(
        new long[] {0x00000006A35C0928L});
    public static final BitSet FOLLOW_stat_in_stat188 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat198 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat200 = new BitSet(
        new long[] {0x00000006A35C0928L});
    public static final BitSet FOLLOW_stat_in_stat202 = new BitSet(
        new long[] {0x00000006A35C0928L});
    public static final BitSet FOLLOW_stat_in_stat205 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat215 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat217 = new BitSet(
        new long[] {0x00000006A35C0928L});
    public static final BitSet FOLLOW_STAR_in_stat226 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat228 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat235 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat241 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat247 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat253 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule267 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule269 = new BitSet(
        new long[] {0x0000000000001008L});
    public static final BitSet FOLLOW_arg_in_rule271 = new BitSet(
        new long[] {0x0000000000001008L});
    public static final BitSet FOLLOW_VAR_in_var_decl294 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl296 = new BitSet(
        new long[] {0x0000000000008000L});
    public static final BitSet FOLLOW_ID_in_var_decl298 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_type0 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_arg354 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg365 = new BitSet(
        new long[] {0x0000000000008000L});
    public static final BitSet FOLLOW_ID_in_arg368 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg380 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg392 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_literal0 = new BitSet(
        new long[] {0x0000000000000002L});

}