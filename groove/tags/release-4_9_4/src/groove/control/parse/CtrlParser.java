// $ANTLR 3.4 E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g 2014-02-25 20:37:48

package groove.control.parse;
import groove.control.*;
import groove.util.antlr.*;
import groove.grammar.model.FormatErrorSet;
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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALAP", "AMP", "ANY", "ARG", "ARGS", "ASTERISK", "ATOM", "BAR", "BLOCK", "BOOL", "BSLASH", "CALL", "CHOICE", "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", "ELSE", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF", "IMPORT", "IMPORTS", "INT", "INT_LIT", "IntegerNumber", "LCURLY", "LPAR", "MINUS", "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT", "PACKAGE", "PAR", "PARS", "PLUS", "PRIORITY", "PROGRAM", "QUOTE", "RCURLY", "REAL", "REAL_LIT", "RECIPE", "RECIPES", "RPAR", "SEMI", "SHARP", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", "TRUE", "TRY", "UNTIL", "VAR", "WHILE", "WS"
    };

    public static final int EOF=-1;
    public static final int ALAP=4;
    public static final int AMP=5;
    public static final int ANY=6;
    public static final int ARG=7;
    public static final int ARGS=8;
    public static final int ASTERISK=9;
    public static final int ATOM=10;
    public static final int BAR=11;
    public static final int BLOCK=12;
    public static final int BOOL=13;
    public static final int BSLASH=14;
    public static final int CALL=15;
    public static final int CHOICE=16;
    public static final int COMMA=17;
    public static final int DO=18;
    public static final int DONT_CARE=19;
    public static final int DOT=20;
    public static final int DO_UNTIL=21;
    public static final int DO_WHILE=22;
    public static final int ELSE=23;
    public static final int EscapeSequence=24;
    public static final int FALSE=25;
    public static final int FUNCTION=26;
    public static final int FUNCTIONS=27;
    public static final int ID=28;
    public static final int IF=29;
    public static final int IMPORT=30;
    public static final int IMPORTS=31;
    public static final int INT=32;
    public static final int INT_LIT=33;
    public static final int IntegerNumber=34;
    public static final int LCURLY=35;
    public static final int LPAR=36;
    public static final int MINUS=37;
    public static final int ML_COMMENT=38;
    public static final int NODE=39;
    public static final int NOT=40;
    public static final int NonIntegerNumber=41;
    public static final int OR=42;
    public static final int OTHER=43;
    public static final int OUT=44;
    public static final int PACKAGE=45;
    public static final int PAR=46;
    public static final int PARS=47;
    public static final int PLUS=48;
    public static final int PRIORITY=49;
    public static final int PROGRAM=50;
    public static final int QUOTE=51;
    public static final int RCURLY=52;
    public static final int REAL=53;
    public static final int REAL_LIT=54;
    public static final int RECIPE=55;
    public static final int RECIPES=56;
    public static final int RPAR=57;
    public static final int SEMI=58;
    public static final int SHARP=59;
    public static final int SL_COMMENT=60;
    public static final int STAR=61;
    public static final int STRING=62;
    public static final int STRING_LIT=63;
    public static final int TRUE=64;
    public static final int TRY=65;
    public static final int UNTIL=66;
    public static final int VAR=67;
    public static final int WHILE=68;
    public static final int WS=69;

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
    public String getGrammarFileName() { return "E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g"; }


        /** Helper class to convert AST trees to namespace. */
        private CtrlHelper helper;
        
        public void displayRecognitionError(String[] tokenNames,
                RecognitionException e) {
            String hdr = getErrorHeader(e);
            String msg = getErrorMessage(e, tokenNames);
            this.helper.addError(hdr + " " + msg, e.line, e.charPositionInLine);
        }

        public void initialise(ParseInfo namespace) {
            this.helper = new CtrlHelper((Namespace) namespace);
        }


    public static class program_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "program"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:75:1: program : package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) ) ;
    public final CtrlParser.program_return program() throws RecognitionException {
        CtrlParser.program_return retval = new CtrlParser.program_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token EOF6=null;
        CtrlParser.package_decl_return package_decl1 =null;

        CtrlParser.import_decl_return import_decl2 =null;

        CtrlParser.function_return function3 =null;

        CtrlParser.recipe_return recipe4 =null;

        CtrlParser.stat_return stat5 =null;


        CtrlTree EOF6_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_package_decl=new RewriteRuleSubtreeStream(adaptor,"rule package_decl");
        RewriteRuleSubtreeStream stream_recipe=new RewriteRuleSubtreeStream(adaptor,"rule recipe");
        RewriteRuleSubtreeStream stream_import_decl=new RewriteRuleSubtreeStream(adaptor,"rule import_decl");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        RewriteRuleSubtreeStream stream_function=new RewriteRuleSubtreeStream(adaptor,"rule function");
         helper.clearErrors(); 
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:78:3: ( package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:82:5: package_decl ( import_decl )* ( function | recipe | stat )* EOF
            {
            pushFollow(FOLLOW_package_decl_in_program166);
            package_decl1=package_decl();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_package_decl.add(package_decl1.getTree());

            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:83:5: ( import_decl )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==IMPORT) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:83:5: import_decl
            	    {
            	    pushFollow(FOLLOW_import_decl_in_program172);
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


            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:84:5: ( function | recipe | stat )*
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
                case ATOM:
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
            	    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:84:6: function
            	    {
            	    pushFollow(FOLLOW_function_in_program180);
            	    function3=function();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_function.add(function3.getTree());

            	    }
            	    break;
            	case 2 :
            	    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:84:15: recipe
            	    {
            	    pushFollow(FOLLOW_recipe_in_program182);
            	    recipe4=recipe();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_recipe.add(recipe4.getTree());

            	    }
            	    break;
            	case 3 :
            	    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:84:22: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_program184);
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


            EOF6=(Token)match(input,EOF,FOLLOW_EOF_in_program188); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EOF.add(EOF6);


            if ( state.backtracking==0 ) { helper.checkEOF(EOF6_tree); }

            // AST REWRITE
            // elements: package_decl, recipe, import_decl, stat, function
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CtrlTree)adaptor.nil();
            // 86:5: -> ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) )
            {
                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:86:8: ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(PROGRAM, "PROGRAM")
                , root_1);

                adaptor.addChild(root_1, stream_package_decl.nextTree());

                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:88:11: ^( IMPORTS ( import_decl )* )
                {
                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                root_2 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(IMPORTS, "IMPORTS")
                , root_2);

                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:88:21: ( import_decl )*
                while ( stream_import_decl.hasNext() ) {
                    adaptor.addChild(root_2, stream_import_decl.nextTree());

                }
                stream_import_decl.reset();

                adaptor.addChild(root_1, root_2);
                }

                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:89:11: ^( FUNCTIONS ( function )* )
                {
                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                root_2 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(FUNCTIONS, "FUNCTIONS")
                , root_2);

                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:89:23: ( function )*
                while ( stream_function.hasNext() ) {
                    adaptor.addChild(root_2, stream_function.nextTree());

                }
                stream_function.reset();

                adaptor.addChild(root_1, root_2);
                }

                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:90:11: ^( RECIPES ( recipe )* )
                {
                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                root_2 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(RECIPES, "RECIPES")
                , root_2);

                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:90:21: ( recipe )*
                while ( stream_recipe.hasNext() ) {
                    adaptor.addChild(root_2, stream_recipe.nextTree());

                }
                stream_recipe.reset();

                adaptor.addChild(root_1, root_2);
                }

                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:91:11: ^( BLOCK ( stat )* )
                {
                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                root_2 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                , root_2);

                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:91:19: ( stat )*
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

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) { helper.declareProgram(((CtrlTree)retval.tree)); }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "program"


    public static class package_decl_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "package_decl"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:96:1: package_decl : (key= PACKAGE qual_name close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->) ;
    public final CtrlParser.package_decl_return package_decl() throws RecognitionException {
        CtrlParser.package_decl_return retval = new CtrlParser.package_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token key=null;
        Token close=null;
        CtrlParser.qual_name_return qual_name7 =null;


        CtrlTree key_tree=null;
        CtrlTree close_tree=null;
        RewriteRuleTokenStream stream_PACKAGE=new RewriteRuleTokenStream(adaptor,"token PACKAGE");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleSubtreeStream stream_qual_name=new RewriteRuleSubtreeStream(adaptor,"rule qual_name");
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:97:3: ( (key= PACKAGE qual_name close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:99:5: (key= PACKAGE qual_name close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->)
            {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:99:5: (key= PACKAGE qual_name close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->)
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==PACKAGE) ) {
                alt3=1;
            }
            else if ( (LA3_0==EOF||LA3_0==ALAP||LA3_0==ANY||LA3_0==ATOM||LA3_0==BOOL||LA3_0==CHOICE||LA3_0==DO||LA3_0==FUNCTION||(LA3_0 >= ID && LA3_0 <= IMPORT)||LA3_0==INT||(LA3_0 >= LCURLY && LA3_0 <= LPAR)||LA3_0==NODE||LA3_0==OTHER||LA3_0==REAL||LA3_0==RECIPE||LA3_0==SHARP||LA3_0==STRING||(LA3_0 >= TRY && LA3_0 <= UNTIL)||LA3_0==WHILE) ) {
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
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:99:7: key= PACKAGE qual_name close= SEMI
                    {
                    key=(Token)match(input,PACKAGE,FOLLOW_PACKAGE_in_package_decl325); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PACKAGE.add(key);


                    pushFollow(FOLLOW_qual_name_in_package_decl327);
                    qual_name7=qual_name();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_qual_name.add(qual_name7.getTree());

                    close=(Token)match(input,SEMI,FOLLOW_SEMI_in_package_decl331); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMI.add(close);


                    if ( state.backtracking==0 ) { helper.setPackage((qual_name7!=null?((CtrlTree)qual_name7.tree):null)); }

                    // AST REWRITE
                    // elements: qual_name, PACKAGE, SEMI
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CtrlTree)adaptor.nil();
                    // 101:7: -> ^( PACKAGE[$key] qual_name SEMI[$close] )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:101:10: ^( PACKAGE[$key] qual_name SEMI[$close] )
                        {
                        CtrlTree root_1 = (CtrlTree)adaptor.nil();
                        root_1 = (CtrlTree)adaptor.becomeRoot(
                        (CtrlTree)adaptor.create(PACKAGE, key)
                        , root_1);

                        adaptor.addChild(root_1, stream_qual_name.nextTree());

                        adaptor.addChild(root_1, 
                        (CtrlTree)adaptor.create(SEMI, close)
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:102:7: 
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

                    root_0 = (CtrlTree)adaptor.nil();
                    // 102:7: ->
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

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "package_decl"


    public static class import_decl_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "import_decl"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:107:1: import_decl : IMPORT ^ qual_name SEMI ;
    public final CtrlParser.import_decl_return import_decl() throws RecognitionException {
        CtrlParser.import_decl_return retval = new CtrlParser.import_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token IMPORT8=null;
        Token SEMI10=null;
        CtrlParser.qual_name_return qual_name9 =null;


        CtrlTree IMPORT8_tree=null;
        CtrlTree SEMI10_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:108:3: ( IMPORT ^ qual_name SEMI )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:110:5: IMPORT ^ qual_name SEMI
            {
            root_0 = (CtrlTree)adaptor.nil();


            IMPORT8=(Token)match(input,IMPORT,FOLLOW_IMPORT_in_import_decl398); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IMPORT8_tree = 
            (CtrlTree)adaptor.create(IMPORT8)
            ;
            root_0 = (CtrlTree)adaptor.becomeRoot(IMPORT8_tree, root_0);
            }

            pushFollow(FOLLOW_qual_name_in_import_decl401);
            qual_name9=qual_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qual_name9.getTree());

            SEMI10=(Token)match(input,SEMI,FOLLOW_SEMI_in_import_decl403); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            SEMI10_tree = 
            (CtrlTree)adaptor.create(SEMI10)
            ;
            adaptor.addChild(root_0, SEMI10_tree);
            }

            if ( state.backtracking==0 ) { helper.addImport((qual_name9!=null?((CtrlTree)qual_name9.tree):null));
                }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "import_decl"


    public static class qual_name_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "qual_name"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:116:1: qual_name :ids+= ID ( DOT ids+= ID )* ->;
    public final CtrlParser.qual_name_return qual_name() throws RecognitionException {
        CtrlParser.qual_name_return retval = new CtrlParser.qual_name_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token DOT11=null;
        Token ids=null;
        List list_ids=null;

        CtrlTree DOT11_tree=null;
        CtrlTree ids_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:117:3: (ids+= ID ( DOT ids+= ID )* ->)
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:117:5: ids+= ID ( DOT ids+= ID )*
            {
            ids=(Token)match(input,ID,FOLLOW_ID_in_qual_name426); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ids);

            if (list_ids==null) list_ids=new ArrayList();
            list_ids.add(ids);


            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:117:13: ( DOT ids+= ID )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==DOT) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:117:14: DOT ids+= ID
            	    {
            	    DOT11=(Token)match(input,DOT,FOLLOW_DOT_in_qual_name429); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(DOT11);


            	    ids=(Token)match(input,ID,FOLLOW_ID_in_qual_name433); if (state.failed) return retval; 
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

            root_0 = (CtrlTree)adaptor.nil();
            // 118:5: ->
            {
                adaptor.addChild(root_0,  helper.toQualName(list_ids) );

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "qual_name"


    public static class recipe_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipe"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:124:1: recipe : RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block ;
    public final CtrlParser.recipe_return recipe() throws RecognitionException {
        CtrlParser.recipe_return retval = new CtrlParser.recipe_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token RECIPE12=null;
        Token ID13=null;
        Token PRIORITY15=null;
        Token INT_LIT16=null;
        CtrlParser.par_list_return par_list14 =null;

        CtrlParser.block_return block17 =null;


        CtrlTree RECIPE12_tree=null;
        CtrlTree ID13_tree=null;
        CtrlTree PRIORITY15_tree=null;
        CtrlTree INT_LIT16_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:125:3: ( RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:128:5: RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block
            {
            root_0 = (CtrlTree)adaptor.nil();


            RECIPE12=(Token)match(input,RECIPE,FOLLOW_RECIPE_in_recipe473); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RECIPE12_tree = 
            (CtrlTree)adaptor.create(RECIPE12)
            ;
            root_0 = (CtrlTree)adaptor.becomeRoot(RECIPE12_tree, root_0);
            }

            ID13=(Token)match(input,ID,FOLLOW_ID_in_recipe476); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID13_tree = 
            (CtrlTree)adaptor.create(ID13)
            ;
            adaptor.addChild(root_0, ID13_tree);
            }

            pushFollow(FOLLOW_par_list_in_recipe478);
            par_list14=par_list();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, par_list14.getTree());

            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:128:25: ( PRIORITY ! INT_LIT )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==PRIORITY) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:128:26: PRIORITY ! INT_LIT
                    {
                    PRIORITY15=(Token)match(input,PRIORITY,FOLLOW_PRIORITY_in_recipe481); if (state.failed) return retval;

                    INT_LIT16=(Token)match(input,INT_LIT,FOLLOW_INT_LIT_in_recipe484); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT_LIT16_tree = 
                    (CtrlTree)adaptor.create(INT_LIT16)
                    ;
                    adaptor.addChild(root_0, INT_LIT16_tree);
                    }

                    }
                    break;

            }


            pushFollow(FOLLOW_block_in_recipe488);
            block17=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block17.getTree());

            if ( state.backtracking==0 ) { helper.declareCtrlUnit(RECIPE12_tree); }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "recipe"


    public static class function_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:136:1: function : FUNCTION ^ ID par_list block ;
    public final CtrlParser.function_return function() throws RecognitionException {
        CtrlParser.function_return retval = new CtrlParser.function_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token FUNCTION18=null;
        Token ID19=null;
        CtrlParser.par_list_return par_list20 =null;

        CtrlParser.block_return block21 =null;


        CtrlTree FUNCTION18_tree=null;
        CtrlTree ID19_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:137:3: ( FUNCTION ^ ID par_list block )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:140:5: FUNCTION ^ ID par_list block
            {
            root_0 = (CtrlTree)adaptor.nil();


            FUNCTION18=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function524); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            FUNCTION18_tree = 
            (CtrlTree)adaptor.create(FUNCTION18)
            ;
            root_0 = (CtrlTree)adaptor.becomeRoot(FUNCTION18_tree, root_0);
            }

            ID19=(Token)match(input,ID,FOLLOW_ID_in_function527); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID19_tree = 
            (CtrlTree)adaptor.create(ID19)
            ;
            adaptor.addChild(root_0, ID19_tree);
            }

            pushFollow(FOLLOW_par_list_in_function529);
            par_list20=par_list();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, par_list20.getTree());

            pushFollow(FOLLOW_block_in_function531);
            block21=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block21.getTree());

            if ( state.backtracking==0 ) { helper.declareCtrlUnit(FUNCTION18_tree); }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "function"


    public static class par_list_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "par_list"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:147:1: par_list : LPAR ( par ( COMMA par )* )? RPAR -> ^( PARS ( par )* ) ;
    public final CtrlParser.par_list_return par_list() throws RecognitionException {
        CtrlParser.par_list_return retval = new CtrlParser.par_list_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token LPAR22=null;
        Token COMMA24=null;
        Token RPAR26=null;
        CtrlParser.par_return par23 =null;

        CtrlParser.par_return par25 =null;


        CtrlTree LPAR22_tree=null;
        CtrlTree COMMA24_tree=null;
        CtrlTree RPAR26_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_par=new RewriteRuleSubtreeStream(adaptor,"rule par");
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:148:3: ( LPAR ( par ( COMMA par )* )? RPAR -> ^( PARS ( par )* ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:150:5: LPAR ( par ( COMMA par )* )? RPAR
            {
            LPAR22=(Token)match(input,LPAR,FOLLOW_LPAR_in_par_list562); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAR.add(LPAR22);


            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:150:10: ( par ( COMMA par )* )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==BOOL||LA7_0==INT||LA7_0==NODE||LA7_0==OUT||LA7_0==REAL||LA7_0==STRING) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:150:11: par ( COMMA par )*
                    {
                    pushFollow(FOLLOW_par_in_par_list565);
                    par23=par();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_par.add(par23.getTree());

                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:150:15: ( COMMA par )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==COMMA) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:150:16: COMMA par
                    	    {
                    	    COMMA24=(Token)match(input,COMMA,FOLLOW_COMMA_in_par_list568); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA24);


                    	    pushFollow(FOLLOW_par_in_par_list570);
                    	    par25=par();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_par.add(par25.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);


                    }
                    break;

            }


            RPAR26=(Token)match(input,RPAR,FOLLOW_RPAR_in_par_list576); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAR.add(RPAR26);


            // AST REWRITE
            // elements: par
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CtrlTree)adaptor.nil();
            // 151:5: -> ^( PARS ( par )* )
            {
                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:151:8: ^( PARS ( par )* )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(PARS, "PARS")
                , root_1);

                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:151:15: ( par )*
                while ( stream_par.hasNext() ) {
                    adaptor.addChild(root_1, stream_par.nextTree());

                }
                stream_par.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "par_list"


    public static class par_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "par"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:157:1: par : ( OUT var_type ID -> ^( PAR OUT var_type ID ) | var_type ID -> ^( PAR var_type ID ) );
    public final CtrlParser.par_return par() throws RecognitionException {
        CtrlParser.par_return retval = new CtrlParser.par_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token OUT27=null;
        Token ID29=null;
        Token ID31=null;
        CtrlParser.var_type_return var_type28 =null;

        CtrlParser.var_type_return var_type30 =null;


        CtrlTree OUT27_tree=null;
        CtrlTree ID29_tree=null;
        CtrlTree ID31_tree=null;
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:158:3: ( OUT var_type ID -> ^( PAR OUT var_type ID ) | var_type ID -> ^( PAR var_type ID ) )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==OUT) ) {
                alt8=1;
            }
            else if ( (LA8_0==BOOL||LA8_0==INT||LA8_0==NODE||LA8_0==REAL||LA8_0==STRING) ) {
                alt8=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;

            }
            switch (alt8) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:161:5: OUT var_type ID
                    {
                    OUT27=(Token)match(input,OUT,FOLLOW_OUT_in_par621); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT27);


                    pushFollow(FOLLOW_var_type_in_par623);
                    var_type28=var_type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_var_type.add(var_type28.getTree());

                    ID29=(Token)match(input,ID,FOLLOW_ID_in_par625); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID29);


                    // AST REWRITE
                    // elements: var_type, OUT, ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CtrlTree)adaptor.nil();
                    // 161:21: -> ^( PAR OUT var_type ID )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:161:24: ^( PAR OUT var_type ID )
                        {
                        CtrlTree root_1 = (CtrlTree)adaptor.nil();
                        root_1 = (CtrlTree)adaptor.becomeRoot(
                        (CtrlTree)adaptor.create(PAR, "PAR")
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_OUT.nextNode()
                        );

                        adaptor.addChild(root_1, stream_var_type.nextTree());

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
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:165:5: var_type ID
                    {
                    pushFollow(FOLLOW_var_type_in_par658);
                    var_type30=var_type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_var_type.add(var_type30.getTree());

                    ID31=(Token)match(input,ID,FOLLOW_ID_in_par660); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID31);


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

                    root_0 = (CtrlTree)adaptor.nil();
                    // 165:17: -> ^( PAR var_type ID )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:165:20: ^( PAR var_type ID )
                        {
                        CtrlTree root_1 = (CtrlTree)adaptor.nil();
                        root_1 = (CtrlTree)adaptor.becomeRoot(
                        (CtrlTree)adaptor.create(PAR, "PAR")
                        , root_1);

                        adaptor.addChild(root_1, stream_var_type.nextTree());

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

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "par"


    public static class block_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "block"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:169:1: block : open= LCURLY ( stat )* close= RCURLY -> ^( BLOCK[$open] ( stat )* TRUE[$close] ) ;
    public final CtrlParser.block_return block() throws RecognitionException {
        CtrlParser.block_return retval = new CtrlParser.block_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        CtrlParser.stat_return stat32 =null;


        CtrlTree open_tree=null;
        CtrlTree close_tree=null;
        RewriteRuleTokenStream stream_LCURLY=new RewriteRuleTokenStream(adaptor,"token LCURLY");
        RewriteRuleTokenStream stream_RCURLY=new RewriteRuleTokenStream(adaptor,"token RCURLY");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:170:3: (open= LCURLY ( stat )* close= RCURLY -> ^( BLOCK[$open] ( stat )* TRUE[$close] ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:172:5: open= LCURLY ( stat )* close= RCURLY
            {
            open=(Token)match(input,LCURLY,FOLLOW_LCURLY_in_block699); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LCURLY.add(open);


            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:172:17: ( stat )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==ALAP||LA9_0==ANY||LA9_0==ATOM||LA9_0==BOOL||LA9_0==CHOICE||LA9_0==DO||(LA9_0 >= ID && LA9_0 <= IF)||LA9_0==INT||(LA9_0 >= LCURLY && LA9_0 <= LPAR)||LA9_0==NODE||LA9_0==OTHER||LA9_0==REAL||LA9_0==SHARP||LA9_0==STRING||(LA9_0 >= TRY && LA9_0 <= UNTIL)||LA9_0==WHILE) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:172:17: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_block701);
            	    stat32=stat();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_stat.add(stat32.getTree());

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


            close=(Token)match(input,RCURLY,FOLLOW_RCURLY_in_block706); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RCURLY.add(close);


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

            root_0 = (CtrlTree)adaptor.nil();
            // 173:5: -> ^( BLOCK[$open] ( stat )* TRUE[$close] )
            {
                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:173:8: ^( BLOCK[$open] ( stat )* TRUE[$close] )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(BLOCK, open)
                , root_1);

                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:173:23: ( stat )*
                while ( stream_stat.hasNext() ) {
                    adaptor.addChild(root_1, stream_stat.nextTree());

                }
                stream_stat.reset();

                adaptor.addChild(root_1, 
                (CtrlTree)adaptor.create(TRUE, close)
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "block"


    public static class stat_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "stat"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:176:1: stat : ( block | ALAP ^ stat | ATOM ^ stat | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI ^| var_decl SEMI ^);
    public final CtrlParser.stat_return stat() throws RecognitionException {
        CtrlParser.stat_return retval = new CtrlParser.stat_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token ALAP34=null;
        Token ATOM36=null;
        Token WHILE38=null;
        Token LPAR39=null;
        Token RPAR41=null;
        Token UNTIL43=null;
        Token LPAR44=null;
        Token RPAR46=null;
        Token DO48=null;
        Token WHILE50=null;
        Token LPAR51=null;
        Token RPAR53=null;
        Token UNTIL54=null;
        Token LPAR55=null;
        Token RPAR57=null;
        Token IF58=null;
        Token LPAR59=null;
        Token RPAR61=null;
        Token ELSE63=null;
        Token TRY65=null;
        Token ELSE67=null;
        Token CHOICE69=null;
        Token OR71=null;
        Token SEMI74=null;
        Token SEMI76=null;
        CtrlParser.block_return block33 =null;

        CtrlParser.stat_return stat35 =null;

        CtrlParser.stat_return stat37 =null;

        CtrlParser.cond_return cond40 =null;

        CtrlParser.stat_return stat42 =null;

        CtrlParser.cond_return cond45 =null;

        CtrlParser.stat_return stat47 =null;

        CtrlParser.stat_return stat49 =null;

        CtrlParser.cond_return cond52 =null;

        CtrlParser.cond_return cond56 =null;

        CtrlParser.cond_return cond60 =null;

        CtrlParser.stat_return stat62 =null;

        CtrlParser.stat_return stat64 =null;

        CtrlParser.stat_return stat66 =null;

        CtrlParser.stat_return stat68 =null;

        CtrlParser.stat_return stat70 =null;

        CtrlParser.stat_return stat72 =null;

        CtrlParser.expr_return expr73 =null;

        CtrlParser.var_decl_return var_decl75 =null;


        CtrlTree ALAP34_tree=null;
        CtrlTree ATOM36_tree=null;
        CtrlTree WHILE38_tree=null;
        CtrlTree LPAR39_tree=null;
        CtrlTree RPAR41_tree=null;
        CtrlTree UNTIL43_tree=null;
        CtrlTree LPAR44_tree=null;
        CtrlTree RPAR46_tree=null;
        CtrlTree DO48_tree=null;
        CtrlTree WHILE50_tree=null;
        CtrlTree LPAR51_tree=null;
        CtrlTree RPAR53_tree=null;
        CtrlTree UNTIL54_tree=null;
        CtrlTree LPAR55_tree=null;
        CtrlTree RPAR57_tree=null;
        CtrlTree IF58_tree=null;
        CtrlTree LPAR59_tree=null;
        CtrlTree RPAR61_tree=null;
        CtrlTree ELSE63_tree=null;
        CtrlTree TRY65_tree=null;
        CtrlTree ELSE67_tree=null;
        CtrlTree CHOICE69_tree=null;
        CtrlTree OR71_tree=null;
        CtrlTree SEMI74_tree=null;
        CtrlTree SEMI76_tree=null;
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_WHILE=new RewriteRuleTokenStream(adaptor,"token WHILE");
        RewriteRuleTokenStream stream_UNTIL=new RewriteRuleTokenStream(adaptor,"token UNTIL");
        RewriteRuleSubtreeStream stream_cond=new RewriteRuleSubtreeStream(adaptor,"rule cond");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:177:2: ( block | ALAP ^ stat | ATOM ^ stat | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI ^| var_decl SEMI ^)
            int alt14=11;
            switch ( input.LA(1) ) {
            case LCURLY:
                {
                alt14=1;
                }
                break;
            case ALAP:
                {
                alt14=2;
                }
                break;
            case ATOM:
                {
                alt14=3;
                }
                break;
            case WHILE:
                {
                alt14=4;
                }
                break;
            case UNTIL:
                {
                alt14=5;
                }
                break;
            case DO:
                {
                alt14=6;
                }
                break;
            case IF:
                {
                alt14=7;
                }
                break;
            case TRY:
                {
                alt14=8;
                }
                break;
            case CHOICE:
                {
                alt14=9;
                }
                break;
            case ANY:
            case ID:
            case LPAR:
            case OTHER:
            case SHARP:
                {
                alt14=10;
                }
                break;
            case BOOL:
            case INT:
            case NODE:
            case REAL:
            case STRING:
                {
                alt14=11;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;

            }

            switch (alt14) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:178:4: block
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_block_in_stat738);
                    block33=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block33.getTree());

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:182:4: ALAP ^ stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    ALAP34=(Token)match(input,ALAP,FOLLOW_ALAP_in_stat755); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ALAP34_tree = 
                    (CtrlTree)adaptor.create(ALAP34)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(ALAP34_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat758);
                    stat35=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat35.getTree());

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:186:4: ATOM ^ stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    ATOM36=(Token)match(input,ATOM,FOLLOW_ATOM_in_stat775); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ATOM36_tree = 
                    (CtrlTree)adaptor.create(ATOM36)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(ATOM36_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat778);
                    stat37=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat37.getTree());

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:191:4: WHILE ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    WHILE38=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat799); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    WHILE38_tree = 
                    (CtrlTree)adaptor.create(WHILE38)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(WHILE38_tree, root_0);
                    }

                    LPAR39=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat802); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat805);
                    cond40=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond40.getTree());

                    RPAR41=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat807); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat810);
                    stat42=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat42.getTree());

                    }
                    break;
                case 5 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:195:5: UNTIL ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    UNTIL43=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat830); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    UNTIL43_tree = 
                    (CtrlTree)adaptor.create(UNTIL43)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(UNTIL43_tree, root_0);
                    }

                    LPAR44=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat833); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat836);
                    cond45=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond45.getTree());

                    RPAR46=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat838); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat841);
                    stat47=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat47.getTree());

                    }
                    break;
                case 6 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:196:4: DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                    {
                    DO48=(Token)match(input,DO,FOLLOW_DO_in_stat846); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DO.add(DO48);


                    pushFollow(FOLLOW_stat_in_stat848);
                    stat49=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_stat.add(stat49.getTree());

                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:197:4: ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==WHILE) ) {
                        alt10=1;
                    }
                    else if ( (LA10_0==UNTIL) ) {
                        alt10=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 10, 0, input);

                        throw nvae;

                    }
                    switch (alt10) {
                        case 1 :
                            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:202:7: WHILE LPAR cond RPAR
                            {
                            WHILE50=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat891); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_WHILE.add(WHILE50);


                            LPAR51=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat893); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR51);


                            pushFollow(FOLLOW_cond_in_stat895);
                            cond52=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond52.getTree());

                            RPAR53=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat897); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR53);


                            // AST REWRITE
                            // elements: cond, WHILE, stat, stat
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {

                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (CtrlTree)adaptor.nil();
                            // 202:28: -> ^( BLOCK stat ^( WHILE cond stat ) )
                            {
                                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:202:31: ^( BLOCK stat ^( WHILE cond stat ) )
                                {
                                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                                root_1 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:202:44: ^( WHILE cond stat )
                                {
                                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                                root_2 = (CtrlTree)adaptor.becomeRoot(
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
                            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:209:5: UNTIL LPAR cond RPAR
                            {
                            UNTIL54=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat960); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_UNTIL.add(UNTIL54);


                            LPAR55=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat962); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR55);


                            pushFollow(FOLLOW_cond_in_stat964);
                            cond56=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond56.getTree());

                            RPAR57=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat966); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR57);


                            // AST REWRITE
                            // elements: UNTIL, cond, stat, stat
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {

                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (CtrlTree)adaptor.nil();
                            // 209:26: -> ^( BLOCK stat ^( UNTIL cond stat ) )
                            {
                                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:209:29: ^( BLOCK stat ^( UNTIL cond stat ) )
                                {
                                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                                root_1 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:209:42: ^( UNTIL cond stat )
                                {
                                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                                root_2 = (CtrlTree)adaptor.becomeRoot(
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
                case 7 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:215:5: IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    IF58=(Token)match(input,IF,FOLLOW_IF_in_stat1013); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IF58_tree = 
                    (CtrlTree)adaptor.create(IF58)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(IF58_tree, root_0);
                    }

                    LPAR59=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1016); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat1019);
                    cond60=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond60.getTree());

                    RPAR61=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1021); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat1024);
                    stat62=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat62.getTree());

                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:215:31: ( ( ELSE )=> ELSE ! stat )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==ELSE) ) {
                        int LA11_1 = input.LA(2);

                        if ( (synpred1_Ctrl()) ) {
                            alt11=1;
                        }
                    }
                    switch (alt11) {
                        case 1 :
                            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:215:33: ( ELSE )=> ELSE ! stat
                            {
                            ELSE63=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat1034); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat1037);
                            stat64=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat64.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:219:5: TRY ^ stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    TRY65=(Token)match(input,TRY,FOLLOW_TRY_in_stat1061); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRY65_tree = 
                    (CtrlTree)adaptor.create(TRY65)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(TRY65_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat1064);
                    stat66=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat66.getTree());

                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:219:15: ( ( ELSE )=> ELSE ! stat )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0==ELSE) ) {
                        int LA12_1 = input.LA(2);

                        if ( (synpred2_Ctrl()) ) {
                            alt12=1;
                        }
                    }
                    switch (alt12) {
                        case 1 :
                            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:219:17: ( ELSE )=> ELSE ! stat
                            {
                            ELSE67=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat1074); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat1077);
                            stat68=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat68.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 9 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:222:5: CHOICE ^ stat ( ( OR )=> OR ! stat )+
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    CHOICE69=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_stat1096); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHOICE69_tree = 
                    (CtrlTree)adaptor.create(CHOICE69)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(CHOICE69_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat1099);
                    stat70=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat70.getTree());

                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:222:18: ( ( OR )=> OR ! stat )+
                    int cnt13=0;
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( (LA13_0==OR) ) {
                            int LA13_21 = input.LA(2);

                            if ( (synpred3_Ctrl()) ) {
                                alt13=1;
                            }


                        }


                        switch (alt13) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:222:20: ( OR )=> OR ! stat
                    	    {
                    	    OR71=(Token)match(input,OR,FOLLOW_OR_in_stat1109); if (state.failed) return retval;

                    	    pushFollow(FOLLOW_stat_in_stat1112);
                    	    stat72=stat();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat72.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt13 >= 1 ) break loop13;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(13, input);
                                throw eee;
                        }
                        cnt13++;
                    } while (true);


                    }
                    break;
                case 10 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:225:4: expr SEMI ^
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_expr_in_stat1127);
                    expr73=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr73.getTree());

                    SEMI74=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat1129); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SEMI74_tree = 
                    (CtrlTree)adaptor.create(SEMI74)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(SEMI74_tree, root_0);
                    }

                    }
                    break;
                case 11 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:228:4: var_decl SEMI ^
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_var_decl_in_stat1144);
                    var_decl75=var_decl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, var_decl75.getTree());

                    SEMI76=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat1146); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SEMI76_tree = 
                    (CtrlTree)adaptor.create(SEMI76)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(SEMI76_tree, root_0);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "stat"


    public static class cond_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "cond"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:232:1: cond : cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) ;
    public final CtrlParser.cond_return cond() throws RecognitionException {
        CtrlParser.cond_return retval = new CtrlParser.cond_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token BAR78=null;
        CtrlParser.cond_atom_return cond_atom77 =null;

        CtrlParser.cond_atom_return cond_atom79 =null;


        CtrlTree BAR78_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_cond_atom=new RewriteRuleSubtreeStream(adaptor,"rule cond_atom");
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:233:2: ( cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:235:4: cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            {
            pushFollow(FOLLOW_cond_atom_in_cond1171);
            cond_atom77=cond_atom();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom77.getTree());

            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:236:4: ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==BAR) ) {
                alt16=1;
            }
            else if ( (LA16_0==RPAR) ) {
                alt16=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;

            }
            switch (alt16) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:236:6: ( BAR cond_atom )+
                    {
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:236:6: ( BAR cond_atom )+
                    int cnt15=0;
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0==BAR) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:236:7: BAR cond_atom
                    	    {
                    	    BAR78=(Token)match(input,BAR,FOLLOW_BAR_in_cond1180); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR78);


                    	    pushFollow(FOLLOW_cond_atom_in_cond1182);
                    	    cond_atom79=cond_atom();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom79.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt15 >= 1 ) break loop15;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(15, input);
                                throw eee;
                        }
                        cnt15++;
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

                    root_0 = (CtrlTree)adaptor.nil();
                    // 236:23: -> ^( CHOICE cond_atom ( cond_atom )+ )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:236:26: ^( CHOICE cond_atom ( cond_atom )+ )
                        {
                        CtrlTree root_1 = (CtrlTree)adaptor.nil();
                        root_1 = (CtrlTree)adaptor.becomeRoot(
                        (CtrlTree)adaptor.create(CHOICE, "CHOICE")
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
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:237:6: 
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

                    root_0 = (CtrlTree)adaptor.nil();
                    // 237:6: -> cond_atom
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

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "cond"


    public static class cond_atom_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "cond_atom"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:241:1: cond_atom : ( TRUE | call );
    public final CtrlParser.cond_atom_return cond_atom() throws RecognitionException {
        CtrlParser.cond_atom_return retval = new CtrlParser.cond_atom_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token TRUE80=null;
        CtrlParser.call_return call81 =null;


        CtrlTree TRUE80_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:242:2: ( TRUE | call )
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==TRUE) ) {
                alt17=1;
            }
            else if ( (LA17_0==ID) ) {
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
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:244:4: TRUE
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    TRUE80=(Token)match(input,TRUE,FOLLOW_TRUE_in_cond_atom1228); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRUE80_tree = 
                    (CtrlTree)adaptor.create(TRUE80)
                    ;
                    adaptor.addChild(root_0, TRUE80_tree);
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:248:5: call
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_cond_atom1249);
                    call81=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call81.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "cond_atom"


    public static class expr_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expr"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:251:1: expr : expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) ;
    public final CtrlParser.expr_return expr() throws RecognitionException {
        CtrlParser.expr_return retval = new CtrlParser.expr_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token BAR83=null;
        CtrlParser.expr2_return expr282 =null;

        CtrlParser.expr2_return expr284 =null;


        CtrlTree BAR83_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_expr2=new RewriteRuleSubtreeStream(adaptor,"rule expr2");
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:252:2: ( expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:256:4: expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            {
            pushFollow(FOLLOW_expr2_in_expr1279);
            expr282=expr2();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expr2.add(expr282.getTree());

            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:257:4: ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==BAR) ) {
                alt19=1;
            }
            else if ( ((LA19_0 >= RPAR && LA19_0 <= SEMI)) ) {
                alt19=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;

            }
            switch (alt19) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:257:6: ( BAR expr2 )+
                    {
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:257:6: ( BAR expr2 )+
                    int cnt18=0;
                    loop18:
                    do {
                        int alt18=2;
                        int LA18_0 = input.LA(1);

                        if ( (LA18_0==BAR) ) {
                            alt18=1;
                        }


                        switch (alt18) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:257:7: BAR expr2
                    	    {
                    	    BAR83=(Token)match(input,BAR,FOLLOW_BAR_in_expr1287); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR83);


                    	    pushFollow(FOLLOW_expr2_in_expr1289);
                    	    expr284=expr2();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expr2.add(expr284.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt18 >= 1 ) break loop18;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(18, input);
                                throw eee;
                        }
                        cnt18++;
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

                    root_0 = (CtrlTree)adaptor.nil();
                    // 257:19: -> ^( CHOICE expr2 ( expr2 )+ )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:257:22: ^( CHOICE expr2 ( expr2 )+ )
                        {
                        CtrlTree root_1 = (CtrlTree)adaptor.nil();
                        root_1 = (CtrlTree)adaptor.becomeRoot(
                        (CtrlTree)adaptor.create(CHOICE, "CHOICE")
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
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:258:6: 
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

                    root_0 = (CtrlTree)adaptor.nil();
                    // 258:6: -> expr2
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

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "expr"


    public static class expr2_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expr2"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:262:1: expr2 : (e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e) |op= SHARP expr_atom -> ^( ALAP[$op] expr_atom ) );
    public final CtrlParser.expr2_return expr2() throws RecognitionException {
        CtrlParser.expr2_return retval = new CtrlParser.expr2_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token plus=null;
        Token ast=null;
        Token op=null;
        CtrlParser.expr_atom_return e =null;

        CtrlParser.expr_atom_return expr_atom85 =null;


        CtrlTree plus_tree=null;
        CtrlTree ast_tree=null;
        CtrlTree op_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_SHARP=new RewriteRuleTokenStream(adaptor,"token SHARP");
        RewriteRuleTokenStream stream_ASTERISK=new RewriteRuleTokenStream(adaptor,"token ASTERISK");
        RewriteRuleSubtreeStream stream_expr_atom=new RewriteRuleSubtreeStream(adaptor,"rule expr_atom");
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:263:3: (e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e) |op= SHARP expr_atom -> ^( ALAP[$op] expr_atom ) )
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==ANY||LA21_0==ID||LA21_0==LPAR||LA21_0==OTHER) ) {
                alt21=1;
            }
            else if ( (LA21_0==SHARP) ) {
                alt21=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;

            }
            switch (alt21) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:271:5: e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e)
                    {
                    pushFollow(FOLLOW_expr_atom_in_expr21370);
                    e=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(e.getTree());

                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:272:5: (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e)
                    int alt20=3;
                    switch ( input.LA(1) ) {
                    case PLUS:
                        {
                        alt20=1;
                        }
                        break;
                    case ASTERISK:
                        {
                        alt20=2;
                        }
                        break;
                    case BAR:
                    case RPAR:
                    case SEMI:
                        {
                        alt20=3;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 20, 0, input);

                        throw nvae;

                    }

                    switch (alt20) {
                        case 1 :
                            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:272:7: plus= PLUS
                            {
                            plus=(Token)match(input,PLUS,FOLLOW_PLUS_in_expr21380); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_PLUS.add(plus);


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

                            root_0 = (CtrlTree)adaptor.nil();
                            // 272:17: -> ^( BLOCK $e ^( STAR[$plus] $e) )
                            {
                                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:272:20: ^( BLOCK $e ^( STAR[$plus] $e) )
                                {
                                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                                root_1 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_e.nextTree());

                                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:272:31: ^( STAR[$plus] $e)
                                {
                                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                                root_2 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(STAR, plus)
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
                            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:273:7: ast= ASTERISK
                            {
                            ast=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_expr21407); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ASTERISK.add(ast);


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

                            root_0 = (CtrlTree)adaptor.nil();
                            // 273:20: -> ^( STAR[$ast] $e)
                            {
                                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:273:23: ^( STAR[$ast] $e)
                                {
                                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                                root_1 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(STAR, ast)
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
                            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:274:7: 
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

                            root_0 = (CtrlTree)adaptor.nil();
                            // 274:7: -> $e
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
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:280:5: op= SHARP expr_atom
                    {
                    op=(Token)match(input,SHARP,FOLLOW_SHARP_in_expr21462); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SHARP.add(op);


                    pushFollow(FOLLOW_expr_atom_in_expr21464);
                    expr_atom85=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(expr_atom85.getTree());

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

                    root_0 = (CtrlTree)adaptor.nil();
                    // 280:24: -> ^( ALAP[$op] expr_atom )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:280:27: ^( ALAP[$op] expr_atom )
                        {
                        CtrlTree root_1 = (CtrlTree)adaptor.nil();
                        root_1 = (CtrlTree)adaptor.becomeRoot(
                        (CtrlTree)adaptor.create(ALAP, op)
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

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "expr2"


    public static class expr_atom_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expr_atom"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:283:1: expr_atom : ( ANY | OTHER |open= LPAR expr close= RPAR -> ^( BLOCK[$open] expr TRUE[$close] ) | call );
    public final CtrlParser.expr_atom_return expr_atom() throws RecognitionException {
        CtrlParser.expr_atom_return retval = new CtrlParser.expr_atom_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        Token ANY86=null;
        Token OTHER87=null;
        CtrlParser.expr_return expr88 =null;

        CtrlParser.call_return call89 =null;


        CtrlTree open_tree=null;
        CtrlTree close_tree=null;
        CtrlTree ANY86_tree=null;
        CtrlTree OTHER87_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:284:2: ( ANY | OTHER |open= LPAR expr close= RPAR -> ^( BLOCK[$open] expr TRUE[$close] ) | call )
            int alt22=4;
            switch ( input.LA(1) ) {
            case ANY:
                {
                alt22=1;
                }
                break;
            case OTHER:
                {
                alt22=2;
                }
                break;
            case LPAR:
                {
                alt22=3;
                }
                break;
            case ID:
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
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:286:4: ANY
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    ANY86=(Token)match(input,ANY,FOLLOW_ANY_in_expr_atom1493); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ANY86_tree = 
                    (CtrlTree)adaptor.create(ANY86)
                    ;
                    adaptor.addChild(root_0, ANY86_tree);
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:290:4: OTHER
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    OTHER87=(Token)match(input,OTHER,FOLLOW_OTHER_in_expr_atom1510); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OTHER87_tree = 
                    (CtrlTree)adaptor.create(OTHER87)
                    ;
                    adaptor.addChild(root_0, OTHER87_tree);
                    }

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:293:4: open= LPAR expr close= RPAR
                    {
                    open=(Token)match(input,LPAR,FOLLOW_LPAR_in_expr_atom1525); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAR.add(open);


                    pushFollow(FOLLOW_expr_in_expr_atom1527);
                    expr88=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr.add(expr88.getTree());

                    close=(Token)match(input,RPAR,FOLLOW_RPAR_in_expr_atom1531); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAR.add(close);


                    // AST REWRITE
                    // elements: expr
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CtrlTree)adaptor.nil();
                    // 294:4: -> ^( BLOCK[$open] expr TRUE[$close] )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:294:7: ^( BLOCK[$open] expr TRUE[$close] )
                        {
                        CtrlTree root_1 = (CtrlTree)adaptor.nil();
                        root_1 = (CtrlTree)adaptor.becomeRoot(
                        (CtrlTree)adaptor.create(BLOCK, open)
                        , root_1);

                        adaptor.addChild(root_1, stream_expr.nextTree());

                        adaptor.addChild(root_1, 
                        (CtrlTree)adaptor.create(TRUE, close)
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:297:4: call
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_expr_atom1559);
                    call89=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call89.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "expr_atom"


    public static class call_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "call"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:301:1: call : rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) ;
    public final CtrlParser.call_return call() throws RecognitionException {
        CtrlParser.call_return retval = new CtrlParser.call_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlParser.rule_name_return rule_name90 =null;

        CtrlParser.arg_list_return arg_list91 =null;


        RewriteRuleSubtreeStream stream_arg_list=new RewriteRuleSubtreeStream(adaptor,"rule arg_list");
        RewriteRuleSubtreeStream stream_rule_name=new RewriteRuleSubtreeStream(adaptor,"rule rule_name");
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:302:2: ( rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:306:4: rule_name ( arg_list )?
            {
            pushFollow(FOLLOW_rule_name_in_call1589);
            rule_name90=rule_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_name.add(rule_name90.getTree());

            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:306:14: ( arg_list )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==LPAR) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:306:14: arg_list
                    {
                    pushFollow(FOLLOW_arg_list_in_call1591);
                    arg_list91=arg_list();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg_list.add(arg_list91.getTree());

                    }
                    break;

            }


            // AST REWRITE
            // elements: arg_list, rule_name
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CtrlTree)adaptor.nil();
            // 307:4: -> ^( CALL[$rule_name.start] rule_name ( arg_list )? )
            {
                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:307:7: ^( CALL[$rule_name.start] rule_name ( arg_list )? )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(CALL, (rule_name90!=null?((Token)rule_name90.start):null))
                , root_1);

                adaptor.addChild(root_1, stream_rule_name.nextTree());

                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:307:42: ( arg_list )?
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

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "call"


    public static class arg_list_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg_list"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:313:1: arg_list : open= LPAR ( arg ( COMMA arg )* )? close= RPAR -> ^( ARGS[$open] ( arg )* RPAR[$close] ) ;
    public final CtrlParser.arg_list_return arg_list() throws RecognitionException {
        CtrlParser.arg_list_return retval = new CtrlParser.arg_list_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        Token COMMA93=null;
        CtrlParser.arg_return arg92 =null;

        CtrlParser.arg_return arg94 =null;


        CtrlTree open_tree=null;
        CtrlTree close_tree=null;
        CtrlTree COMMA93_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_arg=new RewriteRuleSubtreeStream(adaptor,"rule arg");
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:314:3: (open= LPAR ( arg ( COMMA arg )* )? close= RPAR -> ^( ARGS[$open] ( arg )* RPAR[$close] ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:316:5: open= LPAR ( arg ( COMMA arg )* )? close= RPAR
            {
            open=(Token)match(input,LPAR,FOLLOW_LPAR_in_arg_list1633); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAR.add(open);


            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:316:15: ( arg ( COMMA arg )* )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==DONT_CARE||LA25_0==FALSE||LA25_0==ID||LA25_0==INT_LIT||LA25_0==OUT||LA25_0==REAL_LIT||(LA25_0 >= STRING_LIT && LA25_0 <= TRUE)) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:316:16: arg ( COMMA arg )*
                    {
                    pushFollow(FOLLOW_arg_in_arg_list1636);
                    arg92=arg();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg.add(arg92.getTree());

                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:316:20: ( COMMA arg )*
                    loop24:
                    do {
                        int alt24=2;
                        int LA24_0 = input.LA(1);

                        if ( (LA24_0==COMMA) ) {
                            alt24=1;
                        }


                        switch (alt24) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:316:21: COMMA arg
                    	    {
                    	    COMMA93=(Token)match(input,COMMA,FOLLOW_COMMA_in_arg_list1639); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA93);


                    	    pushFollow(FOLLOW_arg_in_arg_list1641);
                    	    arg94=arg();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_arg.add(arg94.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop24;
                        }
                    } while (true);


                    }
                    break;

            }


            close=(Token)match(input,RPAR,FOLLOW_RPAR_in_arg_list1649); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAR.add(close);


            // AST REWRITE
            // elements: arg, RPAR
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CtrlTree)adaptor.nil();
            // 317:5: -> ^( ARGS[$open] ( arg )* RPAR[$close] )
            {
                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:317:8: ^( ARGS[$open] ( arg )* RPAR[$close] )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(ARGS, open)
                , root_1);

                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:317:22: ( arg )*
                while ( stream_arg.hasNext() ) {
                    adaptor.addChild(root_1, stream_arg.nextTree());

                }
                stream_arg.reset();

                adaptor.addChild(root_1, 
                (CtrlTree)adaptor.create(RPAR, close)
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "arg_list"


    public static class arg_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:323:1: arg : ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) );
    public final CtrlParser.arg_return arg() throws RecognitionException {
        CtrlParser.arg_return retval = new CtrlParser.arg_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token OUT95=null;
        Token ID96=null;
        Token ID97=null;
        Token DONT_CARE98=null;
        CtrlParser.literal_return literal99 =null;


        CtrlTree OUT95_tree=null;
        CtrlTree ID96_tree=null;
        CtrlTree ID97_tree=null;
        CtrlTree DONT_CARE98_tree=null;
        RewriteRuleTokenStream stream_DONT_CARE=new RewriteRuleTokenStream(adaptor,"token DONT_CARE");
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:324:3: ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) )
            int alt26=4;
            switch ( input.LA(1) ) {
            case OUT:
                {
                alt26=1;
                }
                break;
            case ID:
                {
                alt26=2;
                }
                break;
            case DONT_CARE:
                {
                alt26=3;
                }
                break;
            case FALSE:
            case INT_LIT:
            case REAL_LIT:
            case STRING_LIT:
            case TRUE:
                {
                alt26=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;

            }

            switch (alt26) {
                case 1 :
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:327:5: OUT ID
                    {
                    OUT95=(Token)match(input,OUT,FOLLOW_OUT_in_arg1696); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT95);


                    ID96=(Token)match(input,ID,FOLLOW_ID_in_arg1698); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID96);


                    // AST REWRITE
                    // elements: OUT, ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CtrlTree)adaptor.nil();
                    // 327:12: -> ^( ARG OUT ID )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:327:15: ^( ARG OUT ID )
                        {
                        CtrlTree root_1 = (CtrlTree)adaptor.nil();
                        root_1 = (CtrlTree)adaptor.becomeRoot(
                        (CtrlTree)adaptor.create(ARG, "ARG")
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
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:331:5: ID
                    {
                    ID97=(Token)match(input,ID,FOLLOW_ID_in_arg1729); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID97);


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

                    root_0 = (CtrlTree)adaptor.nil();
                    // 331:8: -> ^( ARG ID )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:331:11: ^( ARG ID )
                        {
                        CtrlTree root_1 = (CtrlTree)adaptor.nil();
                        root_1 = (CtrlTree)adaptor.becomeRoot(
                        (CtrlTree)adaptor.create(ARG, "ARG")
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
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:335:5: DONT_CARE
                    {
                    DONT_CARE98=(Token)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg1758); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DONT_CARE.add(DONT_CARE98);


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

                    root_0 = (CtrlTree)adaptor.nil();
                    // 335:15: -> ^( ARG DONT_CARE )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:335:18: ^( ARG DONT_CARE )
                        {
                        CtrlTree root_1 = (CtrlTree)adaptor.nil();
                        root_1 = (CtrlTree)adaptor.becomeRoot(
                        (CtrlTree)adaptor.create(ARG, "ARG")
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
                    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:336:5: literal
                    {
                    pushFollow(FOLLOW_literal_in_arg1775);
                    literal99=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_literal.add(literal99.getTree());

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

                    root_0 = (CtrlTree)adaptor.nil();
                    // 336:13: -> ^( ARG literal )
                    {
                        // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:336:16: ^( ARG literal )
                        {
                        CtrlTree root_1 = (CtrlTree)adaptor.nil();
                        root_1 = (CtrlTree)adaptor.becomeRoot(
                        (CtrlTree)adaptor.create(ARG, "ARG")
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

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "arg"


    public static class literal_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "literal"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:339:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlParser.literal_return literal() throws RecognitionException {
        CtrlParser.literal_return retval = new CtrlParser.literal_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token set100=null;

        CtrlTree set100_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:340:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:
            {
            root_0 = (CtrlTree)adaptor.nil();


            set100=(Token)input.LT(1);

            if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CtrlTree)adaptor.create(set100)
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

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "literal"


    public static class rule_name_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "rule_name"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:361:1: rule_name : qual_name ->;
    public final CtrlParser.rule_name_return rule_name() throws RecognitionException {
        CtrlParser.rule_name_return retval = new CtrlParser.rule_name_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlParser.qual_name_return qual_name101 =null;


        RewriteRuleSubtreeStream stream_qual_name=new RewriteRuleSubtreeStream(adaptor,"rule qual_name");
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:362:3: ( qual_name ->)
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:362:5: qual_name
            {
            pushFollow(FOLLOW_qual_name_in_rule_name1885);
            qual_name101=qual_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qual_name.add(qual_name101.getTree());

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

            root_0 = (CtrlTree)adaptor.nil();
            // 363:5: ->
            {
                adaptor.addChild(root_0,  helper.qualify((qual_name101!=null?((CtrlTree)qual_name101.tree):null)) );

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "rule_name"


    public static class var_decl_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_decl"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:367:1: var_decl : var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) ;
    public final CtrlParser.var_decl_return var_decl() throws RecognitionException {
        CtrlParser.var_decl_return retval = new CtrlParser.var_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token ID103=null;
        Token COMMA104=null;
        Token ID105=null;
        CtrlParser.var_type_return var_type102 =null;


        CtrlTree ID103_tree=null;
        CtrlTree COMMA104_tree=null;
        CtrlTree ID105_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:368:2: ( var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:370:4: var_type ID ( COMMA ID )*
            {
            pushFollow(FOLLOW_var_type_in_var_decl1915);
            var_type102=var_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_var_type.add(var_type102.getTree());

            ID103=(Token)match(input,ID,FOLLOW_ID_in_var_decl1917); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID103);


            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:370:16: ( COMMA ID )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==COMMA) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:370:17: COMMA ID
            	    {
            	    COMMA104=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_decl1920); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA104);


            	    ID105=(Token)match(input,ID,FOLLOW_ID_in_var_decl1922); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ID105);


            	    }
            	    break;

            	default :
            	    break loop27;
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

            root_0 = (CtrlTree)adaptor.nil();
            // 370:28: -> ^( VAR var_type ( ID )+ )
            {
                // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:370:31: ^( VAR var_type ( ID )+ )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(VAR, "VAR")
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

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "var_decl"


    public static class var_type_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_type"
    // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:374:1: var_type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlParser.var_type_return var_type() throws RecognitionException {
        CtrlParser.var_type_return retval = new CtrlParser.var_type_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token set106=null;

        CtrlTree set106_tree=null;

        try {
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:375:2: ( NODE | BOOL | STRING | INT | REAL )
            // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:
            {
            root_0 = (CtrlTree)adaptor.nil();


            set106=(Token)input.LT(1);

            if ( input.LA(1)==BOOL||input.LA(1)==INT||input.LA(1)==NODE||input.LA(1)==REAL||input.LA(1)==STRING ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CtrlTree)adaptor.create(set106)
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

            retval.tree = (CtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "var_type"

    // $ANTLR start synpred1_Ctrl
    public final void synpred1_Ctrl_fragment() throws RecognitionException {
        // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:215:33: ( ELSE )
        // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:215:34: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred1_Ctrl1029); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred1_Ctrl

    // $ANTLR start synpred2_Ctrl
    public final void synpred2_Ctrl_fragment() throws RecognitionException {
        // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:219:17: ( ELSE )
        // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:219:18: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred2_Ctrl1069); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred2_Ctrl

    // $ANTLR start synpred3_Ctrl
    public final void synpred3_Ctrl_fragment() throws RecognitionException {
        // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:222:20: ( OR )
        // E:\\Eclipse\\groove-head\\src\\groove\\control\\parse\\Ctrl.g:222:21: OR
        {
        match(input,OR,FOLLOW_OR_in_synpred3_Ctrl1104); if (state.failed) return ;

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


 

    public static final BitSet FOLLOW_package_decl_in_program166 = new BitSet(new long[]{0x48A0089974052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_import_decl_in_program172 = new BitSet(new long[]{0x48A0089974052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_function_in_program180 = new BitSet(new long[]{0x48A0089934052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_recipe_in_program182 = new BitSet(new long[]{0x48A0089934052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_program184 = new BitSet(new long[]{0x48A0089934052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_EOF_in_program188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_package_decl325 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_qual_name_in_package_decl327 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_SEMI_in_package_decl331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_decl398 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_qual_name_in_import_decl401 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_SEMI_in_import_decl403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_qual_name426 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_DOT_in_qual_name429 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_qual_name433 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_RECIPE_in_recipe473 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_recipe476 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_par_list_in_recipe478 = new BitSet(new long[]{0x0002000800000000L});
    public static final BitSet FOLLOW_PRIORITY_in_recipe481 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_INT_LIT_in_recipe484 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_block_in_recipe488 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function524 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_function527 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_par_list_in_function529 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_block_in_function531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_par_list562 = new BitSet(new long[]{0x4220108100002000L});
    public static final BitSet FOLLOW_par_in_par_list565 = new BitSet(new long[]{0x0200000000020000L});
    public static final BitSet FOLLOW_COMMA_in_par_list568 = new BitSet(new long[]{0x4020108100002000L});
    public static final BitSet FOLLOW_par_in_par_list570 = new BitSet(new long[]{0x0200000000020000L});
    public static final BitSet FOLLOW_RPAR_in_par_list576 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_par621 = new BitSet(new long[]{0x4020008100002000L});
    public static final BitSet FOLLOW_var_type_in_par623 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_par625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_type_in_par658 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_par660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LCURLY_in_block699 = new BitSet(new long[]{0x4830089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_block701 = new BitSet(new long[]{0x4830089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_RCURLY_in_block706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_stat738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat755 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATOM_in_stat775 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_stat799 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat802 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_cond_in_stat805 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat807 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat830 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat833 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_cond_in_stat836 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat838 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat841 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_stat846 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat848 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000014L});
    public static final BitSet FOLLOW_WHILE_in_stat891 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat893 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_cond_in_stat895 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat960 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat962 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_cond_in_stat964 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_stat1013 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat1016 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_cond_in_stat1019 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat1021 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat1024 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_ELSE_in_stat1034 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat1037 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_stat1061 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat1064 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_ELSE_in_stat1074 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat1077 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_stat1096 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat1099 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_OR_in_stat1109 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat1112 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_expr_in_stat1127 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat1129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat1144 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat1146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cond_atom_in_cond1171 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_BAR_in_cond1180 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_cond_atom_in_cond1182 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_TRUE_in_cond_atom1228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_cond_atom1249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr2_in_expr1279 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_BAR_in_expr1287 = new BitSet(new long[]{0x0800081010000040L});
    public static final BitSet FOLLOW_expr2_in_expr1289 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_expr_atom_in_expr21370 = new BitSet(new long[]{0x0001000000000202L});
    public static final BitSet FOLLOW_PLUS_in_expr21380 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERISK_in_expr21407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expr21462 = new BitSet(new long[]{0x0000081010000040L});
    public static final BitSet FOLLOW_expr_atom_in_expr21464 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expr_atom1493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expr_atom1510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_atom1525 = new BitSet(new long[]{0x0800081010000040L});
    public static final BitSet FOLLOW_expr_in_expr_atom1527 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_RPAR_in_expr_atom1531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expr_atom1559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_name_in_call1589 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_arg_list_in_call1591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_arg_list1633 = new BitSet(new long[]{0x8240100212080000L,0x0000000000000001L});
    public static final BitSet FOLLOW_arg_in_arg_list1636 = new BitSet(new long[]{0x0200000000020000L});
    public static final BitSet FOLLOW_COMMA_in_arg_list1639 = new BitSet(new long[]{0x8040100212080000L,0x0000000000000001L});
    public static final BitSet FOLLOW_arg_in_arg_list1641 = new BitSet(new long[]{0x0200000000020000L});
    public static final BitSet FOLLOW_RPAR_in_arg_list1649 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_arg1696 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_arg1698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_arg1729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg1758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_arg1775 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qual_name_in_rule_name1885 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_type_in_var_decl1915 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_var_decl1917 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_COMMA_in_var_decl1920 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_var_decl1922 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_ELSE_in_synpred1_Ctrl1029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred2_Ctrl1069 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_synpred3_Ctrl1104 = new BitSet(new long[]{0x0000000000000002L});

}