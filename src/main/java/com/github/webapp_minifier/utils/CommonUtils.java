package com.github.webapp_minifier.utils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * @author Lonny
 */
public final class CommonUtils {
   /**
    * Checks if the collection is empty or <code>null</code>.
    * 
    * @param collection
    *           the collection to test
    * @return <code>true</code> if the collection is empty or <code>null</code>
    */
   public static boolean isEmpty(final Collection<?> collection) {
      return collection == null || collection.isEmpty();
   }

   /**
    * Checks if the array is empty or <code>null</code>.
    * 
    * @param array
    *           the array to test
    * @return <code>true</code> if the array is empty or <code>null</code>
    */
   public static <T> boolean isEmpty(final T[] array) {
      return array == null || array.length == 0;
   }

   /**
    * Determines the base URI for the given file somewhere under the base
    * directory.
    * 
    * @param file
    *           the file to search.
    * @param baseDirectory
    *           the base directory.
    * @return the base URI string to the given file.
    * @throws IOException
    *            if unable to create the canonical form of a {@link File}.
    */
   public static String getBaseUri(final File file, final File baseDirectory)
         throws IOException {
      File parentDirectory = file.getCanonicalFile().getParentFile();
      String baseUri = "";
      final File canonicalBase = baseDirectory.getCanonicalFile();
      while ((parentDirectory != null)
            && !parentDirectory.equals(canonicalBase)) {
         baseUri = parentDirectory.getName() + '/' + baseUri;
         parentDirectory = parentDirectory.getParentFile();
      }
      return baseUri;
   }
}
