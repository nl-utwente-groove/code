grammar Ctrl;

options {
	output=AST;
	k=4;
	ASTLabelType = CommonTree;
}

tokens {
  RECIPES;
  ARG;
  ARGS;
	BLOCK;
	CALL;
  DO_WHILE;
  DO_UNTIL;
	FUNCTIONS;
	PROGRAM;
	VAR;
}

@lexer::header {
package groove.control.parse;
import groove.control.*;
import java.util.LinkedList;
}

@header {
package groove.control.parse;
import groove.control.*;
import groove.control.CtrlCall.Kind;
import groove.algebra.AlgebraFamily;
import groove.view.FormatError;
import java.util.LinkedList;
}

@lexer::members {
    /** Last token read when start position is recorded. */
    private String lastToken;
    /** Start position of a recorded substring of the input. */
    private int recordPos;
    /** Helper class to convert AST trees to namespace. */
    private CtrlHelper helper;
    
    /** Starts recording the input string. */
    public void startRecord() {
        lastToken = this.state.token.getText();
        recordPos = getCharIndex();
    }
    
    public String getRecord() {
        org.antlr.runtime.Token currentToken = this.state.token;
        int currentTokenLength =
            currentToken == null ? 0 : currentToken.getText().length();
        return (this.lastToken + getCharStream().substring(this.recordPos,
            getCharIndex() - 1 - currentTokenLength)).trim();
    }
    
    public void setHelper(CtrlHelper helper) {
        this.helper = helper;
    }
    
    public void displayRecognitionError(String[] tokenNames,
            RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        this.helper.addError(hdr + " " + msg, e.line, e.charPositionInLine);
    }
}

@members {
    /** Lexer for the GCL language. */
    private static CtrlLexer lexer = new CtrlLexer(null);
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
    public MyTree run(CharStream input, Namespace namespace, AlgebraFamily family) throws RecognitionException {
        this.helper = new CtrlHelper(this, namespace, family);
        lexer.setCharStream(input);
        lexer.setHelper(this.helper);
        setTokenStream(new CommonTokenStream(lexer));
        setTreeAdaptor(new MyTreeAdaptor());
        return (MyTree) program().getTree();
    }
}

// PARSER ACTIONS

/** @H Main program. */
program
  : //@S ( function | recipe | stat )*
    //@B Main program, consisting of optional top-level statements,
    //@B control function definitions and recipe definitions.
    (function|recipe|stat)* EOF
    -> ^(PROGRAM ^(RECIPES recipe*) ^(FUNCTIONS function*) ^(BLOCK stat*))
  ;

/** @H Recipe declaration.
  * @B During exploration, the body is treated as an atomic transaction.
  */
recipe
  : //@S RECIPE name LPAR RPAR block
    //@B Declares an atomic rule %s, with body %s.
    { lexer.startRecord(); }
    RECIPE^ ID LPAR! RPAR! (PRIORITY! INT_LIT)? block
    { helper.declareName($RECIPE.tree, lexer.getRecord()); }
  ;

/** @H Function declaration.
  * @B The function will be inlined at every place it is called.
  * @B Functions currently can have no parameters. 
  */
function
  : //@S FUNCTION name LPAR RPAR block
    //@B Declares the function %s, with body %s.
    FUNCTION^ ID LPAR! RPAR! block
    { helper.declareName($FUNCTION.tree, null); }
  ;

/** @H Statement block. */
block
  : //@S LCURLY stat* RCURLY
    //@B Possibly empty sequence of statements, surrounded by curly braces.
    LCURLY stat* RCURLY -> ^(BLOCK stat*);

