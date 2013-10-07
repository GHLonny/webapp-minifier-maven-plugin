package com.lonnyjacobson.webapp_minifier.replacer;

import static junitparams.JUnitParamsRunner.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.SilentLog;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class tests {@link JsoupTagReplacer}.
 * 
 * @author Lonny
 */
@RunWith(JUnitParamsRunner.class)
public class JsoupTagReplacerTest {
   private static final Logger LOGGER = LoggerFactory
         .getLogger(JsoupTagReplacerTest.class);

   private static final String XHTML_DOCTYPE = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n";
   private static final String HTML4_DOCTYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n";
   private static final String HTML5_DOCTYPE = "<!DOCTYPE html>\n";
   private final String charsetName = "UTF-8";
   private JsoupTagReplacer tagReplacer;

   private final StringBuilder xhtmlStart = new StringBuilder(XHTML_DOCTYPE);
   private final StringBuilder html4Start = new StringBuilder(HTML4_DOCTYPE);
   private final StringBuilder html5Start = new StringBuilder(HTML5_DOCTYPE);
   private final StringBuilder xhtmlMiddle = new StringBuilder();
   private final StringBuilder html4Middle = new StringBuilder();
   private final StringBuilder html5Middle = new StringBuilder();
   private final String htmlEnd = "</body></html>";

   private static final String REPLACED_CSS_DECLARATION = "color:red";
   private final String replacedCss = "h1 {" + REPLACED_CSS_DECLARATION
         + ";}\n" + "p {color:blue;}";
   private static final String PRESERVED_CSS_DECLARATION = "height:3.14%";
   private final String preservedCss = "body {" + PRESERVED_CSS_DECLARATION
         + ";}\n";
   private static final String REPLACED_JS_DECLARATION = "var i=10;";
   private final String replacedJavaScript = REPLACED_JS_DECLARATION
         + "\nif (i < 5)\n" + "{\n" + "  // embedded JS\n" + "}\n";
   private static final String PRESERVED_JS_DECLARATION = "var i=3.14;";
   private final String preservedJavaScript = PRESERVED_JS_DECLARATION
         + "\nif (i < 5)\n" + "{\n" + "  // embedded JS\n" + "}\n";

   @Before
   public void before() {
      final Log log;
      if (LOGGER.isDebugEnabled()) {
         log = new SystemStreamLog();
      } else {
         log = new SilentLog();
      }
      this.tagReplacer = new JsoupTagReplacer(log, this.charsetName);

      this.xhtmlStart.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
      this.xhtmlStart.append("<head>");
      this.xhtmlStart
            .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
      this.xhtmlMiddle.append("<title>XHTML Test</title>");
      this.xhtmlMiddle.append("</head>");
      this.xhtmlMiddle.append("<body><h1>XHTML Test</h1>");

      this.html4Start.append("<html>");
      this.html4Start.append("<head>");
      this.html4Start
            .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
      this.html4Middle.append("<title>HTML 4 Test</title>");
      this.html4Middle.append("</head>");
      this.html4Middle.append("<body><h1>HTML 4 Test</h1>");

      this.html5Start.append("<html>");
      this.html5Start.append("<head>");
      this.html5Start.append("<meta charset=\"UTF-8\">");
      this.html5Middle.append("<title>HTML 5 Test</title>");
      this.html5Middle.append("</head>");
      this.html5Middle.append("<body><h1>HTML 5 Test</h1>");
   }

   /**
    * This enumeration indicates how CSS/JavaScript should be handled.
    * 
    * @author Lonny
    */
   private enum Handling {
      PRESERVE, REPLACE, IGNORE;
   }

