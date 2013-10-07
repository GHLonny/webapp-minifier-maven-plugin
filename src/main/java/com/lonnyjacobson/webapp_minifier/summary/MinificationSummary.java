package com.lonnyjacobson.webapp_minifier.summary;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class summarizes the work done by the plugin.
 * 
 * @author Lonny
 */
@XmlRootElement
public class MinificationSummary {

   /** The HTML files processed. */
   @XmlElement
   private final Collection<HtmlFileSummary> htmlFiles = new ArrayList<HtmlFileSummary>();

   /**
    * Returns the HTML files processed.
    * 
    * @return the HTML files processed.
    */
   public Collection<HtmlFileSummary> getHtmlFiles() {
      return this.htmlFiles;
   }
}
