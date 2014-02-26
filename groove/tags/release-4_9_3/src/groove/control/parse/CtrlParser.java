// $ANTLR 3.4 D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g 2014-01-22 23:01:13

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:75:1: program : package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) ) ;
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:78:3: ( package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:82:5: package_decl ( import_decl )* ( function | recipe | stat )* EOF
            {
            pushFollow(FOLLOW_package_decl_in_program166);
            package_decl1=package_decl();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_package_decl.add(package_decl1.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:83:5: ( import_decl )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==IMPORT) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:83:5: import_decl
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


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:84:5: ( function | recipe | stat )*
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
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:84:6: function
            	    {
            	    pushFollow(FOLLOW_function_in_program180);
            	    function3=function();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_function.add(function3.getTree());

            	    }
            	    break;
            	case 2 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:84:15: recipe
            	    {
            	    pushFollow(FOLLOW_recipe_in_program182);
            	    recipe4=recipe();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_recipe.add(recipe4.getTree());

            	    }
            	    break;
            	case 3 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:84:22: stat
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
            // elements: package_decl, stat, function, import_decl, recipe
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
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:86:8: ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(PROGRAM, "PROGRAM")
                , root_1);

                adaptor.addChild(root_1, stream_package_decl.nextTree());

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:88:11: ^( IMPORTS ( import_decl )* )
                {
                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                root_2 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(IMPORTS, "IMPORTS")
                , root_2);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:88:21: ( import_decl )*
                while ( stream_import_decl.hasNext() ) {
                    adaptor.addChild(root_2, stream_import_decl.nextTree());

                }
                stream_import_decl.reset();

                adaptor.addChild(root_1, root_2);
                }

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:89:11: ^( FUNCTIONS ( function )* )
                {
                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                root_2 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(FUNCTIONS, "FUNCTIONS")
                , root_2);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:89:23: ( function )*
                while ( stream_function.hasNext() ) {
                    adaptor.addChild(root_2, stream_function.nextTree());

                }
                stream_function.reset();

                adaptor.addChild(root_1, root_2);
                }

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:90:11: ^( RECIPES ( recipe )* )
                {
                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                root_2 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(RECIPES, "RECIPES")
                , root_2);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:90:21: ( recipe )*
                while ( stream_recipe.hasNext() ) {
                    adaptor.addChild(root_2, stream_recipe.nextTree());

                }
                stream_recipe.reset();

                adaptor.addChild(root_1, root_2);
                }

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:91:11: ^( BLOCK ( stat )* )
                {
                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                root_2 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                , root_2);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:91:19: ( stat )*
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:96:1: package_decl : (key= PACKAGE qual_name close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->) ;
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:97:3: ( (key= PACKAGE qual_name close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:99:5: (key= PACKAGE qual_name close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->)
            {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:99:5: (key= PACKAGE qual_name close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->)
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:99:7: key= PACKAGE qual_name close= SEMI
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
                    // elements: PACKAGE, SEMI, qual_name
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
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:101:10: ^( PACKAGE[$key] qual_name SEMI[$close] )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:102:7: 
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:107:1: import_decl : IMPORT ^ qual_name SEMI ;
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:108:3: ( IMPORT ^ qual_name SEMI )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:110:5: IMPORT ^ qual_name SEMI
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:116:1: qual_name :ids+= ID ( DOT ids+= ID )* ->;
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:117:3: (ids+= ID ( DOT ids+= ID )* ->)
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:117:5: ids+= ID ( DOT ids+= ID )*
            {
            ids=(Token)match(input,ID,FOLLOW_ID_in_qual_name426); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ids);

            if (list_ids==null) list_ids=new ArrayList();
            list_ids.add(ids);


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:117:13: ( DOT ids+= ID )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==DOT) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:117:14: DOT ids+= ID
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:124:1: recipe : RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block ;
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
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:125:3: ( RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:128:5: RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block
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

            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:128:25: ( PRIORITY ! INT_LIT )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==PRIORITY) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:128:26: PRIORITY ! INT_LIT
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:136:1: function : FUNCTION ^ ID par_list ( PRIORITY ! INT_LIT )? block ;
    public final CtrlParser.function_return function() throws RecognitionException {
        CtrlParser.function_return retval = new CtrlParser.function_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token FUNCTION18=null;
        Token ID19=null;
        Token PRIORITY21=null;
        Token INT_LIT22=null;
        CtrlParser.par_list_return par_list20 =null;

        CtrlParser.block_return block23 =null;


        CtrlTree FUNCTION18_tree=null;
        CtrlTree ID19_tree=null;
        CtrlTree PRIORITY21_tree=null;
        CtrlTree INT_LIT22_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:137:3: ( FUNCTION ^ ID par_list ( PRIORITY ! INT_LIT )? block )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:140:5: FUNCTION ^ ID par_list ( PRIORITY ! INT_LIT )? block
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

            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:140:27: ( PRIORITY ! INT_LIT )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==PRIORITY) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:140:28: PRIORITY ! INT_LIT
                    {
                    PRIORITY21=(Token)match(input,PRIORITY,FOLLOW_PRIORITY_in_function532); if (state.failed) return retval;

                    INT_LIT22=(Token)match(input,INT_LIT,FOLLOW_INT_LIT_in_function535); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT_LIT22_tree = 
                    (CtrlTree)adaptor.create(INT_LIT22)
                    ;
                    adaptor.addChild(root_0, INT_LIT22_tree);
                    }

                    }
                    break;

            }


            pushFollow(FOLLOW_block_in_function539);
            block23=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block23.getTree());

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:147:1: par_list : LPAR ( par ( COMMA par )* )? RPAR -> ^( PARS ( par )* ) ;
    public final CtrlParser.par_list_return par_list() throws RecognitionException {
        CtrlParser.par_list_return retval = new CtrlParser.par_list_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token LPAR24=null;
        Token COMMA26=null;
        Token RPAR28=null;
        CtrlParser.par_return par25 =null;

        CtrlParser.par_return par27 =null;


        CtrlTree LPAR24_tree=null;
        CtrlTree COMMA26_tree=null;
        CtrlTree RPAR28_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_par=new RewriteRuleSubtreeStream(adaptor,"rule par");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:148:3: ( LPAR ( par ( COMMA par )* )? RPAR -> ^( PARS ( par )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:150:5: LPAR ( par ( COMMA par )* )? RPAR
            {
            LPAR24=(Token)match(input,LPAR,FOLLOW_LPAR_in_par_list570); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAR.add(LPAR24);


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:150:10: ( par ( COMMA par )* )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==BOOL||LA8_0==INT||LA8_0==NODE||LA8_0==OUT||LA8_0==REAL||LA8_0==STRING) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:150:11: par ( COMMA par )*
                    {
                    pushFollow(FOLLOW_par_in_par_list573);
                    par25=par();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_par.add(par25.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:150:15: ( COMMA par )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==COMMA) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:150:16: COMMA par
                    	    {
                    	    COMMA26=(Token)match(input,COMMA,FOLLOW_COMMA_in_par_list576); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA26);


                    	    pushFollow(FOLLOW_par_in_par_list578);
                    	    par27=par();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_par.add(par27.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                    }
                    break;

            }


            RPAR28=(Token)match(input,RPAR,FOLLOW_RPAR_in_par_list584); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAR.add(RPAR28);


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
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:151:8: ^( PARS ( par )* )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(PARS, "PARS")
                , root_1);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:151:15: ( par )*
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:157:1: par : ( OUT var_type ID -> ^( PAR OUT var_type ID ) | var_type ID -> ^( PAR var_type ID ) );
    public final CtrlParser.par_return par() throws RecognitionException {
        CtrlParser.par_return retval = new CtrlParser.par_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token OUT29=null;
        Token ID31=null;
        Token ID33=null;
        CtrlParser.var_type_return var_type30 =null;

        CtrlParser.var_type_return var_type32 =null;


        CtrlTree OUT29_tree=null;
        CtrlTree ID31_tree=null;
        CtrlTree ID33_tree=null;
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:158:3: ( OUT var_type ID -> ^( PAR OUT var_type ID ) | var_type ID -> ^( PAR var_type ID ) )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==OUT) ) {
                alt9=1;
            }
            else if ( (LA9_0==BOOL||LA9_0==INT||LA9_0==NODE||LA9_0==REAL||LA9_0==STRING) ) {
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:161:5: OUT var_type ID
                    {
                    OUT29=(Token)match(input,OUT,FOLLOW_OUT_in_par629); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT29);


                    pushFollow(FOLLOW_var_type_in_par631);
                    var_type30=var_type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_var_type.add(var_type30.getTree());

                    ID31=(Token)match(input,ID,FOLLOW_ID_in_par633); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID31);


                    // AST REWRITE
                    // elements: OUT, ID, var_type
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
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:161:24: ^( PAR OUT var_type ID )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:165:5: var_type ID
                    {
                    pushFollow(FOLLOW_var_type_in_par666);
                    var_type32=var_type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_var_type.add(var_type32.getTree());

                    ID33=(Token)match(input,ID,FOLLOW_ID_in_par668); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID33);


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
                    // 165:17: -> ^( PAR var_type ID )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:165:20: ^( PAR var_type ID )
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:169:1: block : open= LCURLY ( stat )* close= RCURLY -> ^( BLOCK[$open] ( stat )* TRUE[$close] ) ;
    public final CtrlParser.block_return block() throws RecognitionException {
        CtrlParser.block_return retval = new CtrlParser.block_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        CtrlParser.stat_return stat34 =null;


        CtrlTree open_tree=null;
        CtrlTree close_tree=null;
        RewriteRuleTokenStream stream_LCURLY=new RewriteRuleTokenStream(adaptor,"token LCURLY");
        RewriteRuleTokenStream stream_RCURLY=new RewriteRuleTokenStream(adaptor,"token RCURLY");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:3: (open= LCURLY ( stat )* close= RCURLY -> ^( BLOCK[$open] ( stat )* TRUE[$close] ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:172:5: open= LCURLY ( stat )* close= RCURLY
            {
            open=(Token)match(input,LCURLY,FOLLOW_LCURLY_in_block707); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LCURLY.add(open);


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:172:17: ( stat )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==ALAP||LA10_0==ANY||LA10_0==ATOM||LA10_0==BOOL||LA10_0==CHOICE||LA10_0==DO||(LA10_0 >= ID && LA10_0 <= IF)||LA10_0==INT||(LA10_0 >= LCURLY && LA10_0 <= LPAR)||LA10_0==NODE||LA10_0==OTHER||LA10_0==REAL||LA10_0==SHARP||LA10_0==STRING||(LA10_0 >= TRY && LA10_0 <= UNTIL)||LA10_0==WHILE) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:172:17: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_block709);
            	    stat34=stat();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_stat.add(stat34.getTree());

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            close=(Token)match(input,RCURLY,FOLLOW_RCURLY_in_block714); if (state.failed) return retval; 
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
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:173:8: ^( BLOCK[$open] ( stat )* TRUE[$close] )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(BLOCK, open)
                , root_1);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:173:23: ( stat )*
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:176:1: stat : ( block | ALAP ^ stat | ATOM ^ stat | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI ^| var_decl SEMI ^);
    public final CtrlParser.stat_return stat() throws RecognitionException {
        CtrlParser.stat_return retval = new CtrlParser.stat_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token ALAP36=null;
        Token ATOM38=null;
        Token WHILE40=null;
        Token LPAR41=null;
        Token RPAR43=null;
        Token UNTIL45=null;
        Token LPAR46=null;
        Token RPAR48=null;
        Token DO50=null;
        Token WHILE52=null;
        Token LPAR53=null;
        Token RPAR55=null;
        Token UNTIL56=null;
        Token LPAR57=null;
        Token RPAR59=null;
        Token IF60=null;
        Token LPAR61=null;
        Token RPAR63=null;
        Token ELSE65=null;
        Token TRY67=null;
        Token ELSE69=null;
        Token CHOICE71=null;
        Token OR73=null;
        Token SEMI76=null;
        Token SEMI78=null;
        CtrlParser.block_return block35 =null;

        CtrlParser.stat_return stat37 =null;

        CtrlParser.stat_return stat39 =null;

        CtrlParser.cond_return cond42 =null;

        CtrlParser.stat_return stat44 =null;

        CtrlParser.cond_return cond47 =null;

        CtrlParser.stat_return stat49 =null;

        CtrlParser.stat_return stat51 =null;

        CtrlParser.cond_return cond54 =null;

        CtrlParser.cond_return cond58 =null;

        CtrlParser.cond_return cond62 =null;

        CtrlParser.stat_return stat64 =null;

        CtrlParser.stat_return stat66 =null;

        CtrlParser.stat_return stat68 =null;

        CtrlParser.stat_return stat70 =null;

        CtrlParser.stat_return stat72 =null;

        CtrlParser.stat_return stat74 =null;

        CtrlParser.expr_return expr75 =null;

        CtrlParser.var_decl_return var_decl77 =null;


        CtrlTree ALAP36_tree=null;
        CtrlTree ATOM38_tree=null;
        CtrlTree WHILE40_tree=null;
        CtrlTree LPAR41_tree=null;
        CtrlTree RPAR43_tree=null;
        CtrlTree UNTIL45_tree=null;
        CtrlTree LPAR46_tree=null;
        CtrlTree RPAR48_tree=null;
        CtrlTree DO50_tree=null;
        CtrlTree WHILE52_tree=null;
        CtrlTree LPAR53_tree=null;
        CtrlTree RPAR55_tree=null;
        CtrlTree UNTIL56_tree=null;
        CtrlTree LPAR57_tree=null;
        CtrlTree RPAR59_tree=null;
        CtrlTree IF60_tree=null;
        CtrlTree LPAR61_tree=null;
        CtrlTree RPAR63_tree=null;
        CtrlTree ELSE65_tree=null;
        CtrlTree TRY67_tree=null;
        CtrlTree ELSE69_tree=null;
        CtrlTree CHOICE71_tree=null;
        CtrlTree OR73_tree=null;
        CtrlTree SEMI76_tree=null;
        CtrlTree SEMI78_tree=null;
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_WHILE=new RewriteRuleTokenStream(adaptor,"token WHILE");
        RewriteRuleTokenStream stream_UNTIL=new RewriteRuleTokenStream(adaptor,"token UNTIL");
        RewriteRuleSubtreeStream stream_cond=new RewriteRuleSubtreeStream(adaptor,"rule cond");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:177:2: ( block | ALAP ^ stat | ATOM ^ stat | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI ^| var_decl SEMI ^)
            int alt15=11;
            switch ( input.LA(1) ) {
            case LCURLY:
                {
                alt15=1;
                }
                break;
            case ALAP:
                {
                alt15=2;
                }
                break;
            case ATOM:
                {
                alt15=3;
                }
                break;
            case WHILE:
                {
                alt15=4;
                }
                break;
            case UNTIL:
                {
                alt15=5;
                }
                break;
            case DO:
                {
                alt15=6;
                }
                break;
            case IF:
                {
                alt15=7;
                }
                break;
            case TRY:
                {
                alt15=8;
                }
                break;
            case CHOICE:
                {
                alt15=9;
                }
                break;
            case ANY:
            case ID:
            case LPAR:
            case OTHER:
            case SHARP:
                {
                alt15=10;
                }
                break;
            case BOOL:
            case INT:
            case NODE:
            case REAL:
            case STRING:
                {
                alt15=11;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;

            }

            switch (alt15) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:178:4: block
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_block_in_stat746);
                    block35=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block35.getTree());

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:182:4: ALAP ^ stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    ALAP36=(Token)match(input,ALAP,FOLLOW_ALAP_in_stat763); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ALAP36_tree = 
                    (CtrlTree)adaptor.create(ALAP36)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(ALAP36_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat766);
                    stat37=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat37.getTree());

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:186:4: ATOM ^ stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    ATOM38=(Token)match(input,ATOM,FOLLOW_ATOM_in_stat783); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ATOM38_tree = 
                    (CtrlTree)adaptor.create(ATOM38)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(ATOM38_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat786);
                    stat39=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat39.getTree());

                    }
                    break;
                case 4 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:191:4: WHILE ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    WHILE40=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat807); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    WHILE40_tree = 
                    (CtrlTree)adaptor.create(WHILE40)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(WHILE40_tree, root_0);
                    }

                    LPAR41=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat810); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat813);
                    cond42=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond42.getTree());

                    RPAR43=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat815); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat818);
                    stat44=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat44.getTree());

                    }
                    break;
                case 5 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:195:5: UNTIL ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    UNTIL45=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat838); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    UNTIL45_tree = 
                    (CtrlTree)adaptor.create(UNTIL45)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(UNTIL45_tree, root_0);
                    }

                    LPAR46=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat841); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat844);
                    cond47=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond47.getTree());

                    RPAR48=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat846); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat849);
                    stat49=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat49.getTree());

                    }
                    break;
                case 6 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:196:4: DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                    {
                    DO50=(Token)match(input,DO,FOLLOW_DO_in_stat854); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DO.add(DO50);


                    pushFollow(FOLLOW_stat_in_stat856);
                    stat51=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_stat.add(stat51.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:197:4: ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==WHILE) ) {
                        alt11=1;
                    }
                    else if ( (LA11_0==UNTIL) ) {
                        alt11=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 11, 0, input);

                        throw nvae;

                    }
                    switch (alt11) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:202:7: WHILE LPAR cond RPAR
                            {
                            WHILE52=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat899); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_WHILE.add(WHILE52);


                            LPAR53=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat901); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR53);


                            pushFollow(FOLLOW_cond_in_stat903);
                            cond54=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond54.getTree());

                            RPAR55=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat905); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR55);


                            // AST REWRITE
                            // elements: WHILE, stat, stat, cond
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
                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:202:31: ^( BLOCK stat ^( WHILE cond stat ) )
                                {
                                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                                root_1 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:202:44: ^( WHILE cond stat )
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
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:209:5: UNTIL LPAR cond RPAR
                            {
                            UNTIL56=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat968); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_UNTIL.add(UNTIL56);


                            LPAR57=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat970); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR57);


                            pushFollow(FOLLOW_cond_in_stat972);
                            cond58=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond58.getTree());

                            RPAR59=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat974); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR59);


                            // AST REWRITE
                            // elements: stat, UNTIL, cond, stat
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
                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:209:29: ^( BLOCK stat ^( UNTIL cond stat ) )
                                {
                                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                                root_1 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:209:42: ^( UNTIL cond stat )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:215:5: IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    IF60=(Token)match(input,IF,FOLLOW_IF_in_stat1021); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IF60_tree = 
                    (CtrlTree)adaptor.create(IF60)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(IF60_tree, root_0);
                    }

                    LPAR61=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1024); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat1027);
                    cond62=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond62.getTree());

                    RPAR63=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1029); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat1032);
                    stat64=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat64.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:215:31: ( ( ELSE )=> ELSE ! stat )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0==ELSE) ) {
                        int LA12_1 = input.LA(2);

                        if ( (synpred1_Ctrl()) ) {
                            alt12=1;
                        }
                    }
                    switch (alt12) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:215:33: ( ELSE )=> ELSE ! stat
                            {
                            ELSE65=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat1042); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat1045);
                            stat66=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat66.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:219:5: TRY ^ stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    TRY67=(Token)match(input,TRY,FOLLOW_TRY_in_stat1069); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRY67_tree = 
                    (CtrlTree)adaptor.create(TRY67)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(TRY67_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat1072);
                    stat68=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat68.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:219:15: ( ( ELSE )=> ELSE ! stat )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==ELSE) ) {
                        int LA13_1 = input.LA(2);

                        if ( (synpred2_Ctrl()) ) {
                            alt13=1;
                        }
                    }
                    switch (alt13) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:219:17: ( ELSE )=> ELSE ! stat
                            {
                            ELSE69=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat1082); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat1085);
                            stat70=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat70.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 9 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:222:5: CHOICE ^ stat ( ( OR )=> OR ! stat )+
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    CHOICE71=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_stat1104); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHOICE71_tree = 
                    (CtrlTree)adaptor.create(CHOICE71)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(CHOICE71_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat1107);
                    stat72=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat72.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:222:18: ( ( OR )=> OR ! stat )+
                    int cnt14=0;
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==OR) ) {
                            int LA14_21 = input.LA(2);

                            if ( (synpred3_Ctrl()) ) {
                                alt14=1;
                            }


                        }


                        switch (alt14) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:222:20: ( OR )=> OR ! stat
                    	    {
                    	    OR73=(Token)match(input,OR,FOLLOW_OR_in_stat1117); if (state.failed) return retval;

                    	    pushFollow(FOLLOW_stat_in_stat1120);
                    	    stat74=stat();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat74.getTree());

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


                    }
                    break;
                case 10 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:225:4: expr SEMI ^
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_expr_in_stat1135);
                    expr75=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr75.getTree());

                    SEMI76=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat1137); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SEMI76_tree = 
                    (CtrlTree)adaptor.create(SEMI76)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(SEMI76_tree, root_0);
                    }

                    }
                    break;
                case 11 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:228:4: var_decl SEMI ^
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_var_decl_in_stat1152);
                    var_decl77=var_decl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, var_decl77.getTree());

                    SEMI78=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat1154); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SEMI78_tree = 
                    (CtrlTree)adaptor.create(SEMI78)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(SEMI78_tree, root_0);
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:232:1: cond : cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) ;
    public final CtrlParser.cond_return cond() throws RecognitionException {
        CtrlParser.cond_return retval = new CtrlParser.cond_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token BAR80=null;
        CtrlParser.cond_atom_return cond_atom79 =null;

        CtrlParser.cond_atom_return cond_atom81 =null;


        CtrlTree BAR80_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_cond_atom=new RewriteRuleSubtreeStream(adaptor,"rule cond_atom");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:233:2: ( cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:235:4: cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            {
            pushFollow(FOLLOW_cond_atom_in_cond1179);
            cond_atom79=cond_atom();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom79.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:236:4: ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==BAR) ) {
                alt17=1;
            }
            else if ( (LA17_0==RPAR) ) {
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:236:6: ( BAR cond_atom )+
                    {
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:236:6: ( BAR cond_atom )+
                    int cnt16=0;
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);

                        if ( (LA16_0==BAR) ) {
                            alt16=1;
                        }


                        switch (alt16) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:236:7: BAR cond_atom
                    	    {
                    	    BAR80=(Token)match(input,BAR,FOLLOW_BAR_in_cond1188); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR80);


                    	    pushFollow(FOLLOW_cond_atom_in_cond1190);
                    	    cond_atom81=cond_atom();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom81.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt16 >= 1 ) break loop16;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(16, input);
                                throw eee;
                        }
                        cnt16++;
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
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:236:26: ^( CHOICE cond_atom ( cond_atom )+ )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:237:6: 
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:241:1: cond_atom : ( TRUE | call );
    public final CtrlParser.cond_atom_return cond_atom() throws RecognitionException {
        CtrlParser.cond_atom_return retval = new CtrlParser.cond_atom_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token TRUE82=null;
        CtrlParser.call_return call83 =null;


        CtrlTree TRUE82_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:242:2: ( TRUE | call )
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==TRUE) ) {
                alt18=1;
            }
            else if ( (LA18_0==ID) ) {
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:244:4: TRUE
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    TRUE82=(Token)match(input,TRUE,FOLLOW_TRUE_in_cond_atom1236); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRUE82_tree = 
                    (CtrlTree)adaptor.create(TRUE82)
                    ;
                    adaptor.addChild(root_0, TRUE82_tree);
                    }

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:248:5: call
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_cond_atom1257);
                    call83=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call83.getTree());

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:251:1: expr : expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) ;
    public final CtrlParser.expr_return expr() throws RecognitionException {
        CtrlParser.expr_return retval = new CtrlParser.expr_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token BAR85=null;
        CtrlParser.expr2_return expr284 =null;

        CtrlParser.expr2_return expr286 =null;


        CtrlTree BAR85_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_expr2=new RewriteRuleSubtreeStream(adaptor,"rule expr2");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:252:2: ( expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:256:4: expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            {
            pushFollow(FOLLOW_expr2_in_expr1287);
            expr284=expr2();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expr2.add(expr284.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:257:4: ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==BAR) ) {
                alt20=1;
            }
            else if ( ((LA20_0 >= RPAR && LA20_0 <= SEMI)) ) {
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:257:6: ( BAR expr2 )+
                    {
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:257:6: ( BAR expr2 )+
                    int cnt19=0;
                    loop19:
                    do {
                        int alt19=2;
                        int LA19_0 = input.LA(1);

                        if ( (LA19_0==BAR) ) {
                            alt19=1;
                        }


                        switch (alt19) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:257:7: BAR expr2
                    	    {
                    	    BAR85=(Token)match(input,BAR,FOLLOW_BAR_in_expr1295); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR85);


                    	    pushFollow(FOLLOW_expr2_in_expr1297);
                    	    expr286=expr2();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expr2.add(expr286.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt19 >= 1 ) break loop19;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(19, input);
                                throw eee;
                        }
                        cnt19++;
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
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:257:22: ^( CHOICE expr2 ( expr2 )+ )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:258:6: 
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:262:1: expr2 : (e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e) |op= SHARP expr_atom -> ^( ALAP[$op] expr_atom ) );
    public final CtrlParser.expr2_return expr2() throws RecognitionException {
        CtrlParser.expr2_return retval = new CtrlParser.expr2_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token plus=null;
        Token ast=null;
        Token op=null;
        CtrlParser.expr_atom_return e =null;

        CtrlParser.expr_atom_return expr_atom87 =null;


        CtrlTree plus_tree=null;
        CtrlTree ast_tree=null;
        CtrlTree op_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_SHARP=new RewriteRuleTokenStream(adaptor,"token SHARP");
        RewriteRuleTokenStream stream_ASTERISK=new RewriteRuleTokenStream(adaptor,"token ASTERISK");
        RewriteRuleSubtreeStream stream_expr_atom=new RewriteRuleSubtreeStream(adaptor,"rule expr_atom");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:263:3: (e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e) |op= SHARP expr_atom -> ^( ALAP[$op] expr_atom ) )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==ANY||LA22_0==ID||LA22_0==LPAR||LA22_0==OTHER) ) {
                alt22=1;
            }
            else if ( (LA22_0==SHARP) ) {
                alt22=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;

            }
            switch (alt22) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:271:5: e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e)
                    {
                    pushFollow(FOLLOW_expr_atom_in_expr21378);
                    e=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(e.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:272:5: (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e)
                    int alt21=3;
                    switch ( input.LA(1) ) {
                    case PLUS:
                        {
                        alt21=1;
                        }
                        break;
                    case ASTERISK:
                        {
                        alt21=2;
                        }
                        break;
                    case BAR:
                    case RPAR:
                    case SEMI:
                        {
                        alt21=3;
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
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:272:7: plus= PLUS
                            {
                            plus=(Token)match(input,PLUS,FOLLOW_PLUS_in_expr21388); if (state.failed) return retval; 
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
                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:272:20: ^( BLOCK $e ^( STAR[$plus] $e) )
                                {
                                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                                root_1 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_e.nextTree());

                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:272:31: ^( STAR[$plus] $e)
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
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:273:7: ast= ASTERISK
                            {
                            ast=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_expr21415); if (state.failed) return retval; 
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
                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:273:23: ^( STAR[$ast] $e)
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
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:274:7: 
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:280:5: op= SHARP expr_atom
                    {
                    op=(Token)match(input,SHARP,FOLLOW_SHARP_in_expr21470); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SHARP.add(op);


                    pushFollow(FOLLOW_expr_atom_in_expr21472);
                    expr_atom87=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(expr_atom87.getTree());

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
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:280:27: ^( ALAP[$op] expr_atom )
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:283:1: expr_atom : ( ANY | OTHER |open= LPAR expr close= RPAR -> ^( BLOCK[$open] expr TRUE[$close] ) | call );
    public final CtrlParser.expr_atom_return expr_atom() throws RecognitionException {
        CtrlParser.expr_atom_return retval = new CtrlParser.expr_atom_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        Token ANY88=null;
        Token OTHER89=null;
        CtrlParser.expr_return expr90 =null;

        CtrlParser.call_return call91 =null;


        CtrlTree open_tree=null;
        CtrlTree close_tree=null;
        CtrlTree ANY88_tree=null;
        CtrlTree OTHER89_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:284:2: ( ANY | OTHER |open= LPAR expr close= RPAR -> ^( BLOCK[$open] expr TRUE[$close] ) | call )
            int alt23=4;
            switch ( input.LA(1) ) {
            case ANY:
                {
                alt23=1;
                }
                break;
            case OTHER:
                {
                alt23=2;
                }
                break;
            case LPAR:
                {
                alt23=3;
                }
                break;
            case ID:
                {
                alt23=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;

            }

            switch (alt23) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:286:4: ANY
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    ANY88=(Token)match(input,ANY,FOLLOW_ANY_in_expr_atom1501); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ANY88_tree = 
                    (CtrlTree)adaptor.create(ANY88)
                    ;
                    adaptor.addChild(root_0, ANY88_tree);
                    }

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:290:4: OTHER
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    OTHER89=(Token)match(input,OTHER,FOLLOW_OTHER_in_expr_atom1518); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OTHER89_tree = 
                    (CtrlTree)adaptor.create(OTHER89)
                    ;
                    adaptor.addChild(root_0, OTHER89_tree);
                    }

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:293:4: open= LPAR expr close= RPAR
                    {
                    open=(Token)match(input,LPAR,FOLLOW_LPAR_in_expr_atom1533); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAR.add(open);


                    pushFollow(FOLLOW_expr_in_expr_atom1535);
                    expr90=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr.add(expr90.getTree());

                    close=(Token)match(input,RPAR,FOLLOW_RPAR_in_expr_atom1539); if (state.failed) return retval; 
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
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:294:7: ^( BLOCK[$open] expr TRUE[$close] )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:297:4: call
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_expr_atom1567);
                    call91=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call91.getTree());

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:301:1: call : rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) ;
    public final CtrlParser.call_return call() throws RecognitionException {
        CtrlParser.call_return retval = new CtrlParser.call_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlParser.rule_name_return rule_name92 =null;

        CtrlParser.arg_list_return arg_list93 =null;


        RewriteRuleSubtreeStream stream_arg_list=new RewriteRuleSubtreeStream(adaptor,"rule arg_list");
        RewriteRuleSubtreeStream stream_rule_name=new RewriteRuleSubtreeStream(adaptor,"rule rule_name");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:302:2: ( rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:306:4: rule_name ( arg_list )?
            {
            pushFollow(FOLLOW_rule_name_in_call1597);
            rule_name92=rule_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_name.add(rule_name92.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:306:14: ( arg_list )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==LPAR) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:306:14: arg_list
                    {
                    pushFollow(FOLLOW_arg_list_in_call1599);
                    arg_list93=arg_list();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg_list.add(arg_list93.getTree());

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
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:307:7: ^( CALL[$rule_name.start] rule_name ( arg_list )? )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(CALL, (rule_name92!=null?((Token)rule_name92.start):null))
                , root_1);

                adaptor.addChild(root_1, stream_rule_name.nextTree());

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:307:42: ( arg_list )?
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:313:1: arg_list : open= LPAR ( arg ( COMMA arg )* )? close= RPAR -> ^( ARGS[$open] ( arg )* RPAR[$close] ) ;
    public final CtrlParser.arg_list_return arg_list() throws RecognitionException {
        CtrlParser.arg_list_return retval = new CtrlParser.arg_list_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        Token COMMA95=null;
        CtrlParser.arg_return arg94 =null;

        CtrlParser.arg_return arg96 =null;


        CtrlTree open_tree=null;
        CtrlTree close_tree=null;
        CtrlTree COMMA95_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_arg=new RewriteRuleSubtreeStream(adaptor,"rule arg");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:314:3: (open= LPAR ( arg ( COMMA arg )* )? close= RPAR -> ^( ARGS[$open] ( arg )* RPAR[$close] ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:316:5: open= LPAR ( arg ( COMMA arg )* )? close= RPAR
            {
            open=(Token)match(input,LPAR,FOLLOW_LPAR_in_arg_list1641); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAR.add(open);


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:316:15: ( arg ( COMMA arg )* )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==DONT_CARE||LA26_0==FALSE||LA26_0==ID||LA26_0==INT_LIT||LA26_0==OUT||LA26_0==REAL_LIT||(LA26_0 >= STRING_LIT && LA26_0 <= TRUE)) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:316:16: arg ( COMMA arg )*
                    {
                    pushFollow(FOLLOW_arg_in_arg_list1644);
                    arg94=arg();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg.add(arg94.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:316:20: ( COMMA arg )*
                    loop25:
                    do {
                        int alt25=2;
                        int LA25_0 = input.LA(1);

                        if ( (LA25_0==COMMA) ) {
                            alt25=1;
                        }


                        switch (alt25) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:316:21: COMMA arg
                    	    {
                    	    COMMA95=(Token)match(input,COMMA,FOLLOW_COMMA_in_arg_list1647); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA95);


                    	    pushFollow(FOLLOW_arg_in_arg_list1649);
                    	    arg96=arg();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_arg.add(arg96.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop25;
                        }
                    } while (true);


                    }
                    break;

            }


            close=(Token)match(input,RPAR,FOLLOW_RPAR_in_arg_list1657); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAR.add(close);


            // AST REWRITE
            // elements: RPAR, arg
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
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:317:8: ^( ARGS[$open] ( arg )* RPAR[$close] )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(ARGS, open)
                , root_1);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:317:22: ( arg )*
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:323:1: arg : ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) );
    public final CtrlParser.arg_return arg() throws RecognitionException {
        CtrlParser.arg_return retval = new CtrlParser.arg_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token OUT97=null;
        Token ID98=null;
        Token ID99=null;
        Token DONT_CARE100=null;
        CtrlParser.literal_return literal101 =null;


        CtrlTree OUT97_tree=null;
        CtrlTree ID98_tree=null;
        CtrlTree ID99_tree=null;
        CtrlTree DONT_CARE100_tree=null;
        RewriteRuleTokenStream stream_DONT_CARE=new RewriteRuleTokenStream(adaptor,"token DONT_CARE");
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:324:3: ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) )
            int alt27=4;
            switch ( input.LA(1) ) {
            case OUT:
                {
                alt27=1;
                }
                break;
            case ID:
                {
                alt27=2;
                }
                break;
            case DONT_CARE:
                {
                alt27=3;
                }
                break;
            case FALSE:
            case INT_LIT:
            case REAL_LIT:
            case STRING_LIT:
            case TRUE:
                {
                alt27=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;

            }

            switch (alt27) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:327:5: OUT ID
                    {
                    OUT97=(Token)match(input,OUT,FOLLOW_OUT_in_arg1704); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT97);


                    ID98=(Token)match(input,ID,FOLLOW_ID_in_arg1706); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID98);


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

                    root_0 = (CtrlTree)adaptor.nil();
                    // 327:12: -> ^( ARG OUT ID )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:327:15: ^( ARG OUT ID )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:331:5: ID
                    {
                    ID99=(Token)match(input,ID,FOLLOW_ID_in_arg1737); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID99);


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
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:331:11: ^( ARG ID )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:335:5: DONT_CARE
                    {
                    DONT_CARE100=(Token)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg1766); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DONT_CARE.add(DONT_CARE100);


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
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:335:18: ^( ARG DONT_CARE )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:336:5: literal
                    {
                    pushFollow(FOLLOW_literal_in_arg1783);
                    literal101=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_literal.add(literal101.getTree());

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
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:336:16: ^( ARG literal )
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:339:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlParser.literal_return literal() throws RecognitionException {
        CtrlParser.literal_return retval = new CtrlParser.literal_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token set102=null;

        CtrlTree set102_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:340:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
            {
            root_0 = (CtrlTree)adaptor.nil();


            set102=(Token)input.LT(1);

            if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
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
    // $ANTLR end "literal"


    public static class rule_name_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "rule_name"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:361:1: rule_name : qual_name ->;
    public final CtrlParser.rule_name_return rule_name() throws RecognitionException {
        CtrlParser.rule_name_return retval = new CtrlParser.rule_name_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlParser.qual_name_return qual_name103 =null;


        RewriteRuleSubtreeStream stream_qual_name=new RewriteRuleSubtreeStream(adaptor,"rule qual_name");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:362:3: ( qual_name ->)
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:362:5: qual_name
            {
            pushFollow(FOLLOW_qual_name_in_rule_name1893);
            qual_name103=qual_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qual_name.add(qual_name103.getTree());

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
                adaptor.addChild(root_0,  helper.qualify((qual_name103!=null?((CtrlTree)qual_name103.tree):null)) );

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:367:1: var_decl : var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) ;
    public final CtrlParser.var_decl_return var_decl() throws RecognitionException {
        CtrlParser.var_decl_return retval = new CtrlParser.var_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token ID105=null;
        Token COMMA106=null;
        Token ID107=null;
        CtrlParser.var_type_return var_type104 =null;


        CtrlTree ID105_tree=null;
        CtrlTree COMMA106_tree=null;
        CtrlTree ID107_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:368:2: ( var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:370:4: var_type ID ( COMMA ID )*
            {
            pushFollow(FOLLOW_var_type_in_var_decl1923);
            var_type104=var_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_var_type.add(var_type104.getTree());

            ID105=(Token)match(input,ID,FOLLOW_ID_in_var_decl1925); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID105);


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:370:16: ( COMMA ID )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0==COMMA) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:370:17: COMMA ID
            	    {
            	    COMMA106=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_decl1928); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA106);


            	    ID107=(Token)match(input,ID,FOLLOW_ID_in_var_decl1930); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ID107);


            	    }
            	    break;

            	default :
            	    break loop28;
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
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:370:31: ^( VAR var_type ( ID )+ )
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:374:1: var_type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlParser.var_type_return var_type() throws RecognitionException {
        CtrlParser.var_type_return retval = new CtrlParser.var_type_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token set108=null;

        CtrlTree set108_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:375:2: ( NODE | BOOL | STRING | INT | REAL )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
            {
            root_0 = (CtrlTree)adaptor.nil();


            set108=(Token)input.LT(1);

            if ( input.LA(1)==BOOL||input.LA(1)==INT||input.LA(1)==NODE||input.LA(1)==REAL||input.LA(1)==STRING ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CtrlTree)adaptor.create(set108)
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
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:215:33: ( ELSE )
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:215:34: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred1_Ctrl1037); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred1_Ctrl

    // $ANTLR start synpred2_Ctrl
    public final void synpred2_Ctrl_fragment() throws RecognitionException {
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:219:17: ( ELSE )
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:219:18: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred2_Ctrl1077); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred2_Ctrl

    // $ANTLR start synpred3_Ctrl
    public final void synpred3_Ctrl_fragment() throws RecognitionException {
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:222:20: ( OR )
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:222:21: OR
        {
        match(input,OR,FOLLOW_OR_in_synpred3_Ctrl1112); if (state.failed) return ;

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
    public static final BitSet FOLLOW_par_list_in_function529 = new BitSet(new long[]{0x0002000800000000L});
    public static final BitSet FOLLOW_PRIORITY_in_function532 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_INT_LIT_in_function535 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_block_in_function539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_par_list570 = new BitSet(new long[]{0x4220108100002000L});
    public static final BitSet FOLLOW_par_in_par_list573 = new BitSet(new long[]{0x0200000000020000L});
    public static final BitSet FOLLOW_COMMA_in_par_list576 = new BitSet(new long[]{0x4020108100002000L});
    public static final BitSet FOLLOW_par_in_par_list578 = new BitSet(new long[]{0x0200000000020000L});
    public static final BitSet FOLLOW_RPAR_in_par_list584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_par629 = new BitSet(new long[]{0x4020008100002000L});
    public static final BitSet FOLLOW_var_type_in_par631 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_par633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_type_in_par666 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_par668 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LCURLY_in_block707 = new BitSet(new long[]{0x4830089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_block709 = new BitSet(new long[]{0x4830089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_RCURLY_in_block714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_stat746 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat763 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat766 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATOM_in_stat783 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat786 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_stat807 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat810 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_cond_in_stat813 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat815 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat838 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat841 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_cond_in_stat844 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat846 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat849 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_stat854 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat856 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000014L});
    public static final BitSet FOLLOW_WHILE_in_stat899 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat901 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_cond_in_stat903 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat968 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat970 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_cond_in_stat972 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_stat1021 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat1024 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_cond_in_stat1027 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat1029 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat1032 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_ELSE_in_stat1042 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat1045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_stat1069 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat1072 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_ELSE_in_stat1082 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat1085 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_stat1104 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat1107 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_OR_in_stat1117 = new BitSet(new long[]{0x4820089930052450L,0x0000000000000016L});
    public static final BitSet FOLLOW_stat_in_stat1120 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_expr_in_stat1135 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat1137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat1152 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat1154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cond_atom_in_cond1179 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_BAR_in_cond1188 = new BitSet(new long[]{0x0000000010000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_cond_atom_in_cond1190 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_TRUE_in_cond_atom1236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_cond_atom1257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr2_in_expr1287 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_BAR_in_expr1295 = new BitSet(new long[]{0x0800081010000040L});
    public static final BitSet FOLLOW_expr2_in_expr1297 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_expr_atom_in_expr21378 = new BitSet(new long[]{0x0001000000000202L});
    public static final BitSet FOLLOW_PLUS_in_expr21388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERISK_in_expr21415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expr21470 = new BitSet(new long[]{0x0000081010000040L});
    public static final BitSet FOLLOW_expr_atom_in_expr21472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expr_atom1501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expr_atom1518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_atom1533 = new BitSet(new long[]{0x0800081010000040L});
    public static final BitSet FOLLOW_expr_in_expr_atom1535 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_RPAR_in_expr_atom1539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expr_atom1567 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_name_in_call1597 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_arg_list_in_call1599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_arg_list1641 = new BitSet(new long[]{0x8240100212080000L,0x0000000000000001L});
    public static final BitSet FOLLOW_arg_in_arg_list1644 = new BitSet(new long[]{0x0200000000020000L});
    public static final BitSet FOLLOW_COMMA_in_arg_list1647 = new BitSet(new long[]{0x8040100212080000L,0x0000000000000001L});
    public static final BitSet FOLLOW_arg_in_arg_list1649 = new BitSet(new long[]{0x0200000000020000L});
    public static final BitSet FOLLOW_RPAR_in_arg_list1657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_arg1704 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_arg1706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_arg1737 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg1766 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_arg1783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qual_name_in_rule_name1893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_type_in_var_decl1923 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_var_decl1925 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_COMMA_in_var_decl1928 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_var_decl1930 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_ELSE_in_synpred1_Ctrl1037 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred2_Ctrl1077 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_synpred3_Ctrl1112 = new BitSet(new long[]{0x0000000000000002L});

}