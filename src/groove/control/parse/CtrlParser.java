// $ANTLR 3.4 D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g 2014-01-05 23:15:51

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "program"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:72:1: program : package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM package_decl ( import_decl )* ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* RCURLY ) ) ;
    public final CtrlParser.program_return program() throws RecognitionException {
        CtrlParser.program_return retval = new CtrlParser.program_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        Token EOF6=null;
        CtrlParser.package_decl_return package_decl1 =null;

        CtrlParser.import_decl_return import_decl2 =null;

        CtrlParser.function_return function3 =null;

        CtrlParser.recipe_return recipe4 =null;

        CtrlParser.stat_return stat5 =null;


        NewCtrlTree EOF6_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_package_decl=new RewriteRuleSubtreeStream(adaptor,"rule package_decl");
        RewriteRuleSubtreeStream stream_recipe=new RewriteRuleSubtreeStream(adaptor,"rule recipe");
        RewriteRuleSubtreeStream stream_import_decl=new RewriteRuleSubtreeStream(adaptor,"rule import_decl");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        RewriteRuleSubtreeStream stream_function=new RewriteRuleSubtreeStream(adaptor,"rule function");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:73:3: ( package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM package_decl ( import_decl )* ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* RCURLY ) ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:77:5: package_decl ( import_decl )* ( function | recipe | stat )* EOF
            {
            pushFollow(FOLLOW_package_decl_in_program141);
            package_decl1=package_decl();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_package_decl.add(package_decl1.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:78:5: ( import_decl )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==IMPORT) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:78:5: import_decl
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


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:79:5: ( function | recipe | stat )*
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
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:79:6: function
            	    {
            	    pushFollow(FOLLOW_function_in_program155);
            	    function3=function();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_function.add(function3.getTree());

            	    }
            	    break;
            	case 2 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:79:15: recipe
            	    {
            	    pushFollow(FOLLOW_recipe_in_program157);
            	    recipe4=recipe();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_recipe.add(recipe4.getTree());

            	    }
            	    break;
            	case 3 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:79:22: stat
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
            // elements: package_decl, stat, function, import_decl, recipe
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (NewCtrlTree)adaptor.nil();
            // 81:5: -> ^( PROGRAM package_decl ( import_decl )* ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* RCURLY ) )
            {
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:81:8: ^( PROGRAM package_decl ( import_decl )* ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* RCURLY ) )
                {
                NewCtrlTree root_1 = (NewCtrlTree)adaptor.nil();
                root_1 = (NewCtrlTree)adaptor.becomeRoot(
                (NewCtrlTree)adaptor.create(PROGRAM, "PROGRAM")
                , root_1);

                adaptor.addChild(root_1, stream_package_decl.nextTree());

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:83:11: ( import_decl )*
                while ( stream_import_decl.hasNext() ) {
                    adaptor.addChild(root_1, stream_import_decl.nextTree());

                }
                stream_import_decl.reset();

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:84:11: ^( FUNCTIONS ( function )* )
                {
                NewCtrlTree root_2 = (NewCtrlTree)adaptor.nil();
                root_2 = (NewCtrlTree)adaptor.becomeRoot(
                (NewCtrlTree)adaptor.create(FUNCTIONS, "FUNCTIONS")
                , root_2);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:84:23: ( function )*
                while ( stream_function.hasNext() ) {
                    adaptor.addChild(root_2, stream_function.nextTree());

                }
                stream_function.reset();

                adaptor.addChild(root_1, root_2);
                }

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:85:11: ^( RECIPES ( recipe )* )
                {
                NewCtrlTree root_2 = (NewCtrlTree)adaptor.nil();
                root_2 = (NewCtrlTree)adaptor.becomeRoot(
                (NewCtrlTree)adaptor.create(RECIPES, "RECIPES")
                , root_2);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:85:21: ( recipe )*
                while ( stream_recipe.hasNext() ) {
                    adaptor.addChild(root_2, stream_recipe.nextTree());

                }
                stream_recipe.reset();

                adaptor.addChild(root_1, root_2);
                }

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:86:11: ^( BLOCK ( stat )* RCURLY )
                {
                NewCtrlTree root_2 = (NewCtrlTree)adaptor.nil();
                root_2 = (NewCtrlTree)adaptor.becomeRoot(
                (NewCtrlTree)adaptor.create(BLOCK, "BLOCK")
                , root_2);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:86:19: ( stat )*
                while ( stream_stat.hasNext() ) {
                    adaptor.addChild(root_2, stream_stat.nextTree());

                }
                stream_stat.reset();

                adaptor.addChild(root_2, 
                (NewCtrlTree)adaptor.create(RCURLY, "RCURLY")
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

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "program"


    public static class package_decl_return extends ParserRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "package_decl"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:91:1: package_decl : (key= PACKAGE qual_name close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->) ;
    public final CtrlParser.package_decl_return package_decl() throws RecognitionException {
        CtrlParser.package_decl_return retval = new CtrlParser.package_decl_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        Token key=null;
        Token close=null;
        CtrlParser.qual_name_return qual_name7 =null;


        NewCtrlTree key_tree=null;
        NewCtrlTree close_tree=null;
        RewriteRuleTokenStream stream_PACKAGE=new RewriteRuleTokenStream(adaptor,"token PACKAGE");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleSubtreeStream stream_qual_name=new RewriteRuleSubtreeStream(adaptor,"rule qual_name");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:92:3: ( (key= PACKAGE qual_name close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:94:5: (key= PACKAGE qual_name close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->)
            {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:94:5: (key= PACKAGE qual_name close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->)
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:94:7: key= PACKAGE qual_name close= SEMI
                    {
                    key=(Token)match(input,PACKAGE,FOLLOW_PACKAGE_in_package_decl298); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PACKAGE.add(key);


                    pushFollow(FOLLOW_qual_name_in_package_decl300);
                    qual_name7=qual_name();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_qual_name.add(qual_name7.getTree());

                    close=(Token)match(input,SEMI,FOLLOW_SEMI_in_package_decl304); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMI.add(close);


                    if ( state.backtracking==0 ) { helper.setPackage((qual_name7!=null?((NewCtrlTree)qual_name7.tree):null)); }

                    // AST REWRITE
                    // elements: PACKAGE, SEMI, qual_name
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {

                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (NewCtrlTree)adaptor.nil();
                    // 96:7: -> ^( PACKAGE[$key] qual_name SEMI[$close] )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:96:10: ^( PACKAGE[$key] qual_name SEMI[$close] )
                        {
                        NewCtrlTree root_1 = (NewCtrlTree)adaptor.nil();
                        root_1 = (NewCtrlTree)adaptor.becomeRoot(
                        (NewCtrlTree)adaptor.create(PACKAGE, key)
                        , root_1);

                        adaptor.addChild(root_1, stream_qual_name.nextTree());

                        adaptor.addChild(root_1, 
                        (NewCtrlTree)adaptor.create(SEMI, close)
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:97:7: 
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

                    root_0 = (NewCtrlTree)adaptor.nil();
                    // 97:7: ->
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

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "package_decl"


    public static class import_decl_return extends ParserRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "import_decl"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:102:1: import_decl : IMPORT ^ qual_name SEMI ;
    public final CtrlParser.import_decl_return import_decl() throws RecognitionException {
        CtrlParser.import_decl_return retval = new CtrlParser.import_decl_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        Token IMPORT8=null;
        Token SEMI10=null;
        CtrlParser.qual_name_return qual_name9 =null;


        NewCtrlTree IMPORT8_tree=null;
        NewCtrlTree SEMI10_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:103:3: ( IMPORT ^ qual_name SEMI )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:105:5: IMPORT ^ qual_name SEMI
            {
            root_0 = (NewCtrlTree)adaptor.nil();


            IMPORT8=(Token)match(input,IMPORT,FOLLOW_IMPORT_in_import_decl371); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IMPORT8_tree = 
            (NewCtrlTree)adaptor.create(IMPORT8)
            ;
            root_0 = (NewCtrlTree)adaptor.becomeRoot(IMPORT8_tree, root_0);
            }

            pushFollow(FOLLOW_qual_name_in_import_decl374);
            qual_name9=qual_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qual_name9.getTree());

            SEMI10=(Token)match(input,SEMI,FOLLOW_SEMI_in_import_decl376); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            SEMI10_tree = 
            (NewCtrlTree)adaptor.create(SEMI10)
            ;
            adaptor.addChild(root_0, SEMI10_tree);
            }

            if ( state.backtracking==0 ) { helper.addImport((qual_name9!=null?((NewCtrlTree)qual_name9.tree):null));
                }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "import_decl"


    public static class qual_name_return extends ParserRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "qual_name"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:111:1: qual_name :ids+= ID ( DOT ids+= ID )* ->;
    public final CtrlParser.qual_name_return qual_name() throws RecognitionException {
        CtrlParser.qual_name_return retval = new CtrlParser.qual_name_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        Token DOT11=null;
        Token ids=null;
        List list_ids=null;

        NewCtrlTree DOT11_tree=null;
        NewCtrlTree ids_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:112:3: (ids+= ID ( DOT ids+= ID )* ->)
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:112:5: ids+= ID ( DOT ids+= ID )*
            {
            ids=(Token)match(input,ID,FOLLOW_ID_in_qual_name399); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ids);

            if (list_ids==null) list_ids=new ArrayList();
            list_ids.add(ids);


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:112:13: ( DOT ids+= ID )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==DOT) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:112:14: DOT ids+= ID
            	    {
            	    DOT11=(Token)match(input,DOT,FOLLOW_DOT_in_qual_name402); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(DOT11);


            	    ids=(Token)match(input,ID,FOLLOW_ID_in_qual_name406); if (state.failed) return retval; 
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

            root_0 = (NewCtrlTree)adaptor.nil();
            // 113:5: ->
            {
                adaptor.addChild(root_0,  helper.toQualName(list_ids) );

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "qual_name"


    public static class recipe_return extends ParserRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipe"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:119:1: recipe : RECIPE ^ ID LPAR ! RPAR ! block ;
    public final CtrlParser.recipe_return recipe() throws RecognitionException {
        CtrlParser.recipe_return retval = new CtrlParser.recipe_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        Token RECIPE12=null;
        Token ID13=null;
        Token LPAR14=null;
        Token RPAR15=null;
        CtrlParser.block_return block16 =null;


        NewCtrlTree RECIPE12_tree=null;
        NewCtrlTree ID13_tree=null;
        NewCtrlTree LPAR14_tree=null;
        NewCtrlTree RPAR15_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:120:3: ( RECIPE ^ ID LPAR ! RPAR ! block )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:122:5: RECIPE ^ ID LPAR ! RPAR ! block
            {
            root_0 = (NewCtrlTree)adaptor.nil();


            RECIPE12=(Token)match(input,RECIPE,FOLLOW_RECIPE_in_recipe441); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RECIPE12_tree = 
            (NewCtrlTree)adaptor.create(RECIPE12)
            ;
            root_0 = (NewCtrlTree)adaptor.becomeRoot(RECIPE12_tree, root_0);
            }

            ID13=(Token)match(input,ID,FOLLOW_ID_in_recipe444); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID13_tree = 
            (NewCtrlTree)adaptor.create(ID13)
            ;
            adaptor.addChild(root_0, ID13_tree);
            }

            LPAR14=(Token)match(input,LPAR,FOLLOW_LPAR_in_recipe446); if (state.failed) return retval;

            RPAR15=(Token)match(input,RPAR,FOLLOW_RPAR_in_recipe449); if (state.failed) return retval;

            pushFollow(FOLLOW_block_in_recipe466);
            block16=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block16.getTree());

            if ( state.backtracking==0 ) { helper.declareName(RECIPE12_tree); }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "recipe"


    public static class function_return extends ParserRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:133:1: function : FUNCTION ^ ID LPAR ! RPAR ! block ;
    public final CtrlParser.function_return function() throws RecognitionException {
        CtrlParser.function_return retval = new CtrlParser.function_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        Token FUNCTION17=null;
        Token ID18=null;
        Token LPAR19=null;
        Token RPAR20=null;
        CtrlParser.block_return block21 =null;


        NewCtrlTree FUNCTION17_tree=null;
        NewCtrlTree ID18_tree=null;
        NewCtrlTree LPAR19_tree=null;
        NewCtrlTree RPAR20_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:134:3: ( FUNCTION ^ ID LPAR ! RPAR ! block )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:136:5: FUNCTION ^ ID LPAR ! RPAR ! block
            {
            root_0 = (NewCtrlTree)adaptor.nil();


            FUNCTION17=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function497); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            FUNCTION17_tree = 
            (NewCtrlTree)adaptor.create(FUNCTION17)
            ;
            root_0 = (NewCtrlTree)adaptor.becomeRoot(FUNCTION17_tree, root_0);
            }

            ID18=(Token)match(input,ID,FOLLOW_ID_in_function500); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID18_tree = 
            (NewCtrlTree)adaptor.create(ID18)
            ;
            adaptor.addChild(root_0, ID18_tree);
            }

            LPAR19=(Token)match(input,LPAR,FOLLOW_LPAR_in_function502); if (state.failed) return retval;

            RPAR20=(Token)match(input,RPAR,FOLLOW_RPAR_in_function505); if (state.failed) return retval;

            pushFollow(FOLLOW_block_in_function508);
            block21=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block21.getTree());

            if ( state.backtracking==0 ) { helper.declareName(FUNCTION17_tree); }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "function"


    public static class block_return extends ParserRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "block"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:141:1: block : open= LCURLY ( stat )* close= RCURLY -> ^( BLOCK[$open] ( stat )* RCURLY[$close] ) ;
    public final CtrlParser.block_return block() throws RecognitionException {
        CtrlParser.block_return retval = new CtrlParser.block_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        CtrlParser.stat_return stat22 =null;


        NewCtrlTree open_tree=null;
        NewCtrlTree close_tree=null;
        RewriteRuleTokenStream stream_LCURLY=new RewriteRuleTokenStream(adaptor,"token LCURLY");
        RewriteRuleTokenStream stream_RCURLY=new RewriteRuleTokenStream(adaptor,"token RCURLY");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:142:3: (open= LCURLY ( stat )* close= RCURLY -> ^( BLOCK[$open] ( stat )* RCURLY[$close] ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:144:5: open= LCURLY ( stat )* close= RCURLY
            {
            open=(Token)match(input,LCURLY,FOLLOW_LCURLY_in_block541); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LCURLY.add(open);


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:144:17: ( stat )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==ALAP||LA5_0==ANY||LA5_0==BOOL||LA5_0==CHOICE||LA5_0==DO||(LA5_0 >= ID && LA5_0 <= IF)||LA5_0==INT||(LA5_0 >= LCURLY && LA5_0 <= LPAR)||LA5_0==NODE||LA5_0==OTHER||LA5_0==REAL||LA5_0==SHARP||LA5_0==STRING||(LA5_0 >= TRY && LA5_0 <= UNTIL)||LA5_0==WHILE) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:144:17: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_block543);
            	    stat22=stat();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_stat.add(stat22.getTree());

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            close=(Token)match(input,RCURLY,FOLLOW_RCURLY_in_block548); if (state.failed) return retval; 
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

            root_0 = (NewCtrlTree)adaptor.nil();
            // 145:5: -> ^( BLOCK[$open] ( stat )* RCURLY[$close] )
            {
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:145:8: ^( BLOCK[$open] ( stat )* RCURLY[$close] )
                {
                NewCtrlTree root_1 = (NewCtrlTree)adaptor.nil();
                root_1 = (NewCtrlTree)adaptor.becomeRoot(
                (NewCtrlTree)adaptor.create(BLOCK, open)
                , root_1);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:145:23: ( stat )*
                while ( stream_stat.hasNext() ) {
                    adaptor.addChild(root_1, stream_stat.nextTree());

                }
                stream_stat.reset();

                adaptor.addChild(root_1, 
                (NewCtrlTree)adaptor.create(RCURLY, close)
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "block"


    public static class stat_return extends ParserRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "stat"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:148:1: stat : ( block | ALAP ^ stat | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI ^| var_decl SEMI ^);
    public final CtrlParser.stat_return stat() throws RecognitionException {
        CtrlParser.stat_return retval = new CtrlParser.stat_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        Token ALAP24=null;
        Token WHILE26=null;
        Token LPAR27=null;
        Token RPAR29=null;
        Token UNTIL31=null;
        Token LPAR32=null;
        Token RPAR34=null;
        Token DO36=null;
        Token WHILE38=null;
        Token LPAR39=null;
        Token RPAR41=null;
        Token UNTIL42=null;
        Token LPAR43=null;
        Token RPAR45=null;
        Token IF46=null;
        Token LPAR47=null;
        Token RPAR49=null;
        Token ELSE51=null;
        Token TRY53=null;
        Token ELSE55=null;
        Token CHOICE57=null;
        Token OR59=null;
        Token SEMI62=null;
        Token SEMI64=null;
        CtrlParser.block_return block23 =null;

        CtrlParser.stat_return stat25 =null;

        CtrlParser.cond_return cond28 =null;

        CtrlParser.stat_return stat30 =null;

        CtrlParser.cond_return cond33 =null;

        CtrlParser.stat_return stat35 =null;

        CtrlParser.stat_return stat37 =null;

        CtrlParser.cond_return cond40 =null;

        CtrlParser.cond_return cond44 =null;

        CtrlParser.cond_return cond48 =null;

        CtrlParser.stat_return stat50 =null;

        CtrlParser.stat_return stat52 =null;

        CtrlParser.stat_return stat54 =null;

        CtrlParser.stat_return stat56 =null;

        CtrlParser.stat_return stat58 =null;

        CtrlParser.stat_return stat60 =null;

        CtrlParser.expr_return expr61 =null;

        CtrlParser.var_decl_return var_decl63 =null;


        NewCtrlTree ALAP24_tree=null;
        NewCtrlTree WHILE26_tree=null;
        NewCtrlTree LPAR27_tree=null;
        NewCtrlTree RPAR29_tree=null;
        NewCtrlTree UNTIL31_tree=null;
        NewCtrlTree LPAR32_tree=null;
        NewCtrlTree RPAR34_tree=null;
        NewCtrlTree DO36_tree=null;
        NewCtrlTree WHILE38_tree=null;
        NewCtrlTree LPAR39_tree=null;
        NewCtrlTree RPAR41_tree=null;
        NewCtrlTree UNTIL42_tree=null;
        NewCtrlTree LPAR43_tree=null;
        NewCtrlTree RPAR45_tree=null;
        NewCtrlTree IF46_tree=null;
        NewCtrlTree LPAR47_tree=null;
        NewCtrlTree RPAR49_tree=null;
        NewCtrlTree ELSE51_tree=null;
        NewCtrlTree TRY53_tree=null;
        NewCtrlTree ELSE55_tree=null;
        NewCtrlTree CHOICE57_tree=null;
        NewCtrlTree OR59_tree=null;
        NewCtrlTree SEMI62_tree=null;
        NewCtrlTree SEMI64_tree=null;
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_WHILE=new RewriteRuleTokenStream(adaptor,"token WHILE");
        RewriteRuleTokenStream stream_UNTIL=new RewriteRuleTokenStream(adaptor,"token UNTIL");
        RewriteRuleSubtreeStream stream_cond=new RewriteRuleSubtreeStream(adaptor,"rule cond");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:149:2: ( block | ALAP ^ stat | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI ^| var_decl SEMI ^)
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:150:4: block
                    {
                    root_0 = (NewCtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_block_in_stat580);
                    block23=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block23.getTree());

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:154:4: ALAP ^ stat
                    {
                    root_0 = (NewCtrlTree)adaptor.nil();


                    ALAP24=(Token)match(input,ALAP,FOLLOW_ALAP_in_stat597); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ALAP24_tree = 
                    (NewCtrlTree)adaptor.create(ALAP24)
                    ;
                    root_0 = (NewCtrlTree)adaptor.becomeRoot(ALAP24_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat600);
                    stat25=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat25.getTree());

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:159:4: WHILE ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (NewCtrlTree)adaptor.nil();


                    WHILE26=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat621); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    WHILE26_tree = 
                    (NewCtrlTree)adaptor.create(WHILE26)
                    ;
                    root_0 = (NewCtrlTree)adaptor.becomeRoot(WHILE26_tree, root_0);
                    }

                    LPAR27=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat624); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat627);
                    cond28=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond28.getTree());

                    RPAR29=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat629); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat632);
                    stat30=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat30.getTree());

                    }
                    break;
                case 4 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:163:5: UNTIL ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (NewCtrlTree)adaptor.nil();


                    UNTIL31=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat652); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    UNTIL31_tree = 
                    (NewCtrlTree)adaptor.create(UNTIL31)
                    ;
                    root_0 = (NewCtrlTree)adaptor.becomeRoot(UNTIL31_tree, root_0);
                    }

                    LPAR32=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat655); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat658);
                    cond33=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond33.getTree());

                    RPAR34=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat660); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat663);
                    stat35=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat35.getTree());

                    }
                    break;
                case 5 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:164:4: DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                    {
                    DO36=(Token)match(input,DO,FOLLOW_DO_in_stat668); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DO.add(DO36);


                    pushFollow(FOLLOW_stat_in_stat670);
                    stat37=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_stat.add(stat37.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:165:4: ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
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
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:7: WHILE LPAR cond RPAR
                            {
                            WHILE38=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat713); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_WHILE.add(WHILE38);


                            LPAR39=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat715); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR39);


                            pushFollow(FOLLOW_cond_in_stat717);
                            cond40=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond40.getTree());

                            RPAR41=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat719); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR41);


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

                            root_0 = (NewCtrlTree)adaptor.nil();
                            // 170:28: -> ^( BLOCK stat ^( WHILE cond stat ) )
                            {
                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:31: ^( BLOCK stat ^( WHILE cond stat ) )
                                {
                                NewCtrlTree root_1 = (NewCtrlTree)adaptor.nil();
                                root_1 = (NewCtrlTree)adaptor.becomeRoot(
                                (NewCtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:44: ^( WHILE cond stat )
                                {
                                NewCtrlTree root_2 = (NewCtrlTree)adaptor.nil();
                                root_2 = (NewCtrlTree)adaptor.becomeRoot(
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
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:177:5: UNTIL LPAR cond RPAR
                            {
                            UNTIL42=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat782); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_UNTIL.add(UNTIL42);


                            LPAR43=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat784); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR43);


                            pushFollow(FOLLOW_cond_in_stat786);
                            cond44=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond44.getTree());

                            RPAR45=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat788); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR45);


                            // AST REWRITE
                            // elements: stat, cond, UNTIL, stat
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {

                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (NewCtrlTree)adaptor.nil();
                            // 177:26: -> ^( BLOCK stat ^( UNTIL cond stat ) )
                            {
                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:177:29: ^( BLOCK stat ^( UNTIL cond stat ) )
                                {
                                NewCtrlTree root_1 = (NewCtrlTree)adaptor.nil();
                                root_1 = (NewCtrlTree)adaptor.becomeRoot(
                                (NewCtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:177:42: ^( UNTIL cond stat )
                                {
                                NewCtrlTree root_2 = (NewCtrlTree)adaptor.nil();
                                root_2 = (NewCtrlTree)adaptor.becomeRoot(
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:183:5: IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (NewCtrlTree)adaptor.nil();


                    IF46=(Token)match(input,IF,FOLLOW_IF_in_stat835); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IF46_tree = 
                    (NewCtrlTree)adaptor.create(IF46)
                    ;
                    root_0 = (NewCtrlTree)adaptor.becomeRoot(IF46_tree, root_0);
                    }

                    LPAR47=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat838); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat841);
                    cond48=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond48.getTree());

                    RPAR49=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat843); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat846);
                    stat50=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat50.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:183:31: ( ( ELSE )=> ELSE ! stat )?
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
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:183:33: ( ELSE )=> ELSE ! stat
                            {
                            ELSE51=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat856); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat859);
                            stat52=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat52.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 7 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:187:5: TRY ^ stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (NewCtrlTree)adaptor.nil();


                    TRY53=(Token)match(input,TRY,FOLLOW_TRY_in_stat883); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRY53_tree = 
                    (NewCtrlTree)adaptor.create(TRY53)
                    ;
                    root_0 = (NewCtrlTree)adaptor.becomeRoot(TRY53_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat886);
                    stat54=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat54.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:187:15: ( ( ELSE )=> ELSE ! stat )?
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
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:187:17: ( ELSE )=> ELSE ! stat
                            {
                            ELSE55=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat896); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat899);
                            stat56=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat56.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:190:5: CHOICE ^ stat ( ( OR )=> OR ! stat )+
                    {
                    root_0 = (NewCtrlTree)adaptor.nil();


                    CHOICE57=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_stat918); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHOICE57_tree = 
                    (NewCtrlTree)adaptor.create(CHOICE57)
                    ;
                    root_0 = (NewCtrlTree)adaptor.becomeRoot(CHOICE57_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat921);
                    stat58=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat58.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:190:18: ( ( OR )=> OR ! stat )+
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
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:190:20: ( OR )=> OR ! stat
                    	    {
                    	    OR59=(Token)match(input,OR,FOLLOW_OR_in_stat931); if (state.failed) return retval;

                    	    pushFollow(FOLLOW_stat_in_stat934);
                    	    stat60=stat();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat60.getTree());

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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:193:4: expr SEMI ^
                    {
                    root_0 = (NewCtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_expr_in_stat949);
                    expr61=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr61.getTree());

                    SEMI62=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat951); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SEMI62_tree = 
                    (NewCtrlTree)adaptor.create(SEMI62)
                    ;
                    root_0 = (NewCtrlTree)adaptor.becomeRoot(SEMI62_tree, root_0);
                    }

                    }
                    break;
                case 10 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:196:4: var_decl SEMI ^
                    {
                    root_0 = (NewCtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_var_decl_in_stat966);
                    var_decl63=var_decl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, var_decl63.getTree());

                    SEMI64=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat968); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SEMI64_tree = 
                    (NewCtrlTree)adaptor.create(SEMI64)
                    ;
                    root_0 = (NewCtrlTree)adaptor.becomeRoot(SEMI64_tree, root_0);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "stat"


    public static class cond_return extends ParserRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "cond"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:200:1: cond : cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) ;
    public final CtrlParser.cond_return cond() throws RecognitionException {
        CtrlParser.cond_return retval = new CtrlParser.cond_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        Token BAR66=null;
        CtrlParser.cond_atom_return cond_atom65 =null;

        CtrlParser.cond_atom_return cond_atom67 =null;


        NewCtrlTree BAR66_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_cond_atom=new RewriteRuleSubtreeStream(adaptor,"rule cond_atom");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:201:2: ( cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:203:4: cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            {
            pushFollow(FOLLOW_cond_atom_in_cond993);
            cond_atom65=cond_atom();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom65.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:204:4: ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:204:6: ( BAR cond_atom )+
                    {
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:204:6: ( BAR cond_atom )+
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
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:204:7: BAR cond_atom
                    	    {
                    	    BAR66=(Token)match(input,BAR,FOLLOW_BAR_in_cond1002); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR66);


                    	    pushFollow(FOLLOW_cond_atom_in_cond1004);
                    	    cond_atom67=cond_atom();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom67.getTree());

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

                    root_0 = (NewCtrlTree)adaptor.nil();
                    // 204:23: -> ^( CHOICE cond_atom ( cond_atom )+ )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:204:26: ^( CHOICE cond_atom ( cond_atom )+ )
                        {
                        NewCtrlTree root_1 = (NewCtrlTree)adaptor.nil();
                        root_1 = (NewCtrlTree)adaptor.becomeRoot(
                        (NewCtrlTree)adaptor.create(CHOICE, "CHOICE")
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:205:6: 
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

                    root_0 = (NewCtrlTree)adaptor.nil();
                    // 205:6: -> cond_atom
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

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "cond"


    public static class cond_atom_return extends ParserRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "cond_atom"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:209:1: cond_atom : ( TRUE | call );
    public final CtrlParser.cond_atom_return cond_atom() throws RecognitionException {
        CtrlParser.cond_atom_return retval = new CtrlParser.cond_atom_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        Token TRUE68=null;
        CtrlParser.call_return call69 =null;


        NewCtrlTree TRUE68_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:210:2: ( TRUE | call )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:212:4: TRUE
                    {
                    root_0 = (NewCtrlTree)adaptor.nil();


                    TRUE68=(Token)match(input,TRUE,FOLLOW_TRUE_in_cond_atom1050); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRUE68_tree = 
                    (NewCtrlTree)adaptor.create(TRUE68)
                    ;
                    adaptor.addChild(root_0, TRUE68_tree);
                    }

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:216:5: call
                    {
                    root_0 = (NewCtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_cond_atom1071);
                    call69=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call69.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "cond_atom"


    public static class expr_return extends ParserRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expr"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:219:1: expr : expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) ;
    public final CtrlParser.expr_return expr() throws RecognitionException {
        CtrlParser.expr_return retval = new CtrlParser.expr_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        Token BAR71=null;
        CtrlParser.expr2_return expr270 =null;

        CtrlParser.expr2_return expr272 =null;


        NewCtrlTree BAR71_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_expr2=new RewriteRuleSubtreeStream(adaptor,"rule expr2");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:220:2: ( expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:224:4: expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            {
            pushFollow(FOLLOW_expr2_in_expr1101);
            expr270=expr2();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expr2.add(expr270.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:225:4: ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:225:6: ( BAR expr2 )+
                    {
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:225:6: ( BAR expr2 )+
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
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:225:7: BAR expr2
                    	    {
                    	    BAR71=(Token)match(input,BAR,FOLLOW_BAR_in_expr1109); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR71);


                    	    pushFollow(FOLLOW_expr2_in_expr1111);
                    	    expr272=expr2();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expr2.add(expr272.getTree());

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

                    root_0 = (NewCtrlTree)adaptor.nil();
                    // 225:19: -> ^( CHOICE expr2 ( expr2 )+ )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:225:22: ^( CHOICE expr2 ( expr2 )+ )
                        {
                        NewCtrlTree root_1 = (NewCtrlTree)adaptor.nil();
                        root_1 = (NewCtrlTree)adaptor.becomeRoot(
                        (NewCtrlTree)adaptor.create(CHOICE, "CHOICE")
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:226:6: 
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

                    root_0 = (NewCtrlTree)adaptor.nil();
                    // 226:6: -> expr2
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

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "expr"


    public static class expr2_return extends ParserRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expr2"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:230:1: expr2 : (e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR $e) RCURLY[$plus] ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e) |op= SHARP expr_atom -> ^( ALAP[$op] expr_atom ) );
    public final CtrlParser.expr2_return expr2() throws RecognitionException {
        CtrlParser.expr2_return retval = new CtrlParser.expr2_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        Token plus=null;
        Token ast=null;
        Token op=null;
        CtrlParser.expr_atom_return e =null;

        CtrlParser.expr_atom_return expr_atom73 =null;


        NewCtrlTree plus_tree=null;
        NewCtrlTree ast_tree=null;
        NewCtrlTree op_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_SHARP=new RewriteRuleTokenStream(adaptor,"token SHARP");
        RewriteRuleTokenStream stream_ASTERISK=new RewriteRuleTokenStream(adaptor,"token ASTERISK");
        RewriteRuleSubtreeStream stream_expr_atom=new RewriteRuleSubtreeStream(adaptor,"rule expr_atom");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:231:3: (e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR $e) RCURLY[$plus] ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e) |op= SHARP expr_atom -> ^( ALAP[$op] expr_atom ) )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:239:5: e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR $e) RCURLY[$plus] ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e)
                    {
                    pushFollow(FOLLOW_expr_atom_in_expr21192);
                    e=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(e.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:240:5: (plus= PLUS -> ^( BLOCK $e ^( STAR $e) RCURLY[$plus] ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e)
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
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:240:7: plus= PLUS
                            {
                            plus=(Token)match(input,PLUS,FOLLOW_PLUS_in_expr21202); if (state.failed) return retval; 
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

                            root_0 = (NewCtrlTree)adaptor.nil();
                            // 240:17: -> ^( BLOCK $e ^( STAR $e) RCURLY[$plus] )
                            {
                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:240:20: ^( BLOCK $e ^( STAR $e) RCURLY[$plus] )
                                {
                                NewCtrlTree root_1 = (NewCtrlTree)adaptor.nil();
                                root_1 = (NewCtrlTree)adaptor.becomeRoot(
                                (NewCtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_e.nextTree());

                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:240:31: ^( STAR $e)
                                {
                                NewCtrlTree root_2 = (NewCtrlTree)adaptor.nil();
                                root_2 = (NewCtrlTree)adaptor.becomeRoot(
                                (NewCtrlTree)adaptor.create(STAR, "STAR")
                                , root_2);

                                adaptor.addChild(root_2, stream_e.nextTree());

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_1, 
                                (NewCtrlTree)adaptor.create(RCURLY, plus)
                                );

                                adaptor.addChild(root_0, root_1);
                                }

                            }


                            retval.tree = root_0;
                            }

                            }
                            break;
                        case 2 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:241:7: ast= ASTERISK
                            {
                            ast=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_expr21231); if (state.failed) return retval; 
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

                            root_0 = (NewCtrlTree)adaptor.nil();
                            // 241:20: -> ^( STAR[$ast] $e)
                            {
                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:241:23: ^( STAR[$ast] $e)
                                {
                                NewCtrlTree root_1 = (NewCtrlTree)adaptor.nil();
                                root_1 = (NewCtrlTree)adaptor.becomeRoot(
                                (NewCtrlTree)adaptor.create(STAR, ast)
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
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:242:7: 
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

                            root_0 = (NewCtrlTree)adaptor.nil();
                            // 242:7: -> $e
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:248:5: op= SHARP expr_atom
                    {
                    op=(Token)match(input,SHARP,FOLLOW_SHARP_in_expr21286); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SHARP.add(op);


                    pushFollow(FOLLOW_expr_atom_in_expr21288);
                    expr_atom73=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(expr_atom73.getTree());

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

                    root_0 = (NewCtrlTree)adaptor.nil();
                    // 248:24: -> ^( ALAP[$op] expr_atom )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:248:27: ^( ALAP[$op] expr_atom )
                        {
                        NewCtrlTree root_1 = (NewCtrlTree)adaptor.nil();
                        root_1 = (NewCtrlTree)adaptor.becomeRoot(
                        (NewCtrlTree)adaptor.create(ALAP, op)
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

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "expr2"


    public static class expr_atom_return extends ParserRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expr_atom"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:251:1: expr_atom : ( ANY | OTHER |open= LPAR expr close= RPAR -> ^( BLOCK[$open] expr RCURLY[$close] ) | call );
    public final CtrlParser.expr_atom_return expr_atom() throws RecognitionException {
        CtrlParser.expr_atom_return retval = new CtrlParser.expr_atom_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        Token ANY74=null;
        Token OTHER75=null;
        CtrlParser.expr_return expr76 =null;

        CtrlParser.call_return call77 =null;


        NewCtrlTree open_tree=null;
        NewCtrlTree close_tree=null;
        NewCtrlTree ANY74_tree=null;
        NewCtrlTree OTHER75_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:252:2: ( ANY | OTHER |open= LPAR expr close= RPAR -> ^( BLOCK[$open] expr RCURLY[$close] ) | call )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:254:4: ANY
                    {
                    root_0 = (NewCtrlTree)adaptor.nil();


                    ANY74=(Token)match(input,ANY,FOLLOW_ANY_in_expr_atom1317); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ANY74_tree = 
                    (NewCtrlTree)adaptor.create(ANY74)
                    ;
                    adaptor.addChild(root_0, ANY74_tree);
                    }

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:258:4: OTHER
                    {
                    root_0 = (NewCtrlTree)adaptor.nil();


                    OTHER75=(Token)match(input,OTHER,FOLLOW_OTHER_in_expr_atom1334); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OTHER75_tree = 
                    (NewCtrlTree)adaptor.create(OTHER75)
                    ;
                    adaptor.addChild(root_0, OTHER75_tree);
                    }

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:261:4: open= LPAR expr close= RPAR
                    {
                    open=(Token)match(input,LPAR,FOLLOW_LPAR_in_expr_atom1349); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAR.add(open);


                    pushFollow(FOLLOW_expr_in_expr_atom1351);
                    expr76=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr.add(expr76.getTree());

                    close=(Token)match(input,RPAR,FOLLOW_RPAR_in_expr_atom1355); if (state.failed) return retval; 
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

                    root_0 = (NewCtrlTree)adaptor.nil();
                    // 262:4: -> ^( BLOCK[$open] expr RCURLY[$close] )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:262:7: ^( BLOCK[$open] expr RCURLY[$close] )
                        {
                        NewCtrlTree root_1 = (NewCtrlTree)adaptor.nil();
                        root_1 = (NewCtrlTree)adaptor.becomeRoot(
                        (NewCtrlTree)adaptor.create(BLOCK, open)
                        , root_1);

                        adaptor.addChild(root_1, stream_expr.nextTree());

                        adaptor.addChild(root_1, 
                        (NewCtrlTree)adaptor.create(RCURLY, close)
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 4 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:265:4: call
                    {
                    root_0 = (NewCtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_expr_atom1383);
                    call77=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call77.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "expr_atom"


    public static class call_return extends ParserRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "call"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:269:1: call : rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) ;
    public final CtrlParser.call_return call() throws RecognitionException {
        CtrlParser.call_return retval = new CtrlParser.call_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        CtrlParser.rule_name_return rule_name78 =null;

        CtrlParser.arg_list_return arg_list79 =null;


        RewriteRuleSubtreeStream stream_arg_list=new RewriteRuleSubtreeStream(adaptor,"rule arg_list");
        RewriteRuleSubtreeStream stream_rule_name=new RewriteRuleSubtreeStream(adaptor,"rule rule_name");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:270:2: ( rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:274:4: rule_name ( arg_list )?
            {
            pushFollow(FOLLOW_rule_name_in_call1413);
            rule_name78=rule_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_name.add(rule_name78.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:274:14: ( arg_list )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==LPAR) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:274:14: arg_list
                    {
                    pushFollow(FOLLOW_arg_list_in_call1415);
                    arg_list79=arg_list();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg_list.add(arg_list79.getTree());

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

            root_0 = (NewCtrlTree)adaptor.nil();
            // 275:4: -> ^( CALL[$rule_name.start] rule_name ( arg_list )? )
            {
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:275:7: ^( CALL[$rule_name.start] rule_name ( arg_list )? )
                {
                NewCtrlTree root_1 = (NewCtrlTree)adaptor.nil();
                root_1 = (NewCtrlTree)adaptor.becomeRoot(
                (NewCtrlTree)adaptor.create(CALL, (rule_name78!=null?((Token)rule_name78.start):null))
                , root_1);

                adaptor.addChild(root_1, stream_rule_name.nextTree());

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:275:42: ( arg_list )?
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

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "call"


    public static class arg_list_return extends ParserRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg_list"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:281:1: arg_list : open= LPAR ( arg ( COMMA arg )* )? close= RPAR -> ^( ARGS[$open] ( arg )* RPAR[$close] ) ;
    public final CtrlParser.arg_list_return arg_list() throws RecognitionException {
        CtrlParser.arg_list_return retval = new CtrlParser.arg_list_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        Token COMMA81=null;
        CtrlParser.arg_return arg80 =null;

        CtrlParser.arg_return arg82 =null;


        NewCtrlTree open_tree=null;
        NewCtrlTree close_tree=null;
        NewCtrlTree COMMA81_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_arg=new RewriteRuleSubtreeStream(adaptor,"rule arg");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:282:3: (open= LPAR ( arg ( COMMA arg )* )? close= RPAR -> ^( ARGS[$open] ( arg )* RPAR[$close] ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:284:5: open= LPAR ( arg ( COMMA arg )* )? close= RPAR
            {
            open=(Token)match(input,LPAR,FOLLOW_LPAR_in_arg_list1457); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAR.add(open);


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:284:15: ( arg ( COMMA arg )* )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==DONT_CARE||LA21_0==FALSE||LA21_0==ID||LA21_0==INT_LIT||LA21_0==OUT||LA21_0==REAL_LIT||(LA21_0 >= STRING_LIT && LA21_0 <= TRUE)) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:284:16: arg ( COMMA arg )*
                    {
                    pushFollow(FOLLOW_arg_in_arg_list1460);
                    arg80=arg();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg.add(arg80.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:284:20: ( COMMA arg )*
                    loop20:
                    do {
                        int alt20=2;
                        int LA20_0 = input.LA(1);

                        if ( (LA20_0==COMMA) ) {
                            alt20=1;
                        }


                        switch (alt20) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:284:21: COMMA arg
                    	    {
                    	    COMMA81=(Token)match(input,COMMA,FOLLOW_COMMA_in_arg_list1463); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA81);


                    	    pushFollow(FOLLOW_arg_in_arg_list1465);
                    	    arg82=arg();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_arg.add(arg82.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop20;
                        }
                    } while (true);


                    }
                    break;

            }


            close=(Token)match(input,RPAR,FOLLOW_RPAR_in_arg_list1473); if (state.failed) return retval; 
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

            root_0 = (NewCtrlTree)adaptor.nil();
            // 285:5: -> ^( ARGS[$open] ( arg )* RPAR[$close] )
            {
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:285:8: ^( ARGS[$open] ( arg )* RPAR[$close] )
                {
                NewCtrlTree root_1 = (NewCtrlTree)adaptor.nil();
                root_1 = (NewCtrlTree)adaptor.becomeRoot(
                (NewCtrlTree)adaptor.create(ARGS, open)
                , root_1);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:285:22: ( arg )*
                while ( stream_arg.hasNext() ) {
                    adaptor.addChild(root_1, stream_arg.nextTree());

                }
                stream_arg.reset();

                adaptor.addChild(root_1, 
                (NewCtrlTree)adaptor.create(RPAR, close)
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "arg_list"


    public static class arg_return extends ParserRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:291:1: arg : ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) );
    public final CtrlParser.arg_return arg() throws RecognitionException {
        CtrlParser.arg_return retval = new CtrlParser.arg_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        Token OUT83=null;
        Token ID84=null;
        Token ID85=null;
        Token DONT_CARE86=null;
        CtrlParser.literal_return literal87 =null;


        NewCtrlTree OUT83_tree=null;
        NewCtrlTree ID84_tree=null;
        NewCtrlTree ID85_tree=null;
        NewCtrlTree DONT_CARE86_tree=null;
        RewriteRuleTokenStream stream_DONT_CARE=new RewriteRuleTokenStream(adaptor,"token DONT_CARE");
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:292:3: ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:295:5: OUT ID
                    {
                    OUT83=(Token)match(input,OUT,FOLLOW_OUT_in_arg1520); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT83);


                    ID84=(Token)match(input,ID,FOLLOW_ID_in_arg1522); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID84);


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

                    root_0 = (NewCtrlTree)adaptor.nil();
                    // 295:12: -> ^( ARG OUT ID )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:295:15: ^( ARG OUT ID )
                        {
                        NewCtrlTree root_1 = (NewCtrlTree)adaptor.nil();
                        root_1 = (NewCtrlTree)adaptor.becomeRoot(
                        (NewCtrlTree)adaptor.create(ARG, "ARG")
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:299:5: ID
                    {
                    ID85=(Token)match(input,ID,FOLLOW_ID_in_arg1553); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID85);


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

                    root_0 = (NewCtrlTree)adaptor.nil();
                    // 299:8: -> ^( ARG ID )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:299:11: ^( ARG ID )
                        {
                        NewCtrlTree root_1 = (NewCtrlTree)adaptor.nil();
                        root_1 = (NewCtrlTree)adaptor.becomeRoot(
                        (NewCtrlTree)adaptor.create(ARG, "ARG")
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:303:5: DONT_CARE
                    {
                    DONT_CARE86=(Token)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg1582); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DONT_CARE.add(DONT_CARE86);


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

                    root_0 = (NewCtrlTree)adaptor.nil();
                    // 303:15: -> ^( ARG DONT_CARE )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:303:18: ^( ARG DONT_CARE )
                        {
                        NewCtrlTree root_1 = (NewCtrlTree)adaptor.nil();
                        root_1 = (NewCtrlTree)adaptor.becomeRoot(
                        (NewCtrlTree)adaptor.create(ARG, "ARG")
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:304:5: literal
                    {
                    pushFollow(FOLLOW_literal_in_arg1599);
                    literal87=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_literal.add(literal87.getTree());

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

                    root_0 = (NewCtrlTree)adaptor.nil();
                    // 304:13: -> ^( ARG literal )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:304:16: ^( ARG literal )
                        {
                        NewCtrlTree root_1 = (NewCtrlTree)adaptor.nil();
                        root_1 = (NewCtrlTree)adaptor.becomeRoot(
                        (NewCtrlTree)adaptor.create(ARG, "ARG")
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

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "arg"


    public static class literal_return extends ParserRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "literal"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:307:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlParser.literal_return literal() throws RecognitionException {
        CtrlParser.literal_return retval = new CtrlParser.literal_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        Token set88=null;

        NewCtrlTree set88_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:308:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
            {
            root_0 = (NewCtrlTree)adaptor.nil();


            set88=(Token)input.LT(1);

            if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (NewCtrlTree)adaptor.create(set88)
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

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "literal"


    public static class rule_name_return extends ParserRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "rule_name"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:329:1: rule_name : qual_name ->;
    public final CtrlParser.rule_name_return rule_name() throws RecognitionException {
        CtrlParser.rule_name_return retval = new CtrlParser.rule_name_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        CtrlParser.qual_name_return qual_name89 =null;


        RewriteRuleSubtreeStream stream_qual_name=new RewriteRuleSubtreeStream(adaptor,"rule qual_name");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:330:3: ( qual_name ->)
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:330:5: qual_name
            {
            pushFollow(FOLLOW_qual_name_in_rule_name1709);
            qual_name89=qual_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qual_name.add(qual_name89.getTree());

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

            root_0 = (NewCtrlTree)adaptor.nil();
            // 331:5: ->
            {
                adaptor.addChild(root_0,  helper.lookup((qual_name89!=null?((NewCtrlTree)qual_name89.tree):null)) );

            }


            retval.tree = root_0;
            }

            }

            retval.stop = input.LT(-1);


            if ( state.backtracking==0 ) {

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "rule_name"


    public static class var_decl_return extends ParserRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_decl"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:335:1: var_decl : var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) ;
    public final CtrlParser.var_decl_return var_decl() throws RecognitionException {
        CtrlParser.var_decl_return retval = new CtrlParser.var_decl_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        Token ID91=null;
        Token COMMA92=null;
        Token ID93=null;
        CtrlParser.var_type_return var_type90 =null;


        NewCtrlTree ID91_tree=null;
        NewCtrlTree COMMA92_tree=null;
        NewCtrlTree ID93_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:336:2: ( var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:338:4: var_type ID ( COMMA ID )*
            {
            pushFollow(FOLLOW_var_type_in_var_decl1739);
            var_type90=var_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_var_type.add(var_type90.getTree());

            ID91=(Token)match(input,ID,FOLLOW_ID_in_var_decl1741); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID91);


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:338:16: ( COMMA ID )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==COMMA) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:338:17: COMMA ID
            	    {
            	    COMMA92=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_decl1744); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA92);


            	    ID93=(Token)match(input,ID,FOLLOW_ID_in_var_decl1746); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ID93);


            	    }
            	    break;

            	default :
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
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (NewCtrlTree)adaptor.nil();
            // 338:28: -> ^( VAR var_type ( ID )+ )
            {
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:338:31: ^( VAR var_type ( ID )+ )
                {
                NewCtrlTree root_1 = (NewCtrlTree)adaptor.nil();
                root_1 = (NewCtrlTree)adaptor.becomeRoot(
                (NewCtrlTree)adaptor.create(VAR, "VAR")
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

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "var_decl"


    public static class var_type_return extends ParserRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_type"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:342:1: var_type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlParser.var_type_return var_type() throws RecognitionException {
        CtrlParser.var_type_return retval = new CtrlParser.var_type_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        Token set94=null;

        NewCtrlTree set94_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:343:2: ( NODE | BOOL | STRING | INT | REAL )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
            {
            root_0 = (NewCtrlTree)adaptor.nil();


            set94=(Token)input.LT(1);

            if ( input.LA(1)==BOOL||input.LA(1)==INT||input.LA(1)==NODE||input.LA(1)==REAL||input.LA(1)==STRING ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (NewCtrlTree)adaptor.create(set94)
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

            retval.tree = (NewCtrlTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (NewCtrlTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "var_type"

    // $ANTLR start synpred1_Ctrl
    public final void synpred1_Ctrl_fragment() throws RecognitionException {
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:183:33: ( ELSE )
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:183:34: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred1_Ctrl851); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred1_Ctrl

    // $ANTLR start synpred2_Ctrl
    public final void synpred2_Ctrl_fragment() throws RecognitionException {
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:187:17: ( ELSE )
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:187:18: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred2_Ctrl891); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred2_Ctrl

    // $ANTLR start synpred3_Ctrl
    public final void synpred3_Ctrl_fragment() throws RecognitionException {
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:190:20: ( OR )
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:190:21: OR
        {
        match(input,OR,FOLLOW_OR_in_synpred3_Ctrl926); if (state.failed) return ;

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
    public static final BitSet FOLLOW_PACKAGE_in_package_decl298 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_qual_name_in_package_decl300 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_SEMI_in_package_decl304 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_decl371 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_qual_name_in_import_decl374 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_SEMI_in_import_decl376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_qual_name399 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_DOT_in_qual_name402 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_qual_name406 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_RECIPE_in_recipe441 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_recipe444 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_recipe446 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RPAR_in_recipe449 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_block_in_recipe466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function497 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_function500 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_function502 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RPAR_in_function505 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_block_in_function508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LCURLY_in_block541 = new BitSet(new long[]{0x6483022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_block543 = new BitSet(new long[]{0x6483022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_RCURLY_in_block548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_stat580 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat597 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat600 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_stat621 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat624 = new BitSet(new long[]{0x1000000008000000L});
    public static final BitSet FOLLOW_cond_in_stat627 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat629 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat652 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat655 = new BitSet(new long[]{0x1000000008000000L});
    public static final BitSet FOLLOW_cond_in_stat658 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat660 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat663 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_stat668 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat670 = new BitSet(new long[]{0x4000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_WHILE_in_stat713 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat715 = new BitSet(new long[]{0x1000000008000000L});
    public static final BitSet FOLLOW_cond_in_stat717 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat782 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat784 = new BitSet(new long[]{0x1000000008000000L});
    public static final BitSet FOLLOW_cond_in_stat786 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat788 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_stat835 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_LPAR_in_stat838 = new BitSet(new long[]{0x1000000008000000L});
    public static final BitSet FOLLOW_cond_in_stat841 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat843 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat846 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_ELSE_in_stat856 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat859 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_stat883 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat886 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_ELSE_in_stat896 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat899 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_stat918 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat921 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_OR_in_stat931 = new BitSet(new long[]{0x6482022658029050L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat934 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_expr_in_stat949 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat966 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat968 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cond_atom_in_cond993 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_BAR_in_cond1002 = new BitSet(new long[]{0x1000000008000000L});
    public static final BitSet FOLLOW_cond_atom_in_cond1004 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_TRUE_in_cond_atom1050 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_cond_atom1071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr2_in_expr1101 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_BAR_in_expr1109 = new BitSet(new long[]{0x0080020408000040L});
    public static final BitSet FOLLOW_expr2_in_expr1111 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_expr_atom_in_expr21192 = new BitSet(new long[]{0x0000100000000202L});
    public static final BitSet FOLLOW_PLUS_in_expr21202 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERISK_in_expr21231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expr21286 = new BitSet(new long[]{0x0000020408000040L});
    public static final BitSet FOLLOW_expr_atom_in_expr21288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expr_atom1317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expr_atom1334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_atom1349 = new BitSet(new long[]{0x0080020408000040L});
    public static final BitSet FOLLOW_expr_in_expr_atom1351 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RPAR_in_expr_atom1355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expr_atom1383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_name_in_call1413 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_arg_list_in_call1415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_arg_list1457 = new BitSet(new long[]{0x1824040089040000L});
    public static final BitSet FOLLOW_arg_in_arg_list1460 = new BitSet(new long[]{0x0020000000010000L});
    public static final BitSet FOLLOW_COMMA_in_arg_list1463 = new BitSet(new long[]{0x1804040089040000L});
    public static final BitSet FOLLOW_arg_in_arg_list1465 = new BitSet(new long[]{0x0020000000010000L});
    public static final BitSet FOLLOW_RPAR_in_arg_list1473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_arg1520 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_arg1522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_arg1553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg1582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_arg1599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qual_name_in_rule_name1709 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_type_in_var_decl1739 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_var_decl1741 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_COMMA_in_var_decl1744 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_var_decl1746 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_ELSE_in_synpred1_Ctrl851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred2_Ctrl891 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_synpred3_Ctrl926 = new BitSet(new long[]{0x0000000000000002L});

}