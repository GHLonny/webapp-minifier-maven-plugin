package com.github.webapp_minifier.replacer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.IOUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import com.github.webapp_minifier.options.ParseOptionException;

/**
 * This class is a {@link TagReplacer} implementation that uses the <a
 * href="http://jsoup.org/">jsoup</a> library.
 * 
 * @author Lonny
 */
@Component(role = TagReplacer.class, hint = "jsoup")
public class JsoupTagReplacer implements TagReplacer {

   private static final String TEXT_CSS = "text/css";
   private static final String APPLICATION_JS = "application/javascript";
   private static final String TEXT_JS = "text/javascript";

   /** The log instance. */
   private final Log log;

   /** The character set encoding name. */
   private final String charsetName;

   private class Visitor implements NodeVisitor {

      private final NodeHandler handler;

      /** The node currently being processed. */
      private Node currentNode = null;

      /** The collection of nodes to be deleted. */
      private final Collection<Node> toBeDeleted = new ArrayList<Node>();

      public Visitor(final NodeHandler handler) {
         this.handler = handler;
      }

      @Override
      public void head(final Node node, final int depth) {
         if (this.currentNode == null) {
            if ((node instanceof Comment) || (isExternalCss(node))
                  || (isEmbeddedCss(node)) || (isExternalJs(node))
                  || (isEmbeddedJs(node))) {
               this.currentNode = node;
            } else if (node instanceof TextNode) {
               this.handler.handleText(((TextNode) node).text());
            } else {
               this.handler.handleOther(node.nodeName());
            }
         }
      }

      @Override
      public void tail(final Node node, final int depth) {
         if (this.currentNode == node) {
            try {
               if (node instanceof Comment) {
                  handleComment((Comment) node);
               } else if (isExternalCss(node)) {
                  handleExternalCss(node);
               } else if (isEmbeddedCss(node)) {
                  handleEmbeddedCss(node);
               } else if (isExternalJs(node)) {
                  handleExternalJs(node);
               } else if (isEmbeddedJs(node)) {
                  handleEmbeddedJs(node);
               }
            } catch (final IOException e) {
               throw new ReplacerException(e);
            } catch (final ParseOptionException e) {
               throw new ReplacerException(e);
            }
            this.currentNode = null;
         }
      }

      /**
       * Handles a comment node.
       * 
       * @param node
       *           the node to handle.
       * @throws ParseOptionException
       *            if an error occurs while parsing options from the comment.
       */
      private void handleComment(final Comment node)
            throws ParseOptionException {
         final boolean delete = this.handler.handleComment((node).getData());
         if (delete) {
            this.toBeDeleted.add(node);
         }
      }

      /**
       * Handles an external CSS node.
       * 
       * @param node
       *           the node to handle.
       * @throws IOException
       *            if an unexpected I/O error occurs.
       */
      private void handleExternalCss(final Node node) throws IOException {
         final String replacementHref = this.handler.handleExternalCss(node
               .attr("href"));
         if (replacementHref == null) {
            this.toBeDeleted.add(node);
         } else {
            node.attr("href", replacementHref);
         }
      }

      /**
       * Handles an embedded CSS node.
       * 
       * @param node
       *           the node to handle.
       * @throws IOException
       *            if an unexpected I/O error occurs.
       */
      private void handleEmbeddedCss(final Node node) throws IOException {
         final boolean scoped = isScoped(node);
         int removed = 0;
         for (final Node childNode : node.childNodes()) {
            if (childNode instanceof DataNode) {
               final DataNode dataNode = (DataNode) childNode;
               final String data = dataNode.getWholeData();
               final String replacementCss = this.handler.handleEmbeddedCss(
                     data, scoped);
               if (scoped && (replacementCss == null)) {
                  JsoupTagReplacer.this.log
                        .warn("Scoped style cannot be removed.  Preserving the embedded CSS:\n"
                              + data);
               } else if (replacementCss == null) {
                  this.toBeDeleted.add(childNode);
                  removed++;
               } else {
                  dataNode.setWholeData(replacementCss);
               }
            }
         }

         // If the node no longer contains any children, remove it as
         // well.
         if (node.childNodeSize() == removed) {
            this.toBeDeleted.add(node);
         }
      }

      /**
       * Handles an external JavaScript node.
       * 
       * @param node
       *           the node to handle.
       * @throws IOException
       *            if an unexpected I/O error occurs.
       */
      private void handleExternalJs(final Node node) throws IOException {
         final String replacementSrc = this.handler.handleExternalJs(node
               .attr("src"));
         if (replacementSrc == null) {
            this.toBeDeleted.add(node);
         } else {
            node.attr("src", replacementSrc);
         }
      }

