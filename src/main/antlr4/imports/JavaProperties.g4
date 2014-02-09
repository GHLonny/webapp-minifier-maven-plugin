grammar JavaProperties;

import Primitives;

keyValue
  :  key separatorAndValue
     {
       // Replace all escaped `=` and `:`
       String k = $key.text.replace("\\:", ":").replace("\\=", "=");

       // Remove the  separator, if it exists
       String v = $separatorAndValue.text.replaceAll("^\\s*[:=]\\s*", "");

       // Remove all escaped line breaks with trailing spaces
       v = v.replaceAll("\\\\(\r?\n|\r)[ \t\f]*", "").trim();

       System.out.println("\nkey   : `" + k + "`");
       System.out.println("value : `" + v + "`");
     }
  ;

key
   :  keyChar+
   ;

keyChar
   :  ALPHANUM 
   |  BACKSLASH (COLON | EQUALS)
   ;

separatorAndValue
   :  (SPACE | COLON | EQUALS) value
   ;

value
   : valueChar*
   ;

valueChar
   :  ALPHANUM 
   |  SPACE 
   |  BACKSLASH LINEBREAK
   |  EQUALS
   |  COLON
   |  '_'
   ;

BACKSLASH : '\\';
COLON     : ':';
EQUALS    : '=';

COMMENT
   :  ('!' | '#') ~('\r' | '\n')*
   ;
