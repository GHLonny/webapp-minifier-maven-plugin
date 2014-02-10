package com.lonnyjacobson.webapp_minifier.options;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;

import com.google.common.base.Preconditions;
import com.lonnyjacobson.webapp_minifier.antlr.PluginInlineConfigurationBaseListener;
import com.lonnyjacobson.webapp_minifier.antlr.PluginInlineConfigurationLexer;
import com.lonnyjacobson.webapp_minifier.antlr.PluginInlineConfigurationListener;
import com.lonnyjacobson.webapp_minifier.antlr.PluginInlineConfigurationParser;
import com.lonnyjacobson.webapp_minifier.antlr.PluginInlineConfigurationParser.DirectiveContext;
import com.lonnyjacobson.webapp_minifier.antlr.PluginInlineConfigurationParser.KeyValueContext;
import com.lonnyjacobson.webapp_minifier.antlr.PluginInlineConfigurationParser.SeparatorAndValueContext;

/**
 * This class parses plugin options from input.
 * 
 * @author Lonny
 */
public class OptionsParser {
   /** The string indicating options to be parsed. */
   public static final String OPTION_HEADER = "webapp-minifier-maven-plugin:";

   /** The log instance. */
   private final Log log;

   /**
    * Constructs a new <code>OptionsParser</code> using the given log instance.
    * 
    * @param log
    *           the log instance.
    */
   public OptionsParser(final Log log) {
      this.log = log;
   }

   /**
    * Determines if the provided text indicates that it contains options.
    * 
    * @param text
    *           the text to check.
    * @return <code>true</code> if the text <i>probably</i> contains options.
    */
   public boolean containsOptionsHeader(final String text) {
      return StringUtils.contains(text, OPTION_HEADER);
   }

   /**
    * Parses options from a <code>String</code> and creates a new options object from them.
    * 
    * @param text
    *           the text to parse.
    * @param inlineConfigurationHandler
    *           the handler for parsed options.
    * @param directiveHandler
    *           the handler for parsed directives.
    * @throws ParseOptionException
    *            if unable to parse the options or if an unsupported option is received.
    */
   public void parse(final String text,
         final InlineConfigurationHandler inlineConfigurationHandler,
         final DirectiveHandler directiveHandler) throws ParseOptionException {
      Preconditions.checkNotNull(text, "The text cannot be null");
      Preconditions.checkNotNull(inlineConfigurationHandler,
            "The inline configuration handler cannot be null");
      Preconditions.checkNotNull(directiveHandler, "The directive handler cannot be null");

      int index = text.indexOf(OPTION_HEADER);
      if (index >= 0) {
         index += OPTION_HEADER.length();
         try {
            final ANTLRInputStream input = new ANTLRInputStream(text.substring(index));
            final PluginInlineConfigurationLexer lexer = new PluginInlineConfigurationLexer(input);
            final TokenStream tokenInput = new CommonTokenStream(lexer);
            final PluginInlineConfigurationParser parser = new PluginInlineConfigurationParser(
                  tokenInput);
            final PluginInlineConfigurationListener listener = new PluginInlineConfigurationBaseListener() {
               @Override
               public void exitDirective(final DirectiveContext context) {
                  final TerminalNode child = (TerminalNode) context.getChild(0);
                  final String directive = child.getText();
                  OptionsParser.this.log.debug("Handling " + directive);
                  switch (child.getSymbol().getType()) {
                  case PluginInlineConfigurationLexer.SPLITCSS:
                     directiveHandler.splitCss();
                     break;
                  case PluginInlineConfigurationLexer.SPLITJS:
                     directiveHandler.splitJavaScript();
                     break;
                  default:
                     throw new IllegalStateException("Unrecognized directive: " + directive);
                  }
               }

               @Override
               public void exitKeyValue(final KeyValueContext ctx) {
                  final String key = ctx.getChild(0).getText();
                  final SeparatorAndValueContext separatorAndValueContext = (SeparatorAndValueContext) ctx
                        .getChild(1);
                  final String value = separatorAndValueContext.getChild(1).getText();
                  OptionsParser.this.log.debug("Handling " + key + '=' + value);
                  try {
                     inlineConfigurationHandler.handleOption(key, value);
                  } catch (final ParseOptionException e) {
                     throw new RuntimeException(e);
                  }
               }
            };
            parser.addParseListener(listener);
            parser.parse();
         } catch (final RecognitionException e) {
            throw new ParseOptionException(e);
         }
      }
   }
}
