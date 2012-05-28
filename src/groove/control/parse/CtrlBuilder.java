// $ANTLR 3.4 E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g 2012-05-19 20:38:06

package groove.control.parse;

import groove.control.CtrlAut;
import groove.control.CtrlFactory;
import groove.view.FormatErrorSet;

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

@SuppressWarnings({"all", "warnings", "unchecked"})
public class CtrlBuilder extends TreeParser {
    public static final String[] tokenNames = new String[] {"<invalid>",
        "<EOR>", "<DOWN>", "<UP>", "ALAP", "AMP", "ANY", "ARG", "ARGS",
        "ASTERISK", "BAR", "BLOCK", "BOOL", "BSLASH", "CALL", "CHOICE",
        "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", "ELSE",
        "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF",
        "IMPORT", "INT", "INT_LIT", "IntegerNumber", "LCURLY", "LPAR", "MINUS",
        "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT",
        "PACKAGE", "PLUS", "PRIORITY", "PROGRAM", "QUOTE", "RCURLY", "REAL",
        "REAL_LIT", "RECIPE", "RECIPES", "RPAR", "SEMI", "SHARP", "SL_COMMENT",
        "STAR", "STRING", "STRING_LIT", "TRUE", "TRY", "UNTIL", "VAR", "WHILE",
        "WS"};

    public static final int EOF = -1;
    public static final int ALAP = 4;
    public static final int AMP = 5;
    public static final int ANY = 6;
    public static final int ARG = 7;
    public static final int ARGS = 8;
    public static final int ASTERISK = 9;
    public static final int BAR = 10;
    public static final int BLOCK = 11;
    public static final int BOOL = 12;
    public static final int BSLASH = 13;
    public static final int CALL = 14;
    public static final int CHOICE = 15;
    public static final int COMMA = 16;
    public static final int DO = 17;
    public static final int DONT_CARE = 18;
    public static final int DOT = 19;
    public static final int DO_UNTIL = 20;
    public static final int DO_WHILE = 21;
    public static final int ELSE = 22;
    public static final int EscapeSequence = 23;
    public static final int FALSE = 24;
    public static final int FUNCTION = 25;
    public static final int FUNCTIONS = 26;
    public static final int ID = 27;
    public static final int IF = 28;
    public static final int IMPORT = 29;
    public static final int INT = 30;
    public static final int INT_LIT = 31;
    public static final int IntegerNumber = 32;
    public static final int LCURLY = 33;
    public static final int LPAR = 34;
    public static final int MINUS = 35;
    public static final int ML_COMMENT = 36;
    public static final int NODE = 37;
    public static final int NOT = 38;
    public static final int NonIntegerNumber = 39;
    public static final int OR = 40;
    public static final int OTHER = 41;
    public static final int OUT = 42;
    public static final int PACKAGE = 43;
    public static final int PLUS = 44;
    public static final int PRIORITY = 45;
    public static final int PROGRAM = 46;
    public static final int QUOTE = 47;
    public static final int RCURLY = 48;
    public static final int REAL = 49;
    public static final int REAL_LIT = 50;
    public static final int RECIPE = 51;
    public static final int RECIPES = 52;
    public static final int RPAR = 53;
    public static final int SEMI = 54;
    public static final int SHARP = 55;
    public static final int SL_COMMENT = 56;
    public static final int STAR = 57;
    public static final int STRING = 58;
    public static final int STRING_LIT = 59;
    public static final int TRUE = 60;
    public static final int TRY = 61;
    public static final int UNTIL = 62;
    public static final int VAR = 63;
    public static final int WHILE = 64;
    public static final int WS = 65;

    // delegates
    public TreeParser[] getDelegates() {
        return new TreeParser[] {};
    }

    // delegators

    public CtrlBuilder(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }

    public CtrlBuilder(TreeNodeStream input, RecognizerSharedState state) {
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
        return CtrlBuilder.tokenNames;
    }

    public String getGrammarFileName() {
        return "E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g";
    }

    /** Builder for control automata. */
    private CtrlFactory builder;
    /** Namespace used for building the automaton. */
    private Namespace namespace;
    /** Helper class for some final static semantic checks. */
    private CtrlHelper helper;

    /**
     * Runs the builder on a given, checked syntax tree.
     */
    public CtrlAut run(CtrlTree tree, Namespace namespace)
        throws RecognitionException {
        this.builder = CtrlFactory.instance();
        this.namespace = namespace;
        this.helper = new CtrlHelper(this, namespace, null);
        CtrlTreeAdaptor treeAdaptor = new CtrlTreeAdaptor();
        setTreeAdaptor(treeAdaptor);
        setTreeNodeStream(treeAdaptor.createTreeNodeStream(tree));
        CtrlAut result = program().aut;
        return result == null ? null : result.clone(namespace.getFullName());
    }

    public FormatErrorSet getErrors() {
        return this.helper.getErrors();
    }

