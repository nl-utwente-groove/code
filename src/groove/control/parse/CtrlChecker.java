// $ANTLR 3.4 E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g 2011-12-28 22:14:17

package groove.control.parse;
import groove.control.*;
import groove.control.parse.Namespace.Kind;
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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ACTIONS", "ALAP", "AMP", "ANY", "ARG", "ARGS", "ASTERISK", "BAR", "BLOCK", "BOOL", "BSLASH", "CALL", "CHOICE", "COMMA", "DO", "DONT_CARE", "DOT", "DO_UNTIL", "DO_WHILE", "ELSE", "EscapeSequence", "FALSE", "FUNCTION", "FUNCTIONS", "ID", "IF", "INT", "INT_LIT", "IntegerNumber", "LCURLY", "LPAR", "MINUS", "ML_COMMENT", "NODE", "NOT", "NonIntegerNumber", "OR", "OTHER", "OUT", "PLUS", "PROGRAM", "QUOTE", "RCURLY", "REAL", "REAL_LIT", "RPAR", "RULE", "SEMI", "SHARP", "SL_COMMENT", "STAR", "STRING", "STRING_LIT", "TRUE", "TRY", "UNTIL", "VAR", "WHILE", "WS"
    };

    public static final int EOF=-1;
    public static final int ACTIONS=4;
    public static final int ALAP=5;
    public static final int AMP=6;
    public static final int ANY=7;
    public static final int ARG=8;
    public static final int ARGS=9;
    public static final int ASTERISK=10;
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
    public static final int PLUS=43;
    public static final int PROGRAM=44;
    public static final int QUOTE=45;
    public static final int RCURLY=46;
    public static final int REAL=47;
    public static final int REAL_LIT=48;
    public static final int RPAR=49;
    public static final int RULE=50;
    public static final int SEMI=51;
    public static final int SHARP=52;
    public static final int SL_COMMENT=53;
    public static final int STAR=54;
    public static final int STRING=55;
    public static final int STRING_LIT=56;
    public static final int TRUE=57;
    public static final int TRY=58;
    public static final int UNTIL=59;
    public static final int VAR=60;
    public static final int WHILE=61;
    public static final int WS=62;

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
    public String getGrammarFileName() { return "E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g"; }


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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:52:1: program : ^( PROGRAM actions functions block ) ;
    public final CtrlChecker.program_return program() throws RecognitionException {
        CtrlChecker.program_return retval = new CtrlChecker.program_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree PROGRAM1=null;
        CtrlChecker.actions_return actions2 =null;

        CtrlChecker.functions_return functions3 =null;

        CtrlChecker.block_return block4 =null;


        MyTree PROGRAM1_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:53:3: ( ^( PROGRAM actions functions block ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:53:5: ^( PROGRAM actions functions block )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            PROGRAM1=(MyTree)match(input,PROGRAM,FOLLOW_PROGRAM_in_program56); 


            if ( _first_0==null ) _first_0 = PROGRAM1;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_actions_in_program58);
            actions2=actions();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = actions2.tree;


            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_functions_in_program60);
            functions3=functions();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = functions3.tree;


            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_block_in_program62);
            block4=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block4.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             if ((block4!=null?((MyTree)block4.tree):null).getChildCount() == 0) {
                      helper.checkAny(PROGRAM1);
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


    public static class actions_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "actions"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:60:1: actions : ^( ACTIONS ( action )* ) ;
    public final CtrlChecker.actions_return actions() throws RecognitionException {
        CtrlChecker.actions_return retval = new CtrlChecker.actions_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree ACTIONS5=null;
        CtrlChecker.action_return action6 =null;


        MyTree ACTIONS5_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:61:3: ( ^( ACTIONS ( action )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:61:5: ^( ACTIONS ( action )* )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            ACTIONS5=(MyTree)match(input,ACTIONS,FOLLOW_ACTIONS_in_actions84); 


            if ( _first_0==null ) _first_0 = ACTIONS5;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:61:15: ( action )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==RULE) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:61:15: action
                	    {
                	    _last = (MyTree)input.LT(1);
                	    pushFollow(FOLLOW_action_in_actions86);
                	    action6=action();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = action6.tree;


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
    // $ANTLR end "actions"


    public static class action_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "action"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:64:1: action : ^( RULE ID block ) ;
    public final CtrlChecker.action_return action() throws RecognitionException {
        CtrlChecker.action_return retval = new CtrlChecker.action_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree RULE7=null;
        MyTree ID8=null;
        CtrlChecker.block_return block9 =null;


        MyTree RULE7_tree=null;
        MyTree ID8_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:65:3: ( ^( RULE ID block ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:65:5: ^( RULE ID block )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            RULE7=(MyTree)match(input,RULE,FOLLOW_RULE_in_action103); 


            if ( _first_0==null ) _first_0 = RULE7;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            ID8=(MyTree)match(input,ID,FOLLOW_ID_in_action105); 
             
            if ( _first_1==null ) _first_1 = ID8;


             helper.startBody(ID8, Kind.ACTION); 

            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_block_in_action124);
            block9=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block9.tree;


             helper.endBody(); 

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
    // $ANTLR end "action"


    public static class functions_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "functions"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:72:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final CtrlChecker.functions_return functions() throws RecognitionException {
        CtrlChecker.functions_return retval = new CtrlChecker.functions_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree FUNCTIONS10=null;
        CtrlChecker.function_return function11 =null;


        MyTree FUNCTIONS10_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:73:3: ( ^( FUNCTIONS ( function )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:73:5: ^( FUNCTIONS ( function )* )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            FUNCTIONS10=(MyTree)match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions155); 


            if ( _first_0==null ) _first_0 = FUNCTIONS10;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:73:17: ( function )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==FUNCTION) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:73:17: function
                	    {
                	    _last = (MyTree)input.LT(1);
                	    pushFollow(FOLLOW_function_in_functions157);
                	    function11=function();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = function11.tree;


                	    retval.tree = (MyTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

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


             helper.reorderFunctions(FUNCTIONS10); 

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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:77:1: function : ^( FUNCTION ID block ) ;
    public final CtrlChecker.function_return function() throws RecognitionException {
        CtrlChecker.function_return retval = new CtrlChecker.function_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree FUNCTION12=null;
        MyTree ID13=null;
        CtrlChecker.block_return block14 =null;


        MyTree FUNCTION12_tree=null;
        MyTree ID13_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:78:3: ( ^( FUNCTION ID block ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:78:5: ^( FUNCTION ID block )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            FUNCTION12=(MyTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_function180); 


            if ( _first_0==null ) _first_0 = FUNCTION12;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            ID13=(MyTree)match(input,ID,FOLLOW_ID_in_function182); 
             
            if ( _first_1==null ) _first_1 = ID13;


             helper.startBody(ID13, Kind.FUNCTION); 

            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_block_in_function201);
            block14=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block14.tree;


             helper.endBody(); 

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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:85:1: block returns [ CtrlAut aut ] : ^( BLOCK ( stat )* ) ;
    public final CtrlChecker.block_return block() throws RecognitionException {
        CtrlChecker.block_return retval = new CtrlChecker.block_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree BLOCK15=null;
        CtrlChecker.stat_return stat16 =null;


        MyTree BLOCK15_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:86:3: ( ^( BLOCK ( stat )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:86:5: ^( BLOCK ( stat )* )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            BLOCK15=(MyTree)match(input,BLOCK,FOLLOW_BLOCK_in_block239); 


            if ( _first_0==null ) _first_0 = BLOCK15;
             helper.openScope(); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:88:8: ( stat )*
                loop3:
                do {
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==ALAP||LA3_0==ANY||LA3_0==BLOCK||(LA3_0 >= CALL && LA3_0 <= CHOICE)||LA3_0==IF||LA3_0==OTHER||LA3_0==STAR||(LA3_0 >= TRUE && LA3_0 <= WHILE)) ) {
                        alt3=1;
                    }


                    switch (alt3) {
                	case 1 :
                	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:88:8: stat
                	    {
                	    _last = (MyTree)input.LT(1);
                	    pushFollow(FOLLOW_stat_in_block257);
                	    stat16=stat();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = stat16.tree;


                	    retval.tree = (MyTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop3;
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:93:1: stat : ( block | var_decl | ^( ALAP stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE );
    public final CtrlChecker.stat_return stat() throws RecognitionException {
        CtrlChecker.stat_return retval = new CtrlChecker.stat_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree ALAP19=null;
        MyTree WHILE21=null;
        MyTree UNTIL24=null;
        MyTree TRY27=null;
        MyTree IF30=null;
        MyTree CHOICE34=null;
        MyTree STAR37=null;
        MyTree ANY40=null;
        MyTree OTHER41=null;
        MyTree TRUE42=null;
        CtrlChecker.block_return block17 =null;

        CtrlChecker.var_decl_return var_decl18 =null;

        CtrlChecker.stat_return stat20 =null;

        CtrlChecker.stat_return stat22 =null;

        CtrlChecker.stat_return stat23 =null;

        CtrlChecker.stat_return stat25 =null;

        CtrlChecker.stat_return stat26 =null;

        CtrlChecker.stat_return stat28 =null;

        CtrlChecker.stat_return stat29 =null;

        CtrlChecker.stat_return stat31 =null;

        CtrlChecker.stat_return stat32 =null;

        CtrlChecker.stat_return stat33 =null;

        CtrlChecker.stat_return stat35 =null;

        CtrlChecker.stat_return stat36 =null;

        CtrlChecker.stat_return stat38 =null;

        CtrlChecker.rule_return rule39 =null;


        MyTree ALAP19_tree=null;
        MyTree WHILE21_tree=null;
        MyTree UNTIL24_tree=null;
        MyTree TRY27_tree=null;
        MyTree IF30_tree=null;
        MyTree CHOICE34_tree=null;
        MyTree STAR37_tree=null;
        MyTree ANY40_tree=null;
        MyTree OTHER41_tree=null;
        MyTree TRUE42_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:94:3: ( block | var_decl | ^( ALAP stat ) | ^( WHILE stat stat ) | ^( UNTIL stat stat ) | ^( TRY stat ( stat )? ) | ^( IF stat stat ( stat )? ) | ^( CHOICE stat ( stat )* ) | ^( STAR stat ) | rule | ANY | OTHER | TRUE )
            int alt7=13;
            switch ( input.LA(1) ) {
            case BLOCK:
                {
                alt7=1;
                }
                break;
            case VAR:
                {
                alt7=2;
                }
                break;
            case ALAP:
                {
                alt7=3;
                }
                break;
            case WHILE:
                {
                alt7=4;
                }
                break;
            case UNTIL:
                {
                alt7=5;
                }
                break;
            case TRY:
                {
                alt7=6;
                }
                break;
            case IF:
                {
                alt7=7;
                }
                break;
            case CHOICE:
                {
                alt7=8;
                }
                break;
            case STAR:
                {
                alt7=9;
                }
                break;
            case CALL:
                {
                alt7=10;
                }
                break;
            case ANY:
                {
                alt7=11;
                }
                break;
            case OTHER:
                {
                alt7=12;
                }
                break;
            case TRUE:
                {
                alt7=13;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;

            }

            switch (alt7) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:94:5: block
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_stat287);
                    block17=block();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = block17.tree;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:95:5: var_decl
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_var_decl_in_stat293);
                    var_decl18=var_decl();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = var_decl18.tree;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:96:5: ^( ALAP stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    ALAP19=(MyTree)match(input,ALAP,FOLLOW_ALAP_in_stat300); 


                    if ( _first_0==null ) _first_0 = ALAP19;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat302);
                    stat20=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat20.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:97:5: ^( WHILE stat stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    WHILE21=(MyTree)match(input,WHILE,FOLLOW_WHILE_in_stat311); 


                    if ( _first_0==null ) _first_0 = WHILE21;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat320);
                    stat22=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat22.tree;


                     helper.startBranch(); 

                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat338);
                    stat23=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat23.tree;


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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:103:5: ^( UNTIL stat stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    UNTIL24=(MyTree)match(input,UNTIL,FOLLOW_UNTIL_in_stat362); 


                    if ( _first_0==null ) _first_0 = UNTIL24;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat371);
                    stat25=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat25.tree;


                     helper.startBranch(); 

                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat389);
                    stat26=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat26.tree;


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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:109:5: ^( TRY stat ( stat )? )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    TRY27=(MyTree)match(input,TRY,FOLLOW_TRY_in_stat413); 


                    if ( _first_0==null ) _first_0 = TRY27;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat431);
                    stat28=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat28.tree;


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:112:8: ( stat )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==ALAP||LA4_0==ANY||LA4_0==BLOCK||(LA4_0 >= CALL && LA4_0 <= CHOICE)||LA4_0==IF||LA4_0==OTHER||LA4_0==STAR||(LA4_0 >= TRUE && LA4_0 <= WHILE)) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:112:10: stat
                            {
                             helper.nextBranch(); 

                            _last = (MyTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat453);
                            stat29=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = stat29.tree;


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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:117:5: ^( IF stat stat ( stat )? )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    IF30=(MyTree)match(input,IF,FOLLOW_IF_in_stat487); 


                    if ( _first_0==null ) _first_0 = IF30;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat497);
                    stat31=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat31.tree;


                     helper.startBranch(); 

                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat516);
                    stat32=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat32.tree;


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:121:8: ( stat )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==ALAP||LA5_0==ANY||LA5_0==BLOCK||(LA5_0 >= CALL && LA5_0 <= CHOICE)||LA5_0==IF||LA5_0==OTHER||LA5_0==STAR||(LA5_0 >= TRUE && LA5_0 <= WHILE)) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:121:10: stat
                            {
                             helper.nextBranch(); 

                            _last = (MyTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat539);
                            stat33=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = stat33.tree;


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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:126:5: ^( CHOICE stat ( stat )* )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    CHOICE34=(MyTree)match(input,CHOICE,FOLLOW_CHOICE_in_stat573); 


                    if ( _first_0==null ) _first_0 = CHOICE34;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat591);
                    stat35=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat35.tree;


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:129:8: ( stat )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==ALAP||LA6_0==ANY||LA6_0==BLOCK||(LA6_0 >= CALL && LA6_0 <= CHOICE)||LA6_0==IF||LA6_0==OTHER||LA6_0==STAR||(LA6_0 >= TRUE && LA6_0 <= WHILE)) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:129:10: stat
                    	    {
                    	     helper.nextBranch(); 

                    	    _last = (MyTree)input.LT(1);
                    	    pushFollow(FOLLOW_stat_in_stat614);
                    	    stat36=stat();

                    	    state._fsp--;

                    	     
                    	    if ( _first_1==null ) _first_1 = stat36.tree;


                    	    retval.tree = (MyTree)_first_0;
                    	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                    	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:134:5: ^( STAR stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    STAR37=(MyTree)match(input,STAR,FOLLOW_STAR_in_stat650); 


                    if ( _first_0==null ) _first_0 = STAR37;
                     helper.startBranch(); 

                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat668);
                    stat38=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = stat38.tree;


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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:139:5: rule
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_stat690);
                    rule39=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule39.tree;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 11 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:140:5: ANY
                    {
                    _last = (MyTree)input.LT(1);
                    ANY40=(MyTree)match(input,ANY,FOLLOW_ANY_in_stat696); 
                     
                    if ( _first_0==null ) _first_0 = ANY40;


                     helper.checkAny(ANY40); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 12 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:142:5: OTHER
                    {
                    _last = (MyTree)input.LT(1);
                    OTHER41=(MyTree)match(input,OTHER,FOLLOW_OTHER_in_stat708); 
                     
                    if ( _first_0==null ) _first_0 = OTHER41;


                     helper.checkOther(OTHER41); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 13 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:144:5: TRUE
                    {
                    _last = (MyTree)input.LT(1);
                    TRUE42=(MyTree)match(input,TRUE,FOLLOW_TRUE_in_stat720); 
                     
                    if ( _first_0==null ) _first_0 = TRUE42;


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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:147:1: rule : ^( CALL id= ID ( ^( ARGS ( arg )* ) )? ) ;
    public final CtrlChecker.rule_return rule() throws RecognitionException {
        CtrlChecker.rule_return retval = new CtrlChecker.rule_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree id=null;
        MyTree CALL43=null;
        MyTree ARGS44=null;
        CtrlChecker.arg_return arg45 =null;


        MyTree id_tree=null;
        MyTree CALL43_tree=null;
        MyTree ARGS44_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:149:3: ( ^( CALL id= ID ( ^( ARGS ( arg )* ) )? ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:149:5: ^( CALL id= ID ( ^( ARGS ( arg )* ) )? )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            CALL43=(MyTree)match(input,CALL,FOLLOW_CALL_in_rule738); 


            if ( _first_0==null ) _first_0 = CALL43;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            id=(MyTree)match(input,ID,FOLLOW_ID_in_rule742); 
             
            if ( _first_1==null ) _first_1 = id;


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:149:18: ( ^( ARGS ( arg )* ) )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==ARGS) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:149:19: ^( ARGS ( arg )* )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_2 = _last;
                    MyTree _first_2 = null;
                    _last = (MyTree)input.LT(1);
                    ARGS44=(MyTree)match(input,ARGS,FOLLOW_ARGS_in_rule746); 


                    if ( _first_1==null ) _first_1 = ARGS44;
                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:149:26: ( arg )*
                        loop8:
                        do {
                            int alt8=2;
                            int LA8_0 = input.LA(1);

                            if ( (LA8_0==ARG) ) {
                                alt8=1;
                            }


                            switch (alt8) {
                        	case 1 :
                        	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:149:26: arg
                        	    {
                        	    _last = (MyTree)input.LT(1);
                        	    pushFollow(FOLLOW_arg_in_rule748);
                        	    arg45=arg();

                        	    state._fsp--;

                        	     
                        	    if ( _first_2==null ) _first_2 = arg45.tree;


                        	    retval.tree = (MyTree)_first_0;
                        	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                        	    }
                        	    break;

                        	default :
                        	    break loop8;
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:152:1: var_decl : ^( VAR type ( ID )+ ) ;
    public final CtrlChecker.var_decl_return var_decl() throws RecognitionException {
        CtrlChecker.var_decl_return retval = new CtrlChecker.var_decl_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree VAR46=null;
        MyTree ID48=null;
        CtrlChecker.type_return type47 =null;


        MyTree VAR46_tree=null;
        MyTree ID48_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:153:2: ( ^( VAR type ( ID )+ ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:153:4: ^( VAR type ( ID )+ )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            VAR46=(MyTree)match(input,VAR,FOLLOW_VAR_in_var_decl767); 


            if ( _first_0==null ) _first_0 = VAR46;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_type_in_var_decl769);
            type47=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type47.tree;


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:155:7: ( ID )+
            int cnt10=0;
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==ID) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:155:9: ID
            	    {
            	    _last = (MyTree)input.LT(1);
            	    ID48=(MyTree)match(input,ID,FOLLOW_ID_in_var_decl786); 
            	     
            	    if ( _first_1==null ) _first_1 = ID48;


            	     helper.declareVar(ID48, (type47!=null?((MyTree)type47.tree):null)); 

            	    retval.tree = (MyTree)_first_0;
            	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
            	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        throw eee;
                }
                cnt10++;
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:161:1: type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlChecker.type_return type() throws RecognitionException {
        CtrlChecker.type_return retval = new CtrlChecker.type_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree NODE49=null;
        MyTree BOOL50=null;
        MyTree STRING51=null;
        MyTree INT52=null;
        MyTree REAL53=null;

        MyTree NODE49_tree=null;
        MyTree BOOL50_tree=null;
        MyTree STRING51_tree=null;
        MyTree INT52_tree=null;
        MyTree REAL53_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:162:3: ( NODE | BOOL | STRING | INT | REAL )
            int alt11=5;
            switch ( input.LA(1) ) {
            case NODE:
                {
                alt11=1;
                }
                break;
            case BOOL:
                {
                alt11=2;
                }
                break;
            case STRING:
                {
                alt11=3;
                }
                break;
            case INT:
                {
                alt11=4;
                }
                break;
            case REAL:
                {
                alt11=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;

            }

            switch (alt11) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:162:5: NODE
                    {
                    _last = (MyTree)input.LT(1);
                    NODE49=(MyTree)match(input,NODE,FOLLOW_NODE_in_type825); 
                     
                    if ( _first_0==null ) _first_0 = NODE49;


                     helper.checkType(NODE49); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:163:5: BOOL
                    {
                    _last = (MyTree)input.LT(1);
                    BOOL50=(MyTree)match(input,BOOL,FOLLOW_BOOL_in_type835); 
                     
                    if ( _first_0==null ) _first_0 = BOOL50;


                     helper.checkType(BOOL50); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:164:5: STRING
                    {
                    _last = (MyTree)input.LT(1);
                    STRING51=(MyTree)match(input,STRING,FOLLOW_STRING_in_type845); 
                     
                    if ( _first_0==null ) _first_0 = STRING51;


                     helper.checkType(STRING51); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:165:5: INT
                    {
                    _last = (MyTree)input.LT(1);
                    INT52=(MyTree)match(input,INT,FOLLOW_INT_in_type853); 
                     
                    if ( _first_0==null ) _first_0 = INT52;


                     helper.checkType(INT52); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 5 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:166:5: REAL
                    {
                    _last = (MyTree)input.LT(1);
                    REAL53=(MyTree)match(input,REAL,FOLLOW_REAL_in_type864); 
                     
                    if ( _first_0==null ) _first_0 = REAL53;


                     helper.checkType(REAL53); 

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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:169:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final CtrlChecker.arg_return arg() throws RecognitionException {
        CtrlChecker.arg_return retval = new CtrlChecker.arg_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree ARG54=null;
        MyTree OUT55=null;
        MyTree ID56=null;
        MyTree DONT_CARE57=null;
        CtrlChecker.literal_return literal58 =null;


        MyTree ARG54_tree=null;
        MyTree OUT55_tree=null;
        MyTree ID56_tree=null;
        MyTree DONT_CARE57_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:170:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:170:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            ARG54=(MyTree)match(input,ARG,FOLLOW_ARG_in_arg884); 


            if ( _first_0==null ) _first_0 = ARG54;
            match(input, Token.DOWN, null); 
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:171:7: ( ( OUT )? ID | DONT_CARE | literal )
            int alt13=3;
            switch ( input.LA(1) ) {
            case ID:
            case OUT:
                {
                alt13=1;
                }
                break;
            case DONT_CARE:
                {
                alt13=2;
                }
                break;
            case FALSE:
            case INT_LIT:
            case REAL_LIT:
            case STRING_LIT:
            case TRUE:
                {
                alt13=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;

            }

            switch (alt13) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:171:9: ( OUT )? ID
                    {
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:171:9: ( OUT )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0==OUT) ) {
                        alt12=1;
                    }
                    switch (alt12) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:171:9: OUT
                            {
                            _last = (MyTree)input.LT(1);
                            OUT55=(MyTree)match(input,OUT,FOLLOW_OUT_in_arg895); 
                             
                            if ( _first_1==null ) _first_1 = OUT55;


                            retval.tree = (MyTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (MyTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    _last = (MyTree)input.LT(1);
                    ID56=(MyTree)match(input,ID,FOLLOW_ID_in_arg898); 
                     
                    if ( _first_1==null ) _first_1 = ID56;


                     helper.checkVarArg(ARG54); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:172:9: DONT_CARE
                    {
                    _last = (MyTree)input.LT(1);
                    DONT_CARE57=(MyTree)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg910); 
                     
                    if ( _first_1==null ) _first_1 = DONT_CARE57;


                     helper.checkDontCareArg(ARG54); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:173:9: literal
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_literal_in_arg922);
                    literal58=literal();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = literal58.tree;


                     helper.checkConstArg(ARG54); 

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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:178:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlChecker.literal_return literal() throws RecognitionException {
        CtrlChecker.literal_return retval = new CtrlChecker.literal_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree set59=null;

        MyTree set59_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:179:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlChecker.g:
            {
            _last = (MyTree)input.LT(1);
            set59=(MyTree)input.LT(1);

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


 

    public static final BitSet FOLLOW_PROGRAM_in_program56 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_actions_in_program58 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_functions_in_program60 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_program62 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ACTIONS_in_actions84 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_action_in_actions86 = new BitSet(new long[]{0x0004000000000008L});
    public static final BitSet FOLLOW_RULE_in_action103 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_action105 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_action124 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions155 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions157 = new BitSet(new long[]{0x0000000004000008L});
    public static final BitSet FOLLOW_FUNCTION_in_function180 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function182 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_function201 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block239 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block257 = new BitSet(new long[]{0x3E400200200190A8L});
    public static final BitSet FOLLOW_block_in_stat287 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat300 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat302 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat311 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat320 = new BitSet(new long[]{0x3E400200200190A0L});
    public static final BitSet FOLLOW_stat_in_stat338 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat362 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat371 = new BitSet(new long[]{0x3E400200200190A0L});
    public static final BitSet FOLLOW_stat_in_stat389 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat413 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat431 = new BitSet(new long[]{0x3E400200200190A8L});
    public static final BitSet FOLLOW_stat_in_stat453 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat487 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat497 = new BitSet(new long[]{0x3E400200200190A0L});
    public static final BitSet FOLLOW_stat_in_stat516 = new BitSet(new long[]{0x3E400200200190A8L});
    public static final BitSet FOLLOW_stat_in_stat539 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat573 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat591 = new BitSet(new long[]{0x3E400200200190A8L});
    public static final BitSet FOLLOW_stat_in_stat614 = new BitSet(new long[]{0x3E400200200190A8L});
    public static final BitSet FOLLOW_STAR_in_stat650 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat668 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat720 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule738 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule742 = new BitSet(new long[]{0x0000000000000208L});
    public static final BitSet FOLLOW_ARGS_in_rule746 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_arg_in_rule748 = new BitSet(new long[]{0x0000000000000108L});
    public static final BitSet FOLLOW_VAR_in_var_decl767 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl769 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_var_decl786 = new BitSet(new long[]{0x0000000010000008L});
    public static final BitSet FOLLOW_NODE_in_type825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_type835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_type845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_type853 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REAL_in_type864 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_arg884 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg895 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_arg898 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg910 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg922 = new BitSet(new long[]{0x0000000000000008L});

}