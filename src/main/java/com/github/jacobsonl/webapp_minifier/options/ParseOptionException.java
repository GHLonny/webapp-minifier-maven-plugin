package com.github.jacobsonl.webapp_minifier.options;

/**
 * This exception indicates a problem while parsing the plugin options.
 * 
 * @author Lonny
 */
public class ParseOptionException extends Exception {

   /**
    * The default serial version UID.
    */
   private static final long serialVersionUID = 1L;

   /**
    * Constructs a <code>ParseException</code>.
    */
   public ParseOptionException() {
   }

   /**
    * Constructs a <code>ParseException</code> with the provided message and
    * cause.
    * 
    * @param message
    *           the detail message. The detail message is saved for later
    *           retrieval by the {@link #getMessage()} method.
    * @param cause
    *           the cause (which is saved for later retrieval by the
    *           {@link #getCause()} method). (A <code>null</code> value is
    *           permitted, and indicates that the cause is nonexistent or
    *           unknown.)
    */
   public ParseOptionException(String message, Throwable cause) {
      super(message, cause);
   }

   /**
    * Constructs a <code>ParseException</code> with the provided message.
    * 
    * @param message
    *           the detail message. The detail message is saved for later
    *           retrieval by the {@link #getMessage()} method.
    */
   public ParseOptionException(String message) {
      super(message);
   }

   /**
    * Constructs a <code>ParseException</code> with the provided cause.
    * 
    * @param cause
    *           the cause (which is saved for later retrieval by the
    *           {@link #getCause()} method). (A <code>null</code> value is
    *           permitted, and indicates that the cause is nonexistent or
    *           unknown.)
    */
   public ParseOptionException(Throwable cause) {
      super(cause);
   }
}
