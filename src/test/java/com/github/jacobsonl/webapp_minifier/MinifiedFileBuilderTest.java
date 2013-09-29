package com.github.jacobsonl.webapp_minifier;

import static junitparams.JUnitParamsRunner.*;
import static org.junit.Assert.*;

import java.io.File;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class tests {@link MinifiedFileBuilder}.
 * 
 * @author Lonny
 */
@RunWith(JUnitParamsRunner.class)
public class MinifiedFileBuilderTest {
   /**
    * Tests
    * {@link MinifiedFileBuilder#MinifiedFileBuilder(File, String, String)}
    * exceptions.
    * 
    * @param targetDirectory
    *           the target directory.
    * @param prefix
    *           the minified file prefix.
    * @param extension
    *           the minified file's extension.
    */
   @Test(expected = IllegalArgumentException.class)
   @Parameters(method = "constructorExceptions")
   public void testConstructorExceptions(File targetDirectory, String prefix,
         String extension) {
      new MinifiedFileBuilder(targetDirectory, prefix, extension);
   }

   /**
    * Provides test data for
    * {@link #testConstructorExceptions(File, String, String)}.
    * 
    * @return the test data.
    */
   public Object constructorExceptions() {
      final File targetDirectory = new File("target");
      final String prefix = "prefix";
      final String extension = "ext";
      return new Object[][] { $(null, prefix, extension),
            new Object[] { targetDirectory, null, extension },
            new Object[] { targetDirectory, prefix, null }, };
   }

   /**
    * Tests the desired behavior of {@link MinifiedFileBuilder}.
    */
   @Test
   public void testMinifiedFileBuilder() {
      File targetDirectory = new File(System.getProperty("java.io.tmpdir"));
      final String prefix = "test";
      final String extension = "ext";
      MinifiedFileBuilder builder = new MinifiedFileBuilder(targetDirectory,
            prefix, extension);
      assertTrue("This should indicate a new file", builder.isNewFile());

      MinifiedFileInfo fileInfo1 = builder.getCurrentFile();
      assertNotNull("getCurrentFile(); should never return null", fileInfo1);
      assertFalse("This should not indicate a new file", builder.isNewFile());
      assertEquals(fileInfo1, builder.getCurrentFile());

      final File file = fileInfo1.getFile();
      assertNotNull("The file should not be null", file);
      assertEquals(targetDirectory, file.getParentFile());
      final String name = file.getName();
      assertEquals(prefix, name.substring(0, prefix.length()));
      assertEquals(extension, name.substring(name.lastIndexOf('.') + 1));

      builder.finishFile();
      assertTrue("This should indicate a new file", builder.isNewFile());

      MinifiedFileInfo fileInfo2 = builder.getCurrentFile();
      assertNotNull("getCurrentFile(); should never return null", fileInfo2);
      assertNotEquals(fileInfo2, fileInfo1);
      assertNotEquals(fileInfo2.getFile(), fileInfo1.getFile());
   }
}
