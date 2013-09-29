package com.github.jacobsonl.webapp_minifier;

import java.io.File;

/**
 * This class assists in the creation of {@link MinifiedFileInfo} instances.
 * 
 * @author Lonny
 */
public class MinifiedFileBuilder {

   /** The file count. */
   private int fileCount = 0;

   /** The current minified file. */
   private MinifiedFileInfo currentFile;

   /** The target directory for the minified files. */
   private File targetDirectory;

   /** The minified file prefix. */
   private String prefix;

   /** The minified file extension. */
   private String extension;

   /**
    * Constructs a new instance with the given parameters.
    * 
    * @param targetDirectory
    *           the target directory for minified files.
    * @param prefix
    *           the prefix for minified files.
    * @param extension
    *           the extension for minified files.
    */
   public MinifiedFileBuilder(File targetDirectory, String prefix,
         String extension) {
      if (targetDirectory == null) {
         throw new IllegalArgumentException(
               "The target directory cannot be null");
      }
      if (prefix == null) {
         throw new IllegalArgumentException("The prefix cannot be null");
      }
      if (extension == null) {
         throw new IllegalArgumentException("The file extension cannot be null");
      }
      this.targetDirectory = targetDirectory;
      this.prefix = prefix;
      this.extension = extension;
   }

   /**
    * Indicates if the next call to {@link #getCurrentFile()} will return a new
    * file.
    * 
    * @return <code>true</code> if the next request for a
    *         {@link MinifiedFileInfo} will be a new instance.
    */
   public boolean isNewFile() {
      return currentFile == null;
   }

   /**
    * Returns the currently minified file.
    * 
    * @return the currently minified file.
    */
   public MinifiedFileInfo getCurrentFile() {
      if (isNewFile()) {
         final File file = new File(targetDirectory, prefix + '-' + ++fileCount
               + '.' + extension);
         currentFile = new MinifiedFileInfo(file);
      }
      return currentFile;
   }

   /**
    * Finishes the currently minified file.
    */
   public void finishFile() {
      currentFile = null;
   }
}