   /**
    * Tests
    * {@link JsoupTagReplacer#process(InputStream, NodeHandler, String, OutputStream)}
    * for embedded CSS.
    * 
    * @param handling
    *           how the embedded CSS should be handled.
    * @param scoped
    *           if the <code>style</code> is scoped.
    * @param embeddedCss
    *           the embedded CSS.
    * @param input
    *           the test input.
    * @param baseUri
    *           the base URI string.
    * 
    * @throws IOException
    *            if an unexpected I/O exception occurs.
    */
   @Test
   @Parameters(method = "processEmbeddedCssTestData")
   public void testProcessEmbeddedCss(final Handling handling,
         final boolean scoped, final String embeddedCss, final String input,
         final String baseUri) throws IOException {
      final InputStream inputStream = new ByteArrayInputStream(
            input.getBytes(this.charsetName));
      final NodeHandler handler = mock(NodeHandler.class);
      final Answer<String> embeddedAnswer = new Answer<String>() {
         @Override
         public String answer(final InvocationOnMock invocation)
               throws Throwable {
            final Object[] args = invocation.getArguments();
            final String text = (String) args[0];
            if ((handling == Handling.PRESERVE)) {
               return text;
            }
            return null;
         }
      };
      when(handler.handleEmbeddedCss(anyString(), anyBoolean())).thenAnswer(
            embeddedAnswer);
      final ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
      this.tagReplacer.process(inputStream, handler, baseUri, baoStream);
      final String output = baoStream.toString(this.charsetName);

      switch (handling) {
      case PRESERVE:
         verify(handler).handleEmbeddedCss(embeddedCss, scoped);
         assertTrue("The output should contain the embedded CSS",
               output.contains(embeddedCss));
         break;
      case REPLACE:
         verify(handler).handleEmbeddedCss(embeddedCss, scoped);
         if (scoped) {
            assertTrue("The output should contain the embedded CSS",
                  output.contains(embeddedCss));
         } else {
            assertFalse("The output should not contain the embedded CSS",
                  output.contains(embeddedCss));
         }
         break;
      case IGNORE:
      default:
         verify(handler, never()).handleEmbeddedCss(embeddedCss, scoped);
         assertTrue("The output should contain the embedded CSS",
               output.contains(embeddedCss));
      }
   }

   /**
    * The data provider for
    * {@link #testProcessEmbeddedCss(Handling, boolean, String, String, String)}
    * 
    * @return the test data.
    */
   public Object processEmbeddedCssTestData() {
      final List<Object[]> testData = new ArrayList<Object[]>();
      final String baseUri = "";
      final String embeddedCss = "\nh1 {" + "color:red" + ";}\n"
            + "p {color:blue;}\n";

      // XHTML
      String htmlTest = this.xhtmlStart.toString();
      htmlTest += "\n<style type=\"text/css\">" + embeddedCss + "</style>\n";
      htmlTest += this.xhtmlMiddle + this.htmlEnd;
      testData.add($(Handling.PRESERVE, false, embeddedCss, htmlTest, baseUri));
      testData.add(new Object[] { Handling.REPLACE, false, embeddedCss,
            htmlTest, baseUri });

      htmlTest = this.xhtmlStart.toString();
      htmlTest += "\n<style type=\"text/css2\">" + embeddedCss + "</style>\n";
      htmlTest += this.xhtmlMiddle + this.htmlEnd;
      testData.add(new Object[] { Handling.IGNORE, false, embeddedCss,
            htmlTest, baseUri });

      // HTML 4
      htmlTest = this.html4Start.toString();
      htmlTest += "\n<style type=\"text/css\">" + embeddedCss + "</style>\n";
      htmlTest += this.html4Middle + this.htmlEnd;
      testData.add(new Object[] { Handling.PRESERVE, false, embeddedCss,
            htmlTest, baseUri });
      testData.add(new Object[] { Handling.REPLACE, false, embeddedCss,
            htmlTest, baseUri });

      htmlTest = this.html4Start.toString();
      htmlTest += "\n<style type=\"text/css2\">" + embeddedCss + "</style>\n";
      htmlTest += this.html4Middle + this.htmlEnd;
      testData.add(new Object[] { Handling.IGNORE, false, embeddedCss,
            htmlTest, baseUri });

      // HTML 5
      htmlTest = this.html5Start.toString();
      htmlTest += "\n<style type=\"text/css\">" + embeddedCss + "</style>\n";
      htmlTest += this.html5Middle + this.htmlEnd;
      testData.add(new Object[] { Handling.PRESERVE, false, embeddedCss,
            htmlTest, baseUri });
      testData.add(new Object[] { Handling.REPLACE, false, embeddedCss,
            htmlTest, baseUri });

      htmlTest = this.html5Start.toString();
      htmlTest += "\n<style type=\"text/css\" scoped>" + embeddedCss
            + "</style>\n";
      htmlTest += this.html5Middle + this.htmlEnd;
      testData.add(new Object[] { Handling.PRESERVE, true, embeddedCss,
            htmlTest, baseUri });
      testData.add(new Object[] { Handling.REPLACE, true, embeddedCss,
            htmlTest, baseUri });

      htmlTest = this.html5Start.toString();
      htmlTest += "\n<style type=\"text/css2\">" + embeddedCss + "</style>\n";
      htmlTest += this.html5Middle + this.htmlEnd;
      testData.add(new Object[] { Handling.IGNORE, false, embeddedCss,
            htmlTest, baseUri });

      htmlTest = this.html5Start.toString();
      htmlTest += "\n<!--<style type=\"text/css\">" + embeddedCss
            + "</style>-->\n";
      htmlTest += this.html5Middle + this.htmlEnd;
      testData.add(new Object[] { Handling.IGNORE, false, embeddedCss,
            htmlTest, baseUri });

      return testData.toArray(new Object[testData.size()][]);
   }

