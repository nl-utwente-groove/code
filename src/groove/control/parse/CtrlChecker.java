// $ANTLR 3.4 D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g 2014-01-05 18:48:00

package groove.control.parse;
import groove.control.*;
import groove.control.CtrlEdge.Kind;
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
        
        /**
         * Runs the lexer and parser on a given input character stream,
         * with a (presumably empty) namespace.
         * @return the resulting syntax tree
         */
        public NewCtrlTree run(NewCtrlTree tree, Namespace namespace) throws RecognitionException {
            this.helper = new CtrlHelper(namespace);
            ParseTreeAdaptor treeAdaptor = new ParseTreeAdaptor(new NewCtrlTree());
            setTreeAdaptor(treeAdaptor);
            setTreeNodeStream(treeAdaptor.createTreeNodeStream(tree));
            return (NewCtrlTree) program().getTree();
        }


    public static class program_return extends TreeRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "program"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:54:1: program : ^( PROGRAM package_decl ( import_decl )* functions recipes block ) ;
    public final CtrlChecker.program_return program() throws RecognitionException {
        CtrlChecker.program_return retval = new CtrlChecker.program_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree PROGRAM1=null;
        CtrlChecker.package_decl_return package_decl2 =null;

        CtrlChecker.import_decl_return import_decl3 =null;

        CtrlChecker.functions_return functions4 =null;

        CtrlChecker.recipes_return recipes5 =null;

        CtrlChecker.block_return block6 =null;


        NewCtrlTree PROGRAM1_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:55:3: ( ^( PROGRAM package_decl ( import_decl )* functions recipes block ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:55:5: ^( PROGRAM package_decl ( import_decl )* functions recipes block )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            PROGRAM1=(NewCtrlTree)match(input,PROGRAM,FOLLOW_PROGRAM_in_program56); 


            if ( _first_0==null ) _first_0 = PROGRAM1;
            match(input, Token.DOWN, null); 
            _last = (NewCtrlTree)input.LT(1);
            pushFollow(FOLLOW_package_decl_in_program58);
            package_decl2=package_decl();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = package_decl2.tree;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:55:28: ( import_decl )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==IMPORT) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:55:28: import_decl
            	    {
            	    _last = (NewCtrlTree)input.LT(1);
            	    pushFollow(FOLLOW_import_decl_in_program60);
            	    import_decl3=import_decl();

            	    state._fsp--;

            	     
            	    if ( _first_1==null ) _first_1 = import_decl3.tree;


            	    retval.tree = (NewCtrlTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            _last = (NewCtrlTree)input.LT(1);
            pushFollow(FOLLOW_functions_in_program63);
            functions4=functions();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = functions4.tree;


            _last = (NewCtrlTree)input.LT(1);
            pushFollow(FOLLOW_recipes_in_program65);
            recipes5=recipes();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = recipes5.tree;


            _last = (NewCtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_program67);
            block6=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block6.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             if ((block6!=null?((NewCtrlTree)block6.tree):null).getChildCount() == 0) {
                      helper.checkAny(PROGRAM1);
                  }
                

            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "package_decl"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:62:1: package_decl : ^( PACKAGE ID SEMI ) ;
    public final CtrlChecker.package_decl_return package_decl() throws RecognitionException {
        CtrlChecker.package_decl_return retval = new CtrlChecker.package_decl_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree PACKAGE7=null;
        NewCtrlTree ID8=null;
        NewCtrlTree SEMI9=null;

        NewCtrlTree PACKAGE7_tree=null;
        NewCtrlTree ID8_tree=null;
        NewCtrlTree SEMI9_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:63:3: ( ^( PACKAGE ID SEMI ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:63:5: ^( PACKAGE ID SEMI )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            PACKAGE7=(NewCtrlTree)match(input,PACKAGE,FOLLOW_PACKAGE_in_package_decl90); 


            if ( _first_0==null ) _first_0 = PACKAGE7;
            match(input, Token.DOWN, null); 
            _last = (NewCtrlTree)input.LT(1);
            ID8=(NewCtrlTree)match(input,ID,FOLLOW_ID_in_package_decl92); 
             
            if ( _first_1==null ) _first_1 = ID8;


            _last = (NewCtrlTree)input.LT(1);
            SEMI9=(NewCtrlTree)match(input,SEMI,FOLLOW_SEMI_in_package_decl94); 
             
            if ( _first_1==null ) _first_1 = SEMI9;


             helper.checkPackage(ID8); 

            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "import_decl"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:68:1: import_decl : ^( IMPORT ID SEMI ) ;
    public final CtrlChecker.import_decl_return import_decl() throws RecognitionException {
        CtrlChecker.import_decl_return retval = new CtrlChecker.import_decl_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree IMPORT10=null;
        NewCtrlTree ID11=null;
        NewCtrlTree SEMI12=null;

        NewCtrlTree IMPORT10_tree=null;
        NewCtrlTree ID11_tree=null;
        NewCtrlTree SEMI12_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:69:3: ( ^( IMPORT ID SEMI ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:69:5: ^( IMPORT ID SEMI )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            IMPORT10=(NewCtrlTree)match(input,IMPORT,FOLLOW_IMPORT_in_import_decl127); 


            if ( _first_0==null ) _first_0 = IMPORT10;
            match(input, Token.DOWN, null); 
            _last = (NewCtrlTree)input.LT(1);
            ID11=(NewCtrlTree)match(input,ID,FOLLOW_ID_in_import_decl129); 
             
            if ( _first_1==null ) _first_1 = ID11;


            _last = (NewCtrlTree)input.LT(1);
            SEMI12=(NewCtrlTree)match(input,SEMI,FOLLOW_SEMI_in_import_decl131); 
             
            if ( _first_1==null ) _first_1 = SEMI12;


             helper.checkImport(ID11); 

            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipes"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:74:1: recipes : ^( RECIPES ( recipe )* ) ;
    public final CtrlChecker.recipes_return recipes() throws RecognitionException {
        CtrlChecker.recipes_return retval = new CtrlChecker.recipes_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree RECIPES13=null;
        CtrlChecker.recipe_return recipe14 =null;


        NewCtrlTree RECIPES13_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:75:3: ( ^( RECIPES ( recipe )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:75:5: ^( RECIPES ( recipe )* )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            RECIPES13=(NewCtrlTree)match(input,RECIPES,FOLLOW_RECIPES_in_recipes161); 


            if ( _first_0==null ) _first_0 = RECIPES13;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:75:15: ( recipe )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==RECIPE) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:75:15: recipe
                	    {
                	    _last = (NewCtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_recipe_in_recipes163);
                	    recipe14=recipe();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = recipe14.tree;


                	    retval.tree = (NewCtrlTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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


            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "recipe"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:78:1: recipe : ^( RECIPE ID ( INT_LIT )? block ) ;
    public final CtrlChecker.recipe_return recipe() throws RecognitionException {
        CtrlChecker.recipe_return retval = new CtrlChecker.recipe_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree RECIPE15=null;
        NewCtrlTree ID16=null;
        NewCtrlTree INT_LIT17=null;
        CtrlChecker.block_return block18 =null;


        NewCtrlTree RECIPE15_tree=null;
        NewCtrlTree ID16_tree=null;
        NewCtrlTree INT_LIT17_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:79:3: ( ^( RECIPE ID ( INT_LIT )? block ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:79:5: ^( RECIPE ID ( INT_LIT )? block )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            RECIPE15=(NewCtrlTree)match(input,RECIPE,FOLLOW_RECIPE_in_recipe180); 


            if ( _first_0==null ) _first_0 = RECIPE15;
            match(input, Token.DOWN, null); 
            _last = (NewCtrlTree)input.LT(1);
            ID16=(NewCtrlTree)match(input,ID,FOLLOW_ID_in_recipe182); 
             
            if ( _first_1==null ) _first_1 = ID16;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:79:18: ( INT_LIT )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==INT_LIT) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:79:18: INT_LIT
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    INT_LIT17=(NewCtrlTree)match(input,INT_LIT,FOLLOW_INT_LIT_in_recipe184); 
                     
                    if ( _first_1==null ) _first_1 = INT_LIT17;


                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


             helper.startBody(ID16, Kind.RECIPE); 

            _last = (NewCtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_recipe204);
            block18=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block18.tree;


             helper.endBody(); 

            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "functions"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:86:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final CtrlChecker.functions_return functions() throws RecognitionException {
        CtrlChecker.functions_return retval = new CtrlChecker.functions_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree FUNCTIONS19=null;
        CtrlChecker.function_return function20 =null;


        NewCtrlTree FUNCTIONS19_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:87:3: ( ^( FUNCTIONS ( function )* ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:87:5: ^( FUNCTIONS ( function )* )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            FUNCTIONS19=(NewCtrlTree)match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions235); 


            if ( _first_0==null ) _first_0 = FUNCTIONS19;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:87:17: ( function )*
                loop4:
                do {
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==FUNCTION) ) {
                        alt4=1;
                    }


                    switch (alt4) {
                	case 1 :
                	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:87:17: function
                	    {
                	    _last = (NewCtrlTree)input.LT(1);
                	    pushFollow(FOLLOW_function_in_functions237);
                	    function20=function();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = function20.tree;


                	    retval.tree = (NewCtrlTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop4;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }
            _last = _save_last_1;
            }


             helper.reorderFunctions(FUNCTIONS19); 

            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:91:1: function : ^( FUNCTION ID block ) ;
    public final CtrlChecker.function_return function() throws RecognitionException {
        CtrlChecker.function_return retval = new CtrlChecker.function_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree FUNCTION21=null;
        NewCtrlTree ID22=null;
        CtrlChecker.block_return block23 =null;


        NewCtrlTree FUNCTION21_tree=null;
        NewCtrlTree ID22_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:92:3: ( ^( FUNCTION ID block ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:92:5: ^( FUNCTION ID block )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            FUNCTION21=(NewCtrlTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_function260); 


            if ( _first_0==null ) _first_0 = FUNCTION21;
            match(input, Token.DOWN, null); 
            _last = (NewCtrlTree)input.LT(1);
            ID22=(NewCtrlTree)match(input,ID,FOLLOW_ID_in_function262); 
             
            if ( _first_1==null ) _first_1 = ID22;


             helper.startBody(ID22, Kind.FUNCTION); 

            _last = (NewCtrlTree)input.LT(1);
            pushFollow(FOLLOW_block_in_function281);
            block23=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block23.tree;


             helper.endBody(); 

            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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


    public static class block_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "block"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:99:1: block returns [ CtrlAut aut ] : ^( BLOCK ( stat )* RCURLY ) ;
    public final CtrlChecker.block_return block() throws RecognitionException {
        CtrlChecker.block_return retval = new CtrlChecker.block_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree BLOCK24=null;
        NewCtrlTree RCURLY26=null;
        CtrlChecker.stat_return stat25 =null;


        NewCtrlTree BLOCK24_tree=null;
        NewCtrlTree RCURLY26_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:100:3: ( ^( BLOCK ( stat )* RCURLY ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:100:5: ^( BLOCK ( stat )* RCURLY )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            BLOCK24=(NewCtrlTree)match(input,BLOCK,FOLLOW_BLOCK_in_block319); 


            if ( _first_0==null ) _first_0 = BLOCK24;
             helper.openScope(); 

            match(input, Token.DOWN, null); 
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:102:8: ( stat )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==ALAP||LA5_0==ANY||LA5_0==BLOCK||(LA5_0 >= CALL && LA5_0 <= CHOICE)||LA5_0==IF||LA5_0==OTHER||LA5_0==SEMI||LA5_0==STAR||(LA5_0 >= TRUE && LA5_0 <= UNTIL)||LA5_0==WHILE) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:102:8: stat
            	    {
            	    _last = (NewCtrlTree)input.LT(1);
            	    pushFollow(FOLLOW_stat_in_block337);
            	    stat25=stat();

            	    state._fsp--;

            	     
            	    if ( _first_1==null ) _first_1 = stat25.tree;


            	    retval.tree = (NewCtrlTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


             helper.closeScope(); 

            _last = (NewCtrlTree)input.LT(1);
            RCURLY26=(NewCtrlTree)match(input,RCURLY,FOLLOW_RCURLY_in_block356); 
             
            if ( _first_1==null ) _first_1 = RCURLY26;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "stat"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:108:1: stat : ( block | ^( SEMI var_decl ) | ^( SEMI stat ) | ^( ALAP stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE );
    public final CtrlChecker.stat_return stat() throws RecognitionException {
        CtrlChecker.stat_return retval = new CtrlChecker.stat_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree SEMI28=null;
        NewCtrlTree SEMI30=null;
        NewCtrlTree ALAP32=null;
        NewCtrlTree WHILE34=null;
        NewCtrlTree UNTIL37=null;
        NewCtrlTree TRY40=null;
        NewCtrlTree IF43=null;
        NewCtrlTree CHOICE47=null;
        NewCtrlTree STAR50=null;
        NewCtrlTree ANY53=null;
        NewCtrlTree OTHER54=null;
        NewCtrlTree TRUE55=null;
        CtrlChecker.block_return block27 =null;

        CtrlChecker.var_decl_return var_decl29 =null;

        CtrlChecker.stat_return stat31 =null;

        CtrlChecker.stat_return stat33 =null;

        CtrlChecker.stat_return stat35 =null;

        CtrlChecker.stat_return stat36 =null;

        CtrlChecker.stat_return stat38 =null;

        CtrlChecker.stat_return stat39 =null;

        CtrlChecker.stat_return stat41 =null;

        CtrlChecker.stat_return stat42 =null;

        CtrlChecker.stat_return stat44 =null;

        CtrlChecker.stat_return stat45 =null;

        CtrlChecker.stat_return stat46 =null;

        CtrlChecker.stat_return stat48 =null;

        CtrlChecker.stat_return stat49 =null;

        CtrlChecker.stat_return stat51 =null;

        CtrlChecker.rule_return rule52 =null;


        NewCtrlTree SEMI28_tree=null;
        NewCtrlTree SEMI30_tree=null;
        NewCtrlTree ALAP32_tree=null;
        NewCtrlTree WHILE34_tree=null;
        NewCtrlTree UNTIL37_tree=null;
        NewCtrlTree TRY40_tree=null;
        NewCtrlTree IF43_tree=null;
        NewCtrlTree CHOICE47_tree=null;
        NewCtrlTree STAR50_tree=null;
        NewCtrlTree ANY53_tree=null;
        NewCtrlTree OTHER54_tree=null;
        NewCtrlTree TRUE55_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:109:3: ( block | ^( SEMI var_decl ) | ^( SEMI stat ) | ^( ALAP stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE )
            int alt9=14;
            switch ( input.LA(1) ) {
            case BLOCK:
                {
                alt9=1;
                }
                break;
            case SEMI:
                {
                int LA9_2 = input.LA(2);

                if ( (LA9_2==DOWN) ) {
                    int LA9_14 = input.LA(3);

                    if ( (LA9_14==VAR) ) {
                        alt9=2;
                    }
                    else if ( (LA9_14==ALAP||LA9_14==ANY||LA9_14==BLOCK||(LA9_14 >= CALL && LA9_14 <= CHOICE)||LA9_14==IF||LA9_14==OTHER||LA9_14==SEMI||LA9_14==STAR||(LA9_14 >= TRUE && LA9_14 <= UNTIL)||LA9_14==WHILE) ) {
                        alt9=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 9, 14, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 2, input);

                    throw nvae;

                }
                }
                break;
            case ALAP:
                {
                alt9=4;
                }
                break;
            case WHILE:
                {
                alt9=5;
                }
                break;
            case UNTIL:
                {
                alt9=6;
                }
                break;
            case TRY:
                {
                alt9=7;
                }
                break;
            case IF:
                {
                alt9=8;
                }
                break;
            case CHOICE:
                {
                alt9=9;
                }
                break;
            case STAR:
                {
                alt9=10;
                }
                break;
            case CALL:
                {
                alt9=11;
                }
                break;
            case ANY:
                {
                alt9=12;
                }
                break;
            case OTHER:
                {
                alt9=13;
                }
                break;
            case TRUE:
                {
                alt9=14;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;

            }

            switch (alt9) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:109:5: block
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_stat376);
                    block27=block();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = block27.tree;


                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:110:5: ^( SEMI var_decl )
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    {
                    NewCtrlTree _save_last_1 = _last;
                    NewCtrlTree _first_1 = null;
                    _last = (NewCtrlTree)input.LT(1);
                    SEMI28=(NewCtrlTree)match(input,SEMI,FOLLOW_SEMI_in_stat383); 


                    if ( _first_0==null ) _first_0 = SEMI28;
                    match(input, Token.DOWN, null); 
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_var_decl_in_stat385);
                    var_decl29=var_decl();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = var_decl29.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:111:5: ^( SEMI stat )
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    {
                    NewCtrlTree _save_last_1 = _last;
                    NewCtrlTree _first_1 = null;
                    _last = (NewCtrlTree)input.LT(1);
                    SEMI30=(NewCtrlTree)match(input,SEMI,FOLLOW_SEMI_in_stat393); 


                    if ( _first_0==null ) _first_0 = SEMI30;
                    match(input, Token.DOWN, null); 
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat395);
                    stat31=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat31.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:112:5: ^( ALAP stat )
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    {
                    NewCtrlTree _save_last_1 = _last;
                    NewCtrlTree _first_1 = null;
                    _last = (NewCtrlTree)input.LT(1);
                    ALAP32=(NewCtrlTree)match(input,ALAP,FOLLOW_ALAP_in_stat403); 


                    if ( _first_0==null ) _first_0 = ALAP32;
                    match(input, Token.DOWN, null); 
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat405);
                    stat33=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat33.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 5 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:113:5: ^( WHILE stat stat )
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    {
                    NewCtrlTree _save_last_1 = _last;
                    NewCtrlTree _first_1 = null;
                    _last = (NewCtrlTree)input.LT(1);
                    WHILE34=(NewCtrlTree)match(input,WHILE,FOLLOW_WHILE_in_stat414); 


                    if ( _first_0==null ) _first_0 = WHILE34;
                    match(input, Token.DOWN, null); 
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat423);
                    stat35=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat35.tree;


                     helper.startBranch(); 

                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat441);
                    stat36=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat36.tree;


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 6 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:119:5: ^( UNTIL stat stat )
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    {
                    NewCtrlTree _save_last_1 = _last;
                    NewCtrlTree _first_1 = null;
                    _last = (NewCtrlTree)input.LT(1);
                    UNTIL37=(NewCtrlTree)match(input,UNTIL,FOLLOW_UNTIL_in_stat465); 


                    if ( _first_0==null ) _first_0 = UNTIL37;
                    match(input, Token.DOWN, null); 
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat474);
                    stat38=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat38.tree;


                     helper.startBranch(); 

                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat492);
                    stat39=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat39.tree;


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 7 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:125:5: ^( TRY stat ( stat )? )
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    {
                    NewCtrlTree _save_last_1 = _last;
                    NewCtrlTree _first_1 = null;
                    _last = (NewCtrlTree)input.LT(1);
                    TRY40=(NewCtrlTree)match(input,TRY,FOLLOW_TRY_in_stat516); 


                    if ( _first_0==null ) _first_0 = TRY40;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat534);
                    stat41=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat41.tree;


                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:128:8: ( stat )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==ALAP||LA6_0==ANY||LA6_0==BLOCK||(LA6_0 >= CALL && LA6_0 <= CHOICE)||LA6_0==IF||LA6_0==OTHER||LA6_0==SEMI||LA6_0==STAR||(LA6_0 >= TRUE && LA6_0 <= UNTIL)||LA6_0==WHILE) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:128:10: stat
                            {
                             helper.nextBranch(); 

                            _last = (NewCtrlTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat556);
                            stat42=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = stat42.tree;


                            retval.tree = (NewCtrlTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 8 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:133:5: ^( IF stat stat ( stat )? )
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    {
                    NewCtrlTree _save_last_1 = _last;
                    NewCtrlTree _first_1 = null;
                    _last = (NewCtrlTree)input.LT(1);
                    IF43=(NewCtrlTree)match(input,IF,FOLLOW_IF_in_stat590); 


                    if ( _first_0==null ) _first_0 = IF43;
                    match(input, Token.DOWN, null); 
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat600);
                    stat44=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat44.tree;


                     helper.startBranch(); 

                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat619);
                    stat45=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat45.tree;


                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:137:8: ( stat )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==ALAP||LA7_0==ANY||LA7_0==BLOCK||(LA7_0 >= CALL && LA7_0 <= CHOICE)||LA7_0==IF||LA7_0==OTHER||LA7_0==SEMI||LA7_0==STAR||(LA7_0 >= TRUE && LA7_0 <= UNTIL)||LA7_0==WHILE) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:137:10: stat
                            {
                             helper.nextBranch(); 

                            _last = (NewCtrlTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat642);
                            stat46=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = stat46.tree;


                            retval.tree = (NewCtrlTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 9 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:142:5: ^( CHOICE stat ( stat )* )
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    {
                    NewCtrlTree _save_last_1 = _last;
                    NewCtrlTree _first_1 = null;
                    _last = (NewCtrlTree)input.LT(1);
                    CHOICE47=(NewCtrlTree)match(input,CHOICE,FOLLOW_CHOICE_in_stat676); 


                    if ( _first_0==null ) _first_0 = CHOICE47;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat694);
                    stat48=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat48.tree;


                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:145:8: ( stat )*
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( (LA8_0==ALAP||LA8_0==ANY||LA8_0==BLOCK||(LA8_0 >= CALL && LA8_0 <= CHOICE)||LA8_0==IF||LA8_0==OTHER||LA8_0==SEMI||LA8_0==STAR||(LA8_0 >= TRUE && LA8_0 <= UNTIL)||LA8_0==WHILE) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:145:10: stat
                    	    {
                    	     helper.nextBranch(); 

                    	    _last = (NewCtrlTree)input.LT(1);
                    	    pushFollow(FOLLOW_stat_in_stat717);
                    	    stat49=stat();

                    	    state._fsp--;

                    	     
                    	    if ( _first_1==null ) _first_1 = stat49.tree;


                    	    retval.tree = (NewCtrlTree)_first_0;
                    	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                    	        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    	    }
                    	    break;

                    	default :
                    	    break loop8;
                        }
                    } while (true);


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     helper.endBranch(); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 10 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:150:5: ^( STAR stat )
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    {
                    NewCtrlTree _save_last_1 = _last;
                    NewCtrlTree _first_1 = null;
                    _last = (NewCtrlTree)input.LT(1);
                    STAR50=(NewCtrlTree)match(input,STAR,FOLLOW_STAR_in_stat753); 


                    if ( _first_0==null ) _first_0 = STAR50;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat771);
                    stat51=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat51.tree;


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 11 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:155:5: rule
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_stat793);
                    rule52=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule52.tree;


                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 12 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:156:5: ANY
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    ANY53=(NewCtrlTree)match(input,ANY,FOLLOW_ANY_in_stat799); 
                     
                    if ( _first_0==null ) _first_0 = ANY53;


                     helper.checkAny(ANY53); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 13 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:158:5: OTHER
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    OTHER54=(NewCtrlTree)match(input,OTHER,FOLLOW_OTHER_in_stat811); 
                     
                    if ( _first_0==null ) _first_0 = OTHER54;


                     helper.checkOther(OTHER54); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 14 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:160:5: TRUE
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    TRUE55=(NewCtrlTree)match(input,TRUE,FOLLOW_TRUE_in_stat823); 
                     
                    if ( _first_0==null ) _first_0 = TRUE55;


                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "rule"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:163:1: rule : ^( CALL id= ID ( ^( ARGS ( arg )* RPAR ) )? ) ;
    public final CtrlChecker.rule_return rule() throws RecognitionException {
        CtrlChecker.rule_return retval = new CtrlChecker.rule_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree id=null;
        NewCtrlTree CALL56=null;
        NewCtrlTree ARGS57=null;
        NewCtrlTree RPAR59=null;
        CtrlChecker.arg_return arg58 =null;


        NewCtrlTree id_tree=null;
        NewCtrlTree CALL56_tree=null;
        NewCtrlTree ARGS57_tree=null;
        NewCtrlTree RPAR59_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:165:3: ( ^( CALL id= ID ( ^( ARGS ( arg )* RPAR ) )? ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:165:5: ^( CALL id= ID ( ^( ARGS ( arg )* RPAR ) )? )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            CALL56=(NewCtrlTree)match(input,CALL,FOLLOW_CALL_in_rule841); 


            if ( _first_0==null ) _first_0 = CALL56;
            match(input, Token.DOWN, null); 
            _last = (NewCtrlTree)input.LT(1);
            id=(NewCtrlTree)match(input,ID,FOLLOW_ID_in_rule845); 
             
            if ( _first_1==null ) _first_1 = id;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:165:18: ( ^( ARGS ( arg )* RPAR ) )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==ARGS) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:165:19: ^( ARGS ( arg )* RPAR )
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    {
                    NewCtrlTree _save_last_2 = _last;
                    NewCtrlTree _first_2 = null;
                    _last = (NewCtrlTree)input.LT(1);
                    ARGS57=(NewCtrlTree)match(input,ARGS,FOLLOW_ARGS_in_rule849); 


                    if ( _first_1==null ) _first_1 = ARGS57;
                    match(input, Token.DOWN, null); 
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:165:26: ( arg )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==ARG) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:165:26: arg
                    	    {
                    	    _last = (NewCtrlTree)input.LT(1);
                    	    pushFollow(FOLLOW_arg_in_rule851);
                    	    arg58=arg();

                    	    state._fsp--;

                    	     
                    	    if ( _first_2==null ) _first_2 = arg58.tree;


                    	    retval.tree = (NewCtrlTree)_first_0;
                    	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                    	        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);


                    _last = (NewCtrlTree)input.LT(1);
                    RPAR59=(NewCtrlTree)match(input,RPAR,FOLLOW_RPAR_in_rule854); 
                     
                    if ( _first_2==null ) _first_2 = RPAR59;


                    match(input, Token.UP, null); 
                    _last = _save_last_2;
                    }


                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

            }

             helper.checkCall(((NewCtrlTree)retval.tree)); 
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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_decl"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:168:1: var_decl : ^( VAR type ( ID )+ ) ;
    public final CtrlChecker.var_decl_return var_decl() throws RecognitionException {
        CtrlChecker.var_decl_return retval = new CtrlChecker.var_decl_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree VAR60=null;
        NewCtrlTree ID62=null;
        CtrlChecker.type_return type61 =null;


        NewCtrlTree VAR60_tree=null;
        NewCtrlTree ID62_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:169:2: ( ^( VAR type ( ID )+ ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:169:4: ^( VAR type ( ID )+ )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            VAR60=(NewCtrlTree)match(input,VAR,FOLLOW_VAR_in_var_decl872); 


            if ( _first_0==null ) _first_0 = VAR60;
            match(input, Token.DOWN, null); 
            _last = (NewCtrlTree)input.LT(1);
            pushFollow(FOLLOW_type_in_var_decl874);
            type61=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type61.tree;


            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:171:7: ( ID )+
            int cnt12=0;
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==ID) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:171:9: ID
            	    {
            	    _last = (NewCtrlTree)input.LT(1);
            	    ID62=(NewCtrlTree)match(input,ID,FOLLOW_ID_in_var_decl891); 
            	     
            	    if ( _first_1==null ) _first_1 = ID62;


            	     helper.declareVar(ID62, (type61!=null?((NewCtrlTree)type61.tree):null)); 

            	    retval.tree = (NewCtrlTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

            	    }
            	    break;

            	default :
            	    if ( cnt12 >= 1 ) break loop12;
                        EarlyExitException eee =
                            new EarlyExitException(12, input);
                        throw eee;
                }
                cnt12++;
            } while (true);


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "type"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:177:1: type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlChecker.type_return type() throws RecognitionException {
        CtrlChecker.type_return retval = new CtrlChecker.type_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree NODE63=null;
        NewCtrlTree BOOL64=null;
        NewCtrlTree STRING65=null;
        NewCtrlTree INT66=null;
        NewCtrlTree REAL67=null;

        NewCtrlTree NODE63_tree=null;
        NewCtrlTree BOOL64_tree=null;
        NewCtrlTree STRING65_tree=null;
        NewCtrlTree INT66_tree=null;
        NewCtrlTree REAL67_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:178:3: ( NODE | BOOL | STRING | INT | REAL )
            int alt13=5;
            switch ( input.LA(1) ) {
            case NODE:
                {
                alt13=1;
                }
                break;
            case BOOL:
                {
                alt13=2;
                }
                break;
            case STRING:
                {
                alt13=3;
                }
                break;
            case INT:
                {
                alt13=4;
                }
                break;
            case REAL:
                {
                alt13=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;

            }

            switch (alt13) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:178:5: NODE
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    NODE63=(NewCtrlTree)match(input,NODE,FOLLOW_NODE_in_type930); 
                     
                    if ( _first_0==null ) _first_0 = NODE63;


                     helper.checkType(NODE63); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:179:5: BOOL
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    BOOL64=(NewCtrlTree)match(input,BOOL,FOLLOW_BOOL_in_type940); 
                     
                    if ( _first_0==null ) _first_0 = BOOL64;


                     helper.checkType(BOOL64); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:180:5: STRING
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    STRING65=(NewCtrlTree)match(input,STRING,FOLLOW_STRING_in_type950); 
                     
                    if ( _first_0==null ) _first_0 = STRING65;


                     helper.checkType(STRING65); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:181:5: INT
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    INT66=(NewCtrlTree)match(input,INT,FOLLOW_INT_in_type958); 
                     
                    if ( _first_0==null ) _first_0 = INT66;


                     helper.checkType(INT66); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 5 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:182:5: REAL
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    REAL67=(NewCtrlTree)match(input,REAL,FOLLOW_REAL_in_type969); 
                     
                    if ( _first_0==null ) _first_0 = REAL67;


                     helper.checkType(REAL67); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
    // $ANTLR end "type"


    public static class arg_return extends TreeRuleReturnScope {
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:185:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final CtrlChecker.arg_return arg() throws RecognitionException {
        CtrlChecker.arg_return retval = new CtrlChecker.arg_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree ARG68=null;
        NewCtrlTree OUT69=null;
        NewCtrlTree ID70=null;
        NewCtrlTree DONT_CARE71=null;
        CtrlChecker.literal_return literal72 =null;


        NewCtrlTree ARG68_tree=null;
        NewCtrlTree OUT69_tree=null;
        NewCtrlTree ID70_tree=null;
        NewCtrlTree DONT_CARE71_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:186:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:186:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
            _last = (NewCtrlTree)input.LT(1);
            {
            NewCtrlTree _save_last_1 = _last;
            NewCtrlTree _first_1 = null;
            _last = (NewCtrlTree)input.LT(1);
            ARG68=(NewCtrlTree)match(input,ARG,FOLLOW_ARG_in_arg989); 


            if ( _first_0==null ) _first_0 = ARG68;
            match(input, Token.DOWN, null); 
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:187:7: ( ( OUT )? ID | DONT_CARE | literal )
            int alt15=3;
            switch ( input.LA(1) ) {
            case ID:
            case OUT:
                {
                alt15=1;
                }
                break;
            case DONT_CARE:
                {
                alt15=2;
                }
                break;
            case FALSE:
            case INT_LIT:
            case REAL_LIT:
            case STRING_LIT:
            case TRUE:
                {
                alt15=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;

            }

            switch (alt15) {
                case 1 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:187:9: ( OUT )? ID
                    {
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:187:9: ( OUT )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==OUT) ) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:187:9: OUT
                            {
                            _last = (NewCtrlTree)input.LT(1);
                            OUT69=(NewCtrlTree)match(input,OUT,FOLLOW_OUT_in_arg1000); 
                             
                            if ( _first_1==null ) _first_1 = OUT69;


                            retval.tree = (NewCtrlTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    _last = (NewCtrlTree)input.LT(1);
                    ID70=(NewCtrlTree)match(input,ID,FOLLOW_ID_in_arg1003); 
                     
                    if ( _first_1==null ) _first_1 = ID70;


                     helper.checkVarArg(ARG68); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:188:9: DONT_CARE
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    DONT_CARE71=(NewCtrlTree)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg1015); 
                     
                    if ( _first_1==null ) _first_1 = DONT_CARE71;


                     helper.checkDontCareArg(ARG68); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:189:9: literal
                    {
                    _last = (NewCtrlTree)input.LT(1);
                    pushFollow(FOLLOW_literal_in_arg1027);
                    literal72=literal();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = literal72.tree;


                     helper.checkConstArg(ARG68); 

                    retval.tree = (NewCtrlTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);

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
        NewCtrlTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "literal"
    // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:194:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlChecker.literal_return literal() throws RecognitionException {
        CtrlChecker.literal_return retval = new CtrlChecker.literal_return();
        retval.start = input.LT(1);


        NewCtrlTree root_0 = null;

        NewCtrlTree _first_0 = null;
        NewCtrlTree _last = null;

        NewCtrlTree set73=null;

        NewCtrlTree set73_tree=null;

        try {
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:195:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // D:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:
            {
            _last = (NewCtrlTree)input.LT(1);
            set73=(NewCtrlTree)input.LT(1);

            if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            retval.tree = (NewCtrlTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (NewCtrlTree)adaptor.getParent(retval.tree);
             

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
    public static final BitSet FOLLOW_package_decl_in_program58 = new BitSet(new long[]{0x0000000024000000L});
    public static final BitSet FOLLOW_import_decl_in_program60 = new BitSet(new long[]{0x0000000024000000L});
    public static final BitSet FOLLOW_functions_in_program63 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_recipes_in_program65 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_program67 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PACKAGE_in_package_decl90 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_package_decl92 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_SEMI_in_package_decl94 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IMPORT_in_import_decl127 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_import_decl129 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_SEMI_in_import_decl131 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RECIPES_in_recipes161 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_recipe_in_recipes163 = new BitSet(new long[]{0x0008000000000008L});
    public static final BitSet FOLLOW_RECIPE_in_recipe180 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_recipe182 = new BitSet(new long[]{0x0000000080000800L});
    public static final BitSet FOLLOW_INT_LIT_in_recipe184 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_recipe204 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions235 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions237 = new BitSet(new long[]{0x0000000002000008L});
    public static final BitSet FOLLOW_FUNCTION_in_function260 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function262 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_function281 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block319 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block337 = new BitSet(new long[]{0x724102001000C850L,0x0000000000000001L});
    public static final BitSet FOLLOW_RCURLY_in_block356 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_block_in_stat376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMI_in_stat383 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_decl_in_stat385 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SEMI_in_stat393 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat395 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALAP_in_stat403 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat405 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat414 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat423 = new BitSet(new long[]{0x724002001000C850L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat441 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat465 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat474 = new BitSet(new long[]{0x724002001000C850L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat492 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat516 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat534 = new BitSet(new long[]{0x724002001000C858L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat556 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat590 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat600 = new BitSet(new long[]{0x724002001000C850L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat619 = new BitSet(new long[]{0x724002001000C858L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat642 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat676 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat694 = new BitSet(new long[]{0x724002001000C858L,0x0000000000000001L});
    public static final BitSet FOLLOW_stat_in_stat717 = new BitSet(new long[]{0x724002001000C858L,0x0000000000000001L});
    public static final BitSet FOLLOW_STAR_in_stat753 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat771 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat793 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat811 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule841 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule845 = new BitSet(new long[]{0x0000000000000108L});
    public static final BitSet FOLLOW_ARGS_in_rule849 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_arg_in_rule851 = new BitSet(new long[]{0x0020000000000080L});
    public static final BitSet FOLLOW_RPAR_in_rule854 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_VAR_in_var_decl872 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl874 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_var_decl891 = new BitSet(new long[]{0x0000000008000008L});
    public static final BitSet FOLLOW_NODE_in_type930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_type940 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_type950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_type958 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REAL_in_type969 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_arg989 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg1000 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_arg1003 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg1015 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg1027 = new BitSet(new long[]{0x0000000000000008L});

}