webapp-minifier-maven-plugin
============================

The Webapp Minifier plugin minifies CSS and JavaScript content for a web application. The intent behind the plugin is to simplify the task of minifying the application's CSS and JavaScript without the need for any additional configuration. The plugin scans the HTML and either replaces the content with the minified version or appends it to previously minified content. If minification is not desired, such as for debugging purposes, it can be disabled in the plugin's configuration.

# Quick Start
Here is a basic configuration to get you started:

```XML
<project>
   ...
   <packaging>war</packaging>
   ...
   <build>
      <plugins>
         <plugin>
            <groupId>com.lonnyjacobson</groupId>
            <artifactId>webapp-minifier-maven-plugin</artifactId>
            <version>0.9.0</version>
            <executions>
               <execution>
                  <id>Minify the web application</id>
                  <goals>
                     <goal>minify-webapp</goal>
                  </goals>
               </execution>
            </executions>
         </plugin>
         <plugin>
            <artifactId>maven-war-plugin</artifactId>
            <configuration>
               <warSourceDirectory>${project.build.directory}/${project.build.finalName}-minified</warSourceDirectory>
            </configuration>
         </plugin>
      </plugins>
   </build>
</project>
```
 
 By default, the plugin creates the minified web application in the 
 `${project.build.directory}/${project.build.finalName}-minified` directory. You must
 point the war plugin (or whatever you're using to generate your war) to that
 directory.