grammar Formula;

options {
	output=AST;
	ASTLabelType = FormulaTree;
}

tokens {
  CALL;  // operation call
  CONST; // constant expression
  PAR;   // rule parameter expression
  FIELD; // possibly typed field or variable
  OPER;  // possibly typed operator
  STRING;// string literal
  REAL;  // real number literal
  INT;   // integer literal
  BOOL;  // boolean literal
}

@lexer::header {
package groove.explore.syntax;
}

@header {
package groove.explore.syntax;
import groove.util.parse.FormatErrorSet;
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

/** Either a variable or constant, or an operator applied to terms. */
formula
  : or_expr EOF!
  ;

/** Disjunctive expression. */
or_expr
  : and_expr ((BAR|OR)^ and_expr)*
  ;

/** Conjunctive expression. */
and_expr
  : impl_expr ((AMP|AND)^ impl_expr)*
  ;

/** Implication expression. */
impl_expr
  : not_expr ((IMPL | IMPL_BY | EQUIV)^ not_expr)*
  ;

/** Negation expression. */
not_expr
  : NOT^ not_expr
  | atom_expr;

/** Atomic expression, i.e., without operator occurrences. */
atom_expr
  : TRUE
  | FALSE
  | call
  | par_expr
  ;

/** Parenthesised expression. */
par_expr
  : LPAR^ or_expr RPAR
  ;

call
  : qual_name LPAR (arg (COMMA arg)*)? close=RPAR
   -> ^(CALL qual_name arg* RPAR[$close,""])
  ;

qual_name
  : ID (DOT^ ID)*
  ;

/** Constant expression. */
arg
  : DONT_CARE
  | STRING_LIT
  | MINUS? NAT_LIT^
  | MINUS? REAL_LIT^
  | TRUE
  | FALSE
  | ID // node identifier
  ;

// LEXER ACTIONS

TRUE : 'true';
FALSE : 'false';

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

NAT_LIT
  : NaturalNumber 
  ;

fragment
// can't include unary - in the constant as then parsing 1-2 would go wrong
NaturalNumber
  : '0' 
  | '1'..'9' ('0'..'9')*     
  ;

REAL_LIT
  : NonIntegerNumber
  ;

fragment
// can't include unary - in the constant as then parsing 0.1-2.0 would go wrong
NonIntegerNumber
    :   ('0' .. '9')+ '.' ('0' .. '9')*
    |   '.' ( '0' .. '9' )+
    ;

ID  : ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;

AMP       : '&' ;
AND       : '&&' ;
BAR       : '|' ;
BSLASH    : '\\' ;
COMMA     : ',' ;
DONT_CARE : '_' ;
DOT       : '.' ;
EQUIV     : '<->' ;
MINUS     : '-' ;
LPAR      : '(' ;
RPAR      : ')' ;
IMPL      : '->' ;
IMPL_BY   : '<-' ;
OR        : '||' ;
NOT       : '!' ;
QUOTE     : '"' ;

WS  :   (   ' '
        |   '\t'
        |   '\r'
        |   '\n'
        )+
        { $channel=HIDDEN; }
    ;    
