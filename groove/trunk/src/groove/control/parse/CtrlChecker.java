// $ANTLR 3.4 D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g 2014-01-12 20:42:10

package groove.control.parse;
import groove.control.*;
import groove.algebra.AlgebraFamily;
import groove.grammar.model.FormatErrorSet;
import groove.util.antlr.ParseTreeAdaptor;
import groove.util.antlr.ParseInfo;
import java.util.LinkedList;
import java.util.Stack;
import java.util.HashSet;
import java.util.HashMap;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class CtrlChecker extends TreeParser {
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
    public TreeParser[] getDelegates() {
        return new TreeParser[] {};
    }

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
    return adaptor;
}
    public String[] getTokenNames() { return CtrlChecker.tokenNames; }
    public String getGrammarFileName() { return "D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g"; }


        /** Helper class to convert AST trees to namespace. */
        private CtrlHelper helper;
        
        public void displayRecognitionError(String[] tokenNames,
                RecognitionException e) {
            String hdr = getErrorHeader(e);
            String msg = getErrorMessage(e, tokenNames);
            this.helper.addError(hdr + " " + msg, e.line, e.charPositionInLine);
        }

        /** Constructs a helper class, based on the given name space and algebra. */
        public void initialise(ParseInfo namespace) {
            this.helper = new CtrlHelper((Namespace) namespace);
        }


    public static class program_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "program"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:40:1: program : ^( PROGRAM package_decl ( import_decl )* functions recipes block ) ;
    public final CtrlChecker.program_return program() throws RecognitionException {
        CtrlChecker.program_return retval = new CtrlChecker.program_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PROGRAM1=null;
        CtrlChecker.package_decl_return package_decl2 =null;

        CtrlChecker.import_decl_return import_decl3 =null;

        CtrlChecker.functions_return functions4 =null;

        CtrlChecker.recipes_return recipes5 =null;

        CtrlChecker.block_return block6 =null;


        CtrlTree PROGRAM1_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:41:3: ( ^( PROGRAM package_decl ( import_decl )* functions recipes block ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:41:5: ^( PROGRAM package_decl ( import_decl )* functions recipes block )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            PROGRAM1=(CtrlTree)match(input,PROGRAM,FOLLOW_PROGRAM_in_program56); 


            if ( _first_0==null ) _first_0 = PROGRAM1;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_package_decl_in_program58);
            package_decl2=package_decl();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = package_decl2.tree;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:41:28: ( import_decl )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==IMPORT) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:41:28: import_decl
            	    {
            	    _last = (CtrlTree)input.LT(1);
            	    pushFollow(FOLLOW_import_decl_in_program60);
            	    import_decl3=import_decl();

            	    state._fsp--;

            	     
            	    if ( _first_1==null ) _first_1 = import_decl3.tree;


            	    retval.tree = (CtrlTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_functions_in_program63);
            functions4=functions();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = functions4.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_recipes_in_program65);
            recipes5=recipes();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = recipes5.tree;


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_program67);
            block6=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block6.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "program"


    public static class package_decl_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "package_decl"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:48:1: package_decl : ^( PACKAGE ID SEMI ) ;
    public final CtrlChecker.package_decl_return package_decl() throws RecognitionException {
        CtrlChecker.package_decl_return retval = new CtrlChecker.package_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PACKAGE7=null;
        CtrlTree ID8=null;
        CtrlTree SEMI9=null;

        CtrlTree PACKAGE7_tree=null;
        CtrlTree ID8_tree=null;
        CtrlTree SEMI9_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:49:3: ( ^( PACKAGE ID SEMI ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:49:5: ^( PACKAGE ID SEMI )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            PACKAGE7=(CtrlTree)match(input,PACKAGE,FOLLOW_PACKAGE_in_package_decl104); 


            if ( _first_0==null ) _first_0 = PACKAGE7;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID8=(CtrlTree)match(input,ID,FOLLOW_ID_in_package_decl106); 
             
            if ( _first_1==null ) _first_1 = ID8;


            _last = (CtrlTree)input.LT(1);
            SEMI9=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_package_decl108); 
             
            if ( _first_1==null ) _first_1 = SEMI9;


             helper.checkPackage(ID8); 

            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "package_decl"


    public static class import_decl_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "import_decl"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:54:1: import_decl : ^( IMPORT ID SEMI ) ;
    public final CtrlChecker.import_decl_return import_decl() throws RecognitionException {
        CtrlChecker.import_decl_return retval = new CtrlChecker.import_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree IMPORT10=null;
        CtrlTree ID11=null;
        CtrlTree SEMI12=null;

        CtrlTree IMPORT10_tree=null;
        CtrlTree ID11_tree=null;
        CtrlTree SEMI12_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:55:3: ( ^( IMPORT ID SEMI ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:55:5: ^( IMPORT ID SEMI )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            IMPORT10=(CtrlTree)match(input,IMPORT,FOLLOW_IMPORT_in_import_decl141); 


            if ( _first_0==null ) _first_0 = IMPORT10;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID11=(CtrlTree)match(input,ID,FOLLOW_ID_in_import_decl143); 
             
            if ( _first_1==null ) _first_1 = ID11;


            _last = (CtrlTree)input.LT(1);
            SEMI12=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_import_decl145); 
             
            if ( _first_1==null ) _first_1 = SEMI12;


             helper.checkImport(ID11); 

            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "import_decl"


    public static class recipes_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipes"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:60:1: recipes : ^( RECIPES ( recipe )* ) ;
    public final CtrlChecker.recipes_return recipes() throws RecognitionException {
        CtrlChecker.recipes_return retval = new CtrlChecker.recipes_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree RECIPES13=null;
        CtrlChecker.recipe_return recipe14 =null;


        CtrlTree RECIPES13_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:61:3: ( ^( RECIPES ( recipe )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:61:5: ^( RECIPES ( recipe )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            RECIPES13=(CtrlTree)match(input,RECIPES,FOLLOW_RECIPES_in_recipes175); 


            if ( _first_0==null ) _first_0 = RECIPES13;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:61:15: ( recipe )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==RECIPE) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:61:15: recipe
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_recipe_in_recipes177);
                	    recipe14=recipe();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = recipe14.tree;


                	    retval.tree = (CtrlTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop2;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }
            _last = _save_last_1;
            }


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "recipes"


    public static class recipe_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipe"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:64:1: recipe : ^( RECIPE ID ^( PARS ( par_decl )* ) ( INT_LIT )? block ) ;
    public final CtrlChecker.recipe_return recipe() throws RecognitionException {
        CtrlChecker.recipe_return retval = new CtrlChecker.recipe_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree RECIPE15=null;
        CtrlTree ID16=null;
        CtrlTree PARS17=null;
        CtrlTree INT_LIT19=null;
        CtrlChecker.par_decl_return par_decl18 =null;

        CtrlChecker.block_return block20 =null;


        CtrlTree RECIPE15_tree=null;
        CtrlTree ID16_tree=null;
        CtrlTree PARS17_tree=null;
        CtrlTree INT_LIT19_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:65:3: ( ^( RECIPE ID ^( PARS ( par_decl )* ) ( INT_LIT )? block ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:65:5: ^( RECIPE ID ^( PARS ( par_decl )* ) ( INT_LIT )? block )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            RECIPE15=(CtrlTree)match(input,RECIPE,FOLLOW_RECIPE_in_recipe194); 


            if ( _first_0==null ) _first_0 = RECIPE15;
             helper.startBody(RECIPE15); 

            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID16=(CtrlTree)match(input,ID,FOLLOW_ID_in_recipe213); 
             
            if ( _first_1==null ) _first_1 = ID16;


            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_2 = _last;
            CtrlTree _first_2 = null;
            _last = (CtrlTree)input.LT(1);
            PARS17=(CtrlTree)match(input,PARS,FOLLOW_PARS_in_recipe216); 


            if ( _first_1==null ) _first_1 = PARS17;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:67:18: ( par_decl )*
                loop3:
                do {
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==PAR) ) {
                        alt3=1;
                    }


                    switch (alt3) {
                	case 1 :
                	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:67:18: par_decl
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_par_decl_in_recipe218);
                	    par_decl18=par_decl();

                	    state._fsp--;

                	     
                	    if ( _first_2==null ) _first_2 = par_decl18.tree;


                	    retval.tree = (CtrlTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop3;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }
            _last = _save_last_2;
            }


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:67:29: ( INT_LIT )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==INT_LIT) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:67:29: INT_LIT
                    {
                    _last = (CtrlTree)input.LT(1);
                    INT_LIT19=(CtrlTree)match(input,INT_LIT,FOLLOW_INT_LIT_in_recipe222); 
                     
                    if ( _first_1==null ) _first_1 = INT_LIT19;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_recipe232);
            block20=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block20.tree;


             helper.endBody((block20!=null?((CtrlTree)block20.tree):null)); 

            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "recipe"


    public static class functions_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "functions"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:73:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final CtrlChecker.functions_return functions() throws RecognitionException {
        CtrlChecker.functions_return retval = new CtrlChecker.functions_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree FUNCTIONS21=null;
        CtrlChecker.function_return function22 =null;


        CtrlTree FUNCTIONS21_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:74:3: ( ^( FUNCTIONS ( function )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:74:5: ^( FUNCTIONS ( function )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            FUNCTIONS21=(CtrlTree)match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions263); 


            if ( _first_0==null ) _first_0 = FUNCTIONS21;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:74:17: ( function )*
                loop5:
                do {
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==FUNCTION) ) {
                        alt5=1;
                    }


                    switch (alt5) {
                	case 1 :
                	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:74:17: function
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_function_in_functions265);
                	    function22=function();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = function22.tree;


                	    retval.tree = (CtrlTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop5;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }
            _last = _save_last_1;
            }


             helper.reorderFunctions(FUNCTIONS21); 

            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "functions"


    public static class function_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:78:1: function : ^( FUNCTION ID ^( PARS ( par_decl )* ) ( INT_LIT )? block ) ;
    public final CtrlChecker.function_return function() throws RecognitionException {
        CtrlChecker.function_return retval = new CtrlChecker.function_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree FUNCTION23=null;
        CtrlTree ID24=null;
        CtrlTree PARS25=null;
        CtrlTree INT_LIT27=null;
        CtrlChecker.par_decl_return par_decl26 =null;

        CtrlChecker.block_return block28 =null;


        CtrlTree FUNCTION23_tree=null;
        CtrlTree ID24_tree=null;
        CtrlTree PARS25_tree=null;
        CtrlTree INT_LIT27_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:79:3: ( ^( FUNCTION ID ^( PARS ( par_decl )* ) ( INT_LIT )? block ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:79:5: ^( FUNCTION ID ^( PARS ( par_decl )* ) ( INT_LIT )? block )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            FUNCTION23=(CtrlTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_function288); 


            if ( _first_0==null ) _first_0 = FUNCTION23;
             helper.startBody(FUNCTION23); 

            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            ID24=(CtrlTree)match(input,ID,FOLLOW_ID_in_function306); 
             
            if ( _first_1==null ) _first_1 = ID24;


            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_2 = _last;
            CtrlTree _first_2 = null;
            _last = (CtrlTree)input.LT(1);
            PARS25=(CtrlTree)match(input,PARS,FOLLOW_PARS_in_function309); 


            if ( _first_1==null ) _first_1 = PARS25;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:81:18: ( par_decl )*
                loop6:
                do {
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==PAR) ) {
                        alt6=1;
                    }


                    switch (alt6) {
                	case 1 :
                	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:81:18: par_decl
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_par_decl_in_function311);
                	    par_decl26=par_decl();

                	    state._fsp--;

                	     
                	    if ( _first_2==null ) _first_2 = par_decl26.tree;


                	    retval.tree = (CtrlTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop6;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }
            _last = _save_last_2;
            }


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:81:29: ( INT_LIT )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==INT_LIT) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:81:29: INT_LIT
                    {
                    _last = (CtrlTree)input.LT(1);
                    INT_LIT27=(CtrlTree)match(input,INT_LIT,FOLLOW_INT_LIT_in_function315); 
                     
                    if ( _first_1==null ) _first_1 = INT_LIT27;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_function325);
            block28=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block28.tree;


             helper.endBody((block28!=null?((CtrlTree)block28.tree):null)); 

            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "function"


    public static class par_decl_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "par_decl"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:87:1: par_decl : ^( PAR ( OUT )? type ID ) ;
    public final CtrlChecker.par_decl_return par_decl() throws RecognitionException {
        CtrlChecker.par_decl_return retval = new CtrlChecker.par_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree PAR29=null;
        CtrlTree OUT30=null;
        CtrlTree ID32=null;
        CtrlChecker.type_return type31 =null;


        CtrlTree PAR29_tree=null;
        CtrlTree OUT30_tree=null;
        CtrlTree ID32_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:88:3: ( ^( PAR ( OUT )? type ID ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:88:5: ^( PAR ( OUT )? type ID )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            PAR29=(CtrlTree)match(input,PAR,FOLLOW_PAR_in_par_decl358); 


            if ( _first_0==null ) _first_0 = PAR29;
            match(input, Token.DOWN, null); 
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:88:11: ( OUT )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==OUT) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:88:11: OUT
                    {
                    _last = (CtrlTree)input.LT(1);
                    OUT30=(CtrlTree)match(input,OUT,FOLLOW_OUT_in_par_decl360); 
                     
                    if ( _first_1==null ) _first_1 = OUT30;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_type_in_par_decl363);
            type31=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type31.tree;


            _last = (CtrlTree)input.LT(1);
            ID32=(CtrlTree)match(input,ID,FOLLOW_ID_in_par_decl365); 
             
            if ( _first_1==null ) _first_1 = ID32;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             helper.declarePar(ID32, (type31!=null?((CtrlTree)type31.tree):null), OUT30); 

            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "par_decl"


    public static class block_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "block"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:92:1: block returns [ CtrlAut aut ] : ^( BLOCK ( stat )* ) ;
    public final CtrlChecker.block_return block() throws RecognitionException {
        CtrlChecker.block_return retval = new CtrlChecker.block_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree BLOCK33=null;
        CtrlChecker.stat_return stat34 =null;


        CtrlTree BLOCK33_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:93:3: ( ^( BLOCK ( stat )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:93:5: ^( BLOCK ( stat )* )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            BLOCK33=(CtrlTree)match(input,BLOCK,FOLLOW_BLOCK_in_block393); 


            if ( _first_0==null ) _first_0 = BLOCK33;
             helper.openScope(); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:95:8: ( stat )*
                loop9:
                do {
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==ALAP||LA9_0==ANY||LA9_0==ATOM||LA9_0==BLOCK||(LA9_0 >= CALL && LA9_0 <= CHOICE)||LA9_0==IF||LA9_0==OTHER||LA9_0==SEMI||LA9_0==STAR||(LA9_0 >= TRUE && LA9_0 <= UNTIL)||LA9_0==WHILE) ) {
                        alt9=1;
                    }


                    switch (alt9) {
                	case 1 :
                	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:95:8: stat
                	    {
                	    _last = (CtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_stat_in_block411);
                	    stat34=stat();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = stat34.tree;


                	    retval.tree = (CtrlTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop9;
                    }
                } while (true);


                 helper.closeScope(); 

                match(input, Token.UP, null); 
            }
            _last = _save_last_1;
            }


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "block"


    public static class stat_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "stat"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:100:1: stat : ( block | ^( SEMI var_decl ) | ^( SEMI stat ) | ^( ALAP stat ) | ^( ATOM stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE );
    public final CtrlChecker.stat_return stat() throws RecognitionException {
        CtrlChecker.stat_return retval = new CtrlChecker.stat_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree SEMI36=null;
        CtrlTree SEMI38=null;
        CtrlTree ALAP40=null;
        CtrlTree ATOM42=null;
        CtrlTree WHILE44=null;
        CtrlTree UNTIL47=null;
        CtrlTree TRY50=null;
        CtrlTree IF53=null;
        CtrlTree CHOICE57=null;
        CtrlTree STAR60=null;
        CtrlTree ANY63=null;
        CtrlTree OTHER64=null;
        CtrlTree TRUE65=null;
        CtrlChecker.block_return block35 =null;

        CtrlChecker.var_decl_return var_decl37 =null;

        CtrlChecker.stat_return stat39 =null;

        CtrlChecker.stat_return stat41 =null;

        CtrlChecker.stat_return stat43 =null;

        CtrlChecker.stat_return stat45 =null;

        CtrlChecker.stat_return stat46 =null;

        CtrlChecker.stat_return stat48 =null;

        CtrlChecker.stat_return stat49 =null;

        CtrlChecker.stat_return stat51 =null;

        CtrlChecker.stat_return stat52 =null;

        CtrlChecker.stat_return stat54 =null;

        CtrlChecker.stat_return stat55 =null;

        CtrlChecker.stat_return stat56 =null;

        CtrlChecker.stat_return stat58 =null;

        CtrlChecker.stat_return stat59 =null;

        CtrlChecker.stat_return stat61 =null;

        CtrlChecker.rule_return rule62 =null;


        CtrlTree SEMI36_tree=null;
        CtrlTree SEMI38_tree=null;
        CtrlTree ALAP40_tree=null;
        CtrlTree ATOM42_tree=null;
        CtrlTree WHILE44_tree=null;
        CtrlTree UNTIL47_tree=null;
        CtrlTree TRY50_tree=null;
        CtrlTree IF53_tree=null;
        CtrlTree CHOICE57_tree=null;
        CtrlTree STAR60_tree=null;
        CtrlTree ANY63_tree=null;
        CtrlTree OTHER64_tree=null;
        CtrlTree TRUE65_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:101:3: ( block | ^( SEMI var_decl ) | ^( SEMI stat ) | ^( ALAP stat ) | ^( ATOM stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE )
            int alt13=15;
            switch ( input.LA(1) ) {
            case BLOCK:
                {
                alt13=1;
                }
                break;
            case SEMI:
                {
                int LA13_2 = input.LA(2);

                if ( (LA13_2==DOWN) ) {
                    int LA13_15 = input.LA(3);

                    if ( (LA13_15==VAR) ) {
                        alt13=2;
                    }
                    else if ( (LA13_15==ALAP||LA13_15==ANY||LA13_15==ATOM||LA13_15==BLOCK||(LA13_15 >= CALL && LA13_15 <= CHOICE)||LA13_15==IF||LA13_15==OTHER||LA13_15==SEMI||LA13_15==STAR||(LA13_15 >= TRUE && LA13_15 <= UNTIL)||LA13_15==WHILE) ) {
                        alt13=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 13, 15, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 2, input);

                    throw nvae;

                }
                }
                break;
            case ALAP:
                {
                alt13=4;
                }
                break;
            case ATOM:
                {
                alt13=5;
                }
                break;
            case WHILE:
                {
                alt13=6;
                }
                break;
            case UNTIL:
                {
                alt13=7;
                }
                break;
            case TRY:
                {
                alt13=8;
                }
                break;
            case IF:
                {
                alt13=9;
                }
                break;
            case CHOICE:
                {
                alt13=10;
                }
                break;
            case STAR:
                {
                alt13=11;
                }
                break;
            case CALL:
                {
                alt13=12;
                }
                break;
            case ANY:
                {
                alt13=13;
                }
                break;
            case OTHER:
                {
                alt13=14;
                }
                break;
            case TRUE:
                {
                alt13=15;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;

            }

            switch (alt13) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:101:5: block
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_stat441);
                    block35=block();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = block35.tree;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:102:5: ^( SEMI var_decl )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    SEMI36=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_stat448); 


                    if ( _first_0==null ) _first_0 = SEMI36;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_var_decl_in_stat450);
                    var_decl37=var_decl();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = var_decl37.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:103:5: ^( SEMI stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    SEMI38=(CtrlTree)match(input,SEMI,FOLLOW_SEMI_in_stat458); 


                    if ( _first_0==null ) _first_0 = SEMI38;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat460);
                    stat39=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat39.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:104:5: ^( ALAP stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    ALAP40=(CtrlTree)match(input,ALAP,FOLLOW_ALAP_in_stat468); 


                    if ( _first_0==null ) _first_0 = ALAP40;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat470);
                    stat41=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat41.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 5 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:105:5: ^( ATOM stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    ATOM42=(CtrlTree)match(input,ATOM,FOLLOW_ATOM_in_stat478); 


                    if ( _first_0==null ) _first_0 = ATOM42;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat480);
                    stat43=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat43.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 6 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:106:5: ^( WHILE stat stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    WHILE44=(CtrlTree)match(input,WHILE,FOLLOW_WHILE_in_stat489); 


                    if ( _first_0==null ) _first_0 = WHILE44;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat498);
                    stat45=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat45.tree;


                     helper.startBranch(); 

                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat516);
                    stat46=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat46.tree;


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 7 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:112:5: ^( UNTIL stat stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    UNTIL47=(CtrlTree)match(input,UNTIL,FOLLOW_UNTIL_in_stat540); 


                    if ( _first_0==null ) _first_0 = UNTIL47;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat549);
                    stat48=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat48.tree;


                     helper.startBranch(); 

                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat567);
                    stat49=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat49.tree;


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 8 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:118:5: ^( TRY stat ( stat )? )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    TRY50=(CtrlTree)match(input,TRY,FOLLOW_TRY_in_stat591); 


                    if ( _first_0==null ) _first_0 = TRY50;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat609);
                    stat51=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat51.tree;


                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:121:8: ( stat )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==ALAP||LA10_0==ANY||LA10_0==ATOM||LA10_0==BLOCK||(LA10_0 >= CALL && LA10_0 <= CHOICE)||LA10_0==IF||LA10_0==OTHER||LA10_0==SEMI||LA10_0==STAR||(LA10_0 >= TRUE && LA10_0 <= UNTIL)||LA10_0==WHILE) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:121:10: stat
                            {
                             helper.nextBranch(); 

                            _last = (CtrlTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat631);
                            stat52=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = stat52.tree;


                            retval.tree = (CtrlTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 9 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:126:5: ^( IF stat stat ( stat )? )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    IF53=(CtrlTree)match(input,IF,FOLLOW_IF_in_stat665); 


                    if ( _first_0==null ) _first_0 = IF53;
                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat675);
                    stat54=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat54.tree;


                     helper.startBranch(); 

                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat694);
                    stat55=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat55.tree;


                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:130:8: ( stat )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==ALAP||LA11_0==ANY||LA11_0==ATOM||LA11_0==BLOCK||(LA11_0 >= CALL && LA11_0 <= CHOICE)||LA11_0==IF||LA11_0==OTHER||LA11_0==SEMI||LA11_0==STAR||(LA11_0 >= TRUE && LA11_0 <= UNTIL)||LA11_0==WHILE) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:130:10: stat
                            {
                             helper.nextBranch(); 

                            _last = (CtrlTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat717);
                            stat56=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = stat56.tree;


                            retval.tree = (CtrlTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 10 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:135:5: ^( CHOICE stat ( stat )* )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    CHOICE57=(CtrlTree)match(input,CHOICE,FOLLOW_CHOICE_in_stat751); 


                    if ( _first_0==null ) _first_0 = CHOICE57;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat769);
                    stat58=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat58.tree;


                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:138:8: ( stat )*
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==ALAP||LA12_0==ANY||LA12_0==ATOM||LA12_0==BLOCK||(LA12_0 >= CALL && LA12_0 <= CHOICE)||LA12_0==IF||LA12_0==OTHER||LA12_0==SEMI||LA12_0==STAR||(LA12_0 >= TRUE && LA12_0 <= UNTIL)||LA12_0==WHILE) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:138:10: stat
                    	    {
                    	     helper.nextBranch(); 

                    	    _last = (CtrlTree)input.LT(1);
                    	    pushFollow(FOLLOW_stat_in_stat792);
                    	    stat59=stat();

                    	    state._fsp--;

                    	     
                    	    if ( _first_1==null ) _first_1 = stat59.tree;


                    	    retval.tree = (CtrlTree)_first_0;
                    	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                    	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    	    }
                    	    break;

                    	default :
                    	    break loop12;
                        }
                    } while (true);


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     helper.endBranch(); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 11 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:143:5: ^( STAR stat )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_1 = _last;
                    CtrlTree _first_1 = null;
                    _last = (CtrlTree)input.LT(1);
                    STAR60=(CtrlTree)match(input,STAR,FOLLOW_STAR_in_stat828); 


                    if ( _first_0==null ) _first_0 = STAR60;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat846);
                    stat61=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat61.tree;


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 12 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:148:5: rule
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_stat868);
                    rule62=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule62.tree;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 13 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:149:5: ANY
                    {
                    _last = (CtrlTree)input.LT(1);
                    ANY63=(CtrlTree)match(input,ANY,FOLLOW_ANY_in_stat874); 
                     
                    if ( _first_0==null ) _first_0 = ANY63;


                     helper.checkAny(ANY63); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 14 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:151:5: OTHER
                    {
                    _last = (CtrlTree)input.LT(1);
                    OTHER64=(CtrlTree)match(input,OTHER,FOLLOW_OTHER_in_stat886); 
                     
                    if ( _first_0==null ) _first_0 = OTHER64;


                     helper.checkOther(OTHER64); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 15 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:153:5: TRUE
                    {
                    _last = (CtrlTree)input.LT(1);
                    TRUE65=(CtrlTree)match(input,TRUE,FOLLOW_TRUE_in_stat898); 
                     
                    if ( _first_0==null ) _first_0 = TRUE65;


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "stat"


    public static class rule_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "rule"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:156:1: rule : ^( CALL id= ID ( ^( ARGS ( arg )* RPAR ) )? ) ;
    public final CtrlChecker.rule_return rule() throws RecognitionException {
        CtrlChecker.rule_return retval = new CtrlChecker.rule_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree id=null;
        CtrlTree CALL66=null;
        CtrlTree ARGS67=null;
        CtrlTree RPAR69=null;
        CtrlChecker.arg_return arg68 =null;


        CtrlTree id_tree=null;
        CtrlTree CALL66_tree=null;
        CtrlTree ARGS67_tree=null;
        CtrlTree RPAR69_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:158:3: ( ^( CALL id= ID ( ^( ARGS ( arg )* RPAR ) )? ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:158:5: ^( CALL id= ID ( ^( ARGS ( arg )* RPAR ) )? )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            CALL66=(CtrlTree)match(input,CALL,FOLLOW_CALL_in_rule916); 


            if ( _first_0==null ) _first_0 = CALL66;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            id=(CtrlTree)match(input,ID,FOLLOW_ID_in_rule920); 
             
            if ( _first_1==null ) _first_1 = id;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:158:18: ( ^( ARGS ( arg )* RPAR ) )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==ARGS) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:158:19: ^( ARGS ( arg )* RPAR )
                    {
                    _last = (CtrlTree)input.LT(1);
                    {
                    CtrlTree _save_last_2 = _last;
                    CtrlTree _first_2 = null;
                    _last = (CtrlTree)input.LT(1);
                    ARGS67=(CtrlTree)match(input,ARGS,FOLLOW_ARGS_in_rule924); 


                    if ( _first_1==null ) _first_1 = ARGS67;
                    match(input, Token.DOWN, null); 
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:158:26: ( arg )*
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==ARG) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:158:26: arg
                    	    {
                    	    _last = (CtrlTree)input.LT(1);
                    	    pushFollow(FOLLOW_arg_in_rule926);
                    	    arg68=arg();

                    	    state._fsp--;

                    	     
                    	    if ( _first_2==null ) _first_2 = arg68.tree;


                    	    retval.tree = (CtrlTree)_first_0;
                    	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                    	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    	    }
                    	    break;

                    	default :
                    	    break loop14;
                        }
                    } while (true);


                    _last = (CtrlTree)input.LT(1);
                    RPAR69=(CtrlTree)match(input,RPAR,FOLLOW_RPAR_in_rule929); 
                     
                    if ( _first_2==null ) _first_2 = RPAR69;


                    match(input, Token.UP, null); 
                    _last = _save_last_2;
                    }


                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            }

             helper.checkCall(((CtrlTree)retval.tree)); 
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "rule"


    public static class var_decl_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_decl"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:161:1: var_decl : ^( VAR type ( ID )+ ) ;
    public final CtrlChecker.var_decl_return var_decl() throws RecognitionException {
        CtrlChecker.var_decl_return retval = new CtrlChecker.var_decl_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree VAR70=null;
        CtrlTree ID72=null;
        CtrlChecker.type_return type71 =null;


        CtrlTree VAR70_tree=null;
        CtrlTree ID72_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:162:2: ( ^( VAR type ( ID )+ ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:162:4: ^( VAR type ( ID )+ )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            VAR70=(CtrlTree)match(input,VAR,FOLLOW_VAR_in_var_decl947); 


            if ( _first_0==null ) _first_0 = VAR70;
            match(input, Token.DOWN, null); 
            _last = (CtrlTree)input.LT(1);
            pushFollow(FOLLOW_type_in_var_decl949);
            type71=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type71.tree;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:164:7: ( ID )+
            int cnt16=0;
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==ID) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:164:9: ID
            	    {
            	    _last = (CtrlTree)input.LT(1);
            	    ID72=(CtrlTree)match(input,ID,FOLLOW_ID_in_var_decl966); 
            	     
            	    if ( _first_1==null ) _first_1 = ID72;


            	     helper.declareVar(ID72, (type71!=null?((CtrlTree)type71.tree):null)); 

            	    retval.tree = (CtrlTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            	    }
            	    break;

            	default :
            	    if ( cnt16 >= 1 ) break loop16;
                        EarlyExitException eee =
                            new EarlyExitException(16, input);
                        throw eee;
                }
                cnt16++;
            } while (true);


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "var_decl"


    public static class type_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "type"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:170:1: type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlChecker.type_return type() throws RecognitionException {
        CtrlChecker.type_return retval = new CtrlChecker.type_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree set73=null;

        CtrlTree set73_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:171:3: ( NODE | BOOL | STRING | INT | REAL )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:
            {
            _last = (CtrlTree)input.LT(1);
            set73=(CtrlTree)input.LT(1);

            if ( input.LA(1)==BOOL||input.LA(1)==INT||input.LA(1)==NODE||input.LA(1)==REAL||input.LA(1)==STRING ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);
             

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "type"


    public static class arg_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:178:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final CtrlChecker.arg_return arg() throws RecognitionException {
        CtrlChecker.arg_return retval = new CtrlChecker.arg_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree ARG74=null;
        CtrlTree OUT75=null;
        CtrlTree ID76=null;
        CtrlTree DONT_CARE77=null;
        CtrlChecker.literal_return literal78 =null;


        CtrlTree ARG74_tree=null;
        CtrlTree OUT75_tree=null;
        CtrlTree ID76_tree=null;
        CtrlTree DONT_CARE77_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:179:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:179:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
            _last = (CtrlTree)input.LT(1);
            {
            CtrlTree _save_last_1 = _last;
            CtrlTree _first_1 = null;
            _last = (CtrlTree)input.LT(1);
            ARG74=(CtrlTree)match(input,ARG,FOLLOW_ARG_in_arg1045); 


            if ( _first_0==null ) _first_0 = ARG74;
            match(input, Token.DOWN, null); 
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:180:7: ( ( OUT )? ID | DONT_CARE | literal )
            int alt18=3;
            switch ( input.LA(1) ) {
            case ID:
            case OUT:
                {
                alt18=1;
                }
                break;
            case DONT_CARE:
                {
                alt18=2;
                }
                break;
            case FALSE:
            case INT_LIT:
            case REAL_LIT:
            case STRING_LIT:
            case TRUE:
                {
                alt18=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;

            }

            switch (alt18) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:180:9: ( OUT )? ID
                    {
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:180:9: ( OUT )?
                    int alt17=2;
                    int LA17_0 = input.LA(1);

                    if ( (LA17_0==OUT) ) {
                        alt17=1;
                    }
                    switch (alt17) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:180:9: OUT
                            {
                            _last = (CtrlTree)input.LT(1);
                            OUT75=(CtrlTree)match(input,OUT,FOLLOW_OUT_in_arg1056); 
                             
                            if ( _first_1==null ) _first_1 = OUT75;


                            retval.tree = (CtrlTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    _last = (CtrlTree)input.LT(1);
                    ID76=(CtrlTree)match(input,ID,FOLLOW_ID_in_arg1059); 
                     
                    if ( _first_1==null ) _first_1 = ID76;


                     helper.checkVarArg(ARG74); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:181:9: DONT_CARE
                    {
                    _last = (CtrlTree)input.LT(1);
                    DONT_CARE77=(CtrlTree)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg1071); 
                     
                    if ( _first_1==null ) _first_1 = DONT_CARE77;


                     helper.checkDontCareArg(ARG74); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:182:9: literal
                    {
                    _last = (CtrlTree)input.LT(1);
                    pushFollow(FOLLOW_literal_in_arg1083);
                    literal78=literal();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = literal78.tree;


                     helper.checkConstArg(ARG74); 

                    retval.tree = (CtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "arg"


    public static class literal_return extends TreeRuleReturnScope {
        CtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "literal"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:187:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlChecker.literal_return literal() throws RecognitionException {
        CtrlChecker.literal_return retval = new CtrlChecker.literal_return();
        retval.start = input.LT(1);


        CtrlTree root_0 = null;

        CtrlTree _first_0 = null;
        CtrlTree _last = null;

        CtrlTree set79=null;

        CtrlTree set79_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:188:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:
            {
            _last = (CtrlTree)input.LT(1);
            set79=(CtrlTree)input.LT(1);

            if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            retval.tree = (CtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CtrlTree)adaptor.getParent(retval.tree);
             

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "literal"

    // Delegated rules


 

    public static final BitSet FOLLOW_PROGRAM_in_program56 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_package_decl_in_program58 = new BitSet(new long[]{0x0000000048000000L});
    public static final BitSet FOLLOW_import_decl_in_program60 = new BitSet(new long[]{0x0000000048000000L});
    public static final BitSet FOLLOW_functions_in_program63 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_recipes_in_program65 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_program67 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PACKAGE_in_package_decl104 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_package_decl106 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_SEMI_in_package_decl108 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IMPORT_in_import_decl141 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_import_decl143 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_SEMI_in_import_decl145 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RECIPES_in_recipes175 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_recipe_in_recipes177 = new BitSet(new long[]{0x0040000000000008L});
    public static final BitSet FOLLOW_RECIPE_in_recipe194 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_recipe213 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_PARS_in_recipe216 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_par_decl_in_recipe218 = new BitSet(new long[]{0x0000200000000008L});
    public static final BitSet FOLLOW_INT_LIT_in_recipe222 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_recipe232 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions263 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions265 = new BitSet(new long[]{0x0000000004000008L});
    public static final BitSet FOLLOW_FUNCTION_in_function288 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function306 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_PARS_in_function309 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_par_decl_in_function311 = new BitSet(new long[]{0x0000200000000008L});
    public static final BitSet FOLLOW_INT_LIT_in_function315 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_function325 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PAR_in_par_decl358 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_par_decl360 = new BitSet(new long[]{0x2010004080002000L});
    public static final BitSet FOLLOW_type_in_par_decl363 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_par_decl365 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block393 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block411 = new BitSet(new long[]{0x9200040020019458L,0x000000000000000BL});
    public static final BitSet FOLLOW_block_in_stat441 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_stat448 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_decl_in_stat450 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SEMI_in_stat458 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat460 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALAP_in_stat468 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat470 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ATOM_in_stat478 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat480 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat489 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat498 = new BitSet(new long[]{0x9200040020019450L,0x000000000000000BL});
    public static final BitSet FOLLOW_stat_in_stat516 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat540 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat549 = new BitSet(new long[]{0x9200040020019450L,0x000000000000000BL});
    public static final BitSet FOLLOW_stat_in_stat567 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat591 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat609 = new BitSet(new long[]{0x9200040020019458L,0x000000000000000BL});
    public static final BitSet FOLLOW_stat_in_stat631 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat665 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat675 = new BitSet(new long[]{0x9200040020019450L,0x000000000000000BL});
    public static final BitSet FOLLOW_stat_in_stat694 = new BitSet(new long[]{0x9200040020019458L,0x000000000000000BL});
    public static final BitSet FOLLOW_stat_in_stat717 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat751 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat769 = new BitSet(new long[]{0x9200040020019458L,0x000000000000000BL});
    public static final BitSet FOLLOW_stat_in_stat792 = new BitSet(new long[]{0x9200040020019458L,0x000000000000000BL});
    public static final BitSet FOLLOW_STAR_in_stat828 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat846 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat874 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat886 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule916 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule920 = new BitSet(new long[]{0x0000000000000108L});
    public static final BitSet FOLLOW_ARGS_in_rule924 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_arg_in_rule926 = new BitSet(new long[]{0x0100000000000080L});
    public static final BitSet FOLLOW_RPAR_in_rule929 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VAR_in_var_decl947 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl949 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_var_decl966 = new BitSet(new long[]{0x0000000010000008L});
    public static final BitSet FOLLOW_ARG_in_arg1045 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg1056 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_arg1059 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg1071 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg1083 = new BitSet(new long[]{0x0000000000000008L});

}