/** @H Atomic statement. */
stat
	: //@S block
	  block
	| //@S ALAP stat
	  //@B The body %s is repeated as long as it remains enabled.
	  //@B Enabledness is determined by the first rule of the statement.
	  ALAP^ stat
	| //@S WHILE LPAR cond RPAR stat
	  //@B As long as the condition %1$s is successfully applied,
	  //@B the body %2$s is repeated. 
	  //@B <p>This is equivalent to "ALAP LCURLY %1$s SEMI %2$s RCURLY".
	  WHILE^ LPAR! cond RPAR! stat
	| //@S UNTIL LPAR cond RPAR stat
    //@B As long as the condition %2$s is disabled, the body %2$s is repeated. 
    //@B Note that if this terminates, the last action is an application of %1$s.
    UNTIL^ LPAR! cond RPAR! stat
	| DO stat 
	  ( //@S DO stat WHILE LPAR cond RPAR
      //@B Statement %s is executed repeatedly, as long as
      //@B afterwards the condition %s is enabled.
      //@B If enabled, %2$s is also executed.<p>
      //@B Equivalent to "%1$s WHILE LPAR %2$s RPAR %1$s"
      WHILE LPAR cond RPAR -> ^(BLOCK stat ^(WHILE cond stat))
	  | //@S DO stat UNTIL LPAR cond RPAR
      //@B Statement %s is executed repeatedly, as long as
      //@B afterwards the condition %s is not enabled.
      //@B Note that if this terminates, the last action is an application of
      //@B %2$s.<p>
      //@B Equivalent to "%1$s UNTIL LPAR %2$s RPAR %1$s"
    UNTIL LPAR cond RPAR -> ^(BLOCK stat ^(UNTIL cond stat))
	  )
  | //@S IF LPAR cond RPAR stat1 [ELSE stat2]
    //@B If condition %1$s is enabled, it is executed and next
    //@B %2$s is executed; otherwise, the optional %3$s is
    //@B executed.
    IF^ LPAR! cond RPAR! stat ( (ELSE) => ELSE! stat )?
  | //@S TRY stat1 [ELSE stat2]
    //@B Statement %s is executed if it is enabled,
    //@B otherwise the (optional) %s is executed.
    TRY^ stat ( (ELSE) => ELSE! stat )?
  | //@S CHOICE stat [OR stat]+
    //@B Nondeterministic choice of statements.
    CHOICE^ stat ( (OR) => OR! stat)+
	| //@S expr SEMI
	  //@B An expression used as a statement.
	  expr SEMI!
	| //@S var_decl SEMI
	  //@B A variable declaration.
	  var_decl SEMI!
  ;

/** @H Condition. */
cond
	: //@S cond1 BAR cond2
    //@B Nondeterministic choice between %s and %s.
	  cond_atom 
	  ( (BAR cond_atom)+ -> ^(CHOICE cond_atom cond_atom+)
	  | -> cond_atom
	  )
	;

cond_atom
	: //@S cond: TRUE
	  //@B Condition that always succeeds.
	  TRUE
  | //@S cond: call
    //@B Tests the enabledness of a given function or rule.
    //@B Note that the function or rule is in fact executed if enabled.
    call ;

/** @H Expression. */
expr
	: //@S expr1 BAR expr2
	  //@B Nondeterministic choice between %s and %s.
    //@B <p>Equivalent to "CHOICE %1$s SEMI OR %2$s SEMI",
    //@B except that this is an expression and CHOICE is a statement.
	  expr2
	  ( (BAR expr2)+ -> ^(CHOICE expr2 expr2+)
	  | -> expr2
	  )
	;

expr2
  : //@S expr: expr PLUS
    //@B Nondeterministically executes %s one or more times. <p>
    //@B Equivalent to "%1$s SEMI %1$s ASTERISK".
    //
    //@S expr: expr ASTERISK
    //@B Nondeterministically executes %s zero or more times. <p>
    //@B Note that this is <i>not</i> equivalent to "%1$s SHARP" or
    //@B "ALAP %1$s SEMI".
    e=expr_atom
    ( PLUS -> ^(BLOCK $e ^(STAR $e))
    | ASTERISK -> ^(STAR $e)
    | -> $e
    )
  | //@S expr: SHARP expr
    //@B Executes %s as long as possible.<p>    
    //@B Equivalent to "ALAP %1$s SEMI",
    //@B except that this is an expression and ALAP is a statement.
    SHARP expr_atom -> ^(ALAP expr_atom)
  ;

expr_atom
	: //@S expr: ANY
	  //@B Execution of an arbitrary rule.
	  ANY
	| //@S expr: OTHER
	  //@B Execution of an arbitrary rule not explicitly occurring anywhere
	  //@B in this control program.
	  OTHER
	| //@S expr: LPAR expr RPAR
	  //@B Bracketed expression.
	  LPAR! expr RPAR!
	| //@S expr: call
	  //@B Invokes a function or rule.
	  call
	; 

/** @H Rule or function call. */
call
	: //@S name [ LPAR arg_list RPAR ]
	  //@B Invokes a rule or function %s, with optional arguments %s.
	  //@P the rule or function name
	  //@P optional comma-separated list of arguments
	  rule_name arg_list?
	  -> ^(CALL[$rule_name.start] rule_name arg_list?)
	;

