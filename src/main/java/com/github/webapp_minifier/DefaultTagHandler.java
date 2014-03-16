package com.github.webapp_minifier;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.IOUtil;

import com.github.webapp_minifier.options.DefaultInlineConfigurationHandler;
import com.github.webapp_minifier.options.DefaultOverridablePluginOptions;
import com.github.webapp_minifier.options.DirectiveHandler;
import com.github.webapp_minifier.options.InlineConfigurationHandler;
import com.github.webapp_minifier.options.OptionsParser;
import com.github.webapp_minifier.options.OverridablePluginOptions;
import com.github.webapp_minifier.options.ParseOptionException;
import com.github.webapp_minifier.options.PluginOptions;
import com.github.webapp_minifier.replacer.NodeHandler;
import com.github.webapp_minifier.summary.HtmlFileSummary;
import com.github.webapp_minifier.summary.MinificationSummary;
import com.github.webapp_minifier.summary.MinifiedFileMetrics;
import com.googlecode.htmlcompressor.compressor.ClosureJavaScriptCompressor;
import com.googlecode.htmlcompressor.compressor.Compressor;
import com.googlecode.htmlcompressor.compressor.YuiCssCompressor;
import com.googlecode.htmlcompressor.compressor.YuiJavaScriptCompressor;

/**
 * This class provides the default {@link NodeHandler} implementation. It directs the minification
 * of code into the appropriate files.
 * 
 * @author Lonny
 */
public class DefaultTagHandler implements NodeHandler, DirectiveHandler {

   /** The log instance. */
   private final Log log;

   /** The plugin options. */
   private final PluginOptions pluginOptions;

   /** The plugin options. */
   private OverridablePluginOptions options;

   /** The CSS minification context. */
   private final MinificationContext cssContext;

   /** The JavaScript minification context. */
   private final MinificationContext jsContext;

   /** The minification report. */
   private final MinificationSummary minificationSummary = new MinificationSummary();

   /** Contains the mapping of original file to compressed file information. */
   private final Map<File, Collection<MinifiedFileInfo>> minifiedFiles = new HashMap<File, Collection<MinifiedFileInfo>>();

   /** The file currently being parsed. */
   private HtmlFileSummary currentFile;

   private final OptionsParser optionsParser;

   /**
    * Constructs a new instance using the given log and options.
    * 
    * @param log
    *           the log instance.
    * @param options
    *           the options.
    */
   public DefaultTagHandler(final Log log, final PluginOptions options) {
      this.log = log;
      this.pluginOptions = options;
      final MinifiedFileBuilder cssFileBuilder = new MinifiedFileBuilder(
            options.getTargetDirectory(), options.getCssPrefix(), "css");
      this.cssContext = new MinificationContext("YUI", createCssCompressor(options), cssFileBuilder);
      final MinifiedFileBuilder jsFileBuilder = new MinifiedFileBuilder(
            options.getTargetDirectory(), options.getJsPrefix(), "js");
      this.jsContext = new MinificationContext(options.getJsCompressorEngine().toString(),
            createJavaScriptCompressor(options), jsFileBuilder);
      this.optionsParser = new OptionsParser(log);
   }

   /**
    * Constructs a new YUI Compressor using the requested configuration.
    * 
    * @param options
    *           the plugin options.
    * @return the new YUI Compressor.
    */
   protected YuiCssCompressor createYuiCssCompressor(final OverridablePluginOptions options) {
      final YuiCssCompressor compressor = new YuiCssCompressor();
      compressor.setLineBreak(options.getYuiCssLineBreak());
      return compressor;
   }

   /**
    * Constructs a new Google Closure Compressor using the requested configuration.
    * 
    * @param options
    *           the plugin options.
    * @return the new Google Closure Compressor.
    */
   protected ClosureJavaScriptCompressor createClosureJsCompressor(
         final OverridablePluginOptions options) {
      final ClosureJavaScriptCompressor closureJavaScriptCompressor = new ClosureJavaScriptCompressor(
            options.getClosureCompilationLevel());
      // TODO: Allow overriding of Closure compiler options.
      return closureJavaScriptCompressor;
   }

