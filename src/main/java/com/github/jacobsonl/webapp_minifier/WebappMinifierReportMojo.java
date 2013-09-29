package com.github.jacobsonl.webapp_minifier;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import com.github.jacobsonl.webapp_minifier.summary.HtmlFileSummary;
import com.github.jacobsonl.webapp_minifier.summary.MinificationSummary;
import com.github.jacobsonl.webapp_minifier.summary.MinifiedFileMetrics;

/**
 * @author Lonny
 * 
 */
@Mojo(name = "webapp-minifier-report", threadSafe = true, defaultPhase = LifecyclePhase.SITE)
public class WebappMinifierReportMojo extends AbstractMavenReport {

   /**
    * The report output directory. Note that this parameter is only evaluated if
    * the goal is run directly from the command line or during the default
    * lifecycle. If the goal is run indirectly as part of a site generation, the
    * output directory configured in the Maven Site Plugin is used instead.
    */
   @Parameter(defaultValue = "${project.reporting.outputDirectory")
   private File outputDirectory;

   /**
    * Report output encoding. Note that this parameter is only relevant if the
    * goal is run from the command line or from the default build lifecycle. If
    * the goal is run indirectly as part of a site generation, the output
    * encoding configured in the Maven Site Plugin is used instead.
    * 
    * @since 2.4
    */
   @Parameter(defaultValue = "${project.reporting.outputEncoding}")
   private String outputEncoding;

   /**
    * Doxia Site Renderer.
    */
   @Component
   protected Renderer siteRenderer;

   /**
    * The Maven Project.
    */
   @Component
   protected MavenProject project;

   private DecimalFormat lengthFormatter;

   private NumberFormat percentFormatter;

   private DecimalFormat timeFormatter;

   @Override
   public String getOutputName() {
      return "WebappMinifier";
   }

   @Override
   public String getName(Locale locale) {
      return "Minification Report";
   }

   @Override
   public String getDescription(Locale locale) {
      return "This report summarizes the minification results for this project.";
   }

   @Override
   protected Renderer getSiteRenderer() {
      return siteRenderer;
   }

   @Override
   protected String getOutputDirectory() {
      return outputDirectory.getAbsolutePath();
   }

   @Override
   protected MavenProject getProject() {
      return project;
   }

   @Override
   protected void executeReport(Locale locale) throws MavenReportException {
      getLog().info("executeReport(" + locale + ")");
      initializeFormatters(locale);

      final MinificationSummary summary = loadSummary();

      Sink sink = getSink();
      sink.head();
      sink.title();
      sink.text(getName(locale));
      sink.title_();
      sink.head_();

      sink.body();
      sink.section1();
      sink.sectionTitle1();
      sink.text(getName(locale));
      sink.sectionTitle1_();
      sink.text(getDescription(locale));

      for (HtmlFileSummary htmlFile : summary.getHtmlFiles()) {

         Map<String, MutableInt> destCounts = getDestinationCounts(htmlFile
               .getMinifiedFiles());
         sink.section2();
         sink.sectionTitle2();
         sink.text(htmlFile.getFile().getName());
         sink.sectionTitle2_();

         // The results table.
         sink.table();
         sink.tableRows(null, true);

         // The header row.
         sink.tableRow();
         sink.tableHeaderCell();
         sink.text("CSS/JS File");
         sink.tableHeaderCell_();
         sink.tableHeaderCell();
         sink.text("Destination");
         sink.tableHeaderCell_();
         sink.tableHeaderCell();
         sink.text("Minifier");
         sink.tableHeaderCell_();
         sink.tableHeaderCell();
         sink.text("Original Size");
         sink.tableHeaderCell_();
         sink.tableHeaderCell();
         sink.text("Minified Size");
         sink.tableHeaderCell_();
         sink.tableHeaderCell();
         sink.text("% Minified");
         sink.tableHeaderCell_();
         sink.tableHeaderCell();
         sink.text("Time");
         sink.tableHeaderCell_();
         sink.tableRow_();

         Set<String> minifiers = new TreeSet<String>();
         int totalOriginalLength = 0;
         int totalMinifiedLength = 0;
         double totalTime = 0;
         SinkEventAttributes centeredAttributes = new SinkEventAttributeSet();
         centeredAttributes.addAttribute(SinkEventAttributes.ALIGN, "center");
         centeredAttributes.addAttribute(SinkEventAttributes.ROWSPAN, 1);
         SinkEventAttributes rightAttributes = new SinkEventAttributeSet();
         rightAttributes.addAttribute(SinkEventAttributes.ALIGN, "right");
         for (MinifiedFileMetrics metrics : htmlFile.getMinifiedFiles()) {
            // A sample table row.
            sink.tableRow();

            sink.tableCell();
            sink.text(metrics.getSource());
            sink.tableCell_();

            final String destination = metrics.getDestination();
            if (destCounts.containsKey(destination)) {
               SinkEventAttributes destinationAttributes = new SinkEventAttributeSet(
                     centeredAttributes);

               if (!destination.equals(MinifiedFileMetrics.EMBEDDED_CSS)
                     && !destination.equals(MinifiedFileMetrics.EMBEDDED_JS)) {
                  destinationAttributes.addAttribute(
                        SinkEventAttributes.ROWSPAN, destCounts
                              .get(destination).intValue());
                  destCounts.remove(destination);
               }
               sink.tableCell(destinationAttributes);
               sink.text(destination);
               sink.tableCell_();
            }

            minifiers.add(metrics.getMinifier());
            sink.tableCell(centeredAttributes);
            sink.text(metrics.getMinifier());
            sink.tableCell_();

            final int originalLength = metrics.getOriginalLength();
            totalOriginalLength += originalLength;
            sink.tableCell(rightAttributes);
            sink.text(lengthFormatter.format(originalLength));
            sink.tableCell_();

            final int minifiedLength = metrics.getMinifiedLength();
            totalMinifiedLength += minifiedLength;
            sink.tableCell(rightAttributes);
            sink.text(lengthFormatter.format(minifiedLength));
            sink.tableCell_();

            sink.tableCell(rightAttributes);
            sink.text(percentFormatter.format((originalLength - minifiedLength)
                  / (float) originalLength));
            sink.tableCell_();

            final double time = metrics.getTime() / 1000000.0;
            totalTime += time;
            sink.tableCell(rightAttributes);
            sink.text(timeFormatter.format(time));
            sink.tableCell_();

            sink.tableRow_();
         }

         // If there are minification metrics, generate a total.
         if (!htmlFile.getMinifiedFiles().isEmpty()) {
            // Total the metrics.
            sink.tableRow();

            SinkEventAttributes attributes = new SinkEventAttributeSet();
            attributes.addAttribute(SinkEventAttributes.COLSPAN, 2);
            sink.tableHeaderCell(attributes);
            sink.text("Total");
            sink.tableHeaderCell_();

            sink.tableCell(centeredAttributes);
            sink.text(minifiers.size() > 1 ? "Multiple" : minifiers.iterator()
                  .next());
            sink.tableCell_();

            sink.tableCell(rightAttributes);
            sink.text(lengthFormatter.format(totalOriginalLength));
            sink.tableCell_();

            sink.tableCell(rightAttributes);
            sink.text(lengthFormatter.format(totalMinifiedLength));
            sink.tableCell_();

            sink.tableCell(rightAttributes);
            sink.text(percentFormatter
                  .format((totalOriginalLength - totalMinifiedLength)
                        / (float) totalOriginalLength));
            sink.tableCell_();

            sink.tableCell(rightAttributes);
            sink.text(timeFormatter.format(totalTime));
            sink.tableCell_();

            sink.tableRow_();
         }

         sink.tableRows_();
         sink.table_();
         sink.section2_();
      }
      sink.section1_();
      sink.body_();
      sink.flush();
      sink.close();
   }

