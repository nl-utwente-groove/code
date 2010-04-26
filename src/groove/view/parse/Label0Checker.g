tree grammar Label0Checker;

options {
  language = Java;
  output = AST;
  tokenVocab = Label0;
  ASTLabelType = CommonTree;
}

@header {
package groove.view.parse;
import java.util.List;
import java.util.LinkedList;
}

@members {
    private List<String> errors = new LinkedList<String>();
    public void displayRecognitionError(String[] tokenNames,
                                        RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        errors.add(hdr + " " + msg);
    }
    public List<String> getErrors() {
        return errors;
    }

    String concat(CommonTree seq) {
        List children = seq.getChildren();
        if (children == null) {
            return seq.getText();
        } else {
            StringBuilder result = new StringBuilder();
            for (Object token: seq.getChildren()) {
                result.append(((CommonTree) token).getText());
            }
            return result.toString();
        }
    }
}

label
  : prefixedLabel
  | specialLabel
  ;

prefixedLabel
  : ^(FORALL IDENT? prefixedLabel?)
  | ^(FORALLX IDENT? prefixedLabel?)
  | ^(EXISTS IDENT? prefixedLabel?)
  | ^(NEW IDENT? prefixedLabel?)
  | ^(DEL IDENT? prefixedLabel?)
  | ^(NOT IDENT? prefixedLabel?)
  | ^(USE IDENT? prefixedLabel?)
  | ^(CNEW IDENT? prefixedLabel?)
  | actualLabel
  ;

specialLabel
  : ^(REM text)
  | ^(PAR LABEL?)
  | ^(INT (number | IDENT)?)
  | ^(REAL (rnumber | IDENT)?)
  | ^(STRING (^(DQUOTE text) | IDENT)?)
  | ^(BOOL (bool | IDENT)?)
  | ATTR
  | PROD
  | ^(ARG NUMBER)
  ;

bool
  : TRUE | FALSE
  ;
  
number
  : NUMBER 
  ;

rnumber
  : NUMBER (DOT NUMER?)?
  | DOT NUMBER
  ;

actualLabel
  : ^(PLING regExpr)
  | regExpr
  ;

regExpr
  : ^(BAR regExpr regExpr)
  | ^(DOT regExpr regExpr)
  | ^(MINUS regExpr)
  | ^(STAR regExpr)
  | ^(PLUS regExpr)
  | EQUALS
  | ^(QUERY IDENT? HAT? atom*)
  | atom
  ;

atom
  : ^(ATOM (^(TYPE IDENT) ^(FLAG IDENT) | text))
  ;
  
text
  : tokenseq -> { new CommonTree(new CommonToken(IDENT, concat($tokenseq.tree))) }
  ;

tokenseq
  : ~'\n'*
  ;
