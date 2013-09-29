package com.github.jacobsonl.webapp_minifier.replacer;

import org.apache.maven.plugin.logging.Log;

/**
 * @author Lonny
 */
public final class TagReplacerFactory {

   public static TagReplacer getReplacer(final String parser, final Log log,
         final String charsetName) {
      TagReplacer processor = null;
      if ("jsoup".equalsIgnoreCase(parser)) {
         processor = new JsoupTagReplacer(log, charsetName);
      } else {
         throw new IllegalArgumentException("The parser '" + parser
               + "' is not supported");
      }
      return processor;
   }
}
