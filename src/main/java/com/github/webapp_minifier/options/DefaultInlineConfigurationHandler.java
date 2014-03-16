package com.github.webapp_minifier.options;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;

import com.google.javascript.jscomp.CompilationLevel;

/**
 * An <code>OptionHandler</code> handles individually parsed options. Options may override existing
 * configuration or direct processing.
 * 
 * @author Lonny
 */
public class DefaultInlineConfigurationHandler implements InlineConfigurationHandler {

   /** Handles setting properties in the options instance. */
   private BeanUtilsBean beanUtils;

   /** The names of overridable parameters. */
   private final Set<String> overridableNames;

   /** The overridable plugin options. */
   private OverridablePluginOptions options;

   public DefaultInlineConfigurationHandler(final OverridablePluginOptions options) {
      this.options = options;
      final ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
      final Converter compilationLevelConverter = new Converter() {
         @SuppressWarnings("rawtypes")
         @Override
         public Object convert(final Class type, final Object value) {
            return CompilationLevel.valueOf((String) value);
         }
      };
      convertUtilsBean.register(compilationLevelConverter, CompilationLevel.class);
      final Converter jsCompressorConverter = new Converter() {
         @SuppressWarnings("rawtypes")
         @Override
         public Object convert(final Class type, final Object value) {
            return JavaScriptCompressor.valueOf((String) value);
         }
      };
      convertUtilsBean.register(jsCompressorConverter, JavaScriptCompressor.class);
      this.beanUtils = new BeanUtilsBean(convertUtilsBean);
      this.overridableNames = getOverridableNames();
   }

   /**
    * Determines the overrideable property names.
    * 
    * @return the set of overridable property names.
    * @throws IllegalStateException
    *            if unable to determine the overridable property names.
    */
   protected Set<String> getOverridableNames() throws IllegalStateException {
      final Set<String> propertyNames;
      try {
         final BeanInfo beanInfo = Introspector.getBeanInfo(OverridablePluginOptions.class);
         propertyNames = new LinkedHashSet<String>();
         for (final PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
            propertyNames.add(descriptor.getName());
         }
      } catch (final IntrospectionException e) {
         throw new IllegalStateException(
               "Failed to get the bean information for OverridablePluginOptions", e);
      }
      return Collections.unmodifiableSet(propertyNames);
   }

   /**
    * @see com.github.webapp_minifier.options.InlineConfigurationHandler#handleOption(java.lang.String, java.lang.Object)
    */
   @Override
   public void handleOption(final String key, final Object value) throws ParseOptionException {
      if (this.overridableNames.contains(key)) {
         try {
            this.beanUtils.setProperty(this.options, key, value);
         } catch (final Exception e) {
            throw new ParseOptionException("An error occurred while handling option " + key + '='
                  + value, e);
         }
      } else {

      }
   }
}