   protected void initializeFormatters(Locale locale) {
      lengthFormatter = (DecimalFormat) NumberFormat.getIntegerInstance(locale);
      lengthFormatter.setPositiveSuffix(" B");
      percentFormatter = NumberFormat.getPercentInstance(locale);
      percentFormatter.setMinimumFractionDigits(1);
      percentFormatter.setMaximumFractionDigits(1);
      timeFormatter = (DecimalFormat) NumberFormat.getNumberInstance(locale);
      timeFormatter.setPositiveSuffix(" msec");
      timeFormatter.setMinimumFractionDigits(1);
      timeFormatter.setMaximumFractionDigits(1);
   }

   /**
    * Constructs a mapping of destination file to its reference count. This will
    * be used to determine the row span in the final table.
    * 
    * @param minifiedFiles
    *           the metrics.
    * @return the mapping of destination to its reference count.
    */
   private Map<String, MutableInt> getDestinationCounts(
         Collection<MinifiedFileMetrics> minifiedFiles) {
      final Map<String, MutableInt> map = new HashMap<String, MutableInt>();
      for (MinifiedFileMetrics metrics : minifiedFiles) {
         MutableInt count = map.get(metrics.getDestination());
         if (count == null) {
            map.put(metrics.getDestination(), new MutableInt(1));
         } else {
            count.increment();
         }
      }
      return map;
   }

   /**
    * Loads the minification summary. If a summary file cannot be found, a
    * default summary is created.
    * 
    * @return the minification summary.
    * @throws MavenReportException
    *            if loading the summary fails.
    */
   protected MinificationSummary loadSummary() throws MavenReportException {
      final MinificationSummary summary;
      try {
         JAXBContext context = JAXBContext
               .newInstance(MinificationSummary.class);
         Unmarshaller unmarshaller = context.createUnmarshaller();
         final File summaryFile = new File(outputDirectory,
               "webapp-minifier-summary.xml");
         if (summaryFile.exists()) {
            summary = (MinificationSummary) unmarshaller.unmarshal(summaryFile);
         } else {
            getLog().warn(
                  "The summary file '" + summaryFile.getName()
                        + "' does not exist.");
            summary = new MinificationSummary();
         }
      } catch (JAXBException e) {
         throw new MavenReportException(
               "Failed to read the minification summary", e);
      }
      return summary;
   }
}
