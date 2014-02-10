package com.lonnyjacobson.webapp_minifier.options;

/**
 * @author Lonny
 */
public interface DirectiveHandler {
   /**
    * Directs the plugin to start writing minified CSS to a new file.
    */
   void splitCss();

   /**
    * Directs the plugin to start writing minified JavaScript to a new file.
    */
   void splitJavaScript();
}
