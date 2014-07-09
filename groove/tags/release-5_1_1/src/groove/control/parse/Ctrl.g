grammar Ctrl;

options {
	output=AST;
	k=4;
	ASTLabelType = CtrlTree;
}

tokens {
  IMPORTS;
  RECIPES;
  ARG;
  ARGS;
  PAR;
  PARS;
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
import groove.util.antlr.*;
import java.util.LinkedList;
}

@lexer::members {
    /** Name space to record lexer errors. */
    private Namespace namespace;
    
    public void initialise(ParseInfo namespace) {
        this.namespace = (Namespace) namespace;
    }
    
    public void displayRecognitionError(String[] tokenNames,
            RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        this.namespace.addError(hdr + " " + msg, e.line, e.charPositionInLine);
    }
}

@header {
package groove.control.parse;
import groove.control.*;
import groove.util.antlr.*;
import groove.grammar.model.FormatErrorSet;
import java.util.LinkedList;
}

@members {
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
}

// PARSER ACTIONS

/** @H Main program. */
program
@init { helper.clearErrors(); }
@after { helper.declareProgram($tree); }
  : //@S package? import* ( function | recipe | stat )*
    //@B Main program, consisting of a sequence top-level statements,
    //@B control function definitions and recipe definitions.
    //@B Java-like packages and imports are provided for modularity. 
    package_decl
    import_decl*
    (function|recipe|stat)* EOF
    { helper.checkEOF($EOF.tree); }
    -> ^( PROGRAM
          package_decl
          ^(IMPORTS import_decl*)
          ^(FUNCTIONS function*) 
          ^(RECIPES recipe*) 
          ^(BLOCK stat*)
        )
  ;

/** @H Package declaration. */
package_decl
  : //@S PACKAGE qual_name
    //@B Causes all rules and functions to be qualified by %s
    ( key=PACKAGE qual_name close=SEMI
      { helper.setPackage($qual_name.tree); }
      -> ^(PACKAGE[$key] qual_name SEMI[$close])
    | -> { helper.emptyPackage() }
    )
  ;

/** @H Import statement. */
import_decl
  : //@S IMPORT qual_name
    //@B Declares the last part of %s to stand for the entire name
    IMPORT^ qual_name SEMI
    { helper.addImport($qual_name.tree);
    }
  ;

/** Dot-separated sequence of identifiers, translated to a flattened identifier. */
qual_name
  : ids+=ID (DOT ids+=ID)*
    -> { helper.toQualName($ids) }
  ;

/** @H Recipe declaration.
  * @B During exploration, the body is treated as an atomic transaction.
  */
recipe
  : //@S RECIPE name par_list (PRIORITY int)? block
    //@B Declares an atomic rule %s, with parameters %s and body %3$s.
    //@B The optional priority %2$s assigns preference in a choice.
    RECIPE^ ID par_list (PRIORITY! INT_LIT)?
    { helper.setContext($RECIPE.tree); }
    block
    { helper.resetContext();
      helper.declareCtrlUnit($RECIPE.tree);
    }
  ;

/** @H Function declaration.
  * @B The function will be inlined at every place it is called.
  * @B Functions currently can have no parameters. 
  */
function
  : //@S FUNCTION name par_list block
    //@B Declares the function %s, with parameters %s and body %3$s.
    FUNCTION^ ID par_list 
    { helper.setContext($FUNCTION.tree); }
    block
    { helper.resetContext();
      helper.declareCtrlUnit($FUNCTION.tree);
    }
  ;

/** @H Parameter list 
  * @B List of parameters for a function or recipe declaration. 
  */
par_list
  : //@S [ par (COMMA par)* ]
    //@B Possibly empty, comma-separated list of parameters
    LPAR (par (COMMA par)*)? RPAR
    -> ^(PARS par*)
  ;
  
/** @H Parameter declaration
  * @B Parameter in a function or recipe declaration. 
  */
par
  : //@S OUT var_type id
    //@H Output parameter
    //@B Variable %s will receive a value in the course of the function or recipe.
    OUT var_type ID -> ^(PAR OUT var_type ID)
  | //@S var_type id
    //@H Input parameter
    //@B Variable %s is initialised by the argument passed into the call.
    var_type ID -> ^(PAR var_type ID)
  ;
  
