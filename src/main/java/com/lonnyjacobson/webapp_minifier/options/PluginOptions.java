package com.lonnyjacobson.webapp_minifier.options;

import java.io.File;
import java.util.Properties;

/**
 * This class packages the options for the plugin such that they can be easily
 * shared between classes.
 * 
 * @author Lonny
 */
public interface PluginOptions extends OverridablePluginOptions {
   /**
    * Returns the source directory.
    * 
    * @return the source directory.
    */
   File getSourceDirectory();

   /**
    * Sets the source directory.
    * 
    * @param sourceDirectory
    *           the source directory.
    */
   void setSourceDirectory(File sourceDirectory);

   /**
    * Returns the target directory.
    * 
    * @return the target directory.
    */
   File getTargetDirectory();

   /**
    * Sets the target directory.
    * 
    * @param targetDirectory
    *           the target directory.
    */
   void setTargetDirectory(File targetDirectory);

   /**
    * Returns the character encoding.
    * 
    * @return the character encoding.
    */
   String getEncoding();

   /**
    * Sets the character encoding.
    * 
    * @param encoding
    *           the character encoding.
    */
   void setEncoding(String encoding);

   /**
    * Returns the minified CSS file prefix.
    * 
    * @return the file prefix.
    */
   String getCssPrefix();

   /**
    * Sets the minified CSS file prefix.
    * 
    * @param prefix
    *           the file prefix.
    */
   void setCssPrefix(String prefix);

   /**
    * Returns the minified JavaScript file prefix.
    * 
    * @return the file prefix.
    */
   String getJsPrefix();

   /**
    * Sets the minified JavaScript file prefix.
    * 
    * @param prefix
    *           the file prefix.
    */
   void setJsPrefix(String prefix);

   /**
    * Returns the <code>Properties</code> instance that contains the other
    * directories to find CSS and JavaScript.
    * 
    * @return the other directories where CSS and JavaScript may be found.
    */
   Properties getOtherDirectories();
}
