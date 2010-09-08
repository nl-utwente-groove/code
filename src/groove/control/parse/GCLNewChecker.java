// $ANTLR 3.2 Sep 23, 2009 12:02:23 GCLNewChecker.g 2010-09-08 15:26:59

package groove.control.parse;

import groove.trans.SPORule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.TreeAdaptor;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeParser;
import org.antlr.runtime.tree.TreeRuleReturnScope;

@SuppressWarnings("all")
public class GCLNewChecker extends TreeParser {
    public static final String[] tokenNames = new String[] {"<invalid>",
        "<EOR>", "<DOWN>", "<UP>", "PROGRAM", "BLOCK", "FUNCTIONS", "FUNCTION",
        "CALL", "DO_WHILE", "VAR", "ARG", "LCURLY", "RCURLY", "ID", "LPAR",
        "RPAR", "ALAP", "WHILE", "UNTIL", "DO", "IF", "ELSE", "TRY", "CHOICE",
        "CH_OR", "SEMI", "OR", "TRUE", "PLUS", "STAR", "SHARP", "ANY", "OTHER",
        "DOT", "COMMA", "NODE_TYPE", "BOOL_TYPE", "STRING_TYPE", "INT_TYPE",
        "REAL_TYPE", "OUT", "DONT_CARE", "FALSE", "QUOTE", "BSLASH", "MINUS",
        "NUMBER", "AND", "NOT", "ML_COMMENT", "SL_COMMENT", "WS"};
    public static final int FUNCTION = 7;
    public static final int STAR = 30;
    public static final int WHILE = 18;
    public static final int FUNCTIONS = 6;
    public static final int BOOL_TYPE = 37;
    public static final int NODE_TYPE = 36;
    public static final int DO = 20;
    public static final int NOT = 49;
    public static final int ALAP = 17;
    public static final int ID = 14;
    public static final int AND = 48;
    public static final int EOF = -1;
    public static final int IF = 21;
    public static final int ML_COMMENT = 50;
    public static final int QUOTE = 44;
    public static final int ARG = 11;
    public static final int LPAR = 15;
    public static final int COMMA = 35;
    public static final int DO_WHILE = 9;
    public static final int CH_OR = 25;
    public static final int PLUS = 29;
    public static final int VAR = 10;
    public static final int DOT = 34;
    public static final int CHOICE = 24;
    public static final int SHARP = 31;
    public static final int OTHER = 33;
    public static final int ELSE = 22;
    public static final int NUMBER = 47;
    public static final int LCURLY = 12;
    public static final int MINUS = 46;
    public static final int INT_TYPE = 39;
    public static final int SEMI = 26;
    public static final int TRUE = 28;
    public static final int TRY = 23;
    public static final int REAL_TYPE = 40;
    public static final int DONT_CARE = 42;
    public static final int WS = 52;
    public static final int ANY = 32;
    public static final int OUT = 41;
    public static final int UNTIL = 19;
    public static final int STRING_TYPE = 38;
    public static final int BLOCK = 5;
    public static final int OR = 27;
    public static final int RCURLY = 13;
    public static final int SL_COMMENT = 51;
    public static final int PROGRAM = 4;
    public static final int RPAR = 16;
    public static final int CALL = 8;
    public static final int FALSE = 43;
    public static final int BSLASH = 45;

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

    public String[] getTokenNames() {
        return GCLNewChecker.tokenNames;
    }

    public String getGrammarFileName() {
        return "GCLNewChecker.g";
    }

    private Namespace namespace;

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    private SymbolTable st = new SymbolTable();

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

    private List<String> syntaxInit = new ArrayList<String>();

    int numParameters = 0;
    SPORule currentRule;
    HashSet<String> currentOutputParameters = new HashSet<String>();

    private void debug(String msg) {
        if (this.namespace.usesVariables()) {
            //System.err.println("Variables debug (GCLChecker): "+msg);
        }
    }

