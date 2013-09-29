package com.github.jacobsonl.webapp_minifier;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.FileUtils;

/**
 * This class tests {@link WebappMinifierMojo}.
 * 
 * @author Lonny
 */
public class WebappMinifierMojoTestCase extends AbstractMojoTestCase {
   /**
    * @throws Exception
    *            if any
    */
   public void testClosure() throws Exception {
      final File pom = getTestFile("src/test/resources/test1/closure-plugin-config.xml");
      assertNotNull(pom);
      assertTrue(pom + " does not exist", pom.exists());

      final WebappMinifierMojo myMojo = (WebappMinifierMojo) lookupMojo(
            "webapp-minifier", pom);
      assertNotNull(myMojo);
      myMojo.execute();
   }

   /**
    * @throws Exception
    *            if any
    */
   public void testYui() throws Exception {
      final File pom = getTestFile("src/test/resources/test1/yui-plugin-config.xml");
      assertNotNull(pom);
      assertTrue(pom + " does not exist", pom.exists());

      final WebappMinifierMojo myMojo = (WebappMinifierMojo) lookupMojo(
            "webapp-minifier", pom);
      assertNotNull(myMojo);
      myMojo.execute();
   }

   /**
    * @throws Exception
    *            if any
    */
   public void testOtherDirectories() throws Exception {
      final File pom = getTestFile("src/test/resources/test-other-directories/plugin-config.xml");
      assertNotNull(pom);
      assertTrue(pom + " does not exist", pom.exists());

      final WebappMinifierMojo myMojo = (WebappMinifierMojo) lookupMojo(
            "webapp-minifier", pom);
      assertNotNull(myMojo);
      myMojo.execute();

      // Ensure that none of the generated files contain "/test1".
      File[] backupFiles = myMojo.getTargetDirectory().listFiles(
            new FilenameFilter() {
               @Override
               public boolean accept(File arg0, String arg1) {
                  return arg1.endsWith(".bak");
               }
            });
      for (File backupFile : backupFiles) {
         final String name = backupFile.getName();
         File file = new File(backupFile.getParentFile(),
               FileUtils.removeExtension(name));
         assertTrue(file + " should exist", file.exists());
         String contents = FileUtils.fileRead(file, myMojo.getEncoding());
         assertFalse(file + " should not contain '/test1'",
               contents.contains("/test1"));
      }
   }
}
