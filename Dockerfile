FROM tomcat:9-jdk11-openjdk

# Remove default apps
RUN rm -rf /usr/local/tomcat/webapps/*

# Create directory structure
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF/classes
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF/lib

# Copy only the needed libraries
# javaee-api-6.0.jar is intentionally EXCLUDED - Tomcat 9 provides its own
# servlet/JSP APIs. Including javaee-api causes ClassNotFoundException on JSP compilation.
# javax.mail is included separately as a standalone jar (not provided by Tomcat 9).
COPY jstl-1.2.jar /usr/local/tomcat/webapps/ROOT/WEB-INF/lib/
COPY mysql-connector-j-8.0.33.jar /usr/local/tomcat/webapps/ROOT/WEB-INF/lib/
COPY standard.jar /usr/local/tomcat/webapps/ROOT/WEB-INF/lib/
COPY javax.mail-1.6.2.jar /usr/local/tomcat/webapps/ROOT/WEB-INF/lib/

# Copy web content (JSPs, CSS, JS, WEB-INF/web.xml)
COPY web/ /usr/local/tomcat/webapps/ROOT/

# Copy source files for compilation
COPY src/java/ /tmp/src/

# Build a list of .java files and compile them
RUN find /tmp/src/ -name "*.java" > /tmp/sources.txt && \
    javac -d /usr/local/tomcat/webapps/ROOT/WEB-INF/classes \
    -cp "/usr/local/tomcat/lib/servlet-api.jar:/usr/local/tomcat/lib/jsp-api.jar:/usr/local/tomcat/webapps/ROOT/WEB-INF/lib/*" \
    @/tmp/sources.txt

EXPOSE 8080
CMD ["catalina.sh", "run"]
