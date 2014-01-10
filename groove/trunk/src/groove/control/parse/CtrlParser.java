// $ANTLR 3.4 D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g 2014-01-10 13:55:05

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALAP", "AMP", "ANY", "ARG", "ARGS", "ASTERISK", "BAR", "BLOCK", "BOOL", "BSLASH", "CALL", "CHOICE", "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", "ELSE", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF", "IMPORT", "INT", "INT_LIT", "IntegerNumber", "LCURLY", "LPAR", "MINUS", "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT", "PACKAGE", "PAR", "PARS", "PLUS", "PRIORITY", "PROGRAM", "QUOTE", "RCURLY", "REAL", "REAL_LIT", "RECIPE", "RECIPES", "RPAR", "SEMI", "SHARP", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", "TRUE", "TRY", "UNTIL", "VAR", "WHILE", "WS"
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
    public static final int PAR=44;
    public static final int PARS=45;
    public static final int PLUS=46;
    public static final int PRIORITY=47;
    public static final int PROGRAM=48;
    public static final int QUOTE=49;
    public static final int RCURLY=50;
    public static final int REAL=51;
    public static final int REAL_LIT=52;
    public static final int RECIPE=53;
    public static final int RECIPES=54;
    public static final int RPAR=55;
    public static final int SEMI=56;
    public static final int SHARP=57;
    public static final int SL_COMMENT=58;
    public static final int STAR=59;
    public static final int STRING=60;
    public static final int STRING_LIT=61;
    public static final int TRUE=62;
    public static final int TRY=63;
    public static final int UNTIL=64;
    public static final int VAR=65;
    public static final int WHILE=66;
    public static final int WS=67;

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
    public String getGrammarFileName() { return "D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g"; }


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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:74:1: program : package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM package_decl ( import_decl )* ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* RCURLY ) ) ;
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
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:75:3: ( package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM package_decl ( import_decl )* ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* RCURLY ) ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:79:5: package_decl ( import_decl )* ( function | recipe | stat )* EOF
            {
            pushFollow(FOLLOW_package_decl_in_program151);
            package_decl1=package_decl();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_package_decl.add(package_decl1.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:80:5: ( import_decl )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==IMPORT) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:80:5: import_decl
            	    {
            	    pushFollow(FOLLOW_import_decl_in_program157);
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


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:81:5: ( function | recipe | stat )*
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
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:81:6: function
            	    {
            	    pushFollow(FOLLOW_function_in_program165);
            	    function3=function();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_function.add(function3.getTree());

            	    }
            	    break;
            	case 2 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:81:15: recipe
            	    {
            	    pushFollow(FOLLOW_recipe_in_program167);
            	    recipe4=recipe();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_recipe.add(recipe4.getTree());

            	    }
            	    break;
            	case 3 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:81:22: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_program169);
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


            EOF6=(Token)match(input,EOF,FOLLOW_EOF_in_program173); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EOF.add(EOF6);


            if ( state.backtracking==0 ) { helper.checkEOF(EOF6_tree); }

            // AST REWRITE
            // elements: import_decl, package_decl, recipe, function, stat
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CtrlTree)adaptor.nil();
            // 83:5: -> ^( PROGRAM package_decl ( import_decl )* ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* RCURLY ) )
            {
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:83:8: ^( PROGRAM package_decl ( import_decl )* ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* RCURLY ) )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(PROGRAM, "PROGRAM")
                , root_1);

                adaptor.addChild(root_1, stream_package_decl.nextTree());

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:85:11: ( import_decl )*
                while ( stream_import_decl.hasNext() ) {
                    adaptor.addChild(root_1, stream_import_decl.nextTree());

                }
                stream_import_decl.reset();

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:86:11: ^( FUNCTIONS ( function )* )
                {
                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                root_2 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(FUNCTIONS, "FUNCTIONS")
                , root_2);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:86:23: ( function )*
                while ( stream_function.hasNext() ) {
                    adaptor.addChild(root_2, stream_function.nextTree());

                }
                stream_function.reset();

                adaptor.addChild(root_1, root_2);
                }

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:87:11: ^( RECIPES ( recipe )* )
                {
                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                root_2 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(RECIPES, "RECIPES")
                , root_2);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:87:21: ( recipe )*
                while ( stream_recipe.hasNext() ) {
                    adaptor.addChild(root_2, stream_recipe.nextTree());

                }
                stream_recipe.reset();

                adaptor.addChild(root_1, root_2);
                }

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:88:11: ^( BLOCK ( stat )* RCURLY )
                {
                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                root_2 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                , root_2);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:88:19: ( stat )*
                while ( stream_stat.hasNext() ) {
                    adaptor.addChild(root_2, stream_stat.nextTree());

                }
                stream_stat.reset();

                adaptor.addChild(root_2, 
                (CtrlTree)adaptor.create(RCURLY, "RCURLY")
                );

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:93:1: package_decl : (key= PACKAGE qual_name close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->) ;
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:94:3: ( (key= PACKAGE qual_name close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:96:5: (key= PACKAGE qual_name close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->)
            {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:96:5: (key= PACKAGE qual_name close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->)
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:96:7: key= PACKAGE qual_name close= SEMI
                    {
                    key=(Token)match(input,PACKAGE,FOLLOW_PACKAGE_in_package_decl308); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PACKAGE.add(key);


                    pushFollow(FOLLOW_qual_name_in_package_decl310);
                    qual_name7=qual_name();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_qual_name.add(qual_name7.getTree());

                    close=(Token)match(input,SEMI,FOLLOW_SEMI_in_package_decl314); if (state.failed) return retval; 
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
                    // 98:7: -> ^( PACKAGE[$key] qual_name SEMI[$close] )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:98:10: ^( PACKAGE[$key] qual_name SEMI[$close] )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:99:7: 
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
                    // 99:7: ->
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:104:1: import_decl : IMPORT ^ qual_name SEMI ;
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:105:3: ( IMPORT ^ qual_name SEMI )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:107:5: IMPORT ^ qual_name SEMI
            {
            root_0 = (CtrlTree)adaptor.nil();


            IMPORT8=(Token)match(input,IMPORT,FOLLOW_IMPORT_in_import_decl381); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IMPORT8_tree = 
            (CtrlTree)adaptor.create(IMPORT8)
            ;
            root_0 = (CtrlTree)adaptor.becomeRoot(IMPORT8_tree, root_0);
            }

            pushFollow(FOLLOW_qual_name_in_import_decl384);
            qual_name9=qual_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qual_name9.getTree());

            SEMI10=(Token)match(input,SEMI,FOLLOW_SEMI_in_import_decl386); if (state.failed) return retval;
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:113:1: qual_name :ids+= ID ( DOT ids+= ID )* ->;
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:114:3: (ids+= ID ( DOT ids+= ID )* ->)
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:114:5: ids+= ID ( DOT ids+= ID )*
            {
            ids=(Token)match(input,ID,FOLLOW_ID_in_qual_name409); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ids);

            if (list_ids==null) list_ids=new ArrayList();
            list_ids.add(ids);


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:114:13: ( DOT ids+= ID )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==DOT) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:114:14: DOT ids+= ID
            	    {
            	    DOT11=(Token)match(input,DOT,FOLLOW_DOT_in_qual_name412); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(DOT11);


            	    ids=(Token)match(input,ID,FOLLOW_ID_in_qual_name416); if (state.failed) return retval; 
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
            // 115:5: ->
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:121:1: recipe : RECIPE ^ ID par_list block ;
    public final CtrlParser.recipe_return recipe() throws RecognitionException {
        CtrlParser.recipe_return retval = new CtrlParser.recipe_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token RECIPE12=null;
        Token ID13=null;
        CtrlParser.par_list_return par_list14 =null;

        CtrlParser.block_return block15 =null;


        CtrlTree RECIPE12_tree=null;
        CtrlTree ID13_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:122:3: ( RECIPE ^ ID par_list block )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:124:5: RECIPE ^ ID par_list block
            {
            root_0 = (CtrlTree)adaptor.nil();


            RECIPE12=(Token)match(input,RECIPE,FOLLOW_RECIPE_in_recipe451); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RECIPE12_tree = 
            (CtrlTree)adaptor.create(RECIPE12)
            ;
            root_0 = (CtrlTree)adaptor.becomeRoot(RECIPE12_tree, root_0);
            }

            ID13=(Token)match(input,ID,FOLLOW_ID_in_recipe454); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID13_tree = 
            (CtrlTree)adaptor.create(ID13)
            ;
            adaptor.addChild(root_0, ID13_tree);
            }

            pushFollow(FOLLOW_par_list_in_recipe456);
            par_list14=par_list();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, par_list14.getTree());

            pushFollow(FOLLOW_block_in_recipe472);
            block15=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block15.getTree());

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:135:1: function : FUNCTION ^ ID par_list block ;
    public final CtrlParser.function_return function() throws RecognitionException {
        CtrlParser.function_return retval = new CtrlParser.function_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token FUNCTION16=null;
        Token ID17=null;
        CtrlParser.par_list_return par_list18 =null;

        CtrlParser.block_return block19 =null;


        CtrlTree FUNCTION16_tree=null;
        CtrlTree ID17_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:136:3: ( FUNCTION ^ ID par_list block )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:138:5: FUNCTION ^ ID par_list block
            {
            root_0 = (CtrlTree)adaptor.nil();


            FUNCTION16=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function503); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            FUNCTION16_tree = 
            (CtrlTree)adaptor.create(FUNCTION16)
            ;
            root_0 = (CtrlTree)adaptor.becomeRoot(FUNCTION16_tree, root_0);
            }

            ID17=(Token)match(input,ID,FOLLOW_ID_in_function506); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID17_tree = 
            (CtrlTree)adaptor.create(ID17)
            ;
            adaptor.addChild(root_0, ID17_tree);
            }

            pushFollow(FOLLOW_par_list_in_function508);
            par_list18=par_list();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, par_list18.getTree());

            pushFollow(FOLLOW_block_in_function510);
            block19=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block19.getTree());

            if ( state.backtracking==0 ) { helper.declareCtrlUnit(FUNCTION16_tree); }

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:145:1: par_list : LPAR ( par ( COMMA par )* )? RPAR -> ^( PARS ( par )* ) ;
    public final CtrlParser.par_list_return par_list() throws RecognitionException {
        CtrlParser.par_list_return retval = new CtrlParser.par_list_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token LPAR20=null;
        Token COMMA22=null;
        Token RPAR24=null;
        CtrlParser.par_return par21 =null;

        CtrlParser.par_return par23 =null;


        CtrlTree LPAR20_tree=null;
        CtrlTree COMMA22_tree=null;
        CtrlTree RPAR24_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_par=new RewriteRuleSubtreeStream(adaptor,"rule par");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:146:3: ( LPAR ( par ( COMMA par )* )? RPAR -> ^( PARS ( par )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:148:5: LPAR ( par ( COMMA par )* )? RPAR
            {
            LPAR20=(Token)match(input,LPAR,FOLLOW_LPAR_in_par_list541); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAR.add(LPAR20);


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:148:10: ( par ( COMMA par )* )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==BOOL||LA6_0==INT||LA6_0==NODE||LA6_0==OUT||LA6_0==REAL||LA6_0==STRING) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:148:11: par ( COMMA par )*
                    {
                    pushFollow(FOLLOW_par_in_par_list544);
                    par21=par();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_par.add(par21.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:148:15: ( COMMA par )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==COMMA) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:148:16: COMMA par
                    	    {
                    	    COMMA22=(Token)match(input,COMMA,FOLLOW_COMMA_in_par_list547); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA22);


                    	    pushFollow(FOLLOW_par_in_par_list549);
                    	    par23=par();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_par.add(par23.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);


                    }
                    break;

            }


            RPAR24=(Token)match(input,RPAR,FOLLOW_RPAR_in_par_list555); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAR.add(RPAR24);


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
            // 149:5: -> ^( PARS ( par )* )
            {
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:149:8: ^( PARS ( par )* )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(PARS, "PARS")
                , root_1);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:149:15: ( par )*
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:155:1: par : ( OUT var_type ID -> ^( PAR OUT var_type ID ) | var_type ID -> ^( PAR var_type ID ) );
    public final CtrlParser.par_return par() throws RecognitionException {
        CtrlParser.par_return retval = new CtrlParser.par_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token OUT25=null;
        Token ID27=null;
        Token ID29=null;
        CtrlParser.var_type_return var_type26 =null;

        CtrlParser.var_type_return var_type28 =null;


        CtrlTree OUT25_tree=null;
        CtrlTree ID27_tree=null;
        CtrlTree ID29_tree=null;
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:156:3: ( OUT var_type ID -> ^( PAR OUT var_type ID ) | var_type ID -> ^( PAR var_type ID ) )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==OUT) ) {
                alt7=1;
            }
            else if ( (LA7_0==BOOL||LA7_0==INT||LA7_0==NODE||LA7_0==REAL||LA7_0==STRING) ) {
                alt7=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;

            }
            switch (alt7) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:159:5: OUT var_type ID
                    {
                    OUT25=(Token)match(input,OUT,FOLLOW_OUT_in_par600); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT25);


                    pushFollow(FOLLOW_var_type_in_par602);
                    var_type26=var_type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_var_type.add(var_type26.getTree());

                    ID27=(Token)match(input,ID,FOLLOW_ID_in_par604); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID27);


                    // AST REWRITE
                    // elements: OUT, var_type, ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CtrlTree)adaptor.nil();
                    // 159:21: -> ^( PAR OUT var_type ID )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:159:24: ^( PAR OUT var_type ID )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:163:5: var_type ID
                    {
                    pushFollow(FOLLOW_var_type_in_par637);
                    var_type28=var_type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_var_type.add(var_type28.getTree());

                    ID29=(Token)match(input,ID,FOLLOW_ID_in_par639); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID29);


                    // AST REWRITE
                    // elements: var_type, ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CtrlTree)adaptor.nil();
                    // 163:17: -> ^( PAR var_type ID )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:163:20: ^( PAR var_type ID )
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:167:1: block : open= LCURLY ( stat )* close= RCURLY -> ^( BLOCK[$open] ( stat )* RCURLY[$close] ) ;
    public final CtrlParser.block_return block() throws RecognitionException {
        CtrlParser.block_return retval = new CtrlParser.block_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        CtrlParser.stat_return stat30 =null;


        CtrlTree open_tree=null;
        CtrlTree close_tree=null;
        RewriteRuleTokenStream stream_LCURLY=new RewriteRuleTokenStream(adaptor,"token LCURLY");
        RewriteRuleTokenStream stream_RCURLY=new RewriteRuleTokenStream(adaptor,"token RCURLY");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:168:3: (open= LCURLY ( stat )* close= RCURLY -> ^( BLOCK[$open] ( stat )* RCURLY[$close] ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:5: open= LCURLY ( stat )* close= RCURLY
            {
            open=(Token)match(input,LCURLY,FOLLOW_LCURLY_in_block678); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LCURLY.add(open);


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:17: ( stat )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==ALAP||LA8_0==ANY||LA8_0==BOOL||LA8_0==CHOICE||LA8_0==DO||(LA8_0 >= ID && LA8_0 <= IF)||LA8_0==INT||(LA8_0 >= LCURLY && LA8_0 <= LPAR)||LA8_0==NODE||LA8_0==OTHER||LA8_0==REAL||LA8_0==SHARP||LA8_0==STRING||(LA8_0 >= TRY && LA8_0 <= UNTIL)||LA8_0==WHILE) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:17: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_block680);
            	    stat30=stat();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_stat.add(stat30.getTree());

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            close=(Token)match(input,RCURLY,FOLLOW_RCURLY_in_block685); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RCURLY.add(close);


            // AST REWRITE
            // elements: stat, RCURLY
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CtrlTree)adaptor.nil();
            // 171:5: -> ^( BLOCK[$open] ( stat )* RCURLY[$close] )
            {
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:171:8: ^( BLOCK[$open] ( stat )* RCURLY[$close] )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(BLOCK, open)
                , root_1);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:171:23: ( stat )*
                while ( stream_stat.hasNext() ) {
                    adaptor.addChild(root_1, stream_stat.nextTree());

                }
                stream_stat.reset();

                adaptor.addChild(root_1, 
                (CtrlTree)adaptor.create(RCURLY, close)
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:174:1: stat : ( block | ALAP ^ stat | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI ^| var_decl SEMI ^);
    public final CtrlParser.stat_return stat() throws RecognitionException {
        CtrlParser.stat_return retval = new CtrlParser.stat_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token ALAP32=null;
        Token WHILE34=null;
        Token LPAR35=null;
        Token RPAR37=null;
        Token UNTIL39=null;
        Token LPAR40=null;
        Token RPAR42=null;
        Token DO44=null;
        Token WHILE46=null;
        Token LPAR47=null;
        Token RPAR49=null;
        Token UNTIL50=null;
        Token LPAR51=null;
        Token RPAR53=null;
        Token IF54=null;
        Token LPAR55=null;
        Token RPAR57=null;
        Token ELSE59=null;
        Token TRY61=null;
        Token ELSE63=null;
        Token CHOICE65=null;
        Token OR67=null;
        Token SEMI70=null;
        Token SEMI72=null;
        CtrlParser.block_return block31 =null;

        CtrlParser.stat_return stat33 =null;

        CtrlParser.cond_return cond36 =null;

        CtrlParser.stat_return stat38 =null;

        CtrlParser.cond_return cond41 =null;

        CtrlParser.stat_return stat43 =null;

        CtrlParser.stat_return stat45 =null;

        CtrlParser.cond_return cond48 =null;

        CtrlParser.cond_return cond52 =null;

        CtrlParser.cond_return cond56 =null;

        CtrlParser.stat_return stat58 =null;

        CtrlParser.stat_return stat60 =null;

        CtrlParser.stat_return stat62 =null;

        CtrlParser.stat_return stat64 =null;

        CtrlParser.stat_return stat66 =null;

        CtrlParser.stat_return stat68 =null;

        CtrlParser.expr_return expr69 =null;

        CtrlParser.var_decl_return var_decl71 =null;


        CtrlTree ALAP32_tree=null;
        CtrlTree WHILE34_tree=null;
        CtrlTree LPAR35_tree=null;
        CtrlTree RPAR37_tree=null;
        CtrlTree UNTIL39_tree=null;
        CtrlTree LPAR40_tree=null;
        CtrlTree RPAR42_tree=null;
        CtrlTree DO44_tree=null;
        CtrlTree WHILE46_tree=null;
        CtrlTree LPAR47_tree=null;
        CtrlTree RPAR49_tree=null;
        CtrlTree UNTIL50_tree=null;
        CtrlTree LPAR51_tree=null;
        CtrlTree RPAR53_tree=null;
        CtrlTree IF54_tree=null;
        CtrlTree LPAR55_tree=null;
        CtrlTree RPAR57_tree=null;
        CtrlTree ELSE59_tree=null;
        CtrlTree TRY61_tree=null;
        CtrlTree ELSE63_tree=null;
        CtrlTree CHOICE65_tree=null;
        CtrlTree OR67_tree=null;
        CtrlTree SEMI70_tree=null;
        CtrlTree SEMI72_tree=null;
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_WHILE=new RewriteRuleTokenStream(adaptor,"token WHILE");
        RewriteRuleTokenStream stream_UNTIL=new RewriteRuleTokenStream(adaptor,"token UNTIL");
        RewriteRuleSubtreeStream stream_cond=new RewriteRuleSubtreeStream(adaptor,"rule cond");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:175:2: ( block | ALAP ^ stat | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI ^| var_decl SEMI ^)
            int alt13=10;
            switch ( input.LA(1) ) {
            case LCURLY:
                {
                alt13=1;
                }
                break;
            case ALAP:
                {
                alt13=2;
                }
                break;
            case WHILE:
                {
                alt13=3;
                }
                break;
            case UNTIL:
                {
                alt13=4;
                }
                break;
            case DO:
                {
                alt13=5;
                }
                break;
            case IF:
                {
                alt13=6;
                }
                break;
            case TRY:
                {
                alt13=7;
                }
                break;
            case CHOICE:
                {
                alt13=8;
                }
                break;
            case ANY:
            case ID:
            case LPAR:
            case OTHER:
            case SHARP:
                {
                alt13=9;
                }
                break;
            case BOOL:
            case INT:
            case NODE:
            case REAL:
            case STRING:
                {
                alt13=10;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;

            }

            switch (alt13) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:176:4: block
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_block_in_stat717);
                    block31=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block31.getTree());

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:180:4: ALAP ^ stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    ALAP32=(Token)match(input,ALAP,FOLLOW_ALAP_in_stat734); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ALAP32_tree = 
                    (CtrlTree)adaptor.create(ALAP32)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(ALAP32_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat737);
                    stat33=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat33.getTree());

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:185:4: WHILE ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    WHILE34=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat758); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    WHILE34_tree = 
                    (CtrlTree)adaptor.create(WHILE34)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(WHILE34_tree, root_0);
                    }

                    LPAR35=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat761); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat764);
                    cond36=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond36.getTree());

                    RPAR37=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat766); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat769);
                    stat38=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat38.getTree());

                    }
                    break;
                case 4 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:189:5: UNTIL ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    UNTIL39=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat789); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    UNTIL39_tree = 
                    (CtrlTree)adaptor.create(UNTIL39)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(UNTIL39_tree, root_0);
                    }

                    LPAR40=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat792); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat795);
                    cond41=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond41.getTree());

                    RPAR42=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat797); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat800);
                    stat43=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat43.getTree());

                    }
                    break;
                case 5 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:190:4: DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                    {
                    DO44=(Token)match(input,DO,FOLLOW_DO_in_stat805); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DO.add(DO44);


                    pushFollow(FOLLOW_stat_in_stat807);
                    stat45=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_stat.add(stat45.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:191:4: ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==WHILE) ) {
                        alt9=1;
                    }
                    else if ( (LA9_0==UNTIL) ) {
                        alt9=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 0, input);

                        throw nvae;

                    }
                    switch (alt9) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:196:7: WHILE LPAR cond RPAR
                            {
                            WHILE46=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat850); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_WHILE.add(WHILE46);


                            LPAR47=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat852); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR47);


                            pushFollow(FOLLOW_cond_in_stat854);
                            cond48=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond48.getTree());

                            RPAR49=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat856); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR49);


                            // AST REWRITE
                            // elements: stat, cond, stat, WHILE
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {

                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (CtrlTree)adaptor.nil();
                            // 196:28: -> ^( BLOCK stat ^( WHILE cond stat ) )
                            {
                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:196:31: ^( BLOCK stat ^( WHILE cond stat ) )
                                {
                                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                                root_1 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:196:44: ^( WHILE cond stat )
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
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:203:5: UNTIL LPAR cond RPAR
                            {
                            UNTIL50=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat919); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_UNTIL.add(UNTIL50);


                            LPAR51=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat921); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR51);


                            pushFollow(FOLLOW_cond_in_stat923);
                            cond52=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond52.getTree());

                            RPAR53=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat925); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR53);


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
                            // 203:26: -> ^( BLOCK stat ^( UNTIL cond stat ) )
                            {
                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:203:29: ^( BLOCK stat ^( UNTIL cond stat ) )
                                {
                                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                                root_1 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:203:42: ^( UNTIL cond stat )
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
                case 6 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:209:5: IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    IF54=(Token)match(input,IF,FOLLOW_IF_in_stat972); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IF54_tree = 
                    (CtrlTree)adaptor.create(IF54)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(IF54_tree, root_0);
                    }

                    LPAR55=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat975); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat978);
                    cond56=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond56.getTree());

                    RPAR57=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat980); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat983);
                    stat58=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat58.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:209:31: ( ( ELSE )=> ELSE ! stat )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==ELSE) ) {
                        int LA10_1 = input.LA(2);

                        if ( (synpred1_Ctrl()) ) {
                            alt10=1;
                        }
                    }
                    switch (alt10) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:209:33: ( ELSE )=> ELSE ! stat
                            {
                            ELSE59=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat993); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat996);
                            stat60=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat60.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 7 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:213:5: TRY ^ stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    TRY61=(Token)match(input,TRY,FOLLOW_TRY_in_stat1020); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRY61_tree = 
                    (CtrlTree)adaptor.create(TRY61)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(TRY61_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat1023);
                    stat62=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat62.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:213:15: ( ( ELSE )=> ELSE ! stat )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==ELSE) ) {
                        int LA11_1 = input.LA(2);

                        if ( (synpred2_Ctrl()) ) {
                            alt11=1;
                        }
                    }
                    switch (alt11) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:213:17: ( ELSE )=> ELSE ! stat
                            {
                            ELSE63=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat1033); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat1036);
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:216:5: CHOICE ^ stat ( ( OR )=> OR ! stat )+
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    CHOICE65=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_stat1055); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHOICE65_tree = 
                    (CtrlTree)adaptor.create(CHOICE65)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(CHOICE65_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat1058);
                    stat66=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat66.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:216:18: ( ( OR )=> OR ! stat )+
                    int cnt12=0;
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==OR) ) {
                            int LA12_20 = input.LA(2);

                            if ( (synpred3_Ctrl()) ) {
                                alt12=1;
                            }


                        }


                        switch (alt12) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:216:20: ( OR )=> OR ! stat
                    	    {
                    	    OR67=(Token)match(input,OR,FOLLOW_OR_in_stat1068); if (state.failed) return retval;

                    	    pushFollow(FOLLOW_stat_in_stat1071);
                    	    stat68=stat();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat68.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt12 >= 1 ) break loop12;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(12, input);
                                throw eee;
                        }
                        cnt12++;
                    } while (true);


                    }
                    break;
                case 9 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:219:4: expr SEMI ^
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_expr_in_stat1086);
                    expr69=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr69.getTree());

                    SEMI70=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat1088); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SEMI70_tree = 
                    (CtrlTree)adaptor.create(SEMI70)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(SEMI70_tree, root_0);
                    }

                    }
                    break;
                case 10 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:222:4: var_decl SEMI ^
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_var_decl_in_stat1103);
                    var_decl71=var_decl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, var_decl71.getTree());

                    SEMI72=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat1105); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SEMI72_tree = 
                    (CtrlTree)adaptor.create(SEMI72)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(SEMI72_tree, root_0);
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:226:1: cond : cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) ;
    public final CtrlParser.cond_return cond() throws RecognitionException {
        CtrlParser.cond_return retval = new CtrlParser.cond_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token BAR74=null;
        CtrlParser.cond_atom_return cond_atom73 =null;

        CtrlParser.cond_atom_return cond_atom75 =null;


        CtrlTree BAR74_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_cond_atom=new RewriteRuleSubtreeStream(adaptor,"rule cond_atom");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:227:2: ( cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:229:4: cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            {
            pushFollow(FOLLOW_cond_atom_in_cond1130);
            cond_atom73=cond_atom();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom73.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:230:4: ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==BAR) ) {
                alt15=1;
            }
            else if ( (LA15_0==RPAR) ) {
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:230:6: ( BAR cond_atom )+
                    {
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:230:6: ( BAR cond_atom )+
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
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:230:7: BAR cond_atom
                    	    {
                    	    BAR74=(Token)match(input,BAR,FOLLOW_BAR_in_cond1139); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR74);


                    	    pushFollow(FOLLOW_cond_atom_in_cond1141);
                    	    cond_atom75=cond_atom();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom75.getTree());

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
                    // 230:23: -> ^( CHOICE cond_atom ( cond_atom )+ )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:230:26: ^( CHOICE cond_atom ( cond_atom )+ )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:231:6: 
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
                    // 231:6: -> cond_atom
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:235:1: cond_atom : ( TRUE | call );
    public final CtrlParser.cond_atom_return cond_atom() throws RecognitionException {
        CtrlParser.cond_atom_return retval = new CtrlParser.cond_atom_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token TRUE76=null;
        CtrlParser.call_return call77 =null;


        CtrlTree TRUE76_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:236:2: ( TRUE | call )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==TRUE) ) {
                alt16=1;
            }
            else if ( (LA16_0==ID) ) {
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:238:4: TRUE
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    TRUE76=(Token)match(input,TRUE,FOLLOW_TRUE_in_cond_atom1187); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRUE76_tree = 
                    (CtrlTree)adaptor.create(TRUE76)
                    ;
                    adaptor.addChild(root_0, TRUE76_tree);
                    }

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:242:5: call
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_cond_atom1208);
                    call77=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call77.getTree());

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:245:1: expr : expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) ;
    public final CtrlParser.expr_return expr() throws RecognitionException {
        CtrlParser.expr_return retval = new CtrlParser.expr_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token BAR79=null;
        CtrlParser.expr2_return expr278 =null;

        CtrlParser.expr2_return expr280 =null;


        CtrlTree BAR79_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_expr2=new RewriteRuleSubtreeStream(adaptor,"rule expr2");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:246:2: ( expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:250:4: expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            {
            pushFollow(FOLLOW_expr2_in_expr1238);
            expr278=expr2();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expr2.add(expr278.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:251:4: ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==BAR) ) {
                alt18=1;
            }
            else if ( ((LA18_0 >= RPAR && LA18_0 <= SEMI)) ) {
                alt18=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;

            }
            switch (alt18) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:251:6: ( BAR expr2 )+
                    {
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:251:6: ( BAR expr2 )+
                    int cnt17=0;
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( (LA17_0==BAR) ) {
                            alt17=1;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:251:7: BAR expr2
                    	    {
                    	    BAR79=(Token)match(input,BAR,FOLLOW_BAR_in_expr1246); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR79);


                    	    pushFollow(FOLLOW_expr2_in_expr1248);
                    	    expr280=expr2();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expr2.add(expr280.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt17 >= 1 ) break loop17;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(17, input);
                                throw eee;
                        }
                        cnt17++;
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
                    // 251:19: -> ^( CHOICE expr2 ( expr2 )+ )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:251:22: ^( CHOICE expr2 ( expr2 )+ )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:252:6: 
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
                    // 252:6: -> expr2
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:256:1: expr2 : (e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR $e) RCURLY[$plus] ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e) |op= SHARP expr_atom -> ^( ALAP[$op] expr_atom ) );
    public final CtrlParser.expr2_return expr2() throws RecognitionException {
        CtrlParser.expr2_return retval = new CtrlParser.expr2_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token plus=null;
        Token ast=null;
        Token op=null;
        CtrlParser.expr_atom_return e =null;

        CtrlParser.expr_atom_return expr_atom81 =null;


        CtrlTree plus_tree=null;
        CtrlTree ast_tree=null;
        CtrlTree op_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_SHARP=new RewriteRuleTokenStream(adaptor,"token SHARP");
        RewriteRuleTokenStream stream_ASTERISK=new RewriteRuleTokenStream(adaptor,"token ASTERISK");
        RewriteRuleSubtreeStream stream_expr_atom=new RewriteRuleSubtreeStream(adaptor,"rule expr_atom");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:257:3: (e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR $e) RCURLY[$plus] ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e) |op= SHARP expr_atom -> ^( ALAP[$op] expr_atom ) )
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==ANY||LA20_0==ID||LA20_0==LPAR||LA20_0==OTHER) ) {
                alt20=1;
            }
            else if ( (LA20_0==SHARP) ) {
                alt20=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;

            }
            switch (alt20) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:265:5: e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR $e) RCURLY[$plus] ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e)
                    {
                    pushFollow(FOLLOW_expr_atom_in_expr21329);
                    e=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(e.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:266:5: (plus= PLUS -> ^( BLOCK $e ^( STAR $e) RCURLY[$plus] ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e)
                    int alt19=3;
                    switch ( input.LA(1) ) {
                    case PLUS:
                        {
                        alt19=1;
                        }
                        break;
                    case ASTERISK:
                        {
                        alt19=2;
                        }
                        break;
                    case BAR:
                    case RPAR:
                    case SEMI:
                        {
                        alt19=3;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 19, 0, input);

                        throw nvae;

                    }

                    switch (alt19) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:266:7: plus= PLUS
                            {
                            plus=(Token)match(input,PLUS,FOLLOW_PLUS_in_expr21339); if (state.failed) return retval; 
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
                            // 266:17: -> ^( BLOCK $e ^( STAR $e) RCURLY[$plus] )
                            {
                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:266:20: ^( BLOCK $e ^( STAR $e) RCURLY[$plus] )
                                {
                                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                                root_1 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_e.nextTree());

                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:266:31: ^( STAR $e)
                                {
                                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                                root_2 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(STAR, "STAR")
                                , root_2);

                                adaptor.addChild(root_2, stream_e.nextTree());

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_1, 
                                (CtrlTree)adaptor.create(RCURLY, plus)
                                );

                                adaptor.addChild(root_0, root_1);
                                }

                            }


                            retval.tree = root_0;
                            }

                            }
                            break;
                        case 2 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:267:7: ast= ASTERISK
                            {
                            ast=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_expr21368); if (state.failed) return retval; 
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
                            // 267:20: -> ^( STAR[$ast] $e)
                            {
                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:267:23: ^( STAR[$ast] $e)
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
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:268:7: 
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
                            // 268:7: -> $e
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:274:5: op= SHARP expr_atom
                    {
                    op=(Token)match(input,SHARP,FOLLOW_SHARP_in_expr21423); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SHARP.add(op);


                    pushFollow(FOLLOW_expr_atom_in_expr21425);
                    expr_atom81=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(expr_atom81.getTree());

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
                    // 274:24: -> ^( ALAP[$op] expr_atom )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:274:27: ^( ALAP[$op] expr_atom )
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:277:1: expr_atom : ( ANY | OTHER |open= LPAR expr close= RPAR -> ^( BLOCK[$open] expr RCURLY[$close] ) | call );
    public final CtrlParser.expr_atom_return expr_atom() throws RecognitionException {
        CtrlParser.expr_atom_return retval = new CtrlParser.expr_atom_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        Token ANY82=null;
        Token OTHER83=null;
        CtrlParser.expr_return expr84 =null;

        CtrlParser.call_return call85 =null;


        CtrlTree open_tree=null;
        CtrlTree close_tree=null;
        CtrlTree ANY82_tree=null;
        CtrlTree OTHER83_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:278:2: ( ANY | OTHER |open= LPAR expr close= RPAR -> ^( BLOCK[$open] expr RCURLY[$close] ) | call )
            int alt21=4;
            switch ( input.LA(1) ) {
            case ANY:
                {
                alt21=1;
                }
                break;
            case OTHER:
                {
                alt21=2;
                }
                break;
            case LPAR:
                {
                alt21=3;
                }
                break;
            case ID:
                {
                alt21=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;

            }

            switch (alt21) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:280:4: ANY
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    ANY82=(Token)match(input,ANY,FOLLOW_ANY_in_expr_atom1454); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ANY82_tree = 
                    (CtrlTree)adaptor.create(ANY82)
                    ;
                    adaptor.addChild(root_0, ANY82_tree);
                    }

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:284:4: OTHER
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    OTHER83=(Token)match(input,OTHER,FOLLOW_OTHER_in_expr_atom1471); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OTHER83_tree = 
                    (CtrlTree)adaptor.create(OTHER83)
                    ;
                    adaptor.addChild(root_0, OTHER83_tree);
                    }

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:287:4: open= LPAR expr close= RPAR
                    {
                    open=(Token)match(input,LPAR,FOLLOW_LPAR_in_expr_atom1486); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAR.add(open);


                    pushFollow(FOLLOW_expr_in_expr_atom1488);
                    expr84=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr.add(expr84.getTree());

                    close=(Token)match(input,RPAR,FOLLOW_RPAR_in_expr_atom1492); if (state.failed) return retval; 
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
                    // 288:4: -> ^( BLOCK[$open] expr RCURLY[$close] )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:288:7: ^( BLOCK[$open] expr RCURLY[$close] )
                        {
                        CtrlTree root_1 = (CtrlTree)adaptor.nil();
                        root_1 = (CtrlTree)adaptor.becomeRoot(
                        (CtrlTree)adaptor.create(BLOCK, open)
                        , root_1);

                        adaptor.addChild(root_1, stream_expr.nextTree());

                        adaptor.addChild(root_1, 
                        (CtrlTree)adaptor.create(RCURLY, close)
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 4 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:291:4: call
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_expr_atom1520);
                    call85=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call85.getTree());

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:295:1: call : rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) ;
    public final CtrlParser.call_return call() throws RecognitionException {
        CtrlParser.call_return retval = new CtrlParser.call_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlParser.rule_name_return rule_name86 =null;

        CtrlParser.arg_list_return arg_list87 =null;


        RewriteRuleSubtreeStream stream_arg_list=new RewriteRuleSubtreeStream(adaptor,"rule arg_list");
        RewriteRuleSubtreeStream stream_rule_name=new RewriteRuleSubtreeStream(adaptor,"rule rule_name");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:296:2: ( rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:300:4: rule_name ( arg_list )?
            {
            pushFollow(FOLLOW_rule_name_in_call1550);
            rule_name86=rule_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_name.add(rule_name86.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:300:14: ( arg_list )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==LPAR) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:300:14: arg_list
                    {
                    pushFollow(FOLLOW_arg_list_in_call1552);
                    arg_list87=arg_list();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg_list.add(arg_list87.getTree());

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

            root_0 = (CtrlTree)adaptor.nil();
            // 301:4: -> ^( CALL[$rule_name.start] rule_name ( arg_list )? )
            {
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:301:7: ^( CALL[$rule_name.start] rule_name ( arg_list )? )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(CALL, (rule_name86!=null?((Token)rule_name86.start):null))
                , root_1);

                adaptor.addChild(root_1, stream_rule_name.nextTree());

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:301:42: ( arg_list )?
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:307:1: arg_list : open= LPAR ( arg ( COMMA arg )* )? close= RPAR -> ^( ARGS[$open] ( arg )* RPAR[$close] ) ;
    public final CtrlParser.arg_list_return arg_list() throws RecognitionException {
        CtrlParser.arg_list_return retval = new CtrlParser.arg_list_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        Token COMMA89=null;
        CtrlParser.arg_return arg88 =null;

        CtrlParser.arg_return arg90 =null;


        CtrlTree open_tree=null;
        CtrlTree close_tree=null;
        CtrlTree COMMA89_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_arg=new RewriteRuleSubtreeStream(adaptor,"rule arg");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:308:3: (open= LPAR ( arg ( COMMA arg )* )? close= RPAR -> ^( ARGS[$open] ( arg )* RPAR[$close] ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:310:5: open= LPAR ( arg ( COMMA arg )* )? close= RPAR
            {
            open=(Token)match(input,LPAR,FOLLOW_LPAR_in_arg_list1594); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAR.add(open);


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:310:15: ( arg ( COMMA arg )* )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==DONT_CARE||LA24_0==FALSE||LA24_0==ID||LA24_0==INT_LIT||LA24_0==OUT||LA24_0==REAL_LIT||(LA24_0 >= STRING_LIT && LA24_0 <= TRUE)) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:310:16: arg ( COMMA arg )*
                    {
                    pushFollow(FOLLOW_arg_in_arg_list1597);
                    arg88=arg();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg.add(arg88.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:310:20: ( COMMA arg )*
                    loop23:
                    do {
                        int alt23=2;
                        int LA23_0 = input.LA(1);

                        if ( (LA23_0==COMMA) ) {
                            alt23=1;
                        }


                        switch (alt23) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:310:21: COMMA arg
                    	    {
                    	    COMMA89=(Token)match(input,COMMA,FOLLOW_COMMA_in_arg_list1600); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA89);


                    	    pushFollow(FOLLOW_arg_in_arg_list1602);
                    	    arg90=arg();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_arg.add(arg90.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop23;
                        }
                    } while (true);


                    }
                    break;

            }


            close=(Token)match(input,RPAR,FOLLOW_RPAR_in_arg_list1610); if (state.failed) return retval; 
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
            // 311:5: -> ^( ARGS[$open] ( arg )* RPAR[$close] )
            {
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:311:8: ^( ARGS[$open] ( arg )* RPAR[$close] )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(ARGS, open)
                , root_1);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:311:22: ( arg )*
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:317:1: arg : ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) );
    public final CtrlParser.arg_return arg() throws RecognitionException {
        CtrlParser.arg_return retval = new CtrlParser.arg_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token OUT91=null;
        Token ID92=null;
        Token ID93=null;
        Token DONT_CARE94=null;
        CtrlParser.literal_return literal95 =null;


        CtrlTree OUT91_tree=null;
        CtrlTree ID92_tree=null;
        CtrlTree ID93_tree=null;
        CtrlTree DONT_CARE94_tree=null;
        RewriteRuleTokenStream stream_DONT_CARE=new RewriteRuleTokenStream(adaptor,"token DONT_CARE");
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:318:3: ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) )
            int alt25=4;
            switch ( input.LA(1) ) {
            case OUT:
                {
                alt25=1;
                }
                break;
            case ID:
                {
                alt25=2;
                }
                break;
            case DONT_CARE:
                {
                alt25=3;
                }
                break;
            case FALSE:
            case INT_LIT:
            case REAL_LIT:
            case STRING_LIT:
            case TRUE:
                {
                alt25=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;

            }

            switch (alt25) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:321:5: OUT ID
                    {
                    OUT91=(Token)match(input,OUT,FOLLOW_OUT_in_arg1657); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT91);


                    ID92=(Token)match(input,ID,FOLLOW_ID_in_arg1659); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID92);


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
                    // 321:12: -> ^( ARG OUT ID )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:321:15: ^( ARG OUT ID )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:325:5: ID
                    {
                    ID93=(Token)match(input,ID,FOLLOW_ID_in_arg1690); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID93);


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
                    // 325:8: -> ^( ARG ID )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:325:11: ^( ARG ID )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:329:5: DONT_CARE
                    {
                    DONT_CARE94=(Token)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg1719); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DONT_CARE.add(DONT_CARE94);


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
                    // 329:15: -> ^( ARG DONT_CARE )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:329:18: ^( ARG DONT_CARE )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:330:5: literal
                    {
                    pushFollow(FOLLOW_literal_in_arg1736);
                    literal95=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_literal.add(literal95.getTree());

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
                    // 330:13: -> ^( ARG literal )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:330:16: ^( ARG literal )
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:333:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlParser.literal_return literal() throws RecognitionException {
        CtrlParser.literal_return retval = new CtrlParser.literal_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token set96=null;

        CtrlTree set96_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:334:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
            {
            root_0 = (CtrlTree)adaptor.nil();


            set96=(Token)input.LT(1);

            if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CtrlTree)adaptor.create(set96)
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:355:1: rule_name : qual_name ->;
    public final CtrlParser.rule_name_return rule_name() throws RecognitionException {
        CtrlParser.rule_name_return retval = new CtrlParser.rule_name_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlParser.qual_name_return qual_name97 =null;


        RewriteRuleSubtreeStream stream_qual_name=new RewriteRuleSubtreeStream(adaptor,"rule qual_name");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:356:3: ( qual_name ->)
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:356:5: qual_name
            {
            pushFollow(FOLLOW_qual_name_in_rule_name1846);
            qual_name97=qual_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qual_name.add(qual_name97.getTree());

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
            // 357:5: ->
            {
                adaptor.addChild(root_0,  helper.lookup((qual_name97!=null?((CtrlTree)qual_name97.tree):null)) );

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:361:1: var_decl : var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) ;
    public final CtrlParser.var_decl_return var_decl() throws RecognitionException {
        CtrlParser.var_decl_return retval = new CtrlParser.var_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token ID99=null;
        Token COMMA100=null;
        Token ID101=null;
        CtrlParser.var_type_return var_type98 =null;


        CtrlTree ID99_tree=null;
        CtrlTree COMMA100_tree=null;
        CtrlTree ID101_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:362:2: ( var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:364:4: var_type ID ( COMMA ID )*
            {
            pushFollow(FOLLOW_var_type_in_var_decl1876);
            var_type98=var_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_var_type.add(var_type98.getTree());

            ID99=(Token)match(input,ID,FOLLOW_ID_in_var_decl1878); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID99);


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:364:16: ( COMMA ID )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==COMMA) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:364:17: COMMA ID
            	    {
            	    COMMA100=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_decl1881); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA100);


            	    ID101=(Token)match(input,ID,FOLLOW_ID_in_var_decl1883); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ID101);


            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);


            // AST REWRITE
            // elements: var_type, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CtrlTree)adaptor.nil();
            // 364:28: -> ^( VAR var_type ( ID )+ )
            {
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:364:31: ^( VAR var_type ( ID )+ )
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:368:1: var_type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlParser.var_type_return var_type() throws RecognitionException {
        CtrlParser.var_type_return retval = new CtrlParser.var_type_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token set102=null;

        CtrlTree set102_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:369:2: ( NODE | BOOL | STRING | INT | REAL )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
            {
            root_0 = (CtrlTree)adaptor.nil();


            set102=(Token)input.LT(1);

            if ( input.LA(1)==BOOL||input.LA(1)==INT||input.LA(1)==NODE||input.LA(1)==REAL||input.LA(1)==STRING ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CtrlTree)adaptor.create(set102)
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
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:209:33: ( ELSE )
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:209:34: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred1_Ctrl988); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred1_Ctrl

    // $ANTLR start synpred2_Ctrl
    public final void synpred2_Ctrl_fragment() throws RecognitionException {
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:213:17: ( ELSE )
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:213:18: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred2_Ctrl1028); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred2_Ctrl

    // $ANTLR start synpred3_Ctrl
    public final void synpred3_Ctrl_fragment() throws RecognitionException {
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:216:20: ( OR )
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:216:21: OR
        {
        match(input,OR,FOLLOW_OR_in_synpred3_Ctrl1063); if (state.failed) return ;

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


 

    public static final BitSet FOLLOW_package_decl_in_program151 = new BitSet(new long[]{0x922802267A029050L,0x0000000000000005L});
    public static final BitSet FOLLOW_import_decl_in_program157 = new BitSet(new long[]{0x922802267A029050L,0x0000000000000005L});
    public static final BitSet FOLLOW_function_in_program165 = new BitSet(new long[]{0x922802265A029050L,0x0000000000000005L});
    public static final BitSet FOLLOW_recipe_in_program167 = new BitSet(new long[]{0x922802265A029050L,0x0000000000000005L});
    public static final BitSet FOLLOW_stat_in_program169 = new BitSet(new long[]{0x922802265A029050L,0x0000000000000005L});
    public static final BitSet FOLLOW_EOF_in_program173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_package_decl308 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_qual_name_in_package_decl310 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_SEMI_in_package_decl314 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_decl381 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_qual_name_in_import_decl384 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_SEMI_in_import_decl386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_qual_name409 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_DOT_in_qual_name412 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_qual_name416 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_RECIPE_in_recipe451 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_recipe454 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_par_list_in_recipe456 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_block_in_recipe472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function503 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_function506 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_par_list_in_function508 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_block_in_function510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_par_list541 = new BitSet(new long[]{0x1088042040001000L});
    public static final BitSet FOLLOW_par_in_par_list544 = new BitSet(new long[]{0x0080000000010000L});
    public static final BitSet FOLLOW_COMMA_in_par_list547 = new BitSet(new long[]{0x1008042040001000L});
    public static final BitSet FOLLOW_par_in_par_list549 = new BitSet(new long[]{0x0080000000010000L});
    public static final BitSet FOLLOW_RPAR_in_par_list555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_par600 = new BitSet(new long[]{0x1008002040001000L});
    public static final BitSet FOLLOW_var_type_in_par602 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_par604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_type_in_par637 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_par639 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LCURLY_in_block678 = new BitSet(new long[]{0x920C022658029050L,0x0000000000000005L});
    public static final BitSet FOLLOW_stat_in_block680 = new BitSet(new long[]{0x920C022658029050L,0x0000000000000005L});
    public static final BitSet FOLLOW_RCURLY_in_block685 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_stat717 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat734 = new BitSet(new long[]{0x9208022658029050L,0x0000000000000005L});
    public static final BitSet FOLLOW_stat_in_stat737 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_stat758 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat761 = new BitSet(new long[]{0x4000000008000000L});
    public static final BitSet FOLLOW_cond_in_stat764 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat766 = new BitSet(new long[]{0x9208022658029050L,0x0000000000000005L});
    public static final BitSet FOLLOW_stat_in_stat769 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat789 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat792 = new BitSet(new long[]{0x4000000008000000L});
    public static final BitSet FOLLOW_cond_in_stat795 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat797 = new BitSet(new long[]{0x9208022658029050L,0x0000000000000005L});
    public static final BitSet FOLLOW_stat_in_stat800 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_stat805 = new BitSet(new long[]{0x9208022658029050L,0x0000000000000005L});
    public static final BitSet FOLLOW_stat_in_stat807 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000005L});
    public static final BitSet FOLLOW_WHILE_in_stat850 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat852 = new BitSet(new long[]{0x4000000008000000L});
    public static final BitSet FOLLOW_cond_in_stat854 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat919 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat921 = new BitSet(new long[]{0x4000000008000000L});
    public static final BitSet FOLLOW_cond_in_stat923 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_stat972 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat975 = new BitSet(new long[]{0x4000000008000000L});
    public static final BitSet FOLLOW_cond_in_stat978 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat980 = new BitSet(new long[]{0x9208022658029050L,0x0000000000000005L});
    public static final BitSet FOLLOW_stat_in_stat983 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_ELSE_in_stat993 = new BitSet(new long[]{0x9208022658029050L,0x0000000000000005L});
    public static final BitSet FOLLOW_stat_in_stat996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_stat1020 = new BitSet(new long[]{0x9208022658029050L,0x0000000000000005L});
    public static final BitSet FOLLOW_stat_in_stat1023 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_ELSE_in_stat1033 = new BitSet(new long[]{0x9208022658029050L,0x0000000000000005L});
    public static final BitSet FOLLOW_stat_in_stat1036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_stat1055 = new BitSet(new long[]{0x9208022658029050L,0x0000000000000005L});
    public static final BitSet FOLLOW_stat_in_stat1058 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_OR_in_stat1068 = new BitSet(new long[]{0x9208022658029050L,0x0000000000000005L});
    public static final BitSet FOLLOW_stat_in_stat1071 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_expr_in_stat1086 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat1088 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat1103 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat1105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cond_atom_in_cond1130 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_BAR_in_cond1139 = new BitSet(new long[]{0x4000000008000000L});
    public static final BitSet FOLLOW_cond_atom_in_cond1141 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_TRUE_in_cond_atom1187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_cond_atom1208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr2_in_expr1238 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_BAR_in_expr1246 = new BitSet(new long[]{0x0200020408000040L});
    public static final BitSet FOLLOW_expr2_in_expr1248 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_expr_atom_in_expr21329 = new BitSet(new long[]{0x0000400000000202L});
    public static final BitSet FOLLOW_PLUS_in_expr21339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERISK_in_expr21368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expr21423 = new BitSet(new long[]{0x0000020408000040L});
    public static final BitSet FOLLOW_expr_atom_in_expr21425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expr_atom1454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expr_atom1471 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_atom1486 = new BitSet(new long[]{0x0200020408000040L});
    public static final BitSet FOLLOW_expr_in_expr_atom1488 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_RPAR_in_expr_atom1492 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expr_atom1520 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_name_in_call1550 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_arg_list_in_call1552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_arg_list1594 = new BitSet(new long[]{0x6090040089040000L});
    public static final BitSet FOLLOW_arg_in_arg_list1597 = new BitSet(new long[]{0x0080000000010000L});
    public static final BitSet FOLLOW_COMMA_in_arg_list1600 = new BitSet(new long[]{0x6010040089040000L});
    public static final BitSet FOLLOW_arg_in_arg_list1602 = new BitSet(new long[]{0x0080000000010000L});
    public static final BitSet FOLLOW_RPAR_in_arg_list1610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_arg1657 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_arg1659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_arg1690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg1719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_arg1736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qual_name_in_rule_name1846 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_type_in_var_decl1876 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_var_decl1878 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_COMMA_in_var_decl1881 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_var_decl1883 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_ELSE_in_synpred1_Ctrl988 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred2_Ctrl1028 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_synpred3_Ctrl1063 = new BitSet(new long[]{0x0000000000000002L});

}