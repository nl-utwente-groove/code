grammar Label;

options {
  language = Java;
  output = AST;
}

tokens {
  NEW='new';
  DEL='del';
  NOT='not';
  USE='use';
  CNEW='cnew';
  REM='rem';
  
  FORALL='forall';
  FORALLX='forallx';
  EXISTS='exists';
  NESTED='nested';
  
  INT='int';
  REAL='real';
  STRING='string';
  BOOL='bool';
  ATTR='attr';
  PROD='prod';
  ARG='arg';
  
  PAR='par';
  
  TYPE='type';
  FLAG='flag';
  PATH='path';
  EMPTY;
  ATOM;
  
  TRUE='true';
  FALSE='false';
}

@lexer::header {
package groove.view.parse;
}

@header {
package groove.view.parse;
}

@members {
    private boolean isGraph;
    public void setIsGraph(boolean isGraph) {
        this.isGraph = isGraph;
    }
}

label
   : prefix* actualLabel? EOF!
   ;

prefix
   : ( FORALL^ | FORALLX^ | EXISTS^ ) (EQUALS IDENT)? COLON! 
   | ( NEW^ | DEL^ | NOT^ | USE^ | CNEW^ ) (EQUALS IDENT)? COLON!
   | NESTED COLON^;

actualLabel
   : COLON a=(.*) -> ^(ATOM $a)
   | REM^ COLON! .* 
   | PAR^ (EQUALS! DOLLAR! NUMBER)? COLON!
   | PATH^ COLON! PLING? regExpr
   | attrLabel
   | nodeLabel
   | (graphDefault EOF) => { isGraph }? => graphDefault -> ^(ATOM graphDefault)
   | (ruleLabel EOF) => { !isGraph }? => ruleLabel
   ;

ruleLabel
   : wildcard
   | EQUALS
   | LBRACE! regExpr RBRACE!
   | sqText -> ^(ATOM sqText)
   | PLING^ ruleLabel
   | ruleDefault -> ^(ATOM ruleDefault)
   ;

graphDefault
   : (~COLON)+
   ;

ruleDefault
   : ~(EQUALS | QUERY | PLING | SQUOTE | LBRACE | RBRACE | BSLASH | COLON) (~(SQUOTE | LBRACE | RBRACE | BSLASH | COLON))*
   ;

nodeLabel
   : TYPE^ COLON! IDENT
   | FLAG^ COLON! IDENT;
   
attrLabel
   : INT^ COLON! (NUMBER | IDENT)?
   | REAL^ COLON! (rnumber | IDENT)?
   | STRING^ COLON! (DQTEXT | IDENT)?
   | BOOL^ COLON! (TRUE | FALSE | IDENT)?
   | ATTR^ COLON!
   | PROD^ COLON!
   | ARG^ COLON! NUMBER
   ;

regExpr
   : choice ;

choice
   : sequence (BAR^ choice)? ;

sequence
   : unary (DOT^ sequence)? ;

unary
   : MINUS unary
   | atom (STAR^ | PLUS^)? ;

atom
   : sqText -> ^(ATOM sqText)
   | atomLabel -> ^(ATOM atomLabel)
   | EQUALS
   | LPAR regExpr RPAR
   | wildcard
   ;

atomLabel
   : (NUMBER | IDENT | IDENTCHAR)*
   ;
   
wildcard
   : QUERY^ IDENT? LSQUARE! HAT? atom (COMMA! atom)* RSQUARE!
   ;

sqText
   : SQUOTE! (~(SQUOTE|BSLASH) | sqTextSpecial)* SQUOTE!;

sqTextSpecial
   : BSLASH c=(BSLASH|SQUOTE) -> $c
   ;

fragment DQTEXT
   : DQUOTE (~(DQUOTE|BSLASH) | BSLASH (BSLASH|DQUOTE))* DQUOTE;

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

rnumber
   : (NUMBER (DOT (NUMBER|))? | DOT NUMBER)
   ;

fragment IDENTCHAR
   : LETTER | DIGIT | DOLLAR | UNDER;

fragment LETTER : 'a'..'z'|'A'..'Z' ;
fragment DIGIT  : '0'..'9' ;
