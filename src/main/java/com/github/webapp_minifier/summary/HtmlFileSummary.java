package com.github.webapp_minifier.summary;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Summarizes the minification performed on a single HTML file.
 * 
 * @author Lonny
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class HtmlFileSummary {

   /** The file that references the code. */
   @XmlAttribute
   private File file;

   /** The individual minification results. */
   private final Collection<MinifiedFileMetrics> minifiedFiles = new ArrayList<MinifiedFileMetrics>();

   /**
    * Sets the file that references the code.
    * 
    * @param file
    *           the referencing file.
    */
   public void setFile(final File file) {
      this.file = file;
   }

   /**
    * Returns the file that references the code.
    * 
    * @return the referencing file.
    */
   public File getFile() {
      return this.file;
   }

   /**
    * Returns the collection of individual minification results.
    * 
    * @return the collection of individual minification results.
    */
   public Collection<MinifiedFileMetrics> getMinifiedFiles() {
      return this.minifiedFiles;
   }
}
