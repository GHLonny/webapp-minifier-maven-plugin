package com.github.jacobsonl.webapp_minifier;

import com.googlecode.htmlcompressor.compressor.Compressor;

/**
 * This class defines the context for a minification session.
 * 
 * @author Lonny
 */
public class MinificationContext {
   /** The name of the minifier (e.g. YUI). */
   private String minifier;

   /** The Compressor. */
   private Compressor compressor;

   /** The builder for minified files. */
   private final MinifiedFileBuilder fileBuilder;

   /**
    * Constructs a new minification context.
    * 
    * @param minifier
    *           the name of the minifier.
    * @param compressor
    *           the compressor.
    * @param fileBuilder
    *           the minified file builder.
    */
   public MinificationContext(final String minifier,
         final Compressor compressor, final MinifiedFileBuilder fileBuilder) {
      this.minifier = minifier;
      this.compressor = compressor;
      this.fileBuilder = fileBuilder;
   }

   /**
    * Sets the name of the minifier.
    * 
    * @param minifier
    *           the name of the minifier.
    */
   public void setMinifier(final String minifier) {
      this.minifier = minifier;
   }

   /**
    * Returns the name of the minifier.
    * 
    * @return the name of the minifier.
    */
   public String getMinifier() {
      return this.minifier;
   }

   /**
    * Sets the compressor.
    * 
    * @param compressor
    *           the compressor.
    */
   public void setCompressor(final Compressor compressor) {
      this.compressor = compressor;
   }

   /**
    * Returns the compressor.
    * 
    * @return the compressor.
    */
   public Compressor getCompressor() {
      return this.compressor;
   }

   /**
    * Returns the minified file builder.
    * 
    * @return the minified file builder.
    */
   public MinifiedFileBuilder getFileBuilder() {
      return this.fileBuilder;
   }
}