   /**
    * Tests
    * {@link JsoupTagReplacer#process(InputStream, NodeHandler, String, OutputStream)}
    * for embedded JavaScript.
    * 
    * @param handling
    *           how the embedded JavaScript should be handled.
    * @param embeddedJs
    *           the embedded JavaScript.
    * @param input
    *           the test input.
    * @param baseUri
    *           the base URI string.
    * @throws IOException
    *            if an unexpected I/O exception occurs.
    */
   @Test
   @Parameters(method = "processEmbeddedJsTestData")
   public void testProcessEmbeddedJs(final Handling handling,
         final String embeddedJs, final String input, final String baseUri)
         throws IOException {
      final InputStream inputStream = new ByteArrayInputStream(
            input.getBytes(this.charsetName));
      final NodeHandler handler = mock(NodeHandler.class);
      final Answer<String> embeddedAnswer = new Answer<String>() {
         @Override
         public String answer(final InvocationOnMock invocation)
               throws Throwable {
            final Object[] args = invocation.getArguments();
            final String text = (String) args[0];
            if (handling == Handling.PRESERVE) {
               return text;
            }
            return null;
         }
      };
      when(handler.handleEmbeddedJs(anyString())).thenAnswer(embeddedAnswer);
      final ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
      this.tagReplacer.process(inputStream, handler, baseUri, baoStream);
      final String output = baoStream.toString(this.charsetName);

      switch (handling) {
      case PRESERVE:
         verify(handler).handleEmbeddedJs(embeddedJs);
         assertTrue("The output should contain the embedded JavaScript: "
               + embeddedJs, output.contains(embeddedJs));
         break;
      case REPLACE:
         verify(handler).handleEmbeddedJs(embeddedJs);
         assertFalse("The output should not contain the embedded JavaScript: "
               + embeddedJs, output.contains(embeddedJs));
         break;
      case IGNORE:
      default:
         verify(handler, never()).handleEmbeddedJs(embeddedJs);
         assertTrue("The output should contain the embedded JavaScript: "
               + embeddedJs, output.contains(embeddedJs));
      }
   }

   /**
    * The data provider for
    * {@link #testProcessEmbeddedJs(Handling, String, String, String)}.
    * 
    * @return the test data.
    */
   public Object processEmbeddedJsTestData() {
      final List<Object[]> testData = new ArrayList<Object[]>();
      final String baseUri = "";
      final String embeddedJs = "\nvar i=0; for (i=0;i<10;i++) {alert(i);}\n";

      // XHTML
      final String xhtmlEmbeddedJs = "\n//<![CDATA[" + embeddedJs + "\n//]]>\n";
      String htmlTest = this.xhtmlStart.toString() + this.xhtmlMiddle;
      htmlTest += "\n<script type=\"TeXt/JaVaScRiPt\">" + xhtmlEmbeddedJs
            + "</script>\n";
      htmlTest += this.htmlEnd;
      testData.add(new Object[] { Handling.PRESERVE, xhtmlEmbeddedJs, htmlTest,
            baseUri });
      testData.add(new Object[] { Handling.REPLACE, xhtmlEmbeddedJs, htmlTest,
            baseUri });

      htmlTest = this.xhtmlStart.toString() + this.xhtmlMiddle;
      htmlTest += "\n<script type=\"text/java\">" + xhtmlEmbeddedJs
            + "</script>\n";
      htmlTest += this.htmlEnd;
      testData.add(new Object[] { Handling.IGNORE, xhtmlEmbeddedJs, htmlTest,
            baseUri });

      // HTML 4
      htmlTest = this.html4Start.toString() + this.html4Middle;
      htmlTest += "\n<ScRiPt TyPe=\"TeXt/JaVaScRiPt\">" + embeddedJs
            + "</ScRiPt>\n";
      htmlTest += this.htmlEnd;
      testData.add(new Object[] { Handling.PRESERVE, embeddedJs, htmlTest,
            baseUri });
      testData.add(new Object[] { Handling.REPLACE, embeddedJs, htmlTest,
            baseUri });

      htmlTest = this.html4Start.toString() + this.html4Middle;
      htmlTest += "\n<ScRiPt TyPe=\"text/javascripts\">" + embeddedJs
            + "</ScRiPt>\n";
      htmlTest += this.htmlEnd;
      testData.add(new Object[] { Handling.IGNORE, embeddedJs, htmlTest,
            baseUri });

      // HTML 5
      htmlTest = this.html5Start.toString() + this.html5Middle;
      htmlTest += "\n<ScRiPt TyPe=\"TeXt/JaVaScRiPt\">" + embeddedJs
            + "</ScRiPt>\n";
      htmlTest += this.htmlEnd;
      testData.add(new Object[] { Handling.PRESERVE, embeddedJs, htmlTest,
            baseUri });
      testData.add(new Object[] { Handling.REPLACE, embeddedJs, htmlTest,
            baseUri });

      htmlTest = this.html5Start.toString() + this.html5Middle;
      htmlTest += "\n<script>" + embeddedJs + "</script>\n";
      htmlTest += this.htmlEnd;
      testData.add(new Object[] { Handling.PRESERVE, embeddedJs, htmlTest,
            baseUri });
      testData.add(new Object[] { Handling.REPLACE, embeddedJs, htmlTest,
            baseUri });

      htmlTest = this.html5Start.toString() + this.html5Middle;
      htmlTest += "\n<script type=\"text/php\">" + embeddedJs + "</script>\n";
      htmlTest += this.htmlEnd;
      testData.add(new Object[] { Handling.IGNORE, embeddedJs, htmlTest,
            baseUri });

      htmlTest = this.html5Start.toString() + this.html5Middle;
      htmlTest += "\n<!--script>" + embeddedJs + "</script-->\n";
      htmlTest += this.htmlEnd;
      testData.add(new Object[] { Handling.IGNORE, embeddedJs, htmlTest,
            baseUri });

      return testData.toArray(new Object[testData.size()][]);
   }

