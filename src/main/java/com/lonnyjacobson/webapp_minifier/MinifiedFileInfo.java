package com.lonnyjacobson.webapp_minifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains details about a minified file.
 * 
 * @author Lonny
 */
public class MinifiedFileInfo {
   /** The minified file. */
   private final File file;

   /** The list of files minified into this file. */
   private final List<File> files = new ArrayList<File>();

   /** Indicates if embedded content is also included in the file. */
   private boolean embeddedIncluded;

   public MinifiedFileInfo(final File file) {
      if (file == null) {
         throw new IllegalArgumentException("The file cannot be null");
      }
      this.file = file;
   }

   /**
    * Returns the minified file.
    * 
    * @return the minified file.
    */
   public File getFile() {
      return this.file;
   }

   /**
    * Returns the list of files included in the minified file.
    * 
    * @return the list of files included in the minified file.
    */
   public List<File> getFiles() {
      return this.files;
   }

   /**
    * Indicates if the minified file contains embedded content.
    * 
    * @return <code>true</code> if the minified file contains embedded content.
    */
   public boolean isEmbeddedIncluded() {
      return this.embeddedIncluded;
   }

   /**
    * Sets the flag indicating if the minified file contains embedded content.
    * 
    * @param embeddedIncluded
    *           <code>true</code> if the minified file contains embedded
    *           content.
    */
   public void setEmbeddedIncluded(final boolean embeddedIncluded) {
      this.embeddedIncluded = embeddedIncluded;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (this.embeddedIncluded ? 1231 : 1237);
      result = prime * result
            + ((this.file == null) ? 0 : this.file.hashCode());
      result = prime * result
            + ((this.files == null) ? 0 : this.files.hashCode());
      return result;
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final MinifiedFileInfo other = (MinifiedFileInfo) obj;
      if (this.embeddedIncluded != other.embeddedIncluded) {
         return false;
      }
      if (this.file == null) {
         if (other.file != null) {
            return false;
         }
      } else if (!this.file.equals(other.file)) {
         return false;
      }
      if (this.files == null) {
         if (other.files != null) {
            return false;
         }
      } else if (!this.files.equals(other.files)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      final StringBuilder builder = new StringBuilder();
      builder.append("MinifiedFileInfo [file=");
      builder.append(this.file);
      builder.append(", embeddedIncluded=");
      builder.append(this.embeddedIncluded);
      builder.append(", files=");
      builder.append(this.files);
      builder.append("]");
      return builder.toString();
   }
}
