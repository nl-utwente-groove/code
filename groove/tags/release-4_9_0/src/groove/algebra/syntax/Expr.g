grammar Expr;

options {
	output=AST;
	ASTLabelType = ExprTree;
}

tokens {
  CONST;
  VAR;
  PAR;
  CALL;
  FIELD;
  OPER;
  STRING;
  REAL;
  INT;
  BOOL;
}

@lexer::header {
package groove.algebra.syntax;
}

@header {
package groove.algebra.syntax;
import groove.grammar.model.FormatErrorSet;
}

@members {
    private FormatErrorSet errors = new FormatErrorSet();
    
    public void displayRecognitionError(String[] tokenNames,
            RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        this.errors.add(hdr + " " + msg, e.line, e.charPositionInLine);
    }

    public FormatErrorSet getErrors() {
        return this.errors;
    }
}

// PARSER ACTIONS
/** Either a variable or constant, or an operator applied to terms. */
expression
  : or_expr EOF!
  ;

or_expr
  : and_expr (BAR^ and_expr)*
  ;

and_expr
  : not_expr (AMP^ not_expr)*
  ;

not_expr
  : NOT^ not_expr
  | equal_expr;

equal_expr
  : compare_expr ((EQ | NEQ)^ compare_expr)*;

compare_expr
  : assign_expr ((LT | LE | GT | GE)^ assign_expr)*
  ;
  
assign_expr
  : add_expr
  ;
  
add_expr
  : mult_expr ((PLUS | MINUS)^ mult_expr)*
  ;

mult_expr
  : unary_expr ((ASTERISK | SLASH | PERCENT)^ unary_expr)*
  ;

unary_expr
  : MINUS^ unary_expr
  | atom_expr
  ;

atom_expr
  : constant
  | variableOrField
  | call
  | open=LPAR or_expr close=RPAR
    -> ^(LPAR[$open,""] or_expr RPAR[$close,""])
  ;

constant
  : prefix=ID COLON literal
    -> ^(CONST literal ID)
  | literal
    -> ^(CONST literal)
  ;

parameter
  : prefix=ID DOLLAR NAT_LIT
    -> ^(PAR NAT_LIT $prefix)
  | DOLLAR NAT_LIT
    -> ^(PAR NAT_LIT)
  ;

variableOrField
  : prefix=ID COLON name=ID 
    ( DOT field1=ID
      -> ^(FIELD $name $field1 $prefix)
    | -> ^(VAR $name $prefix)
    )
  | name=ID
    ( DOT field2=ID
      -> ^(FIELD $name $field2)
    | -> ^(VAR $name)
    )
  ;

call
  : oper LPAR (or_expr (COMMA or_expr)*)? close=RPAR
   -> ^(CALL oper or_expr* RPAR[$close,""])
  ;

oper
  : prefix=ID COLON name=ID -> ^(OPER $name $prefix)
  | ID -> ^(OPER ID)
  ;

literal
  : REAL_LIT -> ^(REAL REAL_LIT)
  | NAT_LIT -> ^(INT NAT_LIT)
  | STRING_LIT -> ^(STRING STRING_LIT)
  | TRUE -> ^(BOOL TRUE)
  | FALSE -> ^(BOOL FALSE)
  ;

// LEXER ACTIONS

TRUE : 'true';
FALSE : 'false';

NAT_LIT
  : Naturalumber 
  ;

fragment
Naturalumber
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
DOLLAR    : '$' ;
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
SLASH     : '/';
PERCENT   : '%';
COMMA     : ',' ;
SEMI      : ';' ;
COLON     : ':' ;
LPAR      : '(' ;
RPAR      : ')' ;
LCURLY    : '{' ;
RCURLY    : '}' ;
EQ        : '==' ;
NEQ       : '!=' ;
GT        : '>' ;
GE        : '>=' ;
LT        : '<' ;
LE        : '<=' ;

WS  :   (   ' '
        |   '\t'
        |   '\r'
        |   '\n'
        )+
        { $channel=HIDDEN; }
    ;    
