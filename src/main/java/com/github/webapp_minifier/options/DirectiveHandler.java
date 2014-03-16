package com.github.webapp_minifier.options;

/**
 * Implementations of the <code>DirectiveHandler</code> receive notification of a directive in
 * configuration and react to it.
 * 
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
