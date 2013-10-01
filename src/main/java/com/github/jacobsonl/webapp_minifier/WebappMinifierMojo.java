package com.github.jacobsonl.webapp_minifier;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.ArrayUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import com.github.jacobsonl.webapp_minifier.options.OverridablePluginOptions;
import com.github.jacobsonl.webapp_minifier.options.PluginOptions;
import com.github.jacobsonl.webapp_minifier.replacer.TagReplacer;
import com.github.jacobsonl.webapp_minifier.replacer.TagReplacerFactory;
import com.github.jacobsonl.webapp_minifier.summary.MinificationSummary;
import com.github.jacobsonl.webapp_minifier.utils.CommonUtils;
import com.google.javascript.jscomp.CompilationLevel;

/**
 * This mojo minifies web applications.
 */
@Mojo(name = "webapp-minifier", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class WebappMinifierMojo extends AbstractMojo implements PluginOptions {

   /**
    * The web application source directory.
    */
   @Parameter(defaultValue = "${basedir}/src/main/webapp", required = true)
   private File sourceDirectory;

   /**
    * The web application target directory.
    */
   @Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}-min", required = true)
   private File targetDirectory;

   /**
    * The HTML document parser.
    * <p>
    * Possible values are:
    * <ul>
    * <li>jsoup
    * </ul>
    */
   @Parameter(defaultValue = "jsoup", required = true, readonly = true)
   private String parser;

   @Requirement(hint = "jsoup")
   private TagReplacer tagReplacer;

   /**
    * The HTML files to include in processing.
    * <p>
    * <b>Default values are:</b>
    * <ul>
    * <li>**&#47;*.html
    * <li>**&#47;*.htm
    * <li>**&#47;*.jsp
    * </ul>
    */
   @Parameter
   private String[] htmlIncludes;

   /**
    * The HTML files to exclude from processing.
    */
   @Parameter
   private String[] htmlExcludes;

   /**
    * Skips any minification processing.
    */
   @Parameter(defaultValue = "false")
   private boolean skipMinify;

   /**
    * Skips CSS minification.
    */
   @Parameter(defaultValue = "false")
   private boolean skipCssMinify;

   /**
    * Skips Embedded CSS minification.
    */
   @Parameter(defaultValue = "false")
   private boolean skipEmbeddedCssMinify;

   /**
    * Skips JavaScript minification.
    */
   @Parameter(defaultValue = "false")
   private boolean skipJsMinify;

   /**
    * Skips Embedded JavaScript minification.
    */
   @Parameter(defaultValue = "false")
   private boolean skipEmbeddedJsMinify;

   /**
    * Merges embedded CSS with externally minified CSS.
    */
   @Parameter(defaultValue = "false")
   private boolean mergeEmbeddedCss;

   /**
    * Merges embedded JavaScript with externally minified JavaScript.
    */
   @Parameter(defaultValue = "false")
   private boolean mergeEmbeddedJs;

   /**
    * The character encoding used for HTML, JavScript and CSS files.
    */
   @Parameter(defaultValue = "${project.build.sourceEncoding}")
   private String encoding;

   /**
    * The prefix for minified CSS files.
    */
   @Parameter(defaultValue = "css")
   private String cssPrefix;

   /**
    * The prefix for minified JavaScript files.
    */
   @Parameter(defaultValue = "js")
   private String jsPrefix;

   /**
    * Defines other directories where CSS and JavaScript files may be found.
    * This is useful when other projects contain common CSS and JavaScript. The
    * name should be a location as defined in the HTML. The value would be the
    * directory that corresponds to the location.
    * <p>
    * For example,
    * 
    * <pre>
    * &lt;otherDirectories>
    *   &lt;property>
    *     &lt;name>/common&lt;/name>
    *     &lt;value>${basedir}/../../common-project/src/main/webapp&lt;/value>
    *   &lt;/property>
    * &lt;/otherDirectories>
    * </pre>
    */
   @Parameter
   private final Properties otherDirectories = new Properties();

   /**
    * The JavaScript Compressor to use:
    * <ul>
    * <li><b>CLOSURE</b> - The <a
    * href="https://developers.google.com/closure/compiler/">Google Closure
    * Compiler</a>.
    * <li><b>YUI</b> - The <a href="http://yui.github.io/yuicompressor/">YUI
    * Compressor</a>.
    * </ul>
    */
   @Parameter(defaultValue = "YUI")
   private OverridablePluginOptions.JavaScriptCompressor jsCompressorEngine;

   /**
    * The Google Closure compiler level.
    */
   @Parameter(defaultValue = "SIMPLE_OPTIMIZATIONS")
   private CompilationLevel closureCompilationLevel;

   /**
    * Instructs the YUI Compressor to break lines after the specified number of
    * characters.
    * <ul>
    * <li><b>-1</b> - Disables line breaking.
    * <li><b>0</b> - Causes a line break after each rule in CSS.
    * </ul>
    */
   @Parameter(defaultValue = "-1")
   private int yuiCssLineBreak;

   /**
    * Instructs the YUI Compressor to disable all JavaScript
    * micro-optimizations.
    */
   @Parameter(defaultValue = "false")
   private boolean yuiJsDisableOptimizations;

   /**
    * Instructs the YUI Compressor to break lines after the specified number of
    * characters.
    * <ul>
    * <li><b>-1</b> - Disables line breaking.
    * <li><b>0</b> - Causes a line break after each semi-colon in JavaScript.
    * </ul>
    */
   @Parameter(defaultValue = "-1")
   private int yuiJsLineBreak;

   /**
    * Instructs the YUI Compressor to only minify JavaScript without obfuscating
    * local symbols.
    */
   @Parameter(defaultValue = "false")
   private boolean yuiJsNoMunge;

   /**
    * Instructs the YUI Compressor to preserve unnecessary semicolons in
    * JavaScript. This option is useful when compressed code has to be run
    * through JSLint.
    */
   @Parameter(defaultValue = "false")
   private boolean yuiJsPreserveAllSemiColons;

   /**
    * 
    * @see org.apache.maven.plugin.AbstractMojo#execute()
    */
   @Override
   public void execute() throws MojoExecutionException {
      // Copy the source directory to the target directory.
      try {
         getLog().debug(
               "Copying " + this.sourceDirectory + " to "
                     + this.targetDirectory);
         if (this.targetDirectory.exists()) {
            FileUtils.deleteDirectory(this.targetDirectory);
         }
         FileUtils.copyDirectoryStructure(this.sourceDirectory,
               this.targetDirectory);
      } catch (final IOException e) {
         throw new MojoExecutionException(
               "Failed to copy the source directory", e);
      }

      if (!this.skipMinify) {
         // Process each of the requested files.
         final DefaultTagHandler tagHandler = new DefaultTagHandler(getLog(),
               this);
         final TagReplacer tagReplacer = TagReplacerFactory.getReplacer(
               this.parser, getLog(), this.encoding);
         for (final String fileName : getFilesToProcess()) {
            final File htmlFile = new File(this.targetDirectory, fileName);
            final File minifiedHtmlFile = new File(this.targetDirectory,
                  fileName + ".min");
            final File htmlFileBackup = new File(this.targetDirectory, fileName
                  + ".bak");

            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
               getLog().info("Processing " + htmlFile.getCanonicalFile());
               final String baseUri = CommonUtils.getBaseUri(htmlFile,
                     this.targetDirectory);
               inputStream = new BufferedInputStream(new FileInputStream(
                     htmlFile));
               outputStream = new BufferedOutputStream(new FileOutputStream(
                     minifiedHtmlFile));
               tagHandler.start(htmlFile);
               tagReplacer.process(inputStream, tagHandler, baseUri,
                     outputStream);
            } catch (final IOException e) {
               throw new MojoExecutionException(
                     "Failed to process " + htmlFile, e);
            } finally {
               IOUtil.close(inputStream);
               IOUtil.close(outputStream);
            }
            if (!htmlFile.renameTo(htmlFileBackup)) {
               throw new MojoExecutionException("Failed to rename "
                     + htmlFile.getName() + " to " + htmlFileBackup.getName());
            }
            if (!minifiedHtmlFile.renameTo(htmlFile)) {
               throw new MojoExecutionException("Failed to rename "
                     + minifiedHtmlFile.getName() + " to " + htmlFile.getName());
            }
         }

         // Write out the summary file.
         final File summaryFile = new File(this.targetDirectory,
               "webapp-minifier-summary.xml");
         try {
            final JAXBContext context = JAXBContext
                  .newInstance(MinificationSummary.class);
            final Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, getEncoding());
            marshaller.marshal(tagHandler.getReport(), summaryFile);
            tagHandler.getReport();
         } catch (final JAXBException e) {
            throw new MojoExecutionException(
                  "Failed to marshal the plugin's summary to XML", e);
         }
      }
   }

   /**
    * Returns the array of files to process.
    * 
    * @return the array of files to process.
    */
   protected String[] getFilesToProcess() {
      final DirectoryScanner directoryScanner = new DirectoryScanner();
      directoryScanner.setBasedir(this.targetDirectory);

      final String[] includes = CommonUtils.isEmpty(this.htmlIncludes) ? getDefaultIncludes()
            : this.htmlIncludes;
      getLog().debug("HTML Includes: " + ArrayUtils.toString(includes));
      directoryScanner.setIncludes(includes);

      final String[] excludes = CommonUtils.isEmpty(this.htmlExcludes) ? getDefaultExcludes()
            : this.htmlExcludes;
      getLog().debug("HTML Excludes: " + ArrayUtils.toString(excludes));
      directoryScanner.setExcludes(excludes);

      directoryScanner.scan();
      return directoryScanner.getIncludedFiles();
   }

   /**
    * @return
    */
   protected String[] getDefaultIncludes() {
      return new String[] { "**/*.html", "**/*.htm", "**/*.jsp" };
   }

   /**
    * @return
    */
   protected String[] getDefaultExcludes() {
      return new String[0];
   }

   @Override
   public File getSourceDirectory() {
      return this.sourceDirectory;
   }

   @Override
   public void setSourceDirectory(final File sourceDirectory) {
      this.sourceDirectory = sourceDirectory;
   }

   @Override
   public File getTargetDirectory() {
      return this.targetDirectory;
   }

   @Override
   public void setTargetDirectory(final File targetDirectory) {
      this.targetDirectory = targetDirectory;
   }

   @Override
   public boolean isSkipCssMinify() {
      return this.skipCssMinify;
   }

   @Override
   public void setSkipCssMinify(final boolean flag) {
      this.skipCssMinify = flag;
   }

   @Override
   public boolean isSkipEmbeddedCssMinify() {
      return this.skipEmbeddedCssMinify;
   }

   @Override
   public void setSkipEmbeddedCssMinify(final boolean flag) {
      this.skipEmbeddedCssMinify = flag;
   }

   @Override
   public boolean isSkipJsMinify() {
      return this.skipJsMinify;
   }

   @Override
   public void setSkipJsMinify(final boolean flag) {
      this.skipJsMinify = flag;
   }

   @Override
   public boolean isSkipEmbeddedJsMinify() {
      return this.skipEmbeddedJsMinify;
   }

   @Override
   public void setSkipEmbeddedJsMinify(final boolean flag) {
      this.skipEmbeddedJsMinify = flag;
   }

   @Override
   public String getEncoding() {
      return this.encoding;
   }

   @Override
   public void setEncoding(final String encoding) {
      this.encoding = encoding;
   }

   @Override
   public OverridablePluginOptions.JavaScriptCompressor getJsCompressorEngine() {
      return this.jsCompressorEngine;
   }

   @Override
   public void setJsCompressorEngine(
         final OverridablePluginOptions.JavaScriptCompressor jsCompressorEngine) {
      this.jsCompressorEngine = jsCompressorEngine;

   }

   @Override
   public CompilationLevel getClosureCompilationLevel() {
      return this.closureCompilationLevel;
   }

   @Override
   public void setClosureCompilationLevel(
         final CompilationLevel compilationLevel) {
      this.closureCompilationLevel = compilationLevel;
   }

   @Override
   public int getYuiCssLineBreak() {
      return this.yuiCssLineBreak;
   }

   @Override
   public void setYuiCssLineBreak(final int lineBreak) {
      this.yuiCssLineBreak = lineBreak;
   }

   @Override
   public boolean isYuiJsDisableOptimizations() {
      return this.yuiJsDisableOptimizations;
   }

   @Override
   public void setYuiJsDisableOptimizations(final boolean flag) {
      this.yuiJsDisableOptimizations = flag;
   }

   @Override
   public int getYuiJsLineBreak() {
      return this.yuiJsLineBreak;
   }

   @Override
   public void setYuiJsLineBreak(final int lineBreak) {
      this.yuiJsLineBreak = lineBreak;
   }

   @Override
   public boolean isYuiJsNoMunge() {
      return this.yuiJsNoMunge;
   }

   @Override
   public void setYuiJsNoMunge(final boolean flag) {
      this.yuiJsNoMunge = flag;
   }

   @Override
   public boolean isYuiJsPreserveAllSemiColons() {
      return this.yuiJsPreserveAllSemiColons;
   }

   @Override
   public void setYuiJsPreserveAllSemiColons(final boolean flag) {
      this.yuiJsPreserveAllSemiColons = flag;
   }

   @Override
   public String getCssPrefix() {
      return this.cssPrefix;
   }

   @Override
   public void setCssPrefix(final String cssPrefix) {
      this.cssPrefix = cssPrefix;
   }

   @Override
   public String getJsPrefix() {
      return this.jsPrefix;
   }

   @Override
   public void setJsPrefix(final String jsPrefix) {
      this.jsPrefix = jsPrefix;
   }

   @Override
   public boolean isMergeEmbeddedCss() {
      return this.mergeEmbeddedCss;
   }

   @Override
   public void setMergeEmbeddedCss(final boolean flag) {
      this.mergeEmbeddedCss = flag;
   }

   @Override
   public boolean isMergeEmbeddedJs() {
      return this.mergeEmbeddedJs;
   }

   @Override
   public void setMergeEmbeddedJs(final boolean flag) {
      this.mergeEmbeddedJs = flag;
   }

   @Override
   public Properties getOtherDirectories() {
      return this.otherDirectories;
   }
}
