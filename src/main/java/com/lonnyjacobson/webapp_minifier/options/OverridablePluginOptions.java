package com.lonnyjacobson.webapp_minifier.options;

import com.google.javascript.jscomp.CompilationLevel;

/**
 * This interface defines the plugin options that can be overridable.
 * 
 * @author Lonny
 */
public interface OverridablePluginOptions {
   /**
    * The JavaScript Compressor types.
    */
   enum JavaScriptCompressor {
      CLOSURE, YUI;
   }

   /**
    * Indicates if CSS minification should be skipped.
    * 
    * @return <code>true</code> if CSS minification should be skipped.
    */
   boolean isSkipCssMinify();

   /**
    * Sets the flag indicating if CSS should be minified.
    * 
    * @param flag
    *           <code>true</code> to skip minifying CSS.
    */
   void setSkipCssMinify(boolean flag);

   /**
    * Sets the flag indicating if embedded CSS should be minified.
    * 
    * @return <code>true</code> if embedded CSS minification should be skipped.
    */
   boolean isSkipEmbeddedCssMinify();

   /**
    * Sets the flag indicating if embedded CSS should be minified.
    * 
    * @param flag
    *           <code>true</code> to skip minifying embedded CSS.
    */
   void setSkipEmbeddedCssMinify(boolean flag);

   /**
    * Indicates if JavaScript minification should be skipped.
    * 
    * @return <code>true</code> if JavaScript minification should be skipped.
    */
   boolean isSkipJsMinify();

   /**
    * Sets the flag indicating if JavaScript should be minified.
    * 
    * @param flag
    *           <code>true</code> to skip JavaScript minification.
    */
   void setSkipJsMinify(boolean flag);

   /**
    * Sets the flag indicating if embedded JavaScript should be minified.
    * 
    * @return <code>true</code> if embedded JavaScript minification should be
    *         skipped.
    */
   boolean isSkipEmbeddedJsMinify();

   /**
    * Sets the flag indicating if embedded JavaScript should be minified.
    * 
    * @param flag
    *           <code>true</code> to skip minifying embedded JavaScript.
    */
   void setSkipEmbeddedJsMinify(boolean flag);

   /**
    * Indicates if embedded CSS should be merged into an external file.
    * 
    * @return <code>true</code> if embedded CSS should be merged into an
    *         external file.
    */
   boolean isMergeEmbeddedCss();

   /**
    * Sets the flag indicating if embedded CSS should be merged into an external
    * file. This does not guarantee that the embedded CSS will always be merged.
    * 
    * @param flag
    *           <code>true</code> to indicate if embedded CSS should be merged.
    */
   void setMergeEmbeddedCss(boolean flag);

   /**
    * Indicates if embedded JavaScript should be merged into an external file.
    * 
    * @return <code>true</code> if embedded JavaScript should be merged into an
    *         external file.
    */
   boolean isMergeEmbeddedJs();

   /**
    * Sets the flag indicating if embedded JavaScript should be merged into an
    * external file. This does not guarantee that the embedded JavaScript will
    * always be merged.
    * 
    * @param flag
    *           <code>true</code> to indicate if embedded JavaScript should be
    *           merged.
    */
   void setMergeEmbeddedJs(boolean flag);

   /**
    * Returns the JavaScript compressor engine.
    * 
    * @return the JavaScript compressor engine.
    */
   OverridablePluginOptions.JavaScriptCompressor getJsCompressorEngine();

   /**
    * Sets the JavaScript compressor engine.
    * 
    * @param jsCompressorEngine
    *           the JavaScript compressor engine.
    */
   void setJsCompressorEngine(
         OverridablePluginOptions.JavaScriptCompressor jsCompressorEngine);

   /**
    * Returns the Google Closure compilation level.
    * 
    * @return the Google Closure compilation level.
    */
   CompilationLevel getClosureCompilationLevel();

   /**
    * Sets the Google Closure compilation level.
    * 
    * @param compilationLevel
    *           the Google Closure compilation level.
    */
   void setClosureCompilationLevel(CompilationLevel compilationLevel);

   /**
    * Returns the YUI CSS line break value.
    * 
    * @return the YUI CSS line break value.
    */
   int getYuiCssLineBreak();

   /**
    * Sets the YUI CSS line break value.
    * 
    * @param lineBreak
    *           the YUI CSS line break value.
    */
   void setYuiCssLineBreak(int lineBreak);

   /**
    * Indicates if the YUI JavaScript micro-optimizations should be disabled.
    * 
    * @return <code>true</code> if YUI JavaScript micro-optimizations should be
    *         disabled.
    */
   boolean isYuiJsDisableOptimizations();

   /**
    * Sets the flag indicating if YUI JavaScript micro-optimizations should be
    * disabled.
    * 
    * @param flag
    *           <code>true</code> if YUI JavaScript micro-optimizations should
    *           be disabled.
    */
   void setYuiJsDisableOptimizations(boolean flag);

   /**
    * Returns the YUI JavaScript line break value.
    * 
    * @return the YUI JavaScript line break value.
    */
   int getYuiJsLineBreak();

   /**
    * Sets the YUI JavaScript line break value.
    * 
    * @param lineBreak
    *           the YUI JavaScript line break value.
    */
   void setYuiJsLineBreak(int lineBreak);

   /**
    * Indicates if the YUI JavaScript code obfuscation should be performed.
    * 
    * @return <code>true</code> if the YUI minifier should only minify and not
    *         obfuscate JavaScript.
    */
   boolean isYuiJsNoMunge();

   /**
    * Sets the flag indicating if YUI JavaScript code obfuscation should be
    * performed.
    * 
    * @param flag
    *           <code>true</code> if the YUI minifier should only minify and not
    *           obfuscate JavaScript.
    */
   void setYuiJsNoMunge(boolean flag);

   /**
    * Indicates if the YUI JavaScript minifier should preserve all semicolons.
    * 
    * @return <code>true</code> if semicolons should be preserved.
    */
   boolean isYuiJsPreserveAllSemiColons();

   /**
    * Sets the flag indicating if the YUI JavaScript minifier should preserve
    * all semicolons.
    * 
    * @param flag
    *           <code>true</code> if semicolons should be preserved..
    */
   void setYuiJsPreserveAllSemiColons(boolean flag);

}