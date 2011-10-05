// $ANTLR 3.4 CtrlChecker.g 2011-07-31 09:50:04

package groove.control.parse;
import groove.control.*;
import groove.trans.Rule;
import groove.algebra.AlgebraFamily;
import groove.view.FormatError;
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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALAP", "AMP", "ANY", "ARG", "ARGS", "ASTERISK", "BAR", "BLOCK", "BOOL", "BSLASH", "CALL", "CHOICE", "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", "ELSE", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF", "INT", "INT_LIT", "IntegerNumber", "LCURLY", "LPAR", "MINUS", "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT", "PLUS", "PROGRAM", "QUOTE", "RCURLY", "REAL", "REAL_LIT", "RPAR", "SEMI", "SHARP", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", "TRUE", "TRY", "UNTIL", "VAR", "WHILE", "WS"
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
    public static final int INT=29;
    public static final int INT_LIT=30;
    public static final int IntegerNumber=31;
    public static final int LCURLY=32;
    public static final int LPAR=33;
    public static final int MINUS=34;
    public static final int ML_COMMENT=35;
    public static final int NODE=36;
    public static final int NOT=37;
    public static final int NonIntegerNumber=38;
    public static final int OR=39;
    public static final int OTHER=40;
    public static final int OUT=41;
    public static final int PLUS=42;
    public static final int PROGRAM=43;
    public static final int QUOTE=44;
    public static final int RCURLY=45;
    public static final int REAL=46;
    public static final int REAL_LIT=47;
    public static final int RPAR=48;
    public static final int SEMI=49;
    public static final int SHARP=50;
    public static final int SL_COMMENT=51;
    public static final int STAR=52;
    public static final int STRING=53;
    public static final int STRING_LIT=54;
    public static final int TRUE=55;
    public static final int TRY=56;
    public static final int UNTIL=57;
    public static final int VAR=58;
    public static final int WHILE=59;
    public static final int WS=60;

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
    public String getGrammarFileName() { return "CtrlChecker.g"; }


        /** Helper class to convert AST trees to namespace. */
        private CtrlHelper helper;
        
        public void displayRecognitionError(String[] tokenNames,
                RecognitionException e) {
            String hdr = getErrorHeader(e);
            String msg = getErrorMessage(e, tokenNames);
            this.helper.addError(hdr + " " + msg, e.line, e.charPositionInLine);
        }

        public List<FormatError> getErrors() {
            return this.helper.getErrors();
        }

        /**
         * Runs the lexer and parser on a given input character stream,
         * with a (presumably empty) namespace.
         * @return the resulting syntax tree
         */
        public MyTree run(MyTree tree, Namespace namespace, AlgebraFamily family) throws RecognitionException {
            this.helper = new CtrlHelper(this, namespace, family);
            MyTreeAdaptor treeAdaptor = new MyTreeAdaptor();
            setTreeAdaptor(treeAdaptor);
            setTreeNodeStream(treeAdaptor.createTreeNodeStream(tree));
            return (MyTree) program().getTree();
        }


    public static class program_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "program"
    // CtrlChecker.g:51:1: program : ^( PROGRAM functions block ) ;
    public final CtrlChecker.program_return program() throws RecognitionException {
        CtrlChecker.program_return retval = new CtrlChecker.program_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree PROGRAM1=null;
        CtrlChecker.functions_return functions2 =null;

        CtrlChecker.block_return block3 =null;


        MyTree PROGRAM1_tree=null;

        try {
            // CtrlChecker.g:52:3: ( ^( PROGRAM functions block ) )
            // CtrlChecker.g:52:6: ^( PROGRAM functions block )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            PROGRAM1=(MyTree)match(input,PROGRAM,FOLLOW_PROGRAM_in_program57); 


            if ( _first_0==null ) _first_0 = PROGRAM1;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_functions_in_program59);
            functions2=functions();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = functions2.tree;


            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_block_in_program61);
            block3=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block3.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);

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


    public static class functions_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "functions"
    // CtrlChecker.g:55:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final CtrlChecker.functions_return functions() throws RecognitionException {
        CtrlChecker.functions_return retval = new CtrlChecker.functions_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree FUNCTIONS4=null;
        CtrlChecker.function_return function5 =null;


        MyTree FUNCTIONS4_tree=null;

        try {
            // CtrlChecker.g:56:3: ( ^( FUNCTIONS ( function )* ) )
            // CtrlChecker.g:56:5: ^( FUNCTIONS ( function )* )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            FUNCTIONS4=(MyTree)match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions77); 


            if ( _first_0==null ) _first_0 = FUNCTIONS4;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // CtrlChecker.g:56:17: ( function )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==FUNCTION) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // CtrlChecker.g:56:17: function
                	    {
                	    _last = (MyTree)input.LT(1);
                	    pushFollow(FOLLOW_function_in_functions79);
                	    function5=function();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = function5.tree;


                	    retval.tree = (MyTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop1;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }
            _last = _save_last_1;
            }


             helper.reorderFunctions(FUNCTIONS4); 

            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);

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
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // CtrlChecker.g:60:1: function : ^( FUNCTION ID block ) ;
    public final CtrlChecker.function_return function() throws RecognitionException {
        CtrlChecker.function_return retval = new CtrlChecker.function_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree FUNCTION6=null;
        MyTree ID7=null;
        CtrlChecker.block_return block8 =null;


        MyTree FUNCTION6_tree=null;
        MyTree ID7_tree=null;

        try {
            // CtrlChecker.g:61:3: ( ^( FUNCTION ID block ) )
            // CtrlChecker.g:61:5: ^( FUNCTION ID block )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            FUNCTION6=(MyTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_function102); 


            if ( _first_0==null ) _first_0 = FUNCTION6;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            ID7=(MyTree)match(input,ID,FOLLOW_ID_in_function104); 
             
            if ( _first_1==null ) _first_1 = ID7;


             helper.startFunction(ID7); 

            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_block_in_function123);
            block8=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block8.tree;


             helper.endFunction(); 

            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);

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
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "block"
    // CtrlChecker.g:68:1: block returns [ CtrlAut aut ] : ^( BLOCK ( stat )* ) ;
    public final CtrlChecker.block_return block() throws RecognitionException {
        CtrlChecker.block_return retval = new CtrlChecker.block_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree BLOCK9=null;
        CtrlChecker.stat_return stat10 =null;


        MyTree BLOCK9_tree=null;

        try {
            // CtrlChecker.g:69:3: ( ^( BLOCK ( stat )* ) )
            // CtrlChecker.g:69:5: ^( BLOCK ( stat )* )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            BLOCK9=(MyTree)match(input,BLOCK,FOLLOW_BLOCK_in_block161); 


            if ( _first_0==null ) _first_0 = BLOCK9;
             helper.openScope(); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // CtrlChecker.g:71:8: ( stat )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==ALAP||LA2_0==ANY||LA2_0==BLOCK||(LA2_0 >= CALL && LA2_0 <= CHOICE)||LA2_0==IF||LA2_0==OTHER||LA2_0==STAR||(LA2_0 >= TRUE && LA2_0 <= WHILE)) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // CtrlChecker.g:71:8: stat
                	    {
                	    _last = (MyTree)input.LT(1);
                	    pushFollow(FOLLOW_stat_in_block179);
                	    stat10=stat();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = stat10.tree;


                	    retval.tree = (MyTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop2;
                    }
                } while (true);


                 helper.closeScope(); 

                match(input, Token.UP, null); 
            }
            _last = _save_last_1;
            }


            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);

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
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "stat"
    // CtrlChecker.g:76:1: stat : ( block | var_decl | ^( ALAP stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE );
    public final CtrlChecker.stat_return stat() throws RecognitionException {
        CtrlChecker.stat_return retval = new CtrlChecker.stat_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree ALAP13=null;
        MyTree WHILE15=null;
        MyTree UNTIL18=null;
        MyTree TRY21=null;
        MyTree IF24=null;
        MyTree CHOICE28=null;
        MyTree STAR31=null;
        MyTree ANY34=null;
        MyTree OTHER35=null;
        MyTree TRUE36=null;
        CtrlChecker.block_return block11 =null;

        CtrlChecker.var_decl_return var_decl12 =null;

        CtrlChecker.stat_return stat14 =null;

        CtrlChecker.stat_return stat16 =null;

        CtrlChecker.stat_return stat17 =null;

        CtrlChecker.stat_return stat19 =null;

        CtrlChecker.stat_return stat20 =null;

        CtrlChecker.stat_return stat22 =null;

        CtrlChecker.stat_return stat23 =null;

        CtrlChecker.stat_return stat25 =null;

        CtrlChecker.stat_return stat26 =null;

        CtrlChecker.stat_return stat27 =null;

        CtrlChecker.stat_return stat29 =null;

        CtrlChecker.stat_return stat30 =null;

        CtrlChecker.stat_return stat32 =null;

        CtrlChecker.rule_return rule33 =null;


        MyTree ALAP13_tree=null;
        MyTree WHILE15_tree=null;
        MyTree UNTIL18_tree=null;
        MyTree TRY21_tree=null;
        MyTree IF24_tree=null;
        MyTree CHOICE28_tree=null;
        MyTree STAR31_tree=null;
        MyTree ANY34_tree=null;
        MyTree OTHER35_tree=null;
        MyTree TRUE36_tree=null;

        try {
            // CtrlChecker.g:77:3: ( block | var_decl | ^( ALAP stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE )
            int alt6=13;
            switch ( input.LA(1) ) {
            case BLOCK:
                {
                alt6=1;
                }
                break;
            case VAR:
                {
                alt6=2;
                }
                break;
            case ALAP:
                {
                alt6=3;
                }
                break;
            case WHILE:
                {
                alt6=4;
                }
                break;
            case UNTIL:
                {
                alt6=5;
                }
                break;
            case TRY:
                {
                alt6=6;
                }
                break;
            case IF:
                {
                alt6=7;
                }
                break;
            case CHOICE:
                {
                alt6=8;
                }
                break;
            case STAR:
                {
                alt6=9;
                }
                break;
            case CALL:
                {
                alt6=10;
                }
                break;
            case ANY:
                {
                alt6=11;
                }
                break;
            case OTHER:
                {
                alt6=12;
                }
                break;
            case TRUE:
                {
                alt6=13;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;

            }

            switch (alt6) {
                case 1 :
                    // CtrlChecker.g:77:5: block
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_stat209);
                    block11=block();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = block11.tree;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // CtrlChecker.g:78:5: var_decl
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_var_decl_in_stat215);
                    var_decl12=var_decl();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = var_decl12.tree;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // CtrlChecker.g:79:5: ^( ALAP stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    ALAP13=(MyTree)match(input,ALAP,FOLLOW_ALAP_in_stat222); 


                    if ( _first_0==null ) _first_0 = ALAP13;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat224);
                    stat14=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat14.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // CtrlChecker.g:80:5: ^( WHILE stat stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    WHILE15=(MyTree)match(input,WHILE,FOLLOW_WHILE_in_stat233); 


                    if ( _first_0==null ) _first_0 = WHILE15;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat242);
                    stat16=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat16.tree;


                     helper.startBranch(); 

                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat260);
                    stat17=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat17.tree;


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 5 :
                    // CtrlChecker.g:86:5: ^( UNTIL stat stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    UNTIL18=(MyTree)match(input,UNTIL,FOLLOW_UNTIL_in_stat284); 


                    if ( _first_0==null ) _first_0 = UNTIL18;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat293);
                    stat19=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat19.tree;


                     helper.startBranch(); 

                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat311);
                    stat20=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat20.tree;


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 6 :
                    // CtrlChecker.g:92:5: ^( TRY stat ( stat )? )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    TRY21=(MyTree)match(input,TRY,FOLLOW_TRY_in_stat335); 


                    if ( _first_0==null ) _first_0 = TRY21;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat353);
                    stat22=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat22.tree;


                    // CtrlChecker.g:95:8: ( stat )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==ALAP||LA3_0==ANY||LA3_0==BLOCK||(LA3_0 >= CALL && LA3_0 <= CHOICE)||LA3_0==IF||LA3_0==OTHER||LA3_0==STAR||(LA3_0 >= TRUE && LA3_0 <= WHILE)) ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // CtrlChecker.g:95:10: stat
                            {
                             helper.nextBranch(); 

                            _last = (MyTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat375);
                            stat23=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = stat23.tree;


                            retval.tree = (MyTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (MyTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 7 :
                    // CtrlChecker.g:100:5: ^( IF stat stat ( stat )? )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    IF24=(MyTree)match(input,IF,FOLLOW_IF_in_stat409); 


                    if ( _first_0==null ) _first_0 = IF24;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat419);
                    stat25=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat25.tree;


                     helper.startBranch(); 

                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat438);
                    stat26=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat26.tree;


                    // CtrlChecker.g:104:8: ( stat )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==ALAP||LA4_0==ANY||LA4_0==BLOCK||(LA4_0 >= CALL && LA4_0 <= CHOICE)||LA4_0==IF||LA4_0==OTHER||LA4_0==STAR||(LA4_0 >= TRUE && LA4_0 <= WHILE)) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // CtrlChecker.g:104:10: stat
                            {
                             helper.nextBranch(); 

                            _last = (MyTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat461);
                            stat27=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = stat27.tree;


                            retval.tree = (MyTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (MyTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 8 :
                    // CtrlChecker.g:109:5: ^( CHOICE stat ( stat )* )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    CHOICE28=(MyTree)match(input,CHOICE,FOLLOW_CHOICE_in_stat495); 


                    if ( _first_0==null ) _first_0 = CHOICE28;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat513);
                    stat29=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat29.tree;


                    // CtrlChecker.g:112:8: ( stat )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==ALAP||LA5_0==ANY||LA5_0==BLOCK||(LA5_0 >= CALL && LA5_0 <= CHOICE)||LA5_0==IF||LA5_0==OTHER||LA5_0==STAR||(LA5_0 >= TRUE && LA5_0 <= WHILE)) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // CtrlChecker.g:112:10: stat
                    	    {
                    	     helper.nextBranch(); 

                    	    _last = (MyTree)input.LT(1);
                    	    pushFollow(FOLLOW_stat_in_stat536);
                    	    stat30=stat();

                    	    state._fsp--;

                    	     
                    	    if ( _first_1==null ) _first_1 = stat30.tree;


                    	    retval.tree = (MyTree)_first_0;
                    	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                    	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     helper.endBranch(); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 9 :
                    // CtrlChecker.g:117:5: ^( STAR stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    STAR31=(MyTree)match(input,STAR,FOLLOW_STAR_in_stat572); 


                    if ( _first_0==null ) _first_0 = STAR31;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat590);
                    stat32=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat32.tree;


                     helper.endBranch(); 

                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 10 :
                    // CtrlChecker.g:122:5: rule
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_stat612);
                    rule33=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule33.tree;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 11 :
                    // CtrlChecker.g:123:5: ANY
                    {
                    _last = (MyTree)input.LT(1);
                    ANY34=(MyTree)match(input,ANY,FOLLOW_ANY_in_stat618); 
                     
                    if ( _first_0==null ) _first_0 = ANY34;


                     helper.checkAny(ANY34); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 12 :
                    // CtrlChecker.g:125:5: OTHER
                    {
                    _last = (MyTree)input.LT(1);
                    OTHER35=(MyTree)match(input,OTHER,FOLLOW_OTHER_in_stat630); 
                     
                    if ( _first_0==null ) _first_0 = OTHER35;


                     helper.checkOther(OTHER35); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 13 :
                    // CtrlChecker.g:127:5: TRUE
                    {
                    _last = (MyTree)input.LT(1);
                    TRUE36=(MyTree)match(input,TRUE,FOLLOW_TRUE_in_stat642); 
                     
                    if ( _first_0==null ) _first_0 = TRUE36;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

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
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "rule"
    // CtrlChecker.g:130:1: rule : ^( CALL id= ID ( ^( ARGS ( arg )* ) )? ) ;
    public final CtrlChecker.rule_return rule() throws RecognitionException {
        CtrlChecker.rule_return retval = new CtrlChecker.rule_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree id=null;
        MyTree CALL37=null;
        MyTree ARGS38=null;
        CtrlChecker.arg_return arg39 =null;


        MyTree id_tree=null;
        MyTree CALL37_tree=null;
        MyTree ARGS38_tree=null;

        try {
            // CtrlChecker.g:132:3: ( ^( CALL id= ID ( ^( ARGS ( arg )* ) )? ) )
            // CtrlChecker.g:132:5: ^( CALL id= ID ( ^( ARGS ( arg )* ) )? )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            CALL37=(MyTree)match(input,CALL,FOLLOW_CALL_in_rule660); 


            if ( _first_0==null ) _first_0 = CALL37;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            id=(MyTree)match(input,ID,FOLLOW_ID_in_rule664); 
             
            if ( _first_1==null ) _first_1 = id;


            // CtrlChecker.g:132:18: ( ^( ARGS ( arg )* ) )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==ARGS) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // CtrlChecker.g:132:19: ^( ARGS ( arg )* )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_2 = _last;
                    MyTree _first_2 = null;
                    _last = (MyTree)input.LT(1);
                    ARGS38=(MyTree)match(input,ARGS,FOLLOW_ARGS_in_rule668); 


                    if ( _first_1==null ) _first_1 = ARGS38;
                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // CtrlChecker.g:132:26: ( arg )*
                        loop7:
                        do {
                            int alt7=2;
                            int LA7_0 = input.LA(1);

                            if ( (LA7_0==ARG) ) {
                                alt7=1;
                            }


                            switch (alt7) {
                        	case 1 :
                        	    // CtrlChecker.g:132:26: arg
                        	    {
                        	    _last = (MyTree)input.LT(1);
                        	    pushFollow(FOLLOW_arg_in_rule670);
                        	    arg39=arg();

                        	    state._fsp--;

                        	     
                        	    if ( _first_2==null ) _first_2 = arg39.tree;


                        	    retval.tree = (MyTree)_first_0;
                        	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                        	    }
                        	    break;

                        	default :
                        	    break loop7;
                            }
                        } while (true);


                        match(input, Token.UP, null); 
                    }
                    _last = _save_last_2;
                    }


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);

            }

             helper.checkCall(((MyTree)retval.tree)); 
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
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "var_decl"
    // CtrlChecker.g:135:1: var_decl : ^( VAR type ( ID )+ ) ;
    public final CtrlChecker.var_decl_return var_decl() throws RecognitionException {
        CtrlChecker.var_decl_return retval = new CtrlChecker.var_decl_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree VAR40=null;
        MyTree ID42=null;
        CtrlChecker.type_return type41 =null;


        MyTree VAR40_tree=null;
        MyTree ID42_tree=null;

        try {
            // CtrlChecker.g:136:2: ( ^( VAR type ( ID )+ ) )
            // CtrlChecker.g:136:4: ^( VAR type ( ID )+ )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            VAR40=(MyTree)match(input,VAR,FOLLOW_VAR_in_var_decl689); 


            if ( _first_0==null ) _first_0 = VAR40;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_type_in_var_decl691);
            type41=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type41.tree;


            // CtrlChecker.g:138:7: ( ID )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==ID) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // CtrlChecker.g:138:9: ID
            	    {
            	    _last = (MyTree)input.LT(1);
            	    ID42=(MyTree)match(input,ID,FOLLOW_ID_in_var_decl708); 
            	     
            	    if ( _first_1==null ) _first_1 = ID42;


            	     helper.declareVar(ID42, (type41!=null?((MyTree)type41.tree):null)); 

            	    retval.tree = (MyTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
            } while (true);


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);

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
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "type"
    // CtrlChecker.g:144:1: type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlChecker.type_return type() throws RecognitionException {
        CtrlChecker.type_return retval = new CtrlChecker.type_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree NODE43=null;
        MyTree BOOL44=null;
        MyTree STRING45=null;
        MyTree INT46=null;
        MyTree REAL47=null;

        MyTree NODE43_tree=null;
        MyTree BOOL44_tree=null;
        MyTree STRING45_tree=null;
        MyTree INT46_tree=null;
        MyTree REAL47_tree=null;

        try {
            // CtrlChecker.g:145:3: ( NODE | BOOL | STRING | INT | REAL )
            int alt10=5;
            switch ( input.LA(1) ) {
            case NODE:
                {
                alt10=1;
                }
                break;
            case BOOL:
                {
                alt10=2;
                }
                break;
            case STRING:
                {
                alt10=3;
                }
                break;
            case INT:
                {
                alt10=4;
                }
                break;
            case REAL:
                {
                alt10=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;

            }

            switch (alt10) {
                case 1 :
                    // CtrlChecker.g:145:5: NODE
                    {
                    _last = (MyTree)input.LT(1);
                    NODE43=(MyTree)match(input,NODE,FOLLOW_NODE_in_type747); 
                     
                    if ( _first_0==null ) _first_0 = NODE43;


                     helper.checkType(NODE43); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // CtrlChecker.g:146:5: BOOL
                    {
                    _last = (MyTree)input.LT(1);
                    BOOL44=(MyTree)match(input,BOOL,FOLLOW_BOOL_in_type757); 
                     
                    if ( _first_0==null ) _first_0 = BOOL44;


                     helper.checkType(BOOL44); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // CtrlChecker.g:147:5: STRING
                    {
                    _last = (MyTree)input.LT(1);
                    STRING45=(MyTree)match(input,STRING,FOLLOW_STRING_in_type767); 
                     
                    if ( _first_0==null ) _first_0 = STRING45;


                     helper.checkType(STRING45); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // CtrlChecker.g:148:5: INT
                    {
                    _last = (MyTree)input.LT(1);
                    INT46=(MyTree)match(input,INT,FOLLOW_INT_in_type775); 
                     
                    if ( _first_0==null ) _first_0 = INT46;


                     helper.checkType(INT46); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 5 :
                    // CtrlChecker.g:149:5: REAL
                    {
                    _last = (MyTree)input.LT(1);
                    REAL47=(MyTree)match(input,REAL,FOLLOW_REAL_in_type786); 
                     
                    if ( _first_0==null ) _first_0 = REAL47;


                     helper.checkType(REAL47); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

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
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg"
    // CtrlChecker.g:152:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final CtrlChecker.arg_return arg() throws RecognitionException {
        CtrlChecker.arg_return retval = new CtrlChecker.arg_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree ARG48=null;
        MyTree OUT49=null;
        MyTree ID50=null;
        MyTree DONT_CARE51=null;
        CtrlChecker.literal_return literal52 =null;


        MyTree ARG48_tree=null;
        MyTree OUT49_tree=null;
        MyTree ID50_tree=null;
        MyTree DONT_CARE51_tree=null;

        try {
            // CtrlChecker.g:153:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // CtrlChecker.g:153:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            ARG48=(MyTree)match(input,ARG,FOLLOW_ARG_in_arg806); 


            if ( _first_0==null ) _first_0 = ARG48;
            match(input, Token.DOWN, null); 
            // CtrlChecker.g:154:7: ( ( OUT )? ID | DONT_CARE | literal )
            int alt12=3;
            switch ( input.LA(1) ) {
            case ID:
            case OUT:
                {
                alt12=1;
                }
                break;
            case DONT_CARE:
                {
                alt12=2;
                }
                break;
            case FALSE:
            case INT_LIT:
            case REAL_LIT:
            case STRING_LIT:
            case TRUE:
                {
                alt12=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;

            }

            switch (alt12) {
                case 1 :
                    // CtrlChecker.g:154:9: ( OUT )? ID
                    {
                    // CtrlChecker.g:154:9: ( OUT )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==OUT) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // CtrlChecker.g:154:9: OUT
                            {
                            _last = (MyTree)input.LT(1);
                            OUT49=(MyTree)match(input,OUT,FOLLOW_OUT_in_arg817); 
                             
                            if ( _first_1==null ) _first_1 = OUT49;


                            retval.tree = (MyTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (MyTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    _last = (MyTree)input.LT(1);
                    ID50=(MyTree)match(input,ID,FOLLOW_ID_in_arg820); 
                     
                    if ( _first_1==null ) _first_1 = ID50;


                     helper.checkVarArg(ARG48); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // CtrlChecker.g:155:9: DONT_CARE
                    {
                    _last = (MyTree)input.LT(1);
                    DONT_CARE51=(MyTree)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg832); 
                     
                    if ( _first_1==null ) _first_1 = DONT_CARE51;


                     helper.checkDontCareArg(ARG48); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // CtrlChecker.g:156:9: literal
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_literal_in_arg844);
                    literal52=literal();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = literal52.tree;


                     helper.checkConstArg(ARG48); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;

            }


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);

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
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "literal"
    // CtrlChecker.g:161:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlChecker.literal_return literal() throws RecognitionException {
        CtrlChecker.literal_return retval = new CtrlChecker.literal_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree set53=null;

        MyTree set53_tree=null;

        try {
            // CtrlChecker.g:162:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // CtrlChecker.g:
            {
            _last = (MyTree)input.LT(1);
            set53=(MyTree)input.LT(1);

            if ( input.LA(1)==FALSE||input.LA(1)==INT_LIT||input.LA(1)==REAL_LIT||(input.LA(1) >= STRING_LIT && input.LA(1) <= TRUE) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            retval.tree = (MyTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (MyTree)adaptor.getParent(retval.tree);
             

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


 

    public static final BitSet FOLLOW_PROGRAM_in_program57 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_functions_in_program59 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_program61 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions77 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions79 = new BitSet(new long[]{0x0000000002000008L});
    public static final BitSet FOLLOW_FUNCTION_in_function102 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function104 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_block_in_function123 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block161 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block179 = new BitSet(new long[]{0x0F9001001000C858L});
    public static final BitSet FOLLOW_block_in_stat209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat222 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat224 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat233 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat242 = new BitSet(new long[]{0x0F9001001000C850L});
    public static final BitSet FOLLOW_stat_in_stat260 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat284 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat293 = new BitSet(new long[]{0x0F9001001000C850L});
    public static final BitSet FOLLOW_stat_in_stat311 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat335 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat353 = new BitSet(new long[]{0x0F9001001000C858L});
    public static final BitSet FOLLOW_stat_in_stat375 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat409 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat419 = new BitSet(new long[]{0x0F9001001000C850L});
    public static final BitSet FOLLOW_stat_in_stat438 = new BitSet(new long[]{0x0F9001001000C858L});
    public static final BitSet FOLLOW_stat_in_stat461 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat495 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat513 = new BitSet(new long[]{0x0F9001001000C858L});
    public static final BitSet FOLLOW_stat_in_stat536 = new BitSet(new long[]{0x0F9001001000C858L});
    public static final BitSet FOLLOW_STAR_in_stat572 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat590 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat618 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule660 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule664 = new BitSet(new long[]{0x0000000000000108L});
    public static final BitSet FOLLOW_ARGS_in_rule668 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_arg_in_rule670 = new BitSet(new long[]{0x0000000000000088L});
    public static final BitSet FOLLOW_VAR_in_var_decl689 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl691 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_var_decl708 = new BitSet(new long[]{0x0000000008000008L});
    public static final BitSet FOLLOW_NODE_in_type747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_type757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_type767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_type775 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REAL_in_type786 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_arg806 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg817 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_arg820 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg832 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg844 = new BitSet(new long[]{0x0000000000000008L});

}