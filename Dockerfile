
# Stage 1: Build the Struts application
FROM maven:3.9.9-eclipse-temurin-8 AS build

WORKDIR /app

COPY . .

RUN mvn -B -DskipTests clean package

# Stage 2: Run the application
FROM tomcat:9.0-jdk8-temurin

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Deploy the generated WAR as the ROOT application
COPY --from=build /app/target/*.war \
    /usr/local/tomcat/webapps/ROOT.war

# Render uses the PORT environment variable
EXPOSE 10000

# Configure Tomcat to listen on Render's assigned port
CMD ["sh", "-c", "sed -i 's/port=\"8080\"/port=\"'${PORT:-10000}'\"/' $CATALINA_HOME/conf/server.xml && catalina.sh run"]
