grammar Ctrl;

options {
	output=AST;
	k=4;
	ASTLabelType = CommonTree;
}

tokens {
  ARG;
  ARGS;
	BLOCK;
	CALL;
  DO_WHILE;
  DO_UNTIL;
	FUNCTION;
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
import groove.algebra.AlgebraFamily;
import java.util.LinkedList;
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
        helper.addError(hdr + " " + msg);
    }
    
    public List<String> getErrors() {
        return helper.getErrors();
    }

    /**
     * Runs the lexer and parser on a given input character stream,
     * with a (presumably empty) namespace.
     * @return the resulting syntax tree
     */
    public MyTree run(CharStream input, Namespace namespace, AlgebraFamily family) throws RecognitionException {
        this.helper = new CtrlHelper(this, namespace, family);
        lexer.setCharStream(input);
        setTokenStream(new CommonTokenStream(lexer));
        setTreeAdaptor(new MyTreeAdaptor());
        return (MyTree) program().getTree();
    }
}

// PARSER rules

/** Main program. */
program
  : //@ ( function | stat )*
    // Main program, consisting of optional top-level statements and
    // control function definitions. 
    (function|stat)*
    -> ^(PROGRAM ^(FUNCTIONS function*) ^(BLOCK stat*))
  ;

/** Function declaration.
  * The function will be inlined at every place it is called.
  * Functions currently can have no parameters. 
  */
function
  : //@ FUNCTION name LPAR RPAR block
    // Declares the function %s, with body %s.
    FUNCTION^ ID LPAR! RPAR! block
    { helper.declareFunction($FUNCTION.tree); }
  ;

/** Statement block. */
block
  : //@ LCURLY stat* RCURLY
    // Possibly empty sequence of statements, surrounded by curly braces.
    LCURLY stat* RCURLY -> ^(BLOCK stat*);

/** Atomic statement. */
stat
	: //@ block
	  block
	| //@ ALAP stat
	  // The body %s is repeated as long as it remains enabled.
	  // Enabledness is determined by the first rule of the statement.
	  ALAP^ stat
	| //@ WHILE LPAR cond RPAR stat
	  // As long as the condition %1$s is successfully applied,
	  // the body %2$s is repeated. 
	  // <p>This is equivalent to "ALAP LCURLY %1$s SEMI %2$s RCURLY".
	  WHILE^ LPAR! cond RPAR! stat
	| //@ UNTIL LPAR cond RPAR stat
    // As long as the condition %2$s is disabled, the body %2$s is repeated. 
    // Note that if this terminates, the last action is an application of %1$s.
    UNTIL^ LPAR! cond RPAR! stat
	| DO stat 
	  ( //@ DO stat WHILE LPAR cond RPAR
      // Statement %s is executed repeatedly, as long as
      // afterwards the condition %s is enabled.
      // If enabled, %2$s is also executed.<p>
      // Equivalent to "%1$s WHILE LPAR %2$s RPAR %1$s"
      WHILE LPAR cond RPAR -> ^(BLOCK stat ^(WHILE cond stat))
	  | //@ DO stat UNTIL LPAR cond RPAR
      // Statement %s is executed repeatedly, as long as
      // afterwards the condition %s is not enabled.
      // Note that if this terminates, the last action is an application of
      // %2$s.<p>
      // Equivalent to "%1$s UNTIL LPAR %2$s RPAR %1$s"
    UNTIL LPAR cond RPAR -> ^(BLOCK stat ^(UNTIL cond stat))
	  )
  | //@ IF LPAR cond RPAR stat1 [ELSE stat2]
    // If condition %1$s is enabled, it is executed and next
    // %2$s is executed; otherwise, the optional %3$s is
    // executed.
    IF^ LPAR! cond RPAR! stat ( (ELSE) => ELSE! stat )?
  | //@ TRY stat1 [ELSE stat2]
    // Statement %s is executed if it is enabled,
    // otherwise the (optional) %s is executed.
    TRY^ stat ( (ELSE) => ELSE! stat )?
  | //@ CHOICE stat [OR stat]+
    // Nondeterministic choice of statements.
    CHOICE^ stat ( (OR) => OR! stat)+
	| //@ expr SEMI
	  // An expression used as a statement.
	  expr SEMI!
	| //@ var_decl SEMI
	  // A variable declaration.
	  var_decl SEMI!
  ;

