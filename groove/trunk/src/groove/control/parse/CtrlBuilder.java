// $ANTLR 3.4 E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g 2011-12-28 10:24:35

package groove.control.parse;
import groove.control.*;
import groove.control.parse.Namespace.Kind;
import groove.trans.Rule;
import groove.view.FormatError;
import java.util.Set;
import java.util.HashSet;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class CtrlBuilder extends TreeParser {
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


    public CtrlBuilder(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }
    public CtrlBuilder(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return CtrlBuilder.tokenNames; }
    public String getGrammarFileName() { return "E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g"; }


        /** Builder for control automata. */
        private CtrlFactory builder;
        /** Namespace used for building the automaton. */
        private Namespace namespace;
        /** Helper class for some final static semantic checks. */
        private CtrlHelper helper;

        /**
         * Runs the builder on a given, checked syntax tree.
         */
        public CtrlAut run(MyTree tree, Namespace namespace) throws RecognitionException {
            this.builder = CtrlFactory.instance();
            this.namespace = namespace;
            this.helper = new CtrlHelper(this, namespace, null);
            MyTreeAdaptor treeAdaptor = new MyTreeAdaptor();
            setTreeAdaptor(treeAdaptor);
            setTreeNodeStream(treeAdaptor.createTreeNodeStream(tree));
            return program().aut;
        }
        
        public List<FormatError> getErrors() {
            return this.helper.getErrors();
        }


    public static class program_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "program"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:46:1: program returns [ CtrlAut aut ] : ^( PROGRAM actions functions block ) ;
    public final CtrlBuilder.program_return program() throws RecognitionException {
        CtrlBuilder.program_return retval = new CtrlBuilder.program_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree PROGRAM1=null;
        CtrlBuilder.actions_return actions2 =null;

        CtrlBuilder.functions_return functions3 =null;

        CtrlBuilder.block_return block4 =null;


        MyTree PROGRAM1_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:47:3: ( ^( PROGRAM actions functions block ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:47:5: ^( PROGRAM actions functions block )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            PROGRAM1=(MyTree)match(input,PROGRAM,FOLLOW_PROGRAM_in_program59); 


            if ( _first_0==null ) _first_0 = PROGRAM1;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_actions_in_program61);
            actions2=actions();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = actions2.tree;


            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_functions_in_program63);
            functions3=functions();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = functions3.tree;


            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_block_in_program65);
            block4=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block4.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             if ((block4!=null?((MyTree)block4.tree):null).getChildCount() == 0) {
                      retval.aut = builder.buildAlap(builder.buildAny(namespace));
                  } else {
                      retval.aut = (block4!=null?block4.aut:null);
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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:56:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final CtrlBuilder.functions_return functions() throws RecognitionException {
        CtrlBuilder.functions_return retval = new CtrlBuilder.functions_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree FUNCTIONS5=null;
        CtrlBuilder.function_return function6 =null;


        MyTree FUNCTIONS5_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:57:3: ( ^( FUNCTIONS ( function )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:57:5: ^( FUNCTIONS ( function )* )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            FUNCTIONS5=(MyTree)match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions86); 


            if ( _first_0==null ) _first_0 = FUNCTIONS5;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:57:17: ( function )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==FUNCTION) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:57:17: function
                	    {
                	    _last = (MyTree)input.LT(1);
                	    pushFollow(FOLLOW_function_in_functions88);
                	    function6=function();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = function6.tree;


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
    // $ANTLR end "functions"


    public static class function_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:59:1: function : ^( FUNCTION ID block ) ;
    public final CtrlBuilder.function_return function() throws RecognitionException {
        CtrlBuilder.function_return retval = new CtrlBuilder.function_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree FUNCTION7=null;
        MyTree ID8=null;
        CtrlBuilder.block_return block9 =null;


        MyTree FUNCTION7_tree=null;
        MyTree ID8_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:60:3: ( ^( FUNCTION ID block ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:60:5: ^( FUNCTION ID block )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            FUNCTION7=(MyTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_function101); 


            if ( _first_0==null ) _first_0 = FUNCTION7;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            ID8=(MyTree)match(input,ID,FOLLOW_ID_in_function103); 
             
            if ( _first_1==null ) _first_1 = ID8;


            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_block_in_function105);
            block9=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block9.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             namespace.addBody(Kind.FUNCTION, (ID8!=null?ID8.getText():null), (block9!=null?block9.aut:null)); 

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


    public static class actions_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "actions"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:64:1: actions : ^( ACTIONS ( action )* ) ;
    public final CtrlBuilder.actions_return actions() throws RecognitionException {
        CtrlBuilder.actions_return retval = new CtrlBuilder.actions_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree ACTIONS10=null;
        CtrlBuilder.action_return action11 =null;


        MyTree ACTIONS10_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:65:3: ( ^( ACTIONS ( action )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:65:5: ^( ACTIONS ( action )* )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            ACTIONS10=(MyTree)match(input,ACTIONS,FOLLOW_ACTIONS_in_actions128); 


            if ( _first_0==null ) _first_0 = ACTIONS10;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:65:15: ( action )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==RULE) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:65:15: action
                	    {
                	    _last = (MyTree)input.LT(1);
                	    pushFollow(FOLLOW_action_in_actions130);
                	    action11=action();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = action11.tree;


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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:67:1: action : ^( RULE ID block ) ;
    public final CtrlBuilder.action_return action() throws RecognitionException {
        CtrlBuilder.action_return retval = new CtrlBuilder.action_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree RULE12=null;
        MyTree ID13=null;
        CtrlBuilder.block_return block14 =null;


        MyTree RULE12_tree=null;
        MyTree ID13_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:68:3: ( ^( RULE ID block ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:68:5: ^( RULE ID block )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            RULE12=(MyTree)match(input,RULE,FOLLOW_RULE_in_action143); 


            if ( _first_0==null ) _first_0 = RULE12;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            ID13=(MyTree)match(input,ID,FOLLOW_ID_in_action145); 
             
            if ( _first_1==null ) _first_1 = ID13;


            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_block_in_action147);
            block14=block();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = block14.tree;


            match(input, Token.UP, null); 
            _last = _save_last_1;
            }


             helper.checkActionBody(RULE12, (ID13!=null?ID13.getText():null), (block14!=null?block14.aut:null));
                  namespace.addBody(Kind.ACTION, (ID13!=null?ID13.getText():null), (block14!=null?block14.aut:null));
                

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


    public static class block_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "block"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:74:1: block returns [ CtrlAut aut ] : ^( BLOCK ( stat )* ) ;
    public final CtrlBuilder.block_return block() throws RecognitionException {
        CtrlBuilder.block_return retval = new CtrlBuilder.block_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree BLOCK15=null;
        CtrlBuilder.stat_return stat16 =null;


        MyTree BLOCK15_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:75:3: ( ^( BLOCK ( stat )* ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:75:5: ^( BLOCK ( stat )* )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            BLOCK15=(MyTree)match(input,BLOCK,FOLLOW_BLOCK_in_block173); 


            if ( _first_0==null ) _first_0 = BLOCK15;
             retval.aut = builder.buildTrue(); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:77:8: ( stat )*
                loop3:
                do {
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==ALAP||LA3_0==ANY||LA3_0==BLOCK||(LA3_0 >= CALL && LA3_0 <= CHOICE)||LA3_0==IF||LA3_0==OTHER||LA3_0==STAR||(LA3_0 >= TRUE && LA3_0 <= WHILE)) ) {
                        alt3=1;
                    }


                    switch (alt3) {
                	case 1 :
                	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:77:10: stat
                	    {
                	    _last = (MyTree)input.LT(1);
                	    pushFollow(FOLLOW_stat_in_block193);
                	    stat16=stat();

                	    state._fsp--;

                	     
                	    if ( _first_1==null ) _first_1 = stat16.tree;


                	     retval.aut = builder.buildSeq(retval.aut, (stat16!=null?stat16.aut:null)); 

                	    retval.tree = (MyTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                	    }
                	    break;

                	default :
                	    break loop3;
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
    // $ANTLR end "block"


    public static class stat_return extends TreeRuleReturnScope {
        public CtrlAut aut;
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "stat"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:83:1: stat returns [ CtrlAut aut ] : ( block | var_decl | ^( ALAP s= stat ) | ^( WHILE c= stat s= stat ) | ^( UNTIL c= stat s= stat ) | ^( TRY s1= stat (s2= stat )? ) | ^( IF c= stat s1= stat (s2= stat )? ) | ^( CHOICE s1= stat (s2= stat )* ) | ^( STAR s= stat ) | rule | ANY | OTHER | TRUE );
    public final CtrlBuilder.stat_return stat() throws RecognitionException {
        CtrlBuilder.stat_return retval = new CtrlBuilder.stat_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree ALAP19=null;
        MyTree WHILE20=null;
        MyTree UNTIL21=null;
        MyTree TRY22=null;
        MyTree IF23=null;
        MyTree CHOICE24=null;
        MyTree STAR25=null;
        MyTree ANY27=null;
        MyTree OTHER28=null;
        MyTree TRUE29=null;
        CtrlBuilder.stat_return s =null;

        CtrlBuilder.stat_return c =null;

        CtrlBuilder.stat_return s1 =null;

        CtrlBuilder.stat_return s2 =null;

        CtrlBuilder.block_return block17 =null;

        CtrlBuilder.var_decl_return var_decl18 =null;

        CtrlBuilder.rule_return rule26 =null;


        MyTree ALAP19_tree=null;
        MyTree WHILE20_tree=null;
        MyTree UNTIL21_tree=null;
        MyTree TRY22_tree=null;
        MyTree IF23_tree=null;
        MyTree CHOICE24_tree=null;
        MyTree STAR25_tree=null;
        MyTree ANY27_tree=null;
        MyTree OTHER28_tree=null;
        MyTree TRUE29_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:84:3: ( block | var_decl | ^( ALAP s= stat ) | ^( WHILE c= stat s= stat ) | ^( UNTIL c= stat s= stat ) | ^( TRY s1= stat (s2= stat )? ) | ^( IF c= stat s1= stat (s2= stat )? ) | ^( CHOICE s1= stat (s2= stat )* ) | ^( STAR s= stat ) | rule | ANY | OTHER | TRUE )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:84:5: block
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_block_in_stat238);
                    block17=block();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = block17.tree;


                     retval.aut = (block17!=null?block17.aut:null); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:86:5: var_decl
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_var_decl_in_stat250);
                    var_decl18=var_decl();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = var_decl18.tree;


                     retval.aut = builder.buildTrue(); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:88:5: ^( ALAP s= stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    ALAP19=(MyTree)match(input,ALAP,FOLLOW_ALAP_in_stat263); 


                    if ( _first_0==null ) _first_0 = ALAP19;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat267);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildAlap((s!=null?s.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 4 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:90:5: ^( WHILE c= stat s= stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    WHILE20=(MyTree)match(input,WHILE,FOLLOW_WHILE_in_stat281); 


                    if ( _first_0==null ) _first_0 = WHILE20;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat285);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat289);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildWhileDo((c!=null?c.aut:null), (s!=null?s.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 5 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:92:5: ^( UNTIL c= stat s= stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    UNTIL21=(MyTree)match(input,UNTIL,FOLLOW_UNTIL_in_stat303); 


                    if ( _first_0==null ) _first_0 = UNTIL21;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat307);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat311);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildUntilDo((c!=null?c.aut:null), (s!=null?s.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 6 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:94:5: ^( TRY s1= stat (s2= stat )? )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    TRY22=(MyTree)match(input,TRY,FOLLOW_TRY_in_stat325); 


                    if ( _first_0==null ) _first_0 = TRY22;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat329);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:94:19: (s2= stat )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==ALAP||LA4_0==ANY||LA4_0==BLOCK||(LA4_0 >= CALL && LA4_0 <= CHOICE)||LA4_0==IF||LA4_0==OTHER||LA4_0==STAR||(LA4_0 >= TRUE && LA4_0 <= WHILE)) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:94:20: s2= stat
                            {
                            _last = (MyTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat334);
                            s2=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = s2.tree;


                            retval.tree = (MyTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (MyTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildTryElse((s1!=null?s1.aut:null), (s2!=null?s2.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 7 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:96:5: ^( IF c= stat s1= stat (s2= stat )? )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    IF23=(MyTree)match(input,IF,FOLLOW_IF_in_stat350); 


                    if ( _first_0==null ) _first_0 = IF23;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat354);
                    c=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = c.tree;


                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat358);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:96:25: (s2= stat )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==ALAP||LA5_0==ANY||LA5_0==BLOCK||(LA5_0 >= CALL && LA5_0 <= CHOICE)||LA5_0==IF||LA5_0==OTHER||LA5_0==STAR||(LA5_0 >= TRUE && LA5_0 <= WHILE)) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:96:26: s2= stat
                            {
                            _last = (MyTree)input.LT(1);
                            pushFollow(FOLLOW_stat_in_stat363);
                            s2=stat();

                            state._fsp--;

                             
                            if ( _first_1==null ) _first_1 = s2.tree;


                            retval.tree = (MyTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (MyTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildIfThenElse((c!=null?c.aut:null), (s1!=null?s1.aut:null), (s2!=null?s2.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 8 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:98:5: ^( CHOICE s1= stat (s2= stat )* )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    CHOICE24=(MyTree)match(input,CHOICE,FOLLOW_CHOICE_in_stat380); 


                    if ( _first_0==null ) _first_0 = CHOICE24;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat392);
                    s1=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s1.tree;


                     retval.aut = (s1!=null?s1.aut:null); 

                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:101:8: (s2= stat )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==ALAP||LA6_0==ANY||LA6_0==BLOCK||(LA6_0 >= CALL && LA6_0 <= CHOICE)||LA6_0==IF||LA6_0==OTHER||LA6_0==STAR||(LA6_0 >= TRUE && LA6_0 <= WHILE)) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:101:10: s2= stat
                    	    {
                    	    _last = (MyTree)input.LT(1);
                    	    pushFollow(FOLLOW_stat_in_stat414);
                    	    s2=stat();

                    	    state._fsp--;

                    	     
                    	    if ( _first_1==null ) _first_1 = s2.tree;


                    	     retval.aut = builder.buildOr(retval.aut, (s2!=null?s2.aut:null)); 

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


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 9 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:105:5: ^( STAR s= stat )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_1 = _last;
                    MyTree _first_1 = null;
                    _last = (MyTree)input.LT(1);
                    STAR25=(MyTree)match(input,STAR,FOLLOW_STAR_in_stat449); 


                    if ( _first_0==null ) _first_0 = STAR25;
                    match(input, Token.DOWN, null); 
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_stat_in_stat453);
                    s=stat();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = s.tree;


                    match(input, Token.UP, null); 
                    _last = _save_last_1;
                    }


                     retval.aut = builder.buildStar((s!=null?s.aut:null)); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 10 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:107:5: rule
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_rule_in_stat466);
                    rule26=rule();

                    state._fsp--;

                     
                    if ( _first_0==null ) _first_0 = rule26.tree;


                     retval.aut = builder.buildCall((rule26!=null?((MyTree)rule26.tree):null).getCtrlCall(), namespace); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 11 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:109:5: ANY
                    {
                    _last = (MyTree)input.LT(1);
                    ANY27=(MyTree)match(input,ANY,FOLLOW_ANY_in_stat478); 
                     
                    if ( _first_0==null ) _first_0 = ANY27;


                     retval.aut = builder.buildAny(namespace); 

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 12 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:111:5: OTHER
                    {
                    _last = (MyTree)input.LT(1);
                    OTHER28=(MyTree)match(input,OTHER,FOLLOW_OTHER_in_stat490); 
                     
                    if ( _first_0==null ) _first_0 = OTHER28;


                     retval.aut = builder.buildOther(namespace); 
                        

                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 13 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:114:5: TRUE
                    {
                    _last = (MyTree)input.LT(1);
                    TRUE29=(MyTree)match(input,TRUE,FOLLOW_TRUE_in_stat502); 
                     
                    if ( _first_0==null ) _first_0 = TRUE29;


                     retval.aut = builder.buildTrue(); 

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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:118:1: rule : ^( CALL ID ( ^( ARGS ( arg )* ) )? ) ;
    public final CtrlBuilder.rule_return rule() throws RecognitionException {
        CtrlBuilder.rule_return retval = new CtrlBuilder.rule_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree CALL30=null;
        MyTree ID31=null;
        MyTree ARGS32=null;
        CtrlBuilder.arg_return arg33 =null;


        MyTree CALL30_tree=null;
        MyTree ID31_tree=null;
        MyTree ARGS32_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:119:3: ( ^( CALL ID ( ^( ARGS ( arg )* ) )? ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:119:5: ^( CALL ID ( ^( ARGS ( arg )* ) )? )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            CALL30=(MyTree)match(input,CALL,FOLLOW_CALL_in_rule522); 


            if ( _first_0==null ) _first_0 = CALL30;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            ID31=(MyTree)match(input,ID,FOLLOW_ID_in_rule524); 
             
            if ( _first_1==null ) _first_1 = ID31;


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:119:15: ( ^( ARGS ( arg )* ) )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==ARGS) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:119:16: ^( ARGS ( arg )* )
                    {
                    _last = (MyTree)input.LT(1);
                    {
                    MyTree _save_last_2 = _last;
                    MyTree _first_2 = null;
                    _last = (MyTree)input.LT(1);
                    ARGS32=(MyTree)match(input,ARGS,FOLLOW_ARGS_in_rule528); 


                    if ( _first_1==null ) _first_1 = ARGS32;
                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:119:23: ( arg )*
                        loop8:
                        do {
                            int alt8=2;
                            int LA8_0 = input.LA(1);

                            if ( (LA8_0==ARG) ) {
                                alt8=1;
                            }


                            switch (alt8) {
                        	case 1 :
                        	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:119:23: arg
                        	    {
                        	    _last = (MyTree)input.LT(1);
                        	    pushFollow(FOLLOW_arg_in_rule530);
                        	    arg33=arg();

                        	    state._fsp--;

                        	     
                        	    if ( _first_2==null ) _first_2 = arg33.tree;


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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:122:1: var_decl : ^( VAR type ( ID )+ ) ;
    public final CtrlBuilder.var_decl_return var_decl() throws RecognitionException {
        CtrlBuilder.var_decl_return retval = new CtrlBuilder.var_decl_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree VAR34=null;
        MyTree ID36=null;
        CtrlBuilder.type_return type35 =null;


        MyTree VAR34_tree=null;
        MyTree ID36_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:123:2: ( ^( VAR type ( ID )+ ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:123:4: ^( VAR type ( ID )+ )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            VAR34=(MyTree)match(input,VAR,FOLLOW_VAR_in_var_decl549); 


            if ( _first_0==null ) _first_0 = VAR34;
            match(input, Token.DOWN, null); 
            _last = (MyTree)input.LT(1);
            pushFollow(FOLLOW_type_in_var_decl551);
            type35=type();

            state._fsp--;

             
            if ( _first_1==null ) _first_1 = type35.tree;


            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:123:16: ( ID )+
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
            	    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:123:16: ID
            	    {
            	    _last = (MyTree)input.LT(1);
            	    ID36=(MyTree)match(input,ID,FOLLOW_ID_in_var_decl553); 
            	     
            	    if ( _first_1==null ) _first_1 = ID36;


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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:126:1: type : ( NODE | BOOL | STRING | INT | REAL );
    public final CtrlBuilder.type_return type() throws RecognitionException {
        CtrlBuilder.type_return retval = new CtrlBuilder.type_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree set37=null;

        MyTree set37_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:127:3: ( NODE | BOOL | STRING | INT | REAL )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:
            {
            _last = (MyTree)input.LT(1);
            set37=(MyTree)input.LT(1);

            if ( input.LA(1)==BOOL||input.LA(1)==INT||input.LA(1)==NODE||input.LA(1)==REAL||input.LA(1)==STRING ) {
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
    // $ANTLR end "type"


    public static class arg_return extends TreeRuleReturnScope {
        MyTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "arg"
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:130:1: arg : ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) ;
    public final CtrlBuilder.arg_return arg() throws RecognitionException {
        CtrlBuilder.arg_return retval = new CtrlBuilder.arg_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree ARG38=null;
        MyTree OUT39=null;
        MyTree ID40=null;
        MyTree DONT_CARE41=null;
        CtrlBuilder.literal_return literal42 =null;


        MyTree ARG38_tree=null;
        MyTree OUT39_tree=null;
        MyTree ID40_tree=null;
        MyTree DONT_CARE41_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:2: ( ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) ) )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:4: ^( ARG ( ( OUT )? ID | DONT_CARE | literal ) )
            {
            _last = (MyTree)input.LT(1);
            {
            MyTree _save_last_1 = _last;
            MyTree _first_1 = null;
            _last = (MyTree)input.LT(1);
            ARG38=(MyTree)match(input,ARG,FOLLOW_ARG_in_arg600); 


            if ( _first_0==null ) _first_0 = ARG38;
            match(input, Token.DOWN, null); 
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:11: ( ( OUT )? ID | DONT_CARE | literal )
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
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:13: ( OUT )? ID
                    {
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:13: ( OUT )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==OUT) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:13: OUT
                            {
                            _last = (MyTree)input.LT(1);
                            OUT39=(MyTree)match(input,OUT,FOLLOW_OUT_in_arg604); 
                             
                            if ( _first_1==null ) _first_1 = OUT39;


                            retval.tree = (MyTree)_first_0;
                            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                                retval.tree = (MyTree)adaptor.getParent(retval.tree);

                            }
                            break;

                    }


                    _last = (MyTree)input.LT(1);
                    ID40=(MyTree)match(input,ID,FOLLOW_ID_in_arg607); 
                     
                    if ( _first_1==null ) _first_1 = ID40;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 2 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:23: DONT_CARE
                    {
                    _last = (MyTree)input.LT(1);
                    DONT_CARE41=(MyTree)match(input,DONT_CARE,FOLLOW_DONT_CARE_in_arg611); 
                     
                    if ( _first_1==null ) _first_1 = DONT_CARE41;


                    retval.tree = (MyTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (MyTree)adaptor.getParent(retval.tree);

                    }
                    break;
                case 3 :
                    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:131:35: literal
                    {
                    _last = (MyTree)input.LT(1);
                    pushFollow(FOLLOW_literal_in_arg615);
                    literal42=literal();

                    state._fsp--;

                     
                    if ( _first_1==null ) _first_1 = literal42.tree;


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
    // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:134:1: literal : ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT );
    public final CtrlBuilder.literal_return literal() throws RecognitionException {
        CtrlBuilder.literal_return retval = new CtrlBuilder.literal_return();
        retval.start = input.LT(1);


        MyTree root_0 = null;

        MyTree _first_0 = null;
        MyTree _last = null;

        MyTree set43=null;

        MyTree set43_tree=null;

        try {
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:135:3: ( TRUE | FALSE | STRING_LIT | INT_LIT | REAL_LIT )
            // E:\\Eclipse\\groove\\src\\groove\\control\\parse\\CtrlBuilder.g:
            {
            _last = (MyTree)input.LT(1);
            set43=(MyTree)input.LT(1);

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


 

    public static final BitSet FOLLOW_PROGRAM_in_program59 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_actions_in_program61 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_functions_in_program63 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_program65 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions86 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions88 = new BitSet(new long[]{0x0000000004000008L});
    public static final BitSet FOLLOW_FUNCTION_in_function101 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_function103 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_function105 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ACTIONS_in_actions128 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_action_in_actions130 = new BitSet(new long[]{0x0004000000000008L});
    public static final BitSet FOLLOW_RULE_in_action143 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_action145 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_action147 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block173 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_block193 = new BitSet(new long[]{0x3E400200200190A8L});
    public static final BitSet FOLLOW_block_in_stat238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_decl_in_stat250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALAP_in_stat263 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat267 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_stat281 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat285 = new BitSet(new long[]{0x3E400200200190A0L});
    public static final BitSet FOLLOW_stat_in_stat289 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_stat303 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat307 = new BitSet(new long[]{0x3E400200200190A0L});
    public static final BitSet FOLLOW_stat_in_stat311 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_stat325 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat329 = new BitSet(new long[]{0x3E400200200190A8L});
    public static final BitSet FOLLOW_stat_in_stat334 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_stat350 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat354 = new BitSet(new long[]{0x3E400200200190A0L});
    public static final BitSet FOLLOW_stat_in_stat358 = new BitSet(new long[]{0x3E400200200190A8L});
    public static final BitSet FOLLOW_stat_in_stat363 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_stat380 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat392 = new BitSet(new long[]{0x3E400200200190A8L});
    public static final BitSet FOLLOW_stat_in_stat414 = new BitSet(new long[]{0x3E400200200190A8L});
    public static final BitSet FOLLOW_STAR_in_stat449 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_stat_in_stat453 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rule_in_stat466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_stat478 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_stat490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_stat502 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CALL_in_rule522 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule524 = new BitSet(new long[]{0x0000000000000208L});
    public static final BitSet FOLLOW_ARGS_in_rule528 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_arg_in_rule530 = new BitSet(new long[]{0x0000000000000108L});
    public static final BitSet FOLLOW_VAR_in_var_decl549 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_var_decl551 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_var_decl553 = new BitSet(new long[]{0x0000000010000008L});
    public static final BitSet FOLLOW_ARG_in_arg600 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_arg604 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ID_in_arg607 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DONT_CARE_in_arg611 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_literal_in_arg615 = new BitSet(new long[]{0x0000000000000008L});

}