// $ANTLR 3.2 Sep 23, 2009 12:02:23 GCLChecker.g 2010-06-07 09:24:14

package groove.control.parse;

import groove.control.ControlAutomaton;
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
import org.antlr.runtime.tree.RewriteRuleNodeStream;
import org.antlr.runtime.tree.RewriteRuleSubtreeStream;
import org.antlr.runtime.tree.TreeAdaptor;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeParser;
import org.antlr.runtime.tree.TreeRuleReturnScope;

@SuppressWarnings("all")
public class GCLChecker extends TreeParser {
    public static final String[] tokenNames =
        new String[] {"<invalid>", "<EOR>", "<DOWN>", "<UP>", "PROGRAM",
            "BLOCK", "FUNCTIONS", "FUNCTION", "CALL", "DO", "VAR", "PARAM",
            "IDENTIFIER", "OR", "ALAP", "WHILE", "UNTIL", "CHOICE", "CH_OR",
            "IF", "ELSE", "TRY", "TRUE", "PLUS", "STAR", "SHARP", "ANY",
            "OTHER", "DOT", "NODE_TYPE", "BOOL_TYPE", "STRING_TYPE",
            "INT_TYPE", "REAL_TYPE", "COMMA", "OUT", "DONT_CARE", "FALSE",
            "QUOTE", "BSLASH", "MINUS", "NUMBER", "AND", "NOT", "ML_COMMENT",
            "SL_COMMENT", "WS", "'{'", "'}'", "'('", "')'", "';'"};
    public static final int FUNCTION = 7;
    public static final int STAR = 24;
    public static final int FUNCTIONS = 6;
    public static final int WHILE = 15;
    public static final int BOOL_TYPE = 30;
    public static final int NODE_TYPE = 29;
    public static final int DO = 9;
    public static final int PARAM = 11;
    public static final int NOT = 43;
    public static final int ALAP = 14;
    public static final int AND = 42;
    public static final int EOF = -1;
    public static final int IF = 19;
    public static final int ML_COMMENT = 44;
    public static final int QUOTE = 38;
    public static final int T__51 = 51;
    public static final int COMMA = 34;
    public static final int IDENTIFIER = 12;
    public static final int CH_OR = 18;
    public static final int PLUS = 23;
    public static final int VAR = 10;
    public static final int DOT = 28;
    public static final int T__50 = 50;
    public static final int CHOICE = 17;
    public static final int T__47 = 47;
    public static final int SHARP = 25;
    public static final int OTHER = 27;
    public static final int T__48 = 48;
    public static final int T__49 = 49;
    public static final int ELSE = 20;
    public static final int NUMBER = 41;
    public static final int MINUS = 40;
    public static final int INT_TYPE = 32;
    public static final int TRUE = 22;
    public static final int TRY = 21;
    public static final int REAL_TYPE = 33;
    public static final int DONT_CARE = 36;
    public static final int WS = 46;
    public static final int ANY = 26;
    public static final int OUT = 35;
    public static final int UNTIL = 16;
    public static final int STRING_TYPE = 31;
    public static final int BLOCK = 5;
    public static final int OR = 13;
    public static final int SL_COMMENT = 45;
    public static final int PROGRAM = 4;
    public static final int CALL = 8;
    public static final int FALSE = 37;
    public static final int BSLASH = 39;

    // delegates
    // delegators

    public GCLChecker(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }

    public GCLChecker(TreeNodeStream input, RecognizerSharedState state) {
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
        return GCLChecker.tokenNames;
    }

    public String getGrammarFileName() {
        return "GCLChecker.g";
    }

