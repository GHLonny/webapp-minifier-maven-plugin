package com.lonnyjacobson.webapp_minifier;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

/**
 * This class tests {@link WebappMinifierReportMojo}.
 * 
 * @author Lonny
 */
public class WebappMinifierReportMojoITCase extends AbstractMojoTestCase {
   /**
    * @throws Exception
    *            if any
    */
   public void testClosureReport() throws Exception {
      final File pom = getTestFile("src/test/resources/report-tests/test-closure.xml");
      assertNotNull(pom);
      assertTrue(pom + " does not exist", pom.exists());

      final WebappMinifierReportMojo myMojo = (WebappMinifierReportMojo) lookupMojo(
            "webapp-minifier-report", pom);
      assertNotNull(myMojo);
      myMojo.execute();
   }

   /**
    * @throws Exception
    *            if any
    */
   public void testYuiReport() throws Exception {
      final File pom = getTestFile("src/test/resources/report-tests/test-yui.xml");
      assertNotNull(pom);
      assertTrue(pom + " does not exist", pom.exists());

      final WebappMinifierReportMojo myMojo = (WebappMinifierReportMojo) lookupMojo(
            "webapp-minifier-report", pom);
      assertNotNull(myMojo);
      myMojo.execute();
   }
}
