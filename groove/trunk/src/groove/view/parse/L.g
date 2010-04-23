grammar Labels;

options {
  language = Java;
  output = AST;
}

tokens {
  NEW;
  DEL;
  NOT;
  USE;
  CNEW;
  REM;
  
  FORALL;
  FORALLX;
  EXISTS;
  NESTED;
  
  INT;
  REAL;
  STRING;
  BOOL;
  ATTR;
  PROD;
  ARG;
  
  PAR;
  
  TYPE;
  FLAG;
  PATH;
  EMPTY;
}

@lexer::header {
package groove.view.parse;
}

@header {
package groove.view.parse;
}

graphLabel :
  prefix* actualGraphLabel;
 
prefix
   : ( forallP | forallxP | existsP ) (EQUALS IDENT)? COLON 
   | ( newP | delP | notP | useP | cnewP ) (EQUALS IDENT)? COLON 
   | nestedP COLON;

actualGraphLabel
   : COLON .*
   | remP COLON .*
   | valueLabel
   | nodeLabel
   | (~COLON)+ ;
   
valueLabel
   : intP COLON NUMBER -> ^(INT NUMBER)
   | realP COLON RNUMBER -> ^(REAL RNUMBER)
   | stringP COLON DQTEXT -> ^(STRING DQTEXT)
   | boolP COLON (trueP | falseP);
   
nodeLabel
   : typeP COLON IDENT -> ^(TYPE IDENT)
   | flagP COLON IDENT -> ^(FLAG IDENT);

ruleLabel
   : prefix* actualRuleLabel ;
   
actualRuleLabel
   : COLON .*
   | remP COLON .*
   | parP COLON
   | pathP COLON PLING? regExpr
   | valueLabel
   | nodeLabel
   | attrLabel
   | negLabel
   | posLabel ;
   
attrLabel
   : intP COLON IDENT? -> ^(INT IDENT)
   | realP COLON IDENT? -> ^(REAL IDENT)
   | stringP COLON IDENT? -> ^(STRING IDENT)
   | boolP COLON IDENT? -> ^(BOOL IDENT)
   | attrP COLON -> ^(ATTR IDENT)
   | prodP COLON -> ^(PROD IDENT)
   | argP COLON DIGIT+ -> ^(ARG IDENT);

negLabel
   : PLING posLabel ;
   
posLabel
   : wildcard
   | EQUALS
   | LBRACE regExpr RBRACE
   | SQTEXT
   | (~(SQTEXT|LBRACE|RBRACE|QUERY|EQUALS|COLON|PLING))* ;
   
regExpr
   : choice ;

choice
   : sequence (BAR! choice)? ;

sequence
   : unary (DOT! sequence)? ;

unary
   : MINUS unary
   | atom (STAR! | PLUS!)? ;

atom
   : SQTEXT
   | IDENTCHAR+
   | EQUALS
   | LPAR regExpr RPAR
   | wildcard ;

wildcard
   : QUERY IDENT? LSQUARE HAT? atom (COMMA atom)* RSQUARE
     -> ^(QUERY IDENT HAT atom*);

SQTEXT
   : SQUOTE (~(SQUOTE|BSLASH) | BSLASH (BSLASH|SQUOTE))* SQUOTE;

DQTEXT
   : DQUOTE (~(DQUOTE|BSLASH) | BSLASH (BSLASH|DQUOTE))* DQUOTE;

newP    : 'new';
delP    : 'del';
cnewP   : 'cnew';
notP    : 'not';
useP    : 'use';
remP    : 'rem';

forallP : 'forall';
forallxP : 'forallx';
existsP : 'exists';
nestedP : 'nested';

parP    : 'par';

attrP   : 'attr';
prodP   : 'prod';
argP    : 'arg';
intP    : 'int';
realP   : 'real';
stringP : 'string';
boolP   : 'bool';

typeP   : 'type';
flagP   : 'flag';
pathP   : 'path';

trueP   : 'true';
falseP  : 'false';

MINUS  : '-';
STAR   : '*';
PLUS   : '+';
DOT    : '.';
BAR    : '|';
HAT    : '^';
EQUALS : '=';
LBRACE : '{';
RBRACE : '}';
LPAR   : '(';
RPAR   : ')';
LSQUARE : '[';
RSQUARE : ']';
PLING  : '!';
QUERY  : '?'; 
COLON  : ':' ;
COMMA  : ',' ;
SQUOTE : '\'' ;
DQUOTE : '"' ;
DOLLAR : '$';
UNDER  : '_';
BSLASH : '\\' ;

IDENT
   : LETTER IDENTCHAR* 
   ;
   
NUMBER
   : DIGIT+
   ;

RNUMBER
   : (DIGIT+ (DOT DIGIT*)? | DOT DIGIT+)
   ;

LABEL
   : IDENTCHAR*
   ;

fragment IDENTCHAR
   : LETTER | DIGIT | DOLLAR | UNDER;

fragment LETTER : 'a'..'z'|'A'..'Z' ;
fragment DIGIT  : '0'..'9' ;