   /**
    * Constructs a new YUI JavaScript Compressor using the requested configuration.
    * 
    * @param options
    *           the plugin options.
    * @return the new YUI Compressor.
    */
   protected YuiJavaScriptCompressor createYuiJsCompressor(final OverridablePluginOptions options) {
      final YuiJavaScriptCompressor compressor = new YuiJavaScriptCompressor();
      compressor.setDisableOptimizations(options.isYuiJsDisableOptimizations());
      compressor.setLineBreak(options.getYuiJsLineBreak());
      compressor.setNoMunge(options.isYuiJsNoMunge());
      compressor.setPreserveAllSemiColons(options.isYuiJsPreserveAllSemiColons());
      return compressor;
   }

   /**
    * Constructs a new CSS compressor.
    * 
    * @param options
    *           the plugin options.
    * @return the new CSS compressor or <code>null</code> if CSS minification is disabled.
    */
   protected Compressor createCssCompressor(final OverridablePluginOptions options) {
      final Compressor compressor;
      if (options.isSkipJsMinify()) {
         compressor = null;
      } else {
         compressor = createYuiCssCompressor(options);
      }
      return compressor;
   }

   /**
    * Constructs a new JavaScript compressor.
    * 
    * @param options
    *           the plugin options.
    * @return the new JavaScript compressor or <code>null</code> if JavaScript minification is
    *         disabled.
    */
   protected Compressor createJavaScriptCompressor(final OverridablePluginOptions options) {
      final Compressor compressor;
      if (options.isSkipJsMinify()) {
         compressor = null;
      } else {
         switch (options.getJsCompressorEngine()) {
         case CLOSURE:
            compressor = createClosureJsCompressor(options);
            break;
         case YUI:
         default:
            compressor = createYuiJsCompressor(options);
         }
      }
      return compressor;
   }

   @Override
   public void start(final File file) {
      this.options = new DefaultOverridablePluginOptions(this.pluginOptions);
      this.currentFile = new HtmlFileSummary();
      this.currentFile.setFile(file);
      this.minificationSummary.getHtmlFiles().add(this.currentFile);
   }

   @Override
   public boolean handleComment(final String text) throws ParseOptionException {
      this.log.debug("Handling comment '" + text + "'");

      if (this.optionsParser.containsOptionsHeader(text)) {
         final InlineConfigurationHandler defaultInlineConfigurationHandler = new DefaultInlineConfigurationHandler(
               this.options);
         this.optionsParser.parse(text, defaultInlineConfigurationHandler, this);
         this.cssContext.setCompressor(createCssCompressor(this.options));
         this.jsContext.setCompressor(createJavaScriptCompressor(this.options));
         this.jsContext.setMinifier(this.options.getJsCompressorEngine().toString());
         return true;
      }
      return false;
   }

   @Override
   public void splitJavaScript() {
      this.log.debug("Splitting the minified JavaScript file.");
      this.jsContext.getFileBuilder().finishFile();
   }

   @Override
   public void splitCss() {
      this.log.debug("Splitting the minified CSS file.");
      this.cssContext.getFileBuilder().finishFile();
   }

   @Override
   public String handleExternalCss(final String url) throws IOException {
      this.log.debug("Handling external CSS '" + url + "'");
      final String result = minifyExternalCode(url, this.cssContext);
      return result;
   }

   @Override
   public String handleEmbeddedCss(final String text, final boolean scoped) throws IOException {
      if (this.log.isDebugEnabled()) {
         this.log.debug("Handling embedded CSS '" + text + "'");
      }
      final boolean skipEmbedded = this.options.isSkipEmbeddedCssMinify();
      final boolean mergeEmbedded = this.options.isMergeEmbeddedCss();
      final MinifiedFileMetrics metrics = new MinifiedFileMetrics();
      metrics.setSource(MinifiedFileMetrics.EMBEDDED_CSS);

      final String result = minifyEmbedded(text, skipEmbedded, mergeEmbedded, this.cssContext,
            metrics);

      if (metrics.getDestination() == null) {
         metrics.setDestination(MinifiedFileMetrics.EMBEDDED_CSS);
      }
      this.currentFile.getMinifiedFiles().add(metrics);
      return result;
   }

