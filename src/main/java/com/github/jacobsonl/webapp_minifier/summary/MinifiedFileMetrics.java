package com.github.jacobsonl.webapp_minifier.summary;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class contains the metrics for a single embedded or standalone file
 * containing CSS or JavaScript.
 * 
 * @author Lonny
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class MinifiedFileMetrics {

   /** Constant to use when describing the embedded CSS source/destination. */
   public static final String EMBEDDED_CSS = "Embedded CSS";

   /**
    * Constant to use when describing the embedded JavaScript
    * source/destination.
    */
   public static final String EMBEDDED_JS = "Embedded JavaScript";

   /** The source of the minified code. */
   @XmlAttribute
   private String source;

   /** The destination of the minified code. */
   @XmlAttribute
   private String destination;

   /** The name of the minifier used on the file. */
   @XmlElement
   private String minifier;

   /** The number of nanoseconds to minify the input. */
   @XmlElement
   private long time;

   /** The original length of the code. */
   @XmlElement
   private int originalLength;

   /** The minified length of the code. */
   @XmlElement
   private int minifiedLength;

   /**
    * Sets the source of the minified code.
    * 
    * @param source
    *           the code's source (e.g. file name).
    */
   public void setSource(String source) {
      this.source = source;
   }

   /**
    * Returns the source of the minified code.
    * 
    * @return the code's source (e.g. file name).
    */
   public String getSource() {
      return this.source;
   }

   /**
    * Sets the destination of the minified code.
    * 
    * @param destination
    *           the code's destination (e.g. file name).
    */
   public void setDestination(String destination) {
      this.destination = destination;
   }

   /**
    * Returns the destination of the minified code.
    * 
    * @return the code's destination (e.g. file name).
    */
   public String getDestination() {
      return this.destination;
   }

   /**
    * Sets the number of nanoseconds to minify the input.
    * 
    * @param time
    *           the number of nanoseconds to minify the input.
    */
   public void setTime(long time) {
      this.time = time;
   }

   /**
    * Returns the number of nanoseconds to minify the input.
    * 
    * @return the number of nanoseconds to minify the input.
    */
   public long getTime() {
      return this.time;
   }

   /**
    * Sets the original length of the code.
    * 
    * @param length
    *           the original length.
    */
   public void setOriginalLength(int length) {
      this.originalLength = length;
   }

   /**
    * Returns the original length of the code.
    * 
    * @return the original length.
    */
   public int getOriginalLength() {
      return this.originalLength;
   }

   /**
    * Sets the minified length of the code.
    * 
    * @param length
    *           the minified length.
    */
   public void setMinifiedLength(int length) {
      this.minifiedLength = length;
   }

   /**
    * Returns the minified length of the code.
    * 
    * @return the minified length.
    */
   public int getMinifiedLength() {
      return this.minifiedLength;
   }

   /**
    * Sets the minifier used on this file.
    * 
    * @param minifier
    *           the minifier used.
    */
   public void setMinifier(String minifier) {
      this.minifier = minifier;
   }

   /**
    * Returns the minifier used on this file.
    * 
    * @return the minifier used.
    */
   public String getMinifier() {
      return this.minifier;
   }
}
