// $ANTLR 3.4 E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g 2012-05-19 20:47:40

package groove.control.parse;
import groove.control.*;
import groove.control.CtrlCall.Kind;
import groove.algebra.AlgebraFamily;
import groove.view.FormatErrorSet;
import java.util.LinkedList;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class CtrlParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALAP", "AMP", "ANY", "ARG", "ARGS", "ASTERISK", "BAR", "BLOCK", "BOOL", "BSLASH", "CALL", "CHOICE", "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", "ELSE", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF", "IMPORT", "INT", "INT_LIT", "IntegerNumber", "LCURLY", "LPAR", "MINUS", "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT", "PACKAGE", "PLUS", "PRIORITY", "PROGRAM", "QUOTE", "RCURLY", "REAL", "REAL_LIT", "RECIPE", "RECIPES", "RPAR", "SEMI", "SHARP", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", "TRUE", "TRY", "UNTIL", "VAR", "WHILE", "WS"
    };

    public static final int EOF=-1;
    public static final int ALAP=4;
    public static final int AMP=5;
    public static final int ANY=6;
    public static final int ARG=7;
    public static final int ARGS=8;
    public static final int ASTERISK=9;
    public static final int BAR=10;
    public static final int BLOCK=11;
    public static final int BOOL=12;
    public static final int BSLASH=13;
    public static final int CALL=14;
    public static final int CHOICE=15;
    public static final int COMMA=16;
    public static final int DO=17;
    public static final int DONT_CARE=18;
    public static final int DOT=19;
    public static final int DO_UNTIL=20;
    public static final int DO_WHILE=21;
    public static final int ELSE=22;
    public static final int EscapeSequence=23;
    public static final int FALSE=24;
    public static final int FUNCTION=25;
    public static final int FUNCTIONS=26;
    public static final int ID=27;
    public static final int IF=28;
    public static final int IMPORT=29;
    public static final int INT=30;
    public static final int INT_LIT=31;
    public static final int IntegerNumber=32;
    public static final int LCURLY=33;
    public static final int LPAR=34;
    public static final int MINUS=35;
    public static final int ML_COMMENT=36;
    public static final int NODE=37;
    public static final int NOT=38;
    public static final int NonIntegerNumber=39;
    public static final int OR=40;
    public static final int OTHER=41;
    public static final int OUT=42;
    public static final int PACKAGE=43;
    public static final int PLUS=44;
    public static final int PRIORITY=45;
    public static final int PROGRAM=46;
    public static final int QUOTE=47;
    public static final int RCURLY=48;
    public static final int REAL=49;
    public static final int REAL_LIT=50;
    public static final int RECIPE=51;
    public static final int RECIPES=52;
    public static final int RPAR=53;
    public static final int SEMI=54;
    public static final int SHARP=55;
    public static final int SL_COMMENT=56;
    public static final int STAR=57;
    public static final int STRING=58;
    public static final int STRING_LIT=59;
    public static final int TRUE=60;
    public static final int TRY=61;
    public static final int UNTIL=62;
    public static final int VAR=63;
    public static final int WHILE=64;
    public static final int WS=65;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

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
    return adaptor;
}
    public String[] getTokenNames() { return CtrlParser.tokenNames; }
    public String getGrammarFileName() { return "E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g"; }


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
        public CtrlTree run(CharStream input, Namespace namespace, AlgebraFamily family) throws RecognitionException {
            this.helper = new CtrlHelper(this, namespace, family);
            lexer.setCharStream(input);
            lexer.setHelper(this.helper);
            setTokenStream(new CommonTokenStream(lexer));
            setTreeAdaptor(new CtrlTreeAdaptor());
            return (CtrlTree) program().getTree();
        }


    public static class program_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "program"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:106:1: program : package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM ( package_decl )? ( import_decl )* ^( RECIPES ( recipe )* ) ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) ) ;
    public final CtrlParser.program_return program() throws RecognitionException {
        CtrlParser.program_return retval = new CtrlParser.program_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token EOF6=null;
        CtrlParser.package_decl_return package_decl1 =null;

        CtrlParser.import_decl_return import_decl2 =null;

        CtrlParser.function_return function3 =null;

        CtrlParser.recipe_return recipe4 =null;

        CtrlParser.stat_return stat5 =null;


        CommonTree EOF6_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_package_decl=new RewriteRuleSubtreeStream(adaptor,"rule package_decl");
        RewriteRuleSubtreeStream stream_recipe=new RewriteRuleSubtreeStream(adaptor,"rule recipe");
        RewriteRuleSubtreeStream stream_import_decl=new RewriteRuleSubtreeStream(adaptor,"rule import_decl");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        RewriteRuleSubtreeStream stream_function=new RewriteRuleSubtreeStream(adaptor,"rule function");
        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:107:3: ( package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM ( package_decl )? ( import_decl )* ^( RECIPES ( recipe )* ) ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) ) )
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:111:5: package_decl ( import_decl )* ( function | recipe | stat )* EOF
            {
            pushFollow(FOLLOW_package_decl_in_program141);
            package_decl1=package_decl();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_package_decl.add(package_decl1.getTree());

            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:112:5: ( import_decl )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==IMPORT) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:112:5: import_decl
            	    {
            	    pushFollow(FOLLOW_import_decl_in_program147);
            	    import_decl2=import_decl();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_import_decl.add(import_decl2.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:113:5: ( function | recipe | stat )*
            loop2:
            do {
                int alt2=4;
                switch ( input.LA(1) ) {
                case FUNCTION:
                    {
                    alt2=1;
                    }
                    break;
                case RECIPE:
                    {
                    alt2=2;
                    }
                    break;
                case ALAP:
                case ANY:
                case BOOL:
                case CHOICE:
                case DO:
                case ID:
                case IF:
                case INT:
                case LCURLY:
                case LPAR:
                case NODE:
                case OTHER:
                case REAL:
                case SHARP:
                case STRING:
                case TRY:
                case UNTIL:
                case WHILE:
                    {
                    alt2=3;
                    }
                    break;

                }

                switch (alt2) {
            	case 1 :
            	    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:113:6: function
            	    {
            	    pushFollow(FOLLOW_function_in_program155);
            	    function3=function();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_function.add(function3.getTree());

            	    }
            	    break;
            	case 2 :
            	    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:113:15: recipe
            	    {
            	    pushFollow(FOLLOW_recipe_in_program157);
            	    recipe4=recipe();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_recipe.add(recipe4.getTree());

            	    }
            	    break;
            	case 3 :
            	    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:113:22: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_program159);
            	    stat5=stat();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_stat.add(stat5.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            EOF6=(Token)match(input,EOF,FOLLOW_EOF_in_program163); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EOF.add(EOF6);


            if ( state.backtracking==0 ) { helper.checkEOF(EOF6_tree); }

            // AST REWRITE
            // elements: function, stat, recipe, package_decl, import_decl
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 115:5: -> ^( PROGRAM ( package_decl )? ( import_decl )* ^( RECIPES ( recipe )* ) ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) )
            {
                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:115:8: ^( PROGRAM ( package_decl )? ( import_decl )* ^( RECIPES ( recipe )* ) ^( FUNCTIONS ( function )* ) ^( BLOCK ( stat )* ) )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(PROGRAM, "PROGRAM")
                , root_1);

                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:116:11: ( package_decl )?
                if ( stream_package_decl.hasNext() ) {
                    adaptor.addChild(root_1, stream_package_decl.nextTree());

                }
                stream_package_decl.reset();

                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:117:11: ( import_decl )*
                while ( stream_import_decl.hasNext() ) {
                    adaptor.addChild(root_1, stream_import_decl.nextTree());

                }
                stream_import_decl.reset();

                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:118:11: ^( RECIPES ( recipe )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(RECIPES, "RECIPES")
                , root_2);

                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:118:21: ( recipe )*
                while ( stream_recipe.hasNext() ) {
                    adaptor.addChild(root_2, stream_recipe.nextTree());

                }
                stream_recipe.reset();

                adaptor.addChild(root_1, root_2);
                }

                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:119:11: ^( FUNCTIONS ( function )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(FUNCTIONS, "FUNCTIONS")
                , root_2);

                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:119:23: ( function )*
                while ( stream_function.hasNext() ) {
                    adaptor.addChild(root_2, stream_function.nextTree());

                }
                stream_function.reset();

                adaptor.addChild(root_1, root_2);
                }

                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:120:11: ^( BLOCK ( stat )* )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                , root_2);

                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:120:19: ( stat )*
                while ( stream_stat.hasNext() ) {
                    adaptor.addChild(root_2, stream_stat.nextTree());

                }
                stream_stat.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "program"


    public static class package_decl_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "package_decl"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:125:1: package_decl : ( PACKAGE qual_name SEMI -> ^( PACKAGE qual_name ) | ->) ;
    public final CtrlParser.package_decl_return package_decl() throws RecognitionException {
        CtrlParser.package_decl_return retval = new CtrlParser.package_decl_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token PACKAGE7=null;
        Token SEMI9=null;
        CtrlParser.qual_name_return qual_name8 =null;


        CommonTree PACKAGE7_tree=null;
        CommonTree SEMI9_tree=null;
        RewriteRuleTokenStream stream_PACKAGE=new RewriteRuleTokenStream(adaptor,"token PACKAGE");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleSubtreeStream stream_qual_name=new RewriteRuleSubtreeStream(adaptor,"rule qual_name");
        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:126:3: ( ( PACKAGE qual_name SEMI -> ^( PACKAGE qual_name ) | ->) )
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:128:5: ( PACKAGE qual_name SEMI -> ^( PACKAGE qual_name ) | ->)
            {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:128:5: ( PACKAGE qual_name SEMI -> ^( PACKAGE qual_name ) | ->)
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==PACKAGE) ) {
                alt3=1;
            }
            else if ( (LA3_0==EOF||LA3_0==ALAP||LA3_0==ANY||LA3_0==BOOL||LA3_0==CHOICE||LA3_0==DO||LA3_0==FUNCTION||(LA3_0 >= ID && LA3_0 <= INT)||(LA3_0 >= LCURLY && LA3_0 <= LPAR)||LA3_0==NODE||LA3_0==OTHER||LA3_0==REAL||LA3_0==RECIPE||LA3_0==SHARP||LA3_0==STRING||(LA3_0 >= TRY && LA3_0 <= UNTIL)||LA3_0==WHILE) ) {
                alt3=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;

            }
            switch (alt3) {
                case 1 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:128:7: PACKAGE qual_name SEMI
                    {
                    PACKAGE7=(Token)match(input,PACKAGE,FOLLOW_PACKAGE_in_package_decl295); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PACKAGE.add(PACKAGE7);


                    pushFollow(FOLLOW_qual_name_in_package_decl297);
                    qual_name8=qual_name();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_qual_name.add(qual_name8.getTree());

                    SEMI9=(Token)match(input,SEMI,FOLLOW_SEMI_in_package_decl299); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMI.add(SEMI9);


                    if ( state.backtracking==0 ) { helper.setPackage((qual_name8!=null?((CommonTree)qual_name8.tree):null)); }

                    // AST REWRITE
                    // elements: PACKAGE, qual_name
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 130:7: -> ^( PACKAGE qual_name )
                    {
                        // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:130:10: ^( PACKAGE qual_name )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        stream_PACKAGE.nextNode()
                        , root_1);

                        adaptor.addChild(root_1, stream_qual_name.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:131:7: 
                    {
                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 131:7: ->
                    {
                        adaptor.addChild(root_0,  helper.emptyPackage() );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "package_decl"


    public static class import_decl_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "import_decl"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:136:1: import_decl : IMPORT ^ qual_name SEMI !;
    public final CtrlParser.import_decl_return import_decl() throws RecognitionException {
        CtrlParser.import_decl_return retval = new CtrlParser.import_decl_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token IMPORT10=null;
        Token SEMI12=null;
        CtrlParser.qual_name_return qual_name11 =null;


        CommonTree IMPORT10_tree=null;
        CommonTree SEMI12_tree=null;

        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:137:3: ( IMPORT ^ qual_name SEMI !)
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:139:5: IMPORT ^ qual_name SEMI !
            {
            root_0 = (CommonTree)adaptor.nil();


            IMPORT10=(Token)match(input,IMPORT,FOLLOW_IMPORT_in_import_decl363); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IMPORT10_tree = 
            (CommonTree)adaptor.create(IMPORT10)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(IMPORT10_tree, root_0);
            }

            pushFollow(FOLLOW_qual_name_in_import_decl366);
            qual_name11=qual_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qual_name11.getTree());

            SEMI12=(Token)match(input,SEMI,FOLLOW_SEMI_in_import_decl368); if (state.failed) return retval;

            if ( state.backtracking==0 ) { helper.addImport((qual_name11!=null?((CommonTree)qual_name11.tree):null));
                }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "import_decl"


    public static class qual_name_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "qual_name"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:145:1: qual_name :ids+= ID ( DOT ids+= ID )* ->;
    public final CtrlParser.qual_name_return qual_name() throws RecognitionException {
        CtrlParser.qual_name_return retval = new CtrlParser.qual_name_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token DOT13=null;
        Token ids=null;
        List list_ids=null;

        CommonTree DOT13_tree=null;
        CommonTree ids_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");

        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:146:3: (ids+= ID ( DOT ids+= ID )* ->)
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:146:5: ids+= ID ( DOT ids+= ID )*
            {
            ids=(Token)match(input,ID,FOLLOW_ID_in_qual_name392); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ids);

            if (list_ids==null) list_ids=new ArrayList();
            list_ids.add(ids);


            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:146:13: ( DOT ids+= ID )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==DOT) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:146:14: DOT ids+= ID
            	    {
            	    DOT13=(Token)match(input,DOT,FOLLOW_DOT_in_qual_name395); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(DOT13);


            	    ids=(Token)match(input,ID,FOLLOW_ID_in_qual_name399); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ids);

            	    if (list_ids==null) list_ids=new ArrayList();
            	    list_ids.add(ids);


            	    }
            	    break;

            	default :
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
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 147:5: ->
            {
                adaptor.addChild(root_0,  helper.toQualName(list_ids) );

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "qual_name"


    public static class recipe_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipe"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:153:1: recipe : RECIPE ^ ID LPAR ! RPAR ! block ;
    public final CtrlParser.recipe_return recipe() throws RecognitionException {
        CtrlParser.recipe_return retval = new CtrlParser.recipe_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token RECIPE14=null;
        Token ID15=null;
        Token LPAR16=null;
        Token RPAR17=null;
        CtrlParser.block_return block18 =null;


        CommonTree RECIPE14_tree=null;
        CommonTree ID15_tree=null;
        CommonTree LPAR16_tree=null;
        CommonTree RPAR17_tree=null;

        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:154:3: ( RECIPE ^ ID LPAR ! RPAR ! block )
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:156:5: RECIPE ^ ID LPAR ! RPAR ! block
            {
            root_0 = (CommonTree)adaptor.nil();


            if ( state.backtracking==0 ) { lexer.startRecord(); }

            RECIPE14=(Token)match(input,RECIPE,FOLLOW_RECIPE_in_recipe440); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RECIPE14_tree = 
            (CommonTree)adaptor.create(RECIPE14)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(RECIPE14_tree, root_0);
            }

            ID15=(Token)match(input,ID,FOLLOW_ID_in_recipe443); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID15_tree = 
            (CommonTree)adaptor.create(ID15)
            ;
            adaptor.addChild(root_0, ID15_tree);
            }

            LPAR16=(Token)match(input,LPAR,FOLLOW_LPAR_in_recipe445); if (state.failed) return retval;

            RPAR17=(Token)match(input,RPAR,FOLLOW_RPAR_in_recipe448); if (state.failed) return retval;

            pushFollow(FOLLOW_block_in_recipe465);
            block18=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block18.getTree());

            if ( state.backtracking==0 ) { helper.declareName(RECIPE14_tree, lexer.getRecord()); }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "recipe"


    public static class function_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:168:1: function : FUNCTION ^ ID LPAR ! RPAR ! block ;
    public final CtrlParser.function_return function() throws RecognitionException {
        CtrlParser.function_return retval = new CtrlParser.function_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token FUNCTION19=null;
        Token ID20=null;
        Token LPAR21=null;
        Token RPAR22=null;
        CtrlParser.block_return block23 =null;


        CommonTree FUNCTION19_tree=null;
        CommonTree ID20_tree=null;
        CommonTree LPAR21_tree=null;
        CommonTree RPAR22_tree=null;

        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:169:3: ( FUNCTION ^ ID LPAR ! RPAR ! block )
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:171:5: FUNCTION ^ ID LPAR ! RPAR ! block
            {
            root_0 = (CommonTree)adaptor.nil();


            FUNCTION19=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function496); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            FUNCTION19_tree = 
            (CommonTree)adaptor.create(FUNCTION19)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(FUNCTION19_tree, root_0);
            }

            ID20=(Token)match(input,ID,FOLLOW_ID_in_function499); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID20_tree = 
            (CommonTree)adaptor.create(ID20)
            ;
            adaptor.addChild(root_0, ID20_tree);
            }

            LPAR21=(Token)match(input,LPAR,FOLLOW_LPAR_in_function501); if (state.failed) return retval;

            RPAR22=(Token)match(input,RPAR,FOLLOW_RPAR_in_function504); if (state.failed) return retval;

            pushFollow(FOLLOW_block_in_function507);
            block23=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block23.getTree());

            if ( state.backtracking==0 ) { helper.declareName(FUNCTION19_tree, null); }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "function"


    public static class block_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "block"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:176:1: block : LCURLY ( stat )* RCURLY -> ^( BLOCK ( stat )* ) ;
    public final CtrlParser.block_return block() throws RecognitionException {
        CtrlParser.block_return retval = new CtrlParser.block_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token LCURLY24=null;
        Token RCURLY26=null;
        CtrlParser.stat_return stat25 =null;


        CommonTree LCURLY24_tree=null;
        CommonTree RCURLY26_tree=null;
        RewriteRuleTokenStream stream_LCURLY=new RewriteRuleTokenStream(adaptor,"token LCURLY");
        RewriteRuleTokenStream stream_RCURLY=new RewriteRuleTokenStream(adaptor,"token RCURLY");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:177:3: ( LCURLY ( stat )* RCURLY -> ^( BLOCK ( stat )* ) )
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:179:5: LCURLY ( stat )* RCURLY
            {
            LCURLY24=(Token)match(input,LCURLY,FOLLOW_LCURLY_in_block538); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LCURLY.add(LCURLY24);


            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:179:12: ( stat )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==ALAP||LA5_0==ANY||LA5_0==BOOL||LA5_0==CHOICE||LA5_0==DO||(LA5_0 >= ID && LA5_0 <= IF)||LA5_0==INT||(LA5_0 >= LCURLY && LA5_0 <= LPAR)||LA5_0==NODE||LA5_0==OTHER||LA5_0==REAL||LA5_0==SHARP||LA5_0==STRING||(LA5_0 >= TRY && LA5_0 <= UNTIL)||LA5_0==WHILE) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:179:12: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_block540);
            	    stat25=stat();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_stat.add(stat25.getTree());

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            RCURLY26=(Token)match(input,RCURLY,FOLLOW_RCURLY_in_block543); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RCURLY.add(RCURLY26);


            // AST REWRITE
            // elements: stat
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 179:25: -> ^( BLOCK ( stat )* )
            {
                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:179:28: ^( BLOCK ( stat )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                , root_1);

                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:179:36: ( stat )*
                while ( stream_stat.hasNext() ) {
                    adaptor.addChild(root_1, stream_stat.nextTree());

                }
                stream_stat.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "block"


    public static class stat_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "stat"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:182:1: stat : ( block | ALAP ^ stat | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI !| var_decl SEMI !);
    public final CtrlParser.stat_return stat() throws RecognitionException {
        CtrlParser.stat_return retval = new CtrlParser.stat_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ALAP28=null;
        Token WHILE30=null;
        Token LPAR31=null;
        Token RPAR33=null;
        Token UNTIL35=null;
        Token LPAR36=null;
        Token RPAR38=null;
        Token DO40=null;
        Token WHILE42=null;
        Token LPAR43=null;
        Token RPAR45=null;
        Token UNTIL46=null;
        Token LPAR47=null;
        Token RPAR49=null;
        Token IF50=null;
        Token LPAR51=null;
        Token RPAR53=null;
        Token ELSE55=null;
        Token TRY57=null;
        Token ELSE59=null;
        Token CHOICE61=null;
        Token OR63=null;
        Token SEMI66=null;
        Token SEMI68=null;
        CtrlParser.block_return block27 =null;

        CtrlParser.stat_return stat29 =null;

        CtrlParser.cond_return cond32 =null;

        CtrlParser.stat_return stat34 =null;

        CtrlParser.cond_return cond37 =null;

        CtrlParser.stat_return stat39 =null;

        CtrlParser.stat_return stat41 =null;

        CtrlParser.cond_return cond44 =null;

        CtrlParser.cond_return cond48 =null;

        CtrlParser.cond_return cond52 =null;

        CtrlParser.stat_return stat54 =null;

        CtrlParser.stat_return stat56 =null;

        CtrlParser.stat_return stat58 =null;

        CtrlParser.stat_return stat60 =null;

        CtrlParser.stat_return stat62 =null;

        CtrlParser.stat_return stat64 =null;

        CtrlParser.expr_return expr65 =null;

        CtrlParser.var_decl_return var_decl67 =null;


        CommonTree ALAP28_tree=null;
        CommonTree WHILE30_tree=null;
        CommonTree LPAR31_tree=null;
        CommonTree RPAR33_tree=null;
        CommonTree UNTIL35_tree=null;
        CommonTree LPAR36_tree=null;
        CommonTree RPAR38_tree=null;
        CommonTree DO40_tree=null;
        CommonTree WHILE42_tree=null;
        CommonTree LPAR43_tree=null;
        CommonTree RPAR45_tree=null;
        CommonTree UNTIL46_tree=null;
        CommonTree LPAR47_tree=null;
        CommonTree RPAR49_tree=null;
        CommonTree IF50_tree=null;
        CommonTree LPAR51_tree=null;
        CommonTree RPAR53_tree=null;
        CommonTree ELSE55_tree=null;
        CommonTree TRY57_tree=null;
        CommonTree ELSE59_tree=null;
        CommonTree CHOICE61_tree=null;
        CommonTree OR63_tree=null;
        CommonTree SEMI66_tree=null;
        CommonTree SEMI68_tree=null;
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_WHILE=new RewriteRuleTokenStream(adaptor,"token WHILE");
        RewriteRuleTokenStream stream_UNTIL=new RewriteRuleTokenStream(adaptor,"token UNTIL");
        RewriteRuleSubtreeStream stream_cond=new RewriteRuleSubtreeStream(adaptor,"rule cond");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:183:2: ( block | ALAP ^ stat | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI !| var_decl SEMI !)
            int alt10=10;
            switch ( input.LA(1) ) {
            case LCURLY:
                {
                alt10=1;
                }
                break;
            case ALAP:
                {
                alt10=2;
                }
                break;
            case WHILE:
                {
                alt10=3;
                }
                break;
            case UNTIL:
                {
                alt10=4;
                }
                break;
            case DO:
                {
                alt10=5;
                }
                break;
            case IF:
                {
                alt10=6;
                }
                break;
            case TRY:
                {
                alt10=7;
                }
                break;
            case CHOICE:
                {
                alt10=8;
                }
                break;
            case ANY:
            case ID:
            case LPAR:
            case OTHER:
            case SHARP:
                {
                alt10=9;
                }
                break;
            case BOOL:
            case INT:
            case NODE:
            case REAL:
            case STRING:
                {
                alt10=10;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;

            }

            switch (alt10) {
                case 1 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:184:4: block
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_block_in_stat567);
                    block27=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block27.getTree());

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:188:4: ALAP ^ stat
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    ALAP28=(Token)match(input,ALAP,FOLLOW_ALAP_in_stat584); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ALAP28_tree = 
                    (CommonTree)adaptor.create(ALAP28)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(ALAP28_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat587);
                    stat29=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat29.getTree());

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:193:4: WHILE ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    WHILE30=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat608); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    WHILE30_tree = 
                    (CommonTree)adaptor.create(WHILE30)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(WHILE30_tree, root_0);
                    }

                    LPAR31=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat611); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat614);
                    cond32=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond32.getTree());

                    RPAR33=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat616); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat619);
                    stat34=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat34.getTree());

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:197:5: UNTIL ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    UNTIL35=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat639); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    UNTIL35_tree = 
                    (CommonTree)adaptor.create(UNTIL35)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(UNTIL35_tree, root_0);
                    }

                    LPAR36=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat642); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat645);
                    cond37=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond37.getTree());

                    RPAR38=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat647); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat650);
                    stat39=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat39.getTree());

                    }
                    break;
                case 5 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:198:4: DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                    {
                    DO40=(Token)match(input,DO,FOLLOW_DO_in_stat655); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DO.add(DO40);


                    pushFollow(FOLLOW_stat_in_stat657);
                    stat41=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_stat.add(stat41.getTree());

                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:199:4: ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==WHILE) ) {
                        alt6=1;
                    }
                    else if ( (LA6_0==UNTIL) ) {
                        alt6=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 6, 0, input);

                        throw nvae;

                    }
                    switch (alt6) {
                        case 1 :
                            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:204:7: WHILE LPAR cond RPAR
                            {
                            WHILE42=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat700); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_WHILE.add(WHILE42);


                            LPAR43=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat702); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR43);


                            pushFollow(FOLLOW_cond_in_stat704);
                            cond44=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond44.getTree());

                            RPAR45=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat706); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR45);


                            // AST REWRITE
                            // elements: stat, cond, WHILE, stat
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {

                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 204:28: -> ^( BLOCK stat ^( WHILE cond stat ) )
                            {
                                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:204:31: ^( BLOCK stat ^( WHILE cond stat ) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(
                                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:204:44: ^( WHILE cond stat )
                                {
                                CommonTree root_2 = (CommonTree)adaptor.nil();
                                root_2 = (CommonTree)adaptor.becomeRoot(
                                stream_WHILE.nextNode()
                                , root_2);

                                adaptor.addChild(root_2, stream_cond.nextTree());

                                adaptor.addChild(root_2, stream_stat.nextTree());

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_0, root_1);
                                }

                            }


                            retval.tree = root_0;
                            }

                            }
                            break;
                        case 2 :
                            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:211:5: UNTIL LPAR cond RPAR
                            {
                            UNTIL46=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat769); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_UNTIL.add(UNTIL46);


                            LPAR47=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat771); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR47);


                            pushFollow(FOLLOW_cond_in_stat773);
                            cond48=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond48.getTree());

                            RPAR49=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat775); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR49);


                            // AST REWRITE
                            // elements: stat, cond, stat, UNTIL
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {

                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 211:26: -> ^( BLOCK stat ^( UNTIL cond stat ) )
                            {
                                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:211:29: ^( BLOCK stat ^( UNTIL cond stat ) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(
                                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:211:42: ^( UNTIL cond stat )
                                {
                                CommonTree root_2 = (CommonTree)adaptor.nil();
                                root_2 = (CommonTree)adaptor.becomeRoot(
                                stream_UNTIL.nextNode()
                                , root_2);

                                adaptor.addChild(root_2, stream_cond.nextTree());

                                adaptor.addChild(root_2, stream_stat.nextTree());

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_0, root_1);
                                }

                            }


                            retval.tree = root_0;
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 6 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:217:5: IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    IF50=(Token)match(input,IF,FOLLOW_IF_in_stat822); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IF50_tree = 
                    (CommonTree)adaptor.create(IF50)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(IF50_tree, root_0);
                    }

                    LPAR51=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat825); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat828);
                    cond52=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond52.getTree());

                    RPAR53=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat830); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat833);
                    stat54=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat54.getTree());

                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:217:31: ( ( ELSE )=> ELSE ! stat )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==ELSE) ) {
                        int LA7_1 = input.LA(2);

                        if ( (synpred1_Ctrl()) ) {
                            alt7=1;
                        }
                    }
                    switch (alt7) {
                        case 1 :
                            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:217:33: ( ELSE )=> ELSE ! stat
                            {
                            ELSE55=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat843); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat846);
                            stat56=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat56.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 7 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:221:5: TRY ^ stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    TRY57=(Token)match(input,TRY,FOLLOW_TRY_in_stat870); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRY57_tree = 
                    (CommonTree)adaptor.create(TRY57)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(TRY57_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat873);
                    stat58=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat58.getTree());

                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:221:15: ( ( ELSE )=> ELSE ! stat )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==ELSE) ) {
                        int LA8_1 = input.LA(2);

                        if ( (synpred2_Ctrl()) ) {
                            alt8=1;
                        }
                    }
                    switch (alt8) {
                        case 1 :
                            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:221:17: ( ELSE )=> ELSE ! stat
                            {
                            ELSE59=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat883); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat886);
                            stat60=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat60.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:224:5: CHOICE ^ stat ( ( OR )=> OR ! stat )+
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    CHOICE61=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_stat905); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHOICE61_tree = 
                    (CommonTree)adaptor.create(CHOICE61)
                    ;
                    root_0 = (CommonTree)adaptor.becomeRoot(CHOICE61_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat908);
                    stat62=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat62.getTree());

                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:224:18: ( ( OR )=> OR ! stat )+
                    int cnt9=0;
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0==OR) ) {
                            int LA9_20 = input.LA(2);

                            if ( (synpred3_Ctrl()) ) {
                                alt9=1;
                            }


                        }


                        switch (alt9) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:224:20: ( OR )=> OR ! stat
                    	    {
                    	    OR63=(Token)match(input,OR,FOLLOW_OR_in_stat918); if (state.failed) return retval;

                    	    pushFollow(FOLLOW_stat_in_stat921);
                    	    stat64=stat();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat64.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt9 >= 1 ) break loop9;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(9, input);
                                throw eee;
                        }
                        cnt9++;
                    } while (true);


                    }
                    break;
                case 9 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:227:4: expr SEMI !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_expr_in_stat936);
                    expr65=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr65.getTree());

                    SEMI66=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat938); if (state.failed) return retval;

                    }
                    break;
                case 10 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:230:4: var_decl SEMI !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_var_decl_in_stat952);
                    var_decl67=var_decl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, var_decl67.getTree());

                    SEMI68=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat954); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "stat"


    public static class cond_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "cond"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:234:1: cond : cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) ;
    public final CtrlParser.cond_return cond() throws RecognitionException {
        CtrlParser.cond_return retval = new CtrlParser.cond_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token BAR70=null;
        CtrlParser.cond_atom_return cond_atom69 =null;

        CtrlParser.cond_atom_return cond_atom71 =null;


        CommonTree BAR70_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_cond_atom=new RewriteRuleSubtreeStream(adaptor,"rule cond_atom");
        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:235:2: ( cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) )
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:237:4: cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            {
            pushFollow(FOLLOW_cond_atom_in_cond978);
            cond_atom69=cond_atom();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom69.getTree());

            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:238:4: ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==BAR) ) {
                alt12=1;
            }
            else if ( (LA12_0==RPAR) ) {
                alt12=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;

            }
            switch (alt12) {
                case 1 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:238:6: ( BAR cond_atom )+
                    {
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:238:6: ( BAR cond_atom )+
                    int cnt11=0;
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0==BAR) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:238:7: BAR cond_atom
                    	    {
                    	    BAR70=(Token)match(input,BAR,FOLLOW_BAR_in_cond987); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR70);


                    	    pushFollow(FOLLOW_cond_atom_in_cond989);
                    	    cond_atom71=cond_atom();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom71.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt11 >= 1 ) break loop11;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(11, input);
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
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 238:23: -> ^( CHOICE cond_atom ( cond_atom )+ )
                    {
                        // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:238:26: ^( CHOICE cond_atom ( cond_atom )+ )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(CHOICE, "CHOICE")
                        , root_1);

                        adaptor.addChild(root_1, stream_cond_atom.nextTree());

                        if ( !(stream_cond_atom.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_cond_atom.hasNext() ) {
                            adaptor.addChild(root_1, stream_cond_atom.nextTree());

                        }
                        stream_cond_atom.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:239:6: 
                    {
                    // AST REWRITE
                    // elements: cond_atom
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 239:6: -> cond_atom
                    {
                        adaptor.addChild(root_0, stream_cond_atom.nextTree());

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "cond"


    public static class cond_atom_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "cond_atom"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:243:1: cond_atom : ( TRUE | call );
    public final CtrlParser.cond_atom_return cond_atom() throws RecognitionException {
        CtrlParser.cond_atom_return retval = new CtrlParser.cond_atom_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token TRUE72=null;
        CtrlParser.call_return call73 =null;


        CommonTree TRUE72_tree=null;

        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:244:2: ( TRUE | call )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==TRUE) ) {
                alt13=1;
            }
            else if ( (LA13_0==ID) ) {
                alt13=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;

            }
            switch (alt13) {
                case 1 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:246:4: TRUE
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    TRUE72=(Token)match(input,TRUE,FOLLOW_TRUE_in_cond_atom1035); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRUE72_tree = 
                    (CommonTree)adaptor.create(TRUE72)
                    ;
                    adaptor.addChild(root_0, TRUE72_tree);
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:250:5: call
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_cond_atom1056);
                    call73=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call73.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "cond_atom"


    public static class expr_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expr"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:253:1: expr : expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) ;
    public final CtrlParser.expr_return expr() throws RecognitionException {
        CtrlParser.expr_return retval = new CtrlParser.expr_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token BAR75=null;
        CtrlParser.expr2_return expr274 =null;

        CtrlParser.expr2_return expr276 =null;


        CommonTree BAR75_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_expr2=new RewriteRuleSubtreeStream(adaptor,"rule expr2");
        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:254:2: ( expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) )
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:258:4: expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            {
            pushFollow(FOLLOW_expr2_in_expr1086);
            expr274=expr2();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expr2.add(expr274.getTree());

            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:259:4: ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==BAR) ) {
                alt15=1;
            }
            else if ( ((LA15_0 >= RPAR && LA15_0 <= SEMI)) ) {
                alt15=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;

            }
            switch (alt15) {
                case 1 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:259:6: ( BAR expr2 )+
                    {
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:259:6: ( BAR expr2 )+
                    int cnt14=0;
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==BAR) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:259:7: BAR expr2
                    	    {
                    	    BAR75=(Token)match(input,BAR,FOLLOW_BAR_in_expr1094); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR75);


                    	    pushFollow(FOLLOW_expr2_in_expr1096);
                    	    expr276=expr2();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expr2.add(expr276.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt14 >= 1 ) break loop14;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(14, input);
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
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 259:19: -> ^( CHOICE expr2 ( expr2 )+ )
                    {
                        // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:259:22: ^( CHOICE expr2 ( expr2 )+ )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(CHOICE, "CHOICE")
                        , root_1);

                        adaptor.addChild(root_1, stream_expr2.nextTree());

                        if ( !(stream_expr2.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_expr2.hasNext() ) {
                            adaptor.addChild(root_1, stream_expr2.nextTree());

                        }
                        stream_expr2.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:260:6: 
                    {
                    // AST REWRITE
                    // elements: expr2
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 260:6: -> expr2
                    {
                        adaptor.addChild(root_0, stream_expr2.nextTree());

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "expr"


    public static class expr2_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expr2"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:264:1: expr2 : (e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e) | SHARP expr_atom -> ^( ALAP expr_atom ) );
    public final CtrlParser.expr2_return expr2() throws RecognitionException {
        CtrlParser.expr2_return retval = new CtrlParser.expr2_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token PLUS77=null;
        Token ASTERISK78=null;
        Token SHARP79=null;
        CtrlParser.expr_atom_return e =null;

        CtrlParser.expr_atom_return expr_atom80 =null;


        CommonTree PLUS77_tree=null;
        CommonTree ASTERISK78_tree=null;
        CommonTree SHARP79_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_SHARP=new RewriteRuleTokenStream(adaptor,"token SHARP");
        RewriteRuleTokenStream stream_ASTERISK=new RewriteRuleTokenStream(adaptor,"token ASTERISK");
        RewriteRuleSubtreeStream stream_expr_atom=new RewriteRuleSubtreeStream(adaptor,"rule expr_atom");
        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:265:3: (e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e) | SHARP expr_atom -> ^( ALAP expr_atom ) )
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==ANY||LA17_0==ID||LA17_0==LPAR||LA17_0==OTHER) ) {
                alt17=1;
            }
            else if ( (LA17_0==SHARP) ) {
                alt17=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;

            }
            switch (alt17) {
                case 1 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:273:5: e= expr_atom ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e)
                    {
                    pushFollow(FOLLOW_expr_atom_in_expr21177);
                    e=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(e.getTree());

                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:274:5: ( PLUS -> ^( BLOCK $e ^( STAR $e) ) | ASTERISK -> ^( STAR $e) | -> $e)
                    int alt16=3;
                    switch ( input.LA(1) ) {
                    case PLUS:
                        {
                        alt16=1;
                        }
                        break;
                    case ASTERISK:
                        {
                        alt16=2;
                        }
                        break;
                    case BAR:
                    case RPAR:
                    case SEMI:
                        {
                        alt16=3;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 16, 0, input);

                        throw nvae;

                    }

                    switch (alt16) {
                        case 1 :
                            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:274:7: PLUS
                            {
                            PLUS77=(Token)match(input,PLUS,FOLLOW_PLUS_in_expr21185); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_PLUS.add(PLUS77);


                            // AST REWRITE
                            // elements: e, e
                            // token labels: 
                            // rule labels: retval, e
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {

                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                            RewriteRuleSubtreeStream stream_e=new RewriteRuleSubtreeStream(adaptor,"rule e",e!=null?e.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 274:12: -> ^( BLOCK $e ^( STAR $e) )
                            {
                                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:274:15: ^( BLOCK $e ^( STAR $e) )
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(
                                (CommonTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_e.nextTree());

                                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:274:26: ^( STAR $e)
                                {
                                CommonTree root_2 = (CommonTree)adaptor.nil();
                                root_2 = (CommonTree)adaptor.becomeRoot(
                                (CommonTree)adaptor.create(STAR, "STAR")
                                , root_2);

                                adaptor.addChild(root_2, stream_e.nextTree());

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_0, root_1);
                                }

                            }


                            retval.tree = root_0;
                            }

                            }
                            break;
                        case 2 :
                            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:275:7: ASTERISK
                            {
                            ASTERISK78=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_expr21209); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ASTERISK.add(ASTERISK78);


                            // AST REWRITE
                            // elements: e
                            // token labels: 
                            // rule labels: retval, e
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {

                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                            RewriteRuleSubtreeStream stream_e=new RewriteRuleSubtreeStream(adaptor,"rule e",e!=null?e.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 275:16: -> ^( STAR $e)
                            {
                                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:275:19: ^( STAR $e)
                                {
                                CommonTree root_1 = (CommonTree)adaptor.nil();
                                root_1 = (CommonTree)adaptor.becomeRoot(
                                (CommonTree)adaptor.create(STAR, "STAR")
                                , root_1);

                                adaptor.addChild(root_1, stream_e.nextTree());

                                adaptor.addChild(root_0, root_1);
                                }

                            }


                            retval.tree = root_0;
                            }

                            }
                            break;
                        case 3 :
                            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:276:7: 
                            {
                            // AST REWRITE
                            // elements: e
                            // token labels: 
                            // rule labels: retval, e
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {

                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                            RewriteRuleSubtreeStream stream_e=new RewriteRuleSubtreeStream(adaptor,"rule e",e!=null?e.tree:null);

                            root_0 = (CommonTree)adaptor.nil();
                            // 276:7: -> $e
                            {
                                adaptor.addChild(root_0, stream_e.nextTree());

                            }


                            retval.tree = root_0;
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:282:5: SHARP expr_atom
                    {
                    SHARP79=(Token)match(input,SHARP,FOLLOW_SHARP_in_expr21261); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SHARP.add(SHARP79);


                    pushFollow(FOLLOW_expr_atom_in_expr21263);
                    expr_atom80=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(expr_atom80.getTree());

                    // AST REWRITE
                    // elements: expr_atom
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 282:21: -> ^( ALAP expr_atom )
                    {
                        // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:282:24: ^( ALAP expr_atom )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(ALAP, "ALAP")
                        , root_1);

                        adaptor.addChild(root_1, stream_expr_atom.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "expr2"


    public static class expr_atom_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expr_atom"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:285:1: expr_atom : ( ANY | OTHER | LPAR ! expr RPAR !| call );
    public final CtrlParser.expr_atom_return expr_atom() throws RecognitionException {
        CtrlParser.expr_atom_return retval = new CtrlParser.expr_atom_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ANY81=null;
        Token OTHER82=null;
        Token LPAR83=null;
        Token RPAR85=null;
        CtrlParser.expr_return expr84 =null;

        CtrlParser.call_return call86 =null;


        CommonTree ANY81_tree=null;
        CommonTree OTHER82_tree=null;
        CommonTree LPAR83_tree=null;
        CommonTree RPAR85_tree=null;

        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:286:2: ( ANY | OTHER | LPAR ! expr RPAR !| call )
            int alt18=4;
            switch ( input.LA(1) ) {
            case ANY:
                {
                alt18=1;
                }
                break;
            case OTHER:
                {
                alt18=2;
                }
                break;
            case LPAR:
                {
                alt18=3;
                }
                break;
            case ID:
                {
                alt18=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;

            }

            switch (alt18) {
                case 1 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:288:4: ANY
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    ANY81=(Token)match(input,ANY,FOLLOW_ANY_in_expr_atom1291); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ANY81_tree = 
                    (CommonTree)adaptor.create(ANY81)
                    ;
                    adaptor.addChild(root_0, ANY81_tree);
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:292:4: OTHER
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    OTHER82=(Token)match(input,OTHER,FOLLOW_OTHER_in_expr_atom1308); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OTHER82_tree = 
                    (CommonTree)adaptor.create(OTHER82)
                    ;
                    adaptor.addChild(root_0, OTHER82_tree);
                    }

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:295:4: LPAR ! expr RPAR !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    LPAR83=(Token)match(input,LPAR,FOLLOW_LPAR_in_expr_atom1321); if (state.failed) return retval;

                    pushFollow(FOLLOW_expr_in_expr_atom1324);
                    expr84=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr84.getTree());

                    RPAR85=(Token)match(input,RPAR,FOLLOW_RPAR_in_expr_atom1326); if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:298:4: call
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_expr_atom1340);
                    call86=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call86.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "expr_atom"


    public static class call_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "call"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:302:1: call : rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) ;
    public final CtrlParser.call_return call() throws RecognitionException {
        CtrlParser.call_return retval = new CtrlParser.call_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        CtrlParser.rule_name_return rule_name87 =null;

        CtrlParser.arg_list_return arg_list88 =null;


        RewriteRuleSubtreeStream stream_arg_list=new RewriteRuleSubtreeStream(adaptor,"rule arg_list");
        RewriteRuleSubtreeStream stream_rule_name=new RewriteRuleSubtreeStream(adaptor,"rule rule_name");
        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:303:2: ( rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) )
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:307:4: rule_name ( arg_list )?
            {
            pushFollow(FOLLOW_rule_name_in_call1370);
            rule_name87=rule_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_name.add(rule_name87.getTree());

            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:307:14: ( arg_list )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==LPAR) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:307:14: arg_list
                    {
                    pushFollow(FOLLOW_arg_list_in_call1372);
                    arg_list88=arg_list();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg_list.add(arg_list88.getTree());

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
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 308:4: -> ^( CALL[$rule_name.start] rule_name ( arg_list )? )
            {
                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:308:7: ^( CALL[$rule_name.start] rule_name ( arg_list )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(CALL, (rule_name87!=null?((Token)rule_name87.start):null))
                , root_1);

                adaptor.addChild(root_1, stream_rule_name.nextTree());

                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:308:42: ( arg_list )?
                if ( stream_arg_list.hasNext() ) {
                    adaptor.addChild(root_1, stream_arg_list.nextTree());

                }
                stream_arg_list.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "call"


    public static class arg_list_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg_list"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:314:1: arg_list : LPAR ( arg ( COMMA arg )* )? RPAR -> ^( ARGS ( arg )* ) ;
    public final CtrlParser.arg_list_return arg_list() throws RecognitionException {
        CtrlParser.arg_list_return retval = new CtrlParser.arg_list_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token LPAR89=null;
        Token COMMA91=null;
        Token RPAR93=null;
        CtrlParser.arg_return arg90 =null;

        CtrlParser.arg_return arg92 =null;


        CommonTree LPAR89_tree=null;
        CommonTree COMMA91_tree=null;
        CommonTree RPAR93_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_arg=new RewriteRuleSubtreeStream(adaptor,"rule arg");
        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:315:3: ( LPAR ( arg ( COMMA arg )* )? RPAR -> ^( ARGS ( arg )* ) )
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:317:5: LPAR ( arg ( COMMA arg )* )? RPAR
            {
            LPAR89=(Token)match(input,LPAR,FOLLOW_LPAR_in_arg_list1412); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAR.add(LPAR89);


            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:317:10: ( arg ( COMMA arg )* )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==DONT_CARE||LA21_0==FALSE||LA21_0==ID||LA21_0==INT_LIT||LA21_0==OUT||LA21_0==REAL_LIT||(LA21_0 >= STRING_LIT && LA21_0 <= TRUE)) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:317:11: arg ( COMMA arg )*
                    {
                    pushFollow(FOLLOW_arg_in_arg_list1415);
                    arg90=arg();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg.add(arg90.getTree());

                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:317:15: ( COMMA arg )*
                    loop20:
                    do {
                        int alt20=2;
                        int LA20_0 = input.LA(1);

                        if ( (LA20_0==COMMA) ) {
                            alt20=1;
                        }


                        switch (alt20) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:317:16: COMMA arg
                    	    {
                    	    COMMA91=(Token)match(input,COMMA,FOLLOW_COMMA_in_arg_list1418); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA91);


                    	    pushFollow(FOLLOW_arg_in_arg_list1420);
                    	    arg92=arg();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_arg.add(arg92.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop20;
                        }
                    } while (true);


                    }
                    break;

            }


            RPAR93=(Token)match(input,RPAR,FOLLOW_RPAR_in_arg_list1426); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAR.add(RPAR93);


            // AST REWRITE
            // elements: arg
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 318:5: -> ^( ARGS ( arg )* )
            {
                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:318:8: ^( ARGS ( arg )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(ARGS, "ARGS")
                , root_1);

                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:318:15: ( arg )*
                while ( stream_arg.hasNext() ) {
                    adaptor.addChild(root_1, stream_arg.nextTree());

                }
                stream_arg.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "arg_list"


    public static class arg_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:324:1: arg : ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) );
    public final CtrlParser.arg_return arg() throws RecognitionException {
        CtrlParser.arg_return retval = new CtrlParser.arg_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token OUT94=null;
        Token ID95=null;
        Token ID96=null;
        Token DONT_CARE97=null;
        CtrlParser.literal_return literal98 =null;


        CommonTree OUT94_tree=null;
        CommonTree ID95_tree=null;
        CommonTree ID96_tree=null;
        CommonTree DONT_CARE97_tree=null;
        RewriteRuleTokenStream stream_DONT_CARE=new RewriteRuleTokenStream(adaptor,"token DONT_CARE");
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");
        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:325:3: ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) )
            int alt22=4;
            switch ( input.LA(1) ) {
            case OUT:
                {
                alt22=1;
                }
                break;
            case ID:
                {
                alt22=2;
                }
                break;
            case DONT_CARE:
                {
                alt22=3;
                }
                break;
            case FALSE:
            case INT_LIT:
            case REAL_LIT:
            case STRING_LIT:
            case TRUE:
                {
                alt22=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;

            }

            switch (alt22) {
                case 1 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:328:5: OUT ID
                    {
                    OUT94=(Token)match(input,OUT,FOLLOW_OUT_in_arg1469); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT94);


                    ID95=(Token)match(input,ID,FOLLOW_ID_in_arg1471); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID95);


                    // AST REWRITE
                    // elements: ID, OUT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 328:12: -> ^( ARG OUT ID )
                    {
                        // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:328:15: ^( ARG OUT ID )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(ARG, "ARG")
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_OUT.nextNode()
                        );

                        adaptor.addChild(root_1, 
                        stream_ID.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:332:5: ID
                    {
                    ID96=(Token)match(input,ID,FOLLOW_ID_in_arg1502); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID96);


                    // AST REWRITE
                    // elements: ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 332:8: -> ^( ARG ID )
                    {
                        // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:332:11: ^( ARG ID )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(ARG, "ARG")
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_ID.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:336:5: DONT_CARE
                    {
                    DONT_CARE97=(Token)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg1531); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DONT_CARE.add(DONT_CARE97);


                    // AST REWRITE
                    // elements: DONT_CARE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 336:15: -> ^( ARG DONT_CARE )
                    {
                        // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:336:18: ^( ARG DONT_CARE )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(ARG, "ARG")
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_DONT_CARE.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:337:5: literal
                    {
                    pushFollow(FOLLOW_literal_in_arg1548);
                    literal98=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_literal.add(literal98.getTree());

                    // AST REWRITE
                    // elements: literal
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 337:13: -> ^( ARG literal )
                    {
                        // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:337:16: ^( ARG literal )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(
                        (CommonTree)adaptor.create(ARG, "ARG")
                        , root_1);

                        adaptor.addChild(root_1, stream_literal.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "arg"


    public static class literal_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "literal"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:340:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlParser.literal_return literal() throws RecognitionException {
        CtrlParser.literal_return retval = new CtrlParser.literal_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set99=null;

        CommonTree set99_tree=null;

        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:341:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set99=(Token)input.LT(1);

            if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set99)
                );
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "literal"


    public static class rule_name_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "rule_name"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:362:1: rule_name : qual_name ->;
    public final CtrlParser.rule_name_return rule_name() throws RecognitionException {
        CtrlParser.rule_name_return retval = new CtrlParser.rule_name_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        CtrlParser.qual_name_return qual_name100 =null;


        RewriteRuleSubtreeStream stream_qual_name=new RewriteRuleSubtreeStream(adaptor,"rule qual_name");
        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:363:3: ( qual_name ->)
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:363:5: qual_name
            {
            pushFollow(FOLLOW_qual_name_in_rule_name1658);
            qual_name100=qual_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qual_name.add(qual_name100.getTree());

            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 364:5: ->
            {
                adaptor.addChild(root_0,  helper.lookup((qual_name100!=null?((CommonTree)qual_name100.tree):null)) );

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "rule_name"


    public static class var_decl_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_decl"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:368:1: var_decl : var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) ;
    public final CtrlParser.var_decl_return var_decl() throws RecognitionException {
        CtrlParser.var_decl_return retval = new CtrlParser.var_decl_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ID102=null;
        Token COMMA103=null;
        Token ID104=null;
        CtrlParser.var_type_return var_type101 =null;


        CommonTree ID102_tree=null;
        CommonTree COMMA103_tree=null;
        CommonTree ID104_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:369:2: ( var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) )
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:371:4: var_type ID ( COMMA ID )*
            {
            pushFollow(FOLLOW_var_type_in_var_decl1688);
            var_type101=var_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_var_type.add(var_type101.getTree());

            ID102=(Token)match(input,ID,FOLLOW_ID_in_var_decl1690); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID102);


            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:371:16: ( COMMA ID )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==COMMA) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:371:17: COMMA ID
            	    {
            	    COMMA103=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_decl1693); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA103);


            	    ID104=(Token)match(input,ID,FOLLOW_ID_in_var_decl1695); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ID104);


            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);


            // AST REWRITE
            // elements: ID, var_type
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 371:28: -> ^( VAR var_type ( ID )+ )
            {
                // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:371:31: ^( VAR var_type ( ID )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(
                (CommonTree)adaptor.create(VAR, "VAR")
                , root_1);

                adaptor.addChild(root_1, stream_var_type.nextTree());

                if ( !(stream_ID.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ID.hasNext() ) {
                    adaptor.addChild(root_1, 
                    stream_ID.nextNode()
                    );

                }
                stream_ID.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "var_decl"


    public static class var_type_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_type"
    // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:375:1: var_type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlParser.var_type_return var_type() throws RecognitionException {
        CtrlParser.var_type_return retval = new CtrlParser.var_type_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set105=null;

        CommonTree set105_tree=null;

        try {
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:376:2: ( NODE | BOOL | STRING | INT | REAL )
            // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set105=(Token)input.LT(1);

            if ( input.LA(1)==BOOL||input.LA(1)==INT||input.LA(1)==NODE||input.LA(1)==REAL||input.LA(1)==STRING ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set105)
                );
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "var_type"

    // $ANTLR start synpred1_Ctrl
    public final void synpred1_Ctrl_fragment() throws RecognitionException {
        // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:217:33: ( ELSE )
        // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:217:34: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred1_Ctrl838); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred1_Ctrl

    // $ANTLR start synpred2_Ctrl
    public final void synpred2_Ctrl_fragment() throws RecognitionException {
        // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:221:17: ( ELSE )
        // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:221:18: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred2_Ctrl878); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred2_Ctrl

    // $ANTLR start synpred3_Ctrl
    public final void synpred3_Ctrl_fragment() throws RecognitionException {
        // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:224:20: ( OR )
        // E:\\Eclipse\\groove-recipe\\src\\groove\\control\\parse\\Ctrl.g:224:21: OR
        {
        match(input,OR,FOLLOW_OR_in_synpred3_Ctrl913); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred3_Ctrl

    // Delegated rules

    public final boolean synpred1_Ctrl() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_Ctrl_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred3_Ctrl() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_Ctrl_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_Ctrl() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_Ctrl_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


 

    public static final BitSet FOLLOW_package_decl_in_program141 = new BitSet(new long[]{0x648A02267A029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_import_decl_in_program147 = new BitSet(new long[]{0x648A02267A029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_function_in_program155 = new BitSet(new long[]{0x648A02265A029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_recipe_in_program157 = new BitSet(new long[]{0x648A02265A029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_program159 = new BitSet(new long[]{0x648A02265A029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_EOF_in_program163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_package_decl295 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_qual_name_in_package_decl297 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_SEMI_in_package_decl299 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_decl363 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_qual_name_in_import_decl366 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_SEMI_in_import_decl368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_qual_name392 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_DOT_in_qual_name395 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_qual_name399 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_RECIPE_in_recipe440 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_recipe443 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_recipe445 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RPAR_in_recipe448 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_block_in_recipe465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function496 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_function499 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_function501 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RPAR_in_function504 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_block_in_function507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LCURLY_in_block538 = new BitSet(new long[]{0x6483022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_block540 = new BitSet(new long[]{0x6483022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_RCURLY_in_block543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_stat567 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat584 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_stat608 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat611 = new BitSet(new long[]{0x1000000008000000L});
    public static final BitSet FOLLOW_cond_in_stat614 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat616 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat639 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat642 = new BitSet(new long[]{0x1000000008000000L});
    public static final BitSet FOLLOW_cond_in_stat645 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat647 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat650 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_stat655 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat657 = new BitSet(new long[]{0x4000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_WHILE_in_stat700 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat702 = new BitSet(new long[]{0x1000000008000000L});
    public static final BitSet FOLLOW_cond_in_stat704 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat769 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat771 = new BitSet(new long[]{0x1000000008000000L});
    public static final BitSet FOLLOW_cond_in_stat773 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat775 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_stat822 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat825 = new BitSet(new long[]{0x1000000008000000L});
    public static final BitSet FOLLOW_cond_in_stat828 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat830 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat833 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_ELSE_in_stat843 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat846 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_stat870 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat873 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_ELSE_in_stat883 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat886 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_stat905 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat908 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_OR_in_stat918 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat921 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_expr_in_stat936 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat952 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cond_atom_in_cond978 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_BAR_in_cond987 = new BitSet(new long[]{0x1000000008000000L});
    public static final BitSet FOLLOW_cond_atom_in_cond989 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_TRUE_in_cond_atom1035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_cond_atom1056 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr2_in_expr1086 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_BAR_in_expr1094 = new BitSet(new long[]{0x0080020408000040L});
    public static final BitSet FOLLOW_expr2_in_expr1096 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_expr_atom_in_expr21177 = new BitSet(new long[]{0x0000100000000202L});
    public static final BitSet FOLLOW_PLUS_in_expr21185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERISK_in_expr21209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expr21261 = new BitSet(new long[]{0x0000020408000040L});
    public static final BitSet FOLLOW_expr_atom_in_expr21263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expr_atom1291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expr_atom1308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_atom1321 = new BitSet(new long[]{0x0080020408000040L});
    public static final BitSet FOLLOW_expr_in_expr_atom1324 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RPAR_in_expr_atom1326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expr_atom1340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_name_in_call1370 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_arg_list_in_call1372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_arg_list1412 = new BitSet(new long[]{0x1824040089040000L});
    public static final BitSet FOLLOW_arg_in_arg_list1415 = new BitSet(new long[]{0x0020000000010000L});
    public static final BitSet FOLLOW_COMMA_in_arg_list1418 = new BitSet(new long[]{0x1804040089040000L});
    public static final BitSet FOLLOW_arg_in_arg_list1420 = new BitSet(new long[]{0x0020000000010000L});
    public static final BitSet FOLLOW_RPAR_in_arg_list1426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_arg1469 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_arg1471 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_arg1502 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg1531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_arg1548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qual_name_in_rule_name1658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_type_in_var_decl1688 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_var_decl1690 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_COMMA_in_var_decl1693 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_var_decl1695 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_ELSE_in_synpred1_Ctrl838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred2_Ctrl878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_synpred3_Ctrl913 = new BitSet(new long[]{0x0000000000000002L});

}