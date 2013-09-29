package com.github.jacobsonl.webapp_minifier.options;

import static junitparams.JUnitParamsRunner.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.SilentLog;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.jacobsonl.webapp_minifier.options.OverridablePluginOptions.JavaScriptCompressor;
import com.google.javascript.jscomp.CompilationLevel;

/**
 * This class tests {@link OptionsParser}.
 * 
 * @author Lonny
 */
@RunWith(JUnitParamsRunner.class)
public class OptionsParserTest {
   private OptionsParser parser;

   /**
    * Creates the instance to be tested.
    */
   @Before
   public void beforeTest() {
      Log log = new SilentLog();
      log = new SystemStreamLog();
      parser = new OptionsParser(log);
   }

   /**
    * Tests {@link OptionsParser#containsOptionsHeader(String)}.
    * 
    * @param text
    *           the text to test.
    * @param expected
    *           the expected result.
    */
   @Test
   @Parameters(method = "containsOptionsHeaderTestData")
   public void testContainsOptionsHeader(String text, boolean expected) {
      boolean actual = parser.containsOptionsHeader(text);
      assertEquals(expected, actual);
   }

   /**
    * Provides the test data for
    * {@link #testContainsOptionsHeader(String, boolean)}.
    * 
    * @return the test data.
    */
   public Object containsOptionsHeaderTestData() {
      return new Object[][] { $(null, false), new Object[] { "", false },
            new Object[] { OptionsParser.OPTION_HEADER.substring(1), false },
            new Object[] { OptionsParser.OPTION_HEADER, true },
            new Object[] { ' ' + OptionsParser.OPTION_HEADER, true },
            new Object[] { 'x' + OptionsParser.OPTION_HEADER, true }, };
   }

   /**
    * Tests {@link OptionsParser#containsOptionsHeader(String)}.
    * 
    * @param text
    *           the text to test.
    * @param property
    *           the property being tested.
    * @param expected
    *           the expected result.
    * @throws Exception
    *            if the test fails.
    */
   @Test
   @Parameters(method = "parseTestData")
   public void testParse(String text, String property, Object expected)
         throws Exception {
      if (property == null) {
         OverridablePluginOptions options = mock(OverridablePluginOptions.class);
         parser.parse(text, options);
         verifyZeroInteractions(options);
      } else {
         OverridablePluginOptions options = parser.parse(text);
         Object actual = PropertyUtils.getProperty(options, property);
         assertNotNull(actual);
         assertEquals(expected.getClass(), actual.getClass());
         assertEquals(property + " should match", expected, actual);
      }
   }

   /**
    * Provides the test data for
    * {@link #testContainsOptionsHeader(String, boolean)}.
    * 
    * @return the test data.
    */
   public Object parseTestData() {
      return $(
            $("", null, null),
            generateParseTestCase("closureCompilationLevel",
                  CompilationLevel.SIMPLE_OPTIMIZATIONS),
            generateParseTestCase("jsCompressorEngine",
                  JavaScriptCompressor.CLOSURE),
            generateParseTestCase("yuiCssLineBreak", 38),
            generateParseTestCase("skipCssMinify", true));
   }

   /**
    * Generates a single test case for {@link #parseTestData()}.
    * 
    * @param property
    *           the property to test.
    * @param value
    *           the property's value.
    * @return the test case.
    */
   private Object[] generateParseTestCase(String property, Object value) {
      return $(OptionsParser.OPTION_HEADER + '\n' + property + '=' + value,
            property, value);
   }

   /**
    * Tests {@link OptionsParser#containsOptionsHeader(String)}.
    * 
    * @param text
    *           the text to test.
    * @throws Exception
    *            if the test fails.
    */
   @Test(expected = ParseOptionException.class)
   @Parameters(method = "parseExceptionTestData")
   public void testParseException(String text, String property)
         throws Exception {
      OverridablePluginOptions options = parser.parse(text);
      Object actual = PropertyUtils.getProperty(options, property);
      fail("An exception should have been thrown.  The value of " + property
            + " was " + actual);
   }

   /**
    * Provides the test data for
    * {@link #testContainsOptionsHeader(String, boolean)}.
    * 
    * @return the test data.
    */
   public Object parseExceptionTestData() {
      return $(
            generateParseExceptionTestCase("closureCompilationLevel",
                  "xSIMPLE_OPTIMIZATIONS"),
            generateParseExceptionTestCase("jsCompressorEngine", "xCLOSURE"));
   }

   /**
    * Generates a single test case for {@link #parseTestData()}.
    * 
    * @param property
    *           the property to test.
    * @param value
    *           the property's value.
    * @return the test case.
    */
   private Object[] generateParseExceptionTestCase(String property, Object value) {
      return $(OptionsParser.OPTION_HEADER + '\n' + property + '=' + value,
            property);
   }
}
