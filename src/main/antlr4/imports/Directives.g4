grammar Directives;

import Primitives;

directive 
   :  (SPLITJS | SPLITCSS);

SPLITJS  : 'split-javascript';
SPLITCSS : 'split-css';