   @Override
   public String handleExternalJs(final String url) throws IOException {
      this.log.debug("Handling external JavaScript '" + url + "'");
      final String result = minifyExternalCode(url, this.jsContext);
      return result;
   }

   @Override
   public String handleEmbeddedJs(final String text) throws IOException {
      if (this.log.isDebugEnabled()) {
         this.log.debug("Handling embedded JavaScript '" + text + "'");
      }
      final boolean skipEmbedded = this.options.isSkipEmbeddedJsMinify();
      final boolean mergeEmbedded = this.options.isMergeEmbeddedJs();
      final MinifiedFileMetrics metrics = new MinifiedFileMetrics();
      metrics.setSource(MinifiedFileMetrics.EMBEDDED_JS);

      final String result = minifyEmbedded(text, skipEmbedded, mergeEmbedded, this.jsContext,
            metrics);

      if (metrics.getDestination() == null) {
         metrics.setDestination(MinifiedFileMetrics.EMBEDDED_JS);
      }
      this.currentFile.getMinifiedFiles().add(metrics);
      return result;
   }

   @Override
   public void handleText(final String text) {
      // Do nothing.
   }

   @Override
   public void handleOther(final String nodeName) {
      if (this.log.isDebugEnabled()) {
         this.log.debug("Handling another node '" + nodeName + "'");
      }
      this.cssContext.getFileBuilder().finishFile();
      this.jsContext.getFileBuilder().finishFile();
   }

   @Override
   public void complete() {
      this.cssContext.getFileBuilder().finishFile();
      this.jsContext.getFileBuilder().finishFile();
      this.currentFile = null;
   }

   @Override
   public MinificationSummary getReport() {
      return this.minificationSummary;
   }

   /**
    * Minifies the external code.
    * 
    * @param urlString
    *           the URL to the external code.
    * @param compressor
    *           the compressor to use.
    * @param builder
    *           the builder for the compressed files.
    * @return the original URL or the file name containing the minified code.
    * @throws FileNotFoundException
    *            if the minified file could not be created.
    * @throws IOException
    *            if any other error occurred while writing to the minified file.
    */
   protected String minifyExternalCode(final String urlString, final MinificationContext context)
         throws FileNotFoundException, IOException {
      String result = urlString;
      final Compressor compressor = context.getCompressor();
      final MinifiedFileBuilder builder = context.getFileBuilder();
      if (compressor != null) {
         // TODO: Determine if the file has already been minified.
         File sourceFile = new File(this.pluginOptions.getTargetDirectory(), urlString);

         // If the file exists, get it's input stream.
         InputStream inputStream = null;
         if (sourceFile.exists()) {
            inputStream = new FileInputStream(sourceFile);
         } else {
            // Otherwise, search the "other directories" for a matching
            // file.
            for (final Entry<Object, Object> entry : this.pluginOptions.getOtherDirectories()
                  .entrySet()) {
               String key = (String) entry.getKey();
               if (!key.endsWith("/")) {
                  key = key + '/';
               }
               if (urlString.startsWith(key)) {
                  this.log.debug("Searching for '" + urlString + "' using key '" + key
                        + "' which points to '" + entry.getValue() + "'");
                  final File otherTarget = new File((String) entry.getValue());
                  sourceFile = new File(otherTarget, urlString.substring(key.length()));
                  if (sourceFile.exists()) {
                     inputStream = new FileInputStream(sourceFile);
                     break;
                  }
               }
            }
         }

         // If an input stream was not found for the current URL string, skip
         // the tag and create a new minified file next time.
         if (inputStream == null) {
            this.log.debug("Did not find '" + urlString + "'.  Its content will not be minified.");
            builder.finishFile();
         } else {
            // Minify the contents of the input stream.
            final String original = IOUtil.toString(inputStream, this.pluginOptions.getEncoding(),
                  8192);
            final MinifiedFileInfo fileInfo = builder.getCurrentFile();
            final File destinationFile = fileInfo.getFile();
            final MinifiedFileMetrics metrics = new MinifiedFileMetrics();
            metrics.setSource(urlString);
            metrics.setMinifier(context.getMinifier());
            result = minify(compressor, original, destinationFile, metrics);
            this.currentFile.getMinifiedFiles().add(metrics);

            // Keep track of which inputs went into which outputs.
            fileInfo.getFiles().add(sourceFile);
            if (this.minifiedFiles.containsKey(sourceFile)) {
               this.minifiedFiles.get(sourceFile).add(fileInfo);
            } else {
               final Collection<MinifiedFileInfo> c = new ArrayList<MinifiedFileInfo>();
               c.add(fileInfo);
               this.minifiedFiles.put(sourceFile, c);
            }
         }
      }
      return result;
   }

