package com.lonnyjacobson.webapp_minifier.options;

public interface InlineConfigurationHandler {

   /**
    * Handles the given option. The <code>key</code> parameter may be an overridable plugin
    * parameter or a supported directive.
    * 
    * @param key
    *           the overridable parameter name or directive name.
    * @param value
    *           the optional value.
    * @throws ParseOptionException
    *            if unable to handle the option.
    */
   public abstract void handleOption(final String key, final Object value)
         throws ParseOptionException;

}