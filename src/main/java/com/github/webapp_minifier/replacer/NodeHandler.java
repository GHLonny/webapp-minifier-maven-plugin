package com.github.webapp_minifier.replacer;

import java.io.File;
import java.io.IOException;

import com.github.webapp_minifier.options.ParseOptionException;
import com.github.webapp_minifier.summary.MinificationSummary;

/**
 * Handler for nodes in an HTML document.
 * 
 * @author Lonny
 */
public interface NodeHandler {

   /**
    * Indicates that the given file is about to be parsed.
    * 
    * @param file
    *           the file to be parsed.
    */
   void start(File file);

   /**
    * Handles the comment.
    * 
    * @param text
    *           the comment's text.
    * @return <code>true</code> if the comment should be deleted.
    *         <code>false</code> to keep the comment.
    * @throws ParseOptionException
    *            if an error occurs while parsing options from the comment.
    */
   boolean handleComment(String text) throws ParseOptionException;

   /**
    * Handles the external CSS.
    * 
    * @param url
    *           the location of the external CSS.
    * @return the new value for the <code>href</code> attribute or
    *         <code>null</code> to delete the entire <code>link</code> element.
    * @throws IOException
    *            if an unexpected I/O error occurs.
    */
   String handleExternalCss(String url) throws IOException;

   /**
    * Handles the embedded CSS.
    * 
    * @param text
    *           the embedded CSS.
    * @param scoped
    *           indicates if the embedded CSS was scoped.
    * @return the new value for the embedded CSS or <code>null</code> to delete
    *         it.
    * @throws IOException
    *            if an unexpected I/O error occurs.
    */
   String handleEmbeddedCss(String text, boolean scoped) throws IOException;

   /**
    * Handles the external JavaScript.
    * 
    * @param url
    *           the location of the external JavaScript.
    * @return the new value for the <code>src</code> attribute or
    *         <code>null</code> to delete the entire <code>script</code>
    *         element.
    * @throws IOException
    *            if an unexpected I/O error occurs.
    */
   String handleExternalJs(String url) throws IOException;

   /**
    * Handles the embedded JavaScript.
    * 
    * @param text
    *           the embedded JavaScript.
    * @return the new value for the embedded JavaScript or <code>null</code> to
    *         delete it.
    * @throws IOException
    *            if an unexpected I/O error occurs.
    */
   String handleEmbeddedJs(String text) throws IOException;

   /**
    * Handles the text value.
    * 
    * @param text
    *           the text content.
    */
   void handleText(String text);

   /**
    * Handles any other type of node in the document.
    * 
    * @param nodeName
    *           the node name.
    */
   void handleOther(String nodeName);

   /**
    * Indicates that parsing of the current file has completed.
    */
   void complete();

   /**
    * Returns the minification summary.
    * 
    * @return the summary.
    */
   MinificationSummary getReport();
}