   /**
    * Tests
    * {@link JsoupTagReplacer#process(InputStream, NodeHandler, String, OutputStream)}
    * 
    * @param input
    *           the test input.
    * @param baseUri
    *           the base URI string.
    * @throws IOException
    *            if an unexpected I/O exception occurs.
    */
   @Test
   @Parameters(method = "processTestData")
   public void testProcess(final String input, final String baseUri)
         throws IOException {
      final InputStream inputStream = new ByteArrayInputStream(
            input.getBytes(this.charsetName));
      final NodeHandler handler = mock(NodeHandler.class);
      final Answer<String> embeddedAnswer = new Answer<String>() {
         @Override
         public String answer(final InvocationOnMock invocation)
               throws Throwable {
            final Object[] args = invocation.getArguments();
            final String text = (String) args[0];
            if (text.contains("3.14")) {
               return text;
            }
            return null;
         }

      };
      final Answer<String> externalAnswer = new Answer<String>() {
         @Override
         public String answer(final InvocationOnMock invocation)
               throws Throwable {
            final Object[] args = invocation.getArguments();
            final String text = (String) args[0];
            if (text.contains("preserve")) {
               return text;
            } else if (text.contains("minify")) {
               return text + "-minified";
            }
            return null;
         }

      };
      when(handler.handleEmbeddedCss(anyString(), anyBoolean())).thenAnswer(
            embeddedAnswer);
      when(handler.handleEmbeddedJs(anyString())).thenAnswer(embeddedAnswer);
      when(handler.handleExternalCss(anyString())).thenAnswer(externalAnswer);
      when(handler.handleExternalJs(anyString())).thenAnswer(externalAnswer);
      final ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
      this.tagReplacer.process(inputStream, handler, baseUri, baoStream);
      final String output = baoStream.toString(this.charsetName);

      assertTrue(input.contains(this.replacedCss));
      assertFalse(output.contains(REPLACED_CSS_DECLARATION));
      assertTrue(input.contains(this.preservedCss));
      assertTrue(output.contains(PRESERVED_CSS_DECLARATION));

      assertTrue(input.contains(this.replacedJavaScript));
      assertFalse(output.contains(REPLACED_JS_DECLARATION));
      assertTrue(input.contains(this.preservedJavaScript));
      assertTrue(output.contains(PRESERVED_JS_DECLARATION));

      assertTrue(input.contains("preserve.css"));
      assertTrue(output.contains("preserve.css"));
      assertTrue(input.contains("minify.css"));
      assertTrue(output.contains("minify.css-minified"));
      assertTrue(input.contains("remove.css"));
      assertFalse(output.contains("remove.css"));

      assertTrue(input.contains("preserve.js"));
      assertTrue(output.contains("preserve.js"));
      assertTrue(input.contains("minify.js"));
      assertTrue(output.contains("minify.js-minified"));
      assertTrue(input.contains("remove.js"));
      assertFalse(output.contains("remove.js"));
   }

/**
     * The data provider for {@link #testProcess(String, NodeHandler, String).
     * @return the test data.
     */
   public Object processTestData() {
      // XHTML
      String xhtmlTest = this.xhtmlStart.toString();
      xhtmlTest += "<LiNk ReL=\"StylesheeT\" TyPe=\"Text/Css\" HrEf=\"preserve.css\"/>";
      xhtmlTest += "<link rel=\"stylesheet\" type=\"text/css\" href=\"minify.css\"/>";
      xhtmlTest += "<link rel=\"stylesheet\" type=\"text/css\" href=\"remove.css\"/>";
      xhtmlTest += "<style type=\"text/css\">\n" + this.replacedCss
            + "\n</style>\n";
      xhtmlTest += "<style type=\"text/css\">\n" + this.preservedCss
            + "\n</style>\n";
      xhtmlTest += this.xhtmlMiddle.toString();
      xhtmlTest += "<ScRiPt TyPe=\"Text/JavaScript\" SrC=\"preserve.js\"></script>\n";
      xhtmlTest += "<ScRiPt TyPe=\"Text/JavaScript\" SrC=\"minify.js\"></script>\n";
      xhtmlTest += "<ScRiPt TyPe=\"Text/JavaScript\" SrC=\"remove.js\"></script>\n";
      xhtmlTest += "<ScRiPt TyPe=\"Text/JavaScript\">\n" + "//<![CDATA[\n"
            + this.replacedJavaScript + "//]]>\n" + "</script>\n";
      xhtmlTest += "<script type=\"text/javascript\">\n" + "//<![CDATA[\n"
            + this.preservedJavaScript + "//]]>\n" + "</script>\n";
      xhtmlTest += this.htmlEnd;

      // HTML 4
      String html4Test = this.html4Start.toString();
      html4Test += "<LiNk ReL=\"StylesheeT\" TyPe=\"Text/Css\" HrEf=\"preserve.css\"/>";
      html4Test += "<link rel=\"stylesheet\" type=\"text/css\" href=\"minify.css\"/>";
      html4Test += "<link rel=\"stylesheet\" type=\"text/css\" href=\"remove.css\"/>";
      html4Test += "<style type=\"text/css\">\n" + this.replacedCss
            + "\n</style>";
      html4Test += "<style type=\"text/css\">\n" + this.preservedCss
            + "\n</style>";
      html4Test += this.html4Middle.toString();
      html4Test += "<ScRiPt TyPe=\"Text/JavaScript\" SrC=\"preserve.js\"></script>\n";
      html4Test += "<ScRiPt TyPe=\"Text/JavaScript\" SrC=\"minify.js\"></script>\n";
      html4Test += "<ScRiPt TyPe=\"Text/JavaScript\" SrC=\"remove.js\"></script>\n";
      html4Test += "<ScRiPt TyPe=\"Text/JavaScript\">\n"
            + this.replacedJavaScript + "</script>\n";
      html4Test += "<script type=\"text/javascript\">\n"
            + this.preservedJavaScript + "</script>\n";
      html4Test += this.htmlEnd;

      // HTML 5
      String html5Test = this.html5Start.toString();
      html5Test += "<LiNk ReL=\"StylesheeT\" TyPe=\"Text/Css\" HrEf=\"preserve.css\"/>";
      html5Test += "<link rel=\"stylesheet\" type=\"text/css\" href=\"minify.css\"/>";
      html5Test += "<link rel=\"stylesheet\" type=\"text/css\" href=\"remove.css\"/>";
      html5Test += "<style TYPE=\"text/cSs\">\n" + this.replacedCss
            + "\n</style>\n";
      html5Test += "<style type=\"text/css\">\n" + this.preservedCss
            + "\n</style>\n";
      html5Test += this.html5Middle.toString();
      html5Test += "<ScRiPt TyPe=\"Text/JavaScript\" SrC=\"preserve.js\"></script>\n";
      html5Test += "<ScRiPt TyPe=\"Text/JavaScript\" SrC=\"minify.js\"></script>\n";
      html5Test += "<ScRiPt TyPe=\"Text/JavaScript\" SrC=\"remove.js\"></script>\n";
      html5Test += "<ScRiPt TyPe=\"Text/JavaScript\">\n"
            + this.replacedJavaScript + "</script>\n";
      html5Test += "<script type=\"text/javascript\">\n"
            + this.preservedJavaScript + "</script>\n";
      html5Test += this.htmlEnd;

      return new Object[][] { new Object[] { xhtmlTest, "" },
            new Object[] { html4Test, "" }, new Object[] { html5Test, "" }, };
   }
}
