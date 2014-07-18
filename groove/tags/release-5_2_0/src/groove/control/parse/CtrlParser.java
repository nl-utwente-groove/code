// $ANTLR 3.4 E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g 2014-07-17 20:52:59

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALAP", "AMP", "ANY", "ARG", "ARGS", "ASTERISK", "ATOM", "BAR", "BLOCK", "BOOL", "BSLASH", "CALL", "CHOICE", "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", "ELSE", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF", "IMPORT", "IMPORTS", "INT", "INT_LIT", "IntegerNumber", "LANGLE", "LCURLY", "LPAR", "MINUS", "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT", "PACKAGE", "PAR", "PARS", "PLUS", "PRIORITY", "PROGRAM", "QUOTE", "RANGLE", "RCURLY", "REAL", "REAL_LIT", "RECIPE", "RECIPES", "RPAR", "SEMI", "SHARP", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", "TRUE", "TRY", "UNTIL", "VAR", "WHILE", "WS"
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
    public static final int LANGLE=35;
    public static final int LCURLY=36;
    public static final int LPAR=37;
    public static final int MINUS=38;
    public static final int ML_COMMENT=39;
    public static final int NODE=40;
    public static final int NOT=41;
    public static final int NonIntegerNumber=42;
    public static final int OR=43;
    public static final int OTHER=44;
    public static final int OUT=45;
    public static final int PACKAGE=46;
    public static final int PAR=47;
    public static final int PARS=48;
    public static final int PLUS=49;
    public static final int PRIORITY=50;
    public static final int PROGRAM=51;
    public static final int QUOTE=52;
    public static final int RANGLE=53;
    public static final int RCURLY=54;
    public static final int REAL=55;
    public static final int REAL_LIT=56;
    public static final int RECIPE=57;
    public static final int RECIPES=58;
    public static final int RPAR=59;
    public static final int SEMI=60;
    public static final int SHARP=61;
    public static final int SL_COMMENT=62;
    public static final int STAR=63;
    public static final int STRING=64;
    public static final int STRING_LIT=65;
    public static final int TRUE=66;
    public static final int TRY=67;
    public static final int UNTIL=68;
    public static final int VAR=69;
    public static final int WHILE=70;
    public static final int WS=71;

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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:75:1: program : package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) ) ;
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:78:3: ( package_decl ( import_decl )* ( function | recipe | stat )* EOF -> ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:82:5: package_decl ( import_decl )* ( function | recipe | stat )* EOF
            {
            pushFollow(FOLLOW_package_decl_in_program166);
            package_decl1=package_decl();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_package_decl.add(package_decl1.getTree());

            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:83:5: ( import_decl )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==IMPORT) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:83:5: import_decl
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


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:84:5: ( function | recipe | stat )*
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
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:84:6: function
            	    {
            	    pushFollow(FOLLOW_function_in_program180);
            	    function3=function();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_function.add(function3.getTree());

            	    }
            	    break;
            	case 2 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:84:15: recipe
            	    {
            	    pushFollow(FOLLOW_recipe_in_program182);
            	    recipe4=recipe();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_recipe.add(recipe4.getTree());

            	    }
            	    break;
            	case 3 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:84:22: stat
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
            // 86:5: -> ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) )
            {
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:86:8: ^( PROGRAM package_decl ^( IMPORTS ( import_decl )* ) ^( FUNCTIONS ( function )* ) ^( RECIPES ( recipe )* ) ^( BLOCK ( stat )* ) )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(PROGRAM, "PROGRAM")
                , root_1);

                adaptor.addChild(root_1, stream_package_decl.nextTree());

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:88:11: ^( IMPORTS ( import_decl )* )
                {
                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                root_2 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(IMPORTS, "IMPORTS")
                , root_2);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:88:21: ( import_decl )*
                while ( stream_import_decl.hasNext() ) {
                    adaptor.addChild(root_2, stream_import_decl.nextTree());

                }
                stream_import_decl.reset();

                adaptor.addChild(root_1, root_2);
                }

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:89:11: ^( FUNCTIONS ( function )* )
                {
                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                root_2 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(FUNCTIONS, "FUNCTIONS")
                , root_2);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:89:23: ( function )*
                while ( stream_function.hasNext() ) {
                    adaptor.addChild(root_2, stream_function.nextTree());

                }
                stream_function.reset();

                adaptor.addChild(root_1, root_2);
                }

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:90:11: ^( RECIPES ( recipe )* )
                {
                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                root_2 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(RECIPES, "RECIPES")
                , root_2);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:90:21: ( recipe )*
                while ( stream_recipe.hasNext() ) {
                    adaptor.addChild(root_2, stream_recipe.nextTree());

                }
                stream_recipe.reset();

                adaptor.addChild(root_1, root_2);
                }

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:91:11: ^( BLOCK ( stat )* )
                {
                CtrlTree root_2 = (CtrlTree)adaptor.nil();
                root_2 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                , root_2);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:91:19: ( stat )*
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:96:1: package_decl : (key= PACKAGE qual_name[false] close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->) ;
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:97:3: ( (key= PACKAGE qual_name[false] close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:99:5: (key= PACKAGE qual_name[false] close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->)
            {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:99:5: (key= PACKAGE qual_name[false] close= SEMI -> ^( PACKAGE[$key] qual_name SEMI[$close] ) | ->)
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:99:7: key= PACKAGE qual_name[false] close= SEMI
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
                    // 101:7: -> ^( PACKAGE[$key] qual_name SEMI[$close] )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:101:10: ^( PACKAGE[$key] qual_name SEMI[$close] )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:102:7: 
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:107:1: import_decl : IMPORT ^ qual_name[false] SEMI ;
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:108:3: ( IMPORT ^ qual_name[false] SEMI )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:110:5: IMPORT ^ qual_name[false] SEMI
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:116:1: qual_name[boolean any] : (id= ID ( DOT rest= qual_name[any] )? ->|{...}? ( ASTERISK DOT )? ( ANY ->| OTHER ->) );
    public final CtrlParser.qual_name_return qual_name(boolean any) throws RecognitionException {
        CtrlParser.qual_name_return retval = new CtrlParser.qual_name_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token id=null;
        Token DOT11=null;
        Token ASTERISK12=null;
        Token DOT13=null;
        Token ANY14=null;
        Token OTHER15=null;
        CtrlParser.qual_name_return rest =null;


        CtrlTree id_tree=null;
        CtrlTree DOT11_tree=null;
        CtrlTree ASTERISK12_tree=null;
        CtrlTree DOT13_tree=null;
        CtrlTree ANY14_tree=null;
        CtrlTree OTHER15_tree=null;
        RewriteRuleTokenStream stream_ANY=new RewriteRuleTokenStream(adaptor,"token ANY");
        RewriteRuleTokenStream stream_OTHER=new RewriteRuleTokenStream(adaptor,"token OTHER");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_ASTERISK=new RewriteRuleTokenStream(adaptor,"token ASTERISK");
        RewriteRuleSubtreeStream stream_qual_name=new RewriteRuleSubtreeStream(adaptor,"rule qual_name");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:117:3: (id= ID ( DOT rest= qual_name[any] )? ->|{...}? ( ASTERISK DOT )? ( ANY ->| OTHER ->) )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:117:5: id= ID ( DOT rest= qual_name[any] )?
                    {
                    id=(Token)match(input,ID,FOLLOW_ID_in_qual_name429); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(id);


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:117:11: ( DOT rest= qual_name[any] )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==DOT) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:117:13: DOT rest= qual_name[any]
                            {
                            DOT11=(Token)match(input,DOT,FOLLOW_DOT_in_qual_name433); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_DOT.add(DOT11);


                            pushFollow(FOLLOW_qual_name_in_qual_name437);
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
                    // 118:22: ->
                    {
                        adaptor.addChild(root_0,  helper.toQualName(id, (rest!=null?((CtrlTree)rest.tree):null)) );

                    }


                    retval.tree = root_0;
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:119:5: {...}? ( ASTERISK DOT )? ( ANY ->| OTHER ->)
                    {
                    if ( !(( any )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "qual_name", " any ");
                    }

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:119:14: ( ASTERISK DOT )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==ASTERISK) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:119:16: ASTERISK DOT
                            {
                            ASTERISK12=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_qual_name476); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ASTERISK.add(ASTERISK12);


                            DOT13=(Token)match(input,DOT,FOLLOW_DOT_in_qual_name478); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_DOT.add(DOT13);


                            }
                            break;

                    }


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:120:14: ( ANY ->| OTHER ->)
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
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:120:16: ANY
                            {
                            ANY14=(Token)match(input,ANY,FOLLOW_ANY_in_qual_name498); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ANY.add(ANY14);


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
                            // 120:22: ->
                            {
                                adaptor.addChild(root_0,  helper.toQualName(ASTERISK12, ANY14) );

                            }


                            retval.tree = root_0;
                            }

                            }
                            break;
                        case 2 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:121:16: OTHER
                            {
                            OTHER15=(Token)match(input,OTHER,FOLLOW_OTHER_in_qual_name521); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_OTHER.add(OTHER15);


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
                                adaptor.addChild(root_0,  helper.toQualName(ASTERISK12, OTHER15) );

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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:128:1: recipe : RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block ;
    public final CtrlParser.recipe_return recipe() throws RecognitionException {
        CtrlParser.recipe_return retval = new CtrlParser.recipe_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token RECIPE16=null;
        Token ID17=null;
        Token PRIORITY19=null;
        Token INT_LIT20=null;
        CtrlParser.par_list_return par_list18 =null;

        CtrlParser.block_return block21 =null;


        CtrlTree RECIPE16_tree=null;
        CtrlTree ID17_tree=null;
        CtrlTree PRIORITY19_tree=null;
        CtrlTree INT_LIT20_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:129:3: ( RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:132:5: RECIPE ^ ID par_list ( PRIORITY ! INT_LIT )? block
            {
            root_0 = (CtrlTree)adaptor.nil();


            RECIPE16=(Token)match(input,RECIPE,FOLLOW_RECIPE_in_recipe570); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RECIPE16_tree = 
            (CtrlTree)adaptor.create(RECIPE16)
            ;
            root_0 = (CtrlTree)adaptor.becomeRoot(RECIPE16_tree, root_0);
            }

            ID17=(Token)match(input,ID,FOLLOW_ID_in_recipe573); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID17_tree = 
            (CtrlTree)adaptor.create(ID17)
            ;
            adaptor.addChild(root_0, ID17_tree);
            }

            pushFollow(FOLLOW_par_list_in_recipe575);
            par_list18=par_list();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, par_list18.getTree());

            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:132:25: ( PRIORITY ! INT_LIT )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==PRIORITY) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:132:26: PRIORITY ! INT_LIT
                    {
                    PRIORITY19=(Token)match(input,PRIORITY,FOLLOW_PRIORITY_in_recipe578); if (state.failed) return retval;

                    INT_LIT20=(Token)match(input,INT_LIT,FOLLOW_INT_LIT_in_recipe581); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT_LIT20_tree = 
                    (CtrlTree)adaptor.create(INT_LIT20)
                    ;
                    adaptor.addChild(root_0, INT_LIT20_tree);
                    }

                    }
                    break;

            }


            if ( state.backtracking==0 ) { helper.setContext(RECIPE16_tree); }

            pushFollow(FOLLOW_block_in_recipe595);
            block21=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block21.getTree());

            if ( state.backtracking==0 ) { helper.resetContext();
                  helper.declareCtrlUnit(RECIPE16_tree);
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:144:1: function : FUNCTION ^ ID par_list block ;
    public final CtrlParser.function_return function() throws RecognitionException {
        CtrlParser.function_return retval = new CtrlParser.function_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token FUNCTION22=null;
        Token ID23=null;
        CtrlParser.par_list_return par_list24 =null;

        CtrlParser.block_return block25 =null;


        CtrlTree FUNCTION22_tree=null;
        CtrlTree ID23_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:145:3: ( FUNCTION ^ ID par_list block )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:147:5: FUNCTION ^ ID par_list block
            {
            root_0 = (CtrlTree)adaptor.nil();


            FUNCTION22=(Token)match(input,FUNCTION,FOLLOW_FUNCTION_in_function626); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            FUNCTION22_tree = 
            (CtrlTree)adaptor.create(FUNCTION22)
            ;
            root_0 = (CtrlTree)adaptor.becomeRoot(FUNCTION22_tree, root_0);
            }

            ID23=(Token)match(input,ID,FOLLOW_ID_in_function629); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID23_tree = 
            (CtrlTree)adaptor.create(ID23)
            ;
            adaptor.addChild(root_0, ID23_tree);
            }

            pushFollow(FOLLOW_par_list_in_function631);
            par_list24=par_list();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, par_list24.getTree());

            if ( state.backtracking==0 ) { helper.setContext(FUNCTION22_tree); }

            pushFollow(FOLLOW_block_in_function644);
            block25=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block25.getTree());

            if ( state.backtracking==0 ) { helper.resetContext();
                  helper.declareCtrlUnit(FUNCTION22_tree);
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:158:1: par_list : LPAR ( par ( COMMA par )* )? RPAR -> ^( PARS ( par )* ) ;
    public final CtrlParser.par_list_return par_list() throws RecognitionException {
        CtrlParser.par_list_return retval = new CtrlParser.par_list_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token LPAR26=null;
        Token COMMA28=null;
        Token RPAR30=null;
        CtrlParser.par_return par27 =null;

        CtrlParser.par_return par29 =null;


        CtrlTree LPAR26_tree=null;
        CtrlTree COMMA28_tree=null;
        CtrlTree RPAR30_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_par=new RewriteRuleSubtreeStream(adaptor,"rule par");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:159:3: ( LPAR ( par ( COMMA par )* )? RPAR -> ^( PARS ( par )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:161:5: LPAR ( par ( COMMA par )* )? RPAR
            {
            LPAR26=(Token)match(input,LPAR,FOLLOW_LPAR_in_par_list675); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAR.add(LPAR26);


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:161:10: ( par ( COMMA par )* )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==BOOL||LA10_0==INT||LA10_0==NODE||LA10_0==OUT||LA10_0==REAL||LA10_0==STRING) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:161:11: par ( COMMA par )*
                    {
                    pushFollow(FOLLOW_par_in_par_list678);
                    par27=par();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_par.add(par27.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:161:15: ( COMMA par )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0==COMMA) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:161:16: COMMA par
                    	    {
                    	    COMMA28=(Token)match(input,COMMA,FOLLOW_COMMA_in_par_list681); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA28);


                    	    pushFollow(FOLLOW_par_in_par_list683);
                    	    par29=par();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_par.add(par29.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);


                    }
                    break;

            }


            RPAR30=(Token)match(input,RPAR,FOLLOW_RPAR_in_par_list689); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAR.add(RPAR30);


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
            // 162:5: -> ^( PARS ( par )* )
            {
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:162:8: ^( PARS ( par )* )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(PARS, "PARS")
                , root_1);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:162:15: ( par )*
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:168:1: par : ( OUT var_type ID -> ^( PAR OUT var_type ID ) | var_type ID -> ^( PAR var_type ID ) );
    public final CtrlParser.par_return par() throws RecognitionException {
        CtrlParser.par_return retval = new CtrlParser.par_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token OUT31=null;
        Token ID33=null;
        Token ID35=null;
        CtrlParser.var_type_return var_type32 =null;

        CtrlParser.var_type_return var_type34 =null;


        CtrlTree OUT31_tree=null;
        CtrlTree ID33_tree=null;
        CtrlTree ID35_tree=null;
        RewriteRuleTokenStream stream_OUT=new RewriteRuleTokenStream(adaptor,"token OUT");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_var_type=new RewriteRuleSubtreeStream(adaptor,"rule var_type");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:169:3: ( OUT var_type ID -> ^( PAR OUT var_type ID ) | var_type ID -> ^( PAR var_type ID ) )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:172:5: OUT var_type ID
                    {
                    OUT31=(Token)match(input,OUT,FOLLOW_OUT_in_par734); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT31);


                    pushFollow(FOLLOW_var_type_in_par736);
                    var_type32=var_type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_var_type.add(var_type32.getTree());

                    ID33=(Token)match(input,ID,FOLLOW_ID_in_par738); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID33);


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
                    // 172:21: -> ^( PAR OUT var_type ID )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:172:24: ^( PAR OUT var_type ID )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:176:5: var_type ID
                    {
                    pushFollow(FOLLOW_var_type_in_par771);
                    var_type34=var_type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_var_type.add(var_type34.getTree());

                    ID35=(Token)match(input,ID,FOLLOW_ID_in_par773); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID35);


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
                    // 176:17: -> ^( PAR var_type ID )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:176:20: ^( PAR var_type ID )
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:180:1: block : open= LCURLY ( stat )* close= RCURLY -> ^( BLOCK[$open] ( stat )* TRUE[$close] ) ;
    public final CtrlParser.block_return block() throws RecognitionException {
        CtrlParser.block_return retval = new CtrlParser.block_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        CtrlParser.stat_return stat36 =null;


        CtrlTree open_tree=null;
        CtrlTree close_tree=null;
        RewriteRuleTokenStream stream_LCURLY=new RewriteRuleTokenStream(adaptor,"token LCURLY");
        RewriteRuleTokenStream stream_RCURLY=new RewriteRuleTokenStream(adaptor,"token RCURLY");
        RewriteRuleSubtreeStream stream_stat=new RewriteRuleSubtreeStream(adaptor,"rule stat");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:181:3: (open= LCURLY ( stat )* close= RCURLY -> ^( BLOCK[$open] ( stat )* TRUE[$close] ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:183:5: open= LCURLY ( stat )* close= RCURLY
            {
            open=(Token)match(input,LCURLY,FOLLOW_LCURLY_in_block812); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LCURLY.add(open);


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:183:17: ( stat )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==ALAP||LA12_0==ANY||LA12_0==ASTERISK||LA12_0==BOOL||LA12_0==CHOICE||LA12_0==DO||(LA12_0 >= ID && LA12_0 <= IF)||LA12_0==INT||(LA12_0 >= LANGLE && LA12_0 <= LPAR)||LA12_0==NODE||LA12_0==OTHER||LA12_0==REAL||LA12_0==SHARP||LA12_0==STRING||(LA12_0 >= TRY && LA12_0 <= UNTIL)||LA12_0==WHILE) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:183:17: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_block814);
            	    stat36=stat();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_stat.add(stat36.getTree());

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            close=(Token)match(input,RCURLY,FOLLOW_RCURLY_in_block819); if (state.failed) return retval; 
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
            // 184:5: -> ^( BLOCK[$open] ( stat )* TRUE[$close] )
            {
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:184:8: ^( BLOCK[$open] ( stat )* TRUE[$close] )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(BLOCK, open)
                , root_1);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:184:23: ( stat )*
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:187:1: stat : ( block | ALAP ^ stat |open= LANGLE ( stat )* close= RANGLE -> ^( ATOM[$open] ^( BLOCK ( stat )* TRUE[$close] ) ) | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI ^| var_decl SEMI ^);
    public final CtrlParser.stat_return stat() throws RecognitionException {
        CtrlParser.stat_return retval = new CtrlParser.stat_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        Token ALAP38=null;
        Token WHILE41=null;
        Token LPAR42=null;
        Token RPAR44=null;
        Token UNTIL46=null;
        Token LPAR47=null;
        Token RPAR49=null;
        Token DO51=null;
        Token WHILE53=null;
        Token LPAR54=null;
        Token RPAR56=null;
        Token UNTIL57=null;
        Token LPAR58=null;
        Token RPAR60=null;
        Token IF61=null;
        Token LPAR62=null;
        Token RPAR64=null;
        Token ELSE66=null;
        Token TRY68=null;
        Token ELSE70=null;
        Token CHOICE72=null;
        Token OR74=null;
        Token SEMI77=null;
        Token SEMI79=null;
        CtrlParser.block_return block37 =null;

        CtrlParser.stat_return stat39 =null;

        CtrlParser.stat_return stat40 =null;

        CtrlParser.cond_return cond43 =null;

        CtrlParser.stat_return stat45 =null;

        CtrlParser.cond_return cond48 =null;

        CtrlParser.stat_return stat50 =null;

        CtrlParser.stat_return stat52 =null;

        CtrlParser.cond_return cond55 =null;

        CtrlParser.cond_return cond59 =null;

        CtrlParser.cond_return cond63 =null;

        CtrlParser.stat_return stat65 =null;

        CtrlParser.stat_return stat67 =null;

        CtrlParser.stat_return stat69 =null;

        CtrlParser.stat_return stat71 =null;

        CtrlParser.stat_return stat73 =null;

        CtrlParser.stat_return stat75 =null;

        CtrlParser.expr_return expr76 =null;

        CtrlParser.var_decl_return var_decl78 =null;


        CtrlTree open_tree=null;
        CtrlTree close_tree=null;
        CtrlTree ALAP38_tree=null;
        CtrlTree WHILE41_tree=null;
        CtrlTree LPAR42_tree=null;
        CtrlTree RPAR44_tree=null;
        CtrlTree UNTIL46_tree=null;
        CtrlTree LPAR47_tree=null;
        CtrlTree RPAR49_tree=null;
        CtrlTree DO51_tree=null;
        CtrlTree WHILE53_tree=null;
        CtrlTree LPAR54_tree=null;
        CtrlTree RPAR56_tree=null;
        CtrlTree UNTIL57_tree=null;
        CtrlTree LPAR58_tree=null;
        CtrlTree RPAR60_tree=null;
        CtrlTree IF61_tree=null;
        CtrlTree LPAR62_tree=null;
        CtrlTree RPAR64_tree=null;
        CtrlTree ELSE66_tree=null;
        CtrlTree TRY68_tree=null;
        CtrlTree ELSE70_tree=null;
        CtrlTree CHOICE72_tree=null;
        CtrlTree OR74_tree=null;
        CtrlTree SEMI77_tree=null;
        CtrlTree SEMI79_tree=null;
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:188:2: ( block | ALAP ^ stat |open= LANGLE ( stat )* close= RANGLE -> ^( ATOM[$open] ^( BLOCK ( stat )* TRUE[$close] ) ) | WHILE ^ LPAR ! cond RPAR ! stat | UNTIL ^ LPAR ! cond RPAR ! stat | DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) ) | IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )? | TRY ^ stat ( ( ELSE )=> ELSE ! stat )? | CHOICE ^ stat ( ( OR )=> OR ! stat )+ | expr SEMI ^| var_decl SEMI ^)
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:189:4: block
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_block_in_stat851);
                    block37=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block37.getTree());

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:193:4: ALAP ^ stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    ALAP38=(Token)match(input,ALAP,FOLLOW_ALAP_in_stat868); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ALAP38_tree = 
                    (CtrlTree)adaptor.create(ALAP38)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(ALAP38_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat871);
                    stat39=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat39.getTree());

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:202:4: open= LANGLE ( stat )* close= RANGLE
                    {
                    open=(Token)match(input,LANGLE,FOLLOW_LANGLE_in_stat902); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LANGLE.add(open);


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:202:16: ( stat )*
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( (LA13_0==ALAP||LA13_0==ANY||LA13_0==ASTERISK||LA13_0==BOOL||LA13_0==CHOICE||LA13_0==DO||(LA13_0 >= ID && LA13_0 <= IF)||LA13_0==INT||(LA13_0 >= LANGLE && LA13_0 <= LPAR)||LA13_0==NODE||LA13_0==OTHER||LA13_0==REAL||LA13_0==SHARP||LA13_0==STRING||(LA13_0 >= TRY && LA13_0 <= UNTIL)||LA13_0==WHILE) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:202:16: stat
                    	    {
                    	    pushFollow(FOLLOW_stat_in_stat904);
                    	    stat40=stat();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_stat.add(stat40.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop13;
                        }
                    } while (true);


                    close=(Token)match(input,RANGLE,FOLLOW_RANGLE_in_stat909); if (state.failed) return retval; 
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
                    // 203:4: -> ^( ATOM[$open] ^( BLOCK ( stat )* TRUE[$close] ) )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:203:7: ^( ATOM[$open] ^( BLOCK ( stat )* TRUE[$close] ) )
                        {
                        CtrlTree root_1 = (CtrlTree)adaptor.nil();
                        root_1 = (CtrlTree)adaptor.becomeRoot(
                        (CtrlTree)adaptor.create(ATOM, open)
                        , root_1);

                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:203:21: ^( BLOCK ( stat )* TRUE[$close] )
                        {
                        CtrlTree root_2 = (CtrlTree)adaptor.nil();
                        root_2 = (CtrlTree)adaptor.becomeRoot(
                        (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                        , root_2);

                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:203:29: ( stat )*
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:208:4: WHILE ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    WHILE41=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat950); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    WHILE41_tree = 
                    (CtrlTree)adaptor.create(WHILE41)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(WHILE41_tree, root_0);
                    }

                    LPAR42=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat953); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat956);
                    cond43=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond43.getTree());

                    RPAR44=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat958); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat961);
                    stat45=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat45.getTree());

                    }
                    break;
                case 5 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:212:5: UNTIL ^ LPAR ! cond RPAR ! stat
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    UNTIL46=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat981); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    UNTIL46_tree = 
                    (CtrlTree)adaptor.create(UNTIL46)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(UNTIL46_tree, root_0);
                    }

                    LPAR47=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat984); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat987);
                    cond48=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond48.getTree());

                    RPAR49=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat989); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat992);
                    stat50=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat50.getTree());

                    }
                    break;
                case 6 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:213:4: DO stat ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
                    {
                    DO51=(Token)match(input,DO,FOLLOW_DO_in_stat997); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DO.add(DO51);


                    pushFollow(FOLLOW_stat_in_stat999);
                    stat52=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_stat.add(stat52.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:214:4: ( WHILE LPAR cond RPAR -> ^( BLOCK stat ^( WHILE cond stat ) ) | UNTIL LPAR cond RPAR -> ^( BLOCK stat ^( UNTIL cond stat ) ) )
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
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:219:7: WHILE LPAR cond RPAR
                            {
                            WHILE53=(Token)match(input,WHILE,FOLLOW_WHILE_in_stat1042); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_WHILE.add(WHILE53);


                            LPAR54=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1044); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR54);


                            pushFollow(FOLLOW_cond_in_stat1046);
                            cond55=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond55.getTree());

                            RPAR56=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1048); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR56);


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
                            // 219:28: -> ^( BLOCK stat ^( WHILE cond stat ) )
                            {
                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:219:31: ^( BLOCK stat ^( WHILE cond stat ) )
                                {
                                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                                root_1 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:219:44: ^( WHILE cond stat )
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
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:226:5: UNTIL LPAR cond RPAR
                            {
                            UNTIL57=(Token)match(input,UNTIL,FOLLOW_UNTIL_in_stat1111); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_UNTIL.add(UNTIL57);


                            LPAR58=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1113); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_LPAR.add(LPAR58);


                            pushFollow(FOLLOW_cond_in_stat1115);
                            cond59=cond();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_cond.add(cond59.getTree());

                            RPAR60=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1117); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_RPAR.add(RPAR60);


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
                            // 226:26: -> ^( BLOCK stat ^( UNTIL cond stat ) )
                            {
                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:226:29: ^( BLOCK stat ^( UNTIL cond stat ) )
                                {
                                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                                root_1 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_stat.nextTree());

                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:226:42: ^( UNTIL cond stat )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:232:5: IF ^ LPAR ! cond RPAR ! stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    IF61=(Token)match(input,IF,FOLLOW_IF_in_stat1164); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IF61_tree = 
                    (CtrlTree)adaptor.create(IF61)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(IF61_tree, root_0);
                    }

                    LPAR62=(Token)match(input,LPAR,FOLLOW_LPAR_in_stat1167); if (state.failed) return retval;

                    pushFollow(FOLLOW_cond_in_stat1170);
                    cond63=cond();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, cond63.getTree());

                    RPAR64=(Token)match(input,RPAR,FOLLOW_RPAR_in_stat1172); if (state.failed) return retval;

                    pushFollow(FOLLOW_stat_in_stat1175);
                    stat65=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat65.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:232:31: ( ( ELSE )=> ELSE ! stat )?
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
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:232:33: ( ELSE )=> ELSE ! stat
                            {
                            ELSE66=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat1185); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat1188);
                            stat67=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat67.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:236:5: TRY ^ stat ( ( ELSE )=> ELSE ! stat )?
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    TRY68=(Token)match(input,TRY,FOLLOW_TRY_in_stat1212); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRY68_tree = 
                    (CtrlTree)adaptor.create(TRY68)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(TRY68_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat1215);
                    stat69=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat69.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:236:15: ( ( ELSE )=> ELSE ! stat )?
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
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:236:17: ( ELSE )=> ELSE ! stat
                            {
                            ELSE70=(Token)match(input,ELSE,FOLLOW_ELSE_in_stat1225); if (state.failed) return retval;

                            pushFollow(FOLLOW_stat_in_stat1228);
                            stat71=stat();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, stat71.getTree());

                            }
                            break;

                    }


                    }
                    break;
                case 9 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:239:5: CHOICE ^ stat ( ( OR )=> OR ! stat )+
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    CHOICE72=(Token)match(input,CHOICE,FOLLOW_CHOICE_in_stat1247); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CHOICE72_tree = 
                    (CtrlTree)adaptor.create(CHOICE72)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(CHOICE72_tree, root_0);
                    }

                    pushFollow(FOLLOW_stat_in_stat1250);
                    stat73=stat();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat73.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:239:18: ( ( OR )=> OR ! stat )+
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
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:239:20: ( OR )=> OR ! stat
                    	    {
                    	    OR74=(Token)match(input,OR,FOLLOW_OR_in_stat1260); if (state.failed) return retval;

                    	    pushFollow(FOLLOW_stat_in_stat1263);
                    	    stat75=stat();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, stat75.getTree());

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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:242:4: expr SEMI ^
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_expr_in_stat1278);
                    expr76=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, expr76.getTree());

                    SEMI77=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat1280); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SEMI77_tree = 
                    (CtrlTree)adaptor.create(SEMI77)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(SEMI77_tree, root_0);
                    }

                    }
                    break;
                case 11 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:245:4: var_decl SEMI ^
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_var_decl_in_stat1295);
                    var_decl78=var_decl();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, var_decl78.getTree());

                    SEMI79=(Token)match(input,SEMI,FOLLOW_SEMI_in_stat1297); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    SEMI79_tree = 
                    (CtrlTree)adaptor.create(SEMI79)
                    ;
                    root_0 = (CtrlTree)adaptor.becomeRoot(SEMI79_tree, root_0);
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:249:1: cond : cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) ;
    public final CtrlParser.cond_return cond() throws RecognitionException {
        CtrlParser.cond_return retval = new CtrlParser.cond_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token BAR81=null;
        CtrlParser.cond_atom_return cond_atom80 =null;

        CtrlParser.cond_atom_return cond_atom82 =null;


        CtrlTree BAR81_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_cond_atom=new RewriteRuleSubtreeStream(adaptor,"rule cond_atom");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:250:2: ( cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:252:4: cond_atom ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
            {
            pushFollow(FOLLOW_cond_atom_in_cond1322);
            cond_atom80=cond_atom();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom80.getTree());

            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:253:4: ( ( BAR cond_atom )+ -> ^( CHOICE cond_atom ( cond_atom )+ ) | -> cond_atom )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:253:6: ( BAR cond_atom )+
                    {
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:253:6: ( BAR cond_atom )+
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
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:253:7: BAR cond_atom
                    	    {
                    	    BAR81=(Token)match(input,BAR,FOLLOW_BAR_in_cond1331); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR81);


                    	    pushFollow(FOLLOW_cond_atom_in_cond1333);
                    	    cond_atom82=cond_atom();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_cond_atom.add(cond_atom82.getTree());

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
                    // 253:23: -> ^( CHOICE cond_atom ( cond_atom )+ )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:253:26: ^( CHOICE cond_atom ( cond_atom )+ )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:254:6: 
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
                    // 254:6: -> cond_atom
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:258:1: cond_atom : ( TRUE | call );
    public final CtrlParser.cond_atom_return cond_atom() throws RecognitionException {
        CtrlParser.cond_atom_return retval = new CtrlParser.cond_atom_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token TRUE83=null;
        CtrlParser.call_return call84 =null;


        CtrlTree TRUE83_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:259:2: ( TRUE | call )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:261:4: TRUE
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    TRUE83=(Token)match(input,TRUE,FOLLOW_TRUE_in_cond_atom1379); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    TRUE83_tree = 
                    (CtrlTree)adaptor.create(TRUE83)
                    ;
                    adaptor.addChild(root_0, TRUE83_tree);
                    }

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:265:5: call
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_cond_atom1400);
                    call84=call();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, call84.getTree());

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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:268:1: expr : expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) ;
    public final CtrlParser.expr_return expr() throws RecognitionException {
        CtrlParser.expr_return retval = new CtrlParser.expr_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token BAR86=null;
        CtrlParser.expr2_return expr285 =null;

        CtrlParser.expr2_return expr287 =null;


        CtrlTree BAR86_tree=null;
        RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
        RewriteRuleSubtreeStream stream_expr2=new RewriteRuleSubtreeStream(adaptor,"rule expr2");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:269:2: ( expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:273:4: expr2 ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
            {
            pushFollow(FOLLOW_expr2_in_expr1430);
            expr285=expr2();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_expr2.add(expr285.getTree());

            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:274:4: ( ( BAR expr2 )+ -> ^( CHOICE expr2 ( expr2 )+ ) | -> expr2 )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:274:6: ( BAR expr2 )+
                    {
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:274:6: ( BAR expr2 )+
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
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:274:7: BAR expr2
                    	    {
                    	    BAR86=(Token)match(input,BAR,FOLLOW_BAR_in_expr1438); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_BAR.add(BAR86);


                    	    pushFollow(FOLLOW_expr2_in_expr1440);
                    	    expr287=expr2();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_expr2.add(expr287.getTree());

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
                    // 274:19: -> ^( CHOICE expr2 ( expr2 )+ )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:274:22: ^( CHOICE expr2 ( expr2 )+ )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:275:6: 
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
                    // 275:6: -> expr2
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:279:1: expr2 : (e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e) |op= SHARP expr_atom -> ^( ALAP[$op] expr_atom ) );
    public final CtrlParser.expr2_return expr2() throws RecognitionException {
        CtrlParser.expr2_return retval = new CtrlParser.expr2_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token plus=null;
        Token ast=null;
        Token op=null;
        CtrlParser.expr_atom_return e =null;

        CtrlParser.expr_atom_return expr_atom88 =null;


        CtrlTree plus_tree=null;
        CtrlTree ast_tree=null;
        CtrlTree op_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_SHARP=new RewriteRuleTokenStream(adaptor,"token SHARP");
        RewriteRuleTokenStream stream_ASTERISK=new RewriteRuleTokenStream(adaptor,"token ASTERISK");
        RewriteRuleSubtreeStream stream_expr_atom=new RewriteRuleSubtreeStream(adaptor,"rule expr_atom");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:280:3: (e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e) |op= SHARP expr_atom -> ^( ALAP[$op] expr_atom ) )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:288:5: e= expr_atom (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e)
                    {
                    pushFollow(FOLLOW_expr_atom_in_expr21521);
                    e=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(e.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:289:5: (plus= PLUS -> ^( BLOCK $e ^( STAR[$plus] $e) ) |ast= ASTERISK -> ^( STAR[$ast] $e) | -> $e)
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
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:289:7: plus= PLUS
                            {
                            plus=(Token)match(input,PLUS,FOLLOW_PLUS_in_expr21531); if (state.failed) return retval; 
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
                            // 289:17: -> ^( BLOCK $e ^( STAR[$plus] $e) )
                            {
                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:289:20: ^( BLOCK $e ^( STAR[$plus] $e) )
                                {
                                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                                root_1 = (CtrlTree)adaptor.becomeRoot(
                                (CtrlTree)adaptor.create(BLOCK, "BLOCK")
                                , root_1);

                                adaptor.addChild(root_1, stream_e.nextTree());

                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:289:31: ^( STAR[$plus] $e)
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
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:290:7: ast= ASTERISK
                            {
                            ast=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_expr21558); if (state.failed) return retval; 
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
                            // 290:20: -> ^( STAR[$ast] $e)
                            {
                                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:290:23: ^( STAR[$ast] $e)
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
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:291:7: 
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
                            // 291:7: -> $e
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:297:5: op= SHARP expr_atom
                    {
                    op=(Token)match(input,SHARP,FOLLOW_SHARP_in_expr21613); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SHARP.add(op);


                    pushFollow(FOLLOW_expr_atom_in_expr21615);
                    expr_atom88=expr_atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr_atom.add(expr_atom88.getTree());

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
                    // 297:24: -> ^( ALAP[$op] expr_atom )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:297:27: ^( ALAP[$op] expr_atom )
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:300:1: expr_atom : (open= LPAR expr close= RPAR -> ^( BLOCK[$open] expr TRUE[$close] ) | call );
    public final CtrlParser.expr_atom_return expr_atom() throws RecognitionException {
        CtrlParser.expr_atom_return retval = new CtrlParser.expr_atom_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token open=null;
        Token close=null;
        CtrlParser.expr_return expr89 =null;

        CtrlParser.call_return call90 =null;


        CtrlTree open_tree=null;
        CtrlTree close_tree=null;
        RewriteRuleTokenStream stream_RPAR=new RewriteRuleTokenStream(adaptor,"token RPAR");
        RewriteRuleTokenStream stream_LPAR=new RewriteRuleTokenStream(adaptor,"token LPAR");
        RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:301:2: (open= LPAR expr close= RPAR -> ^( BLOCK[$open] expr TRUE[$close] ) | call )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:310:4: open= LPAR expr close= RPAR
                    {
                    open=(Token)match(input,LPAR,FOLLOW_LPAR_in_expr_atom1661); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAR.add(open);


                    pushFollow(FOLLOW_expr_in_expr_atom1663);
                    expr89=expr();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expr.add(expr89.getTree());

                    close=(Token)match(input,RPAR,FOLLOW_RPAR_in_expr_atom1667); if (state.failed) return retval; 
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
                    // 311:4: -> ^( BLOCK[$open] expr TRUE[$close] )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:311:7: ^( BLOCK[$open] expr TRUE[$close] )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:314:4: call
                    {
                    root_0 = (CtrlTree)adaptor.nil();


                    pushFollow(FOLLOW_call_in_expr_atom1695);
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:318:1: call : rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) ;
    public final CtrlParser.call_return call() throws RecognitionException {
        CtrlParser.call_return retval = new CtrlParser.call_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlParser.rule_name_return rule_name91 =null;

        CtrlParser.arg_list_return arg_list92 =null;


        RewriteRuleSubtreeStream stream_arg_list=new RewriteRuleSubtreeStream(adaptor,"rule arg_list");
        RewriteRuleSubtreeStream stream_rule_name=new RewriteRuleSubtreeStream(adaptor,"rule rule_name");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:319:2: ( rule_name ( arg_list )? -> ^( CALL[$rule_name.start] rule_name ( arg_list )? ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:323:4: rule_name ( arg_list )?
            {
            pushFollow(FOLLOW_rule_name_in_call1725);
            rule_name91=rule_name();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rule_name.add(rule_name91.getTree());

            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:323:14: ( arg_list )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==LPAR) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:323:14: arg_list
                    {
                    pushFollow(FOLLOW_arg_list_in_call1727);
                    arg_list92=arg_list();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg_list.add(arg_list92.getTree());

                    }
                    break;

            }


            if ( state.backtracking==0 ) { helper.registerCall((rule_name91!=null?((CtrlTree)rule_name91.tree):null)); }

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
            // 325:4: -> ^( CALL[$rule_name.start] rule_name ( arg_list )? )
            {
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:325:7: ^( CALL[$rule_name.start] rule_name ( arg_list )? )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(CALL, (rule_name91!=null?((Token)rule_name91.start):null))
                , root_1);

                adaptor.addChild(root_1, stream_rule_name.nextTree());

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:325:42: ( arg_list )?
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:331:1: arg_list : open= LPAR ( arg ( COMMA arg )* )? close= RPAR -> ^( ARGS[$open] ( arg )* RPAR[$close] ) ;
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:332:3: (open= LPAR ( arg ( COMMA arg )* )? close= RPAR -> ^( ARGS[$open] ( arg )* RPAR[$close] ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:334:5: open= LPAR ( arg ( COMMA arg )* )? close= RPAR
            {
            open=(Token)match(input,LPAR,FOLLOW_LPAR_in_arg_list1775); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAR.add(open);


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:334:15: ( arg ( COMMA arg )* )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==DONT_CARE||LA29_0==FALSE||LA29_0==ID||LA29_0==INT_LIT||LA29_0==OUT||LA29_0==REAL_LIT||(LA29_0 >= STRING_LIT && LA29_0 <= TRUE)) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:334:16: arg ( COMMA arg )*
                    {
                    pushFollow(FOLLOW_arg_in_arg_list1778);
                    arg93=arg();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arg.add(arg93.getTree());

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:334:20: ( COMMA arg )*
                    loop28:
                    do {
                        int alt28=2;
                        int LA28_0 = input.LA(1);

                        if ( (LA28_0==COMMA) ) {
                            alt28=1;
                        }


                        switch (alt28) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:334:21: COMMA arg
                    	    {
                    	    COMMA94=(Token)match(input,COMMA,FOLLOW_COMMA_in_arg_list1781); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA94);


                    	    pushFollow(FOLLOW_arg_in_arg_list1783);
                    	    arg95=arg();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_arg.add(arg95.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop28;
                        }
                    } while (true);


                    }
                    break;

            }


            close=(Token)match(input,RPAR,FOLLOW_RPAR_in_arg_list1791); if (state.failed) return retval; 
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
            // 335:5: -> ^( ARGS[$open] ( arg )* RPAR[$close] )
            {
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:335:8: ^( ARGS[$open] ( arg )* RPAR[$close] )
                {
                CtrlTree root_1 = (CtrlTree)adaptor.nil();
                root_1 = (CtrlTree)adaptor.becomeRoot(
                (CtrlTree)adaptor.create(ARGS, open)
                , root_1);

                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:335:22: ( arg )*
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:341:1: arg : ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) );
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:342:3: ( OUT ID -> ^( ARG OUT ID ) | ID -> ^( ARG ID ) | DONT_CARE -> ^( ARG DONT_CARE ) | literal -> ^( ARG literal ) )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:345:5: OUT ID
                    {
                    OUT96=(Token)match(input,OUT,FOLLOW_OUT_in_arg1838); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_OUT.add(OUT96);


                    ID97=(Token)match(input,ID,FOLLOW_ID_in_arg1840); if (state.failed) return retval; 
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
                    // 345:12: -> ^( ARG OUT ID )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:345:15: ^( ARG OUT ID )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:349:5: ID
                    {
                    ID98=(Token)match(input,ID,FOLLOW_ID_in_arg1871); if (state.failed) return retval; 
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
                    // 349:8: -> ^( ARG ID )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:349:11: ^( ARG ID )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:353:5: DONT_CARE
                    {
                    DONT_CARE99=(Token)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg1900); if (state.failed) return retval; 
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
                    // 353:15: -> ^( ARG DONT_CARE )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:353:18: ^( ARG DONT_CARE )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:354:5: literal
                    {
                    pushFollow(FOLLOW_literal_in_arg1917);
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
                    // 354:13: -> ^( ARG literal )
                    {
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:354:16: ^( ARG literal )
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:357:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlParser.literal_return literal() throws RecognitionException {
        CtrlParser.literal_return retval = new CtrlParser.literal_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token set101=null;

        CtrlTree set101_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:358:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:379:1: rule_name : qual_name[true] ->;
    public final CtrlParser.rule_name_return rule_name() throws RecognitionException {
        CtrlParser.rule_name_return retval = new CtrlParser.rule_name_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlParser.qual_name_return qual_name102 =null;


        RewriteRuleSubtreeStream stream_qual_name=new RewriteRuleSubtreeStream(adaptor,"rule qual_name");
        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:380:3: ( qual_name[true] ->)
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:380:5: qual_name[true]
            {
            pushFollow(FOLLOW_qual_name_in_rule_name2027);
            qual_name102=qual_name(true);

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
            // 381:5: ->
            {
                adaptor.addChild(root_0,  helper.qualify((qual_name102!=null?((CtrlTree)qual_name102.tree):null)) );

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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:385:1: var_decl : var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) ;
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
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:386:2: ( var_type ID ( COMMA ID )* -> ^( VAR var_type ( ID )+ ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:388:4: var_type ID ( COMMA ID )*
            {
            pushFollow(FOLLOW_var_type_in_var_decl2058);
            var_type103=var_type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_var_type.add(var_type103.getTree());

            ID104=(Token)match(input,ID,FOLLOW_ID_in_var_decl2060); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(ID104);


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:388:16: ( COMMA ID )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( (LA31_0==COMMA) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:388:17: COMMA ID
            	    {
            	    COMMA105=(Token)match(input,COMMA,FOLLOW_COMMA_in_var_decl2063); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA105);


            	    ID106=(Token)match(input,ID,FOLLOW_ID_in_var_decl2065); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ID106);


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
            // 388:28: -> ^( VAR var_type ( ID )+ )
            {
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:388:31: ^( VAR var_type ( ID )+ )
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:392:1: var_type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlParser.var_type_return var_type() throws RecognitionException {
        CtrlParser.var_type_return retval = new CtrlParser.var_type_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        Token set107=null;

        CtrlTree set107_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:393:2: ( NODE | BOOL | STRING | INT | REAL )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:
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
        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:232:33: ( ELSE )
        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:232:34: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred1_Ctrl1180); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred1_Ctrl

    // $ANTLR start synpred2_Ctrl
    public final void synpred2_Ctrl_fragment() throws RecognitionException {
        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:236:17: ( ELSE )
        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:236:18: ELSE
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred2_Ctrl1220); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred2_Ctrl

    // $ANTLR start synpred3_Ctrl
    public final void synpred3_Ctrl_fragment() throws RecognitionException {
        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:239:20: ( OR )
        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\Ctrl.g:239:21: OR
        {
        match(input,OR,FOLLOW_OR_in_synpred3_Ctrl1255); if (state.failed) return ;

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


 

    public static final BitSet FOLLOW_package_decl_in_program166 = new BitSet(new long[]{0x2280113974052250L,0x0000000000000059L});
    public static final BitSet FOLLOW_import_decl_in_program172 = new BitSet(new long[]{0x2280113974052250L,0x0000000000000059L});
    public static final BitSet FOLLOW_function_in_program180 = new BitSet(new long[]{0x2280113934052250L,0x0000000000000059L});
    public static final BitSet FOLLOW_recipe_in_program182 = new BitSet(new long[]{0x2280113934052250L,0x0000000000000059L});
    public static final BitSet FOLLOW_stat_in_program184 = new BitSet(new long[]{0x2280113934052250L,0x0000000000000059L});
    public static final BitSet FOLLOW_EOF_in_program188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_package_decl325 = new BitSet(new long[]{0x0000100010000240L});
    public static final BitSet FOLLOW_qual_name_in_package_decl327 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_SEMI_in_package_decl332 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_decl399 = new BitSet(new long[]{0x0000100010000240L});
    public static final BitSet FOLLOW_qual_name_in_import_decl402 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_SEMI_in_import_decl405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_qual_name429 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_DOT_in_qual_name433 = new BitSet(new long[]{0x0000100010000240L});
    public static final BitSet FOLLOW_qual_name_in_qual_name437 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERISK_in_qual_name476 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_DOT_in_qual_name478 = new BitSet(new long[]{0x0000100000000040L});
    public static final BitSet FOLLOW_ANY_in_qual_name498 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_qual_name521 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RECIPE_in_recipe570 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_recipe573 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_par_list_in_recipe575 = new BitSet(new long[]{0x0004001000000000L});
    public static final BitSet FOLLOW_PRIORITY_in_recipe578 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_INT_LIT_in_recipe581 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_block_in_recipe595 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function626 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_function629 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_par_list_in_function631 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_block_in_function644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_par_list675 = new BitSet(new long[]{0x0880210100002000L,0x0000000000000001L});
    public static final BitSet FOLLOW_par_in_par_list678 = new BitSet(new long[]{0x0800000000020000L});
    public static final BitSet FOLLOW_COMMA_in_par_list681 = new BitSet(new long[]{0x0080210100002000L,0x0000000000000001L});
    public static final BitSet FOLLOW_par_in_par_list683 = new BitSet(new long[]{0x0800000000020000L});
    public static final BitSet FOLLOW_RPAR_in_par_list689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_par734 = new BitSet(new long[]{0x0080010100002000L,0x0000000000000001L});
    public static final BitSet FOLLOW_var_type_in_par736 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_par738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_type_in_par771 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_par773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LCURLY_in_block812 = new BitSet(new long[]{0x20C0113930052250L,0x0000000000000059L});
    public static final BitSet FOLLOW_stat_in_block814 = new BitSet(new long[]{0x20C0113930052250L,0x0000000000000059L});
    public static final BitSet FOLLOW_RCURLY_in_block819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_stat851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat868 = new BitSet(new long[]{0x2080113930052250L,0x0000000000000059L});
    public static final BitSet FOLLOW_stat_in_stat871 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LANGLE_in_stat902 = new BitSet(new long[]{0x20A0113930052250L,0x0000000000000059L});
    public static final BitSet FOLLOW_stat_in_stat904 = new BitSet(new long[]{0x20A0113930052250L,0x0000000000000059L});
    public static final BitSet FOLLOW_RANGLE_in_stat909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_stat950 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat953 = new BitSet(new long[]{0x0000100010000240L,0x0000000000000004L});
    public static final BitSet FOLLOW_cond_in_stat956 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat958 = new BitSet(new long[]{0x2080113930052250L,0x0000000000000059L});
    public static final BitSet FOLLOW_stat_in_stat961 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat981 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat984 = new BitSet(new long[]{0x0000100010000240L,0x0000000000000004L});
    public static final BitSet FOLLOW_cond_in_stat987 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat989 = new BitSet(new long[]{0x2080113930052250L,0x0000000000000059L});
    public static final BitSet FOLLOW_stat_in_stat992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_stat997 = new BitSet(new long[]{0x2080113930052250L,0x0000000000000059L});
    public static final BitSet FOLLOW_stat_in_stat999 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000050L});
    public static final BitSet FOLLOW_WHILE_in_stat1042 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat1044 = new BitSet(new long[]{0x0000100010000240L,0x0000000000000004L});
    public static final BitSet FOLLOW_cond_in_stat1046 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat1048 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UNTIL_in_stat1111 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat1113 = new BitSet(new long[]{0x0000100010000240L,0x0000000000000004L});
    public static final BitSet FOLLOW_cond_in_stat1115 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat1117 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_stat1164 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_LPAR_in_stat1167 = new BitSet(new long[]{0x0000100010000240L,0x0000000000000004L});
    public static final BitSet FOLLOW_cond_in_stat1170 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_RPAR_in_stat1172 = new BitSet(new long[]{0x2080113930052250L,0x0000000000000059L});
    public static final BitSet FOLLOW_stat_in_stat1175 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_ELSE_in_stat1185 = new BitSet(new long[]{0x2080113930052250L,0x0000000000000059L});
    public static final BitSet FOLLOW_stat_in_stat1188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_stat1212 = new BitSet(new long[]{0x2080113930052250L,0x0000000000000059L});
    public static final BitSet FOLLOW_stat_in_stat1215 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_ELSE_in_stat1225 = new BitSet(new long[]{0x2080113930052250L,0x0000000000000059L});
    public static final BitSet FOLLOW_stat_in_stat1228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHOICE_in_stat1247 = new BitSet(new long[]{0x2080113930052250L,0x0000000000000059L});
    public static final BitSet FOLLOW_stat_in_stat1250 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_OR_in_stat1260 = new BitSet(new long[]{0x2080113930052250L,0x0000000000000059L});
    public static final BitSet FOLLOW_stat_in_stat1263 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_expr_in_stat1278 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat1280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat1295 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_SEMI_in_stat1297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cond_atom_in_cond1322 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_BAR_in_cond1331 = new BitSet(new long[]{0x0000100010000240L,0x0000000000000004L});
    public static final BitSet FOLLOW_cond_atom_in_cond1333 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_TRUE_in_cond_atom1379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_cond_atom1400 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr2_in_expr1430 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_BAR_in_expr1438 = new BitSet(new long[]{0x2000102010000240L});
    public static final BitSet FOLLOW_expr2_in_expr1440 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_expr_atom_in_expr21521 = new BitSet(new long[]{0x0002000000000202L});
    public static final BitSet FOLLOW_PLUS_in_expr21531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERISK_in_expr21558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_expr21613 = new BitSet(new long[]{0x0000102010000240L});
    public static final BitSet FOLLOW_expr_atom_in_expr21615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_atom1661 = new BitSet(new long[]{0x2000102010000240L});
    public static final BitSet FOLLOW_expr_in_expr_atom1663 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_RPAR_in_expr_atom1667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_expr_atom1695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_name_in_call1725 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_arg_list_in_call1727 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_arg_list1775 = new BitSet(new long[]{0x0900200212080000L,0x0000000000000006L});
    public static final BitSet FOLLOW_arg_in_arg_list1778 = new BitSet(new long[]{0x0800000000020000L});
    public static final BitSet FOLLOW_COMMA_in_arg_list1781 = new BitSet(new long[]{0x0100200212080000L,0x0000000000000006L});
    public static final BitSet FOLLOW_arg_in_arg_list1783 = new BitSet(new long[]{0x0800000000020000L});
    public static final BitSet FOLLOW_RPAR_in_arg_list1791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OUT_in_arg1838 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_arg1840 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_arg1871 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg1900 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_arg1917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qual_name_in_rule_name2027 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_type_in_var_decl2058 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_var_decl2060 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_COMMA_in_var_decl2063 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_var_decl2065 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_ELSE_in_synpred1_Ctrl1180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred2_Ctrl1220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_synpred3_Ctrl1255 = new BitSet(new long[]{0x0000000000000002L});

}