      /**
       * Handles an embedded JavaScript node.
       * 
       * @param node
       *           the node to handle.
       * @throws IOException
       *            if an unexpected I/O error occurs.
       */
      private void handleEmbeddedJs(final Node node) throws IOException {
         int removed = 0;
         for (final Node childNode : node.childNodes()) {
            if (childNode instanceof DataNode) {
               final DataNode dataNode = (DataNode) childNode;
               final String replacementJs = this.handler
                     .handleEmbeddedJs(dataNode.getWholeData());
               if (replacementJs == null) {
                  this.toBeDeleted.add(childNode);
                  removed++;
               } else {
                  dataNode.setWholeData(replacementJs);
               }
            }
         }

         // If the node no longer contains any children, remove it as
         // well.
         if (node.childNodeSize() == removed) {
            this.toBeDeleted.add(node);
         }
      }

      /**
       * Determines if the given node is an external CSS tag.
       * 
       * @param node
       *           the node to test.
       * @return <code>true</code> if the node is an external CSS tag.
       */
      private boolean isExternalCss(final Node node) {
         return "link".equalsIgnoreCase(node.nodeName())
               && "stylesheet".equalsIgnoreCase(node.attr("rel"))
               && TEXT_CSS.equalsIgnoreCase(node.attr("type"))
               && node.hasAttr("href");
      }

      /**
       * Determines if the given node is an embedded CSS tag.
       * 
       * @param node
       *           the node to test.
       * @return <code>true</code> if the node is an embedded CSS tag.
       */
      private boolean isEmbeddedCss(final Node node) {
         return "style".equalsIgnoreCase(node.nodeName())
               && TEXT_CSS.equalsIgnoreCase(node.attr("type"));
      }

      /**
       * Determines if the given node is an external JavaScript tag.
       * 
       * @param node
       *           the node to test.
       * @return <code>true</code> if the node is an external JavaScript tag.
       */
      private boolean isExternalJs(final Node node) {
         return isJsScript(node) && node.hasAttr("src");
      }

      /**
       * Determines if the given node is an embedded JavaScript tag.
       * 
       * @param node
       *           the node to test.
       * @return <code>true</code> if the node is an embedded JavaScript tag.
       */
      private boolean isEmbeddedJs(final Node node) {
         return isJsScript(node) && !node.hasAttr("src");
      }

      /**
       * @param node
       * @return
       */
      private boolean isJsScript(final Node node) {
         final boolean isJsScript = "script".equalsIgnoreCase(node.nodeName())
               && (!node.hasAttr("type")
                     || TEXT_JS.equalsIgnoreCase(node.attr("type")) || APPLICATION_JS
                        .equalsIgnoreCase(node.attr("type")));
         return isJsScript;
      }

      /**
       * Determines if the given node is "scoped". In HTML 5,
       * <code>&lt;style></code> tags can be scoped which means that they only
       * apply to the element in which they exist and that element's children.
       * 
       * @param node
       *           the node to test.
       * @return <code>true</code> if the node is "scoped".
       */
      private boolean isScoped(final Node node) {
         return node.hasAttr("scoped");
      }

      /** Cleans up any nodes marked for deletion. */
      public void cleanUp() {
         this.handler.complete();
         for (final Node node : this.toBeDeleted) {
            node.remove();
         }
      }
   }

   public JsoupTagReplacer(final Log log, final String charsetName) {
      this.log = log;
      this.charsetName = charsetName;
   }

   /**
    * @throws IOException
    * @see com.github.jacobsonl.webapp_minifier.replacer.TagReplacer#process(InputStream,
    *      com.github.jacobsonl.webapp_minifier.replacer.NodeHandler, String,
    *      OutputStream)
    */
   @Override
   public void process(final InputStream inputStream,
         final NodeHandler handler, final String baseUri,
         final OutputStream outputStream) throws IOException {
      if (inputStream == null) {
         throw new IllegalArgumentException("The inputStream cannot be null");
      }
      if (handler == null) {
         throw new IllegalArgumentException("The node handler cannot be null");
      }
      if (baseUri == null) {
         throw new IllegalArgumentException("The base URI cannot be null");
      }

      final Visitor visitor = new Visitor(handler);
      final NodeTraversor traversor = new NodeTraversor(visitor);
      final Document document = Jsoup.parse(inputStream, this.charsetName,
            baseUri);

      traversor.traverse(document);
      visitor.cleanUp();

      document.outputSettings().prettyPrint(false);
      final String html = document.outerHtml();
      IOUtil.copy(html, outputStream);
   }
}
