// $ANTLR 3.3 Nov 30, 2010 12:45:30 D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g 2013-05-07 17:43:03

package groove.control.parse;

import groove.algebra.AlgebraFamily;
import groove.control.CtrlAut;
import groove.control.CtrlCall.Kind;
import groove.grammar.model.FormatErrorSet;

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
        "<EOR>", "<DOWN>", "<UP>", "RECIPES", "ARG", "ARGS", "BLOCK", "CALL",
        "DO_WHILE", "DO_UNTIL", "FUNCTIONS", "PROGRAM", "VAR", "PACKAGE",
        "SEMI", "IMPORT", "ID", "DOT", "RECIPE", "LPAR", "RPAR", "FUNCTION",
        "LCURLY", "RCURLY", "ALAP", "WHILE", "UNTIL", "DO", "IF", "ELSE",
        "TRY", "CHOICE", "OR", "BAR", "TRUE", "PLUS", "ASTERISK", "SHARP",
        "ANY", "OTHER", "COMMA", "OUT", "DONT_CARE", "FALSE", "STRING_LIT",
        "INT_LIT", "REAL_LIT", "NODE", "BOOL", "STRING", "INT", "REAL",
        "PRIORITY", "STAR", "IntegerNumber", "NonIntegerNumber", "QUOTE",
        "EscapeSequence", "BSLASH", "AMP", "NOT", "MINUS", "ML_COMMENT",
        "SL_COMMENT", "WS"};
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
        return "D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g";
    }

    /** Helper class to convert AST trees to namespace. */
    private CtrlHelper helper;

    public void displayRecognitionError(String[] tokenNames,
            RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        this.helper.addError(hdr + " " + msg, e.line, e.charPositionInLine);
    }

    public FormatErrorSet getErrors() {
        return this.helper.getErrors();
    }

    /**
     * Runs the lexer and parser on a given input character stream,
     * with a (presumably empty) namespace.
     * @return the resulting syntax tree
     */
    public CtrlTree run(CtrlTree tree, Namespace namespace, AlgebraFamily family)
        throws RecognitionException {
        this.helper = new CtrlHelper(this, namespace, family);
        CtrlTreeAdaptor treeAdaptor = new CtrlTreeAdaptor();
        setTreeAdaptor(treeAdaptor);
        setTreeNodeStream(treeAdaptor.createTreeNodeStream(tree));
        return (CtrlTree) program().getTree();
    }

    public static class program_return extends TreeRuleReturnScope {
        CtrlTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "program"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:51:1: program : ^( PROGRAM package_decl ( import_decl )* functions recipes block ) ;
    public final CtrlChecker.program_return program()
        throws RecognitionException {
        CtrlChecker.program_return retval = new CtrlChecker.program_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PROGRAM1 = null;
        CtrlChecker.package_decl_return package_decl2 = null;

        CtrlChecker.import_decl_return import_decl3 = null;

        CtrlChecker.functions_return functions4 = null;

        CtrlChecker.recipes_return recipes5 = null;

        CtrlChecker.block_return block6 = null;

        CtrlTree PROGRAM1_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:52:3: ( ^( PROGRAM package_decl ( import_decl )* functions recipes block ) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:52:5: ^( PROGRAM package_decl ( import_decl )* functions recipes block )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    PROGRAM1 =
                        (CtrlTree) match(this.input, PROGRAM,
                            FOLLOW_PROGRAM_in_program56);

                    if (_first_0 == null) {
                        _first_0 = PROGRAM1;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_package_decl_in_program58);
                    package_decl2 = package_decl();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = package_decl2.tree;
                    }
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:52:28: ( import_decl )*
                    loop1: do {
                        int alt1 = 2;
                        int LA1_0 = this.input.LA(1);

                        if ((LA1_0 == IMPORT)) {
                            alt1 = 1;
                        }

                        switch (alt1) {
                        case 1:
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:52:28: import_decl
                        {
                            _last = (CtrlTree) this.input.LT(1);
                            pushFollow(FOLLOW_import_decl_in_program60);
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
                    pushFollow(FOLLOW_functions_in_program63);
                    functions4 = functions();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = functions4.tree;
                    }
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_recipes_in_program65);
                    recipes5 = recipes();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = recipes5.tree;
                    }
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_block_in_program67);
                    block6 = block();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = block6.tree;
                    }

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                if ((block6 != null ? ((CtrlTree) block6.tree) : null).getChildCount() == 0) {
                    this.helper.checkAny(PROGRAM1);
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
        } finally {
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
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:59:1: package_decl : ^( PACKAGE ID ) ;
    public final CtrlChecker.package_decl_return package_decl()
        throws RecognitionException {
        CtrlChecker.package_decl_return retval =
            new CtrlChecker.package_decl_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PACKAGE7 = null;
        CtrlTree ID8 = null;

        CtrlTree PACKAGE7_tree = null;
        CtrlTree ID8_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:60:3: ( ^( PACKAGE ID ) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:60:5: ^( PACKAGE ID )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    PACKAGE7 =
                        (CtrlTree) match(this.input, PACKAGE,
                            FOLLOW_PACKAGE_in_package_decl90);

                    if (_first_0 == null) {
                        _first_0 = PACKAGE7;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    ID8 =
                        (CtrlTree) match(this.input, ID,
                            FOLLOW_ID_in_package_decl92);

                    if (_first_1 == null) {
                        _first_1 = ID8;
                    }
                    this.helper.checkPackage(ID8);

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
        } finally {
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
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:65:1: import_decl : ^( IMPORT ID ) ;
    public final CtrlChecker.import_decl_return import_decl()
        throws RecognitionException {
        CtrlChecker.import_decl_return retval =
            new CtrlChecker.import_decl_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree IMPORT9 = null;
        CtrlTree ID10 = null;

        CtrlTree IMPORT9_tree = null;
        CtrlTree ID10_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:66:3: ( ^( IMPORT ID ) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:66:5: ^( IMPORT ID )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    IMPORT9 =
                        (CtrlTree) match(this.input, IMPORT,
                            FOLLOW_IMPORT_in_import_decl125);

                    if (_first_0 == null) {
                        _first_0 = IMPORT9;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    ID10 =
                        (CtrlTree) match(this.input, ID,
                            FOLLOW_ID_in_import_decl127);

                    if (_first_1 == null) {
                        _first_1 = ID10;
                    }
                    this.helper.checkImport(ID10);

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
        } finally {
        }
        return retval;
    }

    // $ANTLR end "import_decl"

    public static class recipes_return extends TreeRuleReturnScope {
        CtrlTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "recipes"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:71:1: recipes : ^( RECIPES ( recipe )* ) ;
    public final CtrlChecker.recipes_return recipes()
        throws RecognitionException {
        CtrlChecker.recipes_return retval = new CtrlChecker.recipes_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree RECIPES11 = null;
        CtrlChecker.recipe_return recipe12 = null;

        CtrlTree RECIPES11_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:72:3: ( ^( RECIPES ( recipe )* ) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:72:5: ^( RECIPES ( recipe )* )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    RECIPES11 =
                        (CtrlTree) match(this.input, RECIPES,
                            FOLLOW_RECIPES_in_recipes157);

                    if (_first_0 == null) {
                        _first_0 = RECIPES11;
                    }
                    if (this.input.LA(1) == Token.DOWN) {
                        match(this.input, Token.DOWN, null);
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:72:15: ( recipe )*
                        loop2: do {
                            int alt2 = 2;
                            int LA2_0 = this.input.LA(1);

                            if ((LA2_0 == RECIPE)) {
                                alt2 = 1;
                            }

                            switch (alt2) {
                            case 1:
                            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:72:15: recipe
                            {
                                _last = (CtrlTree) this.input.LT(1);
                                pushFollow(FOLLOW_recipe_in_recipes159);
                                recipe12 = recipe();

                                this.state._fsp--;

                                if (_first_1 == null) {
                                    _first_1 = recipe12.tree;
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
        } finally {
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
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:75:1: recipe : ^( RECIPE ID ( INT_LIT )? block ) ;
    public final CtrlChecker.recipe_return recipe() throws RecognitionException {
        CtrlChecker.recipe_return retval = new CtrlChecker.recipe_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree RECIPE13 = null;
        CtrlTree ID14 = null;
        CtrlTree INT_LIT15 = null;
        CtrlChecker.block_return block16 = null;

        CtrlTree RECIPE13_tree = null;
        CtrlTree ID14_tree = null;
        CtrlTree INT_LIT15_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:76:3: ( ^( RECIPE ID ( INT_LIT )? block ) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:76:5: ^( RECIPE ID ( INT_LIT )? block )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    RECIPE13 =
                        (CtrlTree) match(this.input, RECIPE,
                            FOLLOW_RECIPE_in_recipe176);

                    if (_first_0 == null) {
                        _first_0 = RECIPE13;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    ID14 =
                        (CtrlTree) match(this.input, ID, FOLLOW_ID_in_recipe178);

                    if (_first_1 == null) {
                        _first_1 = ID14;
                    }
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:76:18: ( INT_LIT )?
                    int alt3 = 2;
                    int LA3_0 = this.input.LA(1);

                    if ((LA3_0 == INT_LIT)) {
                        alt3 = 1;
                    }
                    switch (alt3) {
                    case 1:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:76:18: INT_LIT
                    {
                        _last = (CtrlTree) this.input.LT(1);
                        INT_LIT15 =
                            (CtrlTree) match(this.input, INT_LIT,
                                FOLLOW_INT_LIT_in_recipe180);

                        if (_first_1 == null) {
                            _first_1 = INT_LIT15;
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

                    this.helper.startBody(ID14, Kind.RECIPE);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_block_in_recipe200);
                    block16 = block();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = block16.tree;
                    }
                    this.helper.endBody();

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
        } finally {
        }
        return retval;
    }

    // $ANTLR end "recipe"

    public static class functions_return extends TreeRuleReturnScope {
        CtrlTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "functions"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:83:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final CtrlChecker.functions_return functions()
        throws RecognitionException {
        CtrlChecker.functions_return retval =
            new CtrlChecker.functions_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree FUNCTIONS17 = null;
        CtrlChecker.function_return function18 = null;

        CtrlTree FUNCTIONS17_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:84:3: ( ^( FUNCTIONS ( function )* ) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:84:5: ^( FUNCTIONS ( function )* )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    FUNCTIONS17 =
                        (CtrlTree) match(this.input, FUNCTIONS,
                            FOLLOW_FUNCTIONS_in_functions231);

                    if (_first_0 == null) {
                        _first_0 = FUNCTIONS17;
                    }
                    if (this.input.LA(1) == Token.DOWN) {
                        match(this.input, Token.DOWN, null);
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:84:17: ( function )*
                        loop4: do {
                            int alt4 = 2;
                            int LA4_0 = this.input.LA(1);

                            if ((LA4_0 == FUNCTION)) {
                                alt4 = 1;
                            }

                            switch (alt4) {
                            case 1:
                            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:84:17: function
                            {
                                _last = (CtrlTree) this.input.LT(1);
                                pushFollow(FOLLOW_function_in_functions233);
                                function18 = function();

                                this.state._fsp--;

                                if (_first_1 == null) {
                                    _first_1 = function18.tree;
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
                                break loop4;
                            }
                        } while (true);

                        match(this.input, Token.UP, null);
                    }
                    _last = _save_last_1;
                }

                this.helper.reorderFunctions(FUNCTIONS17);

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
        } finally {
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
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:88:1: function : ^( FUNCTION ID block ) ;
    public final CtrlChecker.function_return function()
        throws RecognitionException {
        CtrlChecker.function_return retval = new CtrlChecker.function_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree FUNCTION19 = null;
        CtrlTree ID20 = null;
        CtrlChecker.block_return block21 = null;

        CtrlTree FUNCTION19_tree = null;
        CtrlTree ID20_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:89:3: ( ^( FUNCTION ID block ) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:89:5: ^( FUNCTION ID block )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    FUNCTION19 =
                        (CtrlTree) match(this.input, FUNCTION,
                            FOLLOW_FUNCTION_in_function256);

                    if (_first_0 == null) {
                        _first_0 = FUNCTION19;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    ID20 =
                        (CtrlTree) match(this.input, ID,
                            FOLLOW_ID_in_function258);

                    if (_first_1 == null) {
                        _first_1 = ID20;
                    }
                    this.helper.startBody(ID20, Kind.FUNCTION);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_block_in_function277);
                    block21 = block();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = block21.tree;
                    }
                    this.helper.endBody();

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
        } finally {
        }
        return retval;
    }

    // $ANTLR end "function"

    public static class block_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        CtrlTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "block"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:96:1: block returns [ CtrlAut aut ] : ^( BLOCK ( stat )* ) ;
    public final CtrlChecker.block_return block() throws RecognitionException {
        CtrlChecker.block_return retval = new CtrlChecker.block_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree BLOCK22 = null;
        CtrlChecker.stat_return stat23 = null;

        CtrlTree BLOCK22_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:97:3: ( ^( BLOCK ( stat )* ) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:97:5: ^( BLOCK ( stat )* )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    BLOCK22 =
                        (CtrlTree) match(this.input, BLOCK,
                            FOLLOW_BLOCK_in_block315);

                    if (_first_0 == null) {
                        _first_0 = BLOCK22;
                    }
                    this.helper.openScope();

                    if (this.input.LA(1) == Token.DOWN) {
                        match(this.input, Token.DOWN, null);
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:99:8: ( stat )*
                        loop5: do {
                            int alt5 = 2;
                            int LA5_0 = this.input.LA(1);

                            if (((LA5_0 >= BLOCK && LA5_0 <= CALL)
                                || LA5_0 == VAR
                                || (LA5_0 >= ALAP && LA5_0 <= UNTIL)
                                || LA5_0 == IF
                                || (LA5_0 >= TRY && LA5_0 <= CHOICE)
                                || LA5_0 == TRUE
                                || (LA5_0 >= ANY && LA5_0 <= OTHER) || LA5_0 == STAR)) {
                                alt5 = 1;
                            }

                            switch (alt5) {
                            case 1:
                            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:99:8: stat
                            {
                                _last = (CtrlTree) this.input.LT(1);
                                pushFollow(FOLLOW_stat_in_block333);
                                stat23 = stat();

                                this.state._fsp--;

                                if (_first_1 == null) {
                                    _first_1 = stat23.tree;
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
                                break loop5;
                            }
                        } while (true);

                        this.helper.closeScope();

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
        } finally {
        }
        return retval;
    }

    // $ANTLR end "block"

    public static class stat_return extends TreeRuleReturnScope {
        CtrlTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "stat"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:104:1: stat : ( block | var_decl | ^( ALAP stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE );
    public final CtrlChecker.stat_return stat() throws RecognitionException {
        CtrlChecker.stat_return retval = new CtrlChecker.stat_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree ALAP26 = null;
        CtrlTree WHILE28 = null;
        CtrlTree UNTIL31 = null;
        CtrlTree TRY34 = null;
        CtrlTree IF37 = null;
        CtrlTree CHOICE41 = null;
        CtrlTree STAR44 = null;
        CtrlTree ANY47 = null;
        CtrlTree OTHER48 = null;
        CtrlTree TRUE49 = null;
        CtrlChecker.block_return block24 = null;

        CtrlChecker.var_decl_return var_decl25 = null;

        CtrlChecker.stat_return stat27 = null;

        CtrlChecker.stat_return stat29 = null;

        CtrlChecker.stat_return stat30 = null;

        CtrlChecker.stat_return stat32 = null;

        CtrlChecker.stat_return stat33 = null;

        CtrlChecker.stat_return stat35 = null;

        CtrlChecker.stat_return stat36 = null;

        CtrlChecker.stat_return stat38 = null;

        CtrlChecker.stat_return stat39 = null;

        CtrlChecker.stat_return stat40 = null;

        CtrlChecker.stat_return stat42 = null;

        CtrlChecker.stat_return stat43 = null;

        CtrlChecker.stat_return stat45 = null;

        CtrlChecker.rule_return rule46 = null;

        CtrlTree ALAP26_tree = null;
        CtrlTree WHILE28_tree = null;
        CtrlTree UNTIL31_tree = null;
        CtrlTree TRY34_tree = null;
        CtrlTree IF37_tree = null;
        CtrlTree CHOICE41_tree = null;
        CtrlTree STAR44_tree = null;
        CtrlTree ANY47_tree = null;
        CtrlTree OTHER48_tree = null;
        CtrlTree TRUE49_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:105:3: ( block | var_decl | ^( ALAP stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE )
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:105:5: block
            {
                _last = (CtrlTree) this.input.LT(1);
                pushFollow(FOLLOW_block_in_stat363);
                block24 = block();

                this.state._fsp--;

                if (_first_0 == null) {
                    _first_0 = block24.tree;
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:106:5: var_decl
            {
                _last = (CtrlTree) this.input.LT(1);
                pushFollow(FOLLOW_var_decl_in_stat369);
                var_decl25 = var_decl();

                this.state._fsp--;

                if (_first_0 == null) {
                    _first_0 = var_decl25.tree;
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
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:107:5: ^( ALAP stat )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    ALAP26 =
                        (CtrlTree) match(this.input, ALAP,
                            FOLLOW_ALAP_in_stat376);

                    if (_first_0 == null) {
                        _first_0 = ALAP26;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat378);
                    stat27 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat27.tree;
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
                break;
            case 4:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:108:5: ^( WHILE stat stat )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    WHILE28 =
                        (CtrlTree) match(this.input, WHILE,
                            FOLLOW_WHILE_in_stat387);

                    if (_first_0 == null) {
                        _first_0 = WHILE28;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat396);
                    stat29 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat29.tree;
                    }
                    this.helper.startBranch();
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat414);
                    stat30 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat30.tree;
                    }
                    this.helper.endBranch();

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
            case 5:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:114:5: ^( UNTIL stat stat )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    UNTIL31 =
                        (CtrlTree) match(this.input, UNTIL,
                            FOLLOW_UNTIL_in_stat438);

                    if (_first_0 == null) {
                        _first_0 = UNTIL31;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat447);
                    stat32 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat32.tree;
                    }
                    this.helper.startBranch();
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat465);
                    stat33 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat33.tree;
                    }
                    this.helper.endBranch();

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
            case 6:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:120:5: ^( TRY stat ( stat )? )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    TRY34 =
                        (CtrlTree) match(this.input, TRY, FOLLOW_TRY_in_stat489);

                    if (_first_0 == null) {
                        _first_0 = TRY34;
                    }
                    this.helper.startBranch();

                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat507);
                    stat35 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat35.tree;
                    }
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:123:8: ( stat )?
                    int alt6 = 2;
                    int LA6_0 = this.input.LA(1);

                    if (((LA6_0 >= BLOCK && LA6_0 <= CALL) || LA6_0 == VAR
                        || (LA6_0 >= ALAP && LA6_0 <= UNTIL) || LA6_0 == IF
                        || (LA6_0 >= TRY && LA6_0 <= CHOICE) || LA6_0 == TRUE
                        || (LA6_0 >= ANY && LA6_0 <= OTHER) || LA6_0 == STAR)) {
                        alt6 = 1;
                    }
                    switch (alt6) {
                    case 1:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:123:10: stat
                    {
                        this.helper.nextBranch();
                        _last = (CtrlTree) this.input.LT(1);
                        pushFollow(FOLLOW_stat_in_stat529);
                        stat36 = stat();

                        this.state._fsp--;

                        if (_first_1 == null) {
                            _first_1 = stat36.tree;
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

                    this.helper.endBranch();

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
            case 7:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:128:5: ^( IF stat stat ( stat )? )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    IF37 =
                        (CtrlTree) match(this.input, IF, FOLLOW_IF_in_stat563);

                    if (_first_0 == null) {
                        _first_0 = IF37;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat573);
                    stat38 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat38.tree;
                    }
                    this.helper.startBranch();
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat592);
                    stat39 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat39.tree;
                    }
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:132:8: ( stat )?
                    int alt7 = 2;
                    int LA7_0 = this.input.LA(1);

                    if (((LA7_0 >= BLOCK && LA7_0 <= CALL) || LA7_0 == VAR
                        || (LA7_0 >= ALAP && LA7_0 <= UNTIL) || LA7_0 == IF
                        || (LA7_0 >= TRY && LA7_0 <= CHOICE) || LA7_0 == TRUE
                        || (LA7_0 >= ANY && LA7_0 <= OTHER) || LA7_0 == STAR)) {
                        alt7 = 1;
                    }
                    switch (alt7) {
                    case 1:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:132:10: stat
                    {
                        this.helper.nextBranch();
                        _last = (CtrlTree) this.input.LT(1);
                        pushFollow(FOLLOW_stat_in_stat615);
                        stat40 = stat();

                        this.state._fsp--;

                        if (_first_1 == null) {
                            _first_1 = stat40.tree;
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

                    this.helper.endBranch();

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
            case 8:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:137:5: ^( CHOICE stat ( stat )* )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    CHOICE41 =
                        (CtrlTree) match(this.input, CHOICE,
                            FOLLOW_CHOICE_in_stat649);

                    if (_first_0 == null) {
                        _first_0 = CHOICE41;
                    }
                    this.helper.startBranch();

                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat667);
                    stat42 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat42.tree;
                    }
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:140:8: ( stat )*
                    loop8: do {
                        int alt8 = 2;
                        int LA8_0 = this.input.LA(1);

                        if (((LA8_0 >= BLOCK && LA8_0 <= CALL) || LA8_0 == VAR
                            || (LA8_0 >= ALAP && LA8_0 <= UNTIL) || LA8_0 == IF
                            || (LA8_0 >= TRY && LA8_0 <= CHOICE)
                            || LA8_0 == TRUE
                            || (LA8_0 >= ANY && LA8_0 <= OTHER) || LA8_0 == STAR)) {
                            alt8 = 1;
                        }

                        switch (alt8) {
                        case 1:
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:140:10: stat
                        {
                            this.helper.nextBranch();
                            _last = (CtrlTree) this.input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat690);
                            stat43 = stat();

                            this.state._fsp--;

                            if (_first_1 == null) {
                                _first_1 = stat43.tree;
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
                            break loop8;
                        }
                    } while (true);

                    match(this.input, Token.UP, null);
                    _last = _save_last_1;
                }

                this.helper.endBranch();

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 9:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:145:5: ^( STAR stat )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    STAR44 =
                        (CtrlTree) match(this.input, STAR,
                            FOLLOW_STAR_in_stat726);

                    if (_first_0 == null) {
                        _first_0 = STAR44;
                    }
                    this.helper.startBranch();

                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat744);
                    stat45 = stat();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = stat45.tree;
                    }
                    this.helper.endBranch();

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
            case 10:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:150:5: rule
            {
                _last = (CtrlTree) this.input.LT(1);
                pushFollow(FOLLOW_rule_in_stat766);
                rule46 = rule();

                this.state._fsp--;

                if (_first_0 == null) {
                    _first_0 = rule46.tree;
                }

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 11:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:151:5: ANY
            {
                _last = (CtrlTree) this.input.LT(1);
                ANY47 =
                    (CtrlTree) match(this.input, ANY, FOLLOW_ANY_in_stat772);

                if (_first_0 == null) {
                    _first_0 = ANY47;
                }
                this.helper.checkAny(ANY47);

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 12:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:153:5: OTHER
            {
                _last = (CtrlTree) this.input.LT(1);
                OTHER48 =
                    (CtrlTree) match(this.input, OTHER, FOLLOW_OTHER_in_stat784);

                if (_first_0 == null) {
                    _first_0 = OTHER48;
                }
                this.helper.checkOther(OTHER48);

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 13:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:155:5: TRUE
            {
                _last = (CtrlTree) this.input.LT(1);
                TRUE49 =
                    (CtrlTree) match(this.input, TRUE, FOLLOW_TRUE_in_stat796);

                if (_first_0 == null) {
                    _first_0 = TRUE49;
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
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        } finally {
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
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:158:1: rule : ^( CALL id= ID ( ^( ARGS ( arg )* ) )? ) ;
    public final CtrlChecker.rule_return rule() throws RecognitionException {
        CtrlChecker.rule_return retval = new CtrlChecker.rule_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree id = null;
        CtrlTree CALL50 = null;
        CtrlTree ARGS51 = null;
        CtrlChecker.arg_return arg52 = null;

        CtrlTree id_tree = null;
        CtrlTree CALL50_tree = null;
        CtrlTree ARGS51_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:160:3: ( ^( CALL id= ID ( ^( ARGS ( arg )* ) )? ) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:160:5: ^( CALL id= ID ( ^( ARGS ( arg )* ) )? )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    CALL50 =
                        (CtrlTree) match(this.input, CALL,
                            FOLLOW_CALL_in_rule814);

                    if (_first_0 == null) {
                        _first_0 = CALL50;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    id = (CtrlTree) match(this.input, ID, FOLLOW_ID_in_rule818);

                    if (_first_1 == null) {
                        _first_1 = id;
                    }
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:160:18: ( ^( ARGS ( arg )* ) )?
                    int alt11 = 2;
                    int LA11_0 = this.input.LA(1);

                    if ((LA11_0 == ARGS)) {
                        alt11 = 1;
                    }
                    switch (alt11) {
                    case 1:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:160:19: ^( ARGS ( arg )* )
                    {
                        _last = (CtrlTree) this.input.LT(1);
                        {
                            CtrlTree _save_last_2 = _last;
                            CtrlTree _first_2 = null;
                            _last = (CtrlTree) this.input.LT(1);
                            ARGS51 =
                                (CtrlTree) match(this.input, ARGS,
                                    FOLLOW_ARGS_in_rule822);

                            if (_first_1 == null) {
                                _first_1 = ARGS51;
                            }
                            if (this.input.LA(1) == Token.DOWN) {
                                match(this.input, Token.DOWN, null);
                                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:160:26: ( arg )*
                                loop10: do {
                                    int alt10 = 2;
                                    int LA10_0 = this.input.LA(1);

                                    if ((LA10_0 == ARG)) {
                                        alt10 = 1;
                                    }

                                    switch (alt10) {
                                    case 1:
                                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:160:26: arg
                                    {
                                        _last = (CtrlTree) this.input.LT(1);
                                        pushFollow(FOLLOW_arg_in_rule824);
                                        arg52 = arg();

                                        this.state._fsp--;

                                        if (_first_2 == null) {
                                            _first_2 = arg52.tree;
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

            this.helper.checkCall(((CtrlTree) retval.tree));
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
        } finally {
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
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:163:1: var_decl : ^( VAR type ( ID )+ ) ;
    public final CtrlChecker.var_decl_return var_decl()
        throws RecognitionException {
        CtrlChecker.var_decl_return retval = new CtrlChecker.var_decl_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree VAR53 = null;
        CtrlTree ID55 = null;
        CtrlChecker.type_return type54 = null;

        CtrlTree VAR53_tree = null;
        CtrlTree ID55_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:164:2: ( ^( VAR type ( ID )+ ) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:164:4: ^( VAR type ( ID )+ )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    VAR53 =
                        (CtrlTree) match(this.input, VAR,
                            FOLLOW_VAR_in_var_decl843);

                    if (_first_0 == null) {
                        _first_0 = VAR53;
                    }
                    match(this.input, Token.DOWN, null);
                    _last = (CtrlTree) this.input.LT(1);
                    pushFollow(FOLLOW_type_in_var_decl845);
                    type54 = type();

                    this.state._fsp--;

                    if (_first_1 == null) {
                        _first_1 = type54.tree;
                    }
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:166:7: ( ID )+
                    int cnt12 = 0;
                    loop12: do {
                        int alt12 = 2;
                        int LA12_0 = this.input.LA(1);

                        if ((LA12_0 == ID)) {
                            alt12 = 1;
                        }

                        switch (alt12) {
                        case 1:
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:166:9: ID
                        {
                            _last = (CtrlTree) this.input.LT(1);
                            ID55 =
                                (CtrlTree) match(this.input, ID,
                                    FOLLOW_ID_in_var_decl862);

                            if (_first_1 == null) {
                                _first_1 = ID55;
                            }
                            this.helper.declareVar(ID55, (type54 != null
                                    ? ((CtrlTree) type54.tree) : null));

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
        } finally {
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
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:172:1: type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlChecker.type_return type() throws RecognitionException {
        CtrlChecker.type_return retval = new CtrlChecker.type_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree NODE56 = null;
        CtrlTree BOOL57 = null;
        CtrlTree STRING58 = null;
        CtrlTree INT59 = null;
        CtrlTree REAL60 = null;

        CtrlTree NODE56_tree = null;
        CtrlTree BOOL57_tree = null;
        CtrlTree STRING58_tree = null;
        CtrlTree INT59_tree = null;
        CtrlTree REAL60_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:173:3: ( NODE | BOOL | STRING | INT | REAL )
            int alt13 = 5;
            switch (this.input.LA(1)) {
            case NODE: {
                alt13 = 1;
            }
                break;
            case BOOL: {
                alt13 = 2;
            }
                break;
            case STRING: {
                alt13 = 3;
            }
                break;
            case INT: {
                alt13 = 4;
            }
                break;
            case REAL: {
                alt13 = 5;
            }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, this.input);

                throw nvae;
            }

            switch (alt13) {
            case 1:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:173:5: NODE
            {
                _last = (CtrlTree) this.input.LT(1);
                NODE56 =
                    (CtrlTree) match(this.input, NODE, FOLLOW_NODE_in_type901);

                if (_first_0 == null) {
                    _first_0 = NODE56;
                }
                this.helper.checkType(NODE56);

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 2:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:174:5: BOOL
            {
                _last = (CtrlTree) this.input.LT(1);
                BOOL57 =
                    (CtrlTree) match(this.input, BOOL, FOLLOW_BOOL_in_type911);

                if (_first_0 == null) {
                    _first_0 = BOOL57;
                }
                this.helper.checkType(BOOL57);

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 3:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:175:5: STRING
            {
                _last = (CtrlTree) this.input.LT(1);
                STRING58 =
                    (CtrlTree) match(this.input, STRING,
                        FOLLOW_STRING_in_type921);

                if (_first_0 == null) {
                    _first_0 = STRING58;
                }
                this.helper.checkType(STRING58);

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 4:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:176:5: INT
            {
                _last = (CtrlTree) this.input.LT(1);
                INT59 =
                    (CtrlTree) match(this.input, INT, FOLLOW_INT_in_type929);

                if (_first_0 == null) {
                    _first_0 = INT59;
                }
                this.helper.checkType(INT59);

                retval.tree = (CtrlTree) _first_0;
                if (this.adaptor.getParent(retval.tree) != null
                    && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                    retval.tree =
                        (CtrlTree) this.adaptor.getParent(retval.tree);
                }
            }
                break;
            case 5:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:177:5: REAL
            {
                _last = (CtrlTree) this.input.LT(1);
                REAL60 =
                    (CtrlTree) match(this.input, REAL, FOLLOW_REAL_in_type940);

                if (_first_0 == null) {
                    _first_0 = REAL60;
                }
                this.helper.checkType(REAL60);

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
        } finally {
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
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:180:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final CtrlChecker.arg_return arg() throws RecognitionException {
        CtrlChecker.arg_return retval = new CtrlChecker.arg_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree ARG61 = null;
        CtrlTree OUT62 = null;
        CtrlTree ID63 = null;
        CtrlTree DONT_CARE64 = null;
        CtrlChecker.literal_return literal65 = null;

        CtrlTree ARG61_tree = null;
        CtrlTree OUT62_tree = null;
        CtrlTree ID63_tree = null;
        CtrlTree DONT_CARE64_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:181:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:181:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
                _last = (CtrlTree) this.input.LT(1);
                {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree) this.input.LT(1);
                    ARG61 =
                        (CtrlTree) match(this.input, ARG, FOLLOW_ARG_in_arg960);

                    if (_first_0 == null) {
                        _first_0 = ARG61;
                    }
                    match(this.input, Token.DOWN, null);
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:182:7: ( ( OUT )? ID | DONT_CARE | literal )
                    int alt15 = 3;
                    switch (this.input.LA(1)) {
                    case ID:
                    case OUT: {
                        alt15 = 1;
                    }
                        break;
                    case DONT_CARE: {
                        alt15 = 2;
                    }
                        break;
                    case TRUE:
                    case FALSE:
                    case STRING_LIT:
                    case INT_LIT:
                    case REAL_LIT: {
                        alt15 = 3;
                    }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 15, 0, this.input);

                        throw nvae;
                    }

                    switch (alt15) {
                    case 1:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:182:9: ( OUT )? ID
                    {
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:182:9: ( OUT )?
                        int alt14 = 2;
                        int LA14_0 = this.input.LA(1);

                        if ((LA14_0 == OUT)) {
                            alt14 = 1;
                        }
                        switch (alt14) {
                        case 1:
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:182:9: OUT
                        {
                            _last = (CtrlTree) this.input.LT(1);
                            OUT62 =
                                (CtrlTree) match(this.input, OUT,
                                    FOLLOW_OUT_in_arg971);

                            if (_first_1 == null) {
                                _first_1 = OUT62;
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
                        ID63 =
                            (CtrlTree) match(this.input, ID,
                                FOLLOW_ID_in_arg974);

                        if (_first_1 == null) {
                            _first_1 = ID63;
                        }
                        this.helper.checkVarArg(ARG61);

                        retval.tree = (CtrlTree) _first_0;
                        if (this.adaptor.getParent(retval.tree) != null
                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                            retval.tree =
                                (CtrlTree) this.adaptor.getParent(retval.tree);
                        }
                    }
                        break;
                    case 2:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:183:9: DONT_CARE
                    {
                        _last = (CtrlTree) this.input.LT(1);
                        DONT_CARE64 =
                            (CtrlTree) match(this.input, DONT_CARE,
                                FOLLOW_DONT_CARE_in_arg986);

                        if (_first_1 == null) {
                            _first_1 = DONT_CARE64;
                        }
                        this.helper.checkDontCareArg(ARG61);

                        retval.tree = (CtrlTree) _first_0;
                        if (this.adaptor.getParent(retval.tree) != null
                            && this.adaptor.isNil(this.adaptor.getParent(retval.tree))) {
                            retval.tree =
                                (CtrlTree) this.adaptor.getParent(retval.tree);
                        }
                    }
                        break;
                    case 3:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:184:9: literal
                    {
                        _last = (CtrlTree) this.input.LT(1);
                        pushFollow(FOLLOW_literal_in_arg998);
                        literal65 = literal();

                        this.state._fsp--;

                        if (_first_1 == null) {
                            _first_1 = literal65.tree;
                        }
                        this.helper.checkConstArg(ARG61);

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
        } finally {
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
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:189:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlChecker.literal_return literal()
        throws RecognitionException {
        CtrlChecker.literal_return retval = new CtrlChecker.literal_return();
        retval.start = this.input.LT(1);

        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree set66 = null;

        CtrlTree set66_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:190:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:
            {
                _last = (CtrlTree) this.input.LT(1);
                set66 = (CtrlTree) this.input.LT(1);
                if (this.input.LA(1) == TRUE
                    || (this.input.LA(1) >= FALSE && this.input.LA(1) <= REAL_LIT)) {
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
        } finally {
        }
        return retval;
    }

    // $ANTLR end "literal"

    // Delegated rules

    public static final BitSet FOLLOW_PROGRAM_in_program56 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_package_decl_in_program58 = new BitSet(
        new long[] {0x0000000000010800L});
    public static final BitSet FOLLOW_import_decl_in_program60 = new BitSet(
        new long[] {0x0000000000010800L});
    public static final BitSet FOLLOW_functions_in_program63 = new BitSet(
        new long[] {0x0000000000000010L});
    public static final BitSet FOLLOW_recipes_in_program65 = new BitSet(
        new long[] {0x0000000000000080L});
    public static final BitSet FOLLOW_block_in_program67 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_PACKAGE_in_package_decl90 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_package_decl92 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_IMPORT_in_import_decl125 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_import_decl127 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_RECIPES_in_recipes157 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_recipe_in_recipes159 = new BitSet(
        new long[] {0x0000000000080008L});
    public static final BitSet FOLLOW_RECIPE_in_recipe176 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_recipe178 = new BitSet(
        new long[] {0x0000400000000080L});
    public static final BitSet FOLLOW_INT_LIT_in_recipe180 = new BitSet(
        new long[] {0x0000000000000080L});
    public static final BitSet FOLLOW_block_in_recipe200 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions231 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions233 = new BitSet(
        new long[] {0x0000000000400008L});
    public static final BitSet FOLLOW_FUNCTION_in_function256 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function258 = new BitSet(
        new long[] {0x0000000000000080L});
    public static final BitSet FOLLOW_block_in_function277 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block315 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block333 = new BitSet(
        new long[] {0x00400189AE002188L});
    public static final BitSet FOLLOW_block_in_stat363 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat369 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat376 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat378 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat387 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat396 = new BitSet(
        new long[] {0x00400189AE002188L});
    public static final BitSet FOLLOW_stat_in_stat414 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat438 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat447 = new BitSet(
        new long[] {0x00400189AE002188L});
    public static final BitSet FOLLOW_stat_in_stat465 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat489 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat507 = new BitSet(
        new long[] {0x00400189AE002188L});
    public static final BitSet FOLLOW_stat_in_stat529 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat563 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat573 = new BitSet(
        new long[] {0x00400189AE002188L});
    public static final BitSet FOLLOW_stat_in_stat592 = new BitSet(
        new long[] {0x00400189AE002188L});
    public static final BitSet FOLLOW_stat_in_stat615 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat649 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat667 = new BitSet(
        new long[] {0x00400189AE002188L});
    public static final BitSet FOLLOW_stat_in_stat690 = new BitSet(
        new long[] {0x00400189AE002188L});
    public static final BitSet FOLLOW_STAR_in_stat726 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat744 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat766 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat772 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat784 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat796 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule814 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule818 = new BitSet(
        new long[] {0x0000000000000048L});
    public static final BitSet FOLLOW_ARGS_in_rule822 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_arg_in_rule824 = new BitSet(
        new long[] {0x0000000000000028L});
    public static final BitSet FOLLOW_VAR_in_var_decl843 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl845 = new BitSet(
        new long[] {0x0000000000020000L});
    public static final BitSet FOLLOW_ID_in_var_decl862 = new BitSet(
        new long[] {0x0000000000020008L});
    public static final BitSet FOLLOW_NODE_in_type901 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_type911 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_type921 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_type929 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_REAL_in_type940 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_arg960 = new BitSet(
        new long[] {0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg971 = new BitSet(
        new long[] {0x0000000000020000L});
    public static final BitSet FOLLOW_ID_in_arg974 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg986 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg998 = new BitSet(
        new long[] {0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_literal0 = new BitSet(
        new long[] {0x0000000000000002L});

}