    public static class program_return extends TreeRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "program"
    // GCLNewChecker.g:53:1: program : ^( PROGRAM functions block ) ;
    public final GCLNewChecker.program_return program()
        throws RecognitionException {
        GCLNewChecker.program_return retval =
            new GCLNewChecker.program_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree PROGRAM1 = null;
        GCLNewChecker.functions_return functions2 = null;

        GCLNewChecker.block_return block3 = null;

        CommonTree PROGRAM1_tree = null;

        try {
            // GCLNewChecker.g:54:3: ( ^( PROGRAM functions block ) )
            // GCLNewChecker.g:54:6: ^( PROGRAM functions block )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    PROGRAM1 =
                        (CommonTree) match(this.input, PROGRAM,
                            FOLLOW_PROGRAM_in_program57);

                    if (_first_0 == null) {
                        _first_0 = PROGRAM1;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_functions_in_program59);
                    functions2 = functions();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = functions2.tree;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_block_in_program61);
                    block3 = block();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = block3.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
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
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "functions"
    // GCLNewChecker.g:57:1: functions : ^(f= FUNCTIONS ( function )* ) ;
    public final GCLNewChecker.functions_return functions()
        throws RecognitionException {
        GCLNewChecker.functions_return retval =
            new GCLNewChecker.functions_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree f = null;
        GCLNewChecker.function_return function4 = null;

        CommonTree f_tree = null;

        try {
            // GCLNewChecker.g:58:3: ( ^(f= FUNCTIONS ( function )* ) )
            // GCLNewChecker.g:58:5: ^(f= FUNCTIONS ( function )* )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    f =
                        (CommonTree) match(this.input, FUNCTIONS,
                            FOLLOW_FUNCTIONS_in_functions79);

                    if (_first_0 == null) {
                        _first_0 = f;
                    }
                    this.namespace.storeProcs(f);

                    if (this.input.LA(1) == Token.DOWN) {
                        match(this.input, Token.DOWN, null);
                        // GCLNewChecker.g:58:48: ( function )*
                        loop1: do {
                            int alt1 = 2;
                            int LA1_0 = this.input.LA(1);

                            if ((LA1_0 == FUNCTION)) {
                                alt1 = 1;
                            }

                            switch (alt1) {
                            case 1:
                                // GCLNewChecker.g:58:48: function
                            {
                                _last = (CommonTree) this.input.LT(1);
                                pushFollow(FOLLOW_function_in_functions83);
                                function4 = function();

                                this.state._fsp--;

                                if (_first_1 == null) {
                                    _first_1 = function4.tree;
                                }

                                retval.tree = (CommonTree) _first_0;
                                if (this.adaptor.getParent(retval.tree) != null
                                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                    retval.tree =
                                        (CommonTree) this.adaptor.getParent(retval.tree);
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

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
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
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "function"
    // GCLNewChecker.g:60:1: function : ^( FUNCTION ID block ) ;
    public final GCLNewChecker.function_return function()
        throws RecognitionException {
        GCLNewChecker.function_return retval =
            new GCLNewChecker.function_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree FUNCTION5 = null;
        CommonTree ID6 = null;
        GCLNewChecker.block_return block7 = null;

        CommonTree FUNCTION5_tree = null;
        CommonTree ID6_tree = null;

        try {
            // GCLNewChecker.g:61:3: ( ^( FUNCTION ID block ) )
            // GCLNewChecker.g:61:5: ^( FUNCTION ID block )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    FUNCTION5 =
                        (CommonTree) match(this.input, FUNCTION,
                            FOLLOW_FUNCTION_in_function96);

                    if (_first_0 == null) {
                        _first_0 = FUNCTION5;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    ID6 =
                        (CommonTree) match(this.input, ID,
                            FOLLOW_ID_in_function98);

                    if (_first_1 == null) {
                        _first_1 = ID6;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_block_in_function100);
                    block7 = block();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = block7.tree;
                    }

                    debug("proc: " + (ID6 != null ? ID6.getText() : null));
                    if (this.namespace.hasRule((ID6 != null ? ID6.getText()
                            : null))) {
                        this.errors.add("There already exists a rule with the name: "
                            + (ID6 != null ? ID6.getText() : null));
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
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
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "block"
    // GCLNewChecker.g:68:1: block : ^( BLOCK ( stat )* ) ;
    public final GCLNewChecker.block_return block() throws RecognitionException {
        GCLNewChecker.block_return retval = new GCLNewChecker.block_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree BLOCK8 = null;
        GCLNewChecker.stat_return stat9 = null;

        CommonTree BLOCK8_tree = null;

        try {
            // GCLNewChecker.g:69:3: ( ^( BLOCK ( stat )* ) )
            // GCLNewChecker.g:69:5: ^( BLOCK ( stat )* )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    BLOCK8 =
                        (CommonTree) match(this.input, BLOCK,
                            FOLLOW_BLOCK_in_block117);

                    if (_first_0 == null) {
                        _first_0 = BLOCK8;
                    }
                    this.st.openScope();

                    if (this.input.LA(1) == Token.DOWN) {
                        match(this.input, Token.DOWN, null);
                        // GCLNewChecker.g:69:33: ( stat )*
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
                                // GCLNewChecker.g:69:34: stat
                            {
                                _last = (CommonTree) this.input.LT(1);
                                pushFollow(FOLLOW_stat_in_block122);
                                stat9 = stat();

                                this.state._fsp--;

                                if (_first_1 == null) {
                                    _first_1 = stat9.tree;
                                }

                                retval.tree = (CommonTree) _first_0;
                                if (this.adaptor.getParent(retval.tree) != null
                                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                    retval.tree =
                                        (CommonTree) this.adaptor.getParent(retval.tree);
                                }
                            }
                                break;

                            default:
                                break loop2;
                            }
                        } while (true);

                        this.st.closeScope();

                        match(this.input, Token.UP, null);
                    }
                    _last = _save_last_1;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
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
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "stat"
    // GCLNewChecker.g:72:1: stat : ( block | var_decl | ^( ALAP stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE ( stat )+ ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE );
    public final GCLNewChecker.stat_return stat() throws RecognitionException {
        GCLNewChecker.stat_return retval = new GCLNewChecker.stat_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree ALAP12 = null;
        CommonTree WHILE14 = null;
        CommonTree UNTIL17 = null;
        CommonTree TRY20 = null;
        CommonTree IF23 = null;
        CommonTree CHOICE27 = null;
        CommonTree STAR29 = null;
        CommonTree ANY32 = null;
        CommonTree OTHER33 = null;
        CommonTree TRUE34 = null;
        GCLNewChecker.block_return block10 = null;

        GCLNewChecker.var_decl_return var_decl11 = null;

        GCLNewChecker.stat_return stat13 = null;

        GCLNewChecker.stat_return stat15 = null;

        GCLNewChecker.stat_return stat16 = null;

        GCLNewChecker.stat_return stat18 = null;

        GCLNewChecker.stat_return stat19 = null;

        GCLNewChecker.stat_return stat21 = null;

        GCLNewChecker.stat_return stat22 = null;

        GCLNewChecker.stat_return stat24 = null;

        GCLNewChecker.stat_return stat25 = null;

        GCLNewChecker.stat_return stat26 = null;

        GCLNewChecker.stat_return stat28 = null;

        GCLNewChecker.stat_return stat30 = null;

        GCLNewChecker.rule_return rule31 = null;

        CommonTree ALAP12_tree = null;
        CommonTree WHILE14_tree = null;
        CommonTree UNTIL17_tree = null;
        CommonTree TRY20_tree = null;
        CommonTree IF23_tree = null;
        CommonTree CHOICE27_tree = null;
        CommonTree STAR29_tree = null;
        CommonTree ANY32_tree = null;
        CommonTree OTHER33_tree = null;
        CommonTree TRUE34_tree = null;

        try {
            // GCLNewChecker.g:73:3: ( block | var_decl | ^( ALAP stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE ( stat )+ ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE )
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
                // GCLNewChecker.g:73:5: block
            {
                _last = (CommonTree) this.input.LT(1);
                pushFollow(FOLLOW_block_in_stat140);
                block10 = block();

                this.state._fsp--;

                if (_first_0 == null) {
                    _first_0 = block10.tree;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 2:
                // GCLNewChecker.g:74:5: var_decl
            {
                _last = (CommonTree) this.input.LT(1);
                pushFollow(FOLLOW_var_decl_in_stat146);
                var_decl11 = var_decl();

                this.state._fsp--;

                if (_first_0 == null) {
                    _first_0 = var_decl11.tree;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 3:
                // GCLNewChecker.g:75:5: ^( ALAP stat )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    ALAP12 =
                        (CommonTree) match(this.input, ALAP,
                            FOLLOW_ALAP_in_stat153);

                    if (_first_0 == null) {
                        _first_0 = ALAP12;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat155);
                    stat13 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat13.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 4:
                // GCLNewChecker.g:76:5: ^( WHILE stat stat )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    WHILE14 =
                        (CommonTree) match(this.input, WHILE,
                            FOLLOW_WHILE_in_stat163);

                    if (_first_0 == null) {
                        _first_0 = WHILE14;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat165);
                    stat15 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat15.tree;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat167);
                    stat16 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat16.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 5:
                // GCLNewChecker.g:77:5: ^( UNTIL stat stat )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    UNTIL17 =
                        (CommonTree) match(this.input, UNTIL,
                            FOLLOW_UNTIL_in_stat175);

                    if (_first_0 == null) {
                        _first_0 = UNTIL17;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat177);
                    stat18 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat18.tree;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat179);
                    stat19 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat19.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 6:
                // GCLNewChecker.g:78:5: ^( TRY stat ( stat )? )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    TRY20 =
                        (CommonTree) match(this.input, TRY,
                            FOLLOW_TRY_in_stat187);

                    if (_first_0 == null) {
                        _first_0 = TRY20;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat189);
                    stat21 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat21.tree;
                    }
                    // GCLNewChecker.g:78:16: ( stat )?
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
                        // GCLNewChecker.g:78:17: stat
                    {
                        _last = (CommonTree) this.input.LT(1);
                        pushFollow(FOLLOW_stat_in_stat192);
                        stat22 = stat();

                        this.state._fsp--;

                        if (_first_1 == null) {
                            _first_1 = stat22.tree;
                        }

                        retval.tree = (CommonTree) _first_0;
                        if (this.adaptor.getParent(retval.tree) != null
                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                            retval.tree =
                                (CommonTree) this.adaptor.getParent(retval.tree);
                        }
                    }
                        break;

                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 7:
                // GCLNewChecker.g:79:5: ^( IF stat stat ( stat )? )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    IF23 =
                        (CommonTree) match(this.input, IF, FOLLOW_IF_in_stat202);

                    if (_first_0 == null) {
                        _first_0 = IF23;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat204);
                    stat24 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat24.tree;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat206);
                    stat25 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat25.tree;
                    }
                    // GCLNewChecker.g:79:20: ( stat )?
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
                        // GCLNewChecker.g:79:21: stat
                    {
                        _last = (CommonTree) this.input.LT(1);
                        pushFollow(FOLLOW_stat_in_stat209);
                        stat26 = stat();

                        this.state._fsp--;

                        if (_first_1 == null) {
                            _first_1 = stat26.tree;
                        }

                        retval.tree = (CommonTree) _first_0;
                        if (this.adaptor.getParent(retval.tree) != null
                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                            retval.tree =
                                (CommonTree) this.adaptor.getParent(retval.tree);
                        }
                    }
                        break;

                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 8:
                // GCLNewChecker.g:80:5: ^( CHOICE ( stat )+ )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    CHOICE27 =
                        (CommonTree) match(this.input, CHOICE,
                            FOLLOW_CHOICE_in_stat219);

                    if (_first_0 == null) {
                        _first_0 = CHOICE27;
                    }
                    match(this.input, Token.DOWN, null);
                    // GCLNewChecker.g:80:14: ( stat )+
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
                            // GCLNewChecker.g:80:14: stat
                        {
                            _last = (CommonTree) this.input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat221);
                            stat28 = stat();

                            this.state._fsp--;

                            if (_first_1 == null) {
                                _first_1 = stat28.tree;
                            }

                            retval.tree = (CommonTree) _first_0;
                            if (this.adaptor.getParent(retval.tree) != null
                                && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                retval.tree =
                                    (CommonTree) this.adaptor.getParent(retval.tree);
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

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 9:
                // GCLNewChecker.g:81:5: ^( STAR stat )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    STAR29 =
                        (CommonTree) match(this.input, STAR,
                            FOLLOW_STAR_in_stat230);

                    if (_first_0 == null) {
                        _first_0 = STAR29;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat232);
                    stat30 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat30.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 10:
                // GCLNewChecker.g:82:5: rule
            {
                _last = (CommonTree) this.input.LT(1);
                pushFollow(FOLLOW_rule_in_stat239);
                rule31 = rule();

                this.state._fsp--;

                if (_first_0 == null) {
                    _first_0 = rule31.tree;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 11:
                // GCLNewChecker.g:83:5: ANY
            {
                _last = (CommonTree) this.input.LT(1);
                ANY32 =
                    (CommonTree) match(this.input, ANY, FOLLOW_ANY_in_stat245);

                if (_first_0 == null) {
                    _first_0 = ANY32;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 12:
                // GCLNewChecker.g:84:5: OTHER
            {
                _last = (CommonTree) this.input.LT(1);
                OTHER33 =
                    (CommonTree) match(this.input, OTHER,
                        FOLLOW_OTHER_in_stat251);

                if (_first_0 == null) {
                    _first_0 = OTHER33;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 13:
                // GCLNewChecker.g:85:5: TRUE
            {
                _last = (CommonTree) this.input.LT(1);
                TRUE34 =
                    (CommonTree) match(this.input, TRUE, FOLLOW_TRUE_in_stat257);

                if (_first_0 == null) {
                    _first_0 = TRUE34;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
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
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "rule"
    // GCLNewChecker.g:88:1: rule : ^( CALL r= ID ( arg )* ) ;
    public final GCLNewChecker.rule_return rule() throws RecognitionException {
        GCLNewChecker.rule_return retval = new GCLNewChecker.rule_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree r = null;
        CommonTree CALL35 = null;
        GCLNewChecker.arg_return arg36 = null;

        CommonTree r_tree = null;
        CommonTree CALL35_tree = null;

        try {
            // GCLNewChecker.g:89:3: ( ^( CALL r= ID ( arg )* ) )
            // GCLNewChecker.g:89:5: ^( CALL r= ID ( arg )* )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    CALL35 =
                        (CommonTree) match(this.input, CALL,
                            FOLLOW_CALL_in_rule271);

                    if (_first_0 == null) {
                        _first_0 = CALL35;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    r =
                        (CommonTree) match(this.input, ID, FOLLOW_ID_in_rule275);

                    if (_first_1 == null) {
                        _first_1 = r;
                    }
                    this.currentRule = this.namespace.getRule(r.getText());
                    // GCLNewChecker.g:89:67: ( arg )*
                    loop7: do {
                        int alt7 = 2;
                        int LA7_0 = this.input.LA(1);

                        if ((LA7_0 == ARG)) {
                            alt7 = 1;
                        }

                        switch (alt7) {
                        case 1:
                            // GCLNewChecker.g:89:67: arg
                        {
                            _last = (CommonTree) this.input.LT(1);
                            pushFollow(FOLLOW_arg_in_rule279);
                            arg36 = arg();

                            this.state._fsp--;

                            if (_first_1 == null) {
                                _first_1 = arg36.tree;
                            }

                            retval.tree = (CommonTree) _first_0;
                            if (this.adaptor.getParent(retval.tree) != null
                                && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                retval.tree =
                                    (CommonTree) this.adaptor.getParent(retval.tree);
                            }
                        }
                            break;

                        default:
                            break loop7;
                        }
                    } while (true);

                    debug("checking if " + (r != null ? r.getText() : null)
                        + " exists");
                    if (!this.namespace.hasRule((r != null ? r.getText() : null))
                        && !this.namespace.hasProc((r != null ? r.getText()
                                : null))) {
                        this.errors.add("No such rule: "
                            + (r != null ? r.getText() : null) + " on line "
                            + (r != null ? r.getLine() : 0));
                    } else if (this.numParameters != 0
                        && this.numParameters != this.currentRule.getVisibleParCount()) {
                        this.errors.add("The number of parameters used in this call of "
                            + this.currentRule.getName().toString()
                            + " ("
                            + this.numParameters
                            + ") does not match the number of parameters defined in the rule ("
                            + this.currentRule.getVisibleParCount()
                            + ") on line " + (r != null ? r.getLine() : 0));
                    }
                    if (this.numParameters == 0 && this.currentRule != null
                        && this.currentRule.hasRequiredInputs()) {
                        this.errors.add("The rule "
                            + this.currentRule.getName().toString()
                            + " has required input parameters on line "
                            + (r != null ? r.getLine() : 0));
                    }
                    this.numParameters = 0;
                    this.currentOutputParameters.clear();

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
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
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "var_decl"
    // GCLNewChecker.g:104:1: var_decl : ^( VAR t= ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE ) ID ) ;
    public final GCLNewChecker.var_decl_return var_decl()
        throws RecognitionException {
        GCLNewChecker.var_decl_return retval =
            new GCLNewChecker.var_decl_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree t = null;
        CommonTree VAR37 = null;
        CommonTree ID38 = null;

        CommonTree t_tree = null;
        CommonTree VAR37_tree = null;
        CommonTree ID38_tree = null;

        try {
            // GCLNewChecker.g:105:2: ( ^( VAR t= ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE ) ID ) )
            // GCLNewChecker.g:105:4: ^( VAR t= ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE ) ID )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    VAR37 =
                        (CommonTree) match(this.input, VAR,
                            FOLLOW_VAR_in_var_decl297);

                    if (_first_0 == null) {
                        _first_0 = VAR37;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    t = (CommonTree) this.input.LT(1);
                    if ((this.input.LA(1) >= NODE_TYPE && this.input.LA(1) <= REAL_TYPE)) {
                        this.input.consume();

                        this.state.errorRecovery = false;
                    } else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null, this.input);
                        throw mse;
                    }

                    _last = (CommonTree) this.input.LT(1);
                    ID38 =
                        (CommonTree) match(this.input, ID,
                            FOLLOW_ID_in_var_decl321);

                    if (_first_1 == null) {
                        _first_1 = ID38;
                    }

                    if (!this.namespace.hasVariable((ID38 != null
                            ? ID38.getText() : null))) {
                        this.namespace.addVariable((ID38 != null
                                ? ID38.getText() : null));
                        this.st.declareSymbol((ID38 != null ? ID38.getText()
                                : null), t.getText());
                    } else {
                        this.errors.add("Double declaration of variable '"
                            + (ID38 != null ? ID38.getText() : null)
                            + "' on line "
                            + (ID38 != null ? ID38.getLine() : 0));
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
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

    public static class arg_return extends TreeRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "arg"
    // GCLNewChecker.g:115:1: arg : ( ^( ARG ID ) | ^( ARG OUT ID ) | ^( ARG DONT_CARE ) | ^( ARG BOOL_TYPE bool= ( TRUE | FALSE ) ) | ^( ARG STRING_TYPE str= ID ) | ^( ARG INT_TYPE in= ID ) | ^( ARG REAL_TYPE r= ID ) );
    public final GCLNewChecker.arg_return arg() throws RecognitionException {
        GCLNewChecker.arg_return retval = new GCLNewChecker.arg_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree bool = null;
        CommonTree str = null;
        CommonTree in = null;
        CommonTree r = null;
        CommonTree ARG39 = null;
        CommonTree ID40 = null;
        CommonTree ARG41 = null;
        CommonTree OUT42 = null;
        CommonTree ID43 = null;
        CommonTree ARG44 = null;
        CommonTree DONT_CARE45 = null;
        CommonTree ARG46 = null;
        CommonTree BOOL_TYPE47 = null;
        CommonTree ARG48 = null;
        CommonTree STRING_TYPE49 = null;
        CommonTree ARG50 = null;
        CommonTree INT_TYPE51 = null;
        CommonTree ARG52 = null;
        CommonTree REAL_TYPE53 = null;

        CommonTree bool_tree = null;
        CommonTree str_tree = null;
        CommonTree in_tree = null;
        CommonTree r_tree = null;
        CommonTree ARG39_tree = null;
        CommonTree ID40_tree = null;
        CommonTree ARG41_tree = null;
        CommonTree OUT42_tree = null;
        CommonTree ID43_tree = null;
        CommonTree ARG44_tree = null;
        CommonTree DONT_CARE45_tree = null;
        CommonTree ARG46_tree = null;
        CommonTree BOOL_TYPE47_tree = null;
        CommonTree ARG48_tree = null;
        CommonTree STRING_TYPE49_tree = null;
        CommonTree ARG50_tree = null;
        CommonTree INT_TYPE51_tree = null;
        CommonTree ARG52_tree = null;
        CommonTree REAL_TYPE53_tree = null;

        try {
            // GCLNewChecker.g:116:2: ( ^( ARG ID ) | ^( ARG OUT ID ) | ^( ARG DONT_CARE ) | ^( ARG BOOL_TYPE bool= ( TRUE | FALSE ) ) | ^( ARG STRING_TYPE str= ID ) | ^( ARG INT_TYPE in= ID ) | ^( ARG REAL_TYPE r= ID ) )
            int alt8 = 7;
            alt8 = this.dfa8.predict(this.input);
            switch (alt8) {
            case 1:
                // GCLNewChecker.g:116:4: ^( ARG ID )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    ARG39 =
                        (CommonTree) match(this.input, ARG,
                            FOLLOW_ARG_in_arg337);

                    if (_first_0 == null) {
                        _first_0 = ARG39;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    ID40 =
                        (CommonTree) match(this.input, ID, FOLLOW_ID_in_arg339);

                    if (_first_1 == null) {
                        _first_1 = ID40;
                    }

                    this.numParameters++;
                    if (this.st.isDeclared((ID40 != null ? ID40.getText()
                            : null))) {
                        if (!this.st.isInitialized((ID40 != null
                                ? ID40.getText() : null))) {
                            this.errors.add("The variable "
                                + (ID40 != null ? ID40.getText() : null)
                                + " might not have been initialized on line "
                                + (ID40 != null ? ID40.getLine() : 0));
                        }
                    } else {
                        this.errors.add("No such variable: "
                            + (ID40 != null ? ID40.getText() : null));
                    }
                    if (this.currentRule != null
                        && this.currentRule.getNumberOfParameters() < this.numParameters) {
                        this.errors.add("Rule "
                            + this.currentRule.getName().toString()
                            + " does not have this many parameters on line "
                            + (ID40 != null ? ID40.getLine() : 0));
                    } else {
                        if (this.currentRule != null
                            && !this.currentRule.isInputParameter(this.numParameters)) {
                            this.errors.add("Parameter number "
                                + (this.numParameters)
                                + " cannot be an input parameter in rule "
                                + this.currentRule.getName().toString()
                                + " on line "
                                + (ID40 != null ? ID40.getLine() : 0));
                        }
                        if (this.currentRule != null
                            && !this.currentRule.getAttributeParameterType(
                                this.numParameters).equals(
                                this.st.getType((ID40 != null ? ID40.getText()
                                        : null)))) {
                            this.errors.add("Type mismatch between parameter "
                                + this.numParameters
                                + " of "
                                + this.currentRule.getName().toString()
                                + " and variable "
                                + (ID40 != null ? ID40.getText() : null)
                                + " ("
                                + this.currentRule.getAttributeParameterType(this.numParameters)
                                + " is not "
                                + this.st.getType((ID40 != null
                                        ? ID40.getText() : null)) + ")");
                        }
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 2:
                // GCLNewChecker.g:136:4: ^( ARG OUT ID )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    ARG41 =
                        (CommonTree) match(this.input, ARG,
                            FOLLOW_ARG_in_arg349);

                    if (_first_0 == null) {
                        _first_0 = ARG41;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    OUT42 =
                        (CommonTree) match(this.input, OUT,
                            FOLLOW_OUT_in_arg351);

                    if (_first_1 == null) {
                        _first_1 = OUT42;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    ID43 =
                        (CommonTree) match(this.input, ID, FOLLOW_ID_in_arg353);

                    if (_first_1 == null) {
                        _first_1 = ID43;
                    }

                    this.numParameters++;

                    if (this.st.isDeclared((ID43 != null ? ID43.getText()
                            : null))) {
                        if (!this.st.canInitialize((ID43 != null
                                ? ID43.getText() : null))
                            || this.syntaxInit.contains((ID43 != null
                                    ? ID43.getText() : null))) {
                            this.errors.add("Variable already initialized: "
                                + (ID43 != null ? ID43.getText() : null)
                                + " on line "
                                + (ID43 != null ? ID43.getLine() : 0));
                        } else {
                            this.st.initializeSymbol((ID43 != null
                                    ? ID43.getText() : null));
                            this.syntaxInit.add((ID43 != null ? ID43.getText()
                                    : null));
                        }
                    } else {
                        this.errors.add("No such variable: "
                            + (ID43 != null ? ID43.getText() : null)
                            + " on line " + (ID43 != null ? ID43.getLine() : 0));
                    }
                    if (this.currentRule != null
                        && this.currentRule.getNumberOfParameters() < this.numParameters) {
                        this.errors.add("Rule "
                            + this.currentRule.getName().toString()
                            + " does not have this many parameters on line "
                            + (ID43 != null ? ID43.getLine() : 0));
                    } else {
                        if (this.currentRule != null
                            && !this.currentRule.isOutputParameter(this.numParameters)) {
                            this.errors.add("Parameter number "
                                + (this.numParameters)
                                + " cannot be an output parameter in rule "
                                + this.currentRule.getName().toString()
                                + " on line "
                                + (ID43 != null ? ID43.getLine() : 0));
                        }
                        if (this.currentOutputParameters.contains((ID43 != null
                                ? ID43.getText() : null))) {
                            this.errors.add("You can not use the same parameter as output more than once per call: "
                                + (ID43 != null ? ID43.getText() : null)
                                + " on line "
                                + (ID43 != null ? ID43.getLine() : 0));
                        }
                        if (this.currentRule != null
                            && !this.currentRule.getAttributeParameterType(
                                this.numParameters).equals(
                                this.st.getType((ID43 != null ? ID43.getText()
                                        : null)))) {
                            this.errors.add("Type mismatch between parameter "
                                + this.numParameters
                                + " of "
                                + this.currentRule.getName().toString()
                                + " and variable "
                                + (ID43 != null ? ID43.getText() : null)
                                + " ("
                                + this.currentRule.getAttributeParameterType(this.numParameters)
                                + " is not "
                                + this.st.getType((ID43 != null
                                        ? ID43.getText() : null)) + ")");
                        }
                        if (this.currentRule != null
                            && this.currentRule.isRequiredInput(this.numParameters)) {
                            this.errors.add("Parameter " + this.numParameters
                                + " of rule "
                                + this.currentRule.getName().toString()
                                + " must be an input parameter.");
                        }
                    }
                    this.currentOutputParameters.add((ID43 != null
                            ? ID43.getText() : null));

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 3:
                // GCLNewChecker.g:167:4: ^( ARG DONT_CARE )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    ARG44 =
                        (CommonTree) match(this.input, ARG,
                            FOLLOW_ARG_in_arg363);

                    if (_first_0 == null) {
                        _first_0 = ARG44;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    DONT_CARE45 =
                        (CommonTree) match(this.input, DONT_CARE,
                            FOLLOW_DONT_CARE_in_arg365);

                    if (_first_1 == null) {
                        _first_1 = DONT_CARE45;
                    }

                    this.numParameters++;
                    if (this.currentRule != null
                        && this.currentRule.isRequiredInput(this.numParameters)) {
                        this.errors.add("Parameter " + this.numParameters
                            + " of rule "
                            + this.currentRule.getName().toString()
                            + " must be an input parameter.");
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 4:
                // GCLNewChecker.g:173:4: ^( ARG BOOL_TYPE bool= ( TRUE | FALSE ) )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    ARG46 =
                        (CommonTree) match(this.input, ARG,
                            FOLLOW_ARG_in_arg374);

                    if (_first_0 == null) {
                        _first_0 = ARG46;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    BOOL_TYPE47 =
                        (CommonTree) match(this.input, BOOL_TYPE,
                            FOLLOW_BOOL_TYPE_in_arg376);

                    if (_first_1 == null) {
                        _first_1 = BOOL_TYPE47;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    bool = (CommonTree) this.input.LT(1);
                    if (this.input.LA(1) == TRUE || this.input.LA(1) == FALSE) {
                        this.input.consume();

                        this.state.errorRecovery = false;
                    } else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null, this.input);
                        throw mse;
                    }

                    this.numParameters++;
                    if (this.currentRule != null
                        && !this.currentRule.getAttributeParameterType(
                            this.numParameters).equals("bool")) {
                        this.errors.add("Type mismatch between parameter "
                            + this.numParameters
                            + " of "
                            + this.currentRule.getName().toString()
                            + " and '"
                            + bool.getText()
                            + "' on line "
                            + bool.getLine()
                            + " ("
                            + this.currentRule.getAttributeParameterType(this.numParameters)
                            + " is not bool)");
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 5:
                // GCLNewChecker.g:179:4: ^( ARG STRING_TYPE str= ID )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    ARG48 =
                        (CommonTree) match(this.input, ARG,
                            FOLLOW_ARG_in_arg393);

                    if (_first_0 == null) {
                        _first_0 = ARG48;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    STRING_TYPE49 =
                        (CommonTree) match(this.input, STRING_TYPE,
                            FOLLOW_STRING_TYPE_in_arg395);

                    if (_first_1 == null) {
                        _first_1 = STRING_TYPE49;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    str =
                        (CommonTree) match(this.input, ID, FOLLOW_ID_in_arg399);

                    if (_first_1 == null) {
                        _first_1 = str;
                    }

                    this.numParameters++;
                    if (this.currentRule != null
                        && !this.currentRule.getAttributeParameterType(
                            this.numParameters).equals("string")) {
                        this.errors.add("Type mismatch between parameter "
                            + this.numParameters
                            + " of "
                            + this.currentRule.getName().toString()
                            + " and "
                            + str.getText()
                            + " on line "
                            + str.getLine()
                            + " ("
                            + this.currentRule.getAttributeParameterType(this.numParameters)
                            + " is not string)");
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 6:
                // GCLNewChecker.g:185:4: ^( ARG INT_TYPE in= ID )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    ARG50 =
                        (CommonTree) match(this.input, ARG,
                            FOLLOW_ARG_in_arg408);

                    if (_first_0 == null) {
                        _first_0 = ARG50;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    INT_TYPE51 =
                        (CommonTree) match(this.input, INT_TYPE,
                            FOLLOW_INT_TYPE_in_arg410);

                    if (_first_1 == null) {
                        _first_1 = INT_TYPE51;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    in =
                        (CommonTree) match(this.input, ID, FOLLOW_ID_in_arg414);

                    if (_first_1 == null) {
                        _first_1 = in;
                    }

                    this.numParameters++;
                    if (this.currentRule != null
                        && !this.currentRule.getAttributeParameterType(
                            this.numParameters).equals("int")) {
                        this.errors.add("Type mismatch between parameter "
                            + this.numParameters
                            + " of "
                            + this.currentRule.getName().toString()
                            + " and '"
                            + in.getText()
                            + "' on line "
                            + in.getLine()
                            + " ("
                            + this.currentRule.getAttributeParameterType(this.numParameters)
                            + " is not int)");
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 7:
                // GCLNewChecker.g:191:4: ^( ARG REAL_TYPE r= ID )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    ARG52 =
                        (CommonTree) match(this.input, ARG,
                            FOLLOW_ARG_in_arg423);

                    if (_first_0 == null) {
                        _first_0 = ARG52;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    REAL_TYPE53 =
                        (CommonTree) match(this.input, REAL_TYPE,
                            FOLLOW_REAL_TYPE_in_arg425);

                    if (_first_1 == null) {
                        _first_1 = REAL_TYPE53;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    r = (CommonTree) match(this.input, ID, FOLLOW_ID_in_arg429);

                    if (_first_1 == null) {
                        _first_1 = r;
                    }

                    this.numParameters++;
                    if (r.getText().equals(".")) {
                        this.errors.add("'.' is not a valid real value on line "
                            + r.getLine());
                    }
                    if (this.currentRule != null
                        && !this.currentRule.getAttributeParameterType(
                            this.numParameters).equals("real")) {
                        this.errors.add("Type mismatch between parameter "
                            + this.numParameters
                            + " of "
                            + this.currentRule.getName().toString()
                            + " and '"
                            + r.getText()
                            + "' on line "
                            + r.getLine()
                            + " ("
                            + this.currentRule.getAttributeParameterType(this.numParameters)
                            + " is not real)");
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CommonTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CommonTree) this.adaptor.getParent(retval.tree);
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

    // $ANTLR end "arg"

    // Delegated rules

    protected DFA8 dfa8 = new DFA8(this);
    static final String DFA8_eotS = "\12\uffff";
    static final String DFA8_eofS = "\12\uffff";
    static final String DFA8_minS = "\1\13\1\2\1\16\7\uffff";
    static final String DFA8_maxS = "\1\13\1\2\1\52\7\uffff";
    static final String DFA8_acceptS = "\3\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7";
    static final String DFA8_specialS = "\12\uffff}>";
    static final String[] DFA8_transitionS = {"\1\1", "\1\2",
        "\1\3\26\uffff\1\6\1\7\1\10\1\11\1\4\1\5", "", "", "", "", "", "", ""};

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }

        public String getDescription() {
            return "115:1: arg : ( ^( ARG ID ) | ^( ARG OUT ID ) | ^( ARG DONT_CARE ) | ^( ARG BOOL_TYPE bool= ( TRUE | FALSE ) ) | ^( ARG STRING_TYPE str= ID ) | ^( ARG INT_TYPE in= ID ) | ^( ARG REAL_TYPE r= ID ) );";
        }
    }

    public static final BitSet FOLLOW_PROGRAM_in_program57 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_functions_in_program59 = new BitSet(
        new long[] {0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_program61 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions79 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions83 = new BitSet(
        new long[] {0x0000000000000088L});
    public static final BitSet FOLLOW_FUNCTION_in_function96 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function98 = new BitSet(
        new long[] {0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_function100 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block117 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block122 = new BitSet(
        new long[] {0x0000000351AE0528L});
    public static final BitSet FOLLOW_block_in_stat140 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat146 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat153 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat155 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat163 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat165 = new BitSet(
        new long[] {0x0000000351AE0528L});
    public static final BitSet FOLLOW_stat_in_stat167 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat175 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat177 = new BitSet(
        new long[] {0x0000000351AE0528L});
    public static final BitSet FOLLOW_stat_in_stat179 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat187 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat189 = new BitSet(
        new long[] {0x0000000351AE0528L});
    public static final BitSet FOLLOW_stat_in_stat192 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat202 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat204 = new BitSet(
        new long[] {0x0000000351AE0528L});
    public static final BitSet FOLLOW_stat_in_stat206 = new BitSet(
        new long[] {0x0000000351AE0528L});
    public static final BitSet FOLLOW_stat_in_stat209 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat219 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat221 = new BitSet(
        new long[] {0x0000000351AE0528L});
    public static final BitSet FOLLOW_STAR_in_stat230 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat232 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat239 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat245 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat251 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat257 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule271 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule275 = new BitSet(
        new long[] {0x0000000000000808L});
    public static final BitSet FOLLOW_arg_in_rule279 = new BitSet(
        new long[] {0x0000000000000808L});
    public static final BitSet FOLLOW_VAR_in_var_decl297 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_var_decl301 = new BitSet(
        new long[] {0x0000000000004000L});
    public static final BitSet FOLLOW_ID_in_var_decl321 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_ARG_in_arg337 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_arg339 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_ARG_in_arg349 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg351 = new BitSet(
        new long[] {0x0000000000004000L});
    public static final BitSet FOLLOW_ID_in_arg353 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_ARG_in_arg363 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg365 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_ARG_in_arg374 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_TYPE_in_arg376 = new BitSet(
        new long[] {0x0000080010000000L});
    public static final BitSet FOLLOW_set_in_arg380 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_ARG_in_arg393 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_TYPE_in_arg395 = new BitSet(
        new long[] {0x0000000000004000L});
    public static final BitSet FOLLOW_ID_in_arg399 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_ARG_in_arg408 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_INT_TYPE_in_arg410 = new BitSet(
        new long[] {0x0000000000004000L});
    public static final BitSet FOLLOW_ID_in_arg414 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_ARG_in_arg423 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_REAL_TYPE_in_arg425 = new BitSet(
        new long[] {0x0000000000004000L});
    public static final BitSet FOLLOW_ID_in_arg429 = new BitSet(
        new long[] {0x0000000000000008L});

}