/** Condition. */
cond
	: //@ cond1 BAR cond2
    // Nondeterministic choice between %s and %s.
	  cond_atom 
	  ( (BAR cond_atom)+ -> ^(CHOICE cond_atom cond_atom+)
	  | -> cond_atom
	  )
	;

cond_atom
	: //@ cond: TRUE
	  // Condition that always succeeds.
	  TRUE
  | //@ cond: call
    // Tests the enabledness of a given function or rule.
    // Note that the function or rule is in fact executed if enabled.
    call ;

/** Expression. */
expr
	: //@ expr1 BAR expr2
	  // Nondeterministic choice between %s and %s.
    // <p>Equivalent to "CHOICE %1$s SEMI OR %2$s SEMI",
    // except that this is an expression and CHOICE is a statement.
	  expr2
	  ( (BAR expr2)+ -> ^(CHOICE expr2 expr2+)
	  | -> expr2
	  )
	;

expr2
  : //@ expr: expr PLUS
    // Nondeterministically executes %s one or more times. <p>
    // Equivalent to "%1$s SEMI %1$s ASTERISK".
    //
    //@ expr: expr ASTERISK
    // Nondeterministically executes %s zero or more times. <p>
    // Note that this is <i>not</i> equivalent to "%1$s SHARP" or
    // "ALAP %1$s SEMI".
    e=expr_atom
    ( PLUS -> ^(BLOCK $e ^(STAR $e))
    | ASTERISK -> ^(STAR $e)
    | -> $e
    )
  | //@ expr: SHARP expr
    // Executes %s as long as possible.<p>    
    // Equivalent to "ALAP %1$s SEMI",
    // except that this is an expression and ALAP is a statement.
    SHARP expr_atom -> ^(ALAP expr_atom)
  ;

expr_atom
	: //@ expr: ANY
	  // Execution of an arbitrary rule.
	  ANY
	| //@ expr: OTHER
	  // Execution of an arbitrary rule not explicitly occurring anywhere
	  // in this control program.
	  OTHER
	| //@ expr: LPAR expr RPAR
	  // Bracketed expression.
	  LPAR! expr RPAR!
	| //@ expr: call
	  // Invokes a function or rule.
	  call
	; 

/** Rule or function call. */
call
	: //@ name [ LPAR arg_list RPAR ]
	  // Invokes a rule or function %s, with optional arguments %s.
	  rule_name arg_list?
	  -> ^(CALL[$rule_name] rule_name arg_list?)
	;

/** List of arguments for a rule or function call. */
arg_list
  : //@ [ arg (COMMA arg)* ]
    // Possibly empty, comma-separated list of arguments
    LPAR (arg (COMMA arg)*)? RPAR
    -> ^(ARGS arg*)
  ;

/** Argument for a rule or function call. */
arg
  : //@ OUT id
    // Output argument: variable %s will receive a value through the call.
    OUT ID -> ^(ARG OUT ID)
  | //@ id
    // Input argument: variable %s must be bound to a value, which will be passed into the call.
    ID -> ^(ARG ID)
  | //@ DONT_CARE
    // Don't-care argument: the parameter will be bound to any node or value
    DONT_CARE -> ^(ARG DONT_CARE)   
  | literal -> ^(ARG literal)
  ;

literal
  : //@ arg: TRUE
    // Boolean value for truth.
    TRUE
  | //@ arg: FALSE
    // Boolean value for falsehood.
    FALSE
  | //@ arg: QUOTE text QUOTE
    // String constant with value %s.
    STRING_LIT
  | //@ arg: number
    // Integer constant with value %s.
    INT_LIT
  | //@ arg: number DOT number
    // Real number constant.
    REAL_LIT
  ;

/** Returns a flattened rule name. */
rule_name
  : ids+=ID (DOT ids+=ID)*
    -> { helper.toRuleName($ids) }
  ;

/** Variable declaration. */
var_decl
	: //@ var_type id (COMMA id)*
	  // Declares a list of variables, all of the same %s.
	  var_type ID (COMMA ID)* -> ^(VAR var_type ID+)
	;

/** Variable type. */
var_type
	: //@ NODE
	  // The type of all non-value nodes.
	  NODE
	| //@ BOOL
	  // The type of boolean values.
	  BOOL
	| //@ STRING
	  // The type of string values.
	  STRING
	| //@ INT
	  // The type of integer values.
	  INT
	| //@ REAL
	  // The type of real number values.
	  REAL
	;
	
// LEXER rules

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
STAR     : 'string';
STRING   : 'star';
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
