package com.github.webapp_minifier.replacer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Implementations of this interface aid in the modification of original HTML
 * files by replacing tags and/or values as directed by the {@link NodeHandler}.
 * 
 * @author Lonny
 */
public interface TagReplacer {
   /**
    * Processes the given input stream.
    * 
    * @param inputStream
    *           an input stream to the HTML source to process.
    * @param handler
    *           the handler for the nodes encountered in the source.
    * @param baseUri
    *           The URL where the HTML was retrieved from, to resolve relative
    *           links against.
    * @param outputStream
    *           the destination for the processed input stream.
    * @throws IOException
    *            if an error occurs while processing the input or writing the
    *            output.
    */
   void process(InputStream inputStream, NodeHandler handler, String baseUri,
         OutputStream outputStream) throws IOException;
}
