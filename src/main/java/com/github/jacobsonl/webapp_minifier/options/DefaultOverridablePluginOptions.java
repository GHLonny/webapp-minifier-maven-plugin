package com.github.jacobsonl.webapp_minifier.options;

import com.google.javascript.jscomp.CompilationLevel;

/**
 * This class is the default implementation of the
 * {@link OverridablePluginOptions} interface.
 * 
 * @author Lonny
 */
public class DefaultOverridablePluginOptions implements
      OverridablePluginOptions {
   /** Flag indicating if CSS minification should be skipped. */
   private boolean skipCssMinify;

   /** Flag indicating if embedded CSS minification should be skipped. */
   private boolean skipEmbeddedCssMinify;

   /** Flag indicating if JavaScript minification should be skipped. */
   private boolean skipJsMinify;

   /** Flag indicating if embedded JavaScript minification should be skipped. */
   private boolean skipEmbeddedJsMinify;

   /** Flag indicating if embedded CSS should be merged with external CSS files. */
   private boolean mergeEmbeddedCss;

   /**
    * Flag indicating if embedded JavaScript should be merged with external JS
    * files.
    */
   private boolean mergeEmbeddedJs;

   /** The JavaScript compressor to use. */
   private JavaScriptCompressor jsCompressorEngine;

   /** The closure compilation level. */
   private CompilationLevel closureCompilationLevel;

   /** The YUI CSS line break value. */
   private int yuiCssLineBreak;

   /** The flag indicating if YUI JavaScript optimizations should be disabled. */
   private boolean yuiJsDisableOptimizations;

   /** The YUI JavaScript link break value. */
   private int yuiJsLineBreak;

   /** Flag indicating if the YUI JS minification should munge the code. */
   private boolean yuiJsNoMunge;

   /** Flag indicating if the YUI JS minification should preserve semicolons. */
   private boolean yuiJsPreserveAllSemiColons;

   /** Constructs a new instance. */
   public DefaultOverridablePluginOptions() {

   }

   /**
    * Constructs a new instance from another {@link OverridablePluginOptions}.
    * 
    * @param options
    *           the other options to copy.
    */
   public DefaultOverridablePluginOptions(final OverridablePluginOptions options) {
      this.setClosureCompilationLevel(options.getClosureCompilationLevel());
      this.setJsCompressorEngine(options.getJsCompressorEngine());
      this.setMergeEmbeddedCss(options.isMergeEmbeddedCss());
      this.setMergeEmbeddedJs(options.isMergeEmbeddedJs());
      this.setSkipCssMinify(options.isSkipCssMinify());
      this.setSkipEmbeddedCssMinify(options.isSkipEmbeddedCssMinify());
      this.setSkipEmbeddedJsMinify(options.isSkipEmbeddedJsMinify());
      setSkipJsMinify(options.isSkipJsMinify());
      setYuiCssLineBreak(options.getYuiCssLineBreak());
      setYuiJsDisableOptimizations(options.isYuiJsDisableOptimizations());
      setYuiJsLineBreak(options.getYuiJsLineBreak());
      setYuiJsNoMunge(options.isYuiJsNoMunge());
      setYuiJsPreserveAllSemiColons(options.isYuiJsPreserveAllSemiColons());
   }

   @Override
   public boolean isSkipCssMinify() {
      return this.skipCssMinify;
   }

   @Override
   public void setSkipCssMinify(final boolean flag) {
      this.skipCssMinify = flag;
   }

   @Override
   public boolean isSkipEmbeddedCssMinify() {
      return this.skipEmbeddedCssMinify;
   }

   @Override
   public void setSkipEmbeddedCssMinify(final boolean flag) {
      this.skipEmbeddedCssMinify = flag;
   }

   @Override
   public boolean isSkipJsMinify() {
      return this.skipJsMinify;
   }

   @Override
   public void setSkipJsMinify(final boolean flag) {
      this.skipJsMinify = flag;
   }

   @Override
   public boolean isSkipEmbeddedJsMinify() {
      return this.skipEmbeddedJsMinify;
   }

   @Override
   public void setSkipEmbeddedJsMinify(final boolean flag) {
      this.skipEmbeddedJsMinify = flag;
   }

   @Override
   public boolean isMergeEmbeddedCss() {
      return this.mergeEmbeddedCss;
   }

   @Override
   public void setMergeEmbeddedCss(final boolean flag) {
      this.mergeEmbeddedCss = flag;
   }

   @Override
   public boolean isMergeEmbeddedJs() {
      return this.mergeEmbeddedJs;
   }

   @Override
   public void setMergeEmbeddedJs(final boolean flag) {
      this.mergeEmbeddedJs = flag;
   }

   @Override
   public JavaScriptCompressor getJsCompressorEngine() {
      return this.jsCompressorEngine;
   }

   @Override
   public void setJsCompressorEngine(
         final JavaScriptCompressor jsCompressorEngine) {
      this.jsCompressorEngine = jsCompressorEngine;
   }

   @Override
   public CompilationLevel getClosureCompilationLevel() {
      return this.closureCompilationLevel;
   }

   @Override
   public void setClosureCompilationLevel(
         final CompilationLevel compilationLevel) {
      this.closureCompilationLevel = compilationLevel;
   }

   @Override
   public int getYuiCssLineBreak() {
      return this.yuiCssLineBreak;
   }

   @Override
   public void setYuiCssLineBreak(final int lineBreak) {
      this.yuiCssLineBreak = lineBreak;
   }

   @Override
   public boolean isYuiJsDisableOptimizations() {
      return this.yuiJsDisableOptimizations;
   }

   @Override
   public void setYuiJsDisableOptimizations(final boolean flag) {
      this.yuiJsDisableOptimizations = flag;
   }

   @Override
   public int getYuiJsLineBreak() {
      return this.yuiJsLineBreak;
   }

   @Override
   public void setYuiJsLineBreak(final int lineBreak) {
      this.yuiJsLineBreak = lineBreak;
   }

   @Override
   public boolean isYuiJsNoMunge() {
      return this.yuiJsNoMunge;
   }

   @Override
   public void setYuiJsNoMunge(final boolean flag) {
      this.yuiJsNoMunge = flag;
   }

   @Override
   public boolean isYuiJsPreserveAllSemiColons() {
      return this.yuiJsPreserveAllSemiColons;
   }

   @Override
   public void setYuiJsPreserveAllSemiColons(final boolean flag) {
      this.yuiJsPreserveAllSemiColons = flag;
   }
}
