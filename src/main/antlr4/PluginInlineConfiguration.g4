grammar PluginInlineConfiguration;

@header {
package com.lonnyjacobson.webapp_minifier.antlr;
}

import Directives, JavaProperties, Primitives;

parse
   :  line* EOF
   ;

line
   :  SPACE* (directive | keyValue) SPACE* eol
   |  SPACE* COMMENT eol
   |  SPACE* LINEBREAK
   ;