   /**
    * Minifies the embedded CSS or JavaScript.
    * 
    * @param text
    *           the CSS or JavaScript to minify.
    * @param skipEmbedded
    *           indicates if minification should be skipped.
    * @param mergeEmbedded
    *           indicates if the embedded code should be merged with an external file.
    * @param compressor
    *           the compressor to use.
    * @param builder
    *           the builder for the compressed files.
    * @param metrics
    *           the place to store metrics about minification.
    * @return the minified result or <code>null</code> if it was added to another file.
    * @throws FileNotFoundException
    *            if the minified file could not be created.
    * @throws IOException
    *            if any other error occurred while writing to the minified file.
    */
   protected String minifyEmbedded(final String text, final boolean skipEmbedded,
         final boolean mergeEmbedded, final MinificationContext context,
         final MinifiedFileMetrics metrics) throws FileNotFoundException, IOException {
      final String result;
      final Compressor compressor = context.getCompressor();
      final MinifiedFileBuilder builder = context.getFileBuilder();
      if (skipEmbedded || (compressor == null)) {
         result = text;
         builder.finishFile();
      } else if (mergeEmbedded && !builder.isNewFile()) {
         final MinifiedFileInfo fileInfo = builder.getCurrentFile();
         final File file = fileInfo.getFile();
         metrics.setMinifier(context.getMinifier());
         minify(compressor, text, file, metrics);
         fileInfo.setEmbeddedIncluded(true);
         result = null;
      } else {
         final long startTime = System.nanoTime();
         result = compressor.compress(text);
         final long endTime = System.nanoTime();
         metrics.setTime(endTime - startTime);
         metrics.setOriginalLength(text.length());
         metrics.setMinifiedLength(result.length());
         metrics.setMinifier(context.getMinifier());
         builder.finishFile();
      }
      return result;
   }

   /**
    * Minifies the input using the provided compressor.
    * 
    * @param compressor
    *           the compressor
    * @param input
    *           the input to be minified.
    * @param destinationFile
    *           the destination file.
    * @param metrics
    *           the place to store metrics about minification.
    * @return the destination file name if the file was created or <code>null</code> if the minified
    *         output was appended to the file.
    * @throws FileNotFoundException
    *            if the file exists but is a directory rather than a regular file, does not exist
    *            but cannot be created, or cannot be opened for any other reason.
    * @throws IOException
    *            if an error occurs while copying the minified output to the file.
    */
   protected String minify(final Compressor compressor, final String input,
         final File destinationFile, final MinifiedFileMetrics metrics)
         throws FileNotFoundException, IOException {
      String result;
      OutputStream oStream = null;
      try {
         if (destinationFile.exists()) {
            result = null;
         } else {
            result = destinationFile.getName();
         }
         oStream = new BufferedOutputStream(new FileOutputStream(destinationFile, true));
         final long startTime = System.nanoTime();
         final String compressed = compressor.compress(input);
         final long endTime = System.nanoTime();
         metrics.setTime(endTime - startTime);
         metrics.setOriginalLength(input.length());
         metrics.setMinifiedLength(compressed.length());
         metrics.setDestination(destinationFile.getName());
         IOUtil.copy(compressed, oStream);
         this.log.info("Reduced input from " + input.length() + " to " + compressed.length()
               + " characters");
      } finally {
         IOUtil.close(oStream);
      }
      return result;
   }
}
