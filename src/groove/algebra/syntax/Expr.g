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
  CLOSE; // imaginary token for the end of an expression
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
  : subexpr EOF!
  ;

/** Analogous to expression; distinguished to avoid initial recursion. */
subexpr
  : constant
  | variableOrField
  | call
  ;

constant
  : prefix=ID COLON literal
    -> ^(CONST literal ID)
  | literal
    -> ^(CONST literal)
  ;

parameter
  : prefix=ID DOLLAR INT_LIT
    -> ^(PAR INT_LIT $prefix)
  | DOLLAR INT_LIT
    -> ^(PAR INT_LIT)
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
  : oper LPAR (subexpr (COMMA subexpr)*)? close=RPAR
   -> ^(CALL oper subexpr* CLOSE[$close,""])
  ;

oper
  : prefix=ID COLON name=ID -> ^(OPER $name $prefix)
  | ID -> ^(OPER ID)
  ;

literal
  : REAL_LIT -> ^(REAL REAL_LIT)
  | INT_LIT -> ^(INT INT_LIT)
  | STRING_LIT -> ^(STRING STRING_LIT)
  | TRUE -> ^(BOOL TRUE)
  | FALSE -> ^(BOOL FALSE)
  ;

// LEXER ACTIONS

TRUE : 'true';
FALSE : 'false';

INT_LIT
  : IntegerNumber 
  ;

fragment
IntegerNumber
  : '-'? '0' 
  | '-'? '1'..'9' ('0'..'9')*     
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
COMMA     : ',' ;
SEMI      : ';' ;
COLON     : ':' ;
LPAR      : '(' ;
RPAR      : ')' ;
LCURLY    : '{' ;
RCURLY    : '}' ;

WS  :   (   ' '
        |   '\t'
        |   '\r'
        |   '\n'
        )+
        { $channel=HIDDEN; }
    ;    
