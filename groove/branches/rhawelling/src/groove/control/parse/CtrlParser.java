// $ANTLR 3.3 Nov 30, 2010 12:45:30 D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g 2013-05-07 17:43:36

package groove.control.parse;

import groove.algebra.AlgebraFamily;
import groove.grammar.model.FormatErrorSet;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.RewriteEarlyExitException;
import org.antlr.runtime.tree.RewriteRuleSubtreeStream;
import org.antlr.runtime.tree.RewriteRuleTokenStream;
import org.antlr.runtime.tree.TreeAdaptor;

@SuppressWarnings("all")
public class CtrlParser extends Parser {
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

    public CtrlParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }

    public CtrlParser(TokenStream input, RecognizerSharedState state) {
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
        return CtrlParser.tokenNames;
    }

    public String getGrammarFileName() {
        return "D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g";
    }

    /** Lexer for the GCL language. */
    private static CtrlLexer lexer = new CtrlLexer(null);
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
    public CtrlTree run(CharStream input, Namespace namespace,
            AlgebraFamily family) throws RecognitionException {
        this.helper = new CtrlHelper(this, namespace, family);
        lexer.setCharStream(input);
        lexer.setHelper(this.helper);
        setTokenStream(new CommonTokenStream(lexer));
        setTreeAdaptor(new CtrlTreeAdaptor());
        return (CtrlTree) program().getTree();
    }

    public static class program_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "program"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:104:1: program : package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM ( package_decl )? ( import_decl )* ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) ) ;
    public final CtrlParser.program_return program()
        throws RecognitionException {
        CtrlParser.program_return retval = new CtrlParser.program_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token EOF6 = null;
        CtrlParser.package_decl_return package_decl1 = null;

        CtrlParser.import_decl_return import_decl2 = null;

        CtrlParser.function_return function3 = null;

        CtrlParser.recipe_return recipe4 = null;

        CtrlParser.stat_return stat5 = null;

        CommonTree EOF6_tree = null;
        RewriteRuleTokenStream stream_EOF =
            new RewriteRuleTokenStream(this.adaptor, "token EOF");
        RewriteRuleSubtreeStream stream_package_decl =
            new RewriteRuleSubtreeStream(this.adaptor, "rule package_decl");
        RewriteRuleSubtreeStream stream_recipe =
            new RewriteRuleSubtreeStream(this.adaptor, "rule recipe");
        RewriteRuleSubtreeStream stream_import_decl =
            new RewriteRuleSubtreeStream(this.adaptor, "rule import_decl");
        RewriteRuleSubtreeStream stream_stat =
            new RewriteRuleSubtreeStream(this.adaptor, "rule stat");
        RewriteRuleSubtreeStream stream_function =
            new RewriteRuleSubtreeStream(this.adaptor, "rule function");
        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:106:3: ( package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM ( package_decl )? ( import_decl )* ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) ) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:110:5: package_decl ( import_decl )* ( function | recipe | stat )* EOF
            {
                pushFollow(FOLLOW_package_decl_in_program141);
                package_decl1 = package_decl();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_package_decl.add(package_decl1.getTree());
                }
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:111:5: ( import_decl )*
                loop1: do {
                    int alt1 = 2;
                    alt1 = this.dfa1.predict(this.input);
                    switch (alt1) {
                    case 1:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:111:5: import_decl
                    {
                        pushFollow(FOLLOW_import_decl_in_program147);
                        import_decl2 = import_decl();

                        this.state._fsp--;
                        if (this.state.failed) {
                            return retval;
                        }
                        if (this.state.backtracking == 0) {
                            stream_import_decl.add(import_decl2.getTree());
                        }

                    }
                        break;

                    default:
                        break loop1;
                    }
                } while (true);

                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:112:5: ( function | recipe | stat )*
                loop2: do {
                    int alt2 = 4;
                    alt2 = this.dfa2.predict(this.input);
                    switch (alt2) {
                    case 1:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:112:6: function
                    {
                        pushFollow(FOLLOW_function_in_program155);
                        function3 = function();

                        this.state._fsp--;
                        if (this.state.failed) {
                            return retval;
                        }
                        if (this.state.backtracking == 0) {
                            stream_function.add(function3.getTree());
                        }

                    }
                        break;
                    case 2:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:112:15: recipe
                    {
                        pushFollow(FOLLOW_recipe_in_program157);
                        recipe4 = recipe();

                        this.state._fsp--;
                        if (this.state.failed) {
                            return retval;
                        }
                        if (this.state.backtracking == 0) {
                            stream_recipe.add(recipe4.getTree());
                        }

                    }
                        break;
                    case 3:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:112:22: stat
                    {
                        pushFollow(FOLLOW_stat_in_program159);
                        stat5 = stat();

                        this.state._fsp--;
                        if (this.state.failed) {
                            return retval;
                        }
                        if (this.state.backtracking == 0) {
                            stream_stat.add(stat5.getTree());
                        }

                    }
                        break;

                    default:
                        break loop2;
                    }
                } while (true);

                EOF6 = (Token) match(this.input, EOF, FOLLOW_EOF_in_program163);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_EOF.add(EOF6);
                }

                if (this.state.backtracking == 0) {
                    this.helper.checkEOF(EOF6_tree);
                }

                // AST REWRITE
                // elements: function, stat, recipe, import_decl, package_decl
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 114:5: -> ^( PROGRAM ( package_decl )? ( import_decl )* ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) )
                    {
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:114:8: ^( PROGRAM ( package_decl )? ( import_decl )* ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(PROGRAM,
                                        "PROGRAM"), root_1);

                            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:115:11: ( package_decl )?
                            if (stream_package_decl.hasNext()) {
                                this.adaptor.addChild(root_1,
                                    stream_package_decl.nextTree());

                            }
                            stream_package_decl.reset();
                            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:116:11: ( import_decl )*
                            while (stream_import_decl.hasNext()) {
                                this.adaptor.addChild(root_1,
                                    stream_import_decl.nextTree());

                            }
                            stream_import_decl.reset();
                            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:117:11: ^( FUNCTIONS ( function )* )
                            {
                                CommonTree root_2 =
                                    (CommonTree) this.adaptor.nil();
                                root_2 =
                                    (CommonTree) this.adaptor.becomeRoot(
                                        (CommonTree) this.adaptor.create(
                                            FUNCTIONS, "FUNCTIONS"), root_2);

                                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:117:23: ( function )*
                                while (stream_function.hasNext()) {
                                    this.adaptor.addChild(root_2,
                                        stream_function.nextTree());

                                }
                                stream_function.reset();

                                this.adaptor.addChild(root_1, root_2);
                            }
                            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:118:11: ^( RECIPES ( recipe )* )
                            {
                                CommonTree root_2 =
                                    (CommonTree) this.adaptor.nil();
                                root_2 =
                                    (CommonTree) this.adaptor.becomeRoot(
                                        (CommonTree) this.adaptor.create(
                                            RECIPES, "RECIPES"), root_2);

                                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:118:21: ( recipe )*
                                while (stream_recipe.hasNext()) {
                                    this.adaptor.addChild(root_2,
                                        stream_recipe.nextTree());

                                }
                                stream_recipe.reset();

                                this.adaptor.addChild(root_1, root_2);
                            }
                            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:119:11: ^( BLOCK ( stat )* )
                            {
                                CommonTree root_2 =
                                    (CommonTree) this.adaptor.nil();
                                root_2 =
                                    (CommonTree) this.adaptor.becomeRoot(
                                        (CommonTree) this.adaptor.create(BLOCK,
                                            "BLOCK"), root_2);

                                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:119:19: ( stat )*
                                while (stream_stat.hasNext()) {
                                    this.adaptor.addChild(root_2,
                                        stream_stat.nextTree());

                                }
                                stream_stat.reset();

                                this.adaptor.addChild(root_1, root_2);
                            }

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "program"

    public static class package_decl_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "package_decl"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:123:1: package_decl : ( PACKAGE qual_name SEMI -> ^( PACKAGE qual_name ) | ->) ;
    public final CtrlParser.package_decl_return package_decl()
        throws RecognitionException {
        CtrlParser.package_decl_return retval =
            new CtrlParser.package_decl_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token PACKAGE7 = null;
        Token SEMI9 = null;
        CtrlParser.qual_name_return qual_name8 = null;

        CommonTree PACKAGE7_tree = null;
        CommonTree SEMI9_tree = null;
        RewriteRuleTokenStream stream_PACKAGE =
            new RewriteRuleTokenStream(this.adaptor, "token PACKAGE");
        RewriteRuleTokenStream stream_SEMI =
            new RewriteRuleTokenStream(this.adaptor, "token SEMI");
        RewriteRuleSubtreeStream stream_qual_name =
            new RewriteRuleSubtreeStream(this.adaptor, "rule qual_name");
        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:125:3: ( ( PACKAGE qual_name SEMI -> ^( PACKAGE qual_name ) | ->) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:127:5: ( PACKAGE qual_name SEMI -> ^( PACKAGE qual_name ) | ->)
            {
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:127:5: ( PACKAGE qual_name SEMI -> ^( PACKAGE qual_name ) | ->)
                int alt3 = 2;
                alt3 = this.dfa3.predict(this.input);
                switch (alt3) {
                case 1:
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:127:7: PACKAGE qual_name SEMI
                {
                    PACKAGE7 =
                        (Token) match(this.input, PACKAGE,
                            FOLLOW_PACKAGE_in_package_decl295);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_PACKAGE.add(PACKAGE7);
                    }

                    pushFollow(FOLLOW_qual_name_in_package_decl297);
                    qual_name8 = qual_name();

                    this.state._fsp--;
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_qual_name.add(qual_name8.getTree());
                    }
                    SEMI9 =
                        (Token) match(this.input, SEMI,
                            FOLLOW_SEMI_in_package_decl299);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_SEMI.add(SEMI9);
                    }

                    if (this.state.backtracking == 0) {
                        this.helper.setPackage((qual_name8 != null
                                ? ((CommonTree) qual_name8.tree) : null));
                    }

                    // AST REWRITE
                    // elements: qual_name, PACKAGE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if (this.state.backtracking == 0) {
                        retval.tree = root_0;
                        RewriteRuleSubtreeStream stream_retval =
                            new RewriteRuleSubtreeStream(this.adaptor,
                                "rule retval", retval != null ? retval.tree
                                        : null);

                        root_0 = (CommonTree) this.adaptor.nil();
                        // 129:7: -> ^( PACKAGE qual_name )
                        {
                            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:129:10: ^( PACKAGE qual_name )
                            {
                                CommonTree root_1 =
                                    (CommonTree) this.adaptor.nil();
                                root_1 =
                                    (CommonTree) this.adaptor.becomeRoot(
                                        stream_PACKAGE.nextNode(), root_1);

                                this.adaptor.addChild(root_1,
                                    stream_qual_name.nextTree());

                                this.adaptor.addChild(root_0, root_1);
                            }

                        }

                        retval.tree = root_0;
                    }
                }
                    break;
                case 2:
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:130:7: 
                {

                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if (this.state.backtracking == 0) {
                        retval.tree = root_0;
                        RewriteRuleSubtreeStream stream_retval =
                            new RewriteRuleSubtreeStream(this.adaptor,
                                "rule retval", retval != null ? retval.tree
                                        : null);

                        root_0 = (CommonTree) this.adaptor.nil();
                        // 130:7: ->
                        {
                            this.adaptor.addChild(root_0,
                                this.helper.emptyPackage());

                        }

                        retval.tree = root_0;
                    }
                }
                    break;

                }

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "package_decl"

    public static class import_decl_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "import_decl"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:134:1: import_decl : IMPORT qual_name SEMI ;
    public final CtrlParser.import_decl_return import_decl()
        throws RecognitionException {
        CtrlParser.import_decl_return retval =
            new CtrlParser.import_decl_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token IMPORT10 = null;
        Token SEMI12 = null;
        CtrlParser.qual_name_return qual_name11 = null;

        CommonTree IMPORT10_tree = null;
        CommonTree SEMI12_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:136:3: ( IMPORT qual_name SEMI )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:138:5: IMPORT qual_name SEMI
            {
                root_0 = (CommonTree) this.adaptor.nil();

                IMPORT10 =
                    (Token) match(this.input, IMPORT,
                        FOLLOW_IMPORT_in_import_decl363);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    IMPORT10_tree = (CommonTree) this.adaptor.create(IMPORT10);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(IMPORT10_tree,
                            root_0);
                }
                pushFollow(FOLLOW_qual_name_in_import_decl366);
                qual_name11 = qual_name();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, qual_name11.getTree());
                }
                SEMI12 =
                    (Token) match(this.input, SEMI,
                        FOLLOW_SEMI_in_import_decl368);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.helper.addImport((qual_name11 != null
                            ? ((CommonTree) qual_name11.tree) : null));

                }

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "import_decl"

    public static class qual_name_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "qual_name"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:143:1: qual_name : ids+= ID ( DOT ids+= ID )* ->;
    public final CtrlParser.qual_name_return qual_name()
        throws RecognitionException {
        CtrlParser.qual_name_return retval = new CtrlParser.qual_name_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token DOT13 = null;
        Token ids = null;
        List list_ids = null;

        CommonTree DOT13_tree = null;
        CommonTree ids_tree = null;
        RewriteRuleTokenStream stream_ID =
            new RewriteRuleTokenStream(this.adaptor, "token ID");
        RewriteRuleTokenStream stream_DOT =
            new RewriteRuleTokenStream(this.adaptor, "token DOT");

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:145:3: (ids+= ID ( DOT ids+= ID )* ->)
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:145:5: ids+= ID ( DOT ids+= ID )*
            {
                ids = (Token) match(this.input, ID, FOLLOW_ID_in_qual_name392);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_ID.add(ids);
                }

                if (list_ids == null) {
                    list_ids = new ArrayList();
                }
                list_ids.add(ids);

                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:145:13: ( DOT ids+= ID )*
                loop4: do {
                    int alt4 = 2;
                    int LA4_0 = this.input.LA(1);

                    if ((LA4_0 == DOT)) {
                        alt4 = 1;
                    }

                    switch (alt4) {
                    case 1:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:145:14: DOT ids+= ID
                    {
                        DOT13 =
                            (Token) match(this.input, DOT,
                                FOLLOW_DOT_in_qual_name395);
                        if (this.state.failed) {
                            return retval;
                        }
                        if (this.state.backtracking == 0) {
                            stream_DOT.add(DOT13);
                        }

                        ids =
                            (Token) match(this.input, ID,
                                FOLLOW_ID_in_qual_name399);
                        if (this.state.failed) {
                            return retval;
                        }
                        if (this.state.backtracking == 0) {
                            stream_ID.add(ids);
                        }

                        if (list_ids == null) {
                            list_ids = new ArrayList();
                        }
                        list_ids.add(ids);

                    }
                        break;

                    default:
                        break loop4;
                    }
                } while (true);

                // AST REWRITE
                // elements: 
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 146:5: ->
                    {
                        this.adaptor.addChild(root_0,
                            this.helper.toQualName(list_ids));

                    }

                    retval.tree = root_0;
                }
            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "qual_name"

    public static class recipe_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "recipe"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:149:1: recipe : RECIPE ID LPAR RPAR block ;
    public final CtrlParser.recipe_return recipe() throws RecognitionException {
        CtrlParser.recipe_return retval = new CtrlParser.recipe_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token RECIPE14 = null;
        Token ID15 = null;
        Token LPAR16 = null;
        Token RPAR17 = null;
        CtrlParser.block_return block18 = null;

        CommonTree RECIPE14_tree = null;
        CommonTree ID15_tree = null;
        CommonTree LPAR16_tree = null;
        CommonTree RPAR17_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:153:3: ( RECIPE ID LPAR RPAR block )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:155:5: RECIPE ID LPAR RPAR block
            {
                root_0 = (CommonTree) this.adaptor.nil();

                if (this.state.backtracking == 0) {
                    lexer.startRecord();
                }
                RECIPE14 =
                    (Token) match(this.input, RECIPE,
                        FOLLOW_RECIPE_in_recipe440);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    RECIPE14_tree = (CommonTree) this.adaptor.create(RECIPE14);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(RECIPE14_tree,
                            root_0);
                }
                ID15 = (Token) match(this.input, ID, FOLLOW_ID_in_recipe443);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    ID15_tree = (CommonTree) this.adaptor.create(ID15);
                    this.adaptor.addChild(root_0, ID15_tree);
                }
                LPAR16 =
                    (Token) match(this.input, LPAR, FOLLOW_LPAR_in_recipe445);
                if (this.state.failed) {
                    return retval;
                }
                RPAR17 =
                    (Token) match(this.input, RPAR, FOLLOW_RPAR_in_recipe448);
                if (this.state.failed) {
                    return retval;
                }
                pushFollow(FOLLOW_block_in_recipe465);
                block18 = block();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, block18.getTree());
                }
                if (this.state.backtracking == 0) {
                    this.helper.declareName(RECIPE14_tree, lexer.getRecord());
                }

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "recipe"

    public static class function_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "function"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:163:1: function : FUNCTION ID LPAR RPAR block ;
    public final CtrlParser.function_return function()
        throws RecognitionException {
        CtrlParser.function_return retval = new CtrlParser.function_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token FUNCTION19 = null;
        Token ID20 = null;
        Token LPAR21 = null;
        Token RPAR22 = null;
        CtrlParser.block_return block23 = null;

        CommonTree FUNCTION19_tree = null;
        CommonTree ID20_tree = null;
        CommonTree LPAR21_tree = null;
        CommonTree RPAR22_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:168:3: ( FUNCTION ID LPAR RPAR block )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:5: FUNCTION ID LPAR RPAR block
            {
                root_0 = (CommonTree) this.adaptor.nil();

                FUNCTION19 =
                    (Token) match(this.input, FUNCTION,
                        FOLLOW_FUNCTION_in_function496);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    FUNCTION19_tree =
                        (CommonTree) this.adaptor.create(FUNCTION19);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(FUNCTION19_tree,
                            root_0);
                }
                ID20 = (Token) match(this.input, ID, FOLLOW_ID_in_function499);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    ID20_tree = (CommonTree) this.adaptor.create(ID20);
                    this.adaptor.addChild(root_0, ID20_tree);
                }
                LPAR21 =
                    (Token) match(this.input, LPAR, FOLLOW_LPAR_in_function501);
                if (this.state.failed) {
                    return retval;
                }
                RPAR22 =
                    (Token) match(this.input, RPAR, FOLLOW_RPAR_in_function504);
                if (this.state.failed) {
                    return retval;
                }
                pushFollow(FOLLOW_block_in_function507);
                block23 = block();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, block23.getTree());
                }
                if (this.state.backtracking == 0) {
                    this.helper.declareName(FUNCTION19_tree, null);
                }

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "function"

    public static class block_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "block"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:174:1: block : LCURLY ( stat )* RCURLY -> ^( BLOCK ( stat )* ) ;
    public final CtrlParser.block_return block() throws RecognitionException {
        CtrlParser.block_return retval = new CtrlParser.block_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token LCURLY24 = null;
        Token RCURLY26 = null;
        CtrlParser.stat_return stat25 = null;

        CommonTree LCURLY24_tree = null;
        CommonTree RCURLY26_tree = null;
        RewriteRuleTokenStream stream_LCURLY =
            new RewriteRuleTokenStream(this.adaptor, "token LCURLY");
        RewriteRuleTokenStream stream_RCURLY =
            new RewriteRuleTokenStream(this.adaptor, "token RCURLY");
        RewriteRuleSubtreeStream stream_stat =
            new RewriteRuleSubtreeStream(this.adaptor, "rule stat");
        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:176:3: ( LCURLY ( stat )* RCURLY -> ^( BLOCK ( stat )* ) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:178:5: LCURLY ( stat )* RCURLY
            {
                LCURLY24 =
                    (Token) match(this.input, LCURLY, FOLLOW_LCURLY_in_block538);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_LCURLY.add(LCURLY24);
                }

                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:178:12: ( stat )*
                loop5: do {
                    int alt5 = 2;
                    alt5 = this.dfa5.predict(this.input);
                    switch (alt5) {
                    case 1:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:178:12: stat
                    {
                        pushFollow(FOLLOW_stat_in_block540);
                        stat25 = stat();

                        this.state._fsp--;
                        if (this.state.failed) {
                            return retval;
                        }
                        if (this.state.backtracking == 0) {
                            stream_stat.add(stat25.getTree());
                        }

                    }
                        break;

                    default:
                        break loop5;
                    }
                } while (true);

                RCURLY26 =
                    (Token) match(this.input, RCURLY, FOLLOW_RCURLY_in_block543);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_RCURLY.add(RCURLY26);
                }

                // AST REWRITE
                // elements: stat
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 178:25: -> ^( BLOCK ( stat )* )
                    {
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:178:28: ^( BLOCK ( stat )* )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(BLOCK,
                                        "BLOCK"), root_1);

                            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:178:36: ( stat )*
                            while (stream_stat.hasNext()) {
                                this.adaptor.addChild(root_1,
                                    stream_stat.nextTree());

                            }
                            stream_stat.reset();

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "block"

    public static class stat_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "stat"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:180:1: stat : ( block | ALAP stat | WHILE LPAR cond RPAR stat | UNTIL LPAR cond RPAR stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF LPAR cond RPAR stat ( ( ELSE )=> ELSE stat )? | TRY stat ( ( ELSE )=> ELSE stat )? | CHOICE stat ( ( OR )=> OR stat )+ | expr SEMI | var_decl SEMI );
    public final CtrlParser.stat_return stat() throws RecognitionException {
        CtrlParser.stat_return retval = new CtrlParser.stat_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token ALAP28 = null;
        Token WHILE30 = null;
        Token LPAR31 = null;
        Token RPAR33 = null;
        Token UNTIL35 = null;
        Token LPAR36 = null;
        Token RPAR38 = null;
        Token DO40 = null;
        Token WHILE42 = null;
        Token LPAR43 = null;
        Token RPAR45 = null;
        Token UNTIL46 = null;
        Token LPAR47 = null;
        Token RPAR49 = null;
        Token IF50 = null;
        Token LPAR51 = null;
        Token RPAR53 = null;
        Token ELSE55 = null;
        Token TRY57 = null;
        Token ELSE59 = null;
        Token CHOICE61 = null;
        Token OR63 = null;
        Token SEMI66 = null;
        Token SEMI68 = null;
        CtrlParser.block_return block27 = null;

        CtrlParser.stat_return stat29 = null;

        CtrlParser.cond_return cond32 = null;

        CtrlParser.stat_return stat34 = null;

        CtrlParser.cond_return cond37 = null;

        CtrlParser.stat_return stat39 = null;

        CtrlParser.stat_return stat41 = null;

        CtrlParser.cond_return cond44 = null;

        CtrlParser.cond_return cond48 = null;

        CtrlParser.cond_return cond52 = null;

        CtrlParser.stat_return stat54 = null;

        CtrlParser.stat_return stat56 = null;

        CtrlParser.stat_return stat58 = null;

        CtrlParser.stat_return stat60 = null;

        CtrlParser.stat_return stat62 = null;

        CtrlParser.stat_return stat64 = null;

        CtrlParser.expr_return expr65 = null;

        CtrlParser.var_decl_return var_decl67 = null;

        CommonTree ALAP28_tree = null;
        CommonTree WHILE30_tree = null;
        CommonTree LPAR31_tree = null;
        CommonTree RPAR33_tree = null;
        CommonTree UNTIL35_tree = null;
        CommonTree LPAR36_tree = null;
        CommonTree RPAR38_tree = null;
        CommonTree DO40_tree = null;
        CommonTree WHILE42_tree = null;
        CommonTree LPAR43_tree = null;
        CommonTree RPAR45_tree = null;
        CommonTree UNTIL46_tree = null;
        CommonTree LPAR47_tree = null;
        CommonTree RPAR49_tree = null;
        CommonTree IF50_tree = null;
        CommonTree LPAR51_tree = null;
        CommonTree RPAR53_tree = null;
        CommonTree ELSE55_tree = null;
        CommonTree TRY57_tree = null;
        CommonTree ELSE59_tree = null;
        CommonTree CHOICE61_tree = null;
        CommonTree OR63_tree = null;
        CommonTree SEMI66_tree = null;
        CommonTree SEMI68_tree = null;
        RewriteRuleTokenStream stream_DO =
            new RewriteRuleTokenStream(this.adaptor, "token DO");
        RewriteRuleTokenStream stream_RPAR =
            new RewriteRuleTokenStream(this.adaptor, "token RPAR");
        RewriteRuleTokenStream stream_LPAR =
            new RewriteRuleTokenStream(this.adaptor, "token LPAR");
        RewriteRuleTokenStream stream_WHILE =
            new RewriteRuleTokenStream(this.adaptor, "token WHILE");
        RewriteRuleTokenStream stream_UNTIL =
            new RewriteRuleTokenStream(this.adaptor, "token UNTIL");
        RewriteRuleSubtreeStream stream_cond =
            new RewriteRuleSubtreeStream(this.adaptor, "rule cond");
        RewriteRuleSubtreeStream stream_stat =
            new RewriteRuleSubtreeStream(this.adaptor, "rule stat");
        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:182:2: ( block | ALAP stat | WHILE LPAR cond RPAR stat | UNTIL LPAR cond RPAR stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF LPAR cond RPAR stat ( ( ELSE )=> ELSE stat )? | TRY stat ( ( ELSE )=> ELSE stat )? | CHOICE stat ( ( OR )=> OR stat )+ | expr SEMI | var_decl SEMI )
            int alt10 = 10;
            alt10 = this.dfa10.predict(this.input);
            switch (alt10) {
            case 1:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:183:4: block
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_block_in_stat567);
                block27 = block();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, block27.getTree());
                }

            }
                break;
            case 2:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:187:4: ALAP stat
            {
                root_0 = (CommonTree) this.adaptor.nil();

                ALAP28 =
                    (Token) match(this.input, ALAP, FOLLOW_ALAP_in_stat584);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    ALAP28_tree = (CommonTree) this.adaptor.create(ALAP28);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(ALAP28_tree,
                            root_0);
                }
                pushFollow(FOLLOW_stat_in_stat587);
                stat29 = stat();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, stat29.getTree());
                }

            }
                break;
            case 3:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:192:4: WHILE LPAR cond RPAR stat
            {
                root_0 = (CommonTree) this.adaptor.nil();

                WHILE30 =
                    (Token) match(this.input, WHILE, FOLLOW_WHILE_in_stat608);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    WHILE30_tree = (CommonTree) this.adaptor.create(WHILE30);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(WHILE30_tree,
                            root_0);
                }
                LPAR31 =
                    (Token) match(this.input, LPAR, FOLLOW_LPAR_in_stat611);
                if (this.state.failed) {
                    return retval;
                }
                pushFollow(FOLLOW_cond_in_stat614);
                cond32 = cond();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, cond32.getTree());
                }
                RPAR33 =
                    (Token) match(this.input, RPAR, FOLLOW_RPAR_in_stat616);
                if (this.state.failed) {
                    return retval;
                }
                pushFollow(FOLLOW_stat_in_stat619);
                stat34 = stat();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, stat34.getTree());
                }

            }
                break;
            case 4:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:196:5: UNTIL LPAR cond RPAR stat
            {
                root_0 = (CommonTree) this.adaptor.nil();

                UNTIL35 =
                    (Token) match(this.input, UNTIL, FOLLOW_UNTIL_in_stat639);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    UNTIL35_tree = (CommonTree) this.adaptor.create(UNTIL35);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(UNTIL35_tree,
                            root_0);
                }
                LPAR36 =
                    (Token) match(this.input, LPAR, FOLLOW_LPAR_in_stat642);
                if (this.state.failed) {
                    return retval;
                }
                pushFollow(FOLLOW_cond_in_stat645);
                cond37 = cond();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, cond37.getTree());
                }
                RPAR38 =
                    (Token) match(this.input, RPAR, FOLLOW_RPAR_in_stat647);
                if (this.state.failed) {
                    return retval;
                }
                pushFollow(FOLLOW_stat_in_stat650);
                stat39 = stat();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, stat39.getTree());
                }

            }
                break;
            case 5:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:197:4: DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
            {
                DO40 = (Token) match(this.input, DO, FOLLOW_DO_in_stat655);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_DO.add(DO40);
                }

                pushFollow(FOLLOW_stat_in_stat657);
                stat41 = stat();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_stat.add(stat41.getTree());
                }
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:198:4: ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                int alt6 = 2;
                int LA6_0 = this.input.LA(1);

                if ((LA6_0 == WHILE)) {
                    alt6 = 1;
                } else if ((LA6_0 == UNTIL)) {
                    alt6 = 2;
                } else {
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 0, this.input);

                    throw nvae;
                }
                switch (alt6) {
                case 1:
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:203:7: WHILE LPAR cond RPAR
                {
                    WHILE42 =
                        (Token) match(this.input, WHILE,
                            FOLLOW_WHILE_in_stat700);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_WHILE.add(WHILE42);
                    }

                    LPAR43 =
                        (Token) match(this.input, LPAR, FOLLOW_LPAR_in_stat702);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_LPAR.add(LPAR43);
                    }

                    pushFollow(FOLLOW_cond_in_stat704);
                    cond44 = cond();

                    this.state._fsp--;
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_cond.add(cond44.getTree());
                    }
                    RPAR45 =
                        (Token) match(this.input, RPAR, FOLLOW_RPAR_in_stat706);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_RPAR.add(RPAR45);
                    }

                    // AST REWRITE
                    // elements: stat, stat, WHILE, cond
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if (this.state.backtracking == 0) {
                        retval.tree = root_0;
                        RewriteRuleSubtreeStream stream_retval =
                            new RewriteRuleSubtreeStream(this.adaptor,
                                "rule retval", retval != null ? retval.tree
                                        : null);

                        root_0 = (CommonTree) this.adaptor.nil();
                        // 203:28: -> ^( BLOCK stat ^( WHILE cond stat ) )
                        {
                            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:203:31: ^( BLOCK stat ^( WHILE cond stat ) )
                            {
                                CommonTree root_1 =
                                    (CommonTree) this.adaptor.nil();
                                root_1 =
                                    (CommonTree) this.adaptor.becomeRoot(
                                        (CommonTree) this.adaptor.create(BLOCK,
                                            "BLOCK"), root_1);

                                this.adaptor.addChild(root_1,
                                    stream_stat.nextTree());
                                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:203:44: ^( WHILE cond stat )
                                {
                                    CommonTree root_2 =
                                        (CommonTree) this.adaptor.nil();
                                    root_2 =
                                        (CommonTree) this.adaptor.becomeRoot(
                                            stream_WHILE.nextNode(), root_2);

                                    this.adaptor.addChild(root_2,
                                        stream_cond.nextTree());
                                    this.adaptor.addChild(root_2,
                                        stream_stat.nextTree());

                                    this.adaptor.addChild(root_1, root_2);
                                }

                                this.adaptor.addChild(root_0, root_1);
                            }

                        }

                        retval.tree = root_0;
                    }
                }
                    break;
                case 2:
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:210:5: UNTIL LPAR cond RPAR
                {
                    UNTIL46 =
                        (Token) match(this.input, UNTIL,
                            FOLLOW_UNTIL_in_stat769);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_UNTIL.add(UNTIL46);
                    }

                    LPAR47 =
                        (Token) match(this.input, LPAR, FOLLOW_LPAR_in_stat771);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_LPAR.add(LPAR47);
                    }

                    pushFollow(FOLLOW_cond_in_stat773);
                    cond48 = cond();

                    this.state._fsp--;
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_cond.add(cond48.getTree());
                    }
                    RPAR49 =
                        (Token) match(this.input, RPAR, FOLLOW_RPAR_in_stat775);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_RPAR.add(RPAR49);
                    }

                    // AST REWRITE
                    // elements: cond, stat, UNTIL, stat
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if (this.state.backtracking == 0) {
                        retval.tree = root_0;
                        RewriteRuleSubtreeStream stream_retval =
                            new RewriteRuleSubtreeStream(this.adaptor,
                                "rule retval", retval != null ? retval.tree
                                        : null);

                        root_0 = (CommonTree) this.adaptor.nil();
                        // 210:26: -> ^( BLOCK stat ^( UNTIL cond stat ) )
                        {
                            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:210:29: ^( BLOCK stat ^( UNTIL cond stat ) )
                            {
                                CommonTree root_1 =
                                    (CommonTree) this.adaptor.nil();
                                root_1 =
                                    (CommonTree) this.adaptor.becomeRoot(
                                        (CommonTree) this.adaptor.create(BLOCK,
                                            "BLOCK"), root_1);

                                this.adaptor.addChild(root_1,
                                    stream_stat.nextTree());
                                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:210:42: ^( UNTIL cond stat )
                                {
                                    CommonTree root_2 =
                                        (CommonTree) this.adaptor.nil();
                                    root_2 =
                                        (CommonTree) this.adaptor.becomeRoot(
                                            stream_UNTIL.nextNode(), root_2);

                                    this.adaptor.addChild(root_2,
                                        stream_cond.nextTree());
                                    this.adaptor.addChild(root_2,
                                        stream_stat.nextTree());

                                    this.adaptor.addChild(root_1, root_2);
                                }

                                this.adaptor.addChild(root_0, root_1);
                            }

                        }

                        retval.tree = root_0;
                    }
                }
                    break;

                }

            }
                break;
            case 6:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:216:5: IF LPAR cond RPAR stat ( ( ELSE )=> ELSE stat )?
            {
                root_0 = (CommonTree) this.adaptor.nil();

                IF50 = (Token) match(this.input, IF, FOLLOW_IF_in_stat822);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    IF50_tree = (CommonTree) this.adaptor.create(IF50);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(IF50_tree, root_0);
                }
                LPAR51 =
                    (Token) match(this.input, LPAR, FOLLOW_LPAR_in_stat825);
                if (this.state.failed) {
                    return retval;
                }
                pushFollow(FOLLOW_cond_in_stat828);
                cond52 = cond();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, cond52.getTree());
                }
                RPAR53 =
                    (Token) match(this.input, RPAR, FOLLOW_RPAR_in_stat830);
                if (this.state.failed) {
                    return retval;
                }
                pushFollow(FOLLOW_stat_in_stat833);
                stat54 = stat();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, stat54.getTree());
                }
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:216:31: ( ( ELSE )=> ELSE stat )?
                int alt7 = 2;
                alt7 = this.dfa7.predict(this.input);
                switch (alt7) {
                case 1:
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:216:33: ( ELSE )=> ELSE stat
                {
                    ELSE55 =
                        (Token) match(this.input, ELSE, FOLLOW_ELSE_in_stat843);
                    if (this.state.failed) {
                        return retval;
                    }
                    pushFollow(FOLLOW_stat_in_stat846);
                    stat56 = stat();

                    this.state._fsp--;
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0, stat56.getTree());
                    }

                }
                    break;

                }

            }
                break;
            case 7:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:220:5: TRY stat ( ( ELSE )=> ELSE stat )?
            {
                root_0 = (CommonTree) this.adaptor.nil();

                TRY57 = (Token) match(this.input, TRY, FOLLOW_TRY_in_stat870);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    TRY57_tree = (CommonTree) this.adaptor.create(TRY57);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(TRY57_tree, root_0);
                }
                pushFollow(FOLLOW_stat_in_stat873);
                stat58 = stat();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, stat58.getTree());
                }
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:220:15: ( ( ELSE )=> ELSE stat )?
                int alt8 = 2;
                alt8 = this.dfa8.predict(this.input);
                switch (alt8) {
                case 1:
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:220:17: ( ELSE )=> ELSE stat
                {
                    ELSE59 =
                        (Token) match(this.input, ELSE, FOLLOW_ELSE_in_stat883);
                    if (this.state.failed) {
                        return retval;
                    }
                    pushFollow(FOLLOW_stat_in_stat886);
                    stat60 = stat();

                    this.state._fsp--;
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0, stat60.getTree());
                    }

                }
                    break;

                }

            }
                break;
            case 8:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:223:5: CHOICE stat ( ( OR )=> OR stat )+
            {
                root_0 = (CommonTree) this.adaptor.nil();

                CHOICE61 =
                    (Token) match(this.input, CHOICE, FOLLOW_CHOICE_in_stat905);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    CHOICE61_tree = (CommonTree) this.adaptor.create(CHOICE61);
                    root_0 =
                        (CommonTree) this.adaptor.becomeRoot(CHOICE61_tree,
                            root_0);
                }
                pushFollow(FOLLOW_stat_in_stat908);
                stat62 = stat();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, stat62.getTree());
                }
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:223:18: ( ( OR )=> OR stat )+
                int cnt9 = 0;
                loop9: do {
                    int alt9 = 2;
                    alt9 = this.dfa9.predict(this.input);
                    switch (alt9) {
                    case 1:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:223:20: ( OR )=> OR stat
                    {
                        OR63 =
                            (Token) match(this.input, OR, FOLLOW_OR_in_stat918);
                        if (this.state.failed) {
                            return retval;
                        }
                        pushFollow(FOLLOW_stat_in_stat921);
                        stat64 = stat();

                        this.state._fsp--;
                        if (this.state.failed) {
                            return retval;
                        }
                        if (this.state.backtracking == 0) {
                            this.adaptor.addChild(root_0, stat64.getTree());
                        }

                    }
                        break;

                    default:
                        if (cnt9 >= 1) {
                            break loop9;
                        }
                        if (this.state.backtracking > 0) {
                            this.state.failed = true;
                            return retval;
                        }
                        EarlyExitException eee =
                            new EarlyExitException(9, this.input);
                        throw eee;
                    }
                    cnt9++;
                } while (true);

            }
                break;
            case 9:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:226:4: expr SEMI
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_expr_in_stat936);
                expr65 = expr();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, expr65.getTree());
                }
                SEMI66 =
                    (Token) match(this.input, SEMI, FOLLOW_SEMI_in_stat938);
                if (this.state.failed) {
                    return retval;
                }

            }
                break;
            case 10:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:229:4: var_decl SEMI
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_var_decl_in_stat952);
                var_decl67 = var_decl();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, var_decl67.getTree());
                }
                SEMI68 =
                    (Token) match(this.input, SEMI, FOLLOW_SEMI_in_stat954);
                if (this.state.failed) {
                    return retval;
                }

            }
                break;

            }
            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "stat"

    public static class cond_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "cond"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:232:1: cond : cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) ;
    public final CtrlParser.cond_return cond() throws RecognitionException {
        CtrlParser.cond_return retval = new CtrlParser.cond_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token BAR70 = null;
        CtrlParser.cond_atom_return cond_atom69 = null;

        CtrlParser.cond_atom_return cond_atom71 = null;

        CommonTree BAR70_tree = null;
        RewriteRuleTokenStream stream_BAR =
            new RewriteRuleTokenStream(this.adaptor, "token BAR");
        RewriteRuleSubtreeStream stream_cond_atom =
            new RewriteRuleSubtreeStream(this.adaptor, "rule cond_atom");
        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:234:2: ( cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:236:4: cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            {
                pushFollow(FOLLOW_cond_atom_in_cond978);
                cond_atom69 = cond_atom();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_cond_atom.add(cond_atom69.getTree());
                }
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:237:4: ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
                int alt12 = 2;
                int LA12_0 = this.input.LA(1);

                if ((LA12_0 == BAR)) {
                    alt12 = 1;
                } else if ((LA12_0 == RPAR)) {
                    alt12 = 2;
                } else {
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 0, this.input);

                    throw nvae;
                }
                switch (alt12) {
                case 1:
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:237:6: ( BAR cond_atom )+
                {
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:237:6: ( BAR cond_atom )+
                    int cnt11 = 0;
                    loop11: do {
                        int alt11 = 2;
                        int LA11_0 = this.input.LA(1);

                        if ((LA11_0 == BAR)) {
                            alt11 = 1;
                        }

                        switch (alt11) {
                        case 1:
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:237:7: BAR cond_atom
                        {
                            BAR70 =
                                (Token) match(this.input, BAR,
                                    FOLLOW_BAR_in_cond987);
                            if (this.state.failed) {
                                return retval;
                            }
                            if (this.state.backtracking == 0) {
                                stream_BAR.add(BAR70);
                            }

                            pushFollow(FOLLOW_cond_atom_in_cond989);
                            cond_atom71 = cond_atom();

                            this.state._fsp--;
                            if (this.state.failed) {
                                return retval;
                            }
                            if (this.state.backtracking == 0) {
                                stream_cond_atom.add(cond_atom71.getTree());
                            }

                        }
                            break;

                        default:
                            if (cnt11 >= 1) {
                                break loop11;
                            }
                            if (this.state.backtracking > 0) {
                                this.state.failed = true;
                                return retval;
                            }
                            EarlyExitException eee =
                                new EarlyExitException(11, this.input);
                            throw eee;
                        }
                        cnt11++;
                    } while (true);

                    // AST REWRITE
                    // elements: cond_atom, cond_atom
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if (this.state.backtracking == 0) {
                        retval.tree = root_0;
                        RewriteRuleSubtreeStream stream_retval =
                            new RewriteRuleSubtreeStream(this.adaptor,
                                "rule retval", retval != null ? retval.tree
                                        : null);

                        root_0 = (CommonTree) this.adaptor.nil();
                        // 237:23: -> ^( CHOICE cond_atom ( cond_atom )+ )
                        {
                            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:237:26: ^( CHOICE cond_atom ( cond_atom )+ )
                            {
                                CommonTree root_1 =
                                    (CommonTree) this.adaptor.nil();
                                root_1 =
                                    (CommonTree) this.adaptor.becomeRoot(
                                        (CommonTree) this.adaptor.create(
                                            CHOICE, "CHOICE"), root_1);

                                this.adaptor.addChild(root_1,
                                    stream_cond_atom.nextTree());
                                if (!(stream_cond_atom.hasNext())) {
                                    throw new RewriteEarlyExitException();
                                }
                                while (stream_cond_atom.hasNext()) {
                                    this.adaptor.addChild(root_1,
                                        stream_cond_atom.nextTree());

                                }
                                stream_cond_atom.reset();

                                this.adaptor.addChild(root_0, root_1);
                            }

                        }

                        retval.tree = root_0;
                    }
                }
                    break;
                case 2:
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:238:6: 
                {

                    // AST REWRITE
                    // elements: cond_atom
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if (this.state.backtracking == 0) {
                        retval.tree = root_0;
                        RewriteRuleSubtreeStream stream_retval =
                            new RewriteRuleSubtreeStream(this.adaptor,
                                "rule retval", retval != null ? retval.tree
                                        : null);

                        root_0 = (CommonTree) this.adaptor.nil();
                        // 238:6: -> cond_atom
                        {
                            this.adaptor.addChild(root_0,
                                stream_cond_atom.nextTree());

                        }

                        retval.tree = root_0;
                    }
                }
                    break;

                }

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "cond"

    public static class cond_atom_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "cond_atom"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:242:1: cond_atom : ( TRUE | call );
    public final CtrlParser.cond_atom_return cond_atom()
        throws RecognitionException {
        CtrlParser.cond_atom_return retval = new CtrlParser.cond_atom_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token TRUE72 = null;
        CtrlParser.call_return call73 = null;

        CommonTree TRUE72_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:243:2: ( TRUE | call )
            int alt13 = 2;
            int LA13_0 = this.input.LA(1);

            if ((LA13_0 == TRUE)) {
                alt13 = 1;
            } else if ((LA13_0 == ID)) {
                alt13 = 2;
            } else {
                if (this.state.backtracking > 0) {
                    this.state.failed = true;
                    return retval;
                }
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, this.input);

                throw nvae;
            }
            switch (alt13) {
            case 1:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:245:4: TRUE
            {
                root_0 = (CommonTree) this.adaptor.nil();

                TRUE72 =
                    (Token) match(this.input, TRUE,
                        FOLLOW_TRUE_in_cond_atom1035);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    TRUE72_tree = (CommonTree) this.adaptor.create(TRUE72);
                    this.adaptor.addChild(root_0, TRUE72_tree);
                }

            }
                break;
            case 2:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:249:5: call
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_call_in_cond_atom1056);
                call73 = call();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, call73.getTree());
                }

            }
                break;

            }
            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "cond_atom"

    public static class expr_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "expr"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:251:1: expr : expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) ;
    public final CtrlParser.expr_return expr() throws RecognitionException {
        CtrlParser.expr_return retval = new CtrlParser.expr_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token BAR75 = null;
        CtrlParser.expr2_return expr274 = null;

        CtrlParser.expr2_return expr276 = null;

        CommonTree BAR75_tree = null;
        RewriteRuleTokenStream stream_BAR =
            new RewriteRuleTokenStream(this.adaptor, "token BAR");
        RewriteRuleSubtreeStream stream_expr2 =
            new RewriteRuleSubtreeStream(this.adaptor, "rule expr2");
        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:253:2: ( expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:257:4: expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            {
                pushFollow(FOLLOW_expr2_in_expr1086);
                expr274 = expr2();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_expr2.add(expr274.getTree());
                }
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:258:4: ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
                int alt15 = 2;
                int LA15_0 = this.input.LA(1);

                if ((LA15_0 == BAR)) {
                    alt15 = 1;
                } else if ((LA15_0 == SEMI || LA15_0 == RPAR)) {
                    alt15 = 2;
                } else {
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 0, this.input);

                    throw nvae;
                }
                switch (alt15) {
                case 1:
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:258:6: ( BAR expr2 )+
                {
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:258:6: ( BAR expr2 )+
                    int cnt14 = 0;
                    loop14: do {
                        int alt14 = 2;
                        int LA14_0 = this.input.LA(1);

                        if ((LA14_0 == BAR)) {
                            alt14 = 1;
                        }

                        switch (alt14) {
                        case 1:
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:258:7: BAR expr2
                        {
                            BAR75 =
                                (Token) match(this.input, BAR,
                                    FOLLOW_BAR_in_expr1094);
                            if (this.state.failed) {
                                return retval;
                            }
                            if (this.state.backtracking == 0) {
                                stream_BAR.add(BAR75);
                            }

                            pushFollow(FOLLOW_expr2_in_expr1096);
                            expr276 = expr2();

                            this.state._fsp--;
                            if (this.state.failed) {
                                return retval;
                            }
                            if (this.state.backtracking == 0) {
                                stream_expr2.add(expr276.getTree());
                            }

                        }
                            break;

                        default:
                            if (cnt14 >= 1) {
                                break loop14;
                            }
                            if (this.state.backtracking > 0) {
                                this.state.failed = true;
                                return retval;
                            }
                            EarlyExitException eee =
                                new EarlyExitException(14, this.input);
                            throw eee;
                        }
                        cnt14++;
                    } while (true);

                    // AST REWRITE
                    // elements: expr2, expr2
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if (this.state.backtracking == 0) {
                        retval.tree = root_0;
                        RewriteRuleSubtreeStream stream_retval =
                            new RewriteRuleSubtreeStream(this.adaptor,
                                "rule retval", retval != null ? retval.tree
                                        : null);

                        root_0 = (CommonTree) this.adaptor.nil();
                        // 258:19: -> ^( CHOICE expr2 ( expr2 )+ )
                        {
                            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:258:22: ^( CHOICE expr2 ( expr2 )+ )
                            {
                                CommonTree root_1 =
                                    (CommonTree) this.adaptor.nil();
                                root_1 =
                                    (CommonTree) this.adaptor.becomeRoot(
                                        (CommonTree) this.adaptor.create(
                                            CHOICE, "CHOICE"), root_1);

                                this.adaptor.addChild(root_1,
                                    stream_expr2.nextTree());
                                if (!(stream_expr2.hasNext())) {
                                    throw new RewriteEarlyExitException();
                                }
                                while (stream_expr2.hasNext()) {
                                    this.adaptor.addChild(root_1,
                                        stream_expr2.nextTree());

                                }
                                stream_expr2.reset();

                                this.adaptor.addChild(root_0, root_1);
                            }

                        }

                        retval.tree = root_0;
                    }
                }
                    break;
                case 2:
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:259:6: 
                {

                    // AST REWRITE
                    // elements: expr2
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if (this.state.backtracking == 0) {
                        retval.tree = root_0;
                        RewriteRuleSubtreeStream stream_retval =
                            new RewriteRuleSubtreeStream(this.adaptor,
                                "rule retval", retval != null ? retval.tree
                                        : null);

                        root_0 = (CommonTree) this.adaptor.nil();
                        // 259:6: -> expr2
                        {
                            this.adaptor.addChild(root_0,
                                stream_expr2.nextTree());

                        }

                        retval.tree = root_0;
                    }
                }
                    break;

                }

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "expr"

    public static class expr2_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "expr2"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:263:1: expr2 : (e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e) | SHARP expr_atom -> ^( ALAP expr_atom ) );
    public final CtrlParser.expr2_return expr2() throws RecognitionException {
        CtrlParser.expr2_return retval = new CtrlParser.expr2_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token PLUS77 = null;
        Token ASTERISK78 = null;
        Token SHARP79 = null;
        CtrlParser.expr_atom_return e = null;

        CtrlParser.expr_atom_return expr_atom80 = null;

        CommonTree PLUS77_tree = null;
        CommonTree ASTERISK78_tree = null;
        CommonTree SHARP79_tree = null;
        RewriteRuleTokenStream stream_PLUS =
            new RewriteRuleTokenStream(this.adaptor, "token PLUS");
        RewriteRuleTokenStream stream_SHARP =
            new RewriteRuleTokenStream(this.adaptor, "token SHARP");
        RewriteRuleTokenStream stream_ASTERISK =
            new RewriteRuleTokenStream(this.adaptor, "token ASTERISK");
        RewriteRuleSubtreeStream stream_expr_atom =
            new RewriteRuleSubtreeStream(this.adaptor, "rule expr_atom");
        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:264:3: (e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e) | SHARP expr_atom -> ^( ALAP expr_atom ) )
            int alt17 = 2;
            int LA17_0 = this.input.LA(1);

            if ((LA17_0 == ID || LA17_0 == LPAR || (LA17_0 >= ANY && LA17_0 <= OTHER))) {
                alt17 = 1;
            } else if ((LA17_0 == SHARP)) {
                alt17 = 2;
            } else {
                if (this.state.backtracking > 0) {
                    this.state.failed = true;
                    return retval;
                }
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, this.input);

                throw nvae;
            }
            switch (alt17) {
            case 1:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:272:5: e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e)
            {
                pushFollow(FOLLOW_expr_atom_in_expr21177);
                e = expr_atom();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_expr_atom.add(e.getTree());
                }
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:273:5: ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e)
                int alt16 = 3;
                switch (this.input.LA(1)) {
                case PLUS: {
                    alt16 = 1;
                }
                    break;
                case ASTERISK: {
                    alt16 = 2;
                }
                    break;
                case SEMI:
                case RPAR:
                case BAR: {
                    alt16 = 3;
                }
                    break;
                default:
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 0, this.input);

                    throw nvae;
                }

                switch (alt16) {
                case 1:
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:273:7: PLUS
                {
                    PLUS77 =
                        (Token) match(this.input, PLUS,
                            FOLLOW_PLUS_in_expr21185);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_PLUS.add(PLUS77);
                    }

                    // AST REWRITE
                    // elements: e, e
                    // token labels: 
                    // rule labels: retval, e
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if (this.state.backtracking == 0) {
                        retval.tree = root_0;
                        RewriteRuleSubtreeStream stream_retval =
                            new RewriteRuleSubtreeStream(this.adaptor,
                                "rule retval", retval != null ? retval.tree
                                        : null);
                        RewriteRuleSubtreeStream stream_e =
                            new RewriteRuleSubtreeStream(this.adaptor,
                                "rule e", e != null ? e.tree : null);

                        root_0 = (CommonTree) this.adaptor.nil();
                        // 273:12: -> ^( BLOCK $e ^( STAR $e) )
                        {
                            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:273:15: ^( BLOCK $e ^( STAR $e) )
                            {
                                CommonTree root_1 =
                                    (CommonTree) this.adaptor.nil();
                                root_1 =
                                    (CommonTree) this.adaptor.becomeRoot(
                                        (CommonTree) this.adaptor.create(BLOCK,
                                            "BLOCK"), root_1);

                                this.adaptor.addChild(root_1,
                                    stream_e.nextTree());
                                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:273:26: ^( STAR $e)
                                {
                                    CommonTree root_2 =
                                        (CommonTree) this.adaptor.nil();
                                    root_2 =
                                        (CommonTree) this.adaptor.becomeRoot(
                                            (CommonTree) this.adaptor.create(
                                                STAR, "STAR"), root_2);

                                    this.adaptor.addChild(root_2,
                                        stream_e.nextTree());

                                    this.adaptor.addChild(root_1, root_2);
                                }

                                this.adaptor.addChild(root_0, root_1);
                            }

                        }

                        retval.tree = root_0;
                    }
                }
                    break;
                case 2:
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:274:7: ASTERISK
                {
                    ASTERISK78 =
                        (Token) match(this.input, ASTERISK,
                            FOLLOW_ASTERISK_in_expr21209);
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_ASTERISK.add(ASTERISK78);
                    }

                    // AST REWRITE
                    // elements: e
                    // token labels: 
                    // rule labels: retval, e
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if (this.state.backtracking == 0) {
                        retval.tree = root_0;
                        RewriteRuleSubtreeStream stream_retval =
                            new RewriteRuleSubtreeStream(this.adaptor,
                                "rule retval", retval != null ? retval.tree
                                        : null);
                        RewriteRuleSubtreeStream stream_e =
                            new RewriteRuleSubtreeStream(this.adaptor,
                                "rule e", e != null ? e.tree : null);

                        root_0 = (CommonTree) this.adaptor.nil();
                        // 274:16: -> ^( STAR $e)
                        {
                            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:274:19: ^( STAR $e)
                            {
                                CommonTree root_1 =
                                    (CommonTree) this.adaptor.nil();
                                root_1 =
                                    (CommonTree) this.adaptor.becomeRoot(
                                        (CommonTree) this.adaptor.create(STAR,
                                            "STAR"), root_1);

                                this.adaptor.addChild(root_1,
                                    stream_e.nextTree());

                                this.adaptor.addChild(root_0, root_1);
                            }

                        }

                        retval.tree = root_0;
                    }
                }
                    break;
                case 3:
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:275:7: 
                {

                    // AST REWRITE
                    // elements: e
                    // token labels: 
                    // rule labels: retval, e
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if (this.state.backtracking == 0) {
                        retval.tree = root_0;
                        RewriteRuleSubtreeStream stream_retval =
                            new RewriteRuleSubtreeStream(this.adaptor,
                                "rule retval", retval != null ? retval.tree
                                        : null);
                        RewriteRuleSubtreeStream stream_e =
                            new RewriteRuleSubtreeStream(this.adaptor,
                                "rule e", e != null ? e.tree : null);

                        root_0 = (CommonTree) this.adaptor.nil();
                        // 275:7: -> $e
                        {
                            this.adaptor.addChild(root_0, stream_e.nextTree());

                        }

                        retval.tree = root_0;
                    }
                }
                    break;

                }

            }
                break;
            case 2:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:281:5: SHARP expr_atom
            {
                SHARP79 =
                    (Token) match(this.input, SHARP, FOLLOW_SHARP_in_expr21261);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_SHARP.add(SHARP79);
                }

                pushFollow(FOLLOW_expr_atom_in_expr21263);
                expr_atom80 = expr_atom();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_expr_atom.add(expr_atom80.getTree());
                }

                // AST REWRITE
                // elements: expr_atom
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 281:21: -> ^( ALAP expr_atom )
                    {
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:281:24: ^( ALAP expr_atom )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(ALAP,
                                        "ALAP"), root_1);

                            this.adaptor.addChild(root_1,
                                stream_expr_atom.nextTree());

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
            }
                break;

            }
            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "expr2"

    public static class expr_atom_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "expr_atom"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:284:1: expr_atom : ( ANY | OTHER | LPAR expr RPAR | call );
    public final CtrlParser.expr_atom_return expr_atom()
        throws RecognitionException {
        CtrlParser.expr_atom_return retval = new CtrlParser.expr_atom_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token ANY81 = null;
        Token OTHER82 = null;
        Token LPAR83 = null;
        Token RPAR85 = null;
        CtrlParser.expr_return expr84 = null;

        CtrlParser.call_return call86 = null;

        CommonTree ANY81_tree = null;
        CommonTree OTHER82_tree = null;
        CommonTree LPAR83_tree = null;
        CommonTree RPAR85_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:285:2: ( ANY | OTHER | LPAR expr RPAR | call )
            int alt18 = 4;
            switch (this.input.LA(1)) {
            case ANY: {
                alt18 = 1;
            }
                break;
            case OTHER: {
                alt18 = 2;
            }
                break;
            case LPAR: {
                alt18 = 3;
            }
                break;
            case ID: {
                alt18 = 4;
            }
                break;
            default:
                if (this.state.backtracking > 0) {
                    this.state.failed = true;
                    return retval;
                }
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, this.input);

                throw nvae;
            }

            switch (alt18) {
            case 1:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:287:4: ANY
            {
                root_0 = (CommonTree) this.adaptor.nil();

                ANY81 =
                    (Token) match(this.input, ANY, FOLLOW_ANY_in_expr_atom1291);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    ANY81_tree = (CommonTree) this.adaptor.create(ANY81);
                    this.adaptor.addChild(root_0, ANY81_tree);
                }

            }
                break;
            case 2:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:291:4: OTHER
            {
                root_0 = (CommonTree) this.adaptor.nil();

                OTHER82 =
                    (Token) match(this.input, OTHER,
                        FOLLOW_OTHER_in_expr_atom1308);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    OTHER82_tree = (CommonTree) this.adaptor.create(OTHER82);
                    this.adaptor.addChild(root_0, OTHER82_tree);
                }

            }
                break;
            case 3:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:294:4: LPAR expr RPAR
            {
                root_0 = (CommonTree) this.adaptor.nil();

                LPAR83 =
                    (Token) match(this.input, LPAR,
                        FOLLOW_LPAR_in_expr_atom1321);
                if (this.state.failed) {
                    return retval;
                }
                pushFollow(FOLLOW_expr_in_expr_atom1324);
                expr84 = expr();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, expr84.getTree());
                }
                RPAR85 =
                    (Token) match(this.input, RPAR,
                        FOLLOW_RPAR_in_expr_atom1326);
                if (this.state.failed) {
                    return retval;
                }

            }
                break;
            case 4:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:297:4: call
            {
                root_0 = (CommonTree) this.adaptor.nil();

                pushFollow(FOLLOW_call_in_expr_atom1340);
                call86 = call();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    this.adaptor.addChild(root_0, call86.getTree());
                }

            }
                break;

            }
            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "expr_atom"

    public static class call_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "call"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:300:1: call : rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) ;
    public final CtrlParser.call_return call() throws RecognitionException {
        CtrlParser.call_return retval = new CtrlParser.call_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CtrlParser.rule_name_return rule_name87 = null;

        CtrlParser.arg_list_return arg_list88 = null;

        RewriteRuleSubtreeStream stream_arg_list =
            new RewriteRuleSubtreeStream(this.adaptor, "rule arg_list");
        RewriteRuleSubtreeStream stream_rule_name =
            new RewriteRuleSubtreeStream(this.adaptor, "rule rule_name");
        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:302:2: ( rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:306:4: rule_name ( arg_list )?
            {
                pushFollow(FOLLOW_rule_name_in_call1370);
                rule_name87 = rule_name();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_rule_name.add(rule_name87.getTree());
                }
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:306:14: ( arg_list )?
                int alt19 = 2;
                int LA19_0 = this.input.LA(1);

                if ((LA19_0 == LPAR)) {
                    alt19 = 1;
                }
                switch (alt19) {
                case 1:
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:306:14: arg_list
                {
                    pushFollow(FOLLOW_arg_list_in_call1372);
                    arg_list88 = arg_list();

                    this.state._fsp--;
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_arg_list.add(arg_list88.getTree());
                    }

                }
                    break;

                }

                // AST REWRITE
                // elements: rule_name, arg_list
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 307:4: -> ^( CALL[$rule_name.start] rule_name ( arg_list )? )
                    {
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:307:7: ^( CALL[$rule_name.start] rule_name ( arg_list )? )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(CALL,
                                        (rule_name87 != null
                                                ? ((Token) rule_name87.start)
                                                : null)), root_1);

                            this.adaptor.addChild(root_1,
                                stream_rule_name.nextTree());
                            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:307:42: ( arg_list )?
                            if (stream_arg_list.hasNext()) {
                                this.adaptor.addChild(root_1,
                                    stream_arg_list.nextTree());

                            }
                            stream_arg_list.reset();

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "call"

    public static class arg_list_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "arg_list"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:310:1: arg_list : LPAR ( arg ( COMMA arg )* )? RPAR -> ^( ARGS ( arg )* ) ;
    public final CtrlParser.arg_list_return arg_list()
        throws RecognitionException {
        CtrlParser.arg_list_return retval = new CtrlParser.arg_list_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token LPAR89 = null;
        Token COMMA91 = null;
        Token RPAR93 = null;
        CtrlParser.arg_return arg90 = null;

        CtrlParser.arg_return arg92 = null;

        CommonTree LPAR89_tree = null;
        CommonTree COMMA91_tree = null;
        CommonTree RPAR93_tree = null;
        RewriteRuleTokenStream stream_RPAR =
            new RewriteRuleTokenStream(this.adaptor, "token RPAR");
        RewriteRuleTokenStream stream_LPAR =
            new RewriteRuleTokenStream(this.adaptor, "token LPAR");
        RewriteRuleTokenStream stream_COMMA =
            new RewriteRuleTokenStream(this.adaptor, "token COMMA");
        RewriteRuleSubtreeStream stream_arg =
            new RewriteRuleSubtreeStream(this.adaptor, "rule arg");
        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:314:3: ( LPAR ( arg ( COMMA arg )* )? RPAR -> ^( ARGS ( arg )* ) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:316:5: LPAR ( arg ( COMMA arg )* )? RPAR
            {
                LPAR89 =
                    (Token) match(this.input, LPAR, FOLLOW_LPAR_in_arg_list1412);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_LPAR.add(LPAR89);
                }

                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:316:10: ( arg ( COMMA arg )* )?
                int alt21 = 2;
                int LA21_0 = this.input.LA(1);

                if ((LA21_0 == ID || LA21_0 == TRUE || (LA21_0 >= OUT && LA21_0 <= REAL_LIT))) {
                    alt21 = 1;
                }
                switch (alt21) {
                case 1:
                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:316:11: arg ( COMMA arg )*
                {
                    pushFollow(FOLLOW_arg_in_arg_list1415);
                    arg90 = arg();

                    this.state._fsp--;
                    if (this.state.failed) {
                        return retval;
                    }
                    if (this.state.backtracking == 0) {
                        stream_arg.add(arg90.getTree());
                    }
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:316:15: ( COMMA arg )*
                    loop20: do {
                        int alt20 = 2;
                        int LA20_0 = this.input.LA(1);

                        if ((LA20_0 == COMMA)) {
                            alt20 = 1;
                        }

                        switch (alt20) {
                        case 1:
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:316:16: COMMA arg
                        {
                            COMMA91 =
                                (Token) match(this.input, COMMA,
                                    FOLLOW_COMMA_in_arg_list1418);
                            if (this.state.failed) {
                                return retval;
                            }
                            if (this.state.backtracking == 0) {
                                stream_COMMA.add(COMMA91);
                            }

                            pushFollow(FOLLOW_arg_in_arg_list1420);
                            arg92 = arg();

                            this.state._fsp--;
                            if (this.state.failed) {
                                return retval;
                            }
                            if (this.state.backtracking == 0) {
                                stream_arg.add(arg92.getTree());
                            }

                        }
                            break;

                        default:
                            break loop20;
                        }
                    } while (true);

                }
                    break;

                }

                RPAR93 =
                    (Token) match(this.input, RPAR, FOLLOW_RPAR_in_arg_list1426);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_RPAR.add(RPAR93);
                }

                // AST REWRITE
                // elements: arg
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 317:5: -> ^( ARGS ( arg )* )
                    {
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:317:8: ^( ARGS ( arg )* )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(ARGS,
                                        "ARGS"), root_1);

                            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:317:15: ( arg )*
                            while (stream_arg.hasNext()) {
                                this.adaptor.addChild(root_1,
                                    stream_arg.nextTree());

                            }
                            stream_arg.reset();

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "arg_list"

    public static class arg_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "arg"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:320:1: arg : ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) );
    public final CtrlParser.arg_return arg() throws RecognitionException {
        CtrlParser.arg_return retval = new CtrlParser.arg_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token OUT94 = null;
        Token ID95 = null;
        Token ID96 = null;
        Token DONT_CARE97 = null;
        CtrlParser.literal_return literal98 = null;

        CommonTree OUT94_tree = null;
        CommonTree ID95_tree = null;
        CommonTree ID96_tree = null;
        CommonTree DONT_CARE97_tree = null;
        RewriteRuleTokenStream stream_DONT_CARE =
            new RewriteRuleTokenStream(this.adaptor, "token DONT_CARE");
        RewriteRuleTokenStream stream_OUT =
            new RewriteRuleTokenStream(this.adaptor, "token OUT");
        RewriteRuleTokenStream stream_ID =
            new RewriteRuleTokenStream(this.adaptor, "token ID");
        RewriteRuleSubtreeStream stream_literal =
            new RewriteRuleSubtreeStream(this.adaptor, "rule literal");
        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:324:3: ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) )
            int alt22 = 4;
            switch (this.input.LA(1)) {
            case OUT: {
                alt22 = 1;
            }
                break;
            case ID: {
                alt22 = 2;
            }
                break;
            case DONT_CARE: {
                alt22 = 3;
            }
                break;
            case TRUE:
            case FALSE:
            case STRING_LIT:
            case INT_LIT:
            case REAL_LIT: {
                alt22 = 4;
            }
                break;
            default:
                if (this.state.backtracking > 0) {
                    this.state.failed = true;
                    return retval;
                }
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, this.input);

                throw nvae;
            }

            switch (alt22) {
            case 1:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:327:5: OUT ID
            {
                OUT94 = (Token) match(this.input, OUT, FOLLOW_OUT_in_arg1469);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_OUT.add(OUT94);
                }

                ID95 = (Token) match(this.input, ID, FOLLOW_ID_in_arg1471);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_ID.add(ID95);
                }

                // AST REWRITE
                // elements: ID, OUT
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 327:12: -> ^( ARG OUT ID )
                    {
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:327:15: ^( ARG OUT ID )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(ARG, "ARG"),
                                    root_1);

                            this.adaptor.addChild(root_1, stream_OUT.nextNode());
                            this.adaptor.addChild(root_1, stream_ID.nextNode());

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
            }
                break;
            case 2:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:331:5: ID
            {
                ID96 = (Token) match(this.input, ID, FOLLOW_ID_in_arg1502);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_ID.add(ID96);
                }

                // AST REWRITE
                // elements: ID
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 331:8: -> ^( ARG ID )
                    {
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:331:11: ^( ARG ID )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(ARG, "ARG"),
                                    root_1);

                            this.adaptor.addChild(root_1, stream_ID.nextNode());

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
            }
                break;
            case 3:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:335:5: DONT_CARE
            {
                DONT_CARE97 =
                    (Token) match(this.input, DONT_CARE,
                        FOLLOW_DONT_CARE_in_arg1531);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_DONT_CARE.add(DONT_CARE97);
                }

                // AST REWRITE
                // elements: DONT_CARE
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 335:15: -> ^( ARG DONT_CARE )
                    {
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:335:18: ^( ARG DONT_CARE )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(ARG, "ARG"),
                                    root_1);

                            this.adaptor.addChild(root_1,
                                stream_DONT_CARE.nextNode());

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
            }
                break;
            case 4:
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:336:5: literal
            {
                pushFollow(FOLLOW_literal_in_arg1548);
                literal98 = literal();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_literal.add(literal98.getTree());
                }

                // AST REWRITE
                // elements: literal
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 336:13: -> ^( ARG literal )
                    {
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:336:16: ^( ARG literal )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(ARG, "ARG"),
                                    root_1);

                            this.adaptor.addChild(root_1,
                                stream_literal.nextTree());

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
            }
                break;

            }
            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "arg"

    public static class literal_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "literal"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:339:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlParser.literal_return literal()
        throws RecognitionException {
        CtrlParser.literal_return retval = new CtrlParser.literal_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token set99 = null;

        CommonTree set99_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:340:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
            {
                root_0 = (CommonTree) this.adaptor.nil();

                set99 = (Token) this.input.LT(1);
                if (this.input.LA(1) == TRUE
                    || (this.input.LA(1) >= FALSE && this.input.LA(1) <= REAL_LIT)) {
                    this.input.consume();
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0,
                            (CommonTree) this.adaptor.create(set99));
                    }
                    this.state.errorRecovery = false;
                    this.state.failed = false;
                } else {
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    MismatchedSetException mse =
                        new MismatchedSetException(null, this.input);
                    throw mse;
                }

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "literal"

    public static class rule_name_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "rule_name"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:357:1: rule_name : qual_name ->;
    public final CtrlParser.rule_name_return rule_name()
        throws RecognitionException {
        CtrlParser.rule_name_return retval = new CtrlParser.rule_name_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        CtrlParser.qual_name_return qual_name100 = null;

        RewriteRuleSubtreeStream stream_qual_name =
            new RewriteRuleSubtreeStream(this.adaptor, "rule qual_name");
        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:362:3: ( qual_name ->)
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:362:5: qual_name
            {
                pushFollow(FOLLOW_qual_name_in_rule_name1658);
                qual_name100 = qual_name();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_qual_name.add(qual_name100.getTree());
                }

                // AST REWRITE
                // elements: 
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 363:5: ->
                    {
                        this.adaptor.addChild(root_0,
                            this.helper.lookup((qual_name100 != null
                                    ? ((CommonTree) qual_name100.tree) : null)));

                    }

                    retval.tree = root_0;
                }
            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "rule_name"

    public static class var_decl_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "var_decl"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:366:1: var_decl : var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) ;
    public final CtrlParser.var_decl_return var_decl()
        throws RecognitionException {
        CtrlParser.var_decl_return retval = new CtrlParser.var_decl_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token ID102 = null;
        Token COMMA103 = null;
        Token ID104 = null;
        CtrlParser.var_type_return var_type101 = null;

        CommonTree ID102_tree = null;
        CommonTree COMMA103_tree = null;
        CommonTree ID104_tree = null;
        RewriteRuleTokenStream stream_ID =
            new RewriteRuleTokenStream(this.adaptor, "token ID");
        RewriteRuleTokenStream stream_COMMA =
            new RewriteRuleTokenStream(this.adaptor, "token COMMA");
        RewriteRuleSubtreeStream stream_var_type =
            new RewriteRuleSubtreeStream(this.adaptor, "rule var_type");
        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:368:2: ( var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:370:4: var_type ID ( COMMA ID )*
            {
                pushFollow(FOLLOW_var_type_in_var_decl1688);
                var_type101 = var_type();

                this.state._fsp--;
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_var_type.add(var_type101.getTree());
                }
                ID102 =
                    (Token) match(this.input, ID, FOLLOW_ID_in_var_decl1690);
                if (this.state.failed) {
                    return retval;
                }
                if (this.state.backtracking == 0) {
                    stream_ID.add(ID102);
                }

                // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:370:16: ( COMMA ID )*
                loop23: do {
                    int alt23 = 2;
                    int LA23_0 = this.input.LA(1);

                    if ((LA23_0 == COMMA)) {
                        alt23 = 1;
                    }

                    switch (alt23) {
                    case 1:
                    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:370:17: COMMA ID
                    {
                        COMMA103 =
                            (Token) match(this.input, COMMA,
                                FOLLOW_COMMA_in_var_decl1693);
                        if (this.state.failed) {
                            return retval;
                        }
                        if (this.state.backtracking == 0) {
                            stream_COMMA.add(COMMA103);
                        }

                        ID104 =
                            (Token) match(this.input, ID,
                                FOLLOW_ID_in_var_decl1695);
                        if (this.state.failed) {
                            return retval;
                        }
                        if (this.state.backtracking == 0) {
                            stream_ID.add(ID104);
                        }

                    }
                        break;

                    default:
                        break loop23;
                    }
                } while (true);

                // AST REWRITE
                // elements: var_type, ID
                // token labels: 
                // rule labels: retval
                // token list labels: 
                // rule list labels: 
                // wildcard labels: 
                if (this.state.backtracking == 0) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval =
                        new RewriteRuleSubtreeStream(this.adaptor,
                            "rule retval", retval != null ? retval.tree : null);

                    root_0 = (CommonTree) this.adaptor.nil();
                    // 370:28: -> ^( VAR var_type ( ID )+ )
                    {
                        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:370:31: ^( VAR var_type ( ID )+ )
                        {
                            CommonTree root_1 = (CommonTree) this.adaptor.nil();
                            root_1 =
                                (CommonTree) this.adaptor.becomeRoot(
                                    (CommonTree) this.adaptor.create(VAR, "VAR"),
                                    root_1);

                            this.adaptor.addChild(root_1,
                                stream_var_type.nextTree());
                            if (!(stream_ID.hasNext())) {
                                throw new RewriteEarlyExitException();
                            }
                            while (stream_ID.hasNext()) {
                                this.adaptor.addChild(root_1,
                                    stream_ID.nextNode());

                            }
                            stream_ID.reset();

                            this.adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                }
            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "var_decl"

    public static class var_type_return extends ParserRuleReturnScope {
        CommonTree tree;

        public Object getTree() {
            return this.tree;
        }
    };

    // $ANTLR start "var_type"
    // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:373:1: var_type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlParser.var_type_return var_type()
        throws RecognitionException {
        CtrlParser.var_type_return retval = new CtrlParser.var_type_return();
        retval.start = this.input.LT(1);

        CommonTree root_0 = null;

        Token set105 = null;

        CommonTree set105_tree = null;

        try {
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:375:2: ( NODE | BOOL | STRING | INT | REAL )
            // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
            {
                root_0 = (CommonTree) this.adaptor.nil();

                set105 = (Token) this.input.LT(1);
                if ((this.input.LA(1) >= NODE && this.input.LA(1) <= REAL)) {
                    this.input.consume();
                    if (this.state.backtracking == 0) {
                        this.adaptor.addChild(root_0,
                            (CommonTree) this.adaptor.create(set105));
                    }
                    this.state.errorRecovery = false;
                    this.state.failed = false;
                } else {
                    if (this.state.backtracking > 0) {
                        this.state.failed = true;
                        return retval;
                    }
                    MismatchedSetException mse =
                        new MismatchedSetException(null, this.input);
                    throw mse;
                }

            }

            retval.stop = this.input.LT(-1);

            if (this.state.backtracking == 0) {

                retval.tree =
                    (CommonTree) this.adaptor.rulePostProcessing(root_0);
                this.adaptor.setTokenBoundaries(retval.tree, retval.start,
                    retval.stop);
            }
        } catch (RecognitionException re) {
            reportError(re);
            recover(this.input, re);
            retval.tree =
                (CommonTree) this.adaptor.errorNode(this.input, retval.start,
                    this.input.LT(-1), re);

        } finally {
        }
        return retval;
    }

    // $ANTLR end "var_type"

    // $ANTLR start synpred1_Ctrl
    public final void synpred1_Ctrl_fragment() throws RecognitionException {
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:216:33: ( ELSE )
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:216:34: ELSE
        {
            match(this.input, ELSE, FOLLOW_ELSE_in_synpred1_Ctrl838);
            if (this.state.failed) {
                return;
            }

        }
    }

    // $ANTLR end synpred1_Ctrl

    // $ANTLR start synpred2_Ctrl
    public final void synpred2_Ctrl_fragment() throws RecognitionException {
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:220:17: ( ELSE )
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:220:18: ELSE
        {
            match(this.input, ELSE, FOLLOW_ELSE_in_synpred2_Ctrl878);
            if (this.state.failed) {
                return;
            }

        }
    }

    // $ANTLR end synpred2_Ctrl

    // $ANTLR start synpred3_Ctrl
    public final void synpred3_Ctrl_fragment() throws RecognitionException {
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:223:20: ( OR )
        // D:\\eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:223:21: OR
        {
            match(this.input, OR, FOLLOW_OR_in_synpred3_Ctrl913);
            if (this.state.failed) {
                return;
            }

        }
    }

    // $ANTLR end synpred3_Ctrl

    // Delegated rules

    public final boolean synpred1_Ctrl() {
        this.state.backtracking++;
        int start = this.input.mark();
        try {
            synpred1_Ctrl_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: " + re);
        }
        boolean success = !this.state.failed;
        this.input.rewind(start);
        this.state.backtracking--;
        this.state.failed = false;
        return success;
    }

    public final boolean synpred3_Ctrl() {
        this.state.backtracking++;
        int start = this.input.mark();
        try {
            synpred3_Ctrl_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: " + re);
        }
        boolean success = !this.state.failed;
        this.input.rewind(start);
        this.state.backtracking--;
        this.state.failed = false;
        return success;
    }

    public final boolean synpred2_Ctrl() {
        this.state.backtracking++;
        int start = this.input.mark();
        try {
            synpred2_Ctrl_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: " + re);
        }
        boolean success = !this.state.failed;
        this.input.rewind(start);
        this.state.backtracking--;
        this.state.failed = false;
        return success;
    }

    protected DFA1 dfa1 = new DFA1(this);
    protected DFA2 dfa2 = new DFA2(this);
    protected DFA3 dfa3 = new DFA3(this);
    protected DFA5 dfa5 = new DFA5(this);
    protected DFA10 dfa10 = new DFA10(this);
    protected DFA7 dfa7 = new DFA7(this);
    protected DFA8 dfa8 = new DFA8(this);
    protected DFA9 dfa9 = new DFA9(this);
    static final String DFA1_eotS = "\23\uffff";
    static final String DFA1_eofS = "\1\1\22\uffff";
    static final String DFA1_minS = "\1\20\22\uffff";
    static final String DFA1_maxS = "\1\64\22\uffff";
    static final String DFA1_acceptS = "\1\uffff\1\2\20\uffff\1\1";
    static final String DFA1_specialS = "\23\uffff}>";
    static final String[] DFA1_transitionS = {
        "\1\22\1\1\1\uffff\2\1\1\uffff\2\1\1\uffff\5\1\1\uffff\2\1\5"
            + "\uffff\3\1\7\uffff\5\1", "", "", "", "", "", "", "", "", "", "",
        "", "", "", "", "", "", "", ""};

    static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
    static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
    static final char[] DFA1_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
    static final char[] DFA1_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
    static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
    static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
    static final short[][] DFA1_transition;

    static {
        int numStates = DFA1_transitionS.length;
        DFA1_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
        }
    }

    class DFA1 extends DFA {

        public DFA1(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 1;
            this.eot = DFA1_eot;
            this.eof = DFA1_eof;
            this.min = DFA1_min;
            this.max = DFA1_max;
            this.accept = DFA1_accept;
            this.special = DFA1_special;
            this.transition = DFA1_transition;
        }

        public String getDescription() {
            return "()* loopback of 111:5: ( import_decl )*";
        }
    }

    static final String DFA2_eotS = "\22\uffff";
    static final String DFA2_eofS = "\1\1\21\uffff";
    static final String DFA2_minS = "\1\21\21\uffff";
    static final String DFA2_maxS = "\1\64\21\uffff";
    static final String DFA2_acceptS = "\1\uffff\1\4\1\1\1\2\1\3\15\uffff";
    static final String DFA2_specialS = "\22\uffff}>";
    static final String[] DFA2_transitionS = {
        "\1\4\1\uffff\1\3\1\4\1\uffff\1\2\1\4\1\uffff\5\4\1\uffff\2"
            + "\4\5\uffff\3\4\7\uffff\5\4", "", "", "", "", "", "", "", "", "",
        "", "", "", "", "", "", "", ""};

    static final short[] DFA2_eot = DFA.unpackEncodedString(DFA2_eotS);
    static final short[] DFA2_eof = DFA.unpackEncodedString(DFA2_eofS);
    static final char[] DFA2_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
    static final char[] DFA2_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
    static final short[] DFA2_accept = DFA.unpackEncodedString(DFA2_acceptS);
    static final short[] DFA2_special = DFA.unpackEncodedString(DFA2_specialS);
    static final short[][] DFA2_transition;

    static {
        int numStates = DFA2_transitionS.length;
        DFA2_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA2_transition[i] = DFA.unpackEncodedString(DFA2_transitionS[i]);
        }
    }

    class DFA2 extends DFA {

        public DFA2(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 2;
            this.eot = DFA2_eot;
            this.eof = DFA2_eof;
            this.min = DFA2_min;
            this.max = DFA2_max;
            this.accept = DFA2_accept;
            this.special = DFA2_special;
            this.transition = DFA2_transition;
        }

        public String getDescription() {
            return "()* loopback of 112:5: ( function | recipe | stat )*";
        }
    }

    static final String DFA3_eotS = "\24\uffff";
    static final String DFA3_eofS = "\1\2\23\uffff";
    static final String DFA3_minS = "\1\16\23\uffff";
    static final String DFA3_maxS = "\1\64\23\uffff";
    static final String DFA3_acceptS = "\1\uffff\1\1\1\2\21\uffff";
    static final String DFA3_specialS = "\24\uffff}>";
    static final String[] DFA3_transitionS = {
        "\1\1\1\uffff\2\2\1\uffff\2\2\1\uffff\2\2\1\uffff\5\2\1\uffff"
            + "\2\2\5\uffff\3\2\7\uffff\5\2", "", "", "", "", "", "", "", "",
        "", "", "", "", "", "", "", "", "", "", ""};

    static final short[] DFA3_eot = DFA.unpackEncodedString(DFA3_eotS);
    static final short[] DFA3_eof = DFA.unpackEncodedString(DFA3_eofS);
    static final char[] DFA3_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA3_minS);
    static final char[] DFA3_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA3_maxS);
    static final short[] DFA3_accept = DFA.unpackEncodedString(DFA3_acceptS);
    static final short[] DFA3_special = DFA.unpackEncodedString(DFA3_specialS);
    static final short[][] DFA3_transition;

    static {
        int numStates = DFA3_transitionS.length;
        DFA3_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA3_transition[i] = DFA.unpackEncodedString(DFA3_transitionS[i]);
        }
    }

    class DFA3 extends DFA {

        public DFA3(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 3;
            this.eot = DFA3_eot;
            this.eof = DFA3_eof;
            this.min = DFA3_min;
            this.max = DFA3_max;
            this.accept = DFA3_accept;
            this.special = DFA3_special;
            this.transition = DFA3_transition;
        }

        public String getDescription() {
            return "127:5: ( PACKAGE qual_name SEMI -> ^( PACKAGE qual_name ) | ->)";
        }
    }

    static final String DFA5_eotS = "\20\uffff";
    static final String DFA5_eofS = "\20\uffff";
    static final String DFA5_minS = "\1\21\17\uffff";
    static final String DFA5_maxS = "\1\64\17\uffff";
    static final String DFA5_acceptS = "\1\uffff\1\2\1\1\15\uffff";
    static final String DFA5_specialS = "\20\uffff}>";
    static final String[] DFA5_transitionS = {
        "\1\2\2\uffff\1\2\2\uffff\1\2\1\1\5\2\1\uffff\2\2\5\uffff\3"
            + "\2\7\uffff\5\2", "", "", "", "", "", "", "", "", "", "", "", "",
        "", "", ""};

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }

        public String getDescription() {
            return "()* loopback of 178:12: ( stat )*";
        }
    }

    static final String DFA10_eotS = "\17\uffff";
    static final String DFA10_eofS = "\17\uffff";
    static final String DFA10_minS = "\1\21\16\uffff";
    static final String DFA10_maxS = "\1\64\16\uffff";
    static final String DFA10_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\4\uffff\1\12";
    static final String DFA10_specialS = "\17\uffff}>";
    static final String[] DFA10_transitionS = {
        "\1\11\2\uffff\1\11\2\uffff\1\1\1\uffff\1\2\1\3\1\4\1\5\1\6"
            + "\1\uffff\1\7\1\10\5\uffff\3\11\7\uffff\5\16", "", "", "", "",
        "", "", "", "", "", "", "", "", "", ""};

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
            return "180:1: stat : ( block | ALAP stat | WHILE LPAR cond RPAR stat | UNTIL LPAR cond RPAR stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF LPAR cond RPAR stat ( ( ELSE )=> ELSE stat )? | TRY stat ( ( ELSE )=> ELSE stat )? | CHOICE stat ( ( OR )=> OR stat )+ | expr SEMI | var_decl SEMI );";
        }
    }

    static final String DFA7_eotS = "\44\uffff";
    static final String DFA7_eofS = "\1\2\43\uffff";
    static final String DFA7_minS = "\1\21\1\0\42\uffff";
    static final String DFA7_maxS = "\1\64\1\0\42\uffff";
    static final String DFA7_acceptS = "\2\uffff\1\2\40\uffff\1\1";
    static final String DFA7_specialS = "\1\uffff\1\0\42\uffff}>";
    static final String[] DFA7_transitionS = {
        "\1\2\1\uffff\2\2\1\uffff\10\2\1\1\3\2\4\uffff\3\2\7\uffff\5" + "\2",
        "\1\uffff", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
        "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
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
            return "216:31: ( ( ELSE )=> ELSE stat )?";
        }

        public int specialStateTransition(int s, IntStream _input)
            throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch (s) {
            case 0:
                int LA7_1 = input.LA(1);

                int index7_1 = input.index();
                input.rewind();
                s = -1;
                if ((synpred1_Ctrl())) {
                    s = 35;
                }

                else if ((true)) {
                    s = 2;
                }

                input.seek(index7_1);
                if (s >= 0) {
                    return s;
                }
                break;
            }
            if (CtrlParser.this.state.backtracking > 0) {
                CtrlParser.this.state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 7, _s, input);
            error(nvae);
            throw nvae;
        }
    }

    static final String DFA8_eotS = "\44\uffff";
    static final String DFA8_eofS = "\1\2\43\uffff";
    static final String DFA8_minS = "\1\21\1\0\42\uffff";
    static final String DFA8_maxS = "\1\64\1\0\42\uffff";
    static final String DFA8_acceptS = "\2\uffff\1\2\40\uffff\1\1";
    static final String DFA8_specialS = "\1\uffff\1\0\42\uffff}>";
    static final String[] DFA8_transitionS = {
        "\1\2\1\uffff\2\2\1\uffff\10\2\1\1\3\2\4\uffff\3\2\7\uffff\5" + "\2",
        "\1\uffff", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
        "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
        ""};

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
            return "220:15: ( ( ELSE )=> ELSE stat )?";
        }

        public int specialStateTransition(int s, IntStream _input)
            throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch (s) {
            case 0:
                int LA8_1 = input.LA(1);

                int index8_1 = input.index();
                input.rewind();
                s = -1;
                if ((synpred2_Ctrl())) {
                    s = 35;
                }

                else if ((true)) {
                    s = 2;
                }

                input.seek(index8_1);
                if (s >= 0) {
                    return s;
                }
                break;
            }
            if (CtrlParser.this.state.backtracking > 0) {
                CtrlParser.this.state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 8, _s, input);
            error(nvae);
            throw nvae;
        }
    }

    static final String DFA9_eotS = "\44\uffff";
    static final String DFA9_eofS = "\1\1\43\uffff";
    static final String DFA9_minS = "\1\21\23\uffff\1\0\17\uffff";
    static final String DFA9_maxS = "\1\64\23\uffff\1\0\17\uffff";
    static final String DFA9_acceptS = "\1\uffff\1\2\41\uffff\1\1";
    static final String DFA9_specialS = "\24\uffff\1\0\17\uffff}>";
    static final String[] DFA9_transitionS = {
        "\1\1\1\uffff\2\1\1\uffff\13\1\1\24\4\uffff\3\1\7\uffff\5\1", "", "",
        "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
        "\1\uffff", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min =
        DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max =
        DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }

        public String getDescription() {
            return "()+ loopback of 223:18: ( ( OR )=> OR stat )+";
        }

        public int specialStateTransition(int s, IntStream _input)
            throws NoViableAltException {
            TokenStream input = (TokenStream) _input;
            int _s = s;
            switch (s) {
            case 0:
                int LA9_20 = input.LA(1);

                int index9_20 = input.index();
                input.rewind();
                s = -1;
                if ((synpred3_Ctrl())) {
                    s = 35;
                }

                else if ((true)) {
                    s = 1;
                }

                input.seek(index9_20);
                if (s >= 0) {
                    return s;
                }
                break;
            }
            if (CtrlParser.this.state.backtracking > 0) {
                CtrlParser.this.state.failed = true;
                return -1;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 9, _s, input);
            error(nvae);
            throw nvae;
        }
    }

    public static final BitSet FOLLOW_package_decl_in_program141 = new BitSet(
        new long[] {0x001F01C1BEDB0000L});
    public static final BitSet FOLLOW_import_decl_in_program147 = new BitSet(
        new long[] {0x001F01C1BEDB0000L});
    public static final BitSet FOLLOW_function_in_program155 = new BitSet(
        new long[] {0x001F01C1BEDA0000L});
    public static final BitSet FOLLOW_recipe_in_program157 = new BitSet(
        new long[] {0x001F01C1BEDA0000L});
    public static final BitSet FOLLOW_stat_in_program159 = new BitSet(
        new long[] {0x001F01C1BEDA0000L});
    public static final BitSet FOLLOW_EOF_in_program163 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_package_decl295 = new BitSet(
        new long[] {0x0000018000120000L});
    public static final BitSet FOLLOW_qual_name_in_package_decl297 =
        new BitSet(new long[] {0x0000000000008000L});
    public static final BitSet FOLLOW_SEMI_in_package_decl299 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_decl363 = new BitSet(
        new long[] {0x0000018000120000L});
    public static final BitSet FOLLOW_qual_name_in_import_decl366 = new BitSet(
        new long[] {0x0000000000008000L});
    public static final BitSet FOLLOW_SEMI_in_import_decl368 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_qual_name392 = new BitSet(
        new long[] {0x0000000000040002L});
    public static final BitSet FOLLOW_DOT_in_qual_name395 = new BitSet(
        new long[] {0x0000000000020000L});
    public static final BitSet FOLLOW_ID_in_qual_name399 = new BitSet(
        new long[] {0x0000000000040002L});
    public static final BitSet FOLLOW_RECIPE_in_recipe440 = new BitSet(
        new long[] {0x0000000000020000L});
    public static final BitSet FOLLOW_ID_in_recipe443 = new BitSet(
        new long[] {0x0000000000100000L});
    public static final BitSet FOLLOW_LPAR_in_recipe445 = new BitSet(
        new long[] {0x0000000000200000L});
    public static final BitSet FOLLOW_RPAR_in_recipe448 = new BitSet(
        new long[] {0x0000000000800000L});
    public static final BitSet FOLLOW_block_in_recipe465 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function496 = new BitSet(
        new long[] {0x0000000000020000L});
    public static final BitSet FOLLOW_ID_in_function499 = new BitSet(
        new long[] {0x0000000000100000L});
    public static final BitSet FOLLOW_LPAR_in_function501 = new BitSet(
        new long[] {0x0000000000200000L});
    public static final BitSet FOLLOW_RPAR_in_function504 = new BitSet(
        new long[] {0x0000000000800000L});
    public static final BitSet FOLLOW_block_in_function507 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_LCURLY_in_block538 = new BitSet(
        new long[] {0x001F01C1BFDA0000L});
    public static final BitSet FOLLOW_stat_in_block540 = new BitSet(
        new long[] {0x001F01C1BFDA0000L});
    public static final BitSet FOLLOW_RCURLY_in_block543 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_stat567 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat584 = new BitSet(
        new long[] {0x001F01C1BEDA0000L});
    public static final BitSet FOLLOW_stat_in_stat587 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_stat608 = new BitSet(
        new long[] {0x0000000000100000L});
    public static final BitSet FOLLOW_LPAR_in_stat611 = new BitSet(
        new long[] {0x0000018800120000L});
    public static final BitSet FOLLOW_cond_in_stat614 = new BitSet(
        new long[] {0x0000000000200000L});
    public static final BitSet FOLLOW_RPAR_in_stat616 = new BitSet(
        new long[] {0x001F01C1BEDA0000L});
    public static final BitSet FOLLOW_stat_in_stat619 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat639 = new BitSet(
        new long[] {0x0000000000100000L});
    public static final BitSet FOLLOW_LPAR_in_stat642 = new BitSet(
        new long[] {0x0000018800120000L});
    public static final BitSet FOLLOW_cond_in_stat645 = new BitSet(
        new long[] {0x0000000000200000L});
    public static final BitSet FOLLOW_RPAR_in_stat647 = new BitSet(
        new long[] {0x001F01C1BEDA0000L});
    public static final BitSet FOLLOW_stat_in_stat650 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_stat655 = new BitSet(
        new long[] {0x001F01C1BEDA0000L});
    public static final BitSet FOLLOW_stat_in_stat657 = new BitSet(
        new long[] {0x000000000C000000L});
    public static final BitSet FOLLOW_WHILE_in_stat700 = new BitSet(
        new long[] {0x0000000000100000L});
    public static final BitSet FOLLOW_LPAR_in_stat702 = new BitSet(
        new long[] {0x0000018800120000L});
    public static final BitSet FOLLOW_cond_in_stat704 = new BitSet(
        new long[] {0x0000000000200000L});
    public static final BitSet FOLLOW_RPAR_in_stat706 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat769 = new BitSet(
        new long[] {0x0000000000100000L});
    public static final BitSet FOLLOW_LPAR_in_stat771 = new BitSet(
        new long[] {0x0000018800120000L});
    public static final BitSet FOLLOW_cond_in_stat773 = new BitSet(
        new long[] {0x0000000000200000L});
    public static final BitSet FOLLOW_RPAR_in_stat775 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_stat822 = new BitSet(
        new long[] {0x0000000000100000L});
    public static final BitSet FOLLOW_LPAR_in_stat825 = new BitSet(
        new long[] {0x0000018800120000L});
    public static final BitSet FOLLOW_cond_in_stat828 = new BitSet(
        new long[] {0x0000000000200000L});
    public static final BitSet FOLLOW_RPAR_in_stat830 = new BitSet(
        new long[] {0x001F01C1BEDA0000L});
    public static final BitSet FOLLOW_stat_in_stat833 = new BitSet(
        new long[] {0x0000000040000002L});
    public static final BitSet FOLLOW_ELSE_in_stat843 = new BitSet(
        new long[] {0x001F01C1BEDA0000L});
    public static final BitSet FOLLOW_stat_in_stat846 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_stat870 = new BitSet(
        new long[] {0x001F01C1BEDA0000L});
    public static final BitSet FOLLOW_stat_in_stat873 = new BitSet(
        new long[] {0x0000000040000002L});
    public static final BitSet FOLLOW_ELSE_in_stat883 = new BitSet(
        new long[] {0x001F01C1BEDA0000L});
    public static final BitSet FOLLOW_stat_in_stat886 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_stat905 = new BitSet(
        new long[] {0x001F01C1BEDA0000L});
    public static final BitSet FOLLOW_stat_in_stat908 = new BitSet(
        new long[] {0x0000000200000000L});
    public static final BitSet FOLLOW_OR_in_stat918 = new BitSet(
        new long[] {0x001F01C1BEDA0000L});
    public static final BitSet FOLLOW_stat_in_stat921 = new BitSet(
        new long[] {0x0000000200000002L});
    public static final BitSet FOLLOW_expr_in_stat936 = new BitSet(
        new long[] {0x0000000000008000L});
    public static final BitSet FOLLOW_SEMI_in_stat938 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat952 = new BitSet(
        new long[] {0x0000000000008000L});
    public static final BitSet FOLLOW_SEMI_in_stat954 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_cond_atom_in_cond978 = new BitSet(
        new long[] {0x0000000400000002L});
    public static final BitSet FOLLOW_BAR_in_cond987 = new BitSet(
        new long[] {0x0000018800120000L});
    public static final BitSet FOLLOW_cond_atom_in_cond989 = new BitSet(
        new long[] {0x0000000400000002L});
    public static final BitSet FOLLOW_TRUE_in_cond_atom1035 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_cond_atom1056 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_expr2_in_expr1086 = new BitSet(
        new long[] {0x0000000400000002L});
    public static final BitSet FOLLOW_BAR_in_expr1094 = new BitSet(
        new long[] {0x000001C000120000L});
    public static final BitSet FOLLOW_expr2_in_expr1096 = new BitSet(
        new long[] {0x0000000400000002L});
    public static final BitSet FOLLOW_expr_atom_in_expr21177 = new BitSet(
        new long[] {0x0000003000000002L});
    public static final BitSet FOLLOW_PLUS_in_expr21185 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERISK_in_expr21209 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expr21261 = new BitSet(
        new long[] {0x0000018000120000L});
    public static final BitSet FOLLOW_expr_atom_in_expr21263 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expr_atom1291 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expr_atom1308 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_atom1321 = new BitSet(
        new long[] {0x000001C000120000L});
    public static final BitSet FOLLOW_expr_in_expr_atom1324 = new BitSet(
        new long[] {0x0000000000200000L});
    public static final BitSet FOLLOW_RPAR_in_expr_atom1326 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expr_atom1340 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_rule_name_in_call1370 = new BitSet(
        new long[] {0x0000000000100002L});
    public static final BitSet FOLLOW_arg_list_in_call1372 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_arg_list1412 = new BitSet(
        new long[] {0x0000FC0800220000L});
    public static final BitSet FOLLOW_arg_in_arg_list1415 = new BitSet(
        new long[] {0x0000020000200000L});
    public static final BitSet FOLLOW_COMMA_in_arg_list1418 = new BitSet(
        new long[] {0x0000FC0800020000L});
    public static final BitSet FOLLOW_arg_in_arg_list1420 = new BitSet(
        new long[] {0x0000020000200000L});
    public static final BitSet FOLLOW_RPAR_in_arg_list1426 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_arg1469 = new BitSet(
        new long[] {0x0000000000020000L});
    public static final BitSet FOLLOW_ID_in_arg1471 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_arg1502 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg1531 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_arg1548 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_literal0 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_qual_name_in_rule_name1658 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_var_type_in_var_decl1688 = new BitSet(
        new long[] {0x0000000000020000L});
    public static final BitSet FOLLOW_ID_in_var_decl1690 = new BitSet(
        new long[] {0x0000020000000002L});
    public static final BitSet FOLLOW_COMMA_in_var_decl1693 = new BitSet(
        new long[] {0x0000000000020000L});
    public static final BitSet FOLLOW_ID_in_var_decl1695 = new BitSet(
        new long[] {0x0000020000000002L});
    public static final BitSet FOLLOW_set_in_var_type0 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred1_Ctrl838 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred2_Ctrl878 = new BitSet(
        new long[] {0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_synpred3_Ctrl913 = new BitSet(
        new long[] {0x0000000000000002L});

}