    private ControlAutomaton aut;

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
    // GCLChecker.g:55:1: program : ^( PROGRAM functions block ) ;
    public final GCLChecker.program_return program()
        throws RecognitionException {
        GCLChecker.program_return retval = new GCLChecker.program_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree PROGRAM1 = null;
        GCLChecker.functions_return functions2 = null;

        GCLChecker.block_return block3 = null;

        CommonTree PROGRAM1_tree = null;

        try {
            // GCLChecker.g:56:3: ( ^( PROGRAM functions block ) )
            // GCLChecker.g:56:6: ^( PROGRAM functions block )
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
    // GCLChecker.g:59:1: functions : ^(f= FUNCTIONS ( function )* ) ;
    public final GCLChecker.functions_return functions()
        throws RecognitionException {
        GCLChecker.functions_return retval = new GCLChecker.functions_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree f = null;
        GCLChecker.function_return function4 = null;

        CommonTree f_tree = null;

        try {
            // GCLChecker.g:60:3: ( ^(f= FUNCTIONS ( function )* ) )
            // GCLChecker.g:60:5: ^(f= FUNCTIONS ( function )* )
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
                        // GCLChecker.g:60:48: ( function )*
                        loop1: do {
                            int alt1 = 2;
                            int LA1_0 = this.input.LA(1);

                            if ((LA1_0 == FUNCTION)) {
                                alt1 = 1;
                            }

                            switch (alt1) {
                            case 1:
                                // GCLChecker.g:60:48: function
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
    // GCLChecker.g:62:1: function : ^( FUNCTION IDENTIFIER block ) ;
    public final GCLChecker.function_return function()
        throws RecognitionException {
        GCLChecker.function_return retval = new GCLChecker.function_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree FUNCTION5 = null;
        CommonTree IDENTIFIER6 = null;
        GCLChecker.block_return block7 = null;

        CommonTree FUNCTION5_tree = null;
        CommonTree IDENTIFIER6_tree = null;

        try {
            // GCLChecker.g:63:3: ( ^( FUNCTION IDENTIFIER block ) )
            // GCLChecker.g:63:5: ^( FUNCTION IDENTIFIER block )
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
                    IDENTIFIER6 =
                        (CommonTree) match(this.input, IDENTIFIER,
                            FOLLOW_IDENTIFIER_in_function98);

                    if (_first_1 == null) {
                        _first_1 = IDENTIFIER6;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_block_in_function100);
                    block7 = block();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = block7.tree;
                    }

                    debug("proc: "
                        + (IDENTIFIER6 != null ? IDENTIFIER6.getText() : null));
                    if (this.namespace.hasRule((IDENTIFIER6 != null
                            ? IDENTIFIER6.getText() : null))) {
                        this.errors.add("There already exists a rule with the name: "
                            + (IDENTIFIER6 != null ? IDENTIFIER6.getText()
                                    : null));
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
    // GCLChecker.g:70:1: block : ^( BLOCK ( statement )* ) ;
    public final GCLChecker.block_return block() throws RecognitionException {
        GCLChecker.block_return retval = new GCLChecker.block_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree BLOCK8 = null;
        GCLChecker.statement_return statement9 = null;

        CommonTree BLOCK8_tree = null;

        try {
            // GCLChecker.g:71:3: ( ^( BLOCK ( statement )* ) )
            // GCLChecker.g:71:5: ^( BLOCK ( statement )* )
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
                        // GCLChecker.g:71:33: ( statement )*
                        loop2: do {
                            int alt2 = 2;
                            int LA2_0 = this.input.LA(1);

                            if (((LA2_0 >= CALL && LA2_0 <= VAR)
                                || (LA2_0 >= OR && LA2_0 <= CHOICE)
                                || LA2_0 == IF || LA2_0 == TRY || (LA2_0 >= PLUS && LA2_0 <= OTHER))) {
                                alt2 = 1;
                            }

                            switch (alt2) {
                            case 1:
                                // GCLChecker.g:71:34: statement
                            {
                                _last = (CommonTree) this.input.LT(1);
                                pushFollow(FOLLOW_statement_in_block122);
                                statement9 = statement();

                                this.state._fsp--;

                                if (_first_1 == null) {
                                    _first_1 = statement9.tree;
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

    public static class statement_return extends TreeRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "statement"
    // GCLChecker.g:74:1: statement : ( ^( ALAP block ) | ^( WHILE condition block ) | ^( UNTIL condition block ) | ^( DO block condition ) | ^( TRY block ( block )? ) | ^( IF condition block ( block )? ) | ^( CHOICE ( block )+ ) | expression | var_declaration );
    public final GCLChecker.statement_return statement()
        throws RecognitionException {
        GCLChecker.statement_return retval = new GCLChecker.statement_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree ALAP10 = null;
        CommonTree WHILE12 = null;
        CommonTree UNTIL15 = null;
        CommonTree DO18 = null;
        CommonTree TRY21 = null;
        CommonTree IF24 = null;
        CommonTree CHOICE28 = null;
        GCLChecker.block_return block11 = null;

        GCLChecker.condition_return condition13 = null;

        GCLChecker.block_return block14 = null;

        GCLChecker.condition_return condition16 = null;

        GCLChecker.block_return block17 = null;

        GCLChecker.block_return block19 = null;

        GCLChecker.condition_return condition20 = null;

        GCLChecker.block_return block22 = null;

        GCLChecker.block_return block23 = null;

        GCLChecker.condition_return condition25 = null;

        GCLChecker.block_return block26 = null;

        GCLChecker.block_return block27 = null;

        GCLChecker.block_return block29 = null;

        GCLChecker.expression_return expression30 = null;

        GCLChecker.var_declaration_return var_declaration31 = null;

        CommonTree ALAP10_tree = null;
        CommonTree WHILE12_tree = null;
        CommonTree UNTIL15_tree = null;
        CommonTree DO18_tree = null;
        CommonTree TRY21_tree = null;
        CommonTree IF24_tree = null;
        CommonTree CHOICE28_tree = null;

        try {
            // GCLChecker.g:75:3: ( ^( ALAP block ) | ^( WHILE condition block ) | ^( UNTIL condition block ) | ^( DO block condition ) | ^( TRY block ( block )? ) | ^( IF condition block ( block )? ) | ^( CHOICE ( block )+ ) | expression | var_declaration )
            int alt6 = 9;
            switch (this.input.LA(1)) {
            case ALAP: {
                alt6 = 1;
            }
                break;
            case WHILE: {
                alt6 = 2;
            }
                break;
            case UNTIL: {
                alt6 = 3;
            }
                break;
            case DO: {
                alt6 = 4;
            }
                break;
            case TRY: {
                alt6 = 5;
            }
                break;
            case IF: {
                alt6 = 6;
            }
                break;
            case CHOICE: {
                alt6 = 7;
            }
                break;
            case CALL:
            case OR:
            case PLUS:
            case STAR:
            case SHARP:
            case ANY:
            case OTHER: {
                alt6 = 8;
            }
                break;
            case VAR: {
                alt6 = 9;
            }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, this.input);

                throw nvae;
            }

            switch (alt6) {
            case 1:
                // GCLChecker.g:75:5: ^( ALAP block )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    ALAP10 =
                        (CommonTree) match(this.input, ALAP,
                            FOLLOW_ALAP_in_statement141);

                    if (_first_0 == null) {
                        _first_0 = ALAP10;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_block_in_statement143);
                    block11 = block();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = block11.tree;
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
                // GCLChecker.g:76:5: ^( WHILE condition block )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    WHILE12 =
                        (CommonTree) match(this.input, WHILE,
                            FOLLOW_WHILE_in_statement151);

                    if (_first_0 == null) {
                        _first_0 = WHILE12;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement153);
                    condition13 = condition();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = condition13.tree;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_block_in_statement155);
                    block14 = block();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = block14.tree;
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
            case 3:
                // GCLChecker.g:77:5: ^( UNTIL condition block )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    UNTIL15 =
                        (CommonTree) match(this.input, UNTIL,
                            FOLLOW_UNTIL_in_statement163);

                    if (_first_0 == null) {
                        _first_0 = UNTIL15;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement165);
                    condition16 = condition();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = condition16.tree;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_block_in_statement167);
                    block17 = block();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = block17.tree;
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
                // GCLChecker.g:78:5: ^( DO block condition )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    DO18 =
                        (CommonTree) match(this.input, DO,
                            FOLLOW_DO_in_statement175);

                    if (_first_0 == null) {
                        _first_0 = DO18;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_block_in_statement177);
                    block19 = block();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = block19.tree;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement179);
                    condition20 = condition();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = condition20.tree;
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
                // GCLChecker.g:79:5: ^( TRY block ( block )? )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    TRY21 =
                        (CommonTree) match(this.input, TRY,
                            FOLLOW_TRY_in_statement187);

                    if (_first_0 == null) {
                        _first_0 = TRY21;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_block_in_statement189);
                    block22 = block();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = block22.tree;
                    }
                    // GCLChecker.g:79:17: ( block )?
                    int alt3 = 2;
                    int LA3_0 = this.input.LA(1);

                    if ((LA3_0 == BLOCK)) {
                        alt3 = 1;
                    }
                    switch (alt3) {
                    case 1:
                        // GCLChecker.g:79:18: block
                    {
                        _last = (CommonTree) this.input.LT(1);
                        pushFollow(FOLLOW_block_in_statement192);
                        block23 = block();

                        this.state._fsp--;

                        if (_first_1 == null) {
                            _first_1 = block23.tree;
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
            case 6:
                // GCLChecker.g:80:5: ^( IF condition block ( block )? )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    IF24 =
                        (CommonTree) match(this.input, IF,
                            FOLLOW_IF_in_statement202);

                    if (_first_0 == null) {
                        _first_0 = IF24;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_condition_in_statement204);
                    condition25 = condition();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = condition25.tree;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_block_in_statement206);
                    block26 = block();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = block26.tree;
                    }
                    // GCLChecker.g:80:26: ( block )?
                    int alt4 = 2;
                    int LA4_0 = this.input.LA(1);

                    if ((LA4_0 == BLOCK)) {
                        alt4 = 1;
                    }
                    switch (alt4) {
                    case 1:
                        // GCLChecker.g:80:27: block
                    {
                        _last = (CommonTree) this.input.LT(1);
                        pushFollow(FOLLOW_block_in_statement209);
                        block27 = block();

                        this.state._fsp--;

                        if (_first_1 == null) {
                            _first_1 = block27.tree;
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
                // GCLChecker.g:81:5: ^( CHOICE ( block )+ )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    CHOICE28 =
                        (CommonTree) match(this.input, CHOICE,
                            FOLLOW_CHOICE_in_statement219);

                    if (_first_0 == null) {
                        _first_0 = CHOICE28;
                    }
                    match(this.input, Token.DOWN, null);
                    // GCLChecker.g:81:14: ( block )+
                    int cnt5 = 0;
                    loop5: do {
                        int alt5 = 2;
                        int LA5_0 = this.input.LA(1);

                        if ((LA5_0 == BLOCK)) {
                            alt5 = 1;
                        }

                        switch (alt5) {
                        case 1:
                            // GCLChecker.g:81:14: block
                        {
                            _last = (CommonTree) this.input.LT(1);
                            pushFollow(FOLLOW_block_in_statement221);
                            block29 = block();

                            this.state._fsp--;

                            if (_first_1 == null) {
                                _first_1 = block29.tree;
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
            case 8:
                // GCLChecker.g:82:5: expression
            {
                _last = (CommonTree) this.input.LT(1);
                pushFollow(FOLLOW_expression_in_statement229);
                expression30 = expression();

                this.state._fsp--;

                if (_first_0 == null) {
                    _first_0 = expression30.tree;
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
                // GCLChecker.g:83:5: var_declaration
            {
                _last = (CommonTree) this.input.LT(1);
                pushFollow(FOLLOW_var_declaration_in_statement235);
                var_declaration31 = var_declaration();

                this.state._fsp--;

                if (_first_0 == null) {
                    _first_0 = var_declaration31.tree;
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

    // $ANTLR end "statement"

    public static class expression_return extends TreeRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "expression"
    // GCLChecker.g:86:1: expression : ( ^( OR expression expression ) | ^( PLUS e1= expression ) -> ^( PLUS $e1 $e1) | ^( STAR expression ) | ^( SHARP expression ) | rule | ANY | OTHER );
    public final GCLChecker.expression_return expression()
        throws RecognitionException {
        GCLChecker.expression_return retval =
            new GCLChecker.expression_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree OR32 = null;
        CommonTree PLUS35 = null;
        CommonTree STAR36 = null;
        CommonTree SHARP38 = null;
        CommonTree ANY41 = null;
        CommonTree OTHER42 = null;
        GCLChecker.expression_return e1 = null;

        GCLChecker.expression_return expression33 = null;

        GCLChecker.expression_return expression34 = null;

        GCLChecker.expression_return expression37 = null;

        GCLChecker.expression_return expression39 = null;

        GCLChecker.rule_return rule40 = null;

        CommonTree OR32_tree = null;
        CommonTree PLUS35_tree = null;
        CommonTree STAR36_tree = null;
        CommonTree SHARP38_tree = null;
        CommonTree ANY41_tree = null;
        CommonTree OTHER42_tree = null;
        RewriteRuleNodeStream stream_PLUS =
            new RewriteRuleNodeStream(this.adaptor, "token PLUS");
        RewriteRuleSubtreeStream stream_expression =
            new RewriteRuleSubtreeStream(this.adaptor, "rule expression");
        try {
            // GCLChecker.g:87:2: ( ^( OR expression expression ) | ^( PLUS e1= expression ) -> ^( PLUS $e1 $e1) | ^( STAR expression ) | ^( SHARP expression ) | rule | ANY | OTHER )
            int alt7 = 7;
            switch (this.input.LA(1)) {
            case OR: {
                alt7 = 1;
            }
                break;
            case PLUS: {
                alt7 = 2;
            }
                break;
            case STAR: {
                alt7 = 3;
            }
                break;
            case SHARP: {
                alt7 = 4;
            }
                break;
            case CALL: {
                alt7 = 5;
            }
                break;
            case ANY: {
                alt7 = 6;
            }
                break;
            case OTHER: {
                alt7 = 7;
            }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, this.input);

                throw nvae;
            }

            switch (alt7) {
            case 1:
                // GCLChecker.g:87:4: ^( OR expression expression )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    OR32 =
                        (CommonTree) match(this.input, OR,
                            FOLLOW_OR_in_expression249);

                    if (_first_0 == null) {
                        _first_0 = OR32;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression251);
                    expression33 = expression();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = expression33.tree;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression253);
                    expression34 = expression();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = expression34.tree;
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
                // GCLChecker.g:88:4: ^( PLUS e1= expression )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    PLUS35 =
                        (CommonTree) match(this.input, PLUS,
                            FOLLOW_PLUS_in_expression260);
                    stream_PLUS.add(PLUS35);

                    if (_first_0 == null) {
                        _first_0 = PLUS35;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression264);
                    e1 = expression();

                    this.state._fsp--;

                    stream_expression.add(e1.getTree());

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                // AST REWRITE
                // elements: PLUS, e1, e1
                // token labels: 
                // rule labels: retval, e1
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                retval.tree = root_0;
                RewriteRuleSubtreeStream stream_retval =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule retval",
                        retval != null ? retval.tree : null);
                RewriteRuleSubtreeStream stream_e1 =
                    new RewriteRuleSubtreeStream(this.adaptor, "rule e1",
                        e1 != null ? e1.tree : null);

                root_0 = (CommonTree) this.adaptor.nil();
                // 88:26: -> ^( PLUS $e1 $e1)
                {
                    // GCLChecker.g:88:29: ^( PLUS $e1 $e1)
                    {
                        CommonTree root_1 = (CommonTree) this.adaptor.nil();
                        root_1 =
                            (CommonTree) this.adaptor.becomeRoot(
                                stream_PLUS.nextNode(), root_1);

                        this.adaptor.addChild(root_1, stream_e1.nextTree());
                        this.adaptor.addChild(root_1, stream_e1.nextTree());

                        this.adaptor.addChild(root_0, root_1);
                    }

                }

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.input.replaceChildren(
                    this.adaptor.getParent(retval.start),
                    this.adaptor.getChildIndex(retval.start),
                    this.adaptor.getChildIndex(_last), retval.tree);
            }
                break;
            case 3:
                // GCLChecker.g:89:4: ^( STAR expression )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    STAR36 =
                        (CommonTree) match(this.input, STAR,
                            FOLLOW_STAR_in_expression283);

                    if (_first_0 == null) {
                        _first_0 = STAR36;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression285);
                    expression37 = expression();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = expression37.tree;
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
                // GCLChecker.g:90:4: ^( SHARP expression )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    SHARP38 =
                        (CommonTree) match(this.input, SHARP,
                            FOLLOW_SHARP_in_expression292);

                    if (_first_0 == null) {
                        _first_0 = SHARP38;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_expression_in_expression294);
                    expression39 = expression();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = expression39.tree;
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
                // GCLChecker.g:91:4: rule
            {
                _last = (CommonTree) this.input.LT(1);
                pushFollow(FOLLOW_rule_in_expression300);
                rule40 = rule();

                this.state._fsp--;

                if (_first_0 == null) {
                    _first_0 = rule40.tree;
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
                // GCLChecker.g:92:4: ANY
            {
                _last = (CommonTree) this.input.LT(1);
                ANY41 =
                    (CommonTree) match(this.input, ANY,
                        FOLLOW_ANY_in_expression305);

                if (_first_0 == null) {
                    _first_0 = ANY41;
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
                // GCLChecker.g:93:4: OTHER
            {
                _last = (CommonTree) this.input.LT(1);
                OTHER42 =
                    (CommonTree) match(this.input, OTHER,
                        FOLLOW_OTHER_in_expression310);

                if (_first_0 == null) {
                    _first_0 = OTHER42;
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

    // $ANTLR end "expression"

    public static class condition_return extends TreeRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "condition"
    // GCLChecker.g:96:1: condition : ( ^( OR condition condition ) | rule | TRUE );
    public final GCLChecker.condition_return condition()
        throws RecognitionException {
        GCLChecker.condition_return retval = new GCLChecker.condition_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree OR43 = null;
        CommonTree TRUE47 = null;
        GCLChecker.condition_return condition44 = null;

        GCLChecker.condition_return condition45 = null;

        GCLChecker.rule_return rule46 = null;

        CommonTree OR43_tree = null;
        CommonTree TRUE47_tree = null;

        try {
            // GCLChecker.g:97:3: ( ^( OR condition condition ) | rule | TRUE )
            int alt8 = 3;
            switch (this.input.LA(1)) {
            case OR: {
                alt8 = 1;
            }
                break;
            case CALL: {
                alt8 = 2;
            }
                break;
            case TRUE: {
                alt8 = 3;
            }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, this.input);

                throw nvae;
            }

            switch (alt8) {
            case 1:
                // GCLChecker.g:97:5: ^( OR condition condition )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    OR43 =
                        (CommonTree) match(this.input, OR,
                            FOLLOW_OR_in_condition324);

                    if (_first_0 == null) {
                        _first_0 = OR43;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_condition_in_condition326);
                    condition44 = condition();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = condition44.tree;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    pushFollow(FOLLOW_condition_in_condition328);
                    condition45 = condition();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = condition45.tree;
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
                // GCLChecker.g:98:5: rule
            {
                _last = (CommonTree) this.input.LT(1);
                pushFollow(FOLLOW_rule_in_condition335);
                rule46 = rule();

                this.state._fsp--;

                if (_first_0 == null) {
                    _first_0 = rule46.tree;
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
                // GCLChecker.g:99:5: TRUE
            {
                _last = (CommonTree) this.input.LT(1);
                TRUE47 =
                    (CommonTree) match(this.input, TRUE,
                        FOLLOW_TRUE_in_condition341);

                if (_first_0 == null) {
                    _first_0 = TRUE47;
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

    // $ANTLR end "condition"

    public static class rule_return extends TreeRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "rule"
    // GCLChecker.g:102:1: rule : ^( CALL r= IDENTIFIER ( param )* ) ;
    public final GCLChecker.rule_return rule() throws RecognitionException {
        GCLChecker.rule_return retval = new GCLChecker.rule_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree r = null;
        CommonTree CALL48 = null;
        GCLChecker.param_return param49 = null;

        CommonTree r_tree = null;
        CommonTree CALL48_tree = null;

        try {
            // GCLChecker.g:103:3: ( ^( CALL r= IDENTIFIER ( param )* ) )
            // GCLChecker.g:103:5: ^( CALL r= IDENTIFIER ( param )* )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    CALL48 =
                        (CommonTree) match(this.input, CALL,
                            FOLLOW_CALL_in_rule355);

                    if (_first_0 == null) {
                        _first_0 = CALL48;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    r =
                        (CommonTree) match(this.input, IDENTIFIER,
                            FOLLOW_IDENTIFIER_in_rule359);

                    if (_first_1 == null) {
                        _first_1 = r;
                    }
                    this.currentRule = this.namespace.getRule(r.getText());
                    // GCLChecker.g:103:75: ( param )*
                    loop9: do {
                        int alt9 = 2;
                        int LA9_0 = this.input.LA(1);

                        if ((LA9_0 == PARAM)) {
                            alt9 = 1;
                        }

                        switch (alt9) {
                        case 1:
                            // GCLChecker.g:103:75: param
                        {
                            _last = (CommonTree) this.input.LT(1);
                            pushFollow(FOLLOW_param_in_rule363);
                            param49 = param();

                            this.state._fsp--;

                            if (_first_1 == null) {
                                _first_1 = param49.tree;
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
                            break loop9;
                        }
                    } while (true);

                    //debug("currentRule: "+currentRule.getName().toString());
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

    public static class var_declaration_return extends TreeRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "var_declaration"
    // GCLChecker.g:119:1: var_declaration : ^( VAR t= ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE ) IDENTIFIER ) ;
    public final GCLChecker.var_declaration_return var_declaration()
        throws RecognitionException {
        GCLChecker.var_declaration_return retval =
            new GCLChecker.var_declaration_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree t = null;
        CommonTree VAR50 = null;
        CommonTree IDENTIFIER51 = null;

        CommonTree t_tree = null;
        CommonTree VAR50_tree = null;
        CommonTree IDENTIFIER51_tree = null;

        try {
            // GCLChecker.g:120:2: ( ^( VAR t= ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE ) IDENTIFIER ) )
            // GCLChecker.g:120:4: ^( VAR t= ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE ) IDENTIFIER )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    VAR50 =
                        (CommonTree) match(this.input, VAR,
                            FOLLOW_VAR_in_var_declaration381);

                    if (_first_0 == null) {
                        _first_0 = VAR50;
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
                    IDENTIFIER51 =
                        (CommonTree) match(this.input, IDENTIFIER,
                            FOLLOW_IDENTIFIER_in_var_declaration405);

                    if (_first_1 == null) {
                        _first_1 = IDENTIFIER51;
                    }

                    if (!this.namespace.hasVariable((IDENTIFIER51 != null
                            ? IDENTIFIER51.getText() : null))) {
                        this.namespace.addVariable((IDENTIFIER51 != null
                                ? IDENTIFIER51.getText() : null));
                        this.st.declareSymbol((IDENTIFIER51 != null
                                ? IDENTIFIER51.getText() : null), t.getText());
                    } else {
                        this.errors.add("Double declaration of variable '"
                            + (IDENTIFIER51 != null ? IDENTIFIER51.getText()
                                    : null)
                            + "' on line "
                            + (IDENTIFIER51 != null ? IDENTIFIER51.getLine()
                                    : 0));
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

    // $ANTLR end "var_declaration"

    public static class param_return extends TreeRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "param"
    // GCLChecker.g:130:1: param : ( ^( PARAM IDENTIFIER ) | ^( PARAM OUT IDENTIFIER ) | ^( PARAM DONT_CARE ) | ^( PARAM BOOL_TYPE bool= ( TRUE | FALSE ) ) | ^( PARAM STRING_TYPE str= IDENTIFIER ) | ^( PARAM INT_TYPE in= IDENTIFIER ) | ^( PARAM REAL_TYPE r= IDENTIFIER ) );
    public final GCLChecker.param_return param() throws RecognitionException {
        GCLChecker.param_return retval = new GCLChecker.param_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree bool = null;
        CommonTree str = null;
        CommonTree in = null;
        CommonTree r = null;
        CommonTree PARAM52 = null;
        CommonTree IDENTIFIER53 = null;
        CommonTree PARAM54 = null;
        CommonTree OUT55 = null;
        CommonTree IDENTIFIER56 = null;
        CommonTree PARAM57 = null;
        CommonTree DONT_CARE58 = null;
        CommonTree PARAM59 = null;
        CommonTree BOOL_TYPE60 = null;
        CommonTree PARAM61 = null;
        CommonTree STRING_TYPE62 = null;
        CommonTree PARAM63 = null;
        CommonTree INT_TYPE64 = null;
        CommonTree PARAM65 = null;
        CommonTree REAL_TYPE66 = null;

        CommonTree bool_tree = null;
        CommonTree str_tree = null;
        CommonTree in_tree = null;
        CommonTree r_tree = null;
        CommonTree PARAM52_tree = null;
        CommonTree IDENTIFIER53_tree = null;
        CommonTree PARAM54_tree = null;
        CommonTree OUT55_tree = null;
        CommonTree IDENTIFIER56_tree = null;
        CommonTree PARAM57_tree = null;
        CommonTree DONT_CARE58_tree = null;
        CommonTree PARAM59_tree = null;
        CommonTree BOOL_TYPE60_tree = null;
        CommonTree PARAM61_tree = null;
        CommonTree STRING_TYPE62_tree = null;
        CommonTree PARAM63_tree = null;
        CommonTree INT_TYPE64_tree = null;
        CommonTree PARAM65_tree = null;
        CommonTree REAL_TYPE66_tree = null;

        try {
            // GCLChecker.g:131:2: ( ^( PARAM IDENTIFIER ) | ^( PARAM OUT IDENTIFIER ) | ^( PARAM DONT_CARE ) | ^( PARAM BOOL_TYPE bool= ( TRUE | FALSE ) ) | ^( PARAM STRING_TYPE str= IDENTIFIER ) | ^( PARAM INT_TYPE in= IDENTIFIER ) | ^( PARAM REAL_TYPE r= IDENTIFIER ) )
            int alt10 = 7;
            alt10 = this.dfa10.predict(this.input);
            switch (alt10) {
            case 1:
                // GCLChecker.g:131:4: ^( PARAM IDENTIFIER )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    PARAM52 =
                        (CommonTree) match(this.input, PARAM,
                            FOLLOW_PARAM_in_param421);

                    if (_first_0 == null) {
                        _first_0 = PARAM52;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    IDENTIFIER53 =
                        (CommonTree) match(this.input, IDENTIFIER,
                            FOLLOW_IDENTIFIER_in_param423);

                    if (_first_1 == null) {
                        _first_1 = IDENTIFIER53;
                    }

                    this.numParameters++;
                    if (this.st.isDeclared((IDENTIFIER53 != null
                            ? IDENTIFIER53.getText() : null))) {
                        if (!this.st.isInitialized((IDENTIFIER53 != null
                                ? IDENTIFIER53.getText() : null))) {
                            this.errors.add("The variable "
                                + (IDENTIFIER53 != null
                                        ? IDENTIFIER53.getText() : null)
                                + " might not have been initialized on line "
                                + (IDENTIFIER53 != null
                                        ? IDENTIFIER53.getLine() : 0));
                        }
                    } else {
                        this.errors.add("No such variable: "
                            + (IDENTIFIER53 != null ? IDENTIFIER53.getText()
                                    : null));
                    }
                    if (this.currentRule != null
                        && this.currentRule.getNumberOfParameters() < this.numParameters) {
                        this.errors.add("Rule "
                            + this.currentRule.getName().toString()
                            + " does not have this many parameters on line "
                            + (IDENTIFIER53 != null ? IDENTIFIER53.getLine()
                                    : 0));
                    } else {
                        if (this.currentRule != null
                            && !this.currentRule.isInputParameter(this.numParameters)) {
                            this.errors.add("Parameter number "
                                + (this.numParameters)
                                + " cannot be an input parameter in rule "
                                + this.currentRule.getName().toString()
                                + " on line "
                                + (IDENTIFIER53 != null
                                        ? IDENTIFIER53.getLine() : 0));
                        }
                        if (this.currentRule != null
                            && !this.currentRule.getAttributeParameterType(
                                this.numParameters).equals(
                                this.st.getType((IDENTIFIER53 != null
                                        ? IDENTIFIER53.getText() : null)))) {
                            this.errors.add("Type mismatch between parameter "
                                + this.numParameters
                                + " of "
                                + this.currentRule.getName().toString()
                                + " and variable "
                                + (IDENTIFIER53 != null
                                        ? IDENTIFIER53.getText() : null)
                                + " ("
                                + this.currentRule.getAttributeParameterType(this.numParameters)
                                + " is not "
                                + this.st.getType((IDENTIFIER53 != null
                                        ? IDENTIFIER53.getText() : null)) + ")");
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
                // GCLChecker.g:151:4: ^( PARAM OUT IDENTIFIER )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    PARAM54 =
                        (CommonTree) match(this.input, PARAM,
                            FOLLOW_PARAM_in_param433);

                    if (_first_0 == null) {
                        _first_0 = PARAM54;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    OUT55 =
                        (CommonTree) match(this.input, OUT,
                            FOLLOW_OUT_in_param435);

                    if (_first_1 == null) {
                        _first_1 = OUT55;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    IDENTIFIER56 =
                        (CommonTree) match(this.input, IDENTIFIER,
                            FOLLOW_IDENTIFIER_in_param437);

                    if (_first_1 == null) {
                        _first_1 = IDENTIFIER56;
                    }

                    this.numParameters++;

                    if (this.st.isDeclared((IDENTIFIER56 != null
                            ? IDENTIFIER56.getText() : null))) {
                        if (!this.st.canInitialize((IDENTIFIER56 != null
                                ? IDENTIFIER56.getText() : null))
                            || this.syntaxInit.contains((IDENTIFIER56 != null
                                    ? IDENTIFIER56.getText() : null))) {
                            this.errors.add("Variable already initialized: "
                                + (IDENTIFIER56 != null
                                        ? IDENTIFIER56.getText() : null)
                                + " on line "
                                + (IDENTIFIER56 != null
                                        ? IDENTIFIER56.getLine() : 0));
                        } else {
                            this.st.initializeSymbol((IDENTIFIER56 != null
                                    ? IDENTIFIER56.getText() : null));
                            this.syntaxInit.add((IDENTIFIER56 != null
                                    ? IDENTIFIER56.getText() : null));
                        }
                    } else {
                        this.errors.add("No such variable: "
                            + (IDENTIFIER56 != null ? IDENTIFIER56.getText()
                                    : null)
                            + " on line "
                            + (IDENTIFIER56 != null ? IDENTIFIER56.getLine()
                                    : 0));
                    }
                    if (this.currentRule != null
                        && this.currentRule.getNumberOfParameters() < this.numParameters) {
                        this.errors.add("Rule "
                            + this.currentRule.getName().toString()
                            + " does not have this many parameters on line "
                            + (IDENTIFIER56 != null ? IDENTIFIER56.getLine()
                                    : 0));
                    } else {
                        if (this.currentRule != null
                            && !this.currentRule.isOutputParameter(this.numParameters)) {
                            this.errors.add("Parameter number "
                                + (this.numParameters)
                                + " cannot be an output parameter in rule "
                                + this.currentRule.getName().toString()
                                + " on line "
                                + (IDENTIFIER56 != null
                                        ? IDENTIFIER56.getLine() : 0));
                        }
                        if (this.currentOutputParameters.contains((IDENTIFIER56 != null
                                ? IDENTIFIER56.getText() : null))) {
                            this.errors.add("You can not use the same parameter as output more than once per call: "
                                + (IDENTIFIER56 != null
                                        ? IDENTIFIER56.getText() : null)
                                + " on line "
                                + (IDENTIFIER56 != null
                                        ? IDENTIFIER56.getLine() : 0));
                        }
                        if (this.currentRule != null
                            && !this.currentRule.getAttributeParameterType(
                                this.numParameters).equals(
                                this.st.getType((IDENTIFIER56 != null
                                        ? IDENTIFIER56.getText() : null)))) {
                            this.errors.add("Type mismatch between parameter "
                                + this.numParameters
                                + " of "
                                + this.currentRule.getName().toString()
                                + " and variable "
                                + (IDENTIFIER56 != null
                                        ? IDENTIFIER56.getText() : null)
                                + " ("
                                + this.currentRule.getAttributeParameterType(this.numParameters)
                                + " is not "
                                + this.st.getType((IDENTIFIER56 != null
                                        ? IDENTIFIER56.getText() : null)) + ")");
                        }
                        if (this.currentRule != null
                            && this.currentRule.isRequiredInput(this.numParameters)) {
                            this.errors.add("Parameter " + this.numParameters
                                + " of rule "
                                + this.currentRule.getName().toString()
                                + " must be an input parameter.");
                        }
                    }
                    this.currentOutputParameters.add((IDENTIFIER56 != null
                            ? IDENTIFIER56.getText() : null));

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
                // GCLChecker.g:182:4: ^( PARAM DONT_CARE )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    PARAM57 =
                        (CommonTree) match(this.input, PARAM,
                            FOLLOW_PARAM_in_param447);

                    if (_first_0 == null) {
                        _first_0 = PARAM57;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    DONT_CARE58 =
                        (CommonTree) match(this.input, DONT_CARE,
                            FOLLOW_DONT_CARE_in_param449);

                    if (_first_1 == null) {
                        _first_1 = DONT_CARE58;
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
                // GCLChecker.g:188:4: ^( PARAM BOOL_TYPE bool= ( TRUE | FALSE ) )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    PARAM59 =
                        (CommonTree) match(this.input, PARAM,
                            FOLLOW_PARAM_in_param458);

                    if (_first_0 == null) {
                        _first_0 = PARAM59;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    BOOL_TYPE60 =
                        (CommonTree) match(this.input, BOOL_TYPE,
                            FOLLOW_BOOL_TYPE_in_param460);

                    if (_first_1 == null) {
                        _first_1 = BOOL_TYPE60;
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
                // GCLChecker.g:194:4: ^( PARAM STRING_TYPE str= IDENTIFIER )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    PARAM61 =
                        (CommonTree) match(this.input, PARAM,
                            FOLLOW_PARAM_in_param477);

                    if (_first_0 == null) {
                        _first_0 = PARAM61;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    STRING_TYPE62 =
                        (CommonTree) match(this.input, STRING_TYPE,
                            FOLLOW_STRING_TYPE_in_param479);

                    if (_first_1 == null) {
                        _first_1 = STRING_TYPE62;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    str =
                        (CommonTree) match(this.input, IDENTIFIER,
                            FOLLOW_IDENTIFIER_in_param483);

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
                // GCLChecker.g:200:4: ^( PARAM INT_TYPE in= IDENTIFIER )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    PARAM63 =
                        (CommonTree) match(this.input, PARAM,
                            FOLLOW_PARAM_in_param492);

                    if (_first_0 == null) {
                        _first_0 = PARAM63;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    INT_TYPE64 =
                        (CommonTree) match(this.input, INT_TYPE,
                            FOLLOW_INT_TYPE_in_param494);

                    if (_first_1 == null) {
                        _first_1 = INT_TYPE64;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    in =
                        (CommonTree) match(this.input, IDENTIFIER,
                            FOLLOW_IDENTIFIER_in_param498);

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
                // GCLChecker.g:206:4: ^( PARAM REAL_TYPE r= IDENTIFIER )
            {
                _last = (CommonTree) this.input.LT(1);
                {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree) this.input.LT(1);
                    PARAM65 =
                        (CommonTree) match(this.input, PARAM,
                            FOLLOW_PARAM_in_param507);

                    if (_first_0 == null) {
                        _first_0 = PARAM65;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CommonTree) this.input.LT(1);
                    REAL_TYPE66 =
                        (CommonTree) match(this.input, REAL_TYPE,
                            FOLLOW_REAL_TYPE_in_param509);

                    if (_first_1 == null) {
                        _first_1 = REAL_TYPE66;
                    }
                    _last = (CommonTree) this.input.LT(1);
                    r =
                        (CommonTree) match(this.input, IDENTIFIER,
                            FOLLOW_IDENTIFIER_in_param513);

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

    // $ANTLR end "param"

    // Delegated rules

    protected DFA10 dfa10 = new DFA10(this);
    static final String DFA10_eotS = "\12\uffff";
    static final String DFA10_eofS = "\12\uffff";
    static final String DFA10_minS = "\1\13\1\2\1\14\7\uffff";
    static final String DFA10_maxS = "\1\13\1\2\1\44\7\uffff";
    static final String DFA10_acceptS = "\3\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7";
    static final String DFA10_specialS = "\12\uffff}>";
    static final String[] DFA10_transitionS =
        {"\1\1", "\1\2", "\1\3\21\uffff\1\6\1\7\1\10\1\11\1\uffff\1\4\1\5", "",
            "", "", "", "", "", ""};

    static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
    static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
    static final char[] DFA10_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
    static final char[] DFA10_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
    static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
    static final short[] DFA10_special =
        DFA.unpackEncodedString(DFA10_specialS);
    static final short[][] DFA10_transition;

    static {
        int numStates = DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
        }
    }

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = DFA10_eot;
            this.eof = DFA10_eof;
            this.min = DFA10_min;
            this.max = DFA10_max;
            this.accept = DFA10_accept;
            this.special = DFA10_special;
            this.transition = DFA10_transition;
        }

        public String getDescription() {
            return "130:1: param : ( ^( PARAM IDENTIFIER ) | ^( PARAM OUT IDENTIFIER ) | ^( PARAM DONT_CARE ) | ^( PARAM BOOL_TYPE bool= ( TRUE | FALSE ) ) | ^( PARAM STRING_TYPE str= IDENTIFIER ) | ^( PARAM INT_TYPE in= IDENTIFIER ) | ^( PARAM REAL_TYPE r= IDENTIFIER ) );";
        }
    }

    public static final BitSet FOLLOW_PROGRAM_in_program57 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_functions_in_program59 =
        new BitSet(new long[] {0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_program61 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions79 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions83 =
        new BitSet(new long[] {0x0000000000000088L});
    public static final BitSet FOLLOW_FUNCTION_in_function96 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_function98 =
        new BitSet(new long[] {0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_function100 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block117 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_block122 =
        new BitSet(new long[] {0x000000000FABE708L});
    public static final BitSet FOLLOW_ALAP_in_statement141 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement143 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_statement151 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement153 =
        new BitSet(new long[] {0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement155 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_statement163 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement165 =
        new BitSet(new long[] {0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement167 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_DO_in_statement175 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement177 =
        new BitSet(new long[] {0x0000000000402100L});
    public static final BitSet FOLLOW_condition_in_statement179 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_statement187 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement189 =
        new BitSet(new long[] {0x0000000000000028L});
    public static final BitSet FOLLOW_block_in_statement192 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_statement202 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement204 =
        new BitSet(new long[] {0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement206 =
        new BitSet(new long[] {0x0000000000000028L});
    public static final BitSet FOLLOW_block_in_statement209 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_statement219 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement221 =
        new BitSet(new long[] {0x0000000000000028L});
    public static final BitSet FOLLOW_expression_in_statement229 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_var_declaration_in_statement235 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_expression249 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression251 =
        new BitSet(new long[] {0x000000000F802100L});
    public static final BitSet FOLLOW_expression_in_expression253 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_in_expression260 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression264 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_STAR_in_expression283 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression285 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_SHARP_in_expression292 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression294 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_expression300 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expression305 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expression310 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_condition324 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_condition326 =
        new BitSet(new long[] {0x0000000000402100L});
    public static final BitSet FOLLOW_condition_in_condition328 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_condition335 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_condition341 =
        new BitSet(new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule355 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_rule359 =
        new BitSet(new long[] {0x0000000000000808L});
    public static final BitSet FOLLOW_param_in_rule363 =
        new BitSet(new long[] {0x0000000000000808L});
    public static final BitSet FOLLOW_VAR_in_var_declaration381 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_var_declaration385 =
        new BitSet(new long[] {0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_var_declaration405 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_param421 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_param423 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_param433 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_param435 =
        new BitSet(new long[] {0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_param437 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_param447 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_DONT_CARE_in_param449 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_param458 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_TYPE_in_param460 =
        new BitSet(new long[] {0x0000002000400000L});
    public static final BitSet FOLLOW_set_in_param464 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_param477 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_TYPE_in_param479 =
        new BitSet(new long[] {0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_param483 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_param492 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_INT_TYPE_in_param494 =
        new BitSet(new long[] {0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_param498 =
        new BitSet(new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_param507 =
        new BitSet(new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_REAL_TYPE_in_param509 =
        new BitSet(new long[] {0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_param513 =
        new BitSet(new long[] {0x0000000000000008L});

}