/** @H Statement block. */
block
  : //@S LCURLY stat* RCURLY
    //@B Possibly empty sequence of statements, surrounded by curly braces.
    open=LCURLY stat* close=RCURLY
    -> ^(BLOCK[$open] stat* TRUE[$close]);

/** @H Atomic statement. */
stat
	: //@S block
	  block
	| //@S ALAP stat
	  //@B The body %s is repeated as long as it remains enabled.
	  //@B Enabledness is determined by the first rule of the statement.
	  ALAP^ stat
	// | //@ S ATOM stat
	//  //@ B The body %s is evaluated atomically, meaning that it is only
	//  //@ B added to the transition system if it finishes successfully
	// ATOM^ stat
	| //@S LANGLE stat* RANGLE
	  //@B Atomically evaluated sequence of statements, surrounded by angle brackets.
	  //@B The transitions in the body are only added to the transition system if
	  //@B they complete successfully
	  open=LANGLE stat* close=RANGLE
	  -> ^(ATOM[$open] ^(BLOCK stat* TRUE[$close]))
	| //@S WHILE LPAR cond RPAR stat
	  //@B As long as the condition %1$s is successfully applied,
	  //@B the body %2$s is repeated. 
	  //@B <p>This is equivalent to "ALAP LCURLY %1$s SEMI %2$s RCURLY".
	  WHILE^ LPAR! cond RPAR! stat
	| //@S UNTIL LPAR cond RPAR stat
    //@B As long as the condition %2$s fails, the body %2$s is repeated. 
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
	  expr SEMI^ // SEMI retained for token positioning
	| //@S var_decl SEMI
	  //@B A variable declaration.
	  var_decl SEMI^ // SEMI retained for token positioning
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
    ( plus=PLUS -> ^(BLOCK $e ^(STAR[$plus] $e))
    | ast=ASTERISK -> ^(STAR[$ast] $e)
    | -> $e
    )
  | //@S expr: SHARP expr
    //@B Executes %s as long as possible.<p>    
    //@B Equivalent to "ALAP %1$s SEMI",
    //@B except that this is an expression and ALAP is a statement.
    op=SHARP expr_atom -> ^(ALAP[$op] expr_atom)
  ;

expr_atom
	: //@S expr: ANY
	  //@B Execution of an arbitrary non-property action.
	  ANY
	| //@S expr: OTHER
	  //@B Execution of an arbitrary non-property action not explicitly occurring 
	  //@B in this control program.
	  OTHER
	| //@S expr: LPAR expr RPAR
	  //@B Bracketed expression.
	  open=LPAR expr close=RPAR
	  -> ^(BLOCK[$open] expr TRUE[$close])
	| //@S expr: call
	  //@B Invokes a function or rule.
	  call
	; 

/** @H Rule or procedure call. */
call
	: //@S name [ LPAR arg_list RPAR ]
	  //@B Invokes a rule or procedure %s, with optional arguments %s.
	  //@P the rule or preocedure name
	  //@P optional comma-separated list of arguments
	  rule_name arg_list?
    { helper.registerCall($rule_name.tree); }
	  -> ^(CALL[$rule_name.start] rule_name arg_list?)
	;

/** @H Argument list 
  * @B List of arguments for a rule, function or recipe call. 
  */
arg_list
  : //@S [ arg (COMMA arg)* ]
    //@B Possibly empty, comma-separated list of arguments
    open=LPAR (arg (COMMA arg)*)? close=RPAR
    -> ^(ARGS[$open] arg* RPAR[$close])
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

/** Returns a "normalised" rule name.
 *  Normalisation means a non-qualified rule is looked up 
 *  in the imports, and if not found, prefixed with the package name
 */
rule_name
  : qual_name
    -> { helper.qualify($qual_name.tree) }
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
ATOM     : 'atomic';
BOOL     : 'bool';
CHOICE   : 'choice';
DO       : 'do';
ELSE     : 'else';
FALSE    : 'false';
FUNCTION : 'function';
IF       : 'if';
IMPORT   : 'import';
INT      : 'int';
NODE     : 'node';
OR       : 'or';
OTHER    : 'other';
OUT	     : 'out';
REAL     : 'real';
PACKAGE  : 'package';
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
LANGLE    : '<' ;
RANGLE    : '>' ;
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
