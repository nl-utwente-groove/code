// $ANTLR 3.4 E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g 2014-08-23 18:05:34

package groove.control.parse;
import groove.control.*;
import groove.util.antlr.*;
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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALAP", "AMP", "ANY", "ARG", "ARGS", "ASTERISK", "ATOM", "BAR", "BLOCK", "BOOL", "BQUOTE", "BSLASH", "CALL", "CHOICE", "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", "ELSE", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF", "IMPORT", "IMPORTS", "INT", "INT_LIT", "IntegerNumber", "LANGLE", "LCURLY", "LPAR", "MINUS", "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT", "PACKAGE", "PAR", "PARS", "PLUS", "PRIORITY", "PROGRAM", "QUOTE", "RANGLE", "RCURLY", "REAL", "REAL_LIT", "RECIPE", "RECIPES", "RPAR", "SEMI", "SHARP", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", "TRUE", "TRY", "UNTIL", "VAR", "WHILE", "WS"
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
    public static final int BQUOTE=14;
    public static final int BSLASH=15;
    public static final int CALL=16;
    public static final int CHOICE=17;
    public static final int COMMA=18;
    public static final int DO=19;
    public static final int DONT_CARE=20;
    public static final int DOT=21;
    public static final int DO_UNTIL=22;
    public static final int DO_WHILE=23;
    public static final int ELSE=24;
    public static final int EscapeSequence=25;
    public static final int FALSE=26;
    public static final int FUNCTION=27;
    public static final int FUNCTIONS=28;
    public static final int ID=29;
    public static final int IF=30;
    public static final int IMPORT=31;
    public static final int IMPORTS=32;
    public static final int INT=33;
    public static final int INT_LIT=34;
    public static final int IntegerNumber=35;
    public static final int LANGLE=36;
    public static final int LCURLY=37;
    public static final int LPAR=38;
    public static final int MINUS=39;
    public static final int ML_COMMENT=40;
    public static final int NODE=41;
    public static final int NOT=42;
    public static final int NonIntegerNumber=43;
    public static final int OR=44;
    public static final int OTHER=45;
    public static final int OUT=46;
    public static final int PACKAGE=47;
    public static final int PAR=48;
    public static final int PARS=49;
    public static final int PLUS=50;
    public static final int PRIORITY=51;
    public static final int PROGRAM=52;
    public static final int QUOTE=53;
    public static final int RANGLE=54;
    public static final int RCURLY=55;
    public static final int REAL=56;
    public static final int REAL_LIT=57;
    public static final int RECIPE=58;
    public static final int RECIPES=59;
    public static final int RPAR=60;
    public static final int SEMI=61;
    public static final int SHARP=62;
    public static final int SL_COMMENT=63;
    public static final int STAR=64;
    public static final int STRING=65;
    public static final int STRING_LIT=66;
    public static final int TRUE=67;
    public static final int TRY=68;
    public static final int UNTIL=69;
    public static final int VAR=70;
    public static final int WHILE=71;
    public static final int WS=72;

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
    public String getGrammarFileName() { return "E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g"; }


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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:74:1: program : package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) ) ;
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:77:3: ( package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:81:5: package_decl ( import_decl )* ( function | recipe | stat )* EOF
            {
            pushFollow(FOLLOW_package_decl_in_program166);
            package_decl1=package_decl();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_package_decl.add(package_decl1.getTree());

            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:82:5: ( import_decl )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==IMPORT) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:82:5: import_decl
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


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:83:5: ( function | recipe | stat )*
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
                case ASTERISK:
                case BOOL:
                case CHOICE:
                case DO:
                case ID:
                case IF:
                case INT:
                case LANGLE:
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
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:83:6: function
            	    {
            	    pushFollow(FOLLOW_function_in_program180);
            	    function3=function();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_function.add(function3.getTree());

            	    }
            	    break;
            	case 2 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:83:15: recipe
            	    {
            	    pushFollow(FOLLOW_recipe_in_program182);
            	    recipe4=recipe();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_recipe.add(recipe4.getTree());

            	    }
            	    break;
            	case 3 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:83:22: stat
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
            // elements: stat, function, package_decl, recipe, import_decl
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {

            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CtrlTree)adaptor.nil();
            // 85:5: -> ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) )
            {
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:85:8: ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(PROGRAM, "PROGRAM")
                , root_1);

                adaptor.addChild(root_1, stream_package_decl.nextTree());

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:87:11: ^( IMPORTS ( import_decl )* )
                {
                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                root_2 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(IMPORTS, "IMPORTS")
                , root_2);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:87:21: ( import_decl )*
                while ( stream_import_decl.hasNext() ) {
                    adaptor.addChild(root_2, stream_import_decl.nextTree());

                }
                stream_import_decl.reset();

                adaptor.addChild(root_1, root_2);
                }

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:88:11: ^( FUNCTIONS ( function )* )
                {
                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                root_2 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(FUNCTIONS, "FUNCTIONS")
                , root_2);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:88:23: ( function )*
                while ( stream_function.hasNext() ) {
                    adaptor.addChild(root_2, stream_function.nextTree());

                }
                stream_function.reset();

                adaptor.addChild(root_1, root_2);
                }

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:89:11: ^( RECIPES ( recipe )* )
                {
                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                root_2 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(RECIPES, "RECIPES")
                , root_2);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:89:21: ( recipe )*
                while ( stream_recipe.hasNext() ) {
                    adaptor.addChild(root_2, stream_recipe.nextTree());

                }
                stream_recipe.reset();

                adaptor.addChild(root_1, root_2);
                }

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:90:11: ^( BLOCK ( stat )* )
                {
                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                root_2 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                , root_2);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:90:19: ( stat )*
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:95:1: package_decl : (key= PACKAGE qual_name[false] close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->) ;
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:96:3: ( (key= PACKAGE qual_name[false] close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:98:5: (key= PACKAGE qual_name[false] close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->)
            {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:98:5: (key= PACKAGE qual_name[false] close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->)
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==PACKAGE) ) {
                alt3=1;
            }
            else if ( (LA3_0==EOF||LA3_0==ALAP||LA3_0==ANY||LA3_0==ASTERISK||LA3_0==BOOL||LA3_0==CHOICE||LA3_0==DO||LA3_0==FUNCTION||(LA3_0 >= ID && LA3_0 <= IMPORT)||LA3_0==INT||(LA3_0 >= LANGLE && LA3_0 <= LPAR)||LA3_0==NODE||LA3_0==OTHER||LA3_0==REAL||LA3_0==RECIPE||LA3_0==SHARP||LA3_0==STRING||(LA3_0 >= TRY && LA3_0 <= UNTIL)||LA3_0==WHILE) ) {
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:98:7: key= PACKAGE qual_name[false] close= SEMI
                    {
                    key=(Token)match(input,PACKAGE,FOLLOW_PACKAGE_in_package_decl325); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PACKAGE.add(key);


                    pushFollow(FOLLOW_qual_name_in_package_decl327);
                    qual_name7=qual_name(false);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_qual_name.add(qual_name7.getTree());

                    close=(Token)match(input,SEMI,FOLLOW_SEMI_in_package_decl332); if (state.failed) return retval; 
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
                    // 100:7: -> ^( PACKAGE[$key] qual_name SEMI[$close] )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:100:10: ^( PACKAGE[$key] qual_name SEMI[$close] )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:101:7: 
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
                    // 101:7: ->
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:106:1: import_decl : IMPORT ^ qual_name[false] SEMI ;
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:107:3: ( IMPORT ^ qual_name[false] SEMI )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:109:5: IMPORT ^ qual_name[false] SEMI
            {
            root_0 = (CtrlTree)adaptor.nil();


            IMPORT8=(Token)match(input,IMPORT,FOLLOW_IMPORT_in_import_decl399); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            IMPORT8_tree = 
            (CtrlTree)adaptor.create(IMPORT8)
            ;
            root_0 = (CtrlTree)adaptor.becomeRoot(IMPORT8_tree, root_0);
            }

            pushFollow(FOLLOW_qual_name_in_import_decl402);
            qual_name9=qual_name(false);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, qual_name9.getTree());

            SEMI10=(Token)match(input,SEMI,FOLLOW_SEMI_in_import_decl405); if (state.failed) return retval;
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:115:1: qual_name[boolean any] : ( ID ( DOT rest= qual_name[any] )? ->|{...}? ( ASTERISK DOT )? ( ANY ->| OTHER ->) );
    public final CtrlParser.qual_name_return qual_name(boolean any) throws RecognitionException {
        CtrlParser.qual_name_return retval = new CtrlParser.qual_name_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token ID11=null;
        Token DOT12=null;
        Token ASTERISK13=null;
        Token DOT14=null;
        Token ANY15=null;
        Token OTHER16=null;
        CtrlParser.qual_name_return rest =null;


        CtrlTree ID11_tree=null;
        CtrlTree DOT12_tree=null;
        CtrlTree ASTERISK13_tree=null;
        CtrlTree DOT14_tree=null;
        CtrlTree ANY15_tree=null;
        CtrlTree OTHER16_tree=null;
        RewriteRuleTokenStream stream_ANY=new RewriteRuleTokenStream(adaptor,"token ANY");
        RewriteRuleTokenStream stream_OTHER=new RewriteRuleTokenStream(adaptor,"token OTHER");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_ASTERISK=new RewriteRuleTokenStream(adaptor,"token ASTERISK");
        RewriteRuleSubtreeStream stream_qual_name=new RewriteRuleSubtreeStream(adaptor,"rule qual_name");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:116:3: ( ID ( DOT rest= qual_name[any] )? ->|{...}? ( ASTERISK DOT )? ( ANY ->| OTHER ->) )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==ID) ) {
                alt7=1;
            }
            else if ( (LA7_0==ANY||LA7_0==ASTERISK||LA7_0==OTHER) ) {
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:120:5: ID ( DOT rest= qual_name[any] )?
                    {
                    ID11=(Token)match(input,ID,FOLLOW_ID_in_qual_name447); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID11);


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:120:8: ( DOT rest= qual_name[any] )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==DOT) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:120:10: DOT rest= qual_name[any]
                            {
                            DOT12=(Token)match(input,DOT,FOLLOW_DOT_in_qual_name451); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_DOT.add(DOT12);


                            pushFollow(FOLLOW_qual_name_in_qual_name455);
                            rest=qual_name(any);

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_qual_name.add(rest.getTree());

                            }
                            break;

                    }


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
                    // 121:22: ->
                    {
                        adaptor.addChild(root_0,  helper.toQualName(ID11, (rest!=null?((CtrlTree)rest.tree):null)) );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:122:5: {...}? ( ASTERISK DOT )? ( ANY ->| OTHER ->)
                    {
                    if ( !(( any )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "qual_name", " any ");
                    }

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:122:14: ( ASTERISK DOT )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==ASTERISK) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:122:16: ASTERISK DOT
                            {
                            ASTERISK13=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_qual_name494); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ASTERISK.add(ASTERISK13);


                            DOT14=(Token)match(input,DOT,FOLLOW_DOT_in_qual_name496); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_DOT.add(DOT14);


                            }
                            break;

                    }


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:123:14: ( ANY ->| OTHER ->)
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==ANY) ) {
                        alt6=1;
                    }
                    else if ( (LA6_0==OTHER) ) {
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
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:123:16: ANY
                            {
                            ANY15=(Token)match(input,ANY,FOLLOW_ANY_in_qual_name516); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ANY.add(ANY15);


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
                            // 123:22: ->
                            {
                                adaptor.addChild(root_0,  helper.toQualName(ASTERISK13, ANY15) );

                            }


                            retval.tree = root_0;
                            }

                            }
                            break;
                        case 2 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:124:16: OTHER
                            {
                            OTHER16=(Token)match(input,OTHER,FOLLOW_OTHER_in_qual_name539); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_OTHER.add(OTHER16);


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
                            // 124:22: ->
                            {
                                adaptor.addChild(root_0,  helper.toQualName(ASTERISK13, OTHER16) );

                            }


                            retval.tree = root_0;
                            }

                            }
                            break;

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
    // $ANTLR end "qual_name"


    public static class recipe_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipe"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:131:1: recipe : RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block ;
    public final CtrlParser.recipe_return recipe() throws RecognitionException {
        CtrlParser.recipe_return retval = new CtrlParser.recipe_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token RECIPE17=null;
        Token ID18=null;
        Token PRIORITY20=null;
        Token INT_LIT21=null;
        CtrlParser.par_list_return par_list19 =null;

        CtrlParser.block_return block22 =null;


        CtrlTree RECIPE17_tree=null;
        CtrlTree ID18_tree=null;
        CtrlTree PRIORITY20_tree=null;
        CtrlTree INT_LIT21_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:132:3: ( RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:139:5: RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block
            {
            root_0 = (CtrlTree)adaptor.nil();


            RECIPE17=(Token)match(input,RECIPE,FOLLOW_RECIPE_in_recipe608); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RECIPE17_tree = 
            (CtrlTree)adaptor.create(RECIPE17)
            ;
            root_0 = (CtrlTree)adaptor.becomeRoot(RECIPE17_tree, root_0);
            }

            ID18=(Token)match(input,ID,FOLLOW_ID_in_recipe611); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID18_tree = 
            (CtrlTree)adaptor.create(ID18)
            ;
            adaptor.addChild(root_0, ID18_tree);
            }

            pushFollow(FOLLOW_par_list_in_recipe613);
            par_list19=par_list();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, par_list19.getTree());

            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:139:25: ( PRIORITY ! INT_LIT )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==PRIORITY) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:139:26: PRIORITY ! INT_LIT
                    {
                    PRIORITY20=(Token)match(input,PRIORITY,FOLLOW_PRIORITY_in_recipe616); if (state.failed) return retval;

                    INT_LIT21=(Token)match(input,INT_LIT,FOLLOW_INT_LIT_in_recipe619); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT_LIT21_tree = 
                    (CtrlTree)adaptor.create(INT_LIT21)
                    ;
                    adaptor.addChild(root_0, INT_LIT21_tree);
                    }

                    }
                    break;

            }


            if ( state.backtracking==0 ) { helper.setContext(RECIPE17_tree); }

            pushFollow(FOLLOW_block_in_recipe633);
            block22=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block22.getTree());

            if ( state.backtracking==0 ) { helper.resetContext();
                  helper.declareCtrlUnit(RECIPE17_tree);
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
    // $ANTLR end "recipe"


    public static class function_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:150:1: function : FUNCTION ^ ID par_list block ;
    public final CtrlParser.function_return function() throws RecognitionException {
        CtrlParser.function_return retval = new CtrlParser.function_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token FUNCTION23=null;
        Token ID24=null;
        CtrlParser.par_list_return par_list25 =null;

        CtrlParser.block_return block26 =null;


        CtrlTree FUNCTION23_tree=null;
        CtrlTree ID24_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:151:3: ( FUNCTION ^ ID par_list block )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:156:5: FUNCTION ^ ID par_list block
            {
            root_0 = (CtrlTree)adaptor.nil();


            FUNCTION23=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function679); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            FUNCTION23_tree = 
            (CtrlTree)adaptor.create(FUNCTION23)
            ;
            root_0 = (CtrlTree)adaptor.becomeRoot(FUNCTION23_tree, root_0);
            }

            ID24=(Token)match(input,ID,FOLLOW_ID_in_function682); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID24_tree = 
            (CtrlTree)adaptor.create(ID24)
            ;
            adaptor.addChild(root_0, ID24_tree);
            }

            pushFollow(FOLLOW_par_list_in_function684);
            par_list25=par_list();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, par_list25.getTree());

            if ( state.backtracking==0 ) { helper.setContext(FUNCTION23_tree); }

            pushFollow(FOLLOW_block_in_function697);
            block26=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block26.getTree());

            if ( state.backtracking==0 ) { helper.resetContext();
                  helper.declareCtrlUnit(FUNCTION23_tree);
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
    // $ANTLR end "function"


    public static class par_list_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "par_list"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:167:1: par_list : LPAR ( par ( COMMA par )* )? RPAR -> ^( PARS ( par )* ) ;
    public final CtrlParser.par_list_return par_list() throws RecognitionException {
        CtrlParser.par_list_return retval = new CtrlParser.par_list_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token LPAR27=null;
        Token COMMA29=null;
        Token RPAR31=null;
        CtrlParser.par_return par28 =null;

        CtrlParser.par_return par30 =null;


        CtrlTree LPAR27_tree=null;
        CtrlTree COMMA29_tree=null;
        CtrlTree RPAR31_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_par=new RewriteRuleSubtreeStream(adaptor,"rule par");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:168:3: ( LPAR ( par ( COMMA par )* )? RPAR -> ^( PARS ( par )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:5: LPAR ( par ( COMMA par )* )? RPAR
            {
            LPAR27=(Token)match(input,LPAR,FOLLOW_LPAR_in_par_list728); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAR.add(LPAR27);


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:10: ( par ( COMMA par )* )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==BOOL||LA10_0==INT||LA10_0==NODE||LA10_0==OUT||LA10_0==REAL||LA10_0==STRING) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:11: par ( COMMA par )*
                    {
                    pushFollow(FOLLOW_par_in_par_list731);
                    par28=par();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_par.add(par28.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:15: ( COMMA par )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0==COMMA) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:170:16: COMMA par
                    	    {
                    	    COMMA29=(Token)match(input,COMMA,FOLLOW_COMMA_in_par_list734); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA29);


                    	    pushFollow(FOLLOW_par_in_par_list736);
                    	    par30=par();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_par.add(par30.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);


                    }
                    break;

            }


            RPAR31=(Token)match(input,RPAR,FOLLOW_RPAR_in_par_list742); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAR.add(RPAR31);


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
            // 171:5: -> ^( PARS ( par )* )
            {
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:171:8: ^( PARS ( par )* )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(PARS, "PARS")
                , root_1);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:171:15: ( par )*
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:177:1: par : ( OUT var_type ID -> ^( PAR OUT var_type ID ) | var_type ID -> ^( PAR var_type ID ) );
    public final CtrlParser.par_return par() throws RecognitionException {
        CtrlParser.par_return retval = new CtrlParser.par_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token OUT32=null;
        Token ID34=null;
        Token ID36=null;
        CtrlParser.var_type_return var_type33 =null;

        CtrlParser.var_type_return var_type35 =null;


        CtrlTree OUT32_tree=null;
        CtrlTree ID34_tree=null;
        CtrlTree ID36_tree=null;
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:178:3: ( OUT var_type ID -> ^( PAR OUT var_type ID ) | var_type ID -> ^( PAR var_type ID ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==OUT) ) {
                alt11=1;
            }
            else if ( (LA11_0==BOOL||LA11_0==INT||LA11_0==NODE||LA11_0==REAL||LA11_0==STRING) ) {
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:181:5: OUT var_type ID
                    {
                    OUT32=(Token)match(input,OUT,FOLLOW_OUT_in_par787); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT32);


                    pushFollow(FOLLOW_var_type_in_par789);
                    var_type33=var_type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_var_type.add(var_type33.getTree());

                    ID34=(Token)match(input,ID,FOLLOW_ID_in_par791); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID34);


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
                    // 181:21: -> ^( PAR OUT var_type ID )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:181:24: ^( PAR OUT var_type ID )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:185:5: var_type ID
                    {
                    pushFollow(FOLLOW_var_type_in_par824);
                    var_type35=var_type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_var_type.add(var_type35.getTree());

                    ID36=(Token)match(input,ID,FOLLOW_ID_in_par826); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID36);


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
                    // 185:17: -> ^( PAR var_type ID )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:185:20: ^( PAR var_type ID )
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:189:1: block : open= LCURLY ( stat )* close= RCURLY -> ^( BLOCK[$open] ( stat )* TRUE[$close] ) ;
    public final CtrlParser.block_return block() throws RecognitionException {
        CtrlParser.block_return retval = new CtrlParser.block_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        CtrlParser.stat_return stat37 =null;


        CtrlTree open_tree=null;
        CtrlTree close_tree=null;
        RewriteRuleTokenStream stream_LCURLY=new RewriteRuleTokenStream(adaptor,"token LCURLY");
        RewriteRuleTokenStream stream_RCURLY=new RewriteRuleTokenStream(adaptor,"token RCURLY");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:190:3: (open= LCURLY ( stat )* close= RCURLY -> ^( BLOCK[$open] ( stat )* TRUE[$close] ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:192:5: open= LCURLY ( stat )* close= RCURLY
            {
            open=(Token)match(input,LCURLY,FOLLOW_LCURLY_in_block865); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LCURLY.add(open);


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:192:17: ( stat )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==ALAP||LA12_0==ANY||LA12_0==ASTERISK||LA12_0==BOOL||LA12_0==CHOICE||LA12_0==DO||(LA12_0 >= ID && LA12_0 <= IF)||LA12_0==INT||(LA12_0 >= LANGLE && LA12_0 <= LPAR)||LA12_0==NODE||LA12_0==OTHER||LA12_0==REAL||LA12_0==SHARP||LA12_0==STRING||(LA12_0 >= TRY && LA12_0 <= UNTIL)||LA12_0==WHILE) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:192:17: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_block867);
            	    stat37=stat();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_stat.add(stat37.getTree());

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            close=(Token)match(input,RCURLY,FOLLOW_RCURLY_in_block872); if (state.failed) return retval; 
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
            // 193:5: -> ^( BLOCK[$open] ( stat )* TRUE[$close] )
            {
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:193:8: ^( BLOCK[$open] ( stat )* TRUE[$close] )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(BLOCK, open)
                , root_1);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:193:23: ( stat )*
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:196:1: stat : ( block | ALAP ^ stat |open= LANGLE ( stat )* close= RANGLE -> ^( ATOM[$open] ^( BLOCK ( stat )* TRUE[$close] ) ) | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI ^| var_decl SEMI ^);
    public final CtrlParser.stat_return stat() throws RecognitionException {
        CtrlParser.stat_return retval = new CtrlParser.stat_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        Token ALAP39=null;
        Token WHILE42=null;
        Token LPAR43=null;
        Token RPAR45=null;
        Token UNTIL47=null;
        Token LPAR48=null;
        Token RPAR50=null;
        Token DO52=null;
        Token WHILE54=null;
        Token LPAR55=null;
        Token RPAR57=null;
        Token UNTIL58=null;
        Token LPAR59=null;
        Token RPAR61=null;
        Token IF62=null;
        Token LPAR63=null;
        Token RPAR65=null;
        Token ELSE67=null;
        Token TRY69=null;
        Token ELSE71=null;
        Token CHOICE73=null;
        Token OR75=null;
        Token SEMI78=null;
        Token SEMI80=null;
        CtrlParser.block_return block38 =null;

        CtrlParser.stat_return stat40 =null;

        CtrlParser.stat_return stat41 =null;

        CtrlParser.cond_return cond44 =null;

        CtrlParser.stat_return stat46 =null;

        CtrlParser.cond_return cond49 =null;

        CtrlParser.stat_return stat51 =null;

        CtrlParser.stat_return stat53 =null;

        CtrlParser.cond_return cond56 =null;

        CtrlParser.cond_return cond60 =null;

        CtrlParser.cond_return cond64 =null;

        CtrlParser.stat_return stat66 =null;

        CtrlParser.stat_return stat68 =null;

        CtrlParser.stat_return stat70 =null;

        CtrlParser.stat_return stat72 =null;

        CtrlParser.stat_return stat74 =null;

        CtrlParser.stat_return stat76 =null;

        CtrlParser.expr_return expr77 =null;

        CtrlParser.var_decl_return var_decl79 =null;


        CtrlTree open_tree=null;
        CtrlTree close_tree=null;
        CtrlTree ALAP39_tree=null;
        CtrlTree WHILE42_tree=null;
        CtrlTree LPAR43_tree=null;
        CtrlTree RPAR45_tree=null;
        CtrlTree UNTIL47_tree=null;
        CtrlTree LPAR48_tree=null;
        CtrlTree RPAR50_tree=null;
        CtrlTree DO52_tree=null;
        CtrlTree WHILE54_tree=null;
        CtrlTree LPAR55_tree=null;
        CtrlTree RPAR57_tree=null;
        CtrlTree UNTIL58_tree=null;
        CtrlTree LPAR59_tree=null;
        CtrlTree RPAR61_tree=null;
        CtrlTree IF62_tree=null;
        CtrlTree LPAR63_tree=null;
        CtrlTree RPAR65_tree=null;
        CtrlTree ELSE67_tree=null;
        CtrlTree TRY69_tree=null;
        CtrlTree ELSE71_tree=null;
        CtrlTree CHOICE73_tree=null;
        CtrlTree OR75_tree=null;
        CtrlTree SEMI78_tree=null;
        CtrlTree SEMI80_tree=null;
        RewriteRuleTokenStream stream_DO=new RewriteRuleTokenStream(adaptor,"token DO");
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_WHILE=new RewriteRuleTokenStream(adaptor,"token WHILE");
        RewriteRuleTokenStream stream_RANGLE=new RewriteRuleTokenStream(adaptor,"token RANGLE");
        RewriteRuleTokenStream stream_UNTIL=new RewriteRuleTokenStream(adaptor,"token UNTIL");
        RewriteRuleTokenStream stream_LANGLE=new RewriteRuleTokenStream(adaptor,"token LANGLE");
        RewriteRuleSubtreeStream stream_cond=new RewriteRuleSubtreeStream(adaptor,"rule cond");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:197:2: ( block | ALAP ^ stat |open= LANGLE ( stat )* close= RANGLE -> ^( ATOM[$open] ^( BLOCK ( stat )* TRUE[$close] ) ) | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI ^| var_decl SEMI ^)
            int alt18=11;
            switch ( input.LA(1) ) {
            case LCURLY:
                {
                alt18=1;
                }
                break;
            case ALAP:
                {
                alt18=2;
                }
                break;
            case LANGLE:
                {
                alt18=3;
                }
                break;
            case WHILE:
                {
                alt18=4;
                }
                break;
            case UNTIL:
                {
                alt18=5;
                }
                break;
            case DO:
                {
                alt18=6;
                }
                break;
            case IF:
                {
                alt18=7;
                }
                break;
            case TRY:
                {
                alt18=8;
                }
                break;
            case CHOICE:
                {
                alt18=9;
                }
                break;
            case ANY:
            case ASTERISK:
            case ID:
            case LPAR:
            case OTHER:
            case SHARP:
                {
                alt18=10;
                }
                break;
            case BOOL:
            case INT:
            case NODE:
            case REAL:
            case STRING:
                {
                alt18=11;
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:198:4: block
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_block_in_stat904);
                    block38=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block38.getTree());

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:202:4: ALAP ^ stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    ALAP39=(Token)match(input,ALAP,FOLLOW_ALAP_in_stat921); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ALAP39_tree = 
                    (CtrlTree)adaptor.create(ALAP39)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(ALAP39_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat924);
                    stat40=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat40.getTree());

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:207:4: open= LANGLE ( stat )* close= RANGLE
                    {
                    open=(Token)match(input,LANGLE,FOLLOW_LANGLE_in_stat947); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LANGLE.add(open);


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:207:16: ( stat )*
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( (LA13_0==ALAP||LA13_0==ANY||LA13_0==ASTERISK||LA13_0==BOOL||LA13_0==CHOICE||LA13_0==DO||(LA13_0 >= ID && LA13_0 <= IF)||LA13_0==INT||(LA13_0 >= LANGLE && LA13_0 <= LPAR)||LA13_0==NODE||LA13_0==OTHER||LA13_0==REAL||LA13_0==SHARP||LA13_0==STRING||(LA13_0 >= TRY && LA13_0 <= UNTIL)||LA13_0==WHILE) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:207:16: stat
                    	    {
                    	    pushFollow(FOLLOW_stat_in_stat949);
                    	    stat41=stat();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_stat.add(stat41.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop13;
                        }
                    } while (true);


                    close=(Token)match(input,RANGLE,FOLLOW_RANGLE_in_stat954); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RANGLE.add(close);


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
                    // 208:4: -> ^( ATOM[$open] ^( BLOCK ( stat )* TRUE[$close] ) )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:208:7: ^( ATOM[$open] ^( BLOCK ( stat )* TRUE[$close] ) )
                        {
                        CtrlTree root_1 = (CtrlTree)adaptor.nil();
                        root_1 = (CtrlTree)adaptor.becomeRoot(
                        (CtrlTree)adaptor.create(ATOM, open)
                        , root_1);

                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:208:21: ^( BLOCK ( stat )* TRUE[$close] )
                        {
                        CtrlTree root_2 = (CtrlTree)adaptor.nil();
                        root_2 = (CtrlTree)adaptor.becomeRoot(
                        (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                        , root_2);

                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:208:29: ( stat )*
                        while ( stream_stat.hasNext() ) {
                            adaptor.addChild(root_2, stream_stat.nextTree());

                        }
                        stream_stat.reset();

                        adaptor.addChild(root_2, 
                        (CtrlTree)adaptor.create(TRUE, close)
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:213:4: WHILE ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    WHILE42=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat995); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    WHILE42_tree = 
                    (CtrlTree)adaptor.create(WHILE42)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(WHILE42_tree, root_0);
                    }

                    LPAR43=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat998); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat1001);
                    cond44=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond44.getTree());

                    RPAR45=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1003); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat1006);
                    stat46=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat46.getTree());

                    }
                    break;
                case 5 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:217:5: UNTIL ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    UNTIL47=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat1026); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    UNTIL47_tree = 
                    (CtrlTree)adaptor.create(UNTIL47)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(UNTIL47_tree, root_0);
                    }

                    LPAR48=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1029); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat1032);
                    cond49=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond49.getTree());

                    RPAR50=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1034); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat1037);
                    stat51=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat51.getTree());

                    }
                    break;
                case 6 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:218:4: DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                    {
                    DO52=(Token)match(input,DO,FOLLOW_DO_in_stat1042); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DO.add(DO52);


                    pushFollow(FOLLOW_stat_in_stat1044);
                    stat53=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_stat.add(stat53.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:219:4: ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==WHILE) ) {
                        alt14=1;
                    }
                    else if ( (LA14_0==UNTIL) ) {
                        alt14=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 14, 0, input);

                        throw nvae;

                    }
                    switch (alt14) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:224:7: WHILE LPAR cond RPAR
                            {
                            WHILE54=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat1087); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_WHILE.add(WHILE54);


                            LPAR55=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1089); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR55);


                            pushFollow(FOLLOW_cond_in_stat1091);
                            cond56=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond56.getTree());

                            RPAR57=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1093); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR57);


                            // AST REWRITE
                            // elements: stat, stat, WHILE, cond
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {

                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (CtrlTree)adaptor.nil();
                            // 224:28: -> ^( BLOCK stat ^( WHILE cond stat ) )
                            {
                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:224:31: ^( BLOCK stat ^( WHILE cond stat ) )
                                {
                                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                                root_1 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:224:44: ^( WHILE cond stat )
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
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:231:5: UNTIL LPAR cond RPAR
                            {
                            UNTIL58=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat1156); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_UNTIL.add(UNTIL58);


                            LPAR59=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1158); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR59);


                            pushFollow(FOLLOW_cond_in_stat1160);
                            cond60=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond60.getTree());

                            RPAR61=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1162); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR61);


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

                            root_0 = (CtrlTree)adaptor.nil();
                            // 231:26: -> ^( BLOCK stat ^( UNTIL cond stat ) )
                            {
                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:231:29: ^( BLOCK stat ^( UNTIL cond stat ) )
                                {
                                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                                root_1 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:231:42: ^( UNTIL cond stat )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:237:5: IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    IF62=(Token)match(input,IF,FOLLOW_IF_in_stat1209); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IF62_tree = 
                    (CtrlTree)adaptor.create(IF62)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(IF62_tree, root_0);
                    }

                    LPAR63=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1212); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat1215);
                    cond64=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond64.getTree());

                    RPAR65=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1217); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat1220);
                    stat66=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat66.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:237:31: ( ( ELSE )=> ELSE ! stat )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==ELSE) ) {
                        int LA15_1 = input.LA(2);

                        if ( (synpred1_Ctrl()) ) {
                            alt15=1;
                        }
                    }
                    switch (alt15) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:237:33: ( ELSE )=> ELSE ! stat
                            {
                            ELSE67=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat1230); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat1233);
                            stat68=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat68.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:241:5: TRY ^ stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    TRY69=(Token)match(input,TRY,FOLLOW_TRY_in_stat1257); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRY69_tree = 
                    (CtrlTree)adaptor.create(TRY69)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(TRY69_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat1260);
                    stat70=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat70.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:241:15: ( ( ELSE )=> ELSE ! stat )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==ELSE) ) {
                        int LA16_1 = input.LA(2);

                        if ( (synpred2_Ctrl()) ) {
                            alt16=1;
                        }
                    }
                    switch (alt16) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:241:17: ( ELSE )=> ELSE ! stat
                            {
                            ELSE71=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat1270); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat1273);
                            stat72=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat72.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 9 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:244:5: CHOICE ^ stat ( ( OR )=> OR ! stat )+
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    CHOICE73=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_stat1292); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHOICE73_tree = 
                    (CtrlTree)adaptor.create(CHOICE73)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(CHOICE73_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat1295);
                    stat74=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat74.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:244:18: ( ( OR )=> OR ! stat )+
                    int cnt17=0;
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( (LA17_0==OR) ) {
                            int LA17_23 = input.LA(2);

                            if ( (synpred3_Ctrl()) ) {
                                alt17=1;
                            }


                        }


                        switch (alt17) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:244:20: ( OR )=> OR ! stat
                    	    {
                    	    OR75=(Token)match(input,OR,FOLLOW_OR_in_stat1305); if (state.failed) return retval;

                    	    pushFollow(FOLLOW_stat_in_stat1308);
                    	    stat76=stat();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat76.getTree());

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


                    }
                    break;
                case 10 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:247:4: expr SEMI ^
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_expr_in_stat1323);
                    expr77=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr77.getTree());

                    SEMI78=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat1325); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SEMI78_tree = 
                    (CtrlTree)adaptor.create(SEMI78)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(SEMI78_tree, root_0);
                    }

                    }
                    break;
                case 11 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:250:4: var_decl SEMI ^
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_var_decl_in_stat1340);
                    var_decl79=var_decl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, var_decl79.getTree());

                    SEMI80=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat1342); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SEMI80_tree = 
                    (CtrlTree)adaptor.create(SEMI80)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(SEMI80_tree, root_0);
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:254:1: cond : cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) ;
    public final CtrlParser.cond_return cond() throws RecognitionException {
        CtrlParser.cond_return retval = new CtrlParser.cond_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token BAR82=null;
        CtrlParser.cond_atom_return cond_atom81 =null;

        CtrlParser.cond_atom_return cond_atom83 =null;


        CtrlTree BAR82_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_cond_atom=new RewriteRuleSubtreeStream(adaptor,"rule cond_atom");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:255:2: ( cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:257:4: cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            {
            pushFollow(FOLLOW_cond_atom_in_cond1367);
            cond_atom81=cond_atom();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom81.getTree());

            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:258:4: ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==BAR) ) {
                alt20=1;
            }
            else if ( (LA20_0==RPAR) ) {
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:258:6: ( BAR cond_atom )+
                    {
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:258:6: ( BAR cond_atom )+
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
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:258:7: BAR cond_atom
                    	    {
                    	    BAR82=(Token)match(input,BAR,FOLLOW_BAR_in_cond1376); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR82);


                    	    pushFollow(FOLLOW_cond_atom_in_cond1378);
                    	    cond_atom83=cond_atom();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom83.getTree());

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
                    // 258:23: -> ^( CHOICE cond_atom ( cond_atom )+ )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:258:26: ^( CHOICE cond_atom ( cond_atom )+ )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:259:6: 
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
                    // 259:6: -> cond_atom
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:263:1: cond_atom : ( TRUE | call );
    public final CtrlParser.cond_atom_return cond_atom() throws RecognitionException {
        CtrlParser.cond_atom_return retval = new CtrlParser.cond_atom_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token TRUE84=null;
        CtrlParser.call_return call85 =null;


        CtrlTree TRUE84_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:264:2: ( TRUE | call )
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==TRUE) ) {
                alt21=1;
            }
            else if ( (LA21_0==ANY||LA21_0==ASTERISK||LA21_0==ID||LA21_0==OTHER) ) {
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:266:4: TRUE
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    TRUE84=(Token)match(input,TRUE,FOLLOW_TRUE_in_cond_atom1424); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRUE84_tree = 
                    (CtrlTree)adaptor.create(TRUE84)
                    ;
                    adaptor.addChild(root_0, TRUE84_tree);
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:270:5: call
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_cond_atom1445);
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
    // $ANTLR end "cond_atom"


    public static class expr_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expr"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:273:1: expr : expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) ;
    public final CtrlParser.expr_return expr() throws RecognitionException {
        CtrlParser.expr_return retval = new CtrlParser.expr_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token BAR87=null;
        CtrlParser.expr2_return expr286 =null;

        CtrlParser.expr2_return expr288 =null;


        CtrlTree BAR87_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_expr2=new RewriteRuleSubtreeStream(adaptor,"rule expr2");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:274:2: ( expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:278:4: expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            {
            pushFollow(FOLLOW_expr2_in_expr1475);
            expr286=expr2();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expr2.add(expr286.getTree());

            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:279:4: ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==BAR) ) {
                alt23=1;
            }
            else if ( ((LA23_0 >= RPAR && LA23_0 <= SEMI)) ) {
                alt23=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;

            }
            switch (alt23) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:279:6: ( BAR expr2 )+
                    {
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:279:6: ( BAR expr2 )+
                    int cnt22=0;
                    loop22:
                    do {
                        int alt22=2;
                        int LA22_0 = input.LA(1);

                        if ( (LA22_0==BAR) ) {
                            alt22=1;
                        }


                        switch (alt22) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:279:7: BAR expr2
                    	    {
                    	    BAR87=(Token)match(input,BAR,FOLLOW_BAR_in_expr1483); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR87);


                    	    pushFollow(FOLLOW_expr2_in_expr1485);
                    	    expr288=expr2();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expr2.add(expr288.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt22 >= 1 ) break loop22;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(22, input);
                                throw eee;
                        }
                        cnt22++;
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
                    // 279:19: -> ^( CHOICE expr2 ( expr2 )+ )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:279:22: ^( CHOICE expr2 ( expr2 )+ )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:280:6: 
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
                    // 280:6: -> expr2
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:284:1: expr2 : (e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e) |op= SHARP expr_atom -> ^( ALAP[$op] expr_atom ) );
    public final CtrlParser.expr2_return expr2() throws RecognitionException {
        CtrlParser.expr2_return retval = new CtrlParser.expr2_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token plus=null;
        Token ast=null;
        Token op=null;
        CtrlParser.expr_atom_return e =null;

        CtrlParser.expr_atom_return expr_atom89 =null;


        CtrlTree plus_tree=null;
        CtrlTree ast_tree=null;
        CtrlTree op_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_SHARP=new RewriteRuleTokenStream(adaptor,"token SHARP");
        RewriteRuleTokenStream stream_ASTERISK=new RewriteRuleTokenStream(adaptor,"token ASTERISK");
        RewriteRuleSubtreeStream stream_expr_atom=new RewriteRuleSubtreeStream(adaptor,"rule expr_atom");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:285:3: (e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e) |op= SHARP expr_atom -> ^( ALAP[$op] expr_atom ) )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==ANY||LA25_0==ASTERISK||LA25_0==ID||LA25_0==LPAR||LA25_0==OTHER) ) {
                alt25=1;
            }
            else if ( (LA25_0==SHARP) ) {
                alt25=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;

            }
            switch (alt25) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:293:5: e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e)
                    {
                    pushFollow(FOLLOW_expr_atom_in_expr21566);
                    e=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(e.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:294:5: (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e)
                    int alt24=3;
                    switch ( input.LA(1) ) {
                    case PLUS:
                        {
                        alt24=1;
                        }
                        break;
                    case ASTERISK:
                        {
                        alt24=2;
                        }
                        break;
                    case BAR:
                    case RPAR:
                    case SEMI:
                        {
                        alt24=3;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 24, 0, input);

                        throw nvae;

                    }

                    switch (alt24) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:294:7: plus= PLUS
                            {
                            plus=(Token)match(input,PLUS,FOLLOW_PLUS_in_expr21576); if (state.failed) return retval; 
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
                            // 294:17: -> ^( BLOCK $e ^( STAR[$plus] $e) )
                            {
                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:294:20: ^( BLOCK $e ^( STAR[$plus] $e) )
                                {
                                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                                root_1 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_e.nextTree());

                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:294:31: ^( STAR[$plus] $e)
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
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:295:7: ast= ASTERISK
                            {
                            ast=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_expr21603); if (state.failed) return retval; 
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
                            // 295:20: -> ^( STAR[$ast] $e)
                            {
                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:295:23: ^( STAR[$ast] $e)
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
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:296:7: 
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
                            // 296:7: -> $e
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:302:5: op= SHARP expr_atom
                    {
                    op=(Token)match(input,SHARP,FOLLOW_SHARP_in_expr21658); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SHARP.add(op);


                    pushFollow(FOLLOW_expr_atom_in_expr21660);
                    expr_atom89=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(expr_atom89.getTree());

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
                    // 302:24: -> ^( ALAP[$op] expr_atom )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:302:27: ^( ALAP[$op] expr_atom )
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:305:1: expr_atom : (open= LPAR expr close= RPAR -> ^( BLOCK[$open] expr TRUE[$close] ) | call );
    public final CtrlParser.expr_atom_return expr_atom() throws RecognitionException {
        CtrlParser.expr_atom_return retval = new CtrlParser.expr_atom_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        CtrlParser.expr_return expr90 =null;

        CtrlParser.call_return call91 =null;


        CtrlTree open_tree=null;
        CtrlTree close_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:306:2: (open= LPAR expr close= RPAR -> ^( BLOCK[$open] expr TRUE[$close] ) | call )
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==LPAR) ) {
                alt26=1;
            }
            else if ( (LA26_0==ANY||LA26_0==ASTERISK||LA26_0==ID||LA26_0==OTHER) ) {
                alt26=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;

            }
            switch (alt26) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:308:4: open= LPAR expr close= RPAR
                    {
                    open=(Token)match(input,LPAR,FOLLOW_LPAR_in_expr_atom1691); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAR.add(open);


                    pushFollow(FOLLOW_expr_in_expr_atom1693);
                    expr90=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr.add(expr90.getTree());

                    close=(Token)match(input,RPAR,FOLLOW_RPAR_in_expr_atom1697); if (state.failed) return retval; 
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
                    // 309:4: -> ^( BLOCK[$open] expr TRUE[$close] )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:309:7: ^( BLOCK[$open] expr TRUE[$close] )
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
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:312:4: call
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_expr_atom1725);
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:316:1: call : rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) ;
    public final CtrlParser.call_return call() throws RecognitionException {
        CtrlParser.call_return retval = new CtrlParser.call_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlParser.rule_name_return rule_name92 =null;

        CtrlParser.arg_list_return arg_list93 =null;


        RewriteRuleSubtreeStream stream_arg_list=new RewriteRuleSubtreeStream(adaptor,"rule arg_list");
        RewriteRuleSubtreeStream stream_rule_name=new RewriteRuleSubtreeStream(adaptor,"rule rule_name");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:317:2: ( rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:321:4: rule_name ( arg_list )?
            {
            pushFollow(FOLLOW_rule_name_in_call1755);
            rule_name92=rule_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_name.add(rule_name92.getTree());

            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:321:14: ( arg_list )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==LPAR) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:321:14: arg_list
                    {
                    pushFollow(FOLLOW_arg_list_in_call1757);
                    arg_list93=arg_list();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg_list.add(arg_list93.getTree());

                    }
                    break;

            }


            if ( state.backtracking==0 ) { helper.registerCall((rule_name92!=null?((CtrlTree)rule_name92.tree):null)); }

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
            // 323:4: -> ^( CALL[$rule_name.start] rule_name ( arg_list )? )
            {
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:323:7: ^( CALL[$rule_name.start] rule_name ( arg_list )? )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(CALL, (rule_name92!=null?((Token)rule_name92.start):null))
                , root_1);

                adaptor.addChild(root_1, stream_rule_name.nextTree());

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:323:42: ( arg_list )?
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


    public static class rule_name_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "rule_name"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:327:1: rule_name : qual_name[true] ->;
    public final CtrlParser.rule_name_return rule_name() throws RecognitionException {
        CtrlParser.rule_name_return retval = new CtrlParser.rule_name_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlParser.qual_name_return qual_name94 =null;


        RewriteRuleSubtreeStream stream_qual_name=new RewriteRuleSubtreeStream(adaptor,"rule qual_name");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:328:3: ( qual_name[true] ->)
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:338:5: qual_name[true]
            {
            pushFollow(FOLLOW_qual_name_in_rule_name1843);
            qual_name94=qual_name(true);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qual_name.add(qual_name94.getTree());

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
            // 339:5: ->
            {
                adaptor.addChild(root_0,  helper.qualify((qual_name94!=null?((CtrlTree)qual_name94.tree):null)) );

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


    public static class arg_list_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg_list"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:345:1: arg_list : open= LPAR ( arg ( COMMA arg )* )? close= RPAR -> ^( ARGS[$open] ( arg )* RPAR[$close] ) ;
    public final CtrlParser.arg_list_return arg_list() throws RecognitionException {
        CtrlParser.arg_list_return retval = new CtrlParser.arg_list_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        Token COMMA96=null;
        CtrlParser.arg_return arg95 =null;

        CtrlParser.arg_return arg97 =null;


        CtrlTree open_tree=null;
        CtrlTree close_tree=null;
        CtrlTree COMMA96_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_arg=new RewriteRuleSubtreeStream(adaptor,"rule arg");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:346:3: (open= LPAR ( arg ( COMMA arg )* )? close= RPAR -> ^( ARGS[$open] ( arg )* RPAR[$close] ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:348:5: open= LPAR ( arg ( COMMA arg )* )? close= RPAR
            {
            open=(Token)match(input,LPAR,FOLLOW_LPAR_in_arg_list1879); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAR.add(open);


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:348:15: ( arg ( COMMA arg )* )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==DONT_CARE||LA29_0==FALSE||LA29_0==ID||LA29_0==INT_LIT||LA29_0==OUT||LA29_0==REAL_LIT||(LA29_0 >= STRING_LIT && LA29_0 <= TRUE)) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:348:16: arg ( COMMA arg )*
                    {
                    pushFollow(FOLLOW_arg_in_arg_list1882);
                    arg95=arg();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg.add(arg95.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:348:20: ( COMMA arg )*
                    loop28:
                    do {
                        int alt28=2;
                        int LA28_0 = input.LA(1);

                        if ( (LA28_0==COMMA) ) {
                            alt28=1;
                        }


                        switch (alt28) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:348:21: COMMA arg
                    	    {
                    	    COMMA96=(Token)match(input,COMMA,FOLLOW_COMMA_in_arg_list1885); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA96);


                    	    pushFollow(FOLLOW_arg_in_arg_list1887);
                    	    arg97=arg();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_arg.add(arg97.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop28;
                        }
                    } while (true);


                    }
                    break;

            }


            close=(Token)match(input,RPAR,FOLLOW_RPAR_in_arg_list1895); if (state.failed) return retval; 
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
            // 349:5: -> ^( ARGS[$open] ( arg )* RPAR[$close] )
            {
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:349:8: ^( ARGS[$open] ( arg )* RPAR[$close] )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(ARGS, open)
                , root_1);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:349:22: ( arg )*
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:355:1: arg : ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) );
    public final CtrlParser.arg_return arg() throws RecognitionException {
        CtrlParser.arg_return retval = new CtrlParser.arg_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token OUT98=null;
        Token ID99=null;
        Token ID100=null;
        Token DONT_CARE101=null;
        CtrlParser.literal_return literal102 =null;


        CtrlTree OUT98_tree=null;
        CtrlTree ID99_tree=null;
        CtrlTree ID100_tree=null;
        CtrlTree DONT_CARE101_tree=null;
        RewriteRuleTokenStream stream_DONT_CARE=new RewriteRuleTokenStream(adaptor,"token DONT_CARE");
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_literal=new RewriteRuleSubtreeStream(adaptor,"rule literal");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:356:3: ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) )
            int alt30=4;
            switch ( input.LA(1) ) {
            case OUT:
                {
                alt30=1;
                }
                break;
            case ID:
                {
                alt30=2;
                }
                break;
            case DONT_CARE:
                {
                alt30=3;
                }
                break;
            case FALSE:
            case INT_LIT:
            case REAL_LIT:
            case STRING_LIT:
            case TRUE:
                {
                alt30=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;

            }

            switch (alt30) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:359:5: OUT ID
                    {
                    OUT98=(Token)match(input,OUT,FOLLOW_OUT_in_arg1942); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT98);


                    ID99=(Token)match(input,ID,FOLLOW_ID_in_arg1944); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID99);


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
                    // 359:12: -> ^( ARG OUT ID )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:359:15: ^( ARG OUT ID )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:363:5: ID
                    {
                    ID100=(Token)match(input,ID,FOLLOW_ID_in_arg1975); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID100);


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
                    // 363:8: -> ^( ARG ID )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:363:11: ^( ARG ID )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:367:5: DONT_CARE
                    {
                    DONT_CARE101=(Token)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg2004); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DONT_CARE.add(DONT_CARE101);


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
                    // 367:15: -> ^( ARG DONT_CARE )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:367:18: ^( ARG DONT_CARE )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:368:5: literal
                    {
                    pushFollow(FOLLOW_literal_in_arg2021);
                    literal102=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_literal.add(literal102.getTree());

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
                    // 368:13: -> ^( ARG literal )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:368:16: ^( ARG literal )
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:371:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlParser.literal_return literal() throws RecognitionException {
        CtrlParser.literal_return retval = new CtrlParser.literal_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token set103=null;

        CtrlTree set103_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:372:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
            {
            root_0 = (CtrlTree)adaptor.nil();


            set103=(Token)input.LT(1);

            if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, 
                (CtrlTree)adaptor.create(set103)
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


    public static class var_decl_return extends ParserRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_decl"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:390:1: var_decl : var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) ;
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:391:2: ( var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:393:4: var_type ID ( COMMA ID )*
            {
            pushFollow(FOLLOW_var_type_in_var_decl2138);
            var_type104=var_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_var_type.add(var_type104.getTree());

            ID105=(Token)match(input,ID,FOLLOW_ID_in_var_decl2140); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID105);


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:393:16: ( COMMA ID )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( (LA31_0==COMMA) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:393:17: COMMA ID
            	    {
            	    COMMA106=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_decl2143); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA106);


            	    ID107=(Token)match(input,ID,FOLLOW_ID_in_var_decl2145); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ID107);


            	    }
            	    break;

            	default :
            	    break loop31;
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
            // 393:28: -> ^( VAR var_type ( ID )+ )
            {
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:393:31: ^( VAR var_type ( ID )+ )
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:397:1: var_type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlParser.var_type_return var_type() throws RecognitionException {
        CtrlParser.var_type_return retval = new CtrlParser.var_type_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token set108=null;

        CtrlTree set108_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:398:2: ( NODE | BOOL | STRING | INT | REAL )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
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
        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:237:33: ( ELSE )
        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:237:34: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred1_Ctrl1225); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred1_Ctrl

    // $ANTLR start synpred2_Ctrl
    public final void synpred2_Ctrl_fragment() throws RecognitionException {
        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:241:17: ( ELSE )
        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:241:18: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred2_Ctrl1265); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred2_Ctrl

    // $ANTLR start synpred3_Ctrl
    public final void synpred3_Ctrl_fragment() throws RecognitionException {
        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:244:20: ( OR )
        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:244:21: OR
        {
        match(input,OR,FOLLOW_OR_in_synpred3_Ctrl1300); if (state.failed) return ;

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


 

    public static final BitSet FOLLOW_package_decl_in_program166 = new BitSet(new long[]{0x45002272E80A2250L,0x00000000000000B2L});
    public static final BitSet FOLLOW_import_decl_in_program172 = new BitSet(new long[]{0x45002272E80A2250L,0x00000000000000B2L});
    public static final BitSet FOLLOW_function_in_program180 = new BitSet(new long[]{0x45002272680A2250L,0x00000000000000B2L});
    public static final BitSet FOLLOW_recipe_in_program182 = new BitSet(new long[]{0x45002272680A2250L,0x00000000000000B2L});
    public static final BitSet FOLLOW_stat_in_program184 = new BitSet(new long[]{0x45002272680A2250L,0x00000000000000B2L});
    public static final BitSet FOLLOW_EOF_in_program188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_package_decl325 = new BitSet(new long[]{0x0000200020000240L});
    public static final BitSet FOLLOW_qual_name_in_package_decl327 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_SEMI_in_package_decl332 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_decl399 = new BitSet(new long[]{0x0000200020000240L});
    public static final BitSet FOLLOW_qual_name_in_import_decl402 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_SEMI_in_import_decl405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_qual_name447 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_DOT_in_qual_name451 = new BitSet(new long[]{0x0000200020000240L});
    public static final BitSet FOLLOW_qual_name_in_qual_name455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERISK_in_qual_name494 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_DOT_in_qual_name496 = new BitSet(new long[]{0x0000200000000040L});
    public static final BitSet FOLLOW_ANY_in_qual_name516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_qual_name539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RECIPE_in_recipe608 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_ID_in_recipe611 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_par_list_in_recipe613 = new BitSet(new long[]{0x0008002000000000L});
    public static final BitSet FOLLOW_PRIORITY_in_recipe616 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_INT_LIT_in_recipe619 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_block_in_recipe633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function679 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_ID_in_function682 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_par_list_in_function684 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_block_in_function697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_par_list728 = new BitSet(new long[]{0x1100420200002000L,0x0000000000000002L});
    public static final BitSet FOLLOW_par_in_par_list731 = new BitSet(new long[]{0x1000000000040000L});
    public static final BitSet FOLLOW_COMMA_in_par_list734 = new BitSet(new long[]{0x0100420200002000L,0x0000000000000002L});
    public static final BitSet FOLLOW_par_in_par_list736 = new BitSet(new long[]{0x1000000000040000L});
    public static final BitSet FOLLOW_RPAR_in_par_list742 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_par787 = new BitSet(new long[]{0x0100020200002000L,0x0000000000000002L});
    public static final BitSet FOLLOW_var_type_in_par789 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_ID_in_par791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_type_in_par824 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_ID_in_par826 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LCURLY_in_block865 = new BitSet(new long[]{0x41802272600A2250L,0x00000000000000B2L});
    public static final BitSet FOLLOW_stat_in_block867 = new BitSet(new long[]{0x41802272600A2250L,0x00000000000000B2L});
    public static final BitSet FOLLOW_RCURLY_in_block872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_stat904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat921 = new BitSet(new long[]{0x41002272600A2250L,0x00000000000000B2L});
    public static final BitSet FOLLOW_stat_in_stat924 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LANGLE_in_stat947 = new BitSet(new long[]{0x41402272600A2250L,0x00000000000000B2L});
    public static final BitSet FOLLOW_stat_in_stat949 = new BitSet(new long[]{0x41402272600A2250L,0x00000000000000B2L});
    public static final BitSet FOLLOW_RANGLE_in_stat954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_stat995 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat998 = new BitSet(new long[]{0x0000200020000240L,0x0000000000000008L});
    public static final BitSet FOLLOW_cond_in_stat1001 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat1003 = new BitSet(new long[]{0x41002272600A2250L,0x00000000000000B2L});
    public static final BitSet FOLLOW_stat_in_stat1006 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat1026 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat1029 = new BitSet(new long[]{0x0000200020000240L,0x0000000000000008L});
    public static final BitSet FOLLOW_cond_in_stat1032 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat1034 = new BitSet(new long[]{0x41002272600A2250L,0x00000000000000B2L});
    public static final BitSet FOLLOW_stat_in_stat1037 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_stat1042 = new BitSet(new long[]{0x41002272600A2250L,0x00000000000000B2L});
    public static final BitSet FOLLOW_stat_in_stat1044 = new BitSet(new long[]{0x0000000000000000L,0x00000000000000A0L});
    public static final BitSet FOLLOW_WHILE_in_stat1087 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat1089 = new BitSet(new long[]{0x0000200020000240L,0x0000000000000008L});
    public static final BitSet FOLLOW_cond_in_stat1091 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat1093 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat1156 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat1158 = new BitSet(new long[]{0x0000200020000240L,0x0000000000000008L});
    public static final BitSet FOLLOW_cond_in_stat1160 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat1162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_stat1209 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat1212 = new BitSet(new long[]{0x0000200020000240L,0x0000000000000008L});
    public static final BitSet FOLLOW_cond_in_stat1215 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat1217 = new BitSet(new long[]{0x41002272600A2250L,0x00000000000000B2L});
    public static final BitSet FOLLOW_stat_in_stat1220 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_ELSE_in_stat1230 = new BitSet(new long[]{0x41002272600A2250L,0x00000000000000B2L});
    public static final BitSet FOLLOW_stat_in_stat1233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_stat1257 = new BitSet(new long[]{0x41002272600A2250L,0x00000000000000B2L});
    public static final BitSet FOLLOW_stat_in_stat1260 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_ELSE_in_stat1270 = new BitSet(new long[]{0x41002272600A2250L,0x00000000000000B2L});
    public static final BitSet FOLLOW_stat_in_stat1273 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_stat1292 = new BitSet(new long[]{0x41002272600A2250L,0x00000000000000B2L});
    public static final BitSet FOLLOW_stat_in_stat1295 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_OR_in_stat1305 = new BitSet(new long[]{0x41002272600A2250L,0x00000000000000B2L});
    public static final BitSet FOLLOW_stat_in_stat1308 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_expr_in_stat1323 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat1325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat1340 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat1342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cond_atom_in_cond1367 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_BAR_in_cond1376 = new BitSet(new long[]{0x0000200020000240L,0x0000000000000008L});
    public static final BitSet FOLLOW_cond_atom_in_cond1378 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_TRUE_in_cond_atom1424 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_cond_atom1445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr2_in_expr1475 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_BAR_in_expr1483 = new BitSet(new long[]{0x4000204020000240L});
    public static final BitSet FOLLOW_expr2_in_expr1485 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_expr_atom_in_expr21566 = new BitSet(new long[]{0x0004000000000202L});
    public static final BitSet FOLLOW_PLUS_in_expr21576 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERISK_in_expr21603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expr21658 = new BitSet(new long[]{0x0000204020000240L});
    public static final BitSet FOLLOW_expr_atom_in_expr21660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_atom1691 = new BitSet(new long[]{0x4000204020000240L});
    public static final BitSet FOLLOW_expr_in_expr_atom1693 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_RPAR_in_expr_atom1697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expr_atom1725 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_name_in_call1755 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_arg_list_in_call1757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qual_name_in_rule_name1843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_arg_list1879 = new BitSet(new long[]{0x1200400424100000L,0x000000000000000CL});
    public static final BitSet FOLLOW_arg_in_arg_list1882 = new BitSet(new long[]{0x1000000000040000L});
    public static final BitSet FOLLOW_COMMA_in_arg_list1885 = new BitSet(new long[]{0x0200400424100000L,0x000000000000000CL});
    public static final BitSet FOLLOW_arg_in_arg_list1887 = new BitSet(new long[]{0x1000000000040000L});
    public static final BitSet FOLLOW_RPAR_in_arg_list1895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_arg1942 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_ID_in_arg1944 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_arg1975 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg2004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_arg2021 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_type_in_var_decl2138 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_ID_in_var_decl2140 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_COMMA_in_var_decl2143 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_ID_in_var_decl2145 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_ELSE_in_synpred1_Ctrl1225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred2_Ctrl1265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_synpred3_Ctrl1300 = new BitSet(new long[]{0x0000000000000002L});

}