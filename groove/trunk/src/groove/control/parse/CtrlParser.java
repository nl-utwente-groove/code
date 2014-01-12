// $ANTLR 3.4 D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g 2014-01-12 16:19:08

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALAP", "AMP", "ANY", "ARG", "ARGS", "ASTERISK", "ATOM", "BAR", "BLOCK", "BOOL", "BSLASH", "CALL", "CHOICE", "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", "ELSE", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF", "IMPORT", "INT", "INT_LIT", "IntegerNumber", "LCURLY", "LPAR", "MINUS", "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT", "PACKAGE", "PAR", "PARS", "PLUS", "PRIORITY", "PROGRAM", "QUOTE", "RCURLY", "REAL", "REAL_LIT", "RECIPE", "RECIPES", "RPAR", "SEMI", "SHARP", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", "TRUE", "TRY", "UNTIL", "VAR", "WHILE", "WS"
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
    public static final int INT=31;
    public static final int INT_LIT=32;
    public static final int IntegerNumber=33;
    public static final int LCURLY=34;
    public static final int LPAR=35;
    public static final int MINUS=36;
    public static final int ML_COMMENT=37;
    public static final int NODE=38;
    public static final int NOT=39;
    public static final int NonIntegerNumber=40;
    public static final int OR=41;
    public static final int OTHER=42;
    public static final int OUT=43;
    public static final int PACKAGE=44;
    public static final int PAR=45;
    public static final int PARS=46;
    public static final int PLUS=47;
    public static final int PRIORITY=48;
    public static final int PROGRAM=49;
    public static final int QUOTE=50;
    public static final int RCURLY=51;
    public static final int REAL=52;
    public static final int REAL_LIT=53;
    public static final int RECIPE=54;
    public static final int RECIPES=55;
    public static final int RPAR=56;
    public static final int SEMI=57;
    public static final int SHARP=58;
    public static final int SL_COMMENT=59;
    public static final int STAR=60;
    public static final int STRING=61;
    public static final int STRING_LIT=62;
    public static final int TRUE=63;
    public static final int TRY=64;
    public static final int UNTIL=65;
    public static final int VAR=66;
    public static final int WHILE=67;
    public static final int WS=68;

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:74:1: program : package_decl ( import_decl )* ( function | recipe | stat )* eof= EOF -> ^( PROGRAM package_decl ( import_decl )* ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* TRUE[$eof] ) ) ;
    public final CtrlParser.program_return program() throws RecognitionException {
        CtrlParser.program_return retval = new CtrlParser.program_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token eof=null;
        CtrlParser.package_decl_return package_decl1 =null;

        CtrlParser.import_decl_return import_decl2 =null;

        CtrlParser.function_return function3 =null;

        CtrlParser.recipe_return recipe4 =null;

        CtrlParser.stat_return stat5 =null;


        CtrlTree eof_tree=null;
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleSubtreeStream stream_package_decl=new RewriteRuleSubtreeStream(adaptor,"rule package_decl");
        RewriteRuleSubtreeStream stream_recipe=new RewriteRuleSubtreeStream(adaptor,"rule recipe");
        RewriteRuleSubtreeStream stream_import_decl=new RewriteRuleSubtreeStream(adaptor,"rule import_decl");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        RewriteRuleSubtreeStream stream_function=new RewriteRuleSubtreeStream(adaptor,"rule function");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:75:3: ( package_decl ( import_decl )* ( function | recipe | stat )* eof= EOF -> ^( PROGRAM package_decl ( import_decl )* ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* TRUE[$eof] ) ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:79:5: package_decl ( import_decl )* ( function | recipe | stat )* eof= EOF
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


            eof=(Token)match(input,EOF,FOLLOW_EOF_in_program175); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EOF.add(eof);


            if ( state.backtracking==0 ) { helper.checkEOF(eof_tree); }

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
            // 83:5: -> ^( PROGRAM package_decl ( import_decl )* ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* TRUE[$eof] ) )
            {
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:83:8: ^( PROGRAM package_decl ( import_decl )* ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* TRUE[$eof] ) )
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

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:88:11: ^( BLOCK ( stat )* TRUE[$eof] )
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
                (CtrlTree)adaptor.create(TRUE, eof)
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
        CtrlParser.qual_name_return qual_name6 =null;


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
            else if ( (LA3_0==EOF||LA3_0==ALAP||LA3_0==ANY||LA3_0==ATOM||LA3_0==BOOL||LA3_0==CHOICE||LA3_0==DO||LA3_0==FUNCTION||(LA3_0 >= ID && LA3_0 <= INT)||(LA3_0 >= LCURLY && LA3_0 <= LPAR)||LA3_0==NODE||LA3_0==OTHER||LA3_0==REAL||LA3_0==RECIPE||LA3_0==SHARP||LA3_0==STRING||(LA3_0 >= TRY && LA3_0 <= UNTIL)||LA3_0==WHILE) ) {
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
                    key=(Token)match(input,PACKAGE,FOLLOW_PACKAGE_in_package_decl311); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PACKAGE.add(key);


                    pushFollow(FOLLOW_qual_name_in_package_decl313);
                    qual_name6=qual_name();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_qual_name.add(qual_name6.getTree());

                    close=(Token)match(input,SEMI,FOLLOW_SEMI_in_package_decl317); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMI.add(close);


                    if ( state.backtracking==0 ) { helper.setPackage((qual_name6!=null?((CtrlTree)qual_name6.tree):null)); }

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

        Token IMPORT7=null;
        Token SEMI9=null;
        CtrlParser.qual_name_return qual_name8 =null;


        CtrlTree IMPORT7_tree=null;
        CtrlTree SEMI9_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:105:3: ( IMPORT ^ qual_name SEMI )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:107:5: IMPORT ^ qual_name SEMI
            {
            root_0 = (CtrlTree)adaptor.nil();


            IMPORT7=(Token)match(input,IMPORT,FOLLOW_IMPORT_in_import_decl384); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IMPORT7_tree = 
            (CtrlTree)adaptor.create(IMPORT7)
            ;
            root_0 = (CtrlTree)adaptor.becomeRoot(IMPORT7_tree, root_0);
            }

            pushFollow(FOLLOW_qual_name_in_import_decl387);
            qual_name8=qual_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qual_name8.getTree());

            SEMI9=(Token)match(input,SEMI,FOLLOW_SEMI_in_import_decl389); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            SEMI9_tree = 
            (CtrlTree)adaptor.create(SEMI9)
            ;
            adaptor.addChild(root_0, SEMI9_tree);
            }

            if ( state.backtracking==0 ) { helper.addImport((qual_name8!=null?((CtrlTree)qual_name8.tree):null));
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

        Token DOT10=null;
        Token ids=null;
        List list_ids=null;

        CtrlTree DOT10_tree=null;
        CtrlTree ids_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:114:3: (ids+= ID ( DOT ids+= ID )* ->)
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:114:5: ids+= ID ( DOT ids+= ID )*
            {
            ids=(Token)match(input,ID,FOLLOW_ID_in_qual_name412); if (state.failed) return retval; 
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
            	    DOT10=(Token)match(input,DOT,FOLLOW_DOT_in_qual_name415); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(DOT10);


            	    ids=(Token)match(input,ID,FOLLOW_ID_in_qual_name419); if (state.failed) return retval; 
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:121:1: recipe : RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block ;
    public final CtrlParser.recipe_return recipe() throws RecognitionException {
        CtrlParser.recipe_return retval = new CtrlParser.recipe_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token RECIPE11=null;
        Token ID12=null;
        Token PRIORITY14=null;
        Token INT_LIT15=null;
        CtrlParser.par_list_return par_list13 =null;

        CtrlParser.block_return block16 =null;


        CtrlTree RECIPE11_tree=null;
        CtrlTree ID12_tree=null;
        CtrlTree PRIORITY14_tree=null;
        CtrlTree INT_LIT15_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:122:3: ( RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:125:5: RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block
            {
            root_0 = (CtrlTree)adaptor.nil();


            RECIPE11=(Token)match(input,RECIPE,FOLLOW_RECIPE_in_recipe459); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RECIPE11_tree = 
            (CtrlTree)adaptor.create(RECIPE11)
            ;
            root_0 = (CtrlTree)adaptor.becomeRoot(RECIPE11_tree, root_0);
            }

            ID12=(Token)match(input,ID,FOLLOW_ID_in_recipe462); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID12_tree = 
            (CtrlTree)adaptor.create(ID12)
            ;
            adaptor.addChild(root_0, ID12_tree);
            }

            pushFollow(FOLLOW_par_list_in_recipe464);
            par_list13=par_list();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, par_list13.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:125:25: ( PRIORITY ! INT_LIT )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==PRIORITY) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:125:26: PRIORITY ! INT_LIT
                    {
                    PRIORITY14=(Token)match(input,PRIORITY,FOLLOW_PRIORITY_in_recipe467); if (state.failed) return retval;

                    INT_LIT15=(Token)match(input,INT_LIT,FOLLOW_INT_LIT_in_recipe470); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT_LIT15_tree = 
                    (CtrlTree)adaptor.create(INT_LIT15)
                    ;
                    adaptor.addChild(root_0, INT_LIT15_tree);
                    }

                    }
                    break;

            }


            pushFollow(FOLLOW_block_in_recipe474);
            block16=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block16.getTree());

            if ( state.backtracking==0 ) { helper.declareCtrlUnit(RECIPE11_tree); }

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:133:1: function : FUNCTION ^ ID par_list ( PRIORITY ! INT_LIT )? block ;
    public final CtrlParser.function_return function() throws RecognitionException {
        CtrlParser.function_return retval = new CtrlParser.function_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token FUNCTION17=null;
        Token ID18=null;
        Token PRIORITY20=null;
        Token INT_LIT21=null;
        CtrlParser.par_list_return par_list19 =null;

        CtrlParser.block_return block22 =null;


        CtrlTree FUNCTION17_tree=null;
        CtrlTree ID18_tree=null;
        CtrlTree PRIORITY20_tree=null;
        CtrlTree INT_LIT21_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:134:3: ( FUNCTION ^ ID par_list ( PRIORITY ! INT_LIT )? block )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:137:5: FUNCTION ^ ID par_list ( PRIORITY ! INT_LIT )? block
            {
            root_0 = (CtrlTree)adaptor.nil();


            FUNCTION17=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function510); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            FUNCTION17_tree = 
            (CtrlTree)adaptor.create(FUNCTION17)
            ;
            root_0 = (CtrlTree)adaptor.becomeRoot(FUNCTION17_tree, root_0);
            }

            ID18=(Token)match(input,ID,FOLLOW_ID_in_function513); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID18_tree = 
            (CtrlTree)adaptor.create(ID18)
            ;
            adaptor.addChild(root_0, ID18_tree);
            }

            pushFollow(FOLLOW_par_list_in_function515);
            par_list19=par_list();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, par_list19.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:137:27: ( PRIORITY ! INT_LIT )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==PRIORITY) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:137:28: PRIORITY ! INT_LIT
                    {
                    PRIORITY20=(Token)match(input,PRIORITY,FOLLOW_PRIORITY_in_function518); if (state.failed) return retval;

                    INT_LIT21=(Token)match(input,INT_LIT,FOLLOW_INT_LIT_in_function521); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT_LIT21_tree = 
                    (CtrlTree)adaptor.create(INT_LIT21)
                    ;
                    adaptor.addChild(root_0, INT_LIT21_tree);
                    }

                    }
                    break;

            }


            pushFollow(FOLLOW_block_in_function525);
            block22=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block22.getTree());

            if ( state.backtracking==0 ) { helper.declareCtrlUnit(FUNCTION17_tree); }

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:144:1: par_list : LPAR ( par ( COMMA par )* )? RPAR -> ^( PARS ( par )* ) ;
    public final CtrlParser.par_list_return par_list() throws RecognitionException {
        CtrlParser.par_list_return retval = new CtrlParser.par_list_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token LPAR23=null;
        Token COMMA25=null;
        Token RPAR27=null;
        CtrlParser.par_return par24 =null;

        CtrlParser.par_return par26 =null;


        CtrlTree LPAR23_tree=null;
        CtrlTree COMMA25_tree=null;
        CtrlTree RPAR27_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_par=new RewriteRuleSubtreeStream(adaptor,"rule par");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:145:3: ( LPAR ( par ( COMMA par )* )? RPAR -> ^( PARS ( par )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:147:5: LPAR ( par ( COMMA par )* )? RPAR
            {
            LPAR23=(Token)match(input,LPAR,FOLLOW_LPAR_in_par_list556); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAR.add(LPAR23);


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:147:10: ( par ( COMMA par )* )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==BOOL||LA8_0==INT||LA8_0==NODE||LA8_0==OUT||LA8_0==REAL||LA8_0==STRING) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:147:11: par ( COMMA par )*
                    {
                    pushFollow(FOLLOW_par_in_par_list559);
                    par24=par();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_par.add(par24.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:147:15: ( COMMA par )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==COMMA) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:147:16: COMMA par
                    	    {
                    	    COMMA25=(Token)match(input,COMMA,FOLLOW_COMMA_in_par_list562); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA25);


                    	    pushFollow(FOLLOW_par_in_par_list564);
                    	    par26=par();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_par.add(par26.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                    }
                    break;

            }


            RPAR27=(Token)match(input,RPAR,FOLLOW_RPAR_in_par_list570); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAR.add(RPAR27);


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
            // 148:5: -> ^( PARS ( par )* )
            {
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:148:8: ^( PARS ( par )* )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(PARS, "PARS")
                , root_1);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:148:15: ( par )*
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:154:1: par : ( OUT var_type ID -> ^( PAR OUT var_type ID ) | var_type ID -> ^( PAR var_type ID ) );
    public final CtrlParser.par_return par() throws RecognitionException {
        CtrlParser.par_return retval = new CtrlParser.par_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token OUT28=null;
        Token ID30=null;
        Token ID32=null;
        CtrlParser.var_type_return var_type29 =null;

        CtrlParser.var_type_return var_type31 =null;


        CtrlTree OUT28_tree=null;
        CtrlTree ID30_tree=null;
        CtrlTree ID32_tree=null;
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:155:3: ( OUT var_type ID -> ^( PAR OUT var_type ID ) | var_type ID -> ^( PAR var_type ID ) )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:158:5: OUT var_type ID
                    {
                    OUT28=(Token)match(input,OUT,FOLLOW_OUT_in_par615); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT28);


                    pushFollow(FOLLOW_var_type_in_par617);
                    var_type29=var_type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_var_type.add(var_type29.getTree());

                    ID30=(Token)match(input,ID,FOLLOW_ID_in_par619); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID30);


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
                    // 158:21: -> ^( PAR OUT var_type ID )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:158:24: ^( PAR OUT var_type ID )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:162:5: var_type ID
                    {
                    pushFollow(FOLLOW_var_type_in_par652);
                    var_type31=var_type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_var_type.add(var_type31.getTree());

                    ID32=(Token)match(input,ID,FOLLOW_ID_in_par654); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID32);


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
                    // 162:17: -> ^( PAR var_type ID )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:162:20: ^( PAR var_type ID )
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:166:1: block : open= LCURLY ( stat )* close= RCURLY -> ^( BLOCK[$open] ( stat )* TRUE[$close] ) ;
    public final CtrlParser.block_return block() throws RecognitionException {
        CtrlParser.block_return retval = new CtrlParser.block_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        CtrlParser.stat_return stat33 =null;


        CtrlTree open_tree=null;
        CtrlTree close_tree=null;
        RewriteRuleTokenStream stream_LCURLY=new RewriteRuleTokenStream(adaptor,"token LCURLY");
        RewriteRuleTokenStream stream_RCURLY=new RewriteRuleTokenStream(adaptor,"token RCURLY");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:167:3: (open= LCURLY ( stat )* close= RCURLY -> ^( BLOCK[$open] ( stat )* TRUE[$close] ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:169:5: open= LCURLY ( stat )* close= RCURLY
            {
            open=(Token)match(input,LCURLY,FOLLOW_LCURLY_in_block693); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LCURLY.add(open);


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:169:17: ( stat )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==ALAP||LA10_0==ANY||LA10_0==ATOM||LA10_0==BOOL||LA10_0==CHOICE||LA10_0==DO||(LA10_0 >= ID && LA10_0 <= IF)||LA10_0==INT||(LA10_0 >= LCURLY && LA10_0 <= LPAR)||LA10_0==NODE||LA10_0==OTHER||LA10_0==REAL||LA10_0==SHARP||LA10_0==STRING||(LA10_0 >= TRY && LA10_0 <= UNTIL)||LA10_0==WHILE) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:169:17: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_block695);
            	    stat33=stat();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_stat.add(stat33.getTree());

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            close=(Token)match(input,RCURLY,FOLLOW_RCURLY_in_block700); if (state.failed) return retval; 
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
            // 170:5: -> ^( BLOCK[$open] ( stat )* TRUE[$close] )
            {
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:8: ^( BLOCK[$open] ( stat )* TRUE[$close] )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(BLOCK, open)
                , root_1);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:23: ( stat )*
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:173:1: stat : ( block | ALAP ^ stat | ATOM ^ stat | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI ^| var_decl SEMI ^);
    public final CtrlParser.stat_return stat() throws RecognitionException {
        CtrlParser.stat_return retval = new CtrlParser.stat_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token ALAP35=null;
        Token ATOM37=null;
        Token WHILE39=null;
        Token LPAR40=null;
        Token RPAR42=null;
        Token UNTIL44=null;
        Token LPAR45=null;
        Token RPAR47=null;
        Token DO49=null;
        Token WHILE51=null;
        Token LPAR52=null;
        Token RPAR54=null;
        Token UNTIL55=null;
        Token LPAR56=null;
        Token RPAR58=null;
        Token IF59=null;
        Token LPAR60=null;
        Token RPAR62=null;
        Token ELSE64=null;
        Token TRY66=null;
        Token ELSE68=null;
        Token CHOICE70=null;
        Token OR72=null;
        Token SEMI75=null;
        Token SEMI77=null;
        CtrlParser.block_return block34 =null;

        CtrlParser.stat_return stat36 =null;

        CtrlParser.stat_return stat38 =null;

        CtrlParser.cond_return cond41 =null;

        CtrlParser.stat_return stat43 =null;

        CtrlParser.cond_return cond46 =null;

        CtrlParser.stat_return stat48 =null;

        CtrlParser.stat_return stat50 =null;

        CtrlParser.cond_return cond53 =null;

        CtrlParser.cond_return cond57 =null;

        CtrlParser.cond_return cond61 =null;

        CtrlParser.stat_return stat63 =null;

        CtrlParser.stat_return stat65 =null;

        CtrlParser.stat_return stat67 =null;

        CtrlParser.stat_return stat69 =null;

        CtrlParser.stat_return stat71 =null;

        CtrlParser.stat_return stat73 =null;

        CtrlParser.expr_return expr74 =null;

        CtrlParser.var_decl_return var_decl76 =null;


        CtrlTree ALAP35_tree=null;
        CtrlTree ATOM37_tree=null;
        CtrlTree WHILE39_tree=null;
        CtrlTree LPAR40_tree=null;
        CtrlTree RPAR42_tree=null;
        CtrlTree UNTIL44_tree=null;
        CtrlTree LPAR45_tree=null;
        CtrlTree RPAR47_tree=null;
        CtrlTree DO49_tree=null;
        CtrlTree WHILE51_tree=null;
        CtrlTree LPAR52_tree=null;
        CtrlTree RPAR54_tree=null;
        CtrlTree UNTIL55_tree=null;
        CtrlTree LPAR56_tree=null;
        CtrlTree RPAR58_tree=null;
        CtrlTree IF59_tree=null;
        CtrlTree LPAR60_tree=null;
        CtrlTree RPAR62_tree=null;
        CtrlTree ELSE64_tree=null;
        CtrlTree TRY66_tree=null;
        CtrlTree ELSE68_tree=null;
        CtrlTree CHOICE70_tree=null;
        CtrlTree OR72_tree=null;
        CtrlTree SEMI75_tree=null;
        CtrlTree SEMI77_tree=null;
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_WHILE=new RewriteRuleTokenStream(adaptor,"token WHILE");
        RewriteRuleTokenStream stream_UNTIL=new RewriteRuleTokenStream(adaptor,"token UNTIL");
        RewriteRuleSubtreeStream stream_cond=new RewriteRuleSubtreeStream(adaptor,"rule cond");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:174:2: ( block | ALAP ^ stat | ATOM ^ stat | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI ^| var_decl SEMI ^)
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:175:4: block
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_block_in_stat732);
                    block34=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block34.getTree());

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:179:4: ALAP ^ stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    ALAP35=(Token)match(input,ALAP,FOLLOW_ALAP_in_stat749); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ALAP35_tree = 
                    (CtrlTree)adaptor.create(ALAP35)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(ALAP35_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat752);
                    stat36=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat36.getTree());

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:183:4: ATOM ^ stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    ATOM37=(Token)match(input,ATOM,FOLLOW_ATOM_in_stat769); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ATOM37_tree = 
                    (CtrlTree)adaptor.create(ATOM37)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(ATOM37_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat772);
                    stat38=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat38.getTree());

                    }
                    break;
                case 4 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:188:4: WHILE ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    WHILE39=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat793); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    WHILE39_tree = 
                    (CtrlTree)adaptor.create(WHILE39)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(WHILE39_tree, root_0);
                    }

                    LPAR40=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat796); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat799);
                    cond41=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond41.getTree());

                    RPAR42=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat801); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat804);
                    stat43=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat43.getTree());

                    }
                    break;
                case 5 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:192:5: UNTIL ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    UNTIL44=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat824); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    UNTIL44_tree = 
                    (CtrlTree)adaptor.create(UNTIL44)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(UNTIL44_tree, root_0);
                    }

                    LPAR45=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat827); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat830);
                    cond46=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond46.getTree());

                    RPAR47=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat832); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat835);
                    stat48=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat48.getTree());

                    }
                    break;
                case 6 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:193:4: DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                    {
                    DO49=(Token)match(input,DO,FOLLOW_DO_in_stat840); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DO.add(DO49);


                    pushFollow(FOLLOW_stat_in_stat842);
                    stat50=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_stat.add(stat50.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:194:4: ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
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
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:199:7: WHILE LPAR cond RPAR
                            {
                            WHILE51=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat885); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_WHILE.add(WHILE51);


                            LPAR52=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat887); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR52);


                            pushFollow(FOLLOW_cond_in_stat889);
                            cond53=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond53.getTree());

                            RPAR54=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat891); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR54);


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
                            // 199:28: -> ^( BLOCK stat ^( WHILE cond stat ) )
                            {
                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:199:31: ^( BLOCK stat ^( WHILE cond stat ) )
                                {
                                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                                root_1 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:199:44: ^( WHILE cond stat )
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
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:206:5: UNTIL LPAR cond RPAR
                            {
                            UNTIL55=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat954); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_UNTIL.add(UNTIL55);


                            LPAR56=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat956); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR56);


                            pushFollow(FOLLOW_cond_in_stat958);
                            cond57=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond57.getTree());

                            RPAR58=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat960); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR58);


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
                            // 206:26: -> ^( BLOCK stat ^( UNTIL cond stat ) )
                            {
                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:206:29: ^( BLOCK stat ^( UNTIL cond stat ) )
                                {
                                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                                root_1 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:206:42: ^( UNTIL cond stat )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:212:5: IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    IF59=(Token)match(input,IF,FOLLOW_IF_in_stat1007); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IF59_tree = 
                    (CtrlTree)adaptor.create(IF59)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(IF59_tree, root_0);
                    }

                    LPAR60=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1010); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat1013);
                    cond61=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond61.getTree());

                    RPAR62=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1015); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat1018);
                    stat63=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat63.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:212:31: ( ( ELSE )=> ELSE ! stat )?
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
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:212:33: ( ELSE )=> ELSE ! stat
                            {
                            ELSE64=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat1028); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat1031);
                            stat65=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat65.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:216:5: TRY ^ stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    TRY66=(Token)match(input,TRY,FOLLOW_TRY_in_stat1055); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRY66_tree = 
                    (CtrlTree)adaptor.create(TRY66)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(TRY66_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat1058);
                    stat67=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat67.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:216:15: ( ( ELSE )=> ELSE ! stat )?
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
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:216:17: ( ELSE )=> ELSE ! stat
                            {
                            ELSE68=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat1068); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat1071);
                            stat69=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat69.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 9 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:219:5: CHOICE ^ stat ( ( OR )=> OR ! stat )+
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    CHOICE70=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_stat1090); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHOICE70_tree = 
                    (CtrlTree)adaptor.create(CHOICE70)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(CHOICE70_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat1093);
                    stat71=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat71.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:219:18: ( ( OR )=> OR ! stat )+
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
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:219:20: ( OR )=> OR ! stat
                    	    {
                    	    OR72=(Token)match(input,OR,FOLLOW_OR_in_stat1103); if (state.failed) return retval;

                    	    pushFollow(FOLLOW_stat_in_stat1106);
                    	    stat73=stat();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat73.getTree());

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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:222:4: expr SEMI ^
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_expr_in_stat1121);
                    expr74=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr74.getTree());

                    SEMI75=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat1123); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SEMI75_tree = 
                    (CtrlTree)adaptor.create(SEMI75)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(SEMI75_tree, root_0);
                    }

                    }
                    break;
                case 11 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:225:4: var_decl SEMI ^
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_var_decl_in_stat1138);
                    var_decl76=var_decl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, var_decl76.getTree());

                    SEMI77=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat1140); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SEMI77_tree = 
                    (CtrlTree)adaptor.create(SEMI77)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(SEMI77_tree, root_0);
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:229:1: cond : cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) ;
    public final CtrlParser.cond_return cond() throws RecognitionException {
        CtrlParser.cond_return retval = new CtrlParser.cond_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token BAR79=null;
        CtrlParser.cond_atom_return cond_atom78 =null;

        CtrlParser.cond_atom_return cond_atom80 =null;


        CtrlTree BAR79_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_cond_atom=new RewriteRuleSubtreeStream(adaptor,"rule cond_atom");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:230:2: ( cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:232:4: cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            {
            pushFollow(FOLLOW_cond_atom_in_cond1165);
            cond_atom78=cond_atom();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom78.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:233:4: ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:233:6: ( BAR cond_atom )+
                    {
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:233:6: ( BAR cond_atom )+
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
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:233:7: BAR cond_atom
                    	    {
                    	    BAR79=(Token)match(input,BAR,FOLLOW_BAR_in_cond1174); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR79);


                    	    pushFollow(FOLLOW_cond_atom_in_cond1176);
                    	    cond_atom80=cond_atom();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom80.getTree());

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
                    // 233:23: -> ^( CHOICE cond_atom ( cond_atom )+ )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:233:26: ^( CHOICE cond_atom ( cond_atom )+ )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:234:6: 
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
                    // 234:6: -> cond_atom
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:238:1: cond_atom : ( TRUE | call );
    public final CtrlParser.cond_atom_return cond_atom() throws RecognitionException {
        CtrlParser.cond_atom_return retval = new CtrlParser.cond_atom_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token TRUE81=null;
        CtrlParser.call_return call82 =null;


        CtrlTree TRUE81_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:239:2: ( TRUE | call )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:241:4: TRUE
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    TRUE81=(Token)match(input,TRUE,FOLLOW_TRUE_in_cond_atom1222); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRUE81_tree = 
                    (CtrlTree)adaptor.create(TRUE81)
                    ;
                    adaptor.addChild(root_0, TRUE81_tree);
                    }

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:245:5: call
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_cond_atom1243);
                    call82=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call82.getTree());

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:248:1: expr : expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) ;
    public final CtrlParser.expr_return expr() throws RecognitionException {
        CtrlParser.expr_return retval = new CtrlParser.expr_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token BAR84=null;
        CtrlParser.expr2_return expr283 =null;

        CtrlParser.expr2_return expr285 =null;


        CtrlTree BAR84_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_expr2=new RewriteRuleSubtreeStream(adaptor,"rule expr2");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:249:2: ( expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:253:4: expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            {
            pushFollow(FOLLOW_expr2_in_expr1273);
            expr283=expr2();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expr2.add(expr283.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:254:4: ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:254:6: ( BAR expr2 )+
                    {
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:254:6: ( BAR expr2 )+
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
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:254:7: BAR expr2
                    	    {
                    	    BAR84=(Token)match(input,BAR,FOLLOW_BAR_in_expr1281); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR84);


                    	    pushFollow(FOLLOW_expr2_in_expr1283);
                    	    expr285=expr2();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expr2.add(expr285.getTree());

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
                    // 254:19: -> ^( CHOICE expr2 ( expr2 )+ )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:254:22: ^( CHOICE expr2 ( expr2 )+ )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:255:6: 
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
                    // 255:6: -> expr2
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:259:1: expr2 : (e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e) |op= SHARP expr_atom -> ^( ALAP[$op] expr_atom ) );
    public final CtrlParser.expr2_return expr2() throws RecognitionException {
        CtrlParser.expr2_return retval = new CtrlParser.expr2_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token plus=null;
        Token ast=null;
        Token op=null;
        CtrlParser.expr_atom_return e =null;

        CtrlParser.expr_atom_return expr_atom86 =null;


        CtrlTree plus_tree=null;
        CtrlTree ast_tree=null;
        CtrlTree op_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_SHARP=new RewriteRuleTokenStream(adaptor,"token SHARP");
        RewriteRuleTokenStream stream_ASTERISK=new RewriteRuleTokenStream(adaptor,"token ASTERISK");
        RewriteRuleSubtreeStream stream_expr_atom=new RewriteRuleSubtreeStream(adaptor,"rule expr_atom");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:260:3: (e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e) |op= SHARP expr_atom -> ^( ALAP[$op] expr_atom ) )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:268:5: e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e)
                    {
                    pushFollow(FOLLOW_expr_atom_in_expr21364);
                    e=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(e.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:269:5: (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e)
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
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:269:7: plus= PLUS
                            {
                            plus=(Token)match(input,PLUS,FOLLOW_PLUS_in_expr21374); if (state.failed) return retval; 
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
                            // 269:17: -> ^( BLOCK $e ^( STAR[$plus] $e) )
                            {
                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:269:20: ^( BLOCK $e ^( STAR[$plus] $e) )
                                {
                                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                                root_1 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_e.nextTree());

                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:269:31: ^( STAR[$plus] $e)
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
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:270:7: ast= ASTERISK
                            {
                            ast=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_expr21401); if (state.failed) return retval; 
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
                            // 270:20: -> ^( STAR[$ast] $e)
                            {
                                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:270:23: ^( STAR[$ast] $e)
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
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:271:7: 
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
                            // 271:7: -> $e
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:277:5: op= SHARP expr_atom
                    {
                    op=(Token)match(input,SHARP,FOLLOW_SHARP_in_expr21456); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SHARP.add(op);


                    pushFollow(FOLLOW_expr_atom_in_expr21458);
                    expr_atom86=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(expr_atom86.getTree());

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
                    // 277:24: -> ^( ALAP[$op] expr_atom )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:277:27: ^( ALAP[$op] expr_atom )
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:280:1: expr_atom : ( ANY | OTHER |open= LPAR expr close= RPAR -> ^( BLOCK[$open] expr TRUE[$close] ) | call );
    public final CtrlParser.expr_atom_return expr_atom() throws RecognitionException {
        CtrlParser.expr_atom_return retval = new CtrlParser.expr_atom_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        Token ANY87=null;
        Token OTHER88=null;
        CtrlParser.expr_return expr89 =null;

        CtrlParser.call_return call90 =null;


        CtrlTree open_tree=null;
        CtrlTree close_tree=null;
        CtrlTree ANY87_tree=null;
        CtrlTree OTHER88_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:281:2: ( ANY | OTHER |open= LPAR expr close= RPAR -> ^( BLOCK[$open] expr TRUE[$close] ) | call )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:283:4: ANY
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    ANY87=(Token)match(input,ANY,FOLLOW_ANY_in_expr_atom1487); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ANY87_tree = 
                    (CtrlTree)adaptor.create(ANY87)
                    ;
                    adaptor.addChild(root_0, ANY87_tree);
                    }

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:287:4: OTHER
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    OTHER88=(Token)match(input,OTHER,FOLLOW_OTHER_in_expr_atom1504); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    OTHER88_tree = 
                    (CtrlTree)adaptor.create(OTHER88)
                    ;
                    adaptor.addChild(root_0, OTHER88_tree);
                    }

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:290:4: open= LPAR expr close= RPAR
                    {
                    open=(Token)match(input,LPAR,FOLLOW_LPAR_in_expr_atom1519); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAR.add(open);


                    pushFollow(FOLLOW_expr_in_expr_atom1521);
                    expr89=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr.add(expr89.getTree());

                    close=(Token)match(input,RPAR,FOLLOW_RPAR_in_expr_atom1525); if (state.failed) return retval; 
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
                    // 291:4: -> ^( BLOCK[$open] expr TRUE[$close] )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:291:7: ^( BLOCK[$open] expr TRUE[$close] )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:294:4: call
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_expr_atom1553);
                    call90=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call90.getTree());

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:298:1: call : rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) ;
    public final CtrlParser.call_return call() throws RecognitionException {
        CtrlParser.call_return retval = new CtrlParser.call_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlParser.rule_name_return rule_name91 =null;

        CtrlParser.arg_list_return arg_list92 =null;


        RewriteRuleSubtreeStream stream_arg_list=new RewriteRuleSubtreeStream(adaptor,"rule arg_list");
        RewriteRuleSubtreeStream stream_rule_name=new RewriteRuleSubtreeStream(adaptor,"rule rule_name");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:299:2: ( rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:303:4: rule_name ( arg_list )?
            {
            pushFollow(FOLLOW_rule_name_in_call1583);
            rule_name91=rule_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_name.add(rule_name91.getTree());

            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:303:14: ( arg_list )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==LPAR) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:303:14: arg_list
                    {
                    pushFollow(FOLLOW_arg_list_in_call1585);
                    arg_list92=arg_list();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg_list.add(arg_list92.getTree());

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
            // 304:4: -> ^( CALL[$rule_name.start] rule_name ( arg_list )? )
            {
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:304:7: ^( CALL[$rule_name.start] rule_name ( arg_list )? )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(CALL, (rule_name91!=null?((Token)rule_name91.start):null))
                , root_1);

                adaptor.addChild(root_1, stream_rule_name.nextTree());

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:304:42: ( arg_list )?
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:310:1: arg_list : open= LPAR ( arg ( COMMA arg )* )? close= RPAR -> ^( ARGS[$open] ( arg )* RPAR[$close] ) ;
    public final CtrlParser.arg_list_return arg_list() throws RecognitionException {
        CtrlParser.arg_list_return retval = new CtrlParser.arg_list_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        Token COMMA94=null;
        CtrlParser.arg_return arg93 =null;

        CtrlParser.arg_return arg95 =null;


        CtrlTree open_tree=null;
        CtrlTree close_tree=null;
        CtrlTree COMMA94_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_arg=new RewriteRuleSubtreeStream(adaptor,"rule arg");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:311:3: (open= LPAR ( arg ( COMMA arg )* )? close= RPAR -> ^( ARGS[$open] ( arg )* RPAR[$close] ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:313:5: open= LPAR ( arg ( COMMA arg )* )? close= RPAR
            {
            open=(Token)match(input,LPAR,FOLLOW_LPAR_in_arg_list1627); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAR.add(open);


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:313:15: ( arg ( COMMA arg )* )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==DONT_CARE||LA26_0==FALSE||LA26_0==ID||LA26_0==INT_LIT||LA26_0==OUT||LA26_0==REAL_LIT||(LA26_0 >= STRING_LIT && LA26_0 <= TRUE)) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:313:16: arg ( COMMA arg )*
                    {
                    pushFollow(FOLLOW_arg_in_arg_list1630);
                    arg93=arg();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg.add(arg93.getTree());

                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:313:20: ( COMMA arg )*
                    loop25:
                    do {
                        int alt25=2;
                        int LA25_0 = input.LA(1);

                        if ( (LA25_0==COMMA) ) {
                            alt25=1;
                        }


                        switch (alt25) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:313:21: COMMA arg
                    	    {
                    	    COMMA94=(Token)match(input,COMMA,FOLLOW_COMMA_in_arg_list1633); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA94);


                    	    pushFollow(FOLLOW_arg_in_arg_list1635);
                    	    arg95=arg();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_arg.add(arg95.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop25;
                        }
                    } while (true);


                    }
                    break;

            }


            close=(Token)match(input,RPAR,FOLLOW_RPAR_in_arg_list1643); if (state.failed) return retval; 
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
            // 314:5: -> ^( ARGS[$open] ( arg )* RPAR[$close] )
            {
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:314:8: ^( ARGS[$open] ( arg )* RPAR[$close] )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(ARGS, open)
                , root_1);

                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:314:22: ( arg )*
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:320:1: arg : ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) );
    public final CtrlParser.arg_return arg() throws RecognitionException {
        CtrlParser.arg_return retval = new CtrlParser.arg_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token OUT96=null;
        Token ID97=null;
        Token ID98=null;
        Token DONT_CARE99=null;
        CtrlParser.literal_return literal100 =null;


        CtrlTree OUT96_tree=null;
        CtrlTree ID97_tree=null;
        CtrlTree ID98_tree=null;
        CtrlTree DONT_CARE99_tree=null;
        RewriteRuleTokenStream stream_DONT_CARE=new RewriteRuleTokenStream(adaptor,"token DONT_CARE");
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:321:3: ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:324:5: OUT ID
                    {
                    OUT96=(Token)match(input,OUT,FOLLOW_OUT_in_arg1690); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT96);


                    ID97=(Token)match(input,ID,FOLLOW_ID_in_arg1692); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID97);


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
                    // 324:12: -> ^( ARG OUT ID )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:324:15: ^( ARG OUT ID )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:328:5: ID
                    {
                    ID98=(Token)match(input,ID,FOLLOW_ID_in_arg1723); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID98);


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
                    // 328:8: -> ^( ARG ID )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:328:11: ^( ARG ID )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:332:5: DONT_CARE
                    {
                    DONT_CARE99=(Token)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg1752); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DONT_CARE.add(DONT_CARE99);


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
                    // 332:15: -> ^( ARG DONT_CARE )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:332:18: ^( ARG DONT_CARE )
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
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:333:5: literal
                    {
                    pushFollow(FOLLOW_literal_in_arg1769);
                    literal100=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_literal.add(literal100.getTree());

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
                    // 333:13: -> ^( ARG literal )
                    {
                        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:333:16: ^( ARG literal )
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:336:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlParser.literal_return literal() throws RecognitionException {
        CtrlParser.literal_return retval = new CtrlParser.literal_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token set101=null;

        CtrlTree set101_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:337:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
            {
            root_0 = (CtrlTree)adaptor.nil();


            set101=(Token)input.LT(1);

            if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CtrlTree)adaptor.create(set101)
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:358:1: rule_name : qual_name ->;
    public final CtrlParser.rule_name_return rule_name() throws RecognitionException {
        CtrlParser.rule_name_return retval = new CtrlParser.rule_name_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlParser.qual_name_return qual_name102 =null;


        RewriteRuleSubtreeStream stream_qual_name=new RewriteRuleSubtreeStream(adaptor,"rule qual_name");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:359:3: ( qual_name ->)
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:359:5: qual_name
            {
            pushFollow(FOLLOW_qual_name_in_rule_name1879);
            qual_name102=qual_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qual_name.add(qual_name102.getTree());

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
            // 360:5: ->
            {
                adaptor.addChild(root_0,  helper.lookup((qual_name102!=null?((CtrlTree)qual_name102.tree):null)) );

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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:364:1: var_decl : var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) ;
    public final CtrlParser.var_decl_return var_decl() throws RecognitionException {
        CtrlParser.var_decl_return retval = new CtrlParser.var_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token ID104=null;
        Token COMMA105=null;
        Token ID106=null;
        CtrlParser.var_type_return var_type103 =null;


        CtrlTree ID104_tree=null;
        CtrlTree COMMA105_tree=null;
        CtrlTree ID106_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:365:2: ( var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:367:4: var_type ID ( COMMA ID )*
            {
            pushFollow(FOLLOW_var_type_in_var_decl1909);
            var_type103=var_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_var_type.add(var_type103.getTree());

            ID104=(Token)match(input,ID,FOLLOW_ID_in_var_decl1911); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID104);


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:367:16: ( COMMA ID )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0==COMMA) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:367:17: COMMA ID
            	    {
            	    COMMA105=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_decl1914); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA105);


            	    ID106=(Token)match(input,ID,FOLLOW_ID_in_var_decl1916); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ID106);


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
            // 367:28: -> ^( VAR var_type ( ID )+ )
            {
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:367:31: ^( VAR var_type ( ID )+ )
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
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:371:1: var_type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlParser.var_type_return var_type() throws RecognitionException {
        CtrlParser.var_type_return retval = new CtrlParser.var_type_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token set107=null;

        CtrlTree set107_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:372:2: ( NODE | BOOL | STRING | INT | REAL )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
            {
            root_0 = (CtrlTree)adaptor.nil();


            set107=(Token)input.LT(1);

            if ( input.LA(1)==BOOL||input.LA(1)==INT||input.LA(1)==NODE||input.LA(1)==REAL||input.LA(1)==STRING ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CtrlTree)adaptor.create(set107)
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
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:212:33: ( ELSE )
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:212:34: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred1_Ctrl1023); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred1_Ctrl

    // $ANTLR start synpred2_Ctrl
    public final void synpred2_Ctrl_fragment() throws RecognitionException {
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:216:17: ( ELSE )
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:216:18: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred2_Ctrl1063); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred2_Ctrl

    // $ANTLR start synpred3_Ctrl
    public final void synpred3_Ctrl_fragment() throws RecognitionException {
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:219:20: ( OR )
        // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:219:21: OR
        {
        match(input,OR,FOLLOW_OR_in_synpred3_Ctrl1098); if (state.failed) return ;

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


 

    public static final BitSet FOLLOW_package_decl_in_program151 = new BitSet(new long[]{0x2450044CF4052450L,0x000000000000000BL});
    public static final BitSet FOLLOW_import_decl_in_program157 = new BitSet(new long[]{0x2450044CF4052450L,0x000000000000000BL});
    public static final BitSet FOLLOW_function_in_program165 = new BitSet(new long[]{0x2450044CB4052450L,0x000000000000000BL});
    public static final BitSet FOLLOW_recipe_in_program167 = new BitSet(new long[]{0x2450044CB4052450L,0x000000000000000BL});
    public static final BitSet FOLLOW_stat_in_program169 = new BitSet(new long[]{0x2450044CB4052450L,0x000000000000000BL});
    public static final BitSet FOLLOW_EOF_in_program175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_package_decl311 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_qual_name_in_package_decl313 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_SEMI_in_package_decl317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_decl384 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_qual_name_in_import_decl387 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_SEMI_in_import_decl389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_qual_name412 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_DOT_in_qual_name415 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_qual_name419 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_RECIPE_in_recipe459 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_recipe462 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_par_list_in_recipe464 = new BitSet(new long[]{0x0001000400000000L});
    public static final BitSet FOLLOW_PRIORITY_in_recipe467 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_INT_LIT_in_recipe470 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_block_in_recipe474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function510 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_function513 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_par_list_in_function515 = new BitSet(new long[]{0x0001000400000000L});
    public static final BitSet FOLLOW_PRIORITY_in_function518 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_INT_LIT_in_function521 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_block_in_function525 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_par_list556 = new BitSet(new long[]{0x2110084080002000L});
    public static final BitSet FOLLOW_par_in_par_list559 = new BitSet(new long[]{0x0100000000020000L});
    public static final BitSet FOLLOW_COMMA_in_par_list562 = new BitSet(new long[]{0x2010084080002000L});
    public static final BitSet FOLLOW_par_in_par_list564 = new BitSet(new long[]{0x0100000000020000L});
    public static final BitSet FOLLOW_RPAR_in_par_list570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_par615 = new BitSet(new long[]{0x2010004080002000L});
    public static final BitSet FOLLOW_var_type_in_par617 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_par619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_type_in_par652 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_par654 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LCURLY_in_block693 = new BitSet(new long[]{0x2418044CB0052450L,0x000000000000000BL});
    public static final BitSet FOLLOW_stat_in_block695 = new BitSet(new long[]{0x2418044CB0052450L,0x000000000000000BL});
    public static final BitSet FOLLOW_RCURLY_in_block700 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_stat732 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat749 = new BitSet(new long[]{0x2410044CB0052450L,0x000000000000000BL});
    public static final BitSet FOLLOW_stat_in_stat752 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATOM_in_stat769 = new BitSet(new long[]{0x2410044CB0052450L,0x000000000000000BL});
    public static final BitSet FOLLOW_stat_in_stat772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_stat793 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_LPAR_in_stat796 = new BitSet(new long[]{0x8000000010000000L});
    public static final BitSet FOLLOW_cond_in_stat799 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat801 = new BitSet(new long[]{0x2410044CB0052450L,0x000000000000000BL});
    public static final BitSet FOLLOW_stat_in_stat804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat824 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_LPAR_in_stat827 = new BitSet(new long[]{0x8000000010000000L});
    public static final BitSet FOLLOW_cond_in_stat830 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat832 = new BitSet(new long[]{0x2410044CB0052450L,0x000000000000000BL});
    public static final BitSet FOLLOW_stat_in_stat835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_stat840 = new BitSet(new long[]{0x2410044CB0052450L,0x000000000000000BL});
    public static final BitSet FOLLOW_stat_in_stat842 = new BitSet(new long[]{0x0000000000000000L,0x000000000000000AL});
    public static final BitSet FOLLOW_WHILE_in_stat885 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_LPAR_in_stat887 = new BitSet(new long[]{0x8000000010000000L});
    public static final BitSet FOLLOW_cond_in_stat889 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat891 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat954 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_LPAR_in_stat956 = new BitSet(new long[]{0x8000000010000000L});
    public static final BitSet FOLLOW_cond_in_stat958 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat960 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_stat1007 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_LPAR_in_stat1010 = new BitSet(new long[]{0x8000000010000000L});
    public static final BitSet FOLLOW_cond_in_stat1013 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat1015 = new BitSet(new long[]{0x2410044CB0052450L,0x000000000000000BL});
    public static final BitSet FOLLOW_stat_in_stat1018 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_ELSE_in_stat1028 = new BitSet(new long[]{0x2410044CB0052450L,0x000000000000000BL});
    public static final BitSet FOLLOW_stat_in_stat1031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_stat1055 = new BitSet(new long[]{0x2410044CB0052450L,0x000000000000000BL});
    public static final BitSet FOLLOW_stat_in_stat1058 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_ELSE_in_stat1068 = new BitSet(new long[]{0x2410044CB0052450L,0x000000000000000BL});
    public static final BitSet FOLLOW_stat_in_stat1071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_stat1090 = new BitSet(new long[]{0x2410044CB0052450L,0x000000000000000BL});
    public static final BitSet FOLLOW_stat_in_stat1093 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_OR_in_stat1103 = new BitSet(new long[]{0x2410044CB0052450L,0x000000000000000BL});
    public static final BitSet FOLLOW_stat_in_stat1106 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_expr_in_stat1121 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat1123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat1138 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat1140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cond_atom_in_cond1165 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_BAR_in_cond1174 = new BitSet(new long[]{0x8000000010000000L});
    public static final BitSet FOLLOW_cond_atom_in_cond1176 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_TRUE_in_cond_atom1222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_cond_atom1243 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr2_in_expr1273 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_BAR_in_expr1281 = new BitSet(new long[]{0x0400040810000040L});
    public static final BitSet FOLLOW_expr2_in_expr1283 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_expr_atom_in_expr21364 = new BitSet(new long[]{0x0000800000000202L});
    public static final BitSet FOLLOW_PLUS_in_expr21374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERISK_in_expr21401 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expr21456 = new BitSet(new long[]{0x0000040810000040L});
    public static final BitSet FOLLOW_expr_atom_in_expr21458 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expr_atom1487 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expr_atom1504 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_atom1519 = new BitSet(new long[]{0x0400040810000040L});
    public static final BitSet FOLLOW_expr_in_expr_atom1521 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_RPAR_in_expr_atom1525 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expr_atom1553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_name_in_call1583 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_arg_list_in_call1585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_arg_list1627 = new BitSet(new long[]{0xC120080112080000L});
    public static final BitSet FOLLOW_arg_in_arg_list1630 = new BitSet(new long[]{0x0100000000020000L});
    public static final BitSet FOLLOW_COMMA_in_arg_list1633 = new BitSet(new long[]{0xC020080112080000L});
    public static final BitSet FOLLOW_arg_in_arg_list1635 = new BitSet(new long[]{0x0100000000020000L});
    public static final BitSet FOLLOW_RPAR_in_arg_list1643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_arg1690 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_arg1692 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_arg1723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg1752 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_arg1769 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qual_name_in_rule_name1879 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_type_in_var_decl1909 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_var_decl1911 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_COMMA_in_var_decl1914 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_var_decl1916 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_ELSE_in_synpred1_Ctrl1023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred2_Ctrl1063 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_synpred3_Ctrl1098 = new BitSet(new long[]{0x0000000000000002L});

}