    public static class program_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        CtrlTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "program"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:46:1: program returns [ CtrlAut aut ] : ^( PROGRAM package_decl ( import_decl )* recipes functions block ) ;
    public final CtrlBuilder.program_return program()
        throws RecognitionException {
        CtrlBuilder.program_return retval = new CtrlBuilder.program_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PROGRAM1 = null;
        CtrlBuilder.package_decl_return package_decl2 = null;

        CtrlBuilder.import_decl_return import_decl3 = null;

        CtrlBuilder.recipes_return recipes4 = null;

        CtrlBuilder.functions_return functions5 = null;

        CtrlBuilder.block_return block6 = null;

        CtrlTree PROGRAM1_tree = null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:47:3: ( ^( PROGRAM package_decl ( import_decl )* recipes functions block ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:47:5: ^( PROGRAM package_decl ( import_decl )* recipes functions block )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    PROGRAM1 =
                        (CtrlTree) match(this.input, PROGRAM,
                            FOLLOW_PROGRAM_in_program59);

                    if (_first_0 == null) {
                        _first_0 = PROGRAM1;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_package_decl_in_program61);
                    package_decl2 = package_decl();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = package_decl2.tree;
                    }

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:47:28: ( import_decl )*
                    loop1: do {
                        int alt1 = 2;
                        int LA1_0 = this.input.LA(1);

                        if ((LA1_0 == IMPORT)) {
                            alt1 = 1;
                        }

                        switch (alt1) {
                        case 1:
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:47:28: import_decl
                        {
                            _last = (CtrlTree) this.input.LT(1);
                            pushFollow(FOLLOW_import_decl_in_program63);
                            import_decl3 = import_decl();

                            this.state._fsp--;

                            if (_first_1 == null) {
                                _first_1 = import_decl3.tree;
                            }

                            retval.tree = (CtrlTree) _first_0;
                            if (this.adaptor.getParent(retval.tree) != null
                                && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                retval.tree =
                                    (CtrlTree) this.adaptor.getParent(retval.tree);
                            }

                        }
                            break;

                        default:
                            break loop1;
                        }
                    } while (true);

                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_recipes_in_program66);
                    recipes4 = recipes();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = recipes4.tree;
                    }

                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_functions_in_program68);
                    functions5 = functions();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = functions5.tree;
                    }

                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_block_in_program70);
                    block6 = block();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = block6.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                if ((block6 != null ? ((CtrlTree) block6.tree) : null).getChildCount() == 0) {
                    retval.aut = null;
                } else {
                    retval.aut = (block6 != null ? block6.aut : null);
                }

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        }

        finally {
            // do for sure before leaving
        }
        return retval;
    }

    // $ANTLR end "program"

    public static class package_decl_return extends TreeRuleReturnScope {
        CtrlTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "package_decl"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:56:1: package_decl : ^( PACKAGE ID ) ;
    public final CtrlBuilder.package_decl_return package_decl()
        throws RecognitionException {
        CtrlBuilder.package_decl_return retval =
            new CtrlBuilder.package_decl_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PACKAGE7 = null;
        CtrlTree ID8 = null;

        CtrlTree PACKAGE7_tree = null;
        CtrlTree ID8_tree = null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:57:3: ( ^( PACKAGE ID ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:57:5: ^( PACKAGE ID )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    PACKAGE7 =
                        (CtrlTree) match(this.input, PACKAGE,
                            FOLLOW_PACKAGE_in_package_decl91);

                    if (_first_0 == null) {
                        _first_0 = PACKAGE7;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    ID8 =
                        (CtrlTree) match(this.input, ID,
                            FOLLOW_ID_in_package_decl93);

                    if (_first_1 == null) {
                        _first_1 = ID8;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        }

        finally {
            // do for sure before leaving
        }
        return retval;
    }

    // $ANTLR end "package_decl"

    public static class import_decl_return extends TreeRuleReturnScope {
        CtrlTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "import_decl"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:60:1: import_decl : ^( IMPORT ID ) ;
    public final CtrlBuilder.import_decl_return import_decl()
        throws RecognitionException {
        CtrlBuilder.import_decl_return retval =
            new CtrlBuilder.import_decl_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree IMPORT9 = null;
        CtrlTree ID10 = null;

        CtrlTree IMPORT9_tree = null;
        CtrlTree ID10_tree = null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:61:3: ( ^( IMPORT ID ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:61:5: ^( IMPORT ID )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    IMPORT9 =
                        (CtrlTree) match(this.input, IMPORT,
                            FOLLOW_IMPORT_in_import_decl110);

                    if (_first_0 == null) {
                        _first_0 = IMPORT9;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    ID10 =
                        (CtrlTree) match(this.input, ID,
                            FOLLOW_ID_in_import_decl112);

                    if (_first_1 == null) {
                        _first_1 = ID10;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        }

        finally {
            // do for sure before leaving
        }
        return retval;
    }

    // $ANTLR end "import_decl"

    public static class functions_return extends TreeRuleReturnScope {
        CtrlTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "functions"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:64:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final CtrlBuilder.functions_return functions()
        throws RecognitionException {
        CtrlBuilder.functions_return retval =
            new CtrlBuilder.functions_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree FUNCTIONS11 = null;
        CtrlBuilder.function_return function12 = null;

        CtrlTree FUNCTIONS11_tree = null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:65:3: ( ^( FUNCTIONS ( function )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:65:5: ^( FUNCTIONS ( function )* )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    FUNCTIONS11 =
                        (CtrlTree) match(this.input, FUNCTIONS,
                            FOLLOW_FUNCTIONS_in_functions127);

                    if (_first_0 == null) {
                        _first_0 = FUNCTIONS11;
                    }
                    if (this.input.LA(1) == Token.DOWN) {
                        match(this.input, Token.DOWN, null);
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:65:17: ( function )*
                        loop2: do {
                            int alt2 = 2;
                            int LA2_0 = this.input.LA(1);

                            if ((LA2_0 == FUNCTION)) {
                                alt2 = 1;
                            }

                            switch (alt2) {
                            case 1:
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:65:17: function
                            {
                                _last = (CtrlTree) this.input.LT(1);
                                pushFollow(FOLLOW_function_in_functions129);
                                function12 = function();

                                this.state._fsp--;

                                if (_first_1 == null) {
                                    _first_1 = function12.tree;
                                }

                                retval.tree = (CtrlTree) _first_0;
                                if (this.adaptor.getParent(retval.tree) != null
                                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                    retval.tree =
                                        (CtrlTree) this.adaptor.getParent(retval.tree);
                                }

                            }
                                break;

                            default:
                                break loop2;
                            }
                        } while (true);

                        match(this.input, Token.UP, null);
                    }
                    _last = _save_last_1;
                }

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        }

        finally {
            // do for sure before leaving
        }
        return retval;
    }

    // $ANTLR end "functions"

    public static class function_return extends TreeRuleReturnScope {
        CtrlTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "function"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:68:1: function : ^( FUNCTION ID block ) ;
    public final CtrlBuilder.function_return function()
        throws RecognitionException {
        CtrlBuilder.function_return retval = new CtrlBuilder.function_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree FUNCTION13 = null;
        CtrlTree ID14 = null;
        CtrlBuilder.block_return block15 = null;

        CtrlTree FUNCTION13_tree = null;
        CtrlTree ID14_tree = null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:69:3: ( ^( FUNCTION ID block ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:69:5: ^( FUNCTION ID block )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    FUNCTION13 =
                        (CtrlTree) match(this.input, FUNCTION,
                            FOLLOW_FUNCTION_in_function145);

                    if (_first_0 == null) {
                        _first_0 = FUNCTION13;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    ID14 =
                        (CtrlTree) match(this.input, ID,
                            FOLLOW_ID_in_function147);

                    if (_first_1 == null) {
                        _first_1 = ID14;
                    }

                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_block_in_function149);
                    block15 = block();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = block15.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                this.namespace.addBody(
                    this.helper.qualify((ID14 != null ? ID14.getText() : null)),
                    (block15 != null ? block15.aut : null));

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        }

        finally {
            // do for sure before leaving
        }
        return retval;
    }

    // $ANTLR end "function"

    public static class recipes_return extends TreeRuleReturnScope {
        CtrlTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "recipes"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:73:1: recipes : ^( RECIPES ( recipe )* ) ;
    public final CtrlBuilder.recipes_return recipes()
        throws RecognitionException {
        CtrlBuilder.recipes_return retval = new CtrlBuilder.recipes_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree RECIPES16 = null;
        CtrlBuilder.recipe_return recipe17 = null;

        CtrlTree RECIPES16_tree = null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:74:3: ( ^( RECIPES ( recipe )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:74:5: ^( RECIPES ( recipe )* )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    RECIPES16 =
                        (CtrlTree) match(this.input, RECIPES,
                            FOLLOW_RECIPES_in_recipes172);

                    if (_first_0 == null) {
                        _first_0 = RECIPES16;
                    }
                    if (this.input.LA(1) == Token.DOWN) {
                        match(this.input, Token.DOWN, null);
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:74:15: ( recipe )*
                        loop3: do {
                            int alt3 = 2;
                            int LA3_0 = this.input.LA(1);

                            if ((LA3_0 == RECIPE)) {
                                alt3 = 1;
                            }

                            switch (alt3) {
                            case 1:
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:74:15: recipe
                            {
                                _last = (CtrlTree) this.input.LT(1);
                                pushFollow(FOLLOW_recipe_in_recipes174);
                                recipe17 = recipe();

                                this.state._fsp--;

                                if (_first_1 == null) {
                                    _first_1 = recipe17.tree;
                                }

                                retval.tree = (CtrlTree) _first_0;
                                if (this.adaptor.getParent(retval.tree) != null
                                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                    retval.tree =
                                        (CtrlTree) this.adaptor.getParent(retval.tree);
                                }

                            }
                                break;

                            default:
                                break loop3;
                            }
                        } while (true);

                        match(this.input, Token.UP, null);
                    }
                    _last = _save_last_1;
                }

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        }

        finally {
            // do for sure before leaving
        }
        return retval;
    }

    // $ANTLR end "recipes"

    public static class recipe_return extends TreeRuleReturnScope {
        CtrlTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "recipe"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:77:1: recipe : ^( RECIPE ID ( INT_LIT )? block ) ;
    public final CtrlBuilder.recipe_return recipe() throws RecognitionException {
        CtrlBuilder.recipe_return retval = new CtrlBuilder.recipe_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree RECIPE18 = null;
        CtrlTree ID19 = null;
        CtrlTree INT_LIT20 = null;
        CtrlBuilder.block_return block21 = null;

        CtrlTree RECIPE18_tree = null;
        CtrlTree ID19_tree = null;
        CtrlTree INT_LIT20_tree = null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:78:3: ( ^( RECIPE ID ( INT_LIT )? block ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:78:5: ^( RECIPE ID ( INT_LIT )? block )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    RECIPE18 =
                        (CtrlTree) match(this.input, RECIPE,
                            FOLLOW_RECIPE_in_recipe190);

                    if (_first_0 == null) {
                        _first_0 = RECIPE18;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    ID19 =
                        (CtrlTree) match(this.input, ID, FOLLOW_ID_in_recipe192);

                    if (_first_1 == null) {
                        _first_1 = ID19;
                    }

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:78:17: ( INT_LIT )?
                    int alt4 = 2;
                    int LA4_0 = this.input.LA(1);

                    if ((LA4_0 == INT_LIT)) {
                        alt4 = 1;
                    }
                    switch (alt4) {
                    case 1:
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:78:17: INT_LIT
                    {
                        _last = (CtrlTree) this.input.LT(1);
                        INT_LIT20 =
                            (CtrlTree) match(this.input, INT_LIT,
                                FOLLOW_INT_LIT_in_recipe194);

                        if (_first_1 == null) {
                            _first_1 = INT_LIT20;
                        }

                        retval.tree = (CtrlTree) _first_0;
                        if (this.adaptor.getParent(retval.tree) != null
                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                            retval.tree =
                                (CtrlTree) this.adaptor.getParent(retval.tree);
                        }

                    }
                        break;

                    }

                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_block_in_recipe197);
                    block21 = block();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = block21.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                String recipeName =
                    this.helper.qualify((ID19 != null ? ID19.getText() : null));
                this.helper.checkRecipeBody(RECIPE18, recipeName,
                    (block21 != null ? block21.aut : null));
                this.namespace.addBody(recipeName, (block21 != null
                        ? block21.aut : null));

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        }

        finally {
            // do for sure before leaving
        }
        return retval;
    }

    // $ANTLR end "recipe"

    public static class block_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        CtrlTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "block"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:86:1: block returns [ CtrlAut aut ] : ^( BLOCK ( stat )* ) ;
    public final CtrlBuilder.block_return block() throws RecognitionException {
        CtrlBuilder.block_return retval = new CtrlBuilder.block_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree BLOCK22 = null;
        CtrlBuilder.stat_return stat23 = null;

        CtrlTree BLOCK22_tree = null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:87:3: ( ^( BLOCK ( stat )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:87:5: ^( BLOCK ( stat )* )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    BLOCK22 =
                        (CtrlTree) match(this.input, BLOCK,
                            FOLLOW_BLOCK_in_block223);

                    if (_first_0 == null) {
                        _first_0 = BLOCK22;
                    }
                    retval.aut = this.builder.buildTrue();

                    if (this.input.LA(1) == Token.DOWN) {
                        match(this.input, Token.DOWN, null);
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:89:8: ( stat )*
                        loop5: do {
                            int alt5 = 2;
                            int LA5_0 = this.input.LA(1);

                            if ((LA5_0 == ALAP || LA5_0 == ANY
                                || LA5_0 == BLOCK
                                || (LA5_0 >= CALL && LA5_0 <= CHOICE)
                                || LA5_0 == IF || LA5_0 == OTHER
                                || LA5_0 == STAR || (LA5_0 >= TRUE && LA5_0 <= WHILE))) {
                                alt5 = 1;
                            }

                            switch (alt5) {
                            case 1:
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:89:10: stat
                            {
                                _last = (CtrlTree) this.input.LT(1);
                                pushFollow(FOLLOW_stat_in_block243);
                                stat23 = stat();

                                this.state._fsp--;

                                if (_first_1 == null) {
                                    _first_1 = stat23.tree;
                                }

                                retval.aut =
                                    this.builder.buildSeq(retval.aut,
                                        (stat23 != null ? stat23.aut : null));

                                retval.tree = (CtrlTree) _first_0;
                                if (this.adaptor.getParent(retval.tree) != null
                                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                    retval.tree =
                                        (CtrlTree) this.adaptor.getParent(retval.tree);
                                }

                            }
                                break;

                            default:
                                break loop5;
                            }
                        } while (true);

                        match(this.input, Token.UP, null);
                    }
                    _last = _save_last_1;
                }

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        }

        finally {
            // do for sure before leaving
        }
        return retval;
    }

    // $ANTLR end "block"

    public static class stat_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        CtrlTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "stat"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:95:1: stat returns [ CtrlAut aut ] : ( block | var_decl | ^( ALAP s= stat ) | ^( WHILE c= stat s= stat ) | ^( UNTIL c= stat s= stat ) | ^( TRY s1= stat (s2= stat )? ) | ^( IF c= stat s1= stat (s2= stat )? ) | ^( CHOICE s1= stat (s2= stat )* ) | ^( STAR s= stat ) | rule | ANY | OTHER | TRUE );
    public final CtrlBuilder.stat_return stat() throws RecognitionException {
        CtrlBuilder.stat_return retval = new CtrlBuilder.stat_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree ALAP26 = null;
        CtrlTree WHILE27 = null;
        CtrlTree UNTIL28 = null;
        CtrlTree TRY29 = null;
        CtrlTree IF30 = null;
        CtrlTree CHOICE31 = null;
        CtrlTree STAR32 = null;
        CtrlTree ANY34 = null;
        CtrlTree OTHER35 = null;
        CtrlTree TRUE36 = null;
        CtrlBuilder.stat_return s = null;

        CtrlBuilder.stat_return c = null;

        CtrlBuilder.stat_return s1 = null;

        CtrlBuilder.stat_return s2 = null;

        CtrlBuilder.block_return block24 = null;

        CtrlBuilder.var_decl_return var_decl25 = null;

        CtrlBuilder.rule_return rule33 = null;

        CtrlTree ALAP26_tree = null;
        CtrlTree WHILE27_tree = null;
        CtrlTree UNTIL28_tree = null;
        CtrlTree TRY29_tree = null;
        CtrlTree IF30_tree = null;
        CtrlTree CHOICE31_tree = null;
        CtrlTree STAR32_tree = null;
        CtrlTree ANY34_tree = null;
        CtrlTree OTHER35_tree = null;
        CtrlTree TRUE36_tree = null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:96:3: ( block | var_decl | ^( ALAP s= stat ) | ^( WHILE c= stat s= stat ) | ^( UNTIL c= stat s= stat ) | ^( TRY s1= stat (s2= stat )? ) | ^( IF c= stat s1= stat (s2= stat )? ) | ^( CHOICE s1= stat (s2= stat )* ) | ^( STAR s= stat ) | rule | ANY | OTHER | TRUE )
            int alt9 = 13;
            switch (this.input.LA(1)) {
            case BLOCK: {
                alt9 = 1;
            }
                break;
            case VAR: {
                alt9 = 2;
            }
                break;
            case ALAP: {
                alt9 = 3;
            }
                break;
            case WHILE: {
                alt9 = 4;
            }
                break;
            case UNTIL: {
                alt9 = 5;
            }
                break;
            case TRY: {
                alt9 = 6;
            }
                break;
            case IF: {
                alt9 = 7;
            }
                break;
            case CHOICE: {
                alt9 = 8;
            }
                break;
            case STAR: {
                alt9 = 9;
            }
                break;
            case CALL: {
                alt9 = 10;
            }
                break;
            case ANY: {
                alt9 = 11;
            }
                break;
            case OTHER: {
                alt9 = 12;
            }
                break;
            case TRUE: {
                alt9 = 13;
            }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, this.input);

                throw nvae;

            }

            switch (alt9) {
            case 1:
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:96:5: block
            {
                _last = (CtrlTree) this.input.LT(1);
                pushFollow(FOLLOW_block_in_stat288);
                block24 = block();

                this.state._fsp--;

                if (_first_0 == null) {
                    _first_0 = block24.tree;
                }

                retval.aut = (block24 != null ? block24.aut : null);

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }
                break;
            case 2:
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:98:5: var_decl
            {
                _last = (CtrlTree) this.input.LT(1);
                pushFollow(FOLLOW_var_decl_in_stat300);
                var_decl25 = var_decl();

                this.state._fsp--;

                if (_first_0 == null) {
                    _first_0 = var_decl25.tree;
                }

                retval.aut = this.builder.buildTrue();

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }
                break;
            case 3:
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:100:5: ^( ALAP s= stat )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    ALAP26 =
                        (CtrlTree) match(this.input, ALAP,
                            FOLLOW_ALAP_in_stat313);

                    if (_first_0 == null) {
                        _first_0 = ALAP26;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat317);
                    s = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = s.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.aut = this.builder.buildAlap((s != null ? s.aut : null));

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }
                break;
            case 4:
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:102:5: ^( WHILE c= stat s= stat )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    WHILE27 =
                        (CtrlTree) match(this.input, WHILE,
                            FOLLOW_WHILE_in_stat331);

                    if (_first_0 == null) {
                        _first_0 = WHILE27;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat335);
                    c = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = c.tree;
                    }

                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat339);
                    s = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = s.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.aut =
                    this.builder.buildWhileDo((c != null ? c.aut : null),
                        (s != null ? s.aut : null));

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }
                break;
            case 5:
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:104:5: ^( UNTIL c= stat s= stat )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    UNTIL28 =
                        (CtrlTree) match(this.input, UNTIL,
                            FOLLOW_UNTIL_in_stat353);

                    if (_first_0 == null) {
                        _first_0 = UNTIL28;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat357);
                    c = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = c.tree;
                    }

                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat361);
                    s = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = s.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.aut =
                    this.builder.buildUntilDo((c != null ? c.aut : null),
                        (s != null ? s.aut : null));

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }
                break;
            case 6:
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:106:5: ^( TRY s1= stat (s2= stat )? )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    TRY29 =
                        (CtrlTree) match(this.input, TRY, FOLLOW_TRY_in_stat375);

                    if (_first_0 == null) {
                        _first_0 = TRY29;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat379);
                    s1 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = s1.tree;
                    }

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:106:19: (s2= stat )?
                    int alt6 = 2;
                    int LA6_0 = this.input.LA(1);

                    if ((LA6_0 == ALAP || LA6_0 == ANY || LA6_0 == BLOCK
                        || (LA6_0 >= CALL && LA6_0 <= CHOICE) || LA6_0 == IF
                        || LA6_0 == OTHER || LA6_0 == STAR || (LA6_0 >= TRUE && LA6_0 <= WHILE))) {
                        alt6 = 1;
                    }
                    switch (alt6) {
                    case 1:
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:106:20: s2= stat
                    {
                        _last = (CtrlTree) this.input.LT(1);
                        pushFollow(FOLLOW_stat_in_stat384);
                        s2 = stat();

                        this.state._fsp--;

                        if (_first_1 == null) {
                            _first_1 = s2.tree;
                        }

                        retval.tree = (CtrlTree) _first_0;
                        if (this.adaptor.getParent(retval.tree) != null
                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                            retval.tree =
                                (CtrlTree) this.adaptor.getParent(retval.tree);
                        }

                    }
                        break;

                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.aut =
                    this.builder.buildTryElse((s1 != null ? s1.aut : null),
                        (s2 != null ? s2.aut : null));

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }
                break;
            case 7:
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:108:5: ^( IF c= stat s1= stat (s2= stat )? )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    IF30 =
                        (CtrlTree) match(this.input, IF, FOLLOW_IF_in_stat400);

                    if (_first_0 == null) {
                        _first_0 = IF30;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat404);
                    c = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = c.tree;
                    }

                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat408);
                    s1 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = s1.tree;
                    }

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:108:25: (s2= stat )?
                    int alt7 = 2;
                    int LA7_0 = this.input.LA(1);

                    if ((LA7_0 == ALAP || LA7_0 == ANY || LA7_0 == BLOCK
                        || (LA7_0 >= CALL && LA7_0 <= CHOICE) || LA7_0 == IF
                        || LA7_0 == OTHER || LA7_0 == STAR || (LA7_0 >= TRUE && LA7_0 <= WHILE))) {
                        alt7 = 1;
                    }
                    switch (alt7) {
                    case 1:
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:108:26: s2= stat
                    {
                        _last = (CtrlTree) this.input.LT(1);
                        pushFollow(FOLLOW_stat_in_stat413);
                        s2 = stat();

                        this.state._fsp--;

                        if (_first_1 == null) {
                            _first_1 = s2.tree;
                        }

                        retval.tree = (CtrlTree) _first_0;
                        if (this.adaptor.getParent(retval.tree) != null
                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                            retval.tree =
                                (CtrlTree) this.adaptor.getParent(retval.tree);
                        }

                    }
                        break;

                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.aut =
                    this.builder.buildIfThenElse((c != null ? c.aut : null),
                        (s1 != null ? s1.aut : null), (s2 != null ? s2.aut
                                : null));

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }
                break;
            case 8:
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:110:5: ^( CHOICE s1= stat (s2= stat )* )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    CHOICE31 =
                        (CtrlTree) match(this.input, CHOICE,
                            FOLLOW_CHOICE_in_stat430);

                    if (_first_0 == null) {
                        _first_0 = CHOICE31;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat442);
                    s1 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = s1.tree;
                    }

                    retval.aut = (s1 != null ? s1.aut : null);

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:113:8: (s2= stat )*
                    loop8: do {
                        int alt8 = 2;
                        int LA8_0 = this.input.LA(1);

                        if ((LA8_0 == ALAP || LA8_0 == ANY || LA8_0 == BLOCK
                            || (LA8_0 >= CALL && LA8_0 <= CHOICE)
                            || LA8_0 == IF || LA8_0 == OTHER || LA8_0 == STAR || (LA8_0 >= TRUE && LA8_0 <= WHILE))) {
                            alt8 = 1;
                        }

                        switch (alt8) {
                        case 1:
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:113:10: s2= stat
                        {
                            _last = (CtrlTree) this.input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat464);
                            s2 = stat();

                            this.state._fsp--;

                            if (_first_1 == null) {
                                _first_1 = s2.tree;
                            }

                            retval.aut =
                                this.builder.buildOr(retval.aut, (s2 != null
                                        ? s2.aut : null));

                            retval.tree = (CtrlTree) _first_0;
                            if (this.adaptor.getParent(retval.tree) != null
                                && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                retval.tree =
                                    (CtrlTree) this.adaptor.getParent(retval.tree);
                            }

                        }
                            break;

                        default:
                            break loop8;
                        }
                    } while (true);

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }
                break;
            case 9:
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:117:5: ^( STAR s= stat )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    STAR32 =
                        (CtrlTree) match(this.input, STAR,
                            FOLLOW_STAR_in_stat499);

                    if (_first_0 == null) {
                        _first_0 = STAR32;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat503);
                    s = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = s.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.aut = this.builder.buildStar((s != null ? s.aut : null));

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }
                break;
            case 10:
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:119:5: rule
            {
                _last = (CtrlTree) this.input.LT(1);
                pushFollow(FOLLOW_rule_in_stat516);
                rule33 = rule();

                this.state._fsp--;

                if (_first_0 == null) {
                    _first_0 = rule33.tree;
                }

                retval.aut =
                    this.builder.buildCall((rule33 != null
                            ? ((CtrlTree) rule33.tree) : null).getCtrlCall(),
                        this.namespace);

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }
                break;
            case 11:
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:121:5: ANY
            {
                _last = (CtrlTree) this.input.LT(1);
                ANY34 =
                    (CtrlTree) match(this.input, ANY, FOLLOW_ANY_in_stat528);

                if (_first_0 == null) {
                    _first_0 = ANY34;
                }

                retval.aut = this.builder.buildAny(this.namespace);

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }
                break;
            case 12:
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:123:5: OTHER
            {
                _last = (CtrlTree) this.input.LT(1);
                OTHER35 =
                    (CtrlTree) match(this.input, OTHER, FOLLOW_OTHER_in_stat540);

                if (_first_0 == null) {
                    _first_0 = OTHER35;
                }

                retval.aut = this.builder.buildOther(this.namespace);

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }
                break;
            case 13:
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:126:5: TRUE
            {
                _last = (CtrlTree) this.input.LT(1);
                TRUE36 =
                    (CtrlTree) match(this.input, TRUE, FOLLOW_TRUE_in_stat552);

                if (_first_0 == null) {
                    _first_0 = TRUE36;
                }

                retval.aut = this.builder.buildTrue();

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }
                break;

            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        }

        finally {
            // do for sure before leaving
        }
        return retval;
    }

    // $ANTLR end "stat"

    public static class rule_return extends TreeRuleReturnScope {
        CtrlTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "rule"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:130:1: rule : ^( CALL ID ( ^( ARGS ( arg )* ) )? ) ;
    public final CtrlBuilder.rule_return rule() throws RecognitionException {
        CtrlBuilder.rule_return retval = new CtrlBuilder.rule_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree CALL37 = null;
        CtrlTree ID38 = null;
        CtrlTree ARGS39 = null;
        CtrlBuilder.arg_return arg40 = null;

        CtrlTree CALL37_tree = null;
        CtrlTree ID38_tree = null;
        CtrlTree ARGS39_tree = null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:3: ( ^( CALL ID ( ^( ARGS ( arg )* ) )? ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:5: ^( CALL ID ( ^( ARGS ( arg )* ) )? )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    CALL37 =
                        (CtrlTree) match(this.input, CALL,
                            FOLLOW_CALL_in_rule572);

                    if (_first_0 == null) {
                        _first_0 = CALL37;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    ID38 =
                        (CtrlTree) match(this.input, ID, FOLLOW_ID_in_rule574);

                    if (_first_1 == null) {
                        _first_1 = ID38;
                    }

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:15: ( ^( ARGS ( arg )* ) )?
                    int alt11 = 2;
                    int LA11_0 = this.input.LA(1);

                    if ((LA11_0 == ARGS)) {
                        alt11 = 1;
                    }
                    switch (alt11) {
                    case 1:
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:16: ^( ARGS ( arg )* )
                    {
                        _last = (CtrlTree) this.input.LT(1);
                        {
                            CtrlTree _save_last_2 = _last;
                            CtrlTree _first_2 = null;
                            _last = (CtrlTree) this.input.LT(1);
                            ARGS39 =
                                (CtrlTree) match(this.input, ARGS,
                                    FOLLOW_ARGS_in_rule578);

                            if (_first_1 == null) {
                                _first_1 = ARGS39;
                            }
                            if (this.input.LA(1) == Token.DOWN) {
                                match(this.input, Token.DOWN, null);
                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:23: ( arg )*
                                loop10: do {
                                    int alt10 = 2;
                                    int LA10_0 = this.input.LA(1);

                                    if ((LA10_0 == ARG)) {
                                        alt10 = 1;
                                    }

                                    switch (alt10) {
                                    case 1:
                                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:23: arg
                                    {
                                        _last = (CtrlTree) this.input.LT(1);
                                        pushFollow(FOLLOW_arg_in_rule580);
                                        arg40 = arg();

                                        this.state._fsp--;

                                        if (_first_2 == null) {
                                            _first_2 = arg40.tree;
                                        }

                                        retval.tree = (CtrlTree) _first_0;
                                        if (this.adaptor.getParent(retval.tree) != null
                                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                            retval.tree =
                                                (CtrlTree) this.adaptor.getParent(retval.tree);
                                        }

                                    }
                                        break;

                                    default:
                                        break loop10;
                                    }
                                } while (true);

                                match(this.input, Token.UP, null);
                            }
                            _last = _save_last_2;
                        }

                        retval.tree = (CtrlTree) _first_0;
                        if (this.adaptor.getParent(retval.tree) != null
                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                            retval.tree =
                                (CtrlTree) this.adaptor.getParent(retval.tree);
                        }

                    }
                        break;

                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        }

        finally {
            // do for sure before leaving
        }
        return retval;
    }

    // $ANTLR end "rule"

    public static class var_decl_return extends TreeRuleReturnScope {
        CtrlTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "var_decl"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:134:1: var_decl : ^( VAR type ( ID )+ ) ;
    public final CtrlBuilder.var_decl_return var_decl()
        throws RecognitionException {
        CtrlBuilder.var_decl_return retval = new CtrlBuilder.var_decl_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree VAR41 = null;
        CtrlTree ID43 = null;
        CtrlBuilder.type_return type42 = null;

        CtrlTree VAR41_tree = null;
        CtrlTree ID43_tree = null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:135:2: ( ^( VAR type ( ID )+ ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:135:4: ^( VAR type ( ID )+ )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    VAR41 =
                        (CtrlTree) match(this.input, VAR,
                            FOLLOW_VAR_in_var_decl599);

                    if (_first_0 == null) {
                        _first_0 = VAR41;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_type_in_var_decl601);
                    type42 = type();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = type42.tree;
                    }

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:135:16: ( ID )+
                    int cnt12 = 0;
                    loop12: do {
                        int alt12 = 2;
                        int LA12_0 = this.input.LA(1);

                        if ((LA12_0 == ID)) {
                            alt12 = 1;
                        }

                        switch (alt12) {
                        case 1:
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:135:16: ID
                        {
                            _last = (CtrlTree) this.input.LT(1);
                            ID43 =
                                (CtrlTree) match(this.input, ID,
                                    FOLLOW_ID_in_var_decl603);

                            if (_first_1 == null) {
                                _first_1 = ID43;
                            }

                            retval.tree = (CtrlTree) _first_0;
                            if (this.adaptor.getParent(retval.tree) != null
                                && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                retval.tree =
                                    (CtrlTree) this.adaptor.getParent(retval.tree);
                            }

                        }
                            break;

                        default:
                            if (cnt12 >= 1) {
                                break loop12;
                            }
                            EarlyExitException eee =
                                new EarlyExitException(12, this.input);
                            throw eee;
                        }
                        cnt12++;
                    } while (true);

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        }

        finally {
            // do for sure before leaving
        }
        return retval;
    }

    // $ANTLR end "var_decl"

    public static class type_return extends TreeRuleReturnScope {
        CtrlTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "type"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:138:1: type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlBuilder.type_return type() throws RecognitionException {
        CtrlBuilder.type_return retval = new CtrlBuilder.type_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree set44 = null;

        CtrlTree set44_tree = null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:139:3: ( NODE | BOOL | STRING | INT | REAL )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:
            {
                _last = (CtrlTree) this.input.LT(1);
                set44 = (CtrlTree) this.input.LT(1);

                if (this.input.LA(1) == BOOL || this.input.LA(1) == INT
                    || this.input.LA(1) == NODE || this.input.LA(1) == REAL
                    || this.input.LA(1) == STRING) {
                    this.input.consume();
                    this.state.errorRecovery = false;
                } else {
                    MismatchedSetException mse =
                        new MismatchedSetException(null, this.input);
                    throw mse;
                }

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        }

        finally {
            // do for sure before leaving
        }
        return retval;
    }

    // $ANTLR end "type"

    public static class arg_return extends TreeRuleReturnScope {
        CtrlTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "arg"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:142:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final CtrlBuilder.arg_return arg() throws RecognitionException {
        CtrlBuilder.arg_return retval = new CtrlBuilder.arg_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree ARG45 = null;
        CtrlTree OUT46 = null;
        CtrlTree ID47 = null;
        CtrlTree DONT_CARE48 = null;
        CtrlBuilder.literal_return literal49 = null;

        CtrlTree ARG45_tree = null;
        CtrlTree OUT46_tree = null;
        CtrlTree ID47_tree = null;
        CtrlTree DONT_CARE48_tree = null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:143:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:143:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    ARG45 =
                        (CtrlTree) match(this.input, ARG, FOLLOW_ARG_in_arg650);

                    if (_first_0 == null) {
                        _first_0 = ARG45;
                    }
                    match(this.input, Token.DOWN, null);
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:143:11: ( ( OUT )? ID | DONT_CARE | literal )
                    int alt14 = 3;
                    switch (this.input.LA(1)) {
                    case ID:
                    case OUT: {
                        alt14 = 1;
                    }
                        break;
                    case DONT_CARE: {
                        alt14 = 2;
                    }
                        break;
                    case FALSE:
                    case INT_LIT:
                    case REAL_LIT:
                    case STRING_LIT:
                    case TRUE: {
                        alt14 = 3;
                    }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 14, 0, this.input);

                        throw nvae;

                    }

                    switch (alt14) {
                    case 1:
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:143:13: ( OUT )? ID
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:143:13: ( OUT )?
                        int alt13 = 2;
                        int LA13_0 = this.input.LA(1);

                        if ((LA13_0 == OUT)) {
                            alt13 = 1;
                        }
                        switch (alt13) {
                        case 1:
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:143:13: OUT
                        {
                            _last = (CtrlTree) this.input.LT(1);
                            OUT46 =
                                (CtrlTree) match(this.input, OUT,
                                    FOLLOW_OUT_in_arg654);

                            if (_first_1 == null) {
                                _first_1 = OUT46;
                            }

                            retval.tree = (CtrlTree) _first_0;
                            if (this.adaptor.getParent(retval.tree) != null
                                && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                                retval.tree =
                                    (CtrlTree) this.adaptor.getParent(retval.tree);
                            }

                        }
                            break;

                        }

                        _last = (CtrlTree) this.input.LT(1);
                        ID47 =
                            (CtrlTree) match(this.input, ID,
                                FOLLOW_ID_in_arg657);

                        if (_first_1 == null) {
                            _first_1 = ID47;
                        }

                        retval.tree = (CtrlTree) _first_0;
                        if (this.adaptor.getParent(retval.tree) != null
                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                            retval.tree =
                                (CtrlTree) this.adaptor.getParent(retval.tree);
                        }

                    }
                        break;
                    case 2:
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:143:23: DONT_CARE
                    {
                        _last = (CtrlTree) this.input.LT(1);
                        DONT_CARE48 =
                            (CtrlTree) match(this.input, DONT_CARE,
                                FOLLOW_DONT_CARE_in_arg661);

                        if (_first_1 == null) {
                            _first_1 = DONT_CARE48;
                        }

                        retval.tree = (CtrlTree) _first_0;
                        if (this.adaptor.getParent(retval.tree) != null
                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                            retval.tree =
                                (CtrlTree) this.adaptor.getParent(retval.tree);
                        }

                    }
                        break;
                    case 3:
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:143:35: literal
                    {
                        _last = (CtrlTree) this.input.LT(1);
                        pushFollow(FOLLOW_literal_in_arg665);
                        literal49 = literal();

                        this.state._fsp--;

                        if (_first_1 == null) {
                            _first_1 = literal49.tree;
                        }

                        retval.tree = (CtrlTree) _first_0;
                        if (this.adaptor.getParent(retval.tree) != null
                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                            retval.tree =
                                (CtrlTree) this.adaptor.getParent(retval.tree);
                        }

                    }
                        break;

                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        }

        finally {
            // do for sure before leaving
        }
        return retval;
    }

    // $ANTLR end "arg"

    public static class literal_return extends TreeRuleReturnScope {
        CtrlTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "literal"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:146:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlBuilder.literal_return literal()
        throws RecognitionException {
        CtrlBuilder.literal_return retval = new CtrlBuilder.literal_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree set50 = null;

        CtrlTree set50_tree = null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:147:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:
            {
                _last = (CtrlTree) this.input.LT(1);
                set50 = (CtrlTree) this.input.LT(1);

                if (this.input.LA(1) == FALSE
                    || this.input.LA(1) == INT_LIT
                    || this.input.LA(1) == REAL_LIT
                    || (this.input.LA(1) >= STRING_LIT && this.input.LA(1) <= TRUE)) {
                    this.input.consume();
                    this.state.errorRecovery = false;
                } else {
                    MismatchedSetException mse =
                        new MismatchedSetException(null, this.input);
                    throw mse;
                }

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }

            }

        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        }

        finally {
            // do for sure before leaving
        }
        return retval;
    }

    // $ANTLR end "literal"

    // Delegated rules

    public static final BitSet FOLLOW_PROGRAM_in_program59 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_package_decl_in_program61 = new BitSet(
        new long[] {0x0010000020000000L});
    public static final BitSet FOLLOW_import_decl_in_program63 = new BitSet(
        new long[] {0x0010000020000000L});
    public static final BitSet FOLLOW_recipes_in_program66 = new BitSet(
        new long[] {0x0000000004000000L});
    public static final BitSet FOLLOW_functions_in_program68 = new BitSet(
        new long[] {0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_program70 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_PACKAGE_in_package_decl91 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_package_decl93 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_IMPORT_in_import_decl110 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_import_decl112 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions127 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions129 = new BitSet(
        new long[] {0x0000000002000008L});
    public static final BitSet FOLLOW_FUNCTION_in_function145 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function147 = new BitSet(
        new long[] {0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_function149 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_RECIPES_in_recipes172 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_recipe_in_recipes174 = new BitSet(
        new long[] {0x0008000000000008L});
    public static final BitSet FOLLOW_RECIPE_in_recipe190 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_recipe192 = new BitSet(
        new long[] {0x0000000080000800L});
    public static final BitSet FOLLOW_INT_LIT_in_recipe194 = new BitSet(
        new long[] {0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_recipe197 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block223 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block243 = new BitSet(new long[] {
        0xF20002001000C858L, 0x0000000000000001L});
    public static final BitSet FOLLOW_block_in_stat288 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat300 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat313 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat317 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat331 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat335 = new BitSet(new long[] {
        0xF20002001000C850L, 0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat339 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat353 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat357 = new BitSet(new long[] {
        0xF20002001000C850L, 0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat361 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat375 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat379 = new BitSet(new long[] {
        0xF20002001000C858L, 0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat384 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat400 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat404 = new BitSet(new long[] {
        0xF20002001000C850L, 0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat408 = new BitSet(new long[] {
        0xF20002001000C858L, 0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat413 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat430 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat442 = new BitSet(new long[] {
        0xF20002001000C858L, 0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat464 = new BitSet(new long[] {
        0xF20002001000C858L, 0x0000000000000001L});
    public static final BitSet FOLLOW_STAR_in_stat499 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat503 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat516 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat528 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat540 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat552 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule572 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule574 = new BitSet(
        new long[] {0x0000000000000108L});
    public static final BitSet FOLLOW_ARGS_in_rule578 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_arg_in_rule580 = new BitSet(
        new long[] {0x0000000000000088L});
    public static final BitSet FOLLOW_VAR_in_var_decl599 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl601 = new BitSet(
        new long[] {0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_var_decl603 = new BitSet(
        new long[] {0x0000000008000008L});
    public static final BitSet FOLLOW_ARG_in_arg650 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg654 = new BitSet(
        new long[] {0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_arg657 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg661 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg665 = new BitSet(
        new long[] {0x0000000000000008L});

}