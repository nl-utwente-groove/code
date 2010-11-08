// $ANTLR 3.2 Sep 23, 2009 12:02:23 GCLBuilder.g 2010-11-08 15:08:14

package groove.control.parse;
import groove.control.*;
import groove.util.ExprParser;
import groove.util.Pair;
import groove.trans.Rule;
import groove.graph.GraphInfo;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class GCLBuilder extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PROGRAM", "BLOCK", "FUNCTIONS", "FUNCTION", "CALL", "DO", "VAR", "PARAM", "IDENTIFIER", "OR", "ALAP", "WHILE", "UNTIL", "CHOICE", "CH_OR", "IF", "ELSE", "TRY", "TRUE", "PLUS", "STAR", "SHARP", "ANY", "OTHER", "DOT", "NODE_TYPE", "BOOL_TYPE", "STRING_TYPE", "INT_TYPE", "REAL_TYPE", "COMMA", "OUT", "DONT_CARE", "FALSE", "STRING", "QUOTE", "BSLASH", "MINUS", "NUMBER", "AND", "NOT", "ML_COMMENT", "SL_COMMENT", "WS", "ANY_CHAR", "'{'", "'}'", "'('", "')'", "';'"
    };
    public static final int FUNCTION=7;
    public static final int STAR=24;
    public static final int FUNCTIONS=6;
    public static final int WHILE=15;
    public static final int BOOL_TYPE=30;
    public static final int NODE_TYPE=29;
    public static final int DO=9;
    public static final int PARAM=11;
    public static final int NOT=44;
    public static final int ALAP=14;
    public static final int AND=43;
    public static final int EOF=-1;
    public static final int IF=19;
    public static final int ML_COMMENT=45;
    public static final int ANY_CHAR=48;
    public static final int QUOTE=39;
    public static final int T__51=51;
    public static final int T__52=52;
    public static final int T__53=53;
    public static final int COMMA=34;
    public static final int IDENTIFIER=12;
    public static final int CH_OR=18;
    public static final int PLUS=23;
    public static final int VAR=10;
    public static final int DOT=28;
    public static final int T__50=50;
    public static final int CHOICE=17;
    public static final int SHARP=25;
    public static final int OTHER=27;
    public static final int T__49=49;
    public static final int ELSE=20;
    public static final int NUMBER=42;
    public static final int MINUS=41;
    public static final int INT_TYPE=32;
    public static final int TRUE=22;
    public static final int TRY=21;
    public static final int REAL_TYPE=33;
    public static final int DONT_CARE=36;
    public static final int WS=47;
    public static final int ANY=26;
    public static final int OUT=35;
    public static final int UNTIL=16;
    public static final int STRING_TYPE=31;
    public static final int BLOCK=5;
    public static final int OR=13;
    public static final int SL_COMMENT=46;
    public static final int PROGRAM=4;
    public static final int CALL=8;
    public static final int FALSE=37;
    public static final int BSLASH=40;
    public static final int STRING=38;

    // delegates
    // delegators


        public GCLBuilder(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public GCLBuilder(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return GCLBuilder.tokenNames; }
    public String getGrammarFileName() { return "GCLBuilder.g"; }


    	AutomatonBuilder builder;
      String name;
      
        public void setBuilder(AutomatonBuilder ab) {
        	this.builder = ab;
        }
        
        public void setName(String name) {
          this.name = name;
        }
        
        public String getName() {
          return name;
        }
        
        private void proc(CommonTree block) throws RecognitionException {
        	TreeNodeStream restore = input;
        	input = new CommonTreeNodeStream(block);
        	block();
        	this.input = restore;
        }
        
        private ArrayList<Pair<String,Integer>> parameters = new ArrayList<Pair<String,Integer>>();
        
        private void debug(String msg) {
        	if (builder.usesVariables()) {
        		//System.err.println("Variables debug (GCLBuilder): "+msg);
        	}
        }
        
        ControlTransition currentTransition;



    // $ANTLR start "program"
    // GCLBuilder.g:55:1: program returns [ControlAutomaton aut=null] : ^( PROGRAM functions block ) ;
    public final ControlAutomaton program() throws RecognitionException {
        ControlAutomaton aut = null;

         ControlState start; ControlState end; 
        try {
            // GCLBuilder.g:57:3: ( ^( PROGRAM functions block ) )
            // GCLBuilder.g:57:5: ^( PROGRAM functions block )
            {
            match(input,PROGRAM,FOLLOW_PROGRAM_in_program58); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_functions_in_program60);
            functions();

            state._fsp--;


            		aut = builder.startProgram(); 
            		GraphInfo.setName(aut, getName());
              		start = builder.getStart(); 
              		end = builder.getEnd(); 
            	
            pushFollow(FOLLOW_block_in_program67);
            block();

            state._fsp--;


              		builder.endProgram(); 
              	

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return aut;
    }
    // $ANTLR end "program"


    // $ANTLR start "functions"
    // GCLBuilder.g:68:1: functions : ^( FUNCTIONS ( function )* ) ;
    public final void functions() throws RecognitionException {
        try {
            // GCLBuilder.g:69:3: ( ^( FUNCTIONS ( function )* ) )
            // GCLBuilder.g:69:5: ^( FUNCTIONS ( function )* )
            {
            match(input,FUNCTIONS,FOLLOW_FUNCTIONS_in_functions82); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // GCLBuilder.g:69:17: ( function )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==FUNCTION) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // GCLBuilder.g:69:17: function
                	    {
                	    pushFollow(FOLLOW_function_in_functions84);
                	    function();

                	    state._fsp--;


                	    }
                	    break;

                	default :
                	    break loop1;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "functions"


    // $ANTLR start "function"
    // GCLBuilder.g:71:1: function : ^( FUNCTION IDENTIFIER ) ;
    public final void function() throws RecognitionException {
        try {
            // GCLBuilder.g:72:3: ( ^( FUNCTION IDENTIFIER ) )
            // GCLBuilder.g:72:5: ^( FUNCTION IDENTIFIER )
            {
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function97); 

            match(input, Token.DOWN, null); 
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_function99); 

            match(input, Token.UP, null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "function"


    // $ANTLR start "block"
    // GCLBuilder.g:74:1: block : ^( BLOCK ( statement )* ) ;
    public final void block() throws RecognitionException {
         
        	ControlState start = builder.getStart(); 
        	ControlState end = builder.getEnd(); 
        	boolean empty = true;
        	boolean first = true;
        	ControlState newState = builder.newState();
          	builder.restore(start, newState);
          	ControlState tmpStart = start;

        try {
            // GCLBuilder.g:83:3: ( ^( BLOCK ( statement )* ) )
            // GCLBuilder.g:83:5: ^( BLOCK ( statement )* )
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_block115); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // GCLBuilder.g:83:13: ( statement )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( ((LA2_0>=CALL && LA2_0<=VAR)||(LA2_0>=IDENTIFIER && LA2_0<=CHOICE)||LA2_0==IF||(LA2_0>=TRY && LA2_0<=OTHER)) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // GCLBuilder.g:83:14: statement
                	    {
                	    pushFollow(FOLLOW_statement_in_block118);
                	    statement();

                	    state._fsp--;


                	      				if( !first ) { 
                	    					builder.deltaInitCopy(tmpStart, start); 
                	    				} else {
                	    					first = false;
                	    				}
                	    				tmpStart = newState;
                	    				builder.restore(newState, newState = builder.newState());
                	    				empty = false;
                	    			

                	    }
                	    break;

                	default :
                	    break loop2;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            	builder.rmState(newState);
            	builder.restore(builder.getStart(), end);
            	builder.merge();
            	if( empty ) { builder.tagDelta(start); }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "block"


    // $ANTLR start "statement"
    // GCLBuilder.g:102:1: statement : ( ^( ALAP block ) | ^( WHILE condition block ) | ^( UNTIL condition block ) | ^( DO block condition ) | ^( TRY block ( block )? ) | ^( IF condition block ( block )? ) | ^( CHOICE ( block )+ ) | expression | var_declaration );
    public final void statement() throws RecognitionException {

        	ControlState start = builder.getStart();
        	ControlState end = builder.getEnd();
        	ControlState newState;
        	ControlTransition fail;

        try {
            // GCLBuilder.g:108:3: ( ^( ALAP block ) | ^( WHILE condition block ) | ^( UNTIL condition block ) | ^( DO block condition ) | ^( TRY block ( block )? ) | ^( IF condition block ( block )? ) | ^( CHOICE ( block )+ ) | expression | var_declaration )
            int alt6=9;
            switch ( input.LA(1) ) {
            case ALAP:
                {
                alt6=1;
                }
                break;
            case WHILE:
                {
                alt6=2;
                }
                break;
            case UNTIL:
                {
                alt6=3;
                }
                break;
            case DO:
                {
                alt6=4;
                }
                break;
            case TRY:
                {
                alt6=5;
                }
                break;
            case IF:
                {
                alt6=6;
                }
                break;
            case CHOICE:
                {
                alt6=7;
                }
                break;
            case CALL:
            case IDENTIFIER:
            case OR:
            case TRUE:
            case PLUS:
            case STAR:
            case SHARP:
            case ANY:
            case OTHER:
                {
                alt6=8;
                }
                break;
            case VAR:
                {
                alt6=9;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // GCLBuilder.g:108:5: ^( ALAP block )
                    {
                    match(input,ALAP,FOLLOW_ALAP_in_statement147); 


                    		fail = builder.addElse(); 
                    		newState = builder.newState(); 
                    		builder.restore(newState, start); 
                    		builder.addLambda(); 
                    		builder.restore(start, newState); 
                    	

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_statement155);
                    block();

                    state._fsp--;


                        	builder.fail(start,fail);
                        	builder.tagDelta(start); 
                        

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // GCLBuilder.g:119:5: ^( WHILE condition block )
                    {
                    match(input,WHILE,FOLLOW_WHILE_in_statement164); 


                    		fail = builder.addElse(); 
                    		newState = builder.newState(); 
                    		builder.restore(start, newState); 
                    	

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_condition_in_statement168);
                    condition();

                    state._fsp--;


                    		builder.fail(start, fail); 
                    		builder.restore(newState, start);
                    	
                    pushFollow(FOLLOW_block_in_statement172);
                    block();

                    state._fsp--;


                    		builder.deltaInitCopy(newState, start); 
                    		builder.tagDelta(start);
                    	

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // GCLBuilder.g:130:5: ^( UNTIL condition block )
                    {
                    match(input,UNTIL,FOLLOW_UNTIL_in_statement181); 


                    		newState = builder.newState(); 
                    		builder.restore(start, end);
                    	

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_condition_in_statement185);
                    condition();

                    state._fsp--;


                    		builder.restore(start, newState); 
                    		fail = builder.addElse(); 
                    		builder.fail(start, fail); 
                    		builder.restore(newState,start);
                    	
                    pushFollow(FOLLOW_block_in_statement189);
                    block();

                    state._fsp--;


                    		builder.tagDelta(newState); 
                    		builder.deltaInitCopy(newState, start); 
                    	

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // GCLBuilder.g:142:5: ^( DO block condition )
                    {
                    match(input,DO,FOLLOW_DO_in_statement198); 


                    		newState = builder.newState(); 
                    		builder.restore(newState, end); 
                    		fail = builder.addElse(); 
                    		builder.restore(start, newState);
                    	

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_statement202);
                    block();

                    state._fsp--;


                    		builder.restore(newState, start);
                    	
                    pushFollow(FOLLOW_condition_in_statement206);
                    condition();

                    state._fsp--;


                    		builder.fail(newState,fail);
                    		builder.tagDelta(newState);
                    		builder.deltaInitCopy(newState, start);
                    	

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // GCLBuilder.g:154:5: ^( TRY block ( block )? )
                    {
                    match(input,TRY,FOLLOW_TRY_in_statement215); 

                     
                        	debug("TRY STARTS HERE");
                    		newState = builder.newState(); 
                    		builder.copyInitializedVariables(start, newState);
                    		builder.copyInitializedVariables(start, end);
                    		builder.restore(start, newState); 
                    		fail = builder.addElse(); 
                    		builder.restore(start, end);
                    		debug("TRY PART ENDS HERE");
                    	

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_statement223);
                    block();

                    state._fsp--;


                    		debug("BLOCK STARTS HERE ");
                    		builder.fail(start, fail);
                    		builder.restore(newState, end); 
                    		boolean block = false;
                    		debug("BLOCK ENDS HERE");
                    	
                    // GCLBuilder.g:170:4: ( block )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==BLOCK) ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // GCLBuilder.g:170:6: block
                            {
                            pushFollow(FOLLOW_block_in_statement229);
                            block();

                            state._fsp--;


                            		debug("BLOCK2 STARTS HERE");
                            		block = true;
                            		debug("BLOCK2 ENDS HERE");
                            	

                            }
                            break;

                    }


                    		debug("TRY PART 2 STARTS HERE");
                    		if (!block) {
                    			builder.merge(); 
                    			builder.tagDelta(start);
                    		} else {
                    			builder.initCopy(newState, start);
                    		}
                    		debug("TRY ENDS HERE");
                    	

                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // GCLBuilder.g:184:5: ^( IF condition block ( block )? )
                    {
                    match(input,IF,FOLLOW_IF_in_statement244); 

                     
                    		newState = builder.newState(); 
                    		builder.restore(start, newState);
                    	

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_condition_in_statement248);
                    condition();

                    state._fsp--;


                    		builder.restore(newState, end);
                    	
                    pushFollow(FOLLOW_block_in_statement252);
                    block();

                    state._fsp--;


                    		newState = builder.newState(); 
                    		builder.restore(start, newState); 
                    		fail = builder.addElse(); 
                    		builder.fail(start,fail); 
                    		builder.restore(newState,end); 
                    		boolean block = false;
                    	
                    // GCLBuilder.g:196:4: ( block )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==BLOCK) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // GCLBuilder.g:196:6: block
                            {
                            pushFollow(FOLLOW_block_in_statement258);
                            block();

                            state._fsp--;


                            		block = true;
                            	

                            }
                            break;

                    }


                    		if (!block) { 
                    			builder.merge();
                    			builder.tagDelta(start);
                    		} else {
                    			builder.initCopy(newState, start);
                    		}
                    	

                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // GCLBuilder.g:206:5: ^( CHOICE ( block )+ )
                    {
                    match(input,CHOICE,FOLLOW_CHOICE_in_statement272); 

                    match(input, Token.DOWN, null); 
                    // GCLBuilder.g:206:14: ( block )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==BLOCK) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // GCLBuilder.g:206:16: block
                    	    {
                    	     
                    	    		newState = builder.newState(); 
                    	    		builder.restore(start, newState); 
                    	    		builder.addLambda(); 
                    	    		builder.restore(newState, end); 
                    	    	
                    	    pushFollow(FOLLOW_block_in_statement278);
                    	    block();

                    	    state._fsp--;


                    	    		start.addInit(newState); 
                    	    	

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt5 >= 1 ) break loop5;
                                EarlyExitException eee =
                                    new EarlyExitException(5, input);
                                throw eee;
                        }
                        cnt5++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // GCLBuilder.g:214:7: expression
                    {
                    pushFollow(FOLLOW_expression_in_statement288);
                    expression();

                    state._fsp--;


                    }
                    break;
                case 9 :
                    // GCLBuilder.g:215:4: var_declaration
                    {
                    pushFollow(FOLLOW_var_declaration_in_statement293);
                    var_declaration();

                    state._fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "statement"


    // $ANTLR start "expression"
    // GCLBuilder.g:217:1: expression : ( ^( OR expression expression ) | ^( PLUS expression expression ) | ^( STAR expression ) | ^( SHARP expression ) | ^( CALL IDENTIFIER ( parameter )* ) | TRUE | OTHER | ANY | rule );
    public final void expression() throws RecognitionException {
        CommonTree IDENTIFIER1=null;


        	ControlState start = builder.getStart();
        	ControlState end = builder.getEnd();
        	ControlState newState;
        	ControlTransition fail;
        	parameters.clear();

        try {
            // GCLBuilder.g:224:3: ( ^( OR expression expression ) | ^( PLUS expression expression ) | ^( STAR expression ) | ^( SHARP expression ) | ^( CALL IDENTIFIER ( parameter )* ) | TRUE | OTHER | ANY | rule )
            int alt8=9;
            switch ( input.LA(1) ) {
            case OR:
                {
                alt8=1;
                }
                break;
            case PLUS:
                {
                alt8=2;
                }
                break;
            case STAR:
                {
                alt8=3;
                }
                break;
            case SHARP:
                {
                alt8=4;
                }
                break;
            case CALL:
                {
                alt8=5;
                }
                break;
            case TRUE:
                {
                alt8=6;
                }
                break;
            case OTHER:
                {
                alt8=7;
                }
                break;
            case ANY:
                {
                alt8=8;
                }
                break;
            case IDENTIFIER:
                {
                alt8=9;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // GCLBuilder.g:224:5: ^( OR expression expression )
                    {
                    match(input,OR,FOLLOW_OR_in_expression307); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression309);
                    expression();

                    state._fsp--;


                    		builder.restore(start, end);
                    	
                    pushFollow(FOLLOW_expression_in_expression313);
                    expression();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // GCLBuilder.g:227:5: ^( PLUS expression expression )
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_expression321); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression323);
                    expression();

                    state._fsp--;

                     
                    		builder.restore(end,end);
                    	
                    pushFollow(FOLLOW_expression_in_expression327);
                    expression();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // GCLBuilder.g:230:5: ^( STAR expression )
                    {
                    match(input,STAR,FOLLOW_STAR_in_expression334); 


                    		newState = builder.newState(); 
                    		builder.restore(start,newState); 
                    		builder.addLambda(); 
                    		builder.restore(newState,newState); 
                    	

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression338);
                    expression();

                    state._fsp--;


                    		builder.restore(newState,end);
                    		builder.addLambda();
                    		builder.tagDelta(start);
                    	

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // GCLBuilder.g:240:5: ^( SHARP expression )
                    {
                    match(input,SHARP,FOLLOW_SHARP_in_expression347); 

                     
                    		fail = builder.addElse();
                    		builder.restore(start, start); 
                    	

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression351);
                    expression();

                    state._fsp--;

                     
                    		builder.fail(start,fail); 
                    	

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // GCLBuilder.g:246:5: ^( CALL IDENTIFIER ( parameter )* )
                    {
                    match(input,CALL,FOLLOW_CALL_in_expression360); 

                    match(input, Token.DOWN, null); 
                    IDENTIFIER1=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_expression362); 
                    // GCLBuilder.g:246:23: ( parameter )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==PARAM) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // GCLBuilder.g:246:23: parameter
                    	    {
                    	    pushFollow(FOLLOW_parameter_in_expression364);
                    	    parameter();

                    	    state._fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                    match(input, Token.UP, null); 

                    		if (builder.hasProc((IDENTIFIER1!=null?IDENTIFIER1.getText():null))) {
                    			debug("adding proc:"+(IDENTIFIER1!=null?IDENTIFIER1.getText():null));
                    			proc(builder.getProc((IDENTIFIER1!=null?IDENTIFIER1.getText():null))); 
                    		} else {
                    			ControlTransition ct = builder.addTransition((IDENTIFIER1!=null?IDENTIFIER1.getText():null));
                    			for(Pair<String,Integer> parameter : parameters) {
                    				debug("adding a parameter: "+(IDENTIFIER1!=null?IDENTIFIER1.getText():null));
                    				ct.addParameter(parameter.first(), parameter.second()); 
                    			}
                    			ct.setRule(builder.getRule((IDENTIFIER1!=null?IDENTIFIER1.getText():null)));
                    			currentTransition = ct;
                    		}
                    	

                    }
                    break;
                case 6 :
                    // GCLBuilder.g:260:5: TRUE
                    {
                    match(input,TRUE,FOLLOW_TRUE_in_expression374); 
                     
                      		builder.addLambda();
                      		builder.tagDelta(start); 
                      	

                    }
                    break;
                case 7 :
                    // GCLBuilder.g:264:5: OTHER
                    {
                    match(input,OTHER,FOLLOW_OTHER_in_expression382); 
                     
                      		builder.addOther(); 
                      	

                    }
                    break;
                case 8 :
                    // GCLBuilder.g:267:5: ANY
                    {
                    match(input,ANY,FOLLOW_ANY_in_expression390); 
                     
                      		builder.addAny(); 
                      	

                    }
                    break;
                case 9 :
                    // GCLBuilder.g:270:5: rule
                    {
                    pushFollow(FOLLOW_rule_in_expression398);
                    rule();

                    state._fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "expression"


    // $ANTLR start "condition"
    // GCLBuilder.g:273:1: condition : expression ;
    public final void condition() throws RecognitionException {
        try {
            // GCLBuilder.g:274:3: ( expression )
            // GCLBuilder.g:274:5: expression
            {
            pushFollow(FOLLOW_expression_in_condition413);
            expression();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "condition"


    // $ANTLR start "rule"
    // GCLBuilder.g:277:1: rule : IDENTIFIER ;
    public final void rule() throws RecognitionException {
        CommonTree IDENTIFIER2=null;

        try {
            // GCLBuilder.g:278:3: ( IDENTIFIER )
            // GCLBuilder.g:278:5: IDENTIFIER
            {
            IDENTIFIER2=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_rule426); 
             builder.addTransition((IDENTIFIER2!=null?IDENTIFIER2.getText():null)); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "rule"


    // $ANTLR start "var_declaration"
    // GCLBuilder.g:282:1: var_declaration : ^( VAR var_type IDENTIFIER ) ;
    public final void var_declaration() throws RecognitionException {
        try {
            // GCLBuilder.g:283:3: ( ^( VAR var_type IDENTIFIER ) )
            // GCLBuilder.g:283:5: ^( VAR var_type IDENTIFIER )
            {
            match(input,VAR,FOLLOW_VAR_in_var_declaration444); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_var_type_in_var_declaration446);
            var_type();

            state._fsp--;

            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_var_declaration448); 

            match(input, Token.UP, null); 
             builder.addLambda(); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "var_declaration"


    // $ANTLR start "var_type"
    // GCLBuilder.g:286:1: var_type : ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE );
    public final void var_type() throws RecognitionException {
        try {
            // GCLBuilder.g:287:3: ( NODE_TYPE | BOOL_TYPE | STRING_TYPE | INT_TYPE | REAL_TYPE )
            // GCLBuilder.g:
            {
            if ( (input.LA(1)>=NODE_TYPE && input.LA(1)<=REAL_TYPE) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "var_type"


    // $ANTLR start "parameter"
    // GCLBuilder.g:294:1: parameter : ( ^( PARAM OUT IDENTIFIER ) | ^( PARAM IDENTIFIER ) | ^( PARAM DONT_CARE ) | ^( PARAM BOOL_TYPE TRUE ) | ^( PARAM BOOL_TYPE FALSE ) | ^( PARAM STRING_TYPE str= IDENTIFIER ) | ^( PARAM INT_TYPE in= IDENTIFIER ) | ^( PARAM REAL_TYPE r= IDENTIFIER ) );
    public final void parameter() throws RecognitionException {
        CommonTree str=null;
        CommonTree in=null;
        CommonTree r=null;
        CommonTree IDENTIFIER3=null;
        CommonTree IDENTIFIER4=null;

        try {
            // GCLBuilder.g:295:3: ( ^( PARAM OUT IDENTIFIER ) | ^( PARAM IDENTIFIER ) | ^( PARAM DONT_CARE ) | ^( PARAM BOOL_TYPE TRUE ) | ^( PARAM BOOL_TYPE FALSE ) | ^( PARAM STRING_TYPE str= IDENTIFIER ) | ^( PARAM INT_TYPE in= IDENTIFIER ) | ^( PARAM REAL_TYPE r= IDENTIFIER ) )
            int alt9=8;
            alt9 = dfa9.predict(input);
            switch (alt9) {
                case 1 :
                    // GCLBuilder.g:295:5: ^( PARAM OUT IDENTIFIER )
                    {
                    match(input,PARAM,FOLLOW_PARAM_in_parameter506); 

                    match(input, Token.DOWN, null); 
                    match(input,OUT,FOLLOW_OUT_in_parameter508); 
                    IDENTIFIER3=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_parameter510); 

                      		builder.getEnd().initializeVariable((IDENTIFIER3!=null?IDENTIFIER3.getText():null)); 
                      		parameters.add(new Pair<String,Integer>((IDENTIFIER3!=null?IDENTIFIER3.getText():null), Rule.PARAMETER_OUTPUT));
                      	

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // GCLBuilder.g:299:5: ^( PARAM IDENTIFIER )
                    {
                    match(input,PARAM,FOLLOW_PARAM_in_parameter520); 

                    match(input, Token.DOWN, null); 
                    IDENTIFIER4=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_parameter522); 

                      		parameters.add(new Pair<String,Integer>((IDENTIFIER4!=null?IDENTIFIER4.getText():null), Rule.PARAMETER_INPUT));
                      	

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // GCLBuilder.g:302:5: ^( PARAM DONT_CARE )
                    {
                    match(input,PARAM,FOLLOW_PARAM_in_parameter532); 

                    match(input, Token.DOWN, null); 
                    match(input,DONT_CARE,FOLLOW_DONT_CARE_in_parameter534); 

                      		parameters.add(new Pair<String,Integer>("", Rule.PARAMETER_DONT_CARE));
                      	

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // GCLBuilder.g:305:5: ^( PARAM BOOL_TYPE TRUE )
                    {
                    match(input,PARAM,FOLLOW_PARAM_in_parameter544); 

                    match(input, Token.DOWN, null); 
                    match(input,BOOL_TYPE,FOLLOW_BOOL_TYPE_in_parameter546); 
                    match(input,TRUE,FOLLOW_TRUE_in_parameter548); 

                      		debug("Adding boolean parameter: true");
                      		parameters.add(new Pair<String,Integer>("true", Rule.PARAMETER_INPUT));
                      

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // GCLBuilder.g:309:5: ^( PARAM BOOL_TYPE FALSE )
                    {
                    match(input,PARAM,FOLLOW_PARAM_in_parameter558); 

                    match(input, Token.DOWN, null); 
                    match(input,BOOL_TYPE,FOLLOW_BOOL_TYPE_in_parameter560); 
                    match(input,FALSE,FOLLOW_FALSE_in_parameter562); 

                      		debug("Adding boolean parameter: false");
                      		parameters.add(new Pair<String,Integer>("false", Rule.PARAMETER_INPUT));
                      

                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // GCLBuilder.g:313:5: ^( PARAM STRING_TYPE str= IDENTIFIER )
                    {
                    match(input,PARAM,FOLLOW_PARAM_in_parameter572); 

                    match(input, Token.DOWN, null); 
                    match(input,STRING_TYPE,FOLLOW_STRING_TYPE_in_parameter574); 
                    str=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_parameter578); 

                      		debug("Adding string parameter: "+str.getText());
                      		parameters.add(new Pair<String,Integer>(ExprParser.toQuoted(str.getText(),'"'), Rule.PARAMETER_INPUT));
                      	

                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // GCLBuilder.g:317:5: ^( PARAM INT_TYPE in= IDENTIFIER )
                    {
                    match(input,PARAM,FOLLOW_PARAM_in_parameter588); 

                    match(input, Token.DOWN, null); 
                    match(input,INT_TYPE,FOLLOW_INT_TYPE_in_parameter590); 
                    in=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_parameter594); 

                      		debug("Adding integer parameter: "+in.getText());
                      		parameters.add(new Pair<String,Integer>(in.getText(), Rule.PARAMETER_INPUT));
                      

                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // GCLBuilder.g:321:5: ^( PARAM REAL_TYPE r= IDENTIFIER )
                    {
                    match(input,PARAM,FOLLOW_PARAM_in_parameter604); 

                    match(input, Token.DOWN, null); 
                    match(input,REAL_TYPE,FOLLOW_REAL_TYPE_in_parameter606); 
                    r=(CommonTree)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_parameter610); 

                      		debug("Adding real parameter: "+r.getText());
                      		parameters.add(new Pair<String,Integer>(r.getText(), Rule.PARAMETER_INPUT));
                      

                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "parameter"

    // Delegated rules


    protected DFA9 dfa9 = new DFA9(this);
    static final String DFA9_eotS =
        "\14\uffff";
    static final String DFA9_eofS =
        "\14\uffff";
    static final String DFA9_minS =
        "\1\13\1\2\1\14\3\uffff\1\26\5\uffff";
    static final String DFA9_maxS =
        "\1\13\1\2\1\44\3\uffff\1\45\5\uffff";
    static final String DFA9_acceptS =
        "\3\uffff\1\1\1\2\1\3\1\uffff\1\6\1\7\1\10\1\4\1\5";
    static final String DFA9_specialS =
        "\14\uffff}>";
    static final String[] DFA9_transitionS = {
            "\1\1",
            "\1\2",
            "\1\4\21\uffff\1\6\1\7\1\10\1\11\1\uffff\1\3\1\5",
            "",
            "",
            "",
            "\1\12\16\uffff\1\13",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        public String getDescription() {
            return "294:1: parameter : ( ^( PARAM OUT IDENTIFIER ) | ^( PARAM IDENTIFIER ) | ^( PARAM DONT_CARE ) | ^( PARAM BOOL_TYPE TRUE ) | ^( PARAM BOOL_TYPE FALSE ) | ^( PARAM STRING_TYPE str= IDENTIFIER ) | ^( PARAM INT_TYPE in= IDENTIFIER ) | ^( PARAM REAL_TYPE r= IDENTIFIER ) );";
        }
    }
 

    public static final BitSet FOLLOW_PROGRAM_in_program58 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_functions_in_program60 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_program67 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTIONS_in_functions82 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_function_in_functions84 = new BitSet(new long[]{0x0000000000000088L});
    public static final BitSet FOLLOW_FUNCTION_in_function97 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_function99 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block115 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_statement_in_block118 = new BitSet(new long[]{0x000000000FEBF708L});
    public static final BitSet FOLLOW_ALAP_in_statement147 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement155 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WHILE_in_statement164 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement168 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement172 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_UNTIL_in_statement181 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement185 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement189 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DO_in_statement198 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement202 = new BitSet(new long[]{0x000000000FC03100L});
    public static final BitSet FOLLOW_condition_in_statement206 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TRY_in_statement215 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement223 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_block_in_statement229 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_IF_in_statement244 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_condition_in_statement248 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_block_in_statement252 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_block_in_statement258 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHOICE_in_statement272 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement278 = new BitSet(new long[]{0x0000000000000028L});
    public static final BitSet FOLLOW_expression_in_statement288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_var_declaration_in_statement293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_expression307 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression309 = new BitSet(new long[]{0x000000000FC03100L});
    public static final BitSet FOLLOW_expression_in_expression313 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_in_expression321 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression323 = new BitSet(new long[]{0x000000000FC03100L});
    public static final BitSet FOLLOW_expression_in_expression327 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STAR_in_expression334 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression338 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHARP_in_expression347 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression351 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CALL_in_expression360 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_expression362 = new BitSet(new long[]{0x0000000000000808L});
    public static final BitSet FOLLOW_parameter_in_expression364 = new BitSet(new long[]{0x0000000000000808L});
    public static final BitSet FOLLOW_TRUE_in_expression374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OTHER_in_expression382 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_expression390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_expression398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_condition413 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_rule426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_var_declaration444 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_var_type_in_var_declaration446 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_var_declaration448 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_var_type0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PARAM_in_parameter506 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OUT_in_parameter508 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_parameter510 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_parameter520 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IDENTIFIER_in_parameter522 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_parameter532 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_DONT_CARE_in_parameter534 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_parameter544 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_TYPE_in_parameter546 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_TRUE_in_parameter548 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_parameter558 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BOOL_TYPE_in_parameter560 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_FALSE_in_parameter562 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_parameter572 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_TYPE_in_parameter574 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_parameter578 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_parameter588 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_INT_TYPE_in_parameter590 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_parameter594 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PARAM_in_parameter604 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_REAL_TYPE_in_parameter606 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_parameter610 = new BitSet(new long[]{0x0000000000000008L});

}