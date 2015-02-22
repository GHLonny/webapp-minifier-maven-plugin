grammar Primitives;

eol
   :  LINEBREAK
   ;

LINEBREAK
   :  '\r'? '\n'
   |  '\r'
   ;

COMMENT
   :  ' ' 
   |  '\t' 
   |  '\f'
   ;

SPACE
   : ' '
   | '\t'
   | '\f'
   ;

ALPHANUM
   :  'a'..'z'
   |  'A'..'Z'
   |  '0'..'9'
   ;