/** @H Argument list 
  * @B List of arguments for a rule or function call. 
  */
arg_list
  : //@S [ arg (COMMA arg)* ]
    //@B Possibly empty, comma-separated list of arguments
    LPAR (arg (COMMA arg)*)? RPAR
    -> ^(ARGS arg*)
  ;

/** @H Argument
  * @B Argument for a rule or function call. 
  */
arg
  : //@S OUT id
    //@H Output argument
    //@B Variable %s will receive a value through the call.
    OUT ID -> ^(ARG OUT ID)
  | //@S id
    //@H Input argument
    //@B Variable %s must be bound to a value, which will be passed into the call.
    ID -> ^(ARG ID)
  | //@S DONT_CARE
    //@H Don't-care argument
    //@B The parameter does not affect the match or the control state.
    DONT_CARE -> ^(ARG DONT_CARE)   
  | literal -> ^(ARG literal)
  ;

literal
  : //@S arg: TRUE
    //@B Boolean value for truth.
    TRUE
  | //@S arg: FALSE
    //@B Boolean value for falsehood.
    FALSE
  | //@S arg: QUOTE.text.QUOTE
    //@B String constant with value %s.
    STRING_LIT
  | //@S arg: number
    //@B Integer constant with value %s.
    INT_LIT
  | //@S arg: number DOT number
    //@B Real number constant.
    REAL_LIT
  ;

/** Returns a flattened rule name. */
rule_name
  : ids+=ID (DOT ids+=ID)*
    -> { helper.toRuleName($ids) }
  ;

/** @H Variable declaration. */
var_decl
	: //@S var_type id (COMMA id)*
	  //@B Declares a list of variables, all of the same %s.
	  var_type ID (COMMA ID)* -> ^(VAR var_type ID+)
	;

/** @H Variable type. */
var_type
	: //@S NODE
	  //@B The type of all non-value nodes.
	  NODE
	| //@S BOOL
	  //@B The type of boolean values.
	  BOOL
	| //@S STRING
	  //@B The type of string values.
	  STRING
	| //@S INT
	  //@B The type of integer values.
	  INT
	| //@S REAL
	  //@B The type of real number values.
	  REAL
	;
	
// LEXER RULES

ALAP     : 'alap';
ANY		   : 'any';
BOOL     : 'bool';
CHOICE   : 'choice';
DO       : 'do';
ELSE     : 'else';
FALSE    : 'false';
FUNCTION : 'function';
IF       : 'if';
INT      : 'int';
NODE     : 'node';
OR       : 'or';
OTHER    : 'other';
OUT	     : 'out';
REAL     : 'real';
PRIORITY : 'priority';
RECIPE   : 'recipe';
STAR     : 'star';
STRING   : 'string';
TRY      : 'try';
TRUE     : 'true';
UNTIL    : 'until';
WHILE    : 'while';

INT_LIT
  : IntegerNumber 
  ;

fragment
IntegerNumber
  : '0' 
  | '1'..'9' ('0'..'9')*     
  ;

REAL_LIT
  : NonIntegerNumber
  ;

fragment
NonIntegerNumber
    :   ('0' .. '9')+ '.' ('0' .. '9')*
    |   '.' ( '0' .. '9' )+
    ;

STRING_LIT
// @after{ setText(toUnquoted($text)); }
  : QUOTE 
    ( EscapeSequence
    | ~( BSLASH | QUOTE | '\r' | '\n'  )        
    )* 
    QUOTE 
  ;

fragment
EscapeSequence 
  : BSLASH
    ( QUOTE
      BSLASH 
    )          
  ;    

ID  : ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-')*;

AMP       : '&' ;
DOT       : '.' ;
NOT       : '!' ;
BAR       : '|' ;
SHARP     : '#' ;
PLUS      : '+' ;
ASTERISK  : '*' ;
DONT_CARE	: '_' ;
MINUS     : '-' ;
QUOTE     : '"' ;
BSLASH    : '\\';
COMMA     : ',' ;
SEMI      : ';' ;
LPAR      : '(' ;
RPAR      : ')' ;
LCURLY    : '{' ;
RCURLY    : '}' ;

ML_COMMENT : '/*' ( options {greedy=false;} : . )* '*/' { $channel=HIDDEN; };
SL_COMMENT : '//' ( options {greedy=false;} : . )* '\n' { $channel=HIDDEN; };

WS  :   (   ' '
        |   '\t'
        |   '\r'
        |   '\n'
        )+
        { $channel=HIDDEN; }
    ;    
