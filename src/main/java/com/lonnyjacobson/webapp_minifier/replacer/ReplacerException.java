package com.lonnyjacobson.webapp_minifier.replacer;

/**
 * <code>ReplacerException</code> is a runtime exception that is thrown when an
 * error occurs during HTML parsing or tag replacement.
 * 
 * @author Lonny
 */
public class ReplacerException extends RuntimeException {
   /** serialVersionUID */
   private static final long serialVersionUID = -7854000087848439427L;

   /**
    * Constructs a <code>ReplacerException</code>.
    */
   public ReplacerException() {
   }

   /**
    * Constructs a <code>ReplacerException</code>.
    * 
    * @param message
    *           the detail message. The detail message is saved for later
    *           retrieval by the {@link #getMessage()} method.
    */
   public ReplacerException(final String message) {
      super(message);
   }

   /**
    * Constructs a <code>ReplacerException</code>.
    * 
    * @param cause
    *           the cause (which is saved for later retrieval by the
    *           {@link #getCause()} method). (A <code>null</code> value is
    *           permitted, and indicates that the cause is nonexistent or
    *           unknown.)
    */
   public ReplacerException(final Throwable cause) {
      super(cause);
   }

   /**
    * Constructs a <code>ReplacerException</code>.
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
   public ReplacerException(final String message, final Throwable cause) {
      super(message, cause);
   }
}
