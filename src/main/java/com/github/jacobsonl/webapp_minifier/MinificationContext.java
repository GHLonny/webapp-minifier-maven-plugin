package com.github.jacobsonl.webapp_minifier;

import com.googlecode.htmlcompressor.compressor.Compressor;

/**
 * This class defines the context for a minification session.
 * 
 * @author Lonny
 */
public class MinificationContext {
   /** The name of the minifier (e.g. YUI). */
   private final String minifier;

   /** The Compressor. */
   private final Compressor compressor;

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
   public MinificationContext(String minifier, Compressor compressor,
         MinifiedFileBuilder fileBuilder) {
      this.minifier = minifier;
      this.compressor = compressor;
      this.fileBuilder = fileBuilder;
   }

   /**
    * Returns the name of the minifier.
    * 
    * @return the name of the minifier.
    */
   public String getMinifier() {
      return minifier;
   }

   /**
    * Returns the compressor.
    * 
    * @return the compressor.
    */
   public Compressor getCompressor() {
      return compressor;
   }

   /**
    * Returns the minified file builder.
    * 
    * @return the minified file builder.
    */
   public MinifiedFileBuilder getFileBuilder() {
      return fileBuilder;
   }
}
