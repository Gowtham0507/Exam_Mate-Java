FROM tomcat:9-jdk11-openjdk

# Remove default apps
RUN rm -rf /usr/local/tomcat/webapps/*

# Create directory structure
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF/classes
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF/lib

# Copy libraries from project root to WEB-INF/lib
# Note: Adjusting for the specific jars found in listing
COPY jstl-1.2.jar /usr/local/tomcat/webapps/ROOT/WEB-INF/lib/
COPY mysql-connector-java-5.1.26-bin.jar /usr/local/tomcat/webapps/ROOT/WEB-INF/lib/
COPY standard.jar /usr/local/tomcat/webapps/ROOT/WEB-INF/lib/
COPY javaee-api-6.0.jar /usr/local/tomcat/webapps/ROOT/WEB-INF/lib/

# Download JSF implementation JARs (required for com.sun.faces.config.ConfigureListener)
RUN curl -fL -o /usr/local/tomcat/webapps/ROOT/WEB-INF/lib/jsf-api.jar \
    https://repo1.maven.org/maven2/com/sun/faces/jsf-api/2.2.20/jsf-api-2.2.20.jar && \
    curl -fL -o /usr/local/tomcat/webapps/ROOT/WEB-INF/lib/jsf-impl.jar \
    https://repo1.maven.org/maven2/com/sun/faces/jsf-impl/2.2.20/jsf-impl-2.2.20.jar

# Copy web content (JSPs, CSS, JS)
COPY web/ /usr/local/tomcat/webapps/ROOT/

# Copy source files to temporary directory for compilation
COPY src/java/ /tmp/src/

# Compile Java source files
# We include tomcat's servlet/jsp apis and our own libs in the classpath
RUN javac -d /usr/local/tomcat/webapps/ROOT/WEB-INF/classes \
    -cp "/usr/local/tomcat/lib/servlet-api.jar:/usr/local/tomcat/lib/jsp-api.jar:/usr/local/tomcat/webapps/ROOT/WEB-INF/lib/*" \
    $(find /tmp/src/ -name "*.java")

# Expose port
EXPOSE 8080

# Run tomcat
CMD ["catalina.sh", "run"]
