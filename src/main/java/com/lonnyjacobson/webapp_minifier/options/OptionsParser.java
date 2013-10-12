package com.lonnyjacobson.webapp_minifier.options;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.IOUtil;

import com.google.common.base.Preconditions;
import com.google.javascript.jscomp.CompilationLevel;

/**
 * This class parses plugin options from input.
 * 
 * @author Lonny
 */
public class OptionsParser {
   /** The string indicating options to be parsed. */
   public static final String OPTION_HEADER = "webapp-minifier-maven-plugin:";

   /** The log instance. */
   private final Log log;

   /** Handles setting properties in the options instance. */
   private BeanUtilsBean beanUtils;

   /**
    * Constructs a new <code>OptionsParser</code> using the given log instance.
    * 
    * @param log
    *           the log instance.
    */
   public OptionsParser(final Log log) {
      this.log = log;
      final ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
      final Converter compilationLevelConverter = new Converter() {
         @SuppressWarnings("rawtypes")
         @Override
         public Object convert(final Class type, final Object value) {
            return CompilationLevel.valueOf((String) value);
         }
      };
      convertUtilsBean.register(compilationLevelConverter,
            CompilationLevel.class);
      final Converter jsCompressorConverter = new Converter() {
         @SuppressWarnings("rawtypes")
         @Override
         public Object convert(final Class type, final Object value) {
            return JavaScriptCompressor.valueOf((String) value);
         }
      };
      convertUtilsBean.register(jsCompressorConverter,
            JavaScriptCompressor.class);
      this.beanUtils = new BeanUtilsBean(convertUtilsBean);
   }

   /**
    * Determines if the provided text indicates that it contains options.
    * 
    * @param text
    *           the text to check.
    * @return <code>true</code> if the text <i>probably</i> contains options.
    */
   public boolean containsOptionsHeader(final String text) {
      return StringUtils.contains(text, OPTION_HEADER);
   }

   /**
    * Parses options from a <code>String</code> and creates a new options object
    * from them.
    * 
    * @param text
    *           the text to parse.
    * @return the new options instance.
    * @throws ParseOptionException
    *            if unable to parse the options or if an unsupported option is
    *            received.
    */
   public OverridablePluginOptions parse(final String text)
         throws ParseOptionException {
      return parse(text, new DefaultOverridablePluginOptions());
   }

   /**
    * Parses options from a <code>String</code> and creates a new options object
    * from them.
    * 
    * @param text
    *           the text to parse.
    * @param options
    *           the destination for the parsed options.
    * @return the new options instance.
    * @throws ParseOptionException
    *            if unable to parse the options or if an unsupported option is
    *            received.
    */
   public OverridablePluginOptions parse(final String text,
         final OverridablePluginOptions options) throws ParseOptionException {
      Preconditions.checkNotNull(text, "The text cannot be null");
      Preconditions.checkNotNull(options, "The options cannot be null");

      int index = text.indexOf(OPTION_HEADER);
      if (index >= 0) {
         index += OPTION_HEADER.length();
         final Properties properties = new Properties();
         final StringReader reader = new StringReader(text.substring(index));
         try {
            properties.load(reader);
            for (final Entry<Object, Object> entry : properties.entrySet()) {
               try {
                  this.log.debug("Setting " + entry.getKey() + " to "
                        + entry.getValue());
                  this.beanUtils.setProperty(options, (String) entry.getKey(),
                        entry.getValue());
               } catch (final Exception e) {
                  throw new ParseOptionException(
                        "An error occurred while handling option " + entry, e);
               }
            }
         } catch (final IOException e) {
            throw new ParseOptionException(e);
         } finally {
            IOUtil.close(reader);
         }
      }
      return options;
   }
}
